# コードテンプレート（言語別ドキュメントコメント）

## 📋 概要

各プログラミング言語のドキュメントコメントテンプレート集です。
Phase 3実装時に参照し、コピー&ペーストして使用してください。

---

## 🌐 対応言語

| 言語 | ドキュメント形式 | ディレクトリ | Linter |
|------|----------------|-------------|--------|
| TypeScript/JavaScript | JSDoc | `typescript/` | ESLint + jsdoc |
| Python | Docstring (Google Style) | `python/` | Pylint + pydocstyle |
| Java | Javadoc | `java/` | Checkstyle |
| C# | XML Documentation | `csharp/` | StyleCop |
| Go | Godoc | `go/` | golint |
| Rust | Rustdoc | `rust/` | rustdoc |
| PHP | PHPDoc | `php/` | PHPStan |
| Ruby | RDoc | `ruby/` | RuboCop |

---

## 📁 ディレクトリ構造

```
code-templates/
├── typescript/          # TypeScript/JavaScript
│   ├── file-header.txt
│   ├── class-jsdoc.txt
│   ├── function-jsdoc.txt
│   └── interface-jsdoc.txt
├── python/              # Python
│   ├── module-docstring.txt
│   ├── class-docstring.txt
│   ├── function-docstring.txt
│   └── google-style-guide.md
├── java/                # Java
│   ├── file-header.txt
│   ├── class-javadoc.txt
│   ├── method-javadoc.txt
│   └── package-info-template.java
└── ...
```

---

## 🚀 使用方法

### ステップ1: 言語を選択

自分が実装する言語のディレクトリに移動:
```bash
cd /08-templates/code-templates/[言語名]/
```

### ステップ2: テンプレートをコピー

必要なテンプレートファイルを開き、コピー:
```bash
# 例: TypeScript関数JSDocテンプレート
cat typescript/function-jsdoc.txt
```

### ステップ3: コードに貼り付け

コピーしたテンプレートをエディタに貼り付け、
プロジェクト固有の情報に書き換え。

### ステップ4: 自動チェック

Linterで検証:
```bash
# TypeScript/JavaScript
npm run lint:jsdoc

# Python
pylint --enable=missing-docstring src/

# Java
mvn checkstyle:check
```

---

## 📖 言語別ガイド

### TypeScript/JavaScript

**参照ドキュメント**: `/01-coding-standards/typescript-javascript-standards.md` - セクションX

**テンプレート**:
- `file-header.txt` - ファイルヘッダー（@fileoverview）
- `class-jsdoc.txt` - クラスJSDoc
- `function-jsdoc.txt` - 関数JSDoc
- `interface-jsdoc.txt` - インターフェースJSDoc

**自動チェック**:
```bash
npm run lint:jsdoc
npm run docs:generate  # TypeDoc
```

---

### Python

**参照ドキュメント**: `/01-coding-standards/python-standards.md` - セクションX

**テンプレート**:
- `module-docstring.txt` - モジュールDocstring
- `class-docstring.txt` - クラスDocstring
- `function-docstring.txt` - 関数Docstring（Google Style）
- `google-style-guide.md` - Google Styleガイド詳細

**自動チェック**:
```bash
pylint --enable=missing-docstring src/
pydocstyle src/
```

---

### Java

**参照ドキュメント**: `/01-coding-standards/java-standards.md` - セクションX

**テンプレート**:
- `file-header.txt` - ファイルヘッダー
- `class-javadoc.txt` - クラスJavadoc
- `method-javadoc.txt` - メソッドJavadoc
- `package-info-template.java` - パッケージJavadoc

**自動チェック**:
```bash
mvn checkstyle:check
mvn javadoc:javadoc
```

---

## 🔗 関連ドキュメント

- `/00-guides/phase-guides/phase-3-implementation-guide.md` - セクション3.7
- `/00-guides/ai-guides/AI-DOCUMENTATION-COMMENT-CHECKLIST.md`
- `/03-development-process/documentation-standards.md` - セクション7

---
