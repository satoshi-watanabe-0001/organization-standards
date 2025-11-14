# Accessibility & Security

**説明**: アクセシビリティ、WCAG 2.1準拠、セキュリティ対策

**主要トピック**:
- WCAG 2.1 AAレベル準拠
- アクセシブルなコンポーネント
- セキュリティ原則
- JWT認証、セッション管理
- XSS、CSRF、CSP対策
- データ保護・暗号化

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


