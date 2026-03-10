# TypeScript/JavaScript コーディング規約

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
