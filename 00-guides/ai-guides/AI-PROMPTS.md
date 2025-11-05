# Devin AI プロンプト例集

## 📋 このドキュメントについて

このドキュメントは、Devin AIが本標準リポジトリを活用する際の**20+の具体的なプロンプト例**を提供します。各プロンプトは実際に使用できる形式で記載されており、コピー&カスタマイズして使用できます。

**関連ドキュメント**:
- [AI-USAGE-GUIDE.md](./AI-USAGE-GUIDE.md) - 全体概要・ナビゲーション
- [AI-WORKFLOWS.md](./AI-WORKFLOWS.md) - 詳細ワークフロー
- [AI-CHECKLISTS.md](./AI-CHECKLISTS.md) - 自己検証チェックリスト

---

## 📑 目次

1. [プロジェクト初期化](#プロジェクト初期化)
2. [技術選定](#技術選定)
3. [コード生成](#コード生成)
4. [API設計・実装](#api設計実装)
5. [テスト生成](#テスト生成)
6. [セキュリティ実装](#セキュリティ実装)
7. [CI/CD設定](#cicd設定)
8. [デプロイメント](#デプロイメント)
9. [ドキュメント生成](#ドキュメント生成)
10. [リファクタリング](#リファクタリング)
11. [監査・検証](#監査検証)

---

## 🚀 プロジェクト初期化

### プロンプト #1: マイクロサービスプロジェクト初期化

```
「以下の手順でPaymentServiceマイクロサービスを初期化してください：

1. 08-templates/project-templates/microservice-nodejsテンプレートを使用
2. プロジェクト名: PaymentService
3. 説明: 決済処理マイクロサービス
4. 技術スタック（05-technology-stack/backend-technologies.mdから選定）:
   - Node.js 18.x + TypeScript
   - Express.js
   - PostgreSQL 15
   - Redis（キャッシュ）
5. 全プレースホルダーを置換:
   - [PROJECT_NAME] → PaymentService
   - [AUTHOR_NAME] → DevTeam
   - [DATABASE_NAME] → payment_db
   - [PORT] → 3001
6. 依存関係インストール
7. Git初期化（03-development-process/git-workflow.md準拠）
8. 初期コミット

完了後、プロジェクト構造とREADME.mdを確認してください。」
```

---

### プロンプト #2: Webアプリケーションプロジェクト初期化

```
「以下の要件でReact SPAプロジェクトを初期化してください：

1. 08-templates/project-templates/web-app-reactテンプレート使用
2. プロジェクト名: CustomerPortal
3. 技術スタック（05-technology-stack/frontend-technologies.md準拠）:
   - React 18 + TypeScript
   - Material-UI
   - React Router v6
   - Axios（API通信）
   - Vite（ビルドツール）
4. 02-architecture-standards/system-architecture-patterns.mdの
   レイヤードアーキテクチャパターンに基づいてディレクトリ構成
5. 01-coding-standards/typescript-javascript-standards.mdに準拠した
   ESLint・Prettier設定
6. README.md作成（08-templates/documentation-templates/使用）

完了後、npm run devで起動確認してください。」
```

---

## 🔍 技術選定

### プロンプト #3: データベース選定

```
「以下のプロジェクト要件に最適なデータベースを選定してください：

要件:
- ユーザー数: 100万人規模
- トランザクション: 1000 TPS
- データモデル: リレーショナル（ユーザー、注文、商品）
- 一貫性要件: ACID準拠必須
- レプリケーション: 読み取りスケーリング必要
- 予算: クラウドマネージドサービス利用可能

手順:
1. 05-technology-stack/database-technologies.mdから候補を抽出
2. 09-reference/decision-matrix.mdのデータベース選定マトリックスで評価
3. 評価軸: パフォーマンス、スケーラビリティ、運用コスト、チームスキル
4. 最終推奨とその理由を文書化

出力: 技術選定書（Markdown形式）」
```

---

### プロンプト #4: CI/CDプラットフォーム選定

```
「GitHubリポジトリで管理するプロジェクトに最適なCI/CDプラットフォームを選定：

プロジェクト情報:
- リポジトリ: GitHub
- 言語: Python（FastAPI）
- デプロイ先: AWS ECS
- チーム規模: 5人

手順:
1. 06-tools-and-environment/ci-cd-pipeline.mdから候補確認
   （GitHub Actions、GitLab CI、Jenkins）
2. 評価基準:
   - GitHub統合の容易さ
   - コスト
   - 学習曲線
   - AWS ECS デプロイサポート
3. 推奨プラットフォームと設定例を提示

出力: CI/CD選定書 + 基本パイプライン設定例」
```

---

## 💻 コード生成

### プロンプト #5: REST APIエンドポイント実装（Python）

```
「01-coding-standards/python-standards.mdに準拠して、
ユーザー管理REST APIを実装してください：

要件:
- FastAPIフレームワーク使用
- エンドポイント:
  - POST /users - ユーザー作成
  - GET /users/{user_id} - ユーザー取得
  - PUT /users/{user_id} - ユーザー更新
  - DELETE /users/{user_id} - ユーザー削除
- Pydanticモデルでバリデーション
- SQLAlchemy ORM使用
- 適切なHTTPステータスコード
- エラーハンドリング
- 型ヒント完備
- docstring記述

02-architecture-standards/api-architecture.mdの設計原則に従い、
- レイヤー分離（ルーター、サービス、リポジトリ）
- 依存性注入
- バリデーション

完了後、コードレビューチェックリストで自己検証してください。」
```

---

### プロンプト #6: React コンポーネント実装

```
「01-coding-standards/typescript-javascript-standards.mdに準拠して、
ユーザープロフィール表示コンポーネントを実装：

要件:
- TypeScript使用
- 関数コンポーネント + Hooks
- Material-UI使用
- Props型定義（interface）
- エラーハンドリング
- ローディング状態管理
- アクセシビリティ考慮（04-quality-standards/accessibility-standards.md）

コンポーネント仕様:
- ユーザー情報表示（名前、メール、アバター）
- 編集ボタン（モーダル表示）
- 保存・キャンセル機能
- バリデーション（フロントエンド）

完了後、ESLint実行で規約準拠確認してください。」
```

---

### プロンプト #7: データベースマイグレーション作成

```
「01-coding-standards/sql-standards.mdに準拠して、
以下のマイグレーションを作成してください：

目的: ユーザーテーブル作成

テーブル: users
カラム:
- id (UUID, PRIMARY KEY)
- email (VARCHAR(255), UNIQUE, NOT NULL)
- password_hash (VARCHAR(255), NOT NULL)
- full_name (VARCHAR(100))
- created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- updated_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

要件:
- 命名規約準拠（snake_case）
- 適切なインデックス（email）
- 外部キー制約（該当する場合）
- ロールバックスクリプト含む
- コメント記述

02-architecture-standards/database-architecture.mdの正規化原則に従ってください。」
```

---

## 🌐 API設計・実装

### プロンプト #8: OpenAPI 3.0仕様書生成

```
「02-architecture-standards/api-architecture.mdに基づいて、
PaymentServiceのOpenAPI 3.0仕様書を生成してください：

エンドポイント:
1. POST /payments - 決済作成
2. GET /payments/{payment_id} - 決済取得
3. POST /payments/{payment_id}/refund - 返金
4. GET /payments - 決済一覧（ページネーション）

各エンドポイントに含める:
- リクエスト/レスポンススキーマ（JSON）
- HTTPステータスコード
- エラーレスポンス
- 認証方式（Bearer Token）
- サンプルリクエスト/レスポンス

08-templates/documentation-templates/api-specテンプレート使用
出力: openapi.yaml」
```

---

### プロンプト #9: GraphQL スキーマ設計

```
「02-architecture-standards/api-architecture.mdのGraphQL設計原則に基づいて、
Eコマースシステムのスキーマを設計：

エンティティ:
- User（ユーザー）
- Product（商品）
- Order（注文）
- OrderItem（注文明細）

クエリ:
- user(id: ID!): User
- products(filter: ProductFilter, limit: Int, offset: Int): [Product]
- order(id: ID!): Order

ミューテーション:
- createOrder(input: CreateOrderInput!): Order
- updateOrderStatus(orderId: ID!, status: OrderStatus!): Order

要件:
- リレーションシップ定義
- ページネーション（Relay Cursor Connections）
- エラーハンドリング
- 型定義完備

出力: schema.graphql + リゾルバー実装ガイド」
```

---

## ✅ テスト生成

### プロンプト #10: ユニットテスト生成（Python）

```
「04-quality-standards/testing-standards.mdに基づいて、
UserServiceクラスのユニットテストを作成：

テスト対象メソッド:
- create_user(email, password, full_name) -> User
- get_user(user_id) -> User | None
- update_user(user_id, **kwargs) -> User
- delete_user(user_id) -> bool

要件:
- pytest使用
- カバレッジ: 80%以上
- モック: データベースアクセス（pytest-mock）
- テストケース:
  - 正常系
  - 異常系（バリデーションエラー、NotFound等）
  - エッジケース（空文字列、None、重複email等）
- フィクスチャ使用（conftest.py）
- Arrange-Act-Assert パターン

完了後、pytest --cov実行でカバレッジ確認してください。」
```

---

### プロンプト #11: E2Eテスト生成（Playwright）

```
「04-quality-standards/testing-standards.mdのE2E標準に基づいて、
ユーザー登録フローのテストを作成：

テストシナリオ:
1. トップページにアクセス
2. 「新規登録」ボタンクリック
3. メールアドレス、パスワード、名前を入力
4. 利用規約チェックボックスにチェック
5. 「登録」ボタンクリック
6. 確認メール送信メッセージ表示確認
7. （メールリンククリックは省略）

要件:
- Playwright（TypeScript）使用
- Page Object Modelパターン
- 環境変数で接続先設定
- スクリーンショット保存（エラー時）
- タイムアウト設定
- データクリーンアップ（afterEach）

出力: tests/e2e/user-registration.spec.ts」
```

---

## 🔒 セキュリティ実装

### プロンプト #12: JWT認証実装

```
「07-security-compliance/authentication-authorization.mdに基づいて、
JWT認証を実装してください：

要件:
- アクセストークン: 15分有効
- リフレッシュトークン: 7日有効
- トークンペイロード: user_id, email, roles
- 署名アルゴリズム: RS256（非対称鍵）
- トークンストレージ: HTTPOnly Cookie（XSS対策）
- CSRF対策: ダブルサブミットクッキー

エンドポイント:
- POST /auth/login - ログイン
- POST /auth/refresh - トークンリフレッシュ
- POST /auth/logout - ログアウト

実装言語: Python（FastAPI）

07-security-compliance/data-protection.mdの暗号化標準に従って、
シークレット管理（環境変数、AWS Secrets Manager等）を実装してください。」
```

---

### プロンプト #13: 入力バリデーション・サニタイゼーション

```
「07-security-compliance/security-standards.mdに基づいて、
ユーザー入力のバリデーション・サニタイゼーションを実装：

対象フィールド:
- email: メールアドレス形式、最大255文字
- password: 8文字以上、大小英数字+記号含む
- full_name: 最大100文字、XSS対策サニタイゼーション
- bio: 最大500文字、HTMLタグエスケープ

脆弱性対策:
- SQLインジェクション: パラメータ化クエリ
- XSS: 出力エスケープ
- CSRF: トークン検証
- コマンドインジェクション: 入力検証

実装:
- バックエンド（FastAPI + Pydantic）
- フロントエンド（React + Yup）

完了後、OWASP Top 10チェックリストで検証してください。」
```

---

## ⚙️ CI/CD設定

### プロンプト #14: GitHub Actionsワークフロー作成

```
「06-tools-and-environment/ci-cd-pipeline.mdに基づいて、
Node.js + TypeScriptプロジェクトのGitHub Actionsワークフローを作成：

トリガー:
- push: mainブランチ、developブランチ
- pull_request: 全ブランチ

ジョブ:
1. build-and-test:
   - Node.js 18セットアップ
   - 依存関係インストール（npm ci）
   - リンター実行（npm run lint）
   - テスト実行（npm test）
   - カバレッジレポート生成
   
2. security-scan:
   - npm audit
   - Snyk脆弱性スキャン
   
3. build-docker:
   - Dockerイメージビルド
   - ECRにプッシュ（mainブランチのみ）
   
4. deploy-staging:
   - ECSにデプロイ（developブランチ）
   
5. deploy-production:
   - ECSにデプロイ（mainブランチ、手動承認）

秘密情報: GitHub Secrets使用
出力: .github/workflows/ci-cd.yml」
```

---

### プロンプト #15: Docker マルチステージビルド

```
「06-tools-and-environment/containerization.mdに基づいて、
Python FastAPIアプリケーションのDockerfileを作成：

要件:
- マルチステージビルド（builder、production）
- ベースイメージ: python:3.11-slim
- 依存関係: requirements.txt
- 非rootユーザーで実行
- ヘルスチェック: /health エンドポイント
- イメージサイズ最適化:
  - .dockerignore使用
  - 不要なファイル除外
  - レイヤーキャッシング活用
- セキュリティ:
  - 脆弱性スキャン対応
  - 最小権限

出力: Dockerfile + .dockerignore」
```

---

## 🚀 デプロイメント

### プロンプト #16: Kubernetes本番環境マニフェスト

```
「08-templates/deployment-templates/kubernetesを使用して、
PaymentServiceの本番環境マニフェストを作成：

要件:
- Deployment:
  - レプリカ数: 3
  - イメージ: ECRリポジトリ
  - リソース:
    - requests: CPU 500m、メモリ512Mi
    - limits: CPU 1、メモリ1Gi
  - ヘルスチェック:
    - livenessProbe: /health（10秒間隔）
    - readinessProbe: /ready（5秒間隔）
  - 環境変数: ConfigMapとSecretから注入

- Service:
  - タイプ: ClusterIP
  - ポート: 80 → 3001

- Ingress:
  - ホスト: payment.example.com
  - TLS有効（cert-manager）
  - パス: /

- HorizontalPodAutoscaler:
  - 最小: 3、最大: 10
  - CPU使用率: 70%

- ConfigMap: アプリケーション設定
- Secret: データベース認証情報

06-tools-and-environment/containerization.mdのベストプラクティスに従ってください。
出力: k8s/production/ ディレクトリ配下に各マニフェスト」
```

---

### プロンプト #17: Terraform インフラ構築

```
「06-tools-and-environment/infrastructure-technologies.mdに基づいて、
AWSインフラをTerraformで構築：

構成:
- VPC（パブリック/プライベートサブネット）
- ECS Cluster
- RDS PostgreSQL（Multi-AZ）
- ElastiCache Redis
- Application Load Balancer
- CloudWatch ログ・メトリクス
- IAMロール・ポリシー

要件:
- 環境別設定（dev、staging、prod）
- tfvarsファイルで変数管理
- バックエンド: S3（ステート管理）
- モジュール化
- セキュリティグループ適切に設定
- タグ付け標準

02-architecture-standards/security-architecture.mdのネットワーク設計に従ってください。
出力: terraform/ ディレクトリ構造」
```

---

## 📄 ドキュメント生成

### プロンプト #18: 包括的README生成

```
「08-templates/documentation-templates/project-documentation-README.mdを使用して、
PaymentServiceの包括的なREADMEを生成：

含めるセクション:
1. プロジェクト概要
   - 目的、機能概要
   
2. 技術スタック
   - 使用技術一覧（05-technology-stack/参照）
   
3. アーキテクチャ
   - システム構成図（Mermaid図）
   - コンポーネント説明
   
4. セットアップ手順
   - 前提条件
   - インストール手順
   - 環境変数設定
   
5. API仕様
   - エンドポイント一覧
   - OpenAPI仕様へのリンク
   
6. 開発ガイド
   - ローカル開発環境
   - テスト実行方法
   - デバッグ方法
   
7. デプロイメント
   - デプロイ手順
   - 環境別設定
   
8. コントリビューション
   - ブランチ戦略（03-development-process/git-workflow.md）
   - コードレビュープロセス
   
9. ライセンス

コードベースを分析して自動的に情報を抽出し、プレースホルダーを埋めてください。」
```

---

### プロンプト #19: アーキテクチャドキュメント生成

```
「02-architecture-standards/に基づいて、
Eコマースシステムのアーキテクチャドキュメントを作成：

含める内容:
1. システム概要
   - ビジネスコンテキスト
   - 主要機能
   
2. アーキテクチャパターン
   - 採用パターン（マイクロサービス）
   - 選定理由
   
3. コンポーネント図
   - マイクロサービス一覧
   - 通信パターン（Mermaid図）
   
4. データアーキテクチャ
   - データベース設計
   - データフロー図
   
5. セキュリティアーキテクチャ
   - 認証・認可フロー
   - ネットワークセグメンテーション
   
6. デプロイメントアーキテクチャ
   - インフラ構成図
   - CI/CDパイプライン
   
7. 技術的決定記録（ADR）
   - 主要な技術選定とその理由
   
8. 非機能要件
   - パフォーマンス目標
   - スケーラビリティ戦略
   - 可用性設計

図はMermaidまたはPlantUML形式で作成してください。
出力: docs/architecture.md」
```

---

## 🔄 リファクタリング

### プロンプト #20: コード品質改善リファクタリング

```
「以下のコードを01-coding-standards/python-standards.mdに準拠するよう
リファクタリングしてください：

[既存コードを貼り付け]

改善項目:
1. 命名規約: PEP 8準拠の変数名・関数名
2. 型ヒント: 全関数・メソッドに追加
3. docstring: Google スタイルで追加
4. エラーハンドリング: 適切な例外処理
5. 関数分割: 単一責任原則に基づいて分割
6. DRY原則: 重複コード削除
7. SOLID原則: 依存性注入、インターフェース分離
8. コメント: 複雑なロジックに説明追加

完了後:
- pylint実行でスコア8.0以上確認
- 04-quality-standards/code-quality-metrics.mdの基準達成確認

出力: リファクタリング後のコード + 変更説明」
```

---

### プロンプト #21: パフォーマンス最適化

```
「04-quality-standards/performance-standards.mdに基づいて、
以下のAPIエンドポイントをパフォーマンス最適化：

現在のパフォーマンス:
- レスポンス時間: 2.5秒
- スループット: 50 req/sec

目標:
- レスポンス時間: 200ms以下
- スループット: 500 req/sec以上

最適化手法:
1. データベースクエリ最適化
   - N+1問題解決
   - 適切なインデックス追加
   - クエリプラン分析

2. キャッシュ実装
   - Redis使用
   - キャッシュキー戦略
   - TTL設定

3. 非同期処理
   - 重い処理をバックグラウンドジョブ化
   - Celery使用

4. コード最適化
   - アルゴリズム改善
   - 不要な処理削除

完了後、負荷テスト（Locust/k6）で検証してください。
出力: 最適化後のコード + パフォーマンステストレポート」
```

---

## 🔍 監査・検証

### プロンプト #22: 標準準拠セルフ監査

```
「10-governance/compliance-audit.mdに基づいて、
PaymentServiceプロジェクトのセルフ監査を実施：

監査項目:
1. コーディング規約（01-coding-standards/）
   - Python標準準拠確認
   - リンター実行結果
   
2. アーキテクチャ標準（02-architecture-standards/）
   - パターン適用確認
   - レイヤー分離確認
   
3. 開発プロセス（03-development-process/）
   - Git規約準拠
   - コードレビュー実施確認
   
4. 品質標準（04-quality-standards/）
   - テストカバレッジ確認（目標80%）
   - 品質メトリクス確認
   
5. セキュリティ（07-security-compliance/）
   - 脆弱性スキャン結果
   - OWASP Top 10対策確認
   
6. ツール・環境（06-tools-and-environment/）
   - CI/CD設定確認
   - コンテナ化標準準拠

出力:
- 監査レポート（Markdown）
- 発見事項リスト（重要度別）
- 是正計画
- 完了予定日

AI-CHECKLISTS.mdの各チェックリストを活用してください。」
```

---

### プロンプト #23: セキュリティ脆弱性スキャン

```
「07-security-compliance/vulnerability-management.mdに基づいて、
包括的なセキュリティスキャンを実施：

スキャン項目:
1. 依存関係スキャン
   - npm audit / pip-audit実行
   - Snyk / Dependabot確認
   
2. 静的解析（SAST）
   - Bandit（Python）/ ESLint security plugin
   - SonarQube実行
   
3. コンテナイメージスキャン
   - Trivy実行
   - Docker Bench for Security
   
4. 動的解析（DAST）
   - OWASP ZAP実行
   - 認証・認可テスト
   
5. シークレットスキャン
   - git-secrets
   - TruffleHog

出力:
- 脆弱性レポート（CVE ID、CVSS スコア）
- 重要度分類（Critical/High/Medium/Low）
- 修正手順
- 完了期限（重要度別）

発見された脆弱性の修正後、再スキャンで検証してください。」
```

---

## 🎯 使い方のヒント

### プロンプトのカスタマイズ方法

1. **プロジェクト固有情報に置換**:
   - `PaymentService` → あなたのプロジェクト名
   - `Python/FastAPI` → あなたの技術スタック
   - `AWS` → あなたのクラウドプロバイダー

2. **要件の追加・削除**:
   - 不要な要件を削除
   - プロジェクト固有の要件を追加

3. **標準参照の調整**:
   - 該当する標準セクションを正確に指定
   - 複数標準の統合が必要な場合は明示

### 効果的なプロンプトの書き方

✅ **良い例**:
```
「01-coding-standards/python-standards.mdに準拠して、
ユーザー認証機能を実装してください。
- FastAPI使用
- 型ヒント完備
- docstring記述
- 適切なエラーハンドリング」
```

❌ **悪い例**:
```
「ユーザー認証を作って」
（標準参照なし、要件不明確）
```

---

## 📞 サポート

プロンプト使用で不明な点がある場合:
- **[AI-USAGE-GUIDE.md](./AI-USAGE-GUIDE.md)** - セクション別ガイドを確認
- **[AI-WORKFLOWS.md](./AI-WORKFLOWS.md)** - ワークフロー全体を理解
- **[AI-CHECKLISTS.md](./AI-CHECKLISTS.md)** - 検証チェックリストを使用

---

**最終更新**: 2025-10-17  
**バージョン**: 1.0.0

**注**: これらのプロンプトは出発点です。プロジェクト固有の要件に応じてカスタマイズしてください。
