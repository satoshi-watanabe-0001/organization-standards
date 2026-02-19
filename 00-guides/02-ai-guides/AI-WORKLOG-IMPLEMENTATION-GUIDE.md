---
document_type: implementation_guide
target_audience: システム管理者・ドキュメント管理者・AI運用設計者
priority: high
scope: organization_standards_repository
version: 3.0
last_updated: 2026-02-19
changes: 作業ログ方式を structured_output＋messages 必須（監査ログ方式）に置換。worklog_*.md 前提を撤廃。
related_documents:
  - AI-WORKLOG-ENFORCEMENT-GUIDE.md
  - AICQ_Devin_reasoning_log.md
  - AI-WORKLOG-GRANULARITY-GUIDE.md
---

# 作業ログ（監査ログ）取得のための実装ガイド v3.0

> このガイドは、組織標準リポジトリ（organization-standards）において、AI作業の監査ログを **確実に取得・保存・集計できる状態**を作るための実装手順です。
> v3.0 では、監査ログの必須成果物を **structured_output＋messages** に統一します。

---

## 🔄 ドキュメントバージョン対応表

| ENFORCEMENT-GUIDE | IMPLEMENTATION-GUIDE | 必須成果物 | 主な変更点 |
|---|---|---|---|
| **v3.0** (2026-02-19) | **v3.0** (2026-02-19) | **structured_output＋messages** | 監査ログ方式へ全面移行、STOP-GATE置換、命名規則をJSON/JSONLへ |
| v2.0 (2026-02-13) | v1.0 (2026-02-13) | worklog_*.md（Markdown） | STOP-GATEでMarkdown作業ログを必須化 |

---

## 🎯 エグゼクティブサマリー

### 目的
- AIの作業を **監査可能**にする（説明責任・再現性・逸脱検知）
- AICQメトリクスの算出に耐える「推論ログ相当」を取得する

### v3.0 の基本方針
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
- `/organization-standards/00-guides/02-ai-guides/AICQ_Devin_reasoning_log.md`（監査ログ仕様）

### 2.2 AICQ ドキュメントの位置づけ
AICQドキュメントは、
- structured_output のJSONスキーマ
- Playbook（コピペ用テンプレ）
- 回収→保存→集計のデータモデル
- AICQメトリクス算出の考え方
を含むため、ENFORCEMENT/IMPLEMENTATION の**規範（仕様書）**として参照されます。

---

## セクション3: 運用実装（STOP-GATE、Playbook、更新規律）

### 3.1 STOP-GATE の実装ポイント
- GATE 1: 保存先（エクスポート先）確定
- GATE 2: structured_output 初期化（スキーマ適用＋Playbook適用）
- GATE 3: 回収可能性の確立（API回収・命名規則・保存先）

詳細は ENFORCEMENT v3.0 を参照。

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

---

## セクション5: 監査（継続運用）

### 5.1 週次の最小監査（例）
- 直近N日で、監査ログフォルダが増えているか
- `structured_output.json` が欠落していないか
- `messages.jsonl` が欠落していないか

> 旧版で実施していた `worklog_*.md` 前提の監査は廃止し、上記成果物の存在監査に置換してください。

---

## セクション6: 移行（v2.0 → v3.0）

- v2.0で作成済みの `worklog_*.md` は、既存資産として保持してよい（削除は必須ではない）
- v3.0以降の新規作業は、structured_output＋messages を必須成果物とする

---

## 付録: 参照
- ENFORCEMENT v3.0: AI-WORKLOG-ENFORCEMENT-GUIDE.md
- 監査ログ仕様: AICQ_Devin_reasoning_log.md
