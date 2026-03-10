# テスト標準

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

