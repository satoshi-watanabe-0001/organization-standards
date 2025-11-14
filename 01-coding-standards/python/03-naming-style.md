## 2. 命名規則・スタイル

### 2.1 命名規則

#### **モジュール・パッケージ名**
```python
# ✅ Good: 小文字、アンダースコア区切り、意味的な名前
import user_service
import data_processing
from utils import string_helpers
from api.v1 import user_endpoints

# ❌ Bad: 大文字・キャメルケース・意味不明
import UserService
import dataProcessing
from utils import StringHelpers
from api.v1 import endpnts
```

#### **クラス名**
```python
# ✅ Good: PascalCase、目的が明確
class UserService:
    """ユーザー関連のビジネスロジックを担当"""
    pass

class OrderProcessor:
    """注文処理の中心クラス"""
    pass

class DatabaseConnection:
    """データベース接続を管理"""
    pass

class APIClient:
    """外部APIとの通信を担当"""
    pass

# 抽象基底クラス
from abc import ABC, abstractmethod

class BaseRepository(ABC):
    """リポジトリの基底クラス"""
    
    @abstractmethod
    def save(self, entity: Any) -> Any:
        pass

# データクラス
from dataclasses import dataclass
from typing import Optional

@dataclass
class UserProfile:
    """ユーザープロファイルデータ"""
    user_id: int
    email: str
    first_name: str
    last_name: str
    is_active: bool = True

# ❌ Bad: 不適切な命名
class userservice:           # 小文字
class User_Service:          # アンダースコア
class Manager:               # 抽象的すぎる
class Data:                  # 汎用的すぎる
class UserServiceImpl:       # 不要なImpl接尾辞
```

#### **関数・メソッド名**
```python
# ✅ Good: snake_case、動詞で開始、目的が明確
def get_user_by_id(user_id: int) -> Optional[User]:
    """IDによるユーザー取得"""
    pass

def create_new_user(user_data: dict) -> User:
    """新規ユーザー作成"""
    pass

def is_email_valid(email: str) -> bool:
    """メールアドレス形式チェック"""
    pass

def calculate_order_total(items: List[OrderItem]) -> Decimal:
    """注文合計金額計算"""
    pass

def send_welcome_email(user: User) -> None:
    """ウェルカムメール送信"""
    pass

# プライベートメソッド
def _validate_user_data(self, user_data: dict) -> None:
    """内部用：ユーザーデータバリデーション"""
    pass

# 特殊メソッド
class User:
    def __str__(self) -> str:
        return f"User({self.email})"
    
    def __repr__(self) -> str:
        return f"User(id={self.id}, email='{self.email}')"

# ❌ Bad: 不適切な命名
def getUserById(user_id):        # キャメルケース
def user(user_id):               # 動詞がない
def check(email):                # 抽象的すぎる
def get_users():                 # 複数形で条件不明確
def validate():                  # 何をバリデートするか不明
```

#### **変数名**
```python
# ✅ Good: snake_case、意味が明確、スコープに応じた長さ
user_id = 12345
email_address = "user@example.com"
is_authenticated = True
order_items = []
total_amount = Decimal('100.00')

# 定数
DEFAULT_PAGE_SIZE = 20
MAX_RETRY_ATTEMPTS = 3
API_BASE_URL = "https://api.example.com"
DATABASE_TIMEOUT_SECONDS = 30

# 環境変数
import os

DEBUG = os.getenv('DEBUG', 'False').lower() == 'true'
SECRET_KEY = os.getenv('SECRET_KEY')
DATABASE_URL = os.getenv('DATABASE_URL')

# リスト内包表記・ジェネレータでの短い変数名（適切なスコープ内）
active_users = [user for user in users if user.is_active]
user_emails = [user.email for user in users]
squared_numbers = [x**2 for x in range(10)]

# ループ内での明確な変数名
for user in users:
    process_user_data(user)

for order_item in order.items:
    calculate_item_total(order_item)

# ❌ Bad: 不適切な命名
userId = 12345                   # キャメルケース
e = "user@example.com"          # 単文字（適切なスコープ外）
flag = True                     # 意味不明
data = []                       # 汎用的すぎる
temp = calculate_something()    # 曖昧
x = get_user_by_id(1)          # 意味不明

# 略語の乱用
usr = get_user()                # user と書く
btn = create_button()           # button と書く
cfg = load_config()             # config と書く
```

### 2.2 型ヒント・アノテーション

#### **基本的な型ヒント**
```python
# ✅ Good: 包括的な型ヒント
from typing import (
    Any, Dict, List, Optional, Union, Tuple, Set, 
    Callable, TypeVar, Generic, Protocol, Literal
)
from decimal import Decimal
from datetime import datetime, date
from uuid import UUID

# 基本型
def get_user_name(user_id: int) -> str:
    """ユーザー名を取得"""
    pass

def calculate_tax(amount: Decimal, rate: float) -> Decimal:
    """税額計算"""
    pass

def is_weekend(target_date: date) -> bool:
    """週末判定"""
    pass

# Optional型
def find_user_by_email(email: str) -> Optional[User]:
    """メールアドレスによるユーザー検索（見つからない場合はNone）"""
    pass

# Union型
def process_id(user_id: Union[int, str, UUID]) -> str:
    """様々な形式のIDを文字列に変換"""
    pass

# コレクション型
def get_user_permissions(user: User) -> List[str]:
    """ユーザー権限一覧を取得"""
    pass

def group_users_by_role(users: List[User]) -> Dict[str, List[User]]:
    """ロール別にユーザーをグループ化"""
    pass

def get_unique_emails(users: List[User]) -> Set[str]:
    """重複のないメールアドレス一覧"""
    pass

# 複雑な型
def create_user_profile(
    user_data: Dict[str, Any],
    permissions: List[str],
    metadata: Optional[Dict[str, str]] = None
) -> Tuple[User, UserProfile]:
    """ユーザーとプロファイルを同時作成"""
    pass

# Callable型
def apply_discount(
    price: Decimal,
    discount_func: Callable[[Decimal], Decimal]
) -> Decimal:
    """割引関数を適用"""
    pass

# TypeVar・Generic
T = TypeVar('T')
K = TypeVar('K')
V = TypeVar('V')

class Repository(Generic[T]):
    """汎用リポジトリクラス"""
    
    def save(self, entity: T) -> T:
        pass
    
    def find_by_id(self, entity_id: int) -> Optional[T]:
        pass

# Protocol（構造的サブタイピング）
class Serializable(Protocol):
    def serialize(self) -> Dict[str, Any]:
        ...

def save_to_json(obj: Serializable, filename: str) -> None:
    """シリアライズ可能なオブジェクトをJSONファイルに保存"""
    pass

# Literal型
Status = Literal['active', 'inactive', 'pending']

def update_user_status(user_id: int, status: Status) -> None:
    """ユーザーステータス更新"""
    pass
```

#### **クラスの型ヒント**
```python
# ✅ Good: クラスでの適切な型ヒント
from typing import ClassVar, Final
from dataclasses import dataclass, field
from datetime import datetime

@dataclass
class User:
    """ユーザーエンティティ"""
    
    # クラス変数
    DEFAULT_ROLE: ClassVar[str] = 'user'
    MAX_LOGIN_ATTEMPTS: ClassVar[int] = 3
    
    # インスタンス変数
    id: int
    email: str
    first_name: str
    last_name: str
    is_active: bool = True
    created_at: datetime = field(default_factory=datetime.now)
    permissions: List[str] = field(default_factory=list)
    metadata: Dict[str, Any] = field(default_factory=dict)
    
    # 定数
    ADMIN_EMAIL_DOMAIN: Final[str] = '@admin.company.com'
    
    def __post_init__(self) -> None:
        """初期化後処理"""
        if not self.permissions:
            self.permissions = [self.DEFAULT_ROLE]
    
    @property
    def full_name(self) -> str:
        """フルネーム取得"""
        return f"{self.first_name} {self.last_name}"
    
    @property
    def is_admin(self) -> bool:
        """管理者判定"""
        return self.email.endswith(self.ADMIN_EMAIL_DOMAIN)
    
    def add_permission(self, permission: str) -> None:
        """権限追加"""
        if permission not in self.permissions:
            self.permissions.append(permission)
    
    def has_permission(self, permission: str) -> bool:
        """権限チェック"""
        return permission in self.permissions

class UserService:
    """ユーザーサービス"""
    
    def __init__(self, repository: Repository[User]) -> None:
        self._repository = repository
        self._cache: Dict[int, User] = {}
    
    def create_user(
        self, 
        user_data: Dict[str, Any],
        send_welcome_email: bool = True
    ) -> User:
        """新規ユーザー作成"""
        pass
    
    def get_user_by_id(self, user_id: int) -> Optional[User]:
        """IDによるユーザー取得"""
        pass
    
    def update_user(self, user_id: int, updates: Dict[str, Any]) -> User:
        """ユーザー情報更新"""
        pass
```

### 2.3 docstring規約

#### **Google Styleのdocstring**
```python
# ✅ Good: Google Style docstring
def create_user_account(
    email: str,
    password: str,
    first_name: str,
    last_name: str,
    role: str = 'user',
    send_welcome_email: bool = True
) -> User:
    """新規ユーザーアカウントを作成する.
    
    指定されたメールアドレスとパスワードで新しいユーザーアカウントを作成し、
    オプションでウェルカムメールを送信する。
    
    Args:
        email: ユーザーのメールアドレス。一意である必要がある。
        password: プレーンテキストのパスワード。8文字以上推奨。
        first_name: ユーザーの名前。
        last_name: ユーザーの姓。
        role: ユーザーロール。デフォルトは'user'。
        send_welcome_email: ウェルカムメール送信フラグ。
    
    Returns:
        作成されたUserオブジェクト。
    
    Raises:
        ValidationError: メールアドレスが無効またはパスワードが弱い場合。
        DuplicateEmailError: メールアドレスが既に使用されている場合。
        DatabaseError: データベース操作でエラーが発生した場合。
    
    Examples:
        >>> user = create_user_account(
        ...     email='user@example.com',
        ...     password='secure_password',
        ...     first_name='太郎',
        ...     last_name='田中'
        ... )
        >>> user.email
        'user@example.com'
        >>> user.is_active
        True
        
    Note:
        パスワードは内部でハッシュ化されて保存される。
        メールアドレスは大文字小文字を区別しない。
    """
    pass

class UserRepository:
    """ユーザーデータの永続化を担当するリポジトリクラス.
    
    データベースとのやり取りを抽象化し、ユーザーエンティティの
    CRUD操作を提供する。
    
    Attributes:
        connection: データベース接続オブジェクト。
        cache_enabled: キャッシュ機能の有効/無効フラグ。
    
    Examples:
        >>> repo = UserRepository(db_connection)
        >>> user = repo.find_by_email('user@example.com')
        >>> if user:
        ...     print(f"Found user: {user.full_name}")
    """
    
    def __init__(self, connection: DatabaseConnection) -> None:
        """リポジトリを初期化する.
        
        Args:
            connection: データベース接続オブジェクト。
        """
        self.connection = connection
        self.cache_enabled = True
    
    def find_by_email(self, email: str) -> Optional[User]:
        """メールアドレスでユーザーを検索する.
        
        Args:
            email: 検索対象のメールアドレス。大文字小文字は区別しない。
        
        Returns:
            見つかったUserオブジェクト。見つからない場合はNone。
        
        Raises:
            DatabaseError: データベースクエリでエラーが発生した場合。
        """
        pass
```

#### **クラスの詳細docstring**
```python
# ✅ Good: 包括的なクラスdocstring
class OrderProcessor:
    """注文処理のメインクラス.
    
    このクラスは注文の作成から決済処理、在庫管理、通知送信まで
    注文に関するすべてのビジネスロジックを統合的に処理する。
    
    処理フロー:
        1. 注文データのバリデーション
        2. 在庫確認・引当
        3. 決済処理
        4. 注文確定・永続化
        5. 確認メール送信
        6. 在庫更新
    
    Attributes:
        inventory_service: 在庫管理サービス。
        payment_service: 決済処理サービス。
        notification_service: 通知サービス。
        order_repository: 注文データリポジトリ。
        logger: ログ出力用ロガー。
    
    Class Attributes:
        MAX_ITEMS_PER_ORDER: 1注文あたりの最大アイテム数。
        DEFAULT_TIMEOUT: デフォルトタイムアウト時間（秒）。
    
    Examples:
        基本的な使用方法:
        
        >>> processor = OrderProcessor(
        ...     inventory_service=inventory,
        ...     payment_service=payment,
        ...     notification_service=notification,
        ...     order_repository=repository
        ... )
        >>> order = processor.process_order(order_data)
        >>> print(f"Order {order.id} processed successfully")
        
        エラーハンドリング付きの使用:
        
        >>> try:
        ...     order = processor.process_order(order_data)
        ... except InsufficientInventoryError as e:
        ...     logger.warning(f"Inventory shortage: {e}")
        ... except PaymentProcessingError as e:
        ...     logger.error(f"Payment failed: {e}")
    
    Note:
        このクラスはスレッドセーフではない。並行処理が必要な場合は
        インスタンスを分離するか、適切な同期機構を使用すること。
    """
    
    MAX_ITEMS_PER_ORDER: ClassVar[int] = 100
    DEFAULT_TIMEOUT: ClassVar[int] = 30
    
    def __init__(
        self,
        inventory_service: InventoryService,
        payment_service: PaymentService,
        notification_service: NotificationService,
        order_repository: OrderRepository
    ) -> None:
        """OrderProcessorを初期化する.
        
        Args:
            inventory_service: 在庫管理を担当するサービス。
            payment_service: 決済処理を担当するサービス。
            notification_service: 通知送信を担当するサービス。
            order_repository: 注文データの永続化を担当するリポジトリ。
        
        Raises:
            ValueError: 必須サービスがNoneの場合。
        """
        pass
```

---

