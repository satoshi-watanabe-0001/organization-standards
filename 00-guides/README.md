---
title: "統合ガイド - 00-guides"
version: "2.2.0"
created_date: "2025-11-05"
last_updated: "2025-11-14 (AI-DOCUMENTATION-COMMENT-CHECKLIST追加、code-templates追加)"
status: "Active"
owner: "Engineering Leadership Team"
---

# 00-guides - 統合ガイド

> AI開発ワークフローとドキュメント体系の統合ガイド集

**ディレクトリパス**: `/devin-organization-standards/00-guides/`

---

## 📖 概要

このディレクトリは、自律型AIと人間の開発者が効率的に協働するための**統合ガイド**を集約しています。

### 🎯 目的

1. **ワークフローの統合**: PBIから始まる全開発フェーズの統合ワークフローを提供
2. **ドキュメント参照の最適化**: 「いつ」「どのドキュメントを」「どう使うか」を明確化
3. **参照漏れの防止**: AIエージェントが必要なドキュメントを確実に参照できるようナビゲーション
4. **人間のレビュー効率化**: レビューアーが必要な標準を迅速に参照できる環境を提供

### 🌟 特徴

- **統合性**: 全開発フェーズを網羅した統合ワークフロー
- **軽量性**: 各ガイドは15-30KBの軽量ナビゲーション型
- **実行型**: 「What（何を守るか）」ではなく「When/How/Which（いつ・どう・どれを）」に焦点
- **柔軟性**: フェーズスキップ可能、プロジェクト規模に応じた適用

---

## 📁 ディレクトリ構成

```
00-guides/
├── README.md (このファイル)
│
├── 【マスターガイド】
├── AI-MASTER-WORKFLOW-GUIDE.md (40KB)
├── DOCUMENT-USAGE-MANUAL.md (29KB)
│
├── 【品質・コメント標準】⭐NEW v2.2.0
├── AI-DOCUMENTATION-COMMENT-CHECKLIST.md (7.3KB) - ドキュメントコメント品質チェックリスト
│
├── 【開発フェーズ別ガイド】
├── phase-guides/
│   ├── README.md
│   ├── phase-0-requirements-planning-guide.md (15KB)
│   ├── phase-1-project-initialization-guide.md (15KB)
│   ├── phase-2.1-pre-implementation-design-guide.md (16KB) ⭐NEW
│   ├── phase-2.2-post-implementation-design-guide.md (19KB) ⭐NEW
│   ├── (phase-2-design-guide.md - 旧版、参照非推奨)
│   ├── phase-3-implementation-guide.md (28KB)
│   ├── phase-4-review-qa-guide.md (39KB)
│   ├── phase-5-deployment-guide.md (14KB)
│   └── phase-6-operations-maintenance-guide.md (15KB)
│
└── 【AI向けプロンプト・ガイド】
    └── ai-guides/
        ├── README.md
        ├── AI-CODING-INSTRUCTIONS.md (15KB)
        ├── AI-PROMPT-TEMPLATES.md (13KB)
        ├── AI-PROMPTS.md (23KB)
        ├── AI-QUICK-REFERENCE.md (7KB)
        ├── AI-USAGE-GUIDE.md (26KB)
        └── AI-WORKFLOWS.md (18KB)

├── 【統合テスト・マルチリポジトリガイド】
├── (統合済み) Phase 4統合テスト追加完了
├── (統合済み) PBIタイプ別マトリックス追加完了
├── MULTI-REPOSITORY-TESTING-GUIDELINES.md (19.5KB)
│
├── 【SQLマイグレーション標準統合】
├── phase-3-sql-migration-addition.md (18.6KB) - 参考用
├── ci-setup-sql-quality-gate.md (25.8KB) - 参考用
│
合計: 2マスターガイド + 7フェーズガイド + 7AIガイド + 1品質チェックリスト + 1統合テストガイド + 2SQLガイド(参考) = 20ファイル (約397KB)
```

**注**: 08-templates/code-templates/ にコードテンプレートが別途存在（詳細は[08-templates/README.md](../08-templates/README.md)参照）

---
## 📚 主要ドキュメント詳細

### 🎯 マスターガイド（3ファイル）

#### 1. AI-MASTER-WORKFLOW-GUIDE.md (40KB)
**対象**: 🤖 AIエージェント、👨‍💻 開発者、📊 プロジェクトマネージャー

**内容**:
- PBIから始まる全フェーズ統合ワークフロー
- Phase 0（要件分析）〜 Phase 6（運用保守）の完全ガイド
- フェーズスキップ判断基準
- エスカレーション基準
- 各フェーズの詳細手順と参照ドキュメント

**いつ使う**:
- ✅ 新規PBIを受け取った時（必ず最初に読む）
- ✅ 現在のフェーズから次フェーズへ移行する時
- ✅ フェーズスキップの判断が必要な時
- ✅ エスカレーションが必要か判断する時

**重要度**: ⭐⭐⭐⭐⭐ **最重要** - AIエージェントの起点となるガイド

**Quick Start**: 
```
1. PBIを受け取る
2. このガイドのPhase 0セクションを開く
3. ステップバイステップで実行
4. 各Phaseで phase-guides/ の該当ガイドを参照
```

---

#### 2. DOCUMENT-USAGE-MANUAL.md (29KB)
**対象**: 🤖 AIエージェント、👨‍💻 全開発者、👥 新規メンバー

**内容**:
- ドキュメント体系全体の利用マニュアル
- シチュエーション別ドキュメント参照方法
- ドキュメントの役割分担（What/Why vs When/How/Which）
- フェーズ別必須ドキュメント一覧
- トラブルシューティング（どのドキュメントを見るべきか迷った時）

**いつ使う**:
- ✅ 初めてこのドキュメント体系を使う時（オンボーディング）
- ✅ 特定のシチュエーションでどのドキュメントを見るべきか分からない時
- ✅ ドキュメント体系全体を理解したい時
- ✅ 新規メンバーへの説明資料として

**重要度**: ⭐⭐⭐⭐⭐ **最重要** - ドキュメント体系のナビゲーター

**Quick Start**: 
```
1. 「シチュエーション別ガイド」セクションを開く
2. 自分の状況に合致するシチュエーションを探す
3. 推奨ドキュメントのリストを確認
4. リンクをクリックして該当ドキュメントへ
```

---

#### 3. AI-DELIVERABLE-REFERENCE-GUIDE.md (23KB) 🆕 NEW
**対象**: 🤖 AIエージェント、👨‍💻 開発者

**内容**:
- Phase別成果物参照ガイド（Phase 0-6の8フェーズ）
- Phase-Deliverable Matrix（フェーズ×成果物対応表）
- PBIタイプ別参照ガイド（新規機能、バグ修正等）
- Phase 2.1（軽量版）vs Phase 2.2（完全版）の区別
- 参照タイミング決定フローチャート

**いつ使う**:
- ✅ 各Phase開始時に「AI活用システム開発ドキュメント」を参照する前
- ✅ 成果物作成時にどのテンプレートを参照すべきか迷った時
- ✅ Phase 2.1（実装前）とPhase 2.2（実装後）の違いを確認したい時
- ✅ PBIタイプに応じたフェーズスキップ判断が必要な時

**重要度**: ⭐⭐⭐⭐⭐ **最重要** - 成果物作成のナビゲーター

**Quick Start**: 
```
1. 現在のPhase番号を確認（Phase 0-6）
2. 該当Phaseのセクションを開く
3. 参照すべき成果物リストを確認
4. AI活用システム開発ドキュメント配下のテンプレートを参照
```

**関連ガイド**:
- → AI-MASTER-WORKFLOW-GUIDE.md（全体プロセス）
- → AI-PRE-WORK-CHECKLIST.md（作業前チェック）
- → AI活用システム開発ドキュメント/README_成果物重要度定義.md（成果物一覧）

---

### 📋 開発フェーズ別ガイド（8ファイル ⭐、約150KB）

**ディレクトリ**: `phase-guides/`  
**詳細**: [phase-guides/README.md](./phase-guides/README.md) を参照

#### ⚠️ Phase 2 の改訂について
Phase 2（設計）は Phase 2.1（実装前設計）と Phase 2.2（実装後設計）に分割されました：

- **phase-2.1-pre-implementation-design-guide.md** (16KB) ⭐NEW
  - 実行タイミング: Phase 1 の後、Phase 3 の前
  - 期間: 1-2日
  - 目的: 実装の方向性を定める最小限の設計
  - 成果物: ADR、API契約書、制約条件文書
  - 📂 成果物管理: [design-artifacts-management-guide.md v2.0.0](../../03-development-process/design-artifacts-management-guide.md)

- **phase-2.2-post-implementation-design-guide.md** (19KB) ⭐NEW
  - 実行タイミング: Phase 4 の後、Phase 5 の前（または並行）
  - 期間: 2-3日
  - 目的: 実装内容の詳細な文書化
  - 成果物: 設計書、完全版API仕様書、アーキテクチャ図、データモデル文書
  - 📂 成果物管理: [design-artifacts-management-guide.md v2.0.0](../../03-development-process/design-artifacts-management-guide.md)

- **phase-2-design-guide.md** (21KB) ⚠️ 旧版
  - 参照非推奨。phase-2.1 と phase-2.2 を参照してください。

詳細は `/03-development-process/revised-development-process-overview.md` を参照。


各フェーズガイドの概要:

| Phase | ガイド名 | サイズ | 概要 |
|-------|---------|--------|------|
| **Phase 0** | 要件分析・企画 | 15KB | PBI分析、要件整理、技術選定、リスク分析 |
| **Phase 1** | プロジェクト初期化 | 15KB | 環境確認、リポジトリ初期化、プロジェクト構造作成 |
| **Phase 2** | 設計 | 18KB | アーキテクチャ、データモデル、API、セキュリティ設計 |
| **Phase 3** | 実装 | 28KB | タスク粒度別・タイプ別・レイヤー別実装ガイド |
| **Phase 4** | レビュー・QA | 39KB | コードレビュー、テスト戦略、各種テスト実施、ドキュメントコメントレビュー |
| **Phase 5** | デプロイメント | 14KB | デプロイ前チェック、実行、検証、リリースノート |
| **Phase 6** | 運用・保守 | 15KB | モニタリング、ログ管理、インシデント対応 |

**共通構造**:
```
1. フェーズ概要
2. 開始条件（このフェーズをいつ開始するか）
3. ステップバイステップのワークフロー
4. 各ステップで参照すべきドキュメント（優先度付き）
5. 成果物チェックリスト
6. 完了基準
7. 次フェーズへの判断基準
```

**いつ使う**:
- ✅ AI-MASTER-WORKFLOW-GUIDEから誘導された時
- ✅ 特定のフェーズの詳細手順を知りたい時
- ✅ フェーズの成果物を確認したい時

**重要度**: ⭐⭐⭐⭐⭐ **最重要** - 実行時の詳細ガイド

---

### 🤖 AI向けプロンプト・ガイド（7ファイル、117KB）

**ディレクトリ**: `ai-guides/`  
**詳細**: [ai-guides/README.md](./ai-guides/README.md) を参照

AIエージェントの実行精度を高めるためのプロンプトとガイドライン集:

| ファイル名 | サイズ | 用途 |
|-----------|--------|------|
| AI-CODING-INSTRUCTIONS.md | 15KB | コーディング時の詳細指示 |
| AI-PROMPT-TEMPLATES.md | 13KB | 再利用可能なプロンプトテンプレート |
| AI-PROMPTS.md | 23KB | 各種タスク向けプロンプト集 |
| AI-QUICK-REFERENCE.md | 7KB | クイックリファレンス |
| AI-USAGE-GUIDE.md | 26KB | AI利用ガイド（人間向け） |
| AI-WORKFLOWS.md | 18KB | ワークフロー詳細 |

**いつ使う**:
- ✅ AI実行の精度を高めたい時
- ✅ 特定のタスクに最適なプロンプトを探す時
- ✅ AIの振る舞いをカスタマイズしたい時

**重要度**: ⭐⭐⭐⭐ 高 - AI実行の品質向上に寄与

---

## 🎯 対象者別ガイド

### 🤖 AIエージェント

**推奨読書順序**:
1. **AI-MASTER-WORKFLOW-GUIDE.md** (40KB) - まずこれを読む
2. **DOCUMENT-USAGE-MANUAL.md** (29KB) - ドキュメント参照方法を理解
3. **phase-guides/** - 各フェーズの詳細実行手順
4. **ai-guides/** - 実行精度向上のためのプロンプト集

**推定時間**: 初回30分、以降は必要に応じて参照

---

### 👨‍💻 新規開発者

**推奨読書順序**:
1. **DOCUMENT-USAGE-MANUAL.md** (29KB) - ドキュメント体系全体を理解
2. **AI-MASTER-WORKFLOW-GUIDE.md** (40KB) - ワークフロー全体を把握
3. **phase-guides/phase-0-requirements-planning-guide.md** - 要件分析から理解

**推定時間**: 1-2時間

**目標**: 
- ドキュメント体系の全体像を理解
- どこに何が書いてあるか把握
- 実際のタスクで迷わず参照できる

---

### 🏗️ アーキテクト / シニアエンジニア

2. **phase-guides/phase-2.1-pre-implementation-design-guide.md** (16KB) ⭐NEW - 実装前設計
3. **phase-guides/phase-2.2-post-implementation-design-guide.md** (19KB) ⭐NEW - 実装後設計
4. **phase-guides/phase-4-review-qa-guide.md** (39KB) - レビュー基準、ドキュメントコメントレビュー
3. **phase-guides/phase-4-review-qa-guide.md** (17KB) - レビュー基準

**推定時間**: 30-60分

**目標**: 
- 設計とレビューのポイントを把握
- AIが参照すべきドキュメントを理解
- レビュー時の確認項目を明確化

---

### 📊 プロジェクトマネージャー

**推奨読書順序**:
1. **AI-MASTER-WORKFLOW-GUIDE.md** (40KB) - 全フェーズの流れを把握
2. **DOCUMENT-USAGE-MANUAL.md** (29KB) - ドキュメント体系を理解
3. **phase-guides/** - 各フェーズの成果物と完了基準を確認

**推定時間**: 1-2時間

**目標**: 
- プロジェクト進行の判断基準を理解
- 各フェーズの完了基準を把握
- エスカレーション基準を理解

---

## 💡 使い方

### 🚀 クイックスタート（5分）

#### AIエージェントとして開発を開始
```
1. PBIを受け取る
2. AI-MASTER-WORKFLOW-GUIDE.md を開く
3. Phase 0 セクションから開始
4. 各ステップで phase-guides/ の該当ガイドを参照
```

#### 人間の開発者として参照
```
1. DOCUMENT-USAGE-MANUAL.md を開く
2. 「シチュエーション別ガイド」で自分の状況を探す
3. 推奨ドキュメントを確認
4. 必要に応じて phase-guides/ を参照
```

---

### 📖 詳細な使用手順

#### シナリオ1: 新規PBIを受け取った

**AIエージェント**:
```
Step 1: AI-MASTER-WORKFLOW-GUIDE.md を開く
Step 2: 「Phase 0: 要件分析・企画」セクションへ
Step 3: phase-guides/phase-0-requirements-planning-guide.md で詳細確認
Step 4: PBIタイプを判定（Feature/Bug/Enhancement等）
Step 5: ガイドに従ってStep 0.1〜0.7を実行
Step 6: Phase 0完了基準を満たしたら Phase 1へ
```

**人間のレビューアー**:
```
Step 1: AIが参照したドキュメントを確認
Step 2: phase-guides/phase-0-requirements-planning-guide.md の完了基準を確認
Step 3: AIの成果物が基準を満たしているか検証
Step 4: 必要に応じて既存標準（01-10/）を参照
```

---

#### シナリオ2: 特定のドキュメントを探している

**全員**:
```
Step 1: DOCUMENT-USAGE-MANUAL.md を開く
Step 2: 「目次」または「シチュエーション別ガイド」セクションへ
Step 3: キーワードで検索（Ctrl+F / Cmd+F）
Step 4: 該当するシチュエーションを見つける
Step 5: 推奨ドキュメントのリンクをクリック
```

---

#### シナリオ3: フェーズをスキップすべきか判断

**AIエージェント / プロジェクトマネージャー**:
```
Step 1: AI-MASTER-WORKFLOW-GUIDE.md の「フェーズスキップ判断基準」セクション
Step 2: 現在のPBIタイプ、規模、複雑度を確認
Step 3: スキップ条件に合致するか判定
Step 4: スキップする場合、次のフェーズへ
Step 5: スキップしない場合、該当フェーズの phase-guides/ を参照
```

---

## 🔗 関連ドキュメント

### 既存標準ドキュメント（01-10/）

このディレクトリ（00-guides/）は、既存の標準ドキュメント（01-10/）への「ナビゲーター」として機能します。

| カテゴリ | ディレクトリ | 関係 |
|---------|------------|------|
| コーディング標準 | `../01-coding-standards/` | Phase 3実装時に参照 |
| アーキテクチャ標準 | `../02-architecture-standards/` | Phase 2設計時に参照 |
| 開発プロセス | `../03-development-process/` | 全Phaseで参照 |
| 設計成果物管理 | `../03-development-process/design-artifacts-management-guide.md` | Phase 2成果物の格納・管理 ⭐v2.0.0 |
| API仕様管理 | `../03-development-process/api-specification-management-guide.md` | API仕様（Swagger）の管理・統合 ⭐NEW |
| 品質標準 | `../04-quality-standards/` | Phase 4レビュー・QA時に参照 |
| 技術スタック | `../05-technology-stack/` | Phase 0, 1で参照 |
| ツールと環境 | `../06-tools-and-environment/` | Phase 1初期化時に参照 |
| セキュリティ | `../07-security-compliance/` | Phase 2設計、Phase 4テスト時に参照 |
| テンプレート | `../08-templates/` | 全Phaseで適宜使用 |
| リファレンス | `../09-reference/` | 学習、問題解決時に参照 |
| ガバナンス | `../10-governance/` | Phase 0技術選定時に参照 |

**ドキュメントの役割分担**:
- **既存標準（01-10/）**: **What（何を守るべきか）**、**Why（なぜ守るべきか）**
- **統合ガイド（00-guides/）**: **When（いつ参照すべきか）**、**How（どう使うか）**、**Which（どのドキュメントを参照すべきか）**

---

### トップレベルREADME

- [devin-organization-standards/README.md](../README.md) - プロジェクト全体の概要とディレクトリ構成

---

## 📊 統計情報

### ファイル数とサイズ

| カテゴリ | ファイル数 | 合計サイズ |
|---------|-----------|-----------|
| マスターガイド | 2 | 69 KB |
| Phase Guides | 7 + 1 README | 124 KB |
| AI Guides | 7 + 1 README | 117 KB |
| **合計** | **18** | **310 KB** |

### 推定読書時間

| 対象者 | 初回 | 日常利用 |
|--------|------|---------|
| AIエージェント | 30分 | 必要時参照（5-10分/回） |
| 新規開発者 | 1-2時間 | 必要時参照（5-15分/回） |
| シニアエンジニア | 30-60分 | 必要時参照（2-5分/回） |
| プロジェクトマネージャー | 1-2時間 | 必要時参照（5-10分/回） |

---

## 🎨 ドキュメント設計原則

### 1. 軽量ナビゲーション型
- 各ガイドは15-30KBに抑制
- 詳細は既存標準（01-10/）に委譲
- 「何を参照すべきか」に焦点

### 2. 実行型アプローチ
- ステップバイステップのワークフロー
- 各ステップに明確な参照ドキュメント
- 判断基準を明示

### 3. 柔軟性
- フェーズスキップ可能
- プロジェクト規模に応じた適用
- 必須/推奨/参考の3段階の優先度

### 4. 一貫性
- 全Phase Guidesが同じ構造
- 用語の統一
- リンク形式の統一

---

## 📝 メンテナンス情報

### 更新頻度
- **マスターガイド**: 四半期ごとにレビュー、必要に応じて更新
- **Phase Guides**: 半期ごとにレビュー、プロセス変更時に更新
- **AI Guides**: 月次でレビュー、AI技術進化に応じて更新

### 管理者
**Owner**: Engineering Leadership Team  
**Contributors**: AI開発チーム、プロセス改善チーム  
**Reviewers**: シニアアーキテクト、テックリード

### レビュー基準
1. **完全性**: 全フェーズをカバーしているか
2. **正確性**: リンクが正しいか、内容が最新か
3. **使いやすさ**: 初見でも理解できるか
4. **一貫性**: 他のガイドと矛盾していないか


### バージョン履歴
- **v2.2.0** (2025-11-14): ドキュメントコメント品質標準追加 ⭐NEW
  - AI-DOCUMENTATION-COMMENT-CHECKLIST.md 追加（全言語対応チェックリスト）
  - 08-templates/code-templates/ 追加（TypeScript/Python/Java用テンプレート）
  - ドキュメントコメント必須化の明確化
  - パブリックAPI: 100%カバレッジ必須
  - 複雑なロジック: 80%以上推奨
  - Why重視(70%)のコメント方針
- **v1.2.0** (2025-11-12): Phase 2.1/2.2対応
  - phase-2.1-pre-implementation-design-guide.md 追加
  - phase-2.2-post-implementation-design-guide.md 追加
  - Design Phase の実装前後分割対応
- **v1.1.0** (2025-11-12): 統合テスト・SQLマイグレーション標準統合
  - Phase 4統合テスト詳細ガイド追加 (phase-4-integration-test-addition.md)
  - PBIタイプ別テスト要件マトリックス追加 (testing-standards-pbi-matrix-addition.md)
  - マルチリポジトリテストガイドライン追加 (MULTI-REPOSITORY-TESTING-GUIDELINES.md)
  - SQLマイグレーション標準の統合完了
    - Phase 3実装ガイド Section 3.8追加 (2,163行)
    - CI-SETUP-CHECKLIST Section 5.3追加 (2,600行)
  - レポートファイルを_archive/reports/に整理
- **v1.0.0** (2025-11-05): 初版リリース
  - AI-MASTER-WORKFLOW-GUIDE.md 作成
  - DOCUMENT-USAGE-MANUAL.md 作成
  - Phase 0-6 ガイド作成
  - AI Guides 集約

---

## ❓ よくある質問

### Q1: どのガイドから読むべきですか？
**A**: 役割によって異なります:
- **AIエージェント**: AI-MASTER-WORKFLOW-GUIDE.md → phase-guides/
- **新規開発者**: DOCUMENT-USAGE-MANUAL.md → AI-MASTER-WORKFLOW-GUIDE.md
- **レビューアー**: phase-guides/ の該当フェーズ → 既存標準（01-10/）

### Q2: すべてのフェーズを必ず踏襲する必要がありますか？
**A**: いいえ。AI-MASTER-WORKFLOW-GUIDE.mdの「フェーズスキップ判断基準」を参照してください。小規模なバグ修正などはPhase 0-2をスキップ可能です。

### Q3: 既存標準（01-10/）とこのディレクトリの違いは？
**A**: 
- **既存標準**: 「何を守るべきか（What）」「なぜ守るべきか（Why）」
- **00-guides**: 「いつ参照すべきか（When）」「どう使うか（How）」「どのドキュメントを参照すべきか（Which）」

### Q4: Phase Guidesは開発者も読むべきですか？
**A**: はい、特にレビューアーとして。AIが参照したドキュメントと成果物を確認する際に、Phase Guidesの完了基準が役立ちます。

### Q5: 新しいフェーズを追加できますか？
**A**: はい。ただし、全体の一貫性を保つため、以下の手順に従ってください:
1. Engineering Leadership Teamに提案
2. 既存Phase Guidesの構造に従って作成
3. AI-MASTER-WORKFLOW-GUIDE.mdに統合
4. レビューと承認

---

## 🚀 次のステップ

### 初めての方へ
1. ✅ この README.md を読む（今ここ）
2. ✅ [DOCUMENT-USAGE-MANUAL.md](./DOCUMENT-USAGE-MANUAL.md) でドキュメント体系を理解
3. ✅ [AI-MASTER-WORKFLOW-GUIDE.md](./AI-MASTER-WORKFLOW-GUIDE.md) でワークフロー全体を把握
4. ✅ [phase-guides/README.md](./phase-guides/README.md) でPhase Guidesの詳細を確認
5. ✅ 実際のPBIで活用開始

### AIエージェントとして開始
```bash
# Step 1: PBIを受け取る
# Step 2: AI-MASTER-WORKFLOW-GUIDE.md を開く
# Step 3: Phase 0から順次実行
```

### 人間の開発者として参照
```bash
# Step 1: DOCUMENT-USAGE-MANUAL.md を開く
# Step 2: シチュエーション別ガイドで自分の状況を探す
# Step 3: 推奨ドキュメントを参照
```

---

## 📞 サポート

### フィードバック
このガイドに関するフィードバックは歓迎します:
- **改善提案**: Engineering Leadership Teamへ
- **不明点**: phase-guides/ の該当ガイドを再確認、または DOCUMENT-USAGE-MANUAL.md 参照
- **バグ報告**: GitHub Issues または 内部チャンネル

### 貢献方法
1. 改善提案を作成
2. Engineering Leadership Teamにレビュー依頼
3. 承認後、該当ドキュメントを更新
4. バージョンを更新

---

**Last Updated**: 2025-11-12  
**Version**: 1.1.0  
**Status**: ✅ Active
