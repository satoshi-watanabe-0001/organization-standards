# インターフェース設計書

**プロジェクト名:** ECサイト構築プロジェクト  
**ドキュメントID:** IF-DESIGN-001  
**バージョン:** 1.0  
**作成日:** 2025-10-30  
**作成者:** APIアーキテクト  
**承認者:** システムアーキテクト

---

## 1. 基本情報

### 1.1 目的

本ドキュメントは、ECサイトシステムのAPIインターフェースを定義し、以下を明確にする：

- REST API エンドポイント一覧
- リクエスト・レスポンス仕様
- 認証・認可方式
- エラーハンドリング
- 外部システム連携

### 1.2 対象読者

- フロントエンド開発者
- バックエンド開発者
- テスト担当者
- 外部連携担当者

### 1.3 関連成果物

- **入力**: [要件定義書](../../02_要件定義/要件定義書.md)、[機能要件一覧](../../02_要件定義/機能要件一覧.md)
- **出力**: [詳細設計書](../../04_詳細設計/sample_04_詳細設計書_UserService.md)

---

## 2. API設計概要

### 2.1 API基本情報

| 項目 | 内容 |
|------|------|
| **APIスタイル** | RESTful API |
| **ベースURL** | `https://api.ec-shop.com/api/v1` |
| **プロトコル** | HTTPS（TLS 1.3） |
| **データ形式** | JSON |
| **文字コード** | UTF-8 |
| **認証方式** | JWT Bearer Token |
| **バージョニング** | URLパス（/v1, /v2） |

### 2.2 API設計原則

| 原則 | 説明 |
|------|------|
| **RESTful** | HTTPメソッド（GET, POST, PUT, DELETE）を正しく使用 |
| **リソース指向** | URLはリソースを表現（動詞ではなく名詞） |
| **ステートレス** | セッション状態を保持しない（JWTで認証） |
| **HATEOAS** | レスポンスに関連リソースのリンクを含める |
| **ページネーション** | 大量データは必ずページング |

---

## 3. 認証・認可

### 3.1 JWT認証フロー

```mermaid
sequenceDiagram
    participant Client as クライアント
    participant API as APIサーバー
    participant Redis as Redis
    
    Client->>API: POST /auth/login<br/>{email, password}
    API->>API: 認証情報検証
    API->>Redis: Refresh Token保存
    API-->>Client: 200 OK<br/>{accessToken, refreshToken}
    
    Note over Client: accessToken（有効期限15分）<br/>をストレージに保存
    
    Client->>API: GET /users/me<br/>Authorization: Bearer {accessToken}
    API->>API: JWT検証
    API-->>Client: 200 OK<br/>{user data}
    
    Note over Client: accessTokenが期限切れ
    
    Client->>API: POST /auth/refresh<br/>{refreshToken}
    API->>Redis: Refresh Token検証
    API->>Redis: 新しいRefresh Token保存
    API-->>Client: 200 OK<br/>{accessToken, refreshToken}
```

### 3.2 認証エンドポイント

#### POST /auth/register（ユーザー登録）

**リクエスト:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "name": "山田太郎",
  "phone": "090-1234-5678"
}
```

**レスポンス（201 Created）:**
```json
{
  "userId": 12345,
  "email": "user@example.com",
  "name": "山田太郎",
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "dGhpcyBpcyByZWZyZXNo...",
  "expiresIn": 900
}
```

---

#### POST /auth/login（ログイン）

**リクエスト:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

**レスポンス（200 OK）:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "dGhpcyBpcyByZWZyZXNo...",
  "expiresIn": 900,
  "tokenType": "Bearer"
}
```

---

#### POST /auth/refresh（トークンリフレッシュ）

**リクエスト:**
```json
{
  "refreshToken": "dGhpcyBpcyByZWZyZXNo..."
}
```

**レスポンス（200 OK）:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "bmV3IHJlZnJlc2ggdG9r...",
  "expiresIn": 900
}
```

---

### 3.3 認可（Role-Based Access Control）

| ロール | 説明 | アクセス可能なリソース |
|--------|------|---------------------|
| **CUSTOMER** | 一般顧客 | 自分の注文、プロフィール |
| **ADMIN** | 管理者 | すべてのリソース、商品管理、ユーザー管理 |

**認可チェック例:**
```http
GET /api/v1/admin/users
Authorization: Bearer {accessToken}

→ Require: ADMIN role
```

---

## 4. APIエンドポイント一覧

### 4.1 ユーザー管理API

| メソッド | エンドポイント | 説明 | 認証 |
|---------|--------------|------|------|
| GET | /users/me | 自分のプロフィール取得 | 必須 |
| PUT | /users/me | プロフィール更新 | 必須 |
| GET | /users/me/orders | 注文履歴取得 | 必須 |
| POST | /users/me/addresses | 住所追加 | 必須 |
| GET | /users/me/addresses | 住所一覧取得 | 必須 |

#### GET /users/me（プロフィール取得）

**リクエスト:**
```http
GET /api/v1/users/me HTTP/1.1
Host: api.ec-shop.com
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

**レスポンス（200 OK）:**
```json
{
  "id": 12345,
  "email": "user@example.com",
  "name": "山田太郎",
  "phone": "090-1234-5678",
  "role": "CUSTOMER",
  "emailVerified": true,
  "createdAt": "2025-01-15T10:30:00Z",
  "_links": {
    "self": { "href": "/api/v1/users/me" },
    "orders": { "href": "/api/v1/users/me/orders" },
    "addresses": { "href": "/api/v1/users/me/addresses" }
  }
}
```

---

### 4.2 商品管理API

| メソッド | エンドポイント | 説明 | 認証 |
|---------|--------------|------|------|
| GET | /products | 商品一覧取得（ページング） | 不要 |
| GET | /products/{id} | 商品詳細取得 | 不要 |
| GET | /products/search | 商品検索 | 不要 |
| POST | /products | 商品登録 | 必須（ADMIN） |
| PUT | /products/{id} | 商品更新 | 必須（ADMIN） |
| DELETE | /products/{id} | 商品削除 | 必須（ADMIN） |

#### GET /products（商品一覧）

**リクエスト:**
```http
GET /api/v1/products?page=1&size=20&categoryId=5&sort=price,asc HTTP/1.1
Host: api.ec-shop.com
```

**クエリパラメータ:**
| パラメータ | 型 | 必須 | デフォルト | 説明 |
|-----------|---|------|----------|------|
| page | integer | ✗ | 1 | ページ番号（1始まり） |
| size | integer | ✗ | 20 | 1ページあたりの件数（最大100） |
| categoryId | integer | ✗ | - | カテゴリID |
| sort | string | ✗ | createdAt,desc | ソート（price,asc / name,desc） |

**レスポンス（200 OK）:**
```json
{
  "content": [
    {
      "id": 101,
      "name": "ワイヤレスマウス",
      "price": 2980,
      "description": "高精度センサー搭載",
      "stockQuantity": 50,
      "imageUrl": "https://cdn.ec-shop.com/products/101/main.jpg",
      "category": {
        "id": 5,
        "name": "PC周辺機器"
      },
      "_links": {
        "self": { "href": "/api/v1/products/101" }
      }
    }
  ],
  "page": {
    "size": 20,
    "totalElements": 156,
    "totalPages": 8,
    "number": 1
  },
  "_links": {
    "first": { "href": "/api/v1/products?page=1&size=20" },
    "self": { "href": "/api/v1/products?page=1&size=20" },
    "next": { "href": "/api/v1/products?page=2&size=20" },
    "last": { "href": "/api/v1/products?page=8&size=20" }
  }
}
```

---

#### POST /products（商品登録）

**リクエスト:**
```http
POST /api/v1/products HTTP/1.1
Host: api.ec-shop.com
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
Content-Type: application/json

{
  "categoryId": 5,
  "name": "ワイヤレスマウス",
  "description": "高精度センサー搭載のワイヤレスマウス",
  "price": 2980,
  "stockQuantity": 100,
  "sku": "MOUSE-WL-001"
}
```

**レスポンス（201 Created）:**
```json
{
  "id": 102,
  "categoryId": 5,
  "name": "ワイヤレスマウス",
  "slug": "wireless-mouse-102",
  "description": "高精度センサー搭載のワイヤレスマウス",
  "price": 2980,
  "stockQuantity": 100,
  "sku": "MOUSE-WL-001",
  "isPublished": false,
  "createdAt": "2025-10-30T15:30:00Z",
  "_links": {
    "self": { "href": "/api/v1/products/102" }
  }
}
```

---

### 4.3 注文管理API

| メソッド | エンドポイント | 説明 | 認証 |
|---------|--------------|------|------|
| POST | /orders | 注文作成 | 必須 |
| GET | /orders/{id} | 注文詳細取得 | 必須 |
| GET | /orders | 注文一覧取得 | 必須（ADMIN） |
| PUT | /orders/{id}/cancel | 注文キャンセル | 必須 |

#### POST /orders（注文作成）

**リクエスト:**
```json
{
  "items": [
    {
      "productId": 101,
      "quantity": 2
    },
    {
      "productId": 105,
      "quantity": 1
    }
  ],
  "shippingAddressId": 201,
  "paymentMethod": "CREDIT_CARD"
}
```

**レスポンス（201 Created）:**
```json
{
  "id": 5001,
  "orderNumber": "ORD-20251030-5001",
  "status": "PENDING",
  "items": [
    {
      "productId": 101,
      "productName": "ワイヤレスマウス",
      "quantity": 2,
      "unitPrice": 2980,
      "subtotal": 5960
    }
  ],
  "subtotal": 8940,
  "taxAmount": 894,
  "shippingFee": 500,
  "totalAmount": 10334,
  "orderedAt": "2025-10-30T15:45:00Z",
  "_links": {
    "self": { "href": "/api/v1/orders/5001" },
    "payment": { "href": "/api/v1/orders/5001/payment" }
  }
}
```

---

### 4.4 カート管理API

| メソッド | エンドポイント | 説明 | 認証 |
|---------|--------------|------|------|
| GET | /cart | カート内容取得 | 必須 |
| POST | /cart/items | カートに追加 | 必須 |
| PUT | /cart/items/{id} | 数量変更 | 必須 |
| DELETE | /cart/items/{id} | カートから削除 | 必須 |

---

## 5. 外部システム連携

### 5.1 Stripe決済API

```mermaid
sequenceDiagram
    participant Client as フロントエンド
    participant API as APIサーバー
    participant Stripe as Stripe API
    
    Client->>API: POST /orders<br/>{items, address}
    API-->>Client: Order Created<br/>{orderId, amount}
    
    Client->>Stripe: Create Payment Intent<br/>(Stripe.js)
    Stripe-->>Client: Payment Intent<br/>{clientSecret}
    
    Client->>Stripe: Confirm Payment<br/>(カード情報)
    Stripe-->>Client: Payment Success
    
    Stripe->>API: Webhook: payment_intent.succeeded
    API->>API: Order Status → PAID
    API-->>Stripe: 200 OK
```

**Stripe API連携:**
```yaml
Endpoint: https://api.stripe.com/v1/payment_intents
Method: POST
Authentication: Bearer sk_live_xxx (Secret Key)

Request:
  amount: 10334
  currency: jpy
  payment_method_types: [card]
  metadata:
    orderId: 5001
```

---

### 5.2 SendGrid メール送信API

**ウェルカムメール送信:**
```java
@Service
public class EmailService {
    
    private final SendGrid sendGrid;
    
    public void sendWelcomeEmail(String toEmail, String userName) {
        Email from = new Email("noreply@ec-shop.com");
        Email to = new Email(toEmail);
        String subject = "ご登録ありがとうございます";
        Content content = new Content("text/html", 
            "<h1>ようこそ、" + userName + "様</h1>");
        
        Mail mail = new Mail(from, subject, to, content);
        
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        
        sendGrid.api(request);
    }
}
```

---

## 6. エラーハンドリング

### 6.1 標準エラーレスポンス

**フォーマット:**
```json
{
  "timestamp": "2025-10-30T15:50:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "バリデーションエラーが発生しました",
  "path": "/api/v1/products",
  "errors": [
    {
      "field": "price",
      "rejectedValue": -100,
      "message": "価格は0以上である必要があります"
    },
    {
      "field": "name",
      "rejectedValue": "",
      "message": "商品名は必須です"
    }
  ]
}
```

### 6.2 HTTPステータスコード

| コード | 説明 | 使用例 |
|-------|------|--------|
| 200 | OK | GET成功 |
| 201 | Created | POST成功（リソース作成） |
| 204 | No Content | DELETE成功 |
| 400 | Bad Request | バリデーションエラー |
| 401 | Unauthorized | 認証失敗 |
| 403 | Forbidden | 認可失敗（権限不足） |
| 404 | Not Found | リソース not found |
| 409 | Conflict | データ競合（重複登録等） |
| 422 | Unprocessable Entity | ビジネスルール違反 |
| 429 | Too Many Requests | レート制限超過 |
| 500 | Internal Server Error | サーバーエラー |

---

## 7. 非機能要件

### 7.1 パフォーマンス

| 項目 | 目標値 | 測定方法 |
|------|--------|---------|
| **レスポンスタイム** | P95 < 500ms | CloudWatch メトリクス |
| **スループット** | 100 TPS | 負荷テスト |
| **同時接続数** | 1,000ユーザー | JMeter |

### 7.2 レート制限

| エンドポイント | 制限 | 期間 |
|--------------|------|------|
| 認証系（/auth/**） | 5リクエスト | 1分 |
| 一般API | 100リクエスト | 1分 |
| 検索API | 30リクエスト | 1分 |

**レート制限超過レスポンス（429）:**
```json
{
  "error": "Too Many Requests",
  "message": "レート制限を超えました。しばらくしてから再試行してください",
  "retryAfter": 60
}
```

---

## 8. 変更履歴

| バージョン | 日付 | 変更内容 | 変更者 |
|-----------|------|---------|--------|
| 1.0 | 2025-10-30 | 初版作成 | APIアーキテクト |

---

**ドキュメント終了**
