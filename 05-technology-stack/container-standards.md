# コンテナ標準 / Container Standards

## バージョン情報 / Version Information
- **最終更新日 / Last Updated**: 2025-10-24
- **バージョン / Version**: 1.0
- **対象 / Target**: すべてのプロジェクト / All Projects
- **適用範囲 / Scope**: 任意 / Optional (Tier 3)

---

## 目的 / Purpose

このドキュメントは、Dockerコンテナとkubernetesを使用したコンテナ化アプリケーションの標準を定義します。Dockerfileのベストプラクティス、イメージ最適化、Kubernetes manifest標準、Helmチャート、セキュリティスキャンなど、コンテナ化の実装ガイドラインを提供します。

This document defines standards for containerized applications using Docker and Kubernetes, including Dockerfile best practices, image optimization, Kubernetes manifests, Helm charts, and security scanning guidelines.

---

## 1. Dockerfileベストプラクティス / Dockerfile Best Practices

### 1.1 マルチステージビルド

```dockerfile
# ✅ 良い例: マルチステージビルド
# ステージ1: ビルド
FROM node:18-alpine AS builder

WORKDIR /app

# 依存関係のみ先にコピー（キャッシュ活用）
COPY package*.json ./
RUN npm ci --only=production

# ソースコードをコピー
COPY . .

# ビルド
RUN npm run build

# ステージ2: 実行環境（最小化）
FROM node:18-alpine

WORKDIR /app

# セキュリティ: 非rootユーザーで実行
RUN addgroup -g 1001 -S nodejs && \
    adduser -S nodejs -u 1001

# ビルド成果物のみコピー
COPY --from=builder --chown=nodejs:nodejs /app/dist ./dist
COPY --from=builder --chown=nodejs:nodejs /app/node_modules ./node_modules
COPY --chown=nodejs:nodejs package*.json ./

USER nodejs

EXPOSE 3000

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD node healthcheck.js

CMD ["node", "dist/main.js"]
```

```dockerfile
# ❌ 悪い例: 単一ステージ、最適化なし
FROM node:18

WORKDIR /app

COPY . .
RUN npm install  # devDependenciesも含まれる
RUN npm run build

EXPOSE 3000

# rootユーザーで実行 - NG
CMD ["node", "dist/main.js"]
```

### 1.2 イメージサイズ最適化

```dockerfile
# ✅ 良い例: Python アプリケーション
FROM python:3.11-slim AS builder

WORKDIR /app

# 依存関係インストール（キャッシュ活用）
COPY requirements.txt .
RUN pip install --user --no-cache-dir -r requirements.txt

# 実行環境
FROM python:3.11-slim

WORKDIR /app

# セキュリティアップデート
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 非rootユーザー
RUN useradd -m -u 1001 appuser

# 依存関係をコピー
COPY --from=builder /root/.local /home/appuser/.local

# アプリケーションコード
COPY --chown=appuser:appuser . .

USER appuser

ENV PATH=/home/appuser/.local/bin:$PATH

HEALTHCHECK --interval=30s --timeout=3s \
  CMD python healthcheck.py || exit 1

CMD ["python", "app.py"]
```

### 1.3 レイヤーキャッシング最適化

```dockerfile
# ✅ 良い例: 変更頻度の低いものを先にコピー
FROM golang:1.21-alpine AS builder

WORKDIR /app

# 1. 依存関係定義ファイル（変更頻度: 低）
COPY go.mod go.sum ./
RUN go mod download

# 2. ソースコード（変更頻度: 高）
COPY . .

# 3. ビルド
RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o main .

# 実行環境
FROM alpine:latest

RUN apk --no-cache add ca-certificates

WORKDIR /root/

COPY --from=builder /app/main .

CMD ["./main"]
```

---

## 2. イメージタグ戦略 / Image Tagging Strategy

### 2.1 タグ命名規則

```bash
# ✅ 良い例: セマンティックバージョニング + メタデータ
myapp:1.2.3                    # セマンティックバージョン
myapp:1.2.3-alpine             # ベースイメージ情報
myapp:1.2.3-20251024           # ビルド日付
myapp:1.2.3-abc123             # Gitコミットハッシュ
myapp:1.2.3-abc123-alpine      # 組み合わせ

# 環境別タグ
myapp:1.2.3-dev
myapp:1.2.3-staging  
myapp:1.2.3-prod

# ❌ 悪い例
myapp:latest         # 本番環境では使用しない
myapp:v1             # バージョンが不明確
myapp:new            # 意味不明
```

### 2.2 タグ管理スクリプト

```python
# ✅ 良い例: イメージタグ生成
import subprocess
import datetime
from typing import List

def generate_image_tags(
    image_name: str,
    version: str,
    commit_hash: str,
    base_image: str = "alpine"
) -> List[str]:
    """イメージタグを生成"""
    
    date_str = datetime.datetime.now().strftime("%Y%m%d")
    short_hash = commit_hash[:7]
    
    tags = [
        f"{image_name}:{version}",
        f"{image_name}:{version}-{base_image}",
        f"{image_name}:{version}-{date_str}",
        f"{image_name}:{version}-{short_hash}",
        f"{image_name}:{version}-{short_hash}-{base_image}",
    ]
    
    return tags

def build_and_tag_image(dockerfile_path: str, context_path: str, 
                       image_name: str, version: str, commit_hash: str):
    """イメージをビルドして複数タグを付与"""
    
    tags = generate_image_tags(image_name, version, commit_hash)
    
    # ビルド
    subprocess.run([
        "docker", "build",
        "-f", dockerfile_path,
        "-t", tags[0],  # メインタグ
        context_path
    ], check=True)
    
    # 追加タグを付与
    for tag in tags[1:]:
        subprocess.run([
            "docker", "tag", tags[0], tag
        ], check=True)
    
    print(f"✅ イメージビルド完了: {len(tags)} タグ")
    for tag in tags:
        print(f"  - {tag}")
    
    return tags
```

---

## 3. Kubernetes Manifest標準 / Kubernetes Manifest Standards

### 3.1 Deployment

```yaml
# ✅ 良い例: 本番環境向けDeployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
  namespace: production
  labels:
    app: myapp
    version: v1.2.3
    environment: production
spec:
  replicas: 3
  revisionHistoryLimit: 10
  
  # アップデート戦略
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  
  selector:
    matchLabels:
      app: myapp
      version: v1.2.3
  
  template:
    metadata:
      labels:
        app: myapp
        version: v1.2.3
        environment: production
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "9090"
        prometheus.io/path: "/metrics"
    
    spec:
      # セキュリティコンテキスト
      securityContext:
        runAsNonRoot: true
        runAsUser: 1001
        fsGroup: 1001
      
      # サービスアカウント
      serviceAccountName: myapp-sa
      
      containers:
      - name: myapp
        image: myregistry/myapp:1.2.3-abc123-alpine
        imagePullPolicy: IfNotPresent
        
        ports:
        - name: http
          containerPort: 3000
          protocol: TCP
        - name: metrics
          containerPort: 9090
          protocol: TCP
        
        # 環境変数
        env:
        - name: NODE_ENV
          value: "production"
        - name: LOG_LEVEL
          value: "info"
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: myapp-secrets
              key: database-url
        
        # リソース制限
        resources:
          requests:
            cpu: 100m
            memory: 128Mi
          limits:
            cpu: 500m
            memory: 512Mi
        
        # ヘルスチェック
        livenessProbe:
          httpGet:
            path: /health/live
            port: http
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        
        readinessProbe:
          httpGet:
            path: /health/ready
            port: http
          initialDelaySeconds: 10
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 3
        
        # セキュリティコンテキスト
        securityContext:
          allowPrivilegeEscalation: false
          readOnlyRootFilesystem: true
          capabilities:
            drop:
            - ALL
        
        # ボリュームマウント
        volumeMounts:
        - name: tmp
          mountPath: /tmp
        - name: cache
          mountPath: /app/cache
      
      # ボリューム
      volumes:
      - name: tmp
        emptyDir: {}
      - name: cache
        emptyDir: {}
      
      # アフィニティ（Pod分散）
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
```

### 3.2 Service

```yaml
# ✅ 良い例: Service定義
apiVersion: v1
kind: Service
metadata:
  name: myapp-service
  namespace: production
  labels:
    app: myapp
spec:
  type: ClusterIP
  sessionAffinity: ClientIP
  sessionAffinityConfig:
    clientIP:
      timeoutSeconds: 10800
  ports:
  - name: http
    port: 80
    targetPort: http
    protocol: TCP
  - name: metrics
    port: 9090
    targetPort: metrics
    protocol: TCP
  selector:
    app: myapp
    version: v1.2.3
```

### 3.3 ConfigMap と Secret

```yaml
# ✅ 良い例: ConfigMap
apiVersion: v1
kind: ConfigMap
metadata:
  name: myapp-config
  namespace: production
data:
  app.conf: |
    server {
      listen 80;
      location / {
        proxy_pass http://backend:3000;
      }
    }
  log-config.json: |
    {
      "level": "info",
      "format": "json"
    }

---
# ✅ 良い例: Secret（base64エンコード済み）
apiVersion: v1
kind: Secret
metadata:
  name: myapp-secrets
  namespace: production
type: Opaque
data:
  database-url: cG9zdGdyZXNxbDovL3VzZXI6cGFzc0BkYi5leGFtcGxlLmNvbTo1NDMyL215ZGI=
  api-key: YWJjMTIzNDU2Nzg5MA==
```

---

## 4. Helmチャート標準 / Helm Chart Standards

### 4.1 Chart構造

```
mychart/
├── Chart.yaml              # チャートメタデータ
├── values.yaml             # デフォルト値
├── values-dev.yaml         # 開発環境用
├── values-staging.yaml     # ステージング環境用
├── values-prod.yaml        # 本番環境用
├── templates/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── ingress.yaml
│   ├── configmap.yaml
│   ├── secret.yaml
│   ├── hpa.yaml           # HorizontalPodAutoscaler
│   ├── pdb.yaml           # PodDisruptionBudget
│   ├── serviceaccount.yaml
│   ├── _helpers.tpl       # テンプレートヘルパー
│   └── NOTES.txt
└── README.md
```

### 4.2 Chart.yaml

```yaml
# ✅ 良い例: Chart.yaml
apiVersion: v2
name: myapp
description: My Application Helm Chart
type: application
version: 1.2.3
appVersion: "1.2.3"

keywords:
  - microservices
  - api
  - backend

maintainers:
  - name: DevOps Team
    email: devops@example.com

dependencies:
  - name: postgresql
    version: "12.x"
    repository: "https://charts.bitnami.com/bitnami"
    condition: postgresql.enabled
  
  - name: redis
    version: "17.x"
    repository: "https://charts.bitnami.com/bitnami"
    condition: redis.enabled
```

### 4.3 values.yaml

```yaml
# ✅ 良い例: values.yaml（詳細なコメント付き）
# イメージ設定
image:
  repository: myregistry/myapp
  tag: "1.2.3"
  pullPolicy: IfNotPresent

# レプリカ数
replicaCount: 3

# サービス設定
service:
  type: ClusterIP
  port: 80
  targetPort: 3000

# Ingress設定
ingress:
  enabled: true
  className: nginx
  annotations:
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
  hosts:
    - host: myapp.example.com
      paths:
        - path: /
          pathType: Prefix
  tls:
    - secretName: myapp-tls
      hosts:
        - myapp.example.com

# リソース設定
resources:
  requests:
    cpu: 100m
    memory: 128Mi
  limits:
    cpu: 500m
    memory: 512Mi

# オートスケーリング
autoscaling:
  enabled: true
  minReplicas: 3
  maxReplicas: 10
  targetCPUUtilizationPercentage: 80
  targetMemoryUtilizationPercentage: 80

# ヘルスチェック
livenessProbe:
  httpGet:
    path: /health/live
    port: http
  initialDelaySeconds: 30
  periodSeconds: 10

readinessProbe:
  httpGet:
    path: /health/ready
    port: http
  initialDelaySeconds: 10
  periodSeconds: 5

# 環境変数
env:
  - name: NODE_ENV
    value: "production"
  - name: LOG_LEVEL
    value: "info"

# Secrets
secrets:
  DATABASE_URL: "postgresql://user:pass@db:5432/mydb"
  API_KEY: "secret-api-key"

# PostgreSQL依存
postgresql:
  enabled: true
  auth:
    database: mydb
    username: myuser
    password: mypassword

# Redis依存
redis:
  enabled: true
  auth:
    password: redispassword
```

### 4.4 テンプレート例

```yaml
# templates/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ include "myapp.fullname" . }}
  labels:
    {{- include "myapp.labels" . | nindent 4 }}
spec:
  {{- if not .Values.autoscaling.enabled }}
  replicas: {{ .Values.replicaCount }}
  {{- end }}
  selector:
    matchLabels:
      {{- include "myapp.selectorLabels" . | nindent 6 }}
  template:
    metadata:
      labels:
        {{- include "myapp.selectorLabels" . | nindent 8 }}
    spec:
      containers:
      - name: {{ .Chart.Name }}
        image: "{{ .Values.image.repository }}:{{ .Values.image.tag | default .Chart.AppVersion }}"
        imagePullPolicy: {{ .Values.image.pullPolicy }}
        ports:
        - name: http
          containerPort: {{ .Values.service.targetPort }}
        env:
        {{- range .Values.env }}
        - name: {{ .name }}
          value: {{ .value | quote }}
        {{- end }}
        {{- if .Values.livenessProbe }}
        livenessProbe:
          {{- toYaml .Values.livenessProbe | nindent 10 }}
        {{- end }}
        {{- if .Values.readinessProbe }}
        readinessProbe:
          {{- toYaml .Values.readinessProbe | nindent 10 }}
        {{- end }}
        resources:
          {{- toYaml .Values.resources | nindent 10 }}
```

---

## 5. セキュリティスキャン / Security Scanning

### 5.1 Trivyによるスキャン

```bash
# ✅ 良い例: Trivyでイメージスキャン
#!/bin/bash

IMAGE_NAME=$1
SEVERITY_THRESHOLD="HIGH,CRITICAL"

echo "Scanning image: $IMAGE_NAME"

# イメージスキャン
trivy image \
  --severity $SEVERITY_THRESHOLD \
  --exit-code 1 \
  --no-progress \
  --format json \
  --output scan-results.json \
  $IMAGE_NAME

# 結果の確認
if [ $? -eq 0 ]; then
  echo "✅ No vulnerabilities found"
else
  echo "❌ Vulnerabilities detected"
  trivy image --severity $SEVERITY_THRESHOLD $IMAGE_NAME
  exit 1
fi
```

### 5.2 CI/CDパイプラインでのスキャン

```yaml
# ✅ 良い例: GitLab CI/CDでのスキャン
stages:
  - build
  - scan
  - deploy

build:
  stage: build
  script:
    - docker build -t $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA .
    - docker push $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA

security_scan:
  stage: scan
  image: aquasec/trivy:latest
  script:
    - trivy image --exit-code 1 --severity CRITICAL $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA
  allow_failure: false

deploy:
  stage: deploy
  script:
    - helm upgrade --install myapp ./chart --set image.tag=$CI_COMMIT_SHA
  only:
    - main
```

---

## 6. Devin向けの利用パターン / Usage Patterns for Devin

### プロンプト1: Dockerfile最適化
```
タスク: 既存のDockerfileを最適化してください

要件:
1. マルチステージビルドを実装
2. イメージサイズを50%以上削減
3. 非rootユーザーで実行
4. ヘルスチェックを追加
5. レイヤーキャッシングを最適化

実装基準:
- このドキュメントのセクション1（Dockerfileベストプラクティス）に従う
- alpine ベースイメージを使用
```

### プロンプト2: Helmチャート作成
```
タスク: アプリケーション用のHelmチャートを作成してください

要件:
1. 標準的なチャート構造
2. 環境別values.yaml（dev/staging/prod）
3. HPA、PDB、Ingressを含める
4. PostgreSQLとRedisの依存関係
5. 詳細なREADME.md

実装基準:
- このドキュメントのセクション4（Helmチャート標準）に従う
```

### プロンプト3: セキュリティ強化
```
タスク: Kubernetesマニフェストのセキュリティを強化してください

要件:
1. SecurityContextの適切な設定
2. NetworkPolicyの実装
3. PodSecurityPolicyまたはPodSecurityStandards
4. Secretsの適切な管理
5. RBACの最小権限原則

実装基準:
- このドキュメントのセクション3（Kubernetes Manifest標準）に従う
```

---

## 7. チェックリスト / Checklist

### Dockerfileチェックリスト
- [ ] マルチステージビルドを使用
- [ ] alpineまたはdistrolessベースイメージ
- [ ] 非rootユーザーで実行
- [ ] ヘルスチェックを含む
- [ ] .dockerignoreファイルを作成
- [ ] レイヤーキャッシングを最適化
- [ ] セキュリティスキャン実施

### Kubernetesマニフェストチェックリスト
- [ ] リソースrequest/limitを設定
- [ ] livenessProbe/readinessProbeを設定
- [ ] SecurityContextを適切に設定
- [ ] PodAntiAffinityで分散配置
- [ ] HPA設定（必要に応じて）
- [ ] PDB設定（本番環境）
- [ ] NetworkPolicy設定

### Helmチャートチェックリスト
- [ ] Chart.yamlに詳細情報
- [ ] values.yamlに詳細コメント
- [ ] 環境別valuesファイル
- [ ] _helpers.tplでテンプレート共通化
- [ ] NOTES.txtでデプロイ後の情報提供
- [ ] README.mdで使用方法を説明
- [ ] helm lintでチェック

---

## 8. 関連ドキュメント / Related Documents

- [マイクロサービス設計ガイドライン](../02-architecture-standards/microservices-guidelines.md)
- [CI/CDパイプライン標準](../03-development-process/ci-cd-pipeline.md)
- [監視・ログ標準](./monitoring-logging.md)
- [セキュリティチェックリスト](../07-security-compliance/security-checklist.md)

---

## 9. 更新履歴 / Change History

| バージョン | 日付 | 変更内容 | 作成者 |
|---------|------|---------|-------|
| 1.0 | 2025-10-24 | 初版作成 | Development Team |

---

**このドキュメントの維持管理についてのお問い合わせは、DevOpsチームまでご連絡ください。**
