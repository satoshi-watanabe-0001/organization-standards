# 負荷テスト標準

## 実装ガイド / Implementation Guide

### k6 実装ガイド / k6 Implementation Guide

#### プロジェクト構造 / Project Structure

```
load-tests/
├── config/
│   ├── dev.json
│   ├── staging.json
│   └── production.json
├── data/
│   ├── users.json
│   ├── products.json
│   └── test-data-generator.js
├── scenarios/
│   ├── user-flow.js
│   ├── api-test.js
│   └── checkout-flow.js
├── lib/
│   ├── auth.js
│   ├── helpers.js
│   └── custom-metrics.js
├── reports/
│   └── .gitkeep
├── package.json
└── run-tests.sh
```

#### 設定ファイル例 / Configuration File Example

```json
// config/staging.json
{
  "baseUrl": "https://staging-api.example.com",
  "stages": [
    { "duration": "5m", "target": 100 },
    { "duration": "20m", "target": 100 },
    { "duration": "5m", "target": 0 }
  ],
  "thresholds": {
    "http_req_duration": ["p(95)<500", "p(99)<1000"],
    "http_req_failed": ["rate<0.01"],
    "checks": ["rate>0.95"]
  },
  "tags": {
    "environment": "staging",
    "test_type": "load"
  }
}
```

#### ヘルパー関数 / Helper Functions

```javascript
// lib/helpers.js
import { check } from 'k6';

export function validateResponse(response, expectedStatus, checkName) {
  return check(response, {
    [`${checkName}: status is ${expectedStatus}`]: (r) => r.status === expectedStatus,
    [`${checkName}: has body`]: (r) => r.body.length > 0,
    [`${checkName}: response time < 1s`]: (r) => r.timings.duration < 1000,
  });
}

export function randomElement(array) {
  return array[Math.floor(Math.random() * array.length)];
}

export function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

export function generateEmail() {
  const timestamp = Date.now();
  const random = Math.floor(Math.random() * 10000);
  return `testuser${timestamp}${random}@example.com`;
}

export function sleep(min, max) {
  const duration = max ? randomInt(min, max) : min;
  return require('k6').sleep(duration);
}
```

#### 認証モジュール / Authentication Module

```javascript
// lib/auth.js
import http from 'k6/http';
import { check } from 'k6';

export class AuthManager {
  constructor(baseUrl) {
    this.baseUrl = baseUrl;
    this.tokens = new Map();
  }

  login(email, password) {
    const cacheKey = `${email}:${password}`;
    
    // キャッシュから取得
    if (this.tokens.has(cacheKey)) {
      const cached = this.tokens.get(cacheKey);
      if (cached.expiresAt > Date.now()) {
        return cached.token;
      }
    }

    // ログインリクエスト
    const response = http.post(
      `${this.baseUrl}/api/auth/login`,
      JSON.stringify({ email, password }),
      {
        headers: { 'Content-Type': 'application/json' },
      }
    );

    check(response, {
      'login successful': (r) => r.status === 200,
    });

    if (response.status === 200) {
      const body = JSON.parse(response.body);
      const token = body.token;
      
      // キャッシュに保存（1時間有効）
      this.tokens.set(cacheKey, {
        token: token,
        expiresAt: Date.now() + 3600000,
      });
      
      return token;
    }

    throw new Error(`Login failed: ${response.status}`);
  }

  getAuthHeaders(token) {
    return {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    };
  }

  refreshToken(refreshToken) {
    const response = http.post(
      `${this.baseUrl}/api/auth/refresh`,
      JSON.stringify({ refreshToken }),
      {
        headers: { 'Content-Type': 'application/json' },
      }
    );

    if (response.status === 200) {
      return JSON.parse(response.body).token;
    }

    throw new Error(`Token refresh failed: ${response.status}`);
  }
}
```

#### カスタムメトリクス / Custom Metrics

```javascript
// lib/custom-metrics.js
import { Counter, Trend, Rate, Gauge } from 'k6/metrics';

export const customMetrics = {
  // カウンター
  checkoutAttempts: new Counter('checkout_attempts'),
  checkoutSuccesses: new Counter('checkout_successes'),
  checkoutFailures: new Counter('checkout_failures'),
  
  // トレンド
  checkoutDuration: new Trend('checkout_duration'),
  searchDuration: new Trend('search_duration'),
  dbQueryTime: new Trend('db_query_time'),
  
  // レート
  checkoutSuccessRate: new Rate('checkout_success_rate'),
  apiErrorRate: new Rate('api_error_rate'),
  
  // ゲージ
  activeUsers: new Gauge('active_users'),
  cartSize: new Gauge('cart_size'),
};

export function recordCheckout(success, duration) {
  customMetrics.checkoutAttempts.add(1);
  
  if (success) {
    customMetrics.checkoutSuccesses.add(1);
    customMetrics.checkoutSuccessRate.add(true);
  } else {
    customMetrics.checkoutFailures.add(1);
    customMetrics.checkoutSuccessRate.add(false);
  }
  
  customMetrics.checkoutDuration.add(duration);
}
```

#### メインテストスクリプト / Main Test Script

```javascript
// scenarios/full-user-journey.js
import http from 'k6/http';
import { check, sleep } from 'k6';
import { AuthManager } from '../lib/auth.js';
import { validateResponse, randomElement, randomInt, sleep as randomSleep } from '../lib/helpers.js';
import { customMetrics, recordCheckout } from '../lib/custom-metrics.js';

// 設定の読み込み
const config = JSON.parse(open('../config/staging.json'));

export const options = {
  stages: config.stages,
  thresholds: config.thresholds,
  tags: config.tags,
};

const authManager = new AuthManager(config.baseUrl);

export function setup() {
  // テストデータの読み込み
  const users = JSON.parse(open('../data/users.json'));
  const products = JSON.parse(open('../data/products.json'));
  
  return { users, products };
}

export default function (data) {
  const user = randomElement(data.users);
  const product = randomElement(data.products);
  
  // 1. ログイン
  const token = authManager.login(user.email, user.password);
  const headers = authManager.getAuthHeaders(token);
  
  randomSleep(2, 5);
  
  // 2. ホームページアクセス
  let res = http.get(`${config.baseUrl}/`, { headers });
  validateResponse(res, 200, 'Homepage');
  randomSleep(3, 8);
  
  // 3. 商品検索
  const searchStart = Date.now();
  res = http.get(`${config.baseUrl}/api/search?q=${product.category}`, { headers });
  customMetrics.searchDuration.add(Date.now() - searchStart);
  validateResponse(res, 200, 'Search');
  randomSleep(2, 5);
  
  // 4. 商品詳細
  res = http.get(`${config.baseUrl}/api/products/${product.id}`, { headers });
  validateResponse(res, 200, 'Product Detail');
  randomSleep(5, 10);
  
  // 5. カートに追加
  res = http.post(
    `${config.baseUrl}/api/cart`,
    JSON.stringify({
      productId: product.id,
      quantity: randomInt(1, 3),
    }),
    { headers }
  );
  validateResponse(res, 200, 'Add to Cart');
  
  const cartData = JSON.parse(res.body);
  customMetrics.cartSize.add(cartData.itemCount || 1);
  
  randomSleep(3, 7);
  
  // 6. チェックアウト（50%の確率）
  if (Math.random() < 0.5) {
    const checkoutStart = Date.now();
    
    res = http.post(
      `${config.baseUrl}/api/checkout`,
      JSON.stringify({
        paymentMethod: 'credit_card',
        shippingAddress: user.address,
      }),
      { headers }
    );
    
    const checkoutDuration = Date.now() - checkoutStart;
    const success = res.status === 200;
    
    recordCheckout(success, checkoutDuration);
    validateResponse(res, 200, 'Checkout');
    
    randomSleep(5, 10);
  }
  
  customMetrics.activeUsers.add(1);
}

export function teardown(data) {
  console.log('Test completed successfully');
}
```

#### 実行スクリプト / Execution Script

```bash
#!/bin/bash
# run-tests.sh

set -e

# 設定
ENVIRONMENT=${1:-staging}
TEST_TYPE=${2:-load}
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
REPORT_DIR="./reports/${ENVIRONMENT}_${TEST_TYPE}_${TIMESTAMP}"

# ディレクトリ作成
mkdir -p ${REPORT_DIR}

echo "==========================================="
echo "Load Test Execution"
echo "==========================================="
echo "Environment: ${ENVIRONMENT}"
echo "Test Type: ${TEST_TYPE}"
echo "Timestamp: ${TIMESTAMP}"
echo "Report Dir: ${REPORT_DIR}"
echo "==========================================="

# テストの選択
case ${TEST_TYPE} in
  baseline)
    TEST_SCRIPT="scenarios/baseline-test.js"
    ;;
  load)
    TEST_SCRIPT="scenarios/full-user-journey.js"
    ;;
  stress)
    TEST_SCRIPT="scenarios/stress-test.js"
    ;;
  spike)
    TEST_SCRIPT="scenarios/spike-test.js"
    ;;
  *)
    echo "Unknown test type: ${TEST_TYPE}"
    exit 1
    ;;
esac

# k6実行
echo "Starting k6 test..."
k6 run \
  --config config/${ENVIRONMENT}.json \
  --out json=${REPORT_DIR}/results.json \
  --out influxdb=http://localhost:8086/k6 \
  --summary-export=${REPORT_DIR}/summary.json \
  ${TEST_SCRIPT} \
  2>&1 | tee ${REPORT_DIR}/console.log

# 結果の処理
echo "Processing results..."
node scripts/process-results.js ${REPORT_DIR}/results.json ${REPORT_DIR}

# レポート生成
echo "Generating HTML report..."
node scripts/generate-report.js ${REPORT_DIR}

echo "==========================================="
echo "Test completed!"
echo "Report: ${REPORT_DIR}/index.html"
echo "==========================================="

# Slackに通知（オプション）
if [ -n "${SLACK_WEBHOOK_URL}" ]; then
  curl -X POST ${SLACK_WEBHOOK_URL} \
    -H 'Content-Type: application/json' \
    -d "{\"text\":\"Load test completed: ${ENVIRONMENT} - ${TEST_TYPE}\n Report: ${REPORT_DIR}/index.html\"}"
fi
```

---

## 実行とモニタリング / Execution and Monitoring

### 実行前チェックリスト / Pre-execution Checklist

```yaml
pre_execution_checklist:
  environment:
    - ☑ テスト環境が正常に稼働
    - ☑ すべてのサービスがヘルシー
    - ☑ データベースが準備完了
    - ☑ 監視ツールが動作中
    - ☑ ログ収集が有効
  
  configuration:
    - ☑ テストスクリプトが検証済み
    - ☑ 負荷パラメータが正しく設定
    - ☑ 環境変数が設定済み
    - ☑ 認証情報が有効
  
  coordination:
    - ☑ 関係者に通知済み
    - ☑ テスト時間帯が確保済み
    - ☑ エスカレーションパスが明確
    - ☑ ロールバック計画が準備済み
  
  monitoring:
    - ☑ ダッシュボードが準備済み
    - ☑ アラートが設定済み
    - ☑ ログ保存先が確保済み
    - ☑ 結果保存先が準備済み
```

### リアルタイムモニタリング / Real-time Monitoring

#### Grafana ダッシュボード設定 / Grafana Dashboard Setup

```yaml
grafana_dashboard:
  panels:
    response_time:
      title: "Response Time"
      metrics:
        - http_req_duration (p50, p95, p99)
      visualization: "Time series"
      unit: "milliseconds"
    
    throughput:
      title: "Throughput"
      metrics:
        - http_reqs (rate per second)
      visualization: "Graph"
      unit: "req/s"
    
    error_rate:
      title: "Error Rate"
      metrics:
        - http_req_failed
      visualization: "Stat"
      unit: "percent"
      threshold:
        - value: 1
          color: "yellow"
        - value: 5
          color: "red"
    
    virtual_users:
      title: "Active Virtual Users"
      metrics:
        - vus
        - vus_max
      visualization: "Graph"
      unit: "users"
    
    system_resources:
      title: "System Resources"
      metrics:
        - cpu_usage
        - memory_usage
        - disk_io
      visualization: "Time series"
      unit: "percent"
    
    custom_metrics:
      title: "Business Metrics"
      metrics:
        - checkout_success_rate
        - active_users
        - cart_size
      visualization: "Mixed"
```

#### CloudWatch メトリクス / CloudWatch Metrics

```yaml
cloudwatch_monitoring:
  application_metrics:
    - metric: "RequestCount"
      namespace: "AWS/ApplicationELB"
      statistic: "Sum"
      period: 60
    
    - metric: "TargetResponseTime"
      namespace: "AWS/ApplicationELB"
      statistic: "Average"
      period: 60
    
    - metric: "HTTPCode_Target_4XX_Count"
      namespace: "AWS/ApplicationELB"
      statistic: "Sum"
      period: 60
    
    - metric: "HTTPCode_Target_5XX_Count"
      namespace: "AWS/ApplicationELB"
      statistic: "Sum"
      period: 60
  
  ecs_metrics:
    - metric: "CPUUtilization"
      namespace: "AWS/ECS"
      statistic: "Average"
      period: 60
    
    - metric: "MemoryUtilization"
      namespace: "AWS/ECS"
      statistic: "Average"
      period: 60
  
  rds_metrics:
    - metric: "CPUUtilization"
      namespace: "AWS/RDS"
      statistic: "Average"
      period: 60
    
    - metric: "DatabaseConnections"
      namespace: "AWS/RDS"
      statistic: "Sum"
      period: 60
    
    - metric: "ReadLatency"
      namespace: "AWS/RDS"
      statistic: "Average"
      period: 60
    
    - metric: "WriteLatency"
      namespace: "AWS/RDS"
      statistic: "Average"
      period: 60
```

### アラート設定 / Alert Configuration

```yaml
alerts:
  critical:
    - name: "High Error Rate"
      condition: "error_rate > 5%"
      duration: "2 minutes"
      action: "停止を検討、即時通知"
    
    - name: "Service Down"
      condition: "availability < 95%"
      duration: "1 minute"
      action: "テスト即時停止"
    
    - name: "Database CPU Critical"
      condition: "db_cpu > 90%"
      duration: "3 minutes"
      action: "テスト停止、調査開始"
  
  warning:
    - name: "Elevated Response Time"
      condition: "p95_response_time > 1000ms"
      duration: "5 minutes"
      action: "監視強化、記録"
    
    - name: "High CPU Usage"
      condition: "cpu_usage > 80%"
      duration: "5 minutes"
      action: "リソース使用状況確認"
    
    - name: "Memory Pressure"
      condition: "memory_usage > 85%"
      duration: "5 minutes"
      action: "メモリリーク調査準備"
  
  info:
    - name: "Load Increase"
      condition: "vus > baseline * 1.5"
      action: "記録のみ"
    
    - name: "Response Time Trend"
      condition: "p95_response_time increased by 20%"
      duration: "10 minutes"
      action: "トレンド分析"
```

---

## 結果評価 / Result Evaluation

### 評価基準 / Evaluation Criteria

```yaml
evaluation_criteria:
  pass_conditions:
    response_time:
      - p50 < 200ms
      - p95 < 500ms
      - p99 < 1000ms
    
    throughput:
      - achieved >= target * 0.95
      - sustained for test duration
    
    error_rate:
      - total < 0.1%
      - 4xx < 0.5%
      - 5xx < 0.1%
    
    resource_utilization:
      - cpu < 80%
      - memory < 85%
      - disk_io < 80%
    
    stability:
      - no memory leaks
      - no performance degradation
      - graceful recovery
  
  marginal_pass:
    - 90%以上の基準を満たす
    - 重大な問題がない
    - 改善計画が明確
  
  fail_conditions:
    - 重要基準を満たさない
    - システム障害発生
    - データ損失
    - セキュリティ問題
```

### 比較分析 / Comparative Analysis

```python
# 結果比較スクリプト
import json
import pandas as pd
import matplotlib.pyplot as plt

def compare_test_results(baseline_file, current_file):
    """
    ベースラインと現在のテスト結果を比較
    """
    with open(baseline_file, 'r') as f:
        baseline = json.load(f)
    
    with open(current_file, 'r') as f:
        current = json.load(f)
    
    # メトリクスの比較
    comparison = {
        'metric': [],
        'baseline': [],
        'current': [],
        'change_pct': [],
        'status': []
    }
    
    metrics = [
        ('p50_response_time', 'lower_is_better'),
        ('p95_response_time', 'lower_is_better'),
        ('p99_response_time', 'lower_is_better'),
        ('throughput', 'higher_is_better'),
        ('error_rate', 'lower_is_better'),
    ]
    
    for metric, direction in metrics:
        baseline_value = baseline['metrics'][metric]
        current_value = current['metrics'][metric]
        
        change_pct = ((current_value - baseline_value) / baseline_value) * 100
        
        # ステータス判定
        if direction == 'lower_is_better':
            if change_pct < -10:
                status = '✅ Improved'
            elif change_pct > 10:
                status = '❌ Degraded'
            else:
                status = '➖ Stable'
        else:
            if change_pct > 10:
                status = '✅ Improved'
            elif change_pct < -10:
                status = '❌ Degraded'
            else:
                status = '➖ Stable'
        
        comparison['metric'].append(metric)
        comparison['baseline'].append(baseline_value)
        comparison['current'].append(current_value)
        comparison['change_pct'].append(change_pct)
        comparison['status'].append(status)
    
    df = pd.DataFrame(comparison)
    
    # レポート生成
    print("=" * 80)
    print("Performance Comparison Report")
    print("=" * 80)
    print(df.to_string(index=False))
    print("=" * 80)
    
    # グラフ生成
    generate_comparison_charts(df)
    
    return df

def generate_comparison_charts(df):
    """
    比較チャートの生成
    """
    fig, axes = plt.subplots(2, 2, figsize=(15, 10))
    fig.suptitle('Performance Comparison', fontsize=16)
    
    # Response Time比較
    ax1 = axes[0, 0]
    response_time_metrics = df[df['metric'].str.contains('response_time')]
    ax1.bar(response_time_metrics['metric'], response_time_metrics['baseline'], 
            label='Baseline', alpha=0.7)
    ax1.bar(response_time_metrics['metric'], response_time_metrics['current'], 
            label='Current', alpha=0.7)
    ax1.set_title('Response Time Comparison')
    ax1.set_ylabel('Time (ms)')
    ax1.legend()
    ax1.tick_params(axis='x', rotation=45)
    
    # 変化率
    ax2 = axes[0, 1]
    colors = ['green' if '✅' in s else 'red' if '❌' in s else 'gray' 
              for s in df['status']]
    ax2.barh(df['metric'], df['change_pct'], color=colors, alpha=0.7)
    ax2.set_title('Performance Change (%)')
    ax2.set_xlabel('Change (%)')
    ax2.axvline(x=0, color='black', linestyle='--', linewidth=0.5)
    
    plt.tight_layout()
    plt.savefig('comparison_report.png', dpi=150)
    print("\nComparison charts saved to: comparison_report.png")

# 使用例
compare_test_results('baseline_results.json', 'current_results.json')
```



