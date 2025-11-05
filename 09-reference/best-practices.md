---
version: 1.0.0
last_updated: 2025-10-28
status: active
owner: Engineering Team
category: reference
---

# Best Practices / ベストプラクティス

## 📋 概要

このドキュメントは、組織全体で推奨される開発のベストプラクティスを集約しています。コード品質の向上、一貫性の確保、効率的な開発を目指します。

---

## 📚 目次

1. [コーディングベストプラクティス](#コーディングベストプラクティス)
2. [アーキテクチャベストプラクティス](#アーキテクチャベストプラクティス)
3. [セキュリティベストプラクティス](#セキュリティベストプラクティス)
4. [パフォーマンスベストプラクティス](#パフォーマンスベストプラクティス)
5. [テストベストプラクティス](#テストベストプラクティス)
6. [DevOpsベストプラクティス](#devopsベストプラクティス)
7. [データベースベストプラクティス](#データベースベストプラクティス)
8. [API設計ベストプラクティス](#api設計ベストプラクティス)

---

## コーディングベストプラクティス

### 1. 命名規則

#### 明確で説明的な名前を使用する

**❌ Bad**:
```typescript
const d = new Date();
function calc(a, b) { return a + b; }
const arr = ['item1', 'item2'];
```

**✅ Good**:
```typescript
const currentDate = new Date();
function calculateTotal(price: number, tax: number): number {
  return price + tax;
}
const productNames = ['item1', 'item2'];
```

#### 一貫した命名パターン

- **変数・関数**: camelCase
- **クラス・型**: PascalCase
- **定数**: UPPER_SNAKE_CASE
- **プライベートメンバー**: _prefix (必要に応じて)

```typescript
// 変数・関数
const userName = 'John';
function getUserData() { }

// クラス・型
class UserService { }
interface UserProfile { }

// 定数
const MAX_RETRY_COUNT = 3;
const API_BASE_URL = 'https://api.example.com';
```

---

### 2. 関数設計

#### 単一責任の原則

関数は1つのことだけを行うべき。

**❌ Bad**:
```typescript
function processUserAndSendEmail(user: User) {
  // ユーザーの検証
  if (!user.email) throw new Error('Invalid email');
  
  // データベース更新
  database.update(user);
  
  // メール送信
  emailService.send(user.email, 'Welcome!');
  
  // ログ記録
  logger.info(`User processed: ${user.id}`);
}
```

**✅ Good**:
```typescript
function validateUser(user: User): void {
  if (!user.email) throw new Error('Invalid email');
}

function updateUserInDatabase(user: User): Promise<void> {
  return database.update(user);
}

function sendWelcomeEmail(email: string): Promise<void> {
  return emailService.send(email, 'Welcome!');
}

async function processUser(user: User): Promise<void> {
  validateUser(user);
  await updateUserInDatabase(user);
  await sendWelcomeEmail(user.email);
  logger.info(`User processed: ${user.id}`);
}
```

#### 関数は短く保つ

目安: 20-30行以内。長い関数は分割を検討。

#### 引数は少なく

- 理想: 0-2個
- 最大: 3個
- それ以上: オブジェクトにまとめる

**❌ Bad**:
```typescript
function createUser(
  name: string,
  email: string,
  age: number,
  address: string,
  phone: string
) { }
```

**✅ Good**:
```typescript
interface CreateUserDto {
  name: string;
  email: string;
  age: number;
  address: string;
  phone: string;
}

function createUser(userData: CreateUserDto) { }
```

---

### 3. エラーハンドリング

#### 明示的なエラー処理

**❌ Bad**:
```typescript
function getUser(id: string) {
  const user = database.findById(id);
  return user; // user が null の可能性
}
```

**✅ Good**:
```typescript
function getUser(id: string): User {
  const user = database.findById(id);
  if (!user) {
    throw new UserNotFoundError(`User with id ${id} not found`);
  }
  return user;
}
```

#### カスタムエラークラスの使用

```typescript
class ApplicationError extends Error {
  constructor(
    message: string,
    public code: string,
    public statusCode: number = 500
  ) {
    super(message);
    this.name = this.constructor.name;
  }
}

class UserNotFoundError extends ApplicationError {
  constructor(message: string) {
    super(message, 'USER_NOT_FOUND', 404);
  }
}

class ValidationError extends ApplicationError {
  constructor(message: string) {
    super(message, 'VALIDATION_ERROR', 400);
  }
}
```

#### エラーログの記録

```typescript
try {
  await processPayment(order);
} catch (error) {
  logger.error('Payment processing failed', {
    orderId: order.id,
    error: error.message,
    stack: error.stack,
    timestamp: new Date().toISOString()
  });
  throw error; // 適切に再スロー
}
```

---

### 4. コメントとドキュメント

#### コードは自己文書化すべき

**❌ Bad**:
```typescript
// ユーザーのステータスが1の場合
if (user.status === 1) {
  // アクティブユーザー処理
}
```

**✅ Good**:
```typescript
enum UserStatus {
  Active = 1,
  Inactive = 2,
  Suspended = 3
}

if (user.status === UserStatus.Active) {
  processActiveUser(user);
}
```

#### コメントは「なぜ」を説明

```typescript
// ✅ Good: 理由を説明
// レート制限API対策のため、リクエスト間に500ms待機
await sleep(500);

// ❌ Bad: コードを繰り返すだけ
// 500ms待つ
await sleep(500);
```

#### 複雑なロジックには説明を追加

```typescript
/**
 * ユーザーの信用スコアを計算
 * 
 * アルゴリズム:
 * 1. 取引履歴から基礎スコアを算出 (0-70点)
 * 2. アカウント年数でボーナス加算 (+0-20点)
 * 3. 違反履歴でペナルティ減算 (-0-30点)
 * 
 * @param user - 計算対象のユーザー
 * @returns 信用スコア (0-100)
 */
function calculateCreditScore(user: User): number {
  const baseScore = calculateBaseScore(user.transactions);
  const ageBonus = calculateAgeBonus(user.accountAge);
  const violationPenalty = calculatePenalty(user.violations);
  
  return Math.max(0, Math.min(100, baseScore + ageBonus - violationPenalty));
}
```

---

### 5. DRY (Don't Repeat Yourself)

#### 重複コードを避ける

**❌ Bad**:
```typescript
function formatUserName(user: User): string {
  return `${user.firstName} ${user.lastName}`.trim();
}

function displayUserName(user: User): void {
  const name = `${user.firstName} ${user.lastName}`.trim();
  console.log(name);
}
```

**✅ Good**:
```typescript
function formatUserName(user: User): string {
  return `${user.firstName} ${user.lastName}`.trim();
}

function displayUserName(user: User): void {
  console.log(formatUserName(user));
}
```

---

### 6. KISS (Keep It Simple, Stupid)

#### シンプルさを保つ

**❌ Bad**:
```typescript
const isEligible = user.age >= 18 && user.verified === true && 
  user.status !== 'suspended' && user.status !== 'banned' && 
  (user.subscription === 'premium' || user.subscription === 'enterprise');
```

**✅ Good**:
```typescript
function isUserEligible(user: User): boolean {
  const isAdult = user.age >= 18;
  const isVerified = user.verified;
  const isActiveStatus = !['suspended', 'banned'].includes(user.status);
  const hasPremiumAccess = ['premium', 'enterprise'].includes(user.subscription);
  
  return isAdult && isVerified && isActiveStatus && hasPremiumAccess;
}

const isEligible = isUserEligible(user);
```

---

### 7. 型安全性 (TypeScript)

#### any型を避ける

**❌ Bad**:
```typescript
function processData(data: any) {
  return data.value * 2; // 型エラーが検出されない
}
```

**✅ Good**:
```typescript
interface DataInput {
  value: number;
}

function processData(data: DataInput): number {
  return data.value * 2;
}
```

#### ユーティリティ型の活用

```typescript
interface User {
  id: string;
  name: string;
  email: string;
  password: string;
}

// Partial: すべてのプロパティをオプションに
type UpdateUserDto = Partial<User>;

// Omit: 特定のプロパティを除外
type PublicUser = Omit<User, 'password'>;

// Pick: 特定のプロパティのみ選択
type UserCredentials = Pick<User, 'email' | 'password'>;

// Readonly: すべてのプロパティを読み取り専用に
type ImmutableUser = Readonly<User>;
```

---

## アーキテクチャベストプラクティス

### 1. レイヤードアーキテクチャ

```
┌─────────────────────┐
│  Presentation Layer │ (Controllers, Routes)
├─────────────────────┤
│   Business Layer    │ (Services, Use Cases)
├─────────────────────┤
│  Persistence Layer  │ (Repositories, DAOs)
├─────────────────────┤
│   Database Layer    │ (Database)
└─────────────────────┘
```

#### 各レイヤーの責務を明確に

```typescript
// ❌ Bad: Controller にビジネスロジック
class UserController {
  async createUser(req: Request, res: Response) {
    const { email, password } = req.body;
    
    // バリデーション
    if (!email || !password) {
      return res.status(400).json({ error: 'Invalid input' });
    }
    
    // ビジネスロジック
    const hashedPassword = await bcrypt.hash(password, 10);
    const user = await database.users.create({ email, password: hashedPassword });
    
    // メール送信
    await emailService.sendWelcome(email);
    
    res.json(user);
  }
}

// ✅ Good: 責務を分離
class UserController {
  constructor(private userService: UserService) {}
  
  async createUser(req: Request, res: Response) {
    try {
      const userData = CreateUserDto.validate(req.body);
      const user = await this.userService.createUser(userData);
      res.status(201).json(user);
    } catch (error) {
      handleError(error, res);
    }
  }
}

class UserService {
  constructor(
    private userRepository: UserRepository,
    private emailService: EmailService
  ) {}
  
  async createUser(userData: CreateUserDto): Promise<User> {
    const hashedPassword = await this.hashPassword(userData.password);
    const user = await this.userRepository.create({
      ...userData,
      password: hashedPassword
    });
    await this.emailService.sendWelcome(user.email);
    return user;
  }
}
```

---

### 2. 依存性注入 (DI)

#### インターフェースに依存する

```typescript
// インターフェース定義
interface IUserRepository {
  findById(id: string): Promise<User | null>;
  create(user: CreateUserDto): Promise<User>;
}

interface IEmailService {
  sendWelcome(email: string): Promise<void>;
}

// 実装
class UserService {
  constructor(
    private userRepository: IUserRepository, // 具体的な実装ではなくインターフェースに依存
    private emailService: IEmailService
  ) {}
  
  async createUser(userData: CreateUserDto): Promise<User> {
    const user = await this.userRepository.create(userData);
    await this.emailService.sendWelcome(user.email);
    return user;
  }
}

// DIコンテナでの登録
container.register('UserRepository', PostgresUserRepository);
container.register('EmailService', SendGridEmailService);
container.register('UserService', UserService);
```

---

### 3. SOLID原則の適用

#### Single Responsibility Principle (単一責任の原則)

```typescript
// ❌ Bad: 複数の責任
class UserManager {
  validateUser(user: User) { }
  saveUser(user: User) { }
  sendEmail(user: User) { }
  generateReport(user: User) { }
}

// ✅ Good: 責任を分離
class UserValidator {
  validate(user: User): void { }
}

class UserRepository {
  save(user: User): Promise<User> { }
}

class EmailService {
  send(to: string, subject: string, body: string): Promise<void> { }
}

class ReportGenerator {
  generate(user: User): Report { }
}
```

#### Open/Closed Principle (開放/閉鎖の原則)

```typescript
// ✅ Good: 拡張に開いており、修正に閉じている
interface PaymentMethod {
  process(amount: number): Promise<void>;
}

class CreditCardPayment implements PaymentMethod {
  async process(amount: number): Promise<void> {
    // クレジットカード処理
  }
}

class PayPalPayment implements PaymentMethod {
  async process(amount: number): Promise<void> {
    // PayPal処理
  }
}

class PaymentProcessor {
  constructor(private paymentMethod: PaymentMethod) {}
  
  async processPayment(amount: number): Promise<void> {
    await this.paymentMethod.process(amount);
  }
}
```

---

## セキュリティベストプラクティス

### 1. 入力バリデーション

#### すべての入力を検証

```typescript
import { z } from 'zod';

const CreateUserSchema = z.object({
  email: z.string().email(),
  password: z.string().min(8).regex(/^(?=.*[A-Za-z])(?=.*\d)/),
  age: z.number().min(0).max(150)
});

function createUser(input: unknown) {
  const userData = CreateUserSchema.parse(input); // 検証失敗時は例外
  // ...
}
```

---

### 2. 認証・認可

#### パスワードの安全な保存

```typescript
import bcrypt from 'bcrypt';

const SALT_ROUNDS = 12;

async function hashPassword(password: string): Promise<string> {
  return bcrypt.hash(password, SALT_ROUNDS);
}

async function verifyPassword(password: string, hash: string): Promise<boolean> {
  return bcrypt.compare(password, hash);
}
```

#### JWTの適切な使用

```typescript
import jwt from 'jsonwebtoken';

const ACCESS_TOKEN_EXPIRY = '15m';
const REFRESH_TOKEN_EXPIRY = '7d';

function generateAccessToken(userId: string): string {
  return jwt.sign(
    { userId, type: 'access' },
    process.env.JWT_SECRET!,
    { expiresIn: ACCESS_TOKEN_EXPIRY }
  );
}

function generateRefreshToken(userId: string): string {
  return jwt.sign(
    { userId, type: 'refresh' },
    process.env.JWT_REFRESH_SECRET!,
    { expiresIn: REFRESH_TOKEN_EXPIRY }
  );
}
```

---

### 3. SQLインジェクション対策

#### プリペアドステートメントの使用

```typescript
// ❌ Bad: SQLインジェクションの脆弱性
const query = `SELECT * FROM users WHERE email = '${email}'`;
db.query(query);

// ✅ Good: プリペアドステートメント
const query = 'SELECT * FROM users WHERE email = ?';
db.query(query, [email]);

// ✅ Better: ORM使用
const user = await userRepository.findOne({ where: { email } });
```

---

### 4. XSS対策

#### 出力のエスケープ

```typescript
import escape from 'escape-html';

function renderUserName(userName: string): string {
  return escape(userName); // HTML特殊文字をエスケープ
}

// React では自動的にエスケープされる
function UserProfile({ name }: { name: string }) {
  return <div>{name}</div>; // 安全
}

// dangerouslySetInnerHTML は避ける
function UnsafeComponent({ html }: { html: string }) {
  return <div dangerouslySetInnerHTML={{ __html: html }} />; // 危険
}
```

---

### 5. 機密情報の管理

#### 環境変数の使用

```typescript
// ❌ Bad: コードにハードコード
const API_KEY = 'sk_live_abcd1234';

// ✅ Good: 環境変数から読み込み
const API_KEY = process.env.API_KEY;
if (!API_KEY) {
  throw new Error('API_KEY is not set');
}
```

#### .envファイルの管理

```bash
# .env (Git にコミットしない)
DATABASE_URL=postgresql://user:pass@localhost:5432/db
API_KEY=sk_live_abcd1234
JWT_SECRET=your-secret-key

# .env.example (Git にコミット)
DATABASE_URL=
API_KEY=
JWT_SECRET=
```

---

## パフォーマンスベストプラクティス

### 1. データベースクエリの最適化

#### N+1クエリ問題の回避

```typescript
// ❌ Bad: N+1クエリ
const users = await userRepository.find();
for (const user of users) {
  user.posts = await postRepository.findByUserId(user.id); // N回のクエリ
}

// ✅ Good: Eager Loading
const users = await userRepository.find({
  relations: ['posts'] // 1回のJOINクエリ
});
```

#### インデックスの活用

```sql
-- 頻繁に検索されるカラムにインデックス
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- 複合インデックス
CREATE INDEX idx_orders_user_status ON orders(user_id, status);
```

---

### 2. キャッシングの活用

```typescript
import Redis from 'ioredis';

const redis = new Redis();
const CACHE_TTL = 60 * 5; // 5分

async function getUser(userId: string): Promise<User> {
  // キャッシュチェック
  const cached = await redis.get(`user:${userId}`);
  if (cached) {
    return JSON.parse(cached);
  }
  
  // データベースから取得
  const user = await userRepository.findById(userId);
  
  // キャッシュに保存
  await redis.setex(`user:${userId}`, CACHE_TTL, JSON.stringify(user));
  
  return user;
}

// キャッシュ無効化
async function updateUser(userId: string, data: UpdateUserDto): Promise<User> {
  const user = await userRepository.update(userId, data);
  await redis.del(`user:${userId}`); // キャッシュクリア
  return user;
}
```

---

### 3. 非同期処理の活用

```typescript
// ❌ Bad: 順次実行
const user = await getUserById(userId);
const orders = await getOrdersByUserId(userId);
const notifications = await getNotificationsByUserId(userId);

// ✅ Good: 並列実行
const [user, orders, notifications] = await Promise.all([
  getUserById(userId),
  getOrdersByUserId(userId),
  getNotificationsByUserId(userId)
]);
```

---

### 4. ペジネーション

```typescript
interface PaginationParams {
  page: number;
  limit: number;
}

interface PaginatedResponse<T> {
  data: T[];
  total: number;
  page: number;
  totalPages: number;
}

async function getUsers(
  params: PaginationParams
): Promise<PaginatedResponse<User>> {
  const offset = (params.page - 1) * params.limit;
  
  const [data, total] = await Promise.all([
    userRepository.find({
      skip: offset,
      take: params.limit
    }),
    userRepository.count()
  ]);
  
  return {
    data,
    total,
    page: params.page,
    totalPages: Math.ceil(total / params.limit)
  };
}
```

---

## テストベストプラクティス

### 1. テストピラミッド

```
           /\
          /  \  E2E (10%)
         /----\
        /      \ Integration (30%)
       /--------\
      /          \ Unit (60%)
     /____________\
```

### 2. ユニットテストの原則

#### AAA パターン (Arrange-Act-Assert)

```typescript
describe('UserService', () => {
  describe('createUser', () => {
    it('should create a user with hashed password', async () => {
      // Arrange (準備)
      const userData = {
        email: 'test@example.com',
        password: 'password123'
      };
      const mockRepository = {
        create: jest.fn().mockResolvedValue({ id: '1', ...userData })
      };
      const service = new UserService(mockRepository);
      
      // Act (実行)
      const result = await service.createUser(userData);
      
      // Assert (検証)
      expect(result).toHaveProperty('id');
      expect(result.email).toBe(userData.email);
      expect(mockRepository.create).toHaveBeenCalledTimes(1);
    });
  });
});
```

#### テストは独立させる

```typescript
// ❌ Bad: テスト間で状態を共有
let user: User;

beforeAll(() => {
  user = createUser(); // すべてのテストで同じユーザー
});

// ✅ Good: 各テストで独立した状態
beforeEach(() => {
  user = createUser(); // 各テストで新しいユーザー
});
```

---

### 3. モックの適切な使用

```typescript
// 外部依存のモック
jest.mock('./emailService');

describe('UserService', () => {
  it('should send welcome email after user creation', async () => {
    const mockEmailService = {
      sendWelcome: jest.fn().mockResolvedValue(undefined)
    };
    
    const service = new UserService(userRepository, mockEmailService);
    await service.createUser(userData);
    
    expect(mockEmailService.sendWelcome).toHaveBeenCalledWith(userData.email);
  });
});
```

---

## DevOpsベストプラクティス

### 1. CI/CDパイプライン

```yaml
# .github/workflows/ci.yml
name: CI

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup Node.js
        uses: actions/setup-node@v2
        with:
          node-version: '18'
      - name: Install dependencies
        run: npm ci
      - name: Lint
        run: npm run lint
      - name: Test
        run: npm test
      - name: Build
        run: npm run build
```

---

### 2. インフラストラクチャのコード化 (IaC)

```terraform
# Terraform example
resource "aws_instance" "web" {
  ami           = "ami-0c55b159cbfafe1f0"
  instance_type = "t3.micro"
  
  tags = {
    Name = "web-server"
    Environment = "production"
  }
}
```

---

### 3. ロギングのベストプラクティス

```typescript
import winston from 'winston';

const logger = winston.createLogger({
  level: process.env.LOG_LEVEL || 'info',
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.json()
  ),
  defaultMeta: { service: 'user-service' },
  transports: [
    new winston.transports.File({ filename: 'error.log', level: 'error' }),
    new winston.transports.File({ filename: 'combined.log' })
  ]
});

// 構造化ログ
logger.info('User created', {
  userId: user.id,
  email: user.email,
  timestamp: new Date().toISOString()
});
```

---

## データベースベストプラクティス

### 1. トランザクションの使用

```typescript
async function transferMoney(fromId: string, toId: string, amount: number) {
  const connection = await database.getConnection();
  
  try {
    await connection.beginTransaction();
    
    await connection.query(
      'UPDATE accounts SET balance = balance - ? WHERE id = ?',
      [amount, fromId]
    );
    
    await connection.query(
      'UPDATE accounts SET balance = balance + ? WHERE id = ?',
      [amount, toId]
    );
    
    await connection.commit();
  } catch (error) {
    await connection.rollback();
    throw error;
  } finally {
    connection.release();
  }
}
```

---

### 2. マイグレーション管理

```typescript
// migrations/001_create_users_table.ts
export async function up(connection: Connection) {
  await connection.query(`
    CREATE TABLE users (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      email VARCHAR(255) UNIQUE NOT NULL,
      password_hash VARCHAR(255) NOT NULL,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )
  `);
}

export async function down(connection: Connection) {
  await connection.query('DROP TABLE users');
}
```

---

## API設計ベストプラクティス

### 1. RESTful API設計

```typescript
// リソース指向のURL設計
GET    /api/v1/users          // ユーザー一覧取得
GET    /api/v1/users/:id      // 特定ユーザー取得
POST   /api/v1/users          // ユーザー作成
PUT    /api/v1/users/:id      // ユーザー更新(全体)
PATCH  /api/v1/users/:id      // ユーザー更新(部分)
DELETE /api/v1/users/:id      // ユーザー削除

// ネストされたリソース
GET /api/v1/users/:id/orders  // 特定ユーザーの注文一覧
```

---

### 2. バージョニング

```typescript
// URLでバージョン管理
app.use('/api/v1', v1Router);
app.use('/api/v2', v2Router);

// ヘッダーでバージョン管理
app.use((req, res, next) => {
  const version = req.headers['api-version'] || 'v1';
  // バージョンに応じた処理
});
```

---

### 3. エラーレスポンスの標準化

```typescript
interface ErrorResponse {
  error: {
    code: string;
    message: string;
    details?: any[];
    timestamp: string;
    path: string;
  };
}

app.use((error: Error, req: Request, res: Response, next: NextFunction) => {
  const response: ErrorResponse = {
    error: {
      code: error.name,
      message: error.message,
      timestamp: new Date().toISOString(),
      path: req.path
    }
  };
  
  res.status(getStatusCode(error)).json(response);
});
```

---

## 📚 参考資料

- [Clean Code by Robert C. Martin](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)
- [Refactoring by Martin Fowler](https://refactoring.com/)
- [The Pragmatic Programmer](https://pragprog.com/titles/tpp20/)
- [Google Style Guides](https://google.github.io/styleguide/)

---

## 変更履歴

| バージョン | 日付 | 変更者 | 変更内容 |
|----------|------|--------|---------|
| 1.0.0 | 2025-10-28 | Engineering Team | 初版作成 |

---

**保存先**: `/devin-organization-standards/09-reference/best-practices.md`