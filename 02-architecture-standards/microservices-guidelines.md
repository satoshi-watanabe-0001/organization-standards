# マイクロサービス設計ガイドライン / Microservices Design Guidelines

## バージョン情報 / Version Information
- **最終更新日 / Last Updated**: 2025-10-24
- **バージョン / Version**: 1.0
- **対象 / Target**: すべてのプロジェクト / All Projects
- **適用範囲 / Scope**: 任意 / Optional (Tier 3)

---

## 目的 / Purpose

このドキュメントは、マイクロサービスアーキテクチャの設計と実装のためのガイドラインを提供します。サービス分割原則、サービス間通信、データ管理、分散トランザクション、サービスメッシュなど、マイクロサービスアーキテクチャのベストプラクティスを定義します。

This document provides guidelines for designing and implementing microservices architecture, including service decomposition principles, inter-service communication, data management, distributed transactions, and service mesh patterns.

---

## 1. サービス分割原則 / Service Decomposition Principles

### 1.1 ドメイン駆動設計（DDD）による分割

```python
# ✅ 良い例: 境界づけられたコンテキスト（Bounded Context）
from dataclasses import dataclass
from typing import List, Optional
from enum import Enum

# コンテキスト1: 注文管理
class OrderStatus(Enum):
    PENDING = "pending"
    CONFIRMED = "confirmed"
    SHIPPED = "shipped"
    DELIVERED = "delivered"
    CANCELLED = "cancelled"

@dataclass
class Order:
    """注文集約（Aggregate）"""
    order_id: str
    customer_id: str
    items: List['OrderItem']
    status: OrderStatus
    total_amount: float
    
    def confirm(self):
        """注文を確認"""
        if self.status != OrderStatus.PENDING:
            raise ValueError("確認できるのは保留中の注文のみです")
        self.status = OrderStatus.CONFIRMED
    
    def cancel(self):
        """注文をキャンセル"""
        if self.status in [OrderStatus.SHIPPED, OrderStatus.DELIVERED]:
            raise ValueError("出荷済み/配達済みの注文はキャンセルできません")
        self.status = OrderStatus.CANCELLED

@dataclass
class OrderItem:
    """注文明細"""
    product_id: str
    quantity: int
    price: float

# コンテキスト2: 在庫管理
@dataclass
class Inventory:
    """在庫集約"""
    product_id: str
    available_quantity: int
    reserved_quantity: int
    
    def reserve(self, quantity: int) -> bool:
        """在庫を予約"""
        if self.available_quantity >= quantity:
            self.available_quantity -= quantity
            self.reserved_quantity += quantity
            return True
        return False
    
    def release(self, quantity: int):
        """予約を解放"""
        self.reserved_quantity -= quantity
        self.available_quantity += quantity

# コンテキスト3: 決済管理
@dataclass
class Payment:
    """決済集約"""
    payment_id: str
    order_id: str
    amount: float
    status: str
    
    def process(self):
        """決済を処理"""
        # 決済処理ロジック
        self.status = "completed"

# ❌ 悪い例: すべてを1つのモノリスで管理
class MonolithicOrderSystem:
    def create_order(self, customer, items):
        # 注文、在庫、決済をすべて1つのトランザクションで処理
        order = self.save_order(customer, items)
        self.update_inventory(items)
        self.process_payment(order)
        # 密結合で変更が困難 - NG
```

### 1.2 サービス分割の基準

```python
# ✅ 良い例: サービス分割チェックリスト
from dataclasses import dataclass
from typing import List

@dataclass
class ServiceDecompositionCriteria:
    """サービス分割基準"""
    
    # ビジネス能力（Business Capability）
    business_capability: str
    
    # チーム構成（Team Organization）
    team_size: int
    team_autonomy: bool
    
    # データ整合性（Data Consistency）
    requires_strong_consistency: bool
    
    # 変更頻度（Change Frequency）
    change_frequency: str  # high, medium, low
    
    # スケーラビリティ（Scalability）
    scalability_requirements: str
    
    # 技術スタック（Technology Stack）
    can_use_different_tech: bool

def should_split_service(criteria: ServiceDecompositionCriteria) -> dict:
    """サービスを分割すべきか判定"""
    
    split_score = 0
    reasons = []
    
    # ビジネス能力が明確
    if criteria.business_capability:
        split_score += 2
        reasons.append("明確なビジネス能力が定義されている")
    
    # チームが自律的
    if criteria.team_autonomy and criteria.team_size >= 5:
        split_score += 2
        reasons.append("自律的なチームが存在する")
    
    # 強い整合性が不要
    if not criteria.requires_strong_consistency:
        split_score += 1
        reasons.append("結果整合性で問題ない")
    
    # 変更頻度が高い
    if criteria.change_frequency == "high":
        split_score += 1
        reasons.append("変更頻度が高い")
    
    # 異なる技術スタックを使用可能
    if criteria.can_use_different_tech:
        split_score += 1
        reasons.append("異なる技術スタックの利点がある")
    
    recommendation = "分割推奨" if split_score >= 4 else "モノリス維持推奨"
    
    return {
        "score": split_score,
        "recommendation": recommendation,
        "reasons": reasons
    }

# 使用例
criteria = ServiceDecompositionCriteria(
    business_capability="注文管理",
    team_size=6,
    team_autonomy=True,
    requires_strong_consistency=False,
    change_frequency="high",
    scalability_requirements="high",
    can_use_different_tech=True
)

result = should_split_service(criteria)
print(f"判定: {result['recommendation']} (スコア: {result['score']})")
```

---

## 2. サービス間通信 / Inter-Service Communication

### 2.1 同期通信（REST API）

```python
# ✅ 良い例: RESTful APIクライアント
import requests
from typing import Optional, Dict
import logging
from tenacity import retry, stop_after_attempt, wait_exponential

logger = logging.getLogger(__name__)

class ServiceClient:
    """マイクロサービスクライアント"""
    
    def __init__(self, base_url: str, timeout: int = 30):
        self.base_url = base_url
        self.timeout = timeout
        self.session = requests.Session()
        
        # サーキットブレーカー設定
        self.circuit_breaker = CircuitBreaker(
            failure_threshold=5,
            recovery_timeout=60
        )
    
    @retry(
        stop=stop_after_attempt(3),
        wait=wait_exponential(multiplier=1, min=2, max=10)
    )
    def get(self, endpoint: str, params: Optional[Dict] = None) -> Dict:
        """GET リクエスト（リトライ機能付き）"""
        
        if self.circuit_breaker.is_open():
            raise ServiceUnavailableError("Circuit breaker is open")
        
        try:
            response = self.session.get(
                f"{self.base_url}/{endpoint}",
                params=params,
                timeout=self.timeout,
                headers={
                    "Content-Type": "application/json",
                    "X-Request-ID": get_request_id(),
                    "X-Service-Name": "order-service"
                }
            )
            
            response.raise_for_status()
            self.circuit_breaker.record_success()
            
            return response.json()
            
        except requests.exceptions.RequestException as e:
            self.circuit_breaker.record_failure()
            logger.error(f"Request failed: {endpoint}", exc_info=True)
            raise
    
    def post(self, endpoint: str, data: Dict) -> Dict:
        """POST リクエスト"""
        # 同様の実装
        pass

# サーキットブレーカーの実装
class CircuitBreaker:
    def __init__(self, failure_threshold: int, recovery_timeout: int):
        self.failure_threshold = failure_threshold
        self.recovery_timeout = recovery_timeout
        self.failure_count = 0
        self.last_failure_time = None
        self.state = "CLOSED"  # CLOSED, OPEN, HALF_OPEN
    
    def is_open(self) -> bool:
        if self.state == "OPEN":
            # タイムアウト後、HALF_OPENに移行
            if time.time() - self.last_failure_time > self.recovery_timeout:
                self.state = "HALF_OPEN"
                return False
            return True
        return False
    
    def record_success(self):
        self.failure_count = 0
        self.state = "CLOSED"
    
    def record_failure(self):
        self.failure_count += 1
        self.last_failure_time = time.time()
        
        if self.failure_count >= self.failure_threshold:
            self.state = "OPEN"
            logger.warning("Circuit breaker opened")

# ❌ 悪い例: リトライなし、タイムアウトなし
def bad_api_call(url):
    response = requests.get(url)  # タイムアウトなし、エラーハンドリングなし - NG
    return response.json()
```

### 2.2 非同期通信（メッセージング）

```python
# ✅ 良い例: イベント駆動アーキテクチャ
from dataclasses import dataclass, asdict
from datetime import datetime
import json
import pika  # RabbitMQ

@dataclass
class DomainEvent:
    """ドメインイベント基底クラス"""
    event_id: str
    event_type: str
    timestamp: datetime
    aggregate_id: str
    version: int

@dataclass
class OrderCreatedEvent(DomainEvent):
    """注文作成イベント"""
    customer_id: str
    items: list
    total_amount: float
    
    def to_json(self) -> str:
        data = asdict(self)
        data['timestamp'] = data['timestamp'].isoformat()
        return json.dumps(data)

class EventPublisher:
    """イベントパブリッシャー"""
    
    def __init__(self, rabbitmq_url: str):
        self.connection = pika.BlockingConnection(
            pika.URLParameters(rabbitmq_url)
        )
        self.channel = self.connection.channel()
        
        # Exchange宣言
        self.channel.exchange_declare(
            exchange='domain_events',
            exchange_type='topic',
            durable=True
        )
    
    def publish(self, event: DomainEvent, routing_key: str):
        """イベントを発行"""
        self.channel.basic_publish(
            exchange='domain_events',
            routing_key=routing_key,
            body=event.to_json(),
            properties=pika.BasicProperties(
                delivery_mode=2,  # 永続化
                content_type='application/json',
                message_id=event.event_id,
                timestamp=int(event.timestamp.timestamp())
            )
        )
        
        logger.info(f"Published event: {event.event_type}", extra={
            "event_id": event.event_id,
            "routing_key": routing_key
        })

class EventSubscriber:
    """イベントサブスクライバー"""
    
    def __init__(self, rabbitmq_url: str, queue_name: str):
        self.connection = pika.BlockingConnection(
            pika.URLParameters(rabbitmq_url)
        )
        self.channel = self.connection.channel()
        self.queue_name = queue_name
        
        # Queue宣言
        self.channel.queue_declare(
            queue=queue_name,
            durable=True
        )
        
        # QoS設定（同時処理数制限）
        self.channel.basic_qos(prefetch_count=1)
    
    def subscribe(self, routing_key: str, handler):
        """イベントを購読"""
        # Queueをexchangeにバインド
        self.channel.queue_bind(
            exchange='domain_events',
            queue=self.queue_name,
            routing_key=routing_key
        )
        
        def callback(ch, method, properties, body):
            try:
                event_data = json.loads(body)
                handler(event_data)
                
                # Ack（処理成功）
                ch.basic_ack(delivery_tag=method.delivery_tag)
                
            except Exception as e:
                logger.error(f"Event processing failed", exc_info=True)
                # Nack（処理失敗）- 再キューイング
                ch.basic_nack(
                    delivery_tag=method.delivery_tag,
                    requeue=True
                )
        
        self.channel.basic_consume(
            queue=self.queue_name,
            on_message_callback=callback
        )
        
        logger.info(f"Subscribed to {routing_key}")
        self.channel.start_consuming()

# 使用例: 注文サービス
def create_order(customer_id: str, items: list):
    # 注文を作成
    order = Order(...)
    order_repository.save(order)
    
    # イベントを発行
    event = OrderCreatedEvent(
        event_id=str(uuid.uuid4()),
        event_type="OrderCreated",
        timestamp=datetime.utcnow(),
        aggregate_id=order.order_id,
        version=1,
        customer_id=customer_id,
        items=items,
        total_amount=order.total_amount
    )
    
    publisher.publish(event, routing_key="order.created")

# 使用例: 在庫サービス
def handle_order_created(event_data):
    """注文作成イベントを処理"""
    order_id = event_data['aggregate_id']
    items = event_data['items']
    
    # 在庫を予約
    for item in items:
        inventory_service.reserve(item['product_id'], item['quantity'])
    
    logger.info(f"Inventory reserved for order {order_id}")
```

### 2.3 gRPC通信

```protobuf
// ✅ 良い例: gRPC サービス定義
syntax = "proto3";

package order;

// 注文サービス
service OrderService {
  // 注文を作成
  rpc CreateOrder (CreateOrderRequest) returns (CreateOrderResponse);
  
  // 注文を取得
  rpc GetOrder (GetOrderRequest) returns (Order);
  
  // 注文をストリーミング
  rpc StreamOrders (StreamOrdersRequest) returns (stream Order);
}

message CreateOrderRequest {
  string customer_id = 1;
  repeated OrderItem items = 2;
}

message CreateOrderResponse {
  string order_id = 1;
  string status = 2;
}

message GetOrderRequest {
  string order_id = 1;
}

message Order {
  string order_id = 1;
  string customer_id = 2;
  repeated OrderItem items = 3;
  string status = 4;
  double total_amount = 5;
  int64 created_at = 6;
}

message OrderItem {
  string product_id = 1;
  int32 quantity = 2;
  double price = 3;
}

message StreamOrdersRequest {
  string customer_id = 1;
}
```

```python
# ✅ 良い例: gRPC サーバー実装
import grpc
from concurrent import futures
import order_pb2
import order_pb2_grpc

class OrderServiceServicer(order_pb2_grpc.OrderServiceServicer):
    """注文サービスの実装"""
    
    def CreateOrder(self, request, context):
        """注文を作成"""
        try:
            # ビジネスロジック
            order = create_order_logic(
                customer_id=request.customer_id,
                items=request.items
            )
            
            return order_pb2.CreateOrderResponse(
                order_id=order.order_id,
                status=order.status
            )
            
        except Exception as e:
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(str(e))
            return order_pb2.CreateOrderResponse()
    
    def GetOrder(self, request, context):
        """注文を取得"""
        order = get_order_by_id(request.order_id)
        
        if not order:
            context.set_code(grpc.StatusCode.NOT_FOUND)
            context.set_details(f"Order {request.order_id} not found")
            return order_pb2.Order()
        
        return order_pb2.Order(
            order_id=order.order_id,
            customer_id=order.customer_id,
            status=order.status,
            total_amount=order.total_amount
        )
    
    def StreamOrders(self, request, context):
        """注文をストリーミング"""
        orders = get_orders_by_customer(request.customer_id)
        
        for order in orders:
            yield order_pb2.Order(
                order_id=order.order_id,
                customer_id=order.customer_id,
                status=order.status,
                total_amount=order.total_amount
            )

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    order_pb2_grpc.add_OrderServiceServicer_to_server(
        OrderServiceServicer(), server
    )
    server.add_insecure_port('[::]:50051')
    server.start()
    server.wait_for_termination()
```

---

## 3. データ管理 / Data Management

### 3.1 Database per Service パターン

```python
# ✅ 良い例: 各サービスが独自のデータベースを持つ

# 注文サービスのデータベース
class OrderDatabase:
    def __init__(self, connection_string: str):
        self.engine = create_engine(connection_string)
    
    # 注文サービス専用のテーブル
    # orders, order_items

# 在庫サービスのデータベース
class InventoryDatabase:
    def __init__(self, connection_string: str):
        self.engine = create_engine(connection_string)
    
    # 在庫サービス専用のテーブル
    # inventory, reservations

# 顧客サービスのデータベース
class CustomerDatabase:
    def __init__(self, connection_string: str):
        self.engine = create_engine(connection_string)
    
    # 顧客サービス専用のテーブル
    # customers, addresses

# ❌ 悪い例: 複数のサービスが同じデータベースを共有
class SharedDatabase:
    # すべてのサービスが同じDBにアクセス
    # orders, inventory, customers を1つのDBに - NG
    pass
```

### 3.2 イベントソーシング

```python
# ✅ 良い例: イベントソーシングの実装
from dataclasses import dataclass
from typing import List
from datetime import datetime

@dataclass
class Event:
    """イベント基底クラス"""
    aggregate_id: str
    event_type: str
    timestamp: datetime
    version: int
    data: dict

class EventStore:
    """イベントストア"""
    
    def __init__(self, db_connection):
        self.db = db_connection
    
    def append(self, aggregate_id: str, events: List[Event]):
        """イベントを追加"""
        for event in events:
            self.db.execute("""
                INSERT INTO events (aggregate_id, event_type, timestamp, version, data)
                VALUES (?, ?, ?, ?, ?)
            """, (
                event.aggregate_id,
                event.event_type,
                event.timestamp,
                event.version,
                json.dumps(event.data)
            ))
    
    def get_events(self, aggregate_id: str) -> List[Event]:
        """集約のすべてのイベントを取得"""
        rows = self.db.execute("""
            SELECT * FROM events
            WHERE aggregate_id = ?
            ORDER BY version ASC
        """, (aggregate_id,)).fetchall()
        
        return [Event(**row) for row in rows]

class OrderAggregate:
    """注文集約（イベントソーシング）"""
    
    def __init__(self, order_id: str):
        self.order_id = order_id
        self.version = 0
        self.status = None
        self.items = []
        self.uncommitted_events = []
    
    def create(self, customer_id: str, items: List[dict]):
        """注文を作成"""
        event = Event(
            aggregate_id=self.order_id,
            event_type="OrderCreated",
            timestamp=datetime.utcnow(),
            version=self.version + 1,
            data={
                "customer_id": customer_id,
                "items": items
            }
        )
        
        self.apply(event)
        self.uncommitted_events.append(event)
    
    def confirm(self):
        """注文を確認"""
        if self.status != "pending":
            raise ValueError("確認できるのは保留中の注文のみです")
        
        event = Event(
            aggregate_id=self.order_id,
            event_type="OrderConfirmed",
            timestamp=datetime.utcnow(),
            version=self.version + 1,
            data={}
        )
        
        self.apply(event)
        self.uncommitted_events.append(event)
    
    def apply(self, event: Event):
        """イベントを適用して状態を更新"""
        if event.event_type == "OrderCreated":
            self.status = "pending"
            self.items = event.data['items']
        elif event.event_type == "OrderConfirmed":
            self.status = "confirmed"
        elif event.event_type == "OrderCancelled":
            self.status = "cancelled"
        
        self.version = event.version
    
    @classmethod
    def load_from_history(cls, order_id: str, events: List[Event]):
        """イベント履歴から集約を再構築"""
        order = cls(order_id)
        for event in events:
            order.apply(event)
        return order
```

### 3.3 CQRS（Command Query Responsibility Segregation）

```python
# ✅ 良い例: CQRSパターン
from abc import ABC, abstractmethod

# コマンド側（書き込み）
class Command(ABC):
    """コマンド基底クラス"""
    pass

@dataclass
class CreateOrderCommand(Command):
    customer_id: str
    items: List[dict]

class CommandHandler(ABC):
    @abstractmethod
    def handle(self, command: Command):
        pass

class CreateOrderCommandHandler(CommandHandler):
    def __init__(self, event_store: EventStore):
        self.event_store = event_store
    
    def handle(self, command: CreateOrderCommand):
        """注文作成コマンドを処理"""
        order_id = str(uuid.uuid4())
        order = OrderAggregate(order_id)
        order.create(command.customer_id, command.items)
        
        # イベントを保存
        self.event_store.append(order_id, order.uncommitted_events)
        
        return order_id

# クエリ側（読み取り）
class Query(ABC):
    """クエリ基底クラス"""
    pass

@dataclass
class GetOrderQuery(Query):
    order_id: str

class QueryHandler(ABC):
    @abstractmethod
    def handle(self, query: Query):
        pass

class GetOrderQueryHandler(QueryHandler):
    def __init__(self, read_model_db):
        self.db = read_model_db
    
    def handle(self, query: GetOrderQuery):
        """注文取得クエリを処理"""
        # Read Modelから取得（最適化されたクエリ）
        return self.db.execute("""
            SELECT o.*, c.name as customer_name
            FROM orders_read_model o
            JOIN customers c ON o.customer_id = c.customer_id
            WHERE o.order_id = ?
        """, (query.order_id,)).fetchone()

# Read Model更新（イベントハンドラー）
class OrderReadModelUpdater:
    def __init__(self, read_model_db):
        self.db = read_model_db
    
    def handle_order_created(self, event: Event):
        """注文作成イベントでRead Modelを更新"""
        self.db.execute("""
            INSERT INTO orders_read_model (order_id, customer_id, status, items, created_at)
            VALUES (?, ?, ?, ?, ?)
        """, (
            event.aggregate_id,
            event.data['customer_id'],
            'pending',
            json.dumps(event.data['items']),
            event.timestamp
        ))
```

---

## 4. 分散トランザクション / Distributed Transactions

### 4.1 Saga パターン

```python
# ✅ 良い例: Orchestration-based Saga
from enum import Enum
from dataclasses import dataclass
from typing import List, Callable

class SagaStatus(Enum):
    STARTED = "started"
    COMPLETED = "completed"
    COMPENSATING = "compensating"
    FAILED = "failed"

@dataclass
class SagaStep:
    """Sagaステップ"""
    name: str
    action: Callable
    compensation: Callable

class OrderSagaOrchestrator:
    """注文Sagaオーケストレーター"""
    
    def __init__(self):
        self.steps: List[SagaStep] = []
        self.completed_steps: List[str] = []
        self.status = SagaStatus.STARTED
    
    def add_step(self, name: str, action: Callable, compensation: Callable):
        """ステップを追加"""
        self.steps.append(SagaStep(name, action, compensation))
    
    def execute(self, context: dict):
        """Sagaを実行"""
        try:
            # 各ステップを順次実行
            for step in self.steps:
                logger.info(f"Executing step: {step.name}")
                step.action(context)
                self.completed_steps.append(step.name)
            
            self.status = SagaStatus.COMPLETED
            logger.info("Saga completed successfully")
            
        except Exception as e:
            logger.error(f"Saga failed at step: {step.name}", exc_info=True)
            self.status = SagaStatus.COMPENSATING
            
            # 補償トランザクションを逆順で実行
            self.compensate()
            
            self.status = SagaStatus.FAILED
            raise
    
    def compensate(self):
        """補償トランザクションを実行"""
        logger.info("Starting compensation")
        
        for step_name in reversed(self.completed_steps):
            step = next(s for s in self.steps if s.name == step_name)
            
            try:
                logger.info(f"Compensating step: {step.name}")
                step.compensation()
            except Exception as e:
                logger.error(f"Compensation failed: {step.name}", exc_info=True)
                # 補償失敗を記録（手動介入が必要）

# 使用例: 注文作成Saga
def create_order_saga(customer_id: str, items: List[dict], payment_info: dict):
    """注文作成Saga"""
    saga = OrderSagaOrchestrator()
    context = {
        'customer_id': customer_id,
        'items': items,
        'payment_info': payment_info
    }
    
    # ステップ1: 注文作成
    saga.add_step(
        name="create_order",
        action=lambda ctx: order_service.create(ctx['customer_id'], ctx['items']),
        compensation=lambda: order_service.cancel(context.get('order_id'))
    )
    
    # ステップ2: 在庫予約
    saga.add_step(
        name="reserve_inventory",
        action=lambda ctx: inventory_service.reserve(ctx['items']),
        compensation=lambda: inventory_service.release(context['items'])
    )
    
    # ステップ3: 決済処理
    saga.add_step(
        name="process_payment",
        action=lambda ctx: payment_service.charge(ctx['payment_info']),
        compensation=lambda: payment_service.refund(context.get('payment_id'))
    )
    
    # ステップ4: 配送手配
    saga.add_step(
        name="arrange_shipment",
        action=lambda ctx: shipment_service.arrange(context['order_id']),
        compensation=lambda: shipment_service.cancel(context.get('shipment_id'))
    )
    
    # Saga実行
    saga.execute(context)
    
    return context.get('order_id')
```

### 4.2 Choreography-based Saga

```python
# ✅ 良い例: Choreography-based Saga（イベント駆動）
# 各サービスがイベントをリッスンして独立に動作

# 注文サービス
class OrderService:
    def handle_create_order_request(self, customer_id, items):
        """注文作成リクエストを処理"""
        order = Order(customer_id=customer_id, items=items, status="pending")
        order_repository.save(order)
        
        # イベント発行: OrderCreated
        event_publisher.publish(OrderCreatedEvent(
            order_id=order.order_id,
            customer_id=customer_id,
            items=items
        ))
    
    def handle_inventory_reserved(self, event):
        """在庫予約完了イベントを処理"""
        order = order_repository.get(event.order_id)
        order.status = "inventory_reserved"
        order_repository.save(order)
        
        # イベント発行: ProcessPayment
        event_publisher.publish(ProcessPaymentEvent(
            order_id=order.order_id,
            amount=order.total_amount
        ))
    
    def handle_payment_failed(self, event):
        """決済失敗イベントを処理"""
        order = order_repository.get(event.order_id)
        order.status = "cancelled"
        order_repository.save(order)
        
        # イベント発行: ReleaseInventory（補償）
        event_publisher.publish(ReleaseInventoryEvent(
            order_id=order.order_id,
            items=order.items
        ))

# 在庫サービス
class InventoryService:
    def handle_order_created(self, event):
        """注文作成イベントを処理"""
        try:
            # 在庫予約
            for item in event.items:
                inventory_repository.reserve(item.product_id, item.quantity)
            
            # イベント発行: InventoryReserved
            event_publisher.publish(InventoryReservedEvent(
                order_id=event.order_id
            ))
            
        except InsufficientInventoryError:
            # イベント発行: InventoryReservationFailed
            event_publisher.publish(InventoryReservationFailedEvent(
                order_id=event.order_id
            ))
    
    def handle_release_inventory(self, event):
        """在庫解放イベントを処理（補償）"""
        for item in event.items:
            inventory_repository.release(item.product_id, item.quantity)

# 決済サービス
class PaymentService:
    def handle_process_payment(self, event):
        """決済処理イベントを処理"""
        try:
            payment = payment_gateway.charge(event.amount)
            
            # イベント発行: PaymentCompleted
            event_publisher.publish(PaymentCompletedEvent(
                order_id=event.order_id,
                payment_id=payment.id
            ))
            
        except PaymentError:
            # イベント発行: PaymentFailed
            event_publisher.publish(PaymentFailedEvent(
                order_id=event.order_id
            ))
```

---

## 5. API Gateway パターン / API Gateway Pattern

```python
# ✅ 良い例: API Gateway実装（FastAPI）
from fastapi import FastAPI, HTTPException, Depends
from typing import List
import httpx

app = FastAPI(title="API Gateway")

# サービスエンドポイント
ORDER_SERVICE_URL = "http://order-service:8000"
INVENTORY_SERVICE_URL = "http://inventory-service:8000"
CUSTOMER_SERVICE_URL = "http://customer-service:8000"

@app.post("/api/orders")
async def create_order(request: CreateOrderRequest):
    """注文を作成（複数サービスを集約）"""
    async with httpx.AsyncClient() as client:
        try:
            # 1. 顧客情報を取得
            customer_response = await client.get(
                f"{CUSTOMER_SERVICE_URL}/customers/{request.customer_id}",
                timeout=10.0
            )
            customer = customer_response.json()
            
            # 2. 在庫チェック
            inventory_response = await client.post(
                f"{INVENTORY_SERVICE_URL}/inventory/check",
                json={"items": request.items},
                timeout=10.0
            )
            
            if not inventory_response.json()['available']:
                raise HTTPException(status_code=400, detail="Insufficient inventory")
            
            # 3. 注文作成
            order_response = await client.post(
                f"{ORDER_SERVICE_URL}/orders",
                json={
                    "customer_id": request.customer_id,
                    "items": request.items
                },
                timeout=10.0
            )
            
            return order_response.json()
            
        except httpx.TimeoutException:
            raise HTTPException(status_code=504, detail="Service timeout")
        except httpx.RequestError as e:
            raise HTTPException(status_code=503, detail="Service unavailable")

@app.get("/api/orders/{order_id}")
async def get_order_details(order_id: str):
    """注文詳細を取得（複数サービスから集約）"""
    async with httpx.AsyncClient() as client:
        # 並列リクエスト
        order_task = client.get(f"{ORDER_SERVICE_URL}/orders/{order_id}")
        
        order_response = await order_task
        order = order_response.json()
        
        # 顧客情報を追加取得
        customer_response = await client.get(
            f"{CUSTOMER_SERVICE_URL}/customers/{order['customer_id']}"
        )
        
        # レスポンスを集約
        return {
            **order,
            "customer": customer_response.json()
        }
```

---

## 6. Devin向けの利用パターン / Usage Patterns for Devin

### プロンプト1: マイクロサービス分割
```
タスク: モノリシックなEコマースシステムをマイクロサービスに分割してください

要件:
1. ドメイン駆動設計に基づいてサービスを分割
2. 以下のコンテキストを特定:
   - 注文管理
   - 在庫管理
   - 決済管理
   - 顧客管理
   - 配送管理

3. 各サービスの境界と責務を明確化
4. サービス間の依存関係を図示

実装基準:
- このドキュメントのセクション1（サービス分割原則）に従う
- Database per Service パターンを適用
```

### プロンプト2: Saga実装
```
タスク: 注文処理のための Saga パターンを実装してください

要件:
1. Orchestration-based Saga を使用
2. 以下のステップを含める:
   - 注文作成
   - 在庫予約
   - 決済処理
   - 配送手配

3. 各ステップの補償トランザクションを実装
4. エラーハンドリングとリトライ機能

実装基準:
- このドキュメントのセクション4（分散トランザクション）に従う
```

### プロンプト3: API Gateway構築
```
タスク: マイクロサービス用のAPI Gatewayを実装してください

要件:
1. ルーティング機能
2. リクエスト集約
3. 認証・認可
4. レート制限
5. サーキットブレーカー

実装基準:
- このドキュメントのセクション5（API Gateway パターン）に従う
- FastAPIまたはKong を使用
```

---

## 7. 関連ドキュメント / Related Documents

- [アーキテクチャ設計原則](./design-principles.md)
- [データベース設計標準](./database-design.md)
- [API設計標準](./api-architecture.md)
- [監視・ログ標準](../05-technology-stack/monitoring-logging.md)

---

## 8. 更新履歴 / Change History

| バージョン | 日付 | 変更内容 | 作成者 |
|---------|------|---------|-------|
| 1.0 | 2025-10-24 | 初版作成 | Development Team |

---

**このドキュメントの維持管理についてのお問い合わせは、アーキテクチャチームまでご連絡ください。**
