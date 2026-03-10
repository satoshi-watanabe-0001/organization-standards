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

