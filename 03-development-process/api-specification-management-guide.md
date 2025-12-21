---
title: "API Specification Management Guide - API仕様書管理ガイド"
version: "1.0.0"
created_date: "2025-11-12"
last_updated: "2025-11-12"
status: "Active"
owner: "Engineering Architecture Team"
category: "development-process"
phase: "Phase 2 (Design)"
---

# API仕様書管理ガイド（OpenAPI/Swagger）

> マイクロサービス環境でのAPI仕様書の統合管理とリポジトリ分散戦略

**対象読者**: 
- 🤖 自律型AIエージェント (Devin, Cursor等)
- 👤 バックエンドエンジニア
- 📐 アーキテクト
- 🔌 APIデザイナー

**目的**: 
- リポジトリ単位でSwagger/OpenAPI仕様を管理
- 統合Swaggerで全APIを一元参照
- `$ref`による参照で重複を排除

---

## 📚 目次

1. [アーキテクチャ概要](#アーキテクチャ概要)
2. [ディレクトリ構造](#ディレクトリ構造)
3. [統合Swagger仕様](#統合swagger仕様)
4. [各リポジトリのSwagger仕様](#各リポジトリのswagger仕様)
5. [共通コンポーネントの管理](#共通コンポーネントの管理)
6. [実装パターン](#実装パターン)
7. [ツールとワークフロー](#ツールとワークフロー)
8. [FAQ](#faq)

---

## 🏗️ アーキテクチャ概要

### 3層構造のAPI仕様管理

```
┌─────────────────────────────────────────────────────────┐
│  Layer 1: 統合Swagger（api-gateway または専用リポジトリ）│
│  - 全マイクロサービスのAPI統合ビュー                      │
│  - 外部向けAPI仕様の一元管理                             │
│  - $ref による各リポジトリSwaggerの参照                  │
├─────────────────────────────────────────────────────────┤
│  Layer 2: 各リポジトリのSwagger（サービス単位）          │
│  - user-service/docs/api/openapi.yaml                   │
│  - order-service/docs/api/openapi.yaml                  │
│  - product-service/docs/api/openapi.yaml                │
├─────────────────────────────────────────────────────────┤
│  Layer 3: 共通コンポーネント（api-common リポジトリ）     │
│  - 共通スキーマ（Error, Pagination等）                   │
│  - 共通パラメータ（認証ヘッダー等）                       │
│  - 共通レスポンス                                        │
└─────────────────────────────────────────────────────────┘
```

### 参照の流れ

```
統合Swagger (openapi.yaml)
   ├─→ $ref: user-service/docs/api/openapi.yaml
   ├─→ $ref: order-service/docs/api/openapi.yaml
   └─→ $ref: product-service/docs/api/openapi.yaml
           ├─→ $ref: api-common/schemas/error.yaml
           └─→ $ref: api-common/schemas/pagination.yaml
```

---

## 📂 ディレクトリ構造

### パターンA: API Gateway リポジトリで統合

**推奨**: API Gatewayを使用している場合

```
organization/
│
├── api-gateway/                          # API Gateway リポジトリ
│   ├── docs/
│   │   └── api/
│   │       ├── openapi.yaml              # 統合Swagger（メイン）
│   │       ├── README.md                 # API仕様書索引
│   │       └── generated/                # 自動生成ドキュメント
│   │           └── api-docs.html
│   └── ...
│
├── user-service/                         # ユーザーサービス
│   ├── docs/
│   │   ├── design/
│   │   │   └── phase-2a/
│   │   │       └── api/
│   │   │           └── api-specification.md  # 設計書（人間用）
│   │   └── api/
│   │       ├── openapi.yaml              # サービス固有Swagger
│   │       ├── README.md
│   │       └── schemas/                  # サービス固有スキーマ
│   │           ├── user.yaml
│   │           ├── profile.yaml
│   │           └── authentication.yaml
│   └── ...
│
├── order-service/                        # 注文サービス
│   ├── docs/
│   │   └── api/
│   │       ├── openapi.yaml              # サービス固有Swagger
│   │       └── schemas/
│   │           ├── order.yaml
│   │           ├── payment.yaml
│   │           └── shipment.yaml
│   └── ...
│
├── product-service/                      # 商品サービス
│   ├── docs/
│   │   └── api/
│   │       ├── openapi.yaml              # サービス固有Swagger
│   │       └── schemas/
│   │           ├── product.yaml
│   │           ├── category.yaml
│   │           └── inventory.yaml
│   └── ...
│
└── api-common/                           # API共通定義
    ├── schemas/
    │   ├── error.yaml                    # 共通エラー定義
    │   ├── pagination.yaml               # ページネーション
    │   ├── metadata.yaml                 # メタデータ
    │   └── health.yaml                   # ヘルスチェック
    ├── parameters/
    │   ├── auth-header.yaml              # 認証ヘッダー
    │   ├── pagination-params.yaml        # ページネーションパラメータ
    │   └── common-headers.yaml           # 共通ヘッダー
    └── responses/
        ├── error-responses.yaml          # 共通エラーレスポンス
        └── success-responses.yaml        # 共通成功レスポンス
```

### パターンB: 専用API仕様リポジトリで統合

**推奨**: API Gatewayがない場合、または仕様を独立管理したい場合

```
organization/
│
├── api-specifications/                   # 専用API仕様リポジトリ
│   ├── openapi.yaml                      # 統合Swagger（メイン）
│   ├── README.md
│   ├── services/                         # 各サービスへの参照
│   │   ├── user-service.yaml             # user-serviceへの参照定義
│   │   ├── order-service.yaml
│   │   └── product-service.yaml
│   ├── common/                           # 共通定義（api-commonと同じ）
│   │   ├── schemas/
│   │   ├── parameters/
│   │   └── responses/
│   └── generated/
│       └── api-docs.html
│
├── user-service/
│   └── docs/api/openapi.yaml
├── order-service/
│   └── docs/api/openapi.yaml
└── product-service/
    └── docs/api/openapi.yaml
```

---

## 📄 統合Swagger仕様

### openapi.yaml（統合Swagger - API Gateway）

```yaml
openapi: 3.0.3
info:
  title: 統合API仕様書
  description: |
    全マイクロサービスのAPI仕様を統合したドキュメント
    
    ## サービス一覧
    - **User Service**: ユーザー管理API
    - **Order Service**: 注文管理API
    - **Product Service**: 商品管理API
  version: 1.0.0
  contact:
    name: API Support
    email: api-support@example.com
  license:
    name: Proprietary

servers:
  - url: https://api.example.com/v1
    description: Production
  - url: https://api-staging.example.com/v1
    description: Staging
  - url: http://localhost:8080/v1
    description: Local Development

# タグでサービスを分類
tags:
  - name: User Service
    description: ユーザー管理API
  - name: Order Service
    description: 注文管理API
  - name: Product Service
    description: 商品管理API

# 統合パス定義（各サービスのOpenAPIを参照）
paths:
  # ========================================
  # User Service APIs
  # ========================================
  /users:
    $ref: '../user-service/docs/api/openapi.yaml#/paths/~1users'
  
  /users/{userId}:
    $ref: '../user-service/docs/api/openapi.yaml#/paths/~1users~1{userId}'
  
  /users/{userId}/profile:
    $ref: '../user-service/docs/api/openapi.yaml#/paths/~1users~1{userId}~1profile'
  
  # ========================================
  # Order Service APIs
  # ========================================
  /orders:
    $ref: '../order-service/docs/api/openapi.yaml#/paths/~1orders'
  
  /orders/{orderId}:
    $ref: '../order-service/docs/api/openapi.yaml#/paths/~1orders~1{orderId}'
  
  /orders/{orderId}/status:
    $ref: '../order-service/docs/api/openapi.yaml#/paths/~1orders~1{orderId}~1status'
  
  # ========================================
  # Product Service APIs
  # ========================================
  /products:
    $ref: '../product-service/docs/api/openapi.yaml#/paths/~1products'
  
  /products/{productId}:
    $ref: '../product-service/docs/api/openapi.yaml#/paths/~1products~1{productId}'
  
  /products/{productId}/inventory:
    $ref: '../product-service/docs/api/openapi.yaml#/paths/~1products~1{productId}~1inventory'

# 共通コンポーネント
components:
  # 共通スキーマ
  schemas:
    Error:
      $ref: '../api-common/schemas/error.yaml'
    
    Pagination:
      $ref: '../api-common/schemas/pagination.yaml'
    
    Metadata:
      $ref: '../api-common/schemas/metadata.yaml'
  
  # 共通パラメータ
  parameters:
    AuthorizationHeader:
      $ref: '../api-common/parameters/auth-header.yaml'
    
    PaginationLimit:
      $ref: '../api-common/parameters/pagination-params.yaml#/limit'
    
    PaginationOffset:
      $ref: '../api-common/parameters/pagination-params.yaml#/offset'
  
  # 共通レスポンス
  responses:
    UnauthorizedError:
      $ref: '../api-common/responses/error-responses.yaml#/UnauthorizedError'
    
    NotFoundError:
      $ref: '../api-common/responses/error-responses.yaml#/NotFoundError'
    
    ValidationError:
      $ref: '../api-common/responses/error-responses.yaml#/ValidationError'
  
  # セキュリティスキーム
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
      description: JWT認証トークン

# グローバルセキュリティ
security:
  - BearerAuth: []
```

### 統合Swagger生成スクリプト（オプション）

完全自動化したい場合は、スクリプトで統合Swaggerを生成：

```bash
#!/bin/bash
# scripts/generate-integrated-openapi.sh

# 各サービスのOpenAPIを収集
services=(
  "user-service"
  "order-service"
  "product-service"
)

# 統合OpenAPIのベース
cat > openapi.yaml << 'EOF'
openapi: 3.0.3
info:
  title: 統合API仕様書
  version: 1.0.0
paths:
EOF

# 各サービスのパスを統合
for service in "${services[@]}"; do
  echo "Processing $service..."
  # パス定義を抽出して統合（yamlツール使用）
  yq eval ".paths" "../$service/docs/api/openapi.yaml" >> openapi.yaml
done

echo "統合OpenAPI生成完了: openapi.yaml"
```

---

## 📋 各リポジトリのSwagger仕様

### user-service/docs/api/openapi.yaml

```yaml
openapi: 3.0.3
info:
  title: User Service API
  description: ユーザー管理サービスのAPI仕様
  version: 1.0.0
  contact:
    name: User Service Team
    email: user-service-team@example.com

servers:
  - url: http://localhost:8081/v1
    description: Local Development
  - url: https://user-service.example.com/v1
    description: Production

tags:
  - name: Users
    description: ユーザー管理
  - name: Profiles
    description: プロフィール管理
  - name: Authentication
    description: 認証・認可

paths:
  /users:
    get:
      summary: ユーザー一覧取得
      operationId: listUsers
      tags:
        - Users
      parameters:
        - $ref: '../../api-common/parameters/pagination-params.yaml#/limit'
        - $ref: '../../api-common/parameters/pagination-params.yaml#/offset'
        - name: role
          in: query
          description: ロールでフィルタ
          schema:
            type: string
            enum: [admin, user, guest]
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  data:
                    type: array
                    items:
                      $ref: './schemas/user.yaml'
                  pagination:
                    $ref: '../../api-common/schemas/pagination.yaml'
        '401':
          $ref: '../../api-common/responses/error-responses.yaml#/UnauthorizedError'
      security:
        - BearerAuth: []
    
    post:
      summary: ユーザー作成
      operationId: createUser
      tags:
        - Users
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: './schemas/user-create-request.yaml'
      responses:
        '201':
          description: 作成成功
          content:
            application/json:
              schema:
                $ref: './schemas/user.yaml'
        '400':
          $ref: '../../api-common/responses/error-responses.yaml#/ValidationError'
        '401':
          $ref: '../../api-common/responses/error-responses.yaml#/UnauthorizedError'
      security:
        - BearerAuth: []

  /users/{userId}:
    parameters:
      - name: userId
        in: path
        required: true
        description: ユーザーID
        schema:
          type: string
          format: uuid
    
    get:
      summary: ユーザー詳細取得
      operationId: getUser
      tags:
        - Users
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: './schemas/user.yaml'
        '404':
          $ref: '../../api-common/responses/error-responses.yaml#/NotFoundError'
      security:
        - BearerAuth: []
    
    put:
      summary: ユーザー更新
      operationId: updateUser
      tags:
        - Users
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: './schemas/user-update-request.yaml'
      responses:
        '200':
          description: 更新成功
          content:
            application/json:
              schema:
                $ref: './schemas/user.yaml'
        '404':
          $ref: '../../api-common/responses/error-responses.yaml#/NotFoundError'
      security:
        - BearerAuth: []
    
    delete:
      summary: ユーザー削除
      operationId: deleteUser
      tags:
        - Users
      responses:
        '204':
          description: 削除成功
        '404':
          $ref: '../../api-common/responses/error-responses.yaml#/NotFoundError'
      security:
        - BearerAuth: []

  /users/{userId}/profile:
    parameters:
      - name: userId
        in: path
        required: true
        schema:
          type: string
          format: uuid
    
    get:
      summary: プロフィール取得
      operationId: getUserProfile
      tags:
        - Profiles
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: './schemas/profile.yaml'
        '404':
          $ref: '../../api-common/responses/error-responses.yaml#/NotFoundError'
      security:
        - BearerAuth: []

components:
  schemas:
    # サービス固有のスキーマ定義は外部ファイルに分離
    User:
      $ref: './schemas/user.yaml'
    
    Profile:
      $ref: './schemas/profile.yaml'
    
    UserCreateRequest:
      $ref: './schemas/user-create-request.yaml'
    
    UserUpdateRequest:
      $ref: './schemas/user-update-request.yaml'
  
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
```

### user-service/docs/api/schemas/user.yaml

```yaml
type: object
description: ユーザーエンティティ
required:
  - id
  - email
  - username
  - role
  - createdAt
properties:
  id:
    type: string
    format: uuid
    description: ユーザーID
    example: "550e8400-e29b-41d4-a716-446655440000"
  
  email:
    type: string
    format: email
    description: メールアドレス
    example: "user@example.com"
  
  username:
    type: string
    minLength: 3
    maxLength: 50
    description: ユーザー名
    example: "john_doe"
  
  role:
    type: string
    enum: [admin, user, guest]
    description: ロール
    example: "user"
  
  status:
    type: string
    enum: [active, inactive, suspended]
    description: ステータス
    example: "active"
  
  profile:
    $ref: './profile.yaml'
  
  createdAt:
    type: string
    format: date-time
    description: 作成日時
    example: "2025-01-15T10:30:00Z"
  
  updatedAt:
    type: string
    format: date-time
    description: 更新日時
    example: "2025-01-15T10:30:00Z"
```

---

## 🔧 共通コンポーネントの管理

### api-common/schemas/error.yaml

```yaml
type: object
description: 共通エラーレスポンス
required:
  - code
  - message
properties:
  code:
    type: string
    description: エラーコード
    example: "INVALID_REQUEST"
  
  message:
    type: string
    description: エラーメッセージ
    example: "The request is invalid"
  
  details:
    type: array
    description: エラー詳細
    items:
      type: object
      properties:
        field:
          type: string
          description: エラーフィールド
          example: "email"
        message:
          type: string
          description: フィールド固有のエラーメッセージ
          example: "Email format is invalid"
  
  traceId:
    type: string
    format: uuid
    description: トレースID（デバッグ用）
    example: "a1b2c3d4-e5f6-4789-a012-3456789abcde"
  
  timestamp:
    type: string
    format: date-time
    description: エラー発生時刻
    example: "2025-01-15T10:30:00Z"
```

### api-common/schemas/pagination.yaml

```yaml
type: object
description: ページネーション情報
required:
  - limit
  - offset
  - total
properties:
  limit:
    type: integer
    minimum: 1
    maximum: 100
    description: 1ページあたりの件数
    example: 20
  
  offset:
    type: integer
    minimum: 0
    description: オフセット
    example: 0
  
  total:
    type: integer
    minimum: 0
    description: 総件数
    example: 150
  
  hasNext:
    type: boolean
    description: 次ページの有無
    example: true
  
  hasPrevious:
    type: boolean
    description: 前ページの有無
    example: false
```

### api-common/parameters/pagination-params.yaml

```yaml
limit:
  name: limit
  in: query
  description: 1ページあたりの件数
  required: false
  schema:
    type: integer
    minimum: 1
    maximum: 100
    default: 20

offset:
  name: offset
  in: query
  description: オフセット
  required: false
  schema:
    type: integer
    minimum: 0
    default: 0
```

### api-common/responses/error-responses.yaml

```yaml
UnauthorizedError:
  description: 認証エラー
  content:
    application/json:
      schema:
        $ref: '../schemas/error.yaml'
      example:
        code: "UNAUTHORIZED"
        message: "Authentication required"
        traceId: "a1b2c3d4-e5f6-4789-a012-3456789abcde"
        timestamp: "2025-01-15T10:30:00Z"

NotFoundError:
  description: リソースが見つからない
  content:
    application/json:
      schema:
        $ref: '../schemas/error.yaml'
      example:
        code: "NOT_FOUND"
        message: "Resource not found"
        traceId: "a1b2c3d4-e5f6-4789-a012-3456789abcde"
        timestamp: "2025-01-15T10:30:00Z"

ValidationError:
  description: バリデーションエラー
  content:
    application/json:
      schema:
        $ref: '../schemas/error.yaml'
      example:
        code: "VALIDATION_ERROR"
        message: "Validation failed"
        details:
          - field: "email"
            message: "Email format is invalid"
          - field: "username"
            message: "Username must be at least 3 characters"
        traceId: "a1b2c3d4-e5f6-4789-a012-3456789abcde"
        timestamp: "2025-01-15T10:30:00Z"
```

---

## 💡 実装パターン

### パターン1: Git Submodule による統合

**適用**: 各サービスが独立したリポジトリの場合

```bash
# api-gateway リポジトリで実行
cd api-gateway

# サービスリポジトリをサブモジュールとして追加
git submodule add https://github.com/org/user-service.git external/user-service
git submodule add https://github.com/org/order-service.git external/order-service
git submodule add https://github.com/org/product-service.git external/product-service

# 統合Swaggerから参照
# openapi.yaml 内で:
# $ref: './external/user-service/docs/api/openapi.yaml#/paths/~1users'
```

### パターン2: Monorepo による統合

**適用**: モノレポ構成の場合

```
monorepo/
├── services/
│   ├── user-service/
│   ├── order-service/
│   └── product-service/
├── api-specifications/
│   └── openapi.yaml  # 統合Swagger
└── api-common/
    └── schemas/
```

```yaml
# api-specifications/openapi.yaml
paths:
  /users:
    $ref: '../services/user-service/docs/api/openapi.yaml#/paths/~1users'
```

### パターン3: API仕様の公開リポジトリ

**適用**: API仕様のみを独立管理したい場合

```bash
# api-specifications リポジトリを作成
git init api-specifications

# 各サービスのOpenAPIをコピー（CI/CDで自動化）
mkdir -p services
cp ../user-service/docs/api/openapi.yaml services/user-service.yaml
cp ../order-service/docs/api/openapi.yaml services/order-service.yaml

# 統合Swaggerを作成
# openapi.yaml 内で:
# $ref: './services/user-service.yaml#/paths/~1users'
```

---

## 🛠️ ツールとワークフロー

### 推奨ツール

#### 1. Swagger UI（ドキュメント生成）

```yaml
# docker-compose.yml（API Gateway）
version: '3.8'
services:
  swagger-ui:
    image: swaggerapi/swagger-ui:latest
    ports:
      - "8080:8080"
    environment:
      SWAGGER_JSON: /docs/openapi.yaml
    volumes:
      - ./docs/api:/docs
```

アクセス: `http://localhost:8080`

#### 2. Redoc（美しいドキュメント）

```yaml
# docker-compose.yml
  redoc:
    image: redocly/redoc:latest
    ports:
      - "8081:80"
    environment:
      SPEC_URL: /docs/openapi.yaml
    volumes:
      - ./docs/api:/docs
```

#### 3. OpenAPI Generator（コード生成）

```bash
# クライアントコード生成
npx @openapitools/openapi-generator-cli generate \
  -i docs/api/openapi.yaml \
  -g typescript-axios \
  -o src/generated/api-client

# サーバースタブ生成
npx @openapitools/openapi-generator-cli generate \
  -i docs/api/openapi.yaml \
  -g nodejs-express-server \
  -o src/generated/server-stub
```

#### 4. Spectral（Linting）

```bash
# OpenAPIのリント
npx @stoplight/spectral-cli lint docs/api/openapi.yaml

# .spectral.yaml（ルール定義）
extends: [[spectral:oas, all]]
rules:
  operation-description: error
  operation-operationId: error
  operation-tags: error
```

### CI/CD統合

#### GitHub Actions（統合Swagger生成・検証）

```yaml
# .github/workflows/api-validation.yml
name: API Specification Validation

on:
  pull_request:
    paths:
      - 'docs/api/**'
      - 'services/*/docs/api/**'

jobs:
  validate:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
        with:
          submodules: recursive  # サブモジュールも取得
      
      - name: Validate OpenAPI
        uses: char0n/swagger-editor-validate@v1
        with:
          definition-file: docs/api/openapi.yaml
      
      - name: Lint OpenAPI
        run: |
          npm install -g @stoplight/spectral-cli
          spectral lint docs/api/openapi.yaml
      
      - name: Generate API Documentation
        run: |
          docker run --rm -v ${PWD}:/local \
            openapitools/openapi-generator-cli generate \
            -i /local/docs/api/openapi.yaml \
            -g html2 \
            -o /local/docs/api/generated
      
      - name: Upload Documentation
        uses: actions/upload-artifact@v3
        with:
          name: api-documentation
          path: docs/api/generated
```

### 自動同期スクリプト

```bash
#!/bin/bash
# scripts/sync-api-specs.sh
# 各サービスのOpenAPIを統合Swaggerに同期

set -e

echo "=== API仕様の同期を開始 ==="

# サブモジュール更新
git submodule update --remote

# 各サービスのOpenAPIをコピー
services=(
  "user-service"
  "order-service"
  "product-service"
)

for service in "${services[@]}"; do
  echo "Syncing $service..."
  cp "external/$service/docs/api/openapi.yaml" "services/$service.yaml"
done

# OpenAPI検証
echo "OpenAPI仕様を検証中..."
npx @stoplight/spectral-cli lint docs/api/openapi.yaml

# ドキュメント生成
echo "APIドキュメントを生成中..."
docker run --rm -v ${PWD}:/local \
  openapitools/openapi-generator-cli generate \
  -i /local/docs/api/openapi.yaml \
  -g html2 \
  -o /local/docs/api/generated

echo "=== 同期完了 ==="
```

---

## ❓ FAQ

### Q1: 統合Swaggerと各リポジトリSwagger、どちらを優先すべきですか？

**A**: **各リポジトリSwaggerが正**です。

- **各リポジトリSwagger**: 各チームが管理する**正式な仕様**
- **統合Swagger**: 各リポジトリSwaggerを**参照・統合**したビュー

**ワークフロー**:
1. 各チームが各リポジトリSwaggerを更新
2. CI/CDで統合Swaggerを自動生成
3. 統合Swaggerで全体を確認

### Q2: `$ref`の相対パスが機能しない場合は？

**A**: 以下を確認してください：

1. **Swagger UIのバージョン**: v4.0以降推奨
2. **相対パスの解決**: ファイルシステム上で正しく解決できるか確認
3. **HTTPサーバー経由**: ローカルファイルではなく、HTTPサーバー経由で参照

```bash
# HTTPサーバーで確認
cd api-gateway
python3 -m http.server 8000
# http://localhost:8000/docs/api/openapi.yaml にアクセス
```

4. **Bundling**: `$ref`を解決して1ファイルにまとめる

```bash
# swagger-cliでバンドル
npx swagger-cli bundle docs/api/openapi.yaml \
  --outfile docs/api/openapi-bundled.yaml \
  --type yaml
```

### Q3: 各サービスのSwaggerを独立して閲覧したい場合は？

**A**: 各サービスにSwagger UIを配置してください。

```yaml
# user-service/docker-compose.yml
version: '3.8'
services:
  user-service-api-docs:
    image: swaggerapi/swagger-ui:latest
    ports:
      - "8082:8080"
    environment:
      SWAGGER_JSON: /docs/openapi.yaml
    volumes:
      - ./docs/api:/docs
```

- **統合Swagger**: `http://localhost:8080`（全API）
- **User Service**: `http://localhost:8082`（User APIのみ）
- **Order Service**: `http://localhost:8083`（Order APIのみ）

### Q4: APIバージョニング戦略は？

**A**: **URL Versioning**を推奨します。

```yaml
servers:
  - url: https://api.example.com/v1
    description: Version 1
  - url: https://api.example.com/v2
    description: Version 2
```

**ディレクトリ構造**:
```
docs/api/
├── v1/
│   ├── openapi.yaml
│   └── schemas/
└── v2/
    ├── openapi.yaml
    └── schemas/
```

### Q5: 共通コンポーネントの変更管理は？

**A**: **Semantic Versioning**と**Breaking Change通知**を推奨します。

```yaml
# api-common/schemas/error.yaml
# version: 2.0.0
# changelog:
#   2.0.0: 'details'フィールドを配列から複雑オブジェクトに変更（Breaking）
#   1.1.0: 'traceId'フィールドを追加（Non-breaking）
#   1.0.0: 初版
```

**変更フロー**:
1. Breaking Change → メジャーバージョンアップ
2. 新フィールド追加 → マイナーバージョンアップ
3. バグ修正 → パッチバージョンアップ
4. 全サービスに通知（Slack、メール等）

---

## 📞 サポート

### 問い合わせ先
- **API仕様**: API Architecture Team
- **統合Swagger**: Platform Engineering Team
- **各サービスSwagger**: 各サービスチーム

### Slack チャンネル
- `#api-design` - API設計の相談
- `#api-specifications` - 仕様書管理の質問

---

**最終更新**: 2025-11-12  
**バージョン**: 1.0.0  
**フィードバック歓迎**: 改善提案をお待ちしています

---

## 📝 バージョン履歴

| バージョン | 日付 | 変更内容 |
|-----------|------|---------|
| 1.0.0 | 2025-11-12 | 初版リリース |

---

**このガイドは living document です。実践から得られた知見で継続的に改善されます。**
