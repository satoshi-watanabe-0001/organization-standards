# アーキテクチャ設計原則 / Architecture Design Principles

**最終更新日**: 2025-10-24  
**バージョン**: 1.0.0  
**対象**: アーキテクト、テクニカルリード、自律型AI Devin  
**適用範囲**: 全プロジェクト共通

---

## 📖 目次

1. [基本設計原則](#1-基本設計原則)
2. [SOLID原則](#2-solid原則)
3. [アーキテクチャパターン](#3-アーキテクチャパターン)
4. [ドメイン駆動設計（DDD）](#4-ドメイン駆動設計ddd)
5. [スケーラビリティ設計](#5-スケーラビリティ設計)
6. [パフォーマンス設計](#6-パフォーマンス設計)
7. [疎結合・高凝集](#7-疎結合高凝集)
8. [設計評価基準](#8-設計評価基準)

---

## 1. 基本設計原則

### 1.1 KISS原則（Keep It Simple, Stupid）

**定義**: シンプルさを保つことが最優先

**実践方法**:
- ✅ 必要最小限の機能から始める
- ✅ 複雑な設計は分割して理解しやすくする
- ✅ 過剰な抽象化を避ける
- ❌ 将来使うかもしれない機能を先に実装しない

**良い例**:
```typescript
// シンプルで明確
class UserService {
  createUser(data: UserData): User {
    return this.repository.save(data);
  }
}
```

**悪い例**:
```typescript
// 過剰な抽象化
class AbstractUserServiceFactoryBuilderProvider {
  createFactoryBuilderForUserServiceProvider() {
    // 複雑すぎる
  }
}
```

---

### 1.2 YAGNI原則（You Aren't Gonna Need It）

**定義**: 今必要でない機能は実装しない

**実践方法**:
- ✅ 現在の要件に集中する
- ✅ 将来の拡張性は必要になった時に追加
- ✅ 推測による設計を避ける
- ❌ 「将来必要になるかも」という理由で機能を追加しない

**判断基準**:
```
機能追加の判断フローチャート:

今必要か？
  ├─ YES → 実装する
  └─ NO → 要件が確定するまで待つ
```

---

### 1.3 DRY原則（Don't Repeat Yourself）

**定義**: 同じコードや知識を重複させない

**実践方法**:
- ✅ 共通ロジックを関数・クラスに抽出
- ✅ 設定情報を一元管理
- ✅ コードジェネレーターやテンプレートの活用
- ❌ コピー&ペーストによる重複

**良い例**:
```typescript
// 共通ロジックを抽出
function validateEmail(email: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

// 各所で再利用
userService.validateEmail(input);
adminService.validateEmail(adminInput);
```

**悪い例**:
```typescript
// 同じバリデーションを重複記述
// UserServiceで
if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) { ... }
// AdminServiceでも同じコード
if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) { ... }
```

---

### 1.4 関心の分離（Separation of Concerns）

**定義**: 異なる責務を明確に分離する

**実践方法**:
- ✅ レイヤードアーキテクチャの採用
- ✅ 各レイヤーの責務を明確化
- ✅ ビジネスロジックとインフラロジックの分離
- ❌ すべての処理を1つのファイルやクラスに記述

**レイヤー構成例**:
```
┌─────────────────────────┐
│  Presentation Layer     │  ← UI、コントローラー
├─────────────────────────┤
│  Application Layer      │  ← ユースケース、オーケストレーション
├─────────────────────────┤
│  Domain Layer          │  ← ビジネスロジック、エンティティ
├─────────────────────────┤
│  Infrastructure Layer   │  ← DB、外部API、ファイルI/O
└─────────────────────────┘
```

---

## 2. SOLID原則

### 2.1 単一責任の原則（Single Responsibility Principle）

**定義**: 1つのクラスは1つの責任だけを持つべき

**実践方法**:
```typescript
// ❌ 悪い例：複数の責任を持つクラス
class User {
  name: string;
  email: string;
  
  save() { /* DB保存 */ }
  sendEmail() { /* メール送信 */ }
  generateReport() { /* レポート生成 */ }
}

// ✅ 良い例：責任を分離
class User {
  name: string;
  email: string;
}

class UserRepository {
  save(user: User) { /* DB保存 */ }
}

class EmailService {
  send(user: User, message: string) { /* メール送信 */ }
}

class ReportGenerator {
  generate(user: User) { /* レポート生成 */ }
}
```

**判断基準**: 「このクラスが変更される理由は1つか?」

---

### 2.2 オープン・クローズドの原則（Open/Closed Principle）

**定義**: 拡張に対して開いており、修正に対して閉じているべき

**実践方法**:
```typescript
// ❌ 悪い例：新しい支払い方法を追加するたびに修正が必要
class PaymentProcessor {
  process(type: string, amount: number) {
    if (type === 'credit') {
      // クレジット処理
    } else if (type === 'paypal') {
      // PayPal処理
    }
    // 新しい支払い方法を追加するたびに修正が必要
  }
}

// ✅ 良い例：拡張可能な設計
interface PaymentMethod {
  process(amount: number): void;
}

class CreditCardPayment implements PaymentMethod {
  process(amount: number) { /* クレジット処理 */ }
}

class PayPalPayment implements PaymentMethod {
  process(amount: number) { /* PayPal処理 */ }
}

class PaymentProcessor {
  process(method: PaymentMethod, amount: number) {
    method.process(amount); // 既存コードを変更せずに拡張可能
  }
}
```

---

### 2.3 リスコフの置換原則（Liskov Substitution Principle）

**定義**: サブクラスは基底クラスと置き換え可能であるべき

**実践方法**:
```typescript
// ✅ 良い例：正しい継承関係
class Bird {
  fly() { /* 飛ぶ */ }
}

class Sparrow extends Bird {
  fly() { /* スズメとして飛ぶ */ }
}

// ❌ 悪い例：ペンギンは飛べない（LSP違反）
class Penguin extends Bird {
  fly() {
    throw new Error('ペンギンは飛べません');
  }
}

// ✅ 改善：インターフェースで分離
interface Animal {
  move(): void;
}

interface Flyable {
  fly(): void;
}

class Sparrow implements Animal, Flyable {
  move() { this.fly(); }
  fly() { /* 飛ぶ */ }
}

class Penguin implements Animal {
  move() { /* 歩く */ }
  // flyメソッドは持たない
}
```

---

### 2.4 インターフェース分離の原則（Interface Segregation Principle）

**定義**: クライアントは使わないメソッドへの依存を強制されるべきでない

**実践方法**:
```typescript
// ❌ 悪い例：巨大なインターフェース
interface Worker {
  work(): void;
  eat(): void;
  sleep(): void;
  code(): void;
}

class Robot implements Worker {
  work() { /* 作業 */ }
  eat() { throw new Error('ロボットは食べない'); }
  sleep() { throw new Error('ロボットは寝ない'); }
  code() { /* コーディング */ }
}

// ✅ 良い例：インターフェースを分離
interface Workable {
  work(): void;
}

interface Eatable {
  eat(): void;
}

interface Sleepable {
  sleep(): void;
}

class Human implements Workable, Eatable, Sleepable {
  work() { /* 作業 */ }
  eat() { /* 食べる */ }
  sleep() { /* 寝る */ }
}

class Robot implements Workable {
  work() { /* 作業 */ }
  // eat、sleepは実装不要
}
```

---

### 2.5 依存性逆転の原則（Dependency Inversion Principle）

**定義**: 高レベルモジュールは低レベルモジュールに依存すべきでない。両者は抽象に依存すべき

**実践方法**:
```typescript
// ❌ 悪い例：具象クラスに依存
class MySQLDatabase {
  save(data: any) { /* MySQL保存 */ }
}

class UserService {
  private db = new MySQLDatabase(); // 具象クラスに依存
  
  saveUser(user: User) {
    this.db.save(user);
  }
}

// ✅ 良い例：抽象に依存
interface Database {
  save(data: any): void;
}

class MySQLDatabase implements Database {
  save(data: any) { /* MySQL保存 */ }
}

class PostgreSQLDatabase implements Database {
  save(data: any) { /* PostgreSQL保存 */ }
}

class UserService {
  constructor(private db: Database) {} // 抽象に依存
  
  saveUser(user: User) {
    this.db.save(user);
  }
}

// 依存性注入
const userService = new UserService(new MySQLDatabase());
// または
const userService = new UserService(new PostgreSQLDatabase());
```

---

## 3. アーキテクチャパターン

### 3.1 レイヤードアーキテクチャ（Layered Architecture）

**概要**: システムを複数の層に分割し、各層は隣接する層のみと通信

**構成**:
```
┌─────────────────────────┐
│  Presentation Layer     │ ← ユーザーインターフェース
│  (Controllers, Views)   │
└────────┬────────────────┘
         │
┌────────▼────────────────┐
│  Application Layer      │ ← アプリケーションロジック
│  (Use Cases, Services)  │
└────────┬────────────────┘
         │
┌────────▼────────────────┐
│  Domain Layer          │ ← ビジネスロジック
│  (Entities, Value Obj) │
└────────┬────────────────┘
         │
┌────────▼────────────────┐
│  Infrastructure Layer   │ ← 技術的実装
│  (DB, API, File I/O)   │
└─────────────────────────┘
```

**適用シーン**:
- 中小規模のWebアプリケーション
- 明確な責務分離が必要なシステム
- チーム間での並行開発

**メリット**:
- ✅ 理解しやすい構造
- ✅ レイヤー単位でのテストが容易
- ✅ 技術スタックの変更が局所化

**デメリット**:
- ❌ 層を跨ぐ変更が複雑
- ❌ パフォーマンスオーバーヘッド（多層通過）

---

### 3.2 クリーンアーキテクチャ（Clean Architecture）

**概要**: ビジネスロジックを中心に、外側に向かって依存関係を持つ

**構成**:
```
        ┌──────────────────┐
        │   Frameworks &   │
        │     Drivers      │
        │  (UI, DB, Web)   │
        └────────┬─────────┘
                 │
        ┌────────▼─────────┐
        │  Interface       │
        │  Adapters        │
        │ (Controllers,    │
        │  Presenters)     │
        └────────┬─────────┘
                 │
        ┌────────▼─────────┐
        │  Use Cases       │
        │ (Application     │
        │  Business Rules) │
        └────────┬─────────┘
                 │
        ┌────────▼─────────┐
        │   Entities       │
        │  (Enterprise     │
        │ Business Rules)  │
        └──────────────────┘
```

**依存関係のルール**: 内側の層は外側の層を知らない

**適用シーン**:
- 長期的な保守が必要なシステム
- ビジネスロジックが複雑なシステム
- フレームワーク依存を最小化したい場合

**メリット**:
- ✅ テスタビリティが非常に高い
- ✅ フレームワークやDBの変更が容易
- ✅ ビジネスロジックが明確

**デメリット**:
- ❌ 初期の学習コストが高い
- ❌ 小規模プロジェクトでは過剰

---

### 3.3 ヘキサゴナルアーキテクチャ（Hexagonal Architecture / Ports & Adapters）

**概要**: アプリケーションコアを中心に、外部とのやり取りはポート経由

**構成**:
```
        ┌─────────────┐
        │   Adapter   │
        │    (UI)     │
        └──────┬──────┘
               │
        ┌──────▼──────┐
        │    Port     │
        │  (Interface)│
        └──────┬──────┘
               │
      ┌────────▼────────┐
      │  Application    │
      │      Core       │
      │ (Business Logic)│
      └────────┬────────┘
               │
        ┌──────▼──────┐
        │    Port     │
        │  (Interface)│
        └──────┬──────┘
               │
        ┌──────▼──────┐
        │   Adapter   │
        │    (DB)     │
        └─────────────┘
```

**ポートとアダプター**:
- **ポート**: アプリケーションが外部と通信するためのインターフェース
- **アダプター**: ポートの具体的な実装

**実装例**:
```typescript
// ポート（インターフェース）
interface UserRepository {
  findById(id: string): Promise<User>;
  save(user: User): Promise<void>;
}

// アダプター（具体的実装）
class MySQLUserRepository implements UserRepository {
  async findById(id: string): Promise<User> {
    // MySQL実装
  }
  async save(user: User): Promise<void> {
    // MySQL実装
  }
}

// アプリケーションコア（ビジネスロジック）
class UserService {
  constructor(private repository: UserRepository) {} // ポートに依存
  
  async activateUser(id: string) {
    const user = await this.repository.findById(id);
    user.activate();
    await this.repository.save(user);
  }
}
```

**適用シーン**:
- 外部システムとの連携が多いシステム
- テスト駆動開発（TDD）を実践する場合
- 技術スタックの変更を容易にしたい場合

---

### 3.4 マイクロサービスアーキテクチャ

**概要**: システムを小さな独立したサービスに分割

**詳細**: `microservices-guidelines.md` を参照

**基本原則**:
- 各サービスは独立してデプロイ可能
- サービス間通信はAPIまたはメッセージキュー経由
- 各サービスは独自のデータベースを持つ

---

## 4. ドメイン駆動設計（DDD）

### 4.1 DDD基本概念

**エンティティ（Entity）**:
- 識別子（ID）を持つオブジェクト
- ライフサイクルを通じて追跡される

```typescript
class User {
  constructor(
    private readonly id: UserId, // 識別子
    private name: string,
    private email: string
  ) {}
  
  changeName(newName: string) {
    this.name = newName;
  }
}
```

**値オブジェクト（Value Object）**:
- 識別子を持たない
- 不変（Immutable）
- 等価性は属性で判断

```typescript
class Email {
  private constructor(private readonly value: string) {
    if (!this.isValid(value)) {
      throw new Error('無効なメールアドレス');
    }
  }
  
  static create(value: string): Email {
    return new Email(value);
  }
  
  private isValid(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }
  
  equals(other: Email): boolean {
    return this.value === other.value;
  }
}
```

**集約（Aggregate）**:
- 関連するエンティティと値オブジェクトのまとまり
- 集約ルート（Aggregate Root）を通じてのみアクセス可能

```typescript
class Order { // 集約ルート
  private items: OrderItem[] = [];
  
  addItem(product: Product, quantity: number) {
    const item = new OrderItem(product, quantity);
    this.items.push(item);
  }
  
  // 外部からは集約ルート経由でのみアクセス
}

class OrderItem { // 集約内のエンティティ
  constructor(
    private product: Product,
    private quantity: number
  ) {}
}
```

---

### 4.2 境界づけられたコンテキスト（Bounded Context）

**概要**: ドメインモデルが適用される明確な境界

**例**:
```
┌─────────────────────┐    ┌─────────────────────┐
│  販売コンテキスト    │    │  配送コンテキスト    │
│                     │    │                     │
│  Customer (顧客)    │    │  Recipient (受取人) │
│  - 購買履歴         │    │  - 配送先住所       │
│  - クレジット情報   │    │  - 配送状況         │
└─────────────────────┘    └─────────────────────┘
```

同じ「顧客」でも、コンテキストによって異なる属性と責務を持つ

---

### 4.3 ユビキタス言語（Ubiquitous Language）

**定義**: ドメインエキスパートと開発者が共有する共通言語

**実践方法**:
- ✅ コード内の命名にドメイン用語を使用
- ✅ ドキュメントとコードで用語を統一
- ✅ チーム全体で用語集を管理

**例**:
```typescript
// ✅ 良い例：ドメイン用語を使用
class Order {
  ship() { /* 出荷 */ }
  cancel() { /* キャンセル */ }
}

// ❌ 悪い例：技術用語のみ
class OrderData {
  updateStatus() { /* ステータス更新 */ }
}
```

---

## 5. スケーラビリティ設計

### 5.1 水平スケーリング vs 垂直スケーリング

**垂直スケーリング（Scale Up）**:
- サーバーのスペックを向上（CPU、メモリ増強）
- **メリット**: 実装が簡単
- **デメリット**: 物理的限界あり、単一障害点

**水平スケーリング（Scale Out）**:
- サーバー台数を増やす
- **メリット**: 理論上無限にスケール可能、冗長性確保
- **デメリット**: 設計が複雑、状態管理が難しい

**推奨**: 初期は垂直、成長に応じて水平スケーリング

---

### 5.2 ステートレス設計

**原則**: サーバーがセッション状態を保持しない

**実装方法**:
```typescript
// ✅ 良い例：ステートレス
app.get('/user/:id', (req, res) => {
  const token = req.headers.authorization;
  const user = await getUserFromToken(token);
  res.json(user);
});

// ❌ 悪い例：ステートフル
let currentUser; // サーバー側で状態保持
app.get('/user', (req, res) => {
  res.json(currentUser); // どのサーバーにルーティングされるか依存
});
```

**メリット**:
- ロードバランサーでの分散が容易
- サーバー障害時の影響が小さい
- 水平スケーリングが容易

---

### 5.3 キャッシング戦略

**適用箇所**:
```
┌─────────┐    ┌─────────┐    ┌─────────┐
│ Browser │───>│  CDN    │───>│ Server  │
│ Cache   │    │ Cache   │    │ Cache   │
└─────────┘    └─────────┘    └─────────┘
                                    │
                              ┌─────▼─────┐
                              │ Database  │
                              └───────────┘
```

**キャッシュレベル**:
1. **ブラウザキャッシュ**: 静的リソース
2. **CDNキャッシュ**: グローバル配信
3. **アプリケーションキャッシュ**: Redis、Memcached
4. **データベースキャッシュ**: クエリキャッシュ

---

## 6. パフォーマンス設計

### 6.1 パフォーマンス目標設定

**レスポンスタイム基準**:
- **Excellent**: < 100ms
- **Good**: 100ms - 300ms
- **Acceptable**: 300ms - 1000ms
- **Needs Improvement**: > 1000ms

**スループット基準**:
- プロジェクト要件に応じて設定
- 例: 1000リクエスト/秒

---

### 6.2 パフォーマンス最適化戦略

**データベース最適化**:
- ✅ 適切なインデックス設定
- ✅ N+1問題の回避
- ✅ ページネーション実装
- ✅ クエリの最適化

**フロントエンド最適化**:
- ✅ コード分割（Code Splitting）
- ✅ 遅延読み込み（Lazy Loading）
- ✅ 画像最適化
- ✅ バンドルサイズ削減

**ネットワーク最適化**:
- ✅ HTTP/2の活用
- ✅ gzip圧縮
- ✅ CDN活用
- ✅ リクエスト数削減

---

## 7. 疎結合・高凝集

### 7.1 疎結合（Low Coupling）

**定義**: モジュール間の依存関係を最小化

**実現方法**:
- インターフェースの活用
- 依存性注入（DI）
- イベント駆動アーキテクチャ

```typescript
// ✅ 疎結合：インターフェース経由
interface EmailService {
  send(to: string, message: string): void;
}

class UserService {
  constructor(private emailService: EmailService) {}
  
  registerUser(user: User) {
    // 登録処理
    this.emailService.send(user.email, 'Welcome!');
  }
}
```

---

### 7.2 高凝集（High Cohesion）

**定義**: 関連する機能を1つのモジュールにまとめる

**実現方法**:
```typescript
// ✅ 高凝集：注文関連の処理をまとめる
class OrderService {
  createOrder(items: OrderItem[]) { }
  cancelOrder(orderId: string) { }
  getOrderStatus(orderId: string) { }
}

// ❌ 低凝集：無関係な処理が混在
class MixedService {
  createOrder() { }
  sendEmail() { }
  calculateTax() { }
  logError() { }
}
```

---

## 8. 設計評価基準

### 8.1 設計品質チェックリスト

**基本原則**:
- [ ] KISS原則に従っているか
- [ ] YAGNI原則に従っているか
- [ ] DRY原則に従っているか
- [ ] 関心の分離ができているか

**SOLID原則**:
- [ ] 単一責任の原則を満たしているか
- [ ] オープン・クローズドの原則を満たしているか
- [ ] リスコフの置換原則を満たしているか
- [ ] インターフェース分離の原則を満たしているか
- [ ] 依存性逆転の原則を満たしているか

**アーキテクチャ**:
- [ ] 適切なアーキテクチャパターンを選択しているか
- [ ] レイヤー間の依存関係は適切か
- [ ] スケーラビリティを考慮しているか
- [ ] パフォーマンス要件を満たしているか

**保守性**:
- [ ] コードは理解しやすいか
- [ ] テストは書きやすいか
- [ ] 将来の変更に柔軟に対応できるか
- [ ] ドキュメントは十分か

---

### 8.2 設計レビュー観点

**アーキテクチャレビュー**:
1. ビジネス要件を満たしているか
2. 非機能要件（性能、可用性等）を考慮しているか
3. セキュリティリスクは評価されているか
4. 技術負債の発生リスクは低いか

**コードレビュー**:
1. 設計原則に準拠しているか
2. 適切な抽象化レベルか
3. テストカバレッジは十分か
4. ドキュメントは適切か

---

## 📚 参考資料

- **書籍**:
  - "Clean Architecture" by Robert C. Martin
  - "Domain-Driven Design" by Eric Evans
  - "Design Patterns" by Gang of Four
  
- **オンラインリソース**:
  - [Martin Fowler's Blog](https://martinfowler.com/)
  - [The Twelve-Factor App](https://12factor.net/)
  - [Microsoft Architecture Guide](https://docs.microsoft.com/en-us/azure/architecture/)

---

## 次のステップ

1. プロジェクトの規模と特性を評価
2. 適切なアーキテクチャパターンを選択
3. 各レイヤーの詳細設計を実施
4. `api-architecture.md`、`database-design.md`等の専門ドキュメントを参照

---

**関連ドキュメント**:
- `/02-architecture-standards/README.md` - セクション概要
- `/02-architecture-standards/microservices-guidelines.md` - マイクロサービス設計
- `/01-coding-standards/00-general-principles.md` - コーディング基本原則
