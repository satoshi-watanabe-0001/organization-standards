# ツール・環境標準 / Tools and Environment

---

**メタデータ / Metadata**
```yaml
version: 1.0.0
last_updated: 2025-10-27
status: active
owner: DevOps Team
category: tools-environment
```

---

## 📋 概要

このディレクトリには、開発に使用するツール、CI/CDパイプライン、監視・ログ管理、環境構成、コンテナ化に関する標準が含まれています。これらの標準は、開発効率の向上、一貫した環境構築、自動化の促進を目的としています。

**重要度**: Tier 3（中程度）  
**対象**: 全開発者、DevOpsエンジニア、Devin AI  
**更新頻度**: 半期ごとのレビュー推奨

---

## 📁 ファイル一覧

このディレクトリには以下の5つの外部文書が含まれています：

### 🛠️ 1. development-tools.md

**概要**: 承認済み開発ツールとその使用標準

**主要トピック**:
- **IDE（統合開発環境）**:
  - Visual Studio Code: 推奨拡張機能、設定
  - IntelliJ IDEA: プロジェクト設定、プラグイン
  - PyCharm: Python開発専用設定

**概要**: 承認済み開発ツールとその使用標準

**主要トピック**:
- **IDE（統合開発環境）**:
  - Visual Studio Code: 推奨拡張機能、設定
  - IntelliJ IDEA: プロジェクト設定、プラグイン
  - PyCharm: Python開発専用設定
- **デバッガ**: ブレークポイント戦略、リモートデバッグ
- **パッケージマネージャ**:
  - npm/yarn（JavaScript/TypeScript）
  - pip/poetry（Python）
  - Maven/Gradle（Java）
- **バージョン管理**: Git、GUI クライアント
- **コード品質ツール**: ESLint、Pylint、SonarQube
- **API開発ツール**: Postman、Insomnia、Swagger Editor

**対象者**: 全開発者、Devin AI  
**外部文書URL**: [作成済み外部文書]

---

### ⚙️ 2. ci-cd-pipeline.md

**概要**: CI/CDパイプラインの標準構成と実装ガイドライン

**主要トピック**:
- **CI/CDプラットフォーム**:
  - GitHub Actions: ワークフロー設定、マトリックスビルド
  - GitLab CI/CD: パイプライン構成、ランナー設定
  - Jenkins: パイプライン as Code、プラグイン管理
- **パイプライン段階**:
  - ビルド: コンパイル、依存関係解決
  - テスト: ユニット、統合、E2E実行
  - 静的解析: コード品質、セキュリティスキャン
  - デプロイ: 環境別デプロイ戦略
- **成果物管理**: Docker Registry、npm Registry、Artifactory
- **通知・レポート**: Slack通知、メール、ダッシュボード
- **シークレット管理**: 環境変数、KMS、Vault統合

**対象者**: DevOpsエンジニア、バックエンド開発者、Devin AI  
**外部文書URL**: [作成済み外部文書]

---

### 📊 3. monitoring-logging.md

**概要**: アプリケーション監視とログ管理の標準

**主要トピック**:
- **APM（Application Performance Monitoring）**:
  - New Relic: トランザクショントレース、エラー追跡
  - Datadog: メトリクス収集、ダッシュボード
  - Elastic APM: 分散トレーシング
- **ログ集約**:
  - ELK Stack（Elasticsearch、Logstash、Kibana）
  - CloudWatch Logs（AWS）
  - Stackdriver（GCP）
- **ログフォーマット**: 構造化ログ（JSON）、ログレベル標準
- **アラート設定**:
  - 閾値設定: CPU、メモリ、エラー率
  - エスカレーションポリシー
  - オンコール体制
- **ダッシュボード**: ビジネスKPI、技術メトリクス
- **トレーシング**: 分散トレーシング、スパン管理

**対象者**: DevOpsエンジニア、SREチーム、運用チーム、Devin AI  
**外部文書URL**: [作成済み外部文書]

---

### 🌍 4. environment-management.md

**概要**: 開発・ステージング・本番環境の分離と管理標準

**主要トピック**:
- **環境分離戦略**:
  - Development（開発）: 開発者個別環境、高頻度デプロイ
  - Staging（ステージング）: 本番同等環境、リリース前検証
  - Production（本番）: 本番環境、厳格な変更管理
- **環境別設定管理**:
  - 環境変数: .env ファイル、KMS、Secrets Manager
  - 設定ファイル: config.dev.json、config.prod.json
  - フィーチャーフラグ: 段階的機能リリース
- **データ管理**:
  - テストデータ: 匿名化、合成データ生成
  - データベース分離: 環境別データベース
  - データ同期: 本番→ステージングのサニタイズ
- **アクセス制御**:
  - 環境別IAM/RBAC設定
  - VPN、ネットワーク分離
  - 本番環境への厳格なアクセス管理
- **インフラストラクチャ as Code**: Terraform、CloudFormation

**対象者**: DevOpsエンジニア、インフラエンジニア、Devin AI  
**外部文書URL**: [作成済み外部文書]

---

### 🐳 5. containerization.md

**概要**: Docker/Kubernetesを用いたコンテナ化の標準

**主要トピック**:
- **Docker標準**:
  - Dockerfile ベストプラクティス: マルチステージビルド、レイヤーキャッシング
  - イメージサイズ最適化: Alpine Linux、不要ファイル除外
  - セキュリティ: 非rootユーザー、脆弱性スキャン
  - Docker Compose: ローカル開発環境構築
- **Kubernetes標準**:
  - マニフェスト構成: Deployment、Service、Ingress、ConfigMap、Secret
  - リソース管理: requests/limits設定、HPA（オートスケーリング）
  - ヘルスチェック: Liveness/Readiness Probe
  - ネームスペース: 環境別分離
  - ストレージ: PersistentVolume、StatefulSet
- **Helm**: チャート管理、テンプレート化
- **レジストリ管理**: Docker Hub、ECR、GCR、プライベートレジストリ
- **セキュリティ**: イメージスキャン、ポリシー管理、ネットワークポリシー

**対象者**: DevOpsエンジニア、全開発者、Devin AI  
**外部文書URL**: [作成済み外部文書]

---

## 🎯 使用ガイドライン

### Devin AIによる自律実行

**プロジェクト開始時**:
1. `development-tools.md`から承認済みツールを選択
2. `ci-cd-pipeline.md`に基づいてパイプライン設定を生成
3. `containerization.md`に準拠したDockerfile/Kubernetes設定を作成

**開発中**:
1. `environment-management.md`に基づいて環境別設定を管理
2. `monitoring-logging.md`の標準に従ってログ・メトリクス実装
3. CI/CDパイプラインでの自動化実行

**運用時**:
1. 監視ダッシュボードの自動構築
2. アラート設定の標準化
3. ログ分析とトラブルシューティング

### 人間開発者による使用

**新規プロジェクト**:
- 環境構築時に承認済みツール一覧から選択
- CI/CDパイプラインのテンプレート活用
- コンテナ化標準に基づいたDockerfile作成

**既存プロジェクト改善**:
- 監視・ログ標準への段階的移行
- CI/CD自動化の強化
- コンテナ化への移行

### DevOpsチームによる使用

**インフラ管理**:
- 環境分離戦略の実装
- Kubernetesクラスター管理
- 監視・アラート体制の整備

**継続的改善**:
- ツール評価と更新
- パイプライン最適化
- セキュリティ強化

---

## 🔗 関連標準

### 開発プロセスとの統合
- **[03-development-process/git-workflow.md](../03-development-process/)**: Git運用とCI/CD統合
- **[03-development-process/release-management.md](../03-development-process/)**: リリースプロセスとデプロイパイプライン

### 品質標準との統合
- **[04-quality-standards/testing-standards.md](../04-quality-standards/)**: テスト自動化とCI/CD統合
- **[04-quality-standards/code-quality-metrics.md](../04-quality-standards/)**: 静的解析ツール統合

### セキュリティとの統合
- **[07-security-compliance/vulnerability-management.md](../07-security-compliance/)**: コンテナイメージスキャン、脆弱性検出
- **[07-security-compliance/data-protection.md](../07-security-compliance/)**: シークレット管理、暗号化

### インフラとの統合
- **[05-technology-stack/infrastructure-technologies.md](../05-technology-stack/)**: 承認済みインフラ技術
- **[02-architecture-standards/](../02-architecture-standards/)**: アーキテクチャとインフラの整合性

---

## 🚀 クイックスタート

### 1. 開発環境セットアップ（新規開発者向け）
```bash
# 必須ツールのインストール確認
git --version
docker --version
kubectl version --client

# IDEのインストール（Visual Studio Code推奨）
# 推奨拡張機能のインストール（development-tools.md参照）
```

### 2. CI/CDパイプライン構築（プロジェクト初期）
```yaml
# .github/workflows/ci.yml の例
# 詳細は ci-cd-pipeline.md を参照
name: CI Pipeline
on: [push, pull_request]
jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Run tests
        run: npm test
      - name: Build
        run: npm run build
```

### 3. コンテナ化（既存プロジェクト）
```dockerfile
# Dockerfile の例
# 詳細は containerization.md を参照
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:18-alpine
WORKDIR /app
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/node_modules ./node_modules
USER node
CMD ["node", "dist/index.js"]
```

### 4. 監視・ログ設定（運用開始前）
```javascript
// 構造化ログの例
// 詳細は monitoring-logging.md を参照
logger.info({
  event: 'user_login',
  userId: user.id,
  timestamp: new Date().toISOString(),
  metadata: { source: 'web' }
});
```

---

## 📝 Devin AIチェックリスト

プロジェクトでツール・環境標準を適用する際のチェック項目：

**開発ツール**:
- [ ] 承認済みIDE・エディタを使用している
- [ ] 推奨拡張機能・プラグインをインストールした
- [ ] コード品質ツール（Linter、Formatter）を統合した
- [ ] API開発ツールを設定した

**CI/CD**:
- [ ] CI/CDプラットフォームを選択・設定した
- [ ] パイプライン段階（ビルド、テスト、デプロイ）を実装した
- [ ] 静的解析・セキュリティスキャンを統合した
- [ ] 環境別デプロイ戦略を実装した
- [ ] 成果物管理とバージョニングを設定した

**監視・ログ**:
- [ ] APMツールを統合した
- [ ] 構造化ログを実装した
- [ ] ログ集約システムを設定した
- [ ] アラート・通知を設定した
- [ ] ダッシュボードを作成した

**環境管理**:
- [ ] dev/staging/prod環境を分離した
- [ ] 環境別設定管理を実装した
- [ ] シークレット管理を設定した
- [ ] アクセス制御を実装した
- [ ] Infrastructure as Codeを使用した

**コンテナ化**:
- [ ] Dockerfileをベストプラクティスに従って作成した
- [ ] イメージサイズを最適化した
- [ ] セキュリティスキャンを実行した
- [ ] Kubernetes マニフェストを作成した
- [ ] リソース管理（requests/limits）を設定した
- [ ] ヘルスチェックを実装した

---

## 🔧 トラブルシューティング

### よくある問題と解決策

**問題1: CI/CDパイプラインが遅い**
- **原因**: キャッシュ未使用、並列化不足
- **解決**: 依存関係キャッシング、ジョブ並列実行、マトリックスビルド
- **参照**: `ci-cd-pipeline.md` - パフォーマンス最適化セクション

**問題2: ログが多すぎて追跡困難**
- **原因**: ログレベル設定不適切、構造化不足
- **解決**: 環境別ログレベル設定、構造化ログフォーマット採用
- **参照**: `monitoring-logging.md` - ログフォーマット標準

**問題3: コンテナイメージが大きい**
- **原因**: 不要ファイル含有、マルチステージビルド未使用
- **解決**: .dockerignore使用、マルチステージビルド、Alpine Linux採用
- **参照**: `containerization.md` - イメージ最適化セクション

**問題4: 環境間の設定差異でバグ**
- **原因**: ハードコード設定、環境変数管理不足
- **解決**: 設定外部化、環境変数・ConfigMap使用、IaC採用
- **参照**: `environment-management.md` - 設定管理セクション

---

## 📞 お問い合わせ

ツール・環境標準に関する質問、改善提案、例外申請は以下を参照：
- **[10-governance/exception-process.md](../10-governance/)**: 例外承認プロセス
- **[10-governance/update-process.md](../10-governance/)**: 標準更新プロセス
- **[09-reference/escalation-matrix.md](../09-reference/)**: エスカレーションフロー

---

## 📚 外部文書リンク

各標準の詳細文書（外部URL管理）:

1. **development-tools.md** - [外部文書URL]
2. **ci-cd-pipeline.md** - [外部文書URL]
3. **monitoring-logging.md** - [外部文書URL]
4. **environment-management.md** - [外部文書URL]
5. **containerization.md** - [外部文書URL]

---

**最終更新**: 2025-10-17  
**バージョン**: 1.0.0  
**管理者**: 標準化委員会  
**レビューサイクル**: 半期ごと
