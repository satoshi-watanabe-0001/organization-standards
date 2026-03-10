---
version: 1.0.0
last_updated: 2025-10-28
status: active
owner: Engineering Team
category: reference
---

# Anti-Patterns / アンチパターン

## 📋 概要

このドキュメントは、避けるべき設計・実装パターンとその理由、代替案を提供します。アンチパターンを理解することで、より良いコードとアーキテクチャを実現できます。

---

## 📚 目次

1. [コードアンチパターン](#コードアンチパターン)
2. [アーキテクチャアンチパターン](#アーキテクチャアンチパターン)
3. [データベースアンチパターン](#データベースアンチパターン)
4. [セキュリティアンチパターン](#セキュリティアンチパターン)
5. [パフォーマンスアンチパターン](#パフォーマンスアンチパターン)
6. [プロジェクト管理アンチパターン](#プロジェクト管理アンチパターン)

---

## コードアンチパターン

### 1. God Object (神オブジェクト)

#### 問題
1つのクラスやモジュールが多すぎる責任を持ち、アプリケーションの大部分を制御している。

#### なぜ悪いか
- 単一責任の原則違反
- テストが困難
- 保守性の低下
- 再利用性の欠如
- 変更の影響範囲が広い

#### 悪い例

```typescript
class UserManager {
  // ユーザー管理
  createUser(data: any) { }
  updateUser(id: string, data: any) { }
  deleteUser(id: string) { }
  
  // 認証
  login(email: string, password: string) { }
  logout(userId: string) { }
  resetPassword(email: string) { }
  
  // 権限管理
  checkPermission(userId: string, resource: string) { }
  assignRole(userId: string, role: string) { }
  
  // メール送信
  sendWelcomeEmail(userId: string) { }
  sendPasswordResetEmail(email: string) { }
  
  // レポート生成
  generateUserReport() { }
  exportUserData() { }
  
  // ログ記録
  logUserActivity(userId: string, action: string) { }
  
  // その他、さらに多くのメソッド...
}
```

#### 良い例（責任を分離）

```typescript
// ユーザー管理
class UserService {
  createUser(data: CreateUserDto) { }
  updateUser(id: string, data: UpdateUserDto) { }
  deleteUser(id: string) { }
}

// 認証
class AuthService {
  login(email: string, password: string) { }
  logout(userId: string) { }
  resetPassword(email: string) { }
}

// 権限管理
class AuthorizationService {
  checkPermission(userId: string, resource: string) { }
  assignRole(userId: string, role: string) { }
}

// メール送信
class EmailService {
  sendWelcomeEmail(userId: string) { }
  sendPasswordResetEmail(email: string) { }
}

// レポート生成
class ReportService {
  generateUserReport() { }
  exportUserData() { }
}

// ログ記録
class LoggingService {
  logUserActivity(userId: string, action: string) { }
}
```

---

### 2. Magic Numbers/Strings (マジックナンバー/文字列)

#### 問題
コード中に意味不明な数値や文字列がハードコードされている。

#### なぜ悪いか
- 可読性の低下
- 保守性の低下
- 変更時のエラー発生リスク
- 意図が不明確

#### 悪い例

```typescript
function calculateDiscount(price: number, customerType: string): number {
  if (customerType === 'vip') {
    return price * 0.8; // 20% off - なぜ0.8？
  } else if (customerType === 'regular') {
    return price * 0.95; // 5% off
  }
  return price;
}

function processOrder(order: Order) {
  if (order.items.length > 10) { // なぜ10？
    applyBulkDiscount(order);
  }
  
  if (order.total > 1000) { // なぜ1000？
    addFreeShipping(order);
  }
}
```

#### 良い例

```typescript
// 定数を定義
const DISCOUNT_RATES = {
  VIP: 0.8,      // 20% discount
  REGULAR: 0.95  // 5% discount
} as const;

const CUSTOMER_TYPES = {
  VIP: 'vip',
  REGULAR: 'regular'
} as const;

const BULK_ORDER_THRESHOLD = 10; // items
const FREE_SHIPPING_THRESHOLD = 1000; // dollars

function calculateDiscount(price: number, customerType: string): number {
  if (customerType === CUSTOMER_TYPES.VIP) {
    return price * DISCOUNT_RATES.VIP;
  } else if (customerType === CUSTOMER_TYPES.REGULAR) {
    return price * DISCOUNT_RATES.REGULAR;
  }
  return price;
}

function processOrder(order: Order) {
  if (order.items.length > BULK_ORDER_THRESHOLD) {
    applyBulkDiscount(order);
  }
  
  if (order.total > FREE_SHIPPING_THRESHOLD) {
    addFreeShipping(order);
  }
}
```

---

### 3. Spaghetti Code (スパゲッティコード)

#### 問題
構造化されておらず、複雑に絡み合った制御フローのコード。

#### なぜ悪いか
- 可読性が極めて低い
- デバッグが困難
- 変更が危険
- 理解するのに時間がかかる

#### 悪い例

```typescript
function processUserData(user: any) {
  if (user) {
    if (user.email) {
      if (validateEmail(user.email)) {
        if (user.age) {
          if (user.age >= 18) {
            if (user.country) {
              if (ALLOWED_COUNTRIES.includes(user.country)) {
                if (user.agreed_terms) {
                  // 実際の処理
                  const result = saveUser(user);
                  if (result) {
                    sendWelcomeEmail(user.email);
                    if (user.referral) {
                      processReferral(user.referral);
                    }
                    return { success: true };
                  } else {
                    return { success: false, error: 'Save failed' };
                  }
                } else {
                  return { success: false, error: 'Terms not agreed' };
                }
              } else {
                return { success: false, error: 'Country not allowed' };
              }
            } else {
              return { success: false, error: 'Country required' };
            }
          } else {
            return { success: false, error: 'Must be 18+' };
          }
        } else {
          return { success: false, error: 'Age required' };
        }
      } else {
        return { success: false, error: 'Invalid email' };
      }
    } else {
      return { success: false, error: 'Email required' };
    }
  } else {
    return { success: false, error: 'User data required' };
  }
}
```

#### 良い例（Early Return & 関数分割）

```typescript
interface ProcessResult {
  success: boolean;
  error?: string;
}

function processUserData(user: any): ProcessResult {
  // バリデーション（Early Return）
  const validationError = validateUserData(user);
  if (validationError) {
    return { success: false, error: validationError };
  }
  
  // メイン処理
  const saveResult = saveUser(user);
  if (!saveResult) {
    return { success: false, error: 'Save failed' };
  }
  
  // 後処理
  performPostProcessing(user);
  
  return { success: true };
}

function validateUserData(user: any): string | null {
  if (!user) return 'User data required';
  if (!user.email) return 'Email required';
  if (!validateEmail(user.email)) return 'Invalid email';
  if (!user.age) return 'Age required';
  if (user.age < 18) return 'Must be 18+';
  if (!user.country) return 'Country required';
  if (!ALLOWED_COUNTRIES.includes(user.country)) return 'Country not allowed';
  if (!user.agreed_terms) return 'Terms not agreed';
  
  return null; // バリデーション成功
}

function performPostProcessing(user: any): void {
  sendWelcomeEmail(user.email);
  
  if (user.referral) {
    processReferral(user.referral);
  }
}
```

---

### 4. Copy-Paste Programming (コピペプログラミング)

#### 問題
コードをコピーして複数箇所に貼り付け、わずかな変更を加えて使用。

#### なぜ悪いか
- DRY原則違反
- バグ修正時に複数箇所の修正が必要
- 保守コストの増加
- コードの肥大化

#### 悪い例

```typescript
// ユーザー作成
async function createUser(data: any) {
  if (!data.email) throw new Error('Email required');
  if (!validateEmail(data.email)) throw new Error('Invalid email');
  const hashedPassword = await bcrypt.hash(data.password, 10);
  const user = await db.users.create({
    email: data.email,
    password: hashedPassword
  });
  logger.info(`User created: ${user.id}`);
  return user;
}

// 管理者作成（ほぼ同じコード）
async function createAdmin(data: any) {
  if (!data.email) throw new Error('Email required');
  if (!validateEmail(data.email)) throw new Error('Invalid email');
  const hashedPassword = await bcrypt.hash(data.password, 10);
  const admin = await db.admins.create({
    email: data.email,
    password: hashedPassword
  });
  logger.info(`Admin created: ${admin.id}`);
  return admin;
}

// モデレーター作成（ほぼ同じコード）
async function createModerator(data: any) {
  if (!data.email) throw new Error('Email required');
  if (!validateEmail(data.email)) throw new Error('Invalid email');
  const hashedPassword = await bcrypt.hash(data.password, 10);
  const moderator = await db.moderators.create({
    email: data.email,
    password: hashedPassword
  });
  logger.info(`Moderator created: ${moderator.id}`);
  return moderator;
}
```

#### 良い例（共通ロジックの抽出）

```typescript
interface CreateAccountData {
  email: string;
  password: string;
}

enum AccountType {
  USER = 'user',
  ADMIN = 'admin',
  MODERATOR = 'moderator'
}

async function createAccount(
  data: CreateAccountData,
  type: AccountType
): Promise<any> {
  // 共通バリデーション
  validateAccountData(data);
  
  // 共通処理
  const hashedPassword = await hashPassword(data.password);
  
  // タイプ別の処理
  const account = await saveAccount(data.email, hashedPassword, type);
  
  // 共通ログ
  logger.info(`${type} created: ${account.id}`);
  
  return account;
}

function validateAccountData(data: CreateAccountData): void {
  if (!data.email) throw new Error('Email required');
  if (!validateEmail(data.email)) throw new Error('Invalid email');
}

async function hashPassword(password: string): Promise<string> {
  return bcrypt.hash(password, 10);
}

async function saveAccount(
  email: string,
  hashedPassword: string,
  type: AccountType
): Promise<any> {
  const collection = getCollectionByType(type);
  return collection.create({ email, password: hashedPassword });
}

// 使用
const user = await createAccount(userData, AccountType.USER);
const admin = await createAccount(adminData, AccountType.ADMIN);
const moderator = await createAccount(modData, AccountType.MODERATOR);
```

---

### 5. Premature Optimization (早すぎる最適化)

#### 問題
必要性が不明確なうちにパフォーマンス最適化を行う。

#### なぜ悪いか
- 複雑性の増加
- 可読性の低下
- 実際のボトルネックではない箇所の最適化
- 開発時間の浪費

#### Donald Knuthの名言
> "Premature optimization is the root of all evil."
> （早すぎる最適化は諸悪の根源である）

#### 悪い例

```typescript
// 過度に最適化された不明瞭なコード
function calculateTotal(items: number[]): number {
  let t = 0, i = items.length;
  while (i--) t += items[i]; // 逆ループで高速化？
  return t;
}

// ビット演算で"最適化"
function isEven(n: number): boolean {
  return !(n & 1); // 可読性が低い
}
```

#### 良い例（まず明確さを優先）

```typescript
// 明確で理解しやすいコード
function calculateTotal(items: number[]): number {
  return items.reduce((sum, item) => sum + item, 0);
}

function isEven(n: number): boolean {
  return n % 2 === 0; // 意図が明確
}

// パフォーマンスが問題になったら、その時に最適化
// 1. プロファイリングでボトルネック特定
// 2. ベンチマークで改善効果を測定
// 3. 最適化とコメント追加
```

#### 最適化の正しいアプローチ

```typescript
// 1. まず動くコードを書く
function processData(data: any[]) {
  return data.map(item => transform(item));
}

// 2. パフォーマンス問題が確認されたら計測
// 3. ボトルネックを特定
// 4. 最適化とドキュメント化

/**
 * データ処理（最適化版）
 * 
 * 通常の map よりも 30% 高速
 * ベンチマーク: 10,000件で 45ms → 31ms
 * 
 * 最適化理由: 本番環境で処理遅延が発生したため
 * 日付: 2025-10-28
 */
function processDataOptimized(data: any[]) {
  const result = new Array(data.length);
  for (let i = 0; i < data.length; i++) {
    result[i] = transform(data[i]);
  }
  return result;
}
```

---

## アーキテクチャアンチパターン

### 1. Big Ball of Mud (泥団子)

#### 問題
構造やアーキテクチャが存在せず、無秩序にコードが追加されている状態。

#### なぜ悪いか
- 理解が困難
- 変更が危険
- スケールしない
- テストが困難

#### 症状
- 明確なレイヤー分離がない
- 循環依存が多数
- モジュール境界が不明確
- ビジネスロジックが散在

#### 対策

```typescript
// ❌ Bad: すべてが混在
// controller.ts
app.post('/users', async (req, res) => {
  // バリデーション、ビジネスロジック、データアクセスがすべて混在
  if (!req.body.email) return res.status(400).json({ error: 'Email required' });
  const hashedPassword = await bcrypt.hash(req.body.password, 10);
  const user = await db.query('INSERT INTO users...');
  await sendEmail(user.email, 'Welcome!');
  res.json(user);
});

// ✅ Good: レイヤー分離
// controllers/user.controller.ts
class UserController {
  constructor(private userService: UserService) {}
  
  async create(req: Request, res: Response) {
    const userData = CreateUserDto.validate(req.body);
    const user = await this.userService.createUser(userData);
    res.status(201).json(user);
  }
}

// services/user.service.ts
class UserService {
  constructor(
    private userRepository: UserRepository,
    private emailService: EmailService
  ) {}
  
  async createUser(data: CreateUserDto): Promise<User> {
    const hashedPassword = await this.hashPassword(data.password);
    const user = await this.userRepository.create({
      ...data,
      password: hashedPassword
    });
    await this.emailService.sendWelcome(user.email);
    return user;
  }
}

// repositories/user.repository.ts
class UserRepository {
  async create(data: CreateUserData): Promise<User> {
    return this.db.users.create(data);
  }
}
```

---

### 2. Distributed Monolith (分散モノリス)

#### 問題
マイクロサービスアーキテクチャの形をしているが、実質的に密結合したモノリス。

#### なぜ悪いか
- マイクロサービスの利点がない
- モノリスの欠点とマイクロサービスの複雑さの両方を抱える
- デプロイの依存関係
- ネットワークオーバーヘッド

#### 症状

```typescript
// サービス間の密結合
// Order Service
class OrderService {
  async createOrder(orderData: any) {
    // 直接的な同期呼び出し
    const user = await userService.getUser(orderData.userId);
    const inventory = await inventoryService.checkStock(orderData.items);
    const payment = await paymentService.processPayment(orderData);
    
    // すべてが同期的で、1つでも失敗すると全体が失敗
    if (!user || !inventory || !payment) {
      throw new Error('Order creation failed');
    }
    
    return this.saveOrder(orderData);
  }
}
```

#### 対策（疎結合化）

```typescript
// イベント駆動アーキテクチャ
class OrderService {
  constructor(private eventBus: EventBus) {}
  
  async createOrder(orderData: any) {
    // 注文を作成（自己完結）
    const order = await this.saveOrder(orderData);
    
    // イベントを発行（非同期）
    await this.eventBus.publish('order.created', {
      orderId: order.id,
      userId: orderData.userId,
      items: orderData.items
    });
    
    return order;
  }
}

// 他のサービスはイベントを購読
class InventoryService {
  constructor(private eventBus: EventBus) {
    this.eventBus.subscribe('order.created', this.handleOrderCreated);
  }
  
  async handleOrderCreated(event: OrderCreatedEvent) {
    await this.reserveStock(event.items);
  }
}
```

---

### 3. Golden Hammer (金槌症候群)

#### 問題
「すべての問題は釘に見える」- 1つの技術やパターンをすべての問題に適用しようとする。

#### なぜ悪いか
- 問題に最適でない解決策
- オーバーエンジニアリング
- 不必要な複雑性

#### 例

```typescript
// ❌ Bad: すべてにマイクロサービスを使用
// 小規模アプリでも無理にマイクロサービス化
// - User Service
// - Auth Service
// - Notification Service
// - Email Service
// - SMS Service
// ...（10個のマイクロサービス）

// 結果: 
// - 運用の複雑性
// - デバッグの困難さ
// - パフォーマンスオーバーヘッド

// ✅ Good: 問題に適したアーキテクチャ
// 小規模〜中規模の場合、まずモノリシックモジュラーアーキテクチャから
// スケールが必要になったら段階的にマイクロサービス化
```

---

## データベースアンチパターン

### 1. EAV (Entity-Attribute-Value) の誤用

#### 問題
すべてのデータをEAVパターンで保存する。

#### なぜ悪いか
- パフォーマンスの低下
- クエリの複雑化
- データ整合性の保証が困難
- 型安全性の欠如

#### 悪い例

```sql
-- EAV テーブル
CREATE TABLE entity_attributes (
  entity_id INT,
  attribute_name VARCHAR(100),
  attribute_value TEXT
);

-- データの挿入（非常に冗長）
INSERT INTO entity_attributes VALUES (1, 'name', 'John Doe');
INSERT INTO entity_attributes VALUES (1, 'email', 'john@example.com');
INSERT INTO entity_attributes VALUES (1, 'age', '30');

-- クエリが複雑
SELECT 
  MAX(CASE WHEN attribute_name = 'name' THEN attribute_value END) as name,
  MAX(CASE WHEN attribute_name = 'email' THEN attribute_value END) as email,
  MAX(CASE WHEN attribute_name = 'age' THEN attribute_value END) as age
FROM entity_attributes
WHERE entity_id = 1
GROUP BY entity_id;
```

#### 良い例

```sql
-- 通常のテーブル設計
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  age INTEGER CHECK (age >= 0)
);

-- シンプルなクエリ
SELECT name, email, age FROM users WHERE id = 1;
```

---

### 2. N+1 Query Problem

#### 問題
関連データを取得する際に、1回のクエリで取得できるものを N+1 回のクエリで取得。

#### なぜ悪いか
- パフォーマンスの大幅な低下
- データベース負荷の増加

#### 悪い例

```typescript
// ❌ Bad: N+1 クエリ
async function getUsersWithPosts() {
  const users = await db.query('SELECT * FROM users'); // 1回
  
  for (const user of users) {
    // N回のクエリ（ユーザー数分）
    user.posts = await db.query(
      'SELECT * FROM posts WHERE user_id = ?',
      [user.id]
    );
  }
  
  return users;
}
```

#### 良い例

```typescript
// ✅ Good: JOIN または Eager Loading
async function getUsersWithPosts() {
  // 1回のクエリで取得
  const result = await db.query(`
    SELECT 
      u.*,
      p.id as post_id,
      p.title as post_title,
      p.content as post_content
    FROM users u
    LEFT JOIN posts p ON u.id = p.user_id
  `);
  
  // 結果を整形
  return groupUsersPosts(result);
}

// または ORM の Eager Loading
const users = await userRepository.find({
  relations: ['posts']
});
```

---

### 3. CHAR(1) for Boolean (Boolean に CHAR を使用)

#### 問題
真偽値に CHAR(1) や TINYINT を使用する。

#### なぜ悪いか
- 可読性の低下
- 型安全性の欠如
- 誤った値の挿入リスク

#### 悪い例

```sql
CREATE TABLE users (
  id INT PRIMARY KEY,
  name VARCHAR(100),
  is_active CHAR(1)  -- 'Y', 'N', 't', 'f', '1', '0'? 不明確
);

-- クエリが不明瞭
SELECT * FROM users WHERE is_active = 'Y';
SELECT * FROM users WHERE is_active = '1';  -- どっち？
```

#### 良い例

```sql
CREATE TABLE users (
  id INT PRIMARY KEY,
  name VARCHAR(100),
  is_active BOOLEAN DEFAULT TRUE  -- 明確
);

-- クエリが明確
SELECT * FROM users WHERE is_active = TRUE;
```

---

## セキュリティアンチパターン

### 1. Storing Passwords in Plain Text (平文パスワード保存)

#### 問題
パスワードを暗号化せずにそのまま保存する。

#### なぜ悪いか
- データ漏洩時に全ユーザーのパスワードが露出
- 法的問題
- 信頼の喪失

#### 悪い例

```typescript
// ❌ Bad: 平文保存
async function createUser(email: string, password: string) {
  await db.users.create({
    email,
    password  // 平文で保存！
  });
}

async function login(email: string, password: string) {
  const user = await db.users.findOne({ email });
  return user && user.password === password;  // 平文比較！
}
```

#### 良い例

```typescript
// ✅ Good: ハッシュ化して保存
import bcrypt from 'bcrypt';

const SALT_ROUNDS = 12;

async function createUser(email: string, password: string) {
  const passwordHash = await bcrypt.hash(password, SALT_ROUNDS);
  await db.users.create({
    email,
    password: passwordHash  // ハッシュ化して保存
  });
}

async function login(email: string, password: string) {
  const user = await db.users.findOne({ email });
  if (!user) return false;
  
  return bcrypt.compare(password, user.password);  // 安全な比較
}
```

---

### 2. SQL Injection Vulnerability

#### 問題
ユーザー入力を直接SQLクエリに埋め込む。

#### 悪い例

```typescript
// ❌ Bad: SQLインジェクションの脆弱性
async function getUser(email: string) {
  const query = `SELECT * FROM users WHERE email = '${email}'`;
  return db.query(query);
}

// 攻撃例: email = "'; DROP TABLE users; --"
// 実行されるクエリ: SELECT * FROM users WHERE email = ''; DROP TABLE users; --'
```

#### 良い例

```typescript
// ✅ Good: プリペアドステートメント
async function getUser(email: string) {
  const query = 'SELECT * FROM users WHERE email = ?';
  return db.query(query, [email]);  // 安全
}

// または ORM 使用
const user = await userRepository.findOne({ where: { email } });
```

---

## パフォーマンスアンチパターン

### 1. Unnecessary Database Calls in Loops

#### 問題
ループ内で不要なデータベースクエリを実行する。

#### 悪い例

```typescript
// ❌ Bad: ループ内でDB呼び出し
async function updateUserScores(userIds: string[]) {
  for (const userId of userIds) {
    const user = await db.users.findById(userId);  // N回のクエリ
    const newScore = calculateScore(user);
    await db.users.update(userId, { score: newScore });  // さらにN回
  }
}
```

#### 良い例

```typescript
// ✅ Good: バルク操作
async function updateUserScores(userIds: string[]) {
  // 1回で全ユーザー取得
  const users = await db.users.find({ id: { $in: userIds } });
  
  // バルク更新用のデータ準備
  const updates = users.map(user => ({
    id: user.id,
    score: calculateScore(user)
  }));
  
  // 1回のバルク更新
  await db.users.bulkUpdate(updates);
}
```

---

### 2. Loading Entire Dataset into Memory

#### 問題
大量のデータを一度にメモリに読み込む。

#### 悪い例

```typescript
// ❌ Bad: 全データをメモリに
async function processAllUsers() {
  const users = await db.users.find();  // 100万件全て取得
  
  for (const user of users) {
    await processUser(user);
  }
}
```

#### 良い例

```typescript
// ✅ Good: ストリーミングまたはページネーション
async function processAllUsers() {
  const PAGE_SIZE = 100;
  let page = 0;
  let hasMore = true;
  
  while (hasMore) {
    const users = await db.users.find({
      skip: page * PAGE_SIZE,
      take: PAGE_SIZE
    });
    
    for (const user of users) {
      await processUser(user);
    }
    
    hasMore = users.length === PAGE_SIZE;
    page++;
  }
}

// または Stream 使用
const stream = db.users.stream();
stream.on('data', async (user) => {
  await processUser(user);
});
```

---

## プロジェクト管理アンチパターン

### 1. Analysis Paralysis (分析麻痺)

#### 問題
完璧な設計を求めて、実装を始められない。

#### なぜ悪いか
- 時間の浪費
- 市場機会の損失
- チームのモチベーション低下

#### 対策
- MVP (Minimum Viable Product) アプローチ
- Agile 開発
- 早期のフィードバック収集
- "Done is better than perfect"

---

### 2. Death March (デスマーチ)

#### 問題
非現実的なスケジュールとリソースでプロジェクトを進行。

#### 症状
- 長時間労働
- バーンアウト
- 品質の低下
- 離職率の増加

#### 対策
- 現実的な見積もり
- バッファの確保
- スコープ調整
- 定期的な進捗確認

---

### 3. Feature Creep (機能のクリープ)

#### 問題
プロジェクト途中で次々と新機能を追加する。

#### なぜ悪いか
- スケジュール遅延
- 予算超過
- フォーカスの喪失

#### 対策
- 明確な要件定義
- 変更管理プロセス
- 優先度付け
- スコープの固定（タイムボックス）

---

## 📚 参考資料

- [AntiPatterns: Refactoring Software, Architectures, and Projects in Crisis](https://www.amazon.com/dp/0471197130)
- [SQL Antipatterns by Bill Karwin](https://pragprog.com/titles/bksqla/)
- [Refactoring by Martin Fowler](https://refactoring.com/)

---

## 変更履歴

| バージョン | 日付 | 変更者 | 変更内容 |
|----------|------|--------|---------|
| 1.0.0 | 2025-10-28 | Engineering Team | 初版作成 |

---

**保存先**: `/devin-organization-standards/09-reference/anti-patterns.md`