# Devin組織横断開発標準リポジトリ

**最終更新**: 2025-10-23  
**バージョン**: 1.1.0  
**管理者**: DevOps Team

---

## 📖 概要

このリポジトリは、**Devin AI**を活用した組織横断プロジェクト開発における統一標準を定義・管理します。技術選定、コーディング規約、セキュリティ、インフラ構成など、ソフトウェア開発ライフサイクル全体をカバーする包括的なガイドラインを提供します。

**対象読者**:
- Devin AI（自律開発・コード生成）
- 開発者・アーキテクト（レビュー・監督）
- テクニカルリード（技術意思決定）
- QAエンジニア（品質保証）
- DevOps・インフラエンジニア（環境構築・運用）

---

## 🗂️ リポジトリ構成

```
devin-organization-standards/
│
├── README.md                          # このファイル（全体概要・ナビゲーション）
│
├── AI-USAGE-GUIDE.md                  # AI活用ガイド（全体概要・ナビゲーション）
├── AI-WORKFLOWS.md                    # AI向け詳細ワークフロー
├── AI-PROMPTS.md                      # AI向けプロンプト例集
├── AI-CHECKLISTS.md                   # AI向けチェックリスト
├── AI-CODING-INSTRUCTIONS.md          # AI開発者向けコーディング規約参照指示
├── AI-QUICK-REFERENCE.md              # AI開発者向けクイックリファレンス
├── AI-PROMPT-TEMPLATES.md             # AIプロンプトテンプレート集
│
├── 01-coding-standards/               # コーディング規約（Tier 1: 必須）
│   ├── README.md                      # セクション概要
│   ├── 00-general-principles.md       # 全言語共通原則・保守性メトリクス・ドキュメンテーション標準
│   ├── typescript-javascript-standards.md
│   ├── python-standards.md
│   ├── java-standards.md
│   ├── sql-standards.md
│   └── css-styling-standards.md
│
├── 02-architecture/                   # システム設計標準（Tier 1）
│   ├── README.md
│   ├── design-principles.md           # アーキテクチャ設計原則
│   ├── api-design.md                  # API設計ガイド
│   ├── microservices-guidelines.md    # マイクロサービス設計
│   ├── database-design.md             # データベース設計標準
│   └── frontend-architecture.md       # フロントエンド設計
│
├── 03-security/                       # セキュリティ標準（Tier 1）
│   ├── README.md
│   ├── security-checklist.md          # セキュリティチェックリスト
│   ├── authentication-authorization.md # 認証・認可
│   ├── data-protection.md             # データ保護
│   ├── vulnerability-management.md     # 脆弱性管理
│   └── secure-coding-practices.md     # セキュアコーディング
│
├── 04-testing/                        # テスト標準（Tier 2: 推奨）
│   ├── README.md
│   ├── testing-strategy.md            # テスト戦略
│   ├── unit-testing.md                # ユニットテスト
│   ├── integration-testing.md         # 統合テスト
│   ├── e2e-testing.md                 # E2Eテスト
│   └── performance-testing.md         # パフォーマンステスト
│
├── 05-infrastructure/                 # インフラ標準（Tier 2）
│   ├── README.md
│   ├── cloud-providers.md             # クラウドプロバイダー選定
│   ├── container-standards.md         # コンテナ化標準
│   ├── ci-cd-pipeline.md              # CI/CDパイプライン
│   └── monitoring-logging.md          # 監視・ログ
│
├── 06-tools-and-environment/          # 開発環境（Tier 3: 任意）
│   ├── README.md
│   ├── ide-setup.md                   # IDE設定
│   ├── linters-formatters.md          # リンター・フォーマッター
│   └── recommended-extensions.md      # 推奨拡張機能
│
├── 07-project-management/             # プロジェクト管理（Tier 2）
│   ├── README.md
│   ├── git-workflow.md                # Gitワークフロー
│   ├── code-review-standards.md       # コードレビュー標準
│   ├── documentation-standards.md     # ドキュメンテーション
│   └── release-management.md          # リリース管理
│
├── 08-performance/                    # パフォーマンス（Tier 2）
│   ├── README.md
│   ├── optimization-guidelines.md     # 最適化ガイドライン
│   ├── caching-strategies.md          # キャッシング戦略
│   └── scalability-patterns.md        # スケーラビリティパターン
│
├── 09-data-management/                # データ管理（Tier 2）
│   ├── README.md
│   ├── data-governance.md             # データガバナンス
│   ├── backup-recovery.md             # バックアップ・リカバリー
│   └── data-migration.md              # データ移行
│
├── 10-compliance/                     # コンプライアンス（Tier 1）
│   ├── README.md
│   ├── gdpr-compliance.md             # GDPR対応
│   ├── accessibility-standards.md     # アクセシビリティ
│   └── licensing.md                   # ライセンス管理
│
├── _archive/                          # 廃止・旧版アーカイブ
│
└── implementation-temp/               # 実装資料（参考用）
    ├── IMPLEMENTATION_SUMMARY.md      # 実装内容サマリー
    └── [言語別ドキュメンテーション追加セクション]

```

---

## 📂 各セクション詳細

### 01. コーディング規約（Tier 1: 必須）

**記載内容**:
- **00-general-principles.md**: SOLID原則・命名規則・エラーハンドリング・**保守性メトリクス（関数/クラスサイズ、複雑度の数値基準）**・**ドキュメンテーション必須レベル（3段階）**
- **typescript-javascript-standards.md**: TypeScript/JavaScript規約・**JSDoc標準・ESLint設定**
- **python-standards.md**: Python規約・**Google Style Docstring・Pylint設定**
- **java-standards.md**: Java規約・**Javadoc標準・CheckStyle設定**
- **sql-standards.md**: SQL規約・**SQLコメント標準**
- **css-styling-standards.md**: CSS/SCSS規約・**BEM命名規則・Stylelint設定**

**利用シーン**:
- **コード生成前**: AI開発者が言語別規約を確認
- **コードレビュー時**: レビュアーが基準として参照
- **リファクタリング時**: 既存コードの品質改善
- **新規参画時**: 開発者のオンボーディング

**保守性メトリクス（v1.1.0追加）**:
```
関数/メソッド行数:
  - 理想: 20行以内
  - 推奨上限: 50行
  - 絶対上限: 100行（超過時は必ずリファクタリング）

クラス/ファイル行数:
  - 理想: 200行以内
  - 推奨上限: 400行
  - 絶対上限: 1000行（超過時は必ず分割）

循環的複雑度:
  - 理想: 5以下
  - 推奨上限: 10
  - 絶対上限: 15（超過時は必ずリファクタリング）

ネスト深さ:
  - 理想: 2階層以内
  - 推奨上限: 3階層
  - 絶対上限: 4階層（超過時は必ずリファクタリング）

パラメータ数:
  - 理想: 3個以内
  - 推奨上限: 5個
  - 絶対上限: 7個（超過時はオブジェクト化検討）
```

**ドキュメンテーション必須レベル（v1.1.0追加）**:
- **Level 1（必須）**: すべてのファイル・パブリッククラス・パブリックAPIにドキュメント必須
- **Level 2（推奨）**: 複雑なロジック（複雑度10以上）、ビジネスルール、非自明な実装
- **Level 3（任意）**: 単純な処理（自己文書化優先）

---

### 02. アーキテクチャ設計（Tier 1: 必須）

**記載内容**:
- **design-principles.md**: マイクロサービス、レイヤードアーキテクチャ、DDD原則
- **api-design.md**: RESTful API設計、GraphQL設計、バージョニング戦略
- **microservices-guidelines.md**: サービス分割、通信パターン、データ整合性
- **database-design.md**: スキーマ設計、インデックス戦略、データモデリング
- **frontend-architecture.md**: Component設計、状態管理、ルーティング

**利用シーン**:
- **プロジェクト初期設計**: システム構成の意思決定
- **技術選定**: アーキテクチャパターンの選択
- **リファクタリング**: 既存システムの改善
- **技術負債解消**: アーキテクチャの再設計

---

### 03. セキュリティ標準（Tier 1: 必須）

**記載内容**:
- **security-checklist.md**: フェーズ別セキュリティチェック項目
- **authentication-authorization.md**: OAuth2.0、JWT、RBAC実装
- **data-protection.md**: 暗号化、個人情報保護、データマスキング
- **vulnerability-management.md**: 脆弱性スキャン、パッチ適用プロセス
- **secure-coding-practices.md**: OWASP Top 10対策、入力検証

**利用シーン**:
- **設計レビュー**: セキュリティリスクの事前評価
- **実装時**: セキュアコーディング実践
- **脆弱性対応**: セキュリティインシデントの修正
- **監査・コンプライアンス**: セキュリティ監査の準備

---

### 04. テスト標準（Tier 2: 推奨）

**記載内容**:
- **testing-strategy.md**: テストピラミッド、カバレッジ目標
- **unit-testing.md**: ユニットテストベストプラクティス
- **integration-testing.md**: 統合テスト戦略
- **e2e-testing.md**: E2Eテスト自動化
- **performance-testing.md**: 負荷テスト、ストレステスト

**利用シーン**:
- **テストコード生成**: AI開発者がテストを自動生成
- **品質保証**: QAエンジニアがテスト計画策定
- **リグレッション防止**: 既存機能の保護
- **CI/CD統合**: 自動テスト実行の設定

---

### 05. インフラ標準（Tier 2: 推奨）

**記載内容**:
- **cloud-providers.md**: AWS、Azure、GCPの選定基準
- **container-standards.md**: Docker、Kubernetes設定
- **ci-cd-pipeline.md**: GitHub Actions、GitLab CI/CD設定
- **monitoring-logging.md**: Prometheus、Grafana、ELKスタック

**利用シーン**:
- **環境構築**: 開発・ステージング・本番環境の構築
- **デプロイ自動化**: CI/CDパイプラインの設定
- **障害対応**: ログ分析、メトリクス監視
- **スケーリング**: インフラのスケールアウト設計

---

### 06. 開発環境・ツール（Tier 3: 任意）

**記載内容**:
- **ide-setup.md**: VSCode、IntelliJ IDEA設定
- **linters-formatters.md**: ESLint、Prettier、Pylint設定
- **recommended-extensions.md**: 生産性向上ツール

**利用シーン**:
- **新規参画時**: 開発環境の統一セットアップ
- **コードフォーマット自動化**: CI/CDでのコード品質チェック
- **チーム開発効率化**: ツールの標準化

---

### 07. プロジェクト管理（Tier 2: 推奨）

**記載内容**:
- **git-workflow.md**: Git Flow、GitHub Flow、トランクベース開発
- **code-review-standards.md**: レビュープロセス、チェックリスト
- **documentation-standards.md**: README、API仕様書、運用手順書
- **release-management.md**: バージョニング、リリースノート作成

**利用シーン**:
- **ブランチ戦略策定**: プロジェクト開始時のワークフロー決定
- **コードレビュー**: Pull Request時のレビュー基準
- **リリース準備**: リリース計画とバージョン管理
- **ドキュメント作成**: プロジェクトドキュメントの標準化

---

### 08. パフォーマンス最適化（Tier 2: 推奨）

**記載内容**:
- **optimization-guidelines.md**: フロントエンド・バックエンド最適化
- **caching-strategies.md**: Redis、Memcached、CDN活用
- **scalability-patterns.md**: 水平スケーリング、負荷分散

**利用シーン**:
- **パフォーマンス改善**: レスポンスタイム短縮
- **スケーラビリティ設計**: 大規模トラフィック対応
- **コスト最適化**: リソース効率化
- **ボトルネック解消**: パフォーマンス分析と改善

---

### 09. データ管理（Tier 2: 推奨）

**記載内容**:
- **data-governance.md**: データ品質、マスターデータ管理
- **backup-recovery.md**: バックアップ戦略、災害復旧計画
- **data-migration.md**: データ移行プロセス、検証方法

**利用シーン**:
- **データ基盤構築**: データウェアハウス、データレイク設計
- **災害対策**: バックアップ・リカバリー体制構築
- **システム移行**: レガシーシステムからの移行
- **データ品質向上**: データクレンジング、正規化

---

### 10. コンプライアンス（Tier 1: 必須）

**記載内容**:
- **gdpr-compliance.md**: GDPR対応チェックリスト
- **accessibility-standards.md**: WCAG準拠、アクセシビリティテスト
- **licensing.md**: OSSライセンス管理

**利用シーン**:
- **法的リスク回避**: GDPR、個人情報保護法対応
- **アクセシビリティ対応**: WCAG準拠の実装
- **ライセンス管理**: OSSライセンスコンプライアンス
- **監査対応**: 外部監査の準備

---

### AI開発者向けドキュメント

**記載内容**:
- **AI-USAGE-GUIDE.md**: AI活用の全体概要とナビゲーション（16.2KB）
- **AI-WORKFLOWS.md**: フェーズ別の詳細ワークフロー（18.0KB）
- **AI-PROMPTS.md**: 実践的なプロンプト例集（22.8KB）
- **AI-CHECKLISTS.md**: 検証チェックリスト（13.3KB）
- **AI-CODING-INSTRUCTIONS.md**: コーディング規約参照指示（15.4KB）
- **AI-QUICK-REFERENCE.md**: クイックリファレンス（7.3KB）
- **AI-PROMPT-TEMPLATES.md**: プロンプトテンプレート集（13.3KB）

**利用シーン**:
- **AI開発ツール（Devin、Cursor、GitHub Copilot等）によるコード生成時**
- **コーディング規約の迅速な確認**
- **プロンプト作成時のテンプレート参照**
- **生成コードの自己検証**

**ドキュメント役割分担**:
```
┌─────────────────────────────────────────────┐
│ AI-USAGE-GUIDE.md (全体ナビゲーション)      │
│ - リポジトリ全体の使い方                    │
│ - フェーズ別ガイド                          │
│ - ドキュメント間の関係                      │
└─────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────┐
│ AI-CODING-INSTRUCTIONS.md (コーディング詳細)│
│ - コーディング規約の詳細参照指示            │
│ - 数値基準一覧                              │
│ - 言語別ドキュメンテーション形式            │
│ - セルフチェックリスト                      │
└─────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────┐
│ AI-QUICK-REFERENCE.md (クイックリファレンス)│
│ - コーディング規約の要約版                  │
│ - 数値基準表                                │
│ - 必須チェックリスト                        │
└─────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────┐
│ AI-PROMPT-TEMPLATES.md (プロンプト集)       │
│ - コード生成用テンプレート                  │
│ - リファクタリング用テンプレート            │
│ - レビュー用テンプレート                    │
└─────────────────────────────────────────────┘
```

---

### implementation-temp/（実装資料）

**記載内容**:
- **IMPLEMENTATION_SUMMARY.md**: 実装内容の包括的サマリー
- **言語別ドキュメンテーション追加セクション**（5ファイル）: 各言語規約に追加した内容の元ファイル

**利用シーン**:
- **実装内容の確認**: 今回追加した保守性メトリクスとドキュメンテーション標準の詳細
- **適用手順の参照**: 各言語での適用方法
- **参考資料**: 将来の規約更新時の参考

**注意**: このディレクトリは参考資料であり、実際の規約は各言語の標準ファイルに統合済みです。

---

## 🎯 主要特徴

### 1. 横断性
すべてのプロジェクトで共通的に適用可能な標準を定義。特定プロジェクト固有のルールは含まない。

### 2. 安定性
頻繁に変更されない確立されたベストプラクティスを記載。変更時はバージョン管理とチェンジログで追跡。

### 3. 再利用性
新規プロジェクト開始時、このリポジトリをテンプレートとして参照可能。

---

## 🤖 Devinの活用方法

### Devinへの指示例

**プロジェクト初期化時**:
```
このプロジェクトは、以下の組織標準に準拠してください：
https://github.com/[your-org]/devin-organization-standards

特に以下を厳守してください：
- 01-coding-standards/typescript-javascript-standards.md
- 02-architecture/api-design.md
- 03-security/security-checklist.md
```

**コード生成時**:
```
以下のコーディング規約に準拠したTypeScriptコードを生成してください：
https://github.com/[your-org]/devin-organization-standards/blob/main/01-coding-standards/typescript-javascript-standards.md

【重要な数値基準】
- 関数は50行以内、絶対上限100行
- 循環的複雑度は10以下、絶対上限15
- すべてのパブリックAPIにJSDocコメント必須
```

**セキュリティレビュー時**:
```
以下のセキュリティチェックリストに基づいてコードを検証してください：
https://github.com/[your-org]/devin-organization-standards/blob/main/03-security/security-checklist.md

特にOWASP Top 10対策を確認してください。
```

**AI開発者向けクイックスタート**:
```
以下のドキュメントを参照して、コーディング規約を遵守したコードを生成してください：

1. クイックリファレンス（要約版）:
   /devin-organization-standards/AI-QUICK-REFERENCE.md

2. 詳細指示（数値基準・形式）:
   /devin-organization-standards/AI-CODING-INSTRUCTIONS.md

3. プロンプトテンプレート（コード生成例）:
   /devin-organization-standards/AI-PROMPT-TEMPLATES.md
```

---

## 🔄 更新・ガバナンス

### 更新プロセス
1. **提案**: Issue作成（テンプレート使用）
2. **議論**: ステークホルダーレビュー
3. **承認**: テクニカルリード承認
4. **マージ**: Pull Request経由で更新
5. **通知**: 開発チームへのアナウンス

### バージョン管理
- **Major**: 破壊的変更（例: アーキテクチャ方針の根本的変更）
- **Minor**: 新規追加（例: 新しい言語の標準追加）
- **Patch**: 誤字修正、軽微な改善

### 更新履歴（Changelog）

#### v1.1.0 (2025-10-23)
**追加**:
- 保守性メトリクス（関数/クラスサイズ、複雑度、ネスト深さ等の数値基準）
- ドキュメンテーション必須レベル（3段階: 必須/推奨/任意）
- 言語別ドキュメンテーション標準（JSDoc、Docstring、Javadoc等）
- 自動チェックツール設定（ESLint、Pylint、CheckStyle、Stylelint）
- AI開発者向けドキュメント3件（AI-CODING-INSTRUCTIONS.md、AI-QUICK-REFERENCE.md、AI-PROMPT-TEMPLATES.md）
- implementation-tempディレクトリ（実装資料）

**変更**:
- 00-general-principles.md: 1.3保守性メトリクス、5.3ドキュメンテーション必須レベル、5.4ファイル/クラスドキュメンテーション標準を追加
- typescript-javascript-standards.md: 9.JSDoc標準、10.自動チェックツール設定を追加
- python-standards.md: 11.Docstring標準、12.自動チェックツール設定を追加
- java-standards.md: 10.Javadoc標準、11.自動チェックツール設定を追加
- sql-standards.md: 9.SQLコメント標準を追加
- css-styling-standards.md: 9.CSS/SCSSコメント標準、10.自動チェックツール設定を追加

#### v1.0.0 (2025-10-20)
- 初版リリース
- 10セクション、7つのAI向けドキュメント

---

## 📞 連携方法

### 1. GitHubリポジトリとして管理
```bash
git clone https://github.com/[your-org]/devin-organization-standards.git
```

### 2. プロジェクトへの参照
```
# プロジェクトのREADME.mdに記載
このプロジェクトは以下の組織標準に準拠しています：
https://github.com/[your-org]/devin-organization-standards
```

### 3. CI/CDパイプラインへの統合
```yaml
# .github/workflows/standards-check.yml
name: Standards Check
on: [push, pull_request]
jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Run ESLint
        run: npm run lint
      - name: Run Pylint
        run: pylint src/
```

### 4. Devinへの自動参照
Devinのプロジェクト設定に、このリポジトリのURLを登録することで、自動的に標準を参照可能。

---

## 📚 関連リソース

- **Devin公式ドキュメント**: https://devin.ai/docs
- **OWASP**: https://owasp.org
- **12 Factor App**: https://12factor.net
- **Clean Code**: Robert C. Martin著

---

## 📝 ライセンス

MIT License（組織内部使用）

---

## ✉️ お問い合わせ

- **DevOps Team**: devops@yourorg.com
- **GitHub Issues**: https://github.com/[your-org]/devin-organization-standards/issues
