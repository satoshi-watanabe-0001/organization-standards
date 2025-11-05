# リンター・フォーマッター設定標準

**最終更新**: 2025-10-27  
**バージョン**: 1.0.0  
**対象**: 開発チーム全体

---

## 📖 概要

このドキュメントは、コード品質を維持するためのリンター（静的解析ツール）とフォーマッター（コード整形ツール）の設定標準を定義します。自動化されたコード品質チェックにより、レビュー負荷を軽減し、一貫性のあるコードベースを維持します。

---

## 🎯 リンター・フォーマッターの目的

### リンター（Linter）
- コードの潜在的なバグを検出
- ベストプラクティスへの準拠を確認
- コード品質メトリクスの測定

### フォーマッター（Formatter）
- コードスタイルの統一
- 自動整形による手作業の削減
- 可読性の向上

---

## 📦 言語別ツール

| 言語 | リンター | フォーマッター |
|-----|---------|---------------|
| TypeScript/JavaScript | **ESLint** | **Prettier** |
| Python | **Pylint**, **Flake8** | **Black**, **isort** |
| Java | **CheckStyle**, **PMD** | **Google Java Format** |
| SQL | **SQLFluff** | **SQL Formatter** |
| CSS/SCSS | **Stylelint** | **Prettier** |
| YAML | **yamllint** | **Prettier** |
| Markdown | **markdownlint** | **Prettier** |

---

## 🔧 TypeScript/JavaScript 設定

### ESLint 設定

#### インストール

```bash
npm install --save-dev eslint \
  @typescript-eslint/parser \
  @typescript-eslint/eslint-plugin \
  eslint-config-prettier \
  eslint-plugin-prettier
```

#### .eslintrc.js

```javascript
module.exports = {
  root: true,
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 2022,
    sourceType: 'module',
    project: './tsconfig.json',
  },
  env: {
    node: true,
    browser: true,
    es2022: true,
  },
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:@typescript-eslint/recommended-requiring-type-checking',
    'prettier',
  ],
  plugins: ['@typescript-eslint', 'prettier'],
  rules: {
    // エラーレベル
    'prettier/prettier': 'error',
    'no-console': ['warn', { allow: ['warn', 'error'] }],
    'no-debugger': 'error',
    'no-unused-vars': 'off',
    '@typescript-eslint/no-unused-vars': [
      'error',
      { argsIgnorePattern: '^_', varsIgnorePattern: '^_' },
    ],
    
    // 関数・クラスのサイズ制限（保守性メトリクス）
    'max-lines-per-function': ['warn', { max: 50, skipBlankLines: true, skipComments: true }],
    'max-lines': ['warn', { max: 400, skipBlankLines: true, skipComments: true }],
    'complexity': ['error', 15],
    'max-depth': ['error', 4],
    'max-params': ['warn', 5],
    
    // TypeScript固有
    '@typescript-eslint/explicit-function-return-type': [
      'warn',
      { allowExpressions: true },
    ],
    '@typescript-eslint/no-explicit-any': 'error',
    '@typescript-eslint/no-non-null-assertion': 'warn',
    '@typescript-eslint/prefer-nullish-coalescing': 'warn',
    '@typescript-eslint/prefer-optional-chain': 'warn',
    
    // ベストプラクティス
    'eqeqeq': ['error', 'always'],
    'no-var': 'error',
    'prefer-const': 'error',
    'prefer-arrow-callback': 'warn',
  },
  ignorePatterns: [
    'node_modules/',
    'dist/',
    'build/',
    '*.config.js',
    'coverage/',
  ],
};
```

### Prettier 設定

#### インストール

```bash
npm install --save-dev prettier
```

#### .prettierrc.json

```json
{
  "semi": true,
  "trailingComma": "es5",
  "singleQuote": true,
  "printWidth": 100,
  "tabWidth": 2,
  "useTabs": false,
  "arrowParens": "always",
  "endOfLine": "lf",
  "bracketSpacing": true,
  "jsxSingleQuote": false,
  "quoteProps": "as-needed"
}
```

#### .prettierignore

```
node_modules/
dist/
build/
coverage/
*.min.js
*.min.css
package-lock.json
yarn.lock
```

### package.json スクリプト

```json
{
  "scripts": {
    "lint": "eslint . --ext .ts,.tsx,.js,.jsx",
    "lint:fix": "eslint . --ext .ts,.tsx,.js,.jsx --fix",
    "format": "prettier --write \"src/**/*.{ts,tsx,js,jsx,json,css,md}\"",
    "format:check": "prettier --check \"src/**/*.{ts,tsx,js,jsx,json,css,md}\"",
    "check": "npm run lint && npm run format:check"
  }
}
```

---

## 🐍 Python 設定

### Pylint 設定

#### インストール

```bash
pip install pylint black flake8 isort mypy
```

#### .pylintrc

```ini
[MASTER]
ignore=CVS,.git,__pycache__,venv,.venv
persistent=yes
jobs=4

[MESSAGES CONTROL]
disable=
    C0111,  # missing-docstring
    C0103,  # invalid-name (Black handles this)
    W0511,  # fixme
    R0903,  # too-few-public-methods
    R0913,  # too-many-arguments (上限は別途定義)

[REPORTS]
output-format=colorized
reports=no

[FORMAT]
max-line-length=120
indent-string='    '

[DESIGN]
# 保守性メトリクス
max-args=5
max-locals=15
max-returns=6
max-branches=12
max-statements=50
max-attributes=10
min-public-methods=1
max-public-methods=20

[SIMILARITIES]
min-similarity-lines=4
ignore-comments=yes
ignore-docstrings=yes
ignore-imports=yes

[BASIC]
good-names=i,j,k,ex,Run,_,id,db
```

### Black 設定

#### pyproject.toml

```toml
[tool.black]
line-length = 120
target-version = ['py311']
include = '\.pyi?$'
extend-exclude = '''
/(
  # デフォルト除外パターン
  \.eggs
  | \.git
  | \.hg
  | \.mypy_cache
  | \.tox
  | \.venv
  | _build
  | buck-out
  | build
  | dist
)/
'''
```

### isort 設定

#### pyproject.toml

```toml
[tool.isort]
profile = "black"
line_length = 120
multi_line_output = 3
include_trailing_comma = true
force_grid_wrap = 0
use_parentheses = true
ensure_newline_before_comments = true
```

### Flake8 設定

#### .flake8

```ini
[flake8]
max-line-length = 120
extend-ignore = E203, E266, E501, W503
exclude =
    .git,
    __pycache__,
    .venv,
    venv,
    build,
    dist,
    *.egg-info
per-file-ignores =
    __init__.py:F401
max-complexity = 15
```

### Makefile または スクリプト

```makefile
.PHONY: lint format check

lint:
	pylint src/
	flake8 src/
	mypy src/

format:
	black src/
	isort src/

check: lint format
	@echo "All checks passed!"
```

---

## ☕ Java 設定

### CheckStyle 設定

#### build.gradle

```groovy
plugins {
    id 'checkstyle'
}

checkstyle {
    toolVersion = '10.12.0'
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
}

tasks.withType(Checkstyle) {
    reports {
        xml.required = false
        html.required = true
    }
}
```

#### config/checkstyle/checkstyle.xml

```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
    "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
    "https://checkstyle.org/dtds/configuration_1_3.dtd">

<module name="Checker">
    <property name="charset" value="UTF-8"/>
    <property name="severity" value="error"/>
    <property name="fileExtensions" value="java"/>

    <!-- サイズ制限（保守性メトリクス） -->
    <module name="FileLength">
        <property name="max" value="1000"/>
    </module>

    <module name="TreeWalker">
        <!-- 命名規則 -->
        <module name="TypeName"/>
        <module name="MethodName"/>
        <module name="ConstantName"/>
        <module name="LocalVariableName"/>
        <module name="PackageName"/>

        <!-- サイズ制限 -->
        <module name="MethodLength">
            <property name="max" value="100"/>
            <property name="tokens" value="METHOD_DEF"/>
        </module>
        <module name="ParameterNumber">
            <property name="max" value="5"/>
        </module>

        <!-- 複雑度 -->
        <module name="CyclomaticComplexity">
            <property name="max" value="15"/>
        </module>
        <module name="JavaNCSS">
            <property name="methodMaximum" value="50"/>
        </module>

        <!-- Javadoc -->
        <module name="JavadocMethod">
            <property name="scope" value="public"/>
        </module>
        <module name="JavadocType"/>

        <!-- コーディングスタイル -->
        <module name="EmptyBlock"/>
        <module name="NeedBraces"/>
        <module name="LeftCurly"/>
        <module name="RightCurly"/>
    </module>
</module>
```

### Google Java Format

#### Maven設定（pom.xml）

```xml
<plugin>
    <groupId>com.spotify.fmt</groupId>
    <artifactId>fmt-maven-plugin</artifactId>
    <version>2.21.1</version>
    <executions>
        <execution>
            <goals>
                <goal>format</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

## 💅 CSS/SCSS 設定

### Stylelint 設定

#### インストール

```bash
npm install --save-dev stylelint \
  stylelint-config-standard-scss \
  stylelint-config-prettier-scss
```

#### .stylelintrc.json

```json
{
  "extends": [
    "stylelint-config-standard-scss",
    "stylelint-config-prettier-scss"
  ],
  "rules": {
    "max-nesting-depth": 4,
    "selector-max-compound-selectors": 4,
    "selector-max-id": 0,
    "selector-no-qualifying-type": true,
    "declaration-no-important": true,
    "color-named": "never",
    "color-hex-length": "short",
    "scss/at-rule-no-unknown": true,
    "scss/dollar-variable-pattern": "^[a-z][a-zA-Z0-9]*$"
  },
  "ignoreFiles": [
    "node_modules/**",
    "dist/**",
    "build/**"
  ]
}
```

---

## 🗄️ SQL 設定

### SQLFluff 設定

#### インストール

```bash
pip install sqlfluff
```

#### .sqlfluff

```ini
[sqlfluff]
dialect = postgres
templater = jinja
sql_file_exts = .sql
exclude_rules = L034, L036
max_line_length = 120

[sqlfluff:rules]
tab_space_size = 2
indent_unit = space

[sqlfluff:rules:L010]
# Keywords should be lowercase
capitalisation_policy = lower

[sqlfluff:rules:L030]
# Column names should be lowercase
capitalisation_policy = lower

[sqlfluff:rules:L014]
# Unquoted identifiers should be lowercase
extended_capitalisation_policy = lower
```

---

## 🔄 CI/CD統合

### GitHub Actions（.github/workflows/lint.yml）

```yaml
name: Lint and Format Check

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

jobs:
  typescript-lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - run: npm ci
      - run: npm run lint
      - run: npm run format:check

  python-lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-python@v4
        with:
          python-version: '3.11'
      - run: pip install pylint black flake8 isort mypy
      - run: black --check src/
      - run: pylint src/
      - run: flake8 src/
      - run: mypy src/

  java-checkstyle:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: '17'
      - run: ./gradlew checkstyleMain
```

### pre-commit フック設定

#### .pre-commit-config.yaml

```yaml
repos:
  - repo: https://github.com/pre-commit/pre-commit-hooks
    rev: v4.5.0
    hooks:
      - id: trailing-whitespace
      - id: end-of-file-fixer
      - id: check-yaml
      - id: check-json
      - id: check-added-large-files

  - repo: https://github.com/psf/black
    rev: 23.12.0
    hooks:
      - id: black

  - repo: https://github.com/pycqa/isort
    rev: 5.13.0
    hooks:
      - id: isort

  - repo: https://github.com/pycqa/flake8
    rev: 6.1.0
    hooks:
      - id: flake8

  - repo: https://github.com/pre-commit/mirrors-eslint
    rev: v8.56.0
    hooks:
      - id: eslint
        args: [--fix]
        types: [javascript, jsx, ts, tsx]

  - repo: https://github.com/pre-commit/mirrors-prettier
    rev: v3.1.0
    hooks:
      - id: prettier
        types_or: [javascript, jsx, ts, tsx, css, scss, json, yaml, markdown]
```

#### インストール

```bash
pip install pre-commit
pre-commit install
```

---

## 📊 品質メトリクスダッシュボード

### SonarQube連携

```yaml
# sonar-project.properties
sonar.projectKey=your-project
sonar.sources=src
sonar.exclusions=**/node_modules/**,**/dist/**,**/build/**
sonar.javascript.lcov.reportPaths=coverage/lcov.info
sonar.python.coverage.reportPaths=coverage.xml
```

---

## 🔍 トラブルシューティング

### ESLintとPrettierの競合

**問題**: ESLintとPrettierのルールが競合する

**解決策**:
```bash
npm install --save-dev eslint-config-prettier
```

```javascript
// .eslintrc.js
module.exports = {
  extends: [
    'eslint:recommended',
    'prettier', // 必ず最後に配置
  ],
};
```

### Blackとisortの競合

**問題**: importの順序でBlackとisortが競合

**解決策**:
```toml
[tool.isort]
profile = "black"  # Blackと互換性のあるプロファイルを使用
```

---

## 📚 参考リソース

- **ESLint**: https://eslint.org/
- **Prettier**: https://prettier.io/
- **Pylint**: https://pylint.pycqa.org/
- **Black**: https://black.readthedocs.io/
- **CheckStyle**: https://checkstyle.org/
- **Stylelint**: https://stylelint.io/
- **SQLFluff**: https://www.sqlfluff.com/

---

## 🔄 更新履歴

### v1.0.0 (2025-10-27)
- 初版作成
- TypeScript/JavaScript、Python、Java、CSS/SCSS、SQLのリンター・フォーマッター設定を定義
- CI/CD統合例を追加
- pre-commitフック設定を追加
