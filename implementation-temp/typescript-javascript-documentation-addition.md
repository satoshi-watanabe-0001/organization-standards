# TypeScript/JavaScript ドキュメンテーション標準追加セクション

---

## X. ドキュメンテーション標準（Documentation Standards）

### X.1 JSDoc 必須要件

#### **適用範囲**

**Level 1: 必須（品質ゲート）**
- すべてのソースファイルのファイルヘッダー
- すべてのパブリッククラス・インターフェース
- すべてのエクスポートされる関数・メソッド
- すべてのパブリックAPI

**Level 2: 強く推奨**
- 複雑な内部ロジック（循環的複雑度10以上）
- ビジネスルール・制約を反映した実装
- 非自明な実装（パフォーマンス最適化、技術的回避策）

**Level 3: 任意**
- 単純なゲッター/セッター
- 自己説明的なプライベートメソッド

---

### X.2 JSDoc 標準形式

#### **ファイルヘッダー（必須）**

```typescript
/**
 * @fileoverview ユーザー認証サービス
 * 
 * ユーザーのログイン・ログアウト・トークン検証を担当する。
 * JWTベースの認証を実装し、リフレッシュトークンをサポート。
 * 
 * @module services/auth
 */
```

**必須要素**:
- `@fileoverview`: ファイルの目的・責任の説明
- `@module`: モジュール名（推奨）

---

#### **クラス・インターフェース（必須）**

```typescript
/**
 * ユーザー認証サービスクラス
 * 
 * JWTトークンの生成・検証、ユーザーセッション管理を担当。
 * シングルトンパターンで実装され、アプリケーション全体で共有される。
 * 
 * @class
 * @example
 * const authService = AuthService.getInstance();
 * const token = await authService.login(credentials);
 */
export class AuthService {
  // ...
}
```

**必須要素**:
- クラス/インターフェースの目的と責任
- `@class` または `@interface` タグ

**推奨要素**:
- `@example`: 使用例（複雑なAPIの場合）
- 注意事項・制約条件

---

#### **関数・メソッド（パブリックAPIは必須）**

```typescript
/**
 * ユーザーをログインさせ、JWTトークンを発行する
 * 
 * 認証情報を検証し、成功した場合はアクセストークンと
 * リフレッシュトークンのペアを返す。
 * 
 * @param credentials - ユーザーの認証情報
 * @param credentials.email - メールアドレス
 * @param credentials.password - パスワード
 * @returns アクセストークンとリフレッシュトークンを含むオブジェクト
 * @throws {AuthenticationError} 認証情報が不正な場合
 * @throws {NetworkError} 認証サーバーへの接続に失敗した場合
 * 
 * @example
 * const tokens = await login({
 *   email: 'user@example.com',
 *   password: 'securePassword123'
 * });
 * console.log(tokens.accessToken);
 */
async function login(credentials: LoginCredentials): Promise<TokenPair> {
  // 実装...
}
```

**必須要素**:
- 関数の目的・動作の説明
- `@param`: すべてのパラメータの説明
- `@returns`: 戻り値の説明
- `@throws`: 発生する可能性のある例外

**推奨要素**:
- `@example`: 使用例（複雑なAPIの場合）
- `@deprecated`: 非推奨の場合

---

#### **型定義・インターフェース（パブリックは必須）**

```typescript
/**
 * ユーザーログイン認証情報
 * 
 * ログインAPIに渡されるユーザー認証情報を表現する。
 * 
 * @interface
 */
export interface LoginCredentials {
  /** ユーザーのメールアドレス（形式検証済みであること） */
  email: string;
  
  /** ユーザーのパスワード（平文、送信時にハッシュ化される） */
  password: string;
  
  /** オプション: 2要素認証コード */
  twoFactorCode?: string;
}
```

**必須要素**:
- インターフェース/型の目的
- 各プロパティの説明（インラインコメント）

---

#### **定数・設定値（パブリックは必須）**

```typescript
/**
 * JWT トークンの有効期限設定
 * 
 * セキュリティ要件に基づき、短期間のアクセストークンと
 * 長期間のリフレッシュトークンを使い分ける。
 * 
 * @constant
 */
export const TOKEN_EXPIRATION = {
  /** アクセストークン有効期限（15分） */
  ACCESS_TOKEN: 15 * 60 * 1000,
  
  /** リフレッシュトークン有効期限（7日） */
  REFRESH_TOKEN: 7 * 24 * 60 * 60 * 1000,
} as const;
```

---

### X.3 ESLint による自動チェック

#### **推奨 ESLint ルール**

```json
{
  "plugins": ["jsdoc"],
  "rules": {
    // JSDoc 必須化
    "jsdoc/require-jsdoc": [
      "error",
      {
        "require": {
          "FunctionDeclaration": true,
          "MethodDefinition": true,
          "ClassDeclaration": true,
          "ArrowFunctionExpression": false,
          "FunctionExpression": false
        },
        "contexts": [
          "TSInterfaceDeclaration",
          "TSTypeAliasDeclaration",
          "ExportNamedDeclaration"
        ],
        "publicOnly": true
      }
    ],
    
    // JSDoc 構文チェック
    "jsdoc/check-alignment": "error",
    "jsdoc/check-param-names": "error",
    "jsdoc/check-tag-names": "error",
    "jsdoc/check-types": "error",
    "jsdoc/require-param": "error",
    "jsdoc/require-param-description": "error",
    "jsdoc/require-param-type": "error",
    "jsdoc/require-returns": "error",
    "jsdoc/require-returns-description": "error",
    "jsdoc/require-returns-type": "error",
    
    // 説明の品質
    "jsdoc/require-description": [
      "warn",
      {
        "contexts": ["any"]
      }
    ],
    "jsdoc/no-undefined-types": "warn"
  }
}
```

#### **インストール**

```bash
npm install --save-dev eslint-plugin-jsdoc
```

---

### X.4 TypeScript との統合

#### **tsconfig.json 設定**

```json
{
  "compilerOptions": {
    "declaration": true,
    "declarationMap": true,
    "emitDeclarationOnly": false,
    
    // JSDoc から型情報を抽出
    "allowJs": true,
    "checkJs": true,
    "maxNodeModuleJsDepth": 1
  }
}
```

#### **型と JSDoc の併用**

```typescript
/**
 * ユーザーデータを ID で取得する
 * 
 * データベースからユーザー情報を取得し、存在しない場合は null を返す。
 * キャッシュ機構により、頻繁なアクセスでもパフォーマンスを維持。
 * 
 * @param userId - 取得するユーザーの ID
 * @returns ユーザーオブジェクト、または見つからない場合は null
 * 
 * @example
 * const user = await getUserById('user-123');
 * if (user) {
 *   console.log(user.name);
 * }
 */
export async function getUserById(userId: string): Promise<User | null> {
  // 実装...
}
```

**ポイント**:
- TypeScript の型定義は維持
- JSDoc はビジネスロジック・動作の説明に集中
- 型情報の重複は避ける（TypeScript の型で表現できる情報は省略可）

---

### X.5 ドキュメント生成

#### **TypeDoc の使用**

```bash
# インストール
npm install --save-dev typedoc

# ドキュメント生成
npx typedoc --out docs src
```

#### **typedoc.json 設定**

```json
{
  "entryPoints": ["src/index.ts"],
  "out": "docs",
  "exclude": ["**/*.spec.ts", "**/*.test.ts"],
  "excludePrivate": true,
  "excludeProtected": false,
  "excludeExternals": true,
  "readme": "README.md",
  "includeVersion": true,
  "theme": "default"
}
```

---

### X.6 コードレビューチェックリスト

**レビュアーは以下を確認**:

#### **必須項目**
- [ ] ファイルヘッダーが存在するか
- [ ] パブリッククラス・インターフェースにJSDocがあるか
- [ ] エクスポートされる関数すべてにJSDocがあるか
- [ ] `@param`、`@returns`、`@throws` が適切に記載されているか

#### **品質項目**
- [ ] 「何を」だけでなく「なぜ」が説明されているか
- [ ] ビジネスルール・制約が明記されているか
- [ ] 複雑なロジック（複雑度10以上）にコメントがあるか
- [ ] 使用例が必要な複雑なAPIに `@example` があるか

#### **エラー検出**
- [ ] ESLint で jsdoc ルール違反がないか
- [ ] TypeDoc でドキュメント生成エラーがないか

---

### X.7 ベストプラクティス

#### **✅ Good Examples**

```typescript
/**
 * @fileoverview 注文処理サービス
 * 
 * 注文の作成・更新・キャンセル処理を担当する。
 * 在庫管理システムとの連携により、在庫確認・引き当てを実行。
 * 
 * @module services/order
 */

/**
 * 注文を作成し、在庫を引き当てる
 * 
 * トランザクション内で以下を実行:
 * 1. 在庫の可用性チェック
 * 2. 注文レコードの作成
 * 3. 在庫の引き当て
 * 
 * いずれかの処理が失敗した場合、すべてロールバックされる。
 * 
 * @param orderData - 注文データ
 * @returns 作成された注文オブジェクト
 * @throws {InsufficientStockError} 在庫不足の場合
 * @throws {DatabaseError} データベース操作に失敗した場合
 */
export async function createOrder(orderData: OrderInput): Promise<Order> {
  // 実装...
}
```

#### **❌ Bad Examples**

```typescript
// ❌ コメントなし（パブリックAPIは必須）
export function processPayment(data: any) {
  // ...
}

// ❌ 「何を」の繰り返しのみ（「なぜ」がない）
/**
 * ユーザーを取得する
 * @param id - ID
 * @returns ユーザー
 */
export function getUser(id: string): User {
  // ...
}

// ❌ パラメータ・戻り値の説明不足
/**
 * データを処理する
 */
export function processData(data: unknown): unknown {
  // ...
}
```

---

### X.8 まとめ

**必須ルール**:
1. すべてのファイルにファイルヘッダー
2. すべてのパブリッククラス・インターフェースにJSDoc
3. すべてのエクスポート関数に完全なJSDoc（`@param`、`@returns`、`@throws`）
4. ESLint `jsdoc` プラグインで自動チェック

**推奨プラクティス**:
1. 複雑なロジック（複雑度10以上）にインラインコメント
2. ビジネスルール・制約の明記
3. 使用例（`@example`）の提供
4. TypeDoc による定期的なドキュメント生成

**コードレビューで不合格となる条件**:
- パブリックAPIにJSDocがない
- `@param`、`@returns` の記載漏れ
- ESLint jsdoc ルール違反

---

**このセクションを `typescript-javascript-standards.md` の末尾に追加してください。**
