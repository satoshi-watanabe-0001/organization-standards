# データベース設計標準 / Database Design Standards

## バージョン情報 / Version Information
- **最終更新日 / Last Updated**: 2025-10-24
- **バージョン / Version**: 1.0
- **対象 / Target**: すべてのプロジェクト / All Projects
- **適用範囲 / Scope**: 推奨 / Recommended (Tier 2)

---

## 目的 / Purpose

このドキュメントは、データベース設計のベストプラクティスと標準を定義します。スキーマ設計原則、インデックス戦略、トランザクション設計、データベース選定基準など、データベースの設計から実装までの指針を提供します。

This document defines best practices and standards for database design, including schema design principles, indexing strategies, transaction design, database selection criteria, and guidelines from design to implementation.

---

## 1. スキーマ設計原則 / Schema Design Principles

### 1.1 正規化 / Normalization

#### 第一正規形（1NF）/ First Normal Form
```sql
-- ❌ 悪い例: 繰り返しグループが存在
CREATE TABLE orders_bad (
    order_id INT PRIMARY KEY,
    customer_name VARCHAR(100),
    products VARCHAR(500),  -- 'ProductA, ProductB, ProductC' - NG
    quantities VARCHAR(100) -- '2, 1, 5' - NG
);

-- ✅ 良い例: 繰り返しグループを分離
CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    customer_name VARCHAR(100),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    order_item_id INT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);
```

#### 第二正規形（2NF）/ Second Normal Form
```sql
-- ❌ 悪い例: 部分関数従属が存在
CREATE TABLE order_details_bad (
    order_id INT,
    product_id INT,
    product_name VARCHAR(100),    -- product_idに従属
    product_price DECIMAL(10,2),  -- product_idに従属
    quantity INT,
    PRIMARY KEY (order_id, product_id)
);

-- ✅ 良い例: 部分関数従属を除去
CREATE TABLE products (
    product_id INT PRIMARY KEY,
    product_name VARCHAR(100),
    product_price DECIMAL(10,2)
);

CREATE TABLE order_items (
    order_id INT,
    product_id INT,
    quantity INT,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);
```

#### 第三正規形（3NF）/ Third Normal Form
```sql
-- ❌ 悪い例: 推移的関数従属が存在
CREATE TABLE employees_bad (
    employee_id INT PRIMARY KEY,
    employee_name VARCHAR(100),
    department_id INT,
    department_name VARCHAR(100),  -- department_idに従属 - NG
    department_location VARCHAR(100) -- department_idに従属 - NG
);

-- ✅ 良い例: 推移的関数従属を除去
CREATE TABLE departments (
    department_id INT PRIMARY KEY,
    department_name VARCHAR(100),
    department_location VARCHAR(100)
);

CREATE TABLE employees (
    employee_id INT PRIMARY KEY,
    employee_name VARCHAR(100),
    department_id INT,
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
);
```

### 1.2 非正規化 / Denormalization

パフォーマンス最適化のための戦略的な非正規化：

```sql
-- ✅ 良い例: 読み取り最適化のための非正規化
-- 集計値の事前計算
CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    customer_id INT,
    order_date TIMESTAMP,
    total_amount DECIMAL(10,2),  -- 非正規化: 計算済み合計
    item_count INT,              -- 非正規化: アイテム数
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- トリガーで非正規化データを維持
CREATE TRIGGER update_order_totals
AFTER INSERT OR UPDATE OR DELETE ON order_items
FOR EACH ROW
EXECUTE FUNCTION recalculate_order_totals();
```

#### 非正規化の適用基準
- ✅ 読み取り頻度 >> 更新頻度
- ✅ 複雑な結合のパフォーマンス問題
- ✅ 集計クエリの頻繁な実行
- ❌ データ整合性のリスクが高い場合
- ❌ 更新頻度が高い場合

---

## 2. インデックス戦略 / Indexing Strategy

### 2.1 B-Treeインデックス / B-Tree Index

```sql
-- ✅ 良い例: 適切なB-Treeインデックス
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- 頻繁な検索条件にインデックス
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_created_at ON users(created_at);

-- 複合インデックス（検索頻度の高い組み合わせ）
CREATE INDEX idx_users_username_created ON users(username, created_at);
```

#### インデックス選定基準
```python
# インデックス作成の判断基準
class IndexingCriteria:
    @staticmethod
    def should_create_index(table_name: str, column_name: str) -> bool:
        """インデックス作成の判断"""
        # 1. カーディナリティチェック
        distinct_ratio = get_distinct_ratio(table_name, column_name)
        if distinct_ratio < 0.1:  # 10%未満は低カーディナリティ
            return False
        
        # 2. 使用頻度チェック
        query_frequency = get_query_frequency(table_name, column_name)
        if query_frequency < 100:  # 週100回未満
            return False
        
        # 3. テーブルサイズチェック
        row_count = get_table_row_count(table_name)
        if row_count < 10000:  # 1万行未満は不要
            return False
        
        return True
```

### 2.2 部分インデックス / Partial Index

```sql
-- ✅ 良い例: 部分インデックスで効率化
CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    customer_id INT,
    status VARCHAR(20),
    order_date TIMESTAMP,
    total_amount DECIMAL(10,2)
);

-- アクティブな注文のみにインデックス
CREATE INDEX idx_orders_active 
ON orders(customer_id, order_date)
WHERE status IN ('pending', 'processing');

-- 高額注文のみにインデックス
CREATE INDEX idx_orders_high_value
ON orders(order_date)
WHERE total_amount > 10000;
```

### 2.3 全文検索インデックス / Full-Text Search Index

```sql
-- ✅ 良い例: 全文検索インデックス（PostgreSQL）
CREATE TABLE articles (
    article_id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    content TEXT,
    published_at TIMESTAMP
);

-- tsvectorカラムを追加
ALTER TABLE articles ADD COLUMN content_tsv tsvector;

-- tsvectorを更新するトリガー
CREATE TRIGGER articles_content_tsv_update
BEFORE INSERT OR UPDATE ON articles
FOR EACH ROW EXECUTE FUNCTION
tsvector_update_trigger(content_tsv, 'pg_catalog.english', title, content);

-- GINインデックスを作成
CREATE INDEX idx_articles_content_tsv ON articles USING GIN(content_tsv);

-- 使用例
SELECT article_id, title
FROM articles
WHERE content_tsv @@ to_tsquery('postgresql & performance');
```

### 2.4 インデックスメンテナンス / Index Maintenance

```sql
-- ✅ 良い例: インデックスの監視とメンテナンス

-- 1. インデックス使用状況の監視
CREATE VIEW index_usage_stats AS
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan as index_scans,
    idx_tup_read as tuples_read,
    idx_tup_fetch as tuples_fetched
FROM pg_stat_user_indexes
ORDER BY idx_scan ASC;

-- 2. 未使用インデックスの検出
SELECT
    schemaname || '.' || tablename AS table,
    indexname AS index,
    pg_size_pretty(pg_relation_size(indexrelid)) AS size
FROM pg_stat_user_indexes
WHERE idx_scan = 0
    AND indexrelid::regclass::text NOT LIKE '%_pkey'
ORDER BY pg_relation_size(indexrelid) DESC;

-- 3. インデックスの再構築（定期メンテナンス）
REINDEX INDEX CONCURRENTLY idx_users_email;
REINDEX TABLE CONCURRENTLY users;
```

---

## 3. トランザクション設計 / Transaction Design

### 3.1 ACID特性 / ACID Properties

#### Atomicity（原子性）
```python
# ✅ 良い例: トランザクションの原子性
from contextlib import contextmanager
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

@contextmanager
def transaction_scope(session):
    """トランザクションスコープの管理"""
    try:
        yield session
        session.commit()  # すべて成功時のみコミット
    except Exception as e:
        session.rollback()  # エラー時はロールバック
        raise e
    finally:
        session.close()

# 使用例: 銀行送金
def transfer_money(from_account_id: int, to_account_id: int, amount: float):
    with transaction_scope(Session()) as session:
        # すべての操作が成功するか、すべて失敗する
        from_account = session.query(Account).filter_by(id=from_account_id).with_for_update().first()
        to_account = session.query(Account).filter_by(id=to_account_id).with_for_update().first()
        
        if from_account.balance < amount:
            raise InsufficientFundsError()
        
        from_account.balance -= amount
        to_account.balance += amount
        
        # トランザクションログ
        log = TransactionLog(
            from_account_id=from_account_id,
            to_account_id=to_account_id,
            amount=amount
        )
        session.add(log)
        # commit()は自動的に呼ばれる

# ❌ 悪い例: トランザクションなし
def transfer_money_bad(from_account_id: int, to_account_id: int, amount: float):
    from_account = get_account(from_account_id)
    from_account.balance -= amount
    save_account(from_account)  # ここで例外が発生すると...
    
    to_account = get_account(to_account_id)
    to_account.balance += amount
    save_account(to_account)  # ...これが実行されない
```

#### Isolation（分離性）
```sql
-- 分離レベルの選択

-- ✅ READ COMMITTED（デフォルト、ほとんどのケースで推奨）
BEGIN TRANSACTION ISOLATION LEVEL READ COMMITTED;
SELECT * FROM accounts WHERE account_id = 123;
-- 他のトランザクションのコミット済み変更が見える
COMMIT;

-- ✅ REPEATABLE READ（一貫性のある読み取りが必要な場合）
BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ;
SELECT balance FROM accounts WHERE account_id = 123;
-- 2回目の読み取りで同じ値が保証される
SELECT balance FROM accounts WHERE account_id = 123;
COMMIT;

-- ✅ SERIALIZABLE（最高レベルの分離が必要な場合）
BEGIN TRANSACTION ISOLATION LEVEL SERIALIZABLE;
-- トランザクション間の相互作用を完全に排除
COMMIT;

-- ❌ READ UNCOMMITTED（推奨しない）
-- ダーティリードが発生する可能性
```

### 3.2 楽観的ロック vs 悲観的ロック / Optimistic vs Pessimistic Locking

#### 楽観的ロック（Optimistic Locking）
```python
# ✅ 良い例: バージョン番号による楽観的ロック
from sqlalchemy import Column, Integer, String, DateTime
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class Product(Base):
    __tablename__ = 'products'
    
    id = Column(Integer, primary_key=True)
    name = Column(String(100))
    stock = Column(Integer)
    version = Column(Integer, default=0)  # バージョン番号
    updated_at = Column(DateTime)

# 更新処理
def update_product_stock(product_id: int, new_stock: int, expected_version: int):
    result = session.query(Product).filter(
        Product.id == product_id,
        Product.version == expected_version  # バージョンチェック
    ).update({
        'stock': new_stock,
        'version': Product.version + 1,  # バージョンをインクリメント
        'updated_at': datetime.now()
    })
    
    if result == 0:
        raise OptimisticLockError("Product was modified by another transaction")
    
    session.commit()
```

#### 悲観的ロック（Pessimistic Locking）
```python
# ✅ 良い例: SELECT FOR UPDATE による悲観的ロック
def reserve_seat(seat_id: int, user_id: int):
    with transaction_scope(Session()) as session:
        # 行ロックを取得
        seat = session.query(Seat).filter_by(id=seat_id).with_for_update().first()
        
        if not seat:
            raise SeatNotFoundError()
        
        if seat.is_reserved:
            raise SeatAlreadyReservedError()
        
        # ロック取得中は他のトランザクションは待機
        seat.is_reserved = True
        seat.reserved_by = user_id
        seat.reserved_at = datetime.now()

# ❌ 悪い例: ロックなし（競合状態）
def reserve_seat_bad(seat_id: int, user_id: int):
    seat = session.query(Seat).filter_by(id=seat_id).first()
    
    if seat.is_reserved:  # チェック時は未予約
        raise SeatAlreadyReservedError()
    
    # ここで他のトランザクションが予約する可能性
    
    seat.is_reserved = True  # 二重予約が発生
    session.commit()
```

### 3.3 デッドロックの回避 / Deadlock Prevention

```python
# ✅ 良い例: 一貫した順序でのロック取得
def transfer_between_accounts(account_id_1: int, account_id_2: int, amount: float):
    # 常にIDの小さい順でロック取得（デッドロック回避）
    first_id, second_id = sorted([account_id_1, account_id_2])
    
    with transaction_scope(Session()) as session:
        first_account = session.query(Account).filter_by(
            id=first_id
        ).with_for_update().first()
        
        second_account = session.query(Account).filter_by(
            id=second_id
        ).with_for_update().first()
        
        # 送金処理
        if first_id == account_id_1:
            first_account.balance -= amount
            second_account.balance += amount
        else:
            first_account.balance += amount
            second_account.balance -= amount

# ❌ 悪い例: 順序が一貫しない（デッドロック発生の可能性）
def transfer_bad(from_id: int, to_id: int, amount: float):
    with transaction_scope(Session()) as session:
        from_account = session.query(Account).filter_by(id=from_id).with_for_update().first()
        # トランザクションAがアカウント1をロック、Bがアカウント2をロック
        to_account = session.query(Account).filter_by(id=to_id).with_for_update().first()
        # Aがアカウント2を、Bがアカウント1をロックしようとしてデッドロック
```

---

## 4. RDBMS vs NoSQL選定基準 / RDBMS vs NoSQL Selection Criteria

### 4.1 選定フローチャート / Selection Flowchart

```
データベース選定フロー:

[開始]
  ↓
[トランザクション整合性が必須?]
  ├─Yes → [複雑な結合が必要?]
  │         ├─Yes → **RDBMS (PostgreSQL, MySQL)**
  │         └─No → [書き込み頻度は?]
  │                  ├─低 → **RDBMS**
  │                  └─高 → [データ構造は?]
  │                           ├─固定 → **RDBMS**
  │                           └─可変 → **NoSQL (MongoDB)**
  │
  └─No → [データモデルは?]
          ├─ドキュメント → **MongoDB**
          ├─キー・バリュー → [スケール要件は?]
          │                  ├─超高速読み取り → **Redis**
          │                  └─大規模分散 → **DynamoDB**
          ├─グラフ → **Neo4j**
          ├─カラムファミリー → **Cassandra**
          └─時系列 → **InfluxDB / TimescaleDB**
```

### 4.2 RDBMS（リレーショナルデータベース）

#### 適用ケース
- ✅ ACID特性が必須
- ✅ 複雑な結合クエリが必要
- ✅ データ整合性が重要
- ✅ トランザクション処理が中心

```sql
-- ✅ RDBMS向きの例: 金融システム
CREATE TABLE accounts (
    account_id SERIAL PRIMARY KEY,
    customer_id INT NOT NULL,
    balance DECIMAL(15,2) NOT NULL CHECK (balance >= 0),
    account_type VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE transactions (
    transaction_id SERIAL PRIMARY KEY,
    from_account_id INT NOT NULL,
    to_account_id INT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20),
    FOREIGN KEY (from_account_id) REFERENCES accounts(account_id),
    FOREIGN KEY (to_account_id) REFERENCES accounts(account_id)
);

-- 複雑な結合クエリ
SELECT 
    c.customer_name,
    a.account_id,
    a.balance,
    COUNT(t.transaction_id) as transaction_count,
    SUM(t.amount) as total_amount
FROM customers c
JOIN accounts a ON c.customer_id = a.customer_id
LEFT JOIN transactions t ON a.account_id = t.from_account_id
WHERE t.transaction_date >= NOW() - INTERVAL '30 days'
GROUP BY c.customer_name, a.account_id, a.balance
HAVING COUNT(t.transaction_id) > 10;
```

### 4.3 NoSQL（非リレーショナルデータベース）

#### MongoDB（ドキュメント指向）
```javascript
// ✅ MongoDB向きの例: コンテンツ管理システム
// スキーマの柔軟性が必要な場合

// ブログ記事のドキュメント
{
    "_id": ObjectId("..."),
    "title": "Database Design Best Practices",
    "author": {
        "name": "John Doe",
        "email": "john@example.com"
    },
    "content": "...",
    "tags": ["database", "design", "best-practices"],
    "comments": [
        {
            "user": "Jane",
            "text": "Great article!",
            "timestamp": ISODate("2025-10-24T10:00:00Z")
        }
    ],
    "metadata": {
        "views": 1234,
        "likes": 56,
        "shares": 12
    },
    "published_at": ISODate("2025-10-20T00:00:00Z")
}

// 複雑なクエリ
db.articles.find({
    "tags": "database",
    "published_at": { $gte: new Date("2025-01-01") },
    "metadata.views": { $gt: 1000 }
}).sort({ "published_at": -1 }).limit(10);
```

#### Redis（キー・バリュー）
```python
# ✅ Redis向きの例: セッション管理、キャッシュ
import redis
import json

r = redis.Redis(host='localhost', port=6379, decode_responses=True)

# セッション保存（TTL付き）
def save_session(session_id: str, user_data: dict, ttl: int = 3600):
    r.setex(
        f"session:{session_id}",
        ttl,
        json.dumps(user_data)
    )

# キャッシュ戦略
def get_user_profile(user_id: int):
    cache_key = f"user_profile:{user_id}"
    
    # キャッシュチェック
    cached = r.get(cache_key)
    if cached:
        return json.loads(cached)
    
    # DBから取得
    profile = db.query(User).filter_by(id=user_id).first()
    
    # キャッシュに保存（5分間）
    r.setex(cache_key, 300, json.dumps(profile.to_dict()))
    
    return profile
```

#### Cassandra（カラムファミリー）
```sql
-- ✅ Cassandra向きの例: IoTデータ、時系列データ
-- 大規模書き込み、分散環境

CREATE TABLE sensor_data (
    sensor_id UUID,
    timestamp TIMESTAMP,
    temperature DECIMAL,
    humidity DECIMAL,
    location TEXT,
    PRIMARY KEY ((sensor_id), timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC);

-- パーティションキーによる効率的なクエリ
SELECT * FROM sensor_data
WHERE sensor_id = 123e4567-e89b-12d3-a456-426614174000
    AND timestamp >= '2025-10-24 00:00:00'
    AND timestamp < '2025-10-25 00:00:00';
```

---

## 5. パーティショニングとシャーディング / Partitioning and Sharding

### 5.1 水平パーティショニング / Horizontal Partitioning

```sql
-- ✅ 良い例: 時系列データの範囲パーティショニング（PostgreSQL）
CREATE TABLE sales (
    sale_id SERIAL,
    sale_date DATE NOT NULL,
    customer_id INT,
    amount DECIMAL(10,2),
    PRIMARY KEY (sale_id, sale_date)
) PARTITION BY RANGE (sale_date);

-- パーティションの作成
CREATE TABLE sales_2024_q1 PARTITION OF sales
    FOR VALUES FROM ('2024-01-01') TO ('2024-04-01');

CREATE TABLE sales_2024_q2 PARTITION OF sales
    FOR VALUES FROM ('2024-04-01') TO ('2024-07-01');

CREATE TABLE sales_2024_q3 PARTITION OF sales
    FOR VALUES FROM ('2024-07-01') TO ('2024-10-01');

CREATE TABLE sales_2024_q4 PARTITION OF sales
    FOR VALUES FROM ('2024-10-01') TO ('2025-01-01');

-- クエリは自動的に適切なパーティションにルーティング
SELECT * FROM sales
WHERE sale_date BETWEEN '2024-04-01' AND '2024-06-30';
-- sales_2024_q2パーティションのみスキャン
```

### 5.2 垂直パーティショニング / Vertical Partitioning

```sql
-- ✅ 良い例: 頻繁にアクセスされるデータと稀にアクセスされるデータを分離
-- 頻繁にアクセスされるカラム
CREATE TABLE users_hot (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    last_login TIMESTAMP,
    is_active BOOLEAN
);

-- 稀にアクセスされるカラム
CREATE TABLE users_cold (
    user_id INT PRIMARY KEY,
    full_address TEXT,
    biography TEXT,
    preferences JSONB,
    FOREIGN KEY (user_id) REFERENCES users_hot(user_id)
);
```

### 5.3 シャーディング / Sharding

```python
# ✅ 良い例: アプリケーションレベルのシャーディング
class ShardManager:
    def __init__(self, shard_count: int):
        self.shard_count = shard_count
        self.shards = {
            i: create_engine(f"postgresql://shard{i}.db")
            for i in range(shard_count)
        }
    
    def get_shard(self, user_id: int):
        """ユーザーIDからシャードを決定"""
        shard_id = user_id % self.shard_count
        return self.shards[shard_id]
    
    def insert_user(self, user_id: int, user_data: dict):
        """適切なシャードに挿入"""
        shard = self.get_shard(user_id)
        with shard.connect() as conn:
            conn.execute(
                "INSERT INTO users (user_id, username, email) VALUES (%s, %s, %s)",
                (user_id, user_data['username'], user_data['email'])
            )
    
    def get_user(self, user_id: int):
        """適切なシャードから取得"""
        shard = self.get_shard(user_id)
        with shard.connect() as conn:
            result = conn.execute(
                "SELECT * FROM users WHERE user_id = %s",
                (user_id,)
            )
            return result.fetchone()

# 使用例
shard_manager = ShardManager(shard_count=4)
shard_manager.insert_user(12345, {'username': 'john', 'email': 'john@example.com'})
user = shard_manager.get_user(12345)
```

---

## 6. データ移行戦略 / Data Migration Strategy

### 6.1 マイグレーションの原則 / Migration Principles

#### 段階的マイグレーション
```python
# ✅ 良い例: 段階的スキーマ変更
class DatabaseMigration:
    def migrate_add_column_safely(self):
        """カラム追加の安全な移行"""
        # フェーズ1: NULLABLEなカラムを追加
        self.execute("""
            ALTER TABLE users
            ADD COLUMN phone_number VARCHAR(20) NULL;
        """)
        
        # フェーズ2: 既存データを更新
        self.execute("""
            UPDATE users
            SET phone_number = legacy_phone
            WHERE phone_number IS NULL AND legacy_phone IS NOT NULL;
        """)
        
        # フェーズ3: アプリケーションコードをデプロイ
        # （新しいカラムを使用するように変更）
        
        # フェーズ4: NOT NULL制約を追加
        self.execute("""
            ALTER TABLE users
            ALTER COLUMN phone_number SET NOT NULL;
        """)
        
        # フェーズ5: 古いカラムを削除
        self.execute("""
            ALTER TABLE users
            DROP COLUMN legacy_phone;
        """)

# ❌ 悪い例: 一度にすべて変更
def migrate_bad():
    execute("ALTER TABLE users DROP COLUMN old_column, ADD COLUMN new_column VARCHAR(20) NOT NULL;")
    # ダウンタイムが発生、ロールバックが困難
```

### 6.2 ゼロダウンタイムマイグレーション / Zero-Downtime Migration

```python
# ✅ 良い例: Expand-Contract パターン
class ZeroDowntimeMigration:
    def phase1_expand(self):
        """拡張フェーズ: 新しい構造を追加"""
        # 新しいテーブルを作成
        self.execute("""
            CREATE TABLE users_v2 (
                user_id SERIAL PRIMARY KEY,
                email VARCHAR(255) NOT NULL,
                first_name VARCHAR(50),
                last_name VARCHAR(50)
            );
        """)
        
        # 二重書き込みトリガーを追加
        self.execute("""
            CREATE TRIGGER sync_to_v2
            AFTER INSERT OR UPDATE ON users
            FOR EACH ROW
            EXECUTE FUNCTION sync_user_to_v2();
        """)
    
    def phase2_migrate_data(self):
        """データ移行フェーズ"""
        # バッチ処理で既存データを移行
        batch_size = 1000
        offset = 0
        
        while True:
            result = self.execute(f"""
                INSERT INTO users_v2 (user_id, email, first_name, last_name)
                SELECT 
                    user_id, 
                    email,
                    split_part(full_name, ' ', 1) as first_name,
                    split_part(full_name, ' ', 2) as last_name
                FROM users
                WHERE user_id NOT IN (SELECT user_id FROM users_v2)
                ORDER BY user_id
                LIMIT {batch_size}
            """)
            
            if result.rowcount == 0:
                break
            
            offset += batch_size
            time.sleep(0.1)  # 負荷を分散
    
    def phase3_switch(self):
        """切り替えフェーズ: アプリケーションを新しいテーブルに向ける"""
        # アプリケーションコードをデプロイ（users_v2を使用）
        pass
    
    def phase4_contract(self):
        """収縮フェーズ: 古い構造を削除"""
        # トリガーを削除
        self.execute("DROP TRIGGER sync_to_v2 ON users;")
        
        # 古いテーブルを削除
        self.execute("DROP TABLE users;")
        
        # 新しいテーブルをリネーム
        self.execute("ALTER TABLE users_v2 RENAME TO users;")
```

---

## 7. パフォーマンス最適化 / Performance Optimization

### 7.1 クエリ最適化 / Query Optimization

```sql
-- ❌ 悪い例: N+1問題
SELECT * FROM orders;
-- アプリケーションで各注文の顧客情報を取得
-- SELECT * FROM customers WHERE id = ?; (N回実行)

-- ✅ 良い例: JOINで一度に取得
SELECT 
    o.*,
    c.customer_name,
    c.email
FROM orders o
JOIN customers c ON o.customer_id = c.customer_id;

-- ❌ 悪い例: SELECT *
SELECT * FROM users WHERE user_id = 123;

-- ✅ 良い例: 必要なカラムのみ選択
SELECT user_id, username, email FROM users WHERE user_id = 123;

-- ❌ 悪い例: サブクエリの非効率な使用
SELECT *
FROM products
WHERE category_id IN (
    SELECT category_id
    FROM categories
    WHERE category_name = 'Electronics'
);

-- ✅ 良い例: JOINを使用
SELECT p.*
FROM products p
JOIN categories c ON p.category_id = c.category_id
WHERE c.category_name = 'Electronics';
```

### 7.2 EXPLAIN ANALYZEの活用 / Using EXPLAIN ANALYZE

```sql
-- ✅ 良い例: 実行計画の分析
EXPLAIN ANALYZE
SELECT 
    u.username,
    COUNT(o.order_id) as order_count
FROM users u
LEFT JOIN orders o ON u.user_id = o.customer_id
WHERE u.created_at >= '2025-01-01'
GROUP BY u.user_id, u.username
HAVING COUNT(o.order_id) > 5;

-- 出力例:
-- GroupAggregate  (cost=... rows=... time=...)
--   Group Key: u.user_id, u.username
--   Filter: (count(o.order_id) > 5)
--   ->  Sort  (cost=... rows=... time=...)
--         Sort Key: u.user_id
--         ->  Hash Left Join  (cost=... rows=... time=...)
--               Hash Cond: (o.customer_id = u.user_id)
--               ->  Seq Scan on orders o  (cost=... rows=... time=...)
--               ->  Hash  (cost=... rows=... time=...)
--                     ->  Seq Scan on users u  (cost=... rows=... time=...)
--                           Filter: (created_at >= '2025-01-01'::date)
```

### 7.3 コネクションプーリング / Connection Pooling

```python
# ✅ 良い例: コネクションプールの実装
from sqlalchemy import create_engine
from sqlalchemy.pool import QueuePool

# プール設定
engine = create_engine(
    'postgresql://user:password@localhost/dbname',
    poolclass=QueuePool,
    pool_size=20,           # プール内の常時接続数
    max_overflow=10,        # プールサイズを超えて作成可能な接続数
    pool_timeout=30,        # 接続取得のタイムアウト（秒）
    pool_recycle=3600,      # 接続の再利用時間（秒）
    pool_pre_ping=True      # 接続の有効性を事前確認
)

# 使用例
def execute_query():
    # コネクションプールから接続を取得
    with engine.connect() as conn:
        result = conn.execute("SELECT * FROM users LIMIT 10")
        return result.fetchall()
    # 自動的にプールに返却される
```

---

## 8. Devin向けの利用パターン / Usage Patterns for Devin

### 8.1 スキーマ設計のプロンプト例

#### プロンプト1: 正規化されたスキーマ設計
```
タスク: Eコマースプラットフォーム向けのデータベーススキーマを設計してください

要件:
1. 正規化:
   - 第三正規形（3NF）まで正規化
   - 適切な外部キー制約
   - NOT NULL制約とCHECK制約

2. 含めるエンティティ:
   - ユーザー（customers）
   - 商品（products）
   - カテゴリ（categories）
   - 注文（orders）
   - 注文明細（order_items）
   - レビュー（reviews）

3. 実装基準:
   - このドキュメントのセクション1（スキーマ設計原則）に従う
   - 適切なインデックスを含める
   - トランザクション整合性を保証

出力形式: PostgreSQL DDL
```

#### プロンプト2: パフォーマンス最適化
```
タスク: 既存のデータベーススキーマのパフォーマンスを最適化してください

要件:
1. インデックス分析:
   - 頻繁に実行されるクエリを特定
   - 適切なインデックスを提案
   - 未使用インデックスを検出

2. クエリ最適化:
   - N+1問題の解消
   - 不要なサブクエリの削除
   - 適切なJOIN戦略

3. 実装基準:
   - このドキュメントのセクション2（インデックス戦略）とセクション7（パフォーマンス最適化）に従う
   - EXPLAIN ANALYZEで検証
```

#### プロンプト3: データベース選定
```
タスク: 以下のユースケースに最適なデータベースを選定してください

ユースケース:
- リアルタイムチャットアプリケーション
- メッセージの永続化
- 既読管理
- ユーザーオンラインステータス
- メッセージ検索機能

要件:
1. データベース選定基準:
   - トランザクション要件
   - スケーラビリティ
   - クエリパターン
   - 一貫性要件

2. 評価項目:
   - RDBMS vs NoSQL の比較
   - 具体的な製品の推奨（PostgreSQL、MongoDB、Redisなど）
   - ハイブリッド構成の検討

3. 実装基準:
   - このドキュメントのセクション4（RDBMS vs NoSQL選定基準）に従う
```

### 8.2 マイグレーション自動生成プロンプト

#### プロンプト4: ゼロダウンタイムマイグレーション
```
タスク: 既存のusersテーブルに新しいカラムを追加するゼロダウンタイムマイグレーションを実装してください

現在のスキーマ:
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    full_name VARCHAR(100)
);

新しい要件:
- full_nameをfirst_nameとlast_nameに分割
- 既存データを保持
- ダウンタイムなし

要件:
1. Expand-Contractパターンを使用
2. 段階的なマイグレーション計画
3. ロールバック手順

実装基準:
- このドキュメントのセクション6（データ移行戦略）に従う
```

---

## 9. チェックリスト / Checklist

### データベース設計チェックリスト

#### 設計フェーズ
- [ ] データモデルの作成（ERダイアグラム）
- [ ] 正規化の実施（最低3NFまで）
- [ ] 非正規化の戦略的検討
- [ ] インデックス戦略の策定
- [ ] トランザクション境界の定義
- [ ] データベースタイプの選定（RDBMS vs NoSQL）

#### 実装フェーズ
- [ ] DDLスクリプトの作成
- [ ] 外部キー制約の定義
- [ ] NOT NULL制約とCHECK制約
- [ ] 適切なインデックスの作成
- [ ] トランザクション分離レベルの設定
- [ ] コネクションプールの設定

#### テストフェーズ
- [ ] データ整合性のテスト
- [ ] トランザクションのテスト（ACID特性）
- [ ] パフォーマンステスト（EXPLAIN ANALYZE）
- [ ] 負荷テスト
- [ ] デッドロックテスト
- [ ] マイグレーションテスト

#### 運用フェーズ
- [ ] インデックス使用状況の監視
- [ ] スロークエリログの分析
- [ ] 定期的なVACUUM/ANALYZE（PostgreSQL）
- [ ] バックアップの実施と検証
- [ ] パフォーマンスメトリクスの監視

---

## 10. 関連ドキュメント / Related Documents

- [アーキテクチャ設計原則](./design-principles.md)
- [データ保護標準](../07-security-compliance/data-protection.md)
- [コーディング標準](../01-coding-standards/README.md)
- [品質標準](../04-quality-standards/README.md)

---

## 11. 更新履歴 / Change History

| バージョン | 日付 | 変更内容 | 作成者 |
|---------|------|---------|-------|
| 1.0 | 2025-10-24 | 初版作成 | Development Team |

---

**このドキュメントの維持管理についてのお問い合わせは、アーキテクチャチームまでご連絡ください。**
