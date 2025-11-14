# RESTful ステータスコード

**このドキュメントについて**: API設計標準 - RESTful ステータスコード

---

### 2.3 ステータスコード標準使用

#### 2.3.1 適切なステータスコード選択

**Good: 適切なステータスコード使用**
```python
# Django REST Framework - 詳細なステータスコード実装
from rest_framework import status, viewsets
from rest_framework.response import Response
from rest_framework.decorators import action
from django.shortcuts import get_object_or_404
from django.db import IntegrityError, transaction
import logging

logger = logging.getLogger(__name__)

class ProductViewSet(viewsets.ModelViewSet):
    """製品管理API - 詳細なステータスコード実装例"""
    
    def create(self, request):
        """
        製品作成 - 詳細なステータスコード使い分け
        """
        serializer = self.get_serializer(data=request.data)
        
        # バリデーションエラー: 400 Bad Request
        if not serializer.is_valid():
            return Response({
                'error': 'Validation failed',
                'details': serializer.errors,
                'error_code': 'VALIDATION_ERROR'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        try:
            with transaction.atomic():
                product = serializer.save()
                
                # 関連データの作成
                self._create_product_metadata(product, request.data)
                
                logger.info(f"Product created: {product.id}")
                
                # 成功: 201 Created
                return Response({
                    'id': product.id,
                    'message': 'Product created successfully',
                    'data': serializer.data
                }, status=status.HTTP_201_CREATED)
                
        except IntegrityError as e:
            # 制約違反: 409 Conflict
            logger.warning(f"Product creation conflict: {str(e)}")
            return Response({
                'error': 'Product already exists',
                'details': 'Product with this name or SKU already exists',
                'error_code': 'DUPLICATE_PRODUCT'
            }, status=status.HTTP_409_CONFLICT)
            
        except Exception as e:
            # サーバーエラー: 500 Internal Server Error
            logger.error(f"Product creation failed: {str(e)}")
            return Response({
                'error': 'Internal server error',
                'details': 'Failed to create product',
                'error_code': 'INTERNAL_ERROR'
            }, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
    
    def retrieve(self, request, pk=None):
        """
        製品詳細取得 - 条件付きレスポンス
        """
        try:
            product = self.get_object()
            
            # 条件付きリクエスト処理
            if_none_match = request.META.get('HTTP_IF_NONE_MATCH')
            current_etag = f'"{product.updated_at.timestamp()}"'
            
            # ETag一致: 304 Not Modified
            if if_none_match == current_etag:
                return Response(status=status.HTTP_304_NOT_MODIFIED)
            
            # 製品が非公開の場合の処理
            if not product.is_public and not request.user.is_staff:
                # 認証済みだが権限なし: 403 Forbidden
                if request.user.is_authenticated:
                    return Response({
                        'error': 'Access denied',
                        'details': 'This product is not publicly available',
                        'error_code': 'ACCESS_DENIED'
                    }, status=status.HTTP_403_FORBIDDEN)
                
                # 未認証: 401 Unauthorized
                else:
                    return Response({
                        'error': 'Authentication required',
                        'details': 'Authentication required to access this resource',
                        'error_code': 'AUTHENTICATION_REQUIRED'
                    }, status=status.HTTP_401_UNAUTHORIZED)
            
            serializer = self.get_serializer(product)
            response = Response(serializer.data, status=status.HTTP_200_OK)
            
            # キャッシュヘッダー設定
            response['ETag'] = current_etag
            response['Cache-Control'] = 'max-age=3600'
            
            return response
            
        except Http404:
            # リソース未発見: 404 Not Found
            return Response({
                'error': 'Product not found',
                'details': f'Product with ID {pk} does not exist',
                'error_code': 'RESOURCE_NOT_FOUND'
            }, status=status.HTTP_404_NOT_FOUND)
    
    def update(self, request, pk=None):
        """
        製品更新 - 楽観的ロック実装
        """
        product = self.get_object()
        
        # 楽観的ロックチェック
        if_match = request.META.get('HTTP_IF_MATCH')
        current_etag = f'"{product.updated_at.timestamp()}"'
        
        # ETag不一致: 412 Precondition Failed
        if if_match and if_match != current_etag:
            return Response({
                'error': 'Precondition failed',
                'details': 'Resource has been modified by another user',
                'error_code': 'PRECONDITION_FAILED',
                'current_etag': current_etag
            }, status=status.HTTP_412_PRECONDITION_FAILED)
        
        serializer = self.get_serializer(product, data=request.data)
        
        if not serializer.is_valid():
            return Response({
                'error': 'Validation failed',
                'details': serializer.errors,
                'error_code': 'VALIDATION_ERROR'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        try:
            updated_product = serializer.save()
            
            # 成功: 200 OK
            response = Response({
                'message': 'Product updated successfully',
                'data': serializer.data
            }, status=status.HTTP_200_OK)
            
            # 新しいETag設定
            response['ETag'] = f'"{updated_product.updated_at.timestamp()}"'
            
            return response
            
        except IntegrityError:
            return Response({
                'error': 'Update conflict',
                'details': 'Another product with the same name or SKU exists',
                'error_code': 'DUPLICATE_PRODUCT'
            }, status=status.HTTP_409_CONFLICT)
    
    def partial_update(self, request, pk=None):
        """
        部分更新 - PATCH実装
        """
        product = self.get_object()
        serializer = self.get_serializer(product, data=request.data, partial=True)
        
        if not serializer.is_valid():
            return Response({
                'error': 'Validation failed',
                'details': serializer.errors,
                'error_code': 'VALIDATION_ERROR'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        # 更新内容が空の場合
        if not request.data:
            return Response({
                'error': 'No update data provided',
                'details': 'Request body cannot be empty for partial update',
                'error_code': 'EMPTY_REQUEST_BODY'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        updated_product = serializer.save()
        
        # 部分更新成功: 200 OK
        return Response({
            'message': 'Product partially updated successfully',
            'updated_fields': list(request.data.keys()),
            'data': serializer.data
        }, status=status.HTTP_200_OK)
    
    def destroy(self, request, pk=None):
        """
        製品削除 - ソフト削除実装
        """
        product = self.get_object()
        
        # 削除可能性チェック
        if hasattr(product, 'orders') and product.orders.exists():
            # 依存関係あり: 409 Conflict
            return Response({
                'error': 'Cannot delete product',
                'details': 'Product has associated orders and cannot be deleted',
                'error_code': 'DEPENDENCY_CONFLICT'
            }, status=status.HTTP_409_CONFLICT)
        
        # ソフト削除実行
        product.is_deleted = True
        product.deleted_at = timezone.now()
        product.save()
        
        logger.info(f"Product soft deleted: {product.id}")
        
        # 削除成功: 204 No Content
        return Response(status=status.HTTP_204_NO_CONTENT)
    
    @action(detail=True, methods=['post'])
    def restore(self, request, pk=None):
        """
        削除済み製品の復元
        """
        try:
            # 削除済み製品を含めて検索
            product = Product.objects_with_deleted.get(pk=pk)
            
            if not product.is_deleted:
                # すでに有効: 409 Conflict
                return Response({
                    'error': 'Product is not deleted',
                    'details': 'Product is already active and cannot be restored',
                    'error_code': 'ALREADY_ACTIVE'
                }, status=status.HTTP_409_CONFLICT)
            
            # 復元実行
            product.is_deleted = False
            product.deleted_at = None
            product.save()
            
            # 復元成功: 200 OK
            return Response({
                'message': 'Product restored successfully',
                'data': ProductSerializer(product).data
            }, status=status.HTTP_200_OK)
            
        except Product.DoesNotExist:
            return Response({
                'error': 'Product not found',
                'details': f'Product with ID {pk} does not exist',
                'error_code': 'RESOURCE_NOT_FOUND'
            }, status=status.HTTP_404_NOT_FOUND)
    
    @action(detail=False, methods=['post'])
    def bulk_create(self, request):
        """
        一括作成 - 207 Multi-Status実装
        """
        if not isinstance(request.data, list):
            return Response({
                'error': 'Invalid request format',
                'details': 'Request body must be an array of product objects',
                'error_code': 'INVALID_FORMAT'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        results = []
        created_count = 0
        error_count = 0
        
        for index, product_data in enumerate(request.data):
            try:
                serializer = ProductSerializer(data=product_data)
                
                if serializer.is_valid():
                    product = serializer.save()
                    results.append({
                        'index': index,
                        'status': 201,
                        'message': 'Created successfully',
                        'data': serializer.data
                    })
                    created_count += 1
                
                else:
                    results.append({
                        'index': index,
                        'status': 400,
                        'message': 'Validation failed',
                        'errors': serializer.errors
                    })
                    error_count += 1
                    
            except IntegrityError:
                results.append({
                    'index': index,
                    'status': 409,
                    'message': 'Product already exists',
                    'errors': {'duplicate': 'Product with this name or SKU already exists'}
                })
                error_count += 1
                
            except Exception as e:
                results.append({
                    'index': index,
                    'status': 500,
                    'message': 'Internal server error',
                    'errors': {'internal': str(e)}
                })
                error_count += 1
        
        # 全て成功: 201 Created
        if error_count == 0:
            return Response({
                'message': f'All {created_count} products created successfully',
                'results': results
            }, status=status.HTTP_201_CREATED)
        
        # 全て失敗: 400 Bad Request
        elif created_count == 0:
            return Response({
                'message': 'All products failed to create',
                'results': results
            }, status=status.HTTP_400_BAD_REQUEST)
        
        # 一部成功: 207 Multi-Status
        else:
            return Response({
                'message': f'{created_count} products created, {error_count} failed',
                'summary': {
                    'created': created_count,
                    'failed': error_count,
                    'total': len(request.data)
                },
                'results': results
            }, status=status.HTTP_207_MULTI_STATUS)

# カスタムエラーハンドラー
@api_view(['GET', 'POST', 'PUT', 'PATCH', 'DELETE'])
def rate_limited_view(request):
    """
    レート制限実装例
    """
    from django_ratelimit.decorators import ratelimit
    from django_ratelimit.exceptions import Ratelimited
    
    try:
        # レート制限チェック
        if getattr(request, 'limited', False):
            # レート制限超過: 429 Too Many Requests
            return Response({
                'error': 'Rate limit exceeded',
                'details': 'Too many requests. Please try again later.',
                'error_code': 'RATE_LIMIT_EXCEEDED',
                'retry_after': 60  # 60秒後に再試行
            }, status=status.HTTP_429_TOO_MANY_REQUESTS)
        
        # 通常処理
        return Response({'message': 'Success'}, status=status.HTTP_200_OK)
        
    except Ratelimited:
        return Response({
            'error': 'Rate limit exceeded',
            'details': 'Too many requests. Please try again later.',
            'error_code': 'RATE_LIMIT_EXCEEDED'
        }, status=status.HTTP_429_TOO_MANY_REQUESTS)
```

**Bad: 不適切なステータスコード使用**
```python
# 悪い例：ステータスコードの誤用

@api_view(['POST'])
def create_user_bad(request):
    serializer = UserSerializer(data=request.data)
    
    if not serializer.is_valid():
        # 悪い: バリデーションエラーで500を返す
        return Response({
            'error': 'Something went wrong'
        }, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
    
    try:
        user = serializer.save()
        # 悪い: 作成成功で200を返す（201が適切）
        return Response(serializer.data, status=status.HTTP_200_OK)
    
    except IntegrityError:
        # 悪い: 重複エラーで400を返す（409が適切）
        return Response({
            'error': 'Bad request'
        }, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET'])
def get_user_bad(request, user_id):
    try:
        user = User.objects.get(id=user_id)
        # 悪い: 正常取得で201を返す（200が適切）
        return Response(UserSerializer(user).data, status=status.HTTP_201_CREATED)
    
    except User.DoesNotExist:
        # 悪い: 未発見で500を返す（404が適切）
        return Response({
            'error': 'Server error'
        }, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

@api_view(['DELETE'])
def delete_user_bad(request, user_id):
    try:
        user = User.objects.get(id=user_id)
        user.delete()
        
        # 悪い: 削除成功でデータを返す（204 No Contentが適切）
        return Response({
            'message': 'User deleted',
            'deleted_user': UserSerializer(user).data
        }, status=status.HTTP_200_OK)
    
    except User.DoesNotExist:
        # 悪い: 削除対象なしで200を返す（404が適切）
        return Response({
            'message': 'User not found but OK'
        }, status=status.HTTP_200_OK)
```

