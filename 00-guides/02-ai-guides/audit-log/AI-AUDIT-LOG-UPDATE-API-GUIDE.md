# AI監査ログ API更新ガイド - structured_output の技術実装

---
document_type: technical_guide
target_audience:
  - AIエージェント（Devin, Cursor等）
  - バックエンド開発者
  - システムインテグレーター
priority: high
scope: audit_log_api_implementation
version: 1.0
last_updated: 2026-03-10
related_documents:
  - AICQ_AUDIT_LOG_SCHEMA.md
  - AI-WORKLOG-ENFORCEMENT-GUIDE.md
  - AI-AUDIT-LOG-QUICK-DECISION-GUIDE.md
---

## 1. ガイドの目的と対象読者

### 1.1 目的
このガイドは、Devin APIを使用してstructured_outputを技術的に更新する具体的な実装方法を提供します。理論ではなく、**即座に使えるコード例**に重点を置いています。

**解決する技術課題:**
- 🔧 Devin APIでのstructured_output更新の実装方法
- 🔄 部分更新 vs 全体更新の使い分け
- ⚡ パフォーマンス最適化とエラーハンドリング
- 🧪 テスト・検証の実装方法

### 1.2 対象読者

| 読者タイプ | 利用目的 | 必要スキル |
|-----------|----------|-----------|
| **AIエージェント** | 自身のログ更新実装 | API呼び出し基礎 |
| **バックエンド開発者** | 監査ログシステム構築 | Python/JavaScript中級 |
| **システムインテグレーター** | Devin連携システム開発 | REST API設計経験 |

### 1.3 前提条件
- Devin API v1アクセス権限
- API Token取得済み
- AICQ_AUDIT_LOG_SCHEMA.mdの理解

---

## 2. Devin API概要

### 2.1 基本設定

```
Base URL: https://api.devin.ai/v1
認証方式: Bearer Token
Content-Type: application/json
```

### 2.2 主要エンドポイント一覧

| エンドポイント | HTTP Method | 目的 | レスポンス |
|--------------|-------------|------|----------|
| `/sessions` | POST | 新規セッション作成 | Session Object |
| `/sessions/{session_id}` | GET | セッション詳細取得 | Session with Messages |
| `/sessions/{session_id}/structured_output` | GET | structured_output取得 | AICQ Audit Log |
| `/sessions/{session_id}/structured_output` | PUT | structured_output全体更新 | Updated Log |
| `/sessions/{session_id}/structured_output` | PATCH | structured_output部分更新 | Updated Log |
| `/sessions/{session_id}/messages` | GET | Messages履歴取得 | Messages Array |
| `/sessions/{session_id}` | DELETE | セッション終了 | Status |

### 2.3 レスポンス形式

#### 成功レスポンス
```json
{
  "success": true,
  "data": {
    "session_id": "sess_abc123",
    "structured_output": { ... },
    "version": 5,
    "last_updated": "2026-03-10T15:30:00Z"
  }
}
```

#### エラーレスポンス
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Required field 'meta' is missing",
    "details": {
      "missing_fields": ["meta.session_id"]
    }
  }
}
```

---

## 3. 更新パターン3種類

### 3.1 全体更新（PUT方式）

**使用ケース:**
- セッション初期化
- 大規模な構造変更
- データ整合性を完全保証したい場合

**メリット:** シンプル、整合性保証  
**デメリット:** 全データ送信、競合リスク高

### 3.2 部分更新（PATCH方式）

**使用ケース:**
- 新しいstep追加
- deviation記録
- 単一フィールドの更新

**メリット:** 効率的、競合リスク低  
**デメリット:** JSON Patch記法の理解が必要

### 3.3 ハイブリッド方式（推奨）

**戦略:**
1. ローカルキャッシュで structured_output を保持
2. 変更時はローカル更新 + 必要に応じてAPI反映
3. 定期的な全体同期（5分間隔 or Phase遷移時）
4. 緊急変更は即座にPATCH

---

## 4. Python実装例

### 4.1 基本クライアントクラス

```python
import requests
import json
import time
from typing import Dict, List, Optional, Any
from dataclasses import dataclass
import logging

@dataclass
class DevinConfig:
    base_url: str = "https://api.devin.ai/v1"
    api_token: str = ""
    timeout: int = 30
    max_retries: int = 3

class DevinAPIClient:
    def __init__(self, config: DevinConfig):
        self.config = config
        self.session = requests.Session()
        self.session.headers.update({
            'Authorization': f'Bearer {config.api_token}',
            'Content-Type': 'application/json'
        })
        
    def _make_request(self, method: str, endpoint: str, **kwargs) -> Dict:
        """基本的なリクエストメソッド（リトライ機能付き）"""
        url = f"{self.config.base_url}{endpoint}"
        
        for attempt in range(self.config.max_retries):
            try:
                response = self.session.request(
                    method=method, 
                    url=url, 
                    timeout=self.config.timeout,
                    **kwargs
                )
                response.raise_for_status()
                return response.json()
                
            except requests.exceptions.RequestException as e:
                if attempt == self.config.max_retries - 1:
                    raise
                wait_time = 2 ** attempt
                logging.warning(f"API call failed (attempt {attempt+1}), retrying in {wait_time}s")
                time.sleep(wait_time)
    
    def get_structured_output(self, session_id: str) -> Dict:
        """structured_outputを取得"""
        return self._make_request('GET', f'/sessions/{session_id}/structured_output')
    
    def update_structured_output_full(self, session_id: str, structured_output: Dict) -> Dict:
        """structured_output全体を更新（PUT）"""
        return self._make_request(
            'PUT', 
            f'/sessions/{session_id}/structured_output',
            json=structured_output
        )
    
    def update_structured_output_patch(self, session_id: str, patch_operations: List[Dict]) -> Dict:
        """structured_outputを部分更新（PATCH）"""
        return self._make_request(
            'PATCH',
            f'/sessions/{session_id}/structured_output',
            json=patch_operations,
            headers={'Content-Type': 'application/json-patch+json'}
        )
```

### 4.2 StructuredOutput管理クラス

```python
class StructuredOutputManager:
    def __init__(self, session_id: str, client: DevinAPIClient):
        self.session_id = session_id
        self.client = client
        self.local_cache: Optional[Dict] = None
        self.last_sync: Optional[float] = None
        self.sync_interval = 300  # 5分間隔
        self.dirty = False  # ローカル変更フラグ
        
    def get_or_initialize(self) -> Dict:
        """structured_outputを取得または初期化"""
        if self.local_cache is None:
            try:
                response = self.client.get_structured_output(self.session_id)
                self.local_cache = response['data']['structured_output']
            except requests.HTTPError as e:
                if e.response.status_code == 404:
                    # セッションが存在しない場合は空の構造を作成
                    self.local_cache = self._create_empty_structure()
                else:
                    raise
        return self.local_cache
    
    def _create_empty_structure(self) -> Dict:
        """空のstructured_output構造を作成"""
        return {
            "meta": {
                "session_id": self.session_id,
                "created_at": time.strftime("%Y-%m-%dT%H:%M:%SZ"),
                "agent": {"name": "Devin", "version": "1.0"}
            },
            "requirements": {"req_items": [], "assumptions": [], "questions": []},
            "definitions": {"terms": [], "invariants": [], "changes": []},
            "steps": [],
            "guidelines": {"ruleset_id": "", "checks": []},
            "deviations": [],
            "checks": {
                "stability": {"recheck_runs": [], "delta_summary": ""},
                "consistency": {"issues": []},
                "internal_definition_consistency": {"issues": []},
                "explanation_clarity": {"rubric_scores": {}, "notes": ""}
            },
            "final": {"summary": "", "decision_map": [], "known_limits": [], "next_actions": []}
        }
    
    def add_step(self, step: Dict) -> None:
        """新しいstepを追加"""
        so = self.get_or_initialize()
        so['steps'].append(step)
        self.dirty = True
        
        # 即座にPATCH更新
        patch_ops = [{
            "op": "add",
            "path": "/steps/-",
            "value": step
        }]
        self.client.update_structured_output_patch(self.session_id, patch_ops)
    
    def add_deviation(self, deviation: Dict) -> None:
        """deviationを追加"""
        so = self.get_or_initialize()
        so['deviations'].append(deviation)
        self.dirty = True
        
        # 即座にPATCH更新
        patch_ops = [{
            "op": "add", 
            "path": "/deviations/-",
            "value": deviation
        }]
        self.client.update_structured_output_patch(self.session_id, patch_ops)
    
    def update_requirement(self, req_id: str, updates: Dict) -> None:
        """既存の要件を更新"""
        so = self.get_or_initialize()
        
        # 該当要件を検索
        for i, req in enumerate(so['requirements']['req_items']):
            if req.get('req_id') == req_id:
                req.update(updates)
                self.dirty = True
                
                # PATCH更新
                patch_ops = [{
                    "op": "replace",
                    "path": f"/requirements/req_items/{i}",
                    "value": req
                }]
                self.client.update_structured_output_patch(self.session_id, patch_ops)
                break
    
    def mark_deviation_recovered(self, deviation_id: str, recovery_actions: List[str]) -> None:
        """deviationを復帰済みにマーク"""
        so = self.get_or_initialize()
        
        for i, dev in enumerate(so['deviations']):
            if dev.get('deviation_id') == deviation_id:
                dev['recovered'] = True
                dev['recovery_actions'] = recovery_actions
                self.dirty = True
                
                patch_ops = [{
                    "op": "replace",
                    "path": f"/deviations/{i}",
                    "value": dev
                }]
                self.client.update_structured_output_patch(self.session_id, patch_ops)
                break
    
    def sync_full_if_needed(self) -> None:
        """必要に応じて全体同期を実行"""
        current_time = time.time()
        
        if (self.dirty and 
            (self.last_sync is None or 
             current_time - self.last_sync > self.sync_interval)):
            
            self.sync_full()
    
    def sync_full(self) -> None:
        """強制的に全体同期"""
        if self.local_cache is None:
            return
            
        self.client.update_structured_output_full(self.session_id, self.local_cache)
        self.last_sync = time.time()
        self.dirty = False
    
    def validate_local_cache(self) -> List[str]:
        """ローカルキャッシュの基本バリデーション"""
        if self.local_cache is None:
            return ["Local cache is None"]
        
        issues = []
        required_fields = ['meta', 'requirements', 'definitions', 'steps', 
                          'guidelines', 'deviations', 'checks', 'final']
        
        for field in required_fields:
            if field not in self.local_cache:
                issues.append(f"Missing required field: {field}")
        
        # decision_idの重複チェック
        decision_ids = []
        for step in self.local_cache.get('steps', []):
            for decision in step.get('decisions', []):
                did = decision.get('decision_id')
                if did in decision_ids:
                    issues.append(f"Duplicate decision_id: {did}")
                decision_ids.append(did)
        
        return issues
```

### 4.3 高レベルなヘルパー関数

```python
def record_implementation_complete(
    manager: StructuredOutputManager,
    step_id: str,
    goal: str,
    implemented_files: List[str],
    decisions_made: List[Dict]
) -> None:
    """実装完了時の記録"""
    step = {
        "step_id": step_id,
        "phase": "implement",
        "goal": goal,
        "inputs": [],
        "outputs": implemented_files,
        "reasoning_summary": {
            "summary": f"Implementation completed: {goal}",
            "alternatives_considered": [],
            "assumptions_used": [],
            "risks": []
        },
        "decisions": decisions_made,
        "evidence": [{
            "type": "code_ref",
            "ref": file,
            "result": "implemented"
        } for file in implemented_files],
        "self_checks": {
            "consistency_checked": True,
            "guidelines_checked": True,
            "notes": "Implementation completed and reviewed"
        }
    }
    
    manager.add_step(step)

def record_bug_found_and_fixed(
    manager: StructuredOutputManager,
    bug_id: str,
    description: str,
    severity: str,
    fix_description: str
) -> None:
    """バグ発見と修正の一連の記録"""
    # 1. Deviation記録
    deviation = {
        "dev_id": bug_id,
        "type": "logic_error",
        "severity": severity,
        "description": description,
        "detected_at_step": f"IMPL-{len(manager.get_or_initialize()['steps']):03d}",
        "recovered": True,
        "root_cause": "Implementation oversight"
    }
    manager.add_deviation(deviation)
    
    # 2. 修正Step記録
    fix_step = {
        "step_id": f"BUGFIX-{bug_id}",
        "phase": "implement",
        "goal": f"Fix bug: {description}",
        "outputs": ["Fixed implementation"],
        "reasoning_summary": {
            "summary": fix_description,
            "alternatives_considered": [],
            "assumptions_used": [],
            "risks": []
        },
        "decisions": [{
            "decision_id": f"BUGFIX-{bug_id}-D1",
            "decision": f"Apply fix: {fix_description}",
            "rationale": f"Addresses {severity} severity bug",
            "req_links": []
        }],
        "evidence": [{
            "type": "test",
            "ref": f"test_fix_{bug_id}",
            "result": "passed"
        }],
        "self_checks": {
            "consistency_checked": True,
            "guidelines_checked": True,
            "notes": f"Bug {bug_id} fixed and verified"
        }
    }
    manager.add_step(fix_step)

def transition_phase(
    manager: StructuredOutputManager,
    from_phase: str,
    to_phase: str,
    completion_summary: str
) -> None:
    """フェーズ遷移の記録"""
    # Phase完了step
    completion_step = {
        "step_id": f"{from_phase.upper()}-COMPLETE",
        "phase": from_phase,
        "goal": f"Complete {from_phase} phase",
        "outputs": [f"{from_phase} phase deliverables"],
        "reasoning_summary": {
            "summary": completion_summary,
            "alternatives_considered": [],
            "assumptions_used": [],
            "risks": []
        },
        "decisions": [],
        "evidence": [{
            "type": "doc_ref",
            "ref": f"{from_phase}_completion_checklist",
            "result": "all_items_completed"
        }],
        "self_checks": {
            "consistency_checked": True,
            "guidelines_checked": True,
            "notes": f"{from_phase} phase successfully completed"
        }
    }
    manager.add_step(completion_step)
    
    # Meta情報の更新
    so = manager.get_or_initialize()
    so['meta']['current_phase'] = to_phase
    so['meta']['phase_transition_time'] = time.strftime("%Y-%m-%dT%H:%M:%SZ")
    
    # 全体同期（重要なマイルストーン）
    manager.sync_full()
```

---

## 5. JavaScript/TypeScript実装例

### 5.1 基本型定義

```typescript
// types.ts
export interface DevinConfig {
  baseUrl?: string;
  apiToken: string;
  timeout?: number;
  maxRetries?: number;
}

export interface StructuredOutput {
  meta: {
    session_id: string;
    created_at: string;
    agent: {
      name: string;
      version: string;
    };
  };
  requirements: {
    req_items: RequirementItem[];
    assumptions: Assumption[];
    questions: Question[];
  };
  definitions: {
    terms: DefinitionTerm[];
    invariants: string[];
    changes: DefinitionChange[];
  };
  steps: Step[];
  guidelines: {
    ruleset_id: string;
    checks: GuidelineCheck[];
  };
  deviations: Deviation[];
  checks: {
    stability: { recheck_runs: any[]; delta_summary: string; };
    consistency: { issues: any[]; };
    internal_definition_consistency: { issues: any[]; };
    explanation_clarity: { rubric_scores: any; notes: string; };
  };
  final: {
    summary: string;
    decision_map: string[];
    known_limits: string[];
    next_actions: string[];
  };
}

export interface Step {
  step_id: string;
  phase: string;
  goal: string;
  inputs: string[];
  outputs: string[];
  reasoning_summary: {
    summary: string;
    alternatives_considered: string[];
    assumptions_used: string[];
    risks: string[];
  };
  decisions: Decision[];
  evidence: Evidence[];
  self_checks: {
    consistency_checked: boolean;
    guidelines_checked: boolean;
    notes: string;
  };
}

export interface Deviation {
  dev_id: string;
  type: string;
  severity: 'low' | 'medium' | 'high' | 'critical';
  description: string;
  detected_at_step: string;
  recovered: boolean;
  root_cause?: string;
  recovery_actions?: string[];
}

export interface Decision {
  decision_id: string;
  decision: string;
  rationale: string;
  req_links: string[];
}

// 他のインターフェース定義...
```

### 5.2 APIクライアント実装

```typescript
// api-client.ts
export class DevinAPIClient {
  private config: Required<DevinConfig>;
  
  constructor(config: DevinConfig) {
    this.config = {
      baseUrl: 'https://api.devin.ai/v1',
      timeout: 30000,
      maxRetries: 3,
      ...config
    };
  }
  
  private async makeRequest<T>(
    method: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE',
    endpoint: string,
    data?: any,
    headers?: Record<string, string>
  ): Promise<T> {
    const url = `${this.config.baseUrl}${endpoint}`;
    
    for (let attempt = 0; attempt < this.config.maxRetries; attempt++) {
      try {
        const response = await fetch(url, {
          method,
          headers: {
            'Authorization': `Bearer ${this.config.apiToken}`,
            'Content-Type': 'application/json',
            ...headers
          },
          body: data ? JSON.stringify(data) : undefined,
          signal: AbortSignal.timeout(this.config.timeout)
        });
        
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${await response.text()}`);
        }
        
        return await response.json();
      } catch (error) {
        if (attempt === this.config.maxRetries - 1) {
          throw error;
        }
        
        const waitTime = Math.pow(2, attempt) * 1000;
        console.warn(`API call failed (attempt ${attempt + 1}), retrying in ${waitTime}ms`);
        await new Promise(resolve => setTimeout(resolve, waitTime));
      }
    }
    
    throw new Error('Max retries reached');
  }
  
  async getStructuredOutput(sessionId: string): Promise<StructuredOutput> {
    const response = await this.makeRequest<{data: {structured_output: StructuredOutput}}>(
      'GET',
      `/sessions/${sessionId}/structured_output`
    );
    return response.data.structured_output;
  }
  
  async updateStructuredOutputFull(
    sessionId: string,
    structuredOutput: StructuredOutput
  ): Promise<StructuredOutput> {
    const response = await this.makeRequest<{data: {structured_output: StructuredOutput}}>(
      'PUT',
      `/sessions/${sessionId}/structured_output`,
      structuredOutput
    );
    return response.data.structured_output;
  }
  
  async updateStructuredOutputPatch(
    sessionId: string,
    patchOperations: Array<{op: string; path: string; value?: any}>
  ): Promise<StructuredOutput> {
    const response = await this.makeRequest<{data: {structured_output: StructuredOutput}}>(
      'PATCH',
      `/sessions/${sessionId}/structured_output`,
      patchOperations,
      {'Content-Type': 'application/json-patch+json'}
    );
    return response.data.structured_output;
  }
}
```

### 5.3 Manager実装

```typescript
// structured-output-manager.ts
export class StructuredOutputManager {
  private localCache: StructuredOutput | null = null;
  private lastSync: number | null = null;
  private readonly syncInterval = 5 * 60 * 1000; // 5分
  private dirty = false;
  
  constructor(
    private sessionId: string,
    private client: DevinAPIClient
  ) {}
  
  async getOrInitialize(): Promise<StructuredOutput> {
    if (this.localCache === null) {
      try {
        this.localCache = await this.client.getStructuredOutput(this.sessionId);
      } catch (error) {
        console.warn('Failed to fetch existing structured_output, creating new one');
        this.localCache = this.createEmptyStructure();
      }
    }
    return this.localCache;
  }
  
  private createEmptyStructure(): StructuredOutput {
    return {
      meta: {
        session_id: this.sessionId,
        created_at: new Date().toISOString(),
        agent: {
          name: 'Devin',
          version: '1.0'
        }
      },
      requirements: { req_items: [], assumptions: [], questions: [] },
      definitions: { terms: [], invariants: [], changes: [] },
      steps: [],
      guidelines: { ruleset_id: '', checks: [] },
      deviations: [],
      checks: {
        stability: { recheck_runs: [], delta_summary: '' },
        consistency: { issues: [] },
        internal_definition_consistency: { issues: [] },
        explanation_clarity: { rubric_scores: {}, notes: '' }
      },
      final: { summary: '', decision_map: [], known_limits: [], next_actions: [] }
    };
  }
  
  async addStep(step: Step): Promise<void> {
    const so = await this.getOrInitialize();
    so.steps.push(step);
    this.dirty = true;
    
    // 即座にPATCH更新
    await this.client.updateStructuredOutputPatch(this.sessionId, [
      { op: 'add', path: '/steps/-', value: step }
    ]);
  }
  
  async addDeviation(deviation: Deviation): Promise<void> {
    const so = await this.getOrInitialize();
    so.deviations.push(deviation);
    this.dirty = true;
    
    await this.client.updateStructuredOutputPatch(this.sessionId, [
      { op: 'add', path: '/deviations/-', value: deviation }
    ]);
  }
  
  async updateDecision(stepId: string, decision: Decision): Promise<void> {
    const so = await this.getOrInitialize();
    const stepIndex = so.steps.findIndex(s => s.step_id === stepId);
    
    if (stepIndex === -1) {
      throw new Error(`Step not found: ${stepId}`);
    }
    
    so.steps[stepIndex].decisions.push(decision);
    this.dirty = true;
    
    await this.client.updateStructuredOutputPatch(this.sessionId, [
      { op: 'add', path: `/steps/${stepIndex}/decisions/-`, value: decision }
    ]);
  }
  
  async syncFullIfNeeded(): Promise<void> {
    const now = Date.now();
    
    if (this.dirty && 
        (this.lastSync === null || now - this.lastSync > this.syncInterval)) {
      await this.syncFull();
    }
  }
  
  async syncFull(): Promise<void> {
    if (this.localCache === null) return;
    
    await this.client.updateStructuredOutputFull(this.sessionId, this.localCache);
    this.lastSync = Date.now();
    this.dirty = false;
  }
  
  validateLocalCache(): string[] {
    if (this.localCache === null) {
      return ['Local cache is null'];
    }
    
    const issues: string[] = [];
    const requiredFields = ['meta', 'requirements', 'definitions', 'steps', 
                          'guidelines', 'deviations', 'checks', 'final'];
    
    requiredFields.forEach(field => {
      if (!(field in this.localCache!)) {
        issues.push(`Missing required field: ${field}`);
      }
    });
    
    // decision_idの重複チェック
    const decisionIds = new Set<string>();
    this.localCache.steps.forEach(step => {
      step.decisions.forEach(decision => {
        if (decisionIds.has(decision.decision_id)) {
          issues.push(`Duplicate decision_id: ${decision.decision_id}`);
        }
        decisionIds.add(decision.decision_id);
      });
    });
    
    return issues;
  }
}
```

### 5.4 高レベルヘルパー関数

```typescript
// helpers.ts
export async function recordImplementationComplete(
  manager: StructuredOutputManager,
  stepId: string,
  goal: string,
  implementedFiles: string[],
  decisionsMade: Decision[]
): Promise<void> {
  const step: Step = {
    step_id: stepId,
    phase: 'implement',
    goal,
    inputs: [],
    outputs: implementedFiles,
    reasoning_summary: {
      summary: `Implementation completed: ${goal}`,
      alternatives_considered: [],
      assumptions_used: [],
      risks: []
    },
    decisions: decisionsMade,
    evidence: implementedFiles.map(file => ({
      type: 'code_ref',
      ref: file,
      result: 'implemented'
    })),
    self_checks: {
      consistency_checked: true,
      guidelines_checked: true,
      notes: 'Implementation completed and reviewed'
    }
  };
  
  await manager.addStep(step);
}

export async function recordBugFoundAndFixed(
  manager: StructuredOutputManager,
  bugId: string,
  description: string,
  severity: 'low' | 'medium' | 'high' | 'critical',
  fixDescription: string
): Promise<void> {
  // Deviation記録
  const deviation: Deviation = {
    dev_id: bugId,
    type: 'logic_error',
    severity,
    description,
    detected_at_step: `IMPL-${(await manager.getOrInitialize()).steps.length.toString().padStart(3, '0')}`,
    recovered: true,
    root_cause: 'Implementation oversight',
    recovery_actions: [fixDescription]
  };
  await manager.addDeviation(deviation);
  
  // 修正Step記録
  const fixStep: Step = {
    step_id: `BUGFIX-${bugId}`,
    phase: 'implement',
    goal: `Fix bug: ${description}`,
    inputs: [],
    outputs: ['Fixed implementation'],
    reasoning_summary: {
      summary: fixDescription,
      alternatives_considered: [],
      assumptions_used: [],
      risks: []
    },
    decisions: [{
      decision_id: `BUGFIX-${bugId}-D1`,
      decision: `Apply fix: ${fixDescription}`,
      rationale: `Addresses ${severity} severity bug`,
      req_links: []
    }],
    evidence: [{
      type: 'test',
      ref: `test_fix_${bugId}`,
      result: 'passed'
    }],
    self_checks: {
      consistency_checked: true,
      guidelines_checked: true,
      notes: `Bug ${bugId} fixed and verified`
    }
  };
  await manager.addStep(fixStep);
}
```

---

## 6. 更新シナリオ別実装

### 6.1 新しいstepの追加

```python
# Python版
def add_design_step(manager: StructuredOutputManager, design_decision: str) -> str:
    """設計判断stepを追加"""
    step_count = len(manager.get_or_initialize()['steps']) + 1
    step_id = f"DESIGN-{step_count:03d}"
    
    step = {
        "step_id": step_id,
        "phase": "design",
        "goal": f"Make design decision: {design_decision}",
        "inputs": ["Requirements analysis", "Architecture constraints"],
        "outputs": ["Design decision document"],
        "reasoning_summary": {
            "summary": f"Decided on: {design_decision}",
            "alternatives_considered": ["Alternative A", "Alternative B"],
            "assumptions_used": ["Standard architecture patterns"],
            "risks": ["Integration complexity", "Performance impact"]
        },
        "decisions": [{
            "decision_id": f"{step_id}-D1",
            "decision": design_decision,
            "rationale": "Best fit for requirements and constraints",
            "req_links": ["REQ-001", "REQ-002"]
        }],
        "evidence": [{
            "type": "doc_ref",
            "ref": "architecture_patterns_comparison.md",
            "result": "analysis_completed"
        }],
        "self_checks": {
            "consistency_checked": True,
            "guidelines_checked": True,
            "notes": "Design decision documented and reviewed"
        }
    }
    
    manager.add_step(step)
    return step_id
```

```typescript
// TypeScript版
async function addDesignStep(
  manager: StructuredOutputManager, 
  designDecision: string
): Promise<string> {
  const so = await manager.getOrInitialize();
  const stepCount = so.steps.length + 1;
  const stepId = `DESIGN-${stepCount.toString().padStart(3, '0')}`;
  
  const step: Step = {
    step_id: stepId,
    phase: 'design',
    goal: `Make design decision: ${designDecision}`,
    inputs: ['Requirements analysis', 'Architecture constraints'],
    outputs: ['Design decision document'],
    reasoning_summary: {
      summary: `Decided on: ${designDecision}`,
      alternatives_considered: ['Alternative A', 'Alternative B'],
      assumptions_used: ['Standard architecture patterns'],
      risks: ['Integration complexity', 'Performance impact']
    },
    decisions: [{
      decision_id: `${stepId}-D1`,
      decision: designDecision,
      rationale: 'Best fit for requirements and constraints',
      req_links: ['REQ-001', 'REQ-002']
    }],
    evidence: [{
      type: 'doc_ref',
      ref: 'architecture_patterns_comparison.md',
      result: 'analysis_completed'
    }],
    self_checks: {
      consistency_checked: true,
      guidelines_checked: true,
      notes: 'Design decision documented and reviewed'
    }
  };
  
  await manager.addStep(step);
  return stepId;
}
```

### 6.2 deviationの記録

```python
def record_performance_deviation(
    manager: StructuredOutputManager,
    expected_time: float,
    actual_time: float,
    operation: str
) -> str:
    """パフォーマンス逸脱の記録"""
    deviation_id = f"PERF-{int(time.time())}"
    
    severity = "low"
    if actual_time > expected_time * 3:
        severity = "critical"
    elif actual_time > expected_time * 2:
        severity = "high"
    elif actual_time > expected_time * 1.5:
        severity = "medium"
    
    deviation = {
        "dev_id": deviation_id,
        "type": "performance",
        "severity": severity,
        "description": f"{operation} took {actual_time:.2f}s (expected: {expected_time:.2f}s)",
        "detected_at_step": f"TEST-{len(manager.get_or_initialize()['steps']):03d}",
        "recovered": False,
        "root_cause": "Unoptimized algorithm or data structure"
    }
    
    manager.add_deviation(deviation)
    return deviation_id

def record_performance_fix(
    manager: StructuredOutputManager, 
    deviation_id: str, 
    fix_description: str,
    new_time: float
) -> None:
    """パフォーマンス修正の記録"""
    # Deviation復旧マーク
    manager.mark_deviation_recovered(deviation_id, [fix_description])
    
    # 修正stepを追加
    fix_step = {
        "step_id": f"PERFFIX-{deviation_id}",
        "phase": "implement",
        "goal": f"Fix performance issue: {deviation_id}",
        "inputs": ["Performance analysis", "Profiling results"],
        "outputs": ["Optimized code", "Performance test results"],
        "reasoning_summary": {
            "summary": fix_description,
            "alternatives_considered": ["Algorithm optimization", "Caching", "Data structure change"],
            "assumptions_used": ["Performance requirements"],
            "risks": ["Code complexity increase"]
        },
        "decisions": [{
            "decision_id": f"PERFFIX-{deviation_id}-D1",
            "decision": f"Apply optimization: {fix_description}",
            "rationale": "Addresses performance regression while maintaining functionality",
            "req_links": ["NFR-PERF-001"]
        }],
        "evidence": [{
            "type": "test",
            "ref": f"performance_test_{deviation_id}",
            "result": f"execution_time: {new_time:.2f}s"
        }],
        "self_checks": {
            "consistency_checked": True,
            "guidelines_checked": True,
            "notes": f"Performance issue {deviation_id} resolved"
        }
    }
    
    manager.add_step(fix_step)
```

### 6.3 requirements動的更新

```python
def handle_requirements_change(
    manager: StructuredOutputManager,
    change_request: str,
    new_requirements: List[Dict],
    impact_analysis: str
) -> None:
    """要件変更の処理と記録"""
    so = manager.get_or_initialize()
    
    # 新要件を追加
    for req in new_requirements:
        so['requirements']['req_items'].append(req)
    
    # 変更履歴stepを追加
    change_step = {
        "step_id": f"REQCHANGE-{int(time.time())}",
        "phase": "understand",
        "goal": "Handle requirements change",
        "inputs": [change_request],
        "outputs": ["Updated requirements specification"],
        "reasoning_summary": {
            "summary": f"Requirements updated: {len(new_requirements)} new items added",
            "alternatives_considered": ["Reject changes", "Defer to next release", "Accept with modifications"],
            "assumptions_used": ["Client approval", "Timeline flexibility"],
            "risks": ["Schedule impact", "Scope creep"]
        },
        "decisions": [{
            "decision_id": f"REQCHANGE-D1",
            "decision": "Accept requirements changes with impact analysis",
            "rationale": impact_analysis,
            "req_links": [req['req_id'] for req in new_requirements]
        }],
        "evidence": [{
            "type": "doc_ref",
            "ref": "requirements_change_request.md",
            "result": "analyzed_and_accepted"
        }],
        "self_checks": {
            "consistency_checked": True,
            "guidelines_checked": True,
            "notes": "Requirements change properly analyzed and documented"
        }
    }
    
    manager.add_step(change_step)
    
    # 重要な変更のため即座に全体同期
    manager.sync_full()
```

### 6.4 フェーズ遷移処理

```python
def execute_phase_transition(
    manager: StructuredOutputManager,
    current_phase: str,
    next_phase: str,
    readiness_checklist: List[str],
    blocking_issues: List[str] = None
) -> bool:
    """フェーズ遷移の実行"""
    
    # ブロッキング問題がある場合は遷移を拒否
    if blocking_issues:
        deviation = {
            "dev_id": f"PHASE-BLOCKED-{int(time.time())}",
            "type": "process_skip",
            "severity": "high",
            "description": f"Phase transition from {current_phase} to {next_phase} blocked",
            "detected_at_step": f"{current_phase.upper()}-TRANSITION",
            "recovered": False,
            "root_cause": f"Blocking issues: {', '.join(blocking_issues)}"
        }
        manager.add_deviation(deviation)
        return False
    
    # Phase完了step
    completion_step = {
        "step_id": f"{current_phase.upper()}-COMPLETE",
        "phase": current_phase,
        "goal": f"Complete {current_phase} phase",
        "inputs": readiness_checklist,
        "outputs": [f"{current_phase} phase deliverables", "Phase completion report"],
        "reasoning_summary": {
            "summary": f"{current_phase} phase completed successfully",
            "alternatives_considered": [],
            "assumptions_used": [f"{current_phase} deliverables meet quality standards"],
            "risks": []
        },
        "decisions": [{
            "decision_id": f"{current_phase.upper()}-COMPLETE-D1",
            "decision": f"Approve {current_phase} phase completion",
            "rationale": "All deliverables completed and quality gates passed",
            "req_links": []
        }],
        "evidence": [{
            "type": "doc_ref", 
            "ref": f"{current_phase}_completion_checklist",
            "result": "all_items_passed"
        }],
        "self_checks": {
            "consistency_checked": True,
            "guidelines_checked": True,
            "notes": f"{current_phase} phase ready for transition"
        }
    }
    manager.add_step(completion_step)
    
    # Phase開始step
    start_step = {
        "step_id": f"{next_phase.upper()}-START",
        "phase": next_phase,
        "goal": f"Start {next_phase} phase",
        "inputs": [f"{current_phase} deliverables"],
        "outputs": [f"{next_phase} kickoff"],
        "reasoning_summary": {
            "summary": f"Starting {next_phase} phase with all prerequisites met",
            "alternatives_considered": [],
            "assumptions_used": [f"{current_phase} deliverables are complete and correct"],
            "risks": [f"Dependencies from {current_phase} may have issues"]
        },
        "decisions": [{
            "decision_id": f"{next_phase.upper()}-START-D1",
            "decision": f"Proceed with {next_phase} phase",
            "rationale": f"Ready to start based on {current_phase} completion",
            "req_links": []
        }],
        "evidence": [{
            "type": "doc_ref",
            "ref": f"{next_phase}_readiness_checklist",
            "result": "prerequisites_met"
        }],
        "self_checks": {
            "consistency_checked": True,
            "guidelines_checked": True,
            "notes": f"{next_phase} phase started successfully"
        }
    }
    manager.add_step(start_step)
    
    # Meta情報更新
    so = manager.get_or_initialize()
    so['meta']['current_phase'] = next_phase
    so['meta']['previous_phase'] = current_phase
    so['meta']['phase_transition_time'] = time.strftime("%Y-%m-%dT%H:%M:%SZ")
    
    # 重要なマイルストーンなので全体同期
    manager.sync_full()
    
    return True
```

---

## 7. エラーハンドリングとリトライ戦略

### 7.1 包括的エラーハンドリング

```python
from enum import Enum
from typing import Callable, TypeVar, Any

class APIErrorType(Enum):
    NETWORK_ERROR = "network_error"
    AUTHENTICATION_ERROR = "authentication_error"  
    VALIDATION_ERROR = "validation_error"
    RATE_LIMIT_ERROR = "rate_limit_error"
    SERVER_ERROR = "server_error"
    CONFLICT_ERROR = "conflict_error"

class StructuredOutputAPIError(Exception):
    def __init__(self, error_type: APIErrorType, message: str, details: Dict = None):
        self.error_type = error_type
        self.message = message
        self.details = details or {}
        super().__init__(f"{error_type.value}: {message}")

def classify_api_error(response: requests.Response) -> APIErrorType:
    """HTTPレスポンスからエラー種別を判定"""
    status_code = response.status_code
    
    if status_code == 401:
        return APIErrorType.AUTHENTICATION_ERROR
    elif status_code == 400:
        return APIErrorType.VALIDATION_ERROR
    elif status_code == 409:
        return APIErrorType.CONFLICT_ERROR
    elif status_code == 429:
        return APIErrorType.RATE_LIMIT_ERROR
    elif 500 <= status_code < 600:
        return APIErrorType.SERVER_ERROR
    else:
        return APIErrorType.NETWORK_ERROR

T = TypeVar('T')

def with_retry_strategy(
    func: Callable[[], T],
    max_retries: int = 3,
    backoff_factor: float = 2.0,
    retryable_errors: List[APIErrorType] = None
) -> T:
    """リトライ戦略付きでAPI呼び出しを実行"""
    
    if retryable_errors is None:
        retryable_errors = [
            APIErrorType.NETWORK_ERROR,
            APIErrorType.SERVER_ERROR,
            APIErrorType.RATE_LIMIT_ERROR
        ]
    
    last_exception = None
    
    for attempt in range(max_retries):
        try:
            return func()
        except requests.RequestException as e:
            if hasattr(e, 'response') and e.response is not None:
                error_type = classify_api_error(e.response)
                
                # 特定のエラーはリトライしない
                if error_type not in retryable_errors:
                    if error_type == APIErrorType.VALIDATION_ERROR:
                        try:
                            error_details = e.response.json()
                        except:
                            error_details = {}
                        raise StructuredOutputAPIError(
                            error_type, 
                            f"Validation failed: {e.response.text}",
                            error_details
                        )
                    else:
                        raise StructuredOutputAPIError(
                            error_type,
                            str(e),
                            {"status_code": e.response.status_code}
                        )
            
            last_exception = e
            
            if attempt == max_retries - 1:
                break
                
            wait_time = backoff_factor ** attempt
            
            # レート制限の場合は少し長めに待機
            if hasattr(e, 'response') and e.response is not None:
                if e.response.status_code == 429:
                    retry_after = e.response.headers.get('Retry-After')
                    if retry_after:
                        wait_time = max(wait_time, int(retry_after))
            
            logging.warning(f"API call failed (attempt {attempt+1}/{max_retries}). Retrying in {wait_time}s")
            time.sleep(wait_time)
    
    raise StructuredOutputAPIError(
        APIErrorType.NETWORK_ERROR,
        f"Max retries ({max_retries}) exceeded",
        {"last_exception": str(last_exception)}
    )

# 使用例
def safe_update_structured_output(manager: StructuredOutputManager, updates: Dict) -> bool:
    """安全なstructured_output更新"""
    try:
        def update_operation():
            so = manager.get_or_initialize()
            so.update(updates)
            manager.sync_full()
            return True
            
        return with_retry_strategy(update_operation)
        
    except StructuredOutputAPIError as e:
        if e.error_type == APIErrorType.VALIDATION_ERROR:
            logging.error(f"Validation error: {e.details}")
            # バリデーションエラーの場合は修正を試みる
            return attempt_validation_fix(manager, e.details)
        elif e.error_type == APIErrorType.CONFLICT_ERROR:
            logging.warning("Conflict detected, attempting to resolve")
            return resolve_conflict_and_retry(manager, updates)
        else:
            logging.error(f"Unrecoverable error: {e}")
            return False
    except Exception as e:
        logging.error(f"Unexpected error: {e}")
        return False

def attempt_validation_fix(manager: StructuredOutputManager, error_details: Dict) -> bool:
    """バリデーションエラーの自動修正を試行"""
    so = manager.get_or_initialize()
    
    # 欠落フィールドの補完
    if 'missing_fields' in error_details:
        for field_path in error_details['missing_fields']:
            parts = field_path.split('.')
            current = so
            
            for part in parts[:-1]:
                if part not in current:
                    current[part] = {}
                current = current[part]
            
            # デフォルト値を設定
            field_name = parts[-1]
            if field_name == 'session_id':
                current[field_name] = manager.session_id
            elif field_name in ['req_items', 'assumptions', 'questions', 'steps', 'deviations']:
                current[field_name] = []
            elif field_name in ['terms', 'invariants', 'changes']:
                current[field_name] = []
            else:
                current[field_name] = ""
    
    # 再度更新を試行
    try:
        manager.sync_full()
        return True
    except:
        return False

def resolve_conflict_and_retry(manager: StructuredOutputManager, updates: Dict) -> bool:
    """競合解決とリトライ"""
    try:
        # 最新版を取得
        latest = manager.client.get_structured_output(manager.session_id)
        
        # 簡単なマージ戦略（配列は追加、その他は上書き）
        merged = latest.copy()
        
        for key, value in updates.items():
            if isinstance(value, list) and key in merged and isinstance(merged[key], list):
                # 配列は既存に追加
                merged[key].extend(value)
            else:
                # その他は上書き
                merged[key] = value
        
        # マージ結果で更新
        manager.local_cache = merged
        manager.sync_full()
        return True
    except:
        return False
```

### 7.2 TypeScript版エラーハンドリング

```typescript
// error-handling.ts
export enum APIErrorType {
  NetworkError = 'network_error',
  AuthenticationError = 'authentication_error',
  ValidationError = 'validation_error',
  RateLimitError = 'rate_limit_error',
  ServerError = 'server_error',
  ConflictError = 'conflict_error'
}

export class StructuredOutputAPIError extends Error {
  constructor(
    public errorType: APIErrorType,
    message: string,
    public details: Record<string, any> = {}
  ) {
    super(`${errorType}: ${message}`);
    this.name = 'StructuredOutputAPIError';
  }
}

function classifyAPIError(status: number): APIErrorType {
  if (status === 401) return APIErrorType.AuthenticationError;
  if (status === 400) return APIErrorType.ValidationError;
  if (status === 409) return APIErrorType.ConflictError;
  if (status === 429) return APIErrorType.RateLimitError;
  if (status >= 500) return APIErrorType.ServerError;
  return APIErrorType.NetworkError;
}

export async function withRetryStrategy<T>(
  operation: () => Promise<T>,
  maxRetries: number = 3,
  backoffFactor: number = 2.0,
  retryableErrors: APIErrorType[] = [
    APIErrorType.NetworkError,
    APIErrorType.ServerError,
    APIErrorType.RateLimitError
  ]
): Promise<T> {
  let lastError: Error | null = null;
  
  for (let attempt = 0; attempt < maxRetries; attempt++) {
    try {
      return await operation();
    } catch (error) {
      lastError = error as Error;
      
      if (error instanceof StructuredOutputAPIError) {
        if (!retryableErrors.includes(error.errorType)) {
          throw error; // リトライしない
        }
      }
      
      if (attempt === maxRetries - 1) {
        break;
      }
      
      const waitTime = Math.pow(backoffFactor, attempt) * 1000;
      console.warn(`Operation failed (attempt ${attempt + 1}/${maxRetries}). Retrying in ${waitTime}ms`);
      await new Promise(resolve => setTimeout(resolve, waitTime));
    }
  }
  
  throw new StructuredOutputAPIError(
    APIErrorType.NetworkError,
    `Max retries (${maxRetries}) exceeded`,
    { lastError: lastError?.message }
  );
}

export async function safeUpdateStructuredOutput(
  manager: StructuredOutputManager,
  updates: Partial<StructuredOutput>
): Promise<boolean> {
  try {
    await withRetryStrategy(async () => {
      const so = await manager.getOrInitialize();
      Object.assign(so, updates);
      await manager.syncFull();
    });
    return true;
  } catch (error) {
    if (error instanceof StructuredOutputAPIError) {
      console.error(`API Error: ${error.errorType} - ${error.message}`, error.details);
      
      if (error.errorType === APIErrorType.ValidationError) {
        return await attemptValidationFix(manager, error.details);
      } else if (error.errorType === APIErrorType.ConflictError) {
        return await resolveConflictAndRetry(manager, updates);
      }
    }
    return false;
  }
}

async function attemptValidationFix(
  manager: StructuredOutputManager, 
  errorDetails: Record<string, any>
): Promise<boolean> {
  const so = await manager.getOrInitialize();
  
  // 基本的な欠落フィールドの補完
  const requiredFields = ['meta', 'requirements', 'definitions', 'steps', 
                         'guidelines', 'deviations', 'checks', 'final'];
  
  requiredFields.forEach(field => {
    if (!(field in so)) {
      switch (field) {
        case 'meta':
          (so as any)[field] = {
            session_id: manager['sessionId'],
            created_at: new Date().toISOString(),
            agent: { name: 'Devin', version: '1.0' }
          };
          break;
        case 'requirements':
          (so as any)[field] = { req_items: [], assumptions: [], questions: [] };
          break;
        case 'definitions':
          (so as any)[field] = { terms: [], invariants: [], changes: [] };
          break;
        default:
          (so as any)[field] = Array.isArray((so as any)[field]) ? [] : {};
      }
    }
  });
  
  try {
    await manager.syncFull();
    return true;
  } catch {
    return false;
  }
}
```

---

## 8. パフォーマンス最適化

### 8.1 バッチ更新システム

```python
from threading import Lock
from typing import List, Tuple, Any
import threading
import time

class BatchUpdateManager:
    def __init__(self, manager: StructuredOutputManager, batch_size: int = 10, batch_timeout: float = 30.0):
        self.manager = manager
        self.batch_size = batch_size
        self.batch_timeout = batch_timeout
        self.pending_operations: List[Tuple[str, Any]] = []
        self.lock = Lock()
        self.last_batch_time = time.time()
        
        # バックグラウンドで定期的にフラッシュ
        self.flush_timer = None
        self._schedule_flush()
    
    def queue_step(self, step: Dict) -> None:
        """stepの追加をキューに登録"""
        with self.lock:
            self.pending_operations.append(('add_step', step))
            self._check_flush_conditions()
    
    def queue_deviation(self, deviation: Dict) -> None:
        """deviationの追加をキューに登録"""
        with self.lock:
            self.pending_operations.append(('add_deviation', deviation))
            self._check_flush_conditions()
    
    def queue_decision_update(self, step_id: str, decision: Dict) -> None:
        """decisionの更新をキューに登録"""
        with self.lock:
            self.pending_operations.append(('update_decision', (step_id, decision)))
            self._check_flush_conditions()
    
    def _check_flush_conditions(self) -> None:
        """フラッシュ条件をチェック（ロック内で呼び出し）"""
        if (len(self.pending_operations) >= self.batch_size or 
            time.time() - self.last_batch_time > self.batch_timeout):
            self._flush_internal()
    
    def _flush_internal(self) -> None:
        """内部フラッシュ処理（ロック内で呼び出し）"""
        if not self.pending_operations:
            return
        
        so = self.manager.get_or_initialize()
        
        for operation_type, data in self.pending_operations:
            if operation_type == 'add_step':
                so['steps'].append(data)
            elif operation_type == 'add_deviation':
                so['deviations'].append(data)
            elif operation_type == 'update_decision':
                step_id, decision = data
                for step in so['steps']:
                    if step['step_id'] == step_id:
                        step['decisions'].append(decision)
                        break
        
        # バッチをまとめて送信
        self.manager.sync_full()
        self.pending_operations.clear()
        self.last_batch_time = time.time()
        
        # 次のタイマーをスケジュール
        self._schedule_flush()
    
    def flush(self) -> None:
        """手動フラッシュ"""
        with self.lock:
            self._flush_internal()
    
    def _schedule_flush(self) -> None:
        """次の定期フラッシュをスケジュール"""
        if self.flush_timer:
            self.flush_timer.cancel()
        
        self.flush_timer = threading.Timer(self.batch_timeout, self._periodic_flush)
        self.flush_timer.daemon = True
        self.flush_timer.start()
    
    def _periodic_flush(self) -> None:
        """定期的なフラッシュ処理"""
        with self.lock:
            if self.pending_operations:
                self._flush_internal()
    
    def stop(self) -> None:
        """バッチマネージャーを停止"""
        if self.flush_timer:
            self.flush_timer.cancel()
        self.flush()

# 使用例
def optimized_development_session(session_id: str, client: DevinAPIClient):
    """最適化された開発セッション"""
    manager = StructuredOutputManager(session_id, client)
    batch_manager = BatchUpdateManager(manager, batch_size=5, batch_timeout=15.0)
    
    try:
        # 複数のstepを効率的に追加
        for i in range(10):
            step = {
                "step_id": f"IMPL-{i:03d}",
                "phase": "implement",
                "goal": f"Implement feature {i}",
                "outputs": [f"feature_{i}.py"]
            }
            batch_manager.queue_step(step)  # キューに追加（即座には送信されない）
        
        # 手動でフラッシュしたい場合
        batch_manager.flush()
        
    finally:
        batch_manager.stop()  # 残りのバッチをフラッシュして停止
```

### 8.2 差分更新システム

```python
import jsonpatch
from typing import Dict, Any

class DiffUpdateManager:
    def __init__(self, manager: StructuredOutputManager):
        self.manager = manager
        self.baseline_version: Dict = None
        self.current_version: Dict = None
    
    def create_baseline(self) -> None:
        """ベースライン版を作成"""
        self.baseline_version = self.manager.get_or_initialize().copy()
        self.current_version = self.baseline_version.copy()
    
    def update_local(self, updates: Dict) -> None:
        """ローカル版を更新（APIには送信しない）"""
        if self.current_version is None:
            self.create_baseline()
        
        # Deep merge
        self._deep_merge(self.current_version, updates)
    
    def _deep_merge(self, target: Dict, source: Dict) -> None:
        """辞書の深いマージ"""
        for key, value in source.items():
            if key in target and isinstance(target[key], dict) and isinstance(value, dict):
                self._deep_merge(target[key], value)
            elif key in target and isinstance(target[key], list) and isinstance(value, list):
                target[key].extend(value)
            else:
                target[key] = value
    
    def compute_and_apply_diff(self) -> bool:
        """差分を計算してAPIに適用"""
        if self.baseline_version is None or self.current_version is None:
            return False
        
        # JSON Patchを生成
        patch = jsonpatch.make_patch(self.baseline_version, self.current_version)
        patch_operations = list(patch)
        
        if not patch_operations:
            return True  # 変更なし
        
        # 差分サイズが大きい場合は全体更新に切り替え
        if len(patch_operations) > 20:  # 閾値は調整可能
            self.manager.local_cache = self.current_version
            self.manager