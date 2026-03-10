# フロントエンドスタック / Frontend Stack

---

**メタデータ / Metadata**
```yaml
version: 1.0.0
last_updated: 2025-01-15
status: active
owner: Frontend Team
category: technology-stack
```

---

## 📋 目次 / Table of Contents

1. [概要](#概要--overview)
2. [コアテクノロジー](#コアテクノロジー--core-technologies)
3. [フレームワークとライブラリ](#フレームワークとライブラリ--frameworks-and-libraries)
4. [開発ツール](#開発ツール--development-tools)
5. [状態管理](#状態管理--state-management)
6. [スタイリング](#スタイリング--styling)
7. [テスト](#テスト--testing)
8. [ビルドとバンドル](#ビルドとバンドル--build-and-bundling)
9. [パフォーマンス最適化](#パフォーマンス最適化--performance-optimization)
10. [セキュリティ](#セキュリティ--security)
11. [アクセシビリティ](#アクセシビリティ--accessibility)
12. [モニタリングと分析](#モニタリングと分析--monitoring-and-analytics)

---

## 概要 / Overview

### 目的 / Purpose

このドキュメントは、組織で承認されたフロントエンド技術スタックを定義します。一貫性のある開発環境、保守性の向上、チーム間のコラボレーション促進を目指します。

### 技術選定基準 / Technology Selection Criteria

```yaml
selection_criteria:
  stability:
    - 成熟度とコミュニティサポート
    - 長期的なメンテナンス保証
    - 安定したAPIと後方互換性
  
  performance:
    - バンドルサイズ
    - ランタイムパフォーマンス
    - 初期ロード時間
    - レスポンシブネス
  
  developer_experience:
    - 学習曲線
    - ドキュメント品質
    - デバッグツール
    - エコシステムの充実度
  
  ecosystem:
    - プラグイン・拡張の豊富さ
    - 統合の容易さ
    - ツールチェーンのサポート
  
  security:
    - セキュリティ実績
    - 脆弱性対応の速さ
    - セキュリティベストプラクティス
  
  team_expertise:
    - 既存のスキルセット
    - トレーニングコスト
    - 採用市場
```

### スタック構成概要 / Stack Overview

```yaml
stack_layers:
  presentation:
    primary: "React 18.x"
    alternatives: "Next.js 14.x(SSR/SSG要件がある場合)"
  
  state_management:
    primary: "Redux Toolkit + RTK Query"
    alternatives: "Zustand(小規模アプリ), TanStack Query(データフェッチング)"
  
  styling:
    primary: "Tailwind CSS 3.x"
    component_library: "shadcn/ui"
    alternatives: "CSS Modules, Styled Components(既存プロジェクト)"
  
  routing:
    spa: "React Router 6.x"
    ssr: "Next.js App Router"
  
  data_fetching:
    primary: "RTK Query / TanStack Query"
    graphql: "Apollo Client(GraphQL APIの場合)"
  
  forms:
    primary: "React Hook Form"
    validation: "Zod"
  
  testing:
    unit: "Vitest"
    component: "React Testing Library"
    e2e: "Playwright"
  
  build:
    bundler: "Vite 5.x"
    transpiler: "TypeScript 5.x"
  
  language:
    primary: "TypeScript 5.x"
    standard: "100% TypeScript(新規プロジェクト)"
```

---

## コアテクノロジー / Core Technologies

### JavaScript/TypeScript

```yaml
typescript:
  version: "5.x(最新安定版)"
  status: "必須(新規プロジェクト)"
  
  configuration:
    strict_mode: true
    target: "ES2022"
    module: "ESNext"
    jsx: "react-jsx"
  
  tsconfig_base:
    compilerOptions:
      strict: true
      noImplicitAny: true
      strictNullChecks: true
      noUnusedLocals: true
      noUnusedParameters: true
      esModuleInterop: true
      skipLibCheck: true
      forceConsistentCasingInFileNames: true
  
  best_practices:
    - 型定義ファイル(.d.ts)の活用
    - Genericsの適切な使用
    - Union Types / Intersection Typesの活用
    - Type Guardsの実装
    - 'any' 型の使用を最小化
  
  migration:
    from_javascript:
      - 段階的な移行を推奨
      - allowJs: true で開始
      - ファイルごとに .ts に変換
      - strict モードは段階的に有効化
```

### HTML5

```yaml
html5:
  standards:
    - セマンティックHTML使用必須
    - WAI-ARIA属性の適切な使用
    - メタタグの最適化
    - 構造化データ(Schema.org)
  
  semantic_elements:
    required:
      - <header>, <nav>, <main>, <article>
      - <section>, <aside>, <footer>
      - <figure>, <figcaption>
    
    avoid:
      - 過度な <div> のネスト
      - 意味のないコンテナ要素
  
  best_practices:
    - 適切な見出し階層(h1-h6)
    - ランドマークロールの使用
    - alt属性の必須化(画像)
    - labelとinputの適切な関連付け
```

### CSS3

```yaml
css3:
  approach: "Utility-First(Tailwind CSS)"
  
  modern_features:
    - CSS Grid Layout
    - Flexbox
    - CSS Custom Properties(CSS変数)
    - CSS Containment
    - Container Queries
  
  browser_support:
    - 最新2バージョンのモダンブラウザ
    - Safari, Chrome, Firefox, Edge
    - モバイル: iOS Safari, Chrome Android
  
  prefixing:
    - Autoprefixer で自動化
    - ベンダープレフィックス不要(ビルド時に追加)
  
  performance:
    - Critical CSS の抽出
    - 未使用CSSの削除(PurgeCSS)
    - CSS-in-JSの最適化
```

---

## フレームワークとライブラリ / Frameworks and Libraries

### React

```yaml
react:
  version: "18.x"
  status: "標準(すべての新規プロジェクト)"
  
  key_features:
    - Concurrent Rendering
    - Automatic Batching
    - Transitions
    - Suspense for Data Fetching
    - Server Components(Next.js)
  
  component_patterns:
    functional_components:
      status: "必須"
      hooks:
        - useState, useEffect, useContext
        - useReducer, useCallback, useMemo
        - useRef, useImperativeHandle
        - Custom Hooks推奨
    
    class_components:
      status: "非推奨(新規開発では使用しない)"
      migration: "段階的にFunctional Componentsへ移行"
  
  best_practices:
    composition:
      - Composition over Inheritance
      - Higher-Order Components(HOC)は慎重に使用
      - Render Props パターン(必要な場合のみ)
    
    hooks_rules:
      - Hooks は最上位でのみ呼び出す
      - React関数内でのみ呼び出す
      - 依存配列の適切な管理
    
    performance:
      - React.memo の適切な使用
      - useMemo, useCallback の活用
      - コンポーネント分割
      - 仮想スクロール(大規模リスト)
    
    code_splitting:
      - React.lazy + Suspense
      - ルートベースのコード分割
      - コンポーネントベースの遅延ロード
  
  file_structure:
    recommended: |
      src/
      ├── components/
      │   ├── common/          # 共通コンポーネント
      │   ├── features/        # 機能別コンポーネント
      │   └── layouts/         # レイアウトコンポーネント
      ├── hooks/               # カスタムフック
      ├── services/            # API・ビジネスロジック
      ├── stores/              # 状態管理
      ├── types/               # TypeScript型定義
      ├── utils/               # ユーティリティ関数
      └── App.tsx
```

### Next.js

```yaml
nextjs:
  version: "14.x"
  status: "推奨(SSR/SSG要件がある場合)"
  
  use_cases:
    - SEOが重要なマーケティングサイト
    - サーバーサイドレンダリングが必要
    - 静的サイト生成(SSG)
    - API Routesの活用
    - エッジコンピューティング
  
  app_router:
    status: "標準(新規プロジェクト)"
    features:
      - React Server Components
      - Streaming
      - Server Actions
      - Parallel Routes
      - Intercepting Routes
  
  rendering_strategies:
    ssg:
      description: "Static Site Generation"
      use_case: "コンテンツが静的、ビルド時に生成"
      method: "generateStaticParams"
    
    ssr:
      description: "Server-Side Rendering"
      use_case: "動的コンテンツ、リクエスト毎に生成"
      method: "fetch with no-cache"
    
    isr:
      description: "Incremental Static Regeneration"
      use_case: "静的だが定期更新が必要"
      method: "fetch with revalidate"
    
    csr:
      description: "Client-Side Rendering"
      use_case: "高度にインタラクティブなUI"
      method: "'use client' directive"
  
  optimization:
    - 自動画像最適化(next/image)
    - フォント最適化(next/font)
    - スクリプト最適化(next/script)
    - バンドル分析(@next/bundle-analyzer)
  
  deployment:
    preferred: "Vercel"
    alternatives:
      - "Docker container"
      - "Node.js server"
      - "AWS Amplify"
```

### UIコンポーネントライブラリ / UI Component Libraries

```yaml
component_libraries:
  primary: "shadcn/ui"
  
  shadcn_ui:
    version: "最新"
    status: "標準"
    
    philosophy:
      - コンポーネントのコピー&ペースト
      - 完全なカスタマイズ可能性
      - 依存関係の最小化
      - Radix UI + Tailwind CSS ベース
    
    components:
      - Button, Input, Select, Checkbox
      - Dialog, Dropdown Menu, Popover
      - Toast, Alert, Badge
      - Card, Tabs, Accordion
      - Table, Form, Sheet
    
    advantages:
      - 完全な所有権(コード)
      - TypeScript完全サポート
      - アクセシビリティ組み込み
      - カスタマイズが容易
    
    usage:
      installation: "npx shadcn-ui@latest add [component]"
      customization: "components/ui/ で直接編集"
  
  alternatives:
    mui:
      name: "Material-UI (MUI)"
      version: "5.x"
      status: "許可(既存プロジェクト)"
      use_case: "Material Design が必要な場合"
    
    chakra_ui:
      name: "Chakra UI"
      version: "2.x"
      status: "許可(小規模プロジェクト)"
      use_case: "迅速なプロトタイピング"
    
    ant_design:
      name: "Ant Design"
      version: "5.x"
      status: "評価中"
      use_case: "エンタープライズアプリケーション"
```

---

## 開発ツール / Development Tools

### パッケージマネージャー / Package Managers

```yaml
package_managers:
  primary: "pnpm"
  
  pnpm:
    version: "8.x"
    advantages:
      - ディスク使用量削減
      - インストール速度
      - Monorepo サポート
      - Strict node_modules 構造
    
    configuration:
      .npmrc: |
        auto-install-peers=true
        strict-peer-dependencies=false
        shamefully-hoist=true
    
    commands:
      install: "pnpm install"
      add: "pnpm add <package>"
      remove: "pnpm remove <package>"
      update: "pnpm update"
  
  alternatives:
    npm:
      version: "9.x"
      status: "許可"
      use_case: "既存プロジェクト"
    
    yarn:
      version: "3.x (Berry)"
      status: "許可(既存プロジェクト)"
      use_case: "Yarn Workspaces 使用中"
```

### コード品質ツール / Code Quality Tools

```yaml
linting:
  eslint:
    version: "8.x"
    status: "必須"
    
    configuration:
      extends:
        - "eslint:recommended"
        - "plugin:react/recommended"
        - "plugin:react-hooks/recommended"
        - "plugin:@typescript-eslint/recommended"
        - "plugin:jsx-a11y/recommended"
        - "prettier"
      
      plugins:
        - "react"
        - "react-hooks"
        - "@typescript-eslint"
        - "jsx-a11y"
        - "import"
      
      rules:
        react/react-in-jsx-scope: "off"  # React 17+
        react/prop-types: "off"  # TypeScript使用時
        @typescript-eslint/no-unused-vars: "error"
        @typescript-eslint/no-explicit-any: "warn"
    
    scripts:
      lint: "eslint . --ext .ts,.tsx"
      lint_fix: "eslint . --ext .ts,.tsx --fix"

formatting:
  prettier:
    version: "3.x"
    status: "必須"
    
    configuration:
      .prettierrc: |
        {
          "semi": true,
          "trailingComma": "es5",
          "singleQuote": true,
          "printWidth": 100,
          "tabWidth": 2,
          "useTabs": false,
          "arrowParens": "avoid",
          "endOfLine": "lf"
        }
    
    integration:
      - VSCode: Format on Save
      - Pre-commit hook: lint-staged
      - CI/CD: 自動チェック
    
    scripts:
      format: "prettier --write \"src/**/*.{ts,tsx,css}\""
      format_check: "prettier --check \"src/**/*.{ts,tsx,css}\""

type_checking:
  typescript:
    scripts:
      type_check: "tsc --noEmit"
      type_check_watch: "tsc --noEmit --watch"

git_hooks:
  husky:
    version: "8.x"
    hooks:
      pre_commit:
        - "lint-staged"
      
      commit_msg:
        - "commitlint"
  
  lint_staged:
    configuration:
      "*.{ts,tsx}":
        - "eslint --fix"
        - "prettier --write"
      
      "*.{css,scss}":
        - "prettier --write"
```

### IDE設定 / IDE Configuration

```yaml
vscode:
  extensions:
    required:
      - "dbaeumer.vscode-eslint"
      - "esbenp.prettier-vscode"
      - "bradlc.vscode-tailwindcss"
      - "ms-vscode.vscode-typescript-next"
    
    recommended:
      - "dsznajder.es7-react-js-snippets"
      - "christian-kohler.path-intellisense"
      - "formulahendry.auto-rename-tag"
      - "wix.vscode-import-cost"
  
  settings:
    .vscode/settings.json: |
      {
        "editor.formatOnSave": true,
        "editor.defaultFormatter": "esbenp.prettier-vscode",
        "editor.codeActionsOnSave": {
          "source.fixAll.eslint": true
        },
        "typescript.tsdk": "node_modules/typescript/lib",
        "typescript.enablePromptUseWorkspaceTsdk": true,
        "tailwindCSS.experimental.classRegex": [
          ["cva\\(([^)]*)\\)", "[\"'`]([^\"'`]*).*?[\"'`]"],
          ["cx\\(([^)]*)\\)", "(?:'|\"|`)([^']*)(?:'|\"|`)"]
        ]
      }
```

---

## 状態管理 / State Management

### Redux Toolkit

```yaml
redux_toolkit:
  version: "2.x"
  status: "標準(複雑な状態管理)"
  
  use_cases:
    - グローバル状態が複雑
    - 多数のコンポーネント間で状態共有
    - タイムトラベルデバッグが必要
    - ミドルウェアの活用
  
  structure:
    store: |
      src/store/
      ├── index.ts           # Store設定
      ├── hooks.ts           # Typed hooks
      ├── slices/
      │   ├── authSlice.ts
      │   ├── userSlice.ts
      │   └── uiSlice.ts
      └── api/
          └── apiSlice.ts    # RTK Query
  
  best_practices:
    - createSlice の使用
    - Immer による不変性自動管理
    - Redux DevTools 統合
    - TypeScript 型付きhooks
    - Entity Adapter の活用
  
  rtk_query:
    status: "推奨(データフェッチング)"
    features:
      - 自動キャッシング
      - 自動再フェッチ
      - Optimistic Updates
      - 型安全なAPI定義
    
    example: |
      import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
      
      export const apiSlice = createApi({
        reducerPath: 'api',
        baseQuery: fetchBaseQuery({ baseUrl: '/api' }),
        tagTypes: ['User', 'Post'],
        endpoints: (builder) => ({
          getUsers: builder.query<User[], void>({
            query: () => '/users',
            providesTags: ['User'],
          }),
          getUserById: builder.query<User, string>({
            query: (id) => `/users/${id}`,
            providesTags: (result, error, id) => [{ type: 'User', id }],
          }),
          updateUser: builder.mutation<User, Partial<User>>({
            query: ({ id, ...patch }) => ({
              url: `/users/${id}`,
              method: 'PATCH',
              body: patch,
            }),
            invalidatesTags: (result, error, { id }) => [{ type: 'User', id }],
          }),
        }),
      });
```

### Zustand

```yaml
zustand:
  version: "4.x"
  status: "推奨(小～中規模状態管理)"
  
  use_cases:
    - シンプルなグローバル状態
    - Redux のボイラープレートを避けたい
    - パフォーマンスが重要
    - 学習曲線を低くしたい
  
  advantages:
    - ボイラープレートが少ない
    - TypeScript サポート優秀
    - ミドルウェアサポート
    - React Context不要
  
  example: |
    import { create } from 'zustand';
    import { devtools, persist } from 'zustand/middleware';
    
    interface UserState {
      user: User | null;
      setUser: (user: User) => void;
      logout: () => void;
    }
    
    export const useUserStore = create<UserState>()(
      devtools(
        persist(
          (set) => ({
            user: null,
            setUser: (user) => set({ user }),
            logout: () => set({ user: null }),
          }),
          { name: 'user-storage' }
        )
      )
    );
  
  best_practices:
    - Store分割(関心の分離)
    - Selector の使用(再レンダリング最適化)
    - ミドルウェアの活用(devtools, persist)
    - Immer ミドルウェア(不変性)
```

### TanStack Query (React Query)

```yaml
tanstack_query:
  version: "5.x"
  status: "推奨(サーバー状態管理)"
  
  use_cases:
    - データフェッチングが主な用途
    - キャッシング戦略が必要
    - 楽観的更新
    - 自動リフェッチ
  
  features:
    - 自動バックグラウンドリフェッチ
    - ウィンドウフォーカス時のリフェッチ
    - ページネーション・Infinite Queries
    - Optimistic Updates
    - キャッシュの細かい制御
  
  configuration:
    queryClient: |
      import { QueryClient } from '@tanstack/react-query';
      
      export const queryClient = new QueryClient({
        defaultOptions: {
          queries: {
            staleTime: 5 * 60 * 1000,  // 5分
            cacheTime: 10 * 60 * 1000,  // 10分
            refetchOnWindowFocus: false,
            retry: 1,
          },
        },
      });
  
  example: |
    import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
    
    // Query
    const { data, isLoading, error } = useQuery({
      queryKey: ['users', userId],
      queryFn: () => fetchUser(userId),
    });
    
    // Mutation
    const queryClient = useQueryClient();
    const mutation = useMutation({
      mutationFn: updateUser,
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ['users'] });
      },
    });
```

### React Context

```yaml
react_context:
  status: "推奨(限定的な使用)"
  
  use_cases:
    - テーマ設定
    - 言語設定(i18n)
    - 認証状態(シンプルな場合)
    - 深いコンポーネントツリーでのprops drilling回避
  
  avoid_for:
    - 頻繁に更新される状態
    - 複雑な状態ロジック
    - 大規模アプリケーションのグローバル状態
  
  best_practices:
    - Context を小さく保つ
    - 関心ごとに分離
    - useMemo で値をメモ化
    - useCallback でハンドラをメモ化
  
  example: |
    import { createContext, useContext, useState, ReactNode } from 'react';
    
    interface ThemeContextType {
      theme: 'light' | 'dark';
      toggleTheme: () => void;
    }
    
    const ThemeContext = createContext<ThemeContextType | undefined>(undefined);
    
    export const ThemeProvider = ({ children }: { children: ReactNode }) => {
      const [theme, setTheme] = useState<'light' | 'dark'>('light');
      
      const toggleTheme = () => {
        setTheme(prev => prev === 'light' ? 'dark' : 'light');
      };
      
      return (
        <ThemeContext.Provider value={{ theme, toggleTheme }}>
          {children}
        </ThemeContext.Provider>
      );
    };
    
    export const useTheme = () => {
      const context = useContext(ThemeContext);
      if (!context) {
        throw new Error('useTheme must be used within ThemeProvider');
      }
      return context;
    };
```

---

## スタイリング / Styling

### Tailwind CSS

```yaml
tailwind_css:
  version: "3.x"
  status: "標準"
  
  philosophy:
    - Utility-First CSS
    - コンポーネント内でスタイル完結
    - デザインシステムの一貫性
    - JIT(Just-In-Time)コンパイル
  
  configuration:
    tailwind.config.js: |
      /** @type {import('tailwindcss').Config} */
      module.exports = {
        content: ['./src/**/*.{ts,tsx}'],
        theme: {
          extend: {
            colors: {
              primary: {
                50: '#f0f9ff',
                // ... 他の色
                950: '#082f49',
              },
            },
            fontFamily: {
              sans: ['Inter', 'sans-serif'],
            },
            spacing: {
              '128': '32rem',
            },
          },
        },
        plugins: [
          require('@tailwindcss/forms'),
          require('@tailwindcss/typography'),
          require('@tailwindcss/aspect-ratio'),
        ],
      };
  
  best_practices:
    organization:
      - クラス名の順序を統一(Prettier plugin)
      - 長いクラス名は抽出(@apply または コンポーネント化)
      - カスタムクラスは最小限
    
    responsive:
      - Mobile-First アプローチ
      - Breakpoints: sm, md, lg, xl, 2xl
      - Container Queries の活用
    
    dark_mode:
      strategy: "class"  # または 'media'
      implementation: |
        <html class="dark">
          <!-- dark: prefix でダークモードスタイル -->
        </html>
  
  plugins:
    required:
      - "@tailwindcss/forms"  # フォームスタイル
      - "@tailwindcss/typography"  # プロース用
    
    recommended:
      - "tailwindcss-animate"  # アニメーション
      - "@tailwindcss/container-queries"
  
  utilities:
    custom_utilities: |
      @layer utilities {
        .scrollbar-hide {
          -ms-overflow-style: none;
          scrollbar-width: none;
        }
        .scrollbar-hide::-webkit-scrollbar {
          display: none;
        }
      }
```

### CSS Modules

```yaml
css_modules:
  status: "許可(既存プロジェクト)"
  
  use_cases:
    - コンポーネント固有のスタイル
    - Tailwind との併用
    - グローバルスタイル汚染の回避
  
  naming_convention:
    file: "ComponentName.module.css"
    class: "camelCase"
  
  example: |
    // Button.module.css
    .button {
      @apply px-4 py-2 rounded;
    }
    
    .buttonPrimary {
      @apply bg-blue-500 text-white;
    }
    
    // Button.tsx
    import styles from './Button.module.css';
    
    export const Button = () => (
      <button className={styles.button}>Click</button>
    );
```

### Styled Components

```yaml
styled_components:
  version: "6.x"
  status: "レガシー(新規使用非推奨)"
  
  migration_path:
    - 新規コンポーネント: Tailwind CSS使用
    - 既存コンポーネント: 段階的にTailwindへ移行
    - 移行優先度: 低(動作中なら急がない)
  
  if_using:
    - Server Components との互換性に注意
    - パフォーマンスへの影響を監視
    - バンドルサイズの増加に注意
```

---

## テスト / Testing

### ユニットテスト / Unit Testing

```yaml
vitest:
  version: "1.x"
  status: "標準"
  
  advantages:
    - Vite ネイティブサポート
    - Jest互換API
    - 高速実行
    - ESM/TypeScript サポート
  
  configuration:
    vitest.config.ts: |
      import { defineConfig } from 'vitest/config';
      import react from '@vitejs/plugin-react';
      
      export default defineConfig({
        plugins: [react()],
        test: {
          globals: true,
          environment: 'jsdom',
          setupFiles: './src/test/setup.ts',
          coverage: {
            provider: 'v8',
            reporter: ['text', 'json', 'html'],
            exclude: [
              'node_modules/',
              'src/test/',
            ],
          },
        },
      });
  
  setup:
    src/test/setup.ts: |
      import { expect, afterEach } from 'vitest';
      import { cleanup } from '@testing-library/react';
      import * as matchers from '@testing-library/jest-dom/matchers';
      
      expect.extend(matchers);
      
      afterEach(() => {
        cleanup();
      });
```

### コンポーネントテスト / Component Testing

```yaml
react_testing_library:
  version: "14.x"
  status: "必須"
  
  philosophy:
    - ユーザー視点でテスト
    - 実装詳細ではなく動作をテスト
    - アクセシビリティを考慮
  
  best_practices:
    queries:
      priority:
        1: "getByRole"  # アクセシビリティ重視
        2: "getByLabelText"
        3: "getByPlaceholderText"
        4: "getByText"
        5: "getByTestId"  # 最後の手段
    
    user_interactions:
      - "@testing-library/user-event" 使用
      - 実際のユーザー操作を模倣
      - 非同期処理の適切な待機
  
  example: |
    import { render, screen } from '@testing-library/react';
    import userEvent from '@testing-library/user-event';
    import { describe, it, expect, vi } from 'vitest';
    import { Button } from './Button';
    
    describe('Button', () => {
      it('renders correctly', () => {
        render(<Button>Click me</Button>);
        expect(screen.getByRole('button', { name: /click me/i }))
          .toBeInTheDocument();
      });
      
      it('calls onClick when clicked', async () => {
        const user = userEvent.setup();
        const handleClick = vi.fn();
        render(<Button onClick={handleClick}>Click</Button>);
        
        await user.click(screen.getByRole('button'));
        expect(handleClick).toHaveBeenCalledTimes(1);
      });
    });
```

### E2Eテスト / End-to-End Testing

```yaml
playwright:
  version: "1.x"
  status: "標準"
  
  advantages:
    - クロスブラウザサポート
    - 自動待機
    - スクリーンショット・ビデオ録画
    - トレースビューア
    - ネットワークインターセプト
  
  configuration:
    playwright.config.ts: |
      import { defineConfig, devices } from '@playwright/test';
      
      export default defineConfig({
        testDir: './e2e',
        fullyParallel: true,
        forbidOnly: !!process.env.CI,
        retries: process.env.CI ? 2 : 0,
        workers: process.env.CI ? 1 : undefined,
        reporter: 'html',
        use: {
          baseURL: 'http://localhost:3000',
          trace: 'on-first-retry',
          screenshot: 'only-on-failure',
        },
        projects: [
          {
            name: 'chromium',
            use: { ...devices['Desktop Chrome'] },
          },
          {
            name: 'firefox',
            use: { ...devices['Desktop Firefox'] },
          },
          {
            name: 'Mobile Safari',
            use: { ...devices['iPhone 13'] },
          },
        ],
      });
  
  best_practices:
    - Page Object Model パターン
    - テストデータの分離
    - 環境変数の活用
    - CI/CDとの統合
  
  example: |
    import { test, expect } from '@playwright/test';
    
    test('user can login', async ({ page }) => {
      await page.goto('/login');
      
      await page.getByLabel('Email').fill('user@example.com');
      await page.getByLabel('Password').fill('password123');
      await page.getByRole('button', { name: /log in/i }).click();
      
      await expect(page).toHaveURL('/dashboard');
      await expect(page.getByRole('heading', { name: /dashboard/i }))
        .toBeVisible();
    });
```

### ビジュアルリグレッションテスト / Visual Regression Testing

```yaml
visual_testing:
  tool: "Playwright + Percy / Chromatic"
  status: "推奨(重要なUI)"
  
  use_cases:
    - デザインシステムコンポーネント
    - クリティカルなUIフロー
    - レスポンシブデザインの検証
  
  percy:
    integration: |
      import percySnapshot from '@percy/playwright';
      
      test('homepage visual test', async ({ page }) => {
        await page.goto('/');
        await percySnapshot(page, 'Homepage');
      });
```

---

## ビルドとバンドル / Build and Bundling

### Vite

```yaml
vite:
  version: "5.x"
  status: "標準"
  
  advantages:
    - 超高速なHMR(Hot Module Replacement)
    - ESM ネイティブ
    - ロールアップベースの最適化
    - プラグインエコシステム
  
  configuration:
    vite.config.ts: |
      import { defineConfig } from 'vite';
      import react from '@vitejs/plugin-react';
      import path from 'path';
      
      export default defineConfig({
        plugins: [react()],
        resolve: {
          alias: {
            '@': path.resolve(__dirname, './src'),
          },
        },
        build: {
          sourcemap: true,
          rollupOptions: {
            output: {
              manualChunks: {
                'react-vendor': ['react', 'react-dom'],
                'router': ['react-router-dom'],
              },
            },
          },
        },
        server: {
          port: 3000,
          open: true,
        },
      });
  
  plugins:
    required:
      - "@vitejs/plugin-react"
    
    recommended:
      - "vite-plugin-svgr"  # SVGをReactコンポーネント化
      - "vite-plugin-pwa"  # PWAサポート
      - "vite-bundle-visualizer"  # バンドル分析
```

### コード分割 / Code Splitting

```yaml
code_splitting:
  strategies:
    route_based:
      description: "ルート単位で分割"
      implementation: |
        import { lazy, Suspense } from 'react';
        
        const Dashboard = lazy(() => import('./pages/Dashboard'));
        const Settings = lazy(() => import('./pages/Settings'));
        
        function App() {
          return (
            <Suspense fallback={<Loading />}>
              <Routes>
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/settings" element={<Settings />} />
              </Routes>
            </Suspense>
          );
        }
    
    component_based:
      description: "重いコンポーネントを遅延ロード"
      implementation: |
        const HeavyChart = lazy(() => import('./components/HeavyChart'));
        
        function Analytics() {
          return (
            <Suspense fallback={<Skeleton />}>
              <HeavyChart data={data} />
            </Suspense>
          );
        }
    
    vendor_chunking:
      description: "ベンダーライブラリを分離"
      configuration: |
        // vite.config.ts
        manualChunks: {
          'react-vendor': ['react', 'react-dom', 'react-router-dom'],
          'ui-vendor': ['@radix-ui/react-dialog', /* ... */],
          'utils': ['lodash-es', 'date-fns'],
        }
```

### 最適化 / Optimization

```yaml
build_optimization:
  tree_shaking:
    - ESM import/export の使用
    - 副作用のないモジュール(sideEffects: false)
    - 未使用コードの自動削除
  
  minification:
    - Terser による圧縮
    - CSS 圧縮
    - HTML 圧縮
  
  compression:
    - Gzip 圧縮
    - Brotli 圧縮
  
  asset_optimization:
    images:
      - WebP / AVIF 形式
      - 適切なサイズ・解像度
      - Lazy loading
      - next/image (Next.js)
    
    fonts:
      - フォントサブセット化
      - font-display: swap
      - Variable fonts の活用
```
