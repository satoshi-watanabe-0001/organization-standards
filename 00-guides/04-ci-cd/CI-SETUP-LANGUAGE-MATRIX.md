# CI Setup - 言語別必須設定マトリクス

**バージョン**: v2.1.0  
**最終更新**: 2025-11-13  
**対象Phase**: Phase 1 (プロジェクト初期化)

---

## 📌 このドキュメントについて

各プログラミング言語・フレームワークでのCI設定の詳細要件を記載しています。

### 使用タイミング

- **Phase 1**: プロジェクト初期化時、言語を決定した直後
- **Phase 3**: 実装中、CI設定を確認・調整する際
- **Phase 4**: レビュー時、CI設定の妥当性を検証する際

### 関連ドキュメント

- [CI-SETUP-CHECKLIST.md](./CI-SETUP-CHECKLIST.md) - CI設定の共通チェックリスト
- [CI-SETUP-QUICK-CHECKLIST.md](./CI-SETUP-QUICK-CHECKLIST.md) - クイックチェックリスト

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


---

## 🔍 言語別ローカル実行確認手順

各言語での品質ゲートコマンドのローカル実行手順です。

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


---

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


---

## 変更履歴

- **2025-11-13**: 言語別マトリクスを独立ドキュメント化
- **2025-11-07**: (以前の履歴は CI-SETUP-CHECKLIST.md を参照)
