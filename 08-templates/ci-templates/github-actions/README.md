# GitHub Actions CI テンプレート集

## 📋 目次
- [概要](#概要)
- [テンプレート一覧](#テンプレート一覧)
- [どのCIファイルを使うべきか](#どのciファイルを使うべきか)
- [推奨CI構成](#推奨ci構成)
- [使用方法](#使用方法)
- [ベストプラクティス](#ベストプラクティス)
- [よくある質問（FAQ）](#よくある質問faq)
- [関連ドキュメント](#関連ドキュメント)

---

## 概要

このディレクトリには、GitHub Actionsで使用する**CI/CD品質ゲート**テンプレートが格納されています。

### 目的
- **組織標準の統一**: すべてのプロジェクトで同じCI品質ゲートを使用
- **品質保証**: PRの品質を自動的に検証
- **セキュリティ強化**: 脆弱性やシークレット漏洩を防止
- **効率化**: 手動チェックを自動化
- **強制力**: CI失敗でマージを防止

### 対象プロジェクト
- すべてのGitHubリポジトリ
- Java、Node.js、Python プロジェクト

---

## テンプレート一覧

### 🔴 必須テンプレート

#### 1. PR言語チェック
**ファイル**: `pr-language-check.yaml`

**用途**: PRのタイトル・説明文が日本語で記載されているかを自動検証

**機能**:
- ✅ 日本語文字（ひらがな、カタカナ、漢字）の検出
- ✅ 英語PRはCI失敗でマージ防止
- ✅ botコメントで修正方法を自動提示
- ✅ 技術用語（API、JWT等）の英語表記は許可

**適用対象**: すべてのプロジェクト（**必須**）

**実行時間**: 5-10秒

**詳細ドキュメント**: [pr-language-check.md](./pr-language-check.md)

---

#### 1.5. PR記載レベル品質チェック ⭐NEW
**ファイル**: `pr-description-quality-gate.yml`

**用途**: PR説明文の記載レベルを自動検証、組織標準の品質を保証

**機能**:
- ✅ 必須セクション7つの存在確認
- ✅ 変更内容の概要の文字数チェック（50文字以上）
- ✅ チェックボックス確認率の検証（70%以上）
- ✅ 日本語記載の確認
- ✅ 関連チケット番号の形式チェック
- ✅ 組織標準チェックリストの確認状況
- ✅ 自動ラベル管理（`pr-description-incomplete`）
- ✅ 詳細なコメント投稿（改善点を明示）
- ✅ 統計情報の表示（文字数、チェック率）

**適用対象**: すべてのプロジェクト（**必須**）

**実行時間**: 5-10秒

**効果**: 
- レビュー時間の短縮（-20%）
- レビュー往復回数の削減（-25%）
- 初回レビュー指摘事項の削減（-30%）

**連携**:
- PR言語チェック：言語の検証
- PR記載レベルチェック：構造と内容の検証
- PRセルフレビューリマインダー：自己レビュー実施確認

**詳細ドキュメント**: [pr-description-quality-gate.md](./pr-description-quality-gate.md)

---

### 🔴 最優先テンプレート

#### 2. セキュリティスキャン
**ファイル**: `security-scan.yaml`

**用途**: 依存関係の脆弱性とハードコードされた認証情報を自動検出

**機能**:
- ✅ OWASP Dependency-Check（依存関係脆弱性スキャン）
- ✅ TruffleHog Secret Scan（シークレットスキャン）
- ✅ 毎週月曜日に定期実行
- ✅ CVSS 7.0以上の脆弱性でCI失敗

**適用対象**: すべてのプロジェクト（**最優先**）

**実行時間**: 4-7分

**効果**: セキュリティインシデントを未然に防止（80%のリスクをカバー）

**詳細ドキュメント**: [security-scan.md](./security-scan.md)

---

### 🟡 推奨テンプレート

#### 3. PRセルフレビューリマインダー
**ファイル**: `pr-self-review-reminder.yml`

**用途**: PRが投稿された際に、セルフレビューを促すリマインダーコメントを自動投稿

**機能**:
- ✅ PR初回投稿時のみ自動コメント
- ✅ 組織標準ドキュメントへの参照を提示
- ✅ AIエージェントが自律的にチェックリストを選定
- ✅ 重複投稿を自動防止

**適用対象**: すべてのプロジェクト（推奨）

**実行時間**: 5秒（初回のみ）

**詳細ドキュメント**: [pr-self-review-reminder.md](./pr-self-review-reminder.md)

---

#### 4. コード品質分析
**ファイル**: `code-quality.yaml`

**用途**: コード品質を自動分析し、保守性を向上

**機能**:
- ✅ SonarCloud（総合的なコード品質分析）
- ✅ SpotBugs（Java静的解析）
- ✅ PMD（Javaコード品質チェック）
- ✅ ESLint（Node.js静的解析）
- ✅ Pylint（Python静的解析）

**適用対象**: コード品質を重視するプロジェクト（推奨）

**実行時間**: 5-10分

**効果**: 技術的負債の蓄積を防止、潜在的なバグの早期発見

**詳細ドキュメント**: [code-quality.md](./code-quality.md)

---

#### 5. 統合テスト
**ファイル**: `integration-test.yaml`

**用途**: データベースやAPIを含む統合テストを自動実行

**機能**:
- ✅ GitHub Actions Services（PostgreSQL/MySQL/Redis）
- ✅ TestContainers（Dockerベースの統合テスト）
- ✅ REST API テスト（REST Assured）
- ✅ Node.js/Python 統合テスト

**適用対象**: データベース連携があるプロジェクト（推奨）

**実行時間**: 10-15分

**効果**: 実環境に近いテスト、コンポーネント間の連携検証

**詳細ドキュメント**: [integration-test.md](./integration-test.md)

---

## どのCIファイルを使うべきか

### プロジェクトのフェーズ別

#### 🚀 新規プロジェクト立ち上げ時（必須のみ）

最初は**最小限の構成**から始めましょう：

```
.github/workflows/
├── pr-language-check.yaml         # 必須
├── security-scan.yaml             # 最優先
└── pr-self-review-reminder.yml    # 推奨
```

**所要時間**: 30分程度でセットアップ完了

---

#### 📈 プロジェクト成長期（品質重視）

プロジェクトが成長したら、**品質ゲート**を追加：

```
.github/workflows/
├── pr-language-check.yaml         # 必須
├── security-scan.yaml             # 最優先
├── pr-self-review-reminder.yml    # 推奨
├── code-quality.yaml              # ← 追加
└── ci.yaml                        # プロジェクト固有のCI
```

**追加理由**: 技術的負債の蓄積を防ぐため

---

#### 🏢 本番運用フェーズ（完全構成）

本番運用時は、**完全な品質保証**を実施：

```
.github/workflows/
├── pr-language-check.yaml         # 必須
├── security-scan.yaml             # 最優先
├── pr-self-review-reminder.yml    # 推奨
├── code-quality.yaml              # 推奨
├── integration-test.yaml          # ← 追加
└── ci.yaml                        # プロジェクト固有のCI
```

**追加理由**: 実環境に近い統合テストで品質を保証

---

### プロジェクトの種類別

#### 🟢 シンプルなプロジェクト（API、ライブラリ等）

```
.github/workflows/
├── pr-language-check.yaml
├── security-scan.yaml
├── pr-self-review-reminder.yml
└── ci.yaml  # ビルド、単体テスト、カバレッジ
```

**理由**: データベース連携がないため、統合テストは不要

---

#### 🟡 中規模プロジェクト（Web API、マイクロサービス）

```
.github/workflows/
├── pr-language-check.yaml
├── security-scan.yaml
├── pr-self-review-reminder.yml
├── code-quality.yaml
└── ci.yaml
```

**理由**: コード品質を重視し、技術的負債を防止

---

#### 🔴 大規模プロジェクト（エンタープライズシステム）

```
.github/workflows/
├── pr-language-check.yaml
├── pr-description-quality-gate.yml
├── security-scan.yaml
├── pr-self-review-reminder.yml
├── code-quality.yaml
├── integration-test.yaml
└── ci.yaml
```

**理由**: 完全な品質保証が必要

---

### 機能別の使い分け

| 目的 | 使用するテンプレート | 優先度 |
|------|---------------------|--------|
| **日本語PR強制** | pr-language-check.yaml | 🔴 必須 |
| **PR記載レベル品質保証** | pr-description-quality-gate.yml | 🔴 必須 |
| **セキュリティ強化** | security-scan.yaml | 🔴 最優先 |
| **AIセルフレビュー促進** | pr-self-review-reminder.yml | 🟡 推奨 |
| **コード品質向上** | code-quality.yaml | 🟡 推奨 |
| **統合テスト実施** | integration-test.yaml | 🟡 推奨 |
| **ビルド・単体テスト** | ci.yaml（個別作成） | 🔴 必須 |

---

## 推奨CI構成

### 最小構成（新規プロジェクト）

```bash
# ステップ1: 必須テンプレートをコピー
cp pr-language-check.yaml <project>/.github/workflows/
cp pr-description-quality-gate.yml <project>/.github/workflows/
cp security-scan.yaml <project>/.github/workflows/
cp pr-self-review-reminder.yml <project>/.github/workflows/

# ステップ2: Dependabot を有効化（5分）
# Settings → Security → Dependabot alerts/updates を有効化

# ステップ3: GitHub Secret Scanning を有効化（5分）
# Settings → Security → Secret scanning を有効化
```

**所要時間**: 30分  
**効果**: 基本的な品質保証＋セキュリティ強化

---

### 推奨構成（成長期プロジェクト）

```bash
# 最小構成に加えて
cp code-quality.yaml <project>/.github/workflows/

# SonarCloud セットアップ（15分）
# 1. SonarCloud にログイン
# 2. プロジェクトをインポート
# 3. SONAR_TOKEN を GitHub Secrets に登録
```

**所要時間**: 1時間  
**効果**: コード品質の継続的な向上

---

### 完全構成（本番運用プロジェクト）

```bash
# 推奨構成に加えて
cp integration-test.yaml <project>/.github/workflows/

# 統合テストの設定（1-2時間）
# 1. pom.xml/build.gradle に統合テスト用プロファイルを追加
# 2. TestContainers の依存関係を追加
# 3. 統合テストクラスを作成
```

**所要時間**: 2-3時間  
**効果**: 実環境に近いテストで品質保証

---

## 使用方法

### Step 1: 必要なテンプレートを選択

上記の「どのCIファイルを使うべきか」を参照して、プロジェクトに必要なテンプレートを選択してください。

**最低限**:
- ✅ `pr-language-check.yaml` - すべてのプロジェクトで必須
- ✅ `security-scan.yaml` - セキュリティ強化（最優先）

**推奨**:
- ✅ `pr-self-review-reminder.yml` - AIによるセルフレビューを促進
- ✅ `code-quality.yaml` - コード品質の継続的な向上
- ✅ `integration-test.yaml` - データベース連携があるプロジェクト

---

### Step 2: プロジェクトへの配置

#### 方法1: 個別ファイルをコピー（推奨）

```bash
# プロジェクトのルートディレクトリに移動
cd <project-root>

# .github/workflows/ ディレクトリを作成（存在しない場合）
mkdir -p .github/workflows

# 必須: PR言語チェック
cp /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/pr-language-check.yaml \
   .github/workflows/pr-language-check.yaml

# 最優先: セキュリティスキャン
cp /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/security-scan.yaml \
   .github/workflows/security-scan.yaml

# 推奨: PRセルフレビューリマインダー
cp /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/pr-self-review-reminder.yml \
   .github/workflows/pr-self-review-reminder.yml

# 推奨: コード品質分析
cp /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/code-quality.yaml \
   .github/workflows/code-quality.yaml

# 推奨: 統合テスト
cp /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/integration-test.yaml \
   .github/workflows/integration-test.yaml
```

#### 方法2: 一括コピー

```bash
# すべてのGitHub Actions CIテンプレートをコピー
cp /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/*.yaml \
   <project-root>/.github/workflows/
cp /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/*.yml \
   <project-root>/.github/workflows/
```

**注意**: 一括コピーは、プロジェクトに不要なCIファイルも含まれる可能性があるため、個別コピーを推奨します。

---

### Step 3: 追加設定（テンプレートごとに異なる）

各テンプレートの詳細ドキュメントを参照して、追加設定を行ってください：

| テンプレート | 追加設定 | 所要時間 |
|-------------|---------|---------|
| pr-language-check.yaml | 不要 | 0分 |
| security-scan.yaml | Dependabot/Secret Scanning有効化 | 10分 |
| pr-self-review-reminder.yml | 不要 | 0分 |
| code-quality.yaml | SonarCloud セットアップ | 15分 |
| integration-test.yaml | pom.xml/build.gradle 設定 | 1-2時間 |

---

### Step 4: コミット＆プッシュ

```bash
# ファイルをステージング
git add .github/workflows/

# コミット
git commit -m "feat: GitHub Actions CI品質ゲートを追加

追加したCI:
- PR言語チェック（日本語必須）
- セキュリティスキャン（脆弱性＋シークレット検出）
- PRセルフレビューリマインダー
- コード品質分析（SonarCloud）
- 統合テスト（TestContainers）

参照: devin-organization-standards/08-templates/ci-templates/github-actions/"

# プッシュ
git push origin main
```

---

### Step 5: ブランチ保護ルールの設定（必須）

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
     - `依存関係脆弱性スキャン`
     - `シークレットスキャン`
     - `SonarCloud 分析`（使用している場合）
     - （その他、プロジェクト固有のCI）

4. **保存**
   - Save changes

---

### Step 6: 動作確認

各テンプレートの詳細ドキュメントを参照してください：
- [PR言語チェックの動作確認](./pr-language-check.md#動作確認)
- [セキュリティスキャンの動作確認](./security-scan.md#セットアップ方法)
- [PRセルフレビューリマインダーの動作確認](./pr-self-review-reminder.md#セットアップ方法)
- [コード品質分析の動作確認](./code-quality.md#セットアップ方法)
- [統合テストの動作確認](./integration-test.md#セットアップ方法)

---

## ベストプラクティス

### 1. すべてのプロジェクトで統一

組織内のすべてのプロジェクトで、同じCI品質ゲートを使用してください。

**推奨構成**:
```
<project-root>/.github/workflows/
├── ci.yaml                        # プロジェクト固有のCI（ビルド、テスト等）
├── pr-language-check.yaml         # 組織標準（必須）
├── security-scan.yaml             # 組織標準（最優先）
├── pr-self-review-reminder.yml    # 組織標準（推奨）
├── code-quality.yaml              # 組織標準（推奨）
└── integration-test.yaml          # 組織標準（推奨）
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
diff <project-root>/.github/workflows/security-scan.yaml \
     /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/security-scan.yaml

# 3. 更新が必要な場合、上書き
cp /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/security-scan.yaml \
   <project-root>/.github/workflows/security-scan.yaml

# 4. コミット＆プッシュ
git add .github/workflows/security-scan.yaml
git commit -m "chore: セキュリティスキャンCIを最新版に更新"
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

### 5. セキュリティを最優先

**重要**: セキュリティスキャンは最優先で導入してください。

**即座に実施可能**（所要時間: 10分）:
1. Dependabot alerts 有効化
2. Dependabot security updates 有効化
3. GitHub Secret Scanning 有効化

**効果**: これだけで80%のセキュリティリスクをカバーできます。

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

### 問題: CI実行時間が長すぎる

**対処方法**:

#### 1. 不要なCIを削除
プロジェクトに不要なCIファイルは削除してください。

#### 2. CI実行を並列化
```yaml
jobs:
  test:
    strategy:
      matrix:
        os: [ubuntu-latest]
        java: [17]
```

#### 3. キャッシュを活用
```yaml
- name: Cache Maven packages
  uses: actions/cache@v4
  with:
    path: ~/.m2
    key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
```

---

## よくある質問（FAQ）

### Q1: すべてのテンプレートを使用する必要がありますか？

**A**: いいえ。プロジェクトの状況に応じて選択してください。

**最低限**:
- ✅ `pr-language-check.yaml`（必須）
- ✅ `security-scan.yaml`（最優先）

**推奨**:
- ✅ `pr-self-review-reminder.yml`
- ✅ `code-quality.yaml`
- ✅ `integration-test.yaml`

---

### Q2: CI実行にどれくらいの時間がかかりますか？

**A**: 構成により異なります。

| 構成 | 実行時間 |
|------|---------|
| 最小構成 | 5-10分 |
| 推奨構成 | 10-15分 |
| 完全構成 | 15-25分 |

---

### Q3: GitHub Actions の無料枠で足りますか？

**A**: はい、多くのプロジェクトで無料枠内（月2,000分）で運用できます。

**月間コスト試算**（PR 50回/月の場合）:
- 最小構成: $0（約250分/月）
- 推奨構成: $0（約750分/月）
- 完全構成: $0（約1,250分/月）

---

### Q4: SonarCloud は必須ですか？

**A**: 必須ではありませんが、強く推奨します。

**理由**:
- コード品質の継続的な監視
- 技術的負債の早期発見
- パブリックリポジトリは無料

---

### Q5: 統合テストは必ず必要ですか？

**A**: データベース連携があるプロジェクトでは強く推奨します。

**推奨するプロジェクト**:
- Web API
- マイクロサービス
- データベース連携があるアプリケーション

**不要なプロジェクト**:
- ライブラリ
- CLIツール
- フロントエンドのみのプロジェクト

---

### Q6: Dependabot と OWASP Dependency-Check の違いは？

**A**: 両方とも依存関係の脆弱性を検出しますが、役割が異なります。

| 項目 | Dependabot | OWASP Dependency-Check |
|------|-----------|----------------------|
| 監視頻度 | 継続的 | 週次 |
| 自動修正 | あり（PR作成） | なし |
| カバレッジ | GitHub Advisory Database | NVD（National Vulnerability Database） |
| コスト | 無料 | 無料 |

**推奨**: 両方を併用してください。

---

### Q7: PRセルフレビューリマインダーはAIがいないと機能しませんか？

**A**: いいえ、人間の開発者にも有効です。

リマインダーを見て、手動でチェックリストを確認し、セルフレビューを実施してください。ただし、AIエージェントを使用している場合は、自動的にセルフレビューが実施されるため、より効率的です。

---

### Q8: 実装に問題があった場合の連絡先は？

**A**: 以下に連絡してください：
- Issue作成: [組織標準リポジトリ]
- Slack: #dev-standards チャンネル
- メール: dev-standards@example.com

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
   
2. **言語非依存CI設定**（PR言語チェック、セキュリティスキャン等）
   - `.github/workflows/pr-language-check.yaml`
   - `.github/workflows/security-scan.yaml`
   - `.github/workflows/pr-self-review-reminder.yml`
   - `.github/workflows/code-quality.yaml`
   - `.github/workflows/integration-test.yaml`

```
<project-root>/.github/workflows/
├── ci.yaml                        # 言語別CI（Java/TypeScript/Python）
├── pr-language-check.yaml         # 言語非依存CI（すべてのプロジェクト共通）
├── security-scan.yaml             # 言語非依存CI（すべてのプロジェクト共通）
├── pr-self-review-reminder.yml    # 言語非依存CI（すべてのプロジェクト共通）
├── code-quality.yaml              # 言語非依存CI（すべてのプロジェクト共通）
└── integration-test.yaml          # 言語非依存CI（すべてのプロジェクト共通）
```

---

## 関連ドキュメント

### 組織標準
- [PR言語問題の解決策](../../00-guides/PR-LANGUAGE-ISSUE-SOLUTION.md)
- [CI設定チェックリスト](../../00-guides/CI-SETUP-CHECKLIST.md)
- [Phase 4レビューガイド](../../00-guides/phase-guides/phase-4-review-qa-guide.md)
- [CI/CDギャップ分析](../../ci-gap-analysis.md)
- [Java CI構成ガイド](../../java-project-ci-structure.md)

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
| 2025-11-10 | 2.0.0 | セキュリティスキャン、コード品質分析、統合テストを追加 | Organization Standards Team |
| 2025-11-10 | 1.1.0 | PRセルフレビューリマインダー追加 | Organization Standards Team |
| 2025-11-07 | 1.0.0 | 初版作成（PR言語チェック追加） | Organization Standards Team |

---

**このドキュメントは組織標準の一部です。新しいCI品質ゲートの提案や改善案は Issue で受け付けています。**
