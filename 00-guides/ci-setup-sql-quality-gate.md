---
title: "CI-SETUP-CHECKLIST - SQL品質ゲート追加セクション"
version: "1.1.0"
created_date: "2025-11-07"
last_updated: "2025-11-07"
status: "Active"
purpose: "CI-SETUP-CHECKLISTにSQLマイグレーションコメント品質ゲートを追加"
---

# CI-SETUP-CHECKLIST - SQL品質ゲート追加セクション

> **統合先**: `00-guides/CI-SETUP-CHECKLIST.md`  
> **挿入位置**: Section 5.3（新規セクション）「ドキュメントコメント品質ゲート」の後

---

## 5.3 SQLマイグレーションコメント品質ゲート

### 目的

Flywayマイグレーションファイルが組織標準（`01-coding-standards/sql-standards.md`）に準拠していることを自動的に検証し、コメント不足によるレビュー指摘を防止する。

### 対象ファイル

```yaml
対象パターン:
  - src/main/resources/db/migration/**/*.sql
  - src/main/resources/db/migrations/**/*.sql
  - **/flyway/**/*.sql
  - **/liquibase/**/*.sql

除外パターン:
  - **/*_test.sql
  - **/*_fixture.sql
```

---

## ステップ5.3.1: GitHub Actions ワークフロー作成

### ファイル作成

**パス**: `.github/workflows/sql-migration-comment-check.yml`

```yaml
name: SQL Migration Comment Quality Gate

on:
  pull_request:
    paths:
      - 'src/main/resources/db/migration/**/*.sql'
      - 'src/main/resources/db/migrations/**/*.sql'
      - '**/flyway/**/*.sql'
      - '**/liquibase/**/*.sql'

jobs:
  sql-comment-check:
    name: SQL Migration Comment Check
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        with:
          fetch-depth: 0  # 全履歴取得（差分比較用）

      - name: Get changed SQL files
        id: changed-files
        run: |
          # PRで変更されたSQLファイルを取得
          CHANGED_FILES=$(git diff --name-only origin/${{ github.base_ref }}...HEAD | \
            grep -E '\.(sql)$' | \
            grep -v '_test\.sql$' | \
            grep -v '_fixture\.sql$' || true)
          
          if [ -z "$CHANGED_FILES" ]; then
            echo "changed_files=" >> $GITHUB_OUTPUT
            echo "has_changes=false" >> $GITHUB_OUTPUT
          else
            # 改行をスペースに変換
            FILES_SPACE=$(echo "$CHANGED_FILES" | tr '\n' ' ')
            echo "changed_files=$FILES_SPACE" >> $GITHUB_OUTPUT
            echo "has_changes=true" >> $GITHUB_OUTPUT
          fi
          
          echo "Changed SQL files:"
          echo "$CHANGED_FILES"

      - name: Check SQL Migration Comments
        if: steps.changed-files.outputs.has_changes == 'true'
        id: check
        run: |
          #!/bin/bash
          set -e
          
          # 色定義
          RED='\033[0;31m'
          GREEN='\033[0;32m'
          YELLOW='\033[1;33m'
          BLUE='\033[0;34m'
          NC='\033[0m' # No Color
          
          echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
          echo -e "${BLUE}🔍 SQLマイグレーションコメント品質チェック${NC}"
          echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
          echo ""
          
          CHANGED_FILES="${{ steps.changed-files.outputs.changed_files }}"
          ERRORS=0
          WARNINGS=0
          CHECKED_FILES=0
          
          for FILE in $CHANGED_FILES; do
            # ファイル存在確認
            if [ ! -f "$FILE" ]; then
              echo -e "${YELLOW}⚠️  スキップ: $FILE (ファイルが見つかりません)${NC}"
              continue
            fi
            
            ((CHECKED_FILES++))
            echo ""
            echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            echo -e "${BLUE}📄 チェック中: $FILE${NC}"
            echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            
            FILE_ERRORS=0
            FILE_WARNINGS=0
            
            # ============================================================
            # 1. ファイル冒頭コメントのチェック
            # ============================================================
            echo ""
            echo "📋 [1/4] ファイル冒頭コメントをチェック中..."
            
            if ! grep -q "^/\*" "$FILE"; then
              echo -e "${RED}❌ エラー: ファイル冒頭の複数行コメントがありません${NC}"
              echo "   必須セクション:"
              echo "   - 【目的】"
              echo "   - 【ビジネス背景】（チケット番号）"
              echo "   - 【主な設計判断】"
              echo "   - 【想定クエリパターン】"
              echo "   - 【インデックス方針】"
              ((ERRORS++))
              ((FILE_ERRORS++))
            else
              # ヘッダーコメント抽出
              HEADER_COMMENT=$(sed -n '/^\/\*/,/\*\//p' "$FILE")
              
              # 必須キーワードチェック
              MISSING_SECTIONS=""
              
              if ! echo "$HEADER_COMMENT" | grep -qi "目的\|purpose"; then
                MISSING_SECTIONS="${MISSING_SECTIONS}- 【目的】セクション\n"
                ((FILE_WARNINGS++))
              fi
              
              if ! echo "$HEADER_COMMENT" | grep -qi "チケット\|ticket\|EC-[0-9]\|JIRA"; then
                MISSING_SECTIONS="${MISSING_SECTIONS}- チケット番号\n"
                ((FILE_WARNINGS++))
              fi
              
              if ! echo "$HEADER_COMMENT" | grep -qi "設計判断\|design decision"; then
                MISSING_SECTIONS="${MISSING_SECTIONS}- 【主な設計判断】セクション\n"
                ((FILE_WARNINGS++))
              fi
              
              if ! echo "$HEADER_COMMENT" | grep -qi "想定クエリ\|query pattern\|expected query"; then
                MISSING_SECTIONS="${MISSING_SECTIONS}- 【想定クエリパターン】セクション\n"
                ((FILE_WARNINGS++))
              fi
              
              if ! echo "$HEADER_COMMENT" | grep -qi "インデックス\|index"; then
                MISSING_SECTIONS="${MISSING_SECTIONS}- 【インデックス方針】セクション\n"
                ((FILE_WARNINGS++))
              fi
              
              if [ -n "$MISSING_SECTIONS" ]; then
                echo -e "${YELLOW}⚠️  警告: 以下のセクションが見つかりません:${NC}"
                echo -e "$MISSING_SECTIONS"
                ((WARNINGS += FILE_WARNINGS))
              else
                echo -e "${GREEN}✅ ファイル冒頭コメント: OK${NC}"
              fi
            fi
            
            # ============================================================
            # 2. CREATE INDEXのチェック
            # ============================================================
            echo ""
            echo "🔍 [2/4] インデックスコメントをチェック中..."
            
            INDEX_COUNT=$(grep -c "^CREATE INDEX\|^CREATE UNIQUE INDEX" "$FILE" || true)
            
            if [ "$INDEX_COUNT" -eq 0 ]; then
              echo -e "${GREEN}ℹ️  インデックスなし（スキップ）${NC}"
            else
              echo "   📊 検出されたインデックス数: $INDEX_COUNT"
              
              # 各インデックスのチェック
              INDEX_LINES=$(grep -n "^CREATE INDEX\|^CREATE UNIQUE INDEX" "$FILE" | cut -d: -f1)
              INDEX_NUM=0
              
              for LINE_NUM in $INDEX_LINES; do
                ((INDEX_NUM++))
                
                # インデックス名取得
                INDEX_LINE=$(sed -n "${LINE_NUM}p" "$FILE")
                INDEX_NAME=$(echo "$INDEX_LINE" | sed -n 's/.*CREATE \(UNIQUE \)\?INDEX \([^ ]*\).*/\2/p')
                
                echo ""
                echo "   🔹 インデックス $INDEX_NUM/$INDEX_COUNT: $INDEX_NAME (行 $LINE_NUM)"
                
                # インデックス作成の10行前を確認
                START_LINE=$((LINE_NUM - 10))
                if [ $START_LINE -lt 1 ]; then
                  START_LINE=1
                fi
                
                CONTEXT=$(sed -n "${START_LINE},${LINE_NUM}p" "$FILE")
                
                # インラインコメントチェック
                if ! echo "$CONTEXT" | grep -q "^--"; then
                  echo -e "   ${RED}❌ エラー: インデックス '$INDEX_NAME' の前にインラインコメントがありません${NC}"
                  echo "   必須項目: 目的、想定クエリ、実行頻度、パフォーマンス効果"
                  ((ERRORS++))
                  ((FILE_ERRORS++))
                else
                  # コメント内容の詳細チェック
                  INLINE_COMMENT=$(echo "$CONTEXT" | grep "^--" | tail -20)
                  
                  MISSING_ITEMS=""
                  if ! echo "$INLINE_COMMENT" | grep -qi "目的\|purpose"; then
                    MISSING_ITEMS="${MISSING_ITEMS}目的, "
                  fi
                  if ! echo "$INLINE_COMMENT" | grep -qi "クエリ\|query\|SELECT"; then
                    MISSING_ITEMS="${MISSING_ITEMS}想定クエリ, "
                  fi
                  if ! echo "$INLINE_COMMENT" | grep -qi "頻度\|frequency"; then
                    MISSING_ITEMS="${MISSING_ITEMS}実行頻度, "
                  fi
                  
                  if [ -n "$MISSING_ITEMS" ]; then
                    echo -e "   ${YELLOW}⚠️  警告: コメントに不足項目があります: ${MISSING_ITEMS%??}${NC}"
                    ((FILE_WARNINGS++))
                  else
                    echo -e "   ${GREEN}✅ インラインコメント: OK${NC}"
                  fi
                fi
              done
              
              # COMMENT ON INDEX のチェック
              echo ""
              COMMENT_INDEX_COUNT=$(grep -c "^COMMENT ON INDEX" "$FILE" || true)
              echo "   📝 COMMENT ON INDEX: $COMMENT_INDEX_COUNT/$INDEX_COUNT"
              
              if [ "$COMMENT_INDEX_COUNT" -lt "$INDEX_COUNT" ]; then
                MISSING_COUNT=$((INDEX_COUNT - COMMENT_INDEX_COUNT))
                echo -e "   ${RED}❌ エラー: COMMENT ON INDEX が $MISSING_COUNT 個不足しています${NC}"
                echo "   全てのインデックスに COMMENT ON INDEX を追加してください"
                ((ERRORS++))
                ((FILE_ERRORS++))
              else
                echo -e "   ${GREEN}✅ COMMENT ON INDEX: OK${NC}"
              fi
            fi
            
            # ============================================================
            # 3. FOREIGN KEYのチェック
            # ============================================================
            echo ""
            echo "🔗 [3/4] 外部キー制約コメントをチェック中..."
            
            FK_COUNT=$(grep -c "FOREIGN KEY\|REFERENCES" "$FILE" || true)
            
            if [ "$FK_COUNT" -eq 0 ]; then
              echo -e "${GREEN}ℹ️  外部キーなし（スキップ）${NC}"
            else
              echo "   🔗 検出された外部キー: $FK_COUNT"
              
              COMMENT_CONSTRAINT_COUNT=$(grep -c "^COMMENT ON CONSTRAINT" "$FILE" || true)
              
              if [ "$COMMENT_CONSTRAINT_COUNT" -eq 0 ]; then
                echo -e "   ${YELLOW}⚠️  推奨: COMMENT ON CONSTRAINT を追加してください${NC}"
                echo "   外部キー制約の目的・動作（ON DELETE CASCADE等）を記録することを推奨"
                ((FILE_WARNINGS++))
              else
                echo -e "   ${GREEN}✅ COMMENT ON CONSTRAINT: $COMMENT_CONSTRAINT_COUNT 個記載${NC}"
              fi
            fi
            
            # ============================================================
            # 4. COMMENT ON TABLE/COLUMN のチェック
            # ============================================================
            echo ""
            echo "📝 [4/4] テーブル・カラムコメントをチェック中..."
            
            CREATE_TABLE_COUNT=$(grep -c "^CREATE TABLE" "$FILE" || true)
            
            if [ "$CREATE_TABLE_COUNT" -eq 0 ]; then
              echo -e "${GREEN}ℹ️  テーブル作成なし（スキップ）${NC}"
            else
              echo "   📊 テーブル作成: $CREATE_TABLE_COUNT"
              
              if ! grep -q "^COMMENT ON TABLE" "$FILE"; then
                echo -e "   ${YELLOW}⚠️  警告: COMMENT ON TABLE がありません${NC}"
                ((FILE_WARNINGS++))
              else
                echo -e "   ${GREEN}✅ COMMENT ON TABLE: OK${NC}"
              fi
              
              if ! grep -q "^COMMENT ON COLUMN" "$FILE"; then
                echo -e "   ${YELLOW}⚠️  警告: COMMENT ON COLUMN がありません${NC}"
                echo "   主要カラムにはコメントを追加することを推奨"
                ((FILE_WARNINGS++))
              else
                COLUMN_COMMENT_COUNT=$(grep -c "^COMMENT ON COLUMN" "$FILE")
                echo -e "   ${GREEN}✅ COMMENT ON COLUMN: $COLUMN_COMMENT_COUNT 個記載${NC}"
              fi
            fi
            
            # ファイルごとのサマリー
            echo ""
            echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            if [ $FILE_ERRORS -eq 0 ] && [ $FILE_WARNINGS -eq 0 ]; then
              echo -e "${GREEN}✅ $FILE: すべてのチェックに合格${NC}"
            elif [ $FILE_ERRORS -gt 0 ]; then
              echo -e "${RED}❌ $FILE: エラー ${FILE_ERRORS} 件、警告 ${FILE_WARNINGS} 件${NC}"
            else
              echo -e "${YELLOW}⚠️  $FILE: 警告 ${FILE_WARNINGS} 件${NC}"
            fi
            echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            
            ((WARNINGS += FILE_WARNINGS))
          done
          
          # ============================================================
          # 最終結果サマリー
          # ============================================================
          echo ""
          echo ""
          echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
          echo -e "${BLUE}📊 チェック結果サマリー${NC}"
          echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
          echo ""
          echo "   チェック対象ファイル: $CHECKED_FILES"
          echo -e "   ❌ エラー: $ERRORS"
          echo -e "   ⚠️  警告: $WARNINGS"
          echo ""
          
          # 結果判定
          if [ $ERRORS -gt 0 ]; then
            echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            echo -e "${RED}💥 品質ゲート: 失敗${NC}"
            echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            echo ""
            echo "🔧 修正方法:"
            echo "   1. organization-standards/01-coding-standards/sql-standards.md を確認"
            echo "   2. 00-guides/SQL-MIGRATION-COMMENT-SOLUTION.md のテンプレートを使用"
            echo "   3. 00-guides/phase-guides/phase-3-implementation-guide.md Section 3.8 を参照"
            echo ""
            echo "📚 参考リソース:"
            echo "   - SQL標準: organization-standards/01-coding-standards/sql-standards.md"
            echo "   - 解決策: 00-guides/SQL-MIGRATION-COMMENT-SOLUTION.md"
            echo "   - 実装ガイド: 00-guides/phase-guides/phase-3-implementation-guide.md"
            echo ""
            
            # エラー詳細をGitHub出力に保存
            echo "error_count=$ERRORS" >> $GITHUB_OUTPUT
            echo "warning_count=$WARNINGS" >> $GITHUB_OUTPUT
            echo "result=failure" >> $GITHUB_OUTPUT
            
            exit 1
            
          elif [ $WARNINGS -gt 0 ]; then
            echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            echo -e "${YELLOW}⚠️  品質ゲート: 警告あり（マージ可能）${NC}"
            echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            echo ""
            echo "💡 推奨事項:"
            echo "   警告項目を修正することで、コード品質がさらに向上します"
            echo ""
            
            echo "error_count=0" >> $GITHUB_OUTPUT
            echo "warning_count=$WARNINGS" >> $GITHUB_OUTPUT
            echo "result=warning" >> $GITHUB_OUTPUT
            
            exit 0
            
          else
            echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            echo -e "${GREEN}✅ 品質ゲート: 合格${NC}"
            echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            echo ""
            echo "🎉 すべてのチェックに合格しました！"
            echo ""
            
            echo "error_count=0" >> $GITHUB_OUTPUT
            echo "warning_count=0" >> $GITHUB_OUTPUT
            echo "result=success" >> $GITHUB_OUTPUT
            
            exit 0
          fi

      - name: Comment PR (on failure)
        if: failure() && steps.check.outputs.result == 'failure'
        uses: actions/github-script@v7
        with:
          script: |
            const errorCount = '${{ steps.check.outputs.error_count }}';
            const warningCount = '${{ steps.check.outputs.warning_count }}';
            
            const body = `## ❌ SQLマイグレーションコメント品質ゲート: 失敗

**検出された問題:**
- ❌ エラー: ${errorCount} 件
- ⚠️  警告: ${warningCount} 件

### 📋 必須対応項目

#### ファイル冒頭コメント
- [ ] 複数行コメント (\`/* ... */\`) が存在する
- [ ] 【目的】セクションが記載されている
- [ ] 【ビジネス背景】にチケット番号が記載されている
- [ ] 【主な設計判断】が記載されている
- [ ] 【想定クエリパターン】が3つ以上記載されている
- [ ] 【インデックス方針】が記載されている

#### インデックスコメント
- [ ] 各 \`CREATE INDEX\` の前にインラインコメント (\`--\`) がある
- [ ] インラインコメントに以下が含まれる:
  - [ ] 目的
  - [ ] 想定クエリ
  - [ ] 実行頻度
  - [ ] パフォーマンス効果
- [ ] 各インデックスに \`COMMENT ON INDEX\` がある

#### 推奨対応項目
- [ ] 外部キー制約に \`COMMENT ON CONSTRAINT\` がある
- [ ] テーブル・カラムに \`COMMENT ON TABLE/COLUMN\` がある

### 🔧 修正方法

1. **テンプレートを使用**
   - [\`SQL-MIGRATION-COMMENT-SOLUTION.md\`](../blob/main/00-guides/SQL-MIGRATION-COMMENT-SOLUTION.md) のテンプレートをコピー

2. **実装ガイド参照**
   - [\`phase-3-implementation-guide.md\` Section 3.8](../blob/main/00-guides/phase-guides/phase-3-implementation-guide.md) を確認

3. **SQL標準確認**
   - [\`sql-standards.md\`](../blob/main/01-coding-standards/sql-standards.md) で組織標準を確認

### 📚 参考ドキュメント

| ドキュメント | 内容 |
|------------|------|
| [SQL標準](../blob/main/01-coding-standards/sql-standards.md) | 組織のSQL標準 |
| [解決策ガイド](../blob/main/00-guides/SQL-MIGRATION-COMMENT-SOLUTION.md) | 完全なテンプレートと例 |
| [実装ガイド](../blob/main/00-guides/phase-guides/phase-3-implementation-guide.md) | Phase 3 Section 3.8 |
| [CI設定](../blob/main/00-guides/CI-SETUP-CHECKLIST.md) | CI/CD設定ガイド |

詳細は [ワークフロー実行ログ](${{ github.server_url }}/${{ github.repository }}/actions/runs/${{ github.run_id }}) を確認してください。`;

            await github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: body
            });

      - name: Comment PR (on warning)
        if: success() && steps.check.outputs.result == 'warning'
        uses: actions/github-script@v7
        with:
          script: |
            const warningCount = '${{ steps.check.outputs.warning_count }}';
            
            const body = `## ⚠️  SQLマイグレーションコメント品質ゲート: 警告

**検出された警告:**
- ⚠️  警告: ${warningCount} 件

マージは可能ですが、以下の項目を修正することでコード品質がさらに向上します。

### 📋 推奨対応項目

- [ ] ファイル冒頭コメントの全セクション記載
- [ ] 外部キー制約に \`COMMENT ON CONSTRAINT\`
- [ ] 全カラムに \`COMMENT ON COLUMN\`

### 📚 参考ドキュメント

- [SQL標準](../blob/main/01-coding-standards/sql-standards.md)
- [解決策ガイド](../blob/main/00-guides/SQL-MIGRATION-COMMENT-SOLUTION.md)

詳細は [ワークフロー実行ログ](${{ github.server_url }}/${{ github.repository }}/actions/runs/${{ github.run_id }}) を確認してください。`;

            await github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: body
            });
```

---

## ステップ5.3.2: ワークフロー動作確認

### ローカルテスト

```bash
# テスト用SQLファイルを作成
mkdir -p test/sql
cat > test/sql/V999__Test_migration.sql << 'EOF'
CREATE TABLE test_table (
    id UUID PRIMARY KEY
);
EOF

# チェックスクリプトを実行
# （ワークフローのrun部分を抽出して実行）
```

### PRでの動作確認

1. **SQLファイルを変更してPR作成**
   ```bash
   git checkout -b test/sql-comment-check
   # SQLファイルを編集
   git add src/main/resources/db/migration/
   git commit -m "test: SQL comment check"
   git push origin test/sql-comment-check
   ```

2. **GitHub Actionsで結果確認**
   - PRページの「Checks」タブを確認
   - 「SQL Migration Comment Quality Gate」の結果を確認

3. **期待される動作**:
   - ✅ エラーなし → チェック合格、マージ可能
   - ⚠️ 警告のみ → チェック合格、マージ可能（推奨事項あり）
   - ❌ エラーあり → チェック失敗、マージブロック

---

## ステップ5.3.3: チェックリスト

### CI設定完了確認

- [ ] `.github/workflows/sql-migration-comment-check.yml` が作成されている
- [ ] ワークフローがPRで自動実行されることを確認
- [ ] エラー時にPRへのコメントが投稿されることを確認
- [ ] 警告時にPRへのコメントが投稿されることを確認
- [ ] マージブロックが正しく機能することを確認

### ドキュメント整備確認

- [ ] `SQL-MIGRATION-COMMENT-SOLUTION.md` が作成されている
- [ ] `phase-3-implementation-guide.md` に Section 3.8 が追加されている
- [ ] `CI-SETUP-CHECKLIST.md` に Section 5.3 が追加されている
- [ ] チーム向けオンボーディング資料に追記されている

### チーム周知確認

- [ ] Slackでチーム全体に周知済み
- [ ] 既存PRに対する移行計画を策定
- [ ] レビュアー向けガイドを更新
- [ ] FAQ・トラブルシューティングを準備

---

## トラブルシューティング

### 問題1: ワークフローが実行されない

**原因**: パストリガーが一致していない

**解決策**:
```yaml
# プロジェクトのSQLファイルパスを確認
find . -name "*.sql" -path "*/migration/*"

# ワークフローのpathsを調整
on:
  pull_request:
    paths:
      - 'あなたのプロジェクトのパス/**/*.sql'
```

### 問題2: チェックが厳しすぎる

**解決策**: 警告レベルを調整

```bash
# エラー→警告に変更
# ワークフロー内で ((ERRORS++)) を ((WARNINGS++)) に変更
```

### 問題3: 既存SQLファイルが大量にエラー

**解決策**: 段階的導入

```yaml
# オプション1: 新規ファイルのみチェック
on:
  pull_request:
    paths:
      - 'src/main/resources/db/migration/V[5-9]*__*.sql'  # 新しいバージョンのみ

# オプション2: 警告のみでマージ可能に
# ワークフローで exit 1 を exit 0 に変更（暫定対応）
```

---

## まとめ

### Section 5.3の要点

1. ✅ **自動チェック**: PR作成時にSQLコメントを自動検証
2. ✅ **マージブロック**: エラー検出時はマージ不可
3. ✅ **詳細フィードバック**: PRに自動コメント投稿
4. ✅ **組織標準準拠**: `sql-standards.md`に準拠
5. ✅ **プロセス統合**: Phase 3実装ガイドと連携

### 期待される効果

- レビュー時のコメント不足指摘が **0件**
- SQLマイグレーションの品質向上
- ドキュメント化の自動化
- 組織標準の自動適用

---

**統合日**: 2025-11-07  
**対象ドキュメント**: `00-guides/CI-SETUP-CHECKLIST.md`  
**セクション**: 5.3（新規）
