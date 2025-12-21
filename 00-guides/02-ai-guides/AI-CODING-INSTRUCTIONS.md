# AI開発者向け コーディング規約参照指示

**最終更新**: 2025-10-23  
**対象**: Devin、Cursor、GitHub Copilot、その他のAI開発支援ツール

---

## 🎯 目的

このドキュメントは、AI開発者がコードを生成・修正する際に、組織のコーディング規約を正確に遵守するための指示を提供します。

---

## 📚 必須参照ドキュメント

AI開発者は、コード生成前に以下のドキュメントを**必ず参照**してください：

### 1️⃣ 共通原則（全言語必須）

**ファイル**: `/01-coding-standards/00-general-principles.md`

**参照タイミング**: すべてのコード生成・修正時

**重要セクション**:
- **1.3 コード保守性メトリクス**: 関数/クラスサイズ、複雑度の数値基準
- **5.3 ドキュメンテーション必須レベル**: コメント必須要件（3段階）
- **5.4 ファイル/クラスレベルドキュメンテーション標準**: 言語別形式

**遵守必須の数値基準**:
```
関数/メソッド:
  - 理想: 20行以内
  - 推奨上限: 50行
  - 絶対上限: 100行

クラス/ファイル:
  - 理想: 200行以内
  - 推奨上限: 400行
  - 絶対上限: 1000行

循環的複雑度:
  - 理想: 5以下
  - 推奨上限: 10
  - 絶対上限: 15

ネスト深さ:
  - 理想: 2階層以内
  - 推奨上限: 3階層
  - 絶対上限: 4階層

パラメータ数:
  - 理想: 3個以内
  - 推奨上限: 5個
  - 絶対上限: 7個
```

**ドキュメンテーション必須要件**:
```
Level 1（必須 - 違反は品質ゲート不合格）:
  ✓ すべてのファイルにファイルヘッダー
  ✓ すべてのパブリッククラス/インターフェースにコメント
  ✓ すべてのパブリックAPI（関数/メソッド）にコメント

Level 2（強く推奨 - レビューで指摘）:
  ✓ 循環的複雑度10以上の関数
  ✓ ビジネスルール・制約を反映した実装
  ✓ 非自明な実装（最適化、回避策）

Level 3（任意）:
  - 単純なゲッター/セッター
  - 自己説明的なコード
```

---

### 2️⃣ 言語別規約（使用言語に応じて参照）

#### TypeScript/JavaScript

**ディレクトリ**: `/01-coding-standards/typescript/` (10ファイル、AI-QUICK-REFERENCE.md参照)

**必須セクション**:
- **X. ドキュメンテーション標準**: JSDoc形式、ESLint設定

**コード生成時の必須要件**:
```typescript
// ✅ 必須: ファイルヘッダー
/**
 * @fileoverview ファイルの目的・責任
 * @module モジュール名
 */

// ✅ 必須: パブリッククラスにJSDoc
/**
 * クラスの目的と責任
 * @class
 * @example 使用例（複雑なAPIの場合）
 */
export class MyClass {}

// ✅ 必須: パブリック関数にJSDoc
/**
 * 関数の目的・動作
 * @param name - パラメータの説明
 * @returns 戻り値の説明
 * @throws {ErrorType} 例外の説明
 */
export function myFunction(name: string): void {}
```

**自動チェック**: ESLint + jsdoc plugin を使用

---

#### Python

**ディレクトリ**: `/01-coding-standards/python/` (16ファイル、AI-QUICK-REFERENCE.md参照)

**必須セクション**:
- **X. ドキュメンテーション標準**: Google Style Docstring、Pylint設定

**コード生成時の必須要件**:
```python
# ✅ 必須: モジュールDocstring
"""モジュールの目的・責任

詳細な説明。典型的な使用例など。
"""

# ✅ 必須: パブリッククラスにDocstring
class MyClass:
    """クラスの目的と責任
    
    Attributes:
        attr1: 属性の説明
    
    Example:
        >>> obj = MyClass()
    """
    pass

# ✅ 必須: パブリック関数にDocstring
def my_function(name: str) -> None:
    """関数の目的・動作
    
    Args:
        name: パラメータの説明
    
    Returns:
        戻り値の説明
    
    Raises:
        ErrorType: 例外の説明
    """
    pass
```

**自動チェック**: Pylint + pydocstyle を使用

---

#### Java

**ディレクトリ**: `/01-coding-standards/java/` (10ファイル、AI-QUICK-REFERENCE.md参照)

**必須セクション**:
- **X. ドキュメンテーション標準**: Javadoc、CheckStyle設定

**コード生成時の必須要件**:
```java
// ✅ 必須: パッケージDocumentation (package-info.java)
/**
 * パッケージの目的・責任
 * @since 1.0
 */
package com.example.mypackage;

// ✅ 必須: パブリッククラスにJavadoc
/**
 * クラスの目的と責任
 * @author 作成者（オプション）
 * @since 1.0
 */
public class MyClass {}

// ✅ 必須: パブリックメソッドにJavadoc
/**
 * メソッドの目的・動作
 * @param name パラメータの説明（null許可の可否を明記）
 * @return 戻り値の説明
 * @throws ExceptionType 例外の説明
 */
public void myMethod(String name) throws ExceptionType {}
```

**自動チェック**: CheckStyle を使用

---

#### SQL

**ディレクトリ**: `/01-coding-standards/sql/` (10ファイル)

**必須セクション**:
- **X. ドキュメンテーション標準**: SQLコメント形式

**コード生成時の必須要件**:
```sql
-- ============================================================================
-- Script: ファイル名.sql
-- Description: スクリプトの目的・責任
-- Dependencies: 依存関係
-- Notes: 注意事項
-- ============================================================================

-- ✅ 必須: テーブルにコメント
-- テーブルの目的・責任
CREATE TABLE users (
    -- ユーザーID（プライマリキー、UUID形式）
    user_id VARCHAR(36) NOT NULL PRIMARY KEY,
    
    -- メールアドレス（ログイン識別子、ユニーク制約）
    email VARCHAR(255) NOT NULL UNIQUE
);

-- ✅ 必須: ビュー・プロシージャにコメント
-- ============================================================================
-- View: view_name
-- Description: ビューの目的
-- Dependencies: 依存テーブル
-- Performance: パフォーマンス注意事項
-- ============================================================================
CREATE VIEW view_name AS ...
```

---

#### CSS/SCSS

**ディレクトリ**: `/01-coding-standards/css/` (17ファイル)

**必須セクション**:
- **X. ドキュメンテーション標準**: CSS/SCSSコメント、Stylelint設定

**コード生成時の必須要件**:
```scss
/**
 * ファイル/コンポーネントの目的
 * 
 * 詳細な説明。適用範囲、レスポンシブ対応状況など。
 * 
 * @component コンポーネント名
 * @category カテゴリ
 * @responsive レスポンシブ対応状況
 */

/**
 * コンポーネントクラスの目的
 * 
 * Modifiers:
 *   --modifier: モディファイアの説明
 * 
 * States:
 *   .is-state: 状態の説明
 */
.component {}

/**
 * 複雑なレイアウトの説明
 * 
 * Grid Areas: エリアの説明
 * Layout: ASCII図（推奨）
 * Responsive: レスポンシブ動作
 */
.layout {}
```

**自動チェック**: Stylelint を使用

---

## 🤖 AI開発者への具体的指示

### コード生成時の必須手順

```
ステップ1: 使用言語の特定
  ↓
ステップ2: 共通原則（00-general-principles.md）の参照
  ↓
ステップ3: 言語別規約の参照
  ↓
ステップ4: コード生成
  ↓
ステップ5: セルフチェック（以下の基準）
```

### セルフチェックリスト

**必須チェック項目**:

#### ✅ 構造品質
- [ ] 関数は50行以内か？（理想は20行）
- [ ] クラスは400行以内か？（理想は200行）
- [ ] 循環的複雑度は10以下か？（理想は5）
- [ ] ネストは3階層以内か？（理想は2階層）
- [ ] パラメータは5個以内か？（理想は3個）

#### ✅ ドキュメンテーション
- [ ] ファイルヘッダーコメントがあるか？
- [ ] パブリッククラスにコメントがあるか？
- [ ] パブリックAPIにコメントがあるか？
- [ ] コメントは「なぜ」を説明しているか？（「何を」だけでない）
- [ ] 言語に応じた標準形式（JSDoc、Docstring、Javadoc等）を使用しているか？

#### ✅ コードレビュー準備
- [ ] ESLint / Pylint / CheckStyle / Stylelint でエラーがないか？
- [ ] ドキュメント生成（TypeDoc、Sphinx、Javadoc等）でエラーがないか？

---

## 🚨 絶対に避けるべき反パターン

### ❌ 禁止事項

```python
# ❌ NG: ファイルヘッダーなし
def my_function():
    pass

# ❌ NG: パブリックAPIにコメントなし
def public_api(param1, param2, param3):
    pass

# ❌ NG: 「何を」だけのコメント
def get_user(id):
    """ユーザーを取得する"""  # これだけでは不十分
    pass

# ❌ NG: 関数が長すぎる（100行超）
def giant_function():
    # 150行のコード...
    pass

# ❌ NG: 複雑度が高すぎる（15超）
def complex_function():
    if ...:
        if ...:
            if ...:
                if ...:  # ネスト4階層超
                    pass
```

### ✅ 正しい例

```python
"""ユーザー認証モジュール

ユーザーのログイン・ログアウト・トークン検証機能を提供する。
JWTベースの認証を実装し、リフレッシュトークンをサポート。
"""

def authenticate_user(email: str, password: str) -> Optional[User]:
    """ユーザーを認証し、認証情報を返す
    
    メールアドレスとパスワードを検証し、認証に成功した場合は
    ユーザーオブジェクトを返す。パスワードはbcryptでハッシュ化される。
    
    Args:
        email: ユーザーのメールアドレス（形式検証済みであること）
        password: ユーザーのパスワード（平文）
    
    Returns:
        認証成功時はUserオブジェクト、失敗時はNone
    
    Raises:
        ValidationError: メールアドレスの形式が不正な場合
        DatabaseError: データベース接続に失敗した場合
    
    Example:
        >>> user = authenticate_user('user@example.com', 'password123')
        >>> if user:
        ...     print(f"Welcome, {user.name}")
    """
    # 実装は20行以内に収める
    pass
```

---

## 🔧 自動チェックツールの設定

AI開発者がコードを生成した後、以下のツールで自動検証してください：

### TypeScript/JavaScript
```bash
# ESLint実行
npx eslint --ext .ts,.tsx,.js,.jsx src/

# TypeDoc生成確認
npx typedoc --out docs src
```

### Python
```bash
# Pylint実行
pylint src/

# pydocstyle実行
pydocstyle src/

# Sphinx生成確認
cd docs && make html
```

### Java
```bash
# CheckStyle実行
mvn checkstyle:check

# Javadoc生成確認
mvn javadoc:javadoc
```

### CSS/SCSS
```bash
# Stylelint実行
npx stylelint "src/**/*.{css,scss}"
```

---

## 📋 コード生成テンプレート

### 新規ファイル作成時のテンプレート

#### TypeScript/JavaScript
```typescript
/**
 * @fileoverview [ファイルの目的を1-2行で説明]
 * 
 * [詳細な説明]
 * 
 * @module [モジュール名]
 */

import { ... } from '...';

/**
 * [クラスの目的と責任]
 * 
 * [詳細な説明]
 * 
 * @class
 * @example
 * const obj = new MyClass();
 * obj.method();
 */
export class MyClass {
  /**
   * [メソッドの目的]
   * 
   * [詳細な説明]
   * 
   * @param param - パラメータの説明
   * @returns 戻り値の説明
   * @throws {ErrorType} 例外の説明
   */
  public method(param: string): void {
    // 実装（20行以内を目標）
  }
}
```

#### Python
```python
"""[モジュールの目的を1-2行で説明]

[詳細な説明]

Example:
    典型的な使用例
"""

from typing import Optional


class MyClass:
    """[クラスの目的と責任]
    
    [詳細な説明]
    
    Attributes:
        attr1: 属性の説明
    
    Example:
        >>> obj = MyClass()
        >>> obj.method()
    """
    
    def method(self, param: str) -> None:
        """[メソッドの目的]
        
        [詳細な説明]
        
        Args:
            param: パラメータの説明
        
        Returns:
            戻り値の説明
        
        Raises:
            ErrorType: 例外の説明
        
        Example:
            >>> obj.method('value')
        """
        # 実装（20行以内を目標）
        pass
```

---

## 🎯 優先度と判断基準

### 必須（違反は品質ゲート不合格）
1. ファイルヘッダーコメント
2. パブリッククラス/インターフェースのコメント
3. パブリックAPIのコメント（`@param`、`@returns`、`@throws`完備）
4. 関数100行以内、クラス1000行以内
5. 循環的複雑度15以内
6. ネスト4階層以内

### 強く推奨（コードレビューで指摘）
1. 関数50行以内、クラス400行以内
2. 循環的複雑度10以内
3. 複雑なロジック（複雑度10以上）のコメント
4. ビジネスルール・制約のコメント
5. 非自明な実装のコメント

### 推奨（ベストプラクティス）
1. 関数20行以内、クラス200行以内
2. 循環的複雑度5以内
3. ネスト2階層以内
4. パラメータ3個以内
5. 使用例（`@example`）の提供

---

## 🔄 リファクタリングトリガー

AI開発者は、以下の条件に該当する場合、即座にリファクタリングを提案してください：

### 即座にリファクタ（優先度: 高）
- 循環的複雑度 15超
- 関数の行数 100超
- クラスの行数 1000超
- ネスト 4階層超
- パラメータ数 7個超
- 同一コードの3箇所以上の重複

### 計画的リファクタ（優先度: 中）
- 循環的複雑度 10超
- 関数の行数 50超
- クラスの行数 400超
- 依存関係 10超
- コード重複率 5%超

---

## 💡 AI開発者への推奨アプローチ

### 1. コード生成前の確認
```
質問: 「このコードを生成する際、以下のドキュメントを参照しますか？」
  - /01-coding-standards/00-general-principles.md
  - /01-coding-standards/[言語]/ ディレクトリ内のファイル

確認: 「生成するコードは以下の基準を満たしますか？」
  - 関数50行以内
  - 循環的複雑度10以内
  - 完全なドキュメンテーションコメント
```

### 2. コード生成時の配慮
- 関数は単一責任に分割
- 複雑な条件はヘルパー関数に抽出
- ビジネスロジックはコメントで説明
- マジックナンバーは定数化

### 3. コード生成後の検証
- セルフチェックリストの実行
- 自動チェックツールの実行
- リファクタリング提案（必要に応じて）

---

## 📞 サポート・質問

規約に関する質問や不明点がある場合：
1. 各言語規約ファイルの具体例を確認
2. 開発チームに確認

---

## 📝 改訂履歴

| バージョン | 日付 | 変更内容 |
|-----------|------|----------|
| 1.0 | 2025-10-23 | 初版作成（保守性メトリクス + ドキュメンテーション標準対応） |

---

**重要**: このドキュメントは、AI開発者が組織のコーディング規約を遵守するための指示書です。すべてのコード生成時に参照し、高品質で保守性の高いコードを生成してください。
