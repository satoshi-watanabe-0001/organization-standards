# IDE設定標準

**最終更新**: 2025-10-27  
**バージョン**: 1.0.0  
**対象**: 開発チーム全体

---

## 📖 概要

このドキュメントは、組織内で推奨されるIDE（統合開発環境）の設定標準を定義します。統一された開発環境により、チーム間の協業効率が向上し、コード品質の一貫性が保たれます。

---

## 🎯 推奨IDE

### 1. Visual Studio Code（推奨）

**対象言語**: TypeScript、JavaScript、Python、Markdown、JSON、YAML  
**理由**: 
- 軽量で高速
- 豊富な拡張機能
- GitとのシームレスなAI統合
- AI開発ツール（Cursor、GitHub Copilot）との親和性

**最小バージョン**: v1.85.0以上

### 2. IntelliJ IDEA / WebStorm

**対象言語**: Java、Kotlin、TypeScript、JavaScript  
**理由**:
- 強力なリファクタリング機能
- エンタープライズ向け機能
- 優れたデバッグ機能

**最小バージョン**: 2023.3以上

### 3. PyCharm

**対象言語**: Python  
**理由**:
- Python開発に特化
- 科学技術計算ライブラリのサポート
- データベース統合

**最小バージョン**: 2023.3以上

---

## ⚙️ Visual Studio Code 設定

### 基本設定（settings.json）

```json
{
  // エディタ設定
  "editor.fontSize": 14,
  "editor.tabSize": 2,
  "editor.insertSpaces": true,
  "editor.detectIndentation": false,
  "editor.formatOnSave": true,
  "editor.formatOnPaste": true,
  "editor.codeActionsOnSave": {
    "source.fixAll": true,
    "source.organizeImports": true
  },
  "editor.rulers": [80, 120],
  "editor.renderWhitespace": "boundary",
  "editor.bracketPairColorization.enabled": true,
  "editor.guides.bracketPairs": true,
  
  // ファイル設定
  "files.encoding": "utf8",
  "files.eol": "\n",
  "files.trimTrailingWhitespace": true,
  "files.insertFinalNewline": true,
  "files.exclude": {
    "**/.git": true,
    "**/.DS_Store": true,
    "**/*.pyc": true,
    "**/__pycache__": true,
    "**/node_modules": true,
    "**/dist": true,
    "**/build": true
  },
  
  // TypeScript/JavaScript設定
  "typescript.updateImportsOnFileMove.enabled": "always",
  "javascript.updateImportsOnFileMove.enabled": "always",
  "typescript.preferences.importModuleSpecifier": "relative",
  "javascript.preferences.importModuleSpecifier": "relative",
  
  // Python設定
  "python.linting.enabled": true,
  "python.linting.pylintEnabled": true,
  "python.linting.flake8Enabled": true,
  "python.formatting.provider": "black",
  "python.analysis.typeCheckingMode": "basic",
  
  // Git設定
  "git.autofetch": true,
  "git.confirmSync": false,
  "git.enableSmartCommit": true,
  
  // 検索設定
  "search.exclude": {
    "**/node_modules": true,
    "**/bower_components": true,
    "**/*.code-search": true,
    "**/dist": true,
    "**/build": true,
    "**/.venv": true,
    "**/__pycache__": true
  },
  
  // ターミナル設定
  "terminal.integrated.fontSize": 13,
  "terminal.integrated.fontFamily": "Menlo, Monaco, 'Courier New', monospace",
  
  // セキュリティ
  "security.workspace.trust.enabled": true
}
```

### 言語別設定

#### TypeScript/JavaScript

```json
{
  "[typescript]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode",
    "editor.tabSize": 2
  },
  "[javascript]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode",
    "editor.tabSize": 2
  },
  "[typescriptreact]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode",
    "editor.tabSize": 2
  },
  "[javascriptreact]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode",
    "editor.tabSize": 2
  }
}
```

#### Python

```json
{
  "[python]": {
    "editor.defaultFormatter": "ms-python.black-formatter",
    "editor.tabSize": 4,
    "editor.formatOnSave": true
  }
}
```

#### Java

```json
{
  "[java]": {
    "editor.tabSize": 4,
    "editor.insertSpaces": true
  }
}
```

#### SQL

```json
{
  "[sql]": {
    "editor.tabSize": 2,
    "editor.formatOnSave": true
  }
}
```

#### Markdown

```json
{
  "[markdown]": {
    "editor.wordWrap": "on",
    "editor.quickSuggestions": false
  }
}
```

---

## 📦 必須拡張機能

### 全言語共通

| 拡張機能 | ID | 用途 |
|---------|-----|-----|
| **Prettier** | `esbenp.prettier-vscode` | コードフォーマッター |
| **ESLint** | `dbaeumer.vscode-eslint` | JavaScript/TypeScriptリンター |
| **GitLens** | `eamodio.gitlens` | Git拡張機能 |
| **EditorConfig** | `editorconfig.editorconfig` | エディタ設定の統一 |
| **Error Lens** | `usernamehw.errorlens` | エラーのインライン表示 |
| **Todo Tree** | `gruntfuggly.todo-tree` | TODOコメント管理 |
| **Path Intellisense** | `christian-kohler.path-intellisense` | パス補完 |

### TypeScript/JavaScript開発

| 拡張機能 | ID | 用途 |
|---------|-----|-----|
| **TypeScript Hero** | `rbbit.typescript-hero` | import自動整理 |
| **JavaScript Debugger** | ビルトイン | デバッグ |
| **npm Intellisense** | `christian-kohler.npm-intellisense` | npm補完 |

### Python開発

| 拡張機能 | ID | 用途 |
|---------|-----|-----|
| **Python** | `ms-python.python` | Python言語サポート |
| **Pylance** | `ms-python.vscode-pylance` | 型チェック・補完 |
| **Black Formatter** | `ms-python.black-formatter` | Pythonフォーマッター |
| **isort** | `ms-python.isort` | import自動整理 |

### Java開発

| 拡張機能 | ID | 用途 |
|---------|-----|-----|
| **Java Extension Pack** | `vscjava.vscode-java-pack` | Java開発パック |
| **Checkstyle** | `shengchen.vscode-checkstyle` | Java静的解析 |

### データベース

| 拡張機能 | ID | 用途 |
|---------|-----|-----|
| **SQLTools** | `mtxr.sqltools` | SQL管理 |
| **PostgreSQL** | `ckolkman.vscode-postgres` | PostgreSQL接続 |

### DevOps

| 拡張機能 | ID | 用途 |
|---------|-----|-----|
| **Docker** | `ms-azuretools.vscode-docker` | Docker管理 |
| **Kubernetes** | `ms-kubernetes-tools.vscode-kubernetes-tools` | K8s管理 |
| **YAML** | `redhat.vscode-yaml` | YAML検証 |

---

## 🔧 IntelliJ IDEA / WebStorm 設定

### コードスタイル設定

#### TypeScript/JavaScript

```
Settings → Editor → Code Style → TypeScript/JavaScript
- Tab size: 2
- Indent: 2
- Use tab character: OFF
- Continuation indent: 2
```

#### Java

```
Settings → Editor → Code Style → Java
- Tab size: 4
- Indent: 4
- Use tab character: OFF
- Continuation indent: 8
```

### プラグイン

| プラグイン | 用途 |
|-----------|-----|
| **Prettier** | コードフォーマット |
| **ESLint** | JavaScript/TypeScriptリンター |
| **CheckStyle-IDEA** | Java静的解析 |
| **SonarLint** | コード品質チェック |
| **GitToolBox** | Git拡張 |
| **Rainbow Brackets** | ブラケット可視化 |

### 自動保存時の設定

```
Settings → Tools → Actions on Save
✓ Reformat code
✓ Optimize imports
✓ Run code cleanup
✓ Run ESLint --fix
```

---

## 🎨 PyCharm 設定

### コードスタイル設定

```
Settings → Editor → Code Style → Python
- Tab size: 4
- Indent: 4
- Use tab character: OFF
- Continuation indent: 4
```

### Python インタープリター設定

```
Settings → Project → Python Interpreter
- 仮想環境（venv）を使用
- Python 3.11以上を推奨
```

### プラグイン

| プラグイン | 用途 |
|-----------|-----|
| **Pylint** | Python静的解析 |
| **Black Formatter** | Pythonフォーマット |
| **Docker** | Docker統合 |
| **Database Navigator** | DB管理 |

---

## 📋 .editorconfig ファイル

プロジェクトルートに配置する`.editorconfig`ファイルの標準設定：

```ini
# EditorConfig: https://editorconfig.org

root = true

# 全ファイル共通
[*]
charset = utf-8
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true

# TypeScript/JavaScript
[*.{ts,tsx,js,jsx,json}]
indent_style = space
indent_size = 2

# Python
[*.py]
indent_style = space
indent_size = 4
max_line_length = 120

# Java
[*.java]
indent_style = space
indent_size = 4
max_line_length = 120

# SQL
[*.sql]
indent_style = space
indent_size = 2

# CSS/SCSS
[*.{css,scss,sass}]
indent_style = space
indent_size = 2

# YAML
[*.{yml,yaml}]
indent_style = space
indent_size = 2

# Markdown
[*.md]
trim_trailing_whitespace = false
max_line_length = off

# Makefiles
[Makefile]
indent_style = tab
```

---

## 🚀 クイックセットアップ手順

### Visual Studio Code

1. **VSCodeのインストール**
   ```bash
   # macOS
   brew install --cask visual-studio-code
   
   # Windows
   winget install Microsoft.VisualStudioCode
   ```

2. **設定ファイルの配置**
   ```bash
   # macOS/Linux
   cp settings.json ~/.config/Code/User/settings.json
   
   # Windows
   copy settings.json %APPDATA%\Code\User\settings.json
   ```

3. **拡張機能の一括インストール**
   ```bash
   # extensions.txtファイルから一括インストール
   cat extensions.txt | xargs -L 1 code --install-extension
   ```

### IntelliJ IDEA

1. **設定のインポート**
   - `File → Manage IDE Settings → Import Settings`
   - 組織標準の設定ファイル（`intellij-settings.zip`）をインポート

2. **プラグインのインストール**
   - `File → Settings → Plugins`
   - 必須プラグインリストから順次インストール

---

## 🔍 トラブルシューティング

### フォーマットが保存時に実行されない

**原因**: フォーマッターが未インストールまたは設定が無効

**解決策**:
```json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode"
}
```

### ESLintエラーが表示されない

**原因**: ESLint拡張機能が未インストールまたは設定ファイルが不正

**解決策**:
1. ESLint拡張機能をインストール
2. プロジェクトルートに`.eslintrc.js`が存在するか確認
3. `npm install`で依存関係をインストール

### Python仮想環境が認識されない

**原因**: インタープリターが未選択

**解決策**:
1. `Cmd+Shift+P` → `Python: Select Interpreter`
2. `.venv`内のPythonを選択

---

## 📚 参考リソース

- **Visual Studio Code公式**: https://code.visualstudio.com/docs
- **IntelliJ IDEA公式**: https://www.jetbrains.com/help/idea/
- **EditorConfig**: https://editorconfig.org/
- **Prettier**: https://prettier.io/

---

## 🔄 更新履歴

### v1.0.0 (2025-10-27)
- 初版作成
- VSCode、IntelliJ IDEA、PyCharmの基本設定を定義
- 必須拡張機能リストを追加
- .editorconfigファイルの標準を制定
