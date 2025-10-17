# プロジェクトテンプレート (Templates)

## 📋 概要

このディレクトリには、新規プロジェクトの迅速な立ち上げ、統一されたドキュメント作成、標準化されたデプロイメント設定を実現するためのテンプレート集が含まれています。これらのテンプレートは、Devin AIおよび開発者が効率的にプロジェクトを開始できるよう設計されています。

**重要度**: Tier 4（効率化・運用）  
**対象**: 全開発者、プロジェクトマネージャー、Devin AI  
**更新頻度**: 四半期ごとのレビュー推奨

**実装アプローチ**: 優先度版（3/5サブディレクトリ完成）

---

## 📁 ディレクトリ構造

```
08-templates/
├── README.md (このファイル)
├── project-templates/ ............... ✅ 優先度版完成
│   └── [外部文書] プロジェクト初期化テンプレート集
├── documentation-templates/ ......... ✅ 優先度版完成
│   ├── project-documentation-README.md (10.3 KB)
│   └── [外部文書] ドキュメントテンプレート集
├── deployment-templates/ ............ ✅ 優先度版完成
│   └── [外部文書] デプロイメント設定テンプレート集
├── code-templates/ .................. ⏸️ オプション（未作成）
│   └── コードボイラープレート
└── testing-templates/ ............... ⏸️ オプション（未作成）
    └── テストテンプレート
```

---

## 🎯 優先度版について

**完成状況: 3/5サブディレクトリ（60%完成）**

このディレクトリは**優先度版アプローチ**で実装されており、最も頻繁に使用される3つのテンプレートカテゴリが完成しています。

### ✅ 完成済み（優先度高）

1. **project-templates**: プロジェクト立ち上げ時に必須
2. **documentation-templates**: 全プロジェクトで必要
3. **deployment-templates**: 本番環境デプロイ時に必須

### ⏸️ オプション（未作成・優先度低）

4. **code-templates**: 言語別コーディング標準（`01-coding-standards`）に含まれるため重複
5. **testing-templates**: 品質標準（`04-quality-standards/testing-standards.md`）に含まれるため重複

**選択理由**: パレートの法則に基づき、80%の実用性を20%の労力で達成。未作成の2カテゴリは他の標準セクションでカバーされています。

---

## 📂 各サブディレクトリの詳細

### 1. project-templates/ ✅

**目的**: 新規プロジェクトの迅速な初期化とセットアップ

**含まれるテンプレート**:
- **プロジェクト構造テンプレート**:
  - Webアプリケーション（React、Vue、Angular）
  - RESTful API（Node.js、Python、Java）
  - マイクロサービス
  - データパイプライン
- **設定ファイル**:
  - `package.json`（Node.js）
  - `requirements.txt`/`pyproject.toml`（Python）
  - `pom.xml`/`build.gradle`（Java）
- **環境設定**:
  - `.env.example` - 環境変数テンプレート
  - `.editorconfig` - エディタ設定統一
  - `.gitignore` - Git除外ファイル
  - `.gitattributes` - Git属性設定
- **IDE設定**:
  - VSCode設定（`.vscode/`）
  - IntelliJ設定（`.idea/`）

**外部文書URL**: [作成済み外部文書]

**使用方法**:
1. プロジェクトタイプを選択（Web App、API、Microservice等）
2. テンプレートをコピー: `cp -r project-templates/web-app ./my-project`
3. プレースホルダーを置換（後述）
4. 依存関係をインストール: `npm install` / `pip install -r requirements.txt`
5. 初期コミット: `git init && git add . && git commit -m "Initial commit"`

**Devin AI活用**:
```
「project-templatesから'microservice-nodejs'テンプレートを使用して、
プロジェクト名'PaymentService'で新規マイクロサービスを初期化してください。
技術スタックはNode.js + TypeScript + PostgreSQLです。」
```

---

### 2. documentation-templates/ ✅

**目的**: 統一されたドキュメント構造と高品質な技術文書作成

**含まれるテンプレート**:
- **README.md テンプレート**:
  - プロジェクト概要
  - インストール・セットアップ手順
  - 使用方法
  - API仕様
  - コントリビューションガイドライン
  - ライセンス情報
- **API仕様書テンプレート**:
  - OpenAPI 3.0仕様
  - エンドポイント一覧
  - リクエスト/レスポンス例
  - 認証方法
  - エラーコード一覧
- **アーキテクチャドキュメント**:
  - システム概要図
  - コンポーネント構成
  - データフロー
  - デプロイメント図
  - 技術スタック説明
- **ユーザーガイド**:
  - 機能説明
  - 使用手順
  - トラブルシューティング
- **運用ドキュメント**:
  - デプロイ手順
  - 監視・アラート設定
  - インシデント対応手順
  - バックアップ・リストア手順

**実ファイル（AI Drive内）**:
- `project-documentation-README.md` (10.3 KB) - 詳細なREADMEテンプレート

**外部文書URL**: [作成済み外部文書]

**使用方法**:
1. ドキュメントタイプを選択
2. テンプレートをプロジェクトにコピー
3. プレースホルダー（`[PROJECT_NAME]`等）を実際の値に置換
4. プロジェクト固有情報を追加
5. 不要なセクションを削除
6. Markdownリンティング実行

**Devin AI活用**:
```
「documentation-templatesのAPI仕様書テンプレートを使用して、
現在のコードベースからOpenAPI 3.0仕様を自動生成してください。
全エンドポイント、リクエスト/レスポンススキーマを含めてください。」
```

---

### 3. deployment-templates/ ✅

**目的**: 一貫したデプロイメント設定と環境別構成管理

**含まれるテンプレート**:
- **Docker設定**:
  - `Dockerfile` - マルチステージビルド対応
  - `docker-compose.yml` - ローカル開発環境
  - `.dockerignore` - イメージ最適化
- **Kubernetes マニフェスト**:
  - `deployment.yaml` - アプリケーションデプロイメント
  - `service.yaml` - サービス公開
  - `ingress.yaml` - 外部アクセス設定
  - `configmap.yaml` - 設定管理
  - `secret.yaml` - シークレット管理（テンプレート）
  - `hpa.yaml` - 水平オートスケーリング
- **CI/CDパイプライン**:
  - GitHub Actions（`.github/workflows/`）
  - GitLab CI（`.gitlab-ci.yml`）
  - Jenkins（`Jenkinsfile`）
- **環境別設定**:
  - `dev/` - 開発環境
  - `staging/` - ステージング環境
  - `production/` - 本番環境
- **Infrastructure as Code**:
  - Terraform設定
  - AWS CloudFormation
  - Helm Chart

**外部文書URL**: [作成済み外部文書]

**使用方法**:
1. デプロイメント環境を選択（Docker、Kubernetes、Cloud）
2. テンプレートをインフラディレクトリにコピー
3. 環境変数・リソース設定をカスタマイズ
4. シークレット管理を設定（KMS、Vault等）
5. デプロイテスト実行: `kubectl apply --dry-run=client -f k8s/`

**Devin AI活用**:
```
「deployment-templatesからKubernetesテンプレートを使用して、
本番環境用マニフェストを生成してください。
レプリカ数3、HPA有効、CPU/メモリlimits設定済みで。」
```

---

### 4. code-templates/ ⏸️ （オプション・未作成）

**状態**: 優先度版では未作成

**未作成理由**:
- 言語別コードボイラープレートは`01-coding-standards`の各言語標準に豊富な実装例として含まれる
- TypeScript/JavaScript、Python、Java、SQL、CSS標準に具体的コード例が記載済み
- 必要に応じて各言語標準から抽出・再利用可能

**将来的な内容（必要時）**:
- 言語別クラス/関数テンプレート
- デザインパターン実装例（Factory、Singleton、Observer等）
- 共通ユーティリティコード
- エラーハンドリングボイラープレート

**作成判断基準**:
- 組織規模拡大により標準化コードの需要増加
- 新規参加者の急増
- コードレビューで頻繁に同じパターン指摘

---

### 5. testing-templates/ ⏸️ （オプション・未作成）

**状態**: 優先度版では未作成

**未作成理由**:
- テストテンプレートは`04-quality-standards/testing-standards.md`に詳細に記載
- ユニット/統合/E2Eテストの実装例が品質標準に含まれる
- 各言語のテストフレームワーク（Jest、pytest、JUnit）の使用例あり

**将来的な内容（必要時）**:
- ユニットテストテンプレート
- 統合テストシナリオ
- E2Eテストテンプレート
- テストフィクスチャ・モックテンプレート
- テストデータジェネレータ
- パフォーマンステストスクリプト

**作成判断基準**:
- テストカバレッジ向上の組織的取り組み
- テスト自動化の標準化需要
- 新規テストフレームワーク導入

---

## 🛠️ テンプレート使用ガイドライン

### 基本原則

1. **カスタマイズ必須**: テンプレートは出発点であり、プロジェクト固有にカスタマイズ
2. **不要部分削除**: 使用しないセクション・ファイルは積極的に削除して簡潔に
3. **標準準拠**: 他の標準文書との整合性を維持
4. **定期更新**: テンプレート自体も定期的に見直し・改善

### カスタマイズ手順

**ステップ1: テンプレート選択**
```bash
# プロジェクトタイプに応じた適切なテンプレート選択
ls 08-templates/project-templates/
```

**ステップ2: コピー**
```bash
# テンプレートを新規プロジェクトディレクトリにコピー
cp -r 08-templates/project-templates/web-app-react ./my-new-project
cd my-new-project
```

**ステップ3: プレースホルダー置換**
```bash
# 一括置換（Linux/Mac）
find . -type f -exec sed -i 's/\[PROJECT_NAME\]/MyProject/g' {} +
find . -type f -exec sed -i 's/\[AUTHOR_NAME\]/YourName/g' {} +
find . -type f -exec sed -i 's/\[AUTHOR_EMAIL\]/your@email.com/g' {} +
```

**ステップ4: カスタマイズ**
- 不要なファイル・セクション削除
- プロジェクト固有の要件追加
- 依存関係の調整・更新

**ステップ5: 検証**
```bash
# リンティング・フォーマット実行
npm run lint
npm run format

# ビルド・テスト実行
npm run build
npm test

# 標準準拠チェック（該当する場合）
```

---

## 📋 プレースホルダー一覧

テンプレート内で使用される標準プレースホルダー：

| プレースホルダー | 説明 | 例 |
|----------------|------|-----|
| `[PROJECT_NAME]` | プロジェクト名 | PaymentService |
| `[PROJECT_DESCRIPTION]` | プロジェクト説明文 | A microservice for payment processing |
| `[AUTHOR_NAME]` | 作成者名 | John Doe |
| `[AUTHOR_EMAIL]` | 作成者メールアドレス | john.doe@company.com |
| `[LICENSE]` | ライセンス | MIT / Apache 2.0 |
| `[VERSION]` | 初期バージョン | 1.0.0 / 0.1.0 |
| `[TECH_STACK]` | 技術スタック | React, Node.js, PostgreSQL |
| `[REPOSITORY_URL]` | リポジトリURL | https://github.com/org/repo |
| `[API_BASE_URL]` | API基本URL | https://api.example.com/v1 |
| `[DATABASE_NAME]` | データベース名 | payment_service_db |
| `[PORT]` | アプリケーションポート | 3000 / 8080 |
| `[ENVIRONMENT]` | 環境名 | development / staging / production |

---

## 🤖 Devin AI向けテンプレート活用

### 自動プロジェクト初期化

**指示例**:
```
「project-templatesから'microservice-python'テンプレートを使用して、
プロジェクト名'UserService'、説明'ユーザー管理マイクロサービス'で
新規プロジェクトを初期化してください。
技術スタックはPython 3.11 + FastAPI + PostgreSQL、
ポート番号8001で設定してください。」
```

**Devin AIの実行内容**:
1. テンプレートディレクトリのコピー
2. 全プレースホルダーの自動置換
3. `requirements.txt`の生成・依存関係更新
4. `.env.example`のカスタマイズ
5. 初期Gitコミット実行
6. README.md生成

### 自動ドキュメント生成

**指示例**:
```
「documentation-templatesを使用して、
現在のコードベースから包括的なREADMEを自動生成してください。
インストール手順、使用方法、API仕様の概要、
コントリビューションガイドラインを含めてください。」
```

**Devin AIの実行内容**:
1. コードベースの分析（依存関係、エンドポイント抽出）
2. テンプレートへの情報マッピング
3. 技術スタック自動検出
4. コード例の自動生成
5. Markdownフォーマット整形

### 自動デプロイ設定生成

**指示例**:
```
「deployment-templatesからKubernetesテンプレートを使用して、
本番環境用マニフェストを生成してください。
- レプリカ数: 3
- オートスケーリング: 有効（最小3、最大10）
- リソース: CPU 500m、メモリ512Mi（requests）/ CPU 1、メモリ1Gi（limits）
- ヘルスチェック: /health エンドポイント
- 環境変数: DATABASE_URLはsecretから取得」
```

**Devin AIの実行内容**:
1. Kubernetes テンプレートのコピー
2. 環境変数・リソース設定のカスタマイズ
3. Secret/ConfigMap生成
4. HPA（オートスケーリング）設定
5. マニフェスト検証（`kubectl apply --dry-run`）
6. デプロイドキュメント生成

---

## 🔗 関連標準との統合

### コーディング規約との統合
- テンプレート内のコード例は`01-coding-standards`に準拠
- 言語別の標準設定ファイル（ESLint、Pylint等）を含む

### アーキテクチャ標準との統合
- プロジェクト構造は`02-architecture-standards`のパターンに準拠
- マイクロサービステンプレートは`microservices-guidelines.md`に準拠

### 開発プロセスとの統合
- Git設定は`03-development-process/git-workflow.md`に準拠
- CI/CDテンプレートはリリース管理標準と整合

### 品質標準との統合
- テストディレクトリ構造は`04-quality-standards`に準拠
- CI/CD設定に品質ゲート統合

### ツール・環境との統合
- CI/CDテンプレートは`06-tools-and-environment`の標準ツールを使用
- コンテナ化設定は`containerization.md`標準に準拠

---

## ✅ Devin AIチェックリスト

テンプレート使用時の確認項目：

**プロジェクト初期化**:
- [ ] 適切なテンプレートを選択した
- [ ] 全てのプレースホルダーを置換した
- [ ] 不要なファイル・セクションを削除した
- [ ] プロジェクト固有の要件を追加した
- [ ] 依存関係を更新・インストールした

**標準準拠**:
- [ ] コーディング規約に準拠している
- [ ] アーキテクチャ標準に準拠している
- [ ] ドキュメントが完成している
- [ ] テスト環境が構築されている

**動作確認**:
- [ ] ビルドが成功する
- [ ] テストが実行できる
- [ ] ローカル環境で起動できる
- [ ] CI/CDパイプラインが動作する

**デプロイ準備**:
- [ ] デプロイ設定が検証済み
- [ ] 環境変数・シークレットが設定済み
- [ ] ヘルスチェックが実装されている
- [ ] 監視・ログ設定が完了している

**バージョン管理**:
- [ ] 初期コミットを実行した
- [ ] `.gitignore`が適切に設定されている
- [ ] リポジトリ（GitHub/GitLab等）を作成した

---

## 📞 お問い合わせ

テンプレートに関する質問、新規テンプレート追加提案、改善要望は以下を参照：
- **[10-governance/update-process.md](../10-governance/)**: 標準更新プロセス
- **[10-governance/exception-process.md](../10-governance/)**: 例外承認プロセス
- **[09-reference/escalation-matrix.md](../09-reference/)**: エスカレーションフロー

---

## 📚 外部文書リンク

各テンプレートカテゴリの詳細文書（外部URL管理）:

1. **project-templates/** - [作成済み外部文書URL]
2. **documentation-templates/** - [作成済み外部文書URL]
3. **deployment-templates/** - [作成済み外部文書URL]

実ファイル（AI Drive内）:
- **documentation-templates/project-documentation-README.md** (10.3 KB)

---

**最終更新**: 2025-10-17  
**バージョン**: 1.0.0（優先度版）  
**管理者**: 標準化委員会  
**レビューサイクル**: 四半期ごと

**注**: 優先度版として3/5サブディレクトリ完成。code-templates、testing-templatesは他標準でカバー済みのため未作成。
