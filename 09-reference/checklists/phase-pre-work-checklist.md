---
title: "Phase開始前チェックリスト"
version: "2.0.0"
created_date: "2025-11-13"
last_updated: "2025-11-13"
status: "Active"
phase: "All Phases - Pre-Work"
checklist_type: "Pre-Phase"
document_type: "Checklist"
---

# Phase開始前チェックリスト

> 各Phase開始前に、必要な準備が整っていることを確認

**目的**: 各Phaseを開始する前に、必要な準備が整っていることを確認  
**使用タイミング**: Phase 0-6のいずれかを開始する直前  
**参照元**: [AI-PRE-WORK-CHECKLIST.md](../../00-guides/AI-PRE-WORK-CHECKLIST.md)

---

## ✅ 使い方

### このチェックリストの位置づけ

このチェックリストは、各Phaseの**開始準備**を確認するためのものです。

| チェックリスト | 役割 | 実施タイミング |
|--------------|------|---------------|
| **Phase完了チェックリスト** | Phase Nの作業が完了したことを確認 | Phase N作業終了時 |
| **Phase開始前チェックリスト**（このドキュメント） | Phase N+1を開始する準備が整っていることを確認 | Phase N+1作業開始直前 |

### チェック手順

**重要**: 必ず以下の順序で実施してください

1. **前Phaseの完了チェックリストを完了** ← まずこちらを優先
2. このチェックリストで次Phaseの開始準備を確認
3. すべて☑️になったら、次Phaseの作業を開始

### 未完了項目がある場合

- **前Phase完了が未完了**: 前Phaseに戻って完了させる
- **環境・権限が未準備**: 必要なリソースを準備
- **時間が確保できない**: スケジュール調整、または開始を保留

---

## 📋 全Phase共通の開始前確認

### 前Phase完了の確認

> **最優先**: 前Phaseの完了チェックリストを**先に**完了させてください

- [ ] 前Phase完了チェックリストがすべて☑️
- [ ] 前Phaseのレビュー・承認が完了
- [ ] 前Phaseで発生した課題がすべて解決

**前Phase完了チェックリストへのリンク**:
- [PBI受領時チェックリスト](pbi-reception-checklist.md) - Phase 0開始前
- [Phase 0完了チェックリスト](phase-0-completion-checklist.md) - Phase 1開始前
- [Phase 1完了チェックリスト](phase-1-completion-checklist.md) - Phase 2A開始前
- [Phase 2A完了チェックリスト](phase-2A-completion-checklist.md) - Phase 3開始前
- [Phase 3完了チェックリスト](phase-3-completion-checklist.md) - Phase 4開始前
- [Phase 4完了チェックリスト](phase-4-completion-checklist.md) - Phase 2B開始前
- [Phase 2B完了チェックリスト](phase-2B-completion-checklist.md) - Phase 5開始前
- [Phase 5完了チェックリスト](phase-5-completion-checklist.md) - Phase 6開始前

### 環境・アクセス権限

- [ ] 開発環境にアクセスできる
- [ ] リポジトリにアクセス権限がある
- [ ] 必要なツールがインストールされている
- [ ] ドキュメント管理システムにアクセスできる
- [ ] JIRAにアクセスできる

### リソース・時間

- [ ] Phase作業に必要な時間が確保できている
- [ ] チームメンバーと連絡が取れる
- [ ] ステークホルダーに状況報告できる
- [ ] 質問・相談先が明確である
- [ ] エスカレーション経路が分かっている

---

## 🎯 Phase別開始前確認

### Phase 0 開始前（要件分析・計画）

#### PBI受領の確認

- [ ] [PBI受領時チェックリスト](pbi-reception-checklist.md) が完了
- [ ] PBIの必須情報がすべて揃っている
- [ ] 受入条件（Acceptance Criteria）が明確である

#### Phase 0固有の準備

- [ ] 要件整理に必要な時間が確保できている（**1-2日目安**）
- [ ] ステークホルダーとの連絡手段が確立している
- [ ] 要件確認のための質問リストを準備できている

---

### Phase 1 開始前（プロジェクト初期化）

#### Phase 0完了の確認

- [ ] [Phase 0完了チェックリスト](phase-0-completion-checklist.md) がすべて☑️

> Phase 0の成果物（要件分析書、タスク分解リスト等）が完成していることを確認

#### Phase 1固有の準備

- [ ] リポジトリ作成権限がある
- [ ] プロジェクト初期化に必要な時間が確保できている（**1-2日目安**）
- [ ] 技術スタック候補が絞られている（または決定済み）

---

### Phase 2A 開始前（実装前設計）

#### Phase 1完了の確認

- [ ] [Phase 1完了チェックリスト](phase-1-completion-checklist.md) がすべて☑️

> プロジェクト初期化（リポジトリ、CI/CD、技術スタック決定）が完了していることを確認

#### Phase 2A固有の準備

- [ ] 設計に必要な技術情報が揃っている
- [ ] アーキテクチャ検討に必要な時間が確保できている（**0.5-3日目安**）
- [ ] 設計ドキュメントのテンプレート・ツールが利用可能

---

### Phase 3 開始前（実装）

#### Phase 2A完了の確認

- [ ] [Phase 2A完了チェックリスト](phase-2A-completion-checklist.md) がすべて☑️

> 実装前設計（軽量設計書）が完成し、レビュー承認されていることを確認

#### Phase 3固有の準備

- [ ] 実装環境が準備されている
- [ ] テスト環境が利用可能である
- [ ] 実装に必要な時間が確保できている（**3-5日目安**）
- [ ] ローカル開発環境が正常に動作する

---

### Phase 4 開始前（レビュー・QA）

#### Phase 3完了の確認

- [ ] [Phase 3完了チェックリスト](phase-3-completion-checklist.md) がすべて☑️

> 実装完了（すべてのテスト合格、コード品質基準達成）を確認

#### Phase 4固有の準備

- [ ] レビュアーが決定している
- [ ] レビューに必要な時間が確保できている（**1-2日目安**）
- [ ] レビュー環境（プレビューURL等）が利用可能

---

### Phase 2B 開始前（実装後設計）

#### Phase 4完了の確認

- [ ] [Phase 4完了チェックリスト](phase-4-completion-checklist.md) がすべて☑️

> レビュー・QAが完了し、承認されていることを確認

#### Phase 2B固有の準備

- [ ] 実装中の設計変更が整理されている
- [ ] 設計書最終化に必要な時間が確保できている（**1-3日目安**）
- [ ] 実装コードとの差分が明確になっている

---

### Phase 5 開始前（デプロイ）

#### Phase 2B完了の確認（該当する場合）

- [ ] [Phase 2B完了チェックリスト](phase-2B-completion-checklist.md) がすべて☑️

> 実装後設計（詳細設計書）が最終化されていることを確認（該当する場合）

#### Phase 5固有の準備

- [ ] デプロイ権限が付与されている
- [ ] 本番環境の状態が確認できている
- [ ] ロールバック手順が準備されている
- [ ] デプロイ作業に必要な時間が確保できている
- [ ] 関係者（ステークホルダー、運用チーム）に事前通知済み

---

### Phase 6 開始前（運用・保守）

#### Phase 5完了の確認

- [ ] [Phase 5完了チェックリスト](phase-5-completion-checklist.md) がすべて☑️

> デプロイが成功し、本番環境で正常動作が確認されていることを確認

#### Phase 6固有の準備

- [ ] 監視ツールが設定されている
- [ ] 運用ドキュメントが準備されている
- [ ] 運用に必要な時間が確保できている
- [ ] インシデント対応手順が明確である
- [ ] 運用チームへの引き継ぎが完了している（該当する場合）

---

## 🔧 技術的準備確認（Phase 1-3向け）

### ローカル環境

- [ ] 最新のコードをpullしている
- [ ] 依存ライブラリがインストールされている
- [ ] ローカルでビルドが通る
- [ ] ローカルでテストが実行できる

### リモート環境

- [ ] CI/CDパイプラインが正常に動作している
- [ ] 開発環境が利用可能である
- [ ] テスト環境が利用可能である

> **Phase 4以降の技術的確認**: 各Phase完了チェックリストを参照してください

---

## 📝 開始記録

### チェック実施情報
- **実施日時**: YYYY-MM-DD HH:MM
- **実施者**: [Your Name]
- **PBI番号**: [PBI-XXXX]
- **開始するPhase**: Phase X
- **判定**: 開始可 / 開始保留

### 前Phase完了確認
- **前Phase**: Phase X
- **完了チェックリスト**: 完了 / 未完了
- **レビュー承認**: 承認済み / 未承認
- **課題**: すべて解決 / 未解決あり

### Phase固有準備状況
- **環境準備**: 完了 / 未完了
- **権限付与**: 完了 / 未完了
- **時間確保**: 確保済み / 要調整

### 特記事項
<!-- 気づいた点、課題、準備に時間がかかった項目を記録 -->

---

## 🔗 関連チェックリスト

### Phase完了チェックリスト（優先）

**重要**: これらを**先に**完了させてから、Phase開始前チェックリストを実施

- [PBI受領時チェックリスト](pbi-reception-checklist.md) - Phase 0開始前に完了
- [Phase 0 完了チェックリスト](phase-0-completion-checklist.md) - Phase 1開始前に完了
- [Phase 1 完了チェックリスト](phase-1-completion-checklist.md) - Phase 2A開始前に完了
- [Phase 2A 完了チェックリスト](phase-2A-completion-checklist.md) - Phase 3開始前に完了
- [Phase 3 完了チェックリスト](phase-3-completion-checklist.md) - Phase 4開始前に完了
- [Phase 4 完了チェックリスト](phase-4-completion-checklist.md) - Phase 2B開始前に完了
- [Phase 2B 完了チェックリスト](phase-2B-completion-checklist.md) - Phase 5開始前に完了
- [Phase 5 完了チェックリスト](phase-5-completion-checklist.md) - Phase 6開始前に完了
- [Phase 6 完了チェックリスト](phase-6-completion-checklist.md) - PBI完了時に完了

---

## 📚 参考ドキュメント

- [AI-PRE-WORK-CHECKLIST](../../00-guides/AI-PRE-WORK-CHECKLIST.md) - AI事前作業チェックリスト
- [AI-MASTER-WORKFLOW-GUIDE](../../00-guides/AI-MASTER-WORKFLOW-GUIDE.md) - 開発プロセス全体ガイド
- [ESCALATION-CRITERIA-GUIDE](../../00-guides/ESCALATION-CRITERIA-GUIDE.md) - エスカレーション基準ガイド

---

## 🆕 変更履歴

- **2025-11-13 v2.0.0**: 役割明確化、Phase別セクション簡略化、重複項目の参照化
  - 「Phase完了チェックリストを先に完了」を強調
  - Phase別セクションを「前Phase完了確認」+「Phase固有準備」に整理
  - 技術的確認をPhase 1-3に限定、Phase 4以降は完了チェックリストに委ねる
- **2025-11-13 v1.0.0**: 初版作成
