# AICQ データモデル仕様

---
document_type: data_model_specification
target_audience:
  - データベース設計者
  - システム管理者
  - バックエンド開発者
  - データ分析担当者
priority: high
scope: aicq_audit_log_storage
version: 1.0
last_updated: 2026-03-10
related_documents:
  - AICQ_AUDIT_LOG_SCHEMA.md
  - AI-WORKLOG-IMPLEMENTATION-GUIDE.md
  - AICQ_METRICS_SPEC.md
---

## 📋 概要

### 目的

本ドキュメントは、AICQ監査ログシステムにおける**データベース設計仕様**を定義します。DevinをはじめとするAIエージェントから収集した監査ログ（structured_output + messages）を効率的に保存・集計・分析するためのテーブル構造とデータフローを提供します。

### 対象読者

- **データベース設計者**: テーブル構造の実装責任者
- **システム管理者**: 監査ログシステムの運用責任者  
- **バックエンド開発者**: API・集計処理の実装者
- **データ分析担当者**: AICQメトリクス算出・ダッシュボード開発者

---

## 🏗️ データモデル概要

### システムの目的

```
監査ログの保存 → 構造化データへの変換 → メトリクス算出 → 品質分析
```

**主要な機能:**
- ✅ **ログ保存**: セッション単位での完全なログ保存
- ✅ **イベント抽出**: 逸脱・不整合・定義変更等の構造化
- ✅ **メトリクス算出**: AICQ Phase1最優先8メトリクスの自動計算
- ✅ **トレンド分析**: プロジェクト別・時系列での品質推移追跡

### アーキテクチャ思想

**RDB/NoSQL両対応設計:**
- **RDB**: PostgreSQL, MySQL等での実装を想定した正規化設計
- **DocumentDB**: MongoDB, DynamoDB等での実装を想定したJSON保存最適化
- **ハイブリッド**: 生ログはDocumentDB、集計データはRDBという使い分けも可能

---

## 📊 AICQ_AUDIT_LOG_SCHEMA.md との違い

| 項目 | **AICQ_AUDIT_LOG_SCHEMA.md** | **AICQ_DATA_MODEL.md (本ファイル)** |
|------|------------------------------|-------------------------------------|
| **役割** | Devinが出力するJSONの形式定義 | 収集したJSONを保存するDB構造定義 |
| **対象** | AIエージェント（出力側） | システム（保存・集計側） |
| **内容** | structured_outputの8フィールド | sessions/events/metricsテーブル設計 |
| **フォーマット** | JSON Schema (Draft-07) | テーブル定義 (DDL/ERD相当) |
| **更新頻度** | 低（基本スキーマは固定） | 中（機能拡張に応じて追加） |
| **使用場面** | ログ出力・バリデーション | ログ保存・集計・分析 |

**データフロー:**
```
AIエージェント → [AICQ_AUDIT_LOG_SCHEMA準拠] → structured_output.json
                                                      ↓
システム → [AICQ_DATA_MODEL準拠] → sessions/events/metricsテーブル
```

---

## 🗄️ テーブル設計

### 全体ER図

```
sessions (1) ←→ (N) events
   ↓
   (1) ←→ (N) metrics

guideline_rules (独立マスタ)
```

### 1. sessions テーブル（セッション情報）

**目的**: 1つのAIセッションに関する全情報を格納

| カラム名 | 型 | 必須 | 説明 |
|----------|----|----|------|
| **session_id** | VARCHAR(255) | ✅ | セッション一意ID（PK） |
| project_id | VARCHAR(255) | ✅ | プロジェクトID |
| run_id | VARCHAR(255) | ✅ | 実行ID（同一要件での再実行区別） |
| created_at | TIMESTAMP | ✅ | セッション開始時刻 |
| updated_at | TIMESTAMP | ✅ | 最終更新時刻 |
| status | VARCHAR(50) | ✅ | success/failed/aborted |
| agent_name | VARCHAR(100) | ✅ | Devin/Cursor等 |
| agent_plan | VARCHAR(100) | ⚪ | Team/Enterprise等 |
| agent_version | VARCHAR(50) | ⚪ | エージェントバージョン |
| repo | VARCHAR(500) | ⚪ | リポジトリURL/パス |
| branch | VARCHAR(255) | ⚪ | 対象ブランチ |
| commit_hash | VARCHAR(255) | ⚪ | 対象コミットハッシュ |
| **raw_messages_json** | JSON/TEXT | ✅ | messages配列の生データ |
| **structured_output_json** | JSON/TEXT | ✅ | structured_outputの生データ |
| validation_status | VARCHAR(50) | ✅ | ok/failed (JSONスキーマ検証結果) |
| validation_errors | JSON/TEXT | ⚪ | バリデーションエラー詳細 |
| total_steps | INT | ⚪ | stepsの総数（集計用） |
| total_decisions | INT | ⚪ | decisionsの総数（集計用） |

**インデックス推奨:**
```sql
-- 基本検索用
CREATE INDEX idx_sessions_project_created ON sessions (project_id, created_at);
CREATE INDEX idx_sessions_status ON sessions (status);
CREATE INDEX idx_sessions_agent ON sessions (agent_name, agent_plan);

-- 分析用
CREATE INDEX idx_sessions_validation ON sessions (validation_status);
```

---

### 2. events テーブル（イベント正規化）

**目的**: 逸脱・不整合・定義変更等の重要イベントを正規化して保存

| カラム名 | 型 | 必須 | 説明 |
|----------|----|----|------|
| **event_id** | VARCHAR(255) | ✅ | イベント一意ID（PK） |
| **session_id** | VARCHAR(255) | ✅ | 親セッションID（FK） |
| event_type | VARCHAR(100) | ✅ | deviation/consistency_issue/definition_change/guideline_fail |
| severity | VARCHAR(50) | ✅ | critical/high/medium/low |
| step_id | VARCHAR(255) | ⚪ | 発生したstep_id |
| occurred_at | TIMESTAMP | ✅ | イベント発生時刻 |
| title | VARCHAR(500) | ✅ | イベントタイトル |
| description | TEXT | ✅ | 詳細説明 |
| **payload_json** | JSON/TEXT | ⚪ | イベント固有データ |
| resolved | BOOLEAN | ✅ | 解決済みフラグ |
| root_cause | TEXT | ⚪ | 根本原因 |
| impact | TEXT | ⚪ | 影響範囲 |

**インデックス推奨:**
```sql
-- 基本検索用
CREATE INDEX idx_events_session ON events (session_id);
CREATE INDEX idx_events_type_severity ON events (event_type, severity);

-- 分析用
CREATE INDEX idx_events_occurred ON events (occurred_at);
CREATE INDEX idx_events_resolved ON events (resolved);
```

**payload_json の例:**
```json
// deviation イベントの場合
{
  "deviation_type": "policy_risk",
  "detected_at_step": "STEP-003",
  "recovery_action": "人間介入により修正"
}

// consistency_issue イベントの場合  
{
  "decision_id": "DESIGN-001",
  "inconsistency_type": "設計と説明の不一致",
  "affected_sections": ["steps[2].decisions", "final.summary"]
}
```

---

### 3. metrics テーブル（メトリクス算出結果）

**目的**: 算出されたAICQメトリクスを時系列で保存

| カラム名 | 型 | 必須 | 説明 |
|----------|----|----|------|
| **metric_id** | VARCHAR(255) | ✅ | メトリクス記録ID（PK） |
| **session_id** | VARCHAR(255) | ✅ | 対象セッションID（FK） |
| **metric_key** | VARCHAR(100) | ✅ | A1.requirements_understanding_accuracy等 |
| value | DECIMAL(5,2) | ✅ | スコア値（0.00-100.00） |
| confidence | DECIMAL(3,2) | ✅ | 算出信頼度（0.00-1.00） |
| computed_at | TIMESTAMP | ✅ | 算出時刻 |
| **evidence_json** | JSON/TEXT | ⚪ | 根拠・計算過程 |
| algorithm_version | VARCHAR(50) | ✅ | 算出アルゴリズムバージョン |

**インデックス推奨:**
```sql
-- 基本検索用
CREATE INDEX idx_metrics_session ON metrics (session_id);
CREATE INDEX idx_metrics_key ON metrics (metric_key);

-- 分析用（時系列）
CREATE INDEX idx_metrics_key_computed ON metrics (metric_key, computed_at);
CREATE INDEX idx_metrics_value ON metrics (metric_key, value);
```

**evidence_json の例:**
```json
{
  "metric_name": "要件理解正確度",
  "calculation": {
    "req_coverage": 0.95,
    "assumption_declaration": 0.80,
    "requirement_linking": 0.90,
    "final_score": 88.5
  },
  "breakdown": {
    "total_requirements": 10,
    "requirements_with_acceptance": 9,
    "total_assumptions": 5,
    "declared_assumptions": 4
  },
  "evidence_refs": [
    "requirements.req_items[0-9]",
    "requirements.assumptions[0-4]",
    "steps[*].decisions[*].req_links"
  ]
}
```

---

### 4. guideline_rules テーブル（ルールマスタ）

**目的**: ガイドライン遵守チェックのルール定義

| カラム名 | 型 | 必須 | 説明 |
|----------|----|----|------|
| **ruleset_id** | VARCHAR(100) | ✅ | ルールセットID |
| **rule_id** | VARCHAR(100) | ✅ | ルール個別ID |
| description | VARCHAR(1000) | ✅ | ルール説明 |
| category | VARCHAR(100) | ✅ | safety/quality/process/compliance |
| severity | VARCHAR(50) | ✅ | critical/high/medium/low |
| active | BOOLEAN | ✅ | 有効フラグ |
| created_at | TIMESTAMP | ✅ | ルール作成日 |

**複合主キー:** (ruleset_id, rule_id)

**インデックス推奨:**
```sql
-- 基本検索用
CREATE INDEX idx_rules_active ON guideline_rules (active);
CREATE INDEX idx_rules_category ON guideline_rules (category);
```

---

## 🔄 データフロー図

```
1. AIセッション完了
   ↓
2. API回収 (GET /v1/sessions/{session_id})
   ├── messages[] 取得
   └── structured_output 取得
   ↓
3. sessions テーブルに保存
   ├── raw_messages_json ← messages
   └── structured_output_json ← structured_output
   ↓
4. JSONスキーマ バリデーション
   ├── 成功 → validation_status = 'ok'
   └── 失敗 → validation_status = 'failed'
   ↓
5. events 抽出・正規化
   ├── deviations[] → events (type='deviation')
   ├── checks.consistency.issues[] → events (type='consistency_issue')
   └── definitions.changes[] → events (type='definition_change')
   ↓
6. AICQメトリクス算出
   ├── structured_output_json を解析
   ├── 8メトリクス計算実行
   └── metrics テーブルに保存
   ↓
7. ダッシュボード・分析システム
   ├── 週次監査レポート生成
   ├── 品質トレンド分析
   └── アラート・通知
```

---

## 💻 実装パターン

### RDB実装（PostgreSQL例）

**DDL例:**
```sql
-- sessions テーブル
CREATE TABLE sessions (
    session_id VARCHAR(255) PRIMARY KEY,
    project_id VARCHAR(255) NOT NULL,
    run_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL CHECK (status IN ('success', 'failed', 'aborted')),
    agent_name VARCHAR(100) NOT NULL,
    raw_messages_json JSONB NOT NULL,
    structured_output_json JSONB NOT NULL,
    validation_status VARCHAR(50) NOT NULL DEFAULT 'pending'
);

-- events テーブル
CREATE TABLE events (
    event_id VARCHAR(255) PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL REFERENCES sessions(session_id),
    event_type VARCHAR(100) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    occurred_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    title VARCHAR(500) NOT NULL,
    payload_json JSONB
);

-- metrics テーブル
CREATE TABLE metrics (
    metric_id VARCHAR(255) PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL REFERENCES sessions(session_id),
    metric_key VARCHAR(100) NOT NULL,
    value DECIMAL(5,2) NOT NULL CHECK (value >= 0 AND value <= 100),
    confidence DECIMAL(3,2) NOT NULL CHECK (confidence >= 0 AND confidence <= 1),
    computed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    evidence_json JSONB
);
```

### DocumentDB実装（MongoDB例）

**コレクション設計:**

**sessions コレクション:**
```javascript
{
  _id: "session_12345",
  session_id: "session_12345",
  project_id: "project_alpha",
  created_at: ISODate("2026-03-10T10:00:00Z"),
  status: "success",
  agent: {
    name: "Devin",
    plan: "Team",
    version: "2.1.0"
  },
  raw_messages: [...],
  structured_output: {...},
  validation: {
    status: "ok",
    errors: null
  },
  events: [
    {
      event_id: "evt_001",
      type: "deviation",
      severity: "medium",
      title: "ガイドライン逸脱の疑い",
      payload: {...}
    }
  ],
  metrics: [
    {
      metric_key: "A1.requirements_understanding_accuracy",
      value: 88.5,
      confidence: 0.92,
      computed_at: ISODate("2026-03-10T11:00:00Z")
    }
  ]
}
```

---

## 📊 クエリ例

### 1. セッション一覧取得

**目的**: プロジェクト別のセッション履歴表示

```sql
-- RDB版
SELECT 
    session_id,
    project_id,
    created_at,
    status,
    agent_name,
    validation_status,
    total_steps,
    total_decisions
FROM sessions 
WHERE project_id = 'project_alpha'
    AND created_at >= '2026-03-01'
ORDER BY created_at DESC
LIMIT 50;
```

```javascript
// MongoDB版
db.sessions.find({
  project_id: "project_alpha",
  created_at: { $gte: ISODate("2026-03-01") }
}).sort({ created_at: -1 }).limit(50);
```

### 2. 逸脱イベント集計

**目的**: プロジェクトの品質状況をダッシュボードで表示

```sql
-- RDB版
SELECT 
    event_type,
    severity,
    COUNT(*) as count,
    COUNT(CASE WHEN resolved = true THEN 1 END) as resolved_count
FROM events e
JOIN sessions s ON e.session_id = s.session_id
WHERE s.project_id = 'project_alpha'
    AND e.occurred_at >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY event_type, severity
ORDER BY 
    CASE severity 
        WHEN 'critical' THEN 1 
        WHEN 'high' THEN 2 
        WHEN 'medium' THEN 3 
        WHEN 'low' THEN 4 
    END;
```

### 3. メトリクス推移取得

**目的**: 品質メトリクスの時系列グラフ表示

```sql
-- RDB版（週次平均）
SELECT 
    metric_key,
    DATE_TRUNC('week', computed_at) as week_start,
    AVG(value) as avg_score,
    COUNT(*) as sample_count,
    MIN(value) as min_score,
    MAX(value) as max_score
FROM metrics m
JOIN sessions s ON m.session_id = s.session_id  
WHERE s.project_id = 'project_alpha'
    AND computed_at >= CURRENT_DATE - INTERVAL '90 days'
    AND metric_key IN (
        'A1.requirements_understanding_accuracy',
        'A1.reasoning_accuracy', 
        'A6.guideline_compliance_rate'
    )
GROUP BY metric_key, DATE_TRUNC('week', computed_at)
ORDER BY metric_key, week_start;
```

### 4. 品質アラート検知

**目的**: 閾値を下回ったメトリクスの自動通知

```sql
-- RDB版
SELECT DISTINCT
    s.session_id,
    s.project_id,
    m.metric_key,
    m.value,
    s.created_at
FROM metrics m
JOIN sessions s ON m.session_id = s.session_id
WHERE m.computed_at >= CURRENT_DATE - INTERVAL '1 day'
    AND (
        (m.metric_key LIKE 'A1.%' AND m.value < 70.0) OR  -- A1系は70点未満でアラート
        (m.metric_key LIKE 'A6.%' AND m.value < 80.0)     -- A6系は80点未満でアラート
    )
ORDER BY s.created_at DESC;
```

---

## ⚡ スケーリング考慮事項

### データ量予測

**想定規模（年間）:**
- AIセッション数: 10,000-50,000セッション
- sessions テーブル: 50,000レコード × 1-10MB/レコード = 50GB-500GB
- events テーブル: 200,000レコード × 1KB/レコード = 200MB
- metrics テーブル: 400,000レコード × 500B/レコード = 200MB

### パフォーマンス最適化

**1. パーティショニング**
```sql
-- 月次パーティショニング（PostgreSQL）
CREATE TABLE sessions (...)
PARTITION BY RANGE (created_at);

CREATE TABLE sessions_2026_03 PARTITION OF sessions
FOR VALUES FROM ('2026-03-01') TO ('2026-04-01');
```

**2. JSON フィールドの最適化**
```sql
-- よく検索されるフィールドのインデックス
CREATE INDEX idx_sessions_agent_name 
ON sessions USING GIN ((structured_output_json->>'agent'->>'name'));

CREATE INDEX idx_sessions_status_type
ON sessions USING GIN ((structured_output_json->>'meta'->>'status'));
```

**3. 読み込み専用レプリカ**
- 分析クエリ専用のリードレプリカ設置
- メトリクス算出処理の負荷分散

### アーカイブ戦略

**1. 段階的アーカイブ**
```
現在-3ヶ月: ホットデータ（高速アクセス）
3ヶ月-1年: ウォームデータ（標準アクセス）  
1年以上: コールドデータ（アーカイブストレージ）
```

**2. データ圧縮**
- structured_output_json の圧縮保存
- 古いセッションの詳細ログ削除（メトリクスは保持）

### 高可用性設計

**1. バックアップ**
```sql
-- 日次フルバックアップ + 継続的WALアーカイブ
pg_dump --format=custom --compress=9 aicq_db > backup_$(date +%Y%m%d).dump
```

**2. 冗長化**
- プライマリ-スタンバイ構成
- 自動フェイルオーバー設定

**3. 監視**
```sql
-- ディスク使用量監視
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(tablename::regclass)) as size
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(tablename::regclass) DESC;
```

---

## 🎯 実装チェックリスト

### Phase 1: 基本実装
- [ ] sessions/events/metrics テーブル作成
- [ ] 基本的なINSERT/SELECT クエリ実装
- [ ] JSONスキーマ バリデーション組み込み
- [ ] 基本インデックス設定

### Phase 2: 集計・分析機能
- [ ] メトリクス算出バッチ処理実装
- [ ] イベント抽出ロジック実装
- [ ] ダッシュボード用API実装
- [ ] アラート通知機能実装

### Phase 3: 運用最適化
- [ ] パフォーマンスチューニング
- [ ] パーティショニング設定
- [ ] バックアップ・リストア手順確立
- [ ] 監視・ログ設定

このデータモデルに基づいて実装することで、AICQ監査ログシステムの堅牢で拡張可能なデータ基盤を構築できます。