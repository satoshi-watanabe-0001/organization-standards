# ⚙️ GitHub Actions リポジトリ設定ガイド

**作成日**: 2025-11-14  
**目的**: 各CIワークフローを実行するために必要なリポジトリ設定を明確化

---

## 🎯 概要

GitHub Actionsのワークフローを正常に実行するには、リポジトリレベルでの設定が必要です。このガイドでは、organization-standardsのCIテンプレートを使用する際に必要な設定手順を説明します。

---

## 📋 必須設定

### 1. Workflow Permissions（ワークフロー権限）

**設定場所**: `Settings > Actions > General > Workflow permissions`

#### 推奨設定

```
⚪ Read repository contents and packages permissions (推奨)
   └─ この設定を選択することで、各ワークフローファイルで明示的に権限を指定する必要があります
   └─ セキュリティ上最も安全な設定です

❌ Read and write permissions
   └─ すべてのワークフローに広範な権限が付与されます
   └─ セキュリティリスクがあるため推奨しません
```

#### 設定手順

1. リポジトリのページに移動
2. **Settings**タブをクリック
3. 左サイドバーの**Actions** > **General**をクリック
4. **Workflow permissions**セクションまでスクロール
5. **Read repository contents and packages permissions**を選択
6. （オプション）**Allow GitHub Actions to create and approve pull requests**のチェックボックスを確認
   - 通常のCIワークフローでは**チェック不要**
   - 自動PR作成を行うワークフローがある場合のみチェック
7. **Save**ボタンをクリック

---

### 2. Actions Permissions（Actions権限）

**設定場所**: `Settings > Actions > General > Actions permissions`

#### 推奨設定

```
✅ Allow all actions and reusable workflows (推奨)
   └─ すべてのGitHub Actions Marketplaceのアクションを使用可能
   └─ organization-standardsのテンプレートはこの設定を前提としています

⚠️ Allow [organization] actions and reusable workflows
   └─ 組織内のアクションのみ許可
   └─ 外部アクションを使用する場合は、個別に許可リストに追加が必要

⚠️ Allow select actions and reusable workflows
   └─ 特定のアクションのみ許可
   └─ セキュリティは高いが、管理が煩雑
```

#### 設定手順

1. リポジトリのページに移動
2. **Settings**タブをクリック
3. 左サイドバーの**Actions** > **General**をクリック
4. **Actions permissions**セクションを確認
5. **Allow all actions and reusable workflows**を選択
6. **Save**ボタンをクリック

---

### 3. Fork Pull Request Workflows（フォークPRワークフロー）

**設定場所**: `Settings > Actions > General > Fork pull request workflows from outside collaborators`

#### 推奨設定

```
⚪ Require approval for first-time contributors (推奨)
   └─ 初回コントリビューターのワークフロー実行には承認が必要
   └─ セキュリティとコラボレーションのバランスが良い

⚠️ Require approval for all outside collaborators
   └─ すべての外部コラボレーターのワークフロー実行に承認が必要
   └─ 非常に厳格だが、コラボレーションが煩雑になる

❌ Require approval for first-time contributors who are new to GitHub
   └─ GitHub新規ユーザーのみ承認が必要
   └─ セキュリティが低い
```

#### 設定手順

1. リポジトリのページに移動
2. **Settings**タブをクリック
3. 左サイドバーの**Actions** > **General**をクリック
4. **Fork pull request workflows from outside collaborators**セクションを確認
5. **Require approval for first-time contributors**を選択
6. **Save**ボタンをクリック

---

## 🔧 各CIワークフロー固有の設定

### 1. SonarCloud (code-quality.yaml)

**追加設定**:

#### Repository Secrets

`Settings > Secrets and variables > Actions > Repository secrets`

| Secret名 | 説明 | 取得方法 |
|---------|------|---------|
| `SONAR_TOKEN` | SonarCloudの認証トークン | SonarCloud Dashboard > My Account > Security > Generate Token |
| `SONAR_HOST_URL` | SonarCloudのURL（オプション） | 通常は `https://sonarcloud.io` |

#### SonarCloud プロジェクト設定

1. [SonarCloud](https://sonarcloud.io/)にログイン
2. プロジェクトを作成
3. プロジェクトキーを取得
4. `sonar-project.properties`ファイルをリポジトリルートに作成

```properties
sonar.projectKey=your-organization_your-repo
sonar.organization=your-organization

# ソースコードのパス
sonar.sources=src

# テストコードのパス（オプション）
sonar.tests=tests

# 除外するファイル（オプション）
sonar.exclusions=**/node_modules/**,**/target/**
```

---

### 2. Security Scan (security-scan.yaml)

**追加設定**: 特になし

**注意事項**:
- GitHub Advanced Securityが有効な場合、より詳細なスキャン結果が得られます
- `security-events: write`権限が必要です（ワークフローファイルに設定済み）

---

### 3. PR Description Quality Gate (pr-description-quality-gate.yml)

**追加設定**:

#### PR Template

`.github/PULL_REQUEST_TEMPLATE.md`を作成して、標準的なPR記載フォーマットを提供

```markdown
## 📋 変更内容の概要
<!-- 変更内容を50文字以上で記載してください -->

## 🎯 関連チケット
<!-- 関連するIssue番号を記載: #123, PBI-456 など -->

## 🔍 変更の詳細
<!-- 具体的な変更内容を記載 -->

## ✅ テストの実施状況
- [ ] 単体テスト実施済み
- [ ] 統合テスト実施済み

## 🔒 セキュリティチェック
- [ ] 脆弱性スキャン完了
- [ ] 個人情報の確認完了

## ✅ 組織標準チェックリスト
- [ ] 日本語での記載を完了
- [ ] コーディング規約に準拠
```

---

### 4. PR Language Check (pr-language-check.yaml)

**追加設定**: 特になし

**注意事項**:
- 日本語（ひらがな、カタカナ、漢字）の検出に特化
- PRタイトルと説明文の両方をチェック

---

### 5. PR Self Review Reminder (pr-self-review-reminder.yml)

**追加設定**:

#### ラベル設定（オプション）

以下のラベルを作成すると、より効果的に動作します:

| ラベル名 | 色 | 説明 |
|---------|-----|------|
| `needs-self-review` | `#d73a4a` | セルフレビューが必要 |
| `self-review-done` | `#0e8a16` | セルフレビュー完了 |

**ラベル作成手順**:
1. `Settings > Labels` に移動
2. **New label**をクリック
3. ラベル名と色を設定
4. **Create label**をクリック

---

## 📊 設定確認チェックリスト

### リポジトリ設定

- [ ] **Workflow permissions**: "Read repository contents and packages permissions"に設定
- [ ] **Actions permissions**: "Allow all actions and reusable workflows"に設定
- [ ] **Fork PR workflows**: "Require approval for first-time contributors"に設定

### Secrets設定（該当する場合）

- [ ] `SONAR_TOKEN`を設定（SonarCloud使用時）
- [ ] その他の必要なSecretsを設定

### ファイル配置

- [ ] `.github/workflows/`ディレクトリに各CIファイルを配置
- [ ] `.github/PULL_REQUEST_TEMPLATE.md`を配置（推奨）
- [ ] `sonar-project.properties`を配置（SonarCloud使用時）

### 動作確認

- [ ] テストPRを作成して各ワークフローが実行されることを確認
- [ ] 権限エラーが発生しないことを確認
- [ ] コメント投稿が正常に動作することを確認

---

## 🚨 トラブルシューティング

### 問題1: ワークフローが実行されない

**症状**:
- PRを作成してもCIが実行されない

**確認事項**:
1. `Settings > Actions > General` でActionsが有効か
2. ワークフローファイルが`.github/workflows/`ディレクトリに配置されているか
3. ワークフローファイルのYAML構文が正しいか

**対処法**:
```bash
# YAML構文チェック
yamllint .github/workflows/*.yaml
```

---

### 問題2: 権限エラーが発生

**症状**:
- `Resource not accessible by integration`エラー

**確認事項**:
1. Workflow permissionsが"Read repository contents..."に設定されているか
2. 各ワークフローファイルに`permissions:`セクションがあるか

**対処法**:
各ワークフローファイルに適切な権限設定を追加（[CI-PERMISSIONS-GUIDE.md](./CI-PERMISSIONS-GUIDE.md)参照）

---

### 問題3: SonarCloudスキャンが失敗

**症状**:
- SonarCloudへの接続エラー

**確認事項**:
1. `SONAR_TOKEN`が正しく設定されているか
2. `sonar-project.properties`が存在するか
3. SonarCloudのプロジェクトが作成されているか

**対処法**:
1. SonarCloudでトークンを再生成
2. Secretsを再設定
3. `sonar-project.properties`の内容を確認

---

## 📚 関連ドキュメント

- [CI-PERMISSIONS-GUIDE.md](./CI-PERMISSIONS-GUIDE.md) - 各ワークフローの権限設定詳細
- [GITHUB-ACTIONS-PERMISSIONS-SETUP.md](./GITHUB-ACTIONS-PERMISSIONS-SETUP.md) - GitHub Actions権限の詳細説明
- [README.md](./README.md) - 各ワークフローの概要

---

## 🎉 完了

このガイドに従って設定を完了すると、organization-standardsのすべてのCIテンプレートが正常に動作します。

設定後は必ずテストPRを作成して動作確認を行ってください。

---

**作成日**: 2025-11-14  
**バージョン**: 1.0  
**ステータス**: ✅ 完成
