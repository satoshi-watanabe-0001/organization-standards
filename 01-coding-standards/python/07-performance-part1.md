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


