# State Management

**説明**: 状態管理戦略、TanStack Query、Zustand、Redux Toolkit

**主要トピック**:
- 状態管理戦略
- TanStack Query（サーバー状態）
- Zustand（軽量グローバル状態）
- Redux Toolkit（複雑な状態管理）

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


