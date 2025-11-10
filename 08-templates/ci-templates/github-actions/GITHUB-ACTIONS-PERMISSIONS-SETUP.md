# GitHub Actions API呼び出し権限設定ガイド

**作成日**: 2025-11-10  
**対象**: GitHub Actions ワークフローでのAPI呼び出し設定  
**目的**: GitHub APIへのアクセス権限の適切な設定方法

---

## 📋 概要

GitHub ActionsからGitHub API（コメント追加、ラベル管理など）を呼び出すには、適切な**権限設定**が必要です。

このガイドでは、3つのレベルでの設定方法を説明します。

---

## 🔐 権限設定の3つのレベル

```
優先順位（上から順に適用）

1. ワークフローファイル（.github/workflows/*.yml）
   ├─ permissions: セクション ← 最優先・推奨
   └─ 各ジョブごとに細かく制御可能

2. リポジトリ設定
   ├─ Settings → Actions → General
   └─ デフォルトの権限を設定

3. Organization 設定（該当する場合）
   ├─ Organization Settings → Actions → General
   └─ Organization 全体のデフォルト
```

---

## ✅ 方法1: ワークフローファイルでの設定（推奨）

### 最も推奨される方法

ワークフローファイルに `permissions:` セクションを追加することで、**明示的に必要な権限のみ**を付与できます。

### 設定例

```yaml
name: PR Self-Review Reminder

on:
  pull_request:
    types: [opened, synchronize]

# ✅ ここで権限を明示的に設定（推奨）
permissions:
  contents: read           # リポジトリの読み取り
  pull-requests: write     # PRへのコメント、ラベル操作
  issues: write            # Issueコメント、ラベル操作（PRもIssueとして扱われる）

jobs:
  remind-checklist:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/github-script@v7
        with:
          script: |
            await github.rest.issues.createComment({
              owner: context.repo.owner,
              repo: context.repo.repo,
              issue_number: context.issue.number,
              body: 'コメント'
            });
```

### メリット

- ✅ **セキュリティ強化**: 必要最小限の権限のみ付与（最小権限の原則）
- ✅ **明示的**: コードレビュー時に権限が一目で分かる
- ✅ **ポータブル**: リポジトリ間でワークフローをコピーしても動作
- ✅ **ジョブごとに制御可能**: より細かい権限管理

### 利用可能な権限スコープ

| 権限 | 説明 | 値 |
|------|------|---|
| `contents` | リポジトリの内容 | `read`, `write`, `none` |
| `pull-requests` | Pull Request | `read`, `write`, `none` |
| `issues` | Issues（PRもIssueとして扱われる） | `read`, `write`, `none` |
| `statuses` | コミットステータス | `read`, `write`, `none` |
| `checks` | Checks（CI状態） | `read`, `write`, `none` |
| `deployments` | デプロイメント | `read`, `write`, `none` |
| `packages` | GitHub Packages | `read`, `write`, `none` |

**完全なリスト**: [GitHub Actions Permissions](https://docs.github.com/en/actions/security-guides/automatic-token-authentication#permissions-for-the-github_token)

---

## ⚙️ 方法2: リポジトリレベルでの設定

### 設定手順

1. **リポジトリのSettings に移動**
   ```
   Repository → Settings → Actions → General
   ```

2. **"Workflow permissions" セクションまでスクロール**

3. **以下のいずれかを選択**:

   #### オプション A: Read and write permissions（推奨）
   ```
   ○ Read and write permissions
     Workflows have read and write permissions in the repository 
     for all scopes.
   
   ☑ Allow GitHub Actions to create and approve pull requests
   ```
   
   **効果**:
   - すべてのワークフローがデフォルトで読み書き権限を持つ
   - コメント、ラベル、PRの作成・承認が可能
   
   **メリット**:
   - 設定が簡単
   - ワークフローファイルに `permissions:` を書かなくても動作
   
   **デメリット**:
   - セキュリティリスクが高い（すべてのワークフローに強い権限）
   - 明示的でない

   #### オプション B: Read repository contents and packages permissions（セキュア）
   ```
   ● Read repository contents and packages permissions
     Workflows have read permissions in the repository 
     for the contents and packages scopes only.
   
   □ Allow GitHub Actions to create and approve pull requests
   ```
   
   **効果**:
   - デフォルトは読み取りのみ
   - 書き込みが必要な場合は、ワークフローファイルで明示的に指定が必要
   
   **メリット**:
   - セキュリティが高い（最小権限の原則）
   - 明示的な権限付与が必須
   
   **デメリット**:
   - 各ワークフローで `permissions:` を明示する必要がある

### 推奨設定

```
● Read repository contents and packages permissions
□ Allow GitHub Actions to create and approve pull requests
```

**理由**: 
- セキュリティを優先
- 各ワークフローで必要な権限を明示的に指定（方法1）

---

## 🏢 方法3: Organization レベルでの設定

### 設定手順（Organization オーナーのみ）

1. **Organization Settings に移動**
   ```
   Organization → Settings → Actions → General
   ```

2. **"Workflow permissions" セクションまでスクロール**

3. **リポジトリと同様の選択肢から選ぶ**

### Organization 設定の優先順位

```
Organization 設定（デフォルト）
  ↓
各リポジトリで上書き可能
  ↓
各ワークフローファイルで上書き可能（最優先）
```

---

## 🎯 推奨設定の組み合わせ

### ベストプラクティス

```yaml
# ✅ 推奨: ワークフローファイルに明示的に記載

# リポジトリ設定
Settings → Actions → General
└─ Read repository contents and packages permissions (デフォルト)

# ワークフローファイル
permissions:
  contents: read           # 必要最小限
  pull-requests: write     # 必要に応じて
  issues: write            # 必要に応じて
```

### メリット

1. **セキュリティ**: 各ワークフローが必要最小限の権限のみ持つ
2. **明示性**: コードレビューで権限が明確
3. **保守性**: 権限の変更がGit履歴に残る
4. **ポータブル**: 他のリポジトリにコピーしても動作

---

## 🔍 権限が必要なAPI操作

### PR/Issue関連（`pull-requests: write` / `issues: write`）

| 操作 | 必要な権限 |
|------|----------|
| コメント追加 | `issues: write` または `pull-requests: write` |
| コメント編集 | `issues: write` または `pull-requests: write` |
| コメント削除 | `issues: write` または `pull-requests: write` |
| ラベル追加 | `issues: write` または `pull-requests: write` |
| ラベル削除 | `issues: write` または `pull-requests: write` |
| PR作成 | `pull-requests: write` |
| PRマージ | `pull-requests: write` + `contents: write` |
| レビュー投稿 | `pull-requests: write` |

### リポジトリ関連（`contents: write`）

| 操作 | 必要な権限 |
|------|----------|
| ファイル読み取り | `contents: read` |
| ファイル変更 | `contents: write` |
| ブランチ作成 | `contents: write` |
| タグ作成 | `contents: write` |
| コミット作成 | `contents: write` |

---

## 🚨 トラブルシューティング

### エラー1: "Resource not accessible by integration"

```
Error: Resource not accessible by integration
```

**原因**: 権限が不足している

**解決方法**:

#### Step 1: ワークフローファイルを確認

```yaml
# ✅ permissions セクションがあるか確認
permissions:
  contents: read
  pull-requests: write  # ← この権限があるか？
  issues: write         # ← この権限があるか？
```

#### Step 2: リポジトリ設定を確認

```
Repository → Settings → Actions → General
→ Workflow permissions
→ "Read and write permissions" または
   ワークフローファイルで権限を明示
```

#### Step 3: GitHub Actions のログを確認

```bash
# Actions タブ → 該当のワークフロー実行 → ログを確認
# どのAPI呼び出しで失敗しているか確認
```

---

### エラー2: "Bad credentials"

```
Error: Bad credentials
```

**原因**: `GITHUB_TOKEN` が正しく渡されていない

**解決方法**:

```yaml
# ✅ actions/github-script では自動的に GITHUB_TOKEN が使われる
- uses: actions/github-script@v7
  with:
    script: |
      // 明示的にトークンを指定する必要はない
      await github.rest.issues.createComment({...});

# ❌ 他のアクションで明示的に必要な場合
- uses: some-action@v1
  with:
    github-token: ${{ secrets.GITHUB_TOKEN }}  # ← 明示的に渡す
```

---

### エラー3: ワークフローが実行されない

**原因**: Actions が無効化されている

**解決方法**:

```
Repository → Settings → Actions → General
→ Actions permissions
→ "Allow all actions and reusable workflows" を選択
```

---

## 📊 現在のワークフローに必要な権限

### `pr-self-review-reminder.yml`

```yaml
permissions:
  contents: read           # ✅ 必須（リポジトリ情報の読み取り）
  pull-requests: write     # ✅ 必須（PRへのコメント、ラベル）
  issues: write            # ✅ 必須（PRはIssueとしても扱われる）
```

### 他のCI品質ゲートテンプレート

#### `pr-language-check.yaml`
```yaml
permissions:
  contents: read
  pull-requests: write
  issues: write
```

#### `code-quality.yaml`, `integration-test.yaml`, `security-scan.yaml`
```yaml
permissions:
  contents: read
  pull-requests: write
  issues: write
  checks: write           # ✅ CI ステータスの更新
```

---

## ✅ セットアップチェックリスト

### 初回セットアップ時

- [ ] **ワークフローファイルに `permissions:` セクションを追加**
  ```yaml
  permissions:
    contents: read
    pull-requests: write
    issues: write
  ```

- [ ] **リポジトリ設定を確認**
  - Settings → Actions → General
  - Workflow permissions: "Read repository contents..." を推奨

- [ ] **テストPRで動作確認**
  - テストPRを作成
  - ワークフローが実行されるか確認
  - コメントが投稿されるか確認
  - ラベルが追加されるか確認

- [ ] **エラーログを確認**
  - Actions タブでログを確認
  - エラーがあれば上記トラブルシューティングを参照

---

## 🔒 セキュリティベストプラクティス

### 1. 最小権限の原則

```yaml
# ✅ 良い例: 必要な権限のみ
permissions:
  contents: read
  pull-requests: write

# ❌ 悪い例: すべての権限
permissions: write-all  # 使用しない！
```

### 2. ジョブごとの権限制御

```yaml
permissions:
  contents: read  # デフォルト: 読み取りのみ

jobs:
  read-only-job:
    runs-on: ubuntu-latest
    # このジョブは contents: read のみ
    
  write-job:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      pull-requests: write  # このジョブのみ書き込み可能
```

### 3. Personal Access Token (PAT) は使わない

```yaml
# ❌ 悪い例: PAT を使う
- uses: actions/github-script@v7
  with:
    github-token: ${{ secrets.MY_PERSONAL_TOKEN }}  # 推奨しない

# ✅ 良い例: GITHUB_TOKEN を使う（デフォルト）
- uses: actions/github-script@v7
  # github-token は指定不要（自動的に GITHUB_TOKEN が使われる）
```

**理由**:
- `GITHUB_TOKEN` は一時的（ワークフロー実行終了で無効化）
- PATは永続的で、漏洩リスクが高い
- `GITHUB_TOKEN` の権限は `permissions:` で制御可能

---

## 📚 参考資料

### 公式ドキュメント

- [Automatic token authentication](https://docs.github.com/en/actions/security-guides/automatic-token-authentication)
- [Permissions for the GITHUB_TOKEN](https://docs.github.com/en/actions/security-guides/automatic-token-authentication#permissions-for-the-github_token)
- [Workflow syntax - permissions](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions#permissions)

### 関連ドキュメント

- **PRセルフレビューリマインダー**: `pr-self-review-reminder.md`
- **GitHub Actions テンプレート集**: `README.md`
- **PR言語チェック**: `pr-language-check.md`

---

## 🎯 まとめ

### API呼び出しを許可するための設定手順

1. ✅ **ワークフローファイルに `permissions:` を追加（推奨）**
   ```yaml
   permissions:
     contents: read
     pull-requests: write
     issues: write
   ```

2. ✅ **リポジトリ設定を確認**
   - Settings → Actions → General
   - Workflow permissions の確認

3. ✅ **テストPRで動作確認**

4. ✅ **エラーがあればトラブルシューティングを参照**

### ベストプラクティス

- 🔒 **最小権限の原則**: 必要な権限のみ付与
- 📝 **明示的に記載**: `permissions:` セクションをワークフローファイルに記載
- 🧪 **テスト**: 実際のPRで動作確認
- 📖 **ドキュメント化**: チーム内で権限設定を共有

---

**作成者**: AI Assistant  
**最終更新**: 2025-11-10  
**ドキュメントバージョン**: 1.0
