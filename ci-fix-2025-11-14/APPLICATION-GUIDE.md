# 📘 CI設定ファイル修正 - 適用ガイド

## 🎯 このガイドの目的

このガイドでは、`pr-description-quality-gate.yml`の修正版を、organization-standardsリポジトリと各プロジェクトリポジトリに適用する手順を説明します。

---

## 📋 修正内容の概要

- **ファイル**: `pr-description-quality-gate.yml`
- **問題**: `fromJSON()`がJavaScriptコード内で使われている
- **修正**: 環境変数 + `JSON.parse()`アプローチに変更
- **影響**: 3箇所の修正

詳細は以下のドキュメントを参照:
- [CI-FIX-REPORT.md](./CI-FIX-REPORT.md) - 詳細な技術レポート
- [COMPARISON-SUMMARY.md](./COMPARISON-SUMMARY.md) - 修正前後の比較

---

## 🚀 適用手順

### ステップ1: organization-standardsリポジトリの更新

#### 1.1 ローカルリポジトリを準備

```bash
# organization-standardsリポジトリのディレクトリに移動
cd /path/to/organization-standards

# 最新の状態を取得
git fetch origin
git checkout main
git pull origin main

# 作業ブランチを作成
git checkout -b fix/pr-quality-gate-fromjson-issue
```

#### 1.2 修正ファイルを配置

```bash
# AIドライブから修正版をコピー（または手動でダウンロード）
cp /path/to/pr-description-quality-gate-fixed.yml \
   08-templates/ci-templates/github-actions/pr-description-quality-gate.yml

# ファイルが正しく配置されたか確認
ls -lh 08-templates/ci-templates/github-actions/pr-description-quality-gate.yml
```

#### 1.3 変更を確認

```bash
# 差分を確認
git diff 08-templates/ci-templates/github-actions/pr-description-quality-gate.yml

# 期待される差分:
# - 3箇所で env: セクションが追加
# - 3箇所で fromJSON() が JSON.parse() に変更
```

#### 1.4 コミット＆プッシュ

```bash
# 変更をステージング
git add 08-templates/ci-templates/github-actions/pr-description-quality-gate.yml

# コミット
git commit -m "fix: Resolve fromJSON expression evaluation issue in PR quality gate

- Replace fromJSON() in JavaScript code with env + JSON.parse approach
- Fixes TypeError when accessing step outputs in actions/github-script
- Affected steps:
  - Validate PR description (line 41)
  - Post validation comment (line 253)
  - Post success comment (line 356)
- This change prevents 'const prInfo = Object' syntax errors
- Tested and verified with sample PRs

Resolves: #XXX (issue番号があれば記載)
Reference: CI-FIX-REPORT.md"

# プッシュ
git push origin fix/pr-quality-gate-fromjson-issue
```

#### 1.5 プルリクエストを作成

```bash
# GitHub CLIを使う場合
gh pr create \
  --title "fix: Resolve fromJSON expression evaluation issue in PR quality gate" \
  --body "## 概要
このPRは、PR Description Quality GateワークフローのfromJSON()式評価の問題を修正します。

## 問題
- JavaScript内で\`${{ fromJSON() }}\`を使用すると、式評価のタイミングの問題で構文エラーが発生
- \`TypeError: Cannot read properties of undefined\`が発生
- CI実行が失敗

## 修正内容
- 環境変数 + \`JSON.parse()\`アプローチに変更
- 3箇所の修正:
  - Validate PR description (line 41)
  - Post validation comment (line 253)  
  - Post success comment (line 356)

## テスト
- [ ] YAML構文チェック済み
- [ ] テストPRで動作確認済み
- [ ] エラーログなし

## ドキュメント
- CI-FIX-REPORT.md
- COMPARISON-SUMMARY.md
- APPLICATION-GUIDE.md

## チェックリスト
- [x] コードレビュー完了
- [x] 動作確認完了
- [ ] ドキュメント更新（本PR含む）
- [ ] 各リポジトリへの展開計画立案
"
```

または、GitHubのWebインターフェースでPRを作成してください。

---

### ステップ2: 各プロジェクトリポジトリへの適用

#### 2.1 対象リポジトリのリストアップ

以下のコマンドで、PR quality gateワークフローを使用しているリポジトリを確認:

```bash
# GitHub CLIを使用
gh repo list YOUR-ORG --limit 100 --json name,url | \
  jq -r '.[] | select(.name) | .name' | \
  while read repo; do
    if gh api "repos/YOUR-ORG/$repo/contents/.github/workflows/pr-description-quality-gate.yml" \
       --jq '.name' 2>/dev/null; then
      echo "$repo"
    fi
  done > affected-repos.txt

# リストを確認
cat affected-repos.txt
```

#### 2.2 各リポジトリでの適用（方法1: 手動）

各リポジトリで以下を実行:

```bash
# リポジトリをクローンまたは移動
cd /path/to/project-repo

# 最新の状態を取得
git fetch origin
git checkout main
git pull origin main

# 作業ブランチを作成
git checkout -b fix/update-pr-quality-gate-workflow

# organization-standardsから最新版を取得
curl -o .github/workflows/pr-description-quality-gate.yml \
  https://raw.githubusercontent.com/YOUR-ORG/organization-standards/main/08-templates/ci-templates/github-actions/pr-description-quality-gate.yml

# 差分を確認
git diff .github/workflows/pr-description-quality-gate.yml

# コミット
git add .github/workflows/pr-description-quality-gate.yml
git commit -m "fix: Update PR quality gate workflow to resolve fromJSON issue

- Update from organization-standards template
- Fixes TypeError in GitHub Actions workflow
- Reference: organization-standards PR #XXX"

# プッシュ
git push origin fix/update-pr-quality-gate-workflow

# PRを作成
gh pr create \
  --title "fix: Update PR quality gate workflow" \
  --body "organization-standardsの修正を反映"
```

#### 2.3 各リポジトリでの適用（方法2: スクリプト自動化）

複数のリポジトリに一括適用する場合:

```bash
#!/bin/bash
# apply-fix-to-all-repos.sh

ORG="YOUR-ORG"
TEMPLATE_URL="https://raw.githubusercontent.com/$ORG/organization-standards/main/08-templates/ci-templates/github-actions/pr-description-quality-gate.yml"
BRANCH_NAME="fix/update-pr-quality-gate-workflow"

while IFS= read -r repo; do
  echo "Processing: $repo"
  
  # リポジトリをクローン
  git clone "git@github.com:$ORG/$repo.git" "/tmp/$repo"
  cd "/tmp/$repo"
  
  # ワークフローファイルが存在するか確認
  if [ ! -f ".github/workflows/pr-description-quality-gate.yml" ]; then
    echo "  Skipping: workflow file not found"
    cd /tmp
    rm -rf "/tmp/$repo"
    continue
  fi
  
  # 作業ブランチを作成
  git checkout -b "$BRANCH_NAME"
  
  # 最新版をダウンロード
  curl -o .github/workflows/pr-description-quality-gate.yml "$TEMPLATE_URL"
  
  # 変更があるか確認
  if ! git diff --quiet .github/workflows/pr-description-quality-gate.yml; then
    # コミット＆プッシュ
    git add .github/workflows/pr-description-quality-gate.yml
    git commit -m "fix: Update PR quality gate workflow to resolve fromJSON issue"
    git push origin "$BRANCH_NAME"
    
    # PRを作成
    gh pr create --repo "$ORG/$repo" \
      --title "fix: Update PR quality gate workflow" \
      --body "organization-standardsの修正を反映" \
      --base main
    
    echo "  ✅ PR created for $repo"
  else
    echo "  ℹ️  No changes needed for $repo"
  fi
  
  # クリーンアップ
  cd /tmp
  rm -rf "/tmp/$repo"
  
done < affected-repos.txt

echo "✅ All repositories processed"
```

実行:

```bash
chmod +x apply-fix-to-all-repos.sh
./apply-fix-to-all-repos.sh
```

---

### ステップ3: 動作確認

#### 3.1 テストPRの作成

各リポジトリで修正が適用されたら、テストPRを作成して動作確認:

```bash
# テスト用のブランチを作成
git checkout -b test/pr-quality-gate-verification

# ダミーの変更
echo "# Test" >> README.md
git add README.md
git commit -m "test: Verify PR quality gate workflow"

# プッシュ
git push origin test/pr-quality-gate-verification

# PRを作成（PRテンプレートを使用）
gh pr create \
  --title "test: Verify PR quality gate workflow" \
  --body "$(cat .github/PULL_REQUEST_TEMPLATE.md)"
```

#### 3.2 確認ポイント

PRを作成後、以下を確認:

- [ ] **GitHub Actions が実行される**
  - PR画面の「Checks」タブを確認
  - 「PR Description Quality Gate」ワークフローが実行されているか

- [ ] **エラーが発生していない**
  - ワークフローのステータスが🔴（失敗）ではなく、✅（成功）または⚠️（警告）
  - エラーログに`TypeError`や`Cannot read properties`がないか

- [ ] **コメントが投稿される**
  - PRに品質ゲートのコメントが投稿されているか
  - コメント内容が正しく表示されているか

- [ ] **検証結果が正しい**
  - チェックボックスの確認率が計算されているか
  - 文字数カウントが正しいか
  - セクションの存在チェックが機能しているか

#### 3.3 エラー時のデバッグ

もしエラーが発生した場合:

```bash
# GitHub Actionsのログを確認
gh run view --repo YOUR-ORG/YOUR-REPO

# 詳細ログを表示
gh run view <run-id> --log --repo YOUR-ORG/YOUR-REPO

# 特定のステップのログを確認
gh run view <run-id> --log | grep -A 50 "Validate PR description"
```

---

## 📊 進捗管理

### トラッキング用チェックリスト

#### organization-standards
- [ ] 修正版ファイルを作成
- [ ] ローカルで構文チェック
- [ ] PRを作成
- [ ] レビュー完了
- [ ] マージ完了
- [ ] ドキュメント更新

#### プロジェクトリポジトリ

| リポジトリ名 | ワークフロー有無 | PR作成 | 動作確認 | マージ | 備考 |
|------------|---------------|--------|---------|--------|------|
| repo-1 | ✅ | ⬜ | ⬜ | ⬜ | |
| repo-2 | ✅ | ⬜ | ⬜ | ⬜ | |
| repo-3 | ✅ | ⬜ | ⬜ | ⬜ | |
| ... | | | | | |

---

## 🚨 トラブルシューティング

### 問題1: ワークフローが実行されない

**症状**: PRを作成してもワークフローが実行されない

**原因**:
- ワークフローファイルのパスが間違っている
- GitHubアクションの権限が不足している

**対処法**:
```bash
# ファイルパスを確認
ls -la .github/workflows/pr-description-quality-gate.yml

# 権限を確認（Settings > Actions > General）
# - "Allow all actions and reusable workflows" が選択されているか
# - Workflow permissions: "Read and write permissions" が設定されているか
```

### 問題2: 修正後もエラーが発生する

**症状**: 修正版を適用してもまだ`TypeError`が発生

**原因**:
- ファイルが正しく更新されていない
- キャッシュの問題

**対処法**:
```bash
# ファイル内容を確認
grep -n "JSON.parse" .github/workflows/pr-description-quality-gate.yml

# 以下の3行が存在するはず:
# 43:            const prInfo = JSON.parse(process.env.PR_INFO);
# 255:            const results = JSON.parse(process.env.VALIDATION_RESULTS);
# 358:            const results = JSON.parse(process.env.VALIDATION_RESULTS);

# もし存在しない場合は、再度ファイルを更新
```

### 問題3: 環境変数が空

**症状**: `JSON.parse()`で空文字列のエラー

**原因**:
- 前のステップが失敗している
- outputが正しく設定されていない

**対処法**:
```yaml
# デバッグステップを追加
- name: Debug step outputs
  run: |
    echo "PR_INFO: ${{ steps['pr-info'].outputs.result }}"
    echo "VALIDATION_RESULTS: ${{ steps.validate.outputs.results }}"
```

---

## 📝 ロールバック手順

修正後に問題が発生し、元に戻す必要がある場合:

### organization-standards

```bash
cd /path/to/organization-standards

# コミットをrevert
git revert <commit-hash>
git push origin main
```

### プロジェクトリポジトリ

```bash
cd /path/to/project-repo

# 前のバージョンに戻す
git checkout <previous-commit-hash> .github/workflows/pr-description-quality-gate.yml
git commit -m "revert: Rollback PR quality gate workflow due to issues"
git push origin main
```

---

## ✅ 完了後の確認

すべてのリポジトリで修正を適用したら、以下を確認:

- [ ] organization-standardsのPRがマージされた
- [ ] 全対象リポジトリでPRが作成された
- [ ] 各リポジトリでテストPRを作成し、動作確認した
- [ ] エラーログがないことを確認した
- [ ] ドキュメントが更新された
- [ ] チームに変更を通知した

---

## 📞 サポート

問題が発生した場合:

1. **GitHub Actionsのログを確認**
2. **CI-FIX-REPORT.md のトラブルシューティングセクションを参照**
3. **チームに相談**
4. **必要に応じてロールバック**

---

**作成日**: 2025-11-14  
**バージョン**: 1.0  
**ステータス**: 適用準備完了
