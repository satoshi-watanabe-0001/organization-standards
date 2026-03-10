# RESTful リソース設計

**このドキュメントについて**: API設計標準 - RESTful リソース設計

---

## 2. RESTful API設計標準

### 2.1 リソース設計原則

#### 2.1.1 リソース命名規則

**Good: 適切なリソース命名**
```python
# Django REST Framework - 適切なリソース設計
from rest_framework import viewsets, serializers
from rest_framework.decorators import action
from rest_framework.response import Response
from django.db import models

# モデル定義
class User(models.Model):
    username = models.CharField(max_length=150, unique=True)
    email = models.EmailField(unique=True)
    created_at = models.DateTimeField(auto_now_add=True)
    is_active = models.BooleanField(default=True)
    
    class Meta:
        db_table = 'users'
        ordering = ['-created_at']

class Post(models.Model):
    title = models.CharField(max_length=200)
    content = models.TextField()
    author = models.ForeignKey(User, on_delete=models.CASCADE, related_name='posts')
    tags = models.ManyToManyField('Tag', blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    
    class Meta:
        db_table = 'posts'
        ordering = ['-created_at']

class Comment(models.Model):
    content = models.TextField()
    post = models.ForeignKey(Post, on_delete=models.CASCADE, related_name='comments')
    author = models.ForeignKey(User, on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now_add=True)
    
    class Meta:
        db_table = 'comments'
        ordering = ['-created_at']

# シリアライザー
class UserSerializer(serializers.ModelSerializer):
    posts_count = serializers.SerializerMethodField()
    
    class Meta:
        model = User
        fields = ['id', 'username', 'email', 'created_at', 'is_active', 'posts_count']
        read_only_fields = ['id', 'created_at']
    
    def get_posts_count(self, obj):
        return obj.posts.count()

class CommentSerializer(serializers.ModelSerializer):
    author_username = serializers.CharField(source='author.username', read_only=True)
    
    class Meta:
        model = Comment
        fields = ['id', 'content', 'author', 'author_username', 'created_at']
        read_only_fields = ['id', 'created_at', 'author_username']

class PostSerializer(serializers.ModelSerializer):
    author_username = serializers.CharField(source='author.username', read_only=True)
    comments_count = serializers.SerializerMethodField()
    tags = serializers.StringRelatedField(many=True, read_only=True)
    
    class Meta:
        model = Post
        fields = ['id', 'title', 'content', 'author', 'author_username', 
                 'tags', 'comments_count', 'created_at']
        read_only_fields = ['id', 'created_at', 'author_username']
    
    def get_comments_count(self, obj):
        return obj.comments.count()

# ViewSet - RESTful設計パターン
class UserViewSet(viewsets.ModelViewSet):
    """
    標準的なCRUD操作を提供するユーザーViewSet
    
    リソースURL設計:
    - GET /api/users/ - ユーザー一覧
    - POST /api/users/ - ユーザー作成
    - GET /api/users/{id}/ - ユーザー詳細
    - PUT /api/users/{id}/ - ユーザー更新（全体）
    - PATCH /api/users/{id}/ - ユーザー更新（部分）
    - DELETE /api/users/{id}/ - ユーザー削除
    """
    queryset = User.objects.all()
    serializer_class = UserSerializer
    lookup_field = 'id'
    
    @action(detail=True, methods=['get'])
    def posts(self, request, id=None):
        """ユーザーの投稿一覧取得"""
        user = self.get_object()
        posts = user.posts.all()
        serializer = PostSerializer(posts, many=True)
        return Response({
            'user_id': user.id,
            'posts': serializer.data,
            'total_count': posts.count()
        })
    
    @action(detail=True, methods=['post'])
    def activate(self, request, id=None):
        """ユーザーアクティベーション"""
        user = self.get_object()
        user.is_active = True
        user.save()
        return Response({'message': 'User activated successfully'})
    
    @action(detail=True, methods=['post'])
    def deactivate(self, request, id=None):
        """ユーザー非アクティベーション"""
        user = self.get_object()
        user.is_active = False
        user.save()
        return Response({'message': 'User deactivated successfully'})

# URL設計（urls.py）
from django.urls import path, include
from rest_framework.routers import DefaultRouter

router = DefaultRouter()
router.register(r'users', UserViewSet, basename='user')
router.register(r'posts', PostViewSet, basename='post')
router.register(r'comments', CommentViewSet, basename='comment')

urlpatterns = [
    path('api/', include(router.urls)),
    # ネストしたリソース
    path('api/users/<int:user_id>/posts/', UserPostsView.as_view(), name='user-posts'),
    path('api/posts/<int:post_id>/comments/', PostCommentsView.as_view(), name='post-comments'),
]
```

**Bad: 不適切なリソース命名**
```python
# 悪い例：動詞ベースのURL設計
@api_view(['GET'])
def get_user_list(request):  # 悪い: 動詞を含む
    pass

@api_view(['POST'])
def create_new_user(request):  # 悪い: 動詞を含む
    pass

@api_view(['GET'])
def show_user_posts(request, user_id):  # 悪い: 動詞を含む
    pass

# 悪いURL設計
urlpatterns = [
    path('api/getUserList/', get_user_list),  # 悪い: camelCase + 動詞
    path('api/createUser/', create_new_user),  # 悪い: 動詞ベース
    path('api/showUserPosts/<int:user_id>/', show_user_posts),  # 悪い: 動詞ベース
    path('api/user_data/', user_data_view),  # 悪い: 不明確な命名
]
```

#### 2.1.2 ネストしたリソース設計

**Good: 適切なリソース階層**
```python
# FastAPI - ネストしたリソース設計
from fastapi import FastAPI, Depends, HTTPException, Query
from sqlalchemy.orm import Session
from typing import List, Optional
import uuid

app = FastAPI(title="Blog API", version="1.0.0")

# 階層リソース設計
@app.get("/users/{user_id}/posts", response_model=List[PostResponse])
async def get_user_posts(
    user_id: uuid.UUID,
    skip: int = Query(0, ge=0),
    limit: int = Query(10, ge=1, le=100),
    published_only: bool = Query(True),
    db: Session = Depends(get_db)
):
    """
    特定ユーザーの投稿一覧取得
    
    - **user_id**: ユーザーUUID
    - **skip**: スキップ件数（ページネーション）
    - **limit**: 取得件数上限
    - **published_only**: 公開投稿のみフィルタ
    """
    query = db.query(Post).filter(Post.author_id == user_id)
    
    if published_only:
        query = query.filter(Post.is_published == True)
    
    posts = query.offset(skip).limit(limit).all()
    
    if not posts and skip == 0:
        # ユーザーが存在するかチェック
        user = db.query(User).filter(User.id == user_id).first()
        if not user:
            raise HTTPException(status_code=404, detail="User not found")
    
    return posts

@app.post("/users/{user_id}/posts", response_model=PostResponse, status_code=201)
async def create_user_post(
    user_id: uuid.UUID,
    post: PostCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """
    特定ユーザーの新規投稿作成
    
    権限チェック：自分の投稿のみ作成可能
    """
    # 権限チェック
    if current_user.id != user_id:
        raise HTTPException(
            status_code=403, 
            detail="Can only create posts for yourself"
        )
    
    # ユーザー存在チェック
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    db_post = Post(**post.dict(), author_id=user_id)
    db.add(db_post)
    db.commit()
    db.refresh(db_post)
    
    return db_post

@app.get("/posts/{post_id}/comments", response_model=List[CommentResponse])
async def get_post_comments(
    post_id: uuid.UUID,
    skip: int = Query(0, ge=0),
    limit: int = Query(20, ge=1, le=100),
    sort_order: str = Query("desc", regex="^(asc|desc)$"),
    db: Session = Depends(get_db)
):
    """
    特定投稿のコメント一覧取得
    
    - **post_id**: 投稿UUID
    - **sort_order**: ソート順序（asc/desc）
    """
    # 投稿存在チェック
    post = db.query(Post).filter(Post.id == post_id).first()
    if not post:
        raise HTTPException(status_code=404, detail="Post not found")
    
    query = db.query(Comment).filter(Comment.post_id == post_id)
    
    if sort_order == "asc":
        query = query.order_by(Comment.created_at.asc())
    else:
        query = query.order_by(Comment.created_at.desc())
    
    comments = query.offset(skip).limit(limit).all()
    return comments

@app.post("/posts/{post_id}/comments", response_model=CommentResponse, status_code=201)
async def create_post_comment(
    post_id: uuid.UUID,
    comment: CommentCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """
    投稿にコメント追加
    """
    # 投稿存在チェック
    post = db.query(Post).filter(Post.id == post_id).first()
    if not post:
        raise HTTPException(status_code=404, detail="Post not found")
    
    # 投稿が公開されているかチェック
    if not post.is_published:
        raise HTTPException(status_code=403, detail="Cannot comment on unpublished post")
    
    db_comment = Comment(
        content=comment.content,
        post_id=post_id,
        author_id=current_user.id
    )
    db.add(db_comment)
    db.commit()
    db.refresh(db_comment)
    
    return db_comment

# より深いネスト（3階層）
@app.get("/users/{user_id}/posts/{post_id}/comments", response_model=List[CommentResponse])
async def get_user_post_comments(
    user_id: uuid.UUID,
    post_id: uuid.UUID,
    db: Session = Depends(get_db)
):
    """
    特定ユーザーの特定投稿のコメント取得
    
    3階層ネスト例
    """
    # ユーザーと投稿の関連性チェック
    post = db.query(Post).filter(
        Post.id == post_id,
        Post.author_id == user_id
    ).first()
    
    if not post:
        raise HTTPException(
            status_code=404, 
            detail="Post not found or does not belong to the user"
        )
    
    comments = db.query(Comment).filter(Comment.post_id == post_id).all()
    return comments
```

**Bad: 不適切なネスト設計**
```python
# 悪い例：過度なネスト
@app.get("/users/{user_id}/posts/{post_id}/comments/{comment_id}/replies/{reply_id}/likes")
async def get_reply_likes():  # 悪い: 6階層は深すぎる
    pass

# 悪い例：不一貫なリソース階層
@app.get("/posts/{post_id}/author")  # 悪い: /users/{user_id}の方が適切
async def get_post_author():
    pass

@app.get("/comments/{comment_id}/post/author")  # 悪い: 複雑すぎる階層
async def get_comment_post_author():
    pass
```

