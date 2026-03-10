# 統合テスト実装ガイド
## Integration Testing Implementation Guide

**最終更新日**: 2025-10-27  
**バージョン**: 1.0.0  
**対象**: 開発者・QAエンジニア・テクニカルリード・自律型AI Devin  
**適用範囲**: 全プロジェクト共通統合テスト実装標準

---

## 📖 概要

このドキュメントは、**言語非依存の統合テスト設計原則と実装パターン**を定義します。複数のコンポーネント、モジュール、サービス間の相互作用とインターフェースをテストする方法を包括的に説明します。

### 🎯 目的

- **統合ポイントの検証**: コンポーネント間のインターフェース、データフロー、相互作用の確認
- **実装パターン**: APIテスト、データベーステスト、外部サービス統合の標準化
- **環境戦略**: テスト環境、データ管理、モックとリアル統合の使い分け
- **品質保証**: 統合レベルでのバグ早期発見、システム全体の信頼性向上

### 📚 関連ドキュメント

- **[Testing Strategy](./testing-strategy.md)**: WHY（なぜ）、WHAT（何を）、WHEN（いつ）- テスト戦略
- **[Unit Testing](./unit-testing.md)**: ユニットテスト実装ガイド
- **[E2E Testing](./e2e-testing.md)**: E2Eテスト実装ガイド
- **[Test Data Management](./test-data-management.md)**: テストデータ管理戦略
- **言語固有の実装**:
  - [Python Standards](../01-coding-standards/python-standards.md): requests, pytest-integration
  - [TypeScript/JavaScript Standards](../01-coding-standards/typescript-javascript-standards.md): supertest, axios
  - [Java Standards](../01-coding-standards/java-standards.md): RestAssured, Spring Test

---

## 📂 目次

1. [統合テストの基本原則](#1-統合テストの基本原則)
2. [統合テストのスコープ](#2-統合テストのスコープ)
3. [統合戦略とアプローチ](#3-統合戦略とアプローチ)
4. [APIテスト](#4-apiテスト)
5. [データベース統合テスト](#5-データベース統合テスト)
6. [外部サービス統合](#6-外部サービス統合)
7. [メッセージング統合テスト](#7-メッセージング統合テスト)
8. [テストデータ戦略](#8-テストデータ戦略)
9. [テスト環境管理](#9-テスト環境管理)
10. [パフォーマンスとタイムアウト](#10-パフォーマンスとタイムアウト)
11. [Devin AIガイドライン](#11-devin-aiガイドライン)

---

## 1. 統合テストの基本原則

### 1.1 統合テストとは

```mermaid
graph TD
    A[統合テスト] --> B[コンポーネント間の<br/>相互作用を検証]
    A --> C[実際の依存関係を使用]
    A --> D[インターフェースの<br/>整合性を確認]
    
    B --> B1[API呼び出し]
    B --> B2[データフロー]
    B --> B3[イベント伝播]
    
    C --> C1[実データベース]
    C --> C2[実メッセージキュー]
    C --> C3[実キャッシュ]
    
    D --> D1[契約テスト]
    D --> D2[スキーマ検証]
    D --> D3[エラーハンドリング]
```

**統合テストの定義**:
- **コンポーネント間の相互作用**: API呼び出し、データフロー、イベント伝播を検証
- **実際の依存関係を使用**: 実データベース、実メッセージキュー、実キャッシュと統合
- **インターフェースの整合性**: 契約テスト、スキーマ検証、エラーハンドリングを確認

---

### 1.2 ユニットテストとの違い

| 観点 | ユニットテスト | 統合テスト |
|------|--------------|-----------|
| **スコープ** | 単一の関数/クラス | 複数のコンポーネント |
| **依存関係** | モック/スタブ | 実際の依存関係 |
| **実行速度** | 非常に高速（ミリ秒） | 中速（秒単位） |
| **環境** | インメモリ | テスト環境が必要 |
| **目的** | ロジックの正確性 | 統合の正確性 |
| **失敗原因** | ロジックのバグ | インターフェースの不整合 |

---

### 1.3 統合テストの原則

```mermaid
graph LR
    A[統合テストの原則] --> B[現実的な環境]
    A --> C[独立性の維持]
    A --> D[明確な境界]
    A --> E[クリーンアップ]
    
    B --> B1[本番に近い構成]
    C --> C1[テスト間の独立]
    D --> D1[テスト範囲の明確化]
    E --> E1[後処理の徹底]
```

**4つの基本原則**:

1. **現実的な環境** - 本番に近い構成でテスト
2. **独立性の維持** - テスト間で状態を共有しない
3. **明確な境界** - テスト範囲を明確に定義
4. **クリーンアップ** - 後処理を徹底し、次のテストに影響を与えない

---

## 2. 統合テストのスコープ

### 2.1 テスト対象の分類

```mermaid
graph TD
    A[統合テスト対象] --> B[APIエンドポイント]
    A --> C[データベース操作]
    A --> D[外部サービス連携]
    A --> E[メッセージキュー]
    A --> F[キャッシュ層]
    
    B --> B1[RESTful API]
    B --> B2[GraphQL]
    B --> B3[gRPC]
    
    C --> C1[CRUD操作]
    C --> C2[トランザクション]
    C --> C3[複雑なクエリ]
    
    D --> D1[認証サービス]
    D --> D2[決済ゲートウェイ]
    D --> D3[メール送信]
    
    E --> E1[メッセージ発行]
    E --> E2[メッセージ消費]
    E --> E3[イベント処理]
    
    F --> F1[キャッシュ戦略]
    F --> F2[無効化処理]
```

**統合テストの対象**:

1. **APIエンドポイント**: RESTful API、GraphQL、gRPCの動作確認
2. **データベース操作**: CRUD、トランザクション、複雑なクエリの検証
3. **外部サービス連携**: 認証、決済、メール送信などの統合確認
4. **メッセージキュー**: メッセージの発行、消費、イベント処理の検証
5. **キャッシュ層**: キャッシュ戦略、無効化処理の確認

---

### 2.2 統合レベルの階層

```mermaid
graph TD
    A[統合テストの階層] --> B[コンポーネント統合<br/>Component Integration]
    A --> C[サービス統合<br/>Service Integration]
    A --> D[システム統合<br/>System Integration]
    
    B --> B1[2-3個のクラス/モジュール]
    B --> B2[ローカル統合]
    
    C --> C1[マイクロサービス間]
    C --> C2[API経由の統合]
    
    D --> D1[複数システム]
    D --> D2[本番環境に近い]
```

**階層の定義**:

1. **コンポーネント統合**: 2-3個のクラス/モジュールのローカル統合
2. **サービス統合**: マイクロサービス間のAPI経由の統合
3. **システム統合**: 複数システムの本番環境に近い統合

---

## 3. 統合戦略とアプローチ

### 3.1 統合アプローチの種類

```mermaid
graph TD
    A[統合アプローチ] --> B[ビッグバン統合]
    A --> C[インクリメンタル統合]
    
    C --> D[トップダウン]
    C --> E[ボトムアップ]
    C --> F[サンドイッチ]
    
    B --> B1[全コンポーネント一度に統合]
    B --> B2[高リスク]
    
    D --> D1[上位モジュールから]
    D --> D2[スタブが必要]
    
    E --> E1[下位モジュールから]
    E --> E2[ドライバーが必要]
    
    F --> F1[上位と下位を並行]
    F --> F2[中間層で統合]
```

**各アプローチの特徴**:

| アプローチ | メリット | デメリット | 推奨度 |
|----------|---------|----------|--------|
| **ビッグバン** | 簡単、迅速 | バグ特定困難、高リスク | ❌ 非推奨 |
| **トップダウン** | 早期UI確認 | スタブ作成コスト | ⚠️ 状況による |
| **ボトムアップ** | 基盤から構築 | UI確認が遅い | ✅ 推奨 |
| **サンドイッチ** | バランス良い | 複雑な管理 | ✅ 推奨（大規模） |

---

### 3.2 推奨アプローチ：ボトムアップ

**理由**:
- データ層から始めて安定性を確保
- 下位層のバグを早期発見
- 実装順序と一致しやすい
- スタブ・ドライバーの作成が最小限

**実装手順**:

```
1. データベース層の統合テスト
   ↓
2. ビジネスロジック層とDB層の統合
   ↓
3. API層とビジネスロジック層の統合
   ↓
4. 外部サービスとの統合
   ↓
5. フロントエンドとAPIの統合
```

---

## 4. APIテスト

### 4.1 RESTful APIテストの基本

```mermaid
sequenceDiagram
    participant T as テストコード
    participant A as APIサーバー
    participant D as データベース
    participant E as 外部サービス
    
    T->>A: HTTPリクエスト送信
    activate A
    A->>D: データ取得/更新
    activate D
    D-->>A: データ返却
    deactivate D
    A->>E: 外部API呼び出し
    activate E
    E-->>A: レスポンス返却
    deactivate E
    A-->>T: HTTPレスポンス返却
    deactivate A
    T->>T: レスポンス検証
```

**APIテストの流れ**:
1. テストコードからHTTPリクエスト送信
2. APIサーバーがデータベースからデータ取得/更新
3. 必要に応じて外部サービスAPIを呼び出し
4. HTTPレスポンスを返却
5. テストコードでレスポンスを検証

---

### 4.2 APIテストの検証項目

**チェックリスト**:

```mermaid
graph TD
    A[APIテスト検証項目] --> B[ステータスコード]
    A --> C[レスポンスボディ]
    A --> D[ヘッダー]
    A --> E[パフォーマンス]
    A --> F[エラーハンドリング]
    
    B --> B1[200 OK]
    B --> B2[201 Created]
    B --> B3[400 Bad Request]
    B --> B4[401 Unauthorized]
    B --> B5[404 Not Found]
    
    C --> C1[スキーマ検証]
    C --> C2[データ型]
    C --> C3[必須フィールド]
    
    D --> D1[Content-Type]
    D --> D2[認証トークン]
    D --> D3[CORS]
    
    E --> E1[レスポンス時間]
    E --> E2[タイムアウト]
    
    F --> F1[エラーメッセージ]
    F --> F2[エラーコード]
```

**検証項目**:
- **ステータスコード**: 200 OK、201 Created、400 Bad Request、401 Unauthorized、404 Not Found
- **レスポンスボディ**: スキーマ検証、データ型、必須フィールドの確認
- **ヘッダー**: Content-Type、認証トークン、CORSの検証
- **パフォーマンス**: レスポンス時間、タイムアウトの測定
- **エラーハンドリング**: エラーメッセージ、エラーコードの確認

---

### 4.3 APIテストの実装パターン

<details>
<summary>RESTful APIテストの完全な実装例（全25行・クリックして展開）</summary>

```kotlin
testCreateUser_WithValidData_ShouldReturn201():
    // ========== Arrange ==========
    apiClient = HttpClient(baseUrl="http://test-api.example.com")
    userData = {
        "username": "testuser",
        "email": "test@example.com",
        "password": "SecurePass123!"
    }
    
    // ========== Act ==========
    response = apiClient.post("/api/v1/users", body=userData)
    
    // ========== Assert ==========
    // ステータスコード検証
    assert response.statusCode == 201
    
    // レスポンスボディ検証
    assert response.body.id != null
    assert response.body.username == "testuser"
    assert response.body.email == "test@example.com"
    assert response.body.password == undefined  // パスワードは返却されない
    
    // ヘッダー検証
    assert response.headers["Content-Type"] == "application/json"
    assert response.headers["Location"] == "/api/v1/users/" + response.body.id
    
    // データベース検証（オプション）
    dbUser = database.query("SELECT * FROM users WHERE id = ?", response.body.id)
    assert dbUser != null
    assert dbUser.username == "testuser"
```

</details>

---

### 4.4 スキーマ検証

**JSON Schemaによる検証**:

<details>
<summary>JSON Schema検証の実装例（全20行・クリックして展開）</summary>

```kotlin
testGetUser_ShouldReturnValidSchema():
    // Arrange
    userId = createTestUser()
    expectedSchema = {
        "type": "object",
        "required": ["id", "username", "email", "createdAt"],
        "properties": {
            "id": {"type": "integer"},
            "username": {"type": "string", "minLength": 3},
            "email": {"type": "string", "format": "email"},
            "createdAt": {"type": "string", "format": "date-time"},
            "isActive": {"type": "boolean"}
        }
    }
    
    // Act
    response = apiClient.get("/api/v1/users/" + userId)
    
    // Assert
    assert response.statusCode == 200
    assertJsonSchema(response.body, expectedSchema)
```

</details>

---

## 5. データベース統合テスト

### 5.1 データベーステストの戦略

```mermaid
graph TD
    A[データベーステスト戦略] --> B[トランザクション管理]
    A --> C[データ準備]
    A --> D[クリーンアップ]
    
    B --> B1[各テスト前に開始]
    B --> B2[テスト後にロールバック]
    
    C --> C1[フィクスチャ]
    C --> C2[ファクトリー]
    C --> C3[データビルダー]
    
    D --> D1[自動ロールバック]
    D --> D2[手動削除]
    D --> D3[データベースリセット]
```

**データベーステストの3つの戦略**:

1. **トランザクション管理**: 各テスト前に開始、テスト後にロールバック
2. **データ準備**: フィクスチャ、ファクトリー、データビルダーの使用
3. **クリーンアップ**: 自動ロールバック、手動削除、データベースリセット

---

### 5.2 トランザクション分離

**推奨パターン: テストごとのトランザクション**

```kotlin
testUserRepository_SaveAndRetrieve():
    // Setup: トランザクション開始
    transaction = database.beginTransaction()
    
    try:
        // Arrange
        user = User(username="testuser", email="test@example.com")
        
        // Act
        savedUser = userRepository.save(user)
        retrievedUser = userRepository.findById(savedUser.id)
        
        // Assert
        assert retrievedUser != null
        assert retrievedUser.username == "testuser"
        assert retrievedUser.email == "test@example.com"
        
    finally:
        // Teardown: トランザクションロールバック
        transaction.rollback()
```

---

### 5.3 複雑なクエリのテスト

<details>
<summary>複雑なクエリテストの実装例（全30行・クリックして展開）</summary>

```kotlin
testUserRepository_FindActiveUsersWithPremiumSubscription():
    // Arrange
    transaction = database.beginTransaction()
    
    // 通常ユーザー（非アクティブ）
    inactiveUser = createUser(username="inactive", isActive=false)
    
    // 通常ユーザー（アクティブ、無料）
    freeUser = createUser(username="free", isActive=true, isPremium=false)
    
    // プレミアムユーザー（アクティブ）
    premiumUser1 = createUser(username="premium1", isActive=true, isPremium=true)
    premiumUser2 = createUser(username="premium2", isActive=true, isPremium=true)
    
    // プレミアムユーザー（非アクティブ）
    inactivePremium = createUser(username="inactive_premium", isActive=false, isPremium=true)
    
    // Act
    result = userRepository.findActiveUsersWithPremiumSubscription()
    
    // Assert
    assert result.length == 2
    assert result.contains(premiumUser1)
    assert result.contains(premiumUser2)
    assert !result.contains(inactiveUser)
    assert !result.contains(freeUser)
    assert !result.contains(inactivePremium)
    
    // Teardown
    transaction.rollback()
```

</details>

---

### 5.4 データベース制約のテスト

**一意性制約、外部キー制約などのテスト**:

```kotlin
testUserRepository_SaveDuplicateEmail_ShouldThrowConstraintViolation():
    transaction = database.beginTransaction()
    
    try:
        // Arrange
        email = "duplicate@example.com"
        user1 = User(username="user1", email=email)
        userRepository.save(user1)
        
        // Act & Assert
        user2 = User(username="user2", email=email)
        assertThrows(ConstraintViolationException) {
            userRepository.save(user2)
        }
        
    finally:
        transaction.rollback()
```

---

## 6. 外部サービス統合

### 6.1 外部サービス統合のアプローチ

```mermaid
graph TD
    A[外部サービス統合] --> B[モック使用]
    A --> C[スタブサーバー]
    A --> D[サンドボックス環境]
    A --> E[本番API]
    
    B --> B1[完全制御]
    B --> B2[高速]
    B --> B3[制限: 実際の動作未検証]
    
    C --> C1[WireMock/MockServer]
    C --> C2[実際のHTTP通信]
    C --> C3[制限: 設定が必要]
    
    D --> D1[ベンダー提供]
    D --> D2[本番に近い]
    D --> D3[制限: コストがかかる]
    
    E --> E1[最も現実的]
    E --> E2[制限: レート制限、コスト]
```

**4つのアプローチ**:

1. **モック使用**: 完全制御、高速、但し実際の動作未検証
2. **スタブサーバー**: WireMock等で実際のHTTP通信、但し設定が必要
3. **サンドボックス環境**: ベンダー提供、本番に近い、但しコストがかかる
4. **本番API**: 最も現実的、但しレート制限とコストに注意

---

### 6.2 推奨: サンドボックス環境の使用

**理由**:
- 本番環境に近い動作
- 実際のAPIレスポンスを検証
- エラーケースもテスト可能
- コストは許容範囲

**実装例**:

<details>
<summary>サンドボックス環境を使用した決済APIテスト（全25行・クリックして展開）</summary>

```kotlin
testPaymentService_ProcessPayment_ShouldReturnTransactionId():
    // Arrange
    paymentService = PaymentService(
        apiKey=SANDBOX_API_KEY,
        apiUrl="https://sandbox.payment-provider.com"
    )
    
    paymentRequest = {
        "amount": 100.00,
        "currency": "USD",
        "cardNumber": "4242424242424242",  // テストカード番号
        "expiryMonth": 12,
        "expiryYear": 2025,
        "cvv": "123"
    }
    
    // Act
    response = paymentService.processPayment(paymentRequest)
    
    // Assert
    assert response.success == true
    assert response.transactionId != null
    assert response.transactionId.startsWith("txn_")
    assert response.amount == 100.00
    assert response.currency == "USD"
    assert response.status == "completed"
```

</details>

---

### 6.3 契約テスト（Contract Testing）

```mermaid
graph LR
    A[プロバイダー<br/>Provider] -->|契約定義| B[契約<br/>Contract]
    B -->|契約検証| C[コンシューマー<br/>Consumer]
    
    A -->|実装| D[実際のAPI]
    D -->|契約準拠| B
    C -->|期待| B
```

**契約テストの概念**:
- **プロバイダー**: APIを提供する側が契約を定義
- **契約**: APIの仕様（リクエスト/レスポンス形式）
- **コンシューマー**: APIを使用する側が契約を検証

**実装例**:

```kotlin
testPaymentAPI_Contract_ShouldMatchExpectedSchema():
    // 契約定義
    expectedContract = {
        "request": {
            "method": "POST",
            "path": "/api/v1/payments",
            "body": {"amount": 100.00, "currency": "USD"}
        },
        "response": {
            "status": 200,
            "body": {
                "transactionId": "string",
                "status": "completed",
                "amount": 100.00
            }
        }
    }
    
    // 契約検証
    response = paymentClient.post("/api/v1/payments", body={"amount": 100.00, "currency": "USD"})
    assertContractMatch(response, expectedContract)
```

---

## 7. メッセージング統合テスト

### 7.1 メッセージキューの統合テスト

```mermaid
sequenceDiagram
    participant P as Producer
    participant Q as Queue
    participant C as Consumer
    participant D as Database
    
    P->>Q: メッセージ発行
    activate Q
    Q->>C: メッセージ配信
    deactivate Q
    activate C
    C->>D: データ処理
    activate D
    D-->>C: 完了
    deactivate D
    C-->>Q: ACK送信
    deactivate C
```

**メッセージングテストの流れ**:
1. Producer（生産者）がメッセージ発行
2. Queue（キュー）がメッセージを保持
3. Consumer（消費者）がメッセージ配信を受け取る
4. Databaseにデータ処理
5. ACK（確認応答）を送信

---

### 7.2 メッセージ発行のテスト

<details>
<summary>メッセージ発行テストの実装例（全20行・クリックして展開）</summary>

```kotlin
testOrderService_CreateOrder_ShouldPublishOrderCreatedEvent():
    // Arrange
    messageQueue = InMemoryMessageQueue()  // テスト用のインメモリキュー
    orderService = OrderService(messageQueue)
    orderData = {
        "customerId": 123,
        "items": [{"productId": 1, "quantity": 2}],
        "totalAmount": 50.00
    }
    
    // Act
    order = orderService.createOrder(orderData)
    
    // Assert
    messages = messageQueue.getPublishedMessages("order.created")
    assert messages.length == 1
    assert messages[0].orderId == order.id
    assert messages[0].customerId == 123
    assert messages[0].totalAmount == 50.00
```

</details>

---

### 7.3 メッセージ消費のテスト

<details>
<summary>メッセージ消費テストの実装例（全25行・クリックして展開）</summary>

```kotlin
testEmailNotificationConsumer_ProcessOrderCreated_ShouldSendEmail():
    // Arrange
    messageQueue = InMemoryMessageQueue()
    emailService = MockEmailService()
    consumer = EmailNotificationConsumer(messageQueue, emailService)
    
    message = {
        "eventType": "order.created",
        "orderId": 456,
        "customerId": 123,
        "customerEmail": "customer@example.com",
        "totalAmount": 50.00
    }
    
    // Act
    messageQueue.publish("order.created", message)
    consumer.processMessages()  // メッセージを処理
    
    // Assert
    sentEmails = emailService.getSentEmails()
    assert sentEmails.length == 1
    assert sentEmails[0].to == "customer@example.com"
    assert sentEmails[0].subject.contains("Order Confirmation")
    assert sentEmails[0].body.contains("Order #456")
    assert sentEmails[0].body.contains("$50.00")
```

</details>

---

## 8. テストデータ戦略

### 8.1 テストデータの管理方針

```mermaid
graph TD
    A[テストデータ管理] --> B[データ準備戦略]
    A --> C[データ分離戦略]
    A --> D[クリーンアップ戦略]
    
    B --> B1[フィクスチャ]
    B --> B2[ファクトリーメソッド]
    B --> B3[データビルダー]
    
    C --> C1[テストごとに独立]
    C --> C2[一意なID使用]
    C --> C3[専用スキーマ]
    
    D --> D1[トランザクションロールバック]
    D --> D2[後処理フック]
    D --> D3[定期的なリセット]
```

**3つの管理戦略**:

1. **データ準備戦略**: フィクスチャ、ファクトリーメソッド、データビルダー
2. **データ分離戦略**: テストごとに独立、一意なID使用、専用スキーマ
3. **クリーンアップ戦略**: トランザクションロールバック、後処理フック、定期的なリセット

---

### 8.2 データビルダーパターン

<details>
<summary>統合テスト用データビルダーの実装例（全30行・クリックして展開）</summary>

```kotlin
class TestDataBuilder:
    function createTestUser(username=null, email=null, isActive=true):
        username = username ?: "testuser_" + randomUUID()
        email = email ?: username + "@test.example.com"
        
        user = User(
            username=username,
            email=email,
            isActive=isActive,
            createdAt=now()
        )
        
        return database.insert(user)
    
    function createTestOrder(customerId, itemCount=1, totalAmount=100.00):
        order = Order(
            customerId=customerId,
            orderNumber="ORD-" + randomUUID(),
            totalAmount=totalAmount,
            status="pending",
            createdAt=now()
        )
        
        savedOrder = database.insert(order)
        
        for i in range(itemCount):
            item = OrderItem(
                orderId=savedOrder.id,
                productId=i + 1,
                quantity=1,
                price=totalAmount / itemCount
            )
            database.insert(item)
        
        return savedOrder

// 使用例
testOrderAPI_GetOrder_ShouldReturnOrderWithItems():
    // Arrange
    builder = TestDataBuilder()
    user = builder.createTestUser()
    order = builder.createTestOrder(customerId=user.id, itemCount=3)
    
    // Act
    response = apiClient.get("/api/v1/orders/" + order.id)
    
    // Assert
    assert response.body.items.length == 3
```

</details>

---

### 8.3 一意性の確保

**UUID/タイムスタンプの使用**:

```kotlin
testUserAPI_CreateMultipleUsers_ShouldSucceed():
    // 一意なユーザー名を生成
    timestamp = currentTimeMillis()
    user1Username = "testuser_" + timestamp + "_1"
    user2Username = "testuser_" + timestamp + "_2"
    
    // 並列実行でも一意性が保証される
    response1 = apiClient.post("/api/v1/users", {username: user1Username})
    response2 = apiClient.post("/api/v1/users", {username: user2Username})
    
    assert response1.statusCode == 201
    assert response2.statusCode == 201
    assert response1.body.id != response2.body.id
```

---

## 9. テスト環境管理

### 9.1 環境構成

```mermaid
graph TD
    A[テスト環境] --> B[ローカル環境]
    A --> C[CI/CD環境]
    A --> D[専用テスト環境]
    
    B --> B1[Docker Compose]
    B --> B2[開発者PC]
    
    C --> C1[GitHub Actions]
    C --> C2[GitLab CI]
    C --> C3[Jenkins]
    
    D --> D1[ステージング環境]
    D --> D2[専用DBサーバー]
```

**3つの環境タイプ**:

1. **ローカル環境**: Docker Composeで開発者PC上に構築
2. **CI/CD環境**: GitHub Actions、GitLab CI、Jenkinsで自動実行
3. **専用テスト環境**: ステージング環境、専用DBサーバー

---

### 9.2 Docker Composeによる環境構築

**推奨: Docker Composeで統合テスト環境を構築**

```yaml
# docker-compose.test.yml
version: '3.8'

services:
  postgres:
    image: postgres:14
    environment:
      POSTGRES_DB: test_db
      POSTGRES_USER: test_user
      POSTGRES_PASSWORD: test_pass
    ports:
      - "5432:5432"
  
  redis:
    image: redis:7
    ports:
      - "6379:6379"
  
  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"
  
  api:
    build: .
    environment:
      DATABASE_URL: postgres://test_user:test_pass@postgres:5432/test_db
      REDIS_URL: redis://redis:6379
      RABBITMQ_URL: amqp://rabbitmq:5672
    depends_on:
      - postgres
      - redis
      - rabbitmq
    ports:
      - "8080:8080"
```

---

### 9.3 環境変数の管理

**テスト用設定ファイルの使用**:

```kotlin
// test.config
DATABASE_URL=postgres://test_user:test_pass@localhost:5432/test_db
API_BASE_URL=http://localhost:8080
REDIS_URL=redis://localhost:6379
RABBITMQ_URL=amqp://localhost:5672

// 認証情報
API_KEY=test_api_key_12345
API_SECRET=test_secret_67890

// 外部サービス（サンドボックス）
PAYMENT_API_URL=https://sandbox.payment-provider.com
PAYMENT_API_KEY=sandbox_key_12345
```

---

## 10. パフォーマンスとタイムアウト

### 10.1 タイムアウト設定

```mermaid
graph TD
    A[タイムアウト設定] --> B[接続タイムアウト]
    A --> C[読み取りタイムアウト]
    A --> D[全体タイムアウト]
    
    B --> B1[3-5秒]
    C --> C1[10-30秒]
    D --> D1[60秒]
```

**推奨タイムアウト設定**:

| タイプ | 推奨値 | 説明 |
|-------|-------|------|
| **接続タイムアウト** | 3-5秒 | サーバーへの接続確立まで |
| **読み取りタイムアウト** | 10-30秒 | レスポンス受信まで |
| **全体タイムアウト** | 60秒 | テスト全体の最大実行時間 |

---

### 10.2 パフォーマンス目標

**統合テストの実行時間目標**:

```
単一の統合テスト:    < 5秒
テストスイート全体:  < 10分（100テストの場合）

目標:
- データベース操作: < 1秒
- API呼び出し: < 3秒
- 外部サービス: < 5秒
```

---

### 10.3 遅いテストの最適化

**最適化テクニック**:

```mermaid
graph TD
    A[テスト高速化] --> B[並列実行]
    A --> C[データ準備の最適化]
    A --> D[キャッシュの活用]
    A --> E[不要な検証の削減]
    
    B --> B1[独立したテストを並列化]
    C --> C1[バルクインサート]
    D --> D1[共有フィクスチャ]
    E --> E1[重要な検証に絞る]
```

**具体的な手法**:
1. **並列実行**: 独立したテストを並列化
2. **データ準備の最適化**: バルクインサートを使用
3. **キャッシュの活用**: 共有フィクスチャを使用（慎重に）
4. **不要な検証の削減**: 重要な検証に絞る

---

## 11. Devin AIガイドライン

### 11.1 自動統合テスト生成時の指針

```mermaid
graph TD
    A[Devin AI<br/>統合テスト生成] --> B[適切なスコープ設定]
    A --> C[環境依存の最小化]
    A --> D[クリーンアップの徹底]
    A --> E[エラーケースの網羅]
    
    B --> B1[統合ポイントに焦点]
    C --> C1[Docker Compose活用]
    D --> D1[トランザクション管理]
    E --> E1[境界値、異常系]
```

**Devin AIが統合テストを生成する際の原則**:

1. **適切なスコープ設定**: 統合ポイントに焦点を当てる
2. **環境依存の最小化**: Docker Composeを活用
3. **クリーンアップの徹底**: トランザクション管理を確実に
4. **エラーケースの網羅**: 境界値、異常系をカバー

---

### 11.2 統合テスト生成プロンプト

<details>
<summary>推奨プロンプトテンプレート（クリックして展開）</summary>

```kotlin
"以下のAPIエンドポイントの統合テストを生成してください:

[APIエンドポイントの定義]

要件:
- AAAパターンに従う
- データベース統合を含む
- トランザクションロールバックを使用
- ステータスコード、レスポンスボディ、データベース状態を検証
- 正常系と異常系の両方をカバー
- 言語固有の実装は [言語]-standards.md を参照

テストケース:
1. 正常系: 有効なデータでの作成/取得/更新/削除
2. 異常系: 無効なデータ、存在しないリソース
3. 境界値: 最小値、最大値
4. 並行処理: 競合状態のテスト
5. エラーハンドリング: 適切なエラーレスポンス"
```

</details>

---

### 11.3 レビューチェックリスト

**Devin AIが生成した統合テストコードのレビューポイント**:

- ☐ 適切なスコープ（統合ポイントに焦点）
- ☐ トランザクション管理が正しい
- ☐ クリーンアップが徹底されている
- ☐ 実際の依存関係を使用している
- ☐ タイムアウト設定が適切
- ☐ エラーケースがカバーされている
- ☐ レスポンス検証が十分
- ☐ データベース状態の検証がある
- ☐ 並列実行に対応している
- ☐ 環境変数が適切に管理されている

---

### 11.4 継続的改善

**統合テストの品質向上サイクル**:

```mermaid
graph LR
    A[1. 生成] --> B[2. 実行]
    B --> C[3. 分析]
    C --> D[4. 改善]
    D --> A
    
    A --> A1[Devin AIがテスト生成]
    B --> B1[CI/CDで自動実行]
    C --> C1[カバレッジ・実行時間分析]
    D --> D1[遅いテストの最適化]
```

**サイクルの流れ**:

1. **生成**: Devin AIが統合テストを生成
2. **実行**: CI/CDで自動実行
3. **分析**: カバレッジと実行時間を分析
4. **改善**: 遅いテストを最適化し、カバレッジ向上

---

## 📚 参考資料

### 関連ドキュメント

- **[Testing Strategy](./testing-strategy.md)**: テスト戦略全体像
- **[Unit Testing](./unit-testing.md)**: ユニットテスト実装ガイド
- **[E2E Testing](./e2e-testing.md)**: E2Eテスト実装ガイド
- **[Test Data Management](./test-data-management.md)**: テストデータ管理戦略
- **[Code Quality Standards](./code-quality-standards.md)**: コード品質基準

### 言語固有の実装

- **[Python Standards](../01-coding-standards/python-standards.md)**: requests, pytest-integration
- **[TypeScript/JavaScript Standards](../01-coding-standards/typescript-javascript-standards.md)**: supertest, axios
- **[Java Standards](../01-coding-standards/java-standards.md)**: RestAssured, Spring Test

---

## 📝 改訂履歴

| バージョン | 日付       | 変更内容                           | 変更者       |
|-----------|------------|----------------------------------|-------------|
| 1.0.0     | 2025-10-27 | 初版作成（言語非依存設計、ドキュメント生成ルールv1.1.2準拠） | Devin AI    |

---

**最終更新**: 2025-10-27  
**次回レビュー予定**: 2025-11-27
