---
title: "Devin初期設定ガイド - 組織標準セットアップ手順書"
version: "1.0.0"
created_date: "2025-11-07"
last_updated: "2025-11-07"
status: "Active"
owner: "Engineering Leadership Team"
category: "Setup & Configuration"
---

# Devin初期設定ガイド
## 組織標準セットアップ手順書

> Devinを組織で効果的に活用するための包括的な初期設定ガイド

**バージョン**: 1.0.0  
**最終更新**: 2025-11-07  
**対象者**: DevOpsエンジニア、プロジェクトマネージャー、システム管理者、Devin導入担当者

---

## 📋 目次

1. [概要](#概要)
2. [前提条件](#前提条件)
3. [アカウント設定](#アカウント設定)
4. [GitHub統合設定](#github統合設定)
5. [リポジトリセットアップ](#リポジトリセットアップ)
6. [Secrets管理設定](#secrets管理設定)
7. [統合サービス設定](#統合サービス設定)
8. [権限・ロール設定](#権限ロール設定)
9. [ベストプラクティス](#ベストプラクティス)
10. [トラブルシューティング](#トラブルシューティング)
11. [チェックリスト](#チェックリスト)

---

## 🎯 概要

### このガイドの目的

Devinを組織で導入する際の初期設定を体系的に実施するための手順書です。以下の課題を解決します:

- ✅ GitHub PRマージの一貫性確保
- ✅ Devin Space編集権限の明確化
- ✅ 統合サービスの適切な設定
- ✅ セキュリティとコンプライアンスの担保
- ✅ チーム全体での効率的な運用開始

### 推定所要時間

| フェーズ | 所要時間 | 実施者 |
|---------|---------|--------|
| アカウント設定 | 15分 | 管理者 |
| GitHub統合 | 30分 | 管理者 |
| リポジトリセットアップ | 1-2時間/リポジトリ | DevOps/開発者 |
| Secrets設定 | 30分 | 管理者/DevOps |
| 統合サービス | 30分 | 管理者 |
| 権限設定 | 30分 | 管理者 |
| **合計** | **3-4時間** | - |

---

## 📦 前提条件

### 必要なアカウント・権限

#### ✅ Devin側
- [ ] Devinアカウント（Enterprise/Teams/Individual）
- [ ] Enterprise Adminまたは Organization Admin権限

#### ✅ GitHub側
- [ ] GitHub Organization Admin権限
- [ ] リポジトリへのAdmin権限
- [ ] GitHub App設定権限

#### ✅ その他サービス
- [ ] Slack Workspace Admin権限（Slack連携時）
- [ ] Jira/Linear Admin権限（チケット管理統合時）
- [ ] AWS/Azure/GCPアクセス権限（クラウド連携時）

### 推奨環境

- **ブラウザ**: Chrome、Edge、Firefox（最新版）
- **ネットワーク**: 安定したインターネット接続
- **OS**: Windows、macOS、Linux（開発環境構築用）

---

## 🔐 アカウント設定

### ステップ1: Devinアカウント登録

1. **Devin公式サイトにアクセス**
   ```
   https://app.devin.ai/
   ```

2. **プランを選択**
   
   | プラン | 料金 | 推奨用途 |
   |-------|------|---------|
   | **Individual** | $500/月 | 個人開発者、試験導入 |
   | **Teams** | $500/月（無制限シート） | 小〜中規模チーム |
   | **Enterprise** | カスタム見積もり | 大規模組織、高度な管理 |

   **推奨**: チーム導入の場合は**Teams**プラン以上

3. **アカウント作成**
   - メールアドレス登録
   - 組織名設定
   - 支払い情報入力

### ステップ2: 基本設定（Customization）

**設定パス**: `Settings > Customizations`

#### 2.1 タイムアウト設定

```yaml
Session Timeout: 30分（推奨）
Auto-close PR: 7日間（デフォルト）
Idle Timeout: 2時間（推奨）
```

**理由**: 
- セッション管理の効率化
- リソースの適切な解放
- PR管理の自動化

#### 2.2 通知設定

```yaml
Email Notifications: 有効
  - Session completion
  - Error notifications
  - Weekly summary

Slack Notifications: 有効（Slack統合後）
  - Real-time updates
  - Status changes
```

#### 2.3 Data Control（重要）

**設定パス**: `Settings > Data Control`

```yaml
Training Data Usage: 無効（推奨）
  理由: 組織のコードを学習データに使用させない

Session Recording: 監査目的のみ有効
Telemetry: 基本情報のみ
```

**セキュリティ重要度**: ⭐⭐⭐⭐⭐

---

## 🔗 GitHub統合設定

### ステップ3: GitHub App統合

#### 3.1 Devin専用GitHubアカウント作成

**ベストプラクティス**: 人間のアカウントではなく、Devin専用アカウントを作成

```
推奨アカウント名: devin@yourcompany.com
または: github-devin@yourcompany.com
```

**手順**:
1. GitHub新規アカウント作成
2. Organization Memberとして追加
3. 適切なチーム・権限を付与

#### 3.2 GitHub統合の設定

**Devin側の設定**:

1. **統合開始**
   ```
   Settings > Integrations > GitHub > Connect
   ```

2. **GitHub App承認画面**
   - Devin専用アカウントでログイン
   - 組織を選択
   - リポジトリアクセスを設定

**リポジトリアクセス選択**:

```
オプション1: All repositories（すべてのリポジトリ）
  推奨: 小規模組織、統一管理

オプション2: Selected repositories（選択したリポジトリのみ）
  推奨: 大規模組織、段階的導入
```

#### 3.3 必要な権限の確認

**GitHub App統合で付与される権限**:

**Read権限**:
- ✅ `dependabot alerts` - 依存関係アラート
- ✅ `actions` - GitHub Actionsワークフロー
- ✅ `checks` - CI/CDチェック
- ✅ `commit statuses` - コミット状態
- ✅ `metadata` - リポジトリメタデータ
- ✅ `repository advisories` - セキュリティアドバイザリ

**Read & Write権限**:
- ✅ `code` - コード読み書き（必須）
- ✅ `pull requests` - PR作成・マージ（必須）
- ✅ `issues` - Issue作成・管理
- ✅ `discussions` - ディスカッション参加
- ✅ `workflows` - ワークフロー設定

**権限確認方法**:
```
GitHub > Settings > Installations > Devin.ai Integration > Configure
```

#### 3.4 ブランチ保護ルールの設定

**目的**: PRマージの一貫性確保

**設定パス**: `Repository > Settings > Branches > Branch protection rules`

**推奨設定（masterブランチ）**:

```yaml
Branch name pattern: main (または master)

保護ルール:
  ✅ Require pull request reviews before merging
     - Required approving reviews: 1（最低1名）
     - Dismiss stale pull request approvals: 有効
  
  ✅ Require status checks to pass before merging
     - Require branches to be up to date: 有効
     - Status checks: 
         - CI/CD pipeline
         - Code quality checks
         - Security scans
  
  ❌ Require conversation resolution before merging: 無効
     理由: Devinが自動マージする場合に障害となる
  
  ✅ Require signed commits: 有効（推奨）
     理由: コミット署名でDevinの作業を識別
  
  ✅ Include administrators: 有効
     理由: 一貫性確保のため管理者も同じルールに従う
  
  ✅ Allow force pushes: 無効
  ✅ Allow deletions: 無効
```

**Devinがマージできない場合のチェックポイント**:

1. ✅ ブランチ保護ルールが厳しすぎないか
2. ✅ 必須レビュアー設定が適切か
3. ✅ CI/CDステータスチェックが完了しているか
4. ✅ Devinアカウントに`Write`権限があるか

#### 3.5 GPGキー設定（コミット署名）

**目的**: Devinのコミットを識別・検証可能にする

**手順**:

1. **Devinのターミナルで鍵生成**（リポジトリセットアップ時）
   ```bash
   gpg --full-generate-key
   
   # 選択肢:
   # 1) RSA and RSA (default)
   # 4096 bits
   # Key does not expire
   # Real name: Devin AI Bot
   # Email: devin@yourcompany.com
   ```

2. **公開鍵をエクスポート**
   ```bash
   gpg --armor --export devin@yourcompany.com
   ```

3. **GitHub Devinアカウントに登録**
   ```
   GitHub > Settings > SSH and GPG keys > New GPG key
   ```

4. **Git設定**
   ```bash
   git config --global user.name "Devin AI Bot"
   git config --global user.email "devin@yourcompany.com"
   git config --global user.signingkey <KEY_ID>
   git config --global commit.gpgsign true
   ```

**確認方法**:
```bash
git log --show-signature
```

---

## 🗂️ リポジトリセットアップ

### ステップ4: Devinのワークスペース設定

**設定パス**: `Settings > Devin's Workspace`

#### 4.1 リポジトリ追加

1. **リポジトリ選択**
   ```
   Settings > Devin's Workspace > + Repository
   ```

2. **基本情報入力**
   - Repository URL
   - Branch: main (または master)
   - Description: リポジトリの説明

#### 4.2 開発環境セットアップ（重要）

**VS Code統合エディタで実行**

##### 4.2.1 必須コマンド設定

**目的**: Devinが正しくビルド・テスト・リントを実行できるようにする

**設定例（Node.js/TypeScript プロジェクト）**:

```bash
# 1. Build Command
npm run build

# 2. Test Command
npm test

# 3. Lint Command
npm run lint

# 4. Git Pull（常に最新化）
git pull origin main
```

**設定例（Python プロジェクト）**:

```bash
# 1. Install Dependencies
pip install -r requirements.txt

# 2. Test Command
pytest

# 3. Lint Command
flake8 . && black --check .

# 4. Git Pull
git pull origin main
```

**設定例（Java/Spring Boot プロジェクト）**:

```bash
# 1. Build Command
./mvnw clean package

# 2. Test Command
./mvnw test

# 3. Lint Command
./mvnw checkstyle:check

# 4. Git Pull
git pull origin main
```

**コマンド検証**:
- 各コマンドが正常終了することを確認
- タイムアウト: 5分以内（推奨）

##### 4.2.2 環境変数設定（direnv使用）

**direnvインストール**（2025年4月24日以降は自動インストール済み）:

```bash
# Ubuntu/Debian
apt-get install direnv

# macOS
brew install direnv

# ~/.bashrcに追加
echo 'eval "$(direnv hook bash)"' >> ~/.bashrc
source ~/.bashrc
```

**リポジトリルートに`.envrc`作成**:

```bash
# .envrc
export NODE_ENV=development
export DATABASE_URL=$DATABASE_URL_SECRET
export API_KEY=$API_KEY_SECRET
export LOG_LEVEL=debug

# Node.jsバージョン管理（nvmの場合）
nvm use 20

# Pythonバージョン管理（pyenvの場合）
pyenv local 3.11
```

**承認**:
```bash
direnv allow
```

##### 4.2.3 リポジトリ別自動環境設定

**複数リポジトリで異なる環境を使う場合**:

**~/.bashrcに追加**:

```bash
# ~/.bashrc

function custom_cd() {
  builtin cd "$@"
  
  # プロジェクトAの設定
  if [[ "$PWD" == "$HOME/repos/project-a"* ]]; then
    export NODE_ENV=development
    nvm use 18 >/dev/null 2>&1
    echo "🔧 Project A環境を読み込みました (Node 18)"
  
  # プロジェクトBの設定
  elif [[ "$PWD" == "$HOME/repos/project-b"* ]]; then
    export NODE_ENV=production
    nvm use 20 >/dev/null 2>&1
    echo "🔧 Project B環境を読み込みました (Node 20)"
  
  # Pythonプロジェクトの設定
  elif [[ "$PWD" == "$HOME/repos/python-project"* ]]; then
    pyenv local 3.11 >/dev/null 2>&1
    source venv/bin/activate 2>/dev/null
    echo "🐍 Python環境を読み込みました (Python 3.11)"
  fi
}

alias cd='custom_cd'
cd $PWD  # 初回読み込み
```

#### 4.3 ブラウザログイン（Session Cookies）

**目的**: Devinが認証が必要なサービスにアクセスできるようにする

**手順**:

1. **Devinのブラウザタブを開く**
   ```
   Devin Workspace > Browser
   ```

2. **対象サイトにログイン**
   - 社内システム
   - クラウドコンソール（AWS、Azure、GCP）
   - 外部API管理画面

3. **セッション保存**
   - ログイン状態は自動保存される
   - 次回セッションから利用可能

**注意**:
- セッションタイムアウトが頻繁な場合は、Secretsに認証情報を登録

#### 4.4 スナップショット保存

**すべての設定完了後**:

1. **検証実行**
   ```
   各コマンドが正常に実行されることを確認
   ```

2. **スナップショット保存**
   ```
   Settings > Devin's Workspace > Save Snapshot
   ```

3. **確認**
   - 保存完了まで数分待機
   - Version Historyで確認可能

**重要**: 今後のすべてのDevinセッションは、このスナップショットから起動される

---

## 🔑 Secrets管理設定

### ステップ5: Secrets設定

**設定パス**: `Settings > Secrets`

#### 5.1 Secretsの種類と用途

| 種類 | 用途 | 例 |
|------|------|-----|
| **Raw Secret** | API Key、Token、Password | `GITHUB_TOKEN`, `AWS_ACCESS_KEY` |
| **Site Cookies** | ウェブサイト自動ログイン | AWS Console、社内システム |
| **TOTP** | 2FA認証 | Google Authenticator形式 |

#### 5.2 Raw Secret設定

**推奨セットアップ**:

```yaml
Secret Name: GITHUB_TOKEN
Secret Value: ghp_xxxxxxxxxxxxxxxxxxxx
Note: "GitHub API access for CI/CD pipeline. Expires: 2026-01-01. Contact: devops-team@company.com"

Secret Name: AWS_ACCESS_KEY_ID
Secret Value: AKIAIOSFODNN7EXAMPLE
Note: "AWS production account. Region: us-west-2. Contact: cloud-team@company.com"

Secret Name: AWS_SECRET_ACCESS_KEY
Secret Value: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
Note: "AWS production account secret. Rotate every 90 days."

Secret Name: DATABASE_URL
Secret Value: postgresql://user:pass@host:5432/db
Note: "Production database connection. Read-only access."

Secret Name: SLACK_WEBHOOK_URL
Secret Value: https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXX
Note: "Deployment notifications channel"
```

**命名規則**:
- 大文字スネークケース（例: `DATABASE_URL`）
- 説明的な名前（`KEY1`ではなく`STRIPE_API_KEY`）
- プレフィックス使用（`PROD_`, `STAGING_`など）

#### 5.3 Site Cookies設定

**Chrome拡張機能インストール**:

1. **[Share your cookies](https://chromewebstore.google.com/detail/share-your-cookies/poijkganimmndbhghgkmnfgpiejmlpke)** をインストール

2. **対象サイトにログイン**
   - AWS Management Console
   - Azure Portal
   - GCP Console
   - 社内システム

3. **Cookieエクスポート**
   - 拡張機能でCookieをエクスポート
   - Base64エンコードされたJSON形式

4. **Devinに登録**
   ```
   Settings > Secrets > Add Secret
   Type: Site Cookies
   Name: AWS_CONSOLE_COOKIE
   Value: [エクスポートされたCookie]
   ```

#### 5.4 TOTP（2FA）設定

**手順**:

1. **対象サービスで2FA設定画面を開く**
   - GitHub、AWS、Azure等

2. **QRコードを表示**
   - 「Set up authenticator app」等を選択

3. **QRコードをスクリーンショット**

4. **Devinに登録**
   ```
   Settings > Secrets > Add Secret
   Type: One-time Password
   Name: GITHUB_2FA_TOTP
   Value: [QRコードアップロード]
   ```

#### 5.5 リポジトリ固有のSecrets

**direnvで管理**:

```bash
# リポジトリルートの .envrc
export API_KEY=$API_KEY_SECRET
export DATABASE_URL=$DATABASE_URL_SECRET
export STRIPE_KEY=$STRIPE_KEY_SECRET

direnv allow
```

**スコープ**:
- このリポジトリのスナップショットを使うセッションでのみ有効
- 他のリポジトリには影響しない

---

## 🔧 統合サービス設定

### ステップ6: サービス統合

#### 6.1 Slack統合

**目的**: Slackから直接Devinを起動、リアルタイム通知

**設定手順**:

1. **Devin側設定**
   ```
   Settings > Integrations > Slack > Connect
   ```

2. **Slackワークスペース選択**
   - ワークスペースを選択
   - アプリ権限を承認

3. **個人アカウント連携**
   - 各ユーザーが個別に連携必要
   - Slackメールアドレス = Devinメールアドレス（推奨）

**使用方法**:
```
# Slackチャンネルで
@Devin このバグを修正して

# インラインコマンド
!ask データベース設計について教えて
!deep React最新のベストプラクティスを調査
mute  # Devinを一時停止
unmute  # Devinを再開
archive  # セッションを終了してアーカイブ
```

**推奨チャンネル構成**:
```
#devin-runs - すべてのDevin実行ログ
#devin-alerts - エラー・警告通知
#devin-reviews - PRレビュー通知
```

#### 6.2 Jira統合

**目的**: JiraチケットからDevinセッションを自動起動

**設定手順**:

1. **Jira専用アカウント作成**
   ```
   Email: devin@yourcompany.com
   または: jira+devin@yourcompany.com
   ```

2. **Devin側設定**
   ```
   Settings > Integrations > Jira > Connect
   ```

3. **Jiraアカウントでログイン**

**Enterprise向け追加設定**:
```
Enterprise Settings > Connected Accounts > Jira
Organization to Project Mapping:
  Dev Team → PROJECT-DEV
  QA Team → PROJECT-QA
```

**使用方法**:
1. Jiraチケットに`Devin`ラベルを追加
2. Devinが自動分析（1分以内）
3. 実装計画を生成（5分以内）
4. リンククリックでセッション開始

#### 6.3 Linear統合

**目的**: Linearチケット自動分析、実装計画生成

**設定手順**:

1. **Devin側設定**
   ```
   Settings > Integrations > Linear > Connect
   ```

2. **Linear認証**
   - アクセスするTeamを選択
   - 権限を承認

**自動トリガー設定**:
```
Settings > Integrations > Linear > Auto-trigger
  ✅ New issues in "Backlog"
  ✅ Status changed to "Todo"
  ✅ Label "Devin" added
```

#### 6.4 GitLab/Azure DevOps統合

**GitLab**:
```
Settings > Integrations > GitLab > Connect
- GitLab専用アカウントでログイン
- リポジトリアクセス権限付与
```

**Azure DevOps**:
```
Enterprise Settings > Connected Accounts > Azure DevOps
- AAD Global Admin権限が必要
- Organization選択
- Project/Repositoryアクセス設定
```

---

## 👥 権限・ロール設定

### ステップ7: RBAC設定（Enterprise向け）

#### 7.1 役割の理解

**Organization-Level Roles**:

| 役割 | 権限 | 推奨対象 |
|------|------|---------|
| **Admin** | 全権限 | プロジェクトマネージャー、テックリード |
| **Member** | 開発機能のみ | 開発者 |
| **DeepWiki Only** | ドキュメント検索のみ | サポートチーム、ビジネスサイド |

**Enterprise-Level Roles（Account Roles）**:

| 役割 | 権限 | 推奨対象 |
|------|------|---------|
| **Enterprise Admin** | 全組織の管理 | CTO、VP of Engineering |
| **Member** | 全組織へのアクセス | シニアエンジニア |

#### 7.2 カスタムロール作成

**シナリオ**: Devin Spaceを編集できるロールを作成

**手順**:

1. **Enterprise Admin権限でログイン**

2. **カスタムロール作成**
   ```
   Enterprise Settings > Roles > Create custom role
   
   Role Name: Space Editor
   Level: Organization-level
   
   Permissions:
   ✅ Use Devin Sessions
   ✅ Manage Snapshots and Repository Indexing
   ✅ Manage Knowledge
   ❌ Manage Membership（不要）
   ❌ Manage Settings（不要）
   ```

3. **ユーザーに割り当て**
   ```
   Organization Members > [ユーザー選択] > Assign Role > Space Editor
   ```

#### 7.3 IdPグループ連携（SSO使用時）

**Okta/Entra ID使用時**:

1. **グループマッピング設定**
   ```
   Enterprise Settings > Authentication > Groups
   
   IdP Group: engineering-team
   → Devin Role: Member
   
   IdP Group: engineering-leads
   → Devin Role: Admin
   ```

2. **自動プロビジョニング**
   - IdPでグループメンバーシップ変更
   - Devinに自動反映（次回ログイン時）

#### 7.4 Git Permissions（Enterprise）

**目的**: 各組織に特定のリポジトリアクセスを付与

**設定**:

```
Enterprise Settings > Connected Accounts > GitHub > Git Permissions

Organization: Dev Team
  ✅ Group Permissions:
     Group Prefix: company-name/backend
     → backend-*配下の全リポジトリにアクセス可能
  
  ✅ Repository Permissions:
     Repository: company-name/frontend-app
     → 特定リポジトリのみアクセス可能

Organization: QA Team
  ✅ Repository Permissions:
     Repository: company-name/test-automation
```

---

## ✅ ベストプラクティス

### セキュリティ

#### 1. 最小権限の原則

```yaml
推奨:
  ✅ Devinには必要最小限のリポジトリアクセスのみ
  ✅ Secretsは必要なセッションにのみ共有
  ✅ Production環境へのアクセスは慎重に

非推奨:
  ❌ すべてのリポジトリへの無制限アクセス
  ❌ Adminアカウントのトークンを使用
  ❌ Production Secretsを開発環境で使用
```

#### 2. 認証情報のローテーション

```yaml
頻度:
  - API Tokens: 90日ごと
  - Passwords: 90日ごと
  - SSH Keys: 180日ごと
  - GPG Keys: 1年ごと

プロセス:
  1. 新しい認証情報を生成
  2. Devin Secretsを更新
  3. 古い認証情報を無効化
  4. 動作確認
```

#### 3. 監査ログの確認

```yaml
定期確認:
  - 毎週: Devinのアクセスログ確認
  - 毎月: PRマージ履歴の監査
  - 四半期: セキュリティレビュー

確認項目:
  ✅ 不審なアクセスパターン
  ✅ 予期しないPRマージ
  ✅ Secrets使用履歴
  ✅ 失敗したアクセス試行
```

### 運用

#### 4. リポジトリセットアップの標準化

**テンプレート化**:

```bash
# repo-setup-template.sh

#!/bin/bash

# 1. 依存関係インストール
npm install

# 2. ビルドテスト
npm run build

# 3. テスト実行
npm test

# 4. 環境変数設定
cp .env.example .env
direnv allow

# 5. Git設定
git config user.name "Devin AI Bot"
git config user.email "devin@company.com"

echo "✅ セットアップ完了"
```

#### 5. スナップショット管理

```yaml
命名規則:
  - v1.0.0-initial-setup (初期設定)
  - v1.1.0-add-python-support (Python対応追加)
  - v2.0.0-major-refactor (大規模変更)

バックアップ:
  - 重要なスナップショットは説明を詳細に記載
  - 定期的に古いスナップショットをクリーンアップ
  - 破壊的変更前に必ずスナップショット作成

復元:
  Settings > Devin's Workspace > Version History > Restore
```

#### 6. チーム全体での活用

**オンボーディング資料準備**:

```markdown
# Devin使用ガイド（チーム向け）

## 1. 初回セットアップ
- [ ] Devinアカウント作成
- [ ] Slack連携
- [ ] 担当プロジェクトのリポジトリアクセス確認

## 2. 日常的な使い方
- Slackで @Devin [タスク内容]
- Jira/Linearチケットに"Devin"ラベル
- PRレビュー依頼

## 3. 注意事項
- Production環境での作業は要承認
- PRマージ前に必ず内容確認
- 不明点はテックリードに相談
```

### パフォーマンス

#### 7. セッション管理

```yaml
効率化:
  ✅ 複数の小タスクは並列セッション実行
  ✅ 大きなタスクは適切に分割
  ✅ 明確な完了基準を提示

非効率:
  ❌ 1つの巨大タスクをDevinに丸投げ
  ❌ 曖昧な指示
  ❌ コンテキスト不足
```

#### 8. Knowledge・Playbooks活用

**Knowledge**:
```
Settings > Knowledge

追加内容:
  - プロジェクト固有の命名規則
  - アーキテクチャドキュメント
  - APIドキュメント
  - よくあるトラブルシューティング
```

**Playbooks**:
```
Settings > Playbooks

作成例:
  - "バグ修正手順"
  - "新機能実装フロー"
  - "テスト追加プロセス"
  - "デプロイ前チェックリスト"
```

---

## 🚨 トラブルシューティング

### よくある問題と解決策

#### 問題1: PRがマージできない

**症状**:
- Devinがmain/masterブランチにPRをマージできない
- 「Permission denied」エラー

**原因**:
1. ブランチ保護ルールが厳しい
2. 必須レビュアー設定
3. DevinアカウントにWrite権限がない
4. CI/CDチェックが失敗している

**解決策**:

```yaml
1. ブランチ保護ルール確認:
   Repository > Settings > Branches
   
   確認項目:
   - "Require approvals"が1以下か
   - "Require status checks"で必須チェックが通過しているか
   - "Restrict who can push"でDevinが含まれているか

2. Devin権限確認:
   GitHub > Settings > Installations > Devin.ai
   
   確認:
   - Pull requestsに"Read & Write"権限があるか
   - 対象リポジトリがアクセス可能か

3. GitHub App再設定:
   Devin Settings > Integrations > GitHub
   - Reconnect実行
   - 権限を再確認
```

#### 問題2: Devin Spaceが編集できない

**症状**:
- Devin Workspaceでリポジトリ設定を編集できない
- 「Permission denied」表示

**原因**:
- Organization Member権限しかない
- `Manage Snapshots`権限がない

**解決策**:

```yaml
Enterprise環境:
  1. Enterprise Admin に連絡
  2. カスタムロール作成を依頼
     必要権限:
     - Manage Snapshots and Repository Indexing
  3. ロールを自分に割り当ててもらう

Teams環境:
  1. Organization Adminに連絡
  2. Admin権限を付与してもらう
  
代替案:
  - forkしたSpaceを使用
  - 定期的に元のSpaceからpull
```

#### 問題3: Secretsが認識されない

**症状**:
- 環境変数が空
- APIキーが使えない

**原因**:
1. Secretsがセッション開始後に追加された
2. 名前に無効な文字が含まれる
3. direnvが承認されていない

**解決策**:

```yaml
1. セッション再起動:
   - Secretsは新しいセッションでのみ有効
   - 既存セッションは影響を受けない

2. Secret名の修正:
   有効: API_KEY, DATABASE_URL
   無効: api-key, database.url
   
   ルール:
   - 英字で開始
   - 英数字とアンダースコアのみ
   - 大文字推奨

3. direnv確認:
   cd /path/to/repo
   direnv allow
```

#### 問題4: ビルド/テストコマンドが失敗する

**症状**:
- リポジトリセットアップでコマンド検証が失敗
- タイムアウトエラー

**原因**:
1. 依存関係未インストール
2. 環境変数不足
3. パス問題

**解決策**:

```bash
# 1. 依存関係を先にインストール
npm install  # Node.js
pip install -r requirements.txt  # Python
./mvnw install  # Java

# 2. 環境変数設定
export NODE_ENV=test
export DATABASE_URL=sqlite::memory:

# 3. 絶対パス使用
/usr/local/bin/node --version
/usr/bin/python3 --version

# 4. PATHに追加
export PATH="/usr/local/bin:$PATH"

# 5. タイムアウト対策（長時間コマンド）
# キャッシュを事前生成
npm run build
```

#### 問題5: Slack統合が動かない

**症状**:
- @Devinに反応しない
- 通知が来ない

**原因**:
1. 個人アカウント連携していない
2. Slackメールとdevinメールが異なる
3. チャンネルにDevinが追加されていない

**解決策**:

```yaml
1. 個人連携確認:
   Devin Settings > Integrations > Slack
   - Link your accountをクリック
   - Slackで再認証

2. メールアドレス統一:
   Slack Profile > Email: user@company.com
   Devin Settings > Profile > Email: user@company.com
   
   異なる場合:
   - Devin Settingsでメール変更
   - または、Slack連携時に別メール指定

3. チャンネル招待:
   /invite @Devin
```

#### 問題6: CI/CDが実行されない

**症状**:
- PRを作成してもCIが動かない
- ステータスチェックが表示されない

**原因**:
1. ワークフローファイルが不正
2. Devinアカウントのアクションが無効
3. ブランチ名パターンが一致しない

**解決策**:

```yaml
1. ワークフロー確認:
   .github/workflows/ci.yml
   
   on:
     pull_request:
       branches: [ main, master ]  # ブランチ指定
     push:
       branches: [ main, master ]

2. Actions有効化:
   Repository > Settings > Actions > General
   - Allow all actions: 有効

3. Devinアカウント確認:
   GitHub Devinアカウントでログイン
   - Actionsが実行可能か確認
   - Workflow permissionsを確認
```

---

## ✅ チェックリスト

### 初期設定完了チェック

**アカウント・基本設定**:
- [ ] Devinアカウント作成完了
- [ ] プラン選択・支払い設定完了
- [ ] Customization設定完了（タイムアウト、通知等）
- [ ] Data Control設定完了（Training Data: 無効）

**GitHub統合**:
- [ ] Devin専用GitHubアカウント作成
- [ ] GitHub App統合完了
- [ ] リポジトリアクセス権限設定
- [ ] 必要な権限（code, pull requests等）確認
- [ ] ブランチ保護ルール設定
- [ ] GPGキー設定・コミット署名有効化

**リポジトリセットアップ**:
- [ ] 各リポジトリをDevin's Workspaceに追加
- [ ] ビルドコマンド設定・検証
- [ ] テストコマンド設定・検証
- [ ] リントコマンド設定・検証
- [ ] 環境変数設定（direnv）
- [ ] ブラウザログイン（必要な場合）
- [ ] スナップショット保存完了

**Secrets管理**:
- [ ] 必要なRaw Secrets登録（API Key、Token等）
- [ ] Site Cookies設定（必要な場合）
- [ ] TOTP設定（2FA必要な場合）
- [ ] リポジトリ固有Secrets設定
- [ ] Secrets動作確認

**統合サービス**:
- [ ] Slack統合設定（使用する場合）
- [ ] 各ユーザーのSlack個人連携完了
- [ ] Jira統合設定（使用する場合）
- [ ] Linear統合設定（使用する場合）
- [ ] その他サービス統合（GitLab、Azure DevOps等）

**権限・ロール**:
- [ ] Enterprise/Organization役割の理解
- [ ] カスタムロール作成（必要な場合）
- [ ] ユーザーへのロール割り当て
- [ ] Git Permissions設定（Enterprise）
- [ ] IdPグループ連携（SSO使用時）

**ドキュメント・ナレッジ**:
- [ ] チーム向けオンボーディング資料作成
- [ ] Knowledge登録（プロジェクト固有情報）
- [ ] Playbooks作成（標準作業手順）
- [ ] トラブルシューティングガイド共有

**動作確認**:
- [ ] 簡単なタスクでDevin実行テスト
- [ ] PRマージ動作確認
- [ ] Secrets使用確認
- [ ] Slack通知動作確認
- [ ] ビルド・テスト実行確認

**セキュリティ**:
- [ ] 最小権限の原則適用確認
- [ ] Productionアクセス制御確認
- [ ] 認証情報ローテーション計画策定
- [ ] 監査ログ確認プロセス確立

---

## 📚 関連ドキュメント

### 内部リソース

- [AI-MASTER-WORKFLOW-GUIDE.md](./00-guides/AI-MASTER-WORKFLOW-GUIDE.md) - AIワークフロー統合ガイド
- [DOCUMENT-USAGE-MANUAL.md](./00-guides/DOCUMENT-USAGE-MANUAL.md) - ドキュメント利用マニュアル
- [Git Workflow](./03-development-process/git-workflow.md) - Gitフロー標準
- [Security Standards](./07-security-compliance/security-standards.md) - セキュリティ標準

### 外部リソース

- [Devin公式ドキュメント](https://docs.devin.ai/)
- [GitHub Integration Guide](https://docs.devin.ai/integrations/gh)
- [Repository Setup](https://docs.devin.ai/onboard-devin/repo-setup)
- [Secrets Management](https://docs.devin.ai/product-guides/secrets)
- [Enterprise RBAC](https://docs.devin.ai/enterprise/custom-roles)

---

## 📞 サポート・問い合わせ

### 組織内サポート

- **Slackチャンネル**: `#devin-support`
- **担当チーム**: DevOps Team / Platform Engineering
- **緊急連絡先**: devops-team@company.com

### Devin公式サポート

- **メール**: support@cognition.ai
- **ドキュメント**: https://docs.devin.ai/
- **Slack Connect**: Settings > Support > Create Slack Connect Channel

---

## 📝 バージョン履歴

### v1.0.0 (2025-11-07) - 初版リリース

**作成内容**:
- ✨ 初期設定の包括的ガイド作成
- ✨ GitHub統合詳細手順
- ✨ リポジトリセットアップガイド
- ✨ Secrets管理ベストプラクティス
- ✨ 統合サービス設定手順
- ✨ 権限・ロール設定（RBAC）
- ✨ トラブルシューティング集
- ✨ チェックリスト

**対象課題**:
- GitHub PRマージの一貫性問題
- Devin Space編集権限の不明確さ
- 統合サービス設定の複雑さ

---

**最終更新**: 2025-11-07  
**バージョン**: 1.0.0  
**管理者**: Engineering Leadership Team
