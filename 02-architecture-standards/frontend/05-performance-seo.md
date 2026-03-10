# Performance Optimization & SEO

**説明**: パフォーマンス最適化、Core Web Vitals、SEO対策

**主要トピック**:
- Core Web Vitals目標値（LCP < 2.5s、FID < 100ms、CLS < 0.1）
- 最適化手法（コード分割、遅延ロード、画像最適化）
- パフォーマンスモニタリング
- メタデータ管理
- 構造化データ（JSON-LD）
- sitemap.xml、robots.txt

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


