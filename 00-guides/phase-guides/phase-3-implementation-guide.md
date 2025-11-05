---
title: "Phase 3 Implementation Guide"
version: "2.0.0"
created_date: "2025-11-05"
last_updated: "2025-11-05"
status: "Active"
phase: "Phase 3 - Implementation"
owner: "Engineering Team"
document_type: "Lightweight Navigation Guide"
---

# Phase 3: 実装ガイド

## 本ガイドの目的

本ガイドは、AIが実装タスクを実行する際に、devin-organization-standards内のどのドキュメントを参照すべきかを明確にすることを目的としています。

**重要な原則**:
- 本ガイドは参照先ドキュメントへの案内役です
- **各ドキュメントの記載内容が最優先であり、必ずその内容に従ってください**
- 本ガイドに記載されていない詳細や具体的な実装方法は、参照先ドキュメントの記載に従ってください
- ドキュメント間で矛盾がある場合は、より具体的なドキュメントの記載を優先してください

---

## 参照ドキュメントの重要度

- **🔴 必須**: タスク実行前に必ず参照し、記載内容を遵守してください
- **🟡 推奨**: タスクの品質向上のために参照を推奨します
- **⚪ 参考**: 必要に応じて参照してください

---

## 1. タスク粒度別ガイド

実装タスクの規模に応じて参照すべきドキュメントが異なります。

### レベル0: 機能全体の実装（複数日〜数週間）

**タスク例**: ユーザー管理機能全体、注文処理システム、レポート生成機能

**参照ドキュメント**:

#### 🔴 必須参照
1. **アーキテクチャ設計書** (`02-architecture-standards/`)
   - `layered-architecture.md` - システム全体の層構造設計
   - `clean-architecture.md` - 依存関係の方向性と境界
   - `domain-driven-design.md` - ドメインモデルの設計原則
   - **参照目的**: 機能全体のアーキテクチャ設計を理解し、記載内容に従って実装してください

2. **データベース設計** (`02-architecture-standards/database/`)
   - `schema-design-principles.md` - テーブル設計の原則
   - `migration-strategy.md` - マイグレーション管理
   - **参照目的**: データモデル設計の方針を理解し、記載内容に従って実装してください

3. **API設計** (`02-architecture-standards/api/`)
   - `rest-api-design.md` - RESTful API設計原則
   - `graphql-design.md` (該当する場合)
   - **参照目的**: API設計の方針を理解し、記載内容に従って実装してください

#### 🟡 推奨参照
4. **セキュリティ標準** (`06-security-standards/`)
   - `authentication-standards.md`
   - `authorization-patterns.md`
   - **参照目的**: セキュリティ要件を理解し、記載内容に従って実装してください

5. **テスト戦略** (`04-quality-standards/testing/`)
   - `testing-strategy.md` - テスト全体方針
   - **参照目的**: テスト計画を策定する際、記載内容に従ってください

#### ⚪ 参考
6. **ドメイン固有のガイドライン** (`03-domain-guidelines/`)
   - 該当するドメインのREADME.md
   - **参照目的**: ドメイン特有の要件を確認し、記載内容に従って実装してください

---

### レベル1: コンポーネント/モジュールの実装（1日〜数日）

**タスク例**: UserAPI、OrderService、ReportGenerator、PaymentGateway

**参照ドキュメント**:

#### 🔴 必須参照
1. **コーディング規約** (`01-coding-standards/`)
   - 使用言語の規約ファイル（例: `typescript.md`, `python.md`）
   - `naming-conventions.md` - 命名規則
   - **参照目的**: コード記述の基本ルールを理解し、記載内容に従って実装してください

2. **アーキテクチャパターン** (`02-architecture-standards/`)
   - `layered-architecture.md` - レイヤー間の依存関係
   - 該当するレイヤーのパターン（例: `repository-pattern.md`, `service-layer-pattern.md`）
   - **参照目的**: コンポーネントの配置と責務を理解し、記載内容に従って実装してください

3. **エラーハンドリング** (`05-operational-standards/logging/`)
   - `error-handling-patterns.md`
   - **参照目的**: エラー処理の実装方法を理解し、記載内容に従って実装してください

#### 🟡 推奨参照
4. **テスト手法** (`04-quality-standards/testing/`)
   - `unit-testing-guide.md`
   - `integration-testing-guide.md`
   - **参照目的**: テストコードの実装方法を理解し、記載内容に従って実装してください

5. **ドキュメント作成** (`07-documentation-standards/code-documentation/`)
   - `inline-documentation.md`
   - `api-documentation.md`
   - **参照目的**: ドキュメントの記述方法を理解し、記載内容に従って作成してください

#### ⚪ 参考
6. **パフォーマンス最適化** (`05-operational-standards/performance/`)
   - `caching-strategy.md` (該当する場合)
   - **参照目的**: パフォーマンス要件を確認し、記載内容に従って実装してください

---

### レベル2: クラス/モジュールの実装（数時間〜1日）

**タスク例**: UserService、OrderRepository、ValidationMiddleware、EmailNotifier

**参照ドキュメント**:

#### 🔴 必須参照
1. **コーディング規約** (`01-coding-standards/`)
   - 使用言語の規約ファイル
   - `naming-conventions.md`
   - `code-formatting.md`
   - **参照目的**: コードスタイルの詳細を理解し、記載内容に従って実装してください

2. **設計原則** (`02-architecture-standards/`)
   - `solid-principles.md` - SOLID原則の適用
   - **参照目的**: クラス設計の原則を理解し、記載内容に従って実装してください

3. **依存性注入** (`02-architecture-standards/`)
   - `dependency-injection.md`
   - **参照目的**: 依存関係の管理方法を理解し、記載内容に従って実装してください

#### 🟡 推奨参照
4. **テスト** (`04-quality-standards/testing/`)
   - `unit-testing-guide.md`
   - `test-doubles.md` (モックやスタブが必要な場合)
   - **参照目的**: ユニットテストの実装方法を理解し、記載内容に従って実装してください

5. **ログ出力** (`05-operational-standards/logging/`)
   - `logging-standards.md`
   - **参照目的**: ログ出力の実装方法を理解し、記載内容に従って実装してください

#### ⚪ 参考
6. **コードレビュー基準** (`04-quality-standards/code-review/`)
   - `review-checklist.md`
   - **参照目的**: レビュー観点を事前確認し、記載内容を意識して実装してください

---

### レベル3: メソッド/関数の実装（30分〜数時間）

**タスク例**: createUser()、calculateTotal()、validateEmail()、formatDate()

**参照ドキュメント**:

#### 🔴 必須参照
1. **コーディング規約** (`01-coding-standards/`)
   - 使用言語の規約ファイル
   - `naming-conventions.md`
   - **参照目的**: 関数命名とコードスタイルを理解し、記載内容に従って実装してください

2. **コメント規約** (`07-documentation-standards/code-documentation/`)
   - `inline-documentation.md`
   - **参照目的**: コメントの記述方法を理解し、記載内容に従って実装してください

#### 🟡 推奨参照
3. **エラーハンドリング** (`05-operational-standards/logging/`)
   - `error-handling-patterns.md`
   - **参照目的**: エラー処理の実装パターンを理解し、記載内容に従って実装してください

4. **ユニットテスト** (`04-quality-standards/testing/`)
   - `unit-testing-guide.md`
   - **参照目的**: テストケースの設計方法を理解し、記載内容に従って実装してください

#### ⚪ 参考
5. **パフォーマンス** (`05-operational-standards/performance/`)
   - 該当する最適化ガイド
   - **参照目的**: パフォーマンス考慮点を確認し、記載内容に従って実装してください

---

### レベル4: ロジック単位の実装（数分〜30分）

**タスク例**: バリデーションロジック、条件分岐、ループ処理、データ変換

**参照ドキュメント**:

#### 🔴 必須参照
1. **コーディング規約** (`01-coding-standards/`)
   - 使用言語の規約ファイル（特に該当する言語機能のセクション）
   - **参照目的**: 言語固有の記述方法を理解し、記載内容に従って実装してください

#### 🟡 推奨参照
2. **コードの可読性** (`01-coding-standards/`)
   - `code-formatting.md`
   - **参照目的**: 可読性の高いコードを記述するため、記載内容に従って実装してください

#### ⚪ 参考
3. **セキュリティ** (`06-security-standards/`)
   - 該当するセキュリティガイド（入力検証、SQLインジェクション対策等）
   - **参照目的**: セキュリティ上の注意点を確認し、記載内容に従って実装してください

---

## 2. タスクタイプ別ガイド

実装タスクの種類に応じた参照ドキュメントを示します。

### 新規実装（New Feature Implementation）

**参照ドキュメント**:

#### 🔴 必須参照
- **アーキテクチャ標準** (`02-architecture-standards/`)
  - タスク粒度に応じたアーキテクチャドキュメント
  - **これらのドキュメントの記載内容に従って設計・実装してください**

- **コーディング規約** (`01-coding-standards/`)
  - 使用言語の規約
  - **これらのドキュメントの記載内容に従ってコードを記述してください**

#### 🟡 推奨参照
- **テスト標準** (`04-quality-standards/testing/`)
  - 新規機能のテスト戦略
  - **これらのドキュメントの記載内容に従ってテストを実装してください**

- **ドキュメント作成** (`07-documentation-standards/`)
  - 新規機能のドキュメント化
  - **これらのドキュメントの記載内容に従ってドキュメントを作成してください**

---

### リファクタリング（Refactoring）

**参照ドキュメント**:

#### 🔴 必須参照
- **設計原則** (`02-architecture-standards/`)
  - `solid-principles.md`
  - `design-patterns.md`
  - **これらのドキュメントの記載内容に従ってリファクタリングしてください**

- **コーディング規約** (`01-coding-standards/`)
  - 使用言語の規約
  - **これらのドキュメントの記載内容に従ってコードを改善してください**

#### 🟡 推奨参照
- **テスト** (`04-quality-standards/testing/`)
  - `regression-testing.md`
  - **これらのドキュメントの記載内容に従って既存動作の検証を行ってください**

- **コードレビュー** (`04-quality-standards/code-review/`)
  - `review-checklist.md`
  - **これらのドキュメントの記載内容を参照して品質を確認してください**

---

### バグ修正（Bug Fix）

**参照ドキュメント**:

#### 🔴 必須参照
- **エラーハンドリング** (`05-operational-standards/logging/`)
  - `error-handling-patterns.md`
  - **このドキュメントの記載内容に従って適切なエラー処理を実装してください**

- **テスト** (`04-quality-standards/testing/`)
  - `unit-testing-guide.md`
  - `regression-testing.md`
  - **これらのドキュメントの記載内容に従って再発防止のテストを追加してください**

#### 🟡 推奨参照
- **ログ出力** (`05-operational-standards/logging/`)
  - `logging-standards.md`
  - **このドキュメントの記載内容に従って適切なログを追加してください**

- **デバッグ手法** (`04-quality-standards/`)
  - 該当するデバッグガイド
  - **このドキュメントの記載内容を参照してデバッグを行ってください**

---

### パフォーマンス改善（Performance Improvement）

**参照ドキュメント**:

#### 🔴 必須参照
- **パフォーマンス標準** (`05-operational-standards/performance/`)
  - `performance-optimization.md`
  - `caching-strategy.md`
  - **これらのドキュメントの記載内容に従って最適化を実装してください**

#### 🟡 推奨参照
- **データベース最適化** (`02-architecture-standards/database/`)
  - `query-optimization.md`
  - `indexing-strategy.md`
  - **これらのドキュメントの記載内容に従ってクエリを最適化してください**

- **モニタリング** (`05-operational-standards/monitoring/`)
  - `performance-monitoring.md`
  - **このドキュメントの記載内容に従って計測可能な形で実装してください**

---

### セキュリティ強化（Security Enhancement）

**参照ドキュメント**:

#### 🔴 必須参照
- **セキュリティ標準** (`06-security-standards/`)
  - タスクに応じた具体的なセキュリティガイド
  - `authentication-standards.md`
  - `authorization-patterns.md`
  - `input-validation.md`
  - **これらのドキュメントの記載内容に従ってセキュリティ対策を実装してください**

#### 🟡 推奨参照
- **セキュリティテスト** (`04-quality-standards/testing/`)
  - `security-testing.md`
  - **このドキュメントの記載内容に従ってセキュリティテストを実施してください**

- **監査ログ** (`05-operational-standards/logging/`)
  - `audit-logging.md`
  - **このドキュメントの記載内容に従って監査ログを実装してください**

---

## 3. アーキテクチャレイヤー別ガイド

実装対象のレイヤーに応じた参照ドキュメントを示します。

### データモデル層（Domain/Entity Layer）

**参照ドキュメント**:

#### 🔴 必須参照
- `02-architecture-standards/domain-driven-design.md`
  - **このドキュメントの記載内容に従ってドメインモデルを設計してください**
- `02-architecture-standards/database/schema-design-principles.md`
  - **このドキュメントの記載内容に従ってスキーマを設計してください**

#### 🟡 推奨参照
- `01-coding-standards/naming-conventions.md`
  - **このドキュメントの記載内容に従ってエンティティやフィールドに命名してください**
- `02-architecture-standards/database/migration-strategy.md`
  - **このドキュメントの記載内容に従ってマイグレーションを管理してください**

---

### リポジトリ層（Data Access Layer）

**参照ドキュメント**:

#### 🔴 必須参照
- `02-architecture-standards/repository-pattern.md`
  - **このドキュメントの記載内容に従ってリポジトリを実装してください**
- `02-architecture-standards/database/query-optimization.md`
  - **このドキュメントの記載内容に従ってクエリを最適化してください**

#### 🟡 推奨参照
- `05-operational-standards/logging/logging-standards.md`
  - **このドキュメントの記載内容に従ってクエリログを出力してください**
- `05-operational-standards/performance/caching-strategy.md`
  - **このドキュメントの記載内容に従ってキャッシュを実装してください**

---

### サービス層（Business Logic Layer）

**参照ドキュメント**:

#### 🔴 必須参照
- `02-architecture-standards/service-layer-pattern.md`
  - **このドキュメントの記載内容に従ってサービス層を実装してください**
- `02-architecture-standards/solid-principles.md`
  - **このドキュメントの記載内容に従って設計してください**

#### 🟡 推奨参照
- `02-architecture-standards/dependency-injection.md`
  - **このドキュメントの記載内容に従って依存関係を管理してください**
- `05-operational-standards/logging/error-handling-patterns.md`
  - **このドキュメントの記載内容に従ってビジネスロジックのエラーを処理してください**

---

### コントローラー層（API/Presentation Layer）

**参照ドキュメント**:

#### 🔴 必須参照
- `02-architecture-standards/api/rest-api-design.md`
  - **このドキュメントの記載内容に従ってAPIエンドポイントを設計してください**
- `06-security-standards/input-validation.md`
  - **このドキュメントの記載内容に従って入力検証を実装してください**

#### 🟡 推奨参照
- `06-security-standards/authentication-standards.md`
  - **このドキュメントの記載内容に従って認証を実装してください**
- `07-documentation-standards/api-documentation.md`
  - **このドキュメントの記載内容に従ってAPIドキュメントを作成してください**

---

### プレゼンテーション層（UI Layer）

**参照ドキュメント**:

#### 🔴 必須参照
- `01-coding-standards/` 内の該当するフロントエンド言語/フレームワーク規約
  - **このドキュメントの記載内容に従ってUIコンポーネントを実装してください**

#### 🟡 推奨参照
- `04-quality-standards/testing/` 内のフロントエンドテストガイド
  - **このドキュメントの記載内容に従ってUIテストを実装してください**
- `07-documentation-standards/` 内のUI関連ドキュメント
  - **このドキュメントの記載内容に従ってUIドキュメントを作成してください**

---

## 4. CRUD操作別ガイド

データ操作の種類に応じた参照ドキュメントを示します。

### Create（作成）

**参照ドキュメント**:

#### 🔴 必須参照
- `06-security-standards/input-validation.md`
  - **このドキュメントの記載内容に従って入力データを検証してください**
- `02-architecture-standards/database/transaction-management.md`
  - **このドキュメントの記載内容に従ってトランザクションを管理してください**

#### 🟡 推奨参照
- `05-operational-standards/logging/audit-logging.md`
  - **このドキュメントの記載内容に従って作成操作を記録してください**

---

### Read（読み取り）

**参照ドキュメント**:

#### 🔴 必須参照
- `02-architecture-standards/database/query-optimization.md`
  - **このドキュメントの記載内容に従ってクエリを最適化してください**
- `06-security-standards/authorization-patterns.md`
  - **このドキュメントの記載内容に従ってアクセス権限を確認してください**

#### 🟡 推奨参照
- `05-operational-standards/performance/caching-strategy.md`
  - **このドキュメントの記載内容に従ってキャッシュを活用してください**

---

### Update（更新）

**参照ドキュメント**:

#### 🔴 必須参照
- `06-security-standards/input-validation.md`
  - **このドキュメントの記載内容に従って更新データを検証してください**
- `02-architecture-standards/database/transaction-management.md`
  - **このドキュメントの記載内容に従ってトランザクションを管理してください**
- `06-security-standards/authorization-patterns.md`
  - **このドキュメントの記載内容に従って更新権限を確認してください**

#### 🟡 推奨参照
- `05-operational-standards/logging/audit-logging.md`
  - **このドキュメントの記載内容に従って更新操作を記録してください**
- `02-architecture-standards/database/concurrency-control.md`
  - **このドキュメントの記載内容に従って同時更新を制御してください**

---

### Delete（削除）

**参照ドキュメント**:

#### 🔴 必須参照
- `06-security-standards/authorization-patterns.md`
  - **このドキュメントの記載内容に従って削除権限を確認してください**
- `02-architecture-standards/database/soft-delete-strategy.md`（該当する場合）
  - **このドキュメントの記載内容に従って論理削除を実装してください**

#### 🟡 推奨参照
- `05-operational-standards/logging/audit-logging.md`
  - **このドキュメントの記載内容に従って削除操作を記録してください**
- `02-architecture-standards/database/cascade-delete-policy.md`
  - **このドキュメントの記載内容に従って関連データの削除を管理してください**

---

## 5. 技術的タスク別ガイド

特定の技術的タスクに応じた参照ドキュメントを示します。

### 認証・認可の実装

**参照ドキュメント**:

#### 🔴 必須参照
- `06-security-standards/authentication-standards.md`
  - **このドキュメントの記載内容に従って認証を実装してください**
- `06-security-standards/authorization-patterns.md`
  - **このドキュメントの記載内容に従って認可を実装してください**

#### 🟡 推奨参照
- `06-security-standards/session-management.md`
  - **このドキュメントの記載内容に従ってセッションを管理してください**
- `05-operational-standards/logging/audit-logging.md`
  - **このドキュメントの記載内容に従って認証・認可イベントを記録してください**

---

### バリデーションの実装

**参照ドキュメント**:

#### 🔴 必須参照
- `06-security-standards/input-validation.md`
  - **このドキュメントの記載内容に従って入力検証を実装してください**

#### 🟡 推奨参照
- `05-operational-standards/logging/error-handling-patterns.md`
  - **このドキュメントの記載内容に従ってバリデーションエラーを処理してください**

---

### エラーハンドリングの実装

**参照ドキュメント**:

#### 🔴 必須参照
- `05-operational-standards/logging/error-handling-patterns.md`
  - **このドキュメントの記載内容に従ってエラーを処理してください**
- `05-operational-standards/logging/logging-standards.md`
  - **このドキュメントの記載内容に従ってエラーログを出力してください**

#### 🟡 推奨参照
- `02-architecture-standards/api/error-response-format.md`
  - **このドキュメントの記載内容に従ってエラーレスポンスを返してください**

---

### ログ出力の実装

**参照ドキュメント**:

#### 🔴 必須参照
- `05-operational-standards/logging/logging-standards.md`
  - **このドキュメントの記載内容に従ってログを出力してください**

#### 🟡 推奨参照
- `05-operational-standards/logging/audit-logging.md`（監査ログの場合）
  - **このドキュメントの記載内容に従って監査ログを出力してください**
- `06-security-standards/sensitive-data-handling.md`
  - **このドキュメントの記載内容に従って機密情報のログ出力を制御してください**

---

### キャッシュの実装

**参照ドキュメント**:

#### 🔴 必須参照
- `05-operational-standards/performance/caching-strategy.md`
  - **このドキュメントの記載内容に従ってキャッシュを実装してください**

#### 🟡 推奨参照
- `02-architecture-standards/database/query-optimization.md`
  - **このドキュメントの記載内容を参照してキャッシュ対象を選定してください**
- `05-operational-standards/performance/cache-invalidation.md`
  - **このドキュメントの記載内容に従ってキャッシュ無効化を実装してください**

---

## 6. 参照ドキュメント優先度マトリクス

タスク粒度とドキュメントカテゴリの優先度を一覧化します。

| ドキュメントカテゴリ | レベル0 | レベル1 | レベル2 | レベル3 | レベル4 |
|----------------------|---------|---------|---------|---------|---------|
| **コーディング規約** (`01-coding-standards/`) | 🟡 推奨 | 🔴 必須 | 🔴 必須 | 🔴 必須 | 🔴 必須 |
| **アーキテクチャ標準** (`02-architecture-standards/`) | 🔴 必須 | 🔴 必須 | 🔴 必須 | 🟡 推奨 | ⚪ 参考 |
| **ドメインガイドライン** (`03-domain-guidelines/`) | 🟡 推奨 | 🟡 推奨 | ⚪ 参考 | ⚪ 参考 | ⚪ 参考 |
| **品質標準** (`04-quality-standards/`) | 🟡 推奨 | 🟡 推奨 | 🟡 推奨 | 🟡 推奨 | ⚪ 参考 |
| **運用標準** (`05-operational-standards/`) | 🟡 推奨 | 🔴 必須 | 🟡 推奨 | 🟡 推奨 | ⚪ 参考 |
| **セキュリティ標準** (`06-security-standards/`) | 🔴 必須 | 🟡 推奨 | 🟡 推奨 | ⚪ 参考 | ⚪ 参考 |
| **ドキュメント標準** (`07-documentation-standards/`) | 🟡 推奨 | 🟡 推奨 | 🟡 推奨 | 🔴 必須 | ⚪ 参考 |

**注意**: 各ドキュメントの記載内容が最優先です。本マトリクスは参照の目安であり、実際の実装は各ドキュメントの記載内容に従ってください。

---

## 7. 実践的な活用方法

### 基本的なワークフロー

1. **タスクの分類**
   - タスクの粒度（レベル0〜4）を特定
   - タスクのタイプ（新規実装、リファクタリング等）を特定
   - 対象レイヤー（データモデル層、サービス層等）を特定

2. **ドキュメントの参照**
   - 本ガイドで🔴必須参照のドキュメントを最初に確認
   - **各ドキュメントの記載内容を理解し、その内容に従って実装を進める**
   - 必要に応じて🟡推奨参照、⚪参考のドキュメントも確認

3. **実装の実施**
   - **参照したドキュメントの記載内容に従って実装**
   - ドキュメントに記載されていない詳細については、より具体的なドキュメントを追加で参照
   - 疑問点がある場合は、関連する複数のドキュメントを横断的に確認

4. **品質の確認**
   - 参照したドキュメントの要件を満たしているか確認
   - 必要に応じてコードレビューチェックリストを参照
   - **ドキュメントの記載内容と実装が一致していることを確認**

---

### 複数ドキュメントの参照順序

複数のドキュメントを参照する場合、以下の順序を推奨します：

1. **アーキテクチャレベル** - 全体構造の理解
2. **セキュリティ要件** - 必須の制約事項
3. **コーディング規約** - 具体的な記述方法
4. **品質基準** - テストとレビュー観点
5. **運用標準** - ログやモニタリング

**重要**: 各段階で該当するドキュメントの記載内容を理解し、その内容に従って実装してください。

---

### ドキュメント間の優先順位

ドキュメント間で矛盾や疑問が生じた場合：

1. **より具体的なドキュメントを優先** - 抽象的な原則より具体的な実装ガイドを優先
2. **より新しいドキュメントを優先** - 古いドキュメントより更新日が新しいものを優先
3. **ドメイン固有のドキュメントを優先** - 一般的なガイドラインより、ドメイン固有の要件を優先

**判断に迷う場合**: 該当するREADME.mdファイルを参照し、ドキュメントの位置づけや関連性を確認してください。

---

## 8. ガイドの更新

本ガイドは、devin-organization-standards内のドキュメント構成の変更に応じて定期的に更新されます。

- **最終更新日**: 2025-11-05
- **バージョン**: 2.0.0（軽量ナビゲーション型）
- **対象**: devin-organization-standards 全カテゴリ

ドキュメント構成に変更があった場合や、本ガイドの改善提案がある場合は、適切なチャネルを通じてフィードバックをお願いします。

---

## まとめ

本ガイドは、実装タスクを効率的に進めるための「地図」です。

- **本ガイドの役割**: 参照すべきドキュメントへの案内
- **各ドキュメントの役割**: 実装の具体的な方法と要件の定義

**最も重要な原則**: 
各ドキュメントの記載内容が最優先であり、本ガイドはあくまで参照先への案内です。実装時は必ず該当するドキュメントの記載内容を確認し、その内容に従ってください。

疑問や矛盾がある場合は、関連するドキュメントを複数参照し、より具体的で新しいドキュメントの記載を優先してください。
