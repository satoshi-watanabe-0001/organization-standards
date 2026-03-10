# CI/CDパイプライン標準

## ドキュメント情報

- **バージョン**: 1.0.0
- **最終更新日**: 2025-10-24
- **ステータス**: アクティブ
- **管理者**: DevOpsチーム / Platform Engineering
- **カテゴリー**: 開発プロセス

## 目次

1. [概要](#概要)
2. [パイプラインアーキテクチャ](#パイプラインアーキテクチャ)
3. [継続的インテグレーション（CI）](#継続的インテグレーションci)
4. [継続的デプロイメント（CD）](#継続的デプロイメントcd)
5. [環境戦略](#環境戦略)
6. [デプロイ戦略](#デプロイ戦略)
7. [パイプラインセキュリティ](#パイプラインセキュリティ)
8. [監視と可観測性](#監視と可観測性)
9. [ロールバック手順](#ロールバック手順)
10. [ベストプラクティス](#ベストプラクティス)
11. [Devin AIガイドライン](#devin-aiガイドライン)

---

## 概要

### 目的

本ドキュメントは、すべてのプロジェクトにおける継続的インテグレーションおよび継続的デプロイメント（CI/CD）パイプラインの実装と維持の標準を確立します。適切に設計されたCI/CDパイプラインにより、迅速で信頼性が高く、再現可能なソフトウェアデリバリーが可能になります。

### 適用範囲

- **継続的インテグレーション**: 自動ビルド、テスト、検証
- **継続的デプロイメント**: 環境への自動デプロイ
- **Infrastructure as Code**: バージョン管理されたインフラストラクチャ
- **パイプライン構成**: 標準化されたパイプライン定義
- **デプロイ自動化**: ゼロダウンタイムデプロイメント

### 基本原則

1. **自動化優先**: すべての反復可能なタスクを自動化
2. **迅速なフィードバック**: 高速なパイプライン実行（目標: 10分未満）
3. **品質ゲート**: 各ステージで自動品質チェック
4. **Infrastructure as Code**: バージョン管理されたインフラストラクチャ
5. **セキュリティバイデザイン**: 全体にわたるセキュリティチェックの統合
6. **可観測性**: 包括的なログと監視

---

## パイプラインアーキテクチャ

### 標準パイプラインステージ

```
┌─────────────┐
│   Commit    │ 開発者がコードをプッシュ
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Build     │ コンパイル、リント、セキュリティスキャン
└──────┬──────┘
       │
       ▼
┌─────────────┐
│    Test     │ ユニット、統合、セキュリティテスト
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Package   │ コンテナビルド、アーティファクト作成
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Deploy    │ ターゲット環境へのデプロイ
│  (Dev/Stg)  │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Verify    │ スモークテスト、ヘルスチェック
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Production │ 手動承認 → デプロイ
└─────────────┘
```

### パイプライン実行時間目標

| ステージ | 目標時間 | 最大時間 |
|-------|-------------|--------------|
| **ビルド** | <2分 | 5分 |
| **ユニットテスト** | <3分 | 5分 |
| **統合テスト** | <5分 | 10分 |
| **セキュリティスキャン** | <2分 | 5分 |
| **パッケージ** | <3分 | 5分 |
| **デプロイ（開発環境）** | <2分 | 5分 |
| **デプロイ（本番環境）** | <5分 | 10分 |
| **合計（開発環境まで）** | <10分 | 20分 |

### マルチブランチ戦略

```yaml
# ブランチ固有のパイプライン動作
branches:
  main:
    - フルパイプライン
    - ステージングへ自動デプロイ
    - 本番環境は手動承認
    
  develop:
    - フルパイプライン
    - 開発環境へ自動デプロイ
    
  feature/*:
    - ビルド + テストのみ
    - デプロイなし
    
  hotfix/*:
    - フルパイプライン
    - 迅速承認プロセス
    
  release/*:
    - フルパイプライン
    - ステージングへ自動デプロイ
    - リリース候補作成
```

---

## 継続的インテグレーション（CI）

### CIワークフロー

#### 1. コード品質ステージ

```yaml
# 例: GitHub Actions CIパイプライン
name: CI Pipeline
on:
  push:
    branches: [main, develop, 'feature/**']
  pull_request:
    branches: [main, develop]

jobs:
  code-quality:
    name: Code Quality Checks
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v3
        with:
          fetch-depth: 0  # 詳細な分析のための完全な履歴
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Lint check
        run: npm run lint
      
      - name: Code formatting check
        run: npm run format:check
      
      - name: Type checking
        run: npm run type-check
      
      - name: Detect code smells
        run: npm run analyze
```

#### 2. ビルドステージ

```yaml
  build:
    name: Build Application
    runs-on: ubuntu-latest
    needs: code-quality
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Build application
        run: npm run build
        env:
          NODE_ENV: production
      
      - name: Upload build artifacts
        uses: actions/upload-artifact@v3
        with:
          name: build-artifacts
          path: dist/
          retention-days: 7
```

#### 3. テストステージ

```yaml
  unit-tests:
    name: Unit Tests
    runs-on: ubuntu-latest
    needs: build
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Run unit tests
        run: npm test -- --coverage --maxWorkers=2
      
      - name: Upload coverage reports
        uses: codecov/codecov-action@v3
        with:
          files: ./coverage/lcov.info
          flags: unittests
          fail_ci_if_error: true
      
      - name: Check coverage threshold
        run: |
          COVERAGE=$(cat coverage/coverage-summary.json | jq '.total.lines.pct')
          if (( $(echo "$COVERAGE < 80" | bc -l) )); then
            echo "Coverage $COVERAGE% is below 80% threshold"
            exit 1
          fi

  integration-tests:
    name: Integration Tests
    runs-on: ubuntu-latest
    needs: build
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_DB: testdb
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
        ports:
          - 5432:5432
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
      
      redis:
        image: redis:7-alpine
        ports:
          - 6379:6379
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Run integration tests
        run: npm run test:integration
        env:
          DATABASE_URL: postgresql://test:test@localhost:5432/testdb
          REDIS_URL: redis://localhost:6379
```

#### 4. セキュリティスキャンステージ

```yaml
  security-scan:
    name: Security Scans
    runs-on: ubuntu-latest
    needs: build
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Secret scanning
        uses: trufflesecurity/trufflehog@main
        with:
          path: ./
          base: ${{ github.event.repository.default_branch }}
          head: HEAD
      
      - name: Dependency vulnerability scan
        run: npm audit --audit-level=moderate
      
      - name: SAST scan
        uses: returntocorp/semgrep-action@v1
        with:
          config: >-
            p/security-audit
            p/owasp-top-ten
      
      - name: Container image scan
        if: github.ref == 'refs/heads/main'
        uses: aquasecurity/trivy-action@master
        with:
          image-ref: 'myapp:${{ github.sha }}'
          format: 'sarif'
          severity: 'CRITICAL,HIGH'
          output: 'trivy-results.sarif'
      
      - name: Upload security results
        uses: github/codeql-action/upload-sarif@v2
        if: always()
        with:
          sarif_file: 'trivy-results.sarif'
```

### CI品質ゲート

**パイプラインはすべてのゲートを通過する必要があります**:

✅ **コード品質ゲート**:
- リンティング: 0エラー
- フォーマット: すべてのファイルがフォーマット済み
- 型チェック: 0型エラー

✅ **ビルドゲート**:
- ビルドが成功
- コンパイルエラーなし
- アーティファクト生成済み

✅ **テストゲート**:
- すべてのテストが合格
- カバレッジ ≥ 80%
- 不安定なテストなし

✅ **セキュリティゲート**:
- Critical/High脆弱性なし
- コード内にシークレットなし
- 依存関係が最新

### キャッシング戦略

**インテリジェントなキャッシングでCIを高速化**:

```yaml
# npm依存関係のキャッシング
- name: Cache node modules
  uses: actions/cache@v3
  with:
    path: ~/.npm
    key: ${{ runner.os }}-node-${{ hashFiles('**/package-lock.json') }}
    restore-keys: |
      ${{ runner.os }}-node-

# ビルドキャッシュ
- name: Cache build output
  uses: actions/cache@v3
  with:
    path: |
      dist
      .next/cache
    key: ${{ runner.os }}-build-${{ hashFiles('**/*.ts', '**/*.tsx') }}

# Dockerレイヤーキャッシング
- name: Set up Docker Buildx
  uses: docker/setup-buildx-action@v2

- name: Build with cache
  uses: docker/build-push-action@v4
  with:
    context: .
    cache-from: type=gha
    cache-to: type=gha,mode=max
```

---

## 継続的デプロイメント（CD）

### CDワークフロー

#### 1. コンテナビルドステージ

```yaml
  build-container:
    name: Build and Push Container
    runs-on: ubuntu-latest
    needs: [unit-tests, integration-tests, security-scan]
    if: github.ref == 'refs/heads/main' || github.ref == 'refs/heads/develop'
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v2
      
      - name: Log in to Container Registry
        uses: docker/login-action@v2
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}
      
      - name: Extract metadata
        id: meta
        uses: docker/metadata-action@v4
        with:
          images: ghcr.io/${{ github.repository }}
          tags: |
            type=ref,event=branch
            type=sha,prefix={{branch}}-
            type=semver,pattern={{version}}
      
      - name: Build and push
        uses: docker/build-push-action@v4
        with:
          context: .
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          labels: ${{ steps.meta.outputs.labels }}
          cache-from: type=gha
          cache-to: type=gha,mode=max
          build-args: |
            BUILD_DATE=${{ github.event.head_commit.timestamp }}
            VCS_REF=${{ github.sha }}
            VERSION=${{ steps.meta.outputs.version }}
```

#### 2. 開発環境へのデプロイ

```yaml
  deploy-dev:
    name: Deploy to Development
    runs-on: ubuntu-latest
    needs: build-container
    if: github.ref == 'refs/heads/develop'
    environment:
      name: development
      url: https://dev.example.com
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v3
      
      - name: Setup kubectl
        uses: azure/setup-kubectl@v3
      
      - name: Configure kubectl
        run: |
          echo "${{ secrets.KUBE_CONFIG_DEV }}" | base64 -d > kubeconfig
          export KUBECONFIG=./kubeconfig
      
      - name: Deploy to Kubernetes
        run: |
          kubectl set image deployment/myapp \
            myapp=ghcr.io/${{ github.repository }}:develop-${{ github.sha }} \
            -n development
          
          kubectl rollout status deployment/myapp -n development
      
      - name: Run smoke tests
        run: |
          sleep 30  # デプロイ待機
          curl -f https://dev.example.com/health || exit 1
```

#### 3. ステージング環境へのデプロイ

```yaml
  deploy-staging:
    name: Deploy to Staging
    runs-on: ubuntu-latest
    needs: build-container
    if: github.ref == 'refs/heads/main'
    environment:
      name: staging
      url: https://staging.example.com
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v3
      
      - name: Setup kubectl
        uses: azure/setup-kubectl@v3
      
      - name: Configure kubectl
        run: |
          echo "${{ secrets.KUBE_CONFIG_STAGING }}" | base64 -d > kubeconfig
          export KUBECONFIG=./kubeconfig
      
      - name: Deploy to Kubernetes
        run: |
          kubectl set image deployment/myapp \
            myapp=ghcr.io/${{ github.repository }}:main-${{ github.sha }} \
            -n staging
          
          kubectl rollout status deployment/myapp -n staging
      
      - name: Run smoke tests
        run: npm run test:smoke -- --baseUrl=https://staging.example.com
      
      - name: Run E2E tests
        run: npm run test:e2e -- --baseUrl=https://staging.example.com
```

#### 4. 本番環境へのデプロイ

```yaml
  deploy-production:
    name: Deploy to Production
    runs-on: ubuntu-latest
    needs: deploy-staging
    if: github.ref == 'refs/heads/main'
    environment:
      name: production
      url: https://example.com
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v3
      
      - name: Setup kubectl
        uses: azure/setup-kubectl@v3
      
      - name: Configure kubectl
        run: |
          echo "${{ secrets.KUBE_CONFIG_PROD }}" | base64 -d > kubeconfig
          export KUBECONFIG=./kubeconfig
      
      - name: Create backup
        run: |
          kubectl get deployment myapp -n production -o yaml > backup-deployment.yaml
      
      - name: Deploy to Kubernetes (Blue-Green)
        run: |
          # Greenenv環境へデプロイ
          kubectl apply -f k8s/deployment-green.yaml
          kubectl set image deployment/myapp-green \
            myapp=ghcr.io/${{ github.repository }}:main-${{ github.sha }} \
            -n production
          
          kubectl rollout status deployment/myapp-green -n production
      
      - name: Run production smoke tests
        run: |
          # Green環境をテスト
          curl -f https://green.example.com/health || exit 1
          npm run test:smoke -- --baseUrl=https://green.example.com
      
      - name: Switch traffic to green
        run: |
          kubectl patch service myapp -n production -p '{"spec":{"selector":{"version":"green"}}}'
      
      - name: Monitor for 5 minutes
        run: |
          sleep 300
          ERROR_RATE=$(curl -s https://example.com/metrics | grep error_rate | awk '{print $2}')
          if (( $(echo "$ERROR_RATE > 0.01" | bc -l) )); then
            echo "エラー率が高すぎる、ロールバック中"
            exit 1
          fi
      
      - name: Delete blue deployment
        if: success()
        run: kubectl delete deployment myapp-blue -n production
      
      - name: Rollback on failure
        if: failure()
        run: |
          kubectl patch service myapp -n production -p '{"spec":{"selector":{"version":"blue"}}}'
          kubectl delete deployment myapp-green -n production
```

### デプロイ承認プロセス

#### 本番環境への手動承認

```yaml
environment:
  name: production
  url: https://example.com
  # 指定されたレビュアーからの手動承認が必要
  protection_rules:
    - type: required_reviewers
      reviewers:
        - devops-team
        - tech-lead
    - type: wait_timer
      wait_minutes: 5  # クールオフ期間
```

#### 自動承認基準

**以下の場合に自動承認**:
- すべてのテストが合格
- Critical脆弱性なし
- デプロイウィンドウ内（平日10am-4pm）
- 進行中のインシデントなし

**以下の場合に手動承認が必要**:
- 高リスク変更（データベースマイグレーション、設定変更）
- デプロイウィンドウ外
- 最近の本番インシデント
- その日の最初のデプロイ

---

## 環境戦略

### 環境タイプ

#### 1. 開発環境

**目的**: 迅速な開発とテスト

```yaml
development:
  auto_deploy: true
  branch: develop
  replicas: 1
  resources:
    cpu: "250m"
    memory: "512Mi"
  features:
    - debug_mode
    - hot_reload
    - mock_external_services
  data:
    type: synthetic
    refresh: daily
```

#### 2. ステージング環境

**目的**: 最終テスト用の本番レプリカ

```yaml
staging:
  auto_deploy: true  # mainブランチから
  branch: main
  replicas: 2
  resources:
    cpu: "500m"
    memory: "1Gi"
  features:
    - production_parity
    - real_integrations
    - performance_testing
  data:
    type: anonymized_production
    refresh: weekly
```

#### 3. 本番環境

**目的**: 顧客向けのライブ環境

```yaml
production:
  auto_deploy: false  # 承認が必要
  branch: main
  replicas: 3
  autoscaling:
    min: 3
    max: 10
    target_cpu: 70%
  resources:
    cpu: "1000m"
    memory: "2Gi"
  features:
    - high_availability
    - monitoring
    - alerting
    - disaster_recovery
  data:
    type: production
    backup: continuous
```

### 環境設定管理

```yaml
# config/base/config.yaml (共通)
app:
  name: myapp
  port: 3000
  log_level: info

# config/development/config.yaml (オーバーライド)
app:
  log_level: debug
  debug_mode: true
database:
  host: localhost
  name: myapp_dev

# config/production/config.yaml (オーバーライド)
app:
  log_level: warn
database:
  host: prod-db.example.com
  name: myapp_prod
  ssl: true
  pool_size: 20
```

### Infrastructure as Code

#### Terraform例

```hcl
# environments/production/main.tf
module "app" {
  source = "../../modules/app"

  environment     = "production"
  replicas        = 3
  instance_type   = "t3.medium"
  
  database_config = {
    instance_class = "db.t3.medium"
    storage_gb     = 100
    multi_az       = true
    backup_retention = 30
  }
  
  monitoring = {
    enabled       = true
    alert_email   = "devops@example.com"
  }
  
  tags = {
    Environment = "production"
    ManagedBy   = "terraform"
    CostCenter  = "engineering"
  }
}
```

#### Kubernetesマニフェスト

```yaml
# k8s/base/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
spec:
  replicas: 1  # 環境ごとにオーバーライド
  selector:
    matchLabels:
      app: myapp
  template:
    metadata:
      labels:
        app: myapp
    spec:
      containers:
      - name: myapp
        image: myapp:latest  # 環境ごとにオーバーライド
        ports:
        - containerPort: 3000
        env:
        - name: NODE_ENV
          value: production
        resources:
          requests:
            cpu: 100m
            memory: 128Mi
          limits:
            cpu: 500m
            memory: 512Mi

# k8s/overlays/production/kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
bases:
  - ../../base
replicas:
  - name: myapp
    count: 3
images:
  - name: myapp
    newName: ghcr.io/org/myapp
    newTag: v1.2.3
patchesStrategicMerge:
  - resources.yaml
```

---

## デプロイ戦略

### 1. ローリングデプロイメント

**ほとんどのアプリケーションのデフォルト戦略**

```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 1        # 更新中の追加Pod 1つ
    maxUnavailable: 0  # ダウンタイムなし
```

**特徴**:
- ✅ ゼロダウンタイム
- ✅ 段階的ロールアウト
- ⚠️ 複数バージョンが同時実行
- ⚠️ ロールバックが遅い

**使用する場合**:
- 後方互換性のある変更
- ステートレスアプリケーション
- データベースマイグレーションなし

### 2. Blue-Greenデプロイメント

**2つの同一環境、トラフィックを瞬時に切り替え**

```yaml
# Blueデプロイ（現在）
apiVersion: v1
kind: Service
metadata:
  name: myapp
spec:
  selector:
    app: myapp
    version: blue  # 現在の本番

---
# Greenデプロイ（新規）
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp-green
spec:
  replicas: 3
  selector:
    matchLabels:
      app: myapp
      version: green
```

**トラフィック切り替え**:
```bash
# 検証後
kubectl patch service myapp -p '{"spec":{"selector":{"version":"green"}}}'
```

**特徴**:
- ✅ 即座のロールバック
- ✅ 切り替え前の完全なテスト
- ⚠️ 2倍のリソースが必要
- ⚠️ 複雑なデータベースマイグレーション

**使用する場合**:
- 高リスクデプロイ
- 即座のロールバックが必要
- データベース変更が後方互換

### 3. カナリアデプロイメント

**ユーザーのサブセットへの段階的ロールアウト**

```yaml
# 安定版デプロイ（90%）
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp-stable
spec:
  replicas: 9

---
# カナリアデプロイ（10%）
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp-canary
spec:
  replicas: 1
```

**段階的トラフィックシフト**:
```
フェーズ1: 95% 安定版, 5% カナリア   (15分)
フェーズ2: 90% 安定版, 10% カナリア  (30分)
フェーズ3: 75% 安定版, 25% カナリア  (1時間)
フェーズ4: 50% 安定版, 50% カナリア  (2時間)
フェーズ5: 0% 安定版, 100% カナリア  (完了)
```

**自動カナリア分析**:
```yaml
- name: Canary analysis
  run: |
    ERROR_RATE_STABLE=$(get_metric myapp-stable error_rate)
    ERROR_RATE_CANARY=$(get_metric myapp-canary error_rate)
    
    if (( $(echo "$ERROR_RATE_CANARY > $ERROR_RATE_STABLE * 1.5" | bc -l) )); then
      echo "カナリアのエラー率が高すぎる、中止"
      exit 1
    fi
```

**特徴**:
- ✅ 低リスクロールアウト
- ✅ 実ユーザーからのフィードバック
- ⚠️ 複雑なルーティング
- ⚠️ 監視が必要

**使用する場合**:
- 高トラフィックアプリケーション
- 不確実性のある新機能
- 実ユーザーフィードバックが必要

### 4. フィーチャーフラグ

**コードをデプロイし、機能を段階的に有効化**

```typescript
// フィーチャーフラグ実装
import { FeatureFlags } from './feature-flags';

async function handleRequest(req, res) {
  const user = await getUser(req);
  
  if (FeatureFlags.isEnabled('new-checkout', user)) {
    return newCheckoutFlow(req, res);
  } else {
    return oldCheckoutFlow(req, res);
  }
}

// 段階的ロールアウト
FeatureFlags.configure('new-checkout', {
  enabled: true,
  rollout: {
    percentage: 10,  // ユーザーの10%
    userSegments: ['beta-testers'],
  }
});
```

**特徴**:
- ✅ デプロイとリリースの分離
- ✅ 即座のロールバック（フラグ無効化）
- ✅ A/Bテスト機能
- ⚠️ コードの複雑さ
- ⚠️ 技術的負債（古いコードパス）

**使用する場合**:
- A/Bテストが必要
- 段階的な機能ロールアウト
- 即座のキルスイッチが必要

---

## パイプラインセキュリティ

### シークレット管理

#### シークレットをコミットしない

❌ **悪い例**:
```yaml
# config.yaml
database:
  password: "MySecretPassword123"  # 絶対にこれをしない
```

✅ **良い例**:
```yaml
# config.yaml
database:
  password: ${DATABASE_PASSWORD}  # 環境変数から

# GitHub Actions
env:
  DATABASE_PASSWORD: ${{ secrets.DATABASE_PASSWORD }}
```

#### シークレット管理サービスを使用

```yaml
# AWS Secrets Manager
- name: Get secrets
  run: |
    SECRET=$(aws secretsmanager get-secret-value \
      --secret-id prod/myapp/database \
      --query SecretString \
      --output text)
    echo "::add-mask::$SECRET"
    echo "DATABASE_URL=$SECRET" >> $GITHUB_ENV

# HashiCorp Vault
- name: Import secrets
  uses: hashicorp/vault-action@v2
  with:
    url: https://vault.example.com
    token: ${{ secrets.VAULT_TOKEN }}
    secrets: |
      secret/data/prod/database password | DATABASE_PASSWORD
```

### パイプラインのRBAC

```yaml
# GitHub環境保護ルール
environments:
  production:
    protection_rules:
      - type: required_reviewers
        reviewers: [devops-team, tech-leads]
      - type: branch_policy
        allowed_branches: [main]
      - type: wait_timer
        wait_minutes: 5

# 制限されたサービスアカウント権限
service_account:
  permissions:
    - deploy:production:write
    - logs:production:read
    - metrics:production:read
  deny:
    - delete:production:*
    - database:production:admin
```

### アーティファクト署名

```yaml
- name: Sign container image
  uses: sigstore/cosign-installer@main

- name: Sign the published Docker image
  run: |
    cosign sign --key cosign.key \
      ghcr.io/${{ github.repository }}:${{ github.sha }}
  env:
    COSIGN_PASSWORD: ${{ secrets.COSIGN_PASSWORD }}

- name: Verify signature
  run: |
    cosign verify --key cosign.pub \
      ghcr.io/${{ github.repository }}:${{ github.sha }}
```

### 監査ログ

```yaml
- name: Log deployment
  run: |
    curl -X POST https://audit.example.com/log \
      -H "Authorization: Bearer ${{ secrets.AUDIT_TOKEN }}" \
      -d '{
        "event": "deployment",
        "environment": "production",
        "version": "${{ github.sha }}",
        "deployer": "${{ github.actor }}",
        "timestamp": "$(date -u +%Y-%m-%dT%H:%M:%SZ)"
      }'
```

---

## 監視と可観測性

### デプロイ監視

#### ヘルスチェック

```yaml
# Kubernetesヘルスチェック
livenessProbe:
  httpGet:
    path: /health
    port: 3000
  initialDelaySeconds: 30
  periodSeconds: 10
  timeoutSeconds: 5
  failureThreshold: 3

readinessProbe:
  httpGet:
    path: /ready
    port: 3000
  initialDelaySeconds: 10
  periodSeconds: 5
  timeoutSeconds: 3
  failureThreshold: 2
```

```typescript
// ヘルスチェックエンドポイント実装
app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'healthy',
    version: process.env.APP_VERSION,
    timestamp: new Date().toISOString()
  });
});

app.get('/ready', async (req, res) => {
  try {
    await database.ping();
    await redis.ping();
    
    res.status(200).json({
      status: 'ready',
      dependencies: {
        database: 'connected',
        redis: 'connected'
      }
    });
  } catch (error) {
    res.status(503).json({
      status: 'not ready',
      error: error.message
    });
  }
});
```

#### デプロイメトリクス

**追跡すべき主要メトリクス**:
- デプロイ頻度
- 変更のリードタイム
- 平均復旧時間（MTTR）
- 変更失敗率

```yaml
# Prometheusメトリクス
- name: Record deployment
  run: |
    cat <<EOF | curl --data-binary @- http://pushgateway:9091/metrics/job/deployment
    # TYPE deployment_info gauge
    deployment_info{version="${{ github.sha }}",environment="production"} 1
    # TYPE deployment_timestamp gauge
    deployment_timestamp{environment="production"} $(date +%s)
    EOF
```

#### ログ集約

```yaml
# Fluent Bit設定
[OUTPUT]
    Name        es
    Match       *
    Host        elasticsearch.example.com
    Port        9200
    Index       app-logs
    Type        _doc
    Logstash_Format On
    Logstash_Prefix myapp
    
    # デプロイメタデータを含める
    Record      deployment_version ${APP_VERSION}
    Record      environment ${ENVIRONMENT}
```

### アラート

```yaml
# Prometheus AlertManagerルール
groups:
  - name: deployment
    rules:
      - alert: HighErrorRate
        expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.05
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "高エラー率検出"
          description: "過去5分間のエラー率は {{ $value }}%"
      
      - alert: DeploymentFailed
        expr: kube_deployment_status_replicas_available < kube_deployment_spec_replicas
        for: 10m
        labels:
          severity: critical
        annotations:
          summary: "デプロイメントに利用不可能なレプリカあり"
      
      - alert: SlowResponse
        expr: histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m])) > 1
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "95パーセンタイルのレスポンス時間が1秒超過"
```

---

## ロールバック手順

### 自動ロールバックトリガー

```yaml
- name: Monitor deployment
  run: |
    for i in {1..30}; do
      ERROR_RATE=$(curl -s https://example.com/metrics | grep error_rate | awk '{print $2}')
      RESPONSE_TIME=$(curl -s https://example.com/metrics | grep response_time_p95 | awk '{print $2}')
      
      if (( $(echo "$ERROR_RATE > 0.05" | bc -l) )); then
        echo "エラー率 ${ERROR_RATE} が閾値超過"
        exit 1
      fi
      
      if (( $(echo "$RESPONSE_TIME > 2000" | bc -l) )); then
        echo "レスポンス時間 ${RESPONSE_TIME}ms が閾値超過"
        exit 1
      fi
      
      sleep 10
    done

- name: Rollback on failure
  if: failure()
  run: |
    kubectl rollout undo deployment/myapp -n production
    kubectl rollout status deployment/myapp -n production
    
    # チームに通知
    curl -X POST ${{ secrets.SLACK_WEBHOOK }} \
      -d '{"text":"🚨 高エラー率によりデプロイがロールバックされました"}'
```

### 手動ロールバック

```bash
# Kubernetesロールバックコマンド
kubectl rollout history deployment/myapp -n production
kubectl rollout undo deployment/myapp -n production
kubectl rollout undo deployment/myapp -n production --to-revision=3

# ロールバック確認
kubectl rollout status deployment/myapp -n production
kubectl get pods -n production -l app=myapp
```

### データベースマイグレーションロールバック

```typescript
// migrations/20251024_add_user_field.ts
export async function up(db: Database) {
  await db.schema.alterTable('users', (table) => {
    table.string('new_field');
  });
}

export async function down(db: Database) {
  await db.schema.alterTable('users', (table) => {
    table.dropColumn('new_field');
  });
}

// ロールバックコマンド
// npm run migrate:rollback
```

---

## ベストプラクティス

### すべきこと

✅ **パイプラインを高速に保つ**（目標: 10分未満）  
✅ **早期に失敗させる**（クイックチェックを最初に実行）  
✅ **パイプラインを再現可能にする**（同じ入力 → 同じ出力）  
✅ **すべてをバージョン管理**（コード、設定、インフラ）  
✅ **不変アーティファクトを使用**（一度ビルド、どこでもデプロイ）  
✅ **品質ゲートを自動化**（手動チェックなし）  
✅ **デプロイを監視**（メトリクス、ログ、トレース）  
✅ **ロールバック手順をテスト**（定期的に練習）  
✅ **デプロイプロセスを文書化**  
✅ **シークレット管理を使用**（シークレットをコミットしない）  

### すべきでないこと

❌ **金曜日にデプロイしない**（必要な場合を除く）  
❌ **テストをスキップしない**（時間節約のため）  
❌ **レビューされていないコードをデプロイしない**  
❌ **失敗したデプロイを無視しない**（即座に調査）  
❌ **手動デプロイステップを持たない**（すべてを自動化）  
❌ **ロールバック計画なしでデプロイしない**  
❌ **共有認証情報を使用しない**（個別のサービスアカウント）  
❌ **ピークトラフィック中にデプロイしない**（緊急時を除く）  
❌ **監視アラートを無視しない**  
❌ **main/productionに直接コミットしない**  

### パイプライン最適化

#### 並列実行

```yaml
jobs:
  unit-tests:
    runs-on: ubuntu-latest
    # ...
  
  integration-tests:
    runs-on: ubuntu-latest
    # ...
  
  security-scan:
    runs-on: ubuntu-latest
    # ...
  
  # 3つすべてが並列実行
  deploy:
    needs: [unit-tests, integration-tests, security-scan]
    # ...
```

#### マトリックスビルド

```yaml
jobs:
  test:
    strategy:
      matrix:
        node-version: [16, 18, 20]
        os: [ubuntu-latest, windows-latest, macos-latest]
    runs-on: ${{ matrix.os }}
    steps:
      - uses: actions/setup-node@v3
        with:
          node-version: ${{ matrix.node-version }}
      - run: npm test
```

#### 条件付き実行

```yaml
jobs:
  deploy:
    if: github.ref == 'refs/heads/main' && github.event_name == 'push'
    # mainブランチへのプッシュ時のみデプロイ
    
  security-scan:
    if: contains(github.event.head_commit.message, '[security]')
    # コミットメッセージに[security]が含まれる場合のみセキュリティスキャン実行
```

---

## Devin AIガイドライン

### 自動パイプラインメンテナンス

**Devinがすべきこと**:
1. パイプライン実行時間を監視
2. ボトルネックを特定し、最適化を提案
3. パイプライン構成の依存関係を更新
4. 失敗したパイプラインステップを自動修正（可能な場合）

### パイプライン構成生成

**Devinが生成できるもの**:
```yaml
# Devinはプロジェクト構造と要件に基づいて
# 適切なCI/CD設定を生成

name: Auto-generated CI/CD Pipeline
on: [push, pull_request]

jobs:
  # Devinが検出: TypeScriptのNode.jsプロジェクト
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - run: npm ci
      - run: npm run build
      - run: npm test
  
  # Devinが検出: Dockerfileが存在
  docker:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - name: Build Docker image
        run: docker build -t myapp .
```

### デプロイ検証

**Devinが検証すべきこと**:
```bash
# デプロイ後チェック
curl -f https://example.com/health || exit 1
curl -f https://example.com/api/version | jq -e '.version == "${{ github.sha }}"'

# パフォーマンス検証
RESPONSE_TIME=$(curl -w "%{time_total}" -o /dev/null -s https://example.com)
if (( $(echo "$RESPONSE_TIME > 2" | bc -l) )); then
  echo "レスポンス時間が遅すぎる: ${RESPONSE_TIME}s"
  exit 1
fi
```

### ロールバック判断

**Devinが自動的にロールバックすべき条件**:
- エラー率 > 5% が5分間継続
- レスポンス時間p95 > 2000ms が5分間継続
- ヘルスチェック失敗 > 3回連続
- デプロイが10分以上停滞

---

## 付録

### 有用なリソース

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [GitLab CI/CD](https://docs.gitlab.com/ee/ci/)
- [AWS CodePipeline](https://aws.amazon.com/codepipeline/)
- [Azure DevOps](https://azure.microsoft.com/ja-jp/products/devops)

### 変更履歴

| バージョン | 日付 | 変更内容 |
|---------|------|---------|
| 1.0.0 | 2025-10-24 | 包括的なCI/CD標準の初版 |

---

**ドキュメント管理者**: DevOpsチーム / Platform Engineering  
**レビュー頻度**: 四半期ごと  
**次回レビュー**: 2025-01-24
