# AI監査ログ シナリオクックブック - 実践的記録レシピ集

---
document_type: scenario_cookbook
target_audience:
  - AIエージェント（Devin, Cursor等）
  - AI作業実行者
  - 開発チームリーダー
priority: critical
scope: audit_log_practical_examples
version: 1.0
last_updated: 2026-03-10
related_documents:
  - AI-AUDIT-LOG-QUICK-DECISION-GUIDE.md
  - AICQ_AUDIT_LOG_SCHEMA.md
  - AI-WORKLOG-ENFORCEMENT-GUIDE.md
---

## 📖 1. このクックブックの使い方

### 1.1 目的
このクックブックは、実際の開発現場で遭遇する典型的なシーンに対して、**即座にコピー&ペーストできる監査ログ記録例**を提供します。

**解決する問題:**
- ❓ 「この状況、どう記録すればいい？」
- ❓ 「どのフィールドに何を書けばいい？」
- ❓ 「decision_idはどう付番する？」
- ❓ 「複数の変更が同時に起きた場合は？」

### 1.2 構成
各シナリオは **「状況 → 判断 → 記録例」** の3ステップ構成：

```
📋 状況: 何が起きたか（トリガー・影響範囲）
🤔 判断: 記録要否・記録先の決定プロセス
📝 記録例: コピペ可能なJSON（完全版）
💡 ポイント: 注意事項・よくある間違い
🔗 関連: 他シナリオとの組み合わせ
```

### 1.3 活用方法
1. **類似状況の検索** → キーワード・フェーズ・フィールド別インデックス活用
2. **該当レシピをコピー** → JSONをコピーしてカスタマイズ
3. **組み合わせ応用** → 複数シナリオの同時発生時の対応
4. **チーム標準化** → プロジェクト固有のカスタマイズ

---

## 📋 2. 20の実践シナリオ概要

### 🐛 カテゴリA: バグ・問題対応（5シナリオ）
| # | シナリオ | 発生Phase | 主な更新先 |
|---|----------|-----------|-----------|
| 1 | 実装中にロジックバグ発見 | Phase 2 | deviations + steps |
| 2 | テスト実行でエッジケース失敗 | Phase 3 | deviations + steps |
| 3 | コードレビューで脆弱性指摘 | Phase 4 | deviations + steps.decisions |
| 4 | 本番環境でパフォーマンス劣化検知 | Phase 6 | deviations + steps |
| 5 | 依存ライブラリの脆弱性アラート | 随時 | deviations + requirements.assumptions |

### 🎯 カテゴリB: 設計・判断変更（5シナリオ）
| # | シナリオ | 発生Phase | 主な更新先 |
|---|----------|-----------|-----------|
| 6 | 要件の曖昧さ発見と明確化 | Phase 0-1 | requirements.questions + req_items |
| 7 | 技術選定の変更 | Phase 1 | steps.decisions + definitions.changes |
| 8 | アーキテクチャパターンの見直し | Phase 1 | steps.decisions + definitions.invariants |
| 9 | データモデル設計の変更 | Phase 1-2 | steps.decisions + definitions.changes |
| 10 | API仕様の後方互換性対応 | Phase 1-2 | steps.decisions + requirements.assumptions |

### ⚙️ カテゴリC: 実装・テスト作業（5シナリオ）
| # | シナリオ | 発生Phase | 主な更新先 |
|---|----------|-----------|-----------|
| 11 | 新機能の実装 | Phase 2 | steps.outputs + evidence |
| 12 | リファクタリング実施 | Phase 2 | steps.decisions + outputs |
| 13 | ユニットテスト・統合テスト作成 | Phase 3 | steps.outputs + evidence |
| 14 | CI/CDパイプライン設定変更 | Phase 5 | steps.decisions + outputs |
| 15 | ドキュメント作成・更新 | 随時 | steps.outputs |

### 🤝 カテゴリD: 協働・外部要因（5シナリオ）
| # | シナリオ | 発生Phase | 主な更新先 |
|---|----------|-----------|-----------|
| 16 | ユーザーからの要件変更依頼 | 随時 | requirements.req_items + assumptions |
| 17 | 他チームAPIの仕様変更通知 | 随時 | requirements.assumptions + deviations |
| 18 | 並行開発でのマージコンフリクト解決 | Phase 4 | deviations + steps |
| 19 | 緊急ホットフィックス対応 | 随時 | deviations + steps.decisions |
| 20 | 実験的機能の試験導入と評価 | Phase 2-3 | steps.decisions + evidence |

---

## 🔧 3. シナリオ詳細（全20個）

### カテゴリA: バグ・問題対応

### シナリオ1: 実装中にロジックバグ発見

**📋 状況**
- 発生タイミング: Phase 2（実装フェーズ）
- トリガー: ユーザー登録処理の実装中、メールアドレス重複チェックが機能していないことを発見
- 影響範囲: 既存のユーザー登録ロジック、データ整合性

**🤔 判断**
- 記録要否: YES（逸脱検知 + 修正対応）
- 記録先: `deviations`（逸脱記録） + `steps`（修正作業）
- 更新タイプ: 新規追加（deviation） + 新規追加（修正step）

**📝 記録例（JSON）**
```json
{
  "deviations": [{
    "dev_id": "DEV-001",
    "type": "logic_error",
    "severity": "high",
    "description": "ユーザーメールアドレスの重複チェックロジックが実装されていない",
    "detected_at_step": "IMPL-003",
    "recovered": true,
    "root_cause": "データベース制約のみに依存、アプリケーション層での事前チェック不足"
  }],
  "steps": [{
    "step_id": "IMPL-004",
    "phase": "implement",
    "goal": "DEV-001修正: メールアドレス重複チェック実装",
    "inputs": ["既存UserService", "重複チェック要件"],
    "outputs": ["UserService.js修正", "重複チェックロジック追加", "エラーハンドリング追加"],
    "reasoning_summary": {
      "summary": "DBレベル制約に加えてアプリケーション層での事前チェックを実装",
      "alternatives_considered": ["DB制約のみ", "アプリ層チェックのみ", "両方実装"],
      "assumptions_used": ["ASM-002"],
      "risks": ["パフォーマンス若干低下", "競合状態でのエッジケース"]
    },
    "decisions": [{
      "decision_id": "IMPL-004-D1",
      "decision": "DB制約 + アプリ層事前チェック の二重保護を採用",
      "rationale": "確実性とユーザビリティの両立。DB例外よりも事前チェックエラーの方がUX良好",
      "req_links": ["REQ-USER-001"]
    }],
    "evidence": [{
      "type": "test",
      "ref": "duplicate_email_test.js",
      "result": "重複チェック正常動作確認。既存メール登録で適切なエラー応答"
    }],
    "self_checks": {
      "consistency_checked": true,
      "guidelines_checked": true,
      "notes": "セキュリティガイドライン準拠確認済み"
    }
  }]
}
```

**💡 ポイント**
- **即座に記録**: バグ発見時は作業を止めてでも deviation を記録
- **recovered フラグ**: 修正完了後に true に更新
- **root_cause**: 将来の類似問題防止のため、根本原因を明記
- **evidence**: 修正確認のテスト結果を必ず記録

**🔗 関連シナリオ**
- シナリオ13（テスト作成）: 修正後のテスト強化
- シナリオ12（リファクタリング）: 根本的な設計見直しが必要な場合

---

### シナリオ2: テスト実行でエッジケース失敗

**📋 状況**
- 発生タイミング: Phase 3（テストフェーズ）
- トリガー: 境界値テスト実行中、パスワード長256文字で処理がハングアップ
- 影響範囲: パスワードバリデーション、パフォーマンス

**🤔 判断**
- 記録要否: YES（テスト失敗 = 逸脱検知）
- 記録先: `deviations` + `steps`（修正step）
- 更新タイプ: 新規追加

**📝 記録例（JSON）**
```json
{
  "deviations": [{
    "dev_id": "DEV-002",
    "type": "performance",
    "severity": "medium",
    "description": "パスワード長256文字でハッシュ処理がタイムアウト（>10秒）",
    "detected_at_step": "TEST-005",
    "recovered": true,
    "root_cause": "bcryptのコスト設定が本番想定より高い + 長文字列未考慮"
  }],
  "steps": [{
    "step_id": "TEST-006",
    "phase": "test",
    "goal": "DEV-002修正: パスワード長制限とパフォーマンス改善",
    "outputs": ["パスワード長上限128文字に設定", "bcryptコスト調整", "境界値テスト更新"],
    "decisions": [{
      "decision_id": "TEST-006-D1",
      "decision": "パスワード最大長を128文字に制限",
      "rationale": "NIST推奨（64文字以上）を満たしつつ、パフォーマンスとのバランス",
      "req_links": ["REQ-SEC-002"]
    }],
    "evidence": [{
      "type": "test",
      "ref": "password_performance_test.js",
      "result": "128文字: 0.2秒、256文字制限エラー: 即座にレスポンス"
    }]
  }]
}
```

**💡 ポイント**
- **エッジケースは重要な逸脱**: 境界値での失敗は必ず記録
- **パフォーマンス測定**: 修正前後の定量データを evidence に記録
- **制限値の根拠**: decision で「なぜその値にしたか」を明記

**🔗 関連シナリオ**
- シナリオ1（バグ発見）: 同様の逸脱検知パターン
- シナリオ4（パフォーマンス問題）: パフォーマンス関連の類似対応

---

### シナリオ3: コードレビューで脆弱性指摘

**📋 状況**
- 発生タイミング: Phase 4（レビューフェーズ）
- トリガー: レビューアからSQL インジェクション脆弱性の指摘
- 影響範囲: データベースアクセス全般、セキュリティ

**🤔 判断**
- 記録要否: YES（セキュリティ逸脱 + 対応判断）
- 記録先: `deviations` + `steps.decisions` + `guidelines.checks`
- 更新タイプ: 新規追加 + ガイドライン更新

**📝 記録例（JSON）**
```json
{
  "deviations": [{
    "dev_id": "DEV-003",
    "type": "security",
    "severity": "critical",
    "description": "レビューア指摘: 動的SQLクエリでパラメータ化未実装（SQL Injection脆弱性）",
    "detected_at_step": "REVIEW-001",
    "recovered": true,
    "root_cause": "セキュリティガイドライン理解不足、ORM使用の誤解"
  }],
  "guidelines": {
    "ruleset_id": "SEC-GUIDE-v2.1",
    "checks": [{
      "rule_id": "SEC-DB-001",
      "description": "データベースアクセス時はパラメータ化クエリを使用",
      "status": "fail",
      "evidence_ref": "UserRepository.js:45-52",
      "step_id": "REVIEW-001"
    }]
  },
  "steps": [{
    "step_id": "SEC-FIX-001",
    "phase": "implement",
    "goal": "SQL Injection脆弱性修正",
    "decisions": [{
      "decision_id": "SEC-FIX-001-D1",
      "decision": "全ての動的クエリをパラメータ化クエリに変更",
      "rationale": "OWASP推奨のベストプラクティス。ORMのraw queryも対象",
      "req_links": ["REQ-SEC-001"]
    }],
    "outputs": ["UserRepository.js修正", "ProductRepository.js修正", "セキュリティテスト追加"],
    "evidence": [{
      "type": "test",
      "ref": "sql_injection_test.js",
      "result": "悪意のある入力に対して適切にエスケープされることを確認"
    }]
  }]
}
```

**💡 ポイント**
- **critical severity**: セキュリティ問題は必ず最高重要度
- **guidelines.checks**: ガイドライン違反は専用フィールドに記録
- **全面的な影響確認**: 類似箇所の一括修正を outputs に記録
- **セキュリティテスト**: 修正後のセキュリティテストを evidence として必須

**🔗 関連シナリオ**
- シナリオ1, 2（バグ・問題対応）: 同様の逸脱対応パターン
- シナリオ13（テスト作成）: セキュリティテスト強化

---

### シナリオ4: 本番環境でパフォーマンス劣化検知

**📋 状況**
- 発生タイミング: Phase 6（運用フェーズ）
- トリガー: 本番監視で API レスポンス時間が SLA（500ms）を超過（平均 1.2秒）
- 影響範囲: ユーザー体験、SLA違反

**🤔 判断**
- 記録要否: YES（本番での重大な逸脱）
- 記録先: `deviations` + `steps`（調査・対応）
- 更新タイプ: 新規追加（緊急対応として）

**📝 記録例（JSON）**
```json
{
  "deviations": [{
    "dev_id": "DEV-004",
    "type": "performance",
    "severity": "high",
    "description": "本番API レスポンス時間 SLA違反: 500ms → 平均1200ms",
    "detected_at_step": "MONITOR-001",
    "recovered": true,
    "root_cause": "N+1クエリ問題。商品一覧取得時に関連データを個別クエリで取得"
  }],
  "steps": [{
    "step_id": "HOTFIX-001",
    "phase": "implement",
    "goal": "緊急パフォーマンス改善: N+1クエリ解消",
    "inputs": ["本番監視データ", "APM分析結果"],
    "outputs": ["ProductService.js修正", "eager loading実装", "クエリ最適化"],
    "reasoning_summary": {
      "summary": "本番データ分析によりN+1クエリ問題を特定。JOINによるeager loading で解決",
      "alternatives_considered": ["キャッシュ追加", "クエリ最適化", "ページング導入"],
      "risks": ["メモリ使用量増加", "複雑なクエリによる別の性能問題"]
    },
    "decisions": [{
      "decision_id": "HOTFIX-001-D1",
      "decision": "eager loading による一括取得を実装",
      "rationale": "最も直接的で効果的。キャッシュは根本解決でない",
      "req_links": ["REQ-PERF-001"]
    }],
    "evidence": [{
      "type": "run_log",
      "ref": "production_apm_before_after.json",
      "result": "修正後: 平均レスポンス時間 320ms（36%改善）、SLA達成"
    }]
  }]
}
```

**💡 ポイント**
- **本番での問題は最重要**: severity を high/critical に設定
- **APM データ活用**: evidence に本番監視データを含める
- **定量的効果測定**: 修正前後の具体的な数値を記録
- **根本原因分析**: 表面的な症状でなく、技術的根本原因を明記

**🔗 関連シナリオ**
- シナリオ2（エッジケース失敗）: パフォーマンス問題の類似対応
- シナリオ19（緊急ホットフィックス）: 緊急対応パターン

---

### シナリオ5: 依存ライブラリの脆弱性アラート

**📋 状況**
- 発生タイミング: 随時（セキュリティアラート受信）
- トリガー: GitHub Security Alert で使用中の express 4.16.4 に高レベル脆弱性検知
- 影響範囲: アプリケーション全体、セキュリティ

**🤔 判断**
- 記録要否: YES（セキュリティ脅威 + 対応判断）
- 記録先: `deviations` + `requirements.assumptions` + `steps`
- 更新タイプ: 新規追加 + 既存前提更新

**📝 記録例（JSON）**
```json
{
  "deviations": [{
    "dev_id": "DEV-005",
    "type": "security",
    "severity": "high",
    "description": "依存ライブラリ express 4.16.4 に DoS 脆弱性（CVE-2022-24999）",
    "detected_at_step": "SECURITY-SCAN-001",
    "recovered": true,
    "root_cause": "依存ライブラリの定期的なセキュリティ更新が未実施"
  }],
  "requirements": {
    "assumptions": [{
      "assumption_id": "ASM-004",
      "text": "express 4.16.4 は安全",
      "status": "retracted",
      "reason": "CVE-2022-24999 脆弱性発見により前提が無効化",
      "risk": "high"
    }, {
      "assumption_id": "ASM-005",
      "text": "express 4.18.2 以降は当該脆弱性が修正済み",
      "status": "tentative",
      "reason": "公式セキュリティアドバイザリによる確認",
      "risk": "low"
    }]
  },
  "steps": [{
    "step_id": "SEC-UPDATE-001",
    "phase": "implement",
    "goal": "express セキュリティアップデート",
    "decisions": [{
      "decision_id": "SEC-UPDATE-001-D1",
      "decision": "express を 4.18.2 にメジャーアップデート",
      "rationale": "脆弱性修正 + 破壊的変更の最小化。4.18系は4.16系との互換性が高い",
      "req_links": ["REQ-SEC-001"]
    }],
    "outputs": ["package.json更新", "依存関係アップデート", "回帰テスト実行"],
    "evidence": [{
      "type": "test",
      "ref": "security_regression_test.js",
      "result": "アップデート後の動作確認完了。既存機能に影響なし"
    }]
  }]
}
```

**💡ポイント**
- **assumptions の更新**: セキュリティ情報変化時は前提条件を見直す
- **CVE番号記載**: 脆弱性の詳細トレーサビリティのため必ず記載
- **互換性考慮**: アップデート時の破壊的変更リスクを判断理由に含める
- **回帰テスト**: セキュリティアップデート後は必ず動作確認

**🔗 関連シナリオ**
- シナリオ3（セキュリティ指摘）: セキュリティ問題対応の類似パターン
- シナリオ17（外部要因）: 外部からの影響による対応

---

### カテゴリB: 設計・判断変更

### シナリオ6: 要件の曖昧さ発見と明確化

**📋 状況**
- 発生タイミング: Phase 0-1（要件分析・設計初期）
- トリガー: 「ユーザーが商品をお気に入り登録できる」機能で、登録上限数が不明
- 影響範囲: 要件定義、データ設計

**🤔 判断**
- 記録要否: YES（要件の不明確さ = 重要な発見）
- 記録先: `requirements.questions` → `requirements.req_items`（解決後）
- 更新タイプ: 新規追加 → 既存更新

**📝 記録例（JSON）**
```json
{
  "requirements": {
    "questions": [{
      "q_id": "Q-001",
      "question": "お気に入り商品の登録上限数は？無制限？制限あり？",
      "target": "user",
      "status": "answered",
      "answer": "1ユーザーあたり上限100件。UX考慮とシステム負荷のバランス"
    }],
    "req_items": [{
      "req_id": "REQ-FAV-001",
      "text": "ユーザーは商品をお気に入り登録でき、上限は100件とする",
      "type": "functional",
      "priority": "must",
      "acceptance": [
        "お気に入り登録ボタンで商品を登録できる",
        "100件到達時は「上限に達しました」メッセージ表示",
        "上限到達後は古い登録を削除してから新規登録可能"
      ]
    }],
    "assumptions": [{
      "assumption_id": "ASM-006",
      "text": "お気に入り100件で十分なユーザーが95%以上",
      "reason": "競合サービス調査により一般的なユーザー行動パターンから推定",
      "risk": "medium",
      "status": "tentative"
    }]
  }
}
```

**💡 ポイント**
- **質問 → 回答 → 要件** の流れを structured_output で追跡
- **acceptance 基準**: 曖昧だった部分を具体的な受入条件に変換
- **assumptions 記録**: 「なぜその制限値にしたか」の根拠を記録
- **status 管理**: 質問の解決状況を適切に更新

**🔗 関連シナリオ**
- シナリオ16（ユーザー要件変更）: ユーザーとのコミュニケーション関連
- シナリオ9（データモデル変更）: 要件明確化に伴う設計変更

---

### シナリオ7: 技術選定の変更（ライブラリ・フレームワーク）

**📋 状況**
- 発生タイミング: Phase 1（設計フェーズ）
- トリガー: 当初選定の Redux が要件に対してオーバースペック。状態管理をより軽量な Zustand に変更検討
- 影響範囲: フロントエンド設計、開発効率

**🤔 判断**
- 記録要否: YES（重要な技術判断変更）
- 記録先: `steps.decisions` + `definitions.changes`
- 更新タイプ: 新規追加（新判断） + 新規追加（定義変更）

**📝 記録例（JSON）**
```json
{
  "steps": [{
    "step_id": "DESIGN-005",
    "phase": "design",
    "goal": "状態管理ライブラリの再選定",
    "reasoning_summary": {
      "summary": "Redux は本プロジェクトの要件に対してオーバースペック。Zustand による軽量化を検討",
      "alternatives_considered": ["Redux継続", "Zustand", "Context API", "Jotai"],
      "assumptions_used": ["ASM-007"],
      "risks": ["学習コスト", "エコシステムの差", "将来のスケーラビリティ"]
    },
    "decisions": [{
      "decision_id": "DESIGN-005-D1",
      "decision": "状態管理を Redux から Zustand に変更",
      "rationale": "シンプルなアプリ要件にマッチ。Redux比で学習コスト50%削減、Bundle size 60%削減見込み",
      "req_links": ["REQ-PERF-002", "REQ-DEV-001"]
    }],
    "evidence": [{
      "type": "doc_ref",
      "ref": "zustand_vs_redux_comparison.md",
      "result": "要件複雑度・チーム経験・パフォーマンス の3観点でZustandが優位"
    }]
  }],
  "definitions": {
    "changes": [{
      "change_id": "DEF-CHG-002",
      "term": "状態管理",
      "before": "Redux + Redux Toolkit による集中管理",
      "after": "Zustand による軽量状態管理",
      "reason": "要件の複雑度に対してReduxがオーバースペック",
      "impact": "フロントエンド設計の全面見直し、開発速度向上期待",
      "approved_by": "self"
    }]
  }
}
```

**💡 ポイント**
- **代替案の網羅**: alternatives_considered で検討した選択肢を全て記録
- **定量的根拠**: Bundle size、学習コストなど具体的な比較データ
- **要件とのリンク**: req_links でパフォーマンス・開発効率要件と関連付け
- **影響範囲の明記**: impact で変更による影響を具体的に記述

**🔗 関連シナリオ**
- シナリオ8（アーキテクチャ見直し）: 技術選定に伴う設計変更
- シナリオ11（新機能実装）: 変更後の技術での実装

---

### シナリオ8: アーキテクチャパターンの見直し

**📋 状況**
- 発生タイミング: Phase 1（設計フェーズ）
- トリガー: 当初想定のモノリシック構成では、将来の機能拡張時にスケーラビリティ課題が予想される
- 影響範囲: システム全体アーキテクチャ、開発・運用方針

**🤔 判断**
- 記録要否: YES（アーキテクチャの根本的変更）
- 記録先: `steps.decisions` + `definitions.invariants`
- 更新タイプ: 新規追加

**📝 記録例（JSON）**
```json
{
  "steps": [{
    "step_id": "ARCH-001",
    "phase": "design",
    "goal": "アーキテクチャパターンの選定",
    "reasoning_summary": {
      "summary": "モノリシック vs マイクロサービス vs モジュラーモノリス を比較検討",
      "alternatives_considered": [
        "モノリシック（当初案）",
        "マイクロサービス",
        "モジュラーモノリス",
        "サーバーレス"
      ],
      "assumptions_used": ["ASM-008", "ASM-009"],
      "risks": [
        "複雑性増大",
        "運用コスト増加", 
        "開発初期速度低下",
        "分散システム固有の問題"
      ]
    },
    "decisions": [{
      "decision_id": "ARCH-001-D1",
      "decision": "モジュラーモノリス パターンを採用",
      "rationale": "初期開発速度とスケーラビリティのバランス。将来のマイクロサービス分割も容易",
      "req_links": ["REQ-SCALE-001", "REQ-DEV-001", "REQ-DEPLOY-001"]
    }],
    "evidence": [{
      "type": "doc_ref", 
      "ref": "architecture_comparison_matrix.xlsx",
      "result": "開発速度・運用複雑度・スケーラビリティの3軸でモジュラーモノリスが最適解"
    }]
  }],
  "definitions": {
    "invariants": [
      "各モジュールは明確な境界と責務を持つ",
      "モジュール間は定義されたインターフェースのみで通信",
      "データベースアクセスは各モジュールが独立して管理",
      "共通ロジックは shared モジュールに集約",
      "将来のマイクロサービス分割を考慮した設計とする"
    ]
  }
}
```

**💡 ポイント**
- **アーキテクチャ決定記録（ADR）**: 重要な建築判断は必ず記録
- **多軸評価**: 単一観点でなく複数の評価軸での比較結果を evidence に
- **invariants 活用**: アーキテクチャパターンの制約・原則を不変条件として記録
- **将来への配慮**: 現在の判断が将来の選択肢にどう影響するかを考慮

**🔗 関連シナリオ**
- シナリオ7（技術選定変更）: 技術判断の関連パターン
- シナリオ9（データモデル変更）: アーキテクチャに伴う具体的設計変更

---

### シナリオ9: データモデル設計の変更

**📋 状況**
- 発生タイミング: Phase 1-2（設計〜実装初期）
- トリガー: ユーザーテーブル設計で、当初の単一テーブルでは拡張性に課題。プロファイル情報を別テーブルに分離する設計変更
- 影響範囲: データベース設計、API仕様、データアクセス層

**🤔 判断**
- 記録要否: YES（データモデルの重要な変更）
- 記録先: `steps.decisions` + `definitions.changes`
- 更新タイプ: 新規追加

**📝 記録例（JSON）**
```json
{
  "steps": [{
    "step_id": "DATA-001",
    "phase": "design",
    "goal": "ユーザーデータモデルの再設計",
    "reasoning_summary": {
      "summary": "単一ユーザーテーブルから基本情報とプロファイル情報への分離を検討",
      "alternatives_considered": [
        "単一テーブル継続（users）",
        "2テーブル分離（users + user_profiles）",
        "3テーブル分離（users + profiles + preferences）",
        "NoSQL ドキュメント型"
      ],
      "assumptions_used": ["ASM-010"],
      "risks": ["JOIN複雑化", "API変更", "既存データ移行"]
    },
    "decisions": [{
      "decision_id": "DATA-001-D1", 
      "decision": "users + user_profiles の2テーブル構成を採用",
      "rationale": "正規化によるデータ整合性向上 + 将来の項目追加容易性。JOINコストは許容範囲",
      "req_links": ["REQ-DATA-001", "REQ-SCALE-002"]
    }],
    "outputs": ["ER図更新", "migration script作成", "UserService修正設計"],
    "evidence": [{
      "type": "doc_ref",
      "ref": "user_table_design_v2.drawio",
      "result": "新ER図でリレーション整合性確認完了"
    }]
  }],
  "definitions": {
    "changes": [{
      "change_id": "DEF-CHG-003",
      "term": "ユーザーデータ構造",
      "before": "単一usersテーブルで全情報管理",
      "after": "users（基本情報）+ user_profiles（拡張情報）の分離構成",
      "reason": "データ正規化とスケーラビリティ向上",
      "impact": "UserAPI仕様変更、データアクセス層全面修正、既存データ移行必要",
      "approved_by": "self"
    }]
  }
}
```

**💡 ポイント**
- **データ設計の変更影響**: impact でAPI、DAO、移行など広範囲の影響を記録
- **ER図の更新**: evidence で設計ドキュメントの更新完了を確認
- **正規化の根拠**: なぜ分離するのか、どのような利益があるかを明記
- **移行の考慮**: 既存データへの影響と移行方法への言及

**🔗 関連シナリオ**
- シナリオ8（アーキテクチャ見直し）: 上位設計に伴う具体的な変更
- シナリオ11（新機能実装）: 変更後のデータモデルでの実装

---

### シナリオ10: API仕様の後方互換性対応

**📋 状況**
- 発生タイミング: Phase 1-2（設計〜実装）
- トリガー: API レスポンス形式変更が必要だが、既存のフロントエンドアプリとの互換性を保つ必要がある
- 影響範囲: API設計、バージョン管理戦略

**🤔 判断**
- 記録要否: YES（API設計の重要な判断）
- 記録先: `steps.decisions` + `requirements.assumptions`
- 更新タイプ: 新規追加

**📝 記録例（JSON）**
```json
{
  "steps": [{
    "step_id": "API-001",
    "phase": "design", 
    "goal": "API後方互換性戦略の決定",
    "reasoning_summary": {
      "summary": "API v1 の互換性を保ちつつ、v2 で新仕様を提供する方法を検討",
      "alternatives_considered": [
        "破壊的変更（非互換）",
        "URLパスバージョニング（/v1, /v2）", 
        "ヘッダーバージョニング",
        "フィールド追加のみ（後方互換保持）"
      ],
      "assumptions_used": ["ASM-011"],
      "risks": ["運用コスト増大", "コードベース複雑化", "廃止スケジュール管理"]
    },
    "decisions": [{
      "decision_id": "API-001-D1",
      "decision": "URLパスバージョニング（/v1, /v2）を採用",
      "rationale": "明確な境界。クライアント側での移行制御可能。6ヶ月の移行期間設定",
      "req_links": ["REQ-API-001", "REQ-COMPAT-001"]
    }],
    "outputs": ["API仕様書v2作成", "バージョニング戦略ドキュメント", "移行スケジュール"],
    "evidence": [{
      "type": "doc_ref",
      "ref": "api_versioning_strategy.md", 
      "result": "v1廃止スケジュール（6ヶ月後）と移行手順確定"
    }]
  }],
  "requirements": {
    "assumptions": [{
      "assumption_id": "ASM-012",
      "text": "既存クライアントは6ヶ月以内にAPIv2へ移行可能",
      "reason": "フロントエンド開発チームとの合意事項",
      "risk": "medium",
      "status": "confirmed"
    }]
  }
}
```

**💡 ポイント**
- **バージョニング戦略**: API設計の重要な判断として必ず記録
- **移行スケジュール**: assumptions で移行期間の前提を明記
- **廃止計画**: evidence で具体的な廃止スケジュールを文書化
- **クライアント影響**: 後方互換性の判断根拠を明確に

**🔗 関連シナリオ**
- シナリオ9（データモデル変更）: データ変更に伴うAPI仕様への影響
- シナリオ17（他チームAPI変更）: 外部APIとの互換性考慮

---

### カテゴリC: 実装・テスト作業

### シナリオ11: 新機能の実装（設計→実装→テスト）

**📋 状況**
- 発生タイミング: Phase 2（実装フェーズ）
- トリガー: ユーザー認証機能の実装完了（登録、ログイン、JWT発行）
- 影響範囲: 認証システム全般

**🤔 判断**
- 記録要否: YES（主要な成果物完成）
- 記録先: `steps.outputs` + `evidence`
- 更新タイプ: 新規追加

**📝 記録例（JSON）**
```json
{
  "steps": [{
    "step_id": "IMPL-005",
    "phase": "implement",
    "goal": "ユーザー認証機能の実装完了",
    "inputs": ["認証設計書", "API仕様書v1", "セキュリティガイドライン"],
    "outputs": [
      "AuthController.js（登録・ログイン・リフレッシュ）",
      "UserService.js（ユーザー管理ロジック）",
      "JwtUtil.js（トークン生成・検証）",
      "auth-middleware.js（認証ミドルウェア）",
      "bcrypt-util.js（パスワードハッシュ）"
    ],
    "reasoning_summary": {
      "summary": "JWT + bcrypt によるステートレス認証を実装。セキュリティベストプラクティス準拠",
      "assumptions_used": ["ASM-013"],
      "risks": ["JWT秘密鍵管理", "トークン盗取リスク"]
    },
    "decisions": [{
      "decision_id": "IMPL-005-D1", 
      "decision": "アクセストークン有効期限15分、リフレッシュトークン7日に設定",
      "rationale": "セキュリティとUXのバランス。OWASP推奨値を参考",
      "req_links": ["REQ-SEC-003"]
    }],
    "evidence": [{
      "type": "test",
      "ref": "auth.test.js",
      "result": "認証フロー全体のテスト完了。正常系・異常系ともにPASS（覆盖率98%）"
    }, {
      "type": "run_log",
      "ref": "postman_auth_collection.json",
      "result": "API動作確認完了。全エンドポイントで期待通りのレスポンス"
    }],
    "self_checks": {
      "consistency_checked": true,
      "guidelines_checked": true,
      "notes": "セキュリティガイドライン全項目を確認済み"
    }
  }]
}
```

**💡 ポイント**
- **outputs の詳細**: 実装した全ファイルを具体的に列挙
- **テストカバレッジ**: evidence でカバレッジ率を定量的に記録
- **セキュリティ確認**: 認証機能は security guidelines の準拠確認が必須
- **API動作確認**: 統合テストレベルでの動作確認も evidence として記録

**🔗 関連シナリオ**
- シナリオ13（テスト作成）: 実装に対応するテスト強化
- シナリオ3（セキュリティ指摘）: セキュリティ関連の注意点

---

### シナリオ12: リファクタリング実施（技術的負債解消）

**📋 状況**
- 発生タイミング: Phase 2（実装フェーズ）
- トリガー: UserService が肥大化（500行超）し、責務が不明確。DRY原則違反も多数発見
- 影響範囲: ユーザー関連ロジック全般

**🤔 判断**
- 記録要否: YES（重要なコード品質改善）
- 記録先: `steps.decisions` + `outputs`
- 更新タイプ: 新規追加

**📝 記録例（JSON）**
```json
{
  "steps": [{
    "step_id": "REFACTOR-001",
    "phase": "implement", 
    "goal": "UserService のリファクタリング（責務分離・重複削除）",
    "inputs": ["既存UserService.js（500行）", "コード分析結果"],
    "outputs": [
      "UserService.js（コア機能、180行）",
      "UserValidationService.js（入力検証、80行）",
      "UserNotificationService.js（通知機能、60行）",
      "UserProfileService.js（プロファイル管理、120行）",
      "shared/validation-utils.js（共通バリデーション）"
    ],
    "reasoning_summary": {
      "summary": "単一責務原則に従い機能別に分離。共通ロジックをutilsに抽出してDRY原則適用",
      "alternatives_considered": [
        "現状維持（技術的負債蓄積）",
        "部分的リファクタリング", 
        "全面的な責務分離（採用）"
      ],
      "risks": ["既存テストの大幅更新", "インポート修正の広範囲影響"]
    },
    "decisions": [{
      "decision_id": "REFACTOR-001-D1",
      "decision": "機能別Service分離 + 共通utilsパターンを採用",
      "rationale": "保守性向上、テスト容易性向上、責務明確化。将来の機能追加も容易",
      "req_links": ["REQ-MAINTAIN-001"]
    }],
    "evidence": [{
      "type": "code_ref",
      "ref": "sonar_analysis_before_after.json",
      "result": "コード品質: C → A、複雑度: 15 → 6、重複: 23% → 3%"
    }, {
      "type": "test", 
      "ref": "user_services_test_suite.js",
      "result": "リファクタリング後のテスト全て通過。カバレッジ維持（96%）"
    }],
    "self_checks": {
      "consistency_checked": true,
      "guidelines_checked": true,
      "notes": "コーディング規約準拠、テスト網羅性確認済み"
    }
  }]
}
```

**💡 ポイント**
- **定量的品質改善**: evidence で品質指標の改善を数値で示す
- **責務分離の明示**: outputs で分離後のファイル構成と行数を具体的に記録
- **リスク管理**: 既存テストやインポートへの影響を risks として事前に認識
- **保守性への投資**: 将来の開発効率向上を rationale として記録

**🔗 関連シナリオ**
- シナリオ11（新機能実装）: リファクタリング後の新機能開発
- シナリオ13（テスト作成）: リファクタリング後のテスト再整備

---

### シナリオ13: ユニットテスト・統合テスト作成

**📋 状況**
- 発生タイミング: Phase 3（テストフェーズ）
- トリガー: 認証機能のテスト強化。ユニットテスト、統合テスト、セキュリティテストを網羅的に作成
- 影響範囲: 品質保証、CI/CD パイプライン

**🤔 判断**
- 記録要否: YES（品質保証の重要な成果物）
- 記録先: `steps.outputs` + `evidence`
- 更新タイプ: 新規追加

**📝 記録例（JSON）**
```json
{
  "steps": [{
    "step_id": "TEST-007",
    "phase": "test",
    "goal": "認証機能の包括的テストスイート作成",
    "inputs": ["認証機能実装コード", "テスト戦略書", "セキュリティ要件"],
    "outputs": [
      "auth-controller.test.js（API層テスト、50ケース）",
      "user-service.test.js（ビジネスロジック、35ケース）", 
      "jwt-util.test.js（トークン処理、25ケース）",
      "auth-integration.test.js（統合テスト、15シナリオ）",
      "auth-security.test.js（セキュリティテスト、10ケース）",
      "fixtures/auth-test-data.json（テストデータ）"
    ],
    "reasoning_summary": {
      "summary": "テストピラミッドに従い、ユニット70%、統合25%、E2E5%の配分で実装",
      "assumptions_used": ["ASM-014"],
      "risks": ["テスト実行時間増大", "CI/CD パイプライン負荷"]
    },
    "decisions": [{
      "decision_id": "TEST-007-D1",
      "decision": "認証テストは並列実行可能な設計とし、独立したテストDBを使用",
      "rationale": "テスト実行時間短縮とデータ汚染回避。CI効率化",
      "req_links": ["REQ-TEST-001"]
    }],
    "evidence": [{
      "type": "test",
      "ref": "auth_test_coverage_report.html",
      "result": "カバレッジ: Statement 96%, Branch 94%, Function 100%"
    }, {
      "type": "run_log", 
      "ref": "ci_test_execution_log.txt",
      "result": "全135テストケース実行完了。実行時間: 2.3分（並列化効果）"
    }],
    "self_checks": {
      "consistency_checked": true,
      "guidelines_checked": true, 
      "notes": "テストガイドライン準拠、エッジケース網羅確認済み"
    }
  }]
}
```

**💡 ポイント**
- **テスト分類の明示**: ユニット・統合・セキュリティなど種類別にテスト数を記録
- **カバレッジ目標達成**: evidence で具体的なカバレッジ率を記録
- **CI/CD効率化**: 並列実行など実行効率への配慮を decisions に記録
- **テストデータ管理**: fixtures の作成もテスト品質向上の重要な成果物

**🔗 関連シナリオ**
- シナリオ11（新機能実装）: 実装に対応するテスト作成
- シナリオ2（エッジケース失敗）: テスト実行結果での問題発見

---

### シナリオ14: CI/CDパイプライン設定変更

**📋 状況**
- 発生タイミング: Phase 5（デプロイフェーズ）
- トリガー: セキュリティスキャン追加、デプロイ自動化、品質ゲート強化が必要
- 影響範囲: CI/CD プロセス、デプロイ効率

**🤔 判断**
- 記録要否: YES（インフラ・プロセス重要変更）
- 記録先: `steps.decisions` + `outputs`
- 更新タイプ: 新規追加

**📝 記録例（JSON）**
```json
{
  "steps": [{
    "step_id": "CICD-001",
    "phase": "deployment",
    "goal": "CI/CDパイプラインのセキュリティ・品質強化",
    "inputs": ["既存GitHub Actions設定", "セキュリティ要件", "デプロイ要件"],
    "outputs": [
      ".github/workflows/ci.yml（更新）",
      ".github/workflows/security-scan.yml（新規）",
      ".github/workflows/deploy-staging.yml（新規）",
      ".github/workflows/deploy-production.yml（新規）",
      "sonar-project.properties（品質ゲート設定）"
    ],
    "reasoning_summary": {
      "summary": "セキュリティスキャン、品質ゲート、段階的デプロイを自動化に組み込む",
      "alternatives_considered": [
        "手動デプロイ継続",
        "部分的自動化",
        "全自動化（採用）"
      ],
      "risks": ["初期設定コスト", "パイプライン複雑化", "障害時の切り戻し"]
    },
    "decisions": [{
      "decision_id": "CICD-001-D1",
      "decision": "staging → production の2段階デプロイ + 品質ゲート必須通過",
      "rationale": "本番品質保証とリリース効率のバランス。障害リスク最小化",
      "req_links": ["REQ-DEPLOY-001", "REQ-QUALITY-001"]
    }],
    "evidence": [{
      "type": "run_log",
      "ref": "pipeline_execution_test.log", 
      "result": "新パイプライン動作確認完了。全ステップ正常実行（総実行時間8.5分）"
    }, {
      "type": "doc_ref",
      "ref": "deployment_flowchart.png",
      "result": "デプロイフロー図更新。承認プロセス組み込み完了"
    }]
  }]
}
```

**💡 ポイント**
- **インフラ変更も記録**: CI/CD設定はコードと同じく重要な成果物
- **段階的デプロイ**: 本番リスク軽減のための意思決定を明確に記録
- **実行時間測定**: パイプライン効率化の効果を定量的に把握
- **フロー図更新**: 複雑なプロセス変更は視覚的ドキュメントも更新

**🔗 関連シナリオ**
- シナリオ13（テスト作成）: CI/CDでのテスト自動実行
- シナリオ5（脆弱性アラート）: セキュリティスキャンでの発見

---

### シナリオ15: ドキュメント作成・更新

**📋 状況**
- 発生タイミング: 随時（実装完了後、仕様変更後）
- トリガー: API実装完了に伴い、OpenAPI仕様書、README、運用マニュアルを作成・更新
- 影響範囲: プロジェクトドキュメント全般

**🤔 判断**
- 記録要否: YES（重要な成果物・保守性向上）
- 記録先: `steps.outputs`
- 更新タイプ: 新規追加

**📝 記録例（JSON）**
```json
{
  "steps": [{
    "step_id": "DOC-001",
    "phase": "implement",
    "goal": "プロジェクトドキュメントの作成・更新",
    "inputs": ["実装済みAPIコード", "設計書", "テスト結果"],
    "outputs": [
      "openapi.yaml（API仕様書完全版）",
      "README.md（プロジェクト概要・セットアップ手順）",
      "docs/deployment-guide.md（デプロイ手順）",
      "docs/troubleshooting.md（トラブルシューティング）",
      "docs/api-examples.md（API使用例集）",
      "CHANGELOG.md（変更履歴）"
    ],
    "reasoning_summary": {
      "summary": "実装完了のタイミングでドキュメントを一括整備。コードとの乖離防止",
      "assumptions_used": ["ASM-015"], 
      "risks": ["ドキュメント更新の継続コスト", "実装とドキュメントの乖離"]
    },
    "decisions": [{
      "decision_id": "DOC-001-D1",
      "decision": "OpenAPI仕様書はコードから自動生成 + 手動補強の方式を採用",
      "rationale": "最新性保持と詳細さのバランス。CI/CDで自動更新チェック",
      "req_links": ["REQ-DOC-001"]
    }],
    "evidence": [{
      "type": "doc_ref",
      "ref": "swagger-ui-screenshot.png",
      "result": "OpenAPI仕様書のSwagger UI正常表示確認。全エンドポイント動作確認可能"
    }, {
      "type": "test",
      "ref": "readme_setup_test.log",
      "result": "README手順に従った新規環境セットアップ成功確認（所要時間15分）"
    }]
  }]
}
```

**💡 ポイント**
- **ドキュメントも成果物**: コードと同等に重要な deliverable として扱う
- **自動生成の活用**: コードとドキュメントの乖離防止策を decisions に記録
- **実用性確認**: README手順の実際の検証を evidence として記録
- **継続性の課題**: ドキュメント更新コストを risks として認識

**🔗 関連シナリオ**
- シナリオ11（新機能実装）: 実装完了に伴うドキュメント更新
- シナリオ14（CI/CD変更）: 自動ドキュメント更新の仕組み

---

### カテゴリD: 協働・外部要因

### シナリオ16: ユーザーからの要件変更依頼

**📋 状況**
- 発生タイミング: 随時（開発中）
- トリガー: ユーザーから「お気に入り機能で、フォルダ分け機能も追加してほしい」との追加要求
- 影響範囲: 要件定義、設計、実装範囲

**🤔 判断**
- 記録要否: YES（要件変更 = 重要な影響）
- 記録先: `requirements.req_items` + `assumptions`
- 更新タイプ: 新規追加 + 既存更新

**📝 記録例（JSON）**
```json
{
  "requirements": {
    "req_items": [{
      "req_id": "REQ-FAV-002",
      "text": "お気に入り商品をフォルダに分類して管理できる",
      "type": "functional", 
      "priority": "should",
      "acceptance": [
        "フォルダを作成・削除・名前変更できる",
        "商品をフォルダに移動できる", 
        "フォルダ別に商品一覧を表示できる",
        "未分類フォ