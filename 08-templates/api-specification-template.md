---
title: "API仕様書テンプレート"
version: "1.0.0"
last_updated: "2025-10-28"
status: "Active"
template_type: "Technical Specification"
---

# [API名] 仕様書

> [APIの目的と概要を1-2行で記述]

**API Version**: v1.0.0  
**Document Version**: 1.0.0  
**Last Updated**: 2025-10-28  
**Status**: Draft / In Review / Approved

---

## 📖 目次

1. [概要](#概要)
2. [認証](#認証)
3. [エンドポイント](#エンドポイント)
4. [データモデル](#データモデル)
5. [エラーコード](#エラーコード)
6. [レート制限](#レート制限)
7. [バージョニング](#バージョニング)
8. [変更履歴](#変更履歴)

---

## 📖 概要

### API の目的

[このAPIが提供する機能と解決する問題を記述]

### ベースURL

- **Production**: `https://api.example.com/v1`
- **Staging**: `https://staging-api.example.com/v1`
- **Development**: `http://localhost:3000/api/v1`

### プロトコル

- **Protocol**: HTTPS (TLS 1.2+)
- **Content-Type**: `application/json`
- **Character Encoding**: UTF-8

---

## 🔐 認証

### 認証方式

このAPIは **OAuth 2.0** および **JWT (JSON Web Token)** を使用した認証をサポートしています。

#### OAuth 2.0 フロー

```mermaid
sequenceDiagram
    participant Client
    participant AuthServer
    participant ResourceServer
    
    Client->>AuthServer: 1. 認証リクエスト
    AuthServer->>Client: 2. Authorization Code
    Client->>AuthServer: 3. Token Request
    AuthServer->>Client: 4. Access Token
    Client->>ResourceServer: 5. API Request (with Token)
    ResourceServer->>Client: 6. API Response
```

#### トークン取得

**エンドポイント**: `POST /auth/token`

**リクエスト**:

```http
POST /auth/token HTTP/1.1
Host: api.example.com
Content-Type: application/json

{
  "grant_type": "password",
  "username": "user@example.com",
  "password": "your_password",
  "client_id": "your_client_id",
  "client_secret": "your_client_secret"
}
```

**レスポンス**:

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "scope": "read write"
}
```

#### トークンの使用

全てのAPIリクエストには、Authorizationヘッダーにトークンを含める必要があります。

```http
GET /api/v1/users HTTP/1.1
Host: api.example.com
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### トークンリフレッシュ

**エンドポイント**: `POST /auth/refresh`

**リクエスト**:

```json
{
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## 📡 エンドポイント

### ユーザー管理

#### ユーザー一覧取得

**エンドポイント**: `GET /users`

**説明**: システムに登録されているユーザーの一覧を取得します。

**認証**: 必須

**パラメータ**:

| パラメータ | 型 | 必須 | デフォルト | 説明 |
|----------|-----|------|----------|------|
| page | integer | No | 1 | ページ番号 |
| limit | integer | No | 20 | 1ページあたりの件数(最大100) |
| sort | string | No | created_at | ソート項目 (created_at, name, email) |
| order | string | No | desc | ソート順 (asc, desc) |
| status | string | No | all | ユーザーステータス (active, inactive, all) |
| search | string | No | - | 検索キーワード(名前、メールで検索) |

**リクエスト例**:

```bash
curl -X GET "https://api.example.com/v1/users?page=1&limit=20&status=active" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**レスポンス**: `200 OK`

```json
{
  "data": [
    {
      "id": "user_123abc",
      "email": "john.doe@example.com",
      "name": "John Doe",
      "status": "active",
      "role": "user",
      "avatar_url": "https://example.com/avatars/user_123abc.jpg",
      "created_at": "2025-01-15T10:30:00Z",
      "updated_at": "2025-10-20T14:22:00Z"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "total": 150,
    "total_pages": 8
  }
}
```

**エラーレスポンス**:

- `401 Unauthorized`: 認証トークンが無効
- `403 Forbidden`: アクセス権限なし
- `429 Too Many Requests`: レート制限超過

---

#### ユーザー詳細取得

**エンドポイント**: `GET /users/:id`

**説明**: 指定されたIDのユーザー詳細情報を取得します。

**認証**: 必須

**パスパラメータ**:

| パラメータ | 型 | 説明 |
|----------|-----|------|
| id | string | ユーザーID |

**リクエスト例**:

```bash
curl -X GET "https://api.example.com/v1/users/user_123abc" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**レスポンス**: `200 OK`

```json
{
  "id": "user_123abc",
  "email": "john.doe@example.com",
  "name": "John Doe",
  "status": "active",
  "role": "user",
  "avatar_url": "https://example.com/avatars/user_123abc.jpg",
  "bio": "Software Engineer",
  "location": "Tokyo, Japan",
  "website": "https://johndoe.com",
  "social": {
    "twitter": "@johndoe",
    "github": "johndoe"
  },
  "created_at": "2025-01-15T10:30:00Z",
  "updated_at": "2025-10-20T14:22:00Z",
  "last_login_at": "2025-10-28T09:15:00Z"
}
```

**エラーレスポンス**:

- `404 Not Found`: ユーザーが存在しない

---

#### ユーザー作成

**エンドポイント**: `POST /users`

**説明**: 新しいユーザーを作成します。

**認証**: 必須(管理者権限)

**リクエストボディ**:

```json
{
  "email": "newuser@example.com",
  "name": "New User",
  "password": "SecurePassword123!",
  "role": "user",
  "status": "active"
}
```

**バリデーション**:

| フィールド | ルール |
|----------|--------|
| email | 必須、メールアドレス形式、ユニーク |
| name | 必須、2-100文字 |
| password | 必須、8文字以上、英数字記号を含む |
| role | オプション、user/admin/moderator |
| status | オプション、active/inactive |

**レスポンス**: `201 Created`

```json
{
  "id": "user_456def",
  "email": "newuser@example.com",
  "name": "New User",
  "status": "active",
  "role": "user",
  "created_at": "2025-10-28T10:30:00Z"
}
```

**エラーレスポンス**:

- `400 Bad Request`: バリデーションエラー
- `409 Conflict`: メールアドレスが既に存在

---

#### ユーザー更新

**エンドポイント**: `PUT /users/:id`

**説明**: 既存のユーザー情報を更新します。

**認証**: 必須(本人または管理者)

**パスパラメータ**:

| パラメータ | 型 | 説明 |
|----------|-----|------|
| id | string | ユーザーID |

**リクエストボディ**:

```json
{
  "name": "Updated Name",
  "bio": "Updated bio",
  "location": "Osaka, Japan",
  "website": "https://updated-site.com"
}
```

**レスポンス**: `200 OK`

```json
{
  "id": "user_123abc",
  "email": "john.doe@example.com",
  "name": "Updated Name",
  "bio": "Updated bio",
  "location": "Osaka, Japan",
  "website": "https://updated-site.com",
  "updated_at": "2025-10-28T11:00:00Z"
}
```

---

#### ユーザー削除

**エンドポイント**: `DELETE /users/:id`

**説明**: 指定されたユーザーを削除します(論理削除)。

**認証**: 必須(管理者権限)

**パスパラメータ**:

| パラメータ | 型 | 説明 |
|----------|-----|------|
| id | string | ユーザーID |

**レスポンス**: `204 No Content`

**エラーレスポンス**:

- `404 Not Found`: ユーザーが存在しない
- `403 Forbidden`: 削除権限なし


---

## 📊 データモデル

### User

ユーザー情報を表すモデル。

```json
{
  "id": "string",
  "email": "string",
  "name": "string",
  "status": "active | inactive",
  "role": "user | admin | moderator",
  "avatar_url": "string | null",
  "bio": "string | null",
  "location": "string | null",
  "website": "string | null",
  "social": {
    "twitter": "string | null",
    "github": "string | null",
    "linkedin": "string | null"
  },
  "created_at": "string (ISO 8601)",
  "updated_at": "string (ISO 8601)",
  "last_login_at": "string (ISO 8601) | null"
}
```

**フィールド説明**:

| フィールド | 型 | 必須 | 説明 |
|----------|-----|------|------|
| id | string | Yes | ユーザーの一意識別子 |
| email | string | Yes | メールアドレス(ユニーク) |
| name | string | Yes | ユーザー名 |
| status | string | Yes | アカウントステータス |
| role | string | Yes | ユーザーロール |
| avatar_url | string\|null | No | プロフィール画像URL |
| bio | string\|null | No | 自己紹介(最大500文字) |
| location | string\|null | No | 所在地 |
| website | string\|null | No | ウェブサイトURL |
| social | object\|null | No | SNSアカウント情報 |
| created_at | string | Yes | 作成日時(ISO 8601形式) |
| updated_at | string | Yes | 更新日時(ISO 8601形式) |
| last_login_at | string\|null | No | 最終ログイン日時 |

---

### Pagination

ページネーション情報。

```json
{
  "page": "integer",
  "limit": "integer",
  "total": "integer",
  "total_pages": "integer"
}
```

---

### Error

エラーレスポンスの構造。

```json
{
  "error": {
    "code": "string",
    "message": "string",
    "details": "array | null"
  }
}
```

---

## ❌ エラーコード

### HTTPステータスコード

| コード | 説明 | 使用例 |
|-------|------|--------|
| 200 | OK | 成功レスポンス |
| 201 | Created | リソース作成成功 |
| 204 | No Content | 削除成功(レスポンスボディなし) |
| 400 | Bad Request | バリデーションエラー |
| 401 | Unauthorized | 認証失敗 |
| 403 | Forbidden | 権限不足 |
| 404 | Not Found | リソースが存在しない |
| 409 | Conflict | リソースの競合 |
| 422 | Unprocessable Entity | 処理不可能なエンティティ |
| 429 | Too Many Requests | レート制限超過 |
| 500 | Internal Server Error | サーバー内部エラー |
| 503 | Service Unavailable | サービス利用不可 |

### アプリケーションエラーコード

| エラーコード | HTTPステータス | 説明 |
|------------|--------------|------|
| AUTH_001 | 401 | 認証トークンが無効 |
| AUTH_002 | 401 | トークンの有効期限切れ |
| AUTH_003 | 403 | 権限不足 |
| VAL_001 | 400 | 必須フィールドが欠落 |
| VAL_002 | 400 | フィールド形式が不正 |
| VAL_003 | 422 | バリデーションエラー |
| RES_001 | 404 | リソースが存在しない |
| RES_002 | 409 | リソースが既に存在 |
| RATE_001 | 429 | レート制限超過 |
| SYS_001 | 500 | 内部サーバーエラー |
| SYS_002 | 503 | サービス利用不可 |

### エラーレスポンス例

**400 Bad Request**:

```json
{
  "error": {
    "code": "VAL_001",
    "message": "Validation failed",
    "details": [
      {
        "field": "email",
        "message": "Email is required"
      },
      {
        "field": "password",
        "message": "Password must be at least 8 characters"
      }
    ]
  }
}
```

**401 Unauthorized**:

```json
{
  "error": {
    "code": "AUTH_001",
    "message": "Invalid authentication token"
  }
}
```

**429 Too Many Requests**:

```json
{
  "error": {
    "code": "RATE_001",
    "message": "Rate limit exceeded. Please try again in 60 seconds.",
    "retry_after": 60
  }
}
```

---

## ⏱️ レート制限

### 制限内容

| ユーザータイプ | リクエスト数 | 期間 |
|-------------|-----------|------|
| 未認証 | 100 | 1時間 |
| 認証済みユーザー | 1,000 | 1時間 |
| プレミアムユーザー | 10,000 | 1時間 |

### レート制限ヘッダー

全てのAPIレスポンスには以下のヘッダーが含まれます:

```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1609459200
```

| ヘッダー | 説明 |
|---------|------|
| X-RateLimit-Limit | 期間内の最大リクエスト数 |
| X-RateLimit-Remaining | 残りリクエスト数 |
| X-RateLimit-Reset | リセット時刻(UNIXタイムスタンプ) |

### レート制限超過時

レート制限を超えた場合、`429 Too Many Requests` が返されます。

**Retry-After**ヘッダーで、次のリクエストが可能になるまでの秒数が示されます。

```http
HTTP/1.1 429 Too Many Requests
Retry-After: 3600
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1609459200
```

---

## 🔄 バージョニング

### バージョニング戦略

このAPIは **URLパスバージョニング** を採用しています。

**現在のバージョン**: v1

**URL形式**:
```
https://api.example.com/v1/users
```

### 後方互換性

- マイナーバージョンアップ(v1.1, v1.2等)は後方互換性を保証
- メジャーバージョンアップ(v2, v3等)では互換性を破壊する変更が含まれる可能性
- 旧バージョンは最低12ヶ月間サポート

### 非推奨化プロセス

1. **アナウンス**: 6ヶ月前に非推奨化を通知
2. **警告期間**: レスポンスヘッダーで警告
   ```http
   Deprecation: version="v1", date="2026-04-01"
   ```
3. **サポート終了**: 12ヶ月後にサポート終了

---

## 📝 変更履歴

### v1.0.0 (2025-10-28)

**追加**:
- ユーザー管理API実装
- OAuth 2.0認証実装
- レート制限機能実装

**変更**:
- なし

**削除**:
- なし

**非推奨**:
- なし

---

## 📚 関連ドキュメント

- [API Design Standards](../../02-architecture-standards/api-design-standards.md)
- [Authentication Guide](../../07-security-compliance/authentication-authorization.md)
- [Error Handling Guide](../../01-coding-standards/error-handling.md)

---

## 📞 サポート

### 質問・問題報告

- **Technical Support**: api-support@example.com
- **Documentation Issues**: docs@example.com
- **Security Issues**: security@example.com

---

**最終更新**: 2025-10-28  
**ドキュメントバージョン**: 1.0.0  
**APIバージョン**: v1.0.0  
**管理者**: API Team
