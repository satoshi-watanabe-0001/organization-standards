---
title: "Design Artifacts Management Guide - 設計成果物管理ガイド"
version: "2.1.0"
created_date: "2025-11-12"
last_updated: "2025-11-20"
status: "Active"
owner: "Engineering Architecture Team"
category: "development-process"
phase: "Phase 2 (Design)"
---

# 設計成果物管理ガイド

> Phase 2（設計フェーズ）で作成する成果物の格納場所、命名規則、管理方法を定義

**対象読者**: 
- 🤖 自律型AIエージェント (Devin, Cursor等)
- 👤 エンジニア（全ロール）
- 📐 アーキテクト
- 📊 プロジェクトマネージャー

**目的**: 
- 設計成果物の格納場所を統一し、発見可能性を向上
- 命名規則を標準化し、バージョン管理を容易に
- 成果物のライフサイクル管理を明確化

**重要な変更（v2.0.0）**:
- **専用リポジトリ想定**: 単一プロジェクトの設計専用リポジトリを前提
- **成果物種類ベース**: フェーズ別ではなく、成果物の種類でディレクトリ分類
- **一目でわかる構造**: フォルダ名から格納内容が即座に理解可能

---

## 📚 目次

1. [基本原則](#基本原則)
2. [リポジトリ構造](#リポジトリ構造)
3. [成果物カタログ](#成果物カタログ)
4. [ファイル命名規則](#ファイル命名規則)
5. [バージョン管理](#バージョン管理)
6. [成果物のライフサイクル](#成果物のライフサイクル)
7. [実践例](#実践例)

---

## 🎯 基本原則

### 設計原則

#### 1. **専用リポジトリ管理**
- 設計ドキュメントは専用リポジトリで管理
- リポジトリ名: `{project-name}-design`
- 例: `user-service-design`, `e-commerce-platform-design`

#### 2. **成果物種類ベースの分類**
- Phase 2A/2.2 のような時系列分類は**使用しない**
- アーキテクチャ、API、データモデル等の**成果物種類で分類**
- フォルダ名から内容が一目でわかる

#### 3. **実装前/実装後の区別**
- 基本的に成果物種類のみで分類
- 実装後の差分は `as-built/` フォルダで管理
- 実装前設計書を上書きせず、as-built で補完

#### 4. **利用者視点**
- アーキテクトと開発チーム双方が使いやすい構造
- 欲しい情報にすぐアクセスできる
- 重複を排除し、単一の真実の情報源（SSOT）を維持

---

## 📂 リポジトリ構造

### 標準ディレクトリ構造

```
{project-name}-design/
│
├── README.md                     # リポジトリ全体の説明・索引
│
├── architecture/                 # アーキテクチャ設計
│   ├── system-architecture.md        # システム全体アーキテクチャ
│   ├── component-design.md           # コンポーネント設計
│   ├── integration-design.md         # 統合・連携設計
│   ├── layered-architecture.md       # レイヤードアーキテクチャ
│   └── diagrams/                     # アーキテクチャ図
│       ├── system-context.puml
│       ├── container-diagram.puml
│       ├── component-diagrams/
│       └── deployment-diagram.puml
│
├── api/                          # API設計
│   ├── specifications/               # API仕様書
│   │   ├── openapi.yaml                  # 統合Swagger（該当する場合）
│   │   └── services/                     # 各サービスのSwagger
│   │       ├── user-api.yaml
│   │       └── order-api.yaml
│   ├── contracts/                    # API契約
│   │   ├── rest-api-contracts.md
│   │   ├── grpc-contracts.proto
│   │   └── graphql-schema.graphql
│   ├── integration-patterns.md       # API統合パターン
│   └── versioning-strategy.md        # APIバージョニング戦略
│
├── data-model/                   # データモデル設計
│   ├── entity-relationship.md        # エンティティ関係設計
│   ├── schemas/                      # スキーマ定義
│   │   ├── user-schema.sql
│   │   ├── order-schema.sql
│   │   └── product-schema.sql
│   ├── migrations/                   # マイグレーション計画
│   │   ├── migration-strategy.md
│   │   └── migration-scripts/
│   └── diagrams/                     # データモデル図
│       ├── erd.puml                      # ER図
│       └── domain-model.puml
│
├── security/                     # セキュリティ設計
│   ├── security-architecture.md      # セキュリティアーキテクチャ
│   ├── threat-model.md               # 脅威モデル
│   ├── authentication-design.md      # 認証設計
│   ├── authorization-design.md       # 認可設計
│   ├── data-protection.md            # データ保護設計
│   └── compliance/                   # コンプライアンス
│       ├── gdpr-compliance.md
│       └── security-audit.md
│
├── infrastructure/               # インフラ設計
│   ├── deployment-architecture.md    # デプロイメントアーキテクチャ
│   ├── network-design.md             # ネットワーク設計
│   ├── disaster-recovery.md          # 災害復旧計画
│   ├── capacity-planning.md          # キャパシティ計画
│   └── diagrams/                     # インフラ図
│       ├── network-topology.puml
│       └── deployment-environments/
│
├── performance/                  # パフォーマンス設計
│   ├── performance-requirements.md   # パフォーマンス要件
│   ├── load-testing-strategy.md      # 負荷テスト戦略
│   ├── optimization-design.md        # 最適化設計
│   └── caching-strategy.md           # キャッシング戦略
│
├── integration/                  # 統合設計
│   ├── external-integrations.md      # 外部システム統合
│   ├── message-queue-design.md       # メッセージキュー設計
│   ├── event-driven-design.md        # イベント駆動設計
│   └── integration-patterns.md       # 統合パターン
│
├── ui-ux/                        # UI/UX設計（該当する場合）
│   ├── user-flows.md                 # ユーザーフロー
│   ├── wireframes/                   # ワイヤーフレーム
│   │   ├── login-flow.png
│   │   └── dashboard.png
│   ├── design-system.md              # デザインシステム
│   └── accessibility.md              # アクセシビリティ設計
│
├── as-built/                     # 実装後ドキュメント（As-Built）
│   ├── architecture-as-built.md      # 実装されたアーキテクチャ
│   ├── api-as-built.md               # 実装されたAPI
│   ├── data-model-as-built.md        # 実装されたデータモデル
│   ├── deviation-reports/            # 設計からの差分報告
│   │   ├── 2024-01-architecture-deviations.md
│   │   └── 2024-02-security-changes.md
│   └── lessons-learned.md            # 実装から得られた教訓
│
├── adr/                          # Architecture Decision Records
│   ├── README.md                     # ADRインデックス
│   ├── 0001-use-microservices-architecture.md
│   ├── 0002-adopt-postgresql-for-primary-database.md
│   ├── 0003-implement-api-gateway-pattern.md
│   ├── 0004-choose-jwt-for-authentication.md
│   └── template.md                   # ADRテンプレート
│
├── improvements/                 # 改善提案・技術負債
│   ├── design-improvements.md        # 設計改善提案
│   ├── technical-debt.md             # 技術負債リスト
│   ├── refactoring-proposals/        # リファクタリング提案
│   │   ├── user-service-refactoring.md
│   │   └── api-gateway-refactoring.md
│   └── performance-improvements.md   # パフォーマンス改善提案
│
├── compliance/                   # コンプライアンス（該当する場合）
│   ├── regulatory-requirements.md    # 規制要件
│   ├── gdpr-compliance.md            # GDPR対応
│   ├── audit-trail.md                # 監査証跡
│   └── data-retention.md             # データ保持ポリシー
│
├── testing/                      # テスト設計（該当する場合）
│   ├── test-strategy.md              # テスト戦略
│   ├── integration-test-design.md    # 統合テスト設計
│   └── performance-test-design.md    # パフォーマンステスト設計
│
├── archive/                      # アーカイブ（旧バージョン）
│   ├── v1.0.0/                       # バージョン1.0.0の設計
│   │   ├── architecture/
│   │   ├── api/
│   │   └── data-model/
│   └── v2.0.0/                       # バージョン2.0.0の設計
│
└── templates/                    # ドキュメントテンプレート
    ├── architecture-template.md
    ├── api-specification-template.md
    └── adr-template.md
```

### ディレクトリ説明

| ディレクトリ | 目的 | 必須/推奨 | 主な利用者 |
|------------|------|---------|----------|
| `architecture/` | システム全体のアーキテクチャ設計 | 🔴 必須 | アーキテクト |
| `api/` | API仕様と契約 | 🔴 必須 | 開発チーム |
| `data-model/` | データ構造とスキーマ | 🔴 必須 | データベース担当 |
| `security/` | セキュリティ要件と実装方針 | 🔴 必須 | セキュリティ担当 |
| `infrastructure/` | インフラとデプロイメント | 🟡 推奨 | DevOps/SRE |
| `performance/` | パフォーマンス要件と最適化 | 🟡 推奨 | 全員 |
| `integration/` | 外部システム統合 | 🟢 任意 | バックエンド |
| `ui-ux/` | UI/UX設計 | 🟢 任意 | フロントエンド |
| `as-built/` | 実装後の実際の設計 | 🔴 必須 | 全員 |
| `adr/` | 重要な設計判断記録 | 🔴 必須 | アーキテクト |
| `improvements/` | 改善提案と技術負債 | 🔴 必須 | 全員 |
| `compliance/` | コンプライアンス要件 | 🟢 任意 | コンプライアンス担当 |
| `testing/` | テスト設計 | 🟢 任意 | QA担当 |

---

## 📋 成果物カタログ

### アーキテクチャ関連

#### 1. システムアーキテクチャ設計書
- **格納先**: `architecture/system-architecture.md`
- **目的**: システム全体のアーキテクチャと技術選定
- **内容**:
  - システム全体構成
  - アーキテクチャパターン（マイクロサービス、レイヤードアーキテクチャ等）
  - 技術スタック選定理由
  - 非機能要件への対応方針
  - スケーラビリティ戦略
- **関連図**: `architecture/diagrams/system-context.puml`, `container-diagram.puml`

#### 2. コンポーネント設計書
- **格納先**: `architecture/component-design.md`
- **目的**: 個別コンポーネントの責務と関係性
- **内容**:
  - コンポーネント一覧
  - 各コンポーネントの責務
  - コンポーネント間の依存関係
  - インターフェース定義
- **関連図**: `architecture/diagrams/component-diagrams/`

#### 3. 統合・連携設計書
- **格納先**: `architecture/integration-design.md`
- **目的**: コンポーネント間の統合方法
- **内容**:
  - 統合パターン
  - 通信プロトコル
  - データフォーマット
  - エラーハンドリング

---

### API関連

#### 4. API仕様書（OpenAPI/Swagger）
- **格納先**: `api/specifications/openapi.yaml`
- **目的**: RESTful APIの完全な仕様定義
- **内容**:
  - エンドポイント一覧
  - リクエスト/レスポンス形式
  - 認証・認可方式
  - エラーレスポンス
  - APIバージョニング
- **参照**: [API Specification Management Guide](./api-specification-management-guide.md)

#### 5. API契約書
- **格納先**: `api/contracts/rest-api-contracts.md`
- **目的**: API提供者と消費者の契約
- **内容**:
  - SLA（Service Level Agreement）
  - レート制限
  - 利用規約
  - 後方互換性保証

#### 6. gRPC契約
- **格納先**: `api/contracts/grpc-contracts.proto`
- **目的**: gRPC サービス定義
- **内容**: Protocol Buffers 定義

---

### データモデル関連

#### 7. エンティティ関係設計書
- **格納先**: `data-model/entity-relationship.md`
- **目的**: ドメインエンティティと関係性
- **内容**:
  - エンティティ定義
  - リレーション定義
  - ビジネスルール
  - 制約条件
- **関連図**: `data-model/diagrams/erd.puml`

#### 8. スキーマ定義
- **格納先**: `data-model/schemas/*.sql`
- **目的**: データベーススキーマの実装
- **内容**:
  - テーブル定義（DDL）
  - インデックス戦略
  - パーティション戦略
  - ビュー定義

#### 9. マイグレーション計画
- **格納先**: `data-model/migrations/migration-strategy.md`
- **目的**: スキーマ変更の計画と実行
- **内容**:
  - マイグレーション戦略
  - ダウンタイム対策
  - ロールバック計画

---

### セキュリティ関連

#### 10. セキュリティアーキテクチャ
- **格納先**: `security/security-architecture.md`
- **目的**: セキュリティ全体設計
- **内容**:
  - セキュリティレイヤー
  - 防御戦略（Defense in Depth）
  - セキュリティコントロール

#### 11. 脅威モデル
- **格納先**: `security/threat-model.md`
- **目的**: 脅威の特定と対策
- **内容**:
  - STRIDE分析
  - 脅威シナリオ
  - リスクアセスメント
  - 対策方針

#### 12. 認証設計書
- **格納先**: `security/authentication-design.md`
- **目的**: 認証方式の設計
- **内容**:
  - 認証フロー（OAuth 2.0, SAML等）
  - トークン管理（JWT等）
  - セッション管理
  - MFA（多要素認証）

#### 13. 認可設計書
- **格納先**: `security/authorization-design.md`
- **目的**: 認可方式の設計
- **内容**:
  - RBAC（ロールベースアクセス制御）
  - ABAC（属性ベースアクセス制御）
  - ポリシー定義
  - リソースアクセス制御

---

### インフラ関連

#### 14. デプロイメントアーキテクチャ
- **格納先**: `infrastructure/deployment-architecture.md`
- **目的**: デプロイメント構成
- **内容**:
  - クラウドリソース構成
  - コンテナオーケストレーション（Kubernetes等）
  - CI/CDパイプライン
  - 環境構成（開発/ステージング/本番）
- **関連図**: `infrastructure/diagrams/deployment-diagram.puml`

#### 15. ネットワーク設計書
- **格納先**: `infrastructure/network-design.md`
- **目的**: ネットワーク構成
- **内容**:
  - ネットワークトポロジー
  - サブネット設計
  - ファイアウォールルール
  - ロードバランシング
- **関連図**: `infrastructure/diagrams/network-topology.puml`

#### 16. 災害復旧計画
- **格納先**: `infrastructure/disaster-recovery.md`
- **目的**: DR（災害復旧）戦略
- **内容**:
  - RTO（目標復旧時間）
  - RPO（目標復旧時点）
  - バックアップ戦略
  - フェイルオーバー計画

---

### パフォーマンス関連

#### 17. パフォーマンス要件書
- **格納先**: `performance/performance-requirements.md`
- **目的**: パフォーマンス要件の定義
- **内容**:
  - レスポンスタイム目標
  - スループット目標
  - 同時接続数
  - スケーラビリティ要件

#### 18. 負荷テスト戦略
- **格納先**: `performance/load-testing-strategy.md`
- **目的**: 負荷テスト計画
- **内容**:
  - テストシナリオ
  - 負荷パターン
  - 測定指標
  - ボトルネック特定方法

---

### 実装後ドキュメント

#### 19. As-Built アーキテクチャ
- **格納先**: `as-built/architecture-as-built.md`
- **目的**: 実装された実際のアーキテクチャ
- **内容**:
  - 実装された構成
  - 設計からの差分
  - 差分の理由
  - 実装時の制約・問題と解決策

#### 20. 差分報告書
- **格納先**: `as-built/deviation-reports/YYYY-MM-topic.md`
- **目的**: 設計と実装の差異記録
- **内容**:
  - 変更内容
  - 変更理由
  - 影響範囲
  - リスク評価

#### 21. 教訓ドキュメント
- **格納先**: `as-built/lessons-learned.md`
- **目的**: 実装から得られた学び
- **内容**:
  - うまくいった点
  - 困難だった点
  - 次回への改善点
  - プロセス改善提案

---

### ADR（Architecture Decision Records）

#### 22. ADR
- **格納先**: `adr/NNNN-decision-title.md`
- **目的**: 重要な設計判断の記録
- **内容**:
  - 決定事項
  - 背景・コンテキスト
  - 検討した選択肢
  - 選定理由
  - 影響・トレードオフ
  - ステータス（Proposed, Accepted, Deprecated, Superseded）
- **命名規則**: `NNNN-kebab-case-title.md`
- **テンプレート**: `templates/adr-template.md`

**ADRが必要な判断例**:
- データベース選定（PostgreSQL vs MySQL vs MongoDB）
- アーキテクチャパターン選択（マイクロサービス vs モノリス）
- 認証方式決定（OAuth 2.0 vs SAML vs JWT）
- メッセージキュー選定（Kafka vs RabbitMQ vs SQS）
- クラウドプロバイダー選定（AWS vs GCP vs Azure）

---

### 改善・技術負債

#### 23. 設計改善提案書
- **格納先**: `improvements/design-improvements.md`
- **目的**: 設計改善の提案
- **内容**:
  - 改善が必要な箇所
  - 改善案
  - 優先度（High/Medium/Low）
  - 影響範囲
  - 実施タイミング

#### 24. 技術負債リスト
- **格納先**: `improvements/technical-debt.md`
- **目的**: 技術負債の追跡
- **内容**:
  - 負債の内容
  - 発生理由
  - 影響度・緊急度
  - 解消計画
  - 担当者
- **参照**: `03-development-process/technical-debt-management.md`

---

## 📝 ファイル命名規則

### 基本ルール

#### プロジェクト共通成果物（汎用設計）

```
<category>-<description>.<extension>

例:
- system-architecture.md
- user-api.yaml
- erd.puml
- authentication-design.md
```

**命名原則**:
- **小文字**: すべて小文字を使用
- **ハイフン区切り**: 単語はハイフン（-）で区切る
- **説明的**: ファイル名から内容が推測できる
- **簡潔**: 冗長な表現を避ける

---

#### PBI固有成果物（個別PBIの設計）

PBI個別の設計成果物は、PBI-KEYをプレフィックスとして命名します。

```
{PBI-KEY}-<document-type>[-v<version>].<extension>

例:
- PROJ-1234-requirements.md
- PROJ-1234-adr-001-use-postgresql.md
- PROJ-1234-api-contract-v0.1.yaml
- PROJ-1234-api-spec-v1.0.yaml
- PROJ-1234-detailed-design.md
- PROJ-1234-sequence-payment-flow.puml
- PROJ-5678-architecture-changes.md
```

**命名原則**:
- **PBI-KEYプレフィックス**: 必須（例: PROJ-1234）
- **document-type**: 成果物の種類（requirements, api-contract, detailed-design等）
- **バージョン**: API仕様書、リリースノート等はバージョン付与
- **小文字**: PBI-KEY以外は小文字
- **ハイフン区切り**: 単語はハイフンで区切る

**配置場所の原則**:

```yaml
phase_0_outputs:
  location: "docs/requirements/"
  files:
    - "{PBI-KEY}-requirements.md"
    - "{PBI-KEY}-acceptance-criteria.md"
    - "{PBI-KEY}-technical-research.md"
    - "{PBI-KEY}-risk-analysis.md"

phase_2_1_outputs:
  location: "docs/design/pre-implementation/"
  files:
    - "{PBI-KEY}-adr-{NNN}-{title}.md"
    - "{PBI-KEY}-constraints.md"
    - "{PBI-KEY}-data-model-draft.md"
  api_contracts:
    location: "docs/api/"
    files:
      - "{PBI-KEY}-api-contract-v0.1.yaml"

phase_3_outputs:
  source_code: "src/"
  tests: "tests/"
  migrations: "scripts/migrations/"

phase_2_2_outputs:
  location: "docs/design/post-implementation/"
  files:
    - "{PBI-KEY}-detailed-design.md"
    - "{PBI-KEY}-as-built-notes.md"
    - "{PBI-KEY}-tech-debt.md"
  api_specs:
    location: "docs/api/"
    files:
      - "{PBI-KEY}-api-spec-v1.0.yaml"
  architecture:
    location: "docs/architecture/"
    files:
      - "{PBI-KEY}-c4-context.puml"
      - "{PBI-KEY}-c4-container.puml"
      - "{PBI-KEY}-sequence-{name}.puml"
      - "{PBI-KEY}-er-diagram.puml"

phase_5_outputs:
  location: "docs/operations/"
  files:
    - "{PBI-KEY}-release-notes-v{version}.md"
    - "{PBI-KEY}-deployment-record.md"
    - "{PBI-KEY}-rollback-plan.md"
    - "{PBI-KEY}-operations-manual.md"
  scripts:
    location: "scripts/deployment/"
    files:
      - "deploy-staging.sh"
      - "deploy-production.sh"
```

---

### プロジェクト統合の原則

#### 個別PBI設計とプロジェクト全体設計の位置づけ

```yaml
プロジェクト全体設計:
  目的: プロジェクト全体のアーキテクチャ、基盤設計
  対象: 汎用的な設計成果物
  配置: docs/architecture/, docs/api/specifications/, docs/data-model/
  命名: {category}-{description}.md
  例:
    - system-architecture.md
    - api-versioning-strategy.md
    - security-architecture.md

個別PBI設計:
  目的: 特定PBIの設計成果物
  対象: PBI固有の設計成果物
  配置: docs/design/{pre|post}-implementation/, docs/api/, docs/architecture/
  命名: {PBI-KEY}-{document-type}.md
  例:
    - PROJ-1234-api-contract-v0.1.yaml
    - PROJ-1234-detailed-design.md
    - PROJ-5678-architecture-changes.md
```

#### 統合ルール

**1. 共通API仕様の統合**

```
docs/api/
├── specifications/
│   ├── api-spec-master-v1.0.yaml        # 統合API仕様
│   ├── components/
│   │   ├── schemas-common.yaml          # 共通スキーマ
│   │   ├── responses-common.yaml        # 共通レスポンス
│   │   └── parameters-common.yaml       # 共通パラメータ
│   └── pbi/
│       ├── PROJ-1234-api-spec-v1.0.yaml # PBI個別仕様
│       └── PROJ-5678-api-spec-v1.0.yaml # PBI個別仕様
└── ...
```

**統合原則**:
- 個別PBIのAPI仕様は `docs/api/pbi/` に配置
- 共通コンポーネントは `$ref` で参照
- マスター仕様で全体を統合

**2. アーキテクチャドキュメントの進化**

```
docs/architecture/
├── system-architecture-v1.0.md         # 全体アーキテクチャ
├── pbi-changes/
│   ├── PROJ-1234-architecture-changes.md  # PBI個別の変更
│   └── PROJ-5678-architecture-changes.md  # PBI個別の変更
└── ...
```

**更新ルール**:
- PBI完了時に全体アーキテクチャを更新
- 個別変更記録は別ファイルで保持
- 四半期ごとに全体アーキテクチャをレビュー

**3. 複数リポジトリの場合**

```
service-a/docs/
service-b/docs/
shared-docs/                    # 共通ドキュメント専用リポジトリ
  ├── architecture/            # 全体アーキテクチャ
  └── api/                     # 統合API仕様
```

---

### バージョン管理ルール

#### ドキュメントのバージョニング

| ドキュメント種類 | バージョン管理方法 | 例 |
|----------------|--------------------|-----|
| **API仕様書** | セマンティックバージョニング | `v0.1`, `v1.0`, `v1.1` |
| **リリースノート** | リリースバージョンと一致 | `v1.0.0`, `v1.1.0` |
| **設計書** | Gitコミット履歴で管理 | (ファイル名にバージョン不要) |
| **ADR** | 番号で管理 (不変) | `adr-001`, `adr-002` |

#### API仕様書のバージョニング

```
Phase 2A: PROJ-1234-api-contract-v0.1.yaml  (ドラフト版)
         ↓ 実装開始
         ↓ 実装完了
Phase 5 (旧Phase 2B): PROJ-1234-api-spec-v1.0.yaml      (正式版)
         ↓ マイナー変更
         : PROJ-1234-api-spec-v1.1.yaml
         ↓ 破壊的変更
         : PROJ-1234-api-spec-v2.0.yaml
```

**ルール**:
- `v0.x`: Phase 2Aのドラフト版
- `v1.0`: Phase 5 (旧Phase 2B)の最初の正式版
- `v1.x`: 後方互換性のある変更
- `v2.0`: 破壊的変更

### カテゴリ別命名規則

#### アーキテクチャ関連
```
system-architecture.md
component-design.md
integration-design.md
layered-architecture.md
```

#### API関連
```
openapi.yaml
rest-api-contracts.md
grpc-contracts.proto
graphql-schema.graphql
api-versioning-strategy.md
```

#### データモデル関連
```
entity-relationship.md
user-schema.sql
order-schema.sql
migration-strategy.md
erd.puml
```

#### セキュリティ関連
```
security-architecture.md
threat-model.md
authentication-design.md
authorization-design.md
data-protection.md
```

#### 図表ファイル
```
<diagram-type>-<description>.<extension>

例:
- system-context.puml
- container-diagram.puml
- erd.puml
- deployment-diagram.puml
- network-topology.png
```

**推奨拡張子**:
- **PlantUML**: `.puml`（コードベース、バージョン管理に最適）
- **PNG**: `.png`（ラスター画像）
- **SVG**: `.svg`（ベクター画像、推奨）
- **Draw.io**: `.drawio`（Draw.io形式）

---

### ADR命名規則

```
NNNN-<decision-title-in-kebab-case>.md

例:
- 0001-use-microservices-architecture.md
- 0002-adopt-postgresql-for-primary-database.md
- 0003-implement-api-gateway-pattern.md
- 0004-choose-jwt-for-authentication.md
```

**ルール**:
- `NNNN`: 4桁の連番（0001から開始）
- タイトル: kebab-case（小文字、ハイフン区切り）
- 拡張子: `.md`
- **変更禁止**: 一度作成したADRのファイル名は変更しない

---

### As-Built/差分報告書命名規則

```
# As-Built ドキュメント
<category>-as-built.md

例:
- architecture-as-built.md
- api-as-built.md
- data-model-as-built.md

# 差分報告書
YYYY-MM-<topic>.md

例:
- 2024-01-architecture-deviations.md
- 2024-02-security-changes.md
- 2024-03-api-contract-updates.md
```

---

## 🔄 バージョン管理

### Git によるバージョン管理

**原則**: すべての設計ドキュメントは Git で管理

#### タグ戦略

```bash
# マイルストーン完了時
git tag -a design-v1.0.0 -m "Initial design completed"
git tag -a design-v1.1.0 -m "API design updated"
git tag -a design-v2.0.0 -m "Major architecture redesign"

# Phase 完了時（オプション）
git tag -a phase-2-complete -m "Phase 2 design completed"
git tag -a implementation-complete -m "Implementation completed, as-built documented"
```

#### ブランチ戦略

```bash
main              # 承認済み設計
├── feature/api-design-update      # API設計更新
├── feature/new-security-model     # セキュリティモデル追加
└── feature/performance-optimization  # パフォーマンス最適化設計
```

---

### メジャー変更時のアーカイブ

**大規模な設計変更時**はアーカイブを作成：

```bash
# 1. 現在の設計をアーカイブ
mkdir -p archive/v1.0.0
cp -r architecture/ archive/v1.0.0/
cp -r api/ archive/v1.0.0/
cp -r data-model/ archive/v1.0.0/

# 2. 新しい設計を作成
# architecture/, api/, data-model/ を更新

# 3. コミット
git add .
git commit -m "Archive v1.0.0 design and create v2.0.0 design"
git tag -a design-v2.0.0 -m "Design version 2.0.0"
```

**アーカイブが必要なケース**:
- アーキテクチャの根本的な変更（モノリス → マイクロサービス等）
- データベースの変更（MySQL → PostgreSQL等）
- 技術スタックの刷新

---

### ドキュメント内のバージョン表記

各設計ドキュメントのメタデータにバージョン情報を記載：

```yaml
---
title: "System Architecture Design"
version: "2.0.0"
created_date: "2025-01-15"
last_updated: "2025-11-12"
status: "Active"  # Active, Deprecated, Archived
author: "Architecture Team"
reviewers: ["Alice", "Bob"]
---
```

---

## 🔄 成果物のライフサイクル

### ステータス定義

| ステータス | 説明 | 対応 |
|-----------|------|------|
| **Draft** | 作成中 | レビュー待ち |
| **In Review** | レビュー中 | フィードバック反映中 |
| **Approved** | 承認済み | 実装可能 |
| **Implemented** | 実装済み | As-Built 作成推奨 |
| **Deprecated** | 非推奨 | アーカイブ予定 |
| **Archived** | アーカイブ済み | `archive/` に移動済み |

---

### ライフサイクルフロー

```
[Draft]
   ↓ 
   ├─→ レビュー要求
   ↓
[In Review]
   ↓
   ├─→ フィードバック反映
   ↓
[Approved]
   ↓
   ├─→ 実装開始（Phase 3）
   ↓
[Implemented]
   ↓
   ├─→ As-Built ドキュメント作成
   ↓
   ├─→ 大規模変更時
   ↓
[Deprecated] → [Archived]
```

---

### 更新頻度の目安

| 成果物 | 設計フェーズ | 実装フェーズ | 運用フェーズ |
|-------|------------|------------|------------|
| アーキテクチャ設計書 | 毎週 | 重大変更時 | 重大変更時のみ |
| API仕様書 | 毎週 | API変更時 | API変更時 |
| データモデル設計書 | 毎週 | スキーマ変更時 | スキーマ変更時 |
| セキュリティ設計書 | 毎週 | 脆弱性対応時 | 定期レビュー（四半期） |
| ADR | 重要判断時 | 重要判断時 | 重要判断時 |
| As-Built | - | 実装完了時 | 大規模変更時 |
| 技術負債リスト | - | 随時 | 随時 |

---

## 💡 実践例

### 例1: 新規マイクロサービスの設計

**プロジェクト**: User Service
**リポジトリ**: `user-service-design`

```
user-service-design/
│
├── README.md
│   # User Service 設計ドキュメント
│   # 最終更新: 2025-11-12
│   # ステータス: Approved
│
├── architecture/
│   ├── system-architecture.md
│   │   # マイクロサービスアーキテクチャ
│   │   # RESTful API + gRPC
│   │   # PostgreSQL + Redis
│   ├── component-design.md
│   │   # UserController, UserService, UserRepository
│   │   # AuthenticationMiddleware
│   └── diagrams/
│       ├── system-context.puml
│       ├── container-diagram.puml
│       └── component-diagram.puml
│
├── api/
│   ├── specifications/
│   │   └── user-api.yaml
│   │       # OpenAPI 3.0 仕様
│   │       # GET /users, POST /users, PUT /users/{id}
│   │       # DELETE /users/{id}
│   ├── contracts/
│   │   ├── rest-api-contracts.md
│   │   └── grpc-contracts.proto
│   └── versioning-strategy.md
│       # URI Versioning: /api/v1/users
│
├── data-model/
│   ├── entity-relationship.md
│   │   # User, Role, Permission エンティティ
│   ├── schemas/
│   │   ├── user-schema.sql
│   │   │   # users, roles, permissions, user_roles テーブル
│   │   └── migration-001-initial-schema.sql
│   └── diagrams/
│       └── erd.puml
│
├── security/
│   ├── security-architecture.md
│   ├── authentication-design.md
│   │   # JWT認証
│   │   # OAuth 2.0 統合
│   ├── authorization-design.md
│   │   # RBAC（ロールベースアクセス制御）
│   └── threat-model.md
│       # STRIDE分析
│
├── infrastructure/
│   ├── deployment-architecture.md
│   │   # Kubernetes デプロイメント
│   │   # AWS EKS
│   │   # RDS for PostgreSQL
│   │   # ElastiCache for Redis
│   └── diagrams/
│       └── deployment-diagram.puml
│
├── performance/
│   ├── performance-requirements.md
│   │   # レスポンスタイム: < 200ms (p95)
│   │   # スループット: 1000 req/sec
│   └── caching-strategy.md
│       # Redis による User キャッシュ
│
├── as-built/
│   ├── architecture-as-built.md
│   │   # 実装結果: 設計通り実装
│   │   # 変更点: Redis Cluster 追加（パフォーマンス要件達成のため）
│   └── lessons-learned.md
│       # うまくいった点: JWT認証、RBAC
│       # 困難だった点: gRPC統合のテスト
│
├── adr/
│   ├── README.md
│   ├── 0001-use-postgresql-for-primary-database.md
│   │   # 決定: PostgreSQL採用
│   │   # 理由: JSONB型、トランザクション、実績
│   ├── 0002-implement-jwt-authentication.md
│   │   # 決定: JWT認証
│   │   # 理由: ステートレス、スケーラビリティ
│   ├── 0003-adopt-rbac-for-authorization.md
│   │   # 決定: RBAC採用
│   │   # 理由: 管理しやすさ、柔軟性
│   └── 0004-use-redis-for-caching.md
│       # 決定: Redis キャッシュ
│       # 理由: パフォーマンス、セッション管理
│
├── improvements/
│   ├── design-improvements.md
│   │   # 改善提案: GraphQL対応検討
│   └── technical-debt.md
│       # 技術負債: gRPCエラーハンドリング改善必要
│
└── templates/
    ├── adr-template.md
    └── api-specification-template.md
```

---

### 例2: 既存システムの大規模リファクタリング

**プロジェクト**: E-Commerce Platform
**変更内容**: モノリスからマイクロサービスへの移行
**リポジトリ**: `ecommerce-platform-design`

```
ecommerce-platform-design/
│
├── README.md
│   # E-Commerce Platform リアーキテクチャ
│   # モノリス → マイクロサービス移行
│   # フェーズ1: User Service 分離
│   # フェーズ2: Order Service 分離
│   # フェーズ3: Product Service 分離
│
├── architecture/
│   ├── system-architecture.md
│   │   # マイクロサービスアーキテクチャ
│   │   # API Gateway パターン
│   │   # Event-Driven Architecture
│   ├── migration-strategy.md
│   │   # Strangler Fig パターン
│   │   # フェーズ別移行計画
│   └── diagrams/
│       ├── current-monolith-architecture.puml
│       ├── target-microservices-architecture.puml
│       └── migration-phases.puml
│
├── api/
│   ├── specifications/
│   │   ├── api-gateway.yaml          # 統合 Swagger
│   │   └── services/
│   │       ├── user-service.yaml
│   │       ├── order-service.yaml
│   │       └── product-service.yaml
│   └── integration-patterns.md
│       # Saga パターン（分散トランザクション）
│
├── data-model/
│   ├── entity-relationship.md
│   ├── schemas/
│   │   ├── user-service-schema.sql
│   │   ├── order-service-schema.sql
│   │   └── product-service-schema.sql
│   ├── migrations/
│   │   ├── migration-strategy.md
│   │   │   # Zero-Downtime Migration
│   │   │   # Dual-Write Pattern
│   │   └── data-migration-plan.md
│   └── diagrams/
│       ├── monolith-erd.puml
│       └── microservices-erd.puml
│
├── integration/
│   ├── event-driven-design.md
│   │   # Apache Kafka 採用
│   │   # イベントソーシング
│   ├── message-queue-design.md
│   └── saga-pattern.md
│       # Order Saga: 注文 → 在庫確保 → 決済 → 配送
│
├── as-built/
│   ├── architecture-as-built.md
│   │   # Phase 1 完了: User Service 分離成功
│   │   # Phase 2 進行中: Order Service 分離中
│   ├── deviation-reports/
│   │   ├── 2024-10-kafka-instead-of-rabbitmq.md
│   │   │   # 変更: RabbitMQ → Kafka
│   │   │   # 理由: スループット要件
│   │   └── 2024-11-saga-pattern-adjustment.md
│   │       # 変更: Saga パターン調整
│   │       # 理由: 実装複雑性
│   └── lessons-learned.md
│
├── adr/
│   ├── 0010-migrate-to-microservices.md
│   │   # 決定: マイクロサービス移行
│   │   # 理由: スケーラビリティ、独立デプロイ
│   ├── 0011-use-strangler-fig-pattern.md
│   │   # 決定: Strangler Fig パターン
│   │   # 理由: リスク低減、段階的移行
│   ├── 0012-adopt-kafka-for-event-streaming.md
│   │   # 決定: Apache Kafka 採用
│   │   # 理由: 高スループット、イベントソーシング
│   ├── 0013-implement-saga-pattern.md
│   │   # 決定: Saga パターン
│   │   # 理由: 分散トランザクション管理
│   └── 0014-use-api-gateway-pattern.md
│       # 決定: API Gateway パターン
│       # 理由: 統一エントリーポイント、認証集約
│
├── improvements/
│   ├── design-improvements.md
│   │   # 改善提案: CQRS パターン検討
│   │   # 改善提案: Circuit Breaker 実装
│   └── technical-debt.md
│       # 技術負債: モノリス残存機能のリファクタリング
│       # 技術負債: 分散トレーシング未実装
│
└── archive/
    └── v1.0.0/                       # モノリス時代の設計
        ├── architecture/
        │   └── monolith-architecture.md
        └── data-model/
            └── monolith-erd.puml
```

---

## 🔗 関連ドキュメント

### 組織標準
- [DOCUMENT-USAGE-MANUAL.md](../DOCUMENT-USAGE-MANUAL.md) - ドキュメント利用マニュアル
- [03-development-process/documentation-standards.md](./documentation-standards.md) - ドキュメント標準
- [03-development-process/api-specification-management-guide.md](./api-specification-management-guide.md) - API仕様管理ガイド
- [10-governance/architecture-decision-records.md](../10-governance/architecture-decision-records.md) - ADR管理

### Phase ガイド
- [00-guides/phase-guides/phase-2-design-guide.md](../00-guides/phase-guides/phase-2-design-guide.md) - Phase 2 総合ガイド
- [00-guides/phase-guides/phase-2A-pre-implementation-design-guide.md](../00-guides/phase-guides/phase-2A-pre-implementation-design-guide.md) - Phase 2A ガイド（実装前設計）
- [00-guides/phase-guides/phase-2B-post-implementation-design-guide.md](../00-guides/phase-guides/phase-2B-post-implementation-design-guide.md) - Phase 5 (旧Phase 2B) ガイド（実装後設計）

### テンプレート
- [08-templates/design-document-template.md](../08-templates/design-document-template.md) - 設計書テンプレート
- [08-templates/adr-template.md](../08-templates/adr-template.md) - ADRテンプレート
- [08-templates/api-specification-template.md](../08-templates/api-specification-template.md) - API仕様書テンプレート

### AI活用システム開発ドキュメント
- [AI活用システム開発ドキュメント/README_成果物重要度定義.md](../AI活用システム開発ドキュメント/README_成果物重要度定義.md) - 成果物重要度定義
- [AI活用システム開発ドキュメント/03_基本設計/](../AI活用システム開発ドキュメント/03_基本設計/) - 基本設計作成ルール集

---

## ❓ よくある質問（FAQ）

### Q1: なぜ Phase 2A/2.2 で分けないのですか？

**A**: **成果物の種類で分類する方が直感的で発見しやすい**からです。

**従来の問題点**（Phase別分類）:
- `phase-2A/architecture/` と `phase-2B/as-built/` を行き来する必要がある
- 「アーキテクチャ設計を見たい」という単純なニーズに対して複雑

**新構造の利点**（成果物種類別）:
- `architecture/` フォルダを見れば、すべてのアーキテクチャ関連情報が揃っている
- `as-built/` で実装後の差分を管理
- 実装前と実装後を比較しやすい

**区別が必要な場合の対応**:
- 実装前: `architecture/system-architecture.md` にステータス `status: Approved` を記載
- 実装後: `as-built/architecture-as-built.md` で実装結果を記録

---

### Q2: 既存プロジェクトはどうすればよいですか？

**A**: 段階的に移行してください。

#### パターンA: 最小限の移行（1-2時間）

```bash
# 新しいリポジトリを作成
mkdir project-name-design
cd project-name-design

# 基本ディレクトリ作成
mkdir -p architecture api data-model security adr as-built improvements

# 既存ドキュメントを移動
mv ../project/docs/architecture-design.md architecture/system-architecture.md
mv ../project/docs/api-spec.yaml api/specifications/openapi.yaml
mv ../project/docs/erd.png data-model/diagrams/erd.png
```

#### パターンB: 完全な移行（1-2日）

1. **計画**:
   - 既存ドキュメントの棚卸し
   - 新構造へのマッピング作成

2. **実行**:
   - 新リポジトリ作成
   - ディレクトリ構造構築
   - ドキュメント移動と整理
   - README.md 作成（索引）

3. **検証**:
   - すべてのリンクが有効か確認
   - ドキュメントの重複排除
   - 欠落している成果物の特定

#### パターンC: 段階的移行（推奨）

```bash
# Week 1: コアドキュメントのみ移行
- architecture/system-architecture.md
- api/specifications/openapi.yaml
- data-model/entity-relationship.md
- README.md

# Week 2: ADR移行
- adr/*.md

# Week 3: 残りのドキュメント移行
- security/, infrastructure/, performance/ 等

# Week 4: As-Built と改善提案追加
- as-built/
- improvements/
```

---

### Q3: ADRはすべての判断に必要ですか？

**A**: いいえ、**重要な判断のみ**です。

#### ADRが必要な判断（記録する）

✅ **技術スタック選定**:
- データベース選定（PostgreSQL vs MySQL vs MongoDB）
- フレームワーク選定（Spring Boot vs Node.js vs Django）
- クラウドプロバイダー選定（AWS vs GCP vs Azure）

✅ **アーキテクチャパターン**:
- マイクロサービス vs モノリス
- Event-Driven vs Request-Response
- CQRS パターン採用

✅ **セキュリティ・認証**:
- 認証方式（OAuth 2.0 vs SAML vs JWT）
- 暗号化方式

✅ **インフラ・デプロイメント**:
- コンテナオーケストレーション（Kubernetes vs ECS）
- CI/CDツール選定

#### ADRが不要な判断（記録しない）

❌ **日常的な開発判断**:
- 変数名の命名
- コードのリファクタリング
- ログレベルの調整

❌ **局所的な最適化**:
- 特定関数のアルゴリズム選択
- UIコンポーネントの配置

❌ **一時的な対応**:
- 緊急バグ修正の実装方法

#### 判断の目安

**「半年後の自分や他のメンバーがこの判断の理由を知りたいか？」**

YES → ADRを書く  
NO → 通常のコミットメッセージで十分

---

### Q4: 図表は必須ですか？

**A**: アーキテクチャ図とER図は**必須**、その他は**推奨**です。

#### 必須の図表（🔴 重要度A）

| 図表 | 格納先 | ツール | 理由 |
|-----|-------|-------|------|
| **システムアーキテクチャ図** | `architecture/diagrams/system-context.puml` | PlantUML | システム全体像の理解 |
| **ER図** | `data-model/diagrams/erd.puml` | PlantUML | データ構造の理解 |

#### 推奨の図表（🟡 重要度B）

| 図表 | 格納先 | 使用ケース |
|-----|-------|----------|
| **コンポーネント図** | `architecture/diagrams/component-diagram.puml` | マイクロサービス、複雑なシステム |
| **シーケンス図** | `architecture/diagrams/sequence-*.puml` | 複雑な処理フロー |
| **デプロイメント図** | `infrastructure/diagrams/deployment-diagram.puml` | クラウド構成、Kubernetes |

#### ツール推奨

| ツール | 用途 | メリット | デメリット |
|-------|------|---------|----------|
| **PlantUML** | すべての図 | コードベース、Git管理、自動生成 | 学習コスト |
| **Mermaid** | Markdown埋め込み | GitHub表示、簡易 | 表現力限定 |
| **Draw.io** | 汎用 | 直感的、豊富な図形 | バイナリ形式 |
| **Lucidchart** | プレゼン向け | 美しい、共同編集 | 有料、Git管理困難 |

**推奨**: PlantUML（コードベース、バージョン管理に最適）

---

### Q5: ドキュメントの更新タイミングは？

**A**: **設計変更時**と**実装完了時**です。

| タイミング | 対象ドキュメント | 必須/推奨 | 所要時間 |
|-----------|----------------|---------|---------|
| **設計判断時** | 該当する設計書、ADR | 必須 | 1-2時間 |
| **API変更時** | API仕様書、関連ADR | 必須 | 30分-1時間 |
| **スキーマ変更時** | データモデル設計書、ER図 | 必須 | 1時間 |
| **アーキテクチャ変更時** | アーキテクチャ設計書、ADR | 必須 | 2-4時間 |
| **実装完了時** | As-Built、教訓 | 必須 | 1-2時間 |
| **技術負債発生時** | 技術負債リスト | 推奨 | 15分 |
| **改善提案時** | 設計改善提案書 | 推奨 | 30分 |

#### ベストプラクティス

✅ **DO（実施すべき）**:
- 設計判断と同時にドキュメント更新
- ADRは判断直後に記録（記憶が新鮮なうちに）
- 実装完了時に As-Built 作成
- PR に設計ドキュメント更新を含める

❌ **DON'T（避けるべき）**:
- 「後でまとめて更新」（記憶が薄れる）
- 実装とドキュメントの乖離放置
- 口頭での設計判断（記録なし）
- As-Built の省略

---

### Q6: 専用リポジトリのメリットは？

**A**: **設計の独立管理**と**明確な責務分離**です。

#### メリット

| 観点 | メリット |
|-----|---------|
| **独立性** | コードと設計を独立管理、異なるライフサイクル |
| **アクセス制御** | アーキテクトと開発者で異なる権限設定可能 |
| **レビュープロセス** | 設計レビューとコードレビューを分離 |
| **サイズ管理** | コードリポジトリの肥大化防止 |
| **明確な責務** | 「設計」と「実装」の役割が明確 |
| **再利用性** | 複数の実装リポジトリから参照可能 |

#### デメリットと対応

| デメリット | 対応策 |
|----------|--------|
| **同期の手間** | Git Submodule または参照リンク |
| **2つのリポジトリ管理** | CI/CD で自動同期 |
| **初期セットアップコスト** | テンプレートリポジトリ用意 |

#### 使い分け

| プロジェクト規模 | 推奨アプローチ |
|---------------|-------------|
| **小規模（1-3人）** | コードリポジトリ内 `docs/design/` |
| **中規模（4-10人）** | 専用リポジトリ（本ガイド推奨） |
| **大規模（10人以上）** | 専用リポジトリ + Confluence等 |

---

## 📞 サポート

### 問い合わせ先
- **ドキュメント構造**: Engineering Architecture Team
- **Phase 2 ガイド**: Engineering Leadership Team
- **ADR管理**: Architecture Review Committee

### Slack チャンネル
- `#dev-standards` - 標準に関する質問
- `#architecture` - アーキテクチャ設計の相談
- `#design-review` - 設計レビュー依頼

---

**最終更新**: 2025-11-12  
**バージョン**: 2.0.0  
**フィードバック歓迎**: 改善提案をお待ちしています

---

## 📝 バージョン履歴

| バージョン | 日付 | 変更内容 |
|-----------|------|---------|
| 1.0.0 | 2025-11-12 | 初版リリース（Phase別分類） |
| 2.0.0 | 2025-11-12 | **メジャー変更**: 専用リポジトリ想定、成果物種類ベースの分類に変更 |

---

**このガイドは living document です。プロジェクトの実践から得られた知見で継続的に改善されます。**
