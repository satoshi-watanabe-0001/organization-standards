# AI クイックリファレンス - 欠陥管理

> **AI開発アシスタント向け必須チェックリスト**  
> 欠陥報告・トリアージ・解決時に最優先で確認すべき項目TOP20

## 🎯 使い方

このドキュメントは、AIアシスタント（Devin、Copilot等）が欠陥管理タスクを実行する際に、**最初に確認すべき重要項目**をまとめたものです。

---

## ✅ TOP 20 必須チェック項目

### 🔷 欠陥検出・報告（Defect Detection & Reporting）

#### 1. ✅ 欠陥の再現性確認
```
【必須確認】
- 再現手順が明確か？
- 再現率は？（100%、50%、稀）
- 環境依存性は？（OS、ブラウザ、デバイス）
```
📖 **詳細**: [02-classification-reporting.md](02-classification-reporting.md#欠陥レポート作成)

#### 2. ✅ Severity（重大度）の正確な判定
```
- Critical: システムダウン、データ損失
- High: 主要機能が使用不可
- Medium: 機能の一部に問題
- Low: UI/UXの軽微な問題
```
📖 **詳細**: [02-classification-reporting.md](02-classification-reporting.md#欠陥の分類と優先度)

#### 3. ✅ 必須情報の記載
```
【必須項目】
✓ タイトル（簡潔かつ具体的）
✓ 再現手順（Step-by-step）
✓ 期待結果 vs 実際の結果
✓ 環境情報（OS、バージョン等）
✓ スクリーンショット/ログ
```
📖 **詳細**: [02-classification-reporting.md](02-classification-reporting.md#欠陥レポート作成)

#### 4. ✅ 既存欠陥との重複チェック
```
【チェック手順】
1. キーワード検索
2. 類似症状の確認
3. 重複の場合は既存チケットにリンク
```
📖 **詳細**: [02-classification-reporting.md](02-classification-reporting.md)

---

### 🔶 欠陥トリアージ（Defect Triage）

#### 5. ✅ 優先度（Priority）の決定
```
Priority = f(Severity, Business Impact, Affected Users)

- P0: 即座に修正（本番環境停止）
- P1: 次回リリースで必須
- P2: 近日中に対応
- P3: 時間があれば対応
```
📖 **詳細**: [03-triage-resolution.md](03-triage-resolution.md#トリアージプロセス)

#### 6. ✅ ビジネスインパクトの評価
```
【評価軸】
- 影響を受けるユーザー数
- 収益への影響
- ブランドイメージへの影響
- 回避策の有無
```
📖 **詳細**: [03-triage-resolution.md](03-triage-resolution.md)

#### 7. ✅ 適切な担当者へのアサイン
```
【アサイン基準】
- モジュールオーナー
- 過去の類似欠陥修正者
- 現在の作業負荷
```
📖 **詳細**: [03-triage-resolution.md](03-triage-resolution.md)

---

### 🔵 欠陥解決（Defect Resolution）

#### 8. ✅ 根本原因の特定（RCA必須）
```
【5 Whys法】
問題: ログイン失敗
Why1: セッションタイムアウト
Why2: タイムアウト時間が短すぎる
Why3: 設定ファイルのデフォルト値
Why4: レビュー漏れ
Why5: チェックリストに項目がない → 根本原因
```
📖 **詳細**: [04-root-cause-analysis.md](04-root-cause-analysis.md#根本原因分析)

#### 9. ✅ 修正内容のレビュー
```
【レビュー項目】
✓ 根本原因に対処しているか？
✓ 副作用がないか？
✓ テストケースが追加されているか？
✓ 同様の箇所に横展開が必要か？
```
📖 **詳細**: [03-triage-resolution.md](03-triage-resolution.md#欠陥解決ワークフロー)

#### 10. ✅ 回帰テストの実施
```
【必須テスト】
1. 修正箇所の動作確認
2. 関連機能のテスト
3. 自動テストの実行
4. 本番相当環境での確認
```
📖 **詳細**: [03-triage-resolution.md](03-triage-resolution.md)

---

### 🟢 欠陥分析・予防（Analysis & Prevention）

#### 11. ✅ 欠陥パターンの分析
```
【分析視点】
- 発生頻度の高いモジュール
- 同一開発者に集中していないか？
- 同種の欠陥が繰り返していないか？
```
📖 **詳細**: [05-metrics-prevention.md](05-metrics-prevention.md#予防策と継続的改善)

#### 12. ✅ Defect Densityの監視
```
計算式:
Defect Density = 欠陥数 / コード行数(KLOC)

目標: < 2 defects/KLOC
```
📖 **詳細**: [05-metrics-prevention.md](05-metrics-prevention.md#メトリクスとレポーティング)

#### 13. ✅ Escape Rateの測定
```
Escape Rate = 本番発見欠陥数 / 総欠陥数

目標: < 10%
```
📖 **詳細**: [05-metrics-prevention.md](05-metrics-prevention.md)

#### 14. ✅ 予防策の実施
```
【予防アクション】
✓ コードレビューの強化
✓ テストカバレッジの向上
✓ 自動化テストの追加
✓ ペアプログラミング
```
📖 **詳細**: [05-metrics-prevention.md](05-metrics-prevention.md#予防策と継続的改善)

---

### 🟡 ツールと自動化（Tools & Automation）

#### 15. ✅ 欠陥追跡システムの活用
```
【推奨ツール】
- Jira: エンタープライズ向け
- GitHub Issues: 開発チーム向け
- Azure DevOps: Microsoft環境
```
📖 **詳細**: [06-tools-devin.md](06-tools-devin.md#ツールと自動化)

#### 16. ✅ 自動トリアージの設定
```
【自動化ルール】
- Severityに基づくラベル付け
- 担当者の自動アサイン
- SLA違反のアラート
```
📖 **詳細**: [06-tools-devin.md](06-tools-devin.md)

#### 17. ✅ CI/CDでの自動検出
```
【自動チェック】
✓ 静的解析（ESLint、SonarQube）
✓ ユニットテスト
✓ セキュリティスキャン
```
📖 **詳細**: [06-tools-devin.md](06-tools-devin.md)

---

### 🟣 Devin AI特有ガイドライン

#### 18. ✅ 欠陥報告の自動生成
```
【Devinタスク】
1. エラーログを収集
2. 再現手順を記録
3. 環境情報を取得
4. 構造化されたレポート作成
```
📖 **詳細**: [06-tools-devin.md](06-tools-devin.md#devin-aiガイドライン)

#### 19. ✅ 類似欠陥の検索
```
【検索手順】
1. エラーメッセージで検索
2. スタックトレースのパターンマッチ
3. 修正履歴の確認
```
📖 **詳細**: [06-tools-devin.md](06-tools-devin.md)

#### 20. ✅ 修正後の自動テスト
```
【Devin自動実行】
✓ ユニットテスト実行
✓ 関連テストケース実行
✓ カバレッジレポート生成
✓ 結果の要約作成
```
📖 **詳細**: [06-tools-devin.md](06-tools-devin.md)

---

## 🚨 絶対にやってはいけないこと（Critical Anti-patterns）

### ❌ 1. 口頭報告のみで記録しない
```
❌ 「バグ見つけたから直しといて」（Slackのみ）
✅ チケット作成 + Slackで通知
```

### ❌ 2. 再現手順を省略
```
❌ 「ログインできない」だけ
✅ 「1. XXページを開く 2. YYを入力 3. ...」
```

### ❌ 3. SeverityとPriorityの混同
```
❌ Severity=Lowなら後回し
✅ ビジネスインパクトで優先度決定
```

### ❌ 4. 症状だけ修正して原因放置
```
❌ エラー表示を消すだけ
✅ 根本原因（RCA）を特定して修正
```

### ❌ 5. 横展開しない
```
❌ 該当箇所だけ修正
✅ 同様のコードパターンをすべて確認
```

---

## 📋 欠陥報告テンプレート（AI用）

```markdown
## 欠陥報告

**タイトル**: [簡潔かつ具体的に]

**Severity**: Critical / High / Medium / Low

**環境**:
- OS: 
- ブラウザ: 
- バージョン: 

**再現手順**:
1. 
2. 
3. 

**期待結果**:

**実際の結果**:

**スクリーンショット/ログ**:
[添付]

**再現率**: 100% / 50% / 稀

**影響範囲**:
- 影響を受けるユーザー: 
- ビジネスインパクト: 
```

---

## 🔗 詳細ドキュメントへのリンク

| トピック | ドキュメント |
|---------|-------------|
| 基本原則・ライフサイクル | [01-principles-lifecycle.md](01-principles-lifecycle.md) |
| 分類・レポート作成 | [02-classification-reporting.md](02-classification-reporting.md) |
| トリアージ・解決 | [03-triage-resolution.md](03-triage-resolution.md) |
| 根本原因分析 | [04-root-cause-analysis.md](04-root-cause-analysis.md) |
| メトリクス・予防 | [05-metrics-prevention.md](05-metrics-prevention.md) |
| ツール・Devinガイド | [06-tools-devin.md](06-tools-devin.md) |

---

**AI-QUICK-REFERENCE** - Phase 7  
作成日: 2025-11-13  
対象: AI開発アシスタント（Devin、Copilot、GPT等）
