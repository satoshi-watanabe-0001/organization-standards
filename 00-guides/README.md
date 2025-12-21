# 📚 Devin組織標準ガイド集

開発プロジェクトを成功に導くための総合ガイドコレクションです。AI活用からプロジェクト管理、技術実装まで、あらゆるフェーズをカバーしています。

## 🗂️ フォルダ構成

### [01-getting-started](./01-getting-started/) 🚀
**はじめに・初期セットアップ**

プロジェクト開始時に必要な初期設定とガイドラインをまとめています。

- `DEVIN-INITIAL-SETUP-GUIDE.md` - Devinの初期セットアップ手順
- `DOCUMENT-USAGE-MANUAL.md` - ドキュメント利用マニュアル
- `ESCALATION-CRITERIA-GUIDE.md` - エスカレーション基準ガイド

---

### [02-ai-guides](./02-ai-guides/) 🤖
**AI活用ガイド**

AI（Devin等）を効果的に活用するためのワークフロー、チェックリスト、プロンプト集です。

- `AI-MASTER-WORKFLOW-GUIDE.md` - AIマスターワークフローガイド
- `AI-PRE-WORK-CHECKLIST.md` - AI作業前チェックリスト
- `AI-DELIVERABLE-REFERENCE-GUIDE.md` - AI成果物リファレンスガイド
- その他、プロンプトテンプレート、使い方ガイド多数

---

### [03-pbi-management](./03-pbi-management/) 📋
**PBI（Product Backlog Item）マネジメント**

バックログアイテムの作成、優先順位付け、管理に関する包括的なガイド群です。

#### サブフォルダ構成
- **`core/`** - コアガイド（作成ベストプラクティス、優先順位付け、タイプ判定）
- **`quick-reference/`** - クイックリファレンス（簡易版）
- **`templates-prompts/`** - テンプレート・プロンプト集
- **`guides/`** - 各種ガイド（分割、JIRA管理、改善アクション）

詳細は [`PBI-MASTER-INDEX.md`](./03-pbi-management/PBI-MASTER-INDEX.md) を参照

---

### [04-ci-cd](./04-ci-cd/) ⚙️
**CI/CDセットアップ**

継続的インテグレーション・デプロイメントの設定ガイドとチェックリストです。

- `CI-SETUP-CHECKLIST.md` - CI セットアップチェックリスト（詳細版）
- `CI-SETUP-QUICK-CHECKLIST.md` - CI セットアップクイックチェックリスト
- `CI-SETUP-LANGUAGE-MATRIX.md` - 言語別CIセットアップマトリックス
- `ci-setup-sql-quality-gate.md` - SQL品質ゲート設定

---

### [05-testing](./05-testing/) 🧪
**テスト関連**

テスト戦略とガイドラインをまとめています。

- `MULTI-REPOSITORY-TESTING-GUIDELINES.md` - マルチリポジトリテストガイドライン

---

### [06-phase-guides](./06-phase-guides/) 📅
**フェーズ別ガイド**

プロジェクトの各フェーズ（要件定義〜運用保守）における作業ガイドです。

- `phase-0-requirements-planning-guide.md` - Phase 0: 要件定義・計画
- `phase-1-project-initialization-guide.md` - Phase 1: プロジェクト初期化
- `phase-2-design-guide.md` - Phase 2: 設計
- `phase-2A-pre-implementation-design-guide.md` - Phase 2A: 実装前設計
- `phase-2B-post-implementation-design-guide.md` - Phase 2B: 実装後設計
- `phase-3-implementation-guide.md` - Phase 3: 実装
- `phase-4-review-qa-guide.md` - Phase 4: レビュー・QA
- `phase-5-deployment-guide.md` - Phase 5: デプロイメント
- `phase-6-operations-maintenance-guide.md` - Phase 6: 運用・保守
- `PHASE-CHECKLIST-TEMPLATE.md` - フェーズチェックリストテンプレート

---

### [99-maintenance](./99-maintenance/) 🔧
**メンテナンス**

ガイド自体のメンテナンスと更新に関するドキュメントです。

- `SUBDIRECTORY-UPDATE-CHECKLIST.md` - サブディレクトリ更新チェックリスト

---

## 🎯 使い方

### 🆕 プロジェクトを始める方
1. **[01-getting-started](./01-getting-started/)** で初期設定を完了
2. **[02-ai-guides](./02-ai-guides/)** でAI活用方法を理解
3. **[06-phase-guides](./06-phase-guides/)** で現在のフェーズに該当するガイドを参照

### 📋 PBI管理を学びたい方
- **[03-pbi-management](./03-pbi-management/)** フォルダ内の `PBI-MASTER-INDEX.md` から開始

### ⚙️ CI/CDを設定したい方
- **[04-ci-cd](./04-ci-cd/)** の `CI-SETUP-QUICK-CHECKLIST.md` で概要を把握
- その後、詳細版チェックリストで実装

### 🔍 特定のトピックを探したい方
- 各フォルダのREADME.mdを参照
- または、このインデックスから関連ファイルを検索

---

## 📝 ドキュメント管理方針

- ✅ **人間目線の構成** - カテゴリ別・段階的に整理
- ✅ **階層は3階層まで** - 深すぎず探しやすく
- ✅ **Quick Referenceを分離** - 詳細版と簡易版を区別
- ✅ **各フォルダにREADME** - フォルダ内のナビゲーションを提供

---

## 🔄 最終更新

- **構成整理日**: 2025-11-18
- **整理内容**: フォルダ直下のファイル乱立を解消し、カテゴリ別フォルダ構成に再編成

---

**ご質問・改善提案は**: メンテナンス担当者まで
