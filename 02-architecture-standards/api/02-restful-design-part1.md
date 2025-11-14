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

