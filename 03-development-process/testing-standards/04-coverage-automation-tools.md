# テスト標準

## テストカバレッジ要件

### カバレッジメトリクス

以下のカバレッジメトリクスを追跡します:

1. **ライン(行)カバレッジ**: 実行されたコード行の割合
2. **ブランチカバレッジ**: 実行された分岐の割合
3. **関数カバレッジ**: 呼び出された関数の割合
4. **ステートメントカバレッジ**: 実行されたステートメントの割合

### カバレッジ目標

プロジェクト全体のカバレッジ目標:

| コンポーネント | 最小カバレッジ | 目標カバレッジ |
|---------------|--------------|--------------|
| ビジネスロジック | 90% | 95% |
| APIエンドポイント | 85% | 90% |
| データベース層 | 80% | 85% |
| ユーティリティ関数 | 85% | 90% |
| UIコンポーネント | 70% | 80% |
| **全体** | **80%** | **85%** |

### カバレッジレポート

Jestを使用してカバレッジレポートを生成:

```json
{
  "jest": {
    "collectCoverage": true,
    "coverageDirectory": "coverage",
    "coverageReporters": ["text", "lcov", "html"],
    "collectCoverageFrom": [
      "src/**/*.{js,jsx,ts,tsx}",
      "!src/**/*.test.{js,jsx,ts,tsx}",
      "!src/**/*.spec.{js,jsx,ts,tsx}",
      "!src/index.{js,jsx,ts,tsx}",
      "!src/setupTests.{js,jsx,ts,tsx}"
    ],
    "coverageThreshold": {
      "global": {
        "branches": 80,
        "functions": 80,
        "lines": 80,
        "statements": 80
      },
      "src/core/**/*.js": {
        "branches": 90,
        "functions": 90,
        "lines": 90,
        "statements": 90
      }
    }
  }
}
```

### カバレッジレポートの例

```bash
# カバレッジ付きでテストを実行
npm test -- --coverage

# カバレッジレポート
---------------------------|---------|----------|---------|---------|
File                       | % Stmts | % Branch | % Funcs | % Lines |
---------------------------|---------|----------|---------|---------|
All files                  |   85.23 |    82.14 |   87.50 |   85.67 |
 src/services              |   92.31 |    88.89 |   95.00 |   92.50 |
  UserService.js           |   95.45 |    90.91 |   100   |   95.45 |
  AuthService.js           |   89.47 |    85.71 |   90.00 |   89.47 |
 src/controllers           |   88.24 |    84.62 |   90.00 |   88.24 |
  UserController.js        |   90.00 |    87.50 |   92.31 |   90.00 |
  AuthController.js        |   86.36 |    81.82 |   87.50 |   86.36 |
 src/utils                 |   78.95 |    73.33 |   80.00 |   79.17 |
  validation.js            |   75.00 |    70.00 |   77.78 |   75.00 |
  formatting.js            |   82.35 |    76.92 |   82.35 |   82.35 |
---------------------------|---------|----------|---------|---------|
```

## テスト自動化

### CI/CDにおけるテスト自動化

すべてのテストをCI/CDパイプラインに統合:

```yaml
# .github/workflows/test.yml
name: Test Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

jobs:
  unit-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Run unit tests
        run: npm run test:unit -- --coverage
      
      - name: Upload coverage reports
        uses: codecov/codecov-action@v3
        with:
          files: ./coverage/lcov.info
          flags: unittests

  integration-tests:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:14
        env:
          POSTGRES_PASSWORD: postgres
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
      
      redis:
        image: redis:7
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 6379:6379

    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Run integration tests
        run: npm run test:integration
        env:
          DATABASE_URL: postgresql://postgres:postgres@localhost:5432/testdb
          REDIS_URL: redis://localhost:6379

  e2e-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Install Playwright
        run: npx playwright install --with-deps
      
      - name: Build application
        run: npm run build
      
      - name: Run E2E tests
        run: npm run test:e2e
      
      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: playwright-report
          path: playwright-report/

  security-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Run security audit
        run: npm audit --audit-level=moderate
      
      - name: Run OWASP dependency check
        uses: dependency-check/Dependency-Check_Action@main
        with:
          project: 'project-name'
          path: '.'
          format: 'HTML'
      
      - name: Upload security results
        uses: actions/upload-artifact@v3
        with:
          name: security-report
          path: reports/
```

### テストレポート

テスト結果の包括的なレポートを生成:

```javascript
// jest.config.js
module.exports = {
  reporters: [
    'default',
    [
      'jest-html-reporter',
      {
        pageTitle: 'Test Report',
        outputPath: 'test-report/index.html',
        includeFailureMsg: true,
        includeConsoleLog: true,
        sort: 'status'
      }
    ],
    [
      'jest-junit',
      {
        outputDirectory: 'test-report',
        outputName: 'junit.xml',
        classNameTemplate: '{classname}',
        titleTemplate: '{title}',
        ancestorSeparator: ' › ',
        usePathForSuiteName: true
      }
    ]
  ]
};
```

## テストツール

### フロントエンド

#### React Testing Library

```javascript
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { UserProfile } from './UserProfile';

describe('UserProfile', () => {
  it('should display user information', () => {
    const user = {
      name: 'John Doe',
      email: 'john@example.com'
    };

    render(<UserProfile user={user} />);

    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('john@example.com')).toBeInTheDocument();
  });

  it('should handle form submission', async () => {
    const onSubmit = jest.fn();
    render(<UserProfile onSubmit={onSubmit} />);

    const nameInput = screen.getByLabelText('Name');
    const submitButton = screen.getByRole('button', { name: /save/i });

    await userEvent.type(nameInput, 'Jane Doe');
    await userEvent.click(submitButton);

    await waitFor(() => {
      expect(onSubmit).toHaveBeenCalledWith({
        name: 'Jane Doe'
      });
    });
  });
});
```

### バックエンド

#### Supertest (APIテスト用)

```javascript
import request from 'supertest';
import { app } from './app';

describe('API Endpoints', () => {
  describe('GET /api/users', () => {
    it('should return list of users', async () => {
      const response = await request(app)
        .get('/api/users')
        .expect('Content-Type', /json/)
        .expect(200);

      expect(response.body).toHaveProperty('users');
      expect(Array.isArray(response.body.users)).toBe(true);
    });
  });
});
```

#### テストコンテナ

```javascript
import { GenericContainer } from 'testcontainers';

describe('Database Tests', () => {
  let container;
  let database;

  beforeAll(async () => {
    container = await new GenericContainer('postgres:14')
      .withExposedPorts(5432)
      .withEnvironment({
        POSTGRES_USER: 'test',
        POSTGRES_PASSWORD: 'test',
        POSTGRES_DB: 'testdb'
      })
      .start();

    const connectionString = `postgresql://test:test@${container.getHost()}:${container.getMappedPort(5432)}/testdb`;
    database = await connect(connectionString);
  });

  afterAll(async () => {
    await database.disconnect();
    await container.stop();
  });

  // テストケース...
});
```

### E2Eテスト

#### Playwright

```javascript
import { test, expect } from '@playwright/test';

test.describe('E2E Tests', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:3000');
  });

  test('should navigate to user profile', async ({ page }) => {
    await page.click('text=Profile');
    await expect(page).toHaveURL(/.*profile/);
    await expect(page.locator('h1')).toContainText('User Profile');
  });
});
```

### パフォーマンステスト

#### k6

```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 10,
  duration: '30s',
};

export default function () {
  const res = http.get('http://api.example.com/users');
  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });
  sleep(1);
}
```

### セキュリティテスト

#### OWASP ZAP

```bash
# ZAPベースラインスキャン
docker run -v $(pwd):/zap/wrk/:rw -t owasp/zap2docker-stable \
  zap-baseline.py -t http://localhost:3000 -r zap-report.html

# ZAPフルスキャン
docker run -v $(pwd):/zap/wrk/:rw -t owasp/zap2docker-stable \
  zap-full-scan.py -t http://localhost:3000 -r zap-full-report.html
```

## ベストプラクティス

### テストの原則

1. **F.I.R.S.T. 原則に従う**:
   - **Fast**: テストは高速であるべき
   - **Independent**: テストは独立しているべき
   - **Repeatable**: テストは繰り返し可能であるべき
   - **Self-validating**: テストは自己検証すべき
   - **Timely**: テストはタイムリーに書くべき

2. **1つのアサートで1つのことをテスト**: 各テストは1つの明確な目的を持つべき

3. **Given-When-Then / Arrange-Act-Assert**: テストを明確な段階で構造化

4. **テストに意味のある名前を付ける**: テスト名は何をテストしているかを説明すべき

5. **魔法の数字を避ける**: 定数には意味のある名前を使用

6. **DRY(Don't Repeat Yourself)とWET(Write Everything Twice)のバランス**: テストは時には繰り返しが良い

### テストの組織化

```javascript
// 機能でテストをグループ化
describe('UserService', () => {
  // メソッドでグループ化
  describe('createUser', () => {
    // シナリオでグループ化
    describe('when valid data is provided', () => {
      it('should create a new user', () => {});
      it('should hash the password', () => {});
      it('should send welcome email', () => {});
    });

    describe('when invalid data is provided', () => {
      it('should throw validation error', () => {});
      it('should not save to database', () => {});
    });
  });

  describe('updateUser', () => {
    // ...
  });
});
```

### テストデータ管理

```javascript
// テストフィクスチャファイル
// tests/fixtures/users.js
export const validUser = {
  email: 'test@example.com',
  name: 'Test User',
  password: 'SecurePass123!'
};

export const invalidUsers = [
  { email: 'invalid', name: 'Test' },
  { email: '', name: 'Test' },
  { email: 'test@example.com', name: '' }
];

// テストでの使用
import { validUser, invalidUsers } from './fixtures/users';

describe('User validation', () => {
  it('should accept valid user', () => {
    expect(validateUser(validUser)).toBe(true);
  });

  it('should reject invalid users', () => {
    invalidUsers.forEach(user => {
      expect(() => validateUser(user)).toThrow();
    });
  });
});
```

### テストヘルパーとユーティリティ

```javascript
// tests/helpers/database.js
export async function createTestUser(overrides = {}) {
  const defaultUser = {
    email: 'test@example.com',
    name: 'Test User',
    password: 'password123'
  };

  return await db.users.create({
    ...defaultUser,
    ...overrides
  });
}

export async function cleanDatabase() {
  await db.users.deleteMany({});
  await db.posts.deleteMany({});
  await db.comments.deleteMany({});
}

// tests/helpers/auth.js
export async function getAuthToken(email = 'test@example.com') {
  const response = await request(app)
    .post('/api/auth/login')
    .send({ email, password: 'password123' });
  
  return response.body.token;
}

// テストでの使用
import { createTestUser, cleanDatabase } from './helpers/database';
import { getAuthToken } from './helpers/auth';

describe('Protected routes', () => {
  beforeEach(async () => {
    await cleanDatabase();
    await createTestUser();
  });

  it('should access protected route with valid token', async () => {
    const token = await getAuthToken();
    
    const response = await request(app)
      .get('/api/user/profile')
      .set('Authorization', `Bearer ${token}`)
      .expect(200);
  });
});
```

### モックとスタブのベストプラクティス

```javascript
// 良いモック: 必要な部分のみをモック化
jest.mock('../services/EmailService', () => ({
  sendEmail: jest.fn().mockResolvedValue(true)
}));

// 悪いモック: 過度なモック化
jest.mock('../services/EmailService');
jest.mock('../services/SMSService');
jest.mock('../services/PushNotificationService');
// ... 多数のモック

// 良いスタブ: 特定の動作を定義
const mockRepository = {
  findById: jest.fn()
    .mockResolvedValueOnce(user1)
    .mockResolvedValueOnce(user2)
};

// 悪いスタブ: 実装の詳細が多すぎる
const mockRepository = {
  findById: jest.fn(async (id) => {
    if (id === 1) return user1;
    if (id === 2) return user2;
    // ... 複雑なロジック
  })
};
```

### 非同期テスト

```javascript
// 良い例: async/await を使用
it('should fetch user data', async () => {
  const user = await userService.getUser(1);
  expect(user.name).toBe('Test User');
});

// 良い例: Promise を返す
it('should fetch user data', () => {
  return userService.getUser(1).then(user => {
    expect(user.name).toBe('Test User');
  });
});

// 悪い例: done コールバックを忘れる
it('should fetch user data', (done) => {
  userService.getUser(1).then(user => {
    expect(user.name).toBe('Test User');
    // done() を忘れている!
  });
});

// 良い例: done を正しく使用
it('should fetch user data', (done) => {
  userService.getUser(1).then(user => {
    expect(user.name).toBe('Test User');
    done();
  }).catch(done);
});
```

### テストのタイムアウト

```javascript
// 個別のテストのタイムアウトを設定
it('should complete long operation', async () => {
  await longRunningOperation();
}, 10000); // 10秒のタイムアウト

// スイート全体のタイムアウトを設定
describe('Integration tests', () => {
  jest.setTimeout(30000); // 30秒のタイムアウト

  it('should perform complex operation', async () => {
    await complexOperation();
  });
});
```

