---
version: "1.0.0"
last_updated: "2025-01-15"
status: "active"
owner: "QA Team"
category: "quality"
---

# 負荷テスト標準 / Load Testing Standards

## 目次 / Table of Contents

1. [概要 / Overview](#概要--overview)
2. [負荷テストの目的 / Load Testing Objectives](#負荷テストの目的--load-testing-objectives)
3. [負荷テストの種類 / Types of Load Testing](#負荷テストの種類--types-of-load-testing)
4. [負荷テスト戦略 / Load Testing Strategy](#負荷テスト戦略--load-testing-strategy)
5. [テストシナリオ設計 / Test Scenario Design](#テストシナリオ設計--test-scenario-design)
6. [負荷モデリング / Load Modeling](#負荷モデリング--load-modeling)
7. [実装ガイド / Implementation Guide](#実装ガイド--implementation-guide)
8. [実行とモニタリング / Execution and Monitoring](#実行とモニタリング--execution-and-monitoring)
9. [結果評価 / Result Evaluation](#結果評価--result-evaluation)
10. [トラブルシューティング / Troubleshooting](#トラブルシューティング--troubleshooting)
11. [ベストプラクティス / Best Practices](#ベストプラクティス--best-practices)
12. [参考資料 / References](#参考資料--references)

---

## 概要 / Overview

### 目的 / Purpose

本ドキュメントは、システムに対する負荷テストの標準的な手法と実践方法を定義します。

This document defines standard methodologies and practices for load testing systems.

### 適用範囲 / Scope

- Webアプリケーション
- RESTful API
- GraphQL API
- マイクロサービス
- データベース
- メッセージングシステム

### 定義 / Definitions

```yaml
definitions:
  load_testing:
    description: "想定される負荷条件下でのシステム動作を検証するテスト"
    purpose: "パフォーマンス特性の把握とキャパシティの検証"
  
  virtual_user:
    description: "実際のユーザーを模倣する仮想的なテストエージェント"
    alias: "VU, Concurrent User"
  
  think_time:
    description: "ユーザーがアクションとアクションの間に待機する時間"
    typical_range: "1-30秒"
  
  ramp_up:
    description: "仮想ユーザー数を段階的に増加させる期間"
    purpose: "システムへの急激な負荷を避ける"
  
  steady_state:
    description: "一定の負荷を維持する期間"
    purpose: "安定状態でのパフォーマンス測定"
```

---

## 負荷テストの目的 / Load Testing Objectives

### 主要目的 / Primary Objectives

#### 1. キャパシティ検証 / Capacity Verification

```yaml
capacity_verification:
  goals:
    - 想定同時ユーザー数の処理能力確認
    - 最大処理可能トランザクション数の特定
    - リソース使用率の把握
  
  success_criteria:
    - 目標同時ユーザー数で安定動作
    - レスポンスタイムが目標値以内
    - エラー率が許容範囲内
    - リソース使用率が適正範囲内
```

#### 2. スケーラビリティ評価 / Scalability Assessment

```yaml
scalability_assessment:
  goals:
    - 負荷増加に対する線形性の確認
    - スケールアウト効果の測定
    - ボトルネックの特定
  
  test_approach:
    - 段階的な負荷増加
    - リソース追加時の効果測定
    - スループット/レスポンスタイムの相関分析
```

#### 3. 安定性確認 / Stability Verification

```yaml
stability_verification:
  goals:
    - 長時間稼働での安定性確認
    - メモリリークの検出
    - リソース枯渇の検証
  
  duration:
    minimum: "2時間"
    recommended: "8-24時間"
    extended: "72時間（重要システム）"
```

#### 4. SLA遵守確認 / SLA Compliance Verification

```yaml
sla_verification:
  response_time:
    p50: "< 200ms"
    p95: "< 500ms"
    p99: "< 1000ms"
  
  availability:
    target: "99.9%"
    measurement_period: "月次"
  
  throughput:
    minimum: "1000 req/sec"
    peak: "5000 req/sec"
```

---

## 負荷テストの種類 / Types of Load Testing

### 1. ベースライン負荷テスト / Baseline Load Test

**目的**: 通常業務レベルでの基準性能を確立

```yaml
baseline_load_test:
  configuration:
    virtual_users: 100
    duration: "30分"
    ramp_up: "5分"
    steady_state: "20分"
    ramp_down: "5分"
  
  scenarios:
    - name: "通常業務フロー"
      distribution: "100%"
      think_time: "3-10秒"
  
  frequency: "毎回のリリース前"
  
  success_criteria:
    - すべてのメトリクスが目標値以内
    - 前回のベースラインとの差異が10%以内
```

### 2. ピーク負荷テスト / Peak Load Test

**目的**: 最大想定負荷での動作確認

```yaml
peak_load_test:
  configuration:
    virtual_users: 1000
    duration: "60分"
    ramp_up: "10分"
    steady_state: "40分"
    ramp_down: "10分"
  
  load_pattern:
    - phase: "通常負荷"
      users: 500
      duration: "15分"
    
    - phase: "ピーク1"
      users: 1000
      duration: "15分"
    
    - phase: "通常負荷"
      users: 500
      duration: "10分"
    
    - phase: "ピーク2"
      users: 1000
      duration: "10分"
  
  frequency: "月次"
```

### 3. ストレステスト / Stress Test

**目的**: システムの限界点と破壊点を特定

```yaml
stress_test:
  configuration:
    initial_users: 100
    max_users: 5000
    increment: 100
    increment_interval: "2分"
  
  stop_conditions:
    - エラー率 > 5%
    - レスポンスタイムP95 > 5秒
    - システムリソース枯渇
    - サービスダウン
  
  objectives:
    - 最大処理可能ユーザー数の特定
    - 障害発生時の挙動確認
    - 回復能力の検証
  
  frequency: "四半期ごと"
```

### 4. スパイクテスト / Spike Test

**目的**: 急激な負荷変動への対応力を検証

```yaml
spike_test:
  configuration:
    baseline_users: 200
    spike_users: 2000
    spike_duration: "2分"
    recovery_period: "5分"
  
  pattern:
    - duration: "10分"
      users: 200
      description: "通常負荷"
    
    - duration: "2分"
      users: 2000
      description: "スパイク発生"
    
    - duration: "5分"
      users: 200
      description: "回復期間"
    
    - duration: "2分"
      users: 2000
      description: "2回目のスパイク"
    
    - duration: "10分"
      users: 200
      description: "最終回復"
  
  frequency: "主要イベント前"
```

### 5. ソークテスト / Soak Test

**目的**: 長時間稼働での安定性とリソースリークを検証

```yaml
soak_test:
  configuration:
    virtual_users: 500
    duration: "24時間"
    ramp_up: "30分"
    steady_state: "23時間"
    ramp_down: "30分"
  
  monitoring_focus:
    - メモリ使用量の推移
    - コネクション数の推移
    - ディスク使用量の推移
    - レスポンスタイムの劣化
  
  alerts:
    - メモリ使用量が20%以上増加
    - レスポンスタイムが初期値の150%超過
    - エラー率が0.5%超過
  
  frequency: "リリース前（重要変更時）"
```

---

## 負荷テスト戦略 / Load Testing Strategy

### テスト計画フレームワーク / Test Planning Framework

```yaml
test_planning_framework:
  phase_1_preparation:
    activities:
      - 要件定義
      - 目標値設定
      - シナリオ設計
      - 環境準備
    duration: "1週間"
    deliverables:
      - テスト計画書
      - テストシナリオ
      - 環境構成図
  
  phase_2_implementation:
    activities:
      - スクリプト作成
      - データ準備
      - 環境構築
      - スモークテスト
    duration: "1週間"
    deliverables:
      - テストスクリプト
      - テストデータ
      - 検証済み環境
  
  phase_3_execution:
    activities:
      - ベースラインテスト
      - 各種負荷テスト
      - 結果収集
      - モニタリング
    duration: "3-5日"
    deliverables:
      - テスト結果データ
      - パフォーマンスログ
      - モニタリングデータ
  
  phase_4_analysis:
    activities:
      - データ分析
      - ボトルネック特定
      - 改善提案作成
      - レポート作成
    duration: "2-3日"
    deliverables:
      - 分析レポート
      - 改善提案書
      - エグゼクティブサマリー
```

### リスク管理 / Risk Management

```yaml
risk_management:
  identified_risks:
    test_environment:
      risk: "テスト環境が本番環境と異なる"
      impact: "高"
      mitigation: "環境の同等性を最大限確保、差異を文書化"
    
    test_data:
      risk: "テストデータが実データと乖離"
      impact: "中"
      mitigation: "本番データの匿名化コピーを使用"
    
    production_impact:
      risk: "テストが本番環境に影響"
      impact: "致命的"
      mitigation: "完全分離環境、ファイアウォール設定確認"
    
    resource_contention:
      risk: "他のテストとリソース競合"
      impact: "中"
      mitigation: "テストスケジュール調整、専用リソース確保"
    
    data_loss:
      risk: "テスト結果データの損失"
      impact: "中"
      mitigation: "自動バックアップ、複数保存先"
```

---

## テストシナリオ設計 / Test Scenario Design

### シナリオ設計原則 / Scenario Design Principles

```yaml
scenario_design_principles:
  realistic_user_behavior:
    - 実際のユーザー行動パターンを反映
    - 適切なthink timeを設定
    - ランダム性を導入
  
  business_criticality:
    - ビジネス上重要な機能を優先
    - 高頻度操作を重点的にテスト
    - エンドツーエンドフローを含める
  
  coverage:
    - 主要な機能を網羅
    - 正常系と異常系を含む
    - CRUD操作をバランスよく配置
  
  maintainability:
    - モジュール化されたスクリプト
    - パラメータ化された設定
    - 明確なドキュメント
```

### 主要シナリオ例 / Key Scenario Examples

#### シナリオ1: ECサイト - 商品閲覧〜購入 / E-commerce - Browse to Purchase

```javascript
// k6 スクリプト例
import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomItem, randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
  stages: [
    { duration: '5m', target: 500 },
    { duration: '20m', target: 500 },
    { duration: '5m', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    http_req_failed: ['rate<0.01'],
  },
};

const BASE_URL = 'https://api.example.com';

export default function () {
  // 1. ホームページ訪問 (40%のユーザー)
  if (Math.random() < 0.4) {
    let res = http.get(`${BASE_URL}/`);
    check(res, { 'homepage status 200': (r) => r.status === 200 });
    sleep(randomIntBetween(2, 5));
  }
  
  // 2. 商品検索 (60%のユーザー)
  const searchTerms = ['laptop', 'smartphone', 'headphones', 'camera'];
  let res = http.get(`${BASE_URL}/api/search?q=${randomItem(searchTerms)}`);
  check(res, { 'search status 200': (r) => r.status === 200 });
  sleep(randomIntBetween(3, 8));
  
  // 3. 商品詳細表示 (80%のユーザー)
  if (Math.random() < 0.8) {
    const products = JSON.parse(res.body).data;
    const productId = randomItem(products).id;
    
    res = http.get(`${BASE_URL}/api/products/${productId}`);
    check(res, { 'product detail status 200': (r) => r.status === 200 });
    sleep(randomIntBetween(5, 15));
    
    // 4. カート追加 (30%のユーザー)
    if (Math.random() < 0.3) {
      res = http.post(
        `${BASE_URL}/api/cart`,
        JSON.stringify({
          productId: productId,
          quantity: randomIntBetween(1, 3),
        }),
        { headers: { 'Content-Type': 'application/json' } }
      );
      check(res, { 'add to cart status 200': (r) => r.status === 200 });
      sleep(randomIntBetween(2, 5));
      
      // 5. チェックアウト (50%のユーザー)
      if (Math.random() < 0.5) {
        res = http.post(
          `${BASE_URL}/api/checkout`,
          JSON.stringify({
            paymentMethod: 'credit_card',
            shippingAddress: {
              street: '1-1-1 Test Street',
              city: 'Tokyo',
              postalCode: '100-0001',
            },
          }),
          { headers: { 'Content-Type': 'application/json' } }
        );
        check(res, { 'checkout status 200': (r) => r.status === 200 });
        sleep(randomIntBetween(3, 10));
      }
    }
  }
}
```

#### シナリオ2: SaaS - ダッシュボード操作 / SaaS - Dashboard Operations

```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '3m', target: 200 },
    { duration: '15m', target: 200 },
    { duration: '2m', target: 0 },
  ],
};

const BASE_URL = 'https://api.saas-example.com';
let authToken;

export function setup() {
  // 認証トークン取得
  const loginRes = http.post(
    `${BASE_URL}/api/auth/login`,
    JSON.stringify({
      email: 'testuser@example.com',
      password: 'TestPassword123',
    }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  
  return { token: JSON.parse(loginRes.body).token };
}

export default function (data) {
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${data.token}`,
  };
  
  // 1. ダッシュボード表示
  let res = http.get(`${BASE_URL}/api/dashboard`, { headers });
  check(res, { 'dashboard loaded': (r) => r.status === 200 });
  sleep(5);
  
  // 2. データ一覧取得
  res = http.get(`${BASE_URL}/api/data?page=1&limit=20`, { headers });
  check(res, { 'data list loaded': (r) => r.status === 200 });
  sleep(3);
  
  // 3. データ詳細表示 (70%のユーザー)
  if (Math.random() < 0.7) {
    const items = JSON.parse(res.body).data;
    const itemId = items[0].id;
    
    res = http.get(`${BASE_URL}/api/data/${itemId}`, { headers });
    check(res, { 'data detail loaded': (r) => r.status === 200 });
    sleep(8);
    
    // 4. データ更新 (20%のユーザー)
    if (Math.random() < 0.2) {
      res = http.put(
        `${BASE_URL}/api/data/${itemId}`,
        JSON.stringify({ status: 'updated', notes: 'Load test update' }),
        { headers }
      );
      check(res, { 'data updated': (r) => r.status === 200 });
      sleep(3);
    }
  }
  
  // 5. レポート生成 (10%のユーザー)
  if (Math.random() < 0.1) {
    res = http.post(
      `${BASE_URL}/api/reports/generate`,
      JSON.stringify({
        type: 'summary',
        dateRange: { from: '2025-01-01', to: '2025-01-15' },
      }),
      { headers }
    );
    check(res, { 'report generated': (r) => r.status === 200 });
    sleep(10);
  }
}
```

#### シナリオ3: API - マイクロサービス間通信 / API - Microservice Communication

```javascript
import http from 'k6/http';
import { check } from 'k6';

export const options = {
  scenarios: {
    user_service: {
      executor: 'constant-vus',
      vus: 100,
      duration: '10m',
      exec: 'userServiceTest',
    },
    order_service: {
      executor: 'constant-vus',
      vus: 150,
      duration: '10m',
      exec: 'orderServiceTest',
    },
    inventory_service: {
      executor: 'constant-vus',
      vus: 80,
      duration: '10m',
      exec: 'inventoryServiceTest',
    },
  },
  thresholds: {
    'http_req_duration{service:user}': ['p(95)<300'],
    'http_req_duration{service:order}': ['p(95)<500'],
    'http_req_duration{service:inventory}': ['p(95)<200'],
  },
};

export function userServiceTest() {
  const res = http.get('https://user-service.example.com/api/users/123', {
    tags: { service: 'user' },
  });
  check(res, { 'user service ok': (r) => r.status === 200 });
}

export function orderServiceTest() {
  const res = http.post(
    'https://order-service.example.com/api/orders',
    JSON.stringify({ userId: '123', items: [{ id: '456', qty: 2 }] }),
    {
      headers: { 'Content-Type': 'application/json' },
      tags: { service: 'order' },
    }
  );
  check(res, { 'order service ok': (r) => r.status === 201 });
}

export function inventoryServiceTest() {
  const res = http.get('https://inventory-service.example.com/api/stock/456', {
    tags: { service: 'inventory' },
  });
  check(res, { 'inventory service ok': (r) => r.status === 200 });
}
```

---

## 負荷モデリング / Load Modeling

### ユーザー行動モデル / User Behavior Model

```yaml
user_behavior_model:
  user_types:
    casual_browser:
      percentage: 50
      characteristics:
        - 短時間滞在
        - 多くのページを閲覧
        - 購入率低い
      think_time: "2-5秒"
      session_duration: "3-10分"
    
    intent_shopper:
      percentage: 30
      characteristics:
        - 目的を持って訪問
        - 少数のページを閲覧
        - 検索を頻繁に使用
      think_time: "3-8秒"
      session_duration: "5-15分"
    
    power_user:
      percentage: 15
      characteristics:
        - 頻繁に訪問
        - アカウント保有
        - 高い購入率
      think_time: "1-3秒"
      session_duration: "10-30分"
    
    admin_user:
      percentage: 5
      characteristics:
        - 管理機能使用
        - 長時間セッション
        - 複雑な操作
      think_time: "5-15秒"
      session_duration: "30-120分"
```

### 負荷分散パターン / Load Distribution Patterns

```yaml
load_distribution:
  time_of_day:
    business_hours:
      time: "09:00-18:00"
      load_multiplier: 1.5
      description: "通常業務時間、高負荷"
    
    lunch_time:
      time: "12:00-13:00"
      load_multiplier: 1.8
      description: "ランチタイムピーク"
    
    evening:
      time: "18:00-22:00"
      load_multiplier: 2.0
      description: "夕方〜夜のピーク"
    
    night:
      time: "22:00-06:00"
      load_multiplier: 0.3
      description: "深夜、低負荷"
  
  day_of_week:
    weekday:
      multiplier: 1.0
      description: "平日標準"
    
    weekend:
      multiplier: 1.5
      description: "週末増加（BtoC）"
  
  seasonal:
    normal:
      multiplier: 1.0
      months: [2, 3, 4, 5, 9, 10]
    
    peak:
      multiplier: 3.0
      months: [11, 12]
      description: "年末商戦"
    
    low:
      multiplier: 0.7
      months: [1, 6, 7, 8]
      description: "閑散期"
```

### 負荷計算式 / Load Calculation Formula

```python
# 負荷計算スクリプト例
def calculate_required_load(
    daily_active_users,
    avg_session_duration_minutes,
    avg_requests_per_session,
    peak_hour_percentage=0.15,
    concurrent_session_percentage=0.1
):
    """
    必要な負荷テストパラメータを計算
    
    Args:
        daily_active_users: 1日のアクティブユーザー数
        avg_session_duration_minutes: 平均セッション時間（分）
        avg_requests_per_session: セッションあたりの平均リクエスト数
        peak_hour_percentage: ピーク時の割合（デフォルト15%）
        concurrent_session_percentage: 同時セッション率（デフォルト10%）
    
    Returns:
        dict: 負荷テストパラメータ
    """
    
    # ピーク時のユーザー数
    peak_hour_users = daily_active_users * peak_hour_percentage
    
    # 同時接続ユーザー数
    concurrent_users = peak_hour_users * concurrent_session_percentage
    
    # 1時間あたりのセッション数
    sessions_per_hour = peak_hour_users / (avg_session_duration_minutes / 60)
    
    # 秒あたりリクエスト数（RPS）
    requests_per_second = (sessions_per_hour * avg_requests_per_session) / 3600
    
    return {
        'concurrent_users': int(concurrent_users),
        'peak_hour_users': int(peak_hour_users),
        'sessions_per_hour': int(sessions_per_hour),
        'requests_per_second': int(requests_per_second),
        'recommended_vus_baseline': int(concurrent_users * 0.5),
        'recommended_vus_peak': int(concurrent_users * 1.5),
        'recommended_vus_stress': int(concurrent_users * 3.0),
    }

# 使用例
params = calculate_required_load(
    daily_active_users=100000,
    avg_session_duration_minutes=15,
    avg_requests_per_session=50,
    peak_hour_percentage=0.15,
    concurrent_session_percentage=0.1
)

print("負荷テストパラメータ:")
print(f"  同時接続ユーザー数: {params['concurrent_users']}")
print(f"  ベースライン負荷: {params['recommended_vus_baseline']} VUs")
print(f"  ピーク負荷: {params['recommended_vus_peak']} VUs")
print(f"  ストレステスト負荷: {params['recommended_vus_stress']} VUs")
print(f"  目標RPS: {params['requests_per_second']}")
```


---

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



## トラブルシューティング / Troubleshooting

### 一般的な問題と解決策 / Common Issues and Solutions

```yaml
common_issues:
  high_response_time:
    symptoms:
      - レスポンスタイムが目標値を大幅に超過
      - P95, P99が極端に高い
      - レスポンスタイムが時間とともに増加
    
    investigation:
      - アプリケーションログの確認
      - データベースクエリのプロファイリング
      - APMツールでのトランザクション分析
      - ネットワークレイテンシの測定
    
    solutions:
      - スロークエリの最適化
      - インデックスの追加
      - キャッシュの導入
      - コネクションプールの調整
      - アプリケーションコードの最適化
  
  high_error_rate:
    symptoms:
      - 5xx エラーが多発
      - タイムアウトエラーが発生
      - 接続エラーが増加
    
    investigation:
      - エラーログの詳細確認
      - リソース使用状況の確認
      - データベース接続状態の確認
      - サービスヘルスチェック
    
    solutions:
      - リソース増強
      - タイムアウト値の調整
      - エラーハンドリングの改善
      - サーキットブレーカーの実装
      - リトライロジックの最適化
  
  memory_leak:
    symptoms:
      - メモリ使用量が時間とともに増加
      - GCが頻発
      - OutOfMemoryエラー
    
    investigation:
      - ヒープダンプの取得
      - プロファイリングツールの使用
      - オブジェクト保持状況の分析
    
    solutions:
      - メモリリークの修正
      - オブジェクトプールの適切な実装
      - リソースのクローズ漏れ修正
      - GC設定の最適化
  
  database_bottleneck:
    symptoms:
      - データベースCPUが高い
      - クエリ待機時間が長い
      - コネクションプール枯渇
      - デッドロック発生
    
    investigation:
      - スロークエリログの分析
      - 実行計画の確認
      - インデックス使用状況の確認
      - ロック競合の分析
    
    solutions:
      - クエリの最適化
      - 適切なインデックスの追加
      - コネクションプールサイズの調整
      - トランザクション分離レベルの見直し
      - リードレプリカの活用
  
  network_issues:
    symptoms:
      - 高いネットワークレイテンシ
      - タイムアウトエラー
      - 接続エラー
    
    investigation:
      - ネットワークトラフィックの確認
      - パケットロスの測定
      - DNS解決時間の確認
      - ファイアウォールログの確認
    
    solutions:
      - ネットワーク構成の最適化
      - CDNの活用
      - Keep-Aliveの有効化
      - DNS設定の最適化
```

### デバッグ手法 / Debugging Techniques

#### 詳細ロギング / Detailed Logging

```javascript
// k6デバッグロギング例
import http from 'k6/http';
import { check } from 'k6';

export default function () {
  const startTime = Date.now();
  
  // リクエスト送信
  const res = http.get('https://api.example.com/users', {
    tags: { name: 'GetUsers' }
  });
  
  const endTime = Date.now();
  const duration = endTime - startTime;
  
  // 詳細なログ出力
  console.log(JSON.stringify({
    timestamp: new Date().toISOString(),
    method: 'GET',
    url: res.url,
    status: res.status,
    duration: duration,
    size: res.body.length,
    timings: res.timings,
    headers: res.headers,
  }));
  
  // 異常な応答時間の場合、追加情報を出力
  if (duration > 1000) {
    console.warn(`Slow request detected: ${duration}ms`);
    console.log('Request details:', {
      dns: res.timings.dns,
      connecting: res.timings.connecting,
      tls_handshaking: res.timings.tls_handshaking,
      sending: res.timings.sending,
      waiting: res.timings.waiting,
      receiving: res.timings.receiving,
    });
  }
  
  // エラーの場合、詳細を出力
  if (res.status >= 400) {
    console.error('Request failed:', {
      status: res.status,
      body: res.body.substring(0, 500),
      error: res.error,
    });
  }
}
```

#### トレーシング / Tracing

```yaml
distributed_tracing:
  tool: "AWS X-Ray / Jaeger"
  
  implementation:
    - リクエストIDの生成と伝播
    - スパン情報の記録
    - サービス間の依存関係マッピング
  
  analysis:
    - エンドツーエンドのレイテンシ分析
    - ボトルネックの特定
    - サービス間の呼び出し関係の可視化
```

### パフォーマンスプロファイリング / Performance Profiling

```python
# アプリケーションプロファイリングスクリプト
import cProfile
import pstats
import io
from pstats import SortKey

def profile_endpoint(func):
    """
    関数のプロファイリングデコレーター
    """
    def wrapper(*args, **kwargs):
        profiler = cProfile.Profile()
        profiler.enable()
        
        result = func(*args, **kwargs)
        
        profiler.disable()
        
        # 結果の出力
        s = io.StringIO()
        ps = pstats.Stats(profiler, stream=s).sort_stats(SortKey.CUMULATIVE)
        ps.print_stats(20)  # 上位20件を表示
        
        print(s.getvalue())
        
        return result
    
    return wrapper

# 使用例
@profile_endpoint
def process_request():
    # 処理内容
    pass
```

---

## ベストプラクティス / Best Practices

### テスト設計のベストプラクティス / Test Design Best Practices

```yaml
test_design_best_practices:
  realistic_scenarios:
    - 実際のユーザー行動を模倣
    - 適切なthink timeを設定
    - データのバリエーションを確保
    - ランダム性を適切に導入
  
  incremental_approach:
    - 小規模から開始
    - 段階的に負荷を増加
    - 各段階で結果を確認
    - ボトルネックを順次解決
  
  isolation:
    - 専用のテスト環境を使用
    - 他のテストとの干渉を避ける
    - 本番環境への影響を防止
    - クリーンな状態から開始
  
  repeatability:
    - 再現可能なテストシナリオ
    - 固定されたテストデータ
    - バージョン管理されたスクリプト
    - ドキュメント化された手順
  
  comprehensive_monitoring:
    - 全レイヤーのモニタリング
    - リアルタイムダッシュボード
    - 異常検知アラート
    - 詳細なログ記録
```

### スクリプト開発のベストプラクティス / Script Development Best Practices

```javascript
// ✅ 良い例: モジュール化、再利用可能、保守しやすい

// config.js - 設定の分離
export const config = {
  baseUrl: __ENV.BASE_URL || 'https://api.example.com',
  timeout: 30000,
  thresholds: {
    http_req_duration: ['p(95)<500'],
    http_req_failed: ['rate<0.01'],
  },
};

// helpers.js - 共通機能
export function validateResponse(res, expectedStatus) {
  return check(res, {
    [`status is ${expectedStatus}`]: (r) => r.status === expectedStatus,
    'response has body': (r) => r.body && r.body.length > 0,
  });
}

// auth.js - 認証処理
export class Auth {
  constructor(baseUrl) {
    this.baseUrl = baseUrl;
  }
  
  login(credentials) {
    const res = http.post(
      `${this.baseUrl}/auth/login`,
      JSON.stringify(credentials),
      { headers: { 'Content-Type': 'application/json' } }
    );
    
    if (res.status === 200) {
      return JSON.parse(res.body).token;
    }
    
    throw new Error(`Login failed: ${res.status}`);
  }
}

// main.js - メインロジック
import { config } from './config.js';
import { validateResponse } from './helpers.js';
import { Auth } from './auth.js';

export const options = {
  stages: [
    { duration: '2m', target: 50 },
    { duration: '5m', target: 50 },
    { duration: '2m', target: 0 },
  ],
  thresholds: config.thresholds,
};

const auth = new Auth(config.baseUrl);

export default function () {
  const token = auth.login({
    email: 'user@example.com',
    password: 'password',
  });
  
  const headers = {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json',
  };
  
  const res = http.get(`${config.baseUrl}/api/users`, { headers });
  validateResponse(res, 200);
}

// ❌ 悪い例: すべてが1ファイル、ハードコード、再利用困難
/*
export default function () {
  // 認証
  let res = http.post('https://api.example.com/auth/login', '{"email":"user@example.com","password":"password"}');
  let token = JSON.parse(res.body).token;
  
  // APIコール
  res = http.get('https://api.example.com/api/users', {
    headers: { 'Authorization': 'Bearer ' + token }
  });
  
  // その他の処理...
}
*/
```

### データ管理のベストプラクティス / Data Management Best Practices

```yaml
data_management:
  test_data_generation:
    approach: "スクリプトによる自動生成"
    benefits:
      - 再現性の確保
      - スケーラビリティ
      - バリエーションの制御
    
    example: |
      // data-generator.js
      export function generateUser(index) {
        return {
          id: `user_${index}`,
          email: `user${index}@example.com`,
          name: `Test User ${index}`,
          createdAt: new Date(Date.now() - Math.random() * 31536000000).toISOString()
        };
      }
      
      export function generateUsers(count) {
        return Array.from({ length: count }, (_, i) => generateUser(i));
      }
  
  data_cleanup:
    strategy: "テスト後の自動クリーンアップ"
    implementation:
      - teardown関数での削除
      - TTL設定
      - 識別可能なプレフィックス使用
    
    example: |
      export function teardown(data) {
        // テストデータの削除
        const testUsers = data.createdUsers;
        testUsers.forEach(userId => {
          http.del(`${BASE_URL}/api/users/${userId}`);
        });
      }
  
  sensitive_data:
    handling: "本番データの匿名化"
    requirements:
      - PII（個人識別情報）の除去
      - クレジットカード情報のマスキング
      - GDPR/CCPA準拠
    
    tools:
      - AWS Glue DataBrew
      - Custom anonymization scripts
```

### 継続的負荷テスト / Continuous Load Testing

```yaml
continuous_load_testing:
  ci_cd_integration:
    triggers:
      - プルリクエスト作成時（軽量テスト）
      - メインブランチマージ時（完全テスト）
      - 定期実行（日次/週次）
    
    pipeline_stages:
      - name: "Build & Deploy"
        actions:
          - アプリケーションビルド
          - テスト環境へのデプロイ
      
      - name: "Smoke Test"
        actions:
          - 基本機能確認
          - 10 VUs × 2分
          - クリティカルパスのみ
      
      - name: "Load Test"
        actions:
          - 標準負荷テスト
          - 100 VUs × 10分
          - 主要シナリオ実行
        conditions:
          - Smoke Test通過
          - 本番デプロイ前
      
      - name: "Analysis & Report"
        actions:
          - 結果分析
          - ベースライン比較
          - レポート生成
          - 通知送信
      
      - name: "Gate Check"
        actions:
          - 閾値チェック
          - リグレッション検出
          - 承認/却下判定
  
  automated_decision:
    pass_criteria:
      - すべての閾値を満たす
      - ベースラインからの劣化 < 10%
      - エラー率 < 0.1%
    
    actions_on_pass:
      - パイプライン続行
      - 次ステージへ進む
      - 成功通知送信
    
    actions_on_fail:
      - パイプライン停止
      - 詳細レポート生成
      - 担当者への通知
      - 自動Issue作成
```

### コスト最適化 / Cost Optimization

```yaml
cost_optimization:
  test_environment:
    strategies:
      - オンデマンドリソースの使用
      - テスト時のみリソース起動
      - スポットインスタンスの活用
      - 適切なインスタンスサイズ選択
    
    automation:
      - 自動起動/停止スクリプト
      - スケジュールベースの実行
      - 未使用リソースの自動削除
  
  test_execution:
    strategies:
      - 効率的なテスト設計
      - 不要な繰り返しの削減
      - 並列実行の活用
      - 段階的なテスト実行
    
    monitoring:
      - コスト追跡
      - リソース使用率の監視
      - 最適化機会の特定
```

---

## 参考資料 / References

### 関連ドキュメント / Related Documents

```yaml
internal_documents:
  - title: "パフォーマンステスト標準"
    path: "/devin-organization-standards/04-quality-standards/performance-testing.md"
    description: "パフォーマンステスト全般の標準"
  
  - title: "フロントエンドアーキテクチャ"
    path: "/devin-organization-standards/02-architecture-standards/frontend-architecture.md"
    description: "フロントエンドのパフォーマンス最適化"
  
  - title: "クラウドアーキテクチャ"
    path: "/devin-organization-standards/02-architecture-standards/cloud-architecture.md"
    description: "インフラのスケーラビリティ設計"
  
  - title: "品質基準"
    path: "/devin-organization-standards/04-quality-standards/README.md"
    description: "全体的な品質基準"
```

### 外部リソース / External Resources

```yaml
official_documentation:
  - title: "k6 Documentation"
    url: "https://k6.io/docs/"
    description: "k6の公式ドキュメント"
  
  - title: "Apache JMeter User Manual"
    url: "https://jmeter.apache.org/usermanual/index.html"
    description: "JMeterの使用方法"
  
  - title: "Gatling Documentation"
    url: "https://gatling.io/docs/"
    description: "Gatlingの公式ドキュメント"

books:
  - title: "The Art of Application Performance Testing"
    author: "Ian Molyneaux"
    description: "パフォーマンステストの包括的ガイド"
  
  - title: "Performance Testing Guidance for Web Applications"
    author: "Microsoft patterns & practices"
    url: "https://docs.microsoft.com/en-us/previous-versions/msp-n-p/bb924375(v=pandp.10)"
  
  - title: "Web Performance in Action"
    author: "Jeremy Wagner"
    description: "Webパフォーマンス最適化の実践"

online_courses:
  - title: "Load Testing with k6"
    provider: "Test Automation University"
    url: "https://testautomationu.applitools.com/k6-load-testing/"
  
  - title: "Performance Testing Masterclass"
    provider: "Udemy"
    description: "JMeterを使ったパフォーマンステスト"
```

### ツールとライブラリ / Tools and Libraries

```yaml
load_testing_tools:
  - name: "k6"
    type: "オープンソース"
    language: "JavaScript"
    best_for: "API負荷テスト、CI/CD統合"
    url: "https://k6.io/"
  
  - name: "Apache JMeter"
    type: "オープンソース"
    language: "Java"
    best_for: "多様なプロトコル対応、GUI"
    url: "https://jmeter.apache.org/"
  
  - name: "Gatling"
    type: "オープンソース/商用"
    language: "Scala"
    best_for: "高負荷シナリオ、詳細レポート"
    url: "https://gatling.io/"
  
  - name: "Locust"
    type: "オープンソース"
    language: "Python"
    best_for: "カスタムシナリオ、分散実行"
    url: "https://locust.io/"
  
  - name: "Artillery"
    type: "オープンソース"
    language: "JavaScript"
    best_for: "モダンアプリケーション、WebSocket"
    url: "https://artillery.io/"

monitoring_tools:
  - name: "Grafana"
    purpose: "メトリクス可視化"
    url: "https://grafana.com/"
  
  - name: "Prometheus"
    purpose: "メトリクス収集"
    url: "https://prometheus.io/"
  
  - name: "Datadog"
    purpose: "APM、インフラ監視"
    url: "https://www.datadoghq.com/"
  
  - name: "New Relic"
    purpose: "APM、パフォーマンス分析"
    url: "https://newrelic.com/"
```

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
- **Email**: qa-team@company.com
- **Slack**: #qa-load-testing
- **会議**: 毎週木曜日 15:00-16:00

**Performance Engineering Team**
- **Email**: performance@company.com
- **Slack**: #performance-engineering
- **オンコール**: performance-oncall@company.com

