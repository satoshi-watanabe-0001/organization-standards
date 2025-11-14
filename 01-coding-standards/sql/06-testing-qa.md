# SQL テスト・品質保証

**このドキュメントについて**: SQL コーディング規約 - テスト・品質保証

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

