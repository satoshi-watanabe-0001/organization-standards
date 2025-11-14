# SQL開発 - AI向けクイックリファレンス

**目的**: AIがSQLコード生成・レビュー時に参照すべき重要ポイントをまとめたサマリー

**バージョン**: 1.0.0  
**最終更新**: 2025-11-13

---

## 🎯 このドキュメントの使い方

**AIエージェント向け**:
- コード生成時: このファイルを最初に読み込む
- コードレビュー時: チェックリストとして使用
- 詳細確認: 各項目のリンクから詳細ドキュメントへ

**人間向け**:
- クイックチェック: レビュー時の確認項目
- 学習教材: 重要ポイントの把握

---

## ✅ 必須チェック項目 TOP 25

### A. セキュリティ (1-8)

#### 1. ✓ SQLインジェクション対策: パラメータ化クエリの使用

❌ **悪い例** - 文字列結合:
```python
# 危険! SQLインジェクションの脆弱性
user_id = request.get('user_id')
query = f"SELECT * FROM users WHERE user_id = {user_id}"
cursor.execute(query)
```

✅ **良い例** - パラメータ化:
```python
# PostgreSQL (psycopg2)
cursor.execute("SELECT * FROM users WHERE user_id = %s", (user_id,))

# MySQL (mysql-connector)
cursor.execute("SELECT * FROM users WHERE user_id = %s", (user_id,))

# Python sqlite3
cursor.execute("SELECT * FROM users WHERE user_id = ?", (user_id,))
```

#### 2. ✓ 入力バリデーションの徹底

```python
# データ型チェック
if not isinstance(user_id, int):
    raise ValueError("Invalid user_id type")

# 範囲チェック
if user_id < 1 or user_id > 9999999:
    raise ValueError("user_id out of range")

# ホワイトリスト方式
allowed_columns = {'user_id', 'user_name', 'email'}
if column not in allowed_columns:
    raise ValueError(f"Invalid column: {column}")
```

#### 3. ✓ 最小権限の原則

```sql
-- ❌ すべての権限を付与
GRANT ALL PRIVILEGES ON DATABASE mydb TO app_user;

-- ✅ 必要な権限のみ
GRANT SELECT, INSERT, UPDATE ON users TO app_user;
GRANT SELECT ON products TO app_user;  -- 参照のみ
```

#### 4. ✓ 機密データの暗号化

```sql
-- パスワードのハッシュ化（PostgreSQL）
INSERT INTO users (username, password_hash)
VALUES ('john', crypt('password123', gen_salt('bf')));

-- 検証
SELECT (password_hash = crypt('input_password', password_hash)) AS valid
FROM users WHERE username = 'john';

-- 機密カラムの暗号化
CREATE TABLE user_profiles (
    user_id INT PRIMARY KEY,
    ssn_encrypted BYTEA,  -- 暗号化された社会保障番号
    credit_card_encrypted BYTEA
);
```

#### 5. ✓ Row Level Security (RLS) の活用

```sql
-- PostgreSQL RLS設定
ALTER TABLE documents ENABLE ROW LEVEL SECURITY;

-- ユーザーは自分のドキュメントのみアクセス可能
CREATE POLICY user_documents ON documents
    FOR ALL
    USING (owner_id = current_user_id());

-- マネージャーは部署のドキュメントにアクセス可能
CREATE POLICY manager_documents ON documents
    FOR ALL
    USING (
        department_id IN (
            SELECT department_id 
            FROM users 
            WHERE user_id = current_user_id()
              AND role = 'manager'
        )
    );
```

#### 6. ✓ 監査ログの記録

```sql
-- 監査ログテーブル
CREATE TABLE audit_logs (
    log_id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(100) NOT NULL,
    operation VARCHAR(10) NOT NULL,  -- INSERT, UPDATE, DELETE
    user_id INT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    old_values JSONB,
    new_values JSONB,
    ip_address INET
);

-- トリガーで自動記録
CREATE OR REPLACE FUNCTION audit_trigger_func()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO audit_logs (table_name, operation, user_id, old_values, new_values)
    VALUES (
        TG_TABLE_NAME,
        TG_OP,
        current_setting('app.user_id')::INT,
        CASE WHEN TG_OP = 'DELETE' THEN row_to_json(OLD) ELSE NULL END,
        CASE WHEN TG_OP IN ('INSERT', 'UPDATE') THEN row_to_json(NEW) ELSE NULL END
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

#### 7. ✓ 接続文字列の安全な管理

```python
# ❌ ハードコード
conn = psycopg2.connect(
    "host=localhost dbname=mydb user=admin password=admin123"
)

# ✅ 環境変数
import os
conn = psycopg2.connect(
    host=os.environ['DB_HOST'],
    database=os.environ['DB_NAME'],
    user=os.environ['DB_USER'],
    password=os.environ['DB_PASSWORD']
)
```

#### 8. ✓ データマスキング（本番データの保護）

```sql
-- ビューでマスキング
CREATE VIEW masked_users AS
SELECT 
    user_id,
    user_name,
    -- メールの一部をマスク
    CONCAT(
        LEFT(email, 2),
        '***@',
        SUBSTRING(email FROM POSITION('@' IN email) + 1)
    ) AS email,
    -- クレジットカード番号の下4桁のみ表示
    CONCAT('****-****-****-', RIGHT(credit_card, 4)) AS credit_card_masked
FROM users;
```

---

### B. パフォーマンス (9-16)

#### 9. ✓ SELECT * を避ける

```sql
-- ❌ すべてのカラムを取得
SELECT * FROM users WHERE user_id = 123;

-- ✅ 必要なカラムのみ
SELECT user_id, user_name, email FROM users WHERE user_id = 123;
```

**理由**:
- ネットワーク転送量削減
- インデックスオンリースキャン活用可能
- アプリケーション側のメモリ使用量削減

#### 10. ✓ WHERE句でインデックスを活用

```sql
-- ❌ インデックスが使えない
WHERE YEAR(created_at) = 2024
WHERE LOWER(email) = 'john@example.com'
WHERE user_id + 10 = 123

-- ✅ インデックスが効く
WHERE created_at >= '2024-01-01' AND created_at < '2025-01-01'
WHERE email = 'john@example.com'  -- emailにインデックス
WHERE user_id = 113
```

#### 11. ✓ N+1問題の回避

```python
# ❌ N+1問題
users = db.execute("SELECT * FROM users LIMIT 100")
for user in users:
    # 100回クエリ実行!
    orders = db.execute(
        "SELECT * FROM orders WHERE user_id = ?", 
        (user['user_id'],)
    )

# ✅ JOINで一括取得
query = """
SELECT 
    u.user_id, u.user_name, u.email,
    o.order_id, o.total_amount, o.created_at
FROM users u
LEFT JOIN orders o ON u.user_id = o.user_id
WHERE u.user_id IN (SELECT user_id FROM users LIMIT 100)
"""
results = db.execute(query)
```

#### 12. ✓ 適切なインデックスの作成

```sql
-- 単一カラムインデックス
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- 複合インデックス (順序重要!)
CREATE INDEX idx_orders_user_created 
ON orders(user_id, created_at DESC);

-- 部分インデックス
CREATE INDEX idx_active_users 
ON users(user_id) 
WHERE status = 'active';

-- カバリングインデックス
CREATE INDEX idx_users_covering 
ON users(user_id, user_name, email);
```

#### 13. ✓ LIMIT/OFFSETの最適化

```sql
-- ❌ 大きなOFFSETは遅い
SELECT * FROM users 
ORDER BY created_at DESC 
LIMIT 20 OFFSET 100000;  -- 100,000件スキップ

-- ✅ カーソルベースページネーション
SELECT * FROM users 
WHERE created_at < '2024-10-01 12:00:00'  -- 前ページの最終値
ORDER BY created_at DESC 
LIMIT 20;
```

#### 14. ✓ EXPLAINでクエリ分析

```sql
-- 実行計画確認
EXPLAIN ANALYZE
SELECT u.user_name, COUNT(o.order_id) as order_count
FROM users u
LEFT JOIN orders o ON u.user_id = o.user_id
WHERE u.created_at >= '2024-01-01'
GROUP BY u.user_id, u.user_name;

-- 確認項目:
-- - Seq Scan (シーケンシャルスキャン) → インデックス追加検討
-- - Nested Loop → JOINアルゴリズム確認
-- - actual time → 実際の実行時間
```

#### 15. ✓ サブクエリよりCTE/JOIN

```sql
-- ❌ 相関サブクエリ（遅い）
SELECT 
    u.user_id,
    u.user_name,
    (SELECT COUNT(*) FROM orders WHERE user_id = u.user_id) as order_count
FROM users u;

-- ✅ JOINで最適化
SELECT 
    u.user_id,
    u.user_name,
    COUNT(o.order_id) as order_count
FROM users u
LEFT JOIN orders o ON u.user_id = o.user_id
GROUP BY u.user_id, u.user_name;

-- ✅ または CTE使用
WITH order_counts AS (
    SELECT user_id, COUNT(*) as cnt
    FROM orders
    GROUP BY user_id
)
SELECT u.user_id, u.user_name, COALESCE(oc.cnt, 0) as order_count
FROM users u
LEFT JOIN order_counts oc ON u.user_id = oc.user_id;
```

#### 16. ✓ バッチ処理の活用

```sql
-- ❌ 1件ずつINSERT
INSERT INTO logs (message) VALUES ('log1');
INSERT INTO logs (message) VALUES ('log2');
INSERT INTO logs (message) VALUES ('log3');

-- ✅ バッチINSERT
INSERT INTO logs (message) VALUES 
('log1'),
('log2'),
('log3'),
... (最大1000件程度);

-- ✅ バッチUPDATE
UPDATE orders 
SET status = 'shipped', updated_at = CURRENT_TIMESTAMP
WHERE order_id IN (123, 456, 789, ...);
```

---

### C. データ整合性 (17-20)

#### 17. ✓ 適切な制約の定義

```sql
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    age INT CHECK (age >= 0 AND age <= 150),
    status VARCHAR(20) CHECK (status IN ('active', 'inactive', 'suspended')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 外部キー制約
CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

#### 18. ✓ トランザクションの適切な使用

```python
# ✅ トランザクションでアトミック性確保
try:
    conn.begin()
    
    # 在庫減少
    cursor.execute(
        "UPDATE products SET stock = stock - %s WHERE product_id = %s",
        (quantity, product_id)
    )
    
    # 注文作成
    cursor.execute(
        "INSERT INTO orders (user_id, product_id, quantity) VALUES (%s, %s, %s)",
        (user_id, product_id, quantity)
    )
    
    conn.commit()
except Exception as e:
    conn.rollback()
    raise
```

#### 19. ✓ デッドロック対策

```sql
-- ✅ 常に同じ順序でロック取得
BEGIN;
-- 常に user_id の昇順でロック
UPDATE accounts SET balance = balance - 100 
WHERE user_id = 1 FOR UPDATE;

UPDATE accounts SET balance = balance + 100 
WHERE user_id = 2 FOR UPDATE;
COMMIT;

-- タイムアウト設定
SET lock_timeout = '5s';
```

#### 20. ✓ 適切なデータ型選択

```sql
-- ❌ 不適切なデータ型
CREATE TABLE bad_table (
    id VARCHAR(255),  -- ❌ 数値IDを文字列で
    price VARCHAR(50),  -- ❌ 金額を文字列で
    created_at VARCHAR(100),  -- ❌ 日時を文字列で
    is_active VARCHAR(10)  -- ❌ 真偽値を文字列で
);

-- ✅ 適切なデータ型
CREATE TABLE good_table (
    id INT PRIMARY KEY,  -- または BIGINT
    price DECIMAL(10,2),  -- 金額は DECIMAL
    created_at TIMESTAMP,  -- 日時は TIMESTAMP
    is_active BOOLEAN  -- 真偽値は BOOLEAN
);
```

---

### D. エラーハンドリング (21-25)

#### 21. ✓ 構造化されたエラーハンドリング

```sql
-- PostgreSQL 例外処理
CREATE OR REPLACE FUNCTION transfer_funds(
    from_account_id INT,
    to_account_id INT,
    amount DECIMAL
) RETURNS VOID AS $$
DECLARE
    from_balance DECIMAL;
BEGIN
    -- 残高チェック
    SELECT balance INTO from_balance 
    FROM accounts 
    WHERE account_id = from_account_id 
    FOR UPDATE;
    
    IF from_balance < amount THEN
        RAISE EXCEPTION 'INSUFFICIENT_FUNDS: Balance % is less than %', 
            from_balance, amount;
    END IF;
    
    -- 送金処理
    UPDATE accounts SET balance = balance - amount 
    WHERE account_id = from_account_id;
    
    UPDATE accounts SET balance = balance + amount 
    WHERE account_id = to_account_id;
    
EXCEPTION
    WHEN OTHERS THEN
        -- エラーログ記録
        INSERT INTO error_logs (error_code, error_message, occurred_at)
        VALUES (SQLSTATE, SQLERRM, CURRENT_TIMESTAMP);
        RAISE;
END;
$$ LANGUAGE plpgsql;
```

#### 22. ✓ セーブポイントによる部分ロールバック

```sql
BEGIN;
    INSERT INTO orders (user_id, total_amount) VALUES (123, 1000);
    
    SAVEPOINT before_inventory_update;
    
    UPDATE inventory SET stock = stock - 1 WHERE product_id = 456;
    
    -- エラー発生時は在庫更新のみロールバック
    IF (SELECT stock FROM inventory WHERE product_id = 456) < 0 THEN
        ROLLBACK TO SAVEPOINT before_inventory_update;
        -- 注文はキャンセル扱いに
        UPDATE orders SET status = 'cancelled' WHERE order_id = LAST_INSERT_ID();
    END IF;
COMMIT;
```

#### 23. ✓ エラーログテーブルの活用

```sql
CREATE TABLE error_logs (
    error_id BIGSERIAL PRIMARY KEY,
    error_code VARCHAR(50),
    error_message TEXT,
    error_detail TEXT,
    sql_state VARCHAR(10),
    context_info JSONB,  -- エラー発生時のコンテキスト
    user_id INT,
    occurred_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_error_occurred (occurred_at),
    INDEX idx_error_code (error_code)
);
```

#### 24. ✓ リトライメカニズム

```python
import time
from psycopg2 import OperationalError

def execute_with_retry(cursor, query, params, max_retries=3):
    """デッドロックやタイムアウト時にリトライ"""
    for attempt in range(max_retries):
        try:
            cursor.execute(query, params)
            return
        except OperationalError as e:
            if 'deadlock' in str(e).lower() and attempt < max_retries - 1:
                time.sleep(0.1 * (2 ** attempt))  # 指数バックオフ
                continue
            raise
```

#### 25. ✓ ユーザーフレンドリーなエラーメッセージ

```python
try:
    cursor.execute(query, params)
except psycopg2.IntegrityError as e:
    if 'unique constraint' in str(e):
        raise UserFriendlyError("このメールアドレスは既に登録されています")
    elif 'foreign key constraint' in str(e):
        raise UserFriendlyError("指定されたユーザーIDが存在しません")
    else:
        # 技術的な詳細はログに記録、ユーザーには汎用メッセージ
        logger.error(f"Database error: {e}")
        raise UserFriendlyError("データベースエラーが発生しました")
```

---

## 🚫 よくある間違いと修正方法

### 間違い1: WHERE句での関数使用によるインデックス無効化

❌ **悪い例**:
```sql
SELECT * FROM users 
WHERE LOWER(email) = 'john@example.com';
-- インデックスが効かない!
```

✅ **良い例**:
```sql
-- 方法1: アプリ側で正規化
SELECT * FROM users 
WHERE email = 'john@example.com';  -- すべて小文字で保存済み

-- 方法2: 関数インデックス作成
CREATE INDEX idx_users_email_lower ON users(LOWER(email));
SELECT * FROM users 
WHERE LOWER(email) = 'john@example.com';
```

---

### 間違い2: トランザクション内での長時間処理

❌ **悪い例**:
```python
conn.begin()
for i in range(10000):
    cursor.execute("INSERT INTO logs VALUES (%s)", (f'log_{i}',))
    time.sleep(0.01)  # 外部API呼び出しなど
conn.commit()  # ロック保持時間が長すぎる!
```

✅ **良い例**:
```python
# 方法1: トランザクションを小さく
for i in range(0, 10000, 100):
    conn.begin()
    batch = [(f'log_{j}',) for j in range(i, min(i+100, 10000))]
    cursor.executemany("INSERT INTO logs VALUES (%s)", batch)
    conn.commit()

# 方法2: 外部処理はトランザクション外で
results = []
for i in range(10000):
    result = external_api_call()  # トランザクション外
    results.append(result)

# まとめてINSERT
conn.begin()
cursor.executemany("INSERT INTO logs VALUES (%s, %s)", results)
conn.commit()
```

---

### 間違い3: 正規化不足によるデータ重複

❌ **悪い例**:
```sql
CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    user_name VARCHAR(100),  -- ❌ ユーザー情報が重複
    user_email VARCHAR(255),  -- ❌ 
    product_name VARCHAR(200),  -- ❌ 商品情報が重複
    product_price DECIMAL(10,2),  -- ❌
    quantity INT
);
```

✅ **良い例**:
```sql
CREATE TABLE users (
    user_id INT PRIMARY KEY,
    user_name VARCHAR(100),
    email VARCHAR(255) UNIQUE
);

CREATE TABLE products (
    product_id INT PRIMARY KEY,
    product_name VARCHAR(200),
    price DECIMAL(10,2)
);

CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    user_id INT REFERENCES users(user_id),
    product_id INT REFERENCES products(product_id),
    quantity INT,
    -- 注文時の価格を記録（履歴保持）
    price_at_order DECIMAL(10,2)
);
```

---

## 🔒 セキュリティチェックリスト

- [ ] すべてのクエリがパラメータ化されている
- [ ] ユーザー入力がバリデーションされている
- [ ] 最小権限の原則が適用されている
- [ ] パスワードがハッシュ化されている
- [ ] 機密データが暗号化されている
- [ ] Row Level Security が必要な箇所に適用されている
- [ ] 監査ログが記録されている
- [ ] 接続文字列が環境変数で管理されている

---

## 📊 パフォーマンスチェックリスト

- [ ] SELECT * を避けている
- [ ] WHERE句でインデックスが効く形式になっている
- [ ] N+1問題が発生していない
- [ ] 適切なインデックスが作成されている
- [ ] EXPLAINで実行計画を確認済み
- [ ] LIMIT/OFFSETが最適化されている
- [ ] バッチ処理が活用されている
- [ ] 不要なJOINがない

---

## 🔗 詳細ドキュメントへのリンク

- [01-syntax-and-style.md](./01-syntax-and-style.md) - 構文とスタイル
- [02-database-design.md](./02-database-design.md) - DB設計
- [03-query-optimization.md](./03-query-optimization.md) - 最適化
- [04-security-access-control.md](./04-security-access-control.md) - セキュリティ
- [05-error-handling.md](./05-error-handling.md) - エラー処理
- [06-testing-qa.md](./06-testing-qa.md) - テスト
- [07-operations-monitoring.md](./07-operations-monitoring.md) - 運用
- [09-documentation-standards.md](./09-documentation-standards.md) - ドキュメント

---

**このドキュメントについて**: このクイックリファレンスは、AI・人間の両方が効率的にSQL開発標準を活用できるように作成されました。
