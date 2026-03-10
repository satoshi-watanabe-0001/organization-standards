# 統合テスト CI テンプレート

## 📋 目次
- [概要](#概要)
- [機能](#機能)
- [セットアップ方法](#セットアップ方法)
- [設定内容の詳細](#設定内容の詳細)
- [トラブルシューティング](#トラブルシューティング)
- [カスタマイズ](#カスタマイズ)

---

## 概要

**ファイル**: `integration-test.yaml`

このテンプレートは、プロジェクトの統合テストを自動実行するCIワークフローです。

### 目的
- データベースやAPIを含む実環境に近いテストを実施
- コンポーネント間の連携を検証
- E2E（End-to-End）シナリオのテスト
- リグレッション（機能退行）の防止

### 対応言語
- ✅ Java（Maven/Gradle）
- ✅ Node.js（npm/yarn）
- ✅ Python

---

## 機能

### 1. Java統合テスト（GitHub Services）

**テスト内容**:
- データベース連携テスト（PostgreSQL/MySQL）
- REST APIテスト
- トランザクション処理のテスト
- キャッシュ連携テスト（Redis）

**使用技術**:
- JUnit 5
- Spring Boot Test
- GitHub Actions Services（Docker）

---

### 2. TestContainers テスト

**テスト内容**:
- Dockerコンテナを使用した統合テスト
- データベース、メッセージキュー、その他のミドルウェアのテスト
- 本番環境と同等の環境でのテスト

**メリット**:
- 実際のデータベース/サービスを使用
- 環境の一貫性
- 分離されたテスト環境

---

### 3. APIテスト（REST Assured）

**テスト内容**:
- REST APIのエンドポイントテスト
- HTTPステータスコードの検証
- レスポンスボディの検証
- 認証・認可のテスト

**使用技術**:
- REST Assured
- Spring Boot Test
- JUnit 5

---

### 4. Node.js統合テスト

**テスト内容**:
- Express/Fastify API テスト
- データベース連携テスト
- 外部APIモックテスト

**使用技術**:
- Jest / Mocha
- Supertest
- PostgreSQL Test Database

---

### 5. Python統合テスト

**テスト内容**:
- Flask/FastAPI テスト
- SQLAlchemy データベーステスト
- 非同期処理のテスト

**使用技術**:
- pytest
- pytest-asyncio
- PostgreSQL Test Database

---

## セットアップ方法

### Step 1: テンプレートをプロジェクトにコピー

```bash
# プロジェクトのルートディレクトリに移動
cd <project-root>

# .github/workflows/ ディレクトリを作成（存在しない場合）
mkdir -p .github/workflows

# テンプレートをコピー
cp /path/to/devin-organization-standards/08-templates/ci-templates/github-actions/integration-test.yaml \
   .github/workflows/integration-test.yaml
```

---

### Step 2: 言語別の追加設定

#### Java プロジェクト（Maven）

**pom.xml** に以下を追加：

```xml
<profiles>
  <!-- 統合テスト用プロファイル -->
  <profile>
    <id>integration-test</id>
    <build>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-failsafe-plugin</artifactId>
          <version>3.2.5</version>
          <executions>
            <execution>
              <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
              </goals>
            </execution>
          </executions>
        </plugin>
      </plugins>
    </build>
  </profile>

  <!-- TestContainers用プロファイル -->
  <profile>
    <id>testcontainers</id>
    <dependencies>
      <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>testcontainers</artifactId>
        <version>1.19.3</version>
        <scope>test</scope>
      </dependency>
      <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <version>1.19.3</version>
        <scope>test</scope>
      </dependency>
    </dependencies>
  </profile>

  <!-- APIテスト用プロファイル -->
  <profile>
    <id>api-test</id>
    <dependencies>
      <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <version>5.4.0</version>
        <scope>test</scope>
      </dependency>
    </dependencies>
  </profile>
</profiles>
```

**統合テストクラス例**（`src/test/java`）:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreateUser() {
        UserDto user = new UserDto("test@example.com", "Test User");
        
        ResponseEntity<UserDto> response = restTemplate.postForEntity(
            "/api/users", user, UserDto.class);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
    }
}
```

---

#### Java プロジェクト（Gradle）

**build.gradle** に以下を追加：

```gradle
// 統合テスト用ソースセット
sourceSets {
    integrationTest {
        java {
            compileClasspath += main.output + test.output
            runtimeClasspath += main.output + test.output
            srcDir file('src/integration-test/java')
        }
        resources.srcDir file('src/integration-test/resources')
    }
}

configurations {
    integrationTestImplementation.extendsFrom testImplementation
    integrationTestRuntimeOnly.extendsFrom testRuntimeOnly
}

// 統合テストタスク
task integrationTest(type: Test) {
    description = 'Runs integration tests.'
    group = 'verification'
    
    testClassesDirs = sourceSets.integrationTest.output.classesDirs
    classpath = sourceSets.integrationTest.runtimeClasspath
    shouldRunAfter test
}

// TestContainers
dependencies {
    integrationTestImplementation 'org.testcontainers:testcontainers:1.19.3'
    integrationTestImplementation 'org.testcontainers:postgresql:1.19.3'
    
    // REST Assured
    integrationTestImplementation 'io.rest-assured:rest-assured:5.4.0'
}
```

---

#### Node.js プロジェクト

**package.json** に以下を追加：

```json
{
  "scripts": {
    "test:integration": "jest --testPathPattern=tests/integration --runInBand"
  },
  "devDependencies": {
    "jest": "^29.0.0",
    "supertest": "^6.3.0",
    "@types/jest": "^29.0.0",
    "@types/supertest": "^6.0.0"
  }
}
```

**統合テストファイル例**（`tests/integration/user.test.js`）:

```javascript
const request = require('supertest');
const app = require('../../src/app');

describe('User API Integration Tests', () => {
  test('POST /api/users - create user', async () => {
    const response = await request(app)
      .post('/api/users')
      .send({
        email: 'test@example.com',
        name: 'Test User'
      });
    
    expect(response.status).toBe(201);
    expect(response.body).toHaveProperty('id');
    expect(response.body.email).toBe('test@example.com');
  });
});
```

---

#### Python プロジェクト

**pytest.ini** をプロジェクトルートに作成：

```ini
[pytest]
testpaths = tests/integration
python_files = test_*.py
python_classes = Test*
python_functions = test_*
asyncio_mode = auto
```

**統合テストファイル例**（`tests/integration/test_user.py`）:

```python
import pytest
from httpx import AsyncClient
from app.main import app

@pytest.mark.asyncio
async def test_create_user():
    async with AsyncClient(app=app, base_url="http://test") as client:
        response = await client.post(
            "/api/users",
            json={"email": "test@example.com", "name": "Test User"}
        )
    
    assert response.status_code == 201
    assert "id" in response.json()
    assert response.json()["email"] == "test@example.com"
```

---

### Step 3: コミット＆プッシュ

```bash
# ファイルをステージング
git add .github/workflows/integration-test.yaml
git add pom.xml  # または build.gradle、package.json、pytest.ini

# 統合テストファイルも追加
git add src/test/java  # Java
git add tests/integration/  # Node.js, Python

# コミット
git commit -m "feat: 統合テストCIを追加

- データベース連携テスト（PostgreSQL）
- TestContainers統合テスト
- REST API テスト（REST Assured）
- Node.js/Python 統合テスト

参照: devin-organization-standards/08-templates/ci-templates/github-actions/integration-test.yaml"

# プッシュ
git push origin main
```

---

### Step 4: ブランチ保護ルールの設定（推奨）

**設定手順**:
1. GitHubリポジトリ → Settings → Branches
2. Branch protection rule を追加
   - Branch name pattern: `main` または `develop`
3. 必須ステータスチェックを有効化：
   - ✅ Require status checks to pass before merging
   - ステータスチェック選択: 
     - `統合テスト (Java)` または該当する統合テスト

---

## 設定内容の詳細

### 実行タイミング

```yaml
on:
  pull_request:
    branches: [main, develop]  # PR作成時
  push:
    branches: [main, develop]  # プッシュ時
  workflow_dispatch:            # 手動実行
```

### 実行時間

| ジョブ | 実行時間 |
|-------|---------|
| Java統合テスト | 5-8分 |
| TestContainers | 8-12分 |
| APIテスト | 5-8分 |
| Node.js統合テスト | 3-5分 |
| Python統合テスト | 3-5分 |
| **合計** | **10-15分** |

### GitHub Services

テンプレートは、GitHub Actions Servicesを使用してデータベース等を起動します：

```yaml
services:
  postgres:
    image: postgres:15-alpine
    env:
      POSTGRES_DB: testdb
      POSTGRES_USER: testuser
      POSTGRES_PASSWORD: testpass
    options: >-
      --health-cmd pg_isready
      --health-interval 10s
      --health-timeout 5s
      --health-retries 5
    ports:
      - 5432:5432
```

---

## トラブルシューティング

### 問題1: データベース接続エラー

**エラーメッセージ**:
```
Connection refused: connect
```

**原因**: データベースサービスが起動していない、またはポート設定が間違っている

**対処方法**:

#### 1. ヘルスチェックを確認

```yaml
services:
  postgres:
    options: >-
      --health-cmd pg_isready
      --health-interval 10s
      --health-timeout 5s
      --health-retries 5
```

#### 2. 接続URLを確認

```yaml
env:
  SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/testdb
  SPRING_DATASOURCE_USERNAME: testuser
  SPRING_DATASOURCE_PASSWORD: testpass
```

---

### 問題2: テストがタイムアウトする

**原因**: テスト実行時間が長すぎる

**対処方法**:

#### 1. タイムアウト値を増やす

```yaml
- name: Run Integration Tests
  timeout-minutes: 15  # デフォルトは5分
  run: mvn verify -P integration-test
```

#### 2. テストを並列実行

```xml
<!-- Maven Surefire Plugin -->
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <configuration>
    <parallel>methods</parallel>
    <threadCount>4</threadCount>
  </configuration>
</plugin>
```

---

### 問題3: TestContainersが起動しない

**エラーメッセージ**:
```
Could not start container
```

**原因**: Docker環境が適切に設定されていない

**対処方法**:

#### 1. Docker Buildxを有効化

```yaml
- name: Set up Docker Buildx
  uses: docker/setup-buildx-action@v3
```

#### 2. TestContainersの設定を確認

```java
@Testcontainers
@SpringBootTest
public class UserRepositoryTest {

    @Container
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("testdb")
        .withUsername("testuser")
        .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

---

### 問題4: APIテストが失敗する

**原因**: アプリケーションが起動していない、またはエンドポイントが変更された

**対処方法**:

#### 1. アプリケーション起動を確認

```bash
# ローカルでアプリケーションを起動
java -jar target/*.jar

# ヘルスチェック
curl http://localhost:8080/actuator/health
```

#### 2. エンドポイントを確認

```java
@Test
public void testGetUsers() {
    // エンドポイントが正しいか確認
    given()
        .when()
        .get("/api/users")
        .then()
        .statusCode(200);
}
```

---

## カスタマイズ

### データベースの変更

#### PostgreSQL → MySQL

```yaml
services:
  mysql:
    image: mysql:8.0
    env:
      MYSQL_ROOT_PASSWORD: rootpass
      MYSQL_DATABASE: testdb
      MYSQL_USER: testuser
      MYSQL_PASSWORD: testpass
    options: >-
      --health-cmd="mysqladmin ping"
      --health-interval=10s
      --health-timeout=5s
      --health-retries=5
    ports:
      - 3306:3306
```

```yaml
env:
  SPRING_DATASOURCE_URL: jdbc:mysql://localhost:3306/testdb
  SPRING_DATASOURCE_USERNAME: testuser
  SPRING_DATASOURCE_PASSWORD: testpass
```

---

### Redis を追加

```yaml
services:
  redis:
    image: redis:7-alpine
    options: >-
      --health-cmd "redis-cli ping"
      --health-interval 10s
      --health-timeout 5s
      --health-retries 5
    ports:
      - 6379:6379
```

```yaml
env:
  SPRING_REDIS_HOST: localhost
  SPRING_REDIS_PORT: 6379
```

---

### テストカバレッジの収集

```yaml
- name: Run Integration Tests with Coverage
  run: mvn verify -P integration-test -Djacoco.skip=false

- name: Upload Coverage Report
  uses: codecov/codecov-action@v4
  with:
    files: ./target/site/jacoco/jacoco.xml
    flags: integration-tests
```

---

## コスト見積もり

### CI実行時間とコスト

| 項目 | 実行時間 | 頻度 | 月間コスト（目安） |
|------|---------|------|------------------|
| PR時の統合テスト | 12分/回 | 50回/月 | $0（無料枠内） |
| **合計** | - | - | **$0** |

**注意**: GitHub Actions の無料枠は月2,000分です。

---

## ベストプラクティス

### 1. 単体テストと統合テストを分離

```
src/
├── main/
│   └── java/
└── test/
    ├── java/           # 単体テスト
    └── integration/    # 統合テスト
```

**理由**: 実行時間の短縮、テストの明確な分離。

---

### 2. TestContainersを活用

実際のデータベースを使用することで、より正確なテストが可能です。

**メリット**:
- 本番環境と同等のテスト
- モックでは検出できないバグの発見

---

### 3. テストデータの管理

```java
@BeforeEach
public void setUp() {
    // テストデータの初期化
    userRepository.deleteAll();
    userRepository.save(new User("test@example.com", "Test User"));
}

@AfterEach
public void tearDown() {
    // テストデータのクリーンアップ
    userRepository.deleteAll();
}
```

---

### 4. トランザクションのロールバック

```java
@Transactional
@Rollback
@Test
public void testCreateUser() {
    // テスト実行後、自動的にロールバック
    userService.createUser("test@example.com", "Test User");
    assertEquals(1, userRepository.count());
}
```

---

## 関連ドキュメント

### 組織標準
- [CI/CDギャップ分析](../../ci-gap-analysis.md)
- [Java CI構成ガイド](../../java-project-ci-structure.md)

### 外部リンク
- [TestContainers Documentation](https://www.testcontainers.org/)
- [REST Assured Documentation](https://rest-assured.io/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Jest Documentation](https://jestjs.io/)
- [Pytest Documentation](https://docs.pytest.org/)

---

**このドキュメントは組織標準の一部です。改善案は Issue で受け付けています。**
