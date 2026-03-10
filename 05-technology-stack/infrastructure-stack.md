# インフラストラクチャ技術スタック / Infrastructure Technology Stack

```yaml
version: "2.0.0"
last_updated: "2025-01-15"
status: "active"
owner: "Infrastructure & DevOps Team"
category: "technology-stack"
```

## 目次 / Table of Contents

1. [概要 / Overview](#概要--overview)
2. [クラウドプラットフォーム / Cloud Platforms](#クラウドプラットフォーム--cloud-platforms)
3. [コンテナ技術 / Container Technologies](#コンテナ技術--container-technologies)
4. [オーケストレーション / Orchestration](#オーケストレーション--orchestration)
5. [CI/CDツール / CI/CD Tools](#cicdツール--cicd-tools)
6. [Infrastructure as Code](#infrastructure-as-code)
7. [モニタリングと可観測性 / Monitoring and Observability](#モニタリングと可観測性--monitoring-and-observability)
8. [ロギング / Logging](#ロギング--logging)
9. [セキュリティツール / Security Tools](#セキュリティツール--security-tools)
10. [ネットワーキング / Networking](#ネットワーキング--networking)
11. [バックアップとディザスタリカバリ / Backup and Disaster Recovery](#バックアップとディザスタリカバリ--backup-and-disaster-recovery)
12. [標準設定 / Standard Configuration](#標準設定--standard-configuration)
13. [ベストプラクティス / Best Practices](#ベストプラクティス--best-practices)
14. [コスト最適化 / Cost Optimization](#コスト最適化--cost-optimization)
15. [参考資料 / References](#参考資料--references)

---

## 概要 / Overview

このドキュメントは、組織全体で使用するインフラストラクチャ技術スタックの標準を定義します。

### 目的 / Purpose

- スケーラブルで信頼性の高いインフラストラクチャの構築
- 自動化とDevOpsプラクティスの推進
- セキュリティとコンプライアンスの確保
- コスト効率の最適化

### 適用範囲 / Scope

- すべてのクラウドインフラストラクチャ
- コンテナ化されたアプリケーション
- CI/CDパイプライン
- モニタリングとロギングシステム

---

## クラウドプラットフォーム / Cloud Platforms

### Amazon Web Services (AWS) - 標準 / Standard

```yaml
cloud_provider:
  name: "AWS"
  status: "standard"
  strategy: "マルチリージョン"
  
primary_regions:
  production:
    - "ap-northeast-1 (Tokyo)"
    - "us-east-1 (N. Virginia)" # DR
  development:
    - "ap-northeast-1 (Tokyo)"
  
core_services:
  compute:
    - name: "EC2"
      use_cases: ["一般的なワークロード", "レガシーアプリケーション"]
    - name: "ECS Fargate"
      use_cases: ["コンテナワークロード", "マイクロサービス"]
      status: "recommended"
    - name: "Lambda"
      use_cases: ["サーバーレス", "イベント駆動処理"]
    - name: "EKS"
      use_cases: ["Kubernetesワークロード", "複雑なオーケストレーション"]
  
  storage:
    - name: "S3"
      use_cases: ["オブジェクトストレージ", "静的コンテンツ", "バックアップ"]
      storage_classes:
        hot_data: "S3 Standard"
        warm_data: "S3 Intelligent-Tiering"
        cold_data: "S3 Glacier"
    - name: "EBS"
      use_cases: ["ブロックストレージ", "データベースボリューム"]
    - name: "EFS"
      use_cases: ["共有ファイルシステム", "コンテナ永続化"]
  
  database:
    - name: "RDS"
      engines: ["PostgreSQL", "MySQL", "Aurora PostgreSQL"]
      multi_az: true
    - name: "DynamoDB"
      use_cases: ["NoSQL", "高スループット"]
    - name: "ElastiCache"
      engines: ["Redis", "Memcached"]
  
  networking:
    - "VPC"
    - "Route 53"
    - "CloudFront"
    - "Application Load Balancer"
    - "Network Load Balancer"
  
  security:
    - "IAM"
    - "Secrets Manager"
    - "KMS"
    - "Security Hub"
    - "GuardDuty"
    - "WAF"
```

**VPC設計標準**:

```yaml
vpc_architecture:
  cidr_block: "10.0.0.0/16"
  
  subnets:
    public:
      cidr_blocks:
        - "10.0.1.0/24"  # AZ-A
        - "10.0.2.0/24"  # AZ-C
        - "10.0.3.0/24"  # AZ-D
      resources: ["ALB", "NAT Gateway", "Bastion"]
    
    private_app:
      cidr_blocks:
        - "10.0.11.0/24" # AZ-A
        - "10.0.12.0/24" # AZ-C
        - "10.0.13.0/24" # AZ-D
      resources: ["ECS Tasks", "EC2", "Lambda"]
    
    private_data:
      cidr_blocks:
        - "10.0.21.0/24" # AZ-A
        - "10.0.22.0/24" # AZ-C
        - "10.0.23.0/24" # AZ-D
      resources: ["RDS", "ElastiCache", "DocumentDB"]
  
  routing:
    public_subnets: "Internet Gateway"
    private_subnets: "NAT Gateway"
  
  high_availability:
    availability_zones: 3
    multi_az_deployment: true
```

**IAM ポリシー標準**:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "LeastPrivilegePolicy",
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:PutObject"
      ],
      "Resource": "arn:aws:s3:::my-bucket/*",
      "Condition": {
        "IpAddress": {
          "aws:SourceIp": ["10.0.0.0/16"]
        }
      }
    }
  ]
}
```

### Google Cloud Platform (GCP) - 承認済み / Approved

```yaml
cloud_provider:
  name: "GCP"
  status: "approved"
  use_cases:
    - "BigQueryデータ分析"
    - "機械学習ワークロード"
    - "特定プロジェクト"
  
core_services:
  compute:
    - "Compute Engine"
    - "Cloud Run"
    - "GKE (Google Kubernetes Engine)"
  
  storage:
    - "Cloud Storage"
    - "Persistent Disk"
  
  database:
    - "Cloud SQL"
    - "Firestore"
    - "Bigtable"
  
  analytics:
    - "BigQuery"
    - "Dataflow"
    - "Pub/Sub"
```

### Microsoft Azure - 評価中 / Under Evaluation

```yaml
cloud_provider:
  name: "Azure"
  status: "under_evaluation"
  evaluation_period: "Q1 2025"
  use_cases:
    - "エンタープライズ顧客向け"
    - "Active Directory統合"
```

---

## コンテナ技術 / Container Technologies

### Docker (必須 / Required)

```yaml
container:
  name: "Docker"
  version: "24.x+"
  status: "required"
  
  image_registry:
    primary: "Amazon ECR"
    alternative: "Docker Hub (public images only)"
  
  best_practices:
    base_images:
      - "alpine:3.19 (最小イメージ)"
      - "node:20-alpine"
      - "python:3.12-slim"
      - "golang:1.22-alpine"
    
    security:
      - "非rootユーザーで実行"
      - "マルチステージビルド"
      - "脆弱性スキャン (Trivy)"
      - "イメージサイズ最小化"
    
    tagging_strategy:
      production: "semantic versioning (v1.2.3)"
      staging: "commit SHA (abc1234)"
      development: "branch name (feature-xxx)"
```

**Dockerfile標準テンプレート**:

```dockerfile
# ビルドステージ
FROM node:20-alpine AS builder

WORKDIR /app

# 依存関係のインストール
COPY package*.json pnpm-lock.yaml ./
RUN corepack enable && \
    pnpm install --frozen-lockfile

# アプリケーションのビルド
COPY . .
RUN pnpm build && \
    pnpm prune --prod

# 実行ステージ
FROM node:20-alpine AS runner

# セキュリティ: 非rootユーザー作成
RUN addgroup -g 1001 -S nodejs && \
    adduser -S nodejs -u 1001

WORKDIR /app

# 必要なファイルのみコピー
COPY --from=builder --chown=nodejs:nodejs /app/dist ./dist
COPY --from=builder --chown=nodejs:nodejs /app/node_modules ./node_modules
COPY --from=builder --chown=nodejs:nodejs /app/package.json ./

# 非rootユーザーに切り替え
USER nodejs

# ヘルスチェック
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD node healthcheck.js

EXPOSE 3000

CMD ["node", "dist/main.js"]
```

**Docker Compose (ローカル開発)**:

```yaml
version: '3.9'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
      target: development
    ports:
      - "3000:3000"
    environment:
      - NODE_ENV=development
      - DATABASE_URL=postgresql://user:password@postgres:5432/mydb
      - REDIS_HOST=redis
    volumes:
      - ./src:/app/src
      - /app/node_modules
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_started
    networks:
      - app-network

  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
      POSTGRES_DB: mydb
    volumes:
      - postgres-data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U user"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - app-network

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    networks:
      - app-network

volumes:
  postgres-data:
  redis-data:

networks:
  app-network:
    driver: bridge
```

---

## オーケストレーション / Orchestration

### Amazon ECS Fargate (標準 / Standard)

```yaml
orchestration:
  name: "ECS Fargate"
  status: "standard"
  use_cases:
    - "マイクロサービス"
    - "コンテナ化されたAPI"
    - "バックグラウンドワーカー"
  
  advantages:
    - "サーバーレスコンテナ"
    - "自動スケーリング"
    - "AWS統合"
    - "管理オーバーヘッド削減"
  
  task_definition:
    cpu: ["256", "512", "1024", "2048", "4096"]
    memory: ["512MB", "1GB", "2GB", "4GB", "8GB", "16GB"]
    network_mode: "awsvpc"
  
  service_configuration:
    deployment_type: "Rolling update"
    min_healthy_percent: 100
    max_percent: 200
    health_check_grace_period: 60
```

**ECS Task Definition例**:

```json
{
  "family": "my-app",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "1024",
  "memory": "2048",
  "executionRoleArn": "arn:aws:iam::123456789012:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::123456789012:role/myAppTaskRole",
  "containerDefinitions": [
    {
      "name": "app",
      "image": "123456789012.dkr.ecr.ap-northeast-1.amazonaws.com/my-app:v1.2.3",
      "portMappings": [
        {
          "containerPort": 3000,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "NODE_ENV",
          "value": "production"
        }
      ],
      "secrets": [
        {
          "name": "DATABASE_URL",
          "valueFrom": "arn:aws:secretsmanager:ap-northeast-1:123456789012:secret:db-url"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/my-app",
          "awslogs-region": "ap-northeast-1",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "healthCheck": {
        "command": ["CMD-SHELL", "curl -f http://localhost:3000/health || exit 1"],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 60
      }
    }
  ]
}
```

**Auto Scaling設定**:

```yaml
auto_scaling:
  target_tracking:
    - metric: "ECSServiceAverageCPUUtilization"
      target_value: 70
      scale_out_cooldown: 60
      scale_in_cooldown: 300
    
    - metric: "ECSServiceAverageMemoryUtilization"
      target_value: 80
      scale_out_cooldown: 60
      scale_in_cooldown: 300
    
    - metric: "ALBRequestCountPerTarget"
      target_value: 1000
      scale_out_cooldown: 60
      scale_in_cooldown: 300
  
  capacity:
    min_capacity: 2
    max_capacity: 10
    desired_capacity: 2
```

### Kubernetes / Amazon EKS (推奨 / Recommended)

```yaml
orchestration:
  name: "Amazon EKS"
  version: "1.28+"
  status: "recommended"
  use_cases:
    - "複雑なマイクロサービスアーキテクチャ"
    - "ハイブリッドクラウド"
    - "高度なオーケストレーション要件"
  
  node_groups:
    managed_node_groups:
      instance_types: ["t3.medium", "t3.large", "m5.large"]
      scaling:
        min_size: 2
        max_size: 10
        desired_size: 3
    
    fargate_profiles:
      namespaces: ["default", "production"]
  
  add_ons:
    - "vpc-cni"
    - "coredns"
    - "kube-proxy"
    - "aws-load-balancer-controller"
    - "cluster-autoscaler"
    - "metrics-server"
```

**Kubernetesマニフェスト標準**:

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  namespace: production
  labels:
    app: my-app
    version: v1.2.3
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
        version: v1.2.3
    spec:
      serviceAccountName: my-app-sa
      securityContext:
        runAsNonRoot: true
        runAsUser: 1000
        fsGroup: 1000
      
      containers:
      - name: app
        image: 123456789012.dkr.ecr.ap-northeast-1.amazonaws.com/my-app:v1.2.3
        imagePullPolicy: IfNotPresent
        
        ports:
        - name: http
          containerPort: 3000
          protocol: TCP
        
        env:
        - name: NODE_ENV
          value: production
        - name: PORT
          value: "3000"
        
        envFrom:
        - secretRef:
            name: my-app-secrets
        - configMapRef:
            name: my-app-config
        
        resources:
          requests:
            cpu: 250m
            memory: 512Mi
          limits:
            cpu: 1000m
            memory: 1Gi
        
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
            path: /ready
            port: http
          initialDelaySeconds: 10
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 3
        
        securityContext:
          allowPrivilegeEscalation: false
          readOnlyRootFilesystem: true
          capabilities:
            drop:
            - ALL

---
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: my-app
  namespace: production
spec:
  type: ClusterIP
  ports:
  - port: 80
    targetPort: http
    protocol: TCP
    name: http
  selector:
    app: my-app

---
# hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: my-app-hpa
  namespace: production
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: my-app
  minReplicas: 2
  maxReplicas: 10
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
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Percent
        value: 50
        periodSeconds: 60
    scaleUp:
      stabilizationWindowSeconds: 0
      policies:
      - type: Percent
        value: 100
        periodSeconds: 30
      - type: Pods
        value: 2
        periodSeconds: 30
      selectPolicy: Max
```

---

## CI/CDツール / CI/CD Tools

### GitHub Actions (標準 / Standard)

```yaml
cicd:
  name: "GitHub Actions"
  status: "standard"
  
  workflows:
    - "Build and Test"
    - "Security Scan"
    - "Deploy to Staging"
    - "Deploy to Production"
  
  self_hosted_runners:
    enabled: true
    labels: ["self-hosted", "linux", "x64"]
```

**GitHub Actions ワークフロー例**:

```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

env:
  AWS_REGION: ap-northeast-1
  ECR_REPOSITORY: my-app
  ECS_SERVICE: my-app-service
  ECS_CLUSTER: production-cluster
  ECS_TASK_DEFINITION: my-app-task-def

jobs:
  test:
    name: Test
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Setup Node.js
      uses: actions/setup-node@v4
      with:
        node-version: '20'
        cache: 'pnpm'
    
    - name: Install pnpm
      run: corepack enable
    
    - name: Install dependencies
      run: pnpm install --frozen-lockfile
    
    - name: Run linter
      run: pnpm lint
    
    - name: Run type check
      run: pnpm type-check
    
    - name: Run tests
      run: pnpm test:coverage
    
    - name: Upload coverage
      uses: codecov/codecov-action@v3
      with:
        files: ./coverage/coverage-final.json
        flags: unittests

  security:
    name: Security Scan
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Run Trivy vulnerability scanner
      uses: aquasecurity/trivy-action@master
      with:
        scan-type: 'fs'
        scan-ref: '.'
        format: 'sarif'
        output: 'trivy-results.sarif'
    
    - name: Upload Trivy results to GitHub Security
      uses: github/codeql-action/upload-sarif@v2
      with:
        sarif_file: 'trivy-results.sarif'

  build:
    name: Build and Push
    needs: [test, security]
    runs-on: ubuntu-latest
    if: github.event_name == 'push'
    
    outputs:
      image_tag: ${{ steps.meta.outputs.tags }}
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Configure AWS credentials
      uses: aws-actions/configure-aws-credentials@v4
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-region: ${{ env.AWS_REGION }}
    
    - name: Login to Amazon ECR
      id: login-ecr
      uses: aws-actions/amazon-ecr-login@v2
    
    - name: Extract metadata
      id: meta
      uses: docker/metadata-action@v5
      with:
        images: ${{ steps.login-ecr.outputs.registry }}/${{ env.ECR_REPOSITORY }}
        tags: |
          type=ref,event=branch
          type=sha,prefix={{branch}}-
          type=semver,pattern={{version}}
    
    - name: Build and push Docker image
      uses: docker/build-push-action@v5
      with:
        context: .
        push: true
        tags: ${{ steps.meta.outputs.tags }}
        cache-from: type=gha
        cache-to: type=gha,mode=max
        build-args: |
          NODE_ENV=production

  deploy-staging:
    name: Deploy to Staging
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/develop'
    environment:
      name: staging
      url: https://staging.example.com
    
    steps:
    - name: Configure AWS credentials
      uses: aws-actions/configure-aws-credentials@v4
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-region: ${{ env.AWS_REGION }}
    
    - name: Update ECS service
      run: |
        aws ecs update-service \
          --cluster staging-cluster \
          --service ${{ env.ECS_SERVICE }} \
          --force-new-deployment

  deploy-production:
    name: Deploy to Production
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    environment:
      name: production
      url: https://example.com
    
    steps:
    - name: Configure AWS credentials
      uses: aws-actions/configure-aws-credentials@v4
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-region: ${{ env.AWS_REGION }}
    
    - name: Update ECS service
      run: |
        aws ecs update-service \
          --cluster ${{ env.ECS_CLUSTER }} \
          --service ${{ env.ECS_SERVICE }} \
          --force-new-deployment
    
    - name: Wait for deployment
      run: |
        aws ecs wait services-stable \
          --cluster ${{ env.ECS_CLUSTER }} \
          --services ${{ env.ECS_SERVICE }}
    
    - name: Notify Slack
      uses: slackapi/slack-github-action@v1
      with:
        payload: |
          {
            "text": "Deployment to Production completed successfully!"
          }
      env:
        SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_URL }}
```

### AWS CodePipeline (承認済み / Approved)

```yaml
cicd:
  name: "AWS CodePipeline"
  status: "approved"
  use_cases:
    - "AWS統合が必要な場合"
    - "複雑なデプロイメントパイプライン"
  
  components:
    - "CodeCommit / GitHub"
    - "CodeBuild"
    - "CodeDeploy"
    - "CodePipeline"
```

---

## Infrastructure as Code

### Terraform (標準 / Standard)

```yaml
iac:
  name: "Terraform"
  version: "1.6+"
  status: "standard"
  
  structure:
    root_modules:
      - "networking"
      - "compute"
      - "database"
      - "monitoring"
    
    state_management:
      backend: "S3"
      locking: "DynamoDB"
      encryption: true
  
  best_practices:
    - "モジュール化"
    - "環境ごとのワークスペース"
    - "変数の外部化"
    - "terraform.tfvarsの暗号化"
```

**Terraformプロジェクト構造**:

```
terraform/
├── modules/
│   ├── vpc/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   ├── outputs.tf
│   │   └── README.md
│   ├── ecs/
│   └── rds/
├── environments/
│   ├── production/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   ├── terraform.tfvars
│   │   └── backend.tf
│   ├── staging/
│   └── development/
├── .terraform.lock.hcl
└── README.md
```

**Terraform設定例**:

```hcl
# environments/production/main.tf
terraform {
  required_version = ">= 1.6"
  
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  
  backend "s3" {
    bucket         = "my-terraform-state"
    key            = "production/terraform.tfstate"
    region         = "ap-northeast-1"
    encrypt        = true
    dynamodb_table = "terraform-locks"
  }
}

provider "aws" {
  region = var.aws_region
  
  default_tags {
    tags = {
      Environment = "production"
      ManagedBy   = "Terraform"
      Project     = "MyApp"
    }
  }
}

# VPCモジュール
module "vpc" {
  source = "../../modules/vpc"
  
  environment         = "production"
  vpc_cidr           = "10.0.0.0/16"
  availability_zones = ["ap-northeast-1a", "ap-northeast-1c", "ap-northeast-1d"]
  
  public_subnet_cidrs  = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
  private_subnet_cidrs = ["10.0.11.0/24", "10.0.12.0/24", "10.0.13.0/24"]
  database_subnet_cidrs = ["10.0.21.0/24", "10.0.22.0/24", "10.0.23.0/24"]
  
  enable_nat_gateway = true
  single_nat_gateway = false
}

# ECSクラスター
module "ecs" {
  source = "../../modules/ecs"
  
  cluster_name = "production-cluster"
  vpc_id       = module.vpc.vpc_id
  subnet_ids   = module.vpc.private_subnet_ids
  
  services = {
    api = {
      cpu    = 1024
      memory = 2048
      desired_count = 3
    }
  }
}

# RDSデータベース
module "rds" {
  source = "../../modules/rds"
  
  identifier     = "production-db"
  engine         = "postgres"
  engine_version = "16.1"
  instance_class = "db.t3.medium"
  
  allocated_storage = 100
  storage_encrypted = true
  
  vpc_id            = module.vpc.vpc_id
  subnet_ids        = module.vpc.database_subnet_ids
  multi_az          = true
  
  backup_retention_period = 30
  backup_window          = "03:00-04:00"
  maintenance_window     = "mon:04:00-mon:05:00"
}
```

### AWS CloudFormation (承認済み / Approved)

```yaml
iac:
  name: "AWS CloudFormation"
  status: "approved"
  use_cases:
    - "AWS特化リソース"
    - "StackSetsによるマルチアカウント管理"
```

---

## モニタリングと可観測性 / Monitoring and Observability

### Amazon CloudWatch (標準 / Standard)

```yaml
monitoring:
  name: "CloudWatch"
  status: "standard"
  
  components:
    metrics:
      - "標準メトリクス (CPU, メモリ, ネットワーク)"
      - "カスタムメトリクス"
      - "アプリケーションメトリクス"
    
    logs:
      retention: "30日 (本番), 7日 (開発)"
      insights: true
    
    alarms:
      - "CPU使用率 > 80%"
      - "メモリ使用率 > 85%"
      - "HTTPエラー率 > 5%"
      - "レスポンスタイム > 2秒"
    
    dashboards:
      - "システム概要"
      - "アプリケーションパフォーマンス"
      - "インフラストラクチャ健全性"
```

**CloudWatch Alarm設定例**:

```json
{
  "AlarmName": "HighCPUUtilization",
  "AlarmDescription": "Alert when CPU exceeds 80%",
  "MetricName": "CPUUtilization",
  "Namespace": "AWS/ECS",
  "Statistic": "Average",
  "Period": 300,
  "EvaluationPeriods": 2,
  "Threshold": 80.0,
  "ComparisonOperator": "GreaterThanThreshold",
  "Dimensions": [
    {
      "Name": "ServiceName",
      "Value": "my-app-service"
    },
    {
      "Name": "ClusterName",
      "Value": "production-cluster"
    }
  ],
  "AlarmActions": [
    "arn:aws:sns:ap-northeast-1:123456789012:production-alerts"
  ]
}
```

### Prometheus + Grafana (推奨 / Recommended)

```yaml
monitoring:
  name: "Prometheus + Grafana"
  status: "recommended"
  use_cases:
    - "詳細なメトリクス収集"
    - "カスタムダッシュボード"
    - "アラートルール"
  
  deployment:
    prometheus:
      version: "2.48+"
      storage: "EBS (gp3)"
      retention: "15日"
    
    grafana:
      version: "10.x"
      authentication: "OAuth (SSO)"
  
  exporters:
    - "node-exporter"
    - "blackbox-exporter"
    - "postgres-exporter"
    - "redis-exporter"
```

### Datadog (承認済み / Approved)

```yaml
monitoring:
  name: "Datadog"
  status: "approved"
  use_cases:
    - "エンタープライズ監視"
    - "APM (Application Performance Monitoring)"
    - "統合ダッシュボード"
  
  integrations:
    - "AWS"
    - "Docker"
    - "PostgreSQL"
    - "Redis"
    - "Nginx"
```

---

## ロギング / Logging

### CloudWatch Logs (標準 / Standard)

```yaml
logging:
  name: "CloudWatch Logs"
  status: "standard"
  
  log_groups:
    application: "/aws/ecs/my-app"
    system: "/aws/ec2/system"
    access: "/aws/alb/access-logs"
  
  retention:
    production: 30
    staging: 7
    development: 3
  
  log_insights:
    enabled: true
    saved_queries:
      - "エラーログ分析"
      - "レスポンスタイム分析"
      - "ユーザーアクティビティ"
```

**CloudWatch Logs Insights クエリ例**:

```
# エラーログ検索
fields @timestamp, @message
| filter @message like /ERROR/
| sort @timestamp desc
| limit 100

# レスポンスタイム分析
fields @timestamp, duration
| filter @type = "http_request"
| stats avg(duration), max(duration), min(duration) by bin(5m)

# HTTPステータスコード集計
fields @timestamp, status_code
| filter @type = "http_request"
| stats count() by status_code
```

### ELK Stack (承認済み / Approved)

```yaml
logging:
  name: "ELK Stack"
  status: "approved"
  components:
    - "Elasticsearch"
    - "Logstash"
    - "Kibana"
  
  use_cases:
    - "大規模ログ分析"
    - "全文検索"
    - "複雑なログ集約"
```

---

## セキュリティツール / Security Tools

### AWS Security Services

```yaml
security:
  services:
    - name: "AWS WAF"
      purpose: "Webアプリケーションファイアウォール"
      rules:
        - "SQL Injection防止"
        - "XSS防止"
        - "レート制限"
        - "地域ベースのブロック"
    
    - name: "GuardDuty"
      purpose: "脅威検出"
      enabled: true
    
    - name: "Security Hub"
      purpose: "セキュリティ統合管理"
      standards:
        - "AWS Foundational Security Best Practices"
        - "CIS AWS Foundations Benchmark"
    
    - name: "AWS Secrets Manager"
      purpose: "シークレット管理"
      rotation: "自動ローテーション (90日)"
    
    - name: "AWS KMS"
      purpose: "暗号化キー管理"
      key_rotation: true
```

### Trivy (必須 / Required)

```yaml
security:
  name: "Trivy"
  purpose: "コンテナ脆弱性スキャン"
  status: "required"
  
  scan_targets:
    - "Dockerイメージ"
    - "ファイルシステム"
    - "Git リポジトリ"
  
  ci_integration: true
  severity_threshold: "CRITICAL,HIGH"
```

---

## ネットワーキング / Networking

### Content Delivery Network (CDN)

```yaml
cdn:
  name: "Amazon CloudFront"
  status: "standard"
  
  configuration:
    price_class: "PriceClass_All"
    http_version: "http2and3"
    ssl_certificate: "ACM"
    
    cache_behaviors:
      static_assets:
        path_pattern: "/static/*"
        ttl: 86400  # 24時間
      
      api:
        path_pattern: "/api/*"
        ttl: 0  # キャッシュなし
    
    security:
      - "Origin Access Identity"
      - "Signed URLs (プライベートコンテンツ)"
      - "AWS WAF統合"
```

### Load Balancing

```yaml
load_balancer:
  type: "Application Load Balancer"
  status: "standard"
  
  configuration:
    scheme: "internet-facing"
    ip_address_type: "ipv4"
    
    listeners:
      - protocol: "HTTPS"
        port: 443
        ssl_policy: "ELBSecurityPolicy-TLS13-1-2-2021-06"
      
      - protocol: "HTTP"
        port: 80
        default_action: "redirect to HTTPS"
    
    target_groups:
      health_check:
        protocol: "HTTP"
        path: "/health"
        interval: 30
        timeout: 5
        healthy_threshold: 2
        unhealthy_threshold: 3
    
    attributes:
      - "access_logs.s3.enabled: true"
      - "deletion_protection.enabled: true"
      - "idle_timeout.timeout_seconds: 60"
```

---

## バックアップとディザスタリカバリ / Backup and Disaster Recovery

### バックアップ戦略 / Backup Strategy

```yaml
backup:
  rds:
    automated_backups:
      retention_period: 30
      backup_window: "03:00-04:00 JST"
    
    snapshots:
      frequency: "毎週日曜日"
      retention: "90日"
      cross_region_copy: true
      destination_region: "us-east-1"
  
  s3:
    versioning: true
    lifecycle_rules:
      - transition_to_glacier: 90
      - expire: 365
    
    cross_region_replication:
      enabled: true
      destination_bucket: "s3://my-app-backup-us-east-1"
  
  efs:
    aws_backup:
      frequency: "毎日"
      retention: "35日"
```

### ディザスタリカバリ / Disaster Recovery

```yaml
disaster_recovery:
  strategy: "Warm Standby"
  rto: "1時間"  # Recovery Time Objective
  rpo: "15分"   # Recovery Point Objective
  
  dr_site:
    region: "us-east-1"
    components:
      - "VPC (事前構築)"
      - "RDS Read Replica (自動昇格可能)"
      - "S3 Cross-Region Replication"
      - "Route 53 Health Checks + Failover"
  
  failover_process:
    - "Route 53でDNSフェイルオーバー"
    - "RDS Read Replicaをプライマリに昇格"
    - "ECS/EKSスケールアップ"
    - "アプリケーション動作確認"
  
  testing_schedule: "四半期ごと"
```

---

## 標準設定 / Standard Configuration

### タグ付け戦略 / Tagging Strategy

```yaml
tagging:
  required_tags:
    - key: "Environment"
      values: ["production", "staging", "development"]
    
    - key: "Project"
      description: "プロジェクト名"
    
    - key: "Owner"
      description: "担当チーム"
    
    - key: "CostCenter"
      description: "コストセンターコード"
    
    - key: "ManagedBy"
      values: ["Terraform", "CloudFormation", "Manual"]
  
  optional_tags:
    - "Application"
    - "Version"
    - "BackupPolicy"
```

### 命名規則 / Naming Conventions

```yaml
naming:
  format: "{environment}-{project}-{resource}-{identifier}"
  
  examples:
    vpc: "prod-myapp-vpc-main"
    subnet: "prod-myapp-subnet-public-1a"
    security_group: "prod-myapp-sg-alb"
    ecs_cluster: "prod-myapp-ecs-cluster"
    rds: "prod-myapp-rds-postgres"
    s3_bucket: "prod-myapp-s3-assets"
```

---

## ベストプラクティス / Best Practices

### セキュリティ / Security

1. **最小権限の原則**
   - IAMロール、ポリシーは必要最小限
   - 定期的な権限レビュー

2. **ネットワークセグメンテーション**
   - パブリック/プライベート/データベースサブネット分離
   - セキュリティグループで厳格な制御

3. **暗号化**
   - 保管時暗号化 (at rest)
   - 転送時暗号化 (in transit)
   - KMSによるキー管理

4. **シークレット管理**
   - Secrets Managerの使用
   - ハードコード禁止
   - 自動ローテーション

### 信頼性 / Reliability

1. **Multi-AZ配置**
   - 最低2つのAZ
   - 本番環境は3つのAZ推奨

2. **自動スケーリング**
   - メトリクスベースのオートスケーリング
   - 予測スケーリング

3. **ヘルスチェック**
   - アプリケーションレベルのヘルスチェック
   - ロードバランサーとの統合

4. **バックアップ**
   - 自動バックアップ有効化
   - クロスリージョンバックアップ

### パフォーマンス / Performance

1. **キャッシング**
   - CloudFront CDN
   - ElastiCache (Redis)
   - アプリケーションレベルキャッシュ

2. **データベース最適化**
   - Read Replica活用
   - 接続プーリング
   - クエリ最適化

3. **リソース最適化**
   - 適切なインスタンスタイプ選択
   - CPU/メモリの適正配分

---

## コスト最適化 / Cost Optimization

### コスト削減戦略 / Cost Reduction Strategies

```yaml
cost_optimization:
  compute:
    - name: "Savings Plans"
      discount: "最大72%"
      commitment: "1年 or 3年"
    
    - name: "Reserved Instances"
      use_cases: ["安定したワークロード"]
    
    - name: "Spot Instances"
      use_cases: ["バッチ処理", "開発環境"]
      discount: "最大90%"
  
  storage:
    - name: "S3 Intelligent-Tiering"
      savings: "自動的にコスト最適化"
    
    - name: "EBS gp3"
      advantage: "gp2より20%安価"
  
  monitoring:
    - "AWS Cost Explorer"
    - "AWS Budgets (予算アラート)"
    - "Trusted Advisor"
  
  best_practices:
    - "未使用リソースの削除"
    - "開発環境の夜間停止"
    - "適切なインスタンスサイズ"
    - "データ転送コストの最適化"
```

---

## 参考資料 / References

### 公式ドキュメント / Official Documentation

- [AWS Documentation](https://docs.aws.amazon.com/)
- [Docker Documentation](https://docs.docker.com/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Terraform Documentation](https://www.terraform.io/docs)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)

### ベストプラクティスガイド / Best Practice Guides

- [AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/)
- [AWS Security Best Practices](https://docs.aws.amazon.com/security/)
- [Kubernetes Production Best Practices](https://learnk8s.io/production-best-practices)
- [Docker Security Best Practices](https://docs.docker.com/engine/security/)

### ツールとリソース / Tools and Resources

- [Terraform AWS Modules](https://registry.terraform.io/namespaces/terraform-aws-modules)
- [AWS Architecture Center](https://aws.amazon.com/architecture/)
- [Cloud Native Computing Foundation](https://www.cncf.io/)

---

## バージョン履歴 / Version History

| バージョン | 日付 | 変更内容 | 承認者 |
|----------|------|---------|--------|
| 2.0.0 | 2025-01-15 | インフラストラクチャスタック標準策定 | Infrastructure Team |
| 1.5.0 | 2024-12-01 | ECS Fargate標準化、EKS推奨 | CTO |
| 1.0.0 | 2024-10-01 | 初版リリース | DevOps Manager |

---

## 承認 / Approval

| 役割 | 氏名 | 承認日 | 署名 |
|------|------|--------|------|
| CTO | [Name] | 2025-01-15 | [Signature] |
| Infrastructure Lead | [Name] | 2025-01-15 | [Signature] |
| Security Lead | [Name] | 2025-01-15 | [Signature] |
| Cloud Architect | [Name] | 2025-01-15 | [Signature] |

---

## お問い合わせ / Contact

**Infrastructure & DevOps Team**
- Email: infrastructure@company.com
- Slack: #infrastructure
- 担当者: [Infrastructure Lead Name]

---
