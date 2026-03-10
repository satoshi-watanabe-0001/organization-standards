# SQL セキュリティ・権限管理

**このドキュメントについて**: SQL コーディング規約 - セキュリティ・権限管理

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

