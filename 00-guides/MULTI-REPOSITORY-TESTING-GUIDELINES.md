# マルチリポジトリ環境での統合テスト要件

**作成日**: 2025-11-07  
**目的**: 複数リポジトリに分かれたプロジェクトでの統合テスト実施要件を明確化  
**対象**: マイクロサービスアーキテクチャ、モジュラーモノリス、フロントエンド/バックエンド分離構成

---

## 📋 基本原則

### 原則1: 単一リポジトリ内のテストは必須

**定義**:
- 同一リポジトリ内のすべてのコンポーネント間の統合テスト
- データベース、キャッシュ、メッセージキュー等、リポジトリが直接依存する外部サービスとの統合テスト

**実施要件**: 🔴 **必須** - すべてのPBIで実施

**対象範囲**:
```yaml
単一リポジトリ内:
  ✅ レイヤー間の統合（Controller → Service → Repository）
  ✅ データベースとの統合（PostgreSQL, MySQL, MongoDB等）
  ✅ キャッシュとの統合（Redis, Memcached等）
  ✅ メッセージキューとの統合（RabbitMQ, Kafka等）
  ✅ ファイルストレージとの統合（S3, GCS等）
  ✅ 認証サービスとの統合（Auth0, Keycloak等）
  
実施方法:
  - TestContainers推奨
  - Docker Compose可
  - Phase 4で実施
```

---

### 原則2: 複数リポジトリに渡るテストはPBIを分けて実施

**定義**:
- 異なるリポジトリのサービス間の統合テスト
- エンドツーエンドのビジネスフロー検証

**実施要件**: 🔴 **必須** - ただし、**別PBIとして計画・実施**

**対象範囲**:
```yaml
複数リポジトリ間:
  ✅ マイクロサービス間のAPI呼び出し
  ✅ イベント駆動アーキテクチャでのメッセージング
  ✅ フロントエンド ↔ バックエンドの統合
  ✅ BFF（Backend for Frontend）パターンでの統合
  ✅ エンドツーエンドのユーザーフロー
  
実施方法:
  - 専用のテストリポジトリ作成
  - Docker Compose必須
  - 別PBIとして計画
```

---

## 🎯 PBIの分け方

### パターン1: 機能開発PBI + 統合テストPBI

#### **PBI-1: [機能名]の実装（単一リポジトリ内）**

**対象**: 単一リポジトリでの機能実装

**テスト要件**:
```yaml
ユニットテスト: 🔴 必須
  - 対象: 新規ビジネスロジック
  - カバレッジ: 75%以上

統合テスト: 🔴 必須（単一リポジトリ内のみ）
  - 対象: 新規API endpoints
  - 対象: データベース操作
  - 対象: キャッシュ操作
  - 実施方法: TestContainers
  
E2Eテスト: ⚪ なし（この段階では実施しない）
  - 複数リポジトリ間のテストは次のPBIで実施
```

**完了条件**:
- [ ] 単一リポジトリ内のすべてのテストがパス
- [ ] カバレッジ目標達成
- [ ] PRマージ完了

---

#### **PBI-2: [機能名]のE2E統合テスト（複数リポジトリ間）**

**前提条件**: PBI-1完了後

**対象**: 複数リポジトリ間の統合検証

**テスト要件**:
```yaml
統合テスト: 🔴 必須（複数リポジトリ間）
  - 対象: サービス間API呼び出し
  - 対象: イベントメッセージング
  - 実施方法: Docker Compose必須
  - テストコード保存: 専用テストリポジトリ
  
E2Eテスト: 🔴 必須
  - 対象: エンドツーエンドのユーザーフロー
  - 実施方法: Playwright, Cypress等
```

**テスト環境構成例**:
```yaml
# docker-compose.e2e.yml
version: '3.8'
services:
  # リポジトリ1: ユーザーサービス
  user-service:
    image: user-service:latest
    ports:
      - "8081:8080"
    depends_on:
      - user-db
  
  # リポジトリ2: 注文サービス
  order-service:
    image: order-service:latest
    ports:
      - "8082:8080"
    depends_on:
      - order-db
      - kafka
  
  # リポジトリ3: フロントエンド
  frontend:
    image: frontend:latest
    ports:
      - "3000:3000"
    environment:
      USER_SERVICE_URL: http://user-service:8080
      ORDER_SERVICE_URL: http://order-service:8080
  
  # 共通インフラ
  user-db:
    image: postgres:15
  order-db:
    image: postgres:15
  kafka:
    image: confluentinc/cp-kafka:latest
```

**完了条件**:
- [ ] すべてのサービス間統合テストがパス
- [ ] E2Eテストがパス
- [ ] テストドキュメント作成完了

---

## 📊 具体例

### 例1: EC-15（パスワードリセット機能） - 単一リポジトリケース

**構成**: 単一リポジトリ（バックエンドのみ）

#### **PBI-1: EC-15 パスワードリセットAPI実装**

**テスト範囲**:
```yaml
単一リポジトリ内の統合テスト: ✅ 必須
  API endpoints:
    - POST /api/auth/password-reset/request
    - POST /api/auth/password-reset/confirm
  
  データベース:
    - password_reset_tokensテーブル操作
    - usersテーブル更新
  
  外部サービス（リポジトリの直接依存）:
    - メール送信サービス（MailHogでモック）
  
  実施方法:
    - TestContainers（PostgreSQL + MailHog）
    - Phase 4で実施

複数リポジトリ間のテスト: ⚪ なし
  - バックエンドのみのため不要
```

**テスト実装**:
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class PasswordResetApiIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Container
    static GenericContainer<?> mailhog = new GenericContainer<>("mailhog/mailhog:latest");
    
    @Test
    void testPasswordResetRequest_Success() {
        // 単一リポジトリ内の完全な統合テスト
    }
}
```

---

### 例2: ユーザー登録機能 - マルチリポジトリケース

**構成**: 
- リポジトリ1: フロントエンド（React）
- リポジトリ2: バックエンド（Spring Boot）
- リポジトリ3: 通知サービス（Node.js）

#### **PBI-1: ユーザー登録API実装（バックエンド）**

**テスト範囲**:
```yaml
単一リポジトリ内の統合テスト: ✅ 必須
  API endpoints:
    - POST /api/users/register
    - GET /api/users/{id}
  
  データベース:
    - usersテーブル操作
  
  メッセージキュー（リポジトリの直接依存）:
    - Kafka: user.registeredイベント発行
  
  実施方法:
    - TestContainers（PostgreSQL + Kafka）
    - Phase 4で実施

複数リポジトリ間のテスト: ⚪ この段階では実施しない
  - 通知サービスとの統合 → PBI-3で実施
  - フロントエンドとの統合 → PBI-4で実施
```

**テスト実装**:
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserRegistrationApiIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Container
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
    );
    
    @Test
    void testUserRegistration_Success() {
        // Given
        RegisterUserRequest request = new RegisterUserRequest(
            "newuser@example.com",
            "SecurePassword123!",
            "New User"
        );
        
        // When
        ResponseEntity<UserResponse> response = restTemplate.postForEntity(
            "/api/users/register",
            request,
            UserResponse.class
        );
        
        // Then: HTTPステータスコード
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        // Then: データベース状態
        User savedUser = userRepository.findByEmail("newuser@example.com").get();
        assertThat(savedUser).isNotNull();
        
        // Then: Kafkaイベント発行（単一リポジトリの責任範囲）
        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(5));
        assertThat(records).hasSize(1);
        assertThat(records.iterator().next().topic()).isEqualTo("user.registered");
    }
}
```

---

#### **PBI-2: 通知サービスの実装（通知サービス）**

**テスト範囲**:
```yaml
単一リポジトリ内の統合テスト: ✅ 必須
  Kafkaコンシューマー:
    - user.registeredイベント受信
  
  メール送信:
    - ウェルカムメール送信
  
  実施方法:
    - TestContainers（Kafka + MailHog）
    - Phase 4で実施

複数リポジトリ間のテスト: ⚪ この段階では実施しない
  - バックエンドとの統合 → PBI-3で実施
```

**テスト実装**:
```javascript
// 通知サービス（Node.js）のテスト
describe('User Registration Notification', () => {
  let kafkaContainer;
  let mailhogContainer;
  
  beforeAll(async () => {
    kafkaContainer = await new GenericContainer('confluentinc/cp-kafka:7.5.0')
      .withExposedPorts(9093)
      .start();
    
    mailhogContainer = await new GenericContainer('mailhog/mailhog:latest')
      .withExposedPorts(1025, 8025)
      .start();
  });
  
  test('should send welcome email on user.registered event', async () => {
    // Given: Kafkaにuser.registeredイベントを投稿
    await kafkaProducer.send({
      topic: 'user.registered',
      messages: [{
        value: JSON.stringify({
          userId: 'user-123',
          email: 'newuser@example.com',
          name: 'New User'
        })
      }]
    });
    
    // When: イベント処理を待つ
    await sleep(2000);
    
    // Then: メールが送信されていることを確認
    const mailhogAPI = `http://${mailhogContainer.getHost()}:${mailhogContainer.getMappedPort(8025)}`;
    const messages = await fetch(`${mailhogAPI}/api/v2/messages`).then(r => r.json());
    
    expect(messages.items).toHaveLength(1);
    expect(messages.items[0].Content.Headers.To[0]).toBe('newuser@example.com');
    expect(messages.items[0].Content.Headers.Subject[0]).toContain('Welcome');
  });
});
```

---

#### **PBI-3: ユーザー登録E2E統合テスト（バックエンド ↔ 通知サービス）**

**前提条件**: PBI-1, PBI-2完了後

**テスト範囲**:
```yaml
複数リポジトリ間の統合テスト: ✅ 必須
  サービス間連携:
    - バックエンド → Kafka → 通知サービス
    - エンドツーエンドのメッセージフロー
  
  実施方法:
    - Docker Compose必須
    - 専用テストリポジトリ: e2e-tests/
    - Phase 4（別PBIとして）で実施
```

**テスト環境構成**:
```yaml
# e2e-tests/docker-compose.user-registration.yml
version: '3.8'

services:
  backend:
    image: backend-service:${VERSION}
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
    image: notification-service:${VERSION}
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
  
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
    depends_on:
      - zookeeper
  
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
  
  mailhog:
    image: mailhog/mailhog:latest
    ports:
      - "8025:8025"
```

**テスト実装**:
```javascript
// e2e-tests/user-registration-e2e.test.js
describe('User Registration E2E (Backend + Notification Service)', () => {
  test('complete user registration flow with notification', async () => {
    // Given: テスト環境が起動している
    const backendURL = 'http://localhost:8080';
    const mailhogURL = 'http://localhost:8025';
    
    // When: ユーザー登録APIを呼び出す
    const registerResponse = await fetch(`${backendURL}/api/users/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: 'e2etest@example.com',
        password: 'SecurePassword123!',
        name: 'E2E Test User'
      })
    });
    
    // Then: バックエンドがユーザーを作成
    expect(registerResponse.status).toBe(201);
    const user = await registerResponse.json();
    expect(user.email).toBe('e2etest@example.com');
    
    // Then: 通知サービスがウェルカムメールを送信（最大10秒待機）
    await waitFor(async () => {
      const mailResponse = await fetch(`${mailhogURL}/api/v2/messages`);
      const mails = await mailResponse.json();
      
      const welcomeMail = mails.items.find(m => 
        m.Content.Headers.To[0] === 'e2etest@example.com'
      );
      
      expect(welcomeMail).toBeDefined();
      expect(welcomeMail.Content.Headers.Subject[0]).toContain('Welcome');
    }, { timeout: 10000, interval: 1000 });
  });
});
```

**実行方法**:
```bash
# e2e-tests/run-user-registration-e2e.sh
#!/bin/bash
set -e

# 環境変数設定
export VERSION=${1:-latest}

echo "🚀 Starting E2E test environment..."
docker-compose -f docker-compose.user-registration.yml up -d

echo "⏳ Waiting for services to be ready..."
./wait-for-services.sh

echo "🧪 Running E2E tests..."
npm test -- user-registration-e2e.test.js

TEST_EXIT_CODE=$?

echo "🧹 Cleaning up..."
docker-compose -f docker-compose.user-registration.yml down -v

if [ $TEST_EXIT_CODE -eq 0 ]; then
  echo "✅ E2E tests passed!"
else
  echo "❌ E2E tests failed!"
  exit $TEST_EXIT_CODE
fi
```

---

#### **PBI-4: フロントエンド統合（フロントエンド）**

**前提条件**: PBI-3完了後

**テスト範囲**:
```yaml
単一リポジトリ内のテスト: ✅ 必須
  コンポーネントテスト:
    - ユーザー登録フォーム
    - バリデーション
  
  実施方法:
    - Jest + React Testing Library
    - Phase 4で実施

複数リポジトリ間のテスト: ⚪ この段階では実施しない
  - バックエンドとの統合 → PBI-5で実施
```

---

#### **PBI-5: 完全E2Eテスト（フロントエンド ↔ バックエンド ↔ 通知サービス）**

**前提条件**: PBI-1, PBI-2, PBI-3, PBI-4完了後

**テスト範囲**:
```yaml
完全なE2Eテスト: ✅ 必須
  ユーザーフロー:
    1. フロントエンドでユーザー登録フォーム入力
    2. バックエンドがユーザー作成
    3. Kafkaイベント発行
    4. 通知サービスがメール送信
    5. フロントエンドに成功メッセージ表示
  
  実施方法:
    - Playwright / Cypress
    - Docker Compose（全サービス起動）
    - 専用テストリポジトリ: e2e-tests/
```

**テスト実装**:
```javascript
// e2e-tests/complete-user-registration.e2e.js (Playwright)
import { test, expect } from '@playwright/test';

test('complete user registration flow', async ({ page }) => {
  // Given: すべてのサービスが起動している
  const frontendURL = 'http://localhost:3000';
  const mailhogURL = 'http://localhost:8025';
  
  // When: ユーザー登録ページにアクセス
  await page.goto(`${frontendURL}/register`);
  
  // When: 登録フォームに入力
  await page.fill('[name="email"]', 'playwright@example.com');
  await page.fill('[name="password"]', 'SecurePassword123!');
  await page.fill('[name="name"]', 'Playwright Test User');
  
  // When: 登録ボタンをクリック
  await page.click('button[type="submit"]');
  
  // Then: 成功メッセージが表示される
  await expect(page.locator('.success-message')).toHaveText(
    'Registration successful! Please check your email.'
  );
  
  // Then: ダッシュボードにリダイレクトされる
  await expect(page).toHaveURL(`${frontendURL}/dashboard`);
  
  // Then: ウェルカムメールが送信されている
  const mailhogResponse = await fetch(`${mailhogURL}/api/v2/messages`);
  const mails = await mailhogResponse.json();
  
  const welcomeMail = mails.items.find(m => 
    m.Content.Headers.To[0] === 'playwright@example.com'
  );
  
  expect(welcomeMail).toBeDefined();
  expect(welcomeMail.Content.Headers.Subject[0]).toContain('Welcome');
});
```

---

## 🎯 PBI計画のベストプラクティス

### ✅ 推奨アプローチ

#### **Phase 1: 各サービスの単独実装（並行可能）**
```
PBI-1: サービスA実装 + 単一リポジトリ内テスト
PBI-2: サービスB実装 + 単一リポジトリ内テスト
PBI-3: サービスC実装 + 単一リポジトリ内テスト

並行作業可能 ✅
各チームが独立して作業できる
```

#### **Phase 2: サービス間統合（順次実施）**
```
PBI-4: サービスA ↔ サービスB統合テスト
  ↓ 完了後
PBI-5: サービスB ↔ サービスC統合テスト
  ↓ 完了後
PBI-6: 完全E2Eテスト（A ↔ B ↔ C）

順次実施 ⚠️
依存関係あり
```

---

### ❌ 非推奨アプローチ

#### **アンチパターン1: すべてを1つのPBIにまとめる**
```
PBI-1: 全サービス実装 + 全統合テスト

問題点:
  ❌ PBIが巨大すぎる
  ❌ 並行作業不可能
  ❌ レビューが困難
  ❌ リスクが高い
```

#### **アンチパターン2: 統合テストを後回しにする**
```
PBI-1: サービスA実装
PBI-2: サービスB実装
PBI-3: サービスC実装
（統合テストなし）

問題点:
  ❌ 統合時に大量のバグ発見
  ❌ 手戻りが大きい
  ❌ リリース遅延
```

---

## 📋 チェックリスト

### 単一リポジトリ内テスト（各PBIで必須）

```yaml
Phase 3: 実装
  - [ ] ユニットテストを実装した
  - [ ] カバレッジ目標を達成した

Phase 4: レビュー・QA
  - [ ] 統合テストを実装した（TestContainers）
  - [ ] API endpointsをテストした
  - [ ] データベース操作をテストした
  - [ ] キャッシュ操作をテストした（該当する場合）
  - [ ] メッセージキュー操作をテストした（該当する場合）
  - [ ] すべてのテストがパスした
  - [ ] CI/CDでテストが実行できる
```

### 複数リポジトリ間テスト（別PBIで実施）

```yaml
Phase 4: レビュー・QA（専用PBI）
  - [ ] 専用テストリポジトリを作成した
  - [ ] Docker Compose構成を作成した
  - [ ] サービス間統合テストを実装した
  - [ ] E2Eテストを実装した
  - [ ] すべてのテストがパスした
  - [ ] テスト実行スクリプトを作成した
  - [ ] テストドキュメントを作成した
```

---

## 📚 関連ドキュメント

- **Phase 4レビュー・QAガイド**: Step 4.4統合テスト
- **testing-standards.md**: PBIタイプ別テスト要件マトリックス
- **integration-testing.md**: 統合テスト実施ガイド
- **container-standards.md**: Dockerコンテナ標準

---

## 🎯 まとめ

### 重要な原則

1. ✅ **単一リポジトリ内のテストは必須** - すべてのPBIで実施
2. ✅ **複数リポジトリ間のテストは別PBI** - 計画的に実施
3. ✅ **各サービスは独立してテスト可能** - 並行作業を促進
4. ✅ **統合テストは段階的に実施** - リスクを最小化

### 期待される効果

- ✅ **開発速度の向上**: 並行作業が可能
- ✅ **品質の向上**: 段階的な検証
- ✅ **リスクの低減**: 早期のバグ発見
- ✅ **チーム間の協調**: 明確な責任範囲

---

**発行**: devin-organization-standards 管理チーム  
**作成日**: 2025-11-07  
**バージョン**: 1.0.0
