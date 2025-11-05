# メッセージング技術スタック / Messaging Technology Stack

```yaml
version: "2.0.0"
last_updated: "2025-01-15"
status: "active"
owner: "Backend Architecture Team"
category: "technology-stack"
```

## 目次 / Table of Contents

1. [概要 / Overview](#概要--overview)
2. [メッセージングパターン / Messaging Patterns](#メッセージングパターン--messaging-patterns)
3. [メッセージブローカー / Message Brokers](#メッセージブローカー--message-brokers)
4. [イベントストリーミング / Event Streaming](#イベントストリーミング--event-streaming)
5. [Pub/Subメッセージング / Pub/Sub Messaging](#pubsubメッセージング--pubsub-messaging)
6. [ジョブキュー / Job Queues](#ジョブキュー--job-queues)
7. [リアルタイム通信 / Real-time Communication](#リアルタイム通信--real-time-communication)
8. [メッセージフォーマット / Message Formats](#メッセージフォーマット--message-formats)
9. [エラーハンドリングと再試行 / Error Handling and Retries](#エラーハンドリングと再試行--error-handling-and-retries)
10. [監視とトレーシング / Monitoring and Tracing](#監視とトレーシング--monitoring-and-tracing)
11. [セキュリティ / Security](#セキュリティ--security)
12. [ベストプラクティス / Best Practices](#ベストプラクティス--best-practices)
13. [参考資料 / References](#参考資料--references)

---

## 概要 / Overview

このドキュメントは、組織全体で使用するメッセージング技術スタックの標準を定義します。

### 目的 / Purpose

- 非同期通信の標準化
- マイクロサービス間の疎結合化
- イベント駆動アーキテクチャの実現
- スケーラブルで信頼性の高いメッセージング基盤の構築

### 適用範囲 / Scope

- マイクロサービス間通信
- イベント駆動アーキテクチャ
- バックグラウンドジョブ処理
- リアルタイム通信
- システム間連携

### メッセージングの利点 / Benefits of Messaging

- **疎結合**: サービス間の依存関係を最小化
- **スケーラビリティ**: 独立したスケーリングが可能
- **信頼性**: メッセージの永続化と再試行
- **非同期処理**: レスポンス時間の改善
- **イベント駆動**: リアクティブなシステム設計

---

## メッセージングパターン / Messaging Patterns

### 基本パターン / Basic Patterns

```yaml
messaging_patterns:
  point_to_point:
    description: "1対1の通信"
    use_cases:
      - "コマンド処理"
      - "タスク分散"
    implementation: "Queue"
    guarantees: "メッセージは1つのコンシューマーのみが処理"
  
  publish_subscribe:
    description: "1対多の通信"
    use_cases:
      - "イベント通知"
      - "データ同期"
    implementation: "Topic"
    guarantees: "すべてのサブスクライバーがメッセージを受信"
  
  request_reply:
    description: "同期的な要求応答"
    use_cases:
      - "RPC風の通信"
      - "クエリ処理"
    implementation: "Correlation ID + 応答キュー"
  
  event_sourcing:
    description: "イベントストリームとしてのデータ管理"
    use_cases:
      - "監査ログ"
      - "イベント再生"
    implementation: "Event Store"
  
  saga:
    description: "分散トランザクション"
    use_cases:
      - "複数サービス間の整合性"
      - "長時間実行トランザクション"
    implementation: "オーケストレーション / コレオグラフィ"
```

### アーキテクチャパターン / Architecture Patterns

```yaml
architecture_patterns:
  event_driven:
    description: "イベント駆動アーキテクチャ"
    components:
      - "Event Producers"
      - "Event Brokers"
      - "Event Consumers"
    benefits:
      - "リアルタイム処理"
      - "スケーラビリティ"
      - "疎結合"
  
  cqrs:
    description: "Command Query Responsibility Segregation"
    separation:
      write_model: "コマンド処理"
      read_model: "クエリ処理"
    sync_mechanism: "イベント"
  
  choreography:
    description: "コレオグラフィベースのSaga"
    characteristic: "分散制御"
    coordination: "イベント駆動"
  
  orchestration:
    description: "オーケストレーションベースのSaga"
    characteristic: "中央制御"
    coordination: "オーケストレーター"
```

---

## メッセージブローカー / Message Brokers

### Amazon SQS (標準 / Standard)

```yaml
message_broker:
  name: "Amazon SQS"
  type: "Managed Message Queue"
  status: "standard"
  
  queue_types:
    standard:
      throughput: "無制限"
      ordering: "ベストエフォート"
      duplication: "少なくとも1回配信"
      use_cases:
        - "高スループットが必要"
        - "順序保証が不要"
    
    fifo:
      throughput: "300 TPS (バッチ: 3000 TPS)"
      ordering: "厳密な順序保証"
      duplication: "正確に1回処理"
      use_cases:
        - "順序保証が必要"
        - "重複排除が必要"
  
  features:
    visibility_timeout: "0秒 ~ 12時間"
    message_retention: "1分 ~ 14日"
    max_message_size: "256 KB"
    delay_queues: "0秒 ~ 15分"
    dead_letter_queue: true
    long_polling: true
  
  pricing:
    model: "リクエスト数課金"
    free_tier: "100万リクエスト/月"
```

**SQS設定例 (Node.js)**:

```typescript
import { SQSClient, SendMessageCommand, ReceiveMessageCommand, DeleteMessageCommand } from '@aws-sdk/client-sqs';

const sqsClient = new SQSClient({ region: 'ap-northeast-1' });

// キュー設定
const QUEUE_CONFIG = {
  standard: {
    queueUrl: 'https://sqs.ap-northeast-1.amazonaws.com/123456789012/my-standard-queue',
    maxMessages: 10,
    visibilityTimeout: 300, // 5分
    waitTimeSeconds: 20, // ロングポーリング
  },
  fifo: {
    queueUrl: 'https://sqs.ap-northeast-1.amazonaws.com/123456789012/my-fifo-queue.fifo',
    maxMessages: 10,
    visibilityTimeout: 300,
    waitTimeSeconds: 20,
  },
};

// メッセージ送信
export class SQSProducer {
  async sendMessage(queueType: 'standard' | 'fifo', message: any): Promise<void> {
    const config = QUEUE_CONFIG[queueType];
    
    const params: any = {
      QueueUrl: config.queueUrl,
      MessageBody: JSON.stringify(message),
      MessageAttributes: {
        Timestamp: {
          DataType: 'Number',
          StringValue: Date.now().toString(),
        },
        Source: {
          DataType: 'String',
          StringValue: 'my-service',
        },
      },
    };

    // FIFOキューの場合
    if (queueType === 'fifo') {
      params.MessageGroupId = message.userId || 'default-group';
      params.MessageDeduplicationId = message.id || `${Date.now()}-${Math.random()}`;
    }

    try {
      const command = new SendMessageCommand(params);
      const result = await sqsClient.send(command);
      console.log('Message sent:', result.MessageId);
    } catch (error) {
      console.error('Failed to send message:', error);
      throw error;
    }
  }

  async sendBatch(queueType: 'standard' | 'fifo', messages: any[]): Promise<void> {
    // 最大10メッセージまでバッチ送信可能
    const batches = this.chunkArray(messages, 10);
    
    for (const batch of batches) {
      const entries = batch.map((msg, index) => ({
        Id: `msg-${index}`,
        MessageBody: JSON.stringify(msg),
        MessageAttributes: {
          Timestamp: {
            DataType: 'Number',
            StringValue: Date.now().toString(),
          },
        },
        ...(queueType === 'fifo' && {
          MessageGroupId: msg.userId || 'default-group',
          MessageDeduplicationId: msg.id || `${Date.now()}-${index}`,
        }),
      }));

      // SendMessageBatch実装
    }
  }

  private chunkArray<T>(array: T[], size: number): T[][] {
    return Array.from({ length: Math.ceil(array.length / size) }, (_, i) =>
      array.slice(i * size, i * size + size)
    );
  }
}

// メッセージ受信
export class SQSConsumer {
  private isRunning = false;

  async start(queueType: 'standard' | 'fifo', handler: (message: any) => Promise<void>): Promise<void> {
    this.isRunning = true;
    const config = QUEUE_CONFIG[queueType];

    while (this.isRunning) {
      try {
        const command = new ReceiveMessageCommand({
          QueueUrl: config.queueUrl,
          MaxNumberOfMessages: config.maxMessages,
          VisibilityTimeout: config.visibilityTimeout,
          WaitTimeSeconds: config.waitTimeSeconds,
          MessageAttributeNames: ['All'],
        });

        const result = await sqsClient.send(command);

        if (result.Messages) {
          await Promise.all(
            result.Messages.map(async (msg) => {
              try {
                const body = JSON.parse(msg.Body!);
                await handler(body);

                // 処理成功後、メッセージ削除
                await this.deleteMessage(config.queueUrl, msg.ReceiptHandle!);
              } catch (error) {
                console.error('Message processing failed:', error);
                // Visibility timeoutが切れると自動的に再度キューに戻る
              }
            })
          );
        }
      } catch (error) {
        console.error('Failed to receive messages:', error);
        await this.sleep(5000); // エラー時は5秒待機
      }
    }
  }

  stop(): void {
    this.isRunning = false;
  }

  private async deleteMessage(queueUrl: string, receiptHandle: string): Promise<void> {
    const command = new DeleteMessageCommand({
      QueueUrl: queueUrl,
      ReceiptHandle: receiptHandle,
    });
    await sqsClient.send(command);
  }

  private sleep(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}

// 使用例
const producer = new SQSProducer();
await producer.sendMessage('standard', {
  userId: '123',
  action: 'send_email',
  data: {
    to: 'user@example.com',
    subject: 'Welcome!',
  },
});

const consumer = new SQSConsumer();
await consumer.start('standard', async (message) => {
  console.log('Processing message:', message);
  // メッセージ処理ロジック
  await processMessage(message);
});
```

**Dead Letter Queue (DLQ) 設定**:

```json
{
  "RedrivePolicy": {
    "deadLetterTargetArn": "arn:aws:sqs:ap-northeast-1:123456789012:my-dlq",
    "maxReceiveCount": 3
  }
}
```

### Amazon SNS (標準 / Standard)

```yaml
pub_sub:
  name: "Amazon SNS"
  type: "Pub/Sub Messaging"
  status: "standard"
  
  topic_types:
    standard:
      description: "標準トピック"
      use_cases: ["イベント通知", "ファンアウト"]
    
    fifo:
      description: "FIFOトピック"
      use_cases: ["順序保証が必要なイベント"]
  
  subscriptions:
    - "SQS"
    - "Lambda"
    - "HTTP/HTTPS"
    - "Email"
    - "SMS"
  
  features:
    message_filtering: true
    message_attributes: true
    delivery_retry: true
    dlq_support: true
```

**SNS設定例**:

```typescript
import { SNSClient, PublishCommand, SubscribeCommand } from '@aws-sdk/client-sns';

const snsClient = new SNSClient({ region: 'ap-northeast-1' });

export class SNSPublisher {
  async publishEvent(topicArn: string, event: any): Promise<void> {
    const command = new PublishCommand({
      TopicArn: topicArn,
      Message: JSON.stringify(event),
      Subject: event.type,
      MessageAttributes: {
        eventType: {
          DataType: 'String',
          StringValue: event.type,
        },
        timestamp: {
          DataType: 'Number',
          StringValue: Date.now().toString(),
        },
        source: {
          DataType: 'String',
          StringValue: 'my-service',
        },
      },
    });

    try {
      const result = await snsClient.send(command);
      console.log('Event published:', result.MessageId);
    } catch (error) {
      console.error('Failed to publish event:', error);
      throw error;
    }
  }

  async publishBatch(topicArn: string, events: any[]): Promise<void> {
    // PublishBatch API使用
    const entries = events.map((event, index) => ({
      Id: `event-${index}`,
      Message: JSON.stringify(event),
      Subject: event.type,
      MessageAttributes: {
        eventType: {
          DataType: 'String',
          StringValue: event.type,
        },
      },
    }));

    // 実装詳細...
  }
}

// SNS + SQSファンアウトパターン
export async function setupFanout(topicArn: string, queueArn: string): Promise<void> {
  const command = new SubscribeCommand({
    TopicArn: topicArn,
    Protocol: 'sqs',
    Endpoint: queueArn,
    Attributes: {
      // メッセージフィルタリング
      FilterPolicy: JSON.stringify({
        eventType: ['user.created', 'user.updated'],
      }),
      RawMessageDelivery: 'true',
    },
  });

  await snsClient.send(command);
}
```

### RabbitMQ (承認済み / Approved)

```yaml
message_broker:
  name: "RabbitMQ"
  type: "Advanced Message Broker"
  status: "approved"
  use_cases:
    - "複雑なルーティング要件"
    - "オンプレミス環境"
    - "AMQP標準準拠が必要"
  
  deployment:
    managed: "Amazon MQ for RabbitMQ"
    self_hosted: "EKS / ECS"
  
  features:
    exchanges:
      - "Direct"
      - "Topic"
      - "Fanout"
      - "Headers"
    routing: "柔軟なメッセージルーティング"
    clustering: true
    high_availability: true
```

---

## イベントストリーミング / Event Streaming

### Amazon Kinesis Data Streams (標準 / Standard)

```yaml
event_streaming:
  name: "Amazon Kinesis Data Streams"
  status: "standard"
  
  characteristics:
    throughput: "1 MB/秒/シャード (書き込み)"
    retention: "24時間 ~ 365日"
    ordering: "パーティションキー単位で保証"
  
  use_cases:
    - "リアルタイムデータストリーミング"
    - "ログ集約"
    - "イベントソーシング"
    - "リアルタイム分析"
  
  scaling:
    shard_management: "手動 or Auto Scaling"
    on_demand_mode: "自動スケーリング (推奨)"
```

**Kinesis Producer/Consumer例**:

```typescript
import { KinesisClient, PutRecordCommand, PutRecordsCommand } from '@aws-sdk/client-kinesis';
import { Consumer } from '@aws-sdk/lib-kinesis-consumer';

const kinesisClient = new KinesisClient({ region: 'ap-northeast-1' });

// プロデューサー
export class KinesisProducer {
  async putRecord(streamName: string, event: any, partitionKey: string): Promise<void> {
    const command = new PutRecordCommand({
      StreamName: streamName,
      Data: Buffer.from(JSON.stringify(event)),
      PartitionKey: partitionKey, // 同じキーは同じシャードへ
    });

    try {
      const result = await kinesisClient.send(command);
      console.log('Record sent to shard:', result.ShardId);
    } catch (error) {
      console.error('Failed to put record:', error);
      throw error;
    }
  }

  async putRecordsBatch(streamName: string, events: Array<{ data: any; partitionKey: string }>): Promise<void> {
    const records = events.map((event) => ({
      Data: Buffer.from(JSON.stringify(event.data)),
      PartitionKey: event.partitionKey,
    }));

    // 最大500レコード、合計5MBまで
    const batches = this.chunkRecords(records, 500);

    for (const batch of batches) {
      const command = new PutRecordsCommand({
        StreamName: streamName,
        Records: batch,
      });

      const result = await kinesisClient.send(command);
      
      if (result.FailedRecordCount && result.FailedRecordCount > 0) {
        console.warn(`${result.FailedRecordCount} records failed`);
        // 失敗したレコードの再試行ロジック
      }
    }
  }

  private chunkRecords(records: any[], size: number): any[][] {
    return Array.from({ length: Math.ceil(records.length / size) }, (_, i) =>
      records.slice(i * size, i * size + size)
    );
  }
}

// コンシューマー (KCL使用)
export class KinesisConsumer {
  private consumer: Consumer;

  constructor(streamName: string, consumerName: string) {
    this.consumer = new Consumer({
      streamName,
      region: 'ap-northeast-1',
      shardIteratorType: 'TRIM_HORIZON',
      consumerName,
      processRecords: async (records) => {
        for (const record of records) {
          try {
            const data = JSON.parse(record.data.toString());
            await this.processRecord(data);
          } catch (error) {
            console.error('Failed to process record:', error);
          }
        }
      },
    });
  }

  async start(): Promise<void> {
    this.consumer.start();
  }

  stop(): void {
    this.consumer.stop();
  }

  private async processRecord(data: any): Promise<void> {
    console.log('Processing record:', data);
    // レコード処理ロジック
  }
}

// 使用例
const producer = new KinesisProducer();
await producer.putRecord('my-stream', {
  userId: '123',
  action: 'page_view',
  timestamp: Date.now(),
}, '123'); // userIdをパーティションキーに使用

const consumer = new KinesisConsumer('my-stream', 'my-consumer-app');
await consumer.start();
```

### Apache Kafka (承認済み / Approved)

```yaml
event_streaming:
  name: "Apache Kafka"
  status: "approved"
  deployment: "Amazon MSK (Managed Streaming for Kafka)"
  
  use_cases:
    - "大規模イベントストリーミング"
    - "ログ集約"
    - "ストリーム処理"
  
  features:
    retention: "設定可能 (時間 or サイズベース)"
    replication: "高可用性"
    partitioning: "スケーラビリティ"
    exactly_once_semantics: true
  
  client_libraries:
    - "KafkaJS (Node.js)"
    - "confluent-kafka-python (Python)"
    - "Sarama (Go)"
```

**Kafka設定例 (KafkaJS)**:

```typescript
import { Kafka, Producer, Consumer, EachMessagePayload } from 'kafkajs';

const kafka = new Kafka({
  clientId: 'my-app',
  brokers: ['broker1:9092', 'broker2:9092', 'broker3:9092'],
  ssl: true,
  sasl: {
    mechanism: 'plain',
    username: process.env.KAFKA_USERNAME!,
    password: process.env.KAFKA_PASSWORD!,
  },
});

// プロデューサー
export class KafkaProducer {
  private producer: Producer;

  constructor() {
    this.producer = kafka.producer({
      allowAutoTopicCreation: false,
      transactionTimeout: 30000,
    });
  }

  async connect(): Promise<void> {
    await this.producer.connect();
  }

  async sendMessage(topic: string, message: any, key?: string): Promise<void> {
    await this.producer.send({
      topic,
      messages: [
        {
          key: key || null,
          value: JSON.stringify(message),
          headers: {
            timestamp: Date.now().toString(),
            source: 'my-service',
          },
        },
      ],
    });
  }

  async sendBatch(topic: string, messages: Array<{ key?: string; value: any }>): Promise<void> {
    await this.producer.sendBatch({
      topicMessages: [
        {
          topic,
          messages: messages.map((msg) => ({
            key: msg.key || null,
            value: JSON.stringify(msg.value),
            headers: {
              timestamp: Date.now().toString(),
            },
          })),
        },
      ],
    });
  }

  async disconnect(): Promise<void> {
    await this.producer.disconnect();
  }
}

// コンシューマー
export class KafkaConsumer {
  private consumer: Consumer;

  constructor(groupId: string) {
    this.consumer = kafka.consumer({
      groupId,
      sessionTimeout: 30000,
      heartbeatInterval: 3000,
    });
  }

  async connect(): Promise<void> {
    await this.consumer.connect();
  }

  async subscribe(topics: string[]): Promise<void> {
    await this.consumer.subscribe({
      topics,
      fromBeginning: false,
    });
  }

  async run(handler: (payload: EachMessagePayload) => Promise<void>): Promise<void> {
    await this.consumer.run({
      eachMessage: async (payload) => {
        try {
          await handler(payload);
        } catch (error) {
          console.error('Message processing failed:', error);
          // エラーハンドリング
        }
      },
    });
  }

  async disconnect(): Promise<void> {
    await this.consumer.disconnect();
  }
}

// 使用例
const producer = new KafkaProducer();
await producer.connect();
await producer.sendMessage('user-events', {
  type: 'user.created',
  userId: '123',
  timestamp: Date.now(),
}, '123'); // キーでパーティショニング

const consumer = new KafkaConsumer('my-consumer-group');
await consumer.connect();
await consumer.subscribe(['user-events', 'order-events']);
await consumer.run(async ({ topic, partition, message }) => {
  const value = JSON.parse(message.value!.toString());
  console.log(`Received message from ${topic}[${partition}]:`, value);
  // メッセージ処理
});
```

---

## Pub/Subメッセージング / Pub/Sub Messaging

### Redis Pub/Sub (推奨 / Recommended)

```yaml
pub_sub:
  name: "Redis Pub/Sub"
  status: "recommended"
  use_cases:
    - "リアルタイム通知"
    - "キャッシュ無効化"
    - "アプリケーション内通信"
  
  characteristics:
    persistence: false  # メッセージは永続化されない
    delivery_guarantee: "At most once"
    use_for: "一時的なイベント通知"
  
  patterns:
    pub_sub: "チャンネルベース"
    pattern_subscribe: "ワイルドカード購読"
```

**Redis Pub/Sub実装**:

```typescript
import Redis from 'ioredis';

const publisher = new Redis({
  host: process.env.REDIS_HOST,
  port: parseInt(process.env.REDIS_PORT || '6379'),
});

const subscriber = new Redis({
  host: process.env.REDIS_HOST,
  port: parseInt(process.env.REDIS_PORT || '6379'),
});

// パブリッシャー
export class RedisPubSub {
  async publish(channel: string, message: any): Promise<void> {
    const payload = JSON.stringify({
      ...message,
      timestamp: Date.now(),
    });

    const subscriberCount = await publisher.publish(channel, payload);
    console.log(`Published to ${subscriberCount} subscribers`);
  }

  async subscribe(channels: string[], handler: (channel: string, message: any) => void): Promise<void> {
    await subscriber.subscribe(...channels);

    subscriber.on('message', (channel, message) => {
      try {
        const data = JSON.parse(message);
        handler(channel, data);
      } catch (error) {
        console.error('Failed to parse message:', error);
      }
    });
  }

  async psubscribe(patterns: string[], handler: (pattern: string, channel: string, message: any) => void): Promise<void> {
    await subscriber.psubscribe(...patterns);

    subscriber.on('pmessage', (pattern, channel, message) => {
      try {
        const data = JSON.parse(message);
        handler(pattern, channel, data);
      } catch (error) {
        console.error('Failed to parse message:', error);
      }
    });
  }

  async unsubscribe(channels: string[]): Promise<void> {
    await subscriber.unsubscribe(...channels);
  }
}

// 使用例
const pubsub = new RedisPubSub();

// パブリッシュ
await pubsub.publish('notifications', {
  type: 'new_message',
  userId: '123',
  content: 'Hello!',
});

// サブスクライブ
await pubsub.subscribe(['notifications', 'alerts'], (channel, message) => {
  console.log(`Received on ${channel}:`, message);
});

// パターンサブスクライブ
await pubsub.psubscribe(['user:*', 'order:*'], (pattern, channel, message) => {
  console.log(`Matched pattern ${pattern} on ${channel}:`, message);
});
```

---

## ジョブキュー / Job Queues

### BullMQ (標準 / Standard)

```yaml
job_queue:
  name: "BullMQ"
  status: "standard"
  backend: "Redis"
  
  features:
    - "優先度付きジョブ"
    - "遅延ジョブ"
    - "スケジュールジョブ"
    - "リトライ戦略"
    - "レート制限"
    - "ジョブの依存関係"
  
  monitoring:
    - "Bull Board (UI)"
    - "Prometheus metrics"
```

**詳細はbackend-stack.mdを参照**

---

## リアルタイム通信 / Real-time Communication

### WebSocket

```yaml
realtime:
  protocol: "WebSocket"
  status: "standard"
  
  implementations:
    socket_io:
      version: "4.x"
      features:
        - "自動再接続"
        - "ルームとネームスペース"
        - "ブロードキャスト"
    
    ws:
      version: "8.x"
      use_case: "低レベルWebSocket"
  
  aws_integration:
    service: "API Gateway WebSocket"
    backend: "Lambda / ECS"
```

**Socket.IO設定例**:

```typescript
import { Server } from 'socket.io';
import { createAdapter } from '@socket.io/redis-adapter';
import Redis from 'ioredis';

const pubClient = new Redis(process.env.REDIS_URL!);
const subClient = pubClient.duplicate();

const io = new Server(httpServer, {
  cors: {
    origin: process.env.ALLOWED_ORIGINS?.split(','),
    credentials: true,
  },
  adapter: createAdapter(pubClient, subClient),
});

// 認証ミドルウェア
io.use(async (socket, next) => {
  try {
    const token = socket.handshake.auth.token;
    const user = await verifyToken(token);
    socket.data.user = user;
    next();
  } catch (error) {
    next(new Error('Authentication failed'));
  }
});

// 接続処理
io.on('connection', (socket) => {
  console.log('User connected:', socket.data.user.id);

  // ユーザー専用ルームに参加
  socket.join(`user:${socket.data.user.id}`);

  // メッセージ受信
  socket.on('send_message', async (data) => {
    // メッセージ処理
    const message = await saveMessage(data);

    // 特定ルームにブロードキャスト
    io.to(`room:${data.roomId}`).emit('new_message', message);
  });

  // 切断処理
  socket.on('disconnect', () => {
    console.log('User disconnected:', socket.data.user.id);
  });
});

// 外部からの通知送信 (Redis Pub/Sub経由)
export async function sendNotification(userId: string, notification: any): Promise<void> {
  io.to(`user:${userId}`).emit('notification', notification);
}
```

### Server-Sent Events (SSE)

```yaml
realtime:
  protocol: "Server-Sent Events"
  status: "approved"
  
  use_cases:
    - "一方向のリアルタイム更新"
    - "進捗通知"
    - "ライブフィード"
  
  advantages:
    - "HTTP/HTTPSベース"
    - "自動再接続"
    - "シンプルな実装"
```

**SSE実装例**:

```typescript
import { Response } from 'express';

export class SSEConnection {
  private response: Response;
  private clientId: string;

  constructor(res: Response, clientId: string) {
    this.response = res;
    this.clientId = clientId;

    // SSEヘッダー設定
    res.setHeader('Content-Type', 'text/event-stream');
    res.setHeader('Cache-Control', 'no-cache');
    res.setHeader('Connection', 'keep-alive');
    res.setHeader('X-Accel-Buffering', 'no'); // Nginxバッファリング無効化

    // 初期接続確認
    this.send('connected', { clientId });
  }

  send(event: string, data: any): void {
    this.response.write(`event: ${event}\n`);
    this.response.write(`data: ${JSON.stringify(data)}\n\n`);
  }

  sendComment(comment: string): void {
    this.response.write(`: ${comment}\n\n`);
  }

  close(): void {
    this.response.end();
  }
}

// Express ルート
app.get('/events', authenticate, (req, res) => {
  const connection = new SSEConnection(res, req.user.id);

  // Redis Pub/Subでイベント購読
  const subscriber = new Redis();
  subscriber.subscribe(`user:${req.user.id}`);

  subscriber.on('message', (channel, message) => {
    const data = JSON.parse(message);
    connection.send(data.type, data);
  });

  // キープアライブ (30秒ごと)
  const keepAliveInterval = setInterval(() => {
    connection.sendComment('keep-alive');
  }, 30000);

  // 切断処理
  req.on('close', () => {
    clearInterval(keepAliveInterval);
    subscriber.unsubscribe();
    subscriber.quit();
    connection.close();
  });
});
```

---

## メッセージフォーマット / Message Formats

### JSON (標準 / Standard)

```yaml
message_format:
  name: "JSON"
  status: "standard"
  
  schema:
    envelope:
      id: "メッセージID (UUID)"
      type: "メッセージタイプ"
      timestamp: "ISO 8601形式"
      source: "送信元サービス"
      data: "ペイロード"
      metadata: "メタデータ"
```

**標準メッセージ構造**:

```typescript
interface Message<T = any> {
  id: string;                    // UUID v4
  type: string;                  // 'user.created', 'order.placed'
  timestamp: string;             // ISO 8601
  source: string;                // 'user-service', 'order-service'
  specversion: string;           // '1.0'
  data: T;                       // ペイロード
  metadata?: Record<string, any>; // 追加メタデータ
  correlationId?: string;        // リクエスト追跡用
  causationId?: string;          // 原因となったメッセージID
}

// 使用例
const message: Message<UserCreatedPayload> = {
  id: '550e8400-e29b-41d4-a716-446655440000',
  type: 'user.created',
  timestamp: '2025-01-15T10:30:00.000Z',
  source: 'user-service',
  specversion: '1.0',
  data: {
    userId: '123',
    email: 'user@example.com',
    name: 'John Doe',
  },
  metadata: {
    region: 'ap-northeast-1',
    version: '1.0.0',
  },
  correlationId: 'req-abc123',
};
```

### Protocol Buffers (承認済み / Approved)

```yaml
message_format:
  name: "Protocol Buffers"
  status: "approved"
  use_cases:
    - "高パフォーマンスが必要"
    - "型安全性が重要"
    - "gRPC通信"
  
  advantages:
    - "コンパクトなバイナリフォーマット"
    - "スキーマ進化サポート"
    - "多言語対応"
```

### Apache Avro (評価中 / Under Evaluation)

```yaml
message_format:
  name: "Apache Avro"
  status: "under_evaluation"
  use_cases:
    - "Kafkaとの統合"
    - "スキーマレジストリ"
```

---

## エラーハンドリングと再試行 / Error Handling and Retries

### 再試行戦略 / Retry Strategies

```yaml
retry_strategies:
  exponential_backoff:
    description: "指数バックオフ"
    formula: "delay = base_delay * (2 ^ attempt)"
    recommended: true
    max_attempts: 5
    example:
      - "1秒"
      - "2秒"
      - "4秒"
      - "8秒"
      - "16秒"
  
  fixed_delay:
    description: "固定遅延"
    use_case: "予測可能な再試行が必要"
  
  jittered_backoff:
    description: "ジッターつき指数バックオフ"
    formula: "delay = random(0, base_delay * (2 ^ attempt))"
    advantage: "サンダリングハード問題回避"
```

**再試行実装例**:

```typescript
export class RetryStrategy {
  async withExponentialBackoff<T>(
    fn: () => Promise<T>,
    options: {
      maxAttempts?: number;
      baseDelay?: number;
      maxDelay?: number;
      jitter?: boolean;
    } = {}
  ): Promise<T> {
    const {
      maxAttempts = 5,
      baseDelay = 1000,
      maxDelay = 30000,
      jitter = true,
    } = options;

    let lastError: Error;

    for (let attempt = 0; attempt < maxAttempts; attempt++) {
      try {
        return await fn();
      } catch (error) {
        lastError = error as Error;

        if (attempt === maxAttempts - 1) {
          break;
        }

        // 再試行不可能なエラーは即座に失敗
        if (this.isNonRetryableError(error)) {
          throw error;
        }

        let delay = Math.min(baseDelay * Math.pow(2, attempt), maxDelay);

        // ジッター追加
        if (jitter) {
          delay = Math.random() * delay;
        }

        console.log(`Retry attempt ${attempt + 1}/${maxAttempts} after ${delay}ms`);
        await this.sleep(delay);
      }
    }

    throw lastError!;
  }

  private isNonRetryableError(error: any): boolean {
    // 400番台エラーは再試行しない
    if (error.statusCode >= 400 && error.statusCode < 500) {
      return true;
    }

    // 認証エラーは再試行しない
    if (error.code === 'AUTHENTICATION_ERROR') {
      return true;
    }

    return false;
  }

  private sleep(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}

// 使用例
const retry = new RetryStrategy();

await retry.withExponentialBackoff(
  async () => {
    return await sendMessage(message);
  },
  {
    maxAttempts: 5,
    baseDelay: 1000,
    maxDelay: 30000,
    jitter: true,
  }
);
```

### Dead Letter Queue (DLQ)

```yaml
dlq:
  purpose: "処理失敗メッセージの隔離"
  
  configuration:
    max_receive_count: 3  # 3回失敗後DLQへ
    retention: "14日"
    alerts: true
  
  processing:
    - "手動レビュー"
    - "データ修正後再処理"
    - "ログ分析"
  
  monitoring:
    - "DLQメッセージ数アラート"
    - "定期的なDLQチェック"
```

---

## 監視とトレーシング / Monitoring and Tracing

### メトリクス / Metrics

```yaml
metrics:
  queue_metrics:
    - "メッセージ送信数"
    - "メッセージ受信数"
    - "処理時間"
    - "エラー率"
    - "キュー深度"
    - "DLQメッセージ数"
  
  consumer_metrics:
    - "処理スループット"
    - "ラグ (遅延)"
    - "リトライ回数"
  
  tools:
    - "CloudWatch Metrics"
    - "Prometheus"
    - "Datadog"
```

**メトリクス計装例**:

```typescript
import { Counter, Histogram, Gauge } from 'prom-client';

export class MessagingMetrics {
  private messagesSent: Counter;
  private messagesReceived: Counter;
  private processingDuration: Histogram;
  private queueDepth: Gauge;
  private errors: Counter;

  constructor() {
    this.messagesSent = new Counter({
      name: 'messaging_messages_sent_total',
      help: 'Total number of messages sent',
      labelNames: ['queue', 'type'],
    });

    this.messagesReceived = new Counter({
      name: 'messaging_messages_received_total',
      help: 'Total number of messages received',
      labelNames: ['queue', 'type'],
    });

    this.processingDuration = new Histogram({
      name: 'messaging_processing_duration_seconds',
      help: 'Message processing duration',
      labelNames: ['queue', 'type'],
      buckets: [0.1, 0.5, 1, 2, 5, 10],
    });

    this.queueDepth = new Gauge({
      name: 'messaging_queue_depth',
      help: 'Current queue depth',
      labelNames: ['queue'],
    });

    this.errors = new Counter({
      name: 'messaging_errors_total',
      help: 'Total number of errors',
      labelNames: ['queue', 'type', 'error_type'],
    });
  }

  recordSent(queue: string, type: string): void {
    this.messagesSent.inc({ queue, type });
  }

  recordReceived(queue: string, type: string): void {
    this.messagesReceived.inc({ queue, type });
  }

  recordProcessingTime(queue: string, type: string, durationSeconds: number): void {
    this.processingDuration.observe({ queue, type }, durationSeconds);
  }

  setQueueDepth(queue: string, depth: number): void {
    this.queueDepth.set({ queue }, depth);
  }

  recordError(queue: string, type: string, errorType: string): void {
    this.errors.inc({ queue, type, error_type: errorType });
  }
}
```

### 分散トレーシング / Distributed Tracing

```yaml
tracing:
  standard: "OpenTelemetry"
  
  context_propagation:
    - "Trace ID"
    - "Span ID"
    - "Correlation ID"
  
  backends:
    - "AWS X-Ray"
    - "Jaeger"
    - "Zipkin"
```

**トレーシング実装**:

```typescript
import { trace, context, SpanStatusCode } from '@opentelemetry/api';

const tracer = trace.getTracer('messaging-service');

export async function sendMessageWithTracing(queue: string, message: any): Promise<void> {
  const span = tracer.startSpan('send_message', {
    attributes: {
      'messaging.system': 'sqs',
      'messaging.destination': queue,
      'messaging.operation': 'send',
    },
  });

  try {
    // トレースコンテキストをメッセージに埋め込む
    const traceContext = {
      traceId: span.spanContext().traceId,
      spanId: span.spanContext().spanId,
    };

    const enrichedMessage = {
      ...message,
      _trace: traceContext,
    };

    await sendToQueue(queue, enrichedMessage);

    span.setStatus({ code: SpanStatusCode.OK });
  } catch (error) {
    span.setStatus({
      code: SpanStatusCode.ERROR,
      message: (error as Error).message,
    });
    span.recordException(error as Error);
    throw error;
  } finally {
    span.end();
  }
}
```

---

## セキュリティ / Security

### メッセージ暗号化 / Message Encryption

```yaml
security:
  encryption:
    at_rest:
      - "SQS: KMS暗号化"
      - "Kinesis: KMS暗号化"
      - "Kafka: ディスク暗号化"
    
    in_transit:
      - "TLS 1.2+"
      - "mTLS (相互TLS)"
  
  authentication:
    - "IAM認証 (AWS)"
    - "SASL (Kafka)"
    - "Token-based認証"
  
  authorization:
    - "IAMポリシー"
    - "ACL (Access Control List)"
```

### メッセージ検証 / Message Validation

```yaml
validation:
  schema_validation:
    tool: "JSON Schema / Zod"
    enforcement: "すべての受信メッセージ"
  
  signature_verification:
    use_case: "外部システムからのメッセージ"
    method: "HMAC SHA-256"
```

**メッセージ検証実装**:

```typescript
import { z } from 'zod';
import crypto from 'crypto';

// スキーマ定義
const userCreatedSchema = z.object({
  id: z.string().uuid(),
  type: z.literal('user.created'),
  timestamp: z.string().datetime(),
  source: z.string(),
  data: z.object({
    userId: z.string(),
    email: z.string().email(),
    name: z.string(),
  }),
});

export class MessageValidator {
  validate<T>(message: unknown, schema: z.ZodSchema<T>): T {
    try {
      return schema.parse(message);
    } catch (error) {
      if (error instanceof z.ZodError) {
        throw new ValidationError('Invalid message format', error.errors);
      }
      throw error;
    }
  }

  verifySignature(message: string, signature: string, secret: string): boolean {
    const expectedSignature = crypto
      .createHmac('sha256', secret)
      .update(message)
      .digest('hex');

    return crypto.timingSafeEqual(
      Buffer.from(signature),
      Buffer.from(expectedSignature)
    );
  }
}

// 使用例
const validator = new MessageValidator();

const message = await receiveMessage();

// スキーマ検証
const validatedMessage = validator.validate(message, userCreatedSchema);

// 署名検証
if (!validator.verifySignature(
  JSON.stringify(message),
  message.signature,
  process.env.WEBHOOK_SECRET!
)) {
  throw new Error('Invalid signature');
}
```

---

## ベストプラクティス / Best Practices

### 設計原則 / Design Principles

1. **冪等性 (Idempotency)**
   - すべてのメッセージハンドラーは冪等に設計
   - 重複メッセージの処理に対応
   - Idempotency Keyの使用

2. **メッセージの小ささ**
   - メッセージサイズは最小限に
   - 大きなペイロードはS3に保存し、参照を送信

3. **スキーマ進化**
   - 後方互換性の維持
   - バージョニング戦略

4. **タイムアウト設定**
   - 適切なVisibility Timeout
   - 処理時間の監視

5. **エラーハンドリング**
   - Dead Letter Queue活用
   - 適切なリトライ戦略
   - エラー通知

### パフォーマンス最適化 / Performance Optimization

1. **バッチ処理**
   - 可能な限りバッチAPI使用
   - コスト削減とスループット向上

2. **並行処理**
   - 複数ワーカーの並列実行
   - 適切な並行数の設定

3. **接続プーリング**
   - 接続の再利用
   - コネクション数の最適化

4. **キャッシング**
   - 頻繁に使用するデータのキャッシュ
   - スキーマキャッシング

---

## 参考資料 / References

### 公式ドキュメント / Official Documentation

- [Amazon SQS Documentation](https://docs.aws.amazon.com/sqs/)
- [Amazon SNS Documentation](https://docs.aws.amazon.com/sns/)
- [Amazon Kinesis Documentation](https://docs.aws.amazon.com/kinesis/)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [BullMQ Documentation](https://docs.bullmq.io/)

### パターンとプラクティス / Patterns and Practices

- [Enterprise Integration Patterns](https://www.enterpriseintegrationpatterns.com/)
- [Microservices Patterns (Chris Richardson)](https://microservices.io/patterns/)
- [AWS Messaging Best Practices](https://aws.amazon.com/messaging/)

### ツールとライブラリ / Tools and Libraries

- [KafkaJS](https://kafka.js.org/)
- [ioredis](https://github.com/luin/ioredis)
- [Socket.IO](https://socket.io/)
- [AWS SDK for JavaScript](https://aws.amazon.com/sdk-for-javascript/)

---

## バージョン履歴 / Version History

| バージョン | 日付 | 変更内容 | 承認者 |
|----------|------|---------|--------|
| 2.0.0 | 2025-01-15 | メッセージング技術スタック標準策定 | Backend Architecture Team |
| 1.5.0 | 2024-12-01 | Kinesis追加、Kafka承認 | CTO |
| 1.0.0 | 2024-10-01 | 初版リリース | Engineering Manager |

---

## 承認 / Approval

| 役割 | 氏名 | 承認日 | 署名 |
|------|------|--------|------|
| CTO | [Name] | 2025-01-15 | [Signature] |
| Backend Lead | [Name] | 2025-01-15 | [Signature] |
| Architecture Lead | [Name] | 2025-01-15 | [Signature] |
| DevOps Lead | [Name] | 2025-01-15 | [Signature] |

---

## お問い合わせ / Contact

**Backend Architecture Team**
- Email: backend-team@company.com
- Slack: #backend-architecture
- 担当者: [Tech Lead Name]

---
