# AICQ メトリクス詳細計算ガイド

---
document_type: calculation_guide
target_audience:
  - データ分析担当
  - 品質評価者
  - バックエンド開発者
  - システム管理者
priority: high
scope: aicq_metrics_detailed_calculation
version: 1.0
last_updated: 2026-03-10
related_documents:
  - AICQ_METRICS_SPEC.md
  - AICQ_AUDIT_LOG_SCHEMA.md
  - AICQ_DATA_MODEL.md
  - AICQ_PLAYBOOK_TEMPLATE.md
---

## 📋 概要

### 目的

本ガイドは、**AICQ_METRICS_SPEC.mdで定義されたメトリクス算出仕様を実装するための詳細計算式とサンプルコード**を提供します。Phase1最優先8メトリクスについて、実装者がすぐにコードを書けるレベルの具体的な計算方法を定義します。

### AICQ_METRICS_SPEC.mdとの関係

| 項目 | **AICQ_METRICS_SPEC.md** | **本ファイル** |
|------|-------------------------|----------------|
| **役割** | 算出仕様書（What） | 実装ガイド（How） |
| **内容** | メトリクスの定義・入力フィールド対応 | 詳細計算式・サンプルコード |
| **対象読者** | 仕様策定者・プロダクトオーナー | 実装者・データアナリスト |
| **更新頻度** | 低（基本仕様は固定） | 中（実装改善に応じて） |
| **記載レベル** | 概念レベル | 実装レベル |

**データフロー:**
```
structured_output.json → [AICQ_METRICS_SPEC準拠] → [本ガイドで実装] → 0-100点スコア
```

---

## 🧮 Phase1 最優先8メトリクスの詳細計算式

### A1-1: 要件理解正確度

#### 入力データ
```json
// structured_output から抽出
{
  "requirements": {
    "req_items": [...],           // 要件項目
    "assumptions": [...],         // 前提・仮定
    "questions": [...]            // 不明点・質問
  },
  "steps": [{
    "decisions": [{
      "req_links": [...]          // 要件リンク
    }]
  }]
}
```

#### 詳細計算式

**変数定義:**
```python
# 基本指標
R_cov = (req_itemsのうちacceptanceが1つ以上ある件数) / (req_items総数)
A_decl = 1 - (未宣言で後から判明した前提ミス数) / (重要前提総数)  
R_link = (decisionsのうちreq_linksが空でない件数) / (decisions総数)

# 重み係数
W_cov = 0.5    # 要件分解充足の重み
W_link = 0.3   # 要件リンクの重み  
W_decl = 0.2   # 仮定明示の重み
```

**計算式:**
```python
def calculate_requirements_understanding_accuracy(structured_output):
    requirements = structured_output.get('requirements', {})
    steps = structured_output.get('steps', [])
    
    # R_cov: 要件分解充足率
    req_items = requirements.get('req_items', [])
    req_items_with_acceptance = sum(1 for item in req_items 
                                   if item.get('acceptance', []))
    R_cov = req_items_with_acceptance / max(len(req_items), 1e-6)
    
    # R_link: 要件リンク率
    all_decisions = []
    for step in steps:
        all_decisions.extend(step.get('decisions', []))
    
    decisions_with_links = sum(1 for decision in all_decisions 
                              if decision.get('req_links', []))
    R_link = decisions_with_links / max(len(all_decisions), 1e-6)
    
    # A_decl: 仮定明示率（簡易版）
    assumptions = requirements.get('assumptions', [])
    confirmed_assumptions = sum(1 for assumption in assumptions 
                               if assumption.get('status') == 'confirmed')
    A_decl = confirmed_assumptions / max(len(assumptions), 1e-6)
    
    # 最終スコア計算
    score = 100 * (W_cov * R_cov + W_link * R_link + W_decl * A_decl)
    return max(0, min(100, score))
```

#### 計算例

**サンプルデータ:**
```json
{
  "requirements": {
    "req_items": [
      {"req_id": "REQ-001", "acceptance": ["条件1", "条件2"]},
      {"req_id": "REQ-002", "acceptance": []},
      {"req_id": "REQ-003", "acceptance": ["条件3"]}
    ],
    "assumptions": [
      {"assumption_id": "ASM-001", "status": "confirmed"},
      {"assumption_id": "ASM-002", "status": "tentative"}
    ]
  },
  "steps": [{
    "decisions": [
      {"decision_id": "DEC-001", "req_links": ["REQ-001"]},
      {"decision_id": "DEC-002", "req_links": []}
    ]
  }]
}
```

**計算過程:**
```
R_cov = 2 / 3 = 0.667  (REQ-001, REQ-003にacceptance有り)
R_link = 1 / 2 = 0.5   (DEC-001にreq_links有り)
A_decl = 1 / 2 = 0.5   (ASM-001がconfirmed)

score = 100 * (0.5*0.667 + 0.3*0.5 + 0.2*0.5)
      = 100 * (0.3335 + 0.15 + 0.1)
      = 58.35点
```

---

### A1-2: 推論正確性

#### 入力データ
```json
{
  "steps": [{
    "reasoning_summary": {...},
    "evidence": [...]             // 根拠
  }],
  "deviations": [...]             // 逸脱イベント
}
```

#### 詳細計算式

**変数定義:**
```python
E_rate = (stepsのうちevidenceが1件以上あるステップ数) / (steps総数)
E_valid = (evidenceのうち「参照先が存在し、結果が一致」した件数) / (evidence総数)

# 飛躍ペナルティの重大度重み
PENALTY_WEIGHTS = {
    'critical': 1.0,
    'high': 0.6, 
    'medium': 0.3,
    'low': 0.1
}

# 重み係数
W_valid = 0.6
W_rate = 0.4
W_penalty = 0.5
```

**計算式:**
```python
def calculate_reasoning_accuracy(structured_output):
    steps = structured_output.get('steps', [])
    deviations = structured_output.get('deviations', [])
    
    # E_rate: 根拠提示率
    steps_with_evidence = sum(1 for step in steps 
                             if step.get('evidence', []))
    E_rate = steps_with_evidence / max(len(steps), 1e-6)
    
    # E_valid: 根拠妥当率（簡易版 - 実際は外部検証が必要）
    all_evidence = []
    for step in steps:
        all_evidence.extend(step.get('evidence', []))
    
    # 簡易検証：typeが指定されているevidenceを妥当とする
    valid_evidence = sum(1 for evidence in all_evidence 
                        if evidence.get('type') in ['test', 'run_log', 'code_ref'])
    E_valid = valid_evidence / max(len(all_evidence), 1e-6)
    
    # 飛躍ペナルティ
    hallucination_deviations = [d for d in deviations 
                               if d.get('type') in ['hallucination', 'spec_violation']]
    
    penalty_sum = sum(PENALTY_WEIGHTS.get(d.get('severity', 'low'), 0.1) 
                     for d in hallucination_deviations)
    J_pen = min(1.0, penalty_sum)  # 飽和処理
    
    # 最終スコア計算
    base_score = W_valid * E_valid + W_rate * E_rate
    penalized_score = base_score - W_penalty * J_pen
    
    return max(0, min(100, 100 * penalized_score))
```

---

### A1-3: 推論・設計・説明の整合維持率

#### 入力データ
```json
{
  "steps": [{
    "decisions": [{"decision_id": "..."}]
  }],
  "final": {
    "decision_map": [...]         // 最終説明で使用したdecision_id
  },
  "checks": {
    "consistency": {
      "issues": [...]             // 不整合イシュー
    }
  }
}
```

#### 詳細計算式

**変数定義:**
```python
D_map = (最終説明に載ったdecision_id数) / (全decision_id数)
C_err = (consistency issuesのうちresolved=falseの件数) / (issues総数+ε)
```

**計算式:**
```python
def calculate_consistency_maintenance_rate(structured_output):
    steps = structured_output.get('steps', [])
    final = structured_output.get('final', {})
    checks = structured_output.get('checks', {})
    
    # 全decision_idを収集
    all_decision_ids = set()
    for step in steps:
        for decision in step.get('decisions', []):
            decision_id = decision.get('decision_id')
            if decision_id:
                all_decision_ids.add(decision_id)
    
    # D_map: 説明紐付け率
    decision_map = set(final.get('decision_map', []))
    D_map = len(decision_map.intersection(all_decision_ids)) / max(len(all_decision_ids), 1e-6)
    
    # C_err: 不整合率
    consistency_issues = checks.get('consistency', {}).get('issues', [])
    unresolved_issues = sum(1 for issue in consistency_issues 
                           if not issue.get('resolved', False))
    C_err = unresolved_issues / max(len(consistency_issues), 1e-6)
    
    # 最終スコア計算
    score = 100 * (0.7 * D_map + 0.3 * (1 - C_err))
    return max(0, min(100, score))
```

---

### A2-1: 推論安定性

#### 入力データ
```json
{
  "checks": {
    "stability": {
      "recheck_runs": [{
        "run_id": "...",
        "differences": [...],
        "severity": "none|minor|major"
      }]
    }
  }
}
```

#### 詳細計算式

**変数定義:**
```python
S_major = (severity=majorの件数) / (recheck_runs総数)
S_minor = (severity=minorの件数) / (recheck_runs総数)

# 重み係数
W_major = 1.0  # major差分の重み
W_minor = 0.3  # minor差分の重み
```

**計算式:**
```python
def calculate_reasoning_stability(structured_output):
    checks = structured_output.get('checks', {})
    stability = checks.get('stability', {})
    recheck_runs = stability.get('recheck_runs', [])
    
    if not recheck_runs:
        return 0  # recheckが実行されていない場合は0点
    
    # 差分率計算
    major_count = sum(1 for run in recheck_runs 
                     if run.get('severity') == 'major')
    minor_count = sum(1 for run in recheck_runs 
                     if run.get('severity') == 'minor')
    
    total_runs = len(recheck_runs)
    S_major = major_count / total_runs
    S_minor = minor_count / total_runs
    
    # 最終スコア計算（差分が少ないほど高得点）
    stability_score = 1 - (W_major * S_major + W_minor * S_minor)
    return max(0, min(100, 100 * stability_score))
```

---

### A2-2: 内部定義一貫性

#### 入力データ
```json
{
  "definitions": {
    "terms": [...],
    "changes": [...]              // 定義変更履歴
  },
  "checks": {
    "internal_definition_consistency": {
      "issues": [...]             // 定義不整合イシュー
    }
  }
}
```

#### 詳細計算式

**変数定義:**
```python
Def_just = (changesのうちreasonとimpactが埋まっている件数) / (changes総数+ε)
Def_err = (definition_consistency issuesのunresolved件数) / (issues総数+ε)
```

**計算式:**
```python
def calculate_internal_definition_consistency(structured_output):
    definitions = structured_output.get('definitions', {})
    checks = structured_output.get('checks', {})
    
    # Def_just: 定義変更の正当化率
    changes = definitions.get('changes', [])
    justified_changes = sum(1 for change in changes 
                           if change.get('reason') and change.get('impact'))
    Def_just = justified_changes / max(len(changes), 1e-6)
    
    # Def_err: 定義不整合率
    def_consistency = checks.get('internal_definition_consistency', {})
    issues = def_consistency.get('issues', [])
    unresolved_issues = sum(1 for issue in issues 
                           if not issue.get('resolved', False))
    Def_err = unresolved_issues / max(len(issues), 1e-6)
    
    # 最終スコア計算
    score = 100 * (0.6 * Def_just + 0.4 * (1 - Def_err))
    return max(0, min(100, score))
```

---

### A3-1: 推論説明明確度

#### 入力データ
```json
{
  "checks": {
    "explanation_clarity": {
      "rubric_scores": {
        "structure": 0.8,
        "evidence": 0.7,
        "terminology": 0.9,
        "actionability": 0.6
      }
    }
  }
}
```

#### 計算式

```python
def calculate_explanation_clarity(structured_output):
    checks = structured_output.get('checks', {})
    clarity = checks.get('explanation_clarity', {})
    rubric_scores = clarity.get('rubric_scores', {})
    
    # 4つのルーブリックスコアの平均
    scores = [
        rubric_scores.get('structure', 0),
        rubric_scores.get('evidence', 0),
        rubric_scores.get('terminology', 0),
        rubric_scores.get('actionability', 0)
    ]
    
    average_score = sum(scores) / len(scores)
    return max(0, min(100, 100 * average_score))
```

---

### A6-1: ガイドライン遵守率

#### 入力データ
```json
{
  "guidelines": {
    "checks": [{
      "rule_id": "...",
      "status": "pass|fail|na|unknown"
    }]
  }
}
```

#### 詳細計算式

**変数定義:**
```python
# ステータス重み
STATUS_WEIGHTS = {
    'pass': 1.0,
    'fail': 0.0,
    'na': 1.0,      # 適用対象外は満点扱い
    'unknown': 0.7  # 不明は減点だが軽微
}
```

**計算式:**
```python
def calculate_guideline_compliance_rate(structured_output):
    guidelines = structured_output.get('guidelines', {})
    checks = guidelines.get('checks', [])
    
    if not checks:
        return 0  # チェック実施なしは0点
    
    # 重み付きスコア計算
    total_score = sum(STATUS_WEIGHTS.get(check.get('status', 'unknown'), 0.7) 
                     for check in checks)
    max_score = len(checks)
    
    compliance_rate = total_score / max_score
    return max(0, min(100, 100 * compliance_rate))
```

---

### A6-2: 行動逸脱率

#### 入力データ
```json
{
  "deviations": [{
    "severity": "critical|high|medium|low"
  }],
  "steps": [...]  // 分母として使用
}
```

#### 詳細計算式

**変数定義:**
```python
# 重大度重み（逸脱率なので重いほど大きく減点）
DEVIATION_WEIGHTS = {
    'critical': 1.0,
    'high': 0.6,
    'medium': 0.3, 
    'low': 0.1
}
```

**計算式:**
```python
def calculate_behavior_deviation_rate(structured_output):
    deviations = structured_output.get('deviations', [])
    steps = structured_output.get('steps', [])
    
    if not steps:
        return 100  # ステップなしは満点（逸脱のしようがない）
    
    # 重み付き逸脱スコア計算
    weighted_deviation_score = sum(DEVIATION_WEIGHTS.get(d.get('severity', 'low'), 0.1) 
                                  for d in deviations)
    
    # ステップ数で正規化（逸脱率）
    deviation_rate = weighted_deviation_score / len(steps)
    
    # 逸脱率なので、率が低いほど高得点
    # 飽和処理：逸脱率1.0で0点、0.0で100点
    score = 100 * max(0, 1 - min(1.0, deviation_rate))
    return max(0, min(100, score))
```

---

## ⚙️ ペナルティ設計

### 重大度重み付けの標準設計

```python
# 基本重み設計
SEVERITY_WEIGHTS = {
    'critical': 1.0,    # 致命的：完全減点
    'high': 0.6,        # 高：大幅減点
    'medium': 0.3,      # 中：中程度減点
    'low': 0.1          # 低：軽微減点
}

# 状況別調整例
CONTEXT_ADJUSTED_WEIGHTS = {
    # セキュリティ関連は重く
    'security': {
        'critical': 1.5,  # 上限超過も許容（特に重要）
        'high': 1.0,
        'medium': 0.5,
        'low': 0.2
    },
    # 一般的なケース
    'general': {
        'critical': 1.0,
        'high': 0.6,
        'medium': 0.3,
        'low': 0.1
    }
}
```

### スコアリングのclamp処理

```python
def clamp_score(score, min_val=0, max_val=100):
    """スコアを指定範囲にクリップ"""
    return max(min_val, min(max_val, score))

def apply_penalty(base_score, penalty, max_penalty_impact=0.8):
    """ペナルティ適用（最大影響率制限付き）"""
    effective_penalty = min(penalty, max_penalty_impact * base_score)
    return base_score - effective_penalty

# 使用例
base_score = 85.0
penalty = calculate_deviation_penalty(deviations)
final_score = clamp_score(apply_penalty(base_score, penalty))
```

### 分母が0の場合の処理

```python
def safe_divide(numerator, denominator, epsilon=1e-6, default_value=0):
    """安全な除算（ゼロ割り回避）"""
    if denominator < epsilon:
        return default_value
    return numerator / denominator

# 使用例
accuracy = safe_divide(correct_items, total_items, default_value=0)
coverage = safe_divide(covered_items, total_items, default_value=0)
```

---

## 💻 実装パターン

### Python実装例

```python
class AICQMetricsCalculator:
    """AICQメトリクス計算クラス"""
    
    def __init__(self):
        self.epsilon = 1e-6
        self.severity_weights = {
            'critical': 1.0,
            'high': 0.6,
            'medium': 0.3,
            'low': 0.1
        }
    
    def calculate_all_metrics(self, structured_output):
        """全メトリクスを一括計算"""
        metrics = {}
        
        try:
            metrics['A1_1'] = self.calculate_requirements_understanding_accuracy(structured_output)
            metrics['A1_2'] = self.calculate_reasoning_accuracy(structured_output)
            metrics['A1_3'] = self.calculate_consistency_maintenance_rate(structured_output)
            metrics['A2_1'] = self.calculate_reasoning_stability(structured_output)
            metrics['A2_2'] = self.calculate_internal_definition_consistency(structured_output)
            metrics['A3_1'] = self.calculate_explanation_clarity(structured_output)
            metrics['A6_1'] = self.calculate_guideline_compliance_rate(structured_output)
            metrics['A6_2'] = self.calculate_behavior_deviation_rate(structured_output)
            
            # メタ情報
            metrics['calculation_timestamp'] = datetime.utcnow().isoformat()
            metrics['schema_version'] = '1.0'
            metrics['total_score'] = sum(metrics[k] for k in metrics if k.startswith('A'))
            
        except Exception as e:
            metrics['error'] = str(e)
            metrics['calculation_status'] = 'failed'
        
        return metrics
    
    def safe_divide(self, numerator, denominator, default_value=0):
        """安全な除算"""
        if denominator < self.epsilon:
            return default_value
        return numerator / denominator
    
    def clamp_score(self, score, min_val=0, max_val=100):
        """スコアのクリッピング"""
        return max(min_val, min(max_val, score))
```

### SQL実装例（データベース集計用）

```sql
-- A1-1: 要件理解正確度の集計クエリ
WITH requirements_metrics AS (
    SELECT 
        session_id,
        -- 要件分解充足率
        CAST(SUM(CASE WHEN JSON_LENGTH(JSON_EXTRACT(req_item, '$.acceptance')) > 0 THEN 1 ELSE 0 END) AS DECIMAL) 
        / GREATEST(JSON_LENGTH(JSON_EXTRACT(structured_output, '$.requirements.req_items')), 1) AS req_coverage,
        
        -- 要件リンク率  
        CAST(SUM(CASE WHEN JSON_LENGTH(JSON_EXTRACT(decision, '$.req_links')) > 0 THEN 1 ELSE 0 END) AS DECIMAL)
        / GREATEST(COUNT(*), 1) AS req_link_rate
        
    FROM sessions s
    CROSS JOIN JSON_TABLE(
        JSON_EXTRACT(s.structured_output, '$.requirements.req_items'),
        '$[*]' COLUMNS (req_item JSON PATH '$')
    ) AS req_items
    CROSS JOIN JSON_TABLE(
        JSON_EXTRACT(s.structured_output, '$.steps[*].decisions'),
        '$[*]' COLUMNS (decision JSON PATH '$')
    ) AS decisions
    GROUP BY session_id
)
SELECT 
    session_id,
    ROUND(100 * (0.5 * req_coverage + 0.3 * req_link_rate + 0.2 * 0.8), 2) AS A1_1_score
FROM requirements_metrics;
```

---

## 🔧 エッジケースの処理

### データ不足ケース

```python
def handle_insufficient_data(structured_output, metric_name):
    """データ不足時の処理"""
    
    # 必須フィールドの存在確認
    required_fields = {
        'A1_1': ['requirements.req_items'],
        'A1_2': ['steps'],
        'A1_3': ['steps', 'final.decision_map'],
        'A2_1': ['checks.stability.recheck_runs'],
        'A2_2': ['definitions'],
        'A3_1': ['checks.explanation_clarity.rubric_scores'],
        'A6_1': ['guidelines.checks'],
        'A6_2': ['deviations', 'steps']
    }
    
    missing_fields = []
    for field_path in required_fields.get(metric_name, []):
        if not get_nested_value(structured_output, field_path):
            missing_fields.append(field_path)
    
    if missing_fields:
        return {
            'score': 0,
            'status': 'insufficient_data',
            'missing_fields': missing_fields,
            'message': f'Required fields missing for {metric_name}: {missing_fields}'
        }
    
    return None  # データ充足

def get_nested_value(data, path, default=None):
    """ネストしたディクショナリから値を安全に取得"""
    keys = path.split('.')
    current = data
    
    for key in keys:
        if isinstance(current, dict) and key in current:
            current = current[key]
        else:
            return default
    
    return current if current is not None else default
```

### 異常値の処理

```python
def detect_anomalies(metrics_results):
    """異常値検出と処理"""
    anomalies = []
    
    for metric_name, score in metrics_results.items():
        if not isinstance(score, (int, float)):
            continue
            
        # 異常値の判定
        if score < 0 or score > 100:
            anomalies.append({
                'metric': metric_name,
                'score': score,
                'issue': 'out_of_range'
            })
        elif metric_name.startswith('A') and score == 0:
            anomalies.append({
                'metric': metric_name,
                'score': score,
                'issue': 'potential_data_issue'
            })
    
    return anomalies

def sanitize_scores(metrics_results):
    """スコアの正規化"""
    for metric_name in metrics_results:
        if isinstance(metrics_results[metric_name], (int, float)):
            metrics_results[metric_name] = max(0, min(100, metrics_results[metric_name]))
    
    return metrics_results
```

---

## 🔍 トラブルシューティング

### よくある計算エラー

#### エラー1: ZeroDivisionError

**原因:**
```python
# 危険なコード
score = numerator / denominator  # denominatorが0の場合エラー
```

**対処法:**
```python
# 安全なコード
score = numerator / max(denominator, 1e-6)
# または
score = safe_divide(numerator, denominator, default_value=0)
```

#### エラー2: KeyError / AttributeError

**原因:**
```python
# 危険なコード
req_items = structured_output['requirements']['req_items']  # キーが存在しない場合エラー
```

**対処法:**
```python
# 安全なコード
req_items = structured_output.get('requirements', {}).get('req_items', [])
# または
req_items = get_nested_value(structured_output, 'requirements.req_items', [])
```

#### エラー3: 型エラー

**原因:**
```python
# 危険なコード
score = sum(scores) / len(scores)  # scoresが数値以外を含む場合
```

**対処法:**
```python
# 安全なコード
numeric_scores = [s for s in scores if isinstance(s, (int, float))]
score = sum(numeric_scores) / max(len(numeric_scores), 1) if numeric_scores else 0
```

### データ不足時の対処法

#### 対処パターン1: デフォルト値の使用

```python
def get_score_with_default(data, calculation_func, default_score=50):
    """データ不足時はデフォルトスコアを返す"""
    try:
        return calculation_func(data)
    except (KeyError, ValueError, TypeError) as e:
        logger.warning(f"Calculation failed, using default score: {e}")
        return default_score
```

#### 対処パターン2: 部分スコアの計算

```python
def calculate_partial_score(available_components):
    """利用可能なコンポーネントのみでスコア計算"""
    if not available_components:
        return 0
    
    # 利用可能なコンポーネントの重みを正規化
    total_weight = sum(component['weight'] for component in available_components)
    normalized_score = sum(
        component['score'] * component['weight'] for component in available_components
    ) / total_weight if total_weight > 0 else 0
    
    return normalized_score
```

#### 対処パターン3: 信頼度スコアの併用

```python
def calculate_with_confidence(structured_output):
    """信頼度付きでスコア計算"""
    required_fields = ['requirements', 'steps', 'final']
    available_fields = sum(1 for field in required_fields 
                          if field in structured_output and structured_output[field])
    
    confidence = available_fields / len(required_fields)
    
    if confidence < 0.5:
        return {
            'score': 0,
            'confidence': confidence,
            'status': 'low_confidence'
        }
    
    base_score = perform_calculation(structured_output)
    
    return {
        'score': base_score,
        'confidence': confidence,
        'status': 'normal'
    }
```

---

## 📈 バッチ処理実装例

### 大量セッション処理

```python
import asyncio
from concurrent.futures import ProcessPoolExecutor
import logging

class BatchMetricsProcessor:
    """バッチでのメトリクス処理"""
    
    def __init__(self, max_workers=4):
        self.calculator = AICQMetricsCalculator()
        self.max_workers = max_workers
        self.logger = logging.getLogger(__name__)
    
    async def process_sessions_batch(self, session_ids, batch_size=100):
        """セッション一括処理"""
        results = []
        
        for i in range(0, len(session_ids), batch_size):
            batch = session_ids[i:i+batch_size]
            self.logger.info(f"Processing batch {i//batch_size + 1}, size: {len(batch)}")
            
            with ProcessPoolExecutor(max_workers=self.max_workers) as executor:
                batch_results = await asyncio.get_event_loop().run_in_executor(
                    executor, self._process_batch, batch
                )
            
            results.extend(batch_results)
            
            # プログレス表示
            progress = min(100, (i + batch_size) / len(session_ids) * 100)
            self.logger.info(f"Progress: {progress:.1f}%")
        
        return results
    
    def _process_batch(self, session_ids):
        """バッチ内の個別処理"""
        batch_results = []
        
        for session_id in session_ids:
            try:
                # セッションデータ取得
                structured_output = self.load_session_data(session_id)
                
                # メトリクス計算
                metrics = self.calculator.calculate_all_metrics(structured_output)
                metrics['session_id'] = session_id
                
                batch_results.append(metrics)
                
            except Exception as e:
                self.logger.error(f"Failed to process session {session_id}: {e}")
                batch_results.append({
                    'session_id': session_id,
                    'error': str(e),
                    'status': 'failed'
                })
        
        return batch_results
```

このガイドを活用することで、AICQメトリクスの詳細な計算実装を確実に行うことができ、品質の高い監査ログ分析システムを構築できます。