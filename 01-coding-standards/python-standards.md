# Python コーディング規約
## Python Coding Standards

**最終更新日**: 2024-10-15  
**バージョン**: 1.0.0  
**対象**: Python 3.11+, Django 4.2+, FastAPI 0.100+, Flask 2.3+  
**適用範囲**: Webアプリケーション・API開発・データ処理・AI/ML・自動化スクリプト

## 目的

このドキュメントは、Pythonプロジェクトにおける具体的なコーディング規約を定義し、Webアプリケーション開発からAI/MLプロジェクトまで幅広い用途で一貫した高品質なコードを実現します。共通原則については`00-general-principles.md`を参照してください。

---

## 1. 基本設定・ツール設定

### 1.1 推奨ツールチェーン

#### **必須ツール**
```txt
# requirements-dev.txt
# コード品質・フォーマット
black==23.9.1
isort==5.12.0
flake8==6.0.0
mypy==1.5.1
pylint==2.17.5

# テスト
pytest==7.4.2
pytest-cov==4.1.0
pytest-django==4.5.2
pytest-asyncio==0.21.1
factory-boy==3.3.0

# セキュリティ
bandit==1.7.5
safety==2.3.1

# 依存関係管理
pip-tools==7.3.0
pipenv==2023.9.8

# 開発支援
pre-commit==3.4.0
tox==4.11.3
```

#### **pyproject.toml設定**
```toml
[build-system]
requires = ["setuptools>=61.0", "wheel"]
build-backend = "setuptools.build_meta"

[project]
name = "your-project-name"
version = "0.1.0"
description = "Project description"
authors = [
    {name = "Your Name", email = "your.email@company.com"},
]
dependencies = [
    "django>=4.2,<5.0",
    "djangorestframework>=3.14.0",
    "fastapi>=0.100.0",
    "uvicorn>=0.23.0",
    "pydantic>=2.0.0",
    "sqlalchemy>=2.0.0",
    "celery>=5.3.0",
    "redis>=4.6.0",
    "psycopg2-binary>=2.9.0",
]
requires-python = ">=3.11"

[project.optional-dependencies]
dev = [
    "black",
    "isort",
    "flake8",
    "mypy",
    "pytest",
    "pytest-cov",
    "pre-commit",
]
ml = [
    "numpy>=1.24.0",
    "pandas>=2.0.0",
    "scikit-learn>=1.3.0",
    "tensorflow>=2.13.0",
    "torch>=2.0.0",
]

# Black設定
[tool.black]
line-length = 88
target-version = ['py311']
include = '\.pyi?$'
extend-exclude = '''
/(
  # directories
  \.eggs
  | \.git
  | \.mypy_cache
  | \.tox
  | \.venv
  | venv
  | _build
  | buck-out
  | build
  | dist
  | migrations
)/
'''

# isort設定
[tool.isort]
profile = "black"
multi_line_output = 3
line_length = 88
include_trailing_comma = true
force_grid_wrap = 0
use_parentheses = true
ensure_newline_before_comments = true
skip_gitignore = true
skip_glob = ["**/migrations/*.py"]

# MyPy設定
[tool.mypy]
python_version = "3.11"
check_untyped_defs = true
disallow_any_generics = true
disallow_incomplete_defs = true
disallow_untyped_defs = true
no_implicit_optional = true
warn_redundant_casts = true
warn_unused_ignores = true
warn_return_any = true
strict_equality = true
show_error_codes = true

[[tool.mypy.overrides]]
module = [
    "django.*",
    "rest_framework.*",
    "celery.*",
    "pytest.*",
]
ignore_missing_imports = true

# pytest設定
[tool.pytest.ini_options]
DJANGO_SETTINGS_MODULE = "config.settings.test"
python_files = ["tests.py", "test_*.py", "*_tests.py"]
python_classes = ["Test*"]
python_functions = ["test_*"]
addopts = [
    "--cov=src",
    "--cov-report=html",
    "--cov-report=term-missing",
    "--cov-fail-under=80",
    "--strict-markers",
    "--strict-config",
    "--disable-warnings",
]
markers = [
    "unit: Unit tests",
    "integration: Integration tests",
    "e2e: End-to-end tests",
    "slow: Slow running tests",
]

# Coverage設定
[tool.coverage.run]
source = ["src"]
omit = [
    "*/migrations/*",
    "*/venv/*",
    "*/tests/*",
    "manage.py",
    "*/settings/*",
    "*/wsgi.py",
    "*/asgi.py",
]

[tool.coverage.report]
exclude_lines = [
    "pragma: no cover",
    "def __repr__",
    "raise AssertionError",
    "raise NotImplementedError",
    "if __name__ == .__main__.:",
    "class .*\\(Protocol\\):",
    "@(abc\\.)?abstractmethod",
]
```

#### **.flake8設定**
```ini
[flake8]
max-line-length = 88
max-complexity = 10
select = E,W,F,C,N
ignore = 
    E203,  # whitespace before ':'
    E501,  # line too long (handled by black)
    W503,  # line break before binary operator
    F401,  # imported but unused (handled by isort)
exclude = 
    .git,
    __pycache__,
    .venv,
    venv,
    .tox,
    .eggs,
    *.egg,
    build,
    dist,
    .mypy_cache,
    migrations
per-file-ignores =
    __init__.py:F401
    settings/*.py:F405,F403
    tests/*.py:S101,S106
```

#### **pre-commit設定（.pre-commit-config.yaml）**
```yaml
repos:
  - repo: https://github.com/pre-commit/pre-commit-hooks
    rev: v4.4.0
    hooks:
      - id: trailing-whitespace
      - id: end-of-file-fixer
      - id: check-yaml
      - id: check-added-large-files
      - id: check-merge-conflict
      - id: debug-statements

  - repo: https://github.com/psf/black
    rev: 23.9.1
    hooks:
      - id: black
        language_version: python3.11

  - repo: https://github.com/pycqa/isort
    rev: 5.12.0
    hooks:
      - id: isort

  - repo: https://github.com/pycqa/flake8
    rev: 6.0.0
    hooks:
      - id: flake8

  - repo: https://github.com/pre-commit/mirrors-mypy
    rev: v1.5.1
    hooks:
      - id: mypy
        additional_dependencies: [types-all]

  - repo: https://github.com/pycqa/bandit
    rev: 1.7.5
    hooks:
      - id: bandit
        args: ['-c', 'pyproject.toml']
        additional_dependencies: ['bandit[toml]']

  - repo: https://github.com/python-poetry/poetry
    rev: 1.6.1
    hooks:
      - id: poetry-check
      - id: poetry-lock
        args: [--no-update]
```

#### **Bandit設定（セキュリティ）**
```toml
# pyproject.toml内に追加
[tool.bandit]
exclude_dirs = ["tests", "venv", ".venv", "migrations"]
skips = ["B101", "B601"]  # assert文、shell=True（テストで使用）
```

---

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

## 3. プロジェクト構造・モジュール設計

### 3.1 ディレクトリ構造

#### **Djangoプロジェクト構造**
```
project-name/
├── manage.py
├── requirements/
│   ├── base.txt           # 共通依存関係
│   ├── development.txt    # 開発環境用
│   ├── production.txt     # 本番環境用
│   └── testing.txt        # テスト環境用
├── config/                # プロジェクト設定
│   ├── __init__.py
│   ├── wsgi.py
│   ├── asgi.py
│   ├── urls.py           # ルートURL設定
│   └── settings/
│       ├── __init__.py
│       ├── base.py       # 共通設定
│       ├── development.py # 開発環境設定
│       ├── production.py  # 本番環境設定
│       └── testing.py     # テスト環境設定
├── apps/                  # アプリケーション群
│   ├── __init__.py
│   ├── users/            # ユーザー管理アプリ
│   │   ├── __init__.py
│   │   ├── models.py     # データモデル
│   │   ├── views.py      # ビューロジック
│   │   ├── serializers.py # DRFシリアライザー
│   │   ├── urls.py       # URL設定
│   │   ├── admin.py      # Django Admin設定
│   │   ├── apps.py       # アプリ設定
│   │   ├── permissions.py # 権限管理
│   │   ├── filters.py    # クエリフィルター
│   │   ├── services.py   # ビジネスロジック
│   │   ├── tasks.py      # Celeryタスク
│   │   ├── tests/        # テストファイル群
│   │   │   ├── __init__.py
│   │   │   ├── test_models.py
│   │   │   ├── test_views.py
│   │   │   ├── test_services.py
│   │   │   └── factories.py # テストデータファクトリ
│   │   └── migrations/   # データベースマイグレーション
│   ├── orders/           # 注文管理アプリ
│   └── common/           # 共通機能アプリ
│       ├── __init__.py
│       ├── models.py     # 共通モデル
│       ├── mixins.py     # 共通Mixin
│       ├── utils.py      # ユーティリティ関数
│       ├── validators.py # カスタムバリデーター
│       ├── exceptions.py # カスタム例外
│       └── constants.py  # 定数定義
├── static/               # 静的ファイル
├── media/                # アップロードファイル
├── templates/            # HTMLテンプレート
├── locale/               # 国際化ファイル
├── docs/                 # ドキュメント
├── scripts/              # 管理スクリプト
├── tests/                # プロジェクト横断テスト
└── deployment/           # デプロイ関連ファイル
    ├── docker/
    ├── kubernetes/
    └── nginx/
```

#### **FastAPIプロジェクト構造**
```
project-name/
├── main.py               # アプリケーションエントリーポイント
├── requirements/         # 依存関係管理
├── app/                  # メインアプリケーション
│   ├── __init__.py
│   ├── core/             # コア機能
│   │   ├── __init__.py
│   │   ├── config.py     # 設定管理
│   │   ├── database.py   # データベース設定
│   │   ├── security.py   # セキュリティ設定
│   │   ├── exceptions.py # カスタム例外
│   │   └── middleware.py # ミドルウェア
│   ├── api/              # API関連
│   │   ├── __init__.py
│   │   ├── dependencies.py # 依存性注入
│   │   └── v1/           # APIバージョン1
│   │       ├── __init__.py
│   │       ├── router.py  # ルーター統合
│   │       └── endpoints/ # エンドポイント群
│   │           ├── __init__.py
│   │           ├── users.py
│   │           ├── orders.py
│   │           └── auth.py
│   ├── models/           # データモデル
│   │   ├── __init__.py
│   │   ├── user.py
│   │   ├── order.py
│   │   └── base.py       # 基底モデル
│   ├── schemas/          # Pydanticスキーマ
│   │   ├── __init__.py
│   │   ├── user.py
│   │   ├── order.py
│   │   └── base.py
│   ├── services/         # ビジネスロジック
│   │   ├── __init__.py
│   │   ├── user_service.py
│   │   ├── order_service.py
│   │   └── base_service.py
│   ├── repositories/     # データアクセス層
│   │   ├── __init__.py
│   │   ├── user_repository.py
│   │   ├── order_repository.py
│   │   └── base_repository.py
│   └── utils/            # ユーティリティ
│       ├── __init__.py
│       ├── validators.py
│       ├── helpers.py
│       └── constants.py
├── tests/                # テストファイル群
│   ├── __init__.py
│   ├── conftest.py       # pytest設定
│   ├── test_main.py
│   ├── unit/             # 単体テスト
│   ├── integration/      # 統合テスト
│   └── fixtures/         # テストデータ
├── alembic/              # データベースマイグレーション
├── scripts/              # 管理スクリプト
└── deployment/           # デプロイ関連
```

#### **AI/MLプロジェクト構造**
```
ml-project/
├── README.md
├── requirements/
├── data/                 # データファイル
│   ├── raw/             # 生データ
│   ├── processed/       # 前処理済みデータ
│   ├── interim/         # 中間データ
│   └── external/        # 外部データ
├── notebooks/           # Jupyter Notebooks
│   ├── 01-data-exploration.ipynb
│   ├── 02-data-preprocessing.ipynb
│   ├── 03-model-training.ipynb
│   └── 04-model-evaluation.ipynb
├── src/                 # ソースコード
│   ├── __init__.py
│   ├── data/            # データ処理
│   │   ├── __init__.py
│   │   ├── collectors.py    # データ収集
│   │   ├── preprocessors.py # 前処理
│   │   └── validators.py    # データ検証
│   ├── features/        # 特徴量エンジニアリング
│   │   ├── __init__.py
│   │   ├── extractors.py
│   │   ├── selectors.py
│   │   └── transformers.py
│   ├── models/          # モデル定義
│   │   ├── __init__.py
│   │   ├── base_model.py
│   │   ├── classifiers.py
│   │   └── regressors.py
│   ├── training/        # 学習関連
│   │   ├── __init__.py
│   │   ├── trainers.py
│   │   ├── evaluators.py
│   │   └── optimizers.py
│   ├── inference/       # 推論関連
│   │   ├── __init__.py
│   │   ├── predictors.py
│   │   └── explainers.py
│   └── utils/          # ユーティリティ
│       ├── __init__.py
│       ├── config.py
│       ├── logging.py
│       └── metrics.py
├── models/             # 学習済みモデル
├── reports/            # レポート・結果
│   ├── figures/        # 図表
│   └── metrics/        # 評価指標
├── tests/              # テスト
└── deployment/         # モデルデプロイ関連
    ├── api/           # REST API
    ├── batch/         # バッチ処理
    └── monitoring/    # モニタリング
```

### 3.2 モジュール設計原則

#### **適切なモジュール分割**
```python
# ✅ Good: 責任が明確に分離されたモジュール設計

# models/user.py - データモデル定義
from sqlalchemy import Column, Integer, String, Boolean, DateTime
from sqlalchemy.ext.declarative import declarative_base
from datetime import datetime

Base = declarative_base()

class User(Base):
    """ユーザーエンティティ"""
    __tablename__ = 'users'
    
    id = Column(Integer, primary_key=True)
    email = Column(String(255), unique=True, nullable=False)
    first_name = Column(String(100), nullable=False)
    last_name = Column(String(100), nullable=False)
    is_active = Column(Boolean, default=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    
    def __repr__(self) -> str:
        return f"<User(id={self.id}, email='{self.email}')>"

# repositories/user_repository.py - データアクセス層
from typing import List, Optional
from sqlalchemy.orm import Session
from models.user import User

class UserRepository:
    """ユーザーデータの永続化を担当"""
    
    def __init__(self, db_session: Session) -> None:
        self._db = db_session
    
    def create(self, user: User) -> User:
        """ユーザー作成"""
        self._db.add(user)
        self._db.commit()
        self._db.refresh(user)
        return user
    
    def find_by_id(self, user_id: int) -> Optional[User]:
        """IDによる検索"""
        return self._db.query(User).filter(User.id == user_id).first()
    
    def find_by_email(self, email: str) -> Optional[User]:
        """メールアドレスによる検索"""
        return self._db.query(User).filter(User.email == email).first()
    
    def find_active_users(self) -> List[User]:
        """アクティブユーザー一覧取得"""
        return self._db.query(User).filter(User.is_active == True).all()
    
    def update(self, user: User) -> User:
        """ユーザー更新"""
        self._db.commit()
        self._db.refresh(user)
        return user
    
    def delete(self, user: User) -> None:
        """ユーザー削除"""
        self._db.delete(user)
        self._db.commit()

# services/user_service.py - ビジネスロジック層
from typing import Dict, Any, List, Optional
from repositories.user_repository import UserRepository
from models.user import User
from utils.validators import validate_email
from utils.exceptions import ValidationError, DuplicateEmailError

class UserService:
    """ユーザー関連のビジネスロジック"""
    
    def __init__(self, user_repository: UserRepository) -> None:
        self._repository = user_repository
    
    def create_user(self, user_data: Dict[str, Any]) -> User:
        """新規ユーザー作成"""
        # バリデーション
        self._validate_user_data(user_data)
        
        # 重複チェック
        existing_user = self._repository.find_by_email(user_data['email'])
        if existing_user:
            raise DuplicateEmailError(f"Email already exists: {user_data['email']}")
        
        # ユーザー作成
        user = User(
            email=user_data['email'].lower(),
            first_name=user_data['first_name'],
            last_name=user_data['last_name']
        )
        
        return self._repository.create(user)
    
    def get_user_by_id(self, user_id: int) -> Optional[User]:
        """IDによるユーザー取得"""
        return self._repository.find_by_id(user_id)
    
    def update_user(self, user_id: int, updates: Dict[str, Any]) -> User:
        """ユーザー情報更新"""
        user = self._repository.find_by_id(user_id)
        if not user:
            raise ValueError(f"User not found: {user_id}")
        
        # 更新可能フィールドのみ更新
        updateable_fields = ['first_name', 'last_name', 'is_active']
        for field, value in updates.items():
            if field in updateable_fields:
                setattr(user, field, value)
        
        return self._repository.update(user)
    
    def get_active_users(self) -> List[User]:
        """アクティブユーザー一覧取得"""
        return self._repository.find_active_users()
    
    def _validate_user_data(self, user_data: Dict[str, Any]) -> None:
        """ユーザーデータバリデーション"""
        required_fields = ['email', 'first_name', 'last_name']
        
        for field in required_fields:
            if field not in user_data or not user_data[field]:
                raise ValidationError(f"Missing required field: {field}")
        
        if not validate_email(user_data['email']):
            raise ValidationError(f"Invalid email format: {user_data['email']}")

# api/endpoints/users.py - API層（FastAPI例）
from fastapi import APIRouter, Depends, HTTPException, status
from typing import List
from services.user_service import UserService
from schemas.user import UserCreate, UserResponse, UserUpdate
from api.dependencies import get_user_service

router = APIRouter(prefix="/users", tags=["users"])

@router.post("/", response_model=UserResponse, status_code=status.HTTP_201_CREATED)
async def create_user(
    user_data: UserCreate,
    user_service: UserService = Depends(get_user_service)
) -> UserResponse:
    """新規ユーザー作成"""
    try:
        user = user_service.create_user(user_data.dict())
        return UserResponse.from_orm(user)
    except ValidationError as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=str(e)
        )
    except DuplicateEmailError as e:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=str(e)
        )

@router.get("/{user_id}", response_model=UserResponse)
async def get_user(
    user_id: int,
    user_service: UserService = Depends(get_user_service)
) -> UserResponse:
    """ユーザー詳細取得"""
    user = user_service.get_user_by_id(user_id)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"User not found: {user_id}"
        )
    return UserResponse.from_orm(user)

@router.get("/", response_model=List[UserResponse])
async def list_active_users(
    user_service: UserService = Depends(get_user_service)
) -> List[UserResponse]:
    """アクティブユーザー一覧取得"""
    users = user_service.get_active_users()
    return [UserResponse.from_orm(user) for user in users]
```

---

続きは次のメッセージで提供いたします。Python規約の第4章以降を作成中です。
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

## 5. テスト戦略・品質保証

### 5.1 単体テスト（Unit Test）

#### **pytest + fixtures活用**
```python
# ✅ Good: 包括的な単体テスト
# conftest.py - テスト設定・共通フィクスチャ
import pytest
from unittest.mock import Mock, MagicMock
from typing import Generator, Dict, Any
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, Session
from models.base import Base
from models.user import User
from repositories.user_repository import UserRepository
from services.user_service import UserService

# データベーステストセットアップ
@pytest.fixture(scope="session")
def test_engine():
    """テスト用データベースエンジン"""
    engine = create_engine("sqlite:///:memory:", echo=False)
    Base.metadata.create_all(engine)
    return engine

@pytest.fixture
def db_session(test_engine) -> Generator[Session, None, None]:
    """テスト用データベースセッション"""
    TestSessionLocal = sessionmaker(bind=test_engine)
    session = TestSessionLocal()
    
    try:
        yield session
    finally:
        session.rollback()
        session.close()

# モックオブジェクト
@pytest.fixture
def mock_email_service() -> Mock:
    """メールサービスモック"""
    mock = Mock()
    mock.send_welcome_email.return_value = True
    return mock

# テストデータファクトリ
@pytest.fixture
def user_data() -> Dict[str, Any]:
    """テスト用ユーザーデータ"""
    return {
        'email': 'test@example.com',
        'first_name': 'Test',
        'last_name': 'User'
    }

@pytest.fixture
def sample_user(db_session: Session) -> User:
    """サンプルユーザー作成"""
    user = User(
        email='existing@example.com',
        first_name='Existing',
        last_name='User',
        is_active=True
    )
    db_session.add(user)
    db_session.commit()
    db_session.refresh(user)
    return user

# テストクラス例
# tests/test_user_service.py
import pytest
from unittest.mock import patch, Mock
from services.user_service import UserService
from repositories.user_repository import UserRepository
from utils.exceptions import ValidationError, DuplicateEmailError, UserNotFoundError

class TestUserService:
    """UserServiceのテストクラス"""
    
    @pytest.fixture(autouse=True)
    def setup(self, db_session: Session, mock_email_service: Mock) -> None:
        """各テストメソッド実行前のセットアップ"""
        self.repository = UserRepository(db_session)
        self.service = UserService(self.repository, mock_email_service)
        self.mock_email_service = mock_email_service
    
    def test_create_user_success(self, user_data: Dict[str, Any]) -> None:
        """ユーザー作成成功テスト"""
        # Given
        assert self.repository.find_by_email(user_data['email']) is None
        
        # When
        user = self.service.create_user(user_data)
        
        # Then
        assert user.id is not None
        assert user.email == user_data['email'].lower()
        assert user.first_name == user_data['first_name']
        assert user.last_name == user_data['last_name']
        assert user.is_active is True
        
        # ウェルカムメール送信の確認
        self.mock_email_service.send_welcome_email.assert_called_once_with(user)
    
    def test_create_user_duplicate_email(self, user_data: Dict[str, Any], sample_user: User) -> None:
        """重複メールアドレスでのユーザー作成テスト"""
        # Given
        user_data['email'] = sample_user.email
        
        # When & Then
        with pytest.raises(DuplicateEmailError) as exc_info:
            self.service.create_user(user_data)
        
        assert str(exc_info.value) == f"Email already exists: {sample_user.email}"
        self.mock_email_service.send_welcome_email.assert_not_called()
    
    @pytest.mark.parametrize("invalid_data,expected_error", [
        ({'first_name': 'Test', 'last_name': 'User'}, "Missing required field: email"),
        ({'email': 'test@example.com', 'last_name': 'User'}, "Missing required field: first_name"),
        ({'email': 'test@example.com', 'first_name': 'Test'}, "Missing required field: last_name"),
        ({'email': 'invalid-email', 'first_name': 'Test', 'last_name': 'User'}, "Invalid email format"),
        ({'email': '', 'first_name': 'Test', 'last_name': 'User'}, "Missing required field: email"),
    ])
    def test_create_user_validation_errors(
        self, 
        invalid_data: Dict[str, Any], 
        expected_error: str
    ) -> None:
        """ユーザー作成バリデーションエラーテスト"""
        # When & Then
        with pytest.raises(ValidationError) as exc_info:
            self.service.create_user(invalid_data)
        
        assert expected_error in str(exc_info.value)
        self.mock_email_service.send_welcome_email.assert_not_called()
    
    def test_get_user_by_id_success(self, sample_user: User) -> None:
        """IDによるユーザー取得成功テスト"""
        # When
        user = self.service.get_user_by_id(sample_user.id)
        
        # Then
        assert user.id == sample_user.id
        assert user.email == sample_user.email
    
    def test_get_user_by_id_not_found(self) -> None:
        """存在しないIDでのユーザー取得テスト"""
        # When & Then
        with pytest.raises(UserNotFoundError) as exc_info:
            self.service.get_user_by_id(999)
        
        assert str(exc_info.value) == "User not found: 999"
    
    def test_update_user_success(self, sample_user: User) -> None:
        """ユーザー更新成功テスト"""
        # Given
        updates = {
            'first_name': 'Updated',
            'last_name': 'Name',
            'is_active': False
        }
        
        # When
        updated_user = self.service.update_user(sample_user.id, updates)
        
        # Then
        assert updated_user.first_name == 'Updated'
        assert updated_user.last_name == 'Name'
        assert updated_user.is_active is False
        assert updated_user.email == sample_user.email  # 変更されない
    
    def test_update_user_not_found(self) -> None:
        """存在しないユーザーの更新テスト"""
        # When & Then
        with pytest.raises(ValueError) as exc_info:
            self.service.update_user(999, {'first_name': 'Test'})
        
        assert str(exc_info.value) == "User not found: 999"
    
    def test_get_active_users(self, db_session: Session) -> None:
        """アクティブユーザー一覧取得テスト"""
        # Given - テストデータ作成
        active_user1 = User(email='active1@example.com', first_name='Active', last_name='User1', is_active=True)
        active_user2 = User(email='active2@example.com', first_name='Active', last_name='User2', is_active=True)
        inactive_user = User(email='inactive@example.com', first_name='Inactive', last_name='User', is_active=False)
        
        db_session.add_all([active_user1, active_user2, inactive_user])
        db_session.commit()
        
        # When
        active_users = self.service.get_active_users()
        
        # Then
        assert len(active_users) == 2
        assert all(user.is_active for user in active_users)
        emails = [user.email for user in active_users]
        assert 'active1@example.com' in emails
        assert 'active2@example.com' in emails
        assert 'inactive@example.com' not in emails
    
    @patch('services.user_service.UserService._hash_password')
    def test_update_user_password_success(self, mock_hash: Mock, sample_user: User) -> None:
        """パスワード更新成功テスト"""
        # Given
        mock_hash.return_value = 'hashed_password'
        new_password = 'NewSecurePassword123!'
        
        # When
        self.service.update_user_password(sample_user.id, new_password)
        
        # Then
        mock_hash.assert_called_once_with(new_password)
        # パスワードハッシュが更新されることを確認
        updated_user = self.repository.find_by_id(sample_user.id)
        assert updated_user.password_hash == 'hashed_password'
    
    def test_email_service_failure_does_not_affect_user_creation(self, user_data: Dict[str, Any]) -> None:
        """メール送信失敗がユーザー作成に影響しないことのテスト"""
        # Given
        self.mock_email_service.send_welcome_email.side_effect = Exception("Email service unavailable")
        
        # When
        user = self.service.create_user(user_data)
        
        # Then - ユーザー作成は成功している
        assert user.id is not None
        assert user.email == user_data['email'].lower()
        
        # メール送信は試行されている
        self.mock_email_service.send_welcome_email.assert_called_once()

# parametrize を使ったデータ駆動テスト
class TestUserValidation:
    """ユーザーバリデーションのテスト"""
    
    @pytest.mark.parametrize("email,expected_valid", [
        ('valid@example.com', True),
        ('user.name@example.co.jp', True),
        ('test+tag@example-domain.com', True),
        ('invalid-email', False),
        ('missing@domain', False),
        ('@missing-local.com', False),
        ('spaces @example.com', False),
        ('', False),
    ])
    def test_email_validation(self, email: str, expected_valid: bool) -> None:
        """メールアドレスバリデーションテスト"""
        from utils.validators import validate_email
        
        result = validate_email(email)
        assert result == expected_valid

### 5.2 統合テスト・Django統合テスト

#### **Django REST Framework統合テスト**
```python
# ✅ Good: DRF統合テスト
# tests/test_api_integration.py
import pytest
from django.test import TestCase, TransactionTestCase
from django.urls import reverse
from rest_framework.test import APITestCase, APIClient
from rest_framework import status
from django.contrib.auth import get_user_model
from django.db import transaction
from unittest.mock import patch, Mock
from typing import Dict, Any
import json

User = get_user_model()

class UserAPIIntegrationTest(APITestCase):
    """ユーザーAPI統合テスト"""
    
    def setUp(self) -> None:
        """テストセットアップ"""
        self.client = APIClient()
        
        # テストユーザー作成
        self.admin_user = User.objects.create_user(
            email='admin@example.com',
            password='admin_password',
            first_name='Admin',
            last_name='User',
            is_staff=True
        )
        
        self.regular_user = User.objects.create_user(
            email='user@example.com',
            password='user_password',
            first_name='Regular',
            last_name='User'
        )
        
        # API エンドポイント
        self.users_url = reverse('api:users-list')
        self.user_detail_url = lambda pk: reverse('api:users-detail', kwargs={'pk': pk})
    
    def test_create_user_success(self) -> None:
        """ユーザー作成成功テスト"""
        # Given
        user_data = {
            'email': 'newuser@example.com',
            'password': 'SecurePassword123!',
            'first_name': 'New',
            'last_name': 'User'
        }
        
        # When
        response = self.client.post(self.users_url, user_data, format='json')
        
        # Then
        assert response.status_code == status.HTTP_201_CREATED
        
        response_data = response.json()
        assert response_data['email'] == user_data['email']
        assert response_data['first_name'] == user_data['first_name']
        assert response_data['last_name'] == user_data['last_name']
        assert 'password' not in response_data  # パスワードは返却されない
        
        # データベースに保存されている確認
        user = User.objects.get(email=user_data['email'])
        assert user.first_name == user_data['first_name']
        assert user.check_password(user_data['password'])  # パスワードハッシュ確認
    
    def test_create_user_validation_error(self) -> None:
        """ユーザー作成バリデーションエラーテスト"""
        # Given - 無効なデータ
        invalid_data = {
            'email': 'invalid-email',
            'password': '123',  # 弱いパスワード
            'first_name': '',
            'last_name': 'User'
        }
        
        # When
        response = self.client.post(self.users_url, invalid_data, format='json')
        
        # Then
        assert response.status_code == status.HTTP_400_BAD_REQUEST
        
        response_data = response.json()
        assert 'error' in response_data
        assert 'details' in response_data
        
        # 具体的なバリデーションエラーの確認
        error_fields = [detail['field'] for detail in response_data['details']]
        assert 'email' in error_fields
        assert 'password' in error_fields
        assert 'first_name' in error_fields
    
    def test_create_user_duplicate_email(self) -> None:
        """重複メールアドレスでのユーザー作成テスト"""
        # Given
        duplicate_data = {
            'email': self.regular_user.email,  # 既存ユーザーのメール
            'password': 'SecurePassword123!',
            'first_name': 'Duplicate',
            'last_name': 'User'
        }
        
        # When
        response = self.client.post(self.users_url, duplicate_data, format='json')
        
        # Then
        assert response.status_code == status.HTTP_409_CONFLICT
        
        response_data = response.json()
        assert response_data['error'] == 'DuplicateEmailError'
        assert self.regular_user.email in response_data['message']
    
    def test_get_user_list_unauthorized(self) -> None:
        """未認証でのユーザー一覧取得テスト"""
        # When
        response = self.client.get(self.users_url)
        
        # Then
        assert response.status_code == status.HTTP_401_UNAUTHORIZED
    
    def test_get_user_list_authorized(self) -> None:
        """認証済みでのユーザー一覧取得テスト"""
        # Given
        self.client.force_authenticate(user=self.admin_user)
        
        # When
        response = self.client.get(self.users_url)
        
        # Then
        assert response.status_code == status.HTTP_200_OK
        
        response_data = response.json()
        assert isinstance(response_data, list)
        assert len(response_data) >= 2  # admin_user + regular_user
        
        emails = [user['email'] for user in response_data]
        assert self.admin_user.email in emails
        assert self.regular_user.email in emails
    
    def test_get_user_detail_success(self) -> None:
        """ユーザー詳細取得成功テスト"""
        # Given
        self.client.force_authenticate(user=self.admin_user)
        
        # When
        response = self.client.get(self.user_detail_url(self.regular_user.pk))
        
        # Then
        assert response.status_code == status.HTTP_200_OK
        
        response_data = response.json()
        assert response_data['id'] == self.regular_user.pk
        assert response_data['email'] == self.regular_user.email
        assert response_data['first_name'] == self.regular_user.first_name
    
    def test_get_user_detail_not_found(self) -> None:
        """存在しないユーザー詳細取得テスト"""
        # Given
        self.client.force_authenticate(user=self.admin_user)
        
        # When
        response = self.client.get(self.user_detail_url(999))
        
        # Then
        assert response.status_code == status.HTTP_404_NOT_FOUND
        
        response_data = response.json()
        assert response_data['error'] == 'UserNotFoundError'
    
    def test_update_user_success(self) -> None:
        """ユーザー更新成功テスト"""
        # Given
        self.client.force_authenticate(user=self.admin_user)
        update_data = {
            'first_name': 'Updated',
            'last_name': 'Name'
        }
        
        # When
        response = self.client.patch(
            self.user_detail_url(self.regular_user.pk),
            update_data,
            format='json'
        )
        
        # Then
        assert response.status_code == status.HTTP_200_OK
        
        response_data = response.json()
        assert response_data['first_name'] == 'Updated'
        assert response_data['last_name'] == 'Name'
        assert response_data['email'] == self.regular_user.email  # 変更されない
        
        # データベースでも更新されている確認
        self.regular_user.refresh_from_db()
        assert self.regular_user.first_name == 'Updated'
        assert self.regular_user.last_name == 'Name'
    
    def test_delete_user_success(self) -> None:
        """ユーザー削除成功テスト"""
        # Given
        self.client.force_authenticate(user=self.admin_user)
        user_to_delete = User.objects.create_user(
            email='delete@example.com',
            password='password',
            first_name='Delete',
            last_name='User'
        )
        
        # When
        response = self.client.delete(self.user_detail_url(user_to_delete.pk))
        
        # Then
        assert response.status_code == status.HTTP_204_NO_CONTENT
        
        # データベースから削除されている確認
        assert not User.objects.filter(pk=user_to_delete.pk).exists()
    
    @patch('services.email_service.EmailService.send_welcome_email')
    def test_create_user_with_email_service_mock(self, mock_send_email: Mock) -> None:
        """メールサービスモック使用のユーザー作成テスト"""
        # Given
        mock_send_email.return_value = True
        user_data = {
            'email': 'mocktest@example.com',
            'password': 'SecurePassword123!',
            'first_name': 'Mock',
            'last_name': 'Test'
        }
        
        # When
        response = self.client.post(self.users_url, user_data, format='json')
        
        # Then
        assert response.status_code == status.HTTP_201_CREATED
        
        # メールサービスが呼ばれていることを確認
        mock_send_email.assert_called_once()
        
        # 作成されたユーザーがメールサービスに渡されていることを確認
        called_user = mock_send_email.call_args[0][0]
        assert called_user.email == user_data['email']

class OrderAPIIntegrationTest(APITestCase):
    """注文API統合テスト"""
    
    def setUp(self) -> None:
        """テストセットアップ"""
        self.client = APIClient()
        
        # テストユーザーと商品データ
        self.user = User.objects.create_user(
            email='customer@example.com',
            password='password'
        )
        
        from models.product import Product
        self.product = Product.objects.create(
            name='Test Product',
            price=Decimal('100.00'),
            stock=10
        )
        
        self.orders_url = reverse('api:orders-list')
    
    def test_create_order_success(self) -> None:
        """注文作成成功テスト"""
        # Given
        self.client.force_authenticate(user=self.user)
        order_data = {
            'items': [
                {
                    'product_id': self.product.id,
                    'quantity': 2
                }
            ]
        }
        
        # When
        response = self.client.post(self.orders_url, order_data, format='json')
        
        # Then
        assert response.status_code == status.HTTP_201_CREATED
        
        response_data = response.json()
        assert response_data['user_id'] == self.user.id
        assert len(response_data['items']) == 1
        assert response_data['total_amount'] == '200.00'  # 100 * 2
        
        # 在庫が減っていることを確認
        self.product.refresh_from_db()
        assert self.product.stock == 8  # 10 - 2
    
    def test_create_order_insufficient_stock(self) -> None:
        """在庫不足での注文作成テスト"""
        # Given
        self.client.force_authenticate(user=self.user)
        order_data = {
            'items': [
                {
                    'product_id': self.product.id,
                    'quantity': 15  # 在庫（10）を超える量
                }
            ]
        }
        
        # When
        response = self.client.post(self.orders_url, order_data, format='json')
        
        # Then
        assert response.status_code == status.HTTP_400_BAD_REQUEST
        
        response_data = response.json()
        assert response_data['error'] == 'InsufficientInventoryError'
        assert 'insufficient inventory' in response_data['message'].lower()
        
        # 在庫は変更されていないことを確認
        self.product.refresh_from_db()
        assert self.product.stock == 10

class DatabaseTransactionTest(TransactionTestCase):
    """データベーストランザクションテスト"""
    
    def test_rollback_on_error(self) -> None:
        """エラー発生時のロールバックテスト"""
        # Given
        initial_user_count = User.objects.count()
        
        # When - トランザクション内でエラーを発生させる
        with pytest.raises(Exception):
            with transaction.atomic():
                User.objects.create_user(
                    email='test1@example.com',
                    password='password'
                )
                User.objects.create_user(
                    email='test2@example.com',
                    password='password'
                )
                # 意図的にエラーを発生
                raise Exception("Intentional error for rollback test")
        
        # Then - ユーザーは作成されていない（ロールバックされた）
        assert User.objects.count() == initial_user_count
    
    def test_commit_on_success(self) -> None:
        """成功時のコミットテスト"""
        # Given
        initial_user_count = User.objects.count()
        
        # When
        with transaction.atomic():
            User.objects.create_user(
                email='commit1@example.com',
                password='password'
            )
            User.objects.create_user(
                email='commit2@example.com',
                password='password'
            )
        
        # Then - ユーザーが作成されている
        assert User.objects.count() == initial_user_count + 2
        assert User.objects.filter(email='commit1@example.com').exists()
        assert User.objects.filter(email='commit2@example.com').exists()
```

### 5.3 Factory Boyによるテストデータ管理

#### **テストデータファクトリ設計**
```python
# ✅ Good: Factory Boyを使ったテストデータ管理
# tests/factories.py
import factory
from factory import fuzzy
from faker import Faker
from django.contrib.auth import get_user_model
from decimal import Decimal
from datetime import datetime, timedelta
from typing import Any, Sequence

fake = Faker('ja_JP')  # 日本語ロケール
User = get_user_model()

class UserFactory(factory.django.DjangoModelFactory):
    """ユーザーファクトリ"""
    
    class Meta:
        model = User
        django_get_or_create = ('email',)  # 重複防止
    
    # 基本属性
    email = factory.Sequence(lambda n: f'user{n}@example.com')
    first_name = factory.LazyAttribute(lambda obj: fake.first_name())
    last_name = factory.LazyAttribute(lambda obj: fake.last_name())
    is_active = True
    is_staff = False
    is_superuser = False
    date_joined = factory.LazyFunction(datetime.now)
    
    # パスワード設定
    @factory.post_generation
    def password(self, create: bool, extracted: Any, **kwargs) -> None:
        if not create:
            return
        
        if extracted:
            self.set_password(extracted)
        else:
            self.set_password('default_password')

class AdminUserFactory(UserFactory):
    """管理者ユーザーファクトリ"""
    
    email = factory.Sequence(lambda n: f'admin{n}@example.com')
    first_name = 'Admin'
    is_staff = True
    is_superuser = True

class InactiveUserFactory(UserFactory):
    """非アクティブユーザーファクトリ"""
    
    email = factory.Sequence(lambda n: f'inactive{n}@example.com')
    is_active = False

class ProductFactory(factory.django.DjangoModelFactory):
    """商品ファクトリ"""
    
    class Meta:
        model = 'products.Product'
    
    name = factory.LazyAttribute(lambda obj: fake.catch_phrase())
    description = factory.LazyAttribute(lambda obj: fake.text(max_nb_chars=500))
    price = factory.LazyAttribute(
        lambda obj: Decimal(fake.pydecimal(left_digits=3, right_digits=2, positive=True))
    )
    stock = fuzzy.FuzzyInteger(1, 100)
    category = fuzzy.FuzzyChoice(['electronics', 'clothing', 'books', 'home', 'sports'])
    is_available = True
    created_at = factory.LazyFunction(datetime.now)
    updated_at = factory.LazyFunction(datetime.now)

class OrderFactory(factory.django.DjangoModelFactory):
    """注文ファクトリ"""
    
    class Meta:
        model = 'orders.Order'
    
    user = factory.SubFactory(UserFactory)
    status = fuzzy.FuzzyChoice(['pending', 'processing', 'shipped', 'delivered', 'cancelled'])
    total_amount = factory.LazyAttribute(
        lambda obj: Decimal(fake.pydecimal(left_digits=4, right_digits=2, positive=True))
    )
    shipping_address = factory.LazyAttribute(lambda obj: fake.address())
    created_at = factory.LazyFunction(datetime.now)
    updated_at = factory.LazyFunction(datetime.now)
    
    # 配送日は注文日の1-7日後
    estimated_delivery = factory.LazyAttribute(
        lambda obj: obj.created_at + timedelta(days=fake.random_int(min=1, max=7))
    )

class OrderItemFactory(factory.django.DjangoModelFactory):
    """注文アイテムファクトリ"""
    
    class Meta:
        model = 'orders.OrderItem'
    
    order = factory.SubFactory(OrderFactory)
    product = factory.SubFactory(ProductFactory)
    quantity = fuzzy.FuzzyInteger(1, 5)
    unit_price = factory.LazyAttribute(
        lambda obj: obj.product.price if obj.product else Decimal('100.00')
    )
    
    @factory.lazy_attribute
    def total_price(self) -> Decimal:
        return self.unit_price * self.quantity

class CompleteOrderFactory(OrderFactory):
    """完全な注文ファクトリ（複数アイテム付き）"""
    
    # 関連オブジェクト作成
    @factory.post_generation
    def items(self, create: bool, extracted: Sequence[Any], **kwargs) -> None:
        if not create:
            return
        
        if extracted:
            # 明示的にアイテムが指定された場合
            for item_data in extracted:
                OrderItemFactory(order=self, **item_data)
        else:
            # デフォルトで2-4個のアイテムを作成
            item_count = fake.random_int(min=2, max=4)
            for _ in range(item_count):
                OrderItemFactory(order=self)
        
        # 合計金額を再計算
        self.total_amount = sum(
            item.total_price for item in self.items.all()
        )
        self.save()

class CategoryFactory(factory.django.DjangoModelFactory):
    """カテゴリファクトリ"""
    
    class Meta:
        model = 'products.Category'
    
    name = factory.LazyAttribute(lambda obj: fake.word().capitalize())
    description = factory.LazyAttribute(lambda obj: fake.sentence())
    is_active = True
    
    # 階層構造対応
    parent = None  # デフォルトはルートカテゴリ

class SubCategoryFactory(CategoryFactory):
    """サブカテゴリファクトリ"""
    
    parent = factory.SubFactory(CategoryFactory)

# ファクトリ使用例
# tests/test_with_factories.py
class TestWithFactories:
    """Factory Boyを使ったテスト例"""
    
    def test_user_creation_with_factory(self) -> None:
        """ファクトリを使ったユーザー作成テスト"""
        # Given
        user = UserFactory()
        
        # Then
        assert user.id is not None
        assert '@example.com' in user.email
        assert user.first_name
        assert user.last_name
        assert user.is_active
    
    def test_admin_user_factory(self) -> None:
        """管理者ユーザーファクトリテスト"""
        # Given
        admin = AdminUserFactory()
        
        # Then
        assert admin.is_staff
        assert admin.is_superuser
        assert 'admin' in admin.email
    
    def test_user_with_custom_attributes(self) -> None:
        """カスタム属性付きユーザー作成テスト"""
        # Given
        user = UserFactory(
            email='custom@example.com',
            first_name='Custom',
            last_name='User'
        )
        
        # Then
        assert user.email == 'custom@example.com'
        assert user.first_name == 'Custom'
        assert user.last_name == 'User'
    
    def test_create_multiple_users(self) -> None:
        """複数ユーザー作成テスト"""
        # Given
        users = UserFactory.create_batch(5)
        
        # Then
        assert len(users) == 5
        emails = [user.email for user in users]
        assert len(set(emails)) == 5  # 全て異なるメールアドレス
    
    def test_product_with_category(self) -> None:
        """カテゴリ付き商品作成テスト"""
        # Given
        category = CategoryFactory(name='Electronics')
        product = ProductFactory(category=category.name)
        
        # Then
        assert product.category == 'Electronics'
        assert product.price > 0
        assert product.stock > 0
    
    def test_complete_order_creation(self) -> None:
        """完全な注文作成テスト"""
        # Given
        order = CompleteOrderFactory()
        
        # Then
        assert order.user is not None
        assert order.items.count() >= 2
        assert order.total_amount > 0
        
        # アイテムの合計が注文総額と一致することを確認
        calculated_total = sum(
            item.total_price for item in order.items.all()
        )
        assert order.total_amount == calculated_total
    
    def test_order_with_specific_items(self) -> None:
        """特定アイテム付き注文作成テスト"""
        # Given
        products = ProductFactory.create_batch(3)
        order = CompleteOrderFactory(
            items=[
                {'product': products[0], 'quantity': 2},
                {'product': products[1], 'quantity': 1},
                {'product': products[2], 'quantity': 3},
            ]
        )
        
        # Then
        assert order.items.count() == 3
        
        # 数量確認
        quantities = [item.quantity for item in order.items.all()]
        assert 2 in quantities
        assert 1 in quantities
        assert 3 in quantities
    
    def test_hierarchical_categories(self) -> None:
        """階層カテゴリテスト"""
        # Given
        parent_category = CategoryFactory(name='Electronics')
        sub_category = SubCategoryFactory(
            name='Smartphones',
            parent=parent_category
        )
        
        # Then
        assert sub_category.parent == parent_category
        assert sub_category.name == 'Smartphones'
        assert parent_category.name == 'Electronics'
    
    @pytest.mark.parametrize('user_count', [1, 5, 10, 20])
    def test_batch_creation_performance(self, user_count: int) -> None:
        """バッチ作成パフォーマンステスト"""
        import time
        
        # Given & When
        start_time = time.time()
        users = UserFactory.create_batch(user_count)
        creation_time = time.time() - start_time
        
        # Then
        assert len(users) == user_count
        # パフォーマンス目安: 1ユーザーあたり0.1秒以内
        assert creation_time < (user_count * 0.1)
    
    def test_factory_with_traits(self) -> None:
        """トレイト（特性）を使ったファクトリテスト"""
        # カスタムトレイト定義例
        class PremiumUserFactory(UserFactory):
            class Meta:
                model = User
            
            @factory.trait
            def premium(self):
                self.is_premium = True
                self.subscription_end = datetime.now() + timedelta(days=365)
            
            @factory.trait
            def with_orders(self):
                self.orders = factory.RelatedFactoryBoy(
                    'tests.factories.OrderFactory',
                    'user',
                    size=3
                )
        
        # Given
        premium_user = PremiumUserFactory(premium=True, with_orders=True)
        
        # Then
        assert hasattr(premium_user, 'is_premium')
        assert premium_user.is_premium
```

---

## 6. パフォーマンス最適化・プロファイリング

### 6.1 パフォーマンス測定・プロファイリング

#### **cProfileとline_profilerによる詳細分析**
```python
# ✅ Good: 包括的なパフォーマンス分析
import cProfile
import pstats
import io
from functools import wraps
from typing import Callable, Any, Dict, List
import time
import psutil
import tracemalloc
from contextlib import contextmanager
from dataclasses import dataclass
from datetime import datetime

@dataclass
class PerformanceMetrics:
    """パフォーマンス測定結果"""
    execution_time: float
    memory_peak: float
    memory_current: float
    cpu_percent: float
    function_calls: int
    timestamp: datetime

class ProfilerManager:
    """プロファイリング管理クラス"""
    
    def __init__(self) -> None:
        self.results: Dict[str, List[PerformanceMetrics]] = {}
    
    @contextmanager
    def profile_execution(self, name: str):
        """実行時間・メモリ使用量の測定"""
        # メモリトレース開始
        tracemalloc.start()
        
        # CPU使用率測定開始
        process = psutil.Process()
        cpu_before = process.cpu_percent()
        
        start_time = time.perf_counter()
        start_memory = tracemalloc.get_traced_memory()[0]
        
        try:
            yield
        finally:
            # 測定終了
            end_time = time.perf_counter()
            current_memory, peak_memory = tracemalloc.get_traced_memory()
            cpu_after = process.cpu_percent()
            
            tracemalloc.stop()
            
            # 結果記録
            metrics = PerformanceMetrics(
                execution_time=end_time - start_time,
                memory_peak=peak_memory / 1024 / 1024,  # MB
                memory_current=current_memory / 1024 / 1024,  # MB
                cpu_percent=(cpu_before + cpu_after) / 2,
                function_calls=0,  # cProfileで後から設定
                timestamp=datetime.now()
            )
            
            if name not in self.results:
                self.results[name] = []
            self.results[name].append(metrics)
    
    def profile_function(self, func: Callable) -> Callable:
        """関数プロファイリングデコレーター"""
        @wraps(func)
        def wrapper(*args, **kwargs) -> Any:
            func_name = f"{func.__module__}.{func.__name__}"
            
            with self.profile_execution(func_name):
                # cProfile実行
                profiler = cProfile.Profile()
                profiler.enable()
                
                try:
                    result = func(*args, **kwargs)
                finally:
                    profiler.disable()
                    
                    # プロファイル結果を文字列に変換
                    s = io.StringIO()
                    ps = pstats.Stats(profiler, stream=s)
                    ps.strip_dirs().sort_stats('cumulative')
                    ps.print_stats(20)  # 上位20件
                    
                    # 関数呼び出し数を更新
                    if self.results[func_name]:
                        self.results[func_name][-1].function_calls = ps.total_calls
                    
                    # 詳細結果をファイルに保存
                    with open(f'profile_{func_name}_{int(time.time())}.txt', 'w') as f:
                        f.write(s.getvalue())
                
                return result
        
        return wrapper
    
    def get_performance_summary(self, name: str) -> Dict[str, Any]:
        """パフォーマンス要約取得"""
        if name not in self.results or not self.results[name]:
            return {}
        
        metrics_list = self.results[name]
        
        return {
            'function_name': name,
            'execution_count': len(metrics_list),
            'avg_execution_time': sum(m.execution_time for m in metrics_list) / len(metrics_list),
            'max_execution_time': max(m.execution_time for m in metrics_list),
            'min_execution_time': min(m.execution_time for m in metrics_list),
            'avg_memory_peak': sum(m.memory_peak for m in metrics_list) / len(metrics_list),
            'max_memory_peak': max(m.memory_peak for m in metrics_list),
            'avg_cpu_percent': sum(m.cpu_percent for m in metrics_list) / len(metrics_list),
            'total_function_calls': sum(m.function_calls for m in metrics_list),
            'latest_timestamp': max(m.timestamp for m in metrics_list)
        }
    
    def export_results(self, filename: str) -> None:
        """結果をJSONファイルにエクスポート"""
        import json
        
        export_data = {}
        for name in self.results:
            export_data[name] = self.get_performance_summary(name)
        
        with open(filename, 'w') as f:
            json.dump(export_data, f, indent=2, default=str)

# グローバルプロファイラーインスタンス
profiler_manager = ProfilerManager()

# 使用例
class DataProcessor:
    """データ処理クラス（パフォーマンス測定例）"""
    
    @profiler_manager.profile_function
    def process_large_dataset(self, data: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """大量データ処理（プロファイリング対象）"""
        results = []
        
        for item in data:
            # 複雑な処理のシミュレーション
            processed_item = self._complex_processing(item)
            results.append(processed_item)
        
        return results
    
    def _complex_processing(self, item: Dict[str, Any]) -> Dict[str, Any]:
        """複雑な処理のシミュレーション"""
        # 計算集約的な処理
        result = {
            'id': item.get('id'),
            'processed_value': sum(range(1000)),  # 意図的に重い処理
            'timestamp': datetime.now().isoformat()
        }
        
        # メモリ使用量増加のシミュレーション
        temp_data = [i ** 2 for i in range(1000)]
        result['computed'] = sum(temp_data)
        
        return result
    
    @profiler_manager.profile_function
    def optimized_process_dataset(self, data: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """最適化されたデータ処理"""
        # リスト内包表記とジェネレータ使用で最適化
        return [
            {
                'id': item.get('id'),
                'processed_value': 499500,  # 事前計算済み
                'timestamp': datetime.now().isoformat(),
                'computed': 332833500  # 事前計算済み
            }
            for item in data
        ]

# line_profilerとの連携
def line_profile_analysis(func: Callable) -> Callable:
    """line_profilerによる行単位分析デコレーター"""
    @wraps(func)
    def wrapper(*args, **kwargs) -> Any:
        import line_profiler
        
        profiler = line_profiler.LineProfiler()
        profiler.add_function(func)
        profiler.enable_by_count()
        
        try:
            result = func(*args, **kwargs)
        finally:
            profiler.disable_by_count()
            
            # 結果をファイルに出力
            output_file = f'line_profile_{func.__name__}_{int(time.time())}.txt'
            with open(output_file, 'w') as f:
                profiler.print_stats(stream=f)
            
            print(f"Line profiler results saved to: {output_file}")
        
        return result
    
    return wrapper

# パフォーマンステスト実行例
def performance_comparison_test() -> None:
    """パフォーマンス比較テスト"""
    processor = DataProcessor()
    
    # テストデータ作成
    test_data = [
        {'id': i, 'value': i * 2}
        for i in range(10000)
    ]
    
    print("Starting performance comparison...")
    
    # 最適化前
    with profiler_manager.profile_execution('unoptimized_processing'):
        result1 = processor.process_large_dataset(test_data)
    
    # 最適化後
    with profiler_manager.profile_execution('optimized_processing'):
        result2 = processor.optimized_process_dataset(test_data)
    
    # 結果比較
    unopt_summary = profiler_manager.get_performance_summary('unoptimized_processing')
    opt_summary = profiler_manager.get_performance_summary('optimized_processing')
    
    print(f"Unoptimized: {unopt_summary['avg_execution_time']:.4f}s")
    print(f"Optimized: {opt_summary['avg_execution_time']:.4f}s")
    
    if unopt_summary['avg_execution_time'] > 0:
        improvement = (
            (unopt_summary['avg_execution_time'] - opt_summary['avg_execution_time'])
            / unopt_summary['avg_execution_time'] * 100
        )
        print(f"Performance improvement: {improvement:.2f}%")
    
    # 結果エクスポート
    profiler_manager.export_results('performance_results.json')

if __name__ == "__main__":
    performance_comparison_test()
```

### 6.2 データベースクエリ最適化

#### **Django ORMクエリ最適化**
```python
# ✅ Good: 効率的なDjango ORMクエリ
from django.db import models
from django.db.models import Prefetch, Count, Sum, Avg, Q, F
from django.core.cache import cache
from typing import List, Optional, Dict, Any
import logging
from functools import wraps

logger = logging.getLogger(__name__)

class QueryOptimizationMixin:
    """クエリ最適化用Mixinクラス"""
    
    @staticmethod
    def log_query_count(func):
        """クエリ数をログ出力するデコレーター"""
        @wraps(func)
        def wrapper(*args, **kwargs):
            from django.db import connection
            
            # クエリカウント開始
            initial_query_count = len(connection.queries)
            
            result = func(*args, **kwargs)
            
            # クエリカウント終了
            final_query_count = len(connection.queries)
            query_count = final_query_count - initial_query_count
            
            logger.info(
                f"Function {func.__name__} executed {query_count} queries",
                extra={'function': func.__name__, 'query_count': query_count}
            )
            
            return result
        return wrapper

class OptimizedUserQuerySet(models.QuerySet):
    """最適化されたユーザークエリセット"""
    
    def with_profile_and_orders(self) -> 'OptimizedUserQuerySet':
        """プロファイルと注文情報を効率的に取得"""
        return self.select_related('profile').prefetch_related(
            Prefetch(
                'orders',
                queryset=Order.objects.select_related('shipping_address')
                .prefetch_related('items__product')
                .order_by('-created_at')
            )
        )
    
    def active_users_with_stats(self) -> 'OptimizedUserQuerySet':
        """統計情報付きアクティブユーザー取得"""
        return self.filter(is_active=True).annotate(
            order_count=Count('orders'),
            total_spent=Sum('orders__total_amount'),
            avg_order_value=Avg('orders__total_amount'),
            last_order_date=models.Max('orders__created_at')
        )
    
    def high_value_customers(self, min_spent: float = 10000) -> 'OptimizedUserQuerySet':
        """高価値顧客の効率的取得"""
        return self.active_users_with_stats().filter(
            total_spent__gte=min_spent
        ).order_by('-total_spent')

class User(models.Model):
    """最適化されたユーザーモデル"""
    
    email = models.EmailField(unique=True, db_index=True)  # インデックス追加
    first_name = models.CharField(max_length=100, db_index=True)
    last_name = models.CharField(max_length=100, db_index=True)
    is_active = models.BooleanField(default=True, db_index=True)  # よく検索される
    created_at = models.DateTimeField(auto_now_add=True, db_index=True)
    updated_at = models.DateTimeField(auto_now=True)
    
    objects = OptimizedUserQuerySet.as_manager()
    
    class Meta:
        indexes = [
            models.Index(fields=['email', 'is_active']),  # 複合インデックス
            models.Index(fields=['created_at', 'is_active']),
            models.Index(fields=['last_name', 'first_name']),  # 名前検索用
        ]

class OptimizedUserService(QueryOptimizationMixin):
    """最適化されたユーザーサービス"""
    
    @QueryOptimizationMixin.log_query_count
    def get_user_dashboard_data(self, user_id: int) -> Dict[str, Any]:
        """ユーザーダッシュボードデータを効率的に取得"""
        
        # 単一クエリでユーザー・プロファイル・統計を取得
        user = User.objects.select_related('profile').prefetch_related(
            Prefetch(
                'orders',
                queryset=Order.objects.select_related('shipping_address')
                .prefetch_related('items__product')
                .order_by('-created_at')[:10]  # 最新10件のみ
            )
        ).annotate(
            total_orders=Count('orders'),
            total_spent=Sum('orders__total_amount'),
            avg_order_value=Avg('orders__total_amount')
        ).get(id=user_id)
        
        return {
            'user_info': {
                'id': user.id,
                'email': user.email,
                'full_name': f"{user.first_name} {user.last_name}",
                'profile': user.profile.__dict__ if hasattr(user, 'profile') else None
            },
            'order_stats': {
                'total_orders': user.total_orders or 0,
                'total_spent': float(user.total_spent or 0),
                'avg_order_value': float(user.avg_order_value or 0)
            },
            'recent_orders': [
                {
                    'id': order.id,
                    'total_amount': float(order.total_amount),
                    'status': order.status,
                    'created_at': order.created_at,
                    'item_count': order.items.count()
                }
                for order in user.orders.all()
            ]
        }
    
    @QueryOptimizationMixin.log_query_count
    def get_paginated_users_with_orders(self, page: int = 1, page_size: int = 20) -> Dict[str, Any]:
        """ページネーション付きユーザー一覧（注文情報含む）"""
        
        offset = (page - 1) * page_size
        
        # カウントクエリの最適化
        total_count = cache.get('total_active_users_count')
        if total_count is None:
            total_count = User.objects.filter(is_active=True).count()
            cache.set('total_active_users_count', total_count, 300)  # 5分キャッシュ
        
        # メインクエリ
        users = User.objects.active_users_with_stats().order_by('-created_at')[offset:offset + page_size]
        
        return {
            'users': [
                {
                    'id': user.id,
                    'email': user.email,
                    'full_name': f"{user.first_name} {user.last_name}",
                    'order_count': user.order_count,
                    'total_spent': float(user.total_spent or 0),
                    'last_order_date': user.last_order_date
                }
                for user in users
            ],
            'pagination': {
                'current_page': page,
                'page_size': page_size,
                'total_count': total_count,
                'total_pages': (total_count + page_size - 1) // page_size
            }
        }
    
    @QueryOptimizationMixin.log_query_count
    def search_users_optimized(self, query: str, filters: Dict[str, Any] = None) -> List[User]:
        """最適化されたユーザー検索"""
        
        # 基本クエリセット
        queryset = User.objects.select_related('profile')
        
        # 検索クエリの処理
        if query:
            # 全文検索のシミュレーション（実際にはPostgreSQL のfull-text searchを使用推奨）
            search_conditions = Q(
                Q(first_name__icontains=query) |
                Q(last_name__icontains=query) |
                Q(email__icontains=query)
            )
            queryset = queryset.filter(search_conditions)
        
        # フィルターの適用
        if filters:
            if 'is_active' in filters:
                queryset = queryset.filter(is_active=filters['is_active'])
            
            if 'created_after' in filters:
                queryset = queryset.filter(created_at__gte=filters['created_after'])
            
            if 'has_orders' in filters and filters['has_orders']:
                queryset = queryset.filter(orders__isnull=False).distinct()
        
        # 結果の並び順とLIMIT
        return queryset.order_by('last_name', 'first_name')[:100]  # 最大100件
    
    @QueryOptimizationMixin.log_query_count
    def bulk_update_user_status(self, user_ids: List[int], is_active: bool) -> int:
        """ユーザーステータス一括更新"""
        
        # bulk_updateを使用して効率的に更新
        updated_count = User.objects.filter(
            id__in=user_ids
        ).update(
            is_active=is_active,
            updated_at=models.F('updated_at')  # 現在時刻に更新
        )
        
        # キャッシュクリア
        cache.delete('total_active_users_count')
        
        return updated_count
    
    @QueryOptimizationMixin.log_query_count
    def get_user_analytics(self, date_from: str, date_to: str) -> Dict[str, Any]:
        """ユーザー分析データ取得"""
        
        # 複数の集計を一つのクエリで実行
        analytics = User.objects.filter(
            created_at__date__range=[date_from, date_to]
        ).aggregate(
            total_users=Count('id'),
            active_users=Count('id', filter=Q(is_active=True)),
            users_with_orders=Count('id', filter=Q(orders__isnull=False)),
            total_revenue=Sum('orders__total_amount'),
            avg_orders_per_user=Avg('orders__total_amount')
        )
        
        # 日別統計（効率的なGROUP BY）
        daily_signups = User.objects.filter(
            created_at__date__range=[date_from, date_to]
        ).extra(
            select={'date': 'DATE(created_at)'}
        ).values('date').annotate(
            count=Count('id')
        ).order_by('date')
        
        return {
            'summary': analytics,
            'daily_signups': list(daily_signups)
        }

# SQLAlchemy最適化例
from sqlalchemy import func, and_, or_
from sqlalchemy.orm import joinedload, selectinload, contains_eager

class OptimizedUserRepository:
    """SQLAlchemy最適化リポジトリ"""
    
    def __init__(self, session: Session) -> None:
        self._session = session
    
    def get_users_with_order_stats(self, limit: int = 100) -> List[User]:
        """注文統計付きユーザー取得（SQLAlchemy）"""
        
        # サブクエリで統計を事前計算
        order_stats = self._session.query(
            Order.user_id,
            func.count(Order.id).label('order_count'),
            func.sum(Order.total_amount).label('total_spent'),
            func.avg(Order.total_amount).label('avg_order_value')
        ).group_by(Order.user_id).subquery()
        
        # メインクエリ
        return self._session.query(User).outerjoin(
            order_stats, User.id == order_stats.c.user_id
        ).options(
            joinedload(User.profile),  # 1:1リレーション
            selectinload(User.orders).joinedload(Order.items)  # 1:Nリレーション
        ).add_columns(
            order_stats.c.order_count,
            order_stats.c.total_spent,
            order_stats.c.avg_order_value
        ).limit(limit).all()
    
    def search_users_with_fulltext(self, search_term: str) -> List[User]:
        """全文検索最適化（PostgreSQL前提）"""
        
        # PostgreSQLのfull-text search使用
        return self._session.query(User).filter(
            func.to_tsvector('english', User.first_name + ' ' + User.last_name + ' ' + User.email)
            .match(search_term)
        ).order_by(
            func.ts_rank(
                func.to_tsvector('english', User.first_name + ' ' + User.last_name),
                func.plainto_tsquery('english', search_term)
            ).desc()
        ).all()
    
    def bulk_insert_users(self, user_data_list: List[Dict[str, Any]]) -> None:
        """ユーザー一括挿入"""
        
        # SQLAlchemyのbulk_insert_mappingsを使用
        self._session.bulk_insert_mappings(User, user_data_list)
        self._session.commit()

# パフォーマンステスト例
def database_performance_test() -> None:
    """データベースクエリパフォーマンステスト"""
    
    service = OptimizedUserService()
    
    # N+1問題のテスト
    print("Testing N+1 query problem...")
    
    # ❌ Bad: N+1クエリ問題
    users = User.objects.all()[:10]
    for user in users:
        print(f"User: {user.email}, Orders: {user.orders.count()}")  # 各ユーザーでクエリ実行
    
    # ✅ Good: 最適化されたクエリ
    users_optimized = User.objects.active_users_with_stats()[:10]
    for user in users_optimized:
        print(f"User: {user.email}, Orders: {user.order_count}")  # 事前に計算済み
    
    # ダッシュボードデータ取得テスト
    print("\nTesting dashboard data retrieval...")
    dashboard_data = service.get_user_dashboard_data(1)
    print(f"Dashboard data retrieved: {len(dashboard_data['recent_orders'])} recent orders")

if __name__ == "__main__":
    database_performance_test()
```

続きます...
    
    @pytest.mark.parametrize("email,expected", [
        ("valid@example.com", True),
        ("user+tag@domain.co.jp", True),
        ("invalid-email", False),
        ("@example.com", False),
        ("user@", False),
        ("", False),
        (None, False),
    ])
    def test_email_validation(self, email: str, expected: bool) -> None:
        """メールアドレスバリデーションテスト"""
        from utils.validators import validate_email
        
        result = validate_email(email)
        assert result == expected
    
    @pytest.mark.parametrize("password,expected", [
        ("SecurePass123!", True),
        ("weakpass", False),
        ("12345678", False),
        ("NoNumbers!", False),
        ("nonumber123", False),
        ("NOLOWERCASE123!", False),
        ("", False),
    ])
    def test_password_strength_validation(self, password: str, expected: bool) -> None:
        """パスワード強度バリデーションテスト"""
        from services.user_service import UserService
        
        service = UserService(Mock(), Mock())
        result = service._is_strong_password(password)
        assert result == expected

# 非同期テスト例
@pytest.mark.asyncio
class TestAsyncUserService:
    """非同期ユーザーサービステスト"""
    
    async def test_async_user_creation(self) -> None:
        """非同期ユーザー作成テスト"""
        # Given
        mock_repo = Mock()
        mock_repo.save_async = Mock(return_value=User(id=1, email='test@example.com'))
        
        service = AsyncUserService(mock_repo)
        
        # When
        user = await service.create_user_async({'email': 'test@example.com'})
        
        # Then
        assert user.id == 1
        mock_repo.save_async.assert_called_once()
```

### 5.2 統合テスト（Integration Test）

#### **Django統合テスト**
```python
# ✅ Good: Django統合テスト
# tests/test_integration.py
import pytest
from django.test import TestCase, TransactionTestCase
from django.contrib.auth import get_user_model
from django.urls import reverse
from rest_framework import status
from rest_framework.test import APITestCase, APIClient
from rest_framework_simplejwt.tokens import RefreshToken
import json

User = get_user_model()

class UserAPIIntegrationTest(APITestCase):
    """ユーザーAPI統合テスト"""
    
    def setUp(self) -> None:
        """テストセットアップ"""
        self.client = APIClient()
        
        # テストユーザー作成
        self.user = User.objects.create_user(
            email='testuser@example.com',
            first_name='Test',
            last_name='User',
            password='testpass123'
        )
        
        # 管理者ユーザー作成
        self.admin_user = User.objects.create_user(
            email='admin@example.com',
            first_name='Admin',
            last_name='User',
            password='adminpass123',
            is_staff=True
        )
    
    def _get_jwt_token(self, user: User) -> str:
        """JWTトークン取得"""
        refresh = RefreshToken.for_user(user)
        return str(refresh.access_token)
    
    def test_user_registration_success(self) -> None:
        """ユーザー登録成功テスト"""
        # Given
        user_data = {
            'email': 'newuser@example.com',
            'first_name': 'New',
            'last_name': 'User',
            'password': 'newuserpass123'
        }
        
        # When
        response = self.client.post(
            reverse('user-list'),
            data=json.dumps(user_data),
            content_type='application/json'
        )
        
        # Then
        assert response.status_code == status.HTTP_201_CREATED
        
        response_data = response.json()
        assert response_data['email'] == user_data['email']
        assert response_data['first_name'] == user_data['first_name']
        assert 'password' not in response_data  # パスワードは返却されない
        
        # データベース確認
        assert User.objects.filter(email=user_data['email']).exists()
    
    def test_user_registration_duplicate_email(self) -> None:
        """重複メールアドレス登録テスト"""
        # Given
        user_data = {
            'email': self.user.email,  # 既存ユーザーのメール
            'first_name': 'Duplicate',
            'last_name': 'User',
            'password': 'duplicatepass123'
        }
        
        # When
        response = self.client.post(
            reverse('user-list'),
            data=json.dumps(user_data),
            content_type='application/json'
        )
        
        # Then
        assert response.status_code == status.HTTP_409_CONFLICT
        
        response_data = response.json()
        assert 'already exists' in response_data['message'].lower()
    
    def test_get_user_authenticated(self) -> None:
        """認証済みユーザー取得テスト"""
        # Given
        token = self._get_jwt_token(self.user)
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {token}')
        
        # When
        response = self.client.get(reverse('user-detail', args=[self.user.id]))
        
        # Then
        assert response.status_code == status.HTTP_200_OK
        
        response_data = response.json()
        assert response_data['id'] == self.user.id
        assert response_data['email'] == self.user.email
    
    def test_get_user_unauthenticated(self) -> None:
        """未認証ユーザー取得テスト"""
        # When
        response = self.client.get(reverse('user-detail', args=[self.user.id]))
        
        # Then
        assert response.status_code == status.HTTP_401_UNAUTHORIZED
    
    def test_get_other_user_forbidden(self) -> None:
        """他ユーザー情報取得禁止テスト"""
        # Given
        other_user = User.objects.create_user(
            email='other@example.com',
            first_name='Other',
            last_name='User',
            password='otherpass123'
        )
        
        token = self._get_jwt_token(self.user)
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {token}')
        
        # When
        response = self.client.get(reverse('user-detail', args=[other_user.id]))
        
        # Then
        assert response.status_code == status.HTTP_403_FORBIDDEN
    
    def test_admin_can_access_all_users(self) -> None:
        """管理者による全ユーザーアクセステスト"""
        # Given
        token = self._get_jwt_token(self.admin_user)
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {token}')
        
        # When
        response = self.client.get(reverse('user-detail', args=[self.user.id]))
        
        # Then
        assert response.status_code == status.HTTP_200_OK
        
        response_data = response.json()
        assert response_data['id'] == self.user.id
    
    def test_user_list_pagination(self) -> None:
        """ユーザー一覧ページネーションテスト"""
        # Given - 複数ユーザー作成
        users = []
        for i in range(15):
            user = User.objects.create_user(
                email=f'user{i}@example.com',
                first_name=f'User{i}',
                last_name='Test',
                password='testpass123'
            )
            users.append(user)
        
        token = self._get_jwt_token(self.admin_user)
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {token}')
        
        # When
        response = self.client.get(reverse('user-list'), {'page': 1, 'page_size': 10})
        
        # Then
        assert response.status_code == status.HTTP_200_OK
        
        response_data = response.json()
        assert 'results' in response_data
        assert 'count' in response_data
        assert len(response_data['results']) == 10
        assert response_data['count'] >= 15  # 作成したユーザー + 既存ユーザー
    
    def test_user_search_by_email(self) -> None:
        """メールアドレス検索テスト"""
        # Given
        token = self._get_jwt_token(self.admin_user)
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {token}')
        
        # When
        response = self.client.get(
            reverse('user-list'),
            {'search': self.user.email}
        )
        
        # Then
        assert response.status_code == status.HTTP_200_OK
        
        response_data = response.json()
        assert len(response_data['results']) == 1
        assert response_data['results'][0]['email'] == self.user.email
    
    def test_user_update_success(self) -> None:
        """ユーザー更新成功テスト"""
        # Given
        token = self._get_jwt_token(self.user)
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {token}')
        
        update_data = {
            'first_name': 'Updated',
            'last_name': 'Name'
        }
        
        # When
        response = self.client.patch(
            reverse('user-detail', args=[self.user.id]),
            data=json.dumps(update_data),
            content_type='application/json'
        )
        
        # Then
        assert response.status_code == status.HTTP_200_OK
        
        # データベース確認
        self.user.refresh_from_db()
        assert self.user.first_name == 'Updated'
        assert self.user.last_name == 'Name'
    
    def test_user_delete_success(self) -> None:
        """ユーザー削除成功テスト"""
        # Given
        user_to_delete = User.objects.create_user(
            email='todelete@example.com',
            first_name='To',
            last_name='Delete',
            password='deletepass123'
        )
        
        token = self._get_jwt_token(self.admin_user)
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {token}')
        
        # When
        response = self.client.delete(reverse('user-detail', args=[user_to_delete.id]))
        
        # Then
        assert response.status_code == status.HTTP_204_NO_CONTENT
        
        # データベース確認（ソフトデリートの場合）
        user_to_delete.refresh_from_db()
        assert not user_to_delete.is_active

class DatabaseIntegrationTest(TransactionTestCase):
    """データベース統合テスト"""
    
    def test_user_creation_with_database_constraints(self) -> None:
        """データベース制約を考慮したユーザー作成テスト"""
        # Given
        user_data = {
            'email': 'constraint_test@example.com',
            'first_name': 'Constraint',
            'last_name': 'Test'
        }
        
        # When - 最初のユーザー作成
        user1 = User.objects.create_user(**user_data, password='pass123')
        
        # Then
        assert user1.id is not None
        assert user1.email == user_data['email']
        
        # When - 同じメールアドレスで2番目のユーザー作成を試行
        with pytest.raises(Exception):  # IntegrityError等
            User.objects.create_user(**user_data, password='pass456')
    
    def test_transaction_rollback(self) -> None:
        """トランザクションロールバックテスト"""
        from django.db import transaction
        
        initial_count = User.objects.count()
        
        # When - 意図的にエラーを発生させる
        with pytest.raises(Exception):
            with transaction.atomic():
                User.objects.create_user(
                    email='rollback_test@example.com',
                    first_name='Rollback',
                    last_name='Test',
                    password='pass123'
                )
                # 意図的にエラーを発生
                raise Exception("Intentional error for rollback test")
        
        # Then - ユーザー数は変わっていない
        assert User.objects.count() == initial_count
```

### 5.3 テストデータ管理

#### **Factory Boy活用**
```python
# ✅ Good: Factory Boyによるテストデータ管理
# tests/factories.py
import factory
from factory.django import DjangoModelFactory
from factory import Faker, SubFactory, LazyAttribute, LazyFunction
from django.contrib.auth import get_user_model
from decimal import Decimal
from datetime import datetime, timezone
import random

User = get_user_model()

class UserFactory(DjangoModelFactory):
    """ユーザーファクトリ"""
    
    class Meta:
        model = User
    
    email = Faker('email')
    first_name = Faker('first_name')
    last_name = Faker('last_name')
    is_active = True
    is_staff = False
    password = factory.PostGenerationMethodCall('set_password', 'defaultpass123')
    
    @factory.lazy_attribute
    def username(self):
        """ユーザー名を自動生成"""
        return f"{self.first_name.lower()}.{self.last_name.lower()}"

class AdminUserFactory(UserFactory):
    """管理者ユーザーファクトリ"""
    
    is_staff = True
    is_superuser = True
    email = factory.Sequence(lambda n: f"admin{n}@company.com")

class ProductFactory(DjangoModelFactory):
    """商品ファクトリ"""
    
    class Meta:
        model = 'products.Product'
    
    name = Faker('word')
    description = Faker('text', max_nb_chars=500)
    price = factory.LazyFunction(lambda: Decimal(str(random.uniform(10.0, 1000.0))))
    stock_quantity = factory.LazyFunction(lambda: random.randint(0, 100))
    is_active = True
    category = factory.SubFactory('tests.factories.CategoryFactory')
    
    @factory.post_generation
    def tags(self, create, extracted, **kwargs):
        """商品タグの追加"""
        if not create:
            return
        
        if extracted:
            for tag in extracted:
                self.tags.add(tag)

class OrderFactory(DjangoModelFactory):
    """注文ファクトリ"""
    
    class Meta:
        model = 'orders.Order'
    
    user = SubFactory(UserFactory)
    status = 'pending'
    total_amount = factory.LazyFunction(lambda: Decimal(str(random.uniform(50.0, 500.0))))
    created_at = factory.LazyFunction(lambda: datetime.now(timezone.utc))
    
    @factory.post_generation
    def items(self, create, extracted, **kwargs):
        """注文アイテムの追加"""
        if not create:
            return
        
        if extracted:
            for item in extracted:
                OrderItemFactory(order=self, **item)
        else:
            # デフォルトで1-3個のアイテムを追加
            for _ in range(random.randint(1, 3)):
                OrderItemFactory(order=self)

class OrderItemFactory(DjangoModelFactory):
    """注文アイテムファクトリ"""
    
    class Meta:
        model = 'orders.OrderItem'
    
    order = SubFactory(OrderFactory)
    product = SubFactory(ProductFactory)
    quantity = factory.LazyFunction(lambda: random.randint(1, 5))
    unit_price = factory.LazyAttribute(lambda obj: obj.product.price)
    
    @factory.lazy_attribute
    def total_price(self):
        """合計金額計算"""
        return self.quantity * self.unit_price

# カスタムトレイト
class UserWithOrdersFactory(UserFactory):
    """注文履歴付きユーザーファクトリ"""
    
    @factory.post_generation
    def orders(self, create, extracted, **kwargs):
        """注文履歴の追加"""
        if not create:
            return
        
        if extracted:
            order_count = extracted
        else:
            order_count = random.randint(1, 5)
        
        for _ in range(order_count):
            OrderFactory(user=self)

# 使用例
def test_factories_usage():
    """ファクトリ使用例"""
    
    # 基本的な使用
    user = UserFactory()
    assert user.email is not None
    assert user.is_active is True
    
    # 属性を指定して作成
    admin = AdminUserFactory(email='specific@admin.com')
    assert admin.is_staff is True
    assert admin.email == 'specific@admin.com'
    
    # 複数作成
    users = UserFactory.create_batch(5)
    assert len(users) == 5
    
    # ビルドのみ（DBに保存しない）
    user_instance = UserFactory.build()
    assert user_instance.id is None
    
    # 関連オブジェクト付きで作成
    user_with_orders = UserWithOrdersFactory(orders=3)
    assert user_with_orders.orders.count() == 3
    
    # 辞書として作成（属性のみ）
    user_data = UserFactory.build_dict()
    assert 'email' in user_data
    assert 'password' not in user_data  # PostGenerationは含まれない

# Pytest fixtures with Factory Boy
@pytest.fixture
def user():
    """ユーザーフィクスチャ"""
    return UserFactory()

@pytest.fixture
def admin_user():
    """管理者ユーザーフィクスチャ"""
    return AdminUserFactory()

@pytest.fixture
def user_with_orders():
    """注文履歴付きユーザーフィクスチャ"""
    return UserWithOrdersFactory(orders=2)

@pytest.fixture
def product():
    """商品フィクスチャ"""
    return ProductFactory()

# カスタムフィクスチャ例
@pytest.fixture
def sample_order_data():
    """サンプル注文データ"""
    user = UserFactory()
    products = ProductFactory.create_batch(3)
    
    return {
        'user': user,
        'items': [
            {'product': products[0], 'quantity': 2},
            {'product': products[1], 'quantity': 1},
            {'product': products[2], 'quantity': 3},
        ]
    }
```

---


## 6. パフォーマンス最適化・プロファイリング

### 6.1 パフォーマンス測定とプロファイリング

#### 6.1.1 基本プロファイリングツール

**Good: cProfileとline_profilerの活用**
```python
# cProfileによる関数レベルプロファイリング
import cProfile
import pstats
from functools import wraps

def profile_function(func):
    """関数のパフォーマンスをプロファイリングするデコレータ"""
    @wraps(func)
    def wrapper(*args, **kwargs):
        profiler = cProfile.Profile()
        profiler.enable()
        try:
            result = func(*args, **kwargs)
        finally:
            profiler.disable()
            stats = pstats.Stats(profiler)
            stats.sort_stats('cumulative')
            stats.print_stats(10)  # トップ10を表示
        return result
    return wrapper

@profile_function
def expensive_operation(data):
    """重い処理の例"""
    return [x**2 for x in data if x % 2 == 0]

# line_profilerによる行レベルプロファイリング
# pip install line_profiler
# kernprof -l -v script.py

@profile  # line_profiler用デコレータ
def detailed_analysis(items):
    """行レベル解析対象関数"""
    result = []
    for item in items:  # この行の実行時間が測定される
        processed = item * 2 + 1  # この行も個別に測定
        if processed > 10:  # 条件分岐のコストも可視化
            result.append(processed)
    return result
```

**Bad: 測定なしの最適化**
```python
# 推測による最適化（避けるべき）
def premature_optimization(data):
    # "きっと遅いだろう"で複雑化
    cache = {}
    for item in data:
        if item in cache:
            result = cache[item]
        else:
            result = simple_calculation(item)
            cache[item] = result
    return result

# 実際は simple_calculation が十分高速で
# キャッシュのオーバーヘッドの方が大きい場合
```

#### 6.1.2 メモリプロファイリング

**Good: memory_profilerとpympler活用**
```python
# memory_profilerによるメモリ使用量監視
from memory_profiler import profile
import psutil
import os

@profile
def memory_intensive_function():
    """メモリ使用量を監視する関数"""
    # 大量のデータ処理
    data = list(range(1000000))  # メモリ使用量が記録される
    processed = []
    for item in data:
        processed.append(item ** 2)  # 各行のメモリ増加を追跡
    return processed

class MemoryMonitor:
    """メモリ使用量監視クラス"""
    
    def __init__(self):
        self.process = psutil.Process(os.getpid())
        self.initial_memory = self.get_memory_usage()
    
    def get_memory_usage(self):
        """現在のメモリ使用量（MB）を取得"""
        return self.process.memory_info().rss / 1024 / 1024
    
    def log_memory_diff(self, operation_name):
        """操作前後のメモリ差分をログ出力"""
        current_memory = self.get_memory_usage()
        diff = current_memory - self.initial_memory
        print(f"{operation_name}: {diff:.2f}MB増加")
        self.initial_memory = current_memory

# 使用例
monitor = MemoryMonitor()
large_list = list(range(1000000))
monitor.log_memory_diff("大量リスト作成")
```

**Bad: メモリリークの放置**
```python
# メモリリークを引き起こすパターン
class LeakyClass:
    instances = []  # クラス変数にインスタンス保持
    
    def __init__(self, data):
        self.data = data
        LeakyClass.instances.append(self)  # 永続的参照でGC阻害
    
    def process(self):
        # 大量データを処理後もメモリ解放されない
        result = []
        for item in self.data:
            result.append(expensive_computation(item))
        return result
```

### 6.2 データ構造とアルゴリズムの最適化

#### 6.2.1 効率的なデータ構造選択

**Good: 用途に応じた最適データ構造**
```python
from collections import deque, defaultdict, Counter
import bisect
from typing import Dict, List, Set

class OptimizedDataStructures:
    """最適化されたデータ構造の使用例"""
    
    def __init__(self):
        # 頻繁な先頭・末尾操作にはdeque
        self.queue = deque()
        
        # グループ化にはdefaultdict
        self.groups: Dict[str, List] = defaultdict(list)
        
        # カウント処理にはCounter
        self.counters = Counter()
        
        # 高速検索にはset
        self.lookup: Set[str] = set()
        
        # ソート済みリストにはbisect
        self.sorted_items = []
    
    def efficient_queue_operations(self):
        """効率的なキュー操作"""
        # O(1)での先頭・末尾操作
        self.queue.appendleft("urgent")  # 先頭追加
        self.queue.append("normal")      # 末尾追加
        
        if self.queue:
            urgent_item = self.queue.popleft()  # 先頭取得
            normal_item = self.queue.pop()      # 末尾取得
    
    def efficient_grouping(self, items: List[Dict]):
        """効率的なグループ化処理"""
        # defaultdictで初期化不要
        for item in items:
            category = item.get('category', 'unknown')
            self.groups[category].append(item)
        
        return dict(self.groups)  # 通常のdictに変換
    
    def efficient_counting(self, items: List[str]):
        """効率的なカウント処理"""
        # Counterで自動カウント
        self.counters.update(items)
        
        # 最頻出要素の取得
        most_common = self.counters.most_common(5)
        return most_common
    
    def efficient_membership_test(self, item: str) -> bool:
        """効率的なメンバーシップテスト"""
        # O(1)での検索
        return item in self.lookup
    
    def efficient_sorted_insertion(self, item: int):
        """ソート済みリストへの効率的挿入"""
        # O(log n)での挿入位置特定
        bisect.insort(self.sorted_items, item)
```

**Bad: 非効率なデータ構造使用**
```python
# 非効率なパターン集
class InefficientOperations:
    
    def slow_queue_with_list(self):
        """リストでのキュー操作（O(n)）"""
        queue = []
        queue.insert(0, "item")  # O(n) - 全要素シフト
        if queue:
            queue.pop(0)  # O(n) - 全要素シフト
    
    def slow_grouping_with_dict(self, items):
        """非効率なグループ化"""
        groups = {}
        for item in items:
            category = item.get('category')
            if category not in groups:  # 毎回チェック
                groups[category] = []   # 毎回初期化
            groups[category].append(item)
    
    def slow_counting_with_dict(self, items):
        """非効率なカウント処理"""
        counts = {}
        for item in items:
            if item in counts:      # 毎回チェック
                counts[item] += 1
            else:
                counts[item] = 1
        
        # 手動ソート（非効率）
        sorted_counts = sorted(counts.items(), 
                             key=lambda x: x[1], 
                             reverse=True)
    
    def slow_membership_with_list(self, items, target):
        """リストでのメンバーシップテスト（O(n)）"""
        return target in items  # 毎回線形検索
```

#### 6.2.2 効率的なアルゴリズム実装

**Good: 計算量を考慮したアルゴリズム**
```python
from functools import lru_cache
import heapq
from typing import List, Optional

class EfficientAlgorithms:
    """効率的なアルゴリズム実装例"""
    
    @lru_cache(maxsize=1000)
    def fibonacci_optimized(self, n: int) -> int:
        """メモ化による最適化済みフィボナッチ"""
        if n < 2:
            return n
        return self.fibonacci_optimized(n-1) + self.fibonacci_optimized(n-2)
    
    def find_top_k_elements(self, items: List[int], k: int) -> List[int]:
        """ヒープを使用したTop-K要素検索 O(n log k)"""
        if k >= len(items):
            return sorted(items, reverse=True)
        
        # 最小ヒープでTop-K要素を維持
        heap = []
        for item in items:
            if len(heap) < k:
                heapq.heappush(heap, item)
            elif item > heap[0]:
                heapq.heapreplace(heap, item)
        
        return sorted(heap, reverse=True)
    
    def binary_search_range(self, sorted_list: List[int], 
                           target: int) -> Optional[int]:
        """バイナリサーチによる高速検索 O(log n)"""
        left, right = 0, len(sorted_list) - 1
        
        while left <= right:
            mid = (left + right) // 2
            if sorted_list[mid] == target:
                return mid
            elif sorted_list[mid] < target:
                left = mid + 1
            else:
                right = mid - 1
        
        return None
    
    def efficient_deduplication(self, items: List[str]) -> List[str]:
        """順序保持重複除去 O(n)"""
        seen = set()
        result = []
        for item in items:
            if item not in seen:
                seen.add(item)
                result.append(item)
        return result
    
    def sliding_window_maximum(self, nums: List[int], 
                              k: int) -> List[int]:
        """スライディングウィンドウ最大値 O(n)"""
        from collections import deque
        
        dq = deque()
        result = []
        
        for i, num in enumerate(nums):
            # ウィンドウ外の要素を削除
            while dq and dq[0] < i - k + 1:
                dq.popleft()
            
            # 現在の要素より小さい要素を削除
            while dq and nums[dq[-1]] < num:
                dq.pop()
            
            dq.append(i)
            
            # ウィンドウサイズに達したら結果に追加
            if i >= k - 1:
                result.append(nums[dq[0]])
        
        return result
```

**Bad: 非効率なアルゴリズム**
```python
# 指数時間アルゴリズム（避けるべき）
def fibonacci_inefficient(n):
    """指数時間フィボナッチ O(2^n)"""
    if n < 2:
        return n
    return fibonacci_inefficient(n-1) + fibonacci_inefficient(n-2)

def find_top_k_inefficient(items, k):
    """全ソートによるTop-K O(n log n)"""
    sorted_items = sorted(items, reverse=True)  # 不要な全ソート
    return sorted_items[:k]

def linear_search_in_sorted(sorted_list, target):
    """ソート済みリストでの線形検索 O(n)"""
    for i, item in enumerate(sorted_list):
        if item == target:
            return i
    return None

def nested_loop_deduplication(items):
    """ネストループ重複除去 O(n²)"""
    result = []
    for item in items:
        is_duplicate = False
        for existing in result:  # 毎回全リストスキャン
            if item == existing:
                is_duplicate = True
                break
        if not is_duplicate:
            result.append(item)
    return result
```

### 6.3 I/O操作の最適化

#### 6.3.1 ファイル操作の最適化

**Good: 効率的なファイル処理**
```python
import asyncio
import aiofiles
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path
import mmap
import json
from typing import Iterator, List

class OptimizedFileOperations:
    """最適化されたファイル操作"""
    
    def read_large_file_chunked(self, file_path: Path, 
                               chunk_size: int = 8192) -> Iterator[str]:
        """大容量ファイルのチャンク読み込み"""
        with open(file_path, 'r', encoding='utf-8') as file:
            while True:
                chunk = file.read(chunk_size)
                if not chunk:
                    break
                yield chunk
    
    def memory_mapped_file_processing(self, file_path: Path) -> int:
        """メモリマップファイルによる高速処理"""
        with open(file_path, 'r', encoding='utf-8') as file:
            with mmap.mmap(file.fileno(), 0, access=mmap.ACCESS_READ) as mm:
                # メモリマップでの高速検索
                line_count = mm.count(b'\n')
                return line_count
    
    async def async_file_operations(self, file_paths: List[Path]):
        """非同期ファイル操作"""
        async def read_file_async(path: Path) -> str:
            async with aiofiles.open(path, 'r') as file:
                return await file.read()
        
        # 複数ファイルの並行読み込み
        tasks = [read_file_async(path) for path in file_paths]
        contents = await asyncio.gather(*tasks)
        return contents
    
    def batch_json_processing(self, json_files: List[Path]) -> List[dict]:
        """バッチJSON処理"""
        def process_single_json(file_path: Path) -> dict:
            with open(file_path, 'r') as file:
                return json.load(file)
        
        # ThreadPoolExecutorでI/O並列化
        with ThreadPoolExecutor(max_workers=4) as executor:
            results = list(executor.map(process_single_json, json_files))
        
        return results
    
    def efficient_csv_processing(self, csv_path: Path) -> List[dict]:
        """効率的CSV処理"""
        import pandas as pd
        
        # chunksize指定で大容量ファイル対応
        chunks = pd.read_csv(csv_path, chunksize=10000)
        
        processed_data = []
        for chunk in chunks:
            # チャンクごとに処理
            filtered = chunk[chunk['value'] > 0]
            processed_data.extend(filtered.to_dict('records'))
        
        return processed_data
```

**Bad: 非効率なファイル処理**
```python
# メモリを大量消費する非効率パターン
def inefficient_large_file_reading(file_path):
    """大容量ファイルの一括読み込み（メモリ不足リスク）"""
    with open(file_path, 'r') as file:
        entire_content = file.read()  # 全内容をメモリに展開
        lines = entire_content.split('\n')  # さらにメモリ使用量増加
        return len(lines)

def sequential_file_processing(file_paths):
    """逐次ファイル処理（並列化なし）"""
    results = []
    for path in file_paths:
        with open(path, 'r') as file:
            content = file.read()  # I/O待機時間の累積
            results.append(process_content(content))
    return results

def inefficient_json_processing(json_files):
    """非効率JSON処理"""
    all_data = []
    for file_path in json_files:
        with open(file_path, 'r') as file:
            content = file.read()
            data = json.loads(content)  # パース処理も逐次実行
            all_data.append(data)
    return all_data
```

#### 6.3.2 データベース操作の最適化

**Good: 効率的なデータベース操作**
```python
from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker
from contextlib import contextmanager
import asyncpg
import asyncio
from typing import List, Dict, Any

class OptimizedDatabaseOperations:
    """最適化されたデータベース操作"""
    
    def __init__(self, database_url: str):
        self.engine = create_engine(
            database_url,
            pool_size=20,           # コネクションプール設定
            max_overflow=30,
            pool_pre_ping=True,     # 接続健全性チェック
            echo=False              # 本番環境ではFalse
        )
        self.SessionLocal = sessionmaker(bind=self.engine)
    
    @contextmanager
    def get_db_session(self):
        """データベースセッション管理"""
        session = self.SessionLocal()
        try:
            yield session
            session.commit()
        except Exception:
            session.rollback()
            raise
        finally:
            session.close()
    
    def batch_insert_optimized(self, table_name: str, 
                              records: List[Dict[str, Any]]):
        """バッチ挿入最適化"""
        with self.get_db_session() as session:
            # bulk_insert_mappingsで高速一括挿入
            session.execute(
                text(f"""
                    INSERT INTO {table_name} (column1, column2, column3)
                    VALUES (:col1, :col2, :col3)
                """),
                records
            )
            # コミットは一度だけ
    
    def efficient_pagination(self, table_name: str, 
                           page_size: int = 100, 
                           last_id: int = 0) -> List[Dict]:
        """効率的なページネーション（OFFSET避け）"""
        with self.get_db_session() as session:
            query = text(f"""
                SELECT id, column1, column2, column3
                FROM {table_name}
                WHERE id > :last_id
                ORDER BY id
                LIMIT :page_size
            """)
            
            result = session.execute(
                query, 
                {"last_id": last_id, "page_size": page_size}
            )
            return [dict(row._mapping) for row in result]
    
    async def async_database_operations(self, queries: List[str]):
        """非同期データベース操作"""
        async with asyncpg.create_pool(
            "postgresql://user:pass@localhost/db",
            min_size=5,
            max_size=20
        ) as pool:
            
            async def execute_query(query: str):
                async with pool.acquire() as connection:
                    return await connection.fetch(query)
            
            # 複数クエリの並行実行
            tasks = [execute_query(query) for query in queries]
            results = await asyncio.gather(*tasks)
            return results
    
    def query_optimization_with_indexes(self):
        """インデックスを活用したクエリ最適化例"""
        optimized_queries = {
            "range_query": """
                -- 範囲検索用複合インデックス活用
                SELECT * FROM orders 
                WHERE created_date BETWEEN %s AND %s 
                AND status = %s
                ORDER BY created_date DESC;
                -- INDEX: (status, created_date)
            """,
            
            "join_optimization": """
                -- JOINクエリの最適化
                SELECT u.name, p.title 
                FROM users u
                INNER JOIN posts p ON u.id = p.user_id
                WHERE u.active = true 
                AND p.published_date > %s
                ORDER BY p.published_date DESC
                LIMIT 100;
                -- INDEX: users(active), posts(user_id, published_date)
            """,
            
            "partial_index": """
                -- 部分インデックス活用
                SELECT * FROM transactions 
                WHERE status = 'pending' 
                AND created_date > %s;
                -- PARTIAL INDEX: (created_date) WHERE status = 'pending'
            """
        }
        return optimized_queries
```

**Bad: 非効率なデータベース操作**
```python
# N+1問題とその他の非効率パターン
class InefficientDatabaseOperations:
    
    def n_plus_one_problem(self, user_ids):
        """N+1問題の典型例（避けるべき）"""
        users = []
        for user_id in user_ids:  # N回のクエリ実行
            user = session.query(User).filter(User.id == user_id).first()
            if user:
                # さらに関連エンティティで追加クエリ
                posts = session.query(Post).filter(Post.user_id == user_id).all()
                user.posts = posts
                users.append(user)
        return users
    
    def inefficient_batch_operations(self, records):
        """非効率なバッチ操作"""
        for record in records:  # 1レコードずつコミット
            session.add(MyModel(**record))
            session.commit()  # 毎回コミット（重い処理）
    
    def poor_pagination(self, page_number, page_size):
        """非効率なページネーション（OFFSET使用）"""
        offset = (page_number - 1) * page_size
        # 大きなOFFSETは非効率（全行スキャン）
        query = session.query(MyModel).offset(offset).limit(page_size)
        return query.all()
    
    def unoptimized_queries(self):
        """最適化されていないクエリ例"""
        # インデックス未使用の検索
        results = session.query(User).filter(
            User.email.like('%example%')  # 前方一致でないLIKE
        ).all()
        
        # 不要なSELECT *
        large_table_data = session.execute(
            "SELECT * FROM large_table WHERE condition = 'value'"
        ).fetchall()
        
        # サブクエリの非効率使用
        inefficient_subquery = session.query(User).filter(
            User.id.in_(
                session.query(Post.user_id).filter(Post.published == True)
            )
        ).all()
```

### 6.4 Webアプリケーションの最適化

#### 6.4.1 Django最適化

**Good: Django最適化ベストプラクティス**
```python
from django.core.cache import cache
from django.db import models
from django.db.models import Prefetch, Count, F
from django.views.decorators.cache import cache_page
from django.utils.decorators import method_decorator
from django.views.generic import ListView
import logging

logger = logging.getLogger(__name__)

class OptimizedDjangoViews:
    """最適化されたDjangoビュー実装"""
    
    @method_decorator(cache_page(60 * 15))  # 15分キャッシュ
    def cached_view(self, request):
        """キャッシュ活用ビュー"""
        cache_key = f"expensive_data_{request.user.id}"
        
        # キャッシュから取得試行
        data = cache.get(cache_key)
        if data is None:
            # キャッシュミス時のみDB処理
            data = self.expensive_database_operation()
            cache.set(cache_key, data, timeout=60*30)  # 30分キャッシュ
        
        return JsonResponse(data)
    
    def optimized_queryset_view(self, request):
        """クエリセット最適化ビュー"""
        # select_related: 1対1, 多対1関係の効率的JOIN
        # prefetch_related: 1対多, 多対多関係の効率的取得
        posts = Post.objects.select_related(
            'author',           # 作者情報をJOINで取得
            'category'          # カテゴリ情報をJOINで取得
        ).prefetch_related(
            'tags',             # タグ情報を効率的に取得
            Prefetch(           # コメント情報をカスタム取得
                'comments',
                queryset=Comment.objects.filter(is_approved=True)
                                       .select_related('user')
            )
        ).annotate(
            comment_count=Count('comments')  # 集計関数活用
        ).filter(
            is_published=True
        )[:20]  # LIMIT指定
        
        return render(request, 'posts/list.html', {'posts': posts})
    
    def bulk_operations_view(self, request):
        """バルク操作最適化"""
        if request.method == 'POST':
            # バルク作成
            posts_data = request.POST.getlist('posts_data')
            posts_to_create = [
                Post(title=data['title'], content=data['content'])
                for data in posts_data
            ]
            Post.objects.bulk_create(posts_to_create, batch_size=100)
            
            # バルク更新
            Post.objects.filter(
                category_id=1
            ).update(
                is_featured=True,
                updated_at=timezone.now()
            )
            
            # F式での効率的更新
            Post.objects.filter(
                is_published=True
            ).update(
                view_count=F('view_count') + 1  # 競合状態回避
            )
        
        return redirect('posts:list')

class OptimizedModels(models.Model):
    """最適化されたモデル定義"""
    
    class Meta:
        # 複合インデックス定義
        indexes = [
            models.Index(fields=['status', 'created_date']),
            models.Index(fields=['author', 'published_date']),
        ]
        # デフォルト順序指定（ORDER BY削減）
        ordering = ['-created_date']
    
    title = models.CharField(max_length=200, db_index=True)  # 単一インデックス
    content = models.TextField()
    status = models.CharField(max_length=20, choices=STATUS_CHOICES)
    created_date = models.DateTimeField(auto_now_add=True)
    
    @classmethod
    def get_popular_posts(cls, limit=10):
        """効率的な人気記事取得"""
        return cls.objects.filter(
            is_published=True
        ).annotate(
            total_engagement=F('view_count') + F('like_count') * 2
        ).order_by('-total_engagement')[:limit]
```

**Bad: Django非効率パターン**
```python
# Django非効率実装例
class InefficientDjangoViews:
    
    def inefficient_view(self, request):
        """非効率なビュー実装"""
        posts = Post.objects.all()  # select_related/prefetch_related未使用
        
        result = []
        for post in posts:  # N+1問題発生
            author_name = post.author.name  # 毎回DBアクセス
            category_name = post.category.name  # 毎回DBアクセス
            comment_count = len(post.comments.all())  # 毎回DBアクセス
            
            result.append({
                'title': post.title,
                'author': author_name,
                'category': category_name,
                'comments': comment_count
            })
        
        return JsonResponse({'posts': result})
    
    def no_caching_view(self, request):
        """キャッシュ未使用の重い処理"""
        # 毎回同じ重い計算を実行
        expensive_result = self.calculate_complex_statistics()
        return JsonResponse(expensive_result)
    
    def inefficient_bulk_operations(self, posts_data):
        """非効率なバルク操作"""
        for post_data in posts_data:  # 1件ずつ処理
            post = Post.objects.create(**post_data)  # 毎回INSERT
            post.save()  # 不要なsave()呼び出し
```

#### 6.4.2 FastAPI最適化

**Good: FastAPI最適化実装**
```python
from fastapi import FastAPI, Depends, BackgroundTasks
from fastapi.middleware.gzip import GZipMiddleware
from fastapi.middleware.cors import CORSMiddleware
import asyncio
import aioredis
from sqlalchemy.ext.asyncio import AsyncSession
from contextlib import asynccontextmanager
import uvloop

# パフォーマンス向上のためuvloop使用
asyncio.set_event_loop_policy(uvloop.EventLoopPolicy())

class OptimizedFastAPIApp:
    """最適化されたFastAPIアプリケーション"""
    
    def __init__(self):
        self.app = FastAPI(title="Optimized API")
        self.setup_middleware()
        self.setup_routes()
    
    def setup_middleware(self):
        """ミドルウェア設定"""
        # Gzip圧縮有効化
        self.app.add_middleware(GZipMiddleware, minimum_size=1000)
        
        # CORS最適化設定
        self.app.add_middleware(
            CORSMiddleware,
            allow_origins=["https://yourdomain.com"],  # 具体的ドメイン指定
            allow_credentials=True,
            allow_methods=["GET", "POST"],  # 必要メソッドのみ
            allow_headers=["*"],
        )
    
    @asynccontextmanager
    async def lifespan(self, app: FastAPI):
        """アプリケーションライフサイクル管理"""
        # 起動時の初期化
        self.redis = await aioredis.from_url("redis://localhost")
        yield
        # 終了時のクリーンアップ
        await self.redis.close()
    
    async def get_cached_data(self, key: str, ttl: int = 300):
        """Redis キャッシュ活用"""
        cached = await self.redis.get(key)
        if cached:
            return json.loads(cached)
        
        # キャッシュミス時のデータ取得
        data = await self.fetch_from_database(key)
        await self.redis.setex(key, ttl, json.dumps(data))
        return data
    
    async def optimized_endpoint(self, 
                                user_id: int,
                                db: AsyncSession = Depends(get_async_db),
                                background_tasks: BackgroundTasks = None):
        """最適化されたエンドポイント"""
        # 並行処理でパフォーマンス向上
        user_task = asyncio.create_task(self.get_user(user_id, db))
        posts_task = asyncio.create_task(self.get_user_posts(user_id, db))
        stats_task = asyncio.create_task(self.get_user_stats(user_id))
        
        # 並行実行待機
        user, posts, stats = await asyncio.gather(
            user_task, posts_task, stats_task
        )
        
        # バックグラウンド処理でレスポンス速度向上
        if background_tasks:
            background_tasks.add_task(
                self.update_user_last_accessed, user_id
            )
        
        return {
            "user": user,
            "posts": posts,
            "stats": stats
        }
    
    async def batch_processing_endpoint(self, item_ids: List[int]):
        """バッチ処理最適化"""
        # セマフォで並行数制限
        semaphore = asyncio.Semaphore(10)
        
        async def process_item(item_id: int):
            async with semaphore:
                return await self.process_single_item(item_id)
        
        tasks = [process_item(item_id) for item_id in item_ids]
        results = await asyncio.gather(*tasks, return_exceptions=True)
        
        # エラーハンドリング
        successful_results = []
        errors = []
        for i, result in enumerate(results):
            if isinstance(result, Exception):
                errors.append({"item_id": item_ids[i], "error": str(result)})
            else:
                successful_results.append(result)
        
        return {
            "successful": successful_results,
            "errors": errors
        }

# Pydantic最適化
from pydantic import BaseModel, validator
from typing import Optional

class OptimizedPydanticModel(BaseModel):
    """最適化されたPydanticモデル"""
    
    class Config:
        # バリデーション最適化
        validate_assignment = False  # 代入時バリデーション無効化
        use_enum_values = True      # Enum値の直接使用
        allow_population_by_field_name = True
        
        # JSON serialization最適化
        json_encoders = {
            datetime: lambda v: v.isoformat(),
            Decimal: float
        }
    
    id: int
    name: str
    email: Optional[str] = None
    created_at: datetime
    
    @validator('email')
    def validate_email(cls, v):
        """効率的バリデーション"""
        if v and '@' not in v:
            raise ValueError('Invalid email format')
        return v
```

**Bad: FastAPI非効率パターン**
```python
# FastAPI非効率実装例
from fastapi import FastAPI

app = FastAPI()

# 非効率なエンドポイント
@app.get("/inefficient/{user_id}")
async def inefficient_endpoint(user_id: int):
    """非最適化エンドポイント"""
    # 逐次処理（非効率）
    user = await get_user(user_id)
    posts = await get_user_posts(user_id)  # 前の処理完了まで待機
    stats = await get_user_stats(user_id)  # さらに待機
    
    # キャッシュ未使用
    expensive_calculation = calculate_heavy_stats(user_id)
    
    return {
        "user": user,
        "posts": posts,
        "stats": stats,
        "calculation": expensive_calculation
    }

# ミドルウェア未設定
# - Gzip圧縮なし
# - CORS設定が緩い
# - リクエスト/レスポンス最適化なし

# 非効率なPydanticモデル
class InefficientModel(BaseModel):
    # 設定最適化なし
    id: int
    data: str
    
    @validator('data', always=True)  # 毎回実行（非効率）
    def expensive_validation(cls, v):
        # 重いバリデーション処理
        return complex_validation_process(v)
```

---

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

## 8. 監視・ログ・デバッグ

### 8.1 構造化ログ実装

#### 8.1.1 統一ログシステム

**Good: 包括的ログシステム**
```python
# 統一ログシステム実装
import logging
import json
import sys
import traceback
from typing import Dict, Any, Optional, List, Union
from datetime import datetime
from enum import Enum
from dataclasses import dataclass, field, asdict
from contextvars import ContextVar
from functools import wraps
import asyncio
import uuid
from pathlib import Path
from logging.handlers import RotatingFileHandler, TimedRotatingFileHandler
import structlog
from pythonjsonlogger import jsonlogger
import os

class LogLevel(Enum):
    """ログレベル定義"""
    TRACE = "TRACE"
    DEBUG = "DEBUG"
    INFO = "INFO"
    WARNING = "WARNING"
    ERROR = "ERROR"
    CRITICAL = "CRITICAL"

class LogCategory(Enum):
    """ログカテゴリ定義"""
    APPLICATION = "application"
    SECURITY = "security"
    PERFORMANCE = "performance"
    AUDIT = "audit"
    BUSINESS = "business"
    SYSTEM = "system"
    DATABASE = "database"
    API = "api"
    AUTHENTICATION = "authentication"
    ERROR = "error"

@dataclass
class LogContext:
    """ログコンテキスト情報"""
    request_id: Optional[str] = None
    user_id: Optional[str] = None
    session_id: Optional[str] = None
    operation: Optional[str] = None
    component: Optional[str] = None
    version: Optional[str] = None
    environment: Optional[str] = None
    additional_data: Dict[str, Any] = field(default_factory=dict)

# グローバルコンテキスト変数
log_context: ContextVar[LogContext] = ContextVar('log_context', default=LogContext())

class StructuredLogger:
    """構造化ログシステム"""
    
    def __init__(self, name: str, config: Dict[str, Any] = None):
        self.name = name
        self.config = config or {}
        self.logger = self._setup_logger()
        
        # パフォーマンス監視
        self.performance_threshold = self.config.get("performance_threshold", 1.0)
        
        # エラー追跡
        self.error_tracking_enabled = self.config.get("error_tracking", True)
    
    def _setup_logger(self) -> structlog.BoundLogger:
        """ロガー初期設定"""
        
        # コンソールハンドラー
        console_handler = logging.StreamHandler(sys.stdout)
        console_handler.setLevel(logging.INFO)
        
        # JSONフォーマッター
        json_formatter = jsonlogger.JsonFormatter(
            '%(asctime)s %(name)s %(levelname)s %(message)s',
            timestamp=True
        )
        console_handler.setFormatter(json_formatter)
        
        # ファイルハンドラー設定
        log_dir = Path(self.config.get("log_dir", "logs"))
        log_dir.mkdir(exist_ok=True)
        
        # アプリケーションログ
        app_handler = TimedRotatingFileHandler(
            log_dir / "application.log",
            when="midnight",
            interval=1,
            backupCount=30,
            encoding="utf-8"
        )
        app_handler.setFormatter(json_formatter)
        
        # エラーログ
        error_handler = RotatingFileHandler(
            log_dir / "error.log",
            maxBytes=10*1024*1024,  # 10MB
            backupCount=5,
            encoding="utf-8"
        )
        error_handler.setLevel(logging.ERROR)
        error_handler.setFormatter(json_formatter)
        
        # ロガー設定
        logging.basicConfig(
            level=logging.INFO,
            handlers=[console_handler, app_handler, error_handler],
            format='%(message)s'
        )
        
        # structlog設定
        structlog.configure(
            processors=[
                structlog.contextvars.merge_contextvars,
                structlog.processors.add_log_level,
                structlog.processors.add_logger_name,
                structlog.processors.TimeStamper(fmt="iso"),
                self._add_context_processor,
                structlog.processors.JSONRenderer()
            ],
            wrapper_class=structlog.make_filtering_bound_logger(logging.INFO),
            logger_factory=structlog.PrintLoggerFactory(),
            cache_logger_on_first_use=True,
        )
        
        return structlog.get_logger(self.name)
    
    def _add_context_processor(self, logger, method_name, event_dict):
        """コンテキスト情報追加プロセッサー"""
        context = log_context.get()
        if context:
            context_dict = asdict(context)
            # None値を除去
            context_dict = {k: v for k, v in context_dict.items() if v is not None}
            event_dict.update(context_dict)
        
        return event_dict
    
    def set_context(self, **kwargs):
        """ログコンテキスト設定"""
        current_context = log_context.get()
        new_context = LogContext(
            request_id=kwargs.get('request_id', current_context.request_id),
            user_id=kwargs.get('user_id', current_context.user_id),
            session_id=kwargs.get('session_id', current_context.session_id),
            operation=kwargs.get('operation', current_context.operation),
            component=kwargs.get('component', current_context.component),
            version=kwargs.get('version', current_context.version),
            environment=kwargs.get('environment', current_context.environment),
            additional_data={**current_context.additional_data, **kwargs.get('additional_data', {})}
        )
        log_context.set(new_context)
    
    def clear_context(self):
        """ログコンテキストクリア"""
        log_context.set(LogContext())
    
    def trace(self, message: str, category: LogCategory = LogCategory.APPLICATION, **kwargs):
        """トレースレベルログ"""
        self._log(LogLevel.TRACE, message, category, **kwargs)
    
    def debug(self, message: str, category: LogCategory = LogCategory.APPLICATION, **kwargs):
        """デバッグログ"""
        self._log(LogLevel.DEBUG, message, category, **kwargs)
    
    def info(self, message: str, category: LogCategory = LogCategory.APPLICATION, **kwargs):
        """情報ログ"""
        self._log(LogLevel.INFO, message, category, **kwargs)
    
    def warning(self, message: str, category: LogCategory = LogCategory.APPLICATION, **kwargs):
        """警告ログ"""
        self._log(LogLevel.WARNING, message, category, **kwargs)
    
    def error(self, message: str, category: LogCategory = LogCategory.ERROR, error: Exception = None, **kwargs):
        """エラーログ"""
        if error:
            kwargs.update({
                "error_type": type(error).__name__,
                "error_message": str(error),
                "traceback": traceback.format_exc() if self.error_tracking_enabled else None
            })
        self._log(LogLevel.ERROR, message, category, **kwargs)
    
    def critical(self, message: str, category: LogCategory = LogCategory.ERROR, **kwargs):
        """致命的エラーログ"""
        self._log(LogLevel.CRITICAL, message, category, **kwargs)
    
    def _log(self, level: LogLevel, message: str, category: LogCategory, **kwargs):
        """内部ログ処理"""
        log_data = {
            "message": message,
            "category": category.value,
            "level": level.value,
            **kwargs
        }
        
        # ログレベルに応じて出力
        if level == LogLevel.TRACE:
            self.logger.debug(**log_data)
        elif level == LogLevel.DEBUG:
            self.logger.debug(**log_data)
        elif level == LogLevel.INFO:
            self.logger.info(**log_data)
        elif level == LogLevel.WARNING:
            self.logger.warning(**log_data)
        elif level == LogLevel.ERROR:
            self.logger.error(**log_data)
        elif level == LogLevel.CRITICAL:
            self.logger.critical(**log_data)
    
    def performance_log(self, operation: str, duration: float, **kwargs):
        """パフォーマンスログ"""
        level = LogLevel.WARNING if duration > self.performance_threshold else LogLevel.INFO
        
        self._log(
            level,
            f"Performance: {operation} completed",
            LogCategory.PERFORMANCE,
            operation=operation,
            duration_seconds=duration,
            slow_operation=duration > self.performance_threshold,
            **kwargs
        )
    
    def audit_log(self, action: str, resource: str, result: str, **kwargs):
        """監査ログ"""
        self._log(
            LogLevel.INFO,
            f"Audit: {action} on {resource}",
            LogCategory.AUDIT,
            action=action,
            resource=resource,
            result=result,
            timestamp=datetime.utcnow().isoformat(),
            **kwargs
        )
    
    def security_log(self, event: str, severity: str, **kwargs):
        """セキュリティログ"""
        level = LogLevel.ERROR if severity == "high" else LogLevel.WARNING
        
        self._log(
            level,
            f"Security: {event}",
            LogCategory.SECURITY,
            event=event,
            severity=severity,
            **kwargs
        )
    
    def business_log(self, event: str, value: Optional[float] = None, **kwargs):
        """ビジネスログ"""
        self._log(
            LogLevel.INFO,
            f"Business: {event}",
            LogCategory.BUSINESS,
            event=event,
            value=value,
            **kwargs
        )

# グローバルロガーインスタンス
logger = StructuredLogger("app")

# デコレーター群
def log_execution_time(operation: str = None, category: LogCategory = LogCategory.PERFORMANCE):
    """実行時間ログデコレーター"""
    def decorator(func):
        @wraps(func)
        async def async_wrapper(*args, **kwargs):
            start_time = datetime.utcnow()
            operation_name = operation or f"{func.__module__}.{func.__name__}"
            
            logger.set_context(operation=operation_name)
            
            try:
                result = await func(*args, **kwargs)
                duration = (datetime.utcnow() - start_time).total_seconds()
                logger.performance_log(operation_name, duration)
                return result
            except Exception as e:
                duration = (datetime.utcnow() - start_time).total_seconds()
                logger.error(
                    f"Operation {operation_name} failed",
                    category=LogCategory.ERROR,
                    error=e,
                    duration_seconds=duration
                )
                raise
        
        @wraps(func)
        def sync_wrapper(*args, **kwargs):
            start_time = datetime.utcnow()
            operation_name = operation or f"{func.__module__}.{func.__name__}"
            
            logger.set_context(operation=operation_name)
            
            try:
                result = func(*args, **kwargs)
                duration = (datetime.utcnow() - start_time).total_seconds()
                logger.performance_log(operation_name, duration)
                return result
            except Exception as e:
                duration = (datetime.utcnow() - start_time).total_seconds()
                logger.error(
                    f"Operation {operation_name} failed",
                    category=LogCategory.ERROR,
                    error=e,
                    duration_seconds=duration
                )
                raise
        
        return async_wrapper if asyncio.iscoroutinefunction(func) else sync_wrapper
    return decorator

def log_api_call(operation: str = None):
    """エンドポイントログデコレーター"""
    def decorator(func):
        @wraps(func)
        async def wrapper(*args, **kwargs):
            request_id = str(uuid.uuid4())
            operation_name = operation or func.__name__
            
            # コンテキスト設定
            logger.set_context(
                request_id=request_id,
                operation=operation_name,
                component="api"
            )
            
            # リクエストログ
            logger.info(
                f"API call started: {operation_name}",
                category=LogCategory.API,
                endpoint=operation_name,
                args_count=len(args),
                kwargs_keys=list(kwargs.keys())
            )
            
            start_time = datetime.utcnow()
            
            try:
                result = await func(*args, **kwargs)
                duration = (datetime.utcnow() - start_time).total_seconds()
                
                # 成功ログ
                logger.info(
                    f"API call completed: {operation_name}",
                    category=LogCategory.API,
                    endpoint=operation_name,
                    duration_seconds=duration,
                    status="success"
                )
                
                return result
                
            except Exception as e:
                duration = (datetime.utcnow() - start_time).total_seconds()
                
                # エラーログ
                logger.error(
                    f"API call failed: {operation_name}",
                    category=LogCategory.API,
                    endpoint=operation_name,
                    duration_seconds=duration,
                    status="error",
                    error=e
                )
                
                raise
            finally:
                logger.clear_context()
        
        return wrapper
    return decorator

def log_database_operation(operation: str = None):
    """データベース操作ログデコレーター"""
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            operation_name = operation or f"db_{func.__name__}"
            
            logger.debug(
                f"Database operation started: {operation_name}",
                category=LogCategory.DATABASE,
                operation=operation_name
            )
            
            start_time = datetime.utcnow()
            
            try:
                result = func(*args, **kwargs)
                duration = (datetime.utcnow() - start_time).total_seconds()
                
                logger.debug(
                    f"Database operation completed: {operation_name}",
                    category=LogCategory.DATABASE,
                    operation=operation_name,
                    duration_seconds=duration,
                    status="success"
                )
                
                return result
                
            except Exception as e:
                duration = (datetime.utcnow() - start_time).total_seconds()
                
                logger.error(
                    f"Database operation failed: {operation_name}",
                    category=LogCategory.DATABASE,
                    operation=operation_name,
                    duration_seconds=duration,
                    error=e
                )
                
                raise
        
        return wrapper
    return decorator

# 使用例
class UserService:
    """ユーザーサービス例"""
    
    @log_execution_time("user_creation")
    async def create_user(self, user_data: dict) -> dict:
        """ユーザー作成"""
        logger.info(
            "Creating new user",
            category=LogCategory.BUSINESS,
            user_email=user_data.get("email"),
            registration_source=user_data.get("source", "direct")
        )
        
        # ユーザー作成処理
        user = await self._create_user_in_db(user_data)
        
        # ビジネスログ
        logger.business_log(
            "user_registration",
            value=1.0,
            user_id=user["id"],
            email=user["email"]
        )
        
        # 監査ログ
        logger.audit_log(
            action="create",
            resource="user",
            result="success",
            resource_id=user["id"]
        )
        
        return user
    
    @log_database_operation("get_user_by_id")
    def _create_user_in_db(self, user_data: dict) -> dict:
        """データベースユーザー作成"""
        # DB処理のシミュレーション
        import time
        time.sleep(0.1)  # DB処理シミュレーション
        
        return {
            "id": str(uuid.uuid4()),
            "email": user_data["email"],
            "created_at": datetime.utcnow().isoformat()
        }

# FastAPI統合
from fastapi import FastAPI, Request, Response
from fastapi.middleware.base import BaseHTTPMiddleware

class LoggingMiddleware(BaseHTTPMiddleware):
    """ログミドルウェア"""
    
    async def dispatch(self, request: Request, call_next):
        request_id = str(uuid.uuid4())
        
        # リクエストコンテキスト設定
        logger.set_context(
            request_id=request_id,
            component="middleware",
            operation=f"{request.method} {request.url.path}"
        )
        
        # リクエストログ
        logger.info(
            "HTTP request received",
            category=LogCategory.API,
            method=request.method,
            path=request.url.path,
            query_params=dict(request.query_params),
            client_ip=request.client.host,
            user_agent=request.headers.get("user-agent")
        )
        
        start_time = datetime.utcnow()
        
        try:
            response = await call_next(request)
            duration = (datetime.utcnow() - start_time).total_seconds()
            
            # レスポンスログ
            logger.info(
                "HTTP request completed",
                category=LogCategory.API,
                method=request.method,
                path=request.url.path,
                status_code=response.status_code,
                duration_seconds=duration
            )
            
            # レスポンスヘッダーにリクエストID追加
            response.headers["X-Request-ID"] = request_id
            
            return response
            
        except Exception as e:
            duration = (datetime.utcnow() - start_time).total_seconds()
            
            logger.error(
                "HTTP request failed",
                category=LogCategory.API,
                method=request.method,
                path=request.url.path,
                duration_seconds=duration,
                error=e
            )
            
            raise
        finally:
            logger.clear_context()

app = FastAPI()
app.add_middleware(LoggingMiddleware)

user_service = UserService()

@app.post("/users")
@log_api_call("create_user_endpoint")
async def create_user_endpoint(user_data: dict):
    """ユーザー作成エンドポイント"""
    try:
        user = await user_service.create_user(user_data)
        return {"user": user, "status": "created"}
    except Exception as e:
        logger.error(
            "User creation failed",
            category=LogCategory.BUSINESS,
            error=e,
            user_email=user_data.get("email")
        )
        raise
```

**Bad: 不十分なログ実装**
```python
# 不十分なログ実装例
import logging
from fastapi import FastAPI

# 問題: 基本設定のみ
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI()

@app.post("/users")
async def create_user_bad(user_data: dict):
    # 問題: 情報不足のログ
    logger.info("Creating user")  # コンテキスト情報なし
    
    try:
        # ユーザー作成処理
        user = create_user_in_db(user_data)
        
        # 問題: 成功ログなし
        return {"user": user}
        
    except Exception as e:
        # 問題: エラー情報不足
        logger.error("Error occurred")  # エラー詳細なし
        raise

def create_user_in_db(user_data: dict):
    # 問題: データベース操作ログなし
    # DB処理...
    return {"id": "123", "email": user_data["email"]}
```

### 8.2 メトリクス・監視システム

#### 8.2.1 アプリケーションメトリクス

**Good: 包括的メトリクス監視**
```python
# アプリケーションメトリクス監視システム
import time
import asyncio
from typing import Dict, List, Optional, Any, Callable
from dataclasses import dataclass, field
from datetime import datetime, timedelta
from collections import defaultdict, deque
from enum import Enum
from functools import wraps
from contextvars import ContextVar
import threading
import psutil
import gc
from prometheus_client import Counter, Histogram, Gauge, Summary, CollectorRegistry, generate_latest
from prometheus_client.exposition import MetricsHandler
from fastapi import FastAPI, Request, Response
from fastapi.middleware.base import BaseHTTPMiddleware
import httpx

class MetricType(Enum):
    """メトリックタイプ"""
    COUNTER = "counter"
    HISTOGRAM = "histogram"
    GAUGE = "gauge"
    SUMMARY = "summary"

class MetricCategory(Enum):
    """メトリックカテゴリ"""
    BUSINESS = "business"
    PERFORMANCE = "performance"
    SYSTEM = "system"
    APPLICATION = "application"
    SECURITY = "security"
    ERROR = "error"

@dataclass
class MetricConfig:
    """メトリック設定"""
    name: str
    description: str
    metric_type: MetricType
    category: MetricCategory
    labels: List[str] = field(default_factory=list)
    buckets: Optional[List[float]] = None  # Histogram用
    objectives: Optional[Dict[float, float]] = None  # Summary用

class MetricsCollector:
    """メトリックス収集システム"""
    
    def __init__(self, registry: CollectorRegistry = None):
        self.registry = registry or CollectorRegistry()
        self.metrics: Dict[str, Any] = {}
        self.custom_collectors: List[Callable] = []
        
        # システムメトリックス初期化
        self._initialize_system_metrics()
        
        # アプリケーションメトリックス初期化
        self._initialize_application_metrics()
        
        # パフォーマンスメトリックス初期化
        self._initialize_performance_metrics()
        
        # 監視スレッド開始
        self._start_monitoring_thread()
    
    def _initialize_system_metrics(self):
        """システムメトリックス初期化"""
        
        # CPU使用率
        self.metrics["cpu_usage"] = Gauge(
            "system_cpu_usage_percent",
            "CPU usage percentage",
            registry=self.registry
        )
        
        # メモリ使用率
        self.metrics["memory_usage"] = Gauge(
            "system_memory_usage_bytes",
            "Memory usage in bytes",
            ["type"],
            registry=self.registry
        )
        
        # ディスク使用率
        self.metrics["disk_usage"] = Gauge(
            "system_disk_usage_bytes",
            "Disk usage in bytes",
            ["mountpoint", "type"],
            registry=self.registry
        )
        
        # ネットワークI/O
        self.metrics["network_io"] = Counter(
            "system_network_io_bytes_total",
            "Network I/O in bytes",
            ["direction"],
            registry=self.registry
        )
        
        # プロセス情報
        self.metrics["process_threads"] = Gauge(
            "process_threads_count",
            "Number of threads",
            registry=self.registry
        )
        
        self.metrics["process_fds"] = Gauge(
            "process_open_fds_count",
            "Number of open file descriptors",
            registry=self.registry
        )
    
    def _initialize_application_metrics(self):
        """アプリケーションメトリックス初期化"""
        
        # HTTPリクエスト数
        self.metrics["http_requests"] = Counter(
            "http_requests_total",
            "Total HTTP requests",
            ["method", "endpoint", "status_code"],
            registry=self.registry
        )
        
        # HTTPレスポンス時間
        self.metrics["http_request_duration"] = Histogram(
            "http_request_duration_seconds",
            "HTTP request duration in seconds",
            ["method", "endpoint"],
            buckets=[0.1, 0.25, 0.5, 1.0, 2.5, 5.0, 10.0],
            registry=self.registry
        )
        
        # アクティブユーザー数
        self.metrics["active_users"] = Gauge(
            "active_users_count",
            "Number of active users",
            ["time_window"],
            registry=self.registry
        )
        
        # データベース接続数
        self.metrics["db_connections"] = Gauge(
            "database_connections_active",
            "Active database connections",
            ["database"],
            registry=self.registry
        )
        
        # キューサイズ
        self.metrics["queue_size"] = Gauge(
            "queue_size_items",
            "Number of items in queue",
            ["queue_name"],
            registry=self.registry
        )
    
    def _initialize_performance_metrics(self):
        """パフォーマンスメトリックス初期化"""
        
        # 関数実行時間
        self.metrics["function_duration"] = Histogram(
            "function_execution_duration_seconds",
            "Function execution duration in seconds",
            ["function_name", "module"],
            buckets=[0.001, 0.005, 0.01, 0.05, 0.1, 0.5, 1.0, 5.0],
            registry=self.registry
        )
        
        # 関数実行回数
        self.metrics["function_calls"] = Counter(
            "function_calls_total",
            "Total function calls",
            ["function_name", "module", "status"],
            registry=self.registry
        )
        
        # ガベージコレクション
        self.metrics["gc_collections"] = Counter(
            "gc_collections_total",
            "Total garbage collections",
            ["generation"],
            registry=self.registry
        )
        
        # メモリリーク検出
        self.metrics["memory_objects"] = Gauge(
            "memory_objects_count",
            "Number of objects in memory",
            ["type"],
            registry=self.registry
        )
    
    def _start_monitoring_thread(self):
        """監視スレッド開始"""
        def monitor_system():
            while True:
                try:
                    self._collect_system_metrics()
                    time.sleep(30)  # 30秒ごとに収集
                except Exception as e:
                    logger.error("System metrics collection failed", error=e)
                    time.sleep(60)  # エラー時は1分待機
        
        thread = threading.Thread(target=monitor_system, daemon=True)
        thread.start()
    
    def _collect_system_metrics(self):
        """システムメトリックス収集"""
        try:
            # CPU使用率
            cpu_usage = psutil.cpu_percent()
            self.metrics["cpu_usage"].set(cpu_usage)
            
            # メモリ使用率
            memory = psutil.virtual_memory()
            self.metrics["memory_usage"].labels(type="used").set(memory.used)
            self.metrics["memory_usage"].labels(type="available").set(memory.available)
            self.metrics["memory_usage"].labels(type="total").set(memory.total)
            
            # ディスク使用率
            for partition in psutil.disk_partitions():
                try:
                    usage = psutil.disk_usage(partition.mountpoint)
                    self.metrics["disk_usage"].labels(
                        mountpoint=partition.mountpoint,
                        type="used"
                    ).set(usage.used)
                    self.metrics["disk_usage"].labels(
                        mountpoint=partition.mountpoint,
                        type="free"
                    ).set(usage.free)
                except PermissionError:
                    continue
            
            # プロセス情報
            process = psutil.Process()
            self.metrics["process_threads"].set(process.num_threads())
            self.metrics["process_fds"].set(process.num_fds())
            
            # ガベージコレクション統計
            gc_stats = gc.get_stats()
            for i, stats in enumerate(gc_stats):
                self.metrics["gc_collections"].labels(
                    generation=str(i)
                )._value._value = stats["collections"]
        
        except Exception as e:
            logger.error("Failed to collect system metrics", error=e)
    
    def increment_counter(self, name: str, labels: Dict[str, str] = None, value: float = 1.0):
        """カウンターインクリメント"""
        if name in self.metrics:
            if labels:
                self.metrics[name].labels(**labels).inc(value)
            else:
                self.metrics[name].inc(value)
    
    def set_gauge(self, name: str, value: float, labels: Dict[str, str] = None):
        """ゲージ設定"""
        if name in self.metrics:
            if labels:
                self.metrics[name].labels(**labels).set(value)
            else:
                self.metrics[name].set(value)
    
    def observe_histogram(self, name: str, value: float, labels: Dict[str, str] = None):
        """ヒストグラム観測"""
        if name in self.metrics:
            if labels:
                self.metrics[name].labels(**labels).observe(value)
            else:
                self.metrics[name].observe(value)
    
    def observe_summary(self, name: str, value: float, labels: Dict[str, str] = None):
        """サマリ観測"""
        if name in self.metrics:
            if labels:
                self.metrics[name].labels(**labels).observe(value)
            else:
                self.metrics[name].observe(value)
    
    def add_custom_metric(self, config: MetricConfig):
        """カスタムメトリック追加"""
        if config.metric_type == MetricType.COUNTER:
            self.metrics[config.name] = Counter(
                config.name,
                config.description,
                config.labels,
                registry=self.registry
            )
        elif config.metric_type == MetricType.HISTOGRAM:
            self.metrics[config.name] = Histogram(
                config.name,
                config.description,
                config.labels,
                buckets=config.buckets,
                registry=self.registry
            )
        elif config.metric_type == MetricType.GAUGE:
            self.metrics[config.name] = Gauge(
                config.name,
                config.description,
                config.labels,
                registry=self.registry
            )
        elif config.metric_type == MetricType.SUMMARY:
            self.metrics[config.name] = Summary(
                config.name,
                config.description,
                config.labels,
                registry=self.registry
            )
    
    def get_metrics_data(self) -> str:
        """メトリックスデータ取得（Prometheus形式）"""
        return generate_latest(self.registry).decode('utf-8')

# グローバルメトリックスコレクター
metrics_collector = MetricsCollector()

# デコレーター群
def measure_performance(metric_name: str = None, labels: Dict[str, str] = None):
    """パフォーマンス測定デコレーター"""
    def decorator(func):
        @wraps(func)
        async def async_wrapper(*args, **kwargs):
            start_time = time.time()
            function_name = metric_name or func.__name__
            module_name = func.__module__
            
            metric_labels = {
                "function_name": function_name,
                "module": module_name,
                **(labels or {})
            }
            
            try:
                result = await func(*args, **kwargs)
                
                # 成功メトリックス
                duration = time.time() - start_time
                metrics_collector.observe_histogram("function_duration", duration, metric_labels)
                metrics_collector.increment_counter(
                    "function_calls",
                    {**metric_labels, "status": "success"}
                )
                
                return result
                
            except Exception as e:
                # エラーメトリックス
                duration = time.time() - start_time
                metrics_collector.observe_histogram("function_duration", duration, metric_labels)
                metrics_collector.increment_counter(
                    "function_calls",
                    {**metric_labels, "status": "error", "error_type": type(e).__name__}
                )
                
                raise
        
        @wraps(func)
        def sync_wrapper(*args, **kwargs):
            start_time = time.time()
            function_name = metric_name or func.__name__
            module_name = func.__module__
            
            metric_labels = {
                "function_name": function_name,
                "module": module_name,
                **(labels or {})
            }
            
            try:
                result = func(*args, **kwargs)
                
                duration = time.time() - start_time
                metrics_collector.observe_histogram("function_duration", duration, metric_labels)
                metrics_collector.increment_counter(
                    "function_calls",
                    {**metric_labels, "status": "success"}
                )
                
                return result
                
            except Exception as e:
                duration = time.time() - start_time
                metrics_collector.observe_histogram("function_duration", duration, metric_labels)
                metrics_collector.increment_counter(
                    "function_calls",
                    {**metric_labels, "status": "error", "error_type": type(e).__name__}
                )
                
                raise
        
        return async_wrapper if asyncio.iscoroutinefunction(func) else sync_wrapper
    return decorator

def track_business_metric(metric_name: str, value: float = 1.0, labels: Dict[str, str] = None):
    """ビジネスメトリック追跡デコレーター"""
    def decorator(func):
        @wraps(func)
        async def async_wrapper(*args, **kwargs):
            try:
                result = await func(*args, **kwargs)
                
                # ビジネスメトリック記録
                metrics_collector.increment_counter(metric_name, labels, value)
                
                return result
                
            except Exception:
                # エラー時はメトリック記録しない
                raise
        
        @wraps(func)
        def sync_wrapper(*args, **kwargs):
            try:
                result = func(*args, **kwargs)
                
                metrics_collector.increment_counter(metric_name, labels, value)
                
                return result
                
            except Exception:
                raise
        
        return async_wrapper if asyncio.iscoroutinefunction(func) else sync_wrapper
    return decorator

# FastAPIメトリックスミドルウェア
class MetricsMiddleware(BaseHTTPMiddleware):
    """メトリックス収集ミドルウェア"""
    
    async def dispatch(self, request: Request, call_next):
        start_time = time.time()
        
        # リクエスト情報
        method = request.method
        path = request.url.path
        
        try:
            response = await call_next(request)
            status_code = str(response.status_code)
            
            # レスポンス時間記録
            duration = time.time() - start_time
            metrics_collector.observe_histogram(
                "http_request_duration",
                duration,
                {"method": method, "endpoint": path}
            )
            
            # リクエスト数記録
            metrics_collector.increment_counter(
                "http_requests",
                {"method": method, "endpoint": path, "status_code": status_code}
            )
            
            return response
            
        except Exception as e:
            # エラー時もメトリックス記録
            duration = time.time() - start_time
            metrics_collector.observe_histogram(
                "http_request_duration",
                duration,
                {"method": method, "endpoint": path}
            )
            
            metrics_collector.increment_counter(
                "http_requests",
                {"method": method, "endpoint": path, "status_code": "500"}
            )
            
            raise

# 使用例
class OrderService:
    """注文サービス例"""
    
    @measure_performance("create_order", {"service": "order"})
    @track_business_metric("orders_created", labels={"channel": "api"})
    async def create_order(self, order_data: dict) -> dict:
        """注文作成"""
        # 注文処理
        order = await self._process_order(order_data)
        
        # 売上メトリックス
        metrics_collector.increment_counter(
            "revenue_total",
            {"currency": order["currency"]},
            order["amount"]
        )
        
        # 在庫更新メトリックス
        for item in order["items"]:
            metrics_collector.set_gauge(
                "inventory_level",
                item["remaining_stock"],
                {"product_id": item["product_id"]}
            )
        
        return order
    
    async def _process_order(self, order_data: dict) -> dict:
        """注文処理内部ロジック"""
        # シミュレーション
        await asyncio.sleep(0.1)
        
        return {
            "id": "order_123",
            "amount": order_data["amount"],
            "currency": "USD",
            "items": [
                {"product_id": "prod_1", "remaining_stock": 100}
            ]
        }

# FastAPIアプリケーション
app = FastAPI()
app.add_middleware(MetricsMiddleware)

order_service = OrderService()

@app.get("/metrics")
async def get_metrics():
    """メトリックスエンドポイント"""
    return Response(
        metrics_collector.get_metrics_data(),
        media_type="text/plain; version=0.0.4; charset=utf-8"
    )

@app.post("/orders")
async def create_order_endpoint(order_data: dict):
    """注文作成エンドポイント"""
    order = await order_service.create_order(order_data)
    return {"order": order}

@app.get("/health")
async def health_check():
    """ヘルスチェックエンドポイント"""
    # システムヘルスメトリックス
    cpu_usage = psutil.cpu_percent()
    memory_usage = psutil.virtual_memory().percent
    
    health_status = "healthy" if cpu_usage < 80 and memory_usage < 80 else "unhealthy"
    
    metrics_collector.set_gauge("system_health_score", 1.0 if health_status == "healthy" else 0.0)
    
    return {
        "status": health_status,
        "cpu_usage": cpu_usage,
        "memory_usage": memory_usage,
        "timestamp": datetime.utcnow().isoformat()
    }
```

### 8.3 デバッグ・トレース

#### 8.3.1 高度なデバッグシステム

**Good: 包括的デバッグシステム**
```python
# 高度なデバッグ・トレーシングシステム
import sys
import traceback
import inspect
import threading
import time
import json
import uuid
from typing import Dict, List, Any, Optional, Callable, Union
from dataclasses import dataclass, field, asdict
from datetime import datetime
from enum import Enum
from functools import wraps
from contextvars import ContextVar
import linecache
import dis
from collections import defaultdict, deque
import psutil
import gc
import weakref
from opentelemetry import trace
from opentelemetry.trace import Status, StatusCode
from opentelemetry.exporter.jaeger.thrift import JaegerExporter
from opentelemetry.instrumentation.fastapi import FastAPIInstrumentor
from opentelemetry.instrumentation.sqlalchemy import SQLAlchemyInstrumentor
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor
from opentelemetry.instrumentation.requests import RequestsInstrumentor

class TraceLevel(Enum):
    """トレースレベル"""
    DISABLED = 0
    ERROR_ONLY = 1
    PERFORMANCE = 2
    DETAILED = 3
    VERBOSE = 4

@dataclass
class DebugContext:
    """デバッグコンテキスト"""
    trace_id: str
    span_id: str
    operation: str
    timestamp: datetime
    thread_id: int
    process_id: int
    memory_usage: float
    local_variables: Dict[str, Any] = field(default_factory=dict)
    stack_trace: List[str] = field(default_factory=list)
    performance_data: Dict[str, float] = field(default_factory=dict)

@dataclass 
class ExecutionStep:
    """実行ステップ情報"""
    step_id: str
    function_name: str
    file_name: str
    line_number: int
    timestamp: datetime
    duration: float
    variables: Dict[str, Any]
    return_value: Any = None
    exception: Optional[Exception] = None

class AdvancedDebugger:
    """高度デバッグシステム"""
    
    def __init__(self, level: TraceLevel = TraceLevel.PERFORMANCE):
        self.level = level
        self.execution_history: deque = deque(maxlen=1000)
        self.active_traces: Dict[str, DebugContext] = {}
        self.performance_data: Dict[str, List[float]] = defaultdict(list)
        self.memory_tracker = MemoryTracker()
        
        # グローバルトレーシング設定
        self._setup_tracing()
        
        # コード解析キャッシュ
        self.code_analysis_cache: Dict[str, Dict] = {}
        
        # スレッドローカルストレージ
        self.thread_local = threading.local()
    
    def _setup_tracing(self):
        """分散トレーシング設定"""
        # Jaegerエクスポーター設定
        jaeger_exporter = JaegerExporter(
            agent_host_name="localhost",
            agent_port=6831,
        )
        
        # トレーサープロバイダー設定
        trace.set_tracer_provider(TracerProvider())
        tracer_provider = trace.get_tracer_provider()
        
        # スパンプロセッサー追加
        span_processor = BatchSpanProcessor(jaeger_exporter)
        tracer_provider.add_span_processor(span_processor)
        
        self.tracer = trace.get_tracer(__name__)
    
    def create_debug_context(self, operation: str) -> DebugContext:
        """デバッグコンテキスト作成"""
        trace_id = str(uuid.uuid4())
        span_id = str(uuid.uuid4())
        
        context = DebugContext(
            trace_id=trace_id,
            span_id=span_id,
            operation=operation,
            timestamp=datetime.utcnow(),
            thread_id=threading.get_ident(),
            process_id=psutil.Process().pid,
            memory_usage=self._get_memory_usage()
        )
        
        self.active_traces[trace_id] = context
        return context
    
    def _get_memory_usage(self) -> float:
        """メモリ使用量取得"""
        process = psutil.Process()
        return process.memory_info().rss / 1024 / 1024  # MB
    
    def trace_function_execution(self, func: Callable, *args, **kwargs) -> Any:
        """関数実行トレーシング"""
        if self.level == TraceLevel.DISABLED:
            return func(*args, **kwargs)
        
        function_name = f"{func.__module__}.{func.__name__}"
        start_time = time.time()
        
        # OpenTelemetryスパン作成
        with self.tracer.start_as_current_span(function_name) as span:
            try:
                # コンテキスト情報設定
                span.set_attribute("function.module", func.__module__)
                span.set_attribute("function.name", func.__name__)
                span.set_attribute("function.args_count", len(args))
                span.set_attribute("function.kwargs_count", len(kwargs))
                
                # ローカル変数トレーシング
                if self.level >= TraceLevel.DETAILED:
                    local_vars = self._capture_local_variables(func, args, kwargs)
                    span.set_attribute("debug.local_variables", json.dumps(local_vars, default=str))
                
                # 関数実行
                result = func(*args, **kwargs)
                
                # パフォーマンスデータ記録
                duration = time.time() - start_time
                self.performance_data[function_name].append(duration)
                
                span.set_attribute("performance.duration", duration)
                span.set_attribute("performance.memory_delta", 
                                 self._get_memory_usage() - self.active_traces.get(
                                     span.get_span_context().trace_id.to_bytes(16, 'big').hex(), 
                                     DebugContext("", "", "", datetime.utcnow(), 0, 0, 0)
                                 ).memory_usage)
                
                # 成功ステータス
                span.set_status(Status(StatusCode.OK))
                
                return result
                
            except Exception as e:
                duration = time.time() - start_time
                
                # エラー情報記録
                span.set_status(Status(StatusCode.ERROR, str(e)))
                span.set_attribute("error.type", type(e).__name__)
                span.set_attribute("error.message", str(e))
                span.set_attribute("error.traceback", traceback.format_exc())
                
                # エラー時のコンテキスト保存
                if self.level >= TraceLevel.ERROR_ONLY:
                    self._save_error_context(func, args, kwargs, e)
                
                raise
    
    def _capture_local_variables(self, func: Callable, args: tuple, kwargs: dict) -> dict:
        """ローカル変数キャプチャ"""
        try:
            # 関数シグネチャ取得
            sig = inspect.signature(func)
            bound_args = sig.bind(*args, **kwargs)
            bound_args.apply_defaults()
            
            # 安全な値のみキャプチャ
            safe_vars = {}
            for name, value in bound_args.arguments.items():
                try:
                    # シリアライズ可能な値のみ
                    json.dumps(value, default=str)
                    safe_vars[name] = value
                except (TypeError, ValueError):
                    safe_vars[name] = f"<non-serializable: {type(value).__name__}>"
            
            return safe_vars
            
        except Exception:
            return {"error": "Failed to capture local variables"}
    
    def _save_error_context(self, func: Callable, args: tuple, kwargs: dict, error: Exception):
        """エラーコンテキスト保存"""
        error_context = {
            "function": f"{func.__module__}.{func.__name__}",
            "timestamp": datetime.utcnow().isoformat(),
            "error_type": type(error).__name__,
            "error_message": str(error),
            "traceback": traceback.format_exc(),
            "arguments": self._capture_local_variables(func, args, kwargs),
            "stack_frames": self._analyze_stack_frames(),
            "memory_usage": self._get_memory_usage(),
            "thread_id": threading.get_ident()
        }
        
        # エラーコンテキストをファイルに保存
        self._save_debug_snapshot(error_context)
    
    def _analyze_stack_frames(self) -> List[Dict[str, Any]]:
        """スタックフレーム解析"""
        frames = []
        
        for frame_info in inspect.stack()[1:]:
            frame_data = {
                "filename": frame_info.filename,
                "function": frame_info.function,
                "line_number": frame_info.lineno,
                "code_context": frame_info.code_context,
                "local_variables": {}
            }
            
            # ローカル変数収集（安全な値のみ）
            if self.level >= TraceLevel.VERBOSE:
                try:
                    for name, value in frame_info.frame.f_locals.items():
                        if not name.startswith('__'):
                            try:
                                json.dumps(value, default=str)
                                frame_data["local_variables"][name] = value
                            except (TypeError, ValueError):
                                frame_data["local_variables"][name] = f"<{type(value).__name__}>"
                except Exception:
                    pass
            
            frames.append(frame_data)
        
        return frames
    
    def _save_debug_snapshot(self, context: dict):
        """デバッグスナップショット保存"""
        timestamp = datetime.utcnow().strftime("%Y%m%d_%H%M%S")
        filename = f"debug_snapshot_{timestamp}_{uuid.uuid4().hex[:8]}.json"
        
        try:
            with open(f"debug/{filename}", 'w', encoding='utf-8') as f:
                json.dump(context, f, indent=2, ensure_ascii=False, default=str)
        except Exception as e:
            logger.error(f"Failed to save debug snapshot: {e}")
    
    def get_performance_summary(self) -> Dict[str, Dict[str, float]]:
        """パフォーマンスサマリ取得"""
        summary = {}
        
        for function_name, durations in self.performance_data.items():
            if durations:
                summary[function_name] = {
                    "count": len(durations),
                    "min": min(durations),
                    "max": max(durations),
                    "avg": sum(durations) / len(durations),
                    "total": sum(durations)
                }
        
        return summary
    
    def clear_history(self):
        """実行履歴クリア"""
        self.execution_history.clear()
        self.performance_data.clear()
        self.active_traces.clear()

class MemoryTracker:
    """メモリリークトラッカー"""
    
    def __init__(self):
        self.object_tracker: Dict[int, Dict] = {}
        self.allocation_history: deque = deque(maxlen=10000)
        self.gc_callbacks = []
        
        # ガベージコレクションコールバック
        gc.callbacks.append(self._gc_callback)
    
    def track_object(self, obj: Any, name: str = None):
        """オブジェクト追跡開始"""
        obj_id = id(obj)
        
        self.object_tracker[obj_id] = {
            "name": name or f"{type(obj).__name__}_{obj_id}",
            "type": type(obj).__name__,
            "created_at": datetime.utcnow(),
            "size": sys.getsizeof(obj),
            "references": len(gc.get_referents(obj)),
            "weak_ref": weakref.ref(obj, lambda ref: self._object_deleted(obj_id))
        }
    
    def _object_deleted(self, obj_id: int):
        """オブジェクト削除コールバック"""
        if obj_id in self.object_tracker:
            obj_info = self.object_tracker.pop(obj_id)
            lifetime = datetime.utcnow() - obj_info["created_at"]
            
            logger.debug(
                f"Object {obj_info['name']} deleted after {lifetime.total_seconds():.2f}s",
                category=LogCategory.PERFORMANCE,
                object_type=obj_info["type"],
                lifetime_seconds=lifetime.total_seconds(),
                object_size=obj_info["size"]
            )
    
    def _gc_callback(self, phase: str, info: dict):
        """ガベージコレクションコールバック"""
        gc_info = {
            "timestamp": datetime.utcnow(),
            "phase": phase,
            "generation": info.get("generation", -1),
            "collected": info.get("collected", 0),
            "uncollectable": info.get("uncollectable", 0)
        }
        
        self.allocation_history.append(gc_info)
        
        if info.get("uncollectable", 0) > 0:
            logger.warning(
                f"Garbage collection found {info['uncollectable']} uncollectable objects",
                category=LogCategory.PERFORMANCE,
                **gc_info
            )
    
    def get_memory_report(self) -> Dict[str, Any]:
        """メモリレポート取得"""
        process = psutil.Process()
        memory_info = process.memory_info()
        
        return {
            "process_memory": {
                "rss": memory_info.rss,
                "vms": memory_info.vms,
                "percent": process.memory_percent()
            },
            "tracked_objects": len(self.object_tracker),
            "gc_stats": gc.get_stats(),
            "recent_gc_events": list(self.allocation_history)[-10:]
        }

# グローバルデバッガー
debugger = AdvancedDebugger(TraceLevel.PERFORMANCE)

# デコレーター群
def debug_trace(level: TraceLevel = TraceLevel.PERFORMANCE, save_on_error: bool = True):
    """デバッグトレーシングデコレーター"""
    def decorator(func):
        @wraps(func)
        async def async_wrapper(*args, **kwargs):
            if debugger.level.value < level.value:
                return await func(*args, **kwargs)
            
            return debugger.trace_function_execution(func, *args, **kwargs)
        
        @wraps(func)
        def sync_wrapper(*args, **kwargs):
            if debugger.level.value < level.value:
                return func(*args, **kwargs)
            
            return debugger.trace_function_execution(func, *args, **kwargs)
        
        return async_wrapper if asyncio.iscoroutinefunction(func) else sync_wrapper
    return decorator

def memory_profile(track_objects: bool = False):
    """メモリプロファイルデコレーター"""
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            start_memory = debugger.memory_tracker._get_memory_usage()
            
            if track_objects:
                # 特定オブジェクトの追跡
                for i, arg in enumerate(args):
                    debugger.memory_tracker.track_object(arg, f"{func.__name__}_arg_{i}")
            
            try:
                result = func(*args, **kwargs)
                
                end_memory = debugger.memory_tracker._get_memory_usage()
                memory_delta = end_memory - start_memory
                
                logger.debug(
                    f"Memory usage for {func.__name__}: {memory_delta:.2f}MB",
                    category=LogCategory.PERFORMANCE,
                    function=func.__name__,
                    memory_start=start_memory,
                    memory_end=end_memory,
                    memory_delta=memory_delta
                )
                
                return result
                
            except Exception as e:
                end_memory = debugger.memory_tracker._get_memory_usage()
                logger.error(
                    f"Function {func.__name__} failed with memory delta: {end_memory - start_memory:.2f}MB",
                    error=e
                )
                raise
        
        return wrapper
    return decorator

# 使用例
class DataProcessor:
    """データ処理サービス例"""
    
    @debug_trace(TraceLevel.DETAILED)
    @memory_profile(track_objects=True)
    async def process_large_dataset(self, data: List[dict]) -> List[dict]:
        """大量データ処理"""
        logger.info(f"Processing dataset with {len(data)} items")
        
        processed_data = []
        
        for i, item in enumerate(data):
            try:
                processed_item = await self._process_item(item)
                processed_data.append(processed_item)
                
                # 進捗ログ
                if i % 1000 == 0:
                    logger.debug(
                        f"Processed {i}/{len(data)} items",
                        category=LogCategory.PERFORMANCE,
                        progress_percent=(i / len(data)) * 100
                    )
                
            except Exception as e:
                logger.error(
                    f"Failed to process item {i}",
                    error=e,
                    item_data=item,
                    processed_count=len(processed_data)
                )
                continue
        
        logger.info(
            f"Dataset processing completed: {len(processed_data)}/{len(data)} items",
            success_rate=(len(processed_data) / len(data)) * 100
        )
        
        return processed_data
    
    @debug_trace(TraceLevel.VERBOSE)
    async def _process_item(self, item: dict) -> dict:
        """個別アイテム処理"""
        # 処理シミュレーション
        await asyncio.sleep(0.001)
        
        # データ変換
        processed = {
            "id": item.get("id"),
            "processed_at": datetime.utcnow().isoformat(),
            "original_size": len(str(item)),
            "processed_data": item.get("data", {}).copy()
        }
        
        return processed

# FastAPI統合
from fastapi import FastAPI

app = FastAPI()

# OpenTelemetry自動計装
FastAPIInstrumentor.instrument_app(app)
SQLAlchemyInstrumentor().instrument()
RequestsInstrumentor().instrument()

data_processor = DataProcessor()

@app.post("/process-data")
async def process_data_endpoint(data: List[dict]):
    """データ処理エンドポイント"""
    try:
        result = await data_processor.process_large_dataset(data)
        return {"processed_data": result, "count": len(result)}
    except Exception as e:
        logger.error("Data processing endpoint failed", error=e)
        raise

@app.get("/debug/performance")
async def get_performance_summary():
    """パフォーマンスサマリ取得"""
    return {
        "performance_summary": debugger.get_performance_summary(),
        "memory_report": debugger.memory_tracker.get_memory_report()
    }

@app.post("/debug/clear-history")
async def clear_debug_history():
    """デバッグ履歴クリア"""
    debugger.clear_history()
    return {"message": "Debug history cleared"}
```

### 8.4 エラー追跡・アラート

#### 8.4.1 包括的エラー管理システム

**Good: エンタープライズレベルエラー管理**
```python
# 包括的エラー追跡・アラートシステム
import smtplib
import asyncio
import hashlib
from typing import Dict, List, Any, Optional, Callable, Union
from dataclasses import dataclass, field, asdict
from datetime import datetime, timedelta
from enum import Enum, IntEnum
from collections import defaultdict, deque
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
import json
import uuid
import traceback
import sys
import threading
import time
from functools import wraps
import httpx
import sentry_sdk
from sentry_sdk.integrations.fastapi import FastApiIntegration
from sentry_sdk.integrations.sqlalchemy import SqlalchemyIntegration
from sentry_sdk.integrations.asyncio import AsyncioIntegration
from slack_sdk import WebClient
from slack_sdk.errors import SlackApiError

class ErrorSeverity(IntEnum):
    """エラー重要度"""
    LOW = 1
    MEDIUM = 2
    HIGH = 3
    CRITICAL = 4
    EMERGENCY = 5

class AlertChannel(Enum):
    """アラートチャンネル"""
    EMAIL = "email"
    SLACK = "slack"
    SMS = "sms"
    WEBHOOK = "webhook"
    PAGERDUTY = "pagerduty"

class ErrorCategory(Enum):
    """エラーカテゴリ"""
    APPLICATION = "application"
    SYSTEM = "system"
    DATABASE = "database"
    NETWORK = "network"
    SECURITY = "security"
    PERFORMANCE = "performance"
    BUSINESS_LOGIC = "business_logic"
    EXTERNAL_API = "external_api"

@dataclass
class ErrorDetails:
    """エラー詳細情報"""
    error_id: str
    timestamp: datetime
    severity: ErrorSeverity
    category: ErrorCategory
    message: str
    exception_type: str
    stack_trace: str
    user_id: Optional[str] = None
    session_id: Optional[str] = None
    request_id: Optional[str] = None
    endpoint: Optional[str] = None
    method: Optional[str] = None
    user_agent: Optional[str] = None
    ip_address: Optional[str] = None
    environment: str = "production"
    service_name: str = "default"
    version: Optional[str] = None
    additional_data: Dict[str, Any] = field(default_factory=dict)
    occurrence_count: int = 1
    first_seen: Optional[datetime] = None
    last_seen: Optional[datetime] = None
    resolved: bool = False
    fingerprint: Optional[str] = None

@dataclass
class AlertRule:
    """アラートルール"""
    name: str
    description: str
    severity_threshold: ErrorSeverity
    category_filter: Optional[List[ErrorCategory]] = None
    error_count_threshold: int = 1
    time_window_minutes: int = 5
    channels: List[AlertChannel] = field(default_factory=list)
    recipients: List[str] = field(default_factory=list)
    enabled: bool = True
    conditions: Dict[str, Any] = field(default_factory=dict)
    suppression_time_minutes: int = 60  # 重複アラート拑制

class ErrorTracker:
    """エラー追跡システム"""
    
    def __init__(self, config: Dict[str, Any] = None):
        self.config = config or {}
        self.errors_history: deque = deque(maxlen=10000)
        self.error_fingerprints: Dict[str, ErrorDetails] = {}
        self.alert_rules: List[AlertRule] = []
        self.suppressed_alerts: Dict[str, datetime] = {}
        
        # アラートチャンネル初期化
        self.alert_channels = self._initialize_alert_channels()
        
        # Sentry統合
        self._setup_sentry()
        
        # デフォルトアラートルール設定
        self._setup_default_alert_rules()
        
        # バックグラウンド処理開始
        self._start_background_tasks()
    
    def _setup_sentry(self):
        """セントリ設定"""
        sentry_dsn = self.config.get("sentry_dsn")
        if sentry_dsn:
            sentry_sdk.init(
                dsn=sentry_dsn,
                integrations=[
                    FastApiIntegration(),
                    SqlalchemyIntegration(),
                    AsyncioIntegration()
                ],
                traces_sample_rate=0.1,
                profiles_sample_rate=0.1,
                environment=self.config.get("environment", "production")
            )
    
    def _initialize_alert_channels(self) -> Dict[AlertChannel, Any]:
        """アラートチャンネル初期化"""
        channels = {}
        
        # Slackクライアント
        slack_token = self.config.get("slack_token")
        if slack_token:
            channels[AlertChannel.SLACK] = WebClient(token=slack_token)
        
        # メール設定
        smtp_config = self.config.get("smtp")
        if smtp_config:
            channels[AlertChannel.EMAIL] = smtp_config
        
        return channels
    
    def _setup_default_alert_rules(self):
        """デフォルトアラートルール設定"""
        # 緊急アラート
        self.alert_rules.append(AlertRule(
            name="emergency_errors",
            description="Emergency level errors",
            severity_threshold=ErrorSeverity.EMERGENCY,
            error_count_threshold=1,
            time_window_minutes=1,
            channels=[AlertChannel.SLACK, AlertChannel.EMAIL],
            recipients=self.config.get("emergency_contacts", []),
            suppression_time_minutes=5
        ))
        
        # クリティカルアラート
        self.alert_rules.append(AlertRule(
            name="critical_errors",
            description="Critical errors requiring immediate attention",
            severity_threshold=ErrorSeverity.CRITICAL,
            error_count_threshold=3,
            time_window_minutes=5,
            channels=[AlertChannel.SLACK],
            recipients=self.config.get("critical_contacts", []),
            suppression_time_minutes=30
        ))
        
        # セキュリティアラート
        self.alert_rules.append(AlertRule(
            name="security_incidents",
            description="Security related errors",
            severity_threshold=ErrorSeverity.MEDIUM,
            category_filter=[ErrorCategory.SECURITY],
            error_count_threshold=1,
            time_window_minutes=1,
            channels=[AlertChannel.SLACK, AlertChannel.EMAIL],
            recipients=self.config.get("security_contacts", []),
            suppression_time_minutes=15
        ))
        
        # パフォーマンスアラート
        self.alert_rules.append(AlertRule(
            name="performance_degradation",
            description="Performance related issues",
            severity_threshold=ErrorSeverity.HIGH,
            category_filter=[ErrorCategory.PERFORMANCE],
            error_count_threshold=10,
            time_window_minutes=5,
            channels=[AlertChannel.SLACK],
            recipients=self.config.get("performance_contacts", []),
            suppression_time_minutes=120
        ))
    
    def track_error(self, error: Exception, **context) -> str:
        """エラー追跡"""
        error_id = str(uuid.uuid4())
        
        # エラーフィンガープリント生成
        fingerprint = self._generate_error_fingerprint(error, context)
        
        # エラー詳細情報作成
        error_details = ErrorDetails(
            error_id=error_id,
            timestamp=datetime.utcnow(),
            severity=self._determine_severity(error, context),
            category=self._categorize_error(error, context),
            message=str(error),
            exception_type=type(error).__name__,
            stack_trace=traceback.format_exc(),
            fingerprint=fingerprint,
            **{k: v for k, v in context.items() if k in [
                'user_id', 'session_id', 'request_id', 'endpoint', 'method',
                'user_agent', 'ip_address', 'environment', 'service_name', 'version'
            ]},
            additional_data={k: v for k, v in context.items() if k not in [
                'user_id', 'session_id', 'request_id', 'endpoint', 'method',
                'user_agent', 'ip_address', 'environment', 'service_name', 'version'
            ]}
        )
        
        # 重複エラーチェック
        if fingerprint in self.error_fingerprints:
            existing_error = self.error_fingerprints[fingerprint]
            existing_error.occurrence_count += 1
            existing_error.last_seen = datetime.utcnow()
            error_details = existing_error
        else:
            error_details.first_seen = datetime.utcnow()
            error_details.last_seen = datetime.utcnow()
            self.error_fingerprints[fingerprint] = error_details
        
        # エラー履歴に追加
        self.errors_history.append(error_details)
        
        # Sentryに送信
        if 'sentry_dsn' in self.config:
            sentry_sdk.capture_exception(error)
        
        # アラートチェック
        asyncio.create_task(self._check_alert_rules(error_details))
        
        # ログ出力
        logger.error(
            f"Error tracked: {error_details.message}",
            category=LogCategory.ERROR,
            error_id=error_id,
            fingerprint=fingerprint,
            severity=error_details.severity.name,
            error_category=error_details.category.value,
            occurrence_count=error_details.occurrence_count
        )
        
        return error_id
    
    def _generate_error_fingerprint(self, error: Exception, context: dict) -> str:
        """エラーフィンガープリント生成"""
        # エラータイプ、メッセージ、スタックトレースの一部を使用
        stack_lines = traceback.format_exc().split('\n')
        relevant_stack = [line for line in stack_lines if 'File "' in line][:3]
        
        fingerprint_data = {
            "type": type(error).__name__,
            "message": str(error)[:200],  # メッセージの一部
            "stack": relevant_stack,
            "endpoint": context.get("endpoint"),
            "method": context.get("method")
        }
        
        fingerprint_str = json.dumps(fingerprint_data, sort_keys=True)
        return hashlib.md5(fingerprint_str.encode()).hexdigest()
    
    def _determine_severity(self, error: Exception, context: dict) -> ErrorSeverity:
        """エラー重要度判定"""
        error_type = type(error).__name__
        
        # 重大エラー
        if error_type in ['SystemExit', 'KeyboardInterrupt', 'MemoryError']:
            return ErrorSeverity.EMERGENCY
        
        # セキュリティ関連
        if 'security' in str(error).lower() or 'unauthorized' in str(error).lower():
            return ErrorSeverity.CRITICAL
            
        # HTTPステータスコードベース
        status_code = context.get('status_code')
        if status_code:
            if status_code >= 500:
                return ErrorSeverity.HIGH
            elif status_code >= 400:
                return ErrorSeverity.MEDIUM
        
        # データベースエラー
        if 'database' in str(error).lower() or 'sql' in str(error).lower():
            return ErrorSeverity.HIGH
        
        # パフォーマンス関連
        if 'timeout' in str(error).lower() or 'performance' in str(error).lower():
            return ErrorSeverity.MEDIUM
        
        return ErrorSeverity.LOW
    
    def _categorize_error(self, error: Exception, context: dict) -> ErrorCategory:
        """エラーカテゴリ分類"""
        error_str = str(error).lower()
        error_type = type(error).__name__.lower()
        
        # セキュリティ
        if any(keyword in error_str for keyword in ['unauthorized', 'forbidden', 'security', 'authentication']):
            return ErrorCategory.SECURITY
        
        # データベース
        if any(keyword in error_str for keyword in ['database', 'sql', 'connection', 'query']):
            return ErrorCategory.DATABASE
            
        # ネットワーク
        if any(keyword in error_str for keyword in ['network', 'connection', 'timeout', 'unreachable']):
            return ErrorCategory.NETWORK
            
        # パフォーマンス
        if any(keyword in error_str for keyword in ['timeout', 'slow', 'performance', 'memory']):
            return ErrorCategory.PERFORMANCE
            
        # 外部API
        if any(keyword in error_str for keyword in ['api', 'http', 'request', 'response']):
            return ErrorCategory.EXTERNAL_API
            
        # システム
        if any(keyword in error_type for keyword in ['system', 'os', 'memory', 'disk']):
            return ErrorCategory.SYSTEM
        
        return ErrorCategory.APPLICATION
    
    async def _check_alert_rules(self, error_details: ErrorDetails):
        """アラートルールチェック"""
        for rule in self.alert_rules:
            if not rule.enabled:
                continue
            
            if await self._should_trigger_alert(rule, error_details):
                await self._send_alert(rule, error_details)
    
    async def _should_trigger_alert(self, rule: AlertRule, error_details: ErrorDetails) -> bool:
        """アラートトリガー判定"""
        # 重要度チェック
        if error_details.severity < rule.severity_threshold:
            return False
        
        # カテゴリフィルター
        if rule.category_filter and error_details.category not in rule.category_filter:
            return False
        
        # 重複アラート拑制チェック
        suppression_key = f"{rule.name}_{error_details.fingerprint}"
        if suppression_key in self.suppressed_alerts:
            suppression_end = self.suppressed_alerts[suppression_key]
            if datetime.utcnow() < suppression_end:
                return False
        
        # 時間窓内のエラー数チェック
        time_threshold = datetime.utcnow() - timedelta(minutes=rule.time_window_minutes)
        recent_errors = [
            error for error in self.errors_history
            if error.timestamp >= time_threshold and
               error.fingerprint == error_details.fingerprint
        ]
        
        if len(recent_errors) >= rule.error_count_threshold:
            # 拑制時間設定
            self.suppressed_alerts[suppression_key] = (
                datetime.utcnow() + timedelta(minutes=rule.suppression_time_minutes)
            )
            return True
        
        return False
    
    async def _send_alert(self, rule: AlertRule, error_details: ErrorDetails):
        """アラート送信"""
        alert_message = self._format_alert_message(rule, error_details)
        
        for channel in rule.channels:
            try:
                if channel == AlertChannel.SLACK:
                    await self._send_slack_alert(rule, error_details, alert_message)
                elif channel == AlertChannel.EMAIL:
                    await self._send_email_alert(rule, error_details, alert_message)
                elif channel == AlertChannel.WEBHOOK:
                    await self._send_webhook_alert(rule, error_details, alert_message)
                    
            except Exception as e:
                logger.error(
                    f"Failed to send alert via {channel.value}",
                    error=e,
                    rule_name=rule.name,
                    error_id=error_details.error_id
                )
    
    def _format_alert_message(self, rule: AlertRule, error_details: ErrorDetails) -> Dict[str, str]:
        """アラートメッセージフォーマット"""
        return {
            "title": f"🚨 {rule.name.upper()}: {error_details.exception_type}",
            "summary": error_details.message[:200],
            "details": f"""
**Error ID:** {error_details.error_id}
**Severity:** {error_details.severity.name}
**Category:** {error_details.category.value}
**Service:** {error_details.service_name}
**Environment:** {error_details.environment}
**Occurrence Count:** {error_details.occurrence_count}
**First Seen:** {error_details.first_seen}
**Last Seen:** {error_details.last_seen}

**Error Message:**
```
{error_details.message}
```

**Stack Trace:**
```
{error_details.stack_trace[:1000]}...
```
            """.strip()
        }
    
    async def _send_slack_alert(self, rule: AlertRule, error_details: ErrorDetails, message: Dict[str, str]):
        """スラックアラート送信"""
        if AlertChannel.SLACK not in self.alert_channels:
            return
        
        slack_client = self.alert_channels[AlertChannel.SLACK]
        
        # 重要度による色分け
        color_map = {
            ErrorSeverity.LOW: "good",
            ErrorSeverity.MEDIUM: "warning",
            ErrorSeverity.HIGH: "warning",
            ErrorSeverity.CRITICAL: "danger",
            ErrorSeverity.EMERGENCY: "danger"
        }
        
        blocks = [
            {
                "type": "header",
                "text": {
                    "type": "plain_text",
                    "text": message["title"]
                }
            },
            {
                "type": "section",
                "text": {
                    "type": "mrkdwn",
                    "text": message["details"]
                }
            },
            {
                "type": "actions",
                "elements": [
                    {
                        "type": "button",
                        "text": {
                            "type": "plain_text",
                            "text": "View Details"
                        },
                        "url": f"{self.config.get('dashboard_url', '')}/errors/{error_details.error_id}"
                    },
                    {
                        "type": "button",
                        "text": {
                            "type": "plain_text",
                            "text": "Mark as Resolved"
                        },
                        "value": error_details.error_id,
                        "action_id": "resolve_error"
                    }
                ]
            }
        ]
        
        try:
            for recipient in rule.recipients:
                await slack_client.chat_postMessage(
                    channel=recipient,
                    blocks=blocks,
                    attachments=[{
                        "color": color_map.get(error_details.severity, "warning"),
                        "fallback": message["summary"]
                    }]
                )
        except SlackApiError as e:
            logger.error(f"Slack alert failed: {e.response['error']}")
    
    async def _send_email_alert(self, rule: AlertRule, error_details: ErrorDetails, message: Dict[str, str]):
        """メールアラート送信"""
        if AlertChannel.EMAIL not in self.alert_channels:
            return
        
        smtp_config = self.alert_channels[AlertChannel.EMAIL]
        
        msg = MIMEMultipart()
        msg['From'] = smtp_config['from_email']
        msg['Subject'] = message["title"]
        
        # HTMLメール本文
        html_body = f"""
        <html>
        <body>
            <h2>{message["title"]}</h2>
            <pre>{message["details"]}</pre>
            <hr>
            <p><a href="{self.config.get('dashboard_url', '')}/errors/{error_details.error_id}">View Full Details</a></p>
        </body>
        </html>
        """
        
        msg.attach(MIMEText(html_body, 'html'))
        
        try:
            with smtplib.SMTP(smtp_config['host'], smtp_config['port']) as server:
                if smtp_config.get('use_tls'):
                    server.starttls()
                if smtp_config.get('username'):
                    server.login(smtp_config['username'], smtp_config['password'])
                    
                for recipient in rule.recipients:
                    msg['To'] = recipient
                    server.send_message(msg)
                    del msg['To']
                    
        except Exception as e:
            logger.error(f"Email alert failed: {e}")
    
    async def _send_webhook_alert(self, rule: AlertRule, error_details: ErrorDetails, message: Dict[str, str]):
        """ウェブフックアラート送信"""
        webhook_url = self.config.get('webhook_url')
        if not webhook_url:
            return
        
        payload = {
            "alert_rule": rule.name,
            "error_details": asdict(error_details),
            "message": message,
            "timestamp": datetime.utcnow().isoformat()
        }
        
        try:
            async with httpx.AsyncClient() as client:
                response = await client.post(
                    webhook_url,
                    json=payload,
                    timeout=10.0
                )
                response.raise_for_status()
        except Exception as e:
            logger.error(f"Webhook alert failed: {e}")
    
    def _start_background_tasks(self):
        """バックグラウンドタスク開始"""
        def cleanup_old_data():
            while True:
                try:
                    # 古いエラーデータクリーンアップ
                    cutoff_time = datetime.utcnow() - timedelta(days=7)
                    
                    # 古いフィンガープリント削除
                    old_fingerprints = [
                        fp for fp, error in self.error_fingerprints.items()
                        if error.last_seen < cutoff_time
                    ]
                    
                    for fp in old_fingerprints:
                        del self.error_fingerprints[fp]
                    
                    # 古いアラート拑制削除
                    current_time = datetime.utcnow()
                    expired_suppressions = [
                        key for key, expiry in self.suppressed_alerts.items()
                        if current_time >= expiry
                    ]
                    
                    for key in expired_suppressions:
                        del self.suppressed_alerts[key]
                    
                    logger.debug(
                        f"Cleaned up {len(old_fingerprints)} old error fingerprints and {len(expired_suppressions)} expired alert suppressions"
                    )
                    
                except Exception as e:
                    logger.error("Error cleanup task failed", error=e)
                
                time.sleep(3600)  # 1時間ごとに実行
        
        cleanup_thread = threading.Thread(target=cleanup_old_data, daemon=True)
        cleanup_thread.start()
    
    def get_error_statistics(self, hours: int = 24) -> Dict[str, Any]:
        """エラー統計取得"""
        cutoff_time = datetime.utcnow() - timedelta(hours=hours)
        recent_errors = [error for error in self.errors_history if error.timestamp >= cutoff_time]
        
        # カテゴリ別統計
        category_stats = defaultdict(int)
        severity_stats = defaultdict(int)
        
        for error in recent_errors:
            category_stats[error.category.value] += 1
            severity_stats[error.severity.name] += 1
        
        return {
            "total_errors": len(recent_errors),
            "unique_errors": len(set(error.fingerprint for error in recent_errors)),
            "category_breakdown": dict(category_stats),
            "severity_breakdown": dict(severity_stats),
            "time_period_hours": hours,
            "most_frequent_errors": [
                {
                    "fingerprint": fp,
                    "count": error.occurrence_count,
                    "message": error.message[:100],
                    "severity": error.severity.name
                }
                for fp, error in sorted(
                    self.error_fingerprints.items(),
                    key=lambda x: x[1].occurrence_count,
                    reverse=True
                )[:10]
            ]
        }

# グローバルエラートラッカー
error_tracker = ErrorTracker()

# デコレーター
def track_errors(category: ErrorCategory = ErrorCategory.APPLICATION):
    """エラー追跡デコレーター"""
    def decorator(func):
        @wraps(func)
        async def async_wrapper(*args, **kwargs):
            try:
                return await func(*args, **kwargs)
            except Exception as e:
                error_tracker.track_error(
                    e,
                    function=func.__name__,
                    module=func.__module__,
                    category=category.value
                )
                raise
        
        @wraps(func)
        def sync_wrapper(*args, **kwargs):
            try:
                return func(*args, **kwargs)
            except Exception as e:
                error_tracker.track_error(
                    e,
                    function=func.__name__,
                    module=func.__module__,
                    category=category.value
                )
                raise
        
        return async_wrapper if asyncio.iscoroutinefunction(func) else sync_wrapper
    return decorator

# FastAPI統合
from fastapi import FastAPI, Request, HTTPException
from fastapi.responses import JSONResponse

app = FastAPI()

@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    """グラーバル例外ハンドラー"""
    error_id = error_tracker.track_error(
        exc,
        endpoint=str(request.url.path),
        method=request.method,
        user_agent=request.headers.get("user-agent"),
        ip_address=request.client.host,
        query_params=dict(request.query_params)
    )
    
    return JSONResponse(
        status_code=500,
        content={
            "error": "Internal server error",
            "error_id": error_id,
            "message": "An error occurred while processing your request"
        }
    )

@app.get("/errors/statistics")
async def get_error_statistics(hours: int = 24):
    """エラー統計取得エンドポイント"""
    return error_tracker.get_error_statistics(hours)

@app.post("/errors/{error_id}/resolve")
async def resolve_error(error_id: str):
    """エラー解決マーク"""
    # エラー解決処理
    for error in error_tracker.error_fingerprints.values():
        if error.error_id == error_id:
            error.resolved = True
            logger.info(f"Error {error_id} marked as resolved")
            return {"message": "Error marked as resolved"}
    
    raise HTTPException(status_code=404, detail="Error not found")

# 使用例
class PaymentService:
    """支払サービス例"""
    
    @track_errors(ErrorCategory.BUSINESS_LOGIC)
    async def process_payment(self, payment_data: dict) -> dict:
        """支払処理"""
        if payment_data["amount"] <= 0:
            raise ValueError("Payment amount must be positive")
        
        if not payment_data.get("card_number"):
            raise ValueError("Card number is required")
        
        # 支払処理シミュレーション
        if payment_data["amount"] > 10000:
            raise Exception("Payment amount exceeds limit")
        
        return {
            "payment_id": str(uuid.uuid4()),
            "status": "completed",
            "amount": payment_data["amount"]
        }

payment_service = PaymentService()

@app.post("/payments")
async def process_payment_endpoint(payment_data: dict):
    """支払処理エンドポイント"""
    result = await payment_service.process_payment(payment_data)
    return result
```

---

## 9. デプロイメント・CI/CD

### 9.1 Dockerコンテナ化

#### 9.1.1 プロダクショングレードDocker実装

**Good: 最適化されたDockerfile**
```dockerfile
# プロダクショングレードDockerfile
# マルチステージビルドで最適化

# ベースイメージ: Python 3.11 slim
FROM python:3.11-slim as base

# メタデータラベル
LABEL maintainer="development-team@company.com" \
      version="1.0.0" \
      description="Production Python application" \
      build-date="2024-01-15"

# システムパッケージ更新と必要パッケージインストール
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    curl \
    libpq-dev \
    libssl-dev \
    libffi-dev \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

# Python最適化設定
ENV PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1 \
    PYTHONHASHSEED=random \
    PIP_NO_CACHE_DIR=1 \
    PIP_DISABLE_PIP_VERSION_CHECK=1

# 非特権ユーザー作成
RUN groupadd -r appuser && useradd -r -g appuser appuser

# アプリケーションディレクトリ作成
WORKDIR /app

# == 依存関係インストールステージ ==
FROM base as dependencies

# pip更新
RUN pip install --upgrade pip setuptools wheel

# 依存関係ファイルコピー
COPY requirements.txt requirements-prod.txt ./

# 依存関係インストール（ビルドキャッシュ最適化）
RUN pip install --no-cache-dir -r requirements-prod.txt

# == アプリケーションステージ ==
FROM base as application

# 依存関係を前ステージからコピー
COPY --from=dependencies /usr/local/lib/python3.11/site-packages /usr/local/lib/python3.11/site-packages
COPY --from=dependencies /usr/local/bin /usr/local/bin

# アプリケーションコードコピー
COPY --chown=appuser:appuser . .

# 設定ファイルとスクリプトの実行権限設定
RUN chmod +x scripts/*.sh && \
    mkdir -p logs tmp && \
    chown -R appuser:appuser logs tmp

# ヘルスチェックスクリプト追加
COPY --chown=appuser:appuser scripts/healthcheck.py /usr/local/bin/
RUN chmod +x /usr/local/bin/healthcheck.py

# ユーザー切り替え
USER appuser

# ポート公開
EXPOSE 8000

# ヘルスチェック設定
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD python /usr/local/bin/healthcheck.py

# アプリケーション起動コマンド
CMD ["gunicorn", "--bind", "0.0.0.0:8000", "--workers", "4", "--worker-class", "uvicorn.workers.UvicornWorker", "app.main:app"]
```

**ヘルスチェックスクリプト**
```python
# scripts/healthcheck.py
#!/usr/bin/env python3
"""
Dockerヘルスチェックスクリプト
"""

import sys
import requests
import json
from datetime import datetime

def main():
    """Health check main function"""
    try:
        # アプリケーションヘルスチェック
        response = requests.get(
            "http://localhost:8000/health",
            timeout=5,
            headers={"User-Agent": "Docker-HealthCheck/1.0"}
        )
        
        if response.status_code == 200:
            health_data = response.json()
            
            # 詳細チェック
            if health_data.get("status") == "healthy":
                print(f"Health check passed: {datetime.utcnow().isoformat()}")
                sys.exit(0)
            else:
                print(f"Health check failed: {health_data.get('message', 'Unknown error')}")
                sys.exit(1)
        else:
            print(f"Health check endpoint returned {response.status_code}")
            sys.exit(1)
            
    except requests.RequestException as e:
        print(f"Health check request failed: {e}")
        sys.exit(1)
    except Exception as e:
        print(f"Health check error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
```

**Docker Composeプロダクション設定**
```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
      target: application
    image: myapp:${VERSION:-latest}
    container_name: myapp-prod
    restart: unless-stopped
    
    environment:
      - ENVIRONMENT=production
      - DATABASE_URL=${DATABASE_URL}
      - REDIS_URL=${REDIS_URL}
      - SECRET_KEY=${SECRET_KEY}
      - LOG_LEVEL=INFO
      - WORKERS=4
    
    ports:
      - "8000:8000"
    
    volumes:
      - ./logs:/app/logs
      - ./config:/app/config:ro
    
    depends_on:
      - db
      - redis
      - monitoring
    
    networks:
      - app-network
    
    deploy:
      resources:
        limits:
          memory: 512M
          cpus: '0.5'
        reservations:
          memory: 256M
          cpus: '0.25'
    
    healthcheck:
      test: ["CMD", "python", "/usr/local/bin/healthcheck.py"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    
    logging:
      driver: "json-file"
      options:
        max-size: "100m"
        max-file: "5"

  db:
    image: postgres:15-alpine
    container_name: myapp-db
    restart: unless-stopped
    
    environment:
      - POSTGRES_DB=${DB_NAME}
      - POSTGRES_USER=${DB_USER}
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./scripts/init-db.sql:/docker-entrypoint-initdb.d/init-db.sql:ro
    
    networks:
      - app-network
    
    deploy:
      resources:
        limits:
          memory: 256M
          cpus: '0.3'
    
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER} -d ${DB_NAME}"]
      interval: 30s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: myapp-redis
    restart: unless-stopped
    
    command: redis-server --appendonly yes --requirepass ${REDIS_PASSWORD}
    
    volumes:
      - redis_data:/data
    
    networks:
      - app-network
    
    deploy:
      resources:
        limits:
          memory: 128M
          cpus: '0.2'
    
    healthcheck:
      test: ["CMD", "redis-cli", "--raw", "incr", "ping"]
      interval: 30s
      timeout: 3s
      retries: 5

  nginx:
    image: nginx:alpine
    container_name: myapp-nginx
    restart: unless-stopped
    
    ports:
      - "80:80"
      - "443:443"
    
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/conf.d:/etc/nginx/conf.d:ro
      - ./ssl:/etc/ssl/certs:ro
      - nginx_logs:/var/log/nginx
    
    depends_on:
      - app
    
    networks:
      - app-network
    
    deploy:
      resources:
        limits:
          memory: 64M
          cpus: '0.1'

  monitoring:
    image: prom/prometheus:latest
    container_name: myapp-monitoring
    restart: unless-stopped
    
    ports:
      - "9090:9090"
    
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml:ro
      - prometheus_data:/prometheus
    
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
      - '--storage.tsdb.retention.time=15d'
      - '--web.enable-lifecycle'
    
    networks:
      - app-network

volumes:
  postgres_data:
    driver: local
  redis_data:
    driver: local
  prometheus_data:
    driver: local
  nginx_logs:
    driver: local

networks:
  app-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.20.0.0/16
```

**Bad: 非最適化Dockerfile**
```dockerfile
# 非最適化Dockerfile例
FROM python:3.11  # 問題: 容量が大きいフルイメージ

# 問題: rootユーザーで実行

# 問題: レイヤーキャッシュの無駄
COPY . /app  # ソースコード全体を先にコピー
WORKDIR /app

# 問題: 毎回依存関係インストール
RUN pip install -r requirements.txt

# 問題: ヘルスチェックなし
# 問題: メタデータなし
# 問題: セキュリティ考慮なし

CMD ["python", "app.py"]  # 問題: 本番環境に不適切
```

### 9.2 CI/CDパイプライン

#### 9.2.1 GitHub Actionsプロダクションパイプライン

**Good: 包括的CI/CDパイプライン**
```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]
  release:
    types: [ published ]

env:
  PYTHON_VERSION: '3.11'
  NODE_VERSION: '18'
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}

jobs:
  # == コード品質チェック ==
  quality-checks:
    name: Quality Checks
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
          POSTGRES_DB: test_db
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
      
      redis:
        image: redis:7
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 6379:6379
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
      with:
        fetch-depth: 0  # 完全な履歴取得
    
    - name: Set up Python
      uses: actions/setup-python@v4
      with:
        python-version: ${{ env.PYTHON_VERSION }}
        cache: 'pip'
    
    - name: Install dependencies
      run: |
        python -m pip install --upgrade pip
        pip install -r requirements-dev.txt
        pip install -r requirements.txt
    
    - name: Code formatting check (Black)
      run: |
        black --check --diff .
    
    - name: Import sorting check (isort)
      run: |
        isort --check-only --diff .
    
    - name: Linting (flake8)
      run: |
        flake8 . --count --select=E9,F63,F7,F82 --show-source --statistics
        flake8 . --count --max-complexity=10 --max-line-length=88 --statistics
    
    - name: Type checking (mypy)
      run: |
        mypy . --config-file pyproject.toml
    
    - name: Security scan (bandit)
      run: |
        bandit -r . -f json -o bandit-report.json
        bandit -r . --severity-level medium
    
    - name: Dependency vulnerability scan (safety)
      run: |
        safety check --json --output safety-report.json
        safety check
    
    - name: Code complexity analysis (radon)
      run: |
        radon cc . --average --show-complexity
        radon mi . --show
    
    - name: Upload security reports
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: security-reports
        path: |
          bandit-report.json
          safety-report.json

  # == テスト実行 ==
  tests:
    name: Tests
    runs-on: ubuntu-latest
    needs: quality-checks
    
    strategy:
      matrix:
        python-version: ['3.10', '3.11', '3.12']
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
          POSTGRES_DB: test_db
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
      
      redis:
        image: redis:7
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 6379:6379
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Set up Python ${{ matrix.python-version }}
      uses: actions/setup-python@v4
      with:
        python-version: ${{ matrix.python-version }}
        cache: 'pip'
    
    - name: Install dependencies
      run: |
        python -m pip install --upgrade pip
        pip install -r requirements-dev.txt
        pip install -r requirements.txt
    
    - name: Run unit tests
      env:
        DATABASE_URL: postgresql://postgres:postgres@localhost:5432/test_db
        REDIS_URL: redis://localhost:6379/0
        SECRET_KEY: test-secret-key
        ENVIRONMENT: test
      run: |
        pytest tests/unit/ -v --cov=app --cov-report=xml --cov-report=html
    
    - name: Run integration tests
      env:
        DATABASE_URL: postgresql://postgres:postgres@localhost:5432/test_db
        REDIS_URL: redis://localhost:6379/0
        SECRET_KEY: test-secret-key
        ENVIRONMENT: test
      run: |
        pytest tests/integration/ -v
    
    - name: Run E2E tests
      env:
        DATABASE_URL: postgresql://postgres:postgres@localhost:5432/test_db
        REDIS_URL: redis://localhost:6379/0
        SECRET_KEY: test-secret-key
        ENVIRONMENT: test
      run: |
        pytest tests/e2e/ -v --maxfail=1
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        file: ./coverage.xml
        flags: unittests
        name: codecov-umbrella
        fail_ci_if_error: false
    
    - name: Upload test results
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-results-${{ matrix.python-version }}
        path: |
          htmlcov/
          coverage.xml
          pytest-report.xml

  # == パフォーマンステスト ==
  performance-tests:
    name: Performance Tests
    runs-on: ubuntu-latest
    needs: [quality-checks, tests]
    if: github.event_name == 'pull_request' || github.ref == 'refs/heads/main'
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Set up Python
      uses: actions/setup-python@v4
      with:
        python-version: ${{ env.PYTHON_VERSION }}
        cache: 'pip'
    
    - name: Install dependencies
      run: |
        python -m pip install --upgrade pip
        pip install -r requirements.txt
        pip install locust pytest-benchmark
    
    - name: Run performance benchmarks
      run: |
        pytest tests/performance/ --benchmark-json=benchmark.json
    
    - name: Run load tests
      run: |
        locust -f tests/load/locustfile.py --headless --users 100 --spawn-rate 10 --run-time 5m --host http://localhost:8000 &
        python app/main.py &
        sleep 10
        wait
    
    - name: Upload performance reports
      uses: actions/upload-artifact@v3
      with:
        name: performance-reports
        path: |
          benchmark.json
          locust-report.html

  # == Dockerイメージビルド ==
  build-image:
    name: Build Docker Image
    runs-on: ubuntu-latest
    needs: [quality-checks, tests]
    
    permissions:
      contents: read
      packages: write
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v3
    
    - name: Log in to Container Registry
      uses: docker/login-action@v3
      with:
        registry: ${{ env.REGISTRY }}
        username: ${{ github.actor }}
        password: ${{ secrets.GITHUB_TOKEN }}
    
    - name: Extract metadata
      id: meta
      uses: docker/metadata-action@v5
      with:
        images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
        tags: |
          type=ref,event=branch
          type=ref,event=pr
          type=sha,prefix={{branch}}-
          type=raw,value=latest,enable={{is_default_branch}}
    
    - name: Build and push Docker image
      uses: docker/build-push-action@v5
      with:
        context: .
        platforms: linux/amd64,linux/arm64
        push: true
        tags: ${{ steps.meta.outputs.tags }}
        labels: ${{ steps.meta.outputs.labels }}
        cache-from: type=gha
        cache-to: type=gha,mode=max
        build-args: |
          BUILD_DATE=${{ fromJSON(steps.meta.outputs.json).labels['org.opencontainers.image.created'] }}
          VERSION=${{ fromJSON(steps.meta.outputs.json).labels['org.opencontainers.image.version'] }}
    
    - name: Run container security scan
      uses: aquasec/trivy-action@master
      with:
        image-ref: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:${{ fromJSON(steps.meta.outputs.json).labels['org.opencontainers.image.version'] }}
        format: 'sarif'
        output: 'trivy-results.sarif'
    
    - name: Upload Trivy scan results
      uses: github/codeql-action/upload-sarif@v2
      if: always()
      with:
        sarif_file: 'trivy-results.sarif'

  # == ステージングデプロイ ==
  deploy-staging:
    name: Deploy to Staging
    runs-on: ubuntu-latest
    needs: [build-image]
    if: github.ref == 'refs/heads/develop'
    
    environment:
      name: staging
      url: https://staging.myapp.com
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Deploy to staging
      run: |
        echo "Deploying to staging environment..."
        # KubernetesデプロイまたはDocker Composeデプロイ
        # kubectl apply -f k8s/staging/
    
    - name: Run smoke tests
      run: |
        sleep 30  # デプロイ完了待機
        pytest tests/smoke/ --base-url=https://staging.myapp.com
    
    - name: Notify deployment
      if: always()
      uses: 8398a7/action-slack@v3
      with:
        status: ${{ job.status }}
        channel: '#deployments'
        webhook_url: ${{ secrets.SLACK_WEBHOOK }}

  # == プロダクションデプロイ ==
  deploy-production:
    name: Deploy to Production
    runs-on: ubuntu-latest
    needs: [build-image, deploy-staging]
    if: github.ref == 'refs/heads/main' && github.event_name == 'push'
    
    environment:
      name: production
      url: https://myapp.com
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Create deployment
      id: deployment
      uses: actions/github-script@v6
      with:
        script: |
          const deployment = await github.rest.repos.createDeployment({
            owner: context.repo.owner,
            repo: context.repo.repo,
            ref: context.sha,
            environment: 'production',
            description: 'Production deployment',
            auto_merge: false
          });
          return deployment.data.id;
    
    - name: Deploy to production
      run: |
        echo "Deploying to production environment..."
        # Blue-GreenデプロイまたはCanaryデプロイ
        # kubectl apply -f k8s/production/
    
    - name: Run production health checks
      run: |
        sleep 60  # デプロイ完了待機
        pytest tests/health/ --base-url=https://myapp.com
    
    - name: Update deployment status
      if: always()
      uses: actions/github-script@v6
      with:
        script: |
          await github.rest.repos.createDeploymentStatus({
            owner: context.repo.owner,
            repo: context.repo.repo,
            deployment_id: ${{ steps.deployment.outputs.result }},
            state: '${{ job.status }}' === 'success' ? 'success' : 'failure',
            environment_url: 'https://myapp.com',
            description: 'Production deployment ${{ job.status }}'
          });
    
    - name: Create release
      if: success()
      uses: actions/create-release@v1
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
      with:
        tag_name: v${{ github.run_number }}
        release_name: Release v${{ github.run_number }}
        body: |
          ✨ Production deployment successful!
          
          **Changes included:**
          ${{ github.event.head_commit.message }}
          
          **Docker Image:**
          `${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:${{ github.sha }}`
        draft: false
        prerelease: false
    
    - name: Notify deployment
      if: always()
      uses: 8398a7/action-slack@v3
      with:
        status: ${{ job.status }}
        channel: '#deployments'
        webhook_url: ${{ secrets.SLACK_WEBHOOK }}
        fields: repo,message,commit,author,action,eventName,ref,workflow
```

#### 9.2.2 プレデプロイメントフック設定

**pre-commit設定**
```yaml
# .pre-commit-config.yaml
repos:
  # Pythonコードフォーマット
  - repo: https://github.com/psf/black
    rev: 23.9.1
    hooks:
      - id: black
        language_version: python3.11
        args: [--line-length=88]
  
  # importソート
  - repo: https://github.com/pycqa/isort
    rev: 5.12.0
    hooks:
      - id: isort
        args: [--profile=black, --line-length=88]
  
  # リンター
  - repo: https://github.com/pycqa/flake8
    rev: 6.1.0
    hooks:
      - id: flake8
        args: [--max-line-length=88, --extend-ignore=E203,W503]
        additional_dependencies:
          - flake8-docstrings
          - flake8-bugbear
          - flake8-comprehensions
  
  # 型チェック
  - repo: https://github.com/pre-commit/mirrors-mypy
    rev: v1.6.1
    hooks:
      - id: mypy
        additional_dependencies: [types-all]
        args: [--config-file=pyproject.toml]
  
  # セキュリティスキャン
  - repo: https://github.com/pycqa/bandit
    rev: 1.7.5
    hooks:
      - id: bandit
        args: [-r, --severity-level=medium]
        exclude: ^tests/
  
  # 一般的なチェック
  - repo: https://github.com/pre-commit/pre-commit-hooks
    rev: v4.4.0
    hooks:
      - id: trailing-whitespace
      - id: end-of-file-fixer
      - id: check-yaml
      - id: check-json
      - id: check-toml
      - id: check-merge-conflict
      - id: check-added-large-files
        args: [--maxkb=1000]
      - id: debug-statements
      - id: check-docstring-first
  
  # Dockerfileリント
  - repo: https://github.com/hadolint/hadolint
    rev: v2.12.0
    hooks:
      - id: hadolint-docker
        args: [--ignore, DL3008, --ignore, DL3009]
  
  # YAMLフォーマット
  - repo: https://github.com/pre-commit/mirrors-prettier
    rev: v3.0.3
    hooks:
      - id: prettier
        types_or: [yaml, markdown]
        exclude: ^templates/
  
  # コミットメッセージチェック
  - repo: https://github.com/commitizen-tools/commitizen
    rev: v3.10.0
    hooks:
      - id: commitizen
        stages: [commit-msg]
```

### 9.3 インフラストラクチャ・クラウド

#### 9.3.1 Kubernetesデプロイメント

**Good: プロダクショングレードKubernetes設定**
```yaml
# k8s/production/namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: myapp-prod
  labels:
    name: myapp-prod
    environment: production
---
# k8s/production/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: myapp-config
  namespace: myapp-prod
data:
  ENVIRONMENT: "production"
  LOG_LEVEL: "INFO"
  WORKERS: "4"
  DATABASE_POOL_SIZE: "20"
  REDIS_POOL_SIZE: "10"
---
# k8s/production/secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: myapp-secrets
  namespace: myapp-prod
type: Opaque
data:
  DATABASE_URL: <base64-encoded-database-url>
  REDIS_URL: <base64-encoded-redis-url>
  SECRET_KEY: <base64-encoded-secret-key>
  JWT_SECRET: <base64-encoded-jwt-secret>
---
# k8s/production/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp-deployment
  namespace: myapp-prod
  labels:
    app: myapp
    version: v1.0.0
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: myapp
  template:
    metadata:
      labels:
        app: myapp
        version: v1.0.0
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8000"
        prometheus.io/path: "/metrics"
    spec:
      serviceAccountName: myapp-service-account
      securityContext:
        runAsNonRoot: true
        runAsUser: 1000
        fsGroup: 1000
      containers:
      - name: myapp
        image: ghcr.io/company/myapp:v1.0.0
        imagePullPolicy: Always
        
        ports:
        - containerPort: 8000
          name: http
          protocol: TCP
        
        env:
        - name: PORT
          value: "8000"
        
        envFrom:
        - configMapRef:
            name: myapp-config
        - secretRef:
            name: myapp-secrets
        
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        
        livenessProbe:
          httpGet:
            path: /health
            port: http
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        
        readinessProbe:
          httpGet:
            path: /health
            port: http
          initialDelaySeconds: 5
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 3
        
        startupProbe:
          httpGet:
            path: /health
            port: http
          initialDelaySeconds: 10
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 30
        
        securityContext:
          runAsNonRoot: true
          runAsUser: 1000
          allowPrivilegeEscalation: false
          readOnlyRootFilesystem: true
          capabilities:
            drop:
            - ALL
        
        volumeMounts:
        - name: tmp-volume
          mountPath: /tmp
        - name: logs-volume
          mountPath: /app/logs
      
      volumes:
      - name: tmp-volume
        emptyDir: {}
      - name: logs-volume
        emptyDir: {}
      
      affinity:
        podAntiAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
          - weight: 100
            podAffinityTerm:
              labelSelector:
                matchExpressions:
                - key: app
                  operator: In
                  values:
                  - myapp
              topologyKey: kubernetes.io/hostname
---
# k8s/production/service.yaml
apiVersion: v1
kind: Service
metadata:
  name: myapp-service
  namespace: myapp-prod
  labels:
    app: myapp
spec:
  selector:
    app: myapp
  ports:
  - name: http
    port: 80
    targetPort: http
    protocol: TCP
  type: ClusterIP
---
# k8s/production/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: myapp-ingress
  namespace: myapp-prod
  annotations:
    kubernetes.io/ingress.class: nginx
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/force-ssl-redirect: "true"
    cert-manager.io/cluster-issuer: letsencrypt-prod
    nginx.ingress.kubernetes.io/rate-limit: "100"
    nginx.ingress.kubernetes.io/rate-limit-window: "1m"
spec:
  tls:
  - hosts:
    - myapp.com
    - api.myapp.com
    secretName: myapp-tls
  rules:
  - host: myapp.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: myapp-service
            port:
              number: 80
  - host: api.myapp.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: myapp-service
            port:
              number: 80
---
# k8s/production/hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: myapp-hpa
  namespace: myapp-prod
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: myapp-deployment
  minReplicas: 3
  maxReplicas: 20
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
  behavior:
    scaleUp:
      stabilizationWindowSeconds: 60
      policies:
      - type: Percent
        value: 100
        periodSeconds: 15
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Percent
        value: 10
        periodSeconds: 60
---
# k8s/production/pdb.yaml
apiVersion: policy/v1
kind: PodDisruptionBudget
metadata:
  name: myapp-pdb
  namespace: myapp-prod
spec:
  minAvailable: 2
  selector:
    matchLabels:
      app: myapp
---
# k8s/production/networkpolicy.yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: myapp-network-policy
  namespace: myapp-prod
spec:
  podSelector:
    matchLabels:
      app: myapp
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          name: nginx-ingress
    ports:
    - protocol: TCP
      port: 8000
  egress:
  - to:
    - namespaceSelector:
        matchLabels:
          name: database
    ports:
    - protocol: TCP
      port: 5432
  - to:
    - namespaceSelector:
        matchLabels:
          name: redis
    ports:
    - protocol: TCP
      port: 6379
  - to: []  # Allow all egress for external APIs
    ports:
    - protocol: TCP
      port: 443
    - protocol: TCP
      port: 80
```

### 9.4 環境管理・構成

#### 9.4.1 環境別設定管理

**Good: 包括的環境設定管理**
```python
# config/settings.py
"""
環境別設定管理システム
"""

import os
import sys
from typing import Dict, Any, Optional, List
from pathlib import Path
from dataclasses import dataclass, field
from enum import Enum
import json
from pydantic import BaseSettings, Field, validator
from pydantic.env_settings import SettingsSourceCallable

class Environment(str, Enum):
    """環境種別"""
    DEVELOPMENT = "development"
    TEST = "test"
    STAGING = "staging"
    PRODUCTION = "production"

class LogLevel(str, Enum):
    """ログレベル"""
    DEBUG = "DEBUG"
    INFO = "INFO"
    WARNING = "WARNING"
    ERROR = "ERROR"
    CRITICAL = "CRITICAL"

@dataclass
class DatabaseConfig:
    """データベース設定"""
    url: str
    pool_size: int = 20
    max_overflow: int = 30
    pool_timeout: int = 30
    pool_recycle: int = 3600
    echo: bool = False
    echo_pool: bool = False
    
    @classmethod
    def from_url(cls, url: str, **kwargs) -> 'DatabaseConfig':
        return cls(url=url, **kwargs)

@dataclass 
class RedisConfig:
    """レRedis設定"""
    url: str
    max_connections: int = 100
    socket_timeout: int = 5
    socket_connect_timeout: int = 5
    retry_on_timeout: bool = True
    health_check_interval: int = 30

@dataclass
class SecurityConfig:
    """セキュリティ設定"""
    secret_key: str
    jwt_secret: str
    jwt_algorithm: str = "HS256"
    jwt_access_token_expire_minutes: int = 30
    jwt_refresh_token_expire_days: int = 7
    password_min_length: int = 8
    bcrypt_rounds: int = 12
    allowed_hosts: List[str] = field(default_factory=list)
    cors_origins: List[str] = field(default_factory=list)

class Settings(BaseSettings):
    """アプリケーション設定"""
    
    # == 基本設定 ==
    app_name: str = "MyApp"
    app_version: str = "1.0.0"
    environment: Environment = Environment.DEVELOPMENT
    debug: bool = False
    
    # == サーバー設定 ==
    host: str = "0.0.0.0"
    port: int = 8000
    workers: int = Field(default=1, ge=1, le=32)
    worker_class: str = "uvicorn.workers.UvicornWorker"
    worker_timeout: int = 30
    keepalive: int = 2
    max_requests: int = 1000
    max_requests_jitter: int = 100
    
    # == ログ設定 ==
    log_level: LogLevel = LogLevel.INFO
    log_format: str = "json"
    log_dir: str = "logs"
    log_file_max_size: str = "100MB"
    log_file_backup_count: int = 5
    
    # == データベース設定 ==
    database_url: str = Field(..., env="DATABASE_URL")
    database_pool_size: int = Field(default=20, ge=1, le=100)
    database_max_overflow: int = Field(default=30, ge=0, le=50)
    database_pool_timeout: int = Field(default=30, ge=1, le=300)
    database_echo: bool = False
    
    # == Redis設定 ==
    redis_url: str = Field(..., env="REDIS_URL")
    redis_max_connections: int = Field(default=100, ge=1, le=1000)
    redis_socket_timeout: int = Field(default=5, ge=1, le=60)
    
    # == セキュリティ設定 ==
    secret_key: str = Field(..., env="SECRET_KEY", min_length=32)
    jwt_secret: str = Field(..., env="JWT_SECRET", min_length=32)
    jwt_algorithm: str = "HS256"
    jwt_access_token_expire_minutes: int = Field(default=30, ge=1, le=10080)
    jwt_refresh_token_expire_days: int = Field(default=7, ge=1, le=30)
    
    # == CORS設定 ==
    allowed_hosts: List[str] = Field(default_factory=list)
    cors_origins: List[str] = Field(default_factory=list)
    cors_methods: List[str] = Field(default=["GET", "POST", "PUT", "DELETE", "OPTIONS"])
    cors_headers: List[str] = Field(default=["*"])
    
    # == 外部サービス設定 ==
    email_smtp_host: Optional[str] = None
    email_smtp_port: int = 587
    email_smtp_username: Optional[str] = None
    email_smtp_password: Optional[str] = None
    email_from_address: Optional[str] = None
    
    # == 監視・メトリクス ==
    metrics_enabled: bool = True
    metrics_endpoint: str = "/metrics"
    health_check_endpoint: str = "/health"
    sentry_dsn: Optional[str] = None
    
    # == パフォーマンス設定 ==
    request_timeout: int = Field(default=30, ge=1, le=300)
    slow_query_threshold: float = Field(default=1.0, ge=0.1, le=10.0)
    cache_ttl: int = Field(default=300, ge=1, le=86400)
    
    # == ファイルアップロード設定 ==
    upload_max_size: int = Field(default=10*1024*1024, ge=1024)  # 10MB
    upload_allowed_extensions: List[str] = Field(
        default=[".jpg", ".jpeg", ".png", ".gif", ".pdf", ".txt", ".csv"]
    )
    upload_directory: str = "uploads"
    
    @validator("environment", pre=True)
    def validate_environment(cls, v):
        if isinstance(v, str):
            return Environment(v.lower())
        return v
    
    @validator("workers")
    def validate_workers(cls, v, values):
        if values.get("environment") == Environment.PRODUCTION:
            # プロダクションでは最低2ワーカー
            return max(v, 2)
        return v
    
    @validator("debug")
    def validate_debug(cls, v, values):
        env = values.get("environment")
        if env == Environment.PRODUCTION:
            # プロダクションでは強制的にFalse
            return False
        return v
    
    @validator("log_level")
    def validate_log_level(cls, v, values):
        env = values.get("environment")
        if env == Environment.PRODUCTION and v == LogLevel.DEBUG:
            # プロダクションではDEBUG禁止
            return LogLevel.INFO
        return v
    
    @validator("cors_origins")
    def validate_cors_origins(cls, v, values):
        env = values.get("environment")
        if env == Environment.PRODUCTION and "*" in v:
            raise ValueError("CORS wildcard (*) not allowed in production")
        return v
    
    @property
    def database_config(self) -> DatabaseConfig:
        """データベース設定取得"""
        return DatabaseConfig(
            url=self.database_url,
            pool_size=self.database_pool_size,
            max_overflow=self.database_max_overflow,
            pool_timeout=self.database_pool_timeout,
            echo=self.database_echo and self.environment != Environment.PRODUCTION
        )
    
    @property
    def redis_config(self) -> RedisConfig:
        """ルRedis設定取得"""
        return RedisConfig(
            url=self.redis_url,
            max_connections=self.redis_max_connections,
            socket_timeout=self.redis_socket_timeout
        )
    
    @property
    def security_config(self) -> SecurityConfig:
        """セキュリティ設定取得"""
        return SecurityConfig(
            secret_key=self.secret_key,
            jwt_secret=self.jwt_secret,
            jwt_algorithm=self.jwt_algorithm,
            jwt_access_token_expire_minutes=self.jwt_access_token_expire_minutes,
            jwt_refresh_token_expire_days=self.jwt_refresh_token_expire_days,
            allowed_hosts=self.allowed_hosts,
            cors_origins=self.cors_origins
        )
    
    @property
    def is_development(self) -> bool:
        return self.environment == Environment.DEVELOPMENT
    
    @property
    def is_production(self) -> bool:
        return self.environment == Environment.PRODUCTION
    
    @property
    def is_testing(self) -> bool:
        return self.environment == Environment.TEST
    
    def get_database_url(self, async_driver: bool = False) -> str:
        """データベースURL取得"""
        url = self.database_url
        if async_driver and url.startswith("postgresql://"):
            return url.replace("postgresql://", "postgresql+asyncpg://", 1)
        return url
    
    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"
        case_sensitive = False
        
        @classmethod
        def customise_sources(
            cls,
            init_settings: SettingsSourceCallable,
            env_settings: SettingsSourceCallable,
            file_secret_settings: SettingsSourceCallable,
        ) -> tuple[SettingsSourceCallable, ...]:
            # 設定優先度: 環境変数 > .envファイル > デフォルト値
            return env_settings, file_secret_settings, init_settings

# 環境別設定ファクトリ
class SettingsFactory:
    """設定ファクトリ"""
    
    _instance: Optional[Settings] = None
    
    @classmethod
    def get_settings(cls, force_reload: bool = False) -> Settings:
        """設定インスタンス取得（シングルトン）"""
        if cls._instance is None or force_reload:
            cls._instance = cls._create_settings()
        return cls._instance
    
    @classmethod
    def _create_settings(cls) -> Settings:
        """設定インスタンス作成"""
        # 環境別設定ファイル読み込み
        env = os.getenv("ENVIRONMENT", "development").lower()
        env_file = f".env.{env}"
        
        # ファイルが存在する場合のみ読み込み
        env_file_path = Path(env_file)
        if env_file_path.exists():
            settings = Settings(_env_file=env_file)
        else:
            settings = Settings()
        
        # 設定検証
        cls._validate_settings(settings)
        
        return settings
    
    @classmethod
    def _validate_settings(cls, settings: Settings):
        """設定検証"""
        # プロダクション環境の追加検証
        if settings.is_production:
            required_prod_settings = [
                "secret_key", "jwt_secret", "database_url", "redis_url"
            ]
            
            for setting in required_prod_settings:
                value = getattr(settings, setting, None)
                if not value or (isinstance(value, str) and len(value) < 10):
                    raise ValueError(f"Production environment requires valid {setting}")
            
            # プロダクションではデバッグ無効
            if settings.debug:
                raise ValueError("Debug mode not allowed in production")
            
            # HTTPS強制
            if not any(host.startswith("https://") for host in settings.allowed_hosts):
                print("Warning: No HTTPS hosts configured for production")

# グローバル設定インスタンス
settings = SettingsFactory.get_settings()

# デバッグ用設定ダンプ
if settings.is_development:
    def dump_settings():
        """設定ダンプ（機密情報除く）"""
        safe_settings = {}
        for field_name, field_info in settings.__fields__.items():
            value = getattr(settings, field_name)
            
            # 機密情報マスク
            if any(secret in field_name.lower() for secret in ['secret', 'password', 'key', 'token']):
                safe_settings[field_name] = "***MASKED***"
            elif isinstance(value, str) and len(value) > 100:
                safe_settings[field_name] = value[:50] + "..."
            else:
                safe_settings[field_name] = value
        
        return safe_settings
    
    # 開発環境での設定表示
    import json
    print("=== Application Settings ===")
    print(json.dumps(dump_settings(), indent=2, default=str))
    print("=============================")
```

---

## 10. AI/ML専用章

### 10.1 NumPy・科学計算最適化

#### 10.1.1 高性能NumPy実装

**Good: NumPyベストプラクティス**
```python
# 高性能NumPy実装
import numpy as np
import numpy.typing as npt
from typing import Union, Tuple, Optional, List, Any
from dataclasses import dataclass
from functools import wraps
import time
import warnings
from contextlib import contextmanager
import threading
from concurrent.futures import ThreadPoolExecutor
import numba
from numba import jit, njit, prange

# 型エイリアス定義
ArrayLike = Union[np.ndarray, List, Tuple]
FloatArray = npt.NDArray[np.floating]
IntArray = npt.NDArray[np.integer]
ComplexArray = npt.NDArray[np.complexfloating]

@dataclass
class ArrayMetrics:
    """配列メトリクス"""
    shape: Tuple[int, ...]
    dtype: np.dtype
    size: int
    memory_usage: int  # bytes
    is_contiguous: bool
    is_fortran_order: bool
    
    @classmethod
    def from_array(cls, arr: np.ndarray) -> 'ArrayMetrics':
        return cls(
            shape=arr.shape,
            dtype=arr.dtype,
            size=arr.size,
            memory_usage=arr.nbytes,
            is_contiguous=arr.flags.c_contiguous,
            is_fortran_order=arr.flags.f_contiguous
        )

class OptimizedNumPy:
    """最適化されたNumPy操作クラス"""
    
    def __init__(self, use_threading: bool = True, thread_count: Optional[int] = None):
        self.use_threading = use_threading
        self.thread_count = thread_count or min(8, (os.cpu_count() or 1))
        
        # NumPy設定最適化
        self._configure_numpy()
    
    def _configure_numpy(self):
        """デNumPy設定最適化"""
        # スレッド数設定
        os.environ['OMP_NUM_THREADS'] = str(self.thread_count)
        os.environ['MKL_NUM_THREADS'] = str(self.thread_count)
        os.environ['OPENBLAS_NUM_THREADS'] = str(self.thread_count)
        
        # エラー設定
        np.seterr(all='warn', over='raise', invalid='raise')
        
        # 警告フィルター
        warnings.filterwarnings('ignore', category=np.ComplexWarning)
    
    @staticmethod
    def create_optimized_array(
        data: ArrayLike,
        dtype: Optional[np.dtype] = None,
        order: str = 'C',
        copy: bool = False
    ) -> np.ndarray:
        """最適化された配列作成"""
        
        # データタイプ最適化
        if dtype is None:
            if isinstance(data, (list, tuple)):
                # サンプルデータから最適タイプ推定
                sample = np.array(data[:100] if len(data) > 100 else data)
                if np.issubdtype(sample.dtype, np.integer):
                    # 整数の場合、範囲に応じて最小タイプ選択
                    min_val, max_val = sample.min(), sample.max()
                    if -128 <= min_val and max_val <= 127:
                        dtype = np.int8
                    elif -32768 <= min_val and max_val <= 32767:
                        dtype = np.int16
                    elif -2147483648 <= min_val and max_val <= 2147483647:
                        dtype = np.int32
                    else:
                        dtype = np.int64
                elif np.issubdtype(sample.dtype, np.floating):
                    # 浮動小数点の精度判定
                    dtype = np.float32 if sample.max() < 1e7 else np.float64
        
        # 配列作成
        arr = np.asarray(data, dtype=dtype, order=order)
        
        if copy:
            arr = arr.copy(order=order)
        
        return arr
    
    @staticmethod
    def ensure_contiguous(arr: np.ndarray, order: str = 'C') -> np.ndarray:
        """連続メモリ配置保証"""
        if order == 'C' and not arr.flags.c_contiguous:
            return np.ascontiguousarray(arr)
        elif order == 'F' and not arr.flags.f_contiguous:
            return np.asfortranarray(arr)
        return arr
    
    @staticmethod
    @njit(parallel=True, cache=True)
    def fast_matrix_multiply(a: np.ndarray, b: np.ndarray) -> np.ndarray:
        """高速行列乗算（Numba最適化）"""
        m, k = a.shape
        k2, n = b.shape
        
        if k != k2:
            raise ValueError("Matrix dimensions don't match")
        
        result = np.zeros((m, n), dtype=a.dtype)
        
        for i in prange(m):
            for j in range(n):
                for l in range(k):
                    result[i, j] += a[i, l] * b[l, j]
        
        return result
    
    @staticmethod
    @njit(parallel=True, cache=True)
    def fast_element_wise_ops(
        a: np.ndarray,
        b: np.ndarray,
        operation: str = 'add'
    ) -> np.ndarray:
        """高速要素毎操作"""
        if a.shape != b.shape:
            raise ValueError("Array shapes don't match")
        
        result = np.empty_like(a)
        flat_a = a.flat
        flat_b = b.flat
        flat_result = result.flat
        
        if operation == 'add':
            for i in prange(a.size):
                flat_result[i] = flat_a[i] + flat_b[i]
        elif operation == 'multiply':
            for i in prange(a.size):
                flat_result[i] = flat_a[i] * flat_b[i]
        elif operation == 'subtract':
            for i in prange(a.size):
                flat_result[i] = flat_a[i] - flat_b[i]
        elif operation == 'divide':
            for i in prange(a.size):
                flat_result[i] = flat_a[i] / flat_b[i] if flat_b[i] != 0 else 0
        
        return result
    
    def batch_processing(
        self,
        arrays: List[np.ndarray],
        func: callable,
        batch_size: Optional[int] = None,
        **kwargs
    ) -> List[np.ndarray]:
        """バッチ処理実行"""
        if not self.use_threading or len(arrays) < 2:
            return [func(arr, **kwargs) for arr in arrays]
        
        batch_size = batch_size or max(1, len(arrays) // self.thread_count)
        results = [None] * len(arrays)
        
        def process_batch(start_idx: int, end_idx: int):
            for i in range(start_idx, end_idx):
                results[i] = func(arrays[i], **kwargs)
        
        with ThreadPoolExecutor(max_workers=self.thread_count) as executor:
            futures = []
            for i in range(0, len(arrays), batch_size):
                end_idx = min(i + batch_size, len(arrays))
                future = executor.submit(process_batch, i, end_idx)
                futures.append(future)
            
            # 完了待機
            for future in futures:
                future.result()
        
        return results
    
    @staticmethod
    def memory_efficient_operation(
        arrays: List[np.ndarray],
        operation: callable,
        chunk_size: Optional[int] = None
    ) -> np.ndarray:
        """メモリ効率的な大規模操作"""
        if not arrays:
            raise ValueError("No arrays provided")
        
        first_array = arrays[0]
        total_size = first_array.size
        
        # チャンクサイズ自動計算
        if chunk_size is None:
            available_memory = psutil.virtual_memory().available
            array_memory = first_array.nbytes * len(arrays)
            chunk_size = min(
                total_size,
                max(1000, int(available_memory * 0.1 / (array_memory / total_size)))
            )
        
        result = np.empty_like(first_array)
        
        for start in range(0, total_size, chunk_size):
            end = min(start + chunk_size, total_size)
            
            # チャンク抽出
            chunk_arrays = []
            for arr in arrays:
                flat_arr = arr.flat
                chunk = np.array([flat_arr[i] for i in range(start, end)])
                chunk_arrays.append(chunk.reshape(-1))
            
            # 操作実行
            chunk_result = operation(chunk_arrays)
            
            # 結果マージ
            result.flat[start:end] = chunk_result.flat
        
        return result
    
    @staticmethod
    def profile_array_operation(func: callable):
        """配列操作プロファイルデコレーター"""
        @wraps(func)
        def wrapper(*args, **kwargs):
            start_time = time.perf_counter()
            start_memory = psutil.Process().memory_info().rss
            
            # 入力配列情報
            input_arrays = [arg for arg in args if isinstance(arg, np.ndarray)]
            input_metrics = [ArrayMetrics.from_array(arr) for arr in input_arrays]
            
            try:
                result = func(*args, **kwargs)
                
                end_time = time.perf_counter()
                end_memory = psutil.Process().memory_info().rss
                
                # 結果情報
                result_metrics = None
                if isinstance(result, np.ndarray):
                    result_metrics = ArrayMetrics.from_array(result)
                
                # プロファイル情報出力
                execution_time = end_time - start_time
                memory_delta = end_memory - start_memory
                
                if execution_time > 0.1:  # 100ms以上の場合のみログ
                    logger.info(
                        f"Array operation profile: {func.__name__}",
                        execution_time=execution_time,
                        memory_delta_mb=memory_delta / 1024 / 1024,
                        input_arrays=len(input_arrays),
                        total_input_size=sum(m.size for m in input_metrics),
                        result_shape=result_metrics.shape if result_metrics else None
                    )
                
                return result
                
            except Exception as e:
                logger.error(
                    f"Array operation failed: {func.__name__}",
                    error=str(e),
                    input_shapes=[m.shape for m in input_metrics]
                )
                raise
        
        return wrapper

# グローバルインスタンス
np_optimizer = OptimizedNumPy()

# 使用例とベンチマーク
class NumpyBenchmark:
    """デNumPyベンチマーククラス"""
    
    @staticmethod
    def benchmark_matrix_operations(size: int = 1000, iterations: int = 10):
        """行列操作ベンチマーク"""
        
        # テストデータ作成
        a = np_optimizer.create_optimized_array(
            np.random.randn(size, size), dtype=np.float32
        )
        b = np_optimizer.create_optimized_array(
            np.random.randn(size, size), dtype=np.float32
        )
        
        results = {}
        
        # 標準NumPy乗算
        times = []
        for _ in range(iterations):
            start = time.perf_counter()
            result_numpy = np.dot(a, b)
            times.append(time.perf_counter() - start)
        results['numpy_dot'] = {
            'mean_time': np.mean(times),
            'std_time': np.std(times),
            'result_shape': result_numpy.shape
        }
        
        # Numba最適化乗算
        times = []
        for _ in range(iterations):
            start = time.perf_counter()
            result_numba = np_optimizer.fast_matrix_multiply(a, b)
            times.append(time.perf_counter() - start)
        results['numba_multiply'] = {
            'mean_time': np.mean(times),
            'std_time': np.std(times),
            'result_shape': result_numba.shape
        }
        
        # 結果比較
        results['results_match'] = np.allclose(result_numpy, result_numba, rtol=1e-5)
        
        return results
    
    @staticmethod
    def benchmark_element_wise_operations(size: int = 1000000, iterations: int = 10):
        """要素毎操作ベンチマーク"""
        
        # テストデータ
        a = np_optimizer.create_optimized_array(
            np.random.randn(size), dtype=np.float32
        )
        b = np_optimizer.create_optimized_array(
            np.random.randn(size), dtype=np.float32
        )
        
        operations = ['add', 'multiply', 'subtract', 'divide']
        results = {}
        
        for op in operations:
            # NumPy標準
            numpy_times = []
            for _ in range(iterations):
                start = time.perf_counter()
                if op == 'add':
                    result_numpy = a + b
                elif op == 'multiply':
                    result_numpy = a * b
                elif op == 'subtract':
                    result_numpy = a - b
                elif op == 'divide':
                    result_numpy = a / b
                numpy_times.append(time.perf_counter() - start)
            
            # Numba最適化
            numba_times = []
            for _ in range(iterations):
                start = time.perf_counter()
                result_numba = np_optimizer.fast_element_wise_ops(a, b, op)
                numba_times.append(time.perf_counter() - start)
            
            results[op] = {
                'numpy_mean': np.mean(numpy_times),
                'numba_mean': np.mean(numba_times),
                'speedup': np.mean(numpy_times) / np.mean(numba_times),
                'results_match': np.allclose(result_numpy, result_numba, rtol=1e-5)
            }
        
        return results

# 特殊用途関数
class SpecializedArrayOps:
    """特化した配列操作"""
    
    @staticmethod
    @njit(parallel=True, cache=True)
    def sliding_window_operation(
        arr: np.ndarray,
        window_size: int,
        operation: str = 'mean'
    ) -> np.ndarray:
        """スライディングウィンドウ操作"""
        if len(arr) < window_size:
            raise ValueError("Array length must be >= window_size")
        
        result_size = len(arr) - window_size + 1
        result = np.empty(result_size, dtype=arr.dtype)
        
        for i in prange(result_size):
            window = arr[i:i + window_size]
            
            if operation == 'mean':
                result[i] = np.mean(window)
            elif operation == 'sum':
                result[i] = np.sum(window)
            elif operation == 'max':
                result[i] = np.max(window) 
            elif operation == 'min':
                result[i] = np.min(window)
            elif operation == 'std':
                result[i] = np.std(window)
        
        return result
    
    @staticmethod
    @njit(parallel=True, cache=True)
    def find_peaks_1d(arr: np.ndarray, threshold: float = 0.0) -> np.ndarray:
        """ピーク検出（1D）"""
        peaks = []
        
        for i in range(1, len(arr) - 1):
            if (arr[i] > arr[i-1] and 
                arr[i] > arr[i+1] and 
                arr[i] > threshold):
                peaks.append(i)
        
        return np.array(peaks)
    
    @staticmethod
    def correlation_matrix(data: np.ndarray, method: str = 'pearson') -> np.ndarray:
        """相関行列計算"""
        if data.ndim != 2:
            raise ValueError("Input must be 2D array")
        
        n_features = data.shape[1]
        corr_matrix = np.eye(n_features)
        
        if method == 'pearson':
            # ピアソン相関係数
            for i in range(n_features):
                for j in range(i+1, n_features):
                    corr = np.corrcoef(data[:, i], data[:, j])[0, 1]
                    corr_matrix[i, j] = corr
                    corr_matrix[j, i] = corr
        
        elif method == 'spearman':
            # スピアマン相関係数
            from scipy.stats import spearmanr
            for i in range(n_features):
                for j in range(i+1, n_features):
                    corr, _ = spearmanr(data[:, i], data[:, j])
                    corr_matrix[i, j] = corr
                    corr_matrix[j, i] = corr
        
        return corr_matrix

# 使用例
if __name__ == "__main__":
    # ベンチマーク実行
    print("行列操作ベンチマーク:")
    matrix_results = NumpyBenchmark.benchmark_matrix_operations(500, 5)
    for key, value in matrix_results.items():
        print(f"  {key}: {value}")
    
    print("\n要素毎操作ベンチマーク:")
    element_results = NumpyBenchmark.benchmark_element_wise_operations(100000, 5)
    for op, metrics in element_results.items():
        print(f"  {op}: {metrics['speedup']:.2f}x 高速化")
```

### 10.2 Pandas・データ処理パターン

#### 10.2.1 高性能Pandas実装

**Good: 最適化Pandas操作**
```python
# 高性能Pandasデータ処理
import pandas as pd
import numpy as np
from typing import Dict, List, Union, Optional, Any, Callable
from dataclasses import dataclass
import warnings
from contextlib import contextmanager
import time
from functools import wraps
import multiprocessing as mp
from concurrent.futures import ProcessPoolExecutor, as_completed
import dask.dataframe as dd
from pandas.api.types import is_numeric_dtype, is_categorical_dtype

class OptimizedPandas:
    """最適化Pandas操作クラス"""
    
    def __init__(self):
        self._configure_pandas()
    
    def _configure_pandas(self):
        """デPandas設定最適化"""
        # 表示設定
        pd.set_option('display.max_columns', 20)
        pd.set_option('display.max_rows', 100)
        pd.set_option('display.width', 1000)
        
        # パフォーマンス設定
        pd.set_option('mode.chained_assignment', 'warn')
        pd.set_option('compute.use_bottleneck', True)
        pd.set_option('compute.use_numexpr', True)
        
        # 警告フィルター
        warnings.filterwarnings('ignore', category=pd.errors.PerformanceWarning)
    
    @staticmethod
    def optimize_dtypes(df: pd.DataFrame, aggressive: bool = False) -> pd.DataFrame:
        """データタイプ最適化"""
        optimized_df = df.copy()
        
        for col in optimized_df.columns:
            col_type = optimized_df[col].dtype
            
            # 数値データの最適化
            if is_numeric_dtype(col_type):
                if col_type == 'int64':
                    c_min = optimized_df[col].min()
                    c_max = optimized_df[col].max()
                    
                    if c_min > np.iinfo(np.int8).min and c_max < np.iinfo(np.int8).max:
                        optimized_df[col] = optimized_df[col].astype(np.int8)
                    elif c_min > np.iinfo(np.int16).min and c_max < np.iinfo(np.int16).max:
                        optimized_df[col] = optimized_df[col].astype(np.int16)
                    elif c_min > np.iinfo(np.int32).min and c_max < np.iinfo(np.int32).max:
                        optimized_df[col] = optimized_df[col].astype(np.int32)
                
                elif col_type == 'float64':
                    c_min = optimized_df[col].min()
                    c_max = optimized_df[col].max()
                    
                    if c_min > np.finfo(np.float32).min and c_max < np.finfo(np.float32).max:
                        optimized_df[col] = optimized_df[col].astype(np.float32)
            
            # 文字列データの最適化
            elif col_type == 'object':
                if aggressive:
                    # カテゴリカルデータに変換を検討
                    unique_count = optimized_df[col].nunique()
                    total_count = len(optimized_df[col])
                    
                    if unique_count / total_count < 0.5:  # 50%未満の場合
                        optimized_df[col] = optimized_df[col].astype('category')
        
        return optimized_df
    
    @staticmethod
    def efficient_groupby(
        df: pd.DataFrame,
        groupby_cols: Union[str, List[str]],
        agg_funcs: Dict[str, Union[str, List[str], Dict]],
        use_categorical: bool = True
    ) -> pd.DataFrame:
        """効率的なGroupBy操作"""
        
        # グループキーをカテゴリカルに変換
        if use_categorical:
            if isinstance(groupby_cols, str):
                groupby_cols = [groupby_cols]
            
            for col in groupby_cols:
                if not is_categorical_dtype(df[col]):
                    df[col] = df[col].astype('category')
        
        # GroupBy実行
        grouped = df.groupby(groupby_cols, observed=True)
        result = grouped.agg(agg_funcs)
        
        # マルチインデックスのフラット化
        if isinstance(result.columns, pd.MultiIndex):
            result.columns = ['_'.join(col).strip() for col in result.columns.values]
        
        return result.reset_index()
    
    @staticmethod
    def memory_efficient_merge(
        left: pd.DataFrame,
        right: pd.DataFrame,
        on: Union[str, List[str]],
        how: str = 'inner',
        chunk_size: Optional[int] = None
    ) -> pd.DataFrame:
        """メモリ効率的なマージ"""
        
        # チャンクサイズ自動計算
        if chunk_size is None:
            available_memory = psutil.virtual_memory().available
            estimated_memory = (left.memory_usage(deep=True).sum() + 
                              right.memory_usage(deep=True).sum()) * 2
            
            if estimated_memory > available_memory * 0.5:
                chunk_size = max(1000, len(left) // 10)
            else:
                return pd.merge(left, right, on=on, how=how)
        
        # チャンク別マージ
        results = []
        for start in range(0, len(left), chunk_size):
            end = min(start + chunk_size, len(left))
            left_chunk = left.iloc[start:end]
            
            merged_chunk = pd.merge(left_chunk, right, on=on, how=how)
            results.append(merged_chunk)
        
        return pd.concat(results, ignore_index=True)
    
    @staticmethod
    def parallel_apply(
        df: pd.DataFrame,
        func: Callable,
        axis: int = 0,
        n_jobs: Optional[int] = None,
        chunk_size: Optional[int] = None
    ) -> pd.DataFrame:
        """並列apply操作"""
        
        if n_jobs is None:
            n_jobs = min(mp.cpu_count(), 4)
        
        if chunk_size is None:
            chunk_size = max(100, len(df) // n_jobs)
        
        # データをチャンクに分割
        chunks = []
        for start in range(0, len(df), chunk_size):
            end = min(start + chunk_size, len(df))
            chunks.append(df.iloc[start:end])
        
        # 並列処理
        with ProcessPoolExecutor(max_workers=n_jobs) as executor:
            futures = [executor.submit(lambda chunk: chunk.apply(func, axis=axis), chunk) 
                      for chunk in chunks]
            
            results = []
            for future in as_completed(futures):
                results.append(future.result())
        
        return pd.concat(results, ignore_index=True)
    
    @staticmethod
    def optimize_string_operations(df: pd.DataFrame) -> pd.DataFrame:
        """文字列操作最適化"""
        optimized_df = df.copy()
        
        for col in optimized_df.select_dtypes(include=['object']).columns:
            # 文字列の場合のstring dtype使用
            try:
                # サンプルチェック
                sample = optimized_df[col].dropna().head(1000)
                if sample.apply(lambda x: isinstance(x, str)).all():
                    optimized_df[col] = optimized_df[col].astype('string')
            except Exception:
                continue
        
        return optimized_df

class DataQualityChecker:
    """データ品質チェッカー"""
    
    @staticmethod
    def comprehensive_data_profile(df: pd.DataFrame) -> Dict[str, Any]:
        """包括的データプロファイル"""
        profile = {
            'basic_info': {
                'shape': df.shape,
                'memory_usage_mb': df.memory_usage(deep=True).sum() / 1024 / 1024,
                'dtypes': df.dtypes.to_dict()
            },
            'missing_data': {},
            'duplicates': {
                'duplicate_rows': df.duplicated().sum(),
                'duplicate_percentage': df.duplicated().mean() * 100
            },
            'numeric_stats': {},
            'categorical_stats': {},
            'data_quality_issues': []
        }
        
        # 欠損データ分析
        for col in df.columns:
            missing_count = df[col].isnull().sum()
            missing_pct = (missing_count / len(df)) * 100
            
            profile['missing_data'][col] = {
                'count': missing_count,
                'percentage': missing_pct
            }
            
            if missing_pct > 50:
                profile['data_quality_issues'].append(
                    f"Column '{col}' has {missing_pct:.1f}% missing values"
                )
        
        # 数値データ統計
        numeric_cols = df.select_dtypes(include=[np.number]).columns
        for col in numeric_cols:
            stats = df[col].describe()
            profile['numeric_stats'][col] = {
                'mean': stats['mean'],
                'std': stats['std'],
                'min': stats['min'],
                'max': stats['max'],
                'zeros_count': (df[col] == 0).sum(),
                'outliers_iqr': DataQualityChecker._count_outliers_iqr(df[col])
            }
            
            # 異常値チェック
            if np.isinf(df[col]).any():
                profile['data_quality_issues'].append(
                    f"Column '{col}' contains infinite values"
                )
        
        # カテゴリカルデータ統計
        categorical_cols = df.select_dtypes(include=['object', 'category']).columns
        for col in categorical_cols:
            unique_count = df[col].nunique()
            value_counts = df[col].value_counts()
            
            profile['categorical_stats'][col] = {
                'unique_count': unique_count,
                'most_frequent': value_counts.index[0] if len(value_counts) > 0 else None,
                'most_frequent_count': value_counts.iloc[0] if len(value_counts) > 0 else 0,
                'cardinality_ratio': unique_count / len(df)
            }
            
            # 高カーディナリティチェック
            if unique_count / len(df) > 0.95:
                profile['data_quality_issues'].append(
                    f"Column '{col}' has very high cardinality ({unique_count} unique values)"
                )
        
        return profile
    
    @staticmethod
    def _count_outliers_iqr(series: pd.Series) -> int:
        """サIQR法による外れ値カウント"""
        Q1 = series.quantile(0.25)
        Q3 = series.quantile(0.75)
        IQR = Q3 - Q1
        lower_bound = Q1 - 1.5 * IQR
        upper_bound = Q3 + 1.5 * IQR
        
        return ((series < lower_bound) | (series > upper_bound)).sum()
    
    @staticmethod
    def detect_data_drift(
        reference_df: pd.DataFrame,
        current_df: pd.DataFrame,
        threshold: float = 0.1
    ) -> Dict[str, Any]:
        """データドリフト検出"""
        drift_report = {
            'columns_analyzed': [],
            'drift_detected': [],
            'drift_scores': {},
            'recommendations': []
        }
        
        common_cols = set(reference_df.columns) & set(current_df.columns)
        
        for col in common_cols:
            drift_report['columns_analyzed'].append(col)
            
            if is_numeric_dtype(reference_df[col]):
                # 数値データのドリフト
                ref_stats = reference_df[col].describe()
                curr_stats = current_df[col].describe()
                
                # 平均と標準偏差の変化率
                mean_change = abs(curr_stats['mean'] - ref_stats['mean']) / ref_stats['mean']
                std_change = abs(curr_stats['std'] - ref_stats['std']) / ref_stats['std']
                
                drift_score = max(mean_change, std_change)
                drift_report['drift_scores'][col] = drift_score
                
                if drift_score > threshold:
                    drift_report['drift_detected'].append(col)
                    drift_report['recommendations'].append(
                        f"Significant drift detected in '{col}': {drift_score:.3f}"
                    )
            
            else:
                # カテゴリカルデータのドリフト
                ref_dist = reference_df[col].value_counts(normalize=True)
                curr_dist = current_df[col].value_counts(normalize=True)
                
                # Jensen-Shannon発散で分布比較
                try:
                    from scipy.spatial.distance import jensenshannon
                    
                    # 共通カテゴリを取得
                    all_categories = set(ref_dist.index) | set(curr_dist.index)
                    ref_aligned = [ref_dist.get(cat, 0) for cat in all_categories]
                    curr_aligned = [curr_dist.get(cat, 0) for cat in all_categories]
                    
                    js_distance = jensenshannon(ref_aligned, curr_aligned)
                    drift_report['drift_scores'][col] = js_distance
                    
                    if js_distance > threshold:
                        drift_report['drift_detected'].append(col)
                        drift_report['recommendations'].append(
                            f"Distribution drift detected in '{col}': {js_distance:.3f}"
                        )
                        
                except ImportError:
                    # scipyがない場合の簡易版
                    drift_score = len(set(curr_dist.index) - set(ref_dist.index)) / len(ref_dist)
                    drift_report['drift_scores'][col] = drift_score
        
        return drift_report

class TimeSeriesOptimizer:
    """時系列データ最適化"""
    
    @staticmethod
    def optimize_datetime_operations(df: pd.DataFrame, datetime_col: str) -> pd.DataFrame:
        """デ日時操作最適化"""
        optimized_df = df.copy()
        
        # datetimeインデックス設定
        if datetime_col in optimized_df.columns:
            optimized_df[datetime_col] = pd.to_datetime(optimized_df[datetime_col])
            optimized_df = optimized_df.set_index(datetime_col)
            
            # 時間関連フィーチャー自動生成
            optimized_df['year'] = optimized_df.index.year
            optimized_df['month'] = optimized_df.index.month
            optimized_df['day'] = optimized_df.index.day
            optimized_df['dayofweek'] = optimized_df.index.dayofweek
            optimized_df['hour'] = optimized_df.index.hour
            
            # 時系列ソート
            optimized_df = optimized_df.sort_index()
        
        return optimized_df
    
    @staticmethod
    def efficient_resampling(
        df: pd.DataFrame,
        freq: str,
        agg_funcs: Dict[str, Union[str, List[str]]]
    ) -> pd.DataFrame:
        """効率的なリサンプリング"""
        
        if not isinstance(df.index, pd.DatetimeIndex):
            raise ValueError("DataFrame must have DatetimeIndex for resampling")
        
        # リサンプリング実行
        resampled = df.resample(freq).agg(agg_funcs)
        
        # MultiIndexのフラット化
        if isinstance(resampled.columns, pd.MultiIndex):
            resampled.columns = ['_'.join(col).strip() for col in resampled.columns.values]
        
        return resampled
    
    @staticmethod
    def rolling_operations_optimized(
        df: pd.DataFrame,
        window: int,
        operations: List[str] = ['mean', 'std', 'min', 'max']
    ) -> pd.DataFrame:
        """最適化されたローリング操作"""
        
        result_df = df.copy()
        numeric_cols = df.select_dtypes(include=[np.number]).columns
        
        for col in numeric_cols:
            rolling = df[col].rolling(window=window, min_periods=1)
            
            for op in operations:
                if hasattr(rolling, op):
                    result_df[f'{col}_rolling_{op}_{window}'] = getattr(rolling, op)()
        
        return result_df

# 使用例とベンチマーク
class PandasBenchmark:
    """デPandasベンチマーク"""
    
    @staticmethod
    def create_test_dataframe(n_rows: int = 100000) -> pd.DataFrame:
        """テスト用DataFrame作成"""
        np.random.seed(42)
        
        data = {
            'id': range(n_rows),
            'category': np.random.choice(['A', 'B', 'C', 'D'], n_rows),
            'value1': np.random.randn(n_rows),
            'value2': np.random.randn(n_rows) * 100,
            'date': pd.date_range('2020-01-01', periods=n_rows, freq='H'),
            'text': [f'text_{i%1000}' for i in range(n_rows)]
        }
        
        return pd.DataFrame(data)
    
    @staticmethod
    def benchmark_groupby_operations(df: pd.DataFrame) -> Dict[str, float]:
        """グGroupBy操作ベンチマーク"""
        results = {}
        
        # 標準GroupBy
        start_time = time.perf_counter()
        standard_result = df.groupby('category').agg({
            'value1': ['mean', 'sum', 'count'],
            'value2': ['mean', 'std']
        })
        results['standard_groupby'] = time.perf_counter() - start_time
        
        # 最適化GroupBy
        optimizer = OptimizedPandas()
        start_time = time.perf_counter()
        optimized_result = optimizer.efficient_groupby(
            df,
            'category',
            {
                'value1': ['mean', 'sum', 'count'],
                'value2': ['mean', 'std']
            }
        )
        results['optimized_groupby'] = time.perf_counter() - start_time
        
        # 結果一致性チェック
        results['results_match'] = len(standard_result) == len(optimized_result)
        
        return results
    
    @staticmethod
    def benchmark_dtype_optimization(df: pd.DataFrame) -> Dict[str, Any]:
        """データタイプ最適化ベンチマーク"""
        optimizer = OptimizedPandas()
        
        original_memory = df.memory_usage(deep=True).sum()
        
        start_time = time.perf_counter()
        optimized_df = optimizer.optimize_dtypes(df, aggressive=True)
        optimization_time = time.perf_counter() - start_time
        
        optimized_memory = optimized_df.memory_usage(deep=True).sum()
        memory_reduction = (original_memory - optimized_memory) / original_memory * 100
        
        return {
            'original_memory_mb': original_memory / 1024 / 1024,
            'optimized_memory_mb': optimized_memory / 1024 / 1024,
            'memory_reduction_percent': memory_reduction,
            'optimization_time': optimization_time
        }

# 使用例
if __name__ == "__main__":
    # テストデータ作成
    test_df = PandasBenchmark.create_test_dataframe(50000)
    
    # データ品質チェック
    quality_checker = DataQualityChecker()
    profile = quality_checker.comprehensive_data_profile(test_df)
    
    print("データプロファイル:")
    print(f"  Shape: {profile['basic_info']['shape']}")
    print(f"  Memory: {profile['basic_info']['memory_usage_mb']:.2f}MB")
    print(f"  Quality Issues: {len(profile['data_quality_issues'])}")
    
    # パフォーマンスベンチマーク
    groupby_results = PandasBenchmark.benchmark_groupby_operations(test_df)
    print(f"\nGroupBy最適化: {groupby_results['standard_groupby']:.3f}s vs {groupby_results['optimized_groupby']:.3f}s")
    
    dtype_results = PandasBenchmark.benchmark_dtype_optimization(test_df)
    print(f"Memory節約: {dtype_results['memory_reduction_percent']:.1f}%")
```

### 10.3 scikit-learn・機械学習パイプライン

#### 10.3.1 プロダクションMLパイプライン

**Good: エンタープライズグレードMLパイプライン**
```python
# プロダクショングレード機械学習パイプライン
import numpy as np
import pandas as pd
from typing import Dict, List, Tuple, Any, Optional, Union, Callable
from dataclasses import dataclass, field
from pathlib import Path
import joblib
import json
from datetime import datetime
import warnings
from abc import ABC, abstractmethod

# scikit-learnインポート
from sklearn.base import BaseEstimator, TransformerMixin
from sklearn.pipeline import Pipeline
from sklearn.compose import ColumnTransformer
from sklearn.preprocessing import StandardScaler, RobustScaler, MinMaxScaler
from sklearn.preprocessing import LabelEncoder, OneHotEncoder, OrdinalEncoder
from sklearn.impute import SimpleImputer, KNNImputer
from sklearn.feature_selection import SelectKBest, SelectFromModel, RFE
from sklearn.model_selection import cross_val_score, GridSearchCV, RandomizedSearchCV
from sklearn.model_selection import train_test_split, StratifiedKFold, TimeSeriesSplit
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score
from sklearn.metrics import roc_auc_score, confusion_matrix, classification_report
from sklearn.metrics import mean_squared_error, mean_absolute_error, r2_score
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
from sklearn.linear_model import LogisticRegression, Ridge, ElasticNet
from sklearn.svm import SVC
from sklearn.utils.validation import check_X_y, check_array

@dataclass
class ModelMetrics:
    """モデル評価メトリクス"""
    model_name: str
    task_type: str  # 'classification' or 'regression'
    train_score: float
    validation_score: float
    test_score: float
    training_time: float
    prediction_time: float
    feature_importance: Optional[Dict[str, float]] = None
    hyperparameters: Dict[str, Any] = field(default_factory=dict)
    cross_val_scores: Optional[List[float]] = None
    confusion_matrix: Optional[np.ndarray] = None
    
class CustomTransformer(BaseEstimator, TransformerMixin):
    """カスタム前処理トランスフォーマー"""
    
    def __init__(self, transform_func: Callable = None):
        self.transform_func = transform_func
        self.fitted_ = False
    
    def fit(self, X, y=None):
        # 必要に応じてフィッティングロジックを実装
        self.fitted_ = True
        return self
    
    def transform(self, X):
        if not self.fitted_:
            raise ValueError("Transformer must be fitted before transform")
        
        if self.transform_func:
            return self.transform_func(X)
        return X

class AdvancedFeatureEngineer:
    """高度な特徴量エンジニアリング"""
    
    @staticmethod
    def create_polynomial_features(X: pd.DataFrame, degree: int = 2, interaction_only: bool = True) -> pd.DataFrame:
        """多項式特徴量作成"""
        from sklearn.preprocessing import PolynomialFeatures
        
        numeric_cols = X.select_dtypes(include=[np.number]).columns
        if len(numeric_cols) == 0:
            return X
        
        poly = PolynomialFeatures(degree=degree, interaction_only=interaction_only, include_bias=False)
        X_numeric = X[numeric_cols]
        X_poly = poly.fit_transform(X_numeric)
        
        # 新しいカラム名作成
        feature_names = poly.get_feature_names_out(numeric_cols)
        X_poly_df = pd.DataFrame(X_poly, columns=feature_names, index=X.index)
        
        # 元のカテゴリカルカラムを結合
        categorical_cols = X.select_dtypes(exclude=[np.number]).columns
        if len(categorical_cols) > 0:
            X_poly_df = pd.concat([X_poly_df, X[categorical_cols]], axis=1)
        
        return X_poly_df
    
    @staticmethod
    def create_time_features(df: pd.DataFrame, datetime_col: str) -> pd.DataFrame:
        """時間系特徴量作成"""
        df_copy = df.copy()
        
        if datetime_col not in df_copy.columns:
            raise ValueError(f"Column {datetime_col} not found")
        
        dt_series = pd.to_datetime(df_copy[datetime_col])
        
        # 基本的な時間特徴量
        df_copy[f'{datetime_col}_year'] = dt_series.dt.year
        df_copy[f'{datetime_col}_month'] = dt_series.dt.month
        df_copy[f'{datetime_col}_day'] = dt_series.dt.day
        df_copy[f'{datetime_col}_dayofweek'] = dt_series.dt.dayofweek
        df_copy[f'{datetime_col}_hour'] = dt_series.dt.hour
        df_copy[f'{datetime_col}_minute'] = dt_series.dt.minute
        
        # 週末フラグ
        df_copy[f'{datetime_col}_is_weekend'] = dt_series.dt.dayofweek.isin([5, 6]).astype(int)
        
        # 四半期
        df_copy[f'{datetime_col}_quarter'] = dt_series.dt.quarter
        
        # 月初・月末フラグ
        df_copy[f'{datetime_col}_is_month_start'] = dt_series.dt.is_month_start.astype(int)
        df_copy[f'{datetime_col}_is_month_end'] = dt_series.dt.is_month_end.astype(int)
        
        return df_copy
    
    @staticmethod
    def create_target_encoding(X: pd.DataFrame, y: pd.Series, categorical_cols: List[str], cv: int = 5) -> pd.DataFrame:
        """ターゲットエンコーディング"""
        from sklearn.model_selection import KFold
        
        X_encoded = X.copy()
        kf = KFold(n_splits=cv, shuffle=True, random_state=42)
        
        for col in categorical_cols:
            if col not in X.columns:
                continue
            
            # クロスバリデーションでターゲットエンコーディング
            encoded_values = np.zeros(len(X))
            
            for train_idx, val_idx in kf.split(X):
                # 訓練データでエンコーディングマップ作成
                train_X, train_y = X.iloc[train_idx], y.iloc[train_idx]
                encoding_map = train_X.groupby(col)[col].count().to_dict()
                target_mean_map = train_y.groupby(train_X[col]).mean().to_dict()
                
                # 検証データに適用
                val_X = X.iloc[val_idx]
                encoded_values[val_idx] = val_X[col].map(target_mean_map).fillna(y.mean())
            
            X_encoded[f'{col}_target_encoded'] = encoded_values
        
        return X_encoded

class MLPipelineBuilder:
    """機械学習パイプラインビルダー"""
    
    def __init__(self, task_type: str = 'classification'):
        self.task_type = task_type
        self.pipeline = None
        self.feature_engineer = AdvancedFeatureEngineer()
        
    def build_preprocessing_pipeline(
        self,
        numeric_features: List[str],
        categorical_features: List[str],
        numeric_strategy: str = 'robust',
        categorical_strategy: str = 'onehot'
    ) -> ColumnTransformer:
        """前処理パイプライン構築"""
        
        # 数値特徴量の前処理
        if numeric_strategy == 'standard':
            numeric_transformer = Pipeline([
                ('imputer', SimpleImputer(strategy='median')),
                ('scaler', StandardScaler())
            ])
        elif numeric_strategy == 'robust':
            numeric_transformer = Pipeline([
                ('imputer', SimpleImputer(strategy='median')),
                ('scaler', RobustScaler())
            ])
        elif numeric_strategy == 'minmax':
            numeric_transformer = Pipeline([
                ('imputer', SimpleImputer(strategy='median')),
                ('scaler', MinMaxScaler())
            ])
        else:
            numeric_transformer = Pipeline([
                ('imputer', SimpleImputer(strategy='median'))
            ])
        
        # カテゴリカル特徴量の前処理
        if categorical_strategy == 'onehot':
            categorical_transformer = Pipeline([
                ('imputer', SimpleImputer(strategy='constant', fill_value='missing')),
                ('encoder', OneHotEncoder(drop='first', sparse_output=False, handle_unknown='ignore'))
            ])
        elif categorical_strategy == 'ordinal':
            categorical_transformer = Pipeline([
                ('imputer', SimpleImputer(strategy='constant', fill_value='missing')),
                ('encoder', OrdinalEncoder(handle_unknown='use_encoded_value', unknown_value=-1))
            ])
        else:
            categorical_transformer = Pipeline([
                ('imputer', SimpleImputer(strategy='constant', fill_value='missing'))
            ])
        
        # 結合
        preprocessor = ColumnTransformer(
            transformers=[
                ('num', numeric_transformer, numeric_features),
                ('cat', categorical_transformer, categorical_features)
            ]
        )
        
        return preprocessor
    
    def build_full_pipeline(
        self,
        preprocessor: ColumnTransformer,
        model: BaseEstimator,
        feature_selection: Optional[str] = None,
        n_features: Optional[int] = None
    ) -> Pipeline:
        """完全なパイプライン構築"""
        
        steps = [('preprocessor', preprocessor)]
        
        # 特徴量選択
        if feature_selection == 'selectkbest' and n_features:
            if self.task_type == 'classification':
                from sklearn.feature_selection import chi2
                selector = SelectKBest(score_func=chi2, k=n_features)
            else:
                from sklearn.feature_selection import f_regression
                selector = SelectKBest(score_func=f_regression, k=n_features)
            steps.append(('feature_selection', selector))
        
        elif feature_selection == 'rfe' and n_features:
            selector = RFE(estimator=model, n_features_to_select=n_features)
            steps.append(('feature_selection', selector))
        
        elif feature_selection == 'model_based':
            selector = SelectFromModel(estimator=model)
            steps.append(('feature_selection', selector))
        
        # モデル追加
        steps.append(('model', model))
        
        self.pipeline = Pipeline(steps)
        return self.pipeline
    
    def hyperparameter_tuning(
        self,
        X: pd.DataFrame,
        y: pd.Series,
        param_grid: Dict[str, List],
        cv: int = 5,
        search_type: str = 'grid',
        n_iter: int = 100,
        scoring: str = None
    ) -> Tuple[Pipeline, Dict[str, Any]]:
        """ハイパーパラメーターチューニング"""
        
        if self.pipeline is None:
            raise ValueError("Pipeline must be built before hyperparameter tuning")
        
        # スコアリング指標設定
        if scoring is None:
            scoring = 'accuracy' if self.task_type == 'classification' else 'neg_mean_squared_error'
        
        # クロスバリデーション設定
        if self.task_type == 'classification':
            cv_strategy = StratifiedKFold(n_splits=cv, shuffle=True, random_state=42)
        else:
            cv_strategy = KFold(n_splits=cv, shuffle=True, random_state=42)
        
        # 探索タイプによる分岐
        if search_type == 'grid':
            search = GridSearchCV(
                self.pipeline,
                param_grid,
                cv=cv_strategy,
                scoring=scoring,
                n_jobs=-1,
                verbose=1
            )
        else:
            search = RandomizedSearchCV(
                self.pipeline,
                param_grid,
                n_iter=n_iter,
                cv=cv_strategy,
                scoring=scoring,
                n_jobs=-1,
                random_state=42,
                verbose=1
            )
        
        # 探索実行
        search.fit(X, y)
        
        # 結果返却
        best_pipeline = search.best_estimator_
        tuning_results = {
            'best_params': search.best_params_,
            'best_score': search.best_score_,
            'cv_results': search.cv_results_
        }
        
        return best_pipeline, tuning_results

class ModelEvaluator:
    """モデル評価クラス"""
    
    @staticmethod
    def comprehensive_evaluation(
        model: BaseEstimator,
        X_train: pd.DataFrame,
        X_test: pd.DataFrame,
        y_train: pd.Series,
        y_test: pd.Series,
        task_type: str = 'classification'
    ) -> ModelMetrics:
        """包括的モデル評価"""
        
        # 訓練時間計測
        start_time = time.perf_counter()
        model.fit(X_train, y_train)
        training_time = time.perf_counter() - start_time
        
        # 予測時間計測
        start_time = time.perf_counter()
        y_pred = model.predict(X_test)
        prediction_time = time.perf_counter() - start_time
        
        # スコア計算
        train_score = model.score(X_train, y_train)
        test_score = model.score(X_test, y_test)
        
        # クロスバリデーションスコア
        cv_scores = cross_val_score(model, X_train, y_train, cv=5)
        
        # 特徴量重要度
        feature_importance = None
        if hasattr(model, 'feature_importances_'):
            if hasattr(X_train, 'columns'):
                feature_importance = dict(zip(X_train.columns, model.feature_importances_))
            else:
                feature_importance = {f'feature_{i}': imp for i, imp in enumerate(model.feature_importances_)}
        
        # 混同行列（分類のみ）
        conf_matrix = None
        if task_type == 'classification':
            conf_matrix = confusion_matrix(y_test, y_pred)
        
        return ModelMetrics(
            model_name=model.__class__.__name__,
            task_type=task_type,
            train_score=train_score,
            validation_score=cv_scores.mean(),
            test_score=test_score,
            training_time=training_time,
            prediction_time=prediction_time,
            feature_importance=feature_importance,
            hyperparameters=model.get_params(),
            cross_val_scores=cv_scores.tolist(),
            confusion_matrix=conf_matrix
        )
    
    @staticmethod
    def compare_models(
        models: List[BaseEstimator],
        X_train: pd.DataFrame,
        X_test: pd.DataFrame,
        y_train: pd.Series,
        y_test: pd.Series,
        task_type: str = 'classification'
    ) -> List[ModelMetrics]:
        """複数モデル比較"""
        
        results = []
        
        for model in models:
            try:
                metrics = ModelEvaluator.comprehensive_evaluation(
                    model, X_train, X_test, y_train, y_test, task_type
                )
                results.append(metrics)
                
                print(f"{model.__class__.__name__}: Test Score = {metrics.test_score:.4f}, "
                      f"CV Score = {metrics.validation_score:.4f} (±{np.std(metrics.cross_val_scores):.4f})")
                      
            except Exception as e:
                print(f"Error evaluating {model.__class__.__name__}: {e}")
                continue
        
        # ソート（テストスコア順）
        results.sort(key=lambda x: x.test_score, reverse=True)
        
        return results

# 使用例
class MLWorkflow:
    """機械学習ワークフロー例"""
    
    @staticmethod
    def classification_workflow(
        df: pd.DataFrame,
        target_col: str,
        test_size: float = 0.2
    ) -> Tuple[List[ModelMetrics], Pipeline]:
        """分類タスクの完全ワークフロー"""
        
        # データ分割
        X = df.drop(columns=[target_col])
        y = df[target_col]
        
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=test_size, random_state=42, stratify=y
        )
        
        # 特徴量タイプ分類
        numeric_features = X.select_dtypes(include=[np.number]).columns.tolist()
        categorical_features = X.select_dtypes(exclude=[np.number]).columns.tolist()
        
        print(f"Numeric features: {len(numeric_features)}")
        print(f"Categorical features: {len(categorical_features)}")
        
        # パイプラインビルダー初期化
        builder = MLPipelineBuilder(task_type='classification')
        
        # 前処理パイプライン
        preprocessor = builder.build_preprocessing_pipeline(
            numeric_features, categorical_features
        )
        
        # モデル定義
        models = [
            LogisticRegression(random_state=42),
            RandomForestClassifier(random_state=42, n_estimators=100),
            GradientBoostingClassifier(random_state=42),
            SVC(random_state=42, probability=True)
        ]
        
        # モデル比較
        model_results = []
        
        for model in models:
            # パイプライン構築
            pipeline = builder.build_full_pipeline(preprocessor, model)
            
            # 評価
            metrics = ModelEvaluator.comprehensive_evaluation(
                pipeline, X_train, X_test, y_train, y_test, 'classification'
            )
            model_results.append(metrics)
        
        # 最適モデル選択
        best_metrics = max(model_results, key=lambda x: x.test_score)
        print(f"\nBest model: {best_metrics.model_name} (Test Score: {best_metrics.test_score:.4f})")
        
        # 最適モデルのパイプライン再構築
        best_model_class = next(m for m in models if m.__class__.__name__ == best_metrics.model_name)
        best_pipeline = builder.build_full_pipeline(preprocessor, best_model_class)
        best_pipeline.fit(X_train, y_train)
        
        return model_results, best_pipeline

# テスト用データ生成
def create_sample_classification_data(n_samples: int = 1000) -> pd.DataFrame:
    """サンプル分類データ作成"""
    from sklearn.datasets import make_classification
    
    X, y = make_classification(
        n_samples=n_samples,
        n_features=20,
        n_informative=15,
        n_redundant=5,
        n_classes=3,
        random_state=42
    )
    
    # データフレーム化
    feature_names = [f'feature_{i}' for i in range(X.shape[1])]
    df = pd.DataFrame(X, columns=feature_names)
    df['target'] = y
    
    # カテゴリカル特徴量追加
    df['category_A'] = np.random.choice(['cat1', 'cat2', 'cat3'], n_samples)
    df['category_B'] = np.random.choice(['type1', 'type2'], n_samples)
    
    return df

if __name__ == "__main__":
    # サンプルデータ作成
    sample_data = create_sample_classification_data(1000)
    
    # MLワークフロー実行
    results, best_pipeline = MLWorkflow.classification_workflow(sample_data, 'target')
    
    print("\n=== Model Comparison Results ===")
    for i, result in enumerate(results, 1):
        print(f"{i}. {result.model_name}: {result.test_score:.4f}")
```

---

## 11. Devin実行ガイドライン

### 11.1 AI開発エージェント対応

#### 11.1.1 Devinコードジェネレーション指針

**Good: Devinに最適化されたコード構造**
```python
# Devin AIエージェント対応コードジェネレーション指針
"""
Devin AIエージェント向けコードジェネレーションガイドライン

このファイルはDevin AIエージェントが効率的にコードを生成・修正できるように
設計されたベストプラクティスとパターン集です。

Key Principles for Devin Integration:
1. 明確で一貫したコード構造
2. 包括的なドキュメンテーション
3. 自動テスト可能な設計
4. モジュラーで再利用可能なコンポーネント
5. エラーハンドリングとログシステムの統合
"""

from typing import Dict, List, Any, Optional, Union, Callable, Protocol
from dataclasses import dataclass, field
from abc import ABC, abstractmethod
from enum import Enum
import logging
import json
from datetime import datetime
from pathlib import Path
import asyncio
from functools import wraps
import inspect

# === DEVIN向けコードジェネレーションパターン ===

class DevinCompatibleComponent(ABC):
    """
    Devin互換コンポーネントベースクラス
    
    このクラスを継承することで、Devinが理解しやすい
    一貫したインターフェースを持つコンポーネントを作成できます。
    """
    
    def __init__(self, name: str, config: Optional[Dict[str, Any]] = None):
        self.name = name
        self.config = config or {}
        self.logger = logging.getLogger(f"{self.__class__.__name__}.{name}")
        self._initialized = False
        self._metadata = self._generate_metadata()
    
    @abstractmethod
    def initialize(self) -> bool:
        """
        コンポーネントの初期化処理
        
        Returns:
            bool: 初期化成功時True
            
        Devin Note:
        - このメソッドは必ず実装してください
        - 初期化に必要なすべての処理をここに集約してください
        - エラー時はFalseを返し、ログに詳細を記録してください
        """
        pass
    
    @abstractmethod
    def process(self, input_data: Any) -> Any:
        """
        メイン処理ロジック
        
        Args:
            input_data: 入力データ
            
        Returns:
            処理結果
            
        Devin Note:
        - 入力データのバリデーションを必ず実装してください
        - 処理結果は一貫した形式で返してください
        - 例外は適切にキャッチし、意味のあるエラーメッセージを提供してください
        """
        pass
    
    def validate_input(self, input_data: Any) -> bool:
        """
        入力データのバリデーション
        
        Args:
            input_data: 検証するデータ
            
        Returns:
            bool: バリデーション成功時True
            
        Devin Note:
        - このメソッドをオーバーライドして入力検証ロジックを実装してください
        """
        return True
    
    def get_metadata(self) -> Dict[str, Any]:
        """
        コンポーネントのメタデータ取得
        
        Returns:
            Dict[str, Any]: コンポーネント情報
            
        Devin Note:
        - Devinはこのメタデータを使用してコンポーネントを理解します
        - 正確で詳細な情報を含めてください
        """
        return self._metadata
    
    def _generate_metadata(self) -> Dict[str, Any]:
        """メタデータ自動生成"""
        return {
            "component_name": self.name,
            "class_name": self.__class__.__name__,
            "version": "1.0.0",
            "description": self.__doc__ or "No description provided",
            "methods": self._get_public_methods(),
            "config_schema": self._get_config_schema(),
            "created_at": datetime.utcnow().isoformat()
        }
    
    def _get_public_methods(self) -> List[str]:
        """パブリックメソッド一覧取得"""
        return [method for method in dir(self) 
                if not method.startswith('_') and callable(getattr(self, method))]
    
    def _get_config_schema(self) -> Dict[str, str]:
        """設定スキーマ取得"""
        # サブクラスでオーバーライドして実装
        return {"config": "Generic configuration"}

# === 具体的な実装例 ===

class DataProcessor(DevinCompatibleComponent):
    """
    データ処理コンポーネント
    
    Devinが理解しやすいように設計されたデータ処理コンポーネント。
    各メソッドは明確な入力・出力とエラーハンドリングを持ちます。
    """
    
    def __init__(self, name: str = "data_processor", config: Optional[Dict[str, Any]] = None):
        default_config = {
            "batch_size": 1000,
            "max_retries": 3,
            "timeout_seconds": 30,
            "validation_enabled": True
        }
        final_config = {**default_config, **(config or {})}
        super().__init__(name, final_config)
        
        self.batch_size = self.config["batch_size"]
        self.max_retries = self.config["max_retries"]
        self.timeout_seconds = self.config["timeout_seconds"]
        self.validation_enabled = self.config["validation_enabled"]
    
    def initialize(self) -> bool:
        """
        データプロセッサーの初期化
        
        Returns:
            bool: 初期化成功敲True
        """
        try:
            self.logger.info(f"Initializing DataProcessor '{self.name}'")
            
            # 初期化処理をここに実装
            # 例: データベース接続、キャッシュ初期化など
            
            self._initialized = True
            self.logger.info("DataProcessor initialized successfully")
            return True
            
        except Exception as e:
            self.logger.error(f"Failed to initialize DataProcessor: {e}")
            return False
    
    def process(self, input_data: Union[List[Dict], Dict]) -> Dict[str, Any]:
        """
        データ処理メインロジック
        
        Args:
            input_data: 処理するデータ（辞書または辞書のリスト）
            
        Returns:
            Dict[str, Any]: 処理結果
            {
                "success": bool,
                "processed_count": int,
                "results": List[Dict],
                "errors": List[str],
                "processing_time": float
            }
            
        Raises:
            ValueError: 入力データが無効な場合
            ProcessingError: 処理中にエラーが発生した場合
        """
        if not self._initialized:
            raise RuntimeError("DataProcessor not initialized. Call initialize() first.")
        
        start_time = datetime.utcnow()
        
        try:
            # 入力検証
            if not self.validate_input(input_data):
                raise ValueError("Invalid input data provided")
            
            # データ正規化
            normalized_data = self._normalize_input(input_data)
            
            # バッチ処理
            results = []
            errors = []
            
            for batch in self._create_batches(normalized_data):
                try:
                    batch_result = self._process_batch(batch)
                    results.extend(batch_result)
                except Exception as e:
                    error_msg = f"Batch processing failed: {str(e)}"
                    self.logger.error(error_msg)
                    errors.append(error_msg)
            
            # 結果組立
            processing_time = (datetime.utcnow() - start_time).total_seconds()
            
            result = {
                "success": len(errors) == 0,
                "processed_count": len(results),
                "results": results,
                "errors": errors,
                "processing_time": processing_time
            }
            
            self.logger.info(
                f"Processing completed: {len(results)} items processed, "
                f"{len(errors)} errors, {processing_time:.2f}s"
            )
            
            return result
            
        except Exception as e:
            self.logger.error(f"Data processing failed: {e}")
            raise ProcessingError(f"Failed to process data: {str(e)}") from e
    
    def validate_input(self, input_data: Any) -> bool:
        """
        入力データのバリデーション
        
        Args:
            input_data: 検証するデータ
            
        Returns:
            bool: バリデーション成功時True
        """
        if not self.validation_enabled:
            return True
        
        if input_data is None:
            self.logger.error("Input data is None")
            return False
        
        if isinstance(input_data, dict):
            return self._validate_dict(input_data)
        elif isinstance(input_data, list):
            return all(self._validate_dict(item) if isinstance(item, dict) else False 
                      for item in input_data)
        else:
            self.logger.error(f"Unsupported input type: {type(input_data)}")
            return False
    
    def _validate_dict(self, data: Dict) -> bool:
        """辞書データのバリデーション"""
        # 必要なフィールドのチェック
        required_fields = ["id", "data"]
        for field in required_fields:
            if field not in data:
                self.logger.error(f"Missing required field: {field}")
                return False
        
        return True
    
    def _normalize_input(self, input_data: Union[List[Dict], Dict]) -> List[Dict]:
        """入力データの正規化"""
        if isinstance(input_data, dict):
            return [input_data]
        return input_data
    
    def _create_batches(self, data: List[Dict]) -> List[List[Dict]]:
        """データをバッチに分割"""
        batches = []
        for i in range(0, len(data), self.batch_size):
            batch = data[i:i + self.batch_size]
            batches.append(batch)
        return batches
    
    def _process_batch(self, batch: List[Dict]) -> List[Dict]:
        """バッチ処理"""
        results = []
        for item in batch:
            # 具体的な処理ロジックをここに実装
            processed_item = {
                "id": item["id"],
                "processed_data": item["data"],
                "processed_at": datetime.utcnow().isoformat()
            }
            results.append(processed_item)
        
        return results
    
    def _get_config_schema(self) -> Dict[str, str]:
        """設定スキーマ定義"""
        return {
            "batch_size": "int: Number of items to process per batch (default: 1000)",
            "max_retries": "int: Maximum number of retry attempts (default: 3)",
            "timeout_seconds": "int: Processing timeout in seconds (default: 30)",
            "validation_enabled": "bool: Enable input validation (default: True)"
        }

# === カスタム例外クラス ===

class ProcessingError(Exception):
    """
    データ処理エラー
    
    Devin Note:
    カスタム例外クラスは具体的で意味のあるエラー情報を提供します。
    """
    pass

# === Devinコードジェネレーションヘルパー ===

class DevinCodeHelper:
    """
    Devin AIエージェント向けコードジェネレーションヘルパー
    
    Devinが効率的にコードを生成・修正できるようにサポートするユーティリティ。
    """
    
    @staticmethod
    def generate_component_template(component_name: str, component_type: str = "processor") -> str:
        """
        Devin用コンポーネントテンプレート生成
        
        Args:
            component_name: コンポーネント名
            component_type: コンポーネントタイプ
            
        Returns:
            str: 生成されたコードテンプレート
        """
        
        class_name = ''.join(word.capitalize() for word in component_name.split('_'))
        
        template = f'''
class {class_name}(DevinCompatibleComponent):
    """
    {component_name}コンポーネント
    
    TODO: Devin, please implement the following:
    1. Add specific configuration parameters
    2. Implement initialization logic
    3. Implement main processing logic
    4. Add input validation
    5. Add error handling
    6. Add logging
    """
    
    def __init__(self, config: Optional[Dict[str, Any]] = None):
        default_config = {{
            # TODO: Devin, add specific configuration parameters here
            "example_param": "default_value"
        }}
        final_config = {{**default_config, **(config or {})}}
        super().__init__("{component_name}", final_config)
        
        # TODO: Devin, initialize component-specific attributes here
    
    def initialize(self) -> bool:
        """
        {class_name}の初期化
        
        TODO: Devin, implement initialization logic:
        - Validate configuration
        - Initialize external connections
        - Set up internal state
        - Return True on success, False on failure
        """
        try:
            self.logger.info(f"Initializing {{self.name}}")
            
            # TODO: Devin, add initialization code here
            
            self._initialized = True
            return True
            
        except Exception as e:
            self.logger.error(f"Failed to initialize {{self.name}}: {{e}}")
            return False
    
    def process(self, input_data: Any) -> Any:
        """
        メイン処理ロジック
        
        Args:
            input_data: 入力データ
            
        Returns:
            処理結果
            
        TODO: Devin, implement main processing logic:
        - Validate input using self.validate_input()
        - Process the data
        - Handle errors appropriately
        - Return structured results
        """
        if not self._initialized:
            raise RuntimeError(f"{{self.name}} not initialized. Call initialize() first.")
        
        try:
            # TODO: Devin, implement processing logic here
            if not self.validate_input(input_data):
                raise ValueError("Invalid input data")
            
            # Processing logic goes here
            result = input_data  # Placeholder
            
            return result
            
        except Exception as e:
            self.logger.error(f"Processing failed in {{self.name}}: {{e}}")
            raise
    
    def validate_input(self, input_data: Any) -> bool:
        """
        入力データのバリデーション
        
        TODO: Devin, implement input validation:
        - Check data types
        - Validate required fields
        - Check data ranges/constraints
        - Return True if valid, False otherwise
        """
        # TODO: Devin, add validation logic here
        return input_data is not None
    
    def _get_config_schema(self) -> Dict[str, str]:
        """設定スキーマ定義"""
        return {{
            # TODO: Devin, define configuration schema here
            "example_param": "str: Example parameter description"
        }}
'''
        return template
    
    @staticmethod
    def generate_test_template(component_name: str) -> str:
        """
        Devin用テストテンプレート生成
        
        Args:
            component_name: コンポーネント名
            
        Returns:
            str: 生成されたテストコードテンプレート
        """
        
        class_name = ''.join(word.capitalize() for word in component_name.split('_'))
        
        template = f'''
import pytest
from unittest.mock import Mock, patch
from {component_name} import {class_name}

class Test{class_name}:
    """
    {class_name}のテストクラス
    
    TODO: Devin, implement comprehensive tests:
    1. Test initialization
    2. Test main processing logic
    3. Test input validation
    4. Test error handling
    5. Test edge cases
    """
    
    def setup_method(self):
        """Test setup - runs before each test method"""
        self.config = {{
            # TODO: Devin, add test configuration here
        }}
        self.component = {class_name}(self.config)
    
    def test_initialization_success(self):
        """正常な初期化のテスト"""
        # TODO: Devin, implement initialization test
        result = self.component.initialize()
        assert result is True
        assert self.component._initialized is True
    
    def test_initialization_failure(self):
        """初期化失敗のテスト"""
        # TODO: Devin, implement failure case test
        pass
    
    def test_process_valid_input(self):
        """正常な入力での処理テスト"""
        # TODO: Devin, implement valid input test
        self.component.initialize()
        
        test_input = {{
            # TODO: Devin, add test input data
        }}
        
        result = self.component.process(test_input)
        
        # TODO: Devin, add result assertions
        assert result is not None
    
    def test_process_invalid_input(self):
        """無効な入力での処理テスト"""
        # TODO: Devin, implement invalid input test
        self.component.initialize()
        
        invalid_input = None  # TODO: Devin, define invalid input
        
        with pytest.raises(ValueError):
            self.component.process(invalid_input)
    
    def test_process_without_initialization(self):
        """未初期化状態での処理テスト"""
        test_input = {{}}  # TODO: Devin, add test input
        
        with pytest.raises(RuntimeError):
            self.component.process(test_input)
    
    def test_validate_input_valid(self):
        """正常な入力バリデーションテスト"""
        # TODO: Devin, implement validation test
        valid_input = {{}}  # TODO: Devin, define valid input
        result = self.component.validate_input(valid_input)
        assert result is True
    
    def test_validate_input_invalid(self):
        """無効な入力バリデーションテスト"""
        # TODO: Devin, implement validation test
        invalid_input = None
        result = self.component.validate_input(invalid_input)
        assert result is False
    
    def test_get_metadata(self):
        """メタデータ取得テスト"""
        metadata = self.component.get_metadata()
        
        assert "component_name" in metadata
        assert "class_name" in metadata
        assert "version" in metadata
        assert metadata["class_name"] == "{class_name}"
    
    # TODO: Devin, add more specific test methods based on component functionality
'''
        return template
    
    @staticmethod
    def create_component_files(component_name: str, output_dir: Path = Path(".")):
        """
        Devin用コンポーネントファイル一式生成
        
        Args:
            component_name: コンポーネント名
            output_dir: 出力ディレクトリ
        """
        output_dir = Path(output_dir)
        output_dir.mkdir(exist_ok=True)
        
        # コンポーネントファイル
        component_file = output_dir / f"{component_name}.py"
        component_code = DevinCodeHelper.generate_component_template(component_name)
        
        with open(component_file, 'w', encoding='utf-8') as f:
            f.write("# -*- coding: utf-8 -*-\n")
            f.write(f'"""\n{component_name} Component\n\nGenerated by DevinCodeHelper\nDate: {datetime.utcnow().isoformat()}\n"""\n')
            f.write(component_code)
        
        # テストファイル
        test_file = output_dir / f"test_{component_name}.py"
        test_code = DevinCodeHelper.generate_test_template(component_name)
        
        with open(test_file, 'w', encoding='utf-8') as f:
            f.write("# -*- coding: utf-8 -*-\n")
            f.write(f'"""\nTests for {component_name} Component\n\nGenerated by DevinCodeHelper\nDate: {datetime.utcnow().isoformat()}\n"""\n')
            f.write(test_code)
        
        print(f"Generated files for {component_name}:")
        print(f"  Component: {component_file}")
        print(f"  Tests: {test_file}")

# === 使用例 ===

if __name__ == "__main__":
    # Devin用コンポーネントの使用例
    
    print("=== Devin Compatible Component Example ===")
    
    # 1. データプロセッサーの作成と初期化
    processor = DataProcessor(
        name="example_processor",
        config={
            "batch_size": 100,
            "validation_enabled": True
        }
    )
    
    if processor.initialize():
        print("✓ DataProcessor initialized successfully")
        
        # 2. メタデータ表示
        metadata = processor.get_metadata()
        print(f"Component: {metadata['component_name']}")
        print(f"Class: {metadata['class_name']}")
        print(f"Methods: {', '.join(metadata['methods'])}")
        
        # 3. テストデータ処理
        test_data = [
            {"id": 1, "data": "test data 1"},
            {"id": 2, "data": "test data 2"}
        ]
        
        try:
            result = processor.process(test_data)
            print(f"✓ Processing completed: {result['processed_count']} items")
            
        except Exception as e:
            print(f"✗ Processing failed: {e}")
    
    else:
        print("✗ Failed to initialize DataProcessor")
    
    # 4. 新しいコンポーネントのテンプレート生成例
    print("\n=== Generating New Component Template ===")
    
    # コンポーネントテンプレート生成
    template = DevinCodeHelper.generate_component_template("email_sender")
    print("Generated component template (first 500 chars):")
    print(template[:500] + "...")
    
    # ファイル作成例（コメントアウト）
    # DevinCodeHelper.create_component_files("email_sender", Path("./generated"))
    
    print("\n=== Devin Instructions ===")
    print("""
    Devin AI Agent Instructions:
    
    1. Use DevinCompatibleComponent as base class for all new components
    2. Always implement initialize(), process(), and validate_input() methods
    3. Include comprehensive error handling and logging
    4. Follow the established patterns for configuration management
    5. Generate tests using the test template helper
    6. Document all methods with clear docstrings
    7. Use type hints for all parameters and return values
    8. Include TODO comments for areas that need implementation
    
    Example workflow:
    1. Create component using template: DevinCodeHelper.generate_component_template()
    2. Implement required methods following the patterns shown
    3. Generate tests using: DevinCodeHelper.generate_test_template()
    4. Test thoroughly before deployment
    
    This structure ensures consistent, maintainable, and testable code.
    """)
```

### 11.2 自動化可能な開発タスク

#### 11.2.1 Devin自動化タスクリスト

**Devinが効率的に実行できるタスク一覧**

```python
# Devin自動化タスク定義
from enum import Enum
from dataclasses import dataclass
from typing import List, Dict, Any, Optional

class TaskComplexity(Enum):
    """タスク複雑さレベル"""
    SIMPLE = "simple"          # 1-2時間で完了
    MODERATE = "moderate"      # 2-6時間で完了
    COMPLEX = "complex"        # 6-24時間で完了
    ADVANCED = "advanced"      # 1-3日で完了

class TaskCategory(Enum):
    """タスクカテゴリ"""
    CODE_GENERATION = "code_generation"
    TESTING = "testing"
    DOCUMENTATION = "documentation"
    REFACTORING = "refactoring"
    API_DEVELOPMENT = "api_development"
    DATABASE = "database"
    DEPLOYMENT = "deployment"
    MONITORING = "monitoring"
    MACHINE_LEARNING = "machine_learning"
    DATA_PROCESSING = "data_processing"

@dataclass
class DevinTask:
    """デDevin自動化タスク定義"""
    name: str
    description: str
    category: TaskCategory
    complexity: TaskComplexity
    prerequisites: List[str]
    deliverables: List[str]
    example_prompt: str
    estimated_time: str
    automation_level: str  # "fully", "mostly", "partially"

# === コード生成タスク ===
CODE_GENERATION_TASKS = [
    DevinTask(
        name="APIエンドポイント作成",
        description="FastAPIを使用したRESTful APIエンドポイントの作成",
        category=TaskCategory.API_DEVELOPMENT,
        complexity=TaskComplexity.MODERATE,
        prerequisites=["FastAPI基本設定", "Pydanticモデル", "データベース接続"],
        deliverables=[
            "エンドポイントコード",
            "リクエスト/レスポンスモデル",
            "エラーハンドリング",
            "APIドキュメント作成"
        ],
        example_prompt="""
        Create a FastAPI endpoint for user management with the following requirements:
        - GET /users: List all users with pagination
        - GET /users/{user_id}: Get specific user
        - POST /users: Create new user
        - PUT /users/{user_id}: Update user
        - DELETE /users/{user_id}: Delete user
        
        Include proper error handling, input validation, and OpenAPI documentation.
        """,
        estimated_time="2-4 hours",
        automation_level="fully"
    ),
    
    DevinTask(
        name="データベースモデル作成",
        description="SQLAlchemyを使用したデータベースモデルの作成",
        category=TaskCategory.DATABASE,
        complexity=TaskComplexity.SIMPLE,
        prerequisites=["データベーススキーマ設計", "SQLAlchemy設定"],
        deliverables=[
            "SQLAlchemyモデルクラス",
            "リレーション設定",
            "バリデーションルール",
            "マイグレーションファイル"
        ],
        example_prompt="""
        Create SQLAlchemy models for a blog system with:
        - User model (id, username, email, password_hash, created_at)
        - Post model (id, title, content, user_id, created_at, updated_at)
        - Comment model (id, content, post_id, user_id, created_at)
        
        Include proper relationships, indexes, and validation.
        """,
        estimated_time="1-2 hours",
        automation_level="fully"
    ),
    
    DevinTask(
        name="データ処理パイプライン",
        description="Pandasを使用したデータ処理パイプラインの作成",
        category=TaskCategory.DATA_PROCESSING,
        complexity=TaskComplexity.MODERATE,
        prerequisites=["データソース確認", "処理要件定義"],
        deliverables=[
            "データ読み込み関数",
            "データクリーニング処理",
            "特徴量エンジニアリング",
            "データ出力関数",
            "パフォーマンス最適化"
        ],
        example_prompt="""
        Create a data processing pipeline that:
        1. Reads CSV files from multiple sources
        2. Cleans and validates the data
        3. Performs feature engineering (create new columns, handle missing values)
        4. Aggregates data by time periods
        5. Exports results to database and CSV
        
        Include error handling, logging, and performance optimization.
        """,
        estimated_time="3-6 hours",
        automation_level="mostly"
    )
]

# === テスト作成タスク ===
TESTING_TASKS = [
    DevinTask(
        name="単体テスト作成",
        description="pytestを使用した包括的な単体テストの作成",
        category=TaskCategory.TESTING,
        complexity=TaskComplexity.MODERATE,
        prerequisites=["テスト対象コード", "pytest設定"],
        deliverables=[
            "テストケース作成",
            "モックオブジェクト設定",
            "テストデータ作成",
            "カバレッジレポート",
            "CI/CD統合"
        ],
        example_prompt="""
        Create comprehensive unit tests for the UserService class with:
        - Test all public methods
        - Test error conditions and edge cases
        - Use mocks for database interactions
        - Achieve 90%+ code coverage
        - Include parameterized tests for multiple scenarios
        
        Follow pytest best practices and include proper fixtures.
        """,
        estimated_time="2-4 hours",
        automation_level="fully"
    ),
    
    DevinTask(
        name="API結合テスト",
        description="FastAPIアプリケーションの結合テスト作成",
        category=TaskCategory.TESTING,
        complexity=TaskComplexity.COMPLEX,
        prerequisites=["APIエンドポイント", "テストデータベース"],
        deliverables=[
            "APIテストケース",
            "データベーステスト統合",
            "認証テスト",
            "エラーハンドリングテスト",
            "パフォーマンステスト"
        ],
        example_prompt="""
        Create integration tests for the blog API that:
        - Test complete user workflows (register, login, create post, comment)
        - Test authentication and authorization
        - Test database transactions and rollbacks
        - Test file upload functionality
        - Test rate limiting and security measures
        
        Use test database with proper setup/teardown.
        """,
        estimated_time="4-8 hours",
        automation_level="mostly"
    )
]

# === ドキュメント作成タスク ===
DOCUMENTATION_TASKS = [
    DevinTask(
        name="APIドキュメント作成",
        description="OpenAPI/Swaggerを使用したAPIドキュメントの作成",
        category=TaskCategory.DOCUMENTATION,
        complexity=TaskComplexity.SIMPLE,
        prerequisites=["APIエンドポイント完成"],
        deliverables=[
            "OpenAPIスキーマ",
            "API使用例",
            "エラーコード一覧",
            "認証ガイド",
            "Postmanコレクション"
        ],
        example_prompt="""
        Create comprehensive API documentation for the user management system:
        - Complete OpenAPI schema with examples
        - Request/response examples for each endpoint
        - Authentication guide with sample tokens
        - Error handling documentation
        - Rate limiting information
        - SDK generation setup
        """,
        estimated_time="2-3 hours",
        automation_level="fully"
    ),
    
    DevinTask(
        name="コードドキュメント作成",
        description="Sphinxを使用したコードドキュメントの作成",
        category=TaskCategory.DOCUMENTATION,
        complexity=TaskComplexity.MODERATE,
        prerequisites=["コードベース完成", "docstring作成済み"],
        deliverables=[
            "Sphinx設定",
            "APIリファレンス",
            "チュートリアル",
            "コード例集",
            "HTML/PDF出力"
        ],
        example_prompt="""
        Create code documentation using Sphinx that includes:
        - Automatic API reference from docstrings
        - User guide with examples
        - Installation and setup instructions
        - Contributing guidelines
        - Architecture overview
        - Generate both HTML and PDF versions
        """,
        estimated_time="3-5 hours",
        automation_level="mostly"
    )
]

# === デプロイメントタスク ===
DEPLOYMENT_TASKS = [
    DevinTask(
        name="Dockerコンテナ化",
        description="アプリケーションのDockerコンテナ化と最適化",
        category=TaskCategory.DEPLOYMENT,
        complexity=TaskComplexity.MODERATE,
        prerequisites=["アプリケーションコード", "依存関係一覧"],
        deliverables=[
            "最適化されたDockerfile",
            "docker-compose.yml",
            "環境別設定ファイル",
            "ヘルスチェック設定",
            "ビルドスクリプト"
        ],
        example_prompt="""
        Create Docker configuration for a FastAPI application:
        - Multi-stage build for optimization
        - Non-root user for security
        - Health checks
        - Environment-specific configurations
        - Docker Compose for local development
        - Build and deployment scripts
        """,
        estimated_time="2-4 hours",
        automation_level="fully"
    ),
    
    DevinTask(
        name="CI/CDパイプライン構築",
        description="GitHub Actionsを使用したCI/CDパイプラインの作成",
        category=TaskCategory.DEPLOYMENT,
        complexity=TaskComplexity.COMPLEX,
        prerequisites=["テスト環境", "Docker設定", "デプロイ先環境"],
        deliverables=[
            "GitHub Actionsワークフロー",
            "テスト自動化",
            "セキュリティスキャン",
            "自動デプロイ",
            "ロールバック機能"
        ],
        example_prompt="""
        Create a complete CI/CD pipeline that:
        - Runs tests on multiple Python versions
        - Performs security and code quality checks
        - Builds and pushes Docker images
        - Deploys to staging automatically
        - Deploys to production with manual approval
        - Includes rollback capabilities
        - Sends notifications on failures
        """,
        estimated_time="6-12 hours",
        automation_level="mostly"
    )
]

# === 機械学習タスク ===
ML_TASKS = [
    DevinTask(
        name="MLモデルパイプライン作成",
        description="scikit-learnを使用した機械学習パイプラインの作成",
        category=TaskCategory.MACHINE_LEARNING,
        complexity=TaskComplexity.COMPLEX,
        prerequisites=["データセット", "問題設定", "評価指標定義"],
        deliverables=[
            "データ前処理パイプライン",
            "特徴量エンジニアリング",
            "モデル訓練コード",
            "ハイパーパラメーターチューニング",
            "モデル評価レポート"
        ],
        example_prompt="""
        Create a machine learning pipeline for customer churn prediction:
        - Data preprocessing with missing value handling
        - Feature engineering and selection
        - Multiple model comparison (RF, XGBoost, LR)
        - Hyperparameter tuning with cross-validation
        - Model evaluation with comprehensive metrics
        - Feature importance analysis
        - Model serialization for deployment
        """,
        estimated_time="8-16 hours",
        automation_level="mostly"
    )
]

# === 全タスク統合 ===
ALL_DEVIN_TASKS = (
    CODE_GENERATION_TASKS + 
    TESTING_TASKS + 
    DOCUMENTATION_TASKS + 
    DEPLOYMENT_TASKS + 
    ML_TASKS
)

# === Devinタスクマネージャー ===
class DevinTaskManager:
    """デDevinタスク管理クラス"""
    
    def __init__(self):
        self.tasks = {task.name: task for task in ALL_DEVIN_TASKS}
    
    def get_tasks_by_category(self, category: TaskCategory) -> List[DevinTask]:
        """カテゴリ別タスク取得"""
        return [task for task in self.tasks.values() if task.category == category]
    
    def get_tasks_by_complexity(self, complexity: TaskComplexity) -> List[DevinTask]:
        """複雑さ別タスク取得"""
        return [task for task in self.tasks.values() if task.complexity == complexity]
    
    def get_fully_automated_tasks(self) -> List[DevinTask]:
        """完全自動化可能タスク取得"""
        return [task for task in self.tasks.values() if task.automation_level == "fully"]
    
    def suggest_task_order(self, task_names: List[str]) -> List[str]:
        """タスク実行順序推奨"""
        # 簡単な依存関係ベースのソート
        ordered_tasks = []
        remaining_tasks = task_names.copy()
        
        while remaining_tasks:
            for task_name in remaining_tasks.copy():
                task = self.tasks[task_name]
                # 依存タスクが完了しているかチェック
                dependencies_met = all(
                    dep in ordered_tasks or dep not in task_names 
                    for dep in task.prerequisites
                )
                
                if dependencies_met:
                    ordered_tasks.append(task_name)
                    remaining_tasks.remove(task_name)
        
        return ordered_tasks
    
    def generate_task_report(self) -> str:
        """タスクレポート生成"""
        report = ["=== Devin Automation Task Report ==="]
        
        # カテゴリ別統計
        for category in TaskCategory:
            tasks = self.get_tasks_by_category(category)
            if tasks:
                report.append(f"\n{category.value.title()} Tasks: {len(tasks)}")
                for task in tasks:
                    report.append(f"  - {task.name} ({task.complexity.value}, {task.automation_level})")
        
        # 複雑さ別統計
        report.append("\n=== Complexity Distribution ===")
        for complexity in TaskComplexity:
            tasks = self.get_tasks_by_complexity(complexity)
            report.append(f"{complexity.value.title()}: {len(tasks)} tasks")
        
        # 自動化レベル統計
        automation_stats = {}
        for task in self.tasks.values():
            level = task.automation_level
            automation_stats[level] = automation_stats.get(level, 0) + 1
        
        report.append("\n=== Automation Level Distribution ===")
        for level, count in automation_stats.items():
            report.append(f"{level.title()}: {count} tasks")
        
        return "\n".join(report)

# 使用例
if __name__ == "__main__":
    task_manager = DevinTaskManager()
    
    print(task_manager.generate_task_report())
    
    print("\n=== Fully Automated Tasks ===")
    fully_automated = task_manager.get_fully_automated_tasks()
    for task in fully_automated:
        print(f"- {task.name} ({task.estimated_time})")
    
    print("\n=== Example Task Prompt ===")
    api_task = task_manager.tasks["APIエンドポイント作成"]
    print(f"Task: {api_task.name}")
    print(f"Prompt: {api_task.example_prompt}")
```

---

## 結語

本 Python 開発標準文書は、現代的な Python 開発における包括的なベストプラクティスを提供します。PEP8 準拠から始まり、エンタープライズグレードのアーキテクチャパターン、AI/ML 特化の最適化手法、そして Devin AI エージェントとの協働まで、幅広いトピックをカバーしています。

この文書を活用することで、チーム全体のコード品質向上、保守性の改善、そしてプロダクティビティの向上を期待できます。継続的な改善とアップデートを通じて、組織の技術的成熟度を高めていきましょう。

**最終更新日**: 2025-10-15  
**バージョン**: 1.0.0  
**総ページ数**: 380KB+ / 13,000行+

# Python ドキュメンテーション標準追加セクション

---

## X. ドキュメンテーション標準（Documentation Standards）

### X.1 Docstring 必須要件

#### **適用範囲**

**Level 1: 必須（品質ゲート）**
- すべてのPythonモジュール（ファイル）のモジュールDocstring
- すべてのパブリッククラス
- すべてのパブリック関数・メソッド
- すべてのパッケージの `__init__.py`

**Level 2: 強く推奨**
- 複雑な内部ロジック（循環的複雑度10以上）
- ビジネスルール・制約を反映した実装
- 非自明な実装（パフォーマンス最適化、技術的回避策）

**Level 3: 任意**
- 単純なプロパティ（`@property`）
- 自己説明的なプライベートメソッド

---

### X.2 Google Style Docstring 標準形式

#### **モジュールDocstring（必須）**

```python
"""ユーザー認証モジュール

ユーザーのログイン・ログアウト・トークン検証機能を提供する。
JWTベースの認証を実装し、リフレッシュトークンをサポート。

典型的な使用例:
    auth_service = AuthService()
    tokens = auth_service.login(email='user@example.com', password='secret')
    
Note:
    このモジュールはシングルトンパターンで実装されている。
    複数インスタンスの生成は推奨されない。
"""

import jwt
from typing import Optional
# ...
```

**必須要素**:
- モジュールの目的・責任の説明（1-3行）
- 主な機能の概要

**推奨要素**:
- 典型的な使用例
- 注意事項・制約条件

---

#### **クラスDocstring（必須）**

```python
class AuthService:
    """ユーザー認証サービス
    
    JWTトークンの生成・検証、ユーザーセッション管理を担当する。
    シングルトンパターンで実装され、アプリケーション全体で共有される。
    
    Attributes:
        token_expiration (int): トークン有効期限（秒）
        refresh_enabled (bool): リフレッシュトークン機能の有効化フラグ
    
    Example:
        >>> auth = AuthService()
        >>> tokens = auth.login('user@example.com', 'password123')
        >>> print(tokens['access_token'])
    
    Note:
        スレッドセーフではないため、マルチスレッド環境では
        適切なロック機構を実装すること。
    """
    
    def __init__(self, token_expiration: int = 3600):
        """AuthServiceを初期化する
        
        Args:
            token_expiration: トークン有効期限（秒）。デフォルトは3600秒（1時間）。
        
        Raises:
            ValueError: token_expirationが0以下の場合
        """
        # 実装...
```

**必須要素**:
- クラスの目的・責任
- `Attributes`: パブリック属性の説明

**推奨要素**:
- `Example`: 使用例
- `Note`: 注意事項・制約

---

#### **関数・メソッドDocstring（パブリックAPIは必須）**

```python
def login(self, email: str, password: str, two_factor_code: Optional[str] = None) -> dict:
    """ユーザーをログインさせ、JWTトークンを発行する
    
    認証情報を検証し、成功した場合はアクセストークンと
    リフレッシュトークンのペアを返す。
    
    Args:
        email: ユーザーのメールアドレス。形式検証済みであること。
        password: ユーザーのパスワード（平文）。ハッシュ化して検証される。
        two_factor_code: 2要素認証コード（オプション）。
            2要素認証が有効なユーザーの場合は必須。
    
    Returns:
        トークンペアを含む辞書:
            - access_token (str): アクセストークン（有効期限: 15分）
            - refresh_token (str): リフレッシュトークン（有効期限: 7日）
            - expires_in (int): アクセストークンの有効期限（秒）
    
    Raises:
        AuthenticationError: 認証情報が不正な場合
        TwoFactorRequiredError: 2要素認証が必要だが、コードが提供されていない場合
        NetworkError: 認証サーバーへの接続に失敗した場合
    
    Example:
        >>> tokens = auth.login('user@example.com', 'password123')
        >>> print(tokens['access_token'])
        eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
        
    Note:
        このメソッドは同期的に実行される。非同期版は `async_login()` を使用。
    """
    # 実装...
```

**必須要素**:
- 関数の目的・動作の説明
- `Args`: すべてのパラメータの説明（型ヒントと併用）
- `Returns`: 戻り値の詳細な説明
- `Raises`: 発生する可能性のある例外

**推奨要素**:
- `Example`: 使用例（複雑なAPIの場合）
- `Note`: 注意事項

---

#### **プロパティ・属性（パブリックは必須）**

```python
@property
def is_authenticated(self) -> bool:
    """現在の認証状態を返す
    
    Returns:
        認証済みの場合はTrue、未認証の場合はFalse
        
    Note:
        このプロパティはトークンの有効期限を考慮する。
        有効期限切れの場合はFalseを返す。
    """
    return self._token is not None and not self._is_token_expired()
```

---

#### **定数・設定値（パブリックは必須）**

```python
"""
JWT トークンの有効期限設定

セキュリティ要件に基づき、短期間のアクセストークンと
長期間のリフレッシュトークンを使い分ける。
"""
TOKEN_EXPIRATION = {
    'ACCESS_TOKEN': 15 * 60,  # 15分（秒）
    'REFRESH_TOKEN': 7 * 24 * 60 * 60,  # 7日（秒）
}
```

---

### X.3 Pylint / Flake8 による自動チェック

#### **Pylint 設定（.pylintrc）**

```ini
[MASTER]
# Docstring チェックを有効化
load-plugins=pylint.extensions.docparams

[MESSAGES CONTROL]
# Docstring 必須化
enable=missing-module-docstring,
       missing-class-docstring,
       missing-function-docstring,
       missing-param-doc,
       missing-return-doc,
       missing-raises-doc,
       missing-type-doc

[BASIC]
# Docstring の最小長さ（文字数）
docstring-min-length=10

# パブリック関数は必ず Docstring
no-docstring-rgx=^_

[PARAMETER_DOCUMENTATION]
# パラメータドキュメントの厳密性
accept-no-param-doc=no
accept-no-raise-doc=no
accept-no-return-doc=no
```

#### **Flake8 + pydocstyle 設定（.flake8 / setup.cfg）**

```ini
[flake8]
# pydocstyle を統合
docstring-convention = google
ignore = D100,D104  # パッケージ __init__.py の Docstring は任意

# Docstring チェックを有効化
select = D

# 除外パターン
exclude = 
    .git,
    __pycache__,
    .venv,
    tests/*

[pydocstyle]
convention = google
add-ignore = D100,D104
```

#### **インストール**

```bash
pip install pylint flake8 pydocstyle
```

---

### X.4 型ヒントとの統合

#### **型ヒント + Docstring の併用**

```python
from typing import Optional, Dict, List

def get_user_by_id(user_id: str) -> Optional[Dict[str, any]]:
    """ユーザーデータをIDで取得する
    
    データベースからユーザー情報を取得し、存在しない場合はNoneを返す。
    キャッシュ機構により、頻繁なアクセスでもパフォーマンスを維持。
    
    Args:
        user_id: 取得するユーザーのID（UUID形式）
    
    Returns:
        ユーザーオブジェクト（辞書形式）。見つからない場合はNone。
        辞書には以下のキーが含まれる:
            - id (str): ユーザーID
            - name (str): ユーザー名
            - email (str): メールアドレス
            - created_at (datetime): 作成日時
    
    Example:
        >>> user = get_user_by_id('user-123')
        >>> if user:
        ...     print(user['name'])
    """
    # 実装...
```

**ポイント**:
- 型ヒントは必ず記載（Python 3.6+）
- Docstring は型の意味・制約・構造を説明
- 型情報の重複は避ける（型で表現できる情報は省略可）

---

### X.5 ドキュメント生成

#### **Sphinx の使用**

```bash
# インストール
pip install sphinx sphinx-rtd-theme

# 初期化
sphinx-quickstart docs

# ドキュメント生成
cd docs
make html
```

#### **conf.py 設定**

```python
# Sphinx 拡張機能
extensions = [
    'sphinx.ext.autodoc',      # Docstring からドキュメント生成
    'sphinx.ext.napoleon',     # Google Style Docstring サポート
    'sphinx.ext.viewcode',     # ソースコードリンク
    'sphinx.ext.intersphinx',  # 外部ドキュメントへのリンク
]

# Napoleon 設定（Google Style）
napoleon_google_docstring = True
napoleon_numpy_docstring = False
napoleon_include_init_with_doc = True
napoleon_include_private_with_doc = False
napoleon_include_special_with_doc = False

# テーマ
html_theme = 'sphinx_rtd_theme'
```

---

### X.6 コードレビューチェックリスト

**レビュアーは以下を確認**:

#### **必須項目**
- [ ] モジュール Docstring が存在するか
- [ ] パブリッククラスすべてに Docstring があるか
- [ ] パブリック関数・メソッドすべてに Docstring があるか
- [ ] `Args`、`Returns`、`Raises` が適切に記載されているか

#### **品質項目**
- [ ] 「何を」だけでなく「なぜ」が説明されているか
- [ ] ビジネスルール・制約が明記されているか
- [ ] 複雑なロジック（複雑度10以上）にコメントがあるか
- [ ] 使用例が必要な複雑なAPIに `Example` があるか
- [ ] 型ヒントが記載されているか（Python 3.6+）

#### **エラー検出**
- [ ] Pylint で Docstring ルール違反がないか
- [ ] pydocstyle でスタイル違反がないか
- [ ] Sphinx でドキュメント生成エラーがないか

---

### X.7 ベストプラクティス

#### **✅ Good Examples**

```python
"""注文処理モジュール

注文の作成・更新・キャンセル処理を提供する。
在庫管理システムとの連携により、在庫確認・引き当てを実行。
"""

from typing import Dict, Optional
from decimal import Decimal

def create_order(order_data: Dict[str, any]) -> Dict[str, any]:
    """注文を作成し、在庫を引き当てる
    
    トランザクション内で以下を実行:
    1. 在庫の可用性チェック
    2. 注文レコードの作成
    3. 在庫の引き当て
    
    いずれかの処理が失敗した場合、すべてロールバックされる。
    
    Args:
        order_data: 注文データ。以下のキーを含む辞書:
            - customer_id (str): 顧客ID
            - items (List[Dict]): 注文アイテムのリスト
            - shipping_address (Dict): 配送先住所
    
    Returns:
        作成された注文オブジェクト（辞書形式）:
            - order_id (str): 注文ID
            - status (str): 注文ステータス
            - total_amount (Decimal): 合計金額
            - created_at (datetime): 作成日時
    
    Raises:
        InsufficientStockError: 在庫不足の場合
        DatabaseError: データベース操作に失敗した場合
        ValidationError: 注文データが不正な場合
    
    Example:
        >>> order = create_order({
        ...     'customer_id': 'cust-123',
        ...     'items': [{'product_id': 'prod-1', 'quantity': 2}],
        ...     'shipping_address': {'city': 'Tokyo', ...}
        ... })
        >>> print(order['order_id'])
        order-456
    
    Note:
        この関数は同期的に実行される。大量の注文処理には
        非同期版の `async_create_order()` を推奨。
    """
    # 実装...
```

#### **❌ Bad Examples**

```python
# ❌ Docstring なし（パブリック関数は必須）
def process_payment(data):
    # ...
    pass

# ❌ 「何を」の繰り返しのみ（「なぜ」がない）
def get_user(id):
    """ユーザーを取得する
    
    Args:
        id: ID
    
    Returns:
        ユーザー
    """
    pass

# ❌ パラメータ・戻り値の説明不足
def process_data(data):
    """データを処理する"""
    pass

# ❌ 型ヒントがない（Python 3.6+では必須）
def calculate_total(items):
    """合計金額を計算する"""
    pass
```

---

### X.8 まとめ

**必須ルール**:
1. すべてのモジュールにモジュール Docstring
2. すべてのパブリッククラスに Docstring
3. すべてのパブリック関数・メソッドに完全な Docstring（`Args`、`Returns`、`Raises`）
4. 型ヒントの記載（Python 3.6+）
5. Pylint / pydocstyle で自動チェック

**推奨プラクティス**:
1. Google Style Docstring の採用
2. 複雑なロジック（複雑度10以上）にインラインコメント
3. ビジネスルール・制約の明記
4. 使用例（`Example`）の提供
5. Sphinx による定期的なドキュメント生成

**コードレビューで不合格となる条件**:
- パブリックAPIに Docstring がない
- `Args`、`Returns` の記載漏れ
- 型ヒントの欠如（Python 3.6+）
- Pylint / pydocstyle ルール違反

---

**このセクションを `python-standards.md` の末尾に追加してください。**
