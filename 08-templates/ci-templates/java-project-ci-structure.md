# 一般的なJavaプロジェクトのCI構成

## 📁 推奨されるCIファイル構成

一般的なJava（Spring Boot）プロジェクトでは、以下のCIファイル構成が推奨されます。

```
<java-project-root>/.github/workflows/
├── ci.yaml                            # メインCI（ビルド、テスト、品質チェック）
├── pr-language-check.yaml             # 組織標準: PR言語チェック（必須）
└── pr-self-review-reminder.yml        # 組織標準: セルフレビューリマインダー（推奨）
```

---

## 📋 各ファイルの詳細

### 1. **ci.yaml** - メインCIパイプライン

**用途**: Java/Spring Bootプロジェクトのビルド、テスト、品質チェック

**テンプレート取得先**:
```
/devin-organization-standards/08-templates/ci-templates/java-spring-boot/ci.yaml.template
```

#### 実行される処理（順序）

```yaml
name: CI Pipeline

on:
  push:
    branches: [main, develop, 'feature/**', 'bugfix/**']
  pull_request:
    branches: [main, develop]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      # 1. コードチェックアウト
      - Checkout code
      
      # 2. Java環境セットアップ
      - Set up JDK (Temurin)
      - Cache: Gradle dependencies
      
      # 3. 権限設定
      - Grant execute permission for gradlew
      
      # ✅ 必須: コード品質チェック
      - Run Checkstyle              # コーディング規約チェック
      - Run Spotless Check          # フォーマットチェック
      
      # ✅ 必須: ビルド
      - Build (./gradlew build -x test)
      
      # ✅ 必須: テスト
      - Run Tests (./gradlew test)
      
      # ✅ 必須: カバレッジ検証（最重要）
      - Verify Test Coverage        # 閾値チェック（80%以上）
      
      # 🟡 推奨: レポート生成・アップロード
      - Generate Coverage Report
      - Upload Coverage Report
      - Upload Test Results
      - Upload Build Artifacts
```

#### 品質ゲート（CI失敗条件）

以下のいずれかが失敗するとCIが失敗し、マージがブロックされます：

1. ✅ **Checkstyle違反** → コーディング規約違反
2. ✅ **Spotless Check失敗** → コードフォーマット不適切
3. ✅ **Build失敗** → コンパイルエラー
4. ✅ **Test失敗** → テストケース不合格
5. ✅ **Coverage < 80%** → テストカバレッジ不足（最重要）

---

### 2. **pr-language-check.yaml** - PR言語チェック（必須）

**用途**: PRのタイトル・説明文が日本語で記載されているかを検証

**テンプレート取得先**:
```
/devin-organization-standards/08-templates/ci-templates/github-actions/pr-language-check.yaml
```

#### 実行される処理

```yaml
name: PR Language Check

on:
  pull_request:
    types: [opened, edited, synchronize]

jobs:
  check-language:
    runs-on: ubuntu-latest
    steps:
      # 日本語文字（ひらがな、カタカナ、漢字）の検出
      - Check PR title for Japanese
      - Check PR body for Japanese
      
      # 失敗時
      - Post comment with instructions
      - Fail CI (exit 1)
```

#### 品質ゲート

- ❌ PRタイトルが英語のみ → CI失敗
- ❌ PR説明文が英語のみ → CI失敗
- ✅ 日本語が含まれる → CI合格

**注意**: 技術用語（API、JWT、OAuth等）の英語表記は許可

---

### 3. **pr-self-review-reminder.yml** - セルフレビューリマインダー（推奨）

**用途**: PR投稿時にセルフレビューを促すリマインダーを自動投稿

**テンプレート取得先**:
```
/devin-organization-standards/08-templates/ci-templates/github-actions/pr-self-review-reminder.yml
```

#### 実行される処理

```yaml
name: PR Self-Review Reminder

on:
  pull_request:
    types: [opened]  # 初回のみ

jobs:
  remind:
    runs-on: ubuntu-latest
    steps:
      # 既存コメントチェック
      - Check if reminder already posted
      
      # リマインダー投稿（初回のみ）
      - Post reminder comment:
          "チェックリストを正しく選定し、確認していますか？"
```

#### 特徴

- ✅ 初回PR投稿時のみ実行
- ✅ 重複投稿を防止
- ✅ AIが自律的にチェックリストを選定してセルフレビュー
- ⚠️ CI失敗にはならない（リマインダーのみ）

---

## 🔄 CI実行フロー

### PRを作成した場合

```
PRを作成
  ↓
┌─────────────────────────────────────┐
│ 並行実行（3つのCIが同時に走る）      │
├─────────────────────────────────────┤
│                                     │
│ 1️⃣ ci.yaml                          │
│    - Checkstyle ✅                  │
│    - Spotless Check ✅              │
│    - Build ✅                       │
│    - Test ✅                        │
│    - Coverage Verification ✅       │
│    （約5-10分）                      │
│                                     │
│ 2️⃣ pr-language-check.yaml           │
│    - PR Title Check ✅              │
│    - PR Body Check ✅               │
│    （約10秒）                        │
│                                     │
│ 3️⃣ pr-self-review-reminder.yml      │
│    - Post Reminder Comment          │
│    （約5秒、初回のみ）               │
│                                     │
└─────────────────────────────────────┘
  ↓
すべてのCIが合格
  ↓
AI（Devin）がリマインダーを検知
  ↓
AIが自律的にセルフレビュー
  - /devin-organization-standards を読み込み
  - Javaコーディング規約を確認
  - コードレビュー基準を確認
  - セルフレビュー結果をコメント
  ↓
レビュアーがレビュー
  ↓
マージ
```

---

## 📦 セットアップ方法

### ステップ1: CIファイルをコピー

```bash
# プロジェクトのルートディレクトリで実行
cd <java-project-root>

# ディレクトリ作成
mkdir -p .github/workflows

# 1. メインCI（Java固有）
cp devin-organization-standards/08-templates/ci-templates/java-spring-boot/ci.yaml.template \
   .github/workflows/ci.yaml

# 2. PR言語チェック（組織標準・必須）
cp devin-organization-standards/08-templates/ci-templates/github-actions/pr-language-check.yaml \
   .github/workflows/pr-language-check.yaml

# 3. セルフレビューリマインダー（組織標準・推奨）
cp devin-organization-standards/08-templates/ci-templates/github-actions/pr-self-review-reminder.yml \
   .github/workflows/pr-self-review-reminder.yml
```

### ステップ2: ci.yaml のカスタマイズ

`ci.yaml` のプレースホルダーを置き換え:

```bash
# Javaバージョンを指定（例: 17）
sed -i 's/${JAVA_VERSION}/17/g' .github/workflows/ci.yaml
```

または手動で編集:
```yaml
# Before
java-version: '${JAVA_VERSION}'

# After
java-version: '17'  # プロジェクトのJavaバージョン
```

### ステップ3: build.gradle の設定

Java CI テンプレートに対応するため、`build.gradle` に以下のプラグインと設定を追加:

```bash
# テンプレートを参照
cp devin-organization-standards/08-templates/ci-templates/java-spring-boot/build.gradle.template \
   build.gradle.reference

# または手動で追加
```

必要な設定:
- ✅ Checkstyle プラグイン
- ✅ Spotless プラグイン
- ✅ JaCoCo プラグイン（カバレッジ）
- ✅ カバレッジ閾値設定（80%）

### ステップ4: コミット & プッシュ

```bash
git add .github/workflows/
git commit -m "feat: CI品質ゲートを追加

- Java CI パイプライン追加（ビルド、テスト、カバレッジ）
- PR言語チェック追加（日本語必須）
- PRセルフレビューリマインダー追加

参照: devin-organization-standards/08-templates/ci-templates/"

git push
```

### ステップ5: ブランチ保護ルールの設定

1. **GitHub リポジトリ設定**
   - Settings → Branches

2. **ブランチ保護ルールを追加**
   - Branch name pattern: `main`

3. **必須ステータスチェックを設定**
   - ✅ Require status checks to pass before merging
   - 必須チェック:
     - `Build and Test` (ci.yaml)
     - `日本語記載チェック` (pr-language-check.yaml)

---

## 📊 CI実行時間の目安

| CI | 実行時間 | 備考 |
|----|---------|------|
| **ci.yaml** | 5-10分 | プロジェクト規模による |
| **pr-language-check.yaml** | 5-10秒 | 軽量 |
| **pr-self-review-reminder.yml** | 5秒 | 初回のみ |
| **合計** | 約5-10分 | 並行実行 |

---

## 🎯 各CIの役割分担

### 言語固有（Java専用）
- **ci.yaml** 
  - ビルド
  - テスト
  - カバレッジ
  - コード品質（Checkstyle, Spotless）

### 言語非依存（全プロジェクト共通）
- **pr-language-check.yaml**
  - PR言語チェック
  - 組織標準の強制

- **pr-self-review-reminder.yml**
  - セルフレビュー促進
  - AIによる自律的なチェックリスト選定

---

## 💡 ベストプラクティス

### 1. すべてのCIを導入する

最小構成でも以下の2つは必須:
- ✅ `ci.yaml` - 品質保証の要
- ✅ `pr-language-check.yaml` - 組織標準

推奨:
- ✅ `pr-self-review-reminder.yml` - 品質向上

### 2. CI失敗は必ず修正する

CI失敗時の対応:
1. ❌ CI失敗 → ログを確認
2. 🔧 修正 → 原因に応じて対処
3. ✅ 再実行 → 自動的に再実行される
4. ✅ 合格 → マージ可能

### 3. カバレッジ閾値を維持する

```gradle
// build.gradle
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80  // 80%（必須）
            }
        }
    }
}
```

- 新規コードは必ずテストを追加
- カバレッジ低下は絶対に避ける
- テストのないコードはマージしない

### 4. コーディング規約を遵守する

```bash
# ローカルで事前チェック
./gradlew checkstyleMain checkstyleTest

# フォーマット自動修正
./gradlew spotlessApply

# カバレッジ確認
./gradlew jacocoTestCoverageVerification
```

---

## 🆘 トラブルシューティング

### CI失敗: Checkstyle違反

**原因**: コーディング規約違反

**解決**:
```bash
# 詳細を確認
./gradlew checkstyleMain

# レポート確認
cat build/reports/checkstyle/main.xml

# 修正後
git add .
git commit -m "fix: Checkstyle違反を修正"
git push
```

### CI失敗: Spotless Check

**原因**: コードフォーマット不適切

**解決**:
```bash
# 自動修正
./gradlew spotlessApply

# 確認
./gradlew spotlessCheck

# コミット
git add .
git commit -m "style: コードフォーマットを修正"
git push
```

### CI失敗: Coverage < 80%

**原因**: テストカバレッジ不足

**解決**:
1. 不足しているテストを特定
   ```bash
   ./gradlew jacocoTestReport
   open build/reports/jacoco/test/html/index.html
   ```

2. テストを追加

3. 確認
   ```bash
   ./gradlew test jacocoTestCoverageVerification
   ```

---

## 📚 関連ドキュメント

### 組織標準
- [Java CI テンプレート](../java-spring-boot/)
- [GitHub Actions テンプレート](../github-actions/)
- [Javaコーディング規約](../../01-coding-standards/java-standards.md)

### CI設定
- [build.gradle テンプレート](../java-spring-boot/build.gradle.template)
- [CI設定チェックリスト](../../00-guides/CI-SETUP-CHECKLIST.md)

---

## 🎉 まとめ

一般的なJavaプロジェクトでは、以下の3つのCIファイルが標準構成です:

1. ✅ **ci.yaml** - Java固有のビルド・テスト・品質チェック
2. ✅ **pr-language-check.yaml** - 組織標準（必須）
3. ✅ **pr-self-review-reminder.yml** - 組織標準（推奨）

この構成により:
- ✅ コード品質の自動検証
- ✅ 組織標準の強制
- ✅ AIによる自律的なセルフレビュー
- ✅ レビュー工数の削減

が実現できます！
