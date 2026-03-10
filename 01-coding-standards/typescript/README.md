# TypeScript/JavaScript コーディング規約

> **Phase 6 完了** - Document Restructuring Project  
> 元ファイル (60.2 KB) を7つの管理しやすいファイルに分割

## 📚 概要

このディレクトリには、TypeScript/JavaScriptプロジェクト（React、Node.js含む）における包括的なコーディング規約が含まれています。フロントエンドとバックエンド両方の開発をカバーし、型安全性、パフォーマンス、保守性を重視した実践的なガイドラインを提供します。

## 🗂️ ファイル構成

| ファイル | サイズ | 内容 | 対象者 |
|---------|--------|------|--------|
| **[01-introduction-setup.md](01-introduction-setup.md)** | 3.5 KB | 基本設定、ツール設定、tsconfig、ESLint、Prettier | 全員 |
| **[02-language-syntax.md](02-language-syntax.md)** | 5.0 KB | 言語仕様、構文規約、型アノテーション | 全員 |
| **[03-naming-typing.md](03-naming-typing.md)** | 7.7 KB | 命名規則、型定義、インターフェース | 全員 |
| **[04-react-frontend.md](04-react-frontend.md)** | 7.6 KB | React固有規約、Hooks、コンポーネント設計 | フロントエンド |
| **[05-nodejs-backend.md](05-nodejs-backend.md)** | 7.3 KB | Node.js固有規約、Express、非同期処理 | バックエンド |
| **[06-testing.md](06-testing.md)** | 8.2 KB | テスト規約、Jest、React Testing Library | 全員 |
| **[07-performance-devin-docs.md](07-performance-devin-docs.md)** | 21.3 KB | パフォーマンス最適化、Devinガイド、ドキュメンテーション | 全員 |
| **[typescript-inline-comment-examples.md](typescript-inline-comment-examples.md)** | 9.8 KB | TypeScript固有のインラインコメント実装例 | 全員 |
| **[typescript-test-comment-examples.md](typescript-test-comment-examples.md)** | 14.5 KB | Jestスタイルのテストコメント実装例 | 全員 |
| **[AI-QUICK-REFERENCE.md](AI-QUICK-REFERENCE.md)** | - | 必須チェック項目TOP25 | AI/人間 |

**共通原則も参照**: [00-inline-comment-standards.md](../00-inline-comment-standards.md) | [00-test-comment-standards.md](../00-test-comment-standards.md)

**合計**: 60.6 KB（7ファイル + README + AI-QUICK-REFERENCE）

## 🚀 クイックスタート

### 新規プロジェクトを始める場合
1. **[01-introduction-setup.md](01-introduction-setup.md)** - tsconfig.json、ESLint、Prettierの設定
2. **[AI-QUICK-REFERENCE.md](AI-QUICK-REFERENCE.md)** - 必須チェック項目を確認
3. 開発環境に応じて:
   - フロントエンド → **[04-react-frontend.md](04-react-frontend.md)**
   - バックエンド → **[05-nodejs-backend.md](05-nodejs-backend.md)**

### コードレビュー時
1. **[AI-QUICK-REFERENCE.md](AI-QUICK-REFERENCE.md)** - チェックリスト確認
2. **[03-naming-typing.md](03-naming-typing.md)** - 命名・型定義の確認
3. 該当分野の詳細ドキュメント参照

### パフォーマンス問題がある場合
- **[07-performance-devin-docs.md](07-performance-devin-docs.md)** のパフォーマンスセクション

## 📖 使い方ガイド

### 役割別推奨読書順序

#### **フロントエンド開発者**
1. 01-introduction-setup.md（基本設定）
2. 02-language-syntax.md（TypeScript構文）
3. 03-naming-typing.md（命名・型定義）
4. 04-react-frontend.md（React規約）★
5. 06-testing.md（テスト）
6. 07-performance-devin-docs.md（最適化）

#### **バックエンド開発者**
1. 01-introduction-setup.md（基本設定）
2. 02-language-syntax.md（TypeScript構文）
3. 03-naming-typing.md（命名・型定義）
4. 05-nodejs-backend.md（Node.js規約）★
5. 06-testing.md（テスト）
6. 07-performance-devin-docs.md（最適化）

#### **フルスタック開発者**
全ファイルを順番に読むことを推奨（01→07）

#### **AI開発アシスタント（Devin等）**
1. AI-QUICK-REFERENCE.md（最優先）
2. typescript-inline-comment-examples.md + typescript-test-comment-examples.md（コメント規約）
3. 07-performance-devin-docs.md の「Devin向け実行ガイドライン」
4. タスクに応じた該当ファイル

## 🎯 主要トピック

### 型安全性
- **[02-language-syntax.md](02-language-syntax.md)** - `any`禁止、strict mode
- **[03-naming-typing.md](03-naming-typing.md)** - 型定義、ジェネリクス

### React開発
- **[04-react-frontend.md](04-react-frontend.md)** - Hooks、メモ化、状態管理

### Node.js開発
- **[05-nodejs-backend.md](05-nodejs-backend.md)** - 非同期処理、Express、エラーハンドリング

### テスト戦略
- **[06-testing.md](06-testing.md)** - ユニット、統合、E2E、カバレッジ

### パフォーマンス
- **[07-performance-devin-docs.md](07-performance-devin-docs.md)** - バンドル最適化、遅延読み込み

## ⚡ 重要ルール（TOP 5）

1. **`any`型禁止** - `unknown`または適切な型を使用
2. **strictモード必須** - tsconfig.jsonで`"strict": true`
3. **非同期処理は`async/await`** - `.then()`チェーンを避ける
4. **命名規則統一** - PascalCase（型）、camelCase（変数・関数）
5. **型アノテーションを明示** - 推論に頼らない

## 🔗 関連ドキュメント

- [00-general-principles.md](../00-general-principles.md) - 全言語共通の原則
- [css-styling-standards.md](../css-styling-standards.md) - CSSスタイリング規約
- [../../02-architecture-standards/frontend/](../../02-architecture-standards/frontend/) - フロントエンドアーキテクチャ

## 📝 改訂履歴

- **2025-11-13**: Phase 6完了 - 60.2 KB → 7ファイルに分割
- **元ドキュメント**: `_archive/typescript-javascript-standards_archived_2025-11-13.md`

## 💡 フィードバック

このドキュメントへのフィードバックや改善提案は、プロジェクトリポジトリのIssueまたはPull Requestで受け付けています。

---

**Document Restructuring Project** - Phase 6  
分割実行日: 2025-11-13  
元ファイルサイズ: 60.2 KB → 分割後: 60.6 KB (7ファイル)
