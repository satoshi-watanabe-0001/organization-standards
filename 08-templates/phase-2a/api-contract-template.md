---
title: "[プロジェクト名] API契約書 / API Contract"
version: "1.0.0"
template_version: "1.0.0"
created_date: "YYYY-MM-DD"
phase: "Phase 2A (Pre-Implementation Design)"
status: "draft"
owner: "[担当者名]"
reviewers: []
approved_by: ""
approved_date: ""
---

# API契約書: [プロジェクト名]

> **目的**: Phase 2Aで作成するAPI契約書の標準テンプレート  
> **スコープ**: エンドポイント一覧と基本的な入出力定義（詳細はPhase 5で作成）  
> **作成タイミング**: 実装開始の1-2日前  
> **作成時間目安**: 2-6時間（プロジェクト規模により）

---

## 📋 メタデータ / Metadata

| 項目 | 内容 |
|-----|------|
| **プロジェクト名** | [例: ユーザー認証マイクロサービス] |
| **プロジェクトコード** | [例: AUTH-SERVICE-001] |
| **作成日** | YYYY-MM-DD |
| **作成者** | [名前] - [役職] |
| **レビュアー** | [名前1], [名前2], [名前3] |
| **承認者** | [テックリード名] |
| **承認日** | YYYY-MM-DD |
| **ステータス** | `draft` / `in-review` / `approved` / `deprecated` |
| **関連ADR** | [ADR-YYYY-XXX: 技術スタック選定] |
| **関連ドキュメント** | [制約条件文書へのリンク] |

---

## 🎯 API概要 / API Overview

### 目的 / Purpose

[このAPIが解決する問題、提供する価値を記載]

**例**:
```
ユーザー認証マイクロサービスのAPIを提供し、以下を実現する：
- ユーザー登録・ログイン機能
- JWT ベースの認証・認可
- リフレッシュトークンによるセッション管理
- セキュアなパスワード管理
```

### スコープ / Scope

**対象範囲**:
- [機能1]
- [機能2]
- [機能3]

**対象外範囲**:
- [対象外の機能1]
- [対象外の機能2]

### ベースURL / Base URLs

| 環境 | URL | 用途 |
|------|-----|------|
| **開発環境** | `https://dev-api.example.com` | 開発・テスト |
| **ステージング** | `https://staging-api.example.com` | 受入テスト |
| **本番環境** | `https://api.example.com` | 本番サービス |

---

## 📡 エンドポイント一覧 / API Endpoints

### 📌 命名規則

```yaml
naming_convention:
  - RESTful な命名（リソース名は複数形）
  - ケバブケース使用: /api/v1/user-profiles
  - 動詞は HTTP メソッドで表現（URL には含めない）
  
examples:
  ✓ 良い例: GET /api/v1/users/{user_id}
  ✗ 悪い例: GET /api/v1/getUser?id={user_id}
```

---

### 1️⃣ 機能グループ: [認証 / Authentication]

#### 1.1 ユーザー登録

```yaml
endpoint: POST /api/v1/auth/register
summary: "新規ユーザーを登録し、アカウントを作成する"
description: |
  メールアドレスとパスワードで新規ユーザーを登録。
  登録完了後、確認メールを送信。

request:
  content_type: "application/json"
  headers:
    - "Content-Type: application/json"
    - "Accept: application/json"
  
  body_schema:
    required_fields:
      - email: string
        description: "メールアドレス（重複不可）"
        format: "email"
        example: "user@example.com"
      
      - password: string
        description: "パスワード（8文字以上、英数字記号を含む）"
        min_length: 8
        pattern: "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
        example: "SecurePass123!"
      
      - full_name: string
        description: "フルネーム"
        max_length: 100
        example: "田中太郎"
    
    optional_fields:
      - phone_number: string
        description: "電話番号（国際形式）"
        pattern: "^\\+[1-9]\\d{1,14}$"
        example: "+81-90-1234-5678"

response:
  success:
    status_code: 201
    description: "ユーザー作成成功"
    body_schema:
      - user_id: string (uuid)
        description: "作成されたユーザーID"
        example: "550e8400-e29b-41d4-a716-446655440000"
      
      - email: string
        description: "登録メールアドレス"
        example: "user@example.com"
      
      - full_name: string
        description: "フルネーム"
        example: "田中太郎"
      
      - created_at: string (datetime)
        description: "作成日時（ISO 8601形式）"
        example: "2025-11-20T12:00:00Z"
      
      - is_verified: boolean
        description: "メール確認済みフラグ"
        example: false
  
  error:
    - status_code: 400
      error_code: "VALIDATION_ERROR"
      description: "バリデーションエラー（メール形式不正、パスワード要件未達等）"
      example_response: |
        {
          "error": {
            "code": "VALIDATION_ERROR",
            "message": "バリデーションエラー",
            "details": [
              {
                "field": "email",
                "message": "メールアドレスの形式が不正です"
              },
              {
                "field": "password",
                "message": "パスワードは8文字以上で、英大文字・小文字・数字・記号を含む必要があります"
              }
            ]
          }
        }
    
    - status_code: 409
      error_code: "DUPLICATE_EMAIL"
      description: "メールアドレス重複"
      example_response: |
        {
          "error": {
            "code": "DUPLICATE_EMAIL",
            "message": "このメールアドレスは既に登録されています"
          }
        }
    
    - status_code: 500
      error_code: "INTERNAL_ERROR"
      description: "サーバーエラー"

authentication: "不要"
rate_limit: "10リクエスト/分/IP"
notes: |
  - パスワードは bcrypt（コストファクター12）でハッシュ化して保存
  - 登録完了後、確認メールを送信（非同期処理）
  - メール確認前でもログイン可能だが、一部機能は制限
```

---

#### 1.2 ログイン

```yaml
endpoint: POST /api/v1/auth/login
summary: "メールアドレスとパスワードでログインし、トークンを取得"
description: |
  認証情報を検証し、アクセストークンとリフレッシュトークンを発行。

request:
  content_type: "application/json"
  body_schema:
    required_fields:
      - email: string
        description: "登録済みメールアドレス"
        example: "user@example.com"
      
      - password: string
        description: "パスワード"
        example: "SecurePass123!"

response:
  success:
    status_code: 200
    description: "ログイン成功"
    body_schema:
      - access_token: string (jwt)
        description: "JWTアクセストークン（有効期限15分）"
        example: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
      
      - refresh_token: string (jwt)
        description: "リフレッシュトークン（有効期限7日）"
        example: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
      
      - token_type: string
        description: "トークンタイプ"
        example: "Bearer"
      
      - expires_in: integer
        description: "アクセストークンの有効期限（秒）"
        example: 900
      
      - user: object
        description: "ユーザー情報"
        fields:
          - user_id: string
          - email: string
          - full_name: string
  
  error:
    - status_code: 401
      error_code: "INVALID_CREDENTIALS"
      description: "認証失敗（メールアドレスまたはパスワードが不正）"
    
    - status_code: 403
      error_code: "ACCOUNT_LOCKED"
      description: "アカウントロック（ログイン試行回数超過）"
    
    - status_code: 429
      error_code: "RATE_LIMIT_EXCEEDED"
      description: "レート制限超過"

authentication: "不要"
rate_limit: "5リクエスト/分/IP"
notes: |
  - ログイン失敗5回でアカウントロック（30分間）
  - トークンはHTTPOnly Cookieでも返却可能（設定により）
```

---

#### 1.3 トークンリフレッシュ

```yaml
endpoint: POST /api/v1/auth/refresh
summary: "リフレッシュトークンを使用して新しいアクセストークンを取得"

request:
  content_type: "application/json"
  body_schema:
    required_fields:
      - refresh_token: string
        description: "有効なリフレッシュトークン"

response:
  success:
    status_code: 200
    body_schema:
      - access_token: string
        description: "新しいアクセストークン"
      - expires_in: integer
        description: "有効期限（秒）"
  
  error:
    - status_code: 401
      error_code: "INVALID_REFRESH_TOKEN"
      description: "リフレッシュトークンが無効または期限切れ"

authentication: "不要（リフレッシュトークンが必要）"
rate_limit: "20リクエスト/分/ユーザー"
```

---

#### 1.4 ログアウト

```yaml
endpoint: POST /api/v1/auth/logout
summary: "現在のセッションを無効化"

request:
  headers:
    - "Authorization: Bearer {access_token}"

response:
  success:
    status_code: 204
    description: "ログアウト成功（レスポンスボディなし）"
  
  error:
    - status_code: 401
      error_code: "UNAUTHORIZED"
      description: "認証トークンが無効"

authentication: "必須（Bearer Token）"
rate_limit: "10リクエスト/分/ユーザー"
notes: |
  - アクセストークンとリフレッシュトークンを無効化
  - リフレッシュトークンはブラックリストに追加（Redis）
```

---

### 2️⃣ 機能グループ: [ユーザー管理 / User Management]

#### 2.1 ユーザー情報取得

```yaml
endpoint: GET /api/v1/users/{user_id}
summary: "指定ユーザーの情報を取得"

path_parameters:
  - user_id: string (uuid)
    description: "ユーザーID"
    example: "550e8400-e29b-41d4-a716-446655440000"

request:
  headers:
    - "Authorization: Bearer {access_token}"

response:
  success:
    status_code: 200
    body_schema:
      - user_id: string
      - email: string
      - full_name: string
      - phone_number: string (nullable)
      - is_verified: boolean
      - created_at: string (datetime)
      - updated_at: string (datetime)
  
  error:
    - status_code: 401
      error_code: "UNAUTHORIZED"
    - status_code: 403
      error_code: "FORBIDDEN"
      description: "他のユーザー情報へのアクセス権限なし"
    - status_code: 404
      error_code: "USER_NOT_FOUND"

authentication: "必須"
rate_limit: "100リクエスト/分/ユーザー"
notes: |
  - 自分自身の情報は常にアクセス可能
  - 他ユーザーの情報は管理者権限が必要
```

---

#### 2.2 ユーザー情報更新

```yaml
endpoint: PATCH /api/v1/users/{user_id}
summary: "ユーザー情報を部分更新"

path_parameters:
  - user_id: string (uuid)

request:
  content_type: "application/json"
  headers:
    - "Authorization: Bearer {access_token}"
  
  body_schema:
    optional_fields:
      - full_name: string
      - phone_number: string

response:
  success:
    status_code: 200
    body_schema:
      - user_id: string
      - email: string
      - full_name: string
      - phone_number: string
      - updated_at: string
  
  error:
    - status_code: 400
      error_code: "VALIDATION_ERROR"
    - status_code: 401
      error_code: "UNAUTHORIZED"
    - status_code: 403
      error_code: "FORBIDDEN"

authentication: "必須"
rate_limit: "10リクエスト/分/ユーザー"
```

---

### 3️⃣ 追加エンドポイント（概要のみ）

| Method | Endpoint | Summary | Auth |
|--------|----------|---------|------|
| POST | /api/v1/auth/password-reset-request | パスワードリセット要求 | 不要 |
| POST | /api/v1/auth/password-reset | パスワードリセット実行 | 不要 |
| POST | /api/v1/auth/verify-email | メールアドレス確認 | 不要 |
| DELETE | /api/v1/users/{user_id} | ユーザーアカウント削除 | 必須 |
| GET | /api/v1/users | ユーザー一覧取得（管理者用） | 必須（Admin） |

**詳細**: Phase 5で完全版API仕様書に記載

---

## 🔐 認証・認可 / Authentication & Authorization

### 認証方式

```yaml
mechanism:
  type: "JWT (JSON Web Token)"
  format: "Bearer Token"
  
token_structure:
  access_token:
    type: "JWT"
    expiry: "15分"
    storage: "メモリ または sessionStorage（推奨）"
    claims:
      - sub: "ユーザーID"
      - email: "メールアドレス"
      - role: "ユーザーロール"
      - exp: "有効期限（Unix timestamp）"
  
  refresh_token:
    type: "JWT"
    expiry: "7日"
    storage: "HTTPOnly Cookie（推奨）または localStorage"
    claims:
      - sub: "ユーザーID"
      - token_id: "トークン識別子"
      - exp: "有効期限"
```

### 認証フロー

```
クライアント                     APIサーバー
    │                                │
    │ 1. POST /auth/login            │
    │ {email, password}              │
    │───────────────────────────────>│
    │                                │ 2. 認証情報検証
    │                                │ 3. JWT生成
    │                                │
    │<───────────────────────────────│
    │ {access_token, refresh_token}  │
    │                                │
    │ 4. GET /users/{id}             │
    │ Header: Authorization: Bearer {access_token}
    │───────────────────────────────>│
    │                                │ 5. トークン検証
    │                                │ 6. ユーザー情報取得
    │<───────────────────────────────│
    │ {user data}                    │
    │                                │
    │ (15分後、トークン期限切れ)       │
    │                                │
    │ 7. POST /auth/refresh          │
    │ {refresh_token}                │
    │───────────────────────────────>│
    │                                │ 8. リフレッシュトークン検証
    │                                │ 9. 新しいアクセストークン生成
    │<───────────────────────────────│
    │ {access_token}                 │
```

### 認可レベル

| レベル | 説明 | 対象エンドポイント |
|-------|------|------------------|
| `public` | 認証不要 | `/auth/register`, `/auth/login` |
| `authenticated` | ログイン必須 | `/users/{user_id}`, `/auth/logout` |
| `admin` | 管理者権限必須 | `/users` (一覧取得), `/admin/*` |

---

## 📊 共通仕様 / Common Specifications

### リクエストヘッダー

```yaml
required:
  - Content-Type: "application/json"
  - Accept: "application/json"

optional:
  - Authorization: "Bearer {token}"  # 認証が必要なエンドポイント
  - X-Request-ID: "{uuid}"           # トレーシング用（推奨）
  - Accept-Language: "ja-JP, en-US"  # 多言語対応時
```

### レスポンス形式

#### 成功レスポンス

```json
{
  "data": {
    // 実際のデータ
  },
  "meta": {
    "request_id": "550e8400-e29b-41d4-a716-446655440000",
    "timestamp": "2025-11-20T12:00:00Z"
  }
}
```

#### エラーレスポンス

```json
{
  "error": {
    "code": "ERROR_CODE",
    "message": "エラーメッセージ（ユーザー向け）",
    "details": [
      {
        "field": "email",
        "message": "メールアドレスが不正です"
      }
    ]
  },
  "meta": {
    "request_id": "550e8400-e29b-41d4-a716-446655440000",
    "timestamp": "2025-11-20T12:00:00Z"
  }
}
```

### HTTPステータスコード

| コード | 説明 | 使用場面 |
|-------|------|---------|
| **200** | OK | リソース取得成功 |
| **201** | Created | リソース作成成功 |
| **204** | No Content | 削除・更新成功（レスポンスボディなし） |
| **400** | Bad Request | バリデーションエラー |
| **401** | Unauthorized | 認証エラー（トークン無効・期限切れ） |
| **403** | Forbidden | 権限エラー（アクセス権限なし） |
| **404** | Not Found | リソース不存在 |
| **409** | Conflict | リソース競合（重複等） |
| **422** | Unprocessable Entity | セマンティックエラー |
| **429** | Too Many Requests | レート制限超過 |
| **500** | Internal Server Error | サーバーエラー |
| **503** | Service Unavailable | サービス一時停止（メンテナンス等） |

---

## ⚡ 非機能要件 / Non-Functional Requirements

### パフォーマンス

```yaml
response_time:
  target:
    average: "< 200ms"
    p95: "< 500ms"
    p99: "< 1秒"
  
  measurement:
    - 全エンドポイントでレスポンスタイム計測
    - CloudWatch/Prometheus等でモニタリング

throughput:
  target: "500 リクエスト/秒（ピーク時）"
  concurrent_users: "1,000ユーザー"
```

### 可用性

```yaml
sla: "99.9%（月間ダウンタイム 43.2分以内）"

maintenance_window:
  frequency: "毎週日曜日"
  time: "2:00-4:00 (JST)"
  duration: "最大2時間"
  notification: "48時間前に通知"
```

### レート制限

```yaml
default_limits:
  per_ip: "100リクエスト/分"
  per_user: "1,000リクエスト/分"

endpoint_specific:
  "/auth/login": "5リクエスト/分/IP"
  "/auth/register": "10リクエスト/分/IP"
  "/auth/refresh": "20リクエスト/分/ユーザー"

rate_limit_headers:
  - X-RateLimit-Limit: "リクエスト上限"
  - X-RateLimit-Remaining: "残りリクエスト数"
  - X-RateLimit-Reset: "リセット時刻（Unix timestamp）"

exceeded_response:
  status_code: 429
  error_code: "RATE_LIMIT_EXCEEDED"
  message: "レート制限を超過しました。しばらくしてから再試行してください"
  retry_after: "リセットまでの秒数"
```

---

## 🔄 バージョニング / Versioning

### バージョニング戦略

```yaml
strategy:
  type: "URLベースバージョニング"
  format: "/api/v{major_version}/"
  examples:
    - "/api/v1/users"
    - "/api/v2/users"

versioning_rules:
  major_version_change:
    - 破壊的変更（既存クライアントが動作しなくなる変更）
    - エンドポイントの削除
    - 必須パラメータの追加
    - レスポンス構造の大幅な変更
  
  minor_version_change:
    - 後方互換性のある変更
    - 新規エンドポイントの追加
    - オプショナルパラメータの追加
    - レスポンスフィールドの追加

backward_compatibility:
  maintenance_period: "最低6ヶ月"
  deprecation_process:
    - "新バージョンリリース"
    - "廃止予定の3ヶ月前に通知"
    - "レスポンスヘッダーに Deprecation: true を付与"
    - "6ヶ月後に旧バージョン停止"
```

### 現在のバージョン

```yaml
current_version: "v1"
release_date: "2025-11-20"
status: "active"
```

---

## 📝 制約事項 / Constraints

### 技術制約

```yaml
technical_constraints:
  - constraint: "実装言語・フレームワーク"
    details: "Python 3.11+ / FastAPI 0.100+"
    reason: "チーム技術スタック、非同期処理サポート"
  
  - constraint: "データベース"
    details: "PostgreSQL 15+"
    reason: "トランザクション管理、JSONBサポート"
  
  - constraint: "キャッシュ"
    details: "Redis 7+"
    reason: "トークンブラックリスト、レート制限管理"
```

### ビジネス制約

```yaml
business_constraints:
  - constraint: "リリース期限"
    details: "2025-12-31までに本番リリース"
    impact: "機能のスコープ調整が必要"
  
  - constraint: "多言語対応"
    details: "初期リリースは日本語・英語のみ"
    future_plan: "ユーザー需要に応じて追加"
```

### セキュリティ制約

```yaml
security_constraints:
  - constraint: "HTTPS必須"
    details: "全通信をTLS 1.3以上で暗号化"
  
  - constraint: "パスワードハッシュ化"
    details: "bcrypt（コストファクター12）"
  
  - constraint: "トークン有効期限"
    details: "アクセストークン15分、リフレッシュトークン7日"
    reason: "セキュリティとUXのバランス"
```

---

## ✅ レビューチェックリスト / Review Checklist

### 機能性

```markdown
- [ ] 全ての必要な機能がカバーされている
- [ ] エンドポイント命名がRESTfulである
- [ ] HTTPメソッドが適切である（GET, POST, PATCH, DELETE）
- [ ] パスパラメータ・クエリパラメータ・リクエストボディが明確
- [ ] レスポンス構造が一貫している
```

### セキュリティ

```markdown
- [ ] 認証・認可メカニズムが適切に設計されている
- [ ] センシティブ情報（パスワード等）の取り扱いが明確
- [ ] レート制限が定義されている
- [ ] エラーメッセージで内部情報を漏洩していない
- [ ] HTTPS通信が前提となっている
```

### パフォーマンス

```markdown
- [ ] レスポンスタイム目標が現実的
- [ ] レート制限が適切
- [ ] ページネーションが考慮されている（リスト取得系）
- [ ] キャッシュ戦略が検討されている
```

### 実装可能性

```markdown
- [ ] Phase 3で実装可能な粒度である
- [ ] 外部依存が明確である
- [ ] 非機能要件が実現可能である
- [ ] データモデルと整合性が取れている
```

### チーム合意

```markdown
- [ ] 関係チーム（フロントエンド、バックエンド、QA）とレビュー実施済み
- [ ] APIユーザー（クライアント側チーム）の承認済み
- [ ] テックリードの承認済み
- [ ] セキュリティチームのレビュー完了（必要な場合）
```

---

## 📚 関連ドキュメント / Related Documents

### Phase 2A（事前設計）

- **ADR**: [ADR-001: 技術スタック選定](../decisions/adr-001-tech-stack.md)
- **ADR**: [ADR-002: 認証メカニズム選定](../decisions/adr-002-auth-mechanism.md)
- **制約条件文書**: [constraints.md](constraints.md)

### Phase 5（実装ベース設計）- 実装後に作成

- **完全版API仕様書**: `api/specifications/openapi.yaml`（全エンドポイントの詳細）
- **APIドキュメント**: `api/api-documentation.md`（利用者向けガイド）
- **設計書**: `design-document.md`

---

## 📅 変更履歴 / Change History

| バージョン | 日付 | 変更内容 | 作成者 |
|----------|------|---------|--------|
| 1.0.0 | YYYY-MM-DD | 初版作成 | [名前] |
| 1.1.0 | YYYY-MM-DD | [変更内容] | [名前] |

---

## 📌 補足・メモ / Notes

### Phase 2A での留意点

```yaml
important_notes:
  - "詳細すぎる記述は不要（Phase 5で詳細化）"
  - "エンドポイント一覧と基本的なI/Oのみ記載"
  - "チーム間の契約合意が主目的"
  - "実装中に変更される可能性がある（Phase 5で確定版）"

recommended_tools:
  - "Swagger Editor（https://editor.swagger.io/）"
  - "Postman（API設計・テスト）"
  - "Stoplight Studio（API設計ツール）"
```

### 次のステップ

```markdown
1. [ ] このAPI契約書のレビュー実施
2. [ ] 関係チームとの合意取得
3. [ ] テックリードの承認取得
4. [ ] Phase 3（実装）開始
5. [ ] 実装完了後、Phase 5で完全版API仕様書に拡張
```

---

**このテンプレートは Phase 2A（事前設計）用です。Phase 3実装完了後、Phase 5（実装ベース設計）で完全版API仕様書（OpenAPI/Swagger）に拡張してください。**
