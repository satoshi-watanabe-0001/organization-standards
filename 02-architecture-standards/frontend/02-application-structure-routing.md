# Application Structure & Routing

**説明**: アプリケーション構造、ディレクトリ設計、ルーティング

**主要トピック**:
- Next.js App Routerディレクトリ構造
- Vite + Reactディレクトリ構造
- 命名規則
- Next.js App Router（推奨）
- React Router（Vite）

---

## アプリケーション構造 / Application Structure

### ディレクトリ構造 / Directory Structure

#### Next.js App Router構造 (推奨)

```
my-app/
├── public/                      # 静的ファイル
│   ├── images/
│   ├── fonts/
│   └── favicon.ico
├── src/
│   ├── app/                     # Next.js App Router
│   │   ├── (auth)/             # ルートグループ
│   │   │   ├── login/
│   │   │   │   └── page.tsx
│   │   │   └── register/
│   │   │       └── page.tsx
│   │   ├── (marketing)/
│   │   │   ├── about/
│   │   │   └── pricing/
│   │   ├── dashboard/
│   │   │   ├── layout.tsx
│   │   │   ├── page.tsx
│   │   │   └── settings/
│   │   ├── api/                # API Routes
│   │   │   └── users/
│   │   │       └── route.ts
│   │   ├── layout.tsx          # ルートレイアウト
│   │   ├── page.tsx            # ホームページ
│   │   ├── loading.tsx         # ローディングUI
│   │   ├── error.tsx           # エラーUI
│   │   └── not-found.tsx       # 404ページ
│   ├── components/             # 共有コンポーネント
│   │   ├── ui/                 # 基本UIコンポーネント
│   │   │   ├── button/
│   │   │   │   ├── button.tsx
│   │   │   │   ├── button.test.tsx
│   │   │   │   └── button.stories.tsx
│   │   │   ├── input/
│   │   │   ├── modal/
│   │   │   └── index.ts
│   │   ├── layout/             # レイアウトコンポーネント
│   │   │   ├── header/
│   │   │   ├── footer/
│   │   │   └── sidebar/
│   │   └── common/             # 共通コンポーネント
│   │       ├── loading-spinner/
│   │       ├── error-boundary/
│   │       └── seo-head/
│   ├── features/               # 機能ベースモジュール
│   │   ├── auth/
│   │   │   ├── components/
│   │   │   │   ├── login-form.tsx
│   │   │   │   └── register-form.tsx
│   │   │   ├── hooks/
│   │   │   │   ├── use-auth.ts
│   │   │   │   └── use-login.ts
│   │   │   ├── api/
│   │   │   │   └── auth-api.ts
│   │   │   ├── types/
│   │   │   │   └── auth.types.ts
│   │   │   └── utils/
│   │   │       └── validate-password.ts
│   │   ├── products/
│   │   │   ├── components/
│   │   │   ├── hooks/
│   │   │   ├── api/
│   │   │   └── types/
│   │   └── checkout/
│   ├── hooks/                  # 共有カスタムフック
│   │   ├── use-media-query.ts
│   │   ├── use-debounce.ts
│   │   ├── use-local-storage.ts
│   │   └── index.ts
│   ├── lib/                    # ユーティリティ、ヘルパー
│   │   ├── api-client.ts
│   │   ├── auth.ts
│   │   ├── utils.ts
│   │   └── constants.ts
│   ├── store/                  # グローバル状態管理
│   │   ├── slices/
│   │   │   ├── auth-slice.ts
│   │   │   └── cart-slice.ts
│   │   ├── hooks.ts
│   │   └── store.ts
│   ├── styles/                 # グローバルスタイル
│   │   ├── globals.css
│   │   └── themes.css
│   ├── types/                  # 共有型定義
│   │   ├── api.types.ts
│   │   ├── models.types.ts
│   │   └── index.ts
│   ├── config/                 # 設定ファイル
│   │   ├── site.config.ts
│   │   ├── api.config.ts
│   │   └── env.ts
│   └── middleware.ts           # Next.js Middleware
├── tests/                      # テスト
│   ├── e2e/                    # E2Eテスト
│   ├── integration/            # 統合テスト
│   └── setup.ts
├── .storybook/                 # Storybook設定
├── .github/                    # GitHub Actions
├── package.json
├── tsconfig.json
├── next.config.js
├── tailwind.config.ts
├── vitest.config.ts
└── README.md
```

#### Vite + React構造

```
my-app/
├── public/
├── src/
│   ├── pages/                  # ページコンポーネント
│   │   ├── home/
│   │   │   ├── home.page.tsx
│   │   │   └── home.test.tsx
│   │   ├── dashboard/
│   │   └── index.tsx
│   ├── components/             # 共有コンポーネント
│   ├── features/               # 機能モジュール
│   ├── hooks/
│   ├── lib/
│   ├── store/
│   ├── routes/                 # ルーティング設定
│   │   ├── protected-route.tsx
│   │   └── app-router.tsx
│   ├── styles/
│   ├── types/
│   ├── config/
│   ├── main.tsx                # エントリーポイント
│   └── app.tsx                 # ルートコンポーネント
├── tests/
├── package.json
├── tsconfig.json
├── vite.config.ts
└── README.md
```

### 命名規則 / Naming Conventions

```yaml
naming:
  files:
    components: "kebab-case (button.tsx, user-profile.tsx)"
    pages_nextjs: "kebab-case (page.tsx, layout.tsx)"
    pages_vite: "PascalCase.page.tsx (Home.page.tsx)"
    hooks: "camelCase (use-auth.ts, use-debounce.ts)"
    utils: "kebab-case (format-date.ts, api-client.ts)"
    types: "kebab-case (user.types.ts, api.types.ts)"
    tests: "同名.test.tsx (button.test.tsx)"
    stories: "同名.stories.tsx (button.stories.tsx)"
  
  components:
    react_components: "PascalCase (Button, UserProfile)"
    variables: "camelCase (userName, isLoading)"
    constants: "UPPER_SNAKE_CASE (API_BASE_URL, MAX_RETRY)"
    types_interfaces: "PascalCase (User, ApiResponse)"
    enums: "PascalCase (UserRole, Status)"
  
  css_classes:
    tailwind: "utility-first (flex items-center justify-between)"
    custom: "kebab-case (nav-item, card-header)"
    bem: "BEM形式も可 (block__element--modifier)"
```

---


## ルーティング / Routing

### Next.js App Router (推奨)

```typescript
// app/layout.tsx
import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import './globals.css';

const inter = Inter({ subsets: ['latin'] });

export const metadata: Metadata = {
  title: 'My App',
  description: 'Application description',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="ja">
      <body className={inter.className}>{children}</body>
    </html>
  );
}

// app/(auth)/layout.tsx - 認証レイアウト
export default function AuthLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="max-w-md w-full">{children}</div>
    </div>
  );
}

// app/(auth)/login/page.tsx
import { LoginForm } from '@/features/auth/components/login-form';

export default function LoginPage() {
  return (
    <div>
      <h1 className="text-2xl font-bold mb-6">ログイン</h1>
      <LoginForm />
    </div>
  );
}

// app/dashboard/layout.tsx - ダッシュボードレイアウト
import { DashboardHeader } from '@/components/layout/dashboard-header';
import { DashboardSidebar } from '@/components/layout/dashboard-sidebar';

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="flex h-screen">
      <DashboardSidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <DashboardHeader />
        <main className="flex-1 overflow-y-auto p-6">{children}</main>
      </div>
    </div>
  );
}

// app/products/[id]/page.tsx - 動的ルート
import { notFound } from 'next/navigation';
import { getProduct } from '@/features/products/api/products-api';

interface Props {
  params: { id: string };
  searchParams: { [key: string]: string | string[] | undefined };
}

export default async function ProductPage({ params }: Props) {
  const product = await getProduct(params.id);

  if (!product) {
    notFound();
  }

  return (
    <div>
      <h1>{product.name}</h1>
      <p>{product.description}</p>
    </div>
  );
}

// app/products/[id]/loading.tsx - ローディングUI
export default function Loading() {
  return (
    <div className="animate-pulse">
      <div className="h-8 bg-gray-200 rounded w-1/3 mb-4"></div>
      <div className="h-4 bg-gray-200 rounded w-full mb-2"></div>
      <div className="h-4 bg-gray-200 rounded w-2/3"></div>
    </div>
  );
}

// app/products/[id]/error.tsx - エラーUI
'use client';

import { useEffect } from 'react';

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  useEffect(() => {
    console.error(error);
  }, [error]);

  return (
    <div>
      <h2>エラーが発生しました</h2>
      <button onClick={() => reset()}>再試行</button>
    </div>
  );
}

// middleware.ts - 認証ミドルウェア
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
  const token = request.cookies.get('token');

  // 認証が必要なパス
  if (request.nextUrl.pathname.startsWith('/dashboard')) {
    if (!token) {
      return NextResponse.redirect(new URL('/login', request.url));
    }
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/dashboard/:path*'],
};
```

### React Router (Vite)

```typescript
// routes/app-router.tsx
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { RootLayout } from '@/components/layout/root-layout';
import { ProtectedRoute } from './protected-route';

// Lazy loading
const HomePage = lazy(() => import('@/pages/home/home.page'));
const DashboardPage = lazy(() => import('@/pages/dashboard/dashboard.page'));
const LoginPage = lazy(() => import('@/pages/login/login.page'));

const router = createBrowserRouter([
  {
    element: <RootLayout />,
    errorElement: <ErrorPage />,
    children: [
      {
        path: '/',
        element: <HomePage />,
      },
      {
        path: '/login',
        element: <LoginPage />,
      },
      {
        path: '/dashboard',
        element: (
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        ),
      },
      {
        path: '/products/:id',
        element: <ProductDetailPage />,
        loader: async ({ params }) => {
          return fetch(`/api/products/${params.id}`);
        },
      },
    ],
  },
]);

export function AppRouter() {
  return <RouterProvider router={router} />;
}

// routes/protected-route.tsx
import { Navigate } from 'react-router-dom';
import { useAuthStore } from '@/store/slices/auth-slice';

interface Props {
  children: React.ReactNode;
}

export function ProtectedRoute({ children }: Props) {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}
```

---


