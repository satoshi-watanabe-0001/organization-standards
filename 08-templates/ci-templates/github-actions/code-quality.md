# コード品質分析 CI テンプレート

## 📋 目次
- [概要](#概要)
- [機能](#機能)
- [セットアップ方法](#セットアップ方法)
- [設定内容の詳細](#設定内容の詳細)
- [トラブルシューティング](#トラブルシューティング)
- [カスタマイズ](#カスタマイズ)

---

## 概要

**ファイル**: `code-quality.yaml`

このテンプレートは、プロジェクトのコード品質を自動的に分析するCIワークフローです。

### 目的
- コードの保守性を向上
- 潜在的なバグを早期発見
- コーディング規約の遵守を徹底
- 技術的負債の蓄積を防止

### 対応言語
- ✅ Java（Maven/Gradle）
- ✅ Node.js（npm/yarn）
- ✅ Python

---

## 機能

### 1. SonarCloud（総合的なコード品質分析）

**分析内容**:
- コードカバレッジ（80%以上推奨）
- コードスメル（保守性の問題）
- バグ（潜在的なバグ）
- セキュリティホットスポット
- 重複コード
- 技術的負債

**品質ゲート基準（デフォルト）**:
| 項目 | 基準 |
|------|------|
| カバレッジ | ≥ 80% |
| 重複率 | ≤ 3% |
| 新規バグ | 0 |
| 新規脆弱性 | 0 |
| セキュリティホットスポット | レビュー済み |
| 技術的負債 | ≤ 5% |

---

### 2. SpotBugs（Java静的解析）

**検出内容**:
- Null ポインタ例外の可能性
- 無限ループ
- リソースリーク
- スレッドセーフティの問題
- パフォーマンス問題

**対象**: Javaプロジェクトのみ

---

### 3. PMD（Javaコード品質チェック）

**検出内容**:
- 未使用の変数
- 空のcatchブロック
- 不必要に複雑なコード
- コーディング規約違反

**対象**: Javaプロジェクトのみ

---

### 4. ESLint（Node.js静的解析）

**検出内容**:
- JavaScriptの構文エラー
- コーディング規約違反
- ベストプラクティス違反
- セキュリティリスク

**対象**: Node.jsプロジェクトのみ

---

### 5. Pylint（Python静的解析）

**検出内容**:
- Pythonの構文エラー
- コーディング規約違反（PEP 8）
- 潜在的なバグ
- コードスメル

**対象**: Pythonプロジェクトのみ

---

## セットアップ方法

### Step 1: テンプレートをプロジェクトにコピー

```bash
# プロジェクトのルートディレクトリに移動
cd <project-root>

# .github/workflows/ ディレクトリを作成（存在しない場合）
mkdir -p .github/workflows

# テンプレートをコピー
cp /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/code-quality.yaml \
   .github/workflows/code-quality.yaml
```

---

### Step 2: SonarCloud のセットアップ

#### 2-1. SonarCloud アカウント作成

1. [SonarCloud](https://sonarcloud.io/) にアクセス
2. "Log in" → "GitHub" でログイン
3. 組織を作成（GitHubの組織と連携）

#### 2-2. プロジェクトをインポート

1. SonarCloud ダッシュボード → "+" → "Analyze new project"
2. 対象のGitHubリポジトリを選択
3. "Set Up" をクリック

#### 2-3. トークンを生成

1. "My Account" → "Security"
2. "Generate Tokens"
3. トークン名を入力（例: `ci-token`）
4. "Generate" をクリック
5. **生成されたトークンをコピー**（後で使用）

#### 2-4. GitHub Secretsに登録

1. GitHubリポジトリ → Settings → Secrets and variables → Actions
2. "New repository secret" をクリック
3. 以下を登録：
   - Name: `SONAR_TOKEN`
   - Value: （上記でコピーしたトークン）

---

### Step 3: 言語別の追加設定

#### Java プロジェクト（Maven）

**pom.xml** に以下を追加：

```xml
<properties>
  <sonar.organization>your-organization</sonar.organization>
  <sonar.host.url>https://sonarcloud.io</sonar.host.url>
</properties>

<build>
  <plugins>
    <!-- SonarQube Maven Plugin -->
    <plugin>
      <groupId>org.sonarsource.scanner.maven</groupId>
      <artifactId>sonar-maven-plugin</artifactId>
      <version>3.10.0.2594</version>
    </plugin>
    
    <!-- SpotBugs -->
    <plugin>
      <groupId>com.github.spotbugs</groupId>
      <artifactId>spotbugs-maven-plugin</artifactId>
      <version>4.8.2.0</version>
    </plugin>
    
    <!-- PMD -->
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-pmd-plugin</artifactId>
      <version>3.21.2</version>
    </plugin>
  </plugins>
</build>
```

#### Java プロジェクト（Gradle）

**build.gradle** に以下を追加：

```gradle
plugins {
    id "org.sonarqube" version "4.4.1.3373"
    id "com.github.spotbugs" version "6.0.7"
    id "pmd"
}

sonar {
    properties {
        property "sonar.projectKey", "your-organization_your-repo"
        property "sonar.organization", "your-organization"
        property "sonar.host.url", "https://sonarcloud.io"
    }
}

spotbugs {
    effort = "max"
    reportLevel = "low"
}

pmd {
    ruleSets = []
    ruleSetFiles = files("config/pmd/ruleset.xml")
}
```

#### Node.js プロジェクト

**sonar-project.properties** をプロジェクトルートに作成：

```properties
sonar.projectKey=your-organization_your-repo
sonar.organization=your-organization
sonar.sources=src
sonar.tests=test
sonar.javascript.lcov.reportPaths=coverage/lcov.info
```

**package.json** に追加：

```json
{
  "scripts": {
    "lint": "eslint src/**/*.js",
    "test": "jest --coverage"
  },
  "devDependencies": {
    "eslint": "^8.0.0",
    "jest": "^29.0.0"
  }
}
```

#### Python プロジェクト

**sonar-project.properties** をプロジェクトルートに作成：

```properties
sonar.projectKey=your-organization_your-repo
sonar.organization=your-organization
sonar.sources=src
sonar.tests=tests
sonar.python.coverage.reportPaths=coverage.xml
```

---

### Step 4: コミット＆プッシュ

```bash
# ファイルをステージング
git add .github/workflows/code-quality.yaml
git add sonar-project.properties  # SonarCloud設定ファイル
git add pom.xml  # または build.gradle、package.json

# コミット
git commit -m "feat: コード品質分析CIを追加

- SonarCloudによる総合的なコード品質分析
- SpotBugs、PMD（Java）、ESLint（Node.js）、Pylint（Python）
- 品質ゲート: カバレッジ80%以上、重複3%以下

参照: devin-organization-standards/08-templates/ci-templates/github-actions/code-quality.yaml"

# プッシュ
git push origin main
```

---

### Step 5: ブランチ保護ルールの設定（推奨）

**設定手順**:
1. GitHubリポジトリ → Settings → Branches
2. Branch protection rule を追加
   - Branch name pattern: `main` または `develop`
3. 必須ステータスチェックを有効化：
   - ✅ Require status checks to pass before merging
   - ステータスチェック選択: 
     - `SonarCloud 分析`
     - （その他、プロジェクトで有効化した静的解析）

---

## 設定内容の詳細

### 実行タイミング

```yaml
on:
  pull_request:
    branches: [main, develop]  # PR作成時
  push:
    branches: [main, develop]  # プッシュ時
  workflow_dispatch:            # 手動実行
```

### 実行時間

| ジョブ | 実行時間 |
|-------|---------|
| SonarCloud | 3-5分 |
| SpotBugs（Java） | 2-3分 |
| PMD（Java） | 1-2分 |
| ESLint（Node.js） | 30秒-1分 |
| Pylint（Python） | 1-2分 |
| **合計** | **5-10分** |

### 自動判定

テンプレートは、プロジェクトの種類を自動判定します：

```yaml
# Java プロジェクト
if: hashFiles('**/pom.xml') != '' || hashFiles('**/*.gradle') != ''

# Node.js プロジェクト
if: hashFiles('**/package.json') != ''

# Python プロジェクト
if: hashFiles('**/requirements.txt') != '' || hashFiles('**/pyproject.toml') != ''
```

---

## トラブルシューティング

### 問題1: SonarCloud 分析が失敗する

**原因1**: SONAR_TOKEN が設定されていない

**対処方法**:
```bash
# GitHub Secrets に SONAR_TOKEN が登録されているか確認
# Settings → Secrets and variables → Actions → SONAR_TOKEN
```

**原因2**: 品質ゲートに合格しない

**対処方法**:
1. [SonarCloud ダッシュボード](https://sonarcloud.io/) にアクセス
2. プロジェクトを選択
3. "Issues" タブで問題を確認
4. 優先度順に修正：
   - 🔴 Blocker → 🟠 Critical → 🟡 Major → 🔵 Minor

---

### 問題2: カバレッジが80%未満

**対処方法**:

#### Java（JUnit）
```java
@Test
public void testExample() {
    // テストケースを追加
    assertEquals(expected, actual);
}
```

#### Node.js（Jest）
```javascript
test('example test', () => {
    expect(add(1, 2)).toBe(3);
});
```

#### Python（pytest）
```python
def test_example():
    assert add(1, 2) == 3
```

---

### 問題3: SpotBugs/PMD が失敗する

**原因**: 潜在的なバグやコーディング規約違反が検出された

**対処方法**:

#### 1. レポートを確認

```bash
# GitHub Actions の "Artifacts" からレポートをダウンロード
# spotbugs-report.html または pmd-report.html を確認
```

#### 2. 指摘された箇所を修正

**よくある指摘**:
- Null ポインタ例外の可能性 → null チェックを追加
- 未使用の変数 → 使用するか削除
- リソースリーク → try-with-resources を使用

---

### 問題4: ESLint/Pylint が失敗する

**対処方法**:

#### ESLint（Node.js）
```bash
# ローカルで実行
npm run lint

# 自動修正
npm run lint -- --fix
```

#### Pylint（Python）
```bash
# ローカルで実行
pylint **/*.py

# 設定ファイル（.pylintrc）で基準を調整
```

---

## カスタマイズ

### SonarCloud 品質ゲートの変更

**デフォルト基準**:
- カバレッジ: ≥ 80%
- 重複率: ≤ 3%
- 新規バグ: 0
- 新規脆弱性: 0

**基準を変更する場合**:
1. SonarCloud → プロジェクト → Administration → Quality Gates
2. "Create" または既存の品質ゲートを編集
3. 条件を変更

**注意**: 基準を緩めすぎないでください。

---

### SpotBugs の検出レベルを変更

**pom.xml**:
```xml
<plugin>
  <groupId>com.github.spotbugs</groupId>
  <artifactId>spotbugs-maven-plugin</artifactId>
  <configuration>
    <effort>Max</effort>
    <threshold>Low</threshold>  <!-- Low/Medium/High -->
  </configuration>
</plugin>
```

---

### ESLint の設定をカスタマイズ

**.eslintrc.json** をプロジェクトルートに作成：

```json
{
  "extends": ["eslint:recommended"],
  "env": {
    "node": true,
    "es6": true
  },
  "rules": {
    "no-console": "warn",
    "no-unused-vars": "error"
  }
}
```

---

## コスト見積もり

### SonarCloud の料金

| プラン | 対象 | 料金 |
|--------|------|------|
| Free | パブリックリポジトリ | 無料 |
| Team | プライベートリポジトリ（小規模） | $10/月 |
| Enterprise | プライベートリポジトリ（大規模） | カスタム |

### CI実行時間とコスト

| 項目 | 実行時間 | 頻度 | 月間コスト（目安） |
|------|---------|------|------------------|
| PR時の分析 | 8分/回 | 50回/月 | $0（無料枠内） |
| **合計** | - | - | **$0** |

**注意**: GitHub Actions の無料枠は月2,000分です。

---

## ベストプラクティス

### 1. 品質ゲートを厳格に保つ

- ✅ カバレッジ: 80%以上
- ✅ 重複率: 3%以下
- ✅ 新規バグ: 0
- ✅ 新規脆弱性: 0

**理由**: 技術的負債の蓄積を防ぐため。

---

### 2. 定期的なリファクタリング

コードスメルが検出された場合、速やかにリファクタリングしてください。

**推奨頻度**: スプリントごと

---

### 3. テストカバレッジを維持

新機能を追加する際は、必ずテストケースも追加してください。

**目標**: すべての新規コードで100%カバレッジ

---

### 4. 静的解析結果を無視しない

SpotBugs、PMD、ESLint、Pylintの指摘は、必ず確認して対処してください。

**理由**: 潜在的なバグの早期発見。

---

## 関連ドキュメント

### 組織標準
- [CI/CDギャップ分析](../../ci-gap-analysis.md)
- [Java CI構成ガイド](../../java-project-ci-structure.md)

### 外部リンク
- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [SpotBugs Documentation](https://spotbugs.github.io/)
- [PMD Documentation](https://pmd.github.io/)
- [ESLint Documentation](https://eslint.org/)
- [Pylint Documentation](https://pylint.pycqa.org/)

---

**このドキュメントは組織標準の一部です。改善案は Issue で受け付けています。**
