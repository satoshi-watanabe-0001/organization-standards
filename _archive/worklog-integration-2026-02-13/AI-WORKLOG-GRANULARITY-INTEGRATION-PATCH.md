---
document_type: integration_patch
target_files: AI-WORKLOG-ENFORCEMENT-GUIDE.md, AI-WORKLOG-IMPLEMENTATION-GUIDE.md, README.md
created_date: 2026-02-13
purpose: GRANULARITY-GUIDEへの参照を既存ガイドに追加
---

# AI-WORKLOG-GRANULARITY-GUIDE 統合パッチ

## メタデータ
- document_type: integration_patch
- target_files: AI-WORKLOG-ENFORCEMENT-GUIDE.md, AI-WORKLOG-IMPLEMENTATION-GUIDE.md, README.md
- created_date: 2026-02-13
- purpose: GRANULARITY-GUIDEへの参照を既存ガイドに追加

## パッチ1: AI-WORKLOG-ENFORCEMENT-GUIDE.md

### 追加箇所: セクション1.3の後に新セクション1.4を追加

```markdown
### 1.4 作業ログの単位・粒度

作業ログの分割は**推論コンテキスト**を基準とします。

**基本原則**: 1つの推論コンテキスト = 1つの作業ログ

**分割判断の基準**:
- 推論が独立している → **分割**
- 推論が連続・相互依存している → **統合**
- 前提条件が変化する → **分割**
- 評価観点が異なる → **分割**

**推奨粒度**:
- 作業時間: 2-8時間（小粒度推奨）
- 推論深度: 1-3段階の意思決定
- 成果物: 1-3個のファイル/モジュール

**詳細な分割基準とベストプラクティスは以下を参照**:
→ [AI-WORKLOG-GRANULARITY-GUIDE.md](./AI-WORKLOG-GRANULARITY-GUIDE.md)

**重要**: 
- 3日以上かかる作業は必ず分割してください
- 1つの要求仕様書が複数の作業ログに分かれることは推奨されています
- 推論の評価可能性を最優先に考えてください
```

---

## パッチ2: AI-WORKLOG-IMPLEMENTATION-GUIDE.md

### 追加箇所: セクション4.3「保存パスの運用」の後に参照を追加

```markdown
### 4.4 作業ログの粒度管理

作業ログの分割単位は推論の評価可能性に直結します。

**基本方針**:
- 推論コンテキストを基準とした分割
- 小粒度（2-8時間）を強く推奨
- 大粒度（3日以上）は必ず分割

**詳細なガイドライン**:
→ [AI-WORKLOG-GRANULARITY-GUIDE.md](./AI-WORKLOG-GRANULARITY-GUIDE.md)

このガイドラインには以下の内容が含まれます:
- 具体的な分割判断基準（5つの質問）
- ケーススタディと実装例
- 推論評価の観点と品質基準
- ベストプラクティスとアンチパターン
```

---

## パッチ3: README.md

### 追加箇所: 重要ドキュメントリストに追加

```markdown
### 重要な参照ドキュメント（優先順位順）

1. **AI-WORKLOG-ENFORCEMENT-GUIDE.md** (必須)
   - 作業開始前の必須手順と3段階STOP-GATE

2. **AI-WORKLOG-GRANULARITY-GUIDE.md** (必須) ← **NEW**
   - 作業ログの単位・粒度の判断基準
   - 推論コンテキストに基づく分割方法
   - ケーススタディと実装例

3. **AI-WORKLOG-IMPLEMENTATION-GUIDE.md** (高)
   - 作業ログシステムの実装・運用ガイド

4. **template_worklog.md** (必須)
   - 作業ログの標準テンプレート
```

### 追加箇所: 「作業開始前の確認事項」セクションに追加

```markdown
> 📋 **作業ログの適切な粒度**
> 
> 1つの要求仕様書が複数の作業ログに分かれることは推奨されています。
> 推論コンテキストを基準に適切に分割してください。
> 
> **判断基準**: [AI-WORKLOG-GRANULARITY-GUIDE.md](./AI-WORKLOG-GRANULARITY-GUIDE.md)
```

---

## パッチ適用手順

### ステップ1: バックアップ作成
```bash
cd /organization-standards/00-guides/02-ai-guides
cp AI-WORKLOG-ENFORCEMENT-GUIDE.md AI-WORKLOG-ENFORCEMENT-GUIDE.md.pre-granularity-patch
cp AI-WORKLOG-IMPLEMENTATION-GUIDE.md AI-WORKLOG-IMPLEMENTATION-GUIDE.md.pre-granularity-patch
cp README.md README.md.pre-granularity-patch
```

### ステップ2: パッチ適用
上記の各パッチ内容を対応するファイルに追加

### ステップ3: 検証
```bash
# リンク確認
grep -r "AI-WORKLOG-GRANULARITY-GUIDE.md" .

# 想定される結果:
# AI-WORKLOG-ENFORCEMENT-GUIDE.md に1箇所
# AI-WORKLOG-IMPLEMENTATION-GUIDE.md に1箇所
# README.md に2箇所
```

---

## 統合後のドキュメント構成

```
organization-standards/00-guides/02-ai-guides/
├── README.md (統合済み)
│   └── → AI-WORKLOG-GRANULARITY-GUIDE.md への参照
├── AI-WORKLOG-ENFORCEMENT-GUIDE.md (統合済み)
│   └── → AI-WORKLOG-GRANULARITY-GUIDE.md への参照
├── AI-WORKLOG-IMPLEMENTATION-GUIDE.md (統合済み)
│   └── → AI-WORKLOG-GRANULARITY-GUIDE.md への参照
├── AI-WORKLOG-GRANULARITY-GUIDE.md (新規作成済み)
└── template_worklog.md
```

---

## 期待される効果

### 統合前の問題
- ❌ 作業ログの粒度が不明確
- ❌ 分割基準がAIエージェントごとにばらつく
- ❌ 推論評価が困難

### 統合後の改善
- ✅ 明確な分割基準の提供
- ✅ 推論コンテキストに基づく一貫した粒度
- ✅ 推論評価の効率化

---

## 完了チェックリスト

- [ ] パッチ1: ENFORCEMENT-GUIDEに参照追加
- [ ] パッチ2: IMPLEMENTATION-GUIDEに参照追加
- [ ] パッチ3: READMEに参照追加
- [ ] 全ファイルのリンク確認
- [ ] バックアップファイルの保存確認
- [ ] 統合完了報告の作成

---

**作成日時**: 2026-02-13
**適用状況**: 準備完了（適用待ち）
**次のアクション**: ユーザー承認後、パッチ適用実行