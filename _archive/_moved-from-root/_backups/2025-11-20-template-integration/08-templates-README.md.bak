---
title: "テンプレート集 - 使用ガイド"
version: "1.3.0"
last_updated: "2025-11-20"
status: "Active"
owner: "Documentation Team"
---

# テンプレート集

> プロジェクトドキュメント作成を効率化する標準テンプレート集

## 📖 概要


## 📁 ディレクトリ構造

```
08-templates/
├── README.md                                  # このファイル
│
├── 【プロジェクト・設計テンプレート】
├── requirements-analysis-template.md         # 要件分析書（Phase 0）
├── project-readme-template.md                # プロジェクトREADME
├── project-proposal-template.md              # プロジェクト提案書
├── design-document-template.md               # 設計ドキュメント
├── design-review-template.md                 # 設計レビュー
├── adr-template.md                           # アーキテクチャ決定記録
│
├── 【API・技術ドキュメント】
├── api-specification-template.md             # API仕様書
├── api-documentation-template.md             # APIドキュメント
├── technical-proposal-template.md            # 技術提案書
│
├── 【テスト・品質テンプレート】
├── test-plan-template.md                     # テスト計画書
├── performance-test-report-template.md       # パフォーマンステストレポート
├── security-assessment-template.md           # セキュリティ評価
│
├── 【運用・インシデント】
├── incident-report-template.md               # インシデントレポート
├── postmortem-template.md                    # 事後分析
│
├── 【課題管理・レビュー】
├── issue-bug-report-template.md              # バグレポート
├── issue-feature-request-template.md         # 機能リクエスト
├── pull-request-template.md                  # プルリクエスト
│
├── 【その他】
├── meeting-minutes-template.md               # 議事録
├── progress-tracking-template.md             # 進捗管理
├── onboarding-checklist-template.md          # オンボーディング
│
└── 【サブディレクトリ】
    ├── ci-templates/                         # CI/CD設定テンプレート
    ├── code-templates/                       # コードテンプレート（TypeScript/Python/Java）
    ├── deployment-templates/                 # デプロイメント設定
    ├── documentation-templates/              # ドキュメント関連テンプレート
    ├── pr-templates/                         # プルリクエストテンプレート
    ├── project-templates/                    # プロジェクトテンプレート
    └── testing-templates/                    # テストテンプレート
```

---
このディレクトリには、日常的に使用する各種ドキュメントの標準テンプレートが含まれています。これらのテンプレートを使用することで、ドキュメント作成時間を大幅に削減し、一貫性のある品質の高いドキュメントを作成できます。

### 目的

- **効率化**: ドキュメント作成時間を50%削減
- **標準化**: 組織全体で統一されたフォーマット
- **品質保証**: 必要な情報の漏れを防止
- **AI連携**: Devin、Cursor等のAIツールとの連携最適化

---

## 📋 利用可能なテンプレート

### 0. 開発プロセステンプレート（Phase 2A / Phase 5）

> **新規追加（2025-11-20）**: AI自律的システム開発プロセスに対応した実践的テンプレート

#### Phase 2A: 事前設計テンプレート

**配置場所**: `phase-2a/`

##### api-contract-template.md

**概要**: 実装前のAPI契約書テンプレート

**使用タイミング**: Phase 1（要件分析）完了後、Phase 3（実装）開始前

**目的**:
- チーム間のAPI契約を事前に合意
- 実装前の方向性確認
- 実装中の手戻りを8-16時間削減

**主要セクション**:
- API概要
- エンドポイント仕様（リクエスト/レスポンス）
- 認証・セキュリティ
- エラーハンドリング
- バージョニング戦略

**所要時間**: 2-4時間  
**AI活用**: 推奨（テンプレート入力でAPI仕様生成）

**パス**: `08-templates/phase-2a/api-contract-template.md`

---

##### constraints-template.md

**概要**: プロジェクト制約条件の文書化テンプレート

**使用タイミング**: Phase 1完了後、Phase 3開始前

**目的**:
- 技術的制約の明確化
- インフラ・運用制約の共有
- セキュリティ・コスト制約の事前確認
- 実装中の制約違反リスクの削減

**主要セクション**:
- 技術的制約（言語、フレームワーク、ライブラリ）
- インフラ制約（サーバー、ネットワーク、ストレージ）
- 運用制約（デプロイ、監視、バックアップ）
- セキュリティ制約（認証、認可、暗号化）
- パフォーマンス制約（レスポンスタイム、スループット）
- コスト制約（予算、ライセンス）
- 組織制約（スキル、工数、スケジュール）

**所要時間**: 1-3時間  
**AI活用**: 推奨（既存環境情報から制約を自動抽出）

**パス**: `08-templates/phase-2a/constraints-template.md`

---

#### Phase 5: 詳細設計テンプレート

**配置場所**: `phase-5/`

##### data-model-template.md

**概要**: データモデル文書化のための詳細テンプレート

**使用タイミング**: Phase 3（実装）完了後、またはPhase 3-4と並行

**目的**:
- 実装されたデータモデルの正確な文書化
- エンティティ、リレーションシップ、制約の明確化
- ER図、スキーマ定義の整備
- 将来の変更・拡張の基盤構築

**主要セクション**:
- エンティティ定義（属性、型、制約）
- リレーションシップ（1:1、1:N、N:M）
- インデックス設計
- 正規化レベル
- マイグレーション戦略
- サンプルクエリ

**対応DB**:
- RDBMS（PostgreSQL、MySQL等）
- NoSQL（MongoDB、DynamoDB等）

**所要時間**: 4-8時間  
**AI活用併用時**: 2-4時間

**パス**: `08-templates/phase-5/data-model-template.md`

---

##### as-built-notes-template.md

**概要**: Phase 5最終化時の実装差分記録テンプレート

**使用タイミング**: Phase 4（統合・テスト）完了後、Phase 5最終化時

**目的**:
- 設計からの変更点を記録
- 変更理由（技術的・ビジネス的）の文書化
- 影響範囲の明確化
- 今後の改善提案の記録
- チーム学習の促進

**主要セクション**:
- 設計からの変更点
- 変更理由と背景
- 影響範囲分析
- 今後の改善提案
- レビューコメント

**所要時間**: 1-2時間  
**AI活用**: 推奨（Git差分から自動生成）

**パス**: `08-templates/phase-5/as-built-notes-template.md`

---

#### Phase 5並行作業支援ツール

**配置場所**: `phase-5-parallel/`

> **Pattern C（マイルストーン並行型）専用**: Phase 3-4実装中にPhase 5を並行実施するための支援ツール

##### milestone-definition-template.md

**概要**: マイルストーン定義の標準テンプレート

**使用タイミング**: プロジェクト開始時、Phase 3開始前

**目的**:
- 機能単位のマイルストーン明確化
- Phase 5作業ブロックのトリガー定義
- 完了条件（Acceptance Criteria）の設定
- 依存関係の整理

**主要セクション**:
- マイルストーン基本情報
- 完了条件（Acceptance Criteria）
- 依存関係
- リスクと対策
- Phase 5作業ブロック定義

**推奨パターン**: 金曜午後パターン
- 月-木: 実装集中（80%の時間）
- 金曜午後: Phase 5作業ブロック（20%の時間）

**所要時間**: 30分-1時間/マイルストーン  
**頻度**: プロジェクト開始時 + マイルストーン追加時

**パス**: `08-templates/phase-5-parallel/milestone-definition-template.md`

---

##### phase-5-work-block-checklist.md

**概要**: Phase 5作業ブロック実施時のチェックリスト

**使用タイミング**: 各Phase 5作業ブロック実施時（週1回程度）

**目的**:
- 作業ブロックの効率的実施
- タイムボックスの厳守（4-6時間）
- 80%ルールの適用（完璧を求めない）
- 進捗の可視化

**主要セクション**:
- 事前準備（コミット、ブランチ作成）
- 作業ブロック中（タイマー設定、集中作業）
- 作業ブロック後（レビュー、コミット）
- 週次チェック（進捗確認、調整）

**推奨実施頻度**: 週1回（金曜午後）  
**所要時間**: 4-6時間/ブロック  
**総作業ブロック数**: 3-5ブロック（プロジェクト規模による）

**パス**: `08-templates/phase-5-parallel/phase-5-work-block-checklist.md`

---

#### 新規テンプレートの定量的効果

**Phase 2A導入による効果**:
- 実装中の手戻り削減: 8-16時間
- チーム間の認識齟齬: 50%削減
- 実装開始までのリードタイム: 1-2日短縮

**Phase 5並行実施（Pattern C）による効果**:
- Phase 5工数削減: 29時間 → 18時間（**38%削減**）
- AI活用併用時: 29時間 → 9.5時間（**67%削減**）
- プロジェクトリードタイム短縮: 4週間 → 3.2週間（**20%短縮**）
- 設計・実装不一致改善: 20% → 5%以下（**75%改善**）

**実測時間配分（Pattern C）**:
- 従来: "20-30%"（曖昧）
- Pattern C: **15.8%**（実測値）
  - Week 1: 10%
  - Week 2: 10%
  - Week 3: 29%

---

#### 使用ガイド: Pattern C（マイルストーン並行型）

**ステップ1: マイルストーン定義**
1. `milestone-definition-template.md`を使用
2. 3-5個のマイルストーンを定義
3. 各マイルストーンの完了条件を明確化

**ステップ2: 作業ブロックのスケジュール**
1. 金曜午後をPhase 5作業ブロックに設定
2. カレンダーに4-6時間のブロックを予約
3. チームに作業ブロックの時間を共有

**ステップ3: 作業ブロックの実施**
1. `phase-5-work-block-checklist.md`を使用
2. タイマーを設定（4-6時間）
3. 80%ルールを適用（完璧を求めない）
4. マイルストーンごとにドキュメント化

**ステップ4: 週次レビュー**
1. Phase 5進捗を確認（25%→50%→80%→100%）
2. 次週の作業ブロック内容を調整
3. 必要に応じて時間を延長/短縮

**ステップ5: 最終化**
1. `as-built-notes-template.md`で差分記録
2. 全ドキュメントの統合
3. チームレビュー実施

---

#### 関連ドキュメント

**プロセスドキュメント**:
- [開発プロセス概要](../03-development-process/revised-development-process-overview.md)
  - Phase 5並行実施の詳細ガイド（678行目以降）
- [成果物マトリックス](../03-development-process/revised-design-deliverables-matrix.md)
  - Pattern Cの詳細タイムライン（218行目以降）

**テンプレートインデックス**:
- [TEMPLATE-INDEX.md](TEMPLATE-INDEX.md)
  - 全テンプレートの参照インデックス（v1.2.0）

---

### 1. プロジェクト関連

#### requirements-analysis-template.md
- **用途**: Phase 0 要件分析書作成
- **サイズ**: 約9KB
- **ステータス**: ✅ 作成済み (2025-11-14)
- **含まれる内容**:
  - プロジェクト情報
  - 背景と目的
  - 機能要件・非機能要件
  - 受入基準
  - 技術的制約
  - タスク分解
  - リスク分析
  - 見積もり

**使用タイミング**: Phase 0 Step 0.6 要件分析書の作成時

---

#### project-readme-template.md
- **用途**: プロジェクトのREADME作成
- **サイズ**: 約14KB
- **含まれる内容**:
  - プロジェクト概要
  - セットアップ手順
  - 使用方法
  - 開発ガイド
  - デプロイ手順
  - トラブルシューティング

**使用タイミング**: 新規プロジェクト開始時

---

#### technical-proposal-template.md
- **用途**: 技術提案書作成
- **サイズ**: 約15KB
- **含まれる内容**:
  - 提案背景
  - 現状分析
  - 提案内容
  - 技術選定
  - ROI分析
  - 実装計画
  - リスク評価

**使用タイミング**: 新技術導入、アーキテクチャ変更提案時

---

### 2. API・設計関連

#### api-specification-template.md
- **用途**: RESTful API仕様書作成
- **サイズ**: 約17KB
- **含まれる内容**:
  - API概要
  - 認証方法
  - エンドポイント定義
  - リクエスト/レスポンス形式
  - エラーコード
  - レート制限
  - バージョニング

**使用タイミング**: API設計時、外部連携時

---

#### design-document-template.md
- **用途**: システム・機能設計書作成
- **サイズ**: 約17KB
- **含まれる内容**:
  - 設計概要
  - アーキテクチャ設計
  - データ設計
  - インターフェース設計
  - セキュリティ設計
  - パフォーマンス設計

**使用タイミング**: 新機能開発、システム刷新時

---

### 3. テスト関連

#### test-plan-template.md
- **用途**: テスト計画書作成
- **サイズ**: 約14KB
- **含まれる内容**:
  - テスト戦略
  - テスト範囲
  - テストスケジュール
  - リソース計画
  - テストケース
  - 合格基準

**使用タイミング**: リリース前、大規模機能追加時

---

### 4. 開発プロセス関連

#### pull-request-template.md
- **用途**: Pull Request作成
- **サイズ**: 約6KB
- **含まれる内容**:
  - 変更内容
  - 変更理由
  - テスト内容
  - チェックリスト
  - スクリーンショット
  - レビュアー向け情報

**使用タイミング**: 全てのPR作成時

---

#### issue-bug-report-template.md
- **用途**: バグ報告Issue作成
- **サイズ**: 約4KB
- **含まれる内容**:
  - バグの説明
  - 再現手順
  - 期待される動作
  - 実際の動作
  - 環境情報
  - スクリーンショット

**使用タイミング**: バグ発見時

---

#### issue-feature-request-template.md
- **用途**: 機能要望Issue作成
- **サイズ**: 約6KB
- **含まれる内容**:
  - 機能の説明
  - ユースケース
  - 実現したいこと
  - 実装案
  - 代替案
  - 優先度

**使用タイミング**: 新機能提案時

---

### 5. 運用関連

#### incident-report-template.md
- **用途**: インシデントレポート作成
- **サイズ**: 約10KB
- **含まれる内容**:
  - インシデント概要
  - タイムライン
  - 影響範囲
  - 根本原因分析
  - 対応内容
  - 再発防止策

**使用タイミング**: インシデント発生後

---

#### meeting-minutes-template.md
- **用途**: 議事録作成
- **サイズ**: 約7KB
- **含まれる内容**:
  - 会議情報
  - 参加者
  - 議題
  - 議論内容
  - 決定事項
  - アクションアイテム

**使用タイミング**: 重要な会議後

---

## 🚀 使用方法

### 基本的な使い方

1. **テンプレート選択**
   - 目的に合ったテンプレートを選択

2. **コピー**
   - テンプレートファイルをコピー

3. **カスタマイズ**
   - プロジェクトに合わせて内容を編集
   - `[プレースホルダー]` を実際の値に置換

4. **保存**
   - 適切な場所に保存

---

### AIツールとの連携

#### Devin使用例

```
devin-organization-standards/08-templates/api-specification-template.md
を基に、ユーザー認証APIの仕様書を作成してください。

要件:
- OAuth 2.0認証
- JWT トークン
- レート制限: 100 req/min
```

#### Cursor使用例

```
@devin-organization-standards/08-templates/design-document-template.md
を参照して、決済機能の設計書を作成
```

---

## 📊 期待される効果

### Before(テンプレート使用前)

- ドキュメント作成: 4-8時間
- 一貫性: 低い
- 情報漏れ: 頻繁
- レビュー時間: 長い

### After(テンプレート使用後)

- ドキュメント作成: 2-4時間(**50%削減**)
- 一貫性: 高い
- 情報漏れ: 最小限
- レビュー時間: 短縮

---

## ✅ チェックリスト

### テンプレート使用時


---

## 📂 サブディレクトリ構成

このディレクトリには、カテゴリ別のテンプレートを格納するサブディレクトリが含まれています。

| ディレクトリ | 用途 | 状態 | 予定ファイル数 |
|------------|------|------|---------------|
| **code-templates/** ([README](./code-templates/README.md)) | コードテンプレート | 🔵 将来の拡張用（README配置済み） | 5-8ファイル |
| **deployment-templates/** ([README](./deployment-templates/README.md)) | デプロイテンプレート | 🔵 将来の拡張用（README配置済み） | 4-6ファイル |
| **documentation-templates/** | ドキュメントテンプレート | ✅ 1ファイル | - |
| **project-templates/** ([README](./project-templates/README.md)) | プロジェクトテンプレート | 🔵 将来の拡張用（README配置済み） | 3-5ファイル |
| **testing-templates/** ([README](./testing-templates/README.md)) | テストテンプレート | 🔵 将来の拡張用（README配置済み） | 4-6ファイル |

---

### code-templates/ 🔵
**目的**: コード作成を効率化する実装テンプレート集

**含まれる予定のテンプレート**:
- `controller-template.ts` - コントローラークラステンプレート
- `service-template.ts` - サービスクラステンプレート
- `repository-template.ts` - リポジトリパターンテンプレート
- `model-template.ts` - データモデルテンプレート
- `dto-template.ts` - Data Transfer Objectテンプレート
- `test-template.spec.ts` - テストファイルテンプレート
- `dockerfile-template` - Dockerfileテンプレート
- `docker-compose-template.yml` - Docker Composeテンプレート

**期待効果**:
- ボイラープレートコードの自動生成
- プロジェクト間のコード一貫性確保
- 新規ファイル作成時間の削減

**状態**: 🚧 **Phase 2で実装予定**  
**予定時期**: 2025年Q2

---

### deployment-templates/ 🔵
**目的**: デプロイメント設定の標準化テンプレート

**含まれる予定のテンプレート**:
- `kubernetes-deployment-template.yaml` - K8sデプロイメント設定
- `helm-chart-template.yaml` - Helmチャートテンプレート
- `github-actions-template.yml` - GitHub Actions CI/CD
- `terraform-template.tf` - Terraformインフラコード
- `cloudformation-template.yaml` - AWS CloudFormation
- `ansible-playbook-template.yml` - Ansible自動化

**期待効果**:
- インフラ設定の標準化
- デプロイメントエラーの削減
- 環境構築時間の短縮

**状態**: 🚧 **Phase 2で実装予定**  
**予定時期**: 2025年Q2

---

### documentation-templates/ ✅
**目的**: ドキュメント作成テンプレート（補助資料）

**現在のファイル**:
- ✅ `project-documentation-README.md` - プロジェクトドキュメント構造ガイド

**状態**: ✅ **一部完成**  
**追加予定**: 必要に応じて追加

---

### project-templates/ 🔵
**目的**: プロジェクト立ち上げ時の設定ファイルテンプレート

**含まれる予定のテンプレート**:
- `project-structure-template.md` - プロジェクト構造ガイド
- `gitignore-template` - .gitignoreテンプレート（言語別）
- `env-template` - .env.exampleテンプレート
- `makefile-template` - Makefileテンプレート
- `package-json-template` - package.jsonテンプレート

**期待効果**:
- プロジェクト立ち上げ時間の短縮
- 設定ファイルの標準化
- 環境設定ミスの防止

**状態**: 🚧 **Phase 2で実装予定**  
**予定時期**: 2025年Q2

---

### testing-templates/ 🔵
**目的**: テストコード作成テンプレート

**含まれる予定のテンプレート**:
- `unit-test-template.spec.ts` - ユニットテストテンプレート
- `integration-test-template.spec.ts` - 統合テストテンプレート
- `e2e-test-template.spec.ts` - E2Eテストテンプレート（Playwright）
- `load-test-template.js` - 負荷テストテンプレート（K6）
- `test-data-template.json` - テストデータテンプレート
- `mock-template.ts` - モック作成テンプレート

**期待効果**:
- テストカバレッジの向上
- テストコード品質の標準化
- テスト作成時間の削減

**状態**: 🚧 **Phase 2で実装予定**  
**予定時期**: 2025年Q2

---

### サブディレクトリ利用ガイド

#### AIツール（Devin、Cursor等）向け
```yaml
テンプレート選択フロー:
  新規ファイル作成時:
    1. code-templates/ から適切なテンプレートを選択
    2. プロジェクト固有の内容でカスタマイズ
    3. コーディング標準に準拠していることを確認
  
  インフラ構築時:
    1. deployment-templates/ から該当テンプレートを選択
    2. 環境に応じたパラメータを設定
    3. セキュリティチェックリストで検証
  
  テスト作成時:
    1. testing-templates/ からテストタイプに応じたテンプレートを選択
    2. テスト対象に合わせてカスタマイズ
    3. テスト標準に準拠していることを確認
```

#### 開発者向け使用ステップ
1. **テンプレート選択**: 用途に応じたサブディレクトリから選択
2. **コピー**: プロジェクトディレクトリにコピー
3. **カスタマイズ**: プロジェクト固有の内容に編集
4. **検証**: チェックリストで品質確認

---

## 🎯 Phase 2 実装計画

### タイムライン
- **2025年Q1**: 要件定義と設計
- **2025年Q2**: サブディレクトリテンプレート実装
- **2025年Q3**: 運用開始とフィードバック収集

### 期待される効果
- **開発効率**: 新規ファイル作成時間を50%削減
- **品質向上**: テンプレート使用によるミス削減
- **標準化**: プロジェクト間の一貫性向上

---

- [ ] 適切なテンプレートを選択
- [ ] 全てのセクションを確認
- [ ] プレースホルダーを全て置換
- [ ] プロジェクト固有の情報を追加
- [ ] 不要なセクションを削除
- [ ] レビューを実施
- [ ] メタデータ(バージョン、日付)を更新

---

## 🤝 貢献

### 新しいテンプレートの提案

1. 必要性の説明
2. サンプルテンプレート作成
3. Pull Request作成
4. レビュープロセス

### 既存テンプレートの改善

- フィードバックをIssueで報告
- 改善案をPRで提出

---

## 📚 関連ドキュメント

- [ドキュメントガイドライン](../03-development-process/documentation-guidelines.md)
- [コーディング標準](../01-coding-standards/README.md)
- [アーキテクチャ標準](../02-architecture-standards/README.md)

---

## 📞 サポート

質問やフィードバックは:
- Slack: #dev-standards
- Email: engineering-team@example.com

---

**最終更新**: 2025-11-20  
**バージョン**: 1.3.0  
**管理者**: Documentation Team

---

## 📝 更新履歴

### v1.3.0 (2025-11-20)
- ✅ Phase 2A / Phase 5テンプレート追加（6件）
- ✅ Pattern C（マイルストーン並行型）実施ガイド追加
- ✅ 定量的効果データ追加

### v1.2.0 (2025-11-14)
- 既存テンプレート更新

### v1.0.0 (2025-10-28)
- 初版リリース