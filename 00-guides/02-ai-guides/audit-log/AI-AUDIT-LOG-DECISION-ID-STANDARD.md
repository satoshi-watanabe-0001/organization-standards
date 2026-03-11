# AI監査ログ decision_ID 標準化ガイド

---
document_type: naming_standard
target_audience:
  - AIエージェント（Devin, Cursor等）
  - 開発チーム
  - プロジェクトマネージャー
priority: high
scope: decision_id_standardization
version: 1.0
last_updated: 2026-03-10
related_documents:
  - AICQ_AUDIT_LOG_SCHEMA.md
  - AI-AUDIT-LOG-SCENARIO-COOKBOOK.md
  - AI-WORKLOG-ENFORCEMENT-GUIDE.md
---

## 📋 1. decision_IDの重要性

### 1.1 標準化の必要性

**decision_id**は、AIエージェントが行った判断を一意に識別し、トレースするための**最重要キー**です。適切な標準化により以下が実現されます：

#### ✅ トレーサビリティの確保
- 要件から実装まで判断の連鎖を追跡可能
- 問題発生時の根本原因分析が迅速化
- 設計変更の影響範囲を正確に把握

#### ✅ メトリクス算出の精度向上
- **A1-3: 推論・設計・説明の整合維持率**の正確な計算
- decision_idによる判断の整合性検証
- final.decision_mapとの自動照合

#### ✅ チーム間での一貫性
- 複数AIエージェント間での統一的な判断管理
- レビュー時の迅速な判断内容理解
- プロジェクト横断での知見共有

#### ✅ 自動化・ツール連携
- CI/CDパイプラインでの判断履歴取得
- ダッシュボードでの判断分析
- 自動レポート生成

### 1.2 標準化なしの問題例

**❌ 悪い例：**
```json
{
  "decisions": [
    {"decision_id": "dec1", "description": "Reactを使う"},
    {"decision_id": "choosing_db", "description": "PostgreSQL選定"},
    {"decision_id": "AUTH-decision", "description": "JWT認証"},
    {"decision_id": "test_strategy", "description": "Jest使用"}
  ]
}
```

**問題点：**
- 命名規則が不統一（dec1, choosing_db, AUTH-decision, test_strategy）
- カテゴリが不明確
- 連番規則がない
- 関連性が追跡困難

**✅ 良い例：**
```json
{
  "decisions": [
    {"decision_id": "ARCH-001", "description": "フロントエンド技術選定: React"},
    {"decision_id": "DATA-001", "description": "データベース選定: PostgreSQL"},
    {"decision_id": "SECURITY-001", "description": "認証方式選定: JWT"},
    {"decision_id": "TEST-001", "description": "テストフレームワーク選定: Jest"}
  ]
}
```

---

## 🏷️ 2. 標準カテゴリ定義（13カテゴリ）

### 2.1 ARCH（アーキテクチャ）

**適用範囲：**
- システム全体のアーキテクチャパターン
- 技術スタック選定
- プロジェクト構造決定
- クロスカッティングな技術判断

**使用例：**
```json
{
  "decision_id": "ARCH-001",
  "description": "マイクロサービスアーキテクチャ採用",
  "rationale": "スケーラビリティと独立デプロイの要件を満たすため"
}
```

**避けるべき誤用：**
- 個別機能の実装判断 → IMPL使用
- 特定のUIコンポーネント設計 → UI使用

### 2.2 DESIGN（詳細設計）

**適用範囲：**
- クラス設計・モジュール設計
- ドメインモデル設計
- コンポーネント間のインターフェース
- 内部アルゴリズム選択

**使用例：**
```json
{
  "decision_id": "DESIGN-001",
  "description": "ユーザー認証のドメインモデル設計",
  "rationale": "DDD原則に基づく凝集度の高い設計"
}
```

### 2.3 IMPL（実装）

**適用範囲：**
- 具体的な実装方式
- ライブラリ・パッケージ選定
- コーディング規約
- 実装技法の選択

**使用例：**
```json
{
  "decision_id": "IMPL-001",
  "description": "非同期処理実装: async/await パターン",
  "rationale": "可読性とエラーハンドリングの優位性"
}
```

### 2.4 TEST（テスト戦略）

**適用範囲：**
- テスト戦略・方針
- テストツール選定
- カバレッジ目標設定
- テストデータ管理

**使用例：**
```json
{
  "decision_id": "TEST-001",
  "description": "カバレッジ目標: 80%以上",
  "rationale": "品質とメンテナンス効率のバランス"
}
```

### 2.5 SECURITY（セキュリティ）

**適用範囲：**
- 認証・認可方式
- 暗号化・ハッシュ化
- セキュリティ監査対応
- 脆弱性対策

**使用例：**
```json
{
  "decision_id": "SECURITY-001",
  "description": "パスワードハッシュ: bcrypt使用",
  "rationale": "計算コスト調整可能、耐ブルートフォース性"
}
```

### 2.6 PERF（パフォーマンス）

**適用範囲：**
- パフォーマンス最適化手法
- キャッシュ戦略
- リソース効率化
- ボトルネック解決

**使用例：**
```json
{
  "decision_id": "PERF-001",
  "description": "データベースクエリ最適化: インデックス追加",
  "rationale": "JOIN処理の高速化（1.2s → 0.3s）"
}
```

### 2.7 DEPLOY（デプロイ）

**適用範囲：**
- デプロイメント戦略
- インフラ設定
- リリース計画
- ロールバック戦略

**使用例：**
```json
{
  "decision_id": "DEPLOY-001",
  "description": "ブルーグリーンデプロイメント採用",
  "rationale": "ダウンタイムゼロと即座のロールバック要件"
}
```

### 2.8 OPS（運用）

**適用範囲：**
- 監視・ログ戦略
- アラート設定
- 障害対応手順
- メンテナンス計画

**使用例：**
```json
{
  "decision_id": "OPS-001",
  "description": "監視ツール: Prometheus + Grafana",
  "rationale": "メトリクス収集と可視化の統合"
}
```

### 2.9 DOC（ドキュメント）

**適用範囲：**
- ドキュメント戦略
- API仕様書形式
- コメント規約
- 知識管理

**使用例：**
```json
{
  "decision_id": "DOC-001",
  "description": "API仕様書: OpenAPI 3.0形式",
  "rationale": "ツールエコシステムの充実、自動検証可能"
}
```

### 2.10 REFACTOR（リファクタリング）

**適用範囲：**
- リファクタリング戦略
- 技術的負債解消
- コード品質改善
- 構造改善

**使用例：**
```json
{
  "decision_id": "REFACTOR-001",
  "description": "レガシーコード段階的リファクタリング",
  "rationale": "ストラングラーパターンで安全な移行"
}
```

### 2.11 DATA（データ設計）

**適用範囲：**
- データベース設計
- データモデル設計
- データ移行戦略
- データ保持・削除ポリシー

**使用例：**
```json
{
  "decision_id": "DATA-001",
  "description": "ユーザーデータ論理削除採用",
  "rationale": "GDPR対応とデータ復旧要件の両立"
}
```

### 2.12 API（API設計）

**適用範囲：**
- REST/GraphQL設計
- APIバージョニング
- エラーハンドリング
- 外部API連携

**使用例：**
```json
{
  "decision_id": "API-001",
  "description": "REST API バージョニング: URL パス方式",
  "rationale": "明示的で理解しやすい（/v1/, /v2/）"
}
```

### 2.13 UI（UI/UX設計）

**適用範囲：**
- ユーザーインターフェース設計
- ユーザビリティ方針
- アクセシビリティ対応
- デザインシステム

**使用例：**
```json
{
  "decision_id": "UI-001",
  "description": "デザインシステム: Material-UI採用",
  "rationale": "一貫性あるUI、開発効率向上"
}
```

---

## 🔢 3. 採番ルール（4パターン）

### 3.1 基本パターン: {CATEGORY}-{連番3桁}

**形式：** `ARCH-001`, `DESIGN-042`, `IMPL-123`

**ルール：**
- 連番は001から開始
- 3桁ゼロパディング必須
- カテゴリ内で通し番号

**使用例：**
```json
{
  "decisions": [
    {"decision_id": "ARCH-001", "description": "技術スタック選定"},
    {"decision_id": "ARCH-002", "description": "デプロイメント戦略"},
    {"decision_id": "DESIGN-001", "description": "ドメインモデル設計"}
  ]
}
```

### 3.2 修正版パターン: {CATEGORY}-{連番}-rev{修正番号}

**形式：** `DESIGN-042-rev1`, `IMPL-010-rev2`

**使用ケース：**
- 既存判断の修正・改良
- レビュー指摘による変更
- 要件変更による判断更新

**使用例：**
```json
{
  "decisions": [
    {
      "decision_id": "SECURITY-001",
      "description": "認証方式: Session Cookie",
      "status": "superseded_by_rev1"
    },
    {
      "decision_id": "SECURITY-001-rev1",
      "description": "認証方式: JWT（RESTful API要件により変更）",
      "supersedes": "SECURITY-001",
      "rationale": "ステートレス要件の追加により変更"
    }
  ]
}
```

### 3.3 関連判断パターン: {CATEGORY}-{連番}-{サブ連番}

**形式：** `IMPL-010-1`, `IMPL-010-2`, `TEST-005-A`

**使用ケース：**
- 大きな判断の詳細分割
- 段階的な判断プロセス
- 同じ文脈での複数選択

**使用例：**
```json
{
  "decisions": [
    {
      "decision_id": "IMPL-010",
      "description": "支払い処理システム実装",
      "rationale": "メイン判断"
    },
    {
      "decision_id": "IMPL-010-1",
      "description": "クレジットカード処理: Stripe API",
      "parent_decision": "IMPL-010"
    },
    {
      "decision_id": "IMPL-010-2", 
      "description": "銀行振込処理: 独自実装",
      "parent_decision": "IMPL-010"
    }
  ]
}
```

### 3.4 派生判断パターン: {CATEGORY}-{連番}-derived

**形式：** `ARCH-001-derived`, `DESIGN-020-derived`

**使用ケース：**
- 他の判断から導出された判断
- 制約による必然的選択
- 依存関係のある判断

**使用例：**
```json
{
  "decisions": [
    {
      "decision_id": "ARCH-001",
      "description": "マイクロサービスアーキテクチャ採用"
    },
    {
      "decision_id": "DEPLOY-001-derived",
      "description": "Docker コンテナ化必須（マイクロサービスのため）",
      "derived_from": "ARCH-001",
      "rationale": "アーキテクチャ選択の必然的結果"
    }
  ]
}
```

---

## 🔄 4. ライフサイクル管理

### 4.1 判断の作成

**初期状態：**
```json
{
  "decision_id": "IMPL-025",
  "description": "ログライブラリ選定: Winston",
  "status": "active",
  "created_at": "2026-03-10T10:00:00Z",
  "rationale": "構造化ログと複数出力先サポート"
}
```

### 4.2 判断の修正・撤回

**修正時：**
```json
{
  "decision_id": "IMPL-025-rev1",
  "description": "ログライブラリ選定: Pino（性能要件追加のため）",
  "status": "active",
  "supersedes": "IMPL-025",
  "created_at": "2026-03-10T14:30:00Z",
  "rationale": "低レイテンシ要件によりWinstonからPinoに変更"
}
```

**元判断の更新：**
```json
{
  "decision_id": "IMPL-025",
  "status": "superseded",
  "superseded_by": "IMPL-025-rev1",
  "superseded_at": "2026-03-10T14:30:00Z"
}
```

### 4.3 判断の統合

**複数の関連判断を統合：**
```json
{
  "decision_id": "ARCH-010",
  "description": "認証・認可システム統合設計",
  "status": "active",
  "consolidates": ["SECURITY-001", "SECURITY-002", "API-003"],
  "rationale": "分散していた認証関連判断を統合"
}
```

### 4.4 判断の廃止

**廃止時：**
```json
{
  "decision_id": "IMPL-008",
  "status": "deprecated",
  "deprecated_at": "2026-03-10T16:00:00Z",
  "deprecation_reason": "要件変更により不要になった機能"
}
```

---

## 👥 5. 複数AI並行作業時の扱い

### 5.1 AI識別子の追加

**形式：** `{CATEGORY}-{連番3桁}{AI_ID}`

**AI識別子：**
- `A`, `B`, `C`... (同期前の識別)
- `DEV`, `STG`, `PROD` (環境別)
- `TEAM1`, `TEAM2` (チーム別)

**使用例：**
```json
{
  "parallel_decisions": [
    {
      "decision_id": "IMPL-001A",
      "description": "ユーザー登録API実装（AI-A担当）",
      "ai_agent": "devin-instance-alpha"
    },
    {
      "decision_id": "IMPL-001B", 
      "description": "ユーザー登録バリデーション実装（AI-B担当）",
      "ai_agent": "devin-instance-beta"
    }
  ]
}
```

### 5.2 競合回避戦略

**1. 範囲分割による回避：**
```json
{
  "assignments": {
    "AI-A": {"categories": ["ARCH", "DESIGN"], "range": "001-099"},
    "AI-B": {"categories": ["IMPL", "TEST"], "range": "001-099"},
    "AI-C": {"categories": ["DEPLOY", "OPS"], "range": "001-099"}
  }
}
```

**2. 時系列による回避：**
```json
{
  "decision_id": "IMPL-001_20260310_1030",
  "description": "タイムスタンプ付きID（緊急回避）",
  "note": "統合時に正規IDに変換予定"
}
```

### 5.3 統合時の採番整理

**統合前：**
```json
{
  "pre_integration": [
    {"decision_id": "IMPL-001A", "description": "機能A実装"},
    {"decision_id": "IMPL-002A", "description": "機能B実装"},
    {"decision_id": "IMPL-001B", "description": "機能C実装"}
  ]
}
```

**統合後：**
```json
{
  "post_integration": [
    {"decision_id": "IMPL-001", "description": "機能A実装", "original_id": "IMPL-001A"},
    {"decision_id": "IMPL-002", "description": "機能B実装", "original_id": "IMPL-002A"},
    {"decision_id": "IMPL-003", "description": "機能C実装", "original_id": "IMPL-001B"}
  ]
}
```

---

## 🔧 6. プロジェクト固有カスタマイズ

### 6.1 業界特化カテゴリの追加

**金融業界の例：**
```json
{
  "custom_categories": {
    "COMPLIANCE": {
      "description": "法規制・コンプライアンス判断",
      "prefix": "COMP",
      "examples": ["COMP-001: PCI DSS対応方針"]
    },
    "RISK": {
      "description": "リスク管理判断",
      "prefix": "RISK", 
      "examples": ["RISK-001: 信用リスク評価アルゴリム選定"]
    }
  }
}
```

**医療業界の例：**
```json
{
  "custom_categories": {
    "MEDICAL": {
      "description": "医療規制・安全性判断",
      "prefix": "MED",
      "examples": ["MED-001: HIPAA準拠データ暗号化方式"]
    },
    "CLINICAL": {
      "description": "臨床プロセス判断",
      "prefix": "CLIN",
      "examples": ["CLIN-001: 診断支援ロジック実装方針"]
    }
  }
}
```

### 6.2 プロジェクト特化プレフィックス

**大規模プロジェクトでの名前空間分離：**
```json
{
  "project_namespaces": {
    "payment_service": "PAY-",
    "user_service": "USER-", 
    "notification_service": "NOTIF-"
  },
  "examples": [
    {"decision_id": "PAY-ARCH-001", "description": "決済サービスアーキテクチャ"},
    {"decision_id": "USER-SECURITY-001", "description": "ユーザーサービス認証方式"},
    {"decision_id": "NOTIF-PERF-001", "description": "通知サービス配信最適化"}
  ]
}
```

### 6.3 チーム別カスタマイズ

**チーム命名規則：**
```json
{
  "team_conventions": {
    "backend_team": {
      "focus_categories": ["ARCH", "DATA", "API", "SECURITY"],
      "naming_pattern": "{CATEGORY}-BE-{number}"
    },
    "frontend_team": {
      "focus_categories": ["UI", "DESIGN", "PERF"],
      "naming_pattern": "{CATEGORY}-FE-{number}"
    },
    "devops_team": {
      "focus_categories": ["DEPLOY", "OPS", "SECURITY"],
      "naming_pattern": "{CATEGORY}-DO-{number}"
    }
  }
}
```

---

## 🗺️ 7. decision_map構築

### 7.1 decision_idからの逆引き

**decision_mapの基本構造：**
```json
{
  "final": {
    "decision_map": {
      "ARCH-001": {
        "title": "マイクロサービスアーキテクチャ採用",
        "impact_scope": ["deployment", "development_process", "testing"],
        "referenced_in_explanation": true,
        "line_numbers": [45, 67, 89]
      },
      "SECURITY-001": {
        "title": "JWT認証方式採用",
        "impact_scope": ["authentication", "session_management"],
        "referenced_in_explanation": true,
        "line_numbers": [23, 78]
      }
    }
  }
}
```

### 7.2 依存関係の可視化

**依存グラフ例：**
```json
{
  "decision_dependencies": {
    "ARCH-001": {
      "depends_on": [],
      "dependents": ["DEPLOY-001", "OPS-001", "TEST-001-derived"]
    },
    "DEPLOY-001": {
      "depends_on": ["ARCH-001"],
      "dependents": ["OPS-002"]
    },
    "SECURITY-001": {
      "depends_on": ["ARCH-001"],
      "dependents": ["API-001", "IMPL-005"]
    }
  }
}
```

### 7.3 影響範囲の追跡

**トレーサビリティマップ：**
```json
{
  "traceability": {
    "requirements_to_decisions": {
      "REQ-001": ["ARCH-001", "SECURITY-001"],
      "REQ-002": ["DATA-001", "API-001"],
      "REQ-003": ["UI-001", "PERF-001"]
    },
    "decisions_to_implementation": {
      "ARCH-001": ["IMPL-001", "IMPL-002", "IMPL-003"],
      "SECURITY-001": ["IMPL-004", "IMPL-005"]
    },
    "decisions_to_tests": {
      "SECURITY-001": ["TEST-001", "TEST-002"],
      "API-001": ["TEST-003", "TEST-004"]
    }
  }
}
```

---

## 💡 8. 実践例（20個）

### 8.1 アーキテクチャ判断

**例1: マイクロサービス採用**
```json
{
  "decision_id": "ARCH-001",
  "description": "マイクロサービスアーキテクチャ採用",
  "rationale": "独立デプロイ、スケーラビリティ、技術多様性の要件",
  "alternatives_considered": ["モノリス", "モジュラーモノリス"],
  "req_links": ["REQ-SCALE-001", "REQ-DEPLOY-001"]
}
```

**例2: イベント駆動アーキテクチャ**
```json
{
  "decision_id": "ARCH-002",
  "description": "イベント駆動アーキテクチャ（Kafka使用）",
  "rationale": "サービス間の疎結合、非同期処理、拡張性",
  "depends_on": ["ARCH-001"],
  "req_links": ["REQ-ASYNC-001"]
}
```

### 8.2 設計判断

**例3: ドメインモデル**
```json
{
  "decision_id": "DESIGN-001", 
  "description": "DDD手法によるドメインモデル設計",
  "rationale": "複雑なビジネスロジックの明確化",
  "req_links": ["REQ-BIZ-001", "REQ-BIZ-002"]
}
```

**例4: API設計**
```json
{
  "decision_id": "DESIGN-002",
  "description": "RESTful API設計（リソース指向）",
  "rationale": "標準性、理解しやすさ、ツールサポート",
  "alternatives_considered": ["GraphQL", "RPC"],
  "req_links": ["REQ-API-001"]
}
```

### 8.3 実装判断

**例5: フロントエンド技術**
```json
{
  "decision_id": "IMPL-001",
  "description": "フロントエンド: React + TypeScript",
  "rationale": "型安全性、コンポーネント再利用、エコシステム",
  "alternatives_considered": ["Vue.js", "Angular"],
  "req_links": ["REQ-UI-001"]
}
```

**例6: 状態管理**
```json
{
  "decision_id": "IMPL-002",
  "description": "状態管理: Redux Toolkit",
  "rationale": "予測可能な状態管理、デバッグツール充実",
  "depends_on": ["IMPL-001"],
  "req_links": ["REQ-UI-002"]
}
```

### 8.4 テスト判断

**例7: テスト戦略**
```json
{
  "decision_id": "TEST-001",
  "description": "テストピラミッド戦略（Unit:Integration:E2E = 70:20:10）",
  "rationale": "コスト効率とフィードバック速度のバランス",
  "req_links": ["REQ-QUALITY-001"]
}
```

**例8: テストツール**
```json
{
  "decision_id": "TEST-002",
  "description": "E2Eテスト: Playwright採用",
  "rationale": "複数ブラウザ対応、高速実行、豊富なAPI",
  "alternatives_considered": ["Cypress", "Puppeteer"],
  "req_links": ["REQ-BROWSER-001"]
}
```

### 8.5 セキュリティ判断

**例9: 認証方式**
```json
{
  "decision_id": "SECURITY-001",
  "description": "JWT + Refresh Token 認証",
  "rationale": "ステートレス、スケーラブル、セキュア",
  "req_links": ["REQ-AUTH-001", "REQ-SCALE-001"]
}
```

**例10: 暗号化**
```json
{
  "decision_id": "SECURITY-002",
  "description": "機密データ暗号化: AES-256-GCM",
  "rationale": "NIST推奨、認証付き暗号化、高性能",
  "req_links": ["REQ-DATA-PROTECTION-001"]
}
```

### 8.6 パフォーマンス判断

**例11: キャッシュ戦略**
```json
{
  "decision_id": "PERF-001",
  "description": "多層キャッシュ戦略（Redis + Application Cache）",
  "rationale": "レスポンス時間最適化（目標: 100ms以下）",
  "req_links": ["REQ-PERF-001"]
}
```

**例12: 画像最適化**
```json
{
  "decision_id": "PERF-002", 
  "description": "画像最適化: WebP + レスポンシブ配信",
  "rationale": "帯域幅削減（30-50%）、UX向上",
  "req_links": ["REQ-MOBILE-001"]
}
```

### 8.7 デプロイ判断

**例13: デプロイ戦略**
```json
{
  "decision_id": "DEPLOY-001",
  "description": "カナリアデプロイメント戦略",
  "rationale": "リスク最小化、段階的ロールアウト",
  "req_links": ["REQ-RELIABILITY-001"]
}
```

**例14: インフラ**
```json
{
  "decision_id": "DEPLOY-002",
  "description": "Kubernetes + Helm デプロイメント",
  "rationale": "宣言的設定、バージョン管理、スケーリング",
  "depends_on": ["ARCH-001"],
  "req_links": ["REQ-INFRA-001"]
}
```

### 8.8 運用判断

**例15: 監視**
```json
{
  "decision_id": "OPS-001",
  "description": "監視スタック: Prometheus + Grafana + AlertManager",
  "rationale": "メトリクス収集、可視化、アラート統合",
  "req_links": ["REQ-MONITORING-001"]
}
```

**例16: ログ管理**
```json
{
  "decision_id": "OPS-002",
  "description": "ログ集約: ELK Stack (Elasticsearch + Logstash + Kibana)",
  "rationale": "中央集約、全文検索、可視化",
  "req_links": ["REQ-LOGGING-001"]
}
```

### 8.9 データ判断

**例17: データベース選定**
```json
{
  "decision_id": "DATA-001",
  "description": "プライマリDB: PostgreSQL",
  "rationale": "ACID特性、豊富な機能、性能",
  "alternatives_considered": ["MySQL", "MongoDB"],
  "req_links": ["REQ-DATA-CONSISTENCY-001"]
}
```

**例18: データ移行**
```json
{
  "decision_id": "DATA-002",
  "description": "段階的データ移行戦略（Strangler Pattern）",
  "rationale": "ダウンタイム最小化、リスク軽減",
  "req_links": ["REQ-MIGRATION-001"]
}
```

### 8.10 複雑なシナリオ

**例19: 修正版の例**
```json
{
  "decisions": [
    {
      "decision_id": "API-001",
      "description": "REST API バージョニング: ヘッダー方式",
      "status": "superseded",
      "superseded_by": "API-001-rev1"
    },
    {
      "decision_id": "API-001-rev1", 
      "description": "REST API バージョニング: URL方式（/v1/, /v2/）",
      "rationale": "クライアント実装の簡素化、明示性向上",
      "supersedes": "API-001"
    }
  ]
}
```

**例20: 関連判断の例**
```json
{
  "decisions": [
    {
      "decision_id": "SECURITY-003",
      "description": "多要素認証(MFA) 実装"
    },
    {
      "decision_id": "SECURITY-003-1",
      "description": "MFA第1要素: TOTP (Google Authenticator)",
      "parent_decision": "SECURITY-003"
    },
    {
      "decision_id": "SECURITY-003-2", 
      "description": "MFA第2要素: SMS バックアップ",
      "parent_decision": "SECURITY-003"
    }
  ]
}
```

---

## ❌ 9. アンチパターン（避けるべき10の間違い）

### 9.1 非標準カテゴリの使用

**❌ 悪い例：**
```json
{
  "decision_id": "FRONTEND-001",  // 非標準カテゴリ
  "description": "React選定"
}
```

**✅ 正しい例：**
```json
{
  "decision_id": "IMPL-001",  // 標準カテゴリ使用
  "description": "フロントエンド技術選定: React"
}
```

### 9.2 連番の不整合

**❌ 悪い例：**
```json
{
  "decisions": [
    {"decision_id": "IMPL-1", "description": "..."},      // ゼロパディングなし
    {"decision_id": "IMPL-002", "description": "..."},    // 一貫性なし
    {"decision_id": "IMPL-10", "description": "..."}     // 桁数不統一
  ]
}
```

**✅ 正しい例：**
```json
{
  "decisions": [
    {"decision_id": "IMPL-001", "description": "..."},
    {"decision_id": "IMPL-002", "description": "..."},
    {"decision_id": "IMPL-003", "description": "..."}
  ]
}
```

### 9.3 decision_idの重複

**❌ 悪い例：**
```json
{
  "decisions": [
    {"decision_id": "ARCH-001", "description": "マイクロサービス採用"},
    {"decision_id": "ARCH-001", "description": "コンテナ化戦略"}  // 重複!
  ]
}
```

### 9.4 意味のない修正版

**❌ 悪い例：**
```json
{
  "decision_id": "DESIGN-001-rev1",
  "description": "ユーザーモデル設計",
  "supersedes": "DESIGN-001",
  "rationale": "少し変更した"  // 理由が曖昧
}
```

**✅ 正しい例：**
```json
{
  "decision_id": "DESIGN-001-rev1",
  "description": "ユーザーモデル設計（プライバシー要件追加）",
  "supersedes": "DESIGN-001", 
  "rationale": "GDPR対応のためプライバシー設定フィールドを追加"
}
```

### 9.5 カテゴリと内容の不一致

**❌ 悪い例：**
```json
{
  "decision_id": "ARCH-001",  // アーキテクチャカテゴリだが...
  "description": "ボタンの色を青に決定"  // UI詳細の判断
}
```

**✅ 正しい例：**
```json
{
  "decision_id": "UI-001",
  "description": "プライマリボタン色: ブランドブルー(#0066CC)"
}
```

### 9.6 final.decision_map との不整合

**❌ 悪い例：**
```json
{
  "steps": [{
    "decisions": [
      {"decision_id": "IMPL-001", "description": "..."},
      {"decision_id": "IMPL-002", "description": "..."}
    ]
  }],
  "final": {
    "decision_map": ["IMPL-001"]  // IMPL-002が漏れている！
  }
}
```

### 9.7 関連判断の親子関係未記録

**❌ 悪い例：**
```json
{
  "decisions": [
    {"decision_id": "SECURITY-001-1", "description": "TOTP実装"},
    {"decision_id": "SECURITY-001-2", "description": "SMS実装"}
    // 親判断への言及なし
  ]
}
```

**✅ 正しい例：**
```json
{
  "decisions": [
    {"decision_id": "SECURITY-001", "description": "多要素認証実装"},
    {
      "decision_id": "SECURITY-001-1",
      "description": "TOTP実装",
      "parent_decision": "SECURITY-001"
    }
  ]
}
```

### 9.8 競合する decision_id の放置

**❌ 悪い例（並行作業後）：**
```json
{
  "decisions": [
    {"decision_id": "IMPL-001A", "description": "..."},  // AI-A
    {"decision_id": "IMPL-001B", "description": "..."}   // AI-B
  ]
  // 統合時に放置
}
```

### 9.9 廃止判断の未マーク

**❌ 悪い例：**
```json
{
  "decisions": [
    {
      "decision_id": "IMPL-005",
      "description": "廃止された機能の実装"
      // statusやdeprecated_atが未設定
    }
  ]
}
```

### 9.10 rationale の記載漏れ

**❌ 悪い例：**
```json
{
  "decision_id": "ARCH-001",
  "description": "マイクロサービス採用"
  // rationale, alternatives_considered が空
}
```

**✅ 正しい例：**
```json
{
  "decision_id": "ARCH-001", 
  "description": "マイクロサービス採用",
  "rationale": "独立デプロイ、技術多様性、チーム自律性の要件を満たすため",
  "alternatives_considered": ["モノリシック", "モジュラーモノリス"]
}
```

---

## ✅ 10. ベストプラクティス

### 10.1 命名に関するベストプラクティス

#### ✅ DO（推奨）
- **標準カテゴリを厳密に使用** - 13カテゴリ以外は使わない
- **3桁ゼロパディングを徹底** - `001`, `002`, `010`, `100`
- **修正版には明確な理由を記載** - なぜ変更したのかを必ず説明
- **関連判断には親子関係を明記** - `parent_decision` フィールドを活用

#### ❌ DON'T（非推奨）
- 独自カテゴリの勝手な追加 
- 連番の飛び番・重複
- 理由のない修正版作成
- decision_id の使い回し

### 10.2 ライフサイクル管理のベストプラクティス

#### ✅ DO（推奨）
- **判断の状態を常に最新に保つ** - `status` フィールドの適切な更新
- **廃止判断は削除せず deprecated にマーク** - 履歴保持の重要性
- **統合時には元判断への参照を残す** - `original_id` で追跡可能性確保

#### ❌ DON'T（非推奨）
- 古い判断の物理削除
- 状態更新の怠慢
- 統合時の履歴消去

### 10.3 チーム作業のベストプラクティス

#### ✅ DO（推奨）
- **並行作業時は AI識別子を使用** - 競合回避
- **統合時には必ず採番整理** - 一貫性のある最終状態
- **decision_map の一意性を保証** - 重複チェック実施

#### ❌ DON'T（非推奨）
- 統合なしの並行ID放置
- decision_map の不整合放置
- 競合時の強制上書き

### 10.4 品質保証のベストプラクティス

#### ✅ DO（推奨）
- **セッション終了前にvalidation実行** - decision_id の一意性・整合性チェック
- **final.decision_map の完全性確認** - 全decision_idが列挙されているか
- **定期的なクリーンアップ** - 不要な並行IDや重複の整理

#### ❌ DON'T（非推奨）
- validation の省略
- decision_map との不整合放置
- 長期間の不整合状態継続

### 10.5 トレーサビリティのベストプラクティス

#### ✅ DO（推奨）
- **requirements との紐付けを明確化** - `req_links` の充実
- **依存関係の明記** - `depends_on` フィールド活用
- **影響範囲の記録** - `impact_scope` で変更影響を明示

#### ❌ DON'T（非推奨）
- 要件との紐付け欠落
- 判断の孤立（関係性不明）
- 変更影響の記録漏れ

### 10.6 運用・メンテナンスのベストプラクティス

#### ✅ DO（推奨）
- **定期的な decision_id レビュー** - 月次での一貫性確認
- **メトリクス算出との連携確認** - AICQメトリクスへの影響検証
- **新カテゴリ追加時は組織全体で合意** - 標準化の維持

#### ❌ DON'T（非推奨）
- 放置による品質劣化
- メトリクスとの不整合
- 個人判断でのルール変更

---

## 📊 まとめ

この標準化ガイドにより、**decision_id の一貫性・追跡性・品質**が大幅に向上します。

### 期待効果

1. **トレーサビリティ向上** - 要件から実装まで判断の流れを完全追跡
2. **メトリクス精度向上** - AICQメトリクスの信頼性向上
3. **チーム効率向上** - 統一ルールによるコミュニケーション円滑化
4. **品質保証強化** - 自動チェック・バリデーションの実現

### 導入アクションプラン

1. **Week 1**: チーム内でのガイドライン共有
2. **Week 2**: 既存 decision_id の標準化（リファクタリング）
3. **Week 3**: validation スクリプトの導入
4. **Week 4**: 定期レビュープロセスの確立

このガイドを参考に、高品質な監査ログシステムを構築してください！