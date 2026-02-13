# Phase 4追加セクション: マルチリポジトリ環境での統合テスト

> **追加先**: `phase-4-integration-test-addition.md`  
> **挿入位置**: Section 4.4.6（EC-15の具体例）の後

---

## 4.4.9 マルチリポジトリ環境での統合テスト 🆕

### 概要

マイクロサービスアーキテクチャやモジュラーモノリスなど、複数のリポジトリに分かれたプロジェクトでは、統合テストの範囲とPBI計画が重要です。

---

### 基本原則

#### **原則1: 単一リポジトリ内のテストは必須**

**定義**: 同一リポジトリ内のすべてのコンポーネント間の統合テスト

**実施要件**: 🔴 **必須** - すべてのPBIで実施

**対象範囲**:
```yaml
✅ 必須テスト範囲（単一リポジトリ内）:
  - レイヤー間の統合（Controller → Service → Repository）
  - データベース操作（PostgreSQL, MySQL, MongoDB等）
  - キャッシュ操作（Redis, Memcached等）
  - メッセージキュー操作（RabbitMQ, Kafka等）
  - ファイルストレージ（S3, GCS等）
  - 認証サービス（Auth0, Keycloak等）

実施方法:
  - TestContainers推奨
  - Phase 4で実施
  - 各PBIで完結
```

---

#### **原則2: 複数リポジトリに渡るテストは別PBIで実施**

**定義**: 異なるリポジトリのサービス間の統合テスト

**実施要件**: 🔴 **必須** - ただし、**別PBIとして計画・実施**

**対象範囲**:
```yaml
✅ 別PBIで実施する範囲:
  - マイクロサービス間のAPI呼び出し
  - イベント駆動アーキテクチャでのメッセージング
  - フロントエンド ↔ バックエンドの統合
  - BFF（Backend for Frontend）パターン
  - エンドツーエンドのユーザーフロー

実施方法:
  - 専用テストリポジトリ作成
  - Docker Compose必須
  - 別PBIとして計画
```

---

### PBI分割の具体例

#### **例: ユーザー登録機能（マイクロサービス構成）**

**システム構成**:
- リポジトリ1: `user-service` (Spring Boot)
- リポジトリ2: `notification-service` (Node.js)
- リポジトリ3: `frontend` (React)
- 共通インフラ: Kafka, PostgreSQL

**PBI計画**:

##### **PBI-1: ユーザー登録API実装（user-service）**

**担当チーム**: バックエンドチーム

**テスト範囲**:
```yaml
単一リポジトリ内の統合テスト: ✅ 必須（このPBIで実施）
  - POST /api/users/register
  - GET /api/users/{id}
  - データベース操作（usersテーブル）
  - Kafkaイベント発行（user.registered）

複数リポジトリ間のテスト: ⚪ このPBIでは実施しない
  - notification-serviceとの連携 → PBI-3で実施
  - frontendとの連携 → PBI-5で実施
```

**TestContainers実装**:
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserRegistrationApiTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Container
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
    );
    
    @Test
    void testUserRegistration_PublishesEventToKafka() {
        // Given
        RegisterUserRequest request = new RegisterUserRequest(
            "newuser@example.com",
            "SecurePassword123!",
            "New User"
        );
        
        // Kafkaコンシューマーを設定
        Consumer<String, String> consumer = createKafkaConsumer();
        consumer.subscribe(Collections.singletonList("user.registered"));
        
        // When: ユーザー登録API呼び出し
        ResponseEntity<UserResponse> response = restTemplate.postForEntity(
            "/api/users/register",
            request,
            UserResponse.class
        );
        
        // Then: API成功
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        // Then: DBにユーザー保存
        User savedUser = userRepository.findByEmail("newuser@example.com").get();
        assertThat(savedUser).isNotNull();
        
        // Then: Kafkaイベント発行（単一リポジトリの責任範囲）
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
        assertThat(records).hasSize(1);
        
        ConsumerRecord<String, String> record = records.iterator().next();
        assertThat(record.topic()).isEqualTo("user.registered");
        
        // イベント内容の検証
        JsonNode event = objectMapper.readTree(record.value());
        assertThat(event.get("userId").asText()).isEqualTo(savedUser.getId().toString());
        assertThat(event.get("email").asText()).isEqualTo("newuser@example.com");
    }
}
```

**完了条件**:
- [x] 単一リポジトリ内のすべてのテストがパス
- [x] Kafkaイベント発行を検証（他サービスの動作は検証しない）
- [x] PRマージ完了

---

##### **PBI-2: 通知サービス実装（notification-service）**

**担当チーム**: 通知チーム

**テスト範囲**:
```yaml
単一リポジトリ内の統合テスト: ✅ 必須（このPBIで実施）
  - Kafkaコンシューマー（user.registeredイベント受信）
  - メール送信処理
  - エラーハンドリング

複数リポジトリ間のテスト: ⚪ このPBIでは実施しない
  - user-serviceとの連携 → PBI-3で実施
```

**TestContainers実装（Node.js）**:
```javascript
import { GenericContainer } from 'testcontainers';
import { Kafka } from 'kafkajs';

describe('User Registration Notification', () => {
  let kafkaContainer;
  let mailhogContainer;
  let kafka;
  
  beforeAll(async () => {
    // Kafkaコンテナ起動
    kafkaContainer = await new GenericContainer('confluentinc/cp-kafka:7.5.0')
      .withEnvironment({
        KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181',
        KAFKA_ADVERTISED_LISTENERS: 'PLAINTEXT://localhost:9093'
      })
      .withExposedPorts(9093)
      .start();
    
    // MailHogコンテナ起動
    mailhogContainer = await new GenericContainer('mailhog/mailhog:latest')
      .withExposedPorts(1025, 8025)
      .start();
    
    // Kafka設定
    kafka = new Kafka({
      clientId: 'notification-service-test',
      brokers: [`${kafkaContainer.getHost()}:${kafkaContainer.getMappedPort(9093)}`]
    });
  });
  
  test('should send welcome email when user.registered event received', async () => {
    // Given: user.registeredイベントをKafkaに投稿
    const producer = kafka.producer();
    await producer.connect();
    
    await producer.send({
      topic: 'user.registered',
      messages: [{
        value: JSON.stringify({
          userId: 'user-123',
          email: 'newuser@example.com',
          name: 'New User',
          timestamp: new Date().toISOString()
        })
      }]
    });
    
    await producer.disconnect();
    
    // When: 通知サービスがイベントを処理（最大10秒待機）
    await sleep(5000);
    
    // Then: MailHogでメール送信を確認（単一リポジトリの責任範囲）
    const mailhogURL = `http://${mailhogContainer.getHost()}:${mailhogContainer.getMappedPort(8025)}`;
    const response = await fetch(`${mailhogURL}/api/v2/messages`);
    const messages = await response.json();
    
    expect(messages.items).toHaveLength(1);
    
    const mail = messages.items[0];
    expect(mail.Content.Headers.To[0]).toBe('newuser@example.com');
    expect(mail.Content.Headers.Subject[0]).toContain('Welcome');
    expect(mail.Content.Body).toContain('New User');
  });
});
```

**完了条件**:
- [x] 単一リポジトリ内のすべてのテストがパス
- [x] Kafkaイベント受信・処理を検証
- [x] メール送信を検証
- [x] PRマージ完了

---

##### **PBI-3: サービス間統合テスト（user-service ↔ notification-service）**

**担当チーム**: QAチーム or 統合チーム

**前提条件**: PBI-1, PBI-2完了後

**テスト範囲**:
```yaml
複数リポジトリ間の統合テスト: ✅ 必須（このPBIで実施）
  - user-service → Kafka → notification-service
  - エンドツーエンドのイベントフロー
  - エラーケース（Kafka停止、メール送信失敗等）

実施場所:
  - 専用テストリポジトリ: e2e-tests/
  - Docker Compose使用
```

**Docker Compose構成**:
```yaml
# e2e-tests/docker-compose.user-notification.yml
version: '3.8'

services:
  user-service:
    image: user-service:${VERSION:-latest}
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/userdb
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    depends_on:
      postgres:
        condition: service_healthy
      kafka:
        condition: service_started
  
  notification-service:
    image: notification-service:${VERSION:-latest}
    environment:
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      MAIL_HOST: mailhog
      MAIL_PORT: 1025
    depends_on:
      - kafka
      - mailhog
  
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: userdb
      POSTGRES_USER: test
      POSTGRES_PASSWORD: test
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U test"]
      interval: 5s
      timeout: 5s
      retries: 5
  
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    depends_on:
      - zookeeper
  
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
  
  mailhog:
    image: mailhog/mailhog:latest
    ports:
      - "8025:8025"
```

**統合テスト実装**:
```javascript
// e2e-tests/user-notification-integration.test.js
describe('User Registration Integration (user-service → notification-service)', () => {
  const userServiceURL = 'http://localhost:8080';
  const mailhogURL = 'http://localhost:8025';
  
  beforeAll(async () => {
    // すべてのサービスが起動するまで待機
    await waitForService(userServiceURL, { timeout: 30000 });
  });
  
  test('complete flow: user registration triggers welcome email', async () => {
    // Given: 新規ユーザー情報
    const newUser = {
      email: 'e2etest@example.com',
      password: 'SecurePassword123!',
      name: 'E2E Test User'
    };
    
    // When: user-serviceでユーザー登録
    const registerResponse = await fetch(`${userServiceURL}/api/users/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(newUser)
    });
    
    // Then: user-serviceがユーザーを作成
    expect(registerResponse.status).toBe(201);
    const user = await registerResponse.json();
    expect(user.email).toBe('e2etest@example.com');
    
    // Then: notification-serviceがウェルカムメールを送信（最大10秒待機）
    const mail = await waitForMail(mailhogURL, 'e2etest@example.com', {
      timeout: 10000,
      interval: 1000
    });
    
    expect(mail).toBeDefined();
    expect(mail.Content.Headers.To[0]).toBe('e2etest@example.com');
    expect(mail.Content.Headers.Subject[0]).toContain('Welcome');
    expect(mail.Content.Body).toContain('E2E Test User');
  });
  
  test('error handling: invalid email does not send notification', async () => {
    // Given: 無効なユーザー情報
    const invalidUser = {
      email: 'invalid-email',
      password: 'SecurePassword123!',
      name: 'Invalid User'
    };
    
    // When: user-serviceでユーザー登録（失敗するはず）
    const registerResponse = await fetch(`${userServiceURL}/api/users/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(invalidUser)
    });
    
    // Then: user-serviceがバリデーションエラーを返す
    expect(registerResponse.status).toBe(400);
    
    // Then: notification-serviceはメールを送信しない
    await sleep(5000);
    
    const mailResponse = await fetch(`${mailhogURL}/api/v2/messages`);
    const mails = await mailResponse.json();
    
    const invalidMail = mails.items.find(m => 
      m.Content.Headers.To[0] === 'invalid-email'
    );
    
    expect(invalidMail).toBeUndefined();
  });
});

// ヘルパー関数
async function waitForMail(mailhogURL, recipientEmail, options = {}) {
  const { timeout = 10000, interval = 1000 } = options;
  const startTime = Date.now();
  
  while (Date.now() - startTime < timeout) {
    const response = await fetch(`${mailhogURL}/api/v2/messages`);
    const mails = await response.json();
    
    const mail = mails.items.find(m => 
      m.Content.Headers.To[0] === recipientEmail
    );
    
    if (mail) {
      return mail;
    }
    
    await sleep(interval);
  }
  
  throw new Error(`Mail to ${recipientEmail} not received within ${timeout}ms`);
}
```

**実行スクリプト**:
```bash
#!/bin/bash
# e2e-tests/run-user-notification-integration.sh

set -e

VERSION=${1:-latest}

echo "🚀 Starting integration test environment..."
VERSION=$VERSION docker-compose -f docker-compose.user-notification.yml up -d

echo "⏳ Waiting for services to be ready..."
./wait-for-services.sh

echo "🧪 Running integration tests..."
npm test -- user-notification-integration.test.js

TEST_EXIT_CODE=$?

echo "🧹 Cleaning up..."
docker-compose -f docker-compose.user-notification.yml down -v

if [ $TEST_EXIT_CODE -eq 0 ]; then
  echo "✅ Integration tests passed!"
  exit 0
else
  echo "❌ Integration tests failed!"
  exit $TEST_EXIT_CODE
fi
```

**完了条件**:
- [x] Docker Compose環境構築完了
- [x] サービス間統合テストがすべてパス
- [x] エラーケーステストがパス
- [x] テスト実行スクリプト作成完了
- [x] テストドキュメント作成完了

---

### チェックリスト: マルチリポジトリ環境

#### **各サービスのPBI（単一リポジトリ内テスト）**
- [ ] ユニットテストを実装した
- [ ] 統合テストを実装した（TestContainers使用）
- [ ] API endpointsをテストした
- [ ] データベース操作をテストした
- [ ] メッセージキュー操作をテストした（該当する場合）
- [ ] **他サービスの動作は検証していない**（モック不要、イベント発行のみ確認）
- [ ] すべてのテストがパスした
- [ ] PRマージ完了

#### **サービス間統合PBI（複数リポジトリ間テスト）**
- [ ] 前提となるサービスPBIがすべて完了している
- [ ] 専用テストリポジトリを作成した（または既存を使用）
- [ ] Docker Compose構成を作成した
- [ ] すべてのサービスをコンテナで起動できる
- [ ] サービス間統合テストを実装した
- [ ] エンドツーエンドのフローをテストした
- [ ] エラーケースをテストした
- [ ] テスト実行スクリプトを作成した
- [ ] テストドキュメントを作成した
- [ ] すべてのテストがパスした

---

### ベストプラクティス

#### ✅ 推奨

1. **各サービスは独立してテスト可能にする**
   - 単一リポジトリ内のテストで完結
   - 他サービスのモック不要
   - 並行開発を促進

2. **段階的に統合する**
   - サービスA → サービスB
   - サービスB → サービスC
   - 完全統合（A → B → C）

3. **専用テストリポジトリを使用**
   - e2e-tests/ リポジトリ
   - Docker Compose構成を集約
   - テストスクリプトを一元管理

#### ❌ 非推奨

1. **すべてを1つのPBIにまとめる**
   - PBIが巨大すぎる
   - 並行作業不可能
   - リスクが高い

2. **統合テストを後回しにする**
   - 統合時に大量のバグ
   - 手戻りが大きい

3. **他サービスをモックする（単一リポジトリ内テストで）**
   - モックメンテナンスコスト
   - 実際の動作との乖離
   - 統合テストで検証する方が効率的

---

**関連ドキュメント**:
- [MULTI-REPOSITORY-TESTING-GUIDELINES.md](../../00-guides/MULTI-REPOSITORY-TESTING-GUIDELINES.md) - 詳細ガイドライン
