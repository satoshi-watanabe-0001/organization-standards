---
title: "Phase 2B 完了チェックリスト（事後実装設計）"
version: "1.0.0"
created_date: "2025-11-13"
last_updated: "2025-11-13"
status: "Active"
phase: "Phase 2B - Post-Implementation Design"
checklist_type: "Phase Completion"
document_type: "Checklist"
---

# Phase 2B 完了チェックリスト（事後実装設計）

> 実装結果の設計への反映完了確認

**目的**: Phase 2Bの設計更新が完了し、実装と設計の差分が文書化されていることを確認  
**使用タイミング**: Phase 5（デプロイ）への移行前  
**参照元**: [phase-2B-post-implementation-design-guide.md](../../00-guides/phase-guides/phase-2B-post-implementation-design-guide.md)

---

## ✅ 使い方

このチェックリストは、Phase 2B（事後実装設計）のすべてのタスクが完了し、次のフェーズに移行する前に使用します。

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

- [ ] ADRが作成されている
- [ ] API契約書が合意されている
- [ ] 制約条件が明確である
- [ ] 実装が完了している (後実施の場合)
- [ ] 主要機能が実装済み (並行実施の場合)
- [ ] コードレビューが進行中
- [ ] テストが実施されている
- [ ] コードが安定している
- [ ] 大きな変更の予定がない
- [ ] 実装中の発見事項が整理されている

## 完了基準

### 必須チェックリスト

- [ ] 設計書が完成している
- [ ] 完全版API仕様書が完成している
- [ ] アーキテクチャ図が完成している
- [ ] データモデル文書が完成している
- [ ] 実装中の追加決定事項がADRに記録されている
- [ ] 実装と完全に一致している
- [ ] 図が明確で理解しやすい
- [ ] 新メンバーがこのドキュメントで理解できる
- [ ] 運用チームが必要な情報を得られる
- [ ] 技術的負債が明記されている
- [ ] セルフレビューが完了している
- [ ] ピアレビューが完了している
- [ ] 実装チームのレビューが完了している
- [ ] テックリードの承認を得ている
- [ ] 保守・拡張時の参考になる
- [ ] トラブルシューティングに役立つ
- [ ] 次のイテレーションの基礎になる

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

- [Phase Guide](../../00-guides/phase-guides/phase-2B-post-implementation-design-guide.md)
- [Master Workflow Guide](../../00-guides/AI-MASTER-WORKFLOW-GUIDE.md)
- [Design Artifacts Management Guide](../../03-development-process/design-artifacts-management-guide.md)
