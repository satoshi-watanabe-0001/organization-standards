# Devin AI ドキュメント活用ガイド

## 📋 このガイドについて

このガイドは、**Devin AI**（および類似のAI開発システム）が本標準リポジトリを効果的に活用し、**完全自律的な開発**を実現するための包括的な指針です。

### ガイド構成

本ガイドは7つのドキュメントで構成されています：

| ドキュメント | 目的 | サイズ | 用途 |
|------------|------|--------|------|
| **[AI-USAGE-GUIDE.md](./AI-USAGE-GUIDE.md)** (このファイル) | 全体概要・ナビゲーション | 約17 KB | 最初に読む、セクション別ガイド |
| **[AI-WORKFLOWS.md](./AI-WORKFLOWS.md)** | 詳細ワークフロー | 約18 KB | フェーズ別の詳細手順 |
| **[AI-PROMPTS.md](./AI-PROMPTS.md)** | 実践プロンプト例集 | 約23 KB | 具体的な指示例20+ |
| **[AI-CHECKLISTS.md](./AI-CHECKLISTS.md)** | 検証チェックリスト | 約13 KB | 自己検証・品質保証 |
| **[AI-CODING-INSTRUCTIONS.md](./AI-CODING-INSTRUCTIONS.md)** | コーディング規約参照指示 | 約15 KB | コーディング規約の詳細ガイド |
| **[AI-QUICK-REFERENCE.md](./AI-QUICK-REFERENCE.md)** | クイックリファレンス | 約7 KB | 数値基準・チェックリスト要約 |
| **[AI-PROMPT-TEMPLATES.md](./AI-PROMPT-TEMPLATES.md)** | プロンプトテンプレート集 | 約13 KB | コード生成・レビュー用テンプレート |

### 推奨読み方

```
1. AI-USAGE-GUIDE.md (このファイル) ← まずここから
   ↓
2. コーディング規約を理解：
   - AI-QUICK-REFERENCE.md（要約版・数値基準）← 最初に確認
   - AI-CODING-INSTRUCTIONS.md（詳細版）
   - AI-PROMPT-TEMPLATES.md（テンプレート）
   ↓
3. 開発フェーズに応じて：
   - プロジェクト開始時 → AI-WORKFLOWS.md (初期化フェーズ)
   - 開発中 → AI-PROMPTS.md (該当するプロンプト例)
   - 検証時 → AI-CHECKLISTS.md (該当するチェックリスト)
   ↓
4. 随時このファイルに戻ってセクション別ガイドを参照
```

---

## 🎯 このリポジトリの目的

このリポジトリは、**Devin AIが人間の詳細な指示なしで、エンタープライズグレードのソフトウェアを自律的に開発できる**ようにするために設計されています。

### 設計思想

1. **完全性**: 技術選定からガバナンスまで全領域をカバー
2. **具体性**: 抽象的な原則ではなく、具体的な実装ガイド
3. **実行可能性**: Devin AIが直接実行できる詳細度
4. **一貫性**: 全標準が統合され、矛盾がない
5. **継続改善**: フィードバックループで進化
6. **定量的基準**: 保守性メトリクスで客観的評価（v1.1.0追加）

---

## 📚 リポジトリ構造（Tier方式）

### 階層型重要度システム

標準は**Tier 1（最重要）→ Tier 4（効率化・運用）**の4階層で整理されています。

| Tier | 重要度 | セクション | 学習優先度 |
|------|--------|-----------|-----------|
| **Tier 1** | 最重要 | 01-coding-standards<br>02-architecture-standards<br>05-technology-stack | 🔴 最優先で習得 |
| **Tier 2** | 重要 | 04-quality-standards<br>07-security-compliance | 🟡 早期に習得 |
| **Tier 3** | 中程度 | 03-development-process<br>06-tools-and-environment | 🟢 開発中に習得 |
| **Tier 4** | 効率化・運用 | 08-templates<br>09-reference<br>10-governance | 🔵 必要時に参照 |

### 推奨学習順序

```
開始 → Tier 1 (コーディング、アーキテクチャ、技術スタック)
       ↓
     Tier 2 (品質、セキュリティ)
       ↓
     Tier 3 (プロセス、ツール)
       ↓
     Tier 4 (テンプレート、リファレンス、ガバナンス)
```

---

## 🗂️ 10セクション完全ガイド

各セクションの**目的、提供内容、使用タイミング、具体的な活用方法**を説明します。

---

### 📁 01-coding-standards (Tier 1) 🆕 v1.1.0更新

**目的**: 一貫した高品質で保守性の高いコード作成

**提供内容**:
- **全言語共通原則** (29.2 KB)
  - SOLID原則・命名規則
  - **🆕 保守性メトリクス**（関数/クラスサイズ、複雑度、ネスト深さ等の数値基準）
  - **🆕 ドキュメンテーション必須レベル**（3段階: 必須/推奨/任意）
  - **🆕 ファイル/クラスレベルドキュメンテーション標準**
- **5言語の詳細コーディング規約**（すべてドキュメンテーション標準を含む）
  - TypeScript/JavaScript (60.2 KB) - **🆕 JSDoc標準・ESLint設定**
  - Python (466.5 KB) - 最も詳細 - **🆕 Google Style Docstring・Pylint設定**
  - Java (130.9 KB) - **🆕 Javadoc標準・CheckStyle設定**
  - SQL (165.6 KB) - **🆕 SQLコメント標準**
  - CSS (138.2 KB) - **🆕 BEM命名規則・Stylelint設定**

**🆕 重要な数値基準（v1.1.0で追加）**:
```
必須遵守の保守性メトリクス：

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

認知的複雑度:
  - 理想: 5以下
  - 推奨上限: 15
  - 絶対上限: 25（超過時は必ずリファクタリング）

ネスト深さ:
  - 理想: 2階層以内
  - 推奨上限: 3階層
  - 絶対上限: 4階層（超過時は必ずリファクタリング）

パラメータ数:
  - 理想: 3個以内
  - 推奨上限: 5個
  - 絶対上限: 7個（超過時はオブジェクト化検討）
```

**🆕 ドキュメンテーション必須レベル（v1.1.0で追加）**:
```
Level 1（必須）- 違反 = 品質ゲート不合格:
  ✓ すべてのファイルにファイルヘッダー
  ✓ すべてのパブリッククラス/インターフェースにコメント
  ✓ すべてのパブリックAPI（関数/メソッド）にコメント
    - パラメータ（@param / Args）
    - 戻り値（@returns / Returns）
    - 例外（@throws / Raises）

Level 2（強く推奨）- コードレビューで指摘:
  ✓ 複雑なロジック（循環的複雑度10以上）
  ✓ ビジネスルール・制約を反映した実装
  ✓ 非自明な実装（パフォーマンス最適化、技術的回避策）

Level 3（任意）- 自己文書化優先:
  ✓ 単純な処理（getter/setter、明確な処理フロー）
  ✓ 自己説明的なコード（変数名・関数名で意図が明確）
```

**使用タイミング**:
- ✅ コード生成前（毎回） - **🆕 AI-QUICK-REFERENCE.mdで数値基準確認**
- ✅ コードレビュー時 - **🆕 保守性メトリクスで評価**
- ✅ リファクタリング時 - **🆕 絶対上限超過時は必須**

**🆕 AI開発者向けクイックスタート**:

**ステップ1**: まず **AI-QUICK-REFERENCE.md** を読む（要約版・7.3KB）
```
必須チェック項目:
□ 関数は50行以内か？（絶対上限100行）
□ 複雑度は10以下か？（絶対上限15）
□ パブリックAPIにコメントがあるか？
□ ファイルヘッダーがあるか？
```

**ステップ2**: 詳細は **AI-CODING-INSTRUCTIONS.md** を参照（詳細版・15.4KB）
```
- 数値基準の詳細説明
- 言語別ドキュメンテーション形式
- セルフチェックリスト
- 自動チェックツール設定
```

**ステップ3**: **AI-PROMPT-TEMPLATES.md** でコード生成テンプレート利用（13.3KB）
```
基本テンプレート:
「以下の規約に準拠したコードを生成してください：
- 関数は50行以内（絶対上限100行）
- 複雑度は10以下（絶対上限15）
- すべてのパブリックAPIに[JSDoc/Docstring/Javadoc]を記載
...」
```

**具体的な活用方法**:
1. プロジェクトで使用する言語の標準を読み込む
2. **🆕 AI-QUICK-REFERENCE.mdで数値基準を確認**
3. コード生成時に標準のパターンと数値基準を適用
4. **🆕 保守性メトリクスで自己評価**
5. 自己検証チェックリストで確認

**Devin AI向けプロンプト例**:
```
「以下のコーディング規約に厳密に準拠して、ユーザー認証機能を実装してください：

【必須参照ドキュメント】
1. AI-QUICK-REFERENCE.md（数値基準確認）
2. 01-coding-standards/python-standards.md（Python規約）
3. 01-coding-standards/00-general-principles.md（共通原則）

【必須遵守事項】
- 関数は20行以内（推奨上限50行、絶対上限100行）
- 循環的複雑度は5以下（推奨上限10、絶対上限15）
- すべてのパブリック関数にGoogle Style Docstringを記載
- ファイルヘッダーを記載
- PEP 8準拠、型ヒント使用、適切なエラーハンドリング

生成後、AI-CODING-INSTRUCTIONS.mdのセルフチェックリストで検証してください。」
```

**🆕 自動チェックツール統合（v1.1.0で追加）**:
```bash
# TypeScript/JavaScript
npm run lint  # ESLint（複雑度・ドキュメントチェック）

# Python
pylint src/  # Pylint（複雑度・Docstringチェック）

# Java
mvn checkstyle:check  # CheckStyle（複雑度・Javadocチェック）

# CSS/SCSS
npm run lint:css  # Stylelint（命名規則チェック）
```

**ディレクトリREADME**: [01-coding-standards/README.md](./01-coding-standards/)

---

### 🏗️ 02-architecture-standards (Tier 1)

**目的**: スケーラブルで保守性の高いシステム設計

**提供内容**:
- システムアーキテクチャパターン
- マイクロサービス設計ガイドライン
- データベースアーキテクチャ
- セキュリティアーキテクチャ
- API設計標準 (293.8 KB - 最も詳細)

**使用タイミング**:
- ✅ プロジェクト設計フェーズ
- ✅ アーキテクチャ決定時
- ✅ システム拡張時

**具体的な活用方法**:
1. プロジェクト要件を分析
2. 適切なアーキテクチャパターンを選択
3. パターンに基づいてコンポーネント設計

**Devin AI向けプロンプト例**:
```
「02-architecture-standards/microservices-guidelines.mdに基づいて、
Eコマースシステムのマイクロサービス分解を提案してください。
サービス境界、通信パターン、データ管理戦略を含めてください。」
```

**ディレクトリREADME**: [02-architecture-standards/README.md](./02-architecture-standards/)

---

### 🔄 03-development-process (Tier 3)

**目的**: 一貫した開発プロセスの遵守

**提供内容**:
- 開発ライフサイクル標準
- Git運用フロー
- コードレビュープロセス
- リリース管理標準
- 変更管理プロセス

**使用タイミング**:
- ✅ Git操作時（コミット、ブランチ、マージ）
- ✅ プルリクエスト作成時
- ✅ リリース準備時

**具体的な活用方法**:
1. Git操作前にgit-workflow.mdを参照
2. コミットメッセージ規約に準拠
3. プルリクエストテンプレート使用

**Devin AI向けプロンプト例**:
```
「03-development-process/git-workflow.mdのコミット規約に従って、
新機能実装の変更をコミットしてください。
適切なブランチ作成、コミットメッセージ、プルリクエスト作成を含めてください。」
```

**ディレクトリREADME**: [03-development-process/README.md](./03-development-process/)

---

### ✅ 04-quality-standards (Tier 2)

**目的**: 高品質なソフトウェアの保証

**提供内容**:
- テスト標準（ユニット/統合/E2E）
- コード品質指標
- パフォーマンス基準
- セキュリティテスト標準
- アクセシビリティ標準

**使用タイミング**:
- ✅ テストコード作成時
- ✅ 品質検証時
- ✅ パフォーマンスチューニング時

**具体的な活用方法**:
1. テストカバレッジ目標を確認
2. 標準に基づいてテストコード生成
3. 品質メトリクスで自己評価

**Devin AI向けプロンプト例**:
```
「04-quality-standards/testing-standards.mdに基づいて、
PaymentServiceクラスの包括的なユニットテストを作成してください。
カバレッジ80%以上、モック使用、エッジケース考慮を含めてください。」
```

**ディレクトリREADME**: [04-quality-standards/README.md](./04-quality-standards/)

---

### 🛠️ 05-technology-stack (Tier 1)

**目的**: 承認済み技術の使用による一貫性

**提供内容**:
- 承認済み技術カタログ
- フロントエンド技術標準
- バックエンド技術標準
- データベース技術標準
- インフラ技術標準
- 外部サービス統合ガイドライン

**使用タイミング**:
- ✅ プロジェクト開始時（技術選定）
- ✅ 新規ライブラリ導入検討時
- ✅ 技術スタック更新時

**具体的な活用方法**:
1. プロジェクト要件を分析
2. 承認済み技術一覧から選択
3. バージョン・互換性を確認

**Devin AI向けプロンプト例**:
```
「05-technology-stack/backend-technologies.mdから、
RESTful API構築に最適なフレームワークを選定してください。
プロジェクト要件：高スループット、低レイテンシ、非同期処理サポート。」
```

**ディレクトリREADME**: [05-technology-stack/README.md](./05-technology-stack/)

---

### ⚙️ 06-tools-and-environment (Tier 3)

**目的**: 統一された開発環境と自動化

**提供内容**:
- 開発ツール標準
- CI/CDパイプライン設定
- 監視・ログ管理標準
- 環境管理標準
- コンテナ化標準

**使用タイミング**:
- ✅ 開発環境セットアップ時
- ✅ CI/CD構築時
- ✅ デプロイメント設定時

**具体的な活用方法**:
1. CI/CDテンプレート取得
2. 環境別設定を適用
3. 監視・ログ設定を実装

**Devin AI向けプロンプト例**:
```
「06-tools-and-environment/ci-cd-pipeline.mdに基づいて、
GitHub Actionsワークフローを作成してください。
ビルド、テスト、静的解析、本番デプロイを含めてください。」
```

**ディレクトリREADME**: [06-tools-and-environment/README.md](./06-tools-and-environment/)

---

### 🔒 07-security-compliance (Tier 2)

**目的**: セキュアで法規制準拠のシステム構築

**提供内容**:
- データ保護標準（GDPR/CCPA）
- 認証・認可標準
- 脆弱性管理標準
- コンプライアンス要件
- インシデント対応プロセス

**使用タイミング**:
- ✅ 認証機能実装時
- ✅ データ処理機能実装時
- ✅ セキュリティレビュー時

**具体的な活用方法**:
1. セキュリティ要件を確認
2. 標準パターンで実装
3. 脆弱性スキャン実施

**Devin AI向けプロンプト例**:
```
「07-security-compliance/authentication-authorization.mdに基づいて、
OAuth 2.0認証フローを実装してください。
トークン管理、リフレッシュフロー、セキュアストレージを含めてください。」
```

**ディレクトリREADME**: [07-security-compliance/README.md](./07-security-compliance/)

---

### 📋 08-templates (Tier 4)

**目的**: プロジェクト開始の効率化

**提供内容**:
- プロジェクトテンプレート（初期化用）
- ドキュメントテンプレート
- デプロイメントテンプレート
- ※優先度版（3/5完成）

**使用タイミング**:
- ✅ プロジェクト初期化時
- ✅ 新規ドキュメント作成時
- ✅ デプロイ設定作成時

**具体的な活用方法**:
1. プロジェクトタイプに応じたテンプレート選択
2. プレースホルダー置換
3. カスタマイズ

**Devin AI向けプロンプト例**:
```
「08-templates/project-templates/から'microservice-nodejs'テンプレートを使用して、
プロジェクト名'NotificationService'で初期化してください。
全プレースホルダーを適切な値に置換してください。」
```

**ディレクトリREADME**: [08-templates/README.md](./08-templates/)

---

### 📖 09-reference (Tier 4)

**目的**: 迅速な情報参照と問題解決

**提供内容**:
- 用語集（500+用語）
- クイックリファレンス（コマンド・設定）
- エスカレーションマトリックス
- 意思決定マトリックス
- チェックリスト集

**使用タイミング**:
- ✅ 不明な用語遭遇時
- ✅ コマンド忘れ時
- ✅ 技術選定時
- ✅ 問題発生時

**具体的な活用方法**:
1. 用語集で定義確認
2. クイックリファレンスでコマンド検索
3. 意思決定マトリックスで選択支援

**Devin AI向けプロンプト例**:
```
「09-reference/decision-matrix.mdのデータベース選定マトリックスを使用して、
現在のプロジェクト要件に最適なデータベースを評価・推奨してください。」
```

**ディレクトリREADME**: [09-reference/README.md](./09-reference/)

---

### 🏛️ 10-governance (Tier 4)

**目的**: 標準の維持・改善・ガバナンス

**提供内容**:
- 標準ガバナンスフレームワーク
- 例外承認プロセス
- 標準更新管理プロセス
- コンプライアンス監査プロセス

**使用タイミング**:
- ✅ 標準逸脱が必要な場合
- ✅ 標準の曖昧さ発見時
- ✅ 改善提案時

**具体的な活用方法**:
1. 例外申請フォーム提出
2. 標準更新提案
3. コンプライアンス自己監査

**Devin AI向けプロンプト例**:
```
「10-governance/exception-process.mdに基づいて、
パフォーマンス要件のためコーディング規約を逸脱する例外申請を作成してください。
リスク評価、代替案、承認者を含めてください。」
```

**ディレクトリREADME**: [10-governance/README.md](./10-governance/)

---

## 🚀 クイックスタート

### 初めての使用

```
ステップ1: このファイル（AI-USAGE-GUIDE.md）を読む
         ↓
ステップ2: 🆕 AI-QUICK-REFERENCE.md で数値基準を確認（最優先）
         ↓
ステップ3: 🆕 AI-CODING-INSTRUCTIONS.md で詳細を理解
         ↓
ステップ4: AI-WORKFLOWS.md の「プロジェクト初期化フェーズ」を読む
         ↓
ステップ5: AI-PROMPTS.md / 🆕 AI-PROMPT-TEMPLATES.md から該当するプロンプト例を参照
         ↓
ステップ6: 実装
         ↓
ステップ7: AI-CHECKLISTS.md で自己検証
```

### 🆕 コーディング規約クイックスタート（v1.1.0追加）

**すぐにコードを書く場合の最短経路**:
```
1. AI-QUICK-REFERENCE.md（7.3KB）← まずここを読む！
   □ 必須チェック項目（4項目）
   □ 数値基準表（6メトリクス）
   □ ドキュメンテーション必須レベル
   ↓
2. コード生成
   ↓
3. セルフチェック（AI-QUICK-REFERENCE.mdのチェックリスト）
   ↓
4. 問題があれば AI-CODING-INSTRUCTIONS.md で詳細確認
```

### タスク別クイックリファレンス

| やりたいこと | 参照するセクション | 具体的なプロンプト例 |
|------------|------------------|-------------------|
| **🆕 コーディング規約確認** | **AI-QUICK-REFERENCE.md** | **数値基準・チェックリスト** |
| **🆕 コード生成（規約遵守）** | **AI-PROMPT-TEMPLATES.md** | **言語別テンプレート** |
| 新規プロジェクト開始 | 08-templates, 05-technology-stack | [AI-PROMPTS.md](./AI-PROMPTS.md)#プロジェクト初期化 |
| コード生成 | 01-coding-standards | [AI-PROMPTS.md](./AI-PROMPTS.md)#コード生成 |
| API設計 | 02-architecture-standards | [AI-PROMPTS.md](./AI-PROMPTS.md)#API設計 |
| テスト作成 | 04-quality-standards | [AI-PROMPTS.md](./AI-PROMPTS.md)#テスト生成 |
| CI/CD構築 | 06-tools-and-environment | [AI-PROMPTS.md](./AI-PROMPTS.md)#CI/CD設定 |
| セキュリティ実装 | 07-security-compliance | [AI-PROMPTS.md](./AI-PROMPTS.md)#セキュリティ実装 |
| デプロイ | 06-tools-and-environment, 08-templates | [AI-PROMPTS.md](./AI-PROMPTS.md)#デプロイメント |
| 品質検証 | 04-quality-standards | [AI-CHECKLISTS.md](./AI-CHECKLISTS.md)#品質チェック |
| **🆕 リファクタリング** | **AI-PROMPT-TEMPLATES.md** | **リファクタリング用テンプレート** |
| **🆕 コードレビュー** | **AI-PROMPT-TEMPLATES.md** | **レビュー用テンプレート** |

---

## 🔗 関連ドキュメント

### 🆕 コーディング規約関連（v1.1.0追加）
- **[AI-QUICK-REFERENCE.md](./AI-QUICK-REFERENCE.md)**: 数値基準・チェックリスト要約版（最優先）
- **[AI-CODING-INSTRUCTIONS.md](./AI-CODING-INSTRUCTIONS.md)**: コーディング規約の詳細参照指示
- **[AI-PROMPT-TEMPLATES.md](./AI-PROMPT-TEMPLATES.md)**: コード生成・リファクタリング・レビュー用テンプレート

### 詳細ガイド
- **[AI-WORKFLOWS.md](./AI-WORKFLOWS.md)**: フェーズ別の詳細ワークフロー
- **[AI-PROMPTS.md](./AI-PROMPTS.md)**: 20+の実践プロンプト例
- **[AI-CHECKLISTS.md](./AI-CHECKLISTS.md)**: 自己検証チェックリスト

### メインドキュメント
- **[README.md](./README.md)**: リポジトリ全体概要

### ディレクトリREADME（全10セクション）
- [01-coding-standards/README.md](./01-coding-standards/)
- [02-architecture-standards/README.md](./02-architecture-standards/)
- [03-development-process/README.md](./03-development-process/)
- [04-quality-standards/README.md](./04-quality-standards/)
- [05-technology-stack/README.md](./05-technology-stack/)
- [06-tools-and-environment/README.md](./06-tools-and-environment/)
- [07-security-compliance/README.md](./07-security-compliance/)
- [08-templates/README.md](./08-templates/)
- [09-reference/README.md](./09-reference/)
- [10-governance/README.md](./10-governance/)

---

## 📝 重要な原則

### Devin AIとして守るべき原則

1. **標準優先**: 常に標準を参照してから実装
2. **🆕 数値基準遵守**: 保守性メトリクスの絶対上限を厳守
3. **🆕 ドキュメント必須**: Level 1（ファイル・パブリッククラス・パブリックAPI）は必ず記載
4. **自己検証**: チェックリストで品質確認
5. **一貫性**: プロジェクト全体で統一された実装
6. **文書化**: 決定理由と実装の記録
7. **継続改善**: フィードバックと学習

### 🆕 コード生成時の必須確認事項（v1.1.0追加）

**すべてのコード生成時に以下を確認**:
```
□ 関数は50行以内か？（絶対上限100行）
□ 複雑度は10以下か？（絶対上限15）
□ ネスト深さは3階層以内か？（絶対上限4階層）
□ パラメータは5個以内か？（絶対上限7個）
□ パブリックAPIにコメントがあるか？（JSDoc/Docstring/Javadoc）
□ ファイルヘッダーがあるか？
```

**絶対上限超過時の対応**:
- 🔴 **即座にリファクタリング**: 関数分割、クラス分割、条件分岐の単純化
- 🔴 **早期返却パターン**: ネスト深さ削減
- 🔴 **オブジェクト化**: パラメータ数削減

### エスカレーション基準

以下の場合は人間にエスカレーション：
- ❌ 標準に明確な記載がない場合
- ❌ 標準間で矛盾がある場合
- ❌ ビジネス判断が必要な場合
- ❌ 標準逸脱が必要な場合
- 🆕 ❌ **絶対上限超過がやむを得ない場合**（例外承認プロセスが必要）

→ [10-governance/exception-process.md](./10-governance/) を参照

---

## 🎯 次のステップ

### 🆕 1. コーディング規約を理解（v1.1.0追加・最優先）
→ **[AI-QUICK-REFERENCE.md](./AI-QUICK-REFERENCE.md)** で数値基準を確認（7.3KB・5分で読める）
→ **[AI-CODING-INSTRUCTIONS.md](./AI-CODING-INSTRUCTIONS.md)** で詳細を理解（15.4KB）
→ **[AI-PROMPT-TEMPLATES.md](./AI-PROMPT-TEMPLATES.md)** でテンプレート確認（13.3KB）

### 2. 詳細ワークフローを学ぶ
→ **[AI-WORKFLOWS.md](./AI-WORKFLOWS.md)** に進む

### 3. 実践プロンプトを確認
→ **[AI-PROMPTS.md](./AI-PROMPTS.md)** で具体例を見る

### 4. 自己検証方法を理解
→ **[AI-CHECKLISTS.md](./AI-CHECKLISTS.md)** でチェックリストを確認

---

## 🆕 更新履歴（v1.1.0）

### v1.1.0 (2025-10-23) - 保守性メトリクスとドキュメンテーション標準の追加

**追加内容**:
1. **保守性メトリクス**
   - 関数/メソッド行数（理想20行、推奨上限50行、絶対上限100行）
   - クラス/ファイル行数（理想200行、推奨上限400行、絶対上限1000行）
   - 循環的複雑度（理想5、推奨上限10、絶対上限15）
   - 認知的複雑度（理想5、推奨上限15、絶対上限25）
   - ネスト深さ（理想2階層、推奨上限3階層、絶対上限4階層）
   - パラメータ数（理想3個、推奨上限5個、絶対上限7個）

2. **ドキュメンテーション必須レベル**
   - Level 1（必須）: ファイル・パブリッククラス・パブリックAPI
   - Level 2（推奨）: 複雑なロジック、ビジネスルール
   - Level 3（任意）: 単純な処理（自己文書化優先）

3. **言語別ドキュメンテーション標準**
   - TypeScript/JavaScript: JSDoc標準・ESLint設定
   - Python: Google Style Docstring・Pylint設定
   - Java: Javadoc標準・CheckStyle設定
   - SQL: SQLコメント標準
   - CSS/SCSS: BEM命名規則・Stylelint設定

4. **AI開発者向けドキュメント**
   - AI-CODING-INSTRUCTIONS.md（15.4KB）: 詳細参照指示
   - AI-QUICK-REFERENCE.md（7.3KB）: クイックリファレンス
   - AI-PROMPT-TEMPLATES.md（13.3KB）: プロンプトテンプレート集

**変更されたセクション**:
- 01-coding-standards: 保守性メトリクス、ドキュメンテーション標準を追加
- 全言語規約ファイル（5ファイル）: ドキュメンテーション標準セクションを追加

**影響範囲**:
- すべてのコード生成・修正タスク
- コードレビュープロセス
- 品質ゲート基準

---

**最終更新**: 2025-10-23  
**バージョン**: 1.1.0  
**管理者**: 標準化委員会

**注**: このガイドは継続的に更新されます。最新版を常に参照してください。
