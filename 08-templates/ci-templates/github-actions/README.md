# GitHub Actions CI テンプレート集

## 📋 目次
- [概要](#概要)
- [テンプレート一覧](#テンプレート一覧)
- [使用方法](#使用方法)
- [ベストプラクティス](#ベストプラクティス)
- [関連ドキュメント](#関連ドキュメント)

---

## 概要

このディレクトリには、GitHub Actionsで使用する**言語非依存のCI品質ゲート**テンプレートが格納されています。

### 目的
- **組織標準の統一**: すべてのプロジェクトで同じCI品質ゲートを使用
- **品質保証**: PRの品質を自動的に検証
- **効率化**: 手動チェックを自動化
- **強制力**: CI失敗でマージを防止

### 対象プロジェクト
- すべてのGitHubリポジトリ
- すべてのプログラミング言語（言語非依存）

---

## テンプレート一覧

### 1. PR言語チェック（必須）

**ファイル**: `pr-language-check.yaml`

**用途**: PRのタイトル・説明文が日本語で記載されているかを自動検証

**機能**:
- ✅ 日本語文字（ひらがな、カタカナ、漢字）の検出
- ✅ 英語PRはCI失敗でマージ防止
- ✅ botコメントで修正方法を自動提示
- ✅ 技術用語（API、JWT等）の英語表記は許可

**適用対象**: すべてのプロジェクト（必須）

**優先度**: 🔴 最優先

**詳細ドキュメント**: [pr-language-check.md](./pr-language-check.md)

---

## 使用方法

### Step 1: 必要なテンプレートを選択

すべてのプロジェクトで **PR言語チェック** は必須です。

---

### Step 2: プロジェクトへの配置

#### 方法1: 個別ファイルをコピー
```bash
# プロジェクトのルートディレクトリに移動
cd <project-root>

# .github/workflows/ ディレクトリを作成（存在しない場合）
mkdir -p .github/workflows

# テンプレートをコピー
cp /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/pr-language-check.yaml \
   .github/workflows/pr-language-check.yaml
```

#### 方法2: 一括コピー
```bash
# すべてのGitHub Actions CIテンプレートをコピー
cp /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/*.yaml \
   <project-root>/.github/workflows/
```

---

### Step 3: コミット＆プッシュ

```bash
# ファイルをステージング
git add .github/workflows/

# コミット
git commit -m "feat: GitHub Actions CI品質ゲートを追加

- PR言語チェックを追加（日本語必須）

参照: devin-organization-standards/08-templates/ci-templates/github-actions/"

# プッシュ
git push origin main
```

---

### Step 4: ブランチ保護ルールの設定（推奨）

#### なぜ必要？
- CIが失敗してもマージできてしまう問題を防止
- 組織標準を強制的に適用

#### 設定方法

1. **リポジトリ設定に移動**
   - Settings → Branches

2. **ブランチ保護ルールを追加**
   - Add branch protection rule
   - Branch name pattern: `main` または `develop`

3. **必須ステータスチェックを有効化**
   - ✅ Require status checks to pass before merging
   - ✅ Require branches to be up to date before merging
   - ステータスチェック選択: 
     - `日本語記載チェック` または `PR Language Check`
     - （その他、プロジェクト固有のCI）

4. **保存**
   - Save changes

---

### Step 5: 動作確認

各テンプレートの詳細ドキュメントを参照してください：
- [PR言語チェックの動作確認](./pr-language-check.md#動作確認)

---

## ベストプラクティス

### 1. すべてのプロジェクトで統一

組織内のすべてのプロジェクトで、同じCI品質ゲートを使用してください。

**推奨構成**:
```
<project-root>/.github/workflows/
├── ci.yaml                      # プロジェクト固有のCI（ビルド、テスト等）
├── pr-language-check.yaml       # 組織標準（必須）
└── （その他、プロジェクト固有のCI）
```

---

### 2. カスタマイズは最小限に

組織標準テンプレートは、できるだけそのまま使用してください。

**カスタマイズが必要な場合**:
1. 組織標準チームに相談
2. 承認を得てからカスタマイズ
3. カスタマイズ内容をドキュメント化

---

### 3. 定期的な更新

組織標準テンプレートが更新された場合、速やかに反映してください。

**更新フロー**:
```bash
# 1. 最新テンプレートを取得
git pull origin main

# 2. 差分を確認
diff <project-root>/.github/workflows/pr-language-check.yaml \
     /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/pr-language-check.yaml

# 3. 更新が必要な場合、上書き
cp /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/pr-language-check.yaml \
   <project-root>/.github/workflows/pr-language-check.yaml

# 4. コミット＆プッシュ
git add .github/workflows/pr-language-check.yaml
git commit -m "chore: PR言語チェックCIを最新版に更新"
git push
```

---

### 4. CI失敗時の対応

CI品質ゲートが失敗した場合、以下の手順で対応してください：

1. **CI失敗の原因を確認**
   - GitHub Actionsのログをチェック
   - botコメントを確認

2. **修正**
   - 各テンプレートの詳細ドキュメントを参照
   - 修正方法に従って対処

3. **再実行**
   - 修正後、自動的にCIが再実行される
   - 手動再実行も可能（Re-run jobsボタン）

4. **それでも解決しない場合**
   - 組織標準チームに相談
   - Issueを作成

---

## トラブルシューティング

### 問題: CIが実行されない

**確認項目**:
1. ファイルの配置場所は正しいか？
   - `.github/workflows/` ディレクトリ内
2. YAMLの文法は正しいか？
   - https://www.yamllint.com/ で検証
3. GitHubリポジトリの権限は十分か？
   - Settings → Actions → General
   - "Allow all actions and reusable workflows" を選択

---

### 問題: botコメントが投稿されない

**確認項目**:
1. GITHUB_TOKENの権限は十分か？
   ```yaml
   permissions:
     pull-requests: write
     issues: write
   ```
2. リポジトリの設定で、GitHub Actionsからのコメントが許可されているか？

---

### 問題: マージボタンが有効化されない

**確認項目**:
1. すべてのCIチェックが合格しているか？
2. ブランチ保護ルールは正しく設定されているか？
3. 必要なレビュー承認は得られているか？

---

## 言語別のCI設定との関係

このディレクトリのCI品質ゲートは**言語非依存**です。
言語別のCI設定（ビルド、テスト、カバレッジ等）は、以下のディレクトリを参照してください：

- **Java**: `/08-templates/ci-templates/java-spring-boot/`
- **TypeScript**: `/08-templates/ci-templates/typescript-node/`
- **Python**: `/08-templates/ci-templates/python/`

### 推奨構成

プロジェクトには、以下の両方を配置してください：

1. **言語別CI設定**（ビルド、テスト、カバレッジ等）
   - 例: Java Spring Boot プロジェクト
   - `.github/workflows/ci.yaml`（Java用）
   
2. **言語非依存CI設定**（PR言語チェック等）
   - `.github/workflows/pr-language-check.yaml`

```
<project-root>/.github/workflows/
├── ci.yaml                      # 言語別CI（Java/TypeScript/Python）
└── pr-language-check.yaml       # 言語非依存CI（すべてのプロジェクト共通）
```

---

## よくある質問（FAQ）

### Q1: GitHub Actions以外のCIプラットフォームは？
**A**: このディレクトリはGitHub Actions専用です。GitLab CI、Circle CI等への移植は可能ですが、構文を変更する必要があります。

---

### Q2: プロジェクト固有のカスタマイズは可能ですか？
**A**: 最小限のカスタマイズのみ推奨します。大幅なカスタマイズが必要な場合は、組織標準チームに相談してください。

---

### Q3: CI品質ゲートを追加したいのですが？
**A**: 以下の手順で提案してください：
1. Issueを作成（組織標準リポジトリ）
2. 提案内容を記載（目的、実装案、効果）
3. 組織標準チームがレビュー
4. 承認後、テンプレートを追加

---

### Q4: このディレクトリのテンプレートは必須ですか？
**A**: はい、組織標準として必須です。すべてのプロジェクトに導入してください。

---

### Q5: 実装に問題があった場合の連絡先は？
**A**: 以下に連絡してください：
- Issue作成: [組織標準リポジトリ]
- Slack: #dev-standards チャンネル
- メール: dev-standards@example.com

---

## 関連ドキュメント

### 組織標準
- [PR言語問題の解決策](../../00-guides/PR-LANGUAGE-ISSUE-SOLUTION.md)
- [CI設定チェックリスト](../../00-guides/CI-SETUP-CHECKLIST.md)
- [Phase 4レビューガイド](../../00-guides/phase-guides/phase-4-review-qa-guide.md)

### 言語別CI設定
- [Java CI設定](../java-spring-boot/)
- [TypeScript CI設定](../typescript-node/)
- [Python CI設定](../python/)

### テンプレート
- [PRテンプレート](../pr-templates/)

---

## 更新履歴

| 日付 | バージョン | 変更内容 | 作成者 |
|------|-----------|---------|--------|
| 2025-11-07 | 1.0.0 | 初版作成（PR言語チェック追加） | Organization Standards Team |

---

**このドキュメントは組織標準の一部です。新しいCI品質ゲートの提案や改善案は Issue で受け付けています。**
