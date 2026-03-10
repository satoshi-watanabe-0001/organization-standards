# テスト標準

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

