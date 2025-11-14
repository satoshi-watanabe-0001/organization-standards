# テストデータ管理標準 (Test Data Management Standards)

> **Phase 7完了** - Document Restructuring Project  
> 元ファイル (63.8 KB) を5つの管理しやすいファイルに分割

## 📚 概要

このディレクトリには、テストデータの戦略、生成、準備、マスキング、ライフサイクル管理に関する包括的な標準が含まれています。適切なテストデータ管理により、テストの効率性、信頼性、コンプライアンスを実現します。

## 🗂️ ファイル構成

| ファイル | サイズ | 内容 | 対象者 |
|---------|--------|------|--------|
| **[01-principles-strategy.md](01-principles-strategy.md)** | 11 KB | 基本原則、テストデータ戦略 | 全員 |
| **[02-privacy-generation.md](02-privacy-generation.md)** | 19 KB | プライバシー、コンプライアンス、データ生成 | QA、セキュリティ |
| **[03-preparation-masking.md](03-preparation-masking.md)** | 24 KB | データ準備、マスキング、匿名化 | 開発者、QA |
| **[04-lifecycle-database.md](04-lifecycle-database.md)** | 8.8 KB | ライフサイクル、データベース管理、最適化 | DBA、DevOps |
| **[05-tools-devin.md](05-tools-devin.md)** | 3.2 KB | ツール、自動化、Devinガイド | 全員 |
| **[AI-QUICK-REFERENCE.md](AI-QUICK-REFERENCE.md)** | - | 必須チェック項目TOP20 | AI/人間 |

**合計**: 66 KB（5ファイル + README + AI-QUICK-REFERENCE）

## 🚀 クイックスタート

### 新規プロジェクトでテストデータ管理を開始する場合
1. **[01-principles-strategy.md](01-principles-strategy.md)** - 基本原則と戦略策定
2. **[02-privacy-generation.md](02-privacy-generation.md)** - GDPR対応とデータ生成方法
3. **[03-preparation-masking.md](03-preparation-masking.md)** - データ準備とマスキング実装

### 本番データを使いたい場合
1. **[02-privacy-generation.md](02-privacy-generation.md)** - プライバシー規制の確認
2. **[03-preparation-masking.md](03-preparation-masking.md)** - マスキング・匿名化の実施

### データ生成を自動化したい場合
- **[05-tools-devin.md](05-tools-devin.md)** - Faker、Mockaroo等のツール活用

## ⚡ 重要ルール（TOP 5）

1. **本番データの直接使用禁止** - 必ずマスキング・匿名化
2. **PII（個人識別情報）の保護** - GDPR、CCPA等の規制遵守
3. **データのバージョン管理** - テスト結果の再現性確保
4. **適切なデータ量** - リアルな規模でテスト
5. **定期的なデータリフレッシュ** - データの陳腐化を防ぐ

## 📝 改訂履歴

- **2025-11-13**: Phase 7完了 - 63.8 KB → 5ファイルに分割
- **元ドキュメント**: `_archive/test-data-management_archived_2025-11-13.md`

---

**Document Restructuring Project** - Phase 7  
分割実行日: 2025-11-13  
元ファイルサイズ: 63.8 KB → 分割後: 66 KB (5ファイル)
