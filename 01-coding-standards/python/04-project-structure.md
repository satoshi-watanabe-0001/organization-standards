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
