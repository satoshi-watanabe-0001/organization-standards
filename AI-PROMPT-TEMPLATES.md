# AI開発者向け プロンプトテンプレート集

**対象**: Devin、Cursor、GitHub Copilot、その他のAI開発支援ツール  
**最終更新**: 2025-10-23

---

## 📌 このドキュメントについて

AI開発ツールに対して、組織のコーディング規約を遵守したコードを生成させるためのプロンプトテンプレート集です。

---

## 🎯 基本プロンプトテンプレート

### テンプレート1: 新規コード生成（汎用）

```
【指示】
以下の要件に従って、[言語名]で[機能の説明]を実装してください。

【必須遵守事項】
1. 以下のドキュメントを参照してコーディング規約に従うこと：
   - /devin-organization-standards/01-coding-standards/00-general-principles.md
   - /devin-organization-standards/01-coding-standards/[言語]-standards.md

2. 数値基準の遵守：
   - 関数: 50行以内（理想20行）
   - クラス: 400行以内（理想200行）
   - 循環的複雑度: 10以下（理想5）
   - ネスト深さ: 3階層以内（理想2階層）
   - パラメータ数: 5個以内（理想3個）

3. ドキュメンテーション必須：
   - ファイルヘッダーコメント
   - すべてのパブリッククラス/関数にコメント
   - パラメータ、戻り値、例外の説明を完備
   - [JSDoc / Docstring / Javadoc / SQLコメント / CSS/SCSSコメント]形式を使用

【要件】
- [具体的な機能要件を記載]

【確認】
生成後、以下を確認してください：
- [ ] 関数サイズ・複雑度が基準内
- [ ] 完全なドキュメンテーション
- [ ] 自動チェックツール（ESLint/Pylint等）でエラーなし
```

---

## 🔧 言語別プロンプトテンプレート

### TypeScript/JavaScript

```
【指示】
TypeScript/JavaScriptで[機能の説明]を実装してください。

【必須遵守】
1. コーディング規約:
   - /devin-organization-standards/01-coding-standards/00-general-principles.md
   - /devin-organization-standards/01-coding-standards/typescript-javascript-standards.md

2. JSDoc必須:
   - ファイルヘッダー: @fileoverview, @module
   - クラス: @class, @example（複雑なAPIの場合）
   - 関数: @param, @returns, @throws

3. 数値基準:
   - 関数50行以内、複雑度10以下、ネスト3階層以内

4. ESLintチェック:
   生成後、`npx eslint --ext .ts,.tsx [ファイル名]` でエラーがないこと

【要件】
- [具体的な要件]

【期待する出力形式】
```typescript
/**
 * @fileoverview [ファイルの目的]
 * @module [モジュール名]
 */

// 実装コード
```
```

---

### Python

```
【指示】
Pythonで[機能の説明]を実装してください。

【必須遵守】
1. コーディング規約:
   - /devin-organization-standards/01-coding-standards/00-general-principles.md
   - /devin-organization-standards/01-coding-standards/python-standards.md

2. Google Style Docstring必須:
   - モジュールDocstring
   - クラス: Attributes, Example
   - 関数: Args, Returns, Raises

3. 型ヒント必須:
   - すべての関数にtype hints
   - from typing import ... を適切に使用

4. 数値基準:
   - 関数50行以内、複雑度10以下、ネスト3階層以内

5. Pylintチェック:
   生成後、`pylint [ファイル名]` でエラーがないこと

【要件】
- [具体的な要件]

【期待する出力形式】
```python
"""[モジュールの目的]

[詳細な説明]
"""

from typing import Optional

# 実装コード
```
```

---

### Java

```
【指示】
Javaで[機能の説明]を実装してください。

【必須遵守】
1. コーディング規約:
   - /devin-organization-standards/01-coding-standards/00-general-principles.md
   - /devin-organization-standards/01-coding-standards/java-standards.md

2. Javadoc必須:
   - パッケージ: package-info.java
   - クラス: @author, @since, @see
   - メソッド: @param（null許可の可否明記）, @return, @throws

3. 数値基準:
   - メソッド50行以内、複雑度10以下、ネスト3階層以内

4. CheckStyleチェック:
   生成後、`mvn checkstyle:check` でエラーがないこと

【要件】
- [具体的な要件]

【期待する出力形式】
```java
/**
 * [クラスの目的]
 * 
 * <p>[詳細な説明]</p>
 * 
 * @author [作成者]
 * @since [バージョン]
 */
public class MyClass {
    // 実装コード
}
```
```

---

### SQL

```
【指示】
SQLで[機能の説明]を実装してください。

【必須遵守】
1. コーディング規約:
   - /devin-organization-standards/01-coding-standards/00-general-principles.md
   - /devin-organization-standards/01-coding-standards/sql-standards.md

2. SQLコメント必須:
   - スクリプトヘッダー: ファイル名、目的、依存関係
   - テーブル: テーブルの目的、各カラムの説明
   - ビュー/プロシージャ: 目的、依存関係、パフォーマンス注意事項

3. 複雑なクエリ:
   - JOIN 3つ以上の場合は各部分を説明
   - ビジネスルールをコメントで明記

【要件】
- [具体的な要件]

【期待する出力形式】
```sql
-- ============================================================================
-- Script: [ファイル名].sql
-- Description: [スクリプトの目的]
-- Dependencies: [依存関係]
-- ============================================================================

-- [実装の説明]
CREATE TABLE ...
```
```

---

### CSS/SCSS

```
【指示】
CSS/SCSSで[コンポーネント/レイアウトの説明]を実装してください。

【必須遵守】
1. コーディング規約:
   - /devin-organization-standards/01-coding-standards/00-general-principles.md
   - /devin-organization-standards/01-coding-standards/css-styling-standards.md

2. CSS/SCSSコメント必須:
   - ファイルヘッダー: @component, @category, @responsive
   - コンポーネント: Modifiers, States
   - 複雑なレイアウト: Grid Areas, Layout図, Responsive

3. BEM命名規則:
   - ブロック__エレメント--モディファイア

4. Stylelintチェック:
   生成後、`npx stylelint [ファイル名]` でエラーがないこと

【要件】
- [具体的な要件]

【期待する出力形式】
```scss
/**
 * [コンポーネントの目的]
 * 
 * @component [コンポーネント名]
 * @responsive [レスポンシブ対応状況]
 */

// 実装コード
```
```

---

## 🔄 リファクタリング用プロンプト

### テンプレート: 既存コードのリファクタリング

```
【指示】
以下のコードを、組織のコーディング規約に準拠するようリファクタリングしてください。

【対象コード】
```[言語]
[既存のコード]
```

【必須遵守】
1. コーディング規約の参照:
   - /devin-organization-standards/01-coding-standards/00-general-principles.md
   - /devin-organization-standards/01-coding-standards/[言語]-standards.md

2. 改善ポイント:
   - [ ] 関数サイズを50行以内に分割
   - [ ] 循環的複雑度を10以下に削減
   - [ ] ネストを3階層以内に削減
   - [ ] 完全なドキュメンテーションコメントを追加
   - [ ] マジックナンバーを定数化

3. 機能は変更しないこと（動作は同じ）

【出力形式】
1. リファクタリング後のコード
2. 変更点の説明
3. 改善されたメトリクス（行数、複雑度等）
```

---

## 📊 コードレビュー用プロンプト

### テンプレート: コードレビュー依頼

```
【指示】
以下のコードを組織のコーディング規約に照らしてレビューしてください。

【対象コード】
```[言語]
[レビュー対象のコード]
```

【レビュー観点】
1. コーディング規約準拠:
   - /devin-organization-standards/01-coding-standards/00-general-principles.md
   - /devin-organization-standards/01-coding-standards/[言語]-standards.md

2. 数値基準チェック:
   - 関数サイズ（50行以内？）
   - 複雑度（10以下？）
   - ネスト深さ（3階層以内？）
   - パラメータ数（5個以内？）

3. ドキュメンテーションチェック:
   - ファイルヘッダー有無
   - パブリックAPI全てにコメント有無
   - @param, @returns, @throws 完備

4. コード品質:
   - SOLID原則準拠
   - DRY原則（重複コードなし）
   - 適切な命名

【出力形式】
1. 問題点のリスト（優先度付き）
2. 改善提案
3. 修正例（必要に応じて）
```

---

## 🧪 テストコード生成用プロンプト

### テンプレート: テストコード生成

```
【指示】
以下のコードに対するテストコードを生成してください。

【対象コード】
```[言語]
[テスト対象のコード]
```

【必須遵守】
1. テスト戦略:
   - AAA パターン（Arrange, Act, Assert）
   - 正常系・異常系・境界値をカバー
   - テストケース名は日本語で目的を明記

2. コーディング規約:
   - /devin-organization-standards/01-coding-standards/00-general-principles.md
   - /devin-organization-standards/01-coding-standards/[言語]-standards.md

3. ドキュメンテーション:
   - 各テストケースの目的をコメントで説明

4. カバレッジ:
   - ビジネスロジックは100%カバレッジ目標

【出力形式】
1. テストコード
2. テストケース一覧（何をテストしているか）
3. 想定カバレッジ
```

---

## 🆕 新規プロジェクト用プロンプト

### テンプレート: プロジェクト初期セットアップ

```
【指示】
[言語]で新規プロジェクトを開始します。組織のコーディング規約に準拠したプロジェクト構造とツール設定を作成してください。

【必須遵守】
1. コーディング規約の参照:
   - /devin-organization-standards/01-coding-standards/00-general-principles.md
   - /devin-organization-standards/01-coding-standards/[言語]-standards.md

2. 自動チェックツールの設定:
   [TypeScript/JS の場合]
   - .eslintrc.json（jsdoc plugin含む）
   - tsconfig.json
   - package.json（依存関係）

   [Python の場合]
   - .pylintrc
   - setup.cfg（pydocstyle設定）
   - pyproject.toml

   [Java の場合]
   - checkstyle.xml
   - pom.xml（Maven）または build.gradle（Gradle）

   [CSS/SCSS の場合]
   - .stylelintrc.json

3. プロジェクト構造:
   - src/、tests/、docs/ など標準的な構造

4. README.md:
   - プロジェクト概要
   - セットアップ手順
   - コーディング規約へのリンク

【出力形式】
1. ディレクトリ構造
2. 各設定ファイルの内容
3. セットアップ手順
```

---

## 💡 使用上のヒント

### プロンプト改善のコツ

1. **具体的な要件を明記**
   - ❌ 「ユーザー認証を実装して」
   - ✅ 「JWT認証を実装。メール・パスワード検証、トークン発行、有効期限チェックを含む」

2. **規約ドキュメントのパスを必ず含める**
   - AI が参照できるよう、明確なパスを指定

3. **期待する出力形式を明示**
   - コードブロック、説明文、チェックリスト等

4. **セルフチェックを依頼**
   - 生成後にAI自身に基準チェックさせる

5. **反復的改善**
   - 一度で完璧を求めず、レビュー→修正を繰り返す

---

## 📝 プロンプト使用例

### 例1: 新規関数の生成

```
【指示】
Pythonでユーザー認証関数を実装してください。

【必須遵守】
- /devin-organization-standards/01-coding-standards/00-general-principles.md
- /devin-organization-standards/01-coding-standards/python-standards.md
- 関数50行以内、複雑度10以下
- Google Style Docstring必須
- 型ヒント必須

【要件】
- メールアドレスとパスワードで認証
- bcryptでパスワードハッシュ検証
- 成功時はUserオブジェクト、失敗時はNone
- ValidationError、DatabaseErrorを適切に処理

【確認】
生成後、pylint実行でエラーがないこと
```

### 例2: リファクタリング

```
【指示】
以下のPython関数をリファクタリングしてください。

【対象コード】
```python
def process(data):
    if data:
        if len(data) > 0:
            result = []
            for item in data:
                if item['status'] == 'active':
                    if item['value'] > 100:
                        result.append(item)
            return result
    return None
```

【改善ポイント】
- ネスト削減（Early Return）
- 関数分割（フィルタ処理を別関数化）
- 型ヒント追加
- Google Style Docstring追加
- リスト内包表記の活用

【必須遵守】
- /devin-organization-standards/01-coding-standards/python-standards.md
- 複雑度5以下、ネスト2階層以内
```

---

## 📞 サポート

プロンプトテンプレートに関する質問や改善提案：
- 詳細指示: `AI-CODING-INSTRUCTIONS.md`
- クイックリファレンス: `AI-QUICK-REFERENCE.md`
- 開発チームSlack: #dev-standards

---

**重要**: これらのプロンプトテンプレートは、AI開発ツールが組織のコーディング規約を遵守したコードを生成するためのガイドです。プロジェクトの状況に応じてカスタマイズしてください。
