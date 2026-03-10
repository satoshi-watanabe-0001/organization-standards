# SQL エラーハンドリング・例外処理

**このドキュメントについて**: SQL コーディング規約 - エラーハンドリング・例外処理

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

