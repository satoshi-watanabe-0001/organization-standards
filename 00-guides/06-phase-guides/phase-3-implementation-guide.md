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

## ⚠️ 禁止事項チェック

### このフェーズ特有の禁止事項
- **設計無視**: API設計書やアーキテクチャ設計を無視して独自実装してはいけない
- **テスト省略**: 実装コードに対応するテストコードを書かずに進めてはいけない
- **コーディング規約違反**: 組織のコーディングスタイルを無視してはいけない
- **セキュリティ考慮不足**: 入力検証、認証・認可、暗号化を省略してはいけない
- **本番でのデバッグ**: 本番環境でconsole.logやprintを使ってデバッグしてはいけない

### 全フェーズ共通禁止事項
- **CI/CD設定の無断変更**: 明示的な指示がない限り、GitHub Actions、CircleCI、Jenkins等のCI/CD設定ファイルを変更してはいけない
- **本番環境への直接変更**: 本番データベース、本番サーバー、本番設定ファイルを直接変更してはいけない
- **セキュリティ設定の独断変更**: 認証・認可・暗号化等のセキュリティ設定を独自判断で変更してはいけない
- **組織標準外技術の無断導入**: プロジェクトで使用していない新しいライブラリ・フレームワーク・言語を独断で導入してはいけない
- **プロジェクト構造の大幅変更**: ディレクトリ構造、命名規則、アーキテクチャパターンを独断で大きく変更してはいけない

### 📚 詳細確認
禁止事項の詳細、具体例、例外ケースについては以下のドキュメントを参照してください：
- [AI開発タスクの禁止事項](../../10-governance/ai-task-prohibitions.md)
- [禁止事項チェックリスト](../../10-governance/ai-task-prohibitions-checklist.md)

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


## 3.8 データベースマイグレーション実装（Flyway/Liquibase）

### 目的

データベーススキーマ変更を安全・確実に管理し、組織標準に準拠したドキュメント化されたマイグレーションを作成する。

### 前提条件

- [ ] Phase 2でデータベース設計が完了している
- [ ] マイグレーションツール（Flyway/Liquibase）が設定済み
- [ ] 組織標準`01-coding-standards/sql-standards.md`を確認済み

---

## ステップ3.8.1: マイグレーションファイル作成準備

### チェックポイント

- [ ] マイグレーション命名規則を確認
- [ ] バージョン番号の採番ルールを確認
- [ ] テンプレートを準備

### Flywayファイル命名規則

```
V<バージョン>__<説明>.sql

例:
V1__Create_users_table.sql
V2__Add_user_profiles_table.sql
V3__Create_password_reset_tokens_table.sql
V4__Add_user_indexes.sql
```

**命名ルール**:
- `V`: バージョン管理マイグレーション（必須）
- バージョン番号: 整数、ドット区切り可能（例: `V1`, `V1.1`, `V2.0.1`）
- `__`: アンダースコア2つ（区切り文字）
- 説明: スネークケース、英語、動詞で開始

---

## ステップ3.8.2: SQLファイル作成（テンプレート使用）

### 🎯 重要: 必須コメント規約

**組織標準準拠のため、以下のコメントを必ず記載**:

#### 1. ファイル冒頭コメント（必須）

```sql
/*
 * =============================================================================
 * Migration: V<N> - <マイグレーション名>
 * =============================================================================
 * 
 * 【目的】
 * - マイグレーションの目的を明確に記述
 * - 何を実現するためのマイグレーションか
 * 
 * 【ビジネス背景】
 * - チケット番号: XX-123
 * - ビジネス要件の概要
 * - なぜこの変更が必要か
 * 
 * 【主な設計判断】
 * 1. データ型選択: なぜこのデータ型を選んだか
 * 2. 制約設計: なぜこの制約が必要か
 * 3. インデックス設計: なぜこのインデックスが必要か
 * 4. 正規化判断: 正規化レベルの選択理由
 * 
 * 【想定クエリパターン】（必須）
 * 1. 最頻出クエリ:
 *    SELECT ... WHERE ... ORDER BY ...;
 *    実行頻度: 毎秒1000リクエスト
 * 
 * 2. バッチ処理クエリ:
 *    UPDATE ... WHERE ...;
 *    実行頻度: 日次1回
 * 
 * 3. 分析クエリ:
 *    SELECT ... GROUP BY ... HAVING ...;
 *    実行頻度: 週次レポート
 * 
 * 【インデックス方針】（必須）
 * - インデックス1: <目的>（<想定クエリ>に対応）
 * - インデックス2: <目的>（<想定クエリ>に対応）
 * - 複合インデックス: <カラムの組み合わせ理由>
 * 
 * 【パフォーマンス考慮】
 * - 想定レコード数: 100万件/年
 * - 成長率: 年20%増
 * - インデックスサイズ: 推定50MB
 * 
 * 【セキュリティ考慮事項】（該当する場合）
 * - 個人情報カラム: <カラム名>（暗号化・マスキング方針）
 * - アクセス制御: <権限設定>
 * - 監査ログ: <記録内容>
 * 
 * 【運用・保守】
 * - 定期クリーンアップ: <頻度・対象データ>
 * - モニタリング: <監視項目>
 * - バックアップ: <バックアップ戦略>
 * 
 * 【参照ドキュメント】
 * - 設計書: docs/design/<関連ドキュメント>
 * - API仕様: docs/api/<関連ドキュメント>
 * 
 * 作成日: YYYY-MM-DD
 * 作成者: <チーム名/担当者>
 * レビュー: <レビュアー>
 * =============================================================================
 */
```

#### 2. インデックスコメント（必須）

**各`CREATE INDEX`の前に必ずインラインコメントを記載**:

```sql
-- -----------------------------------------------------------------------------
-- インデックスN: <一言で目的>
-- -----------------------------------------------------------------------------
-- 【目的】
-- - このインデックスの目的
-- 
-- 【想定クエリ】
-- SELECT ... WHERE column_name = ?
-- 
-- 【実行頻度】
-- - 毎秒1000リクエスト
-- 
-- 【パフォーマンス効果】
-- - フルスキャン: 500ms
-- - インデックススキャン: 5ms
-- - 改善率: 100倍
-- 
-- 【設計判断】
-- - なぜこのカラムにインデックスを作成するか
-- - なぜこのインデックスタイプ（B-Tree/Hash/GiST）を選んだか
CREATE INDEX idx_<table>_<column> ON <table>(<column>);

COMMENT ON INDEX idx_<table>_<column> IS 
'<簡潔な目的>
目的: <詳細な目的>
想定クエリ: SELECT ... WHERE <column> = ?
カーディナリティ: <高/中/低>
インデックスタイプ: <B-Tree/Hash/GiST/etc>
推定効果: <具体的な効果>
実行頻度: <頻度>
メンテナンス: <保守に関する注意事項>
レビュー日: YYYY-MM-DD';
```

#### 3. 制約コメント（推奨）

```sql
COMMENT ON CONSTRAINT <constraint_name> ON <table_name> IS 
'<制約の目的>
目的: <なぜこの制約が必要か>
動作: <ON DELETE CASCADE等の動作説明>
理由: <ビジネスロジック的な理由>
注意事項: <運用上の注意点>
例: CASCADE動作により大量削除の可能性あり';
```

#### 4. テーブル・カラムコメント（必須）

```sql
COMMENT ON TABLE <table_name> IS 
'<テーブルの目的>
関連チケット: XX-123
主要機能: <主な機能>
想定レコード数: <初期/将来>
成長率: <年間増加率>';

COMMENT ON COLUMN <table_name>.<column_name> IS 
'<カラムの説明>
形式: <データ形式・例>
用途: <使用目的>
制約: <制約事項>
デフォルト値: <デフォルト値とその理由>';
```

---

## ステップ3.8.3: 完全なSQLマイグレーションテンプレート

### テンプレート: 新規テーブル作成

```sql
/*
 * =============================================================================
 * Migration: V<N> - <テーブル名>テーブル作成
 * =============================================================================
 * 
 * 【目的】
 * - <機能>のための<テーブル名>テーブルを新規作成
 * - <実現する機能>
 * 
 * 【ビジネス背景】
 * - チケット: <XX-123>
 * - 要件: <ビジネス要件>
 * 
 * 【主な設計判断】
 * 1. データ型: <選択理由>
 * 2. 制約: <設計理由>
 * 3. インデックス: <方針>
 * 
 * 【想定クエリパターン】
 * 1. 検索: SELECT ... WHERE ... (頻度: 高)
 * 2. 更新: UPDATE ... WHERE ... (頻度: 中)
 * 3. 削除: DELETE ... WHERE ... (頻度: 低)
 * 
 * 【インデックス方針】
 * - idx_xxx: <目的>
 * - idx_yyy: <目的>
 * 
 * 【セキュリティ】
 * - <考慮事項>
 * 
 * 作成日: YYYY-MM-DD
 * 作成者: <担当者>
 * =============================================================================
 */

-- =============================================================================
-- テーブル作成
-- =============================================================================

CREATE TABLE <table_name> (
    -- プライマリキー
    <id_column> UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- ビジネスキー
    <business_key> VARCHAR(255) UNIQUE NOT NULL,
    
    -- データカラム
    <data_column1> VARCHAR(100) NOT NULL,
    <data_column2> INTEGER DEFAULT 0,
    
    -- 外部キー
    <foreign_key> UUID NOT NULL,
    
    -- タイムスタンプ
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    -- 外部キー制約
    CONSTRAINT fk_<table>_<column> 
        FOREIGN KEY (<foreign_key>) 
        REFERENCES <parent_table>(<parent_id>) 
        ON DELETE CASCADE,
    
    -- チェック制約
    CONSTRAINT chk_<name> 
        CHECK (<condition>)
);

-- =============================================================================
-- テーブルコメント
-- =============================================================================

COMMENT ON TABLE <table_name> IS 
'<テーブルの目的>
関連チケット: <XX-123>
想定レコード数: <数>
成長率: <率>';

-- =============================================================================
-- カラムコメント
-- =============================================================================

COMMENT ON COLUMN <table_name>.<id_column> IS 
'プライマリキー（UUID自動生成）
形式: UUID v4
用途: テーブル内部での一意識別子';

COMMENT ON COLUMN <table_name>.<business_key> IS 
'ビジネスキー
形式: <形式>
用途: <用途>
一意性: UNIQUE制約';

-- （全カラムのコメントを記載）

-- =============================================================================
-- インデックス作成
-- =============================================================================

-- -----------------------------------------------------------------------------
-- インデックス1: <目的>
-- -----------------------------------------------------------------------------
-- 目的: <詳細な目的>
-- クエリ: SELECT ... WHERE <column> = ?
-- 頻度: <頻度>
-- 効果: <効果>
CREATE INDEX idx_<table>_<column1> ON <table_name>(<column1>);

COMMENT ON INDEX idx_<table>_<column1> IS 
'<目的>
想定クエリ: WHERE <column1> = ?
インデックスタイプ: B-Tree
推定効果: <効果>
実行頻度: <頻度>';

-- -----------------------------------------------------------------------------
-- インデックス2: <目的>
-- -----------------------------------------------------------------------------
-- （同様に記載）

-- =============================================================================
-- 制約コメント
-- =============================================================================

COMMENT ON CONSTRAINT fk_<table>_<column> ON <table_name> IS 
'外部キー制約
目的: データ整合性維持
削除動作: ON DELETE CASCADE
理由: <ビジネスロジック的理由>
注意: <注意事項>';

COMMENT ON CONSTRAINT chk_<name> ON <table_name> IS 
'チェック制約
目的: <目的>
条件: <condition>
理由: <理由>';
```

---

## ステップ3.8.4: マイグレーション実装チェックリスト

### 作成前チェック

- [ ] `01-coding-standards/sql-standards.md` を確認済み
- [ ] Phase 2設計ドキュメントを確認済み
- [ ] 想定クエリパターンを特定済み
- [ ] インデックス戦略を計画済み

### ファイル冒頭コメント

- [ ] 複数行コメント（`/* ... */`）が存在する
- [ ] 【目的】セクションが記載されている
- [ ] 【ビジネス背景】にチケット番号が記載されている
- [ ] 【主な設計判断】が記載されている
- [ ] 【想定クエリパターン】が3つ以上記載されている
- [ ] 【インデックス方針】が記載されている
- [ ] 【セキュリティ考慮事項】が記載されている（該当する場合）

### テーブル定義

- [ ] SQLキーワードが大文字
- [ ] 識別子が小文字スネークケース
- [ ] 適切にインデントされている
- [ ] 全カラムにNOT NULL/DEFAULT等の制約が明示されている
- [ ] 外部キー制約にON DELETE/ON UPDATE動作が明示されている

### インデックス定義

- [ ] 各`CREATE INDEX`の前にインラインコメントがある
- [ ] インラインコメントに以下が含まれる:
  - [ ] 目的
  - [ ] 想定クエリ
  - [ ] 実行頻度
  - [ ] パフォーマンス効果
  - [ ] 設計判断の理由
- [ ] 各インデックスに`COMMENT ON INDEX`がある
- [ ] `COMMENT ON INDEX`に以下が含まれる:
  - [ ] 簡潔な目的
  - [ ] 想定クエリ（SQL例）
  - [ ] インデックスタイプ
  - [ ] 推定効果
  - [ ] 実行頻度

### 制約コメント

- [ ] 外部キー制約に`COMMENT ON CONSTRAINT`がある（推奨）
- [ ] `ON DELETE CASCADE`の理由が説明されている
- [ ] ビジネスロジック的な意図が記載されている

### テーブル・カラムコメント

- [ ] `COMMENT ON TABLE`がある
- [ ] 関連チケット番号が記載されている
- [ ] 想定レコード数・成長率が記載されている
- [ ] 全カラムに`COMMENT ON COLUMN`がある
- [ ] カラムコメントに形式・用途・制約が記載されている

### テスト

- [ ] ローカル環境でマイグレーション実行確認
- [ ] ロールバック可能性を確認（可能な場合）
- [ ] 想定クエリでパフォーマンステスト実施
- [ ] インデックス効果を`EXPLAIN ANALYZE`で確認

---

## ステップ3.8.5: マイグレーション実行前の確認

### ローカル環境でのテスト

```bash
# Flyway Clean（開発環境のみ）
./mvnw flyway:clean

# Flyway Migrate
./mvnw flyway:migrate

# Flyway Info（適用状況確認）
./mvnw flyway:info

# Flyway Validate（チェックサム検証）
./mvnw flyway:validate
```

### パフォーマンス検証

```sql
-- インデックスなしでの実行計画
EXPLAIN ANALYZE
SELECT ... WHERE ...;

-- インデックスありでの実行計画
EXPLAIN ANALYZE
SELECT ... WHERE ...;

-- インデックス使用状況確認
SELECT 
    schemaname, 
    tablename, 
    indexname, 
    idx_scan, 
    idx_tup_read, 
    idx_tup_fetch
FROM pg_stat_user_indexes
WHERE tablename = '<table_name>';
```

---

## ステップ3.8.6: CI/CD統合

### SQLコメント自動チェック設定

**参照**: `00-guides/SQL-MIGRATION-COMMENT-SOLUTION.md`

CI/CDパイプラインに以下を追加:

1. **GitHub Actions ワークフロー**
   - ファイル: `.github/workflows/sql-comment-check.yml`
   - 機能: SQLファイル変更時に自動コメントチェック

2. **チェック項目**:
   - ファイル冒頭コメントの存在
   - 必須キーワード（目的、チケット、クエリ、インデックス）
   - インデックスコメントの網羅性
   - `COMMENT ON INDEX`の存在
   - `COMMENT ON CONSTRAINT`の推奨

3. **エラー時の動作**:
   - PRにコメント自動投稿
   - マージブロック
   - 修正ガイドのリンク提供

---

## ステップ3.8.7: レビュー観点

### レビュアー向けチェックリスト

**コメント品質**:
- [ ] ファイル冒頭コメントが組織標準に準拠している
- [ ] 想定クエリパターンが具体的に記載されている
- [ ] インデックスの必要性が明確に説明されている
- [ ] ビジネス背景とチケット番号が記載されている

**設計品質**:
- [ ] データ型の選択が適切
- [ ] 制約設計が適切（NOT NULL、UNIQUE、CHECK）
- [ ] 外部キー制約のON DELETE/UPDATE動作が適切
- [ ] インデックス設計が想定クエリに対応している

**パフォーマンス**:
- [ ] 不要なインデックスがない
- [ ] 複合インデックスのカラム順序が適切
- [ ] 部分インデックスの活用を検討している

**セキュリティ**:
- [ ] 個人情報カラムの扱いが適切
- [ ] アクセス制御が考慮されている
- [ ] SQLインジェクション対策がされている

---

## トラブルシューティング

### よくある問題

#### 問題1: コメント不足でCIが失敗

**症状**: PR作成後、CI/CDでSQLコメントチェックエラー

**解決策**:
1. `SQL-MIGRATION-COMMENT-SOLUTION.md`を確認
2. テンプレートを使用して修正
3. チェックリストで再確認

#### 問題2: インデックスの効果が見られない

**症状**: インデックスを作成したが、クエリが遅い

**原因**:
- WHERE句のカラム順序がインデックスと異なる
- カーディナリティが低い
- 統計情報が古い

**解決策**:
```sql
-- 統計情報更新
ANALYZE <table_name>;

-- インデックス使用状況確認
EXPLAIN ANALYZE SELECT ... WHERE ...;

-- インデックス再構築（必要に応じて）
REINDEX INDEX idx_<name>;
```

#### 問題3: マイグレーション失敗（既存データと競合）

**症状**: NOT NULL制約追加時にエラー

**解決策**:
```sql
-- ステップ1: デフォルト値付きでカラム追加
ALTER TABLE <table_name> 
ADD COLUMN <column_name> VARCHAR(100) DEFAULT 'default_value';

-- ステップ2: 既存データを更新
UPDATE <table_name> 
SET <column_name> = <適切な値> 
WHERE <column_name> IS NULL;

-- ステップ3: NOT NULL制約を追加
ALTER TABLE <table_name> 
ALTER COLUMN <column_name> SET NOT NULL;
```

---

## 参考リソース

### 内部ドキュメント

- **SQL標準**: `01-coding-standards/sql-standards.md`
- **解決策ガイド**: `00-guides/SQL-MIGRATION-COMMENT-SOLUTION.md`
- **CI設定**: `00-guides/CI-SETUP-CHECKLIST.md`
- **Phase 2設計**: `phase-guides/phase-2-design-guide.md`

### テンプレート

- **マイグレーションテンプレート**: `08-templates/sql-migration-template.sql`（要作成）
- **コメントテンプレート**: `08-templates/code-templates/sql/`（要作成）

### 外部リソース

- [Flyway公式ドキュメント](https://flywaydb.org/documentation/)
- [PostgreSQL COMMENT構文](https://www.postgresql.org/docs/current/sql-comment.html)
- [PostgreSQL インデックス](https://www.postgresql.org/docs/current/indexes.html)

---

## まとめ

### Phase 3でのSQLマイグレーション実装の要点

1. ✅ **テンプレート使用**: 必ず組織標準テンプレートを使用
2. ✅ **詳細コメント**: Why重視のコメント（目的、理由、効果）
3. ✅ **想定クエリ記載**: 最低3パターンの想定クエリを記載
4. ✅ **インデックス説明**: 各インデックスの必要性を明確に説明
5. ✅ **CI/CD統合**: 自動チェックで品質担保
6. ✅ **レビュー実施**: チェックリストに基づく徹底レビュー

### 次のステップ

- [ ] Phase 4: レビュー・QA（SQLコメント品質確認）
- [ ] CI/CD: 自動チェック結果確認
- [ ] ドキュメント更新: マイグレーション実施記録

---

**統合日**: 2025-11-07  
**対象ドキュメント**: `phase-guides/phase-3-implementation-guide.md`  
**セクション**: 3.8（新規）

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

---

## 9. CI設定の検証 ⭐NEW 必須

### 9.1 概要

**目的**: プロジェクトのCI/CD設定が組織標準に準拠しているか確認し、品質ゲート設定の漏れを防止する

**実施タイミング**:
- 🔴 **Phase 1完了直後**: プロジェクト初期化後の設定確認
- 🟡 **Phase 3実装開始前**: 実装前の再確認
- 🟡 **CI設定変更後**: 設定ファイル更新時

**所要時間**: 15-30分

---

### 9.2 なぜCI設定検証が必要か

**問題事例**:
```
❌ 発生した問題:
- organization-standardsで定義されている必須CI品質ゲートが
  CIワークフローで実行されておらず、品質基準を満たさない
  コードのマージが防止されなかった

具体例（Java プロジェクト）:
- spotlessCheck: コードフォーマット違反検出 → 未設定
- build: コンパイルエラー検出 → 未設定
- jacocoTestCoverageVerification: 80%カバレッジ閾値強制 → 未設定

結果:
- コードフォーマット逸脱が見逃される
- コンパイル不備が見逃される
- カバレッジ閾値違反が見逃される
```

**CI設定検証の効果**:
- ✅ 必須品質ゲートの設定漏れを防止
- ✅ 組織標準への準拠を保証
- ✅ Phase 4レビュー時の手戻りを削減

---

### 9.3 実施手順（3ステップ）

#### **Step 1: CI設定チェックリストを開く** (2分)

```bash
# AI Driveから参照
/devin-organization-standards/00-guides/CI-SETUP-CHECKLIST.md
```

このチェックリストには以下が含まれます:
- 言語別（Java/TypeScript/Python）の必須CI設定
- 設定ファイルごとのチェックポイント
- 検証コマンド
- よくある設定ミスと対策

---

#### **Step 2: 言語別必須設定を確認** (10-15分)

**プロジェクトの言語を特定**:
```bash
# Java プロジェクト
test -f build.gradle && echo "Java (Gradle)"

# TypeScript プロジェクト
test -f package.json && echo "TypeScript/JavaScript"

# Python プロジェクト
test -f pyproject.toml && echo "Python"
```

**該当する言語のセクションを確認**:
- Java: チェックリスト セクション 2.1
- TypeScript: チェックリスト セクション 2.2
- Python: チェックリスト セクション 2.3

**チェック項目（全言語共通）**:
- [ ] リンティング（Linting）設定
- [ ] コードフォーマットチェック設定
- [ ] ビルド/コンパイル設定
- [ ] テスト実行設定
- [ ] **カバレッジ閾値（80%）強制設定** ⭐最重要

---

#### **Step 3: ローカルで品質ゲートコマンドを実行** (5-10分)

**目的**: CI実行前にローカルで動作確認

**Java の場合**:
```bash
# すべての品質ゲートを実行
./gradlew spotlessCheck checkstyleMain test jacocoTestCoverageVerification

# 期待される結果: すべてのタスクが成功
# BUILD SUCCESSFUL
```

**TypeScript の場合**:
```bash
# すべての品質ゲートを実行
npm run lint && \
npm run format:check && \
npm run type-check && \
npm run test:coverage

# 期待される結果: すべてのコマンドが成功
```

**Python の場合**:
```bash
# すべての品質ゲートを実行
pylint src/ && \
black --check src/ && \
mypy src/ && \
pytest --cov --cov-fail-under=80

# 期待される結果: すべてのコマンドが成功
```

**エラーが発生した場合**:
1. CI設定チェックリストの該当セクションを参照
2. 不足している設定を追加
3. 再度実行して確認

---

### 9.4 必須確認項目（言語別）

#### Java (Spring Boot) プロジェクト

**build.gradle 必須設定**:
```gradle
plugins {
    id 'jacoco'                        // ✅ 必須
    id 'com.diffplug.spotless'         // ✅ 必須
    id 'checkstyle'                    // ✅ 必須
}

// ✅ カバレッジ閾値（80%）設定 - 最重要
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80  // 80%閾値
            }
        }
    }
}

// ✅ check タスクに依存関係を追加（推奨）
check.dependsOn jacocoTestCoverageVerification
```

**.github/workflows/ci.yaml 必須ステップ**:
```yaml
jobs:
  build:
    steps:
      - run: ./gradlew spotlessCheck              # ✅ 必須
      - run: ./gradlew checkstyleMain             # ✅ 必須
      - run: ./gradlew build                      # ✅ 必須
      - run: ./gradlew test                       # ✅ 必須
      - run: ./gradlew jacocoTestCoverageVerification  # ✅ 最重要
```

**検証コマンド**:
```bash
# 設定確認
./gradlew tasks --group verification | grep -E "spotless|checkstyle|jacoco"

# 実行確認
./gradlew spotlessCheck checkstyleMain jacocoTestCoverageVerification
```

---

#### TypeScript/JavaScript プロジェクト

**package.json 必須スクリプト**:
```json
{
  "scripts": {
    "lint": "eslint src/**/*.ts",           // ✅ 必須
    "format:check": "prettier --check .",   // ✅ 必須
    "type-check": "tsc --noEmit",          // ✅ 必須
    "test:coverage": "jest --coverage"      // ✅ 必須
  }
}
```

**jest.config.js 必須設定**:
```javascript
module.exports = {
  // ✅ カバレッジ閾値（80%）設定 - 最重要
  coverageThreshold: {
    global: {
      lines: 80,
      functions: 80,
      branches: 80,
      statements: 80
    }
  }
};
```

**.github/workflows/ci.yaml 必須ステップ**:
```yaml
jobs:
  build:
    steps:
      - run: npm run lint                    # ✅ 必須
      - run: npm run format:check            # ✅ 必須
      - run: npm run type-check              # ✅ 必須
      - run: npm run build                   # ✅ 必須
      - run: npm run test:coverage           # ✅ 最重要
```

---

#### Python プロジェクト

**pyproject.toml 必須設定**:
```toml
[tool.pytest.ini_options]
# ✅ カバレッジ閾値（80%）設定 - 最重要
addopts = "--cov=src --cov-report=html --cov-fail-under=80"
```

または **.coveragerc**:
```ini
[report]
# ✅ カバレッジ閾値（80%）設定 - 最重要
fail_under = 80
```

**.github/workflows/ci.yaml 必須ステップ**:
```yaml
jobs:
  build:
    steps:
      - run: pylint src/                          # ✅ 必須
      - run: black --check src/                   # ✅ 必須
      - run: mypy src/                            # ✅ 必須
      - run: pytest --cov --cov-fail-under=80     # ✅ 最重要
```

---

### 9.5 よくある設定ミス

#### ❌ ミス1: カバレッジ測定はあるが閾値強制がない

**問題**:
```gradle
// ❌ レポート生成のみで閾値チェックなし
jacoco {
    toolVersion = "0.8.8"
}
// jacocoTestCoverageVerification が無い！
```

```yaml
# ❌ CI で jacocoTestReport のみ実行
- run: ./gradlew test jacocoTestReport
```

**対策**:
```gradle
// ✅ 閾値検証を追加
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80
            }
        }
    }
}
```

```yaml
# ✅ CI で jacocoTestCoverageVerification を実行
- run: ./gradlew jacocoTestCoverageVerification
```

---

#### ❌ ミス2: ローカルでは動くがCIで実行されない

**問題**: 設定ファイルに記述はあるが、CIワークフローで実行ステップが無い

**対策**: `.github/workflows/ci.yaml` に明示的に実行ステップを追加

---

#### ❌ ミス3: フォーマットチェックが警告のみで止まらない

**問題**:
```yaml
# ❌ 失敗しても続行
- run: ./gradlew spotlessCheck || true
```

**対策**:
```yaml
# ✅ 失敗時にパイプライン停止
- run: ./gradlew spotlessCheck
```

---

### 9.6 完了条件

**すべて ✅ になっていること**:

- [ ] 🔴 CI設定チェックリストを実行した
- [ ] 🔴 言語別必須設定がすべて存在する
- [ ] 🔴 カバレッジ閾値（80%）が設定されている
- [ ] 🔴 ローカルですべての品質ゲートコマンドが実行できる
- [ ] 🔴 ローカル実行でエラーが無い（または予想通りのエラー）

**これらが ✅ でない場合、Phase 3実装開始を延期してCI設定を修正**

---

### 9.7 参照ドキュメント

- 🔴 **必須**: `/00-guides/CI-SETUP-CHECKLIST.md` - 本セクションで使用
- 🔴 **必須**: `/03-development-process/ci-cd-pipeline.md` - CI/CD標準全般
- 🔴 **必須**: `/04-quality-standards/code-quality-standards.md` - コード品質基準
- 🔴 **必須**: `/04-quality-standards/testing-standards.md` - カバレッジ基準
- 🟡 **推奨**: `/08-templates/ci-templates/` - CI設定テンプレート（今後追加予定）

---

### 9.8 Phase 4レビュー時の再確認

Phase 4（レビュー）開始時に、CI実行結果を確認:

- [ ] PRのCIがすべてパスしている
- [ ] CIログで品質ゲートが実行されたことを確認
- [ ] カバレッジレポートで実際のカバレッジ率が80%以上

**詳細**: `/00-guides/phase-guides/phase-4-review-qa-guide.md` セクション4.X参照

---

### 9.9 トラブルシューティング

**Q: CI設定チェックリストが見つからない**

A: `/00-guides/CI-SETUP-CHECKLIST.md` を確認してください。

---

**Q: ローカルで品質ゲートコマンドが失敗する**

A: CI設定チェックリストの「よくある設定ミス」セクションを参照してください。

---

**Q: CIが実行されるがログに品質ゲートが見当たらない**

A: `.github/workflows/ci.yaml` に必須ステップが含まれているか確認してください。

---

**Q: カバレッジ閾値をどこで設定すればいいか分からない**

A: CI設定チェックリストのセクション2（言語別必須設定マトリクス）を参照してください。

## 10 ドキュメントコメント必須化ガイド（全言語共通） ⭐NEW

### 10.0 詳細実装ガイド 📖

**重要**: このセクションは基本要件を記載しています。
詳細なCI品質ゲート設定手順、コードテンプレート、解決策については以下を参照してください：

👉 **[DOCUMENTATION-COMMENT-ISSUE-SOLUTION.md](../DOCUMENTATION-COMMENT-ISSUE-SOLUTION.md)**
   - **Java**: Checkstyle設定の詳細手順（`checkstyle.xml`, `build.gradle`）
   - **TypeScript**: ESLint設定（`eslint-plugin-jsdoc`）の詳細手順
   - **Python**: Pylint/pydocstyle設定の詳細手順
   - **各言語のコードテンプレート集**（Controller/Service/DTO）
   - **6つの解決策の実装方法**

---

### 10.1 概要

**重要**: ドキュメントコメントは品質ゲートの一部です。
以下の基準を満たさないコードは、Phase 4レビューで却下されます。

**対象言語**:
- TypeScript/JavaScript (JSDoc)
- Python (Docstring)
- Java (Javadoc)
- その他（言語標準に準拠）

---

### 10.2 必須レベル別チェックリスト（言語非依存）

#### 🔴 Level 1: 必須（品質ゲート）

**ファイルレベル**:
- [ ] すべてのソースファイルにファイルヘッダーコメントを記述した
  - TypeScript: `@fileoverview`
  - Python: モジュールDocstring
  - Java: パッケージJavadoc
- [ ] ファイルの目的・責任を明記した
- [ ] モジュール/パッケージ構造を文書化した

**クラス・インターフェースレベル**:
- [ ] すべてのパブリッククラスにドキュメントコメントを記述した
- [ ] クラスの目的・責任を説明した
- [ ] 使用例を記述した（複雑なクラスの場合）

**関数・メソッドレベル**:
- [ ] すべてのパブリック関数/メソッドにドキュメントコメントを記述した
- [ ] すべてのパラメータを説明した
- [ ] 戻り値を説明した
- [ ] 発生しうる例外を説明した

**インターフェース・型定義レベル**:
- [ ] すべてのパブリックインターフェース/型にドキュメントコメントを記述した
- [ ] すべてのプロパティ/フィールドに説明を記述した

---

#### 🟡 Level 2: 強く推奨

**適用対象**:
- [ ] 複雑な内部ロジック（循環的複雑度10以上）
- [ ] ビジネスルール・制約を反映した実装
- [ ] 非自明な実装（パフォーマンス最適化、技術的回避策）
- [ ] セキュリティに関わる実装

**記述内容**:
- [ ] **なぜ**その実装になったかを説明
- [ ] ビジネス上の制約・理由を明記
- [ ] セキュリティ上の考慮事項を記載
- [ ] 将来の改善点や注意事項を記載（TODO/FIXME）

---

#### ⚪ Level 3: 任意

**適用対象**:
- 単純なゲッター/セッター
- 自己説明的なプライベートメソッド
- テストコード（ただしテスト目的は記述推奨）

---

### 10.3 言語別ドキュメントコメント形式

#### **TypeScript/JavaScript (JSDoc)**

**参照ドキュメント**: 
- `/01-coding-standards/typescript-javascript-standards.md` - セクションX
- `/08-templates/code-templates/typescript/`

**必須タグ**:
- `@fileoverview` - ファイルの目的
- `@class` / `@interface` - クラス/インターフェース
- `@param` - パラメータ
- `@returns` - 戻り値
- `@throws` - 例外

**自動チェック**:
```bash
npm run lint:jsdoc
```

---

#### **Python (Docstring)**

**参照ドキュメント**: 
- `/01-coding-standards/python-standards.md` - セクションX
- `/08-templates/code-templates/python/`

**必須形式**: Google Style Docstring

**必須セクション**:
- モジュールDocstring（ファイル先頭）
- クラスDocstring
- 関数Docstring:
  - `Args:` - パラメータ
  - `Returns:` - 戻り値
  - `Raises:` - 例外

**自動チェック**:
```bash
pylint --enable=missing-docstring src/
pydocstyle src/
```

---

#### **Java (Javadoc)**

**参照ドキュメント**: 
- `/01-coding-standards/java-standards.md` - セクションX
- `/08-templates/code-templates/java/`

**必須タグ**:
- クラスレベル: クラスの目的
- `@param` - パラメータ
- `@return` - 戻り値
- `@throws` - 例外
- `@author` - 作成者（オプション）
- `@since` - バージョン（オプション）

**自動チェック**:
```bash
mvn checkstyle:check
# または
./gradlew checkstyleMain
```

---

#### **その他の言語**

各言語の標準ドキュメントコメント形式に従ってください:

| 言語 | ドキュメント形式 | 参照先 |
|------|----------------|--------|
| C# | XML Documentation | `/01-coding-standards/csharp-standards.md` |
| Go | Godoc | `/01-coding-standards/go-standards.md` |
| Rust | Rustdoc | `/01-coding-standards/rust-standards.md` |
| PHP | PHPDoc | `/01-coding-standards/php-standards.md` |
| Ruby | RDoc | `/01-coding-standards/ruby-standards.md` |

---

### 10.4 実装フロー（言語非依存）

```
[ファイル作成]
  ↓
1. 言語別テンプレートからファイルヘッダーをコピー (1分)
   - TypeScript: /08-templates/code-templates/typescript/file-header.txt
   - Python: /08-templates/code-templates/python/module-docstring.txt
   - Java: /08-templates/code-templates/java/file-header.txt
  ↓
[クラス/インターフェース定義]
  ↓
2. 言語別テンプレートからクラスコメントをコピー (2分)
  ↓
[関数/メソッド実装]
  ↓
3. 言語別テンプレートから関数コメントをコピー (3分)
  ↓
4. 実装しながらコメントを具体化 (実装と同時)
  ↓
[実装完了後]
  ↓
5. 言語別Linterでチェック (1分)
   - TypeScript: npm run lint:jsdoc
   - Python: pylint + pydocstyle
   - Java: mvn checkstyle:check
  ↓
6. エラーがあれば修正 (数分)
  ↓
7. コミット（pre-commitフックで再チェック）
  ↓
✅ 完了
```

---

### 10.5 自動チェックの設定（言語別）

#### **TypeScript/JavaScript**

**ESLint + eslint-plugin-jsdoc**

```json
// .eslintrc.json
{
  "plugins": ["jsdoc"],
  "rules": {
    "jsdoc/require-jsdoc": ["error", {
      "require": {
        "FunctionDeclaration": true,
        "MethodDefinition": true,
        "ClassDeclaration": true
      },
      "publicOnly": true
    }],
    "jsdoc/check-param-names": "error",
    "jsdoc/require-param": "error",
    "jsdoc/require-param-description": "error",
    "jsdoc/require-returns": "error",
    "jsdoc/require-returns-description": "error"
  }
}
```

**インストール**:
```bash
npm install --save-dev eslint-plugin-jsdoc
```

---

#### **Python**

**Pylint + pydocstyle**

```ini
# .pylintrc
[MESSAGES CONTROL]
enable=missing-module-docstring,
       missing-class-docstring,
       missing-function-docstring

[BASIC]
docstring-min-length=10
no-docstring-rgx=^_  # プライベート関数は除外

[DESIGN]
max-complexity=10
```

```ini
# .pydocstyle
[pydocstyle]
convention = google
ignore = D100,D104  # パッケージ __init__.py は任意
match = .*\.py
```

**インストール**:
```bash
pip install pylint pydocstyle
```

**チェックコマンド**:
```bash
pylint --enable=missing-docstring src/
pydocstyle src/
```

---

#### **Java**

**Checkstyle**

```xml
<!-- checkstyle.xml -->
<module name="Checker">
  <module name="TreeWalker">
    <!-- Javadoc 必須化 -->
    <module name="MissingJavadocMethod">
      <property name="scope" value="public"/>
      <property name="allowMissingPropertyJavadoc" value="true"/>
    </module>
    
    <module name="MissingJavadocType">
      <property name="scope" value="public"/>
    </module>
    
    <!-- Javadoc 品質チェック -->
    <module name="JavadocMethod">
      <property name="validateThrows" value="true"/>
    </module>
    
    <module name="JavadocType"/>
    
    <module name="JavadocStyle">
      <property name="checkFirstSentence" value="true"/>
      <property name="checkEmptyJavadoc" value="true"/>
    </module>
  </module>
</module>
```

**Maven設定**:
```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-checkstyle-plugin</artifactId>
  <version>3.3.0</version>
  <configuration>
    <configLocation>checkstyle.xml</configLocation>
    <failOnViolation>true</failOnViolation>
  </configuration>
</plugin>
```

**チェックコマンド**:
```bash
mvn checkstyle:check
```

---

### 10.6 Pre-commitフック設定（言語横断）

**ファイル**: `.husky/pre-commit`

```bash
#!/bin/sh
. "$(dirname "$0")/_/husky.sh"

echo "🔍 Running Documentation Comment checks..."

# TypeScript/JavaScript プロジェクトの場合
if [ -f "package.json" ]; then
  echo "Checking JSDoc..."
  npm run lint:jsdoc
  if [ $? -ne 0 ]; then
    echo "❌ JSDoc violations found"
    exit 1
  fi
fi

# Python プロジェクトの場合
if [ -f "setup.py" ] || [ -f "pyproject.toml" ]; then
  echo "Checking Python Docstrings..."
  pylint --enable=missing-docstring src/
  if [ $? -ne 0 ]; then
    echo "❌ Docstring violations found"
    exit 1
  fi
  
  pydocstyle src/
  if [ $? -ne 0 ]; then
    echo "❌ Docstring style violations found"
    exit 1
  fi
fi

# Java プロジェクトの場合
if [ -f "pom.xml" ]; then
  echo "Checking Javadoc..."
  mvn checkstyle:check
  if [ $? -ne 0 ]; then
    echo "❌ Javadoc violations found"
    exit 1
  fi
elif [ -f "build.gradle" ]; then
  echo "Checking Javadoc..."
  ./gradlew checkstyleMain
  if [ $? -ne 0 ]; then
    echo "❌ Javadoc violations found"
    exit 1
  fi
fi

echo "✅ Documentation Comment checks passed"
```

---

### 10.7 コメント品質基準（言語非依存）

#### **コメントすべき内容** ✅

1. **Why（なぜ）**: 実装の理由・意図
2. **ビジネスロジック**: 業務ルール・制約
3. **複雑なアルゴリズム**: 処理手順の説明
4. **セキュリティ考慮**: 脆弱性対策の理由
5. **パフォーマンス最適化**: 最適化の背景
6. **将来の改善点**: TODO/FIXME

#### **コメント不要な内容** ❌

1. **What（何を）**: コードを読めば分かること
2. **冗長な説明**: 変数名と同じ内容の繰り返し
3. **コミット履歴**: Gitで追跡できる情報
4. **古いコード**: コメントアウトされたコード

---

### 10.8 よくあるミスと対策（言語共通）

#### ❌ ミス1: 実装後にまとめてコメントを書く

**問題**:
- 実装の意図を忘れる
- 一括作業で時間がかかる
- 品質が低下する

**対策**:
✅ 関数定義 → コメント記述 → 実装の順で進める

---

#### ❌ ミス2: "What"だけを書いて"Why"を書かない

**悪い例（TypeScript）**:
```typescript
/**
 * ユーザーを取得する
 * @param id - ID
 * @returns ユーザー
 */
```

**良い例（TypeScript）**:
```typescript
/**
 * ユーザーをIDで取得する
 * 
 * キャッシュ機構により頻繁なアクセスでもパフォーマンスを維持。
 * 存在しない場合はnullを返し、エラーをスローしない設計。
 * 
 * @param id - ユーザーの一意識別子（UUID v4形式）
 * @returns ユーザーオブジェクト、または見つからない場合はnull
 */
```

**悪い例（Python）**:
```python
def get_user(id: str) -> User:
    """ユーザーを取得する"""
    pass
```

**良い例（Python）**:
```python
def get_user(user_id: str) -> Optional[User]:
    """ユーザーをIDで取得する
    
    キャッシュ機構により頻繁なアクセスでもパフォーマンスを維持。
    存在しない場合はNoneを返し、例外をスローしない設計。
    
    Args:
        user_id: ユーザーの一意識別子（UUID v4形式）
    
    Returns:
        ユーザーオブジェクト、または見つからない場合はNone
    
    Note:
        Redis経由でキャッシュ（TTL: 5分）
    """
    pass
```

---

#### ❌ ミス3: 複雑なロジックにインラインコメントがない

**悪い例**:
```python
result = [x for x in data if x.status == 'active' and x.value > 100]
total = sum(x.value for x in result)
```

**良い例**:
```python
# アクティブかつ閾値を超えるデータのみを抽出
# ビジネス要件: 100以下は集計対象外
active_data = [x for x in data if x.status == 'active' and x.value > 100]

# 合計値を計算（月次レポート表示用）
total = sum(x.value for x in active_data)
```

---

### 10.9 参照ドキュメント（言語別）

#### TypeScript/JavaScript
- 🔴 必須: `/01-coding-standards/typescript-javascript-standards.md` - セクションX
- 🔴 必須: `/08-templates/code-templates/typescript/`

#### Python
- 🔴 必須: `/01-coding-standards/python-standards.md` - セクションX
- 🔴 必須: `/08-templates/code-templates/python/`

#### Java
- 🔴 必須: `/01-coding-standards/java-standards.md` - セクションX
- 🔴 必須: `/08-templates/code-templates/java/`

#### 言語非依存
- 🔴 必須: `/03-development-process/documentation-standards.md` - セクション7
- 🟡 推奨: `/09-reference/best-practices.md` - ドキュメントコメントセクション

---

---

## 📋 関連チェックリスト

Phase 3を開始・完了する際は、以下のチェックリストを使用してください：

### Phase 3開始前
- [Phase開始前チェックリスト](../../09-reference/checklists/phase-pre-work-checklist.md)

### Phase 3実施中
- [PBIタイプ別テスト要件チェックリスト](../../09-reference/checklists/pbi-type-test-requirements-checklist.md)
- [AIドキュメントコメントチェックリスト](../../09-reference/checklists/ai-documentation-comment-checklist.md)
- [CI/CD設定チェックリスト](../../09-reference/checklists/ci-setup-checklist.md)

### Phase 3完了時
- [Phase 3 完了チェックリスト](../../09-reference/checklists/phase-3-completion-checklist.md)

---
