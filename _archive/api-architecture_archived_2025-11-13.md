# API設計標準文書 (API Architecture Standards)

**バージョン**: 1.0.0  
**最終更新日**: 2025-10-15  
**対象技術**: RESTful API, GraphQL, gRPC, WebSocket, OpenAPI 3.0+

---

## 目次

1. [API設計基本原則](#1-API設計基本原則)
2. [RESTful API設計標準](#2-RESTful-API設計標準)
3. [GraphQL API設計](#3-GraphQL-API設計)
4. [gRPC・高性能API設計](#4-gRPC・高性能API設計)
5. [API認証・認可](#5-API認証・認可)
6. [APIドキュメンテーション](#6-APIドキュメンテーション)
7. [エラーハンドリング・レスポンス設計](#7-エラーハンドリング・レスポンス設計)
8. [パフォーマンス・スケーラビリティ](#8-パフォーマンス・スケーラビリティ)
9. [API監視・運用](#9-API監視・運用)
10. [API設計パターン](#10-API設計パターン)
11. [セキュリティ・コンプライアンス](#11-セキュリティ・コンプライアンス)

---

## 1. API設計基本原則

### 1.1 設計哲学

#### 1.1.1 APIファースト設計原則

**Good: API中心設計アプローチ**
```yaml
# OpenAPI 3.0仕様書例（設計ファーストアプローチ）
openapi: 3.0.3
info:
  title: User Management API
  version: 1.0.0
  description: |
    Comprehensive user management system API
    
    Design Principles:
    - API-First: Design before implementation
    - Consistency: Uniform patterns across endpoints
    - Developer Experience: Clear, predictable interfaces
    - Versioning: Backward compatibility guaranteed
    - Performance: Optimized for scale
    
  contact:
    name: API Development Team
    email: api-team@company.com
    url: https://docs.company.com/apis
  
  license:
    name: MIT
    url: https://opensource.org/licenses/MIT

servers:
  - url: https://api.company.com/v1
    description: Production server
  - url: https://api-staging.company.com/v1
    description: Staging server
  - url: http://localhost:8000/v1
    description: Development server

# セキュリティスキーム
components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
    
    ApiKeyAuth:
      type: apiKey
      in: header
      name: X-API-Key
    
    OAuth2:
      type: oauth2
      flows:
        authorizationCode:
          authorizationUrl: https://auth.company.com/oauth/authorize
          tokenUrl: https://auth.company.com/oauth/token
          scopes:
            read: Read access
            write: Write access
            admin: Admin access

# グローバルセキュリティ
security:
  - BearerAuth: []
  - ApiKeyAuth: []

paths:
  /users:
    get:
      summary: List users
      description: |
        Retrieve a paginated list of users with optional filtering
        
        **Performance Notes:**
        - Default page size: 20
        - Maximum page size: 100
        - Includes ETag for caching
        - Supports field selection for bandwidth optimization
      
      parameters:
        - name: page
          in: query
          description: Page number (1-based)
          schema:
            type: integer
            minimum: 1
            default: 1
        
        - name: limit
          in: query
          description: Items per page
          schema:
            type: integer
            minimum: 1
            maximum: 100
            default: 20
        
        - name: search
          in: query
          description: Search term for name/email
          schema:
            type: string
            minLength: 2
            maxLength: 100
        
        - name: status
          in: query
          description: Filter by user status
          schema:
            type: array
            items:
              type: string
              enum: [active, inactive, suspended, pending]
        
        - name: fields
          in: query
          description: Comma-separated list of fields to include
          schema:
            type: string
            example: "id,name,email,created_at"
        
        - name: sort
          in: query
          description: Sort field and direction
          schema:
            type: string
            enum: [name_asc, name_desc, created_at_asc, created_at_desc, email_asc, email_desc]
            default: created_at_desc

      responses:
        '200':
          description: Users retrieved successfully
          headers:
            ETag:
              description: Resource version for caching
              schema:
                type: string
            X-Total-Count:
              description: Total number of users
              schema:
                type: integer
            X-Page-Count:
              description: Total number of pages
              schema:
                type: integer
            Link:
              description: Pagination links (RFC 5988)
              schema:
                type: string
                example: '</users?page=2>; rel="next", </users?page=5>; rel="last"'
          
          content:
            application/json:
              schema:
                type: object
                required: [data, pagination, metadata]
                properties:
                  data:
                    type: array
                    items:
                      $ref: '#/components/schemas/User'
                  pagination:
                    $ref: '#/components/schemas/PaginationInfo'
                  metadata:
                    $ref: '#/components/schemas/ResponseMetadata'
              
              examples:
                default:
                  summary: Standard user list
                  value:
                    data:
                      - id: 1
                        name: "John Doe"
                        email: "john@example.com"
                        status: "active"
                        created_at: "2024-01-15T10:30:00Z"
                      - id: 2
                        name: "Jane Smith"
                        email: "jane@example.com"
                        status: "active"
                        created_at: "2024-01-14T15:45:00Z"
                    pagination:
                      page: 1
                      limit: 20
                      total_items: 150
                      total_pages: 8
                      has_next: true
                      has_previous: false
                    metadata:
                      timestamp: "2024-01-15T12:00:00Z"
                      request_id: "req_123456"
                      processing_time: 0.045
                
                filtered:
                  summary: Filtered user list
                  value:
                    data:
                      - id: 3
                        name: "Active User"
                        email: "active@example.com"
                        status: "active"
                        created_at: "2024-01-15T10:30:00Z"
                    pagination:
                      page: 1
                      limit: 20
                      total_items: 1
                      total_pages: 1
                      has_next: false
                      has_previous: false
                    metadata:
                      timestamp: "2024-01-15T12:00:00Z"
                      request_id: "req_789012"
                      processing_time: 0.032
        
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'
        '500':
          $ref: '#/components/responses/InternalError'
      
      tags:
        - Users
      
      security:
        - BearerAuth: []
        - ApiKeyAuth: []

components:
  schemas:
    User:
      type: object
      required: [id, name, email, status, created_at]
      properties:
        id:
          type: integer
          description: Unique user identifier
          example: 1
          minimum: 1
        
        name:
          type: string
          description: User's full name
          example: "John Doe"
          minLength: 2
          maxLength: 100
        
        email:
          type: string
          format: email
          description: User's email address
          example: "john@example.com"
          maxLength: 255
        
        status:
          type: string
          enum: [active, inactive, suspended, pending]
          description: Current user status
          example: "active"
        
        avatar_url:
          type: string
          format: uri
          description: URL to user's avatar image
          example: "https://cdn.company.com/avatars/user_1.jpg"
          nullable: true
        
        created_at:
          type: string
          format: date-time
          description: User creation timestamp
          example: "2024-01-15T10:30:00Z"
        
        updated_at:
          type: string
          format: date-time
          description: Last update timestamp
          example: "2024-01-15T15:45:00Z"
        
        metadata:
          type: object
          description: Additional user metadata
          additionalProperties: true
          example:
            department: "Engineering"
            role: "Senior Developer"
    
    PaginationInfo:
      type: object
      required: [page, limit, total_items, total_pages, has_next, has_previous]
      properties:
        page:
          type: integer
          description: Current page number (1-based)
          minimum: 1
          example: 1
        
        limit:
          type: integer
          description: Items per page
          minimum: 1
          maximum: 100
          example: 20
        
        total_items:
          type: integer
          description: Total number of items
          minimum: 0
          example: 150
        
        total_pages:
          type: integer
          description: Total number of pages
          minimum: 0
          example: 8
        
        has_next:
          type: boolean
          description: Whether next page exists
          example: true
        
        has_previous:
          type: boolean
          description: Whether previous page exists
          example: false
        
        next_page:
          type: string
          format: uri
          description: URL to next page
          example: "/users?page=2&limit=20"
          nullable: true
        
        previous_page:
          type: string
          format: uri
          description: URL to previous page
          example: "/users?page=1&limit=20"
          nullable: true
    
    ResponseMetadata:
      type: object
      required: [timestamp, request_id]
      properties:
        timestamp:
          type: string
          format: date-time
          description: Response generation timestamp
          example: "2024-01-15T12:00:00Z"
        
        request_id:
          type: string
          description: Unique request identifier for tracing
          example: "req_123456789"
        
        processing_time:
          type: number
          format: float
          description: Request processing time in seconds
          minimum: 0
          example: 0.045
        
        api_version:
          type: string
          description: API version used
          example: "1.0.0"
        
        rate_limit:
          type: object
          description: Rate limiting information
          properties:
            remaining:
              type: integer
              description: Remaining requests in current window
              example: 95
            reset:
              type: integer
              description: Unix timestamp when rate limit resets
              example: 1705401600
            limit:
              type: integer
              description: Total requests allowed in window
              example: 100

  responses:
    BadRequest:
      description: Bad request - invalid input parameters
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          examples:
            validation_error:
              summary: Validation error
              value:
                error:
                  code: "VALIDATION_FAILED"
                  message: "Request validation failed"
                  details:
                    - field: "email"
                      message: "Invalid email format"
                      code: "INVALID_FORMAT"
                    - field: "age"
                      message: "Must be between 18 and 120"
                      code: "OUT_OF_RANGE"
                metadata:
                  timestamp: "2024-01-15T12:00:00Z"
                  request_id: "req_123456"
    
    Unauthorized:
      description: Authentication required
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            error:
              code: "AUTHENTICATION_REQUIRED"
              message: "Valid authentication credentials required"
              details: "Bearer token missing or invalid"
            metadata:
              timestamp: "2024-01-15T12:00:00Z"
              request_id: "req_123456"
    
    Forbidden:
      description: Access denied - insufficient permissions
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            error:
              code: "ACCESS_DENIED"
              message: "Insufficient permissions for this operation"
              details: "Required permission: users:read"
            metadata:
              timestamp: "2024-01-15T12:00:00Z"
              request_id: "req_123456"
    
    InternalError:
      description: Internal server error
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            error:
              code: "INTERNAL_ERROR"
              message: "An unexpected error occurred"
              details: "Please try again later or contact support"
            metadata:
              timestamp: "2024-01-15T12:00:00Z"
              request_id: "req_123456"
              support_reference: "ERR_789012"

    ErrorResponse:
      type: object
      required: [error, metadata]
      properties:
        error:
          type: object
          required: [code, message]
          properties:
            code:
              type: string
              description: Machine-readable error code
              example: "VALIDATION_FAILED"
            
            message:
              type: string
              description: Human-readable error message
              example: "Request validation failed"
            
            details:
              oneOf:
                - type: string
                - type: array
                  items:
                    type: object
                - type: object
              description: Additional error details
              example: "Invalid email format provided"
        
        metadata:
          $ref: '#/components/schemas/ResponseMetadata'
```

**Bad: 不一貫なAPI設計**
```yaml
# 問題のあるAPI設計例
openapi: 3.0.3
info:
  title: Bad API Example
  version: 1.0.0  # 問題: 最小限の情報のみ

paths:
  /user:  # 問題: 一貫性のない命名（usersではなくuser）
    get:
      # 問題: ドキュメント不足
      responses:
        '200':
          description: OK  # 問題: 不十分な説明
          content:
            application/json:
              # 問題: スキーマ定義なし
              example:
                users: []  # 問題: フィールド名が統一されていない
  
  /getUsers:  # 問題: RESTful原則違反（動詞を含む）
    post:  # 問題: 不適切なHTTPメソッド（GETであるべき）
      parameters:
        - name: data  # 問題: 不明確なパラメータ名
          in: body  # 問題: OpenAPI 3.0では非推奨
          schema:
            type: string  # 問題: 不適切なデータ型
      
      responses:
        '200':
          description: Success
          # 問題: エラーレスポンス定義なし
        # 問題: 他のステータスコード未定義

  /user/{id}/delete:  # 問題: RESTful原則違反（動詞を含む）
    get:  # 問題: 不適切なメソッド（DELETEであるべき）
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string  # 問題: 整数であるべき
      
      responses:
        '200':
          description: Deleted
          # 問題: 削除操作で200返却は不適切
```

#### 1.1.2 一貫性の原則

**Good: 一貫したAPI設計パターン**
```python
# APIデザイン一貫性実装
from typing import Dict, List, Any, Optional, Generic, TypeVar
from dataclasses import dataclass, field
from datetime import datetime
from enum import Enum
from abc import ABC, abstractmethod
from pydantic import BaseModel, Field, validator
from fastapi import FastAPI, Query, Path, Body, Depends, HTTPException
from fastapi.responses import JSONResponse

# 型定義
T = TypeVar('T')

class SortOrder(str, Enum):
    """ソート順序列挙"""
    ASC = "asc"
    DESC = "desc"

class APIResponseStatus(str, Enum):
    """APIレスポンス状態"""
    SUCCESS = "success"
    ERROR = "error"
    WARNING = "warning"

@dataclass
class PaginationParams:
    """ページネーションパラメータ"""
    page: int = Field(default=1, ge=1, description="Page number (1-based)")
    limit: int = Field(default=20, ge=1, le=100, description="Items per page")
    
    @property
    def offset(self) -> int:
        """オフセット計算"""
        return (self.page - 1) * self.limit

@dataclass
class SortParams:
    """ソートパラメータ"""
    field: str = Field(description="Sort field name")
    order: SortOrder = Field(default=SortOrder.ASC, description="Sort order")
    
    def to_sql_order(self) -> str:
        """SQL ORDER BY句生成"""
        return f"{self.field} {'ASC' if self.order == SortOrder.ASC else 'DESC'}"

class StandardAPIResponse(BaseModel, Generic[T]):
    """標準APIレスポンス形式"""
    
    status: APIResponseStatus = Field(description="Response status")
    data: Optional[T] = Field(None, description="Response data")
    error: Optional[Dict[str, Any]] = Field(None, description="Error information")
    metadata: Dict[str, Any] = Field(default_factory=dict, description="Response metadata")
    
    @classmethod
    def success(
        cls,
        data: T,
        request_id: str = None,
        processing_time: float = None,
        **metadata_kwargs
    ) -> 'StandardAPIResponse[T]':
        """成功レスポンス生成"""
        metadata = {
            "timestamp": datetime.utcnow().isoformat(),
            "request_id": request_id,
            "processing_time": processing_time,
            **metadata_kwargs
        }
        
        return cls(
            status=APIResponseStatus.SUCCESS,
            data=data,
            metadata={k: v for k, v in metadata.items() if v is not None}
        )
    
    @classmethod
    def error(
        cls,
        error_code: str,
        error_message: str,
        error_details: Any = None,
        request_id: str = None,
        **metadata_kwargs
    ) -> 'StandardAPIResponse[None]':
        """エラーレスポンス生成"""
        error_info = {
            "code": error_code,
            "message": error_message
        }
        
        if error_details is not None:
            error_info["details"] = error_details
        
        metadata = {
            "timestamp": datetime.utcnow().isoformat(),
            "request_id": request_id,
            **metadata_kwargs
        }
        
        return cls(
            status=APIResponseStatus.ERROR,
            error=error_info,
            metadata={k: v for k, v in metadata.items() if v is not None}
        )

class PaginatedResponse(StandardAPIResponse[List[T]]):
    """ページネーション付きレスポンス"""
    
    pagination: Dict[str, Any] = Field(description="Pagination information")
    
    @classmethod
    def create(
        cls,
        data: List[T],
        pagination_params: PaginationParams,
        total_items: int,
        request_id: str = None,
        processing_time: float = None
    ) -> 'PaginatedResponse[T]':
        """ページネーションレスポンス生成"""
        
        total_pages = (total_items + pagination_params.limit - 1) // pagination_params.limit
        has_next = pagination_params.page < total_pages
        has_previous = pagination_params.page > 1
        
        pagination_info = {
            "page": pagination_params.page,
            "limit": pagination_params.limit,
            "total_items": total_items,
            "total_pages": total_pages,
            "has_next": has_next,
            "has_previous": has_previous
        }
        
        # ナビゲーションリンク生成
        if has_next:
            pagination_info["next_page"] = pagination_params.page + 1
        if has_previous:
            pagination_info["previous_page"] = pagination_params.page - 1
        
        metadata = {
            "timestamp": datetime.utcnow().isoformat(),
            "request_id": request_id,
            "processing_time": processing_time
        }
        
        return cls(
            status=APIResponseStatus.SUCCESS,
            data=data,
            pagination=pagination_info,
            metadata={k: v for k, v in metadata.items() if v is not None}
        )

# 一貫したエンドポイント実装パターン
class BaseAPIEndpoint(ABC):
    """API基底エンドポイントクラス"""
    
    def __init__(self, resource_name: str):
        self.resource_name = resource_name
        self.logger = logging.getLogger(f"api.{resource_name}")
    
    @abstractmethod
    def get_service(self):
        """対応するサービスクラス取得"""
        pass
    
    @abstractmethod
    def get_model_class(self):
        """モデルクラス取得"""
        pass
    
    def create_list_endpoint(self):
        """一覧取得エンドポイント生成"""
        async def list_resources(
            pagination: PaginationParams = Depends(),
            sort: Optional[SortParams] = Depends(),
            search: Optional[str] = Query(None, min_length=2, max_length=100),
            filters: Optional[Dict[str, Any]] = None
        ):
            """リソース一覧取得"""
            start_time = time.time()
            request_id = str(uuid.uuid4())
            
            try:
                service = self.get_service()
                
                # データ取得
                items, total_count = await service.get_items(
                    pagination=pagination,
                    sort=sort,
                    search=search,
                    filters=filters
                )
                
                # レスポンス生成
                processing_time = time.time() - start_time
                
                return PaginatedResponse.create(
                    data=items,
                    pagination_params=pagination,
                    total_items=total_count,
                    request_id=request_id,
                    processing_time=processing_time
                )
                
            except Exception as e:
                self.logger.error(f"List endpoint failed: {e}", extra={"request_id": request_id})
                return StandardAPIResponse.error(
                    error_code="LIST_FAILED",
                    error_message="Failed to retrieve items",
                    request_id=request_id
                )
        
        return list_resources
    
    def create_get_endpoint(self):
        """単一リソース取得エンドポイント生成"""
        async def get_resource(
            resource_id: int = Path(..., description=f"{self.resource_name} ID", ge=1)
        ):
            """単一リソース取得"""
            start_time = time.time()
            request_id = str(uuid.uuid4())
            
            try:
                service = self.get_service()
                item = await service.get_by_id(resource_id)
                
                if not item:
                    return StandardAPIResponse.error(
                        error_code="NOT_FOUND",
                        error_message=f"{self.resource_name} not found",
                        error_details=f"No {self.resource_name} with ID {resource_id}",
                        request_id=request_id
                    )
                
                processing_time = time.time() - start_time
                
                return StandardAPIResponse.success(
                    data=item,
                    request_id=request_id,
                    processing_time=processing_time
                )
                
            except Exception as e:
                self.logger.error(f"Get endpoint failed: {e}", extra={"request_id": request_id})
                return StandardAPIResponse.error(
                    error_code="GET_FAILED",
                    error_message=f"Failed to retrieve {self.resource_name}",
                    request_id=request_id
                )
        
        return get_resource
    
    def create_create_endpoint(self):
        """リソース作成エンドポイント生成"""
        model_class = self.get_model_class()
        
        async def create_resource(
            resource_data: model_class = Body(..., description=f"{self.resource_name} creation data")
        ):
            """リソース作成"""
            start_time = time.time()
            request_id = str(uuid.uuid4())
            
            try:
                service = self.get_service()
                created_item = await service.create(resource_data.dict())
                
                processing_time = time.time() - start_time
                
                return StandardAPIResponse.success(
                    data=created_item,
                    request_id=request_id,
                    processing_time=processing_time,
                    created=True
                )
                
            except ValidationError as e:
                return StandardAPIResponse.error(
                    error_code="VALIDATION_FAILED",
                    error_message="Resource validation failed",
                    error_details=e.errors(),
                    request_id=request_id
                )
            except Exception as e:
                self.logger.error(f"Create endpoint failed: {e}", extra={"request_id": request_id})
                return StandardAPIResponse.error(
                    error_code="CREATE_FAILED",
                    error_message=f"Failed to create {self.resource_name}",
                    request_id=request_id
                )
        
        return create_resource

# 使用例
class UserAPIEndpoint(BaseAPIEndpoint):
    """ユーザーAPI実装例"""
    
    def __init__(self):
        super().__init__("user")
    
    def get_service(self):
        # 実際のサービスクラスを返す
        return UserService()
    
    def get_model_class(self):
        return UserCreateModel

# FastAPI統合
app = FastAPI(
    title="Consistent API Design Example",
    version="1.0.0",
    description="API consistency patterns demonstration"
)

user_endpoint = UserAPIEndpoint()

# 一貫したパターンでエンドポイント登録
app.add_api_route(
    "/users",
    user_endpoint.create_list_endpoint(),
    methods=["GET"],
    tags=["Users"],
    summary="List users",
    description="Retrieve paginated list of users with filtering and sorting"
)

app.add_api_route(
    "/users/{resource_id}",
    user_endpoint.create_get_endpoint(),
    methods=["GET"],
    tags=["Users"],
    summary="Get user",
    description="Retrieve specific user by ID"
)

app.add_api_route(
    "/users",
    user_endpoint.create_create_endpoint(),
    methods=["POST"],
    tags=["Users"],
    summary="Create user",
    description="Create new user account"
)
```

### 1.2 API設計原理

#### 1.2.1 RESTful原則準拠

**Good: REST成熟度モデルレベル3実装**
```python
# Richardson成熟度モデル レベル3 (HATEOAS) 実装
from typing import Dict, List, Any, Optional
from dataclasses import dataclass
from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

@dataclass
class APILink:
    """HATEOASリンク定義"""
    rel: str  # リンク関係
    href: str  # URL
    method: str = "GET"  # HTTPメソッド
    type: str = "application/json"  # メディアタイプ
    title: Optional[str] = None  # リンクタイトル

class HATEOASResponse:
    """HATEOAS対応レスポンスビルダー"""
    
    @staticmethod
    def build_user_response(user_data: dict, request: Request) -> dict:
        """ユーザーレスポンスにHATEOASリンク追加"""
        
        base_url = f"{request.url.scheme}://{request.url.netloc}"
        user_id = user_data["id"]
        
        # ユーザー関連リンク
        links = [
            APILink(
                rel="self",
                href=f"{base_url}/users/{user_id}",
                method="GET",
                title="Get user details"
            ),
            APILink(
                rel="edit",
                href=f"{base_url}/users/{user_id}",
                method="PUT",
                title="Update user"
            ),
            APILink(
                rel="delete",
                href=f"{base_url}/users/{user_id}",
                method="DELETE",
                title="Delete user"
            ),
            APILink(
                rel="avatar",
                href=f"{base_url}/users/{user_id}/avatar",
                method="GET",
                type="image/*",
                title="User avatar"
            ),
            APILink(
                rel="posts",
                href=f"{base_url}/users/{user_id}/posts",
                method="GET",
                title="User's posts"
            )
        ]
        
        # ユーザー状態に基づく条件付きリンク
        if user_data.get("status") == "inactive":
            links.append(APILink(
                rel="activate",
                href=f"{base_url}/users/{user_id}/activate",
                method="POST",
                title="Activate user"
            ))
        
        if user_data.get("status") == "active":
            links.append(APILink(
                rel="deactivate",
                href=f"{base_url}/users/{user_id}/deactivate",
                method="POST",
                title="Deactivate user"
            ))
        
        # コレクション操作リンク
        links.extend([
            APILink(
                rel="collection",
                href=f"{base_url}/users",
                method="GET",
                title="All users"
            ),
            APILink(
                rel="create",
                href=f"{base_url}/users",
                method="POST",
                title="Create new user"
            )
        ])
        
        return {
            **user_data,
            "_links": {
                link.rel: {
                    "href": link.href,
                    "method": link.method,
                    "type": link.type,
                    "title": link.title
                }
                for link in links
            }
        }
    
    @staticmethod
    def build_collection_response(
        items: List[dict],
        pagination: dict,
        request: Request,
        resource_name: str = "items"
    ) -> dict:
        """コレクションレスポンスにHATEOASリンク追加"""
        
        base_url = f"{request.url.scheme}://{request.url.netloc}"
        
        # 各アイテムにリンク追加
        enhanced_items = []
        for item in items:
            if "id" in item:
                item_with_links = HATEOASResponse.build_user_response(item, request)
                enhanced_items.append(item_with_links)
            else:
                enhanced_items.append(item)
        
        # コレクションレベルのリンク
        collection_links = [
            APILink(
                rel="self",
                href=str(request.url),
                method="GET",
                title=f"Current {resource_name} page"
            ),
            APILink(
                rel="create",
                href=f"{base_url}/{resource_name}",
                method="POST",
                title=f"Create new {resource_name[:-1]}"  # 単数形に変換
            )
        ]
        
        # ページネーションリンク
        current_page = pagination.get("page", 1)
        total_pages = pagination.get("total_pages", 1)
        
        if pagination.get("has_previous"):
            prev_page = current_page - 1
            prev_url = str(request.url.include_query_params(page=prev_page))
            collection_links.append(APILink(
                rel="prev",
                href=prev_url,
                method="GET",
                title="Previous page"
            ))
        
        if pagination.get("has_next"):
            next_page = current_page + 1
            next_url = str(request.url.include_query_params(page=next_page))
            collection_links.append(APILink(
                rel="next",
                href=next_url,
                method="GET",
                title="Next page"
            ))
        
        # 最初と最後のページ
        if current_page > 1:
            first_url = str(request.url.include_query_params(page=1))
            collection_links.append(APILink(
                rel="first",
                href=first_url,
                method="GET",
                title="First page"
            ))
        
        if current_page < total_pages:
            last_url = str(request.url.include_query_params(page=total_pages))
            collection_links.append(APILink(
                rel="last",
                href=last_url,
                method="GET",
                title="Last page"
            ))
        
        return {
            "data": enhanced_items,
            "pagination": pagination,
            "_links": {
                link.rel: {
                    "href": link.href,
                    "method": link.method,
                    "type": link.type,
                    "title": link.title
                }
                for link in collection_links
            },
            "metadata": {
                "timestamp": datetime.utcnow().isoformat(),
                "total_links": len(collection_links)
            }
        }

# 使用例
app = FastAPI()

@app.get("/users")
async def list_users(
    request: Request,
    page: int = Query(1, ge=1),
    limit: int = Query(20, ge=1, le=100)
):
    """HATEOAS対応ユーザー一覧"""
    
    # ページネーションパラメータ
    pagination_params = PaginationParams(page=page, limit=limit)
    
    # データ取得（例）
    users_data = [
        {"id": 1, "name": "John Doe", "email": "john@example.com", "status": "active"},
        {"id": 2, "name": "Jane Smith", "email": "jane@example.com", "status": "inactive"}
    ]
    
    pagination_info = {
        "page": page,
        "limit": limit,
        "total_items": 100,
        "total_pages": 5,
        "has_next": page < 5,
        "has_previous": page > 1
    }
    
    # HATEOASレスポンス構築
    response = HATEOASResponse.build_collection_response(
        items=users_data,
        pagination=pagination_info,
        request=request,
        resource_name="users"
    )
    
    return response

@app.get("/users/{user_id}")
async def get_user(request: Request, user_id: int = Path(..., ge=1)):
    """HATEOAS対応単一ユーザー取得"""
    
    # ユーザーデータ取得（例）
    user_data = {
        "id": user_id,
        "name": "John Doe",
        "email": "john@example.com",
        "status": "active",
        "created_at": "2024-01-15T10:30:00Z"
    }
    
    # HATEOASリンク付きレスポンス
    response = HATEOASResponse.build_user_response(user_data, request)
    
    return StandardAPIResponse.success(
        data=response,
        request_id=str(uuid.uuid4())
    )
```

**Bad: REST原則未準拠**
```python
# REST原則未準拠の悪い例
from fastapi import FastAPI

app = FastAPI()

# 問題: 動詞を含むURL
@app.get("/getUserById")  # 悪い: RESTfulではない
async def get_user_bad(id: str):  # 問題: パスパラメータでない
    return {"user": {}}

@app.post("/createUser")  # 悪い: 動詞を含む
async def create_user_bad():
    return {"message": "created"}

@app.get("/deleteUser")  # 問題: GET でデータ削除
async def delete_user_bad():
    return {"deleted": True}

# 問題: 一貫性のないレスポンス形式
@app.get("/users")
async def list_users_bad():
    return [{"id": 1}, {"id": 2}]  # 配列直接返却

@app.get("/user/{user_id}")
async def get_user_bad(user_id: int):
    return {"data": {"id": user_id}, "success": True}  # 異なる形式

@app.post("/users")
async def create_user_bad(user_data: dict):
    return {"user_id": 123, "status": "ok"}  # また異なる形式

# 問題: エラーハンドリング不一致
@app.get("/error-example")
async def error_example():
    return {"error": "Something went wrong"}  # 統一されていないエラー形式
```

#### 1.2.2 APIバージョニング戦略

**Good: セマンティックバージョニング戦略**
```python
# API バージョニング実装
from enum import Enum
from typing import Optional, Dict, Any
from fastapi import FastAPI, Header, HTTPException, Depends
from pydantic import BaseModel
from dataclasses import dataclass

class APIVersion(str, Enum):
    """サポートするAPIバージョン"""
    V1_0 = "1.0"
    V1_1 = "1.1"
    V2_0 = "2.0"
    V2_1 = "2.1"

@dataclass
class VersionInfo:
    """バージョン情報定義"""
    version: str
    status: str  # stable, deprecated, preview
    sunset_date: Optional[str] = None
    migration_guide: Optional[str] = None
    breaking_changes: Optional[List[str]] = None

class APIVersionManager:
    """APIバージョン管理クラス"""
    
    VERSION_MAP = {
        APIVersion.V1_0: VersionInfo(
            version="1.0",
            status="deprecated",
            sunset_date="2024-12-31",
            migration_guide="https://docs.company.com/api/migration/v1-to-v2",
            breaking_changes=[
                "User ID format changed from integer to UUID",
                "Pagination parameters renamed (page_size → limit)"
            ]
        ),
        APIVersion.V1_1: VersionInfo(
            version="1.1",
            status="stable",
            sunset_date="2025-06-30"
        ),
        APIVersion.V2_0: VersionInfo(
            version="2.0",
            status="stable"
        ),
        APIVersion.V2_1: VersionInfo(
            version="2.1",
            status="preview"
        )
    }
    
    DEFAULT_VERSION = APIVersion.V2_0
    SUPPORTED_VERSIONS = list(VERSION_MAP.keys())
    
    @classmethod
    def get_version_info(cls, version: APIVersion) -> VersionInfo:
        """バージョン情報取得"""
        return cls.VERSION_MAP.get(version)
    
    @classmethod
    def parse_version(cls, version_string: str) -> APIVersion:
        """バージョン文字列をパース"""
        version_string = version_string.lower().replace('v', '')
        
        for api_version in cls.SUPPORTED_VERSIONS:
            if api_version.value == version_string:
                return api_version
        
        raise HTTPException(
            status_code=400,
            detail={
                "error": {
                    "code": "UNSUPPORTED_VERSION",
                    "message": f"API version '{version_string}' is not supported",
                    "supported_versions": [v.value for v in cls.SUPPORTED_VERSIONS]
                }
            }
        )
    
    @classmethod
    def get_version_from_request(
        cls,
        # 複数のバージョニング手法をサポート
        path_version: Optional[str] = None,
        accept_version: Optional[str] = Header(None, alias="Accept-Version"),
        api_version: Optional[str] = Header(None, alias="API-Version"),
        x_api_version: Optional[str] = Header(None, alias="X-API-Version")
    ) -> APIVersion:
        """リクエストからAPIバージョン取得"""
        
        # 優先順位: パスパラメータ > Accept-Version > API-Version > X-API-Version > デフォルト
        version_string = (
            path_version or 
            accept_version or 
            api_version or 
            x_api_version or 
            cls.DEFAULT_VERSION.value
        )
        
        return cls.parse_version(version_string)

# バージョン別レスポンスモデル
class UserResponseV1(BaseModel):
    """API v1.x ユーザーレスポンス"""
    id: int  # v1では整数ID
    name: str
    email: str
    created_at: str

class UserResponseV2(BaseModel):
    """API v2.x ユーザーレスポンス"""
    id: str  # v2ではUUID
    name: str
    email: str
    display_name: Optional[str] = None  # v2で追加
    avatar_url: Optional[str] = None    # v2で追加
    created_at: str
    updated_at: str  # v2で追加
    
    class Config:
        schema_extra = {
            "example": {
                "id": "550e8400-e29b-41d4-a716-446655440000",
                "name": "John Doe",
                "email": "john@example.com",
                "display_name": "Johnny",
                "avatar_url": "https://cdn.company.com/avatars/john.jpg",
                "created_at": "2024-01-15T10:30:00Z",
                "updated_at": "2024-01-15T15:45:00Z"
            }
        }

class VersionedAPIResponse(BaseModel):
    """バージョン情報付きAPIレスポンス"""
    data: Any
    api_version: str
    version_info: Dict[str, Any]
    migration_hints: Optional[List[str]] = None

# FastAPIアプリケーション設定
app = FastAPI(
    title="Versioned API Example",
    version="2.1.0",
    description="Multi-version API implementation"
)

# バージョン情報エンドポイント
@app.get("/api-info")
async def get_api_info():
    """APIバージョン情報取得"""
    return {
        "supported_versions": [
            {
                "version": info.version,
                "status": info.status,
                "sunset_date": info.sunset_date,
                "migration_guide": info.migration_guide
            }
            for info in APIVersionManager.VERSION_MAP.values()
        ],
        "default_version": APIVersionManager.DEFAULT_VERSION.value,
        "versioning_methods": [
            "Path parameter: /v1/users",
            "Accept-Version header",
            "API-Version header",
            "X-API-Version header"
        ]
    }

# パスベースバージョニング
@app.get("/v1/users/{user_id}", response_model=VersionedAPIResponse)
async def get_user_v1(user_id: int):
    """API v1.x ユーザー取得"""
    version = APIVersionManager.V1_0
    version_info = APIVersionManager.get_version_info(version)
    
    # v1形式のデータ変換
    user_data = UserResponseV1(
        id=user_id,
        name="John Doe",
        email="john@example.com",
        created_at="2024-01-15T10:30:00Z"
    )
    
    migration_hints = None
    if version_info.status == "deprecated":
        migration_hints = [
            "This version will be sunset on 2024-12-31",
            "Please migrate to v2.0 for UUID-based user IDs",
            "See migration guide for breaking changes"
        ]
    
    return VersionedAPIResponse(
        data=user_data,
        api_version=version.value,
        version_info={
            "status": version_info.status,
            "sunset_date": version_info.sunset_date,
            "migration_guide": version_info.migration_guide
        },
        migration_hints=migration_hints
    )

@app.get("/v2/users/{user_id}", response_model=VersionedAPIResponse)
async def get_user_v2(user_id: str):
    """API v2.x ユーザー取得"""
    version = APIVersionManager.V2_0
    version_info = APIVersionManager.get_version_info(version)
    
    # v2形式のデータ
    user_data = UserResponseV2(
        id=user_id,
        name="John Doe",
        email="john@example.com",
        display_name="Johnny",
        avatar_url="https://cdn.company.com/avatars/john.jpg",
        created_at="2024-01-15T10:30:00Z",
        updated_at="2024-01-15T15:45:00Z"
    )
    
    return VersionedAPIResponse(
        data=user_data,
        api_version=version.value,
        version_info={
            "status": version_info.status
        }
    )

# ヘッダーベースバージョニング (統一エンドポイント)
@app.get("/users/{user_id}", response_model=VersionedAPIResponse)
async def get_user_versioned(
    user_id: str,
    version: APIVersion = Depends(APIVersionManager.get_version_from_request)
):
    """バージョン対応ユーザー取得"""
    version_info = APIVersionManager.get_version_info(version)
    
    if version in [APIVersion.V1_0, APIVersion.V1_1]:
        # v1.x の処理
        try:
            user_id_int = int(user_id)
        except ValueError:
            raise HTTPException(
                status_code=400,
                detail={
                    "error": {
                        "code": "INVALID_USER_ID",
                        "message": "User ID must be integer for API v1.x",
                        "migration_hint": "Use UUID format for API v2.x"
                    }
                }
            )
        
        user_data = UserResponseV1(
            id=user_id_int,
            name="John Doe",
            email="john@example.com",
            created_at="2024-01-15T10:30:00Z"
        )
        
        migration_hints = [
            "Consider migrating to v2.0 for enhanced features",
            "v2.0 includes display_name and avatar_url fields"
        ]
    
    else:  # v2.x
        user_data = UserResponseV2(
            id=user_id,
            name="John Doe",
            email="john@example.com",
            display_name="Johnny",
            avatar_url="https://cdn.company.com/avatars/john.jpg",
            created_at="2024-01-15T10:30:00Z",
            updated_at="2024-01-15T15:45:00Z"
        )
        migration_hints = None
    
    return VersionedAPIResponse(
        data=user_data,
        api_version=version.value,
        version_info={
            "status": version_info.status,
            "sunset_date": version_info.sunset_date
        },
        migration_hints=migration_hints
    )

# バージョン廃止警告ミドルウェア
@app.middleware("http")
async def version_deprecation_middleware(request, call_next):
    """廃止予定バージョンの警告ヘッダー追加"""
    response = await call_next(request)
    
    # リクエストからバージョン抽出
    try:
        version = APIVersionManager.get_version_from_request(
            accept_version=request.headers.get("Accept-Version"),
            api_version=request.headers.get("API-Version"),
            x_api_version=request.headers.get("X-API-Version")
        )
        
        version_info = APIVersionManager.get_version_info(version)
        
        if version_info.status == "deprecated":
            response.headers["Warning"] = f"299 - "API version {version.value} is deprecated. Sunset date: {version_info.sunset_date}"
            response.headers["Sunset"] = version_info.sunset_date
            
            if version_info.migration_guide:
                response.headers["Link"] = f"<{version_info.migration_guide}>; rel=\"migration-guide\""
    
    except Exception:
        # バージョン取得失敗時はスルー
        pass
    
    return response
```

**Bad: バージョニング戦略なし**
```python
# バージョニング戦略のない悪い例
from fastapi import FastAPI

app = FastAPI()

# 問題: バージョニングなし
@app.get("/users/{user_id}")
async def get_user(user_id: int):
    # 問題: フィールド追加時の互換性なし
    return {
        "id": user_id,
        "name": "John Doe",
        "new_field": "追加されたフィールド"  # 既存クライアントに影響
    }

# 問題: 破壊的変更
@app.put("/users/{user_id}")
async def update_user(user_id: str):  # intからstrに変更
    # 既存のクライアントが動作しなくなる
    return {"updated": True}

# 問題: レスポンス構造の変更
@app.get("/posts")
async def get_posts():
    # 以前: [{"id": 1, "title": "Post 1"}]
    # 現在: {"posts": [{"id": 1, "title": "Post 1"}]}  # 構造変更で互換性なし
    return {
        "posts": [{"id": 1, "title": "Post 1"}],
        "total": 1
    }
```

#### 1.2.3 APIセキュリティ設計原則

**Good: 多層防御セキュリティ実装**
```python
# APIセキュリティ実装
from typing import Optional, List, Dict, Any
from datetime import datetime, timedelta
from fastapi import FastAPI, Depends, HTTPException, Request, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.trustedhost import TrustedHostMiddleware
from fastapi.middleware.httpsredirect import HTTPSRedirectMiddleware
import jwt
import bcrypt
import secrets
import hashlib
import time
from dataclasses import dataclass
from enum import Enum
import logging
import ipaddress
from collections import defaultdict

# セキュリティ設定
@dataclass
class SecurityConfig:
    """セキュリティ設定クラス"""
    jwt_secret_key: str = secrets.token_urlsafe(32)
    jwt_algorithm: str = "HS256"
    access_token_expire_minutes: int = 15
    refresh_token_expire_days: int = 30
    
    # レート制限設定
    rate_limit_requests: int = 100
    rate_limit_window_minutes: int = 15
    
    # セキュリティヘッダー
    security_headers: Dict[str, str] = None
    
    def __post_init__(self):
        if self.security_headers is None:
            self.security_headers = {
                "X-Content-Type-Options": "nosniff",
                "X-Frame-Options": "DENY",
                "X-XSS-Protection": "1; mode=block",
                "Strict-Transport-Security": "max-age=31536000; includeSubDomains",
                "Content-Security-Policy": "default-src 'self'",
                "Referrer-Policy": "strict-origin-when-cross-origin",
                "Permissions-Policy": "geolocation=(), microphone=(), camera=()"
            }

class UserRole(str, Enum):
    """ユーザー権限列挙"""
    ADMIN = "admin"
    USER = "user"
    READONLY = "readonly"
    GUEST = "guest"

class SecurityEvent(str, Enum):
    """セキュリティイベント種別"""
    LOGIN_SUCCESS = "login_success"
    LOGIN_FAILED = "login_failed"
    TOKEN_EXPIRED = "token_expired"
    RATE_LIMIT_EXCEEDED = "rate_limit_exceeded"
    SUSPICIOUS_ACTIVITY = "suspicious_activity"
    UNAUTHORIZED_ACCESS = "unauthorized_access"

class SecurityAuditor:
    """セキュリティ監査ログ"""
    
    def __init__(self):
        self.logger = logging.getLogger("security_audit")
        self.events = []
    
    def log_event(
        self,
        event_type: SecurityEvent,
        request: Request,
        user_id: Optional[str] = None,
        additional_data: Optional[Dict[str, Any]] = None
    ):
        """セキュリティイベントログ記録"""
        
        event_data = {
            "timestamp": datetime.utcnow().isoformat(),
            "event_type": event_type.value,
            "client_ip": request.client.host,
            "user_agent": request.headers.get("user-agent"),
            "request_path": request.url.path,
            "request_method": request.method,
            "user_id": user_id,
            "additional_data": additional_data or {}
        }
        
        self.logger.warning(f"Security Event: {event_type.value}", extra=event_data)
        self.events.append(event_data)
        
        # 重要なイベントはアラート送信
        if event_type in [SecurityEvent.SUSPICIOUS_ACTIVITY, SecurityEvent.RATE_LIMIT_EXCEEDED]:
            self._send_security_alert(event_data)
    
    def _send_security_alert(self, event_data: Dict[str, Any]):
        """セキュリティアラート送信"""
        # 実際の実装では、メール/Slack/PagerDuty等に送信
        self.logger.critical(f"SECURITY ALERT: {event_data}")

class RateLimiter:
    """レート制限実装"""
    
    def __init__(self, config: SecurityConfig):
        self.config = config
        self.requests = defaultdict(list)
        self.blocked_ips = set()
    
    def is_allowed(self, client_ip: str, request_path: str) -> bool:
        """リクエスト許可チェック"""
        
        if client_ip in self.blocked_ips:
            return False
        
        now = datetime.utcnow()
        window_start = now - timedelta(minutes=self.config.rate_limit_window_minutes)
        
        # 古いリクエスト記録を削除
        self.requests[client_ip] = [
            req_time for req_time in self.requests[client_ip]
            if req_time > window_start
        ]
        
        # リクエスト数チェック
        if len(self.requests[client_ip]) >= self.config.rate_limit_requests:
            # 一時的ブロック（実際の実装では永続化推奨）
            self.blocked_ips.add(client_ip)
            return False
        
        # リクエスト記録追加
        self.requests[client_ip].append(now)
        return True

class JWTManager:
    """JWT トークン管理"""
    
    def __init__(self, config: SecurityConfig):
        self.config = config
    
    def create_access_token(self, user_id: str, role: UserRole, permissions: List[str] = None) -> str:
        """アクセストークン生成"""
        
        expire = datetime.utcnow() + timedelta(minutes=self.config.access_token_expire_minutes)
        
        payload = {
            "sub": user_id,
            "role": role.value,
            "permissions": permissions or [],
            "exp": expire,
            "iat": datetime.utcnow(),
            "type": "access",
            "jti": secrets.token_urlsafe(16)  # JWT ID for revocation
        }
        
        return jwt.encode(
            payload,
            self.config.jwt_secret_key,
            algorithm=self.config.jwt_algorithm
        )
    
    def create_refresh_token(self, user_id: str) -> str:
        """リフレッシュトークン生成"""
        
        expire = datetime.utcnow() + timedelta(days=self.config.refresh_token_expire_days)
        
        payload = {
            "sub": user_id,
            "exp": expire,
            "iat": datetime.utcnow(),
            "type": "refresh",
            "jti": secrets.token_urlsafe(16)
        }
        
        return jwt.encode(
            payload,
            self.config.jwt_secret_key,
            algorithm=self.config.jwt_algorithm
        )
    
    def verify_token(self, token: str, token_type: str = "access") -> Dict[str, Any]:
        """トークン検証"""
        
        try:
            payload = jwt.decode(
                token,
                self.config.jwt_secret_key,
                algorithms=[self.config.jwt_algorithm]
            )
            
            if payload.get("type") != token_type:
                raise HTTPException(
                    status_code=status.HTTP_401_UNAUTHORIZED,
                    detail="Invalid token type"
                )
            
            return payload
            
        except jwt.ExpiredSignatureError:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Token has expired"
            )
        except jwt.InvalidTokenError:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Invalid token"
            )

class SecurityMiddleware:
    """セキュリティミドルウェア"""
    
    def __init__(self, config: SecurityConfig):
        self.config = config
        self.auditor = SecurityAuditor()
        self.rate_limiter = RateLimiter(config)
    
    async def __call__(self, request: Request, call_next):
        """セキュリティチェック実行"""
        
        client_ip = request.client.host
        
        # IPアドレス検証
        if not self._is_valid_ip(client_ip):
            self.auditor.log_event(
                SecurityEvent.SUSPICIOUS_ACTIVITY,
                request,
                additional_data={"reason": "Invalid IP address"}
            )
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Access denied"
            )
        
        # レート制限チェック
        if not self.rate_limiter.is_allowed(client_ip, request.url.path):
            self.auditor.log_event(
                SecurityEvent.RATE_LIMIT_EXCEEDED,
                request
            )
            raise HTTPException(
                status_code=status.HTTP_429_TOO_MANY_REQUESTS,
                detail="Rate limit exceeded",
                headers={"Retry-After": "900"}  # 15分後に再試行
            )
        
        # リクエスト処理
        response = await call_next(request)
        
        # セキュリティヘッダー追加
        for header, value in self.config.security_headers.items():
            response.headers[header] = value
        
        return response
    
    def _is_valid_ip(self, ip_address: str) -> bool:
        """IPアドレス妥当性チェック"""
        try:
            ip = ipaddress.ip_address(ip_address)
            
            # プライベートIPアドレスは許可（開発環境考慮）
            if ip.is_private:
                return True
            
            # ブラックリストIPチェック（実装例）
            blocked_networks = [
                "10.0.0.0/8",    # 内部ネットワーク例
                "192.168.0.0/16" # 内部ネットワーク例
            ]
            
            for network in blocked_networks:
                if ip in ipaddress.ip_network(network):
                    return False
            
            return True
            
        except ValueError:
            return False

# FastAPI設定
security_config = SecurityConfig()
jwt_manager = JWTManager(security_config)
security = HTTPBearer()

app = FastAPI(
    title="Secure API Example",
    version="1.0.0",
    docs_url="/docs",  # プロダクションでは無効化推奨
    redoc_url="/redoc"  # プロダクションでは無効化推奨
)

# セキュリティミドルウェア追加
app.add_middleware(HTTPSRedirectMiddleware)  # HTTPS強制
app.add_middleware(TrustedHostMiddleware, allowed_hosts=["*.company.com", "localhost"])
app.add_middleware(
    CORSMiddleware,
    allow_origins=["https://app.company.com"],  # 特定オリジンのみ許可
    allow_credentials=True,
    allow_methods=["GET", "POST", "PUT", "DELETE"],
    allow_headers=["Authorization", "Content-Type"]
)
app.middleware("http")(SecurityMiddleware(security_config))

# 認証・認可依存関数
async def get_current_user(
    request: Request,
    credentials: HTTPAuthorizationCredentials = Depends(security)
) -> Dict[str, Any]:
    """現在のユーザー取得"""
    
    try:
        payload = jwt_manager.verify_token(credentials.credentials)
        
        # 成功ログ
        SecurityAuditor().log_event(
            SecurityEvent.LOGIN_SUCCESS,
            request,
            user_id=payload["sub"]
        )
        
        return payload
        
    except HTTPException as e:
        # 失敗ログ
        SecurityAuditor().log_event(
            SecurityEvent.LOGIN_FAILED,
            request,
            additional_data={"reason": str(e.detail)}
        )
        raise

async def require_permission(permission: str):
    """権限要求デコレータ"""
    
    def permission_checker(
        request: Request,
        current_user: Dict[str, Any] = Depends(get_current_user)
    ):
        user_permissions = current_user.get("permissions", [])
        user_role = current_user.get("role")
        
        # 管理者は全権限所有
        if user_role == UserRole.ADMIN.value:
            return current_user
        
        # 特定権限チェック
        if permission not in user_permissions:
            SecurityAuditor().log_event(
                SecurityEvent.UNAUTHORIZED_ACCESS,
                request,
                user_id=current_user["sub"],
                additional_data={"required_permission": permission}
            )
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=f"Permission '{permission}' required"
            )
        
        return current_user
    
    return permission_checker

# セキュアエンドポイント実装例
@app.post("/auth/login")
async def login(request: Request, username: str, password: str):
    """セキュアログイン"""
    
    # パスワードハッシュ検証（実際はデータベースから取得）
    stored_hash = "$2b$12$example_hash"  # bcryptハッシュ
    
    if not bcrypt.checkpw(password.encode('utf-8'), stored_hash.encode('utf-8')):
        SecurityAuditor().log_event(
            SecurityEvent.LOGIN_FAILED,
            request,
            additional_data={"username": username, "reason": "Invalid password"}
        )
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid credentials"
        )
    
    # トークン生成
    access_token = jwt_manager.create_access_token(
        user_id=username,
        role=UserRole.USER,
        permissions=["users:read", "posts:read"]
    )
    
    refresh_token = jwt_manager.create_refresh_token(username)
    
    SecurityAuditor().log_event(
        SecurityEvent.LOGIN_SUCCESS,
        request,
        user_id=username
    )
    
    return {
        "access_token": access_token,
        "refresh_token": refresh_token,
        "token_type": "bearer",
        "expires_in": security_config.access_token_expire_minutes * 60
    }

@app.get("/users/profile")
async def get_user_profile(
    current_user: Dict[str, Any] = Depends(get_current_user)
):
    """認証必須プロファイル取得"""
    return {
        "user_id": current_user["sub"],
        "role": current_user["role"],
        "permissions": current_user["permissions"]
    }

@app.get("/admin/users")
async def list_all_users(
    request: Request,
    current_user: Dict[str, Any] = Depends(require_permission("users:admin"))
):
    """管理者権限必須ユーザー一覧"""
    return {
        "users": [
            {"id": 1, "name": "User 1"},
            {"id": 2, "name": "User 2"}
        ],
        "accessed_by": current_user["sub"]
    }
```

**Bad: セキュリティ対策不十分**
```python
# セキュリティ対策不十分な悪い例
from fastapi import FastAPI

app = FastAPI()

# 問題: 認証なし
@app.get("/users")
async def get_users():
    return [{"id": 1, "password": "plaintext123"}]  # 問題: パスワード平文

# 問題: 認可チェックなし
@app.delete("/users/{user_id}")
async def delete_user(user_id: int):
    return {"deleted": user_id}  # 誰でも削除可能

# 問題: SQLインジェクション脆弱性
@app.get("/search")
async def search_users(query: str):
    # 問題: 直接SQL文字列結合
    sql = f"SELECT * FROM users WHERE name LIKE '%{query}%'"
    return {"results": []}

# 問題: レート制限なし
@app.post("/send-email")
async def send_email(to: str, subject: str):
    # スパム送信の温床
    return {"sent": True}

# 問題: セキュリティヘッダーなし、HTTPS強制なし
# 問題: エラー情報の過度な露出
@app.get("/debug/{user_id}")
async def debug_user(user_id: int):
    try:
        # 何らかの処理
        pass
    except Exception as e:
        return {"error": str(e), "stack_trace": traceback.format_exc()}  # 内部情報漏洩
```

---

## 2. RESTful API設計標準

### 2.1 リソース設計

#### 2.1.1 リソース命名規則

**Good: 一貫したリソース命名**
```python
# RESTful リソース設計実装
from typing import Dict, List, Any, Optional, Union
from fastapi import FastAPI, Path, Query, Body, Depends, HTTPException, status
from pydantic import BaseModel, Field, validator
from enum import Enum
from datetime import datetime
from dataclasses import dataclass

# リソース命名規則クラス
class ResourceNamingConvention:
    """RESTfulリソース命名規則"""
    
    @staticmethod
    def validate_resource_name(name: str) -> bool:
        """リソース名検証"""
        import re
        
        # 規則: 小文字、複数形、ハイフン区切り
        pattern = r'^[a-z]+(-[a-z]+)*s$'
        return bool(re.match(pattern, name))
    
    @staticmethod
    def get_collection_examples() -> Dict[str, Dict[str, str]]:
        """良いリソース命名例"""
        return {
            "users": {
                "collection": "/users",
                "item": "/users/{user_id}",
                "sub_collection": "/users/{user_id}/posts",
                "sub_item": "/users/{user_id}/posts/{post_id}"
            },
            "blog-posts": {
                "collection": "/blog-posts",
                "item": "/blog-posts/{post_id}",
                "sub_collection": "/blog-posts/{post_id}/comments",
                "sub_item": "/blog-posts/{post_id}/comments/{comment_id}"
            },
            "user-profiles": {
                "collection": "/user-profiles",
                "item": "/user-profiles/{profile_id}",
                "nested": "/users/{user_id}/profile"  # ネストパターン
            },
            "order-items": {
                "collection": "/orders/{order_id}/items",
                "item": "/orders/{order_id}/items/{item_id}",
                "actions": "/orders/{order_id}/items/{item_id}/cancel"  # アクション
            }
        }

class HTTPMethod(str, Enum):
    """HTTP メソッド定義"""
    GET = "GET"
    POST = "POST"
    PUT = "PUT"
    PATCH = "PATCH"
    DELETE = "DELETE"
    OPTIONS = "OPTIONS"
    HEAD = "HEAD"

@dataclass
class RESTEndpoint:
    """RESTエンドポイント定義"""
    path: str
    method: HTTPMethod
    description: str
    resource_type: str  # collection, item, action
    idempotent: bool
    safe: bool
    
    def validate_restful_design(self) -> List[str]:
        """RESTful設計原則検証"""
        issues = []
        
        # パスに動詞が含まれていないかチェック
        verbs = ['create', 'get', 'update', 'delete', 'list', 'search', 'find']
        path_lower = self.path.lower()
        
        for verb in verbs:
            if verb in path_lower:
                issues.append(f"Path contains verb '{verb}' - use HTTP methods instead")
        
        # メソッドとリソースタイプの整合性チェック
        if self.resource_type == "collection":
            if self.method not in [HTTPMethod.GET, HTTPMethod.POST]:
                issues.append(f"Collection should use GET or POST, not {self.method}")
        
        elif self.resource_type == "item":
            if self.method not in [HTTPMethod.GET, HTTPMethod.PUT, HTTPMethod.PATCH, HTTPMethod.DELETE]:
                issues.append(f"Item should use GET/PUT/PATCH/DELETE, not {self.method}")
        
        return issues

# 標準RESTリソース実装パターン
class RESTfulResourceHandler:
    """RESTfulリソースハンドラーベースクラス"""
    
    def __init__(self, resource_name: str, resource_path: str):
        self.resource_name = resource_name
        self.resource_path = resource_path
        self.endpoints = []
    
    def define_collection_endpoints(self) -> List[RESTEndpoint]:
        """コレクションエンドポイント定義"""
        return [
            RESTEndpoint(
                path=self.resource_path,
                method=HTTPMethod.GET,
                description=f"List {self.resource_name}",
                resource_type="collection",
                idempotent=True,
                safe=True
            ),
            RESTEndpoint(
                path=self.resource_path,
                method=HTTPMethod.POST,
                description=f"Create {self.resource_name[:-1]}",  # 単数形
                resource_type="collection",
                idempotent=False,
                safe=False
            )
        ]
    
    def define_item_endpoints(self, id_param: str = "id") -> List[RESTEndpoint]:
        """アイテムエンドポイント定義"""
        item_path = f"{self.resource_path}/{{{id_param}}}"
        
        return [
            RESTEndpoint(
                path=item_path,
                method=HTTPMethod.GET,
                description=f"Get {self.resource_name[:-1]} by ID",
                resource_type="item",
                idempotent=True,
                safe=True
            ),
            RESTEndpoint(
                path=item_path,
                method=HTTPMethod.PUT,
                description=f"Update {self.resource_name[:-1]} (full replacement)",
                resource_type="item",
                idempotent=True,
                safe=False
            ),
            RESTEndpoint(
                path=item_path,
                method=HTTPMethod.PATCH,
                description=f"Partially update {self.resource_name[:-1]}",
                resource_type="item",
                idempotent=True,
                safe=False
            ),
            RESTEndpoint(
                path=item_path,
                method=HTTPMethod.DELETE,
                description=f"Delete {self.resource_name[:-1]}",
                resource_type="item",
                idempotent=True,
                safe=False
            )
        ]
    
    def define_nested_endpoints(self, parent_path: str, nested_resource: str) -> List[RESTEndpoint]:
        """ネストリソースエンドポイント定義"""
        nested_collection = f"{parent_path}/{nested_resource}"
        nested_item = f"{nested_collection}/{{nested_id}}"
        
        return [
            RESTEndpoint(
                path=nested_collection,
                method=HTTPMethod.GET,
                description=f"List {nested_resource} for parent resource",
                resource_type="nested_collection",
                idempotent=True,
                safe=True
            ),
            RESTEndpoint(
                path=nested_collection,
                method=HTTPMethod.POST,
                description=f"Create {nested_resource[:-1]} for parent resource",
                resource_type="nested_collection",
                idempotent=False,
                safe=False
            ),
            RESTEndpoint(
                path=nested_item,
                method=HTTPMethod.GET,
                description=f"Get specific {nested_resource[:-1]} for parent",
                resource_type="nested_item",
                idempotent=True,
                safe=True
            ),
            RESTEndpoint(
                path=nested_item,
                method=HTTPMethod.PUT,
                description=f"Update {nested_resource[:-1]} for parent",
                resource_type="nested_item",
                idempotent=True,
                safe=False
            ),
            RESTEndpoint(
                path=nested_item,
                method=HTTPMethod.DELETE,
                description=f"Delete {nested_resource[:-1]} for parent",
                resource_type="nested_item",
                idempotent=True,
                safe=False
            )
        ]

# 実装例: ユーザーリソース
class UserResourceHandler(RESTfulResourceHandler):
    """ユーザーリソース実装"""
    
    def __init__(self):
        super().__init__("users", "/users")
    
    def get_all_endpoints(self) -> List[RESTEndpoint]:
        """全エンドポイント定義取得"""
        endpoints = []
        
        # 基本エンドポイント
        endpoints.extend(self.define_collection_endpoints())
        endpoints.extend(self.define_item_endpoints("user_id"))
        
        # ネストリソース
        endpoints.extend(self.define_nested_endpoints("/users/{user_id}", "posts"))
        endpoints.extend(self.define_nested_endpoints("/users/{user_id}", "comments"))
        
        # カスタムアクション (RESTful原則に従い最小限に)
        endpoints.extend([
            RESTEndpoint(
                path="/users/{user_id}/activate",
                method=HTTPMethod.POST,
                description="Activate user account",
                resource_type="action",
                idempotent=True,
                safe=False
            ),
            RESTEndpoint(
                path="/users/{user_id}/deactivate",
                method=HTTPMethod.POST,
                description="Deactivate user account",
                resource_type="action",
                idempotent=True,
                safe=False
            ),
            RESTEndpoint(
                path="/users/{user_id}/change-password",
                method=HTTPMethod.POST,
                description="Change user password",
                resource_type="action",
                idempotent=False,
                safe=False
            )
        ])
        
        return endpoints

# FastAPI実装例
app = FastAPI(
    title="RESTful API Example",
    version="1.0.0",
    description="Demonstrates RESTful resource design patterns"
)

# データモデル定義
class UserBase(BaseModel):
    """ユーザーベースモデル"""
    name: str = Field(..., min_length=2, max_length=100)
    email: str = Field(..., regex=r'^[\w\.-]+@[\w\.-]+\.\w+$')
    age: int = Field(..., ge=18, le=120)

class UserCreate(UserBase):
    """ユーザー作成モデル"""
    password: str = Field(..., min_length=8)
    
    @validator('password')
    def validate_password(cls, v):
        """パスワード強度チェック"""
        import re
        if not re.search(r'[A-Z]', v):
            raise ValueError('Password must contain uppercase letter')
        if not re.search(r'[a-z]', v):
            raise ValueError('Password must contain lowercase letter')
        if not re.search(r'\d', v):
            raise ValueError('Password must contain digit')
        return v

class UserUpdate(BaseModel):
    """ユーザー更新モデル（部分更新対応）"""
    name: Optional[str] = Field(None, min_length=2, max_length=100)
    email: Optional[str] = Field(None, regex=r'^[\w\.-]+@[\w\.-]+\.\w+$')
    age: Optional[int] = Field(None, ge=18, le=120)

class UserResponse(UserBase):
    """ユーザーレスポンスモデル"""
    id: int
    created_at: datetime
    updated_at: datetime
    is_active: bool
    
    class Config:
        orm_mode = True

# RESTfulエンドポイント実装
@app.get("/users", response_model=PaginatedResponse[UserResponse])
async def list_users(
    page: int = Query(1, ge=1, description="Page number"),
    limit: int = Query(20, ge=1, le=100, description="Items per page"),
    search: Optional[str] = Query(None, min_length=2, description="Search term"),
    is_active: Optional[bool] = Query(None, description="Filter by active status"),
    sort_by: str = Query("created_at", regex=r'^(name|email|created_at|updated_at)$'),
    sort_order: str = Query("desc", regex=r'^(asc|desc)$')
):
    """ユーザー一覧取得 (RESTful Collection)"""
    
    # クエリパラメータ処理
    filters = {}
    if search:
        filters['search'] = search
    if is_active is not None:
        filters['is_active'] = is_active
    
    # データ取得（実際の実装ではデータベース操作）
    users_data = [
        {
            "id": 1,
            "name": "John Doe",
            "email": "john@example.com",
            "age": 30,
            "created_at": datetime.now(),
            "updated_at": datetime.now(),
            "is_active": True
        }
    ]
    
    pagination_params = PaginationParams(page=page, limit=limit)
    
    return PaginatedResponse.create(
        data=users_data,
        pagination_params=pagination_params,
        total_items=1
    )

@app.post("/users", response_model=StandardAPIResponse[UserResponse], status_code=status.HTTP_201_CREATED)
async def create_user(
    user_data: UserCreate = Body(..., description="User creation data")
):
    """ユーザー作成 (RESTful Collection)"""
    
    # バリデーション
    # 実際の実装では重複チェックなど
    
    # ユーザー作成処理
    created_user = {
        "id": 123,
        "name": user_data.name,
        "email": user_data.email,
        "age": user_data.age,
        "created_at": datetime.now(),
        "updated_at": datetime.now(),
        "is_active": True
    }
    
    return StandardAPIResponse.success(
        data=created_user,
        request_id=str(uuid.uuid4())
    )

@app.get("/users/{user_id}", response_model=StandardAPIResponse[UserResponse])
async def get_user(
    user_id: int = Path(..., ge=1, description="User ID")
):
    """単一ユーザー取得 (RESTful Item)"""
    
    # ユーザー存在チェック
    user_data = {
        "id": user_id,
        "name": "John Doe",
        "email": "john@example.com",
        "age": 30,
        "created_at": datetime.now(),
        "updated_at": datetime.now(),
        "is_active": True
    }
    
    if not user_data:  # 実際の実装では存在チェック
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={
                "error": {
                    "code": "USER_NOT_FOUND",
                    "message": f"User with ID {user_id} not found"
                }
            }
        )
    
    return StandardAPIResponse.success(
        data=user_data,
        request_id=str(uuid.uuid4())
    )

@app.put("/users/{user_id}", response_model=StandardAPIResponse[UserResponse])
async def update_user_full(
    user_id: int = Path(..., ge=1, description="User ID"),
    user_data: UserBase = Body(..., description="Complete user data")
):
    """ユーザー完全更新 (RESTful Item - PUT)"""
    
    # 存在チェック
    existing_user = {"id": user_id}  # 実際の実装では DB 取得
    
    if not existing_user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"error": {"code": "USER_NOT_FOUND", "message": "User not found"}}
        )
    
    # 完全置換更新
    updated_user = {
        "id": user_id,
        "name": user_data.name,
        "email": user_data.email,
        "age": user_data.age,
        "created_at": datetime.now(),  # 既存値保持
        "updated_at": datetime.now(),  # 更新時刻更新
        "is_active": True
    }
    
    return StandardAPIResponse.success(
        data=updated_user,
        request_id=str(uuid.uuid4())
    )

@app.patch("/users/{user_id}", response_model=StandardAPIResponse[UserResponse])
async def update_user_partial(
    user_id: int = Path(..., ge=1, description="User ID"),
    user_data: UserUpdate = Body(..., description="Partial user data")
):
    """ユーザー部分更新 (RESTful Item - PATCH)"""
    
    # 存在チェック
    existing_user = {
        "id": user_id,
        "name": "John Doe",
        "email": "john@example.com",
        "age": 30,
        "created_at": datetime.now(),
        "updated_at": datetime.now(),
        "is_active": True
    }
    
    if not existing_user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"error": {"code": "USER_NOT_FOUND", "message": "User not found"}}
        )
    
    # 部分更新（提供されたフィールドのみ更新）
    update_data = user_data.dict(exclude_unset=True)
    
    for field, value in update_data.items():
        if hasattr(existing_user, field):
            existing_user[field] = value
    
    existing_user["updated_at"] = datetime.now()
    
    return StandardAPIResponse.success(
        data=existing_user,
        request_id=str(uuid.uuid4())
    )

@app.delete("/users/{user_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_user(
    user_id: int = Path(..., ge=1, description="User ID")
):
    """ユーザー削除 (RESTful Item - DELETE)"""
    
    # 存在チェック
    existing_user = {"id": user_id}  # 実際の実装では DB 取得
    
    if not existing_user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"error": {"code": "USER_NOT_FOUND", "message": "User not found"}}
        )
    
    # 削除処理（実際の実装では論理削除推奨）
    # database.delete_user(user_id)
    
    # 204 No Content - レスポンスボディなし
    return None

# ネストリソース実装例
@app.get("/users/{user_id}/posts", response_model=PaginatedResponse[Dict[str, Any]])
async def list_user_posts(
    user_id: int = Path(..., ge=1, description="User ID"),
    page: int = Query(1, ge=1),
    limit: int = Query(20, ge=1, le=100)
):
    """ユーザー投稿一覧 (Nested Collection)"""
    
    # 親リソース存在チェック
    user_exists = True  # 実際の実装では DB チェック
    
    if not user_exists:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"error": {"code": "USER_NOT_FOUND", "message": "User not found"}}
        )
    
    # ユーザーの投稿取得
    posts_data = [
        {
            "id": 1,
            "title": "My First Post",
            "content": "Hello World!",
            "user_id": user_id,
            "created_at": datetime.now().isoformat()
        }
    ]
    
    pagination_params = PaginationParams(page=page, limit=limit)
    
    return PaginatedResponse.create(
        data=posts_data,
        pagination_params=pagination_params,
        total_items=1
    )

@app.post("/users/{user_id}/posts", response_model=StandardAPIResponse[Dict[str, Any]], status_code=status.HTTP_201_CREATED)
async def create_user_post(
    user_id: int = Path(..., ge=1, description="User ID"),
    post_data: Dict[str, Any] = Body(..., description="Post creation data")
):
    """ユーザー投稿作成 (Nested Collection)"""
    
    # 親リソース存在チェック
    user_exists = True
    
    if not user_exists:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"error": {"code": "USER_NOT_FOUND", "message": "User not found"}}
        )
    
    # 投稿作成
    created_post = {
        "id": 123,
        "title": post_data.get("title"),
        "content": post_data.get("content"),
        "user_id": user_id,
        "created_at": datetime.now().isoformat()
    }
    
    return StandardAPIResponse.success(
        data=created_post,
        request_id=str(uuid.uuid4())
    )

# カスタムアクション実装例
@app.post("/users/{user_id}/activate", response_model=StandardAPIResponse[Dict[str, Any]])
async def activate_user(
    user_id: int = Path(..., ge=1, description="User ID")
):
    """ユーザーアクティベート (Custom Action)"""
    
    # 存在チェック
    user_data = {"id": user_id, "is_active": False}
    
    if not user_data:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"error": {"code": "USER_NOT_FOUND", "message": "User not found"}}
        )
    
    if user_data["is_active"]:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={"error": {"code": "ALREADY_ACTIVE", "message": "User is already active"}}
        )
    
    # アクティベート処理
    user_data["is_active"] = True
    user_data["updated_at"] = datetime.now().isoformat()
    
    return StandardAPIResponse.success(
        data={
            "user_id": user_id,
            "status": "activated",
            "activated_at": datetime.now().isoformat()
        },
        request_id=str(uuid.uuid4())
    )
```

**Bad: RESTful原則未準拠**
```python
# RESTful原則未準拠の悪い例
from fastapi import FastAPI

app = FastAPI()

# 問題: 動詞を含むパス
@app.get("/getUserById/{user_id}")  # 悪い: 動詞含む
async def get_user_bad(user_id: int):
    return {"user": {}}

@app.post("/createUser")  # 悪い: 動詞含む
async def create_user_bad():
    return {"created": True}

@app.get("/deleteUser/{user_id}")  # 問題: GETで削除
async def delete_user_bad(user_id: int):
    return {"deleted": True}

# 問題: 不適切なHTTPステータスコード
@app.post("/users")
async def create_user_bad_status():
    return {"status": "created"}  # 200ではなく201であるべき

@app.delete("/users/{user_id}")
async def delete_user_bad_status(user_id: int):
    return {"message": "deleted"}  # 204 No Contentであるべき

# 問題: 一貫性のないリソース命名
@app.get("/user")  # 単数形
async def list_users_bad():
    return []

@app.get("/users_list")  # アンダースコア使用
async def list_users_bad2():
    return []

# 問題: ネストリソースの不適切な設計
@app.get("/posts/by-user/{user_id}")  # 悪い: リソース関係が不明確
async def get_posts_by_user_bad(user_id: int):
    return []

# 適切: /users/{user_id}/posts であるべき
```

#### 2.1.2 HTTPメソッド適用原則

**Good: 適切なHTTPメソッド使用**
```python
# HTTPメソッド適用原則実装
from typing import Dict, List, Any, Optional
from fastapi import FastAPI, Request, Response, status
from enum import Enum
from dataclasses import dataclass
import uuid
from datetime import datetime

class HTTPMethodSemantics(str, Enum):
    """HTTPメソッドセマンティクス定義"""
    
    # セーフメソッド（副作用なし）
    GET = "GET"        # リソース取得
    HEAD = "HEAD"      # ヘッダー情報のみ取得
    OPTIONS = "OPTIONS" # 利用可能メソッド取得
    
    # 非セーフメソッド（副作用あり）
    POST = "POST"      # リソース作成、アクション実行
    PUT = "PUT"        # リソース完全更新・作成
    PATCH = "PATCH"    # リソース部分更新
    DELETE = "DELETE"  # リソース削除

@dataclass
class HTTPMethodProperties:
    """HTTPメソッド特性定義"""
    method: HTTPMethodSemantics
    safe: bool           # セーフ（副作用なし）
    idempotent: bool     # 冪等性
    cacheable: bool      # キャッシュ可能
    request_body: bool   # リクエストボディ許可
    response_body: bool  # レスポンスボディ許可
    
    @classmethod
    def get_properties(cls, method: HTTPMethodSemantics) -> 'HTTPMethodProperties':
        """メソッド特性取得"""
        properties_map = {
            HTTPMethodSemantics.GET: cls(
                method=HTTPMethodSemantics.GET,
                safe=True, idempotent=True, cacheable=True,
                request_body=False, response_body=True
            ),
            HTTPMethodSemantics.HEAD: cls(
                method=HTTPMethodSemantics.HEAD,
                safe=True, idempotent=True, cacheable=True,
                request_body=False, response_body=False
            ),
            HTTPMethodSemantics.OPTIONS: cls(
                method=HTTPMethodSemantics.OPTIONS,
                safe=True, idempotent=True, cacheable=True,
                request_body=False, response_body=True
            ),
            HTTPMethodSemantics.POST: cls(
                method=HTTPMethodSemantics.POST,
                safe=False, idempotent=False, cacheable=False,
                request_body=True, response_body=True
            ),
            HTTPMethodSemantics.PUT: cls(
                method=HTTPMethodSemantics.PUT,
                safe=False, idempotent=True, cacheable=False,
                request_body=True, response_body=True
            ),
            HTTPMethodSemantics.PATCH: cls(
                method=HTTPMethodSemantics.PATCH,
                safe=False, idempotent=True, cacheable=False,
                request_body=True, response_body=True
            ),
            HTTPMethodSemantics.DELETE: cls(
                method=HTTPMethodSemantics.DELETE,
                safe=False, idempotent=True, cacheable=False,
                request_body=False, response_body=False
            )
        }
        
        return properties_map[method]

class RESTfulMethodHandler:
    """RESTfulメソッドハンドラー"""
    
    @staticmethod
    def validate_method_usage(method: str, endpoint_type: str, has_body: bool) -> List[str]:
        """メソッド使用法検証"""
        issues = []
        
        method_enum = HTTPMethodSemantics(method.upper())
        properties = HTTPMethodProperties.get_properties(method_enum)
        
        # リクエストボディ検証
        if has_body and not properties.request_body:
            issues.append(f"{method} should not have request body")
        
        # エンドポイントタイプとメソッドの整合性
        if endpoint_type == "collection":
            if method not in ["GET", "POST", "OPTIONS"]:
                issues.append(f"Collection endpoints should use GET/POST/OPTIONS, not {method}")
        
        elif endpoint_type == "item":
            if method not in ["GET", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"]:
                issues.append(f"Item endpoints should use GET/PUT/PATCH/DELETE/HEAD/OPTIONS, not {method}")
        
        return issues
    
    @staticmethod
    def get_appropriate_status_codes(method: HTTPMethodSemantics) -> Dict[str, int]:
        """メソッド別適切なステータスコード"""
        status_codes = {
            HTTPMethodSemantics.GET: {
                "success": status.HTTP_200_OK,
                "not_found": status.HTTP_404_NOT_FOUND,
                "error": status.HTTP_500_INTERNAL_SERVER_ERROR
            },
            HTTPMethodSemantics.POST: {
                "created": status.HTTP_201_CREATED,
                "accepted": status.HTTP_202_ACCEPTED,
                "bad_request": status.HTTP_400_BAD_REQUEST,
                "conflict": status.HTTP_409_CONFLICT
            },
            HTTPMethodSemantics.PUT: {
                "updated": status.HTTP_200_OK,
                "created": status.HTTP_201_CREATED,
                "no_content": status.HTTP_204_NO_CONTENT,
                "bad_request": status.HTTP_400_BAD_REQUEST
            },
            HTTPMethodSemantics.PATCH: {
                "updated": status.HTTP_200_OK,
                "no_content": status.HTTP_204_NO_CONTENT,
                "bad_request": status.HTTP_400_BAD_REQUEST,
                "not_found": status.HTTP_404_NOT_FOUND
            },
            HTTPMethodSemantics.DELETE: {
                "deleted": status.HTTP_204_NO_CONTENT,
                "accepted": status.HTTP_202_ACCEPTED,
                "not_found": status.HTTP_404_NOT_FOUND
            },
            HTTPMethodSemantics.HEAD: {
                "success": status.HTTP_200_OK,
                "not_found": status.HTTP_404_NOT_FOUND
            },
            HTTPMethodSemantics.OPTIONS: {
                "success": status.HTTP_200_OK
            }
        }
        
        return status_codes.get(method, {})

app = FastAPI(
    title="HTTP Method Semantics Example",
    version="1.0.0"
)

# HTTPメソッド適用実装例

# GET - セーフ、冪等
@app.get("/users/{user_id}")
async def get_user(user_id: int, response: Response):
    """ユーザー取得 - セーフ、冪等操作"""
    
    # キャッシュヘッダー設定（GETは cache-aware）
    response.headers["Cache-Control"] = "max-age=300, public"
    response.headers["ETag"] = f'"user-{user_id}-v1"'
    
    user_data = {
        "id": user_id,
        "name": "John Doe",
        "email": "john@example.com"
    }
    
    return StandardAPIResponse.success(data=user_data)

# HEAD - ヘッダー情報のみ
@app.head("/users/{user_id}")
async def head_user(user_id: int, response: Response):
    """ユーザー存在確認 - ヘッダーのみ返却"""
    
    # ユーザー存在チェック
    user_exists = True  # 実際の実装では DB チェック
    
    if not user_exists:
        response.status_code = status.HTTP_404_NOT_FOUND
        return
    
    # メタデータヘッダー設定
    response.headers["Content-Type"] = "application/json"
    response.headers["Content-Length"] = "150"  # 推定サイズ
    response.headers["Last-Modified"] = datetime.now().strftime("%a, %d %b %Y %H:%M:%S GMT")
    response.headers["ETag"] = f'"user-{user_id}-v1"'
    
    response.status_code = status.HTTP_200_OK
    # ボディは返却しない

# OPTIONS - メソッド情報取得
@app.options("/users/{user_id}")
async def options_user(user_id: int):
    """利用可能メソッド情報"""
    
    return {
        "allowed_methods": ["GET", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"],
        "resource_schema": {
            "type": "object",
            "properties": {
                "id": {"type": "integer"},
                "name": {"type": "string"},
                "email": {"type": "string"}
            }
        },
        "supported_content_types": ["application/json"],
        "rate_limits": {
            "GET": "100 requests/hour",
            "PUT": "20 requests/hour",
            "DELETE": "5 requests/hour"
        }
    }

# POST - 非冪等、作成・アクション
@app.post("/users", status_code=status.HTTP_201_CREATED)
async def create_user(user_data: Dict[str, Any]):
    """ユーザー作成 - 非冪等操作"""
    
    # 重複チェック（POSTは非冪等なので重複作成防止）
    if user_data.get("email") == "existing@example.com":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail={
                "error": {
                    "code": "EMAIL_ALREADY_EXISTS",
                    "message": "User with this email already exists"
                }
            }
        )
    
    # 新規ユーザー作成
    created_user = {
        "id": 123,  # 新規生成ID
        "name": user_data["name"],
        "email": user_data["email"],
        "created_at": datetime.now().isoformat()
    }
    
    # Location ヘッダー設定
    response = Response(
        content=StandardAPIResponse.success(data=created_user).json(),
        status_code=status.HTTP_201_CREATED,
        headers={
            "Location": f"/users/{created_user['id']}",
            "Content-Type": "application/json"
        }
    )
    
    return response

# PUT - 冪等、完全更新・作成
@app.put("/users/{user_id}")
async def put_user(user_id: int, user_data: Dict[str, Any]):
    """ユーザー完全更新/作成 - 冪等操作"""
    
    # 既存ユーザーチェック
    existing_user = None  # 実際の実装では DB 取得
    
    if existing_user:
        # 完全更新（既存フィールドをすべて置換）
        updated_user = {
            "id": user_id,
            "name": user_data["name"],
            "email": user_data["email"],
            "created_at": existing_user["created_at"],
            "updated_at": datetime.now().isoformat()
        }
        
        return StandardAPIResponse.success(
            data=updated_user,
            metadata={"operation": "updated"}
        )
    
    else:
        # 新規作成（PUT は idempotent なので ID 指定作成可）
        created_user = {
            "id": user_id,
            "name": user_data["name"],
            "email": user_data["email"],
            "created_at": datetime.now().isoformat(),
            "updated_at": datetime.now().isoformat()
        }
        
        response = Response(
            content=StandardAPIResponse.success(data=created_user).json(),
            status_code=status.HTTP_201_CREATED,
            headers={
                "Location": f"/users/{user_id}",
                "Content-Type": "application/json"
            }
        )
        
        return response

# PATCH - 冪等、部分更新
@app.patch("/users/{user_id}")
async def patch_user(user_id: int, user_data: Dict[str, Any]):
    """ユーザー部分更新 - 冪等操作"""
    
    # 既存ユーザー取得
    existing_user = {
        "id": user_id,
        "name": "John Doe",
        "email": "john@example.com",
        "created_at": "2024-01-01T00:00:00Z"
    }
    
    if not existing_user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"error": {"code": "USER_NOT_FOUND", "message": "User not found"}}
        )
    
    # 部分更新（提供されたフィールドのみ更新）
    updated_fields = []
    for field, value in user_data.items():
        if field in existing_user and existing_user[field] != value:
            existing_user[field] = value
            updated_fields.append(field)
    
    existing_user["updated_at"] = datetime.now().isoformat()
    
    if not updated_fields:
        # 変更なしの場合 304 Not Modified も適切
        return Response(status_code=status.HTTP_304_NOT_MODIFIED)
    
    return StandardAPIResponse.success(
        data=existing_user,
        metadata={
            "operation": "partially_updated",
            "updated_fields": updated_fields
        }
    )

# DELETE - 冪等、削除
@app.delete("/users/{user_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_user(user_id: int):
    """ユーザー削除 - 冪等操作"""
    
    # 既存ユーザー確認
    existing_user = {"id": user_id}  # 実際の実装では DB 取得
    
    if not existing_user:
        # 冪等性により、既に削除済みでも成功レスポンス
        # ただし、リソースが存在したことがない場合は 404 も適切
        pass
    
    # 削除処理（論理削除推奨）
    # database.soft_delete_user(user_id)
    
    # 204 No Content - レスポンスボディなし
    return None

# カスタムアクション（POST）
@app.post("/users/{user_id}/send-email")
async def send_user_email(user_id: int, email_data: Dict[str, Any]):
    """ユーザーにメール送信 - 非冪等アクション"""
    
    # ユーザー存在確認
    user_exists = True
    
    if not user_exists:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"error": {"code": "USER_NOT_FOUND", "message": "User not found"}}
        )
    
    # メール送信処理（副作用あり、非冪等）
    email_id = str(uuid.uuid4())
    
    # 非同期実行の場合は 202 Accepted
    return Response(
        content=StandardAPIResponse.success(
            data={
                "email_id": email_id,
                "status": "queued",
                "estimated_delivery": "2-5 minutes"
            }
        ).json(),
        status_code=status.HTTP_202_ACCEPTED,
        headers={
            "Location": f"/emails/{email_id}",
            "Content-Type": "application/json"
        }
    )

# 条件付きリクエスト実装例
@app.get("/users/{user_id}/conditional")
async def get_user_conditional(
    user_id: int,
    request: Request,
    response: Response
):
    """条件付きリクエスト対応"""
    
    # 現在のリソース状態
    user_data = {
        "id": user_id,
        "name": "John Doe",
        "version": "v2",
        "last_modified": "2024-01-15T12:00:00Z"
    }
    
    current_etag = f'"user-{user_id}-v2"'
    current_modified = "Mon, 15 Jan 2024 12:00:00 GMT"
    
    # If-None-Match ヘッダーチェック
    if_none_match = request.headers.get("If-None-Match")
    if if_none_match and if_none_match == current_etag:
        response.status_code = status.HTTP_304_NOT_MODIFIED
        response.headers["ETag"] = current_etag
        return None
    
    # If-Modified-Since ヘッダーチェック
    if_modified_since = request.headers.get("If-Modified-Since")
    if if_modified_since and if_modified_since == current_modified:
        response.status_code = status.HTTP_304_NOT_MODIFIED
        response.headers["Last-Modified"] = current_modified
        return None
    
    # キャッシュヘッダー設定
    response.headers["ETag"] = current_etag
    response.headers["Last-Modified"] = current_modified
    response.headers["Cache-Control"] = "max-age=300"
    
    return StandardAPIResponse.success(data=user_data)
```

**Bad: HTTPメソッド誤用**
```python
# HTTPメソッド誤用の悪い例
from fastapi import FastAPI

app = FastAPI()

# 問題: 副作用のある操作にGET使用
@app.get("/users/{user_id}/delete")  # 悪い: GETで削除
async def delete_user_bad(user_id: int):
    # database.delete_user(user_id)  # 副作用あり
    return {"deleted": True}

@app.get("/users/{user_id}/activate")  # 悪い: GETでアクティベート
async def activate_user_bad(user_id: int):
    # database.activate_user(user_id)  #状態変更
    return {"activated": True}

# 問題: 不適切なステータスコード
@app.post("/users")
async def create_user_bad_status():
    return {"created": True}  # 200ではなく201であるべき

@app.delete("/users/{user_id}")
async def delete_user_bad_status(user_id: int):
    return {"message": "deleted"}  # 204 No Contentであるべき

# 問題: 冪等性無視
@app.put("/users/{user_id}/increment-login-count")  # 悪い: 非冪等操作にPUT
async def increment_login_count_bad(user_id: int):
    # login_count += 1  # 非冪等操作
    return {"login_count": 10}

# 問題: POST以外でリクエストボディ
@app.get("/users/search")  # 悪い: GETでボディ
async def search_users_bad(search_data: dict):
    return []

# 問題: HEADでボディ返却
@app.head("/users/{user_id}")
async def head_user_bad(user_id: int):
    return {"id": user_id}  # 悪い: HEADはボディなし
```

@app.delete("/admin/users/{user_id}")
async def delete_user(
    user_id: str,
    current_user: Dict[str, Any] = Depends(require_permission("users:delete"))
):
    """権限必須ユーザー削除"""
    return {"message": f"User {user_id} deleted", "deleted_by": current_user["sub"]}
```

---

## 2. RESTful API設計標準

### 2.1 リソース設計原則

#### 2.1.1 リソース命名規則

**Good: 適切なリソース命名**
```python
# Django REST Framework - 適切なリソース設計
from rest_framework import viewsets, serializers
from rest_framework.decorators import action
from rest_framework.response import Response
from django.db import models

# モデル定義
class User(models.Model):
    username = models.CharField(max_length=150, unique=True)
    email = models.EmailField(unique=True)
    created_at = models.DateTimeField(auto_now_add=True)
    is_active = models.BooleanField(default=True)
    
    class Meta:
        db_table = 'users'
        ordering = ['-created_at']

class Post(models.Model):
    title = models.CharField(max_length=200)
    content = models.TextField()
    author = models.ForeignKey(User, on_delete=models.CASCADE, related_name='posts')
    tags = models.ManyToManyField('Tag', blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    
    class Meta:
        db_table = 'posts'
        ordering = ['-created_at']

class Comment(models.Model):
    content = models.TextField()
    post = models.ForeignKey(Post, on_delete=models.CASCADE, related_name='comments')
    author = models.ForeignKey(User, on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now_add=True)
    
    class Meta:
        db_table = 'comments'
        ordering = ['-created_at']

# シリアライザー
class UserSerializer(serializers.ModelSerializer):
    posts_count = serializers.SerializerMethodField()
    
    class Meta:
        model = User
        fields = ['id', 'username', 'email', 'created_at', 'is_active', 'posts_count']
        read_only_fields = ['id', 'created_at']
    
    def get_posts_count(self, obj):
        return obj.posts.count()

class CommentSerializer(serializers.ModelSerializer):
    author_username = serializers.CharField(source='author.username', read_only=True)
    
    class Meta:
        model = Comment
        fields = ['id', 'content', 'author', 'author_username', 'created_at']
        read_only_fields = ['id', 'created_at', 'author_username']

class PostSerializer(serializers.ModelSerializer):
    author_username = serializers.CharField(source='author.username', read_only=True)
    comments_count = serializers.SerializerMethodField()
    tags = serializers.StringRelatedField(many=True, read_only=True)
    
    class Meta:
        model = Post
        fields = ['id', 'title', 'content', 'author', 'author_username', 
                 'tags', 'comments_count', 'created_at']
        read_only_fields = ['id', 'created_at', 'author_username']
    
    def get_comments_count(self, obj):
        return obj.comments.count()

# ViewSet - RESTful設計パターン
class UserViewSet(viewsets.ModelViewSet):
    """
    標準的なCRUD操作を提供するユーザーViewSet
    
    リソースURL設計:
    - GET /api/users/ - ユーザー一覧
    - POST /api/users/ - ユーザー作成
    - GET /api/users/{id}/ - ユーザー詳細
    - PUT /api/users/{id}/ - ユーザー更新（全体）
    - PATCH /api/users/{id}/ - ユーザー更新（部分）
    - DELETE /api/users/{id}/ - ユーザー削除
    """
    queryset = User.objects.all()
    serializer_class = UserSerializer
    lookup_field = 'id'
    
    @action(detail=True, methods=['get'])
    def posts(self, request, id=None):
        """ユーザーの投稿一覧取得"""
        user = self.get_object()
        posts = user.posts.all()
        serializer = PostSerializer(posts, many=True)
        return Response({
            'user_id': user.id,
            'posts': serializer.data,
            'total_count': posts.count()
        })
    
    @action(detail=True, methods=['post'])
    def activate(self, request, id=None):
        """ユーザーアクティベーション"""
        user = self.get_object()
        user.is_active = True
        user.save()
        return Response({'message': 'User activated successfully'})
    
    @action(detail=True, methods=['post'])
    def deactivate(self, request, id=None):
        """ユーザー非アクティベーション"""
        user = self.get_object()
        user.is_active = False
        user.save()
        return Response({'message': 'User deactivated successfully'})

# URL設計（urls.py）
from django.urls import path, include
from rest_framework.routers import DefaultRouter

router = DefaultRouter()
router.register(r'users', UserViewSet, basename='user')
router.register(r'posts', PostViewSet, basename='post')
router.register(r'comments', CommentViewSet, basename='comment')

urlpatterns = [
    path('api/', include(router.urls)),
    # ネストしたリソース
    path('api/users/<int:user_id>/posts/', UserPostsView.as_view(), name='user-posts'),
    path('api/posts/<int:post_id>/comments/', PostCommentsView.as_view(), name='post-comments'),
]
```

**Bad: 不適切なリソース命名**
```python
# 悪い例：動詞ベースのURL設計
@api_view(['GET'])
def get_user_list(request):  # 悪い: 動詞を含む
    pass

@api_view(['POST'])
def create_new_user(request):  # 悪い: 動詞を含む
    pass

@api_view(['GET'])
def show_user_posts(request, user_id):  # 悪い: 動詞を含む
    pass

# 悪いURL設計
urlpatterns = [
    path('api/getUserList/', get_user_list),  # 悪い: camelCase + 動詞
    path('api/createUser/', create_new_user),  # 悪い: 動詞ベース
    path('api/showUserPosts/<int:user_id>/', show_user_posts),  # 悪い: 動詞ベース
    path('api/user_data/', user_data_view),  # 悪い: 不明確な命名
]
```

#### 2.1.2 ネストしたリソース設計

**Good: 適切なリソース階層**
```python
# FastAPI - ネストしたリソース設計
from fastapi import FastAPI, Depends, HTTPException, Query
from sqlalchemy.orm import Session
from typing import List, Optional
import uuid

app = FastAPI(title="Blog API", version="1.0.0")

# 階層リソース設計
@app.get("/users/{user_id}/posts", response_model=List[PostResponse])
async def get_user_posts(
    user_id: uuid.UUID,
    skip: int = Query(0, ge=0),
    limit: int = Query(10, ge=1, le=100),
    published_only: bool = Query(True),
    db: Session = Depends(get_db)
):
    """
    特定ユーザーの投稿一覧取得
    
    - **user_id**: ユーザーUUID
    - **skip**: スキップ件数（ページネーション）
    - **limit**: 取得件数上限
    - **published_only**: 公開投稿のみフィルタ
    """
    query = db.query(Post).filter(Post.author_id == user_id)
    
    if published_only:
        query = query.filter(Post.is_published == True)
    
    posts = query.offset(skip).limit(limit).all()
    
    if not posts and skip == 0:
        # ユーザーが存在するかチェック
        user = db.query(User).filter(User.id == user_id).first()
        if not user:
            raise HTTPException(status_code=404, detail="User not found")
    
    return posts

@app.post("/users/{user_id}/posts", response_model=PostResponse, status_code=201)
async def create_user_post(
    user_id: uuid.UUID,
    post: PostCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """
    特定ユーザーの新規投稿作成
    
    権限チェック：自分の投稿のみ作成可能
    """
    # 権限チェック
    if current_user.id != user_id:
        raise HTTPException(
            status_code=403, 
            detail="Can only create posts for yourself"
        )
    
    # ユーザー存在チェック
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    db_post = Post(**post.dict(), author_id=user_id)
    db.add(db_post)
    db.commit()
    db.refresh(db_post)
    
    return db_post

@app.get("/posts/{post_id}/comments", response_model=List[CommentResponse])
async def get_post_comments(
    post_id: uuid.UUID,
    skip: int = Query(0, ge=0),
    limit: int = Query(20, ge=1, le=100),
    sort_order: str = Query("desc", regex="^(asc|desc)$"),
    db: Session = Depends(get_db)
):
    """
    特定投稿のコメント一覧取得
    
    - **post_id**: 投稿UUID
    - **sort_order**: ソート順序（asc/desc）
    """
    # 投稿存在チェック
    post = db.query(Post).filter(Post.id == post_id).first()
    if not post:
        raise HTTPException(status_code=404, detail="Post not found")
    
    query = db.query(Comment).filter(Comment.post_id == post_id)
    
    if sort_order == "asc":
        query = query.order_by(Comment.created_at.asc())
    else:
        query = query.order_by(Comment.created_at.desc())
    
    comments = query.offset(skip).limit(limit).all()
    return comments

@app.post("/posts/{post_id}/comments", response_model=CommentResponse, status_code=201)
async def create_post_comment(
    post_id: uuid.UUID,
    comment: CommentCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """
    投稿にコメント追加
    """
    # 投稿存在チェック
    post = db.query(Post).filter(Post.id == post_id).first()
    if not post:
        raise HTTPException(status_code=404, detail="Post not found")
    
    # 投稿が公開されているかチェック
    if not post.is_published:
        raise HTTPException(status_code=403, detail="Cannot comment on unpublished post")
    
    db_comment = Comment(
        content=comment.content,
        post_id=post_id,
        author_id=current_user.id
    )
    db.add(db_comment)
    db.commit()
    db.refresh(db_comment)
    
    return db_comment

# より深いネスト（3階層）
@app.get("/users/{user_id}/posts/{post_id}/comments", response_model=List[CommentResponse])
async def get_user_post_comments(
    user_id: uuid.UUID,
    post_id: uuid.UUID,
    db: Session = Depends(get_db)
):
    """
    特定ユーザーの特定投稿のコメント取得
    
    3階層ネスト例
    """
    # ユーザーと投稿の関連性チェック
    post = db.query(Post).filter(
        Post.id == post_id,
        Post.author_id == user_id
    ).first()
    
    if not post:
        raise HTTPException(
            status_code=404, 
            detail="Post not found or does not belong to the user"
        )
    
    comments = db.query(Comment).filter(Comment.post_id == post_id).all()
    return comments
```

**Bad: 不適切なネスト設計**
```python
# 悪い例：過度なネスト
@app.get("/users/{user_id}/posts/{post_id}/comments/{comment_id}/replies/{reply_id}/likes")
async def get_reply_likes():  # 悪い: 6階層は深すぎる
    pass

# 悪い例：不一貫なリソース階層
@app.get("/posts/{post_id}/author")  # 悪い: /users/{user_id}の方が適切
async def get_post_author():
    pass

@app.get("/comments/{comment_id}/post/author")  # 悪い: 複雑すぎる階層
async def get_comment_post_author():
    pass
```

### 2.2 HTTPメソッド標準利用

#### 2.2.1 標準CRUDパターン

**Good: 適切なHTTPメソッド使用**
```python
# Flask - 標準的なCRUDパターン実装
from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from marshmallow import Schema, fields, ValidationError
import uuid
from datetime import datetime

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'postgresql://localhost/api_db'
db = SQLAlchemy(app)

# モデル定義
class User(db.Model):
    __tablename__ = 'users'
    
    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    username = db.Column(db.String(150), unique=True, nullable=False)
    email = db.Column(db.String(255), unique=True, nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    is_active = db.Column(db.Boolean, default=True)

# バリデーションスキーマ
class UserSchema(Schema):
    id = fields.Str(dump_only=True)
    username = fields.Str(required=True, validate=fields.Length(min=3, max=150))
    email = fields.Email(required=True)
    created_at = fields.DateTime(dump_only=True)
    updated_at = fields.DateTime(dump_only=True)
    is_active = fields.Bool(dump_only=True)

class UserUpdateSchema(Schema):
    username = fields.Str(validate=fields.Length(min=3, max=150))
    email = fields.Email()

user_schema = UserSchema()
users_schema = UserSchema(many=True)
user_update_schema = UserUpdateSchema()

# CREATE - POST /users
@app.route('/users', methods=['POST'])
def create_user():
    """
    新規ユーザー作成
    
    Request Body:
    {
        "username": "john_doe",
        "email": "john@example.com"
    }
    
    Response: 201 Created
    {
        "id": "uuid",
        "username": "john_doe",
        "email": "john@example.com",
        "created_at": "2023-01-01T00:00:00Z",
        "updated_at": "2023-01-01T00:00:00Z",
        "is_active": true
    }
    """
    try:
        # リクエストデータのバリデーション
        data = user_schema.load(request.json)
    except ValidationError as err:
        return jsonify({
            'error': 'Validation failed',
            'details': err.messages
        }), 400
    
    # 重複チェック
    existing_user = User.query.filter(
        (User.username == data['username']) | 
        (User.email == data['email'])
    ).first()
    
    if existing_user:
        return jsonify({
            'error': 'User already exists',
            'details': 'Username or email already in use'
        }), 409
    
    # ユーザー作成
    user = User(**data)
    db.session.add(user)
    db.session.commit()
    
    return jsonify(user_schema.dump(user)), 201

# READ - GET /users (一覧)
@app.route('/users', methods=['GET'])
def get_users():
    """
    ユーザー一覧取得（ページネーション付き）
    
    Query Parameters:
    - page: ページ番号（デフォルト: 1）
    - per_page: 1ページあたりの件数（デフォルト: 20, 最大: 100）
    - active_only: アクティブユーザーのみ（デフォルト: false）
    - search: ユーザー名・メール検索
    
    Response: 200 OK
    {
        "users": [...],
        "pagination": {
            "page": 1,
            "per_page": 20,
            "total": 100,
            "pages": 5
        }
    }
    """
    page = request.args.get('page', 1, type=int)
    per_page = min(request.args.get('per_page', 20, type=int), 100)
    active_only = request.args.get('active_only', 'false').lower() == 'true'
    search = request.args.get('search', '')
    
    query = User.query
    
    # フィルタリング
    if active_only:
        query = query.filter(User.is_active == True)
    
    if search:
        search_filter = f"%{search}%"
        query = query.filter(
            (User.username.ilike(search_filter)) |
            (User.email.ilike(search_filter))
        )
    
    # ページネーション
    pagination = query.paginate(
        page=page, 
        per_page=per_page, 
        error_out=False
    )
    
    return jsonify({
        'users': users_schema.dump(pagination.items),
        'pagination': {
            'page': pagination.page,
            'per_page': pagination.per_page,
            'total': pagination.total,
            'pages': pagination.pages,
            'has_next': pagination.has_next,
            'has_prev': pagination.has_prev
        }
    }), 200

# READ - GET /users/{id} (詳細)
@app.route('/users/<string:user_id>', methods=['GET'])
def get_user(user_id):
    """
    特定ユーザー詳細取得
    
    Response: 200 OK or 404 Not Found
    """
    user = User.query.get_or_404(user_id)
    return jsonify(user_schema.dump(user)), 200

# UPDATE - PUT /users/{id} (全体更新)
@app.route('/users/<string:user_id>', methods=['PUT'])
def update_user_full(user_id):
    """
    ユーザー情報全体更新（すべてのフィールド必須）
    
    Request Body:
    {
        "username": "new_username",
        "email": "new_email@example.com"
    }
    """
    user = User.query.get_or_404(user_id)
    
    try:
        data = user_schema.load(request.json)
    except ValidationError as err:
        return jsonify({
            'error': 'Validation failed',
            'details': err.messages
        }), 400
    
    # 重複チェック（自分以外）
    existing_user = User.query.filter(
        ((User.username == data['username']) | 
         (User.email == data['email'])) &
        (User.id != user_id)
    ).first()
    
    if existing_user:
        return jsonify({
            'error': 'Conflict',
            'details': 'Username or email already in use'
        }), 409
    
    # 全フィールド更新
    user.username = data['username']
    user.email = data['email']
    user.updated_at = datetime.utcnow()
    
    db.session.commit()
    
    return jsonify(user_schema.dump(user)), 200

# UPDATE - PATCH /users/{id} (部分更新)
@app.route('/users/<string:user_id>', methods=['PATCH'])
def update_user_partial(user_id):
    """
    ユーザー情報部分更新（指定されたフィールドのみ）
    
    Request Body (例):
    {
        "username": "new_username"  // emailは更新されない
    }
    """
    user = User.query.get_or_404(user_id)
    
    try:
        data = user_update_schema.load(request.json)
    except ValidationError as err:
        return jsonify({
            'error': 'Validation failed',
            'details': err.messages
        }), 400
    
    # 更新対象がない場合
    if not data:
        return jsonify({
            'error': 'Bad Request',
            'details': 'No valid fields to update'
        }), 400
    
    # 重複チェック（該当フィールドのみ）
    conflict_conditions = []
    if 'username' in data:
        conflict_conditions.append(User.username == data['username'])
    if 'email' in data:
        conflict_conditions.append(User.email == data['email'])
    
    if conflict_conditions:
        from sqlalchemy import or_
        existing_user = User.query.filter(
            or_(*conflict_conditions) & (User.id != user_id)
        ).first()
        
        if existing_user:
            return jsonify({
                'error': 'Conflict',
                'details': 'Username or email already in use'
            }), 409
    
    # 指定されたフィールドのみ更新
    for field, value in data.items():
        setattr(user, field, value)
    
    user.updated_at = datetime.utcnow()
    db.session.commit()
    
    return jsonify(user_schema.dump(user)), 200

# DELETE - DELETE /users/{id}
@app.route('/users/<string:user_id>', methods=['DELETE'])
def delete_user(user_id):
    """
    ユーザー削除
    
    Response: 204 No Content
    """
    user = User.query.get_or_404(user_id)
    
    # ソフト削除（論理削除）
    user.is_active = False
    user.updated_at = datetime.utcnow()
    db.session.commit()
    
    # または物理削除
    # db.session.delete(user)
    # db.session.commit()
    
    return '', 204

# HEAD - HEAD /users/{id} (存在確認)
@app.route('/users/<string:user_id>', methods=['HEAD'])
def check_user_exists(user_id):
    """
    ユーザー存在確認（ボディなし）
    
    Response: 200 OK or 404 Not Found (ボディなし)
    """
    user = User.query.get_or_404(user_id)
    return '', 200

# OPTIONS - OPTIONS /users (メタデータ取得)
@app.route('/users', methods=['OPTIONS'])
def users_options():
    """
    ユーザーリソースのメタデータ情報
    
    Response: 200 OK
    Headers:
    - Allow: GET, POST, HEAD, OPTIONS
    - Accept: application/json
    """
    response = jsonify({
        'methods': ['GET', 'POST', 'HEAD', 'OPTIONS'],
        'description': 'User resource endpoints',
        'schema': {
            'create': user_schema.dump({}),
            'update': user_update_schema.dump({})
        }
    })
    
    response.headers['Allow'] = 'GET, POST, HEAD, OPTIONS'
    response.headers['Accept'] = 'application/json'
    
    return response

@app.route('/users/<string:user_id>', methods=['OPTIONS'])
def user_detail_options(user_id):
    """
    特定ユーザーリソースのメタデータ
    """
    response = jsonify({
        'methods': ['GET', 'PUT', 'PATCH', 'DELETE', 'HEAD', 'OPTIONS'],
        'description': 'Individual user resource endpoints'
    })
    
    response.headers['Allow'] = 'GET, PUT, PATCH, DELETE, HEAD, OPTIONS'
    return response
```

**Bad: 不適切なHTTPメソッド使用**
```python
# 悪い例：HTTPメソッドの誤用

# 悪い: GETで状態変更
@app.route('/users/<user_id>/delete', methods=['GET'])
def delete_user_bad(user_id):  # 悪い: GETで削除処理
    user = User.query.get_or_404(user_id)
    db.session.delete(user)
    db.session.commit()
    return jsonify({'message': 'User deleted'})

# 悪い: POSTで取得
@app.route('/get-user', methods=['POST'])
def get_user_bad():  # 悪い: POSTで取得処理
    user_id = request.json.get('user_id')
    user = User.query.get_or_404(user_id)
    return jsonify(user_schema.dump(user))

# 悪い: PUTで作成（IDがない場合）
@app.route('/users', methods=['PUT'])
def create_user_bad():  # 悪い: PUTで新規作成（IDなし）
    data = request.json
    user = User(**data)
    db.session.add(user)
    db.session.commit()
    return jsonify(user_schema.dump(user))

# 悪い: DELETEでレスポンスボディ返却
@app.route('/users/<user_id>', methods=['DELETE'])
def delete_user_with_body(user_id):  # 悪い: DELETEでボディ返却
    user = User.query.get_or_404(user_id)
    user_data = user_schema.dump(user)  # 削除前にデータ取得
    db.session.delete(user)
    db.session.commit()
    return jsonify(user_data), 200  # 悪い: ボディ付きで200返却
```

### 2.3 ステータスコード標準使用

#### 2.3.1 適切なステータスコード選択

**Good: 適切なステータスコード使用**
```python
# Django REST Framework - 詳細なステータスコード実装
from rest_framework import status, viewsets
from rest_framework.response import Response
from rest_framework.decorators import action
from django.shortcuts import get_object_or_404
from django.db import IntegrityError, transaction
import logging

logger = logging.getLogger(__name__)

class ProductViewSet(viewsets.ModelViewSet):
    """製品管理API - 詳細なステータスコード実装例"""
    
    def create(self, request):
        """
        製品作成 - 詳細なステータスコード使い分け
        """
        serializer = self.get_serializer(data=request.data)
        
        # バリデーションエラー: 400 Bad Request
        if not serializer.is_valid():
            return Response({
                'error': 'Validation failed',
                'details': serializer.errors,
                'error_code': 'VALIDATION_ERROR'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        try:
            with transaction.atomic():
                product = serializer.save()
                
                # 関連データの作成
                self._create_product_metadata(product, request.data)
                
                logger.info(f"Product created: {product.id}")
                
                # 成功: 201 Created
                return Response({
                    'id': product.id,
                    'message': 'Product created successfully',
                    'data': serializer.data
                }, status=status.HTTP_201_CREATED)
                
        except IntegrityError as e:
            # 制約違反: 409 Conflict
            logger.warning(f"Product creation conflict: {str(e)}")
            return Response({
                'error': 'Product already exists',
                'details': 'Product with this name or SKU already exists',
                'error_code': 'DUPLICATE_PRODUCT'
            }, status=status.HTTP_409_CONFLICT)
            
        except Exception as e:
            # サーバーエラー: 500 Internal Server Error
            logger.error(f"Product creation failed: {str(e)}")
            return Response({
                'error': 'Internal server error',
                'details': 'Failed to create product',
                'error_code': 'INTERNAL_ERROR'
            }, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
    
    def retrieve(self, request, pk=None):
        """
        製品詳細取得 - 条件付きレスポンス
        """
        try:
            product = self.get_object()
            
            # 条件付きリクエスト処理
            if_none_match = request.META.get('HTTP_IF_NONE_MATCH')
            current_etag = f'"{product.updated_at.timestamp()}"'
            
            # ETag一致: 304 Not Modified
            if if_none_match == current_etag:
                return Response(status=status.HTTP_304_NOT_MODIFIED)
            
            # 製品が非公開の場合の処理
            if not product.is_public and not request.user.is_staff:
                # 認証済みだが権限なし: 403 Forbidden
                if request.user.is_authenticated:
                    return Response({
                        'error': 'Access denied',
                        'details': 'This product is not publicly available',
                        'error_code': 'ACCESS_DENIED'
                    }, status=status.HTTP_403_FORBIDDEN)
                
                # 未認証: 401 Unauthorized
                else:
                    return Response({
                        'error': 'Authentication required',
                        'details': 'Authentication required to access this resource',
                        'error_code': 'AUTHENTICATION_REQUIRED'
                    }, status=status.HTTP_401_UNAUTHORIZED)
            
            serializer = self.get_serializer(product)
            response = Response(serializer.data, status=status.HTTP_200_OK)
            
            # キャッシュヘッダー設定
            response['ETag'] = current_etag
            response['Cache-Control'] = 'max-age=3600'
            
            return response
            
        except Http404:
            # リソース未発見: 404 Not Found
            return Response({
                'error': 'Product not found',
                'details': f'Product with ID {pk} does not exist',
                'error_code': 'RESOURCE_NOT_FOUND'
            }, status=status.HTTP_404_NOT_FOUND)
    
    def update(self, request, pk=None):
        """
        製品更新 - 楽観的ロック実装
        """
        product = self.get_object()
        
        # 楽観的ロックチェック
        if_match = request.META.get('HTTP_IF_MATCH')
        current_etag = f'"{product.updated_at.timestamp()}"'
        
        # ETag不一致: 412 Precondition Failed
        if if_match and if_match != current_etag:
            return Response({
                'error': 'Precondition failed',
                'details': 'Resource has been modified by another user',
                'error_code': 'PRECONDITION_FAILED',
                'current_etag': current_etag
            }, status=status.HTTP_412_PRECONDITION_FAILED)
        
        serializer = self.get_serializer(product, data=request.data)
        
        if not serializer.is_valid():
            return Response({
                'error': 'Validation failed',
                'details': serializer.errors,
                'error_code': 'VALIDATION_ERROR'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        try:
            updated_product = serializer.save()
            
            # 成功: 200 OK
            response = Response({
                'message': 'Product updated successfully',
                'data': serializer.data
            }, status=status.HTTP_200_OK)
            
            # 新しいETag設定
            response['ETag'] = f'"{updated_product.updated_at.timestamp()}"'
            
            return response
            
        except IntegrityError:
            return Response({
                'error': 'Update conflict',
                'details': 'Another product with the same name or SKU exists',
                'error_code': 'DUPLICATE_PRODUCT'
            }, status=status.HTTP_409_CONFLICT)
    
    def partial_update(self, request, pk=None):
        """
        部分更新 - PATCH実装
        """
        product = self.get_object()
        serializer = self.get_serializer(product, data=request.data, partial=True)
        
        if not serializer.is_valid():
            return Response({
                'error': 'Validation failed',
                'details': serializer.errors,
                'error_code': 'VALIDATION_ERROR'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        # 更新内容が空の場合
        if not request.data:
            return Response({
                'error': 'No update data provided',
                'details': 'Request body cannot be empty for partial update',
                'error_code': 'EMPTY_REQUEST_BODY'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        updated_product = serializer.save()
        
        # 部分更新成功: 200 OK
        return Response({
            'message': 'Product partially updated successfully',
            'updated_fields': list(request.data.keys()),
            'data': serializer.data
        }, status=status.HTTP_200_OK)
    
    def destroy(self, request, pk=None):
        """
        製品削除 - ソフト削除実装
        """
        product = self.get_object()
        
        # 削除可能性チェック
        if hasattr(product, 'orders') and product.orders.exists():
            # 依存関係あり: 409 Conflict
            return Response({
                'error': 'Cannot delete product',
                'details': 'Product has associated orders and cannot be deleted',
                'error_code': 'DEPENDENCY_CONFLICT'
            }, status=status.HTTP_409_CONFLICT)
        
        # ソフト削除実行
        product.is_deleted = True
        product.deleted_at = timezone.now()
        product.save()
        
        logger.info(f"Product soft deleted: {product.id}")
        
        # 削除成功: 204 No Content
        return Response(status=status.HTTP_204_NO_CONTENT)
    
    @action(detail=True, methods=['post'])
    def restore(self, request, pk=None):
        """
        削除済み製品の復元
        """
        try:
            # 削除済み製品を含めて検索
            product = Product.objects_with_deleted.get(pk=pk)
            
            if not product.is_deleted:
                # すでに有効: 409 Conflict
                return Response({
                    'error': 'Product is not deleted',
                    'details': 'Product is already active and cannot be restored',
                    'error_code': 'ALREADY_ACTIVE'
                }, status=status.HTTP_409_CONFLICT)
            
            # 復元実行
            product.is_deleted = False
            product.deleted_at = None
            product.save()
            
            # 復元成功: 200 OK
            return Response({
                'message': 'Product restored successfully',
                'data': ProductSerializer(product).data
            }, status=status.HTTP_200_OK)
            
        except Product.DoesNotExist:
            return Response({
                'error': 'Product not found',
                'details': f'Product with ID {pk} does not exist',
                'error_code': 'RESOURCE_NOT_FOUND'
            }, status=status.HTTP_404_NOT_FOUND)
    
    @action(detail=False, methods=['post'])
    def bulk_create(self, request):
        """
        一括作成 - 207 Multi-Status実装
        """
        if not isinstance(request.data, list):
            return Response({
                'error': 'Invalid request format',
                'details': 'Request body must be an array of product objects',
                'error_code': 'INVALID_FORMAT'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        results = []
        created_count = 0
        error_count = 0
        
        for index, product_data in enumerate(request.data):
            try:
                serializer = ProductSerializer(data=product_data)
                
                if serializer.is_valid():
                    product = serializer.save()
                    results.append({
                        'index': index,
                        'status': 201,
                        'message': 'Created successfully',
                        'data': serializer.data
                    })
                    created_count += 1
                
                else:
                    results.append({
                        'index': index,
                        'status': 400,
                        'message': 'Validation failed',
                        'errors': serializer.errors
                    })
                    error_count += 1
                    
            except IntegrityError:
                results.append({
                    'index': index,
                    'status': 409,
                    'message': 'Product already exists',
                    'errors': {'duplicate': 'Product with this name or SKU already exists'}
                })
                error_count += 1
                
            except Exception as e:
                results.append({
                    'index': index,
                    'status': 500,
                    'message': 'Internal server error',
                    'errors': {'internal': str(e)}
                })
                error_count += 1
        
        # 全て成功: 201 Created
        if error_count == 0:
            return Response({
                'message': f'All {created_count} products created successfully',
                'results': results
            }, status=status.HTTP_201_CREATED)
        
        # 全て失敗: 400 Bad Request
        elif created_count == 0:
            return Response({
                'message': 'All products failed to create',
                'results': results
            }, status=status.HTTP_400_BAD_REQUEST)
        
        # 一部成功: 207 Multi-Status
        else:
            return Response({
                'message': f'{created_count} products created, {error_count} failed',
                'summary': {
                    'created': created_count,
                    'failed': error_count,
                    'total': len(request.data)
                },
                'results': results
            }, status=status.HTTP_207_MULTI_STATUS)

# カスタムエラーハンドラー
@api_view(['GET', 'POST', 'PUT', 'PATCH', 'DELETE'])
def rate_limited_view(request):
    """
    レート制限実装例
    """
    from django_ratelimit.decorators import ratelimit
    from django_ratelimit.exceptions import Ratelimited
    
    try:
        # レート制限チェック
        if getattr(request, 'limited', False):
            # レート制限超過: 429 Too Many Requests
            return Response({
                'error': 'Rate limit exceeded',
                'details': 'Too many requests. Please try again later.',
                'error_code': 'RATE_LIMIT_EXCEEDED',
                'retry_after': 60  # 60秒後に再試行
            }, status=status.HTTP_429_TOO_MANY_REQUESTS)
        
        # 通常処理
        return Response({'message': 'Success'}, status=status.HTTP_200_OK)
        
    except Ratelimited:
        return Response({
            'error': 'Rate limit exceeded',
            'details': 'Too many requests. Please try again later.',
            'error_code': 'RATE_LIMIT_EXCEEDED'
        }, status=status.HTTP_429_TOO_MANY_REQUESTS)
```

**Bad: 不適切なステータスコード使用**
```python
# 悪い例：ステータスコードの誤用

@api_view(['POST'])
def create_user_bad(request):
    serializer = UserSerializer(data=request.data)
    
    if not serializer.is_valid():
        # 悪い: バリデーションエラーで500を返す
        return Response({
            'error': 'Something went wrong'
        }, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
    
    try:
        user = serializer.save()
        # 悪い: 作成成功で200を返す（201が適切）
        return Response(serializer.data, status=status.HTTP_200_OK)
    
    except IntegrityError:
        # 悪い: 重複エラーで400を返す（409が適切）
        return Response({
            'error': 'Bad request'
        }, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET'])
def get_user_bad(request, user_id):
    try:
        user = User.objects.get(id=user_id)
        # 悪い: 正常取得で201を返す（200が適切）
        return Response(UserSerializer(user).data, status=status.HTTP_201_CREATED)
    
    except User.DoesNotExist:
        # 悪い: 未発見で500を返す（404が適切）
        return Response({
            'error': 'Server error'
        }, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

@api_view(['DELETE'])
def delete_user_bad(request, user_id):
    try:
        user = User.objects.get(id=user_id)
        user.delete()
        
        # 悪い: 削除成功でデータを返す（204 No Contentが適切）
        return Response({
            'message': 'User deleted',
            'deleted_user': UserSerializer(user).data
        }, status=status.HTTP_200_OK)
    
    except User.DoesNotExist:
        # 悪い: 削除対象なしで200を返す（404が適切）
        return Response({
            'message': 'User not found but OK'
        }, status=status.HTTP_200_OK)
```

### 2.4 クエリパラメータ設計

#### 2.4.1 ページネーションパターン

**Good: 標準的なページネーション設計**
```python
# FastAPI - 高度なページネーションシステム
from fastapi import FastAPI, Query, HTTPException, Depends
from sqlalchemy.orm import Session
from sqlalchemy import func, asc, desc
from typing import Optional, List, Dict, Any
from enum import Enum
import math

app = FastAPI(title="Advanced Pagination API")

# ソート順序の列挙型
class SortOrder(str, Enum):
    ASC = "asc"
    DESC = "desc"

# フィルタリングオプション
class UserFilter(str, Enum):
    ALL = "all"
    ACTIVE = "active"
    INACTIVE = "inactive"
    VERIFIED = "verified"
    UNVERIFIED = "unverified"

# ページネーションレスポンスモデル
class PaginationMeta(BaseModel):
    page: int
    per_page: int
    total: int
    pages: int
    has_next: bool
    has_prev: bool
    next_page: Optional[int] = None
    prev_page: Optional[int] = None

class PaginatedUsersResponse(BaseModel):
    data: List[UserResponse]
    meta: PaginationMeta
    links: Dict[str, Optional[str]]

# オフセットベースページネーション
@app.get("/users", response_model=PaginatedUsersResponse)
async def get_users_paginated(
    # ページネーションパラメータ
    page: int = Query(1, ge=1, description="ページ番号（1から開始）"),
    per_page: int = Query(20, ge=1, le=100, description="1ページあたりの件数"),
    
    # ソートパラメータ
    sort_by: str = Query("created_at", description="ソートキー"),
    sort_order: SortOrder = Query(SortOrder.DESC, description="ソート順序"),
    
    # フィルタリングパラメータ
    status: UserFilter = Query(UserFilter.ALL, description="ユーザーステータスフィルタ"),
    search: Optional[str] = Query(None, min_length=2, max_length=100, description="検索キーワード"),
    created_after: Optional[datetime] = Query(None, description="作成日時以降フィルタ"),
    created_before: Optional[datetime] = Query(None, description="作成日時以前フィルタ"),
    
    # データベースセッション
    db: Session = Depends(get_db),
    request: Request = None
):
    """
    高度なページネーション付きユーザー一覧取得
    
    機能:
    - オフセットベースページネーション
    - 柔軟なソート機能
    - 組み合わせ可能なフィルタリング
    - 全文検索対応
    - HATEOASリンク生成
    """
    # クエリビルダー初期化
    query = db.query(User)
    
    # フィルタリング適用
    if status == UserFilter.ACTIVE:
        query = query.filter(User.is_active == True)
    elif status == UserFilter.INACTIVE:
        query = query.filter(User.is_active == False)
    elif status == UserFilter.VERIFIED:
        query = query.filter(User.email_verified_at.isnot(None))
    elif status == UserFilter.UNVERIFIED:
        query = query.filter(User.email_verified_at.is_(None))
    
    # 日付範囲フィルタ
    if created_after:
        query = query.filter(User.created_at >= created_after)
    if created_before:
        query = query.filter(User.created_at <= created_before)
    
    # 全文検索
    if search:
        search_filter = f"%{search}%"
        query = query.filter(
            or_(
                User.username.ilike(search_filter),
                User.email.ilike(search_filter),
                User.first_name.ilike(search_filter),
                User.last_name.ilike(search_filter)
            )
        )
    
    # ソート適用
    sort_column = getattr(User, sort_by, None)
    if sort_column is None:
        raise HTTPException(
            status_code=400,
            detail=f"Invalid sort field: {sort_by}"
        )
    
    if sort_order == SortOrder.ASC:
        query = query.order_by(asc(sort_column))
    else:
        query = query.order_by(desc(sort_column))
    
    # 総件数取得
    total = query.count()
    
    # ページ数計算
    pages = math.ceil(total / per_page) if total > 0 else 1
    
    # ページ番号バリデーション
    if page > pages and total > 0:
        raise HTTPException(
            status_code=404,
            detail=f"Page {page} not found. Total pages: {pages}"
        )
    
    # オフセット計算とデータ取得
    offset = (page - 1) * per_page
    users = query.offset(offset).limit(per_page).all()
    
    # メタデータ作成
    has_next = page < pages
    has_prev = page > 1
    
    meta = PaginationMeta(
        page=page,
        per_page=per_page,
        total=total,
        pages=pages,
        has_next=has_next,
        has_prev=has_prev,
        next_page=page + 1 if has_next else None,
        prev_page=page - 1 if has_prev else None
    )
    
    # HATEOASリンク生成
    base_url = str(request.url).split('?')[0]
    query_params = {k: v for k, v in request.query_params.items() if k != 'page'}
    
    def build_link(page_num):
        if page_num is None:
            return None
        params = {**query_params, 'page': page_num}
        param_str = '&'.join([f"{k}={v}" for k, v in params.items()])
        return f"{base_url}?{param_str}"
    
    links = {
        'self': build_link(page),
        'first': build_link(1) if pages > 0 else None,
        'last': build_link(pages) if pages > 0 else None,
        'next': build_link(meta.next_page),
        'prev': build_link(meta.prev_page)
    }
    
    return PaginatedUsersResponse(
        data=[UserResponse.from_orm(user) for user in users],
        meta=meta,
        links=links
    )

# カーソルベースページネーション（大量データ用）
@app.get("/users/cursor", response_model=Dict[str, Any])
async def get_users_cursor_paginated(
    cursor: Optional[str] = Query(None, description="カーソルトークン"),
    limit: int = Query(20, ge=1, le=100, description="取得件数上限"),
    direction: str = Query("next", regex="^(next|prev)$", description="ページ方向"),
    db: Session = Depends(get_db)
):
    """
    カーソルベースページネーション
    
    大量データに対して高パフォーマンスを提供
    カーソルにはcreated_at + idの組み合わせを使用
    """
    query = db.query(User).order_by(desc(User.created_at), desc(User.id))
    
    # カーソルデコード
    cursor_timestamp = None
    cursor_id = None
    
    if cursor:
        try:
            import base64
            import json
            
            decoded = base64.b64decode(cursor).decode('utf-8')
            cursor_data = json.loads(decoded)
            cursor_timestamp = datetime.fromisoformat(cursor_data['timestamp'])
            cursor_id = cursor_data['id']
        except (ValueError, KeyError, json.JSONDecodeError):
            raise HTTPException(
                status_code=400,
                detail="Invalid cursor format"
            )
    
    # カーソルフィルタ適用
    if cursor_timestamp and cursor_id:
        if direction == "next":
            query = query.filter(
                or_(
                    User.created_at < cursor_timestamp,
                    and_(
                        User.created_at == cursor_timestamp,
                        User.id < cursor_id
                    )
                )
            )
        else:  # prev
            query = query.filter(
                or_(
                    User.created_at > cursor_timestamp,
                    and_(
                        User.created_at == cursor_timestamp,
                        User.id > cursor_id
                    )
                )
            ).order_by(asc(User.created_at), asc(User.id))
    
    # データ取得（+1件で次ページ存在チェック）
    users = query.limit(limit + 1).all()
    
    # prev方向の場合は順序を元に戻す
    if direction == "prev":
        users = list(reversed(users))
    
    # 次ページ存在チェック
    has_more = len(users) > limit
    if has_more:
        users = users[:limit]
    
    # 次のカーソル生成
    next_cursor = None
    prev_cursor = None
    
    if users:
        if has_more and direction == "next":
            last_user = users[-1]
            next_cursor_data = {
                'timestamp': last_user.created_at.isoformat(),
                'id': last_user.id
            }
            next_cursor = base64.b64encode(
                json.dumps(next_cursor_data).encode('utf-8')
            ).decode('utf-8')
        
        if cursor:  # 前ページへのカーソル
            first_user = users[0]
            prev_cursor_data = {
                'timestamp': first_user.created_at.isoformat(),
                'id': first_user.id
            }
            prev_cursor = base64.b64encode(
                json.dumps(prev_cursor_data).encode('utf-8')
            ).decode('utf-8')
    
    return {
        'data': [UserResponse.from_orm(user) for user in users],
        'pagination': {
            'has_more': has_more,
            'next_cursor': next_cursor,
            'prev_cursor': prev_cursor,
            'limit': limit
        },
        'links': {
            'self': f"/users/cursor?cursor={cursor}&limit={limit}&direction={direction}" if cursor else None,
            'next': f"/users/cursor?cursor={next_cursor}&limit={limit}&direction=next" if next_cursor else None,
            'prev': f"/users/cursor?cursor={prev_cursor}&limit={limit}&direction=prev" if prev_cursor else None
        }
    }
```

**Bad: 不適切なページネーション設計**
```python
# 悪い例：不完全なページネーション

@app.get("/users")
async def get_users_bad(
    page: int = 1,  # 悪い: バリデーションなし
    size: int = 10,  # 悪い: 上限なし
    db: Session = Depends(get_db)
):
    # 悪い: 総件数を取得しない
    offset = (page - 1) * size
    users = db.query(User).offset(offset).limit(size).all()
    
    # 悪い: メタデータなし
    return {'users': users}

@app.get("/users/all")
async def get_all_users_bad(db: Session = Depends(get_db)):
    # 悪い: 全データを一度に取得（メモリリークリスク）
    users = db.query(User).all()  # 危険！
    return {'users': users, 'count': len(users)}

@app.get("/users/search")
async def search_users_bad(
    q: str,  # 悪い: バリデーションなし
    db: Session = Depends(get_db)
):
    # 悪い: SQLインジェクション脅弱性
    query = f"SELECT * FROM users WHERE username LIKE '%{q}%'"
    result = db.execute(query).fetchall()
    return {'users': result}
```

#### 2.4.2 フィルタリングと検索機能

**Good: 高度なフィルタリングシステム**
```python
# Django REST Framework - 高度なフィルタリングシステム
from django_filters import rest_framework as filters
from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response
from django.db.models import Q, Count, Avg, Max, Min
from django.contrib.postgres.search import SearchVector, SearchQuery, SearchRank
import operator
from functools import reduce

class ProductFilter(filters.FilterSet):
    """
    製品フィルタセット - 高度なフィルタリング機能
    """
    
    # 範囲フィルタ
    price_min = filters.NumberFilter(field_name='price', lookup_expr='gte')
    price_max = filters.NumberFilter(field_name='price', lookup_expr='lte')
    price_range = filters.RangeFilter(field_name='price')
    
    # 日付フィルタ
    created_after = filters.DateTimeFilter(field_name='created_at', lookup_expr='gte')
    created_before = filters.DateTimeFilter(field_name='created_at', lookup_expr='lte')
    created_date_range = filters.DateFromToRangeFilter(field_name='created_at')
    
    # 選択フィルタ
    category = filters.ModelMultipleChoiceFilter(
        field_name='category',
        queryset=Category.objects.all(),
        conjoined=False  # OR条件
    )
    
    status = filters.MultipleChoiceFilter(
        choices=Product.STATUS_CHOICES,
        field_name='status',
        conjoined=False
    )
    
    # ブールフィルタ
    is_featured = filters.BooleanFilter(field_name='is_featured')
    in_stock = filters.BooleanFilter(method='filter_in_stock')
    
    # カスタムフィルタ
    has_discount = filters.BooleanFilter(method='filter_has_discount')
    rating_min = filters.NumberFilter(method='filter_rating_min')
    
    # 全文検索（PostgreSQL専用）
    search = filters.CharFilter(method='filter_search')
    
    # 複合フィルタ
    availability = filters.ChoiceFilter(
        choices=[
            ('available', 'Available'),
            ('low_stock', 'Low Stock'),
            ('out_of_stock', 'Out of Stock'),
        ],
        method='filter_availability'
    )
    
    class Meta:
        model = Product
        fields = {
            'name': ['exact', 'icontains', 'startswith'],
            'sku': ['exact', 'icontains'],
            'brand': ['exact'],
            'tags': ['exact', 'in'],
        }
    
    def filter_in_stock(self, queryset, name, value):
        """在庫ありフィルタ"""
        if value:
            return queryset.filter(stock_quantity__gt=0)
        return queryset.filter(stock_quantity=0)
    
    def filter_has_discount(self, queryset, name, value):
        """割引有無フィルタ"""
        if value:
            return queryset.filter(
                discount_percentage__gt=0,
                discount_start_date__lte=timezone.now(),
                discount_end_date__gte=timezone.now()
            )
        return queryset.filter(
            Q(discount_percentage=0) |
            Q(discount_start_date__gt=timezone.now()) |
            Q(discount_end_date__lt=timezone.now())
        )
    
    def filter_rating_min(self, queryset, name, value):
        """最低評価フィルタ"""
        return queryset.annotate(
            avg_rating=Avg('reviews__rating')
        ).filter(avg_rating__gte=value)
    
    def filter_search(self, queryset, name, value):
        """
        PostgreSQL全文検索実装
        プロダクト名、説明、タグを検索対象とする
        """
        if not value:
            return queryset
        
        # 検索ベクター作成
        search_vector = SearchVector(
            'name', weight='A',  # 名前は最高権重
            'description', weight='B',  # 説明は中権重
            'tags__name', weight='C'  # タグは低権重
        )
        
        search_query = SearchQuery(value)
        
        return queryset.annotate(
            search=search_vector,
            rank=SearchRank(search_vector, search_query)
        ).filter(
            search=search_query
        ).order_by('-rank', '-created_at')
    
    def filter_availability(self, queryset, name, value):
        """在庫状態フィルタ"""
        if value == 'available':
            return queryset.filter(stock_quantity__gte=10)
        elif value == 'low_stock':
            return queryset.filter(stock_quantity__gt=0, stock_quantity__lt=10)
        elif value == 'out_of_stock':
            return queryset.filter(stock_quantity=0)
        return queryset

class ProductViewSet(viewsets.ModelViewSet):
    """製品API - 高度なフィルタリング実装"""
    
    queryset = Product.objects.select_related('category', 'brand').prefetch_related('tags')
    serializer_class = ProductSerializer
    filterset_class = ProductFilter
    
    # ソートオプション
    ordering_fields = [
        'name', 'price', 'created_at', 'updated_at', 'stock_quantity'
    ]
    ordering = ['-created_at']
    
    def get_queryset(self):
        """ベースクエリセットのカスタマイズ"""
        queryset = super().get_queryset()
        
        # パフォーマンス最適化
        if self.action == 'list':
            queryset = queryset.select_related('category', 'brand')
            queryset = queryset.prefetch_related('tags')
        
        # 非公開製品のシステム管理者以外非表示
        if not self.request.user.is_staff:
            queryset = queryset.filter(is_public=True)
        
        return queryset
    
    @action(detail=False, methods=['get'])
    def search_suggestions(self, request):
        """
        検索候補提案API
        オートコンプリート機能用
        """
        query = request.query_params.get('q', '').strip()
        
        if len(query) < 2:
            return Response({
                'suggestions': [],
                'message': 'Query too short. Minimum 2 characters required.'
            })
        
        # 製品名候補
        product_suggestions = Product.objects.filter(
            name__icontains=query,
            is_public=True
        ).values_list('name', flat=True).distinct()[:5]
        
        # カテゴリ候補
        category_suggestions = Category.objects.filter(
            name__icontains=query
        ).values_list('name', flat=True).distinct()[:3]
        
        # タグ候補
        tag_suggestions = Tag.objects.filter(
            name__icontains=query
        ).values_list('name', flat=True).distinct()[:3]
        
        return Response({
            'suggestions': {
                'products': list(product_suggestions),
                'categories': list(category_suggestions),
                'tags': list(tag_suggestions)
            },
            'query': query
        })
    
    @action(detail=False, methods=['get'])
    def faceted_search(self, request):
        """
        ファセット検索（絞込み検索）
        カテゴリ、価格帯、ブランド等のファセット情報を提供
        """
        # フィルタ適用後のクエリセット
        filtered_queryset = self.filter_queryset(self.get_queryset())
        
        # ファセット情報集計
        facets = {
            # カテゴリ別件数
            'categories': list(
                filtered_queryset.values('category__name', 'category__id')
                .annotate(count=Count('id'))
                .order_by('-count')
            ),
            
            # ブランド別件数
            'brands': list(
                filtered_queryset.values('brand__name', 'brand__id')
                .annotate(count=Count('id'))
                .order_by('-count')
            ),
            
            # 価格帯分布
            'price_ranges': {
                '0-1000': filtered_queryset.filter(price__lt=1000).count(),
                '1000-5000': filtered_queryset.filter(price__gte=1000, price__lt=5000).count(),
                '5000-10000': filtered_queryset.filter(price__gte=5000, price__lt=10000).count(),
                '10000+': filtered_queryset.filter(price__gte=10000).count(),
            },
            
            # 在庫状態
            'availability': {
                'in_stock': filtered_queryset.filter(stock_quantity__gt=0).count(),
                'out_of_stock': filtered_queryset.filter(stock_quantity=0).count(),
            },
            
            # 評価分布
            'ratings': {
                '4+': filtered_queryset.annotate(
                    avg_rating=Avg('reviews__rating')
                ).filter(avg_rating__gte=4).count(),
                '3+': filtered_queryset.annotate(
                    avg_rating=Avg('reviews__rating')
                ).filter(avg_rating__gte=3, avg_rating__lt=4).count(),
                '2+': filtered_queryset.annotate(
                    avg_rating=Avg('reviews__rating')
                ).filter(avg_rating__gte=2, avg_rating__lt=3).count(),
                '1+': filtered_queryset.annotate(
                    avg_rating=Avg('reviews__rating')
                ).filter(avg_rating__gte=1, avg_rating__lt=2).count(),
            }
        }
        
        # 基本統計情報
        stats = {
            'total_products': filtered_queryset.count(),
            'price_range': {
                'min': filtered_queryset.aggregate(Min('price'))['price__min'],
                'max': filtered_queryset.aggregate(Max('price'))['price__max'],
                'avg': filtered_queryset.aggregate(Avg('price'))['price__avg'],
            }
        }
        
        return Response({
            'facets': facets,
            'stats': stats,
            'applied_filters': dict(request.query_params)
        })
    
    @action(detail=False, methods=['get'])
    def advanced_search(self, request):
        """
        高度検索API - AND/ORロジック組み合わせ
        
        Query Parameters:
        - q: メイン検索キーワード
        - category_or: カテゴリのOR検索（カンマ区切り）
        - tags_and: タグのAND検索（カンマ区切り）
        - exclude_categories: 除外カテゴリ（カンマ区切り）
        """
        queryset = self.get_queryset()
        
        # メイン検索キーワード
        query = request.query_params.get('q', '').strip()
        if query:
            search_vector = SearchVector('name', 'description')
            search_query = SearchQuery(query)
            queryset = queryset.annotate(
                search=search_vector,
                rank=SearchRank(search_vector, search_query)
            ).filter(search=search_query)
        
        # カテゴリ OR検索
        category_or = request.query_params.get('category_or', '').strip()
        if category_or:
            category_ids = [int(id) for id in category_or.split(',') if id.isdigit()]
            if category_ids:
                queryset = queryset.filter(category_id__in=category_ids)
        
        # タグ AND検索
        tags_and = request.query_params.get('tags_and', '').strip()
        if tags_and:
            tag_names = [name.strip() for name in tags_and.split(',')]
            for tag_name in tag_names:
                queryset = queryset.filter(tags__name__iexact=tag_name)
        
        # 除外カテゴリ
        exclude_categories = request.query_params.get('exclude_categories', '').strip()
        if exclude_categories:
            exclude_ids = [int(id) for id in exclude_categories.split(',') if id.isdigit()]
            if exclude_ids:
                queryset = queryset.exclude(category_id__in=exclude_ids)
        
        # ソート適用
        if query:  # 検索時は関連度順
            queryset = queryset.order_by('-rank', '-created_at')
        else:
            queryset = queryset.order_by('-created_at')
        
        # ページネーション適用
        page = self.paginate_queryset(queryset)
        if page is not None:
            serializer = self.get_serializer(page, many=True)
            return self.get_paginated_response(serializer.data)
        
        serializer = self.get_serializer(queryset, many=True)
        return Response(serializer.data)
```

**Bad: 不適切なフィルタリング実装**
```python
# 悪い例：フィルタリングの悪い実装

@api_view(['GET'])
def search_products_bad(request):
    # 悪い: SQLインジェクション脅弱性
    search_term = request.GET.get('search', '')
    query = f"SELECT * FROM products WHERE name LIKE '%{search_term}%'"
    
    # 悪い: バリデーションなし
    products = Product.objects.extra(where=[query])
    
    return Response({'products': list(products.values())})

@api_view(['GET'])
def filter_products_bad(request):
    # 悪い: 非効率なクエリ
    all_products = Product.objects.all()  # 全データ取得
    
    # 悪い: Pythonレベルでフィルタリング
    min_price = request.GET.get('min_price')
    if min_price:
        all_products = [p for p in all_products if p.price >= float(min_price)]
    
    max_price = request.GET.get('max_price')
    if max_price:
        all_products = [p for p in all_products if p.price <= float(max_price)]
    
    return Response({'products': all_products})

@api_view(['GET'])
def get_products_no_pagination(request):
    # 悪い: ページネーションなしで全データ
    products = Product.objects.all()  # メモリリークリスク
    return Response({'products': ProductSerializer(products, many=True).data})
```

### 2.5 レスポンスフォーマット緑一

#### 2.5.1 JSONレスポンス構造設計

**Good: 一貫したレスポンス構造**
```python
# Flask - 統一レスポンスフォーマット実装
from flask import Flask, jsonify, request
from datetime import datetime
from typing import Dict, Any, Optional, List, Union
import uuid

app = Flask(__name__)

class APIResponse:
    """
    統一APIレスポンスフォーマッター
    すべてのAPIレスポンスで一貫した構造を提供
    """
    
    @staticmethod
    def success(data: Any = None, message: str = "Success", meta: Dict[str, Any] = None) -> Dict[str, Any]:
        """成功レスポンスフォーマット"""
        response = {
            "success": True,
            "message": message,
            "timestamp": datetime.utcnow().isoformat() + "Z",
            "request_id": str(uuid.uuid4())
        }
        
        if data is not None:
            response["data"] = data
        
        if meta:
            response["meta"] = meta
        
        return response
    
    @staticmethod
    def error(
        message: str = "An error occurred",
        error_code: str = "UNKNOWN_ERROR",
        details: Any = None,
        status_code: int = 400
    ) -> Dict[str, Any]:
        """エラーレスポンスフォーマット"""
        response = {
            "success": False,
            "error": {
                "message": message,
                "code": error_code,
                "status_code": status_code
            },
            "timestamp": datetime.utcnow().isoformat() + "Z",
            "request_id": str(uuid.uuid4())
        }
        
        if details:
            response["error"]["details"] = details
        
        return response
    
    @staticmethod
    def paginated(
        data: List[Any],
        page: int,
        per_page: int,
        total: int,
        message: str = "Data retrieved successfully"
    ) -> Dict[str, Any]:
        """ページネーション付きレスポンスフォーマット"""
        import math
        
        pages = math.ceil(total / per_page) if total > 0 else 1
        
        meta = {
            "pagination": {
                "page": page,
                "per_page": per_page,
                "total": total,
                "pages": pages,
                "has_next": page < pages,
                "has_prev": page > 1,
                "next_page": page + 1 if page < pages else None,
                "prev_page": page - 1 if page > 1 else None
            }
        }
        
        return APIResponse.success(data=data, message=message, meta=meta)

# 実際のAPIエンドポイント実装例

@app.route('/users', methods=['GET'])
def get_users():
    """ユーザー一覧取得 - 統一フォーマット"""
    try:
        # ページネーションパラメータ
        page = request.args.get('page', 1, type=int)
        per_page = min(request.args.get('per_page', 20, type=int), 100)
        
        # データ取得ロジック（簡略化）
        users = User.query.paginate(
            page=page, 
            per_page=per_page, 
            error_out=False
        )
        
        # レスポンスデータ整形
        user_data = [
            {
                "id": user.id,
                "username": user.username,
                "email": user.email,
                "created_at": user.created_at.isoformat() + "Z",
                "is_active": user.is_active,
                "links": {
                    "self": f"/users/{user.id}",
                    "posts": f"/users/{user.id}/posts"
                }
            }
            for user in users.items
        ]
        
        return jsonify(APIResponse.paginated(
            data=user_data,
            page=page,
            per_page=per_page,
            total=users.total,
            message=f"Retrieved {len(user_data)} users"
        ))
    
    except Exception as e:
        return jsonify(APIResponse.error(
            message="Failed to retrieve users",
            error_code="USER_RETRIEVAL_ERROR",
            details=str(e)
        )), 500

@app.route('/users/<string:user_id>', methods=['GET'])
def get_user(user_id):
    """ユーザー詳細取得 - 統一フォーマット"""
    try:
        user = User.query.get(user_id)
        
        if not user:
            return jsonify(APIResponse.error(
                message="User not found",
                error_code="USER_NOT_FOUND",
                details=f"User with ID '{user_id}' does not exist"
            )), 404
        
        # 詳細データ構造
        user_data = {
            "id": user.id,
            "username": user.username,
            "email": user.email,
            "profile": {
                "first_name": user.first_name,
                "last_name": user.last_name,
                "bio": user.bio,
                "avatar_url": user.avatar_url
            },
            "stats": {
                "posts_count": user.posts.count(),
                "followers_count": user.followers.count(),
                "following_count": user.following.count()
            },
            "timestamps": {
                "created_at": user.created_at.isoformat() + "Z",
                "updated_at": user.updated_at.isoformat() + "Z",
                "last_login": user.last_login.isoformat() + "Z" if user.last_login else None
            },
            "status": {
                "is_active": user.is_active,
                "is_verified": user.is_verified,
                "account_type": user.account_type
            },
            "links": {
                "self": f"/users/{user.id}",
                "posts": f"/users/{user.id}/posts",
                "followers": f"/users/{user.id}/followers",
                "following": f"/users/{user.id}/following"
            }
        }
        
        return jsonify(APIResponse.success(
            data=user_data,
            message="User retrieved successfully"
        ))
        
    except Exception as e:
        return jsonify(APIResponse.error(
            message="Failed to retrieve user",
            error_code="USER_RETRIEVAL_ERROR",
            details=str(e)
        )), 500

@app.route('/users', methods=['POST'])
def create_user():
    """ユーザー作成 - 統一フォーマット"""
    try:
        # リクエストデータバリデーション
        data = request.get_json()
        
        if not data:
            return jsonify(APIResponse.error(
                message="Request body is required",
                error_code="MISSING_REQUEST_BODY"
            )), 400
        
        required_fields = ['username', 'email', 'password']
        missing_fields = [field for field in required_fields if field not in data]
        
        if missing_fields:
            return jsonify(APIResponse.error(
                message="Missing required fields",
                error_code="VALIDATION_ERROR",
                details={
                    "missing_fields": missing_fields,
                    "required_fields": required_fields
                }
            )), 400
        
        # ユーザー作成処理
        user = User(
            username=data['username'],
            email=data['email'],
            password_hash=generate_password_hash(data['password'])
        )
        
        db.session.add(user)
        db.session.commit()
        
        # 作成成功レスポンス
        created_user = {
            "id": user.id,
            "username": user.username,
            "email": user.email,
            "created_at": user.created_at.isoformat() + "Z",
            "is_active": user.is_active,
            "links": {
                "self": f"/users/{user.id}",
                "posts": f"/users/{user.id}/posts"
            }
        }
        
        return jsonify(APIResponse.success(
            data=created_user,
            message="User created successfully"
        )), 201
    
    except IntegrityError as e:
        db.session.rollback()
        return jsonify(APIResponse.error(
            message="User already exists",
            error_code="DUPLICATE_USER",
            details="Username or email already in use"
        )), 409
    
    except Exception as e:
        db.session.rollback()
        return jsonify(APIResponse.error(
            message="Failed to create user",
            error_code="USER_CREATION_ERROR",
            details=str(e)
        )), 500

@app.route('/users/bulk', methods=['POST'])
def bulk_create_users():
    """一括ユーザー作成 - マルチステータスレスポンス"""
    try:
        data = request.get_json()
        
        if not isinstance(data, list):
            return jsonify(APIResponse.error(
                message="Request body must be an array",
                error_code="INVALID_REQUEST_FORMAT"
            )), 400
        
        results = []
        success_count = 0
        error_count = 0
        
        for index, user_data in enumerate(data):
            try:
                # 個別バリデーション
                required_fields = ['username', 'email', 'password']
                missing_fields = [field for field in required_fields if field not in user_data]
                
                if missing_fields:
                    results.append({
                        "index": index,
                        "success": False,
                        "error": {
                            "message": "Missing required fields",
                            "code": "VALIDATION_ERROR",
                            "details": {"missing_fields": missing_fields}
                        }
                    })
                    error_count += 1
                    continue
                
                # ユーザー作成
                user = User(
                    username=user_data['username'],
                    email=user_data['email'],
                    password_hash=generate_password_hash(user_data['password'])
                )
                
                db.session.add(user)
                db.session.flush()  # ID取得のため
                
                results.append({
                    "index": index,
                    "success": True,
                    "data": {
                        "id": user.id,
                        "username": user.username,
                        "email": user.email
                    }
                })
                success_count += 1
                
            except IntegrityError:
                db.session.rollback()
                results.append({
                    "index": index,
                    "success": False,
                    "error": {
                        "message": "User already exists",
                        "code": "DUPLICATE_USER",
                        "details": "Username or email already in use"
                    }
                })
                error_count += 1
            
            except Exception as e:
                db.session.rollback()
                results.append({
                    "index": index,
                    "success": False,
                    "error": {
                        "message": "Failed to create user",
                        "code": "USER_CREATION_ERROR",
                        "details": str(e)
                    }
                })
                error_count += 1
        
        # 最終コミット
        if success_count > 0:
            db.session.commit()
        
        # マルチステータスレスポンス
        summary = {
            "total": len(data),
            "success": success_count,
            "failed": error_count
        }
        
        # ステータスコード決定
        if error_count == 0:  # 全て成功
            status_code = 201
            message = f"All {success_count} users created successfully"
        elif success_count == 0:  # 全て失敗
            status_code = 400
            message = "All user creation attempts failed"
        else:  # 部分成功
            status_code = 207  # Multi-Status
            message = f"{success_count} users created, {error_count} failed"
        
        response_data = {
            "summary": summary,
            "results": results
        }
        
        return jsonify(APIResponse.success(
            data=response_data,
            message=message
        )), status_code
        
    except Exception as e:
        db.session.rollback()
        return jsonify(APIResponse.error(
            message="Bulk user creation failed",
            error_code="BULK_CREATION_ERROR",
            details=str(e)
        )), 500

# グローバルエラーハンドラー
@app.errorhandler(404)
def not_found_error(error):
    return jsonify(APIResponse.error(
        message="Resource not found",
        error_code="NOT_FOUND",
        details="The requested resource could not be found"
    )), 404

@app.errorhandler(405)
def method_not_allowed_error(error):
    return jsonify(APIResponse.error(
        message="Method not allowed",
        error_code="METHOD_NOT_ALLOWED",
        details=f"The {request.method} method is not allowed for this endpoint"
    )), 405

@app.errorhandler(500)
def internal_error(error):
    return jsonify(APIResponse.error(
        message="Internal server error",
        error_code="INTERNAL_SERVER_ERROR",
        details="An unexpected error occurred"
    )), 500
```

**Bad: 一貫性のないレスポンス構造**
```python
# 悪い例：バラバラなレスポンス構造

@app.route('/users', methods=['GET'])
def get_users_bad():
    users = User.query.all()
    # 悪い: 統一性なし、メタデータなし
    return jsonify([{'id': u.id, 'name': u.username} for u in users])

@app.route('/users/<user_id>', methods=['GET'])
def get_user_bad(user_id):
    user = User.query.get(user_id)
    if user:
        # 悪い: 異なるフォーマット
        return jsonify({
            'status': 'ok',
            'user_data': {
                'user_id': user.id,
                'user_name': user.username  # 悪い: 不一貫なフィールド名
            }
        })
    else:
        # 悪い: エラーフォーマットが異なる
        return jsonify({'error': 'Not found'}), 404

@app.route('/users', methods=['POST'])
def create_user_bad():
    try:
        data = request.json
        user = User(**data)
        db.session.add(user)
        db.session.commit()
        
        # 悪い: また異なるフォーマット
        return jsonify({
            'message': 'Created',
            'id': user.id,
            'timestamp': str(datetime.now())  # 悪い: 不一貫な日付フォーマット
        }), 201
        
    except Exception as e:
        # 悪い: エラー情報不足
        return jsonify({'err': str(e)}), 400
```

---

## 3. API認証・認可

### 3.1 JWT認証システム

#### 3.1.1 JWTトークン管理

**Good: 安全なJWT実装**
```python
# FastAPI - 包括的なJWT認証システム
from fastapi import FastAPI, Depends, HTTPException, status, Request
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from jose import JWTError, jwt
from passlib.context import CryptContext
from datetime import datetime, timedelta, timezone
from typing import Optional, List, Dict, Any
import redis
import secrets
import hashlib

app = FastAPI(title="JWT Authentication API")

# セキュリティ設定
SECRET_KEY = secrets.token_urlsafe(32)  # 本番では環境変数から
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 15
REFRESH_TOKEN_EXPIRE_DAYS = 7
MAX_LOGIN_ATTEMPTS = 5
LOCKOUT_DURATION_MINUTES = 30

# パスワードハッシュ化
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

# Redisクライアント（ブラックリスト管理）
redis_client = redis.Redis(host='localhost', port=6379, db=0)

# HTTPBearerスキーム
security = HTTPBearer(auto_error=False)

class AuthService:
    """認証サービスクラス"""
    
    @staticmethod
    def hash_password(password: str) -> str:
        """パスワードハッシュ化"""
        return pwd_context.hash(password)
    
    @staticmethod
    def verify_password(plain_password: str, hashed_password: str) -> bool:
        """パスワード検証"""
        return pwd_context.verify(plain_password, hashed_password)
    
    @staticmethod
    def create_access_token(data: Dict[str, Any], expires_delta: Optional[timedelta] = None) -> str:
        """アクセストークン作成"""
        to_encode = data.copy()
        
        if expires_delta:
            expire = datetime.now(timezone.utc) + expires_delta
        else:
            expire = datetime.now(timezone.utc) + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
        
        to_encode.update({
            "exp": expire,
            "iat": datetime.now(timezone.utc),
            "type": "access",
            "jti": secrets.token_urlsafe(16)  # JWT ID
        })
        
        return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    
    @staticmethod
    def create_refresh_token(user_id: str) -> str:
        """リフレッシュトークン作成"""
        expire = datetime.now(timezone.utc) + timedelta(days=REFRESH_TOKEN_EXPIRE_DAYS)
        
        to_encode = {
            "sub": user_id,
            "exp": expire,
            "iat": datetime.now(timezone.utc),
            "type": "refresh",
            "jti": secrets.token_urlsafe(16)
        }
        
        return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    
    @staticmethod
    def verify_token(token: str) -> Optional[Dict[str, Any]]:
        """トークン検証"""
        try:
            # JWTデコード
            payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
            
            # ブラックリストチェック
            jti = payload.get("jti")
            if jti and redis_client.get(f"blacklist:{jti}"):
                return None
            
            return payload
            
        except JWTError:
            return None
    
    @staticmethod
    def blacklist_token(token: str) -> bool:
        """トークンをブラックリストに追加"""
        try:
            payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
            jti = payload.get("jti")
            exp = payload.get("exp")
            
            if jti and exp:
                # 有効期限までブラックリストに保存
                expire_time = exp - int(datetime.now(timezone.utc).timestamp())
                if expire_time > 0:
                    redis_client.setex(f"blacklist:{jti}", expire_time, "1")
                return True
                
        except JWTError:
            pass
        
        return False
    
    @staticmethod
    def check_login_attempts(username: str) -> bool:
        """ログイン試行回数チェック"""
        attempts_key = f"login_attempts:{username}"
        attempts = redis_client.get(attempts_key)
        
        if attempts and int(attempts) >= MAX_LOGIN_ATTEMPTS:
            return False
        
        return True
    
    @staticmethod
    def record_login_attempt(username: str, success: bool) -> None:
        """ログイン試行記録"""
        attempts_key = f"login_attempts:{username}"
        
        if success:
            # 成功時はカウンタリセット
            redis_client.delete(attempts_key)
        else:
            # 失敗時はカウンタインクリメント
            current_attempts = redis_client.incr(attempts_key)
            if current_attempts == 1:
                # 初回失敗時に有効期限設定
                redis_client.expire(attempts_key, LOCKOUT_DURATION_MINUTES * 60)

# 依存性関数
async def get_current_user(credentials: Optional[HTTPAuthorizationCredentials] = Depends(security)):
    """現在のユーザー取得"""
    if not credentials:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Authorization header missing",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    payload = AuthService.verify_token(credentials.credentials)
    if not payload:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or expired token",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    # アクセストークンのみ受け付け
    if payload.get("type") != "access":
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid token type",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    user_id = payload.get("sub")
    if not user_id:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid token payload",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    # ユーザー情報取得
    user = await get_user_by_id(user_id)
    if not user or not user.is_active:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="User not found or inactive",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    return user

async def get_current_active_user(current_user: User = Depends(get_current_user)):
    """アクティブユーザーのみ取得"""
    if not current_user.is_active:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Inactive user"
        )
    return current_user

# ログインエンドポイント
@app.post("/auth/login")
async def login(request: Request, login_data: LoginRequest):
    """
    ユーザーログイン
    
    機能:
    - パスワード検証
    - レート制限
    - JWTトークン発行
    - ログイン履歴記録
    """
    # レート制限チェック
    if not AuthService.check_login_attempts(login_data.username):
        raise HTTPException(
            status_code=status.HTTP_429_TOO_MANY_REQUESTS,
            detail=f"Too many login attempts. Try again in {LOCKOUT_DURATION_MINUTES} minutes."
        )
    
    # ユーザー認証
    user = await authenticate_user(login_data.username, login_data.password)
    if not user:
        # 失敗記録
        AuthService.record_login_attempt(login_data.username, False)
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    # 成功記録
    AuthService.record_login_attempt(login_data.username, True)
    
    # デバイス情報収集
    client_ip = request.client.host
    user_agent = request.headers.get("user-agent", "")
    device_info = {
        "ip": client_ip,
        "user_agent": user_agent,
        "login_time": datetime.now(timezone.utc)
    }
    
    # トークン作成
    access_token = AuthService.create_access_token(
        data={
            "sub": str(user.id),
            "username": user.username,
            "roles": [role.name for role in user.roles],
            "permissions": [perm.name for perm in user.permissions],
            "device_info": device_info
        }
    )
    
    refresh_token = AuthService.create_refresh_token(str(user.id))
    
    # リフレッシュトークンをデータベースに保存
    await store_refresh_token(user.id, refresh_token, device_info)
    
    # 最終ログイン時刻更新
    user.last_login = datetime.now(timezone.utc)
    await update_user(user)
    
    return {
        "access_token": access_token,
        "refresh_token": refresh_token,
        "token_type": "bearer",
        "expires_in": ACCESS_TOKEN_EXPIRE_MINUTES * 60,
        "user": {
            "id": user.id,
            "username": user.username,
            "email": user.email,
            "roles": [role.name for role in user.roles]
        }
    }

@app.post("/auth/refresh")
async def refresh_token(refresh_request: RefreshTokenRequest):
    """トークンリフレッシュ"""
    payload = AuthService.verify_token(refresh_request.refresh_token)
    
    if not payload or payload.get("type") != "refresh":
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid refresh token"
        )
    
    user_id = payload.get("sub")
    if not user_id:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid token payload"
        )
    
    # リフレッシュトークンがデータベースに存在するか確認
    if not await is_refresh_token_valid(user_id, refresh_request.refresh_token):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Refresh token not found or expired"
        )
    
    # ユーザー情報取得
    user = await get_user_by_id(user_id)
    if not user or not user.is_active:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="User not found or inactive"
        )
    
    # 新しいアクセストークン作成
    new_access_token = AuthService.create_access_token(
        data={
            "sub": str(user.id),
            "username": user.username,
            "roles": [role.name for role in user.roles],
            "permissions": [perm.name for perm in user.permissions]
        }
    )
    
    return {
        "access_token": new_access_token,
        "token_type": "bearer",
        "expires_in": ACCESS_TOKEN_EXPIRE_MINUTES * 60
    }

@app.post("/auth/logout")
async def logout(
    logout_request: LogoutRequest,
    current_user: User = Depends(get_current_user)
):
    """ユーザーログアウト"""
    # アクセストークンをブラックリストに追加
    AuthService.blacklist_token(logout_request.access_token)
    
    # リフレッシュトークンをデータベースから削除
    if logout_request.refresh_token:
        await revoke_refresh_token(current_user.id, logout_request.refresh_token)
    
    return {"message": "Successfully logged out"}

@app.post("/auth/logout-all")
async def logout_all_devices(current_user: User = Depends(get_current_user)):
    """全デバイスからログアウト"""
    # ユーザーの全リフレッシュトークンを無効化
    await revoke_all_refresh_tokens(current_user.id)
    
    return {"message": "Successfully logged out from all devices"}

# 保護されたエンドポイント例
@app.get("/users/me")
async def get_current_user_info(current_user: User = Depends(get_current_active_user)):
    """現在のユーザー情報取得"""
    return {
        "id": current_user.id,
        "username": current_user.username,
        "email": current_user.email,
        "roles": [role.name for role in current_user.roles],
        "permissions": [perm.name for perm in current_user.permissions],
        "last_login": current_user.last_login,
        "created_at": current_user.created_at
    }

@app.get("/users/me/sessions")
async def get_user_sessions(current_user: User = Depends(get_current_active_user)):
    """ユーザーのアクティブセッション一覧"""
    sessions = await get_user_active_sessions(current_user.id)
    return {"sessions": sessions}
```

**Bad: 不安全なJWT実装**
```python
# 悪い例：セキュリティ上の問題あり

# 悪い: シークレットキーがハードコード
SECRET_KEY = "my-secret-key"  # 危険！

@app.post("/login")
async def login_bad(username: str, password: str):
    # 悪い: パスワードのプレーンテキスト比較
    user = db.query(User).filter(User.username == username).first()
    if user and user.password == password:  # 危険！
        
        # 悪い: 有効期限が無いトークン
        token = jwt.encode({"sub": user.id}, SECRET_KEY, algorithm="HS256")
        return {"token": token}
    
    return {"error": "Invalid credentials"}

@app.get("/protected")
async def protected_bad(token: str):
    try:
        # 悪い: トークンタイプやブラックリストチェックなし
        payload = jwt.decode(token, SECRET_KEY, algorithms=["HS256"])
        user_id = payload["sub"]
        
        # 悪い: ユーザー存在チェックなし
        return {"user_id": user_id}
        
    except JWTError:
        return {"error": "Invalid token"}

# 悪い: ログアウト機能なし
# 悪い: レート制限なし
# 悪い: リフレッシュトークンなし
```

### 3.2 OAuth 2.0実装

#### 3.2.1 OAuth 2.0 Authorization Code Flow

**Good: 標準的なOAuth 2.0実装**
```python
# Django - OAuth 2.0 Authorization Server実装
from django.http import JsonResponse, HttpResponseRedirect
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth import authenticate
from django.contrib.auth.decorators import login_required
from django.utils.decorators import method_decorator
from django.views import View
from urllib.parse import urlencode, parse_qs
import secrets
import hashlib
import base64
import json
from datetime import datetime, timedelta

class OAuthServer:
    """
    OAuth 2.0 Authorization Server実装
    
    サポートするフロー:
    - Authorization Code Flow
    - Authorization Code Flow with PKCE
    - Client Credentials Flow
    - Refresh Token Flow
    """
    
    # サポートするグラントタイプ
    SUPPORTED_GRANT_TYPES = [
        'authorization_code',
        'client_credentials',
        'refresh_token'
    ]
    
    # サポートするレスポンスタイプ
    SUPPORTED_RESPONSE_TYPES = ['code']
    
    # サポートするスコープ
    SUPPORTED_SCOPES = {
        'read': 'Read access to user data',
        'write': 'Write access to user data',
        'profile': 'Access to user profile information',
        'email': 'Access to user email address',
        'openid': 'OpenID Connect authentication'
    }
    
    @staticmethod
    def generate_authorization_code() -> str:
        """認証コード生成"""
        return secrets.token_urlsafe(32)
    
    @staticmethod
    def generate_pkce_challenge(code_verifier: str) -> str:
        """
PKCEコードチャレンジ生成"""
        code_sha = hashlib.sha256(code_verifier.encode('utf-8')).digest()
        code_challenge = base64.urlsafe_b64encode(code_sha).decode('utf-8').rstrip('=')
        return code_challenge
    
    @staticmethod
    def verify_pkce_challenge(code_verifier: str, code_challenge: str) -> bool:
        """
PKCEコードチャレンジ検証"""
        expected_challenge = OAuthServer.generate_pkce_challenge(code_verifier)
        return expected_challenge == code_challenge

@method_decorator(csrf_exempt, name='dispatch')
class AuthorizeView(View):
    """
    OAuth 2.0 Authorization Endpoint
    
    GET /oauth/authorize?response_type=code&client_id=CLIENT_ID&redirect_uri=REDIRECT_URI&scope=SCOPE&state=STATE
    """
    
    def get(self, request):
        """認証リクエスト処理"""
        # パラメータ取得
        response_type = request.GET.get('response_type')
        client_id = request.GET.get('client_id')
        redirect_uri = request.GET.get('redirect_uri')
        scope = request.GET.get('scope', '')
        state = request.GET.get('state')
        code_challenge = request.GET.get('code_challenge')
        code_challenge_method = request.GET.get('code_challenge_method', 'S256')
        
        # 必須パラメータチェック
        if not all([response_type, client_id, redirect_uri]):
            return JsonResponse({
                'error': 'invalid_request',
                'error_description': 'Missing required parameters'
            }, status=400)
        
        # レスポンスタイプ検証
        if response_type not in OAuthServer.SUPPORTED_RESPONSE_TYPES:
            return self._redirect_error(redirect_uri, 'unsupported_response_type', state)
        
        # クリエント検証
        try:
            client = OAuthClient.objects.get(client_id=client_id, is_active=True)
        except OAuthClient.DoesNotExist:
            return self._redirect_error(redirect_uri, 'invalid_client', state)
        
        # リダイレクトURI検証
        if redirect_uri not in client.redirect_uris:
            return JsonResponse({
                'error': 'invalid_request',
                'error_description': 'Invalid redirect_uri'
            }, status=400)
        
        # スコープ検証
        requested_scopes = set(scope.split()) if scope else set()
        allowed_scopes = set(client.allowed_scopes)
        invalid_scopes = requested_scopes - allowed_scopes
        
        if invalid_scopes:
            return self._redirect_error(
                redirect_uri, 
                'invalid_scope', 
                state,
                f"Invalid scopes: {', '.join(invalid_scopes)}"
            )
        
        # PKCEチェック
        if client.require_pkce and not code_challenge:
            return self._redirect_error(
                redirect_uri,
                'invalid_request',
                state,
                'PKCE code_challenge required'
            )
        
        # ユーザー認証状態チェック
        if not request.user.is_authenticated:
            # ログインページにリダイレクト
            login_url = f"/auth/login?next={request.get_full_path()}"
            return HttpResponseRedirect(login_url)
        
        # ユーザーの同意確認
        existing_authorization = OAuthAuthorization.objects.filter(
            user=request.user,
            client=client,
            scope__contains=list(requested_scopes)
        ).first()
        
        if not existing_authorization and not client.skip_authorization:
            # 同意ページ表示
            return self._show_consent_page(
                request, client, requested_scopes, 
                redirect_uri, state, code_challenge, code_challenge_method
            )
        
        # 認証コード発行
        return self._issue_authorization_code(
            request.user, client, requested_scopes,
            redirect_uri, state, code_challenge, code_challenge_method
        )
    
    def post(self, request):
        """ユーザー同意処理"""
        # 同意フォームからのデータ取得
        action = request.POST.get('action')
        client_id = request.POST.get('client_id')
        redirect_uri = request.POST.get('redirect_uri')
        scope = request.POST.get('scope', '')
        state = request.POST.get('state')
        code_challenge = request.POST.get('code_challenge')
        code_challenge_method = request.POST.get('code_challenge_method')
        
        if action == 'deny':
            # ユーザーが拒否
            return self._redirect_error(
                redirect_uri, 'access_denied', state,
                'User denied the authorization request'
            )
        
        if action == 'allow':
            # ユーザーが許可
            try:
                client = OAuthClient.objects.get(client_id=client_id)
                requested_scopes = set(scope.split()) if scope else set()
                
                # 認証コード発行
                return self._issue_authorization_code(
                    request.user, client, requested_scopes,
                    redirect_uri, state, code_challenge, code_challenge_method
                )
                
            except OAuthClient.DoesNotExist:
                return self._redirect_error(redirect_uri, 'invalid_client', state)
        
        return self._redirect_error(redirect_uri, 'invalid_request', state)
    
    def _show_consent_page(self, request, client, scopes, redirect_uri, state, code_challenge, code_challenge_method):
        """同意ページ表示"""
        scope_descriptions = {
            scope: OAuthServer.SUPPORTED_SCOPES.get(scope, scope)
            for scope in scopes
        }
        
        context = {
            'client': client,
            'scopes': scope_descriptions,
            'redirect_uri': redirect_uri,
            'state': state,
            'code_challenge': code_challenge,
            'code_challenge_method': code_challenge_method,
            'client_id': client.client_id,
            'scope': ' '.join(scopes)
        }
        
        return render(request, 'oauth/consent.html', context)
    
    def _issue_authorization_code(self, user, client, scopes, redirect_uri, state, code_challenge, code_challenge_method):
        """認証コード発行"""
        # 認証コード生成
        code = OAuthServer.generate_authorization_code()
        
        # 認証コードをデータベースに保存
        auth_code = OAuthAuthorizationCode.objects.create(
            code=code,
            user=user,
            client=client,
            redirect_uri=redirect_uri,
            scope=list(scopes),
            code_challenge=code_challenge,
            code_challenge_method=code_challenge_method,
            expires_at=datetime.now() + timedelta(minutes=10)  # 10分有効
        )
        
        # ユーザー認証記録保存
        OAuthAuthorization.objects.update_or_create(
            user=user,
            client=client,
            defaults={
                'scope': list(scopes),
                'authorized_at': datetime.now()
            }
        )
        
        # リダイレクトURL構築
        params = {'code': code}
        if state:
            params['state'] = state
        
        redirect_url = f"{redirect_uri}?{urlencode(params)}"
        return HttpResponseRedirect(redirect_url)
    
    def _redirect_error(self, redirect_uri, error, state=None, description=None):
        """エラーリダイレクト"""
        params = {'error': error}
        if state:
            params['state'] = state
        if description:
            params['error_description'] = description
        
        redirect_url = f"{redirect_uri}?{urlencode(params)}"
        return HttpResponseRedirect(redirect_url)

@method_decorator(csrf_exempt, name='dispatch')
class TokenView(View):
    """
    OAuth 2.0 Token Endpoint
    
    POST /oauth/token
    """
    
    def post(self, request):
        """トークンリクエスト処理"""
        grant_type = request.POST.get('grant_type')
        
        if grant_type not in OAuthServer.SUPPORTED_GRANT_TYPES:
            return JsonResponse({
                'error': 'unsupported_grant_type',
                'error_description': f'Grant type {grant_type} is not supported'
            }, status=400)
        
        if grant_type == 'authorization_code':
            return self._handle_authorization_code(request)
        elif grant_type == 'client_credentials':
            return self._handle_client_credentials(request)
        elif grant_type == 'refresh_token':
            return self._handle_refresh_token(request)
        
        return JsonResponse({
            'error': 'invalid_request',
            'error_description': 'Invalid grant_type'
        }, status=400)
    
    def _handle_authorization_code(self, request):
        """
Authorization Codeグラント処理"""
        code = request.POST.get('code')
        client_id = request.POST.get('client_id')
        client_secret = request.POST.get('client_secret')
        redirect_uri = request.POST.get('redirect_uri')
        code_verifier = request.POST.get('code_verifier')
        
        if not all([code, client_id, redirect_uri]):
            return JsonResponse({
                'error': 'invalid_request',
                'error_description': 'Missing required parameters'
            }, status=400)
        
        # クライアント認証
        try:
            client = OAuthClient.objects.get(client_id=client_id)
            
            # Confidentialクライアントの場合はシークレット検証
            if client.client_type == 'confidential':
                if not client_secret or not client.verify_secret(client_secret):
                    return JsonResponse({
                        'error': 'invalid_client',
                        'error_description': 'Invalid client credentials'
                    }, status=401)
            
        except OAuthClient.DoesNotExist:
            return JsonResponse({
                'error': 'invalid_client',
                'error_description': 'Client not found'
            }, status=401)
        
        # 認証コード検証
        try:
            auth_code = OAuthAuthorizationCode.objects.get(
                code=code, 
                client=client,
                used=False
            )
            
            # 有効期限チェック
            if auth_code.is_expired():
                auth_code.delete()
                return JsonResponse({
                    'error': 'invalid_grant',
                    'error_description': 'Authorization code expired'
                }, status=400)
            
            # リダイレクトURI一致チェック
            if auth_code.redirect_uri != redirect_uri:
                return JsonResponse({
                    'error': 'invalid_grant',
                    'error_description': 'Redirect URI mismatch'
                }, status=400)
            
            # PKCE検証
            if auth_code.code_challenge:
                if not code_verifier:
                    return JsonResponse({
                        'error': 'invalid_request',
                        'error_description': 'PKCE code_verifier required'
                    }, status=400)
                
                if not OAuthServer.verify_pkce_challenge(code_verifier, auth_code.code_challenge):
                    return JsonResponse({
                        'error': 'invalid_grant',
                        'error_description': 'Invalid PKCE code_verifier'
                    }, status=400)
            
        except OAuthAuthorizationCode.DoesNotExist:
            return JsonResponse({
                'error': 'invalid_grant',
                'error_description': 'Invalid authorization code'
            }, status=400)
        
        # アクセストークン発行
        access_token = self._create_access_token(
            user=auth_code.user,
            client=client,
            scope=auth_code.scope
        )
        
        refresh_token = self._create_refresh_token(
            user=auth_code.user,
            client=client,
            scope=auth_code.scope
        )
        
        # 認証コードを使用済みにマーク
        auth_code.used = True
        auth_code.save()
        
        response_data = {
            'access_token': access_token.token,
            'token_type': 'Bearer',
            'expires_in': 3600,  # 1時間
            'refresh_token': refresh_token.token,
            'scope': ' '.join(auth_code.scope)
        }
        
        # OpenID Connectのid_token追加
        if 'openid' in auth_code.scope:
            id_token = self._create_id_token(auth_code.user, client, auth_code.scope)
            response_data['id_token'] = id_token
        
        return JsonResponse(response_data)
    
    def _handle_client_credentials(self, request):
        """
Client Credentialsグラント処理"""
        client_id = request.POST.get('client_id')
        client_secret = request.POST.get('client_secret')
        scope = request.POST.get('scope', '')
        
        if not all([client_id, client_secret]):
            return JsonResponse({
                'error': 'invalid_request',
                'error_description': 'Missing client credentials'
            }, status=400)
        
        # クライアント認証
        try:
            client = OAuthClient.objects.get(client_id=client_id)
            
            if not client.verify_secret(client_secret):
                return JsonResponse({
                    'error': 'invalid_client',
                    'error_description': 'Invalid client credentials'
                }, status=401)
            
            # Client Credentialsグラントが許可されているかチェック
            if 'client_credentials' not in client.allowed_grant_types:
                return JsonResponse({
                    'error': 'unauthorized_client',
                    'error_description': 'Client not authorized for this grant type'
                }, status=400)
            
        except OAuthClient.DoesNotExist:
            return JsonResponse({
                'error': 'invalid_client',
                'error_description': 'Client not found'
            }, status=401)
        
        # スコープ検証
        requested_scopes = set(scope.split()) if scope else set()
        allowed_scopes = set(client.allowed_scopes)
        invalid_scopes = requested_scopes - allowed_scopes
        
        if invalid_scopes:
            return JsonResponse({
                'error': 'invalid_scope',
                'error_description': f'Invalid scopes: {", ".join(invalid_scopes)}'
            }, status=400)
        
        # アップリケーション用アクセストークン発行（ユーザーなし）
        access_token = self._create_access_token(
            user=None,  # Client Credentialsではユーザーなし
            client=client,
            scope=list(requested_scopes)
        )
        
        return JsonResponse({
            'access_token': access_token.token,
            'token_type': 'Bearer',
            'expires_in': 3600,
            'scope': ' '.join(requested_scopes)
        })
    
    def _handle_refresh_token(self, request):
        """
Refresh Tokenグラント処理"""
        refresh_token = request.POST.get('refresh_token')
        client_id = request.POST.get('client_id')
        client_secret = request.POST.get('client_secret')
        scope = request.POST.get('scope')
        
        if not all([refresh_token, client_id]):
            return JsonResponse({
                'error': 'invalid_request',
                'error_description': 'Missing required parameters'
            }, status=400)
        
        # クライアント認証
        try:
            client = OAuthClient.objects.get(client_id=client_id)
            
            if client.client_type == 'confidential':
                if not client_secret or not client.verify_secret(client_secret):
                    return JsonResponse({
                        'error': 'invalid_client',
                        'error_description': 'Invalid client credentials'
                    }, status=401)
            
        except OAuthClient.DoesNotExist:
            return JsonResponse({
                'error': 'invalid_client',
                'error_description': 'Client not found'
            }, status=401)
        
        # リフレッシュトークン検証
        try:
            refresh_token_obj = OAuthRefreshToken.objects.get(
                token=refresh_token,
                client=client,
                revoked=False
            )
            
            if refresh_token_obj.is_expired():
                return JsonResponse({
                    'error': 'invalid_grant',
                    'error_description': 'Refresh token expired'
                }, status=400)
            
        except OAuthRefreshToken.DoesNotExist:
            return JsonResponse({
                'error': 'invalid_grant',
                'error_description': 'Invalid refresh token'
            }, status=400)
        
        # スコープチェック
        if scope:
            requested_scopes = set(scope.split())
            original_scopes = set(refresh_token_obj.scope)
            
            # リフレッシュ時はオリジナルより狭いスコープのみ許可
            if not requested_scopes.issubset(original_scopes):
                return JsonResponse({
                    'error': 'invalid_scope',
                    'error_description': 'Requested scope exceeds original scope'
                }, status=400)
            
            final_scope = list(requested_scopes)
        else:
            final_scope = refresh_token_obj.scope
        
        # 新しいアクセストークン発行
        new_access_token = self._create_access_token(
            user=refresh_token_obj.user,
            client=client,
            scope=final_scope
        )
        
        # 新しいリフレッシュトークン発行（ローテーション）
        new_refresh_token = self._create_refresh_token(
            user=refresh_token_obj.user,
            client=client,
            scope=final_scope
        )
        
        # 古いリフレッシュトークンを無効化
        refresh_token_obj.revoked = True
        refresh_token_obj.save()
        
        return JsonResponse({
            'access_token': new_access_token.token,
            'token_type': 'Bearer',
            'expires_in': 3600,
            'refresh_token': new_refresh_token.token,
            'scope': ' '.join(final_scope)
        })
    
    def _create_access_token(self, user, client, scope):
        """アクセストークン作成"""
        return OAuthAccessToken.objects.create(
            token=secrets.token_urlsafe(32),
            user=user,
            client=client,
            scope=scope,
            expires_at=datetime.now() + timedelta(hours=1)
        )
    
    def _create_refresh_token(self, user, client, scope):
        """リフレッシュトークン作成"""
        return OAuthRefreshToken.objects.create(
            token=secrets.token_urlsafe(32),
            user=user,
            client=client,
            scope=scope,
            expires_at=datetime.now() + timedelta(days=30)
        )
    
    def _create_id_token(self, user, client, scope):
        """
OpenID Connect IDトークン作成"""
        now = datetime.now(timezone.utc)
        
        payload = {
            'iss': 'https://your-domain.com',  # Issuer
            'sub': str(user.id),  # Subject
            'aud': client.client_id,  # Audience
            'exp': int((now + timedelta(hours=1)).timestamp()),  # Expiration
            'iat': int(now.timestamp()),  # Issued At
            'nonce': secrets.token_urlsafe(16)  # Nonce
        }
        
        # スコープに応じてユーザー情報追加
        if 'profile' in scope:
            payload.update({
                'name': f"{user.first_name} {user.last_name}",
                'given_name': user.first_name,
                'family_name': user.last_name,
                'preferred_username': user.username
            })
        
        if 'email' in scope:
            payload.update({
                'email': user.email,
                'email_verified': user.email_verified
            })
        
        # JWT署名
        return jwt.encode(payload, settings.SECRET_KEY, algorithm='HS256')

# リソースサーバー側のアクセストークン検証
class OAuthResourceProtection:
    """
OAuth 2.0リソース保護ミドルウェア"""
    
    @staticmethod
    def require_oauth(required_scopes=None):
        """
OAuth認証デコレータ"""
        def decorator(view_func):
            def wrapped_view(request, *args, **kwargs):
                # Authorizationヘッダー取得
                auth_header = request.META.get('HTTP_AUTHORIZATION', '')
                
                if not auth_header.startswith('Bearer '):
                    return JsonResponse({
                        'error': 'invalid_request',
                        'error_description': 'Missing or invalid Authorization header'
                    }, status=401)
                
                token = auth_header.split(' ')[1]
                
                # アクセストークン検証
                try:
                    access_token = OAuthAccessToken.objects.select_related('user', 'client').get(
                        token=token,
                        revoked=False
                    )
                    
                    if access_token.is_expired():
                        return JsonResponse({
                            'error': 'invalid_token',
                            'error_description': 'Access token expired'
                        }, status=401)
                    
                except OAuthAccessToken.DoesNotExist:
                    return JsonResponse({
                        'error': 'invalid_token',
                        'error_description': 'Invalid access token'
                    }, status=401)
                
                # スコープチェック
                if required_scopes:
                    token_scopes = set(access_token.scope)
                    if not set(required_scopes).issubset(token_scopes):
                        return JsonResponse({
                            'error': 'insufficient_scope',
                            'error_description': f'Required scopes: {", ".join(required_scopes)}'
                        }, status=403)
                
                # リクエストにユーザーとクライアント情報を追加
                request.oauth_user = access_token.user
                request.oauth_client = access_token.client
                request.oauth_scopes = access_token.scope
                
                return view_func(request, *args, **kwargs)
            
            return wrapped_view
        return decorator

# 使用例
@OAuthResourceProtection.require_oauth(['read', 'profile'])
def get_user_profile(request):
    """
OAuthで保護されたウーザーフロファイルAPI"""
    user = request.oauth_user
    client = request.oauth_client
    
    return JsonResponse({
        'id': user.id,
        'username': user.username,
        'email': user.email,
        'client_name': client.name
    })
```

**Bad: OAuth 2.0の悪い実装**
```python
# 悪い例：セキュリティ上の問題あり

@csrf_exempt
def oauth_authorize_bad(request):
    client_id = request.GET.get('client_id')
    redirect_uri = request.GET.get('redirect_uri')
    
    # 悪い: クライアント検証なし
    # 悪い: リダイレクトURI検証なし
    # 悪い: stateパラメータチェックなし
    
    code = "simple_code_123"  # 悪い: 推測可能なコード
    return redirect(f"{redirect_uri}?code={code}")

@csrf_exempt
def oauth_token_bad(request):
    code = request.POST.get('code')
    
    # 悪い: コード検証なし
    # 悪い: クライアント認証なし
    # 悪い: PKCEなし
    
    # 悪い: 無限に有効なトークン
    token = "permanent_token_" + code
    
    return JsonResponse({
        'access_token': token,
        'token_type': 'bearer'
        # 悪い: expires_inなし
        # 悪い: refresh_tokenなし
    })

def protected_resource_bad(request):
    token = request.GET.get('token')  # 悪い: クエリパラメータでトークン送信
    
    # 悪い: トークン検証なし
    # 悪い: スコープチェックなし
    
    return JsonResponse({'data': 'sensitive_data'})
```

### 3.3 ロールベースアクセス制御 (RBAC)

#### 3.3.1 粒度の細かい権限管理

**Good: 高度なRBACシステム**
```python
# Django - 高度なロールベースアクセス制御システム
from django.contrib.auth.models import AbstractUser
from django.db import models
from django.contrib.contenttypes.models import ContentType
from django.contrib.contenttypes.fields import GenericForeignKey
from functools import wraps
from typing import List, Dict, Any, Optional
import json

# ロールと権限のモデル定義
class Permission(models.Model):
    """細かい権限定義"""
    
    RESOURCE_TYPES = [
        ('user', 'User Management'),
        ('post', 'Post Management'),
        ('comment', 'Comment Management'),
        ('analytics', 'Analytics Access'),
        ('admin', 'Administrative Functions'),
        ('billing', 'Billing Management'),
        ('system', 'System Configuration'),
    ]
    
    ACTIONS = [
        ('create', 'Create'),
        ('read', 'Read'),
        ('update', 'Update'),
        ('delete', 'Delete'),
        ('list', 'List'),
        ('manage', 'Manage'),
        ('export', 'Export'),
        ('import', 'Import'),
    ]
    
    name = models.CharField(max_length=100, unique=True)
    resource_type = models.CharField(max_length=50, choices=RESOURCE_TYPES)
    action = models.CharField(max_length=50, choices=ACTIONS)
    description = models.TextField(blank=True)
    
    # 高度な条件指定
    conditions = models.JSONField(default=dict, blank=True)
    
    created_at = models.DateTimeField(auto_now_add=True)
    
    class Meta:
        unique_together = ['resource_type', 'action']
        ordering = ['resource_type', 'action']
    
    def __str__(self):
        return f"{self.resource_type}:{self.action}"
    
    def check_conditions(self, user, resource=None, context=None):
        """条件チェック"""
        if not self.conditions:
            return True
        
        context = context or {}
        context.update({
            'user': user,
            'resource': resource,
            'user_id': user.id,
            'is_staff': user.is_staff,
            'is_superuser': user.is_superuser
        })
        
        return self._evaluate_conditions(self.conditions, context)
    
    def _evaluate_conditions(self, conditions, context):
        """条件評価エンジン"""
        if isinstance(conditions, dict):
            operator = conditions.get('operator', 'and')
            rules = conditions.get('rules', [])
            
            if operator == 'and':
                return all(self._evaluate_rule(rule, context) for rule in rules)
            elif operator == 'or':
                return any(self._evaluate_rule(rule, context) for rule in rules)
            elif operator == 'not':
                return not all(self._evaluate_rule(rule, context) for rule in rules)
        
        return True
    
    def _evaluate_rule(self, rule, context):
        """ルール評価"""
        field = rule.get('field')
        operator = rule.get('operator')
        value = rule.get('value')
        
        if not all([field, operator]):
            return True
        
        # コンテキストから値取得
        field_value = self._get_field_value(field, context)
        
        # 演算子による比較
        if operator == 'eq':
            return field_value == value
        elif operator == 'ne':
            return field_value != value
        elif operator == 'gt':
            return field_value > value
        elif operator == 'gte':
            return field_value >= value
        elif operator == 'lt':
            return field_value < value
        elif operator == 'lte':
            return field_value <= value
        elif operator == 'in':
            return field_value in value
        elif operator == 'not_in':
            return field_value not in value
        elif operator == 'contains':
            return value in field_value
        elif operator == 'startswith':
            return str(field_value).startswith(str(value))
        elif operator == 'endswith':
            return str(field_value).endswith(str(value))
        
        return True
    
    def _get_field_value(self, field, context):
        """フィールド値取得"""
        parts = field.split('.')
        value = context
        
        for part in parts:
            if hasattr(value, part):
                value = getattr(value, part)
            elif isinstance(value, dict) and part in value:
                value = value[part]
            else:
                return None
        
        return value

class Role(models.Model):
    """ロール定義"""
    
    ROLE_TYPES = [
        ('system', 'System Role'),
        ('organization', 'Organization Role'),
        ('project', 'Project Role'),
        ('custom', 'Custom Role'),
    ]
    
    name = models.CharField(max_length=100, unique=True)
    description = models.TextField(blank=True)
    role_type = models.CharField(max_length=20, choices=ROLE_TYPES, default='custom')
    
    # 権限の関連付け
    permissions = models.ManyToManyField(Permission, through='RolePermission')
    
    # 階層構造
    parent_role = models.ForeignKey('self', on_delete=models.CASCADE, null=True, blank=True)
    
    # メタデータ
    is_active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    
    class Meta:
        ordering = ['name']
    
    def __str__(self):
        return self.name
    
    def get_all_permissions(self):
        """継承された権限も含めて取得"""
        permissions = set(self.permissions.all())
        
        # 親ロールの権限を継承
        if self.parent_role:
            permissions.update(self.parent_role.get_all_permissions())
        
        return permissions
    
    def has_permission(self, permission_name, user=None, resource=None, context=None):
        """権限チェック"""
        try:
            permission = Permission.objects.get(name=permission_name)
            
            if permission in self.get_all_permissions():
                return permission.check_conditions(user, resource, context)
            
        except Permission.DoesNotExist:
            pass
        
        return False

class RolePermission(models.Model):
    """ロールと権限の中間テーブル"""
    role = models.ForeignKey(Role, on_delete=models.CASCADE)
    permission = models.ForeignKey(Permission, on_delete=models.CASCADE)
    
    # 追加制約条件
    additional_conditions = models.JSONField(default=dict, blank=True)
    
    granted_at = models.DateTimeField(auto_now_add=True)
    granted_by = models.ForeignKey('auth.User', on_delete=models.SET_NULL, null=True)
    
    class Meta:
        unique_together = ['role', 'permission']

class UserRole(models.Model):
    """ユーザーとロールの関連付け"""
    user = models.ForeignKey('auth.User', on_delete=models.CASCADE)
    role = models.ForeignKey(Role, on_delete=models.CASCADE)
    
    # スコープ制限
    content_type = models.ForeignKey(ContentType, on_delete=models.CASCADE, null=True, blank=True)
    object_id = models.PositiveIntegerField(null=True, blank=True)
    content_object = GenericForeignKey('content_type', 'object_id')
    
    # 有効期限
    valid_from = models.DateTimeField(null=True, blank=True)
    valid_until = models.DateTimeField(null=True, blank=True)
    
    # メタデータ
    assigned_at = models.DateTimeField(auto_now_add=True)
    assigned_by = models.ForeignKey('auth.User', on_delete=models.SET_NULL, null=True, related_name='assigned_roles')
    is_active = models.BooleanField(default=True)
    
    class Meta:
        unique_together = ['user', 'role', 'content_type', 'object_id']
    
    def is_valid(self):
        """ロールが有効かチェック"""
        if not self.is_active:
            return False
        
        now = timezone.now()
        
        if self.valid_from and now < self.valid_from:
            return False
        
        if self.valid_until and now > self.valid_until:
            return False
        
        return True

# ユーザーモデルの拡張
class User(AbstractUser):
    """拡張ユーザーモデル"""
    
    # ロール関連
    roles = models.ManyToManyField(Role, through=UserRole, blank=True)
    
    def get_all_roles(self, scope_object=None):
        """有効なロールをすべて取得"""
        user_roles = UserRole.objects.filter(
            user=self,
            is_active=True
        ).select_related('role')
        
        if scope_object:
            content_type = ContentType.objects.get_for_model(scope_object)
            user_roles = user_roles.filter(
                models.Q(content_type=content_type, object_id=scope_object.id) |
                models.Q(content_type__isnull=True, object_id__isnull=True)
            )
        
        valid_roles = []
        for user_role in user_roles:
            if user_role.is_valid():
                valid_roles.append(user_role.role)
        
        return valid_roles
    
    def get_all_permissions(self, scope_object=None):
        """有効な権限をすべて取得"""
        permissions = set()
        
        for role in self.get_all_roles(scope_object):
            permissions.update(role.get_all_permissions())
        
        return permissions
    
    def has_permission(self, permission_name, resource=None, scope_object=None, context=None):
        """権限チェック"""
        # スーパーユーザーは全権限あり
        if self.is_superuser:
            return True
        
        for role in self.get_all_roles(scope_object):
            if role.has_permission(permission_name, self, resource, context):
                return True
        
        return False
    
    def has_role(self, role_name, scope_object=None):
        """ロール保持チェック"""
        return any(
            role.name == role_name 
            for role in self.get_all_roles(scope_object)
        )

# 権限チェックデコレータ
class PermissionRequired:
    """権限チェックデコレータ"""
    
    def __init__(self, permission_name, resource_param=None, scope_param=None):
        self.permission_name = permission_name
        self.resource_param = resource_param
        self.scope_param = scope_param
    
    def __call__(self, func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            # Django REST FrameworkのViewSetでの使用を想定
            if hasattr(args[0], 'request'):
                request = args[0].request
            else:
                request = args[0]  # Function-based view
            
            if not hasattr(request, 'user') or not request.user.is_authenticated:
                from django.http import JsonResponse
                return JsonResponse({
                    'error': 'Authentication required',
                    'error_code': 'AUTHENTICATION_REQUIRED'
                }, status=401)
            
            # リソースオブジェクト取得
            resource = None
            if self.resource_param and self.resource_param in kwargs:
                resource = kwargs[self.resource_param]
            
            # スコープオブジェクト取得
            scope_object = None
            if self.scope_param and self.scope_param in kwargs:
                scope_object = kwargs[self.scope_param]
            
            # 権限チェック
            if not request.user.has_permission(
                self.permission_name, 
                resource=resource, 
                scope_object=scope_object
            ):
                from django.http import JsonResponse
                return JsonResponse({
                    'error': 'Insufficient permissions',
                    'error_code': 'PERMISSION_DENIED',
                    'required_permission': self.permission_name
                }, status=403)
            
            return func(*args, **kwargs)
        
        return wrapper

# Django REST Frameworkでの使用例
from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response

class PostViewSet(viewsets.ModelViewSet):
    """権限制御付き投稿API"""
    
    queryset = Post.objects.all()
    serializer_class = PostSerializer
    
    def get_queryset(self):
        """ユーザーの権限に応じてクエリセットをフィルタ"""
        queryset = super().get_queryset()
        
        # 管理者は全投稿を閲覧可能
        if self.request.user.has_permission('post:manage'):
            return queryset
        
        # 一般ユーザーは公開投稿と自分の投稿のみ
        return queryset.filter(
            models.Q(is_published=True) |
            models.Q(author=self.request.user)
        )
    
    @PermissionRequired('post:create')
    def create(self, request):
        """投稿作成"""
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        
        # 作成者を自動設定
        serializer.save(author=request.user)
        
        return Response(serializer.data, status=status.HTTP_201_CREATED)
    
    @PermissionRequired('post:read', resource_param='instance')
    def retrieve(self, request, pk=None):
        """投稿詳細取得"""
        instance = self.get_object()
        
        # 非公開投稿の場合、作成者または管理者のみ閲覧可能
        if not instance.is_published:
            if not (instance.author == request.user or 
                   request.user.has_permission('post:manage')):
                return Response({
                    'error': 'Post not found or access denied'
                }, status=status.HTTP_404_NOT_FOUND)
        
        serializer = self.get_serializer(instance)
        return Response(serializer.data)
    
    @PermissionRequired('post:update', resource_param='instance')
    def update(self, request, pk=None):
        """投稿更新"""
        instance = self.get_object()
        
        # 作成者または管理者のみ更新可能
        if not (instance.author == request.user or 
               request.user.has_permission('post:manage')):
            return Response({
                'error': 'Permission denied'
            }, status=status.HTTP_403_FORBIDDEN)
        
        serializer = self.get_serializer(instance, data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        
        return Response(serializer.data)
    
    @PermissionRequired('post:delete', resource_param='instance')
    def destroy(self, request, pk=None):
        """投稿削除"""
        instance = self.get_object()
        
        # 作成者または管理者のみ削除可能
        if not (instance.author == request.user or 
               request.user.has_permission('post:manage')):
            return Response({
                'error': 'Permission denied'
            }, status=status.HTTP_403_FORBIDDEN)
        
        instance.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)
    
    @action(detail=True, methods=['post'])
    @PermissionRequired('post:manage')
    def publish(self, request, pk=None):
        """投稿公開（管理者のみ）"""
        instance = self.get_object()
        instance.is_published = True
        instance.published_at = timezone.now()
        instance.save()
        
        return Response({
            'message': 'Post published successfully',
            'published_at': instance.published_at
        })
    
    @action(detail=False, methods=['get'])
    @PermissionRequired('post:manage')
    def analytics(self, request):
        """投稿分析データ（管理者のみ）"""
        from django.db.models import Count, Avg
        
        stats = {
            'total_posts': Post.objects.count(),
            'published_posts': Post.objects.filter(is_published=True).count(),
            'draft_posts': Post.objects.filter(is_published=False).count(),
            'posts_by_author': list(
                Post.objects.values('author__username')
                .annotate(count=Count('id'))
                .order_by('-count')[:10]
            ),
            'average_comments': Post.objects.aggregate(
                avg_comments=Avg('comments__count')
            )['avg_comments'] or 0
        }
        
        return Response(stats)

# 権限初期化スクリプト
def initialize_permissions():
    """権限システムの初期化"""
    
    # 基本権限作成
    permissions_data = [
        # ユーザー管理
        {'name': 'user:create', 'resource_type': 'user', 'action': 'create'},
        {'name': 'user:read', 'resource_type': 'user', 'action': 'read'},
        {'name': 'user:update', 'resource_type': 'user', 'action': 'update',
         'conditions': {
             'operator': 'or',
             'rules': [
                 {'field': 'user_id', 'operator': 'eq', 'value': 'resource.id'},
                 {'field': 'is_staff', 'operator': 'eq', 'value': True}
             ]
         }},
        {'name': 'user:delete', 'resource_type': 'user', 'action': 'delete'},
        {'name': 'user:manage', 'resource_type': 'user', 'action': 'manage'},
        
        # 投稿管理
        {'name': 'post:create', 'resource_type': 'post', 'action': 'create'},
        {'name': 'post:read', 'resource_type': 'post', 'action': 'read'},
        {'name': 'post:update', 'resource_type': 'post', 'action': 'update',
         'conditions': {
             'operator': 'or',
             'rules': [
                 {'field': 'resource.author_id', 'operator': 'eq', 'value': 'user_id'},
                 {'field': 'is_staff', 'operator': 'eq', 'value': True}
             ]
         }},
        {'name': 'post:delete', 'resource_type': 'post', 'action': 'delete'},
        {'name': 'post:manage', 'resource_type': 'post', 'action': 'manage'},
        
        # コメント管理
        {'name': 'comment:create', 'resource_type': 'comment', 'action': 'create'},
        {'name': 'comment:read', 'resource_type': 'comment', 'action': 'read'},
        {'name': 'comment:update', 'resource_type': 'comment', 'action': 'update'},
        {'name': 'comment:delete', 'resource_type': 'comment', 'action': 'delete'},
        {'name': 'comment:manage', 'resource_type': 'comment', 'action': 'manage'},
        
        # 分析とレポート
        {'name': 'analytics:read', 'resource_type': 'analytics', 'action': 'read'},
        {'name': 'analytics:export', 'resource_type': 'analytics', 'action': 'export'},
        
        # 管理機能
        {'name': 'admin:access', 'resource_type': 'admin', 'action': 'read'},
        {'name': 'admin:manage', 'resource_type': 'admin', 'action': 'manage'},
        
        # システム設定
        {'name': 'system:config', 'resource_type': 'system', 'action': 'manage'},
    ]
    
    for perm_data in permissions_data:
        Permission.objects.get_or_create(
            name=perm_data['name'],
            defaults=perm_data
        )
    
    # 基本ロール作成
    roles_data = [
        {
            'name': 'super_admin',
            'description': 'システム管理者（全権限）',
            'role_type': 'system',
            'permissions': [perm['name'] for perm in permissions_data]
        },
        {
            'name': 'admin',
            'description': '管理者',
            'role_type': 'system',
            'permissions': [
                'user:read', 'user:manage',
                'post:read', 'post:manage',
                'comment:read', 'comment:manage',
                'analytics:read', 'admin:access'
            ]
        },
        {
            'name': 'moderator',
            'description': 'モデレーター',
            'role_type': 'organization',
            'permissions': [
                'post:read', 'post:update', 'post:delete',
                'comment:read', 'comment:manage'
            ]
        },
        {
            'name': 'author',
            'description': '投稿者',
            'role_type': 'organization',
            'permissions': [
                'post:create', 'post:read', 'post:update',
                'comment:create', 'comment:read'
            ]
        },
        {
            'name': 'reader',
            'description': '読者',
            'role_type': 'organization',
            'permissions': [
                'post:read', 'comment:read'
            ]
        }
    ]
    
    for role_data in roles_data:
        role, created = Role.objects.get_or_create(
            name=role_data['name'],
            defaults={
                'description': role_data['description'],
                'role_type': role_data['role_type']
            }
        )
        
        if created:
            # 権限をロールに関連付け
            for perm_name in role_data['permissions']:
                try:
                    permission = Permission.objects.get(name=perm_name)
                    RolePermission.objects.get_or_create(
                        role=role,
                        permission=permission
                    )
                except Permission.DoesNotExist:
                    continue

# 使用例
def assign_user_role(user, role_name, scope_object=None, valid_until=None, assigned_by=None):
    """ユーザーにロールを割り当て"""
    try:
        role = Role.objects.get(name=role_name, is_active=True)
        
        user_role_data = {
            'user': user,
            'role': role,
            'assigned_by': assigned_by,
            'valid_until': valid_until
        }
        
        if scope_object:
            user_role_data.update({
                'content_type': ContentType.objects.get_for_model(scope_object),
                'object_id': scope_object.id
            })
        
        user_role, created = UserRole.objects.get_or_create(
            user=user,
            role=role,
            content_type=user_role_data.get('content_type'),
            object_id=user_role_data.get('object_id'),
            defaults=user_role_data
        )
        
        return user_role, created
        
    except Role.DoesNotExist:
        raise ValueError(f"Role '{role_name}' does not exist")

# 権限チェック関数
def check_permission(user, permission_name, resource=None, scope_object=None, **context):
    """汎用権限チェック関数"""
    return user.has_permission(
        permission_name=permission_name,
        resource=resource,
        scope_object=scope_object,
        context=context
    )
```

**Bad: 簡単すぎる権限管理**
```python
# 悪い例：簡単すぎる権限システム

# 悪い: ハードコードされた権限チェック
def check_admin(user):
    return user.username in ['admin', 'root']  # 危険！

def check_author(user, post):
    return user.id == post.author_id  # 簡単すぎる

# 悪い: ロールなし、細かい制御なし
@login_required
def delete_post_bad(request, post_id):
    if not check_admin(request.user):  # 悪い: 柔軟性なし
        return JsonResponse({'error': 'Admin only'}, status=403)
    
    # 悪い: ログなし、監査記録なし
    Post.objects.filter(id=post_id).delete()
    return JsonResponse({'message': 'Deleted'})

# 悪い: 条件付き権限なし
# 悪い: 権限継承なし
# 悪い: スコープ制限なし
```

---

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