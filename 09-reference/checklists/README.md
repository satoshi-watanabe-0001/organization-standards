---
title: "Checklists Directory README"
version: "2.0.0"
created_date: "2025-11-05"
last_updated: "2025-11-13"
status: "Active"
---

# Checklists Directory

> 開発プロセスで使用する各種チェックリストの集約ディレクトリ

このディレクトリには、PBI受領からPhase完了まで、各段階で使用するチェックリストが格納されています。

---

## 📁 ディレクトリ構造

```
09-reference/checklists/
├── README.md (このファイル)
│
├── pbi-reception-checklist.md                  # PBI受領時チェック
├── phase-pre-work-checklist.md                 # 各Phase開始前の共通チェック
│
├── phase-0-completion-checklist.md             # Phase 0完了チェック
├── phase-1-completion-checklist.md             # Phase 1完了チェック
├── phase-2.1-completion-checklist.md           # Phase 2.1完了チェック（事前設計）
├── phase-2.2-completion-checklist.md           # Phase 2.2完了チェック（事後設計）
├── phase-3-completion-checklist.md             # Phase 3完了チェック
├── phase-4-completion-checklist.md             # Phase 4完了チェック
├── phase-5-completion-checklist.md             # Phase 5完了チェック
├── phase-6-completion-checklist.md             # Phase 6完了チェック
│
├── ai-documentation-comment-checklist.md       # ドキュメントコメントチェック
├── ci-setup-checklist.md                       # CI/CD設定チェック
└── pbi-type-test-requirements-checklist.md     # PBIタイプ別テスト要件
```

---

## 🎯 チェックリストの分類

### 1. Pre-Phase チェックリスト（開始前）

| チェックリスト | 使用タイミング | 項目数 |
|---|---|---|
| [pbi-reception-checklist.md](pbi-reception-checklist.md) | PBIアサイン直後 | 44項目 |
| [phase-pre-work-checklist.md](phase-pre-work-checklist.md) | 各Phase開始前 | 52項目 |

### 2. Phase完了チェックリスト

| チェックリスト | 使用タイミング | 項目数 |
|---|---|---|
| [phase-0-completion-checklist.md](phase-0-completion-checklist.md) | Phase 0 → Phase 1移行前 | 97項目 |
| [phase-1-completion-checklist.md](phase-1-completion-checklist.md) | Phase 1 → Phase 2/3移行前 | 92項目 |
| [phase-2.1-completion-checklist.md](phase-2.1-completion-checklist.md) | Phase 2.1 → Phase 3移行前 | 27項目 |
| [phase-2.2-completion-checklist.md](phase-2.2-completion-checklist.md) | Phase 2.2 → Phase 5移行前 | 27項目 |
| [phase-3-completion-checklist.md](phase-3-completion-checklist.md) | Phase 3 → Phase 4移行前 | 240項目 |
| [phase-4-completion-checklist.md](phase-4-completion-checklist.md) | Phase 4 → Phase 2.2/5移行前 | 103項目 |
| [phase-5-completion-checklist.md](phase-5-completion-checklist.md) | Phase 5 → Phase 6移行前 | 49項目 |
| [phase-6-completion-checklist.md](phase-6-completion-checklist.md) | Phase 6 → PBI完了前 | 45項目 |

### 3. 専門チェックリスト

| チェックリスト | 使用タイミング | 項目数 |
|---|---|---|
| [ai-documentation-comment-checklist.md](ai-documentation-comment-checklist.md) | Phase 3/4のコメント作成時 | 38項目 |
| [ci-setup-checklist.md](ci-setup-checklist.md) | Phase 1/3のCI/CD設定時 | 101項目 |
| [pbi-type-test-requirements-checklist.md](pbi-type-test-requirements-checklist.md) | Phase 3のテスト実施時 | 40項目 |

---

## 🔄 チェックリストの使用フロー

### 標準的なPBIの場合

```
1. PBI受領
   └─ [pbi-reception-checklist.md]
   
2. Phase 0開始
   ├─ [phase-pre-work-checklist.md] (Phase 0セクション)
   └─ Phase 0実施
   └─ [phase-0-completion-checklist.md]
   
3. Phase 1開始
   ├─ [phase-pre-work-checklist.md] (Phase 1セクション)
   └─ Phase 1実施
      └─ [ci-setup-checklist.md] ※CI/CD設定時
   └─ [phase-1-completion-checklist.md]
   
4. Phase 2.1開始（事前実装設計）
   ├─ [phase-pre-work-checklist.md] (Phase 2.1セクション)
   └─ Phase 2.1実施
   └─ [phase-2.1-completion-checklist.md]
   
5. Phase 3開始
   ├─ [phase-pre-work-checklist.md] (Phase 3セクション)
   └─ Phase 3実施
      ├─ [pbi-type-test-requirements-checklist.md] ※テスト実施時
      └─ [ai-documentation-comment-checklist.md] ※コメント作成時
   └─ [phase-3-completion-checklist.md]
   
6. Phase 4開始
   ├─ [phase-pre-work-checklist.md] (Phase 4セクション)
   └─ Phase 4実施
      └─ [ai-documentation-comment-checklist.md] ※レビュー時
   └─ [phase-4-completion-checklist.md]
   
7. Phase 2.2開始（事後実装設計）
   ├─ [phase-pre-work-checklist.md] (Phase 2.2セクション)
   └─ Phase 2.2実施
   └─ [phase-2.2-completion-checklist.md]
   
8. Phase 5開始
   ├─ [phase-pre-work-checklist.md] (Phase 5セクション)
   └─ Phase 5実施
   └─ [phase-5-completion-checklist.md]
   
9. Phase 6開始
   ├─ [phase-pre-work-checklist.md] (Phase 6セクション)
   └─ Phase 6実施
   └─ [phase-6-completion-checklist.md]
   
10. PBI完了
```

---

## 📊 Phase 2の分離について

Phase 2は、以下の2つのサブフェーズに分かれています：

### Phase 2.1: 事前実装設計（Pre-Implementation Design）
- **タイミング**: Phase 1完了後、Phase 3（実装）開始前
- **目的**: 実装に必要な設計を事前に作成
- **成果物**: ADR、API契約書、制約条件文書、アーキテクチャ概要図
- **チェックリスト**: [phase-2.1-completion-checklist.md](phase-2.1-completion-checklist.md)

### Phase 2.2: 事後実装設計（Post-Implementation Design）
- **タイミング**: Phase 4（レビュー・QA）完了後、Phase 5（デプロイ）開始前
- **目的**: 実装結果を設計に反映し、差分を文書化
- **成果物**: 完全版設計書、更新されたADR、実装差分レポート
- **チェックリスト**: [phase-2.2-completion-checklist.md](phase-2.2-completion-checklist.md)

この分離により、**実装前の設計（Phase 2.1）** と **実装後の設計反映（Phase 2.2）** を明確に区別できます。

---

## 💡 チェックリスト活用のベストプラクティス

### 1. 適切なタイミングで使用
- **開始前チェック**: Phase開始前に必ず実施し、準備が整っているか確認
- **完了チェック**: Phase終了時に必ず実施し、次Phaseへの移行可否を判断

### 2. すべての項目を確認
- チェックリストは網羅的に設計されています
- 項目をスキップせず、順番に確認してください
- 該当しない項目は理由を記録してスキップ可能

### 3. 記録を残す
- 各チェックリストには「完了記録」セクションがあります
- 実施日時、実施者、特記事項を記録してください
- トレーサビリティの確保に重要です

### 4. チームで共有
- チェックリストの結果をチームで共有
- 未完了項目がある場合は、担当者を明確にして対応

### 5. 継続的改善
- チェックリスト項目が不足している場合は追加提案
- 不要な項目がある場合は削除提案
- プロジェクトの経験を次に活かす

---

## 🔗 関連ドキュメント

### プロセスガイド
- [Phase 0 Guide](../../00-guides/phase-guides/phase-0-requirements-planning-guide.md)
- [Phase 1 Guide](../../00-guides/phase-guides/phase-1-project-initialization-guide.md)
- [Phase 2.1 Guide](../../00-guides/phase-guides/phase-2.1-pre-implementation-design-guide.md)
- [Phase 2.2 Guide](../../00-guides/phase-guides/phase-2.2-post-implementation-design-guide.md)
- [Phase 3 Guide](../../00-guides/phase-guides/phase-3-implementation-guide.md)
- [Phase 4 Guide](../../00-guides/phase-guides/phase-4-review-qa-guide.md)
- [Phase 5 Guide](../../00-guides/phase-guides/phase-5-deployment-guide.md)
- [Phase 6 Guide](../../00-guides/phase-guides/phase-6-operations-maintenance-guide.md)

### ワークフローガイド
- [Master Workflow Guide](../../00-guides/AI-MASTER-WORKFLOW-GUIDE.md)
- [Document Usage Manual](../../00-guides/DOCUMENT-USAGE-MANUAL.md)

### 開発プロセス
- [Testing Standards](../../03-development-process/testing-standards.md)
- [Code Review Standards](../../03-development-process/code-review-standards.md)
- [CI/CD Pipeline](../../03-development-process/ci-cd-pipeline.md)

---

## 📝 変更履歴

### v2.0.0 (2025-11-13)
- チェックリストを14ファイルに再構築
- Phase 2.1/2.2の明確な分離
- フェーズ完了チェックリスト8個を新規作成
- 既存チェックリストを統合・移動
- READMEを全面更新

### v1.0.0 (2025-11-05)
- 初版作成
- 基本的なREADME構造を定義

---

## 📧 フィードバック・質問

チェックリストに関する質問や改善提案は、以下の方法でお寄せください：
- JIRAでIssueを作成
- エンジニアリングリーダーシップチームに連絡
- チーム定例会で議題として提起

---

**最終更新**: 2025-11-13  
**管理者**: Engineering Leadership Team
