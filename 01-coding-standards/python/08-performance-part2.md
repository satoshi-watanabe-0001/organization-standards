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

