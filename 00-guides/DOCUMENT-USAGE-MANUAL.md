---
title: "Document Usage Manual - ドキュメント利用マニュアル"
version: "1.0.0"
created_date: "2025-11-05"
last_updated: "2025-11-14"
status: "Active"
audience: "AI Agents and Human Developers"
---

# ドキュメント利用マニュアル

> devin-organization-standards 体系の完全活用ガイド

**対象読者**: 
- 🤖 自律型AIエージェント (Devin, Cursor, GitHub Copilot等)
- 👤 人間開発者
- 📊 プロジェクトマネージャー

**目的**: ドキュメント体系を最大限に活用し、高品質な開発を効率的に実現

---

## 📚 目次

1. [ドキュメント体系の全体像](#ドキュメント体系の全体像)
2. [AIエージェント向け使用ガイド](#aiエージェント向け使用ガイド)
3. [人間開発者向け使用ガイド](#人間開発者向け使用ガイド)
4. [シチュエーション別ガイド](#シチュエーション別ガイド)
5. [トラブルシューティング](#トラブルシューティング)
6. [FAQとベストプラクティス](#faqとベストプラクティス)

---

## 📖 ドキュメント体系の全体像

### 3層構造の理解

```
┌─────────────────────────────────────────────────────────┐
│  Layer 1: マスタードキュメント (司令塔)                    │
│                                                           │
│  🎯 AI-MASTER-WORKFLOW-GUIDE.md                          │
│     └─ PBIから全フェーズの統合ワークフロー                 │
│                                                           │
│  📖 DOCUMENT-USAGE-MANUAL.md (本書)                      │
│     └─ ドキュメント選択・フェーズ別マップ                  │
│                                                           │
│  🚦 ESCALATION-CRITERIA-GUIDE.md                         │
│     └─ エスカレーション判断・意思決定支援                  │
│                                                           │
├─────────────────────────────────────────────────────────┤
│  Layer 2: フェーズ別 & AI活用ガイド (実行手順)             │
│                                                           │
│  📘 フェーズ別詳細ガイド (phase-guides/)                   │
│     ├─ phase-0-requirements-planning-guide.md            │
│     ├─ phase-1-project-initialization-guide.md                   │
│     ├─ phase-2-design-guide.md                           │
│     ├─ phase-3: phase-3-implementation-guide.md        │
│     ├─ phase-4-review-qa-guide.md                        │
│     ├─ phase-5-deployment-guide.md                       │
│     └─ phase-6-operations-maintenance-guide.md                       │
│                                                           │
│  🤖 AI活用ガイド                                          │
│     ├─ AI-USAGE-GUIDE.md (ナビゲーション)                │
│     ├─ AI-PROMPTS.md (プロンプト例集)                     │
│     ├─ AI-CHECKLISTS.md (チェックリスト)                  │
│     ├─ AI-CODING-INSTRUCTIONS.md (コーディング指示)       │
│     ├─ AI-QUICK-REFERENCE.md (クイックリファレンス)        │
│     └─ AI-PROMPT-TEMPLATES.md (テンプレート)             │
│                                                           │
├─────────────────────────────────────────────────────────┤
│  Layer 3: 組織標準ドキュメント (参照標準)                  │
│                                                           │
│  📂 10カテゴリ、97ファイル                                │
│     ├─ 01-coding-standards/ (Tier 1) ⭐⭐⭐⭐⭐           │
│     ├─ 02-architecture-standards/ (Tier 1) ⭐⭐⭐⭐⭐     │
│     ├─ 03-development-process/ (Tier 3) ⭐⭐⭐           │
│     ├─ 04-quality-standards/ (Tier 2) ⭐⭐⭐⭐            │
│     ├─ 05-technology-stack/ (Tier 1) ⭐⭐⭐⭐⭐           │
│     ├─ 06-tools-and-environment/ (Tier 3) ⭐⭐⭐         │
│     ├─ 07-security-compliance/ (Tier 2) ⭐⭐⭐⭐          │
│     ├─ 08-templates/ (Tier 4) ⭐⭐                       │
│     ├─ 09-reference/ (Tier 4) ⭐⭐                       │
│     └─ 10-governance/ (Tier 4) ⭐⭐                      │
└─────────────────────────────────────────────────────────┘
```

### ドキュメントの役割分担

| レイヤー | 役割 | 使用タイミング | 読む順序 |
|---------|------|--------------|---------|
| **Layer 1** | 全体の流れと判断基準 | プロジェクト開始時・迷った時 | 最初に1回 |
| **Layer 2** | 具体的な作業手順 | 各フェーズ実行時 | フェーズごと |
| **Layer 3** | 詳細な技術標準 | 実装・レビュー時 | 必要に応じて |

---

## 🤖 AIエージェント向け使用ガイド

### クイックスタート: 5ステップ

#### Step 1: 初回セットアップ (1回のみ)

```markdown
## 実行すること

1. **全体構造の理解**
   - このマニュアル(DOCUMENT-USAGE-MANUAL.md)を読む
   - README.md を読む
   - AI-USAGE-GUIDE.md を読む

2. **マスタードキュメントの読み込み**
   - AI-MASTER-WORKFLOW-GUIDE.md を読む
   - 全7フェーズの流れを理解
   - Phase 0 の詳細を特に注意深く読む

3. **クイックリファレンスの把握**
   - AI-QUICK-REFERENCE.md を読む
   - 数値基準を記憶

4. **ドキュメント検索方法の習得**
   - 本マニュアルの第3章「フェーズ別ドキュメントマップ」を読む
   - フェーズとタスクに応じたドキュメント選択方法を理解

5. **準備完了**
   - PBIを受け取る準備ができた
```

**所要時間**: 30-60分 (AIの読み込み速度による)

---

#### Step 2: PBI受領時の標準手順

```markdown
## PBIを受け取ったら

1. **AI-MASTER-WORKFLOW-GUIDE.md を開く**
   - Phase 0 セクションに移動

2. **Phase 0 の各ステップを順番に実行**
   - Step 0.1: PBI読み込み
   - Step 0.2: 情報抽出
   - Step 0.3: タスク細分化
   - Step 0.4: 依存関係特定
   - Step 0.5: 初期技術方針
   - Step 0.6: 要件分析書作成
   - Step 0.7: 完了確認

3. **完了チェックリストを確認**
   - AI-CHECKLISTS.md の Phase 0 セクション
   - 全項目が✓になるまで確認

4. **成果物の確認**
   - requirements-analysis-[PBI-KEY].md
   - task-breakdown-[PBI-KEY].md

5. **次フェーズの判断** ⭐重要
   - AI-MASTER-WORKFLOW-GUIDE.md の「Phase 0完了と次フェーズの判断」セクション
   - 判断フローに従って次に進むべきフェーズを決定
   - 不要なフェーズはスキップ可能
```

---

#### Step 2.5: フェーズスキップの判断 ⭐新規追加

```markdown
## 重要: 全フェーズを実行する必要はありません

### 基本原則

**PBIの内容によって、必要なフェーズのみ実行してください。**

- 新規プロジェクト → 全フェーズ (Phase 0-6) 実施
- 既存プロジェクトへの機能追加 → Phase 1 スキップ可能
- バグ修正 → Phase 0 (簡易版) → 3 → 4
- リファクタリング → Phase 0 → 3 → 4

### 判断方法

**Phase 0 完了後に必ず実施**:

1. **AI-MASTER-WORKFLOW-GUIDE.md を開く**
   - 「Phase 0完了と次フェーズの判断」セクションを読む

2. **判断フローに従う**
   ```
   [Q1] 新規プロジェクトか?
     Yes → Phase 1 へ
     No  → Q2 へ
   
   [Q2] プロジェクト構造・技術スタック確定済みか?
     No  → Phase 1 へ
     Yes → Phase 2 へ (Phase 1 スキップ)
   
   [Q3] 設計変更が必要か?
     Yes → Phase 2 へ
     No  → Phase 3 へ (Phase 2 スキップ)
   
   [Q4] コード変更が必要か?
     Yes → Phase 3 へ
     No  → Phase 4 へ (Phase 3 スキップ)
   ```

3. **各フェーズの開始条件を確認**
   - AI-MASTER-WORKFLOW-GUIDE.md の各Phaseセクション
   - 「このフェーズの開始条件」を読む
   - ✅ 実施すべき場合 に該当するか確認
   - ⏭️ スキップ可能な場合 に該当するか確認

4. **スキップする場合は記録**
   ```markdown
   ## フェーズ実行記録: [PBI-KEY]
   
   ### 実行したフェーズ
   - ✅ Phase 0: 要件分析 (実施)
   - ⏭️ Phase 1: プロジェクト初期化 (スキップ - 既存プロジェクト)
   - ✅ Phase 2: 設計 (実施)
   - ✅ Phase 3: 実装 (実施)
   - ✅ Phase 4: レビュー (実施)
   - ✅ Phase 5: デプロイ (実施)
   - ⏭️ Phase 6: 運用・保守 (後日実施)
   
   ### スキップ理由
   - Phase 1: 既存プロジェクト「ProjectX」への機能追加
   - Phase 6: 新機能のため運用実績なし。1週間後に実施予定
   ```

### スキップ可否の早見表

| Phase | スキップ可能性 | 主な条件 |
|-------|--------------|----------|
| Phase 0 | ⚠️ ほぼ不可 | 簡易版は可能だが、何らかの要件確認は必須 |
| Phase 1 | ✅ 可能 | 既存プロジェクト、技術スタック決定済み |
| Phase 2 | ✅ 可能 | 既存設計に従う、バグ修正、小規模リファクタリング |
| Phase 3 | ✅ 可能 | コード変更不要（ドキュメントのみ等） |
| Phase 4 | ⚠️ 原則不可 | 緊急時のみ簡易版、後で詳細レビュー必須 |
| Phase 5 | ✅ 可能 | 開発環境のみ、実験的機能、後日デプロイ予定 |
| Phase 6 | ✅ 可能 | 未リリース、新機能で運用実績なし |

### 詳細情報

**参照**: AI-MASTER-WORKFLOW-GUIDE.md の「🎯 フェーズ実行の判断ガイド」セクション
- 7つのPBIタイプ別推奨フロー
- フェーズスキップの判断フローチャート
- 各フェーズのスキップ条件詳細
```

---

#### Step 3: 各フェーズでの標準手順

```markdown
## フェーズ実行時の基本パターン

### パターン: フェーズ開始時

1. **このフェーズが必要か確認** ⭐重要
   - AI-MASTER-WORKFLOW-GUIDE.md の該当Phaseセクション
   - 「このフェーズの開始条件」を読む
   - 実施すべきかスキップすべきか判断
   - スキップする場合は記録して次フェーズへ

2. **前フェーズの成果物を確認**
   - Phase N-1 の出力を読み込む
   - 完全性を確認

3. **本フェーズのガイドを開く**
   - AI-MASTER-WORKFLOW-GUIDE.md の該当セクション
   - または phase-guides/phase-N-xxx-guide.md

4. **参照ドキュメントリストを確認**
   - 🔴必須 のドキュメントを最初に読む
   - 🟡推奨 のドキュメントを次に読む
   - ⚪参考 は必要に応じて

5. **ステップバイステップで実行**
   - ガイドの手順に従う
   - 各ステップで成果物を作成

### パターン: フェーズ完了時

1. **完了チェックリストを実行**
   - AI-CHECKLISTS.md の該当フェーズ
   - 全項目✓を確認

2. **成果物の確認**
   - 必要な全ファイルが存在するか
   - 内容が要求を満たしているか

3. **次フェーズへの引き継ぎ準備**
   - 引き継ぎ事項を明確化
   - Phase N+1 の入力を整理

4. **次フェーズへ進むか人間確認を要求**
   - 自動継続可能な場合 → 次フェーズへ
   - 人間確認が必要な場合 → レビュー要求
```

---

#### Step 4: 迷った時の対処法

```markdown
## 問題発生時の対応フロー

### ケース1: どのドキュメントを見るべきか分からない

1. **本マニュアルの第3章「フェーズ別ドキュメントマップ」を開く**
2. 現在のフェーズを確認
3. 実行中のタスクタイプを確認
4. フェーズ別の推奨ドキュメントリストを参照
5. 推奨されたドキュメントを読む

### ケース2: ドキュメント間で矛盾がある

1. **本マニュアルの「問題2: ドキュメント間で矛盾がある」セクションを参照**
2. 矛盾解決の4つのルールを適用:
   - ルール1: 具体的 > 抽象的
   - ルール2: 新しい > 古い
   - ルール3: ドメイン固有 > 一般
   - ルール4: Tier 1 > Tier 2 > Tier 3 > Tier 4
3. 判断根拠を記録

### ケース3: 技術選定で悩んでいる

1. **ESCALATION-CRITERIA-GUIDE.md を開く**
2. エスカレーション基準で影響度を評価
3. 10-governance/technology-radar.md で組織方針確認
4. 10-governance/standards-hierarchy-and-precedence.md で優先順位確認
5. 判断を技術選定書に記録

### ケース4: エスカレーションすべきか判断できない

1. **AI-MASTER-WORKFLOW-GUIDE.md のエスカレーション基準を確認**
2. 該当する場合 → 即座にエスカレーション
3. 該当しない場合 → 自己判断で進行
4. 不確実性が高い場合 → 実装後レビュー要求
```

---

#### Step 5: 継続的な学習と改善

```markdown
## フィードバックループ

### 各フェーズ完了後

1. **何がうまくいったか記録**
   - スムーズだったステップ
   - 役立ったドキュメント

2. **困難だった点を記録**
   - 不明確だったガイド
   - 不足していた情報
   - 見つけにくかったドキュメント

3. **改善提案を作成**
   - ガイドの改善案
   - 新規ドキュメントの提案

4. **人間へのフィードバック**
   - プロジェクト完了時にレポート
```

---

### AIエージェント向けチートシート

```markdown
## よく使うドキュメント一覧

### 毎回参照
- [ ] AI-MASTER-WORKFLOW-GUIDE.md - 現在のフェーズのセクション
- [ ] AI-CHECKLISTS.md - 現在のフェーズのチェックリスト

### フェーズ別必須ドキュメント

**Phase 0: 要件分析**
- 08-templates/requirements-analysis-template.md

**Phase 1: プロジェクト初期化**
- 05-technology-stack/approved-technologies.md
- 08-templates/project-templates/
- 08-templates/code-templates/ (コードテンプレート)

**Phase 2: 設計**
- 02-architecture-standards/ (該当するファイル)
- 02-architecture-standards/api/ (API設計標準)
- 02-architecture-standards/frontend/ (フロントエンド設計標準)
- 07-security-compliance/ (該当するファイル)

**Phase 3: 実装**
- 01-coding-standards/ (使用言語のファイル)
- 03-development-process/code-generation-standards/ (コード生成標準)
- 03-development-process/testing-standards/ (テスト標準)
- 03-development-process/feature-flag-management/ (機能フラグ管理)
- 08-templates/code-templates/ (コードテンプレート)
- implementation-phase-document-reference-guide.md
- AI-CODING-INSTRUCTIONS.md

**Phase 4: レビュー**
- 04-quality-standards/code-quality-standards.md
- 04-quality-standards/e2e-testing/ (E2Eテスト)
- 04-quality-standards/load-testing/ (負荷テスト)
- 04-quality-standards/test-data-management/ (テストデータ管理)
- 04-quality-standards/defect-management/ (不具合管理)
- 03-development-process/code-review-standards.md

**Phase 5: デプロイ**
- 03-development-process/ci-cd-pipeline.md
- 06-tools-and-environment/ (該当するファイル)

**Phase 6: 運用**
- 03-development-process/incident-management.md
- 09-operations/ (該当するファイル)

### 困った時に見るドキュメント
- 本マニュアルの第3章 - ドキュメント選択に迷った時
- ESCALATION-CRITERIA-GUIDE.md - エスカレーション判断に迷った時
- AI-PROMPTS.md - 具体的なプロンプト例が欲しい時
- AI-QUICK-REFERENCE.md - 数値基準を確認したい時
```

---

## 👤 人間開発者向け使用ガイド

### 利用シーン別ガイド

#### シーン1: 新規プロジェクト開始時

```markdown
## 実行すること

1. **PBIを作成・記述**
   - タイトル: 明確で具体的に
   - 説明: 背景・目的を記述
   - 受入基準: 具体的で測定可能に
   - 技術的制約: 使用技術・避けるべき技術を明記

2. **AIに指示**
   ```
   このPBI (PROJ-123) について、Phase 0 (要件分析)を実行してください。
   AI-MASTER-WORKFLOW-GUIDE.md に従って進めてください。
   ```

3. **Phase 0 成果物のレビュー**
   - requirements-analysis-PROJ-123.md を確認
   - task-breakdown-PROJ-123.md を確認
   - 不明点があればAIからの確認リクエストに回答

4. **次フェーズの判断と承認** ⭐重要
   - 要件分析が正しければ次フェーズへ進行を承認
   - **全フェーズが必要とは限りません**
     - 新規プロジェクト → Phase 1 から開始
     - 既存プロジェクトへの機能追加 → Phase 2 から開始 (Phase 1 スキップ)
     - バグ修正 → Phase 3 から開始 (Phase 1,2 スキップ)
   - AIに「次に進むべきフェーズを判断してください」と指示
   - AIが判断したフローを確認・承認
   - 必要に応じて修正指示

5. **フェーズスキップの確認**
   - AIがスキップしたフェーズがある場合
   - スキップ理由を確認
   - 理由が妥当か判断
   - 不適切な場合は該当フェーズの実施を指示

**参考**: AI-MASTER-WORKFLOW-GUIDE.md の「🎯 フェーズ実行の判断ガイド」セクション
```

---

#### シーン2: 実装中のコードレビュー

```markdown
## レビューの進め方

1. **AIが参照したドキュメントを確認**
   - AIは参照履歴を記録しているはず
   - どの標準に基づいて実装したか確認

2. **チェックリストでレビュー**
   - AI-CHECKLISTS.md の Phase 3, 4 セクション
   - 各項目が満たされているか確認

3. **組織標準との照合**
   - 01-coding-standards/ の該当言語標準
   - 02-architecture-standards/ の設計標準

4. **フィードバック**
   - 準拠している点を承認
   - 改善点を具体的に指示
```

---

#### シーン3: 標準の更新・追加

```markdown
## 標準更新プロセス

1. **更新提案の作成**
   - 10-governance/standards-update-process.md を参照
   - 提案書を作成

2. **Technical Committee レビュー**
   - 提案をレビュー
   - 必要に応じて修正

3. **承認・反映**
   - 承認されたら該当ドキュメントを更新
   - バージョン番号を更新
   - last_updated 日付を更新

4. **AIへの通知**
   - 更新内容をAIに通知
   - 再読み込みを指示
```

---

### 人間開発者向けチートシート

```markdown
## よく見るドキュメント

### プロジェクト開始時
- README.md - 全体概要
- AI-MASTER-WORKFLOW-GUIDE.md - ワークフロー確認
- phase_document_matrix.md - フェーズ別参照ドキュメント

### コードレビュー時
- AI-CHECKLISTS.md - レビュー観点
- 01-coding-standards/ - コード品質確認
- 04-quality-standards/ - 品質基準確認

### 標準確認時
- 該当カテゴリの README.md - カテゴリ概要
- 具体的な標準ドキュメント - 詳細確認

### 問題発生時
- 03-development-process/incident-management.md
- ESCALATION-CRITERIA-GUIDE.md - エスカレーション判断基準確認
```

---

## 🎯 シチュエーション別ガイド

### シチュエーション1: 初めて使う (AIエージェント)

```markdown
## 初回利用の流れ

時間: 約1時間

1. このマニュアル全体を読む (20分)
2. AI-USAGE-GUIDE.md を読む (15分)
3. AI-MASTER-WORKFLOW-GUIDE.md を読む (20分)
4. AI-QUICK-REFERENCE.md を読む (5分)

→ 準備完了!
```

---

### シチュエーション2: Phase 3 (実装) だけ担当

```markdown
## Phase 3 のみ実施する場合

前提: Phase 0-2 が完了している

1. **前フェーズ成果物の確認**
   - requirements-analysis-xxx.md
   - design-document-xxx.md
   - API仕様書

2. **Phase 3 ガイドを読む**
   - implementation-phase-document-reference-guide.md
   - タスク粒度に応じた参照ドキュメント確認

3. **コーディング標準を読む**
   - 01-coding-standards/ の使用言語ファイル
   - AI-CODING-INSTRUCTIONS.md

4. **実装開始**
   - ガイドの手順に従う
   - 継続的にチェックリスト確認

5. **完了確認**
   - AI-CHECKLISTS.md の Phase 3
   - 全項目✓を確認
```

---

### シチュエーション3: 緊急バグ修正 (Hotfix)

```markdown
## 緊急対応の場合

時間制約あり - 最小限の参照で進める

**フェーズ実行**: Phase 0 (簡易版) → Phase 3 → Phase 4 (簡易版) → Phase 5
**スキップ**: Phase 1, 2, 6 (後日実施)

1. **Phase 0 簡易版 (5-10分)**
   - バグの理解と再現手順の確認
   - 影響範囲の特定
   - 修正方針の決定

2. **必須参照 (5-10分)**
   - AI-QUICK-REFERENCE.md - 数値基準
   - 01-coding-standards/ - 該当言語の基本ルール

3. **Phase 3: バグ修正実施**
   - できる限り標準に従う
   - 時間がない場合は「TODO: 要リファクタリング」コメント

4. **Phase 4 簡易版: 完了後**
   - 簡易レビュー実施
   - 後で詳細レビュータスク作成

5. **Phase 5: 緊急デプロイ**
   - 本番環境への緊急デプロイ

6. **Phase 6 後日実施**
   - 緊急対応後、必ず Phase 4 (詳細レビュー) を実施
   - Phase 6 で根本原因分析と再発防止

⚠️ **注意**: 緊急時でも Phase 4 (レビュー) を完全スキップすることはできません。
簡易版で実施し、後で詳細レビューを必ず実施してください。
```

---

### シチュエーション4: 技術選定会議の準備

```markdown
## 技術選定のための事前準備

1. **技術レーダー確認**
   - 10-governance/technology-radar.md
   - 組織の技術方針を理解

2. **該当技術スタック確認**
   - 05-technology-stack/ の該当ファイル
   - 承認済み技術リストを確認

3. **エスカレーション基準の確認**
   - ESCALATION-CRITERIA-GUIDE.md の影響度×緊急度マトリクス
   - 評価基準と判断フローを確認

4. **事例確認**
   - 09-reference/case-studies.md
   - 過去の選定事例を参考に

5. **提案書作成**
   - 08-templates/technical-proposal-template.md 使用
```

---

## 🔧 トラブルシューティング

### 問題1: ドキュメントが多すぎて何を見ればいいか分からない

**解決策**:

```markdown
1. **まずは Layer 1 (マスタードキュメント) だけ読む**
   - AI-MASTER-WORKFLOW-GUIDE.md
   - 本マニュアル (DOCUMENT-USAGE-MANUAL.md)
   
2. **現在のフェーズに集中**
   - Phase 0 なら Phase 0 のセクションだけ
   - 他は後回し

3. **必須(🔴)だけ読む**
   - 推奨(🟡)と参考(⚪)は後で

4. **phase_document_matrix.md を活用**
   - フェーズ×ドキュメントの一覧
   - 必要なものだけピックアップ
```

---

### 問題2: ドキュメント間で矛盾がある

**解決策**:

```markdown
1. **本マニュアルの矛盾解決ルールを適用**:
   - ルール1: 具体的 > 抽象的
   - ルール2: 新しい > 古い
   - ルール3: ドメイン固有 > 一般
   - ルール4: Tier 1 > Tier 2 > Tier 3 > Tier 4
2. 以下のセクションを確認

3. **それでも解決しない場合**
   - エスカレーション
   - 人間に確認を要求
```

---

### 問題3: 特定のドキュメントが見つからない

**解決策**:

```markdown
1. **README.md の目次を確認**
   - 全体構造を把握

2. **各カテゴリの README.md を確認**
   - 01-coding-standards/README.md
   - 02-architecture-standards/README.md
   - など

3. **本マニュアルの第3章で検索**
   - フェーズ別ドキュメントマップで探す

4. **見つからない場合**
   - ドキュメントが存在しない可能性
   - 人間に確認または新規作成提案
```

---

### 問題4: 手順が複雑すぎてついていけない

**解決策**:

```markdown
1. **シンプルバージョンを使う**
   - AI-QUICK-REFERENCE.md - 要点のみ
   - チェックリストだけ見る

2. **段階的に習得**
   - 第1週: Phase 0-1 だけ
   - 第2週: Phase 2-3 を追加
   - 第3週: Phase 4-6 を追加

3. **具体例を参考にする**
   - AI-PROMPTS.md の例
   - 09-reference/case-studies.md

4. **わからない部分は飛ばす**
   - 完璧主義にならない
   - 最小限で開始
   - 徐々に充実させる
```

---

## ❓ FAQ とベストプラクティス

### FAQ

#### Q1: 全てのドキュメントを読む必要がありますか?

**A**: いいえ。

- **最初に読むべき**: Layer 1 のマスタードキュメント (3本)
- **フェーズごとに読む**: 該当フェーズのガイド
- **必要に応じて**: Layer 3 の詳細標準

約95%の状況では、Layer 1 + 現在のフェーズガイド + 必須(🔴)ドキュメントだけで十分です。

---

#### Q2: ドキュメントはどれくらいの頻度で更新されますか?

**A**: ドキュメントごとに異なります。

- **マスタードキュメント**: 四半期ごと
- **フェーズ別ガイド**: 実使用フィードバック後
- **組織標準**: 必要に応じて (提案ベース)

各ドキュメントの `last_updated` フィールドで確認できます。

---

#### Q3: 人間の確認なしでどこまで進められますか?

**A**: プロジェクトの種類による。

**完全自律可能**:
- 標準的な CRUD API
- よくある機能の実装
- バグ修正 (軽微なもの)

**人間確認が必要**:
- 新しいアーキテクチャパターン
- セキュリティに影響する実装
- 組織標準からの逸脱
- 高リスクな変更

エスカレーション基準は `AI-MASTER-WORKFLOW-GUIDE.md` を参照。

---

#### Q4: 標準に従わない方が良い場合はどうすれば?

**A**: 例外承認プロセスを使用。

1. `10-governance/exception-approval-process.md` を読む
2. 例外申請書を作成
3. 正当な理由を明記
4. Technical Committee の承認を得る
5. 承認後に実装

承認なしの逸脱は推奨されません。

---

#### Q5: このマニュアル自体が古くなったらどうすれば?

**A**: フィードバックを送ってください。

1. 問題点を具体的に記録
2. 改善案を提示
3. `10-governance/standards-update-process.md` に従う
4. または直接 Engineering Leadership Team に連絡

---

### ベストプラクティス

#### ✅ DO (推奨)

1. **段階的に学習する**
   - 一度に全て覚えようとしない
   - フェーズごとに習得

2. **チェックリストを活用する**
   - AI-CHECKLISTS.md を毎回確認
   - 漏れを防止

3. **参照履歴を記録する**
   - どのドキュメントを見たか記録
   - 判断根拠を明確に

4. **フィードバックを積極的に**
   - 改善提案を歓迎
   - 使いにくい点を報告

5. **人間とのコミュニケーション**
   - 不明点は早めにエスカレーション
   - 定期的にレビュー要求

#### ❌ DON'T (非推奨)

1. **ドキュメントを無視する**
   - 「知っているから読まない」は危険
   - 標準は更新されている

2. **完璧主義になる**
   - 100%の準拠は現実的ではない
   - 80%準拠を目標に

3. **古い情報に頼る**
   - 必ず `last_updated` を確認
   - 古いドキュメントは人間に確認

4. **エスカレーションを躊躇する**
   - 不確実性が高い場合は即確認
   - 後で問題になるより早めに

5. **ドキュメント間を行き来しすぎる**
   - 1つのガイドに集中
   - 必要な参照だけに絞る

---

## 📊 利用状況の追跡

### 推奨: 利用ログの記録

```markdown
## プロジェクトごとの利用ログ

### プロジェクト: PROJ-123

#### Phase 0
- 参照ドキュメント:
  - AI-MASTER-WORKFLOW-GUIDE.md (Phase 0)
  - 08-templates/requirements-analysis-template.md
- 所要時間: 45分
- 困難だった点: タスク細分化の粒度判定
- 改善提案: タスク粒度の判定例をもっと増やす

#### Phase 1
- 参照ドキュメント:
  - AI-MASTER-WORKFLOW-GUIDE.md (Phase 1)
  - 05-technology-stack/backend-stack.md
  - 08-templates/project-templates/microservice-nodejs
- 所要時間: 2時間
- 困難だった点: なし
- 改善提案: なし

[以下各フェーズ同様]
```

このログは継続的改善に活用されます。

---

## 🚀 次のステップ

### 新規ユーザー (AI or 人間)

1. ✅ このマニュアルを読んだ
2. 📖 次に読むべき: `AI-USAGE-GUIDE.md`
3. 🎯 その次: `AI-MASTER-WORKFLOW-GUIDE.md`
4. 🏁 準備完了: PBIを受け取る

### 既存ユーザー

1. 定期的にマスタードキュメントを再読
2. 新規ドキュメント追加時は通知を確認
3. フィードバックを継続的に送信

---

## 📞 サポート

### 問い合わせ先

- **ドキュメント内容**: Engineering Leadership Team
- **技術的質問**: Technical Committee
- **緊急対応**: #dev-standards (Slack)

### 関連リンク

- [メインREADME](./README.md)
- [AI使用ガイド](./AI-USAGE-GUIDE.md)
- [マスターワークフロー](./AI-MASTER-WORKFLOW-GUIDE.md)
- [エスカレーション基準ガイド](./ESCALATION-CRITERIA-GUIDE.md)

---

**最終更新**: 2025-11-05  
**バージョン**: 1.0.0  
**フィードバック歓迎**: 改善提案をお待ちしています

---

## 📝 バージョン履歴

| バージョン | 日付 | 変更内容 |
|-----------|------|---------|
| 1.0.0 | 2025-11-05 | 初版リリース |

---

**このマニュアルは living document です。皆様のフィードバックで継続的に改善されます。**
