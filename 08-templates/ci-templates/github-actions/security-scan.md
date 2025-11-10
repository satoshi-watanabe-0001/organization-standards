# セキュリティスキャン CI テンプレート

## 📋 目次
- [概要](#概要)
- [機能](#機能)
- [セットアップ方法](#セットアップ方法)
- [設定内容の詳細](#設定内容の詳細)
- [トラブルシューティング](#トラブルシューティング)
- [カスタマイズ](#カスタマイズ)

---

## 概要

**ファイル**: `security-scan.yaml`

このテンプレートは、プロジェクトのセキュリティリスクを自動的に検出するCIワークフローです。

### 目的
- 依存関係の既知の脆弱性を検出
- ハードコードされた認証情報（APIキー、パスワード等）を検出
- セキュリティインシデントを未然に防止

### 検出対象
- **依存関係の脆弱性**: CVSS 7.0以上の脆弱性
- **ハードコードされたシークレット**: APIキー、パスワード、トークン、秘密鍵等

---

## 機能

### 1. OWASP Dependency-Check（依存関係脆弱性スキャン）

**検出内容**:
- Javaライブラリ（Maven/Gradle）
- Node.jsパッケージ（npm/yarn）
- Pythonパッケージ（pip）
- .NETライブラリ（NuGet）

**基準**:
- CVSS スコア 7.0以上でCI失敗
- 既知の脆弱性データベース（NVD）と照合

**実行タイミング**:
- PR作成時
- mainブランチへのpush時
- 毎週月曜日 9:00 (JST) に定期実行

---

### 2. TruffleHog Secret Scan（シークレットスキャン）

**検出内容**:
- GitHub Personal Access Token
- AWS Access Key
- Google API Key
- Slack Token
- Private Key（秘密鍵）
- Database Connection String
- その他、600種類以上のシークレットパターン

**特徴**:
- Gitの履歴全体をスキャン
- 検証済みのシークレットのみ報告（誤検出を最小化）

**実行タイミング**:
- PR作成時
- mainブランチへのpush時

---

## セットアップ方法

### Step 1: テンプレートをプロジェクトにコピー

```bash
# プロジェクトのルートディレクトリに移動
cd <project-root>

# .github/workflows/ ディレクトリを作成（存在しない場合）
mkdir -p .github/workflows

# テンプレートをコピー
cp /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/security-scan.yaml \
   .github/workflows/security-scan.yaml
```

---

### Step 2: Dependabot を有効化（推奨）

Dependabot は GitHub の標準機能で、依存関係の脆弱性を自動検出して更新PRを作成します。

**設定手順**:
1. GitHubリポジトリ → Settings → Security → Code security and analysis
2. 以下を有効化：
   - ✅ **Dependabot alerts**: 脆弱性の検出
   - ✅ **Dependabot security updates**: 脆弱性の自動修正PR作成

**コスト**: 無料  
**効果**: 継続的な依存関係の監視

---

### Step 3: GitHub Secret Scanning を有効化（推奨）

GitHub Secret Scanning は、ハードコードされた認証情報を自動検出する標準機能です。

**設定手順**:
1. GitHubリポジトリ → Settings → Security → Code security and analysis
2. 以下を有効化：
   - ✅ **Secret scanning**: シークレットの検出
   - ✅ **Push protection**: プッシュ時にシークレットをブロック

**コスト**: 無料（パブリックリポジトリ）/ GitHub Advanced Security（プライベートリポジトリ）  
**効果**: プッシュ時にシークレットを自動ブロック

---

### Step 4: 抑制ファイルの作成（オプション）

誤検出を抑制するために、プロジェクトルートに抑制ファイルを作成できます。

**ファイル**: `dependency-check-suppressions.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<suppressions xmlns="https://jeremylong.github.io/DependencyCheck/dependency-suppression.1.3.xsd">
  <suppress>
    <notes>
      このライブラリは内部ネットワークでのみ使用されるため、リスクは低い
    </notes>
    <cve>CVE-2023-12345</cve>
  </suppress>
  
  <suppress>
    <notes>
      この脆弱性は最新版で修正されているが、依存関係の都合で更新できない
      対策: WAFでブロック済み
    </notes>
    <packageUrl regex="true">^pkg:maven/com\.example/.*@.*$</packageUrl>
    <cve>CVE-2023-67890</cve>
  </suppress>
</suppressions>
```

**注意**: 抑制する場合は、必ず理由を明記してください。

---

### Step 5: コミット＆プッシュ

```bash
# ファイルをステージング
git add .github/workflows/security-scan.yaml

# 抑制ファイルも追加する場合
git add dependency-check-suppressions.xml

# コミット
git commit -m "feat: セキュリティスキャンCIを追加

- OWASP Dependency-Checkによる依存関係脆弱性スキャン
- TruffleHogによるシークレットスキャン
- 毎週月曜日に定期実行

参照: devin-organization-standards/08-templates/ci-templates/github-actions/security-scan.yaml"

# プッシュ
git push origin main
```

---

### Step 6: ブランチ保護ルールの設定（推奨）

**設定手順**:
1. GitHubリポジトリ → Settings → Branches
2. Branch protection rule を追加
   - Branch name pattern: `main` または `develop`
3. 必須ステータスチェックを有効化：
   - ✅ Require status checks to pass before merging
   - ステータスチェック選択: 
     - `依存関係脆弱性スキャン`
     - `シークレットスキャン`

これにより、セキュリティスキャンに失敗した場合、マージができなくなります。

---

## 設定内容の詳細

### 実行タイミング

```yaml
on:
  pull_request:
    branches: [main, develop]  # PR作成時
  push:
    branches: [main, develop]  # プッシュ時
  schedule:
    - cron: '0 0 * * 1'        # 毎週月曜日 0:00 UTC (JST 9:00)
  workflow_dispatch:            # 手動実行
```

### 実行時間

| ジョブ | 実行時間 |
|-------|---------|
| 依存関係脆弱性スキャン | 3-5分 |
| シークレットスキャン | 1-2分 |
| **合計** | **4-7分** |

### 権限設定

```yaml
permissions:
  contents: read              # ソースコードの読み取り
  security-events: write      # セキュリティイベントの書き込み
  pull-requests: write        # PRへのコメント投稿
```

---

## トラブルシューティング

### 問題1: 依存関係脆弱性スキャンが失敗する

**原因**: 既知の脆弱性が検出された

**対処方法**:

#### 方法1: 依存関係を更新（推奨）

```bash
# Maven
mvn versions:display-dependency-updates
mvn versions:use-latest-releases

# Gradle
./gradlew dependencyUpdates
# build.gradle を手動で更新

# npm
npm audit
npm audit fix

# pip
pip list --outdated
pip install --upgrade <package-name>
```

#### 方法2: 抑制ファイルで誤検出を除外

上記の「Step 4: 抑制ファイルの作成」を参照してください。

---

### 問題2: シークレットスキャンが失敗する

**原因**: ハードコードされた認証情報が検出された

**対処方法**:

#### Step 1: 即座にシークレットを無効化

```bash
# 検出されたAPIキー/トークンを直ちに無効化
# 例: GitHub Personal Access Token
# → GitHub Settings → Developer settings → Personal access tokens → 該当トークンを削除
```

#### Step 2: Gitの履歴から削除

```bash
# BFG Repo-Cleaner を使用（推奨）
# https://rtyley.github.io/bfg-repo-cleaner/
wget https://repo1.maven.org/maven2/com/madgag/bfg/1.14.0/bfg-1.14.0.jar
java -jar bfg-1.14.0.jar --replace-text passwords.txt <repo>

# 強制プッシュ（注意: チーム全員への通知が必要）
git push origin --force --all
```

#### Step 3: 環境変数/シークレット管理に移行

```bash
# GitHub Secrets に登録
# Settings → Secrets and variables → Actions → New repository secret

# コード内での使用例（GitHub Actions）
# ${{ secrets.API_KEY }}
```

#### Step 4: .gitignore に追加

```bash
# .gitignore に追加して今後の漏洩を防止
echo ".env" >> .gitignore
echo "*.key" >> .gitignore
echo "*.pem" >> .gitignore
echo "secrets/" >> .gitignore
```

---

### 問題3: CIが実行されない

**確認項目**:
1. ファイルの配置場所は正しいか？
   - `.github/workflows/security-scan.yaml`
2. YAMLの文法は正しいか？
   - https://www.yamllint.com/ で検証
3. GitHub Actions の権限は有効か？
   - Settings → Actions → General → "Allow all actions and reusable workflows"

---

### 問題4: 定期実行が動作しない

**原因**: リポジトリが非アクティブの場合、定期実行は停止される

**対処方法**:
1. 手動でワークフローを実行
   - Actions → Security Scan → Run workflow
2. リポジトリを定期的に更新

---

## カスタマイズ

### CVSSスコアの閾値を変更

デフォルトでは CVSS 7.0 以上でCI失敗します。

```yaml
# より厳しくする場合（CVSS 5.0以上）
args: >
  --enableRetired
  --failOnCVSS 5
  --suppression dependency-check-suppressions.xml

# より緩くする場合（CVSS 9.0以上）
args: >
  --enableRetired
  --failOnCVSS 9
  --suppression dependency-check-suppressions.xml
```

**推奨**: CVSS 7.0（デフォルト）を維持してください。

---

### 定期実行のスケジュールを変更

```yaml
schedule:
  # 毎日実行（0:00 UTC）
  - cron: '0 0 * * *'
  
  # 毎週月曜日と金曜日（0:00 UTC）
  - cron: '0 0 * * 1,5'
  
  # 毎月1日（0:00 UTC）
  - cron: '0 0 1 * *'
```

**推奨**: 週1回（月曜日）のスキャンで十分です。

---

### 特定のブランチのみスキャン

```yaml
on:
  pull_request:
    branches: [main]  # mainブランチへのPRのみ
  push:
    branches: [main]  # mainブランチへのpushのみ
```

---

## コスト見積もり

### CI実行時間とコスト

| 項目 | 実行時間 | 頻度 | 月間コスト（目安） |
|------|---------|------|------------------|
| PR時のスキャン | 5分/回 | 50回/月 | $0（無料枠内） |
| 定期スキャン | 5分/回 | 4回/月 | $0（無料枠内） |
| **合計** | - | - | **$0** |

**注意**: GitHub Actions の無料枠は月2,000分です。

---

### 推奨する追加施策

| 施策 | コスト | 効果 |
|------|--------|------|
| Dependabot有効化 | 無料 | ★★★★★ |
| GitHub Secret Scanning | 無料 | ★★★★★ |
| SonarCloud（プライベート） | $10/月 | ★★★★☆ |

---

## ベストプラクティス

### 1. Dependabot と併用する

OWASP Dependency-Check は週1回の定期スキャンですが、Dependabot は継続的に監視します。

**推奨構成**:
- ✅ Dependabot: 継続的な監視＋自動修正PR
- ✅ OWASP Dependency-Check: 週次の包括的スキャン

---

### 2. 抑制は最小限に

誤検出を抑制する場合でも、以下のルールを守ってください：

- **理由を必ず記載**: なぜ抑制するのかを明確に
- **定期的に見直し**: 四半期ごとに抑制リストを確認
- **代替策を実施**: 抑制する場合は、別のセキュリティ対策を講じる

---

### 3. シークレットは環境変数で管理

```bash
# ❌ 悪い例
API_KEY = "sk_live_abcd1234"

# ✅ 良い例
API_KEY = os.getenv("API_KEY")
```

---

### 4. 定期的なセキュリティレビュー

- 月次: 検出された脆弱性の確認
- 四半期: 抑制リストの見直し
- 半期: セキュリティポリシーの更新

---

## 関連ドキュメント

### 組織標準
- [CI/CDギャップ分析](../../ci-gap-analysis.md)
- [Java CI構成ガイド](../../java-project-ci-structure.md)

### 外部リンク
- [OWASP Dependency-Check Documentation](https://jeremylong.github.io/DependencyCheck/)
- [TruffleHog Documentation](https://github.com/trufflesecurity/trufflehog)
- [GitHub Dependabot Documentation](https://docs.github.com/en/code-security/dependabot)
- [National Vulnerability Database](https://nvd.nist.gov/)

---

**このドキュメントは組織標準の一部です。改善案は Issue で受け付けています。**
