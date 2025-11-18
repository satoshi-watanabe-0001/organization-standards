# TypeScript インラインコメント実装例

## 📖 概要

本ドキュメントは、TypeScript固有のインラインコメント記述の具体例を提供します。

**共通原則**: [00-inline-comment-standards.md](../00-inline-comment-standards.md)を必ず参照してください。

---

## 🎯 TypeScript特有のコメント場面

### 1. 型アサーション・型ガード

```typescript
// ❌ 悪い例: 型アサーションの理由が不明
const user = data as User;

// ✅ 良い例: なぜ型アサーションが必要かを説明
// 型アサーションの理由:
// 外部APIのレスポンス型は any だが、APIドキュメントでUser型が保証されている
// ランタイムバリデーションは別途実施済み（validateUserResponse関数）
const user = data as User;
```

---

### 2. Optional Chaining / Nullish Coalescing

```typescript
// ❌ 悪い例: なぜオプショナルチェーンを使うか不明
const email = user?.profile?.email;

// ✅ 良い例: nullチェックの理由を説明
// profileは未設定の可能性あり（新規登録直後のユーザー）
// emailも任意項目のため、安全にアクセス
// 理由: NPE防止、ゲストユーザー対応
const email = user?.profile?.email ?? 'guest@example.com';
```

---

### 3. 型推論 vs 明示的型定義

```typescript
// ❌ 悪い例: なぜ型を明示するか不明
const users: User[] = await fetchUsers();

// ✅ 良い例: 型を明示する理由を説明
// 型推論では Promise<any> になってしまうため、明示的に User[] を指定
// 理由: fetchUsers の戻り値が any であり、型安全性を確保するため
const users: User[] = await fetchUsers();
```

---

## 🔢 複雑度が高いコード例

### 例1: 複雑な条件分岐（複雑度12）

```typescript
/**
 * 配送料を決定
 */
function determineShippingFee(
  order: Order,
  customer: Customer,
  destination: Address
): number {
  let baseFee = 500; // 基本配送料
  
  // 配送料計算の複雑なビジネスルール
  // 理由: 複数の割引条件が組み合わさる（マーケティング戦略）
  
  // 条件1: プレミアム会員は送料無料
  // ビジネス要件: 会員特典としてリピート購入を促進
  if (customer.isPremiumMember) {
    return 0;
  }
  
  // 条件2: 購入金額が5000円以上で送料無料
  // ビジネス要件: 客単価向上のため（平均購入額を引き上げる）
  if (order.totalAmount >= 5000) {
    return 0;
  }
  
  // 条件3: 離島・遠隔地は追加料金
  // 理由: 運送会社の追加料金を転嫁（コスト回収）
  if (destination.isRemoteArea) {
    baseFee += 300;
  }
  
  // 条件4: 大型商品は追加料金
  // 理由: 大型商品は特別配送が必要（サイズ制限）
  if (order.hasLargeItems) {
    baseFee += 500;
  }
  
  // 条件5: 初回購入者は半額
  // ビジネス要件: 新規顧客獲得キャンペーン（2025年Q4期間限定）
  if (customer.isFirstTimeBuyer && new Date() < new Date('2025-12-31')) {
    baseFee = Math.floor(baseFee / 2);
  }
  
  // 条件6: クーポン適用
  // 理由: マーケティングキャンペーンでの送料割引
  if (order.hasShippingCoupon) {
    const discount = order.shippingCoupon.discountAmount;
    // クーポン割引額が配送料を超える場合は0円（負の値にしない）
    baseFee = Math.max(0, baseFee - discount);
  }
  
  return baseFee;
}
```

---

## 🧩 TypeScript特有の記法のコメント

### 1. ジェネリクス

```typescript
// ❌ 悪い例: ジェネリクスの制約理由が不明
function filterList<T>(list: T[], predicate: (item: T) => boolean): T[] {
  return list.filter(predicate);
}

// ✅ 良い例: ジェネリクス使用の理由を説明
/**
 * リストをフィルタリング
 * 
 * ジェネリクスを使用する理由:
 * - 型安全性を保ちながら、任意の型の配列に対応
 * - User, Order, Product等、様々なエンティティで再利用可能
 * 
 * @param list フィルタ対象のリスト
 * @param predicate フィルタ条件
 * @returns フィルタ結果
 */
function filterList<T>(list: T[], predicate: (item: T) => boolean): T[] {
  return list.filter(predicate);
}
```

---

### 2. Union型・Intersection型

```typescript
// ❌ 悪い例: Union型の使用理由が不明
type Response = SuccessResponse | ErrorResponse;

// ✅ 良い例: Union型の目的を説明
/**
 * APIレスポンス型
 * 
 * Union型を使用する理由:
 * - APIは成功時と失敗時で異なる構造のデータを返す
 * - 型ガードで分岐することで、型安全なエラーハンドリングが可能
 * - 成功時: { status: 'success', data: T }
 * - 失敗時: { status: 'error', error: string }
 */
type ApiResponse<T> = 
  | { status: 'success'; data: T }
  | { status: 'error'; error: string };
```

---

### 3. Utility Types

```typescript
// ❌ 悪い例: Utility型の選定理由が不明
type PartialUser = Partial<User>;

// ✅ 良い例: Partial使用の理由を説明
/**
 * ユーザー更新リクエスト型
 * 
 * Partial<User>を使用する理由:
 * - 更新APIでは、すべてのフィールドが任意（部分更新）
 * - Userの全プロパティを手動でOptionalにすると、保守性が悪化
 * - Userの定義変更に自動追従できる
 */
type UserUpdateRequest = Partial<User>;
```

---

## 🔧 マジックナンバー・定数

```typescript
// ❌ 悪い例: 数値の意味が不明
if (user.loginAttempts >= 5) {
  lockAccount(user);
}

// ✅ 良い例: 定数化 + 理由を説明
// ログイン試行回数の上限
// ビジネスルール: 5回失敗でアカウントロック（ブルートフォース攻撃対策）
// セキュリティ要件: [REQ-SEC-006]
const MAX_LOGIN_ATTEMPTS = 5;

if (user.loginAttempts >= MAX_LOGIN_ATTEMPTS) {
  lockAccount(user);
}
```

---

## 🧪 JSDoc vs インラインコメント

### JSDocの役割

```typescript
/**
 * 顧客向けの割引額を計算
 * 
 * @param price - 商品価格（税抜）
 * @param customer - 顧客情報（会員ランク、購入履歴を含む）
 * @returns 割引額（円）
 * @throws {Error} 価格が0以下の場合
 * 
 * @example
 * ```typescript
 * const customer = { rank: 'gold', totalPurchases: 100000 };
 * const discount = calculateDiscount(10000, customer);
 * // => 1500  // ゴールド会員: 15%割引
 * ```
 */
function calculateDiscount(price: number, customer: Customer): number {
  if (price <= 0) {
    throw new Error('価格は正の値である必要があります');
  }
  
  // ここからインラインコメント開始
  // 会員ランクに応じた割引率
  // ビジネスルール: ゴールド15%, シルバー10%, ブロンズ5%
  let discountRate = 0;
  switch (customer.rank) {
    case 'gold':
      discountRate = 0.15;
      break;
    case 'silver':
      discountRate = 0.10;
      break;
    case 'bronze':
      discountRate = 0.05;
      break;
  }
  
  // 累計購入額ボーナス（10万円以上でさらに5%）
  // 理由: 優良顧客へのロイヤルティプログラム
  if (customer.totalPurchases >= 100000) {
    discountRate += 0.05;
  }
  
  return price * discountRate;
}
```

---

## 🔍 デバッグ・開発用コメント

### TODO/FIXME/HACK

```typescript
// TODO: [担当: 山田] [期限: 2025-12-01] Redis接続エラー時のフォールバック処理を実装
// 現在はRedis障害時にアプリケーション全体が停止する
// 対応方針: ローカルキャッシュへのフォールバック
async function getCachedData(key: string): Promise<string | null> {
  return await redisClient.get(key);
}

// FIXME: [Issue #1234] ユニコードサロゲートペアで文字化けが発生
// 再現条件: 絵文字「👨‍👩‍👧‍👦」を含む名前で保存すると、取得時に破損
// 暫定対応: 絵文字を除去（根本対応は別途検討）
function sanitizeName(name: string): string {
  return name.replace(/[\u{1F600}-\u{1F64F}]/gu, '');
}

// HACK: 本来はTypeORMのQueryBuilderを使うべきだが、パフォーマンス問題により生SQL使用
// 背景: ORMのN+1問題で応答時間が5秒→30秒に悪化
// リスク: SQLインジェクションの可能性（パラメータバインディングで対策済み）
// TODO: ORMのrelationsで最適化できるか検証（次スプリント）
const users = await connection.query(
  'SELECT * FROM users WHERE department_id = $1',
  [departmentId]
);
```

---

## 🚫 避けるべきパターン

### 1. コードの繰り返し

```typescript
// ❌ 悪い例: コードを日本語に翻訳しただけ
// ユーザーのメールアドレスを取得
const email = user.email;
// メールアドレスがnullでないかチェック
if (email) {
  // メールを送信
  sendEmail(email);
}

// ✅ 良い例: WHYを説明
// メールアドレスが未設定のユーザーには送信しない
// 理由: 無効なアドレスへの送信は、送信サービスの信頼度スコアを下げる
const email = user.email;
if (email) {
  sendEmail(email);
}
```

---

## ✅ レビューチェックリスト

TypeScriptコードレビュー時に確認:

- [ ] 型アサーションに理由が記載されている
- [ ] 複雑な型定義に説明がある
- [ ] 複雑度10以上の関数に詳細コメントがある
- [ ] マジックナンバーが定数化され、理由が記載されている
- [ ] TODO/FIXME/HACKに担当者・期限・理由がある
- [ ] すべてのコメントが日本語で記述されている

---

## 🔗 関連ドキュメント

- [00-inline-comment-standards.md](../00-inline-comment-standards.md) - 共通原則
- [typescript/test-comment-examples.md](test-comment-examples.md) - TypeScriptテストコメント例
- [typescript-coding-standards.md](typescript-coding-standards.md) - TypeScript全体のコーディング規約
