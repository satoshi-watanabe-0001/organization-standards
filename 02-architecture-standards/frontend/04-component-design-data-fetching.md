# Component Design & Data Fetching

**説明**: コンポーネント設計、分類、データフェッチング戦略

**主要トピック**:
- コンポーネント分類（Page、Feature、UI、Layout）
- コンポーネント設計原則
- データフェッチング戦略
- Next.js Server Components
- Client-Side Data Fetching
- エラーハンドリング

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


