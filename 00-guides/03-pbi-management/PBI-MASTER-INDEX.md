# PBIドキュメント マスターインデックス

## 📚 ドキュメント概要

このインデックスは、Product Backlog Item（PBI）に関する全ドキュメントへのナビゲーションを提供します。

**完全版ドキュメント数:** 6個  
**Quick Reference:** 2個（優先順位付け、PBI作成）  
**AIプロンプト集:** 1個  
**合計サイズ:** 340 KB  
**合計トークン数:** 約175,000トークン  
**最終更新:** 2025-11-17

---

## 🤖 AIプロンプト集（AI活用向け）

### [PBI-AI-PROMPT-COLLECTION.md](./templates-prompts/PBI-AI-PROMPT-COLLECTION.md)
**PBI作成AIプロンプト集**

| 項目 | 内容 |
|------|------|
| **サイズ** | 38 KB |
| **トークン数** | 約19,000 |
| **読了時間** | 20-30分 |

**主要内容:**
- 8つのPBIタイプ別プロンプト（NPI/NFD/ENH/BUG/REF/ARC/HOT/POC）
- タスク別プロンプト（優先順位評価、分割、レビュー等）
- 応用プロンプト（Epic分解、依存関係分析等）
- AIエージェント設定用システムプロンプト
- プロンプト最適化のヒント

**こんな時に使う:**
- AIにPBI作成を依頼する時
- AIに優先順位評価を依頼する時
- AIにPBI分割を依頼する時
- AIエージェントを設定する時

---

## ⚡ Quick Reference（日常利用向け）

### [PBI-PRIORITIZATION-GUIDE-QUICK-REF.md](./quick-reference/PBI-PRIORITIZATION-GUIDE-QUICK-REF.md)
**優先順位付けクイックリファレンス**

| 項目 | 内容 |
|------|------|
| **サイズ** | 11 KB |
| **トークン数** | 約2,500（完全版の94.7%削減） |
| **読了時間** | 3-5分 |

**主要内容:**
- 4大原則（ビジネス価値、技術複雑度、リスク、ROI）
- 5つの優先順位付け手法（簡易版）
- PBIタイプ別優先順位戦略
- チェックリスト
- AIエージェント向けクイックガイド

**こんな時に使う:**
- 日常的な優先順位判断
- 会議中の迅速な意思決定
- チェックリストとして活用

---

### [PBI-CREATION-BEST-PRACTICES-QUICK-REF.md](./quick-reference/PBI-CREATION-BEST-PRACTICES-QUICK-REF.md)
**PBI作成ベストプラクティス クイックリファレンス**

| 項目 | 内容 |
|------|------|
| **サイズ** | 15 KB |
| **トークン数** | 約3,500（完全版の92.2%削減） |
| **読了時間** | 5-7分 |

**主要内容:**
- 5大原則（価値、視点、独立性、粒度、検証可能性）
- INVEST原則（6つの基準）
- 8タイプ別作成ガイドライン（重点ポイント）
- ユーザーストーリーと受け入れ基準のテンプレート
- レビューチェックリスト
- よくある失敗パターン Top 10

**こんな時に使う:**
- PBI作成時のリファレンス
- レビュー前のチェック
- 基本原則の確認

---

## 🗂️ 完全版ドキュメント一覧（推奨読了順）

### Phase 0: PBIの基礎理解

#### 1. [PBI-TYPE-JUDGMENT-GUIDE.md](./core/PBI-TYPE-JUDGMENT-GUIDE.md)
**8つのPBIタイプの判定方法**

| 項目 | 内容 |
|------|------|
| **サイズ** | 52 KB (1,616行) |
| **トークン数** | 約26,000 |
| **読了時間** | 15-20分 |
| **対象読者** | 自律的AIエージェント、開発チーム、PM |

**主要内容:**
- 8つのPBIタイプ（NPI/NFD/ENH/BUG/REF/ARC/HOT/POC）の定義
- タイプ判定のフローチャート
- 境界ケースの判定ルール
- AIエージェント向け判定プロンプト

**こんな時に読む:**
- 新しいPBIのタイプを判断したい
- NPI、NFD、ENHの違いを明確にしたい
- 境界ケース（例: ENH vs REF）の判定が必要

---

### Phase 1: PBIの作成

#### 2. [PBI-TEMPLATE-CATALOG.md](./templates-prompts/PBI-TEMPLATE-CATALOG.md)
**PBIテンプレートカタログ**

| 項目 | 内容 |
|------|------|
| **サイズ** | 20 KB (663行) |
| **トークン数** | 約10,000 |
| **読了時間** | 5-10分 |
| **対象読者** | 全員（初心者向け） |

**主要内容:**
- 8つのPBIタイプ別テンプレート
- テンプレート選択ガイド
- 各テンプレートの使用例
- テンプレートファイルへのリンク

**こんな時に読む:**
- PBIを初めて作成する
- 適切なテンプレートを選びたい
- テンプレートの記入例を見たい

**関連ファイル:** `/08-templates/pbi-templates/` (9個のテンプレートファイル)

---

#### 3. [PBI-CREATION-BEST-PRACTICES.md](./core/PBI-CREATION-BEST-PRACTICES.md)
**PBI作成のベストプラクティス**

| 項目 | 内容 |
|------|------|
| **サイズ** | 88 KB (3,157行) |
| **トークン数** | 約45,000 |
| **読了時間** | 30-40分 |
| **対象読者** | Product Owner、開発チーム、AIエージェント |

**主要内容:**
- INVEST原則の詳細解説
- 8タイプ別のPBI作成ガイド
- ユーザーストーリーの書き方
- 受け入れ基準の定義方法
- 見積もり手法（ストーリーポイント）
- 100以上の実例集

**こんな時に読む:**
- 高品質なPBIを作成したい
- INVEST原則を理解したい
- ユーザーストーリーの書き方を学びたい
- 受け入れ基準を明確にしたい

**📌 注意:** このドキュメントは大きいため、必要なセクションを絞って読むことを推奨

---

### Phase 2: PBIの管理・分割

#### 4. [PBI-SPLITTING-GUIDE.md](./guides/PBI-SPLITTING-GUIDE.md)
**PBI分割ガイド**

| 項目 | 内容 |
|------|------|
| **サイズ** | 25 KB (994行) |
| **トークン数** | 約12,000 |
| **読了時間** | 10-15分 |
| **対象読者** | Product Owner、開発チーム |

**主要内容:**
- 8つの分割パターン（横・縦・優先度等）
- PBIタイプ別分割戦略
- Epic管理とストーリー分割
- 分割の実例集
- AI自動分割フロー

**こんな時に読む:**
- 大きすぎるPBIを分割したい
- Epicを適切なサイズのストーリーに分けたい
- 分割の具体例を見たい

---

#### 5. [PBI-PRIORITIZATION-GUIDE.md](./core/PBI-PRIORITIZATION-GUIDE.md)
**PBI優先順位付けガイド**

| 項目 | 内容 |
|------|------|
| **サイズ** | 92 KB (3,559行) |
| **トークン数** | 約47,000 |
| **読了時間** | 30-40分 |
| **対象読者** | Product Owner、開発チーム、PM、AIエージェント |

**主要内容:**
- 優先順位付けの4つの基本原則
- 5つの主要手法（MoSCoW/RICE/WSJF/Kano/Value vs Effort）
- PBIタイプ別優先順位戦略
- 4つの実例集（ECサイト、バグ修正等）
- JIRA連携と自動化
- よくある失敗パターンと対策

**こんな時に読む:**
- バックログの優先順位を決めたい
- RICE評価やWSJFを学びたい
- データに基づく優先順位付けをしたい
- 技術的負債と新機能のバランスを取りたい

**📌 注意:** このドキュメントは大きいため、必要な手法（セクション3）や実例（セクション6）を絞って読むことを推奨

---

### Phase 3: JIRAでの運用

#### 6. [PBI-JIRA-MANAGEMENT-GUIDE.md](./guides/PBI-JIRA-MANAGEMENT-GUIDE.md)
**PBI JIRA管理ガイド**

| 項目 | 内容 |
|------|------|
| **サイズ** | 19 KB (790行) |
| **トークン数** | 約10,000 |
| **読了時間** | 10-15分 |
| **対象読者** | 開発チーム、PM、JIRA管理者 |

**主要内容:**
- JIRA初期設定（Issue Type、カスタムフィールド）
- PBIワークフロー設定
- Epic/Story/Task階層管理
- JQLクエリ集
- 自動化ルールとインテグレーション
- ダッシュボード構成

**こんな時に読む:**
- JIRAでPBI管理を始めたい
- カスタムフィールドを設定したい
- 自動化ルールを作成したい
- 効率的なJQL検索を知りたい

---

## 🎯 シナリオ別ガイド

### シナリオ1: 「新しいPBIを作成したい」

**推奨読了順:**
```
1. PBI-TYPE-JUDGMENT-GUIDE.md (セクション3-4)
   → PBIタイプを判定

2. PBI-TEMPLATE-CATALOG.md
   → 適切なテンプレートを選択

3. PBI-CREATION-BEST-PRACTICES.md (該当タイプのセクション)
   → 詳細な作成方法を確認

4. PBI-JIRA-MANAGEMENT-GUIDE.md (セクション2-4)
   → JIRAに登録
```

**合計トークン数:** 約20,000-30,000（部分読みの場合）

---

### シナリオ2: 「バックログの優先順位を決めたい」

**推奨読了順:**
```
1. PBI-PRIORITIZATION-GUIDE.md (セクション3)
   → 優先順位付け手法を選択
   - RICE評価: データが豊富な場合
   - MoSCoW法: シンプルな分類が必要な場合
   - Value vs Effort: 視覚的に判断したい場合

2. PBI-PRIORITIZATION-GUIDE.md (セクション6)
   → 実例を参考にする

3. PBI-PRIORITIZATION-GUIDE.md (セクション7)
   → JIRAで管理する方法を確認
```

**合計トークン数:** 約15,000-20,000（部分読みの場合）

---

### シナリオ3: 「大きすぎるPBIを分割したい」

**推奨読了順:**
```
1. PBI-SPLITTING-GUIDE.md (セクション2)
   → 分割パターンを学習

2. PBI-SPLITTING-GUIDE.md (セクション3)
   → 該当するPBIタイプの分割戦略

3. PBI-SPLITTING-GUIDE.md (セクション4)
   → 実例を参考にする

4. PBI-CREATION-BEST-PRACTICES.md (該当タイプ)
   → 分割後のPBI作成
```

**合計トークン数:** 約15,000-20,000

---

### シナリオ4: 「PBIタイプの判定に迷っている」

**推奨読了順:**
```
1. PBI-TYPE-JUDGMENT-GUIDE.md (セクション3)
   → 各タイプの詳細判定基準

2. PBI-TYPE-JUDGMENT-GUIDE.md (セクション4)
   → 境界ケース判定ルール

3. PBI-TYPE-JUDGMENT-GUIDE.md (セクション5)
   → 判定フローチャート
```

**合計トークン数:** 約10,000-15,000（部分読みの場合）

---

### シナリオ5: 「JIRAでPBI管理を始めたい」

**推奨読了順:**
```
1. PBI-JIRA-MANAGEMENT-GUIDE.md (セクション2)
   → JIRA初期設定

2. PBI-JIRA-MANAGEMENT-GUIDE.md (セクション3-4)
   → ワークフローとカスタムフィールド

3. PBI-JIRA-MANAGEMENT-GUIDE.md (セクション7)
   → JQLクエリ集

4. PBI-PRIORITIZATION-GUIDE.md (セクション7)
   → 優先順位管理の自動化
```

**合計トークン数:** 約15,000-20,000

---

### シナリオ6: 「技術的負債と新機能のバランスを取りたい」

**推奨読了順:**
```
1. PBI-TYPE-JUDGMENT-GUIDE.md (セクション3.5-3.6)
   → REF/ARCタイプの理解

2. PBI-PRIORITIZATION-GUIDE.md (セクション4: ARC/REF戦略)
   → 技術的ROIの計算方法

3. PBI-PRIORITIZATION-GUIDE.md (セクション9: 失敗2)
   → 20%ルールの適用

4. PBI-CREATION-BEST-PRACTICES.md (REF/ARCセクション)
   → 技術的負債PBIの作成
```

**合計トークン数:** 約20,000-25,000

---

## 🤖 AIエージェント向け利用ガイド

### 読み込み戦略（モデル別）

#### GPT-4 (8K) の場合
**推奨:** 小〜中サイズのドキュメントのみ、または部分読み

```
✅ 単独で読み込み可能:
- PBI-JIRA-MANAGEMENT-GUIDE.md (約10K)
- PBI-TEMPLATE-CATALOG.md (約10K)
- PBI-SPLITTING-GUIDE.md (約12K)

⚠️ 部分読みを推奨:
- PBI-TYPE-JUDGMENT-GUIDE.md → セクション3-5のみ
- PBI-CREATION-BEST-PRACTICES.md → 該当タイプのみ
- PBI-PRIORITIZATION-GUIDE.md → 該当セクションのみ

❌ 全体読み込みは困難:
- 大きいドキュメント3つ
```

#### GPT-4 (32K) の場合
**推奨:** 全ドキュメント単独読み込み可能、2-3個同時可能

```
✅ 単独で読み込み可能: 全ドキュメント

✅ 同時読み込み可能:
- 小サイズ × 3個
- 中サイズ × 2個
- 大サイズ × 1個 + 小サイズ × 1個

⚠️ 全6個同時は制限超過 (156%)
```

#### Claude (100K) / Gemini (1M) の場合
**推奨:** 全ドキュメント同時読み込み可能

```
✅ 全ての組み合わせで問題なし
- 全6個同時読み込み: 約150K (制限の50%以内)
- 余裕を持った運用が可能
```

---

### トークン数一覧（AI読み込み計画用）

| ドキュメント | トークン数 | GPT-4 8K | GPT-4 32K | Claude 100K |
|-------------|-----------|---------|-----------|-------------|
| **AIプロンプト集** |
| PBI-AI-PROMPT-COLLECTION.md | 19,000 | 79% | 20% | 6% |
| **Quick Reference** |
| PBI-PRIORITIZATION-GUIDE-QUICK-REF.md | 2,500 | 10% | 3% | 1% |
| PBI-CREATION-BEST-PRACTICES-QUICK-REF.md | 3,500 | 15% | 4% | 1% |
| **完全版** |
| PBI-JIRA-MANAGEMENT-GUIDE.md | 9,676 | 40% | 10% | 3% |
| PBI-TEMPLATE-CATALOG.md | 10,116 | 42% | 11% | 3% |
| PBI-SPLITTING-GUIDE.md | 12,452 | 52% | 13% | 4% |
| PBI-TYPE-JUDGMENT-GUIDE.md | 26,407 | 110% | 28% | 9% |
| PBI-CREATION-BEST-PRACTICES.md | 44,895 | 187% | 47% | 15% |
| PBI-PRIORITIZATION-GUIDE.md | 46,930 | 196% | 49% | 16% |
| **合計（AIプロンプト集）** | **19,000** | **79%** | **20%** | **6%** |
| **合計（Quick Ref）** | **6,000** | **25%** | **6%** | **2%** |
| **合計（完全版）** | **150,476** | **627%** | **157%** | **50%** |
| **総計** | **175,476** | **731%** | **183%** | **58%** |

---

## 📖 セクション別クイックリファレンス

### 優先順位付け手法の選択

| 状況 | 推奨手法 | 参照先 |
|------|---------|--------|
| データが豊富 | **RICE評価** | PBI-PRIORITIZATION-GUIDE.md § 3.2 |
| シンプルな分類 | **MoSCoW法** | PBI-PRIORITIZATION-GUIDE.md § 3.1 |
| SAFe環境 | **WSJF** | PBI-PRIORITIZATION-GUIDE.md § 3.3 |
| 顧客満足度重視 | **Kano分析** | PBI-PRIORITIZATION-GUIDE.md § 3.4 |
| 視覚的判断 | **Value vs Effort** | PBI-PRIORITIZATION-GUIDE.md § 3.5 |

---

### PBI分割パターンの選択

| 状況 | 推奨パターン | 参照先 |
|------|------------|--------|
| 大きすぎるストーリー | **横分割** | PBI-SPLITTING-GUIDE.md § 2.1 |
| 複数の機能を含む | **縦分割** | PBI-SPLITTING-GUIDE.md § 2.2 |
| 実装順序が明確 | **ステップ分割** | PBI-SPLITTING-GUIDE.md § 2.4 |
| 技術的複雑性が高い | **技術スパイク分離** | PBI-SPLITTING-GUIDE.md § 2.7 |

---

### PBIタイプ判定の境界ケース

| 迷うケース | 判定基準 | 参照先 |
|-----------|---------|--------|
| NFD vs ENH | 新しいユースケースか？ | PBI-TYPE-JUDGMENT-GUIDE.md § 4.1 |
| ENH vs REF | 外部仕様の変更があるか？ | PBI-TYPE-JUDGMENT-GUIDE.md § 4.2 |
| BUG vs HOT | 24時間以内の対応が必要か？ | PBI-TYPE-JUDGMENT-GUIDE.md § 4.3 |
| ARC vs REF | アーキテクチャレベルの変更か？ | PBI-TYPE-JUDGMENT-GUIDE.md § 4.4 |

---

## 🔗 関連ドキュメント

### AI開発ワークフロー
- **[AI-MASTER-WORKFLOW-GUIDE.md](../02-ai-guides/AI-MASTER-WORKFLOW-GUIDE.md)**
  - Phase 0-6の詳細な開発フロー
  - PBIタイプごとのPhase経路

### AI開発成果物
- **[AI-DELIVERABLE-REFERENCE-GUIDE.md](../02-ai-guides/AI-DELIVERABLE-REFERENCE-GUIDE.md)**
  - 各Phaseで作成する成果物の詳細

### AIプロンプト集
- **[PBI-AI-PROMPT-COLLECTION.md](./templates-prompts/PBI-AI-PROMPT-COLLECTION.md)**
  - AIにPBI作成を依頼するためのプロンプトテンプレート集
  - 8つのPBIタイプ別プロンプト
  - タスク別プロンプト（優先順位評価、分割、レビュー）
  - AIエージェント設定用システムプロンプト

### テンプレートファイル
- **`/08-templates/pbi-templates/`**
  - 8つのPBIタイプ別テンプレート（.mdファイル）
  - README.md（テンプレート使用方法）

---

## 📊 ドキュメント更新履歴

| 日付 | ドキュメント | 更新内容 |
|------|-------------|---------|
| 2025-11-17 | PBI-AI-PROMPT-COLLECTION.md | 新規作成（AIプロンプト集） |
| 2025-11-17 | PBI-PRIORITIZATION-GUIDE-QUICK-REF.md | 新規作成（Quick Reference） |
| 2025-11-17 | PBI-CREATION-BEST-PRACTICES-QUICK-REF.md | 新規作成（Quick Reference） |
| 2025-11-17 | PBI-MASTER-INDEX.md | Quick ReferenceとAIプロンプト集追加 |
| 2025-11-17 | PBI-PRIORITIZATION-GUIDE.md | 新規作成（Phase 3） |
| 2025-11-17 | PBI-SPLITTING-GUIDE.md | 新規作成（Phase 2-1） |
| 2025-11-17 | PBI-JIRA-MANAGEMENT-GUIDE.md | 新規作成（Phase 2-2） |
| 2025-11-17 | PBI-MASTER-INDEX.md | 新規作成（マスターインデックス） |
| 2025-11-14 | PBI-CREATION-BEST-PRACTICES.md | 新規作成（Phase 1-1） |
| 2025-11-14 | PBI-TEMPLATE-CATALOG.md | 新規作成（Phase 1-2） |
| 2025-11-14 | PBI-TYPE-JUDGMENT-GUIDE.md | 既存ドキュメント |

---

## 💡 使い方のヒント

### 初めて読む方へ
1. **このマスターインデックスから開始** → 全体像を把握
2. **シナリオ別ガイドを確認** → 自分の状況に合ったドキュメントを特定
3. **推奨読了順に従って読む** → 効率的に学習

### AIエージェントの方へ
1. **トークン数一覧を確認** → 読み込み計画を立てる
2. **部分読みを活用** → 必要なセクションのみを読む
3. **シナリオ別ガイドを参照** → 効率的なドキュメント選択

### チームで使う場合
1. **役割別に推奨ドキュメントを共有**
   - Product Owner → PRIORITIZATION, CREATION
   - 開発チーム → TYPE-JUDGMENT, SPLITTING, JIRA
   - AIエージェント → 全ドキュメント（部分読み）

2. **定期的なレビュー会議で使用**
   - バックログリファインメント → PRIORITIZATION
   - スプリント計画 → SPLITTING
   - レトロスペクティブ → CREATION

---

## 📞 フィードバック・改善提案

このマスターインデックスやPBIドキュメント群へのフィードバックは、以下の方法でお寄せください：

- **JIRA**: 改善提案をPBIとして起票
- **Confluence**: コメント機能での意見提供
- **チームミーティング**: バックログリファインメント会議での議論

---

**最終更新:** 2025-11-17  
**作成者:** AI Agent  
**バージョン:** 1.0.0
