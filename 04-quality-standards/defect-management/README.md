# 欠陥管理標準 (Defect Management Standards)

> **Phase 7完了** - Document Restructuring Project  
> 元ファイル (71.3 KB) を6つの管理しやすいファイルに分割

## 📚 概要

このディレクトリには、ソフトウェア開発における欠陥（バグ・不具合）の検出、報告、トリアージ、解決、分析、予防に関する包括的な標準が含まれています。効率的な欠陥管理プロセスを確立し、製品品質の継続的な向上を実現します。

## 🗂️ ファイル構成

| ファイル | サイズ | 内容 | 対象者 |
|---------|--------|------|--------|
| **[01-principles-lifecycle.md](01-principles-lifecycle.md)** | 9.1 KB | 基本原則、欠陥ライフサイクル | 全員 |
| **[02-classification-reporting.md](02-classification-reporting.md)** | 15 KB | 欠陥の分類、優先度、レポート作成 | 開発者、QA |
| **[03-triage-resolution.md](03-triage-resolution.md)** | 14 KB | トリアージプロセス、解決ワークフロー | PM、リーダー |
| **[04-root-cause-analysis.md](04-root-cause-analysis.md)** | 9.2 KB | 根本原因分析（RCA）、5 Whys、Fishbone | QA、開発者 |
| **[05-metrics-prevention.md](05-metrics-prevention.md)** | 16 KB | メトリクス、レポーティング、予防策 | PM、マネージャー |
| **[06-tools-devin.md](06-tools-devin.md)** | 9.7 KB | ツール、自動化、Devin AIガイドライン | 全員 |
| **[AI-QUICK-REFERENCE.md](AI-QUICK-REFERENCE.md)** | - | 必須チェック項目TOP20 | AI/人間 |

**合計**: 73 KB（6ファイル + README + AI-QUICK-REFERENCE）

## 🚀 クイックスタート

### 新規プロジェクトで欠陥管理を開始する場合
1. **[01-principles-lifecycle.md](01-principles-lifecycle.md)** - 基本原則とライフサイクルを理解
2. **[02-classification-reporting.md](02-classification-reporting.md)** - 分類方法とレポート作成ルール
3. **[AI-QUICK-REFERENCE.md](AI-QUICK-REFERENCE.md)** - 必須チェック項目を確認

### 既存プロジェクトで改善したい場合
1. **[05-metrics-prevention.md](05-metrics-prevention.md)** - 現状のメトリクスを測定
2. **[04-root-cause-analysis.md](04-root-cause-analysis.md)** - 繰り返す欠陥の根本原因を分析
3. **[06-tools-devin.md](06-tools-devin.md)** - 自動化ツールの導入

### トリアージ会議の準備
- **[03-triage-resolution.md](03-triage-resolution.md)** - トリアージ基準と優先度付け

## 📖 使い方ガイド

### 役割別推奨読書順序

#### **開発者**
1. 01-principles-lifecycle.md（基本理解）
2. 02-classification-reporting.md（レポート作成方法）
3. 04-root-cause-analysis.md（RCA実施）
4. 06-tools-devin.md（ツール活用）

#### **QAエンジニア**
1. 01-principles-lifecycle.md（ライフサイクル全体）
2. 02-classification-reporting.md（分類・優先度）★
3. 04-root-cause-analysis.md（分析手法）★
4. 05-metrics-prevention.md（品質メトリクス）

#### **プロジェクトマネージャー**
1. 01-principles-lifecycle.md（プロセス全体）
2. 03-triage-resolution.md（トリアージ管理）★
3. 05-metrics-prevention.md（進捗管理）★
4. 06-tools-devin.md（効率化）

#### **AI開発アシスタント（Devin等）**
1. AI-QUICK-REFERENCE.md（最優先）
2. 06-tools-devin.md の「Devin AIガイドライン」
3. タスクに応じた該当ファイル

## 🎯 主要トピック

### 欠陥ライフサイクル
- **[01-principles-lifecycle.md](01-principles-lifecycle.md)** - New → Assigned → In Progress → Resolved → Closed

### 優先度とSeverity
- **[02-classification-reporting.md](02-classification-reporting.md)** - Critical、High、Medium、Lowの判定基準

### トリアージプロセス
- **[03-triage-resolution.md](03-triage-resolution.md)** - 優先度決定、担当者割り当て

### 根本原因分析
- **[04-root-cause-analysis.md](04-root-cause-analysis.md)** - 5 Whys、Fishbone図、Pareto分析

### 品質メトリクス
- **[05-metrics-prevention.md](05-metrics-prevention.md)** - Defect Density、Escape Rate、MTTR

### ツールと自動化
- **[06-tools-devin.md](06-tools-devin.md)** - Jira、GitHub Issues、自動トリアージ

## ⚡ 重要ルール（TOP 5）

1. **全ての欠陥を記録** - 口頭報告のみは禁止、必ず追跡システムに登録
2. **Severityと優先度は別物** - 影響度（Severity）≠ 修正順序（Priority）
3. **再現手順必須** - 再現できない欠陥は修正不可、詳細な手順を記載
4. **根本原因を特定** - 症状だけでなく原因を修正
5. **メトリクスを活用** - データに基づいて継続的に改善

## 🔗 関連ドキュメント

- [testing-standards.md](../../03-development-process/testing-standards.md) - テスト標準全般
- [quality-metrics.md](../quality-metrics.md) - 品質メトリクス詳細
- [unit-testing.md](../unit-testing.md) - ユニットテストによる欠陥予防

## 📝 改訂履歴

- **2025-11-13**: Phase 7完了 - 71.3 KB → 6ファイルに分割
- **元ドキュメント**: `_archive/defect-management_archived_2025-11-13.md`

## 💡 フィードバック

このドキュメントへのフィードバックや改善提案は、プロジェクトリポジトリのIssueまたはPull Requestで受け付けています。

---

**Document Restructuring Project** - Phase 7  
分割実行日: 2025-11-13  
元ファイルサイズ: 71.3 KB → 分割後: 73 KB (6ファイル)
