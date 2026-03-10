# PR言語チェック CI 設定ガイド

## 📋 目次
- [概要](#概要)
- [ファイル情報](#ファイル情報)
- [機能](#機能)
- [配置方法](#配置方法)
- [動作確認](#動作確認)
- [カスタマイズ](#カスタマイズ)
- [トラブルシューティング](#トラブルシューティング)
- [FAQ](#faq)

---

## 概要

このCI品質ゲートは、プルリクエスト（PR）のタイトルと説明文が**日本語で記載されているか**を自動検証します。

### 目的
- **日本語記載の強制**: 組織標準として日本語でのPR作成を必須化
- **自動検証**: CI品質ゲートで自動的にチェック
- **マージ防止**: 英語PRはマージ不可（CI失敗）
- **ガイダンス**: botコメントで修正方法を自動提示

### 効果
- ✅ 英語PRは自動的にCI失敗 → マージ不可
- ✅ botが修正方法を自動提示 → 手動対応不要
- ✅ チーム全体のコミュニケーション効率向上
- ✅ レビュー時の理解速度向上

---

## ファイル情報

### ファイル名
`pr-language-check.yaml`

### 配置場所
```
<project-root>/.github/workflows/pr-language-check.yaml
```

### サイズ
約4.5KB

### 対応プラットフォーム
- GitHub Actions

### 言語サポート
- 日本語（ひらがな、カタカナ、漢字）の検出
- 技術用語（API、JWT等）の英語表記は許可

---

## 機能

### 1. PR言語検証

#### 検証項目
- ✅ PRタイトルに日本語文字が含まれているか
- ✅ PR説明文に日本語文字が含まれているか
- ✅ PR説明文が空でないか

#### 検証ロジック
```javascript
// 日本語文字（ひらがな、カタカナ、漢字）の正規表現
const japaneseRegex = /[\u3040-\u309F\u30A0-\u30FF\u4E00-\u9FAF]/;

// タイトルと説明文を検証
const hasTitleJapanese = japaneseRegex.test(prTitle);
const hasBodyJapanese = japaneseRegex.test(prBody);
```

---

### 2. 自動botコメント

#### 失敗時のコメント内容
1. **問題点の明示**
   - どの項目が日本語でないか（タイトル/説明文）
   
2. **対処方法の提示**
   - オプション1: PRを修正する（推奨）
   - オプション2: PRを再作成する
   
3. **良い例・悪い例**
   - 日本語記載の正しい例
   - 英語記載の誤った例
   
4. **関連ドキュメント**
   - PRテンプレート
   - Phase 4レビューガイド
   - PR言語問題の解決策

#### 成功時のコメント内容
- ✅ 合格メッセージ
- 次のステップの案内

---

### 3. CI失敗によるマージ防止

#### マージ防止の仕組み
```yaml
# CI失敗
core.setFailed('❌ PR must be written in Japanese');

# GitHubのブランチ保護ルールと連携
# → "Require status checks to pass before merging" が有効な場合
# → このCIが失敗していると、マージボタンが無効化される
```

---

## 配置方法

### Step 1: ファイルのコピー

#### 方法1: 手動コピー
```bash
# プロジェクトのルートディレクトリに移動
cd <project-root>

# .github/workflows/ ディレクトリを作成（存在しない場合）
mkdir -p .github/workflows

# テンプレートからコピー
cp /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/pr-language-check.yaml \
   .github/workflows/pr-language-check.yaml
```

#### 方法2: 直接作成
```bash
# プロジェクトのルートディレクトリに移動
cd <project-root>

# .github/workflows/ ディレクトリを作成
mkdir -p .github/workflows

# ファイルを作成（内容は pr-language-check.yaml を参照）
vim .github/workflows/pr-language-check.yaml
```

---

### Step 2: コミット＆プッシュ

```bash
# ファイルをステージング
git add .github/workflows/pr-language-check.yaml

# コミット
git commit -m "feat: PR言語チェックのCI品質ゲートを追加

- PRタイトル・説明文の日本語記載を自動検証
- 英語記載の場合はCI失敗でマージ防止
- botコメントで修正方法を自動提示

参照: devin-organization-standards/00-guides/PR-LANGUAGE-ISSUE-SOLUTION.md"

# プッシュ
git push origin main
```

---

### Step 3: ブランチ保護ルールの設定（推奨）

#### GitHubでの設定方法

1. **リポジトリ設定に移動**
   - Settings → Branches

2. **ブランチ保護ルールを追加**
   - Add branch protection rule

3. **ブランチ名パターンを指定**
   - Branch name pattern: `main` または `develop`

4. **必須ステータスチェックを有効化**
   - ✅ Require status checks to pass before merging
   - ✅ Require branches to be up to date before merging
   - ステータスチェック選択: `日本語記載チェック` または `PR Language Check`

5. **保存**
   - Save changes

これにより、**このCIが通過しない限り、PRをマージできなくなります**。

---

## 動作確認

### Step 1: テストブランチの作成

```bash
# 新しいブランチを作成
git checkout -b test/pr-language-check

# 簡単な変更を加える
echo "# PR言語チェックのテスト" >> test.md
git add test.md
git commit -m "test: PR言語チェックのテスト"

# プッシュ
git push origin test/pr-language-check
```

---

### Step 2: 英語PRを作成（失敗テスト）

1. **GitHub UIでPR作成**
   - New pull request

2. **英語でPRを記載**
   ```markdown
   Title: Test PR language check
   
   Description:
   This is a test PR to verify the language check CI.
   ```

3. **PR作成**
   - Create pull request

4. **CI実行を待つ**（数秒～数十秒）

5. **結果確認**
   - ❌ CI「PR Language Check」が失敗
   - 🤖 botコメントが投稿される
   - 🚫 マージボタンが無効化（ブランチ保護ルール有効時）

---

### Step 3: 日本語に修正（成功テスト）

1. **PRを編集**
   - Edit ボタンをクリック

2. **日本語に修正**
   ```markdown
   タイトル: PR言語チェックのテスト
   
   ## 説明
   PR言語チェックCIの動作を確認するためのテストPRです。
   ```

3. **保存**

4. **CI自動再実行を待つ**（数秒～数十秒）

5. **結果確認**
   - ✅ CI「PR Language Check」が合格
   - 🤖 botコメント（成功メッセージ）が投稿される
   - ✅ マージボタンが有効化

---

### Step 4: クリーンアップ

```bash
# テストブランチを削除
git branch -D test/pr-language-check
git push origin --delete test/pr-language-check

# テストPRをクローズ
# （GitHub UIで手動クローズ）
```

---

## カスタマイズ

### 1. トリガーイベントのカスタマイズ

#### デフォルト設定
```yaml
on:
  pull_request:
    types: [opened, edited, synchronize, reopened]
```

#### カスタマイズ例1: 特定ブランチのみ対象
```yaml
on:
  pull_request:
    types: [opened, edited, synchronize, reopened]
    branches:
      - main
      - develop
```

#### カスタマイズ例2: 特定パスのみ対象
```yaml
on:
  pull_request:
    types: [opened, edited, synchronize, reopened]
    paths:
      - 'src/**'
      - 'docs/**'
```

---

### 2. 検証ロジックのカスタマイズ

#### 日本語検出の厳格化
```javascript
// 最低文字数を要求（例: 10文字以上）
const japaneseChars = (prBody.match(japaneseRegex) || []).length;
if (japaneseChars < 10) {
  core.setFailed('❌ PR description must contain at least 10 Japanese characters');
}
```

#### 英語の割合を制限
```javascript
// 英語の割合が50%以下であることを要求
const totalChars = prBody.length;
const japaneseChars = (prBody.match(japaneseRegex) || []).length;
const japaneseRatio = japaneseChars / totalChars;

if (japaneseRatio < 0.5) {
  core.setFailed('❌ PR description must be primarily in Japanese (>50%)');
}
```

---

### 3. botコメントのカスタマイズ

#### プロジェクト固有の情報を追加
```javascript
body: `## ❌ PR言語チェック失敗

**このPRは組織標準に準拠していません。**

### 🚨 問題点
${missingParts.map(part => \`- \${part}\`).join('\\n')}

### ✅ 対処方法
...

### 📖 プロジェクト固有の参照
- [プロジェクトPRガイド](docs/pr-guide.md)
- [チーム連絡先](docs/team-contacts.md)

**このCIチェックが通過するまで、PRはマージできません。**`
```

---

### 4. 例外ルールの追加

#### 特定のラベルで検証をスキップ
```javascript
// PRにラベル "skip-language-check" が付いている場合はスキップ
const labels = context.payload.pull_request.labels.map(label => label.name);
if (labels.includes('skip-language-check')) {
  console.log('⏭️ Skipping language check (label: skip-language-check)');
  return;
}
```

#### 特定のユーザーを除外
```javascript
// bot ユーザーは除外
const author = context.payload.pull_request.user.login;
if (author.endsWith('[bot]')) {
  console.log('⏭️ Skipping language check for bot user');
  return;
}
```

---

## トラブルシューティング

### 問題1: CIが実行されない

#### 症状
PRを作成しても、CI「PR Language Check」が実行されない。

#### 原因と対処法

**原因1: ファイルの配置場所が間違っている**
```bash
# 確認
ls -la .github/workflows/pr-language-check.yaml

# 対処: 正しい場所に配置
mkdir -p .github/workflows
mv pr-language-check.yaml .github/workflows/
```

**原因2: YAMLの文法エラー**
```bash
# 確認: GitHub Actionsタブでエラーメッセージをチェック

# 対処: YAMLバリデーターで検証
# https://www.yamllint.com/
```

**原因3: GitHubリポジトリの権限不足**
```
確認: Settings → Actions → General
対処: "Allow all actions and reusable workflows" を選択
```

---

### 問題2: botコメントが投稿されない

#### 症状
CIは実行されるが、botコメントが投稿されない。

#### 原因と対処法

**原因: GITHUB_TOKENの権限不足**
```yaml
# 確認: ワークフローファイルに permissions 設定があるか

# 対処: permissions を追加
permissions:
  pull-requests: write
  issues: write
```

---

### 問題3: 日本語なのにCI失敗する

#### 症状
日本語で記載しているのに、CIが失敗する。

#### 原因と対処法

**原因1: 全角スペースや特殊文字のみ**
```
対処: 実際の日本語文字（ひらがな、カタカナ、漢字）を含める
```

**原因2: 説明文が空**
```
対処: 説明文を記載する（テンプレートに従う）
```

**原因3: 正規表現の範囲外の文字**
```javascript
// 確認: 使用している日本語文字がサポート範囲か
// ひらがな: \u3040-\u309F
// カタカナ: \u30A0-\u30FF
// 漢字: \u4E00-\u9FAF

// 対処: 必要に応じて正規表現を拡張
```

---

### 問題4: マージボタンが有効化されない

#### 症状
CIが合格しても、マージボタンが有効化されない。

#### 原因と対処法

**原因1: 他のCIが失敗している**
```
確認: すべてのCIチェックが合格しているか
対処: 失敗しているCIを修正
```

**原因2: ブランチ保護ルールの設定ミス**
```
確認: Settings → Branches → Branch protection rules
対処: 必須ステータスチェックに「PR Language Check」が含まれているか確認
```

**原因3: レビュー承認が不足**
```
確認: "Require approvals" 設定がある場合
対処: 必要な数のレビュー承認を得る
```

---

## FAQ

### Q1: このCIは必須ですか？
**A**: はい、組織標準として必須です。すべてのプロジェクトに導入してください。

---

### Q2: 技術用語（API、JWT等）は英語でいいですか？
**A**: はい、問題ありません。文章全体が日本語であればOKです。

**例（OK）**:
```markdown
JWT（JSON Web Token）を使用した認証機能を実装しました。
RESTful APIのエンドポイントを追加しました。
```

---

### Q3: コードブロック内は？
**A**: コードブロック内は英語で問題ありません。PR説明文の文章が日本語であればOKです。

---

### Q4: 英語PRを修正するのと、再作成するのと、どちらがいいですか？
**A**: **PR修正（オプション1）を推奨**します。
- コミット履歴が保持される
- PR番号が変わらない
- 簡単で速い

---

### Q5: このCIを無効化できますか？
**A**: 組織標準として必須です。特別な理由がある場合は、組織標準チームに相談してください。

---

### Q6: GitLab CIでも使えますか？
**A**: このファイルはGitHub Actions専用です。GitLab CIへの移植は可能ですが、構文を変更する必要があります。

**GitLab CI版の実装例**:
```yaml
# .gitlab-ci.yml
pr-language-check:
  stage: test
  script:
    - |
      # PR説明文を取得して日本語チェック
      # (GitLab CI特有のロジック)
  only:
    - merge_requests
```

---

### Q7: 例外を設けたい場合は？
**A**: 以下の方法があります：

1. **ラベルでスキップ**（カスタマイズ必要）
   ```javascript
   if (labels.includes('skip-language-check')) {
     return; // スキップ
   }
   ```

2. **組織標準チームに相談**
   - 正当な理由がある場合は例外を検討

---

### Q8: 実装に問題があった場合の連絡先は？
**A**: 以下に連絡してください：
- Issue作成: [組織標準リポジトリ]
- Slack: #dev-standards チャンネル
- メール: dev-standards@example.com

---

## 関連ドキュメント

### 組織標準
- [PR言語問題の解決策](../../00-guides/PR-LANGUAGE-ISSUE-SOLUTION.md)
- [Phase 4レビューガイド](../../00-guides/phase-guides/phase-4-review-qa-guide.md)
- [AI-PRE-WORK-CHECKLIST](../../00-guides/AI-PRE-WORK-CHECKLIST.md)

### テンプレート
- [PRテンプレート](../pr-templates/PULL_REQUEST_TEMPLATE.md)
- [PRテンプレート使用ガイド](../pr-templates/README.md)

### CI設定
- [CI設定チェックリスト](../../00-guides/CI-SETUP-CHECKLIST.md)

---

## 更新履歴

| 日付 | バージョン | 変更内容 | 作成者 |
|------|-----------|---------|--------|
| 2025-11-07 | 1.0.0 | 初版作成 | Organization Standards Team |

---

**このドキュメントは組織標準の一部です。フィードバックや改善提案は Issue で受け付けています。**
