# 監視・ログ標準 / Monitoring and Logging Standards

## バージョン情報 / Version Information
- **最終更新日 / Last Updated**: 2025-10-24
- **バージョン / Version**: 1.0
- **対象 / Target**: すべてのプロジェクト / All Projects
- **適用範囲 / Scope**: 任意 / Optional (Tier 3)

---

## 目的 / Purpose

このドキュメントは、システムの可観測性（Observability）を実現するための監視とログの標準を定義します。構造化ログ、メトリクス収集、分散トレーシング、アラート設定、SLI/SLO/SLAの定義など、本番運用に必要な可観測性の実装ガイドラインを提供します。

This document defines monitoring and logging standards for achieving system observability, including structured logging, metrics collection, distributed tracing, alerting, and SLI/SLO/SLA definitions.

---

## 1. 可観測性の3本柱 / Three Pillars of Observability

### 1.1 ログ（Logs）
- **目的**: イベントの詳細な記録
- **特徴**: タイムスタンプ付きの離散的なイベント
- **用途**: デバッグ、監査、トラブルシューティング

### 1.2 メトリクス（Metrics）
- **目的**: システムの状態を数値で表現
- **特徴**: 時系列データ、集計可能
- **用途**: パフォーマンス監視、容量計画、アラート

### 1.3 トレース（Traces）
- **目的**: リクエストの経路を追跡
- **特徴**: 分散システム全体の可視化
- **用途**: パフォーマンス分析、ボトルネック特定

---

## 2. 構造化ログ標準 / Structured Logging Standards

### 2.1 ログレベル / Log Levels

```python
# ✅ 良い例: 適切なログレベルの使用
import logging

# ログレベルの定義
# CRITICAL (50): システムが動作不能
# ERROR (40): エラーだが動作は継続
# WARNING (30): 警告、将来的な問題の可能性
# INFO (20): 一般的な情報
# DEBUG (10): デバッグ情報

logger = logging.getLogger(__name__)

def process_payment(amount: float, user_id: str):
    logger.info(f"Processing payment", extra={
        "amount": amount,
        "user_id": user_id,
        "event": "payment_started"
    })
    
    try:
        result = payment_gateway.charge(amount, user_id)
        
        logger.info(f"Payment processed successfully", extra={
            "amount": amount,
            "user_id": user_id,
            "transaction_id": result.id,
            "event": "payment_completed"
        })
        
        return result
        
    except PaymentError as e:
        logger.error(f"Payment failed", extra={
            "amount": amount,
            "user_id": user_id,
            "error": str(e),
            "event": "payment_failed"
        }, exc_info=True)
        raise
    except Exception as e:
        logger.critical(f"Unexpected error in payment processing", extra={
            "amount": amount,
            "user_id": user_id,
            "error": str(e),
            "event": "payment_critical_error"
        }, exc_info=True)
        raise

# ❌ 悪い例: 不適切なログレベル
def process_payment_bad(amount, user_id):
    logger.debug(f"Payment: {amount} for {user_id}")  # 重要な情報なのにDEBUG
    result = payment_gateway.charge(amount, user_id)
    logger.error(f"Payment done")  # 正常なのにERROR - NG
    return result
```

### 2.2 構造化ログフォーマット / Structured Log Format

#### JSON形式（推奨）
```python
# ✅ 良い例: JSON構造化ログ
import json
import logging
from datetime import datetime

class JSONFormatter(logging.Formatter):
    def format(self, record):
        log_data = {
            "timestamp": datetime.utcnow().isoformat() + "Z",
            "level": record.levelname,
            "logger": record.name,
            "message": record.getMessage(),
            "module": record.module,
            "function": record.funcName,
            "line": record.lineno,
        }
        
        # extra情報を追加
        if hasattr(record, 'user_id'):
            log_data['user_id'] = record.user_id
        if hasattr(record, 'request_id'):
            log_data['request_id'] = record.request_id
        if hasattr(record, 'event'):
            log_data['event'] = record.event
        
        # 例外情報
        if record.exc_info:
            log_data['exception'] = self.formatException(record.exc_info)
        
        return json.dumps(log_data)

# 使用例
handler = logging.StreamHandler()
handler.setFormatter(JSONFormatter())
logger = logging.getLogger()
logger.addHandler(handler)
logger.setLevel(logging.INFO)

# 出力例:
# {
#   "timestamp": "2025-10-24T10:30:00.123456Z",
#   "level": "INFO",
#   "logger": "payment_service",
#   "message": "Payment processed successfully",
#   "user_id": "user_12345",
#   "request_id": "req_abc123",
#   "event": "payment_completed",
#   "amount": 100.00,
#   "transaction_id": "txn_xyz789"
# }
```

#### 標準フィールド定義
```python
# ✅ 良い例: 標準フィールドの定義
STANDARD_LOG_FIELDS = {
    "timestamp": "ISO 8601形式のタイムスタンプ（UTC）",
    "level": "ログレベル（CRITICAL/ERROR/WARNING/INFO/DEBUG）",
    "logger": "ロガー名",
    "message": "ログメッセージ",
    "service": "サービス名",
    "version": "アプリケーションバージョン",
    "environment": "環境（production/staging/development）",
    "host": "ホスト名またはコンテナID",
    "request_id": "リクエストID（トレーシング用）",
    "user_id": "ユーザーID（該当する場合）",
    "event": "イベント名（カテゴリ化用）",
    "duration_ms": "処理時間（ミリ秒）",
    "status_code": "HTTPステータスコード",
    "error_code": "エラーコード",
    "stack_trace": "スタックトレース"
}
```

### 2.3 ログコンテキストの伝播 / Log Context Propagation

```python
# ✅ 良い例: コンテキスト情報の伝播
import contextvars
import uuid
from typing import Optional

# コンテキスト変数の定義
request_id_var = contextvars.ContextVar('request_id', default=None)
user_id_var = contextvars.ContextVar('user_id', default=None)

class ContextFilter(logging.Filter):
    """ログにコンテキスト情報を追加するフィルター"""
    def filter(self, record):
        record.request_id = request_id_var.get()
        record.user_id = user_id_var.get()
        return True

# ミドルウェアでリクエストIDを設定
def request_middleware(request, call_next):
    request_id = request.headers.get('X-Request-ID', str(uuid.uuid4()))
    request_id_var.set(request_id)
    
    if hasattr(request, 'user'):
        user_id_var.set(request.user.id)
    
    response = call_next(request)
    response.headers['X-Request-ID'] = request_id
    return response

# ロガーにフィルターを追加
logger = logging.getLogger()
logger.addFilter(ContextFilter())

# どこからログ出力してもrequest_idとuser_idが含まれる
def some_function():
    logger.info("Processing data")  # request_id, user_idが自動付与
```

---

## 3. メトリクス収集 / Metrics Collection

### 3.1 メトリクスの種類 / Metrics Types

#### Counter（カウンター）
```python
# ✅ 良い例: Prometheusカウンター
from prometheus_client import Counter

# カウンターの定義
http_requests_total = Counter(
    'http_requests_total',
    'Total HTTP requests',
    ['method', 'endpoint', 'status']
)

# カウンターのインクリメント
def handle_request(method: str, endpoint: str):
    try:
        response = process_request(method, endpoint)
        http_requests_total.labels(
            method=method,
            endpoint=endpoint,
            status=response.status_code
        ).inc()
        return response
    except Exception as e:
        http_requests_total.labels(
            method=method,
            endpoint=endpoint,
            status=500
        ).inc()
        raise
```

#### Gauge（ゲージ）
```python
# ✅ 良い例: Prometheusゲージ
from prometheus_client import Gauge

# ゲージの定義
active_connections = Gauge(
    'active_connections',
    'Number of active connections',
    ['service']
)

# ゲージの更新
def on_connection_opened(service: str):
    active_connections.labels(service=service).inc()

def on_connection_closed(service: str):
    active_connections.labels(service=service).dec()

# 現在の値を直接設定
def update_queue_size(queue_name: str, size: int):
    queue_size = Gauge('queue_size', 'Queue size', ['queue'])
    queue_size.labels(queue=queue_name).set(size)
```

#### Histogram（ヒストグラム）
```python
# ✅ 良い例: Prometheusヒストグラム
from prometheus_client import Histogram
import time

# ヒストグラムの定義（レスポンスタイム測定）
request_duration_seconds = Histogram(
    'request_duration_seconds',
    'Request duration in seconds',
    ['method', 'endpoint'],
    buckets=[0.01, 0.05, 0.1, 0.5, 1.0, 2.5, 5.0, 10.0]
)

# デコレーターで自動測定
def measure_duration(method: str, endpoint: str):
    def decorator(func):
        def wrapper(*args, **kwargs):
            start_time = time.time()
            try:
                return func(*args, **kwargs)
            finally:
                duration = time.time() - start_time
                request_duration_seconds.labels(
                    method=method,
                    endpoint=endpoint
                ).observe(duration)
        return wrapper
    return decorator

# 使用例
@measure_duration('GET', '/api/users')
def get_users():
    return fetch_users_from_db()
```

#### Summary（サマリー）
```python
# ✅ 良い例: Prometheusサマリー
from prometheus_client import Summary

# サマリーの定義
response_size_bytes = Summary(
    'response_size_bytes',
    'Response size in bytes',
    ['endpoint']
)

# サイズの記録
def send_response(endpoint: str, data: bytes):
    response_size_bytes.labels(endpoint=endpoint).observe(len(data))
    return data
```

### 3.2 ゴールデンシグナル / Golden Signals

```python
# ✅ 良い例: 4つのゴールデンシグナル
from prometheus_client import Counter, Histogram, Gauge

# 1. Latency（レイテンシ）- リクエスト処理時間
request_latency = Histogram(
    'request_latency_seconds',
    'Request latency in seconds',
    ['service', 'endpoint']
)

# 2. Traffic（トラフィック）- リクエスト数
request_count = Counter(
    'request_count_total',
    'Total request count',
    ['service', 'endpoint', 'method']
)

# 3. Errors（エラー）- エラー率
error_count = Counter(
    'error_count_total',
    'Total error count',
    ['service', 'endpoint', 'error_type']
)

# 4. Saturation（飽和度）- リソース使用率
cpu_usage_percent = Gauge(
    'cpu_usage_percent',
    'CPU usage percentage',
    ['service', 'host']
)

memory_usage_percent = Gauge(
    'memory_usage_percent',
    'Memory usage percentage',
    ['service', 'host']
)

# 使用例
class MonitoringMiddleware:
    def __init__(self, service_name: str):
        self.service_name = service_name
    
    def process_request(self, request):
        start_time = time.time()
        
        # トラフィックカウント
        request_count.labels(
            service=self.service_name,
            endpoint=request.path,
            method=request.method
        ).inc()
        
        try:
            response = self.handle(request)
            
            # レイテンシ記録
            duration = time.time() - start_time
            request_latency.labels(
                service=self.service_name,
                endpoint=request.path
            ).observe(duration)
            
            return response
            
        except Exception as e:
            # エラーカウント
            error_count.labels(
                service=self.service_name,
                endpoint=request.path,
                error_type=type(e).__name__
            ).inc()
            raise
```

### 3.3 カスタムビジネスメトリクス / Custom Business Metrics

```python
# ✅ 良い例: ビジネスメトリクスの定義
from prometheus_client import Counter, Gauge, Histogram

# ビジネスKPIのメトリクス
orders_total = Counter(
    'orders_total',
    'Total number of orders',
    ['status', 'product_category']
)

revenue_total = Counter(
    'revenue_total_usd',
    'Total revenue in USD',
    ['product_category']
)

cart_abandonment = Counter(
    'cart_abandonment_total',
    'Total cart abandonments',
    ['stage']
)

average_order_value = Gauge(
    'average_order_value_usd',
    'Average order value in USD'
)

checkout_duration = Histogram(
    'checkout_duration_seconds',
    'Checkout process duration',
    buckets=[1, 5, 10, 30, 60, 120, 300]
)

# 使用例
def complete_order(order):
    orders_total.labels(
        status='completed',
        product_category=order.category
    ).inc()
    
    revenue_total.labels(
        product_category=order.category
    ).inc(order.total_amount)
    
    # 平均注文額の更新
    update_average_order_value()
```

---

## 4. 分散トレーシング / Distributed Tracing

### 4.1 OpenTelemetry標準

```python
# ✅ 良い例: OpenTelemetryの実装
from opentelemetry import trace
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor
from opentelemetry.exporter.jaeger.thrift import JaegerExporter
from opentelemetry.instrumentation.requests import RequestsInstrumentor

# トレーサーのセットアップ
trace.set_tracer_provider(TracerProvider())
tracer = trace.get_tracer(__name__)

# Jaegerエクスポーターの設定
jaeger_exporter = JaegerExporter(
    agent_host_name="localhost",
    agent_port=6831,
)

# スパンプロセッサーの追加
span_processor = BatchSpanProcessor(jaeger_exporter)
trace.get_tracer_provider().add_span_processor(span_processor)

# 自動計装
RequestsInstrumentor().instrument()

# 手動スパンの作成
def process_order(order_id: str):
    with tracer.start_as_current_span("process_order") as span:
        span.set_attribute("order_id", order_id)
        span.set_attribute("service.name", "order-service")
        
        # ステップ1: 在庫チェック
        with tracer.start_as_current_span("check_inventory") as inventory_span:
            inventory_span.set_attribute("order_id", order_id)
            available = check_inventory(order_id)
            inventory_span.set_attribute("inventory.available", available)
        
        # ステップ2: 決済処理
        with tracer.start_as_current_span("process_payment") as payment_span:
            payment_span.set_attribute("order_id", order_id)
            try:
                payment_result = process_payment(order_id)
                payment_span.set_attribute("payment.status", "success")
            except PaymentError as e:
                payment_span.set_status(trace.Status(trace.StatusCode.ERROR))
                payment_span.record_exception(e)
                raise
        
        # ステップ3: 出荷準備
        with tracer.start_as_current_span("prepare_shipment") as shipment_span:
            shipment_span.set_attribute("order_id", order_id)
            prepare_shipment(order_id)
        
        span.set_attribute("order.status", "completed")
        return {"status": "success", "order_id": order_id}
```

### 4.2 トレースコンテキストの伝播 / Trace Context Propagation

```python
# ✅ 良い例: マイクロサービス間のコンテキスト伝播
from opentelemetry.propagate import inject, extract
from opentelemetry import context
import requests

# サービスA: コンテキストを送信
def call_service_b(data):
    with tracer.start_as_current_span("call_service_b") as span:
        headers = {}
        # トレースコンテキストをヘッダーに注入
        inject(headers)
        
        response = requests.post(
            "http://service-b/api/process",
            json=data,
            headers=headers
        )
        
        span.set_attribute("http.status_code", response.status_code)
        return response.json()

# サービスB: コンテキストを受信
def process_request(request):
    # ヘッダーからトレースコンテキストを抽出
    ctx = extract(request.headers)
    context.attach(ctx)
    
    with tracer.start_as_current_span("service_b_process") as span:
        # 処理を実行（親スパンと紐付けられる）
        result = do_processing(request.data)
        span.set_attribute("result.count", len(result))
        return result
```

### 4.3 トレースサンプリング / Trace Sampling

```python
# ✅ 良い例: サンプリング戦略
from opentelemetry.sdk.trace.sampling import (
    TraceIdRatioBased,
    ParentBased,
    ALWAYS_ON,
    ALWAYS_OFF
)

# 本番環境: 10%のトレースをサンプリング
production_sampler = ParentBased(
    root=TraceIdRatioBased(0.1)  # 10%
)

# ステージング環境: 50%のトレースをサンプリング
staging_sampler = ParentBased(
    root=TraceIdRatioBased(0.5)  # 50%
)

# 開発環境: すべてのトレースをサンプリング
development_sampler = ParentBased(
    root=ALWAYS_ON
)

# 環境に応じたサンプラーの選択
sampler = {
    "production": production_sampler,
    "staging": staging_sampler,
    "development": development_sampler
}[environment]

trace.set_tracer_provider(
    TracerProvider(sampler=sampler)
)
```

---

## 5. アラート設定 / Alerting Configuration

### 5.1 アラートルール / Alert Rules

```yaml
# ✅ 良い例: Prometheusアラートルール
groups:
  - name: service_alerts
    interval: 30s
    rules:
      # 高エラー率
      - alert: HighErrorRate
        expr: |
          sum(rate(error_count_total[5m])) by (service) 
          / 
          sum(rate(request_count_total[5m])) by (service) 
          > 0.05
        for: 5m
        labels:
          severity: critical
          team: backend
        annotations:
          summary: "High error rate on {{ $labels.service }}"
          description: "Error rate is {{ $value | humanizePercentage }} (threshold: 5%)"
      
      # 高レイテンシ
      - alert: HighLatency
        expr: |
          histogram_quantile(0.95, 
            sum(rate(request_latency_seconds_bucket[5m])) by (service, le)
          ) > 1.0
        for: 5m
        labels:
          severity: warning
          team: backend
        annotations:
          summary: "High latency on {{ $labels.service }}"
          description: "95th percentile latency is {{ $value }}s (threshold: 1s)"
      
      # CPU使用率
      - alert: HighCPUUsage
        expr: cpu_usage_percent > 80
        for: 10m
        labels:
          severity: warning
          team: infrastructure
        annotations:
          summary: "High CPU usage on {{ $labels.host }}"
          description: "CPU usage is {{ $value }}% (threshold: 80%)"
      
      # メモリ使用率
      - alert: HighMemoryUsage
        expr: memory_usage_percent > 85
        for: 10m
        labels:
          severity: warning
          team: infrastructure
        annotations:
          summary: "High memory usage on {{ $labels.host }}"
          description: "Memory usage is {{ $value }}% (threshold: 85%)"
      
      # サービスダウン
      - alert: ServiceDown
        expr: up{job="service"} == 0
        for: 1m
        labels:
          severity: critical
          team: sre
        annotations:
          summary: "Service {{ $labels.instance }} is down"
          description: "Service has been down for more than 1 minute"
```

### 5.2 アラート重要度 / Alert Severity Levels

```python
# ✅ 良い例: アラート重要度の定義
from enum import Enum

class AlertSeverity(Enum):
    CRITICAL = "critical"  # 即座の対応が必要
    ERROR = "error"        # 早急な対応が必要
    WARNING = "warning"    # 注意が必要
    INFO = "info"          # 情報提供

# アラート定義
ALERT_DEFINITIONS = {
    "service_down": {
        "severity": AlertSeverity.CRITICAL,
        "threshold": "up == 0",
        "duration": "1m",
        "notification_channels": ["pagerduty", "slack"],
        "description": "サービスがダウンしています"
    },
    "high_error_rate": {
        "severity": AlertSeverity.CRITICAL,
        "threshold": "error_rate > 0.05",
        "duration": "5m",
        "notification_channels": ["pagerduty", "slack"],
        "description": "エラー率が5%を超えています"
    },
    "high_latency": {
        "severity": AlertSeverity.WARNING,
        "threshold": "p95_latency > 1.0",
        "duration": "5m",
        "notification_channels": ["slack"],
        "description": "レイテンシが1秒を超えています"
    },
    "disk_space_low": {
        "severity": AlertSeverity.WARNING,
        "threshold": "disk_usage > 80",
        "duration": "10m",
        "notification_channels": ["slack", "email"],
        "description": "ディスク使用率が80%を超えています"
    }
}
```

### 5.3 アラート疲労の回避 / Alert Fatigue Prevention

```python
# ✅ 良い例: アラートの抑制とグルーピング
# Alertmanager設定例
alertmanager_config = """
route:
  receiver: 'default'
  group_by: ['alertname', 'service']
  group_wait: 30s        # 最初のアラートを待つ時間
  group_interval: 5m     # グループ化された追加アラートを待つ時間
  repeat_interval: 12h   # 同じアラートの再送間隔
  
  routes:
    # Critical アラートは即座に通知
    - match:
        severity: critical
      receiver: 'pagerduty'
      group_wait: 10s
      repeat_interval: 5m
    
    # Warning アラートはグルーピング
    - match:
        severity: warning
      receiver: 'slack'
      group_wait: 5m
      repeat_interval: 1h

# 抑制ルール（inhibit_rules）
inhibit_rules:
  # サービスダウン時は他のアラートを抑制
  - source_match:
      alertname: 'ServiceDown'
    target_match_re:
      alertname: 'High.*'
    equal: ['service']
"""
```

---

## 6. SLI/SLO/SLA定義 / SLI/SLO/SLA Definitions

### 6.1 SLI（Service Level Indicator）

```python
# ✅ 良い例: SLI定義
from dataclasses import dataclass
from typing import List

@dataclass
class SLI:
    name: str
    description: str
    query: str
    unit: str

# 可用性SLI
availability_sli = SLI(
    name="availability",
    description="正常なリクエストの割合",
    query="""
        sum(rate(http_requests_total{status!~"5.."}[5m]))
        /
        sum(rate(http_requests_total[5m]))
    """,
    unit="percentage"
)

# レイテンシSLI
latency_sli = SLI(
    name="latency",
    description="95パーセンタイルのレスポンスタイム",
    query="""
        histogram_quantile(0.95,
            sum(rate(request_duration_seconds_bucket[5m])) by (le)
        )
    """,
    unit="seconds"
)

# エラー率SLI
error_rate_sli = SLI(
    name="error_rate",
    description="エラーリクエストの割合",
    query="""
        sum(rate(http_requests_total{status=~"5.."}[5m]))
        /
        sum(rate(http_requests_total[5m]))
    """,
    unit="percentage"
)
```

### 6.2 SLO（Service Level Objective）

```python
# ✅ 良い例: SLO定義
@dataclass
class SLO:
    sli: SLI
    target: float
    window: str
    description: str

# SLO定義
slos = [
    SLO(
        sli=availability_sli,
        target=0.999,  # 99.9%
        window="30d",
        description="30日間で99.9%の可用性を維持"
    ),
    SLO(
        sli=latency_sli,
        target=1.0,  # 1秒
        window="30d",
        description="95パーセンタイルのレスポンスタイムが1秒以内"
    ),
    SLO(
        sli=error_rate_sli,
        target=0.001,  # 0.1%
        window="30d",
        description="エラー率が0.1%以下"
    )
]

# エラーバジェット計算
def calculate_error_budget(slo: SLO, actual_value: float) -> dict:
    """エラーバジェットを計算"""
    error_budget = 1 - slo.target
    consumed_budget = max(0, actual_value - slo.target)
    remaining_budget = error_budget - consumed_budget
    
    return {
        "error_budget": error_budget,
        "consumed": consumed_budget,
        "remaining": remaining_budget,
        "percentage_remaining": (remaining_budget / error_budget) * 100 if error_budget > 0 else 0
    }

# 使用例
actual_availability = 0.9985
budget = calculate_error_budget(slos[0], 1 - actual_availability)
print(f"エラーバジェット残: {budget['percentage_remaining']:.1f}%")
```

### 6.3 SLA（Service Level Agreement）

```python
# ✅ 良い例: SLA定義
@dataclass
class SLA:
    service_name: str
    slos: List[SLO]
    measurement_window: str
    consequences: dict
    exclusions: List[str]

# SLA定義例
payment_service_sla = SLA(
    service_name="Payment Service",
    slos=[
        SLO(
            sli=availability_sli,
            target=0.995,  # 99.5%
            window="monthly",
            description="月次で99.5%の可用性"
        ),
        SLO(
            sli=latency_sli,
            target=2.0,  # 2秒
            window="monthly",
            description="95パーセンタイルが2秒以内"
        )
    ],
    measurement_window="monthly",
    consequences={
        "99.5%以上": "正常",
        "99.0%-99.5%": "10%のサービスクレジット",
        "95.0%-99.0%": "25%のサービスクレジット",
        "95.0%未満": "50%のサービスクレジット"
    },
    exclusions=[
        "計画メンテナンス（事前通知あり）",
        "顧客側の問題（ネットワーク等）",
        "不可抗力（天災等）",
        "第三者サービスの障害"
    ]
)
```

---

## 7. ダッシュボード設計 / Dashboard Design

### 7.1 Grafanaダッシュボード例

```json
{
  "dashboard": {
    "title": "Service Overview",
    "panels": [
      {
        "title": "Request Rate",
        "targets": [
          {
            "expr": "sum(rate(http_requests_total[5m])) by (service)",
            "legendFormat": "{{ service }}"
          }
        ],
        "type": "graph"
      },
      {
        "title": "Error Rate",
        "targets": [
          {
            "expr": "sum(rate(http_requests_total{status=~\"5..\"}[5m])) by (service) / sum(rate(http_requests_total[5m])) by (service)",
            "legendFormat": "{{ service }}"
          }
        ],
        "type": "graph"
      },
      {
        "title": "95th Percentile Latency",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, sum(rate(request_duration_seconds_bucket[5m])) by (service, le))",
            "legendFormat": "{{ service }}"
          }
        ],
        "type": "graph"
      },
      {
        "title": "SLO Compliance",
        "targets": [
          {
            "expr": "1 - (sum(rate(http_requests_total{status=~\"5..\"}[30d])) / sum(rate(http_requests_total[30d])))",
            "legendFormat": "Availability SLO (target: 99.9%)"
          }
        ],
        "type": "gauge",
        "thresholds": [
          {"value": 0.999, "color": "green"},
          {"value": 0.995, "color": "yellow"},
          {"value": 0, "color": "red"}
        ]
      }
    ]
  }
}
```

### 7.2 ダッシュボード設計原則

```python
# ✅ 良い例: ダッシュボードの階層化
DASHBOARD_HIERARCHY = {
    "Level 1: Overview Dashboard": {
        "対象": "すべてのステークホルダー",
        "更新頻度": "リアルタイム",
        "メトリクス": [
            "全体の可用性",
            "総リクエスト数",
            "エラー率",
            "平均レスポンスタイム",
            "SLO達成状況"
        ]
    },
    "Level 2: Service Dashboard": {
        "対象": "開発チーム、SRE",
        "更新頻度": "リアルタイム",
        "メトリクス": [
            "サービス別リクエスト数",
            "サービス別エラー率",
            "サービス別レイテンシ",
            "依存関係の状態",
            "リソース使用率"
        ]
    },
    "Level 3: Deep Dive Dashboard": {
        "対象": "開発者、SRE（トラブルシューティング用）",
        "更新頻度": "リアルタイム",
        "メトリクス": [
            "エンドポイント別メトリクス",
            "データベースクエリ性能",
            "キャッシュヒット率",
            "外部API呼び出し時間",
            "詳細なエラー内訳"
        ]
    }
}
```

---

## 8. Devin向けの利用パターン / Usage Patterns for Devin

### 8.1 監視実装のプロンプト例

#### プロンプト1: 構造化ログの実装
```
タスク: Pythonアプリケーションに構造化ログを実装してください

要件:
1. JSON形式の構造化ログ
2. 以下の標準フィールドを含める:
   - timestamp（ISO 8601、UTC）
   - level
   - logger
   - message
   - request_id
   - user_id
   - event

3. コンテキスト情報の自動伝播
4. 例外時のスタックトレース出力

実装基準:
- このドキュメントのセクション2（構造化ログ標準）に従う
- JSONFormatterクラスを実装
- ContextFilterで自動的にコンテキスト情報を追加
```

#### プロンプト2: Prometheusメトリクスの実装
```
タスク: Webアプリケーションにプロメテウスメトリクスを実装してください

要件:
1. 4つのゴールデンシグナルをすべて実装:
   - Latency（レイテンシ）
   - Traffic（トラフィック）
   - Errors（エラー）
   - Saturation（飽和度）

2. メトリクスタイプ:
   - Counter: リクエスト数、エラー数
   - Histogram: レスポンスタイム
   - Gauge: CPU/メモリ使用率

3. ラベル付け:
   - service
   - endpoint
   - method
   - status

実装基準:
- このドキュメントのセクション3（メトリクス収集）に従う
- /metricsエンドポイントでメトリクスを公開
```

#### プロンプト3: 分散トレーシングの実装
```
タスク: マイクロサービスアーキテクチャに分散トレーシングを実装してください

要件:
1. OpenTelemetryを使用
2. Jaegerにトレースをエクスポート
3. サービス間でトレースコンテキストを伝播
4. 主要な処理にスパンを追加:
   - データベースクエリ
   - 外部API呼び出し
   - ビジネスロジック

5. スパン属性:
   - service.name
   - operation名
   - 関連するID（order_id等）

実装基準:
- このドキュメントのセクション4（分散トレーシング）に従う
- 自動計装と手動スパンの併用
```

#### プロンプト4: SLO/SLI設定
```
タスク: サービスのSLI/SLOを定義し、監視を実装してください

要件:
1. 以下のSLIを定義:
   - 可用性（Availability）
   - レイテンシ（Latency）
   - エラー率（Error Rate）

2. 各SLIに対するSLOを設定:
   - 可用性: 99.9%（30日間）
   - レイテンシ: 95パーセンタイルが1秒以内
   - エラー率: 0.1%以下

3. エラーバジェットの計算と可視化
4. SLO違反時のアラート設定

実装基準:
- このドキュメントのセクション6（SLI/SLO/SLA定義）に従う
- Prometheusクエリでメトリクスを収集
- Grafanaでダッシュボード作成
```

### 8.2 アラート設定のプロンプト例

#### プロンプト5: Prometheusアラートルール作成
```
タスク: Prometheusアラートルールを作成してください

要件:
1. 以下のアラートを実装:
   - HighErrorRate: エラー率5%超（5分間）
   - HighLatency: P95レイテンシ1秒超（5分間）
   - ServiceDown: サービスダウン（1分間）
   - HighCPU: CPU使用率80%超（10分間）
   - HighMemory: メモリ使用率85%超（10分間）

2. アラート重要度:
   - critical: 即座の対応が必要
   - warning: 注意が必要

3. アラート疲労防止:
   - グループ化設定
   - 抑制ルール
   - 適切な repeat_interval

実装基準:
- このドキュメントのセクション5（アラート設定）に従う
- YAMLフォーマットでalert rulesを定義
```

---

## 9. チェックリスト / Checklist

### 監視実装チェックリスト

#### ログ実装
- [ ] 構造化ログ（JSON形式）を実装
- [ ] 標準フィールド（timestamp, level, logger等）を含む
- [ ] 適切なログレベルの使用
- [ ] コンテキスト情報の自動伝播
- [ ] 例外時のスタックトレース出力
- [ ] ログローテーション設定
- [ ] 機密情報のマスキング

#### メトリクス実装
- [ ] Prometheusメトリクスエンドポイント（/metrics）
- [ ] 4つのゴールデンシグナルを実装
- [ ] 適切なメトリクスタイプの選択
- [ ] 意味のあるラベル付け
- [ ] ビジネスメトリクスの定義
- [ ] メトリクスのドキュメント化

#### トレーシング実装
- [ ] OpenTelemetry SDK導入
- [ ] トレースエクスポーター設定（Jaeger等）
- [ ] サービス間のコンテキスト伝播
- [ ] 主要処理へのスパン追加
- [ ] スパン属性の適切な設定
- [ ] サンプリング戦略の実装

#### アラート設定
- [ ] 主要メトリクスへのアラート設定
- [ ] 適切なしきい値の設定
- [ ] アラート重要度の分類
- [ ] 通知チャネルの設定
- [ ] アラート疲労防止策
- [ ] オンコールローテーション

#### SLO/SLA
- [ ] SLIの定義と測定
- [ ] SLOの設定
- [ ] エラーバジェットの計算
- [ ] SLO違反時の対応手順
- [ ] SLAの文書化（必要に応じて）

#### ダッシュボード
- [ ] 概要ダッシュボード（Level 1）
- [ ] サービス別ダッシュボード（Level 2）
- [ ] 詳細ダッシュボード（Level 3）
- [ ] SLO達成状況の可視化
- [ ] アラート履歴の表示

---

## 10. 関連ドキュメント / Related Documents

- [アーキテクチャ設計原則](../02-architecture-standards/design-principles.md)
- [品質標準](../04-quality-standards/README.md)
- [セキュリティチェックリスト](../07-security-compliance/security-checklist.md)
- [コンテナ標準](./container-standards.md)

---

## 11. 更新履歴 / Change History

| バージョン | 日付 | 変更内容 | 作成者 |
|---------|------|---------|-------|
| 1.0 | 2025-10-24 | 初版作成 | Development Team |

---

**このドキュメントの維持管理についてのお問い合わせは、SREチームまでご連絡ください。**
