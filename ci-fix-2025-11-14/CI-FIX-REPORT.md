# 🔧 CI設定ファイル修正レポート

## 📋 概要

**ファイル**: `pr-description-quality-gate.yml`  
**問題**: GitHub Actionsの式評価がJavaScriptコード内で正しく動作しない  
**修正方法**: 環境変数 + JSON.parseアプローチ  
**影響範囲**: 3箇所の修正が必要

---

## ❌ 問題の詳細

### 根本原因

GitHub Actionsの`fromJSON()`関数を**JavaScriptコード内**で使用すると、式評価のタイミングの問題で以下のエラーが発生します：

```javascript
// ❌ 問題のあるコード
const prInfo = ${{ fromJSON(steps['pr-info'].outputs.result) }};
```

**発生する問題**:
1. `${{ }}` 式がJavaScriptのパース前に評価される
2. 結果として `const prInfo = Object;` のような無効なコードになる
3. `TypeError: Cannot read properties of undefined` エラー
4. CI実行が失敗する

### 影響箇所

以下の**3箇所**で同じ問題が発生:

| 行番号 | ステップ名 | 問題のコード |
|--------|-----------|-------------|
| 41 | Validate PR description | `const prInfo = ${{ fromJSON(steps['pr-info'].outputs.result) }};` |
| 253 | Post validation comment | `const results = ${{ fromJSON(steps.validate.outputs.results) }};` |
| 356 | Post success comment | `const results = ${{ fromJSON(steps.validate.outputs.results) }};` |

---

## ✅ 修正内容

### 修正アプローチ: 環境変数 + JSON.parse

GitHub Actionsの式評価を**環境変数**に保存し、JavaScript内で`JSON.parse()`を使用してパースします。

### 修正箇所1: Validate PR description（41行目）

#### ❌ 修正前
```yaml
- name: Validate PR description
  id: validate
  uses: actions/github-script@v7
  with:
    script: |
      const prInfo = ${{ fromJSON(steps['pr-info'].outputs.result) }};
      const prBody = prInfo.body || '';
```

#### ✅ 修正後
```yaml
- name: Validate PR description
  id: validate
  uses: actions/github-script@v7
  env:
    PR_INFO: ${{ steps['pr-info'].outputs.result }}
  with:
    script: |
      const prInfo = JSON.parse(process.env.PR_INFO);
      const prBody = prInfo.body || '';
```

**変更点**:
1. `env:` セクションを追加
2. `PR_INFO` 環境変数に結果を格納（fromJSONを使わない）
3. JavaScript内で `JSON.parse(process.env.PR_INFO)` を使用

---

### 修正箇所2: Post validation comment（253行目）

#### ❌ 修正前
```yaml
- name: Post validation comment
  if: steps.validate.outputs.passed == 'false'
  uses: actions/github-script@v7
  with:
    script: |
      const results = ${{ fromJSON(steps.validate.outputs.results) }};
```

#### ✅ 修正後
```yaml
- name: Post validation comment
  if: steps.validate.outputs.passed == 'false'
  uses: actions/github-script@v7
  env:
    VALIDATION_RESULTS: ${{ steps.validate.outputs.results }}
  with:
    script: |
      const results = JSON.parse(process.env.VALIDATION_RESULTS);
```

**変更点**:
1. `env:` セクションを追加
2. `VALIDATION_RESULTS` 環境変数に結果を格納
3. JavaScript内で `JSON.parse(process.env.VALIDATION_RESULTS)` を使用

---

### 修正箇所3: Post success comment（356行目）

#### ❌ 修正前
```yaml
- name: Post success comment
  if: steps.validate.outputs.passed == 'true'
  uses: actions/github-script@v7
  with:
    script: |
      const results = ${{ fromJSON(steps.validate.outputs.results) }};
```

#### ✅ 修正後
```yaml
- name: Post success comment
  if: steps.validate.outputs.passed == 'true'
  uses: actions/github-script@v7
  env:
    VALIDATION_RESULTS: ${{ steps.validate.outputs.results }}
  with:
    script: |
      const results = JSON.parse(process.env.VALIDATION_RESULTS);
```

**変更点**:
1. `env:` セクションを追加
2. `VALIDATION_RESULTS` 環境変数に結果を格納
3. JavaScript内で `JSON.parse(process.env.VALIDATION_RESULTS)` を使用

---

## 🔍 修正の詳細差分

```diff
--- pr-description-quality-gate.yml
+++ pr-description-quality-gate-fixed.yml
@@ -36,9 +36,11 @@
       - name: Validate PR description
         id: validate
         uses: actions/github-script@v7
+        env:
+          PR_INFO: ${{ steps['pr-info'].outputs.result }}
         with:
           script: |
-            const prInfo = ${{ fromJSON(steps['pr-info'].outputs.result) }};
+            const prInfo = JSON.parse(process.env.PR_INFO);
             const prBody = prInfo.body || '';
             const prTitle = prInfo.title || '';
             
@@ -248,9 +250,11 @@
       - name: Post validation comment
         if: steps.validate.outputs.passed == 'false'
         uses: actions/github-script@v7
+        env:
+          VALIDATION_RESULTS: ${{ steps.validate.outputs.results }}
         with:
           script: |
-            const results = ${{ fromJSON(steps.validate.outputs.results) }};
+            const results = JSON.parse(process.env.VALIDATION_RESULTS);
             
@@ -351,9 +355,11 @@
       - name: Post success comment
         if: steps.validate.outputs.passed == 'true'
         uses: actions/github-script@v7
+        env:
+          VALIDATION_RESULTS: ${{ steps.validate.outputs.results }}
         with:
           script: |
-            const results = ${{ fromJSON(steps.validate.outputs.results) }};
+            const results = JSON.parse(process.env.VALIDATION_RESULTS);
```

---

## 🎯 なぜこの修正が正しいのか

### GitHub Actionsの式評価の仕組み

1. **式評価のタイミング**: `${{ }}` は**ワークフロー実行時**に評価される
2. **JavaScriptのパース**: `actions/github-script`の`script:`内のコードは**その後**にパースされる
3. **問題**: 式評価結果がJavaScriptコードの一部になると構文エラーが発生

### 環境変数アプローチの利点

| 利点 | 説明 |
|------|------|
| ✅ **式評価の分離** | GitHub Actionsの式評価と、JavaScriptのパースが分離される |
| ✅ **型安全性** | 環境変数は常に文字列として扱われるため、予測可能 |
| ✅ **デバッグしやすい** | 環境変数の値を確認できる（`echo "$PR_INFO"`） |
| ✅ **公式推奨** | GitHub Actionsのベストプラクティス |

---

## 📊 検証方法

### ローカルでの構文チェック

```bash
# YAMLの構文チェック
yamllint pr-description-quality-gate-fixed.yml

# GitHub Actions CLIでの検証（オプション）
gh workflow view .github/workflows/pr-description-quality-gate-fixed.yml
```

### 実際のPRでのテスト手順

1. **修正版ファイルをコミット**
   ```bash
   cp pr-description-quality-gate-fixed.yml .github/workflows/pr-description-quality-gate.yml
   git add .github/workflows/pr-description-quality-gate.yml
   git commit -m "fix: Resolve fromJSON expression evaluation issue in PR quality gate"
   ```

2. **テストPRを作成**
   - 新しいブランチを作成
   - ダミーの変更をコミット
   - PRを開く

3. **以下をテスト**
   - ✅ PR作成時にワークフローが実行される
   - ✅ エラーが発生しない
   - ✅ コメントが正しく投稿される
   - ✅ 検証結果が正しく表示される

---

## 🚨 再発防止策

### 1. ドキュメント化

以下の内容をドキュメントに追加:

**タイトル**: GitHub Actions内でのJSON処理のベストプラクティス

**内容**:
```markdown
### ❌ やってはいけないこと

JavaScriptコード内でGitHub Actionsの式評価を使用しない:

```yaml
# ❌ NG
script: |
  const data = ${{ fromJSON(steps.output.result) }};
```

### ✅ 推奨される方法

環境変数経由でデータを渡す:

```yaml
# ✅ OK
env:
  DATA: ${{ steps.output.result }}
script: |
  const data = JSON.parse(process.env.DATA);
```
```

### 2. PRレビューチェックリスト

以下をPRレビューチェックリストに追加:

- [ ] GitHub ActionsのYAMLファイルで`${{ }}`がJavaScriptコード内に記述されていないか
- [ ] `fromJSON()`が`actions/github-script`の`script:`ブロック内で使われていないか
- [ ] JSON処理は環境変数経由で行われているか

### 3. CI/CDテンプレートの更新

`08-templates/ci-templates/github-actions/`配下のテンプレートに、正しいパターンの例を追加:

```yaml
# ✅ 推奨パターン: 環境変数 + JSON.parse
- name: Process data
  uses: actions/github-script@v7
  env:
    INPUT_DATA: ${{ steps.previous.outputs.data }}
  with:
    script: |
      const data = JSON.parse(process.env.INPUT_DATA);
      // データを処理
```

---

## 📝 適用手順

### 1. organization-standardsリポジトリでの修正

```bash
# 1. 修正版ファイルを配置
cp pr-description-quality-gate-fixed.yml \
   /path/to/organization-standards/08-templates/ci-templates/github-actions/pr-description-quality-gate.yml

# 2. コミット
cd /path/to/organization-standards
git add 08-templates/ci-templates/github-actions/pr-description-quality-gate.yml
git commit -m "fix: Resolve fromJSON expression evaluation issue in PR quality gate

- Replace fromJSON() in JavaScript code with env + JSON.parse approach
- Fixes: TypeError when accessing step outputs
- Affected: 3 steps (validate, post-validation-comment, post-success-comment)
- Reference: CI-FIX-REPORT.md"

# 3. プッシュ
git push origin main
```

### 2. 各リポジトリへの適用

各リポジトリで以下を実行:

```bash
# 1. 最新のorganization-standardsから取得
wget -O .github/workflows/pr-description-quality-gate.yml \
  https://raw.githubusercontent.com/YOUR-ORG/organization-standards/main/08-templates/ci-templates/github-actions/pr-description-quality-gate.yml

# 2. コミット＆プッシュ
git add .github/workflows/pr-description-quality-gate.yml
git commit -m "fix: Update PR quality gate workflow to resolve fromJSON issue"
git push
```

---

## 📚 関連ドキュメント

- [GitHub Actions: Contexts](https://docs.github.com/en/actions/learn-github-actions/contexts)
- [GitHub Actions: Environment variables](https://docs.github.com/en/actions/learn-github-actions/environment-variables)
- [GitHub Actions: Expressions](https://docs.github.com/en/actions/learn-github-actions/expressions)
- [actions/github-script ドキュメント](https://github.com/actions/github-script)

---

## ✅ チェックリスト

修正適用前に以下を確認:

- [ ] 修正内容を理解した
- [ ] ローカルでYAML構文をチェックした
- [ ] organization-standardsリポジトリに修正をコミットした
- [ ] テストPRで動作確認を行った
- [ ] エラーが解消されることを確認した
- [ ] 他のリポジトリへの展開計画を立てた
- [ ] ドキュメントを更新した（ベストプラクティス）
- [ ] PRレビューチェックリストを更新した

---

## 📞 サポート

この修正に関して問題が発生した場合:

1. **GitHub Actionsのログを確認**
   - PRページ → Checks → 各ステップの詳細ログ
   
2. **環境変数の値を確認**
   ```yaml
   - name: Debug
     run: |
       echo "PR_INFO: $PR_INFO"
       echo "VALIDATION_RESULTS: $VALIDATION_RESULTS"
   ```

3. **ロールバック手順**
   ```bash
   # 以前のバージョンに戻す
   git revert <commit-hash>
   git push
   ```

---

**作成日**: 2025-11-14  
**バージョン**: 1.0  
**ステータス**: 修正完了・適用準備完了
