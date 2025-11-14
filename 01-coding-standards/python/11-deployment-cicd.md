## 9. デプロイメント・CI/CD

### 9.1 Dockerコンテナ化

#### 9.1.1 プロダクショングレードDocker実装

**Good: 最適化されたDockerfile**
```dockerfile
# プロダクショングレードDockerfile
# マルチステージビルドで最適化

# ベースイメージ: Python 3.11 slim
FROM python:3.11-slim as base

# メタデータラベル
LABEL maintainer="development-team@company.com" \
      version="1.0.0" \
      description="Production Python application" \
      build-date="2024-01-15"

# システムパッケージ更新と必要パッケージインストール
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    curl \
    libpq-dev \
    libssl-dev \
    libffi-dev \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

# Python最適化設定
ENV PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1 \
    PYTHONHASHSEED=random \
    PIP_NO_CACHE_DIR=1 \
    PIP_DISABLE_PIP_VERSION_CHECK=1

# 非特権ユーザー作成
RUN groupadd -r appuser && useradd -r -g appuser appuser

# アプリケーションディレクトリ作成
WORKDIR /app

# == 依存関係インストールステージ ==
FROM base as dependencies

# pip更新
RUN pip install --upgrade pip setuptools wheel

# 依存関係ファイルコピー
COPY requirements.txt requirements-prod.txt ./

# 依存関係インストール（ビルドキャッシュ最適化）
RUN pip install --no-cache-dir -r requirements-prod.txt

# == アプリケーションステージ ==
FROM base as application

# 依存関係を前ステージからコピー
COPY --from=dependencies /usr/local/lib/python3.11/site-packages /usr/local/lib/python3.11/site-packages
COPY --from=dependencies /usr/local/bin /usr/local/bin

# アプリケーションコードコピー
COPY --chown=appuser:appuser . .

# 設定ファイルとスクリプトの実行権限設定
RUN chmod +x scripts/*.sh && \
    mkdir -p logs tmp && \
    chown -R appuser:appuser logs tmp

# ヘルスチェックスクリプト追加
COPY --chown=appuser:appuser scripts/healthcheck.py /usr/local/bin/
RUN chmod +x /usr/local/bin/healthcheck.py

# ユーザー切り替え
USER appuser

# ポート公開
EXPOSE 8000

# ヘルスチェック設定
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD python /usr/local/bin/healthcheck.py

# アプリケーション起動コマンド
CMD ["gunicorn", "--bind", "0.0.0.0:8000", "--workers", "4", "--worker-class", "uvicorn.workers.UvicornWorker", "app.main:app"]
```

**ヘルスチェックスクリプト**
```python
# scripts/healthcheck.py
#!/usr/bin/env python3
"""
Dockerヘルスチェックスクリプト
"""

import sys
import requests
import json
from datetime import datetime

def main():
    """Health check main function"""
    try:
        # アプリケーションヘルスチェック
        response = requests.get(
            "http://localhost:8000/health",
            timeout=5,
            headers={"User-Agent": "Docker-HealthCheck/1.0"}
        )
        
        if response.status_code == 200:
            health_data = response.json()
            
            # 詳細チェック
            if health_data.get("status") == "healthy":
                print(f"Health check passed: {datetime.utcnow().isoformat()}")
                sys.exit(0)
            else:
                print(f"Health check failed: {health_data.get('message', 'Unknown error')}")
                sys.exit(1)
        else:
            print(f"Health check endpoint returned {response.status_code}")
            sys.exit(1)
            
    except requests.RequestException as e:
        print(f"Health check request failed: {e}")
        sys.exit(1)
    except Exception as e:
        print(f"Health check error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
```

**Docker Composeプロダクション設定**
```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
      target: application
    image: myapp:${VERSION:-latest}
    container_name: myapp-prod
    restart: unless-stopped
    
    environment:
      - ENVIRONMENT=production
      - DATABASE_URL=${DATABASE_URL}
      - REDIS_URL=${REDIS_URL}
      - SECRET_KEY=${SECRET_KEY}
      - LOG_LEVEL=INFO
      - WORKERS=4
    
    ports:
      - "8000:8000"
    
    volumes:
      - ./logs:/app/logs
      - ./config:/app/config:ro
    
    depends_on:
      - db
      - redis
      - monitoring
    
    networks:
      - app-network
    
    deploy:
      resources:
        limits:
          memory: 512M
          cpus: '0.5'
        reservations:
          memory: 256M
          cpus: '0.25'
    
    healthcheck:
      test: ["CMD", "python", "/usr/local/bin/healthcheck.py"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    
    logging:
      driver: "json-file"
      options:
        max-size: "100m"
        max-file: "5"

  db:
    image: postgres:15-alpine
    container_name: myapp-db
    restart: unless-stopped
    
    environment:
      - POSTGRES_DB=${DB_NAME}
      - POSTGRES_USER=${DB_USER}
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./scripts/init-db.sql:/docker-entrypoint-initdb.d/init-db.sql:ro
    
    networks:
      - app-network
    
    deploy:
      resources:
        limits:
          memory: 256M
          cpus: '0.3'
    
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER} -d ${DB_NAME}"]
      interval: 30s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: myapp-redis
    restart: unless-stopped
    
    command: redis-server --appendonly yes --requirepass ${REDIS_PASSWORD}
    
    volumes:
      - redis_data:/data
    
    networks:
      - app-network
    
    deploy:
      resources:
        limits:
          memory: 128M
          cpus: '0.2'
    
    healthcheck:
      test: ["CMD", "redis-cli", "--raw", "incr", "ping"]
      interval: 30s
      timeout: 3s
      retries: 5

  nginx:
    image: nginx:alpine
    container_name: myapp-nginx
    restart: unless-stopped
    
    ports:
      - "80:80"
      - "443:443"
    
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/conf.d:/etc/nginx/conf.d:ro
      - ./ssl:/etc/ssl/certs:ro
      - nginx_logs:/var/log/nginx
    
    depends_on:
      - app
    
    networks:
      - app-network
    
    deploy:
      resources:
        limits:
          memory: 64M
          cpus: '0.1'

  monitoring:
    image: prom/prometheus:latest
    container_name: myapp-monitoring
    restart: unless-stopped
    
    ports:
      - "9090:9090"
    
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml:ro
      - prometheus_data:/prometheus
    
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
      - '--storage.tsdb.retention.time=15d'
      - '--web.enable-lifecycle'
    
    networks:
      - app-network

volumes:
  postgres_data:
    driver: local
  redis_data:
    driver: local
  prometheus_data:
    driver: local
  nginx_logs:
    driver: local

networks:
  app-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.20.0.0/16
```

**Bad: 非最適化Dockerfile**
```dockerfile
# 非最適化Dockerfile例
FROM python:3.11  # 問題: 容量が大きいフルイメージ

# 問題: rootユーザーで実行

# 問題: レイヤーキャッシュの無駄
COPY . /app  # ソースコード全体を先にコピー
WORKDIR /app

# 問題: 毎回依存関係インストール
RUN pip install -r requirements.txt

# 問題: ヘルスチェックなし
# 問題: メタデータなし
# 問題: セキュリティ考慮なし

CMD ["python", "app.py"]  # 問題: 本番環境に不適切
```

### 9.2 CI/CDパイプライン

#### 9.2.1 GitHub Actionsプロダクションパイプライン

**Good: 包括的CI/CDパイプライン**
```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]
  release:
    types: [ published ]

env:
  PYTHON_VERSION: '3.11'
  NODE_VERSION: '18'
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}

jobs:
  # == コード品質チェック ==
  quality-checks:
    name: Quality Checks
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
          POSTGRES_DB: test_db
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
      
      redis:
        image: redis:7
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 6379:6379
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
      with:
        fetch-depth: 0  # 完全な履歴取得
    
    - name: Set up Python
      uses: actions/setup-python@v4
      with:
        python-version: ${{ env.PYTHON_VERSION }}
        cache: 'pip'
    
    - name: Install dependencies
      run: |
        python -m pip install --upgrade pip
        pip install -r requirements-dev.txt
        pip install -r requirements.txt
    
    - name: Code formatting check (Black)
      run: |
        black --check --diff .
    
    - name: Import sorting check (isort)
      run: |
        isort --check-only --diff .
    
    - name: Linting (flake8)
      run: |
        flake8 . --count --select=E9,F63,F7,F82 --show-source --statistics
        flake8 . --count --max-complexity=10 --max-line-length=88 --statistics
    
    - name: Type checking (mypy)
      run: |
        mypy . --config-file pyproject.toml
    
    - name: Security scan (bandit)
      run: |
        bandit -r . -f json -o bandit-report.json
        bandit -r . --severity-level medium
    
    - name: Dependency vulnerability scan (safety)
      run: |
        safety check --json --output safety-report.json
        safety check
    
    - name: Code complexity analysis (radon)
      run: |
        radon cc . --average --show-complexity
        radon mi . --show
    
    - name: Upload security reports
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: security-reports
        path: |
          bandit-report.json
          safety-report.json

  # == テスト実行 ==
  tests:
    name: Tests
    runs-on: ubuntu-latest
    needs: quality-checks
    
    strategy:
      matrix:
        python-version: ['3.10', '3.11', '3.12']
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
          POSTGRES_DB: test_db
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
      
      redis:
        image: redis:7
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 6379:6379
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Set up Python ${{ matrix.python-version }}
      uses: actions/setup-python@v4
      with:
        python-version: ${{ matrix.python-version }}
        cache: 'pip'
    
    - name: Install dependencies
      run: |
        python -m pip install --upgrade pip
        pip install -r requirements-dev.txt
        pip install -r requirements.txt
    
    - name: Run unit tests
      env:
        DATABASE_URL: postgresql://postgres:postgres@localhost:5432/test_db
        REDIS_URL: redis://localhost:6379/0
        SECRET_KEY: test-secret-key
        ENVIRONMENT: test
      run: |
        pytest tests/unit/ -v --cov=app --cov-report=xml --cov-report=html
    
    - name: Run integration tests
      env:
        DATABASE_URL: postgresql://postgres:postgres@localhost:5432/test_db
        REDIS_URL: redis://localhost:6379/0
        SECRET_KEY: test-secret-key
        ENVIRONMENT: test
      run: |
        pytest tests/integration/ -v
    
    - name: Run E2E tests
      env:
        DATABASE_URL: postgresql://postgres:postgres@localhost:5432/test_db
        REDIS_URL: redis://localhost:6379/0
        SECRET_KEY: test-secret-key
        ENVIRONMENT: test
      run: |
        pytest tests/e2e/ -v --maxfail=1
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        file: ./coverage.xml
        flags: unittests
        name: codecov-umbrella
        fail_ci_if_error: false
    
    - name: Upload test results
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-results-${{ matrix.python-version }}
        path: |
          htmlcov/
          coverage.xml
          pytest-report.xml

  # == パフォーマンステスト ==
  performance-tests:
    name: Performance Tests
    runs-on: ubuntu-latest
    needs: [quality-checks, tests]
    if: github.event_name == 'pull_request' || github.ref == 'refs/heads/main'
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Set up Python
      uses: actions/setup-python@v4
      with:
        python-version: ${{ env.PYTHON_VERSION }}
        cache: 'pip'
    
    - name: Install dependencies
      run: |
        python -m pip install --upgrade pip
        pip install -r requirements.txt
        pip install locust pytest-benchmark
    
    - name: Run performance benchmarks
      run: |
        pytest tests/performance/ --benchmark-json=benchmark.json
    
    - name: Run load tests
      run: |
        locust -f tests/load/locustfile.py --headless --users 100 --spawn-rate 10 --run-time 5m --host http://localhost:8000 &
        python app/main.py &
        sleep 10
        wait
    
    - name: Upload performance reports
      uses: actions/upload-artifact@v3
      with:
        name: performance-reports
        path: |
          benchmark.json
          locust-report.html

  # == Dockerイメージビルド ==
  build-image:
    name: Build Docker Image
    runs-on: ubuntu-latest
    needs: [quality-checks, tests]
    
    permissions:
      contents: read
      packages: write
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v3
    
    - name: Log in to Container Registry
      uses: docker/login-action@v3
      with:
        registry: ${{ env.REGISTRY }}
        username: ${{ github.actor }}
        password: ${{ secrets.GITHUB_TOKEN }}
    
    - name: Extract metadata
      id: meta
      uses: docker/metadata-action@v5
      with:
        images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
        tags: |
          type=ref,event=branch
          type=ref,event=pr
          type=sha,prefix={{branch}}-
          type=raw,value=latest,enable={{is_default_branch}}
    
    - name: Build and push Docker image
      uses: docker/build-push-action@v5
      with:
        context: .
        platforms: linux/amd64,linux/arm64
        push: true
        tags: ${{ steps.meta.outputs.tags }}
        labels: ${{ steps.meta.outputs.labels }}
        cache-from: type=gha
        cache-to: type=gha,mode=max
        build-args: |
          BUILD_DATE=${{ fromJSON(steps.meta.outputs.json).labels['org.opencontainers.image.created'] }}
          VERSION=${{ fromJSON(steps.meta.outputs.json).labels['org.opencontainers.image.version'] }}
    
    - name: Run container security scan
      uses: aquasec/trivy-action@master
      with:
        image-ref: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:${{ fromJSON(steps.meta.outputs.json).labels['org.opencontainers.image.version'] }}
        format: 'sarif'
        output: 'trivy-results.sarif'
    
    - name: Upload Trivy scan results
      uses: github/codeql-action/upload-sarif@v2
      if: always()
      with:
        sarif_file: 'trivy-results.sarif'

  # == ステージングデプロイ ==
  deploy-staging:
    name: Deploy to Staging
    runs-on: ubuntu-latest
    needs: [build-image]
    if: github.ref == 'refs/heads/develop'
    
    environment:
      name: staging
      url: https://staging.myapp.com
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Deploy to staging
      run: |
        echo "Deploying to staging environment..."
        # KubernetesデプロイまたはDocker Composeデプロイ
        # kubectl apply -f k8s/staging/
    
    - name: Run smoke tests
      run: |
        sleep 30  # デプロイ完了待機
        pytest tests/smoke/ --base-url=https://staging.myapp.com
    
    - name: Notify deployment
      if: always()
      uses: 8398a7/action-slack@v3
      with:
        status: ${{ job.status }}
        channel: '#deployments'
        webhook_url: ${{ secrets.SLACK_WEBHOOK }}

  # == プロダクションデプロイ ==
  deploy-production:
    name: Deploy to Production
    runs-on: ubuntu-latest
    needs: [build-image, deploy-staging]
    if: github.ref == 'refs/heads/main' && github.event_name == 'push'
    
    environment:
      name: production
      url: https://myapp.com
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Create deployment
      id: deployment
      uses: actions/github-script@v6
      with:
        script: |
          const deployment = await github.rest.repos.createDeployment({
            owner: context.repo.owner,
            repo: context.repo.repo,
            ref: context.sha,
            environment: 'production',
            description: 'Production deployment',
            auto_merge: false
          });
          return deployment.data.id;
    
    - name: Deploy to production
      run: |
        echo "Deploying to production environment..."
        # Blue-GreenデプロイまたはCanaryデプロイ
        # kubectl apply -f k8s/production/
    
    - name: Run production health checks
      run: |
        sleep 60  # デプロイ完了待機
        pytest tests/health/ --base-url=https://myapp.com
    
    - name: Update deployment status
      if: always()
      uses: actions/github-script@v6
      with:
        script: |
          await github.rest.repos.createDeploymentStatus({
            owner: context.repo.owner,
            repo: context.repo.repo,
            deployment_id: ${{ steps.deployment.outputs.result }},
            state: '${{ job.status }}' === 'success' ? 'success' : 'failure',
            environment_url: 'https://myapp.com',
            description: 'Production deployment ${{ job.status }}'
          });
    
    - name: Create release
      if: success()
      uses: actions/create-release@v1
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
      with:
        tag_name: v${{ github.run_number }}
        release_name: Release v${{ github.run_number }}
        body: |
          ✨ Production deployment successful!
          
          **Changes included:**
          ${{ github.event.head_commit.message }}
          
          **Docker Image:**
          `${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:${{ github.sha }}`
        draft: false
        prerelease: false
    
    - name: Notify deployment
      if: always()
      uses: 8398a7/action-slack@v3
      with:
        status: ${{ job.status }}
        channel: '#deployments'
        webhook_url: ${{ secrets.SLACK_WEBHOOK }}
        fields: repo,message,commit,author,action,eventName,ref,workflow
```

#### 9.2.2 プレデプロイメントフック設定

**pre-commit設定**
```yaml
# .pre-commit-config.yaml
repos:
  # Pythonコードフォーマット
  - repo: https://github.com/psf/black
    rev: 23.9.1
    hooks:
      - id: black
        language_version: python3.11
        args: [--line-length=88]
  
  # importソート
  - repo: https://github.com/pycqa/isort
    rev: 5.12.0
    hooks:
      - id: isort
        args: [--profile=black, --line-length=88]
  
  # リンター
  - repo: https://github.com/pycqa/flake8
    rev: 6.1.0
    hooks:
      - id: flake8
        args: [--max-line-length=88, --extend-ignore=E203,W503]
        additional_dependencies:
          - flake8-docstrings
          - flake8-bugbear
          - flake8-comprehensions
  
  # 型チェック
  - repo: https://github.com/pre-commit/mirrors-mypy
    rev: v1.6.1
    hooks:
      - id: mypy
        additional_dependencies: [types-all]
        args: [--config-file=pyproject.toml]
  
  # セキュリティスキャン
  - repo: https://github.com/pycqa/bandit
    rev: 1.7.5
    hooks:
      - id: bandit
        args: [-r, --severity-level=medium]
        exclude: ^tests/
  
  # 一般的なチェック
  - repo: https://github.com/pre-commit/pre-commit-hooks
    rev: v4.4.0
    hooks:
      - id: trailing-whitespace
      - id: end-of-file-fixer
      - id: check-yaml
      - id: check-json
      - id: check-toml
      - id: check-merge-conflict
      - id: check-added-large-files
        args: [--maxkb=1000]
      - id: debug-statements
      - id: check-docstring-first
  
  # Dockerfileリント
  - repo: https://github.com/hadolint/hadolint
    rev: v2.12.0
    hooks:
      - id: hadolint-docker
        args: [--ignore, DL3008, --ignore, DL3009]
  
  # YAMLフォーマット
  - repo: https://github.com/pre-commit/mirrors-prettier
    rev: v3.0.3
    hooks:
      - id: prettier
        types_or: [yaml, markdown]
        exclude: ^templates/
  
  # コミットメッセージチェック
  - repo: https://github.com/commitizen-tools/commitizen
    rev: v3.10.0
    hooks:
      - id: commitizen
        stages: [commit-msg]
```

### 9.3 インフラストラクチャ・クラウド

#### 9.3.1 Kubernetesデプロイメント

**Good: プロダクショングレードKubernetes設定**
```yaml
# k8s/production/namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: myapp-prod
  labels:
    name: myapp-prod
    environment: production
---
# k8s/production/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: myapp-config
  namespace: myapp-prod
data:
  ENVIRONMENT: "production"
  LOG_LEVEL: "INFO"
  WORKERS: "4"
  DATABASE_POOL_SIZE: "20"
  REDIS_POOL_SIZE: "10"
---
# k8s/production/secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: myapp-secrets
  namespace: myapp-prod
type: Opaque
data:
  DATABASE_URL: <base64-encoded-database-url>
  REDIS_URL: <base64-encoded-redis-url>
  SECRET_KEY: <base64-encoded-secret-key>
  JWT_SECRET: <base64-encoded-jwt-secret>
---
# k8s/production/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp-deployment
  namespace: myapp-prod
  labels:
    app: myapp
    version: v1.0.0
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: myapp
  template:
    metadata:
      labels:
        app: myapp
        version: v1.0.0
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8000"
        prometheus.io/path: "/metrics"
    spec:
      serviceAccountName: myapp-service-account
      securityContext:
        runAsNonRoot: true
        runAsUser: 1000
        fsGroup: 1000
      containers:
      - name: myapp
        image: ghcr.io/company/myapp:v1.0.0
        imagePullPolicy: Always
        
        ports:
        - containerPort: 8000
          name: http
          protocol: TCP
        
        env:
        - name: PORT
          value: "8000"
        
        envFrom:
        - configMapRef:
            name: myapp-config
        - secretRef:
            name: myapp-secrets
        
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        
        livenessProbe:
          httpGet:
            path: /health
            port: http
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        
        readinessProbe:
          httpGet:
            path: /health
            port: http
          initialDelaySeconds: 5
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 3
        
        startupProbe:
          httpGet:
            path: /health
            port: http
          initialDelaySeconds: 10
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 30
        
        securityContext:
          runAsNonRoot: true
          runAsUser: 1000
          allowPrivilegeEscalation: false
          readOnlyRootFilesystem: true
          capabilities:
            drop:
            - ALL
        
        volumeMounts:
        - name: tmp-volume
          mountPath: /tmp
        - name: logs-volume
          mountPath: /app/logs
      
      volumes:
      - name: tmp-volume
        emptyDir: {}
      - name: logs-volume
        emptyDir: {}
      
      affinity:
        podAntiAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
          - weight: 100
            podAffinityTerm:
              labelSelector:
                matchExpressions:
                - key: app
                  operator: In
                  values:
                  - myapp
              topologyKey: kubernetes.io/hostname
---
# k8s/production/service.yaml
apiVersion: v1
kind: Service
metadata:
  name: myapp-service
  namespace: myapp-prod
  labels:
    app: myapp
spec:
  selector:
    app: myapp
  ports:
  - name: http
    port: 80
    targetPort: http
    protocol: TCP
  type: ClusterIP
---
# k8s/production/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: myapp-ingress
  namespace: myapp-prod
  annotations:
    kubernetes.io/ingress.class: nginx
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/force-ssl-redirect: "true"
    cert-manager.io/cluster-issuer: letsencrypt-prod
    nginx.ingress.kubernetes.io/rate-limit: "100"
    nginx.ingress.kubernetes.io/rate-limit-window: "1m"
spec:
  tls:
  - hosts:
    - myapp.com
    - api.myapp.com
    secretName: myapp-tls
  rules:
  - host: myapp.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: myapp-service
            port:
              number: 80
  - host: api.myapp.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: myapp-service
            port:
              number: 80
---
# k8s/production/hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: myapp-hpa
  namespace: myapp-prod
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: myapp-deployment
  minReplicas: 3
  maxReplicas: 20
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
  behavior:
    scaleUp:
      stabilizationWindowSeconds: 60
      policies:
      - type: Percent
        value: 100
        periodSeconds: 15
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Percent
        value: 10
        periodSeconds: 60
---
# k8s/production/pdb.yaml
apiVersion: policy/v1
kind: PodDisruptionBudget
metadata:
  name: myapp-pdb
  namespace: myapp-prod
spec:
  minAvailable: 2
  selector:
    matchLabels:
      app: myapp
---
# k8s/production/networkpolicy.yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: myapp-network-policy
  namespace: myapp-prod
spec:
  podSelector:
    matchLabels:
      app: myapp
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          name: nginx-ingress
    ports:
    - protocol: TCP
      port: 8000
  egress:
  - to:
    - namespaceSelector:
        matchLabels:
          name: database
    ports:
    - protocol: TCP
      port: 5432
  - to:
    - namespaceSelector:
        matchLabels:
          name: redis
    ports:
    - protocol: TCP
      port: 6379
  - to: []  # Allow all egress for external APIs
    ports:
    - protocol: TCP
      port: 443
    - protocol: TCP
      port: 80
```

### 9.4 環境管理・構成

#### 9.4.1 環境別設定管理

**Good: 包括的環境設定管理**
```python
# config/settings.py
"""
環境別設定管理システム
"""

import os
import sys
from typing import Dict, Any, Optional, List
from pathlib import Path
from dataclasses import dataclass, field
from enum import Enum
import json
from pydantic import BaseSettings, Field, validator
from pydantic.env_settings import SettingsSourceCallable

class Environment(str, Enum):
    """環境種別"""
    DEVELOPMENT = "development"
    TEST = "test"
    STAGING = "staging"
    PRODUCTION = "production"

class LogLevel(str, Enum):
    """ログレベル"""
    DEBUG = "DEBUG"
    INFO = "INFO"
    WARNING = "WARNING"
    ERROR = "ERROR"
    CRITICAL = "CRITICAL"

@dataclass
class DatabaseConfig:
    """データベース設定"""
    url: str
    pool_size: int = 20
    max_overflow: int = 30
    pool_timeout: int = 30
    pool_recycle: int = 3600
    echo: bool = False
    echo_pool: bool = False
    
    @classmethod
    def from_url(cls, url: str, **kwargs) -> 'DatabaseConfig':
        return cls(url=url, **kwargs)

@dataclass 
class RedisConfig:
    """レRedis設定"""
    url: str
    max_connections: int = 100
    socket_timeout: int = 5
    socket_connect_timeout: int = 5
    retry_on_timeout: bool = True
    health_check_interval: int = 30

@dataclass
class SecurityConfig:
    """セキュリティ設定"""
    secret_key: str
    jwt_secret: str
    jwt_algorithm: str = "HS256"
    jwt_access_token_expire_minutes: int = 30
    jwt_refresh_token_expire_days: int = 7
    password_min_length: int = 8
    bcrypt_rounds: int = 12
    allowed_hosts: List[str] = field(default_factory=list)
    cors_origins: List[str] = field(default_factory=list)

class Settings(BaseSettings):
    """アプリケーション設定"""
    
    # == 基本設定 ==
    app_name: str = "MyApp"
    app_version: str = "1.0.0"
    environment: Environment = Environment.DEVELOPMENT
    debug: bool = False
    
    # == サーバー設定 ==
    host: str = "0.0.0.0"
    port: int = 8000
    workers: int = Field(default=1, ge=1, le=32)
    worker_class: str = "uvicorn.workers.UvicornWorker"
    worker_timeout: int = 30
    keepalive: int = 2
    max_requests: int = 1000
    max_requests_jitter: int = 100
    
    # == ログ設定 ==
    log_level: LogLevel = LogLevel.INFO
    log_format: str = "json"
    log_dir: str = "logs"
    log_file_max_size: str = "100MB"
    log_file_backup_count: int = 5
    
    # == データベース設定 ==
    database_url: str = Field(..., env="DATABASE_URL")
    database_pool_size: int = Field(default=20, ge=1, le=100)
    database_max_overflow: int = Field(default=30, ge=0, le=50)
    database_pool_timeout: int = Field(default=30, ge=1, le=300)
    database_echo: bool = False
    
    # == Redis設定 ==
    redis_url: str = Field(..., env="REDIS_URL")
    redis_max_connections: int = Field(default=100, ge=1, le=1000)
    redis_socket_timeout: int = Field(default=5, ge=1, le=60)
    
    # == セキュリティ設定 ==
    secret_key: str = Field(..., env="SECRET_KEY", min_length=32)
    jwt_secret: str = Field(..., env="JWT_SECRET", min_length=32)
    jwt_algorithm: str = "HS256"
    jwt_access_token_expire_minutes: int = Field(default=30, ge=1, le=10080)
    jwt_refresh_token_expire_days: int = Field(default=7, ge=1, le=30)
    
    # == CORS設定 ==
    allowed_hosts: List[str] = Field(default_factory=list)
    cors_origins: List[str] = Field(default_factory=list)
    cors_methods: List[str] = Field(default=["GET", "POST", "PUT", "DELETE", "OPTIONS"])
    cors_headers: List[str] = Field(default=["*"])
    
    # == 外部サービス設定 ==
    email_smtp_host: Optional[str] = None
    email_smtp_port: int = 587
    email_smtp_username: Optional[str] = None
    email_smtp_password: Optional[str] = None
    email_from_address: Optional[str] = None
    
    # == 監視・メトリクス ==
    metrics_enabled: bool = True
    metrics_endpoint: str = "/metrics"
    health_check_endpoint: str = "/health"
    sentry_dsn: Optional[str] = None
    
    # == パフォーマンス設定 ==
    request_timeout: int = Field(default=30, ge=1, le=300)
    slow_query_threshold: float = Field(default=1.0, ge=0.1, le=10.0)
    cache_ttl: int = Field(default=300, ge=1, le=86400)
    
    # == ファイルアップロード設定 ==
    upload_max_size: int = Field(default=10*1024*1024, ge=1024)  # 10MB
    upload_allowed_extensions: List[str] = Field(
        default=[".jpg", ".jpeg", ".png", ".gif", ".pdf", ".txt", ".csv"]
    )
    upload_directory: str = "uploads"
    
    @validator("environment", pre=True)
    def validate_environment(cls, v):
        if isinstance(v, str):
            return Environment(v.lower())
        return v
    
    @validator("workers")
    def validate_workers(cls, v, values):
        if values.get("environment") == Environment.PRODUCTION:
            # プロダクションでは最低2ワーカー
            return max(v, 2)
        return v
    
    @validator("debug")
    def validate_debug(cls, v, values):
        env = values.get("environment")
        if env == Environment.PRODUCTION:
            # プロダクションでは強制的にFalse
            return False
        return v
    
    @validator("log_level")
    def validate_log_level(cls, v, values):
        env = values.get("environment")
        if env == Environment.PRODUCTION and v == LogLevel.DEBUG:
            # プロダクションではDEBUG禁止
            return LogLevel.INFO
        return v
    
    @validator("cors_origins")
    def validate_cors_origins(cls, v, values):
        env = values.get("environment")
        if env == Environment.PRODUCTION and "*" in v:
            raise ValueError("CORS wildcard (*) not allowed in production")
        return v
    
    @property
    def database_config(self) -> DatabaseConfig:
        """データベース設定取得"""
        return DatabaseConfig(
            url=self.database_url,
            pool_size=self.database_pool_size,
            max_overflow=self.database_max_overflow,
            pool_timeout=self.database_pool_timeout,
            echo=self.database_echo and self.environment != Environment.PRODUCTION
        )
    
    @property
    def redis_config(self) -> RedisConfig:
        """ルRedis設定取得"""
        return RedisConfig(
            url=self.redis_url,
            max_connections=self.redis_max_connections,
            socket_timeout=self.redis_socket_timeout
        )
    
    @property
    def security_config(self) -> SecurityConfig:
        """セキュリティ設定取得"""
        return SecurityConfig(
            secret_key=self.secret_key,
            jwt_secret=self.jwt_secret,
            jwt_algorithm=self.jwt_algorithm,
            jwt_access_token_expire_minutes=self.jwt_access_token_expire_minutes,
            jwt_refresh_token_expire_days=self.jwt_refresh_token_expire_days,
            allowed_hosts=self.allowed_hosts,
            cors_origins=self.cors_origins
        )
    
    @property
    def is_development(self) -> bool:
        return self.environment == Environment.DEVELOPMENT
    
    @property
    def is_production(self) -> bool:
        return self.environment == Environment.PRODUCTION
    
    @property
    def is_testing(self) -> bool:
        return self.environment == Environment.TEST
    
    def get_database_url(self, async_driver: bool = False) -> str:
        """データベースURL取得"""
        url = self.database_url
        if async_driver and url.startswith("postgresql://"):
            return url.replace("postgresql://", "postgresql+asyncpg://", 1)
        return url
    
    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"
        case_sensitive = False
        
        @classmethod
        def customise_sources(
            cls,
            init_settings: SettingsSourceCallable,
            env_settings: SettingsSourceCallable,
            file_secret_settings: SettingsSourceCallable,
        ) -> tuple[SettingsSourceCallable, ...]:
            # 設定優先度: 環境変数 > .envファイル > デフォルト値
            return env_settings, file_secret_settings, init_settings

# 環境別設定ファクトリ
class SettingsFactory:
    """設定ファクトリ"""
    
    _instance: Optional[Settings] = None
    
    @classmethod
    def get_settings(cls, force_reload: bool = False) -> Settings:
        """設定インスタンス取得（シングルトン）"""
        if cls._instance is None or force_reload:
            cls._instance = cls._create_settings()
        return cls._instance
    
    @classmethod
    def _create_settings(cls) -> Settings:
        """設定インスタンス作成"""
        # 環境別設定ファイル読み込み
        env = os.getenv("ENVIRONMENT", "development").lower()
        env_file = f".env.{env}"
        
        # ファイルが存在する場合のみ読み込み
        env_file_path = Path(env_file)
        if env_file_path.exists():
            settings = Settings(_env_file=env_file)
        else:
            settings = Settings()
        
        # 設定検証
        cls._validate_settings(settings)
        
        return settings
    
    @classmethod
    def _validate_settings(cls, settings: Settings):
        """設定検証"""
        # プロダクション環境の追加検証
        if settings.is_production:
            required_prod_settings = [
                "secret_key", "jwt_secret", "database_url", "redis_url"
            ]
            
            for setting in required_prod_settings:
                value = getattr(settings, setting, None)
                if not value or (isinstance(value, str) and len(value) < 10):
                    raise ValueError(f"Production environment requires valid {setting}")
            
            # プロダクションではデバッグ無効
            if settings.debug:
                raise ValueError("Debug mode not allowed in production")
            
            # HTTPS強制
            if not any(host.startswith("https://") for host in settings.allowed_hosts):
                print("Warning: No HTTPS hosts configured for production")

# グローバル設定インスタンス
settings = SettingsFactory.get_settings()

# デバッグ用設定ダンプ
if settings.is_development:
    def dump_settings():
        """設定ダンプ（機密情報除く）"""
        safe_settings = {}
        for field_name, field_info in settings.__fields__.items():
            value = getattr(settings, field_name)
            
            # 機密情報マスク
            if any(secret in field_name.lower() for secret in ['secret', 'password', 'key', 'token']):
                safe_settings[field_name] = "***MASKED***"
            elif isinstance(value, str) and len(value) > 100:
                safe_settings[field_name] = value[:50] + "..."
            else:
                safe_settings[field_name] = value
        
        return safe_settings
    
    # 開発環境での設定表示
    import json
    print("=== Application Settings ===")
    print(json.dumps(dump_settings(), indent=2, default=str))
    print("=============================")
```

---

