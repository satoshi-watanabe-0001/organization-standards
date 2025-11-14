# TypeScript/JavaScript コーディング規約

## 8. パフォーマンス・最適化

### 8.1 フロントエンド最適化

#### **React最適化**
```typescript
// ✅ Good: React.memo + useMemo + useCallback
import React, { memo, useMemo, useCallback } from 'react';

interface UserListProps {
  users: User[];
  onUserSelect: (user: User) => void;
  searchTerm: string;
}

const UserList = memo<UserListProps>(({ users, onUserSelect, searchTerm }) => {
  // 重い計算はuseMemoでメモ化
  const filteredUsers = useMemo(() => {
    if (!searchTerm) return users;
    
    return users.filter(user => 
      user.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.email.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }, [users, searchTerm]);

  const sortedUsers = useMemo(() => {
    return [...filteredUsers].sort((a, b) => a.name.localeCompare(b.name));
  }, [filteredUsers]);

  // イベントハンドラーはuseCallbackでメモ化
  const handleUserClick = useCallback((user: User) => {
    onUserSelect(user);
  }, [onUserSelect]);

  return (
    <div>
      {sortedUsers.map(user => (
        <UserCard
          key={user.id}
          user={user}
          onClick={handleUserClick}
        />
      ))}
    </div>
  );
});

UserList.displayName = 'UserList';

// ✅ Good: 子コンポーネントもメモ化
interface UserCardProps {
  user: User;
  onClick: (user: User) => void;
}

const UserCard = memo<UserCardProps>(({ user, onClick }) => {
  const handleClick = useCallback(() => {
    onClick(user);
  }, [user, onClick]);

  return (
    <div onClick={handleClick} className="user-card">
      <h3>{user.name}</h3>
      <p>{user.email}</p>
    </div>
  );
});
```

#### **バンドル最適化**
```typescript
// ✅ Good: 動的インポート
const LazyUserManagement = lazy(() => import('./UserManagement'));
const LazyReports = lazy(() => import('./Reports'));

const App: React.FC = () => {
  return (
    <Router>
      <Suspense fallback={<LoadingSpinner />}>
        <Routes>
          <Route path="/users" element={<LazyUserManagement />} />
          <Route path="/reports" element={<LazyReports />} />
        </Routes>
      </Suspense>
    </Router>
  );
};

// ✅ Good: 条件付きインポート
const AdminPanel = lazy(() => 
  import('./AdminPanel').then(module => ({
    default: module.AdminPanel
  }))
);

// ✅ Good: ライブラリの部分インポート
import { debounce } from 'lodash-es'; // 全体インポートを避ける
import debounce from 'lodash/debounce'; // またはこの形式
```

**Devin指示**: React.memo、useMemo、useCallback適切使用、動的インポートでバンドル最適化せよ

### 8.2 バックエンド最適化

#### **データベース最適化**
```typescript
// ✅ Good: クエリ最適化
@Injectable()
export class OptimizedUsersService {
  async getUsersWithPosts(query: GetUsersQuery): Promise<UserWithPosts[]> {
    // JOINを使ってN+1問題を回避
    const queryBuilder = this.usersRepository
      .createQueryBuilder('user')
      .leftJoinAndSelect('user.posts', 'post')
      .leftJoinAndSelect('post.comments', 'comment')
      .select([
        'user.id',
        'user.name',
        'user.email',
        'post.id',
        'post.title',
        'post.createdAt',
        'comment.id',
        'comment.content'
      ])
      .where('user.status = :status', { status: 'active' })
      .orderBy('user.createdAt', 'DESC')
      .addOrderBy('post.createdAt', 'DESC');

    if (query.role) {
      queryBuilder.andWhere('user.role = :role', { role: query.role });
    }

    return queryBuilder.getMany();
  }

  // ✅ Good: バッチ処理
  async updateUsersInBatch(updates: Array<{ id: string; data: Partial<User> }>) {
    const chunkSize = 100;
    const chunks = this.chunkArray(updates, chunkSize);
    
    for (const chunk of chunks) {
      await this.dataSource.transaction(async manager => {
        const updatePromises = chunk.map(({ id, data }) =>
          manager.update(User, id, { ...data, updatedAt: new Date() })
        );
        await Promise.all(updatePromises);
      });
    }
  }

  private chunkArray<T>(array: T[], size: number): T[][] {
    const chunks: T[][] = [];
    for (let i = 0; i < array.length; i += size) {
      chunks.push(array.slice(i, i + size));
    }
    return chunks;
  }
}
```

#### **キャッシング戦略**
```typescript
// ✅ Good: Redis キャッシング
@Injectable()
export class CachedUsersService {
  constructor(
    private usersService: UsersService,
    private cacheManager: Cache
  ) {}

  @Cacheable({ ttl: 300 }) // 5分キャッシュ
  async findById(id: string): Promise<User | null> {
    return this.usersService.findById(id);
  }

  @CacheEvict({ key: 'users:*' })
  async update(id: string, updateData: UpdateUserDto): Promise<User> {
    const user = await this.usersService.update(id, updateData);
    
    // 個別キャッシュも削除
    await this.cacheManager.del(`user:${id}`);
    
    return user;
  }

  async findWithCache(id: string): Promise<User | null> {
    const cacheKey = `user:${id}`;
    
    // キャッシュから取得を試行
    const cached = await this.cacheManager.get<User>(cacheKey);
    if (cached) {
      return cached;
    }

    // キャッシュにない場合はDBから取得
    const user = await this.usersService.findById(id);
    if (user) {
      await this.cacheManager.set(cacheKey, user, 300); // 5分キャッシュ
    }

    return user;
  }
}
```

#### **非同期処理・Queue**
```typescript
// ✅ Good: Queue処理
import { Queue } from 'bull';
import { InjectQueue } from '@nestjs/bull';

@Injectable()
export class UserService {
  constructor(
    @InjectQueue('user-processing') private userQueue: Queue,
    @InjectQueue('email') private emailQueue: Queue
  ) {}

  async createUser(createUserDto: CreateUserDto): Promise<User> {
    const user = await this.usersRepository.save(createUserDto);

    // 重い処理は非同期で実行
    await this.userQueue.add('process-new-user', {
      userId: user.id,
      userData: user,
    });

    // メール送信も非同期
    await this.emailQueue.add('welcome-email', {
      email: user.email,
      name: user.name,
    }, {
      delay: 5000, // 5秒後に実行
      attempts: 3, // 3回リトライ
    });

    return user;
  }
}

// Queue Processor
@Processor('user-processing')
export class UserProcessor {
  private readonly logger = new Logger(UserProcessor.name);

  @Process('process-new-user')
  async processNewUser(job: Job<{ userId: string; userData: User }>) {
    const { userId, userData } = job.data;
    
    try {
      // 重い処理（画像処理、外部API呼び出し等）
      await this.generateUserAvatar(userData);
      await this.syncWithExternalSystem(userData);
      await this.calculateUserScore(userData);
      
      this.logger.log(`User processing completed: ${userId}`);
    } catch (error) {
      this.logger.error(`User processing failed: ${userId}`, error);
      throw error; // リトライを発動
    }
  }
}
```

**Devin指示**: N+1問題回避、適切なキャッシング、重い処理の非同期化を必ず実装せよ

---

## 9. Devin向け実行ガイドライン

### 9.1 プロジェクト開始時チェックリスト

#### **初期設定確認**
- [ ] TypeScript strict モード有効化
- [ ] ESLint + Prettier 設定適用
- [ ] Husky + lint-staged セットアップ
- [ ] tsconfig.json パス設定（@/* エイリアス）
- [ ] 推奨パッケージインストール確認

#### **ファイル構成作成**
```
src/
├── components/          # 共通コンポーネント
│   ├── ui/             # 基本UIコンポーネント
│   └── features/       # 機能別コンポーネント
├── hooks/              # カスタムHooks
├── services/           # API・ビジネスロジック
├── types/              # 型定義
├── utils/              # ユーティリティ関数
├── stores/             # 状態管理（Zustand）
└── __tests__/          # テストファイル
```

**Devin指示**: 上記チェックリスト完了後に実装開始せよ

### 9.2 コード生成時の必須確認項目

#### **型安全性チェック**
- [ ] `any` 型を使用していないか
- [ ] すべての関数に戻り値型が明示されているか
- [ ] インターフェース・型定義が適切か
- [ ] オプショナルプロパティが適切に設定されているか

#### **エラーハンドリング確認**
- [ ] try-catch で適切にエラーを捕捉しているか
- [ ] エラーログが詳細に記録されているか
- [ ] ユーザーフレンドリーなエラーメッセージを表示しているか
- [ ] エラー型に応じた適切な処理が実装されているか

#### **パフォーマンス確認**
- [ ] 不要な再レンダリングを防いでいるか（React.memo、useMemo、useCallback）
- [ ] 重い処理が非同期化されているか
- [ ] データベースクエリが最適化されているか
- [ ] 適切なキャッシング戦略が実装されているか

**Devin指示**: 各チェック項目を生成コードで必ず確認せよ

### 9.3 コードレビュー・改善指針

#### **コード品質評価基準**
1. **可読性**: 変数名・関数名が意図を明確に表現しているか
2. **保守性**: 機能追加・変更が容易な構造になっているか  
3. **テスタビリティ**: 単体テストが書きやすい設計か
4. **一貫性**: 既存コードと統一されたパターンか
5. **セキュリティ**: 入力値検証・認証認可が適切か

#### **自動改善フロー**
```typescript
// Devin実行例
const improveCodeQuality = async (code: string): Promise<string> => {
  // 1. TypeScript型チェック
  const typeChecked = await addMissingTypes(code);
  
  // 2. エラーハンドリング追加
  const errorHandled = await addErrorHandling(typeChecked);
  
  // 3. パフォーマンス最適化
  const optimized = await optimizePerformance(errorHandled);
  
  // 4. テストコード生成
  const tested = await generateTests(optimized);
  
  return tested;
};
```

#### **継続的改善**
- 生成したコードを定期的に見直し
- 新しいベストプラクティスの採用
- パフォーマンス指標の継続的な改善
- セキュリティ脆弱性の定期的なチェック

**Devin最終指示**: この規約に厳密に従い、高品質で保守性の高いTypeScript/JavaScriptコードを生成せよ。判断に迷った場合は00-general-principles.mdの原則に立ち戻り、セキュリティ・可読性を最優先とせよ。

---

## 改訂履歴

| バージョン | 日付 | 変更内容 | 変更者 |
|-----------|------|----------|--------|
| 1.0.0 | 2024-10-09 | 初版作成 | システム |

---

**注意**: この規約は00-general-principles.mdの共通原則に準拠しています。矛盾する場合は共通原則を優先してください。# TypeScript/JavaScript ドキュメンテーション標準追加セクション

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
