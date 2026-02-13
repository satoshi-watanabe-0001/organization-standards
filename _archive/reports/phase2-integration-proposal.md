# Phase 2.1/2.2 統合提案 - 更新計画

## 📋 基本方針

**採用する考え方**: `ai-developer-document-workflow.md` の「設計を2つに分ける（Phase 2.1/2.2）」アプローチ

**統合の目的**:
1. Phase 2.1/2.2 の考え方を既存のドキュメント体系に統合
2. ドキュメント全体の一貫性を確保
3. AI開発者が迷わずに参照できる構造を実現

---

## 🎯 更新が必要なドキュメント一覧

### レベル1: 最優先（マスタードキュメント）

| # | ドキュメント | 場所 | 更新内容 | 優先度 |
|---|------------|------|---------|--------|
| 1 | **AI-MASTER-WORKFLOW-GUIDE.md** | `/00-guides/` | Phase 2 セクションを 2.1/2.2 に分割 | 🔴 最高 |
| 2 | **トップREADME.md** | `/` | Phase 2.1/2.2 への言及追加、フロー図更新 | 🔴 最高 |
| 3 | **00-guides/README.md** | `/00-guides/` | phase-guides の説明を 2.1/2.2 対応に更新 | 🟠 高 |

### レベル2: 重要（フェーズガイド）

| # | ドキュメント | 場所 | 更新内容 | 優先度 |
|---|------------|------|---------|--------|
| 4 | **phase-2-design-guide.md** | `/00-guides/phase-guides/` | 2.1/2.2 統合版に更新 OR 非推奨化 | 🟠 高 |
| 5 | **phase-guides/README.md** | `/00-guides/phase-guides/` | Phase 2 の説明を 2.1/2.2 に更新 | 🟡 中 |

### レベル3: 補完（参照ドキュメント）

| # | ドキュメント | 場所 | 更新内容 | 優先度 |
|---|------------|------|---------|--------|
| 6 | **DOCUMENT-USAGE-MANUAL.md** | `/00-guides/` | Phase 2.1/2.2 への参照を追加 | 🟡 中 |
| 7 | **03-development-process/README.md** | `/03-development-process/` | Phase 2.1/2.2 の説明を追加 | 🟢 低 |

### レベル4: 後処理

| # | ドキュメント | 場所 | アクション | 優先度 |
|---|------------|------|----------|--------|
| 8 | **ai-developer-document-workflow.md** | `/00-guides/` | アーカイブに移動（役割完了） | 🟢 低 |

---

## 📝 詳細な更新計画

### 更新1: AI-MASTER-WORKFLOW-GUIDE.md（最優先）

#### 現在の状態
```markdown
## Phase 2: 設計 (Design)
期間: 3-7日
成果物:
  - アーキテクチャ設計書
  - API仕様書
  - データモデル設計
```

#### 更新後の構造
```markdown
## Phase 2: 設計 (Design) - 改訂版 ⭐

### ⚠️ 重要な変更点
Phase 2 は Phase 2.1（実装前設計）と Phase 2.2（実装後設計）に分割されました。

### Phase 2 の実行順序について
**Phase番号 ≠ 実行順序** であることに注意してください：

```
実行順序:
Phase 0 → Phase 1 → Phase 2.1 → Phase 3 → Phase 4 → Phase 2.2 → Phase 5 → Phase 6
                    ↑ 実装前         ↑ 実装   ↑ レビュー ↑ 実装後
```

Phase 2.2 は「Phase 2」という番号ですが、実行順序では Phase 4 の後です。

---

### Phase 2.1: 実装前設計 (Pre-Implementation Design)

**実行タイミング**: Phase 1 の後、Phase 3 の前  
**期間**: 1-2日（最大3日）  
**目的**: 実装の方向性を定める最小限の設計

#### 成果物
1. **ADR (Architecture Decision Record)**
   - 重要な技術決定の記録
   - テンプレート: `/08-templates/adr-template.md`
   - 目安: 3-5ページ

2. **API契約書 (API Contract)**
   - チーム間のインターフェース契約
   - テンプレート: `/08-templates/api-contract-template.md`
   - 目安: 2-4ページ

3. **制約条件文書 (Constraints Document)**
   - セキュリティ、パフォーマンス要件
   - テンプレート: `/08-templates/constraints-template.md`
   - 目安: 1-3ページ

#### 詳細ガイド
📘 `phase-2.1-pre-implementation-design-guide.md`

#### AI活用のポイント
- ADR候補の自動生成
- 技術選択のトレードオフ分析
- OpenAPI仕様の雛形生成

---

### Phase 2.2: 実装後設計 (Post-Implementation Design)

**実行タイミング**: Phase 4 の後、Phase 5 の前（または Phase 3-4 と並行）  
**期間**: 2-3日  
**目的**: 実装内容の詳細な文書化

#### 実行パターン（3種類）

##### パターンA: AI最適化型（推奨）
```
Phase 2.1 → Phase 3 → Phase 4 → Phase 2.2（並行実施）
                      ↑
                Phase 3 実装中に 20-30% 並行進行
                Phase 4 完了時に 100% 完成
```
**適用**: 新規マイクロサービス、AI主導開発、中小規模
**効果**: リードタイム20%短縮、手戻り30%削減

##### パターンB: 従来型
```
Phase 2.1 → Phase 2.2（実装前詳細設計） → Phase 3 → Phase 4
```
**適用**: 複数チーム連携、セキュリティクリティカル、大規模
**効果**: リスク低減、事前合意による手戻り防止

##### パターンC: リバースエンジニアリング型
```
Phase 3 → Phase 4 → Phase 2.2（実装後のみ）
（Phase 2.1 スキップ）
```
**適用**: 小規模改修、技術検証、PoC
**効果**: 最速開始、検証優先

#### 成果物
1. **設計書 (Design Document)**
   - 詳細な技術設計
   - 目安: 10-30ページ

2. **完全版API仕様書**
   - OpenAPI 3.0形式
   - 全エンドポイントの詳細

3. **アーキテクチャ図**
   - C4モデル（Context, Container, Component, Code）
   - システム構成図

4. **データモデル文書**
   - ER図
   - スキーマ定義

5. **更新版ADR**
   - 実装中に追加された決定事項

#### 詳細ガイド
📘 `phase-2.2-post-implementation-design-guide.md`

#### AI活用のポイント
- コードから設計書を自動生成（55%工数削減）
- アーキテクチャ図の自動生成
- API仕様書の自動抽出

---

### パターン選択ガイド

#### PBIタイプ別の推奨パターン

```yaml
新規プロジェクト:
  pattern: "パターンA または B"
  decision:
    if 複数チーム連携 or セキュリティクリティカル:
      use: "パターンB"
    else:
      use: "パターンA (推奨)"

既存プロジェクトへの機能追加:
  pattern: "パターンA"
  note: "Phase 1 はスキップ"

バグ修正:
  pattern: "パターンC"
  note: "Phase 2.1, 2.2 ともにスキップ可能"

小規模改修・PoC:
  pattern: "パターンC"
  note: "Phase 2.1 スキップ、Phase 2.2 は簡易版"
```

---

### プロセス詳細リファレンス

詳細な説明は以下のドキュメントを参照：
- 📘 `revised-development-process-overview.md` - Phase 2.1/2.2 の詳細説明
- 📘 `revised-design-deliverables-matrix.md` - 成果物マトリクス

---

### PBIタイプ別の推奨フロー（更新版）

#### タイプ1: 新規プロジェクト立ち上げ
```
推奨フロー: Phase 0 → 1 → 2.1 → 3 → 4 → 2.2 → 5 → 6
パターン: A または B
```

#### タイプ2: 既存プロジェクトへの新機能追加
```
推奨フロー: Phase 0 → 2.1 → 3 → 4 → 2.2 → 5
            (Phase 1 スキップ)
パターン: A
```

#### タイプ3: バグ修正
```
推奨フロー: (Phase 0簡易) → 3 → 4
            (Phase 2.1, 2.2 スキップ)
パターン: C
```
```

#### 更新箇所
1. **Phase 2 セクション全体を書き換え**
2. **PBIタイプ別フローを Phase 2.1/2.2 対応に更新**
3. **フェーズスキップ判断にパターンA/B/C追加**

---

### 更新2: トップREADME.md（最優先）

#### 更新箇所1: クイックスタートのフロー図

**現在**:
```
Phase 0 → Phase 1 → Phase 2 → Phase 3 → Phase 4 → Phase 5 → Phase 6
```

**更新後**:
```
Phase 0 → Phase 1 → Phase 2.1 → Phase 3 → Phase 4 → Phase 2.2 → Phase 5 → Phase 6
                    (実装前設計)  (実装)   (レビュー) (実装後設計) (デプロイ)
                    1-2日        1-4週    3-5日     2-3日

⚠️ Phase 2.2 は Phase 4 の後に実行されます
```

#### 更新箇所2: AIエージェント向けクイックスタート

**追加する説明**:
```markdown
#### ⚠️ Phase 2 の重要な変更
Phase 2 は Phase 2.1（実装前設計）と Phase 2.2（実装後設計）に分割されました。

- **Phase 2.1**: Phase 1 の後、Phase 3 の前（1-2日）
- **Phase 2.2**: Phase 4 の後、Phase 5 の前（2-3日）
  - パターンA: Phase 3-4 と並行実施可能
  - パターンB: Phase 3 の前に詳細設計を完成
  - パターンC: Phase 4 の後に実装内容を文書化

詳細は `AI-MASTER-WORKFLOW-GUIDE.md` の Phase 2 セクションを参照。
```

#### 更新箇所3: 3層アーキテクチャの説明

**Layer 2 の説明を更新**:
```markdown
Layer 2: フェーズ別・AI活用ガイド（実行手順）
  
  📂 phase-guides/ (Phase 0-6のガイド)
     ├─ phase-0-requirements-planning-guide.md
     ├─ phase-1-project-initialization-guide.md
     ├─ phase-2.1-pre-implementation-design-guide.md ⭐NEW
     ├─ phase-2.2-post-implementation-design-guide.md ⭐NEW
     ├─ (phase-2-design-guide.md - 旧版、参照非推奨)
     ├─ phase-3-implementation-guide.md
     ├─ phase-4-review-qa-guide.md
     ├─ phase-5-deployment-guide.md
     └─ phase-6-operations-maintenance-guide.md
```

---

### 更新3: 00-guides/README.md（高優先度）

#### 更新箇所: 開発フェーズ別ガイドの説明

**現在**:
```markdown
### 📋 開発フェーズ別ガイド（7ファイル、124KB）

各フェーズガイドの概要:
- phase-0-requirements-planning-guide.md
- phase-1-project-initialization-guide.md
- phase-2-design-guide.md
- ...
```

**更新後**:
```markdown
### 📋 開発フェーズ別ガイド（8ファイル ⭐、約150KB）

#### ⚠️ Phase 2 の改訂について
Phase 2（設計）は Phase 2.1（実装前設計）と Phase 2.2（実装後設計）に分割されました：

- **phase-2.1-pre-implementation-design-guide.md** (16KB) ⭐NEW
  - 実行タイミング: Phase 1 の後、Phase 3 の前
  - 期間: 1-2日
  - 目的: 実装の方向性を定める最小限の設計

- **phase-2.2-post-implementation-design-guide.md** (19KB) ⭐NEW
  - 実行タイミング: Phase 4 の後、Phase 5 の前（または並行）
  - 期間: 2-3日
  - 目的: 実装内容の詳細な文書化

- **phase-2-design-guide.md** (21KB) ⚠️ 旧版
  - 参照非推奨。phase-2.1 と phase-2.2 を参照してください。

各フェーズガイドの一覧:
- phase-0-requirements-planning-guide.md (15KB)
- phase-1-project-initialization-guide.md (15KB)
- phase-2.1-pre-implementation-design-guide.md (16KB) ⭐NEW
- phase-2.2-post-implementation-design-guide.md (19KB) ⭐NEW
- phase-3-implementation-guide.md (69KB)
- phase-4-review-qa-guide.md (32KB)
- phase-5-deployment-guide.md (14KB)
- phase-6-operations-maintenance-guide.md (15KB)
```

---

### 更新4: phase-2-design-guide.md（高優先度）

#### オプションA: 非推奨化（推奨）

**ファイル冒頭に警告を追加**:
```markdown
---
version: "2.0.0"
last_updated: "2025-11-12"
status: "Deprecated" ⚠️
superseded_by: "phase-2.1-pre-implementation-design-guide.md, phase-2.2-post-implementation-design-guide.md"
---

# Phase 2: 設計ガイド

> ⚠️ **重要**: このガイドは廃止されました。Phase 2.1/2.2 版を参照してください。
> 
> ## 新しいガイド
> 
> Phase 2 は Phase 2.1（実装前設計）と Phase 2.2（実装後設計）に分割されました：
> 
> ### Phase 2.1: 実装前設計
> - **ガイド**: [phase-2.1-pre-implementation-design-guide.md](./phase-2.1-pre-implementation-design-guide.md)
> - **実行タイミング**: Phase 1 の後、Phase 3 の前
> - **期間**: 1-2日
> - **成果物**: ADR、API契約書、制約条件文書
> 
> ### Phase 2.2: 実装後設計
> - **ガイド**: [phase-2.2-post-implementation-design-guide.md](./phase-2.2-post-implementation-design-guide.md)
> - **実行タイミング**: Phase 4 の後、Phase 5 の前（または Phase 3-4 と並行）
> - **期間**: 2-3日
> - **成果物**: 設計書、完全版API仕様書、アーキテクチャ図、データモデル文書
> 
> ### プロセス詳細
> - **概要**: [/03-development-process/revised-development-process-overview.md](../../03-development-process/revised-development-process-overview.md)
> - **成果物マトリクス**: [/03-development-process/revised-design-deliverables-matrix.md](../../03-development-process/revised-design-deliverables-matrix.md)
> 
> ---
> 
> 以下は旧版の内容です（参考用）：
```

#### オプションB: 統合版に書き換え（代替案）

Phase 2.1 と Phase 2.2 の両方を含む統合ガイドに完全書き換え。
- 実行タイミングを明確に区別
- 3つのパターン（A/B/C）を説明
- 両方のフェーズの詳細手順を記載

---

### 更新5: phase-guides/README.md（中優先度）

#### 更新箇所: Phase 2 の説明

**追加する内容**:
```markdown
### Phase 2: 設計 (Design) - 改訂版 ⭐

#### ⚠️ 重要な変更
Phase 2 は Phase 2.1（実装前設計）と Phase 2.2（実装後設計）に分割されました。

#### Phase 2.1: 実装前設計
- **ファイル**: `phase-2.1-pre-implementation-design-guide.md`
- **実行順序**: Phase 1 → **Phase 2.1** → Phase 3
- **期間**: 1-2日
- **重要度**: ⭐⭐⭐⭐
- **成果物**: ADR、API契約書、制約条件文書

#### Phase 2.2: 実装後設計
- **ファイル**: `phase-2.2-post-implementation-design-guide.md`
- **実行順序**: Phase 4 → **Phase 2.2** → Phase 5
- **期間**: 2-3日
- **重要度**: ⭐⭐⭐⭐
- **成果物**: 設計書、完全版API仕様書、アーキテクチャ図、データモデル文書

#### 実行パターン
- **パターンA（推奨）**: Phase 2.2 を Phase 3-4 と並行実施
- **パターンB（従来型）**: Phase 2.2 を Phase 3 の前に実施
- **パターンC（リバース）**: Phase 2.2 を Phase 4 の後に実施

詳細は各ガイドを参照してください。
```

---

### 更新6: DOCUMENT-USAGE-MANUAL.md（中優先度）

#### 更新箇所: フェーズ別ドキュメント参照

**Phase 2 の説明を更新**:
```markdown
### Phase 2.1: 実装前設計
**参照ドキュメント**:
- 必須: phase-2.1-pre-implementation-design-guide.md
- テンプレート: adr-template.md, api-contract-template.md, constraints-template.md
- 参考: revised-development-process-overview.md

### Phase 2.2: 実装後設計
**参照ドキュメント**:
- 必須: phase-2.2-post-implementation-design-guide.md
- テンプレート: design-template.md
- 参考: revised-design-deliverables-matrix.md
```

---

### 更新7: 03-development-process/README.md（低優先度）

#### 追加箇所: Phase 2.1/2.2 への言及

**追加する内容**:
```markdown
### 設計プロセスの改訂

Phase 2（設計）は以下に分割されました：
- **Phase 2.1**: 実装前設計 (Pre-Implementation Design)
- **Phase 2.2**: 実装後設計 (Post-Implementation Design)

詳細は以下を参照：
- `revised-development-process-overview.md` - プロセス概要
- `revised-design-deliverables-matrix.md` - 成果物マトリクス
- `/00-guides/phase-guides/phase-2.1-pre-implementation-design-guide.md`
- `/00-guides/phase-guides/phase-2.2-post-implementation-design-guide.md`
```

---

### 更新8: ai-developer-document-workflow.md（後処理）

#### アクション: アーカイブに移動

**理由**:
- 役割完了（内容を既存ドキュメントに統合）
- Layer 1 のクリーン化
- 情報の一元管理

**移動先**:
```
/_archive/phase-2-renaming-2025-11-12/ai-developer-document-workflow.md
```

---

## 📊 更新の優先順位と依存関係

### フェーズ1: 基盤更新（必須）
```
1. AI-MASTER-WORKFLOW-GUIDE.md ← Phase 2セクション全面改訂
   ↓
2. トップREADME.md ← フロー図とクイックスタート更新
   ↓
3. 00-guides/README.md ← phase-guidesの説明更新
```

### フェーズ2: ガイド更新（重要）
```
4. phase-2-design-guide.md ← 非推奨化 OR 統合版書き換え
   ↓
5. phase-guides/README.md ← Phase 2.1/2.2の説明追加
```

### フェーズ3: 参照ドキュメント更新（補完）
```
6. DOCUMENT-USAGE-MANUAL.md ← Phase 2.1/2.2への参照追加
   ↓
7. 03-development-process/README.md ← Phase 2.1/2.2への言及
```

### フェーズ4: クリーンアップ（後処理）
```
8. ai-developer-document-workflow.md ← アーカイブに移動
```

---

## ✅ 更新完了チェックリスト

### フェーズ1: 基盤更新
- [ ] AI-MASTER-WORKFLOW-GUIDE.md の Phase 2 セクション更新
- [ ] AI-MASTER-WORKFLOW-GUIDE.md の PBIタイプ別フロー更新
- [ ] トップREADME.md のフロー図更新
- [ ] トップREADME.md のクイックスタート更新
- [ ] トップREADME.md の3層アーキテクチャ説明更新
- [ ] 00-guides/README.md の phase-guides 説明更新

### フェーズ2: ガイド更新
- [ ] phase-2-design-guide.md の非推奨化（または統合版書き換え）
- [ ] phase-guides/README.md の Phase 2 説明更新

### フェーズ3: 参照ドキュメント更新
- [ ] DOCUMENT-USAGE-MANUAL.md の Phase 2.1/2.2 参照追加
- [ ] 03-development-process/README.md の Phase 2.1/2.2 言及追加

### フェーズ4: クリーンアップ
- [ ] ai-developer-document-workflow.md をアーカイブに移動
- [ ] アーカイブREADME.md の更新

### 最終確認
- [ ] 全ドキュメントのリンク切れチェック
- [ ] Phase 2.1 → Phase 2.2 の実行順序が明確に説明されている
- [ ] AI開発者が迷わずに参照できる構造になっている
- [ ] ドキュメント体系の一貫性が保たれている

---

## 📝 推奨する実行順序

### ステップ1: 確認と承認（このステップ）
- [ ] この提案内容をレビュー
- [ ] 更新方針の承認
- [ ] phase-2-design-guide.md の扱い（非推奨化 or 書き換え）を決定

### ステップ2: フェーズ1の実行
- AI-MASTER-WORKFLOW-GUIDE.md の更新
- トップREADME.md の更新
- 00-guides/README.md の更新

### ステップ3: フェーズ2の実行
- phase-2-design-guide.md の更新
- phase-guides/README.md の更新

### ステップ4: フェーズ3の実行
- DOCUMENT-USAGE-MANUAL.md の更新
- 03-development-process/README.md の更新

### ステップ5: フェーズ4の実行
- ai-developer-document-workflow.md の移動
- 最終確認とリンクチェック

---

**作成日**: 2025-11-12  
**作成者**: AI Assistant  
**目的**: Phase 2.1/2.2 の考え方を既存ドキュメント体系に統合する詳細計画
