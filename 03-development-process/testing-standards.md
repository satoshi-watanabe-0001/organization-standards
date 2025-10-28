# テスト標準

## 概要

このドキュメントは、組織全体のソフトウェアテストに関する包括的な標準を定義します。これらの標準は、すべてのプロジェクトで一貫した品質、信頼性、保守性を確保します。

## 目次

1. [テスト戦略](#テスト戦略)
2. [テストピラミッド](#テストピラミッド)
3. [ユニットテスト](#ユニットテスト)
4. [統合テスト](#統合テスト)
5. [エンドツーエンド(E2E)テスト](#エンドツーエンドe2eテスト)
6. [パフォーマンステスト](#パフォーマンステスト)
7. [セキュリティテスト](#セキュリティテスト)
8. [テストカバレッジ要件](#テストカバレッジ要件)
9. [テスト自動化](#テスト自動化)
10. [テストツール](#テストツール)
11. [ベストプラクティス](#ベストプラクティス)
12. [Devin AIガイドライン](#devin-aiガイドライン)

## テスト戦略

### テストの目的

テストは以下を目的としています:

1. **品質保証**: コードが機能要件とビジネス要件を満たすことを検証
2. **リグレッション防止**: 新しいコード変更が既存機能を破壊しないことを確保
3. **ドキュメント化**: テストが期待される動作のライブドキュメントとして機能
4. **リファクタリングの信頼性**: 安全なコード改善を可能にする
5. **早期バグ検出**: 開発サイクルの早い段階で問題を発見

### テストレベル

以下のテストレベルを実装します:

1. **ユニットテスト**: 個別のコンポーネントや関数のテスト
2. **統合テスト**: コンポーネント間の相互作用のテスト
3. **E2Eテスト**: ユーザーパースペクティブからの完全なワークフローのテスト
4. **パフォーマンステスト**: システムのパフォーマンス特性の検証
5. **セキュリティテスト**: セキュリティ脆弱性の特定

## テストピラミッド

以下のテストピラミッド構造に従います:

```
        /\
       /  \      E2Eテスト(10%)
      /____\     
     /      \    統合テスト(30%)
    /________\   
   /          \  ユニットテスト(60%)
  /__________\
```

### テスト配分

- **ユニットテスト**: 全テストの60%
  - 高速で、独立性が高く、保守が容易
  - ビジネスロジックに焦点
  - モックとスタブを広範囲に使用

- **統合テスト**: 全テストの30%
  - コンポーネント間の相互作用を検証
  - データベース、API、サービスを含む
  - 実際の依存関係を使用

- **E2Eテスト**: 全テストの10%
  - クリティカルなユーザーフローに焦点
  - 最も遅く、最も壊れやすい
  - 戦略的に使用

## ユニットテスト

### 定義

ユニットテストは、依存関係をモック化した状態で、個別のコンポーネント、関数、メソッドを検証します。

### 要件

1. **独立性**: 各テストは独立して実行可能であること
2. **高速**: ユニットテストスイート全体が1分以内に実行されること
3. **決定性**: 一貫した結果を生成すること
4. **可読性**: テストは明確でわかりやすいこと
5. **保守性**: 簡単に更新・保守できること

### ユニットテスト構造

AAA(Arrange-Act-Assert)パターンに従います:

```javascript
describe('UserService', () => {
  describe('createUser', () => {
    it('should create a new user with valid data', () => {
      // Arrange
      const userData = {
        email: 'test@example.com',
        name: 'Test User',
        password: 'SecurePass123!'
      };
      const mockRepository = {
        save: jest.fn().mockResolvedValue({ id: 1, ...userData })
      };
      const userService = new UserService(mockRepository);

      // Act
      const result = await userService.createUser(userData);

      // Assert
      expect(result).toHaveProperty('id');
      expect(result.email).toBe(userData.email);
      expect(mockRepository.save).toHaveBeenCalledWith(userData);
    });

    it('should throw error when email is invalid', () => {
      // Arrange
      const invalidData = { email: 'invalid', name: 'Test' };
      const userService = new UserService(mockRepository);

      // Act & Assert
      await expect(userService.createUser(invalidData))
        .rejects
        .toThrow('Invalid email format');
    });
  });
});
```

### テストケースの命名

明確でわかりやすいテスト名を使用します:

```javascript
// 良い例
it('should return user when valid ID is provided', () => {});
it('should throw NotFoundError when user does not exist', () => {});
it('should hash password before saving user', () => {});

// 悪い例
it('test user', () => {});
it('works', () => {});
it('test1', () => {});
```

### モッキング戦略

1. **依存関係をモック化**: 外部依存関係は常にモック化
2. **スパイを使用**: 関数呼び出しとパラメータを検証
3. **実装の詳細を避ける**: パブリックインターフェースをテスト

```javascript
// 依存関係のモック化
jest.mock('../services/EmailService');

// スパイの使用
const sendEmailSpy = jest.spyOn(EmailService.prototype, 'sendEmail');
expect(sendEmailSpy).toHaveBeenCalledWith(expectedEmail);

// 実装詳細のテストを避ける
// 悪い例: 内部メソッドをテスト
expect(service._internalMethod).toHaveBeenCalled();

// 良い例: パブリックインターフェースをテスト
expect(service.publicMethod()).toBe(expectedResult);
```

### カバレッジ目標

ユニットテストのカバレッジ目標:

- **全体カバレッジ**: 最低80%
- **クリティカルパス**: 100%
- **ビジネスロジック**: 95%
- **ユーティリティ関数**: 90%
- **UIコンポーネント**: 70%

## 統合テスト

### 定義

統合テストは、複数のコンポーネント、サービス、またはシステムが正しく連携することを検証します。

### 要件

1. **実際の依存関係**: 可能な限り実際のサービスを使用
2. **データベーステスト**: 実際のデータベースを使用(テストコンテナなど)
3. **APIテスト**: 実際のHTTPリクエストをテスト
4. **トランザクション**: 各テスト後にクリーンアップ
5. **独立性**: テストは任意の順序で実行可能

### 統合テストの例

```javascript
describe('User API Integration Tests', () => {
  let app;
  let database;
  let testUser;

  beforeAll(async () => {
    // テストデータベースのセットアップ
    database = await setupTestDatabase();
    app = createApp(database);
  });

  afterAll(async () => {
    await database.close();
  });

  beforeEach(async () => {
    // 各テスト前にデータベースをクリーン
    await database.clean();
    testUser = await database.createUser({
      email: 'test@example.com',
      name: 'Test User'
    });
  });

  describe('POST /api/users', () => {
    it('should create a new user', async () => {
      const userData = {
        email: 'new@example.com',
        name: 'New User',
        password: 'SecurePass123!'
      };

      const response = await request(app)
        .post('/api/users')
        .send(userData)
        .expect(201);

      expect(response.body).toMatchObject({
        email: userData.email,
        name: userData.name
      });

      // データベースで確認
      const savedUser = await database.findUserByEmail(userData.email);
      expect(savedUser).toBeDefined();
      expect(savedUser.name).toBe(userData.name);
    });

    it('should reject duplicate email', async () => {
      const duplicateData = {
        email: testUser.email,
        name: 'Another User'
      };

      const response = await request(app)
        .post('/api/users')
        .send(duplicateData)
        .expect(409);

      expect(response.body.error).toContain('already exists');
    });
  });

  describe('GET /api/users/:id', () => {
    it('should return user with valid ID', async () => {
      const response = await request(app)
        .get(`/api/users/${testUser.id}`)
        .expect(200);

      expect(response.body).toMatchObject({
        id: testUser.id,
        email: testUser.email,
        name: testUser.name
      });
    });

    it('should return 404 for non-existent user', async () => {
      const response = await request(app)
        .get('/api/users/99999')
        .expect(404);

      expect(response.body.error).toContain('not found');
    });
  });
});
```

### データベーステスト

テストコンテナを使用して実際のデータベースをテスト:

```javascript
import { GenericContainer } from 'testcontainers';

describe('Database Integration Tests', () => {
  let container;
  let database;

  beforeAll(async () => {
    // PostgreSQLコンテナの起動
    container = await new GenericContainer('postgres:14')
      .withEnvironment({
        POSTGRES_USER: 'test',
        POSTGRES_PASSWORD: 'test',
        POSTGRES_DB: 'testdb'
      })
      .withExposedPorts(5432)
      .start();

    const connectionString = `postgresql://test:test@${container.getHost()}:${container.getMappedPort(5432)}/testdb`;
    database = await connectToDatabase(connectionString);
  });

  afterAll(async () => {
    await database.disconnect();
    await container.stop();
  });

  it('should perform complex query correctly', async () => {
    // テストデータの挿入
    await database.query(`
      INSERT INTO users (email, name) VALUES
      ('user1@example.com', 'User 1'),
      ('user2@example.com', 'User 2')
    `);

    // クエリの実行
    const result = await database.query(`
      SELECT * FROM users WHERE email LIKE '%example.com'
    `);

    expect(result.rows).toHaveLength(2);
  });
});
```

### APIテスト

```javascript
describe('External API Integration', () => {
  let mockServer;

  beforeAll(() => {
    // 外部APIのモックサーバー起動
    mockServer = setupMockServer();
  });

  afterAll(() => {
    mockServer.close();
  });

  it('should handle external API successfully', async () => {
    mockServer.get('/api/data')
      .reply(200, { data: 'test data' });

    const service = new ExternalService();
    const result = await service.fetchData();

    expect(result.data).toBe('test data');
  });

  it('should handle API errors gracefully', async () => {
    mockServer.get('/api/data')
      .reply(500, { error: 'Internal Server Error' });

    const service = new ExternalService();
    
    await expect(service.fetchData())
      .rejects
      .toThrow('Failed to fetch data');
  });
});
```

## エンドツーエンド(E2E)テスト

### 定義

E2Eテストは、実際のユーザーの視点からアプリケーション全体を検証します。

### 要件

1. **ユーザーフロー**: 実際のユーザージャーニーをシミュレート
2. **ブラウザテスト**: 複数のブラウザで実行
3. **データ独立性**: テストデータを独立して管理
4. **環境**: 本番環境に近い環境でテスト
5. **クリーンアップ**: テストデータを適切にクリーンアップ

### E2Eテストフレームワーク

Playwrightを主要なE2Eテストフレームワークとして使用:

```javascript
import { test, expect } from '@playwright/test';

test.describe('User Authentication Flow', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:3000');
  });

  test('should allow user to sign up', async ({ page }) => {
    // サインアップページへ移動
    await page.click('text=Sign Up');
    await expect(page).toHaveURL(/.*signup/);

    // フォームに記入
    await page.fill('input[name="email"]', 'newuser@example.com');
    await page.fill('input[name="name"]', 'New User');
    await page.fill('input[name="password"]', 'SecurePass123!');
    await page.fill('input[name="confirmPassword"]', 'SecurePass123!');

    // 送信
    await page.click('button[type="submit"]');

    // 成功メッセージを確認
    await expect(page.locator('text=Welcome')).toBeVisible();
    await expect(page).toHaveURL(/.*dashboard/);
  });

  test('should allow user to log in', async ({ page }) => {
    // ログインページへ移動
    await page.click('text=Log In');
    
    // 認証情報を入力
    await page.fill('input[name="email"]', 'existing@example.com');
    await page.fill('input[name="password"]', 'password123');
    
    // ログイン
    await page.click('button[type="submit"]');
    
    // ダッシュボードにリダイレクトされることを確認
    await expect(page).toHaveURL(/.*dashboard/);
    await expect(page.locator('text=Dashboard')).toBeVisible();
  });

  test('should show error for invalid credentials', async ({ page }) => {
    await page.click('text=Log In');
    
    await page.fill('input[name="email"]', 'wrong@example.com');
    await page.fill('input[name="password"]', 'wrongpassword');
    
    await page.click('button[type="submit"]');
    
    // エラーメッセージを確認
    await expect(page.locator('text=Invalid credentials')).toBeVisible();
    await expect(page).toHaveURL(/.*login/);
  });
});

test.describe('User Profile Management', () => {
  test.beforeEach(async ({ page }) => {
    // ログイン済みユーザーとしてテストをセットアップ
    await loginAsTestUser(page);
  });

  test('should update user profile', async ({ page }) => {
    // プロフィールページへ移動
    await page.click('text=Profile');
    
    // プロフィール情報を更新
    await page.fill('input[name="name"]', 'Updated Name');
    await page.fill('textarea[name="bio"]', 'Updated bio');
    
    // 変更を保存
    await page.click('button:has-text("Save Changes")');
    
    // 成功メッセージを確認
    await expect(page.locator('text=Profile updated')).toBeVisible();
    
    // 更新されたデータを確認
    const nameInput = await page.inputValue('input[name="name"]');
    expect(nameInput).toBe('Updated Name');
  });

  test('should upload profile picture', async ({ page }) => {
    await page.click('text=Profile');
    
    // ファイルアップロード
    const fileInput = await page.locator('input[type="file"]');
    await fileInput.setInputFiles('test-fixtures/profile-pic.jpg');
    
    // アップロードを待機
    await page.waitForResponse(response => 
      response.url().includes('/api/upload') && response.status() === 200
    );
    
    // プロフィール写真が表示されることを確認
    const profileImage = page.locator('img[alt="Profile picture"]');
    await expect(profileImage).toBeVisible();
  });
});
```

### ページオブジェクトモデル

再利用性を向上させるためにページオブジェクトを使用:

```javascript
// pages/LoginPage.js
export class LoginPage {
  constructor(page) {
    this.page = page;
    this.emailInput = page.locator('input[name="email"]');
    this.passwordInput = page.locator('input[name="password"]');
    this.submitButton = page.locator('button[type="submit"]');
    this.errorMessage = page.locator('.error-message');
  }

  async goto() {
    await this.page.goto('/login');
  }

  async login(email, password) {
    await this.emailInput.fill(email);
    await this.passwordInput.fill(password);
    await this.submitButton.click();
  }

  async getErrorMessage() {
    return await this.errorMessage.textContent();
  }
}

// テストでの使用
test('should login with valid credentials', async ({ page }) => {
  const loginPage = new LoginPage(page);
  await loginPage.goto();
  await loginPage.login('user@example.com', 'password123');
  
  await expect(page).toHaveURL(/.*dashboard/);
});
```

### ビジュアルリグレッションテスト

```javascript
test('should match login page screenshot', async ({ page }) => {
  await page.goto('/login');
  
  // スクリーンショットを比較
  await expect(page).toHaveScreenshot('login-page.png', {
    maxDiffPixels: 100
  });
});

test('should match dashboard layout', async ({ page }) => {
  await loginAsTestUser(page);
  await page.goto('/dashboard');
  
  // 特定の要素のスクリーンショット
  const dashboard = page.locator('.dashboard-container');
  await expect(dashboard).toHaveScreenshot('dashboard-layout.png');
});
```

## パフォーマンステスト

### 定義

パフォーマンステストは、負荷下でのシステムの応答性、安定性、スケーラビリティを検証します。

### パフォーマンステストの種類

1. **ロードテスト**: 通常および高負荷条件下でのパフォーマンスを検証
2. **ストレステスト**: システムの限界を特定
3. **スパイクテスト**: 負荷の急激な増加への対応を検証
4. **持久力テスト**: 長期間の安定性を検証

### k6を使用したパフォーマンステスト

```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

export const options = {
  stages: [
    { duration: '2m', target: 100 },  // 100ユーザーまでランプアップ
    { duration: '5m', target: 100 },  // 100ユーザーで5分間維持
    { duration: '2m', target: 200 },  // 200ユーザーまでランプアップ
    { duration: '5m', target: 200 },  // 200ユーザーで5分間維持
    { duration: '2m', target: 0 },    // ランプダウン
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95%のリクエストが500ms未満
    http_req_failed: ['rate<0.01'],   // エラー率が1%未満
    errors: ['rate<0.1'],             // カスタムエラー率が10%未満
  },
};

export default function () {
  // APIエンドポイントのテスト
  const loginRes = http.post('http://api.example.com/auth/login', {
    email: 'test@example.com',
    password: 'password123'
  });

  const loginSuccess = check(loginRes, {
    'login status is 200': (r) => r.status === 200,
    'login has token': (r) => r.json('token') !== undefined,
  });

  errorRate.add(!loginSuccess);

  if (loginSuccess) {
    const token = loginRes.json('token');
    
    // 認証済みリクエスト
    const dashboardRes = http.get('http://api.example.com/dashboard', {
      headers: { Authorization: `Bearer ${token}` },
    });

    check(dashboardRes, {
      'dashboard status is 200': (r) => r.status === 200,
      'dashboard loads in <500ms': (r) => r.timings.duration < 500,
    });
  }

  sleep(1);
}
```

### データベースパフォーマンステスト

```javascript
import { check } from 'k6';
import sql from 'k6/x/sql';

const db = sql.open('postgres', 'postgres://user:password@localhost:5432/testdb');

export default function () {
  // 読み取りクエリのテスト
  const result = sql.query(db, 'SELECT * FROM users WHERE id = $1', 1);
  check(result, {
    'query returned data': (r) => r.length > 0,
  });

  // 書き込み操作のテスト
  sql.exec(db, `
    INSERT INTO logs (user_id, action, timestamp)
    VALUES ($1, $2, NOW())
  `, 1, 'test_action');
}

export function teardown() {
  db.close();
}
```

### パフォーマンス基準

すべてのAPIエンドポイントは以下を満たす必要があります:

- **応答時間**:
  - p50 (中央値): < 200ms
  - p95: < 500ms
  - p99: < 1000ms

- **スループット**:
  - 最低: 1000 req/sec
  - 目標: 5000 req/sec

- **エラー率**:
  - 通常負荷下: < 0.1%
  - 高負荷下: < 1%

- **データベースクエリ**:
  - 単純クエリ: < 10ms
  - 複雑クエリ: < 100ms
  - レポートクエリ: < 1000ms

## セキュリティテスト

### 定義

セキュリティテストは、脆弱性を特定し、セキュリティ制御を検証し、アプリケーションがセキュリティ標準に準拠していることを確認します。

### セキュリティテストの種類

1. **認証テスト**: ユーザー認証メカニズムを検証
2. **認可テスト**: アクセス制御とパーミッションを検証
3. **入力検証テスト**: 入力サニタイゼーションを検証
4. **セッション管理テスト**: セッションセキュリティを検証
5. **脆弱性スキャン**: 既知の脆弱性を特定

### 認証と認可のテスト

```javascript
describe('Authentication Security Tests', () => {
  describe('Password Security', () => {
    it('should reject weak passwords', async () => {
      const weakPasswords = [
        'password',
        '12345678',
        'qwerty',
        'abc123'
      ];

      for (const password of weakPasswords) {
        const response = await request(app)
          .post('/api/auth/register')
          .send({
            email: 'test@example.com',
            password: password
          })
          .expect(400);

        expect(response.body.error).toContain('password');
      }
    });

    it('should hash passwords before storage', async () => {
      const password = 'SecurePass123!';
      
      await request(app)
        .post('/api/auth/register')
        .send({
          email: 'test@example.com',
          password: password
        })
        .expect(201);

      // データベースから直接取得
      const user = await db.findUserByEmail('test@example.com');
      
      // パスワードがハッシュ化されていることを確認
      expect(user.password).not.toBe(password);
      expect(user.password).toMatch(/^\$2[ayb]\$.{56}$/); // bcryptハッシュ
    });

    it('should implement rate limiting on login', async () => {
      const attempts = Array(6).fill(null);
      
      // 6回失敗したログイン試行
      for (const _ of attempts) {
        await request(app)
          .post('/api/auth/login')
          .send({
            email: 'test@example.com',
            password: 'wrongpassword'
          });
      }

      // 7回目の試行はレート制限されるべき
      const response = await request(app)
        .post('/api/auth/login')
        .send({
          email: 'test@example.com',
          password: 'wrongpassword'
        })
        .expect(429);

      expect(response.body.error).toContain('Too many attempts');
    });
  });

  describe('Authorization Tests', () => {
    it('should prevent unauthorized access to protected routes', async () => {
      const response = await request(app)
        .get('/api/admin/users')
        .expect(401);

      expect(response.body.error).toContain('authentication required');
    });

    it('should prevent access with invalid token', async () => {
      const response = await request(app)
        .get('/api/user/profile')
        .set('Authorization', 'Bearer invalid_token')
        .expect(401);

      expect(response.body.error).toContain('Invalid token');
    });

    it('should enforce role-based access control', async () => {
      // 通常ユーザートークンを取得
      const userToken = await getAuthToken('user@example.com');

      // 管理者専用エンドポイントへのアクセスを試行
      const response = await request(app)
        .get('/api/admin/users')
        .set('Authorization', `Bearer ${userToken}`)
        .expect(403);

      expect(response.body.error).toContain('Insufficient permissions');
    });
  });
});
```

### 入力検証テスト

```javascript
describe('Input Validation Security Tests', () => {
  describe('SQL Injection Prevention', () => {
    it('should prevent SQL injection in user input', async () => {
      const maliciousInput = "admin' OR '1'='1";
      
      const response = await request(app)
        .post('/api/users/search')
        .send({ query: maliciousInput })
        .expect(200);

      // 全ユーザーではなく、一致するユーザーのみを返すべき
      expect(response.body.users).not.toContain(allUsers);
    });
  });

  describe('XSS Prevention', () => {
    it('should sanitize HTML in user input', async () => {
      const xssPayload = '<script>alert("XSS")</script>';
      
      const response = await request(app)
        .post('/api/comments')
        .send({ content: xssPayload })
        .expect(201);

      // サニタイズされたコンテンツを確認
      expect(response.body.content).not.toContain('<script>');
      expect(response.body.content).toContain('&lt;script&gt;');
    });
  });

  describe('Path Traversal Prevention', () => {
    it('should prevent directory traversal attacks', async () => {
      const maliciousPath = '../../../etc/passwd';
      
      const response = await request(app)
        .get(`/api/files/${maliciousPath}`)
        .expect(400);

      expect(response.body.error).toContain('Invalid file path');
    });
  });

  describe('Command Injection Prevention', () => {
    it('should prevent command injection', async () => {
      const maliciousCommand = 'test; rm -rf /';
      
      const response = await request(app)
        .post('/api/process')
        .send({ input: maliciousCommand })
        .expect(400);

      expect(response.body.error).toContain('Invalid input');
    });
  });
});
```

### セッション管理テスト

```javascript
describe('Session Security Tests', () => {
  it('should generate secure session tokens', async () => {
    const response = await request(app)
      .post('/api/auth/login')
      .send({ email: 'test@example.com', password: 'password123' })
      .expect(200);

    const token = response.body.token;
    
    // トークンが十分な長さであることを確認
    expect(token.length).toBeGreaterThan(32);
    
    // トークンがランダムであることを確認
    const response2 = await request(app)
      .post('/api/auth/login')
      .send({ email: 'test@example.com', password: 'password123' });
    
    expect(response2.body.token).not.toBe(token);
  });

  it('should expire sessions after timeout', async () => {
    const token = await getAuthToken();
    
    // トークンの有効期限を設定(テスト用に短縮)
    await new Promise(resolve => setTimeout(resolve, 3600000)); // 1時間
    
    const response = await request(app)
      .get('/api/user/profile')
      .set('Authorization', `Bearer ${token}`)
      .expect(401);

    expect(response.body.error).toContain('Token expired');
  });

  it('should invalidate session on logout', async () => {
    const token = await getAuthToken();
    
    // ログアウト
    await request(app)
      .post('/api/auth/logout')
      .set('Authorization', `Bearer ${token}`)
      .expect(200);

    // トークンが無効化されたことを確認
    await request(app)
      .get('/api/user/profile')
      .set('Authorization', `Bearer ${token}`)
      .expect(401);
  });
});
```

### 自動化セキュリティスキャン

```javascript
// OWASPのセキュリティリスクをテスト
describe('OWASP Top 10 Security Tests', () => {
  it('should test for broken authentication', async () => {
    // 複数の失敗したログイン試行をテスト
    // アカウントロックアウトを確認
    // セッション固定を確認
  });

  it('should test for sensitive data exposure', async () => {
    // HTTPSの使用を確認
    // クッキーのセキュアフラグを確認
    // エラーメッセージでの情報漏洩を確認
  });

  it('should test for XML external entities (XXE)', async () => {
    // XXE攻撃ペイロードでテスト
  });

  it('should test for broken access control', async () => {
    // 水平権限昇格を試行
    // 垂直権限昇格を試行
  });

  it('should test for security misconfiguration', async () => {
    // デフォルトの認証情報を確認
    // 不要な機能を確認
    // 詳細なエラーメッセージを確認
  });
});
```

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

## Devin AIガイドライン

### テスト開発へのDevin AIの使用

Devin AIは、次の方法でテスト開発を支援できます:

1. **テストケースの生成**
   - 既存のコードに基づいてテストケースを生成
   - エッジケースとコーナーケースを特定
   - 包括的なテストスイートを作成

2. **テストカバレッジの改善**
   - カバレッジギャップを分析
   - 欠落しているテストケースを提案
   - カバレッジメトリクスを改善

3. **テストリファクタリング**
   - テストの重複を特定
   - テストの保守性を改善
   - ベストプラクティスに従うようにテストを更新

4. **テストの自動修正**
   - 失敗したテストを分析
   - 問題の根本原因を特定
   - 修正を提案して実装

### Devinプロンプトの例

#### テスト生成

```
"Generate comprehensive unit tests for the UserService class. Include tests for:
- Creating a new user with valid data
- Handling validation errors
- Password hashing
- Email uniqueness validation
- Edge cases and error conditions

Use Jest and follow AAA pattern. Mock external dependencies."
```

#### カバレッジ改善

```
"Analyze the current test coverage for the authentication module.
Identify gaps in coverage and generate tests to improve coverage to at least 90%.
Focus on:
- Branch coverage
- Error handling paths
- Edge cases

Provide a summary of the coverage improvements."
```

#### テストリファクタリング

```
"Refactor the existing test suite for the UserController to:
- Remove test duplication
- Improve test organization
- Add better test descriptions
- Extract common test helpers
- Follow testing best practices

Maintain all existing test functionality."
```

#### E2Eテスト生成

```
"Create E2E tests using Playwright for the user registration flow:
1. Navigate to registration page
2. Fill in registration form
3. Submit form
4. Verify success message
5. Verify user can login
6. Test validation errors

Include tests for both happy path and error scenarios."
```

### Devinのテスト実行

```
"Run the full test suite and:
1. Fix any failing tests
2. Update deprecated test syntax
3. Improve test performance
4. Generate a coverage report
5. Identify and fix flaky tests

Provide a summary of changes made and test results."
```

### Devinのテストメンテナンス

```
"Review and update all tests in the project to:
1. Use the latest testing library versions
2. Follow current best practices
3. Improve test reliability
4. Remove obsolete tests
5. Update test documentation

Create a PR with the changes and a detailed description."
```

### Devinとのベストプラクティス

1. **明確な指示を提供**: テスト目標と要件を明確に述べる
2. **コンテキストを含める**: 関連するコードと既存のテストパターンを提供
3. **制約を指定**: 使用すべきテストフレームワークとライブラリを指定
4. **結果を検証**: Devinが生成したテストを常にレビューして検証
5. **段階的に反復**: 一度にすべてを生成するのではなく、小さな変更から始める

### テストレビューチェックリスト

Devinが生成したテストをレビューする際:

- [ ] テストは独立して実行されるか?
- [ ] テストは適切に構造化されているか(AAA/Given-When-Then)?
- [ ] モックは適切に使用されているか?
- [ ] テスト名は明確でわかりやすいか?
- [ ] エッジケースがカバーされているか?
- [ ] エラー処理がテストされているか?
- [ ] テストは高速に実行されるか?
- [ ] テストは保守可能か?
- [ ] テストはベストプラクティスに従っているか?
- [ ] カバレッジ要件が満たされているか?

---

## まとめ

これらのテスト標準は、組織全体で高品質で信頼性の高い保守可能なソフトウェアを確保するための包括的なフレームワークを提供します。これらの標準に従うことで、バグを早期に発見し、リグレッションを防止し、コードの品質を維持できます。

テストは継続的なプロセスであり、コード自体と同じくらい重要です。これらの標準に従い、Devin AIなどのツールを活用することで、効率的で効果的なテスト戦略を実現できます。

最終更新日: 2024-01-20