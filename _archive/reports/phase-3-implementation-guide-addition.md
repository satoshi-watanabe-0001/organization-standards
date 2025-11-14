# Phase 3実装ガイドへの追加セクション

以下のセクションを `/00-guides/phase-guides/phase-3-implementation-guide.md` の末尾に追加してください。

---

## 3.7 ドキュメントコメント必須化ガイド（全言語共通） ⭐NEW

### 3.7.1 概要

**重要**: ドキュメントコメントは品質ゲートの一部です。
以下の基準を満たさないコードは、Phase 4レビューで却下されます。

**対象言語**:
- TypeScript/JavaScript (JSDoc)
- Python (Docstring)
- Java (Javadoc)
- その他（言語標準に準拠）

---

### 3.7.2 必須レベル別チェックリスト（言語非依存）

#### 🔴 Level 1: 必須（品質ゲート）

**ファイルレベル**:
- [ ] すべてのソースファイルにファイルヘッダーコメントを記述した
  - TypeScript: `@fileoverview`
  - Python: モジュールDocstring
  - Java: パッケージJavadoc
- [ ] ファイルの目的・責任を明記した
- [ ] モジュール/パッケージ構造を文書化した

**クラス・インターフェースレベル**:
- [ ] すべてのパブリッククラスにドキュメントコメントを記述した
- [ ] クラスの目的・責任を説明した
- [ ] 使用例を記述した（複雑なクラスの場合）

**関数・メソッドレベル**:
- [ ] すべてのパブリック関数/メソッドにドキュメントコメントを記述した
- [ ] すべてのパラメータを説明した
- [ ] 戻り値を説明した
- [ ] 発生しうる例外を説明した

**インターフェース・型定義レベル**:
- [ ] すべてのパブリックインターフェース/型にドキュメントコメントを記述した
- [ ] すべてのプロパティ/フィールドに説明を記述した

---

#### 🟡 Level 2: 強く推奨

**適用対象**:
- [ ] 複雑な内部ロジック（循環的複雑度10以上）
- [ ] ビジネスルール・制約を反映した実装
- [ ] 非自明な実装（パフォーマンス最適化、技術的回避策）
- [ ] セキュリティに関わる実装

**記述内容**:
- [ ] **なぜ**その実装になったかを説明
- [ ] ビジネス上の制約・理由を明記
- [ ] セキュリティ上の考慮事項を記載
- [ ] 将来の改善点や注意事項を記載（TODO/FIXME）

---

#### ⚪ Level 3: 任意

**適用対象**:
- 単純なゲッター/セッター
- 自己説明的なプライベートメソッド
- テストコード（ただしテスト目的は記述推奨）

---

### 3.7.3 言語別ドキュメントコメント形式

#### **TypeScript/JavaScript (JSDoc)**

**参照ドキュメント**: 
- `/01-coding-standards/typescript-javascript-standards.md` - セクションX
- `/08-templates/code-templates/typescript/`

**必須タグ**:
- `@fileoverview` - ファイルの目的
- `@class` / `@interface` - クラス/インターフェース
- `@param` - パラメータ
- `@returns` - 戻り値
- `@throws` - 例外

**自動チェック**:
```bash
npm run lint:jsdoc
```

---

#### **Python (Docstring)**

**参照ドキュメント**: 
- `/01-coding-standards/python-standards.md` - セクションX
- `/08-templates/code-templates/python/`

**必須形式**: Google Style Docstring

**必須セクション**:
- モジュールDocstring（ファイル先頭）
- クラスDocstring
- 関数Docstring:
  - `Args:` - パラメータ
  - `Returns:` - 戻り値
  - `Raises:` - 例外

**自動チェック**:
```bash
pylint --enable=missing-docstring src/
pydocstyle src/
```

---

#### **Java (Javadoc)**

**参照ドキュメント**: 
- `/01-coding-standards/java-standards.md` - セクションX
- `/08-templates/code-templates/java/`

**必須タグ**:
- クラスレベル: クラスの目的
- `@param` - パラメータ
- `@return` - 戻り値
- `@throws` - 例外
- `@author` - 作成者（オプション）
- `@since` - バージョン（オプション）

**自動チェック**:
```bash
mvn checkstyle:check
# または
./gradlew checkstyleMain
```

---

#### **その他の言語**

各言語の標準ドキュメントコメント形式に従ってください:

| 言語 | ドキュメント形式 | 参照先 |
|------|----------------|--------|
| C# | XML Documentation | `/01-coding-standards/csharp-standards.md` |
| Go | Godoc | `/01-coding-standards/go-standards.md` |
| Rust | Rustdoc | `/01-coding-standards/rust-standards.md` |
| PHP | PHPDoc | `/01-coding-standards/php-standards.md` |
| Ruby | RDoc | `/01-coding-standards/ruby-standards.md` |

---

### 3.7.4 実装フロー（言語非依存）

```
[ファイル作成]
  ↓
1. 言語別テンプレートからファイルヘッダーをコピー (1分)
   - TypeScript: /08-templates/code-templates/typescript/file-header.txt
   - Python: /08-templates/code-templates/python/module-docstring.txt
   - Java: /08-templates/code-templates/java/file-header.txt
  ↓
[クラス/インターフェース定義]
  ↓
2. 言語別テンプレートからクラスコメントをコピー (2分)
  ↓
[関数/メソッド実装]
  ↓
3. 言語別テンプレートから関数コメントをコピー (3分)
  ↓
4. 実装しながらコメントを具体化 (実装と同時)
  ↓
[実装完了後]
  ↓
5. 言語別Linterでチェック (1分)
   - TypeScript: npm run lint:jsdoc
   - Python: pylint + pydocstyle
   - Java: mvn checkstyle:check
  ↓
6. エラーがあれば修正 (数分)
  ↓
7. コミット（pre-commitフックで再チェック）
  ↓
✅ 完了
```

---

### 3.7.5 自動チェックの設定（言語別）

#### **TypeScript/JavaScript**

**ESLint + eslint-plugin-jsdoc**

```json
// .eslintrc.json
{
  "plugins": ["jsdoc"],
  "rules": {
    "jsdoc/require-jsdoc": ["error", {
      "require": {
        "FunctionDeclaration": true,
        "MethodDefinition": true,
        "ClassDeclaration": true
      },
      "publicOnly": true
    }],
    "jsdoc/check-param-names": "error",
    "jsdoc/require-param": "error",
    "jsdoc/require-param-description": "error",
    "jsdoc/require-returns": "error",
    "jsdoc/require-returns-description": "error"
  }
}
```

**インストール**:
```bash
npm install --save-dev eslint-plugin-jsdoc
```

---

#### **Python**

**Pylint + pydocstyle**

```ini
# .pylintrc
[MESSAGES CONTROL]
enable=missing-module-docstring,
       missing-class-docstring,
       missing-function-docstring

[BASIC]
docstring-min-length=10
no-docstring-rgx=^_  # プライベート関数は除外

[DESIGN]
max-complexity=10
```

```ini
# .pydocstyle
[pydocstyle]
convention = google
ignore = D100,D104  # パッケージ __init__.py は任意
match = .*\.py
```

**インストール**:
```bash
pip install pylint pydocstyle
```

**チェックコマンド**:
```bash
pylint --enable=missing-docstring src/
pydocstyle src/
```

---

#### **Java**

**Checkstyle**

```xml
<!-- checkstyle.xml -->
<module name="Checker">
  <module name="TreeWalker">
    <!-- Javadoc 必須化 -->
    <module name="MissingJavadocMethod">
      <property name="scope" value="public"/>
      <property name="allowMissingPropertyJavadoc" value="true"/>
    </module>
    
    <module name="MissingJavadocType">
      <property name="scope" value="public"/>
    </module>
    
    <!-- Javadoc 品質チェック -->
    <module name="JavadocMethod">
      <property name="validateThrows" value="true"/>
    </module>
    
    <module name="JavadocType"/>
    
    <module name="JavadocStyle">
      <property name="checkFirstSentence" value="true"/>
      <property name="checkEmptyJavadoc" value="true"/>
    </module>
  </module>
</module>
```

**Maven設定**:
```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-checkstyle-plugin</artifactId>
  <version>3.3.0</version>
  <configuration>
    <configLocation>checkstyle.xml</configLocation>
    <failOnViolation>true</failOnViolation>
  </configuration>
</plugin>
```

**チェックコマンド**:
```bash
mvn checkstyle:check
```

---

### 3.7.6 Pre-commitフック設定（言語横断）

**ファイル**: `.husky/pre-commit`

```bash
#!/bin/sh
. "$(dirname "$0")/_/husky.sh"

echo "🔍 Running Documentation Comment checks..."

# TypeScript/JavaScript プロジェクトの場合
if [ -f "package.json" ]; then
  echo "Checking JSDoc..."
  npm run lint:jsdoc
  if [ $? -ne 0 ]; then
    echo "❌ JSDoc violations found"
    exit 1
  fi
fi

# Python プロジェクトの場合
if [ -f "setup.py" ] || [ -f "pyproject.toml" ]; then
  echo "Checking Python Docstrings..."
  pylint --enable=missing-docstring src/
  if [ $? -ne 0 ]; then
    echo "❌ Docstring violations found"
    exit 1
  fi
  
  pydocstyle src/
  if [ $? -ne 0 ]; then
    echo "❌ Docstring style violations found"
    exit 1
  fi
fi

# Java プロジェクトの場合
if [ -f "pom.xml" ]; then
  echo "Checking Javadoc..."
  mvn checkstyle:check
  if [ $? -ne 0 ]; then
    echo "❌ Javadoc violations found"
    exit 1
  fi
elif [ -f "build.gradle" ]; then
  echo "Checking Javadoc..."
  ./gradlew checkstyleMain
  if [ $? -ne 0 ]; then
    echo "❌ Javadoc violations found"
    exit 1
  fi
fi

echo "✅ Documentation Comment checks passed"
```

---

### 3.7.7 コメント品質基準（言語非依存）

#### **コメントすべき内容** ✅

1. **Why（なぜ）**: 実装の理由・意図
2. **ビジネスロジック**: 業務ルール・制約
3. **複雑なアルゴリズム**: 処理手順の説明
4. **セキュリティ考慮**: 脆弱性対策の理由
5. **パフォーマンス最適化**: 最適化の背景
6. **将来の改善点**: TODO/FIXME

#### **コメント不要な内容** ❌

1. **What（何を）**: コードを読めば分かること
2. **冗長な説明**: 変数名と同じ内容の繰り返し
3. **コミット履歴**: Gitで追跡できる情報
4. **古いコード**: コメントアウトされたコード

---

### 3.7.8 よくあるミスと対策（言語共通）

#### ❌ ミス1: 実装後にまとめてコメントを書く

**問題**:
- 実装の意図を忘れる
- 一括作業で時間がかかる
- 品質が低下する

**対策**:
✅ 関数定義 → コメント記述 → 実装の順で進める

---

#### ❌ ミス2: "What"だけを書いて"Why"を書かない

**悪い例（TypeScript）**:
```typescript
/**
 * ユーザーを取得する
 * @param id - ID
 * @returns ユーザー
 */
```

**良い例（TypeScript）**:
```typescript
/**
 * ユーザーをIDで取得する
 * 
 * キャッシュ機構により頻繁なアクセスでもパフォーマンスを維持。
 * 存在しない場合はnullを返し、エラーをスローしない設計。
 * 
 * @param id - ユーザーの一意識別子（UUID v4形式）
 * @returns ユーザーオブジェクト、または見つからない場合はnull
 */
```

**悪い例（Python）**:
```python
def get_user(id: str) -> User:
    """ユーザーを取得する"""
    pass
```

**良い例（Python）**:
```python
def get_user(user_id: str) -> Optional[User]:
    """ユーザーをIDで取得する
    
    キャッシュ機構により頻繁なアクセスでもパフォーマンスを維持。
    存在しない場合はNoneを返し、例外をスローしない設計。
    
    Args:
        user_id: ユーザーの一意識別子（UUID v4形式）
    
    Returns:
        ユーザーオブジェクト、または見つからない場合はNone
    
    Note:
        Redis経由でキャッシュ（TTL: 5分）
    """
    pass
```

---

#### ❌ ミス3: 複雑なロジックにインラインコメントがない

**悪い例**:
```python
result = [x for x in data if x.status == 'active' and x.value > 100]
total = sum(x.value for x in result)
```

**良い例**:
```python
# アクティブかつ閾値を超えるデータのみを抽出
# ビジネス要件: 100以下は集計対象外
active_data = [x for x in data if x.status == 'active' and x.value > 100]

# 合計値を計算（月次レポート表示用）
total = sum(x.value for x in active_data)
```

---

### 3.7.9 参照ドキュメント（言語別）

#### TypeScript/JavaScript
- 🔴 必須: `/01-coding-standards/typescript-javascript-standards.md` - セクションX
- 🔴 必須: `/08-templates/code-templates/typescript/`

#### Python
- 🔴 必須: `/01-coding-standards/python-standards.md` - セクションX
- 🔴 必須: `/08-templates/code-templates/python/`

#### Java
- 🔴 必須: `/01-coding-standards/java-standards.md` - セクションX
- 🔴 必須: `/08-templates/code-templates/java/`

#### 言語非依存
- 🔴 必須: `/03-development-process/documentation-standards.md` - セクション7
- 🟡 推奨: `/09-reference/best-practices.md` - ドキュメントコメントセクション

---
