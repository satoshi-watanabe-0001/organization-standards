---
version: "1.0.0"
last_updated: "2025-11-12 (Phase 2A リネーム版)"
status: "draft"
owner: "Engineering Team"
category: "phase-guide"
phase: "2A"
---

# Phase 2A: 実装前設計ガイド / Pre-Implementation Design Guide

**📂 重要**: 成果物の格納場所、命名規則、管理方法については以下を参照：
- 🔴 **必須**: [`../../03-development-process/design-artifacts-management-guide.md`](../../03-development-process/design-artifacts-management-guide.md)

---

## 📋 目次

1. [フェーズ概要](#フェーズ概要)
2. [開始条件](#開始条件)
3. [成果物詳細](#成果物詳細)
4. [作業ステップ](#作業ステップ)
5. [完了基準](#完了基準)
6. [よくある質問](#よくある質問)

---

## フェーズ概要

### 目的

実装開始前に**必要最小限の設計**を行い、チームが安全に実装を開始できる状態を作る。

---

## ⚠️ 禁止事項チェック

### このフェーズ特有の禁止事項
- **過剰詳細設計**: 実装前に全詳細を記述してはいけない(完全版はPhase 2Bで作成)
- **実装の先行**: API設計が固まる前に実装を始めてはいけない
- **形式違反**: OpenAPI 3.0以外の形式でAPI仕様を記述してはいけない
- **ステークホルダー確認不足**: レビューやフィードバックなしで設計を確定してはいけない

### 全フェーズ共通禁止事項
- **CI/CD設定の無断変更**: 明示的な指示がない限り、GitHub Actions、CircleCI、Jenkins等のCI/CD設定ファイルを変更してはいけない
- **本番環境への直接変更**: 本番データベース、本番サーバー、本番設定ファイルを直接変更してはいけない
- **セキュリティ設定の独断変更**: 認証・認可・暗号化等のセキュリティ設定を独自判断で変更してはいけない
- **組織標準外技術の無断導入**: プロジェクトで使用していない新しいライブラリ・フレームワーク・言語を独断で導入してはいけない
- **プロジェクト構造の大幅変更**: ディレクトリ構造、命名規則、アーキテクチャパターンを独断で大きく変更してはいけない

### 📚 詳細確認
禁止事項の詳細、具体例、例外ケースについては以下のドキュメントを参照してください：
- [AI開発タスクの禁止事項](../../01-coding-standards/ai-task-prohibitions.md)
- [禁止事項チェックリスト](../../01-coding-standards/ai-task-prohibitions-checklist.md)

---

### 重要な原則

```yaml
principles:
  lightweight:
    description: "軽量であること"
    guideline: "完璧を目指さない。80%の完成度で十分"
    
  decision_focused:
    description: "決定事項に集中"
    guideline: "詳細な設計は不要。重要な決定のみ記録"
    
  timebox:
    description: "時間制限"
    guideline: "最大2日。それ以上かけない"
    
  enabler:
    description: "実装の促進"
    guideline: "実装をブロックしない。不明点は実装中に解決"
```

### 期間とリソース

```yaml
duration:
  target: "1-2日"
  maximum: "3日"
  
resources:
  required:
    - テックリード or シニアエンジニア: 1名
    - (オプション) アーキテクト: 0.5名
  
  effort:
    total: "8-16時間"
```

---

## 開始条件

### 前提条件チェックリスト

```yaml
prerequisites:
  phase_0_completed:
    - [ ] 要件分析が完了している
    - [ ] ビジネス要件が明確である
    - [ ] 非機能要件が定義されている
  
  phase_1_completed:
    - [ ] プロジェクトリポジトリが準備されている
    - [ ] 開発環境がセットアップされている
    - [ ] チームアサインが完了している
  
  ready_to_start:
    - [ ] テックリードが決定している
    - [ ] 技術スタックの候補が絞り込まれている
    - [ ] 実装チームが待機状態である
```

### インプット

```yaml
inputs:
  from_phase_0:
    - 要件定義書
    - ユーザーストーリー
    - 非機能要件
    - リスク評価
    - 技術調査結果
  
  from_stakeholders:
    - ビジネス制約
    - 予算・スケジュール制約
    - セキュリティ要件
    - コンプライアンス要件
```

---

## 成果物詳細

### 1. ADR (Architecture Decision Record)

#### 概要
重要な技術決定とその理由を記録する軽量ドキュメント。

#### テンプレート
`/devin-organization-standards/08-templates/adr-template.md`

#### 作成対象の決定事項

```yaml
必須記録事項:
  technology_stack:
    - プログラミング言語の選択
    - フレームワークの選択
    - データベースの選択
    - クラウドプロバイダーの選択
  
  architecture_patterns:
    - アーキテクチャスタイル (モノリス/マイクロサービス/サーバーレス)
    - 通信パターン (REST/GraphQL/gRPC)
    - 認証・認可方式
    - データ永続化戦略
  
  infrastructure:
    - ホスティング環境
    - CI/CDツール
    - 監視・ログツール

条件付き記録事項:
  significant_tradeoffs:
    - パフォーマンス vs 複雑性
    - コスト vs 機能
    - 短期 vs 長期戦略
  
  risky_decisions:
    - 新しい技術の採用
    - 大きな技術的賭け
    - 実証されていないアプローチ
```

#### 作成例

```markdown
# ADR-001: プログラミング言語にTypeScriptを選択

## ステータス
Accepted

## コンテキスト
新規マイクロサービスのバックエンド開発で言語を選択する必要がある。
チームは Node.js の経験があり、型安全性を重視したい。

## 決定
TypeScript を採用する。

## 理由
1. 型安全性により、大規模開発でもバグを減らせる
2. チームが Node.js に精通しており、学習コストが低い
3. 豊富なライブラリエコシステム
4. VS Code との統合が優れている

## 代替案
- Go: パフォーマンスは優れるが、学習コストが高い
- Python: 開発速度は速いが、型システムが弱い

## 影響
- ポジティブ: 型安全性、開発効率
- ネガティブ: ビルド時間の増加、設定の複雑性
```

#### 作成時間
- **1つの決定**: 30-60分
- **プロジェクト全体**: 2-4時間 (3-5個のADR)

---

### 2. API契約書 (API Contract)

#### 概要
チーム間またはサービス間の契約を定義する軽量ドキュメント。

#### テンプレート
`/devin-organization-standards/08-templates/api-contract-template.md` (新規作成)

#### 含めるべき内容

```yaml
minimal_content:
  endpoints_list:
    description: "エンドポイント一覧"
    detail_level: "パスとHTTPメソッドのみ"
    example: "GET /api/v1/users, POST /api/v1/users"
  
  request_structure:
    description: "リクエストの基本構造"
    detail_level: "主要フィールドのみ"
    example: "{ userId: string, name: string }"
  
  response_structure:
    description: "レスポンスの基本構造"
    detail_level: "主要フィールドのみ"
    example: "{ id: string, name: string, createdAt: string }"
  
  authentication:
    description: "認証方式"
    detail_level: "方式名とトークン配置場所"
    example: "JWT in Authorization header"

optional_content:
  error_responses:
    description: "主要なエラーレスポンス"
    detail_level: "ステータスコードと形式"
  
  rate_limiting:
    description: "レート制限"
    detail_level: "制限値のみ"
```

#### 作成例

```markdown
# API契約書: ユーザーサービス

## ベース情報
- ベースURL: `https://api.example.com/v1`
- 認証: JWT (Authorization: Bearer {token})
- Content-Type: application/json

## エンドポイント一覧

### ユーザー管理

| メソッド | パス | 概要 |
|---------|------|------|
| GET | /users | ユーザー一覧取得 |
| GET | /users/{id} | ユーザー詳細取得 |
| POST | /users | ユーザー作成 |
| PUT | /users/{id} | ユーザー更新 |
| DELETE | /users/{id} | ユーザー削除 |

## リクエスト/レスポンス (基本構造)

### POST /users (ユーザー作成)

**Request:**
```json
{
  "email": "string",
  "name": "string",
  "role": "string"
}
```

**Response (201):**
```json
{
  "id": "string",
  "email": "string",
  "name": "string",
  "role": "string",
  "createdAt": "string"
}
```

**Error (400):**
```json
{
  "error": "string",
  "message": "string"
}
```

## 注意事項
- 詳細なバリデーションルールは実装時に定義
- ページネーションの仕様は実装時に決定
```

#### 作成時間
- **単一サービス**: 2-4時間
- **複数サービス連携**: 4-6時間

---

### 3. 制約条件文書 (Constraints Document)

#### 概要
セキュリティ、パフォーマンス、技術制約を明確化するドキュメント。

#### テンプレート
`/devin-organization-standards/08-templates/constraints-template.md` (新規作成)

#### 含めるべき内容

```yaml
security_constraints:
  authentication:
    - 認証方式の指定
    - トークンのライフタイム
    - セッション管理方針
  
  authorization:
    - ロールベースアクセス制御
    - リソースレベルの権限
  
  data_protection:
    - 個人情報の扱い
    - 暗号化要件
    - データ保持期間
  
  compliance:
    - GDPR, CCPA等の対応
    - 業界標準への準拠

performance_constraints:
  latency:
    - API レスポンスタイム目標
    - ページロード時間目標
  
  throughput:
    - 秒間リクエスト数
    - 同時接続数
  
  scalability:
    - 最大ユーザー数
    - データ量の想定

technical_constraints:
  infrastructure:
    - 使用可能なクラウドサービス
    - リージョン制限
    - ネットワーク制約
  
  dependencies:
    - 外部サービスの制約
    - レガシーシステムとの互換性
  
  budget:
    - インフラコスト上限
    - サードパーティサービス費用
```

#### 作成例

```markdown
# 制約条件文書: ECサイト決済サービス

## セキュリティ制約

### 認証・認可
- JWT認証 (有効期限: 1時間)
- リフレッシュトークン (有効期限: 30日)
- RBAC: admin, user, guest

### データ保護
- クレジットカード情報: PCI DSS準拠
- 個人情報: GDPR対応 (EU顧客向け)
- データ暗号化: AES-256 (保存時), TLS 1.3 (通信時)

## パフォーマンス制約

### レイテンシ目標
- API レスポンス: P95 < 200ms, P99 < 500ms
- 決済処理: < 3秒

### スループット
- ピーク時: 1,000 req/sec
- 同時接続: 10,000

### スケーラビリティ
- 想定ユーザー: 100万人
- 1日のトランザクション: 10万件

## 技術制約

### インフラ
- クラウド: AWS (ap-northeast-1)
- データベース: PostgreSQL 14以上
- キャッシュ: Redis 7以上

### 外部依存
- 決済ゲートウェイ: Stripe
- レスポンスタイム依存: < 1秒

### 予算
- インフラ月額: $5,000以内
- Stripe手数料: 売上の3.6%
```

#### 作成時間
- **標準的プロジェクト**: 1-2時間

---

## 作業ステップ

### Day 1: 技術決定とADR作成

#### ステップ1: 技術スタックの確定 (2時間)

```yaml
activities:
  review_requirements:
    - 要件定義書のレビュー
    - 非機能要件の確認
    - 制約条件の整理
  
  evaluate_options:
    - 技術候補の比較
    - トレードオフ分析
    - チームスキルセットの考慮
  
  make_decisions:
    - プログラミング言語
    - フレームワーク
    - データベース
    - アーキテクチャパターン
```

#### ステップ2: ADRの作成 (2時間)

```yaml
activities:
  create_adrs:
    - 各重要決定に対して1つのADR
    - テンプレートを使用
    - 決定理由を明確に記述
    - 代替案を記録
  
  review:
    - セルフレビュー
    - (可能なら)ピアレビュー
```

#### ステップ3: アーキテクチャ概要図の作成 (1-2時間)

```yaml
activities:
  create_diagrams:
    - システム全体構成図 (1図)
    - 主要コンポーネント (2-3個)
    - データフロー (シンプルな矢印)
  
  tools:
    - Diagrams.net (draw.io)
    - Mermaid (コードでの図作成)
    - Excalidraw (手書き風)
```

---

### Day 2: API契約と制約条件

#### ステップ4: API契約書の作成 (2-3時間)

```yaml
activities:
  identify_interfaces:
    - 内部API一覧
    - 外部API一覧
    - サービス間通信
  
  define_contracts:
    - エンドポイント一覧
    - リクエスト/レスポンス基本構造
    - 認証方式
  
  stakeholder_alignment:
    - フロントエンドチームとの確認
    - 他サービスオーナーとの確認
```

#### ステップ5: 制約条件文書の作成 (1-2時間)

```yaml
activities:
  security_requirements:
    - 認証・認可方式
    - データ保護要件
    - コンプライアンス
  
  performance_targets:
    - レイテンシ目標
    - スループット目標
    - スケーラビリティ要件
  
  technical_constraints:
    - インフラ制約
    - 外部依存
    - 予算制約
```

#### ステップ6: レビューと承認 (1-2時間)

```yaml
activities:
  self_review:
    - 完了チェックリストで確認
    - 不足情報の補完
  
  peer_review:
    - テックリードレビュー
    - アーキテクトレビュー (大規模プロジェクト)
  
  stakeholder_approval:
    - エンジニアリングマネージャー承認
    - (必要に応じて) CTO承認
```

---

## 完了基準

### 必須チェックリスト

```yaml
deliverables_completed:
  - [ ] ADRが作成され、重要な技術決定が記録されている
  - [ ] API契約書が作成され、関係チームと合意されている
  - [ ] 制約条件文書が作成され、要件が明確である
  - [ ] アーキテクチャ概要図が作成されている (推奨)

quality_criteria:
  - [ ] ADRは「なぜその選択か」が明確に説明されている
  - [ ] API契約は実装チームが理解できる
  - [ ] 制約条件は測定可能である
  - [ ] ドキュメントは簡潔である (合計10ページ以内)

team_readiness:
  - [ ] 実装チームが設計を理解している
  - [ ] 不明点が許容範囲内である
  - [ ] 必要なツール・環境が準備されている
  - [ ] タスク分解が可能な状態である

approval:
  - [ ] テックリードの承認を得ている
  - [ ] ステークホルダーの承認を得ている (必要な場合)
```

### Go/No-Go判断

```yaml
go_criteria:
  必須:
    - 主要な技術決定が完了している
    - チーム間の契約が合意されている
    - セキュリティ要件が明確である
    - 実装チームがGo判断できる
  
  許容:
    - 一部の詳細が未定 (実装中に決定可能)
    - パフォーマンス目標が概算値
    - 細かい実装詳細が不明

no_go_criteria:
  - 技術スタックが決まっていない
  - セキュリティ要件が不明確
  - 複数チーム間の契約が未合意
  - 実装ブロッカーが存在する
```

---

## よくある質問

### Q1: Phase 2Aで完璧な設計が必要ですか?

**A**: いいえ。80%の完成度で十分です。以下を優先:
- 重要な技術決定
- チーム間の契約
- セキュリティ・パフォーマンス要件

詳細な設計は Phase 2-B (実装後) で行います。

---

### Q2: ADRはいくつ作成すべきですか?

**A**: プロジェクト規模による:
- **小規模 (1-2週間)**: 1-2個 (技術スタック選定のみ)
- **中規模 (1-2ヶ月)**: 3-5個
- **大規模 (3ヶ月以上)**: 5-10個

全ての決定をADRにする必要はありません。**重要な決定のみ**記録します。

---

### Q3: API契約書と Phase 2-B の API仕様書の違いは?

**A**:
- **Phase 2A (API契約書)**: 
  - エンドポイント一覧
  - 基本的な入出力
  - 認証方式
  - **目的**: チーム間の契約合意
  
- **Phase 2-B (API仕様書)**:
  - 全パラメータの詳細
  - バリデーションルール
  - エラーハンドリング
  - コード例
  - **目的**: 完全なAPI仕様の文書化

---

### Q4: 2日以内に完了しない場合は?

**A**: タイムボックスを守ることが重要です:

1. **優先順位付け**: 必須成果物に集中
2. **スコープ削減**: 詳細を Phase 2-B に延期
3. **ヘルプ要請**: アーキテクトやテックリードに相談
4. **最終判断**: 3日目に Go/No-Go を決定

完璧を求めず、実装を開始することを優先します。

---

### Q5: 制約条件が変わった場合は?

**A**: Phase 2A の成果物は**生きたドキュメント**です:
- 実装中に変更があれば随時更新
- 大きな変更は ADR を追加
- Phase 2-B で最終版に整理

---

### Q6: スキップできる場合は?

**A**: 以下の場合はスキップ可能:

**スキップ可能**:
- 既存システムの小規模バグ修正
- 単一エンジニアでの作業
- 技術スタックが完全に確定済み
- 1週間未満の作業

**スキップ不可**:
- 複数チーム連携
- 新規マイクロサービス
- セキュリティクリティカル
- データモデル変更

---

## テンプレート所在

```yaml
templates:
  adr:
    location: "/devin-organization-standards/08-templates/adr-template.md"
    status: "既存"
  
  api_contract:
    location: "/devin-organization-standards/08-templates/api-contract-template.md"
    status: "新規作成必要"
  
  constraints:
    location: "/devin-organization-standards/08-templates/constraints-template.md"
    status: "新規作成必要"
```

---

## 次のステップ

Phase 2A 完了後:

1. **Phase 3 (実装)** に進む
2. 実装中も Phase 2A の成果物を参照
3. 新たな決定事項があれば ADR を追加
4. Phase 4 完了後、Phase 2-B (詳細設計) を実施

---

**このガイドにより、効率的かつ効果的な実装前設計が可能になります。**

---

## 📋 関連チェックリスト

Phase 2Aを開始・完了する際は、以下のチェックリストを使用してください：

### Phase 2A開始前
- [Phase開始前チェックリスト](../../09-reference/checklists/phase-pre-work-checklist.md)

### Phase 2A完了時
- [Phase 2A 完了チェックリスト](../../09-reference/checklists/phase-2A-completion-checklist.md)

---
