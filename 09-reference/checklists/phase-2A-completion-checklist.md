---
title: "Phase 2A 完了チェックリスト（事前実装設計）"
version: "1.0.0"
created_date: "2025-11-13"
last_updated: "2025-11-13"
status: "Active"
phase: "Phase 2A - Pre-Implementation Design"
checklist_type: "Phase Completion"
document_type: "Checklist"
---

# Phase 2A 完了チェックリスト（事前実装設計）

> 実装前の設計作業完了確認

**目的**: Phase 2Aの設計が完了し、実装に必要な設計書がすべて揃っていることを確認  
**使用タイミング**: Phase 3（実装）への移行前  
**参照元**: [phase-2A-pre-implementation-design-guide.md](../../00-guides/phase-guides/phase-2A-pre-implementation-design-guide.md)

---

## ✅ 使い方

このチェックリストは、Phase 2A（事前実装設計）のすべてのタスクが完了し、次のフェーズに移行する前に使用します。

### チェック方法
1. 各項目を順番に確認
2. 完了している項目には `[x]` をマーク
3. すべての項目が完了したら、次のフェーズへ進行可能

### 未完了項目がある場合
- 該当項目の担当者に確認
- 必要に応じてタスクを追加・完了
- すべて完了するまで次フェーズへ進まない

---

## 開始条件

### 前提条件チェックリスト

- [ ] 要件分析が完了している
- [ ] ビジネス要件が明確である
- [ ] 非機能要件が定義されている
- [ ] プロジェクトリポジトリが準備されている
- [ ] 開発環境がセットアップされている
- [ ] チームアサインが完了している
- [ ] テックリードが決定している
- [ ] 技術スタックの候補が絞り込まれている
- [ ] 実装チームが待機状態である

## 完了基準

### 必須チェックリスト

- [ ] ADRが作成され、重要な技術決定が記録されている
- [ ] API契約書が作成され、関係チームと合意されている
- [ ] 制約条件文書が作成され、要件が明確である
- [ ] アーキテクチャ概要図が作成されている (推奨)
- [ ] ADRは「なぜその選択か」が明確に説明されている
- [ ] API契約は実装チームが理解できる
- [ ] 制約条件は測定可能である
- [ ] ドキュメントは簡潔である (合計10ページ以内)
- [ ] 実装チームが設計を理解している
- [ ] 不明点が許容範囲内である
- [ ] 必要なツール・環境が準備されている
- [ ] タスク分解が可能な状態である
- [ ] テックリードの承認を得ている
- [ ] ステークホルダーの承認を得ている (必要な場合)

## よくある質問

### Q4: 2日以内に完了しない場合は?

- [ ] **優先順位付け**: 必須成果物に集中
- [ ] **スコープ削減**: 詳細を Phase 2-B に延期
- [ ] **ヘルプ要請**: アーキテクトやテックリードに相談
- [ ] **最終判断**: 3日目に Go/No-Go を決定

---


## 🚨 エスカレーション基準

> **詳細ガイド**: [ESCALATION-CRITERIA-GUIDE.md](../../00-guides/ESCALATION-CRITERIA-GUIDE.md)  
> エスカレーション判断マトリクス、自己診断チェックリスト、判定事例集を参照してください。

### クイックチェック

以下のいずれかに該当する場合は、即座に作業を停止してエスカレーション:

- [ ] [自己診断チェックリスト](../../00-guides/ESCALATION-CRITERIA-GUIDE.md#提案4-エスカレーション要否の自己診断チェックリスト) でセクションA（セキュリティ）に該当
- [ ] [自己診断チェックリスト](../../00-guides/ESCALATION-CRITERIA-GUIDE.md#提案4-エスカレーション要否の自己診断チェックリスト) でセクションB（アーキテクチャ）に2つ以上該当
- [ ] 組織標準から大きく逸脱する必要がある
- [ ] 複数ドキュメント間で解決不能な矛盾がある

詳細な判定基準・判定マトリクスは [ESCALATION-CRITERIA-GUIDE.md](../../00-guides/ESCALATION-CRITERIA-GUIDE.md) を参照。


---

## 📝 完了記録

### チェック実施情報
- **実施日時**: YYYY-MM-DD HH:MM
- **実施者**: [Your Name]
- **PBI番号**: [PBI-XXXX]
- **総項目数**: 27
- **完了項目数**: _____ / 27

### 特記事項
<!-- 特別な事情や、スキップした項目がある場合は理由を記録 -->

---

## 🔗 関連チェックリスト

- [phase-pre-work-checklist.md](phase-pre-work-checklist.md)

---

## 📚 参考ドキュメント

- [Phase Guide](../../00-guides/phase-guides/phase-2A-pre-implementation-design-guide.md)
- [Master Workflow Guide](../../00-guides/AI-MASTER-WORKFLOW-GUIDE.md)
- [Design Artifacts Management Guide](../../03-development-process/design-artifacts-management-guide.md)
