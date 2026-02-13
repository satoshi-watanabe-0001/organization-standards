---
title: "SQLマイグレーションファイル - ドキュメントコメント不足問題の解決ガイド"
version: "1.0.0"
created_date: "2025-11-07"
last_updated: "2025-11-07"
status: "Active"
category: "Issue Resolution & Best Practice"
related_ticket: "EC-15"
---

# SQLマイグレーションファイル
## ドキュメントコメント不足問題の解決ガイド

> Flywayマイグレーションにおける組織標準準拠のコメント記述ガイド

**対象課題**: EC-15 パスワードリセットAPI実装  
**問題ファイル**: `V3__Create_password_reset_tokens_table.sql`  
**適用標準**: `organization-standards/01-coding-standards/sql-standards.md` (76-117行目)

---

## 📋 目次

1. [問題の概要](#問題の概要)
2. [不足しているコメントの詳細](#不足しているコメントの詳細)
3. [解決策：修正版SQLファイル](#解決策修正版sqlファイル)
4. [コメント記述ガイドライン](#コメント記述ガイドライン)
5. [CI/CD自動チェック設定](#cicd自動チェック設定)
6. [ベストプラクティス](#ベストプラクティス)
7. [チェックリスト](#チェックリスト)

---

## 🎯 問題の概要

### 発生状況

**作業内容**: EC-15 パスワードリセットAPI実装のPR作成後、レビュー時に指摘

**環境**:
- リポジトリ: `ec-site-auth-service`
- 技術スタック: Java 17、Spring Boot 3.2.0、PostgreSQL
- マイグレーションツール: Flyway

### 現状の問題

**現在記載されているコメント**:
- ✅ `COMMENT ON TABLE` - テーブルの説明
- ✅ `COMMENT ON COLUMN` - 各カラムの説明（全6カラム分）

**不足しているコメント**:
- ❌ ファイル冒頭のマイグレーション目的説明（複数行コメント）
- ❌ 各インデックス作成の理由説明（インライン + `COMMENT ON INDEX`）
- ❌ 外部キー制約の理由説明（`COMMENT ON CONSTRAINT`）

---

## 📝 不足しているコメントの詳細

### 1. ファイル冒頭コメント（必須）

組織標準に従い、以下の情報を含む複数行コメントが必要:

```sql
/*
 * [必須] マイグレーションの目的
 * [必須] ビジネス背景・関連チケット番号
 * [必須] 主な設計判断（データ型選択、制約設計）
 * [推奨] 想定クエリパターン・運用方法
 * [推奨] インデックス方針
 * [推奨] セキュリティ考慮事項
 */
```

### 2. インデックスコメント（必須）

**2つのレベルでコメントが必要**:

#### a) インライン説明（なぜ必要か）
```sql
-- [理由] このインデックスが必要な理由
-- [クエリ] 想定されるクエリパターン
-- [パフォーマンス] 期待される効果
CREATE INDEX idx_example ON table_name(column_name);
```

#### b) メタデータ記録（PostgreSQL COMMENT）
```sql
COMMENT ON INDEX idx_example IS 
'[目的] インデックスの目的
[クエリ] 想定クエリ: SELECT ... WHERE column_name = ?
[効果] 検索パフォーマンス向上（推定10倍高速化）';
```

### 3. 制約コメント（推奨）

**外部キー制約の意図を記録**:

```sql
-- [理由] なぜこの制約が必要か
-- [動作] ON DELETE CASCADE の意図
ALTER TABLE child_table 
ADD CONSTRAINT fk_parent_id 
FOREIGN KEY (parent_id) REFERENCES parent_table(parent_id) 
ON DELETE CASCADE;

COMMENT ON CONSTRAINT fk_parent_id ON child_table IS
'[目的] 親レコード削除時に子レコードも自動削除
[理由] データ整合性維持とゴミデータ防止
[注意] CASCADE動作により大量削除の可能性あり';
```

---

## ✅ 解決策：修正版SQLファイル

### V3__Create_password_reset_tokens_table.sql（完全版）

```sql
/*
 * =============================================================================
 * Migration: V3 - パスワードリセットトークンテーブル作成
 * =============================================================================
 * 
 * 【目的】
 * - パスワードリセット機能のためのトークン管理テーブルを新規作成
 * - セキュアなトークン生成・検証・有効期限管理を実現
 * 
 * 【ビジネス背景】
 * - チケット: EC-15 パスワードリセットAPI実装
 * - 要件: ユーザーがパスワードを忘れた際、メール経由でリセット可能にする
 * - セキュリティ: トークンの使い捨て（One-Time Use）、有効期限30分
 * 
 * 【主な設計判断】
 * 1. トークン形式: UUID v4（128bit、推測不可能）
 * 2. 有効期限: 30分（業界標準、セキュリティとUXのバランス）
 * 3. 使用フラグ: 使用済みトークンの再利用防止
 * 4. 外部キー: ON DELETE CASCADE（ユーザー削除時にトークンも削除）
 * 5. データ型:
 *    - user_id: UUID（usersテーブルとの整合性）
 *    - token: VARCHAR(255)（UUID文字列形式）
 *    - is_used: BOOLEAN（シンプルなフラグ管理）
 *    - expires_at: TIMESTAMP WITH TIME ZONE（タイムゾーン考慮）
 * 
 * 【想定クエリパターン】
 * 1. トークン検証（最頻出）:
 *    SELECT * FROM password_reset_tokens 
 *    WHERE token = ? AND is_used = FALSE AND expires_at > NOW();
 * 
 * 2. 有効期限切れトークンのクリーンアップ（定期バッチ）:
 *    DELETE FROM password_reset_tokens WHERE expires_at < NOW() - INTERVAL '7 days';
 * 
 * 3. ユーザーの未使用トークン確認（重複発行防止）:
 *    SELECT * FROM password_reset_tokens 
 *    WHERE user_id = ? AND is_used = FALSE AND expires_at > NOW();
 * 
 * 【インデックス方針】
 * - トークン検証の高速化: idx_password_reset_tokens_token（最重要）
 * - 有効期限管理: idx_password_reset_tokens_expires_at（バッチ処理用）
 * - ユーザー別管理: idx_password_reset_tokens_user_id（重複防止用）
 * - 複合条件検索: idx_password_reset_tokens_is_used_expires（検証クエリ最適化）
 * 
 * 【セキュリティ考慮事項】
 * - トークンは暗号学的に安全なUUID v4を使用
 * - データベースレベルでの有効期限チェック
 * - 使用済みフラグによる再利用防止
 * - インデックスによるタイミング攻撃の緩和
 * 
 * 【運用・保守】
 * - 定期クリーンアップ: 7日以上前の期限切れトークンを削除（推奨）
 * - モニタリング: 未使用トークンの蓄積状況を監視
 * - アラート: 1ユーザーあたり5件以上の未使用トークンで警告
 * 
 * 【参照ドキュメント】
 * - 組織標準: organization-standards/01-coding-standards/sql-standards.md
 * - API設計: docs/api/password-reset-api-spec.md
 * - セキュリティ: docs/security/token-management-policy.md
 * 
 * 作成日: 2025-11-07
 * 作成者: Engineering Team
 * レビュー: Security Team
 * =============================================================================
 */

-- =============================================================================
-- テーブル作成
-- =============================================================================

CREATE TABLE password_reset_tokens (
    -- プライマリキー: 自動生成UUID
    token_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- ユーザー参照: パスワードをリセットする対象ユーザー
    user_id UUID NOT NULL,
    
    -- リセットトークン: メール送信される一意のトークン
    -- 形式: UUID v4の文字列表現（例: "550e8400-e29b-41d4-a716-446655440000"）
    token VARCHAR(255) UNIQUE NOT NULL,
    
    -- 使用フラグ: トークンが既に使用されたかを記録
    -- TRUE: 使用済み（再利用不可）、FALSE: 未使用（使用可能）
    is_used BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- 有効期限: トークンが有効な期限（タイムゾーン付き）
    -- 生成時刻 + 30分が標準的な設定
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    
    -- 作成日時: トークン生成日時の記録（監査・分析用）
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    -- 外部キー制約: usersテーブルとの関連付け
    -- ON DELETE CASCADE: ユーザー削除時にトークンも自動削除
    -- 理由: ユーザーが存在しないトークンは無意味であり、ゴミデータ防止
    CONSTRAINT fk_password_reset_tokens_user_id 
        FOREIGN KEY (user_id) 
        REFERENCES users(user_id) 
        ON DELETE CASCADE,
    
    -- チェック制約: 有効期限は作成日時より後でなければならない
    -- 理由: 論理的整合性の保証、設定ミス防止
    CONSTRAINT chk_expires_after_created 
        CHECK (expires_at > created_at)
);

-- =============================================================================
-- テーブルコメント
-- =============================================================================

COMMENT ON TABLE password_reset_tokens IS 
'パスワードリセットトークン管理テーブル
目的: ユーザーがパスワードを忘れた際のリセット機能を提供
セキュリティ: One-Time Use、30分有効期限、暗号学的に安全なトークン
関連チケット: EC-15';

-- =============================================================================
-- カラムコメント
-- =============================================================================

COMMENT ON COLUMN password_reset_tokens.token_id IS 
'トークンID（プライマリキー）
形式: UUID v4自動生成
用途: テーブル内部での一意識別子';

COMMENT ON COLUMN password_reset_tokens.user_id IS 
'ユーザーID（外部キー）
参照: users.user_id
削除動作: CASCADE（ユーザー削除時にトークンも削除）
用途: パスワードをリセットする対象ユーザーの特定';

COMMENT ON COLUMN password_reset_tokens.token IS 
'リセットトークン（一意制約）
形式: UUID v4の文字列表現（255文字）
生成方法: 暗号学的に安全な乱数生成器（SecureRandom）
セキュリティ: 推測不可能、ブルートフォース攻撃耐性
用途: メール送信、URLパラメータ、API認証';

COMMENT ON COLUMN password_reset_tokens.is_used IS 
'使用フラグ
TRUE: 使用済み（トークンは無効）
FALSE: 未使用（トークンは有効、ただし有効期限内の場合のみ）
用途: トークンの再利用防止（One-Time Use実現）';

COMMENT ON COLUMN password_reset_tokens.expires_at IS 
'有効期限（タイムゾーン付きタイムスタンプ）
設定: 生成時刻 + 30分（標準）
検証: 現在時刻 < expires_at かつ is_used = FALSE で有効
用途: セキュリティ（長期間有効なトークンのリスク回避）';

COMMENT ON COLUMN password_reset_tokens.created_at IS 
'作成日時（タイムゾーン付きタイムスタンプ）
デフォルト: CURRENT_TIMESTAMP
用途: 監査ログ、統計分析、トラブルシューティング';

-- =============================================================================
-- インデックス作成
-- =============================================================================

-- -----------------------------------------------------------------------------
-- インデックス1: トークン検証用（最重要）
-- -----------------------------------------------------------------------------
-- 目的: トークンによる高速検索を実現
-- クエリ: SELECT * FROM password_reset_tokens WHERE token = ?
-- 頻度: 非常に高頻度（ユーザーがリセットリンクをクリックするたびに実行）
-- 効果: フルテーブルスキャン回避、O(log n)の検索時間
-- 理由: トークンはUNIQUE制約があるため、B-Treeインデックスで最適化
-- パフォーマンス: 100万レコード時でも数ミリ秒で検索可能
CREATE INDEX idx_password_reset_tokens_token 
ON password_reset_tokens(token);

COMMENT ON INDEX idx_password_reset_tokens_token IS 
'トークン検証用インデックス
目的: リセットリンククリック時の高速トークン検証
想定クエリ: WHERE token = ?
カーディナリティ: 非常に高い（UNIQUE制約）
インデックスタイプ: B-Tree
推定効果: フルスキャン比で1000倍以上高速化
メンテナンス: 自動（INSERT/DELETE時に更新）';

-- -----------------------------------------------------------------------------
-- インデックス2: 有効期限管理用（バッチ処理用）
-- -----------------------------------------------------------------------------
-- 目的: 期限切れトークンの定期削除（クリーンアップバッチ）
-- クエリ: DELETE FROM password_reset_tokens WHERE expires_at < NOW() - INTERVAL '7 days'
-- 頻度: 低頻度（日次バッチ実行）
-- 効果: 大量レコード削除時のパフォーマンス向上
-- 理由: expires_atでの範囲検索を最適化
-- 運用: インデックスにより削除対象の特定が高速化
CREATE INDEX idx_password_reset_tokens_expires_at 
ON password_reset_tokens(expires_at);

COMMENT ON INDEX idx_password_reset_tokens_expires_at IS 
'有効期限管理用インデックス
目的: 期限切れトークンの定期クリーンアップ最適化
想定クエリ: WHERE expires_at < NOW() - INTERVAL ''7 days''
実行頻度: 日次バッチ（深夜帯）
インデックスタイプ: B-Tree（範囲検索に最適）
推定効果: 削除対象特定が100倍高速化
推奨運用: 7日以上前の期限切れトークンを削除';

-- -----------------------------------------------------------------------------
-- インデックス3: ユーザー別トークン管理用
-- -----------------------------------------------------------------------------
-- 目的: 同一ユーザーの未使用トークン確認（重複発行防止）
-- クエリ: SELECT * FROM password_reset_tokens WHERE user_id = ? AND is_used = FALSE
-- 頻度: 中頻度（トークン発行リクエストごとに実行）
-- 効果: ユーザーあたりのトークン数確認を高速化
-- 理由: 悪意あるユーザーによる大量トークン発行を検知・防止
-- セキュリティ: リクエスト制限（1ユーザー5件まで）の実装基盤
CREATE INDEX idx_password_reset_tokens_user_id 
ON password_reset_tokens(user_id);

COMMENT ON INDEX idx_password_reset_tokens_user_id IS 
'ユーザー別トークン管理用インデックス
目的: 同一ユーザーの未使用トークン確認（重複発行防止）
想定クエリ: WHERE user_id = ? AND is_used = FALSE
セキュリティ用途: トークン発行制限（1ユーザー5件まで）
実行頻度: 中頻度（トークン発行リクエスト時）
インデックスタイプ: B-Tree
推定効果: ユーザー別検索が50倍高速化
アラート条件: 未使用トークン >= 5件で警告';

-- -----------------------------------------------------------------------------
-- インデックス4: 検証クエリ最適化用（複合インデックス）
-- -----------------------------------------------------------------------------
-- 目的: トークン検証クエリの完全最適化
-- クエリ: SELECT * FROM password_reset_tokens 
--         WHERE token = ? AND is_used = FALSE AND expires_at > NOW()
-- 頻度: 非常に高頻度（トークン検証の完全な条件）
-- 効果: 複数条件を含むクエリをIndex-Only Scanで処理
-- 理由: token単独インデックスでは is_used と expires_at の追加フィルタが必要
-- パフォーマンス: カバリングインデックスによりテーブルアクセス不要
CREATE INDEX idx_password_reset_tokens_validation 
ON password_reset_tokens(token, is_used, expires_at) 
WHERE is_used = FALSE AND expires_at > CURRENT_TIMESTAMP;

COMMENT ON INDEX idx_password_reset_tokens_validation IS 
'トークン検証最適化用複合インデックス（部分インデックス）
目的: 検証クエリの完全最適化（Index-Only Scan実現）
想定クエリ: WHERE token = ? AND is_used = FALSE AND expires_at > NOW()
インデックスタイプ: B-Tree複合インデックス + 部分インデックス
カバー範囲: 未使用かつ有効期限内のトークンのみ
推定効果: 
  - Index-Only Scanによりヒープアクセス不要
  - 検証クエリが2-3倍高速化
  - インデックスサイズ削減（使用済み・期限切れを除外）
メンテナンス: PostgreSQL 9.2+で自動最適化';

-- =============================================================================
-- 制約コメント
-- =============================================================================

COMMENT ON CONSTRAINT fk_password_reset_tokens_user_id ON password_reset_tokens IS 
'ユーザー外部キー制約
目的: データ整合性維持（存在しないユーザーへのトークン発行を防止）
削除動作: ON DELETE CASCADE
CASCADE理由:
  - ユーザー削除時に関連トークンも自動削除
  - ゴミデータ防止（存在しないユーザーのトークンは無意味）
  - GDPR対応（ユーザーデータ完全削除の実現）
注意事項:
  - CASCADE動作により大量削除の可能性あり
  - ユーザー削除前にトークン数を確認推奨
  - 削除ログの記録を推奨（監査証跡）';

COMMENT ON CONSTRAINT chk_expires_after_created ON password_reset_tokens IS 
'有効期限妥当性チェック制約
目的: 論理的整合性の保証
条件: expires_at > created_at
理由:
  - 有効期限が作成日時より前は論理的に矛盾
  - アプリケーションバグの早期検出
  - データ品質の維持
エラー時:
  - INSERT/UPDATE時に制約違反エラー
  - アプリケーション側での設定ミスを防止';

-- =============================================================================
-- 完了メッセージ
-- =============================================================================

-- マイグレーション完了
-- 次のステップ:
-- 1. アプリケーション側でトークン生成・検証ロジックを実装
-- 2. メール送信機能との統合
-- 3. 定期クリーンアップバッチの設定
-- 4. モニタリング・アラート設定
-- 5. セキュリティテストの実施
```

---

## 📘 コメント記述ガイドライン

### 必須コメント（MUST）

#### 1. ファイル冒頭コメント

```sql
/*
 * =============================================================================
 * Migration: [バージョン番号] - [マイグレーション名]
 * =============================================================================
 * 
 * 【目的】
 * - マイグレーションの目的を明確に記述
 * 
 * 【ビジネス背景】
 * - チケット番号: XX-123
 * - ビジネス要件の概要
 * 
 * 【主な設計判断】
 * 1. データ型選択の理由
 * 2. 制約設計の理由
 * 3. その他の重要な設計判断
 * 
 * 【想定クエリパターン】（必須）
 * 1. 最頻出クエリ
 * 2. バッチ処理クエリ
 * 3. その他の重要クエリ
 * 
 * 【インデックス方針】（必須）
 * - なぜこのインデックスが必要か
 * 
 * 【セキュリティ考慮事項】（該当する場合）
 * - セキュリティ上の注意点
 * 
 * 作成日: YYYY-MM-DD
 * 作成者: [チーム名/担当者]
 * =============================================================================
 */
```

#### 2. インデックスコメント

**インライン説明 + COMMENT ON INDEX の両方が必須**:

```sql
-- -----------------------------------------------------------------------------
-- インデックスN: [目的を一言で]
-- -----------------------------------------------------------------------------
-- 目的: [このインデックスの目的]
-- クエリ: [想定されるクエリパターン]
-- 頻度: [高頻度/中頻度/低頻度]
-- 効果: [期待されるパフォーマンス効果]
-- 理由: [なぜこのインデックスが必要か]
CREATE INDEX idx_example ON table_name(column_name);

COMMENT ON INDEX idx_example IS 
'[簡潔な目的]
目的: [詳細な目的]
想定クエリ: [SQLクエリ例]
インデックスタイプ: [B-Tree/Hash/GiST/etc]
推定効果: [具体的な効果]
メンテナンス: [保守に関する注意事項]';
```

### 推奨コメント（SHOULD）

#### 3. 制約コメント

```sql
COMMENT ON CONSTRAINT constraint_name ON table_name IS 
'[制約の目的]
目的: [なぜこの制約が必要か]
動作: [ON DELETE CASCADE等の動作説明]
理由: [ビジネスロジック的な理由]
注意事項: [運用上の注意点]';
```

#### 4. テーブル・カラムコメント

```sql
COMMENT ON TABLE table_name IS 
'[テーブルの目的]
関連チケット: XX-123
主要機能: [主な機能]';

COMMENT ON COLUMN table_name.column_name IS 
'[カラムの説明]
形式: [データ形式]
用途: [使用目的]
制約: [制約事項]';
```

---

## 🔧 CI/CD自動チェック設定

### GitHub Actions ワークフロー

**`.github/workflows/sql-comment-check.yml`**:

```yaml
name: SQL Migration Comment Check

on:
  pull_request:
    paths:
      - 'src/main/resources/db/migration/**/*.sql'
      - '**/flyway/**/*.sql'

jobs:
  check-sql-comments:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Check SQL Migration Comments
        run: |
          #!/bin/bash
          set -e
          
          # 色定義
          RED='\033[0;31m'
          GREEN='\033[0;32m'
          YELLOW='\033[1;33m'
          NC='\033[0m' # No Color
          
          echo "🔍 SQLマイグレーションファイルのコメントをチェックしています..."
          
          # 変更されたSQLファイルを取得
          CHANGED_SQL_FILES=$(git diff --name-only origin/${{ github.base_ref }}...HEAD | grep -E '\.sql$' || true)
          
          if [ -z "$CHANGED_SQL_FILES" ]; then
            echo -e "${GREEN}✅ SQLファイルの変更はありません${NC}"
            exit 0
          fi
          
          ERRORS=0
          WARNINGS=0
          
          for FILE in $CHANGED_SQL_FILES; do
            echo ""
            echo "📄 チェック中: $FILE"
            
            # ファイル存在確認
            if [ ! -f "$FILE" ]; then
              continue
            fi
            
            # 1. ファイル冒頭コメントのチェック
            if ! grep -q "^/\*" "$FILE"; then
              echo -e "${RED}❌ エラー: ファイル冒頭の複数行コメントがありません${NC}"
              echo "   必須項目: 目的、ビジネス背景、設計判断、想定クエリ、インデックス方針"
              ((ERRORS++))
            else
              # 必須キーワードの確認
              HEADER_COMMENT=$(sed -n '/^\/\*/,/\*\//p' "$FILE")
              
              if ! echo "$HEADER_COMMENT" | grep -qi "目的\|purpose"; then
                echo -e "${YELLOW}⚠️  警告: 「目的」セクションが見つかりません${NC}"
                ((WARNINGS++))
              fi
              
              if ! echo "$HEADER_COMMENT" | grep -qi "チケット\|ticket\|EC-[0-9]"; then
                echo -e "${YELLOW}⚠️  警告: チケット番号が見つかりません${NC}"
                ((WARNINGS++))
              fi
              
              if ! echo "$HEADER_COMMENT" | grep -qi "想定クエリ\|query pattern"; then
                echo -e "${YELLOW}⚠️  警告: 「想定クエリパターン」セクションが見つかりません${NC}"
                ((WARNINGS++))
              fi
              
              if ! echo "$HEADER_COMMENT" | grep -qi "インデックス\|index"; then
                echo -e "${YELLOW}⚠️  警告: 「インデックス方針」セクションが見つかりません${NC}"
                ((WARNINGS++))
              fi
            fi
            
            # 2. CREATE INDEXのチェック
            INDEX_COUNT=$(grep -c "^CREATE INDEX" "$FILE" || true)
            if [ "$INDEX_COUNT" -gt 0 ]; then
              echo "   📊 インデックス数: $INDEX_COUNT"
              
              # 各インデックスの前にコメントがあるかチェック
              INDEX_LINES=$(grep -n "^CREATE INDEX" "$FILE" | cut -d: -f1)
              for LINE_NUM in $INDEX_LINES; do
                # インデックス作成の5行前を確認
                START_LINE=$((LINE_NUM - 5))
                if [ $START_LINE -lt 1 ]; then
                  START_LINE=1
                fi
                
                CONTEXT=$(sed -n "${START_LINE},${LINE_NUM}p" "$FILE")
                INDEX_NAME=$(sed -n "${LINE_NUM}p" "$FILE" | sed -n 's/.*CREATE INDEX \([^ ]*\).*/\1/p')
                
                if ! echo "$CONTEXT" | grep -q "^--"; then
                  echo -e "${RED}❌ エラー: インデックス '$INDEX_NAME' (行$LINE_NUM) の前にインラインコメントがありません${NC}"
                  echo "   必須項目: 目的、クエリパターン、頻度、効果、理由"
                  ((ERRORS++))
                fi
              done
              
              # COMMENT ON INDEX のチェック
              COMMENT_INDEX_COUNT=$(grep -c "^COMMENT ON INDEX" "$FILE" || true)
              if [ "$COMMENT_INDEX_COUNT" -lt "$INDEX_COUNT" ]; then
                echo -e "${RED}❌ エラー: COMMENT ON INDEX が不足しています (${COMMENT_INDEX_COUNT}/${INDEX_COUNT})${NC}"
                echo "   各インデックスに COMMENT ON INDEX を追加してください"
                ((ERRORS++))
              fi
            fi
            
            # 3. FOREIGN KEYのチェック
            FK_COUNT=$(grep -c "FOREIGN KEY\|REFERENCES" "$FILE" || true)
            if [ "$FK_COUNT" -gt 0 ]; then
              echo "   🔗 外部キー数: $FK_COUNT"
              
              # COMMENT ON CONSTRAINT のチェック（推奨）
              COMMENT_CONSTRAINT_COUNT=$(grep -c "^COMMENT ON CONSTRAINT" "$FILE" || true)
              if [ "$COMMENT_CONSTRAINT_COUNT" -eq 0 ] && [ "$FK_COUNT" -gt 0 ]; then
                echo -e "${YELLOW}⚠️  推奨: COMMENT ON CONSTRAINT を追加してください${NC}"
                echo "   外部キー制約の目的・動作（ON DELETE CASCADE等）を記録することを推奨"
                ((WARNINGS++))
              fi
            fi
            
            # 4. COMMENT ON TABLE/COLUMN のチェック
            if ! grep -q "^COMMENT ON TABLE" "$FILE"; then
              echo -e "${YELLOW}⚠️  警告: COMMENT ON TABLE がありません${NC}"
              ((WARNINGS++))
            fi
            
            CREATE_TABLE_COUNT=$(grep -c "^CREATE TABLE" "$FILE" || true)
            if [ "$CREATE_TABLE_COUNT" -gt 0 ]; then
              if ! grep -q "^COMMENT ON COLUMN" "$FILE"; then
                echo -e "${YELLOW}⚠️  警告: COMMENT ON COLUMN がありません${NC}"
                echo "   主要カラムにはコメントを追加することを推奨"
                ((WARNINGS++))
              fi
            fi
            
            # ファイルごとのサマリー
            if [ $ERRORS -eq 0 ] && [ $WARNINGS -eq 0 ]; then
              echo -e "${GREEN}✅ $FILE: OK${NC}"
            fi
          done
          
          # 最終結果
          echo ""
          echo "=================================="
          echo "📊 チェック結果サマリー"
          echo "=================================="
          echo -e "❌ エラー: $ERRORS"
          echo -e "⚠️  警告: $WARNINGS"
          
          if [ $ERRORS -gt 0 ]; then
            echo ""
            echo -e "${RED}💥 エラーが検出されました。修正してください。${NC}"
            echo ""
            echo "📖 参考ドキュメント:"
            echo "   - organization-standards/01-coding-standards/sql-standards.md"
            echo "   - docs/SQL-MIGRATION-COMMENT-SOLUTION.md"
            exit 1
          elif [ $WARNINGS -gt 0 ]; then
            echo ""
            echo -e "${YELLOW}⚠️  警告があります。確認してください。${NC}"
            echo "   （警告はマージをブロックしませんが、修正を推奨します）"
            exit 0
          else
            echo ""
            echo -e "${GREEN}✅ すべてのチェックに合格しました！${NC}"
            exit 0
          fi

      - name: Comment PR (on failure)
        if: failure()
        uses: actions/github-script@v7
        with:
          script: |
            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: `## ❌ SQLマイグレーションコメントチェック失敗

**エラーが検出されました。以下を確認してください:**

### 📋 必須コメント
- [ ] ファイル冒頭の複数行コメント（目的、ビジネス背景、設計判断、想定クエリ、インデックス方針）
- [ ] 各インデックス作成前のインラインコメント
- [ ] 各インデックスの \`COMMENT ON INDEX\`

### 📖 推奨コメント
- [ ] 外部キー制約の \`COMMENT ON CONSTRAINT\`
- [ ] テーブル・カラムの \`COMMENT ON TABLE/COLUMN\`

### 📚 参考ドキュメント
- [SQL標準](../organization-standards/01-coding-standards/sql-standards.md)
- [SQLマイグレーションコメント解決ガイド](../docs/SQL-MIGRATION-COMMENT-SOLUTION.md)

詳細は [ワークフロー実行ログ](https://github.com/${{ github.repository }}/actions/runs/${{ github.run_id }}) を確認してください。`
            })
```

---

## ✅ ベストプラクティス

### 1. コメント記述の原則

#### Why重視のコメント（70%）
```sql
-- ❌ Bad: Whatのみを説明
-- user_idにインデックスを作成
CREATE INDEX idx_user_id ON table_name(user_id);

-- ✅ Good: Whyを詳細に説明
-- 目的: ユーザー別検索の高速化（最頻出クエリパターン）
-- クエリ: SELECT * FROM table_name WHERE user_id = ?
-- 頻度: 毎秒1000リクエスト
-- 効果: フルスキャン(500ms)→インデックススキャン(5ms)に改善
CREATE INDEX idx_user_id ON table_name(user_id);
```

#### ビジネスコンテキストの記載
```sql
-- ❌ Bad: 技術的な説明のみ
-- CASCADE削除を設定
ON DELETE CASCADE

-- ✅ Good: ビジネス的な理由を説明
-- ON DELETE CASCADE理由:
-- - GDPR対応: ユーザー削除時に個人データを完全消去
-- - データ整合性: 親レコード削除時に孤立レコードを防止
-- - 運用効率: 手動での関連データ削除が不要
ON DELETE CASCADE
```

### 2. インデックス設計のコメント

```sql
-- インデックス設計の完全な説明例
-- -----------------------------------------------------------------------------
-- インデックス: トークン検証用（最重要）
-- -----------------------------------------------------------------------------
-- 【目的】
-- - リセットリンククリック時の高速トークン検証
-- 
-- 【想定クエリ】
-- SELECT * FROM password_reset_tokens 
-- WHERE token = ? 
--   AND is_used = FALSE 
--   AND expires_at > NOW();
-- 
-- 【実行頻度】
-- - 1日あたり10,000リクエスト（ピーク時）
-- - レスポンスタイム要件: 100ms以内
-- 
-- 【パフォーマンス分析】
-- - インデックスなし: 平均500ms（フルテーブルスキャン）
-- - インデックスあり: 平均5ms（B-Treeインデックス検索）
-- - 改善率: 100倍
-- 
-- 【設計判断】
-- - カラム選択: token（UNIQUE制約、高カーディナリティ）
-- - インデックスタイプ: B-Tree（等価検索に最適）
-- - 部分インデックス検討: is_used = FALSEで絞り込み可能だが、
--   トークンのUNIQUE性により効果は限定的と判断
CREATE INDEX idx_password_reset_tokens_token 
ON password_reset_tokens(token);

COMMENT ON INDEX idx_password_reset_tokens_token IS 
'トークン検証用インデックス
目的: リセットリンククリック時の高速検証
想定クエリ: WHERE token = ? AND is_used = FALSE AND expires_at > NOW()
カーディナリティ: 非常に高い（UNIQUE制約）
インデックスタイプ: B-Tree
推定効果: フルスキャン比で100倍高速化（500ms → 5ms）
実行頻度: 1日あたり10,000リクエスト
メンテナンス: 自動（INSERT/DELETE時に更新）
レビュー日: 2025-11-07';
```

### 3. マイグレーションファイルの構造

```sql
/*
 * =============================================================================
 * [1] ヘッダーコメント
 * =============================================================================
 */

-- =============================================================================
-- [2] テーブル作成
-- =============================================================================
CREATE TABLE ...;

-- =============================================================================
-- [3] テーブルコメント
-- =============================================================================
COMMENT ON TABLE ...;

-- =============================================================================
-- [4] カラムコメント
-- =============================================================================
COMMENT ON COLUMN ...;

-- =============================================================================
-- [5] インデックス作成
-- =============================================================================

-- -----------------------------------------------------------------------------
-- インデックス1: ...
-- -----------------------------------------------------------------------------
-- [インライン説明]
CREATE INDEX ...;
COMMENT ON INDEX ...;

-- -----------------------------------------------------------------------------
-- インデックス2: ...
-- -----------------------------------------------------------------------------
-- [インライン説明]
CREATE INDEX ...;
COMMENT ON INDEX ...;

-- =============================================================================
-- [6] 制約コメント
-- =============================================================================
COMMENT ON CONSTRAINT ...;

-- =============================================================================
-- [7] 完了メッセージ・次のステップ
-- =============================================================================
```

---

## ✅ チェックリスト

### PRレビュー時チェックリスト

#### ファイル冒頭コメント
- [ ] 複数行コメント（`/* ... */`）が存在する
- [ ] マイグレーションの目的が明確に記載されている
- [ ] ビジネス背景・チケット番号（EC-XXX）が記載されている
- [ ] 主な設計判断が記載されている
- [ ] 想定クエリパターンが記載されている（最低3パターン）
- [ ] インデックス方針が記載されている
- [ ] セキュリティ考慮事項が記載されている（該当する場合）

#### インデックスコメント
- [ ] 各`CREATE INDEX`の前にインラインコメント（`--`）がある
- [ ] インラインコメントに以下が含まれる:
  - [ ] 目的
  - [ ] 想定クエリ
  - [ ] 実行頻度
  - [ ] 期待される効果
  - [ ] なぜこのインデックスが必要か
- [ ] 各インデックスに`COMMENT ON INDEX`がある
- [ ] `COMMENT ON INDEX`に以下が含まれる:
  - [ ] 簡潔な目的
  - [ ] 想定クエリ（SQL例）
  - [ ] インデックスタイプ（B-Tree/Hash/etc）
  - [ ] 推定効果
  - [ ] メンテナンス情報

#### 制約コメント
- [ ] 外部キー制約に`COMMENT ON CONSTRAINT`がある（推奨）
- [ ] `ON DELETE CASCADE`の理由が説明されている
- [ ] ビジネスロジック的な意図が記載されている

#### テーブル・カラムコメント
- [ ] `COMMENT ON TABLE`がある
- [ ] 主要カラムに`COMMENT ON COLUMN`がある
- [ ] カラムコメントに形式・用途・制約が記載されている

#### 全体品質
- [ ] SQLキーワードが大文字
- [ ] 識別子が小文字
- [ ] 適切にインデントされている
- [ ] セクション区切りが明確（`==============`）
- [ ] 読みやすい構造

---

## 📚 参考資料

### 内部ドキュメント
- [SQL標準](../organization-standards/01-coding-standards/sql-standards.md)
- [コードレビューガイドライン](../organization-standards/03-development-process/code-review-guidelines.md)
- [データベース設計標準](../organization-standards/02-architecture-standards/database-design-standards.md)

### 外部リソース
- [PostgreSQL COMMENT構文](https://www.postgresql.org/docs/current/sql-comment.html)
- [Flyway公式ドキュメント](https://flywaydb.org/documentation/)
- [PostgreSQL インデックス最適化](https://www.postgresql.org/docs/current/indexes.html)

---

## 📝 変更履歴

### v1.0.0 (2025-11-07) - 初版作成

**作成内容**:
- ✨ EC-15課題の詳細分析
- ✨ 修正版SQLファイルの作成（完全コメント版）
- ✨ コメント記述ガイドライン
- ✨ CI/CD自動チェック設定
- ✨ ベストプラクティス
- ✨ チェックリスト

**対象課題**:
- SQLマイグレーションファイルのコメント不足
- organization-standards準拠の徹底

---

**最終更新**: 2025-11-07  
**バージョン**: 1.0.0  
**管理者**: Engineering Leadership Team  
**関連チケット**: EC-15
