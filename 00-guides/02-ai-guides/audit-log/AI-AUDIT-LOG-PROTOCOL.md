---
document_type: protocol_spec
target_audience:
  - 自律型AIランタイム／オーケストレータの開発者
  - 各サービス・バッチ・ツールの実装者
  - セキュリティ／品質／監査担当
priority: critical
scope: organization_wide_ai_audit_logging
version: 1.0
last_updated: 2026-03-04
related_documents:
  - AI-WORKLOG-ENFORCEMENT-GUIDE.md
  - AI-WORKLOG-IMPLEMENTATION-GUIDE.md
  - AICQ_AUDIT_LOG_SCHEMA.md
  - AICQ_Devin_reasoning_log.md
  - AICQ_METRICS_SPEC.md
---

## AI Audit Log Protocol（自律型AI監査ログプロトコル）

> **目的**: すべての自律型AI（Devin, Cursor, その他のAIエージェント）が、  
> 製品・ベンダー・プロンプト内容に依存せず、**同一の監査ログ仕様**で記録・保存されるようにする。
>
> **対象**: 「AIを利用するアプリケーション／サービス／バッチ」と  
> それらを制御する **ランタイム／オーケストレータ／SDK**。

---

## セクション1: プロトコルの基本モデル

### 1.1 セッションライフサイクル

本プロトコルは、AI利用を **セッション単位** で扱う。  
1セッションは、以下の3フェーズで構成される。

1. **START_SESSION**: 監査ログ対象となる作業コンテキストを開始する
2. **LOG_EVENT（0回以上）**: セッション中の重要な事実・判断・逸脱を記録する
3. **END_SESSION**: セッションを終了し、監査ログを確定・保存する

### 1.2 セッションIDと一意性

- すべてのセッションには、**一意な `session_id`** を付与すること。
- `session_id` は、少なくとも次の情報と紐づく必要がある。
  - プロジェクトID／リポジトリ
  - 呼び出し元サービス／バッチ名
  - 対象PBI／チケットID（存在する場合）

### 1.3 必須アウトプット

各セッションは、以下の2つを必ず生成する。

- **`messages`**
  - ユーザー指示・質問・回答・説明・人間の介入などの対話履歴。
  - 途中トリムや要約ではなく、**完全履歴**を保存すること。
- **`structured_output`**
  - 監査目的のために設計された **構造化JSON（AICQ Audit Log）**。
  - スキーマ定義は `AICQ_AUDIT_LOG_SCHEMA.md` を一次ソースとし、  
    `meta`, `requirements`, `definitions`, `steps`, `guidelines`, `deviations`, `checks`, `final` を必須とする。

> **重要**: 本プロトコルは、AI製品が Devin であるか、別製品であるかに依存しない。  
> ランタイム／SDK 側で `messages` と `structured_output` を取得・組み立てればよい。

---

## セクション2: START_SESSION（セッション開始）の要件

### 2.1 START_SESSION コマンド

ランタイム／SDK は、AIへの最初の実行前に必ず **START_SESSION** を実行すること。

**必須入力（例）**

- `project_id` / `repo` / `branch`
- 呼び出し元サービス名（例: `checkout-service`, `batch-daily-report`）
- セッションの目的（例: `feature-implementation`, `bugfix`, `test-generation`）
- 対象PBI／チケットID（存在する場合）
- 監査ログ保存先ベースパス（例: `/var/log/aicq/`）

**処理要件**

1. 一意な `session_id` を発行する。
2. `AICQ_AUDIT_LOG_SCHEMA.md` に従って `structured_output` を初期化する。
   - `meta` にセッションメタデータ（`session_id`, `project_id`, `run_id`, `created_at`, `agent` など）を設定。
   - `requirements` 等、他フィールドは空配列／空オブジェクトなどスキーマに沿った初期値を設定。
3. セッションフォルダを予約する（まだファイルは作らなくてもよい）。
   - ディレクトリ名は `AI-WORKLOG-ENFORCEMENT-GUIDE.md` に準拠し、原則として:
     - `aicq_session_{session_id}_{YYYYMMDD_HHMMSS}/`
4. 以降の AI 呼び出しに `session_id` を付与できるよう、ランタイム内部に保持する。

### 2.2 START_SESSION 完了チェック

START_SESSION は、次の条件を満たさない場合は **失敗** とみなし、AI実行を開始してはならない。

- [ ] 一意な `session_id` が発行されている
- [ ] `structured_output` が AICQ スキーマどおりに初期化されている
- [ ] セッションフォルダの保存先が決定している

---

## セクション3: LOG_EVENT（セッション中イベント）の要件

### 3.1 LOG_EVENT コマンド

ランタイム／SDK は、セッション中の重要な出来事を **LOG_EVENT** として記録する。  
イベントは最低限、以下のカテゴリを区別する。

- `fact`: 新しい事実を取得した（例: 仕様書を読んだ、ログを確認した）
- `hypothesis`: 仮説／前提を置いた・更新した・撤回した
- `decision`: 設計・実装・テスト方針などの重要な意思決定を行った
- `guideline_judgment`: ガイドラインに基づく判断（遵守／例外）
- `deviation`: ルールからの逸脱の兆候／発生／復帰
- `check`: テスト／検証／レビューなどのチェック結果

### 3.2 LOG_EVENT 入力の標準フォーマット

LOG_EVENT は少なくとも次の情報を受け取れるようにする。

- `event_type`: 上記カテゴリのいずれか
- `timestamp`: できる限り高精度な時刻
- `summary`: 1〜3行の要約テキスト
- `details`: 詳細説明（任意）
- `related_requirements`: 関連する要件IDの配列（例: `["REQ-001", "REQ-003"]`）
- `related_artifacts`: 関連するファイル／ドキュメント／URL の配列
- `actor`: `ai` / `human` / `system` など

### 3.3 structured_output へのマッピング

LOG_EVENT は、`AICQ_AUDIT_LOG_SCHEMA.md` に定義されたフィールドにマッピングされる。

例:

- `event_type == "fact"`  
  → `steps[].observations` などに追加
- `event_type == "hypothesis"`  
  → `requirements.assumptions[]` または `steps[].hypotheses` に追加
- `event_type == "decision"`  
  → `steps[].decisions[]` または `final.decisions[]` に追加
- `event_type == "guideline_judgment"`  
  → `guidelines.checks[]` に追加
- `event_type == "deviation"`  
  → `deviations.events[]` に追加
- `event_type == "check"`  
  → `checks.test_results[]` などに追加

> 正確な対応表は `AICQ_AUDIT_LOG_SCHEMA.md` の  
> 「イベント種別とフィールド対応表」を一次ソースとすること。

### 3.4 LOG_EVENT のタイミング

ランタイム／SDK は、少なくとも次のタイミングで LOG_EVENT を呼び出すべきである。

- セッション開始直後（初期要件理解を記録）
- 各フェーズ（要件整理／設計／実装／テスト／レビュー／デプロイ）の開始・完了時
- 新しい事実／仮説／重要な意思決定／逸脱／復帰が発生した時

---

## セクション4: END_SESSION（セッション終了）の要件

### 4.1 END_SESSION コマンド

ランタイム／SDK は、AI作業が完了したタイミングで **END_SESSION** を実行する。

**処理要件**

1. ランタイムから **`messages`** と **`structured_output`** を取得する。
2. セッションフォルダに、少なくとも以下のファイルを出力する。
   - `session_dump.json`: 元APIレスポンスなど、セッション全体の生データ（可能な範囲で）
   - `structured_output.json`: `structured_output` のみを抽出したJSON
   - `messages.jsonl`: `messages` を1行1メッセージ形式で保存したログ
3. `structured_output.json` を `AICQ_AUDIT_LOG_SCHEMA.md` の JSON Schema で検証する。
4. 検証結果をメタデータとして保存する（例: `validation_status: "ok" | "failed"`）。

### 4.2 END_SESSION 完了条件

END_SESSION は、次の条件を満たしたときにのみ「成功」とみなす。

- [ ] `messages` が保存されている（件数0は許容だが、キーが欠落してはならない）
- [ ] `structured_output` が保存されている
- [ ] JSON Schema 検証が実行され、結果が記録されている
- [ ] セッションフォルダ名とファイル命名が標準に従っている

検証に失敗したセッションは、`AICQ_METRICS_SPEC.md` におけるメトリクス算出の際に  
「監査ログ不備」として扱われる。

---

## セクション5: ランタイム／オーケストレータの責務

### 5.1 AIベンダー非依存の実装責務

本プロトコルは、AIベンダーや製品仕様に依存しない。  
ランタイム／オーケストレータは、以下を満たす責任を負う。

- すべてのAI呼び出しを **AuditLog クライアント／SDK** 経由に統一すること
- AI製品固有のプロンプト・Playbook・機能差は、**AuditLog クライアント内部で吸収** すること
- START_SESSION / LOG_EVENT / END_SESSION の各操作が、AI呼び出しの前後で必ず実行されるよう制御すること

### 5.2 環境契約（Environment Contract）

自律型AIを本番利用するランタイムは、少なくとも次を満たさなければならない。

- [ ] すべてのセッションが、本プロトコルの START_SESSION→LOG_EVENT→END_SESSION を通過する
- [ ] `messages` と `structured_output` が少なくとも日次バッチで集計可能な場所に保存される
- [ ] 監査ログ保存先と命名規則が `AI-WORKLOG-ENFORCEMENT-GUIDE.md` に準拠している
- [ ] JSON Schema 検証結果が、後続の集計・ダッシュボードから参照できる

---

## セクション6: 開発チーム・サービス実装者の責務

### 6.1 直接AI APIを呼ばないこと（標準ルール）

本プロトコルが想定する **標準状態（本番運用）** では、アプリケーションコード（サービス／バッチ／ツール）は次の行為を行ってはならない。

- AIモデルAPI（例: `/chat`, `/sessions` など）を **直接** 呼び出すこと
- アプリケーション独自の形式で `messages` や `structured_output` を保存しようとすること

代わりに、常に次を使用すること。

- 組織標準として提供される **AuditLog クライアント／SDK**

### 6.2 必須の連携情報

サービス実装者は、START_SESSION / LOG_EVENT を呼ぶ際に、少なくとも以下を渡さなければならない。

- 対象PBI／チケットID
- 対象機能／ドメイン（例: `user-login`, `inventory-sync`）
- 呼び出し元サービス名
- 環境（`dev`, `stg`, `prod` など）

これにより、後続の集計・監査レポートで「どのAIセッションが、どの機能・どのチケットに紐づくか」を追跡可能にする。

### 6.3 暫定運用: AuditLog クライアント未整備時の特例

AuditLog クライアント／SDK がまだ提供されていない期間に限り、  
**自律型AI自身がツール群（ファイル書き込み／HTTP 等）を用いて、本プロトコルと `AICQ_AUDIT_LOG_SCHEMA.md` に従い監査ログを直接出力する暫定運用**を認める。

この場合、自律型AI（またはそれを制御するオーケストレータ）は、少なくとも次を満たさなければならない。

- [ ] セッションごとに `session_id` を決定し、`AI-AUDIT-LOG-PROTOCOL.md` の START_SESSION 要件を満たす形で `structured_output` を初期化する
- [ ] セッション中の重要イベントごとに、LOG_EVENT 相当の更新を行い、`AICQ_AUDIT_LOG_SCHEMA.md` に従って `structured_output` を更新する
- [ ] セッション終了時に END_SESSION 処理として、次の3ファイルを **自らのツール操作で出力** する
  - `session_dump.json`
  - `structured_output.json`
  - `messages.jsonl`
- [ ] 可能な範囲で JSON Schema 検証を実行し、検証結果を記録する

> この暫定運用は、AuditLog クライアント／SDK が整備されるまでの **移行期間の措置** であり、  
> 本番運用ではセクション6.1の標準ルール（共通クライアント経由）に移行することを前提とする。

---

## セクション7: コンプライアンスとメトリクス

### 7.1 コンプライアンス判定の最低条件

本プロトコルへの準拠状況は、少なくとも次で評価する。

- **セッション網羅率**: AI利用セッションのうち、START_SESSION→END_SESSION が正常に完了している割合
- **スキーマ準拠率**: `structured_output` の JSON Schema 検証成功率
- **イベント充足率**: 必須イベントカテゴリごとの LOG_EVENT 出現率

詳細な定義は `AICQ_METRICS_SPEC.md` を参照のこと。

### 7.2 運用上の推奨

- CI や日次バッチで、本プロトコル準拠状況を定期的にチェックすること。
- 新しい自律型AI製品を導入する際は、まず **AuditLog クライアント／SDK との統合** を完了させてから本番利用を開始すること。

---

## セクション8: 位置づけと他ドキュメントとの関係

- 本ドキュメントは、**自律型AI全般に適用される「監査ログプロトコル仕様」** である。
- Devin 向けの具体的な Playbook／プロンプト設計は、`AICQ_Devin_reasoning_log.md` にて扱う。
- V3.0 監査ログ方式における実装・運用手順（STOP-GATE・命名規則など）は、
  - `AI-WORKLOG-ENFORCEMENT-GUIDE.md`
  - `AI-WORKLOG-IMPLEMENTATION-GUIDE.md`
  を参照すること。
- 監査ログJSONスキーマの詳細仕様は `AICQ_AUDIT_LOG_SCHEMA.md` を一次ソースとし、  
  メトリクス算出方法は `AICQ_METRICS_SPEC.md` を参照する。

