# E2Eテスト標準 (End-to-End Testing Standards)

> **Phase 7完了** - Document Restructuring Project  
> 元ファイル (67.7 KB) を5つの管理しやすいファイルに分割

## 📚 概要

このディレクトリには、エンドツーエンド(E2E)テストの設計、実装、実行、保守に関する包括的な標準が含まれています。ユーザー視点での統合テストを通じて、システム全体の品質を保証します。

## 🗂️ ファイル構成

| ファイル | サイズ | 内容 | 対象者 |
|---------|--------|------|--------|
| **[01-principles-scope.md](01-principles-scope.md)** | 5.7 KB | 基本原則、テストスコープ | 全員 |
| **[02-test-design-userflow.md](02-test-design-userflow.md)** | 15 KB | テスト設計戦略、ユーザーフロー定義 | QA、テスト設計者 |
| **[03-implementation-testdata.md](03-implementation-testdata.md)** | 23 KB | 実装パターン、テストデータ管理 | 開発者、QA |
| **[04-environment-performance.md](04-environment-performance.md)** | 17 KB | 環境構築、CI/CD、パフォーマンス | DevOps、SRE |
| **[05-troubleshooting-devin.md](05-troubleshooting-devin.md)** | 8.9 KB | トラブルシューティング、Devinガイド | 全員 |
| **[AI-QUICK-REFERENCE.md](AI-QUICK-REFERENCE.md)** | - | 必須チェック項目TOP20 | AI/人間 |

**合計**: 69.6 KB（5ファイル + README + AI-QUICK-REFERENCE）

## 🚀 クイックスタート

### 新規プロジェクトでE2Eテストを開始する場合
1. **[01-principles-scope.md](01-principles-scope.md)** - E2Eテストの基本原則とスコープ
2. **[02-test-design-userflow.md](02-test-design-userflow.md)** - ユーザーフローの定義と設計
3. **[03-implementation-testdata.md](03-implementation-testdata.md)** - 実装パターンとツール選定

### 既存プロジェクトでE2Eテストを改善したい場合
1. **[04-environment-performance.md](04-environment-performance.md)** - CI/CD統合と安定性向上
2. **[05-troubleshooting-devin.md](05-troubleshooting-devin.md)** - よくある問題と解決策

### トラブルシューティング
- **[05-troubleshooting-devin.md](05-troubleshooting-devin.md)** - フレーキーテスト、タイムアウト対策

## 📖 使い方ガイド

### 役割別推奨読書順序

#### **QAエンジニア**
1. 01-principles-scope.md（基本理解）
2. 02-test-design-userflow.md（テスト設計）★
3. 03-implementation-testdata.md（実装）★
4. 05-troubleshooting-devin.md（問題解決）

#### **開発者**
1. 01-principles-scope.md（概要把握）
2. 03-implementation-testdata.md（実装パターン）★
3. 04-environment-performance.md（CI/CD統合）
4. 05-troubleshooting-devin.md（デバッグ）

#### **DevOps/SREエンジニア**
1. 04-environment-performance.md（環境構築）★
2. 04-environment-performance.md（CI/CD統合）★
3. 05-troubleshooting-devin.md（安定性向上）

#### **AI開発アシスタント（Devin等）**
1. AI-QUICK-REFERENCE.md（最優先）
2. 05-troubleshooting-devin.md の「Devin AIガイドライン」
3. タスクに応じた該当ファイル

## 🎯 主要トピック

### テスト設計
- **[02-test-design-userflow.md](02-test-design-userflow.md)** - ユーザーストーリーからテストケースへ

### 実装ツール
- **[03-implementation-testdata.md](03-implementation-testdata.md)** - Playwright、Cypress、Selenium

### CI/CD統合
- **[04-environment-performance.md](04-environment-performance.md)** - GitHub Actions、GitLab CI

### フレーキーテスト対策
- **[05-troubleshooting-devin.md](05-troubleshooting-devin.md)** - 不安定なテストの安定化

## ⚡ 重要ルール（TOP 5）

1. **クリティカルパスを優先** - すべてをテストせず、重要なユーザーフローに集中
2. **テストの独立性** - 各テストは他のテストに依存しない
3. **適切な待機戦略** - `sleep`ではなくexplicit waitを使用
4. **Page Objectパターン** - UIの変更に強い構造
5. **CI/CDで実行** - 毎回のコミットで自動実行

## 🔗 関連ドキュメント

- [unit-testing.md](../unit-testing.md) - ユニットテスト標準
- [integration-testing.md](../integration-testing.md) - 統合テスト標準
- [test-data-management.md](./test-data-management/) - テストデータ管理

## 📝 改訂履歴

- **2025-11-13**: Phase 7完了 - 67.7 KB → 5ファイルに分割
- **元ドキュメント**: `_archive/e2e-testing_archived_2025-11-13.md`

---

**Document Restructuring Project** - Phase 7  
分割実行日: 2025-11-13  
元ファイルサイズ: 67.7 KB → 分割後: 69.6 KB (5ファイル)
