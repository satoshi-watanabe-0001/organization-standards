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

