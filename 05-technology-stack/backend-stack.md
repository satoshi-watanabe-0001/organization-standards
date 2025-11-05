# バックエンド技術スタック / Backend Technology Stack

```yaml
version: "2.0.0"
last_updated: "2025-01-15"
status: "active"
owner: "Backend Architecture Team"
category: "technology-stack"
```

## 目次 / Table of Contents

1. [概要 / Overview](#概要--overview)
2. [コアテクノロジー / Core Technologies](#コアテクノロジー--core-technologies)
3. [フレームワークとライブラリ / Frameworks and Libraries](#フレームワークとライブラリ--frameworks-and-libraries)
4. [データベース技術 / Database Technologies](#データベース技術--database-technologies)
5. [API設計 / API Design](#api設計--api-design)
6. [認証と認可 / Authentication and Authorization](#認証と認可--authentication-and-authorization)
7. [バックグラウンド処理 / Background Processing](#バックグラウンド処理--background-processing)
8. [テストツール / Testing Tools](#テストツール--testing-tools)
9. [開発ツール / Development Tools](#開発ツール--development-tools)
10. [標準設定 / Standard Configuration](#標準設定--standard-configuration)
11. [ベストプラクティス / Best Practices](#ベストプラクティス--best-practices)
12. [移行ガイド / Migration Guide](#移行ガイド--migration-guide)
13. [参考資料 / References](#参考資料--references)

---

## 概要 / Overview

このドキュメントは、組織全体で使用するバックエンド技術スタックの標準を定義します。

### 目的 / Purpose

- バックエンドアーキテクチャの統一
- スケーラブルで保守性の高いAPI設計の促進
- セキュリティベストプラクティスの実装
- 開発効率の最大化

### 適用範囲 / Scope

- すべてのバックエンドサービス開発
- RESTful API、GraphQL API、gRPCサービス
- マイクロサービスアーキテクチャ
- バックグラウンドジョブとワーカー

---

## コアテクノロジー / Core Technologies

### プログラミング言語 / Programming Languages

#### Node.js (必須 / Required)

```yaml
technology:
  name: "Node.js"
  version: "20.x LTS"
  status: "required"
  use_cases:
    - "Web API開発"
    - "マイクロサービス"
    - "リアルタイム通信"
  
runtime:
  required_version: ">=20.0.0"
  recommended_version: "20.11.0"
  
configuration:
  package_manager: "pnpm"
  node_version_manager: "nvm"
  environment:
    NODE_ENV: "production"
    NODE_OPTIONS: "--max-old-space-size=4096"
```

**選定理由**:
- 高いパフォーマンスと非同期I/O
- 豊富なエコシステム
- TypeScriptとの優れた統合
- フロントエンドとの技術スタック統一

#### TypeScript (必須 / Required)

```yaml
technology:
  name: "TypeScript"
  version: "5.x"
  status: "required"
  
compiler_options:
  target: "ES2022"
  module: "NodeNext"
  moduleResolution: "NodeNext"
  strict: true
  esModuleInterop: true
  skipLibCheck: true
  forceConsistentCasingInFileNames: true
  resolveJsonModule: true
  declaration: true
  declarationMap: true
  sourceMap: true
  
strict_checks:
  - "strictNullChecks: true"
  - "strictFunctionTypes: true"
  - "noImplicitAny: true"
  - "noUnusedLocals: true"
  - "noUnusedParameters: true"
  - "noImplicitReturns: true"
```

#### Python (承認済み / Approved)

```yaml
technology:
  name: "Python"
  version: "3.11.x, 3.12.x"
  status: "approved"
  use_cases:
    - "データ処理パイプライン"
    - "機械学習サービス"
    - "スクリプトとツール"
  
package_management:
  tool: "poetry"
  alternative: "pip + requirements.txt"
  
type_checking:
  tool: "mypy"
  required: true
```

#### Go (承認済み / Approved)

```yaml
technology:
  name: "Go"
  version: "1.21.x, 1.22.x"
  status: "approved"
  use_cases:
    - "高パフォーマンスサービス"
    - "システムツール"
    - "gRPCサービス"
  
best_practices:
  - "標準ライブラリ優先"
  - "シンプルさの維持"
  - "並行処理の活用"
```

---

## フレームワークとライブラリ / Frameworks and Libraries

### Webフレームワーク / Web Frameworks

#### Express.js (標準 / Standard)

```yaml
framework:
  name: "Express.js"
  version: "4.x"
  status: "standard"
  
recommended_middleware:
  - name: "helmet"
    purpose: "セキュリティヘッダー"
  - name: "cors"
    purpose: "CORS設定"
  - name: "compression"
    purpose: "レスポンス圧縮"
  - name: "morgan"
    purpose: "HTTPリクエストロギング"
  
project_structure: |
    src/
    ├── config/           # 設定ファイル
    ├── controllers/      # コントローラー
    ├── middlewares/      # ミドルウェア
    ├── models/           # データモデル
    ├── routes/           # ルート定義
    ├── services/         # ビジネスロジック
    ├── utils/            # ユーティリティ
    ├── validators/       # バリデーション
    └── app.ts            # アプリケーションエントリー
```

**設定例**:

```typescript
import express from 'express';
import helmet from 'helmet';
import cors from 'cors';
import compression from 'compression';
import morgan from 'morgan';

const app = express();

// セキュリティミドルウェア
app.use(helmet({
  contentSecurityPolicy: {
    directives: {
      defaultSrc: ["'self'"],
      styleSrc: ["'self'", "'unsafe-inline'"],
      scriptSrc: ["'self'"],
      imgSrc: ["'self'", "data:", "https:"],
    },
  },
  hsts: {
    maxAge: 31536000,
    includeSubDomains: true,
    preload: true,
  },
}));

// CORS設定
app.use(cors({
  origin: process.env.ALLOWED_ORIGINS?.split(',') || [],
  credentials: true,
  maxAge: 86400,
}));

// レスポンス圧縮
app.use(compression());

// リクエストロギング
app.use(morgan('combined'));

// ボディパーサー
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

export default app;
```

#### NestJS (推奨 / Recommended)

```yaml
framework:
  name: "NestJS"
  version: "10.x"
  status: "recommended"
  use_cases:
    - "エンタープライズアプリケーション"
    - "マイクロサービス"
    - "複雑なビジネスロジック"
  
architecture:
  pattern: "モジュラーアーキテクチャ"
  di_container: "組み込み依存性注入"
  
key_features:
  - "TypeScriptファーストフレームワーク"
  - "デコレータベースのAPI"
  - "GraphQL、WebSocket、gRPCサポート"
  - "テスト容易性"
```

**プロジェクト構造**:

```
src/
├── auth/                 # 認証モジュール
│   ├── auth.controller.ts
│   ├── auth.service.ts
│   ├── auth.module.ts
│   ├── guards/
│   └── strategies/
├── users/                # ユーザーモジュール
│   ├── users.controller.ts
│   ├── users.service.ts
│   ├── users.module.ts
│   ├── dto/
│   └── entities/
├── common/               # 共通機能
│   ├── decorators/
│   ├── filters/
│   ├── guards/
│   ├── interceptors/
│   └── pipes/
├── config/               # 設定
│   ├── app.config.ts
│   ├── database.config.ts
│   └── validation.schema.ts
├── app.module.ts
└── main.ts
```

#### Fastify (承認済み / Approved)

```yaml
framework:
  name: "Fastify"
  version: "4.x"
  status: "approved"
  use_cases:
    - "高パフォーマンスAPI"
    - "低レイテンシー要求"
  
advantages:
  - "Express.jsより2倍高速"
  - "JSON Schemaバリデーション"
  - "TypeScript完全サポート"
```

---

## データベース技術 / Database Technologies

### リレーショナルデータベース / Relational Databases

#### PostgreSQL (標準 / Standard)

```yaml
database:
  name: "PostgreSQL"
  version: "15.x, 16.x"
  status: "standard"
  
orm:
  primary: "Prisma"
  alternative: "TypeORM"
  
connection_pooling:
  tool: "PgBouncer"
  pool_size: 20
  max_overflow: 10
  
extensions:
  - "uuid-ossp"
  - "pg_trgm"
  - "hstore"
  - "postgis" # 地理空間データ
  
backup:
  frequency: "毎日"
  retention: "30日"
  method: "pg_dump + WAL archiving"
```

**Prisma設定例**:

```prisma
datasource db {
  provider = "postgresql"
  url      = env("DATABASE_URL")
}

generator client {
  provider        = "prisma-client-js"
  previewFeatures = ["fullTextSearch", "fullTextIndex"]
}

model User {
  id        String   @id @default(uuid())
  email     String   @unique
  name      String?
  role      Role     @default(USER)
  posts     Post[]
  createdAt DateTime @default(now())
  updatedAt DateTime @updatedAt

  @@index([email])
  @@map("users")
}

model Post {
  id        String   @id @default(uuid())
  title     String
  content   String?
  published Boolean  @default(false)
  authorId  String
  author    User     @relation(fields: [authorId], references: [id])
  createdAt DateTime @default(now())
  updatedAt DateTime @updatedAt

  @@index([authorId])
  @@index([published])
  @@map("posts")
}

enum Role {
  USER
  ADMIN
  MODERATOR
}
```

**接続管理**:

```typescript
import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient({
  log: ['query', 'error', 'warn'],
  errorFormat: 'pretty',
});

// 接続プール設定
const DATABASE_URL = process.env.DATABASE_URL + 
  '?connection_limit=20&pool_timeout=10';

export default prisma;
```

### NoSQLデータベース / NoSQL Databases

#### MongoDB (承認済み / Approved)

```yaml
database:
  name: "MongoDB"
  version: "6.x, 7.x"
  status: "approved"
  use_cases:
    - "ドキュメント指向データ"
    - "柔軟なスキーマ"
    - "高速書き込み"
  
odm:
  tool: "Mongoose"
  version: "8.x"
  
best_practices:
  - "スキーマバリデーション定義"
  - "インデックス最適化"
  - "適切なシャーディング戦略"
```

**Mongoose設定例**:

```typescript
import mongoose from 'mongoose';

const userSchema = new mongoose.Schema({
  email: {
    type: String,
    required: true,
    unique: true,
    lowercase: true,
    trim: true,
    index: true,
  },
  name: {
    type: String,
    required: true,
    trim: true,
  },
  role: {
    type: String,
    enum: ['USER', 'ADMIN', 'MODERATOR'],
    default: 'USER',
  },
  metadata: {
    type: Map,
    of: mongoose.Schema.Types.Mixed,
  },
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true },
});

// インデックス
userSchema.index({ email: 1, role: 1 });

// バーチャルフィールド
userSchema.virtual('posts', {
  ref: 'Post',
  localField: '_id',
  foreignField: 'authorId',
});

export const User = mongoose.model('User', userSchema);
```

#### Redis (必須 / Required)

```yaml
database:
  name: "Redis"
  version: "7.x"
  status: "required"
  use_cases:
    - "キャッシング"
    - "セッション管理"
    - "Pub/Sub"
    - "レート制限"
  
client:
  library: "ioredis"
  version: "5.x"
  
patterns:
  - "Cache-Aside"
  - "Write-Through"
  - "Read-Through"
  - "Refresh-Ahead"
```

**Redis設定例**:

```typescript
import Redis from 'ioredis';

const redis = new Redis({
  host: process.env.REDIS_HOST || 'localhost',
  port: parseInt(process.env.REDIS_PORT || '6379'),
  password: process.env.REDIS_PASSWORD,
  db: parseInt(process.env.REDIS_DB || '0'),
  retryStrategy: (times) => {
    const delay = Math.min(times * 50, 2000);
    return delay;
  },
  maxRetriesPerRequest: 3,
});

// キャッシュヘルパー
export class CacheService {
  async get<T>(key: string): Promise<T | null> {
    const data = await redis.get(key);
    return data ? JSON.parse(data) : null;
  }

  async set(key: string, value: any, ttl: number = 3600): Promise<void> {
    await redis.setex(key, ttl, JSON.stringify(value));
  }

  async del(key: string): Promise<void> {
    await redis.del(key);
  }

  async invalidatePattern(pattern: string): Promise<void> {
    const keys = await redis.keys(pattern);
    if (keys.length > 0) {
      await redis.del(...keys);
    }
  }
}
```

---

## API設計 / API Design

### RESTful API (標準 / Standard)

```yaml
api_design:
  style: "RESTful"
  status: "standard"
  
conventions:
  versioning: "URL versioning (/api/v1/)"
  naming: "kebab-case"
  http_methods:
    - "GET: リソース取得"
    - "POST: リソース作成"
    - "PUT: リソース完全更新"
    - "PATCH: リソース部分更新"
    - "DELETE: リソース削除"
  
response_format:
  success:
    structure: |
      {
        "data": {},
        "meta": {
          "timestamp": "2025-01-15T10:30:00Z",
          "requestId": "uuid"
        }
      }
  error:
    structure: |
      {
        "error": {
          "code": "ERROR_CODE",
          "message": "Human readable message",
          "details": []
        },
        "meta": {
          "timestamp": "2025-01-15T10:30:00Z",
          "requestId": "uuid"
        }
      }
  
pagination:
  style: "cursor-based"
  parameters:
    - "cursor: 次ページのカーソル"
    - "limit: 取得件数 (default: 20, max: 100)"
  response: |
    {
      "data": [],
      "pagination": {
        "nextCursor": "encoded_cursor",
        "hasMore": true
      }
    }
```

**エンドポイント設計例**:

```typescript
// routes/api/v1/users.ts
import { Router } from 'express';
import { authenticate, authorize } from '@/middlewares/auth';
import { validate } from '@/middlewares/validation';
import { createUserSchema, updateUserSchema } from '@/validators/user';
import * as userController from '@/controllers/user.controller';

const router = Router();

/**
 * @route   GET /api/v1/users
 * @desc    ユーザー一覧取得
 * @access  Private (Admin)
 */
router.get('/',
  authenticate,
  authorize(['ADMIN']),
  userController.getUsers
);

/**
 * @route   GET /api/v1/users/:id
 * @desc    ユーザー詳細取得
 * @access  Private
 */
router.get('/:id',
  authenticate,
  userController.getUserById
);

/**
 * @route   POST /api/v1/users
 * @desc    ユーザー作成
 * @access  Private (Admin)
 */
router.post('/',
  authenticate,
  authorize(['ADMIN']),
  validate(createUserSchema),
  userController.createUser
);

/**
 * @route   PATCH /api/v1/users/:id
 * @desc    ユーザー更新
 * @access  Private
 */
router.patch('/:id',
  authenticate,
  validate(updateUserSchema),
  userController.updateUser
);

/**
 * @route   DELETE /api/v1/users/:id
 * @desc    ユーザー削除
 * @access  Private (Admin)
 */
router.delete('/:id',
  authenticate,
  authorize(['ADMIN']),
  userController.deleteUser
);

export default router;
```

### GraphQL (承認済み / Approved)

```yaml
api_design:
  style: "GraphQL"
  status: "approved"
  use_cases:
    - "複雑なデータ取得"
    - "フロントエンド主導API"
  
server:
  library: "Apollo Server"
  version: "4.x"
  
best_practices:
  - "DataLoaderでN+1問題解決"
  - "深さ制限実装"
  - "コスト分析"
  - "適切なキャッシング"
```

**Apollo Server設定**:

```typescript
import { ApolloServer } from '@apollo/server';
import { expressMiddleware } from '@apollo/server/express4';
import { ApolloServerPluginDrainHttpServer } from '@apollo/server/plugin/drainHttpServer';
import { makeExecutableSchema } from '@graphql-tools/schema';
import depthLimit from 'graphql-depth-limit';
import { createComplexityLimitRule } from 'graphql-validation-complexity';

const schema = makeExecutableSchema({
  typeDefs,
  resolvers,
});

const server = new ApolloServer({
  schema,
  plugins: [
    ApolloServerPluginDrainHttpServer({ httpServer }),
  ],
  validationRules: [
    depthLimit(10), // 最大深さ10
    createComplexityLimitRule(1000), // 複雑度制限
  ],
  formatError: (error) => {
    console.error(error);
    return {
      message: error.message,
      code: error.extensions?.code,
      locations: error.locations,
      path: error.path,
    };
  },
});

await server.start();

app.use('/graphql',
  cors(),
  express.json(),
  expressMiddleware(server, {
    context: async ({ req }) => ({
      user: await authenticateUser(req),
      dataSources: {
        userAPI: new UserAPI(),
        postAPI: new PostAPI(),
      },
    }),
  })
);
```

### gRPC (承認済み / Approved)

```yaml
api_design:
  style: "gRPC"
  status: "approved"
  use_cases:
    - "マイクロサービス間通信"
    - "高性能API"
  
tools:
  - "protobuf"
  - "@grpc/grpc-js"
  - "@grpc/proto-loader"
```

---

## 認証と認可 / Authentication and Authorization

### JWT (標準 / Standard)

```yaml
auth:
  method: "JSON Web Token (JWT)"
  status: "standard"
  
token_types:
  access_token:
    expiry: "15分"
    storage: "メモリ/HTTPOnly Cookie"
  refresh_token:
    expiry: "7日"
    storage: "HTTPOnly Cookie"
    rotation: true
  
signing:
  algorithm: "RS256"
  key_rotation: "90日ごと"
```

**JWT実装例**:

```typescript
import jwt from 'jsonwebtoken';
import { readFileSync } from 'fs';

const PRIVATE_KEY = readFileSync('./keys/private.pem', 'utf8');
const PUBLIC_KEY = readFileSync('./keys/public.pem', 'utf8');

interface TokenPayload {
  userId: string;
  email: string;
  role: string;
}

export class JWTService {
  generateAccessToken(payload: TokenPayload): string {
    return jwt.sign(payload, PRIVATE_KEY, {
      algorithm: 'RS256',
      expiresIn: '15m',
      issuer: 'api.example.com',
      audience: 'example.com',
    });
  }

  generateRefreshToken(payload: TokenPayload): string {
    return jwt.sign(
      { userId: payload.userId },
      PRIVATE_KEY,
      {
        algorithm: 'RS256',
        expiresIn: '7d',
        issuer: 'api.example.com',
        audience: 'example.com',
      }
    );
  }

  verifyAccessToken(token: string): TokenPayload {
    return jwt.verify(token, PUBLIC_KEY, {
      algorithms: ['RS256'],
      issuer: 'api.example.com',
      audience: 'example.com',
    }) as TokenPayload;
  }

  verifyRefreshToken(token: string): { userId: string } {
    return jwt.verify(token, PUBLIC_KEY, {
      algorithms: ['RS256'],
      issuer: 'api.example.com',
      audience: 'example.com',
    }) as { userId: string };
  }
}
```

### OAuth 2.0 / OpenID Connect (推奨 / Recommended)

```yaml
auth:
  protocol: "OAuth 2.0 + OIDC"
  status: "recommended"
  use_cases:
    - "ソーシャルログイン"
    - "サードパーティ統合"
  
providers:
  - "Google"
  - "GitHub"
  - "Microsoft Azure AD"
  
library:
  name: "Passport.js"
  strategies:
    - "passport-google-oauth20"
    - "passport-github2"
    - "passport-azure-ad"
```

---

## バックグラウンド処理 / Background Processing

### ジョブキュー / Job Queue

#### BullMQ (標準 / Standard)

```yaml
queue:
  name: "BullMQ"
  version: "5.x"
  status: "standard"
  backend: "Redis"
  
features:
  - "優先度付きジョブ"
  - "遅延ジョブ"
  - "リトライ戦略"
  - "ジョブスケジューリング"
  
monitoring:
  ui: "Bull Board"
  metrics: "Prometheus"
```

**BullMQ設定例**:

```typescript
import { Queue, Worker, QueueEvents } from 'bullmq';
import IORedis from 'ioredis';

const connection = new IORedis({
  host: process.env.REDIS_HOST,
  port: parseInt(process.env.REDIS_PORT || '6379'),
  maxRetriesPerRequest: null,
});

// キュー定義
export const emailQueue = new Queue('email', {
  connection,
  defaultJobOptions: {
    attempts: 3,
    backoff: {
      type: 'exponential',
      delay: 2000,
    },
    removeOnComplete: 100,
    removeOnFail: 1000,
  },
});

// ワーカー定義
const emailWorker = new Worker(
  'email',
  async (job) => {
    const { to, subject, body } = job.data;
    
    // メール送信ロジック
    await sendEmail(to, subject, body);
    
    return { sentAt: new Date() };
  },
  {
    connection,
    concurrency: 5,
  }
);

// イベントリスナー
const queueEvents = new QueueEvents('email', { connection });

queueEvents.on('completed', ({ jobId, returnvalue }) => {
  console.log(`Job ${jobId} completed:`, returnvalue);
});

queueEvents.on('failed', ({ jobId, failedReason }) => {
  console.error(`Job ${jobId} failed:`, failedReason);
});

// ジョブ追加
export async function sendWelcomeEmail(email: string, name: string) {
  await emailQueue.add('welcome', {
    to: email,
    subject: 'Welcome!',
    body: `Hello ${name}!`,
  }, {
    priority: 1, // 高優先度
  });
}

export async function sendReportEmail(email: string, reportUrl: string) {
  await emailQueue.add('report', {
    to: email,
    subject: 'Your Report',
    body: `Report: ${reportUrl}`,
  }, {
    delay: 60000, // 1分後に送信
  });
}
```

---

## テストツール / Testing Tools

### ユニットテスト / Unit Testing

```yaml
testing:
  framework: "Vitest"
  version: "1.x"
  status: "required"
  
coverage:
  minimum: "80%"
  tool: "c8"
  
mocking:
  library: "vitest mocks"
```

**Vitest設定**:

```typescript
// vitest.config.ts
import { defineConfig } from 'vitest/config';
import { resolve } from 'path';

export default defineConfig({
  test: {
    globals: true,
    environment: 'node',
    coverage: {
      provider: 'c8',
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        'dist/',
        '**/*.spec.ts',
        '**/*.test.ts',
      ],
      lines: 80,
      functions: 80,
      branches: 80,
      statements: 80,
    },
    setupFiles: ['./test/setup.ts'],
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, './src'),
    },
  },
});
```

**テスト例**:

```typescript
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { UserService } from '@/services/user.service';
import { prisma } from '@/lib/prisma';

vi.mock('@/lib/prisma', () => ({
  prisma: {
    user: {
      findUnique: vi.fn(),
      create: vi.fn(),
      update: vi.fn(),
      delete: vi.fn(),
    },
  },
}));

describe('UserService', () => {
  let userService: UserService;

  beforeEach(() => {
    userService = new UserService();
    vi.clearAllMocks();
  });

  describe('getUserById', () => {
    it('既存ユーザーを返す', async () => {
      const mockUser = {
        id: '1',
        email: 'test@example.com',
        name: 'Test User',
      };

      vi.mocked(prisma.user.findUnique).mockResolvedValue(mockUser);

      const result = await userService.getUserById('1');

      expect(result).toEqual(mockUser);
      expect(prisma.user.findUnique).toHaveBeenCalledWith({
        where: { id: '1' },
      });
    });

    it('ユーザーが存在しない場合はnullを返す', async () => {
      vi.mocked(prisma.user.findUnique).mockResolvedValue(null);

      const result = await userService.getUserById('999');

      expect(result).toBeNull();
    });
  });
});
```

### 統合テスト / Integration Testing

```yaml
testing:
  framework: "Supertest"
  status: "required"
  
database:
  strategy: "テストコンテナ"
  tool: "testcontainers"
```

### E2Eテスト / End-to-End Testing

```yaml
testing:
  framework: "Playwright"
  version: "1.x"
  status: "recommended"
```

---

## 開発ツール / Development Tools

### リンティングとフォーマット / Linting and Formatting

```yaml
tools:
  linter:
    name: "ESLint"
    version: "8.x"
    config: "@typescript-eslint"
  
  formatter:
    name: "Prettier"
    version: "3.x"
    
  integration:
    - "eslint-config-prettier"
    - "eslint-plugin-prettier"
```

**ESLint設定**:

```json
{
  "extends": [
    "eslint:recommended",
    "plugin:@typescript-eslint/recommended",
    "plugin:@typescript-eslint/recommended-requiring-type-checking",
    "prettier"
  ],
  "parser": "@typescript-eslint/parser",
  "parserOptions": {
    "project": "./tsconfig.json"
  },
  "plugins": ["@typescript-eslint"],
  "rules": {
    "@typescript-eslint/no-unused-vars": ["error", { "argsIgnorePattern": "^_" }],
    "@typescript-eslint/explicit-function-return-type": "warn",
    "@typescript-eslint/no-explicit-any": "error",
    "@typescript-eslint/no-floating-promises": "error"
  }
}
```

### ドキュメント生成 / Documentation Generation

```yaml
tools:
  api_docs:
    tool: "Swagger / OpenAPI"
    version: "3.x"
    library: "swagger-jsdoc + swagger-ui-express"
  
  code_docs:
    tool: "TypeDoc"
    version: "0.25.x"
```

---

## 標準設定 / Standard Configuration

### 環境変数管理 / Environment Variables

```yaml
env_management:
  tool: "dotenv"
  validation: "zod"
  
structure:
  development: ".env.development"
  staging: ".env.staging"
  production: ".env.production"
  test: ".env.test"
```

**環境変数スキーマ**:

```typescript
import { z } from 'zod';

const envSchema = z.object({
  NODE_ENV: z.enum(['development', 'staging', 'production', 'test']),
  PORT: z.coerce.number().default(3000),
  
  // Database
  DATABASE_URL: z.string().url(),
  DATABASE_POOL_SIZE: z.coerce.number().default(20),
  
  // Redis
  REDIS_HOST: z.string(),
  REDIS_PORT: z.coerce.number().default(6379),
  REDIS_PASSWORD: z.string().optional(),
  
  // JWT
  JWT_PRIVATE_KEY: z.string(),
  JWT_PUBLIC_KEY: z.string(),
  JWT_ACCESS_EXPIRY: z.string().default('15m'),
  JWT_REFRESH_EXPIRY: z.string().default('7d'),
  
  // API
  API_RATE_LIMIT: z.coerce.number().default(100),
  CORS_ORIGINS: z.string().transform((val) => val.split(',')),
  
  // Monitoring
  SENTRY_DSN: z.string().url().optional(),
  LOG_LEVEL: z.enum(['error', 'warn', 'info', 'debug']).default('info'),
});

export const env = envSchema.parse(process.env);

export type Env = z.infer<typeof envSchema>;
```

### ロギング / Logging

```yaml
logging:
  library: "Winston"
  version: "3.x"
  
  levels:
    - "error"
    - "warn"
    - "info"
    - "debug"
  
  transports:
    development:
      - "Console (colorized)"
    production:
      - "File (JSON)"
      - "CloudWatch / Datadog"
  
  structured_logging: true
  correlation_id: true
```

**Winston設定**:

```typescript
import winston from 'winston';
import { env } from './env';

const logFormat = winston.format.combine(
  winston.format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss' }),
  winston.format.errors({ stack: true }),
  winston.format.splat(),
  winston.format.json()
);

const transports: winston.transport[] = [
  new winston.transports.File({
    filename: 'logs/error.log',
    level: 'error',
    maxsize: 5242880, // 5MB
    maxFiles: 5,
  }),
  new winston.transports.File({
    filename: 'logs/combined.log',
    maxsize: 5242880,
    maxFiles: 5,
  }),
];

if (env.NODE_ENV !== 'production') {
  transports.push(
    new winston.transports.Console({
      format: winston.format.combine(
        winston.format.colorize(),
        winston.format.simple()
      ),
    })
  );
}

export const logger = winston.createLogger({
  level: env.LOG_LEVEL,
  format: logFormat,
  transports,
});

// 使用例
logger.info('User logged in', { userId: '123', ip: '192.168.1.1' });
logger.error('Database connection failed', { error: err.message });
```

### エラーハンドリング / Error Handling

```yaml
error_handling:
  strategy: "集中型エラーハンドリング"
  
  custom_errors:
    - "ValidationError"
    - "AuthenticationError"
    - "AuthorizationError"
    - "NotFoundError"
    - "ConflictError"
```

**エラークラス**:

```typescript
export class AppError extends Error {
  constructor(
    public statusCode: number,
    public message: string,
    public code?: string,
    public details?: any
  ) {
    super(message);
    this.name = this.constructor.name;
    Error.captureStackTrace(this, this.constructor);
  }
}

export class ValidationError extends AppError {
  constructor(message: string, details?: any) {
    super(400, message, 'VALIDATION_ERROR', details);
  }
}

export class AuthenticationError extends AppError {
  constructor(message: string = 'Authentication failed') {
    super(401, message, 'AUTHENTICATION_ERROR');
  }
}

export class AuthorizationError extends AppError {
  constructor(message: string = 'Insufficient permissions') {
    super(403, message, 'AUTHORIZATION_ERROR');
  }
}

export class NotFoundError extends AppError {
  constructor(resource: string) {
    super(404, `${resource} not found`, 'NOT_FOUND');
  }
}

export class ConflictError extends AppError {
  constructor(message: string) {
    super(409, message, 'CONFLICT');
  }
}

// グローバルエラーハンドラー
export function errorHandler(
  err: Error,
  req: Request,
  res: Response,
  next: NextFunction
) {
  if (err instanceof AppError) {
    logger.warn('Application error', {
      code: err.code,
      message: err.message,
      path: req.path,
    });

    return res.status(err.statusCode).json({
      error: {
        code: err.code,
        message: err.message,
        details: err.details,
      },
      meta: {
        timestamp: new Date().toISOString(),
        requestId: req.id,
      },
    });
  }

  // 予期しないエラー
  logger.error('Unexpected error', {
    error: err.message,
    stack: err.stack,
    path: req.path,
  });

  res.status(500).json({
    error: {
      code: 'INTERNAL_ERROR',
      message: 'An unexpected error occurred',
    },
    meta: {
      timestamp: new Date().toISOString(),
      requestId: req.id,
    },
  });
}
```

---

## ベストプラクティス / Best Practices

### セキュリティ / Security

1. **入力バリデーション**
   - すべてのユーザー入力を検証
   - Zod、Joi、Yupを使用
   - ホワイトリスト方式の採用

2. **SQL/NoSQLインジェクション対策**
   - ORMのパラメータ化クエリ使用
   - 生クエリの最小化
   - 入力サニタイゼーション

3. **認証・認可**
   - JWTトークンの適切な管理
   - リフレッシュトークンローテーション
   - RBAC (Role-Based Access Control)

4. **レート制限**
   - APIエンドポイントごとの制限
   - express-rate-limitの使用

5. **セキュリティヘッダー**
   - Helmetミドルウェア必須
   - CORS適切な設定
   - CSP (Content Security Policy)

### パフォーマンス / Performance

1. **データベース最適化**
   - 適切なインデックス作成
   - N+1問題の回避
   - 接続プーリング

2. **キャッシング戦略**
   - Redis活用
   - CDNキャッシング
   - HTTP キャッシュヘッダー

3. **非同期処理**
   - バックグラウンドジョブ化
   - BullMQ活用

4. **ページネーション**
   - カーソルベースページネーション
   - 大量データの効率的取得

### コード品質 / Code Quality

1. **SOLID原則**
   - 単一責任原則
   - 依存性逆転原則

2. **DRY (Don't Repeat Yourself)**
   - 共通ロジックの抽出
   - ユーティリティ関数化

3. **テスト駆動開発 (TDD)**
   - ユニットテスト80%以上
   - 統合テスト重要パス

4. **コードレビュー**
   - プルリクエスト必須
   - 2名以上の承認

---

## 移行ガイド / Migration Guide

### Express.js → NestJS

```yaml
migration:
  duration: "2-4週間"
  strategy: "段階的移行"
  
  steps:
    1: "新規機能はNestJSで実装"
    2: "既存APIの段階的移植"
    3: "共通ミドルウェアの統合"
    4: "完全移行後、Express.js削除"
```

### JavaScript → TypeScript

```yaml
migration:
  approach: "段階的型付け"
  
  phases:
    1: "tsconfig.json作成、allowJs: true"
    2: ".js → .ts 段階的変換"
    3: "any型の排除"
    4: "strict: true有効化"
```

---

## 参考資料 / References

### 公式ドキュメント / Official Documentation

- [Node.js Documentation](https://nodejs.org/docs)
- [TypeScript Handbook](https://www.typescriptlang.org/docs)
- [Express.js Guide](https://expressjs.com/en/guide)
- [NestJS Documentation](https://docs.nestjs.com)
- [Prisma Documentation](https://www.prisma.io/docs)
- [PostgreSQL Manual](https://www.postgresql.org/docs)

### ベストプラクティスガイド / Best Practice Guides

- [Node.js Best Practices](https://github.com/goldbergyoni/nodebestpractices)
- [TypeScript Deep Dive](https://basarat.gitbook.io/typescript)
- [REST API Design Best Practices](https://restfulapi.net/)

### セキュリティ / Security

- [OWASP API Security Top 10](https://owasp.org/www-project-api-security/)
- [Node.js Security Best Practices](https://nodejs.org/en/docs/guides/security/)

---

## バージョン履歴 / Version History

| バージョン | 日付 | 変更内容 | 承認者 |
|----------|------|---------|--------|
| 2.0.0 | 2025-01-15 | バックエンドスタック標準策定 | Backend Architecture Team |
| 1.5.0 | 2024-12-01 | NestJS推奨、GraphQL承認 | CTO |
| 1.0.0 | 2024-10-01 | 初版リリース | Engineering Manager |

---

## 承認 / Approval

| 役割 | 氏名 | 承認日 | 署名 |
|------|------|--------|------|
| CTO | [Name] | 2025-01-15 | [Signature] |
| Backend Lead | [Name] | 2025-01-15 | [Signature] |
| Security Lead | [Name] | 2025-01-15 | [Signature] |
| DevOps Lead | [Name] | 2025-01-15 | [Signature] |

---

## お問い合わせ / Contact

**Backend Architecture Team**
- Email: backend-team@company.com
- Slack: #backend-architecture
- 担当者: [Tech Lead Name]

---
