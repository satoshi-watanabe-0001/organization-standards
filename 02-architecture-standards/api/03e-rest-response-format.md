# RESTful レスポンスフォーマット

**このドキュメントについて**: API設計標準 - RESTful レスポンスフォーマット

---

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

