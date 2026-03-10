# RESTful HTTPメソッド

**このドキュメントについて**: API設計標準 - RESTful HTTPメソッド

---

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

