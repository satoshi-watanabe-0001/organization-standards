# AI-WORKLOG-ENFORCEMENT-GUIDE.md (Structured Output + Messages Mandatory)

---
document_type: mandatory_procedure
target_ai: all (Devin, Cursor, その他のAIエージェント)
priority: critical
enforcement_level: mandatory
version: 3.1
last_updated: 2026-03-10
changes: ログ方式を structured_output＋messages 必須（監査ログ方式）に置換。従来の Markdown ベースのログは任意。
related_documents:
  - AICQ_AUDIT_LOG_SCHEMA.md
  - AICQ_PLAYBOOK_TEMPLATE.md
  - AICQ_DATA_MODEL.md
  - AICQ_METRICS_SPEC.md
  - AICQ_METRICS_CALCULATION_GUIDE.md
  - AI-WORKLOG-GRANULARITY-GUIDE.md
  - AI-WORKLOG-IMPLEMENTATION-GUIDE.md
  - AI-AUDIT-LOG-QUICK-DECISION-GUIDE.md
  - AI-AUDIT-LOG-SCENARIO-COOKBOOK.md
  - AI-AUDIT-LOG-UPDATE-API-GUIDE.md
  - AI-AUDIT-LOG-SELF-CHECK-SCRIPT.md
  - AI-AUDIT-LOG-DECISION-ID-STANDARD.md

---

> 🚨 **【必須】作業開始前の絶対条件（V3.0 監査ログ方式）**
>
> **すべてのAIエージェントは作業開始前に必ず以下を実行すること：**
> 1. ユーザーに **監査ログ（structured_output＋messages）のエクスポート保存先** を確認・指定してもらう
> 2. **3段階 STOP-GATE** を完全実行（Gate 2/3 は structured_output＋messages 前提）
> 3. セッション中、**各フェーズにおいて作業のたびに** structured_output を **「関連が起きたら更新」** する（後述）。フェーズ開始・完了に加え、重要なサブタスク・意思決定のたびに記録し、漏れを許さない。
> 4. セッション完了後、**APIで messages と structured_output を回収・保存** する
>
> **structured_output＋messages を取得できない状態での作業開始は組織標準違反です。**

---

## セクション1: 概要と目的

### 1.1 目的
本ガイドは、自律型AI（例: Devin）が実施する開発作業について、**監査・品質評価に耐えるログ（監査ログ）を必ず取得できる状態を作る**ことを目的とします。監査ログの中核は、次の2つです。

- **messages**（ユーザー指示・質疑応答・説明などの対話履歴）
- **structured_output**（監査用に構造化された推論ログ相当）

> 注: 従来の Markdown ログファイル（worklog_*.md）は、本バージョンでは必須ではありません（任意）。

### 1.2 なぜ structured_output＋messages なのか
AIの内部CoT（隠れた思考）をそのまま取得できない状況でも、structured_output を監査目的で設計し、messages と合わせて回収することで、意思決定や逸脱の検出、品質メトリクス算出が可能になります。詳細設計は以下を参照してください：
スキーマ定義: AICQ_AUDIT_LOG_SCHEMA.md
データモデル: AICQ_DATA_MODEL.md
Playbook本文: AICQ_PLAYBOOK_TEMPLATE.md
メトリクス計算: AICQ_METRICS_SPEC.md, AICQ_METRICS_CALCULATION_GUIDE.md

### 1.3 監査ログの単位・粒度
監査ログの分割は **推論コンテキスト**を基準とします。

**基本原則**: 1つの推論コンテキスト = 1つの監査ログ（1セッション）

詳細は [AI-WORKLOG-GRANULARITY-GUIDE.md](./AI-WORKLOG-GRANULARITY-GUIDE.md) を参照してください。

### 1.4 AICQ 監査ログスキーマ準拠

本ガイドで要求する `structured_output` は、原則として  
`AICQ_AUDIT_LOG_SCHEMA.md` で定義される **「AICQ Audit Log (Phase1 Core)」スキーマ** に準拠しなければなりません。

- **必須要素**: `meta`, `requirements`, `definitions`, `steps`, `guidelines`, `deviations`, `checks`, `final`
- **拡張**: プロジェクト固有のフィールドを追加する場合は、上記必須要素を壊さない形で拡張フィールドを追加する（削除・型変更は禁止）

スキーマの完全定義およびイベント種別との対応表は  
`AICQ_AUDIT_LOG_SCHEMA.md` を一次ソースとして参照してください。

---

## セクション2: 作業開始前の必須チェック（STOP-GATE方式）

### 🛑 3段階ゲートシステム（全ステップクリア必須）

#### **GATE 1: 監査ログ（エクスポート）保存先の確認・指定**

**必須実行手順:**
1. ユーザーに保存先を質問
2. 保存先ディレクトリの存在・権限・容量を確認

**検証項目:**
- [ ] ユーザーから明確な保存先パスを取得した
- [ ] 指定ディレクトリの存在を確認した
- [ ] 書き込み権限があることを確認した
- [ ] 容量に問題がないことを確認した

**失格条件:** 保存先が「未定」「後で決める」「仮置き」の場合は **作業開始禁止**

#### **GATE 2: structured_output（監査ログJSON）の初期化（必須）**

**検証項目:**
- [ ] structured_output のスキーマを適用した（AICQ Audit Log）
- [ ] セッション開始直後に structured_output を初期化した
- [ ] 「関連が起きたら更新」の運用（Playbook）を指示に含めた

**失格条件:** structured_output を初期化できない／Playbookを適用できない場合は **作業開始禁止**

#### **GATE 3: 回収可能性の確立（messages＋structured_output のエクスポート計画）**

**検証項目:**
- [ ] 回収対象が合意されている（messages と structured_output）
- [ ] 回収方法が合意されている（例: `GET /v1/sessions/{session_id}`）
- [ ] 保存先（GATE 1）と命名規則（セクション4）が確定している

**失格条件:** 「回収しない」「保存先未確定」「命名規則未確定」は **作業開始禁止**

### ゲート突破確認宣言（コピペ用）

```text
【作業開始ゲート突破宣言（監査ログ方式）】
✅ GATE 1 CLEAR: エクスポート保存先確認完了（ユーザー指定: [実パス]）
✅ GATE 2 CLEAR: structured_output 初期化 & Playbook 適用完了
✅ GATE 3 CLEAR: messages＋structured_output 回収計画（API/命名/保存先）確立

🧾 監査ログの保存先: [実パス]
📦 命名規則: aicq_session_{session_id}_{YYYYMMDD_HHMMSS}/（詳細はセクション4）
🚀 作業開始許可: GRANTED
```

---

## セクション3: セッション中の更新ルール（structured_output運用）

### 3.1 更新の必須タイミング

**原則（漏れ防止）**: **各フェーズにおいて、作業のたびに監査ログを記録する。** 作業の種類（重要・軽微の別）を問わず、作業を行う際にはその都度 structured_output を更新する想定とする。フェーズ開始時・完了時に加え、以下のイベントが起きたら、structured_output を必ず更新します（抜け漏れ防止）。

- 新しい事実を得た
- 仮説/前提（assumptions）を置いた、または撤回した
- 設計・実装・テスト方針の意思決定をした
- ガイドライン上の判断（遵守/例外）を行った
- 逸脱（deviation）の兆候/発生/復帰があった
- **その他、フェーズ内の重要なサブタスクを開始または完了した**

各イベントが `structured_output` 内のどのフィールドを更新すべきかについては、  
`AICQ_AUDIT_LOG_SCHEMA.md` の「イベント種別とフィールド対応表」を参照してください。

### 3.3 フェーズ／イベント別更新タイミング

structured_output は、以下の **フェーズ開始時／完了時／重要イベント時** に最低限更新します。

| タイミング | 実施内容 | structured_output に必ず記録するもの |
|-----------|----------|--------------------------------------|
| **セッション開始直後** | STOP-GATE完了後、監査ログを初期化 | セッションID、対象PBI、PBIタイプ、想定フェーズ経路、保存先パス |
| **Phase 0 開始時** | 要件分析を開始 | PBI要約（タイトル／受入基準）、初期の前提・制約 |
| **Phase 0 完了時** | 要件分析・タスク分解完了 | 抽出要件のサマリ、タスク分解結果の概要、未解決の不明点リスト |
| **Phase 1 開始時** | プロジェクト初期化開始 | 対象プロジェクト、技術スタック方針、初期化で実施する作業一覧 |
| **Phase 1 完了時** | プロジェクト初期化完了 | 作成した成果物一覧、採用した技術・ツール、残課題 |
| **Phase 2A 開始時** | 実装前設計開始 | 対象機能（例: ログインAPI）と設計方針の概要 |
| **Phase 2A 完了時** | 軽量設計完了 | 作成した設計成果物一覧、主要な設計決定（API I/F、データ構造、セキュリティ方針など） |
| **Phase 3 開始時** | 実装開始 | 実装計画（クラス構成、責務分担、テスト戦略の概要） |
| **Phase 3 完了時** | 実装＋単体テスト完了 | 実装した主要コンポーネント一覧、テスト実行状況（カバレッジ等）、既知の制約・残課題 |
| **Phase 4 完了時** | レビュー・品質保証完了 | 実行した品質チェックの結果、レビューでの主要指摘と対応方針、PRの状態 |
| **Phase 5 完了時** | 実装後設計更新＋デプロイ完了 | 更新した設計書一覧、リリース内容要約、デプロイ結果、今後の改善ポイント |
| **Phase 6 開始時** | 運用・保守開始 | 運用移行方針、監視・障害対応の対象範囲 |
| **Phase 6 完了時** | 運用移行・引継ぎ完了 | 運用マニュアル等の成果物一覧、引継ぎ先・今後の改善ポイント |

> **作業のたびに記録すること**: 上記は「最低限」のタイミングである。各フェーズ内で、重要なサブタスクを開始したとき・完了したとき・意思決定したときも、**その都度** structured_output を更新すること。これにより監査ログの漏れを防ぐ。

> 上記に加えて、3.1 で定義したイベント  
> （新しい事実／仮説・前提の変更／設計・実装・テスト方針の意思決定／逸脱の検討・発生・復帰）が発生したタイミングでは、**必ず structured_output を即時更新すること。**

### 3.2 禁止事項
- structured_output の更新を後回しにして作業を継続すること（監査不能）
- セッション終了後に都合よく整形して「それっぽいログ」にすること

### 3.3 実践ガイドの活用

セッション中に判断に迷った場合、以下の実践ガイドを参照してください：

**判断支援:**
- **AI-AUDIT-LOG-QUICK-DECISION-GUIDE.md**: 30秒で判断できるフローチャート、20シナリオの即座判断表

**記録方法:**
- **AI-AUDIT-LOG-SCENARIO-COOKBOOK.md**: 20シナリオの完全記録例（コピペ可能JSON）

**技術実装:**
- **AI-AUDIT-LOG-UPDATE-API-GUIDE.md**: structured_output更新のAPI実装（Python/TypeScript）

**品質保証:**
- **AI-AUDIT-LOG-SELF-CHECK-SCRIPT.md**: セッション終了前の10カテゴリチェック（必須実行）

**命名標準:**
- **AI-AUDIT-LOG-DECISION-ID-STANDARD.md**: decision_IDの13カテゴリ標準・採番ルール


---

## セクション4: 命名規則（エクスポート成果物）

### 4.1 ディレクトリ命名（1セッション=1フォルダ）
```
aicq_session_{session_id}_{YYYYMMDD_HHMMSS}/
```

### 4.2 必須成果物
- `session_dump.json`（APIレスポンス全体）
- `structured_output.json`（structured_output 抽出）
- `messages.jsonl`（messages 抽出・行形式）

---

## セクション5: コンプライアンス（監査観点の最小要件）

- structured_output が存在し、セッション中に更新されている
- messages が回収され、structured_output と紐づいている
- 保存先と命名規則が統一され、後から検索・集計できる

---

## 付録: 参照
- AICQ_Devin_reasoning_log.md（structured_output スキーマ、Playbook、回収/集計モデル、メトリクス定義）
