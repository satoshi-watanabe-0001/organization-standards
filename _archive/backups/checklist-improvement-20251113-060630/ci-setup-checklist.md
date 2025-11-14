---
title: "CI設定チェックリスト"
version: "1.0.0"
created_date: "2025-11-07"
last_updated: "2025-11-07"
status: "Active"
owner: "DevOps Team / Quality Assurance"
audience: "AI Agents, Developers"
---

# CI設定チェックリスト
## CI Setup Checklist

> プロジェクトのCI/CD設定が組織標準に準拠しているか確認するためのチェックリスト

**対象**: Devin、Cursor、開発者全員  
**目的**: 必須CI品質ゲート設定の漏れを防止し、品質基準を満たさないコードのマージを防ぐ

---

## 📌 このチェックリストの使い方

### 使用タイミング

| タイミング | 実施者 | 所要時間 | 目的 |
|-----------|--------|---------|------|
| **Phase 1 完了直後** | 実装担当AI/開発者 | 20-30分 | 初期CI設定の妥当性確認 |
| **Phase 3 実装開始前** | 実装担当AI/開発者 | 15分 | CI設定の再確認 |
| **Phase 4 レビュー時** | レビュー担当 | 10-15分 | CI実行結果の検証 |
| **CI設定変更後** | 変更者 | 15分 | 変更内容の妥当性確認 |

### 重要度の凡例

- 🔴 **必須** - 品質ゲート。未実施の場合はPRマージ不可
- 🟡 **推奨** - 実施推奨。スキップ時は理由を記録
- 🟢 **任意** - プロジェクトの特性に応じて実施

---

## 🎯 1. 必須CI品質ゲート設定 🔴

### 1.1 コード品質チェック

#### ✅ Linting (リンティング)

**目的**: コーディング規約違反の自動検出

- [ ] 🔴 言語別リンターが設定されている
  - [ ] **TypeScript/JavaScript**: ESLint実行
  - [ ] **Python**: pylint または flake8 実行
  - [ ] **Java**: Checkstyle実行

**検証方法**:
```bash
# TypeScript/JavaScript
cat .github/workflows/ci.yaml | grep -i "eslint"
# または
npm run | grep "lint"

# Python
cat .github/workflows/ci.yaml | grep -E "pylint|flake8"
# または
cat pyproject.toml | grep -E "pylint|flake8"

# Java
cat .github/workflows/ci.yaml | grep -i "checkstyle"
# または
./gradlew tasks | grep "checkstyle"
```

**期待される結果**:
- CIワークフローファイルにリンター実行ステップが存在する
- リンターエラー時にCIが失敗する設定になっている（`|| true` が無い）

**NG例**:
```yaml
# ❌ 失敗しても続行してしまう
- run: npm run lint || true
```

**OK例**:
```yaml
# ✅ エラー時にパイプライン停止
- run: npm run lint
```

---

#### ✅ コードフォーマットチェック

**目的**: コードフォーマット違反の検出（自動修正ではなくチェックのみ）

- [ ] 🔴 フォーマットチェックが設定されている
  - [ ] **TypeScript/JavaScript**: Prettier check
  - [ ] **Python**: black check または yapf check
  - [ ] **Java**: spotlessCheck

**検証方法**:
```bash
# TypeScript/JavaScript
cat .github/workflows/ci.yaml | grep -E "prettier.*check"
cat package.json | grep "format:check"

# Python
cat .github/workflows/ci.yaml | grep "black --check"

# Java
cat .github/workflows/ci.yaml | grep "spotlessCheck"
./gradlew tasks | grep "spotlessCheck"
```

**重要**: `spotlessApply` や `prettier --write` ではなく、**チェックのみ**を実行すること

**NG例**:
```yaml
# ❌ 自動修正してしまう（CI上では不適切）
- run: npm run format
- run: ./gradlew spotlessApply
```

**OK例**:
```yaml
# ✅ チェックのみ実行
- run: npm run format:check
- run: ./gradlew spotlessCheck
```

---

#### ✅ 型チェック

**目的**: 型安全性の検証（静的型付け言語の場合）

- [ ] 🔴 型チェックが設定されている
  - [ ] **TypeScript**: `tsc --noEmit` 実行
  - [ ] **Python**: mypy 実行（型ヒント使用時）

**検証方法**:
```bash
# TypeScript
cat .github/workflows/ci.yaml | grep -E "tsc|type-check"
cat package.json | grep "type-check"

# Python (型ヒント使用時)
cat .github/workflows/ci.yaml | grep "mypy"
```

---

### 1.2 ビルド検証

#### ✅ コンパイル/ビルド

**目的**: ソースコードのコンパイル可否、ビルド成功の検証

- [ ] 🔴 ビルドステージが存在する
  - [ ] `build` ジョブまたは `compile` ステップが存在
  - [ ] ビルド失敗時にCIが停止する

**検証方法**:
```bash
# 全言語共通
cat .github/workflows/ci.yaml | grep -E "build|compile"

# Java
./gradlew tasks | grep "build"

# TypeScript
npm run | grep "build"

# Python
cat setup.py  # または pyproject.toml でビルド設定確認
```

**期待される結果**:
- コンパイルエラーがある場合、CIが失敗する
- アーティファクト（jar/war/dist/wheelなど）が生成される

---

#### ✅ アーティファクト生成

**目的**: デプロイ可能な成果物の生成確認

- [ ] 🔴 成果物が正常に生成される
  - [ ] **Java**: jar/war ファイル
  - [ ] **TypeScript**: dist ディレクトリ
  - [ ] **Python**: wheel パッケージ（配布時）

**検証方法**:
```bash
# ローカルでビルド実行
./gradlew build  # Java
npm run build    # TypeScript
python -m build  # Python

# 成果物の確認
ls build/libs/*.jar        # Java (Gradle)
ls target/*.jar            # Java (Maven)
ls dist/                   # TypeScript
ls dist/*.whl              # Python
```

---

### 1.3 テスト実行と品質ゲート ⭐最重要

#### ✅ ユニットテスト実行

**目的**: 全テストの実行とパス確認

- [ ] 🔴 すべてのユニットテストが実行される
  - [ ] `test` ジョブが存在
  - [ ] テスト失敗時にCIが停止する

**検証方法**:
```bash
# 全言語共通
cat .github/workflows/ci.yaml | grep -i "test"

# Java
./gradlew tasks | grep "test"

# TypeScript
npm run | grep "test"

# Python
cat pyproject.toml | grep "pytest"
```

**期待される結果**:
- すべてのテストが実行される（特定のテストがスキップされていない）
- 1つでもテストが失敗するとCIが失敗する

---

#### ✅ カバレッジ測定

**目的**: テストカバレッジの計測

- [ ] 🔴 カバレッジ計測ツールが設定されている
  - [ ] **TypeScript**: Istanbul/Jest coverage
  - [ ] **Python**: coverage.py または pytest-cov
  - [ ] **Java**: JaCoCo

**検証方法**:
```bash
# TypeScript
cat package.json | grep "coverage"
cat jest.config.js | grep "coverage"

# Python
cat .coveragerc
cat pyproject.toml | grep "coverage"

# Java
cat build.gradle | grep "jacoco"
./gradlew tasks | grep "jacoco"
```

**期待される結果**:
- CIログでカバレッジレポートが生成される
- カバレッジ数値が確認できる

---

#### ✅ カバレッジ閾値強制 ⭐⭐最重要⭐⭐

**目的**: 組織標準の80%カバレッジ閾値を強制

- [ ] 🔴 **80%カバレッジ閾値チェックが実行されている**
  - [ ] **Java**: `jacocoTestCoverageVerification` タスクが実行される
  - [ ] **TypeScript**: `jest.config.js` に `coverageThreshold: 80` 設定
  - [ ] **Python**: `.coveragerc` に `fail_under = 80` 設定

**これが今回の問題の核心です！**

**検証方法（Java）**:

```bash
# 1. build.gradle で設定確認
cat build.gradle | grep -A 10 "jacocoTestCoverageVerification"

# 期待される出力:
# jacocoTestCoverageVerification {
#     violationRules {
#         rule {
#             limit {
#                 minimum = 0.80
#             }
#         }
#     }
# }

# 2. CI ワークフローで実行確認
cat .github/workflows/ci.yaml | grep "jacocoTestCoverageVerification"

# 期待される出力:
# - run: ./gradlew jacocoTestCoverageVerification

# 3. ローカルで実行確認
./gradlew jacocoTestCoverageVerification
```

**検証方法（TypeScript）**:

```bash
# 1. jest.config.js で設定確認
cat jest.config.js | grep -A 8 "coverageThreshold"

# 期待される出力:
# coverageThreshold: {
#   global: {
#     lines: 80,
#     functions: 80,
#     branches: 80,
#     statements: 80
#   }
# }

# 2. CI ワークフローで実行確認
cat .github/workflows/ci.yaml | grep "test:coverage"

# 3. ローカルで実行確認
npm run test:coverage
```

**検証方法（Python）**:

```bash
# 1. .coveragerc で設定確認
cat .coveragerc | grep "fail_under"

# 期待される出力:
# fail_under = 80

# 2. CI ワークフローで実行確認
cat .github/workflows/ci.yaml | grep -E "pytest.*cov|coverage"

# 3. ローカルで実行確認
pytest --cov --cov-fail-under=80
```

**期待される結果**:
- カバレッジが80%未満の場合、CIが失敗する
- CIログに "Coverage check failed" または類似のメッセージが表示される

**NG例（Java）**:
```yaml
# ❌ レポート生成のみで閾値チェックなし
- run: ./gradlew test jacocoTestReport
```

```gradle
// ❌ build.gradle に jacocoTestCoverageVerification が無い
plugins {
    id 'jacoco'
}

jacoco {
    toolVersion = "0.8.8"
}

// ここで終わり → 閾値チェックが実行されない！
```

**OK例（Java）**:
```yaml
# ✅ 閾値チェックを明示的に実行
- name: Verify Test Coverage
  run: ./gradlew jacocoTestCoverageVerification
```

```gradle
// ✅ build.gradle に閾値設定がある
plugins {
    id 'jacoco'
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80  // 80%閾値
            }
        }
    }
}

// check タスクに依存させる（推奨）
check.dependsOn jacocoTestCoverageVerification
```

---

## 📋 2. 言語別必須設定マトリクス 🔴

### 2.1 Java (Spring Boot) プロジェクト

#### build.gradle 必須設定チェック

```gradle
// ✅ このブロックがすべて存在するか確認

plugins {
    id 'java'
    id 'org.springframework.boot' version '3.x.x'
    id 'io.spring.dependency-management' version '1.x.x'
    id 'jacoco'                        // ✅ JaCoCo plugin
    id 'com.diffplug.spotless' version '6.x.x'  // ✅ Spotless plugin
    id 'checkstyle'                    // ✅ Checkstyle plugin
}

// ✅ JaCoCo カバレッジ閾値設定
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80  // 80%閾値
            }
        }
    }
}

// ✅ check タスクに依存関係を追加（推奨）
check.dependsOn jacocoTestCoverageVerification

// ✅ Spotless 設定
spotless {
    java {
        googleJavaFormat()  // または eclipse() / prettier()
        target 'src/**/*.java'
    }
}

// ✅ Checkstyle 設定
checkstyle {
    toolVersion = '10.x'
    configFile = file('config/checkstyle/checkstyle.xml')
}
```

**検証コマンド**:
```bash
# すべての品質チェックタスクが存在するか確認
./gradlew tasks --group verification

# 期待される出力（最低限これらが含まれる）:
# - checkstyleMain
# - checkstyleTest
# - spotlessCheck
# - test
# - jacocoTestReport
# - jacocoTestCoverageVerification

# 実際に実行して動作確認
./gradlew spotlessCheck checkstyleMain test jacocoTestCoverageVerification
```

---

#### .github/workflows/ci.yaml 必須設定チェック

```yaml
name: CI Pipeline

on:
  push:
    branches: [main, develop, 'feature/**']
  pull_request:
    branches: [main, develop]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'gradle'
      
      # ✅ 必須: Checkstyle実行
      - name: Run Checkstyle
        run: ./gradlew checkstyleMain checkstyleTest
      
      # ✅ 必須: Spotless Check実行
      - name: Run Spotless Check
        run: ./gradlew spotlessCheck
      
      # ✅ 必須: Build実行
      - name: Build
        run: ./gradlew build -x test
      
      # ✅ 必須: Test実行
      - name: Run Tests
        run: ./gradlew test
      
      # ✅ 必須: カバレッジ閾値検証（最重要）
      - name: Verify Test Coverage
        run: ./gradlew jacocoTestCoverageVerification
      
      # 🟡 推奨: カバレッジレポートのアップロード
      - name: Upload Coverage Report
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: coverage-report
          path: build/reports/jacoco/test/html/
```

**チェックポイント**:
- [ ] すべての必須ステップ（✅マーク）が存在する
- [ ] `jacocoTestCoverageVerification` が **独立したステップ** として実行されている
- [ ] エラー時にパイプラインが停止する（`|| true` が無い）

---

### 2.2 TypeScript/JavaScript (Node.js) プロジェクト

#### package.json 必須スクリプトチェック

```json
{
  "name": "your-project",
  "version": "1.0.0",
  "scripts": {
    // ✅ 必須スクリプト
    "lint": "eslint src/**/*.ts",
    "format:check": "prettier --check .",
    "type-check": "tsc --noEmit",
    "test": "jest",
    "test:coverage": "jest --coverage",
    
    // 🟡 推奨スクリプト
    "format": "prettier --write .",
    "lint:fix": "eslint src/**/*.ts --fix"
  },
  "devDependencies": {
    // ✅ 必須依存関係
    "eslint": "^8.x.x",
    "prettier": "^3.x.x",
    "typescript": "^5.x.x",
    "jest": "^29.x.x",
    "@types/jest": "^29.x.x",
    "ts-jest": "^29.x.x"
  }
}
```

---

#### jest.config.js 必須設定チェック

```javascript
module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'node',
  
  // ✅ 必須: カバレッジ収集設定
  collectCoverage: true,
  collectCoverageFrom: [
    'src/**/*.ts',
    '!src/**/*.d.ts',
    '!src/**/*.test.ts',
    '!src/**/__tests__/**'
  ],
  
  // ✅ 必須: カバレッジ閾値設定（80%）
  coverageThreshold: {
    global: {
      lines: 80,
      functions: 80,
      branches: 80,
      statements: 80
    }
  },
  
  // 🟡 推奨: レポート形式
  coverageReporters: ['text', 'html', 'lcov'],
  
  // 🟡 推奨: テストパス設定
  testMatch: [
    '**/__tests__/**/*.test.ts',
    '**/?(*.)+(spec|test).ts'
  ]
};
```

**検証コマンド**:
```bash
# スクリプト存在確認
npm run | grep -E "lint|format:check|type-check|test:coverage"

# 実際に実行して動作確認
npm run lint
npm run format:check
npm run type-check
npm run test:coverage

# カバレッジ閾値チェック確認
# → カバレッジが80%未満だとエラーで終了するはず
```

---

#### .github/workflows/ci.yaml 必須設定チェック

```yaml
name: CI Pipeline

on:
  push:
    branches: [main, develop, 'feature/**']
  pull_request:
    branches: [main, develop]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v3
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'
      
      - name: Install dependencies
        run: npm ci
      
      # ✅ 必須: Lint実行
      - name: Run Lint
        run: npm run lint
      
      # ✅ 必須: Format Check実行
      - name: Check Code Formatting
        run: npm run format:check
      
      # ✅ 必須: Type Check実行
      - name: Type Check
        run: npm run type-check
      
      # ✅ 必須: Build実行
      - name: Build
        run: npm run build
      
      # ✅ 必須: Test実行（カバレッジ閾値チェック含む）
      - name: Run Tests with Coverage
        run: npm run test:coverage
      
      # 🟡 推奨: カバレッジレポートのアップロード
      - name: Upload Coverage Report
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: coverage-report
          path: coverage/
```

---

### 2.3 Python プロジェクト

#### pyproject.toml 必須設定チェック

```toml
[tool.poetry]
name = "your-project"
version = "1.0.0"
description = ""
authors = ["Your Name <you@example.com>"]

[tool.poetry.dependencies]
python = "^3.11"

[tool.poetry.group.dev.dependencies]
# ✅ 必須依存関係
pytest = "^7.x"
pytest-cov = "^4.x"
pylint = "^3.x"
black = "^23.x"
mypy = "^1.x"

# ✅ 必須: pytest設定
[tool.pytest.ini_options]
testpaths = ["tests"]
python_files = "test_*.py"
python_classes = "Test*"
python_functions = "test_*"

# ✅ 必須: カバレッジ設定
addopts = "--cov=src --cov-report=html --cov-report=term --cov-fail-under=80"

# ✅ 必須: black設定
[tool.black]
line-length = 88
target-version = ['py311']

# ✅ 必須: mypy設定
[tool.mypy]
python_version = "3.11"
warn_return_any = true
warn_unused_configs = true
disallow_untyped_defs = true
```

---

#### .coveragerc 必須設定チェック（代替設定ファイル）

```ini
[run]
source = src
omit =
    */tests/*
    */test_*.py
    */__pycache__/*
    */site-packages/*

[report]
# ✅ 必須: カバレッジ閾値設定（80%）
fail_under = 80

# 🟡 推奨: レポート設定
precision = 2
show_missing = true
skip_covered = false

[html]
directory = htmlcov
```

**検証コマンド**:
```bash
# 設定確認
cat pyproject.toml | grep -E "pytest-cov|black|pylint|mypy"
cat .coveragerc | grep "fail_under"

# 実際に実行して動作確認
pylint src/
black --check src/
mypy src/
pytest --cov --cov-fail-under=80

# カバレッジ閾値チェック確認
# → カバレッジが80%未満だとエラーで終了するはず
```

---

#### .github/workflows/ci.yaml 必須設定チェック

```yaml
name: CI Pipeline

on:
  push:
    branches: [main, develop, 'feature/**']
  pull_request:
    branches: [main, develop]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v3
      
      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.11'
          cache: 'pip'
      
      - name: Install dependencies
        run: |
          pip install --upgrade pip
          pip install -r requirements.txt
          pip install pytest pytest-cov pylint black mypy
      
      # ✅ 必須: Lint実行
      - name: Run Pylint
        run: pylint src/
      
      # ✅ 必須: Format Check実行
      - name: Check Code Formatting
        run: black --check src/
      
      # ✅ 必須: Type Check実行
      - name: Type Check
        run: mypy src/
      
      # ✅ 必須: Test実行（カバレッジ閾値チェック含む）
      - name: Run Tests with Coverage
        run: pytest --cov --cov-fail-under=80
      
      # 🟡 推奨: カバレッジレポートのアップロード
      - name: Upload Coverage Report
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: coverage-report
          path: htmlcov/
```

---

## 🔍 3. CI設定検証手順（段階的チェック）

### Phase 1: 設定ファイル存在確認（5分）

```bash
# 1. CIワークフローファイル確認
test -f .github/workflows/ci.yaml && echo "✅ CI workflow exists" || echo "❌ CI workflow NOT found"

# 2. 言語別設定ファイル確認
# Java
test -f build.gradle && echo "✅ build.gradle exists" || echo "⚠️ Not a Java project"

# TypeScript
test -f package.json && echo "✅ package.json exists" || echo "⚠️ Not a Node.js project"
test -f jest.config.js && echo "✅ jest.config.js exists" || echo "❌ Jest config NOT found"

# Python
test -f pyproject.toml && echo "✅ pyproject.toml exists" || echo "⚠️ Not a Poetry project"
test -f .coveragerc && echo "✅ .coveragerc exists" || echo "⚠️ Coverage config in pyproject.toml"
```

---

### Phase 2: 品質ゲートツール設定確認（10分）

#### Java プロジェクト

```bash
echo "=== Java CI設定検証 ==="

# 1. Gradle pluginsチェック
echo -e "\n1. Gradle Plugins:"
grep -E "id '(jacoco|spotless|checkstyle)'" build.gradle

# 2. JaCoCo 閾値設定チェック
echo -e "\n2. JaCoCo Coverage Threshold:"
grep -A 5 "jacocoTestCoverageVerification" build.gradle | grep "minimum"

# 3. CI ワークフロー必須ステップチェック
echo -e "\n3. CI Workflow Steps:"
grep -E "spotlessCheck|checkstyleMain|jacocoTestCoverageVerification" .github/workflows/ci.yaml

# 4. 利用可能タスク一覧
echo -e "\n4. Available Tasks:"
./gradlew tasks --group verification | grep -E "spotless|checkstyle|jacoco"
```

**期待される出力**:
```
=== Java CI設定検証 ===

1. Gradle Plugins:
    id 'jacoco'
    id 'com.diffplug.spotless' version '6.x.x'
    id 'checkstyle'

2. JaCoCo Coverage Threshold:
                minimum = 0.80

3. CI Workflow Steps:
      - run: ./gradlew spotlessCheck
      - run: ./gradlew checkstyleMain checkstyleTest
      - run: ./gradlew jacocoTestCoverageVerification

4. Available Tasks:
checkstyleMain
checkstyleTest
spotlessCheck
spotlessApply
jacocoTestReport
jacocoTestCoverageVerification
```

---

### 5.2 ドキュメントコメント品質ゲート検証 ⭐NEW

**重要**: ドキュメントコメント（Javadoc/JSDoc/Docstring）の必須化は品質ゲートの一部です。

📖 **詳細実装手順**: [DOCUMENTATION-COMMENT-ISSUE-SOLUTION.md](./DOCUMENTATION-COMMENT-ISSUE-SOLUTION.md)

---

#### Java - Checkstyle (Javadoc必須化)

**検証項目**:
- [ ] `config/checkstyle/checkstyle.xml` が存在する
- [ ] `JavadocMethod` モジュールが設定されている
- [ ] `JavadocType` モジュールが設定されている
- [ ] `build.gradle` の `checkstyleMain` タスクが有効化されている
- [ ] CI ワークフローで `./gradlew checkstyleMain` が実行されている

**検証コマンド**:
```bash
echo "=== Java Javadoc 品質ゲート検証 ==="

# 1. Checkstyle設定ファイルの存在確認
echo -e "\n1. Checkstyle Config:"
ls -lh config/checkstyle/checkstyle.xml

# 2. Javadoc必須化ルールの確認
echo -e "\n2. Javadoc Rules:"
grep -A 3 'module name="JavadocMethod"' config/checkstyle/checkstyle.xml
grep -A 3 'module name="JavadocType"' config/checkstyle/checkstyle.xml

# 3. build.gradle のcheckstyleMain有効化確認
echo -e "\n3. Checkstyle Task:"
grep -A 5 "checkstyle {" build.gradle

# 4. CIワークフローでの実行確認
echo -e "\n4. CI Workflow:"
grep "checkstyleMain" .github/workflows/ci.yaml
```

**期待される出力**:
```
=== Java Javadoc 品質ゲート検証 ===

1. Checkstyle Config:
-rw-r--r-- 1 user user 3.5K config/checkstyle/checkstyle.xml

2. Javadoc Rules:
    <module name="JavadocMethod">
        <property name="accessModifiers" value="public, protected"/>
        <property name="severity" value="error"/>
    </module>
    <module name="JavadocType">
        <property name="scope" value="public"/>
        <property name="severity" value="error"/>
    </module>

3. Checkstyle Task:
checkstyle {
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
    toolVersion = "10.12.0"
}

4. CI Workflow:
      - run: ./gradlew checkstyleMain checkstyleTest
```

📖 **詳細設定手順**: [DOCUMENTATION-COMMENT-ISSUE-SOLUTION.md - Java Checkstyle設定](./DOCUMENTATION-COMMENT-ISSUE-SOLUTION.md#222-java-checkstyle設定)

---

#### TypeScript - ESLint (JSDoc必須化)

**検証項目**:
- [ ] `eslint-plugin-jsdoc` がインストールされている
- [ ] `.eslintrc.json` に `jsdoc` プラグインが設定されている
- [ ] `jsdoc/require-jsdoc` ルールが有効化されている
- [ ] CI ワークフローで `npm run lint` が実行されている

**検証コマンド**:
```bash
echo "=== TypeScript JSDoc 品質ゲート検証 ==="

# 1. eslint-plugin-jsdocのインストール確認
echo -e "\n1. ESLint JSDoc Plugin:"
npm list eslint-plugin-jsdoc

# 2. .eslintrc.json のJSDocルール確認
echo -e "\n2. JSDoc Rules:"
grep -A 10 '"plugins"' .eslintrc.json | grep jsdoc
grep -A 15 '"jsdoc/require-jsdoc"' .eslintrc.json

# 3. CIワークフローでの実行確認
echo -e "\n3. CI Workflow:"
grep "npm run lint" .github/workflows/ci.yaml
```

**期待される出力**:
```
=== TypeScript JSDoc 品質ゲート検証 ===

1. ESLint JSDoc Plugin:
eslint-plugin-jsdoc@48.0.0

2. JSDoc Rules:
  "plugins": ["jsdoc"],
  "jsdoc/require-jsdoc": ["error", {
    "require": {
      "FunctionDeclaration": true,
      "ClassDeclaration": true,
      "MethodDefinition": true
    },
    "contexts": ["TSInterfaceDeclaration"]
  }],

3. CI Workflow:
      - run: npm run lint
```

📖 **詳細設定手順**: [DOCUMENTATION-COMMENT-ISSUE-SOLUTION.md - TypeScript ESLint設定](./DOCUMENTATION-COMMENT-ISSUE-SOLUTION.md#223-typescript-eslint設定)

---

#### Python - Pylint/pydocstyle (Docstring必須化)

**検証項目**:
- [ ] `pylint` がインストールされている
- [ ] `pydocstyle` がインストールされている
- [ ] `.pylintrc` に `missing-docstring` 設定がある
- [ ] `.pydocstyle` に Google Style 設定がある
- [ ] CI ワークフローで `pylint` と `pydocstyle` が実行されている

**検証コマンド**:
```bash
echo "=== Python Docstring 品質ゲート検証 ==="

# 1. Pylint/pydocstyleのインストール確認
echo -e "\n1. Python Linting Tools:"
pip list | grep -E "pylint|pydocstyle"

# 2. .pylintrc の設定確認
echo -e "\n2. Pylint Config:"
grep -A 3 "missing-docstring" .pylintrc

# 3. .pydocstyle の設定確認
echo -e "\n3. Pydocstyle Config:"
cat .pydocstyle

# 4. CIワークフローでの実行確認
echo -e "\n4. CI Workflow:"
grep -E "pylint|pydocstyle" .github/workflows/ci.yaml
```

**期待される出力**:
```
=== Python Docstring 品質ゲート検証 ===

1. Python Linting Tools:
pylint                3.0.3
pydocstyle            6.3.0

2. Pylint Config:
[MESSAGES CONTROL]
enable=missing-docstring

3. Pydocstyle Config:
[pydocstyle]
convention=google
add-ignore=D100,D104

4. CI Workflow:
      - run: pylint src/
      - run: pydocstyle src/
```

📖 **詳細設定手順**: [DOCUMENTATION-COMMENT-ISSUE-SOLUTION.md - Python Pylint/pydocstyle設定](./DOCUMENTATION-COMMENT-ISSUE-SOLUTION.md#224-python-pylintpydocstyle設定)

---



# CI-SETUP-CHECKLIST - SQL品質ゲート追加セクション

> **統合先**: `00-guides/CI-SETUP-CHECKLIST.md`  
> **挿入位置**: Section 5.3（新規セクション）「ドキュメントコメント品質ゲート」の後

---

## 5.3 SQLマイグレーションコメント品質ゲート

### 目的

Flywayマイグレーションファイルが組織標準（`01-coding-standards/sql-standards.md`）に準拠していることを自動的に検証し、コメント不足によるレビュー指摘を防止する。

### 対象ファイル

```yaml
対象パターン:
  - src/main/resources/db/migration/**/*.sql
  - src/main/resources/db/migrations/**/*.sql
  - **/flyway/**/*.sql
  - **/liquibase/**/*.sql

除外パターン:
  - **/*_test.sql
  - **/*_fixture.sql
```

---

## ステップ5.3.1: GitHub Actions ワークフロー作成

### ファイル作成

**パス**: `.github/workflows/sql-migration-comment-check.yml`

```yaml
name: SQL Migration Comment Quality Gate

on:
  pull_request:
    paths:
      - 'src/main/resources/db/migration/**/*.sql'
      - 'src/main/resources/db/migrations/**/*.sql'
      - '**/flyway/**/*.sql'
      - '**/liquibase/**/*.sql'

jobs:
  sql-comment-check:
    name: SQL Migration Comment Check
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        with:
          fetch-depth: 0  # 全履歴取得（差分比較用）

      - name: Get changed SQL files
        id: changed-files
        run: |
          # PRで変更されたSQLファイルを取得
          CHANGED_FILES=$(git diff --name-only origin/${{ github.base_ref }}...HEAD | \
            grep -E '\.(sql)$' | \
            grep -v '_test\.sql$' | \
            grep -v '_fixture\.sql$' || true)
          
          if [ -z "$CHANGED_FILES" ]; then
            echo "changed_files=" >> $GITHUB_OUTPUT
            echo "has_changes=false" >> $GITHUB_OUTPUT
          else
            # 改行をスペースに変換
            FILES_SPACE=$(echo "$CHANGED_FILES" | tr '\n' ' ')
            echo "changed_files=$FILES_SPACE" >> $GITHUB_OUTPUT
            echo "has_changes=true" >> $GITHUB_OUTPUT
          fi
          
          echo "Changed SQL files:"
          echo "$CHANGED_FILES"

      - name: Check SQL Migration Comments
        if: steps.changed-files.outputs.has_changes == 'true'
        id: check
        run: |
          #!/bin/bash
          set -e
          
          # 色定義
          RED='\033[0;31m'
          GREEN='\033[0;32m'
          YELLOW='\033[1;33m'
          BLUE='\033[0;34m'
          NC='\033[0m' # No Color
          
          echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
          echo -e "${BLUE}🔍 SQLマイグレーションコメント品質チェック${NC}"
          echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
          echo ""
          
          CHANGED_FILES="${{ steps.changed-files.outputs.changed_files }}"
          ERRORS=0
          WARNINGS=0
          CHECKED_FILES=0
          
          for FILE in $CHANGED_FILES; do
            # ファイル存在確認
            if [ ! -f "$FILE" ]; then
              echo -e "${YELLOW}⚠️  スキップ: $FILE (ファイルが見つかりません)${NC}"
              continue
            fi
            
            ((CHECKED_FILES++))
            echo ""
            echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            echo -e "${BLUE}📄 チェック中: $FILE${NC}"
            echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            
            FILE_ERRORS=0
            FILE_WARNINGS=0
            
            # ============================================================
            # 1. ファイル冒頭コメントのチェック
            # ============================================================
            echo ""
            echo "📋 [1/4] ファイル冒頭コメントをチェック中..."
            
            if ! grep -q "^/\*" "$FILE"; then
              echo -e "${RED}❌ エラー: ファイル冒頭の複数行コメントがありません${NC}"
              echo "   必須セクション:"
              echo "   - 【目的】"
              echo "   - 【ビジネス背景】（チケット番号）"
              echo "   - 【主な設計判断】"
              echo "   - 【想定クエリパターン】"
              echo "   - 【インデックス方針】"
              ((ERRORS++))
              ((FILE_ERRORS++))
            else
              # ヘッダーコメント抽出
              HEADER_COMMENT=$(sed -n '/^\/\*/,/\*\//p' "$FILE")
              
              # 必須キーワードチェック
              MISSING_SECTIONS=""
              
              if ! echo "$HEADER_COMMENT" | grep -qi "目的\|purpose"; then
                MISSING_SECTIONS="${MISSING_SECTIONS}- 【目的】セクション\n"
                ((FILE_WARNINGS++))
              fi
              
              if ! echo "$HEADER_COMMENT" | grep -qi "チケット\|ticket\|EC-[0-9]\|JIRA"; then
                MISSING_SECTIONS="${MISSING_SECTIONS}- チケット番号\n"
                ((FILE_WARNINGS++))
              fi
              
              if ! echo "$HEADER_COMMENT" | grep -qi "設計判断\|design decision"; then
                MISSING_SECTIONS="${MISSING_SECTIONS}- 【主な設計判断】セクション\n"
                ((FILE_WARNINGS++))
              fi
              
              if ! echo "$HEADER_COMMENT" | grep -qi "想定クエリ\|query pattern\|expected query"; then
                MISSING_SECTIONS="${MISSING_SECTIONS}- 【想定クエリパターン】セクション\n"
                ((FILE_WARNINGS++))
              fi
              
              if ! echo "$HEADER_COMMENT" | grep -qi "インデックス\|index"; then
                MISSING_SECTIONS="${MISSING_SECTIONS}- 【インデックス方針】セクション\n"
                ((FILE_WARNINGS++))
              fi
              
              if [ -n "$MISSING_SECTIONS" ]; then
                echo -e "${YELLOW}⚠️  警告: 以下のセクションが見つかりません:${NC}"
                echo -e "$MISSING_SECTIONS"
                ((WARNINGS += FILE_WARNINGS))
              else
                echo -e "${GREEN}✅ ファイル冒頭コメント: OK${NC}"
              fi
            fi
            
            # ============================================================
            # 2. CREATE INDEXのチェック
            # ============================================================
            echo ""
            echo "🔍 [2/4] インデックスコメントをチェック中..."
            
            INDEX_COUNT=$(grep -c "^CREATE INDEX\|^CREATE UNIQUE INDEX" "$FILE" || true)
            
            if [ "$INDEX_COUNT" -eq 0 ]; then
              echo -e "${GREEN}ℹ️  インデックスなし（スキップ）${NC}"
            else
              echo "   📊 検出されたインデックス数: $INDEX_COUNT"
              
              # 各インデックスのチェック
              INDEX_LINES=$(grep -n "^CREATE INDEX\|^CREATE UNIQUE INDEX" "$FILE" | cut -d: -f1)
              INDEX_NUM=0
              
              for LINE_NUM in $INDEX_LINES; do
                ((INDEX_NUM++))
                
                # インデックス名取得
                INDEX_LINE=$(sed -n "${LINE_NUM}p" "$FILE")
                INDEX_NAME=$(echo "$INDEX_LINE" | sed -n 's/.*CREATE \(UNIQUE \)\?INDEX \([^ ]*\).*/\2/p')
                
                echo ""
                echo "   🔹 インデックス $INDEX_NUM/$INDEX_COUNT: $INDEX_NAME (行 $LINE_NUM)"
                
                # インデックス作成の10行前を確認
                START_LINE=$((LINE_NUM - 10))
                if [ $START_LINE -lt 1 ]; then
                  START_LINE=1
                fi
                
                CONTEXT=$(sed -n "${START_LINE},${LINE_NUM}p" "$FILE")
                
                # インラインコメントチェック
                if ! echo "$CONTEXT" | grep -q "^--"; then
                  echo -e "   ${RED}❌ エラー: インデックス '$INDEX_NAME' の前にインラインコメントがありません${NC}"
                  echo "   必須項目: 目的、想定クエリ、実行頻度、パフォーマンス効果"
                  ((ERRORS++))
                  ((FILE_ERRORS++))
                else
                  # コメント内容の詳細チェック
                  INLINE_COMMENT=$(echo "$CONTEXT" | grep "^--" | tail -20)
                  
                  MISSING_ITEMS=""
                  if ! echo "$INLINE_COMMENT" | grep -qi "目的\|purpose"; then
                    MISSING_ITEMS="${MISSING_ITEMS}目的, "
                  fi
                  if ! echo "$INLINE_COMMENT" | grep -qi "クエリ\|query\|SELECT"; then
                    MISSING_ITEMS="${MISSING_ITEMS}想定クエリ, "
                  fi
                  if ! echo "$INLINE_COMMENT" | grep -qi "頻度\|frequency"; then
                    MISSING_ITEMS="${MISSING_ITEMS}実行頻度, "
                  fi
                  
                  if [ -n "$MISSING_ITEMS" ]; then
                    echo -e "   ${YELLOW}⚠️  警告: コメントに不足項目があります: ${MISSING_ITEMS%??}${NC}"
                    ((FILE_WARNINGS++))
                  else
                    echo -e "   ${GREEN}✅ インラインコメント: OK${NC}"
                  fi
                fi
              done
              
              # COMMENT ON INDEX のチェック
              echo ""
              COMMENT_INDEX_COUNT=$(grep -c "^COMMENT ON INDEX" "$FILE" || true)
              echo "   📝 COMMENT ON INDEX: $COMMENT_INDEX_COUNT/$INDEX_COUNT"
              
              if [ "$COMMENT_INDEX_COUNT" -lt "$INDEX_COUNT" ]; then
                MISSING_COUNT=$((INDEX_COUNT - COMMENT_INDEX_COUNT))
                echo -e "   ${RED}❌ エラー: COMMENT ON INDEX が $MISSING_COUNT 個不足しています${NC}"
                echo "   全てのインデックスに COMMENT ON INDEX を追加してください"
                ((ERRORS++))
                ((FILE_ERRORS++))
              else
                echo -e "   ${GREEN}✅ COMMENT ON INDEX: OK${NC}"
              fi
            fi
            
            # ============================================================
            # 3. FOREIGN KEYのチェック
            # ============================================================
            echo ""
            echo "🔗 [3/4] 外部キー制約コメントをチェック中..."
            
            FK_COUNT=$(grep -c "FOREIGN KEY\|REFERENCES" "$FILE" || true)
            
            if [ "$FK_COUNT" -eq 0 ]; then
              echo -e "${GREEN}ℹ️  外部キーなし（スキップ）${NC}"
            else
              echo "   🔗 検出された外部キー: $FK_COUNT"
              
              COMMENT_CONSTRAINT_COUNT=$(grep -c "^COMMENT ON CONSTRAINT" "$FILE" || true)
              
              if [ "$COMMENT_CONSTRAINT_COUNT" -eq 0 ]; then
                echo -e "   ${YELLOW}⚠️  推奨: COMMENT ON CONSTRAINT を追加してください${NC}"
                echo "   外部キー制約の目的・動作（ON DELETE CASCADE等）を記録することを推奨"
                ((FILE_WARNINGS++))
              else
                echo -e "   ${GREEN}✅ COMMENT ON CONSTRAINT: $COMMENT_CONSTRAINT_COUNT 個記載${NC}"
              fi
            fi
            
            # ============================================================
            # 4. COMMENT ON TABLE/COLUMN のチェック
            # ============================================================
            echo ""
            echo "📝 [4/4] テーブル・カラムコメントをチェック中..."
            
            CREATE_TABLE_COUNT=$(grep -c "^CREATE TABLE" "$FILE" || true)
            
            if [ "$CREATE_TABLE_COUNT" -eq 0 ]; then
              echo -e "${GREEN}ℹ️  テーブル作成なし（スキップ）${NC}"
            else
              echo "   📊 テーブル作成: $CREATE_TABLE_COUNT"
              
              if ! grep -q "^COMMENT ON TABLE" "$FILE"; then
                echo -e "   ${YELLOW}⚠️  警告: COMMENT ON TABLE がありません${NC}"
                ((FILE_WARNINGS++))
              else
                echo -e "   ${GREEN}✅ COMMENT ON TABLE: OK${NC}"
              fi
              
              if ! grep -q "^COMMENT ON COLUMN" "$FILE"; then
                echo -e "   ${YELLOW}⚠️  警告: COMMENT ON COLUMN がありません${NC}"
                echo "   主要カラムにはコメントを追加することを推奨"
                ((FILE_WARNINGS++))
              else
                COLUMN_COMMENT_COUNT=$(grep -c "^COMMENT ON COLUMN" "$FILE")
                echo -e "   ${GREEN}✅ COMMENT ON COLUMN: $COLUMN_COMMENT_COUNT 個記載${NC}"
              fi
            fi
            
            # ファイルごとのサマリー
            echo ""
            echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            if [ $FILE_ERRORS -eq 0 ] && [ $FILE_WARNINGS -eq 0 ]; then
              echo -e "${GREEN}✅ $FILE: すべてのチェックに合格${NC}"
            elif [ $FILE_ERRORS -gt 0 ]; then
              echo -e "${RED}❌ $FILE: エラー ${FILE_ERRORS} 件、警告 ${FILE_WARNINGS} 件${NC}"
            else
              echo -e "${YELLOW}⚠️  $FILE: 警告 ${FILE_WARNINGS} 件${NC}"
            fi
            echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            
            ((WARNINGS += FILE_WARNINGS))
          done
          
          # ============================================================
          # 最終結果サマリー
          # ============================================================
          echo ""
          echo ""
          echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
          echo -e "${BLUE}📊 チェック結果サマリー${NC}"
          echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
          echo ""
          echo "   チェック対象ファイル: $CHECKED_FILES"
          echo -e "   ❌ エラー: $ERRORS"
          echo -e "   ⚠️  警告: $WARNINGS"
          echo ""
          
          # 結果判定
          if [ $ERRORS -gt 0 ]; then
            echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            echo -e "${RED}💥 品質ゲート: 失敗${NC}"
            echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            echo ""
            echo "🔧 修正方法:"
            echo "   1. organization-standards/01-coding-standards/sql-standards.md を確認"
            echo "   2. 00-guides/SQL-MIGRATION-COMMENT-SOLUTION.md のテンプレートを使用"
            echo "   3. 00-guides/phase-guides/phase-3-implementation-guide.md Section 3.8 を参照"
            echo ""
            echo "📚 参考リソース:"
            echo "   - SQL標準: organization-standards/01-coding-standards/sql-standards.md"
            echo "   - 解決策: 00-guides/SQL-MIGRATION-COMMENT-SOLUTION.md"
            echo "   - 実装ガイド: 00-guides/phase-guides/phase-3-implementation-guide.md"
            echo ""
            
            # エラー詳細をGitHub出力に保存
            echo "error_count=$ERRORS" >> $GITHUB_OUTPUT
            echo "warning_count=$WARNINGS" >> $GITHUB_OUTPUT
            echo "result=failure" >> $GITHUB_OUTPUT
            
            exit 1
            
          elif [ $WARNINGS -gt 0 ]; then
            echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            echo -e "${YELLOW}⚠️  品質ゲート: 警告あり（マージ可能）${NC}"
            echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            echo ""
            echo "💡 推奨事項:"
            echo "   警告項目を修正することで、コード品質がさらに向上します"
            echo ""
            
            echo "error_count=0" >> $GITHUB_OUTPUT
            echo "warning_count=$WARNINGS" >> $GITHUB_OUTPUT
            echo "result=warning" >> $GITHUB_OUTPUT
            
            exit 0
            
          else
            echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            echo -e "${GREEN}✅ 品質ゲート: 合格${NC}"
            echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            echo ""
            echo "🎉 すべてのチェックに合格しました！"
            echo ""
            
            echo "error_count=0" >> $GITHUB_OUTPUT
            echo "warning_count=0" >> $GITHUB_OUTPUT
            echo "result=success" >> $GITHUB_OUTPUT
            
            exit 0
          fi

      - name: Comment PR (on failure)
        if: failure() && steps.check.outputs.result == 'failure'
        uses: actions/github-script@v7
        with:
          script: |
            const errorCount = '${{ steps.check.outputs.error_count }}';
            const warningCount = '${{ steps.check.outputs.warning_count }}';
            
            const body = `## ❌ SQLマイグレーションコメント品質ゲート: 失敗

**検出された問題:**
- ❌ エラー: ${errorCount} 件
- ⚠️  警告: ${warningCount} 件

### 📋 必須対応項目

#### ファイル冒頭コメント
- [ ] 複数行コメント (\`/* ... */\`) が存在する
- [ ] 【目的】セクションが記載されている
- [ ] 【ビジネス背景】にチケット番号が記載されている
- [ ] 【主な設計判断】が記載されている
- [ ] 【想定クエリパターン】が3つ以上記載されている
- [ ] 【インデックス方針】が記載されている

#### インデックスコメント
- [ ] 各 \`CREATE INDEX\` の前にインラインコメント (\`--\`) がある
- [ ] インラインコメントに以下が含まれる:
  - [ ] 目的
  - [ ] 想定クエリ
  - [ ] 実行頻度
  - [ ] パフォーマンス効果
- [ ] 各インデックスに \`COMMENT ON INDEX\` がある

#### 推奨対応項目
- [ ] 外部キー制約に \`COMMENT ON CONSTRAINT\` がある
- [ ] テーブル・カラムに \`COMMENT ON TABLE/COLUMN\` がある

### 🔧 修正方法

1. **テンプレートを使用**
   - [\`SQL-MIGRATION-COMMENT-SOLUTION.md\`](../blob/main/00-guides/SQL-MIGRATION-COMMENT-SOLUTION.md) のテンプレートをコピー

2. **実装ガイド参照**
   - [\`phase-3-implementation-guide.md\` Section 3.8](../blob/main/00-guides/phase-guides/phase-3-implementation-guide.md) を確認

3. **SQL標準確認**
   - [\`sql-standards.md\`](../blob/main/01-coding-standards/sql-standards.md) で組織標準を確認

### 📚 参考ドキュメント

| ドキュメント | 内容 |
|------------|------|
| [SQL標準](../blob/main/01-coding-standards/sql-standards.md) | 組織のSQL標準 |
| [解決策ガイド](../blob/main/00-guides/SQL-MIGRATION-COMMENT-SOLUTION.md) | 完全なテンプレートと例 |
| [実装ガイド](../blob/main/00-guides/phase-guides/phase-3-implementation-guide.md) | Phase 3 Section 3.8 |
| [CI設定](../blob/main/00-guides/CI-SETUP-CHECKLIST.md) | CI/CD設定ガイド |

詳細は [ワークフロー実行ログ](${{ github.server_url }}/${{ github.repository }}/actions/runs/${{ github.run_id }}) を確認してください。`;

            await github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: body
            });

      - name: Comment PR (on warning)
        if: success() && steps.check.outputs.result == 'warning'
        uses: actions/github-script@v7
        with:
          script: |
            const warningCount = '${{ steps.check.outputs.warning_count }}';
            
            const body = `## ⚠️  SQLマイグレーションコメント品質ゲート: 警告

**検出された警告:**
- ⚠️  警告: ${warningCount} 件

マージは可能ですが、以下の項目を修正することでコード品質がさらに向上します。

### 📋 推奨対応項目

- [ ] ファイル冒頭コメントの全セクション記載
- [ ] 外部キー制約に \`COMMENT ON CONSTRAINT\`
- [ ] 全カラムに \`COMMENT ON COLUMN\`

### 📚 参考ドキュメント

- [SQL標準](../blob/main/01-coding-standards/sql-standards.md)
- [解決策ガイド](../blob/main/00-guides/SQL-MIGRATION-COMMENT-SOLUTION.md)

詳細は [ワークフロー実行ログ](${{ github.server_url }}/${{ github.repository }}/actions/runs/${{ github.run_id }}) を確認してください。`;

            await github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: body
            });
```

---

## ステップ5.3.2: ワークフロー動作確認

### ローカルテスト

```bash
# テスト用SQLファイルを作成
mkdir -p test/sql
cat > test/sql/V999__Test_migration.sql << 'EOF'
CREATE TABLE test_table (
    id UUID PRIMARY KEY
);
EOF

# チェックスクリプトを実行
# （ワークフローのrun部分を抽出して実行）
```

### PRでの動作確認

1. **SQLファイルを変更してPR作成**
   ```bash
   git checkout -b test/sql-comment-check
   # SQLファイルを編集
   git add src/main/resources/db/migration/
   git commit -m "test: SQL comment check"
   git push origin test/sql-comment-check
   ```

2. **GitHub Actionsで結果確認**
   - PRページの「Checks」タブを確認
   - 「SQL Migration Comment Quality Gate」の結果を確認

3. **期待される動作**:
   - ✅ エラーなし → チェック合格、マージ可能
   - ⚠️ 警告のみ → チェック合格、マージ可能（推奨事項あり）
   - ❌ エラーあり → チェック失敗、マージブロック

---

## ステップ5.3.3: チェックリスト

### CI設定完了確認

- [ ] `.github/workflows/sql-migration-comment-check.yml` が作成されている
- [ ] ワークフローがPRで自動実行されることを確認
- [ ] エラー時にPRへのコメントが投稿されることを確認
- [ ] 警告時にPRへのコメントが投稿されることを確認
- [ ] マージブロックが正しく機能することを確認

### ドキュメント整備確認

- [ ] `SQL-MIGRATION-COMMENT-SOLUTION.md` が作成されている
- [ ] `phase-3-implementation-guide.md` に Section 3.8 が追加されている
- [ ] `CI-SETUP-CHECKLIST.md` に Section 5.3 が追加されている
- [ ] チーム向けオンボーディング資料に追記されている

### チーム周知確認

- [ ] Slackでチーム全体に周知済み
- [ ] 既存PRに対する移行計画を策定
- [ ] レビュアー向けガイドを更新
- [ ] FAQ・トラブルシューティングを準備

---

## トラブルシューティング

### 問題1: ワークフローが実行されない

**原因**: パストリガーが一致していない

**解決策**:
```yaml
# プロジェクトのSQLファイルパスを確認
find . -name "*.sql" -path "*/migration/*"

# ワークフローのpathsを調整
on:
  pull_request:
    paths:
      - 'あなたのプロジェクトのパス/**/*.sql'
```

### 問題2: チェックが厳しすぎる

**解決策**: 警告レベルを調整

```bash
# エラー→警告に変更
# ワークフロー内で ((ERRORS++)) を ((WARNINGS++)) に変更
```

### 問題3: 既存SQLファイルが大量にエラー

**解決策**: 段階的導入

```yaml
# オプション1: 新規ファイルのみチェック
on:
  pull_request:
    paths:
      - 'src/main/resources/db/migration/V[5-9]*__*.sql'  # 新しいバージョンのみ

# オプション2: 警告のみでマージ可能に
# ワークフローで exit 1 を exit 0 に変更（暫定対応）
```

---

## まとめ

### Section 5.3の要点

1. ✅ **自動チェック**: PR作成時にSQLコメントを自動検証
2. ✅ **マージブロック**: エラー検出時はマージ不可
3. ✅ **詳細フィードバック**: PRに自動コメント投稿
4. ✅ **組織標準準拠**: `sql-standards.md`に準拠
5. ✅ **プロセス統合**: Phase 3実装ガイドと連携

### 期待される効果

- レビュー時のコメント不足指摘が **0件**
- SQLマイグレーションの品質向上
- ドキュメント化の自動化
- 組織標準の自動適用

---

**統合日**: 2025-11-07  
**対象ドキュメント**: `00-guides/CI-SETUP-CHECKLIST.md`  
**セクション**: 5.3（新規）

## 6. 既存プロジェクトへの適用ガイド

### 6.1 段階的導入アプローチ

上記のドキュメントコメント品質ゲートを既存プロジェクトに導入する際は、以下の段階的アプローチを推奨します：

#### **Phase 1: 新規コードのみ適用**（推奨）
- 新規作成ファイルにのみドキュメントコメント必須化を適用
- 既存コードは段階的にリファクタリング
- CI設定でディレクトリ単位の除外を設定

#### **Phase 2: 段階的な既存コード対応**
- 優先度の高いパブリックAPIから順次対応
- スプリント単位で対応範囲を拡大
- 除外ディレクトリを徐々に削減

#### **Phase 3: 全体適用**
- すべてのコードでドキュメントコメント必須化
- CI品質ゲートを完全に有効化

---

#### TypeScript プロジェクト

```bash
echo "=== TypeScript CI設定検証 ==="

# 1. 必須 npm スクリプトチェック
echo -e "\n1. NPM Scripts:"
npm run 2>&1 | grep -E "lint|format:check|type-check|test:coverage"

# 2. Jest カバレッジ閾値設定チェック
echo -e "\n2. Jest Coverage Threshold:"
grep -A 6 "coverageThreshold" jest.config.js

# 3. CI ワークフロー必須ステップチェック
echo -e "\n3. CI Workflow Steps:"
grep -E "npm run (lint|format:check|type-check|test:coverage)" .github/workflows/ci.yaml

# 4. 依存関係チェック
echo -e "\n4. Dev Dependencies:"
cat package.json | jq '.devDependencies | with_entries(select(.key | test("eslint|prettier|jest|typescript")))'
```

---

#### Python プロジェクト

```bash
echo "=== Python CI設定検証 ==="

# 1. 依存関係チェック
echo -e "\n1. Dev Dependencies:"
cat pyproject.toml | grep -E "pytest|pylint|black|mypy"

# 2. カバレッジ閾値設定チェック
echo -e "\n2. Coverage Threshold:"
# pyproject.toml の場合
grep "cov-fail-under" pyproject.toml
# .coveragerc の場合
grep "fail_under" .coveragerc 2>/dev/null

# 3. CI ワークフロー必須ステップチェック
echo -e "\n3. CI Workflow Steps:"
grep -E "pylint|black.*check|mypy|pytest.*cov" .github/workflows/ci.yaml

# 4. Pytest設定チェック
echo -e "\n4. Pytest Configuration:"
grep -A 3 "\[tool.pytest.ini_options\]" pyproject.toml
```

---

### Phase 3: ローカル実行確認（10分）

**すべての品質ゲートコマンドをローカルで実行し、動作確認**

#### Java

```bash
echo "=== Java 品質ゲート実行 ==="

# 1. Spotless Check
echo -e "\n1. Running Spotless Check..."
./gradlew spotlessCheck
if [ $? -eq 0 ]; then echo "✅ PASSED"; else echo "❌ FAILED"; fi

# 2. Checkstyle
echo -e "\n2. Running Checkstyle..."
./gradlew checkstyleMain checkstyleTest
if [ $? -eq 0 ]; then echo "✅ PASSED"; else echo "❌ FAILED"; fi

# 3. Build
echo -e "\n3. Running Build..."
./gradlew build -x test
if [ $? -eq 0 ]; then echo "✅ PASSED"; else echo "❌ FAILED"; fi

# 4. Tests
echo -e "\n4. Running Tests..."
./gradlew test
if [ $? -eq 0 ]; then echo "✅ PASSED"; else echo "❌ FAILED"; fi

# 5. Coverage Verification (最重要)
echo -e "\n5. Running Coverage Verification..."
./gradlew jacocoTestCoverageVerification
if [ $? -eq 0 ]; then 
    echo "✅ PASSED - Coverage >= 80%"
else 
    echo "❌ FAILED - Coverage < 80%"
fi
```

---

#### TypeScript

```bash
echo "=== TypeScript 品質ゲート実行 ==="

# 依存関係インストール
npm ci

# 1. Lint
echo -e "\n1. Running Lint..."
npm run lint
if [ $? -eq 0 ]; then echo "✅ PASSED"; else echo "❌ FAILED"; fi

# 2. Format Check
echo -e "\n2. Running Format Check..."
npm run format:check
if [ $? -eq 0 ]; then echo "✅ PASSED"; else echo "❌ FAILED"; fi

# 3. Type Check
echo -e "\n3. Running Type Check..."
npm run type-check
if [ $? -eq 0 ]; then echo "✅ PASSED"; else echo "❌ FAILED"; fi

# 4. Build
echo -e "\n4. Running Build..."
npm run build
if [ $? -eq 0 ]; then echo "✅ PASSED"; else echo "❌ FAILED"; fi

# 5. Test with Coverage (最重要)
echo -e "\n5. Running Tests with Coverage..."
npm run test:coverage
if [ $? -eq 0 ]; then 
    echo "✅ PASSED - Coverage >= 80%"
else 
    echo "❌ FAILED - Coverage < 80% または Test Failed"
fi
```

---

#### Python

```bash
echo "=== Python 品質ゲート実行 ==="

# 依存関係インストール
pip install -r requirements.txt
pip install pytest pytest-cov pylint black mypy

# 1. Pylint
echo -e "\n1. Running Pylint..."
pylint src/
if [ $? -eq 0 ]; then echo "✅ PASSED"; else echo "❌ FAILED"; fi

# 2. Black Check
echo -e "\n2. Running Black Check..."
black --check src/
if [ $? -eq 0 ]; then echo "✅ PASSED"; else echo "❌ FAILED"; fi

# 3. Mypy
echo -e "\n3. Running Mypy..."
mypy src/
if [ $? -eq 0 ]; then echo "✅ PASSED"; else echo "❌ FAILED"; fi

# 4. Pytest with Coverage (最重要)
echo -e "\n4. Running Tests with Coverage..."
pytest --cov --cov-fail-under=80
if [ $? -eq 0 ]; then 
    echo "✅ PASSED - Coverage >= 80%"
else 
    echo "❌ FAILED - Coverage < 80% または Test Failed"
fi
```

---

### Phase 4: CI実行結果確認（Phase 4レビュー時）

**PRのCIが実際に品質ゲートを実行したか確認**

#### GitHub CLI を使った確認

```bash
# PR のCI実行状態確認
gh pr checks

# 期待される出力:
# All checks passed
# ✓ CI Pipeline / build (pull_request)
```

#### GitHub Web UI での確認

1. PRページを開く
2. **Checks** タブをクリック
3. **CI Pipeline** ジョブを展開
4. 各ステップのログを確認:

**チェックリスト**:
- [ ] ✅ "Run Checkstyle" または "Run Lint" ステップが成功
- [ ] ✅ "Run Spotless Check" または "Check Code Formatting" ステップが成功
- [ ] ✅ "Run Tests" ステップが成功
- [ ] ✅ **"Verify Test Coverage" または "Run Tests with Coverage" ステップが成功** ⭐最重要

**ログ内で確認すべきキーワード**:

| 言語 | 確認キーワード |
|-----|--------------|
| **Java** | `BUILD SUCCESSFUL`, `jacocoTestCoverageVerification`, `Rule violated for bundle` (違反時) |
| **TypeScript** | `PASS`, `Coverage summary`, `Jest: "global" coverage threshold` (違反時) |
| **Python** | `passed`, `Coverage HTML written`, `FAIL Required test coverage of 80% not reached` (違反時) |

---

## ❌ 4. よくある設定ミスと対策

### ❌ ミス1: カバレッジ測定はあるが閾値強制がない

**症状**: カバレッジレポートは生成されるが、閾値未達でもCIがパスする

**Java の場合**:
```gradle
// ❌ 悪い例: レポート生成のみ
jacoco {
    toolVersion = "0.8.8"
}
// jacocoTestCoverageVerification が無い！
```

```yaml
# ❌ 悪い例: CI で jacocoTestReport のみ実行
- run: ./gradlew test jacocoTestReport
```

**✅ 修正方法**:
```gradle
// ✅ 良い例: 閾値検証を追加
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80
            }
        }
    }
}

check.dependsOn jacocoTestCoverageVerification
```

```yaml
# ✅ 良い例: CI で jacocoTestCoverageVerification を実行
- name: Verify Test Coverage
  run: ./gradlew jacocoTestCoverageVerification
```

---

### ❌ ミス2: ローカルでは動くがCIで実行されない

**症状**: `build.gradle` や `package.json` に設定はあるが、`.github/workflows/ci.yaml` で実行されていない

**Java の場合**:
```gradle
// build.gradle に設定はあるが...
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80
            }
        }
    }
}
```

```yaml
# ❌ CI ワークフローで実行されていない
jobs:
  build:
    steps:
      - run: ./gradlew build
      - run: ./gradlew test
      # jacocoTestCoverageVerification が無い！
```

**✅ 修正方法**:
```yaml
# ✅ CI で明示的に実行
jobs:
  build:
    steps:
      - run: ./gradlew build
      - run: ./gradlew test
      - run: ./gradlew jacocoTestCoverageVerification  # 追加
```

---

### ❌ ミス3: フォーマットチェックが警告のみで止まらない

**症状**: フォーマット違反があってもCIがパスしてしまう

```yaml
# ❌ 悪い例: 失敗しても続行
- run: ./gradlew spotlessCheck || true
- run: npm run format:check || echo "Format check failed but continuing"
```

**✅ 修正方法**:
```yaml
# ✅ 良い例: 失敗時にパイプライン停止
- name: Check Code Formatting
  run: ./gradlew spotlessCheck  # エラー時は exit 1

- name: Check Code Formatting
  run: npm run format:check  # エラー時は exit 1
```

---

### ❌ ミス4: checkタスクに依存関係が設定されていない

**症状**: `./gradlew build` 実行時に品質チェックがスキップされる

**Java の場合**:
```gradle
// ❌ 悪い例: 依存関係なし
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80
            }
        }
    }
}
// ここで終わり
```

この場合、`./gradlew build` を実行しても `jacocoTestCoverageVerification` は実行されない。

**✅ 修正方法**:
```gradle
// ✅ 良い例: check タスクに依存させる
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80
            }
        }
    }
}

// これを追加
check.dependsOn jacocoTestCoverageVerification
```

これにより、`./gradlew build` や `./gradlew check` 実行時に自動的にカバレッジ検証が実行される。

---

### ❌ ミス5: カバレッジ閾値が低すぎる

**症状**: 組織標準の80%ではなく、低い閾値が設定されている

```gradle
// ❌ 悪い例: 閾値が低すぎる
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.50  // 50% → 組織標準違反！
            }
        }
    }
}
```

```javascript
// ❌ 悪い例
coverageThreshold: {
  global: {
    lines: 60,  // 60% → 組織標準違反！
    functions: 60,
    branches: 60,
    statements: 60
  }
}
```

**✅ 修正方法**:
```gradle
// ✅ 良い例: 組織標準の80%
minimum = 0.80
```

```javascript
// ✅ 良い例
coverageThreshold: {
  global: {
    lines: 80,
    functions: 80,
    branches: 80,
    statements: 80
  }
}
```

**参照**: `/04-quality-standards/testing-standards.md` - カバレッジ基準

---

## 📊 5. チェックリスト完了基準

### Phase 1 (プロジェクト初期化) 完了時

**すべて ✅ になっていること**:

- [ ] 🔴 `.github/workflows/ci.yaml` が存在する
- [ ] 🔴 言語別設定ファイル（`build.gradle` / `package.json` / `pyproject.toml`）に品質ゲート設定がある
- [ ] 🔴 カバレッジ閾値（80%）が設定されている
- [ ] 🔴 ローカルですべての品質ゲートコマンドが実行できる
- [ ] 🔴 ローカル実行でエラーが無い（または予想通りのエラー）

---

### Phase 3 (実装) 完了時

**すべて ✅ になっていること**:

- [ ] 🔴 CI設定ファイルが最新の実装に対応している
- [ ] 🔴 新規追加した依存関係がCI環境で利用可能
- [ ] 🟡 実装中に品質ゲートコマンドを定期的に実行した

---

### Phase 4 (レビュー) 開始時

**すべて ✅ になっていること**:

- [ ] 🔴 **このチェックリストの全項目を検証済み**
- [ ] 🔴 PRのCIがすべてパスしている
- [ ] 🔴 CIログで以下が実行されたことを確認:
  - [ ] Lint / Checkstyle
  - [ ] Format check / Spotless check
  - [ ] Build / Compile
  - [ ] Tests
  - [ ] **Coverage verification (80%閾値チェック)**
- [ ] 🔴 カバレッジレポートで実際のカバレッジ率が80%以上

**これらがすべて ✅ でない場合、レビュー不合格**

---

## 🔧 6. トラブルシューティング

### Q1: カバレッジ閾値設定が見つからない

**対応手順**:
1. このチェックリストの「2. 言語別必須設定マトリクス」を参照
2. 該当言語のテンプレートをコピー
3. 組織標準ドキュメントを参照:
   - `/03-development-process/ci-cd-pipeline.md` - セクション3.3
   - `/04-quality-standards/testing-standards.md` - カバレッジ基準

---

### Q2: CIワークフローファイルが存在しない

**対応手順**:
1. `/08-templates/ci-templates/` (このドキュメント整備後に作成予定) を参照
2. または組織標準CI/CD設定を参照:
   - `/03-development-process/ci-cd-pipeline.md`
3. 言語別サンプルワークフローをコピーして作成

---

### Q3: ローカルで品質ゲートコマンドが失敗する

**対応手順**:

**フォーマット違反の場合**:
```bash
# Java
./gradlew spotlessApply

# TypeScript
npm run format

# Python
black src/
```

**Lint違反の場合**:
```bash
# 自動修正可能なものを修正
npm run lint:fix  # TypeScript
pylint src/ | grep "^C:"  # Python: Convention違反を確認
```

**カバレッジ不足の場合**:
1. カバレッジレポートを確認（`build/reports/jacoco/test/html/index.html` など）
2. テストが不足しているクラス/関数を特定
3. ユニットテストを追加
4. 参照: `/04-quality-standards/unit-testing.md`

---

### Q4: CIが実行されるがログに品質ゲートが見当たらない

**対応手順**:
1. `.github/workflows/ci.yaml` を開く
2. 必須ステップが存在するか確認（上記「言語別必須設定」参照）
3. 不足しているステップを追加
4. コミット・プッシュして再度CI実行

---

## 📚 7. 参照ドキュメント

### 組織標準ドキュメント

- 🔴 **必須**: `/03-development-process/ci-cd-pipeline.md` - CI/CD標準全般
- 🔴 **必須**: `/04-quality-standards/code-quality-standards.md` - コード品質基準
- 🔴 **必須**: `/04-quality-standards/testing-standards.md` - テスト・カバレッジ基準
- 🟡 **推奨**: `/00-guides/AI-MASTER-WORKFLOW-GUIDE.md` - 全体ワークフロー
- 🟡 **推奨**: `/00-guides/phase-guides/phase-3-implementation-guide.md` - 実装フェーズ詳細
- 🟡 **推奨**: `/00-guides/phase-guides/phase-4-review-qa-guide.md` - レビューフェーズ詳細

### 言語別コーディング標準

- `/01-coding-standards/java-standards.md`
- `/01-coding-standards/typescript-javascript-standards.md`
- `/01-coding-standards/python-standards.md`

### テンプレート（このドキュメント整備後に作成予定）

- `/08-templates/ci-templates/java-spring-boot/`
- `/08-templates/ci-templates/typescript-node/`
- `/08-templates/ci-templates/python/`

---

## 📝 8. チェックリスト実行記録テンプレート

プロジェクトで実際にこのチェックリストを実行したら、以下を記録してください。

```markdown
## CI設定チェックリスト実行記録

**プロジェクト名**: [プロジェクト名]
**実施日**: [YYYY-MM-DD]
**実施者**: [AI Agent名 / 開発者名]
**言語**: [Java / TypeScript / Python]

### 実行結果サマリー

- [ ] ✅ すべてのチェック項目がパス
- [ ] ⚠️ 一部警告あり（記録済み）
- [ ] ❌ 不合格項目あり（修正必要）

### 詳細

#### 1. コード品質チェック
- Linting: [✅ / ❌]
- Format check: [✅ / ❌]
- Type check: [✅ / ❌]

#### 2. ビルド検証
- Build: [✅ / ❌]
- Artifact generation: [✅ / ❌]

#### 3. テスト実行と品質ゲート
- Unit tests: [✅ / ❌]
- Coverage measurement: [✅ / ❌]
- **Coverage threshold (80%)**: [✅ / ❌] ← 最重要

#### 4. ローカル実行確認
- すべての品質ゲートコマンドをローカルで実行: [✅ / ❌]
- エラー無し: [✅ / ❌]

#### 5. CI実行確認（Phase 4レビュー時）
- CI実行でパス: [✅ / ❌]
- すべての品質ゲートがログに記録: [✅ / ❌]

### 問題点と対応

[問題があった場合、ここに記録]

例:
- ❌ jacocoTestCoverageVerification が build.gradle に無かった
  → 修正: セクション2.1のテンプレートから追加
  → 再実行: ✅ パス

### 参照したドキュメント

- `/00-guides/CI-SETUP-CHECKLIST.md` (このドキュメント)
- [その他参照したドキュメント]

### 承認

- 実施者: [署名]
- レビュー担当者（Phase 4の場合）: [署名]
- 日付: [YYYY-MM-DD]
```

---

## 🎓 9. AIエージェント向けガイドライン

### Devin / Cursor が自動実行すべきこと

#### Phase 1 完了時

```python
def auto_verify_ci_setup():
    """
    CI設定の自動検証（Phase 1完了時）
    """
    print("🔍 CI設定を自動検証中...")
    
    issues = []
    
    # 1. ワークフローファイル存在確認
    if not os.path.exists('.github/workflows/ci.yaml'):
        issues.append({
            'severity': 'ERROR',
            'message': 'CI workflow file not found',
            'action': 'Create .github/workflows/ci.yaml from template'
        })
    
    # 2. 言語検出と設定検証
    if os.path.exists('build.gradle'):  # Java
        issues.extend(verify_java_ci_config())
    elif os.path.exists('package.json'):  # TypeScript/Node
        issues.extend(verify_typescript_ci_config())
    elif os.path.exists('pyproject.toml'):  # Python
        issues.extend(verify_python_ci_config())
    
    # 3. レポート
    if issues:
        print("❌ CI設定に問題があります:\n")
        for issue in issues:
            severity_icon = "🔴" if issue['severity'] == 'ERROR' else "🟡"
            print(f"{severity_icon} [{issue['severity']}] {issue['message']}")
            print(f"   対応: {issue['action']}\n")
        
        print("📋 修正手順:")
        print("   1. /00-guides/CI-SETUP-CHECKLIST.md を開く")
        print("   2. 該当する言語セクションを参照")
        print("   3. 不足している設定を追加")
        print("   4. ローカルで品質ゲートコマンドを実行")
        print("   5. このスクリプトを再実行\n")
        
        return False
    else:
        print("✅ CI設定は組織標準に準拠しています")
        return True


def verify_java_ci_config():
    """Java プロジェクトのCI設定検証"""
    issues = []
    
    gradle_content = read_file('build.gradle')
    
    # JaCoCo plugin チェック
    if "id 'jacoco'" not in gradle_content:
        issues.append({
            'severity': 'ERROR',
            'message': 'JaCoCo plugin not configured in build.gradle',
            'action': 'Add: id \'jacoco\' to plugins block'
        })
    
    # Spotless plugin チェック
    if 'spotless' not in gradle_content:
        issues.append({
            'severity': 'ERROR',
            'message': 'Spotless plugin not configured in build.gradle',
            'action': 'Add: id \'com.diffplug.spotless\' to plugins block'
        })
    
    # カバレッジ閾値チェック
    if 'jacocoTestCoverageVerification' not in gradle_content:
        issues.append({
            'severity': 'ERROR',
            'message': 'Coverage threshold not configured in build.gradle',
            'action': 'Add jacocoTestCoverageVerification block with minimum = 0.80'
        })
    elif 'minimum = 0.80' not in gradle_content and 'minimum = 0.8' not in gradle_content:
        issues.append({
            'severity': 'WARNING',
            'message': 'Coverage threshold may not be 80%',
            'action': 'Verify minimum = 0.80 in jacocoTestCoverageVerification'
        })
    
    # CI ワークフローチェック
    if os.path.exists('.github/workflows/ci.yaml'):
        ci_yaml = read_file('.github/workflows/ci.yaml')
        
        if 'spotlessCheck' not in ci_yaml:
            issues.append({
                'severity': 'ERROR',
                'message': 'spotlessCheck not executed in CI workflow',
                'action': 'Add step: ./gradlew spotlessCheck'
            })
        
        if 'jacocoTestCoverageVerification' not in ci_yaml:
            issues.append({
                'severity': 'ERROR',
                'message': 'jacocoTestCoverageVerification not executed in CI workflow',
                'action': 'Add step: ./gradlew jacocoTestCoverageVerification'
            })
    
    return issues


def verify_typescript_ci_config():
    """TypeScript プロジェクトのCI設定検証"""
    issues = []
    
    # package.json チェック
    with open('package.json') as f:
        package_json = json.load(f)
    
    required_scripts = ['lint', 'format:check', 'type-check', 'test:coverage']
    for script in required_scripts:
        if script not in package_json.get('scripts', {}):
            issues.append({
                'severity': 'ERROR',
                'message': f'Missing npm script: {script}',
                'action': f'Add "{script}" script to package.json'
            })
    
    # jest.config.js チェック
    if os.path.exists('jest.config.js'):
        jest_config = read_file('jest.config.js')
        
        if 'coverageThreshold' not in jest_config:
            issues.append({
                'severity': 'ERROR',
                'message': 'Coverage threshold not configured in jest.config.js',
                'action': 'Add coverageThreshold with global: { lines: 80, ... }'
            })
    else:
        issues.append({
            'severity': 'WARNING',
            'message': 'jest.config.js not found',
            'action': 'Create jest.config.js with coverage threshold'
        })
    
    return issues


def verify_python_ci_config():
    """Python プロジェクトのCI設定検証"""
    issues = []
    
    # pyproject.toml チェック
    if os.path.exists('pyproject.toml'):
        pyproject_content = read_file('pyproject.toml')
        
        if 'pytest-cov' not in pyproject_content:
            issues.append({
                'severity': 'ERROR',
                'message': 'pytest-cov not in dependencies',
                'action': 'Add pytest-cov to dev dependencies'
            })
        
        if 'cov-fail-under' not in pyproject_content:
            # .coveragerc をチェック
            if not os.path.exists('.coveragerc'):
                issues.append({
                    'severity': 'ERROR',
                    'message': 'Coverage threshold not configured',
                    'action': 'Add cov-fail-under=80 to pyproject.toml or create .coveragerc'
                })
    
    # .coveragerc チェック
    if os.path.exists('.coveragerc'):
        coveragerc_content = read_file('.coveragerc')
        
        if 'fail_under = 80' not in coveragerc_content:
            issues.append({
                'severity': 'WARNING',
                'message': 'Coverage threshold may not be 80%',
                'action': 'Verify fail_under = 80 in .coveragerc'
            })
    
    return issues
```

#### Phase 4 レビュー時

```python
def auto_verify_ci_execution():
    """
    CI実行結果の自動検証（Phase 4レビュー時）
    """
    print("🔍 CI実行結果を検証中...")
    
    # 最新のCI実行ログを取得
    ci_log = get_latest_ci_log()  # GitHub API等から取得
    
    # 必須キーワードの存在確認
    required_checks = {
        'format': {
            'keywords': ['spotlessCheck', 'prettier', 'black --check'],
            'description': 'Code formatting check'
        },
        'lint': {
            'keywords': ['checkstyle', 'eslint', 'pylint'],
            'description': 'Linting'
        },
        'coverage': {
            'keywords': [
                'jacocoTestCoverageVerification',
                'coverageThreshold',
                'fail_under',
                'Coverage check'
            ],
            'description': 'Coverage threshold verification (80%)'
        },
        'build': {
            'keywords': ['BUILD SUCCESSFUL', 'compiled successfully', 'Build'],
            'description': 'Build/Compile'
        },
        'test': {
            'keywords': ['tests passed', 'All tests', 'OK'],
            'description': 'Tests execution'
        }
    }
    
    issues = []
    for check_name, check_info in required_checks.items():
        if not any(kw in ci_log for kw in check_info['keywords']):
            issues.append({
                'check': check_name,
                'description': check_info['description'],
                'message': f"{check_info['description']} not found in CI log"
            })
    
    if issues:
        print("❌ CI実行で品質ゲートが実行されていません:\n")
        for issue in issues:
            print(f"🔴 [{issue['check']}] {issue['message']}")
        
        print("\n📋 対応手順:")
        print("   1. .github/workflows/ci.yaml を確認")
        print("   2. 不足しているステップを追加")
        print("   3. /00-guides/CI-SETUP-CHECKLIST.md の該当セクションを参照")
        print("   4. 再度コミット・プッシュしてCI実行\n")
        
        return False
    else:
        print("✅ すべての品質ゲートがCI上で実行されました")
        
        # カバレッジ率の確認
        coverage_match = re.search(r'(\d+)%\s+coverage', ci_log)
        if coverage_match:
            coverage_pct = int(coverage_match.group(1))
            if coverage_pct >= 80:
                print(f"✅ カバレッジ {coverage_pct}% (>= 80%)")
            else:
                print(f"❌ カバレッジ {coverage_pct}% (< 80%)")
                return False
        
        return True
```

---

## 変更履歴

| バージョン | 日付 | 変更内容 | 変更者 |
|---------|------|---------|-------|
| 1.0.0 | 2025-11-07 | 初版作成 | DevOps Team |

---

**ドキュメント管理者**: DevOps Team / Quality Assurance  
**レビュー頻度**: 四半期ごと  
**次回レビュー**: 2026-02-07
