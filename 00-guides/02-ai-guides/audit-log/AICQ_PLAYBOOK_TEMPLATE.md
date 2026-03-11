# AICQ Playbook テンプレート

---
document_type: playbook_template
target_audience: 
  - Devin使用者
  - AI作業依頼者
  - プロジェクトマネージャー
  - 品質管理担当者
priority: critical
scope: aicq_structured_output_enforcement
version: 1.0
last_updated: 2026-03-10
related_documents:
  - AI-WORKLOG-ENFORCEMENT-GUIDE.md
  - AICQ_AUDIT_LOG_SCHEMA.md
  - AICQ_DATA_MODEL.md
  - AICQ_METRICS_SPEC.md
---

## 📋 概要

### 目的

本テンプレートは、DevinをはじめとするAIエージェントに**監査ログ（structured_output）の記録を強制**するための標準Playbookです。V3.0監査ログ方式に準拠し、AICQメトリクス算出に必要な構造化データを確実に取得します。

### 対象読者

- **Devin使用者**: セッション開始時にPlaybookを適用する担当者
- **AI作業依頼者**: AIに作業を依頼するPM・開発者
- **品質管理担当者**: 監査ログの品質を管理する責任者

### 効果

✅ **監査ログの標準化**: 全セッションで一貫したログ形式  
✅ **取り忘れ防止**: Playbook適用により確実なログ取得  
✅ **メトリクス算出保証**: AICQメトリクス算出に必要なデータを完備  
✅ **品質向上**: 構造化されたログによる継続的改善

---

## 🎯 Playbookとは

### 定義

**Playbook**とは、AIエージェントに対して**毎回同じ行動を取らせるための標準指示文**です。特に監査ログの記録は、人間が都度指示すると抜け漏れが発生するため、定型化された指示（Playbook）として固定化します。

### V3.0監査ログ方式での役割

```
従来の問題点:
❌ 人手での指示 → 記録忘れ・形式ばらつき
❌ 事後整形 → データの信頼性低下
❌ 不完全なログ → メトリクス算出不可

Playbook導入後:
✅ 自動化された記録 → 取り忘れ防止
✅ 構造化された形式 → 一貫性保証
✅ リアルタイム更新 → データ信頼性確保
```

### 適用タイミング

- **必須**: セッション開始直後（3段階STOP-GATE完了後）
- **推奨**: プロジェクト固有ルールの追加時
- **任意**: トラブル発生時の一時的な追加指示

---

## 🚀 使用方法

### Step 1: 事前準備

1. **3段階STOP-GATE完了**を確認
   - GATE 1: エクスポート保存先確定
   - GATE 2: structured_output初期化準備
   - GATE 3: 回収可能性確立

2. **必要な情報を収集**
   - session_id
   - project_id
   - 対象PBI/チケット情報
   - 適用すべきガイドライン（ruleset_id）

### Step 2: Playbook適用

**Devinに以下のPlaybook本文をそのままコピー&ペーストしてください:**

---

## 📜 Playbook本文（コピペ用）

```text
【監査ログ（Structured Output）必須ポリシー】
あなた（Devin）は本セッションで、監査用のstructured_output（JSON）を必ず維持・更新すること。
内部の思考（CoT）の全文は出力しなくてよいが、第三者監査に耐える「推論の要点ログ」を必ず残すこと。

(1) セッション開始直後にstructured_outputを初期化し、meta / requirements / definitions を埋めること。
    - meta: session_id, project_id, run_id, created_at, agent情報, environment情報
    - requirements: 要件の参照元、分解した要件項目、前提・仮定、不明点・質問
    - definitions: 内部定義（用語・変数・仕様解釈）、守るべき不変条件

(2) 次のイベントのたびに structured_output.steps にステップを追加し、根拠(evidence)とdecision_idを残すこと：
    - 要件理解を更新した
    - 仮定(assumption)を置いた/撤回した
    - 設計判断(decision)をした
    - 実装/テスト/デバッグを行った
    - ガイドライン遵守判断をした
    - 不整合/逸脱を検知した、または疑いが生じた

(3) 設計判断は必ず decision_id を付け、最終説明(final)にも decision_id を列挙して整合性検証可能にすること。
    - 例: decision_id: "DESIGN-001", "API-ENDPOINT-002" 等
    - final.decision_map に使用したdecision_idを全てリストアップ

(4) definitions（内部定義）は辞書として維持し、定義を変更する場合は changes に before/after と理由・影響・承認者を必ず残すこと。
    - 既存定義の上書きは「変更理由なし」を厳禁とする
    - change_id, before, after, reason, impact, approved_by を必須記録

(5) ガイドラインは ruleset_id を指定し、各rule_idに pass/fail/na/unknown を付け、証跡(evidence_ref)を記録すること。
    - 各ステップ完了時にガイドライン遵守状況をチェック
    - fail の場合は必ず deviations にもイベント記録

(6) 逸脱イベントは deviations に必ず記録すること（逸脱しかけた場合も含む）。
    - 逸脱タイプ: policy_risk, scope_creep, unsafe_action, hallucination, spec_violation, process_skip
    - 重大度: critical/high/medium/low
    - recovered: 自力復帰できたかのフラグ

(7) 推論安定性のため、重要な判断は「自己検算」を最低1回行い、checks.stability に差分を記録すること。
    - 同一条件で再度推論し、結果の差分を severity: none/minor/major で記録
    - major差分が発生した場合は要注意として delta_summary に詳細記録

(8) 最終回答前に、checks（consistency/definition_consistency/explanation_clarity）を更新し、final にまとめを記録すること。
    - consistency: 推論・設計・説明の不整合チェック
    - definition_consistency: 内部定義の揺れ・矛盾チェック
    - explanation_clarity: 説明の明確度（structure/evidence/terminology/actionability の0-1スコア）

【出力ルール】
- ユーザー向けの最終回答は通常通り提示する。
- structured_outputは監査ログなので、省略せず更新し続ける（ユーザーへの表示は不要）。
- structured_outputの更新は作業と並行して行い、後回しにしない。
- セッション完了時には、全フィールドが適切に埋まった完全な structured_output を保持していること。

【AICQ Audit Log Schema準拠】
本structured_outputは AICQ_AUDIT_LOG_SCHEMA.md に定義されたJSONスキーマに厳密に準拠すること。
以下の8つの必須フィールドが全て適切に維持されていることを確認：
meta, requirements, definitions, steps, guidelines, deviations, checks, final
```

---

### Step 3: 適用確認

Playbook適用後、以下を確認してください：

```markdown
□ Devinがstructured_outputを初期化した
□ meta情報が正しく設定された
□ requirements.req_items に最低1件の要件が記録された
□ definitions.terms が初期化された
□ "理解しました" などの確認応答があった
```

### Step 4: セッション中の監視

セッション実行中は以下をチェック：

```markdown
□ 重要な作業後にstructured_outputが更新されている
□ decision_id が一意かつ説明的な名前になっている
□ deviations が適切に記録されている（該当する場合）
□ evidence_ref が実際のファイル/ログ/テストを指している
```

---

## ⚙️ Playbookカスタマイズガイド

### 基本カスタマイズ

**プロジェクト固有ルールの追加:**
```text
【プロジェクト固有追加ルール: {PROJECT_NAME}】
(9) 本プロジェクトでは以下の追加要件を満たすこと：
    - セキュリティチェック: 各API実装時にOWASP Top10準拠を確認
    - パフォーマンス: レスポンス時間1秒以内を必須要件とする
    - 可用性: 99.9%稼働率を前提とした設計判断を行う
```

**業界固有の調整:**
```text
【金融業界向け調整】
(10) 金融規制対応のため、以下を追加記録：
     - データ保護法準拠: GDPR/個人情報保護法の適用判断
     - 監査証跡: 金融庁検査対応のため詳細証跡を evidence に残す
     - リスク評価: 信用リスク/オペリスクの観点での影響分析
```

### 高度なカスタマイズ

**メトリクス強化版:**
```text
【AICQメトリクス強化版】
(11) 以下のメトリクス向上のため追加記録：
     - 要件理解正確度向上: requirements.assumptions に risk と reason を必須記録
     - 推論安定性向上: 重要判断では recheck_runs を3回実施
     - 説明明確度向上: final.summary に「結論→根拠→検証→残課題」の構造を厳守
```

**デバッグ支援版:**
```text
【トラブル対応強化版】
(12) トラブル発生時の詳細記録：
     - エラー発生時: steps[].evidence に完全なスタックトレース記録
     - 不明点発生時: requirements.questions に target='user' で明確な質問記録
     - 仮説修正時: definitions.changes に仮説変更の詳細プロセス記録
```

### カスタマイズ時の注意事項

⚠️ **互換性の維持**
- 基本ルール(1)-(8)は変更・削除禁止
- 追加ルールは(9)以降の番号を使用
- AICQ Audit Log Schema の必須フィールドは維持

⚠️ **運用負荷の考慮**
- 過度に詳細な記録はAIの処理速度を低下させる
- 本当に必要な項目のみ追加する
- 定期的にカスタマイズの効果を検証

---

## 🔧 トラブルシューティング

### よくある問題と対処法

#### 問題1: structured_outputが更新されない

**症状:**
- 作業は進んでいるが、structured_outputが初期状態のまま
- steps配列が空、または古い情報のまま

**原因と対処法:**
```markdown
□ 原因: Playbookが正しく伝わっていない
  → 対処: Playbook本文を再度コピペして適用

□ 原因: Devinが「更新不要」と判断している  
  → 対処: 「structured_outputを今すぐ更新してください」と明示的に指示

□ 原因: セッション途中でPlaybook指示が忘れられた
  → 対処: 「監査ログルールを思い出してください」とリマインド
```

#### 問題2: decision_idが不適切

**症状:**
- decision_idが"D1", "D2"等の無意味な名前
- 同じdecision_idが重複している
- final.decision_mapに記載されていない

**対処法:**
```text
【decision_id改善指示】
decision_idは以下のルールで命名し直してください：
- 形式: {カテゴリ}-{連番3桁} (例: API-001, AUTH-002, DB-003)
- 内容が推測できる説明的な名前にする
- final.decision_mapに必ず全てリストアップする
```

#### 問題3: deviationsが記録されない

**症状:**
- 明らかに逸脱やガイドライン違反があるが、deviations配列が空
- guidelines.checks で fail になっているのに対応するdeviation記録がない

**対処法:**
```text
【逸脱記録強化指示】
以下の状況では必ずdeviationsに記録してください：
- guidelines.checks でfailが発生した場合
- 「〜するべきだが、今回は例外的に〜」という判断をした場合
- エラー・警告が発生したが続行した場合
- 人間の介入が必要になった場合
```

#### 問題4: JSON Schema バリデーションエラー

**症状:**
- セッション終了時にvalidation_status = 'failed'
- 必須フィールドが欠落している

**対処法:**
```markdown
1. AICQ_AUDIT_LOG_SCHEMA.md の必須フィールドを確認
2. 欠落フィールドを特定（特に多いのは以下）：
   □ meta.agent (name/plan/version)
   □ requirements.req_items (最低1件必須)
   □ steps (最低1件必須)
   □ final.decision_map

3. 不足分を補完してstructured_outputを完成させる
```

### 緊急時対応

**セッション途中でPlaybookが無効化された場合:**

```text
【緊急復旧指示】
監査ログルールが途中で忘れられているようです。
以下を即座に実行してください：

1. 現在のstructured_outputの状態を確認
2. 不足している情報を特定
3. これまでの作業内容を振り返り、steps配列を補完
4. 今後の作業では再び監査ログルールを遵守
```

---

## 📚 関連ドキュメント

### 必須参照ドキュメント

| ドキュメント | 役割 | 参照タイミング |
|-------------|------|---------------|
| **AI-WORKLOG-ENFORCEMENT-GUIDE.md** | 3段階STOP-GATE、運用ルール | セッション開始前 |
| **AICQ_AUDIT_LOG_SCHEMA.md** | JSONスキーマ定義 | Playbook適用時 |
| **AICQ_DATA_MODEL.md** | データモデル・保存形式 | システム実装時 |

### 補足資料

| ドキュメント | 役割 | 参照タイミング |
|-------------|------|---------------|
| **AICQ_METRICS_SPEC.md** | メトリクス算出仕様 | 品質分析時 |
| **AI-WORKLOG-GRANULARITY-GUIDE.md** | セッション分割基準 | 複雑なタスク時 |
| **AI-PRE-WORK-CHECKLIST.md** | 事前確認チェックリスト | 初回起動時 |

### バージョン管理

- **Playbook更新**: 本ファイルのversion更新と共にlast_updatedを更新
- **スキーマ変更**: AICQ_AUDIT_LOG_SCHEMA.md の変更に連動して本Playbookも更新
- **後方互換性**: 既存セッションの structured_output 形式は保持

---

## 🎯 運用チェックリスト

### セッション開始時
```markdown
□ 3段階STOP-GATE完了
□ Playbook本文をコピペ適用
□ Devinからの確認応答を取得
□ structured_outputの初期化を確認
```

### セッション中
```markdown
□ 定期的にstructured_outputの更新状況を確認
□ decision_idの命名ルールを監視
□ 逸脱・不整合の記録漏れがないか確認
```

### セッション終了時
```markdown
□ 全必須フィールドが埋まっていることを確認
□ final.decision_mapとsteps内のdecision_idの整合性確認
□ JSON Schema バリデーション結果の確認
□ API回収・保存の実行
```

このPlaybookテンプレートを活用することで、確実で一貫した監査ログの取得が可能になり、AICQメトリクスによる継続的な品質改善を実現できます。