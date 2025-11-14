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

