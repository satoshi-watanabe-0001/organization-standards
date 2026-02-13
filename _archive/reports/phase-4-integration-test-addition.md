# Phase 4レビュー・QAガイド - 統合テスト追加セクション

> **統合先**: `phase-guides/phase-4-review-qa-guide.md`  
> **挿入位置**: Step 4.4「統合テスト」セクションを置き換え  
> **理由**: EC-15で発覚した統合テスト実施要件の不明確さを解決

---

## Step 4.4: 統合テスト・APIレベルテスト (45-90分)

### 概要

統合テストは、複数のコンポーネント（データベース、外部API、メッセージキュー等）を組み合わせた状態で動作を検証するテストです。特にAPI開発においては、**HTTPリクエスト/レスポンスを実際の環境で検証するAPIレベルテスト**が重要です。

---

### 4.4.1 統合テスト実施の判断基準 🆕

#### ✅ 統合テストが必須の場合

| ケース | 対象範囲 | コンテナ化 |
|-------|---------|-----------|
| **新規プロジェクト** | 全API endpoints | 🔴 必須 |
| **既存への機能追加** | 新規endpoints + 影響を受ける既存endpoints | 🔴 必須 |
| **バグ修正（API関連）** | 修正対象のendpoint + 関連endpoints | 🟡 推奨 |
| **外部システム連携** | 連携を含むすべての機能 | 🔴 必須 |

#### 🟡 統合テストが推奨の場合

- バグ修正（ロジック変更で影響範囲が広い）
- リファクタリング（公開APIに影響がある）

#### ⚪ 統合テストが任意の場合

- 設定変更のみ（ロジック変更なし）
- ドキュメント更新のみ

---

### 4.4.2 APIレベルテストとは 🆕

**定義**:
- HTTPリクエスト/レスポンスを検証するテスト
- データベース、認証、外部連携を含む統合環境でのテスト
- **ユニットテスト（モック使用）とは異なり、実際の依存関係を使用**

**ユニットテスト vs 統合テスト**:

| 観点 | ユニットテスト | 統合テスト（APIレベル） |
|------|--------------|----------------------|
| **対象** | 単一クラス/関数 | API endpoint全体 |
| **依存関係** | モック使用 | 実際のDB・サービス使用 |
| **実行速度** | 高速（<1秒） | 中速（1-5秒/テスト） |
| **実行環境** | メモリ内 | コンテナ環境 |
| **目的** | ロジック検証 | 統合動作検証 |

---

### 4.4.3 必須検証項目 🆕

#### ✅ HTTPステータスコード
```yaml
正常系:
  - 200 OK: リソース取得成功
  - 201 Created: リソース作成成功
  - 204 No Content: 削除成功

異常系:
  - 400 Bad Request: バリデーションエラー
  - 401 Unauthorized: 認証エラー
  - 403 Forbidden: 権限エラー
  - 404 Not Found: リソース不在
  - 500 Internal Server Error: システムエラー
```

#### ✅ レスポンスボディ
```yaml
検証項目:
  - JSONスキーマ検証
  - 必須フィールドの存在確認
  - データ型の検証
  - 値の範囲チェック
  - ビジネスルールの検証
```

#### ✅ レスポンスヘッダー
```yaml
検証項目:
  - Content-Type: application/json
  - 認証トークン（該当する場合）
  - CORS設定（該当する場合）
```

#### ✅ データベース状態
```yaml
検証項目:
  - データが正しく保存されているか
  - トランザクションが正しく動作しているか
  - 外部キー制約が守られているか
  - 楽観的ロックが動作しているか
```

#### ✅ エラーハンドリング
```yaml
検証項目:
  - バリデーションエラーメッセージ
  - 権限エラーメッセージ
  - システムエラーメッセージ
  - ログ出力
```

---

### 4.4.4 コンテナ化テストの判断基準 🆕

#### 🔴 コンテナ化が必須の場合

**データベース依存**:
```yaml
該当:
  - PostgreSQL, MySQL, MongoDB等を使用
  - 複数のデータベースを使用
  - 複雑なクエリ・トランザクションを使用
```

**外部サービス依存**:
```yaml
該当:
  - Redis, RabbitMQ, Kafka等
  - メールサーバー（SMTP）
  - 複雑なミドルウェア構成
```

**環境差異の影響**:
```yaml
該当:
  - OS依存のライブラリ使用
  - ネイティブモジュール使用
  - ファイルシステム操作
```

**マイクロサービス**:
```yaml
該当:
  - 複数サービス間の連携テスト
  - サービスメッシュの検証
```

#### 🟡 コンテナ化が推奨の場合

- CI/CD環境での一貫性確保
- 複数バージョンのテスト（データベース等）

#### ⚪ コンテナ化が不要の場合

- シンプルなユニットテスト（モックだけで完結）
- インメモリデータベース使用（H2, SQLite等）で十分な場合

---

### 4.4.5 コンテナ化方法の選択 🆕

#### **方法1: TestContainers（推奨）**

**使用ケース**:
- Java/Spring Boot, Node.js, Python等
- 単一サービスのAPIテスト
- CI/CD環境での自動テスト

**メリット**:
- ✅ テストコードから直接コンテナを制御
- ✅ テスト完了後に自動クリーンアップ
- ✅ 並列実行が容易
- ✅ IDE統合が良好

**実装例（Spring Boot + PostgreSQL）**:
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserApiIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testCreateUser_Success() {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "newuser@example.com",
            "SecurePassword123!",
            "New User"
        );
        
        // When
        ResponseEntity<UserResponse> response = restTemplate.postForEntity(
            "/api/users",
            request,
            UserResponse.class
        );
        
        // Then: HTTPステータスコード検証
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        // Then: レスポンスボディ検証
        UserResponse user = response.getBody();
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("newuser@example.com");
        assertThat(user.getName()).isEqualTo("New User");
        assertThat(user.getId()).isNotNull();
        
        // Then: データベース状態検証
        Optional<User> savedUser = userRepository.findById(user.getId());
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo("newuser@example.com");
    }
    
    @Test
    void testCreateUser_DuplicateEmail() {
        // Given: 既存ユーザー
        userRepository.save(new User("existing@example.com", "password", "Existing"));
        
        CreateUserRequest request = new CreateUserRequest(
            "existing@example.com",  // 重複
            "SecurePassword123!",
            "New User"
        );
        
        // When
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
            "/api/users",
            request,
            ErrorResponse.class
        );
        
        // Then: 400 Bad Request
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("既に使用されています");
    }
}
```

**実装例（Node.js + Express + PostgreSQL）**:
```javascript
import { GenericContainer } from 'testcontainers';
import supertest from 'supertest';
import { app } from '../src/app';

describe('User API Integration Tests', () => {
  let postgresContainer;
  let request;
  
  beforeAll(async () => {
    // PostgreSQLコンテナ起動
    postgresContainer = await new GenericContainer('postgres:15')
      .withEnvironment({
        POSTGRES_DB: 'testdb',
        POSTGRES_USER: 'test',
        POSTGRES_PASSWORD: 'test'
      })
      .withExposedPorts(5432)
      .start();
    
    // 環境変数設定
    process.env.DATABASE_URL = `postgresql://test:test@${postgresContainer.getHost()}:${postgresContainer.getMappedPort(5432)}/testdb`;
    
    // マイグレーション実行
    await runMigrations();
    
    request = supertest(app);
  });
  
  afterAll(async () => {
    await postgresContainer.stop();
  });
  
  test('POST /api/users - success', async () => {
    // Given
    const newUser = {
      email: 'newuser@example.com',
      password: 'SecurePassword123!',
      name: 'New User'
    };
    
    // When
    const response = await request
      .post('/api/users')
      .send(newUser)
      .expect(201);
    
    // Then
    expect(response.body).toMatchObject({
      email: 'newuser@example.com',
      name: 'New User',
      id: expect.any(Number)
    });
    expect(response.body.password).toBeUndefined(); // パスワード非公開確認
  });
});
```

---

#### **方法2: Docker Compose**

**使用ケース**:
- マイクロサービス間の連携テスト
- 複雑なインフラ構成のテスト
- 手動での統合テスト環境構築

**メリット**:
- ✅ 複数サービスの同時起動が容易
- ✅ 本番環境に近い構成でテスト可能
- ✅ 開発環境としても利用可能

**実装例（docker-compose.test.yml）**:
```yaml
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/testdb
      SPRING_DATASOURCE_USERNAME: test
      SPRING_DATASOURCE_PASSWORD: test
      SPRING_REDIS_HOST: redis
      SPRING_MAIL_HOST: mailhog
      SPRING_MAIL_PORT: 1025
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_started
      mailhog:
        condition: service_started
  
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: testdb
      POSTGRES_USER: test
      POSTGRES_PASSWORD: test
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U test"]
      interval: 5s
      timeout: 5s
      retries: 5
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
  
  mailhog:
    image: mailhog/mailhog:latest
    ports:
      - "1025:1025"  # SMTP
      - "8025:8025"  # Web UI
```

**テスト実行スクリプト**:
```bash
#!/bin/bash
# run-integration-tests.sh

set -e

echo "🚀 Starting test environment..."
docker-compose -f docker-compose.test.yml up -d

echo "⏳ Waiting for services to be healthy..."
docker-compose -f docker-compose.test.yml exec -T postgres pg_isready -U test

echo "📊 Running migrations..."
docker-compose -f docker-compose.test.yml exec -T app ./gradlew flywayMigrate

echo "🧪 Running integration tests..."
docker-compose -f docker-compose.test.yml exec -T app ./gradlew integrationTest

TEST_EXIT_CODE=$?

echo "🧹 Cleaning up..."
docker-compose -f docker-compose.test.yml down -v

if [ $TEST_EXIT_CODE -eq 0 ]; then
  echo "✅ All tests passed!"
else
  echo "❌ Tests failed!"
  exit $TEST_EXIT_CODE
fi
```

---

### 4.4.6 EC-15の具体例 🆕

**PBI**: EC-15 パスワードリセットAPI実装

**分類**: タイプ2（既存プロジェクトへの機能追加）

**統合テスト要件**:
- 🔴 **必須**: 新規API endpoints
  - `POST /api/auth/password-reset/request`
  - `POST /api/auth/password-reset/confirm`
- 🔴 **必須**: データベース操作
  - `password_reset_tokens`テーブルへの挿入・検証
  - トークン有効期限の確認
- 🔴 **必須**: 外部連携
  - メール送信サービス（実際またはMailHogモック）

**APIレベルテスト実装例**:
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class PasswordResetApiIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Container
    static GenericContainer<?> mailhog = new GenericContainer<>("mailhog/mailhog:latest")
        .withExposedPorts(1025, 8025);
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.mail.host", mailhog::getHost);
        registry.add("spring.mail.port", () -> mailhog.getMappedPort(1025));
    }
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @BeforeEach
    void setup() {
        // テストユーザー作成
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("OldPassword123!"));
        userRepository.save(user);
    }
    
    @Test
    void testPasswordResetRequest_Success() {
        // Given
        PasswordResetRequestDto request = new PasswordResetRequestDto("test@example.com");
        
        // When
        ResponseEntity<MessageResponse> response = restTemplate.postForEntity(
            "/api/auth/password-reset/request",
            request,
            MessageResponse.class
        );
        
        // Then: HTTPステータスコード
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Then: レスポンスボディ
        assertThat(response.getBody().getMessage())
            .contains("パスワードリセット用のメールを送信しました");
        
        // Then: データベース状態
        Optional<PasswordResetToken> token = tokenRepository
            .findByEmail("test@example.com");
        assertThat(token).isPresent();
        assertThat(token.get().isValid()).isTrue();
        assertThat(token.get().getExpiresAt())
            .isAfter(LocalDateTime.now());
    }
    
    @Test
    void testPasswordResetRequest_UnknownEmail() {
        // Given
        PasswordResetRequestDto request = new PasswordResetRequestDto("unknown@example.com");
        
        // When
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
            "/api/auth/password-reset/request",
            request,
            ErrorResponse.class
        );
        
        // Then: 404エラー
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage())
            .contains("ユーザーが見つかりません");
    }
    
    @Test
    void testPasswordResetConfirm_Success() {
        // Given: 有効なリセットトークンを作成
        String email = "test@example.com";
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1));
        tokenRepository.save(resetToken);
        
        PasswordResetConfirmDto request = new PasswordResetConfirmDto(
            token,
            "NewSecurePassword123!"
        );
        
        // When
        ResponseEntity<MessageResponse> response = restTemplate.postForEntity(
            "/api/auth/password-reset/confirm",
            request,
            MessageResponse.class
        );
        
        // Then: HTTPステータスコード
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Then: パスワードが更新されている
        User user = userRepository.findByEmail(email).get();
        assertThat(passwordEncoder.matches("NewSecurePassword123!", user.getPassword()))
            .isTrue();
        
        // Then: トークンが無効化されている
        PasswordResetToken usedToken = tokenRepository.findByToken(token).get();
        assertThat(usedToken.isUsed()).isTrue();
    }
    
    @Test
    void testPasswordResetConfirm_ExpiredToken() {
        // Given: 期限切れトークン
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail("test@example.com");
        resetToken.setToken(token);
        resetToken.setExpiresAt(LocalDateTime.now().minusHours(1));  // 期限切れ
        tokenRepository.save(resetToken);
        
        PasswordResetConfirmDto request = new PasswordResetConfirmDto(
            token,
            "NewPassword123!"
        );
        
        // When
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
            "/api/auth/password-reset/confirm",
            request,
            ErrorResponse.class
        );
        
        // Then: 400エラー
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage())
            .contains("トークンの有効期限が切れています");
    }
}
```

**コンテナ化の必要性**: 🔴 必須
- データベース操作（`password_reset_tokens`テーブル）
- トランザクションの検証
- メール送信サービスとの連携（MailHog使用）

---

### 4.4.7 実装チェックリスト

#### **統合テスト計画** (5-10分)
- [ ] テスト対象のAPI endpointsをリストアップした
- [ ] PBIタイプに応じた必須/推奨テストを確認した
- [ ] コンテナ化の必要性を判断した（データベース依存、外部サービス依存）
- [ ] テストツールを選択した（REST Assured, Supertest, pytest等）
- [ ] TestContainersまたはDocker Composeを選択した

#### **APIレベルテスト実装** (30-45分)
- [ ] **正常系テスト**を実装した
  - HTTPステータスコード200/201の検証
  - レスポンスボディの検証
  - データベース状態の検証
- [ ] **異常系テスト**を実装した
  - 400 Bad Request（バリデーションエラー）
  - 401 Unauthorized（認証エラー）
  - 404 Not Found（リソース不在）
  - 500 Internal Server Error（システムエラー）
- [ ] レスポンスヘッダーの検証を実装した
- [ ] トランザクションの検証を実装した
- [ ] エラーメッセージの検証を実装した

#### **コンテナ化テスト実装** (10-20分)
- [ ] TestContainersまたはDocker Composeを設定した
- [ ] データベースコンテナを起動できることを確認した
- [ ] 外部サービスコンテナを起動できることを確認した（該当する場合）
- [ ] マイグレーション/シードデータのセットアップを実装した
- [ ] テスト完了後のクリーンアップを実装した

#### **テスト実行** (10-15分)
- [ ] ローカル環境ですべてのテストがパスした
- [ ] CI/CD環境でテストが実行できることを確認した
- [ ] テスト実行時間が許容範囲内である（<5分推奨）
- [ ] テストカバレッジが目標値を達成している
- [ ] テストが安定している（フレーキーテストがない）

---

### 4.4.8 トラブルシューティング

#### **問題: TestContainersが起動しない**
```yaml
症状:
  - org.testcontainers.containers.ContainerLaunchException
  - Could not start container

解決策:
  1. Docker Desktopが起動しているか確認
  2. Docker Daemonにアクセスできるか確認
     - Linux: sudo usermod -aG docker $USER && newgrp docker
     - Windows/Mac: Docker Desktop設定 > Resources > WSL integration
  3. ポートが既に使用されていないか確認
     - lsof -i :5432 (PostgreSQL)
     - lsof -i :6379 (Redis)
  4. Docker Desktopのリソース制限を確認
     - メモリ: 4GB以上推奨
     - CPU: 2コア以上推奨
```

#### **問題: テスト実行が遅い**
```yaml
症状:
  - テスト実行に10分以上かかる
  - CI/CDタイムアウト

解決策:
  1. コンテナの再利用を有効化
     - @Container(reusable = true)
     - Testcontainers.exposeHostPorts()
  2. 並列実行を有効化
     - JUnit: @Execution(ExecutionMode.CONCURRENT)
     - pytest: pytest -n auto
  3. 不要なデータセットアップを削減
     - @BeforeEach → @BeforeAll
     - トランザクションロールバック活用
  4. イメージのプルを最適化
     - ローカルキャッシュ活用
     - CI環境でのイメージキャッシュ設定
```

#### **問題: CI/CD環境でテストが失敗する**
```yaml
症状:
  - ローカルでは成功するがCI/CDで失敗
  - タイムアウトエラー

解決策:
  1. CI環境でDockerが利用可能か確認
     - GitHub Actions: services設定またはdocker-compose使用
     - GitLab CI: docker:dind使用
  2. タイムアウト設定を調整
     - TestContainers: .withStartupTimeout(Duration.ofMinutes(5))
     - CI設定: timeout: 10m
  3. ログを詳細化して原因を特定
     - Testcontainers.logsConsumer(new Slf4jLogConsumer(log))
  4. ネットワーク設定を確認
     - ファイアウォール設定
     - プロキシ設定
```

#### **問題: データベース状態が期待と異なる**
```yaml
症状:
  - データが保存されていない
  - トランザクションが不正

解決策:
  1. トランザクション設定を確認
     - @Transactional(propagation = Propagation.NOT_SUPPORTED)
     - テストクラスでトランザクション無効化
  2. データベース接続を確認
     - 接続プールサイズ
     - タイムアウト設定
  3. マイグレーション/シードデータを確認
     - Flywayマイグレーション実行確認
     - テストデータの投入順序確認
```

---

**🔴 必須参照**:
- [`04-quality-standards/integration-testing.md`](../../04-quality-standards/integration-testing.md)
  - **統合テストの詳細実装方法**
- [`03-development-process/testing-standards.md`](../../03-development-process/testing-standards.md)
  - **PBIタイプ別テスト要件マトリックス**
- [`05-technology-stack/container-standards.md`](../../05-technology-stack/container-standards.md)
  - **Dockerコンテナ標準**

**🟡 推奨参照**:
- [`04-quality-standards/test-data-management.md`](../../04-quality-standards/test-data-management.md)
  - **テストデータ管理方法**

---

**統合完了チェック**:
- [ ] Step 4.4セクションを本ドキュメント内容で置き換えた
- [ ] 既存のチェックリストと矛盾がないか確認した
- [ ] 参照ドキュメントへのリンクが正しいか確認した
