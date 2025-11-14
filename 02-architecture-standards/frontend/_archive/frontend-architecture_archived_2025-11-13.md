# フロントエンドアーキテクチャ標準 / Frontend Architecture Standards

```yaml
version: "2.0.0"
last_updated: "2025-01-15"
status: "active"
owner: "Frontend Architecture Team"
category: "architecture-standards"
```

## 目次 / Table of Contents

1. [概要 / Overview](#概要--overview)
2. [アーキテクチャ原則 / Architecture Principles](#アーキテクチャ原則--architecture-principles)
3. [アプリケーション構造 / Application Structure](#アプリケーション構造--application-structure)
4. [状態管理 / State Management](#状態管理--state-management)
5. [ルーティング / Routing](#ルーティング--routing)
6. [コンポーネント設計 / Component Design](#コンポーネント設計--component-design)
7. [データフェッチング / Data Fetching](#データフェッチング--data-fetching)
8. [パフォーマンス最適化 / Performance Optimization](#パフォーマンス最適化--performance-optimization)
9. [SEO対策 / SEO Optimization](#seo対策--seo-optimization)
10. [アクセシビリティ / Accessibility](#アクセシビリティ--accessibility)
11. [セキュリティ / Security](#セキュリティ--security)
12. [テスト戦略 / Testing Strategy](#テスト戦略--testing-strategy)
13. [国際化 / Internationalization](#国際化--internationalization)
14. [ビルドと最適化 / Build and Optimization](#ビルドと最適化--build-and-optimization)
15. [モニタリングとエラートラッキング / Monitoring and Error Tracking](#モニタリングとエラートラッキング--monitoring-and-error-tracking)
16. [ベストプラクティス / Best Practices](#ベストプラクティス--best-practices)
17. [参考資料 / References](#参考資料--references)

---

## 概要 / Overview

このドキュメントは、組織全体で適用されるフロントエンドアーキテクチャの標準を定義します。

### 目的 / Purpose

- **一貫性**: 統一されたアーキテクチャパターンの確立
- **保守性**: 長期的に保守可能なコードベース
- **パフォーマンス**: 高速でレスポンシブなユーザー体験
- **スケーラビリティ**: チームとアプリケーションの成長に対応
- **品質**: 高品質なユーザーインターフェースの提供

### 適用範囲 / Scope

- すべてのWebアプリケーション
- モバイルWebアプリケーション
- プログレッシブWebアプリ (PWA)
- 管理画面・ダッシュボード

### 技術スタック / Technology Stack

```yaml
core_stack:
  language: "TypeScript 5.x"
  framework: "React 18.x"
  meta_framework: "Next.js 14.x"
  styling: "Tailwind CSS 3.x"
  build_tool: "Vite 5.x / Next.js"
  package_manager: "pnpm"

reference: "05-technology-stack/frontend-stack.md"
```

---

## アーキテクチャ原則 / Architecture Principles

### 基本原則 / Fundamental Principles

```yaml
principles:
  component_based:
    description: "コンポーネントベースアーキテクチャ"
    practices:
      - "再利用可能なコンポーネント"
      - "単一責任の原則"
      - "疎結合"
      - "高凝集"
  
  type_safety:
    description: "型安全性の確保"
    practices:
      - "TypeScript必須"
      - "strict mode有効化"
      - "any型の禁止"
      - "型定義の共有"
  
  progressive_enhancement:
    description: "プログレッシブエンハンスメント"
    practices:
      - "基本機能はJavaScriptなしで動作"
      - "段階的な機能追加"
      - "グレースフルデグラデーション"
  
  performance_first:
    description: "パフォーマンス優先"
    practices:
      - "Core Web Vitals最適化"
      - "コード分割"
      - "遅延読み込み"
      - "バンドルサイズ最小化"
  
  accessibility_first:
    description: "アクセシビリティファースト"
    practices:
      - "WCAG 2.1 AA準拠"
      - "セマンティックHTML"
      - "キーボード操作対応"
      - "スクリーンリーダー対応"
  
  mobile_first:
    description: "モバイルファースト設計"
    practices:
      - "レスポンシブデザイン"
      - "タッチ操作最適化"
      - "軽量なアセット"
```

### アーキテクチャパターン / Architecture Patterns

```yaml
patterns:
  feature_based:
    description: "機能ベースのディレクトリ構造"
    rationale: "スケーラビリティ、チーム分担"
    structure: |
      src/
      ├── features/
      │   ├── auth/
      │   ├── products/
      │   └── checkout/
      └── shared/
  
  container_presentational:
    description: "コンテナ/プレゼンテーショナルパターン"
    container: "ビジネスロジック、データフェッチング"
    presentational: "UI表示のみ"
  
  compound_components:
    description: "複合コンポーネントパターン"
    use_case: "柔軟で再利用可能なコンポーネント"
    example: "Dropdown, Tabs, Modal"
  
  render_props:
    description: "Render Propsパターン"
    use_case: "ロジックの共有"
  
  custom_hooks:
    description: "カスタムフック"
    use_case: "ステートフルロジックの再利用"
    preferred: true
```

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

## 状態管理 / State Management

### 状態管理戦略 / State Management Strategy

```yaml
state_management:
  hierarchy:
    1_local_state:
      tool: "useState, useReducer"
      use_case: "コンポーネント内の状態"
      example: "フォーム入力、UI状態"
    
    2_shared_state:
      tool: "Context API + useContext"
      use_case: "コンポーネントツリー内の共有状態"
      example: "テーマ、言語設定"
    
    3_server_state:
      tool: "TanStack Query (React Query)"
      use_case: "サーバーデータのキャッシング"
      example: "API取得データ"
      preferred: true
    
    4_global_state:
      tool: "Redux Toolkit / Zustand"
      use_case: "アプリケーション全体の状態"
      example: "認証情報、ショッピングカート"
```

### TanStack Query (推奨)

```typescript
// lib/query-client.ts
import { QueryClient } from '@tanstack/react-query';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 60 * 1000, // 1分
      cacheTime: 5 * 60 * 1000, // 5分
      retry: 3,
      refetchOnWindowFocus: false,
    },
  },
});

// features/products/api/products-api.ts
import { api } from '@/lib/api-client';
import type { Product } from '../types/product.types';

export const productsApi = {
  getAll: async (): Promise<Product[]> => {
    const response = await api.get<Product[]>('/products');
    return response.data;
  },

  getById: async (id: string): Promise<Product> => {
    const response = await api.get<Product>(`/products/${id}`);
    return response.data;
  },

  create: async (data: Omit<Product, 'id'>): Promise<Product> => {
    const response = await api.post<Product>('/products', data);
    return response.data;
  },

  update: async (id: string, data: Partial<Product>): Promise<Product> => {
    const response = await api.patch<Product>(`/products/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/products/${id}`);
  },
};

// features/products/hooks/use-products.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { productsApi } from '../api/products-api';
import type { Product } from '../types/product.types';

export const PRODUCTS_QUERY_KEY = 'products';

export function useProducts() {
  return useQuery({
    queryKey: [PRODUCTS_QUERY_KEY],
    queryFn: productsApi.getAll,
  });
}

export function useProduct(id: string) {
  return useQuery({
    queryKey: [PRODUCTS_QUERY_KEY, id],
    queryFn: () => productsApi.getById(id),
    enabled: !!id,
  });
}

export function useCreateProduct() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: productsApi.create,
    onSuccess: () => {
      // キャッシュの無効化
      queryClient.invalidateQueries({ queryKey: [PRODUCTS_QUERY_KEY] });
    },
  });
}

export function useUpdateProduct() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<Product> }) =>
      productsApi.update(id, data),
    onSuccess: (_, variables) => {
      // 楽観的更新
      queryClient.invalidateQueries({ queryKey: [PRODUCTS_QUERY_KEY] });
      queryClient.invalidateQueries({ queryKey: [PRODUCTS_QUERY_KEY, variables.id] });
    },
  });
}

// 使用例
function ProductList() {
  const { data: products, isLoading, error } = useProducts();
  const createProduct = useCreateProduct();

  if (isLoading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;

  return (
    <div>
      {products?.map((product) => (
        <ProductCard key={product.id} product={product} />
      ))}
    </div>
  );
}
```

### Zustand (軽量グローバル状態)

```typescript
// store/slices/auth-slice.ts
import { create } from 'zustand';
import { devtools, persist } from 'zustand/middleware';

interface User {
  id: string;
  name: string;
  email: string;
  role: string;
}

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  
  // Actions
  login: (user: User, token: string) => void;
  logout: () => void;
  updateUser: (user: Partial<User>) => void;
}

export const useAuthStore = create<AuthState>()(
  devtools(
    persist(
      (set) => ({
        user: null,
        token: null,
        isAuthenticated: false,

        login: (user, token) =>
          set({
            user,
            token,
            isAuthenticated: true,
          }),

        logout: () =>
          set({
            user: null,
            token: null,
            isAuthenticated: false,
          }),

        updateUser: (userData) =>
          set((state) => ({
            user: state.user ? { ...state.user, ...userData } : null,
          })),
      }),
      {
        name: 'auth-storage',
        partialize: (state) => ({
          token: state.token,
          user: state.user,
        }),
      }
    )
  )
);

// 使用例
function UserProfile() {
  const { user, logout } = useAuthStore();

  if (!user) return null;

  return (
    <div>
      <h1>{user.name}</h1>
      <button onClick={logout}>ログアウト</button>
    </div>
  );
}
```

### Redux Toolkit (複雑な状態管理)

```typescript
// store/store.ts
import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/auth-slice';
import cartReducer from './slices/cart-slice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    cart: cartReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

// store/hooks.ts
import { useDispatch, useSelector } from 'react-redux';
import type { RootState, AppDispatch } from './store';

export const useAppDispatch = useDispatch.withTypes<AppDispatch>();
export const useAppSelector = useSelector.withTypes<RootState>();

// store/slices/cart-slice.ts
import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import type { RootState } from '../store';

interface CartItem {
  id: string;
  productId: string;
  name: string;
  price: number;
  quantity: number;
}

interface CartState {
  items: CartItem[];
  total: number;
  isLoading: boolean;
  error: string | null;
}

const initialState: CartState = {
  items: [],
  total: 0,
  isLoading: false,
  error: null,
};

// 非同期アクション
export const fetchCart = createAsyncThunk(
  'cart/fetchCart',
  async (userId: string) => {
    const response = await fetch(`/api/cart/${userId}`);
    return response.json();
  }
);

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    addItem: (state, action: PayloadAction<CartItem>) => {
      const existingItem = state.items.find(
        (item) => item.productId === action.payload.productId
      );

      if (existingItem) {
        existingItem.quantity += action.payload.quantity;
      } else {
        state.items.push(action.payload);
      }

      // 合計金額の再計算
      state.total = state.items.reduce(
        (sum, item) => sum + item.price * item.quantity,
        0
      );
    },

    removeItem: (state, action: PayloadAction<string>) => {
      state.items = state.items.filter((item) => item.id !== action.payload);
      state.total = state.items.reduce(
        (sum, item) => sum + item.price * item.quantity,
        0
      );
    },

    updateQuantity: (
      state,
      action: PayloadAction<{ id: string; quantity: number }>
    ) => {
      const item = state.items.find((item) => item.id === action.payload.id);
      if (item) {
        item.quantity = action.payload.quantity;
        state.total = state.items.reduce(
          (sum, item) => sum + item.price * item.quantity,
          0
        );
      }
    },

    clearCart: (state) => {
      state.items = [];
      state.total = 0;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCart.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchCart.fulfilled, (state, action) => {
        state.isLoading = false;
        state.items = action.payload.items;
        state.total = action.payload.total;
      })
      .addCase(fetchCart.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Failed to fetch cart';
      });
  },
});

export const { addItem, removeItem, updateQuantity, clearCart } = cartSlice.actions;

// Selectors
export const selectCartItems = (state: RootState) => state.cart.items;
export const selectCartTotal = (state: RootState) => state.cart.total;
export const selectCartItemCount = (state: RootState) =>
  state.cart.items.reduce((count, item) => count + item.quantity, 0);

export default cartSlice.reducer;

// 使用例
function ShoppingCart() {
  const dispatch = useAppDispatch();
  const items = useAppSelector(selectCartItems);
  const total = useAppSelector(selectCartTotal);

  const handleAddItem = (item: CartItem) => {
    dispatch(addItem(item));
  };

  return (
    <div>
      {items.map((item) => (
        <CartItem key={item.id} item={item} />
      ))}
      <div>合計: ¥{total}</div>
    </div>
  );
}
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

## コンポーネント設計 / Component Design

### コンポーネント分類 / Component Classification

```yaml
component_types:
  presentational:
    description: "プレゼンテーショナルコンポーネント(UI専用)"
    characteristics:
      - "Propsのみで動作"
      - "ビジネスロジックなし"
      - "純粋関数的"
    examples: ["Button", "Input", "Card"]
  
  container:
    description: "コンテナコンポーネント(ロジック担当)"
    characteristics:
      - "データフェッチング"
      - "状態管理"
      - "子コンポーネントへのデータ提供"
    examples: ["ProductListContainer", "UserProfileContainer"]
  
  layout:
    description: "レイアウトコンポーネント"
    characteristics:
      - "ページ構造定義"
      - "共通レイアウト"
    examples: ["Header", "Footer", "Sidebar", "PageLayout"]
  
  page:
    description: "ページコンポーネント"
    characteristics:
      - "ルート直結"
      - "複数コンポーネントの組み合わせ"
    examples: ["HomePage", "DashboardPage"]
```

### コンポーネント設計原則 / Component Design Principles

```typescript
// 1. 単一責任の原則
// ❌ Bad: 複数の責任
function UserDashboard() {
  const [user, setUser] = useState(null);
  const [posts, setPosts] = useState([]);
  
  // ユーザーデータ取得
  useEffect(() => {
    fetch('/api/user').then(r => r.json()).then(setUser);
  }, []);
  
  // 投稿データ取得
  useEffect(() => {
    fetch('/api/posts').then(r => r.json()).then(setPosts);
  }, []);
  
  return (
    <div>
      <div>{user?.name}</div>
      <div>{posts.map(...)}</div>
    </div>
  );
}

// ✅ Good: 責任の分離
function UserDashboard() {
  return (
    <div>
      <UserProfile />
      <UserPosts />
    </div>
  );
}

function UserProfile() {
  const { data: user } = useUser();
  return <div>{user?.name}</div>;
}

function UserPosts() {
  const { data: posts } = usePosts();
  return <div>{posts?.map(...)}</div>;
}

// 2. Props設計
// ✅ Good: 明確な型定義
interface ButtonProps {
  variant?: 'primary' | 'secondary' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  disabled?: boolean;
  loading?: boolean;
  children: React.ReactNode;
  onClick?: () => void;
  className?: string;
}

export function Button({
  variant = 'primary',
  size = 'md',
  disabled = false,
  loading = false,
  children,
  onClick,
  className,
}: ButtonProps) {
  const baseStyles = 'rounded font-medium transition-colors';
  const variantStyles = {
    primary: 'bg-blue-600 text-white hover:bg-blue-700',
    secondary: 'bg-gray-200 text-gray-900 hover:bg-gray-300',
    danger: 'bg-red-600 text-white hover:bg-red-700',
  };
  const sizeStyles = {
    sm: 'px-3 py-1.5 text-sm',
    md: 'px-4 py-2 text-base',
    lg: 'px-6 py-3 text-lg',
  };

  return (
    <button
      className={cn(
        baseStyles,
        variantStyles[variant],
        sizeStyles[size],
        disabled && 'opacity-50 cursor-not-allowed',
        className
      )}
      disabled={disabled || loading}
      onClick={onClick}
    >
      {loading ? <Spinner /> : children}
    </button>
  );
}

// 3. 複合コンポーネントパターン
interface DropdownProps {
  children: React.ReactNode;
}

interface DropdownComposition {
  Trigger: typeof DropdownTrigger;
  Content: typeof DropdownContent;
  Item: typeof DropdownItem;
}

export const Dropdown: React.FC<DropdownProps> & DropdownComposition = ({
  children,
}) => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <DropdownContext.Provider value={{ isOpen, setIsOpen }}>
      <div className="relative">{children}</div>
    </DropdownContext.Provider>
  );
};

function DropdownTrigger({ children }: { children: React.ReactNode }) {
  const { setIsOpen } = useDropdownContext();
  return (
    <button onClick={() => setIsOpen((prev) => !prev)}>{children}</button>
  );
}

function DropdownContent({ children }: { children: React.ReactNode }) {
  const { isOpen } = useDropdownContext();
  if (!isOpen) return null;
  return <div className="absolute mt-2 w-48 rounded-md shadow-lg">{children}</div>;
}

function DropdownItem({ children }: { children: React.ReactNode }) {
  return <div className="px-4 py-2 hover:bg-gray-100">{children}</div>;
}

Dropdown.Trigger = DropdownTrigger;
Dropdown.Content = DropdownContent;
Dropdown.Item = DropdownItem;

// 使用例
function Example() {
  return (
    <Dropdown>
      <Dropdown.Trigger>
        <button>メニュー</button>
      </Dropdown.Trigger>
      <Dropdown.Content>
        <Dropdown.Item>プロフィール</Dropdown.Item>
        <Dropdown.Item>設定</Dropdown.Item>
        <Dropdown.Item>ログアウト</Dropdown.Item>
      </Dropdown.Content>
    </Dropdown>
  );
}
```

---

---

## データフェッチング / Data Fetching

### データフェッチング戦略 / Data Fetching Strategy

```yaml
data_fetching:
  next_js:
    server_components:
      default: true
      benefits:
        - "サーバーで直接データ取得"
        - "バンドルサイズ削減"
        - "SEO最適化"
      use_cases:
        - "静的コンテンツ"
        - "初期表示データ"
    
    client_components:
      use_when:
        - "インタラクティブUI"
        - "ブラウザAPIが必要"
        - "リアルタイム更新"
      library: "TanStack Query"
  
  vite_react:
    primary: "TanStack Query"
    patterns:
      - "Fetch-on-render"
      - "Fetch-then-render"
      - "Render-as-you-fetch (Suspense)"
```

### Next.js Server Components

```typescript
// app/products/page.tsx - Server Component
import { getProducts } from '@/features/products/api/products-api';
import { ProductList } from '@/features/products/components/product-list';

export default async function ProductsPage() {
  // サーバーで直接データ取得
  const products = await getProducts();

  return (
    <div>
      <h1>商品一覧</h1>
      <ProductList products={products} />
    </div>
  );
}

// Streaming with Suspense
import { Suspense } from 'react';

export default function ProductsPage() {
  return (
    <div>
      <h1>商品一覧</h1>
      <Suspense fallback={<ProductListSkeleton />}>
        <ProductListAsync />
      </Suspense>
    </div>
  );
}

async function ProductListAsync() {
  const products = await getProducts();
  return <ProductList products={products} />;
}

// Parallel Data Fetching
export default async function DashboardPage() {
  // 並列取得
  const [user, stats, activities] = await Promise.all([
    getUser(),
    getStats(),
    getActivities(),
  ]);

  return (
    <div>
      <UserProfile user={user} />
      <StatsCards stats={stats} />
      <ActivityFeed activities={activities} />
    </div>
  );
}

// Sequential Data Fetching
export default async function ProductDetailPage({ 
  params 
}: { 
  params: { id: string } 
}) {
  // 順次取得（product取得後にreviewsを取得）
  const product = await getProduct(params.id);
  const reviews = await getReviews(product.id);

  return (
    <div>
      <ProductDetail product={product} />
      <ProductReviews reviews={reviews} />
    </div>
  );
}
```

### Client-Side Data Fetching

```typescript
// features/products/components/product-filter.tsx - Client Component
'use client';

import { useState } from 'react';
import { useProducts } from '../hooks/use-products';

export function ProductFilter() {
  const [category, setCategory] = useState<string>('all');
  
  const { data: products, isLoading, error } = useProducts({
    category: category !== 'all' ? category : undefined,
  });

  if (isLoading) return <LoadingSkeleton />;
  if (error) return <ErrorMessage error={error} />;

  return (
    <div>
      <select value={category} onChange={(e) => setCategory(e.target.value)}>
        <option value="all">すべて</option>
        <option value="electronics">電子機器</option>
        <option value="clothing">衣類</option>
      </select>

      <div className="grid grid-cols-3 gap-4 mt-4">
        {products?.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </div>
  );
}

// Infinite Scroll
import { useInfiniteQuery } from '@tanstack/react-query';
import { useInView } from 'react-intersection-observer';
import { useEffect } from 'react';

function InfiniteProductList() {
  const { ref, inView } = useInView();

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useInfiniteQuery({
    queryKey: ['products', 'infinite'],
    queryFn: ({ pageParam = 0 }) => getProducts({ page: pageParam }),
    getNextPageParam: (lastPage, pages) => 
      lastPage.hasMore ? pages.length : undefined,
    initialPageParam: 0,
  });

  useEffect(() => {
    if (inView && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [inView, hasNextPage, isFetchingNextPage, fetchNextPage]);

  return (
    <div>
      {data?.pages.map((page) =>
        page.products.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))
      )}
      
      <div ref={ref}>
        {isFetchingNextPage && <LoadingSpinner />}
      </div>
    </div>
  );
}

// Optimistic Updates
function ProductLike({ productId }: { productId: string }) {
  const queryClient = useQueryClient();

  const likeMutation = useMutation({
    mutationFn: (id: string) => likeProduct(id),
    onMutate: async (productId) => {
      // 既存のクエリをキャンセル
      await queryClient.cancelQueries({ queryKey: ['product', productId] });

      // 現在のデータを取得
      const previousProduct = queryClient.getQueryData(['product', productId]);

      // 楽観的更新
      queryClient.setQueryData(['product', productId], (old: any) => ({
        ...old,
        likes: old.likes + 1,
        isLiked: true,
      }));

      // ロールバック用のコンテキストを返す
      return { previousProduct };
    },
    onError: (err, productId, context) => {
      // エラー時はロールバック
      queryClient.setQueryData(
        ['product', productId],
        context?.previousProduct
      );
    },
    onSettled: (data, error, productId) => {
      // 成功・失敗に関わらず再フェッチ
      queryClient.invalidateQueries({ queryKey: ['product', productId] });
    },
  });

  return (
    <button onClick={() => likeMutation.mutate(productId)}>
      いいね
    </button>
  );
}
```

### エラーハンドリング / Error Handling

```typescript
// lib/api-client.ts
import axios, { AxiosError } from 'axios';

export class ApiError extends Error {
  constructor(
    public statusCode: number,
    public message: string,
    public code?: string,
    public details?: any
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// リクエストインターセプター
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// レスポンスインターセプター
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError<any>) => {
    if (error.response) {
      // サーバーエラーレスポンス
      const { status, data } = error.response;

      if (status === 401) {
        // 認証エラー
        localStorage.removeItem('token');
        window.location.href = '/login';
      }

      throw new ApiError(
        status,
        data.message || 'エラーが発生しました',
        data.code,
        data.details
      );
    } else if (error.request) {
      // リクエストは送信されたがレスポンスなし
      throw new ApiError(0, 'ネットワークエラーが発生しました');
    } else {
      // その他のエラー
      throw new ApiError(0, error.message);
    }
  }
);

// components/common/error-boundary.tsx
'use client';

import { Component, ReactNode } from 'react';

interface Props {
  children: ReactNode;
  fallback?: (error: Error, reset: () => void) => ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
}

export class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    console.error('ErrorBoundary caught an error:', error, errorInfo);
  }

  reset = () => {
    this.setState({ hasError: false, error: null });
  };

  render() {
    if (this.state.hasError && this.state.error) {
      if (this.props.fallback) {
        return this.props.fallback(this.state.error, this.reset);
      }

      return (
        <div className="flex flex-col items-center justify-center min-h-screen">
          <h2 className="text-2xl font-bold mb-4">エラーが発生しました</h2>
          <p className="text-gray-600 mb-6">{this.state.error.message}</p>
          <button
            onClick={this.reset}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            再試行
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}
```

---

## パフォーマンス最適化 / Performance Optimization

### Core Web Vitals 目標値 / Core Web Vitals Targets

```yaml
core_web_vitals:
  lcp:
    metric: "Largest Contentful Paint"
    target: "< 2.5秒"
    good: "< 2.5秒"
    needs_improvement: "2.5-4.0秒"
    poor: "> 4.0秒"
  
  fid:
    metric: "First Input Delay"
    target: "< 100ms"
    good: "< 100ms"
    needs_improvement: "100-300ms"
    poor: "> 300ms"
  
  cls:
    metric: "Cumulative Layout Shift"
    target: "< 0.1"
    good: "< 0.1"
    needs_improvement: "0.1-0.25"
    poor: "> 0.25"
  
  fcp:
    metric: "First Contentful Paint"
    target: "< 1.8秒"
  
  ttfb:
    metric: "Time to First Byte"
    target: "< 600ms"
```

### 最適化手法 / Optimization Techniques

```typescript
// 1. Code Splitting（コード分割）
// ルートベース分割
import { lazy, Suspense } from 'react';

const Dashboard = lazy(() => import('./pages/dashboard'));
const Settings = lazy(() => import('./pages/settings'));

function App() {
  return (
    <Suspense fallback={<LoadingSpinner />}>
      <Routes>
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/settings" element={<Settings />} />
      </Routes>
    </Suspense>
  );
}

// コンポーネントベース分割
const HeavyComponent = lazy(() => import('./components/heavy-component'));

function Page() {
  const [showHeavy, setShowHeavy] = useState(false);

  return (
    <div>
      <button onClick={() => setShowHeavy(true)}>表示</button>
      {showHeavy && (
        <Suspense fallback={<div>読み込み中...</div>}>
          <HeavyComponent />
        </Suspense>
      )}
    </div>
  );
}

// 2. Image Optimization（画像最適化）
// Next.js Image
import Image from 'next/image';

function ProductImage({ src, alt }: { src: string; alt: string }) {
  return (
    <Image
      src={src}
      alt={alt}
      width={800}
      height={600}
      quality={85}
      placeholder="blur"
      blurDataURL="data:image/jpeg;base64,..."
      loading="lazy"
      sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
    />
  );
}

// 遅延読み込み
function LazyImage({ src, alt }: { src: string; alt: string }) {
  const [imageSrc, setImageSrc] = useState<string | null>(null);
  const imgRef = useRef<HTMLImageElement>(null);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            setImageSrc(src);
            observer.disconnect();
          }
        });
      },
      { rootMargin: '50px' }
    );

    if (imgRef.current) {
      observer.observe(imgRef.current);
    }

    return () => observer.disconnect();
  }, [src]);

  return (
    <img
      ref={imgRef}
      src={imageSrc || 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7'}
      alt={alt}
      loading="lazy"
    />
  );
}

// 3. Memoization（メモ化）
import { memo, useMemo, useCallback } from 'react';

// React.memo
const ExpensiveComponent = memo(function ExpensiveComponent({ 
  data, 
  onAction 
}: { 
  data: any; 
  onAction: () => void;
}) {
  // 高コストな描画処理
  return <div>{/* ... */}</div>;
}, (prevProps, nextProps) => {
  // カスタム比較関数
  return prevProps.data.id === nextProps.data.id;
});

// useMemo
function ProductList({ products }: { products: Product[] }) {
  const sortedProducts = useMemo(() => {
    return [...products].sort((a, b) => b.rating - a.rating);
  }, [products]);

  const totalPrice = useMemo(() => {
    return products.reduce((sum, p) => sum + p.price, 0);
  }, [products]);

  return <div>{/* ... */}</div>;
}

// useCallback
function Parent() {
  const [count, setCount] = useState(0);

  const handleClick = useCallback(() => {
    console.log('clicked', count);
  }, [count]);

  return <Child onClick={handleClick} />;
}

// 4. Virtual Scrolling（仮想スクロール）
import { useVirtualizer } from '@tanstack/react-virtual';

function VirtualList({ items }: { items: any[] }) {
  const parentRef = useRef<HTMLDivElement>(null);

  const virtualizer = useVirtualizer({
    count: items.length,
    getScrollElement: () => parentRef.current,
    estimateSize: () => 50,
    overscan: 5,
  });

  return (
    <div ref={parentRef} style={{ height: '400px', overflow: 'auto' }}>
      <div
        style={{
          height: `${virtualizer.getTotalSize()}px`,
          position: 'relative',
        }}
      >
        {virtualizer.getVirtualItems().map((virtualItem) => (
          <div
            key={virtualItem.key}
            style={{
              position: 'absolute',
              top: 0,
              left: 0,
              width: '100%',
              height: `${virtualItem.size}px`,
              transform: `translateY(${virtualItem.start}px)`,
            }}
          >
            {items[virtualItem.index].name}
          </div>
        ))}
      </div>
    </div>
  );
}

// 5. Debouncing & Throttling
import { useDebouncedCallback } from 'use-debounce';

function SearchInput() {
  const [value, setValue] = useState('');

  const debouncedSearch = useDebouncedCallback(
    (searchTerm: string) => {
      searchProducts(searchTerm);
    },
    500
  );

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = e.target.value;
    setValue(newValue);
    debouncedSearch(newValue);
  };

  return <input value={value} onChange={handleChange} />;
}

// カスタムデバウンスフック
function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => clearTimeout(handler);
  }, [value, delay]);

  return debouncedValue;
}

// 6. Bundle Size Optimization
// Tree shaking対応のインポート
// ❌ Bad
import _ from 'lodash';
import moment from 'moment';

// ✅ Good
import debounce from 'lodash/debounce';
import { format } from 'date-fns';

// 動的インポート
import dynamic from 'next/dynamic';

const Chart = dynamic(() => import('react-chartjs-2'), {
  loading: () => <p>チャート読み込み中...</p>,
  ssr: false,
});

// 7. Web Workers
// heavy-computation.worker.ts
self.addEventListener('message', (e: MessageEvent) => {
  const { data, type } = e.data;

  if (type === 'PROCESS_DATA') {
    const result = processLargeDataset(data);
    self.postMessage({ type: 'RESULT', result });
  }
});

// hooks/use-worker.ts
export function useWorker() {
  const workerRef = useRef<Worker>();

  useEffect(() => {
    workerRef.current = new Worker(
      new URL('../workers/heavy-computation.worker.ts', import.meta.url)
    );

    return () => {
      workerRef.current?.terminate();
    };
  }, []);

  const processData = useCallback((data: any) => {
    return new Promise((resolve) => {
      workerRef.current?.postMessage({ type: 'PROCESS_DATA', data });
      workerRef.current?.addEventListener('message', (e) => {
        if (e.data.type === 'RESULT') {
          resolve(e.data.result);
        }
      });
    });
  }, []);

  return { processData };
}
```

### パフォーマンスモニタリング / Performance Monitoring

```typescript
// lib/performance.ts
export function measureWebVitals() {
  if (typeof window === 'undefined') return;

  // CLS測定
  let cls = 0;
  new PerformanceObserver((list) => {
    for (const entry of list.getEntries()) {
      if (!(entry as any).hadRecentInput) {
        cls += (entry as any).value;
      }
    }
  }).observe({ type: 'layout-shift', buffered: true });

  // LCP測定
  new PerformanceObserver((list) => {
    const entries = list.getEntries();
    const lastEntry = entries[entries.length - 1];
    console.log('LCP:', lastEntry.startTime);
  }).observe({ type: 'largest-contentful-paint', buffered: true });

  // FID測定
  new PerformanceObserver((list) => {
    for (const entry of list.getEntries()) {
      const fid = (entry as any).processingStart - entry.startTime;
      console.log('FID:', fid);
    }
  }).observe({ type: 'first-input', buffered: true });
}

// Next.js Web Vitals
// app/layout.tsx
import { Analytics } from '@vercel/analytics/react';
import { SpeedInsights } from '@vercel/speed-insights/next';

export default function RootLayout({ 
  children 
}: { 
  children: React.ReactNode 
}) {
  return (
    <html>
      <body>
        {children}
        <Analytics />
        <SpeedInsights />
      </body>
    </html>
  );
}

// カスタムWeb Vitals報告
export function reportWebVitals(metric: any) {
  const { id, name, label, value } = metric;

  if (window.gtag) {
    window.gtag('event', name, {
      event_category: label === 'web-vital' ? 'Web Vitals' : 'Next.js custom metric',
      value: Math.round(name === 'CLS' ? value * 1000 : value),
      event_label: id,
      non_interaction: true,
    });
  }
}
```

---

## SEO対策 / SEO Optimization

### メタデータ管理 / Metadata Management

```typescript
// Next.js App Router Metadata
// app/layout.tsx
import type { Metadata } from 'next';

export const metadata: Metadata = {
  title: {
    default: 'My App',
    template: '%s | My App',
  },
  description: 'アプリケーションの説明',
  keywords: ['キーワード1', 'キーワード2'],
  authors: [{ name: 'Company Name' }],
  creator: 'Company Name',
  publisher: 'Company Name',
  robots: {
    index: true,
    follow: true,
    googleBot: {
      index: true,
      follow: true,
      'max-video-preview': -1,
      'max-image-preview': 'large',
      'max-snippet': -1,
    },
  },
  openGraph: {
    type: 'website',
    locale: 'ja_JP',
    url: 'https://example.com',
    title: 'My App',
    description: 'アプリケーションの説明',
    siteName: 'My App',
    images: [
      {
        url: 'https://example.com/og-image.jpg',
        width: 1200,
        height: 630,
        alt: 'My App OG Image',
      },
    ],
  },
  twitter: {
    card: 'summary_large_image',
    title: 'My App',
    description: 'アプリケーションの説明',
    creator: '@handle',
    images: ['https://example.com/twitter-image.jpg'],
  },
  verification: {
    google: 'google-site-verification-code',
  },
};

// ページ固有のメタデータ
// app/products/[id]/page.tsx
export async function generateMetadata({
  params,
}: {
  params: { id: string };
}): Promise<Metadata> {
  const product = await getProduct(params.id);

  return {
    title: product.name,
    description: product.description,
    openGraph: {
      title: product.name,
      description: product.description,
      images: [product.image],
    },
  };
}

// 動的OGイメージ生成
// app/api/og/route.tsx
import { ImageResponse } from 'next/og';

export async function GET(request: Request) {
  const { searchParams } = new URL(request.url);
  const title = searchParams.get('title');

  return new ImageResponse(
    (
      <div
        style={{
          display: 'flex',
          fontSize: 60,
          color: 'white',
          background: 'linear-gradient(to bottom, #000, #111)',
          width: '100%',
          height: '100%',
          padding: '50px',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        {title}
      </div>
    ),
    {
      width: 1200,
      height: 630,
    }
  );
}
```

### 構造化データ / Structured Data

```typescript
// components/seo/structured-data.tsx
interface StructuredDataProps {
  type: 'Product' | 'Article' | 'Organization' | 'WebSite';
  data: any;
}

export function StructuredData({ type, data }: StructuredDataProps) {
  const schemas = {
    Product: {
      '@context': 'https://schema.org',
      '@type': 'Product',
      name: data.name,
      image: data.image,
      description: data.description,
      sku: data.sku,
      brand: {
        '@type': 'Brand',
        name: data.brand,
      },
      offers: {
        '@type': 'Offer',
        url: data.url,
        priceCurrency: 'JPY',
        price: data.price,
        availability: 'https://schema.org/InStock',
      },
      aggregateRating: {
        '@type': 'AggregateRating',
        ratingValue: data.rating,
        reviewCount: data.reviewCount,
      },
    },
    Article: {
      '@context': 'https://schema.org',
      '@type': 'Article',
      headline: data.title,
      image: data.image,
      author: {
        '@type': 'Person',
        name: data.author,
      },
      datePublished: data.publishedAt,
      dateModified: data.modifiedAt,
      publisher: {
        '@type': 'Organization',
        name: 'Company Name',
        logo: {
          '@type': 'ImageObject',
          url: 'https://example.com/logo.png',
        },
      },
    },
  };

  return (
    <script
      type="application/ld+json"
      dangerouslySetInnerHTML={{ __html: JSON.stringify(schemas[type]) }}
    />
  );
}

// 使用例
function ProductPage({ product }: { product: Product }) {
  return (
    <>
      <StructuredData type="Product" data={product} />
      <div>{/* Product UI */}</div>
    </>
  );
}
```

### sitemap.xml と robots.txt

```typescript
// app/sitemap.ts
import { MetadataRoute } from 'next';

export default async function sitemap(): Promise<MetadataRoute.Sitemap> {
  const products = await getAllProducts();

  const productUrls = products.map((product) => ({
    url: `https://example.com/products/${product.id}`,
    lastModified: product.updatedAt,
    changeFrequency: 'weekly' as const,
    priority: 0.8,
  }));

  return [
    {
      url: 'https://example.com',
      lastModified: new Date(),
      changeFrequency: 'yearly',
      priority: 1,
    },
    {
      url: 'https://example.com/about',
      lastModified: new Date(),
      changeFrequency: 'monthly',
      priority: 0.8,
    },
    ...productUrls,
  ];
}

// app/robots.ts
import { MetadataRoute } from 'next';

export default function robots(): MetadataRoute.Robots {
  return {
    rules: [
      {
        userAgent: '*',
        allow: '/',
        disallow: ['/admin/', '/api/', '/private/'],
      },
      {
        userAgent: 'Googlebot',
        allow: '/',
        crawlDelay: 2,
      },
    ],
    sitemap: 'https://example.com/sitemap.xml',
  };
}
```

---

## アクセシビリティ / Accessibility

### WCAG 2.1準拠 / WCAG 2.1 Compliance

```yaml
accessibility:
  target_level: "AA"
  
  principles:
    perceivable:
      - "代替テキスト提供"
      - "適切なコントラスト比（4.5:1以上）"
      - "テキストサイズ調整可能"
    
    operable:
      - "キーボード操作可能"
      - "十分な時間提供"
      - "フォーカス管理"
    
    understandable:
      - "予測可能な動作"
      - "入力支援"
      - "エラーメッセージの提供"
    
    robust:
      - "セマンティックHTML"
      - "ARIA属性の適切な使用"
```

### アクセシブルなコンポーネント / Accessible Components

```typescript
// components/ui/button.tsx
interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary';
  loading?: boolean;
  loadingText?: string;
  children: React.ReactNode;
}

export function Button({
  variant = 'primary',
  loading = false,
  loadingText = '読み込み中...',
  disabled,
  children,
  ...props
}: ButtonProps) {
  return (
    <button
      {...props}
      disabled={disabled || loading}
      aria-disabled={disabled || loading}
      aria-busy={loading}
      aria-label={loading ? loadingText : undefined}
    >
      {loading ? (
        <>
          <span className="sr-only">{loadingText}</span>
          <Spinner aria-hidden="true" />
        </>
      ) : (
        children
      )}
    </button>
  );
}

// components/ui/modal.tsx
import FocusTrap from 'focus-trap-react';
import { useEffect } from 'react';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
}

export function Modal({ isOpen, onClose, title, children }: ModalProps) {
  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        onClose();
      }
    };

    if (isOpen) {
      document.addEventListener('keydown', handleEscape);
      document.body.style.overflow = 'hidden';
    }

    return () => {
      document.removeEventListener('keydown', handleEscape);
      document.body.style.overflow = 'unset';
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="modal-title"
      className="fixed inset-0 z-50 flex items-center justify-center"
    >
      <div
        className="absolute inset-0 bg-black bg-opacity-50"
        onClick={onClose}
        aria-hidden="true"
      />

      <FocusTrap>
        <div className="relative bg-white rounded-lg shadow-xl max-w-md w-full p-6">
          <h2 id="modal-title" className="text-xl font-bold mb-4">
            {title}
          </h2>

          <div>{children}</div>

          <button
            onClick={onClose}
            className="absolute top-4 right-4"
            aria-label="モーダルを閉じる"
          >
            <XIcon />
          </button>
        </div>
      </FocusTrap>
    </div>
  );
}

// components/ui/form-field.tsx
interface FormFieldProps {
  label: string;
  id: string;
  error?: string;
  required?: boolean;
  children: React.ReactElement;
}

export function FormField({
  label,
  id,
  error,
  required,
  children,
}: FormFieldProps) {
  const errorId = `${id}-error`;

  return (
    <div className="mb-4">
      <label htmlFor={id} className="block mb-2 font-medium">
        {label}
        {required && <span aria-label="必須" className="text-red-600"> *</span>}
      </label>

      {React.cloneElement(children, {
        id,
        'aria-invalid': !!error,
        'aria-describedby': error ? errorId : undefined,
        'aria-required': required,
      })}

      {error && (
        <p id={errorId} className="mt-1 text-sm text-red-600" role="alert">
          {error}
        </p>
      )}
    </div>
  );
}

// スクリーンリーダー専用テキスト
export function ScreenReaderOnly({ 
  children 
}: { 
  children: React.ReactNode 
}) {
  return (
    <span className="sr-only">
      {children}
    </span>
  );
}
```


---

## セキュリティ / Security

### セキュリティ原則 / Security Principles

```yaml
security_principles:
  defense_in_depth: "多層防御"
  least_privilege: "最小権限の原則"
  security_by_design: "設計段階からのセキュリティ"
  zero_trust: "ゼロトラストアーキテクチャ"
```

### 認証とアクセス制御 / Authentication and Access Control

#### JWT ベース認証 / JWT-Based Authentication

```typescript
// lib/auth/jwt.ts
import { jwtVerify, SignJWT } from 'jose';

const JWT_SECRET = new TextEncoder().encode(
  process.env.JWT_SECRET!
);

export interface JWTPayload {
  userId: string;
  email: string;
  role: string;
  permissions: string[];
}

export async function createToken(
  payload: JWTPayload
): Promise<string> {
  return new SignJWT(payload)
    .setProtectedHeader({ alg: 'HS256' })
    .setIssuedAt()
    .setExpirationTime('24h')
    .sign(JWT_SECRET);
}

export async function verifyToken(
  token: string
): Promise<JWTPayload> {
  const { payload } = await jwtVerify(token, JWT_SECRET);
  return payload as JWTPayload;
}
```

#### セッション管理 / Session Management

```typescript
// lib/auth/session.ts
import { cookies } from 'next/headers';
import { verifyToken, type JWTPayload } from './jwt';

export async function getSession(): Promise<JWTPayload | null> {
  const cookieStore = cookies();
  const token = cookieStore.get('auth-token')?.value;
  
  if (!token) return null;
  
  try {
    return await verifyToken(token);
  } catch {
    return null;
  }
}

export async function requireAuth(): Promise<JWTPayload> {
  const session = await getSession();
  
  if (!session) {
    throw new Error('Unauthorized');
  }
  
  return session;
}

export function requireRole(
  session: JWTPayload,
  allowedRoles: string[]
): void {
  if (!allowedRoles.includes(session.role)) {
    throw new Error('Forbidden');
  }
}
```

### XSS対策 / XSS Prevention

#### コンテンツセキュリティポリシー / Content Security Policy

```typescript
// middleware.ts
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
  const response = NextResponse.next();
  
  // CSP ヘッダー設定
  response.headers.set(
    'Content-Security-Policy',
    [
      "default-src 'self'",
      "script-src 'self' 'unsafe-inline' 'unsafe-eval'",
      "style-src 'self' 'unsafe-inline'",
      "img-src 'self' data: https:",
      "font-src 'self' data:",
      "connect-src 'self' https://api.example.com",
      "frame-ancestors 'none'",
      "base-uri 'self'",
      "form-action 'self'"
    ].join('; ')
  );
  
  // その他のセキュリティヘッダー
  response.headers.set('X-Frame-Options', 'DENY');
  response.headers.set('X-Content-Type-Options', 'nosniff');
  response.headers.set('Referrer-Policy', 'strict-origin-when-cross-origin');
  response.headers.set(
    'Permissions-Policy',
    'camera=(), microphone=(), geolocation=()'
  );
  
  return response;
}
```

#### サニタイゼーション / Sanitization

```typescript
// lib/security/sanitize.ts
import DOMPurify from 'isomorphic-dompurify';

export function sanitizeHtml(dirty: string): string {
  return DOMPurify.sanitize(dirty, {
    ALLOWED_TAGS: [
      'p', 'br', 'strong', 'em', 'u', 'h1', 'h2', 'h3',
      'ul', 'ol', 'li', 'a', 'img'
    ],
    ALLOWED_ATTR: ['href', 'src', 'alt', 'title'],
    ALLOW_DATA_ATTR: false
  });
}

// 安全なHTML表示コンポーネント
export function SafeHtml({ 
  html 
}: { 
  html: string 
}) {
  return (
    <div
      dangerouslySetInnerHTML={{ 
        __html: sanitizeHtml(html) 
      }}
    />
  );
}
```

### CSRF対策 / CSRF Prevention

```typescript
// lib/security/csrf.ts
import { cookies } from 'next/headers';
import { randomBytes } from 'crypto';

export function generateCsrfToken(): string {
  return randomBytes(32).toString('hex');
}

export async function getCsrfToken(): Promise<string> {
  const cookieStore = cookies();
  let token = cookieStore.get('csrf-token')?.value;
  
  if (!token) {
    token = generateCsrfToken();
    cookieStore.set('csrf-token', token, {
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'strict',
      maxAge: 60 * 60 * 24 // 24時間
    });
  }
  
  return token;
}

export async function verifyCsrfToken(token: string): Promise<boolean> {
  const cookieStore = cookies();
  const storedToken = cookieStore.get('csrf-token')?.value;
  
  return storedToken === token;
}
```

### 入力検証 / Input Validation

```typescript
// lib/security/validation.ts
import { z } from 'zod';

// スキーマ定義
export const userSchema = z.object({
  email: z.string().email().max(255),
  password: z.string().min(8).max(100),
  name: z.string().min(1).max(100),
  age: z.number().int().positive().max(150).optional()
});

// 安全なバリデーション関数
export function validateInput<T>(
  schema: z.ZodSchema<T>,
  data: unknown
): { success: true; data: T } | { success: false; errors: string[] } {
  try {
    const validated = schema.parse(data);
    return { success: true, data: validated };
  } catch (error) {
    if (error instanceof z.ZodError) {
      return {
        success: false,
        errors: error.errors.map(e => e.message)
      };
    }
    return {
      success: false,
      errors: ['Validation failed']
    };
  }
}

// Server Action での使用例
export async function createUser(formData: FormData) {
  const result = validateInput(userSchema, {
    email: formData.get('email'),
    password: formData.get('password'),
    name: formData.get('name')
  });
  
  if (!result.success) {
    return { errors: result.errors };
  }
  
  // 安全に検証済みデータを使用
  const user = result.data;
  // ... ユーザー作成処理
}
```

### 機密データの保護 / Sensitive Data Protection

```typescript
// lib/security/encryption.ts
import { createCipheriv, createDecipheriv, randomBytes } from 'crypto';

const ENCRYPTION_KEY = Buffer.from(
  process.env.ENCRYPTION_KEY!,
  'hex'
); // 32バイト
const IV_LENGTH = 16;

export function encrypt(text: string): string {
  const iv = randomBytes(IV_LENGTH);
  const cipher = createCipheriv('aes-256-cbc', ENCRYPTION_KEY, iv);
  
  let encrypted = cipher.update(text, 'utf8', 'hex');
  encrypted += cipher.final('hex');
  
  return iv.toString('hex') + ':' + encrypted;
}

export function decrypt(text: string): string {
  const parts = text.split(':');
  const iv = Buffer.from(parts.shift()!, 'hex');
  const encrypted = parts.join(':');
  
  const decipher = createDecipheriv('aes-256-cbc', ENCRYPTION_KEY, iv);
  
  let decrypted = decipher.update(encrypted, 'hex', 'utf8');
  decrypted += decipher.final('utf8');
  
  return decrypted;
}

// 環境変数の安全な管理
export function getSecretValue(key: string): string {
  const value = process.env[key];
  
  if (!value) {
    throw new Error(`Missing required environment variable: ${key}`);
  }
  
  return value;
}
```

### レート制限 / Rate Limiting

```typescript
// lib/security/rate-limit.ts
import { Redis } from '@upstash/redis';

const redis = new Redis({
  url: process.env.UPSTASH_REDIS_REST_URL!,
  token: process.env.UPSTASH_REDIS_REST_TOKEN!
});

export interface RateLimitConfig {
  interval: number; // ミリ秒
  maxRequests: number;
}

export async function rateLimit(
  identifier: string,
  config: RateLimitConfig
): Promise<{
  success: boolean;
  remaining: number;
  reset: number;
}> {
  const key = `rate-limit:${identifier}`;
  const now = Date.now();
  const window = now - config.interval;
  
  // 古いエントリを削除
  await redis.zremrangebyscore(key, 0, window);
  
  // 現在のリクエスト数を取得
  const requestCount = await redis.zcard(key);
  
  if (requestCount >= config.maxRequests) {
    const oldestRequest = await redis.zrange(key, 0, 0);
    const resetTime = parseInt(oldestRequest[0]) + config.interval;
    
    return {
      success: false,
      remaining: 0,
      reset: resetTime
    };
  }
  
  // 新しいリクエストを記録
  await redis.zadd(key, { score: now, member: now.toString() });
  await redis.expire(key, Math.ceil(config.interval / 1000));
  
  return {
    success: true,
    remaining: config.maxRequests - requestCount - 1,
    reset: now + config.interval
  };
}

// API Route での使用例
export async function POST(request: Request) {
  const ip = request.headers.get('x-forwarded-for') || 'unknown';
  
  const { success, remaining, reset } = await rateLimit(ip, {
    interval: 60 * 1000, // 1分
    maxRequests: 10
  });
  
  if (!success) {
    return new Response('Too Many Requests', {
      status: 429,
      headers: {
        'X-RateLimit-Remaining': '0',
        'X-RateLimit-Reset': reset.toString()
      }
    });
  }
  
  // ... 処理を続行
}
```

### セキュアなCookie設定 / Secure Cookie Configuration

```typescript
// lib/security/cookies.ts
import { cookies } from 'next/headers';

export interface CookieOptions {
  name: string;
  value: string;
  maxAge?: number;
  httpOnly?: boolean;
  secure?: boolean;
  sameSite?: 'strict' | 'lax' | 'none';
}

export function setSecureCookie(options: CookieOptions): void {
  const cookieStore = cookies();
  
  cookieStore.set(options.name, options.value, {
    httpOnly: options.httpOnly ?? true,
    secure: options.secure ?? process.env.NODE_ENV === 'production',
    sameSite: options.sameSite ?? 'strict',
    maxAge: options.maxAge ?? 60 * 60 * 24, // デフォルト24時間
    path: '/'
  });
}

export function deleteSecureCookie(name: string): void {
  const cookieStore = cookies();
  cookieStore.delete(name);
}
```

---

## テスト戦略 / Testing Strategy

### テストピラミッド / Testing Pyramid

```yaml
testing_pyramid:
  unit_tests: "70%"
  integration_tests: "20%"
  e2e_tests: "10%"
```

### ユニットテスト / Unit Testing

#### コンポーネントテスト / Component Testing

```typescript
// components/Button/__tests__/Button.test.tsx
import { render, screen, fireEvent } from '@testing-library/react';
import { Button } from '../Button';

describe('Button', () => {
  it('renders with correct text', () => {
    render(<Button>Click me</Button>);
    expect(screen.getByText('Click me')).toBeInTheDocument();
  });
  
  it('calls onClick when clicked', () => {
    const handleClick = jest.fn();
    render(<Button onClick={handleClick}>Click me</Button>);
    
    fireEvent.click(screen.getByText('Click me'));
    expect(handleClick).toHaveBeenCalledTimes(1);
  });
  
  it('is disabled when disabled prop is true', () => {
    render(<Button disabled>Click me</Button>);
    expect(screen.getByText('Click me')).toBeDisabled();
  });
  
  it('applies variant styles correctly', () => {
    const { rerender } = render(<Button variant="primary">Primary</Button>);
    expect(screen.getByText('Primary')).toHaveClass('bg-blue-600');
    
    rerender(<Button variant="secondary">Secondary</Button>);
    expect(screen.getByText('Secondary')).toHaveClass('bg-gray-600');
  });
});
```

#### カスタムフックテスト / Custom Hook Testing

```typescript
// hooks/__tests__/useCounter.test.ts
import { renderHook, act } from '@testing-library/react';
import { useCounter } from '../useCounter';

describe('useCounter', () => {
  it('initializes with default value', () => {
    const { result } = renderHook(() => useCounter());
    expect(result.current.count).toBe(0);
  });
  
  it('initializes with custom value', () => {
    const { result } = renderHook(() => useCounter(10));
    expect(result.current.count).toBe(10);
  });
  
  it('increments count', () => {
    const { result } = renderHook(() => useCounter());
    
    act(() => {
      result.current.increment();
    });
    
    expect(result.current.count).toBe(1);
  });
  
  it('decrements count', () => {
    const { result } = renderHook(() => useCounter(5));
    
    act(() => {
      result.current.decrement();
    });
    
    expect(result.current.count).toBe(4);
  });
  
  it('resets count', () => {
    const { result } = renderHook(() => useCounter(10));
    
    act(() => {
      result.current.increment();
      result.current.reset();
    });
    
    expect(result.current.count).toBe(10);
  });
});
```

### 統合テスト / Integration Testing

```typescript
// app/api/users/__tests__/route.test.ts
import { POST } from '../route';
import { prisma } from '@/lib/prisma';

// Prisma のモック
jest.mock('@/lib/prisma', () => ({
  prisma: {
    user: {
      create: jest.fn(),
      findUnique: jest.fn()
    }
  }
}));

describe('/api/users', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  
  describe('POST', () => {
    it('creates a new user successfully', async () => {
      const mockUser = {
        id: '1',
        email: 'test@example.com',
        name: 'Test User',
        createdAt: new Date()
      };
      
      (prisma.user.create as jest.Mock).mockResolvedValue(mockUser);
      
      const request = new Request('http://localhost:3000/api/users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: 'test@example.com',
          name: 'Test User',
          password: 'password123'
        })
      });
      
      const response = await POST(request);
      const data = await response.json();
      
      expect(response.status).toBe(201);
      expect(data).toEqual({
        id: mockUser.id,
        email: mockUser.email,
        name: mockUser.name
      });
      expect(prisma.user.create).toHaveBeenCalledWith({
        data: expect.objectContaining({
          email: 'test@example.com',
          name: 'Test User'
        })
      });
    });
    
    it('returns 400 for invalid email', async () => {
      const request = new Request('http://localhost:3000/api/users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: 'invalid-email',
          name: 'Test User',
          password: 'password123'
        })
      });
      
      const response = await POST(request);
      
      expect(response.status).toBe(400);
    });
    
    it('returns 409 for duplicate email', async () => {
      (prisma.user.findUnique as jest.Mock).mockResolvedValue({
        id: '1',
        email: 'existing@example.com'
      });
      
      const request = new Request('http://localhost:3000/api/users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: 'existing@example.com',
          name: 'Test User',
          password: 'password123'
        })
      });
      
      const response = await POST(request);
      
      expect(response.status).toBe(409);
    });
  });
});
```

### E2Eテスト / End-to-End Testing

```typescript
// e2e/auth.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Authentication Flow', () => {
  test('user can sign up, log in, and log out', async ({ page }) => {
    // サインアップ
    await page.goto('/signup');
    
    await page.fill('input[name="email"]', 'test@example.com');
    await page.fill('input[name="password"]', 'Password123!');
    await page.fill('input[name="confirmPassword"]', 'Password123!');
    await page.click('button[type="submit"]');
    
    // ダッシュボードにリダイレクトされることを確認
    await expect(page).toHaveURL('/dashboard');
    await expect(page.locator('h1')).toContainText('Dashboard');
    
    // ログアウト
    await page.click('button:has-text("Logout")');
    await expect(page).toHaveURL('/login');
    
    // ログイン
    await page.fill('input[name="email"]', 'test@example.com');
    await page.fill('input[name="password"]', 'Password123!');
    await page.click('button[type="submit"]');
    
    // 再びダッシュボードへ
    await expect(page).toHaveURL('/dashboard');
  });
  
  test('shows error for invalid credentials', async ({ page }) => {
    await page.goto('/login');
    
    await page.fill('input[name="email"]', 'wrong@example.com');
    await page.fill('input[name="password"]', 'wrongpassword');
    await page.click('button[type="submit"]');
    
    // エラーメッセージの確認
    await expect(page.locator('[role="alert"]')).toContainText(
      'Invalid credentials'
    );
    
    // ログインページに留まることを確認
    await expect(page).toHaveURL('/login');
  });
});
```

### ビジュアルリグレッションテスト / Visual Regression Testing

```typescript
// e2e/visual.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Visual Regression', () => {
  test('homepage matches screenshot', async ({ page }) => {
    await page.goto('/');
    await expect(page).toHaveScreenshot('homepage.png');
  });
  
  test('dashboard matches screenshot', async ({ page }) => {
    // 認証済みの状態でテスト
    await page.goto('/dashboard');
    
    // データの読み込みを待つ
    await page.waitForSelector('[data-testid="dashboard-content"]');
    
    await expect(page).toHaveScreenshot('dashboard.png', {
      fullPage: true
    });
  });
  
  test('modal matches screenshot', async ({ page }) => {
    await page.goto('/');
    await page.click('button:has-text("Open Modal")');
    
    // モーダルのアニメーション完了を待つ
    await page.waitForTimeout(300);
    
    await expect(page.locator('[role="dialog"]')).toHaveScreenshot(
      'modal.png'
    );
  });
});
```

### テストカバレッジ / Test Coverage

```javascript
// jest.config.js
module.exports = {
  collectCoverageFrom: [
    'app/**/*.{js,jsx,ts,tsx}',
    'components/**/*.{js,jsx,ts,tsx}',
    'lib/**/*.{js,jsx,ts,tsx}',
    '!**/*.d.ts',
    '!**/node_modules/**',
    '!**/.next/**',
    '!**/coverage/**'
  ],
  coverageThreshold: {
    global: {
      branches: 80,
      functions: 80,
      lines: 80,
      statements: 80
    }
  },
  testMatch: [
    '**/__tests__/**/*.[jt]s?(x)',
    '**/?(*.)+(spec|test).[jt]s?(x)'
  ]
};
```


---

## 国際化 / Internationalization

### 国際化戦略 / i18n Strategy

```yaml
i18n_strategy:
  library: "next-intl"
  supported_languages:
    - ja: "日本語"
    - en: "English"
    - zh: "中文"
    - ko: "한국어"
  default_language: "ja"
  fallback_language: "en"
```

### Next-intl 設定 / Next-intl Configuration

```typescript
// i18n.ts
import { getRequestConfig } from 'next-intl/server';
import { notFound } from 'next/navigation';

export const locales = ['ja', 'en', 'zh', 'ko'] as const;
export type Locale = (typeof locales)[number];

export default getRequestConfig(async ({ locale }) => {
  // サポートされているロケールかチェック
  if (!locales.includes(locale as Locale)) {
    notFound();
  }

  return {
    messages: (await import(`./messages/${locale}.json`)).default,
    timeZone: 'Asia/Tokyo',
    now: new Date()
  };
});
```

```typescript
// middleware.ts
import createMiddleware from 'next-intl/middleware';
import { locales } from './i18n';

export default createMiddleware({
  locales,
  defaultLocale: 'ja',
  localePrefix: 'as-needed'
});

export const config = {
  matcher: ['/((?!api|_next|_vercel|.*\..*).*)']
};
```

### 翻訳ファイルの構造 / Translation File Structure

```json
// messages/ja.json
{
  "common": {
    "loading": "読み込み中...",
    "error": "エラーが発生しました",
    "success": "成功しました",
    "cancel": "キャンセル",
    "save": "保存",
    "delete": "削除",
    "edit": "編集",
    "create": "作成"
  },
  "auth": {
    "login": "ログイン",
    "logout": "ログアウト",
    "signup": "サインアップ",
    "email": "メールアドレス",
    "password": "パスワード",
    "forgotPassword": "パスワードをお忘れですか？",
    "errors": {
      "invalidCredentials": "メールアドレスまたはパスワードが正しくありません",
      "emailRequired": "メールアドレスは必須です",
      "passwordRequired": "パスワードは必須です"
    }
  },
  "navigation": {
    "home": "ホーム",
    "dashboard": "ダッシュボード",
    "settings": "設定",
    "profile": "プロフィール"
  },
  "dashboard": {
    "title": "ダッシュボード",
    "welcome": "{name}さん、ようこそ",
    "stats": {
      "users": "{count}人のユーザー",
      "revenue": "収益: {amount}円",
      "growth": "成長率: {percentage}%"
    }
  }
}
```

```json
// messages/en.json
{
  "common": {
    "loading": "Loading...",
    "error": "An error occurred",
    "success": "Success",
    "cancel": "Cancel",
    "save": "Save",
    "delete": "Delete",
    "edit": "Edit",
    "create": "Create"
  },
  "auth": {
    "login": "Login",
    "logout": "Logout",
    "signup": "Sign Up",
    "email": "Email",
    "password": "Password",
    "forgotPassword": "Forgot password?",
    "errors": {
      "invalidCredentials": "Invalid email or password",
      "emailRequired": "Email is required",
      "passwordRequired": "Password is required"
    }
  },
  "navigation": {
    "home": "Home",
    "dashboard": "Dashboard",
    "settings": "Settings",
    "profile": "Profile"
  },
  "dashboard": {
    "title": "Dashboard",
    "welcome": "Welcome, {name}",
    "stats": {
      "users": "{count} users",
      "revenue": "Revenue: ${amount}",
      "growth": "Growth: {percentage}%"
    }
  }
}
```

### コンポーネントでの使用 / Component Usage

```typescript
// app/[locale]/dashboard/page.tsx
import { useTranslations } from 'next-intl';
import { getTranslations } from 'next-intl/server';

// クライアントコンポーネント
export function DashboardClient() {
  const t = useTranslations('dashboard');
  const user = { name: 'John Doe' };

  return (
    <div>
      <h1>{t('title')}</h1>
      <p>{t('welcome', { name: user.name })}</p>
      
      <div className="stats">
        <div>{t('stats.users', { count: 1234 })}</div>
        <div>{t('stats.revenue', { amount: '1,000,000' })}</div>
        <div>{t('stats.growth', { percentage: 15.5 })}</div>
      </div>
    </div>
  );
}

// サーバーコンポーネント
export default async function DashboardPage() {
  const t = await getTranslations('dashboard');

  return (
    <div>
      <h1>{t('title')}</h1>
      <DashboardClient />
    </div>
  );
}
```

### 複数形対応 / Plural Forms

```json
// messages/ja.json
{
  "items": {
    "count": "{count}個のアイテム"
  }
}
```

```json
// messages/en.json
{
  "items": {
    "count": {
      "one": "{count} item",
      "other": "{count} items"
    }
  }
}
```

```typescript
// 使用例
import { useTranslations } from 'next-intl';

export function ItemCount({ count }: { count: number }) {
  const t = useTranslations('items');
  
  return <p>{t('count', { count })}</p>;
  // 日本語: "5個のアイテム"
  // 英語: "1 item" または "5 items"
}
```

### 日時・数値のフォーマット / Date and Number Formatting

```typescript
// lib/i18n/formatters.ts
import { useFormatter } from 'next-intl';

export function FormattedContent() {
  const format = useFormatter();
  
  const now = new Date();
  const price = 1234567.89;
  const percentage = 0.155;

  return (
    <div>
      {/* 日付フォーマット */}
      <p>{format.dateTime(now, { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
      })}</p>
      {/* 日本語: 2024年1月15日 */}
      {/* 英語: January 15, 2024 */}

      {/* 相対時間 */}
      <p>{format.relativeTime(now, { unit: 'day' })}</p>
      {/* 日本語: 今日 */}
      {/* 英語: today */}

      {/* 数値フォーマット */}
      <p>{format.number(price, { 
        style: 'currency', 
        currency: 'JPY' 
      })}</p>
      {/* 日本語: ¥1,234,568 */}
      {/* 英語: ¥1,234,568 */}

      {/* パーセンテージ */}
      <p>{format.number(percentage, { 
        style: 'percent', 
        maximumFractionDigits: 2 
      })}</p>
      {/* 15.5% */}
    </div>
  );
}
```

### 言語切り替え / Language Switcher

```typescript
// components/LanguageSwitcher.tsx
'use client';

import { useLocale } from 'next-intl';
import { useRouter, usePathname } from 'next/navigation';
import { locales, type Locale } from '@/i18n';

const languageNames: Record<Locale, string> = {
  ja: '日本語',
  en: 'English',
  zh: '中文',
  ko: '한국어'
};

export function LanguageSwitcher() {
  const locale = useLocale();
  const router = useRouter();
  const pathname = usePathname();

  const handleChange = (newLocale: Locale) => {
    // パスから現在のロケールを削除
    const pathnameWithoutLocale = pathname.replace(`/${locale}`, '');
    
    // 新しいロケールでパスを構築
    const newPath = `/${newLocale}${pathnameWithoutLocale}`;
    
    router.push(newPath);
  };

  return (
    <select
      value={locale}
      onChange={(e) => handleChange(e.target.value as Locale)}
      className="px-3 py-2 border rounded-md"
    >
      {locales.map((loc) => (
        <option key={loc} value={loc}>
          {languageNames[loc]}
        </option>
      ))}
    </select>
  );
}
```

### 動的翻訳 / Dynamic Translations

```typescript
// lib/i18n/dynamic.ts
import { getTranslations } from 'next-intl/server';

export async function getDynamicTranslation(
  namespace: string,
  key: string,
  params?: Record<string, string | number>
) {
  const t = await getTranslations(namespace);
  return t(key, params);
}

// 使用例
export async function ServerComponent({ itemId }: { itemId: string }) {
  const item = await fetchItem(itemId);
  const t = await getTranslations('items');
  
  const translatedName = item.nameKey 
    ? t(item.nameKey) 
    : item.name;

  return <h1>{translatedName}</h1>;
}
```

### メタデータの国際化 / Metadata Internationalization

```typescript
// app/[locale]/layout.tsx
import { getTranslations } from 'next-intl/server';
import type { Metadata } from 'next';

export async function generateMetadata({ 
  params: { locale } 
}: { 
  params: { locale: string } 
}): Promise<Metadata> {
  const t = await getTranslations({ locale, namespace: 'metadata' });

  return {
    title: t('title'),
    description: t('description'),
    keywords: t('keywords'),
    openGraph: {
      title: t('og.title'),
      description: t('og.description'),
      locale: locale,
      alternateLocale: ['ja', 'en', 'zh', 'ko'].filter(l => l !== locale)
    }
  };
}
```

### RTL対応 / RTL Support

```typescript
// lib/i18n/direction.ts
import type { Locale } from '@/i18n';

const rtlLocales: Locale[] = ['ar', 'he']; // 将来の対応用

export function getDirection(locale: Locale): 'ltr' | 'rtl' {
  return rtlLocales.includes(locale) ? 'rtl' : 'ltr';
}

// app/[locale]/layout.tsx
import { getDirection } from '@/lib/i18n/direction';

export default function RootLayout({
  children,
  params: { locale }
}: {
  children: React.ReactNode;
  params: { locale: Locale };
}) {
  const dir = getDirection(locale);

  return (
    <html lang={locale} dir={dir}>
      <body>{children}</body>
    </html>
  );
}
```

---

## ビルドと最適化 / Build and Optimization

### ビルド設定 / Build Configuration

```javascript
// next.config.js
/** @type {import('next').NextConfig} */
const nextConfig = {
  // 出力モード
  output: 'standalone', // Dockerデプロイ用

  // 画像最適化
  images: {
    formats: ['image/avif', 'image/webp'],
    deviceSizes: [640, 750, 828, 1080, 1200, 1920, 2048, 3840],
    imageSizes: [16, 32, 48, 64, 96, 128, 256, 384],
    domains: ['cdn.example.com'],
    remotePatterns: [
      {
        protocol: 'https',
        hostname: '**.example.com',
        port: '',
        pathname: '/images/**'
      }
    ]
  },

  // コンパイラ最適化
  compiler: {
    removeConsole: process.env.NODE_ENV === 'production' ? {
      exclude: ['error', 'warn']
    } : false
  },

  // Experimental features
  experimental: {
    serverActions: {
      bodySizeLimit: '2mb'
    },
    optimizePackageImports: [
      '@mui/material',
      '@mui/icons-material',
      'lodash'
    ]
  },

  // Webpack設定
  webpack: (config, { isServer }) => {
    if (!isServer) {
      // クライアント側のバンドルサイズ削減
      config.resolve.alias = {
        ...config.resolve.alias,
        '@mui/material': '@mui/material/esm'
      };
    }

    // Bundle Analyzer（開発時のみ）
    if (process.env.ANALYZE === 'true') {
      const { BundleAnalyzerPlugin } = require('webpack-bundle-analyzer');
      config.plugins.push(
        new BundleAnalyzerPlugin({
          analyzerMode: 'static',
          reportFilename: isServer
            ? '../analyze/server.html'
            : './analyze/client.html'
        })
      );
    }

    return config;
  },

  // ヘッダー設定
  async headers() {
    return [
      {
        source: '/:path*',
        headers: [
          {
            key: 'X-DNS-Prefetch-Control',
            value: 'on'
          },
          {
            key: 'Strict-Transport-Security',
            value: 'max-age=63072000; includeSubDomains; preload'
          },
          {
            key: 'X-Frame-Options',
            value: 'SAMEORIGIN'
          },
          {
            key: 'X-Content-Type-Options',
            value: 'nosniff'
          },
          {
            key: 'Referrer-Policy',
            value: 'origin-when-cross-origin'
          }
        ]
      },
      {
        source: '/static/:path*',
        headers: [
          {
            key: 'Cache-Control',
            value: 'public, max-age=31536000, immutable'
          }
        ]
      }
    ];
  },

  // リダイレクト
  async redirects() {
    return [
      {
        source: '/old-page',
        destination: '/new-page',
        permanent: true
      }
    ];
  }
};

module.exports = nextConfig;
```

### バンドルサイズ最適化 / Bundle Size Optimization

```typescript
// lib/utils/lazy.ts
import dynamic from 'next/dynamic';
import { ComponentType } from 'react';

// 遅延ロード用ヘルパー
export function lazyLoad<P extends object>(
  importFunc: () => Promise<{ default: ComponentType<P> }>,
  options?: {
    loading?: ComponentType;
    ssr?: boolean;
  }
) {
  return dynamic(importFunc, {
    loading: options?.loading,
    ssr: options?.ssr ?? true
  });
}

// 使用例
export const HeavyChart = lazyLoad(
  () => import('@/components/HeavyChart'),
  {
    loading: () => <div>Loading chart...</div>,
    ssr: false
  }
);

export const AdminPanel = lazyLoad(
  () => import('@/components/AdminPanel'),
  { ssr: false }
);
```

```typescript
// 条件付きインポート
'use client';

import { useState } from 'react';

export function ConditionalFeature() {
  const [showEditor, setShowEditor] = useState(false);
  const [Editor, setEditor] = useState<any>(null);

  const loadEditor = async () => {
    if (!Editor) {
      const module = await import('@/components/RichTextEditor');
      setEditor(() => module.RichTextEditor);
    }
    setShowEditor(true);
  };

  return (
    <div>
      {!showEditor && (
        <button onClick={loadEditor}>
          Open Editor
        </button>
      )}
      {showEditor && Editor && <Editor />}
    </div>
  );
}
```

### Tree Shaking 最適化 / Tree Shaking Optimization

```typescript
// ❌ 悪い例: デフォルトインポート
import _ from 'lodash';

const result = _.uniq([1, 2, 2, 3]);

// ✅ 良い例: 名前付きインポート
import { uniq } from 'lodash-es';

const result = uniq([1, 2, 2, 3]);
```

```json
// package.json
{
  "sideEffects": [
    "*.css",
    "*.scss"
  ]
}
```

### コード分割戦略 / Code Splitting Strategy

```typescript
// app/layout.tsx
import { Suspense } from 'react';
import { Header } from '@/components/Header';
import { Footer } from '@/components/Footer';

// 重いコンポーネントは分割
const Analytics = dynamic(() => import('@/components/Analytics'), {
  ssr: false
});

const ChatWidget = dynamic(() => import('@/components/ChatWidget'), {
  ssr: false
});

export default function RootLayout({
  children
}: {
  children: React.ReactNode;
}) {
  return (
    <html>
      <body>
        <Header />
        
        <main>{children}</main>
        
        <Footer />
        
        {/* 非同期読み込み */}
        <Suspense fallback={null}>
          <Analytics />
          <ChatWidget />
        </Suspense>
      </body>
    </html>
  );
}
```

### 静的生成の最適化 / Static Generation Optimization

```typescript
// app/blog/[slug]/page.tsx
import { notFound } from 'next/navigation';
import { getBlogPost, getAllBlogSlugs } from '@/lib/blog';

// 静的パス生成
export async function generateStaticParams() {
  const slugs = await getAllBlogSlugs();
  
  return slugs.map((slug) => ({
    slug
  }));
}

// メタデータ生成
export async function generateMetadata({ 
  params 
}: { 
  params: { slug: string } 
}) {
  const post = await getBlogPost(params.slug);
  
  if (!post) {
    return {};
  }

  return {
    title: post.title,
    description: post.excerpt,
    openGraph: {
      images: [post.coverImage]
    }
  };
}

// ページコンポーネント
export default async function BlogPost({ 
  params 
}: { 
  params: { slug: string } 
}) {
  const post = await getBlogPost(params.slug);
  
  if (!post) {
    notFound();
  }

  return (
    <article>
      <h1>{post.title}</h1>
      <div dangerouslySetInnerHTML={{ __html: post.content }} />
    </article>
  );
}

// 再検証設定
export const revalidate = 3600; // 1時間ごとに再生成
```

### キャッシュ戦略 / Caching Strategy

```typescript
// lib/cache.ts
import { unstable_cache } from 'next/cache';

// データフェッチのキャッシュ
export const getCachedData = unstable_cache(
  async (id: string) => {
    const response = await fetch(`https://api.example.com/data/${id}`);
    return response.json();
  },
  ['data-cache'],
  {
    revalidate: 3600, // 1時間
    tags: ['data']
  }
);

// キャッシュの無効化
import { revalidateTag } from 'next/cache';

export async function updateData(id: string, data: any) {
  await fetch(`https://api.example.com/data/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data)
  });
  
  // キャッシュを無効化
  revalidateTag('data');
}
```

### CDN 最適化 / CDN Optimization

```typescript
// lib/cdn.ts
const CDN_BASE_URL = process.env.CDN_BASE_URL || '';

export function getCdnUrl(path: string): string {
  if (!CDN_BASE_URL) {
    return path;
  }
  
  // パスの正規化
  const normalizedPath = path.startsWith('/') ? path : `/${path}`;
  
  return `${CDN_BASE_URL}${normalizedPath}`;
}

// 使用例
import Image from 'next/image';
import { getCdnUrl } from '@/lib/cdn';

export function OptimizedImage({ src, alt }: { src: string; alt: string }) {
  return (
    <Image
      src={getCdnUrl(src)}
      alt={alt}
      width={800}
      height={600}
      loading="lazy"
    />
  );
}
```

### ビルドスクリプト / Build Scripts

```json
// package.json
{
  "scripts": {
    "dev": "next dev",
    "build": "next build",
    "start": "next start",
    "lint": "next lint",
    "type-check": "tsc --noEmit",
    "test": "jest",
    "test:watch": "jest --watch",
    "test:coverage": "jest --coverage",
    "e2e": "playwright test",
    "e2e:ui": "playwright test --ui",
    "analyze": "ANALYZE=true next build",
    "clean": "rm -rf .next out",
    "prebuild": "npm run clean && npm run type-check && npm run lint",
    "postbuild": "next-sitemap"
  }
}
```


---

## モニタリングとエラートラッキング / Monitoring and Error Tracking

### Error Tracking with Sentry

```typescript
// lib/sentry.ts
import * as Sentry from '@sentry/nextjs';

Sentry.init({
  dsn: process.env.NEXT_PUBLIC_SENTRY_DSN,
  environment: process.env.NODE_ENV,
  tracesSampleRate: 1.0,
  
  // エラーフィルタリング
  beforeSend(event, hint) {
    // 開発環境ではエラーを送信しない
    if (process.env.NODE_ENV === 'development') {
      return null;
    }
    
    // 特定のエラーを除外
    const error = hint.originalException;
    if (error && error.message?.includes('ResizeObserver')) {
      return null;
    }
    
    return event;
  },
});

// エラーハンドリング
try {
  // 危険な操作
} catch (error) {
  Sentry.captureException(error, {
    tags: {
      section: 'checkout',
    },
    extra: {
      userId: user.id,
    },
  });
}
```

### Performance Monitoring

```typescript
// lib/performance.ts
export const measurePerformance = (metricName: string) => {
  if (typeof window === 'undefined') return;
  
  performance.mark(`${metricName}-start`);
  
  return () => {
    performance.mark(`${metricName}-end`);
    performance.measure(
      metricName,
      `${metricName}-start`,
      `${metricName}-end`
    );
    
    const measure = performance.getEntriesByName(metricName)[0];
    console.log(`${metricName}: ${measure.duration}ms`);
    
    // Sentryに送信
    Sentry.metrics.distribution(metricName, measure.duration, {
      unit: 'millisecond',
    });
  };
};

// 使用例
const DataFetchingComponent: React.FC = () => {
  useEffect(() => {
    const endMeasure = measurePerformance('data-fetching');
    
    fetchData().then(() => {
      endMeasure();
    });
  }, []);
};
```

### Analytics実装

```typescript
// lib/analytics.ts
export const analytics = {
  track: (event: string, properties?: Record<string, any>) => {
    if (typeof window === 'undefined') return;
    
    // Google Analytics
    window.gtag?.('event', event, properties);
    
    // カスタム分析
    fetch('/api/analytics', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ event, properties, timestamp: Date.now() }),
    });
  },
  
  page: (path: string) => {
    window.gtag?.('config', process.env.NEXT_PUBLIC_GA_ID, {
      page_path: path,
    });
  },
};

// 使用例
const ProductPage: React.FC = () => {
  useEffect(() => {
    analytics.track('product_viewed', {
      productId: '123',
      category: 'electronics',
    });
  }, []);
  
  const handlePurchase = () => {
    analytics.track('purchase_completed', {
      productId: '123',
      price: 99.99,
    });
  };
};
```

---

## ベストプラクティス / Best Practices

### コーディング規約

#### 命名規則

```typescript
// ✅ Good
// コンポーネント: PascalCase
const UserProfile: React.FC = () => {};

// フック: camelCase with "use" prefix
const useUserData = () => {};

// 定数: UPPER_SNAKE_CASE
const API_BASE_URL = 'https://api.example.com';

// 関数: camelCase
const fetchUserData = async () => {};

// インターフェース: PascalCase
interface UserProfile {
  id: string;
  name: string;
}

// 型エイリアス: PascalCase
type UserId = string;
```

#### ファイル構造

```typescript
// ✅ Good: 1ファイル1エクスポート（コンポーネント）
// UserProfile.tsx
export const UserProfile: React.FC<UserProfileProps> = (props) => {
  // ...
};

// ✅ Good: 複数の関連する型を1ファイルに
// types/user.ts
export interface User {
  id: string;
  name: string;
}

export interface UserProfile extends User {
  bio: string;
}

export type UserId = string;
```

### パフォーマンスチェックリスト

- [ ] 不要な再レンダリングを防ぐ（React.memo、useMemo、useCallback）
- [ ] コード分割を実装（Dynamic Imports）
- [ ] 画像を最適化（Next.js Image、適切なフォーマット）
- [ ] バンドルサイズを監視（Bundle Analyzer）
- [ ] Lazy Loadingを実装（画像、コンポーネント）
- [ ] Virtual Scrollingを使用（長いリスト）
- [ ] Web Vitalsを測定（LCP、FID、CLS）

### アクセシビリティチェックリスト

- [ ] セマンティックHTMLを使用
- [ ] 適切なARIA属性を追加
- [ ] キーボードナビゲーションをサポート
- [ ] フォーカス管理を実装
- [ ] 十分なカラーコントラスト（WCAG AA: 4.5:1）
- [ ] スクリーンリーダーでテスト
- [ ] フォームにラベルを追加
- [ ] エラーメッセージをアクセシブルに

---

## 参考資料 / References

### 公式ドキュメント

- [React Documentation](https://react.dev/)
- [Next.js Documentation](https://nextjs.org/docs)
- [TypeScript Documentation](https://www.typescriptlang.org/docs/)
- [TanStack Query](https://tanstack.com/query/latest)
- [Tailwind CSS](https://tailwindcss.com/docs)

### ベストプラクティスガイド

- [React Best Practices](https://react.dev/learn/thinking-in-react)
- [Web.dev Performance](https://web.dev/performance/)
- [WCAG 2.1 Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)

### ツール

- [React DevTools](https://react.dev/learn/react-developer-tools)
- [Lighthouse](https://developers.google.com/web/tools/lighthouse)
- [axe DevTools](https://www.deque.com/axe/devtools/)
- [Bundle Analyzer](https://www.npmjs.com/package/@next/bundle-analyzer)

### 社内ドキュメント

- [TypeScript Standards](../05-technology-stack/typescript-standards.md)
- [API Architecture](./api-architecture.md)
- [Testing Strategy](../03-development-process/testing-strategy.md)
- [Code Review Guidelines](../03-development-process/code-review.md)


---

## バージョン履歴 / Version History

| バージョン | 日付 | 変更内容 | 承認者 |
|----------|------|---------|--------|
| 2.0.0 | 2024-01-15 | フロントエンドアーキテクチャ標準の全面改訂 | CTO |
| 1.5.0 | 2023-10-01 | Next.js 14対応、App Router追加 | Tech Lead |
| 1.0.0 | 2023-04-01 | 初版作成 | Engineering Manager |

---

## 承認 / Approval

| 役割 | 氏名 | 承認日 |
|-----|------|--------|
| CTO | [氏名] | 2024-01-15 |
| VP of Engineering | [氏名] | 2024-01-15 |
| Tech Lead (Frontend) | [氏名] | 2024-01-15 |

---

## お問い合わせ / Contact

**ドキュメント管理者**: Frontend Architecture Team  
**Email**: frontend-arch@company.com  
**Slack**: #frontend-architecture

**更新リクエスト**: GitHubでPull Requestを作成してください  
**質問**: Slackチャンネルまたはメールでお問い合わせください
