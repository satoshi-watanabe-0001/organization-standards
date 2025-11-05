---
version: 1.0.0
last_updated: 2025-10-28
status: active
owner: Engineering Team
category: reference
---

# Design Patterns / デザインパターン

## 📋 概要

このドキュメントは、組織で推奨するソフトウェアデザインパターンの解説と実装例を提供します。適切なパターンの選択により、保守性、拡張性、再利用性が向上します。

---

## 📚 目次

1. [生成パターン (Creational Patterns)](#生成パターン-creational-patterns)
2. [構造パターン (Structural Patterns)](#構造パターン-structural-patterns)
3. [振る舞いパターン (Behavioral Patterns)](#振る舞いパターン-behavioral-patterns)
4. [アーキテクチャパターン](#アーキテクチャパターン)
5. [クラウドデザインパターン](#クラウドデザインパターン)
6. [マイクロサービスパターン](#マイクロサービスパターン)

---

## 生成パターン (Creational Patterns)

### 1. Singleton Pattern (シングルトンパターン)

#### 目的
クラスのインスタンスが1つだけ存在することを保証し、グローバルなアクセスポイントを提供する。

#### 使用ケース
- データベース接続
- ログ管理
- 設定管理
- キャッシュ管理

#### 実装例

```typescript
class DatabaseConnection {
  private static instance: DatabaseConnection;
  private connection: any;

  private constructor() {
    // プライベートコンストラクタ
    this.connection = this.createConnection();
  }

  public static getInstance(): DatabaseConnection {
    if (!DatabaseConnection.instance) {
      DatabaseConnection.instance = new DatabaseConnection();
    }
    return DatabaseConnection.instance;
  }

  private createConnection(): any {
    // データベース接続のロジック
    return { /* connection object */ };
  }

  public query(sql: string): any {
    return this.connection.query(sql);
  }
}

// 使用例
const db1 = DatabaseConnection.getInstance();
const db2 = DatabaseConnection.getInstance();
console.log(db1 === db2); // true
```

#### 注意点
- テストが困難になる可能性（モック化が難しい）
- グローバル状態を作るため、依存性注入の方が好ましい場合もある
- マルチスレッド環境では注意が必要

---

### 2. Factory Pattern (ファクトリーパターン)

#### 目的
オブジェクト生成のロジックをカプセル化し、インターフェースを通じてオブジェクトを生成する。

#### 使用ケース
- 複雑な生成ロジック
- 条件によって異なるクラスのインスタンスを生成
- 生成するクラスが実行時に決まる場合

#### 実装例

```typescript
// 製品インターフェース
interface Payment {
  process(amount: number): Promise<void>;
}

// 具体的な製品
class CreditCardPayment implements Payment {
  async process(amount: number): Promise<void> {
    console.log(`Processing credit card payment: $${amount}`);
    // クレジットカード処理ロジック
  }
}

class PayPalPayment implements Payment {
  async process(amount: number): Promise<void> {
    console.log(`Processing PayPal payment: $${amount}`);
    // PayPal処理ロジック
  }
}

class BitcoinPayment implements Payment {
  async process(amount: number): Promise<void> {
    console.log(`Processing Bitcoin payment: $${amount}`);
    // Bitcoin処理ロジック
  }
}

// ファクトリークラス
class PaymentFactory {
  static createPayment(type: string): Payment {
    switch (type) {
      case 'credit-card':
        return new CreditCardPayment();
      case 'paypal':
        return new PayPalPayment();
      case 'bitcoin':
        return new BitcoinPayment();
      default:
        throw new Error(`Unknown payment type: ${type}`);
    }
  }
}

// 使用例
const paymentType = 'paypal';
const payment = PaymentFactory.createPayment(paymentType);
await payment.process(100);
```

---

### 3. Builder Pattern (ビルダーパターン)

#### 目的
複雑なオブジェクトの構築プロセスを段階的に行い、同じ構築プロセスで異なる表現を作成できるようにする。

#### 使用ケース
- 多数のオプショナルパラメータを持つオブジェクト
- オブジェクト構築の段階が明確
- 不変オブジェクトの構築

#### 実装例

```typescript
class User {
  constructor(
    public readonly id: string,
    public readonly name: string,
    public readonly email: string,
    public readonly age?: number,
    public readonly address?: string,
    public readonly phone?: string,
    public readonly bio?: string
  ) {}
}

class UserBuilder {
  private id!: string;
  private name!: string;
  private email!: string;
  private age?: number;
  private address?: string;
  private phone?: string;
  private bio?: string;

  setId(id: string): UserBuilder {
    this.id = id;
    return this;
  }

  setName(name: string): UserBuilder {
    this.name = name;
    return this;
  }

  setEmail(email: string): UserBuilder {
    this.email = email;
    return this;
  }

  setAge(age: number): UserBuilder {
    this.age = age;
    return this;
  }

  setAddress(address: string): UserBuilder {
    this.address = address;
    return this;
  }

  setPhone(phone: string): UserBuilder {
    this.phone = phone;
    return this;
  }

  setBio(bio: string): UserBuilder {
    this.bio = bio;
    return this;
  }

  build(): User {
    if (!this.id || !this.name || !this.email) {
      throw new Error('Required fields are missing');
    }
    return new User(
      this.id,
      this.name,
      this.email,
      this.age,
      this.address,
      this.phone,
      this.bio
    );
  }
}

// 使用例
const user = new UserBuilder()
  .setId('123')
  .setName('John Doe')
  .setEmail('john@example.com')
  .setAge(30)
  .setPhone('+1-555-1234')
  .build();
```

---

## 構造パターン (Structural Patterns)

### 1. Adapter Pattern (アダプターパターン)

#### 目的
互換性のないインターフェースを持つクラスを協調動作させる。

#### 使用ケース
- レガシーコードと新しいコードの統合
- サードパーティライブラリの統合
- インターフェースの標準化

#### 実装例

```typescript
// ターゲットインターフェース（期待されるインターフェース）
interface ILogger {
  log(message: string, level: string): void;
}

// 既存のクラス（互換性がない）
class LegacyLogger {
  logMessage(msg: string): void {
    console.log(`[LEGACY] ${msg}`);
  }
}

// アダプター
class LoggerAdapter implements ILogger {
  constructor(private legacyLogger: LegacyLogger) {}

  log(message: string, level: string): void {
    const formattedMessage = `[${level.toUpperCase()}] ${message}`;
    this.legacyLogger.logMessage(formattedMessage);
  }
}

// 使用例
const legacyLogger = new LegacyLogger();
const logger: ILogger = new LoggerAdapter(legacyLogger);
logger.log('Application started', 'info');
```

---

### 2. Decorator Pattern (デコレーターパターン)

#### 目的
オブジェクトに動的に新しい機能を追加する。継承の代替として使用。

#### 使用ケース
- 機能の動的な追加
- 責務の分離
- 組み合わせ可能な機能

#### 実装例

```typescript
// コンポーネントインターフェース
interface Coffee {
  getCost(): number;
  getDescription(): string;
}

// 基本コンポーネント
class SimpleCoffee implements Coffee {
  getCost(): number {
    return 10;
  }

  getDescription(): string {
    return 'Simple coffee';
  }
}

// デコレーター基底クラス
abstract class CoffeeDecorator implements Coffee {
  constructor(protected coffee: Coffee) {}

  abstract getCost(): number;
  abstract getDescription(): string;
}

// 具体的なデコレーター
class MilkDecorator extends CoffeeDecorator {
  getCost(): number {
    return this.coffee.getCost() + 2;
  }

  getDescription(): string {
    return this.coffee.getDescription() + ', milk';
  }
}

class SugarDecorator extends CoffeeDecorator {
  getCost(): number {
    return this.coffee.getCost() + 1;
  }

  getDescription(): string {
    return this.coffee.getDescription() + ', sugar';
  }
}

class WhipDecorator extends CoffeeDecorator {
  getCost(): number {
    return this.coffee.getCost() + 3;
  }

  getDescription(): string {
    return this.coffee.getDescription() + ', whip';
  }
}

// 使用例
let coffee: Coffee = new SimpleCoffee();
console.log(`${coffee.getDescription()} costs $${coffee.getCost()}`);

coffee = new MilkDecorator(coffee);
coffee = new SugarDecorator(coffee);
coffee = new WhipDecorator(coffee);
console.log(`${coffee.getDescription()} costs $${coffee.getCost()}`);
// "Simple coffee, milk, sugar, whip costs $16"
```

---

### 3. Facade Pattern (ファサードパターン)

#### 目的
複雑なサブシステムへの統一されたシンプルなインターフェースを提供する。

#### 使用ケース
- 複雑なライブラリやAPIの簡略化
- レイヤー間の結合度を下げる
- サブシステムへのアクセスポイントを一元化

#### 実装例

```typescript
// 複雑なサブシステム
class CPU {
  freeze(): void {
    console.log('CPU: Freezing...');
  }

  jump(position: number): void {
    console.log(`CPU: Jumping to ${position}`);
  }

  execute(): void {
    console.log('CPU: Executing...');
  }
}

class Memory {
  load(position: number, data: string): void {
    console.log(`Memory: Loading '${data}' at ${position}`);
  }
}

class HardDrive {
  read(lba: number, size: number): string {
    console.log(`HardDrive: Reading ${size} bytes from ${lba}`);
    return 'boot data';
  }
}

// ファサード
class ComputerFacade {
  private cpu: CPU;
  private memory: Memory;
  private hardDrive: HardDrive;

  constructor() {
    this.cpu = new CPU();
    this.memory = new Memory();
    this.hardDrive = new HardDrive();
  }

  start(): void {
    console.log('Computer: Starting...');
    this.cpu.freeze();
    const bootData = this.hardDrive.read(0, 1024);
    this.memory.load(0, bootData);
    this.cpu.jump(0);
    this.cpu.execute();
    console.log('Computer: Started successfully');
  }
}

// 使用例
const computer = new ComputerFacade();
computer.start(); // 複雑な起動プロセスがシンプルに
```

---

## 振る舞いパターン (Behavioral Patterns)

### 1. Strategy Pattern (ストラテジーパターン)

#### 目的
アルゴリズムのファミリーを定義し、それぞれをカプセル化して交換可能にする。

#### 使用ケース
- 条件分岐の代替
- アルゴリズムの切り替え
- 動作の動的な変更

#### 実装例

```typescript
// 戦略インターフェース
interface PricingStrategy {
  calculate(price: number): number;
}

// 具体的な戦略
class RegularPricing implements PricingStrategy {
  calculate(price: number): number {
    return price;
  }
}

class BlackFridayPricing implements PricingStrategy {
  calculate(price: number): number {
    return price * 0.5; // 50% off
  }
}

class MemberPricing implements PricingStrategy {
  calculate(price: number): number {
    return price * 0.9; // 10% off
  }
}

class VIPPricing implements PricingStrategy {
  calculate(price: number): number {
    return price * 0.8; // 20% off
  }
}

// コンテキスト
class ShoppingCart {
  private items: Array<{ name: string; price: number }> = [];
  private pricingStrategy: PricingStrategy;

  constructor(pricingStrategy: PricingStrategy) {
    this.pricingStrategy = pricingStrategy;
  }

  setPricingStrategy(strategy: PricingStrategy): void {
    this.pricingStrategy = strategy;
  }

  addItem(name: string, price: number): void {
    this.items.push({ name, price });
  }

  calculateTotal(): number {
    const subtotal = this.items.reduce((sum, item) => sum + item.price, 0);
    return this.pricingStrategy.calculate(subtotal);
  }
}

// 使用例
const cart = new ShoppingCart(new RegularPricing());
cart.addItem('Laptop', 1000);
cart.addItem('Mouse', 50);
console.log(`Regular price: $${cart.calculateTotal()}`); // $1050

cart.setPricingStrategy(new BlackFridayPricing());
console.log(`Black Friday price: $${cart.calculateTotal()}`); // $525

cart.setPricingStrategy(new VIPPricing());
console.log(`VIP price: $${cart.calculateTotal()}`); // $840
```

---

### 2. Observer Pattern (オブザーバーパターン)

#### 目的
オブジェクト間の一対多の依存関係を定義し、あるオブジェクトの状態が変化したときに、依存する全オブジェクトに自動的に通知する。

#### 使用ケース
- イベント駆動システム
- 状態変更の通知
- リアクティブプログラミング

#### 実装例

```typescript
// オブザーバーインターフェース
interface Observer {
  update(data: any): void;
}

// サブジェクト（監視対象）
class Subject {
  private observers: Observer[] = [];

  attach(observer: Observer): void {
    if (!this.observers.includes(observer)) {
      this.observers.push(observer);
      console.log('Observer attached');
    }
  }

  detach(observer: Observer): void {
    const index = this.observers.indexOf(observer);
    if (index > -1) {
      this.observers.splice(index, 1);
      console.log('Observer detached');
    }
  }

  notify(data: any): void {
    console.log('Notifying observers...');
    for (const observer of this.observers) {
      observer.update(data);
    }
  }
}

// 具体的なサブジェクト
class NewsAgency extends Subject {
  private latestNews: string = '';

  setNews(news: string): void {
    this.latestNews = news;
    this.notify(news);
  }

  getNews(): string {
    return this.latestNews;
  }
}

// 具体的なオブザーバー
class NewsChannel implements Observer {
  constructor(private name: string) {}

  update(news: string): void {
    console.log(`${this.name} received news: ${news}`);
  }
}

class EmailSubscriber implements Observer {
  constructor(private email: string) {}

  update(news: string): void {
    console.log(`Sending email to ${this.email}: ${news}`);
  }
}

// 使用例
const newsAgency = new NewsAgency();

const channel1 = new NewsChannel('Channel 1');
const channel2 = new NewsChannel('Channel 2');
const subscriber = new EmailSubscriber('user@example.com');

newsAgency.attach(channel1);
newsAgency.attach(channel2);
newsAgency.attach(subscriber);

newsAgency.setNews('Breaking: New design patterns released!');
```

---

### 3. Command Pattern (コマンドパターン)

#### 目的
リクエストをオブジェクトとしてカプセル化し、パラメータ化、キュー化、ログ記録、Undo操作を可能にする。

#### 使用ケース
- Undo/Redo機能
- トランザクション処理
- ジョブキュー
- マクロ記録

#### 実装例

```typescript
// コマンドインターフェース
interface Command {
  execute(): void;
  undo(): void;
}

// レシーバー（実際の処理を行うクラス）
class TextEditor {
  private text: string = '';

  append(textToAppend: string): void {
    this.text += textToAppend;
  }

  delete(length: number): void {
    this.text = this.text.slice(0, -length);
  }

  getText(): string {
    return this.text;
  }
}

// 具体的なコマンド
class AppendCommand implements Command {
  private textToAppend: string;

  constructor(private editor: TextEditor, text: string) {
    this.textToAppend = text;
  }

  execute(): void {
    this.editor.append(this.textToAppend);
  }

  undo(): void {
    this.editor.delete(this.textToAppend.length);
  }
}

class DeleteCommand implements Command {
  private deletedText: string = '';

  constructor(private editor: TextEditor, private length: number) {}

  execute(): void {
    const currentText = this.editor.getText();
    this.deletedText = currentText.slice(-this.length);
    this.editor.delete(this.length);
  }

  undo(): void {
    this.editor.append(this.deletedText);
  }
}

// インボーカー（コマンドを実行するクラス）
class CommandManager {
  private history: Command[] = [];
  private currentIndex: number = -1;

  execute(command: Command): void {
    // 現在位置以降の履歴を削除
    this.history = this.history.slice(0, this.currentIndex + 1);
    
    command.execute();
    this.history.push(command);
    this.currentIndex++;
  }

  undo(): void {
    if (this.currentIndex >= 0) {
      this.history[this.currentIndex].undo();
      this.currentIndex--;
    }
  }

  redo(): void {
    if (this.currentIndex < this.history.length - 1) {
      this.currentIndex++;
      this.history[this.currentIndex].execute();
    }
  }
}

// 使用例
const editor = new TextEditor();
const manager = new CommandManager();

manager.execute(new AppendCommand(editor, 'Hello '));
console.log(editor.getText()); // "Hello "

manager.execute(new AppendCommand(editor, 'World!'));
console.log(editor.getText()); // "Hello World!"

manager.undo();
console.log(editor.getText()); // "Hello "

manager.redo();
console.log(editor.getText()); // "Hello World!"
```

---

## アーキテクチャパターン

### 1. MVC (Model-View-Controller)

#### 目的
アプリケーションをモデル（データ）、ビュー（表示）、コントローラー（制御）に分離する。

#### 構造
```
┌──────────────┐
│     View     │ ← ユーザーインターフェース
└──────┬───────┘
       │
       ↓
┌──────────────┐
│  Controller  │ ← ユーザー入力を処理
└──────┬───────┘
       │
       ↓
┌──────────────┐
│    Model     │ ← ビジネスロジック・データ
└──────────────┘
```

#### 使用ケース
- Webアプリケーション
- デスクトップアプリケーション

---

### 2. MVVM (Model-View-ViewModel)

#### 目的
ビューとモデルの間にViewModelレイヤーを導入し、データバインディングを実現する。

#### 構造
```
┌──────────────┐
│     View     │ ← UI
└──────┬───────┘
       │ Data Binding
       ↓
┌──────────────┐
│  ViewModel   │ ← プレゼンテーションロジック
└──────┬───────┘
       │
       ↓
┌──────────────┐
│    Model     │ ← ビジネスロジック・データ
└──────────────┘
```

#### 使用ケース
- React (with hooks)
- Vue.js
- Angular
- モバイルアプリ (Flutter, React Native)

---

### 3. Repository Pattern (リポジトリパターン)

#### 目的
データアクセスロジックを抽象化し、ビジネスロジックとデータソースを分離する。

#### 実装例

```typescript
// エンティティ
interface User {
  id: string;
  name: string;
  email: string;
}

// リポジトリインターフェース
interface IUserRepository {
  findById(id: string): Promise<User | null>;
  findAll(): Promise<User[]>;
  create(user: Omit<User, 'id'>): Promise<User>;
  update(id: string, user: Partial<User>): Promise<User>;
  delete(id: string): Promise<void>;
}

// 具体的な実装（PostgreSQL）
class PostgresUserRepository implements IUserRepository {
  constructor(private db: any) {}

  async findById(id: string): Promise<User | null> {
    const result = await this.db.query(
      'SELECT * FROM users WHERE id = $1',
      [id]
    );
    return result.rows[0] || null;
  }

  async findAll(): Promise<User[]> {
    const result = await this.db.query('SELECT * FROM users');
    return result.rows;
  }

  async create(userData: Omit<User, 'id'>): Promise<User> {
    const result = await this.db.query(
      'INSERT INTO users (name, email) VALUES ($1, $2) RETURNING *',
      [userData.name, userData.email]
    );
    return result.rows[0];
  }

  async update(id: string, userData: Partial<User>): Promise<User> {
    // 更新ロジック
    return {} as User;
  }

  async delete(id: string): Promise<void> {
    await this.db.query('DELETE FROM users WHERE id = $1', [id]);
  }
}

// サービス層での使用
class UserService {
  constructor(private userRepository: IUserRepository) {}

  async getUser(id: string): Promise<User> {
    const user = await this.userRepository.findById(id);
    if (!user) {
      throw new Error('User not found');
    }
    return user;
  }

  async createUser(data: Omit<User, 'id'>): Promise<User> {
    // ビジネスロジック（バリデーション等）
    return this.userRepository.create(data);
  }
}
```

---

## クラウドデザインパターン

### 1. Circuit Breaker Pattern

#### 目的
障害が発生したサービスへの呼び出しを遮断し、システム全体の安定性を保つ。

#### 実装例

```typescript
enum CircuitState {
  CLOSED,  // 正常
  OPEN,    // 障害検知、リクエスト遮断
  HALF_OPEN // 回復試行中
}

class CircuitBreaker {
  private state: CircuitState = CircuitState.CLOSED;
  private failureCount: number = 0;
  private successCount: number = 0;
  private lastFailureTime: number = 0;

  constructor(
    private threshold: number = 5,        // 障害閾値
    private timeout: number = 60000,      // タイムアウト (1分)
    private resetTimeout: number = 30000  // リセットタイムアウト (30秒)
  ) {}

  async execute<T>(operation: () => Promise<T>): Promise<T> {
    if (this.state === CircuitState.OPEN) {
      if (Date.now() - this.lastFailureTime > this.resetTimeout) {
        this.state = CircuitState.HALF_OPEN;
        this.successCount = 0;
      } else {
        throw new Error('Circuit breaker is OPEN');
      }
    }

    try {
      const result = await operation();
      this.onSuccess();
      return result;
    } catch (error) {
      this.onFailure();
      throw error;
    }
  }

  private onSuccess(): void {
    this.failureCount = 0;
    
    if (this.state === CircuitState.HALF_OPEN) {
      this.successCount++;
      if (this.successCount >= this.threshold) {
        this.state = CircuitState.CLOSED;
      }
    }
  }

  private onFailure(): void {
    this.failureCount++;
    this.lastFailureTime = Date.now();
    
    if (this.failureCount >= this.threshold) {
      this.state = CircuitState.OPEN;
    }
  }

  getState(): CircuitState {
    return this.state;
  }
}

// 使用例
const breaker = new CircuitBreaker(3, 60000, 30000);

async function callExternalService() {
  return breaker.execute(async () => {
    // 外部サービス呼び出し
    const response = await fetch('https://api.example.com/data');
    if (!response.ok) throw new Error('Service unavailable');
    return response.json();
  });
}
```

---

### 2. Retry Pattern

#### 目的
一時的な障害に対して、操作を自動的にリトライする。

#### 実装例

```typescript
interface RetryOptions {
  maxAttempts: number;
  delay: number;
  backoffMultiplier?: number;
  maxDelay?: number;
}

async function retry<T>(
  operation: () => Promise<T>,
  options: RetryOptions
): Promise<T> {
  const {
    maxAttempts,
    delay,
    backoffMultiplier = 2,
    maxDelay = 30000
  } = options;

  let lastError: Error;

  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    try {
      return await operation();
    } catch (error) {
      lastError = error as Error;
      
      if (attempt === maxAttempts) {
        break;
      }

      // 指数バックオフ
      const waitTime = Math.min(
        delay * Math.pow(backoffMultiplier, attempt - 1),
        maxDelay
      );

      console.log(
        `Attempt ${attempt} failed. Retrying in ${waitTime}ms...`
      );
      
      await new Promise(resolve => setTimeout(resolve, waitTime));
    }
  }

  throw lastError!;
}

// 使用例
async function fetchData() {
  return retry(
    async () => {
      const response = await fetch('https://api.example.com/data');
      if (!response.ok) throw new Error('Failed to fetch');
      return response.json();
    },
    {
      maxAttempts: 3,
      delay: 1000,
      backoffMultiplier: 2
    }
  );
}
```

---

## マイクロサービスパターン

### 1. API Gateway Pattern

#### 目的
クライアントとマイクロサービス間の単一エントリーポイントを提供する。

#### 責務
- ルーティング
- 認証・認可
- レート制限
- リクエスト/レスポンス変換
- ロードバランシング

---

### 2. Saga Pattern

#### 目的
分散トランザクションを管理し、データの整合性を保つ。

#### 実装方法

**Choreography (コレオグラフィー)**:
- イベント駆動
- サービス間で直接イベントをやり取り

**Orchestration (オーケストレーション)**:
- 中央のオーケストレーターが制御
- ワークフロー管理

#### 実装例（Orchestration）

```typescript
interface SagaStep {
  execute(): Promise<void>;
  compensate(): Promise<void>;
}

class OrderSaga {
  private steps: SagaStep[] = [];
  private completedSteps: SagaStep[] = [];

  addStep(step: SagaStep): void {
    this.steps.push(step);
  }

  async execute(): Promise<void> {
    try {
      for (const step of this.steps) {
        await step.execute();
        this.completedSteps.push(step);
      }
    } catch (error) {
      // エラー発生時は補償処理を逆順で実行
      console.log('Saga failed, compensating...');
      await this.compensate();
      throw error;
    }
  }

  private async compensate(): Promise<void> {
    for (const step of this.completedSteps.reverse()) {
      try {
        await step.compensate();
      } catch (error) {
        console.error('Compensation failed:', error);
      }
    }
  }
}

// 具体的なステップ
class ReserveInventoryStep implements SagaStep {
  async execute(): Promise<void> {
    console.log('Reserving inventory...');
    // 在庫予約ロジック
  }

  async compensate(): Promise<void> {
    console.log('Canceling inventory reservation...');
    // 在庫予約キャンセル
  }
}

class ProcessPaymentStep implements SagaStep {
  async execute(): Promise<void> {
    console.log('Processing payment...');
    // 決済処理
  }

  async compensate(): Promise<void> {
    console.log('Refunding payment...');
    // 返金処理
  }
}

class CreateOrderStep implements SagaStep {
  async execute(): Promise<void> {
    console.log('Creating order...');
    // 注文作成
  }

  async compensate(): Promise<void> {
    console.log('Canceling order...');
    // 注文キャンセル
  }
}

// 使用例
const saga = new OrderSaga();
saga.addStep(new ReserveInventoryStep());
saga.addStep(new ProcessPaymentStep());
saga.addStep(new CreateOrderStep());

await saga.execute();
```

---

## 📚 参考資料

- [Design Patterns: Elements of Reusable Object-Oriented Software (Gang of Four)](https://www.amazon.com/dp/0201633612)
- [Patterns of Enterprise Application Architecture by Martin Fowler](https://martinfowler.com/books/eaa.html)
- [Cloud Design Patterns - Microsoft Azure](https://docs.microsoft.com/azure/architecture/patterns/)
- [Microservices Patterns by Chris Richardson](https://microservices.io/patterns/)
- [Refactoring Guru - Design Patterns](https://refactoring.guru/design-patterns)

---

## 変更履歴

| バージョン | 日付 | 変更者 | 変更内容 |
|----------|------|--------|---------|
| 1.0.0 | 2025-10-28 | Engineering Team | 初版作成 |

---

**保存先**: `/devin-organization-standards/09-reference/design-patterns.md`