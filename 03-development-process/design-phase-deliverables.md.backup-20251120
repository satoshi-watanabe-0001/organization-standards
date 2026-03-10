---
version: "1.0.0"
last_updated: "2025-11-11"
status: "draft"
owner: "Engineering Team"
category: "design-phase"
---

# 設計フェーズ成果物ガイド / Design Phase Deliverables Guide

## 📋 目次

1. [概要](#概要)
2. [設計フェーズの位置づけ](#設計フェーズの位置づけ)
3. [成果物一覧](#成果物一覧)
4. [成果物の詳細](#成果物の詳細)
5. [成果物の作成フロー](#成果物の作成フロー)
6. [品質基準](#品質基準)
7. [レビュープロセス](#レビュープロセス)
8. [ベストプラクティス](#ベストプラクティス)

---

## 概要

### 目的

このドキュメントは、設計フェーズで作成される各種成果物を明確化し、品質の高い設計ドキュメントを効率的に作成するためのガイドラインを提供します。

### 適用範囲

- 新規プロジェクト・機能開発の設計フェーズ
- 既存システムの大規模リファクタリング
- アーキテクチャ変更を伴う改修
- 技術選定が必要なプロジェクト

### 設計フェーズの重要性

```yaml
why_design_phase_matters:
  early_problem_detection:
    - 実装前に問題を発見・解決
    - 手戻りコストの削減
    - リスクの早期識別
  
  stakeholder_alignment:
    - 関係者間の認識統一
    - 技術的意思決定の透明性
    - 実装方針の合意形成
  
  knowledge_sharing:
    - 設計思想の共有
    - チーム全体の理解促進
    - 新メンバーのオンボーディング支援
  
  quality_assurance:
    - アーキテクチャの妥当性確認
    - セキュリティ・パフォーマンス考慮
    - 保守性・拡張性の確保
```

---

## 設計フェーズの位置づけ

### 開発ライフサイクルにおける位置

```
開発ライフサイクル:

Phase 1: 発見・計画 (Discovery & Planning)
  │
  ├─ 要件収集
  ├─ ユーザーリサーチ
  ├─ 実現可能性評価
  └─ プロジェクト計画
  │
  ▼
Phase 2: 設計 (Design) ★ このフェーズ
  │
  ├─ アーキテクチャ設計
  ├─ 技術選定
  ├─ 詳細設計
  ├─ レビュー・承認
  └─ 【成果物出力】← このドキュメントで整理
  │
  ▼
Phase 3: 開発 (Development)
  │
  ├─ 実装
  ├─ ユニットテスト
  └─ コードレビュー
  │
  ▼
Phase 4: QA・検証 (QA & Validation)
Phase 5: デプロイメント (Deployment)
Phase 6: 運用・監視 (Operations)
```

### 設計フェーズのインプット・アウトプット

```yaml
inputs:
  from_discovery_phase:
    - プロジェクト提案書
    - ビジネス要件
    - ユーザーストーリー
    - 技術調査結果
    - リスク評価
  
  from_stakeholders:
    - 機能要件
    - 非機能要件
    - 制約条件
    - 予算・スケジュール

outputs:
  design_documents:
    - 技術提案書 (Technical Proposal)
    - 設計書 (Design Document)
    - API仕様書 (API Specification)
    - ADR (Architecture Decision Record)
  
  to_development_phase:
    - 実装可能な設計仕様
    - 技術的意思決定の記録
    - 実装ガイドライン
    - テスト方針
```

---

## 成果物一覧

### 必須成果物 (Mandatory Deliverables)

| # | 成果物名 | テンプレート | 作成タイミング | 目的 |
|---|---------|------------|--------------|------|
| 1 | **技術提案書** | `technical-proposal-template.md` | プロジェクト初期 | 技術的アプローチの提案と承認取得 |
| 2 | **設計書** | `design-document-template.md` | 設計フェーズ | 詳細な技術設計の文書化 |
| 3 | **ADR** | `adr-template.md` | 重要な技術決定時 | アーキテクチャ決定の記録と理由 |

### 推奨成果物 (Recommended Deliverables)

| # | 成果物名 | テンプレート | 作成タイミング | 目的 |
|---|---------|------------|--------------|------|
| 4 | **API仕様書** | `api-specification-template.md` | API設計時 | API契約の明確化 |
| 5 | **APIドキュメント** | `api-documentation-template.md` | API実装後 | API利用者向けドキュメント |
| 6 | **設計レビュー記録** | `design-review-template.md` | レビュー実施時 | レビュー結果の記録 |

### 条件付き成果物 (Conditional Deliverables)

| # | 成果物名 | 作成条件 | 目的 |
|---|---------|---------|------|
| 7 | **データベーススキーマ設計書** | データモデル変更時 | スキーマ定義の明確化 |
| 8 | **セキュリティ評価書** | セキュリティ要件が高い場合 | セキュリティ対策の文書化 |
| 9 | **パフォーマンステスト計画** | 性能要件がクリティカル | パフォーマンス目標と検証方法 |
| 10 | **マイグレーション計画書** | 既存システムの移行時 | 安全な移行戦略の定義 |

---

## 成果物の詳細

### 1. 技術提案書 (Technical Proposal)

#### 概要
プロジェクトの技術的アプローチを提案し、関係者の承認を得るための包括的なドキュメント。

#### 主要セクション
```yaml
sections:
  executive_summary:
    - 提案の要約
    - 解決する問題
    - 期待される成果
  
  problem_definition:
    - 現状の課題
    - ビジネスへの影響
    - 技術的背景
  
  proposed_solution:
    - ソリューション概要
    - アーキテクチャ図
    - 技術スタック
  
  implementation_plan:
    - フェーズ分け
    - リソース配分
    - タイムライン
  
  cost_and_roi:
    - 初期コスト
    - 運用コスト
    - 投資対効果
  
  risk_and_mitigation:
    - リスク評価
    - 緩和策
  
  alternatives:
    - 代替案の比較
    - 推奨理由
```

#### 作成タイミング
- プロジェクト開始時（要件定義後）
- 新技術導入時
- アーキテクチャ変更時

#### ステークホルダー
- **作成者**: テックリード、アーキテクト
- **レビュアー**: エンジニアリングチーム、プロダクトマネージャー
- **承認者**: CTO、エンジニアリングマネージャー

#### 成功基準
- [ ] ビジネス価値が明確に示されている
- [ ] 技術的実現可能性が検証されている
- [ ] コストとROIが定量的に示されている
- [ ] リスクと緩和策が網羅されている
- [ ] 代替案との比較が適切に行われている
- [ ] ステークホルダーの承認を得ている

---

### 2. 設計書 (Design Document)

#### 概要
システムやモジュールの詳細な技術設計を記述するドキュメント。実装チームが直接参照する技術仕様書。

#### 主要セクション
```yaml
sections:
  overview:
    - 背景とモチベーション
    - 目標と非目標
    - スコープ
  
  architecture:
    - システム全体構成
    - コンポーネント設計
    - データフロー
    - 技術スタック
  
  detailed_design:
    - 各機能の詳細設計
    - 処理フロー
    - 実装詳細（疑似コード）
  
  data_model:
    - データベーススキーマ
    - ER図
    - マイグレーション戦略
  
  interface_design:
    - API仕様
    - 外部システム連携
  
  non_functional_requirements:
    - セキュリティ設計
    - パフォーマンス設計
    - スケーラビリティ
    - エラーハンドリング
  
  testing_strategy:
    - テストレベル
    - カバレッジ目標
  
  deployment:
    - デプロイメント戦略
    - 環境構成
    - リリース手順
  
  monitoring:
    - 監視項目
    - ログ設計
    - アラート設定
```

#### 作成タイミング
- 技術提案書承認後
- 実装開始前
- 大規模リファクタリング時

#### ステークホルダー
- **作成者**: リードエンジニア、担当エンジニア
- **レビュアー**: テックリード、アーキテクト、セキュリティチーム
- **利用者**: 実装チーム全員

#### 成功基準
- [ ] アーキテクチャが明確に図示されている
- [ ] 実装に必要な情報が網羅されている
- [ ] データモデルが適切に設計されている
- [ ] セキュリティ要件が考慮されている
- [ ] パフォーマンス目標が設定されている
- [ ] テスト戦略が明確である
- [ ] デプロイメント手順が定義されている

---

### 3. ADR (Architecture Decision Record)

#### 概要
重要なアーキテクチャ決定とその理由を記録するドキュメント。「なぜその選択をしたか」を明確に残す。

#### 主要セクション
```yaml
sections:
  metadata:
    - ADR番号
    - タイトル
    - ステータス (Proposed/Accepted/Superseded/Deprecated)
    - 日付
    - 決定者
  
  context:
    - 背景
    - 問題定義
    - 制約条件
    - 影響を受けるステークホルダー
  
  decision:
    - 選択した解決策
    - 実装アプローチ
    - 技術仕様
  
  rationale:
    - 決定理由
    - なぜこの選択肢か
    - トレードオフの考慮
  
  alternatives:
    - 検討した代替案
    - 各案のメリット・デメリット
    - 不採用の理由
  
  consequences:
    - ポジティブな影響
    - ネガティブな影響
    - リスク
  
  implementation:
    - 実装計画
    - 影響範囲
    - マイグレーション戦略
```

#### 作成タイミング
```yaml
when_to_create_adr:
  technology_selection:
    - 新しいフレームワーク・ライブラリの採用
    - データベースの選定
    - クラウドプロバイダーの選択
  
  architecture_changes:
    - マイクロサービス化
    - API設計方針の変更
    - データ構造の大幅変更
  
  significant_patterns:
    - 認証・認可方式の決定
    - キャッシング戦略
    - エラーハンドリング方針
  
  trade_offs:
    - パフォーマンスvs複雑性
    - コストvs機能性
    - 短期vs長期の戦略
```

#### ステークホルダー
- **作成者**: アーキテクト、テックリード
- **レビュアー**: エンジニアリングチーム、CTO
- **利用者**: 全エンジニア、新メンバー

#### 成功基準
- [ ] 決定の背景が明確に説明されている
- [ ] 代替案が適切に検討されている
- [ ] 決定理由が論理的に説明されている
- [ ] トレードオフが明示されている
- [ ] 影響範囲が明確である
- [ ] 将来の参照に耐える内容である

---

### 4. API仕様書 (API Specification)

#### 概要
APIのエンドポイント、リクエスト・レスポンス形式、認証方法などを定義する技術仕様書。

#### 主要セクション
```yaml
sections:
  overview:
    - API概要
    - ベースURL
    - バージョニング戦略
    - 認証方式
  
  endpoints:
    - HTTPメソッド
    - パス
    - パラメータ（パス、クエリ、ボディ）
    - リクエスト例
    - レスポンス例
    - ステータスコード
    - エラーレスポンス
  
  data_models:
    - リクエスト・レスポンスのスキーマ
    - データ型定義
    - バリデーションルール
  
  authentication:
    - 認証方式（JWT、OAuth2等）
    - トークン取得方法
    - トークンのライフサイクル
  
  error_handling:
    - エラーコード一覧
    - エラーメッセージフォーマット
    - エラーハンドリング方針
  
  rate_limiting:
    - レート制限の仕様
    - クォータ
  
  examples:
    - 一般的なユースケース
    - コードサンプル
```

#### フォーマット
- **推奨**: OpenAPI (Swagger) 3.0
- **代替**: Markdown形式

#### 作成タイミング
- API設計完了時
- 実装開始前
- API変更時（バージョンアップ）

#### ステークホルダー
- **作成者**: APIエンジニア
- **レビュアー**: テックリード、フロントエンドチーム
- **利用者**: API利用者（内部・外部）

#### 成功基準
- [ ] すべてのエンドポイントが文書化されている
- [ ] リクエスト・レスポンスの例が提供されている
- [ ] 認証方法が明確に説明されている
- [ ] エラーハンドリングが定義されている
- [ ] バージョニング戦略が明確である
- [ ] 実装前にレビュー・承認されている

---

### 5. 設計レビュー記録 (Design Review)

#### 概要
設計レビュー会議の結果を記録し、フィードバックとアクションアイテムを管理するドキュメント。

#### 主要セクション
```yaml
sections:
  review_info:
    - レビューID
    - 日時
    - 参加者
    - レビュー対象
  
  objectives:
    - レビューの目的
    - レビュー範囲
  
  design_summary:
    - 設計の概要
    - 主要なアーキテクチャ決定
  
  review_checklist:
    - アーキテクチャ
    - セキュリティ
    - パフォーマンス
    - テスト
    - 運用
    - コスト
  
  comments:
    - レビュアーごとのコメント
    - 指摘事項
    - 改善提案
  
  action_items:
    - 対応が必要な項目
    - 担当者
    - 期限
  
  outcome:
    - 承認/条件付き承認/要修正/却下
    - サマリー
    - 次のステップ
```

#### 作成タイミング
- 設計レビュー会議の実施時
- 重要なマイルストーンでのレビュー

#### ステークホルダー
- **作成者**: レビュー主催者、テックリード
- **参加者**: レビュアー全員
- **利用者**: プロジェクトチーム全員

#### 成功基準
- [ ] レビュー結果が明確に記録されている
- [ ] すべてのフィードバックが文書化されている
- [ ] アクションアイテムに担当者と期限が設定されている
- [ ] 承認/却下の理由が明確である
- [ ] フォローアップ計画が策定されている

---

## 成果物の作成フロー

### 標準的な作成フロー

```
設計フェーズの標準フロー:

1. プロジェクト開始
   │
   ├─ 要件確認
   ├─ 技術調査
   └─ 初期見積もり
   │
   ▼
2. 技術提案書作成
   │
   ├─ 技術アプローチ検討
   ├─ 代替案評価
   ├─ コスト・ROI分析
   └─ 【成果物】Technical Proposal
   │
   ▼
3. 技術提案レビュー
   │
   ├─ ステークホルダーレビュー
   ├─ フィードバック反映
   └─ 承認取得
   │
   ▼
4. 詳細設計
   │
   ├─ アーキテクチャ設計
   ├─ API設計
   ├─ データモデル設計
   ├─ 【成果物】Design Document
   ├─ 【成果物】API Specification
   └─ 【成果物】ADR (重要な決定時)
   │
   ▼
5. 設計レビュー
   │
   ├─ レビュー会議実施
   ├─ 【成果物】Design Review Record
   ├─ フィードバック対応
   └─ 最終承認
   │
   ▼
6. 実装フェーズへ移行
   │
   └─ 設計ドキュメント引き渡し
```

### プロジェクト規模別フロー

#### 小規模プロジェクト（～1ヶ月）
```yaml
deliverables:
  mandatory:
    - 設計書 (簡易版)
  optional:
    - ADR (重要な決定がある場合)
    - API仕様書 (API開発がある場合)

timeline:
  week_1:
    - 要件確認
    - 設計書作成（簡易版）
    - レビュー
  week_2:
    - 実装開始
```

#### 中規模プロジェクト（1～3ヶ月）
```yaml
deliverables:
  mandatory:
    - 技術提案書
    - 設計書
    - ADR
  recommended:
    - API仕様書
    - 設計レビュー記録

timeline:
  week_1-2:
    - 技術提案書作成
    - 承認取得
  week_3-4:
    - 詳細設計
    - API設計
    - レビュー
  week_5:
    - 修正対応
    - 実装開始
```

#### 大規模プロジェクト（3ヶ月～）
```yaml
deliverables:
  mandatory:
    - 技術提案書
    - 設計書（複数モジュール）
    - ADR（複数）
    - API仕様書
    - 設計レビュー記録
  recommended:
    - セキュリティ評価書
    - パフォーマンステスト計画
    - マイグレーション計画書
    - データベーススキーマ設計書

timeline:
  month_1:
    - 技術提案書作成
    - 承認取得
    - ハイレベル設計
  month_2:
    - 詳細設計（フェーズ1）
    - API設計
    - データモデル設計
    - レビュー
  month_3:
    - 詳細設計（フェーズ2）
    - セキュリティ・パフォーマンス設計
    - 最終レビュー
    - 実装開始
```

---

## 品質基準

### 成果物の品質チェックリスト

#### 技術提案書
```yaml
quality_criteria:
  completeness:
    - [ ] 問題定義が明確
    - [ ] ソリューションが具体的
    - [ ] コスト見積もりが現実的
    - [ ] ROIが定量的に示されている
    - [ ] リスクと緩和策が網羅的
  
  clarity:
    - [ ] 専門用語が適切に説明されている
    - [ ] 図表が効果的に使用されている
    - [ ] 非技術者にも理解可能
  
  validity:
    - [ ] 技術的実現可能性が検証済み
    - [ ] 代替案が適切に評価されている
    - [ ] ステークホルダーの承認済み
```

#### 設計書
```yaml
quality_criteria:
  completeness:
    - [ ] アーキテクチャが明確に定義されている
    - [ ] データモデルが完全
    - [ ] インターフェースが明確
    - [ ] 非機能要件が考慮されている
    - [ ] テスト戦略が定義されている
  
  implementability:
    - [ ] 実装に必要な情報が十分
    - [ ] 疑似コードや具体例が提供されている
    - [ ] 技術的詳細が適切
  
  maintainability:
    - [ ] 図が最新の状態
    - [ ] バージョン管理されている
    - [ ] 変更履歴が記録されている
```

#### ADR
```yaml
quality_criteria:
  clarity:
    - [ ] 決定の背景が明確
    - [ ] 決定内容が簡潔
    - [ ] 影響範囲が明示されている
  
  rationale:
    - [ ] 決定理由が論理的
    - [ ] トレードオフが説明されている
    - [ ] 代替案との比較が適切
  
  longevity:
    - [ ] 将来の参照に耐える内容
    - [ ] ステータスが明確
    - [ ] 関連ADRとの関係が示されている
```

### レビュー基準

#### 設計レビューのチェックポイント

```yaml
review_checklist:
  architecture:
    - [ ] システム構成は要件を満たすか
    - [ ] コンポーネント分割は適切か
    - [ ] 依存関係は明確か
    - [ ] スケーラビリティは考慮されているか
    - [ ] 保守性は十分か
  
  security:
    - [ ] 認証・認可は適切に設計されているか
    - [ ] データ保護は十分か
    - [ ] 既知の脆弱性対策がなされているか
    - [ ] セキュリティテストが計画されているか
  
  performance:
    - [ ] パフォーマンス要件が明確か
    - [ ] ボトルネックは特定されているか
    - [ ] キャッシュ戦略は適切か
    - [ ] 負荷テストが計画されているか
  
  testing:
    - [ ] テスト戦略は包括的か
    - [ ] カバレッジ目標は適切か
    - [ ] テスト環境は準備されているか
  
  operations:
    - [ ] 監視計画は十分か
    - [ ] アラート設定は適切か
    - [ ] ログ管理は考慮されているか
    - [ ] デプロイメント戦略は明確か
    - [ ] ロールバック計画はあるか
  
  cost:
    - [ ] コスト見積もりは妥当か
    - [ ] コスト最適化が考慮されているか
    - [ ] 予算内に収まっているか
```

---

## レビュープロセス

### レビューのタイプ

#### 1. ピアレビュー (Peer Review)
```yaml
peer_review:
  timing: "ドキュメント作成中～完成直後"
  reviewers:
    - 同僚エンジニア（1-2名）
  focus:
    - 技術的正確性
    - 実装可能性
    - コードとの整合性
  duration: "1-2時間"
  format: "非同期レビュー（GitHub PR等）"
```

#### 2. 設計レビュー (Design Review)
```yaml
design_review:
  timing: "設計書完成後"
  reviewers:
    - テックリード
    - アーキテクト
    - 関連チームのリード
    - セキュリティチーム（必要に応じて）
  focus:
    - アーキテクチャの妥当性
    - セキュリティ
    - パフォーマンス
    - 保守性
  duration: "1-2時間のミーティング"
  format: "対面またはオンライン会議"
```

#### 3. 承認レビュー (Approval Review)
```yaml
approval_review:
  timing: "実装開始前"
  reviewers:
    - エンジニアリングマネージャー
    - CTO（大規模プロジェクト）
    - プロダクトマネージャー
  focus:
    - ビジネス価値
    - リソース配分
    - リスク
    - ROI
  duration: "30分-1時間"
  format: "対面またはオンライン会議"
```

### レビューフロー

```
レビューフロー:

1. ドキュメント作成
   │
   ├─ 初稿作成
   ├─ セルフレビュー
   └─ レビュー依頼
   │
   ▼
2. ピアレビュー
   │
   ├─ 同僚エンジニアによるレビュー
   ├─ フィードバック対応
   └─ 技術的正確性の確認
   │
   ▼
3. 設計レビュー会議
   │
   ├─ レビュー会議実施
   ├─ 指摘事項の記録
   ├─ アクションアイテムの設定
   └─ 条件付き承認 or 要修正
   │
   ▼
4. 修正対応
   │
   ├─ フィードバック反映
   ├─ 再レビュー（必要に応じて）
   └─ 修正完了確認
   │
   ▼
5. 最終承認
   │
   ├─ 承認者によるレビュー
   ├─ 承認 or 差し戻し
   └─ 承認済みドキュメントの保管
   │
   ▼
6. 実装フェーズへ
```

### レビューコメントの書き方

#### 効果的なフィードバック
```yaml
good_feedback:
  specific:
    bad: "このアーキテクチャは良くない"
    good: "このアーキテクチャでは、サービスAとBの結合が強すぎます。
           インターフェースを定義して疎結合にすることを推奨します。"
  
  constructive:
    bad: "なぜこの技術を選んだのか理解できない"
    good: "この技術選定の理由を追記してください。
           特に、代替案Xと比較してどのような利点があるかを明確にすると良いです。"
  
  actionable:
    bad: "セキュリティが心配"
    good: "認証フローにCSRF対策が含まれていません。
           トークンベースのCSRF対策を追加してください。"
  
  balanced:
    - ポジティブな点も指摘する
    - 改善点は具体的に
    - 学びの機会として捉える
```

---

## ベストプラクティス

### ドキュメント作成のベストプラクティス

#### 1. 図を効果的に使う
```yaml
diagrams:
  architecture_diagrams:
    - システム全体構成図
    - コンポーネント図
    - デプロイメント図
    tools:
      - draw.io
      - Mermaid
      - PlantUML
  
  sequence_diagrams:
    - データフロー
    - 処理シーケンス
    - 認証フロー
  
  data_diagrams:
    - ER図
    - データフロー図
    - 状態遷移図
  
  best_practices:
    - 複雑な文章説明より図を使う
    - 図は常に最新に保つ
    - 図にコンテキストを付ける
    - バージョン管理する
```

#### 2. 具体例を提供する
```yaml
examples:
  code_samples:
    - API呼び出しの例
    - データ構造の例
    - 設定ファイルの例
  
  use_cases:
    - 典型的なユースケース
    - エッジケース
    - エラーケース
  
  data_examples:
    - リクエスト例
    - レスポンス例
    - データサンプル
```

#### 3. 段階的詳細化
```yaml
progressive_disclosure:
  level_1_executive:
    - 概要
    - 目標
    - 主要な決定
    audience: "経営層、プロダクトマネージャー"
  
  level_2_technical:
    - アーキテクチャ
    - 主要コンポーネント
    - データフロー
    audience: "テックリード、アーキテクト"
  
  level_3_implementation:
    - 詳細設計
    - 実装ガイドライン
    - コード例
    audience: "実装エンジニア"
```

#### 4. メンテナンス可能性
```yaml
maintainability:
  versioning:
    - セマンティックバージョニング
    - 変更履歴の記録
    - 最終更新日の明記
  
  ownership:
    - ドキュメントオーナーの明確化
    - 連絡先の記載
  
  reviews:
    - 定期的なレビュー（四半期ごと）
    - 陳腐化したドキュメントの更新
    - アーカイブ基準
  
  linking:
    - 関連ドキュメントへのリンク
    - 外部リソースへの参照
    - 相互参照の維持
```

### 設計フェーズのベストプラクティス

#### 1. 早期のプロトタイピング
```yaml
prototyping:
  when:
    - 新技術を採用する場合
    - アーキテクチャが複雑な場合
    - パフォーマンスが重要な場合
  
  benefits:
    - 技術的実現可能性の検証
    - 設計の妥当性確認
    - リスクの早期発見
    - チームの理解促進
  
  scope:
    - コア機能のみ
    - 本番品質不要
    - 学びを設計にフィードバック
```

#### 2. ステークホルダーの巻き込み
```yaml
stakeholder_engagement:
  early_involvement:
    - 設計の初期段階から関与
    - 要件の確認
    - 期待値の調整
  
  regular_updates:
    - 週次の進捗共有
    - 主要な決定時の相談
    - レビュー会議への参加
  
  feedback_loops:
    - 継続的なフィードバック
    - 方向性の確認
    - リスクの共有
```

#### 3. 段階的な設計
```yaml
incremental_design:
  phase_1:
    - ハイレベルアーキテクチャ
    - 主要コンポーネントの定義
    - 技術スタックの選定
  
  phase_2:
    - 詳細なコンポーネント設計
    - インターフェース定義
    - データモデル設計
  
  phase_3:
    - 実装詳細
    - エラーハンドリング
    - 監視・運用設計
  
  benefits:
    - 早期のフィードバック
    - リスクの段階的管理
    - 柔軟な方向転換
```

#### 4. 設計の検証
```yaml
design_validation:
  technical_validation:
    - プロトタイプによる検証
    - パフォーマンステスト
    - セキュリティレビュー
  
  business_validation:
    - 要件との整合性確認
    - ROIの再評価
    - リスク評価
  
  team_validation:
    - 実装チームとの設計ウォークスルー
    - 実装可能性の確認
    - 懸念事項の収集
```

### アンチパターンと回避策

#### 避けるべき失敗パターン
```yaml
anti_patterns:
  over_engineering:
    problem: "不要に複雑な設計"
    symptoms:
      - 過度な抽象化
      - 使われない拡張ポイント
      - 実装が困難
    solution:
      - YAGNIの原則（You Aren't Gonna Need It）
      - 現在の要件に焦点を当てる
      - シンプルさを優先
  
  under_design:
    problem: "設計が不十分"
    symptoms:
      - 重要な決定が文書化されていない
      - 非機能要件が考慮されていない
      - 実装時に多くの設計変更
    solution:
      - レビューチェックリストの活用
      - 十分な設計時間の確保
      - ステークホルダーからのフィードバック
  
  design_by_committee:
    problem: "委員会による設計"
    symptoms:
      - 意思決定が遅い
      - 妥協的な設計
      - 一貫性の欠如
    solution:
      - 明確な意思決定者の設定
      - 決定のためのフレームワーク
      - タイムボックス
  
  documentation_debt:
    problem: "ドキュメントの負債"
    symptoms:
      - 実装とドキュメントの乖離
      - 古いドキュメント
      - 更新されない図
    solution:
      - 実装と同時にドキュメント更新
      - 定期的なレビュー
      - ドキュメントのCI/CD
  
  bikeshedding:
    problem: "些細な議論に時間を費やす"
    symptoms:
      - 重要でない詳細に時間をかける
      - 本質的な問題が後回し
      - 会議が長引く
    solution:
      - 議論の優先順位付け
      - タイムボックス
      - 後で決定できることを識別
```

---

## 付録

### テンプレートの場所

```yaml
template_locations:
  path: "/devin-organization-standards/08-templates/"
  
  design_templates:
    technical_proposal: "technical-proposal-template.md"
    design_document: "design-document-template.md"
    design_review: "design-review-template.md"
    adr: "adr-template.md"
    api_specification: "api-specification-template.md"
    api_documentation: "api-documentation-template.md"
```

### 関連ドキュメント

```yaml
related_documents:
  process:
    - "/devin-organization-standards/03-development-process/README.md"
    - "/devin-organization-standards/03-development-process/documentation-standards.md"
  
  architecture:
    - "/devin-organization-standards/02-architecture-standards/design-principles.md"
    - "/devin-organization-standards/02-architecture-standards/api-architecture.md"
  
  quality:
    - "/devin-organization-standards/04-quality-standards/testing-strategy.md"
    - "/devin-organization-standards/04-quality-standards/code-review-standards.md"
```

### 用語集

```yaml
glossary:
  ADR:
    full_name: "Architecture Decision Record"
    definition: "重要なアーキテクチャ決定とその理由を記録したドキュメント"
  
  API Specification:
    definition: "APIのエンドポイント、リクエスト・レスポンス形式を定義した技術仕様書"
  
  Design Document:
    definition: "システムやモジュールの詳細な技術設計を記述したドキュメント"
  
  Technical Proposal:
    definition: "技術的アプローチを提案し承認を得るための包括的なドキュメント"
  
  Design Review:
    definition: "設計の妥当性を確認するためのレビューミーティング"
```

---

## 変更履歴

| バージョン | 日付 | 変更者 | 変更内容 |
|----------|------|--------|---------|
| 1.0.0 | 2025-11-11 | Engineering Team | 初版作成 |

---

## 承認

```yaml
approvals:
  - role: "Engineering Manager"
    name: "[Name]"
    date: "2025-11-11"
    status: "Pending"
  
  - role: "Tech Lead"
    name: "[Name]"
    date: "2025-11-11"
    status: "Pending"
```

---

**注意**: このドキュメントは設計フェーズにおける成果物を整理したガイドラインです。プロジェクトの規模や性質に応じて、柔軟に適用してください。
