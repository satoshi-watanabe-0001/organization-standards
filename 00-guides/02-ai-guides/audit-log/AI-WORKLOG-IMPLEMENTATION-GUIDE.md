---
document_type: implementation_guide
target_audience: システム管理者・ドキュメント管理者・AI運用設計者
priority: high
scope: organization_standards_repository
version: 3.0
last_updated: 2026-02-19
changes: ログ方式を structured_output＋messages 必須（監査ログ方式）に置換。worklog_*.md 前提を撤廃。structured_output スキーマを AICQ_AUDIT_LOG_SCHEMA.md に分離。
related_documents:
  - AI-WORKLOG-ENFORCEMENT-GUIDE.md
  - AI-WORKLOG-GRANULARITY-GUIDE.md
  - AICQ_AUDIT_LOG_SCHEMA.md
  - AICQ_DATA_MODEL.md
  - AICQ_PLAYBOOK_TEMPLATE.md
  - AICQ_METRICS_SPEC.md
  - AICQ_METRICS_CALCULATION_GUIDE.md
  - AI-AUDIT-LOG-UPDATE-API-GUIDE.md
  - AI-AUDIT-LOG-SELF-CHECK-SCRIPT.md
  - AI-AUDIT-LOG-DECISION-ID-STANDARD.md
---

# 監査ログ取得のための実装ガイド V3.0

> このガイドは、組織標準リポジトリ（organization-standards）において、AI作業の監査ログを **確実に取得・保存・集計できる状態**を作るための実装手順です。
> **V3.0** では、監査ログの必須成果物を **structured_output＋messages** に統一しています。

---

## 🔄 バージョン（V3.0 のみ）

本ガイドおよび監査ログ取得手順は **V3.0 のみ** を参照します。必須成果物は **structured_output＋messages** です。旧方式（worklog_*.md 等）は [_archive/audit-log-v2.0/](../../_archive/audit-log-v2.0/README.md) を参照してください。

---

## 🎯 エグゼクティブサマリー

### 目的
- AIの作業を **監査可能**にする（説明責任・再現性・逸脱検知）
- AICQメトリクスの算出に耐える「推論ログ相当」を取得する

### V3.0 の基本方針
- **必須成果物**: `messages` と `structured_output`
- **取得方式**: セッション中は structured_output を逐次更新し、セッション完了後に API で `messages`/`structured_output` を回収して保存する

---

## セクション1: 監査ログ方式の全体アーキテクチャ

### 1.1 監査ログの構成（必須）
- `structured_output`（監査ログJSON、最重要）
- `messages[]`（対話・指示・説明・介入の履歴）

### 1.2 更新・回収のライフサイクル
1. セッション開始直後に structured_output を初期化
2. セッション中にイベント駆動で structured_output を更新
3. セッション完了後に `GET /v1/sessions/{session_id}` 等で `messages` と `structured_output` を回収・保存

---

## セクション2: organization-standards への配置（ドキュメント統合）

### 2.1 必須ドキュメント（存在確認）
- `/organization-standards/00-guides/02-ai-guides/AI-WORKLOG-ENFORCEMENT-GUIDE.md`
- `/organization-standards/00-guides/02-ai-guides/AI-WORKLOG-IMPLEMENTATION-GUIDE.md`
- `/organization-standards/00-guides/02-ai-guides/AICQ_AUDIT_LOG_SCHEMA.md`（structured_output スキーマ／イベント対応表）
- `/organization-standards/00-guides/02-ai-guides/AICQ_Devin_reasoning_log.md`（Playbook／回収→集計モデル／メトリクス仕様）

### 2.2 AICQ ドキュメントの位置づけ
AICQ関連ドキュメントは次のように役割分担されます。

- `AICQ_AUDIT_LOG_SCHEMA.md`:
  - structured_output の **JSONスキーマ（AICQ Audit Log）**
  - 更新イベントとフィールドの **対応表**
- `AICQ_Devin_reasoning_log.md`:
  - Playbook（コピペ用テンプレ）
  - 回収→保存→集計のデータモデル
  - AICQメトリクス算出の考え方

これらは ENFORCEMENT/IMPLEMENTATION の**規範（仕様書）**として参照されます。

### 2.3 AICQ スキーマ検証フロー

`structured_output.json` は、保存・集計に入る前に **必ず AICQ スキーマで検証**します。  
スキーマの一次ソースは `AICQ_AUDIT_LOG_SCHEMA.md` です。

**目的**
- 不完全／不正な `structured_output` によるメトリクス算出の崩壊を防ぐ
- 後続の集計処理で「なぜ失敗したか」を明確にする

**推奨フロー（CI / バッチ共通）**

1. `session_dump.json` から `structured_output` を抽出（セクション4の命名規則に従う）
2. JSON Schema バリデータ（例: `ajv`, `python-jsonschema` など）で、`AICQ_AUDIT_LOG_SCHEMA.md` に準拠しているか検証
3. 結果に応じて次を実施:
   - **バリデーション成功**:
     - `structured_output.json` を保存し、メトリクス算出処理（別途定義）に渡す
   - **バリデーション失敗**:
     - 該当セッションを「監査ログ不備」としてマーク
     - エラー内容（どのフィールドが欠落／型不一致か）をログまたは `events` 相当のストアに保存
     - メトリクス算出処理はスキップ（後続のダッシュボードで「欠損セッション」として可視化）

> 実装言語ごとのバリデータ選定はプロジェクト側に委ねるが、  
> 少なくとも CI もしくは日次バッチのいずれかで **全セッションの structured_output を一度は検証**することを推奨する。

---

## セクション3: 運用実装（STOP-GATE、Playbook、更新規律）

### 3.1 STOP-GATE の実装ポイント
- GATE 1: 保存先（エクスポート先）確定
- GATE 2: structured_output 初期化（スキーマ適用＋Playbook適用）
- GATE 3: 回収可能性の確立（API回収・命名規則・保存先）

詳細は [AI-WORKLOG-ENFORCEMENT-GUIDE.md](./AI-WORKLOG-ENFORCEMENT-GUIDE.md) を参照。

### 3.2 Playbook の標準運用
- Playbook は「毎回同じ形でログを残す」ために必須
- Playbookの中核は次の2点
  - 「作業開始直後に structured_output を初期化」
  - 「新しい事実/仮説/設計判断/逸脱が起きたら structured_output を更新」

---

## セクション4: エクスポート（API回収）と保存フォーマット

### 4.1 必須回収対象
- `messages[]`
- `structured_output`

### 4.2 回収方式（標準）
- `GET /v1/sessions/{session_id}` で `messages` と `structured_output` を取得する

### 4.3 命名規則（必須）
#### ディレクトリ命名（1セッション=1フォルダ）
```
aicq_session_{session_id}_{YYYYMMDD_HHMMSS}/
```

#### 必須成果物（フォルダ配下）
- `session_dump.json`（APIレスポンス全体）
- `structured_output.json`（structured_output 抽出）
- `messages.jsonl`（messages 抽出・行形式）

### 4.4 AICQ ベースの簡易フィールドチェック

`session_dump.json` から `structured_output` を抽出した後、  
最低限、次のフィールドが存在することを簡易チェックすることを推奨します。

- `meta`
- `requirements`
- `definitions`
- `steps`
- `guidelines`
- `deviations`
- `checks`
- `final`

**推奨実装パターン（例）**

- バッチ／スクリプトで `structured_output.json` を読み込み、上記キーの有無だけをまず確認する。
- 1つでも欠落している場合は:
  - AICQ スキーマ検証（セクション2.3）の前に「明らかな欠落」として警告を出す。
  - そのセッションを「要調査」としてフラグし、メトリクス算出処理はスキップする。

> 本チェックは **「スキーマの簡易ガード」** であり、正式な JSON Schema 検証の代替にはならない。  
> ただし、運用初期における誤配線や抽出ミスの早期検知には有効である。

---

## セクション5: 監査（継続運用）

### 5.1 週次の最小監査（例）
- 直近N日で、監査ログフォルダが増えているか
- `structured_output.json` が欠落していないか
- `messages.jsonl` が欠落していないか

> 旧方式（worklog_*.md）の監査は廃止済みです。上記成果物の存在監査に置換してください。旧方式の詳細は [アーカイブ](../../_archive/audit-log-v2.0/README.md) を参照。

### 5.2 AICQ メトリクス算出との接続

保存された `structured_output.json` は、AICQ メトリクス算出処理の **入力データ** として利用されます。

- メトリクス算出仕様の一次ソースは `AICQ_METRICS_SPEC.md` です。
- 具体的な算出式やキー名（例: `A1.requirements_understanding_accuracy`）は  
  `AICQ_METRICS_SPEC.md` および `AICQ_Devin_reasoning_log.md`（詳細解説）に従います。

**推奨運用**

1. 週次または日次で、保存済みの `structured_output.json` をスキャン
2. セクション2.3（スキーマ検証フロー）で定義したバリデーションを実施
3. 検証に通過したものだけを対象に、`AICQ_METRICS_SPEC.md` に従って 8 メトリクスを再計算
4. 結果を `metrics` テーブル／ダッシュボードに反映し、期間別・プロジェクト別の傾向を監視

> このセクションの目的は「監査ログの存在確認」と「メトリクス算出のトレーサビリティ」を結び付けることです。  
> どのセッションがどのメトリクス算出に使われたかが後から追跡できるよう、  
> `session_id` をキーとした一貫した保存戦略を維持してください。

---

## 付録: 参照（V3.0）
- ENFORCEMENT: [AI-WORKLOG-ENFORCEMENT-GUIDE.md](./AI-WORKLOG-ENFORCEMENT-GUIDE.md)
- 監査ログスキーマ: [AICQ_AUDIT_LOG_SCHEMA.md](./AICQ_AUDIT_LOG_SCHEMA.md)
- 監査ログ運用・メトリクス仕様: [AICQ_Devin_reasoning_log.md](./AICQ_Devin_reasoning_log.md)
- 旧方式・旧ポリシー: [_archive/audit-log-v2.0/](../../_archive/audit-log-v2.0/README.md)
