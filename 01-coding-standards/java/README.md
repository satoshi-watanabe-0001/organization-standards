# Java Coding Standards

**対象**: Java 17+、Spring Boot 3.0+、Maven/Gradle  
**最終更新**: 2025-11-13  
**元ファイル**: `java-standards.md` (130.9 KB) → 7ファイル（平均18.8 KB、削減率89%）

---

## 📋 概要

本規約は、AIを活用したJava開発プロセスにおける標準的なコーディング規約、設計原則、テスト戦略、セキュリティ対策、運用ガイドラインを定義します。Java 17以上、Spring Boot 3.0以上を対象とし、Devin等のAIエージェントが高品質なコードを生成するための詳細な指針を提供します。

**主要スコープ**:
- 開発環境のセットアップとツールチェーン
- 命名規則とコードスタイル
- クラス設計とアーキテクチャパターン
- エラーハンドリングとバリデーション
- テスト戦略と品質保証（カバレッジ80%以上）
- パフォーマンス最適化とセキュリティ
- 監視、ロギング、運用（Docker、CI/CD）
- Devin向け実装指示とドキュメンテーション標準

---

## 📁 ドキュメント構成

### 1. [Introduction & Setup](01-introduction-setup.md)（6.3 KB）
**本規約の目的、基本設定、ツールチェーン、開発環境のセットアップ**

- 規約の目的とスコープ
- 推奨ツールチェーン（Java 17+, Spring Boot 3.0+）
- Gradle設定（`build.gradle`）
- Checkstyle、SpotBugs、JaCoCo設定
- 開発環境の標準化

**いつ読む**: プロジェクト開始時、開発環境セットアップ時

---

### 2. [Naming Conventions & Code Style](02-naming-style.md)（5.1 KB）
**命名規則、コードフォーマット、スタイルガイドライン**

- パッケージ、クラス、メソッド、変数の命名規則
- インデント、改行、空白のルール
- Google Java Style Guideの適用
- フォーマットの自動化設定

**いつ読む**: コード記述前、レビュー前、スタイル違反が発生した時

---

### 3. [Class Design & Architecture](03-class-design-architecture.md)（13.2 KB）
**クラス設計原則、レイヤー設計、依存性注入**

- 単一責任原則（SRP）の適用
- 依存性注入（DI）の活用（Spring Framework）
- Controller、Service、Repository層の設計
- レイヤー間の責務分離
- DTO、Entity、Request/Responseの適切な使用

**いつ読む**: クラス設計時、アーキテクチャレビュー時、リファクタリング時

---

### 4. [Error Handling & Validation](04-error-handling-validation.md)（28.9 KB）
**例外処理、バリデーション、セキュリティ対策**

- カスタム例外の作成（`BusinessException`、`ResourceNotFoundException`等）
- グローバル例外ハンドラー（`@ControllerAdvice`）
- Bean Validationの活用（`@Valid`、`@Validated`）
- 認証・認可の実装（Spring Security）
- SQLインジェクション、XSS対策
- データサニタイゼーション

**いつ読む**: エラーハンドリング実装時、バリデーション追加時、セキュリティレビュー時

---

### 5. [Testing Strategy & Quality Assurance](05-testing-quality.md)（17.6 KB）
**テスト戦略、単体テスト、統合テスト、品質保証**

- JUnit 5とMockitoの活用
- 単体テスト（Unit Test）のベストプラクティス
- 統合テスト（Integration Test）
- Spring Boot Testの活用（`@SpringBootTest`、`@WebMvcTest`等）
- テストカバレッジ基準（80%以上）
- テスト専用設定とユーティリティ

**いつ読む**: テスト実装時、テストカバレッジ改善時、品質保証プロセス確立時

---

### 6. [Performance, Security & Operations](06-performance-security-operations.md)（37.2 KB）
**パフォーマンス最適化、セキュリティ、監視、運用**

- JPA/Hibernate最適化（N+1問題対策、遅延ロード等）
- キャッシング戦略（`@Cacheable`、Redis）
- 非同期処理・並行処理（`@Async`、`CompletableFuture`）
- JWT実装のベストプラクティス
- データ保護・暗号化

---

### 💬 コメント規約（2025-11-14追加）✨

| ファイル | サイズ | 内容 |
|---------|-------|------|
| [java-inline-comment-examples.md](java-inline-comment-examples.md) | 16.6 KB | Java固有のインラインコメント実装例（Stream API、アノテーション、Optional等） |
| [java-test-comment-examples.md](java-test-comment-examples.md) | 23.6 KB | JUnit 5/Mockitoスタイルのテストコメント実装例 |

**共通原則も参照**:
- [00-inline-comment-standards.md](../00-inline-comment-standards.md) - インラインコメント共通原則
- [00-test-comment-standards.md](../00-test-comment-standards.md) - テストコメント共通原則

**いつ読む**: コード記述時、コードレビュー時、テスト実装時
- 構造化ロギング（Logback）
- Docker化・コンテナ運用（Dockerfile、docker-compose.yml）
- CI/CD設定（GitHub Actions）

**いつ読む**: パフォーマンス問題発生時、セキュリティレビュー時、本番デプロイ準備時

---

### 7. [Devin Guidelines & Documentation Standards](07-devin-documentation.md)（25.0 KB）
**Devin実行ガイドライン、ドキュメンテーション標準**

- Devin向け実装指示
- 自動コード生成指針
- 品質保証チェックリスト
- Javadoc標準形式（パッケージ、クラス、メソッド）
- README、API仕様書の作成
- ドキュメント管理のベストプラクティス

**いつ読む**: Devinにタスクを指示する時、ドキュメント作成時、コードレビュー時

---

## 🚀 クイックスタートガイド

### プロジェクト開始時のチェックリスト
1. **[01-introduction-setup.md](01-introduction-setup.md)** でツールチェーンをセットアップ
2. **[02-naming-style.md](02-naming-style.md)** で命名規則を確認
3. **[03-class-design-architecture.md](03-class-design-architecture.md)** でアーキテクチャパターンを理解
4. **[AI-QUICK-REFERENCE.md](AI-QUICK-REFERENCE.md)** で必須チェック項目TOP30を確認

### コード実装時のチェックリスト
1. **命名規則**: [02-naming-style.md](02-naming-style.md) に従っているか
2. **クラス設計**: [03-class-design-architecture.md](03-class-design-architecture.md) の単一責任原則、DI原則に従っているか
3. **エラーハンドリング**: [04-error-handling-validation.md](04-error-handling-validation.md) でカスタム例外、グローバルハンドラーを実装したか
4. **テスト**: [05-testing-quality.md](05-testing-quality.md) で80%以上のカバレッジを達成したか

### コードレビュー時のチェックリスト
1. **[AI-QUICK-REFERENCE.md](AI-QUICK-REFERENCE.md)** の必須チェック項目TOP30を確認
2. **セキュリティ**: [04-error-handling-validation.md](04-error-handling-validation.md) のSQLインジェクション、XSS対策
3. **パフォーマンス**: [06-performance-security-operations.md](06-performance-security-operations.md) のJPA最適化、キャッシング
4. **ドキュメント**: [07-devin-documentation.md](07-devin-documentation.md) のJavadoc標準

---

## 💡 AI活用のヒント

### Devinへの指示例
```
以下の規約に厳密に従って、ユーザー管理APIのServiceクラスを実装してください：
- 規約: /devin-organization-standards/01-coding-standards/java/
- 必須確認: AI-QUICK-REFERENCE.mdのチェック項目TOP30
- 重点: クラス設計（SRP、DI）、エラーハンドリング（カスタム例外、グローバルハンドラー）、テスト（カバレッジ80%以上）
```

### AIレビューの活用
```
以下のコードをJava規約に基づいてレビューしてください：
- 規約: /devin-organization-standards/01-coding-standards/java/
- チェック項目: 命名規則、例外処理、バリデーション、Javadoc
- 出力: 違反項目リスト、修正提案、リファクタリング案
```

---

## 📊 分割効果

| 項目 | 分割前 | 分割後 | 効果 |
|------|--------|--------|------|
| **ファイル数** | 1 | 7 | +600% |
| **最大ファイルサイズ** | 130.9 KB | 37.2 KB | -72% |
| **平均ファイルサイズ** | 130.9 KB | 18.8 KB | -86% |
| **AI読み込み効率** | 低 | 高 | - |
| **検索性** | 低 | 高 | - |

**削減率**: **89%**（130.9 KB → 平均18.8 KB）

---

## 🔗 関連ドキュメント

- **[AI-QUICK-REFERENCE.md](AI-QUICK-REFERENCE.md)**: AI用必須チェック項目TOP30（3分で確認）
- **SQL Standards**: `/devin-organization-standards/01-coding-standards/sql/`
- **CSS Standards**: `/devin-organization-standards/01-coding-standards/css/`
- **API Architecture Standards**: `/devin-organization-standards/02-architecture-standards/api/`

---

## 📝 更新履歴

- **2025-11-13**: 初版作成、7ファイルに分割（130.9 KB → 平均18.8 KB、削減率89%）

---

**ナビゲーションのヒント**:
- **トピック別検索**: 各ドキュメントの「主要トピック」セクションを参照
- **段階的学習**: 1→2→3→4→5→6→7の順に読むことを推奨
- **問題解決**: 特定の問題が発生した時は、該当ドキュメントの目次から直接ジャンプ
