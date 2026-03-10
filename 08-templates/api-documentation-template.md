---
version: "1.0.0"
last_updated: "2025-01-15"
status: "active"
owner: "Engineering Team"
category: "template"
---

# API ドキュメントテンプレート / API Documentation Template

## 📋 API 基本情報 / API Basic Information

### API 名 / API Name
[API の正式名称]

### バージョン / Version
`v1.0.0`

### ベースURL / Base URL

| 環境 | URL |
|-----|-----|
| Production | `https://api.example.com` |
| Staging | `https://api-stg.example.com` |
| Development | `https://api-dev.example.com` |

### プロトコル / Protocol
- **HTTP Methods**: GET, POST, PUT, PATCH, DELETE
- **Content-Type**: `application/json`
- **Character Encoding**: UTF-8

---

## 🔐 認証 / Authentication

### 認証方式 / Authentication Method

#### Bearer Token (JWT)

**ヘッダー形式 / Header Format**:
```http
Authorization: Bearer {access_token}
```

**トークン取得方法 / Token Acquisition**:
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**レスポンス / Response**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

#### API Key（オプション）

**ヘッダー形式 / Header Format**:
```http
X-API-Key: {your_api_key}
```

### トークンのリフレッシュ / Token Refresh

```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## 📊 共通仕様 / Common Specifications

### リクエストヘッダー / Request Headers

| ヘッダー名 | 必須 | 説明 | 例 |
|----------|------|------|---|
| `Authorization` | Yes* | 認証トークン | `Bearer {token}` |
| `Content-Type` | Yes | コンテンツタイプ | `application/json` |
| `Accept` | No | 受け入れ形式 | `application/json` |
| `X-Request-ID` | No | リクエストID（トレース用） | `uuid-v4` |
| `Accept-Language` | No | 言語設定 | `ja`, `en` |

*一部の公開エンドポイントでは不要

### レスポンス形式 / Response Format

#### 成功レスポンス / Success Response

```json
{
  "data": {
    "id": "123",
    "name": "Example",
    "createdAt": "2025-01-15T10:00:00Z"
  },
  "meta": {
    "requestId": "req-abc-123",
    "timestamp": "2025-01-15T10:00:00Z"
  }
}
```

#### リスト形式レスポンス / List Response

```json
{
  "data": [
    {
      "id": "123",
      "name": "Item 1"
    },
    {
      "id": "124",
      "name": "Item 2"
    }
  ],
  "meta": {
    "total": 100,
    "page": 1,
    "perPage": 20,
    "totalPages": 5
  },
  "links": {
    "first": "/api/v1/items?page=1",
    "prev": null,
    "next": "/api/v1/items?page=2",
    "last": "/api/v1/items?page=5"
  }
}
```

#### エラーレスポンス / Error Response

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "入力データが不正です",
    "details": [
      {
        "field": "email",
        "message": "有効なメールアドレスを入力してください"
      },
      {
        "field": "password",
        "message": "パスワードは8文字以上必要です"
      }
    ]
  },
  "meta": {
    "requestId": "req-abc-123",
    "timestamp": "2025-01-15T10:00:00Z"
  }
}
```

### ステータスコード / Status Codes

| コード | 説明 | 使用例 |
|-------|------|--------|
| 200 | OK | 成功（GET, PUT, PATCH） |
| 201 | Created | リソース作成成功（POST） |
| 204 | No Content | 削除成功（DELETE） |
| 400 | Bad Request | リクエストが不正 |
| 401 | Unauthorized | 認証が必要 |
| 403 | Forbidden | 権限がない |
| 404 | Not Found | リソースが見つからない |
| 409 | Conflict | リソースの競合 |
| 422 | Unprocessable Entity | バリデーションエラー |
| 429 | Too Many Requests | レート制限超過 |
| 500 | Internal Server Error | サーバーエラー |
| 503 | Service Unavailable | サービス利用不可 |

### エラーコード一覧 / Error Codes

| エラーコード | HTTPコード | 説明 |
|------------|-----------|------|
| `VALIDATION_ERROR` | 422 | 入力検証エラー |
| `AUTHENTICATION_FAILED` | 401 | 認証失敗 |
| `UNAUTHORIZED` | 401 | トークンが無効または期限切れ |
| `FORBIDDEN` | 403 | アクセス権限なし |
| `NOT_FOUND` | 404 | リソースが存在しない |
| `CONFLICT` | 409 | リソースが既に存在 |
| `RATE_LIMIT_EXCEEDED` | 429 | レート制限超過 |
| `INTERNAL_ERROR` | 500 | 内部サーバーエラー |

### ページネーション / Pagination

#### クエリパラメータ / Query Parameters

| パラメータ | 型 | デフォルト | 説明 |
|----------|---|----------|------|
| `page` | integer | 1 | ページ番号 |
| `perPage` | integer | 20 | 1ページあたりの件数（最大100） |
| `sort` | string | - | ソートフィールド（例: `createdAt:desc`） |

#### 例 / Example

```http
GET /api/v1/users?page=2&perPage=50&sort=createdAt:desc
```

### フィルタリング / Filtering

| パラメータ | 型 | 説明 | 例 |
|----------|---|------|---|
| `filter[field]` | string | フィールドでフィルタ | `filter[status]=active` |
| `search` | string | 全文検索 | `search=keyword` |

### レート制限 / Rate Limiting

| レベル | 制限 | 期間 |
|-------|------|------|
| 認証済みユーザー | 1000リクエスト | 1時間 |
| 未認証 | 100リクエスト | 1時間 |

**レート制限ヘッダー**:
```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1642240800
```

---

## 📚 エンドポイント一覧 / Endpoints

### 認証 / Authentication

#### ログイン / Login

```http
POST /api/v1/auth/login
```

**説明**: ユーザー認証を行い、アクセストークンを取得します。

**認証**: 不要

**リクエストボディ**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**リクエストパラメータ**:

| フィールド | 型 | 必須 | 説明 | バリデーション |
|----------|---|------|------|--------------|
| `email` | string | Yes | メールアドレス | Email形式 |
| `password` | string | Yes | パスワード | 8文字以上 |

**レスポンス（200 OK）**:
```json
{
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": "user-123",
      "email": "user@example.com",
      "name": "John Doe",
      "role": "user"
    }
  }
}
```

**エラーレスポンス**:

`401 Unauthorized`:
```json
{
  "error": {
    "code": "AUTHENTICATION_FAILED",
    "message": "メールアドレスまたはパスワードが正しくありません"
  }
}
```

**cURLサンプル**:
```bash
curl -X POST https://api.example.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

---

#### ログアウト / Logout

```http
POST /api/v1/auth/logout
```

**説明**: 現在のセッションを終了します。

**認証**: 必要

**リクエストヘッダー**:
```http
Authorization: Bearer {access_token}
```

**レスポンス（204 No Content）**:
```
（ボディなし）
```

---

#### トークンリフレッシュ / Refresh Token

```http
POST /api/v1/auth/refresh
```

**説明**: リフレッシュトークンを使用して新しいアクセストークンを取得します。

**認証**: 不要

**リクエストボディ**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**レスポンス（200 OK）**:
```json
{
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400
  }
}
```

---

### ユーザー管理 / User Management

#### ユーザー一覧取得 / Get Users

```http
GET /api/v1/users
```

**説明**: ユーザーの一覧を取得します。

**認証**: 必要

**権限**: `admin`, `manager`

**クエリパラメータ**:

| パラメータ | 型 | 必須 | デフォルト | 説明 |
|----------|---|------|----------|------|
| `page` | integer | No | 1 | ページ番号 |
| `perPage` | integer | No | 20 | 1ページあたりの件数 |
| `sort` | string | No | `createdAt:desc` | ソート順 |
| `filter[status]` | string | No | - | ステータスフィルタ（`active`, `inactive`, `suspended`） |
| `filter[role]` | string | No | - | ロールフィルタ（`admin`, `manager`, `user`） |
| `search` | string | No | - | 名前またはメールで検索 |

**レスポンス（200 OK）**:
```json
{
  "data": [
    {
      "id": "user-123",
      "email": "user1@example.com",
      "name": "John Doe",
      "role": "user",
      "status": "active",
      "createdAt": "2025-01-01T00:00:00Z",
      "updatedAt": "2025-01-15T10:00:00Z"
    },
    {
      "id": "user-124",
      "email": "user2@example.com",
      "name": "Jane Smith",
      "role": "manager",
      "status": "active",
      "createdAt": "2025-01-02T00:00:00Z",
      "updatedAt": "2025-01-15T10:00:00Z"
    }
  ],
  "meta": {
    "total": 150,
    "page": 1,
    "perPage": 20,
    "totalPages": 8
  },
  "links": {
    "first": "/api/v1/users?page=1",
    "prev": null,
    "next": "/api/v1/users?page=2",
    "last": "/api/v1/users?page=8"
  }
}
```

**cURLサンプル**:
```bash
curl -X GET "https://api.example.com/api/v1/users?page=1&perPage=20&filter[status]=active" \
  -H "Authorization: Bearer {access_token}"
```

---

#### ユーザー詳細取得 / Get User by ID

```http
GET /api/v1/users/{userId}
```

**説明**: 指定されたIDのユーザー詳細を取得します。

**認証**: 必要

**パスパラメータ**:

| パラメータ | 型 | 必須 | 説明 |
|----------|---|------|------|
| `userId` | string | Yes | ユーザーID |

**レスポンス（200 OK）**:
```json
{
  "data": {
    "id": "user-123",
    "email": "user@example.com",
    "name": "John Doe",
    "role": "user",
    "status": "active",
    "profile": {
      "avatar": "https://cdn.example.com/avatars/user-123.jpg",
      "bio": "Software Engineer",
      "location": "Tokyo, Japan"
    },
    "createdAt": "2025-01-01T00:00:00Z",
    "updatedAt": "2025-01-15T10:00:00Z",
    "lastLoginAt": "2025-01-15T09:00:00Z"
  }
}
```

**エラーレスポンス**:

`404 Not Found`:
```json
{
  "error": {
    "code": "NOT_FOUND",
    "message": "ユーザーが見つかりません"
  }
}
```

---

#### ユーザー作成 / Create User

```http
POST /api/v1/users
```

**説明**: 新しいユーザーを作成します。

**認証**: 必要

**権限**: `admin`

**リクエストボディ**:
```json
{
  "email": "newuser@example.com",
  "name": "New User",
  "password": "password123",
  "role": "user"
}
```

**リクエストパラメータ**:

| フィールド | 型 | 必須 | 説明 | バリデーション |
|----------|---|------|------|--------------|
| `email` | string | Yes | メールアドレス | Email形式、一意 |
| `name` | string | Yes | 氏名 | 1-100文字 |
| `password` | string | Yes | パスワード | 8-100文字、英数字記号 |
| `role` | string | No | ロール（デフォルト: `user`） | `admin`, `manager`, `user` |

**レスポンス（201 Created）**:
```json
{
  "data": {
    "id": "user-125",
    "email": "newuser@example.com",
    "name": "New User",
    "role": "user",
    "status": "active",
    "createdAt": "2025-01-15T10:00:00Z",
    "updatedAt": "2025-01-15T10:00:00Z"
  }
}
```

**エラーレスポンス**:

`422 Unprocessable Entity`:
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "入力データが不正です",
    "details": [
      {
        "field": "email",
        "message": "このメールアドレスは既に使用されています"
      }
    ]
  }
}
```

---

#### ユーザー更新 / Update User

```http
PUT /api/v1/users/{userId}
```

または

```http
PATCH /api/v1/users/{userId}
```

**説明**: ユーザー情報を更新します。

**認証**: 必要

**権限**: 自分自身または `admin`

**パスパラメータ**:

| パラメータ | 型 | 必須 | 説明 |
|----------|---|------|------|
| `userId` | string | Yes | ユーザーID |

**リクエストボディ（部分更新）**:
```json
{
  "name": "Updated Name",
  "profile": {
    "bio": "Updated bio"
  }
}
```

**レスポンス（200 OK）**:
```json
{
  "data": {
    "id": "user-123",
    "email": "user@example.com",
    "name": "Updated Name",
    "role": "user",
    "status": "active",
    "profile": {
      "avatar": "https://cdn.example.com/avatars/user-123.jpg",
      "bio": "Updated bio",
      "location": "Tokyo, Japan"
    },
    "updatedAt": "2025-01-15T11:00:00Z"
  }
}
```

---

#### ユーザー削除 / Delete User

```http
DELETE /api/v1/users/{userId}
```

**説明**: ユーザーを削除します（論理削除）。

**認証**: 必要

**権限**: `admin`

**パスパラメータ**:

| パラメータ | 型 | 必須 | 説明 |
|----------|---|------|------|
| `userId` | string | Yes | ユーザーID |

**レスポンス（204 No Content）**:
```
（ボディなし）
```

**エラーレスポンス**:

`403 Forbidden`:
```json
{
  "error": {
    "code": "FORBIDDEN",
    "message": "この操作を実行する権限がありません"
  }
}
```

---

### リソース管理 / Resource Management

#### リソース一覧取得 / Get Resources

```http
GET /api/v1/resources
```

**説明**: リソースの一覧を取得します。

**認証**: 必要

**クエリパラメータ**:

| パラメータ | 型 | 必須 | デフォルト | 説明 |
|----------|---|------|----------|------|
| `page` | integer | No | 1 | ページ番号 |
| `perPage` | integer | No | 20 | 1ページあたりの件数 |
| `filter[category]` | string | No | - | カテゴリフィルタ |
| `filter[status]` | string | No | - | ステータスフィルタ |

**レスポンス（200 OK）**:
```json
{
  "data": [
    {
      "id": "res-001",
      "name": "Resource 1",
      "category": "documents",
      "status": "active",
      "createdAt": "2025-01-15T10:00:00Z"
    }
  ],
  "meta": {
    "total": 50,
    "page": 1,
    "perPage": 20,
    "totalPages": 3
  }
}
```

---

## 🔄 Webhook

### Webhook イベント / Webhook Events

| イベント | 説明 | ペイロード例 |
|---------|------|------------|
| `user.created` | ユーザー作成時 | ユーザーオブジェクト |
| `user.updated` | ユーザー更新時 | ユーザーオブジェクト |
| `user.deleted` | ユーザー削除時 | ユーザーID |

### Webhook設定 / Webhook Configuration

```http
POST /api/v1/webhooks
```

**リクエストボディ**:
```json
{
  "url": "https://your-app.com/webhook",
  "events": ["user.created", "user.updated"],
  "secret": "your-webhook-secret"
}
```

### Webhook ペイロード / Webhook Payload

```json
{
  "id": "evt-123",
  "event": "user.created",
  "timestamp": "2025-01-15T10:00:00Z",
  "data": {
    "id": "user-123",
    "email": "user@example.com",
    "name": "John Doe"
  }
}
```

---

## 📝 コードサンプル / Code Samples

### JavaScript (Fetch API)

```javascript
// ログイン
const login = async (email, password) => {
  const response = await fetch('https://api.example.com/api/v1/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ email, password }),
  });
  
  if (!response.ok) {
    throw new Error('Login failed');
  }
  
  const data = await response.json();
  return data.data.accessToken;
};

// ユーザー一覧取得
const getUsers = async (token, page = 1) => {
  const response = await fetch(
    `https://api.example.com/api/v1/users?page=${page}`,
    {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    }
  );
  
  const data = await response.json();
  return data.data;
};
```

### Python (requests)

```python
import requests

# ログイン
def login(email, password):
    response = requests.post(
        'https://api.example.com/api/v1/auth/login',
        json={'email': email, 'password': password}
    )
    response.raise_for_status()
    return response.json()['data']['accessToken']

# ユーザー一覧取得
def get_users(token, page=1):
    headers = {'Authorization': f'Bearer {token}'}
    response = requests.get(
        f'https://api.example.com/api/v1/users?page={page}',
        headers=headers
    )
    response.raise_for_status()
    return response.json()['data']
```

### cURL

```bash
# ログイン
curl -X POST https://api.example.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'

# ユーザー一覧取得
curl -X GET https://api.example.com/api/v1/users \
  -H "Authorization: Bearer {access_token}"
```

---

## 📚 付録 / Appendix

### データモデル / Data Models

#### User オブジェクト

```typescript
interface User {
  id: string;
  email: string;
  name: string;
  role: 'admin' | 'manager' | 'user';
  status: 'active' | 'inactive' | 'suspended';
  profile?: {
    avatar?: string;
    bio?: string;
    location?: string;
  };
  createdAt: string; // ISO 8601
  updatedAt: string; // ISO 8601
  lastLoginAt?: string; // ISO 8601
}
```

---

## 📞 サポート / Support

### お問い合わせ / Contact
- **Email**: api-support@example.com
- **Slack**: #api-support
- **ドキュメント**: https://docs.example.com

### 変更履歴 / Changelog
最新の変更履歴は [CHANGELOG.md](./CHANGELOG.md) を参照してください。

---

## 📝 改訂履歴 / Revision History

| バージョン | 日付 | 変更内容 | 担当者 |
|----------|------|---------|--------|
| 1.0.0 | 2025-01-15 | 初版作成 | API Team |

