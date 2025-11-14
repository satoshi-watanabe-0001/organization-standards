# 負荷テスト標準

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

