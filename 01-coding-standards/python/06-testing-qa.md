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

