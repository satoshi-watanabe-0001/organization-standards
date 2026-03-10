# API設計 - AI向けクイックリファレンス

**目的**: AIがコード生成・レビュー時に参照すべき重要ポイントをまとめたサマリー

**バージョン**: 1.0.0  
**最終更新**: 2025-11-13

---

## 🎯 このドキュメントの使い方

このドキュメントは、詳細な標準文書のエッセンスを凝縮したものです。

**AIエージェント向け**:
- コード生成時: このファイルを最初に読み込む
- コードレビュー時: チェックリストとして使用
- 詳細確認: 各項目のリンクから詳細ドキュメントへ

**人間向け**:
- クイックチェック: レビュー時の確認項目
- 学習教材: 重要ポイントの把握
- オンボーディング: 新メンバーへの基礎教育

---

## ✅ 必須チェック項目 TOP 30

### A. API設計の基本 (1-10)

#### 1. ✓ URL設計: 名詞を使う（動詞を使わない）
```
✅ 正: GET /users
✅ 正: POST /users
✅ 正: GET /users/{id}
❌ 誤: GET /getUsers
❌ 誤: POST /createUser
❌ 誤: POST /user/create
```

#### 2. ✓ リソース名: 複数形を使う
```
✅ 正: /users, /posts, /comments
❌ 誤: /user, /post, /comment
```

#### 3. ✓ HTTPメソッドの正しい使用
```
GET    - 取得（副作用なし、冪等）
POST   - 作成（副作用あり、非冪等）
PUT    - 完全更新・作成（冪等）
PATCH  - 部分更新（冪等）
DELETE - 削除（冪等）
```

#### 4. ✓ 適切なHTTPステータスコードの使用
```
200 OK              - 成功（GET, PUT, PATCH）
201 Created         - 作成成功（POST）
204 No Content      - 成功（DELETE, 返すデータなし）
400 Bad Request     - リクエストエラー
401 Unauthorized    - 認証エラー
403 Forbidden       - 認可エラー
404 Not Found       - リソース未発見
409 Conflict        - 競合エラー
422 Unprocessable Entity - バリデーションエラー
500 Internal Server Error - サーバーエラー
```

#### 5. ✓ 一貫したレスポンス構造
```json
// 成功レスポンス
{
  "data": { ... },
  "meta": {
    "timestamp": "2025-11-13T08:00:00Z",
    "version": "1.0"
  }
}

// エラーレスポンス
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": [
      {
        "field": "email",
        "message": "Invalid email format"
      }
    ]
  },
  "meta": {
    "timestamp": "2025-11-13T08:00:00Z",
    "request_id": "req_123456"
  }
}
```

#### 6. ✓ バージョニングの実装
```
✅ 正: /v1/users
✅ 正: Accept: application/vnd.api.v1+json
❌ 誤: バージョニングなし
```

#### 7. ✓ ページネーションの実装
```
// カーソルベース（推奨）
GET /users?cursor=abc123&limit=20

// オフセットベース
GET /users?offset=0&limit=20

// レスポンス
{
  "data": [...],
  "pagination": {
    "cursor": "xyz789",
    "has_more": true,
    "total": 1000
  }
}
```

#### 8. ✓ フィルタリング・検索
```
GET /users?status=active
GET /users?created_after=2025-01-01
GET /users?search=john
GET /users?sort=-created_at  // マイナスで降順
```

#### 9. ✓ ネストリソースの適切な深さ
```
✅ 正: /users/{id}/posts           // 1階層
✅ 正: /users/{id}/posts/{id}      // 2階層
⚠️ 注意: /users/{id}/posts/{id}/comments/{id}  // 3階層以上は避ける

// 深いネストが必要な場合はクエリパラメータ使用
✅ 正: /comments?user_id=123&post_id=456
```

#### 10. ✓ IDempotency（冪等性）の保証
```
// 冪等なメソッド: GET, PUT, PATCH, DELETE
// 何度実行しても同じ結果

// 非冪等なメソッド: POST
// Idempotency-Keyヘッダーで冪等性確保
POST /payments
Idempotency-Key: unique_key_123
```

### B. セキュリティ (11-20)

#### 11. ✓ 認証の必須実装
```
✅ すべてのエンドポイントに認証
❌ 認証なしのエンドポイント（公開API以外）

// 推奨: Bearer Token
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

#### 12. ✓ JWT トークンの適切な検証
```python
# 必須検証項目
- 署名の検証
- 有効期限（exp）の確認
- 発行者（iss）の確認
- 対象者（aud）の確認
- トークンの取り消し確認
```

#### 13. ✓ ロールベースアクセス制御 (RBAC)
```python
# リソースごとに権限チェック
@require_permission("users:read")
def get_user(user_id):
    ...

@require_permission("users:write")
def update_user(user_id):
    ...
```

#### 14. ✓ レート制限の実装
```
# レスポンスヘッダー
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1699876543

# 超過時
HTTP/1.1 429 Too Many Requests
Retry-After: 3600
```

#### 15. ✓ HTTPS の必須使用
```
✅ https://api.example.com
❌ http://api.example.com

# HTTPリクエストは自動的にHTTPSへリダイレクト
```

#### 16. ✓ セキュリティヘッダーの設定
```
Strict-Transport-Security: max-age=31536000
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
Content-Security-Policy: default-src 'self'
X-XSS-Protection: 1; mode=block
```

#### 17. ✓ 入力バリデーションの徹底
```python
# すべての入力をバリデーション
- データ型チェック
- 長さチェック
- 形式チェック（email, URLなど）
- ホワイトリスト方式
```

#### 18. ✓ SQLインジェクション対策
```python
# ✅ パラメータ化クエリ
cursor.execute("SELECT * FROM users WHERE id = ?", (user_id,))

# ❌ 文字列結合
cursor.execute(f"SELECT * FROM users WHERE id = {user_id}")
```

#### 19. ✓ 機密情報の適切な扱い
```
✅ 環境変数に格納
✅ シークレット管理サービス使用
❌ コードにハードコード
❌ GitリポジトリにCommit
❌ ログに出力
```

#### 20. ✓ エラーメッセージの適切な制御
```json
// ✅ 本番環境: 詳細を隠す
{
  "error": {
    "code": "INTERNAL_ERROR",
    "message": "An error occurred"
  }
}

// ❌ 本番環境: スタックトレース露出
{
  "error": {
    "message": "Database connection failed",
    "stack": "Traceback (most recent call last)..."
  }
}
```

### C. パフォーマンス・スケーラビリティ (21-25)

#### 21. ✓ キャッシングの実装
```
# レスポンスヘッダー
Cache-Control: public, max-age=3600
ETag: "abc123"

# 条件付きリクエスト
If-None-Match: "abc123"
→ 304 Not Modified
```

#### 22. ✓ 圧縮の有効化
```
# リクエスト
Accept-Encoding: gzip, deflate

# レスポンス
Content-Encoding: gzip
```

#### 23. ✓ N+1問題の回避
```python
# ❌ N+1問題
users = User.query.all()
for user in users:
    user.posts  # 各ユーザーごとにクエリ実行

# ✅ Eager Loading
users = User.query.options(joinedload(User.posts)).all()
```

#### 24. ✓ バルク操作のサポート
```
# 単一操作
POST /users        { "name": "John" }

# バルク操作
POST /users/batch  [{ "name": "John" }, { "name": "Jane" }]
```

#### 25. ✓ 非同期処理の活用
```python
# 重い処理は非同期化
POST /reports
→ 202 Accepted
{
  "job_id": "job_123",
  "status_url": "/jobs/job_123"
}

# ステータス確認
GET /jobs/job_123
→ 200 OK
{
  "status": "processing",
  "progress": 45
}
```

### D. ドキュメンテーション (26-30)

#### 26. ✓ OpenAPI仕様書の作成
```yaml
openapi: 3.0.3
info:
  title: User API
  version: 1.0.0
paths:
  /users:
    get:
      summary: List users
      responses:
        '200':
          description: Success
```

#### 27. ✓ 例示の充実
```yaml
# リクエスト例
example:
  name: "John Doe"
  email: "john@example.com"

# レスポンス例
examples:
  success:
    value:
      id: 123
      name: "John Doe"
```

#### 28. ✓ エラーコードのドキュメント化
```markdown
## エラーコード一覧
- `VALIDATION_ERROR`: 入力バリデーションエラー
- `AUTH_REQUIRED`: 認証が必要
- `PERMISSION_DENIED`: 権限不足
- `RESOURCE_NOT_FOUND`: リソースが見つからない
```

#### 29. ✓ 変更履歴の記録
```markdown
## Changelog
### v1.1.0 (2025-11-13)
- Added: User profile endpoint
- Changed: Pagination default limit to 20
- Deprecated: /v0/users endpoint
```

#### 30. ✓ サンプルコードの提供
```python
# Python例
import requests

response = requests.get(
    'https://api.example.com/v1/users',
    headers={'Authorization': 'Bearer YOUR_TOKEN'}
)
```

---

## 🚫 よくある間違いと修正方法

### 間違い1: 動詞を含むURL設計

❌ **悪い例**:
```
GET  /getUser?id=123
POST /createUser
POST /user/delete
```

✅ **良い例**:
```
GET    /users/123
POST   /users
DELETE /users/123
```

**理由**: RESTful設計では、URLはリソースを表し、HTTPメソッドがアクションを表す。

---

### 間違い2: 不適切なHTTPステータスコード

❌ **悪い例**:
```python
# すべて200を返す
@app.post("/users")
def create_user():
    try:
        # ユーザー作成
        return {"status": "success"}, 200
    except ValidationError:
        return {"status": "error"}, 200  # ❌
```

✅ **良い例**:
```python
@app.post("/users")
def create_user():
    try:
        user = create_user_in_db()
        return user, 201  # Created
    except ValidationError as e:
        return {"error": str(e)}, 422  # Unprocessable Entity
```

---

### 間違い3: バージョニングなしで破壊的変更

❌ **悪い例**:
```python
# v1でリリース
GET /users → {"id": 1, "name": "John"}

# 後で破壊的変更
GET /users → {"user_id": 1, "full_name": "John"}  # ❌
```

✅ **良い例**:
```python
# v1を維持
GET /v1/users → {"id": 1, "name": "John"}

# v2で新フォーマット
GET /v2/users → {"user_id": 1, "full_name": "John"}
```

---

### 間違い4: 認証なしのエンドポイント

❌ **悪い例**:
```python
@app.get("/users/{user_id}")
def get_user(user_id: int):
    # 認証チェックなし ❌
    return get_user_from_db(user_id)
```

✅ **良い例**:
```python
@app.get("/users/{user_id}")
@require_authentication  # デコレーターで認証強制
def get_user(user_id: int, current_user: User):
    if not current_user.can_view_user(user_id):
        raise PermissionDenied
    return get_user_from_db(user_id)
```

---

### 間違い5: エラーレスポンスの不一致

❌ **悪い例**:
```python
# エンドポイントごとに異なるエラー形式
return {"error": "Not found"}
return {"message": "Invalid input"}
return {"err_msg": "Server error", "code": 500}
```

✅ **良い例**:
```python
# 統一されたエラー形式
{
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "User not found",
    "details": {
      "user_id": 123
    }
  }
}
```

---

## 🔒 セキュリティチェックリスト

コードレビュー時に以下を確認:

### 認証・認可
- [ ] すべてのエンドポイントに認証が実装されている
- [ ] JWTトークンの署名検証が行われている
- [ ] トークンの有効期限チェックが実装されている
- [ ] リソースレベルの認可チェックが実装されている
- [ ] ロールベースのアクセス制御がある

### 入力検証
- [ ] すべての入力パラメータがバリデーションされている
- [ ] データ型が検証されている
- [ ] 長さ制限が適用されている
- [ ] ホワイトリスト方式でバリデーションされている
- [ ] SQLインジェクション対策がされている

### データ保護
- [ ] 機密情報がログに出力されていない
- [ ] パスワードがハッシュ化されている
- [ ] APIキーが環境変数で管理されている
- [ ] HTTPSが必須になっている
- [ ] センシティブデータが暗号化されている

### レート制限
- [ ] レート制限が実装されている
- [ ] レート制限ヘッダーが返されている
- [ ] 429ステータスコードが正しく返される
- [ ] ユーザー/IPごとに制限が適用されている

### エラーハンドリング
- [ ] 本番環境でスタックトレースが露出していない
- [ ] 詳細なエラー情報が隠蔽されている
- [ ] エラーログが適切に記録されている
- [ ] ユーザーフレンドリーなエラーメッセージ

---

## 📊 パフォーマンスチェックリスト

### データベース
- [ ] N+1問題が解決されている（Eager Loading使用）
- [ ] 適切なインデックスが設定されている
- [ ] クエリが最適化されている
- [ ] コネクションプーリングが使用されている

### キャッシング
- [ ] 頻繁にアクセスされるデータがキャッシュされている
- [ ] Cache-Controlヘッダーが適切に設定されている
- [ ] ETagが実装されている
- [ ] 条件付きリクエストがサポートされている

### ペイロード
- [ ] レスポンスサイズが最小化されている
- [ ] 不要なフィールドが除外されている
- [ ] 圧縮が有効化されている（gzip）
- [ ] ページネーションが実装されている

### 非同期処理
- [ ] 重い処理が非同期化されている
- [ ] ジョブキューが使用されている
- [ ] タイムアウトが設定されている
- [ ] ステータス確認エンドポイントがある

---

## 🔗 詳細ドキュメントへのリンク

各項目の詳細は以下を参照:

### 設計原則
- [01-design-principles.md](./01-design-principles.md) - API設計の哲学と原理

### RESTful設計
- [02-restful-design-part1.md](./02-restful-design-part1.md) - RESTful基礎
- [03a-rest-resource-design.md](./03a-rest-resource-design.md) - リソース設計
- [03b-rest-http-methods.md](./03b-rest-http-methods.md) - HTTPメソッド
- [03c-rest-status-codes.md](./03c-rest-status-codes.md) - ステータスコード
- [03d-rest-query-params.md](./03d-rest-query-params.md) - クエリパラメータ
- [03e-rest-response-format.md](./03e-rest-response-format.md) - レスポンス形式

### セキュリティ
- [04-authentication-authorization.md](./04-authentication-authorization.md) - 認証・認可

### ドキュメント
- [05-api-documentation.md](./05-api-documentation.md) - APIドキュメンテーション

---

## 📝 更新履歴

| 日付 | バージョン | 変更内容 |
|------|-----------|---------|
| 2025-11-13 | 1.0.0 | 初版作成 |

---

**このドキュメントについて**: このクイックリファレンスは、AI・人間の両方が効率的にAPI設計標準を活用できるように作成されました。詳細な実装例やコンテキストが必要な場合は、各トピックの詳細ドキュメントを参照してください。
