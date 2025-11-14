## 7. セキュリティ・認証・認可

### 7.1 セキュリティベストプラクティス

#### 7.1.1 入力検証・サニタイゼーション

**Good: 包括的入力検証**
```python
# セキュアな入力検証実装
import re
import html
from typing import Optional, Dict, Any
from pydantic import BaseModel, validator, Field
from sqlalchemy.orm import Session
from passlib.context import CryptContext
from jose import JWTError, jwt
from datetime import datetime, timedelta
from cryptography.fernet import Fernet
import secrets
import hashlib
import bleach
from urllib.parse import urlparse

class SecureInputValidator:
    """セキュアな入力検証クラス"""
    
    # セキュリティパターン定義
    SQL_INJECTION_PATTERNS = [
        r"(union|select|insert|update|delete|drop)\s+",
        r"['\"];.*--",
        r"\b(or|and)\s+['\"]?\d+['\"]?\s*=\s*['\"]?\d+['\"]?"
    ]
    
    XSS_PATTERNS = [
        r"<script[^>]*>.*?</script>",
        r"javascript:\s*",
        r"on\w+\s*=\s*['\"].*?['\"]"    
    ]
    
    @classmethod
    def validate_sql_injection(cls, value: str) -> bool:
        """SQLインジェクション検証"""
        if not isinstance(value, str):
            return True
            
        value_lower = value.lower()
        for pattern in cls.SQL_INJECTION_PATTERNS:
            if re.search(pattern, value_lower, re.IGNORECASE):
                return False
        return True
    
    @classmethod
    def validate_xss(cls, value: str) -> bool:
        """XSS攻撃検証"""
        if not isinstance(value, str):
            return True
            
        for pattern in cls.XSS_PATTERNS:
            if re.search(pattern, value, re.IGNORECASE):
                return False
        return True
    
    @classmethod
    def sanitize_html(cls, value: str) -> str:
        """HTMLサニタイゼーション"""
        if not isinstance(value, str):
            return value
            
        # 許可するHTMLタグとアトリビュート
        allowed_tags = ['p', 'br', 'strong', 'em', 'ul', 'ol', 'li']
        allowed_attributes = {}
        
        return bleach.clean(value, tags=allowed_tags, attributes=allowed_attributes, strip=True)
    
    @classmethod
    def validate_file_upload(cls, filename: str, content: bytes) -> Dict[str, Any]:
        """ファイルアップロード検証"""
        result = {
            "valid": True,
            "errors": [],
            "sanitized_filename": filename
        }
        
        # ファイル名検証
        if not filename or '..' in filename or '/' in filename:
            result["valid"] = False
            result["errors"].append("Invalid filename")
            return result
        
        # 拡張子検証
        allowed_extensions = {'.jpg', '.jpeg', '.png', '.gif', '.pdf', '.txt', '.csv'}
        file_ext = filename.lower().split('.')[-1] if '.' in filename else ''
        if f'.{file_ext}' not in allowed_extensions:
            result["valid"] = False
            result["errors"].append(f"Extension {file_ext} not allowed")
        
        # ファイルサイズ検証
        max_size = 10 * 1024 * 1024  # 10MB
        if len(content) > max_size:
            result["valid"] = False
            result["errors"].append("File too large")
        
        # MIMEタイプ検証（基本的なmagic number確認）
        mime_signatures = {
            b'\xff\xd8\xff': 'image/jpeg',
            b'\x89PNG\r\n\x1a\n': 'image/png',
            b'GIF8': 'image/gif',
            b'%PDF': 'application/pdf'
        }
        
        detected_mime = None
        for signature, mime_type in mime_signatures.items():
            if content.startswith(signature):
                detected_mime = mime_type
                break
        
        if detected_mime is None and file_ext in ['jpg', 'jpeg', 'png', 'gif', 'pdf']:
            result["valid"] = False
            result["errors"].append("File content doesn't match extension")
        
        # ファイル名サニタイゼーション
        sanitized = re.sub(r'[^a-zA-Z0-9._-]', '_', filename)
        result["sanitized_filename"] = sanitized
        
        return result

# Pydanticセキュアモデル
class SecureUserInput(BaseModel):
    """セキュアなユーザー入力モデル"""
    
    username: str = Field(..., min_length=3, max_length=50)
    email: str = Field(..., regex=r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$')
    comment: Optional[str] = Field(None, max_length=1000)
    url: Optional[str] = Field(None, max_length=500)
    
    @validator('username')
    def validate_username(cls, v):
        """ユーザー名セキュリティ検証"""
        if not SecureInputValidator.validate_sql_injection(v):
            raise ValueError("Username contains potential SQL injection")
        if not SecureInputValidator.validate_xss(v):
            raise ValueError("Username contains potential XSS")
        # 英数字とアンダースコアのみ許可
        if not re.match(r'^[a-zA-Z0-9_]+$', v):
            raise ValueError("Username must contain only alphanumeric characters and underscores")
        return v
    
    @validator('comment')
    def validate_comment(cls, v):
        """コメントセキュリティ検証"""
        if v is None:
            return v
        if not SecureInputValidator.validate_sql_injection(v):
            raise ValueError("Comment contains potential SQL injection")
        return SecureInputValidator.sanitize_html(v)
    
    @validator('url')
    def validate_url(cls, v):
        """URL検証"""
        if v is None:
            return v
        
        try:
            parsed = urlparse(v)
            if parsed.scheme not in ['http', 'https']:
                raise ValueError("Only HTTP and HTTPS URLs are allowed")
            if parsed.hostname in ['localhost', '127.0.0.1'] or parsed.hostname.startswith('192.168.'):
                raise ValueError("Local URLs are not allowed")
        except Exception:
            raise ValueError("Invalid URL format")
        
        return v
```

**Bad: 不十分な入力検証**
```python
# セキュリティ脆弱性のある実装例
from fastapi import FastAPI, UploadFile
from pydantic import BaseModel

app = FastAPI()

# 危険: 入力検証なし
class UnsafeUserInput(BaseModel):
    username: str  # 長さ制限なし、特殊文字許可
    comment: str   # HTMLエスケープなし
    sql_query: str # SQLインジェクション対策なし

@app.post("/unsafe-endpoint")
async def unsafe_endpoint(user_input: UnsafeUserInput):
    # 危険: 生のSQL実行
    query = f"SELECT * FROM users WHERE name = '{user_input.username}'"
    # SQLインジェクション脆弱性
    
    # 危険: HTMLエスケープなし
    html_output = f"<div>{user_input.comment}</div>"
    # XSS脆弱性
    
    return {"message": html_output}

@app.post("/unsafe-upload")
async def unsafe_upload(file: UploadFile):
    # 危険: ファイル検証なし
    content = await file.read()
    
    # ファイル名サニタイゼーションなし
    # 拡張子制限なし
    # サイズ制限なし
    # MIMEタイプ検証なし
    
    with open(f"/uploads/{file.filename}", "wb") as f:
        f.write(content)  # パストラバーサル脆弱性
    
    return {"message": "File uploaded"}
```

#### 7.1.2 暗号化・ハッシュ化

**Good: セキュアな暗号化実装**
```python
# セキュアな暗号化・ハッシュ化実装
import os
import base64
import hashlib
import secrets
from typing import Tuple, Optional
from cryptography.fernet import Fernet
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from passlib.context import CryptContext
from passlib.hash import bcrypt

class SecureCrypto:
    """セキュアな暗号化クラス"""
    
    def __init__(self):
        # パスワードハッシュ設定
        self.pwd_context = CryptContext(
            schemes=["bcrypt"],
            deprecated="auto",
            bcrypt__rounds=12  # セキュアなラウンド数
        )
    
    def generate_salt(self, length: int = 32) -> bytes:
        """セキュアなソルト生成"""
        return secrets.token_bytes(length)
    
    def hash_password(self, password: str) -> str:
        """パスワードハッシュ化"""
        return self.pwd_context.hash(password)
    
    def verify_password(self, plain_password: str, hashed_password: str) -> bool:
        """パスワード検証"""
        return self.pwd_context.verify(plain_password, hashed_password)
    
    def generate_key_from_password(self, password: str, salt: bytes) -> bytes:
        """パスワードからキー生成"""
        kdf = PBKDF2HMAC(
            algorithm=hashes.SHA256(),
            length=32,
            salt=salt,
            iterations=100000,  # セキュアな反復回数
        )
        return base64.urlsafe_b64encode(kdf.derive(password.encode()))
    
    def encrypt_data(self, data: str, key: bytes) -> str:
        """データ暗号化"""
        f = Fernet(key)
        encrypted_data = f.encrypt(data.encode())
        return base64.urlsafe_b64encode(encrypted_data).decode()
    
    def decrypt_data(self, encrypted_data: str, key: bytes) -> str:
        """データ復号化"""
        try:
            f = Fernet(key)
            decoded_data = base64.urlsafe_b64decode(encrypted_data.encode())
            decrypted_data = f.decrypt(decoded_data)
            return decrypted_data.decode()
        except Exception as e:
            raise ValueError(f"Decryption failed: {str(e)}")
    
    def generate_secure_token(self, length: int = 32) -> str:
        """セキュアなトークン生成"""
        return secrets.token_urlsafe(length)
    
    def hash_sensitive_data(self, data: str) -> str:
        """機密データハッシュ化（非可逆）"""
        # SHA-256 + ソルト
        salt = secrets.token_bytes(32)
        hash_obj = hashlib.pbkdf2_hmac('sha256', data.encode(), salt, 100000)
        return base64.urlsafe_b64encode(salt + hash_obj).decode()
    
    def verify_sensitive_data(self, data: str, hashed_data: str) -> bool:
        """機密データハッシュ検証"""
        try:
            decoded = base64.urlsafe_b64decode(hashed_data.encode())
            salt = decoded[:32]
            stored_hash = decoded[32:]
            new_hash = hashlib.pbkdf2_hmac('sha256', data.encode(), salt, 100000)
            return secrets.compare_digest(stored_hash, new_hash)
        except Exception:
            return False

# 実用例
class SecureUserService:
    """セキュアなユーザーサービス"""
    
    def __init__(self):
        self.crypto = SecureCrypto()
    
    def create_user(self, username: str, password: str, email: str) -> dict:
        """セキュアなユーザー作成"""
        # パスワード強度チェック
        if not self._validate_password_strength(password):
            raise ValueError("Password does not meet security requirements")
        
        # パスワードハッシュ化
        hashed_password = self.crypto.hash_password(password)
        
        # メールアドレスハッシュ化（プライバシー保護）
        hashed_email = self.crypto.hash_sensitive_data(email)
        
        # セッショントークン生成
        session_token = self.crypto.generate_secure_token()
        
        return {
            "username": username,
            "password_hash": hashed_password,
            "email_hash": hashed_email,
            "session_token": session_token,
            "created_at": datetime.utcnow()
        }
    
    def authenticate_user(self, username: str, password: str) -> Optional[dict]:
        """ユーザー認証"""
        # DB からユーザー情報取得（実装例）
        user = self._get_user_by_username(username)
        if not user:
            # タイミング攻撃対策：存在しない場合もハッシュ計算実行
            self.crypto.verify_password(password, "$2b$12$dummy.hash.to.prevent.timing.attacks")
            return None
        
        # パスワード検証
        if not self.crypto.verify_password(password, user["password_hash"]):
            return None
        
        # 新しいセッショントークン生成
        user["session_token"] = self.crypto.generate_secure_token()
        return user
    
    def _validate_password_strength(self, password: str) -> bool:
        """パスワード強度検証"""
        if len(password) < 8:
            return False
        
        # 複雑性要件
        has_lower = any(c.islower() for c in password)
        has_upper = any(c.isupper() for c in password)
        has_digit = any(c.isdigit() for c in password)
        has_special = any(c in "!@#$%^&*(),.?\":{}|<>" for c in password)
        
        return sum([has_lower, has_upper, has_digit, has_special]) >= 3
    
    def _get_user_by_username(self, username: str) -> Optional[dict]:
        """ユーザー取得（実装例）"""
        # 実際のDB実装に置き換え
        pass
```

**Bad: 脆弱な暗号化実装**
```python
# 脆弱な暗号化実装例
import hashlib
import base64
from Crypto.Cipher import AES  # 古いライブラリ使用

class InsecureCrypto:
    """脆弱な暗号化実装（使用禁止）"""
    
    def hash_password(self, password: str) -> str:
        # 危険: ソルトなしMD5ハッシュ
        return hashlib.md5(password.encode()).hexdigest()
    
    def verify_password(self, password: str, hashed: str) -> bool:
        # 危険: タイミング攻撃脆弱性
        return self.hash_password(password) == hashed
    
    def encrypt_data(self, data: str, key: str) -> str:
        # 危険: 固定IV、ECBモード
        cipher = AES.new(key.encode()[:16], AES.MODE_ECB)
        padded_data = data + ' ' * (16 - len(data) % 16)
        encrypted = cipher.encrypt(padded_data.encode())
        return base64.b64encode(encrypted).decode()
    
    def generate_token(self) -> str:
        # 危険: 予測可能な乱数
        import random
        import time
        random.seed(int(time.time()))
        return str(random.randint(100000, 999999))
```

### 7.2 認証システム実装

#### 7.2.1 JWTトークン認証

**Good: セキュアなJWT実装**
```python
# JWTセキュア実装
from datetime import datetime, timedelta
from typing import Optional, Dict, Any
from jose import JWTError, jwt
from passlib.context import CryptContext
from fastapi import HTTPException, status, Depends
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
import secrets
import os
from enum import Enum

class TokenType(Enum):
    """JWTトークンタイプ"""
    ACCESS = "access"
    REFRESH = "refresh"
    RESET_PASSWORD = "reset_password"
    EMAIL_VERIFICATION = "email_verification"

class JWTManager:
    """セキュアなJWT管理クラス"""
    
    def __init__(self):
        # シークレットキーは環境変数から取得
        self.secret_key = os.getenv("JWT_SECRET_KEY")
        if not self.secret_key:
            raise ValueError("JWT_SECRET_KEY environment variable is required")
        
        self.algorithm = "HS256"
        self.access_token_expire_minutes = 30
        self.refresh_token_expire_days = 7
        self.reset_token_expire_minutes = 15
        
        # パスワードハッシュコンテキスト
        self.pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
        
        # トークンブラックリスト（実際は Redis 等使用）
        self.blacklisted_tokens = set()
    
    def create_access_token(self, user_id: int, username: str, roles: list = None) -> str:
        """アクセストークン生成"""
        expire = datetime.utcnow() + timedelta(minutes=self.access_token_expire_minutes)
        
        payload = {
            "sub": str(user_id),
            "username": username,
            "roles": roles or [],
            "type": TokenType.ACCESS.value,
            "exp": expire,
            "iat": datetime.utcnow(),
            "jti": secrets.token_urlsafe(32)  # ユニークID
        }
        
        return jwt.encode(payload, self.secret_key, algorithm=self.algorithm)
    
    def create_refresh_token(self, user_id: int) -> str:
        """リフレッシュトークン生成"""
        expire = datetime.utcnow() + timedelta(days=self.refresh_token_expire_days)
        
        payload = {
            "sub": str(user_id),
            "type": TokenType.REFRESH.value,
            "exp": expire,
            "iat": datetime.utcnow(),
            "jti": secrets.token_urlsafe(32)
        }
        
        return jwt.encode(payload, self.secret_key, algorithm=self.algorithm)
    
    def create_reset_token(self, user_id: int, email: str) -> str:
        """パスワードリセットトークン生成"""
        expire = datetime.utcnow() + timedelta(minutes=self.reset_token_expire_minutes)
        
        payload = {
            "sub": str(user_id),
            "email": email,
            "type": TokenType.RESET_PASSWORD.value,
            "exp": expire,
            "iat": datetime.utcnow(),
            "jti": secrets.token_urlsafe(32)
        }
        
        return jwt.encode(payload, self.secret_key, algorithm=self.algorithm)
    
    def verify_token(self, token: str, expected_type: TokenType = TokenType.ACCESS) -> Dict[str, Any]:
        """トークン検証"""
        try:
            # ブラックリストチェック
            if token in self.blacklisted_tokens:
                raise HTTPException(
                    status_code=status.HTTP_401_UNAUTHORIZED,
                    detail="Token has been revoked"
                )
            
            # トークンデコード
            payload = jwt.decode(token, self.secret_key, algorithms=[self.algorithm])
            
            # トークンタイプ検証
            if payload.get("type") != expected_type.value:
                raise HTTPException(
                    status_code=status.HTTP_401_UNAUTHORIZED,
                    detail="Invalid token type"
                )
            
            # 必須フィールド確認
            required_fields = ["sub", "exp", "iat", "jti"]
            for field in required_fields:
                if field not in payload:
                    raise HTTPException(
                        status_code=status.HTTP_401_UNAUTHORIZED,
                        detail=f"Missing required field: {field}"
                    )
            
            return payload
            
        except JWTError as e:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail=f"Token validation failed: {str(e)}"
            )
    
    def revoke_token(self, token: str) -> None:
        """トークン無効化"""
        try:
            payload = jwt.decode(token, self.secret_key, algorithms=[self.algorithm])
            jti = payload.get("jti")
            if jti:
                self.blacklisted_tokens.add(token)
        except JWTError:
            pass  # 無効なトークンは無視
    
    def refresh_access_token(self, refresh_token: str) -> Dict[str, str]:
        """アクセストークン更新"""
        payload = self.verify_token(refresh_token, TokenType.REFRESH)
        user_id = int(payload["sub"])
        
        # ユーザー情報取得（DBから）
        user = self._get_user_by_id(user_id)
        if not user or not user.get("is_active"):
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="User not found or inactive"
            )
        
        # 新しいアクセストークン生成
        new_access_token = self.create_access_token(
            user_id=user_id,
            username=user["username"],
            roles=user.get("roles", [])
        )
        
        return {
            "access_token": new_access_token,
            "token_type": "bearer"
        }
    
    def _get_user_by_id(self, user_id: int) -> Optional[dict]:
        """ユーザー取得（実装例）"""
        # 実際のDB実装に置き換え
        pass

# FastAPI セキュリティ依存性
class AuthManager:
    """認証管理クラス"""
    
    def __init__(self):
        self.jwt_manager = JWTManager()
        self.security = HTTPBearer(auto_error=False)
    
    async def get_current_user(self, credentials: Optional[HTTPAuthorizationCredentials] = Depends(HTTPBearer())) -> dict:
        """現在のユーザー取得"""
        if not credentials:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Authentication required"
            )
        
        payload = self.jwt_manager.verify_token(credentials.credentials)
        user_id = int(payload["sub"])
        
        # ユーザー情報取得
        user = self.jwt_manager._get_user_by_id(user_id)
        if not user:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="User not found"
            )
        
        return user
    
    async def get_optional_user(self, credentials: Optional[HTTPAuthorizationCredentials] = Depends(HTTPBearer(auto_error=False))) -> Optional[dict]:
        """オプショナルユーザー取得"""
        if not credentials:
            return None
        
        try:
            return await self.get_current_user(credentials)
        except HTTPException:
            return None
    
    def require_roles(self, required_roles: list):
        """必要ロールチェックデコレータ"""
        def role_checker(user: dict = Depends(self.get_current_user)):
            user_roles = user.get("roles", [])
            if not any(role in user_roles for role in required_roles):
                raise HTTPException(
                    status_code=status.HTTP_403_FORBIDDEN,
                    detail="Insufficient permissions"
                )
            return user
        return role_checker

# 使用例
auth_manager = AuthManager()

# FastAPI エンドポイント例
from fastapi import FastAPI, Depends

app = FastAPI()

@app.post("/login")
async def login(username: str, password: str):
    """ログインエンドポイント"""
    # ユーザー認証処理
    user = authenticate_user(username, password)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid credentials"
        )
    
    # トークン生成
    access_token = auth_manager.jwt_manager.create_access_token(
        user_id=user["id"],
        username=user["username"],
        roles=user.get("roles", [])
    )
    refresh_token = auth_manager.jwt_manager.create_refresh_token(user["id"])
    
    return {
        "access_token": access_token,
        "refresh_token": refresh_token,
        "token_type": "bearer"
    }

@app.get("/profile")
async def get_profile(current_user: dict = Depends(auth_manager.get_current_user)):
    """プロフィール取得（認証必要）"""
    return {"user": current_user}

@app.get("/admin/users")
async def get_all_users(admin_user: dict = Depends(auth_manager.require_roles(["admin"]))):
    """管理者専用エンドポイント"""
    return {"users": [], "requested_by": admin_user["username"]}
```

**Bad: 不安全なJWT実装**
```python
# 不安全なJWT実装例
from jose import jwt
from datetime import datetime, timedelta

class InsecureJWT:
    """不安全なJWT実装（使用禁止）"""
    
    def __init__(self):
        # 危険: ハードコードされたシークレット
        self.secret_key = "my-secret-key"
        self.algorithm = "HS256"
        # 危険: 長すぎる有効期限
        self.expire_hours = 24 * 30  # 30日
    
    def create_token(self, user_id: int) -> str:
        # 危険: 最小限のペイロードのみ
        expire = datetime.utcnow() + timedelta(hours=self.expire_hours)
        payload = {
            "user_id": user_id,  # フィールド名が非標準
            "exp": expire
            # 缶けている情報: iat, jti, タイプ等
        }
        return jwt.encode(payload, self.secret_key, algorithm=self.algorithm)
    
    def verify_token(self, token: str) -> dict:
        # 危険: エラーハンドリングなし
        payload = jwt.decode(token, self.secret_key, algorithms=[self.algorithm])
        return payload
        # 缶けている機能:
        # - ブラックリストチェック
        # - トークンタイプ検証
        # - フィールド検証
```

#### 7.2.2 OAuth2/OpenID Connect

**Good: OAuth2統合実装**
```python
# OAuth2 サービス統合
import httpx
import secrets
from typing import Dict, Optional, Any
from urllib.parse import urlencode, parse_qs
from fastapi import HTTPException, status
from datetime import datetime, timedelta
import hashlib
import base64

class OAuth2Provider:
    """汎用OAuth2プロバイダークラス"""
    
    def __init__(self, provider_name: str, config: Dict[str, str]):
        self.provider_name = provider_name
        self.client_id = config["client_id"]
        self.client_secret = config["client_secret"]
        self.redirect_uri = config["redirect_uri"]
        self.authorization_endpoint = config["authorization_endpoint"]
        self.token_endpoint = config["token_endpoint"]
        self.userinfo_endpoint = config.get("userinfo_endpoint")
        self.scope = config.get("scope", "openid profile email")
        
        # PKCE対応
        self.use_pkce = config.get("use_pkce", True)
        
        # ステート管理
        self.state_store = {}  # 実際はRedis等使用
    
    def generate_authorization_url(self, state: Optional[str] = None) -> Dict[str, str]:
        """認可 URL 生成"""
        if not state:
            state = secrets.token_urlsafe(32)
        
        # PKCE パラメータ生成
        code_verifier = None
        code_challenge = None
        
        if self.use_pkce:
            code_verifier = secrets.token_urlsafe(96)
            code_challenge = base64.urlsafe_b64encode(
                hashlib.sha256(code_verifier.encode()).digest()
            ).decode().rstrip('=')
            
            # state と code_verifier を保存
            self.state_store[state] = {
                "code_verifier": code_verifier,
                "created_at": datetime.utcnow(),
                "provider": self.provider_name
            }
        
        # 認可 URL パラメータ
        params = {
            "response_type": "code",
            "client_id": self.client_id,
            "redirect_uri": self.redirect_uri,
            "scope": self.scope,
            "state": state
        }
        
        if code_challenge:
            params.update({
                "code_challenge": code_challenge,
                "code_challenge_method": "S256"
            })
        
        authorization_url = f"{self.authorization_endpoint}?{urlencode(params)}"
        
        return {
            "authorization_url": authorization_url,
            "state": state
        }
    
    async def exchange_code_for_token(self, code: str, state: str) -> Dict[str, Any]:
        """認可コードをトークンに交換"""
        # state 検証
        if state not in self.state_store:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Invalid or expired state parameter"
            )
        
        state_data = self.state_store[state]
        
        # トークンリクエストパラメータ
        token_data = {
            "grant_type": "authorization_code",
            "code": code,
            "redirect_uri": self.redirect_uri,
            "client_id": self.client_id,
            "client_secret": self.client_secret
        }
        
        # PKCE パラメータ追加
        if self.use_pkce and "code_verifier" in state_data:
            token_data["code_verifier"] = state_data["code_verifier"]
        
        # トークンリクエスト
        async with httpx.AsyncClient() as client:
            response = await client.post(
                self.token_endpoint,
                data=token_data,
                headers={"Accept": "application/json"}
            )
        
        if response.status_code != 200:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"Token exchange failed: {response.text}"
            )
        
        token_response = response.json()
        
        # state クリーンアップ
        del self.state_store[state]
        
        return token_response
    
    async def get_user_info(self, access_token: str) -> Dict[str, Any]:
        """アクセストークンからユーザー情報取得"""
        if not self.userinfo_endpoint:
            raise ValueError("User info endpoint not configured")
        
        headers = {
            "Authorization": f"Bearer {access_token}",
            "Accept": "application/json"
        }
        
        async with httpx.AsyncClient() as client:
            response = await client.get(self.userinfo_endpoint, headers=headers)
        
        if response.status_code != 200:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Failed to fetch user info"
            )
        
        return response.json()

class OAuth2Manager:
    """複数OAuth2プロバイダー管理"""
    
    def __init__(self):
        self.providers = {}
        self._setup_providers()
    
    def _setup_providers(self):
        """プロバイダー設定"""
        # Google OAuth2
        google_config = {
            "client_id": os.getenv("GOOGLE_CLIENT_ID"),
            "client_secret": os.getenv("GOOGLE_CLIENT_SECRET"),
            "redirect_uri": os.getenv("GOOGLE_REDIRECT_URI"),
            "authorization_endpoint": "https://accounts.google.com/o/oauth2/auth",
            "token_endpoint": "https://oauth2.googleapis.com/token",
            "userinfo_endpoint": "https://www.googleapis.com/oauth2/v2/userinfo",
            "scope": "openid profile email"
        }
        
        # GitHub OAuth2
        github_config = {
            "client_id": os.getenv("GITHUB_CLIENT_ID"),
            "client_secret": os.getenv("GITHUB_CLIENT_SECRET"),
            "redirect_uri": os.getenv("GITHUB_REDIRECT_URI"),
            "authorization_endpoint": "https://github.com/login/oauth/authorize",
            "token_endpoint": "https://github.com/login/oauth/access_token",
            "userinfo_endpoint": "https://api.github.com/user",
            "scope": "user:email"
        }
        
        if google_config["client_id"]:
            self.providers["google"] = OAuth2Provider("google", google_config)
        
        if github_config["client_id"]:
            self.providers["github"] = OAuth2Provider("github", github_config)
    
    def get_provider(self, provider_name: str) -> OAuth2Provider:
        """プロバイダー取得"""
        if provider_name not in self.providers:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"Provider {provider_name} not supported"
            )
        
        return self.providers[provider_name]
    
    async def authenticate_with_provider(self, provider_name: str, code: str, state: str) -> Dict[str, Any]:
        """プロバイダー経由認証"""
        provider = self.get_provider(provider_name)
        
        # トークン取得
        token_response = await provider.exchange_code_for_token(code, state)
        
        # ユーザー情報取得
        user_info = await provider.get_user_info(token_response["access_token"])
        
        # ユーザーマッピング処理
        mapped_user = self._map_user_info(provider_name, user_info)
        
        return {
            "user": mapped_user,
            "provider": provider_name,
            "tokens": token_response
        }
    
    def _map_user_info(self, provider_name: str, user_info: Dict[str, Any]) -> Dict[str, Any]:
        """プロバイダー固有ユーザー情報を標準化"""
        if provider_name == "google":
            return {
                "external_id": user_info["id"],
                "email": user_info["email"],
                "name": user_info["name"],
                "picture": user_info.get("picture"),
                "verified_email": user_info.get("verified_email", False)
            }
        elif provider_name == "github":
            return {
                "external_id": str(user_info["id"]),
                "email": user_info.get("email"),
                "name": user_info.get("name") or user_info["login"],
                "picture": user_info.get("avatar_url"),
                "username": user_info["login"]
            }
        else:
            return user_info

# FastAPI エンドポイント例
oauth2_manager = OAuth2Manager()

@app.get("/auth/{provider}/login")
async def oauth_login(provider: str):
    """ソーシャルログイン開始"""
    oauth_provider = oauth2_manager.get_provider(provider)
    auth_data = oauth_provider.generate_authorization_url()
    
    return {
        "authorization_url": auth_data["authorization_url"],
        "state": auth_data["state"]
    }

@app.get("/auth/{provider}/callback")
async def oauth_callback(provider: str, code: str, state: str):
    """ソーシャルログインコールバック"""
    auth_result = await oauth2_manager.authenticate_with_provider(provider, code, state)
    
    # ユーザー登録・更新処理
    user = await create_or_update_user(auth_result["user"], provider)
    
    # ローカルJWTトークン生成
    access_token = auth_manager.jwt_manager.create_access_token(
        user_id=user["id"],
        username=user["username"],
        roles=user.get("roles", [])
    )
    
    return {
        "access_token": access_token,
        "token_type": "bearer",
        "user": user
    }
```

### 7.3 認可・アクセス制御

#### 7.3.1 ロールベースアクセス制御 (RBAC)

**Good: 柔軟なRBAC実装**
```python
# ロールベースアクセス制御実装
from enum import Enum
from typing import List, Dict, Set, Optional, Any
from abc import ABC, abstractmethod
from dataclasses import dataclass
from functools import wraps
from fastapi import HTTPException, status, Depends
from sqlalchemy.orm import Session

class Permission(Enum):
    """基本パーミッション定義"""
    # ユーザー管理
    USER_CREATE = "user:create"
    USER_READ = "user:read"
    USER_UPDATE = "user:update"
    USER_DELETE = "user:delete"
    
    # コンテンツ管理
    CONTENT_CREATE = "content:create"
    CONTENT_READ = "content:read"
    CONTENT_UPDATE = "content:update"
    CONTENT_DELETE = "content:delete"
    CONTENT_PUBLISH = "content:publish"
    
    # システム管理
    SYSTEM_ADMIN = "system:admin"
    SYSTEM_CONFIG = "system:config"
    SYSTEM_LOGS = "system:logs"
    
    # レポート
    REPORT_VIEW = "report:view"
    REPORT_EXPORT = "report:export"

@dataclass
class Role:
    """ロールデータクラス"""
    name: str
    permissions: Set[Permission]
    description: str = ""
    is_system_role: bool = False
    parent_roles: Set[str] = None
    
    def __post_init__(self):
        if self.parent_roles is None:
            self.parent_roles = set()

class RoleManager:
    """ロール管理クラス"""
    
    def __init__(self):
        self.roles: Dict[str, Role] = {}
        self._setup_default_roles()
    
    def _setup_default_roles(self):
        """デフォルトロール設定"""
        # ゲスト権限
        self.roles["guest"] = Role(
            name="guest",
            permissions={Permission.CONTENT_READ},
            description="ゲストユーザー",
            is_system_role=True
        )
        
        # 一般ユーザー
        self.roles["user"] = Role(
            name="user",
            permissions={
                Permission.CONTENT_READ,
                Permission.CONTENT_CREATE,
                Permission.USER_READ  # 自分の情報のみ
            },
            description="一般ユーザー",
            is_system_role=True,
            parent_roles={"guest"}
        )
        
        # 編集者
        self.roles["editor"] = Role(
            name="editor",
            permissions={
                Permission.CONTENT_CREATE,
                Permission.CONTENT_READ,
                Permission.CONTENT_UPDATE,
                Permission.CONTENT_DELETE
            },
            description="コンテンツ編集者",
            parent_roles={"user"}
        )
        
        # モデレーター
        self.roles["moderator"] = Role(
            name="moderator",
            permissions={
                Permission.CONTENT_PUBLISH,
                Permission.USER_READ,
                Permission.REPORT_VIEW
            },
            description="コンテンツモデレーター",
            parent_roles={"editor"}
        )
        
        # 管理者
        self.roles["admin"] = Role(
            name="admin",
            permissions={
                Permission.USER_CREATE,
                Permission.USER_UPDATE,
                Permission.USER_DELETE,
                Permission.SYSTEM_CONFIG,
                Permission.REPORT_EXPORT
            },
            description="システム管理者",
            parent_roles={"moderator"}
        )
        
        # スーパー管理者
        self.roles["superadmin"] = Role(
            name="superadmin",
            permissions={Permission.SYSTEM_ADMIN, Permission.SYSTEM_LOGS},
            description="スーパー管理者",
            is_system_role=True,
            parent_roles={"admin"}
        )
    
    def get_role(self, role_name: str) -> Optional[Role]:
        """ロール取得"""
        return self.roles.get(role_name)
    
    def add_role(self, role: Role) -> None:
        """カスタムロール追加"""
        if role.is_system_role:
            raise ValueError("Cannot modify system roles")
        self.roles[role.name] = role
    
    def get_effective_permissions(self, role_names: List[str]) -> Set[Permission]:
        """実効権限算出（継承考慮）"""
        effective_permissions = set()
        processed_roles = set()
        
        def collect_permissions(role_name: str):
            if role_name in processed_roles:
                return
            
            processed_roles.add(role_name)
            role = self.get_role(role_name)
            
            if role:
                effective_permissions.update(role.permissions)
                # 親ロールの権限も継承
                for parent_role in role.parent_roles:
                    collect_permissions(parent_role)
        
        for role_name in role_names:
            collect_permissions(role_name)
        
        return effective_permissions
    
    def has_permission(self, user_roles: List[str], required_permission: Permission) -> bool:
        """権限チェック"""
        effective_permissions = self.get_effective_permissions(user_roles)
        return required_permission in effective_permissions
    
    def validate_role_assignment(self, user_roles: List[str], target_roles: List[str]) -> bool:
        """ロール割り当て権限検証"""
        user_permissions = self.get_effective_permissions(user_roles)
        
        # ユーザー作成権限が必要
        if Permission.USER_CREATE not in user_permissions:
            return False
        
        # システムロールの割り当てはスーパー管理者のみ
        for role_name in target_roles:
            role = self.get_role(role_name)
            if role and role.is_system_role:
                if Permission.SYSTEM_ADMIN not in user_permissions:
                    return False
        
        return True

class PermissionChecker:
    """権限チェッククラス"""
    
    def __init__(self, role_manager: RoleManager):
        self.role_manager = role_manager
    
    def require_permission(self, permission: Permission):
        """権限チェックデコレータ"""
        def decorator(func):
            @wraps(func)
            async def wrapper(*args, **kwargs):
                # 現在のユーザー取得（FastAPIのDependencyから）
                current_user = kwargs.get('current_user')
                if not current_user:
                    raise HTTPException(
                        status_code=status.HTTP_401_UNAUTHORIZED,
                        detail="Authentication required"
                    )
                
                user_roles = current_user.get("roles", [])
                if not self.role_manager.has_permission(user_roles, permission):
                    raise HTTPException(
                        status_code=status.HTTP_403_FORBIDDEN,
                        detail=f"Permission {permission.value} required"
                    )
                
                return await func(*args, **kwargs)
            return wrapper
        return decorator
    
    def require_any_permission(self, permissions: List[Permission]):
        """いずれかの権限が必要"""
        def decorator(func):
            @wraps(func)
            async def wrapper(*args, **kwargs):
                current_user = kwargs.get('current_user')
                if not current_user:
                    raise HTTPException(
                        status_code=status.HTTP_401_UNAUTHORIZED,
                        detail="Authentication required"
                    )
                
                user_roles = current_user.get("roles", [])
                user_permissions = self.role_manager.get_effective_permissions(user_roles)
                
                if not any(perm in user_permissions for perm in permissions):
                    perm_names = [p.value for p in permissions]
                    raise HTTPException(
                        status_code=status.HTTP_403_FORBIDDEN,
                        detail=f"One of these permissions required: {perm_names}"
                    )
                
                return await func(*args, **kwargs)
            return wrapper
        return decorator

# リソースベースアクセス制御
class ResourceAccessChecker:
    """リソースレベルアクセス制御"""
    
    def __init__(self, role_manager: RoleManager):
        self.role_manager = role_manager
    
    def can_access_resource(self, user: dict, resource: dict, action: str) -> bool:
        """リソースアクセス権限チェック"""
        user_roles = user.get("roles", [])
        user_permissions = self.role_manager.get_effective_permissions(user_roles)
        
        # 基本権限チェック
        required_permission = self._get_required_permission(resource["type"], action)
        if required_permission not in user_permissions:
            return False
        
        # 所有者チェック
        if action in ["update", "delete"]:
            if resource.get("owner_id") == user.get("id"):
                return True
            
            # 管理者はすべてのリソースにアクセス可能
            if Permission.SYSTEM_ADMIN in user_permissions:
                return True
            
            return False
        
        return True
    
    def _get_required_permission(self, resource_type: str, action: str) -> Permission:
        """リソースタイプとアクションから必要権限算出"""
        permission_map = {
            "content": {
                "create": Permission.CONTENT_CREATE,
                "read": Permission.CONTENT_READ,
                "update": Permission.CONTENT_UPDATE,
                "delete": Permission.CONTENT_DELETE
            },
            "user": {
                "create": Permission.USER_CREATE,
                "read": Permission.USER_READ,
                "update": Permission.USER_UPDATE,
                "delete": Permission.USER_DELETE
            }
        }
        
        return permission_map.get(resource_type, {}).get(action, Permission.CONTENT_READ)

# 使用例
role_manager = RoleManager()
permission_checker = PermissionChecker(role_manager)
resource_checker = ResourceAccessChecker(role_manager)

# FastAPI エンドポイント例
from fastapi import FastAPI, Depends

app = FastAPI()

@app.post("/content")
@permission_checker.require_permission(Permission.CONTENT_CREATE)
async def create_content(content_data: dict, current_user: dict = Depends(auth_manager.get_current_user)):
    """コンテンツ作成（権限必要）"""
    content = {
        "id": 123,
        "title": content_data["title"],
        "owner_id": current_user["id"],
        "type": "content"
    }
    return content

@app.get("/admin/users")
@permission_checker.require_any_permission([Permission.USER_READ, Permission.SYSTEM_ADMIN])
async def list_users(current_user: dict = Depends(auth_manager.get_current_user)):
    """ユーザー一覧（複数権限のいずれか）"""
    return {"users": [], "requested_by": current_user["username"]}

@app.put("/content/{content_id}")
async def update_content(
    content_id: int,
    content_data: dict,
    current_user: dict = Depends(auth_manager.get_current_user)
):
    """コンテンツ更新（リソースレベルチェック）"""
    # リソース取得（DBから）
    content = get_content_by_id(content_id)
    if not content:
        raise HTTPException(status_code=404, detail="Content not found")
    
    # アクセス権限チェック
    if not resource_checker.can_access_resource(current_user, content, "update"):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Insufficient permissions to update this content"
        )
    
    # 更新処理
    updated_content = update_content_in_db(content_id, content_data)
    return updated_content
```

**Bad: 柔軟性のないアクセス制御**
```python
# 柔軟性のないアクセス制御例
from fastapi import HTTPException, status

# 危険: ハードコードされた権限チェック
def check_admin_only(user: dict):
    # 柔軟性なし: 管理者か否かのみ
    if user.get("role") != "admin":
        raise HTTPException(status_code=403, detail="Admin only")

@app.post("/content")
async def create_content_bad(content_data: dict, current_user: dict):
    # 問題: ハードコードされたロールチェック
    if current_user.get("role") not in ["admin", "editor"]:
        raise HTTPException(status_code=403, detail="Not authorized")
    
    # コンテンツ作成処理
    return {"message": "Content created"}

@app.put("/content/{content_id}")
async def update_content_bad(content_id: int, current_user: dict):
    # 問題: 所有者チェックなし、柔軟性なし
    if current_user.get("role") != "admin":
        raise HTTPException(status_code=403, detail="Admin required")
    
    # 更新処理（すべてのコンテンツを管理者だけが編集可能）
    return {"message": "Content updated"}
```

#### 7.3.2 コンテキストベースアクセス制御 (CBAC)

**Good: コンテキストアウェアアクセス制御**
```python
# コンテキストベースアクセス制御実装
from datetime import datetime, time
from typing import Dict, Any, List, Optional
from enum import Enum
from dataclasses import dataclass
import ipaddress

class AccessContext(Enum):
    """アクセスコンテキストタイプ"""
    TIME_BASED = "time_based"
    LOCATION_BASED = "location_based"
    DEVICE_BASED = "device_based"
    NETWORK_BASED = "network_based"
    RISK_BASED = "risk_based"

@dataclass
class AccessRequest:
    """アクセスリクエスト情報"""
    user_id: int
    resource_id: str
    action: str
    ip_address: str
    user_agent: str
    timestamp: datetime
    location: Optional[Dict[str, Any]] = None
    device_fingerprint: Optional[str] = None
    session_id: Optional[str] = None
    previous_activities: Optional[List[Dict]] = None

class ContextualAccessChecker:
    """コンテキストアウェアアクセス制御クラス"""
    
    def __init__(self):
        # アクセスポリシー設定
        self.access_policies = self._load_access_policies()
        
        # リスクスコア闾値
        self.risk_thresholds = {
            "low": 0.3,
            "medium": 0.6,
            "high": 0.8
        }
    
    def _load_access_policies(self) -> Dict[str, Any]:
        """アクセスポリシー設定読み込み"""
        return {
            "business_hours": {
                "start_time": time(9, 0),  # 9:00
                "end_time": time(18, 0),   # 18:00
                "weekdays": [0, 1, 2, 3, 4]  # 月-金
            },
            "allowed_networks": [
                "192.168.1.0/24",
                "10.0.0.0/8",
                "172.16.0.0/12"
            ],
            "blocked_countries": ["CN", "RU", "KP"],
            "sensitive_resources": {
                "financial_data": {
                    "required_contexts": [AccessContext.NETWORK_BASED, AccessContext.TIME_BASED],
                    "max_risk_score": 0.4
                },
                "user_data": {
                    "required_contexts": [AccessContext.DEVICE_BASED],
                    "max_risk_score": 0.6
                }
            }
        }
    
    def evaluate_access(self, request: AccessRequest, user: dict, resource: dict) -> Dict[str, Any]:
        """コンテキストベースアクセス評価"""
        result = {
            "allowed": False,
            "risk_score": 0.0,
            "context_checks": {},
            "required_additional_auth": [],
            "reason": ""
        }
        
        # コンテキストチェック実行
        context_results = {
            AccessContext.TIME_BASED: self._check_time_context(request),
            AccessContext.LOCATION_BASED: self._check_location_context(request),
            AccessContext.NETWORK_BASED: self._check_network_context(request),
            AccessContext.DEVICE_BASED: self._check_device_context(request, user),
            AccessContext.RISK_BASED: self._calculate_risk_score(request, user)
        }
        
        result["context_checks"] = context_results
        
        # リソース固有のポリシーチェック
        resource_type = resource.get("type", "default")
        if resource_type in self.access_policies.get("sensitive_resources", {}):
            policy = self.access_policies["sensitive_resources"][resource_type]
            result = self._apply_resource_policy(result, context_results, policy)
        else:
            # デフォルトポリシー
            result = self._apply_default_policy(result, context_results)
        
        return result
    
    def _check_time_context(self, request: AccessRequest) -> Dict[str, Any]:
        """時間ベースコンテキストチェック"""
        current_time = request.timestamp.time()
        current_weekday = request.timestamp.weekday()
        
        business_hours = self.access_policies["business_hours"]
        
        is_business_hours = (
            current_weekday in business_hours["weekdays"] and
            business_hours["start_time"] <= current_time <= business_hours["end_time"]
        )
        
        return {
            "passed": True,  # 時間制限は警告のみ
            "is_business_hours": is_business_hours,
            "risk_factor": 0.1 if not is_business_hours else 0.0,
            "details": f"Access at {current_time} on weekday {current_weekday}"
        }
    
    def _check_location_context(self, request: AccessRequest) -> Dict[str, Any]:
        """位置ベースコンテキストチェック"""
        if not request.location:
            return {
                "passed": True,
                "risk_factor": 0.2,  # 位置情報なしはリスク
                "details": "Location information not available"
            }
        
        country_code = request.location.get("country_code")
        blocked_countries = self.access_policies["blocked_countries"]
        
        if country_code in blocked_countries:
            return {
                "passed": False,
                "risk_factor": 1.0,
                "details": f"Access from blocked country: {country_code}"
            }
        
        return {
            "passed": True,
            "risk_factor": 0.0,
            "details": f"Access from {country_code}"
        }
    
    def _check_network_context(self, request: AccessRequest) -> Dict[str, Any]:
        """ネットワークベースコンテキストチェック"""
        try:
            client_ip = ipaddress.ip_address(request.ip_address)
            allowed_networks = self.access_policies["allowed_networks"]
            
            for network_str in allowed_networks:
                network = ipaddress.ip_network(network_str)
                if client_ip in network:
                    return {
                        "passed": True,
                        "risk_factor": 0.0,
                        "details": f"Access from trusted network: {network_str}"
                    }
            
            return {
                "passed": True,  # 外部アクセスも許可、でもリスクスコア上昇
                "risk_factor": 0.3,
                "details": f"External network access from {request.ip_address}"
            }
            
        except ValueError:
            return {
                "passed": False,
                "risk_factor": 1.0,
                "details": f"Invalid IP address: {request.ip_address}"
            }
    
    def _check_device_context(self, request: AccessRequest, user: dict) -> Dict[str, Any]:
        """デバイスベースコンテキストチェック"""
        known_devices = user.get("known_devices", [])
        current_device = request.device_fingerprint
        
        if not current_device:
            return {
                "passed": True,
                "risk_factor": 0.4,
                "details": "Device fingerprint not available"
            }
        
        if current_device in known_devices:
            return {
                "passed": True,
                "risk_factor": 0.0,
                "details": "Known device"
            }
        
        return {
            "passed": True,
            "risk_factor": 0.5,
            "details": "Unknown device - additional verification may be required",
            "requires_device_registration": True
        }
    
    def _calculate_risk_score(self, request: AccessRequest, user: dict) -> Dict[str, Any]:
        """リスクスコア計算"""
        risk_factors = []
        
        # 最終ログインからの経過時間
        last_login = user.get("last_login")
        if last_login:
            hours_since_login = (request.timestamp - last_login).total_seconds() / 3600
            if hours_since_login > 24:
                risk_factors.append(0.2)
        
        # 連続ログイン失敗
        failed_attempts = user.get("recent_failed_attempts", 0)
        if failed_attempts > 3:
            risk_factors.append(0.3)
        
        # 異常なアクセスパターン
        if request.previous_activities:
            unusual_activity = self._detect_unusual_activity(request.previous_activities)
            risk_factors.append(unusual_activity)
        
        # 総合リスクスコア
        total_risk = min(sum(risk_factors), 1.0)
        
        return {
            "passed": total_risk < self.risk_thresholds["high"],
            "risk_factor": total_risk,
            "details": f"Calculated risk score: {total_risk:.2f}",
            "risk_level": self._get_risk_level(total_risk)
        }
    
    def _detect_unusual_activity(self, activities: List[Dict]) -> float:
        """異常アクティビティ検出"""
        # 簡単な異常検出ロジック
        if len(activities) < 3:
            return 0.1
        
        # 短時間に大量アクセス
        recent_activities = [a for a in activities if (datetime.utcnow() - a["timestamp"]).total_seconds() < 3600]
        if len(recent_activities) > 50:
            return 0.4
        
        return 0.0
    
    def _get_risk_level(self, risk_score: float) -> str:
        """リスクレベル算出"""
        if risk_score >= self.risk_thresholds["high"]:
            return "high"
        elif risk_score >= self.risk_thresholds["medium"]:
            return "medium"
        elif risk_score >= self.risk_thresholds["low"]:
            return "low"
        else:
            return "minimal"
    
    def _apply_resource_policy(self, result: dict, context_results: dict, policy: dict) -> dict:
        """リソース固有ポリシー適用"""
        required_contexts = policy.get("required_contexts", [])
        max_risk_score = policy.get("max_risk_score", 0.5)
        
        # 必要コンテキストチェック
        for context in required_contexts:
            if not context_results[context]["passed"]:
                result["allowed"] = False
                result["reason"] = f"Failed {context.value} check"
                return result
        
        # リスクスコア算出
        total_risk = sum(ctx["risk_factor"] for ctx in context_results.values())
        result["risk_score"] = min(total_risk, 1.0)
        
        if result["risk_score"] > max_risk_score:
            result["allowed"] = False
            result["reason"] = f"Risk score {result['risk_score']:.2f} exceeds limit {max_risk_score}"
            result["required_additional_auth"] = ["mfa", "admin_approval"]
        else:
            result["allowed"] = True
        
        return result
    
    def _apply_default_policy(self, result: dict, context_results: dict) -> dict:
        """デフォルトポリシー適用"""
        # ロケーションチェック失敗は拒否
        if not context_results[AccessContext.LOCATION_BASED]["passed"]:
            result["allowed"] = False
            result["reason"] = "Location-based access denied"
            return result
        
        # リスクスコア算出
        total_risk = sum(ctx["risk_factor"] for ctx in context_results.values())
        result["risk_score"] = min(total_risk, 1.0)
        
        if result["risk_score"] > self.risk_thresholds["medium"]:
            result["required_additional_auth"] = ["mfa"]
        
        result["allowed"] = True
        return result

# 使用例
context_checker = ContextualAccessChecker()

@app.get("/sensitive-data/{resource_id}")
async def access_sensitive_data(
    resource_id: str,
    request: Request,
    current_user: dict = Depends(auth_manager.get_current_user)
):
    """機密データアクセス（コンテキストチェック）"""
    
    # アクセスリクエスト情報構築
    access_request = AccessRequest(
        user_id=current_user["id"],
        resource_id=resource_id,
        action="read",
        ip_address=request.client.host,
        user_agent=request.headers.get("user-agent", ""),
        timestamp=datetime.utcnow(),
        device_fingerprint=request.headers.get("x-device-fingerprint"),
        session_id=request.headers.get("x-session-id")
    )
    
    # リソース情報取得
    resource = get_resource_by_id(resource_id)
    if not resource:
        raise HTTPException(status_code=404, detail="Resource not found")
    
    # コンテキストベースアクセス評価
    access_result = context_checker.evaluate_access(access_request, current_user, resource)
    
    if not access_result["allowed"]:
        # 追加認証が必要な場合
        if access_result["required_additional_auth"]:
            return {
                "message": "Additional authentication required",
                "required_auth": access_result["required_additional_auth"],
                "risk_score": access_result["risk_score"]
            }
        
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=access_result["reason"]
        )
    
    # アクセスログ記録
    log_access_attempt(access_request, access_result)
    
    return {
        "data": resource["data"],
        "access_context": {
            "risk_score": access_result["risk_score"],
            "context_summary": access_result["context_checks"]
        }
    }
```

### 7.4 データ保護・暗号化

#### 7.4.1 機密データ保護

**Good: 包括的データ保護実装**
```python
# 機密データ保護総合システム
import os
import json
import asyncio
from typing import Dict, List, Optional, Any, Union
from enum import Enum
from dataclasses import dataclass, field
from datetime import datetime, timedelta
from cryptography.fernet import Fernet
from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric import rsa, padding
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
import base64
import secrets
import hashlib
import re
from sqlalchemy import Column, String, DateTime, Text, Boolean, Integer
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import Session

Base = declarative_base()

class DataClassification(Enum):
    """データ機密分類"""
    PUBLIC = "public"
    INTERNAL = "internal"
    CONFIDENTIAL = "confidential"
    RESTRICTED = "restricted"
    TOP_SECRET = "top_secret"

class EncryptionLevel(Enum):
    """暗号化レベル"""
    NONE = "none"
    BASIC = "basic"  # AES-256
    ADVANCED = "advanced"  # AES-256 + キーローテーション
    ENTERPRISE = "enterprise"  # RSA + AES-256 + HSM

@dataclass
class DataProtectionPolicy:
    """データ保護ポリシー"""
    classification: DataClassification
    encryption_level: EncryptionLevel
    retention_days: int
    access_logging: bool = True
    anonymization_required: bool = False
    geographic_restrictions: List[str] = field(default_factory=list)
    allowed_export_formats: List[str] = field(default_factory=list)

class SensitiveDataModel(Base):
    """機密データモデル"""
    __tablename__ = "sensitive_data"
    
    id = Column(String, primary_key=True)
    classification = Column(String, nullable=False)
    encrypted_data = Column(Text, nullable=False)
    encryption_key_id = Column(String, nullable=False)
    data_hash = Column(String, nullable=False)  # 整合性検証用
    created_at = Column(DateTime, default=datetime.utcnow)
    expires_at = Column(DateTime)
    access_count = Column(Integer, default=0)
    last_accessed = Column(DateTime)
    anonymized = Column(Boolean, default=False)
    geographic_location = Column(String)

class DataProtectionManager:
    """データ保護管理クラス"""
    
    def __init__(self):
        self.policies = self._initialize_policies()
        self.encryption_keys = {}  # 実際はHSMやキー管理サービス使用
        self.anonymization_rules = self._load_anonymization_rules()
        
        # 暗号化コンテキスト
        self.fernet_contexts = {}
        self._initialize_encryption_contexts()
    
    def _initialize_policies(self) -> Dict[DataClassification, DataProtectionPolicy]:
        """データ保護ポリシー初期化"""
        return {
            DataClassification.PUBLIC: DataProtectionPolicy(
                classification=DataClassification.PUBLIC,
                encryption_level=EncryptionLevel.NONE,
                retention_days=365 * 7,  # 7年
                access_logging=False,
                allowed_export_formats=["json", "csv", "xml"]
            ),
            DataClassification.INTERNAL: DataProtectionPolicy(
                classification=DataClassification.INTERNAL,
                encryption_level=EncryptionLevel.BASIC,
                retention_days=365 * 3,  # 3年
                access_logging=True,
                allowed_export_formats=["json", "csv"]
            ),
            DataClassification.CONFIDENTIAL: DataProtectionPolicy(
                classification=DataClassification.CONFIDENTIAL,
                encryption_level=EncryptionLevel.ADVANCED,
                retention_days=365 * 2,  # 2年
                access_logging=True,
                anonymization_required=True,
                geographic_restrictions=["US", "EU", "JP"],
                allowed_export_formats=["json"]
            ),
            DataClassification.RESTRICTED: DataProtectionPolicy(
                classification=DataClassification.RESTRICTED,
                encryption_level=EncryptionLevel.ENTERPRISE,
                retention_days=365,  # 1年
                access_logging=True,
                anonymization_required=True,
                geographic_restrictions=["US", "EU"],
                allowed_export_formats=[]
            ),
            DataClassification.TOP_SECRET: DataProtectionPolicy(
                classification=DataClassification.TOP_SECRET,
                encryption_level=EncryptionLevel.ENTERPRISE,
                retention_days=90,  # 90日
                access_logging=True,
                anonymization_required=True,
                geographic_restrictions=["US"],
                allowed_export_formats=[]
            )
        }
    
    def _initialize_encryption_contexts(self):
        """暗号化コンテキスト初期化"""
        for classification in DataClassification:
            policy = self.policies[classification]
            if policy.encryption_level != EncryptionLevel.NONE:
                key = self._generate_or_retrieve_key(classification.value)
                self.fernet_contexts[classification] = Fernet(key)
    
    def _generate_or_retrieve_key(self, key_id: str) -> bytes:
        """暗号化キー生成または取得"""
        # 実際はキー管理サービスやHSMから取得
        env_key = f"ENCRYPTION_KEY_{key_id.upper()}"
        key_str = os.getenv(env_key)
        
        if key_str:
            return base64.urlsafe_b64decode(key_str)
        else:
            # 新しいキー生成
            key = Fernet.generate_key()
            # キーを安全に保存（実際はキー管理サービス使用）
            self.encryption_keys[key_id] = key
            return key
    
    def _load_anonymization_rules(self) -> Dict[str, Dict]:
        """匿名化ルール読み込み"""
        return {
            "email": {
                "pattern": r"[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}",
                "replacement": "***@***.***"
            },
            "phone": {
                "pattern": r"\b\d{3}-\d{3}-\d{4}\b",
                "replacement": "***-***-****"
            },
            "ssn": {
                "pattern": r"\b\d{3}-\d{2}-\d{4}\b",
                "replacement": "***-**-****"
            },
            "credit_card": {
                "pattern": r"\b\d{4}[\s-]?\d{4}[\s-]?\d{4}[\s-]?\d{4}\b",
                "replacement": "****-****-****-****"
            },
            "ip_address": {
                "pattern": r"\b(?:\d{1,3}\.){3}\d{1,3}\b",
                "replacement": "***.***.***.***"
            }
        }
    
    def classify_data(self, data: Any, metadata: Dict[str, Any] = None) -> DataClassification:
        """データ機密分類自動判定"""
        if metadata and "classification" in metadata:
            return DataClassification(metadata["classification"])
        
        # データ内容で自動分類
        data_str = str(data).lower() if data else ""
        
        # 最高機密キーワード
        top_secret_keywords = ["classified", "secret", "confidential", "password", "token", "key"]
        if any(keyword in data_str for keyword in top_secret_keywords):
            return DataClassification.RESTRICTED
        
        # 個人情報キーワード
        pii_keywords = ["email", "phone", "ssn", "address", "name", "birthday"]
        if any(keyword in data_str for keyword in pii_keywords):
            return DataClassification.CONFIDENTIAL
        
        # 機密情報のパターンマッチング
        sensitive_patterns = [
            r"[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}",  # Email
            r"\b\d{3}-\d{2}-\d{4}\b",  # SSN
            r"\b\d{4}[\s-]?\d{4}[\s-]?\d{4}[\s-]?\d{4}\b"  # Credit Card
        ]
        
        if any(re.search(pattern, data_str) for pattern in sensitive_patterns):
            return DataClassification.CONFIDENTIAL
        
        return DataClassification.INTERNAL
    
    def encrypt_data(self, data: Any, classification: DataClassification) -> Dict[str, Any]:
        """データ暗号化"""
        policy = self.policies[classification]
        
        if policy.encryption_level == EncryptionLevel.NONE:
            return {
                "encrypted_data": json.dumps(data),
                "encryption_key_id": None,
                "data_hash": self._calculate_hash(data)
            }
        
        # データをJSON文字列に変換
        data_str = json.dumps(data, ensure_ascii=False, separators=(',', ':'))
        
        # 暗号化実行
        fernet = self.fernet_contexts[classification]
        encrypted_data = fernet.encrypt(data_str.encode('utf-8'))
        
        return {
            "encrypted_data": base64.urlsafe_b64encode(encrypted_data).decode('utf-8'),
            "encryption_key_id": classification.value,
            "data_hash": self._calculate_hash(data)
        }
    
    def decrypt_data(self, encrypted_data: str, encryption_key_id: str) -> Any:
        """データ復号化"""
        if not encryption_key_id:
            return json.loads(encrypted_data)
        
        classification = DataClassification(encryption_key_id)
        fernet = self.fernet_contexts[classification]
        
        try:
            # Base64デコード
            encrypted_bytes = base64.urlsafe_b64decode(encrypted_data.encode('utf-8'))
            
            # 復号化
            decrypted_bytes = fernet.decrypt(encrypted_bytes)
            decrypted_str = decrypted_bytes.decode('utf-8')
            
            return json.loads(decrypted_str)
            
        except Exception as e:
            raise ValueError(f"Decryption failed: {str(e)}")
    
    def anonymize_data(self, data: Any, classification: DataClassification) -> Any:
        """データ匿名化"""
        policy = self.policies[classification]
        
        if not policy.anonymization_required:
            return data
        
        if isinstance(data, str):
            return self._anonymize_string(data)
        elif isinstance(data, dict):
            return {key: self.anonymize_data(value, classification) for key, value in data.items()}
        elif isinstance(data, list):
            return [self.anonymize_data(item, classification) for item in data]
        else:
            return data
    
    def _anonymize_string(self, text: str) -> str:
        """文字列匿名化"""
        anonymized = text
        
        for rule_name, rule in self.anonymization_rules.items():
            anonymized = re.sub(rule["pattern"], rule["replacement"], anonymized)
        
        return anonymized
    
    def _calculate_hash(self, data: Any) -> str:
        """データハッシュ計算（整合性検証用）"""
        data_str = json.dumps(data, ensure_ascii=False, sort_keys=True, separators=(',', ':'))
        return hashlib.sha256(data_str.encode('utf-8')).hexdigest()
    
    async def store_sensitive_data(
        self,
        data: Any,
        classification: DataClassification = None,
        metadata: Dict[str, Any] = None,
        db_session: Session = None
    ) -> str:
        """機密データ保存"""
        
        # データ分類
        if not classification:
            classification = self.classify_data(data, metadata)
        
        policy = self.policies[classification]
        
        # データ暗号化
        encryption_result = self.encrypt_data(data, classification)
        
        # 保存期限計算
        expires_at = datetime.utcnow() + timedelta(days=policy.retention_days)
        
        # データベース保存
        data_id = secrets.token_urlsafe(32)
        
        sensitive_data = SensitiveDataModel(
            id=data_id,
            classification=classification.value,
            encrypted_data=encryption_result["encrypted_data"],
            encryption_key_id=encryption_result["encryption_key_id"],
            data_hash=encryption_result["data_hash"],
            expires_at=expires_at,
            geographic_location=metadata.get("location") if metadata else None
        )
        
        if db_session:
            db_session.add(sensitive_data)
            db_session.commit()
        
        return data_id
    
    async def retrieve_sensitive_data(
        self,
        data_id: str,
        user: dict,
        anonymize: bool = False,
        db_session: Session = None
    ) -> Dict[str, Any]:
        """機密データ取得"""
        
        if not db_session:
            raise ValueError("Database session required")
        
        # データ取得
        sensitive_data = db_session.query(SensitiveDataModel).filter(
            SensitiveDataModel.id == data_id
        ).first()
        
        if not sensitive_data:
            raise ValueError("Data not found")
        
        # 有効期限チェック
        if sensitive_data.expires_at and sensitive_data.expires_at < datetime.utcnow():
            raise ValueError("Data has expired")
        
        classification = DataClassification(sensitive_data.classification)
        policy = self.policies[classification]
        
        # 地理的制限チェック
        user_location = user.get("location")
        if policy.geographic_restrictions and user_location:
            if user_location not in policy.geographic_restrictions:
                raise ValueError(f"Access denied from location: {user_location}")
        
        # データ復号化
        decrypted_data = self.decrypt_data(
            sensitive_data.encrypted_data,
            sensitive_data.encryption_key_id
        )
        
        # 匿名化処理
        if anonymize or policy.anonymization_required:
            decrypted_data = self.anonymize_data(decrypted_data, classification)
        
        # アクセスログ更新
        if policy.access_logging:
            sensitive_data.access_count += 1
            sensitive_data.last_accessed = datetime.utcnow()
            db_session.commit()
            
            # アクセスログ記録
            await self._log_data_access(data_id, user, classification)
        
        return {
            "data": decrypted_data,
            "classification": classification.value,
            "anonymized": anonymize or policy.anonymization_required,
            "access_count": sensitive_data.access_count,
            "metadata": {
                "created_at": sensitive_data.created_at,
                "expires_at": sensitive_data.expires_at,
                "last_accessed": sensitive_data.last_accessed
            }
        }
    
    async def _log_data_access(self, data_id: str, user: dict, classification: DataClassification):
        """データアクセスログ記録"""
        log_entry = {
            "timestamp": datetime.utcnow().isoformat(),
            "data_id": data_id,
            "user_id": user.get("id"),
            "username": user.get("username"),
            "classification": classification.value,
            "ip_address": user.get("ip_address"),
            "user_agent": user.get("user_agent")
        }
        
        # ログシステムに送信（実装例）
        # await send_to_log_system(log_entry)
        pass
    
    async def export_data(
        self,
        data_id: str,
        export_format: str,
        user: dict,
        db_session: Session = None
    ) -> bytes:
        """データエクスポート"""
        
        # データ取得
        data_info = await self.retrieve_sensitive_data(data_id, user, anonymize=True, db_session=db_session)
        classification = DataClassification(data_info["classification"])
        policy = self.policies[classification]
        
        # エクスポート形式チェック
        if export_format not in policy.allowed_export_formats:
            raise ValueError(f"Export format {export_format} not allowed for {classification.value} data")
        
        # フォーマット別エクスポート
        if export_format == "json":
            return json.dumps(data_info["data"], ensure_ascii=False, indent=2).encode('utf-8')
        elif export_format == "csv":
            # CSVエクスポート処理
            return self._export_to_csv(data_info["data"])
        elif export_format == "xml":
            # XMLエクスポート処理
            return self._export_to_xml(data_info["data"])
        else:
            raise ValueError(f"Unsupported export format: {export_format}")
    
    def _export_to_csv(self, data: Any) -> bytes:
        """データCSVエクスポート"""
        import csv
        import io
        
        output = io.StringIO()
        
        if isinstance(data, list) and len(data) > 0 and isinstance(data[0], dict):
            # 辞書のリストのCSV化
            fieldnames = data[0].keys()
            writer = csv.DictWriter(output, fieldnames=fieldnames)
            writer.writeheader()
            writer.writerows(data)
        else:
            # シンプルデータのCSV化
            writer = csv.writer(output)
            writer.writerow(["data"])
            writer.writerow([str(data)])
        
        return output.getvalue().encode('utf-8')
    
    def _export_to_xml(self, data: Any) -> bytes:
        """データXMLエクスポート"""
        import xml.etree.ElementTree as ET
        
        root = ET.Element("data")
        self._dict_to_xml(data, root)
        
        return ET.tostring(root, encoding='utf-8', xml_declaration=True)
    
    def _dict_to_xml(self, data: Any, parent: ET.Element):
        """辞書をXMLに変換"""
        if isinstance(data, dict):
            for key, value in data.items():
                child = ET.SubElement(parent, str(key))
                self._dict_to_xml(value, child)
        elif isinstance(data, list):
            for item in data:
                child = ET.SubElement(parent, "item")
                self._dict_to_xml(item, child)
        else:
            parent.text = str(data)

# 使用例
data_protection = DataProtectionManager()

@app.post("/sensitive-data")
async def store_sensitive_data(
    data: dict,
    classification: str = None,
    current_user: dict = Depends(auth_manager.get_current_user),
    db: Session = Depends(get_db)
):
    """機密データ保存"""
    
    # データ分類
    data_classification = DataClassification(classification) if classification else None
    
    # メタデータ構築
    metadata = {
        "user_id": current_user["id"],
        "location": current_user.get("location"),
        "timestamp": datetime.utcnow().isoformat()
    }
    
    # データ保存
    data_id = await data_protection.store_sensitive_data(
        data=data,
        classification=data_classification,
        metadata=metadata,
        db_session=db
    )
    
    return {
        "data_id": data_id,
        "classification": data_classification.value if data_classification else "auto-detected",
        "message": "Data stored securely"
    }

@app.get("/sensitive-data/{data_id}")
async def get_sensitive_data(
    data_id: str,
    anonymize: bool = False,
    current_user: dict = Depends(auth_manager.get_current_user),
    db: Session = Depends(get_db)
):
    """機密データ取得"""
    
    try:
        data_info = await data_protection.retrieve_sensitive_data(
            data_id=data_id,
            user=current_user,
            anonymize=anonymize,
            db_session=db
        )
        
        return data_info
        
    except ValueError as e:
        raise HTTPException(status_code=404, detail=str(e))

@app.get("/sensitive-data/{data_id}/export")
async def export_sensitive_data(
    data_id: str,
    format: str = "json",
    current_user: dict = Depends(auth_manager.get_current_user),
    db: Session = Depends(get_db)
):
    """機密データエクスポート"""
    
    try:
        exported_data = await data_protection.export_data(
            data_id=data_id,
            export_format=format,
            user=current_user,
            db_session=db
        )
        
        # レスポンスヘッダー設定
        headers = {
            "Content-Disposition": f"attachment; filename=data_{data_id}.{format}",
            "Content-Type": f"application/{format}"
        }
        
        return Response(content=exported_data, headers=headers)
        
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
```

---

