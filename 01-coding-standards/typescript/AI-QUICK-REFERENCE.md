# AI クイックリファレンス - TypeScript/JavaScript

> **AI開発アシスタント向け必須チェックリスト**  
> コード生成・レビュー時に最優先で確認すべき項目TOP25

## 🎯 使い方

このドキュメントは、AIアシスタント（Devin、Copilot等）がTypeScript/JavaScriptコードを生成・レビューする際に、**最初に確認すべき重要項目**をまとめたものです。各項目には詳細ドキュメントへのリンクが付いています。

---

## ✅ TOP 25 必須チェック項目

### 🔷 型安全性（Type Safety）- 最優先

#### 1. ❌ `any`型の使用禁止
```typescript
// ❌ NG
const data: any = fetchData();

// ✅ OK
const data: unknown = fetchData();
const data: UserResponse = fetchData();
```
📖 **詳細**: [02-language-syntax.md](02-language-syntax.md#型アノテーション必須化)

#### 2. ✅ strictモード必須
```json
// tsconfig.json
{
  "compilerOptions": {
    "strict": true
  }
}
```
📖 **詳細**: [01-introduction-setup.md](01-introduction-setup.md#基本設定)

#### 3. ✅ 型アノテーション明示
```typescript
// ❌ NG - 推論に頼る
const name = getName();

// ✅ OK
const name: string = getName();
```
📖 **詳細**: [02-language-syntax.md](02-language-syntax.md#型アノテーション)

#### 4. ✅ nullチェックの実装
```typescript
// ✅ OK
if (user?.profile?.email) {
  sendEmail(user.profile.email);
}
```
📖 **詳細**: [02-language-syntax.md](02-language-syntax.md#オプショナルチェイニング)

---

### 🔶 命名規則（Naming Conventions）

#### 5. ✅ PascalCase: 型・クラス・コンポーネント
```typescript
// ✅ OK
interface UserProfile { }
class UserService { }
const UserCard: React.FC = () => { };
```
📖 **詳細**: [03-naming-typing.md](03-naming-typing.md#命名規則)

#### 6. ✅ camelCase: 変数・関数
```typescript
// ✅ OK
const userName = 'John';
function getUserData() { }
```
📖 **詳細**: [03-naming-typing.md](03-naming-typing.md#camelCase)

#### 7. ✅ UPPER_SNAKE_CASE: 定数
```typescript
// ✅ OK
const MAX_RETRY_COUNT = 3;
const API_BASE_URL = 'https://api.example.com';
```
📖 **詳細**: [03-naming-typing.md](03-naming-typing.md#定数)

---

### 🔵 React規約（React Specific）

#### 8. ✅ Functional Component必須
```typescript
// ✅ OK
const UserCard: React.FC<UserCardProps> = ({ user }) => {
  return <div>{user.name}</div>;
};
```
📖 **詳細**: [04-react-frontend.md](04-react-frontend.md#コンポーネント設計)

#### 9. ✅ useCallback/useMemoの適切な使用
```typescript
// ✅ OK - 重い計算のメモ化
const expensiveValue = useMemo(() => 
  computeExpensiveValue(data), [data]
);

// ✅ OK - 子コンポーネントへの関数Props
const handleClick = useCallback(() => {
  doSomething();
}, []);
```
📖 **詳細**: [04-react-frontend.md](04-react-frontend.md#パフォーマンス最適化)

#### 10. ✅ useEffect依存配列の完全性
```typescript
// ✅ OK
useEffect(() => {
  fetchData(userId);
}, [userId]); // userIdを含める
```
📖 **詳細**: [04-react-frontend.md](04-react-frontend.md#hooks)

#### 11. ✅ Propsの型定義
```typescript
// ✅ OK
interface UserCardProps {
  user: User;
  onEdit?: (id: string) => void;
}
```
📖 **詳細**: [04-react-frontend.md](04-react-frontend.md#型定義)

---

### 🔴 Node.js規約（Node.js Specific）

#### 12. ✅ async/await必須（.then()禁止）
```typescript
// ❌ NG
getData().then(data => process(data));

// ✅ OK
const data = await getData();
process(data);
```
📖 **詳細**: [05-nodejs-backend.md](05-nodejs-backend.md#非同期処理)

#### 13. ✅ try-catchでエラーハンドリング
```typescript
// ✅ OK
try {
  const result = await riskyOperation();
} catch (error) {
  logger.error('Operation failed', error);
  throw new AppError('Failed to process', 500);
}
```
📖 **詳細**: [05-nodejs-backend.md](05-nodejs-backend.md#エラーハンドリング)

#### 14. ✅ 環境変数の型定義
```typescript
// ✅ OK
interface EnvConfig {
  DATABASE_URL: string;
  PORT: number;
}

const config: EnvConfig = {
  DATABASE_URL: process.env.DATABASE_URL!,
  PORT: Number(process.env.PORT) || 3000,
};
```
📖 **詳細**: [05-nodejs-backend.md](05-nodejs-backend.md#環境変数)

---

### 🟢 テスト（Testing）

#### 15. ✅ ユニットテストのカバレッジ80%以上
```typescript
// ✅ OK
describe('UserService', () => {
  it('should create user with valid data', async () => {
    const user = await userService.create(validData);
    expect(user.id).toBeDefined();
  });
});
```
📖 **詳細**: [06-testing.md](06-testing.md#カバレッジ目標)

#### 16. ✅ テストの命名規則
```typescript
// ✅ OK
it('should return user data when valid ID is provided', () => {
  // Arrange, Act, Assert
});
```
📖 **詳細**: [06-testing.md](06-testing.md#命名規則)

#### 17. ✅ モック・スタブの適切な使用
```typescript
// ✅ OK
jest.mock('./api/userApi');
const mockGetUser = getUserApi as jest.MockedFunction<typeof getUserApi>;
```
📖 **詳細**: [06-testing.md](06-testing.md#モック)

---

### 🟡 パフォーマンス（Performance）

#### 18. ✅ 遅延読み込み（Lazy Loading）
```typescript
// ✅ OK
const AdminPanel = lazy(() => import('./AdminPanel'));
```
📖 **詳細**: [07-performance-devin-docs.md](07-performance-devin-docs.md#遅延読み込み)

#### 19. ✅ 不要な再レンダリング防止
```typescript
// ✅ OK
const MemoizedComponent = React.memo(ExpensiveComponent);
```
📖 **詳細**: [07-performance-devin-docs.md](07-performance-devin-docs.md#react最適化)

#### 20. ✅ バンドルサイズの監視
```bash
# ✅ OK
npm run build -- --analyze
```
📖 **詳細**: [07-performance-devin-docs.md](07-performance-devin-docs.md#バンドル最適化)

---

### 🟣 コード品質（Code Quality）

#### 21. ✅ ESLintエラー0件
```bash
# ✅ OK
npm run lint -- --max-warnings 0
```
📖 **詳細**: [01-introduction-setup.md](01-introduction-setup.md#eslint設定)

#### 22. ✅ 関数の単一責任
```typescript
// ✅ OK - 1つの関数が1つのことだけを行う
function validateEmail(email: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}
```
📖 **詳細**: [02-language-syntax.md](02-language-syntax.md#関数設計)

#### 23. ✅ Magic Numberの禁止
```typescript
// ❌ NG
if (user.age > 18) { }

// ✅ OK
const ADULT_AGE = 18;
if (user.age > ADULT_AGE) { }
```
📖 **詳細**: [02-language-syntax.md](02-language-syntax.md#定数)

#### 24. ✅ コメントは「なぜ」を説明
```typescript
// ✅ OK
// レート制限を回避するため3秒待機
await sleep(3000);
```
📖 **詳細**: [07-performance-devin-docs.md](07-performance-devin-docs.md#ドキュメンテーション)

#### 25. ✅ 適切なログレベル
```typescript
// ✅ OK
logger.debug('Processing user data');
logger.info('User created successfully');
logger.error('Failed to connect to database', error);
```
📖 **詳細**: [05-nodejs-backend.md](05-nodejs-backend.md#ロギング)

---

## 🚨 絶対にやってはいけないこと（Critical Anti-patterns）

### ❌ 1. `any`型の使用
```typescript
// ❌ 絶対NG
function processData(data: any) { }
```

### ❌ 2. エラーの握りつぶし
```typescript
// ❌ 絶対NG
try {
  await riskyOperation();
} catch (error) {
  // 何もしない
}
```

### ❌ 3. 非同期処理でawaitを忘れる
```typescript
// ❌ 絶対NG
const data = fetchData(); // Promiseが返る
console.log(data.name); // エラー
```

### ❌ 4. useEffect内で無限ループ
```typescript
// ❌ 絶対NG
useEffect(() => {
  setCount(count + 1); // 依存配列にcountがない
});
```

### ❌ 5. パスワード等の機密情報をコミット
```typescript
// ❌ 絶対NG
const DB_PASSWORD = 'mypassword123'; // コードに直接書く
```

---

## 📋 コードレビューチェックリスト

コード生成後、以下を確認：

- [ ] `any`型が使われていない
- [ ] すべての関数に型アノテーションがある
- [ ] nullチェックが適切に実装されている
- [ ] 命名規則が統一されている
- [ ] React Hooksの依存配列が正しい
- [ ] エラーハンドリングが実装されている
- [ ] テストが書かれている（80%以上カバレッジ）
- [ ] ESLintエラーが0件
- [ ] パフォーマンスを考慮した実装になっている
- [ ] セキュリティリスクがない

---

## 🔗 詳細ドキュメントへのリンク

| トピック | ドキュメント |
|---------|-------------|
| 基本設定 | [01-introduction-setup.md](01-introduction-setup.md) |
| 言語仕様 | [02-language-syntax.md](02-language-syntax.md) |
| 命名・型定義 | [03-naming-typing.md](03-naming-typing.md) |
| React規約 | [04-react-frontend.md](04-react-frontend.md) |
| Node.js規約 | [05-nodejs-backend.md](05-nodejs-backend.md) |
| テスト | [06-testing.md](06-testing.md) |
| パフォーマンス | [07-performance-devin-docs.md](07-performance-devin-docs.md) |

---

**AI-QUICK-REFERENCE** - Phase 6  
作成日: 2025-11-13  
対象: AI開発アシスタント（Devin、Copilot、GPT等）
