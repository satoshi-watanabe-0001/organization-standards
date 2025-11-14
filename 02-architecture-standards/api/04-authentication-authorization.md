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

