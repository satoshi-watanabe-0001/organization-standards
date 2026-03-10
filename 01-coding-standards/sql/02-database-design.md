# SQL データベース設計原則

**このドキュメントについて**: SQL コーディング規約 - データベース設計原則

---

## 2. データベース設計原則

### 2.1 正規化・非正規化戦略

#### **第3正規形（3NF）を基本とする**
```sql
-- ✅ Good: 正規化されたテーブル設計
-- ユーザーテーブル（基本情報）
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_name VARCHAR(100) NOT NULL,
    email_address VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'inactive', 'suspended')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ユーザープロフィールテーブル（拡張情報）
CREATE TABLE user_profiles (
    user_id UUID PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    birth_date DATE,
    phone_number VARCHAR(20),
    profile_image_url VARCHAR(500),
    bio TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 住所テーブル（1:N関係）
CREATE TABLE user_addresses (
    address_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    address_type VARCHAR(20) NOT NULL CHECK (address_type IN ('home', 'work', 'billing', 'shipping')),
    street_address VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state_province VARCHAR(100),
    postal_code VARCHAR(20),
    country_code CHAR(2) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- 一意制約：1ユーザーにつき1つのデフォルト住所のみ
    CONSTRAINT unique_default_address_per_user 
        EXCLUDE (user_id WITH =, address_type WITH =) 
        WHERE (is_default = TRUE)
);

-- ❌ Bad: 非正規化されすぎたテーブル
CREATE TABLE users_bad (
    user_id UUID PRIMARY KEY,
    user_name VARCHAR(100),
    email VARCHAR(255),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    home_street VARCHAR(200),
    home_city VARCHAR(100),
    home_postal_code VARCHAR(20),
    work_street VARCHAR(200),  -- 住所情報の重複
    work_city VARCHAR(100),    -- データ整合性の問題
    work_postal_code VARCHAR(20),
    order1_id UUID,           -- 注文情報の重複
    order1_date TIMESTAMP,    -- スケーラビリティの問題
    order1_amount DECIMAL,
    order2_id UUID,
    order2_date TIMESTAMP,
    order2_amount DECIMAL
    -- ... さらに続く
);
```

#### **戦略的非正規化**
```sql
-- ✅ Good: パフォーマンス向上のための計算済みフィールド
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_name VARCHAR(100) NOT NULL,
    email_address VARCHAR(255) UNIQUE NOT NULL,
    
    -- 計算済みフィールド（パフォーマンス向上）
    total_orders INTEGER DEFAULT 0,
    total_spent DECIMAL(12,2) DEFAULT 0.00,
    last_order_date TIMESTAMP WITH TIME ZONE,
    customer_tier VARCHAR(20) DEFAULT 'bronze' CHECK (customer_tier IN ('bronze', 'silver', 'gold', 'platinum')),
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 計算済みフィールド更新用トリガー
CREATE OR REPLACE FUNCTION update_user_statistics()
RETURNS TRIGGER AS $$
BEGIN
    -- 注文統計を更新
    UPDATE users 
    SET 
        total_orders = (
            SELECT COUNT(*) 
            FROM orders 
            WHERE user_id = NEW.user_id 
              AND status NOT IN ('cancelled', 'returned')
        ),
        total_spent = (
            SELECT COALESCE(SUM(total_amount), 0) 
            FROM orders 
            WHERE user_id = NEW.user_id 
              AND status NOT IN ('cancelled', 'returned')
        ),
        last_order_date = (
            SELECT MAX(created_at) 
            FROM orders 
            WHERE user_id = NEW.user_id
        ),
        customer_tier = (
            CASE 
                WHEN COALESCE((
                    SELECT SUM(total_amount) 
                    FROM orders 
                    WHERE user_id = NEW.user_id 
                      AND status NOT IN ('cancelled', 'returned')
                ), 0) >= 100000 THEN 'platinum'
                WHEN COALESCE((
                    SELECT SUM(total_amount) 
                    FROM orders 
                    WHERE user_id = NEW.user_id 
                      AND status NOT IN ('cancelled', 'returned')
                ), 0) >= 50000 THEN 'gold'
                WHEN COALESCE((
                    SELECT SUM(total_amount) 
                    FROM orders 
                    WHERE user_id = NEW.user_id 
                      AND status NOT IN ('cancelled', 'returned')
                ), 0) >= 10000 THEN 'silver'
                ELSE 'bronze'
            END
        ),
        updated_at = CURRENT_TIMESTAMP
    WHERE user_id = NEW.user_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_user_statistics
    AFTER INSERT OR UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION update_user_statistics();
```

### 2.2 データ整合性・制約設計

#### **包括的な制約定義**
```sql
-- ✅ Good: 包括的な制約設計
CREATE TABLE orders (
    order_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT,
    
    -- ステータス制約
    status VARCHAR(20) DEFAULT 'pending' 
        CHECK (status IN ('pending', 'confirmed', 'processing', 'shipped', 'delivered', 'cancelled', 'returned')),
    
    -- 金額制約
    subtotal DECIMAL(12,2) NOT NULL CHECK (subtotal >= 0),
    tax_amount DECIMAL(12,2) NOT NULL CHECK (tax_amount >= 0),
    shipping_fee DECIMAL(12,2) NOT NULL CHECK (shipping_fee >= 0),
    discount_amount DECIMAL(12,2) DEFAULT 0 CHECK (discount_amount >= 0),
    total_amount DECIMAL(12,2) GENERATED ALWAYS AS (subtotal + tax_amount + shipping_fee - discount_amount) STORED,
    
    -- 配送情報
    shipping_address_id UUID REFERENCES user_addresses(address_id),
    billing_address_id UUID REFERENCES user_addresses(address_id),
    
    -- 日時制約
    order_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    shipped_at TIMESTAMP WITH TIME ZONE,
    delivered_at TIMESTAMP WITH TIME ZONE,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- 複合制約
    CONSTRAINT valid_shipping_date CHECK (shipped_at IS NULL OR shipped_at >= order_date),
    CONSTRAINT valid_delivery_date CHECK (delivered_at IS NULL OR delivered_at >= COALESCE(shipped_at, order_date)),
    CONSTRAINT valid_addresses CHECK (shipping_address_id IS NOT NULL OR billing_address_id IS NOT NULL)
);

-- 注文明細テーブル
CREATE TABLE order_items (
    order_item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(product_id) ON DELETE RESTRICT,
    
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    total_price DECIMAL(12,2) GENERATED ALWAYS AS (quantity * unit_price) STORED,
    
    -- 在庫確保時の価格固定
    price_locked_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- 同一注文内での商品重複防止
    UNIQUE(order_id, product_id)
);
```

#### **カスケード削除・更新ポリシー**
```sql
-- ✅ Good: 適切なカスケードポリシー

-- マスターデータ：RESTRICT（削除防止）
ALTER TABLE orders 
    ADD CONSTRAINT fk_orders_user 
    FOREIGN KEY (user_id) REFERENCES users(user_id) 
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- 従属データ：CASCADE（連鎖削除）
ALTER TABLE order_items 
    ADD CONSTRAINT fk_order_items_order 
    FOREIGN KEY (order_id) REFERENCES orders(order_id) 
    ON DELETE CASCADE ON UPDATE CASCADE;

-- 参照データ：SET NULL（NULL設定）
ALTER TABLE orders 
    ADD CONSTRAINT fk_orders_shipping_address 
    FOREIGN KEY (shipping_address_id) REFERENCES user_addresses(address_id) 
    ON DELETE SET NULL ON UPDATE CASCADE;

-- ❌ Bad: 不適切なカスケード設定
-- マスターデータでCASCADE（データ損失リスク）
ALTER TABLE orders 
    ADD CONSTRAINT fk_orders_user_bad 
    FOREIGN KEY (user_id) REFERENCES users(user_id) 
    ON DELETE CASCADE; -- ユーザー削除で注文履歴も消去される危険性
```

**Devin指示**: 第3正規形を基本とし、パフォーマンス向上のための戦略的非正規化のみ適用、包括的制約・適切なカスケードポリシーを必ず実装せよ

### 2.3 主キー・外部キー設計

#### **主キー戦略**
```sql
-- ✅ Good: UUID主キー（推奨）
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email_address VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ✅ Good: 複合主キー（多対多関係）
CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(role_id) ON DELETE CASCADE,
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    assigned_by UUID REFERENCES users(user_id),
    
    PRIMARY KEY (user_id, role_id)
);

-- ✅ 代替案: 連番主キー（高スループット環境）
CREATE TABLE audit_logs (
    log_id BIGSERIAL PRIMARY KEY,
    user_id UUID REFERENCES users(user_id),
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_id VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- 検索用インデックス
    INDEX idx_audit_logs_user_time (user_id, created_at),
    INDEX idx_audit_logs_resource (resource_type, resource_id)
);

-- ❌ Bad: ビジネスキーを主キーに使用
CREATE TABLE users_bad (
    email_address VARCHAR(255) PRIMARY KEY, -- メールアドレス変更で参照テーブル更新が必要
    user_name VARCHAR(100)
);
```

#### **外部キー設計原則**
```sql
-- ✅ Good: 適切な外部キー制約
CREATE TABLE products (
    product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id UUID NOT NULL REFERENCES categories(category_id) ON DELETE RESTRICT,
    supplier_id UUID REFERENCES suppliers(supplier_id) ON DELETE SET NULL,
    
    product_name VARCHAR(200) NOT NULL,
    sku VARCHAR(50) UNIQUE NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    stock_quantity INTEGER DEFAULT 0 CHECK (stock_quantity >= 0),
    
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 外部キー制約の詳細設定
ALTER TABLE products 
    ADD CONSTRAINT fk_products_category 
    FOREIGN KEY (category_id) REFERENCES categories(category_id) 
    ON DELETE RESTRICT     -- カテゴリに商品がある限り削除不可
    ON UPDATE CASCADE      -- カテゴリID変更時は商品も更新
    DEFERRABLE INITIALLY IMMEDIATE; -- トランザクション内での制約チェック

ALTER TABLE products 
    ADD CONSTRAINT fk_products_supplier 
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) 
    ON DELETE SET NULL     -- サプライヤー削除時はNULLに設定
    ON UPDATE CASCADE
    DEFERRABLE INITIALLY IMMEDIATE;
```

### 2.4 インデックス設計戦略

#### **インデックス作成指針**
```sql
-- ✅ Good: クエリパターンに基づくインデックス設計

-- 1. 単一カラムインデックス（高い選択性）
CREATE INDEX idx_users_email ON users(email_address);
CREATE INDEX idx_products_sku ON products(sku);

-- 2. 複合インデックス（複数条件検索）
-- カラム順序重要：等価条件 → 範囲条件 → ソート条件
CREATE INDEX idx_orders_user_date_status ON orders(user_id, order_date, status);
CREATE INDEX idx_products_category_price ON products(category_id, price, is_active);

-- 3. 部分インデックス（条件付きインデックス）
CREATE INDEX idx_orders_pending ON orders(created_at) 
WHERE status = 'pending';

CREATE INDEX idx_products_active ON products(category_id, price) 
WHERE is_active = TRUE;

-- 4. 関数型インデックス
CREATE INDEX idx_users_email_lower ON users(LOWER(email_address));
CREATE INDEX idx_orders_month ON orders(DATE_TRUNC('month', order_date));

-- 5. JSON/配列インデックス（PostgreSQL）
CREATE INDEX idx_user_metadata_gin ON users USING GIN(metadata) 
WHERE metadata IS NOT NULL;

-- 6. 全文検索インデックス
CREATE INDEX idx_products_search ON products USING GIN(
    to_tsvector('english', product_name || ' ' || COALESCE(description, ''))
);
```

#### **インデックス最適化のベストプラクティス**
```sql
-- ✅ Good: クエリパフォーマンス分析

-- EXPLAIN ANALYZEでクエリプラン確認
EXPLAIN (ANALYZE, BUFFERS, FORMAT JSON)
SELECT 
    u.user_id,
    u.user_name,
    COUNT(o.order_id) AS order_count,
    SUM(o.total_amount) AS total_spent
FROM 
    users u
    LEFT JOIN orders o ON u.user_id = o.user_id 
        AND o.status NOT IN ('cancelled', 'returned')
        AND o.order_date >= CURRENT_DATE - INTERVAL '12 months'
WHERE 
    u.status = 'active'
    AND u.created_at >= CURRENT_DATE - INTERVAL '2 years'
GROUP BY 
    u.user_id, u.user_name
HAVING 
    COUNT(o.order_id) > 0
ORDER BY 
    total_spent DESC
LIMIT 100;

-- 上記クエリ用の最適化インデックス
CREATE INDEX idx_users_status_created ON users(status, created_at) 
WHERE status = 'active';

CREATE INDEX idx_orders_user_date_status_amount ON orders(
    user_id, 
    order_date, 
    status, 
    total_amount
) WHERE status NOT IN ('cancelled', 'returned');

-- インデックス使用状況監視（PostgreSQL）
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_tup_read,
    idx_tup_fetch,
    idx_scan,
    ROUND(
        100.0 * idx_tup_fetch / NULLIF(idx_tup_read, 0), 
        2
    ) AS hit_ratio
FROM 
    pg_stat_user_indexes
WHERE 
    idx_scan > 0
ORDER BY 
    idx_scan DESC;
```

### 2.5 データ型選択・パーティショニング

#### **適切なデータ型選択**
```sql
-- ✅ Good: 適切なデータ型選択
CREATE TABLE comprehensive_example (
    -- 識別子
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    legacy_id BIGSERIAL UNIQUE,
    
    -- 文字列
    email VARCHAR(255) NOT NULL,           -- 可変長、最大サイズ明確
    country_code CHAR(2) NOT NULL,         -- 固定長、ISO規格
    phone VARCHAR(20),                     -- 国際電話番号対応
    description TEXT,                      -- 長文、サイズ不明
    
    -- 数値
    price DECIMAL(10,2) NOT NULL,          -- 通貨、精度重要
    weight_kg DECIMAL(8,3),               -- 重量、小数点3桁
    quantity INTEGER NOT NULL DEFAULT 0,   -- 整数、範囲十分
    view_count BIGINT DEFAULT 0,          -- 大きな整数値
    rating SMALLINT CHECK (rating BETWEEN 1 AND 5), -- 小さな整数範囲
    
    -- 日時
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    published_date DATE,                   -- 日付のみ
    open_time TIME,                       -- 時刻のみ
    
    -- ブール値
    is_active BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    
    -- JSON（PostgreSQL）
    metadata JSONB,
    settings JSONB DEFAULT '{}',
    
    -- 配列（PostgreSQL）
    tags TEXT[],
    categories INTEGER[],
    
    -- バイナリ
    file_hash BYTEA,
    
    -- 制約
    CONSTRAINT valid_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT valid_country_code CHECK (country_code ~* '^[A-Z]{2}$')
);

-- ❌ Bad: 不適切なデータ型選択
CREATE TABLE bad_example (
    price VARCHAR(20),          -- 数値を文字列で保存（計算不可）
    created_at VARCHAR(50),     -- 日時を文字列で保存（ソート・比較困難）
    is_active VARCHAR(10),      -- ブール値を文字列で保存
    metadata TEXT               -- JSON データを TEXT で保存（検索・更新困難）
);
```

#### **テーブルパーティショニング戦略**
```sql
-- ✅ Good: 日付ベースパーティショニング（PostgreSQL）

-- 親テーブル作成
CREATE TABLE orders_partitioned (
    order_id UUID NOT NULL,
    user_id UUID NOT NULL,
    order_date TIMESTAMP WITH TIME ZONE NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
) PARTITION BY RANGE (order_date);

-- 月次パーティション作成
CREATE TABLE orders_2024_01 PARTITION OF orders_partitioned
FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE orders_2024_02 PARTITION OF orders_partitioned
FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');

CREATE TABLE orders_2024_03 PARTITION OF orders_partitioned
FOR VALUES FROM ('2024-03-01') TO ('2024-04-01');

-- 自動パーティション作成用関数
CREATE OR REPLACE FUNCTION create_monthly_partition(
    table_name TEXT,
    start_date DATE
) RETURNS VOID AS $$
DECLARE
    partition_name TEXT;
    end_date DATE;
BEGIN
    partition_name := table_name || '_' || to_char(start_date, 'YYYY_MM');
    end_date := start_date + INTERVAL '1 month';
    
    EXECUTE format(
        'CREATE TABLE IF NOT EXISTS %I PARTITION OF %I FOR VALUES FROM (%L) TO (%L)',
        partition_name,
        table_name,
        start_date,
        end_date
    );
    
    -- パーティション用インデックス作成
    EXECUTE format(
        'CREATE INDEX IF NOT EXISTS idx_%s_user_date ON %I (user_id, order_date)',
        partition_name,
        partition_name
    );
END;
$$ LANGUAGE plpgsql;

-- パーティション自動作成（月次スケジュール実行）
SELECT create_monthly_partition('orders_partitioned', date_trunc('month', CURRENT_DATE + INTERVAL '1 month'));

-- ハッシュパーティション（大量データの分散）
CREATE TABLE user_activities_partitioned (
    activity_id UUID NOT NULL,
    user_id UUID NOT NULL,
    activity_type VARCHAR(50) NOT NULL,
    data JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
) PARTITION BY HASH (user_id);

-- ハッシュパーティション作成（4分割）
CREATE TABLE user_activities_0 PARTITION OF user_activities_partitioned
FOR VALUES WITH (modulus 4, remainder 0);

CREATE TABLE user_activities_1 PARTITION OF user_activities_partitioned
FOR VALUES WITH (modulus 4, remainder 1);

CREATE TABLE user_activities_2 PARTITION OF user_activities_partitioned
FOR VALUES WITH (modulus 4, remainder 2);

CREATE TABLE user_activities_3 PARTITION OF user_activities_partitioned
FOR VALUES WITH (modulus 4, remainder 3);
```

### 2.6 Devin実行ガイドライン

#### **データベース設計チェックリスト**

**設計段階**:
- [ ] ビジネス要件とデータモデルの整合性確認
- [ ] エンティティ関係図（ERD）の作成・レビュー
- [ ] 正規化レベルの決定（3NF基本、パフォーマンス要件に応じた非正規化）
- [ ] データ量・成長予測の実施（3年後の想定）
- [ ] アクセスパターン・クエリパターンの分析

**実装段階**:
- [ ] 適切な主キー戦略の選択（UUID推奨、連番は高スループット用途のみ）
- [ ] 外部キー制約の設定（適切なON DELETE/UPDATE動作）
- [ ] データ型の最適選択（精度・パフォーマンス・ストレージ効率）
- [ ] CHECK制約による値の妥当性保証
- [ ] インデックス設計（クエリパターンベース）
- [ ] パーティショニング戦略（大量データテーブル）

**検証段階**:
- [ ] EXPLAIN ANALYZEによるクエリプラン確認
- [ ] 制約違反テストの実施
- [ ] パフォーマンステスト（想定データ量での検証）
- [ ] インデックス使用状況の監視設定

#### **実装フロー例**

```sql
-- Step 1: 要件分析をSQLコメントで記録
/*
 * ビジネス要件:
 * - ECサイトの注文管理システム
 * - 1日平均1000注文、ピーク時3000注文
 * - 注文履歴の長期保存（3年以上）
 * - リアルタイム在庫管理
 * - 顧客ランク別のサービス提供
 * 
 * パフォーマンス要件:
 * - 注文作成: 100ms以内
 * - 注文履歴検索: 200ms以内
 * - 在庫更新: 50ms以内
 */

-- Step 2: マスターテーブルから作成
CREATE TABLE users (...);  -- ユーザーマスター
CREATE TABLE categories (...);  -- カテゴリマスター
CREATE TABLE products (...);  -- 商品マスター

-- Step 3: トランザクションテーブル作成
CREATE TABLE orders (...);  -- 注文ヘッダー
CREATE TABLE order_items (...);  -- 注文明細

-- Step 4: インデックス最適化
CREATE INDEX idx_orders_user_date ON orders(user_id, order_date);
CREATE INDEX idx_products_category_active ON products(category_id, is_active);

-- Step 5: 制約・トリガー設定
ALTER TABLE products ADD CONSTRAINT chk_positive_price CHECK (price > 0);
CREATE TRIGGER update_user_stats AFTER INSERT ON orders ...

-- Step 6: パフォーマンス検証
EXPLAIN ANALYZE SELECT ... -- 主要クエリの実行プラン確認
```

**Devin指示**: 設計段階でビジネス要件とパフォーマンス要件を明確化し、段階的実装フローに従って、適切なデータ型・制約・インデックスを設定せよ

---

