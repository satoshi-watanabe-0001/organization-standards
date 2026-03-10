## 4. エラーハンドリング・例外処理

### 4.1 例外設計

#### **カスタム例外の階層設計**
```python
# ✅ Good: 意味のある例外階層
# exceptions.py

class BaseAppException(Exception):
    """アプリケーション例外の基底クラス"""
    
    def __init__(self, message: str, error_code: Optional[str] = None) -> None:
        super().__init__(message)
        self.message = message
        self.error_code = error_code
    
    def __str__(self) -> str:
        if self.error_code:
            return f"[{self.error_code}] {self.message}"
        return self.message

# ドメイン別例外
class ValidationError(BaseAppException):
    """バリデーションエラー"""
    pass

class NotFoundError(BaseAppException):
    """リソース未発見エラー"""
    pass

class DuplicateError(BaseAppException):
    """重複エラー"""
    pass

class AuthenticationError(BaseAppException):
    """認証エラー"""
    pass

class AuthorizationError(BaseAppException):
    """認可エラー"""
    pass

class ExternalServiceError(BaseAppException):
    """外部サービス連携エラー"""
    pass

# 具体的な例外クラス
class UserNotFoundError(NotFoundError):
    """ユーザー未発見エラー"""
    
    def __init__(self, user_id: Union[int, str]) -> None:
        super().__init__(
            message=f"User not found: {user_id}",
            error_code="USER_NOT_FOUND"
        )
        self.user_id = user_id

class DuplicateEmailError(DuplicateError):
    """メールアドレス重複エラー"""
    
    def __init__(self, email: str) -> None:
        super().__init__(
            message=f"Email already exists: {email}",
            error_code="DUPLICATE_EMAIL"
        )
        self.email = email

class InvalidPasswordError(ValidationError):
    """無効なパスワードエラー"""
    
    def __init__(self, reason: str) -> None:
        super().__init__(
            message=f"Invalid password: {reason}",
            error_code="INVALID_PASSWORD"
        )
        self.reason = reason

class InsufficientInventoryError(ValidationError):
    """在庫不足エラー"""
    
    def __init__(self, product_id: int, requested: int, available: int) -> None:
        super().__init__(
            message=f"Insufficient inventory for product {product_id}: "
                   f"requested {requested}, available {available}",
            error_code="INSUFFICIENT_INVENTORY"
        )
        self.product_id = product_id
        self.requested = requested
        self.available = available

class PaymentProcessingError(ExternalServiceError):
    """決済処理エラー"""
    
    def __init__(self, payment_id: str, reason: str) -> None:
        super().__init__(
            message=f"Payment processing failed for {payment_id}: {reason}",
            error_code="PAYMENT_PROCESSING_ERROR"
        )
        self.payment_id = payment_id
        self.reason = reason
```

#### **適切な例外処理パターン**
```python
# ✅ Good: 適切な例外処理の実装
import logging
from typing import Optional, Dict, Any
from contextlib import contextmanager
from functools import wraps

logger = logging.getLogger(__name__)

class UserService:
    def __init__(self, repository: UserRepository, email_service: EmailService) -> None:
        self._repository = repository
        self._email_service = email_service
    
    def create_user(self, user_data: Dict[str, Any]) -> User:
        """新規ユーザー作成"""
        try:
            # バリデーション
            self._validate_user_data(user_data)
            
            # 重複チェック
            existing_user = self._repository.find_by_email(user_data['email'])
            if existing_user:
                raise DuplicateEmailError(user_data['email'])
            
            # ユーザー作成
            user = User(
                email=user_data['email'].lower(),
                first_name=user_data['first_name'],
                last_name=user_data['last_name']
            )
            
            saved_user = self._repository.save(user)
            logger.info(f"User created successfully: {saved_user.id}")
            
            # ウェルカムメール送信（失敗してもユーザー作成は成功）
            try:
                self._email_service.send_welcome_email(saved_user)
                logger.info(f"Welcome email sent to: {saved_user.email}")
            except Exception as e:
                logger.error(f"Failed to send welcome email to {saved_user.email}: {e}")
                # メール送信失敗はユーザー作成を阻害しない
            
            return saved_user
            
        except ValidationError:
            # バリデーションエラーは再発生させる
            raise
        except DuplicateEmailError:
            # 重複エラーは再発生させる
            raise
        except DatabaseError as e:
            # データベースエラーは内部エラーとして処理
            logger.error(f"Database error during user creation: {e}")
            raise BaseAppException(
                "User creation failed due to internal error",
                error_code="INTERNAL_ERROR"
            ) from e
        except Exception as e:
            # 予期しないエラー
            logger.error(f"Unexpected error during user creation: {e}", exc_info=True)
            raise BaseAppException(
                "Unexpected error occurred during user creation",
                error_code="UNEXPECTED_ERROR"
            ) from e
    
    def get_user_by_id(self, user_id: int) -> User:
        """IDによるユーザー取得"""
        try:
            user = self._repository.find_by_id(user_id)
            if not user:
                raise UserNotFoundError(user_id)
            return user
            
        except UserNotFoundError:
            # ユーザー未発見は想定内のエラー
            raise
        except Exception as e:
            logger.error(f"Error retrieving user {user_id}: {e}")
            raise BaseAppException(
                f"Failed to retrieve user {user_id}",
                error_code="USER_RETRIEVAL_ERROR"
            ) from e
    
    def update_user_password(self, user_id: int, new_password: str) -> None:
        """ユーザーパスワード更新"""
        try:
            # ユーザー存在確認
            user = self.get_user_by_id(user_id)
            
            # パスワード強度チェック
            if not self._is_strong_password(new_password):
                raise InvalidPasswordError("Password does not meet strength requirements")
            
            # パスワード更新
            user.password_hash = self._hash_password(new_password)
            self._repository.save(user)
            
            logger.info(f"Password updated for user: {user_id}")
            
        except (UserNotFoundError, InvalidPasswordError):
            # 想定内のエラーは再発生
            raise
        except Exception as e:
            logger.error(f"Error updating password for user {user_id}: {e}")
            raise BaseAppException(
                "Password update failed",
                error_code="PASSWORD_UPDATE_ERROR"
            ) from e
    
    def _validate_user_data(self, user_data: Dict[str, Any]) -> None:
        """ユーザーデータバリデーション"""
        errors = []
        
        # 必須フィールドチェック
        required_fields = ['email', 'first_name', 'last_name']
        for field in required_fields:
            if not user_data.get(field):
                errors.append(f"Missing required field: {field}")
        
        # メールアドレス形式チェック
        if user_data.get('email') and not self._is_valid_email(user_data['email']):
            errors.append(f"Invalid email format: {user_data['email']}")
        
        # 名前の長さチェック
        for field in ['first_name', 'last_name']:
            value = user_data.get(field, '')
            if len(value) > 50:
                errors.append(f"{field} must be 50 characters or less")
        
        if errors:
            raise ValidationError('; '.join(errors))

# コンテキストマネージャーを使った安全なリソース管理
@contextmanager
def database_transaction(db_session):
    """データベーストランザクション管理"""
    try:
        yield db_session
        db_session.commit()
        logger.debug("Database transaction committed")
    except Exception as e:
        db_session.rollback()
        logger.error(f"Database transaction rolled back: {e}")
        raise
    finally:
        db_session.close()

# デコレーターを使った例外ログ記録
def log_exceptions(logger_name: Optional[str] = None):
    """例外をログに記録するデコレーター"""
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            func_logger = logging.getLogger(logger_name or func.__module__)
            try:
                return func(*args, **kwargs)
            except Exception as e:
                func_logger.error(
                    f"Exception in {func.__name__}: {e}",
                    exc_info=True,
                    extra={
                        'function': func.__name__,
                        'args': args,
                        'kwargs': kwargs
                    }
                )
                raise
        return wrapper
    return decorator

# 使用例
@log_exceptions('user_service')
def process_user_registration(user_data: Dict[str, Any]) -> User:
    """ユーザー登録処理"""
    with database_transaction(get_db_session()) as db:
        user_service = UserService(UserRepository(db), EmailService())
        return user_service.create_user(user_data)
```

### 4.2 エラーレスポンス・ログ処理

#### **APIエラーレスポンス統一**
```python
# ✅ Good: 統一されたエラーレスポンス処理
from fastapi import FastAPI, Request, status
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError
from typing import Dict, Any, List
import uuid
from datetime import datetime

app = FastAPI()

# エラーレスポンスモデル
from pydantic import BaseModel

class ErrorDetail(BaseModel):
    """エラー詳細"""
    field: Optional[str] = None
    message: str
    code: Optional[str] = None

class ErrorResponse(BaseModel):
    """統一エラーレスポンス"""
    error: str
    message: str
    details: List[ErrorDetail] = []
    request_id: str
    timestamp: datetime
    status_code: int

# グローバル例外ハンドラー
@app.exception_handler(BaseAppException)
async def base_app_exception_handler(request: Request, exc: BaseAppException) -> JSONResponse:
    """アプリケーション例外ハンドラー"""
    request_id = str(uuid.uuid4())
    
    # ログ出力
    logger.error(
        f"Application exception: {exc}",
        extra={
            'request_id': request_id,
            'error_code': exc.error_code,
            'path': request.url.path,
            'method': request.method
        }
    )
    
    # ステータスコード決定
    status_code = _get_status_code_for_exception(exc)
    
    error_response = ErrorResponse(
        error=exc.__class__.__name__,
        message=exc.message,
        details=[ErrorDetail(message=exc.message, code=exc.error_code)],
        request_id=request_id,
        timestamp=datetime.utcnow(),
        status_code=status_code
    )
    
    return JSONResponse(
        status_code=status_code,
        content=error_response.dict()
    )

@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError) -> JSONResponse:
    """バリデーション例外ハンドラー"""
    request_id = str(uuid.uuid4())
    
    # バリデーションエラー詳細を構築
    details = []
    for error in exc.errors():
        field = '.'.join(str(loc) for loc in error['loc'])
        details.append(ErrorDetail(
            field=field,
            message=error['msg'],
            code=error['type']
        ))
    
    logger.warning(
        f"Validation error: {len(details)} validation failures",
        extra={
            'request_id': request_id,
            'path': request.url.path,
            'method': request.method,
            'validation_errors': details
        }
    )
    
    error_response = ErrorResponse(
        error="ValidationError",
        message="Request validation failed",
        details=details,
        request_id=request_id,
        timestamp=datetime.utcnow(),
        status_code=status.HTTP_422_UNPROCESSABLE_ENTITY
    )
    
    return JSONResponse(
        status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
        content=error_response.dict()
    )

@app.exception_handler(Exception)
async def general_exception_handler(request: Request, exc: Exception) -> JSONResponse:
    """汎用例外ハンドラー"""
    request_id = str(uuid.uuid4())
    
    # 予期しないエラーは詳細をログ出力
    logger.error(
        f"Unexpected error: {exc}",
        exc_info=True,
        extra={
            'request_id': request_id,
            'path': request.url.path,
            'method': request.method
        }
    )
    
    # 本番環境では詳細なエラー情報を隠す
    if settings.ENVIRONMENT == 'production':
        message = "An internal error occurred"
    else:
        message = str(exc)
    
    error_response = ErrorResponse(
        error="InternalServerError",
        message=message,
        details=[],
        request_id=request_id,
        timestamp=datetime.utcnow(),
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR
    )
    
    return JSONResponse(
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        content=error_response.dict()
    )

def _get_status_code_for_exception(exc: BaseAppException) -> int:
    """例外タイプに基づくステータスコード決定"""
    exception_status_map = {
        ValidationError: status.HTTP_400_BAD_REQUEST,
        NotFoundError: status.HTTP_404_NOT_FOUND,
        DuplicateError: status.HTTP_409_CONFLICT,
        AuthenticationError: status.HTTP_401_UNAUTHORIZED,
        AuthorizationError: status.HTTP_403_FORBIDDEN,
        ExternalServiceError: status.HTTP_502_BAD_GATEWAY,
    }
    
    for exc_type, status_code in exception_status_map.items():
        if isinstance(exc, exc_type):
            return status_code
    
    return status.HTTP_500_INTERNAL_SERVER_ERROR
```

#### **構造化ログ設定**
```python
# ✅ Good: 構造化ログ設定
import logging
import json
from datetime import datetime
from typing import Dict, Any, Optional

class StructuredFormatter(logging.Formatter):
    """構造化ログフォーマッター"""
    
    def format(self, record: logging.LogRecord) -> str:
        """ログレコードをJSON形式でフォーマット"""
        log_entry = {
            'timestamp': datetime.utcnow().isoformat(),
            'level': record.levelname,
            'logger': record.name,
            'message': record.getMessage(),
        }
        
        # リクエスト情報の追加
        if hasattr(record, 'request_id'):
            log_entry['request_id'] = record.request_id
        
        if hasattr(record, 'user_id'):
            log_entry['user_id'] = record.user_id
        
        # エラー情報の追加
        if record.exc_info:
            log_entry['exception'] = self.formatException(record.exc_info)
        
        # 追加のメタデータ
        extra_fields = ['path', 'method', 'status_code', 'processing_time', 'error_code']
        for field in extra_fields:
            if hasattr(record, field):
                log_entry[field] = getattr(record, field)
        
        return json.dumps(log_entry, ensure_ascii=False)

# ログ設定
def setup_logging(log_level: str = "INFO", environment: str = "development") -> None:
    """ログ設定を初期化"""
    
    # ルートロガー設定
    root_logger = logging.getLogger()
    root_logger.setLevel(getattr(logging, log_level.upper()))
    
    # コンソールハンドラー
    console_handler = logging.StreamHandler()
    
    if environment == "production":
        # 本番環境では構造化ログ
        console_handler.setFormatter(StructuredFormatter())
    else:
        # 開発環境では読みやすい形式
        formatter = logging.Formatter(
            '%(asctime)s - %(name)s - %(levelname)s - %(message)s'
        )
        console_handler.setFormatter(formatter)
    
    root_logger.addHandler(console_handler)
    
    # ファイルハンドラー（本番環境）
    if environment == "production":
        file_handler = logging.FileHandler('app.log')
        file_handler.setFormatter(StructuredFormatter())
        root_logger.addHandler(file_handler)
    
    # 外部ライブラリのログレベル調整
    logging.getLogger('uvicorn').setLevel(logging.WARNING)
    logging.getLogger('sqlalchemy.engine').setLevel(logging.WARNING)

# ログコンテキスト管理
import contextvars

request_id_var: contextvars.ContextVar[Optional[str]] = contextvars.ContextVar('request_id', default=None)
user_id_var: contextvars.ContextVar[Optional[str]] = contextvars.ContextVar('user_id', default=None)

class ContextFilter(logging.Filter):
    """コンテキスト情報をログレコードに追加"""
    
    def filter(self, record: logging.LogRecord) -> bool:
        record.request_id = request_id_var.get()
        record.user_id = user_id_var.get()
        return True

# アプリケーション固有ロガー
class AppLogger:
    """アプリケーション専用ロガーラッパー"""
    
    def __init__(self, name: str) -> None:
        self.logger = logging.getLogger(name)
        self.logger.addFilter(ContextFilter())
    
    def info(self, message: str, **kwargs) -> None:
        """INFO レベルログ"""
        self._log(logging.INFO, message, **kwargs)
    
    def warning(self, message: str, **kwargs) -> None:
        """WARNING レベルログ"""
        self._log(logging.WARNING, message, **kwargs)
    
    def error(self, message: str, **kwargs) -> None:
        """ERROR レベルログ"""
        self._log(logging.ERROR, message, **kwargs)
    
    def debug(self, message: str, **kwargs) -> None:
        """DEBUG レベルログ"""
        self._log(logging.DEBUG, message, **kwargs)
    
    def _log(self, level: int, message: str, **kwargs) -> None:
        """ログ出力の共通処理"""
        if self.logger.isEnabledFor(level):
            extra = {}
            for key, value in kwargs.items():
                extra[key] = value
            
            self.logger.log(level, message, extra=extra)

# 使用例
logger = AppLogger(__name__)

def process_order(order_data: Dict[str, Any]) -> None:
    """注文処理（ログ使用例）"""
    order_id = order_data.get('id')
    
    logger.info(
        f"Processing order: {order_id}",
        order_id=order_id,
        item_count=len(order_data.get('items', []))
    )
    
    try:
        # 処理実行
        result = perform_order_processing(order_data)
        
        logger.info(
            f"Order processed successfully: {order_id}",
            order_id=order_id,
            processing_time=result.processing_time,
            total_amount=result.total_amount
        )
        
    except Exception as e:
        logger.error(
            f"Order processing failed: {order_id}",
            order_id=order_id,
            error=str(e),
            error_type=type(e).__name__
        )
        raise
```

---

