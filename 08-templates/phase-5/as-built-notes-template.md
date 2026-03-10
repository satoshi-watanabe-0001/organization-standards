---
template_name: "As-Built Notes Template"
version: "1.0.0"
purpose: "Phase 5最終化時の実装差分記録テンプレート"
target_phase: "Phase 5 (Detailed Design) - 最終化ブロック"
category: "implementation-documentation"
related_templates:
  - "design-document-template.md"
  - "data-model-template.md"
  - "milestone-definition-template.md"
created_date: "2025-11-20"
---

# As-Built記録 / As-Built Notes

## 📋 メタデータ

| 項目 | 内容 |
|-----|------|
| プロジェクト名 | [プロジェクト名] |
| サービス名 | [マイクロサービス名] |
| 実装期間 | YYYY-MM-DD 〜 YYYY-MM-DD |
| 記録日 | YYYY-MM-DD |
| 記録者 | [名前] |
| Phase | Phase 5 (Detailed Design) - 最終化 |

---

## 🎯 As-Built記録の目的

### このドキュメントの役割

**As-Built（竣工図）**とは、実際に構築された状態を記録するドキュメントです。

```yaml
purpose:
  what: "設計と実装の差異を記録"
  why: "将来のメンテナンス・拡張時の参考情報"
  who: "新規参画者、運用チーム、将来の開発者"

key_questions:
  - "なぜ当初の設計から変更したのか?"
  - "どのような実装上の制約があったのか?"
  - "将来、どのように改善すべきか?"
```

---

## 🔄 設計との差異サマリー

### 変更概要

| カテゴリ | 変更項目数 | 影響度 | 承認者 |
|---------|----------|--------|--------|
| **アーキテクチャ** | [数] | 高/中/低 | [名前] |
| **API仕様** | [数] | 高/中/低 | [名前] |
| **データモデル** | [数] | 高/中/低 | [名前] |
| **セキュリティ** | [数] | 高/中/低 | [名前] |
| **パフォーマンス** | [数] | 高/中/低 | [名前] |

---

## 📐 アーキテクチャの変更

### 変更1: [変更項目名]

#### Phase 2A（事前設計）での計画

```yaml
original_design:
  approach: "[当初の設計アプローチ]"
  reason: "[選定理由]"
  
  example: |
    "マイクロサービス間の通信にRESTful APIを使用する計画だった。
    各サービスは同期的にHTTP/JSONで通信する設計。"
```

---

#### 実装での変更内容

```yaml
actual_implementation:
  approach: "[実際の実装アプローチ]"
  change_type: "追加 / 変更 / 削除 / 代替"
  
  example: |
    "一部の通信をイベント駆動（Message Queue経由）に変更。
    特に非同期処理が必要な箇所はRabbitMQを使用。"
```

---

#### 変更理由

```yaml
reasons:
  technical:
    - reason: "[技術的理由]"
      example: "同期通信ではタイムアウトが頻発した"
  
  performance:
    - reason: "[パフォーマンス理由]"
      example: "レスポンスタイムが要件（200ms以下）を満たせなかった"
  
  business:
    - reason: "[ビジネス要件の変更]"
      example: "ステークホルダーから非同期処理の要望があった"
  
  constraint:
    - reason: "[実装上の制約]"
      example: "使用予定のライブラリがストリーミングをサポートしていなかった"
```

**詳細説明**:
```
[変更に至った具体的な経緯、パフォーマンステスト結果、
 代替案の検討内容などを記述]

例:
"Phase 3実装中に統合テストを実施した結果、同期通信では
平均レスポンスタイムが350msとなり、要件の200msを大幅に超過した。
複数の代替案を検討し、以下の理由でMessage Queue採用を決定:
- 非同期処理により平均レスポンスタイム85msを達成
- RabbitMQの実績とチームの経験値
- 障害発生時のリトライ機構が標準装備"
```

---

#### 影響範囲

```yaml
impact:
  affected_components:
    - "[影響を受けたコンポーネント1]"
    - "[影響を受けたコンポーネント2]"
  
  affected_documents:
    - "アーキテクチャ図: [ファイル名]"
    - "API仕様書: [ファイル名]"
  
  additional_work:
    - "RabbitMQ環境のセットアップ"
    - "メッセージスキーマの定義"
    - "リトライ処理の実装"
  
  timeline_impact: "+2日（実装期間延長）"
  cost_impact: "+20時間（追加開発工数）"
```

---

#### 今後の対応

```yaml
future_actions:
  immediate:
    - action: "設計書・アーキテクチャ図の更新"
      status: "完了"
      assignee: "[名前]"
  
  short_term:
    - action: "RabbitMQの監視ダッシュボード構築"
      status: "予定"
      timeline: "Phase 7"
  
  long_term:
    - action: "他のマイクロサービスへのメッセージング適用検討"
      status: "検討中"
      timeline: "Q2 2026"
```

---

### 変更2: [次の変更項目]

[同様のフォーマットで記述]

---

## 🔌 API仕様の変更

### 変更1: [エンドポイント名] の変更

#### 当初の設計（Phase 2A）

```yaml
endpoint: POST /api/v1/auth/login
request_body:
  email: string
  password: string

response:
  access_token: string
  expires_in: number
```

---

#### 実装での変更

```yaml
endpoint: POST /api/v1/auth/login
request_body:
  email: string
  password: string
  device_id: string (NEW)  # デバイストラッキング用

response:
  access_token: string
  refresh_token: string (NEW)  # リフレッシュトークン追加
  expires_in: number
  user:  # ユーザー情報を追加
    id: string
    email: string
    full_name: string
```

---

#### 変更理由

```yaml
reasons:
  security:
    - "リフレッシュトークン機構が必須と判明（Phase 3実装中）"
    - "デバイストラッキングによる不正アクセス検知の実装"
  
  user_experience:
    - "フロントエンドチームから初回ログイン時のユーザー情報取得要望"
    - "追加のAPI呼び出しを削減してUX向上"
```

---

#### 影響範囲

```yaml
affected_parties:
  frontend_team: "ログイン処理の修正（2時間）"
  mobile_team: "device_id取得処理の追加（1時間）"
  documentation: "API仕様書の更新（完了）"

backward_compatibility: "あり（device_idはオプショナル、旧クライアントも動作）"
```

---

## 🗄️ データモデルの変更

### 変更1: [テーブル名] の変更

#### 当初の設計（Phase 2A）

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

---

#### 実装での変更

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,  -- NEW
    is_active BOOLEAN DEFAULT true,   -- NEW
    is_verified BOOLEAN DEFAULT false, -- NEW
    verification_token VARCHAR(255),  -- NEW
    last_login_at TIMESTAMP,          -- NEW
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL     -- NEW
);
```

---

#### 変更理由

```yaml
reasons:
  business_requirement:
    - "メール認証機能が必須と判明（Phase 1で見落とし）"
    - "アカウント停止機能の要件追加"
  
  operational:
    - "最終ログイン日時のトラッキング要望（運用チーム）"
    - "updated_at による変更追跡の必要性"
```

---

#### マイグレーション対応

```yaml
migration:
  version: "V002"
  script: "V002__add_user_fields.sql"
  
  backward_compatibility:
    strategy: "デフォルト値を設定して既存データに影響なし"
    existing_users: "is_active=true, is_verified=true で初期化"
  
  data_migration:
    required: "いいえ（デフォルト値で対応）"
    estimated_time: "即座（レコード数が少ない）"
```

---

## 🔐 セキュリティの変更

### 変更1: [セキュリティ項目]

#### 当初の設計

```yaml
authentication:
  method: "JWT (HS256)"
  token_lifetime: "24時間"
  storage: "localStorage"
```

---

#### 実装での変更

```yaml
authentication:
  method: "JWT (RS256)"  # 変更: 非対称暗号化
  token_lifetime: "15分"  # 変更: 短縮
  refresh_token_lifetime: "7日"  # 追加
  storage: "httpOnly Cookie"  # 変更: よりセキュア
```

---

#### 変更理由

```yaml
reasons:
  security_audit:
    finding: "セキュリティレビューで脆弱性を指摘された"
    details:
      - "HS256は秘密鍵が漏洩すると全トークンが危険"
      - "localStorageはXSS攻撃に脆弱"
      - "24時間のトークン有効期限は長すぎる"
  
  best_practice:
    - "RS256（非対称暗号）がベストプラクティス"
    - "httpOnly CookieでXSS攻撃を軽減"
    - "短いトークン有効期限 + リフレッシュトークン"
```

---

#### セキュリティテスト結果

```yaml
security_testing:
  xss_test: "Pass（httpOnly Cookieにより保護）"
  csrf_test: "Pass（CSRFトークン実装済み）"
  token_expiration_test: "Pass（15分後に無効化確認）"
  
  penetration_test:
    status: "実施済み"
    findings: "重大な脆弱性なし"
    report: "security-test-report-2025-11.pdf"
```

---

## ⚡ パフォーマンス最適化

### 変更1: [最適化項目]

#### 当初の設計

```yaml
database_query:
  approach: "ORMの自動生成クエリを使用"
  expected_performance: "100ms以内"
```

---

#### 実装での変更

```yaml
database_query:
  approach: "N+1問題が発覚、最適化クエリを手動実装"
  actual_performance: "15ms（6.7倍高速化）"
  
  optimization:
    - "JOINクエリによる一括取得"
    - "適切なインデックス追加"
    - "クエリ結果のキャッシング（Redis, 5分TTL）"
```

---

#### パフォーマンステスト結果

```yaml
load_test:
  tool: "Apache JMeter"
  scenarios:
    - name: "ユーザーログイン"
      concurrent_users: 1000
      duration: "10分"
      
      results:
        average_response_time: "85ms"
        95_percentile: "150ms"
        99_percentile: "280ms"
        error_rate: "0.01%"
        throughput: "2500 req/sec"
  
  conclusion: "要件（200ms以下）を満たす"
```

---

## 🧪 テストの変更

### 変更1: [テスト項目]

#### 当初の計画

```yaml
test_coverage:
  target: "80%"
  approach: "ユニットテスト中心"
```

---

#### 実装での変更

```yaml
test_coverage:
  actual: "87%"
  approach:
    unit_test: "85% カバレッジ"
    integration_test: "主要フロー100%"
    e2e_test: "クリティカルパス3シナリオ"
  
  additional_testing:
    - "セキュリティテスト（OWASP Top 10）"
    - "負荷テスト（1000同時接続）"
    - "障害復旧テスト（DB接続断）"
```

---

#### 変更理由

```yaml
reasons:
  quality_requirement:
    - "QAチームからより高いカバレッジ要求"
    - "本番環境での障害リスク低減"
  
  lessons_learned:
    - "過去プロジェクトでの本番障害の教訓"
    - "統合テストの重要性を再認識"
```

---

## 📦 依存ライブラリの変更

### 変更1: [ライブラリ名]

#### 当初の選定

```yaml
library:
  name: "[ライブラリA]"
  version: "X.Y.Z"
  reason: "[選定理由]"
```

---

#### 実装での変更

```yaml
library:
  name: "[ライブラリB]"
  version: "A.B.C"
  reason: "[変更理由]"
  
  migration_effort: "4時間"
```

---

#### 変更理由

```yaml
reasons:
  technical_issue:
    - "[ライブラリAの問題点]"
    - "[ライブラリBの利点]"
  
  example: |
    "当初選定したORM（ライブラリA）では複雑なJOINクエリの
    パフォーマンスが著しく低かった（500ms超）。
    代替のライブラリBに変更した結果、同じクエリが15msで完了。"
```

---

## 🚨 未解決の技術的負債

### 負債1: [項目名]

#### 概要

```yaml
debt_type: "設計 / コード品質 / パフォーマンス / セキュリティ / テスト"

description: |
  [技術的負債の詳細]
  
  例:
  "ユーザーセッション管理をDBに保存しているが、高負荷時に
  DB接続が逼迫する可能性がある。本来はRedisなどのインメモリ
  ストレージを使用すべき。"
```

---

#### 影響度

```yaml
impact:
  severity: "高 / 中 / 低"
  affected_area: "[影響範囲]"
  
  risk:
    - "パフォーマンス劣化のリスク（高負荷時）"
    - "スケーラビリティの制限"
  
  business_impact:
    - "ユーザー数が10万人を超えると問題が顕在化する可能性"
```

---

#### 対応計画

```yaml
resolution:
  priority: "P1 (最高) / P2 (高) / P3 (中) / P4 (低)"
  
  plan:
    - phase: "Phase 7（運用開始後）"
      action: "Redisの導入検討"
      estimated_effort: "3日"
    
    - phase: "Q1 2026"
      action: "セッションストレージの移行実装"
      estimated_effort: "1週間"
  
  workaround: |
    "当面は接続プールのサイズを拡大して対応。
    ユーザー数が5万人を超えたら即座にRedis導入を開始。"
```

---

### 負債2: [次の項目]

[同様のフォーマットで記述]

---

## 💡 学んだこと（Lessons Learned）

### 技術的な学び

```yaml
technical_lessons:
  - lesson: "[学んだこと1]"
    context: "[どのような状況で]"
    takeaway: "[今後に活かすこと]"
    
    example:
      lesson: "ORMの自動生成クエリは常に最適ではない"
      context: "N+1問題が発生し、パフォーマンス要件を満たせなかった"
      takeaway: "初期からクエリのパフォーマンス測定を行う"
```

---

### プロセス的な学び

```yaml
process_lessons:
  - lesson: "[学んだこと1]"
    context: "[どのような状況で]"
    takeaway: "[今後に活かすこと]"
    
    example:
      lesson: "Phase 2Aでのセキュリティレビューが不十分だった"
      context: "Phase 3実装中にセキュリティ問題が発覚し、大幅な手戻り"
      takeaway: "Phase 2Aでセキュリティエキスパートのレビューを必須化"
```

---

## 📊 工数・スケジュールの差異

### 工数実績

| フェーズ | 計画工数 | 実績工数 | 差異 | 差異理由 |
|---------|---------|---------|------|---------|
| Phase 2A | 2日 | 1.5日 | -0.5日 | テンプレート活用により効率化 |
| Phase 3 | 10日 | 12日 | +2日 | Message Queue導入による追加実装 |
| Phase 4 | 3日 | 3.5日 | +0.5日 | セキュリティテスト追加 |
| Phase 5 | 3日 | 2日 | -1日 | AI活用により効率化 |
| **合計** | **18日** | **19日** | **+1日** | - |

---

### スケジュール実績

```yaml
timeline:
  planned_start: "2025-11-01"
  planned_end: "2025-11-22"
  actual_start: "2025-11-01"
  actual_end: "2025-11-25"
  
  delay: "+3日"
  delay_reason: "Message Queue導入の追加実装とテスト"
  
  impact: "許容範囲内（バッファ期間で吸収）"
```

---

## ✅ 完成度チェック

### 成果物の完成度

| 成果物 | 計画範囲 | 実装範囲 | 完成度 | 備考 |
|--------|---------|---------|--------|------|
| API実装 | 10エンドポイント | 12エンドポイント | 120% | 追加要望対応 |
| データモデル | 4テーブル | 6テーブル | 150% | RBAC実装 |
| テストコード | 80%カバレッジ | 87%カバレッジ | 109% | 品質強化 |
| ドキュメント | Phase 5必須成果物 | 全成果物 | 100% | - |

---

### 品質指標

```yaml
quality_metrics:
  code_coverage: "87%（目標: 80%）"
  security_scan: "0 高リスク脆弱性"
  performance: "平均85ms（目標: 200ms以下）"
  bug_count: "2件（非クリティカル）"
  
  overall_assessment: "目標を上回る品質を達成"
```

---

## 📝 今後の推奨事項

### 次のプロジェクトへの提言

```yaml
recommendations:
  process:
    - "Phase 2Aでのセキュリティレビューを必須化"
    - "Phase 3開始時点でパフォーマンステストを実施"
    - "週次で設計との差異をトラッキング"
  
  technical:
    - "初期からRedisなどのキャッシュ層を検討"
    - "Message Queueの採用基準を明確化"
    - "ORMの性能特性を事前に検証"
  
  documentation:
    - "As-Built記録をPhase 5の標準成果物に"
    - "マイルストーンごとに差異を記録"
```

---

## 📚 関連ドキュメント

### 参照ドキュメント

```yaml
references:
  design:
    - "Phase 2A API契約書: api-contract-v1.0.md"
    - "Phase 2A 制約条件文書: constraints-v1.0.md"
  
  implementation:
    - "Phase 5 設計書: design-document-final.md"
    - "Phase 5 API仕様書: api-specification-final.md"
  
  testing:
    - "セキュリティテストレポート: security-test-report.pdf"
    - "負荷テストレポート: load-test-report.pdf"
```

---

## ✍️ 承認記録

```yaml
approval:
  tech_lead:
    name: "[名前]"
    date: "YYYY-MM-DD"
    status: "承認"
    comments: "[コメント]"
  
  architect:
    name: "[名前]"
    date: "YYYY-MM-DD"
    status: "承認"
    comments: "[コメント]"
  
  project_manager:
    name: "[名前]"
    date: "YYYY-MM-DD"
    status: "承認"
    comments: "[コメント]"
```

---

**このAs-Built記録により、実装の真の姿が将来の開発者に伝わります。**
