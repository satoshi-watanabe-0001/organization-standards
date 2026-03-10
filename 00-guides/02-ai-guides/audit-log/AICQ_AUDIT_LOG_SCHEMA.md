---
document_type: specification
target_audience: AIエージェント・システム管理者・品質評価者
priority: critical
scope: aicq_audit_log_schema
version: 1.0
last_updated: 2026-03-03
related_documents:
  - AICQ_Devin_reasoning_log.md
  - AI-WORKLOG-ENFORCEMENT-GUIDE.md
---

## AICQ Audit Log スキーマ仕様

**目的**: Devin 等のAIエージェントが出力する `structured_output` を、AICQ メトリクス算出に耐える **監査ログ（AICQ Audit Log）** として標準化する。

- **スキーマ名**: `AICQ Audit Log (Phase1 Core)`
- **適用対象**: すべての監査対象セッションの `structured_output`
- **利用先**:
  - 監査ログ取得: `AI-WORKLOG-ENFORCEMENT-GUIDE.md`
  - 実装ガイド: `AI-WORKLOG-IMPLEMENTATION-GUIDE.md`
  - メトリクス計算: `AICQ_Devin_reasoning_log.md`

### JSON Schema 定義（標準版）

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "AICQ Audit Log (Phase1 Core)",
  "type": "object",
  "required": ["meta", "requirements", "steps", "definitions", "guidelines", "deviations", "checks", "final"],
  "properties": {
    "meta": {
      "type": "object",
      "required": ["session_id", "project_id", "run_id", "created_at", "updated_at", "agent", "environment"],
      "properties": {
        "session_id": {"type": "string"},
        "project_id": {"type": "string"},
        "run_id": {"type": "string", "description": "同一要件での再実行・再評価を区別するID"},
        "created_at": {"type": "string"},
        "updated_at": {"type": "string"},
        "agent": {
          "type": "object",
          "required": ["name", "plan", "version"],
          "properties": {
            "name": {"type": "string", "enum": ["Devin"]},
            "plan": {"type": "string", "description": "Team/Enterprise等"},
            "version": {"type": "string"}
          }
        },
        "environment": {
          "type": "object",
          "required": ["repo", "branch"],
          "properties": {
            "repo": {"type": "string"},
            "branch": {"type": "string"},
            "commit": {"type": "string"}
          }
        }
      }
    },

    "requirements": {
      "type": "object",
      "required": ["source_refs", "req_items", "assumptions", "questions"],
      "properties": {
        "source_refs": {
          "type": "array",
          "items": {"type": "string"},
          "description": "要件の参照元（doc/issue/URL/commit等）"
        },
        "req_items": {
          "type": "array",
          "minItems": 1,
          "items": {
            "type": "object",
            "required": ["req_id", "text", "type", "priority", "acceptance"],
            "properties": {
              "req_id": {"type": "string"},
              "text": {"type": "string"},
              "type": {"type": "string", "enum": ["functional", "nonfunctional", "constraint", "test", "other"]},
              "priority": {"type": "string", "enum": ["must", "should", "could", "wont"]},
              "acceptance": {
                "type": "array",
                "items": {"type": "string"},
                "description": "受入条件/Done条件を箇条書き"
              }
            }
          }
        },
        "assumptions": {
          "type": "array",
          "items": {
            "type": "object",
            "required": ["assumption_id", "text", "reason", "risk", "status"],
            "properties": {
              "assumption_id": {"type": "string"},
              "text": {"type": "string"},
              "reason": {"type": "string"},
              "risk": {"type": "string", "enum": ["low", "medium", "high"]},
              "status": {"type": "string", "enum": ["tentative", "confirmed", "retracted"]}
            }
          }
        },
        "questions": {
          "type": "array",
          "description": "不明点・確認事項（聞くべきこと）",
          "items": {
            "type": "object",
            "required": ["q_id", "question", "target", "status"],
            "properties": {
              "q_id": {"type": "string"},
              "question": {"type": "string"},
              "target": {"type": "string", "enum": ["user", "docs", "codebase", "tests", "other"]},
              "status": {"type": "string", "enum": ["open", "answered", "dropped"]}
            }
          }
        }
      }
    },

    "definitions": {
      "type": "object",
      "required": ["terms", "invariants", "changes"],
      "properties": {
        "terms": {
          "type": "array",
          "description": "内部定義（用語・変数・仕様解釈）",
          "items": {
            "type": "object",
            "required": ["term", "definition", "scope", "source_ref"],
            "properties": {
              "term": {"type": "string"},
              "definition": {"type": "string"},
              "scope": {"type": "string", "description": "どのモジュール/要件に適用か"},
              "source_ref": {"type": "string", "description": "根拠（要件ID/コード/ドキュメント）"}
            }
          }
        },
        "invariants": {
          "type": "array",
          "description": "守るべき不変条件（例：データ整合、セキュリティ制約）",
          "items": {"type": "string"}
        },
        "changes": {
          "type": "array",
          "description": "定義変更履歴（上書きは必ず理由付き）",
          "items": {
            "type": "object",
            "required": ["change_id", "term", "before", "after", "reason", "impact", "approved_by"],
            "properties": {
              "change_id": {"type": "string"},
              "term": {"type": "string"},
              "before": {"type": "string"},
              "after": {"type": "string"},
              "reason": {"type": "string"},
              "impact": {"type": "string"},
              "approved_by": {"type": "string", "description": "self/user/reviewer"}
            }
          }
        }
      }
    },

    "steps": {
      "type": "array",
      "minItems": 1,
      "description": "作業ステップ（推論・設計・実装・テスト等）",
      "items": {
        "type": "object",
        "required": ["step_id", "phase", "goal", "inputs", "outputs", "reasoning_summary", "decisions", "evidence", "self_checks"],
        "properties": {
          "step_id": {"type": "string"},
          "phase": {"type": "string", "enum": ["understand", "plan", "design", "implement", "test", "debug", "explain", "other"]},
          "goal": {"type": "string"},
          "inputs": {"type": "array", "items": {"type": "string"}},
          "outputs": {"type": "array", "items": {"type": "string"}, "description": "生成/変更した成果物（ファイル等）"},
          "reasoning_summary": {
            "type": "object",
            "required": ["summary", "alternatives_considered", "assumptions_used", "risks"],
            "properties": {
              "summary": {"type": "string", "description": "推論の要点（短く、監査向け）"},
              "alternatives_considered": {"type": "array", "items": {"type": "string"}},
              "assumptions_used": {"type": "array", "items": {"type": "string"}},
              "risks": {"type": "array", "items": {"type": "string"}}
            }
          },
          "decisions": {
            "type": "array",
            "description": "設計/判断（必ずdecision_idで説明と紐付け）",
            "items": {
              "type": "object",
              "required": ["decision_id", "decision", "rationale", "req_links"],
              "properties": {
                "decision_id": {"type": "string"},
                "decision": {"type": "string"},
                "rationale": {"type": "string"},
                "req_links": {"type": "array", "items": {"type": "string"}, "description": "req_idの配列"}
              }
            }
          },
          "evidence": {
            "type": "array",
            "description": "根拠（ログ、実行結果、テスト、参照箇所）",
            "items": {
              "type": "object",
              "required": ["type", "ref", "result"],
              "properties": {
                "type": {"type": "string", "enum": ["test", "run_log", "code_ref", "doc_ref", "ticket_ref", "other"]},
                "ref": {"type": "string"},
                "result": {"type": "string"}
              }
            }
          },
          "self_checks": {
            "type": "object",
            "required": ["consistency_checked", "guidelines_checked", "notes"],
            "properties": {
              "consistency_checked": {"type": "boolean"},
              "guidelines_checked": {"type": "boolean"},
              "notes": {"type": "string"}
            }
          }
        }
      }
    },

    "guidelines": {
      "type": "object",
      "required": ["ruleset_id", "checks"],
      "properties": {
        "ruleset_id": {"type": "string", "description": "適用した行動ガイドラインの版"},
        "checks": {
          "type": "array",
          "description": "ルールごとの遵守判定（各ステップで更新しても良い）",
          "items": {
            "type": "object",
            "required": ["rule_id", "description", "status", "evidence_ref", "step_id"],
            "properties": {
              "rule_id": {"type": "string"},
              "description": {"type": "string"},
              "status": {"type": "string", "enum": ["pass", "fail", "na", "unknown"]},
              "evidence_ref": {"type": "string"},
              "step_id": {"type": "string"}
            }
          }
        }
      }
    },

    "deviations": {
      "type": "array",
      "description": "逸脱イベント（failだけでなく“逸脱しかけた”も記録）",
      "items": {
        "type": "object",
        "required": ["dev_id", "type", "severity", "description", "detected_at_step", "recovered", "root_cause"],
        "properties": {
          "dev_id": {"type": "string"},
          "type": {"type": "string", "enum": ["policy_risk", "scope_creep", "unsafe_action", "hallucination", "spec_violation", "process_skip", "other"]},
          "severity": {"type": "string", "enum": ["low", "medium", "high", "critical"]},
          "description": {"type": "string"},
          "detected_at_step": {"type": "string"},
          "recovered": {"type": "boolean", "description": "自力/介入で復帰できたか"},
          "root_cause": {"type": "string"}
        }
      }
    },

    "checks": {
      "type": "object",
      "required": ["stability", "consistency", "internal_definition_consistency", "explanation_clarity"],
      "properties": {
        "stability": {
          "type": "object",
          "required": ["recheck_runs", "delta_summary"],
          "properties": {
            "recheck_runs": {
              "type": "array",
              "description": "同条件での再評価/再実行の結果",
              "items": {
                "type": "object",
                "required": ["run_id", "differences", "severity"],
                "properties": {
                  "run_id": {"type": "string"},
                  "differences": {"type": "array", "items": {"type": "string"}},
                  "severity": {"type": "string", "enum": ["none", "minor", "major"]}
                }
              }
            },
            "delta_summary": {"type": "string"}
          }
        },
        "consistency": {
          "type": "object",
          "required": ["issues"],
          "properties": {
            "issues": {
              "type": "array",
              "description": "推論・設計・説明の不整合（decision_id基準）",
              "items": {
                "type": "object",
                "required": ["issue_id", "decision_id", "description", "severity", "resolved"],
                "properties": {
                  "issue_id": {"type": "string"},
                  "decision_id": {"type": "string"},
                  "description": {"type": "string"},
                  "severity": {"type": "string", "enum": ["minor", "major"]},
                  "resolved": {"type": "boolean"}
                }
              }
            }
          }
        },
        "internal_definition_consistency": {
          "type": "object",
          "required": ["issues"],
          "properties": {
            "issues": {
              "type": "array",
              "description": "定義の揺れ、矛盾、上書き理由欠落など",
              "items": {
                "type": "object",
                "required": ["issue_id", "term", "description", "severity", "resolved"],
                "properties": {
                  "issue_id": {"type": "string"},
                  "term": {"type": "string"},
                  "description": {"type": "string"},
                  "severity": {"type": "string", "enum": ["minor", "major"]},
                  "resolved": {"type": "boolean"}
                }
              }
            }
          }
        },
        "explanation_clarity": {
          "type": "object",
          "required": ["rubric_scores", "notes"],
          "properties": {
            "rubric_scores": {
              "type": "object",
              "required": ["structure", "evidence", "terminology", "actionability"],
              "properties": {
                "structure": {"type": "number", "minimum": 0, "maximum": 1},
                "evidence": {"type": "number", "minimum": 0, "maximum": 1},
                "terminology": {"type": "number", "minimum": 0, "maximum": 1},
                "actionability": {"type": "number", "minimum": 0, "maximum": 1}
              }
            },
            "notes": {"type": "string"}
          }
        }
      }
    },

    "final": {
      "type": "object",
      "required": ["summary", "decision_map", "known_limits", "next_actions"],
      "properties": {
        "summary": {"type": "string"},
        "decision_map": {
          "type": "array",
          "description": "最終説明に出したdecision_id一覧（説明との紐付け用）",
          "items": {"type": "string"}
        },
        "known_limits": {"type": "array", "items": {"type": "string"}},
        "next_actions": {"type": "array", "items": {"type": "string"}}
      }
    }
  }
}
```

---

## イベント種別とフィールド対応表

**目的**: `AI-WORKLOG-ENFORCEMENT-GUIDE.md` セクション3.1で定義される「更新イベント」と、`structured_output` 内の **どのフィールドを更新すべきか** を 1 か所で定義する。

### 基本ルール

- **原則**: 監査対象となる出来事が起きたら、必ず **該当フィールド＋`steps` の両方**を更新する。
- **steps**: すべての重要な推論・判断・作業は `steps[]` にも 1 レコードとして残す。

### 対応表（サマリ）

| イベント種別（例） | 主な更新フィールド | 補足 |
|--------------------|--------------------|------|
| 新しい事実を得た | `steps.reasoning_summary.summary`, `requirements.source_refs` | 重要な参照元が増えた場合は `source_refs` も更新 |
| 仮説/前提を置いた | `requirements.assumptions[]`, `steps.reasoning_summary.assumptions_used` | 既存前提の変更は `assumptions[].status` を更新 |
| 仮説/前提を撤回した | `requirements.assumptions[].status`, `steps.reasoning_summary.assumptions_used` | `status: retracted` に変更 |
| 要件分解を更新した | `requirements.req_items[]`, `steps.decisions[].req_links` | 要件IDの追加・分割もここに反映 |
| 質問を立てた/クローズした | `requirements.questions[]` | `status: open/answered/dropped` を更新 |
| 設計判断を行った | `steps.decisions[]`, `definitions.invariants`, `checks.consistency` | 重要な設計判断には一意な `decision_id` を付与 |
| 実装を行った | `steps.outputs[]`, `steps.evidence[]` | 生成・変更したファイルパスを `outputs` に記録 |
| テストを実行した | `steps.evidence[]` | テスト結果・ログを `type: "test"` で記録 |
| ガイドライン遵守を確認した | `guidelines.checks[]`, `steps.self_checks.guidelines_checked` | `rule_id` 単位で `status` と `evidence_ref` を更新 |
| ガイドライン例外を検討した | `guidelines.checks[]`, `deviations[]` | 重大な場合は `deviations` にもイベントとして記録 |
| 逸脱の兆候を検知した | `deviations[]`, `steps.self_checks` | `type`/`severity`/`detected_at_step` を必ず記録 |
| 逸脱が発生した | `deviations[]`, `checks.consistency` | クリティカルは `severity: high/critical` で登録 |
| 逸脱から復帰した | `deviations[].recovered`, `steps` | 復帰手順のステップも `steps` に追加 |
| 内部定義を追加した | `definitions.terms[]` | `scope`/`source_ref` を必ず埋める |
| 内部定義を変更した | `definitions.changes[]`, `definitions.terms[]` | `before/after/reason/impact/approved_by` を記録 |
| 安定性の自己検算をした | `checks.stability.recheck_runs[]` | 同一 `run_id` 系列ごとの差分を `differences` に記録 |
| 説明を更新・整理した | `checks.explanation_clarity`, `final.summary` | rubic_scores を更新してから `final` を確定 |
| セッションをクローズした | `final.*`, `meta.updated_at` | 最終的な決定と制約・ToDo を `final` に集約 |

> 各イベントで **どのような JSON を追加/更新するかの具体例** は、必要に応じて `AICQ_Devin_reasoning_log.md` や各プロジェクトの実装ガイド側に追加することを想定している。

