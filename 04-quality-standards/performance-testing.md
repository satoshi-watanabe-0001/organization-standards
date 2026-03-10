---
version: "1.0.0"
last_updated: "2025-01-15"
status: "active"
owner: "QA Team"
category: "quality"
---

# パフォーマンステスト標準 / Performance Testing Standards

## 目次 / Table of Contents

1. [概要 / Overview](#概要--overview)
2. [パフォーマンステストの種類 / Types of Performance Testing](#パフォーマンステストの種類--types-of-performance-testing)
3. [パフォーマンス目標 / Performance Goals](#パフォーマンス目標--performance-goals)
4. [テストツール / Testing Tools](#テストツール--testing-tools)
5. [テスト環境 / Test Environment](#テスト環境--test-environment)
6. [テスト計画 / Test Planning](#テスト計画--test-planning)
7. [テストシナリオ / Test Scenarios](#テストシナリオ--test-scenarios)
8. [実行手順 / Execution Procedures](#実行手順--execution-procedures)
9. [メトリクスと測定 / Metrics and Measurements](#メトリクスと測定--metrics-and-measurements)
10. [結果分析 / Result Analysis](#結果分析--result-analysis)
11. [レポーティング / Reporting](#レポーティング--reporting)
12. [継続的改善 / Continuous Improvement](#継続的改善--continuous-improvement)
13. [参考資料 / References](#参考資料--references)

---

## 概要 / Overview

### 目的 / Purpose

本ドキュメントは、アプリケーションのパフォーマンステストに関する標準と手法を定義します。

This document defines standards and methodologies for application performance testing.

### 適用範囲 / Scope

- Webアプリケーション
- APIサービス
- バックエンドシステム
- データベース
- マイクロサービス

### 重要性 / Importance

パフォーマンステストにより以下を確保します：

- ユーザーエクスペリエンスの最適化
- システム安定性の保証
- スケーラビリティの検証
- ボトルネックの早期発見
- リソース使用の最適化

---

## パフォーマンステストの種類 / Types of Performance Testing

### 1. ロードテスト / Load Testing

**目的**: 通常の負荷下でのシステム動作を検証

**特徴**:
- 想定される同時ユーザー数でテスト
- 通常業務シナリオの実行
- レスポンスタイムの測定
- リソース使用率の監視

**実施タイミング**:
- リリース前の検証
- 主要機能の変更後
- インフラ変更後

### 2. ストレステスト / Stress Testing

**目的**: システムの限界点を特定

**特徴**:
- 通常負荷を超える負荷をかける
- システムの破壊点を見つける
- 障害時の挙動を確認
- 回復能力を検証

**実施タイミング**:
- キャパシティプランニング時
- 大規模イベント前
- 四半期ごとの定期検証

### 3. スパイクテスト / Spike Testing

**目的**: 急激な負荷変動への対応力を検証

**特徴**:
- 短時間で急激に負荷を増加
- 自動スケーリングの検証
- ピーク時の安定性確認

**実施タイミング**:
- セール・キャンペーン前
- 新機能リリース前

### 4. エンデュランステスト / Endurance Testing

**目的**: 長時間稼働時の安定性を検証

**特徴**:
- 数時間〜数日間の連続実行
- メモリリークの検出
- リソース枯渇の確認
- パフォーマンス劣化の検証

**実施タイミング**:
- 月次の定期テスト
- 長時間稼働が予想される場合

### 5. スケーラビリティテスト / Scalability Testing

**目的**: システムのスケーリング能力を検証

**特徴**:
- 段階的な負荷増加
- 水平/垂直スケーリングの効果測定
- リソース追加による改善度合いの確認

**実施タイミング**:
- アーキテクチャ変更時
- スケーリング戦略の評価時

---

## パフォーマンス目標 / Performance Goals

### Webアプリケーション / Web Applications

```yaml
web_performance_targets:
  page_load_time:
    target: "< 2秒"
    good: "< 1秒"
    poor: "> 3秒"
  
  time_to_interactive:
    target: "< 3秒"
    good: "< 2秒"
    poor: "> 5秒"
  
  first_contentful_paint:
    target: "< 1.8秒"
    good: "< 1秒"
    poor: "> 3秒"
  
  largest_contentful_paint:
    target: "< 2.5秒"
    good: "< 2秒"
    poor: "> 4秒"
  
  cumulative_layout_shift:
    target: "< 0.1"
    good: "< 0.05"
    poor: "> 0.25"
  
  first_input_delay:
    target: "< 100ms"
    good: "< 50ms"
    poor: "> 300ms"
```

### API / APIs

```yaml
api_performance_targets:
  response_time_p50:
    target: "< 200ms"
    good: "< 100ms"
    poor: "> 500ms"
  
  response_time_p95:
    target: "< 500ms"
    good: "< 300ms"
    poor: "> 1000ms"
  
  response_time_p99:
    target: "< 1000ms"
    good: "< 500ms"
    poor: "> 2000ms"
  
  throughput:
    target: "> 1000 req/sec"
    good: "> 2000 req/sec"
    poor: "< 500 req/sec"
  
  error_rate:
    target: "< 0.1%"
    good: "< 0.01%"
    poor: "> 1%"
  
  availability:
    target: "> 99.9%"
    good: "> 99.99%"
    poor: "< 99%"
```

### データベース / Database

```yaml
database_performance_targets:
  query_response_time:
    simple_query: "< 10ms"
    complex_query: "< 100ms"
    aggregation: "< 500ms"
  
  connection_pool:
    active_connections: "< 80% of max"
    wait_time: "< 10ms"
  
  throughput:
    reads: "> 10000 ops/sec"
    writes: "> 5000 ops/sec"
  
  cpu_usage: "< 70%"
  memory_usage: "< 80%"
  disk_io: "< 80% saturation"
```

---

## テストツール / Testing Tools

### 推奨ツール / Recommended Tools

#### 1. k6（負荷テスト）/ k6 (Load Testing)

```javascript
// k6テストスクリプト例
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

export const options = {
  stages: [
    { duration: '2m', target: 100 },  // ランプアップ
    { duration: '5m', target: 100 },  // 定常負荷
    { duration: '2m', target: 200 },  // 負荷増加
    { duration: '5m', target: 200 },  // ピーク負荷
    { duration: '2m', target: 0 },    // ランプダウン
  ],
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    http_req_failed: ['rate<0.01'],
    errors: ['rate<0.1'],
  },
};

export default function () {
  const res = http.get('https://api.example.com/users');
  
  const checkRes = check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });
  
  errorRate.add(!checkRes);
  
  sleep(1);
}
```

**用途**:
- API負荷テスト
- パフォーマンスリグレッション検証
- CI/CD統合

**利点**:
- JavaScriptベースで習得容易
- CLIで実行可能
- Grafanaと連携可能

#### 2. Apache JMeter

**用途**:
- Webアプリケーション負荷テスト
- データベースパフォーマンステスト
- FTPサーバーテスト

**特徴**:
- GUI/CLIモード対応
- 豊富なプラグイン
- 詳細なレポート生成

#### 3. Gatling

**用途**:
- 高負荷シナリオテスト
- APIパフォーマンステスト

**特徴**:
- Scalaベース
- 高スループット対応
- リアルタイムレポート

#### 4. Lighthouse（フロントエンド）

```bash
# Lighthouse CLI実行例
lighthouse https://example.com \
  --output html \
  --output-path ./report.html \
  --chrome-flags="--headless"
```

**用途**:
- Webパフォーマンス測定
- SEO監査
- アクセシビリティチェック

#### 5. Artillery

```yaml
# artillery.yml設定例
config:
  target: "https://api.example.com"
  phases:
    - duration: 60
      arrivalRate: 10
      name: "Warm up"
    - duration: 120
      arrivalRate: 50
      name: "Sustained load"
  
scenarios:
  - name: "User flow"
    flow:
      - get:
          url: "/api/users"
      - think: 2
      - post:
          url: "/api/orders"
          json:
            productId: "{{ $randomNumber(1, 100) }}"
            quantity: "{{ $randomNumber(1, 5) }}"
```

---

## テスト環境 / Test Environment

### 環境要件 / Environment Requirements

#### 1. テスト環境の構成 / Test Environment Setup

```yaml
test_environment:
  type: "Dedicated Performance Testing Environment"
  
  infrastructure:
    compute:
      - type: "AWS EC2"
      - instance_type: "c5.2xlarge"
      - count: 3
    
    database:
      - type: "RDS Aurora"
      - instance_class: "db.r5.xlarge"
      - multi_az: true
    
    cache:
      - type: "ElastiCache Redis"
      - node_type: "cache.r5.large"
      - cluster_mode: true
    
    load_balancer:
      - type: "Application Load Balancer"
      - health_check_interval: 30
  
  network:
    vpc_cidr: "10.1.0.0/16"
    subnets:
      - "10.1.1.0/24"
      - "10.1.2.0/24"
      - "10.1.3.0/24"
  
  monitoring:
    - CloudWatch
    - Datadog
    - Prometheus + Grafana
```

#### 2. データ準備 / Data Preparation

```yaml
test_data_requirements:
  users:
    count: 100000
    distribution: "Realistic user profiles"
  
  products:
    count: 50000
    categories: 20
  
  orders:
    count: 500000
    date_range: "Last 6 months"
  
  anonymization: true
  gdpr_compliant: true
```

### 負荷生成環境 / Load Generation Environment

```yaml
load_generators:
  location: "Multiple AWS Regions"
  regions:
    - "ap-northeast-1"  # Tokyo
    - "us-east-1"       # Virginia
    - "eu-west-1"       # Ireland
  
  instances:
    type: "c5.xlarge"
    count_per_region: 5
  
  network:
    bandwidth: "10 Gbps"
    latency_target: "< 50ms"
```

---

## テスト計画 / Test Planning

### テスト計画書の構成 / Test Plan Structure

```markdown
# パフォーマンステスト計画書

## 1. テスト概要
- テスト目的
- テスト範囲
- テスト期間

## 2. テスト対象
- アプリケーション/システム
- バージョン
- 環境情報

## 3. テストシナリオ
- シナリオ一覧
- 各シナリオの詳細
- 期待される結果

## 4. 負荷モデル
- 同時ユーザー数
- トランザクション数
- データ量

## 5. 成功基準
- パフォーマンス目標値
- 許容範囲
- 必須条件

## 6. リスクと制約
- 既知のリスク
- 環境の制約
- 依存関係

## 7. スケジュール
- 準備期間
- 実行期間
- 分析期間

## 8. 役割と責任
- テスト担当者
- 環境管理者
- 結果分析者
```

### 負荷モデルの設計 / Load Model Design

#### 通常負荷シナリオ / Normal Load Scenario

```yaml
normal_load:
  duration: "30 minutes"
  
  virtual_users:
    initial: 0
    ramp_up_time: "5 minutes"
    steady_state: 500
    steady_duration: "20 minutes"
    ramp_down_time: "5 minutes"
  
  user_behavior:
    - action: "Browse products"
      percentage: 40
      think_time: "3-10 seconds"
    
    - action: "Search"
      percentage: 30
      think_time: "2-5 seconds"
    
    - action: "View product details"
      percentage: 20
      think_time: "5-15 seconds"
    
    - action: "Add to cart"
      percentage: 8
      think_time: "3-8 seconds"
    
    - action: "Checkout"
      percentage: 2
      think_time: "10-30 seconds"
```

#### ピーク負荷シナリオ / Peak Load Scenario

```yaml
peak_load:
  duration: "60 minutes"
  
  virtual_users:
    initial: 500
    ramp_up_time: "10 minutes"
    peak: 2000
    peak_duration: "30 minutes"
    ramp_down_time: "20 minutes"
  
  transaction_rate:
    target: "5000 req/min"
    peak: "15000 req/min"
```

---

## テストシナリオ / Test Scenarios

### シナリオ1: ユーザー登録フロー / User Registration Flow

```javascript
// k6 シナリオ例
import http from 'k6/http';
import { check, sleep } from 'k6';

export default function () {
  // 1. ホームページアクセス
  let res = http.get('https://example.com');
  check(res, {
    'homepage loaded': (r) => r.status === 200,
  });
  sleep(2);
  
  // 2. 登録ページアクセス
  res = http.get('https://example.com/signup');
  check(res, {
    'signup page loaded': (r) => r.status === 200,
  });
  sleep(3);
  
  // 3. ユーザー登録
  const payload = JSON.stringify({
    email: `user${Date.now()}@example.com`,
    password: 'SecureP@ssw0rd',
    name: 'Test User',
  });
  
  res = http.post('https://example.com/api/register', payload, {
    headers: { 'Content-Type': 'application/json' },
  });
  
  check(res, {
    'registration successful': (r) => r.status === 201,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });
  
  sleep(2);
}
```

### シナリオ2: EC サイト購入フロー / E-commerce Purchase Flow

```javascript
export default function () {
  const BASE_URL = 'https://api.example.com';
  
  // 1. 商品検索
  let res = http.get(`${BASE_URL}/api/products?search=laptop`);
  check(res, { 'products loaded': (r) => r.status === 200 });
  const products = JSON.parse(res.body);
  sleep(3);
  
  // 2. 商品詳細表示
  const productId = products.data[0].id;
  res = http.get(`${BASE_URL}/api/products/${productId}`);
  check(res, { 'product details loaded': (r) => r.status === 200 });
  sleep(5);
  
  // 3. カートに追加
  res = http.post(`${BASE_URL}/api/cart`, JSON.stringify({
    productId: productId,
    quantity: 1,
  }), {
    headers: { 'Content-Type': 'application/json' },
  });
  check(res, { 'added to cart': (r) => r.status === 200 });
  sleep(2);
  
  // 4. チェックアウト
  res = http.post(`${BASE_URL}/api/checkout`, JSON.stringify({
    paymentMethod: 'credit_card',
    shippingAddress: {
      street: '1-1-1 Shibuya',
      city: 'Tokyo',
      postalCode: '150-0001',
    },
  }), {
    headers: { 'Content-Type': 'application/json' },
  });
  check(res, { 
    'checkout successful': (r) => r.status === 200,
    'response time < 1s': (r) => r.timings.duration < 1000,
  });
  sleep(5);
}
```


---

## 実行手順 / Execution Procedures

### 事前準備 / Pre-execution Preparation

#### 1. 環境チェックリスト / Environment Checklist

```yaml
pre_test_checklist:
  infrastructure:
    - ☑ テスト環境が本番環境と同等の構成
    - ☑ すべてのサービスが正常に起動
    - ☑ データベースに適切なテストデータが投入済み
    - ☑ ネットワーク接続が安定
    - ☑ 監視ツールが正常に動作
  
  application:
    - ☑ 最新バージョンがデプロイ済み
    - ☑ 設定ファイルが正しく配置
    - ☑ ログレベルが適切に設定
    - ☑ キャッシュがウォームアップ済み
  
  tools:
    - ☑ 負荷生成ツールがインストール済み
    - ☑ テストスクリプトが検証済み
    - ☑ 監視ダッシュボードが準備済み
    - ☑ 結果保存先が準備済み
  
  team:
    - ☑ テスト実行者が準備完了
    - ☑ 関係者に通知済み
    - ☑ エスカレーション手順が明確
    - ☑ ロールバック計画が準備済み
```

#### 2. ベースライン測定 / Baseline Measurement

```bash
#!/bin/bash
# ベースライン測定スクリプト

echo "Starting baseline measurement..."

# システムリソースの記録
echo "Recording system resources..."
top -b -n 1 > baseline_cpu.txt
free -h > baseline_memory.txt
df -h > baseline_disk.txt

# アプリケーションメトリクスの記録
echo "Recording application metrics..."
curl -s http://localhost:8080/metrics > baseline_app_metrics.txt

# データベースメトリクスの記録
echo "Recording database metrics..."
psql -U admin -d mydb -c "SELECT * FROM pg_stat_database;" > baseline_db_metrics.txt

echo "Baseline measurement completed."
```

### テスト実行 / Test Execution

#### 1. k6 実行例 / k6 Execution Example

```bash
#!/bin/bash
# k6テスト実行スクリプト

TEST_NAME="load_test_$(date +%Y%m%d_%H%M%S)"
REPORT_DIR="./reports/${TEST_NAME}"

mkdir -p ${REPORT_DIR}

echo "Starting k6 load test: ${TEST_NAME}"

# k6実行（結果をInfluxDBに送信）
k6 run \
  --out influxdb=http://localhost:8086/k6 \
  --out json=${REPORT_DIR}/results.json \
  --summary-export=${REPORT_DIR}/summary.json \
  --vus 100 \
  --duration 30m \
  ./scripts/load_test.js

echo "Test completed. Results saved to ${REPORT_DIR}"

# 結果の簡易表示
cat ${REPORT_DIR}/summary.json | jq '.metrics'
```

#### 2. JMeter 実行例 / JMeter Execution Example

```bash
#!/bin/bash
# JMeter テスト実行スクリプト

TEST_PLAN="./test-plans/api_load_test.jmx"
REPORT_DIR="./reports/jmeter_$(date +%Y%m%d_%H%M%S)"

mkdir -p ${REPORT_DIR}

echo "Starting JMeter test..."

# JMeter実行（非GUIモード）
jmeter -n \
  -t ${TEST_PLAN} \
  -l ${REPORT_DIR}/results.jtl \
  -e \
  -o ${REPORT_DIR}/html \
  -Jthreads=200 \
  -Jrampup=300 \
  -Jduration=1800

echo "Test completed. HTML report: ${REPORT_DIR}/html/index.html"
```

#### 3. リアルタイム監視 / Real-time Monitoring

```yaml
monitoring_during_test:
  system_metrics:
    - CPU使用率
    - メモリ使用率
    - ディスクI/O
    - ネットワーク帯域
  
  application_metrics:
    - リクエスト数
    - レスポンスタイム
    - エラー率
    - スループット
  
  database_metrics:
    - クエリ実行時間
    - コネクション数
    - トランザクション数
    - デッドロック数
  
  alerts:
    - エラー率 > 1%
    - レスポンスタイムP95 > 1秒
    - CPU使用率 > 80%
    - メモリ使用率 > 85%
```

### テスト中断と回復 / Test Interruption and Recovery

```yaml
emergency_procedures:
  stop_conditions:
    - エラー率が5%を超過
    - システムリソースが枯渇
    - データベース接続エラー多発
    - 本番環境への影響を検知
  
  stop_procedure:
    1. "負荷生成を即座に停止"
    2. "現在のメトリクスを記録"
    3. "システム状態を確認"
    4. "ログを収集"
    5. "関係者に通知"
  
  recovery_procedure:
    1. "原因を特定"
    2. "問題を修正"
    3. "システムを再起動"
    4. "ヘルスチェック実施"
    5. "テスト再開判断"
```

---

## メトリクスと測定 / Metrics and Measurements

### 主要メトリクス / Key Metrics

#### 1. レスポンスタイム / Response Time

```yaml
response_time_metrics:
  measurements:
    - metric: "平均レスポンスタイム"
      target: "< 300ms"
      unit: "milliseconds"
    
    - metric: "中央値（P50）"
      target: "< 200ms"
      unit: "milliseconds"
    
    - metric: "95パーセンタイル（P95）"
      target: "< 500ms"
      unit: "milliseconds"
    
    - metric: "99パーセンタイル（P99）"
      target: "< 1000ms"
      unit: "milliseconds"
    
    - metric: "最大レスポンスタイム"
      threshold: "< 5000ms"
      unit: "milliseconds"
  
  calculation:
    formula: "Response Time = Time to Last Byte - Request Start Time"
    include:
      - "Network latency"
      - "Server processing time"
      - "Database query time"
```

#### 2. スループット / Throughput

```yaml
throughput_metrics:
  measurements:
    - metric: "秒あたりリクエスト数（RPS）"
      target: "> 1000"
      unit: "requests/second"
    
    - metric: "分あたりトランザクション数（TPM）"
      target: "> 60000"
      unit: "transactions/minute"
    
    - metric: "時間あたり処理データ量"
      target: "> 100GB"
      unit: "gigabytes/hour"
  
  calculation:
    formula: "Throughput = Total Requests / Total Time"
```

#### 3. エラー率 / Error Rate

```yaml
error_rate_metrics:
  measurements:
    - metric: "HTTP 4xx エラー率"
      target: "< 0.5%"
      unit: "percentage"
    
    - metric: "HTTP 5xx エラー率"
      target: "< 0.1%"
      unit: "percentage"
    
    - metric: "タイムアウト率"
      target: "< 0.05%"
      unit: "percentage"
    
    - metric: "総エラー率"
      target: "< 1%"
      unit: "percentage"
  
  calculation:
    formula: "Error Rate = (Failed Requests / Total Requests) × 100"
```

#### 4. リソース使用率 / Resource Utilization

```yaml
resource_utilization_metrics:
  cpu:
    - metric: "CPU使用率（平均）"
      target: "< 70%"
    - metric: "CPU使用率（ピーク）"
      threshold: "< 90%"
  
  memory:
    - metric: "メモリ使用率（平均）"
      target: "< 75%"
    - metric: "メモリ使用率（ピーク）"
      threshold: "< 85%"
  
  disk:
    - metric: "ディスクI/O使用率"
      target: "< 80%"
    - metric: "ディスクキュー長"
      target: "< 10"
  
  network:
    - metric: "ネットワーク帯域使用率"
      target: "< 70%"
    - metric: "パケット損失率"
      target: "< 0.01%"
```

### カスタムメトリクス / Custom Metrics

```javascript
// k6カスタムメトリクス例
import { Counter, Trend, Rate, Gauge } from 'k6/metrics';

// カウンター：イベントの総数
const checkoutAttempts = new Counter('checkout_attempts');
const checkoutSuccess = new Counter('checkout_success');

// トレンド：時系列の値（平均、パーセンタイルなど）
const checkoutDuration = new Trend('checkout_duration');
const dbQueryTime = new Trend('db_query_time');

// レート：成功/失敗の割合
const checkoutSuccessRate = new Rate('checkout_success_rate');

// ゲージ：現在の値
const activeUsers = new Gauge('active_users');

export default function () {
  checkoutAttempts.add(1);
  
  const startTime = Date.now();
  const res = http.post('https://api.example.com/checkout', payload);
  const duration = Date.now() - startTime;
  
  checkoutDuration.add(duration);
  
  if (res.status === 200) {
    checkoutSuccess.add(1);
    checkoutSuccessRate.add(true);
  } else {
    checkoutSuccessRate.add(false);
  }
  
  activeUsers.add(100); // 現在のアクティブユーザー数
}
```

---

## 結果分析 / Result Analysis

### 分析プロセス / Analysis Process

#### 1. データ収集 / Data Collection

```yaml
data_collection:
  sources:
    load_testing_tool:
      - リクエスト/レスポンスデータ
      - パフォーマンスメトリクス
      - エラーログ
    
    monitoring_tools:
      - システムメトリクス
      - アプリケーションログ
      - データベースメトリクス
    
    apm_tools:
      - トランザクショントレース
      - スロークエリログ
      - エラースタックトレース
  
  format:
    - JSON
    - CSV
    - Time-series data
```

#### 2. データ可視化 / Data Visualization

```yaml
visualization_types:
  time_series_graphs:
    - レスポンスタイムの推移
    - スループットの推移
    - エラー率の推移
    - リソース使用率の推移
  
  distribution_graphs:
    - レスポンスタイムの分布
    - パーセンタイル分析
    - ヒートマップ
  
  comparison_graphs:
    - ビフォー/アフター比較
    - 目標値との比較
    - 過去テストとの比較
```

#### 3. ボトルネック特定 / Bottleneck Identification

```yaml
bottleneck_analysis:
  application_layer:
    indicators:
      - 高いレスポンスタイム
      - CPUスパイク
      - メモリリーク
    
    investigation:
      - プロファイリング実施
      - コードレビュー
      - アルゴリズム最適化検討
  
  database_layer:
    indicators:
      - スローログの増加
      - ロック競合
      - コネクションプール枯渇
    
    investigation:
      - クエリ最適化
      - インデックス追加
      - コネクションプール調整
  
  infrastructure_layer:
    indicators:
      - ネットワーク遅延
      - ディスクI/O飽和
      - メモリ不足
    
    investigation:
      - リソース増強検討
      - アーキテクチャ見直し
      - キャッシュ戦略改善
```

### 分析レポート例 / Analysis Report Example

```markdown
# パフォーマンステスト分析レポート

## エグゼクティブサマリー
- テスト日時: 2025-01-15 14:00-16:00
- テストタイプ: ロードテスト
- テスト結果: 部分的に合格（改善が必要）

## 主要な発見事項
### 合格項目 ✅
- P50レスポンスタイム: 180ms（目標: < 200ms）
- スループット: 1,200 RPS（目標: > 1,000 RPS）
- エラー率: 0.05%（目標: < 0.1%）

### 不合格項目 ❌
- P95レスポンスタイム: 650ms（目標: < 500ms）
- P99レスポンスタイム: 1,500ms（目標: < 1,000ms）
- データベースCPU使用率: 85%（目標: < 70%）

## ボトルネック分析
1. **データベースクエリ性能**
   - 複雑なJOINクエリが500ms以上要している
   - インデックスの追加が必要
   - クエリの最適化が必要

2. **APIエンドポイント /api/search**
   - 高負荷時にレスポンスタイムが劣化
   - キャッシュの活用を推奨

3. **メモリ使用量**
   - 長時間稼働でメモリ使用量が増加傾向
   - メモリリークの可能性

## 推奨事項
1. データベースクエリの最適化（優先度: 高）
2. 検索エンドポイントへのキャッシュ追加（優先度: 高）
3. メモリリークの調査と修正（優先度: 中）
4. データベースコネクションプールの調整（優先度: 中）

## 次のステップ
1. 修正実施（1週間）
2. 再テスト実施（1週間後）
3. 本番リリース判断
```

---

## レポーティング / Reporting

### レポート構成 / Report Structure

```yaml
performance_test_report:
  sections:
    1_executive_summary:
      - テスト概要
      - 主要な結果
      - 総合評価
      - 推奨事項
    
    2_test_details:
      - テスト環境
      - テスト構成
      - テストシナリオ
      - 負荷パターン
    
    3_results:
      - パフォーマンスメトリクス
      - グラフとチャート
      - 目標値との比較
      - 傾向分析
    
    4_issues:
      - 検出された問題
      - 重大度と優先度
      - 影響範囲
      - 推奨される対応
    
    5_appendix:
      - 生データ
      - 詳細ログ
      - 設定ファイル
      - スクリプト
```

### レポート生成の自動化 / Automated Report Generation

```python
# レポート生成スクリプト例
import json
import matplotlib.pyplot as plt
from jinja2 import Template

def generate_performance_report(test_results_file, output_file):
    # テスト結果の読み込み
    with open(test_results_file, 'r') as f:
        results = json.load(f)
    
    # メトリクスの計算
    metrics = calculate_metrics(results)
    
    # グラフの生成
    generate_charts(metrics)
    
    # HTMLレポートの生成
    template = Template(open('report_template.html').read())
    html = template.render(metrics=metrics)
    
    with open(output_file, 'w') as f:
        f.write(html)
    
    print(f"Report generated: {output_file}")

def calculate_metrics(results):
    return {
        'avg_response_time': sum(results['response_times']) / len(results['response_times']),
        'p95_response_time': percentile(results['response_times'], 95),
        'p99_response_time': percentile(results['response_times'], 99),
        'throughput': results['total_requests'] / results['duration'],
        'error_rate': (results['failed_requests'] / results['total_requests']) * 100,
    }

def generate_charts(metrics):
    # レスポンスタイムのグラフ
    plt.figure(figsize=(10, 6))
    plt.plot(metrics['response_time_series'])
    plt.title('Response Time Over Time')
    plt.xlabel('Time (seconds)')
    plt.ylabel('Response Time (ms)')
    plt.savefig('response_time_chart.png')
    plt.close()
    
    # スループットのグラフ
    plt.figure(figsize=(10, 6))
    plt.plot(metrics['throughput_series'])
    plt.title('Throughput Over Time')
    plt.xlabel('Time (seconds)')
    plt.ylabel('Requests per Second')
    plt.savefig('throughput_chart.png')
    plt.close()

# 実行
generate_performance_report('results.json', 'performance_report.html')
```

---

## 継続的改善 / Continuous Improvement

### パフォーマンステストの自動化 / Test Automation

```yaml
ci_cd_integration:
  triggers:
    - プルリクエスト作成時
    - マージ前
    - 定期実行（毎日深夜）
  
  pipeline_stages:
    1_build:
      - コードビルド
      - コンテナイメージ作成
    
    2_deploy_test_env:
      - テスト環境へのデプロイ
      - ヘルスチェック
    
    3_smoke_test:
      - 基本的な機能確認
      - 軽量な負荷テスト
    
    4_performance_test:
      - 完全な負荷テスト（条件付き）
      - 自動分析
    
    5_report:
      - 結果のレポート生成
      - 閾値チェック
      - 通知送信
  
  pass_criteria:
    - すべてのメトリクスが目標値以内
    - エラー率 < 0.1%
    - リグレッションなし
```

### パフォーマンスリグレッション検出 / Performance Regression Detection

```python
# リグレッション検出スクリプト
def detect_performance_regression(current_results, baseline_results, threshold=0.1):
    """
    パフォーマンスリグレッションを検出
    threshold: 許容される劣化率（10% = 0.1）
    """
    regressions = []
    
    metrics_to_check = [
        'avg_response_time',
        'p95_response_time',
        'p99_response_time',
        'throughput',
        'error_rate'
    ]
    
    for metric in metrics_to_check:
        current_value = current_results[metric]
        baseline_value = baseline_results[metric]
        
        # 変化率の計算
        if metric in ['avg_response_time', 'p95_response_time', 'p99_response_time', 'error_rate']:
            # 値が小さいほうが良いメトリクス
            change_rate = (current_value - baseline_value) / baseline_value
            if change_rate > threshold:
                regressions.append({
                    'metric': metric,
                    'baseline': baseline_value,
                    'current': current_value,
                    'change': f"+{change_rate*100:.2f}%",
                    'severity': 'high' if change_rate > threshold * 2 else 'medium'
                })
        else:
            # 値が大きいほうが良いメトリクス（スループット）
            change_rate = (baseline_value - current_value) / baseline_value
            if change_rate > threshold:
                regressions.append({
                    'metric': metric,
                    'baseline': baseline_value,
                    'current': current_value,
                    'change': f"-{change_rate*100:.2f}%",
                    'severity': 'high' if change_rate > threshold * 2 else 'medium'
                })
    
    return regressions
```

### 定期レビュー / Regular Reviews

```yaml
performance_review_schedule:
  weekly:
    - パフォーマンスメトリクスのレビュー
    - トレンド分析
    - 小規模な改善実施
  
  monthly:
    - 包括的なパフォーマンス評価
    - キャパシティプランニング
    - 改善計画の策定
  
  quarterly:
    - 大規模な負荷テスト実施
    - アーキテクチャレビュー
    - パフォーマンス目標の見直し
```

---

## 参考資料 / References

### 関連ドキュメント / Related Documents
- [負荷テスト標準](/devin-organization-standards/04-quality-standards/load-testing.md)
- [品質基準](/devin-organization-standards/04-quality-standards/README.md)
- [フロントエンドアーキテクチャ](/devin-organization-standards/02-architecture-standards/frontend-architecture.md)
- [クラウドアーキテクチャ](/devin-organization-standards/02-architecture-standards/cloud-architecture.md)

### 外部リソース / External Resources
- [k6 Documentation](https://k6.io/docs/)
- [Apache JMeter](https://jmeter.apache.org/)
- [Gatling Documentation](https://gatling.io/docs/)
- [Web Performance Working Group](https://www.w3.org/webperf/)

---

## バージョン履歴 / Version History

| バージョン | 日付 | 変更内容 | 担当者 |
|---------|------|---------|--------|
| 1.0.0 | 2025-01-15 | 初版作成 | QA Team |

---

## 承認 / Approval

| 役割 | 氏名 | 承認日 |
|-----|------|--------|
| QA Manager | [Name] | 2025-01-15 |
| Performance Engineer | [Name] | 2025-01-15 |
| Engineering Manager | [Name] | 2025-01-15 |

---

## お問い合わせ / Contact

**QA Team**
- Email: qa-team@company.com
- Slack: #qa-performance
- 会議: 毎週木曜日 15:00-16:00
