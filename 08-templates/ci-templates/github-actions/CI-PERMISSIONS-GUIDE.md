# 📋 GitHub Actions 権限設定ガイド

**作成日**: 2025-11-14  
**目的**: 各CIワークフローに必要な権限設定を明確化

---

## 🎯 権限設定の必要性

GitHub Actionsでは、セキュリティのために**最小権限の原則**に基づいた権限設定が必要です。

### デフォルト権限の問題

リポジトリの設定で「Permissive」（許可的）が選択されている場合、すべてのワークフローに広範な権限が付与されます。これはセキュリティリスクになります。

### 推奨される設定

1. **リポジトリレベル**: `Settings > Actions > General > Workflow permissions` を「Read repository contents and packages permissions」に設定
2. **ワークフローレベル**: 各ワークフローファイルで明示的に必要な権限のみを指定

---

## 📊 各CIワークフローの推奨権限設定

### 1. PR Description Quality Gate

**ファイル**: `pr-description-quality-gate.yml`

**必要な権限**:
```yaml
permissions:
  contents: read           # リポジトリの読み取り
  pull-requests: write     # PRへのコメント、ラベル操作
  issues: write            # Issueコメント、ラベル操作（PRもIssueとして扱われる）
```

**理由**:
- `contents: read` - PRの内容を取得するため
- `pull-requests: write` - PRにコメントを投稿、ラベルを追加/削除するため
- `issues: write` - PRはIssueとしても扱われるため、コメント投稿に必要

---

### 2. PR Language Check

**ファイル**: `pr-language-check.yaml`

**現在の権限** (コメント機能削除後):
```yaml
permissions:
  contents: read           # リポジトリの読み取り
```

**推奨権限** (将来的にラベル追加する場合):
```yaml
permissions:
  contents: read           # リポジトリの読み取り
  pull-requests: write     # ラベル操作（オプション）
  issues: write            # ラベル操作（オプション）
```

**理由**:
- 現在はCI判定のみなので`contents: read`で十分
- ラベル追加機能を実装する場合は`pull-requests: write`と`issues: write`が必要

---

### 3. PR Self Review Reminder

**ファイル**: `pr-self-review-reminder.yml`

**必要な権限**:
```yaml
permissions:
  contents: read           # リポジトリの読み取り
  pull-requests: write     # PRへのコメント投稿
  issues: write            # Issueコメント操作
```

**理由**:
- `contents: read` - PRの内容を確認するため
- `pull-requests: write` - セルフレビューリマインダーのコメントを投稿するため
- `issues: write` - PRへのコメント投稿に必要

---

### 4. Code Quality (SonarCloud)

**ファイル**: `code-quality.yaml`

**必要な権限**:
```yaml
permissions:
  contents: read           # リポジトリの読み取り
  pull-requests: write     # PRへのコメント、ラベル操作
  issues: write            # Issueコメント、ラベル操作
  checks: write            # CIステータスの更新
  statuses: write          # コミットステータスの更新
```

**理由**:
- `contents: read` - ソースコードの読み取り
- `pull-requests: write` - 品質レポートのコメント投稿
- `issues: write` - PRへのコメント投稿
- `checks: write` - GitHub Checksへのレポート投稿
- `statuses: write` - コミットステータスの更新

---

### 5. Integration Test

**ファイル**: `integration-test.yaml`

**必要な権限**:
```yaml
permissions:
  contents: read           # リポジトリの読み取り
  pull-requests: write     # PRへのコメント、ラベル操作
  issues: write            # Issueコメント、ラベル操作
  checks: write            # CIステータスの更新
  statuses: write          # コミットステータスの更新
```

**理由**:
- `contents: read` - テストコードとソースコードの読み取り
- `pull-requests: write` - テスト結果のコメント投稿
- `issues: write` - PRへのコメント投稿
- `checks: write` - GitHub Checksへのテスト結果投稿
- `statuses: write` - コミットステータスの更新

---

### 6. Security Scan

**ファイル**: `security-scan.yaml`

**必要な権限**:
```yaml
permissions:
  contents: read           # リポジトリの読み取り
  pull-requests: write     # PRへのコメント、ラベル操作
  issues: write            # Issueコメント、ラベル操作
  checks: write            # CIステータスの更新
  statuses: write          # コミットステータスの更新
  security-events: write   # セキュリティアラートの更新
```

**理由**:
- `contents: read` - 依存関係ファイルの読み取り
- `pull-requests: write` - 脆弱性レポートのコメント投稿
- `issues: write` - PRへのコメント投稿
- `checks: write` - GitHub Checksへのスキャン結果投稿
- `statuses: write` - コミットステータスの更新
- `security-events: write` - GitHub Security Advisoriesへのアラート投稿

---

## 🔐 権限の種類と説明

### 基本権限

| 権限 | レベル | 説明 | 用途 |
|------|--------|------|------|
| `contents` | `read` / `write` | リポジトリの内容 | コードの読み取り・書き込み |
| `pull-requests` | `read` / `write` | プルリクエスト | PRの読み取り、コメント投稿、ラベル操作 |
| `issues` | `read` / `write` | Issue | Issueの読み取り、コメント投稿、ラベル操作 |
| `checks` | `read` / `write` | GitHub Checks | CI結果の投稿、ステータス更新 |
| `statuses` | `read` / `write` | コミットステータス | コミットステータスの更新 |

### 特殊権限

| 権限 | レベル | 説明 | 用途 |
|------|--------|------|------|
| `security-events` | `read` / `write` | セキュリティイベント | 脆弱性スキャン結果の投稿 |
| `packages` | `read` / `write` | パッケージレジストリ | パッケージの読み取り・公開 |
| `deployments` | `read` / `write` | デプロイメント | デプロイステータスの管理 |
| `actions` | `read` / `write` | GitHub Actions | ワークフローの管理 |

---

## 📝 権限設定のベストプラクティス

### 1. 最小権限の原則

```yaml
# ❌ 悪い例: 必要以上の権限
permissions:
  contents: write           # 読み取りだけで十分なのに書き込み権限
  pull-requests: write
  issues: write
  checks: write
  statuses: write
  packages: write           # 使用していない権限
  deployments: write        # 使用していない権限

# ✅ 良い例: 必要最小限の権限
permissions:
  contents: read            # 読み取りのみ
  pull-requests: write      # 実際に使用
  issues: write             # 実際に使用
```

### 2. 明示的な権限指定

```yaml
# ❌ 悪い例: 権限指定なし（デフォルトに依存）
name: My Workflow
on: [pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps: [...]

# ✅ 良い例: 明示的に指定
name: My Workflow
on: [pull_request]
permissions:
  contents: read
  pull-requests: write
jobs:
  test:
    runs-on: ubuntu-latest
    steps: [...]
```

### 3. コメント付きで理由を明記

```yaml
permissions:
  contents: read           # リポジトリの読み取り
  pull-requests: write     # PRへのコメント投稿
  issues: write            # Issueコメント操作（PRもIssueとして扱われる）
```

---

## 🚀 適用手順

### ステップ1: リポジトリレベルの設定

1. **Settings > Actions > General** に移動
2. **Workflow permissions** セクションを確認
3. 「Read repository contents and packages permissions」を選択
4. 「Save」をクリック

### ステップ2: 各ワークフローファイルの確認

各ワークフローファイルに適切な`permissions:`セクションがあることを確認:

```bash
# 権限設定の確認
grep -A 5 "^permissions:" .github/workflows/*.yaml
```

### ステップ3: 不足している権限を追加

本ガイドの推奨設定に従って、各ワークフローファイルを更新。

---

## 🔍 トラブルシューティング

### 問題1: 権限エラーが発生する

**エラー例**:
```
Error: Resource not accessible by integration
```

**原因**:
- ワークフローに必要な権限が付与されていない

**対処法**:
1. エラーが発生したステップを確認
2. 必要な権限を特定
3. `permissions:`セクションに追加

### 問題2: コメント投稿が失敗する

**症状**:
- PRへのコメント投稿が失敗
- `403 Forbidden` エラー

**原因**:
- `pull-requests: write` または `issues: write` 権限がない

**対処法**:
```yaml
permissions:
  contents: read
  pull-requests: write     # 追加
  issues: write            # 追加
```

### 問題3: ラベル操作が失敗する

**症状**:
- ラベルの追加/削除が失敗

**原因**:
- `pull-requests: write` と `issues: write` 権限が必要

**対処法**:
```yaml
permissions:
  contents: read
  pull-requests: write     # ラベル操作に必要
  issues: write            # ラベル操作に必要
```

---

## 📚 参考資料

### 公式ドキュメント
- [GitHub Actions: Permissions](https://docs.github.com/en/actions/security-guides/automatic-token-authentication#permissions-for-the-github_token)
- [GitHub Actions: Security guides](https://docs.github.com/en/actions/security-guides)

### 内部ドキュメント
- [GITHUB-ACTIONS-PERMISSIONS-SETUP.md](./GITHUB-ACTIONS-PERMISSIONS-SETUP.md)
- [各ワークフローの説明文書](./README.md)

---

## ✅ チェックリスト

### リポジトリ設定
- [ ] Workflow permissions を「Read repository contents and packages permissions」に設定

### 各ワークフローファイル
- [ ] `pr-description-quality-gate.yml` - 権限設定を確認
- [ ] `pr-language-check.yaml` - 権限設定を確認
- [ ] `pr-self-review-reminder.yml` - 権限設定を確認
- [ ] `code-quality.yaml` - 権限設定を確認
- [ ] `integration-test.yaml` - 権限設定を確認
- [ ] `security-scan.yaml` - 権限設定を確認

### テスト
- [ ] 各ワークフローが正常に実行される
- [ ] 権限エラーが発生しない
- [ ] コメント投稿が正常に動作する
- [ ] ラベル操作が正常に動作する

---

**作成日**: 2025-11-14  
**バージョン**: 1.0  
**ステータス**: ✅ 完成
