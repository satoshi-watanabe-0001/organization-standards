# TypeScript/JavaScript コーディング規約
## TypeScript/JavaScript Coding Standards

**最終更新日**: 2024-10-09  
**バージョン**: 1.0.0  
**対象**: TypeScript 5.0+, JavaScript ES2022+  
**適用範囲**: フロントエンド（React/Next.js）・バックエンド（Node.js/NestJS）

## 目的

このドキュメントは、TypeScript/JavaScriptプロジェクトにおける具体的なコーディング規約を定義し、フロントエンド・バックエンド両方の開発で一貫した高品質なコードを実現します。共通原則については`00-general-principles.md`を参照してください。

---

## 1. 基本設定・ツール設定

### 1.1 推奨ツールチェーン

#### **必須ツール**
```json
{
  "typescript": "^5.0.0",
  "eslint": "^8.50.0",
  "@typescript-eslint/eslint-plugin": "^6.0.0",
  "@typescript-eslint/parser": "^6.0.0",
  "prettier": "^3.0.0",
  "husky": "^8.0.0",
  "lint-staged": "^14.0.0"
}
```

#### **ESLint設定（.eslintrc.json）**
```json
{
  "extends": [
    "eslint:recommended",
    "@typescript-eslint/recommended",
    "@typescript-eslint/recommended-requiring-type-checking",
    "prettier"
  ],
  "parser": "@typescript-eslint/parser",
  "parserOptions": {
    "ecmaVersion": 2022,
    "sourceType": "module",
    "project": "./tsconfig.json"
  },
  "plugins": ["@typescript-eslint"],
  "rules": {
    "@typescript-eslint/no-unused-vars": ["error", { "argsIgnorePattern": "^_" }],
    "@typescript-eslint/explicit-function-return-type": "warn",
    "@typescript-eslint/no-explicit-any": "error",
    "@typescript-eslint/prefer-nullish-coalescing": "error",
    "@typescript-eslint/prefer-optional-chain": "error",
    "prefer-const": "error",
    "no-var": "error",
    "eol-last": "error",
    "comma-dangle": ["error", "es5"]
  }
}
```

#### **Prettier設定（.prettierrc）**
```json
{
  "semi": true,
  "trailingComma": "es5",
  "singleQuote": true,
  "printWidth": 80,
  "tabWidth": 2,
  "useTabs": false,
  "bracketSpacing": true,
  "arrowParens": "avoid"
}
```

#### **TypeScript設定（tsconfig.json）**
```json
{
  "compilerOptions": {
    "target": "ES2022",
    "lib": ["ES2022", "DOM", "DOM.Iterable"],
    "allowJs": true,
    "skipLibCheck": true,
    "esModuleInterop": true,
    "allowSyntheticDefaultImports": true,
    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true,
    "strictFunctionTypes": true,
    "noImplicitReturns": true,
    "noFallthroughCasesInSwitch": true,
    "noUncheckedIndexedAccess": true,
    "forceConsistentCasingInFileNames": true,
    "moduleResolution": "node",
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "preserve",
    "incremental": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["./src/*"]
    }
  },
  "include": ["next-env.d.ts", "**/*.ts", "**/*.tsx"],
  "exclude": ["node_modules"]
}
```

**Devin指示**: プロジェクト初期化時は上記設定を必ず適用し、strictモードを有効にせよ

### 1.2 Git設定

#### **Husky + lint-staged設定**
```json
// package.json
{
  "lint-staged": {
    "*.{ts,tsx,js,jsx}": [
      "eslint --fix",
      "prettier --write"
    ],
    "*.{json,css,md}": [
      "prettier --write"
    ]
  }
}
```

#### **コミットメッセージ規約**
```
<type>(<scope>): <subject>

types: feat, fix, docs, style, refactor, test, chore
例: feat(auth): add JWT token validation
例: fix(api): resolve user profile update issue
```

**Devin指示**: コミット前に必ずlint・format・type-checkを実行せよ

---

## 2. 言語仕様・構文規約

### 2.1 変数宣言

#### **基本原則**
```typescript
// ✅ Good: const優先、letは再代入が必要な場合のみ
const userName = 'John Doe';
const userList = ['Alice', 'Bob'];
let counter = 0; // 再代入が必要な場合のみ

// ❌ Bad: varは使用禁止
var globalVar = 'never use var';
```

#### **分割代入の活用**
```typescript
// ✅ Good: オブジェクト分割代入
const { name, email, age } = user;
const { data, loading, error } = useApiCall();

// ✅ Good: 配列分割代入
const [first, second, ...rest] = items;
const [isOpen, setIsOpen] = useState(false);

// ✅ Good: デフォルト値の指定
const { name = 'Anonymous', age = 0 } = user;
```

#### **テンプレートリテラル**
```typescript
// ✅ Good: テンプレートリテラル使用
const message = `Hello, ${userName}! You have ${count} messages.`;
const apiUrl = `${BASE_URL}/users/${userId}/profile`;

// ❌ Bad: 文字列連結
const message = 'Hello, ' + userName + '! You have ' + count + ' messages.';
```

**Devin指示**: const優先、分割代入を積極活用、テンプレートリテラルで文字列構築せよ

### 2.2 関数定義

#### **関数宣言 vs アロー関数**
```typescript
// ✅ Good: 純粋関数・ユーティリティ関数はアロー関数
const calculateTotal = (items: Item[]): number => {
  return items.reduce((sum, item) => sum + item.price, 0);
};

// ✅ Good: 関数式も可能
const processData = async (data: RawData): Promise<ProcessedData> => {
  const validated = await validateData(data);
  return transformData(validated);
};

// ✅ Good: トップレベル関数・複雑な関数は関数宣言
function complexBusinessLogic(
  input: ComplexInput
): Promise<ComplexOutput> {
  // 複雑な処理...
}

// ✅ Good: メソッドは通常の関数記法
class UserService {
  async findById(id: string): Promise<User | null> {
    return this.repository.findOne(id);
  }
}
```

#### **高階関数・関数型プログラミング**
```typescript
// ✅ Good: map, filter, reduce を積極活用
const activeUsers = users.filter(user => user.status === 'active');
const userNames = users.map(user => user.name);
const totalRevenue = orders.reduce((sum, order) => sum + order.total, 0);

// ✅ Good: チェイン処理
const result = data
  .filter(item => item.isValid)
  .map(item => ({ ...item, processed: true }))
  .sort((a, b) => a.createdAt.getTime() - b.createdAt.getTime());

// ❌ Bad: forループの過度な使用
const activeUsers = [];
for (let i = 0; i < users.length; i++) {
  if (users[i].status === 'active') {
    activeUsers.push(users[i]);
  }
}
```

**Devin指示**: アロー関数を基本とし、関数型プログラミングを積極活用せよ

### 2.3 非同期処理

#### **async/await優先**
```typescript
// ✅ Good: async/await使用
const fetchUserProfile = async (userId: string): Promise<UserProfile> => {
  try {
    const user = await userService.findById(userId);
    const profile = await profileService.getProfile(user.id);
    return profile;
  } catch (error) {
    logger.error('Failed to fetch user profile', { userId, error });
    throw new UserProfileFetchError('Failed to fetch profile', error);
  }
};

// ✅ Good: 並列処理
const fetchUserData = async (userId: string) => {
  const [user, posts, followers] = await Promise.all([
    userService.findById(userId),
    postService.getByUserId(userId),
    followerService.getFollowers(userId),
  ]);
  
  return { user, posts, followers };
};

// ❌ Bad: Promiseチェイン（複雑な場合）
const fetchUserProfile = (userId: string) => {
  return userService.findById(userId)
    .then(user => profileService.getProfile(user.id))
    .then(profile => profile)
    .catch(error => {
      logger.error('Failed to fetch user profile', { userId, error });
      throw error;
    });
};
```

#### **エラーハンドリング**
```typescript
// ✅ Good: 詳細なエラーハンドリング
const processPayment = async (paymentData: PaymentData): Promise<PaymentResult> => {
  try {
    const validated = await validatePaymentData(paymentData);
    const result = await paymentGateway.process(validated);
    
    await auditLogger.log('payment_processed', {
      userId: paymentData.userId,
      amount: paymentData.amount,
      transactionId: result.transactionId,
    });
    
    return result;
  } catch (error) {
    if (error instanceof ValidationError) {
      throw new PaymentValidationError(error.message, error);
    }
    if (error instanceof NetworkError) {
      throw new PaymentNetworkError('Payment gateway unavailable', error);
    }
    
    logger.error('Unexpected payment error', {
      userId: paymentData.userId,
      error: error.message,
      stack: error.stack,
    });
    throw new PaymentProcessingError('Payment processing failed', error);
  }
};
```

**Devin指示**: async/awaitを優先し、並列処理はPromise.allを活用、詳細なエラーハンドリングを実装せよ

---

## 3. 命名規則

### 3.1 基本命名パターン

#### **変数・関数名**
```typescript
// ✅ Good: camelCase + 説明的な名前
const userProfileData = getUserProfile();
const isAuthenticated = checkAuthStatus();
const shouldDisplayWelcomeMessage = user.isFirstLogin && !user.hasSeenWelcome;

// ✅ Good: 動詞 + 名詞の組み合わせ
const calculateTotalPrice = (items: Item[]) => { /* ... */ };
const validateEmailAddress = (email: string) => { /* ... */ };
const fetchUserPreferences = async (userId: string) => { /* ... */ };

// ❌ Bad: 略語・省略形
const calc = (items) => { /* ... */ };
const usr = getUser();
const val = validate(data);
```

#### **定数**
```typescript
// ✅ Good: UPPER_SNAKE_CASE
const MAX_RETRY_ATTEMPTS = 3;
const API_BASE_URL = 'https://api.example.com';
const DEFAULT_PAGE_SIZE = 20;

// ✅ Good: オブジェクト定数
const HTTP_STATUS_CODES = {
  OK: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  NOT_FOUND: 404,
  INTERNAL_SERVER_ERROR: 500,
} as const;

const VALIDATION_MESSAGES = {
  EMAIL_REQUIRED: 'Email address is required',
  PASSWORD_TOO_SHORT: 'Password must be at least 8 characters',
  INVALID_EMAIL_FORMAT: 'Please enter a valid email address',
} as const;
```

#### **ブール値変数**
```typescript
// ✅ Good: is, has, can, should 接頭辞
const isLoading = false;
const hasPermission = checkUserPermission(user, 'admin');
const canEdit = user.role === 'admin' || user.id === resource.ownerId;
const shouldShowNotification = !user.hasSeenUpdate && isImportantUpdate;

// ✅ Good: 否定形の避け方
const isVisible = true; // isHidden ではなく
const isEnabled = false; // isDisabled ではなく
const isValid = validateInput(input); // isInvalid ではなく
```

**Devin指示**: camelCase、説明的名前、動詞+名詞パターン、適切な接頭辞を厳密に適用せよ

### 3.2 クラス・型・インターフェース

#### **クラス名**
```typescript
// ✅ Good: PascalCase + 責任を表現
class UserAuthenticationService {
  async authenticate(credentials: LoginCredentials): Promise<AuthResult> { }
}

class DatabaseConnectionManager {
  private connections: Map<string, Connection> = new Map();
}

class PaymentProcessingError extends Error {
  constructor(message: string, public readonly cause?: Error) {
    super(message);
    this.name = 'PaymentProcessingError';
  }
}
```

#### **インターフェース・型定義**
```typescript
// ✅ Good: 接頭辞なし、説明的な名前
interface UserProfile {
  id: string;
  name: string;
  email: string;
  createdAt: Date;
  updatedAt: Date;
}

interface ApiResponse<T> {
  data: T;
  success: boolean;
  message?: string;
  errors?: string[];
}

// ✅ Good: Union Types
type UserRole = 'admin' | 'moderator' | 'user' | 'guest';
type PaymentStatus = 'pending' | 'processing' | 'completed' | 'failed';

// ✅ Good: 関数型
type EventHandler<T> = (event: T) => void;
type AsyncDataFetcher<T, P> = (params: P) => Promise<T>;
```

#### **Enum vs Union Types**
```typescript
// ✅ Good: const assertions を活用
const USER_ROLES = {
  ADMIN: 'admin',
  MODERATOR: 'moderator',
  USER: 'user',
  GUEST: 'guest',
} as const;

type UserRole = typeof USER_ROLES[keyof typeof USER_ROLES];

// ✅ Good: 文字列リテラル型
type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';

// ❌ Avoid: 数値 Enum（特別な理由がない限り）
enum UserRoleEnum {
  ADMIN, // 0
  USER,  // 1
  GUEST  // 2
}
```

**Devin指示**: PascalCase、説明的名前、const assertions活用、Union Typesを優先せよ

---

## 4. 型定義・インターフェース

### 4.1 基本型設計

#### **厳密な型定義**
```typescript
// ✅ Good: 厳密な型定義
interface CreateUserRequest {
  name: string;
  email: string;
  password: string;
  role?: UserRole;
  preferences?: UserPreferences;
}

interface UserPreferences {
  theme: 'light' | 'dark' | 'auto';
  language: 'en' | 'ja' | 'es' | 'fr';
  notifications: {
    email: boolean;
    push: boolean;
    sms: boolean;
  };
}

// ✅ Good: Utility Typesの活用
type UpdateUserRequest = Partial<Pick<CreateUserRequest, 'name' | 'email'>>;
type UserSummary = Pick<User, 'id' | 'name' | 'email' | 'role'>;
type CreateUserResponse = Omit<User, 'password'>;
```

#### **Generic Types**
```typescript
// ✅ Good: 再利用可能なGeneric型
interface ApiResponse<T> {
  data: T;
  success: boolean;
  message?: string;
  timestamp: string;
}

interface PaginatedResponse<T> extends ApiResponse<T[]> {
  pagination: {
    page: number;
    limit: number;
    total: number;
    totalPages: number;
  };
}

// ✅ Good: 制約付きGeneric
interface Repository<T extends { id: string }> {
  findById(id: string): Promise<T | null>;
  save(entity: T): Promise<T>;
  delete(id: string): Promise<void>;
}

// ✅ Good: 条件型
type NonNullable<T> = T extends null | undefined ? never : T;
type ApiEndpoint<T> = T extends 'user' ? '/api/users' : 
                     T extends 'post' ? '/api/posts' : 
                     never;
```

#### **タイプガード**
```typescript
// ✅ Good: カスタムタイプガード
const isUser = (obj: unknown): obj is User => {
  return (
    typeof obj === 'object' &&
    obj !== null &&
    'id' in obj &&
    'name' in obj &&
    'email' in obj &&
    typeof (obj as User).id === 'string' &&
    typeof (obj as User).name === 'string' &&
    typeof (obj as User).email === 'string'
  );
};

// ✅ Good: 使用例
const processUserData = (data: unknown) => {
  if (isUser(data)) {
    // この時点で data は User 型として認識される
    console.log(`Processing user: ${data.name}`);
    return data.email;
  }
  throw new Error('Invalid user data');
};

// ✅ Good: Discriminated Union
interface LoadingState {
  status: 'loading';
}

interface SuccessState {
  status: 'success';
  data: User[];
}

interface ErrorState {
  status: 'error';
  error: string;
}

type AsyncState = LoadingState | SuccessState | ErrorState;
```

**Devin指示**: 厳密な型定義、Generic活用、タイプガード実装、any型は禁止せよ

### 4.2 外部API・ライブラリ型定義

#### **API レスポンス型**
```typescript
// ✅ Good: 具体的なAPI型定義
interface GetUsersApiResponse {
  users: Array<{
    id: string;
    name: string;
    email: string;
    role: UserRole;
    created_at: string; // API は snake_case
    updated_at: string;
  }>;
  total: number;
  page: number;
  per_page: number;
}

// ✅ Good: APIデータ変換
const transformApiUserToUser = (apiUser: GetUsersApiResponse['users'][0]): User => {
  return {
    id: apiUser.id,
    name: apiUser.name,
    email: apiUser.email,
    role: apiUser.role,
    createdAt: new Date(apiUser.created_at), // camelCase + Date変換
    updatedAt: new Date(apiUser.updated_at),
  };
};
```

#### **環境変数型定義**
```typescript
// ✅ Good: 環境変数の型安全性
interface EnvironmentVariables {
  NODE_ENV: 'development' | 'test' | 'production';
  DATABASE_URL: string;
  JWT_SECRET: string;
  API_BASE_URL: string;
  PORT?: string;
}

// ✅ Good: 環境変数バリデーション
const validateEnv = (): EnvironmentVariables => {
  const env = process.env;
  
  if (!env.DATABASE_URL) {
    throw new Error('DATABASE_URL is required');
  }
  if (!env.JWT_SECRET) {
    throw new Error('JWT_SECRET is required');
  }
  
  return {
    NODE_ENV: env.NODE_ENV as EnvironmentVariables['NODE_ENV'] || 'development',
    DATABASE_URL: env.DATABASE_URL,
    JWT_SECRET: env.JWT_SECRET,
    API_BASE_URL: env.API_BASE_URL || 'http://localhost:3000',
    PORT: env.PORT,
  };
};

const config = validateEnv();
```

**Devin指示**: API型定義、変換関数、環境変数型安全性を必ず実装せよ

---

## 5. React固有規約（フロントエンド）

### 5.1 コンポーネント設計

#### **関数コンポーネント・Hooks**
```typescript
// ✅ Good: 関数コンポーネント + TypeScript
interface UserProfileProps {
  userId: string;
  onUserUpdate?: (user: User) => void;
  className?: string;
}

const UserProfile: React.FC<UserProfileProps> = ({ 
  userId, 
  onUserUpdate,
  className 
}) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        setLoading(true);
        const userData = await userService.findById(userId);
        setUser(userData);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load user');
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [userId]);

  const handleUserUpdate = useCallback(async (updatedData: Partial<User>) => {
    if (!user) return;
    
    try {
      const updatedUser = await userService.update(user.id, updatedData);
      setUser(updatedUser);
      onUserUpdate?.(updatedUser);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to update user');
    }
  }, [user, onUserUpdate]);

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage message={error} />;
  if (!user) return <div>User not found</div>;

  return (
    <div className={className}>
      <h2>{user.name}</h2>
      <p>{user.email}</p>
      <UserEditForm user={user} onSubmit={handleUserUpdate} />
    </div>
  );
};

export default UserProfile;
```

#### **カスタムHooks**
```typescript
// ✅ Good: カスタムHooks
interface UseApiStateOptions<T> {
  initialData?: T;
  onSuccess?: (data: T) => void;
  onError?: (error: Error) => void;
}

const useApiState = <T>(
  apiCall: () => Promise<T>,
  dependencies: React.DependencyList = [],
  options: UseApiStateOptions<T> = {}
) => {
  const [data, setData] = useState<T | null>(options.initialData || null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const execute = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const result = await apiCall();
      setData(result);
      options.onSuccess?.(result);
      return result;
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Unknown error');
      setError(error);
      options.onError?.(error);
      throw error;
    } finally {
      setLoading(false);
    }
  }, [apiCall, options]);

  useEffect(() => {
    execute();
  }, dependencies);

  return {
    data,
    loading,
    error,
    refetch: execute,
  };
};

// 使用例
const UserList: React.FC = () => {
  const { data: users, loading, error, refetch } = useApiState(
    () => userService.getAll(),
    [],
    {
      onError: (error) => toast.error(error.message),
    }
  );

  return (
    <div>
      <button onClick={refetch}>Refresh</button>
      {loading && <LoadingSpinner />}
      {error && <ErrorMessage message={error.message} />}
      {users?.map(user => <UserCard key={user.id} user={user} />)}
    </div>
  );
};
```

#### **状態管理**
```typescript
// ✅ Good: Zustand使用例
interface UserStore {
  currentUser: User | null;
  users: User[];
  loading: boolean;
  error: string | null;
  
  // Actions
  setCurrentUser: (user: User | null) => void;
  fetchUsers: () => Promise<void>;
  updateUser: (id: string, updates: Partial<User>) => Promise<void>;
  clearError: () => void;
}

const useUserStore = create<UserStore>((set, get) => ({
  currentUser: null,
  users: [],
  loading: false,
  error: null,

  setCurrentUser: (user) => set({ currentUser: user }),

  fetchUsers: async () => {
    set({ loading: true, error: null });
    try {
      const users = await userService.getAll();
      set({ users, loading: false });
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Failed to fetch users',
        loading: false 
      });
    }
  },

  updateUser: async (id, updates) => {
    try {
      const updatedUser = await userService.update(id, updates);
      const { users, currentUser } = get();
      
      set({
        users: users.map(user => user.id === id ? updatedUser : user),
        currentUser: currentUser?.id === id ? updatedUser : currentUser,
      });
    } catch (error) {
      set({ error: error instanceof Error ? error.message : 'Failed to update user' });
    }
  },

  clearError: () => set({ error: null }),
}));
```

**Devin指示**: 関数コンポーネント、TypeScript props、カスタムHooks、適切な状態管理を実装せよ

### 5.2 Next.js固有パターン

#### **ページコンポーネント**
```typescript
// ✅ Good: Next.js App Router
// app/users/[id]/page.tsx
interface UserPageProps {
  params: { id: string };
  searchParams: { [key: string]: string | string[] | undefined };
}

const UserPage: React.FC<UserPageProps> = async ({ params }) => {
  // Server Component
  const user = await userService.findById(params.id);
  
  if (!user) {
    notFound();
  }

  return (
    <div>
      <h1>{user.name}</h1>
      <UserProfileClient user={user} />
    </div>
  );
};

export default UserPage;

// ✅ Good: Client Component
'use client';

interface UserProfileClientProps {
  user: User;
}

const UserProfileClient: React.FC<UserProfileClientProps> = ({ user }) => {
  const [isEditing, setIsEditing] = useState(false);
  
  return (
    <div>
      {isEditing ? (
        <UserEditForm 
          user={user} 
          onSave={() => setIsEditing(false)}
          onCancel={() => setIsEditing(false)}
        />
      ) : (
        <UserDisplay 
          user={user} 
          onEdit={() => setIsEditing(true)}
        />
      )}
    </div>
  );
};
```

#### **API Routes**
```typescript
// ✅ Good: API Route Handler
// app/api/users/route.ts
import { NextRequest, NextResponse } from 'next/server';

interface CreateUserRequestBody {
  name: string;
  email: string;
  role?: UserRole;
}

export async function GET(request: NextRequest) {
  try {
    const searchParams = request.nextUrl.searchParams;
    const page = parseInt(searchParams.get('page') || '1');
    const limit = parseInt(searchParams.get('limit') || '10');
    
    const users = await userService.getAll({ page, limit });
    
    return NextResponse.json({
      success: true,
      data: users,
    });
  } catch (error) {
    logger.error('Failed to fetch users', { error });
    return NextResponse.json(
      { success: false, error: 'Failed to fetch users' },
      { status: 500 }
    );
  }
}

export async function POST(request: NextRequest) {
  try {
    const body: CreateUserRequestBody = await request.json();
    
    // バリデーション
    if (!body.name || !body.email) {
      return NextResponse.json(
        { success: false, error: 'Name and email are required' },
        { status: 400 }
      );
    }
    
    const user = await userService.create(body);
    
    return NextResponse.json({
      success: true,
      data: user,
    }, { status: 201 });
  } catch (error) {
    if (error instanceof ValidationError) {
      return NextResponse.json(
        { success: false, error: error.message },
        { status: 400 }
      );
    }
    
    logger.error('Failed to create user', { error });
    return NextResponse.json(
      { success: false, error: 'Failed to create user' },
      { status: 500 }
    );
  }
}
```

**Devin指示**: App Router、Server/Client Component適切分離、型安全なAPI Routesを実装せよ

---

## 6. Node.js固有規約（バックエンド）

### 6.1 NestJS推奨パターン

#### **Controller設計**
```typescript
// ✅ Good: NestJS Controller
import { 
  Controller, 
  Get, 
  Post, 
  Put, 
  Delete, 
  Body, 
  Param, 
  Query,
  UseGuards,
  HttpStatus,
  HttpException
} from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse } from '@nestjs/swagger';

@ApiTags('users')
@Controller('users')
@UseGuards(JwtAuthGuard)
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @Get()
  @ApiOperation({ summary: 'Get all users' })
  @ApiResponse({ status: 200, description: 'Users retrieved successfully' })
  async findAll(
    @Query() query: GetUsersQueryDto
  ): Promise<PaginatedResponse<UserDto>> {
    try {
      const result = await this.usersService.findAll(query);
      return {
        success: true,
        data: result.users.map(user => this.mapToDto(user)),
        pagination: result.pagination,
      };
    } catch (error) {
      throw new HttpException(
        'Failed to retrieve users',
        HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }

  @Post()
  @ApiOperation({ summary: 'Create a new user' })
  @ApiResponse({ status: 201, description: 'User created successfully' })
  async create(@Body() createUserDto: CreateUserDto): Promise<ApiResponse<UserDto>> {
    try {
      const user = await this.usersService.create(createUserDto);
      return {
        success: true,
        data: this.mapToDto(user),
        message: 'User created successfully',
      };
    } catch (error) {
      if (error instanceof ValidationError) {
        throw new HttpException(error.message, HttpStatus.BAD_REQUEST);
      }
      throw new HttpException(
        'Failed to create user',
        HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }

  @Put(':id')
  @ApiOperation({ summary: 'Update user' })
  async update(
    @Param('id') id: string,
    @Body() updateUserDto: UpdateUserDto
  ): Promise<ApiResponse<UserDto>> {
    const user = await this.usersService.update(id, updateUserDto);
    if (!user) {
      throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    }
    
    return {
      success: true,
      data: this.mapToDto(user),
      message: 'User updated successfully',
    };
  }

  private mapToDto(user: User): UserDto {
    return {
      id: user.id,
      name: user.name,
      email: user.email,
      role: user.role,
      createdAt: user.createdAt.toISOString(),
      updatedAt: user.updatedAt.toISOString(),
    };
  }
}
```

#### **Service設計**
```typescript
// ✅ Good: NestJS Service
import { Injectable, Logger } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';

@Injectable()
export class UsersService {
  private readonly logger = new Logger(UsersService.name);

  constructor(
    @InjectRepository(User)
    private usersRepository: Repository<User>,
    private emailService: EmailService,
    private auditService: AuditService
  ) {}

  async findAll(query: GetUsersQueryDto): Promise<{
    users: User[];
    pagination: PaginationInfo;
  }> {
    const { page = 1, limit = 10, role, search } = query;
    const skip = (page - 1) * limit;

    const queryBuilder = this.usersRepository
      .createQueryBuilder('user')
      .take(limit)
      .skip(skip);

    if (role) {
      queryBuilder.andWhere('user.role = :role', { role });
    }

    if (search) {
      queryBuilder.andWhere(
        '(user.name ILIKE :search OR user.email ILIKE :search)',
        { search: `%${search}%` }
      );
    }

    const [users, total] = await queryBuilder.getManyAndCount();

    return {
      users,
      pagination: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit),
      },
    };
  }

  async create(createUserDto: CreateUserDto): Promise<User> {
    const existingUser = await this.usersRepository.findOne({
      where: { email: createUserDto.email },
    });

    if (existingUser) {
      throw new ConflictException('User with this email already exists');
    }

    const user = this.usersRepository.create({
      ...createUserDto,
      id: generateId(),
      createdAt: new Date(),
      updatedAt: new Date(),
    });

    const savedUser = await this.usersRepository.save(user);

    // 非同期処理（ウェルカムメール送信）
    this.emailService.sendWelcomeEmail(savedUser.email, savedUser.name)
      .catch(error => {
        this.logger.error('Failed to send welcome email', {
          userId: savedUser.id,
          email: savedUser.email,
          error: error.message,
        });
      });

    // 監査ログ
    await this.auditService.log('user_created', {
      userId: savedUser.id,
      email: savedUser.email,
      createdBy: 'system', // 実際は認証情報から取得
    });

    this.logger.log(`User created: ${savedUser.id}`);
    return savedUser;
  }

  async update(id: string, updateUserDto: UpdateUserDto): Promise<User | null> {
    const user = await this.usersRepository.findOne({ where: { id } });
    if (!user) {
      return null;
    }

    const updatedUser = await this.usersRepository.save({
      ...user,
      ...updateUserDto,
      updatedAt: new Date(),
    });

    await this.auditService.log('user_updated', {
      userId: id,
      changes: updateUserDto,
      updatedBy: 'system', // 実際は認証情報から取得
    });

    this.logger.log(`User updated: ${id}`);
    return updatedUser;
  }
}
```

#### **DTO・バリデーション**
```typescript
// ✅ Good: DTO with validation
import { 
  IsString, 
  IsEmail, 
  IsOptional, 
  IsEnum, 
  MinLength,
  MaxLength,
  IsNumber,
  Min,
  Max
} from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class CreateUserDto {
  @ApiProperty({ description: 'User name', example: 'John Doe' })
  @IsString()
  @MinLength(2)
  @MaxLength(100)
  name: string;

  @ApiProperty({ description: 'User email', example: 'john@example.com' })
  @IsEmail()
  email: string;

  @ApiPropertyOptional({ description: 'User role', enum: UserRole })
  @IsOptional()
  @IsEnum(UserRole)
  role?: UserRole;
}

export class UpdateUserDto {
  @ApiPropertyOptional()
  @IsOptional()
  @IsString()
  @MinLength(2)
  @MaxLength(100)
  name?: string;

  @ApiPropertyOptional()
  @IsOptional()
  @IsEmail()
  email?: string;
}

export class GetUsersQueryDto {
  @ApiPropertyOptional({ description: 'Page number', example: 1 })
  @IsOptional()
  @IsNumber()
  @Min(1)
  page?: number;

  @ApiPropertyOptional({ description: 'Items per page', example: 10 })
  @IsOptional()
  @IsNumber()
  @Min(1)
  @Max(100)
  limit?: number;

  @ApiPropertyOptional({ description: 'Filter by role', enum: UserRole })
  @IsOptional()
  @IsEnum(UserRole)
  role?: UserRole;

  @ApiPropertyOptional({ description: 'Search term' })
  @IsOptional()
  @IsString()
  @MaxLength(100)
  search?: string;
}

export class UserDto {
  @ApiProperty()
  id: string;

  @ApiProperty()
  name: string;

  @ApiProperty()
  email: string;

  @ApiProperty({ enum: UserRole })
  role: UserRole;

  @ApiProperty()
  createdAt: string;

  @ApiProperty()
  updatedAt: string;
}
```

**Devin指示**: NestJS装飾子、依存性注入、DTO/バリデーション、Swagger文書化を必ず実装せよ

---

## 7. テスト規約

### 7.1 単体テスト（Jest）

#### **テスト構造・命名**
```typescript
// ✅ Good: テストファイル構造
// src/services/__tests__/user.service.test.ts
import { Test, TestingModule } from '@nestjs/testing';
import { getRepositoryToken } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { UsersService } from '../users.service';
import { User } from '../entities/user.entity';
import { CreateUserDto } from '../dto/create-user.dto';

describe('UsersService', () => {
  let service: UsersService;
  let repository: Repository<User>;
  let emailService: EmailService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        UsersService,
        {
          provide: getRepositoryToken(User),
          useValue: {
            find: jest.fn(),
            findOne: jest.fn(),
            create: jest.fn(),
            save: jest.fn(),
            createQueryBuilder: jest.fn(),
          },
        },
        {
          provide: EmailService,
          useValue: {
            sendWelcomeEmail: jest.fn(),
          },
        },
        {
          provide: AuditService,
          useValue: {
            log: jest.fn(),
          },
        },
      ],
    }).compile();

    service = module.get<UsersService>(UsersService);
    repository = module.get<Repository<User>>(getRepositoryToken(User));
    emailService = module.get<EmailService>(EmailService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('create', () => {
    const createUserDto: CreateUserDto = {
      name: 'John Doe',
      email: 'john@example.com',
      role: UserRole.USER,
    };

    it('should create a user successfully', async () => {
      // Arrange
      const mockUser: User = {
        id: 'user-123',
        ...createUserDto,
        createdAt: new Date(),
        updatedAt: new Date(),
      };

      jest.spyOn(repository, 'findOne').mockResolvedValue(null);
      jest.spyOn(repository, 'create').mockReturnValue(mockUser);
      jest.spyOn(repository, 'save').mockResolvedValue(mockUser);
      jest.spyOn(emailService, 'sendWelcomeEmail').mockResolvedValue(undefined);

      // Act
      const result = await service.create(createUserDto);

      // Assert
      expect(result).toEqual(mockUser);
      expect(repository.findOne).toHaveBeenCalledWith({
        where: { email: createUserDto.email },
      });
      expect(repository.create).toHaveBeenCalledWith(
        expect.objectContaining({
          name: createUserDto.name,
          email: createUserDto.email,
          role: createUserDto.role,
        })
      );
      expect(repository.save).toHaveBeenCalledWith(mockUser);
      expect(emailService.sendWelcomeEmail).toHaveBeenCalledWith(
        mockUser.email,
        mockUser.name
      );
    });

    it('should throw ConflictException when user already exists', async () => {
      // Arrange
      const existingUser: User = {
        id: 'existing-user',
        ...createUserDto,
        createdAt: new Date(),
        updatedAt: new Date(),
      };

      jest.spyOn(repository, 'findOne').mockResolvedValue(existingUser);

      // Act & Assert
      await expect(service.create(createUserDto)).rejects.toThrow(
        ConflictException
      );
      expect(repository.findOne).toHaveBeenCalledWith({
        where: { email: createUserDto.email },
      });
      expect(repository.create).not.toHaveBeenCalled();
      expect(repository.save).not.toHaveBeenCalled();
    });

    it('should handle email service failure gracefully', async () => {
      // Arrange
      const mockUser: User = {
        id: 'user-123',
        ...createUserDto,
        createdAt: new Date(),
        updatedAt: new Date(),
      };

      jest.spyOn(repository, 'findOne').mockResolvedValue(null);
      jest.spyOn(repository, 'create').mockReturnValue(mockUser);
      jest.spyOn(repository, 'save').mockResolvedValue(mockUser);
      jest.spyOn(emailService, 'sendWelcomeEmail').mockRejectedValue(
        new Error('Email service unavailable')
      );

      // Act
      const result = await service.create(createUserDto);

      // Assert
      expect(result).toEqual(mockUser);
      // メール送信エラーはサービス作成を阻害しない
    });
  });

  describe('findAll', () => {
    it('should return paginated users', async () => {
      // Arrange
      const mockUsers: User[] = [
        { id: '1', name: 'User 1', email: 'user1@example.com', role: UserRole.USER, createdAt: new Date(), updatedAt: new Date() },
        { id: '2', name: 'User 2', email: 'user2@example.com', role: UserRole.USER, createdAt: new Date(), updatedAt: new Date() },
      ];

      const queryBuilder = {
        take: jest.fn().mockReturnThis(),
        skip: jest.fn().mockReturnThis(),
        andWhere: jest.fn().mockReturnThis(),
        getManyAndCount: jest.fn().mockResolvedValue([mockUsers, 2]),
      };

      jest.spyOn(repository, 'createQueryBuilder').mockReturnValue(queryBuilder as any);

      const query = { page: 1, limit: 10 };

      // Act
      const result = await service.findAll(query);

      // Assert
      expect(result.users).toEqual(mockUsers);
      expect(result.pagination).toEqual({
        page: 1,
        limit: 10,
        total: 2,
        totalPages: 1,
      });
      expect(queryBuilder.take).toHaveBeenCalledWith(10);
      expect(queryBuilder.skip).toHaveBeenCalledWith(0);
    });
  });
});
```

#### **React コンポーネントテスト**
```typescript
// ✅ Good: React Testing Library
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { UserProfile } from '../UserProfile';
import { userService } from '../../services/user.service';

// Mock external dependencies
jest.mock('../../services/user.service');
const mockUserService = userService as jest.Mocked<typeof userService>;

describe('UserProfile', () => {
  const mockUser: User = {
    id: 'user-123',
    name: 'John Doe',
    email: 'john@example.com',
    role: UserRole.USER,
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should display user information when loaded', async () => {
    // Arrange
    mockUserService.findById.mockResolvedValue(mockUser);

    // Act
    render(<UserProfile userId="user-123" />);

    // Assert
    expect(screen.getByText('Loading...')).toBeInTheDocument();
    
    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.getByText('john@example.com')).toBeInTheDocument();
    });

    expect(mockUserService.findById).toHaveBeenCalledWith('user-123');
  });

  it('should display error message when user fetch fails', async () => {
    // Arrange
    mockUserService.findById.mockRejectedValue(new Error('User not found'));

    // Act
    render(<UserProfile userId="user-123" />);

    // Assert
    await waitFor(() => {
      expect(screen.getByText('User not found')).toBeInTheDocument();
    });
  });

  it('should handle user update', async () => {
    // Arrange
    const onUserUpdate = jest.fn();
    const updatedUser = { ...mockUser, name: 'Jane Doe' };
    
    mockUserService.findById.mockResolvedValue(mockUser);
    mockUserService.update.mockResolvedValue(updatedUser);

    // Act
    render(<UserProfile userId="user-123" onUserUpdate={onUserUpdate} />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });

    const editButton = screen.getByRole('button', { name: /edit/i });
    await userEvent.click(editButton);

    const nameInput = screen.getByLabelText(/name/i);
    await userEvent.clear(nameInput);
    await userEvent.type(nameInput, 'Jane Doe');

    const saveButton = screen.getByRole('button', { name: /save/i });
    await userEvent.click(saveButton);

    // Assert
    await waitFor(() => {
      expect(mockUserService.update).toHaveBeenCalledWith('user-123', {
        name: 'Jane Doe',
      });
      expect(onUserUpdate).toHaveBeenCalledWith(updatedUser);
    });
  });
});
```

**Devin指示**: AAA パターン、適切なモック、非同期処理テスト、ユーザーインタラクションテストを実装せよ

---

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

**注意**: この規約は00-general-principles.mdの共通原則に準拠しています。矛盾する場合は共通原則を優先してください。