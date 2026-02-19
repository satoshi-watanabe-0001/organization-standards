# AI-WORKLOG-ENFORCEMENT-GUIDE.md (Structured Output + Messages Mandatory)

---
document_type: mandatory_procedure
target_ai: all (Devin, Cursor, その他のAIエージェント)
priority: critical
enforcement_level: mandatory
version: 3.0
last_updated: 2026-02-19
changes: 作業ログ方式を structured_output＋messages 必須（監査ログ方式）に置換。Markdown作業ログは任意。
related_documents:
  - AICQ_Devin_reasoning_log.md
  - AI-WORKLOG-GRANULARITY-GUIDE.md
---

> 🚨 **【必須】作業開始前の絶対条件（監査ログ方式）**
>
> **すべてのAIエージェントは作業開始前に必ず以下を実行すること：**
> 1. ユーザーに **監査ログ（structured_output＋messages）のエクスポート保存先** を確認・指定してもらう
> 2. **3段階 STOP-GATE** を完全実行（Gate 2/3 は structured_output＋messages 前提）
> 3. セッション中、structured_output を **「関連が起きたら更新」** する（後述）
> 4. セッション完了後、**APIで messages と structured_output を回収・保存** する
>
> **structured_output＋messages を取得できない状態での作業開始は組織標準違反です。**

---

## セクション1: 概要と目的

### 1.1 目的
本ガイドは、自律型AI（例: Devin）が実施する開発作業について、**監査・品質評価に耐えるログ（監査ログ）を必ず取得できる状態を作る**ことを目的とします。監査ログの中核は、次の2つです。

- **messages**（ユーザー指示・質疑応答・説明などの対話履歴）
- **structured_output**（監査用に構造化された推論ログ相当）

> 注: 従来の Markdown 作業ログファイル（worklog_*.md）は、本バージョンでは必須ではありません（任意）。

### 1.2 なぜ structured_output＋messages なのか
AIの内部CoT（隠れた思考）をそのまま取得できない状況でも、structured_output を監査目的で設計し、messages と合わせて回収することで、意思決定や逸脱の検出、品質メトリクス算出が可能になります。詳細設計は **AICQ_Devin_reasoning_log.md** を参照してください。

### 1.3 作業ログ（監査ログ）の単位・粒度
監査ログの分割は **推論コンテキスト**を基準とします。

**基本原則**: 1つの推論コンテキスト = 1つの監査ログ（1セッション）

詳細は [AI-WORKLOG-GRANULARITY-GUIDE.md](./AI-WORKLOG-GRANULARITY-GUIDE.md) を参照してください。

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
次のイベントが起きたら、structured_output を必ず更新します（抜け漏れ防止）。

- 新しい事実を得た
- 仮説/前提（assumptions）を置いた、または撤回した
- 設計・実装・テスト方針の意思決定をした
- ガイドライン上の判断（遵守/例外）を行った
- 逸脱（deviation）の兆候/発生/復帰があった

### 3.2 禁止事項
- structured_output の更新を後回しにして作業を継続すること（監査不能）
- セッション終了後に都合よく整形して「それっぽいログ」にすること

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
