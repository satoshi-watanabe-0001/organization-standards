# RESTful クエリパラメータ

**このドキュメントについて**: API設計標準 - RESTful クエリパラメータ

---

### 2.4 クエリパラメータ設計

#### 2.4.1 ページネーションパターン

**Good: 標準的なページネーション設計**
```python
# FastAPI - 高度なページネーションシステム
from fastapi import FastAPI, Query, HTTPException, Depends
from sqlalchemy.orm import Session
from sqlalchemy import func, asc, desc
from typing import Optional, List, Dict, Any
from enum import Enum
import math

app = FastAPI(title="Advanced Pagination API")

# ソート順序の列挙型
class SortOrder(str, Enum):
    ASC = "asc"
    DESC = "desc"

# フィルタリングオプション
class UserFilter(str, Enum):
    ALL = "all"
    ACTIVE = "active"
    INACTIVE = "inactive"
    VERIFIED = "verified"
    UNVERIFIED = "unverified"

# ページネーションレスポンスモデル
class PaginationMeta(BaseModel):
    page: int
    per_page: int
    total: int
    pages: int
    has_next: bool
    has_prev: bool
    next_page: Optional[int] = None
    prev_page: Optional[int] = None

class PaginatedUsersResponse(BaseModel):
    data: List[UserResponse]
    meta: PaginationMeta
    links: Dict[str, Optional[str]]

# オフセットベースページネーション
@app.get("/users", response_model=PaginatedUsersResponse)
async def get_users_paginated(
    # ページネーションパラメータ
    page: int = Query(1, ge=1, description="ページ番号（1から開始）"),
    per_page: int = Query(20, ge=1, le=100, description="1ページあたりの件数"),
    
    # ソートパラメータ
    sort_by: str = Query("created_at", description="ソートキー"),
    sort_order: SortOrder = Query(SortOrder.DESC, description="ソート順序"),
    
    # フィルタリングパラメータ
    status: UserFilter = Query(UserFilter.ALL, description="ユーザーステータスフィルタ"),
    search: Optional[str] = Query(None, min_length=2, max_length=100, description="検索キーワード"),
    created_after: Optional[datetime] = Query(None, description="作成日時以降フィルタ"),
    created_before: Optional[datetime] = Query(None, description="作成日時以前フィルタ"),
    
    # データベースセッション
    db: Session = Depends(get_db),
    request: Request = None
):
    """
    高度なページネーション付きユーザー一覧取得
    
    機能:
    - オフセットベースページネーション
    - 柔軟なソート機能
    - 組み合わせ可能なフィルタリング
    - 全文検索対応
    - HATEOASリンク生成
    """
    # クエリビルダー初期化
    query = db.query(User)
    
    # フィルタリング適用
    if status == UserFilter.ACTIVE:
        query = query.filter(User.is_active == True)
    elif status == UserFilter.INACTIVE:
        query = query.filter(User.is_active == False)
    elif status == UserFilter.VERIFIED:
        query = query.filter(User.email_verified_at.isnot(None))
    elif status == UserFilter.UNVERIFIED:
        query = query.filter(User.email_verified_at.is_(None))
    
    # 日付範囲フィルタ
    if created_after:
        query = query.filter(User.created_at >= created_after)
    if created_before:
        query = query.filter(User.created_at <= created_before)
    
    # 全文検索
    if search:
        search_filter = f"%{search}%"
        query = query.filter(
            or_(
                User.username.ilike(search_filter),
                User.email.ilike(search_filter),
                User.first_name.ilike(search_filter),
                User.last_name.ilike(search_filter)
            )
        )
    
    # ソート適用
    sort_column = getattr(User, sort_by, None)
    if sort_column is None:
        raise HTTPException(
            status_code=400,
            detail=f"Invalid sort field: {sort_by}"
        )
    
    if sort_order == SortOrder.ASC:
        query = query.order_by(asc(sort_column))
    else:
        query = query.order_by(desc(sort_column))
    
    # 総件数取得
    total = query.count()
    
    # ページ数計算
    pages = math.ceil(total / per_page) if total > 0 else 1
    
    # ページ番号バリデーション
    if page > pages and total > 0:
        raise HTTPException(
            status_code=404,
            detail=f"Page {page} not found. Total pages: {pages}"
        )
    
    # オフセット計算とデータ取得
    offset = (page - 1) * per_page
    users = query.offset(offset).limit(per_page).all()
    
    # メタデータ作成
    has_next = page < pages
    has_prev = page > 1
    
    meta = PaginationMeta(
        page=page,
        per_page=per_page,
        total=total,
        pages=pages,
        has_next=has_next,
        has_prev=has_prev,
        next_page=page + 1 if has_next else None,
        prev_page=page - 1 if has_prev else None
    )
    
    # HATEOASリンク生成
    base_url = str(request.url).split('?')[0]
    query_params = {k: v for k, v in request.query_params.items() if k != 'page'}
    
    def build_link(page_num):
        if page_num is None:
            return None
        params = {**query_params, 'page': page_num}
        param_str = '&'.join([f"{k}={v}" for k, v in params.items()])
        return f"{base_url}?{param_str}"
    
    links = {
        'self': build_link(page),
        'first': build_link(1) if pages > 0 else None,
        'last': build_link(pages) if pages > 0 else None,
        'next': build_link(meta.next_page),
        'prev': build_link(meta.prev_page)
    }
    
    return PaginatedUsersResponse(
        data=[UserResponse.from_orm(user) for user in users],
        meta=meta,
        links=links
    )

# カーソルベースページネーション（大量データ用）
@app.get("/users/cursor", response_model=Dict[str, Any])
async def get_users_cursor_paginated(
    cursor: Optional[str] = Query(None, description="カーソルトークン"),
    limit: int = Query(20, ge=1, le=100, description="取得件数上限"),
    direction: str = Query("next", regex="^(next|prev)$", description="ページ方向"),
    db: Session = Depends(get_db)
):
    """
    カーソルベースページネーション
    
    大量データに対して高パフォーマンスを提供
    カーソルにはcreated_at + idの組み合わせを使用
    """
    query = db.query(User).order_by(desc(User.created_at), desc(User.id))
    
    # カーソルデコード
    cursor_timestamp = None
    cursor_id = None
    
    if cursor:
        try:
            import base64
            import json
            
            decoded = base64.b64decode(cursor).decode('utf-8')
            cursor_data = json.loads(decoded)
            cursor_timestamp = datetime.fromisoformat(cursor_data['timestamp'])
            cursor_id = cursor_data['id']
        except (ValueError, KeyError, json.JSONDecodeError):
            raise HTTPException(
                status_code=400,
                detail="Invalid cursor format"
            )
    
    # カーソルフィルタ適用
    if cursor_timestamp and cursor_id:
        if direction == "next":
            query = query.filter(
                or_(
                    User.created_at < cursor_timestamp,
                    and_(
                        User.created_at == cursor_timestamp,
                        User.id < cursor_id
                    )
                )
            )
        else:  # prev
            query = query.filter(
                or_(
                    User.created_at > cursor_timestamp,
                    and_(
                        User.created_at == cursor_timestamp,
                        User.id > cursor_id
                    )
                )
            ).order_by(asc(User.created_at), asc(User.id))
    
    # データ取得（+1件で次ページ存在チェック）
    users = query.limit(limit + 1).all()
    
    # prev方向の場合は順序を元に戻す
    if direction == "prev":
        users = list(reversed(users))
    
    # 次ページ存在チェック
    has_more = len(users) > limit
    if has_more:
        users = users[:limit]
    
    # 次のカーソル生成
    next_cursor = None
    prev_cursor = None
    
    if users:
        if has_more and direction == "next":
            last_user = users[-1]
            next_cursor_data = {
                'timestamp': last_user.created_at.isoformat(),
                'id': last_user.id
            }
            next_cursor = base64.b64encode(
                json.dumps(next_cursor_data).encode('utf-8')
            ).decode('utf-8')
        
        if cursor:  # 前ページへのカーソル
            first_user = users[0]
            prev_cursor_data = {
                'timestamp': first_user.created_at.isoformat(),
                'id': first_user.id
            }
            prev_cursor = base64.b64encode(
                json.dumps(prev_cursor_data).encode('utf-8')
            ).decode('utf-8')
    
    return {
        'data': [UserResponse.from_orm(user) for user in users],
        'pagination': {
            'has_more': has_more,
            'next_cursor': next_cursor,
            'prev_cursor': prev_cursor,
            'limit': limit
        },
        'links': {
            'self': f"/users/cursor?cursor={cursor}&limit={limit}&direction={direction}" if cursor else None,
            'next': f"/users/cursor?cursor={next_cursor}&limit={limit}&direction=next" if next_cursor else None,
            'prev': f"/users/cursor?cursor={prev_cursor}&limit={limit}&direction=prev" if prev_cursor else None
        }
    }
```

**Bad: 不適切なページネーション設計**
```python
# 悪い例：不完全なページネーション

@app.get("/users")
async def get_users_bad(
    page: int = 1,  # 悪い: バリデーションなし
    size: int = 10,  # 悪い: 上限なし
    db: Session = Depends(get_db)
):
    # 悪い: 総件数を取得しない
    offset = (page - 1) * size
    users = db.query(User).offset(offset).limit(size).all()
    
    # 悪い: メタデータなし
    return {'users': users}

@app.get("/users/all")
async def get_all_users_bad(db: Session = Depends(get_db)):
    # 悪い: 全データを一度に取得（メモリリークリスク）
    users = db.query(User).all()  # 危険！
    return {'users': users, 'count': len(users)}

@app.get("/users/search")
async def search_users_bad(
    q: str,  # 悪い: バリデーションなし
    db: Session = Depends(get_db)
):
    # 悪い: SQLインジェクション脅弱性
    query = f"SELECT * FROM users WHERE username LIKE '%{q}%'"
    result = db.execute(query).fetchall()
    return {'users': result}
```

#### 2.4.2 フィルタリングと検索機能

**Good: 高度なフィルタリングシステム**
```python
# Django REST Framework - 高度なフィルタリングシステム
from django_filters import rest_framework as filters
from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response
from django.db.models import Q, Count, Avg, Max, Min
from django.contrib.postgres.search import SearchVector, SearchQuery, SearchRank
import operator
from functools import reduce

class ProductFilter(filters.FilterSet):
    """
    製品フィルタセット - 高度なフィルタリング機能
    """
    
    # 範囲フィルタ
    price_min = filters.NumberFilter(field_name='price', lookup_expr='gte')
    price_max = filters.NumberFilter(field_name='price', lookup_expr='lte')
    price_range = filters.RangeFilter(field_name='price')
    
    # 日付フィルタ
    created_after = filters.DateTimeFilter(field_name='created_at', lookup_expr='gte')
    created_before = filters.DateTimeFilter(field_name='created_at', lookup_expr='lte')
    created_date_range = filters.DateFromToRangeFilter(field_name='created_at')
    
    # 選択フィルタ
    category = filters.ModelMultipleChoiceFilter(
        field_name='category',
        queryset=Category.objects.all(),
        conjoined=False  # OR条件
    )
    
    status = filters.MultipleChoiceFilter(
        choices=Product.STATUS_CHOICES,
        field_name='status',
        conjoined=False
    )
    
    # ブールフィルタ
    is_featured = filters.BooleanFilter(field_name='is_featured')
    in_stock = filters.BooleanFilter(method='filter_in_stock')
    
    # カスタムフィルタ
    has_discount = filters.BooleanFilter(method='filter_has_discount')
    rating_min = filters.NumberFilter(method='filter_rating_min')
    
    # 全文検索（PostgreSQL専用）
    search = filters.CharFilter(method='filter_search')
    
    # 複合フィルタ
    availability = filters.ChoiceFilter(
        choices=[
            ('available', 'Available'),
            ('low_stock', 'Low Stock'),
            ('out_of_stock', 'Out of Stock'),
        ],
        method='filter_availability'
    )
    
    class Meta:
        model = Product
        fields = {
            'name': ['exact', 'icontains', 'startswith'],
            'sku': ['exact', 'icontains'],
            'brand': ['exact'],
            'tags': ['exact', 'in'],
        }
    
    def filter_in_stock(self, queryset, name, value):
        """在庫ありフィルタ"""
        if value:
            return queryset.filter(stock_quantity__gt=0)
        return queryset.filter(stock_quantity=0)
    
    def filter_has_discount(self, queryset, name, value):
        """割引有無フィルタ"""
        if value:
            return queryset.filter(
                discount_percentage__gt=0,
                discount_start_date__lte=timezone.now(),
                discount_end_date__gte=timezone.now()
            )
        return queryset.filter(
            Q(discount_percentage=0) |
            Q(discount_start_date__gt=timezone.now()) |
            Q(discount_end_date__lt=timezone.now())
        )
    
    def filter_rating_min(self, queryset, name, value):
        """最低評価フィルタ"""
        return queryset.annotate(
            avg_rating=Avg('reviews__rating')
        ).filter(avg_rating__gte=value)
    
    def filter_search(self, queryset, name, value):
        """
        PostgreSQL全文検索実装
        プロダクト名、説明、タグを検索対象とする
        """
        if not value:
            return queryset
        
        # 検索ベクター作成
        search_vector = SearchVector(
            'name', weight='A',  # 名前は最高権重
            'description', weight='B',  # 説明は中権重
            'tags__name', weight='C'  # タグは低権重
        )
        
        search_query = SearchQuery(value)
        
        return queryset.annotate(
            search=search_vector,
            rank=SearchRank(search_vector, search_query)
        ).filter(
            search=search_query
        ).order_by('-rank', '-created_at')
    
    def filter_availability(self, queryset, name, value):
        """在庫状態フィルタ"""
        if value == 'available':
            return queryset.filter(stock_quantity__gte=10)
        elif value == 'low_stock':
            return queryset.filter(stock_quantity__gt=0, stock_quantity__lt=10)
        elif value == 'out_of_stock':
            return queryset.filter(stock_quantity=0)
        return queryset

class ProductViewSet(viewsets.ModelViewSet):
    """製品API - 高度なフィルタリング実装"""
    
    queryset = Product.objects.select_related('category', 'brand').prefetch_related('tags')
    serializer_class = ProductSerializer
    filterset_class = ProductFilter
    
    # ソートオプション
    ordering_fields = [
        'name', 'price', 'created_at', 'updated_at', 'stock_quantity'
    ]
    ordering = ['-created_at']
    
    def get_queryset(self):
        """ベースクエリセットのカスタマイズ"""
        queryset = super().get_queryset()
        
        # パフォーマンス最適化
        if self.action == 'list':
            queryset = queryset.select_related('category', 'brand')
            queryset = queryset.prefetch_related('tags')
        
        # 非公開製品のシステム管理者以外非表示
        if not self.request.user.is_staff:
            queryset = queryset.filter(is_public=True)
        
        return queryset
    
    @action(detail=False, methods=['get'])
    def search_suggestions(self, request):
        """
        検索候補提案API
        オートコンプリート機能用
        """
        query = request.query_params.get('q', '').strip()
        
        if len(query) < 2:
            return Response({
                'suggestions': [],
                'message': 'Query too short. Minimum 2 characters required.'
            })
        
        # 製品名候補
        product_suggestions = Product.objects.filter(
            name__icontains=query,
            is_public=True
        ).values_list('name', flat=True).distinct()[:5]
        
        # カテゴリ候補
        category_suggestions = Category.objects.filter(
            name__icontains=query
        ).values_list('name', flat=True).distinct()[:3]
        
        # タグ候補
        tag_suggestions = Tag.objects.filter(
            name__icontains=query
        ).values_list('name', flat=True).distinct()[:3]
        
        return Response({
            'suggestions': {
                'products': list(product_suggestions),
                'categories': list(category_suggestions),
                'tags': list(tag_suggestions)
            },
            'query': query
        })
    
    @action(detail=False, methods=['get'])
    def faceted_search(self, request):
        """
        ファセット検索（絞込み検索）
        カテゴリ、価格帯、ブランド等のファセット情報を提供
        """
        # フィルタ適用後のクエリセット
        filtered_queryset = self.filter_queryset(self.get_queryset())
        
        # ファセット情報集計
        facets = {
            # カテゴリ別件数
            'categories': list(
                filtered_queryset.values('category__name', 'category__id')
                .annotate(count=Count('id'))
                .order_by('-count')
            ),
            
            # ブランド別件数
            'brands': list(
                filtered_queryset.values('brand__name', 'brand__id')
                .annotate(count=Count('id'))
                .order_by('-count')
            ),
            
            # 価格帯分布
            'price_ranges': {
                '0-1000': filtered_queryset.filter(price__lt=1000).count(),
                '1000-5000': filtered_queryset.filter(price__gte=1000, price__lt=5000).count(),
                '5000-10000': filtered_queryset.filter(price__gte=5000, price__lt=10000).count(),
                '10000+': filtered_queryset.filter(price__gte=10000).count(),
            },
            
            # 在庫状態
            'availability': {
                'in_stock': filtered_queryset.filter(stock_quantity__gt=0).count(),
                'out_of_stock': filtered_queryset.filter(stock_quantity=0).count(),
            },
            
            # 評価分布
            'ratings': {
                '4+': filtered_queryset.annotate(
                    avg_rating=Avg('reviews__rating')
                ).filter(avg_rating__gte=4).count(),
                '3+': filtered_queryset.annotate(
                    avg_rating=Avg('reviews__rating')
                ).filter(avg_rating__gte=3, avg_rating__lt=4).count(),
                '2+': filtered_queryset.annotate(
                    avg_rating=Avg('reviews__rating')
                ).filter(avg_rating__gte=2, avg_rating__lt=3).count(),
                '1+': filtered_queryset.annotate(
                    avg_rating=Avg('reviews__rating')
                ).filter(avg_rating__gte=1, avg_rating__lt=2).count(),
            }
        }
        
        # 基本統計情報
        stats = {
            'total_products': filtered_queryset.count(),
            'price_range': {
                'min': filtered_queryset.aggregate(Min('price'))['price__min'],
                'max': filtered_queryset.aggregate(Max('price'))['price__max'],
                'avg': filtered_queryset.aggregate(Avg('price'))['price__avg'],
            }
        }
        
        return Response({
            'facets': facets,
            'stats': stats,
            'applied_filters': dict(request.query_params)
        })
    
    @action(detail=False, methods=['get'])
    def advanced_search(self, request):
        """
        高度検索API - AND/ORロジック組み合わせ
        
        Query Parameters:
        - q: メイン検索キーワード
        - category_or: カテゴリのOR検索（カンマ区切り）
        - tags_and: タグのAND検索（カンマ区切り）
        - exclude_categories: 除外カテゴリ（カンマ区切り）
        """
        queryset = self.get_queryset()
        
        # メイン検索キーワード
        query = request.query_params.get('q', '').strip()
        if query:
            search_vector = SearchVector('name', 'description')
            search_query = SearchQuery(query)
            queryset = queryset.annotate(
                search=search_vector,
                rank=SearchRank(search_vector, search_query)
            ).filter(search=search_query)
        
        # カテゴリ OR検索
        category_or = request.query_params.get('category_or', '').strip()
        if category_or:
            category_ids = [int(id) for id in category_or.split(',') if id.isdigit()]
            if category_ids:
                queryset = queryset.filter(category_id__in=category_ids)
        
        # タグ AND検索
        tags_and = request.query_params.get('tags_and', '').strip()
        if tags_and:
            tag_names = [name.strip() for name in tags_and.split(',')]
            for tag_name in tag_names:
                queryset = queryset.filter(tags__name__iexact=tag_name)
        
        # 除外カテゴリ
        exclude_categories = request.query_params.get('exclude_categories', '').strip()
        if exclude_categories:
            exclude_ids = [int(id) for id in exclude_categories.split(',') if id.isdigit()]
            if exclude_ids:
                queryset = queryset.exclude(category_id__in=exclude_ids)
        
        # ソート適用
        if query:  # 検索時は関連度順
            queryset = queryset.order_by('-rank', '-created_at')
        else:
            queryset = queryset.order_by('-created_at')
        
        # ページネーション適用
        page = self.paginate_queryset(queryset)
        if page is not None:
            serializer = self.get_serializer(page, many=True)
            return self.get_paginated_response(serializer.data)
        
        serializer = self.get_serializer(queryset, many=True)
        return Response(serializer.data)
```

**Bad: 不適切なフィルタリング実装**
```python
# 悪い例：フィルタリングの悪い実装

@api_view(['GET'])
def search_products_bad(request):
    # 悪い: SQLインジェクション脅弱性
    search_term = request.GET.get('search', '')
    query = f"SELECT * FROM products WHERE name LIKE '%{search_term}%'"
    
    # 悪い: バリデーションなし
    products = Product.objects.extra(where=[query])
    
    return Response({'products': list(products.values())})

@api_view(['GET'])
def filter_products_bad(request):
    # 悪い: 非効率なクエリ
    all_products = Product.objects.all()  # 全データ取得
    
    # 悪い: Pythonレベルでフィルタリング
    min_price = request.GET.get('min_price')
    if min_price:
        all_products = [p for p in all_products if p.price >= float(min_price)]
    
    max_price = request.GET.get('max_price')
    if max_price:
        all_products = [p for p in all_products if p.price <= float(max_price)]
    
    return Response({'products': all_products})

@api_view(['GET'])
def get_products_no_pagination(request):
    # 悪い: ページネーションなしで全データ
    products = Product.objects.all()  # メモリリークリスク
    return Response({'products': ProductSerializer(products, many=True).data})
```

