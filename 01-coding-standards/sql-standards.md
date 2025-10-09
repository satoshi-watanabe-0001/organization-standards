# SQL コーディング規約
## SQL Coding Standards

**最終更新日**: 2024-10-09  
**バージョン**: 1.0.0  
**対象**: PostgreSQL 14+, MySQL 8.0+  
**適用範囲**: データベース設計・クエリ実装・パフォーマンス最適化

## 目的

このドキュメントは、SQLを使用したデータベース設計・実装における具体的なコーディング規約を定義し、パフォーマンス・保守性・セキュリティを兼ね備えた高品質なデータベースソリューションを実現します。共通原則については`00-general-principles.md`を参照してください。

---

## 1. 基本構文・スタイル規約

### 1.1 基本スタイル

#### **大文字・小文字ルール**
```sql
-- ✅ Good: SQLキーワードは大文字、識別子は小文字
SELECT 
    u.user_id,
    u.user_name,
    u.email_address,
    u.created_at
FROM 
    users u
WHERE 
    u.status = 'active'
    AND u.created_at >= '2024-01-01'
ORDER BY 
    u.created_at DESC;

-- ❌ Bad: 一貫性のない大文字小文字
select u.User_ID, u.UserName from Users u where u.STATUS = 'Active';
```

#### **インデント・フォーマット**
```sql
-- ✅ Good: 構造化されたフォーマット
SELECT 
    u.user_id,
    u.user_name,
    u.email_address,
    p.profile_image_url,
    COUNT(o.order_id) AS total_orders,
    SUM(o.total_amount) AS total_spent
FROM 
    users u
    INNER JOIN user_profiles p ON u.user_id = p.user_id
    LEFT JOIN orders o ON u.user_id = o.user_id
WHERE 
    u.status = 'active'
    AND u.created_at >= CURRENT_DATE - INTERVAL '1 year'
    AND (
        u.user_type = 'premium'
        OR u.total_orders > 10
    )
GROUP BY 
    u.user_id,
    u.user_name,
    u.email_address,
    p.profile_image_url
HAVING 
    COUNT(o.order_id) > 0
ORDER BY 
    total_spent DESC,
    u.created_at DESC
LIMIT 100;

-- ❌ Bad: 読みにくいフォーマット
select u.user_id,u.user_name,count(o.order_id) from users u left join orders o on u.user_id=o.user_id where u.status='active' group by u.user_id,u.user_name order by count(o.order_id) desc;
```

#### **コメント規約**
```sql
-- ✅ Good: 目的・ビジネスロジックを説明
/*
 * 月次売上レポート生成クエリ
 * - アクティブユーザーの過去12ヶ月の購入履歴を集計
 * - 返品・キャンセル分を除外
 * - VIP顧客（累計10万円以上購入）を別途識別
 */
SELECT 
    DATE_TRUNC('month', o.created_at) AS sales_month,
    COUNT(DISTINCT o.user_id) AS unique_customers,
    COUNT(o.order_id) AS total_orders,
    SUM(o.total_amount) AS gross_revenue,
    -- 返品・キャンセル分を減算して純売上を算出
    SUM(
        CASE 
            WHEN o.status IN ('returned', 'cancelled') THEN 0
            ELSE o.total_amount
        END
    ) AS net_revenue,
    -- VIP顧客数（累計購入額100,000円以上）
    COUNT(
        DISTINCT CASE 
            WHEN u.total_spent >= 100000 THEN o.user_id
        END
    ) AS vip_customers
FROM 
    orders o
    INNER JOIN users u ON o.user_id = u.user_id
WHERE 
    o.created_at >= CURRENT_DATE - INTERVAL '12 months'
    AND u.status = 'active'
GROUP BY 
    DATE_TRUNC('month', o.created_at)
ORDER BY 
    sales_month DESC;

-- ❌ Bad: 不十分なコメント
-- 売上データ取得
SELECT * FROM orders WHERE created_at > '2024-01-01';
```

**Devin指示**: SQLキーワード大文字、識別子小文字、適切なインデント、ビジネスロジックの詳細コメントを必ず適用せよ

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

## 3. クエリ最適化・パフォーマンス

### 3.1 SELECT文の最適化技法

#### **必要なカラムのみ選択**
```sql
-- ✅ Good: 必要なカラムのみ指定
SELECT 
    u.user_id,
    u.user_name,
    u.email_address,
    u.created_at
FROM 
    users u
WHERE 
    u.status = 'active'
    AND u.created_at >= CURRENT_DATE - INTERVAL '30 days'
ORDER BY 
    u.created_at DESC
LIMIT 100;

-- ❌ Bad: 不必要なカラムを取得（ネットワーク負荷・メモリ使用量増加）
SELECT * FROM users WHERE status = 'active';  -- 全カラム取得

SELECT 
    u.*,
    p.*,  -- 不必要なプロフィール情報も取得
    COALESCE(p.bio, ''),  -- 使用しない計算
FROM users u
LEFT JOIN user_profiles p ON u.user_id = p.user_id;
```

#### **WHERE句の最適化**
```sql
-- ✅ Good: インデックスを活用した条件指定
SELECT 
    o.order_id,
    o.total_amount,
    o.order_date
FROM 
    orders o
WHERE 
    o.user_id = $1                                    -- 等価条件（インデックス活用）
    AND o.order_date >= $2                           -- 範囲条件
    AND o.order_date < $3
    AND o.status IN ('confirmed', 'shipped', 'delivered')  -- IN句で複数値
ORDER BY 
    o.order_date DESC
LIMIT 50;

-- 対応インデックス
CREATE INDEX idx_orders_user_date_status ON orders(user_id, order_date, status);

-- ❌ Bad: インデックスを活用できない条件
SELECT * FROM orders 
WHERE 
    EXTRACT(YEAR FROM order_date) = 2024      -- 関数適用でインデックス不使用
    AND UPPER(status) = 'CONFIRMED'           -- 関数適用でインデックス不使用
    AND total_amount + shipping_fee > 1000;   -- 計算式でインデックス不使用

-- ✅ 改善版: インデックスを活用できる条件に変更
SELECT * FROM orders 
WHERE 
    order_date >= '2024-01-01'                -- 範囲検索でインデックス活用
    AND order_date < '2025-01-01'
    AND status = 'confirmed'                  -- 等価検索でインデックス活用
    AND total_amount > 800;                   -- shipping_feeを考慮した闾値設定
```

#### **LIMITとOFFSETの適切な使用**
```sql
-- ✅ Good: カーソルベースページネーション（高パフォーマンス）
SELECT 
    u.user_id,
    u.user_name,
    u.email_address,
    u.created_at
FROM 
    users u
WHERE 
    u.status = 'active'
    AND u.user_id > $1  -- 前ページの最後のuser_id
ORDER BY 
    u.user_id ASC
LIMIT 20;

-- ❌ Bad: OFFSETベースページネーション（大きなOFFSETでパフォーマンス劣化）
SELECT 
    u.user_id,
    u.user_name,
    u.email_address,
    u.created_at
FROM 
    users u
WHERE 
    u.status = 'active'
ORDER BY 
    u.user_id ASC
LIMIT 20 OFFSET 100000;  -- 100,000件スキップするため、全てをスキャンする必要がある

-- ✅ 時間ベースページネーション（時系列データ用）
SELECT 
    o.order_id,
    o.user_id,
    o.total_amount,
    o.order_date
FROM 
    orders o
WHERE 
    o.order_date < $1  -- 前ページの最古の日時
    AND o.status = 'completed'
ORDER BY 
    o.order_date DESC
LIMIT 20;
```

### 3.2 JOIN最適化戦略

#### **JOINの型と適切な選択**
```sql
-- ✅ Good: 適切なJOIN型の選択
SELECT 
    u.user_id,
    u.user_name,
    p.first_name,
    p.last_name,
    COUNT(o.order_id) AS order_count,
    SUM(o.total_amount) AS total_spent
FROM 
    users u
    INNER JOIN user_profiles p ON u.user_id = p.user_id  -- 必須関係
    LEFT JOIN orders o ON u.user_id = o.user_id           -- オプション関係
        AND o.status NOT IN ('cancelled', 'returned')
        AND o.order_date >= CURRENT_DATE - INTERVAL '12 months'
WHERE 
    u.status = 'active'
    AND u.created_at >= CURRENT_DATE - INTERVAL '2 years'
GROUP BY 
    u.user_id, u.user_name, p.first_name, p.last_name
ORDER BY 
    total_spent DESC NULLS LAST
LIMIT 100;

-- 必要なインデックス
CREATE INDEX idx_users_status_created ON users(status, created_at);
CREATE INDEX idx_orders_user_status_date ON orders(user_id, status, order_date);
```

#### **JOIN条件の最適化**
```sql
-- ✅ Good: JOIN条件でフィルタリング
SELECT 
    o.order_id,
    o.total_amount,
    u.user_name,
    p.product_name
FROM 
    orders o
    INNER JOIN users u ON o.user_id = u.user_id 
        AND u.status = 'active'                    -- JOIN条件でフィルタリング
    INNER JOIN order_items oi ON o.order_id = oi.order_id
    INNER JOIN products p ON oi.product_id = p.product_id 
        AND p.is_active = TRUE                     -- JOIN条件でフィルタリング
WHERE 
    o.order_date >= CURRENT_DATE - INTERVAL '7 days'
    AND o.status = 'completed';

-- ❌ Bad: WHERE句でのみフィルタリング（中間結果が大きくなる）
SELECT 
    o.order_id,
    o.total_amount,
    u.user_name,
    p.product_name
FROM 
    orders o
    INNER JOIN users u ON o.user_id = u.user_id
    INNER JOIN order_items oi ON o.order_id = oi.order_id
    INNER JOIN products p ON oi.product_id = p.product_id
WHERE 
    o.order_date >= CURRENT_DATE - INTERVAL '7 days'
    AND o.status = 'completed'
    AND u.status = 'active'      -- JOIN後にフィルタリング
    AND p.is_active = TRUE;      -- JOIN後にフィルタリング
```

#### **複数テーブルJOINの最適化**
```sql
-- ✅ Good: 効率的なJOIN順序（小さいテーブルから始める）
SELECT 
    c.category_name,
    p.product_name,
    p.price,
    SUM(oi.quantity) AS total_sold,
    SUM(oi.total_price) AS total_revenue
FROM 
    categories c                    -- 小さいマスターテーブル
    INNER JOIN products p ON c.category_id = p.category_id
        AND p.is_active = TRUE
    INNER JOIN order_items oi ON p.product_id = oi.product_id
    INNER JOIN orders o ON oi.order_id = o.order_id
        AND o.status = 'completed'
        AND o.order_date >= CURRENT_DATE - INTERVAL '30 days'
WHERE 
    c.is_active = TRUE
GROUP BY 
    c.category_id, c.category_name, p.product_id, p.product_name, p.price
HAVING 
    SUM(oi.quantity) >= 10  -- 十分な売上のある商品のみ
ORDER BY 
    total_revenue DESC;

-- 必要なインデックス
CREATE INDEX idx_products_category_active ON products(category_id, is_active);
CREATE INDEX idx_order_items_product ON order_items(product_id, quantity, total_price);
CREATE INDEX idx_orders_status_date ON orders(status, order_date);
```

### 3.3 サブクエリ vs CTE vs ウィンドウ関数

#### **相関サブクエリの最適化**
```sql
-- ✅ Good: EXISTSでの存在チェック（パフォーマンスが良い）
SELECT 
    p.product_id,
    p.product_name,
    p.price
FROM 
    products p
WHERE 
    p.is_active = TRUE
    AND EXISTS (
        SELECT 1 
        FROM order_items oi 
        INNER JOIN orders o ON oi.order_id = o.order_id
        WHERE oi.product_id = p.product_id
          AND o.status = 'completed'
          AND o.order_date >= CURRENT_DATE - INTERVAL '30 days'
    )
ORDER BY p.price DESC;

-- ❌ Bad: INサブクエリ（大量データでパフォーマンス劣化）
SELECT 
    p.product_id,
    p.product_name,
    p.price
FROM 
    products p
WHERE 
    p.is_active = TRUE
    AND p.product_id IN (
        SELECT oi.product_id 
        FROM order_items oi 
        INNER JOIN orders o ON oi.order_id = o.order_id
        WHERE o.status = 'completed'
          AND o.order_date >= CURRENT_DATE - INTERVAL '30 days'
    );
```

#### **CTE（Common Table Expression）の活用**
```sql
-- ✅ Good: 複雑なクエリのCTEでの簡素化
WITH monthly_sales AS (
    SELECT 
        DATE_TRUNC('month', o.order_date) AS sales_month,
        o.user_id,
        SUM(o.total_amount) AS monthly_total,
        COUNT(o.order_id) AS monthly_orders
    FROM 
        orders o
    WHERE 
        o.status = 'completed'
        AND o.order_date >= CURRENT_DATE - INTERVAL '12 months'
    GROUP BY 
        DATE_TRUNC('month', o.order_date), o.user_id
),
user_summary AS (
    SELECT 
        ms.user_id,
        COUNT(ms.sales_month) AS active_months,
        SUM(ms.monthly_total) AS total_spent,
        AVG(ms.monthly_total) AS avg_monthly_spend,
        MAX(ms.monthly_total) AS max_monthly_spend
    FROM 
        monthly_sales ms
    GROUP BY 
        ms.user_id
    HAVING 
        COUNT(ms.sales_month) >= 6  -- 6ヶ月以上アクティブ
)
SELECT 
    u.user_id,
    u.user_name,
    u.email_address,
    us.active_months,
    us.total_spent,
    us.avg_monthly_spend,
    us.max_monthly_spend,
    CASE 
        WHEN us.total_spent >= 100000 THEN 'platinum'
        WHEN us.total_spent >= 50000 THEN 'gold'
        WHEN us.total_spent >= 10000 THEN 'silver'
        ELSE 'bronze'
    END AS customer_tier
FROM 
    users u
    INNER JOIN user_summary us ON u.user_id = us.user_id
WHERE 
    u.status = 'active'
ORDER BY 
    us.total_spent DESC;

-- ✅ 再帰CTE（階層データの処理）
WITH RECURSIVE category_hierarchy AS (
    -- ルートカテゴリ
    SELECT 
        category_id,
        category_name,
        parent_category_id,
        0 AS level,
        category_id::TEXT AS path
    FROM 
        categories 
    WHERE 
        parent_category_id IS NULL
    
    UNION ALL
    
    -- 子カテゴリ
    SELECT 
        c.category_id,
        c.category_name,
        c.parent_category_id,
        ch.level + 1,
        ch.path || ' > ' || c.category_id::TEXT
    FROM 
        categories c
        INNER JOIN category_hierarchy ch ON c.parent_category_id = ch.category_id
    WHERE 
        ch.level < 5  -- 無限ループ防止
)
SELECT 
    ch.category_id,
    REPEAT('  ', ch.level) || ch.category_name AS indented_name,
    ch.level,
    ch.path,
    COUNT(p.product_id) AS product_count
FROM 
    category_hierarchy ch
    LEFT JOIN products p ON ch.category_id = p.category_id 
        AND p.is_active = TRUE
GROUP BY 
    ch.category_id, ch.category_name, ch.level, ch.path
ORDER BY 
    ch.path;
```

#### **ウィンドウ関数の活用**
```sql
-- ✅ Good: ウィンドウ関数での分析クエリ
SELECT 
    o.order_id,
    o.user_id,
    o.order_date,
    o.total_amount,
    
    -- 連続した注文番号
    ROW_NUMBER() OVER (
        PARTITION BY o.user_id 
        ORDER BY o.order_date ASC
    ) AS order_sequence,
    
    -- 順位付け（金額ベース）
    RANK() OVER (
        PARTITION BY o.user_id 
        ORDER BY o.total_amount DESC
    ) AS amount_rank,
    
    -- 累積金額
    SUM(o.total_amount) OVER (
        PARTITION BY o.user_id 
        ORDER BY o.order_date ASC 
        ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
    ) AS cumulative_spent,
    
    -- 移動平均（過去3注文）
    AVG(o.total_amount) OVER (
        PARTITION BY o.user_id 
        ORDER BY o.order_date ASC 
        ROWS BETWEEN 2 PRECEDING AND CURRENT ROW
    ) AS moving_avg_3orders,
    
    -- 前回注文との比較
    o.total_amount - LAG(o.total_amount, 1) OVER (
        PARTITION BY o.user_id 
        ORDER BY o.order_date ASC
    ) AS amount_diff_from_prev,
    
    -- 次回注文までの日数
    LEAD(o.order_date, 1) OVER (
        PARTITION BY o.user_id 
        ORDER BY o.order_date ASC
    ) - o.order_date AS days_to_next_order
    
FROM 
    orders o
WHERE 
    o.status = 'completed'
    AND o.order_date >= CURRENT_DATE - INTERVAL '12 months'
ORDER BY 
    o.user_id, o.order_date;

-- ✅ パーセンタイルと分位数関数
SELECT 
    p.product_id,
    p.product_name,
    p.price,
    p.category_id,
    
    -- 価格帯のパーセンタイル
    PERCENT_RANK() OVER (ORDER BY p.price) AS price_percentile,
    
    -- カテゴリ内での価格順位
    NTILE(4) OVER (
        PARTITION BY p.category_id 
        ORDER BY p.price
    ) AS price_quartile,
    
    -- 価格の中央値（カテゴリ内）
    PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY p.price) OVER (
        PARTITION BY p.category_id
    ) AS category_median_price
    
FROM 
    products p
WHERE 
    p.is_active = TRUE
ORDER BY 
    p.category_id, p.price;
```

### 3.4 インデックス活用パターン

#### **カバリングインデックスの活用**
```sql
-- ✅ Good: カバリングインデックスでテーブルアクセスを回避

-- カバリングインデックス作成
CREATE INDEX idx_orders_covering ON orders(
    user_id, 
    status, 
    order_date
) INCLUDE (order_id, total_amount);  -- PostgreSQL 11+

-- MySQL版（INCLUDEなし）
CREATE INDEX idx_orders_covering_mysql ON orders(
    user_id, 
    status, 
    order_date, 
    order_id, 
    total_amount
);

-- カバリングインデックスを活用したクエリ
EXPLAIN (ANALYZE, BUFFERS)
SELECT 
    o.order_id,
    o.total_amount,
    o.order_date
FROM 
    orders o
WHERE 
    o.user_id = $1
    AND o.status = 'completed'
    AND o.order_date >= CURRENT_DATE - INTERVAL '6 months'
ORDER BY 
    o.order_date DESC;
-- Index Only Scanが発生し、テーブルへのアクセスが不要
```

#### **部分インデックスの活用**
```sql
-- ✅ Good: 条件付きインデックスでストレージ節約

-- アクティブユーザーのみのインデックス
CREATE INDEX idx_users_active_email ON users(email_address) 
WHERE status = 'active';

-- 未処理注文のみのインデックス
CREATE INDEX idx_orders_pending_date ON orders(order_date, user_id) 
WHERE status IN ('pending', 'processing');

-- 高価商品のみのインデックス
CREATE INDEX idx_products_expensive ON products(category_id, price) 
WHERE price >= 1000 AND is_active = TRUE;

-- 最近のデータのみのインデックス（時系列データ）
CREATE INDEX idx_audit_logs_recent ON audit_logs(user_id, action, created_at) 
WHERE created_at >= CURRENT_DATE - INTERVAL '90 days';
```

### 3.5 クエリ実行プラン分析

#### **EXPLAIN ANALYZEの活用**
```sql
-- ✅ PostgreSQL: 詳細な実行プラン分析
EXPLAIN (ANALYZE, BUFFERS, VERBOSE, FORMAT JSON)
SELECT 
    u.user_id,
    u.user_name,
    COUNT(o.order_id) AS order_count,
    SUM(o.total_amount) AS total_spent
FROM 
    users u
    LEFT JOIN orders o ON u.user_id = o.user_id 
        AND o.status = 'completed'
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

-- ✅ MySQL: 実行プラン分析
EXPLAIN FORMAT=JSON
SELECT 
    u.user_id,
    u.user_name,
    COUNT(o.order_id) AS order_count,
    SUM(o.total_amount) AS total_spent
FROM 
    users u
    LEFT JOIN orders o ON u.user_id = o.user_id 
        AND o.status = 'completed'
        AND o.order_date >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
WHERE 
    u.status = 'active'
    AND u.created_at >= DATE_SUB(CURDATE(), INTERVAL 2 YEAR)
GROUP BY 
    u.user_id, u.user_name
HAVING 
    COUNT(o.order_id) > 0
ORDER BY 
    total_spent DESC
LIMIT 100;
```

#### **パフォーマンスボトルネックの特定**
```sql
-- ✅ パフォーマンスメトリクスの監視（PostgreSQL）

-- 遅いクエリの特定
SELECT 
    query,
    calls,
    total_time,
    mean_time,
    ROUND((100.0 * total_time / sum(total_time) OVER()), 2) AS percentage
FROM 
    pg_stat_statements 
WHERE 
    mean_time > 100  -- 100ms以上のクエリ
ORDER BY 
    mean_time DESC
LIMIT 10;

-- インデックス使用状況の確認
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan,
    idx_tup_read,
    idx_tup_fetch,
    ROUND(
        100.0 * idx_tup_fetch / NULLIF(idx_tup_read, 0), 2
    ) AS hit_ratio
FROM 
    pg_stat_user_indexes
WHERE 
    idx_scan > 0
ORDER BY 
    idx_scan DESC;

-- 使用されていないインデックスの特定
SELECT 
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(i.indexrelid)) AS index_size
FROM 
    pg_stat_user_indexes ui
    JOIN pg_index i ON ui.indexrelid = i.indexrelid
WHERE 
    ui.idx_scan = 0
    AND NOT i.indisunique  -- ユニーク制約以外
ORDER BY 
    pg_relation_size(i.indexrelid) DESC;
```

**Devin指示**: 必要なカラムのみ取得、適切なJOIN順序・JOIN条件、CTEやウィンドウ関数の活用、カバリングインデックスの作成、EXPLAIN ANALYZEによる分析を必ず実施せよ

---

## 4. セキュリティ・権限管理

### 4.1 SQLインジェクション対策

#### **パラメータ化クエリの強制**
```sql
-- ✅ Good: プリペアドステートメントの使用
-- PostgreSQL例
PREPARE get_user_orders (UUID, DATE, DATE) AS
SELECT 
    o.order_id,
    o.total_amount,
    o.order_date,
    o.status
FROM 
    orders o
WHERE 
    o.user_id = $1
    AND o.order_date >= $2
    AND o.order_date <= $3
    AND o.status NOT IN ('cancelled', 'returned')
ORDER BY 
    o.order_date DESC;

-- 実行
EXECUTE get_user_orders('550e8400-e29b-41d4-a716-446655440000', '2024-01-01', '2024-12-31');

-- MySQL例
PREPARE stmt FROM '
    SELECT 
        o.order_id,
        o.total_amount,
        o.order_date,
        o.status
    FROM 
        orders o
    WHERE 
        o.user_id = ?
        AND o.order_date >= ?
        AND o.order_date <= ?
        AND o.status NOT IN ("cancelled", "returned")
    ORDER BY 
        o.order_date DESC';

SET @user_id = '550e8400-e29b-41d4-a716-446655440000';
SET @start_date = '2024-01-01';
SET @end_date = '2024-12-31';
EXECUTE stmt USING @user_id, @start_date, @end_date;
DEALLOCATE PREPARE stmt;

-- ❌ Bad: 文字列連結（SQLインジェクションのリスク）
-- 絶対に使用禁止
SELECT * FROM users WHERE email = '" + userInput + "';
SELECT * FROM orders WHERE user_id = " + userId + ";
```

#### **入力バリデーションとサニタイゼーション**
```sql
-- ✅ Good: データベースレベルでのバリデーション

-- 入力値検証関数（PostgreSQL）
CREATE OR REPLACE FUNCTION validate_email(
    email_input TEXT
) RETURNS BOOLEAN AS $$
BEGIN
    -- メールアドレス形式チェック
    IF email_input !~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$' THEN
        RETURN FALSE;
    END IF;
    
    -- 長さチェック
    IF LENGTH(email_input) > 255 THEN
        RETURN FALSE;
    END IF;
    
    -- 悪意のある文字列チェック
    IF email_input ~* '(script|javascript|vbscript|onload|onerror)' THEN
        RETURN FALSE;
    END IF;
    
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

-- 入力値サニタイズ関数
CREATE OR REPLACE FUNCTION sanitize_text_input(
    input_text TEXT
) RETURNS TEXT AS $$
BEGIN
    IF input_text IS NULL THEN
        RETURN NULL;
    END IF;
    
    -- HTMLタグとスクリプトを除去
    input_text := regexp_replace(input_text, '<[^>]*>', '', 'g');
    
    -- SQLキーワードをエスケープ
    input_text := replace(input_text, '''', '''''');
    input_text := replace(input_text, ';', '\;');
    input_text := replace(input_text, '--', '\--');
    
    -- 長さ制限
    IF LENGTH(input_text) > 1000 THEN
        input_text := LEFT(input_text, 1000);
    END IF;
    
    RETURN TRIM(input_text);
END;
$$ LANGUAGE plpgsql;

-- 使用例
CREATE OR REPLACE FUNCTION create_user_secure(
    p_email TEXT,
    p_name TEXT,
    p_password_hash TEXT
) RETURNS UUID AS $$
DECLARE
    new_user_id UUID;
BEGIN
    -- 入力バリデーション
    IF NOT validate_email(p_email) THEN
        RAISE EXCEPTION '無効なメールアドレス形式: %', p_email;
    END IF;
    
    -- サニタイゼーション
    p_name := sanitize_text_input(p_name);
    
    -- ユーザー作成
    INSERT INTO users (user_id, email_address, user_name, password_hash)
    VALUES (gen_random_uuid(), p_email, p_name, p_password_hash)
    RETURNING user_id INTO new_user_id;
    
    RETURN new_user_id;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

#### **最小権限の原則**
```sql
-- ✅ Good: 機能別データベースユーザー

-- 読み取り専用ユーザー
CREATE USER app_reader WITH PASSWORD 'strong_password_here';
GRANT CONNECT ON DATABASE ecommerce TO app_reader;
GRANT USAGE ON SCHEMA public TO app_reader;
GRANT SELECT ON users, products, categories TO app_reader;

-- アプリケーション用ユーザー（限定的操作）
CREATE USER app_writer WITH PASSWORD 'strong_password_here';
GRANT CONNECT ON DATABASE ecommerce TO app_writer;
GRANT USAGE ON SCHEMA public TO app_writer;
GRANT SELECT, INSERT, UPDATE ON orders, order_items TO app_writer;
GRANT SELECT ON users, products TO app_writer;
-- 削除権限は付与しない

-- 管理用ユーザー（全権限）
CREATE USER app_admin WITH PASSWORD 'very_strong_password_here';
GRANT ALL PRIVILEGES ON DATABASE ecommerce TO app_admin;

-- バックアップ用ユーザー（読み取りのみ）
CREATE USER backup_user WITH PASSWORD 'backup_password_here';
GRANT CONNECT ON DATABASE ecommerce TO backup_user;
GRANT USAGE ON SCHEMA public TO backup_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO backup_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO backup_user;

-- ❌ Bad: 全機能で同一ユーザー使用
CREATE USER app_user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE ecommerce TO app_user;  -- 過度な権限
```

### 4.2 ロールベースアクセス制御（RBAC）

#### **階層的ロール設計**
```sql
-- ✅ Good: ロールベース権限管理

-- 基本ロール作成
CREATE ROLE readonly_role;
CREATE ROLE readwrite_role;
CREATE ROLE admin_role;
CREATE ROLE audit_role;

-- 権限設定
-- 読み取りロール
GRANT CONNECT ON DATABASE ecommerce TO readonly_role;
GRANT USAGE ON SCHEMA public TO readonly_role;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO readonly_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO readonly_role;

-- 読み書きロール
GRANT readonly_role TO readwrite_role;  -- 読み取り権限を継承
GRANT INSERT, UPDATE ON orders, order_items, users TO readwrite_role;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA public TO readwrite_role;

-- 管理ロール
GRANT readwrite_role TO admin_role;  -- 読み書き権限を継承
GRANT DELETE ON ALL TABLES IN SCHEMA public TO admin_role;
GRANT CREATE ON SCHEMA public TO admin_role;
GRANT USAGE ON SCHEMA information_schema TO admin_role;

-- 監査ロール（ログ閲覧のみ）
GRANT CONNECT ON DATABASE ecommerce TO audit_role;
GRANT USAGE ON SCHEMA public TO audit_role;
GRANT SELECT ON audit_logs, user_sessions TO audit_role;

-- ユーザーにロール付与
CREATE USER john_analyst WITH PASSWORD 'analyst_password';
GRANT readonly_role TO john_analyst;

CREATE USER jane_developer WITH PASSWORD 'dev_password';
GRANT readwrite_role TO jane_developer;

CREATE USER admin_user WITH PASSWORD 'admin_password';
GRANT admin_role TO admin_user;
```

#### **動的権限制御（Row Level Security）**
```sql
-- ✅ Good: 行レベルセキュリティの実装

-- RLSを有効化
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE order_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_addresses ENABLE ROW LEVEL SECURITY;

-- ユーザーは自分のデータのみアクセス可能
CREATE POLICY user_orders_policy ON orders
    FOR ALL TO app_user
    USING (user_id = current_setting('app.current_user_id')::UUID);

CREATE POLICY user_addresses_policy ON user_addresses
    FOR ALL TO app_user
    USING (user_id = current_setting('app.current_user_id')::UUID);

-- 管理者は全データアクセス可能
CREATE POLICY admin_full_access ON orders
    FOR ALL TO admin_role
    USING (true);

-- サポートスタッフは最近のデータのみアクセス可能
CREATE POLICY support_recent_orders ON orders
    FOR SELECT TO support_role
    USING (
        order_date >= CURRENT_DATE - INTERVAL '30 days'
        AND status IN ('pending', 'processing', 'shipped')
    );

-- アプリケーションでの使用例
BEGIN;
-- ユーザーIDをセッション変数に設定
SET LOCAL app.current_user_id = '550e8400-e29b-41d4-a716-446655440000';

-- このクエリは自動的にユーザーのデータのみ返す
SELECT * FROM orders WHERE status = 'completed';
COMMIT;
```

### 4.3 データ暗号化・机密情報保護

#### **機密データの暗号化**
```sql
-- ✅ Good: 機密情報の暗号化保存

-- 暗号化キー管理（PostgreSQL）
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- パスワードハッシュ関数
CREATE OR REPLACE FUNCTION hash_password(
    plain_password TEXT
) RETURNS TEXT AS $$
BEGIN
    RETURN crypt(plain_password, gen_salt('bf', 12));  -- bcrypt, cost=12
END;
$$ LANGUAGE plpgsql;

-- パスワード検証関数
CREATE OR REPLACE FUNCTION verify_password(
    plain_password TEXT,
    hashed_password TEXT
) RETURNS BOOLEAN AS $$
BEGIN
    RETURN (hashed_password = crypt(plain_password, hashed_password));
END;
$$ LANGUAGE plpgsql;

-- 機密データ暗号化関数
CREATE OR REPLACE FUNCTION encrypt_sensitive_data(
    data_to_encrypt TEXT,
    encryption_key TEXT DEFAULT current_setting('app.encryption_key')
) RETURNS BYTEA AS $$
BEGIN
    RETURN pgp_sym_encrypt(data_to_encrypt, encryption_key);
END;
$$ LANGUAGE plpgsql;

-- 機密データ復号化関数
CREATE OR REPLACE FUNCTION decrypt_sensitive_data(
    encrypted_data BYTEA,
    encryption_key TEXT DEFAULT current_setting('app.encryption_key')
) RETURNS TEXT AS $$
BEGIN
    RETURN pgp_sym_decrypt(encrypted_data, encryption_key);
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 機密情報を含むテーブル設計
CREATE TABLE user_sensitive_data (
    user_id UUID PRIMARY KEY REFERENCES users(user_id),
    
    -- 機密情報は暗号化して保存
    ssn_encrypted BYTEA,           -- 社会保障番号
    credit_card_encrypted BYTEA,   -- クレジットカード番号
    bank_account_encrypted BYTEA,  -- 銀行口座番号
    
    -- メタデータ
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    encryption_version INTEGER DEFAULT 1
);

-- 機密データ更新関数
CREATE OR REPLACE FUNCTION update_user_sensitive_data(
    p_user_id UUID,
    p_ssn TEXT,
    p_credit_card TEXT,
    p_bank_account TEXT
) RETURNS VOID AS $$
BEGIN
    INSERT INTO user_sensitive_data (
        user_id,
        ssn_encrypted,
        credit_card_encrypted,
        bank_account_encrypted
    )
    VALUES (
        p_user_id,
        CASE WHEN p_ssn IS NOT NULL THEN encrypt_sensitive_data(p_ssn) END,
        CASE WHEN p_credit_card IS NOT NULL THEN encrypt_sensitive_data(p_credit_card) END,
        CASE WHEN p_bank_account IS NOT NULL THEN encrypt_sensitive_data(p_bank_account) END
    )
    ON CONFLICT (user_id) DO UPDATE SET
        ssn_encrypted = COALESCE(EXCLUDED.ssn_encrypted, user_sensitive_data.ssn_encrypted),
        credit_card_encrypted = COALESCE(EXCLUDED.credit_card_encrypted, user_sensitive_data.credit_card_encrypted),
        bank_account_encrypted = COALESCE(EXCLUDED.bank_account_encrypted, user_sensitive_data.bank_account_encrypted),
        updated_at = CURRENT_TIMESTAMP;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

#### **データマスキング・匿名化**
```sql
-- ✅ Good: データマスキングビューの作成

-- 開発環境用マスキングビュー
CREATE OR REPLACE VIEW users_masked AS
SELECT 
    user_id,
    -- メールアドレスのマスキング
    CASE 
        WHEN current_user IN ('admin_role', 'production_access') THEN email_address
        ELSE 
            SUBSTRING(email_address FROM 1 FOR 2) || 
            '***@' || 
            SUBSTRING(email_address FROM '(?:.*@)(.*)') 
    END AS email_address,
    
    -- 名前のマスキング
    CASE 
        WHEN current_user IN ('admin_role', 'production_access') THEN user_name
        ELSE LEFT(user_name, 1) || '***'
    END AS user_name,
    
    status,
    created_at,
    -- 更新日はぼかし
    DATE_TRUNC('month', updated_at) AS updated_at
FROM users;

-- テストデータ生成関数
CREATE OR REPLACE FUNCTION generate_test_users(
    num_users INTEGER DEFAULT 1000
) RETURNS INTEGER AS $$
DECLARE
    i INTEGER;
    fake_names TEXT[] := ARRAY[
        'John Doe', 'Jane Smith', 'Bob Johnson', 'Alice Brown', 'Charlie Wilson',
        'Diana Davis', 'Eve Miller', 'Frank Moore', 'Grace Taylor', 'Henry Clark'
    ];
    fake_domains TEXT[] := ARRAY[
        'example.com', 'test.org', 'sample.net', 'demo.co'
    ];
BEGIN
    FOR i IN 1..num_users LOOP
        INSERT INTO users (
            user_id,
            email_address,
            user_name,
            password_hash,
            status
        ) VALUES (
            gen_random_uuid(),
            'testuser' || i || '@' || fake_domains[1 + (i % array_length(fake_domains, 1))],
            fake_names[1 + (i % array_length(fake_names, 1))] || ' ' || i,
            hash_password('test_password_' || i),
            CASE WHEN random() > 0.1 THEN 'active' ELSE 'inactive' END
        );
    END LOOP;
    
    RETURN num_users;
END;
$$ LANGUAGE plpgsql;
```

### 4.4 監査ログ・アクセス記録

#### **包括的な監査ログシステム**
```sql
-- ✅ Good: 包括的な監査ログテーブル
CREATE TABLE audit_logs (
    audit_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- 操作情報
    table_name VARCHAR(100) NOT NULL,
    operation VARCHAR(10) NOT NULL CHECK (operation IN ('INSERT', 'UPDATE', 'DELETE', 'SELECT')),
    
    -- ユーザー情報
    user_id UUID REFERENCES users(user_id),
    database_user VARCHAR(100) NOT NULL DEFAULT current_user,
    session_id VARCHAR(100),
    
    -- ネットワーク情報
    client_ip INET,
    user_agent TEXT,
    
    -- データ情報
    old_values JSONB,
    new_values JSONB,
    changed_columns TEXT[],
    
    -- タイムスタンプ
    operation_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- 添加情報
    success BOOLEAN DEFAULT TRUE,
    error_message TEXT,
    execution_time_ms INTEGER
);

-- パーティショニング設定（月次）
CREATE TABLE audit_logs_partitioned (
    LIKE audit_logs INCLUDING ALL
) PARTITION BY RANGE (operation_timestamp);

-- 監査ログ用インデックス
CREATE INDEX idx_audit_logs_user_time ON audit_logs_partitioned (user_id, operation_timestamp);
CREATE INDEX idx_audit_logs_table_operation ON audit_logs_partitioned (table_name, operation);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs_partitioned (operation_timestamp);
CREATE INDEX idx_audit_logs_client_ip ON audit_logs_partitioned (client_ip);

-- 監査ログ記録関数
CREATE OR REPLACE FUNCTION log_audit_event(
    p_table_name VARCHAR(100),
    p_operation VARCHAR(10),
    p_user_id UUID DEFAULT NULL,
    p_old_values JSONB DEFAULT NULL,
    p_new_values JSONB DEFAULT NULL,
    p_client_ip INET DEFAULT NULL,
    p_session_id VARCHAR(100) DEFAULT NULL
) RETURNS UUID AS $$
DECLARE
    audit_id UUID;
    changed_cols TEXT[];
BEGIN
    -- 変更カラムの特定
    IF p_old_values IS NOT NULL AND p_new_values IS NOT NULL THEN
        SELECT array_agg(key) 
        INTO changed_cols
        FROM jsonb_each(p_new_values) 
        WHERE value != COALESCE(p_old_values->key, 'null'::jsonb);
    END IF;
    
    INSERT INTO audit_logs (
        table_name,
        operation,
        user_id,
        database_user,
        session_id,
        client_ip,
        old_values,
        new_values,
        changed_columns
    ) VALUES (
        p_table_name,
        p_operation,
        p_user_id,
        current_user,
        COALESCE(p_session_id, current_setting('app.session_id', true)),
        COALESCE(p_client_ip, inet_client_addr()),
        p_old_values,
        p_new_values,
        changed_cols
    ) RETURNING audit_logs.audit_id INTO audit_id;
    
    RETURN audit_id;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- トリガー関数（汎用）
CREATE OR REPLACE FUNCTION audit_trigger_function()
RETURNS TRIGGER AS $$
DECLARE
    old_data JSONB;
    new_data JSONB;
    user_id_val UUID;
BEGIN
    -- JSON形式でデータを保存
    IF TG_OP = 'DELETE' THEN
        old_data := to_jsonb(OLD);
        user_id_val := OLD.user_id;
    ELSIF TG_OP = 'INSERT' THEN
        new_data := to_jsonb(NEW);
        user_id_val := NEW.user_id;
    ELSIF TG_OP = 'UPDATE' THEN
        old_data := to_jsonb(OLD);
        new_data := to_jsonb(NEW);
        user_id_val := NEW.user_id;
    END IF;
    
    -- 監査ログ記録
    PERFORM log_audit_event(
        TG_TABLE_NAME,
        TG_OP,
        user_id_val,
        old_data,
        new_data
    );
    
    IF TG_OP = 'DELETE' THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- 重要テーブルに監査トリガーを設定
CREATE TRIGGER audit_users_trigger
    AFTER INSERT OR UPDATE OR DELETE ON users
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

CREATE TRIGGER audit_orders_trigger
    AFTER INSERT OR UPDATE OR DELETE ON orders
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();
```

**Devin指示**: パラメータ化クエリの強制、最小権限の原則、機密データの暗号化、包括的な監査ログ、Row Level Securityの実装を必ず適用せよ

---

## 5. エラーハンドリング・例外処理

### 5.1 構造化されたエラーハンドリング

#### **エラーコード体系の確立**
```sql
-- ✅ Good: 構造化されたエラーコード管理

-- エラーコードマスターテーブル
CREATE TABLE error_codes (
    error_code VARCHAR(20) PRIMARY KEY,
    error_category VARCHAR(50) NOT NULL,
    severity_level INTEGER NOT NULL CHECK (severity_level BETWEEN 1 AND 5),
    error_message_template TEXT NOT NULL,
    user_message_template TEXT,
    suggested_action TEXT,
    is_retryable BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 標準エラーコードの登録
INSERT INTO error_codes (error_code, error_category, severity_level, error_message_template, user_message_template, suggested_action, is_retryable) VALUES
-- ビジネスロジックエラー
('BUS_001', 'BUSINESS', 2, 'Insufficient inventory for product {product_id}: available={available}, requested={requested}', '在庫不足のため、ご注文を処理できません', '在庫数を確認してください', false),
('BUS_002', 'BUSINESS', 2, 'User {user_id} is not active', 'アカウントが無効です', 'アカウントを有効化してください', false),
('BUS_003', 'BUSINESS', 3, 'Payment method {payment_id} has expired', 'お支払い方法が有効期限切れです', '新しい支払い方法を登録してください', false),

-- データベースエラー
('DB_001', 'DATABASE', 4, 'Foreign key constraint violation: {constraint_name}', 'データの整合性エラーが発生しました', 'システム管理者にお問い合わせください', false),
('DB_002', 'DATABASE', 3, 'Unique constraint violation: {constraint_name}', '重複するデータが存在します', '別の値を入力してください', false),
('DB_003', 'DATABASE', 5, 'Deadlock detected in transaction', 'データベースの一時的な競合が発生しました', 'しばらくしてから再度実行してください', true),

-- システムエラー
('SYS_001', 'SYSTEM', 5, 'Connection timeout to database', 'システム一時的な障害が発生しています', 'しばらくしてから再度お試しください', true),
('SYS_002', 'SYSTEM', 4, 'Disk space insufficient', 'システムリソース不足', 'システム管理者にお問い合わせください', false),

-- バリデーションエラー
('VAL_001', 'VALIDATION', 2, 'Invalid email format: {email}', 'メールアドレスの形式が正しくありません', '正しいメールアドレスを入力してください', false),
('VAL_002', 'VALIDATION', 2, 'Password does not meet requirements', 'パスワードが要件を満たしていません', '8文字以上で英数字記号を含むパスワードを入力してください', false);

-- エラーハンドリング用関数
CREATE OR REPLACE FUNCTION handle_error(
    p_error_code VARCHAR(20),
    p_context JSONB DEFAULT '{}',
    p_user_id UUID DEFAULT NULL
) RETURNS JSONB AS $$
DECLARE
    error_info RECORD;
    formatted_message TEXT;
    formatted_user_message TEXT;
    result JSONB;
BEGIN
    -- エラー情報を取得
    SELECT * INTO error_info 
    FROM error_codes 
    WHERE error_code = p_error_code;
    
    IF NOT FOUND THEN
        -- 未定義エラーの場合
        error_info := ROW(
            'UNK_001', 'UNKNOWN', 3, 
            'Unknown error occurred', 
            '予期しないエラーが発生しました', 
            'システム管理者にお問い合わせください', 
            false, CURRENT_TIMESTAMP
        );
    END IF;
    
    -- メッセージテンプレートの置換
    formatted_message := error_info.error_message_template;
    formatted_user_message := error_info.user_message_template;
    
    -- コンテキスト変数で置換
    IF p_context IS NOT NULL THEN
        FOR key, value IN SELECT * FROM jsonb_each_text(p_context) LOOP
            formatted_message := replace(formatted_message, '{' || key || '}', value);
            formatted_user_message := replace(formatted_user_message, '{' || key || '}', value);
        END LOOP;
    END IF;
    
    -- 結果JSONを作成
    result := jsonb_build_object(
        'error_code', error_info.error_code,
        'category', error_info.error_category,
        'severity', error_info.severity_level,
        'message', formatted_message,
        'user_message', formatted_user_message,
        'suggested_action', error_info.suggested_action,
        'is_retryable', error_info.is_retryable,
        'timestamp', CURRENT_TIMESTAMP,
        'context', COALESCE(p_context, '{}'::jsonb)
    );
    
    -- エラーログへ記録
    INSERT INTO error_logs (
        error_code,
        user_id,
        error_message,
        user_message,
        context_data,
        severity_level,
        client_ip,
        session_id
    ) VALUES (
        error_info.error_code,
        p_user_id,
        formatted_message,
        formatted_user_message,
        p_context,
        error_info.severity_level,
        inet_client_addr(),
        current_setting('app.session_id', true)
    );
    
    RETURN result;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

#### **エラーログテーブルの設計**
```sql
-- ✅ Good: 包括的なエラーログテーブル
CREATE TABLE error_logs (
    log_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- エラー情報
    error_code VARCHAR(20) NOT NULL REFERENCES error_codes(error_code),
    error_message TEXT NOT NULL,
    user_message TEXT,
    
    -- コンテキスト情報
    user_id UUID REFERENCES users(user_id),
    session_id VARCHAR(100),
    transaction_id VARCHAR(100),
    
    -- システム情報
    database_name VARCHAR(100) DEFAULT current_database(),
    database_user VARCHAR(100) DEFAULT current_user,
    client_ip INET,
    user_agent TEXT,
    
    -- 技術的詳細
    sql_state VARCHAR(10),
    constraint_name VARCHAR(200),
    table_name VARCHAR(100),
    column_name VARCHAR(100),
    
    -- 添加データ
    context_data JSONB,
    stack_trace TEXT,
    
    -- メタデータ
    severity_level INTEGER NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP WITH TIME ZONE,
    resolution_notes TEXT
);

-- エラーログ用インデックス
CREATE INDEX idx_error_logs_occurred ON error_logs(occurred_at);
CREATE INDEX idx_error_logs_code_time ON error_logs(error_code, occurred_at);
CREATE INDEX idx_error_logs_user ON error_logs(user_id, occurred_at);
CREATE INDEX idx_error_logs_severity ON error_logs(severity_level, occurred_at);
CREATE INDEX idx_error_logs_unresolved ON error_logs(occurred_at) WHERE resolved_at IS NULL;
```

### 5.2 カスタム例外の定義と活用

#### **ビジネスロジック例外**
```sql
-- ✅ Good: ビジネスロジック例外の実装

-- 在庫不足チェック関数
CREATE OR REPLACE FUNCTION check_inventory_availability(
    p_product_id UUID,
    p_requested_quantity INTEGER
) RETURNS VOID AS $$
DECLARE
    available_stock INTEGER;
    product_name VARCHAR(200);
BEGIN
    SELECT 
        stock_quantity,
        product_name
    INTO 
        available_stock,
        product_name
    FROM products 
    WHERE product_id = p_product_id AND is_active = TRUE;
    
    IF NOT FOUND THEN
        PERFORM handle_error(
            'BUS_004', 
            jsonb_build_object(
                'product_id', p_product_id::TEXT
            )
        );
        RAISE EXCEPTION 'BUSINESS_ERROR:BUS_004: Product not found: %', p_product_id;
    END IF;
    
    IF available_stock < p_requested_quantity THEN
        PERFORM handle_error(
            'BUS_001', 
            jsonb_build_object(
                'product_id', p_product_id::TEXT,
                'product_name', product_name,
                'available', available_stock::TEXT,
                'requested', p_requested_quantity::TEXT
            )
        );
        RAISE EXCEPTION 'BUSINESS_ERROR:BUS_001: Insufficient inventory for product % (%): available=%, requested=%', 
            p_product_id, product_name, available_stock, p_requested_quantity;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- 注文作成関数（例外処理付き）
CREATE OR REPLACE FUNCTION create_order_safe(
    p_user_id UUID,
    p_items JSONB  -- [{'product_id': 'uuid', 'quantity': int, 'unit_price': decimal}]
) RETURNS UUID AS $$
DECLARE
    new_order_id UUID;
    item RECORD;
    total_amount DECIMAL(12,2) := 0;
BEGIN
    -- 入力バリデーション
    IF p_items IS NULL OR jsonb_array_length(p_items) = 0 THEN
        PERFORM handle_error(
            'VAL_003',
            jsonb_build_object('user_id', p_user_id::TEXT)
        );
        RAISE EXCEPTION 'VALIDATION_ERROR:VAL_003: Order items cannot be empty';
    END IF;
    
    -- ユーザーアクティブ状態チェック
    IF NOT EXISTS (SELECT 1 FROM users WHERE user_id = p_user_id AND status = 'active') THEN
        PERFORM handle_error(
            'BUS_002',
            jsonb_build_object('user_id', p_user_id::TEXT)
        );
        RAISE EXCEPTION 'BUSINESS_ERROR:BUS_002: User is not active: %', p_user_id;
    END IF;
    
    -- トランザクション開始
    BEGIN
        -- 注文ヘッダー作成
        INSERT INTO orders (order_id, user_id, status)
        VALUES (gen_random_uuid(), p_user_id, 'pending')
        RETURNING order_id INTO new_order_id;
        
        -- 各アイテムを処理
        FOR item IN SELECT * FROM jsonb_to_recordset(p_items) AS x(
            product_id UUID, 
            quantity INTEGER, 
            unit_price DECIMAL(10,2)
        ) LOOP
            -- 在庫チェック
            PERFORM check_inventory_availability(item.product_id, item.quantity);
            
            -- 注文明細追加
            INSERT INTO order_items (
                order_id, 
                product_id, 
                quantity, 
                unit_price
            ) VALUES (
                new_order_id,
                item.product_id,
                item.quantity,
                item.unit_price
            );
            
            -- 在庫更新
            UPDATE products 
            SET stock_quantity = stock_quantity - item.quantity,
                updated_at = CURRENT_TIMESTAMP
            WHERE product_id = item.product_id;
            
            total_amount := total_amount + (item.quantity * item.unit_price);
        END LOOP;
        
        -- 注文金額更新
        UPDATE orders 
        SET total_amount = total_amount,
            status = 'confirmed',
            updated_at = CURRENT_TIMESTAMP
        WHERE order_id = new_order_id;
        
        RETURN new_order_id;
        
    EXCEPTION
        WHEN OTHERS THEN
            -- エラーログ記録
            PERFORM handle_error(
                'SYS_003',
                jsonb_build_object(
                    'user_id', p_user_id::TEXT,
                    'sql_state', SQLSTATE,
                    'sql_error_message', SQLERRM,
                    'order_items', p_items::TEXT
                )
            );
            
            -- ロールバック（自動実行）
            RAISE;
    END;
END;
$$ LANGUAGE plpgsql;
```

### 5.3 トランザクション管理とロールバック戦略

#### **セーブポイントを使用した部分ロールバック**
```sql
-- ✅ Good: セーブポイントを使用した精密なエラー処理

CREATE OR REPLACE FUNCTION process_bulk_orders(
    p_orders JSONB  -- 複数注文の配列
) RETURNS JSONB AS $$
DECLARE
    order_data RECORD;
    order_result JSONB;
    results JSONB := '[]'::jsonb;
    success_count INTEGER := 0;
    error_count INTEGER := 0;
    order_id UUID;
BEGIN
    -- メイントランザクション開始
    FOR order_data IN SELECT * FROM jsonb_to_recordset(p_orders) AS x(
        user_id UUID,
        items JSONB
    ) LOOP
        
        -- 各注文用のセーブポイント作成
        SAVEPOINT sp_order;
        
        BEGIN
            -- 注文処理実行
            order_id := create_order_safe(order_data.user_id, order_data.items);
            
            order_result := jsonb_build_object(
                'user_id', order_data.user_id,
                'order_id', order_id,
                'status', 'success',
                'message', '注文が正常に処理されました'
            );
            
            success_count := success_count + 1;
            
            -- セーブポイントをリリース
            RELEASE SAVEPOINT sp_order;
            
        EXCEPTION
            WHEN OTHERS THEN
                -- この注文のみロールバック
                ROLLBACK TO SAVEPOINT sp_order;
                
                order_result := jsonb_build_object(
                    'user_id', order_data.user_id,
                    'order_id', null,
                    'status', 'error',
                    'error_code', split_part(SQLERRM, ':', 2),
                    'message', split_part(SQLERRM, ':', 3)
                );
                
                error_count := error_count + 1;
        END;
        
        -- 結果を集積
        results := results || order_result;
    END LOOP;
    
    -- 全体結果を返す
    RETURN jsonb_build_object(
        'total_orders', jsonb_array_length(p_orders),
        'success_count', success_count,
        'error_count', error_count,
        'results', results,
        'processed_at', CURRENT_TIMESTAMP
    );
END;
$$ LANGUAGE plpgsql;
```

#### **デッドロック対策とリトライメカニズム**
```sql
-- ✅ Good: デッドロック対策と自動リトライ

CREATE OR REPLACE FUNCTION update_inventory_with_retry(
    p_product_id UUID,
    p_quantity_change INTEGER,
    p_max_retries INTEGER DEFAULT 5
) RETURNS BOOLEAN AS $$
DECLARE
    retry_count INTEGER := 0;
    wait_time INTERVAL;
    current_stock INTEGER;
BEGIN
    LOOP
        BEGIN
            -- 排他ロックで在庫更新
            SELECT stock_quantity INTO current_stock
            FROM products 
            WHERE product_id = p_product_id
            FOR UPDATE;
            
            -- 在庫不足チェック
            IF current_stock + p_quantity_change < 0 THEN
                PERFORM handle_error(
                    'BUS_001',
                    jsonb_build_object(
                        'product_id', p_product_id::TEXT,
                        'current_stock', current_stock::TEXT,
                        'requested_change', p_quantity_change::TEXT
                    )
                );
                RETURN FALSE;
            END IF;
            
            -- 在庫更新
            UPDATE products 
            SET stock_quantity = stock_quantity + p_quantity_change,
                updated_at = CURRENT_TIMESTAMP
            WHERE product_id = p_product_id;
            
            RETURN TRUE;
            
        EXCEPTION
            WHEN serialization_failure OR deadlock_detected THEN
                retry_count := retry_count + 1;
                
                IF retry_count >= p_max_retries THEN
                    PERFORM handle_error(
                        'DB_003',
                        jsonb_build_object(
                            'product_id', p_product_id::TEXT,
                            'retry_count', retry_count::TEXT,
                            'max_retries', p_max_retries::TEXT
                        )
                    );
                    RAISE EXCEPTION 'DATABASE_ERROR:DB_003: Max retries exceeded for deadlock';
                END IF;
                
                -- 指数関数的バックオフ
                wait_time := (random() * power(2, retry_count)) * INTERVAL '100 milliseconds';
                PERFORM pg_sleep(extract(epoch from wait_time));
                
                -- リトライログ記録
                INSERT INTO retry_logs (
                    operation_type,
                    resource_id,
                    retry_count,
                    wait_time_ms,
                    error_type
                ) VALUES (
                    'update_inventory',
                    p_product_id::TEXT,
                    retry_count,
                    extract(epoch from wait_time) * 1000,
                    SQLSTATE
                );
                
            WHEN OTHERS THEN
                -- リトライ不可能なエラー
                PERFORM handle_error(
                    'DB_004',
                    jsonb_build_object(
                        'product_id', p_product_id::TEXT,
                        'sql_state', SQLSTATE,
                        'sql_error', SQLERRM
                    )
                );
                RAISE;
        END;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- リトライログテーブル
CREATE TABLE retry_logs (
    log_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    operation_type VARCHAR(100) NOT NULL,
    resource_id TEXT NOT NULL,
    retry_count INTEGER NOT NULL,
    wait_time_ms INTEGER NOT NULL,
    error_type VARCHAR(10),
    occurred_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
```

### 5.4 ユーザー向けエラーメッセージと結果処理

#### **多言語対応エラーメッセージ**
```sql
-- ✅ Good: 国際化対応エラーメッセージ

CREATE TABLE error_messages_i18n (
    error_code VARCHAR(20) NOT NULL REFERENCES error_codes(error_code),
    language_code VARCHAR(5) NOT NULL,
    user_message TEXT NOT NULL,
    suggested_action TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (error_code, language_code)
);

-- 多言語メッセージ登録
INSERT INTO error_messages_i18n (error_code, language_code, user_message, suggested_action) VALUES
-- 日本語
('BUS_001', 'ja-JP', '在庫不足のため、ご注文を処理できません', '在庫数を確認してください'),
('BUS_002', 'ja-JP', 'アカウントが無効です', 'アカウントを有効化してください'),
-- 英語
('BUS_001', 'en-US', 'Insufficient inventory to process your order', 'Please check available stock'),
('BUS_002', 'en-US', 'Account is inactive', 'Please activate your account'),
-- 中国語
('BUS_001', 'zh-CN', '库存不足，无法处理您的订单', '请检查可用库存'),
('BUS_002', 'zh-CN', '账户已停用', '请激活您的账户');

-- 国際化対応エラーハンドラー
CREATE OR REPLACE FUNCTION handle_error_i18n(
    p_error_code VARCHAR(20),
    p_language VARCHAR(5) DEFAULT 'en-US',
    p_context JSONB DEFAULT '{}',
    p_user_id UUID DEFAULT NULL
) RETURNS JSONB AS $$
DECLARE
    error_info RECORD;
    localized_message TEXT;
    localized_action TEXT;
    result JSONB;
BEGIN
    -- ローカライズされたメッセージを取得
    SELECT 
        em.user_message,
        em.suggested_action
    INTO 
        localized_message,
        localized_action
    FROM error_messages_i18n em
    WHERE em.error_code = p_error_code 
      AND em.language_code = p_language;
    
    -- フォールバック：英語メッセージ
    IF localized_message IS NULL THEN
        SELECT 
            em.user_message,
            em.suggested_action
        INTO 
            localized_message,
            localized_action
        FROM error_messages_i18n em
        WHERE em.error_code = p_error_code 
          AND em.language_code = 'en-US';
    END IF;
    
    -- 最終フォールバック：デフォルトメッセージ
    IF localized_message IS NULL THEN
        localized_message := 'An unexpected error occurred';
        localized_action := 'Please contact system administrator';
    END IF;
    
    -- コンテキスト変数で置換
    IF p_context IS NOT NULL THEN
        FOR key, value IN SELECT * FROM jsonb_each_text(p_context) LOOP
            localized_message := replace(localized_message, '{' || key || '}', value);
            localized_action := replace(localized_action, '{' || key || '}', value);
        END LOOP;
    END IF;
    
    -- 結果を作成
    result := jsonb_build_object(
        'error_code', p_error_code,
        'language', p_language,
        'user_message', localized_message,
        'suggested_action', localized_action,
        'timestamp', CURRENT_TIMESTAMP,
        'context', COALESCE(p_context, '{}'::jsonb)
    );
    
    -- エラーログ記録
    INSERT INTO error_logs (
        error_code,
        user_id,
        user_message,
        context_data,
        client_ip,
        session_id
    ) VALUES (
        p_error_code,
        p_user_id,
        localized_message,
        p_context,
        inet_client_addr(),
        current_setting('app.session_id', true)
    );
    
    RETURN result;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

### 5.5 Devin実行ガイドライン

#### **エラーハンドリングチェックリスト**

**設計段階**:
- [ ] エラーコード体系の確立（カテゴリ・重要度・リトライ可能性）
- [ ] エラーメッセージの国際化対応
- [ ] ビジネスロジック例外の定義
- [ ] デッドロック・タイムアウト対策
- [ ] エラーログテーブルの設計（パーティショニング含む）

**実装段階**:
- [ ] エラーハンドリング関数の実装
- [ ] トランザクション管理とセーブポイントの活用
- [ ] リトライメカニズムの実装
- [ ] カスタム例外の定義
- [ ] エラーログ記録トリガーの設定

**テスト段階**:
- [ ] 各エラーシナリオのテスト実行
- [ ] デッドロック発生時の動作確認
- [ ] エラーメッセージの国際化テスト
- [ ] ロールバック機能の検証
- [ ] エラーログの正常記録確認

#### **実装パターン例**

```sql
-- 標準的なエラーハンドリングパターン
CREATE OR REPLACE FUNCTION business_operation_template(
    p_param1 UUID,
    p_param2 TEXT
) RETURNS JSONB AS $$
DECLARE
    result JSONB;
BEGIN
    -- 1. 入力バリデーション
    IF p_param1 IS NULL THEN
        RETURN handle_error_i18n('VAL_001', 'ja-JP', 
            jsonb_build_object('parameter', 'param1'));
    END IF;
    
    -- 2. ビジネスロジック実行（例外処理付き）
    BEGIN
        -- メイン処理
        -- ...
        
        result := jsonb_build_object(
            'status', 'success',
            'data', '...',
            'timestamp', CURRENT_TIMESTAMP
        );
        
    EXCEPTION
        WHEN OTHERS THEN
            -- エラー情報を返す
            RETURN handle_error_i18n('SYS_001', 'ja-JP',
                jsonb_build_object(
                    'sql_state', SQLSTATE,
                    'sql_error', SQLERRM
                ));
    END;
    
    RETURN result;
END;
$$ LANGUAGE plpgsql;
```

**Devin指示**: 構造化されたエラーコード体系、カスタム例外の定義、セーブポイントを使用したトランザクション管理、デッドロックリトライ、国際化対応エラーメッセージを必ず実装せよ

---

## 6. テスト・品質保証

### 6.1 SQLユニットテストの実装

#### **pgTAPを使用したテストフレームワーク**
```sql
-- ✅ Good: pgTAPを使用した包括的なユニットテスト

-- pgTAP拡張を有効化
CREATE EXTENSION IF NOT EXISTS pgtap;

-- テスト用スキーマ作成
CREATE SCHEMA IF NOT EXISTS test_schema;
SET search_path TO test_schema, public;

-- テスト用ヘルパー関数
CREATE OR REPLACE FUNCTION setup_test_data()
RETURNS VOID AS $$
BEGIN
    -- テストデータのクリーンアップ
    TRUNCATE TABLE test_schema.users CASCADE;
    TRUNCATE TABLE test_schema.products CASCADE;
    TRUNCATE TABLE test_schema.orders CASCADE;
    
    -- テストユーザー作成
    INSERT INTO test_schema.users (user_id, email_address, user_name, status)
    VALUES 
        ('550e8400-e29b-41d4-a716-446655440001', 'test1@example.com', 'Test User 1', 'active'),
        ('550e8400-e29b-41d4-a716-446655440002', 'test2@example.com', 'Test User 2', 'inactive'),
        ('550e8400-e29b-41d4-a716-446655440003', 'test3@example.com', 'Test User 3', 'active');
    
    -- テスト商品作成
    INSERT INTO test_schema.products (product_id, product_name, price, stock_quantity, is_active)
    VALUES 
        ('550e8400-e29b-41d4-a716-446655440011', 'Test Product 1', 100.00, 50, true),
        ('550e8400-e29b-41d4-a716-446655440012', 'Test Product 2', 200.00, 0, true),
        ('550e8400-e29b-41d4-a716-446655440013', 'Test Product 3', 300.00, 10, false);
END;
$$ LANGUAGE plpgsql;

-- テストケース: create_order_safe関数のテスト
BEGIN;
SELECT plan(15);  -- テスト数を宣言

-- テストデータセットアップ
SELECT setup_test_data();

-- テスト 1: 正常な注文作成
SELECT lives_ok(
    $test$
        SELECT create_order_safe(
            '550e8400-e29b-41d4-a716-446655440001',
            '[{"product_id": "550e8400-e29b-41d4-a716-446655440011", "quantity": 2, "unit_price": 100.00}]'::jsonb
        )
    $test$,
    '正常な注文作成が成功すること'
);

-- テスト 2: 作成された注文の存在確認
SELECT ok(
    (SELECT COUNT(*) FROM test_schema.orders WHERE user_id = '550e8400-e29b-41d4-a716-446655440001') = 1,
    '注文がデータベースに保存されていること'
);

-- テスト 3: 在庫が正しく更新されていること
SELECT is(
    (SELECT stock_quantity FROM test_schema.products WHERE product_id = '550e8400-e29b-41d4-a716-446655440011'),
    48,
    '在庫数が正しく減っていること'
);

-- テスト 4: 非アクティブユーザーの注文失敗
SELECT throws_ok(
    $test$
        SELECT create_order_safe(
            '550e8400-e29b-41d4-a716-446655440002',
            '[{"product_id": "550e8400-e29b-41d4-a716-446655440011", "quantity": 1, "unit_price": 100.00}]'::jsonb
        )
    $test$,
    'BUSINESS_ERROR:BUS_002%',
    '非アクティブユーザーの注文が失敗すること'
);

-- テスト 5: 在庫不足時のエラー
SELECT throws_ok(
    $test$
        SELECT create_order_safe(
            '550e8400-e29b-41d4-a716-446655440001',
            '[{"product_id": "550e8400-e29b-41d4-a716-446655440012", "quantity": 1, "unit_price": 200.00}]'::jsonb
        )
    $test$,
    'BUSINESS_ERROR:BUS_001%',
    '在庫不足時に適切なエラーが発生すること'
);

-- テスト 6-10: 境界値テスト
SELECT throws_ok(
    $test$
        SELECT create_order_safe(
            '550e8400-e29b-41d4-a716-446655440001',
            '[]'::jsonb
        )
    $test$,
    'VALIDATION_ERROR:VAL_003%',
    '空の注文アイテムでエラーが発生すること'
);

-- テスト 7: NULLユーザーID
SELECT throws_ok(
    $test$
        SELECT create_order_safe(
            NULL,
            '[{"product_id": "550e8400-e29b-41d4-a716-446655440011", "quantity": 1, "unit_price": 100.00}]'::jsonb
        )
    $test$,
    '%',
    'NULLユーザーIDでエラーが発生すること'
);

-- テスト 8-10: 複数アイテムの注文
SELECT setup_test_data();  -- データリセット

SELECT lives_ok(
    $test$
        SELECT create_order_safe(
            '550e8400-e29b-41d4-a716-446655440001',
            '[
                {"product_id": "550e8400-e29b-41d4-a716-446655440011", "quantity": 2, "unit_price": 100.00},
                {"product_id": "550e8400-e29b-41d4-a716-446655440013", "quantity": 1, "unit_price": 300.00}
            ]'::jsonb
        )
    $test$,
    '複数アイテムの注文が成功すること'
);

-- 注文金額の正確性テスト
SELECT is(
    (SELECT total_amount FROM test_schema.orders WHERE user_id = '550e8400-e29b-41d4-a716-446655440001' ORDER BY created_at DESC LIMIT 1),
    500.00::DECIMAL(12,2),
    '注文金額が正しく計算されていること'
);

-- 注文明細の数確認
SELECT is(
    (SELECT COUNT(*) FROM test_schema.order_items oi 
     JOIN test_schema.orders o ON oi.order_id = o.order_id 
     WHERE o.user_id = '550e8400-e29b-41d4-a716-446655440001'),
    2::BIGINT,
    '注文明細が正しく保存されていること'
);

-- テスト終了
SELECT finish();
ROLLBACK;
```

#### **テストケース自動生成システム**
```sql
-- ✅ Good: テストケースの自動生成

CREATE OR REPLACE FUNCTION generate_function_tests(
    p_schema_name TEXT DEFAULT 'public',
    p_function_name TEXT DEFAULT NULL
) RETURNS TEXT AS $$
DECLARE
    func_info RECORD;
    test_code TEXT := '';
    param_list TEXT;
    test_values TEXT;
BEGIN
    FOR func_info IN 
        SELECT 
            n.nspname as schema_name,
            p.proname as function_name,
            pg_get_function_arguments(p.oid) as arguments,
            pg_get_function_result(p.oid) as return_type
        FROM pg_proc p
        JOIN pg_namespace n ON p.pronamespace = n.oid
        WHERE n.nspname = p_schema_name
          AND (p_function_name IS NULL OR p.proname = p_function_name)
          AND p.proname NOT LIKE 'test_%'
        ORDER BY p.proname
    LOOP
        test_code := test_code || format('
-- テスト: %s.%s
BEGIN;
SELECT plan(5);

-- 正常ケーステスト
SELECT lives_ok(
    $test$
        SELECT %s.%s(%s)
    $test$,
    ''%s関数の正常実行''
);

-- NULL入力テスト
SELECT throws_ok(
    $test$
        SELECT %s.%s(NULL)
    $test$,
    ''%%'',
    ''NULL入力時のエラーハンドリング''
);

SELECT finish();
ROLLBACK;

',
            func_info.schema_name,
            func_info.function_name,
            func_info.schema_name,
            func_info.function_name,
            '\'test_value\'',  -- デフォルトテスト値
            func_info.function_name,
            func_info.schema_name,
            func_info.function_name
        );
    END LOOP;
    
    RETURN test_code;
END;
$$ LANGUAGE plpgsql;
```

### 6.2 データベーススキーマテスト

#### **スキーマ検証テスト**
```sql
-- ✅ Good: スキーマの一貫性と制約テスト

-- テーブル存在テスト
BEGIN;
SELECT plan(20);

-- 必要テーブルの存在確認
SELECT has_table('public', 'users', 'ユーザーテーブルが存在すること');
SELECT has_table('public', 'products', '商品テーブルが存在すること');
SELECT has_table('public', 'orders', '注文テーブルが存在すること');
SELECT has_table('public', 'order_items', '注文明細テーブルが存在すること');

-- カラム存在テスト
SELECT has_column('public', 'users', 'user_id', 'ユーザーIDカラムが存在すること');
SELECT has_column('public', 'users', 'email_address', 'メールアドレスカラムが存在すること');
SELECT has_column('public', 'users', 'status', 'ステータスカラムが存在すること');

-- 主キー制約テスト
SELECT has_pk('public', 'users', 'ユーザーテーブルに主キーが存在すること');
SELECT col_is_pk('public', 'users', 'user_id', 'user_idが主キーであること');

-- 外部キー制約テスト
SELECT has_fk('public', 'orders', '注文テーブルに外部キーが存在すること');
SELECT col_is_fk('public', 'orders', 'user_id', 'user_idが外部キーであること');

-- ユニーク制約テスト
SELECT col_is_unique('public', 'users', 'email_address', 'メールアドレスがユニークであること');

-- NOT NULL制約テスト
SELECT col_not_null('public', 'users', 'email_address', 'メールアドレスがNOT NULLであること');
SELECT col_not_null('public', 'users', 'user_name', 'ユーザー名がNOT NULLであること');

-- データ型テスト
SELECT col_type_is('public', 'users', 'user_id', 'uuid', 'user_idがUUID型であること');
SELECT col_type_is('public', 'users', 'email_address', 'character varying(255)', 'メールアドレスの型が正しいこと');

-- デフォルト値テスト
SELECT col_has_default('public', 'users', 'created_at', 'created_atにデフォルト値が設定されていること');
SELECT col_has_default('public', 'users', 'status', 'statusにデフォルト値が設定されていること');

-- インデックス存在テスト
SELECT has_index('public', 'users', 'idx_users_email', 'メールアドレスインデックスが存在すること');
SELECT has_index('public', 'orders', 'idx_orders_user_date', 'ユーザー・日付インデックスが存在すること');

SELECT finish();
ROLLBACK;
```

#### **制約検証テスト**
```sql
-- ✅ Good: ビジネスルール制約のテスト

BEGIN;
SELECT plan(10);

-- テストデータセットアップ
SELECT setup_test_data();

-- CHECK制約テスト: 価格は正の値
SELECT throws_ok(
    $test$
        INSERT INTO test_schema.products (product_id, product_name, price, stock_quantity)
        VALUES (gen_random_uuid(), 'Negative Price Product', -100.00, 10)
    $test$,
    '23514',  -- check_violation
    '負の価格で商品作成が失敗すること'
);

-- CHECK制約テスト: 在庫は非負の値
SELECT throws_ok(
    $test$
        INSERT INTO test_schema.products (product_id, product_name, price, stock_quantity)
        VALUES (gen_random_uuid(), 'Negative Stock Product', 100.00, -5)
    $test$,
    '23514',
    '負の在庫で商品作成が失敗すること'
);

-- UNIQUE制約テスト: メールアドレスの重複
SELECT throws_ok(
    $test$
        INSERT INTO test_schema.users (user_id, email_address, user_name)
        VALUES (gen_random_uuid(), 'test1@example.com', 'Duplicate User')
    $test$,
    '23505',  -- unique_violation
    '重複メールアドレスでユーザー作成が失敗すること'
);

-- FOREIGN KEY制約テスト: 存在しないユーザーで注文作成
SELECT throws_ok(
    $test$
        INSERT INTO test_schema.orders (order_id, user_id, total_amount)
        VALUES (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440099', 100.00)
    $test$,
    '23503',  -- foreign_key_violation
    '存在しないユーザーで注文作成が失敗すること'
);

-- NOT NULL制約テスト: 必須フィールドのNULL
SELECT throws_ok(
    $test$
        INSERT INTO test_schema.users (user_id, user_name)
        VALUES (gen_random_uuid(), 'No Email User')
    $test$,
    '23502',  -- not_null_violation
    'メールアドレスNULLでユーザー作成が失敗すること'
);

-- カスケード削除テスト
DELETE FROM test_schema.users WHERE user_id = '550e8400-e29b-41d4-a716-446655440001';

SELECT ok(
    (SELECT COUNT(*) FROM test_schema.orders WHERE user_id = '550e8400-e29b-41d4-a716-446655440001') = 0,
    'ユーザー削除時に関連注文も削除されること'
);

-- 日付範囲チェック
SELECT throws_ok(
    $test$
        INSERT INTO test_schema.orders (order_id, user_id, order_date, shipped_at)
        VALUES (
            gen_random_uuid(), 
            '550e8400-e29b-41d4-a716-446655440003', 
            '2024-01-01 10:00:00'::timestamp,
            '2023-12-31 10:00:00'::timestamp  -- 注文日より前の発送日
        )
    $test$,
    '23514',
    '発送日が注文日より前の場合エラーとなること'
);

-- 正常ケース: 適切なデータでの成功
SELECT lives_ok(
    $test$
        INSERT INTO test_schema.products (product_id, product_name, price, stock_quantity, is_active)
        VALUES (gen_random_uuid(), 'Valid Product', 150.00, 20, true)
    $test$,
    '正常なデータで商品作成が成功すること'
);

SELECT lives_ok(
    $test$
        INSERT INTO test_schema.orders (order_id, user_id, total_amount, status)
        VALUES (
            gen_random_uuid(), 
            '550e8400-e29b-41d4-a716-446655440003', 
            250.00, 
            'pending'
        )
    $test$,
    '正常なデータで注文作成が成功すること'
);

SELECT finish();
ROLLBACK;
```

### 6.3 パフォーマンステスト

#### **クエリパフォーマンステスト**
```sql
-- ✅ Good: 自動化されたパフォーマンステスト

-- パフォーマンステスト結果テーブル
CREATE TABLE IF NOT EXISTS performance_test_results (
    test_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    test_name VARCHAR(200) NOT NULL,
    query_sql TEXT NOT NULL,
    execution_time_ms DECIMAL(10,3) NOT NULL,
    rows_processed INTEGER,
    buffer_hits INTEGER,
    buffer_misses INTEGER,
    temp_files INTEGER,
    temp_bytes BIGINT,
    execution_plan JSONB,
    test_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    passed BOOLEAN NOT NULL,
    threshold_ms DECIMAL(10,3) NOT NULL
);

-- パフォーマンステスト実行関数
CREATE OR REPLACE FUNCTION run_performance_test(
    p_test_name TEXT,
    p_query_sql TEXT,
    p_threshold_ms DECIMAL(10,3) DEFAULT 1000.0,
    p_iterations INTEGER DEFAULT 5
) RETURNS JSONB AS $$
DECLARE
    i INTEGER;
    start_time TIMESTAMP;
    end_time TIMESTAMP;
    execution_time_ms DECIMAL(10,3);
    avg_time DECIMAL(10,3);
    min_time DECIMAL(10,3);
    max_time DECIMAL(10,3);
    times DECIMAL(10,3)[];
    explain_result JSONB;
    test_passed BOOLEAN;
BEGIN
    -- 実行プランを取得
    EXECUTE 'EXPLAIN (ANALYZE, BUFFERS, FORMAT JSON) ' || p_query_sql
    INTO explain_result;
    
    -- 複数回実行して平均を算出
    FOR i IN 1..p_iterations LOOP
        start_time := clock_timestamp();
        EXECUTE p_query_sql;
        end_time := clock_timestamp();
        
        execution_time_ms := EXTRACT(milliseconds FROM end_time - start_time);
        times := array_append(times, execution_time_ms);
    END LOOP;
    
    -- 統計値計算
    SELECT 
        AVG(unnest),
        MIN(unnest),
        MAX(unnest)
    INTO avg_time, min_time, max_time
    FROM unnest(times);
    
    test_passed := avg_time <= p_threshold_ms;
    
    -- 結果を保存
    INSERT INTO performance_test_results (
        test_name,
        query_sql,
        execution_time_ms,
        execution_plan,
        passed,
        threshold_ms
    ) VALUES (
        p_test_name,
        p_query_sql,
        avg_time,
        explain_result,
        test_passed,
        p_threshold_ms
    );
    
    RETURN jsonb_build_object(
        'test_name', p_test_name,
        'avg_time_ms', avg_time,
        'min_time_ms', min_time,
        'max_time_ms', max_time,
        'threshold_ms', p_threshold_ms,
        'passed', test_passed,
        'iterations', p_iterations
    );
END;
$$ LANGUAGE plpgsql;

-- パフォーマンステストスイートの実行
DO $$
DECLARE
    test_result JSONB;
BEGIN
    -- テストデータを生成（必要に応じて）
    PERFORM generate_test_users(10000);
    
    -- テスト 1: ユーザー検索パフォーマンス
    SELECT run_performance_test(
        'User Search by Email',
        'SELECT * FROM users WHERE email_address = ''test1000@example.com''',
        50.0  -- 50ms闾値
    ) INTO test_result;
    
    RAISE NOTICE 'Test Result: %', test_result;
    
    -- テスト 2: 注文履歴検索
    SELECT run_performance_test(
        'Order History Query',
        'SELECT o.*, u.user_name FROM orders o JOIN users u ON o.user_id = u.user_id WHERE o.order_date >= CURRENT_DATE - INTERVAL ''30 days'' ORDER BY o.order_date DESC LIMIT 100',
        200.0  -- 200ms闾値
    ) INTO test_result;
    
    RAISE NOTICE 'Test Result: %', test_result;
    
    -- テスト 3: 集計クエリ
    SELECT run_performance_test(
        'Monthly Sales Aggregation',
        'SELECT DATE_TRUNC(''month'', order_date) as month, COUNT(*) as order_count, SUM(total_amount) as total_sales FROM orders WHERE order_date >= CURRENT_DATE - INTERVAL ''12 months'' GROUP BY DATE_TRUNC(''month'', order_date) ORDER BY month',
        500.0  -- 500ms闾値
    ) INTO test_result;
    
    RAISE NOTICE 'Test Result: %', test_result;
END;
$$;
```

#### **ロードテストシナリオ**
```sql
-- ✅ Good: 同時アクセスロードテスト

CREATE OR REPLACE FUNCTION simulate_concurrent_orders(
    p_concurrent_users INTEGER DEFAULT 50,
    p_orders_per_user INTEGER DEFAULT 10
) RETURNS JSONB AS $$
DECLARE
    start_time TIMESTAMP;
    end_time TIMESTAMP;
    total_orders INTEGER;
    success_count INTEGER := 0;
    error_count INTEGER := 0;
    i INTEGER;
    j INTEGER;
    test_user_id UUID;
    order_result UUID;
BEGIN
    start_time := clock_timestamp();
    total_orders := p_concurrent_users * p_orders_per_user;
    
    -- 同時注文処理のシミュレーション
    FOR i IN 1..p_concurrent_users LOOP
        -- テストユーザーを作成
        INSERT INTO users (user_id, email_address, user_name, status)
        VALUES (
            gen_random_uuid(),
            'loadtest_user_' || i || '@example.com',
            'Load Test User ' || i,
            'active'
        )
        RETURNING user_id INTO test_user_id;
        
        -- 各ユーザーが複数注文を作成
        FOR j IN 1..p_orders_per_user LOOP
            BEGIN
                SELECT create_order_safe(
                    test_user_id,
                    '[{"product_id": "550e8400-e29b-41d4-a716-446655440011", "quantity": 1, "unit_price": 100.00}]'::jsonb
                ) INTO order_result;
                
                success_count := success_count + 1;
                
            EXCEPTION
                WHEN OTHERS THEN
                    error_count := error_count + 1;
            END;
        END LOOP;
    END LOOP;
    
    end_time := clock_timestamp();
    
    -- ロードテスト結果を返す
    RETURN jsonb_build_object(
        'concurrent_users', p_concurrent_users,
        'orders_per_user', p_orders_per_user,
        'total_orders_attempted', total_orders,
        'successful_orders', success_count,
        'failed_orders', error_count,
        'success_rate', ROUND((success_count::DECIMAL / total_orders) * 100, 2),
        'total_time_seconds', EXTRACT(epoch FROM end_time - start_time),
        'orders_per_second', ROUND(success_count / EXTRACT(epoch FROM end_time - start_time), 2)
    );
END;
$$ LANGUAGE plpgsql;
```

**Devin指示**: pgTAPを使用した包括的ユニットテスト、スキーマ検証テスト、自動化パフォーマンステスト、ロードテストシナリオ、テストデータ管理を必ず実装せよ

---

## 7. 運用・監視・メンテナンス

### 7.1 運用監視システムの構築

#### **包括的な監視メトリクスシステム**
```sql
-- ✅ Good: システム監視用メトリクステーブル
CREATE TABLE system_metrics (
    metric_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- メトリクス基本情報
    metric_name VARCHAR(100) NOT NULL,
    metric_category VARCHAR(50) NOT NULL,  -- 'performance', 'capacity', 'availability', 'security'
    metric_value DECIMAL(15,6) NOT NULL,
    metric_unit VARCHAR(20),  -- 'ms', '%', 'MB', 'count', 'rate'
    
    -- 闾値情報
    warning_threshold DECIMAL(15,6),
    critical_threshold DECIMAL(15,6),
    threshold_direction VARCHAR(10) CHECK (threshold_direction IN ('above', 'below')),
    
    -- コンテキスト情報
    database_name VARCHAR(100) DEFAULT current_database(),
    schema_name VARCHAR(100),
    table_name VARCHAR(100),
    index_name VARCHAR(100),
    
    -- タイムスタンプ
    collected_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- 追加メタデータ
    additional_info JSONB
);

-- パーティショニング（日次）
CREATE TABLE system_metrics_partitioned (
    LIKE system_metrics INCLUDING ALL
) PARTITION BY RANGE (collected_at);

-- 監視メトリクス用インデックス
CREATE INDEX idx_metrics_name_time ON system_metrics_partitioned (metric_name, collected_at);
CREATE INDEX idx_metrics_category_time ON system_metrics_partitioned (metric_category, collected_at);
CREATE INDEX idx_metrics_value_threshold ON system_metrics_partitioned (metric_value, warning_threshold, critical_threshold);

-- メトリクス収集関数
CREATE OR REPLACE FUNCTION collect_database_metrics()
RETURNS INTEGER AS $$
DECLARE
    metrics_inserted INTEGER := 0;
    current_time TIMESTAMP WITH TIME ZONE := CURRENT_TIMESTAMP;
BEGIN
    -- 1. データベースサイズメトリクス
    INSERT INTO system_metrics (metric_name, metric_category, metric_value, metric_unit, warning_threshold, critical_threshold, threshold_direction, collected_at)
    SELECT 
        'database_size_mb',
        'capacity',
        pg_database_size(current_database()) / (1024.0 * 1024.0),
        'MB',
        1000.0,  -- 1GB警告
        5000.0,  -- 5GB致命的
        'above',
        current_time;
    
    -- 2. 接続数メトリクス
    INSERT INTO system_metrics (metric_name, metric_category, metric_value, metric_unit, warning_threshold, critical_threshold, threshold_direction, collected_at)
    SELECT 
        'active_connections',
        'performance',
        COUNT(*),
        'count',
        80.0,   -- 80接続警告
        95.0,   -- 95接続致命的
        'above',
        current_time
    FROM pg_stat_activity 
    WHERE state = 'active';
    
    -- 3. ロック待ちメトリクス
    INSERT INTO system_metrics (metric_name, metric_category, metric_value, metric_unit, warning_threshold, critical_threshold, threshold_direction, collected_at)
    SELECT 
        'waiting_locks',
        'performance',
        COUNT(*),
        'count',
        5.0,    -- 5ロック待ち警告
        20.0,   -- 20ロック待ち致命的
        'above',
        current_time
    FROM pg_stat_activity 
    WHERE wait_event_type = 'Lock' AND wait_event IS NOT NULL;
    
    -- 4. デッドロック数
    INSERT INTO system_metrics (metric_name, metric_category, metric_value, metric_unit, warning_threshold, critical_threshold, threshold_direction, collected_at)
    SELECT 
        'deadlocks',
        'performance',
        deadlocks,
        'count',
        1.0,    -- 1デッドロック警告
        5.0,    -- 5デッドロック致命的
        'above',
        current_time
    FROM pg_stat_database 
    WHERE datname = current_database();
    
    -- 5. キャッシュヒット率
    INSERT INTO system_metrics (metric_name, metric_category, metric_value, metric_unit, warning_threshold, critical_threshold, threshold_direction, collected_at)
    SELECT 
        'cache_hit_ratio',
        'performance',
        ROUND(
            100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0), 2
        ),
        '%',
        95.0,   -- 95%以下で警告
        90.0,   -- 90%以下で致命的
        'below',
        current_time
    FROM pg_stat_database 
    WHERE datname = current_database();
    
    -- 6. テーブルサイズメトリクス（TOP 10）
    INSERT INTO system_metrics (metric_name, metric_category, metric_value, metric_unit, warning_threshold, critical_threshold, threshold_direction, table_name, collected_at)
    SELECT 
        'table_size_mb',
        'capacity',
        pg_total_relation_size(schemaname||'.'||tablename) / (1024.0 * 1024.0),
        'MB',
        100.0,  -- 100MB警告
        500.0,  -- 500MB致命的
        'above',
        tablename,
        current_time
    FROM pg_tables 
    WHERE schemaname = 'public'
    ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC
    LIMIT 10;
    
    GET DIAGNOSTICS metrics_inserted = ROW_COUNT;
    RETURN metrics_inserted;
END;
$$ LANGUAGE plpgsql;
```

#### **アラートシステム**
```sql
-- ✅ Good: アラート管理システム

-- アラートルールテーブル
CREATE TABLE alert_rules (
    rule_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_name VARCHAR(200) NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    condition_operator VARCHAR(10) NOT NULL CHECK (condition_operator IN ('>', '<', '>=', '<=', '=', '!=')),
    threshold_value DECIMAL(15,6) NOT NULL,
    severity_level VARCHAR(20) NOT NULL CHECK (severity_level IN ('info', 'warning', 'critical')),
    
    -- アラート設定
    evaluation_window INTERVAL DEFAULT '5 minutes',
    minimum_duration INTERVAL DEFAULT '1 minute',
    
    -- 通知設定
    notification_channels JSONB, -- ['email', 'slack', 'webhook']
    notification_template TEXT,
    
    -- 状態
    is_enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- アラート履歴テーブル
CREATE TABLE alert_history (
    alert_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_id UUID NOT NULL REFERENCES alert_rules(rule_id),
    
    -- アラート情報
    alert_status VARCHAR(20) NOT NULL CHECK (alert_status IN ('firing', 'resolved')),
    metric_value DECIMAL(15,6) NOT NULL,
    threshold_value DECIMAL(15,6) NOT NULL,
    severity_level VARCHAR(20) NOT NULL,
    
    -- コンテキスト
    database_name VARCHAR(100),
    table_name VARCHAR(100),
    additional_context JSONB,
    
    -- タイムスタンプ
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    resolved_at TIMESTAMP WITH TIME ZONE,
    
    -- 通知状態
    notification_sent BOOLEAN DEFAULT FALSE,
    notification_channels_used JSONB
);

-- 標準アラートルールの登録
INSERT INTO alert_rules (rule_name, metric_name, condition_operator, threshold_value, severity_level, notification_channels, notification_template) VALUES
('データベースサイズ警告', 'database_size_mb', '>', 1000.0, 'warning', '["email", "slack"]', 'データベースサイズが{metric_value}MBに達しました。闾値: {threshold_value}MB'),
('データベースサイズ致命的', 'database_size_mb', '>', 5000.0, 'critical', '["email", "slack", "webhook"]', '【緊急】データベースサイズが致命的レベルに達しました: {metric_value}MB'),
('接続数過多', 'active_connections', '>', 80.0, 'warning', '["email"]', 'アクティブ接続数が多いです: {metric_value}接続'),
('キャッシュヒット率低下', 'cache_hit_ratio', '<', 95.0, 'warning', '["email"]', 'キャッシュヒット率が低下しています: {metric_value}%'),
('デッドロック発生', 'deadlocks', '>', 0.0, 'critical', '["email", "slack"]', 'デッドロックが発生しました: {metric_value}件');

-- アラート評価関数
CREATE OR REPLACE FUNCTION evaluate_alerts()
RETURNS INTEGER AS $$
DECLARE
    rule_record RECORD;
    metric_record RECORD;
    alert_fired INTEGER := 0;
    current_time TIMESTAMP WITH TIME ZONE := CURRENT_TIMESTAMP;
BEGIN
    -- 各アラートルールを評価
    FOR rule_record IN 
        SELECT * FROM alert_rules WHERE is_enabled = TRUE
    LOOP
        -- 最新のメトリクス値を取得
        SELECT 
            metric_value,
            collected_at,
            table_name,
            additional_info
        INTO metric_record
        FROM system_metrics
        WHERE metric_name = rule_record.metric_name
          AND collected_at >= current_time - rule_record.evaluation_window
        ORDER BY collected_at DESC
        LIMIT 1;
        
        IF metric_record IS NOT NULL THEN
            -- アラート条件を評価
            IF (
                (rule_record.condition_operator = '>' AND metric_record.metric_value > rule_record.threshold_value) OR
                (rule_record.condition_operator = '<' AND metric_record.metric_value < rule_record.threshold_value) OR
                (rule_record.condition_operator = '>=' AND metric_record.metric_value >= rule_record.threshold_value) OR
                (rule_record.condition_operator = '<=' AND metric_record.metric_value <= rule_record.threshold_value) OR
                (rule_record.condition_operator = '=' AND metric_record.metric_value = rule_record.threshold_value) OR
                (rule_record.condition_operator = '!=' AND metric_record.metric_value != rule_record.threshold_value)
            ) THEN
                -- アラートが既に発生中かチェック
                IF NOT EXISTS (
                    SELECT 1 FROM alert_history 
                    WHERE rule_id = rule_record.rule_id 
                      AND alert_status = 'firing'
                      AND resolved_at IS NULL
                ) THEN
                    -- 新しいアラートを発生
                    INSERT INTO alert_history (
                        rule_id,
                        alert_status,
                        metric_value,
                        threshold_value,
                        severity_level,
                        database_name,
                        table_name,
                        additional_context,
                        started_at
                    ) VALUES (
                        rule_record.rule_id,
                        'firing',
                        metric_record.metric_value,
                        rule_record.threshold_value,
                        rule_record.severity_level,
                        current_database(),
                        metric_record.table_name,
                        metric_record.additional_info,
                        current_time
                    );
                    
                    alert_fired := alert_fired + 1;
                END IF;
            ELSE
                -- アラートを解決
                UPDATE alert_history 
                SET alert_status = 'resolved',
                    resolved_at = current_time
                WHERE rule_id = rule_record.rule_id 
                  AND alert_status = 'firing'
                  AND resolved_at IS NULL;
            END IF;
        END IF;
    END LOOP;
    
    RETURN alert_fired;
END;
$$ LANGUAGE plpgsql;
```

### 7.2 パフォーマンス監視・アラート

#### **スロークエリ監視システム**
```sql
-- ✅ Good: スロークエリの監視と分析

-- スロークエリログテーブル
CREATE TABLE slow_query_log (
    log_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- クエリ情報
    query_text TEXT NOT NULL,
    query_hash VARCHAR(64) NOT NULL,  -- MD5ハッシュ
    
    -- パフォーマンスメトリクス
    execution_time_ms DECIMAL(12,3) NOT NULL,
    planning_time_ms DECIMAL(12,3),
    rows_examined BIGINT,
    rows_returned BIGINT,
    
    -- リソース使用量
    shared_blks_hit BIGINT,
    shared_blks_read BIGINT,
    shared_blks_written BIGINT,
    temp_blks_read BIGINT,
    temp_blks_written BIGINT,
    
    -- コンテキスト
    database_name VARCHAR(100) DEFAULT current_database(),
    user_name VARCHAR(100),
    application_name VARCHAR(100),
    client_addr INET,
    
    -- 実行プラン
    execution_plan JSONB,
    
    -- タイムスタンプ
    executed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- スロークエリ用インデックス
CREATE INDEX idx_slow_query_time ON slow_query_log(execution_time_ms DESC, executed_at);
CREATE INDEX idx_slow_query_hash ON slow_query_log(query_hash, executed_at);
CREATE INDEX idx_slow_query_user ON slow_query_log(user_name, executed_at);

-- スロークエリ検出関数
CREATE OR REPLACE FUNCTION capture_slow_queries(
    p_threshold_ms DECIMAL(12,3) DEFAULT 1000.0
) RETURNS INTEGER AS $$
DECLARE
    slow_query_count INTEGER := 0;
BEGIN
    -- pg_stat_statementsからスロークエリを取得
    INSERT INTO slow_query_log (
        query_text,
        query_hash,
        execution_time_ms,
        rows_examined,
        rows_returned,
        shared_blks_hit,
        shared_blks_read,
        shared_blks_written,
        temp_blks_read,
        temp_blks_written,
        user_name,
        executed_at
    )
    SELECT 
        query,
        md5(query),
        mean_exec_time,
        rows,
        rows,
        shared_blks_hit,
        shared_blks_read,
        shared_blks_written,
        temp_blks_read,
        temp_blks_written,
        'system',
        CURRENT_TIMESTAMP
    FROM pg_stat_statements
    WHERE mean_exec_time > p_threshold_ms
      AND calls > 1  -- 複数回実行されたクエリのみ
      AND NOT EXISTS (
          SELECT 1 FROM slow_query_log 
          WHERE query_hash = md5(pg_stat_statements.query) 
            AND executed_at >= CURRENT_TIMESTAMP - INTERVAL '1 hour'
      );
    
    GET DIAGNOSTICS slow_query_count = ROW_COUNT;
    RETURN slow_query_count;
END;
$$ LANGUAGE plpgsql;

-- スロークエリ分析レポート
CREATE OR REPLACE FUNCTION analyze_slow_queries(
    p_hours_back INTEGER DEFAULT 24
) RETURNS TABLE (
    query_pattern TEXT,
    total_executions BIGINT,
    avg_execution_time_ms DECIMAL(12,3),
    max_execution_time_ms DECIMAL(12,3),
    total_time_spent_ms DECIMAL(12,3),
    percentage_of_total_time DECIMAL(5,2),
    optimization_priority INTEGER
) AS $$
BEGIN
    RETURN QUERY
    WITH query_stats AS (
        SELECT 
            regexp_replace(query_text, '\$\d+|''[^'']*''|\d+', '?', 'g') as normalized_query,
            COUNT(*) as execution_count,
            AVG(execution_time_ms) as avg_time,
            MAX(execution_time_ms) as max_time,
            SUM(execution_time_ms) as total_time
        FROM slow_query_log
        WHERE executed_at >= CURRENT_TIMESTAMP - (p_hours_back || ' hours')::INTERVAL
        GROUP BY regexp_replace(query_text, '\$\d+|''[^'']*''|\d+', '?', 'g')
    ),
    total_time_calc AS (
        SELECT SUM(total_time) as overall_total_time FROM query_stats
    )
    SELECT 
        qs.normalized_query,
        qs.execution_count,
        qs.avg_time,
        qs.max_time,
        qs.total_time,
        ROUND((qs.total_time / ttc.overall_total_time) * 100, 2),
        CASE 
            WHEN qs.total_time > ttc.overall_total_time * 0.1 THEN 1  -- 高優先度
            WHEN qs.total_time > ttc.overall_total_time * 0.05 THEN 2 -- 中優先度
            ELSE 3 -- 低優先度
        END
    FROM query_stats qs
    CROSS JOIN total_time_calc ttc
    ORDER BY qs.total_time DESC;
END;
$$ LANGUAGE plpgsql;
```

#### **リアルタイムパフォーマンスダッシュボード**
```sql
-- ✅ Good: リアルタイム監視ビュー

CREATE OR REPLACE VIEW real_time_performance AS
SELECT 
    -- システム概要
    (
        SELECT COUNT(*) 
        FROM pg_stat_activity 
        WHERE state = 'active'
    ) as active_connections,
    
    (
        SELECT COUNT(*) 
        FROM pg_stat_activity 
        WHERE wait_event_type = 'Lock'
    ) as blocked_connections,
    
    (
        SELECT ROUND(
            100.0 * sum(blks_hit) / NULLIF(sum(blks_hit) + sum(blks_read), 0), 2
        )
        FROM pg_stat_database
    ) as cache_hit_ratio,
    
    -- データベースサイズ
    (
        SELECT ROUND(pg_database_size(current_database()) / (1024.0^3), 2)
    ) as database_size_gb,
    
    -- 最新のスロークエリ
    (
        SELECT execution_time_ms
        FROM slow_query_log
        WHERE executed_at >= CURRENT_TIMESTAMP - INTERVAL '5 minutes'
        ORDER BY execution_time_ms DESC
        LIMIT 1
    ) as slowest_query_5min,
    
    -- デッドロック統計
    (
        SELECT deadlocks
        FROM pg_stat_database
        WHERE datname = current_database()
    ) as total_deadlocks,
    
    -- テンポラリファイル使用量
    (
        SELECT COALESCE(SUM(temp_bytes), 0) / (1024.0^2)
        FROM pg_stat_database
        WHERE datname = current_database()
    ) as temp_files_mb,
    
    -- 更新時刻
    CURRENT_TIMESTAMP as last_updated;

-- テーブルアクティビティ監視ビュー
CREATE OR REPLACE VIEW table_activity_summary AS
SELECT 
    schemaname,
    tablename,
    seq_scan,
    seq_tup_read,
    idx_scan,
    idx_tup_fetch,
    n_tup_ins as inserts,
    n_tup_upd as updates,
    n_tup_del as deletes,
    n_live_tup as live_tuples,
    n_dead_tup as dead_tuples,
    ROUND(
        100.0 * n_dead_tup / NULLIF(n_live_tup + n_dead_tup, 0), 2
    ) as dead_tuple_ratio,
    last_vacuum,
    last_autovacuum,
    last_analyze,
    last_autoanalyze
FROM pg_stat_user_tables
ORDER BY (n_tup_ins + n_tup_upd + n_tup_del) DESC;

-- インデックス使用率監視ビュー
CREATE OR REPLACE VIEW index_usage_summary AS
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan as scans,
    idx_tup_read as tuples_read,
    idx_tup_fetch as tuples_fetched,
    CASE 
        WHEN idx_scan = 0 THEN 'UNUSED'
        WHEN idx_scan < 100 THEN 'LOW_USAGE'
        WHEN idx_scan < 1000 THEN 'MEDIUM_USAGE'
        ELSE 'HIGH_USAGE'
    END as usage_category,
    pg_size_pretty(pg_relation_size(i.indexrelid)) as index_size
FROM pg_stat_user_indexes ui
JOIN pg_index i ON ui.indexrelid = i.indexrelid
WHERE NOT i.indisunique  -- ユニークインデックスを除外
ORDER BY idx_scan ASC;
```

### 7.3 バックアップ・復旧戦略

#### **包括的バックアップシステム**
```sql
-- ✅ Good: バックアップ管理システム

-- バックアップ記録テーブル
CREATE TABLE backup_history (
    backup_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- バックアップ情報
    backup_type VARCHAR(20) NOT NULL CHECK (backup_type IN ('full', 'incremental', 'differential')),
    backup_method VARCHAR(20) NOT NULL CHECK (backup_method IN ('pg_dump', 'pg_basebackup', 'wal_archive')),
    database_name VARCHAR(100) NOT NULL,
    
    -- ファイル情報
    backup_file_path TEXT NOT NULL,
    backup_file_size BIGINT NOT NULL,
    compression_type VARCHAR(20),
    checksum VARCHAR(64),
    
    -- タイミング
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    duration_seconds INTEGER,
    
    -- 状態
    status VARCHAR(20) NOT NULL CHECK (status IN ('running', 'completed', 'failed')),
    error_message TEXT,
    
    -- 保持期限
    retention_until TIMESTAMP WITH TIME ZONE,
    
    -- メタデータ
    wal_start_lsn TEXT,
    wal_end_lsn TEXT,
    created_by VARCHAR(100) DEFAULT current_user
);

-- バックアップ用インデックス
CREATE INDEX idx_backup_type_date ON backup_history(backup_type, started_at);
CREATE INDEX idx_backup_status ON backup_history(status, started_at);
CREATE INDEX idx_backup_retention ON backup_history(retention_until) WHERE status = 'completed';

-- バックアップ実行関数
CREATE OR REPLACE FUNCTION create_database_backup(
    p_backup_type VARCHAR(20) DEFAULT 'full',
    p_compression BOOLEAN DEFAULT TRUE,
    p_backup_path TEXT DEFAULT '/backup/postgresql/'
) RETURNS UUID AS $$
DECLARE
    backup_uuid UUID;
    backup_filename TEXT;
    backup_command TEXT;
    start_time TIMESTAMP WITH TIME ZONE;
    end_time TIMESTAMP WITH TIME ZONE;
    file_size BIGINT;
    retention_date TIMESTAMP WITH TIME ZONE;
BEGIN
    backup_uuid := gen_random_uuid();
    start_time := CURRENT_TIMESTAMP;
    
    -- ファイル名生成
    backup_filename := format(
        '%s_%s_%s_%s.%s',
        current_database(),
        p_backup_type,
        to_char(start_time, 'YYYYMMDD_HH24MISS'),
        backup_uuid,
        CASE WHEN p_compression THEN 'gz' ELSE 'sql' END
    );
    
    -- 保持期限設定（フルバックアップ: 30日、増分: 7日）
    retention_date := CASE 
        WHEN p_backup_type = 'full' THEN start_time + INTERVAL '30 days'
        ELSE start_time + INTERVAL '7 days'
    END;
    
    -- バックアップ記録を登録
    INSERT INTO backup_history (
        backup_id,
        backup_type,
        backup_method,
        database_name,
        backup_file_path,
        backup_file_size,
        started_at,
        status,
        retention_until
    ) VALUES (
        backup_uuid,
        p_backup_type,
        'pg_dump',
        current_database(),
        p_backup_path || backup_filename,
        0,  -- 完了後に更新
        start_time,
        'running',
        retention_date
    );
    
    -- バックアップコマンド生成（PostgreSQL）
    backup_command := format(
        'pg_dump %s %s --file=%s --verbose --no-password',
        CASE WHEN p_compression THEN '--compress=9' ELSE '' END,
        current_database(),
        p_backup_path || backup_filename
    );
    
    -- 注意: 実際の環境では、ここで外部コマンドを実行
    -- この例ではシミュレート
    PERFORM pg_sleep(2);  -- バックアップ処理のシミュレート
    
    end_time := CURRENT_TIMESTAMP;
    file_size := 1024 * 1024 * 100;  -- シミュレート: 100MB
    
    -- バックアップ完了情報を更新
    UPDATE backup_history 
    SET 
        completed_at = end_time,
        duration_seconds = EXTRACT(epoch FROM end_time - start_time),
        backup_file_size = file_size,
        status = 'completed',
        checksum = md5(backup_filename || file_size::TEXT)
    WHERE backup_id = backup_uuid;
    
    RETURN backup_uuid;
END;
$$ LANGUAGE plpgsql;

-- 古いバックアップファイルのクリーンアップ
CREATE OR REPLACE FUNCTION cleanup_old_backups()
RETURNS INTEGER AS $$
DECLARE
    expired_count INTEGER := 0;
    backup_record RECORD;
BEGIN
    -- 期限切れバックアップを特定
    FOR backup_record IN 
        SELECT backup_id, backup_file_path
        FROM backup_history
        WHERE retention_until < CURRENT_TIMESTAMP
          AND status = 'completed'
    LOOP
        -- ファイル削除（実際の環境ではシステムコマンドを使用）
        -- PERFORM pg_system('rm -f ' || backup_record.backup_file_path);
        
        -- バックアップ記録を削除
        DELETE FROM backup_history WHERE backup_id = backup_record.backup_id;
        expired_count := expired_count + 1;
    END LOOP;
    
    RETURN expired_count;
END;
$$ LANGUAGE plpgsql;
```

#### **ポイントインタイムリカバリ（PITR）システム**
```sql
-- ✅ Good: WALアーカイブ管理

CREATE TABLE wal_archive_status (
    archive_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wal_filename VARCHAR(100) NOT NULL,
    wal_lsn TEXT NOT NULL,
    archive_path TEXT NOT NULL,
    file_size BIGINT,
    archived_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) CHECK (status IN ('archived', 'failed', 'expired')),
    checksum VARCHAR(64),
    retention_until TIMESTAMP WITH TIME ZONE
);

-- PITR復旧ポイント管理
CREATE TABLE recovery_points (
    recovery_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recovery_name VARCHAR(200) NOT NULL,
    target_lsn TEXT NOT NULL,
    target_time TIMESTAMP WITH TIME ZONE NOT NULL,
    base_backup_id UUID REFERENCES backup_history(backup_id),
    required_wal_files TEXT[],
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    description TEXT
);

-- 復旧ポイント作成関数
CREATE OR REPLACE FUNCTION create_recovery_point(
    p_recovery_name TEXT,
    p_description TEXT DEFAULT NULL
) RETURNS UUID AS $$
DECLARE
    recovery_uuid UUID;
    current_lsn TEXT;
    latest_backup_id UUID;
BEGIN
    recovery_uuid := gen_random_uuid();
    
    -- 現在のLSNを取得
    SELECT pg_current_wal_lsn() INTO current_lsn;
    
    -- 最新のフルバックアップを取得
    SELECT backup_id INTO latest_backup_id
    FROM backup_history
    WHERE backup_type = 'full' 
      AND status = 'completed'
    ORDER BY completed_at DESC
    LIMIT 1;
    
    -- 復旧ポイントを登録
    INSERT INTO recovery_points (
        recovery_id,
        recovery_name,
        target_lsn,
        target_time,
        base_backup_id,
        description
    ) VALUES (
        recovery_uuid,
        p_recovery_name,
        current_lsn,
        CURRENT_TIMESTAMP,
        latest_backup_id,
        p_description
    );
    
    RETURN recovery_uuid;
END;
$$ LANGUAGE plpgsql;
```

**Devin指示**: 包括的な監視メトリクスシステム、アラート管理、スロークエリ監視、自動バックアップシステム、PITR復旧機能、リアルタイムダッシュボードを必ず実装せよ

---

## 8. ドキュメント・ナレッジ管理

### 8.1 データベース設計書の自動生成

#### **スキーマドキュメント自動生成システム**
```sql
-- ✅ Good: スキーマ情報の包括的なドキュメント化

-- ドキュメントメタデータテーブル
CREATE TABLE documentation_metadata (
    doc_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    doc_type VARCHAR(50) NOT NULL CHECK (doc_type IN ('schema', 'api', 'procedure', 'guide')),
    doc_title VARCHAR(200) NOT NULL,
    doc_version VARCHAR(20) NOT NULL,
    doc_status VARCHAR(20) DEFAULT 'draft' CHECK (doc_status IN ('draft', 'review', 'approved', 'deprecated')),
    
    -- コンテンツ
    doc_content TEXT NOT NULL,
    doc_format VARCHAR(20) DEFAULT 'markdown' CHECK (doc_format IN ('markdown', 'html', 'json')),
    
    -- 関連オブジェクト
    related_schema VARCHAR(100),
    related_table VARCHAR(100),
    related_function VARCHAR(100),
    
    -- 管理情報
    created_by VARCHAR(100) DEFAULT current_user,
    reviewed_by VARCHAR(100),
    approved_by VARCHAR(100),
    
    -- タイムスタンプ
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP WITH TIME ZONE,
    approved_at TIMESTAMP WITH TIME ZONE,
    
    -- メタデータ
    tags TEXT[],
    search_keywords TEXT,
    
    UNIQUE(doc_type, doc_title, doc_version)
);

-- ドキュメント用インデックス
CREATE INDEX idx_docs_type_status ON documentation_metadata(doc_type, doc_status);
CREATE INDEX idx_docs_schema_table ON documentation_metadata(related_schema, related_table);
CREATE INDEX idx_docs_search ON documentation_metadata USING GIN(to_tsvector('english', search_keywords));

-- スキーマドキュメント自動生成関数
CREATE OR REPLACE FUNCTION generate_schema_documentation(
    p_schema_name TEXT DEFAULT 'public',
    p_include_system_objects BOOLEAN DEFAULT FALSE
) RETURNS UUID AS $$
DECLARE
    doc_uuid UUID;
    doc_content TEXT := '';
    table_info RECORD;
    column_info RECORD;
    constraint_info RECORD;
    index_info RECORD;
BEGIN
    doc_uuid := gen_random_uuid();
    
    -- ドキュメントヘッダー
    doc_content := format('
# データベーススキーマドキュメント

**スキーマ**: %s  
**データベース**: %s  
**生成日時**: %s  
**バージョン**: %s

## 概要

このドキュメントは%sスキーマの全テーブル、カラム、制約、インデックスの詳細情報を含みます。

## テーブル一覧

',
        p_schema_name,
        current_database(),
        CURRENT_TIMESTAMP,
        '1.0.0',
        p_schema_name
    );
    
    -- テーブル情報を収集
    FOR table_info IN
        SELECT 
            t.table_name,
            t.table_type,
            obj_description(c.oid) as table_comment,
            pg_size_pretty(pg_total_relation_size(c.oid)) as table_size
        FROM information_schema.tables t
        LEFT JOIN pg_class c ON c.relname = t.table_name
        LEFT JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE t.table_schema = p_schema_name
          AND (p_include_system_objects OR t.table_type = 'BASE TABLE')
          AND n.nspname = p_schema_name
        ORDER BY t.table_name
    LOOP
        -- テーブルセクション
        doc_content := doc_content || format('
### %s

**テーブルタイプ**: %s  
**サイズ**: %s  
**説明**: %s

#### カラム情報

| カラム名 | データ型 | NULL許可 | デフォルト値 | 説明 |
|---------|-------|----------|------------|------|
',
            table_info.table_name,
            table_info.table_type,
            COALESCE(table_info.table_size, 'N/A'),
            COALESCE(table_info.table_comment, '説明なし')
        );
        
        -- カラム情報を収集
        FOR column_info IN
            SELECT 
                c.column_name,
                c.data_type,
                c.is_nullable,
                c.column_default,
                col_description(pgc.oid, c.ordinal_position) as column_comment
            FROM information_schema.columns c
            LEFT JOIN pg_class pgc ON pgc.relname = c.table_name
            LEFT JOIN pg_namespace n ON n.oid = pgc.relnamespace
            WHERE c.table_schema = p_schema_name
              AND c.table_name = table_info.table_name
              AND n.nspname = p_schema_name
            ORDER BY c.ordinal_position
        LOOP
            doc_content := doc_content || format('| %s | %s | %s | %s | %s |
',
                column_info.column_name,
                column_info.data_type,
                column_info.is_nullable,
                COALESCE(column_info.column_default, '-'),
                COALESCE(column_info.column_comment, '-')
            );
        END LOOP;
        
        -- 制約情報
        doc_content := doc_content || '
#### 制約情報

';
        
        FOR constraint_info IN
            SELECT 
                tc.constraint_name,
                tc.constraint_type,
                string_agg(kcu.column_name, ', ') as columns,
                rc.unique_constraint_name as referenced_constraint
            FROM information_schema.table_constraints tc
            LEFT JOIN information_schema.key_column_usage kcu 
                ON tc.constraint_name = kcu.constraint_name
            LEFT JOIN information_schema.referential_constraints rc 
                ON tc.constraint_name = rc.constraint_name
            WHERE tc.table_schema = p_schema_name
              AND tc.table_name = table_info.table_name
            GROUP BY tc.constraint_name, tc.constraint_type, rc.unique_constraint_name
            ORDER BY tc.constraint_type, tc.constraint_name
        LOOP
            doc_content := doc_content || format('- **%s** (%s): %s
',
                constraint_info.constraint_name,
                constraint_info.constraint_type,
                constraint_info.columns
            );
        END LOOP;
        
        -- インデックス情報
        doc_content := doc_content || '
#### インデックス情報

';
        
        FOR index_info IN
            SELECT 
                i.indexname,
                i.indexdef,
                pg_size_pretty(pg_relation_size(i.indexname::regclass)) as index_size
            FROM pg_indexes i
            WHERE i.schemaname = p_schema_name
              AND i.tablename = table_info.table_name
            ORDER BY i.indexname
        LOOP
            doc_content := doc_content || format('- **%s** (%s): `%s`
',
                index_info.indexname,
                index_info.index_size,
                index_info.indexdef
            );
        END LOOP;
        
        doc_content := doc_content || '
---
';
    END LOOP;
    
    -- ドキュメントを保存
    INSERT INTO documentation_metadata (
        doc_id,
        doc_type,
        doc_title,
        doc_version,
        doc_content,
        related_schema,
        search_keywords
    ) VALUES (
        doc_uuid,
        'schema',
        format('%s Schema Documentation', p_schema_name),
        '1.0.0',
        doc_content,
        p_schema_name,
        format('schema %s database documentation tables columns constraints indexes', p_schema_name)
    );
    
    RETURN doc_uuid;
END;
$$ LANGUAGE plpgsql;
```

#### **APIドキュメント自動生成**
```sql
-- ✅ Good: ストアドプロシージャのAPIドキュメント自動生成

CREATE OR REPLACE FUNCTION generate_api_documentation(
    p_schema_name TEXT DEFAULT 'public',
    p_function_pattern TEXT DEFAULT '%'
) RETURNS UUID AS $$
DECLARE
    doc_uuid UUID;
    doc_content TEXT := '';
    func_info RECORD;
    param_info RECORD;
BEGIN
    doc_uuid := gen_random_uuid();
    
    -- APIドキュメントヘッダー
    doc_content := format('
# APIドキュメント

**スキーマ**: %s  
**生成日時**: %s  
**バージョン**: 1.0.0

## 概要

このドキュメントは%sスキーマの全てのストアドプロシージャと関数のAPI仕様を説明します。

## 関数一覧

',
        p_schema_name,
        CURRENT_TIMESTAMP,
        p_schema_name
    );
    
    -- 関数情報を収集
    FOR func_info IN
        SELECT 
            p.proname as function_name,
            pg_get_function_arguments(p.oid) as arguments,
            pg_get_function_result(p.oid) as return_type,
            p.prosrc as source_code,
            obj_description(p.oid) as function_comment
        FROM pg_proc p
        JOIN pg_namespace n ON p.pronamespace = n.oid
        WHERE n.nspname = p_schema_name
          AND p.proname LIKE p_function_pattern
          AND p.prokind IN ('f', 'p')  -- 関数とプロシージャ
        ORDER BY p.proname
    LOOP
        doc_content := doc_content || format('
### %s

**種別**: %s  
**引数**: %s  
**戻り値**: %s  
**説明**: %s

#### 使用例

```sql
SELECT %s(%s);
```

#### ソースコード

```sql
%s
```

---

',
            func_info.function_name,
            CASE WHEN func_info.return_type = 'void' THEN 'Procedure' ELSE 'Function' END,
            COALESCE(func_info.arguments, '引数なし'),
            func_info.return_type,
            COALESCE(func_info.function_comment, '説明なし'),
            func_info.function_name,
            COALESCE(func_info.arguments, ''),
            func_info.source_code
        );
    END LOOP;
    
    -- APIドキュメントを保存
    INSERT INTO documentation_metadata (
        doc_id,
        doc_type,
        doc_title,
        doc_version,
        doc_content,
        related_schema,
        search_keywords
    ) VALUES (
        doc_uuid,
        'api',
        format('%s API Documentation', p_schema_name),
        '1.0.0',
        doc_content,
        p_schema_name,
        format('api %s functions procedures arguments return types', p_schema_name)
    );
    
    RETURN doc_uuid;
END;
$$ LANGUAGE plpgsql;
```

### 8.2 運用手順書・ナレッジベース管理

#### **トラブルシューティングガイド管理**
```sql
-- ✅ Good: トラブルシューティングナレッジベース

CREATE TABLE troubleshooting_guides (
    guide_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- 問題情報
    problem_title VARCHAR(200) NOT NULL,
    problem_category VARCHAR(50) NOT NULL,  -- 'performance', 'connection', 'data_integrity', 'security'
    severity_level INTEGER CHECK (severity_level BETWEEN 1 AND 5),
    
    -- 症状と原因
    symptoms TEXT NOT NULL,
    root_causes TEXT NOT NULL,
    
    -- 解決手順
    solution_steps TEXT NOT NULL,
    prevention_measures TEXT,
    
    -- 関連情報
    related_error_codes TEXT[],
    related_tables TEXT[],
    related_functions TEXT[],
    
    -- 検索用
    search_keywords TEXT NOT NULL,
    tags TEXT[],
    
    -- 管理情報
    created_by VARCHAR(100) DEFAULT current_user,
    reviewed_by VARCHAR(100),
    last_used_at TIMESTAMP WITH TIME ZONE,
    usage_count INTEGER DEFAULT 0,
    
    -- タイムスタンプ
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- トラブルシューティング用インデックス
CREATE INDEX idx_troubleshooting_category ON troubleshooting_guides(problem_category, severity_level);
CREATE INDEX idx_troubleshooting_search ON troubleshooting_guides USING GIN(to_tsvector('english', search_keywords));
CREATE INDEX idx_troubleshooting_tags ON troubleshooting_guides USING GIN(tags);

-- 標準トラブルシューティングガイドを登録
INSERT INTO troubleshooting_guides (
    problem_title, problem_category, severity_level, symptoms, root_causes, solution_steps, prevention_measures, 
    related_error_codes, search_keywords, tags
) VALUES 
(
    'スロークエリの特定と最適化',
    'performance',
    3,
    '- アプリケーションの応答時間が遅い
- データベースCPU使用率が高い
- ユーザーからのパフォーマンス苦情',
    '非効率なクエリ、不適切なインデックス、全テーブルスキャン',
    '1. pg_stat_statementsでスロークエリを特定
2. EXPLAIN ANALYZEで実行プランを分析
3. 不足しているインデックスを作成
4. クエリのリファクタリング
5. パフォーマンステストの実行',
    '定期的なパフォーマンスモニタリング、クエリレビュープロセスの導入',
    ARRAY['slow_query', 'performance'],
    'slow query performance optimization index explain analyze',
    ARRAY['performance', 'optimization', 'index', 'query']
),
(
    'デッドロックの解決と予防',
    'performance',
    4,
    '- アプリケーションがハングする
- デッドロックエラーメッセージ
- トランザクションのタイムアウト',
    '複数トランザクション間でのリソースの相互依存、ロックの取得順序の不一致',
    '1. pg_stat_activityでロック待ちプロセスを特定
2. pg_locksでロック競合を分析
3. 必要に応じてプロセスを終了
4. アプリケーションコードのロック順序を統一
5. デッドロックタイムアウトの設定',
    'トランザクションの粒度を細かく、リトライメカニズムの実装',
    ARRAY['deadlock_detected', '40P01'],
    'deadlock transaction lock timeout retry mechanism',
    ARRAY['deadlock', 'transaction', 'lock', 'concurrency']
),
(
    'データベース接続エラーの解決',
    'connection',
    5,
    '- アプリケーションがデータベースに接続できない
- "connection refused"エラー
- 接続タイムアウト',
    'データベースサーバーの停止、ネットワーク問題、接続数上限達成',
    '1. データベースサービスの状態確認
2. ネットワーク接続性のテスト
3. 現在の接続数を確認
4. postgresql.confのmax_connections設定を確認
5. コネクションプールの設定を確認',
    '接続監視、コネクションプールの導入、ヘルスチェックの実装',
    ARRAY['connection_refused', '08006'],
    'connection refused network timeout max_connections pool',
    ARRAY['connection', 'network', 'timeout', 'pool']
);

-- トラブルシューティング検索関数
CREATE OR REPLACE FUNCTION search_troubleshooting_guides(
    p_search_text TEXT,
    p_category TEXT DEFAULT NULL,
    p_severity_min INTEGER DEFAULT 1
) RETURNS TABLE (
    guide_id UUID,
    problem_title TEXT,
    problem_category TEXT,
    severity_level INTEGER,
    symptoms TEXT,
    solution_steps TEXT,
    usage_count INTEGER,
    relevance_score REAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        tg.guide_id,
        tg.problem_title,
        tg.problem_category,
        tg.severity_level,
        tg.symptoms,
        tg.solution_steps,
        tg.usage_count,
        ts_rank(to_tsvector('english', tg.search_keywords), plainto_tsquery('english', p_search_text)) as relevance
    FROM troubleshooting_guides tg
    WHERE 
        to_tsvector('english', tg.search_keywords) @@ plainto_tsquery('english', p_search_text)
        AND (p_category IS NULL OR tg.problem_category = p_category)
        AND tg.severity_level >= p_severity_min
    ORDER BY relevance DESC, tg.usage_count DESC, tg.severity_level DESC;
END;
$$ LANGUAGE plpgsql;
```

#### **FAQ・ナレッジ管理システム**
```sql
-- ✅ Good: 組織ナレッジの体系的管理

CREATE TABLE knowledge_base (
    kb_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- コンテンツ情報
    title VARCHAR(300) NOT NULL,
    content_type VARCHAR(50) DEFAULT 'faq' CHECK (content_type IN ('faq', 'howto', 'best_practice', 'case_study')),
    content TEXT NOT NULL,
    summary TEXT,
    
    -- カテゴリ化
    category VARCHAR(100) NOT NULL,
    subcategory VARCHAR(100),
    difficulty_level INTEGER CHECK (difficulty_level BETWEEN 1 AND 5),
    
    -- メタデータ
    tags TEXT[],
    related_tables TEXT[],
    related_functions TEXT[],
    
    -- 品質管理
    status VARCHAR(20) DEFAULT 'draft' CHECK (status IN ('draft', 'review', 'published', 'deprecated')),
    accuracy_rating DECIMAL(3,2) CHECK (accuracy_rating BETWEEN 0.0 AND 5.0),
    usefulness_rating DECIMAL(3,2) CHECK (usefulness_rating BETWEEN 0.0 AND 5.0),
    
    -- 使用統計
    view_count INTEGER DEFAULT 0,
    helpful_votes INTEGER DEFAULT 0,
    unhelpful_votes INTEGER DEFAULT 0,
    last_accessed_at TIMESTAMP WITH TIME ZONE,
    
    -- 管理情報
    created_by VARCHAR(100) DEFAULT current_user,
    reviewed_by VARCHAR(100),
    approved_by VARCHAR(100),
    
    -- タイムスタンプ
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP WITH TIME ZONE
);

-- ナレッジベース用インデックス
CREATE INDEX idx_kb_category_status ON knowledge_base(category, status);
CREATE INDEX idx_kb_content_search ON knowledge_base USING GIN(to_tsvector('english', title || ' ' || content));
CREATE INDEX idx_kb_tags ON knowledge_base USING GIN(tags);
CREATE INDEX idx_kb_rating ON knowledge_base(accuracy_rating DESC, usefulness_rating DESC);

-- サンプルナレッジを登録
INSERT INTO knowledge_base (
    title, content_type, content, category, tags, status, accuracy_rating, usefulness_rating
) VALUES 
(
    'データベースパフォーマンス最適化のベストプラクティス',
    'best_practice',
    '# データベースパフォーマンス最適化

## 1. インデックスの最適化
- クエリパターンに基づいたインデックス設計
- 使用されていないインデックスの定期的なクリーンアップ

## 2. クエリの最適化
- SELECT文で必要なカラムのみ指定
- WHERE句での適切なフィルタリング',
    'performance',
    ARRAY['performance', 'optimization', 'index', 'query'],
    'published',
    4.5,
    4.8
),
(
    'バックアップの自動化設定方法',
    'howto',
    '# バックアップ自動化の設定

## 前提条件
- PostgreSQL 12+
- 十分なストレージ容量

## 設定手順
1. pg_basebackupの設定
2. WALアーカイブの構成
3. cronジョブの設定',
    'backup',
    ARRAY['backup', 'automation', 'pg_basebackup', 'wal'],
    'published',
    4.2,
    4.5
);

-- ナレッジ検索関数
CREATE OR REPLACE FUNCTION search_knowledge_base(
    p_search_text TEXT,
    p_category TEXT DEFAULT NULL,
    p_content_type TEXT DEFAULT NULL,
    p_limit INTEGER DEFAULT 10
) RETURNS TABLE (
    kb_id UUID,
    title TEXT,
    content_type TEXT,
    category TEXT,
    summary TEXT,
    accuracy_rating DECIMAL(3,2),
    usefulness_rating DECIMAL(3,2),
    view_count INTEGER,
    relevance_score REAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        kb.kb_id,
        kb.title,
        kb.content_type,
        kb.category,
        kb.summary,
        kb.accuracy_rating,
        kb.usefulness_rating,
        kb.view_count,
        ts_rank(
            to_tsvector('english', kb.title || ' ' || kb.content), 
            plainto_tsquery('english', p_search_text)
        ) as relevance
    FROM knowledge_base kb
    WHERE 
        kb.status = 'published'
        AND to_tsvector('english', kb.title || ' ' || kb.content) @@ plainto_tsquery('english', p_search_text)
        AND (p_category IS NULL OR kb.category = p_category)
        AND (p_content_type IS NULL OR kb.content_type = p_content_type)
    ORDER BY relevance DESC, kb.accuracy_rating DESC, kb.view_count DESC
    LIMIT p_limit;
END;
$$ LANGUAGE plpgsql;
```

### 8.3 継続的ドキュメント更新の自動化

#### **ドキュメント状態監視システム**
```sql
-- ✅ Good: ドキュメントの鮮度管理と自動更新

CREATE OR REPLACE FUNCTION update_documentation_status()
RETURNS INTEGER AS $$
DECLARE
    updated_docs INTEGER := 0;
    doc_record RECORD;
    schema_changed BOOLEAN;
BEGIN
    -- スキーマドキュメントの更新チェック
    FOR doc_record IN 
        SELECT doc_id, related_schema, updated_at
        FROM documentation_metadata 
        WHERE doc_type = 'schema' 
          AND doc_status = 'approved'
    LOOP
        -- スキーマの変更をチェック
        SELECT EXISTS (
            SELECT 1 FROM information_schema.tables 
            WHERE table_schema = doc_record.related_schema 
              AND (
                  SELECT MAX(GREATEST(
                      COALESCE(last_altered, created), 
                      created
                  ))
                  FROM information_schema.tables t2
                  WHERE t2.table_schema = doc_record.related_schema
              ) > doc_record.updated_at
        ) INTO schema_changed;
        
        IF schema_changed THEN
            -- ドキュメントを古い状態にマーク
            UPDATE documentation_metadata 
            SET doc_status = 'review',
                updated_at = CURRENT_TIMESTAMP
            WHERE doc_id = doc_record.doc_id;
            
            -- 新しいドキュメントを自動生成
            PERFORM generate_schema_documentation(doc_record.related_schema);
            
            updated_docs := updated_docs + 1;
        END IF;
    END LOOP;
    
    RETURN updated_docs;
END;
$$ LANGUAGE plpgsql;

-- 定期的なドキュメント更新ジョブの設定
CREATE OR REPLACE FUNCTION schedule_documentation_updates()
RETURNS VOID AS $$
BEGIN
    -- 毎日午前2時にドキュメント状態をチェック
    -- 実際の環境ではpg_cron拡張を使用
    
    PERFORM cron.schedule(
        'update-documentation', 
        '0 2 * * *',  -- 毎日午前2時
        'SELECT update_documentation_status();'
    );
    
    -- 毎週月曜日に全ドキュメントを再生成
    PERFORM cron.schedule(
        'regenerate-all-docs',
        '0 1 * * 1',  -- 毎週月曜日午前1時
        'SELECT generate_schema_documentation(); SELECT generate_api_documentation();'
    );
END;
$$ LANGUAGE plpgsql;
```

### 8.4 Devin実行ガイドライン総括

#### **包括的な実装チェックリスト**

**フェーズ1: 基盤構築**
- [ ] `00-general-principles.md`の理解と適用
- [ ] データベース設計原則の実装（第2章）
- [ ] スキーマ、テーブル、制約の作成
- [ ] 基本的なインデックスの設定
- [ ] エラーコード体系の確立（第5章）

**フェーズ2: 機能実装**
- [ ] ビジネスロジック関数の実装
- [ ] セキュリティ機能の組み込み（第4章）
- [ ] パフォーマンス最適化の適用（第3章）
- [ ] テストケースの作成と実行（第6章）
- [ ] エラーハンドリングの実装（第5章）

**フェーズ3: 運用準備**
- [ ] 監視システムの構築（第7章）
- [ ] バックアップシステムの設定（第7章）
- [ ] ドキュメントの自動生成（第8章）
- [ ] アラートシステムの設定（第7章）
- [ ] パフォーマンスメトリクスの定義（第7章）

**フェーズ4: 品質保証**
- [ ] 全テストスイートの実行（第6章）
- [ ] パフォーマンステストの実行（第6章）
- [ ] セキュリティテストの実行（第4章）
- [ ] ドキュメントのレビューと承認（第8章）
- [ ] 運用監視ダッシュボードの設定（第7章）

**フェーズ5: 継続的改善**
- [ ] 監視メトリクスの分析と最適化
- [ ] ドキュメントの定期更新
- [ ] ナレッジベースの充実
- [ ] チームフィードバックの収集と反映
- [ ] 新しいベストプラクティスの組み込み

#### **最終確認項目**

1. **コード品質**: 全章のSSQLでシンタックスエラーがないこと
2. **パフォーマンス**: 全クエリが闾値内で実行されること
3. **セキュリティ**: 全セキュリティチェックがパスすること
4. **テスト**: 全テストケースが成功すること
5. **ドキュメント**: 全ドキュメントが最新状態であること
6. **監視**: 全メトリクスが正常範囲内であること
7. **バックアップ**: 復旧テストが成功すること
8. **運用**: 本番環境での安定稼働が確認されること

**Devin最終指示**: このSQLコーディング規約の全章（1-8章）を段階的に実装し、各フェーズで必ず品質確認を実施せよ。特にセキュリティ、パフォーマンス、信頼性に関しては妥協せず、企業レベルの品質を必ず実現せよ。

---

## 総括

このSQLコーディング規約は、自律型AI Devinが高品質で一貫性のあるデータベースシステムを構築するための包括的なガイドラインです。基本構文から高度な運用技術まで、8章にわたって体系的に組織されています。

本規約の特徴は、単なるコーディングスタイルにとどまらず、セキュリティ、パフォーマンス、信頼性、保守性の全てを網羅した、真に実用的なガイドラインであることです。これにより、Devinは企業レベルの要求を満たす、スケーラブルで持続可能なデータベースソリューションを一貫して構築できるようになります。

**最終更新日**: 2024-10-09  
**バージョン**: 1.0.0  
**ステータス**: 完成