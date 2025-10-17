# Devin AI ドキュメント活用ガイド

## 📋 このガイドについて

このガイドは、**Devin AI**（および類似のAI開発システム）が本標準リポジトリを効果的に活用し、**完全自律的な開発**を実現するための包括的な指針です。

### ガイド構成

本ガイドは4つのドキュメントで構成されています：

| ドキュメント | 目的 | サイズ | 用途 |
|------------|------|--------|------|
| **[AI-USAGE-GUIDE.md](./AI-USAGE-GUIDE.md)** (このファイル) | 全体概要・ナビゲーション | 約10 KB | 最初に読む、セクション別ガイド |
| **[AI-WORKFLOWS.md](./AI-WORKFLOWS.md)** | 詳細ワークフロー | 約12 KB | フェーズ別の詳細手順 |
| **[AI-PROMPTS.md](./AI-PROMPTS.md)** | 実践プロンプト例集 | 約15 KB | 具体的な指示例20+ |
| **[AI-CHECKLISTS.md](./AI-CHECKLISTS.md)** | 検証チェックリスト | 約10 KB | 自己検証・品質保証 |

### 推奨読み方

```
1. AI-USAGE-GUIDE.md (このファイル) ← まずここから
   ↓
2. 開発フェーズに応じて：
   - プロジェクト開始時 → AI-WORKFLOWS.md (初期化フェーズ)
   - 開発中 → AI-PROMPTS.md (該当するプロンプト例)
   - 検証時 → AI-CHECKLISTS.md (該当するチェックリスト)
   ↓
3. 随時このファイルに戻ってセクション別ガイドを参照
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

### 📁 01-coding-standards (Tier 1)

**目的**: 一貫した高品質なコード作成

**提供内容**:
- 7言語/フレームワークの詳細コーディング規約
  - TypeScript/JavaScript (49.9 KB)
  - Python (453.3 KB) - 最も詳細
  - Java (116.1 KB)
  - SQL (149.1 KB)
  - CSS (122.8 KB)
- 全言語共通の設計原則 (16.9 KB)

**使用タイミング**:
- ✅ コード生成前（毎回）
- ✅ コードレビュー時
- ✅ リファクタリング時

**具体的な活用方法**:
1. プロジェクトで使用する言語の標準を読み込む
2. コード生成時に標準のパターンを適用
3. 自己検証チェックリストで確認

**Devin AI向けプロンプト例**:
```
「01-coding-standards/python-standards.mdに準拠して、
ユーザー認証機能を実装してください。
PEP 8準拠、型ヒント使用、適切なエラーハンドリングを含めてください。」
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
ステップ2: AI-WORKFLOWS.md の「プロジェクト初期化フェーズ」を読む
         ↓
ステップ3: AI-PROMPTS.md から該当するプロンプト例を参照
         ↓
ステップ4: 実装
         ↓
ステップ5: AI-CHECKLISTS.md で自己検証
```

### タスク別クイックリファレンス

| やりたいこと | 参照するセクション | 具体的なプロンプト例 |
|------------|------------------|-------------------|
| 新規プロジェクト開始 | 08-templates, 05-technology-stack | [AI-PROMPTS.md](./AI-PROMPTS.md)#プロジェクト初期化 |
| コード生成 | 01-coding-standards | [AI-PROMPTS.md](./AI-PROMPTS.md)#コード生成 |
| API設計 | 02-architecture-standards | [AI-PROMPTS.md](./AI-PROMPTS.md)#API設計 |
| テスト作成 | 04-quality-standards | [AI-PROMPTS.md](./AI-PROMPTS.md)#テスト生成 |
| CI/CD構築 | 06-tools-and-environment | [AI-PROMPTS.md](./AI-PROMPTS.md)#CI/CD設定 |
| セキュリティ実装 | 07-security-compliance | [AI-PROMPTS.md](./AI-PROMPTS.md)#セキュリティ実装 |
| デプロイ | 06-tools-and-environment, 08-templates | [AI-PROMPTS.md](./AI-PROMPTS.md)#デプロイメント |
| 品質検証 | 04-quality-standards | [AI-CHECKLISTS.md](./AI-CHECKLISTS.md)#品質チェック |

---

## 🔗 関連ドキュメント

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
2. **自己検証**: チェックリストで品質確認
3. **一貫性**: プロジェクト全体で統一された実装
4. **文書化**: 決定理由と実装の記録
5. **継続改善**: フィードバックと学習

### エスカレーション基準

以下の場合は人間にエスカレーション：
- ❌ 標準に明確な記載がない場合
- ❌ 標準間で矛盾がある場合
- ❌ ビジネス判断が必要な場合
- ❌ 標準逸脱が必要な場合

→ [10-governance/exception-process.md](./10-governance/) を参照

---

## 🎯 次のステップ

### 1. 詳細ワークフローを学ぶ
→ **[AI-WORKFLOWS.md](./AI-WORKFLOWS.md)** に進む

### 2. 実践プロンプトを確認
→ **[AI-PROMPTS.md](./AI-PROMPTS.md)** で具体例を見る

### 3. 自己検証方法を理解
→ **[AI-CHECKLISTS.md](./AI-CHECKLISTS.md)** でチェックリストを確認

---

**最終更新**: 2025-10-17  
**バージョン**: 1.0.0  
**管理者**: 標準化委員会

**注**: このガイドは継続的に更新されます。最新版を常に参照してください。
