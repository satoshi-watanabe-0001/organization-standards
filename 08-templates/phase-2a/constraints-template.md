---
title: "[プロジェクト名] 制約条件文書 / Constraints Document"
version: "1.0.0"
template_version: "1.0.0"
created_date: "YYYY-MM-DD"
phase: "Phase 2A (Pre-Implementation Design)"
status: "draft"
owner: "[担当者名]"
reviewers: []
approved_by: ""
approved_date: ""
---

# 制約条件文書: [プロジェクト名]

> **目的**: Phase 2Aで定義する制約条件の標準テンプレート  
> **スコープ**: セキュリティ、パフォーマンス、技術制約を明確化  
> **作成タイミング**: 実装開始前（Phase 2A）  
> **作成時間目安**: 2-4時間（プロジェクト規模により）

---

## 📋 メタデータ / Metadata

| 項目 | 内容 |
|-----|------|
| **プロジェクト名** | [例: ユーザー認証マイクロサービス] |
| **プロジェクトコード** | [例: AUTH-SERVICE-001] |
| **作成日** | YYYY-MM-DD |
| **作成者** | [名前] - [役職] |
| **レビュアー** | [セキュリティチーム], [インフラチーム], [QA] |
| **承認者** | [テックリード名] |
| **承認日** | YYYY-MM-DD |
| **ステータス** | `draft` / `in-review` / `approved` |
| **関連ADR** | [ADR番号] |
| **関連ドキュメント** | [API契約書へのリンク] |

---

## 🔒 セキュリティ制約 / Security Constraints

### 🛡️ 認証・認可 / Authentication & Authorization

#### 認証メカニズム

```yaml
authentication:
  mechanism: "JWT (JSON Web Token)"
  standard: "RFC 7519"
  
  token_specifications:
    access_token:
      expiry: "15分"
      algorithm: "HS256 または RS256"
      storage_client: "メモリ または sessionStorage"
      required_claims: ["sub", "email", "role", "exp", "iat"]
    
    refresh_token:
      expiry: "7日"
      algorithm: "HS256"
      storage_client: "HTTPOnly Cookie（推奨）"
      required_claims: ["sub", "token_id", "exp", "iat"]

requirements:
  - [ ] 全ての機密APIエンドポイントで認証必須
  - [ ] トークンはHTTPSのみで送信
  - [ ] リフレッシュトークンはHTTPOnly Cookieで管理（推奨）
  - [ ] トークン無効化メカニズムの実装（ブラックリスト）
  - [ ] トークン検証時のタイムスタンプチェック
```

#### 認可モデル

```yaml
authorization:
  model: "RBAC (Role-Based Access Control)"
  
  roles:
    - name: "user"
      description: "一般ユーザー"
      permissions: ["read:own_profile", "update:own_profile"]
    
    - name: "admin"
      description: "管理者"
      permissions: ["read:all_users", "update:all_users", "delete:users"]
    
    - name: "superadmin"
      description: "スーパー管理者"
      permissions: ["all"]

requirements:
  - [ ] 最小権限の原則を適用
  - [ ] 権限チェックはバックエンドで実施（フロントエンドでも表示制御）
  - [ ] ロール変更時の既存トークン無効化
  - [ ] 管理者操作の監査ログ記録
```

---

### 🔐 データ保護 / Data Protection

#### 暗号化

```yaml
encryption:
  in_transit:
    protocol: "TLS 1.3以上"
    cipher_suites: "推奨: TLS_AES_256_GCM_SHA384"
    requirements:
      - [ ] 全通信をHTTPSで暗号化
      - [ ] HTTP→HTTPSリダイレクト
      - [ ] HSTS（HTTP Strict Transport Security）ヘッダー設定
  
  at_rest:
    password_hashing:
      algorithm: "bcrypt"
      cost_factor: 12
      requirements:
        - [ ] パスワードはbcryptでハッシュ化して保存
        - [ ] プレーンテキストパスワードは絶対に保存しない
    
    sensitive_data:
      method: "AES-256-GCM"
      key_management: "AWS KMS / Azure Key Vault / Google Cloud KMS"
      target_data:
        - "個人識別情報（PII）"
        - "クレジットカード情報（該当する場合）"
        - "APIキー・シークレット"
      requirements:
        - [ ] PII（個人識別情報）はDB暗号化
        - [ ] 機密データはAES-256で暗号化
        - [ ] 暗号化キーは定期的にローテーション（90日ごと）
```

#### データハンドリング

```yaml
data_handling:
  logging:
    - [ ] ログにパスワード・トークンを記録しない
    - [ ] ログにクレジットカード番号を記録しない
    - [ ] 機密情報はマスキング（例: email → e***@example.com）
  
  error_messages:
    - [ ] エラーメッセージで内部情報を漏洩しない
    - [ ] スタックトレースは本番環境で非表示
    - [ ] 認証失敗時は"メールアドレスまたはパスワードが不正"と汎用的に返す
  
  sql_injection_prevention:
    - [ ] パラメータ化クエリ（Prepared Statements）使用
    - [ ] ORM（SQLAlchemy等）の使用を推奨
    - [ ] 生SQLの使用は最小限に
  
  xss_prevention:
    - [ ] ユーザー入力のサニタイゼーション
    - [ ] レスポンスのエスケープ処理
    - [ ] Content Security Policy（CSP）ヘッダー設定
```

---

### 📜 コンプライアンス / Compliance

```yaml
regulations:
  gdpr:
    applicable: "Yes / No"
    requirements:
      - [ ] ユーザー同意の取得と記録
      - [ ] データ削除権の実装（Right to be Forgotten）
      - [ ] データポータビリティの確保（データエクスポート機能）
      - [ ] プライバシーポリシーの明示
      - [ ] データ処理の合法性確保
  
  personal_information_protection_law_japan:
    applicable: "Yes / No"
    requirements:
      - [ ] 個人情報の利用目的明示
      - [ ] 第三者提供時の同意取得
      - [ ] 個人情報の安全管理措置
      - [ ] 漏洩時の報告義務対応
  
  pci_dss:
    applicable: "Yes / No"  # クレジットカード決済がある場合
    requirements:
      - [ ] カード情報の非保存（トークナイゼーション推奨）
      - [ ] 伝送中のカード情報暗号化
      - [ ] アクセス制御・監査ログ

notes: |
  - コンプライアンス要件は法務チームと確認
  - 対象地域・ビジネスモデルにより要件が異なる
```

---

## ⚡ パフォーマンス制約 / Performance Constraints

### 🚀 レスポンスタイム / Response Time

```yaml
targets:
  api_response:
    average: "< 200ms"
    p50: "< 150ms"
    p95: "< 500ms"
    p99: "< 1秒"
    timeout: "10秒"
  
  database_query:
    average: "< 50ms"
    p95: "< 150ms"
    p99: "< 200ms"
    timeout: "5秒"
  
  external_api_call:
    timeout: "5秒"
    retry_strategy: "Exponential backoff（最大3回）"

measurement:
  - [ ] 全APIエンドポイントでレスポンスタイム計測
  - [ ] APM（Application Performance Monitoring）ツール導入
  - [ ] CloudWatch / Prometheus / DataDog等でモニタリング

slo_violations:
  - [ ] SLO違反時のアラート設定
  - [ ] レスポンスタイム95パーセンタイル > 500ms でアラート
  - [ ] エラーレート > 1% でアラート
```

---

### 📈 スケーラビリティ / Scalability

```yaml
capacity_requirements:
  concurrent_users: "1,000ユーザー（初期）/ 10,000ユーザー（1年後目標）"
  requests_per_second: "500 RPS（ピーク時）"
  data_volume:
    initial: "100万レコード"
    growth_rate: "年間50%"
    projection: "3年後に約340万レコード"

scalability_design:
  - [ ] 水平スケーリング可能な設計（ステートレスアプリケーション）
  - [ ] ロードバランサー導入（ALB / NLB / Nginx）
  - [ ] データベースのリードレプリカ活用
  - [ ] キャッシュ層の導入（Redis / Memcached）
  - [ ] CDN活用（静的コンテンツ）

future_considerations:
  - [ ] データベースのシャーディング検討（データ量増加時）
  - [ ] マイクロサービス分割検討（複雑度増加時）
  - [ ] イベント駆動アーキテクチャ検討（非同期処理増加時）
```

---

### 🔢 リソース制限 / Resource Limits

```yaml
limits:
  request_body_size: "10 MB"
  response_body_size: "5 MB"
  connection_timeout: "30秒"
  read_timeout: "30秒"
  query_timeout: "10秒"
  file_upload_size: "50 MB"
  
  pagination:
    default_page_size: 20
    max_page_size: 100
    recommendation: "クライアント側で適切なページサイズを指定"

requirements:
  - [ ] リクエストサイズ超過時は413エラー（Payload Too Large）
  - [ ] タイムアウト時は504エラー（Gateway Timeout）
  - [ ] リソース制限の監視とアラート設定
  - [ ] 大容量データはストリーミング処理を検討
```

---

## 🛠️ 技術制約 / Technical Constraints

### 💻 プログラミング言語・フレームワーク

#### バックエンド

```yaml
backend:
  language: "Python 3.11+"
  framework: "FastAPI 0.100+"
  
  reasons:
    - "チームの技術スタック"
    - "非同期処理のサポート（asyncio）"
    - "OpenAPI自動生成"
    - "型ヒントによる安全性"
    - "高パフォーマンス（Starlette/Uvicorn）"
  
  constraints:
    - [ ] Python 3.11以上でのみ動作保証
    - [ ] 外部ライブラリは最小限に抑制（依存関係管理）
    - [ ] セキュリティアップデート対応必須

key_libraries:
  - "SQLAlchemy 2.0+（ORM）"
  - "Pydantic 2.0+（データバリデーション）"
  - "Alembic（DBマイグレーション）"
  - "python-jose（JWT処理）"
  - "bcrypt（パスワードハッシュ化）"
```

#### フロントエンド（該当する場合）

```yaml
frontend:
  language: "TypeScript 5.0+"
  framework: "React 18+ / Next.js 14+"
  
  reasons:
    - "型安全性"
    - "エコシステムの成熟度"
    - "チームの技術スタック"
  
  constraints:
    - [ ] TypeScript strictモード必須
    - [ ] ESLint/Prettier設定必須
```

---

### 🗄️ データベース

```yaml
database:
  primary:
    type: "PostgreSQL 15+"
    version: "15.4以上"
    reason: "トランザクション管理、JSONB、パフォーマンス"
  
  cache:
    type: "Redis 7+"
    version: "7.0以上"
    use_cases:
      - "トークンブラックリスト"
      - "レート制限カウンター"
      - "セッションストレージ"
      - "APIレスポンスキャッシュ"

constraints:
  postgresql:
    - [ ] トランザクション管理必須（BEGIN, COMMIT, ROLLBACK）
    - [ ] マイグレーションツール使用（Alembic）
    - [ ] インデックス設計の最適化
    - [ ] VACUUM / ANALYZE の定期実行
    - [ ] バックアップ戦略（日次フルバックアップ、WALアーカイブ）
  
  redis:
    - [ ] データの永続化設定（RDB / AOF）
    - [ ] メモリ上限設定（maxmemory-policy）
    - [ ] Sentinel / Cluster構成検討（高可用性要件時）
```

---

### ☁️ インフラ・デプロイメント

```yaml
infrastructure:
  cloud_provider: "AWS / Azure / GCP"
  selected_provider: "AWS"  # プロジェクトに応じて選択
  
  container:
    technology: "Docker"
    base_image: "python:3.11-slim"
    requirements:
      - [ ] Dockerfileのマルチステージビルド
      - [ ] イメージサイズ < 500MB
      - [ ] 脆弱性スキャン（Trivy / Snyk）
  
  orchestration:
    technology: "Kubernetes (EKS / AKS / GKE)"
    alternative: "AWS ECS / Fargate"
    requirements:
      - [ ] ヘルスチェックエンドポイント実装必須（/health）
      - [ ] Readiness / Liveness Probe設定
      - [ ] Graceful shutdown実装必須（SIGTERM処理）
      - [ ] リソース制限設定（CPU, Memory）

deployment_strategy:
  - "Blue-Green Deployment（推奨）"
  - "Rolling Update"
  - "Canary Deployment（段階的リリース時）"
```

---

## 🌐 環境制約 / Environment Constraints

### 🌍 ブラウザサポート（該当する場合）

```yaml
supported_browsers:
  desktop:
    - "Google Chrome: 最新2バージョン"
    - "Mozilla Firefox: 最新2バージョン"
    - "Microsoft Edge: 最新2バージョン"
    - "Safari: 最新2バージョン（macOS）"
  
  mobile:
    - "iOS Safari: iOS 15+"
    - "Android Chrome: Android 10+"

not_supported:
  - "Internet Explorer: 全バージョン（サポート対象外）"

requirements:
  - [ ] ポリフィル使用（古いブラウザ対応必要時）
  - [ ] レスポンシブデザイン（モバイル対応）
  - [ ] ブラウザ互換性テスト実施
```

---

### 🔌 ネットワーク

```yaml
network:
  protocols: "HTTP/2, WebSocket（リアルタイム通信時）"
  
  cors:
    allowed_origins:
      - "https://app.example.com"
      - "https://*.example.com"  # ワイルドカード（開発環境のみ）
    allowed_methods: ["GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"]
    allowed_headers: ["Content-Type", "Authorization", "X-Request-ID"]
    max_age: 3600
  
  ip_whitelist:
    admin_panel:
      - "社内IPレンジ: 192.168.0.0/16"
      - "特定パートナーIP: xxx.xxx.xxx.xxx"
    api:
      - "制限なし（レート制限で制御）"

requirements:
  - [ ] CORS設定の厳密な管理
  - [ ] レート制限の実装
  - [ ] DDoS対策（CloudFlare / AWS Shield）
```

---

## 📅 スケジュール制約 / Schedule Constraints

```yaml
deadlines:
  project_start: "YYYY-MM-DD"
  phase_2a_completion: "YYYY-MM-DD"
  phase_3_start: "YYYY-MM-DD"
  phase_3_completion: "YYYY-MM-DD"
  phase_5_completion: "YYYY-MM-DD"
  production_release: "YYYY-MM-DD"

milestones:
  - name: "Phase 2A完了（事前設計）"
    date: "YYYY-MM-DD"
    deliverables: ["ADR", "API契約書", "制約条件文書"]
  
  - name: "Phase 3完了（実装）"
    date: "YYYY-MM-DD"
    deliverables: ["実装コード", "ユニットテスト", "統合テスト"]
  
  - name: "Phase 5完了（実装ベース設計）"
    date: "YYYY-MM-DD"
    deliverables: ["設計書", "API仕様書", "アーキテクチャ図"]
  
  - name: "本番リリース"
    date: "YYYY-MM-DD"
    deliverables: ["本番環境デプロイ", "運用ドキュメント"]

constraints:
  - [ ] リリース日は固定（ビジネス要件）
  - [ ] Phase 3実装期間は最大4週間
  - [ ] 各Phaseの遅延時はスコープ調整を検討
```

---

## 💰 予算・リソース制約 / Budget & Resource Constraints

```yaml
budget:
  development_hours: "XXX人時"
  cloud_cost_monthly: "$XXX/月"
  third_party_services: "$XXX/月"
  
  breakdown:
    - "開発: XXX人時"
    - "QA: XXX人時"
    - "インフラ構築: XXX人時"
    - "AWS/Azure/GCP: $XXX/月"
    - "監視ツール（DataDog等）: $XXX/月"

team:
  backend_engineers: "X名"
  frontend_engineers: "X名"
  qa_engineers: "X名"
  devops_engineers: "X名"
  tech_lead: "1名"

constraints:
  - [ ] 追加リソースの承認プロセスが必要（承認者: [名前]）
  - [ ] クラウドコスト超過時のアラート設定
  - [ ] コスト最適化の定期レビュー（月次）
```

---

## 🔄 運用制約 / Operations Constraints

### 🛠️ メンテナンス

```yaml
maintenance_window:
  frequency: "毎週日曜日"
  time: "2:00-4:00 (JST)"
  duration: "最大2時間"
  notification_period: "48時間前"

requirements:
  - [ ] メンテナンス通知を48時間前に実施
  - [ ] メンテナンスページの表示（503 Service Unavailable）
  - [ ] ロールバック手順の準備
  - [ ] メンテナンス後の動作確認チェックリスト
```

---

### 📊 監視・ログ / Monitoring & Logging

```yaml
monitoring:
  uptime_requirement: "99.9%（SLA）"
  alert_channels: "Slack, Email, PagerDuty"
  
  metrics:
    infrastructure:
      - [ ] CPU使用率（閾値: > 80%）
      - [ ] メモリ使用率（閾値: > 85%）
      - [ ] ディスクI/O（閾値: > 80%）
      - [ ] ネットワーク帯域
    
    application:
      - [ ] APIレスポンスタイム（p95, p99）
      - [ ] エラーレート（閾値: > 1%）
      - [ ] リクエストスループット（RPS）
      - [ ] アクティブユーザー数
    
    business:
      - [ ] ユーザー登録数（日次）
      - [ ] ログイン成功/失敗率
      - [ ] API使用状況（エンドポイント別）

logging:
  retention: "30日間（本番）/ 7日間（開発・ステージング）"
  level:
    production: "INFO"
    staging: "DEBUG"
    development: "DEBUG"
  
  format: "構造化ログ（JSON形式）"
  
  required_fields:
    - "timestamp"
    - "level"
    - "logger_name"
    - "message"
    - "request_id"
    - "user_id"
    - "endpoint"
    - "status_code"
    - "response_time"

requirements:
  - [ ] 構造化ログ（JSON形式）の実装
  - [ ] 全リクエストにトレースID付与（X-Request-ID）
  - [ ] 機密情報のマスキング（パスワード、トークン、クレジットカード）
  - [ ] ログ集約ツール導入（CloudWatch Logs / ELK Stack / Splunk）
  - [ ] アラート設定（閾値超過時）
```

---

### 🔙 バックアップ・災害復旧

```yaml
backup:
  database:
    frequency: "日次フルバックアップ + 継続的WALアーカイブ"
    retention: "30日間"
    storage: "S3 / Azure Blob Storage / GCS"
    encryption: "AES-256"
  
  configuration:
    frequency: "変更時 + 週次"
    storage: "Git Repository + S3"
  
  test_restore:
    frequency: "月次"
    requirements:
      - [ ] バックアップからの復元テスト実施
      - [ ] 復元時間の計測（RTO達成確認）
      - [ ] データ整合性の確認

disaster_recovery:
  rpo: "Recovery Point Objective: 1時間"
  rto: "Recovery Time Objective: 4時間"
  
  strategy:
    - "マルチAZ構成（AWS / Azure）"
    - "自動フェイルオーバー（データベース）"
    - "災害復旧手順書の整備"
    - "定期的なDRテスト実施（四半期ごと）"
```

---

## ⚠️ 既知の制限事項 / Known Limitations

```yaml
known_limitations:
  - limitation: "初期リリースではモバイルアプリ非対応"
    impact: "モバイルユーザーはWebブラウザ経由のみ"
    workaround: "レスポンシブWebデザインで対応"
    future_plan: "Phase 2でiOS/Androidアプリ開発"
    timeline: "リリース後6ヶ月"
  
  - limitation: "多言語対応は英語・日本語のみ"
    impact: "他言語圏ユーザーは利用不可"
    workaround: "UIは多言語対応設計（i18n対応）"
    future_plan: "ユーザー需要に応じて言語追加"
    timeline: "需要調査後3ヶ月"
  
  - limitation: "同時ログインセッション数制限なし"
    impact: "セキュリティリスク（アカウント共有可能）"
    workaround: "現バージョンでは制限なし"
    future_plan: "セッション管理機能追加（最大5デバイス）"
    timeline: "Phase 3で実装検討"

notes: |
  - 制限事項は優先度・リソースに応じて将来的に解消
  - ビジネス影響を評価し、スコープ決定
```

---

## ✅ レビューチェックリスト / Review Checklist

### セキュリティ

```markdown
- [ ] 認証・認可メカニズムが適切に設計されている
- [ ] データ保護要件（暗号化、ハッシュ化）が定義されている
- [ ] コンプライアンス要件が満たされている
- [ ] レート制限が定義されている
- [ ] ログに機密情報が含まれない設計になっている
```

### パフォーマンス

```markdown
- [ ] レスポンスタイム目標が現実的である
- [ ] スケーラビリティ要件が明確である
- [ ] リソース制限が適切に設定されている
- [ ] パフォーマンステスト計画が検討されている
```

### 技術

```markdown
- [ ] 技術スタックがチームのスキルセットと一致している
- [ ] 技術制約が実装可能である
- [ ] インフラ要件が明確である
- [ ] 外部依存が最小限に抑えられている
```

### 運用

```markdown
- [ ] 監視・ログ要件が実現可能である
- [ ] メンテナンス計画が合意されている
- [ ] SLA/SLOが明確に定義されている
- [ ] バックアップ・災害復旧計画が整備されている
```

### ビジネス

```markdown
- [ ] スケジュール制約が現実的である
- [ ] 予算制約が明確である
- [ ] 既知の制限事項が文書化されている
- [ ] ステークホルダーの承認を得ている
```

---

## 📚 関連ドキュメント / Related Documents

### Phase 2A（事前設計）

- **API契約書**: [api-contract.md](api-contract.md)
- **ADR**: [ADR-001: 技術スタック選定](../decisions/adr-001-tech-stack.md)
- **ADR**: [ADR-002: 認証メカニズム](../decisions/adr-002-auth-mechanism.md)

### Phase 5（実装ベース設計）- 実装後に作成

- **設計書**: `design-document.md`
- **セキュリティ評価書**: `security/security-assessment.md`
- **パフォーマンステスト結果**: `performance/performance-test-report.md`

---

## 📅 変更履歴 / Change History

| バージョン | 日付 | 変更内容 | 作成者 |
|----------|------|---------|--------|
| 1.0.0 | YYYY-MM-DD | 初版作成 | [名前] |
| 1.1.0 | YYYY-MM-DD | [変更内容] | [名前] |

---

## 📌 補足・メモ / Notes

### Phase 2A での留意点

```yaml
important_notes:
  - "実装の方向性を制約する重要文書"
  - "セキュリティ・パフォーマンスを Phase 3開始前に明確化"
  - "実装中に制約を守れない場合は、このドキュメントを更新"
  - "Phase 5で実装結果との差異を記録（as-built notes）"

escalation:
  - "制約を満たせない場合は、テックリードに速やかに報告"
  - "ビジネス要件との衝突がある場合は、プロジェクトマネージャーに相談"
```

### 次のステップ

```markdown
1. [ ] この制約条件文書のレビュー実施
2. [ ] セキュリティチーム、インフラチーム、QAとの合意取得
3. [ ] テックリードの承認取得
4. [ ] API契約書との整合性確認
5. [ ] Phase 3（実装）開始時に開発チーム全員に周知
6. [ ] Phase 5で実装結果との差異を検証・記録
```

---

**この制約条件文書は Phase 2A（事前設計）で作成し、Phase 3（実装）を通して遵守すべき「守るべきルール」を定義します。実装完了後、Phase 5（実装ベース設計）で実際の実装との差異を検証・記録してください。**
