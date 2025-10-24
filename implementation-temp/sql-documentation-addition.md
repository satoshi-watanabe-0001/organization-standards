# SQL ドキュメンテーション標準追加セクション

---

## X. ドキュメンテーション標準（Documentation Standards）

### X.1 SQL コメント必須要件

#### **適用範囲**

**Level 1: 必須（品質ゲート）**
- すべてのテーブル定義
- すべてのビュー定義
- すべてのストアドプロシージャ・関数
- すべてのトリガー
- すべての重要なカラム（特にビジネスロジックに関連）

**Level 2: 強く推奨**
- 複雑なクエリ（JOIN が3つ以上、サブクエリを含む等）
- ビジネスルール・制約を反映したクエリ
- パフォーマンス最適化のための特殊な実装
- データ移行・バッチ処理スクリプト

**Level 3: 任意**
- 単純な SELECT/INSERT/UPDATE/DELETE
- 自己説明的な単純クエリ

---

### X.2 SQL コメント標準形式

#### **スクリプトファイルヘッダー（必須）**

```sql
-- ============================================================================
-- Script: 001_create_users_table.sql
-- 
-- Description: ユーザー情報を格納するテーブルの作成
--              認証・認可に必要な基本情報とプロフィール情報を管理
-- 
-- Author: システム開発チーム
-- Created: 2024-10-23
-- Version: 1.0
-- 
-- Dependencies:
--   - roles テーブル（外部キー制約）
-- 
-- Notes:
--   - email カラムにはユニーク制約を設定
--   - パスワードはハッシュ化された状態で保存
--   - created_at, updated_at は自動更新トリガーで管理
-- ============================================================================
```

**必須要素**:
- `Script`: ファイル名
- `Description`: スクリプトの目的・責任
- `Created`: 作成日

**推奨要素**:
- `Author`: 作成者（オプション、Git管理も可）
- `Version`: バージョン
- `Dependencies`: 依存関係
- `Notes`: 注意事項

---

#### **テーブル定義コメント（必須）**

```sql
-- ユーザー情報テーブル
-- 
-- システム利用者の基本情報とプロフィールを管理する。
-- 認証・認可の基盤となるマスタテーブル。
CREATE TABLE users (
    -- ユーザーID（プライマリキー、UUID形式）
    user_id         VARCHAR(36)     NOT NULL PRIMARY KEY,
    
    -- メールアドレス（ログイン識別子、ユニーク制約）
    email           VARCHAR(255)    NOT NULL UNIQUE,
    
    -- パスワードハッシュ（bcrypt、ソルト付き）
    -- 平文パスワードは保存しない
    password_hash   VARCHAR(255)    NOT NULL,
    
    -- ユーザー名（表示名）
    username        VARCHAR(100)    NOT NULL,
    
    -- ロールID（外部キー: roles.role_id）
    -- デフォルトは 'user' ロール
    role_id         VARCHAR(36)     NOT NULL 
                    REFERENCES roles(role_id),
    
    -- アカウント状態（active, suspended, deleted）
    status          VARCHAR(20)     NOT NULL DEFAULT 'active',
    
    -- 最終ログイン日時
    -- NULL = 一度もログインしていない
    last_login_at   TIMESTAMP       NULL,
    
    -- レコード作成日時（自動設定）
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- レコード更新日時（自動更新、トリガーで管理）
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- インデックス: メール検索の高速化
CREATE INDEX idx_users_email ON users(email);

-- インデックス: ロール別ユーザー検索の高速化
CREATE INDEX idx_users_role ON users(role_id);

-- インデックス: アクティブユーザー検索の最適化
CREATE INDEX idx_users_status ON users(status) 
WHERE status = 'active';
```

**必須要素**:
- テーブルの目的・責任
- 各カラムの説明（特にビジネス的意味）
- 外部キー関係の説明
- 制約の理由

**推奨要素**:
- デフォルト値の説明
- NULL許可の理由
- インデックスの目的

---

#### **ビュー定義コメント（必須）**

```sql
-- ============================================================================
-- View: user_activity_summary
-- 
-- Description: ユーザーごとのアクティビティサマリを集計するビュー
--              ダッシュボード表示とレポート生成に使用
-- 
-- Columns:
--   - user_id: ユーザーID
--   - username: ユーザー名
--   - last_login_at: 最終ログイン日時
--   - total_logins: 累計ログイン回数
--   - active_days: アクティブ日数（過去30日）
--   - last_action: 最後のアクション種別
-- 
-- Dependencies:
--   - users テーブル
--   - login_logs テーブル
--   - user_actions テーブル
-- 
-- Performance:
--   - login_logs.user_id にインデックスが必要
--   - user_actions.user_id にインデックスが必要
--   - 大量データの場合はマテリアライズドビューを検討
-- 
-- Notes:
--   - 削除済みユーザー（status = 'deleted'）は除外
--   - 30日以内のデータのみ集計
-- ============================================================================

CREATE VIEW user_activity_summary AS
SELECT 
    u.user_id,
    u.username,
    u.last_login_at,
    
    -- 累計ログイン回数
    COUNT(DISTINCT ll.login_id) AS total_logins,
    
    -- アクティブ日数（過去30日、重複除外）
    COUNT(DISTINCT DATE(ua.action_timestamp)) AS active_days,
    
    -- 最後のアクション種別
    (
        SELECT action_type 
        FROM user_actions 
        WHERE user_id = u.user_id 
        ORDER BY action_timestamp DESC 
        LIMIT 1
    ) AS last_action

FROM users u
LEFT JOIN login_logs ll 
    ON u.user_id = ll.user_id
    AND ll.login_timestamp >= CURRENT_DATE - INTERVAL '30 days'
LEFT JOIN user_actions ua 
    ON u.user_id = ua.user_id
    AND ua.action_timestamp >= CURRENT_DATE - INTERVAL '30 days'

WHERE u.status != 'deleted'  -- 削除済みユーザーを除外

GROUP BY u.user_id, u.username, u.last_login_at;
```

**必須要素**:
- ビューの目的・使用用途
- 各カラムの意味
- 依存テーブル
- パフォーマンスに関する注意事項

---

#### **ストアドプロシージャ・関数コメント（必須）**

```sql
-- ============================================================================
-- Function: calculate_order_total
-- 
-- Description: 注文の合計金額を計算する
--              商品価格、数量、割引、税金を考慮して最終金額を算出
-- 
-- Parameters:
--   @order_id (VARCHAR(36)): 注文ID
-- 
-- Returns:
--   DECIMAL(10,2): 合計金額（税込）
--   NULL: 注文が存在しない、または無効な場合
-- 
-- Business Rules:
--   1. 基本金額 = Σ(商品価格 × 数量)
--   2. 割引適用 = 基本金額 × (1 - 割引率)
--   3. 税額計算 = 割引後金額 × 税率
--   4. 最終金額 = 割引後金額 + 税額
--   5. 小数点以下2桁で四捨五入
-- 
-- Exceptions:
--   - 注文が存在しない場合: NULL を返す
--   - 商品が削除済みの場合: 計算から除外
-- 
-- Example:
--   SELECT calculate_order_total('order-123');
--   -- Returns: 15840.00
-- 
-- Performance:
--   - order_items.order_id にインデックスが必要
--   - 大量の注文アイテムがある場合は処理時間に注意
-- 
-- Notes:
--   - トランザクション内で実行推奨
--   - 並行実行時の整合性は呼び出し側で保証すること
-- ============================================================================

CREATE FUNCTION calculate_order_total(
    @order_id VARCHAR(36)
)
RETURNS DECIMAL(10,2)
AS
BEGIN
    DECLARE @total DECIMAL(10,2);
    DECLARE @subtotal DECIMAL(10,2);
    DECLARE @discount_rate DECIMAL(5,2);
    DECLARE @tax_rate DECIMAL(5,2);
    
    -- 注文の存在チェック
    IF NOT EXISTS (SELECT 1 FROM orders WHERE order_id = @order_id) BEGIN
        RETURN NULL;
    END
    
    -- 基本金額の計算（削除済み商品を除外）
    SELECT @subtotal = SUM(oi.unit_price * oi.quantity)
    FROM order_items oi
    INNER JOIN products p ON oi.product_id = p.product_id
    WHERE oi.order_id = @order_id
      AND p.status != 'deleted';
    
    -- 割引率の取得
    SELECT @discount_rate = COALESCE(discount_rate, 0)
    FROM orders
    WHERE order_id = @order_id;
    
    -- 税率の取得（デフォルト10%）
    SET @tax_rate = 0.10;
    
    -- 最終金額の計算
    -- 1. 割引適用
    SET @total = @subtotal * (1 - @discount_rate);
    
    -- 2. 税額追加
    SET @total = @total * (1 + @tax_rate);
    
    -- 3. 四捨五入
    SET @total = ROUND(@total, 2);
    
    RETURN @total;
END;
```

**必須要素**:
- 関数/プロシージャの目的
- すべてのパラメータの説明
- 戻り値の説明
- ビジネスルールの明記

**推奨要素**:
- 使用例
- 例外処理の説明
- パフォーマンス注意事項

---

#### **トリガーコメント（必須）**

```sql
-- ============================================================================
-- Trigger: trg_users_updated_at
-- 
-- Description: users テーブルの updated_at カラムを自動更新
--              UPDATE 実行時に現在時刻を設定
-- 
-- Timing: BEFORE UPDATE
-- 
-- Purpose:
--   レコード更新時に updated_at を自動的に現在時刻で更新し、
--   変更履歴の追跡を容易にする
-- 
-- Notes:
--   - NEW.updated_at を明示的に設定した場合でも上書きされる
--   - タイムゾーンは UTC で統一
-- ============================================================================

CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END;
```

---

#### **複雑なクエリのコメント（推奨）**

```sql
-- ユーザーの月次アクティビティレポートを生成
-- 
-- 目的: 各ユーザーの月ごとのアクティビティ指標を集計
--       - ログイン回数
--       - アクティブ日数
--       - 実行したアクション数
-- 
-- パフォーマンス: 
--   - login_logs.user_id, login_logs.login_timestamp にインデックス必須
--   - user_actions.user_id, user_actions.action_timestamp にインデックス必須
--   - 年月でパーティショニング推奨
SELECT 
    u.user_id,
    u.username,
    DATE_FORMAT(ll.login_timestamp, '%Y-%m') AS month,
    
    -- 月間ログイン回数
    COUNT(DISTINCT ll.login_id) AS login_count,
    
    -- 月間アクティブ日数
    COUNT(DISTINCT DATE(ua.action_timestamp)) AS active_days,
    
    -- 月間アクション実行回数
    COUNT(ua.action_id) AS action_count

FROM users u

-- ログイン履歴との結合
-- 過去12ヶ月のデータのみ対象
LEFT JOIN login_logs ll
    ON u.user_id = ll.user_id
    AND ll.login_timestamp >= DATE_SUB(CURRENT_DATE, INTERVAL 12 MONTH)

-- ユーザーアクション履歴との結合
-- 過去12ヶ月のデータのみ対象
LEFT JOIN user_actions ua
    ON u.user_id = ua.user_id
    AND ua.action_timestamp >= DATE_SUB(CURRENT_DATE, INTERVAL 12 MONTH)

-- アクティブユーザーのみ対象
WHERE u.status = 'active'

GROUP BY u.user_id, u.username, DATE_FORMAT(ll.login_timestamp, '%Y-%m')

-- 月の新しい順にソート
ORDER BY month DESC, u.username;
```

---

### X.3 コメントスタイルガイド

#### **推奨スタイル**

```sql
-- ✅ Good: 明確なセクション区切り
-- ============================================================================
-- テーブル定義: users
-- ============================================================================

-- ✅ Good: カラム単位のインラインコメント
user_id VARCHAR(36) NOT NULL,  -- ユーザーID（UUID形式）

-- ✅ Good: 複数行コメントでビジネスルールを説明
-- パスワードハッシュは bcrypt アルゴリズムで生成
-- ソルトは自動生成され、ハッシュに含まれる
-- 最小長は60文字
password_hash VARCHAR(255) NOT NULL,

-- ✅ Good: クエリの各部分を説明
-- ステップ1: アクティブユーザーのみ抽出
WHERE status = 'active'
-- ステップ2: 過去30日以内のデータに限定
  AND created_at >= CURRENT_DATE - INTERVAL '30 days'
```

#### **避けるべきスタイル**

```sql
-- ❌ Bad: コメントなし
CREATE TABLE users (
    user_id VARCHAR(36) NOT NULL,
    email VARCHAR(255) NOT NULL
);

-- ❌ Bad: 自明な内容の繰り返し
-- ユーザーIDを選択する
SELECT user_id FROM users;

-- ❌ Bad: 不明確・曖昧なコメント
-- データを処理する
WHERE status = 'active';
```

---

### X.4 コードレビューチェックリスト

**レビュアーは以下を確認**:

#### **必須項目**
- [ ] スクリプトファイルにヘッダーコメントがあるか
- [ ] テーブル定義にテーブル説明があるか
- [ ] 重要なカラムに説明があるか
- [ ] ビュー・ストアドプロシージャに完全なコメントがあるか

#### **品質項目**
- [ ] ビジネスルール・制約が明記されているか
- [ ] 外部キー関係が説明されているか
- [ ] インデックスの目的が明確か
- [ ] パフォーマンス注意事項が記載されているか
- [ ] 複雑なクエリ（JOIN 3つ以上）に説明があるか

#### **データベース設計**
- [ ] テーブル間の関係が明確か
- [ ] NULL許可の理由が説明されているか
- [ ] デフォルト値の意図が明確か

---

### X.5 ベストプラクティス

#### **✅ Good Examples**

```sql
-- ============================================================================
-- View: high_value_customers
-- 
-- Description: 高額購入顧客のビュー
--              過去12ヶ月で10万円以上購入した顧客を抽出
-- 
-- Business Rule:
--   - 高額購入基準: 累計購入額 >= 100,000円
--   - 対象期間: 過去12ヶ月
--   - キャンセル注文は除外
-- 
-- Usage:
--   マーケティングキャンペーン対象者の選定
--   VIP顧客向けサービスの提供判断
-- 
-- Performance:
--   - orders.customer_id にインデックス必須
--   - orders.order_date にインデックス推奨
--   - マテリアライズドビューの検討推奨（日次更新）
-- ============================================================================

CREATE VIEW high_value_customers AS
SELECT 
    c.customer_id,
    c.customer_name,
    c.email,
    
    -- 過去12ヶ月の累計購入額
    SUM(o.total_amount) AS total_purchase_amount,
    
    -- 過去12ヶ月の注文回数
    COUNT(o.order_id) AS order_count,
    
    -- 最終購入日
    MAX(o.order_date) AS last_purchase_date

FROM customers c
INNER JOIN orders o 
    ON c.customer_id = o.customer_id
    
WHERE 
    -- 過去12ヶ月の注文のみ対象
    o.order_date >= CURRENT_DATE - INTERVAL '12 months'
    
    -- キャンセル注文を除外
    AND o.status != 'cancelled'

GROUP BY c.customer_id, c.customer_name, c.email

-- 高額購入基準: 10万円以上
HAVING SUM(o.total_amount) >= 100000

-- 購入額の降順
ORDER BY total_purchase_amount DESC;
```

#### **❌ Bad Examples**

```sql
-- ❌ コメントなし
CREATE TABLE orders (
    order_id VARCHAR(36),
    customer_id VARCHAR(36),
    total_amount DECIMAL(10,2)
);

-- ❌ 不十分なコメント
-- 注文テーブル
CREATE TABLE orders (
    order_id VARCHAR(36),
    customer_id VARCHAR(36)
);

-- ❌ 複雑なクエリにコメントなし
SELECT u.user_id, COUNT(o.order_id)
FROM users u
LEFT JOIN orders o ON u.user_id = o.user_id
LEFT JOIN order_items oi ON o.order_id = oi.order_id
LEFT JOIN products p ON oi.product_id = p.product_id
WHERE o.status = 'completed'
GROUP BY u.user_id
HAVING COUNT(o.order_id) > 10;
```

---

### X.6 まとめ

**必須ルール**:
1. すべてのスクリプトファイルにヘッダーコメント
2. すべてのテーブル・ビューに目的説明
3. すべてのストアドプロシージャ・関数に完全なコメント
4. 重要なカラムに説明（特にビジネスロジック関連）

**推奨プラクティス**:
1. 複雑なクエリ（JOIN 3つ以上）にコメント
2. ビジネスルール・制約の明記
3. パフォーマンス注意事項の記載
4. インデックスの目的説明
5. 外部キー関係の説明

**コードレビューで不合格となる条件**:
- テーブル・ビューにコメントがない
- ストアドプロシージャに説明がない
- 複雑なクエリにコメントがない
- ビジネスルールが不明確

---

**このセクションを `sql-standards.md` の末尾に追加してください。**
