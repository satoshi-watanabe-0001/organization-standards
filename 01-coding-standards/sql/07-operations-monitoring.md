# SQL 運用・監視・メンテナンス

**このドキュメントについて**: SQL コーディング規約 - 運用・監視・メンテナンス

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

