# 負荷テスト標準

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

