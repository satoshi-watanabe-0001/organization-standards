# 技術用語集 (Glossary)

> **Version**: 2.0.0  
> **Last Updated**: 2025-10-28  
> **Status**: Active

## 📋 目次

- [概要](#概要)
- [用語集](#用語集)
  - [A](#a)
  - [B](#b)
  - [C](#c)
  - [D](#d)
  - [E](#e)
  - [F](#f)
  - [G](#g)
  - [H](#h)
  - [I](#i)
  - [J](#j)
  - [K](#k)
  - [L](#l)
  - [M](#m)
  - [N](#n)
  - [O](#o)
  - [P](#p)
  - [Q](#q)
  - [R](#r)
  - [S](#s)
  - [T](#t)
  - [U](#u)
  - [V](#v)
  - [W](#w)
  - [X](#x)
  - [Y](#y)
  - [Z](#z)
- [カテゴリ別索引](#カテゴリ別索引)

---

## 概要

この用語集は、組織内で使用される技術用語の標準的な定義を提供します。各用語には、カテゴリ、定義、組織固有の使用例、関連用語が含まれています。

### 使用方法

1. アルファベット順に用語を検索
2. カテゴリ別索引から関連用語を探索
3. 関連用語のリンクをたどって理解を深める

---

## 用語集

### A

#### ADR (Architecture Decision Record)

**カテゴリ**: アーキテクチャ  
**定義**: アーキテクチャに関する重要な決定を記録するドキュメント。決定の背景、検討した選択肢、選択した理由、影響を明確に記録します。

**組織での使用例**:
```markdown
# ADR-001: マイクロサービスアーキテクチャの採用

## ステータス
承認済み

## 背景
モノリシックアーキテクチャでは、独立したデプロイとスケーリングが困難になっている。

## 決定
マイクロサービスアーキテクチャを採用し、各サービスを独立してデプロイ可能にする。

## 結果
- 各チームが独立して開発・デプロイ可能
- システムの複雑性が増加
- 運用コストの増加
```

**関連用語**: [Architecture](#architecture), [Design Document](#design-document), [Technical Proposal](#technical-proposal)

---

#### API (Application Programming Interface)

**カテゴリ**: アーキテクチャ  
**定義**: ソフトウェアコンポーネント間の通信インターフェース。RESTful API、GraphQL、gRPCなどの形式があります。

**組織での使用例**:
- RESTful API: 外部向けサービス（OpenAPI 3.0仕様書で定義）
- GraphQL: モバイルアプリ向けAPI
- gRPC: 内部マイクロサービス間通信

**推奨ツール**:
- OpenAPI仕様書作成: Swagger Editor
- APIテスト: Postman, Insomnia
- モックサーバー: Prism

**関連用語**: [RESTful API](#restful-api), [GraphQL](#graphql), [Microservices](#microservices)

---

#### Agile

**カテゴリ**: プロセス  
**定義**: 反復的かつ段階的なソフトウェア開発手法。短いスプリント（通常1〜4週間）で価値を提供し、継続的にフィードバックを受けて改善します。

**組織での使用例**:
- 2週間スプリント
- 毎日のスタンドアップミーティング
- スプリント計画、レビュー、レトロスペクティブ

**関連用語**: [Scrum](#scrum), [Sprint](#sprint), [Kanban](#kanban)

---

### B

#### Backward Compatibility

**カテゴリ**: アーキテクチャ  
**定義**: 新しいバージョンが以前のバージョンと互換性を保つ設計原則。APIやデータフォーマットの変更時に重要です。

**組織での使用例**:
```yaml
# API バージョニング戦略
strategy: URL-based
pattern: /api/v{version}/{resource}
deprecation_period: 6 months
example: /api/v1/users, /api/v2/users
```

**関連用語**: [API Versioning](#api-versioning), [Breaking Change](#breaking-change), [Deprecation](#deprecation)

---

#### Blue-Green Deployment

**カテゴリ**: DevOps  
**定義**: 2つの本番環境（BlueとGreen）を用意し、一方で新バージョンをデプロイ・テストした後、トラフィックを切り替えるデプロイ戦略。

**組織での使用例**:
```yaml
# Blue-Green デプロイメント設定
environments:
  blue:
    status: active
    version: v1.2.3
    traffic: 100%
  green:
    status: standby
    version: v1.2.4
    traffic: 0%

cutover:
  validation_checks:
    - health_check
    - smoke_test
    - monitoring_alerts
  rollback_time: < 5 minutes
```

**関連用語**: [Canary Deployment](#canary-deployment), [Rolling Deployment](#rolling-deployment), [CI/CD](#cicd)

---

### C

#### CI/CD (Continuous Integration / Continuous Deployment)

**カテゴリ**: DevOps  
**定義**: コードの統合、テスト、デプロイを自動化するプラクティス。継続的インテグレーション（CI）はコードの統合とテストを、継続的デプロイ（CD）は本番環境へのデプロイを自動化します。

**組織での使用例**:
```yaml
# CI/CD パイプライン
stages:
  - build:
      - compile
      - unit_tests
  - test:
      - integration_tests
      - security_scan
  - deploy:
      - staging
      - production (manual approval required)

tools:
  - GitHub Actions
  - Jenkins
  - ArgoCD
```

**関連用語**: [Pipeline](#pipeline), [Automated Testing](#automated-testing), [DevOps](#devops)

---

#### Canary Deployment

**カテゴリ**: DevOps  
**定義**: 新バージョンを一部のユーザーにのみ段階的にリリースし、問題がないことを確認してから全体に展開するデプロイ戦略。

**組織での使用例**:
```yaml
# Canary デプロイメント戦略
phases:
  - phase: 1
    traffic: 5%
    duration: 1 hour
    rollback_condition: error_rate > 1%
  
  - phase: 2
    traffic: 25%
    duration: 2 hours
    rollback_condition: error_rate > 0.5%
  
  - phase: 3
    traffic: 100%
```

**関連用語**: [Blue-Green Deployment](#blue-green-deployment), [Feature Flag](#feature-flag), [A/B Testing](#ab-testing)

---

#### Code Review

**カテゴリ**: プロセス  
**定義**: コードの品質、セキュリティ、保守性を確保するために、他の開発者がコードを検査するプロセス。

**組織での使用例**:
- 最低2名のレビュアー承認が必要
- レビューチェックリスト使用
- 自動化されたコード品質チェック（SonarQube）

**関連用語**: [Pull Request](#pull-request), [Pair Programming](#pair-programming), [Static Analysis](#static-analysis)

---

### D

#### DDD (Domain-Driven Design)

**カテゴリ**: アーキテクチャ  
**定義**: ビジネスドメインをソフトウェア設計の中心に置く設計手法。複雑なビジネスロジックをモデル化し、技術的な実装と密接に結びつけます。

**組織での使用例**:
```
# DDD 戦略的設計
Bounded Contexts:
  - 注文管理コンテキスト
    - Order (Aggregate Root)
    - OrderItem (Entity)
    - Payment (Value Object)
  
  - 在庫管理コンテキスト
    - Inventory (Aggregate Root)
    - Stock (Entity)

Context Map:
  注文管理 -> 在庫管理 (Customer-Supplier)
```

**関連用語**: [Microservices](#microservices), [Bounded Context](#bounded-context), [Aggregate](#aggregate)

---

#### DevOps

**カテゴリ**: プロセス  
**定義**: 開発（Development）と運用（Operations）を統合し、ソフトウェアのデリバリーを高速化・自動化する文化とプラクティス。

**組織での使用例**:
```yaml
# DevOps 実践項目
automation:
  - CI/CD パイプライン
  - インフラストラクチャ as Code
  - 自動テスト

monitoring:
  - アプリケーションメトリクス
  - インフラメトリクス
  - ログ集約

collaboration:
  - 開発と運用の共同オンコール
  - ポストモーテム分析
  - ChatOps
```

**関連用語**: [CI/CD](#cicd), [Infrastructure as Code](#infrastructure-as-code), [SRE](#sre)

---

#### Docker

**カテゴリ**: ツール  
**定義**: アプリケーションとその依存関係をコンテナにパッケージ化するプラットフォーム。環境の一貫性を保ち、デプロイを簡素化します。

**組織での使用例**:
```dockerfile
# Dockerfile 例
FROM node:18-alpine

WORKDIR /app

COPY package*.json ./
RUN npm ci --only=production

COPY . .

USER node
EXPOSE 3000

CMD ["node", "server.js"]
```

**推奨プラクティス**:
- マルチステージビルドの使用
- 最小限のベースイメージ（Alpine）
- non-rootユーザーでの実行
- .dockerignoreの使用

**関連用語**: [Kubernetes](#kubernetes), [Container](#container), [Microservices](#microservices)

---

### E

#### Event-Driven Architecture

**カテゴリ**: アーキテクチャ  
**定義**: イベントの生成、検出、処理を中心としたアーキテクチャパターン。サービス間の疎結合を実現します。

**組織での使用例**:
```yaml
# イベント駆動アーキテクチャ
event_bus: Apache Kafka

events:
  - OrderCreated:
      producer: order-service
      consumers:
        - inventory-service
        - notification-service
        - analytics-service
  
  - PaymentProcessed:
      producer: payment-service
      consumers:
        - order-service
        - billing-service

patterns:
  - Event Sourcing
  - CQRS (Command Query Responsibility Segregation)
```

**関連用語**: [Microservices](#microservices), [Message Queue](#message-queue), [CQRS](#cqrs)

---

#### ESLint

**カテゴリ**: ツール  
**定義**: JavaScriptおよびTypeScriptのコード品質と一貫性を保つための静的解析ツール。

**組織での使用例**:
```json
{
  "extends": [
    "eslint:recommended",
    "plugin:@typescript-eslint/recommended",
    "plugin:react/recommended",
    "prettier"
  ],
  "rules": {
    "no-console": "warn",
    "no-unused-vars": "error",
    "@typescript-eslint/explicit-function-return-type": "warn"
  }
}
```

**関連用語**: [Static Analysis](#static-analysis), [Code Quality](#code-quality), [Prettier](#prettier)

---

### F

#### Feature Flag

**カテゴリ**: プロセス  
**定義**: 機能のオン/オフを実行時に切り替えられる仕組み。段階的なリリースやA/Bテストを可能にします。

**組織での使用例**:
```typescript
// Feature Flag 実装例
import { featureFlag } from '@/lib/feature-flags';

function CheckoutPage() {
  const newCheckoutEnabled = featureFlag('new_checkout_flow', {
    user: currentUser,
    default: false
  });

  return newCheckoutEnabled 
    ? <NewCheckoutFlow /> 
    : <LegacyCheckoutFlow />;
}
```

**使用シナリオ**:
- Canaryデプロイメント
- A/Bテスト
- 機能の段階的ロールアウト
- 緊急時の機能無効化

**推奨ツール**: LaunchDarkly, Unleash, Firebase Remote Config

**関連用語**: [Canary Deployment](#canary-deployment), [A/B Testing](#ab-testing), [Progressive Delivery](#progressive-delivery)

---

#### FQDN (Fully Qualified Domain Name)

**カテゴリ**: インフラストラクチャ  
**定義**: ホスト名とドメイン名を含む完全なドメイン名。例: `api.example.com`

**組織での使用例**:
```yaml
# DNS 設定例
environments:
  production:
    api: api.example.com
    web: www.example.com
    admin: admin.example.com
  
  staging:
    api: api.staging.example.com
    web: staging.example.com
```

**関連用語**: [DNS](#dns), [Load Balancer](#load-balancer), [SSL/TLS](#ssltls)

---

### G

#### Git

**カテゴリ**: ツール  
**定義**: 分散型バージョン管理システム。コードの変更履歴を追跡し、複数人での協調開発を可能にします。

**組織での使用例**:
```bash
# Git ワークフロー
# 1. 機能ブランチの作成
git checkout -b feature/user-authentication

# 2. 変更のコミット
git add .
git commit -m "feat: implement user authentication"

# 3. リモートへのプッシュ
git push origin feature/user-authentication

# 4. Pull Request の作成
# GitHub UI で実施
```

**ブランチ戦略**: Git Flow（組織標準）

**関連用語**: [GitHub](#github), [Pull Request](#pull-request), [Version Control](#version-control)

---

#### GraphQL

**カテゴリ**: アーキテクチャ  
**定義**: APIのクエリ言語およびランタイム。クライアントが必要なデータを正確に指定でき、オーバーフェッチ/アンダーフェッチを防ぎます。

**組織での使用例**:
```graphql
# GraphQL スキーマ例
type User {
  id: ID!
  name: String!
  email: String!
  posts: [Post!]!
}

type Post {
  id: ID!
  title: String!
  content: String!
  author: User!
}

type Query {
  user(id: ID!): User
  posts(limit: Int, offset: Int): [Post!]!
}

type Mutation {
  createPost(title: String!, content: String!): Post!
}
```

**推奨ツール**:
- Apollo Server / Client
- GraphQL Code Generator
- GraphiQL / GraphQL Playground

**関連用語**: [API](#api), [RESTful API](#restful-api), [Schema](#schema)

---

### H

#### High Availability (HA)

**カテゴリ**: インフラストラクチャ  
**定義**: システムが長期間にわたり継続的に稼働する能力。冗長性とフェイルオーバーメカニズムにより実現します。

**組織での使用例**:
```yaml
# HA 構成例
load_balancer:
  type: Application Load Balancer
  availability_zones: 3
  health_check:
    interval: 30s
    timeout: 5s

application_servers:
  min_instances: 3
  max_instances: 10
  distribution: multi-AZ

database:
  type: PostgreSQL
  replication: multi-AZ
  automatic_failover: enabled
  backup: daily

target_availability: 99.9% (Three Nines)
```

**関連用語**: [Scalability](#scalability), [Fault Tolerance](#fault-tolerance), [SLA](#sla)

---

#### Horizontal Scaling

**カテゴリ**: インフラストラクチャ  
**定義**: サーバーの台数を増やすことで処理能力を向上させるスケーリング手法。垂直スケーリング（サーバーのスペックアップ）と対比されます。

**組織での使用例**:
```yaml
# Auto Scaling 設定
auto_scaling:
  metric: CPU利用率
  target: 70%
  scale_out:
    threshold: 80%
    cooldown: 300s
    increment: 2 instances
  scale_in:
    threshold: 30%
    cooldown: 600s
    decrement: 1 instance
```

**関連用語**: [Vertical Scaling](#vertical-scaling), [Load Balancer](#load-balancer), [Scalability](#scalability)

---

### I

#### IaC (Infrastructure as Code)

**カテゴリ**: DevOps  
**定義**: インフラストラクチャの構成をコードで定義・管理する手法。バージョン管理、再現性、自動化を実現します。

**組織での使用例**:
```hcl
# Terraform 例
resource "aws_instance" "web" {
  ami           = "ami-0c55b159cbfafe1f0"
  instance_type = "t3.medium"

  tags = {
    Name        = "web-server"
    Environment = "production"
    ManagedBy   = "terraform"
  }
}

resource "aws_security_group" "web" {
  name = "web-sg"

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
```

**推奨ツール**:
- Terraform（マルチクラウド）
- AWS CloudFormation（AWS専用）
- Pulumi（プログラマティックIaC）

**関連用語**: [DevOps](#devops), [Terraform](#terraform), [Cloud Computing](#cloud-computing)

---

#### Idempotency

**カテゴリ**: アーキテクチャ  
**定義**: 同じ操作を複数回実行しても、1回実行した場合と同じ結果になる性質。分散システムやAPIで重要な概念です。

**組織での使用例**:
```typescript
// 冪等性を持つAPI実装例
app.post('/api/orders', async (req, res) => {
  const { idempotencyKey, orderData } = req.body;

  // 既に処理済みか確認
  const existingOrder = await Order.findByIdempotencyKey(idempotencyKey);
  if (existingOrder) {
    return res.status(200).json(existingOrder);
  }

  // 新規注文を作成
  const order = await Order.create({
    ...orderData,
    idempotencyKey
  });

  return res.status(201).json(order);
});
```

**関連用語**: [API](#api), [Distributed System](#distributed-system), [Retry Logic](#retry-logic)

---

### J

#### JWT (JSON Web Token)

**カテゴリ**: セキュリティ  
**定義**: JSON形式のデータを安全に送信するためのトークン規格。認証・認可で広く使用されます。

**組織での使用例**:
```typescript
// JWT 生成例
import jwt from 'jsonwebtoken';

const payload = {
  userId: user.id,
  email: user.email,
  role: user.role
};

const token = jwt.sign(payload, process.env.JWT_SECRET, {
  expiresIn: '1h',
  issuer: 'api.example.com'
});

// JWT 検証例
const decoded = jwt.verify(token, process.env.JWT_SECRET);
```

**セキュリティベストプラクティス**:
- 短い有効期限（1時間以内）
- Refresh Tokenの使用
- HTTPSでのみ送信
- Secretの安全な管理

**関連用語**: [Authentication](#authentication), [OAuth](#oauth), [API Security](#api-security)

---

### K

#### Kanban

**カテゴリ**: プロセス  
**定義**: 作業の可視化とフロー管理を重視するアジャイル開発手法。WIP（Work In Progress）制限により、作業の流れを最適化します。

**組織での使用例**:
```yaml
# Kanban ボード構成
columns:
  - Backlog
  - Ready for Development
  - In Progress (WIP: 3)
  - Code Review (WIP: 2)
  - Testing (WIP: 2)
  - Done

policies:
  - WIP制限の厳守
  - 右から左への作業（プル型）
  - ブロッカーの即座の可視化
```

**関連用語**: [Agile](#agile), [Scrum](#scrum), [WIP Limit](#wip-limit)

---

#### Kubernetes (K8s)

**カテゴリ**: インフラストラクチャ  
**定義**: コンテナオーケストレーションプラットフォーム。コンテナのデプロイ、スケーリング、管理を自動化します。

**組織での使用例**:
```yaml
# Kubernetes Deployment 例
apiVersion: apps/v1
kind: Deployment
metadata:
  name: web-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: web
  template:
    metadata:
      labels:
        app: web
    spec:
      containers:
      - name: web
        image: myapp:v1.2.3
        ports:
        - containerPort: 3000
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
```

**関連用語**: [Docker](#docker), [Container](#container), [Microservices](#microservices)

---

### L

#### Load Balancer

**カテゴリ**: インフラストラクチャ  
**定義**: 複数のサーバーにトラフィックを分散するシステムコンポーネント。可用性とパフォーマンスを向上させます。

**組織での使用例**:
```yaml
# Load Balancer 設定
type: Application Load Balancer (ALB)

routing:
  - path: /api/*
    target: api-servers
    algorithm: round-robin
  
  - path: /admin/*
    target: admin-servers
    algorithm: least-connections

health_check:
  endpoint: /health
  interval: 30s
  timeout: 5s
  healthy_threshold: 2
  unhealthy_threshold: 3

ssl_termination: enabled
```

**関連用語**: [High Availability](#high-availability), [Horizontal Scaling](#horizontal-scaling), [CDN](#cdn)

---

#### Logging

**カテゴリ**: 運用  
**定義**: システムのイベントや動作を記録するプラクティス。トラブルシューティング、監査、分析に不可欠です。

**組織での使用例**:
```typescript
// 構造化ログの例
import logger from '@/lib/logger';

logger.info('User logged in', {
  userId: user.id,
  email: user.email,
  ipAddress: req.ip,
  userAgent: req.headers['user-agent'],
  timestamp: new Date().toISOString()
});

logger.error('Payment processing failed', {
  orderId: order.id,
  amount: order.total,
  error: error.message,
  stack: error.stack,
  severity: 'HIGH'
});
```

**ログレベル**:
- ERROR: エラー、即座の対応が必要
- WARN: 警告、監視が必要
- INFO: 情報、重要なビジネスイベント
- DEBUG: デバッグ情報（本番環境では無効）

**推奨ツール**: ELK Stack (Elasticsearch, Logstash, Kibana), Datadog, CloudWatch Logs

**関連用語**: [Monitoring](#monitoring), [Observability](#observability), [Troubleshooting](#troubleshooting)

---

### M

#### Microservices

**カテゴリ**: アーキテクチャ  
**定義**: 小さく独立したサービスの集合としてアプリケーションを構築するアーキテクチャスタイル。各サービスは独立してデプロイ・スケール可能です。

**組織での使用例**:
```yaml
# マイクロサービス構成
services:
  - user-service:
      port: 3001
      database: postgres-users
      responsibilities:
        - ユーザー認証
        - プロフィール管理
  
  - order-service:
      port: 3002
      database: postgres-orders
      responsibilities:
        - 注文作成・管理
        - 注文履歴
  
  - inventory-service:
      port: 3003
      database: postgres-inventory
      responsibilities:
        - 在庫管理
        - 在庫チェック

communication:
  synchronous: RESTful API / gRPC
  asynchronous: Apache Kafka
```

**関連用語**: [API](#api), [Docker](#docker), [Kubernetes](#kubernetes)

---

#### Monitoring

**カテゴリ**: 運用  
**定義**: システムの健全性とパフォーマンスを継続的に監視するプラクティス。問題の早期発見と対応を可能にします。

**組織での使用例**:
```yaml
# 監視項目
infrastructure:
  - CPU使用率
  - メモリ使用率
  - ディスクI/O
  - ネットワーク帯域幅

application:
  - レスポンスタイム
  - エラー率
  - スループット（RPS）
  - アクティブユーザー数

business:
  - 注文数
  - 売上
  - コンバージョン率

alerts:
  - critical: エラー率 > 5% (即座に通知)
  - warning: レスポンスタイム > 1秒 (Slackに通知)
```

**推奨ツール**: Prometheus + Grafana, Datadog, New Relic, CloudWatch

**関連用語**: [Logging](#logging), [Observability](#observability), [Alerting](#alerting)

---

### N

#### NoSQL

**カテゴリ**: データベース  
**定義**: 非リレーショナルデータベースの総称。スキーマレス、水平スケーラビリティ、高速な読み書きが特徴です。

**組織での使用例**:
```yaml
# NoSQL データベース選択基準
document_store:
  use_case: ユーザープロフィール、カタログデータ
  tool: MongoDB
  
key_value_store:
  use_case: セッション管理、キャッシュ
  tool: Redis

wide_column_store:
  use_case: 時系列データ、IoTデータ
  tool: Apache Cassandra

graph_database:
  use_case: ソーシャルグラフ、推薦エンジン
  tool: Neo4j
```

**関連用語**: [Database](#database), [MongoDB](#mongodb), [Redis](#redis)

---

#### Node.js

**カテゴリ**: ツール  
**定義**: JavaScriptランタイム環境。非同期I/Oとイベント駆動アーキテクチャにより、高いスループットを実現します。

**組織での使用例**:
```javascript
// Express.js サーバー例
import express from 'express';
import helmet from 'helmet';
import cors from 'cors';

const app = express();

// セキュリティミドルウェア
app.use(helmet());
app.use(cors());
app.use(express.json());

// ルート定義
app.get('/api/health', (req, res) => {
  res.json({ status: 'healthy' });
});

// エラーハンドリング
app.use((err, req, res, next) => {
  logger.error('Unhandled error', { error: err });
  res.status(500).json({ error: 'Internal server error' });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  logger.info(`Server running on port ${PORT}`);
});
```

**関連用語**: [JavaScript](#javascript), [Express.js](#expressjs), [npm](#npm)

---

### O

#### OAuth

**カテゴリ**: セキュリティ  
**定義**: オープンスタンダードの認可プロトコル。ユーザーが自分の資格情報を共有することなく、サードパーティアプリケーションにアクセスを許可できます。

**組織での使用例**:
```yaml
# OAuth 2.0 フロー
authorization_server: auth.example.com

supported_flows:
  - Authorization Code (推奨: Webアプリ)
  - Client Credentials (サーバー間通信)
  - Refresh Token (トークン更新)

security:
  - PKCE (Proof Key for Code Exchange) 必須
  - State parameter でCSRF対策
  - HTTPSのみ

token_expiry:
  access_token: 1 hour
  refresh_token: 30 days
```

**関連用語**: [JWT](#jwt), [Authentication](#authentication), [API Security](#api-security)

---

#### Observability

**カテゴリ**: 運用  
**定義**: システムの内部状態を外部出力（ログ、メトリクス、トレース）から理解できる能力。監視（Monitoring）よりも広い概念です。

**組織での使用例**:
```yaml
# Observability の3本柱
logs:
  tool: ELK Stack
  purpose: イベントの詳細記録

metrics:
  tool: Prometheus + Grafana
  purpose: システムの定量的測定

traces:
  tool: Jaeger
  purpose: 分散トレーシング、リクエストの追跡

# 実装例
instrumentation:
  - アプリケーションにSDK組み込み
  - 自動計測の有効化
  - カスタムメトリクスの追加
```

**関連用語**: [Monitoring](#monitoring), [Logging](#logging), [Distributed Tracing](#distributed-tracing)

---

### P

#### Pipeline

**カテゴリ**: DevOps  
**定義**: CI/CDにおける自動化されたワークフロー。ビルド、テスト、デプロイのステージを順次実行します。

**組織での使用例**:
```yaml
# GitHub Actions パイプライン例
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
      - name: Install dependencies
        run: npm ci
      - name: Run tests
        run: npm test
      - name: Build
        run: npm run build
  
  deploy:
    needs: build
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - name: Deploy to production
        run: ./scripts/deploy.sh
```

**関連用語**: [CI/CD](#cicd), [DevOps](#devops), [Automated Testing](#automated-testing)

---

#### PostgreSQL

**カテゴリ**: データベース  
**定義**: オープンソースのリレーショナルデータベース管理システム。ACID準拠、拡張性、高度な機能を提供します。

**組織での使用例**:
```sql
-- インデックス戦略
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);

-- パーティショニング（大規模テーブル）
CREATE TABLE orders (
    id BIGSERIAL,
    user_id INTEGER,
    created_at TIMESTAMP,
    ...
) PARTITION BY RANGE (created_at);

CREATE TABLE orders_2024_q1 PARTITION OF orders
    FOR VALUES FROM ('2024-01-01') TO ('2024-04-01');
```

**パフォーマンスチューニング**:
- 適切なインデックス設計
- EXPLAIN ANALYZEによるクエリ分析
- コネクションプーリング（PgBouncer）
- レプリケーション（読み取り負荷分散）

**関連用語**: [Database](#database), [SQL](#sql), [Indexing](#indexing)

---

#### Pull Request (PR)

**カテゴリ**: プロセス  
**定義**: コード変更を本番ブランチにマージする前にレビューを受けるためのプロセス。GitHubなどのプラットフォームで使用されます。

**組織での使用例**:
```markdown
# Pull Request テンプレート

## 変更の概要
[変更内容の簡潔な説明]

## 関連Issue
Closes #123

## 変更の種類
- [ ] Bug fix
- [x] New feature
- [ ] Breaking change

## テスト
- [x] ユニットテスト追加
- [x] 手動テスト実施
- [ ] E2Eテスト追加

## レビュー観点
- パフォーマンスへの影響
- セキュリティ考慮事項
```

**レビュー基準**:
- 最低2名のレビュアー承認
- 全てのCIチェック通過
- コードカバレッジ80%以上

**関連用語**: [Code Review](#code-review), [Git](#git), [GitHub](#github)

---

### Q

#### Queue

**カテゴリ**: アーキテクチャ  
**定義**: メッセージやタスクを一時的に保存し、非同期処理を実現するデータ構造。システム間の疎結合を実現します。

**組織での使用例**:
```typescript
// メッセージキュー実装例（Bull + Redis）
import Queue from 'bull';

const emailQueue = new Queue('email', {
  redis: { host: 'localhost', port: 6379 }
});

// ジョブの追加
emailQueue.add('sendWelcomeEmail', {
  email: user.email,
  name: user.name
}, {
  attempts: 3,
  backoff: {
    type: 'exponential',
    delay: 2000
  }
});

// ジョブの処理
emailQueue.process('sendWelcomeEmail', async (job) => {
  await sendEmail(job.data);
});
```

**使用シナリオ**:
- メール送信
- 画像処理
- レポート生成
- データ同期

**推奨ツール**: RabbitMQ, Apache Kafka, AWS SQS, Redis (Bull)

**関連用語**: [Message Queue](#message-queue), [Async Processing](#async-processing), [Event-Driven Architecture](#event-driven-architecture)

---

### R

#### Redis

**カテゴリ**: データベース  
**定義**: インメモリのキーバリューストア。高速なデータアクセス、キャッシュ、セッション管理、メッセージブローカーとして使用されます。

**組織での使用例**:
```typescript
// Redis キャッシュ実装例
import Redis from 'ioredis';

const redis = new Redis({
  host: 'localhost',
  port: 6379,
  retryStrategy: (times) => Math.min(times * 50, 2000)
});

// キャッシュ戦略（Cache-Aside）
async function getUser(userId: string) {
  // キャッシュから取得を試みる
  const cached = await redis.get(`user:${userId}`);
  if (cached) {
    return JSON.parse(cached);
  }

  // DBから取得
  const user = await db.users.findById(userId);
  
  // キャッシュに保存（TTL: 1時間）
  await redis.setex(
    `user:${userId}`,
    3600,
    JSON.stringify(user)
  );

  return user;
}
```

**使用シナリオ**:
- セッション管理
- APIレスポンスキャッシュ
- レート制限（Rate Limiting）
- リーダーボード（Sorted Sets）

**関連用語**: [Caching](#caching), [NoSQL](#nosql), [Performance Optimization](#performance-optimization)

---

#### RESTful API

**カテゴリ**: アーキテクチャ  
**定義**: REST（Representational State Transfer）アーキテクチャスタイルに従うAPI。HTTP メソッド、ステートレス、リソース指向が特徴です。

**組織での使用例**:
```yaml
# RESTful API 設計
resources:
  /users:
    GET: ユーザー一覧取得
    POST: 新規ユーザー作成
  
  /users/{id}:
    GET: 特定ユーザー取得
    PUT: ユーザー情報更新
    DELETE: ユーザー削除
  
  /users/{id}/orders:
    GET: ユーザーの注文一覧取得

principles:
  - ステートレス（状態を保持しない）
  - リソース指向のURL
  - 標準HTTPメソッド使用
  - 適切なHTTPステータスコード
  - HAL/JSON:API などのハイパーメディア形式
```

**関連用語**: [API](#api), [HTTP](#http), [GraphQL](#graphql)

---

#### Rollback

**カテゴリ**: DevOps  
**定義**: デプロイに問題が発生した際、以前の安定したバージョンに戻すプロセス。迅速な復旧を可能にします。

**組織での使用例**:
```yaml
# Rollback 戦略
automatic_rollback:
  triggers:
    - error_rate > 5%
    - response_time > 2s
    - health_check_failure
  action: 即座にロールバック

manual_rollback:
  command: kubectl rollout undo deployment/web-app
  verification:
    - ヘルスチェック確認
    - エラーログ監視
    - メトリクス確認

deployment_strategy:
  type: Blue-Green
  rollback_time: < 5 minutes
```

**関連用語**: [Deployment](#deployment), [Blue-Green Deployment](#blue-green-deployment), [CI/CD](#cicd)

---

### S

#### SLA (Service Level Agreement)

**カテゴリ**: 運用  
**定義**: サービス提供者と顧客間で合意されたサービスレベルの契約。稼働率、応答時間、サポート時間などを定義します。

**組織での使用例**:
```yaml
# SLA 定義
availability:
  tier_1: 99.9% (Three Nines) - 月間ダウンタイム約43分
  tier_2: 99.95% - 月間ダウンタイム約22分
  tier_3: 99.5% - 月間ダウンタイム約3.6時間

performance:
  api_response_time: p95 < 500ms
  page_load_time: p95 < 2s

support:
  critical: 15分以内に初期対応
  high: 1時間以内に初期対応
  medium: 4時間以内に初期対応
  low: 24時間以内に初期対応

penalties:
  - 可用性 < 99.9%: 10%のサービスクレジット
  - 可用性 < 99.5%: 25%のサービスクレジット
```

**関連用語**: [High Availability](#high-availability), [SLI](#sli), [SLO](#slo)

---

#### SOLID Principles

**カテゴリ**: プロセス  
**定義**: オブジェクト指向設計の5つの基本原則。保守性と拡張性の高いコードを実現します。

**組織での使用例**:
```typescript
// S - Single Responsibility Principle（単一責任の原則）
class UserRepository {
  async save(user: User) { /* DB操作のみ */ }
}

class UserValidator {
  validate(user: User) { /* バリデーションのみ */ }
}

// O - Open/Closed Principle（開放/閉鎖の原則）
interface PaymentProcessor {
  process(amount: number): Promise<void>;
}

class CreditCardProcessor implements PaymentProcessor {
  async process(amount: number) { /* 実装 */ }
}

class PayPalProcessor implements PaymentProcessor {
  async process(amount: number) { /* 実装 */ }
}

// L - Liskov Substitution Principle（リスコフの置換原則）
// 親クラスを子クラスで置き換えても正常に動作

// I - Interface Segregation Principle（インターフェース分離の原則）
interface Readable {
  read(): string;
}

interface Writable {
  write(data: string): void;
}

// D - Dependency Inversion Principle（依存性逆転の原則）
class UserService {
  constructor(private repository: UserRepository) {}
}
```

**関連用語**: [Design Patterns](#design-patterns), [Clean Code](#clean-code), [Refactoring](#refactoring)

---

#### SQL (Structured Query Language)

**カテゴリ**: データベース  
**定義**: リレーショナルデータベースを操作するための標準言語。データの問い合わせ、挿入、更新、削除を行います。

**組織での使用例**:
```sql
-- 複雑なクエリ例
WITH monthly_sales AS (
  SELECT
    DATE_TRUNC('month', created_at) AS month,
    SUM(total_amount) AS total_sales,
    COUNT(*) AS order_count
  FROM orders
  WHERE status = 'completed'
    AND created_at >= NOW() - INTERVAL '12 months'
  GROUP BY DATE_TRUNC('month', created_at)
)
SELECT
  month,
  total_sales,
  order_count,
  ROUND(total_sales / order_count, 2) AS avg_order_value,
  ROUND(
    100.0 * (total_sales - LAG(total_sales) OVER (ORDER BY month))
    / LAG(total_sales) OVER (ORDER BY month),
    2
  ) AS growth_rate
FROM monthly_sales
ORDER BY month DESC;
```

**パフォーマンスベストプラクティス**:
- 適切なインデックス使用
- N+1問題の回避
- プリペアドステートメント使用
- EXPLAIN でクエリ分析

**関連用語**: [Database](#database), [PostgreSQL](#postgresql), [Query Optimization](#query-optimization)

---

#### Scrum

**カテゴリ**: プロセス  
**定義**: 反復的なスプリントに基づくアジャイル開発フレームワーク。定義された役割、イベント、成果物により構造化されています。

**組織での使用例**:
```yaml
# Scrum フレームワーク
sprint:
  duration: 2週間
  
roles:
  - Product Owner: プロダクトバックログ管理
  - Scrum Master: プロセスファシリテーション
  - Development Team: 機能開発

ceremonies:
  - Sprint Planning: 月曜午前（2時間）
  - Daily Standup: 毎朝10:00（15分）
  - Sprint Review: 金曜午後（1時間）
  - Sprint Retrospective: 金曜午後（45分）

artifacts:
  - Product Backlog
  - Sprint Backlog
  - Increment（動作するソフトウェア）
```

**関連用語**: [Agile](#agile), [Sprint](#sprint), [Kanban](#kanban)

---

### T

#### TDD (Test-Driven Development)

**カテゴリ**: プロセス  
**定義**: テストを先に書いてから実装するソフトウェア開発手法。Red（失敗するテスト）→ Green（テスト通過）→ Refactor（リファクタリング）のサイクルを繰り返します。

**組織での使用例**:
```typescript
// TDD サイクル例

// 1. Red: 失敗するテストを書く
describe('UserService', () => {
  it('should create a new user', async () => {
    const userData = { name: 'John', email: 'john@example.com' };
    const user = await userService.create(userData);
    expect(user.id).toBeDefined();
    expect(user.name).toBe('John');
  });
});

// 2. Green: テストを通過させる最小限の実装
class UserService {
  async create(userData: UserData): Promise<User> {
    const user = await db.users.insert(userData);
    return user;
  }
}

// 3. Refactor: コードを改善
class UserService {
  constructor(private repository: UserRepository) {}

  async create(userData: UserData): Promise<User> {
    this.validateUserData(userData);
    return this.repository.save(userData);
  }

  private validateUserData(data: UserData) {
    if (!data.email.includes('@')) {
      throw new Error('Invalid email');
    }
  }
}
```

**関連用語**: [Unit Testing](#unit-testing), [BDD](#bdd), [Automated Testing](#automated-testing)

---

#### Terraform

**カテゴリ**: ツール  
**定義**: Infrastructure as Code（IaC）ツール。宣言的な構成ファイルでインフラストラクチャを定義・管理します。

**組織での使用例**:
```hcl
# Terraform 設定例
terraform {
  required_version = ">= 1.0"
  
  backend "s3" {
    bucket = "terraform-state"
    key    = "production/terraform.tfstate"
    region = "us-east-1"
  }
}

provider "aws" {
  region = var.aws_region
}

resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  
  tags = {
    Name        = "main-vpc"
    Environment = var.environment
    ManagedBy   = "terraform"
  }
}

module "web_servers" {
  source = "./modules/web-servers"
  
  vpc_id      = aws_vpc.main.id
  subnet_ids  = aws_subnet.public[*].id
  environment = var.environment
}
```

**ワークフロー**:
```bash
terraform init      # 初期化
terraform plan      # 変更プレビュー
terraform apply     # 変更適用
terraform destroy   # リソース削除
```

**関連用語**: [IaC](#iac-infrastructure-as-code), [DevOps](#devops), [Cloud Computing](#cloud-computing)

---

#### TypeScript

**カテゴリ**: プログラミング言語  
**定義**: JavaScriptのスーパーセット。静的型付けとモダンなECMAScript機能を提供します。

**組織での使用例**:
```typescript
// TypeScript コード例
interface User {
  id: string;
  name: string;
  email: string;
  role: 'admin' | 'user' | 'guest';
}

interface UserRepository {
  findById(id: string): Promise<User | null>;
  save(user: User): Promise<User>;
}

class UserService {
  constructor(private repository: UserRepository) {}

  async getUser(id: string): Promise<User> {
    const user = await this.repository.findById(id);
    
    if (!user) {
      throw new Error(`User not found: ${id}`);
    }

    return user;
  }

  async updateUser(id: string, data: Partial<User>): Promise<User> {
    const user = await this.getUser(id);
    const updated = { ...user, ...data };
    return this.repository.save(updated);
  }
}
```

**TypeScript の利点**:
- コンパイル時の型チェック
- 優れたIDE サポート（自動補完、リファクタリング）
- 大規模プロジェクトでの保守性向上
- 最新のECMAScript機能のサポート

**関連用語**: [JavaScript](#javascript), [Static Type Checking](#static-type-checking), [Node.js](#nodejs)

---

### U

#### Unit Testing

**カテゴリ**: テスト  
**定義**: 個々の関数やメソッドを独立してテストする手法。コードの最小単位の正確性を検証します。

**組織での使用例**:
```typescript
// Jest によるユニットテスト例
import { calculateDiscount } from './pricing';

describe('calculateDiscount', () => {
  it('should apply 10% discount for orders over $100', () => {
    const result = calculateDiscount(150);
    expect(result).toBe(135); // 150 - 15
  });

  it('should apply no discount for orders under $100', () => {
    const result = calculateDiscount(50);
    expect(result).toBe(50);
  });

  it('should handle edge case at $100', () => {
    const result = calculateDiscount(100);
    expect(result).toBe(90); // Exactly $100 gets discount
  });

  it('should throw error for negative amounts', () => {
    expect(() => calculateDiscount(-10)).toThrow('Invalid amount');
  });
});
```

**カバレッジ目標**:
- ラインカバレッジ: 80%以上
- ブランチカバレッジ: 75%以上
- クリティカルパス: 100%

**推奨ツール**: Jest, Vitest, Mocha + Chai

**関連用語**: [TDD](#tdd-test-driven-development), [Integration Testing](#integration-testing), [Automated Testing](#automated-testing)

---

#### URL (Uniform Resource Locator)

**カテゴリ**: Web  
**定義**: Web上のリソースの場所を指定する標準的な方法。プロトコル、ホスト、パス、クエリパラメータなどで構成されます。

**組織での使用例**:
```
# URL 構造
https://api.example.com/v1/users?page=2&limit=20

プロトコル: https
ホスト: api.example.com
パス: /v1/users
クエリパラメータ: page=2, limit=20

# RESTful URL 設計原則
GET    /api/v1/users           # ユーザー一覧
GET    /api/v1/users/123       # 特定ユーザー
POST   /api/v1/users           # ユーザー作成
PUT    /api/v1/users/123       # ユーザー更新
DELETE /api/v1/users/123       # ユーザー削除
GET    /api/v1/users/123/orders # ユーザーの注文一覧
```

**関連用語**: [RESTful API](#restful-api), [HTTP](#http), [URI](#uri)

---

### V

#### Version Control

**カテゴリ**: ツール  
**定義**: ファイルの変更履歴を管理するシステム。複数人での協調開発、変更の追跡、以前のバージョンへの復元を可能にします。

**組織での使用例**:
```bash
# Git ワークフロー
# 1. ブランチ作成
git checkout -b feature/new-feature

# 2. 変更をコミット
git add .
git commit -m "feat: add new feature"

# 3. リモートにプッシュ
git push origin feature/new-feature

# 4. Pull Request 作成・レビュー

# 5. マージ後、ブランチ削除
git checkout main
git pull origin main
git branch -d feature/new-feature
```

**ブランチ戦略（Git Flow）**:
```
main: 本番環境コード
  └─ release/v1.2.0: リリース準備
develop: 開発統合ブランチ
  ├─ feature/user-auth: 機能開発
  ├─ feature/payment: 機能開発
  └─ hotfix/critical-bug: 緊急バグ修正
```

**関連用語**: [Git](#git), [GitHub](#github), [Pull Request](#pull-request)

---

#### Vertical Scaling

**カテゴリ**: インフラストラクチャ  
**定義**: サーバーのリソース（CPU、メモリ）を増強することでパフォーマンスを向上させるスケーリング手法。水平スケーリングと対比されます。

**組織での使用例**:
```yaml
# Vertical Scaling 戦略
current:
  instance_type: t3.medium
  cpu: 2 vCPUs
  memory: 4 GB

upgraded:
  instance_type: t3.xlarge
  cpu: 4 vCPUs
  memory: 16 GB

considerations:
  pros:
    - シンプルな実装
    - アプリケーション変更不要
  
  cons:
    - ダウンタイムが必要
    - スケールの上限あり
    - 単一障害点（SPOF）

recommendation:
  - 短期的な対応: Vertical Scaling
  - 長期的な対応: Horizontal Scaling への移行
```

**関連用語**: [Horizontal Scaling](#horizontal-scaling), [Scalability](#scalability), [Performance Optimization](#performance-optimization)

---

### W

#### Webhook

**カテゴリ**: アーキテクチャ  
**定義**: イベント発生時に自動的にHTTPリクエストを送信する仕組み。リアルタイムの通知と統合を実現します。

**組織での使用例**:
```typescript
// Webhook エンドポイント実装例
app.post('/webhooks/github', async (req, res) => {
  const event = req.headers['x-github-event'];
  const signature = req.headers['x-hub-signature-256'];

  // 署名検証
  if (!verifySignature(req.body, signature)) {
    return res.status(401).json({ error: 'Invalid signature' });
  }

  // イベント処理
  switch (event) {
    case 'push':
      await handlePushEvent(req.body);
      break;
    case 'pull_request':
      await handlePullRequestEvent(req.body);
      break;
  }

  res.status(200).json({ received: true });
});
```

**セキュリティベストプラクティス**:
- 署名検証（HMAC）
- HTTPS必須
- IPアドレス制限
- リトライメカニズム

**使用例**:
- GitHub: コミット・PRイベント
- Stripe: 支払いイベント
- Slack: メッセージイベント

**関連用語**: [API](#api), [Event-Driven Architecture](#event-driven-architecture), [Integration](#integration)

---

#### WIP (Work In Progress) Limit

**カテゴリ**: プロセス  
**定義**: 同時に進行する作業の数を制限するプラクティス。フローの最適化とボトルネック発見に役立ちます。

**組織での使用例**:
```yaml
# Kanban ボードのWIP制限
columns:
  - Backlog: (制限なし)
  - Ready: WIP 5
  - In Progress: WIP 3
  - Code Review: WIP 2
  - Testing: WIP 2
  - Done: (制限なし)

benefits:
  - ボトルネックの可視化
  - マルチタスクの削減
  - フロー効率の向上
  - 作業完了の促進

policy:
  - WIP制限に達したら新規作業は開始しない
  - ブロックされた作業は可視化
  - チームで解決策を議論
```

**関連用語**: [Kanban](#kanban), [Agile](#agile), [Bottleneck](#bottleneck)

---

### X

#### XSS (Cross-Site Scripting)

**カテゴリ**: セキュリティ  
**定義**: Webアプリケーションの脆弱性の一種。攻撃者が悪意のあるスクリプトを注入し、他のユーザーのブラウザで実行させる攻撃です。

**組織での使用例**:
```typescript
// XSS 対策例

// 1. 入力のサニタイズ
import DOMPurify from 'dompurify';

function sanitizeInput(userInput: string): string {
  return DOMPurify.sanitize(userInput);
}

// 2. 出力のエスケープ（React は自動エスケープ）
function UserComment({ comment }: { comment: string }) {
  // React は自動的にエスケープ
  return <div>{comment}</div>;
}

// 3. Content Security Policy (CSP)
const cspHeader = `
  default-src 'self';
  script-src 'self' 'unsafe-inline';
  style-src 'self' 'unsafe-inline';
  img-src 'self' data: https:;
`;

// 4. HTTPOnly Cookie
res.cookie('sessionId', sessionId, {
  httpOnly: true,
  secure: true,
  sameSite: 'strict'
});
```

**防御策**:
- 入力バリデーション
- 出力エスケープ
- Content Security Policy（CSP）
- HTTPOnly Cookie

**関連用語**: [Security](#security), [CSRF](#csrf), [Input Validation](#input-validation)

---

### Y

#### YAML (YAML Ain't Markup Language)

**カテゴリ**: データフォーマット  
**定義**: 人間が読みやすいデータシリアライゼーション形式。設定ファイル、CI/CDパイプライン、Kubernetes マニフェストなどで広く使用されます。

**組織での使用例**:
```yaml
# アプリケーション設定
app:
  name: my-application
  version: 1.2.3
  environment: production

server:
  port: 3000
  host: 0.0.0.0
  timeout: 30s

database:
  host: db.example.com
  port: 5432
  name: myapp_production
  pool:
    min: 2
    max: 10

logging:
  level: info
  format: json
  outputs:
    - stdout
    - file: /var/log/app.log

features:
  new_checkout: true
  beta_dashboard: false
```

**ベストプラクティス**:
- インデントは2スペース
- クォートは必要な場合のみ使用
- コメントで説明を追加
- 環境変数の活用

**関連用語**: [Configuration](#configuration), [Kubernetes](#kubernetes), [CI/CD](#cicd)

---

### Z

#### Zero Downtime Deployment

**カテゴリ**: DevOps  
**定義**: サービスを停止せずにアプリケーションを更新するデプロイ戦略。ユーザーへの影響を最小化します。

**組織での使用例**:
```yaml
# Zero Downtime デプロイメント戦略
strategy: Rolling Deployment

steps:
  1. 新バージョンのインスタンスを起動
  2. ヘルスチェックが通過するまで待機
  3. ロードバランサーに新インスタンスを追加
  4. 旧インスタンスへの新規リクエストを停止
  5. 既存リクエストの処理完了を待機
  6. 旧インスタンスを終了
  7. 次のインスタンスで繰り返し

requirements:
  - ヘルスチェックエンドポイント
  - Graceful Shutdown（優雅なシャットダウン）
  - Backward Compatible な変更
  - データベースマイグレーション戦略

monitoring:
  - エラー率
  - レスポンスタイム
  - アクティブコネクション数
```

**実装技術**:
- Kubernetes Rolling Update
- Blue-Green Deployment
- Canary Deployment
- Load Balancer の Connection Draining

**関連用語**: [Blue-Green Deployment](#blue-green-deployment), [Rolling Deployment](#rolling-deployment), [High Availability](#high-availability)

---

## カテゴリ別索引

### アーキテクチャ
- [ADR](#adr-architecture-decision-record)
- [API](#api-application-programming-interface)
- [Backward Compatibility](#backward-compatibility)
- [DDD](#ddd-domain-driven-design)
- [Event-Driven Architecture](#event-driven-architecture)
- [GraphQL](#graphql)
- [Idempotency](#idempotency)
- [Microservices](#microservices)
- [Queue](#queue)
- [RESTful API](#restful-api)
- [Webhook](#webhook)

### DevOps
- [Blue-Green Deployment](#blue-green-deployment)
- [Canary Deployment](#canary-deployment)
- [CI/CD](#cicd-continuous-integration--continuous-deployment)
- [DevOps](#devops)
- [IaC](#iac-infrastructure-as-code)
- [Pipeline](#pipeline)
- [Rollback](#rollback)
- [Terraform](#terraform)
- [Zero Downtime Deployment](#zero-downtime-deployment)

### プロセス
- [Agile](#agile)
- [Code Review](#code-review)
- [Feature Flag](#feature-flag)
- [Kanban](#kanban)
- [Pull Request](#pull-request)
- [Scrum](#scrum)
- [SOLID Principles](#solid-principles)
- [TDD](#tdd-test-driven-development)
- [WIP Limit](#wip-work-in-progress-limit)

### セキュリティ
- [JWT](#jwt-json-web-token)
- [OAuth](#oauth)
- [XSS](#xss-cross-site-scripting)

### データベース
- [NoSQL](#nosql)
- [PostgreSQL](#postgresql)
- [Redis](#redis)
- [SQL](#sql-structured-query-language)

### インフラストラクチャ
- [Docker](#docker)
- [FQDN](#fqdn-fully-qualified-domain-name)
- [High Availability](#high-availability)
- [Horizontal Scaling](#horizontal-scaling)
- [Kubernetes](#kubernetes)
- [Load Balancer](#load-balancer)
- [Vertical Scaling](#vertical-scaling)

### 運用
- [Logging](#logging)
- [Monitoring](#monitoring)
- [Observability](#observability)
- [SLA](#sla-service-level-agreement)

### ツール
- [Docker](#docker)
- [ESLint](#eslint)
- [Git](#git)
- [Node.js](#nodejs)
- [Terraform](#terraform)

### テスト
- [Unit Testing](#unit-testing)

### プログラミング言語
- [TypeScript](#typescript)

### データフォーマット
- [YAML](#yaml-yaml-aint-markup-language)

### Web
- [URL](#url-uniform-resource-locator)

---

## 用語集の更新

この用語集は定期的に更新されます。新しい用語の追加や既存用語の更新が必要な場合は、Pull Request を作成してください。

**更新プロセス**:
1. 新しい用語を適切なアルファベットセクションに追加
2. カテゴリ、定義、組織での使用例、関連用語を含める
3. カテゴリ別索引を更新
4. Pull Request を作成し、レビューを依頼

---

**関連ドキュメント**:
- [Best Practices](./best-practices.md)
- [Design Patterns](./design-patterns.md)
- [External Resources](./external-resources.md)