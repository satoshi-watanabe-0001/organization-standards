# Phase 4レビュー・QAガイドへの追加セクション

以下のセクションを `/00-guides/phase-guides/phase-4-review-qa-guide.md` に追加してください。

---

## 4.X ドキュメントコメントレビュー基準（全言語共通） ⭐NEW

### 4.X.1 自動チェック（必須・却下基準）

#### TypeScript/JavaScript
```bash
npm run lint:jsdoc          # JSDocチェック
npm run docs:generate       # TypeDoc生成
```

**却下基準**:
- ❌ JSDoc警告が1件でもある
- ❌ TypeDoc生成が失敗する

---

#### Python
```bash
pylint --enable=missing-docstring src/    # Docstringチェック
pydocstyle src/                           # スタイルチェック
```

**却下基準**:
- ❌ Docstring警告が1件でもある
- ❌ Google Styleに準拠していない

---

#### Java
```bash
mvn checkstyle:check        # Javadocチェック
mvn javadoc:javadoc         # Javadoc生成
```

**却下基準**:
- ❌ Javadoc警告が1件でもある
- ❌ Javadoc生成が失敗する

---

### 4.X.2 手動レビュー基準（言語非依存）

#### 🔴 Level 1: 必須項目（却下基準）

**ファイルレベル**:
- [ ] すべてのファイルにヘッダーコメントがある
- [ ] ファイルの目的が明確に記述されている

**クラス・インターフェースレベル**:
- [ ] すべてのパブリッククラスにコメントがある
- [ ] クラスの責任が明確に記述されている

**関数・メソッドレベル**:
- [ ] すべてのパブリック関数にコメントがある
- [ ] パラメータ、戻り値、例外が記述されている

---

#### 🟡 Level 2: 品質項目（改善要求）

**コメントの質**:
- [ ] "Why"（なぜ）が説明されている
- [ ] ビジネスルール・制約が明記されている
- [ ] 複雑なロジックにコメントがある
- [ ] セキュリティ考慮事項が記載されている

---

### 4.X.3 カバレッジチェック（言語別）

#### TypeScript/JavaScript
```bash
npm run docs:coverage
```

#### Python
```bash
interrogate -v src/  # Docstringカバレッジ
```

#### Java
```bash
mvn javadoc:javadoc -Dshow=private
```

**基準**:
- パブリックAPI: **100%** 🔴必須
- 複雑なロジック: **80%以上** 🟡推奨

---

### 4.X.4 レビューコメントテンプレート（言語非依存）

```markdown
## ドキュメントコメント不足（却下）

以下のファイルでドキュメントコメントが不足しています:

**TypeScript**:
- `src/auth/services/auth.service.ts`
  - ❌ Line 45: `login()`に`@throws`が不足
  - ❌ Line 78: `generateTokens()`にJSDocがない

**Python**:
- `src/auth/services/auth_service.py`
  - ❌ Line 23: `authenticate()`にDocstringがない
  - ❌ Line 45: `Raises:`セクションが不足

**Java**:
- `src/auth/services/AuthService.java`
  - ❌ Line 34: `login()`に`@throws`が不足
  - ❌ Line 67: `generateTokens()`にJavadocがない

**修正方法**:
1. 言語別テンプレートを参照: `/08-templates/code-templates/[言語]/`
2. ドキュメントコメントを追加
3. 自動チェックで確認
4. 再提出

**参照ドキュメント**:
- `/00-guides/ai-guides/AI-DOCUMENTATION-COMMENT-CHECKLIST.md`
```

---

### 4.X.5 コメント品質レビューガイドライン

#### ❌ 低品質コメントの例

**TypeScript**:
```typescript
/**
 * ユーザーを取得する
 * @param id - ID
 * @returns ユーザー
 */
```

**問題点**:
- "What"のみで"Why"がない
- パラメータの詳細不足
- 戻り値の詳細不足

---

#### ✅ 高品質コメントの例

**TypeScript**:
```typescript
/**
 * ユーザーをIDで取得する
 * 
 * キャッシュ機構により頻繁なアクセスでもパフォーマンスを維持。
 * 存在しない場合はnullを返し、エラーをスローしない設計（呼び出し側でハンドリング）。
 * 
 * @param id - ユーザーの一意識別子（UUID v4形式）
 * @returns ユーザーオブジェクト、または見つからない場合はnull
 * 
 * @example
 * ```typescript
 * const user = await getUserById('123e4567-e89b-12d3-a456-426614174000');
 * if (user) {
 *   console.log(user.name);
 * }
 * ```
 */
```

**優れている点**:
- ビジネスロジック（キャッシュ）を説明
- 設計判断（エラーをスローしない）の理由
- パラメータの形式を明記
- 戻り値の詳細説明
- 使用例を記載

---

### 4.X.6 レビュー手順

```
[Phase 4開始]
  ↓
1. 自動チェック実行（5分）
   - 言語別Linter実行
   - ドキュメント生成テスト
   - カバレッジ確認
  ↓
2. 自動チェック結果確認
   ├─ NG → 却下（Phase 3に差し戻し）
   └─ OK → 手動レビューへ
  ↓
3. 手動レビュー（30-60分）
   - ファイルヘッダー確認
   - クラスコメント確認
   - 関数コメント確認
   - コメント品質確認（Why重視）
  ↓
4. レビューコメント作成
   - テンプレートを使用
   - 具体的な改善案を提示
  ↓
5. 承認/却下/改善要求の判断
```

---

### 4.X.7 言語別チェックリスト

#### TypeScript/JavaScript チェック項目
- [ ] `@fileoverview`が全ファイルにある
- [ ] すべてのエクスポートクラスに`@class`がある
- [ ] すべてのエクスポート関数に`@param`, `@returns`, `@throws`がある
- [ ] `eslint-plugin-jsdoc`のルールに違反していない
- [ ] TypeDocが正常に生成できる

#### Python チェック項目
- [ ] モジュールDocstringが全ファイルにある
- [ ] すべてのパブリッククラスにDocstringがある
- [ ] すべてのパブリック関数に`Args:`, `Returns:`, `Raises:`がある
- [ ] Google Styleに準拠している
- [ ] pylint, pydocstyleに違反していない

#### Java チェック項目
- [ ] パッケージJavadocが存在する（package-info.java）
- [ ] すべてのパブリッククラスにJavadocがある
- [ ] すべてのパブリックメソッドに`@param`, `@return`, `@throws`がある
- [ ] Checkstyleに違反していない
- [ ] Javadocが正常に生成できる

---

### 4.X.8 却下基準のまとめ

以下のいずれかに該当する場合、Phase 4を却下し、Phase 3に差し戻します:

**自動チェック違反** 🔴:
- [ ] 言語別Linterでエラー・警告が1件でもある
- [ ] ドキュメント生成ツールが失敗する
- [ ] パブリックAPIのカバレッジが100%未満

**手動レビュー違反** 🔴:
- [ ] ファイルヘッダーコメントが不足
- [ ] パブリッククラスにコメントが不足
- [ ] パブリック関数にコメントが不足
- [ ] パラメータ・戻り値・例外の説明が不足

**品質基準未達** 🟡:
- [ ] "Why"の説明が不足（改善要求）
- [ ] ビジネスロジックの説明が不足（改善要求）
- [ ] 複雑なロジックへのコメントが不足（改善要求）

---

### 4.X.9 参照ドキュメント

- 🔴 必須: `/00-guides/ai-guides/AI-DOCUMENTATION-COMMENT-CHECKLIST.md`
- 🔴 必須: `/00-guides/phase-guides/phase-3-implementation-guide.md` - セクション3.7
- 🔴 必須: `/08-templates/code-templates/` - 言語別テンプレート
- 🟡 推奨: `/01-coding-standards/[言語]-standards.md` - セクションX
- 🟡 推奨: `/03-development-process/documentation-standards.md` - セクション7

---
