# AI-QUICK-REFERENCE: Frontend Architecture Standards

**目的**: AIエージェント（Devin等）が3分で確認できる、フロントエンド開発の必須チェック項目TOP25  
**対象**: Next.js、React、TypeScript、Vite  
**最終更新**: 2025-11-13

---

## ⚡ 必須チェック項目 TOP 25

### 📦 1. プロジェクトセットアップ（5項目）

#### ✅ 1. 技術スタック
- **必須**: React 18+、TypeScript 5+、Next.js 14+（推奨）または Vite 5+
- **状態管理**: TanStack Query（サーバー状態）、Zustand（クライアント状態）
- **テスト**: Vitest、React Testing Library、Playwright
- **参照**: [01-introduction-principles.md](01-introduction-principles.md)

#### ✅ 2. ディレクトリ構造（Next.js App Router）
```
app/
├── (auth)/              # Route Group
│   ├── login/
│   └── register/
├── (dashboard)/
│   ├── layout.tsx       # Nested Layout
│   └── page.tsx
├── api/                 # API Routes
├── layout.tsx           # Root Layout
└── page.tsx             # Home Page

components/
├── features/            # Feature-specific components
├── ui/                  # Reusable UI components
└── layouts/             # Layout components

lib/
├── api/                 # API clients
├── hooks/               # Custom hooks
├── utils/               # Utility functions
└── types/               # TypeScript types
```
- **原則**: レイヤー分離、機能ベース、再利用性
- **参照**: [02-application-structure-routing.md](02-application-structure-routing.md)

#### ✅ 3. 命名規則
- **コンポーネント**: PascalCase（`UserProfile.tsx`）
- **フック**: camelCase + `use`プレフィックス（`useUserData.ts`）
- **ユーティリティ**: camelCase（`formatDate.ts`）
- **定数**: UPPER_SNAKE_CASE（`API_BASE_URL`）
- **参照**: [02-application-structure-routing.md](02-application-structure-routing.md)

#### ✅ 4. TypeScript strict mode
```json
{
  "compilerOptions": {
    "strict": true,
    "noUncheckedIndexedAccess": true,
    "noImplicitReturns": true,
    "noFallthroughCasesInSwitch": true
  }
}
```
- **必須**: strict mode有効化
- **参照**: [01-introduction-principles.md](01-introduction-principles.md)

#### ✅ 5. ESLint & Prettier設定
```json
{
  "extends": [
    "next/core-web-vitals",
    "plugin:@typescript-eslint/recommended",
    "prettier"
  ]
}
```
- **必須**: ESLint、Prettier、TypeScript統合
- **参照**: [08-build-monitoring-best-practices.md](08-build-monitoring-best-practices.md)

---

### 🎨 2. コンポーネント設計（5項目）

#### ✅ 6. コンポーネント分類
- **Page Components**: ルートレベル、データフェッチング、レイアウト構成
- **Feature Components**: 機能固有、ビジネスロジック含む
- **UI Components**: 汎用、再利用可能、ロジック最小限
- **Layout Components**: レイアウト構造のみ
- **参照**: [04-component-design-data-fetching.md](04-component-design-data-fetching.md)

#### ✅ 7. 単一責任原則
```typescript
// ❌ 悪い例: 複数の責務
function UserDashboard() {
  const [users, setUsers] = useState([]);
  const [posts, setPosts] = useState([]);
  // データフェッチング、表示、フィルタリングが混在
}

// ✅ 良い例: 責務分離
function UserDashboard() {
  return (
    <>
      <UserList />
      <PostList />
    </>
  );
}
```
- **ルール**: 1コンポーネント = 1責務
- **参照**: [04-component-design-data-fetching.md](04-component-design-data-fetching.md)

#### ✅ 8. Props設計
```typescript
// ✅ 良いProps設計
interface ButtonProps {
  variant: 'primary' | 'secondary' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  disabled?: boolean;
  onClick: () => void;
  children: React.ReactNode;
}
```
- **原則**: 型安全、明確なインターフェース、デフォルト値
- **参照**: [04-component-design-data-fetching.md](04-component-design-data-fetching.md)

#### ✅ 9. Server Components vs Client Components
```typescript
// ✅ Server Component（デフォルト、Next.js）
async function UserList() {
  const users = await fetchUsers();
  return <div>{/* render */}</div>;
}

// ✅ Client Component（必要な時のみ）
'use client';
function InteractiveButton() {
  const [count, setCount] = useState(0);
  return <button onClick={() => setCount(count + 1)}>{count}</button>;
}
```
- **原則**: デフォルトはServer Components、インタラクティブ性が必要な時のみClient Components
- **参照**: [04-component-design-data-fetching.md](04-component-design-data-fetching.md)

#### ✅ 10. カスタムフックの活用
```typescript
// ✅ ロジックをカスタムフックに分離
function useUserData(userId: string) {
  return useQuery({
    queryKey: ['user', userId],
    queryFn: () => fetchUser(userId),
  });
}
```
- **原則**: ロジック再利用、コンポーネントのシンプル化
- **参照**: [04-component-design-data-fetching.md](04-component-design-data-fetching.md)

---

### 🔄 3. 状態管理 & データフェッチング（5項目）

#### ✅ 11. 状態管理戦略
- **サーバー状態**: TanStack Query（推奨）
- **グローバルUI状態**: Zustand（軽量）
- **複雑な状態**: Redux Toolkit（必要な場合のみ）
- **ローカル状態**: useState
- **参照**: [03-state-management.md](03-state-management.md)

#### ✅ 12. TanStack Query（必須）
```typescript
// ✅ サーバー状態はTanStack Query
function UserProfile({ userId }: { userId: string }) {
  const { data, isLoading, error } = useQuery({
    queryKey: ['user', userId],
    queryFn: () => fetchUser(userId),
    staleTime: 5 * 60 * 1000, // 5分
  });

  if (isLoading) return <Loading />;
  if (error) return <Error error={error} />;
  return <div>{data.name}</div>;
}
```
- **必須**: キャッシング、自動リフェッチ、エラーハンドリング
- **参照**: [03-state-management.md](03-state-management.md)

#### ✅ 13. エラーハンドリング
```typescript
// ✅ エラー境界 + TanStack Query
function UserProfile() {
  const { data, error, isError } = useQuery({
    queryKey: ['user'],
    queryFn: fetchUser,
    retry: 3,
    retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000),
  });

  if (isError) {
    return <ErrorFallback error={error} />;
  }
}
```
- **必須**: Error Boundary、リトライ戦略、フォールバックUI
- **参照**: [04-component-design-data-fetching.md](04-component-design-data-fetching.md)

#### ✅ 14. Optimistic Updates
```typescript
const mutation = useMutation({
  mutationFn: updateUser,
  onMutate: async (newUser) => {
    await queryClient.cancelQueries({ queryKey: ['user', newUser.id] });
    const previousUser = queryClient.getQueryData(['user', newUser.id]);
    queryClient.setQueryData(['user', newUser.id], newUser);
    return { previousUser };
  },
  onError: (err, newUser, context) => {
    queryClient.setQueryData(['user', newUser.id], context.previousUser);
  },
});
```
- **推奨**: 即座のUI更新、ロールバック処理
- **参照**: [03-state-management.md](03-state-management.md)

#### ✅ 15. データフェッチングパターン
- **Next.js Server Components**: `async/await`で直接フェッチ（推奨）
- **Client Components**: TanStack Query
- **禁止**: useEffectでのデータフェッチ（TanStack Queryを使用）
- **参照**: [04-component-design-data-fetching.md](04-component-design-data-fetching.md)

---

### ⚡ 4. パフォーマンス & SEO（5項目）

#### ✅ 16. Core Web Vitals目標値
- **LCP** (Largest Contentful Paint): < 2.5秒
- **FID** (First Input Delay): < 100ミリ秒
- **CLS** (Cumulative Layout Shift): < 0.1
- **必須**: Lighthouseスコア90以上
- **参照**: [05-performance-seo.md](05-performance-seo.md)

#### ✅ 17. 画像最適化
```typescript
// ✅ Next.js Image最適化
import Image from 'next/image';

<Image
  src="/hero.jpg"
  alt="Hero image"
  width={1200}
  height={600}
  priority // Above the fold
  placeholder="blur"
  blurDataURL={blurDataURL}
/>
```
- **必須**: Next.js Image、WebP/AVIF、遅延ロード
- **参照**: [05-performance-seo.md](05-performance-seo.md)

#### ✅ 18. コード分割
```typescript
// ✅ Dynamic Import
import dynamic from 'next/dynamic';

const HeavyComponent = dynamic(() => import('./HeavyComponent'), {
  loading: () => <Skeleton />,
  ssr: false, // Client-side only
});
```
- **必須**: Route-based splitting、Dynamic Import
- **参照**: [05-performance-seo.md](05-performance-seo.md)

#### ✅ 19. SEOメタデータ
```typescript
// ✅ Next.js 14+ Metadata API
export const metadata: Metadata = {
  title: 'User Dashboard | MyApp',
  description: 'Manage your account and settings',
  openGraph: {
    title: 'User Dashboard',
    description: 'Manage your account',
    images: ['/og-image.jpg'],
  },
};
```
- **必須**: タイトル、description、Open Graph、構造化データ
- **参照**: [05-performance-seo.md](05-performance-seo.md)

#### ✅ 20. キャッシング戦略
```typescript
// ✅ ISR (Incremental Static Regeneration)
export const revalidate = 3600; // 1時間

// ✅ TanStack Query キャッシング
const { data } = useQuery({
  queryKey: ['user'],
  queryFn: fetchUser,
  staleTime: 5 * 60 * 1000, // 5分間は新鮮
  cacheTime: 10 * 60 * 1000, // 10分間キャッシュ保持
});
```
- **必須**: ISR、SWR、TanStack Query staleTime設定
- **参照**: [05-performance-seo.md](05-performance-seo.md)

---

### 🔒 5. アクセシビリティ & セキュリティ & テスト（5項目）

#### ✅ 21. WCAG 2.1 AAレベル準拠
```typescript
// ✅ アクセシブルなボタン
<button
  aria-label="Close dialog"
  onClick={handleClose}
  disabled={isLoading}
>
  <CloseIcon aria-hidden="true" />
</button>

// ✅ アクセシブルなフォーム
<label htmlFor="email">Email</label>
<input
  id="email"
  type="email"
  aria-required="true"
  aria-invalid={!!errors.email}
  aria-describedby={errors.email ? 'email-error' : undefined}
/>
{errors.email && <span id="email-error" role="alert">{errors.email}</span>}
```
- **必須**: セマンティックHTML、ARIA属性、キーボードナビゲーション
- **参照**: [06-accessibility-security.md](06-accessibility-security.md)

#### ✅ 22. XSS対策
```typescript
// ✅ デフォルトでエスケープされる
<div>{userInput}</div>

// ❌ 危険: dangerouslySetInnerHTML
<div dangerouslySetInnerHTML={{ __html: userInput }} />

// ✅ サニタイズが必要な場合
import DOMPurify from 'isomorphic-dompurify';
<div dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(userInput) }} />
```
- **必須**: 入力サニタイゼーション、CSP設定
- **参照**: [06-accessibility-security.md](06-accessibility-security.md)

#### ✅ 23. 認証・認可
```typescript
// ✅ JWT認証 + HTTPOnly Cookie
// Server Component
async function ProtectedPage() {
  const session = await getServerSession();
  if (!session) {
    redirect('/login');
  }
  return <Dashboard user={session.user} />;
}

// ✅ クライアント側保護
'use client';
function ProtectedClient() {
  const { data: session, status } = useSession();
  if (status === 'loading') return <Loading />;
  if (!session) return <LoginPrompt />;
  return <Content />;
}
```
- **必須**: HTTPOnly Cookie、JWT、セッション管理
- **参照**: [06-accessibility-security.md](06-accessibility-security.md)

#### ✅ 24. テストカバレッジ
```typescript
// ✅ コンポーネントテスト
import { render, screen, fireEvent } from '@testing-library/react';

test('button click increments counter', () => {
  render(<Counter />);
  const button = screen.getByRole('button', { name: /increment/i });
  fireEvent.click(button);
  expect(screen.getByText('Count: 1')).toBeInTheDocument();
});
```
- **最小カバレッジ**: 80%
- **テスト比率**: ユニット70%、統合20%、E2E10%
- **参照**: [07-testing-i18n.md](07-testing-i18n.md)

#### ✅ 25. エラートラッキング
```typescript
// ✅ Sentry統合
import * as Sentry from '@sentry/nextjs';

Sentry.init({
  dsn: process.env.NEXT_PUBLIC_SENTRY_DSN,
  environment: process.env.NODE_ENV,
  tracesSampleRate: 0.1,
});

// エラー境界
class ErrorBoundary extends React.Component {
  componentDidCatch(error, errorInfo) {
    Sentry.captureException(error, { contexts: { react: errorInfo } });
  }
}
```
- **必須**: Sentry、Error Boundary、ソースマップアップロード
- **参照**: [08-build-monitoring-best-practices.md](08-build-monitoring-best-practices.md)

---

## 🔍 チェックリスト使用方法

### コンポーネント実装前（5分）
1. ✅ 1-5: プロジェクトセットアップ確認
2. ✅ 6-10: コンポーネント設計パターン確認
3. ✅ 11-15: 状態管理戦略確認

### コンポーネント実装中（随時）
1. ✅ 6-10: コンポーネント設計原則
2. ✅ 11-15: データフェッチング、エラーハンドリング
3. ✅ 21-23: アクセシビリティ、セキュリティ

### デプロイ前（10分）
1. ✅ 16-20: パフォーマンス、SEO確認
2. ✅ 21-25: アクセシビリティ、セキュリティ、テスト、モニタリング
3. ✅ 1-25: 全項目再確認

---

## 🤖 Devinへの指示例

```
以下の必須チェック項目TOP25に厳密に従って実装してください：
- 規約: /devin-organization-standards/02-architecture-standards/frontend/AI-QUICK-REFERENCE.md
- 重点項目: ✅6-10（コンポーネント設計）、✅11-15（状態管理・データフェッチング）、✅16-20（パフォーマンス・SEO）
- Core Web Vitals: LCP < 2.5s、FID < 100ms、CLS < 0.1（✅16）
- テストカバレッジ: 80%以上（✅24）
- 実装完了後、✅1-25の全項目を確認し、違反がないことを報告してください
```

---

## 📊 違反時の対処

| チェック項目 | 違反例 | 修正方法 | 参照 |
|------------|--------|---------|------|
| ✅7 SRP違反 | 1コンポーネントに複数責務 | コンポーネント分割 | 04 |
| ✅9 Server/Client | 不要なClient Component | Server Componentに変更 | 04 |
| ✅12 データフェッチ | useEffectでフェッチ | TanStack Query使用 | 03 |
| ✅16 Core Web Vitals | LCP > 2.5s | 画像最適化、コード分割 | 05 |
| ✅21 WCAG違反 | ARIA属性不足 | セマンティックHTML、ARIA追加 | 06 |
| ✅24 カバレッジ | 70% | 80%まで追加 | 07 |

---

## 🔗 詳細ドキュメント

各チェック項目の詳細は以下を参照：
- **[README.md](README.md)**: 全体ナビゲーション
- **[01-introduction-principles.md](01-introduction-principles.md)**: ✅1, 4
- **[02-application-structure-routing.md](02-application-structure-routing.md)**: ✅2, 3, 5
- **[03-state-management.md](03-state-management.md)**: ✅11, 12, 14
- **[04-component-design-data-fetching.md](04-component-design-data-fetching.md)**: ✅6-10, 13, 15
- **[05-performance-seo.md](05-performance-seo.md)**: ✅16-20
- **[06-accessibility-security.md](06-accessibility-security.md)**: ✅21-23
- **[07-testing-i18n.md](07-testing-i18n.md)**: ✅24
- **[08-build-monitoring-best-practices.md](08-build-monitoring-best-practices.md)**: ✅25

---

**最終確認**: 実装完了後、✅1-25の全項目をチェックし、違反ゼロを確認してください。
