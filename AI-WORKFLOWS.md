# Devin AI ワークフロー詳細ガイド

## 📋 このドキュメントについて

このドキュメントは、Devin AIがプロジェクトの各フェーズで**どの標準をどの順序で参照し、どう実装するか**の詳細ワークフローを提供します。

**関連ドキュメント**:
- [AI-USAGE-GUIDE.md](./AI-USAGE-GUIDE.md) - 全体概要・ナビゲーション
- [AI-PROMPTS.md](./AI-PROMPTS.md) - 具体的プロンプト例
- [AI-CHECKLISTS.md](./AI-CHECKLISTS.md) - 自己検証チェックリスト

---

## 🔄 開発ライフサイクル全体像

```
┌─────────────────────────────────────────────────────────┐
│  フェーズ1: プロジェクト初期化                              │
│  ├─ 要件分析                                              │
│  ├─ 技術選定                                              │
│  ├─ プロジェクトセットアップ                                │
│  └─ 初期ドキュメント作成                                    │
├─────────────────────────────────────────────────────────┤
│  フェーズ2: 設計・開発                                      │
│  ├─ アーキテクチャ設計                                      │
│  ├─ コード実装                                            │
│  ├─ テスト作成                                            │
│  └─ 継続的な品質検証                                        │
├─────────────────────────────────────────────────────────┤
│  フェーズ3: レビュー・統合                                  │
│  ├─ コードレビュー                                         │
│  ├─ 統合テスト                                            │
│  ├─ セキュリティ検証                                        │
│  └─ ドキュメント更新                                        │
├─────────────────────────────────────────────────────────┤
│  フェーズ4: デプロイメント                                  │
│  ├─ デプロイ設定                                           │
│  ├─ CI/CD構築                                            │
│  ├─ 本番デプロイ                                           │
│  └─ 監視・ログ設定                                         │
├─────────────────────────────────────────────────────────┤
│  フェーズ5: 運用・保守                                      │
│  ├─ モニタリング                                           │
│  ├─ インシデント対応                                        │
│  ├─ 継続的改善                                            │
│  └─ ドキュメント保守                                        │
└─────────────────────────────────────────────────────────┘
```

---

## 📦 フェーズ1: プロジェクト初期化

### 🎯 フェーズ目標
新規プロジェクトを標準に準拠した構造で迅速に立ち上げる

### 📋 参照する標準（優先順）

1. **05-technology-stack** - 技術選定
2. **08-templates** - プロジェクトテンプレート
3. **02-architecture-standards** - アーキテクチャパターン
4. **03-development-process** - Git設定

### 🔄 詳細ワークフロー

#### ステップ1: 要件分析 (5-10分)

**入力**: プロジェクト要件（人間から提供）

**タスク**:
1. 要件を以下のカテゴリに分類:
   - 機能要件
   - 非機能要件（パフォーマンス、セキュリティ等）
   - 技術的制約
   - ビジネス制約

2. 重要な質問を特定:
   - システムタイプは？（Web App、API、Microservice、Data Pipeline等）
   - 予想ユーザー数・負荷は？
   - セキュリティ・コンプライアンス要件は？
   - チーム規模・スキルセットは？

**出力**: 要件分析ドキュメント

---

#### ステップ2: 技術スタック選定 (10-15分)

**参照標準**: `05-technology-stack/`

**タスク**:
1. `approved-technologies.md`で承認済み技術確認
2. プロジェクトタイプに応じて選択:
   - Web App → `frontend-technologies.md`
   - API/Backend → `backend-technologies.md`
   - データベース → `database-technologies.md`
   - インフラ → `infrastructure-technologies.md`

3. `09-reference/decision-matrix.md`で評価:
   - 各候補技術をスコアリング
   - 重み付け適用
   - 最適な技術を選定

4. バージョン・互換性確認

**プロンプト例**:
```
「05-technology-stack/backend-technologies.mdから、
要件に最適なバックエンドフレームワークを選定してください。
要件: RESTful API、高スループット、PostgreSQL使用、TypeScript優先。
09-reference/decision-matrix.mdで評価してください。」
```

**出力**: 技術スタック決定書（Markdown）

---

#### ステップ3: プロジェクト初期化 (15-20分)

**参照標準**: `08-templates/project-templates/`

**タスク**:
1. 適切なテンプレートを選択:
   - Web Application（React/Vue/Angular）
   - REST API（Node.js/Python/Java）
   - Microservice
   - Data Pipeline

2. テンプレートをコピー・カスタマイズ:
   ```bash
   cp -r 08-templates/project-templates/<type> ./<project-name>
   ```

3. プレースホルダー置換（`08-templates/README.md`参照）:
   - `[PROJECT_NAME]` → 実際のプロジェクト名
   - `[AUTHOR_NAME]` → 開発者名
   - `[DATABASE_NAME]` → DB名
   - 等々

4. 依存関係インストール:
   ```bash
   npm install  # または pip install -r requirements.txt
   ```

**プロンプト例**:
```
「08-templates/project-templates/microservice-nodejsを使用して、
プロジェクト名'PaymentService'で初期化してください。
全プレースホルダーを適切な値に置換し、
依存関係をインストールしてください。」
```

**出力**: 初期化されたプロジェクトディレクトリ

---

#### ステップ4: Git設定 (5分)

**参照標準**: `03-development-process/git-workflow.md`

**タスク**:
1. Gitリポジトリ初期化:
   ```bash
   git init
   ```

2. `.gitignore`設定（テンプレートに含まれる）

3. 初期ブランチ設定（Git Flow準拠）:
   ```bash
   git checkout -b develop
   ```

4. 初期コミット:
   ```bash
   git add .
   git commit -m "chore: initial project setup"
   ```

**出力**: Git管理されたプロジェクト

---

#### ステップ5: アーキテクチャ設計 (20-30分)

**参照標準**: `02-architecture-standards/`

**タスク**:
1. `system-architecture-patterns.md`から適切なパターン選択:
   - レイヤードアーキテクチャ
   - マイクロサービス
   - イベント駆動
   - CQRS

2. コンポーネント分解:
   - プレゼンテーション層
   - ビジネスロジック層
   - データアクセス層

3. `api-architecture.md`でAPI設計:
   - エンドポイント定義
   - リクエスト/レスポンススキーマ
   - 認証方式

4. `database-architecture.md`でDB設計:
   - テーブル設計
   - リレーションシップ
   - インデックス戦略

**出力**: アーキテクチャ設計ドキュメント（Markdown + 図）

---

#### ステップ6: 初期ドキュメント作成 (15分)

**参照標準**: `08-templates/documentation-templates/`

**タスク**:
1. `project-documentation-README.md`テンプレート使用
2. 以下のセクションを埋める:
   - プロジェクト概要
   - 技術スタック
   - セットアップ手順
   - アーキテクチャ概要
   - コントリビューションガイド

**出力**: README.md

---

### ✅ フェーズ1完了チェックリスト

→ 詳細は [AI-CHECKLISTS.md](./AI-CHECKLISTS.md)#プロジェクト初期化チェックリスト

- [ ] 要件分析完了
- [ ] 技術スタック選定・文書化
- [ ] プロジェクトテンプレートから初期化
- [ ] Git設定完了
- [ ] アーキテクチャ設計完了
- [ ] README.md作成完了
- [ ] 初期コミット完了

---

## 💻 フェーズ2: 設計・開発

### 🎯 フェーズ目標
標準に準拠した高品質なコードを実装

### 📋 参照する標準（優先順）

1. **01-coding-standards** - コーディング規約
2. **02-architecture-standards** - アーキテクチャパターン実装
3. **04-quality-standards** - テスト作成
4. **07-security-compliance** - セキュリティ実装

### 🔄 詳細ワークフロー

#### ステップ1: 機能設計 (10-15分/機能)

**タスク**:
1. ユーザーストーリー/要件を技術タスクに分解
2. `02-architecture-standards`のパターンに基づいて設計
3. インターフェース定義
4. データモデル設計

**出力**: 機能設計ドキュメント

---

#### ステップ2: コード実装 (時間は機能による)

**参照標準**: `01-coding-standards/<language>-standards.md`

**タスク（例: Python）**:
1. `python-standards.md`を読み込み
2. 以下を遵守してコード生成:
   - PEP 8スタイル
   - 型ヒント使用
   - 適切な命名規約
   - ドキュメント文字列
   - エラーハンドリング

**プロンプト例**:
```
「01-coding-standards/python-standards.mdに準拠して、
ユーザー認証機能を実装してください。
- FastAPIルーター使用
- Pydanticモデルでバリデーション
- 型ヒント完備
- 適切なエラーハンドリング
- docstring記述」
```

**継続的な確認**:
- コード生成後、自己レビュー
- `01-coding-standards/00-general-principles.md`の原則確認
- SOLID原則、DRY原則の適用確認

**出力**: 実装されたコード

---

#### ステップ3: テストコード作成 (実装時間の30-40%)

**参照標準**: `04-quality-standards/testing-standards.md`

**タスク**:
1. ユニットテスト作成:
   - カバレッジ目標: 80%以上
   - 各関数/メソッドのテストケース
   - エッジケース考慮
   - モック使用（外部依存）

2. 統合テスト作成:
   - コンポーネント間の連携確認
   - データベース統合

**プロンプト例**:
```
「04-quality-standards/testing-standards.mdに基づいて、
UserAuthenticationServiceのユニットテストを作成してください。
- pytest使用
- カバレッジ80%以上
- モック: データベースアクセス、外部API
- エッジケース: 無効な認証情報、期限切れトークン等」
```

**出力**: テストコード（`tests/`ディレクトリ）

---

#### ステップ4: セキュリティ実装 (継続的)

**参照標準**: `07-security-compliance/`

**タスク（該当する場合）**:
1. 認証実装:
   - `authentication-authorization.md`参照
   - OAuth 2.0 / JWT実装

2. データ保護:
   - `data-protection.md`参照
   - 暗号化、サニタイゼーション

3. 脆弱性対策:
   - SQLインジェクション防止
   - XSS防止
   - CSRF防止

**出力**: セキュアなコード

---

#### ステップ5: コードレビュー準備 (10-15分)

**参照標準**: `03-development-process/code-review-process.md`

**タスク**:
1. 自己レビューチェックリスト実行
2. リンター実行:
   ```bash
   npm run lint  # または pylint、checkstyle等
   ```
3. フォーマッター実行:
   ```bash
   npm run format  # または black、prettier等
   ```
4. 全テスト実行:
   ```bash
   npm test
   ```

**出力**: レビュー準備完了のコード

---

### ✅ フェーズ2完了チェックリスト

→ 詳細は [AI-CHECKLISTS.md](./AI-CHECKLISTS.md)#開発チェックリスト

- [ ] 機能設計完了
- [ ] コーディング規約準拠
- [ ] ユニットテストカバレッジ80%以上
- [ ] セキュリティベストプラクティス適用
- [ ] リンター・フォーマッター実行
- [ ] 全テスト成功
- [ ] ドキュメント更新

---

## 🔍 フェーズ3: レビュー・統合

### 🎯 フェーズ目標
品質検証と統合確認

### 📋 参照する標準

1. **03-development-process/code-review-process.md**
2. **04-quality-standards/**
3. **07-security-compliance/**

### 🔄 詳細ワークフロー

#### ステップ1: プルリクエスト作成

**参照標準**: `03-development-process/git-workflow.md`

**タスク**:
1. ブランチ作成（まだの場合）:
   ```bash
   git checkout -b feature/user-authentication
   ```

2. コミット（規約準拠）:
   ```bash
   git commit -m "feat(auth): implement JWT authentication"
   ```

3. プッシュ:
   ```bash
   git push origin feature/user-authentication
   ```

4. プルリクエスト作成（テンプレート使用）

**出力**: プルリクエスト

---

#### ステップ2: 自動化チェック確認

**参照標準**: `06-tools-and-environment/ci-cd-pipeline.md`

**確認項目**:
- [ ] ビルド成功
- [ ] 全テスト成功
- [ ] リンター成功
- [ ] カバレッジ基準達成
- [ ] セキュリティスキャン成功

**不合格の場合**: 修正→再プッシュ

---

#### ステップ3: 統合テスト

**参照標準**: `04-quality-standards/testing-standards.md`

**タスク**:
1. 統合テスト環境で実行
2. データベース統合確認
3. 外部サービス統合確認
4. E2Eテスト実行（該当する場合）

---

### ✅ フェーズ3完了チェックリスト

→ 詳細は [AI-CHECKLISTS.md](./AI-CHECKLISTS.md)#レビューチェックリスト

- [ ] プルリクエスト作成
- [ ] CI/CDチェック全て成功
- [ ] 統合テスト成功
- [ ] セキュリティスキャン合格
- [ ] ドキュメント更新
- [ ] レビュー承認取得

---

## 🚀 フェーズ4: デプロイメント

### 🎯 フェーズ目標
本番環境への安全なデプロイ

### 📋 参照する標準

1. **06-tools-and-environment/containerization.md**
2. **08-templates/deployment-templates/**
3. **03-development-process/release-management.md**

### 🔄 詳細ワークフロー

#### ステップ1: デプロイ設定作成

**参照標準**: `08-templates/deployment-templates/`

**タスク**:
1. Dockerfile作成:
   - `containerization.md`のベストプラクティス準拠
   - マルチステージビルド
   - 非rootユーザー

2. Kubernetes マニフェスト作成:
   - Deployment、Service、Ingress
   - ConfigMap、Secret
   - HPA（オートスケーリング）

3. CI/CDパイプライン設定:
   - デプロイステージ追加
   - 環境別設定

**プロンプト例**:
```
「08-templates/deployment-templates/kubernetesを使用して、
PaymentServiceの本番環境マニフェストを作成してください。
- レプリカ数: 3
- HPA: 最小3、最大10
- リソース: CPU 500m/1、メモリ512Mi/1Gi
- ヘルスチェック: /health」
```

**出力**: デプロイ設定ファイル

---

#### ステップ2: 監視・ログ設定

**参照標準**: `06-tools-and-environment/monitoring-logging.md`

**タスク**:
1. 構造化ログ実装
2. メトリクスエンドポイント公開（Prometheus形式）
3. ダッシュボード作成
4. アラート設定

**出力**: 監視・ログ設定

---

#### ステップ3: デプロイ実行

**参照標準**: `03-development-process/release-management.md`

**タスク**:
1. バージョンタグ作成:
   ```bash
   git tag v1.0.0
   ```

2. デプロイ（CI/CD自動化）
3. デプロイ検証:
   - ヘルスチェック確認
   - ログ確認
   - メトリクス確認

4. スモークテスト実行

**出力**: 本番環境で稼働するアプリケーション

---

### ✅ フェーズ4完了チェックリスト

→ 詳細は [AI-CHECKLISTS.md](./AI-CHECKLISTS.md)#デプロイチェックリスト

- [ ] デプロイ設定作成・検証
- [ ] 監視・ログ設定完了
- [ ] バージョンタグ作成
- [ ] デプロイ成功
- [ ] ヘルスチェック正常
- [ ] スモークテスト成功
- [ ] ロールバック手順確認

---

## 🔧 フェーズ5: 運用・保守

### 🎯 フェーズ目標
安定稼働と継続的改善

### 📋 参照する標準

1. **06-tools-and-environment/monitoring-logging.md**
2. **07-security-compliance/incident-response.md**
3. **10-governance/compliance-audit.md**

### 🔄 詳細ワークフロー

#### 日常運用タスク

**モニタリング**:
- ダッシュボード監視
- アラート対応
- パフォーマンス分析

**インシデント対応**:
- `07-security-compliance/incident-response.md`参照
- `09-reference/escalation-matrix.md`でエスカレーション

**継続的改善**:
- パフォーマンスボトルネック解決
- セキュリティパッチ適用
- 依存関係更新

---

## 🔄 ワークフロー統合図

```
[要件] → [技術選定] → [初期化] → [設計] → [実装] → [テスト]
                ↓
           [レビュー] → [統合] → [デプロイ] → [運用]
                                        ↓
                                   [フィードバック]
                                        ↓
                                  [継続的改善] ←┘
```

---

## 📞 サポート

ワークフローで不明な点がある場合:
- **[AI-USAGE-GUIDE.md](./AI-USAGE-GUIDE.md)** - 全体概要に戻る
- **[AI-PROMPTS.md](./AI-PROMPTS.md)** - 具体的なプロンプト例を見る
- **[09-reference/escalation-matrix.md](./09-reference/)** - 人間にエスカレーション

---

**最終更新**: 2025-10-17  
**バージョン**: 1.0.0
