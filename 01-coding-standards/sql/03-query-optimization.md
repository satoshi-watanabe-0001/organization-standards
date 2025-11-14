# SQL クエリ最適化・パフォーマンス

**このドキュメントについて**: SQL コーディング規約 - クエリ最適化・パフォーマンス

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

