# 物理DB設計書

**プロジェクト名:** ECサイト構築プロジェクト  
**ドキュメントID:** PHY-DB-001  
**バージョン:** 1.0  
**作成日:** 2025-10-30

---

## 1. 基本情報

### 1.1 目的

論理DB設計を物理環境に実装するための詳細仕様を定義する。

### 1.2 対象範囲

- DDL（データ定義言語）
- インデックス詳細設計
- パーティショニング戦略
- レプリケーション設計
- バックアップ・リカバリ方針

---

## 2. DDL定義

### 2.1 users（ユーザー）

```sql
-- テーブル作成
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT chk_users_role CHECK (role IN ('CUSTOMER', 'ADMIN')),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DELETED')),
    CONSTRAINT chk_users_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$')
);

-- UNIQUE インデックス
CREATE UNIQUE INDEX idx_users_email ON users(email);

-- 検索用インデックス
CREATE INDEX idx_users_status ON users(status) WHERE status = 'ACTIVE';
CREATE INDEX idx_users_created_at ON users(created_at DESC);

-- 統計情報更新トリガー
CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- コメント
COMMENT ON TABLE users IS 'ユーザー情報';
COMMENT ON COLUMN users.email IS 'メールアドレス（ログインID）';
COMMENT ON COLUMN users.password IS 'パスワードハッシュ（bcrypt）';
COMMENT ON COLUMN users.version IS '楽観的ロック用バージョン';
```

---

### 2.2 products（商品）

```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    cost DECIMAL(10,2),
    stock_quantity INT NOT NULL DEFAULT 0,
    sku VARCHAR(50) NOT NULL,
    weight DECIMAL(8,2),
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_products_category_id FOREIGN KEY (category_id) 
        REFERENCES categories(id),
    CONSTRAINT chk_products_price CHECK (price >= 0),
    CONSTRAINT chk_products_stock_quantity CHECK (stock_quantity >= 0)
);

CREATE UNIQUE INDEX idx_products_sku ON products(sku);
CREATE UNIQUE INDEX idx_products_slug ON products(slug);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_is_published ON products(is_published) WHERE is_published = TRUE;

-- 全文検索インデックス（日本語対応）
CREATE INDEX idx_products_name_fulltext ON products 
    USING gin(to_tsvector('japanese', name));
```

---

### 2.3 orders（注文）

```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_number VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    subtotal DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) NOT NULL,
    shipping_fee DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL,
    shipping_address_id BIGINT NOT NULL,
    ordered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_orders_user_id FOREIGN KEY (user_id) 
        REFERENCES users(id),
    CONSTRAINT fk_orders_shipping_address_id FOREIGN KEY (shipping_address_id) 
        REFERENCES addresses(id),
    CONSTRAINT chk_orders_status CHECK (status IN 
        ('PENDING', 'PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'COMPLETED', 'CANCELLED'))
);

CREATE UNIQUE INDEX idx_orders_order_number ON orders(order_number);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_ordered_at ON orders(ordered_at DESC);

-- 複合インデックス（ユーザー別・日付降順）
CREATE INDEX idx_orders_user_ordered ON orders(user_id, ordered_at DESC);
```

---

## 3. インデックス戦略

### 3.1 インデックス種別と用途

| テーブル | インデックス名 | 種別 | カラム | 用途 |
|---------|--------------|------|--------|------|
| users | idx_users_email | UNIQUE BTREE | email | ログイン認証（必須） |
| users | idx_users_status | PARTIAL BTREE | status | アクティブユーザー検索 |
| products | idx_products_name_fulltext | GIN | to_tsvector(name) | 商品名全文検索 |
| products | idx_products_category_id | BTREE | category_id | カテゴリ絞り込み |
| orders | idx_orders_user_ordered | COMPOSITE BTREE | user_id, ordered_at | ユーザー別注文履歴 |

### 3.2 インデックス作成方針

**UNIQUE インデックス:**
- 自然キー（email, sku, order_number）に設定
- データ整合性保証

**PARTIAL インデックス:**
```sql
-- アクティブユーザーのみ
CREATE INDEX idx_users_status ON users(status) WHERE status = 'ACTIVE';

-- 公開済み商品のみ
CREATE INDEX idx_products_is_published ON products(is_published) 
    WHERE is_published = TRUE;
```

**複合インデックス:**
```sql
-- WHERE user_id = ? ORDER BY ordered_at DESC のクエリに最適
CREATE INDEX idx_orders_user_ordered ON orders(user_id, ordered_at DESC);
```

---

## 4. パーティショニング

### 4.1 ordersテーブルのパーティショニング

**パーティショニング戦略:** 月次レンジパーティション

```sql
-- 親テーブル（パーティション化）
CREATE TABLE orders (
    -- カラム定義は同じ
) PARTITION BY RANGE (ordered_at);

-- 2025年10月パーティション
CREATE TABLE orders_2025_10 PARTITION OF orders
    FOR VALUES FROM ('2025-10-01') TO ('2025-11-01');

-- 2025年11月パーティション
CREATE TABLE orders_2025_11 PARTITION OF orders
    FOR VALUES FROM ('2025-11-01') TO ('2025-12-01');

-- 2025年12月パーティション
CREATE TABLE orders_2025_12 PARTITION OF orders
    FOR VALUES FROM ('2025-12-01') TO ('2026-01-01');
```

**メリット:**
- クエリパフォーマンス向上（パーティションプルーニング）
- 古いデータのアーカイブが容易
- メンテナンス効率化

**自動パーティション作成（pg_cron使用）:**
```sql
-- 毎月1日に翌月のパーティション作成
SELECT cron.schedule(
    'create-next-month-partition',
    '0 0 1 * *',
    $$
    -- パーティション作成SQL
    $$
);
```

---

## 5. レプリケーション設計

### 5.1 PostgreSQL ストリーミングレプリケーション

```
Primary DB (10.0.100.10)
    ↓ Async Replication
Replica DB (10.0.101.10)
```

**設定（Primary）:**
```sql
-- postgresql.conf
wal_level = replica
max_wal_senders = 3
wal_keep_size = 1GB

-- pg_hba.conf
host replication replicator 10.0.101.10/32 md5
```

**設定（Replica）:**
```sql
-- postgresql.conf
hot_standby = on
```

### 5.2 読み取り分散

```java
// Spring Boot - 読み取り専用クエリをReplicaにルーティング
@Transactional(readOnly = true)
public List<Product> getProducts() {
    return productRepository.findAll();  // → Replica DBへ
}

@Transactional
public Product createProduct(Product product) {
    return productRepository.save(product);  // → Primary DBへ
}
```

---

## 6. バックアップ・リカバリ

### 6.1 バックアップ戦略

| バックアップ種別 | 頻度 | 保持期間 | 方式 |
|---------------|------|---------|------|
| **完全バックアップ** | 毎日 3:00 AM | 7日 | AWS RDS 自動スナップショット |
| **差分バックアップ** | 1時間ごと | 24時間 | WAL アーカイブ |
| **論理バックアップ** | 毎週日曜 | 4週 | pg_dump |

**RDS自動バックアップ設定:**
```terraform
resource "aws_db_instance" "postgres" {
  backup_retention_period = 7
  backup_window          = "03:00-04:00"
  maintenance_window     = "sun:04:00-sun:05:00"
  
  enabled_cloudwatch_logs_exports = ["postgresql", "upgrade"]
}
```

### 6.2 リカバリ手順

**Point-in-Time Recovery (PITR):**
```bash
# 特定時刻へのリカバリ
aws rds restore-db-instance-to-point-in-time \
  --source-db-instance-identifier ec-shop-prod \
  --target-db-instance-identifier ec-shop-restored \
  --restore-time 2025-10-30T15:30:00Z
```

---

## 7. 性能チューニング

### 7.1 PostgreSQL パラメータ

```sql
-- postgresql.conf（db.t3.large想定）

-- メモリ設定
shared_buffers = 2GB              -- RAM の 25%
effective_cache_size = 6GB        -- RAM の 75%
work_mem = 16MB                   -- ソート・ハッシュ用
maintenance_work_mem = 512MB      -- VACUUM, インデックス作成用

-- 接続設定
max_connections = 100

-- WAL設定
wal_buffers = 16MB
checkpoint_completion_target = 0.9

-- クエリプランナー
random_page_cost = 1.1            -- SSD想定
effective_io_concurrency = 200    -- SSD想定

-- 統計情報
default_statistics_target = 100
```

### 7.2 クエリ最適化

**EXPLAIN ANALYZE 活用:**
```sql
EXPLAIN (ANALYZE, BUFFERS) 
SELECT * FROM products 
WHERE category_id = 5 
  AND is_published = TRUE 
ORDER BY created_at DESC 
LIMIT 20;

-- 結果分析:
-- - Index Scan が使われているか
-- - Buffers の shared hits/reads 比率
-- - Execution Time
```

**スロークエリログ:**
```sql
-- postgresql.conf
log_min_duration_statement = 1000  -- 1秒以上のクエリをログ
log_line_prefix = '%t [%p]: [%l-1] user=%u,db=%d,app=%a,client=%h '
```

---

## 8. 容量設計

### 8.1 ストレージ見積もり（3年後）

| テーブル | レコード数 | 平均サイズ | 合計サイズ | インデックス | 総計 |
|---------|-----------|----------|----------|------------|------|
| users | 300,000 | 500B | 150MB | 50MB | 200MB |
| products | 30,000 | 800B | 24MB | 15MB | 39MB |
| orders | 3,000,000 | 400B | 1.2GB | 600MB | 1.8GB |
| order_items | 9,000,000 | 200B | 1.8GB | 900MB | 2.7GB |
| **合計** | - | - | **約3GB** | **約1.5GB** | **約4.5GB** |

**推奨ストレージ:** 100GB（成長余裕含む）

### 8.2 IOPS要件

**計算式:**
```
必要IOPS = (読み取りTPS × 平均読み取りIOPS) + (書き込みTPS × 平均書き込みIOPS)
         = (80 × 2) + (20 × 5)
         = 260 IOPS
```

**推奨:** gp3（3,000 IOPS ベースライン）

---

## 9. 監視項目

### 9.1 CloudWatch メトリクス

| メトリクス | 閾値 | アラート |
|-----------|------|---------|
| **CPUUtilization** | > 80% | Warning |
| **FreeableMemory** | < 512MB | Critical |
| **DatabaseConnections** | > 80 | Warning |
| **ReadLatency** | > 100ms | Warning |
| **WriteLatency** | > 50ms | Warning |

### 9.2 カスタムクエリ

```sql
-- 接続数監視
SELECT count(*) FROM pg_stat_activity WHERE state = 'active';

-- ロック監視
SELECT * FROM pg_locks WHERE NOT granted;

-- テーブルサイズ監視
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

---

## 10. 変更履歴

| バージョン | 日付 | 変更内容 | 変更者 |
|-----------|------|---------|--------|
| 1.0 | 2025-10-30 | 初版作成 | DB設計者 |

---

**ドキュメント終了**
