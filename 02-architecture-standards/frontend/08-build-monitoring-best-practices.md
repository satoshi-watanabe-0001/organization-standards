# Build, Monitoring & Best Practices

**説明**: ビルド最適化、モニタリング、エラートラッキング、ベストプラクティス

**主要トピック**:
- ビルド設定（Next.js、Vite）
- バンドルサイズ最適化、Tree Shaking
- コード分割戦略
- キャッシュ戦略、CDN最適化
- Error Tracking（Sentry）
- Performance Monitoring
- Analytics実装
- コーディング規約チェックリスト
- パフォーマンス・アクセシビリティチェックリスト
- 参考資料、公式ドキュメント

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

