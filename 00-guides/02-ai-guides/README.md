# AI活用ガイド（V3.0 監査ログ方式）

> 🚨 **【必須】作業開始前の絶対条件（V3.0 / structured_output＋messages）**
> 
> **すべてのAIエージェントは作業開始前に必ず以下を実行すること:**
> 1. [AI-WORKLOG-ENFORCEMENT-GUIDE.md](./AI-WORKLOG-ENFORCEMENT-GUIDE.md) を確認（V3.0）
> 2. 3段階STOP-GATEを完全実行（保存先確定 → structured_output初期化 → 回収計画確立）
> 3. セッション中、structured_output を「関連が起きたら更新」する（Playbook運用）
> 4. セッション完了後、messages と structured_output をAPIで回収し保存する
> 
> **structured_output＋messages を取得できない状態での作業開始は組織標準違反です。**

---

## 📋 必須参照ドキュメント（優先順）

| 優先度 | ドキュメント | 用途 | 参照タイミング |
|-------|------------|------|-------------|
| 🔴 **必須** | [AI-WORKLOG-ENFORCEMENT-GUIDE.md](./AI-WORKLOG-ENFORCEMENT-GUIDE.md) | 監査ログ取得の強制手順（STOP-GATE） | 作業開始前必須 |
| 🔴 **必須** | [AICQ_AUDIT_LOG_SCHEMA.md](./AICQ_AUDIT_LOG_SCHEMA.md) | structured_output スキーマ / イベント対応表 | 作業開始前必須 |
| 🔴 **必須** | [AICQ_Devin_reasoning_log.md](./AICQ_Devin_reasoning_log.md) | Playbook / 回収・集計設計 / メトリクス定義 | 作業開始前必須 |
| 🔴 **必須** | [AI-WORKLOG-IMPLEMENTATION-GUIDE.md](./AI-WORKLOG-IMPLEMENTATION-GUIDE.md) | 監査ログ方式の実装・運用（命名/保存/監査） | 初回導入・運用設計時 |
| 🔴 **必須** | [AI-WORKLOG-GRANULARITY-GUIDE.md](./AI-WORKLOG-GRANULARITY-GUIDE.md) | 監査ログの単位・粒度ガイド | 作業開始前必須 |
| 🔴 **必須** | [AI-AUDIT-LOG-PROTOCOL.md](./AI-AUDIT-LOG-PROTOCOL.md) | 自律型AI全般向け監査ログプロトコル（START/LOG_EVENT/END） | ランタイム・ツール実装時 |
| 🟠 **高** | [AI-PRE-WORK-CHECKLIST.md](./AI-PRE-WORK-CHECKLIST.md) | 作業前確認事項 | 作業開始前必須 |
| 🟠 **高** | [AI-MASTER-WORKFLOW-GUIDE.md](./AI-MASTER-WORKFLOW-GUIDE.md) | 全体ワークフローガイド | 定期確認推奨 |

---

## 🚀 クイックスタート（V3.0）

### ステップ1: 監査ログの準備（必須）
1. **[AI-WORKLOG-ENFORCEMENT-GUIDE.md](./AI-WORKLOG-ENFORCEMENT-GUIDE.md) を確認**
2. **[AICQ_AUDIT_LOG_SCHEMA.md](./AICQ_AUDIT_LOG_SCHEMA.md) と [AICQ_Devin_reasoning_log.md](./AICQ_Devin_reasoning_log.md) から、structured_outputスキーマとPlaybookを適用**
3. **保存先を決定**（監査ログエクスポート先。プロジェクト別/機能別/日付別など）
4. **3段階STOP-GATEを通過**（保存先確定 → structured_output初期化 → 回収計画確立）
5. **作業開始ゲート突破宣言**を実行

### ステップ2: 作業の実行（監査ログ更新を伴う）
1. [AI-PRE-WORK-CHECKLIST.md](./AI-PRE-WORK-CHECKLIST.md) で事前確認
2. [AI-MASTER-WORKFLOW-GUIDE.md](./AI-MASTER-WORKFLOW-GUIDE.md) に従って作業実行
3. イベント発生（事実/仮説/意思決定/逸脱）ごとに structured_output を更新

### ステップ3: 作業の完了（必須回収）
1. セッション完了後、**messages と structured_output をAPIで回収**
2. 命名規則に従って保存（ENFORCEMENTの命名規則参照）

---

## 📚 補助ドキュメント

- [AI-USAGE-GUIDE.md](./AI-USAGE-GUIDE.md) - AI活用の基本ガイド
- [AI-CODING-INSTRUCTIONS.md](./AI-CODING-INSTRUCTIONS.md) - コーディング標準・指針
- [AI-TEST-CODE-GENERATION-GUIDE.md](./AI-TEST-CODE-GENERATION-GUIDE.md) - テストコード生成ガイド
