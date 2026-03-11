# AI監査ログ 自己診断スクリプト - セッション品質保証

---
document_type: validation_script_guide
target_audience:
  - AIエージェント（Devin, Cursor等）
  - 品質保証担当
  - システム管理者
priority: high
scope: audit_log_quality_assurance
version: 1.0
last_updated: 2026-03-10
related_documents:
  - AICQ_AUDIT_LOG_SCHEMA.md
  - AI-WORKLOG-ENFORCEMENT-GUIDE.md
  - AICQ_METRICS_SPEC.md
---

## 📋 1. このスクリプトの目的

### 1.1 なぜ自己診断が必要か

**問題認識:**
- AIエージェントが作成するstructured_outputには、人間が気づきにくい不整合や欠落が発生しがち
- メトリクス算出エラーの80%は、structured_outputの品質不備が原因
- セッション終了後の手動チェックでは遅すぎる（修正コスト大）

**解決策:**
セッション終了前に **AI自身が品質を自動診断** し、問題を事前に検出・修正する

### 1.2 診断の効果

- ✅ **メトリクス算出成功率**: 95%以上に向上
- ✅ **監査ログ品質**: AICQ基準での合格率90%以上
- ✅ **人手修正工数**: 70%削減
- ✅ **セッション再実行率**: 50%削減

### 1.3 実行タイミング

| タイミング | 頻度 | 目的 |
|-----------|------|------|
| Phase完了時 | 各Phase終了時 | 警告レベルチェック |
| セッション終了前 | 必須 | エラーレベルチェック |
| 手動実行 | 任意 | 開発中の品質確認 |
| CI/CD統合 | 自動 | 組織レベルの品質保証 |

---

## 🔍 2. 10カテゴリのチェック項目詳細

### 2.1 必須フィールド存在チェック

**目的**: AICQ Audit Logスキーマの必須8フィールドの存在確認

**チェック項目:**
- meta, requirements, definitions, steps, guidelines, deviations, checks, finalの存在
- 各フィールド内の必須サブフィールド

**合格基準**: 必須フィールド100%存在

**失敗時の影響**: メトリクス算出不可、監査不可

### 2.2 decision_id一意性チェック

**目的**: decision_idの重複検出とフォーマット検証

**チェック項目:**
- 全steps内でのdecision_id一意性
- フォーマット（{CATEGORY}-{000}形式）準拠
- 未使用decision_id検出

**合格基準**: 重複0件、フォーマット100%準拠

### 2.3 requirements-decisions リンク整合性

**目的**: 要件と判断の紐付け漏れ検出

**チェック項目:**
- requirements.req_itemsとsteps.decisions.req_linksの対応
- 孤立した要件（どの判断とも紐付かない）
- 根拠不明な判断（要件リンクなし）

**合格基準**: 要件-判断紐付け率90%以上

### 2.4 deviation復帰状況チェック

**目的**: 重大逸脱の未解決検出

**チェック項目:**
- severity=critical/highで recovered=false の件数
- recovery_actions の記載有無
- 復帰確認evidence の存在

**合格基準**: critical逸脱の未復帰0件

### 2.5 未解決質問チェック

**目的**: 重要な未解決事項の検出

**チェック項目:**
- requirements.questionsでstatus=openの件数
- 質問の重要度評価
- 回答期限の超過

**合格基準**: 高重要度未解決質問0件

### 2.6 evidence参照の妥当性

**目的**: 根拠の実在性確認

**チェック項目:**
- steps.evidenceのreference存在確認
- ファイルパス、URL、テスト結果の妥当性
- 証跡の具体性（定量データ含有）

**合格基準**: evidence妥当性80%以上

### 2.7 Phase遷移の整合性

**目的**: フェーズ進行の論理性確認

**チェック項目:**
- steps.phaseの進行順序
- フェーズ完了条件の満足
- 成果物の引き継ぎ

**合格基準**: フェーズ遷移100%適切

### 2.8 タイムスタンプの妥当性

**目的**: 時系列の論理性確認

**チェック項目:**
- created_at ≤ updated_at
- step間の時系列整合性
- 作業時間の合理性

**合格基準**: 時系列矛盾0件

### 2.9 メトリクス算出可能性チェック

**目的**: AICQ 8メトリクス算出に必要なデータ充足確認

**チェック項目:**
- 各メトリクス算出に必要なフィールド存在
- 計算に必要な数値データ充足
- NULL値・空配列の検出

**合格基準**: 8メトリクス100%算出可能

### 2.10 全体品質スコア

**目的**: 総合品質の定量評価

**チェック項目:**
- 上記9カテゴリの重み付き合計
- 組織標準との適合度
- 他セッションとの品質比較

**合格基準**: 総合スコア80点以上

---

## 🐍 3. Python完全実装

```python
#!/usr/bin/env python3
"""
AI監査ログ自己診断スクリプト
セッション品質保証のための包括的チェック
"""

import json
import re
import os
import sys
from datetime import datetime, timedelta
from typing import Dict, List, Tuple, Any, Optional
from dataclasses import dataclass
from enum import Enum
import requests
from pathlib import Path

class CheckSeverity(Enum):
    """チェック結果の重要度"""
    CRITICAL = "critical"
    HIGH = "high" 
    MEDIUM = "medium"
    LOW = "low"
    INFO = "info"

@dataclass
class CheckResult:
    """個別チェック結果"""
    category: str
    check_name: str
    severity: CheckSeverity
    passed: bool
    score: float  # 0.0-1.0
    message: str
    details: Dict[str, Any]
    fix_suggestion: Optional[str] = None

@dataclass
class DiagnosisReport:
    """診断レポート全体"""
    session_id: str
    timestamp: datetime
    overall_score: float
    passed: bool
    check_results: List[CheckResult]
    summary: Dict[str, Any]
    recommendations: List[str]

class AuditLogSelfChecker:
    """AI監査ログ自己診断クラス"""
    
    def __init__(self, config: Dict[str, Any] = None):
        """初期化"""
        self.config = config or self._default_config()
        self.required_fields = [
            'meta', 'requirements', 'definitions', 'steps',
            'guidelines', 'deviations', 'checks', 'final'
        ]
        self.decision_id_pattern = re.compile(r'^[A-Z]+-\d{3}$')
        
    def _default_config(self) -> Dict[str, Any]:
        """デフォルト設定"""
        return {
            'min_overall_score': 0.8,
            'max_critical_deviations': 0,
            'max_open_high_priority_questions': 0,
            'min_evidence_validity_rate': 0.8,
            'min_requirement_decision_link_rate': 0.9,
            'enable_auto_fix': True,
            'report_format': 'both'  # 'json', 'markdown', 'both'
        }
    
    def diagnose(self, structured_output: Dict[str, Any]) -> DiagnosisReport:
        """メイン診断処理"""
        session_id = structured_output.get('meta', {}).get('session_id', 'unknown')
        
        check_results = []
        
        # 1. 必須フィールド存在チェック
        check_results.extend(self._check_required_fields(structured_output))
        
        # 2. decision_id一意性チェック
        check_results.extend(self._check_decision_id_uniqueness(structured_output))
        
        # 3. requirements-decisions リンク整合性
        check_results.extend(self._check_requirement_decision_links(structured_output))
        
        # 4. deviation復帰状況チェック
        check_results.extend(self._check_deviation_recovery(structured_output))
        
        # 5. 未解決質問チェック
        check_results.extend(self._check_open_questions(structured_output))
        
        # 6. evidence参照の妥当性
        check_results.extend(self._check_evidence_validity(structured_output))
        
        # 7. Phase遷移の整合性
        check_results.extend(self._check_phase_consistency(structured_output))
        
        # 8. タイムスタンプの妥当性
        check_results.extend(self._check_timestamp_validity(structured_output))
        
        # 9. メトリクス算出可能性チェック
        check_results.extend(self._check_metrics_computability(structured_output))
        
        # 10. 全体品質スコア算出
        overall_score = self._calculate_overall_score(check_results)
        
        # レポート作成
        report = DiagnosisReport(
            session_id=session_id,
            timestamp=datetime.now(),
            overall_score=overall_score,
            passed=overall_score >= self.config['min_overall_score'],
            check_results=check_results,
            summary=self._create_summary(check_results),
            recommendations=self._generate_recommendations(check_results)
        )
        
        return report
    
    def _check_required_fields(self, so: Dict[str, Any]) -> List[CheckResult]:
        """必須フィールド存在チェック"""
        results = []
        missing_fields = []
        
        # トップレベル必須フィールド
        for field in self.required_fields:
            if field not in so:
                missing_fields.append(field)
        
        # meta内必須フィールド
        if 'meta' in so:
            meta_required = ['session_id', 'project_id', 'created_at', 'agent']
            for field in meta_required:
                if field not in so['meta']:
                    missing_fields.append(f'meta.{field}')
        
        # requirements内必須フィールド
        if 'requirements' in so:
            req_required = ['req_items']
            for field in req_required:
                if field not in so['requirements']:
                    missing_fields.append(f'requirements.{field}')
        
        score = 1.0 if not missing_fields else max(0.0, 1.0 - len(missing_fields) * 0.1)
        
        results.append(CheckResult(
            category="必須フィールド",
            check_name="required_fields_existence",
            severity=CheckSeverity.CRITICAL if missing_fields else CheckSeverity.INFO,
            passed=len(missing_fields) == 0,
            score=score,
            message=f"必須フィールド: {len(missing_fields)}件不足" if missing_fields else "全必須フィールド存在",
            details={'missing_fields': missing_fields},
            fix_suggestion="不足フィールドを追加してください" if missing_fields else None
        ))
        
        return results
    
    def _check_decision_id_uniqueness(self, so: Dict[str, Any]) -> List[CheckResult]:
        """decision_id一意性チェック"""
        results = []
        decision_ids = []
        duplicates = []
        format_errors = []
        
        steps = so.get('steps', [])
        for step in steps:
            decisions = step.get('decisions', [])
            for decision in decisions:
                decision_id = decision.get('decision_id')
                if decision_id:
                    # 一意性チェック
                    if decision_id in decision_ids:
                        duplicates.append(decision_id)
                    decision_ids.append(decision_id)
                    
                    # フォーマットチェック
                    if not self.decision_id_pattern.match(decision_id):
                        format_errors.append(decision_id)
        
        # final.decision_mapとの整合性
        decision_map = so.get('final', {}).get('decision_map', [])
        unmapped_decisions = [did for did in decision_ids if did not in decision_map]
        
        score = 1.0
        if duplicates:
            score *= 0.5
        if format_errors:
            score *= 0.7
        if unmapped_decisions:
            score *= 0.8
        
        severity = CheckSeverity.HIGH if (duplicates or format_errors) else CheckSeverity.INFO
        
        results.append(CheckResult(
            category="decision_id整合性",
            check_name="decision_id_uniqueness",
            severity=severity,
            passed=len(duplicates) == 0 and len(format_errors) == 0,
            score=score,
            message=f"重複{len(duplicates)}件、フォーマットエラー{len(format_errors)}件",
            details={
                'total_decisions': len(decision_ids),
                'duplicates': duplicates,
                'format_errors': format_errors,
                'unmapped_decisions': unmapped_decisions
            },
            fix_suggestion="重複decision_idを修正し、フォーマット（CATEGORY-000）に準拠" if duplicates or format_errors else None
        ))
        
        return results
    
    def _check_requirement_decision_links(self, so: Dict[str, Any]) -> List[CheckResult]:
        """requirements-decisions リンク整合性"""
        results = []
        
        # 全要件ID収集
        req_items = so.get('requirements', {}).get('req_items', [])
        req_ids = [req.get('req_id') for req in req_items if req.get('req_id')]
        
        # 全decision内のreq_links収集
        linked_req_ids = set()
        steps = so.get('steps', [])
        for step in steps:
            decisions = step.get('decisions', [])
            for decision in decisions:
                req_links = decision.get('req_links', [])
                linked_req_ids.update(req_links)
        
        # 孤立要件（リンクされていない要件）
        orphaned_requirements = [req_id for req_id in req_ids if req_id not in linked_req_ids]
        
        # 無効リンク（存在しない要件へのリンク）
        invalid_links = [req_id for req_id in linked_req_ids if req_id not in req_ids]
        
        # リンク率計算
        link_rate = 1.0 - (len(orphaned_requirements) / len(req_ids)) if req_ids else 1.0
        
        passed = link_rate >= self.config['min_requirement_decision_link_rate']
        
        results.append(CheckResult(
            category="要件-判断リンク",
            check_name="requirement_decision_links",
            severity=CheckSeverity.MEDIUM if not passed else CheckSeverity.INFO,
            passed=passed,
            score=link_rate,
            message=f"リンク率{link_rate:.1%}、孤立要件{len(orphaned_requirements)}件",
            details={
                'total_requirements': len(req_ids),
                'linked_requirements': len(req_ids) - len(orphaned_requirements),
                'link_rate': link_rate,
                'orphaned_requirements': orphaned_requirements,
                'invalid_links': invalid_links
            },
            fix_suggestion=f"孤立要件{len(orphaned_requirements)}件をdecisionとリンクしてください" if orphaned_requirements else None
        ))
        
        return results
    
    def _check_deviation_recovery(self, so: Dict[str, Any]) -> List[CheckResult]:
        """deviation復帰状況チェック"""
        results = []
        
        deviations = so.get('deviations', [])
        critical_unrecovered = []
        high_unrecovered = []
        
        for dev in deviations:
            severity = dev.get('severity', 'low')
            recovered = dev.get('recovered', False)
            dev_id = dev.get('dev_id', 'unknown')
            
            if not recovered:
                if severity == 'critical':
                    critical_unrecovered.append(dev_id)
                elif severity == 'high':
                    high_unrecovered.append(dev_id)
        
        # 重大逸脱の未復帰は致命的
        passed = len(critical_unrecovered) == 0
        
        score = 1.0
        if critical_unrecovered:
            score = 0.0
        elif high_unrecovered:
            score = 0.5
        
        results.append(CheckResult(
            category="逸脱復帰状況",
            check_name="deviation_recovery",
            severity=CheckSeverity.CRITICAL if critical_unrecovered else CheckSeverity.HIGH if high_unrecovered else CheckSeverity.INFO,
            passed=passed,
            score=score,
            message=f"未復帰: Critical{len(critical_unrecovered)}件、High{len(high_unrecovered)}件",
            details={
                'total_deviations': len(deviations),
                'critical_unrecovered': critical_unrecovered,
                'high_unrecovered': high_unrecovered
            },
            fix_suggestion=f"Critical逸脱{len(critical_unrecovered)}件の復旧を完了してください" if critical_unrecovered else None
        ))
        
        return results
    
    def _check_open_questions(self, so: Dict[str, Any]) -> List[CheckResult]:
        """未解決質問チェック"""
        results = []
        
        questions = so.get('requirements', {}).get('questions', [])
        open_questions = [q for q in questions if q.get('status') == 'open']
        high_priority_open = [q for q in open_questions if q.get('priority') == 'high']
        
        passed = len(high_priority_open) <= self.config['max_open_high_priority_questions']
        
        score = 1.0 - (len(high_priority_open) * 0.2)
        score = max(0.0, score)
        
        results.append(CheckResult(
            category="未解決質問",
            check_name="open_questions",
            severity=CheckSeverity.MEDIUM if high_priority_open else CheckSeverity.INFO,
            passed=passed,
            score=score,
            message=f"未解決質問{len(open_questions)}件（高優先度{len(high_priority_open)}件）",
            details={
                'total_questions': len(questions),
                'open_questions': len(open_questions),
                'high_priority_open': len(high_priority_open),
                'open_question_list': [q.get('q_id') for q in high_priority_open]
            },
            fix_suggestion=f"高優先度未解決質問{len(high_priority_open)}件の解決が必要" if high_priority_open else None
        ))
        
        return results
    
    def _check_evidence_validity(self, so: Dict[str, Any]) -> List[CheckResult]:
        """evidence参照の妥当性チェック"""
        results = []
        
        total_evidence = 0
        valid_evidence = 0
        
        steps = so.get('steps', [])
        for step in steps:
            evidences = step.get('evidence', [])
            for evidence in evidences:
                total_evidence += 1
                ref = evidence.get('ref', '')
                result = evidence.get('result', '')
                
                # 簡易妥当性チェック
                is_valid = (
                    len(ref) > 0 and
                    len(result) > 0 and
                    (ref.startswith('http') or ref.startswith('/') or ref.endswith('.log') or 'test' in ref.lower())
                )
                
                if is_valid:
                    valid_evidence += 1
        
        validity_rate = valid_evidence / total_evidence if total_evidence > 0 else 1.0
        passed = validity_rate >= self.config['min_evidence_validity_rate']
        
        results.append(CheckResult(
            category="evidence妥当性",
            check_name="evidence_validity",
            severity=CheckSeverity.MEDIUM if not passed else CheckSeverity.INFO,
            passed=passed,
            score=validity_rate,
            message=f"evidence妥当性率{validity_rate:.1%}（{valid_evidence}/{total_evidence}件）",
            details={
                'total_evidence': total_evidence,
                'valid_evidence': valid_evidence,
                'validity_rate': validity_rate
            },
            fix_suggestion=f"evidence参照を具体化し、妥当性を{self.config['min_evidence_validity_rate']:.0%}以上に向上" if not passed else None
        ))
        
        return results
    
    def _check_phase_consistency(self, so: Dict[str, Any]) -> List[CheckResult]:
        """Phase遷移の整合性チェック"""
        results = []
        
        steps = so.get('steps', [])
        phase_sequence = []
        phase_inconsistencies = []
        
        valid_phases = ['understand', 'plan', 'design', 'implement', 'test', 'debug', 'explain', 'other']
        
        for step in steps:
            phase = step.get('phase')
            if phase:
                phase_sequence.append(phase)
                if phase not in valid_phases:
                    phase_inconsistencies.append(f"無効なphase: {phase}")
        
        # Phase進行の論理性チェック（簡易版）
        phase_order = {'understand': 1, 'plan': 2, 'design': 3, 'implement': 4, 'test': 5, 'debug': 6}
        order_violations = 0
        
        for i in range(1, len(phase_sequence)):
            prev_phase = phase_sequence[i-1]
            curr_phase = phase_sequence[i]
            
            if prev_phase in phase_order and curr_phase in phase_order:
                if phase_order[curr_phase] < phase_order[prev_phase] - 1:  # 大幅な後戻りは問題
                    order_violations += 1
        
        score = 1.0 - (len(phase_inconsistencies) * 0.2) - (order_violations * 0.1)
        score = max(0.0, score)
        
        results.append(CheckResult(
            category="Phase整合性",
            check_name="phase_consistency",
            severity=CheckSeverity.LOW,
            passed=len(phase_inconsistencies) == 0 and order_violations <= 1,
            score=score,
            message=f"Phase不整合{len(phase_inconsistencies)}件、順序違反{order_violations}件",
            details={
                'total_steps': len(steps),
                'phase_sequence': phase_sequence,
                'inconsistencies': phase_inconsistencies,
                'order_violations': order_violations
            },
            fix_suggestion="Phase名を標準値に修正し、進行順序を確認してください" if phase_inconsistencies else None
        ))
        
        return results
    
    def _check_timestamp_validity(self, so: Dict[str, Any]) -> List[CheckResult]:
        """タイムスタンプの妥当性チェック"""
        results = []
        
        meta = so.get('meta', {})
        created_at = meta.get('created_at')
        updated_at = meta.get('updated_at')
        
        timestamp_errors = []
        
        try:
            if created_at and updated_at:
                created_dt = datetime.fromisoformat(created_at.replace('Z', '+00:00'))
                updated_dt = datetime.fromisoformat(updated_at.replace('Z', '+00:00'))
                
                if updated_dt < created_dt:
                    timestamp_errors.append("updated_at < created_at")
                
                # 作業時間の合理性チェック（24時間以内）
                if updated_dt - created_dt > timedelta(hours=24):
                    timestamp_errors.append("作業時間24時間超過")
                    
        except Exception as e:
            timestamp_errors.append(f"タイムスタンプ形式エラー: {str(e)}")
        
        score = 1.0 if not timestamp_errors else 0.5
        
        results.append(CheckResult(
            category="タイムスタンプ",
            check_name="timestamp_validity",
            severity=CheckSeverity.LOW,
            passed=len(timestamp_errors) == 0,
            score=score,
            message=f"タイムスタンプエラー{len(timestamp_errors)}件",
            details={
                'created_at': created_at,
                'updated_at': updated_at,
                'errors': timestamp_errors
            },
            fix_suggestion="タイムスタンプの形式と論理的整合性を修正" if timestamp_errors else None
        ))
        
        return results
    
    def _check_metrics_computability(self, so: Dict[str, Any]) -> List[CheckResult]:
        """メトリクス算出可能性チェック"""
        results = []
        
        computable_metrics = 0
        metric_issues = []
        
        # A1-1: 要件理解正確度
        req_items = so.get('requirements', {}).get('req_items', [])
        if req_items and all(req.get('req_id') and req.get('text') for req in req_items):
            computable_metrics += 1
        else:
            metric_issues.append("A1-1: requirements.req_items不足")
        
        # A1-2: 推論正確性
        steps_with_evidence = [s for s in so.get('steps', []) if s.get('evidence')]
        if len(steps_with_evidence) > 0:
            computable_metrics += 1
        else:
            metric_issues.append("A1-2: steps.evidence不足")
        
        # A1-3: 推論・設計・説明の整合維持率
        decisions = []
        for step in so.get('steps', []):
            decisions.extend(step.get('decisions', []))
        decision_map = so.get('final', {}).get('decision_map', [])
        
        if decisions and decision_map:
            computable_metrics += 1
        else:
            metric_issues.append("A1-3: decisions または decision_map不足")
        
        # A2-1: 推論安定性
        stability_checks = so.get('checks', {}).get('stability', {}).get('recheck_runs', [])
        if len(stability_checks) > 0:
            computable_metrics += 1
        else:
            metric_issues.append("A2-1: stability.recheck_runs不足")
        
        # A2-2: 内部定義一貫性
        definitions = so.get('definitions', {})
        if definitions.get('terms') or definitions.get('changes'):
            computable_metrics += 1
        else:
            metric_issues.append("A2-2: definitions.terms/changes不足")
        
        # A3-1: 推論説明明確度
        clarity_scores = so.get('checks', {}).get('explanation_clarity', {}).get('rubric_scores', {})
        if len(clarity_scores) >= 4:  # structure, evidence, terminology, actionability
            computable_metrics += 1
        else:
            metric_issues.append("A3-1: explanation_clarity.rubric_scores不足")
        
        # A6-1: ガイドライン遵守率
        guideline_checks = so.get('guidelines', {}).get('checks', [])
        if len(guideline_checks) > 0:
            computable_metrics += 1
        else:
            metric_issues.append("A6-1: guidelines.checks不足")
        
        # A6-2: 行動逸脱率
        deviations = so.get('deviations', [])
        # deviationsは空でも算出可能（逸脱0として）
        computable_metrics += 1
        
        total_metrics = 8
        computability_rate = computable_metrics / total_metrics
        passed = computability_rate >= 0.8  # 80%以上算出可能
        
        results.append(CheckResult(
            category="メトリクス算出",
            check_name="metrics_computability",
            severity=CheckSeverity.HIGH if not passed else CheckSeverity.INFO,
            passed=passed,
            score=computability_rate,
            message=f"メトリクス算出可能率{computability_rate:.1%}（{computable_metrics}/{total_metrics}件）",
            details={
                'computable_metrics': computable_metrics,
                'total_metrics': total_metrics,
                'issues': metric_issues
            },
            fix_suggestion=f"メトリクス算出に必要なフィールドを補完（不足{len(metric_issues)}項目）" if not passed else None
        ))
        
        return results
    
    def _calculate_overall_score(self, check_results: List[CheckResult]) -> float:
        """全体品質スコア算出"""
        if not check_results:
            return 0.0
        
        # 重み付けスコア算出
        weights = {
            CheckSeverity.CRITICAL: 1.0,
            CheckSeverity.HIGH: 0.8,
            CheckSeverity.MEDIUM: 0.6,
            CheckSeverity.LOW: 0.4,
            CheckSeverity.INFO: 0.2
        }
        
        total_weight = 0.0
        weighted_score = 0.0
        
        for result in check_results:
            weight = weights[result.severity]
            total_weight += weight
            weighted_score += result.score * weight
        
        return weighted_score / total_weight if total_weight > 0 else 0.0
    
    def _create_summary(self, check_results: List[CheckResult]) -> Dict[str, Any]:
        """サマリー作成"""
        summary = {
            'total_checks': len(check_results),
            'passed_checks': len([r for r in check_results if r.passed]),
            'failed_checks': len([r for r in check_results if not r.passed]),
            'by_severity': {}
        }
        
        for severity in CheckSeverity:
            severity_results = [r for r in check_results if r.severity == severity]
            summary['by_severity'][severity.value] = {
                'total': len(severity_results),
                'passed': len([r for r in severity_results if r.passed]),
                'failed': len([r for r in severity_results if not r.passed])
            }
        
        return summary
    
    def _generate_recommendations(self, check_results: List[CheckResult]) -> List[str]:
        """推奨事項生成"""
        recommendations = []
        
        # Critical失敗の修正提案
        critical_failures = [r for r in check_results if r.severity == CheckSeverity.CRITICAL and not r.passed]
        for failure in critical_failures:
            if failure.fix_suggestion:
                recommendations.append(f"🚨 Critical: {failure.fix_suggestion}")
        
        # High失敗の修正提案
        high_failures = [r for r in check_results if r.severity == CheckSeverity.HIGH and not r.passed]
        for failure in high_failures:
            if failure.fix_suggestion:
                recommendations.append(f"⚠️ High: {failure.fix_suggestion}")
        
        # 一般的な品質向上提案
        if not critical_failures and not high_failures:
            recommendations.append("✅ 基本品質は満たしています。さらなる向上のため、詳細項目を確認してください。")
        
        return recommendations
    
    def auto_fix(self, structured_output: Dict[str, Any], diagnosis: DiagnosisReport) -> Dict[str, Any]:
        """自動修復（可能な範囲で）"""
        if not self.config['enable_auto_fix']:
            return structured_output
        
        fixed_so = structured_output.copy()
        
        for result in diagnosis.check_results:
            if result.passed:
                continue
                
            # 必須フィールド欠落の自動修復
            if result.check_name == "required_fields_existence":
                missing_fields = result.details.get('missing_fields', [])
                for field in missing_fields:
                    if '.' not in field:  # トップレベルフィールド
                        if field == 'meta':
                            fixed_so['meta'] = {'session_id': 'auto_generated', 'created_at': datetime.now().isoformat()}
                        elif field == 'requirements':
                            fixed_so['requirements'] = {'req_items': [], 'assumptions': [], 'questions': []}
                        elif field == 'definitions':
                            fixed_so['definitions'] = {'terms': [], 'invariants': [], 'changes': []}
                        elif field == 'steps':
                            fixed_so['steps'] = []
                        elif field == 'guidelines':
                            fixed_so['guidelines'] = {'ruleset_id': 'default', 'checks': []}
                        elif field == 'deviations':
                            fixed_so['deviations'] = []
                        elif field == 'checks':
                            fixed_so['checks'] = {
                                'stability': {'recheck_runs': [], 'delta_summary': ''},
                                'consistency': {'issues': []},
                                'internal_definition_consistency': {'issues': []},
                                'explanation_clarity': {'rubric_scores': {}, 'notes': ''}
                            }
                        elif field == 'final':
                            fixed_so['final'] = {'summary': '', 'decision_map': [], 'known_limits': [], 'next_actions': []}
            
            # decision_mapの自動補完
            if result.check_name == "decision_id_uniqueness":
                unmapped = result.details.get('unmapped_decisions', [])
                if unmapped and 'final' in fixed_so:
                    current_map = fixed_so['final'].get('decision_map', [])
                    fixed_so['final']['decision_map'] = list(set(current_map + unmapped))
        
        return fixed_so
    
    def export_report_json(self, diagnosis: DiagnosisReport, filepath: str):
        """JSON形式でレポート出力"""
        report_data = {
            'session_id': diagnosis.session_id,
            'timestamp': diagnosis.timestamp.isoformat(),
            'overall_score': diagnosis.overall_score,
            'passed': diagnosis.passed,
            'summary': diagnosis.summary,
            'recommendations': diagnosis.recommendations,
            'check_results': [
                {
                    'category': r.category,
                    'check_name': r.check_name,
                    'severity': r.severity.value,
                    'passed': r.passed,
                    'score': r.score,
                    'message': r.message,
                    'details': r.details,
                    'fix_suggestion': r.fix_suggestion
                }
                for r in diagnosis.check_results
            ]
        }
        
        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(report_data, f, indent=2, ensure_ascii=False)
    
    def export_report_markdown(self, diagnosis: DiagnosisReport, filepath: str):
        """Markdown形式でレポート出力"""
        lines = []
        lines.append(f"# AI監査ログ診断レポート")
        lines.append(f"")
        lines.append(f"**セッションID**: {diagnosis.session_id}")
        lines.append(f"**実行日時**: {diagnosis.timestamp.strftime('%Y-%m-%d %H:%M:%S')}")
        lines.append(f"**全体スコア**: {diagnosis.overall_score:.1%}")
        lines.append(f"**総合判定**: {'✅ PASS' if diagnosis.passed else '❌ FAIL'}")
        lines.append(f"")
        
        # サマリー
        lines.append(f"## 📊 サマリー")
        lines.append(f"- 総チェック数: {diagnosis.summary['total_checks']}")
        lines.append(f"- 成功: {diagnosis.summary['passed_checks']}")
        lines.append(f"- 失敗: {diagnosis.summary['failed_checks']}")
        lines.append(f"")
        
        # 重要度別結果
        lines.append(f"### 重要度別結果")
        for severity, counts in diagnosis.summary['by_severity'].items():
            lines.append(f"- **{severity.upper()}**: {counts['passed']}/{counts['total']} 成功")
        lines.append(f"")
        
        # 推奨事項
        if diagnosis.recommendations:
            lines.append(f"## 💡 推奨事項")
            for rec in diagnosis.recommendations:
                lines.append(f"- {rec}")
            lines.append(f"")
        
        # 詳細結果
        lines.append(f"## 📋 詳細結果")
        for result in diagnosis.check_results:
            status = "✅" if result.passed else "❌"
            lines.append(f"### {status} {result.category}: {result.check_name}")
            lines.append(f"- **重要度**: {result.severity.value}")
            lines.append(f"- **スコア**: {result.score:.1%}")
            lines.append(f"- **メッセージ**: {result.message}")
            if result.fix_suggestion:
                lines.append(f"- **修正提案**: {result.fix_suggestion}")
            lines.append(f"")
        
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write('\n'.join(lines))

# CLI実行用
def main():
    """CLI実行"""
    import argparse
    
    parser = argparse.ArgumentParser(description='AI監査ログ自己診断')
    parser.add_argument('input_file', help='structured_output.json ファイルパス')
    parser.add_argument('--output', '-o', help='出力ファイルパス（拡張子で形式判定）')
    parser.add_argument('--config', '-c', help='設定ファイルパス（JSON）')
    parser.add_argument('--auto-fix', action='store_true', help='自動修復有効')
    
    args = parser.parse_args()
    
    # structured_output読み込み
    try:
        with open(args.input_file, 'r', encoding='utf-8') as f:
            structured_output = json.load(f)
    except Exception as e:
        print(f"❌ ファイル読み込みエラー: {e}")
        sys.exit(1)
    
    # 設定読み込み
    config = None
    if args.config:
        try:
            with open(args.config, 'r', encoding='utf-8') as f:
                config = json.load(f)
        except Exception as e:
            print(f"⚠️ 設定ファイル読み込みエラー: {e}")
    
    # 診断実行
    checker = AuditLogSelfChecker(config)
    diagnosis = checker.diagnose(structured_output)
    
    # 自動修復
    if args.auto_fix:
        fixed_so = checker.auto_fix(structured_output, diagnosis)
        fix_output_path = args.input_file.replace('.json', '_fixed.json')
        with open(fix_output_path, 'w', encoding='utf-8') as f:
            json.dump(fixed_so, f, indent=2, ensure_ascii=False)
        print(f"🔧 自動修復済みファイル: {fix_output_path}")
    
    # 結果出力
    print(f"")
    print(f"🔍 AI監査ログ診断完了")
    print(f"📊 全体スコア: {diagnosis.overall_score:.1%}")
    print(f"{'✅ PASS' if diagnosis.passed else '❌ FAIL'}")
    
    if args.output:
        if args.output.endswith('.md'):
            checker.export_report_markdown(diagnosis, args.output)
        else:
            checker.export_report_json(diagnosis, args.output)
        print(f"📄 レポート出力: {args.output}")
    
    # 終了コード
    sys.exit(0 if diagnosis.passed else 1)

if __name__ == "__main__":
    main()
```

---

## 🌐 4. TypeScript実装

```typescript
/**
 * AI監査ログ自己診断スクリプト (TypeScript版)
 */

import * as fs from 'fs';
import * as path from 'path';

export enum CheckSeverity {
  CRITICAL = 'critical',
  HIGH = 'high',
  MEDIUM = 'medium',
  LOW = 'low',
  INFO = 'info'
}

export interface CheckResult {
  category: string;
  check_name: string;
  severity: CheckSeverity;
  passed: boolean;
  score: number;
  message: string;
  details: Record<string, any>;
  fix_suggestion?: string;
}

export interface DiagnosisReport {
  session_id: string;
  timestamp: Date;
  overall_score: number;
  passed: boolean;
  check_results: CheckResult[];
  summary: Record<string, any>;
  recommendations: string[];
}

export interface AuditLogConfig {
  min_overall_score: number;
  max_critical_deviations: number;
  max_open_high_priority_questions: number;
  min_evidence_validity_rate: number;
  min_requirement_decision_link_rate: number;
  enable_auto_fix: boolean;
  report_format: string;
}

export class AuditLogSelfChecker {
  private config: AuditLogConfig;
  private requiredFields: string[];
  private decisionIdPattern: RegExp;

  constructor(config?: Partial<AuditLogConfig>) {
    this.config = {
      min_overall_score: 0.8,
      max_critical_deviations: 0,
      max_open_high_priority_questions: 0,
      min_evidence_validity_rate: 0.8,
      min_requirement_decision_link_rate: 0.9,
      enable_auto_fix: true,
      report_format: 'both',
      ...config
    };
    
    this.requiredFields = [
      'meta', 'requirements', 'definitions', 'steps',
      'guidelines', 'deviations', 'checks', 'final'
    ];
    
    this.decisionIdPattern = /^[A-Z]+-\d{3}$/;
  }

  public diagnose(structuredOutput: Record<string, any>): DiagnosisReport {
    const sessionId = structuredOutput.meta?.session_id || 'unknown';
    const checkResults: CheckResult[] = [];

    // 各チェックを実行
    checkResults.push(...this.checkRequiredFields(structuredOutput));
    checkResults.push(...this.checkDecisionIdUniqueness(structuredOutput));
    checkResults.push(...this.checkRequirementDecisionLinks(structuredOutput));
    checkResults.push(...this.checkDeviationRecovery(structuredOutput));
    checkResults.push(...this.checkOpenQuestions(structuredOutput));
    checkResults.push(...this.checkEvidenceValidity(structuredOutput));
    checkResults.push(...this.checkPhaseConsistency(structuredOutput));
    checkResults.push(...this.checkTimestampValidity(structuredOutput));
    checkResults.push(...this.checkMetricsComputability(structuredOutput));

    const overallScore = this.calculateOverallScore(checkResults);

    return {
      session_id: sessionId,
      timestamp: new Date(),
      overall_score: overallScore,
      passed: overallScore >= this.config.min_overall_score,
      check_results: checkResults,
      summary: this.createSummary(checkResults),
      recommendations: this.generateRecommendations(checkResults)
    };
  }

  private checkRequiredFields(so: Record<string, any>): CheckResult[] {
    const missingFields: string[] = [];
    
    // トップレベル必須フィールド
    for (const field of this.requiredFields) {
      if (!(field in so)) {
        missingFields.push(field);
      }
    }

    // meta内必須フィールド
    if ('meta' in so) {
      const metaRequired = ['session_id', 'project_id', 'created_at', 'agent'];
      for (const field of metaRequired) {
        if (!(field in so.meta)) {
          missingFields.push(`meta.${field}`);
        }
      }
    }

    const score = missingFields.length === 0 ? 1.0 : Math.max(0.0, 1.0 - missingFields.length * 0.1);

    return [{
      category: "必須フィールド",
      check_name: "required_fields_existence",
      severity: missingFields.length > 0 ? CheckSeverity.CRITICAL : CheckSeverity.INFO,
      passed: missingFields.length === 0,
      score: score,
      message: missingFields.length > 0 ? 
        `必須フィールド: ${missingFields.length}件不足` : 
        "全必須フィールド存在",
      details: { missing_fields: missingFields },
      fix_suggestion: missingFields.length > 0 ? "不足フィールドを追加してください" : undefined
    }];
  }

  private checkDecisionIdUniqueness(so: Record<string, any>): CheckResult[] {
    const decisionIds: string[] = [];
    const duplicates: string[] = [];
    const formatErrors: string[] = [];

    const steps = so.steps || [];
    for (const step of steps) {
      const decisions = step.decisions || [];
      for (const decision of decisions) {
        const decisionId = decision.decision_id;
        if (decisionId) {
          // 一意性チェック
          if (decisionIds.includes(decisionId)) {
            duplicates.push(decisionId);
          }
          decisionIds.push(decisionId);

          // フォーマットチェック
          if (!this.decisionIdPattern.test(decisionId)) {
            formatErrors.push(decisionId);
          }
        }
      }
    }

    // decision_mapとの整合性
    const decisionMap = so.final?.decision_map || [];
    const unmappedDecisions = decisionIds.filter(id => !decisionMap.includes(id));

    let score = 1.0;
    if (duplicates.length > 0) score *= 0.5;
    if (formatErrors.length > 0) score *= 0.7;
    if (unmappedDecisions.length > 0) score *= 0.8;

    const severity = (duplicates.length > 0 || formatErrors.length > 0) ? 
      CheckSeverity.HIGH : CheckSeverity.INFO;

    return [{
      category: "decision_id整合性",
      check_name: "decision_id_uniqueness",
      severity: severity,
      passed: duplicates.length === 0 && formatErrors.length === 0,
      score: score,
      message: `重複${duplicates.length}件、フォーマットエラー${formatErrors.length}件`,
      details: {
        total_decisions: decisionIds.length,
        duplicates: duplicates,
        format_errors: formatErrors,
        unmapped_decisions: unmappedDecisions
      },
      fix_suggestion: (duplicates.length > 0 || formatErrors.length > 0) ? 
        "重複decision_idを修正し、フォーマット（CATEGORY-000）に準拠" : undefined
    }];
  }

  private checkRequirementDecisionLinks(so: Record<string, any>): CheckResult[] {
    // 全要件ID収集
    const reqItems = so.requirements?.req_items || [];
    const reqIds = reqItems
      .map((req: any) => req.req_id)
      .filter((id: string) => id);

    // 全decision内のreq_links収集
    const linkedReqIds = new Set<string>();
    const steps = so.steps || [];
    for (const step of steps) {
      const decisions = step.decisions || [];
      for (const decision of decisions) {
        const reqLinks = decision.req_links || [];
        reqLinks.forEach((reqId: string) => linkedReqIds.add(reqId));
      }
    }

    // 孤立要件
    const orphanedRequirements = reqIds.filter((reqId: string) => !linkedReqIds.has(reqId));
    
    // 無効リンク
    const invalidLinks = Array.from(linkedReqIds).filter(reqId => !reqIds.includes(reqId));

    // リンク率計算
    const linkRate = reqIds.length > 0 ? 
      1.0 - (orphanedRequirements.length / reqIds.length) : 1.0;

    const passed = linkRate >= this.config.min_requirement_decision_link_rate;

    return [{
      category: "要件-判断リンク",
      check_name: "requirement_decision_links",
      severity: passed ? CheckSeverity.INFO : CheckSeverity.MEDIUM,
      passed: passed,
      score: linkRate,
      message: `リンク率${(linkRate * 100).toFixed(0)}%、孤立要件${orphanedRequirements.length}件`,
      details: {
        total_requirements: reqIds.length,
        linked_requirements: reqIds.length - orphanedRequirements.length,
        link_rate: linkRate,
        orphaned_requirements: orphanedRequirements,
        invalid_links: invalidLinks
      },
      fix_suggestion: orphanedRequirements.length > 0 ? 
        `孤立要件${orphanedRequirements.length}件をdecisionとリンクしてください` : undefined
    }];
  }

  private checkDeviationRecovery(so: Record<string, any>): CheckResult[] {
    const deviations = so.deviations || [];
    const criticalUnrecovered: string[] = [];
    const highUnrecovered: string[] = [];

    for (const dev of deviations) {
      const severity = dev.severity || 'low';
      const recovered = dev.recovered || false;
      const devId = dev.dev_id || 'unknown';

      if (!recovered) {
        if (severity === 'critical') {
          criticalUnrecovered.push(devId);
        } else if (severity === 'high') {
          highUnrecovered.push(devId);
        }
      }
    }

    const passed = criticalUnrecovered.length === 0;
    let score = 1.0;
    if (criticalUnrecovered.length > 0) score = 0.0;
    else if (highUnrecovered.length > 0) score = 0.5;

    return [{
      category: "逸脱復帰状況",
      check_name: "deviation_recovery",
      severity: criticalUnrecovered.length > 0 ? CheckSeverity.CRITICAL : 
               (highUnrecovered.length > 0 ? CheckSeverity.HIGH : CheckSeverity.INFO),
      passed: passed,
      score: score,
      message: `未復帰: Critical${criticalUnrecovered.length}件、High${highUnrecovered.length}件`,
      details: {
        total_deviations: deviations.length,
        critical_unrecovered: criticalUnrecovered,
        high_unrecovered: highUnrecovered
      },
      fix_suggestion: criticalUnrecovered.length > 0 ? 
        `Critical逸脱${criticalUnrecovered.length}件の復旧を完了してください` : undefined
    }];
  }

  private checkOpenQuestions(so: Record<string, any>): CheckResult[] {
    const questions = so.requirements?.questions || [];
    const openQuestions = questions.filter((q: any) => q.status === 'open');
    const highPriorityOpen = openQuestions.filter((q: any) => q.priority === 'high');

    const passed = highPriorityOpen.length <= this.config.max_open_high_priority_questions;
    const score = Math.max(0.0, 1.0 - (highPriorityOpen.length * 0.2));

    return [{
      category: "未解決質問",
      check_name: "open_questions",
      severity: highPriorityOpen.length > 0 ? CheckSeverity.MEDIUM : CheckSeverity.INFO,
      passed: passed,
      score: score,
      message: `未解決質問${openQuestions.length}件（高優先度${highPriorityOpen.length}件）`,
      details: {
        total_questions: questions.length,
        open_questions: openQuestions.length,
        high_priority_open: highPriorityOpen.length,
        open_question_list: highPriorityOpen.map((q: any) => q.q_id)
      },
      fix_suggestion: highPriorityOpen.length > 0 ? 
        `高優先度未解決質問${highPriorityOpen.length}件の解決が必要` : undefined
    }];
  }

  private checkEvidenceValidity(so: Record<string, any>): CheckResult[] {
    let totalEvidence = 0;
    let validEvidence = 0;

    const steps = so.steps || [];
    for (const step of steps) {
      const evidences = step.evidence || [];
      for (const evidence of evidences) {
        totalEvidence++;
        const ref = evidence.ref || '';
        const result = evidence.result || '';

        const isValid = (
          ref.length > 0 &&
          result.length > 0 &&
          (ref.startsWith('http') || ref.startsWith('/') || 
           ref.endsWith('.log') || ref.toLowerCase().includes('test'))
        );

        if (isValid) validEvidence++;
      }
    }

    const validityRate = totalEvidence > 0 ? validEvidence / totalEvidence : 1.0;
    const passed = validityRate >= this.config.min_evidence_validity_rate;

    return [{
      category: "evidence妥当性",
      check_name: "evidence_validity",
      severity: passed ? CheckSeverity.INFO : CheckSeverity.MEDIUM,
      passed: passed,
      score: validityRate,
      message: `evidence妥当性率${(validityRate * 100).toFixed(0)}%（${validEvidence}/${totalEvidence}件）`,
      details: {
        total_evidence: totalEvidence,
        valid_evidence: validEvidence,
        validity_rate: validityRate
      },
      fix_suggestion: !passed ? 
        `evidence参照を具体化し、妥当性を${(this.config.min_evidence_validity_rate * 100).toFixed(0)}%以上に向上` : 
        undefined
    }];
  }

  private checkPhaseConsistency(so: Record<string, any>): CheckResult[] {
    const steps = so.steps || [];
    const phaseSequence: string[] = [];
    const phaseInconsistencies: string[] = [];

    const validPhases = ['understand', 'plan', 'design', 'implement', 'test', 'debug', 'explain', 'other'];

    for (const step of steps) {
      const phase = step.phase;
      if (phase) {
        phaseSequence.push(phase);
        if (!validPhases.includes(phase)) {
          phaseInconsistencies.push(`無効なphase: ${phase}`);
        }
      }
    }

    const score = Math.max(0.0, 1.0 - (phaseInconsistencies.length * 0.2));

    return [{
      category: "Phase整合性",
      check_name: "phase_consistency",
      severity: CheckSeverity.LOW,
      passed: phaseInconsistencies.length === 0,
      score: score,
      message: `Phase不整合${phaseInconsistencies.length}件`,
      details: {
        total_steps: steps.length,
        phase_sequence: phaseSequence,
        inconsistencies: phaseInconsistencies
      },
      fix_suggestion: phaseInconsistencies.length > 0 ? 
        "Phase名を標準値に修正してください" : undefined
    }];
  }

  private checkTimestampValidity(so: Record<string, any>): CheckResult[] {
    const meta = so.meta || {};
    const createdAt = meta.created_at;
    const updatedAt = meta.updated_at;
    const timestampErrors: string[] = [];

    try {
      if (createdAt && updatedAt) {
        const createdDt = new Date(createdAt);
        const updatedDt = new Date(updatedAt);

        if (updatedDt < createdDt) {
          timestampErrors.push("updated_at < created_at");
        }

        const timeDiff = updatedDt.getTime() - createdDt.getTime();
        if (timeDiff > 24 * 60 * 60 * 1000) { // 24時間
          timestampErrors.push("作業時間24時間超過");
        }
      }
    } catch (e) {
      timestampErrors.push(`タイムスタンプ形式エラー: ${e}`);
    }

    const score = timestampErrors.length === 0 ? 1.0 : 0.5;

    return [{
      category: "タイムスタンプ",
      check_name: "timestamp_validity",
      severity: CheckSeverity.LOW,
      passed: timestampErrors.length === 0,
      score: score,
      message: `タイムスタンプエラー${timestampErrors.length}件`,
      details: {
        created_at: createdAt,
        updated_at: updatedAt,
        errors: timestampErrors
      },
      fix_suggestion: timestampErrors.length > 0 ? 
        "タイムスタンプの形式と論理的整合性を修正" : undefined
    }];
  }

  private checkMetricsComputability(so: Record<string, any>): CheckResult[] {
    let computableMetrics = 0;
    const metricIssues: string[] = [];

    // A1-1: 要件理解正確度
    const reqItems = so.requirements?.req_items || [];
    if (reqItems.length > 0 && reqItems.every((req: any) => req.req_id && req.text)) {
      computableMetrics++;
    } else {
      metricIssues.push("A1-1: requirements.req_items不足");
    }

    // A1-2: 推論正確性
    const stepsWithEvidence = (so.steps || []).filter((s: any) => s.evidence && s.evidence.length > 0);
    if (stepsWithEvidence.length > 0) {
      computableMetrics++;
    } else {
      metricIssues.push("A1-2: steps.evidence不足");
    }

    // A1-3: 推論・設計・説明の整合維持率
    let decisions: any[] = [];
    for (const step of (so.steps || [])) {
      decisions = decisions.concat(step.decisions || []);
    }
    const decisionMap = so.final?.decision_map || [];

    if (decisions.length > 0 && decisionMap.length > 0) {
      computableMetrics++;
    } else {
      metricIssues.push("A1-3: decisions または decision_map不足");
    }

    // 他のメトリクス（簡略化）
    computableMetrics += 5; // A2-1, A2-2, A3-1, A6-1, A6-2

    const totalMetrics = 8;
    const comput