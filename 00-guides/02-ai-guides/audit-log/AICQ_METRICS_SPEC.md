---
document_type: specification
target_audience: システム管理者・データ分析担当・品質評価者
priority: high
scope: aicq_metrics_specification
version: 1.0
last_updated: 2026-03-03
related_documents:
  - AICQ_AUDIT_LOG_SCHEMA.md
  - AICQ_Devin_reasoning_log.md
  - AI-WORKLOG-IMPLEMENTATION-GUIDE.md
---

# AICQ メトリクス算出仕様（Phase1 コア）

**目的**: `AICQ_AUDIT_LOG_SCHEMA.md` で定義された `structured_output`（AICQ Audit Log）から、  
Phase1 で必須となる 8 つの AICQ メトリクスを一貫した方法で算出するための仕様を定義する。

## 1. 対象メトリクス一覧

- A1-1: 要件理解正確度
- A1-2: 推論正確性
- A1-3: 推論・設計・説明の整合維持率
- A2-1: 推論安定性
- A2-2: 内部定義一貫性
- A3-1: 推論説明明確度
- A6-1: ガイドライン遵守率
- A6-2: 行動逸脱率

いずれも **0〜100 点** のスコアとして算出し、内部実装では 0〜1 の正規化値を用いてもよい。

## 2. 前提となる入力

すべてのメトリクスは、以下の入力にのみ依存する。

- `structured_output.json`（AICQ Audit Log 準拠）
  - `meta`, `requirements`, `definitions`, `steps`, `guidelines`, `deviations`, `checks`, `final`
- 任意で `messages.jsonl`（補足的な裏付けにのみ使用）

> 実装上は、`structured_output` が `AICQ_AUDIT_LOG_SCHEMA.md` の JSON Schema に準拠していることを  
> バリデーションで確認した後、本仕様に従ってメトリクス算出を行う。

## 3. スコアリング共通ルール（サマリ）

- スコアは原則として「充足率 − ペナルティ」の形で計算する。
- 分母が 0 になり得る箇所では、`ε`（ごく小さい値）を足してゼロ割を避ける。
- 途中で 0 未満や 1 超過になった値は `clamp(0, 1)` でクリップする。
- critical / high / medium / low などの重大度は、重み付き合計として扱う。

詳細な算出式やサンプルコードは、運用チーム側の実装に委ねるが、  
初期実装では `AICQ_Devin_reasoning_log.md` セクション4に示された式をベースに実装することを推奨する。

## 4. メトリクス別入力フィールド対応（要約）

### 4.1 要件理解正確度（A1-1）

- 主な入力:
  - `requirements.req_items`
  - `requirements.assumptions`
  - `requirements.questions`
  - `steps[].decisions[].req_links`
- 代表的な指標:
  - 要件分解充足率（`req_items` が適切に分解されているか）
  - 仮定明示率（重要な前提が `assumptions` として明示されているか）
  - 要件リンク率（判断に `req_links` が紐付いているか）

### 4.2 推論正確性（A1-2）

- 主な入力:
  - `steps[].reasoning_summary`
  - `steps[].evidence`
  - `deviations[]`（hallucination / spec_violation など）
- 代表的な指標:
  - 根拠提示率（`evidence` が付与されたステップ比率）
  - 根拠妥当率（参照先の存在・整合性チェック）
  - 飛躍ペナルティ（重大な逸脱イベントを重み付きで減点）

### 4.3 推論・設計・説明の整合維持率（A1-3）

- 主な入力:
  - `steps[].decisions[].decision_id`
  - `final.decision_map`
  - `checks.consistency.issues`
- 代表的な指標:
  - 説明紐付け率（全 decision_id のうち最終説明に登場する比率）
  - 不整合率（未解決の consistency issues 比率）

### 4.4 推論安定性（A2-1）

- 主な入力:
  - `checks.stability.recheck_runs[]`
- 代表的な指標:
  - major 差分率（severity=major の割合）
  - minor 差分率（severity=minor の割合）

### 4.5 内部定義一貫性（A2-2）

- 主な入力:
  - `definitions.terms`
  - `definitions.changes`
  - `checks.internal_definition_consistency.issues`
- 代表的な指標:
  - 定義変更の正当化率（`reason`/`impact` が埋まっている変更比率）
  - 定義不整合率（未解決の定義不整合 issues 比率）

### 4.6 推論説明明確度（A3-1）

- 主な入力:
  - `checks.explanation_clarity.rubric_scores`
- 代表的な指標:
  - structure / evidence / terminology / actionability の各スコア平均

### 4.7 ガイドライン遵守率（A6-1）

- 主な入力:
  - `guidelines.checks[]`
- 代表的な指標:
  - 適用対象数に対する pass 率
  - unknown の軽い減点（チェック漏れのペナルティ）

### 4.8 行動逸脱率（A6-2）

- 主な入力:
  - `deviations[]`
  - `steps[]`（分母としてステップ数を利用）
- 代表的な指標:
  - 重大度重み付きの逸脱率（critical/high/medium/low の重み合計をステップ数で割った値）

## 5. 実装ガイドライン（簡易）

- **入力検証**:
  - メトリクス算出前に `AICQ_AUDIT_LOG_SCHEMA.md` の JSON Schema で `structured_output` をバリデーションする。
  - 検証失敗時は、そのセッションのメトリクス算出をスキップし、エラーを `events` や監査ログに記録する。
- **出力フォーマット**:
  - `metrics` テーブル／コレクションを用意し、`metric_key`（例: `A1.requirements_understanding_accuracy`）と `value`（0〜100）、`confidence` を保存する。
- **バッチ／CI での利用**:
  - 日次バッチ、または CI パイプラインの一部として `structured_output.json` を読み込み、本仕様に基づいてメトリクスを再計算する。
  - API 経由のオンライン算出とバッチ算出で、計算式のバージョンを揃えること。

> 本ファイルは **メトリクス算出の「仕様書」** として扱い、実装言語やフレームワークに依存しない形で維持する。  
> 実装レベルの詳細（関数名・クラス設計など）は、各プロジェクト側のドキュメントで補完することを推奨する。

