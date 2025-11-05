# ツール利用ガイド / Tool Guide

## メタデータ

- **文書タイプ**: ツール利用ガイド
- **バージョン**: 1.0.0
- **最終更新日**: 2025-10-30
- **ステータス**: Living Document（継続的に更新）
- **管理者**: Engineering Team
- **対象者**: 全エンジニア

---

## 📋 目次 / Table of Contents

1. [バージョン管理（Git/GitHub）](#バージョン管理gitgithub)
2. [プロジェクト管理（Jira）](#プロジェクト管理jira)
3. [コミュニケーション（Slack）](#コミュニケーションslack)
4. [CI/CD（GitHub Actions）](#cicdgithub-actions)
5. [監視・ログ（Datadog）](#監視ログdatadog)
6. [コンテナ（Docker/Kubernetes）](#コンテナdockerkubernetes)
7. [クラウド（AWS）](#クラウドaws)
8. [API開発（Postman）](#api開発postman)
9. [データベース管理](#データベース管理)
10. [セキュリティツール](#セキュリティツール)

---

## バージョン管理（Git/GitHub）

### 基本設定

#### 初回セットアップ

```bash
# Gitの基本設定
git config --global user.name "Your Name"
git config --global user.email "your.email@company.com"

# デフォルトブランチ名を設定
git config --global init.defaultBranch main

# エディタの設定
git config --global core.editor "code --wait"

# カラー出力を有効化
git config --global color.ui auto

# SSHキーの生成（まだない場合）
ssh-keygen -t ed25519 -C "your.email@company.com"

# SSHキーをGitHubに追加
cat ~/.ssh/id_ed25519.pub
# 出力された公開鍵をGitHub Settings > SSH Keysに追加
```

### ブランチ命名規約

**フォーマット**: `<type>/<ticket-number>-<short-description>`

**例**:
```
feature/PROJ-123-add-user-authentication
bugfix/PROJ-456-fix-login-error
hotfix/PROJ-789-critical-security-patch
refactor/PROJ-321-improve-database-queries
docs/PROJ-654-update-api-documentation
```

**タイプ一覧**:
- `feature/` - 新機能開発
- `bugfix/` - バグ修正
- `hotfix/` - 緊急修正
- `refactor/` - リファクタリング
- `docs/` - ドキュメント更新
- `test/` - テスト追加・修正
- `chore/` - ビルド・設定変更

### コミットメッセージ規約

**フォーマット**:
```
<type>(<scope>): <subject>

<body>

<footer>
```

**例**:
```
feat(auth): add JWT token refresh mechanism

Implement automatic token refresh when the access token expires.
This improves user experience by avoiding forced logouts.

Closes PROJ-123
```

**タイプ一覧**:
- `feat` - 新機能
- `fix` - バグ修正
- `docs` - ドキュメント変更
- `style` - フォーマット変更（コード動作に影響なし）
- `refactor` - リファクタリング
- `test` - テスト追加・修正
- `chore` - ビルド・設定変更
- `perf` - パフォーマンス改善

### 日常的なワークフロー

#### 1. 新しい作業を開始

```bash
# 最新のmainブランチを取得
git checkout main
git pull origin main

# 新しいブランチを作成
git checkout -b feature/PROJ-123-new-feature

# 作業を進める
# ... コードを編集 ...

# 変更をステージング
git add .

# コミット
git commit -m "feat(module): add new feature"

# リモートにプッシュ
git push origin feature/PROJ-123-new-feature
```

#### 2. プルリクエストの作成

1. GitHub上でプルリクエストを作成
2. PRテンプレートに従って記入
3. レビュアーを指定（最低2名）
4. ラベルを追加
5. Jiraチケットをリンク

**PRタイトル**: `[PROJ-123] Add new feature`

**PRテンプレート**:
```markdown
## 概要 / Summary
この変更の概要を記述

## 変更内容 / Changes
- 変更内容1
- 変更内容2

## テスト / Testing
- [ ] ユニットテストを追加
- [ ] 統合テストを実行
- [ ] 手動テストを実施

## スクリーンショット / Screenshots
（該当する場合）

## 関連チケット / Related Issues
Closes PROJ-123
```

#### 3. コードレビュー対応

```bash
# レビューコメントに対応
# ... コードを修正 ...

# コミット
git add .
git commit -m "fix: address review comments"

# プッシュ
git push origin feature/PROJ-123-new-feature
```

#### 4. マージ後のクリーンアップ

```bash
# mainブランチに切り替え
git checkout main

# 最新を取得
git pull origin main

# 不要なブランチを削除
git branch -d feature/PROJ-123-new-feature
```

### よく使うGitコマンド

```bash
# ステータス確認
git status

# 変更差分を確認
git diff

# コミット履歴を確認
git log --oneline --graph --all

# 特定のファイルの変更を取り消す
git checkout -- <file>

# 直前のコミットを修正
git commit --amend

# インタラクティブなリベース（コミットを整理）
git rebase -i HEAD~3

# スタッシュ（一時的に変更を退避）
git stash
git stash pop

# リモートブランチの一覧
git branch -r

# ローカルのブランチを削除
git branch -d <branch-name>
```

### トラブルシューティング

#### コンフリクトの解決

```bash
# mainブランチの変更を取り込む
git checkout feature/PROJ-123-new-feature
git merge main

# コンフリクトが発生した場合
# 1. コンフリクトファイルを手動で編集
# 2. コンフリクトマーカー（<<<<<<<, =======, >>>>>>>）を削除
# 3. 解決後にコミット

git add .
git commit -m "fix: resolve merge conflicts"
git push origin feature/PROJ-123-new-feature
```

#### 誤ってコミットした場合

```bash
# 直前のコミットを取り消す（変更は残る）
git reset --soft HEAD~1

# 直前のコミットを完全に取り消す（変更も削除）
git reset --hard HEAD~1

# プッシュ済みのコミットを取り消す（新しいコミットで打ち消す）
git revert <commit-hash>
```

---

## プロジェクト管理（Jira）

### アクセス方法

**URL**: `https://jira.company.com`

### 基本概念

#### プロジェクト階層

```
Epic (エピック)
  └── Story (ストーリー)
      ├── Task (タスク)
      ├── Sub-task (サブタスク)
      └── Bug (バグ)
```

### チケットタイプ

| タイプ | 用途 | 例 |
|--------|------|-----|
| **Epic** | 大きな機能や取り組み | ユーザー認証システムの実装 |
| **Story** | ユーザーストーリー | ユーザーとしてログインできる |
| **Task** | 技術的なタスク | データベーススキーマの設計 |
| **Bug** | バグ修正 | ログインボタンが動作しない |
| **Sub-task** | ストーリー/タスクの分割 | APIエンドポイントの実装 |

### チケットのステータス

| ステータス | 説明 | 次のアクション |
|----------|------|--------------|
| **Backlog** | 未着手 | スプリント計画で選択 |
| **To Do** | スプリント内で着手予定 | 作業開始 |
| **In Progress** | 作業中 | 実装・テスト |
| **Code Review** | レビュー待ち | レビュー実施 |
| **Testing** | テスト中 | QAテスト |
| **Done** | 完了 | なし |

### チケットの作成

#### Storyの作成例

**タイトル**: `ユーザーがパスワードをリセットできる`

**説明**:
```markdown
## ユーザーストーリー
As a ユーザー
I want to パスワードをリセットしたい
So that アカウントへのアクセスを回復できる

## 受け入れ基準
- [ ] メールアドレスを入力してリセットリンクを要求できる
- [ ] リセットリンクがメールで送信される
- [ ] リンクの有効期限は24時間
- [ ] 新しいパスワードを設定できる
- [ ] パスワードの強度チェックが機能する

## 技術的要件
- パスワードリセットトークンの生成
- メール送信機能の実装
- 新しいパスワード設定API

## デザイン
[Figmaリンク]
```

**優先度**: High/Medium/Low

**ストーリーポイント**: 1, 2, 3, 5, 8, 13（フィボナッチ数列）

### スプリント管理

#### スプリント計画

1. バックログからチケットを選択
2. ストーリーポイントを見積もる
3. チームのベロシティを考慮
4. スプリントゴールを設定

#### デイリースタンドアップ

**質問**:
1. 昨日何をしたか？
2. 今日何をするか？
3. ブロッカーはあるか？

**Jiraボードで確認**:
- 各自のチケットのステータス
- バーンダウンチャート
- スプリント進捗

#### スプリントレトロスペクティブ

**振り返り項目**:
- うまくいったこと（Keep）
- 改善すべきこと（Problem）
- 試してみたいこと（Try）

### JQLクエリ例

```sql
-- 自分のアサインされたチケット
assignee = currentUser() AND status != Done

-- 今週完了したチケット
project = PROJ AND status = Done AND resolved >= startOfWeek()

-- 優先度が高いバグ
project = PROJ AND type = Bug AND priority = High AND status != Done

-- 現在のスプリントのチケット
sprint in openSprints() AND project = PROJ

-- レビュー待ちのチケット
project = PROJ AND status = "Code Review"
```

### ベストプラクティス

**DO（推奨）**:
- ✅ チケットは具体的で測定可能にする
- ✅ 受け入れ基準を明確に記載する
- ✅ 関連するPR、ドキュメントをリンクする
- ✅ 作業開始時にステータスを更新する
- ✅ ブロッカーがあれば即座に報告する

**DON'T（非推奨）**:
- ❌ 抽象的すぎる説明
- ❌ ステータスの更新を忘れる
- ❌ チケットを分割しない（大きすぎる）
- ❌ コメントなしでステータスを変更
- ❌ 完了条件を曖昧にする

---

## コミュニケーション（Slack）

### チャンネル構成

#### 必須参加チャンネル

| チャンネル | 用途 | 参加対象 |
|-----------|------|---------|
| **#general** | 全社的なお知らせ | 全員 |
| **#engineering** | エンジニアリング全般 | 全エンジニア |
| **#announcements** | 重要なアナウンス | 全員 |
| **#team-[チーム名]** | チーム内コミュニケーション | チームメンバー |

#### プロジェクトチャンネル

| チャンネル | 命名規則 | 例 |
|-----------|---------|-----|
| **プロジェクトチャンネル** | `#proj-[project-name]` | #proj-user-authentication |
| **機能開発チャンネル** | `#feature-[feature-name]` | #feature-payment-integration |

#### 技術チャンネル

| チャンネル | 用途 |
|-----------|------|
| **#frontend** | フロントエンド技術 |
| **#backend** | バックエンド技術 |
| **#devops** | DevOps・インフラ |
| **#data-engineering** | データエンジニアリング |
| **#security** | セキュリティ |
| **#code-review** | コードレビュー依頼 |

#### サポートチャンネル

| チャンネル | 用途 |
|-----------|------|
| **#help-desk** | IT サポート |
| **#incident-response** | インシデント対応 |
| **#deploy-notifications** | デプロイ通知 |

### メッセージングのベストプラクティス

#### スレッドを使う

**❌ 悪い例**:
```
User A: この機能について質問があります
User B: どんな質問ですか？
User A: エラーが出ます
User B: エラーコードは？
（チャンネルが流れる）
```

**✅ 良い例**:
```
User A: この機能について質問があります
  └─ User B: どんな質問ですか？
      └─ User A: エラーが出ます
          └─ User B: エラーコードは？
（スレッド内で完結）
```

#### メンションの使い分け

| メンション | 使用場面 | 影響範囲 |
|-----------|---------|---------|
| `@here` | オンラインの人全員に通知（緊急時のみ） | オンライン中のメンバー |
| `@channel` | チャンネルの全員に通知（重要な場合のみ） | チャンネル全員 |
| `@username` | 特定の人に通知 | 指定したユーザー |
| `@team-name` | チーム全員に通知 | チームメンバー |

#### コードスニペットの共有

**シンタックスハイライト**:
````
```javascript
function greet(name) {
  return `Hello, ${name}!`;
}
```
````

#### リアクションの活用

| リアクション | 意味 |
|-----------|------|
| ✅ | 確認した、了解 |
| 👀 | 見ています |
| 🙏 | ありがとう、お願いします |
| 🎉 | おめでとう、完了 |
| 🚀 | デプロイ成功 |
| 🔥 | 緊急、重要 |

### ステータスの設定

**例**:
- 🏢 `オフィス勤務`
- 🏠 `在宅勤務`
- 🍕 `ランチ中（13:00まで）`
- 🤝 `ミーティング中`
- 🎧 `集中作業中（緊急時のみ連絡可）`
- 🌴 `休暇（MM/DD まで）`

### ワークフローの自動化

#### GitHub統合

```
/github subscribe owner/repo
/github subscribe owner/repo pulls,issues,commits
```

**通知される内容**:
- プルリクエストのオープン・マージ
- Issueの作成・クローズ
- コミットプッシュ

#### Jira統合

```
/jira link
```

**できること**:
- チケット番号の自動展開
- チケット作成
- ステータス更新

---

## CI/CD（GitHub Actions）

### ワークフローの基本構造

**ファイル配置**: `.github/workflows/ci.yml`

```yaml
name: CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Setup Node.js
      uses: actions/setup-node@v3
      with:
        node-version: '18'
        cache: 'npm'
    
    - name: Install dependencies
      run: npm ci
    
    - name: Run linter
      run: npm run lint
    
    - name: Run tests
      run: npm test
    
    - name: Upload coverage
      uses: codecov/codecov-action@v3
```

### よく使うワークフロー

#### 1. テストとビルド

```yaml
name: Test and Build

on:
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    strategy:
      matrix:
        node-version: [16, 18, 20]
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Use Node.js ${{ matrix.node-version }}
      uses: actions/setup-node@v3
      with:
        node-version: ${{ matrix.node-version }}
    
    - run: npm ci
    - run: npm test
    
  build:
    runs-on: ubuntu-latest
    needs: test
    
    steps:
    - uses: actions/checkout@v3
    - run: npm ci
    - run: npm run build
```

#### 2. Docker イメージのビルドとプッシュ

```yaml
name: Docker Build and Push

on:
  push:
    branches: [ main ]

jobs:
  docker:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v2
    
    - name: Login to ECR
      uses: aws-actions/amazon-ecr-login@v1
    
    - name: Build and push
      uses: docker/build-push-action@v4
      with:
        context: .
        push: true
        tags: |
          ${{ secrets.ECR_REGISTRY }}/myapp:latest
          ${{ secrets.ECR_REGISTRY }}/myapp:${{ github.sha }}
```

#### 3. 自動デプロイ

```yaml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Configure AWS credentials
      uses: aws-actions/configure-aws-credentials@v2
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-region: ap-northeast-1
    
    - name: Deploy to ECS
      run: |
        aws ecs update-service \
          --cluster production \
          --service myapp \
          --force-new-deployment
```

### シークレットの管理

**GitHub Settings > Secrets > Actions** で設定

```yaml
steps:
  - name: Use secret
    run: echo ${{ secrets.MY_SECRET }}
    env:
      API_KEY: ${{ secrets.API_KEY }}
```

### ベストプラクティス

**DO（推奨）**:
- ✅ キャッシュを活用する（`cache: 'npm'`）
- ✅ マトリックスビルドで複数バージョンをテスト
- ✅ 失敗時の通知を設定する
- ✅ ワークフローを小さく保つ
- ✅ 再利用可能なワークフローを作成

**DON'T（非推奨）**:
- ❌ シークレットをハードコードしない
- ❌ 不必要に頻繁に実行しない
- ❌ すべてのステップを直列に実行しない
- ❌ エラーハンドリングを忘れない

---

## 監視・ログ（Datadog）

### アクセス方法

**URL**: `https://app.datadoghq.com`

### 主要機能

#### 1. ダッシュボード

**定義済みダッシュボード**:
- システム概要ダッシュボード
- アプリケーションパフォーマンス
- インフラストラクチャメトリクス
- ビジネスメトリクス

**カスタムダッシュボードの作成**:
1. Dashboards > New Dashboard
2. ウィジェットを追加（グラフ、数値、ログ等）
3. メトリクスを選択
4. 保存して共有

#### 2. APM（アプリケーションパフォーマンス監視）

**トレースの確認**:
```
APM > Traces
```

**確認項目**:
- レスポンスタイム
- エラー率
- スループット
- 依存関係マップ

#### 3. ログ管理

**ログの検索**:
```
service:myapp status:error
```

**クエリ例**:
```
# エラーログのみ
status:error

# 特定のサービス
service:api AND env:production

# 時間範囲指定
@timestamp:[now-1h TO now]

# HTTPステータスコード
@http.status_code:>=500

# ユーザーIDで検索
@user_id:12345
```

#### 4. アラート設定

**メトリクスアラートの作成**:
1. Monitors > New Monitor
2. Metric を選択
3. メトリクスを定義（例: `avg:system.cpu.user{*}`）
4. アラート条件を設定（例: `> 80%`）
5. 通知先を設定（Slack、メール、PagerDuty）

**アラートの種類**:
- **Metric Alert**: メトリクスのしきい値
- **Anomaly Detection**: 異常検知
- **Outlier Detection**: 外れ値検知
- **Forecast Alert**: 予測ベース

### よく使うメトリクス

```
# CPU使用率
system.cpu.user

# メモリ使用率
system.mem.used / system.mem.total * 100

# ディスクI/O
system.io.r_s, system.io.w_s

# ネットワーク
system.net.bytes_rcvd, system.net.bytes_sent

# アプリケーション
trace.servlet.request.duration
trace.servlet.request.hits
```

---

## コンテナ（Docker/Kubernetes）

### Docker

#### Dockerfile のベストプラクティス

```dockerfile
# マルチステージビルド
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

FROM node:18-alpine
WORKDIR /app

# セキュリティ: 非rootユーザーで実行
RUN addgroup -g 1001 -S nodejs
RUN adduser -S nodejs -u 1001

COPY --from=builder --chown=nodejs:nodejs /app/node_modules ./node_modules
COPY --chown=nodejs:nodejs . .

USER nodejs

EXPOSE 3000

CMD ["node", "server.js"]
```

#### よく使うDockerコマンド

```bash
# イメージのビルド
docker build -t myapp:latest .

# コンテナの実行
docker run -d -p 3000:3000 --name myapp myapp:latest

# コンテナの一覧
docker ps

# ログの確認
docker logs -f myapp

# コンテナに入る
docker exec -it myapp sh

# コンテナの停止・削除
docker stop myapp
docker rm myapp

# イメージの削除
docker rmi myapp:latest

# 不要なリソースのクリーンアップ
docker system prune -a
```

### Kubernetes

#### kubectl 基本コマンド

```bash
# コンテキストの確認
kubectl config current-context

# コンテキストの切り替え
kubectl config use-context production

# Pod の一覧
kubectl get pods -n myapp

# Pod の詳細
kubectl describe pod myapp-7d8f9c5b6-abcde -n myapp

# Pod のログ
kubectl logs -f myapp-7d8f9c5b6-abcde -n myapp

# Pod に入る
kubectl exec -it myapp-7d8f9c5b6-abcde -n myapp -- /bin/sh

# デプロイメントの確認
kubectl get deployments -n myapp

# サービスの確認
kubectl get services -n myapp

# マニフェストの適用
kubectl apply -f deployment.yaml

# ロールアウト状態の確認
kubectl rollout status deployment/myapp -n myapp

# ロールバック
kubectl rollout undo deployment/myapp -n myapp
```

#### マニフェストの例

**deployment.yaml**:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
  namespace: production
spec:
  replicas: 3
  selector:
    matchLabels:
      app: myapp
  template:
    metadata:
      labels:
        app: myapp
    spec:
      containers:
      - name: myapp
        image: myapp:latest
        ports:
        - containerPort: 3000
        resources:
          requests:
            memory: "128Mi"
            cpu: "100m"
          limits:
            memory: "256Mi"
            cpu: "200m"
        livenessProbe:
          httpGet:
            path: /health
            port: 3000
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /ready
            port: 3000
          initialDelaySeconds: 5
          periodSeconds: 5
```

---

## クラウド（AWS）

### IAM ベストプラクティス

- ✅ 最小権限の原則を適用
- ✅ MFA を有効化
- ✅ IAM ロールを使用（アクセスキーを避ける）
- ✅ 定期的に権限をレビュー

### よく使うAWS CLIコマンド

```bash
# S3
aws s3 ls s3://my-bucket/
aws s3 cp local-file.txt s3://my-bucket/
aws s3 sync ./local-dir s3://my-bucket/remote-dir/

# EC2
aws ec2 describe-instances
aws ec2 start-instances --instance-ids i-1234567890abcdef0
aws ec2 stop-instances --instance-ids i-1234567890abcdef0

# ECS
aws ecs list-clusters
aws ecs list-services --cluster production
aws ecs update-service --cluster production --service myapp --force-new-deployment

# CloudWatch Logs
aws logs tail /aws/ecs/myapp --follow

# Secrets Manager
aws secretsmanager get-secret-value --secret-id myapp/db-password
```

---

## API開発（Postman）

### コレクションの構成

```
MyApp API
├── Authentication
│   ├── Login
│   ├── Logout
│   └── Refresh Token
├── Users
│   ├── Get Users
│   ├── Create User
│   ├── Update User
│   └── Delete User
└── Products
    ├── Get Products
    ├── Create Product
    └── Update Product
```

### 環境変数の設定

**環境**: `Development`, `Staging`, `Production`

**変数例**:
```json
{
  "base_url": "https://api-dev.company.com",
  "api_key": "{{API_KEY}}",
  "auth_token": ""
}
```

### プリリクエストスクリプト

```javascript
// JWTトークンの自動更新
if (!pm.environment.get("auth_token") || isTokenExpired()) {
    pm.sendRequest({
        url: pm.environment.get("base_url") + "/auth/refresh",
        method: "POST",
        header: {
            "Content-Type": "application/json"
        }
    }, function (err, res) {
        pm.environment.set("auth_token", res.json().token);
    });
}
```

### テストスクリプト

```javascript
// ステータスコードの確認
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// レスポンスタイムの確認
pm.test("Response time is less than 500ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(500);
});

// レスポンスボディの確認
pm.test("Response has user data", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property("id");
    pm.expect(jsonData).to.have.property("email");
});

// 環境変数に保存
var jsonData = pm.response.json();
pm.environment.set("user_id", jsonData.id);
```

---

## データベース管理

### PostgreSQL

```sql
-- 接続
psql -h localhost -U username -d database_name

-- データベース一覧
\l

-- テーブル一覧
\dt

-- テーブル構造
\d table_name

-- クエリ実行計画
EXPLAIN ANALYZE SELECT * FROM users WHERE email = 'user@example.com';

-- インデックスの作成
CREATE INDEX idx_users_email ON users(email);

-- パフォーマンス統計
SELECT * FROM pg_stat_user_tables;
```

### MongoDB

```javascript
// データベース接続
use myapp

// コレクション一覧
show collections

// ドキュメントの検索
db.users.find({ email: "user@example.com" })

// インデックスの作成
db.users.createIndex({ email: 1 })

// 集計
db.orders.aggregate([
  { $match: { status: "completed" } },
  { $group: { _id: "$user_id", total: { $sum: "$amount" } } }
])
```

---

## セキュリティツール

### Snyk（脆弱性スキャン）

```bash
# インストール
npm install -g snyk

# 認証
snyk auth

# プロジェクトのテスト
snyk test

# 修正可能な脆弱性の自動修正
snyk fix

# 監視の開始
snyk monitor
```

### SonarQube（コード品質）

**URL**: `https://sonar.company.com`

**ローカルスキャン**:
```bash
sonar-scanner \
  -Dsonar.projectKey=myapp \
  -Dsonar.sources=. \
  -Dsonar.host.url=https://sonar.company.com \
  -Dsonar.login=$SONAR_TOKEN
```

---

## 改訂履歴 / Revision History

| バージョン | 日付 | 変更内容 | 変更者 |
|-----------|------|---------|--------|
| 1.0 | 2025-10-30 | 初版作成 | Engineering Team |

---

**保存先パス**: `/devin-organization-standards/09-reference/tool-guide.md`
