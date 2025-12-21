---
version: "2.0.0"
last_updated: "2025-11-12"
status: "Deprecated" ⚠️
superseded_by: "phase-2A-pre-implementation-design-guide.md, phase-2B-post-implementation-design-guide.md"
---

# Phase 2: 設計ガイド

> ⚠️ **重要**: このガイドは廃止されました。Phase 2A/2B 版を参照してください。
> 
> ## 新しいガイド
> 
> Phase 2 は Phase 2A（実装前設計）と Phase 2B（実装後設計）に分割されました：
> 
> ### Phase 2A: 実装前設計 (Pre-Implementation Design)
> - **ガイド**: [phase-2A-pre-implementation-design-guide.md](./phase-2A-pre-implementation-design-guide.md)
> - **実行タイミング**: Phase 1 の後、Phase 3 の前
> - **期間**: 1-2日
> - **目的**: 実装の方向性を定める最小限の設計
> - **成果物**: ADR、API契約書、制約条件文書
> 
> ### Phase 2B: 実装後設計 (Post-Implementation Design)
> - **ガイド**: [phase-2B-post-implementation-design-guide.md](./phase-2B-post-implementation-design-guide.md)
> - **実行タイミング**: Phase 4 の後、Phase 5 の前（または Phase 3-4 と並行）
> - **期間**: 2-3日
> - **目的**: 実装内容の詳細な文書化
> - **成果物**: 設計書、完全版API仕様書、アーキテクチャ図、データモデル文書
> 
> ### 実行パターン
> - **パターンA（AI最適化型・推奨）**: Phase 2A → Phase 3 → Phase 4 → Phase 5（並行）
> - **パターンB（従来型）**: Phase 2A → Phase 5（実装前） → Phase 3 → Phase 4
> - **パターンC（リバースエンジニアリング型）**: Phase 3 → Phase 4 → Phase 5（実装後）
> 
> ### プロセス詳細
> - **概要**: [/03-development-process/revised-development-process-overview.md](../../03-development-process/revised-development-process-overview.md)
> - **成果物マトリクス**: [/03-development-process/revised-design-deliverables-matrix.md](../../03-development-process/revised-design-deliverables-matrix.md)
> - **マスターワークフロー**: [/00-guides/AI-MASTER-WORKFLOW-GUIDE.md](../AI-MASTER-WORKFLOW-GUIDE.md)
> 
> ---
> 
> 以下は旧版の内容です（参考用）：

---
document_type: "Lightweight Navigation Guide"
---

# Phase 2: 設計ガイド

> 要件からアーキテクチャへ - 軽量ナビゲーションガイド

**対象**: Devin、Cursor、その他の自律型AIエージェント  
**前提**: Phase 0（要件分析）とPhase 1（プロジェクト初期化）が完了している  
**目的**: 実装可能な設計を作成し、設計ドキュメントとして記録する

---

## 📋 このガイドについて

### ドキュメントタイプ
- **軽量ナビゲーション型**: 既存ドキュメントへの参照を重視
- **所要時間**: 5-10分で読める
- **重複回避**: 詳細は既存ドキュメントを参照

### Phase 2 の位置づけ

```
Phase 0: 要件分析・企画
    ↓
Phase 1: プロジェクト初期化
    ↓
┌─────────────────────────────────────────┐
│  Phase 2: 設計 ★ここ                     │
│  - アーキテクチャ設計                     │
│  - データモデル設計                       │
│  - API設計                               │
│  - セキュリティ設計                       │
└─────────────────────────────────────────┘
    ↓
Phase 3: 実装
```

### 開始条件

Phase 2は以下の条件を満たす場合に実施：
- ✅ **新規プロジェクト**: 必須（フル設計）
- ✅ **既存への大規模機能追加**: 必須（データモデル・API設計）
- ⚠️ **既存への小規模機能追加**: **簡易版で実施**（1時間程度）
  - 既存アーキテクチャに沿った設計のみ
- ⚠️ **バグ修正**: **条件付きスキップ可能**
  - 設計変更が必要な場合のみ実施（30分-1時間）
  - 設計変更がない場合はスキップ
- ⚠️ **リファクタリング**: **設計改善の場合は必須**
  - ADRで変更理由を記録

---

## 🎯 Phase 2 の成果物

**📂 重要**: 成果物の格納場所、命名規則、管理方法については以下を参照：
- 🔴 **必須**: [`../../03-development-process/design-artifacts-management-guide.md`](../../03-development-process/design-artifacts-management-guide.md)
  - **設計成果物の格納場所と命名規則の完全ガイド**
  - Phase 2A vs Phase 5 の成果物配置
  - ファイル命名規則・バージョン管理・ライフサイクル管理

### 必須成果物
1. **アーキテクチャ図**
   - システム全体構造
   - コンポーネント関係図
   - レイヤー構造

2. **データモデル図（ER図）**
   - エンティティと関係
   - スキーマ定義
   - マイグレーション計画

3. **API仕様書**
   - エンドポイント一覧
   - リクエスト/レスポンス形式
   - エラーハンドリング

4. **設計書**
   - 設計の概要と詳細
   - 設計判断の根拠

### 推奨成果物
5. **ADR（Architecture Decision Records）**
   - 重要な設計判断の記録
   - 選択肢と選定理由

6. **セキュリティ設計書**
   - 認証・認可方式
   - データ保護方式
   - セキュリティ要件

7. **設計レビュー資料**
   - レビュー用の説明資料

---

## 📚 成果物作成の詳細ガイド

### AI活用システム開発ドキュメントの活用

Phase 2で設計書を作成する際は、**AI活用システム開発ドキュメント**フォルダを必ず参照してください。

#### 📂 参照フォルダ

```
devin-organization-standards/
└── AI活用システム開発ドキュメント/
    ├── README_成果物重要度定義.md  ← まずここを確認
    └── 03_基本設計/
        ├── 作成ルール (6ファイル)   ← 詳細な作成方法
        └── samples/ (5ファイル)     ← 実際のサンプル
```

#### 🎯 推奨作業フロー

**ステップ1: 重要度の確認（5分）**
```bash
# 読むファイル
AI活用システム開発ドキュメント/README_成果物重要度定義.md

# 確認内容
- Phase 2（基本設計フェーズ）の成果物一覧
- 重要度A（必須）、B（推奨）、C（任意）の分類
- 自動生成すべき成果物の判断
```

**ステップ2: 作成ルールの参照（15-20分）**

各成果物について、対応する作成ルールを読む：

| 成果物 | 作成ルールファイル | 所要時間 |
|-------|----------------|----------|
| システム構成図 | `03_基本設計/03_システム構成図作成ルール.md` | 3分 |
| アーキテクチャ設計書 | `03_基本設計/03_アーキテクチャ設計書作成ルール.md` | 5分 |
| データベース設計書 | `03_基本設計/03_データベース設計書作成ルール.md` | 4分 |
| 画面設計書 | `03_基本設計/03_画面設計書作成ルール.md` | 3分 |
| インターフェース設計書 | `03_基本設計/03_インターフェース設計書作成ルール.md` | 3分 |
| セキュリティ設計書 | `03_基本設計/03_セキュリティ設計書作成ルール.md` | 3分 |

**ステップ3: サンプルの確認（10分）**
```bash
# サンプルフォルダ
AI活用システム開発ドキュメント/03_基本設計/samples/

# 含まれるサンプル
- sample_03_アーキテクチャ設計書.md
- sample_03_インターフェース設計書.md
- sample_03_システム構成図.md
- sample_03_セキュリティ設計書.md
- sample_03_データベース設計書.md
```

**ステップ4: 実際の作成（1-2日）**
- 作成ルールに従って、プロジェクト固有の設計書を作成
- サンプルを参考に、フォーマットと品質基準を満たす

**ステップ5: 品質確認（30分）**
- 各作成ルールの「品質基準」セクションでチェック
- 完成度チェックリストを実施

#### 📋 各成果物の作成ルールに含まれる情報

各作成ルールファイルには以下が含まれます：

1. **基本情報**: 目的、対象読者、関連成果物
2. **作成タイミング**: いつ作成するか、前提条件
3. **ドキュメント構成**: 必須セクション一覧
4. **記載ルール**: 表記規則、命名規則、図表の使用ルール
5. **品質基準**: 完成度チェックリスト、レビュー観点
6. **AI作成時の具体的指示**: 必須記載項目、避けるべき表現
7. **関連ドキュメント**: 参照すべき他の成果物
8. **よくある失敗例と対策**

#### ⚠️ 重要な注意事項

- **必ず重要度Aの成果物は作成する**（自動生成対象）
- **重要度Bは原則作成する**（プロジェクト特性により省略可）
- **重要度Cはユーザーの指示がある場合のみ作成**

例：
- バッチ設計書（重要度C）→ バッチ処理がない場合は作成不要
- 画面設計書（重要度B）→ API専用システムの場合は省略可

---

## 📊 Phase 2 の所要時間

| プロジェクトタイプ | 所要時間 | 含まれるタスク |
|------------------|---------|---------------|
| **新規プロジェクト（フル設計）** | 3-5時間 | 全タスク |
| **既存への大規模機能追加** | 2-3時間 | データモデル・API・セキュリティ設計 |
| **既存への小規模機能追加** | 1-2時間 | データモデル変更・API追加のみ |
| **リファクタリング** | 1-2時間 | 設計改善提案・ADR記録 |
| **バグ修正（設計変更あり）** | 30分-1時間 | 必要な部分のみ |

---

## 🔄 Phase 2 のワークフロー: 6ステップ

### **Step 2.1: アーキテクチャ設計** (30-60分)

**実行内容**:
1. 全体アーキテクチャの決定
2. レイヤー構造の設計
3. コンポーネント分割
4. 依存関係の整理

**チェックリスト**:
- [ ] システム全体のアーキテクチャパターンを選定（モノリス/マイクロサービス/レイヤード等）
- [ ] レイヤー構造を定義（プレゼンテーション層、ビジネス層、データ層等）
- [ ] コンポーネントの責務を明確化
- [ ] コンポーネント間の依存関係を定義
- [ ] アーキテクチャ図を作成

**🔴 必須参照**:
- [`02-architecture-standards/design-principles.md`](../../02-architecture-standards/design-principles.md)
  - **設計原則（SOLID、DRY、KISS等）を確認**
  - **このドキュメントの原則に従って設計**

**プロジェクトタイプ別の参照**:
- **API/バックエンド**: [`02-architecture-standards/api-architecture.md`](../../02-architecture-standards/api-architecture.md) 🔴 必須
- **フロントエンド**: [`02-architecture-standards/frontend-architecture.md`](../../02-architecture-standards/frontend-architecture.md) 🔴 必須
- **マイクロサービス**: [`02-architecture-standards/microservices-guidelines.md`](../../02-architecture-standards/microservices-guidelines.md) 🔴 必須
- **クラウドネイティブ**: [`02-architecture-standards/cloud-architecture.md`](../../02-architecture-standards/cloud-architecture.md) 🔴 必須

**🟡 推奨参照**:
- [`09-reference/design-patterns.md`](../../09-reference/design-patterns.md)
  - **適用可能なデザインパターンを確認**
- [`09-reference/anti-patterns.md`](../../09-reference/anti-patterns.md)
  - **避けるべきアンチパターンを確認**

---

### **Step 2.2: データモデル設計** (30-45分)

**実行内容**:
1. エンティティの特定
2. エンティティ間の関係定義
3. ER図の作成
4. スキーマ定義
5. マイグレーション計画

**チェックリスト**:
- [ ] 必要なエンティティをリストアップ
- [ ] エンティティ間の関係（1:1、1:N、N:M）を定義
- [ ] ER図を作成
- [ ] 各テーブルのカラム定義（型、制約、インデックス）
- [ ] マイグレーション戦略を決定
- [ ] パフォーマンス最適化を検討（インデックス、パーティショニング等）

**🔴 必須参照**:
- [`02-architecture-standards/database-design.md`](../../02-architecture-standards/database-design.md)
  - **このドキュメントの記載内容に従ってデータベースを設計**
  - **正規化、インデックス設計、制約定義の基準を確認**

**🟡 推奨参照**:
- [`05-technology-stack/database-stack.md`](../../05-technology-stack/database-stack.md)
  - **使用するデータベース技術の選定基準を確認**
- [`09-reference/best-practices.md`](../../09-reference/best-practices.md)
  - **データモデリングのベストプラクティスを確認**

**ER図作成ツール例**:
- Mermaid (マークダウン埋め込み可能)
- dbdiagram.io
- draw.io

---

### **Step 2.3: API設計** (45-90分)

**実行内容**:
1. エンドポイント設計
2. リクエスト/レスポンス形式定義
3. エラーハンドリング設計
4. 認証・認可方式の決定
5. API仕様書作成

**チェックリスト**:
- [ ] RESTful / GraphQL / gRPC 等のAPI形式を決定
- [ ] エンドポイント一覧を作成（パス、HTTPメソッド、用途）
- [ ] リクエストボディ/クエリパラメータを定義
- [ ] レスポンス形式を定義（成功/エラー）
- [ ] エラーコードとエラーメッセージを定義
- [ ] ページネーション/フィルタリング/ソート方式を決定
- [ ] バージョニング戦略を決定
- [ ] レート制限を検討

**🔴 必須参照**:
- [`02-architecture-standards/api-architecture.md`](../../02-architecture-standards/api-architecture.md)
  - **このドキュメントの記載内容に従ってAPI設計を実施**（超重要、294KB）
  - **RESTful API設計原則、エラーハンドリング、バージョニング等を確認**

**🟡 推奨参照**:
- [`08-templates/api-documentation-template.md`](../../08-templates/api-documentation-template.md)
  - **API仕様書のテンプレートを使用**
- [`07-security-compliance/authentication-authorization.md`](../../07-security-compliance/authentication-authorization.md)
  - **認証・認可方式を確認**

**API仕様書作成ツール例**:
- OpenAPI (Swagger)
- Postman
- Markdown テンプレート

---

### **Step 2.4: セキュリティ設計** (30-45分)

**実行内容**:
1. 認証・認可方式の詳細設計
2. データ保護方式の決定
3. セキュリティ要件の整理
4. 脅威モデリング（オプション）

**チェックリスト**:
- [ ] 認証方式を決定（JWT、OAuth2、セッション等）
- [ ] 認可モデルを決定（RBAC、ABAC等）
- [ ] データ暗号化方式を決定（転送時・保存時）
- [ ] 機密情報の取り扱い方針を定義
- [ ] 入力検証ルールを定義
- [ ] セキュリティヘッダーを定義
- [ ] 監査ログ要件を定義

**🔴 必須参照**:
- [`02-architecture-standards/security-architecture.md`](../../02-architecture-standards/security-architecture.md)
  - **このドキュメントの記載内容に従ってセキュリティアーキテクチャを設計**

**🟡 推奨参照**:
- [`07-security-compliance/secure-coding-practices.md`](../../07-security-compliance/secure-coding-practices.md)
  - **セキュアコーディングの基準を確認**
- [`07-security-compliance/authentication-authorization.md`](../../07-security-compliance/authentication-authorization.md)
  - **認証・認可の実装パターンを確認**
- [`07-security-compliance/data-protection.md`](../../07-security-compliance/data-protection.md)
  - **データ保護の要件を確認**

---

### **Step 2.5: パフォーマンス設計** (20-30分)

**実行内容**:
1. キャッシュ戦略の決定
2. クエリ最適化計画
3. スケーリング戦略の検討
4. パフォーマンス目標の設定

**チェックリスト**:
- [ ] キャッシュ対象とキャッシュ戦略を決定（Redis、CDN等）
- [ ] データベースクエリの最適化方針を決定
- [ ] スケーリング方式を検討（水平/垂直、オートスケール）
- [ ] パフォーマンス目標を設定（レスポンスタイム、スループット）
- [ ] ボトルネックになりそうな箇所を特定

**🟡 推奨参照**:
- [`02-architecture-standards/database-design.md`](../../02-architecture-standards/database-design.md)
  - **クエリ最適化の基準を確認**
- [`05-technology-stack/messaging-stack.md`](../../05-technology-stack/messaging-stack.md)
  - **非同期処理・メッセージングの活用を検討**
- [`05-technology-stack/search-stack.md`](../../05-technology-stack/search-stack.md)
  - **検索機能のパフォーマンス最適化を確認**

**⚪ 参考**:
- [`04-quality-standards/performance-testing.md`](../../04-quality-standards/performance-testing.md)
  - **パフォーマンステストの基準を確認**

---

### **Step 2.6: 設計ドキュメント作成** (30-45分)

**実行内容**:
1. 設計書の作成
2. ADR（Architecture Decision Records）の記録
3. 設計レビュー資料の作成

**チェックリスト**:
- [ ] 設計書を作成（概要、詳細設計、図表）
- [ ] 重要な設計判断をADRとして記録
- [ ] 設計レビュー用の資料を準備
- [ ] 設計書を適切な場所に保存（リポジトリのdocs/等）

**🔴 必須参照**:
- [`08-templates/design-document-template.md`](../../08-templates/design-document-template.md)
  - **設計書のテンプレートを使用**

**🟡 推奨参照**:
- [`08-templates/adr-template.md`](../../08-templates/adr-template.md)
  - **ADRのテンプレートを使用**
- [`08-templates/design-review-template.md`](../../08-templates/design-review-template.md)
  - **設計レビュー資料のテンプレートを使用**
- [`10-governance/architecture-decision-records.md`](../../10-governance/architecture-decision-records.md)
  - **ADRの記録方法を確認**

**設計ドキュメントの保存場所**:
```
project-root/
├── docs/
│   ├── design/
│   │   ├── architecture.md        # アーキテクチャ設計書
│   │   ├── data-model.md          # データモデル設計書
│   │   ├── api-specification.md   # API仕様書
│   │   └── security-design.md     # セキュリティ設計書
│   └── adr/
│       ├── 0001-choice-of-database.md
│       ├── 0002-api-versioning-strategy.md
│       └── ...
```

---

## 📚 参照ドキュメント一覧

### コアドキュメント（Phase 2で頻繁に参照）

| ドキュメント | 優先度 | 用途 |
|------------|-------|------|
| `02-architecture-standards/design-principles.md` | 🔴 必須 | 設計原則（全般） |
| `02-architecture-standards/api-architecture.md` | 🔴 必須 | API設計（超重要） |
| `02-architecture-standards/database-design.md` | 🔴 必須 | データベース設計 |
| `02-architecture-standards/security-architecture.md` | 🔴 必須 | セキュリティ設計 |
| `08-templates/design-document-template.md` | 🔴 必須 | 設計書テンプレート |
| `08-templates/api-documentation-template.md` | 🔴 必須 | API仕様書テンプレート |
| `09-reference/design-patterns.md` | 🟡 推奨 | デザインパターン |
| `09-reference/anti-patterns.md` | 🟡 推奨 | アンチパターン |
| `07-security-compliance/authentication-authorization.md` | 🟡 推奨 | 認証・認可 |

### プロジェクトタイプ別の追加参照

**フロントエンドプロジェクト**:
- 🔴 [`02-architecture-standards/frontend-architecture.md`](../../02-architecture-standards/frontend-architecture.md)

**バックエンド/APIプロジェクト**:
- 🔴 [`02-architecture-standards/api-architecture.md`](../../02-architecture-standards/api-architecture.md)

**マイクロサービスプロジェクト**:
- 🔴 [`02-architecture-standards/microservices-guidelines.md`](../../02-architecture-standards/microservices-guidelines.md)
- 🟡 [`05-technology-stack/messaging-stack.md`](../../05-technology-stack/messaging-stack.md)

**クラウドネイティブプロジェクト**:
- 🔴 [`02-architecture-standards/cloud-architecture.md`](../../02-architecture-standards/cloud-architecture.md)

---

## 🔍 実践的な活用方法

### 基本的なワークフロー

1. **アーキテクチャから開始**
   - 必須: 設計原則、該当するアーキテクチャドキュメント
   - 推奨: デザインパターン、アンチパターン

2. **データとAPIの詳細設計**
   - 必須: データベース設計、API設計
   - 推奨: テンプレート使用

3. **セキュリティとパフォーマンスを検討**
   - 必須: セキュリティアーキテクチャ
   - 推奨: 認証・認可、データ保護、パフォーマンス最適化

4. **設計ドキュメントを作成**
   - 必須: 設計書テンプレート
   - 推奨: ADRテンプレート、レビューテンプレート

### プロジェクトタイプ別のアプローチ

**新規プロジェクト**:
- **全ステップを実施** (3-5時間)
- アーキテクチャから詳細設計まで網羅

**既存への大規模機能追加**:
- **Step 2.1（簡易版）+ 2.2 + 2.3 + 2.4** (2-3時間)
- 既存アーキテクチャに沿った設計
- データモデル変更とAPI追加が中心

**既存への小規模機能追加**:
- **Step 2.2（簡易版）+ 2.3（簡易版）** (1-2時間)
- 必要最小限の設計

**リファクタリング**:
- **Step 2.1（設計改善）+ 2.6（ADR記録）** (1-2時間)
- 変更理由の明確化が重要

**バグ修正（設計変更あり）**:
- **該当する設計のみ修正** (30分-1時間)
- 小規模な場合はADR記録のみ

### 設計レビューのタイミング

**設計レビューが推奨される場合**:
- 新規プロジェクト
- 大規模な機能追加
- アーキテクチャ変更を伴うリファクタリング

**設計レビューのチェックポイント**:
- 設計原則に従っているか
- セキュリティ要件を満たしているか
- パフォーマンス目標を達成できるか
- 拡張性・保守性が考慮されているか

---

## ⚠️ 重要な原則

### ドキュメント参照の原則
- **参照ドキュメントの記載内容が最優先**
- 本ガイドは参照先への案内役
- 疑問点は該当ドキュメントを直接確認

### 設計の原則
- **SOLID原則を守る**: 単一責任、開放閉鎖、リスコフ置換、インターフェース分離、依存性逆転
- **セキュリティファースト**: 設計段階でセキュリティを組み込む
- **パフォーマンスを考慮**: 早期にボトルネックを特定
- **拡張性と保守性**: 将来の変更を見越した設計

### 完了基準
Phase 2は以下を満たしたら完了：
- ✅ アーキテクチャ図、データモデル図、API仕様書が作成されている
- ✅ 設計書が作成され、設計判断の根拠が明確
- ✅ セキュリティ要件が整理されている
- ✅ 設計レビューが完了している（該当する場合）
- ✅ 重要な設計判断がADRとして記録されている

**次のフェーズへ**: Phase 3（実装）に進む前に、設計が承認されていることを確認

---

## 🔄 ガイドの更新

- **最終更新日**: 2025-11-05
- **バージョン**: 1.0.0（軽量ナビゲーション型）
- **対象**: devin-organization-standards 全カテゴリ

---

## まとめ

Phase 2は**実装の青写真を作成する重要なフェーズ**です。

- **目的**: 実装可能な設計を作成し、設計ドキュメントとして記録する
- **成果物**: アーキテクチャ図、データモデル図、API仕様書、設計書、ADR
- **所要時間**: 新規プロジェクトで3-5時間、既存への追加で1-3時間

**最も重要な原則**: 
- 各ドキュメントの記載内容を確認し、その内容に従って設計してください
- 設計原則（SOLID、DRY、KISS等）を守ってください
- セキュリティとパフォーマンスを早期に考慮してください
- 設計判断の根拠を明確に記録してください

疑問や矛盾がある場合は、より具体的で新しいドキュメントを優先してください。
