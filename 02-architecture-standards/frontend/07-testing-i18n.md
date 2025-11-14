# Testing Strategy & Internationalization

**説明**: テスト戦略、テストピラミッド、国際化

**主要トピック**:
- テストピラミッド
- ユニットテスト（Vitest、React Testing Library）
- 統合テスト、E2Eテスト（Playwright）
- ビジュアルリグレッションテスト
- テストカバレッジ（80%以上）
- 国際化戦略（next-intl）
- 翻訳ファイル構造
- 日時・数値フォーマット、RTL対応

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


