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

