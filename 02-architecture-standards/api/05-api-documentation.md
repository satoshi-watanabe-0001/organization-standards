## 4. APIドキュメンテーション

### 4.1 OpenAPI 3.0仕様書作成

#### 4.1.1 詳細なOpenAPIスキーマ定義

**Good: 包括的なOpenAPI仕様書**
```yaml
# OpenAPI 3.0.3 - 完全なAPI仕様書例
openapi: 3.0.3
info:
  title: Advanced Blog API
  version: 2.1.0
  description: |
    # ブログ管理API仕様書
    
    このAPIはブログシステムの完全な管理機能を提供します。
    
    ## 主要機能
    - ユーザー管理と認証
    - 投稿の作成、編集、公開
    - コメントシステム
    - タグとカテゴリ管理
    - メディアアップロード
    - 全文検索
    - アナリティクス
    
    ## 認証方式
    このAPIは以下の認証方式をサポートしています：
    - **JWT Bearer Token**: ユーザー認証用
    - **API Key**: アプリケーション認証用
    - **OAuth 2.0**: サードパーティ連携用
    
    ## レート制限
    未認証ユーザー: 100リクエスト/時間2
    認証済みユーザー: 1000リクエスト/時間
    プレミアムユーザー: 5000リクエスト/時間
    
    ## サポート
    - **ドキュメント**: https://docs.example.com/api
    - **サポート**: support@example.com
    - **ステータスページ**: https://status.example.com
    
  termsOfService: https://example.com/terms
  contact:
    name: API Support Team
    url: https://example.com/contact
    email: api-support@example.com
  license:
    name: MIT License
    url: https://opensource.org/licenses/MIT
    identifier: MIT
  
  x-api-id: blog-api-v2
  x-audience: external
  x-lifecycle-stage: production

# サーバー情報
servers:
  - url: https://api.example.com/v2
    description: Production Server
    variables:
      version:
        default: v2
        enum: [v1, v2, v2.1]
        description: API Version
  - url: https://staging-api.example.com/v2
    description: Staging Server
  - url: https://dev-api.example.com/v2
    description: Development Server
  - url: http://localhost:8000/api/v2
    description: Local Development Server

# タグ定義
tags:
  - name: Authentication
    description: |ユーザー認証とトークン管理
    externalDocs:
      description: 認証ガイド
      url: https://docs.example.com/auth
  
  - name: Users
    description: ユーザーアカウント管理
    externalDocs:
      url: https://docs.example.com/users
  
  - name: Posts
    description: ブログ投稿管理
    externalDocs:
      url: https://docs.example.com/posts
  
  - name: Comments
    description: コメント管理
  
  - name: Media
    description: メディアファイル管理
  
  - name: Search
    description: 検索機能
  
  - name: Analytics
    description: アナリティクスとレポート
  
  - name: Admin
    description: 管理者機能

# セキュリティスキーム定義
components:
  securitySchemes:
    # JWT Bearer認証
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
      description: |
        JWTトークンを使用した認証。
        
        トークンは/auth/loginエンドポイントから取得し、
        AuthorizationヘッダーにBearer {token}の形式で送信します。
        
        **トークン有効期限**: 15分
        **リフレッシュ必要**: あり
    
    # API Key認証
    ApiKeyAuth:
      type: apiKey
      in: header
      name: X-API-Key
      description: |
        API Keyを使用した認証。
        
        アプリケーション用の認証方式で、
        X-API-KeyヘッダーにAPIキーを設定して送信します。
        
        **取得方法**: ダッシュボードから発行
        **権限**: ユーザースコープに依存
    
    # OAuth 2.0
    OAuth2:
      type: oauth2
      description: |
        OAuth 2.0認証フロー。
        
        Authorization Code FlowとClient Credentials Flowをサポート。
        PKCE (Proof Key for Code Exchange)を強く推奮。
      flows:
        authorizationCode:
          authorizationUrl: https://auth.example.com/oauth/authorize
          tokenUrl: https://auth.example.com/oauth/token
          refreshUrl: https://auth.example.com/oauth/token
          scopes:
            read: 読み取り権限
            write: 書き込み権限
            admin: 管理者権限
            profile: プロファイル情報アクセス
            email: メールアドレスアクセス
        clientCredentials:
          tokenUrl: https://auth.example.com/oauth/token
          scopes:
            read: アプリケーション読み取り
            write: アプリケーション書き込み
  
  # パラメータ定義
  parameters:
    # ページネーションパラメータ
    PageParam:
      name: page
      in: query
      required: false
      description: ページ番号（1から開始）
      schema:
        type: integer
        minimum: 1
        maximum: 1000
        default: 1
      example: 1
    
    PerPageParam:
      name: per_page
      in: query
      required: false
      description: 1ページあたりの件数
      schema:
        type: integer
        minimum: 1
        maximum: 100
        default: 20
      example: 20
    
    SortParam:
      name: sort
      in: query
      required: false
      description: ソートキー（フィールド名:-は降順）
      schema:
        type: string
        pattern: '^[a-zA-Z_][a-zA-Z0-9_]*(:?-)?$'
      examples:
        asc:
          value: created_at
          description: 作成日昇順
        desc:
          value: -created_at
          description: 作成日降順
        title:
          value: title
          description: タイトル昇順
    
    # フィルタパラメータ
    SearchParam:
      name: search
      in: query
      required: false
      description: 全文検索キーワード
      schema:
        type: string
        minLength: 2
        maxLength: 100
      example: "JavaScript tutorial"
    
    # IDパラメータ
    UserIdParam:
      name: user_id
      in: path
      required: true
      description: ユーザーID
      schema:
        type: string
        format: uuid
      example: "123e4567-e89b-12d3-a456-426614174000"
    
    PostIdParam:
      name: post_id
      in: path
      required: true
      description: 投稿ID
      schema:
        type: string
        format: uuid
      example: "987fcdeb-51a2-43d1-b123-456789abcdef"
    
    # ヘッダーパラメータ
    AcceptLanguageHeader:
      name: Accept-Language
      in: header
      required: false
      description: 希望言語
      schema:
        type: string
        default: ja-JP
      examples:
        japanese:
          value: ja-JP
        english:
          value: en-US
        chinese:
          value: zh-CN
    
    IfMatchHeader:
      name: If-Match
      in: header
      required: false
      description: 楽観ロック用ETag
      schema:
        type: string
      example: '"686897696a7c876b7e"'
  
  # レスポンス定義
  responses:
    # 汎用エラーレスポンス
    BadRequest:
      description: リクエストが不正
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          examples:
            validation_error:
              summary: バリデーションエラー
              value:
                success: false
                error:
                  message: "Validation failed"
                  code: "VALIDATION_ERROR"
                  details:
                    title: ["This field is required"]
                    content: ["Content too short"]
                timestamp: "2023-12-01T10:00:00Z"
                request_id: "req_123456789"
            missing_field:
              summary: 必須フィールド欠如
              value:
                success: false
                error:
                  message: "Missing required field"
                  code: "MISSING_FIELD"
                  details: "Field 'email' is required"
                timestamp: "2023-12-01T10:00:00Z"
                request_id: "req_123456790"
    
    Unauthorized:
      description: 認証が必要
      headers:
        WWW-Authenticate:
          description: 認証方式の指定
          schema:
            type: string
          example: 'Bearer realm="api"'
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          examples:
            missing_token:
              summary: トークンなし
              value:
                success: false
                error:
                  message: "Authentication required"
                  code: "AUTHENTICATION_REQUIRED"
                  details: "Bearer token is required"
                timestamp: "2023-12-01T10:00:00Z"
                request_id: "req_123456791"
            invalid_token:
              summary: 無効トークン
              value:
                success: false
                error:
                  message: "Invalid or expired token"
                  code: "INVALID_TOKEN"
                timestamp: "2023-12-01T10:00:00Z"
                request_id: "req_123456792"
    
    Forbidden:
      description: アクセスが禁止されている
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          examples:
            insufficient_permissions:
              summary: 権限不足
              value:
                success: false
                error:
                  message: "Insufficient permissions"
                  code: "PERMISSION_DENIED"
                  details: "Required permission: post:delete"
                timestamp: "2023-12-01T10:00:00Z"
                request_id: "req_123456793"
    
    NotFound:
      description: リソースが見つからない
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          examples:
            resource_not_found:
              summary: リソース未発見
              value:
                success: false
                error:
                  message: "Resource not found"
                  code: "RESOURCE_NOT_FOUND"
                  details: "Post with ID '123' does not exist"
                timestamp: "2023-12-01T10:00:00Z"
                request_id: "req_123456794"
    
    Conflict:
      description: リソースの競合状態
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          examples:
            duplicate_resource:
              summary: 重複リソース
              value:
                success: false
                error:
                  message: "Resource already exists"
                  code: "DUPLICATE_RESOURCE"
                  details: "User with email 'user@example.com' already exists"
                timestamp: "2023-12-01T10:00:00Z"
                request_id: "req_123456795"
    
    TooManyRequests:
      description: レート制限超過
      headers:
        Retry-After:
          description: 再試行までの秒数
          schema:
            type: integer
          example: 60
        X-RateLimit-Limit:
          description: 制限値
          schema:
            type: integer
          example: 1000
        X-RateLimit-Remaining:
          description: 残り回数
          schema:
            type: integer
          example: 0
        X-RateLimit-Reset:
          description: リセット時刻（Unixタイムスタンプ）
          schema:
            type: integer
          example: 1701234000
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            success: false
            error:
              message: "Rate limit exceeded"
              code: "RATE_LIMIT_EXCEEDED"
              details: "You have exceeded the rate limit. Try again in 60 seconds"
            timestamp: "2023-12-01T10:00:00Z"
            request_id: "req_123456796"
    
    InternalServerError:
      description: サーバー内部エラー
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            success: false
            error:
              message: "Internal server error"
              code: "INTERNAL_ERROR"
              details: "An unexpected error occurred"
            timestamp: "2023-12-01T10:00:00Z"
            request_id: "req_123456797"
  
  # スキーマ定義
  schemas:
    # 共通スキーマ
    Timestamp:
      type: string
      format: date-time
      description: ISO 8601形式のタイムスタンプ
      example: "2023-12-01T10:00:00.000Z"
    
    UUID:
      type: string
      format: uuid
      description: UUID v4形式のID
      example: "123e4567-e89b-12d3-a456-426614174000"
    
    # ページネーションメタデータ
    PaginationMeta:
      type: object
      required:
        - page
        - per_page
        - total
        - pages
        - has_next
        - has_prev
      properties:
        page:
          type: integer
          minimum: 1
          description: 現在のページ番号
          example: 1
        per_page:
          type: integer
          minimum: 1
          maximum: 100
          description: 1ページあたりの件数
          example: 20
        total:
          type: integer
          minimum: 0
          description: 総件数
          example: 150
        pages:
          type: integer
          minimum: 1
          description: 総ページ数
          example: 8
        has_next:
          type: boolean
          description: 次ページの有無
          example: true
        has_prev:
          type: boolean
          description: 前ページの有無
          example: false
        next_page:
          type: integer
          nullable: true
          description: 次ページ番号
          example: 2
        prev_page:
          type: integer
          nullable: true
          description: 前ページ番号
          example: null
    
    # HATEOASリンク
    Links:
      type: object
      description: HATEOASリンク情報
      properties:
        self:
          type: string
          format: uri
          description: 自分自身へのリンク
          example: "/api/v2/posts/123"
        edit:
          type: string
          format: uri
          nullable: true
          description: 編集リンク（権限がある場合のみ）
          example: "/api/v2/posts/123"
        delete:
          type: string
          format: uri
          nullable: true
          description: 削除リンク（権限がある場合のみ）
          example: "/api/v2/posts/123"
        author:
          type: string
          format: uri
          description: 作者へのリンク
          example: "/api/v2/users/456"
        comments:
          type: string
          format: uri
          description: コメント一覧へのリンク
          example: "/api/v2/posts/123/comments"
    
    # エラーレスポンス
    ErrorResponse:
      type: object
      required:
        - success
        - error
        - timestamp
        - request_id
      properties:
        success:
          type: boolean
          enum: [false]
          description: リクエストの成功状態
          example: false
        error:
          type: object
          required:
            - message
            - code
          properties:
            message:
              type: string
              description: ユーザー向けエラーメッセージ
              example: "Validation failed"
            code:
              type: string
              description: エラーコード
              example: "VALIDATION_ERROR"
            details:
              oneOf:
                - type: string
                - type: object
                - type: array
              description: 詳細エラー情報
              example: "Field validation errors"
        timestamp:
          $ref: '#/components/schemas/Timestamp'
        request_id:
          type: string
          description: リクエストID（デバッグ用）
          example: "req_123456789"
    
    # 成功レスポンス
    SuccessResponse:
      type: object
      required:
        - success
        - message
        - timestamp
        - request_id
      properties:
        success:
          type: boolean
          enum: [true]
          description: リクエストの成功状態
          example: true
        message:
          type: string
          description: 成功メッセージ
          example: "Operation completed successfully"
        data:
          type: object
          description: レスポンスデータ
        meta:
          type: object
          description: メタデータ情報
        timestamp:
          $ref: '#/components/schemas/Timestamp'
        request_id:
          type: string
          description: リクエストID
          example: "req_123456789"
    
    # ユーザー関連スキーマ
    User:
      type: object
      required:
        - id
        - username
        - email
        - created_at
      properties:
        id:
          $ref: '#/components/schemas/UUID'
        username:
          type: string
          minLength: 3
          maxLength: 50
          pattern: '^[a-zA-Z0-9_-]+$'
          description: ユーザー名（英数字、アンダースコア、ハイフンのみ）
          example: "john_doe"
        email:
          type: string
          format: email
          maxLength: 255
          description: メールアドレス
          example: "john@example.com"
        first_name:
          type: string
          maxLength: 50
          nullable: true
          description: 名前
          example: "John"
        last_name:
          type: string
          maxLength: 50
          nullable: true
          description: 姓
          example: "Doe"
        avatar_url:
          type: string
          format: uri
          nullable: true
          description: アバター画像URL
          example: "https://cdn.example.com/avatars/john_doe.jpg"
        bio:
          type: string
          maxLength: 500
          nullable: true
          description: 自己紹介
          example: "Full-stack developer passionate about clean code"
        location:
          type: string
          maxLength: 100
          nullable: true
          description: 所在地
          example: "Tokyo, Japan"
        website:
          type: string
          format: uri
          nullable: true
          description: ウェブサイトURL
          example: "https://johndoe.dev"
        is_active:
          type: boolean
          description: アカウント有効状態
          example: true
        is_verified:
          type: boolean
          description: メール認証状態
          example: true
        created_at:
          $ref: '#/components/schemas/Timestamp'
        updated_at:
          $ref: '#/components/schemas/Timestamp'
        last_login:
          allOf:
            - $ref: '#/components/schemas/Timestamp'
            - nullable: true
          description: 最終ログイン日時
        profile_visibility:
          type: string
          enum: [public, private, friends_only]
          description: プロフィール公開設定
          example: "public"
        social_links:
          type: object
          nullable: true
          description: SNSリンク
          properties:
            twitter:
              type: string
              example: "@johndoe"
            github:
              type: string
              example: "johndoe"
            linkedin:
              type: string
              example: "john-doe-dev"
        preferences:
          type: object
          description: ユーザー設定
          properties:
            language:
              type: string
              enum: [ja, en, zh, ko]
              description: 表示言語
              example: "ja"
            timezone:
              type: string
              description: タイムゾーン
              example: "Asia/Tokyo"
            notifications:
              type: object
              properties:
                email:
                  type: boolean
                  example: true
                push:
                  type: boolean
                  example: false
        statistics:
          type: object
          readOnly: true
          description: ユーザー統計情報
          properties:
            posts_count:
              type: integer
              minimum: 0
              example: 42
            followers_count:
              type: integer
              minimum: 0
              example: 150
            following_count:
              type: integer
              minimum: 0
              example: 89
            likes_received:
              type: integer
              minimum: 0
              example: 1250
        links:
          $ref: '#/components/schemas/Links'
    
    UserCreate:
      type: object
      required:
        - username
        - email
        - password
      properties:
        username:
          type: string
          minLength: 3
          maxLength: 50
          pattern: '^[a-zA-Z0-9_-]+$'
          example: "new_user"
        email:
          type: string
          format: email
          maxLength: 255
          example: "newuser@example.com"
        password:
          type: string
          minLength: 8
          maxLength: 128
          description: パスワード（8文字以上、大小英字・数字・記号を含む）
          example: "SecurePass123!"
        first_name:
          type: string
          maxLength: 50
          example: "New"
        last_name:
          type: string
          maxLength: 50
          example: "User"
        agree_to_terms:
          type: boolean
          enum: [true]
          description: 利用規約への同意（必須）
          example: true
    
    UserUpdate:
      type: object
      properties:
        first_name:
          type: string
          maxLength: 50
          nullable: true
        last_name:
          type: string
          maxLength: 50
          nullable: true
        bio:
          type: string
          maxLength: 500
          nullable: true
        location:
          type: string
          maxLength: 100
          nullable: true
        website:
          type: string
          format: uri
          nullable: true
        profile_visibility:
          type: string
          enum: [public, private, friends_only]
        social_links:
          type: object
          nullable: true
          properties:
            twitter:
              type: string
              nullable: true
            github:
              type: string
              nullable: true
            linkedin:
              type: string
              nullable: true
        preferences:
          type: object
          properties:
            language:
              type: string
              enum: [ja, en, zh, ko]
            timezone:
              type: string
            notifications:
              type: object
              properties:
                email:
                  type: boolean
                push:
                  type: boolean
    
    # 投稿関連スキーマ
    Post:
      type: object
      required:
        - id
        - title
        - content
        - author
        - status
        - created_at
      properties:
        id:
          $ref: '#/components/schemas/UUID'
        title:
          type: string
          minLength: 1
          maxLength: 200
          description: 投稿タイトル
          example: "Getting Started with FastAPI"
        slug:
          type: string
          pattern: '^[a-z0-9-]+$'
          description: URL用スラッグ（自動生成）
          example: "getting-started-with-fastapi"
        content:
          type: string
          minLength: 1
          description: 投稿内容（Markdown形式）
          example: "# Introduction\n\nFastAPI is a modern web framework..."
        excerpt:
          type: string
          maxLength: 500
          nullable: true
          description: 抜粋（自動生成または手動設定）
          example: "Learn how to build APIs with FastAPI..."
        status:
          type: string
          enum: [draft, published, archived]
          description: 投稿ステータス
          example: "published"
        visibility:
          type: string
          enum: [public, private, password_protected]
          description: 公開設定
          example: "public"
        password:
          type: string
          nullable: true
          writeOnly: true
          description: パスワード保護用（visibility=password_protectedの場合）
        featured_image:
          type: string
          format: uri
          nullable: true
          description: アイキャッチ画像URL
          example: "https://cdn.example.com/images/fastapi-intro.jpg"
        author:
          $ref: '#/components/schemas/User'
        category:
          $ref: '#/components/schemas/Category'
        tags:
          type: array
          items:
            $ref: '#/components/schemas/Tag'
          description: 関連タグ
        view_count:
          type: integer
          minimum: 0
          readOnly: true
          description: 閲覧数
          example: 1250
        like_count:
          type: integer
          minimum: 0
          readOnly: true
          description: いいね数
          example: 89
        comment_count:
          type: integer
          minimum: 0
          readOnly: true
          description: コメント数
          example: 23
        reading_time:
          type: integer
          minimum: 1
          readOnly: true
          description: 推定読書時間（分）
          example: 5
        seo_title:
          type: string
          maxLength: 60
          nullable: true
          description: SEO用タイトル
          example: "FastAPI Tutorial: Complete Beginner's Guide"
        seo_description:
          type: string
          maxLength: 160
          nullable: true
          description: SEO用説明文
          example: "Learn FastAPI from scratch with this comprehensive tutorial"
        published_at:
          allOf:
            - $ref: '#/components/schemas/Timestamp'
            - nullable: true
          description: 公開日時
        created_at:
          $ref: '#/components/schemas/Timestamp'
        updated_at:
          $ref: '#/components/schemas/Timestamp'
        links:
          $ref: '#/components/schemas/Links'
    
    PostCreate:
      type: object
      required:
        - title
        - content
      properties:
        title:
          type: string
          minLength: 1
          maxLength: 200
          example: "My New Blog Post"
        content:
          type: string
          minLength: 1
          example: "This is the content of my blog post..."
        excerpt:
          type: string
          maxLength: 500
          nullable: true
        status:
          type: string
          enum: [draft, published]
          default: draft
        visibility:
          type: string
          enum: [public, private, password_protected]
          default: public
        password:
          type: string
          nullable: true
        featured_image:
          type: string
          format: uri
          nullable: true
        category_id:
          $ref: '#/components/schemas/UUID'
        tag_ids:
          type: array
          items:
            $ref: '#/components/schemas/UUID'
          description: タグIDリスト
        seo_title:
          type: string
          maxLength: 60
          nullable: true
        seo_description:
          type: string
          maxLength: 160
          nullable: true
        scheduled_publish_at:
          allOf:
            - $ref: '#/components/schemas/Timestamp'
            - nullable: true
          description: 予約公開日時
    
    PostUpdate:
      type: object
      properties:
        title:
          type: string
          minLength: 1
          maxLength: 200
        content:
          type: string
          minLength: 1
        excerpt:
          type: string
          maxLength: 500
          nullable: true
        status:
          type: string
          enum: [draft, published, archived]
        visibility:
          type: string
          enum: [public, private, password_protected]
        password:
          type: string
          nullable: true
        featured_image:
          type: string
          format: uri
          nullable: true
        category_id:
          $ref: '#/components/schemas/UUID'
        tag_ids:
          type: array
          items:
            $ref: '#/components/schemas/UUID'
        seo_title:
          type: string
          maxLength: 60
          nullable: true
        seo_description:
          type: string
          maxLength: 160
          nullable: true
    
    # カテゴリとタグ
    Category:
      type: object
      required:
        - id
        - name
        - slug
      properties:
        id:
          $ref: '#/components/schemas/UUID'
        name:
          type: string
          minLength: 1
          maxLength: 100
          description: カテゴリ名
          example: "Web Development"
        slug:
          type: string
          pattern: '^[a-z0-9-]+$'
          description: URL用スラッグ
          example: "web-development"
        description:
          type: string
          maxLength: 500
          nullable: true
          description: カテゴリ説明
          example: "Articles about web development technologies"
        color:
          type: string
          pattern: '^#[0-9A-Fa-f]{6}$'
          description: カテゴリカラー（HEX形式）
          example: "#3B82F6"
        icon:
          type: string
          nullable: true
          description: アイコン名
          example: "code"
        parent_id:
          allOf:
            - $ref: '#/components/schemas/UUID'
            - nullable: true
          description: 親カテゴリID（階層構造用）
        post_count:
          type: integer
          minimum: 0
          readOnly: true
          description: 所属投稿数
          example: 25
        created_at:
          $ref: '#/components/schemas/Timestamp'
        updated_at:
          $ref: '#/components/schemas/Timestamp'
    
    Tag:
      type: object
      required:
        - id
        - name
        - slug
      properties:
        id:
          $ref: '#/components/schemas/UUID'
        name:
          type: string
          minLength: 1
          maxLength: 50
          description: タグ名
          example: "JavaScript"
        slug:
          type: string
          pattern: '^[a-z0-9-]+$'
          description: URL用スラッグ
          example: "javascript"
        description:
          type: string
          maxLength: 200
          nullable: true
          description: タグ説明
          example: "JavaScript programming language"
        color:
          type: string
          pattern: '^#[0-9A-Fa-f]{6}$'
          description: タグカラー（HEX形式）
          example: "#F7DF1E"
        post_count:
          type: integer
          minimum: 0
          readOnly: true
          description: 使用投稿数
          example: 15
        created_at:
          $ref: '#/components/schemas/Timestamp'
    
    # コメント関連
    Comment:
      type: object
      required:
        - id
        - content
        - author
        - post_id
        - created_at
      properties:
        id:
          $ref: '#/components/schemas/UUID'
        content:
          type: string
          minLength: 1
          maxLength: 2000
          description: コメント内容
          example: "Great article! Very helpful."
        author:
          $ref: '#/components/schemas/User'
        post_id:
          $ref: '#/components/schemas/UUID'
        parent_id:
          allOf:
            - $ref: '#/components/schemas/UUID'
            - nullable: true
          description: 親コメントID（返信の場合）
        status:
          type: string
          enum: [pending, approved, rejected, spam]
          description: コメント承認状態
          example: "approved"
        like_count:
          type: integer
          minimum: 0
          readOnly: true
          description: いいね数
          example: 5
        reply_count:
          type: integer
          minimum: 0
          readOnly: true
          description: 返信数
          example: 2
        created_at:
          $ref: '#/components/schemas/Timestamp'
        updated_at:
          $ref: '#/components/schemas/Timestamp'
        links:
          $ref: '#/components/schemas/Links'
    
    CommentCreate:
      type: object
      required:
        - content
      properties:
        content:
          type: string
          minLength: 1
          maxLength: 2000
          example: "Thank you for this informative post!"
        parent_id:
          allOf:
            - $ref: '#/components/schemas/UUID'
            - nullable: true
          description: 返信先コメントID

# グローバルセキュリティ適用
security:
  - BearerAuth: []
  - ApiKeyAuth: []

# API エンドポイント定義
paths:
  # 認証エンドポイント
  /auth/login:
    post:
      tags: [Authentication]
      summary: ユーザーログイン
      description: |
        ユーザー名（またはメールアドレス）とパスワードでログインし、
        JWTアクセストークンとリフレッシュトークンを取得します。
        
        **セキュリティ機能:**
        - レート制限: 5回/分
        - ブルートフォース攻撃保護
        - ログイン履歴記録
        - デバイス識別
      operationId: loginUser
      security: []  # 認証不要
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - login
                - password
              properties:
                login:
                  type: string
                  description: ユーザー名またはメールアドレス
                  example: "john_doe"
                password:
                  type: string
                  format: password
                  description: パスワード
                  example: "mypassword123"
                remember_me:
                  type: boolean
                  description: ログイン状態を記憶する
                  default: false
                device_name:
                  type: string
                  description: デバイス名（オプション）
                  example: "iPhone 15"
            examples:
              username_login:
                summary: ユーザー名でログイン
                value:
                  login: "john_doe"
                  password: "mypassword123"
                  remember_me: true
              email_login:
                summary: メールアドレスでログイン
                value:
                  login: "john@example.com"
                  password: "mypassword123"
      responses:
        '200':
          description: ログイン成功
          headers:
            Set-Cookie:
              description: リフレッシュトークン用Cookie（HttpOnly）
              schema:
                type: string
              example: "refresh_token=eyJ...; Path=/; HttpOnly; Secure; SameSite=Strict"
          content:
            application/json:
              schema:
                allOf:
                  - $ref: '#/components/schemas/SuccessResponse'
                  - type: object
                    properties:
                      data:
                        type: object
                        properties:
                          access_token:
                            type: string
                            description: JWTアクセストークン
                            example: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                          refresh_token:
                            type: string
                            description: リフレッシュトークン
                            example: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                          token_type:
                            type: string
                            enum: ["bearer"]
                            example: "bearer"
                          expires_in:
                            type: integer
                            description: アクセストークン有効期限（秒）
                            example: 900
                          user:
                            $ref: '#/components/schemas/User'
              example:
                success: true
                message: "Login successful"
                data:
                  access_token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                  refresh_token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                  token_type: "bearer"
                  expires_in: 900
                  user:
                    id: "123e4567-e89b-12d3-a456-426614174000"
                    username: "john_doe"
                    email: "john@example.com"
                timestamp: "2023-12-01T10:00:00.000Z"
                request_id: "req_123456789"
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          description: 認証失敗
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
              examples:
                invalid_credentials:
                  summary: 認証情報が無効
                  value:
                    success: false
                    error:
                      message: "Invalid credentials"
                      code: "INVALID_CREDENTIALS"
                      details: "Username or password is incorrect"
                    timestamp: "2023-12-01T10:00:00Z"
                    request_id: "req_123456789"
        '429':
          $ref: '#/components/responses/TooManyRequests'
        '500':
          $ref: '#/components/responses/InternalServerError'
  
  /auth/refresh:
    post:
      tags: [Authentication]
      summary: トークンリフレッシュ
      description: |
        リフレッシュトークンを使用して新しいアクセストークンを取得します。
        リフレッシュトークンはCookieまたはリクエストボディで送信できます。
      operationId: refreshToken
      security: []  # 認証不要（リフレッシュトークンで認証）
      requestBody:
        required: false
        content:
          application/json:
            schema:
              type: object
              properties:
                refresh_token:
                  type: string
                  description: リフレッシュトークン（Cookie未使用の場合）
                  example: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
      responses:
        '200':
          description: トークンリフレッシュ成功
          content:
            application/json:
              schema:
                allOf:
                  - $ref: '#/components/schemas/SuccessResponse'
                  - type: object
                    properties:
                      data:
                        type: object
                        properties:
                          access_token:
                            type: string
                            example: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                          token_type:
                            type: string
                            enum: ["bearer"]
                          expires_in:
                            type: integer
                            example: 900
        '401':
          description: リフレッシュトークンが無効
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
              example:
                success: false
                error:
                  message: "Invalid or expired refresh token"
                  code: "INVALID_REFRESH_TOKEN"
                timestamp: "2023-12-01T10:00:00Z"
                request_id: "req_123456789"
        '500':
          $ref: '#/components/responses/InternalServerError'
  
  /auth/logout:
    post:
      tags: [Authentication]
      summary: ログアウト
      description: |
        現在のセッションからログアウトし、トークンを無効化します。
        アクセストークンをブラックリストに追加し、リフレッシュトークンを削除します。
      operationId: logoutUser
      security:
        - BearerAuth: []
      requestBody:
        required: false
        content:
          application/json:
            schema:
              type: object
              properties:
                all_devices:
                  type: boolean
                  description: 全デバイスからログアウトするか
                  default: false
      responses:
        '200':
          description: ログアウト成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SuccessResponse'
              example:
                success: true
                message: "Logout successful"
                timestamp: "2023-12-01T10:00:00Z"
                request_id: "req_123456789"
        '401':
          $ref: '#/components/responses/Unauthorized'
        '500':
          $ref: '#/components/responses/InternalServerError'
  
  # ユーザー管理エンドポイント
  /users:
    get:
      tags: [Users]
      summary: ユーザー一覧取得
      description: |
        ページネーション、フィルタリング、ソート機能付きでユーザー一覧を取得します。
        
        **権限要件:** `user:list` または管理者権限
        **レート制限:** 100リクエスト/分
      operationId: listUsers
      security:
        - BearerAuth: []
        - ApiKeyAuth: []
      parameters:
        - $ref: '#/components/parameters/PageParam'
        - $ref: '#/components/parameters/PerPageParam'
        - $ref: '#/components/parameters/SortParam'
        - $ref: '#/components/parameters/SearchParam'
        - name: status
          in: query
          required: false
          description: ユーザーステータスフィルタ
          schema:
            type: string
            enum: [active, inactive, all]
            default: active
        - name: verified
          in: query
          required: false
          description: メール認証状態フィルタ
          schema:
            type: boolean
        - name: created_after
          in: query
          required: false
          description: 作成日時以降フィルタ
          schema:
            type: string
            format: date-time
        - name: role
          in: query
          required: false
          description: ロールフィルタ
          schema:
            type: string
            example: "author"
      responses:
        '200':
          description: ユーザー一覧取得成功
          headers:
            X-Total-Count:
              description: 総ユーザー数
              schema:
                type: integer
              example: 1500
            X-Page-Count:
              description: 総ページ数
              schema:
                type: integer
              example: 75
          content:
            application/json:
              schema:
                allOf:
                  - $ref: '#/components/schemas/SuccessResponse'
                  - type: object
                    properties:
                      data:
                        type: array
                        items:
                          $ref: '#/components/schemas/User'
                      meta:
                        type: object
                        properties:
                          pagination:
                            $ref: '#/components/schemas/PaginationMeta'
                          filters:
                            type: object
                            description: 適用されたフィルタ
                            properties:
                              status:
                                type: string
                              verified:
                                type: boolean
                              search:
                                type: string
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'
        '429':
          $ref: '#/components/responses/TooManyRequests'
        '500':
          $ref: '#/components/responses/InternalServerError'
    
    post:
      tags: [Users]
      summary: 新規ユーザー作成
      description: |
        新しいユーザーアカウントを作成します。
        
        **権限要件:** 公開エンドポイント（サインアップ）または `user:create` 権限
        **レート制限:** 5リクエスト/分（未認証）、20リクエスト/分（認証済み）
        
        **パスワード要件:**
        - 8文字以上
        - 大文字・小文字・数字を含む
        - 特殊文字を1つ以上含む
      operationId: createUser
      security:
        - BearerAuth: []
        - ApiKeyAuth: []
        - {}  # 未認証も許可（サインアップ用）
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UserCreate'
            examples:
              basic_signup:
                summary: 基本的なサインアップ
                value:
                  username: "newuser123"
                  email: "newuser@example.com"
                  password: "SecurePass123!"
                  first_name: "New"
                  last_name: "User"
                  agree_to_terms: true
              admin_create:
                summary: 管理者によるユーザー作成
                value:
                  username: "employee001"
                  email: "employee@company.com"
                  password: "TempPass123!"
                  first_name: "Employee"
                  last_name: "Name"
                  agree_to_terms: true
      responses:
        '201':
          description: ユーザー作成成功
          headers:
            Location:
              description: 作成されたユーザーのURL
              schema:
                type: string
                format: uri
              example: "/api/v2/users/123e4567-e89b-12d3-a456-426614174000"
          content:
            application/json:
              schema:
                allOf:
                  - $ref: '#/components/schemas/SuccessResponse'
                  - type: object
                    properties:
                      data:
                        $ref: '#/components/schemas/User'
              example:
                success: true
                message: "User created successfully"
                data:
                  id: "123e4567-e89b-12d3-a456-426614174000"
                  username: "newuser123"
                  email: "newuser@example.com"
                  first_name: "New"
                  last_name: "User"
                  is_active: true
                  is_verified: false
                  created_at: "2023-12-01T10:00:00.000Z"
                timestamp: "2023-12-01T10:00:00Z"
                request_id: "req_123456789"
        '400':
          $ref: '#/components/responses/BadRequest'
        '409':
          $ref: '#/components/responses/Conflict'
        '429':
          $ref: '#/components/responses/TooManyRequests'
        '500':
          $ref: '#/components/responses/InternalServerError'
  
  /users/{user_id}:
    parameters:
      - $ref: '#/components/parameters/UserIdParam'
    
    get:
      tags: [Users]
      summary: ユーザー詳細取得
      description: |
        指定されたユーザーの詳細情報を取得します。
        
        **権限要件:** 
        - 公開プロフィール: 認証不要
        - 非公開プロフィール: 本人または `user:read` 権限
        - 完全な詳細: 本人または管理者権限
      operationId: getUserById
      security:
        - BearerAuth: []
        - ApiKeyAuth: []
        - {}  # 公開プロフィールは認証不要
      parameters:
        - name: include
          in: query
          required: false
          description: 追加情報の取得
          schema:
            type: array
            items:
              type: string
              enum: [statistics, preferences, social_links]
          style: form
          explode: false
          example: ["statistics", "social_links"]
      responses:
        '200':
          description: ユーザー詳細取得成功
          headers:
            Cache-Control:
              description: キャッシュ制御
              schema:
                type: string
              example: "public, max-age=300"
            ETag:
              description: エンティティタグ
              schema:
                type: string
              example: '"686897696a7c876b7e"'
          content:
            application/json:
              schema:
                allOf:
                  - $ref: '#/components/schemas/SuccessResponse'
                  - type: object
                    properties:
                      data:
                        $ref: '#/components/schemas/User'
        '304':
          description: 未変更（ETagキャッシュヒット）
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'
        '404':
          $ref: '#/components/responses/NotFound'
        '500':
          $ref: '#/components/responses/InternalServerError'
    
    put:
      tags: [Users]
      summary: ユーザー情報更新（完全更新）
      description: |
        ユーザー情報を完全に更新します（全フィールド必須）。
        
        **権限要件:** 本人または `user:update` 権限
        **楽観的ロック:** If-Match ヘッダーでのETag検証をサポート
      operationId: updateUserComplete
      security:
        - BearerAuth: []
      parameters:
        - $ref: '#/components/parameters/IfMatchHeader'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UserUpdate'
      responses:
        '200':
          description: ユーザー更新成功
          headers:
            ETag:
              description: 新しいエンティティタグ
              schema:
                type: string
          content:
            application/json:
              schema:
                allOf:
                  - $ref: '#/components/schemas/SuccessResponse'
                  - type: object
                    properties:
                      data:
                        $ref: '#/components/schemas/User'
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'
        '404':
          $ref: '#/components/responses/NotFound'
        '409':
          $ref: '#/components/responses/Conflict'
        '412':
          description: 前提条件の失敗（ETag不整合）
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
              example:
                success: false
                error:
                  message: "Resource has been modified by another user"
                  code: "PRECONDITION_FAILED"
                  details: "Please refresh and try again"
                timestamp: "2023-12-01T10:00:00Z"
                request_id: "req_123456789"
        '500':
          $ref: '#/components/responses/InternalServerError'
    
    patch:
      tags: [Users]
      summary: ユーザー情報更新（部分更新）
      description: |
        ユーザー情報を部分的に更新します（指定フィールドのみ）。
        
        **権限要件:** 本人または `user:update` 権限
      operationId: updateUserPartial
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UserUpdate'
            examples:
              profile_update:
                summary: プロフィール更新
                value:
                  first_name: "Updated"
                  bio: "Updated bio information"
              preferences_update:
                summary: 設定更新
                value:
                  preferences:
                    language: "en"
                    notifications:
                      email: false
      responses:
        '200':
          description: ユーザー部分更新成功
          content:
            application/json:
              schema:
                allOf:
                  - $ref: '#/components/schemas/SuccessResponse'
                  - type: object
                    properties:
                      data:
                        $ref: '#/components/schemas/User'
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'
        '404':
          $ref: '#/components/responses/NotFound'
        '500':
          $ref: '#/components/responses/InternalServerError'
    
    delete:
      tags: [Users]
      summary: ユーザーアカウント削除
      description: |
        ユーザーアカウントを削除します（ソフト削除）。
        
        **権限要件:** 本人または `user:delete` 権限
        **注意:** この操作は取り消すことができません
      operationId: deleteUser
      security:
        - BearerAuth: []
      parameters:
        - name: permanent
          in: query
          required: false
          description: 物理削除を実行するか（管理者のみ）
          schema:
            type: boolean
            default: false
      responses:
        '204':
          description: ユーザー削除成功
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'
        '404':
          $ref: '#/components/responses/NotFound'
        '409':
          description: 削除できない状態
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
              example:
                success: false
                error:
                  message: "Cannot delete user with active content"
                  code: "DELETION_BLOCKED"
                  details: "User has published posts that need to be transferred or deleted first"
                timestamp: "2023-12-01T10:00:00Z"
                request_id: "req_123456789"
        '500':
          $ref: '#/components/responses/InternalServerError'