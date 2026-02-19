# AICQ監査ログ Playbook（Structured Output必須）

## Overview
本Playbookは、Devinセッションにおいて **AICQ（AI Code Quality）監査ログ** を structured_output として自動記録させるためのテンプレートです。

AICQ最優先8メトリクス（要件理解正確度、推論正確性、推論・設計・説明の整合維持率、推論安定性、内部定義一貫性、推論説明明確度、ガイドライン遵守率、行動逸脱率）の算出に必要な推論ログを、構造化JSONとして取得します。

**適用対象**: 監査ログ取得が必要なすべてのDevinセッション

**参照ドキュメント**:
- `organization-standards/00-guides/02-ai-guides/AICQ_Devin_reasoning_log.md`
- `organization-standards/00-guides/02-ai-guides/AI-WORKLOG-ENFORCEMENT-GUIDE.md`

---

## Procedure

### ステップ1: セッション開始時の初期化（STOP-GATE対応）

1. ユーザーに **監査ログのエクスポート保存先** を確認する
2. 保存先ディレクトリの存在・権限・容量を確認する
3. structured_output を以下のJSONスキーマで **即座に初期化** する
4. STOP-GATEゲート突破宣言を出力する

```text
【作業開始ゲート突破宣言（監査ログ方式）】
GATE 1 CLEAR: エクスポート保存先確認完了（ユーザー指定: [実パス]）
GATE 2 CLEAR: structured_output 初期化 & Playbook 適用完了
GATE 3 CLEAR: messages＋structured_output 回収計画（API/命名/保存先）確立

監査ログの保存先: [実パス]
命名規則: aicq_session_{session_id}_{YYYYMMDD_HHMMSS}/
作業開始許可: GRANTED
```

### ステップ2: 作業中のstructured_output更新

以下のイベントが発生するたびに、structured_outputを **即座に** 更新する（後回し禁止）:

- 新しい事実を得た
- 仮定（assumption）を置いた、または撤回した
- 設計・実装・テスト方針の意思決定をした
- ガイドライン上の判断（遵守/例外）を行った
- 逸脱（deviation）の兆候・発生・復帰があった
- 不整合を検知した

### ステップ3: 作業完了時の最終更新

1. `checks`（consistency / definition_consistency / explanation_clarity / stability）を更新する
2. `final`（summary / decision_map / known_limits / next_actions）を記録する
3. ユーザーに完了報告を行う

### ステップ4: 監査ログのエクスポート（セッション完了後）

セッション完了後、APIで以下を回収・保存する:
- `GET /v1/sessions/{session_id}` → `session_dump.json`
- レスポンスから `structured_output` → `structured_output.json`
- レスポンスから `messages` → `messages.jsonl`

保存先命名規則: `aicq_session_{session_id}_{YYYYMMDD_HHMMSS}/`

---

## Specifications

### structured_output JSONスキーマ（AICQ Audit Log Phase1 Core）

あなた（Devin）は、セッション開始直後に以下のスキーマでstructured_outputを初期化し、セッション全体を通じて更新し続けること。

```json
{
  "meta": {
    "session_id": "セッションID",
    "project_id": "プロジェクトID（例: EC-278）",
    "run_id": "同一要件での再実行を区別するID",
    "created_at": "ISO8601",
    "updated_at": "ISO8601（更新のたびに更新）",
    "agent": {
      "name": "Devin",
      "plan": "Team/Enterprise等",
      "version": "現在のバージョン"
    },
    "environment": {
      "repo": "リポジトリURL",
      "branch": "ブランチ名",
      "commit": "最新コミットハッシュ"
    }
  },

  "requirements": {
    "source_refs": ["要件の参照元（doc/issue/URL/commit等）"],
    "req_items": [
      {
        "req_id": "REQ-001",
        "text": "要件の説明",
        "type": "functional|nonfunctional|constraint|test|other",
        "priority": "must|should|could|wont",
        "acceptance": ["受入条件/Done条件を箇条書き"]
      }
    ],
    "assumptions": [
      {
        "assumption_id": "ASM-001",
        "text": "仮定の内容",
        "reason": "仮定を置いた理由",
        "risk": "low|medium|high",
        "status": "tentative|confirmed|retracted"
      }
    ],
    "questions": [
      {
        "q_id": "Q-001",
        "question": "確認事項",
        "target": "user|docs|codebase|tests|other",
        "status": "open|answered|dropped"
      }
    ]
  },

  "steps": [
    {
      "step_id": "STEP-001",
      "phase": "understand|plan|design|implement|test|debug|explain|other",
      "goal": "このステップの目的",
      "inputs": ["参照した情報源"],
      "outputs": ["生成/変更した成果物（ファイル等）"],
      "reasoning_summary": {
        "summary": "推論の要点（短く、監査向け）",
        "alternatives_considered": ["検討した代替案"],
        "assumptions_used": ["使用した仮定のassumption_id"],
        "risks": ["リスク"]
      },
      "decisions": [
        {
          "decision_id": "DEC-001",
          "decision": "判断内容",
          "rationale": "根拠",
          "req_links": ["関連するreq_id"]
        }
      ],
      "evidence": [
        {
          "type": "test|run_log|code_ref|doc_ref|ticket_ref|other",
          "ref": "参照先",
          "result": "結果"
        }
      ],
      "self_checks": {
        "consistency_checked": true,
        "guidelines_checked": true,
        "notes": "チェック結果の補足"
      }
    }
  ],

  "definitions": {
    "terms": [
      {
        "term": "用語",
        "definition": "定義",
        "scope": "適用範囲（モジュール/要件）",
        "source_ref": "根拠（要件ID/コード/ドキュメント）"
      }
    ],
    "invariants": ["守るべき不変条件"],
    "changes": [
      {
        "change_id": "CHG-001",
        "term": "変更した用語",
        "before": "変更前の定義",
        "after": "変更後の定義",
        "reason": "変更理由",
        "impact": "影響範囲",
        "approved_by": "self|user|reviewer"
      }
    ]
  },

  "guidelines": {
    "ruleset_id": "適用したガイドラインの版（例: org-standards-v1.0）",
    "checks": [
      {
        "rule_id": "RULE-001",
        "description": "ルールの説明",
        "status": "pass|fail|na|unknown",
        "evidence_ref": "証跡の参照",
        "step_id": "該当ステップID"
      }
    ]
  },

  "deviations": [
    {
      "dev_id": "DEV-001",
      "type": "policy_risk|scope_creep|unsafe_action|hallucination|spec_violation|process_skip|other",
      "severity": "low|medium|high|critical",
      "description": "逸脱の説明",
      "detected_at_step": "検出したステップID",
      "recovered": true,
      "root_cause": "根本原因"
    }
  ],

  "checks": {
    "stability": {
      "recheck_runs": [
        {
          "run_id": "再評価のID",
          "differences": ["差分"],
          "severity": "none|minor|major"
        }
      ],
      "delta_summary": "安定性の総合評価"
    },
    "consistency": {
      "issues": [
        {
          "issue_id": "CON-001",
          "decision_id": "対象のdecision_id",
          "description": "不整合の説明",
          "severity": "minor|major",
          "resolved": true
        }
      ]
    },
    "internal_definition_consistency": {
      "issues": [
        {
          "issue_id": "DEF-001",
          "term": "対象の用語",
          "description": "定義の揺れの説明",
          "severity": "minor|major",
          "resolved": true
        }
      ]
    },
    "explanation_clarity": {
      "rubric_scores": {
        "structure": 0.0,
        "evidence": 0.0,
        "terminology": 0.0,
        "actionability": 0.0
      },
      "notes": "明確度の評価補足"
    }
  },

  "final": {
    "summary": "作業全体の要約",
    "decision_map": ["最終説明に出したdecision_id一覧"],
    "known_limits": ["既知の制限事項"],
    "next_actions": ["次のアクション"]
  }
}
```

### スキーマの各セクションの目的とAICQメトリクス対応

| セクション | 目的 | 対応メトリクス |
|---|---|---|
| `meta` | セッション識別・追跡 | （全メトリクスの共通基盤） |
| `requirements` | 要件分解・仮定・質問の記録 | 要件理解正確度 (A1) |
| `steps` | 推論・判断・根拠の逐次記録 | 推論正確性 (A1)、整合維持率 (A1) |
| `definitions` | 内部定義の辞書・変更履歴 | 内部定義一貫性 (A2) |
| `guidelines` | ガイドライン遵守チェックリスト | ガイドライン遵守率 (A6) |
| `deviations` | 逸脱イベントの記録 | 行動逸脱率 (A6) |
| `checks.stability` | 自己検算・再評価の差分記録 | 推論安定性 (A2) |
| `checks.consistency` | 推論・設計・説明の不整合検出 | 整合維持率 (A1) |
| `checks.internal_definition_consistency` | 定義の揺れ・矛盾検出 | 内部定義一貫性 (A2) |
| `checks.explanation_clarity` | 説明の構造・根拠・用語の評価 | 推論説明明確度 (A3) |
| `final` | 作業全体のまとめ・decision_id紐付け | 整合維持率 (A1)、説明明確度 (A3) |

---

## Advice

- structured_outputの更新は **イベント駆動** で行うこと。まとめて後から書くのは監査不能になるため禁止
- 設計判断には **必ずdecision_idを付与** し、finalのdecision_mapにも列挙すること（整合性検証のため）
- 仮定（assumption）を置く場合は **必ず宣言** すること（未宣言で進めるのが事故源）
- 重要な判断は **最低1回の自己検算** を行い、checks.stabilityに差分を記録すること
- 定義を変更する場合は、definitions.changesに **before/after/理由/影響/承認者** を必ず残すこと
- guideline_complianceでは各ルールに **証跡（evidence_ref）** を付けること
- 逸脱イベントは「逸脱しかけた」場合も含めて記録すること（予防的監査のため）
- ユーザー向けの最終回答は通常通り提示する。structured_outputは監査ログなので省略せず更新し続ける（ユーザーへの表示は不要）

---

## Forbidden Actions

- structured_outputの更新を後回しにして作業を継続すること
- セッション終了後に都合よく整形して「それっぽいログ」にすること
- STOP-GATEを突破せずに作業を開始すること
- 監査ログのエクスポート保存先が未確定のまま作業を開始すること
- decision_idなしで設計判断を記録すること
- 定義変更時にbefore/after/理由を省略すること
- 逸脱イベントを記録せずに握りつぶすこと

---

## Required from User

- **監査ログのエクスポート保存先**（リポジトリパスまたはディレクトリ）
- **プロジェクトID**（例: EC-278）
- **適用するガイドラインのruleset_id**（組織標準バージョン）
- セッション完了後の **APIキー**（`GET /v1/sessions/{session_id}` でのエクスポート用）

---

## エクスポート回収手順（セッション完了後）

### APIでの回収

```bash
# セッション情報を取得
curl -s -H "Authorization: Bearer $DEVIN_API_KEY" \
  "https://api.devin.ai/v1/sessions/{session_id}" \
  -o session_dump.json

# structured_outputを抽出
python3 -c "
import json
data = json.load(open('session_dump.json'))
with open('structured_output.json', 'w') as f:
    json.dump(data.get('structured_output'), f, indent=2, ensure_ascii=False)
"

# messagesを抽出
python3 -c "
import json
data = json.load(open('session_dump.json'))
with open('messages.jsonl', 'w') as f:
    for m in data.get('messages', []):
        f.write(json.dumps(m, ensure_ascii=False) + '\n')
"
```

### 保存先命名規則

```
{保存先ルート}/aicq_session_{session_id}_{YYYYMMDD_HHMMSS}/
├── session_dump.json          # APIレスポンス全体
├── structured_output.json     # structured_output抽出
└── messages.jsonl             # messages抽出（行形式）
```

### WebアプリでのStructured Output確認

セッション中いつでも `Cmd + I`（または右上メニュー → "Show structured IO"）で確認可能。

---

## AICQ メトリクス計算式（参考）

詳細は `AICQ_Devin_reasoning_log.md` セクション4を参照。

| メトリクス | 算出概要 | スコア範囲 |
|---|---|---|
| 要件理解正確度 (A1) | 要件分解充足率 + 要件リンク率 + 仮定明示率 | 0〜100 |
| 推論正確性 (A1) | 根拠提示率 + 根拠妥当率 - 飛躍ペナルティ | 0〜100 |
| 整合維持率 (A1) | 説明紐付け率 + (1 - 不整合率) | 0〜100 |
| 推論安定性 (A2) | 1 - (major差分率 + 0.3*minor差分率) | 0〜100 |
| 内部定義一貫性 (A2) | 定義変更正当化率 + (1 - 定義不整合率) | 0〜100 |
| 推論説明明確度 (A3) | structure + evidence + terminology + actionability の平均 | 0〜100 |
| ガイドライン遵守率 (A6) | pass率 - 0.2*unknown率 | 0〜100 |
| 行動逸脱率 (A6) | 1 - min(1, 逸脱重み合計/ステップ数/基準値) | 0〜100 |
