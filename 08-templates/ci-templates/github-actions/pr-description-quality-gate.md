# PR Description Quality Gate - 実装ガイド

## 📋 概要

このドキュメントは、**PR記載レベル自動チェック機能**の実装ガイドです。PR作成時に記載内容を自動検証し、組織標準の記載レベルに達していない場合はCI失敗として再レビューを促します。

---

## 🎯 目的

### 実現すること:
- ✅ PR作成時の記載内容を自動検証
- ✅ 記載レベル未達時のCI失敗とフィードバック
- ✅ 具体的な改善点の提示
- ✅ 自動ラベル管理（`pr-description-incomplete`）
- ✅ 日本語特化版PRテンプレートとの完全統合

### 背景:
> 「PR作成時の文言について、ある程度記載するべき内容を決めたうえでCIの中で自動チェックを行い、想定されている記載レベルに達していない場合はNGとして再度自己レビューを促す」（ユーザー要望）

この要望に対して、既存の日本語特化版PRテンプレート（`/08-templates/pr-templates/PULL_REQUEST_TEMPLATE.md`）を基盤として自動チェック機能を実装しました。

---

## 🔍 検証項目（Phase 1実装）

| # | 検証項目 | 基準 | エラー/警告 |
|---|---------|------|------------|
| 1 | **必須セクションの存在** | 7セクション全て存在 | ❌ エラー |
| 2 | **変更内容の概要の文字数** | 50文字以上 | ❌ エラー |
| 3 | **チェックボックス確認率** | 70%以上チェック済み | ❌ エラー（70%未満）<br>⚠️ 警告（70-85%） |
| 4 | **日本語記載** | 日本語文字を含む | ❌ エラー |
| 5 | **関連チケット番号** | `#数字` または `PBI-数字` 形式 | ⚠️ 警告 |
| 6 | **組織標準チェックリスト** | 重要4項目の確認状況 | ⚠️ 警告 |

### 必須セクション（7項目）:
1. 📋 変更内容の概要
2. 🎯 関連チケット
3. 🔍 変更の詳細
4. ✅ テストの実施状況
5. 🔒 セキュリティチェック
6. ✅ 組織標準チェックリスト
7. 📖 レビュアーへの補足

### 組織標準チェックリスト重要項目（4項目）:
- 日本語での記載を完了している
- コーディング規約に準拠している
- 全てのパブリックAPIにドキュメントコメントを記載している
- CI品質ゲートが全て通過している

---

## 🚀 実装手順

### Step 1: PRテンプレートの配置

#### オプション1: プロジェクトルートに配置（推奨）
```bash
cp /devin-organization-standards/08-templates/pr-templates/PULL_REQUEST_TEMPLATE.md \
   /your-project/.github/PULL_REQUEST_TEMPLATE.md
```

#### オプション2: 複数テンプレート使用時
```bash
mkdir -p /your-project/.github/PULL_REQUEST_TEMPLATE/
cp /devin-organization-standards/08-templates/pr-templates/PULL_REQUEST_TEMPLATE.md \
   /your-project/.github/PULL_REQUEST_TEMPLATE/default.md
```

### Step 2: CI品質ゲートの配置

#### 新規プロジェクトの場合:
```bash
mkdir -p /your-project/.github/workflows/
cp pr-description-quality-gate.yml \
   /your-project/.github/workflows/pr-description-quality-gate.yml
```

#### 既存プロジェクトの場合:
既存のPRチェックワークフローがある場合は、このジョブを追加統合することも可能です。

### Step 3: ラベルの作成

GitHubリポジトリに以下のラベルを作成してください:

| ラベル名 | 色 | 説明 |
|---------|-----|------|
| `pr-description-incomplete` | `#d73a4a` (赤) | PR説明が不完全 |

#### GitHub UIでの作成方法:
1. リポジトリ → Settings → Labels
2. "New label" をクリック
3. 上記の情報を入力して作成

#### GitHub CLIでの作成（自動化）:
```bash
gh label create "pr-description-incomplete" \
  --description "PR説明が不完全" \
  --color "d73a4a" \
  --repo owner/repo
```

### Step 4: 権限の確認

ワークフローが正常に動作するために、以下の権限が必要です:

```yaml
permissions:
  contents: read           # コードの読み取り
  pull-requests: write     # PRへのコメント、ラベル追加
  issues: write            # Issueへのコメント（PRも含む）
```

これらの権限は `pr-description-quality-gate.yml` に既に含まれています。

---

## 🔗 既存CI機能との統合

### 統合対象: PR自己レビューリマインダー

**場所**: `/devin-organization-standards/08-templates/ci-templates/github-actions/pr-self-review-reminder.yml`

**特徴**:
- AI自律型の自動ラベル管理
- チェックリスト確認状態の自動検出
- シンプルなCI（約150行）

### 統合方法: 2つのアプローチ

#### アプローチ1: 並行実行（推奨）

**特徴**: 各CIが独立して動作、明確な責任分離

```
PR作成/編集
  ├─ pr-description-quality-gate.yml
  │    └─ 記載レベル検証（構造、文字数、チェックボックス）
  │         └─ 失敗 → ラベル追加 + コメント投稿 + CI失敗
  │
  └─ pr-self-review-reminder.yml
       └─ AI自己レビュー確認（チェックリスト状態）
            └─ 未確認 → リマインダーコメント + ラベル追加
```

**メリット**:
- 各CIの役割が明確
- 段階的な改善が可能
- 既存のPR自己レビューリマインダーを変更不要

**デメリット**:
- 2つのCIが別々に実行される
- コメントが複数投稿される可能性

#### アプローチ2: 統合実行（高度）

**特徴**: 1つのCIワークフローに統合

```yaml
name: PR Quality Checks (Integrated)

jobs:
  quality-gate:
    steps:
      # 記載レベル検証
      - name: Validate PR description
        # ... (pr-description-quality-gateのロジック)
      
      # AI自己レビュー確認
      - name: Check self-review status
        # ... (pr-self-review-reminderのロジック)
      
      # 統合結果のコメント投稿
      - name: Post integrated feedback
        # ... (両方の結果をまとめて投稿)
```

**メリット**:
- 1つのコメントで全てのフィードバック
- CI実行回数の削減
- 統一されたユーザー体験

**デメリット**:
- 実装の複雑度が増加
- 既存CIの統合作業が必要

### 推奨: アプローチ1（並行実行）

**理由**:
- 既存の「PR自己レビューリマインダー」を変更せずに導入可能
- 段階的な改善とテストが容易
- 各CIの役割が明確で保守性が高い

---

## 📊 動作例

### ケース1: 記載レベル未達（エラー）

**PR説明の問題点**:
- 変更内容の概要: 30文字（50文字未満）
- チェックボックス: 3/10個確認（30%、70%未満）
- 必須セクション不足: 「セキュリティチェック」がない

**CIの動作**:
1. ❌ CI失敗ステータス
2. 🏷️ ラベル追加: `pr-description-incomplete`
3. 💬 コメント投稿:

```markdown
## 🚨 PR記載レベル品質ゲート: 改善が必要です

このPRの説明は、組織標準の記載レベルに達していません。以下の項目を改善してください。

### ❌ 必須改善項目

- **必須セクションが不足しています: セキュリティチェック**
  - 不足セクション:
    - `セキュリティチェック`

- **変更内容の概要が短すぎます（現在: 30文字、最低: 50文字）**
  - 現在: 30文字 → 必要: 50文字以上

- **チェックボックスの確認率が不足しています（現在: 30.0%、最低: 70%）**
  - 現在: 3/10 (30.0%) → 必要: 70%以上

### 📊 現在の記載状況

| 項目 | 値 | 状態 |
|------|-----|------|
| 変更内容の概要 | 30文字 | ❌ (最低50文字) |
| チェックボックス確認率 | 30.0% (3/10) | ❌ (最低70%) |

### 💡 改善方法

1. **PRテンプレートを使用**: `.github/PULL_REQUEST_TEMPLATE.md` を参照
2. **必須セクションを全て記載**: 各セクションの見出しと内容を含める
3. **変更内容を具体的に記載**: 最低50文字、推奨100文字以上
4. **チェックリストを確認**: 該当する項目に `[x]` でチェック
5. **日本語で記載**: 組織標準要件により必須

### 📚 関連ドキュメント

- [PRテンプレート](/08-templates/pr-templates/PULL_REQUEST_TEMPLATE.md)
- [PRテンプレート使用ガイド](/08-templates/pr-templates/README.md)
- [AI-PRE-WORK-CHECKLIST v1.3.0](/00-guides/AI-PRE-WORK-CHECKLIST.md) - Stage 3参照

---
*このチェックはPR説明を編集すると自動的に再実行されます。*
```

### ケース2: 記載レベル合格（成功）

**PR説明の状態**:
- 全ての必須セクションが存在
- 変更内容の概要: 120文字
- チェックボックス: 9/10個確認（90%）
- 日本語記載あり

**CIの動作**:
1. ✅ CI成功ステータス
2. 🏷️ ラベル削除: `pr-description-incomplete`（存在する場合）
3. 💬 コメント投稿:

```markdown
## ✅ PR記載レベル品質ゲート: 合格

このPRの説明は組織標準の記載レベルを満たしています。

### 📊 記載状況

| 項目 | 値 | 状態 |
|------|-----|------|
| 変更内容の概要 | 120文字 | ✅ |
| チェックボックス確認率 | 90.0% (9/10) | ✅ |

---
次のステップ: レビュアーからのフィードバックをお待ちください。
```

### ケース3: 警告あり（成功）

**PR説明の状態**:
- 全ての必須項目は満たしている
- チェックボックス: 7/10個確認（70%、基準ギリギリ）
- 関連チケット番号が不明確

**CIの動作**:
1. ✅ CI成功ステータス（基準は満たしている）
2. 💬 コメント投稿（警告項目を含む）:

```markdown
## ✅ PR記載レベル品質ゲート: 合格

このPRの説明は組織標準の記載レベルを満たしています。

### 📊 記載状況

| 項目 | 値 | 状態 |
|------|-----|------|
| 変更内容の概要 | 85文字 | ✅ |
| チェックボックス確認率 | 70.0% (7/10) | ✅ |

### ⚠️ 推奨改善項目（任意）

- チェックボックスの確認率が推奨値を下回っています（現在: 70.0%、推奨: 85%以上）
- 関連チケット番号が明確ではありません。形式: #123, PBI-456, Issue-789

---
次のステップ: レビュアーからのフィードバックをお待ちください。
```

---

## ⚙️ カスタマイズ

### 検証基準の調整

#### 1. 最低文字数の変更

**現在の基準**: 50文字

```javascript
// pr-description-quality-gate.yml の該当箇所
if (charCount < 50) {  // ← ここを変更
  results.passed = false;
  results.errors.push({
    type: 'insufficient-summary',
    message: `変更内容の概要が短すぎます（現在: ${charCount}文字、最低: 50文字）`,
    current: charCount,
    required: 50  // ← ここも変更
  });
}
```

**推奨値**:
- 小規模変更中心: 30文字
- 標準的なプロジェクト: **50文字**（デフォルト）
- 大規模・複雑なプロジェクト: 100文字

#### 2. チェックボックス確認率の変更

**現在の基準**: 70%（エラー）、85%（警告）

```javascript
// pr-description-quality-gate.yml の該当箇所
if (checkRate < 70) {  // ← エラー基準を変更
  results.passed = false;
  // ...
} else if (checkRate < 85) {  // ← 警告基準を変更
  results.warnings.push({
    // ...
  });
}
```

**推奨値**:
- 緩い基準: 60%（エラー）、75%（警告）
- 標準的な基準: **70%（エラー）、85%（警告）**（デフォルト）
- 厳しい基準: 80%（エラー）、90%（警告）

#### 3. 必須セクションの追加/削除

```javascript
// pr-description-quality-gate.yml の該当箇所
const requiredSections = [
  { name: '変更内容の概要', pattern: /##\s*📋\s*変更内容の概要/i },
  { name: '関連チケット', pattern: /##\s*🎯\s*関連チケット/i },
  // ... 他のセクション
  
  // 新しいセクションを追加する例:
  { name: 'パフォーマンス影響', pattern: /##\s*📊\s*パフォーマンス影響/i },
];
```

### プロジェクトタイプ別の推奨設定

#### フロントエンド重視プロジェクト:

```javascript
// 必須セクションに「スクリーンショット」を追加
const requiredSections = [
  // ... 既存のセクション
  { name: 'スクリーンショット', pattern: /##\s*📸\s*スクリーンショット/i },
];

// UI変更検出の追加チェック
const hasUIChanges = prBody.match(/\.(tsx?|jsx?|vue|svelte|css|scss|sass)/i);
if (hasUIChanges && !prBody.match(/!\[.*\]\(.*\)/)) {
  results.warnings.push({
    type: 'missing-screenshot',
    message: 'UI変更が含まれていますが、スクリーンショットがありません'
  });
}
```

#### セキュリティ重視プロジェクト:

```javascript
// セキュリティチェックリストの厳格化
const securitySection = prBody.match(/##\s*🔒\s*セキュリティチェック\s*\n+([\s\S]*?)(?=\n##|$)/i);
if (securitySection) {
  const securityCheckboxes = (securitySection[1].match(/- \[x\]/gi) || []).length;
  const totalSecurityItems = (securitySection[1].match(/- \[[ x]\]/gi) || []).length;
  
  if (securityCheckboxes < totalSecurityItems) {
    results.passed = false;
    results.errors.push({
      type: 'incomplete-security-check',
      message: 'セキュリティチェックリストを全て確認してください（100%必須）'
    });
  }
}
```

#### API開発プロジェクト:

```javascript
// APIドキュメントの必須化
const hasAPIChanges = prBody.match(/\/(api|routes|controllers|handlers)\//i);
if (hasAPIChanges) {
  const hasAPIDoc = prBody.match(/API.*ドキュメント|OpenAPI|Swagger/i);
  if (!hasAPIDoc) {
    results.errors.push({
      type: 'missing-api-doc',
      message: 'API変更が含まれていますが、APIドキュメントの更新が記載されていません'
    });
    results.passed = false;
  }
}
```

---

## 🧪 テスト方法

### ローカルテスト（推奨）

GitHub Actionsをローカルで実行できる `act` を使用:

```bash
# actのインストール（macOS）
brew install act

# actのインストール（Linux）
curl https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash

# PRイベントのシミュレーション
act pull_request -e test-pr-event.json
```

**test-pr-event.json の例**:
```json
{
  "pull_request": {
    "number": 123,
    "title": "テスト用PR",
    "body": "## 📋 変更内容の概要\n\nテスト用のPR説明です。この文字数は50文字以上になっています。\n\n## 🎯 関連チケット\n\n#456\n\n## 🔍 変更の詳細\n\n- 機能追加: ユーザー認証機能\n\n## ✅ テストの実施状況\n\n- [x] 単体テスト\n- [x] 統合テスト\n- [ ] E2Eテスト\n\n## 🔒 セキュリティチェック\n\n- [x] セキュリティスキャン実施\n\n## ✅ 組織標準チェックリスト\n\n- [x] 日本語での記載を完了している\n- [x] コーディング規約に準拠している\n\n## 📖 レビュアーへの補足\n\n特になし"
  }
}
```

### GitHub上でのテスト

1. テスト用ブランチを作成
2. テスト用PRを作成（意図的に不完全な説明）
3. CIの動作を確認
4. PR説明を編集して改善
5. CIが再実行され、成功することを確認

---

## 📚 関連ドキュメント

### devin-organization-standards内:
- **PRテンプレート**: `/08-templates/pr-templates/PULL_REQUEST_TEMPLATE.md` (5.9KB)
  - 日本語特化・CI統合版（推奨）
  
- **PRテンプレート使用ガイド**: `/08-templates/pr-templates/README.md` (10.1KB)
  - 配置方法、カスタマイズ例、FAQ
  
- **PR自己レビューリマインダー**: `/08-templates/ci-templates/github-actions/pr-self-review-reminder.yml`
  - AI自律型の既存CI（統合対象）
  
- **AI-PRE-WORK-CHECKLIST v1.3.0**: `/00-guides/AI-PRE-WORK-CHECKLIST.md` (33.2KB)
  - Stage 3: ポスト実装レビュー（PR作成前）
  - 自己レビューワークフロー

### Phase 4レビューガイド:
- **AI-PHASE-4-REVIEW-GUIDE.md**: `/00-guides/phase-guides/AI-PHASE-4-REVIEW-GUIDE.md`
  - PR作成時のレビュープロセス
  - 記載すべき内容の詳細ガイド

---

## 🔄 将来の拡張（Phase 2）

### 実装予定の高度な検証:

#### 1. セマンティック分析
- 変更内容と実際のコード差分の整合性チェック
- 自然言語処理による記載品質評価

#### 2. AIレビュー統合
- OpenAI/Gemini APIを使用した記載内容の評価
- 改善提案の自動生成

#### 3. 過去PRとの比較
- 同じ開発者の過去PRと比較
- プロジェクト平均との比較

#### 4. 動的基準調整
- プロジェクトの成熟度に応じた基準の自動調整
- 開発者の経験レベルに応じた基準

#### 5. メトリクス収集
- PR記載品質のトレンド分析
- チーム/個人別の統計情報

---

## ❓ FAQ

### Q1: CIが失敗し続ける場合は?

**A**: 以下を確認してください:

1. **PRテンプレートを使用しているか**
   - `.github/PULL_REQUEST_TEMPLATE.md` が配置されているか確認
   
2. **必須セクションの見出しが正確か**
   - 絵文字とテキストが完全一致する必要があります
   - 例: `## 📋 変更内容の概要`（正）vs `## 変更内容の概要`（誤）

3. **日本語で記載しているか**
   - 少なくとも一部は日本語で記載する必要があります

4. **チェックボックスを確認しているか**
   - `- [ ]` → `- [x]` に変更
   - 70%以上を確認済みにする

### Q2: 一部のPRでチェックをスキップしたい

**A**: PRラベルでスキップ機能を追加できます:

```yaml
# pr-description-quality-gate.yml の冒頭に追加
jobs:
  validate-pr-description:
    # 特定のラベルがある場合はスキップ
    if: |
      !contains(github.event.pull_request.labels.*.name, 'skip-description-check') &&
      !contains(github.event.pull_request.labels.*.name, 'bot')
    
    runs-on: ubuntu-latest
    # ... 以降は同じ
```

使用方法: PRに `skip-description-check` ラベルを追加

### Q3: 既存のPRに対しても実行される?

**A**: はい、トリガー設定により実行されます:

```yaml
on:
  pull_request:
    types: [opened, edited, synchronize, reopened]
```

- `opened`: PR作成時
- `edited`: PR説明編集時
- `synchronize`: コミット追加時
- `reopened`: クローズ後の再オープン時

不要な場合は `types` を `[opened, edited]` のみに変更してください。

### Q4: コメントが多すぎる

**A**: 既存のコメントを更新する方式なので、基本的には1つのコメントのみが維持されます。

もしコメントを完全に非表示にしたい場合:

```yaml
# コメント投稿のステップを削除またはコメントアウト
# - name: Post validation comment
#   if: steps.validate.outputs.passed == 'false'
#   uses: actions/github-script@v7
#   with:
#     script: |
#       # ... コメント投稿ロジック
```

この場合、ラベルとCI失敗のみでフィードバックされます。

### Q5: カスタムPRテンプレートを使用している

**A**: `requiredSections` の正規表現パターンをカスタマイズしてください:

```javascript
// あなたのテンプレートに合わせて変更
const requiredSections = [
  { name: 'Description', pattern: /##\s*Description/i },
  { name: 'Changes', pattern: /##\s*Changes/i },
  // ... あなたのセクション
];
```

---

## 📞 サポート

### 問題が発生した場合:

1. **GitHub Actions のログを確認**
   - PRページ → Checks → "PR Description Quality Gate" → 詳細ログ

2. **デバッグ情報を有効化**
   ```yaml
   # pr-description-quality-gate.yml に追加
   env:
     ACTIONS_STEP_DEBUG: true
   ```

3. **関連ドキュメントを参照**
   - PRテンプレート使用ガイド: `/08-templates/pr-templates/README.md`
   - AI-PRE-WORK-CHECKLIST: `/00-guides/AI-PRE-WORK-CHECKLIST.md`

4. **Issue作成**
   - テンプレートの改善提案
   - バグ報告

---

## 📊 効果測定

### 測定すべきメトリクス:

| メトリクス | 測定方法 | 目標値 |
|----------|---------|--------|
| PR記載品質の向上 | 初回レビューでの指摘事項数 | -30% |
| レビュー時間の短縮 | PR作成からマージまでの時間 | -20% |
| レビュー往復回数の削減 | コメント往復回数 | -25% |
| CI失敗率 | 記載レベルチェック失敗率 | <10% (1ヶ月後) |

### データ収集方法:

```bash
# GitHub APIを使用したデータ収集例
gh pr list --state all --json number,title,createdAt,mergedAt,comments

# 分析スクリプト（Python等）で処理
# - レビュー時間 = mergedAt - createdAt
# - コメント数 = len(comments)
```

---

## 🎓 ベストプラクティス

### 開発者向け:

1. **PRテンプレートを必ず使用する**
   - PR作成時に自動で読み込まれます
   
2. **各セクションを丁寧に記載する**
   - 最低文字数だけを満たすのではなく、レビュアーが理解できる内容を
   
3. **チェックリストを確認してから提出**
   - 自己レビューの一環として全項目を確認
   
4. **CI失敗時は指摘事項を確認**
   - コメントに具体的な改善点が記載されています

### レビュアー向け:

1. **記載レベルチェックは信頼する**
   - 基本的な記載品質はCIで保証されています
   
2. **内容の正確性と妥当性に注力**
   - 構造チェックはCIに任せ、内容の質に集中
   
3. **フィードバックは具体的に**
   - 良い例・悪い例を示す

### プロジェクトマネージャー向け:

1. **定期的に基準を見直す**
   - チームの成熟度に応じて調整
   
2. **効果測定を実施**
   - 1ヶ月ごとにメトリクスを確認
   
3. **チームにフィードバック**
   - 改善された点、まだ課題がある点を共有

---

## 📝 変更履歴

| バージョン | 日付 | 変更内容 |
|-----------|------|---------|
| 1.0.0 | 2025-11-11 | 初回リリース（Phase 1実装） |
|  |  | - 基本検証項目6つ実装 |
|  |  | - 自動ラベル管理 |
|  |  | - 詳細コメント投稿 |

---

## 🚀 まとめ

このPR記載レベル自動チェック機能により、以下が実現されます:

✅ **組織標準の記載レベル遵守の自動化**
✅ **レビュアーの負担軽減**（構造チェックはCIに委任）
✅ **具体的な改善点の即座のフィードバック**
✅ **段階的な品質向上**（エラー/警告の2段階）
✅ **既存CI（PR自己レビューリマインダー）との統合**

次のステップ: プロジェクトに配置してテスト運用を開始してください!
