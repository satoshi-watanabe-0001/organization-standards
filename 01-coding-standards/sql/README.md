# SQL コーディング規約 (SQL Coding Standards)

**バージョン**: 2.0.0  
**最終更新日**: 2025-11-13  
**対象**: PostgreSQL 14+, MySQL 8.0+  
**適用範囲**: データベース設計・クエリ実装・パフォーマンス最適化

---

## 📋 このディレクトリについて

SQL開発における組織標準をまとめたドキュメント群です。元々一つの大きなファイル(165.6 KB)でしたが、AIと人間の両方にとって使いやすいように、トピック別に分割しました。

---

## 📚 ドキュメント一覧

### 基礎編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [01-syntax-and-style.md](./01-syntax-and-style.md) | 3.5 KB | **基本構文・スタイル規約**<br>• 大文字・小文字ルール<br>• インデント・フォーマット<br>• コメント規約 |

### 設計編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [02-database-design.md](./02-database-design.md) | 21.7 KB | **データベース設計原則**<br>• 正規化・非正規化戦略<br>• データ整合性・制約設計<br>• 主キー・外部キー設計<br>• インデックス設計戦略<br>• データ型選択・パーティショニング |

### パフォーマンス編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [03-query-optimization.md](./03-query-optimization.md) | 16.2 KB | **クエリ最適化・パフォーマンス**<br>• SELECT文の最適化技法<br>• JOIN最適化戦略<br>• サブクエリ vs CTE vs ウィンドウ関数<br>• インデックス活用パターン<br>• クエリ実行プラン分析 |

### セキュリティ編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [04-security-access-control.md](./04-security-access-control.md) | 16.7 KB | **セキュリティ・権限管理**<br>• SQLインジェクション対策<br>• ロールベースアクセス制御（RBAC）<br>• データ暗号化・機密情報保護<br>• 監査ログ・アクセス記録 |

### 信頼性編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [05-error-handling.md](./05-error-handling.md) | 22.8 KB | **エラーハンドリング・例外処理**<br>• 構造化されたエラーハンドリング<br>• カスタム例外の定義と活用<br>• トランザクション管理とロールバック戦略<br>• ユーザー向けエラーメッセージ |

### 品質保証編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [06-testing-qa.md](./06-testing-qa.md) | 18.9 KB | **テスト・品質保証**<br>• SQLユニットテストの実装<br>• データベーススキーマテスト<br>• パフォーマンステスト |

### 運用編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [07-operations-monitoring.md](./07-operations-monitoring.md) | 25.2 KB | **運用・監視・メンテナンス**<br>• 運用監視システムの構築<br>• パフォーマンス監視・アラート<br>• バックアップ・復旧戦略 |

### ドキュメント編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [08-documentation-generation.md](./08-documentation-generation.md) | 25.2 KB | **ドキュメント・ナレッジ管理**<br>• データベース設計書の自動生成<br>• 運用手順書・ナレッジベース管理<br>• 継続的ドキュメント更新の自動化 |
| [09-documentation-standards.md](./09-documentation-standards.md) | 16.6 KB | **ドキュメンテーション標準**<br>• SQLコメント必須要件<br>• SQLコメント標準形式<br>• コメントスタイルガイド<br>• コードレビューチェックリスト |

### コメント規約編（2025-11-14追加）✨

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [sql-inline-comment-examples.md](./sql-inline-comment-examples.md) | 6.9 KB | **SQL固有のインラインコメント実装例**<br>• DDL（テーブル定義、インデックス）<br>• 複雑なクエリ（JOIN、サブクエリ）<br>• ウィンドウ関数、再帰クエリ<br>• パフォーマンス最適化 |

**共通原則も参照**: [00-inline-comment-standards.md](../00-inline-comment-standards.md)

### AI活用向けサマリー

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [AI-QUICK-REFERENCE.md](./AI-QUICK-REFERENCE.md) | 作成予定 | **AI向けクイックリファレンス**<br>• 必須チェック項目<br>• よくある間違い<br>• セキュリティチェックリスト |

---

## 🎯 状況別ガイド

### 新規テーブル設計を開始する場合

1. **まず読むべき**: [02-database-design.md](./02-database-design.md)
   - 正規化戦略
   - 制約設計
   - インデックス戦略

2. **次に読むべき**: [01-syntax-and-style.md](./01-syntax-and-style.md)
   - 命名規則
   - フォーマット規約

3. **セキュリティ確認**: [04-security-access-control.md](./04-security-access-control.md)
   - 権限設計
   - 暗号化要件

### パフォーマンス問題が発生した場合

1. **問題特定**: [03-query-optimization.md](./03-query-optimization.md) § 3.5
   - EXPLAIN ANALYZE実行
   - ボトルネック特定

2. **最適化実施**:
   - [03-query-optimization.md](./03-query-optimization.md) § 3.1-3.4
   - インデックス最適化
   - クエリリライト

3. **監視設定**: [07-operations-monitoring.md](./07-operations-monitoring.md)
   - スロークエリ監視
   - パフォーマンスメトリクス

### コードレビューを実施する場合

1. **AIに読ませる**: [AI-QUICK-REFERENCE.md](./AI-QUICK-REFERENCE.md)
   - 必須チェック項目
   
2. **詳細確認**:
   - [04-security-access-control.md](./04-security-access-control.md) - SQLインジェクション
   - [03-query-optimization.md](./03-query-optimization.md) - パフォーマンス
   - [09-documentation-standards.md](./09-documentation-standards.md) - コメント

### 本番運用開始前

1. [06-testing-qa.md](./06-testing-qa.md) - テスト実施
2. [07-operations-monitoring.md](./07-operations-monitoring.md) - 監視設定
3. [05-error-handling.md](./05-error-handling.md) - エラーハンドリング確認

---

## 🔍 トピック別索引

### データ設計
- 正規化: [02-database-design.md](./02-database-design.md) § 2.1
- 主キー設計: [02-database-design.md](./02-database-design.md) § 2.3
- インデックス設計: [02-database-design.md](./02-database-design.md) § 2.4
- パーティショニング: [02-database-design.md](./02-database-design.md) § 2.5

### パフォーマンス最適化
- SELECT最適化: [03-query-optimization.md](./03-query-optimization.md) § 3.1
- JOIN最適化: [03-query-optimization.md](./03-query-optimization.md) § 3.2
- ウィンドウ関数: [03-query-optimization.md](./03-query-optimization.md) § 3.3
- EXPLAIN分析: [03-query-optimization.md](./03-query-optimization.md) § 3.5

### セキュリティ
- SQLインジェクション対策: [04-security-access-control.md](./04-security-access-control.md) § 4.1
- RBAC: [04-security-access-control.md](./04-security-access-control.md) § 4.2
- データ暗号化: [04-security-access-control.md](./04-security-access-control.md) § 4.3
- 監査ログ: [04-security-access-control.md](./04-security-access-control.md) § 4.4

### エラー処理
- エラーハンドリング: [05-error-handling.md](./05-error-handling.md) § 5.1
- トランザクション管理: [05-error-handling.md](./05-error-handling.md) § 5.3
- デッドロック対策: [05-error-handling.md](./05-error-handling.md) § 5.3

### テスト
- ユニットテスト: [06-testing-qa.md](./06-testing-qa.md) § 6.1
- スキーマテスト: [06-testing-qa.md](./06-testing-qa.md) § 6.2
- パフォーマンステスト: [06-testing-qa.md](./06-testing-qa.md) § 6.3

### 運用
- 監視システム: [07-operations-monitoring.md](./07-operations-monitoring.md) § 7.1
- スロークエリ監視: [07-operations-monitoring.md](./07-operations-monitoring.md) § 7.2
- バックアップ: [07-operations-monitoring.md](./07-operations-monitoring.md) § 7.3

---

## 💡 ベストプラクティス

### SQL開発の黄金律

1. **パラメータ化クエリ** - SQLインジェクション対策の基本
2. **適切なインデックス** - パフォーマンスの要
3. **トランザクション管理** - データ整合性の保証
4. **詳細なコメント** - メンテナンス性の向上
5. **EXPLAIN分析** - 本番前の必須チェック

### よくある間違い

❌ **避けるべき実装**:
```sql
-- SELECT * の使用
SELECT * FROM users;

-- 文字列結合によるSQLインジェクション
query = "SELECT * FROM users WHERE id = " + user_input

-- インデックスを使わないWHERE句
WHERE YEAR(created_at) = 2024

-- N+1問題
for user in users:
    orders = db.execute("SELECT * FROM orders WHERE user_id = ?", user.id)
```

✅ **推奨する実装**:
```sql
-- 必要なカラムのみ選択
SELECT user_id, user_name, email FROM users;

-- パラメータ化クエリ
cursor.execute("SELECT * FROM users WHERE id = ?", (user_id,))

-- インデックスが効くWHERE句
WHERE created_at >= '2024-01-01' AND created_at < '2025-01-01'

-- JOINで一括取得
SELECT u.*, o.* 
FROM users u 
LEFT JOIN orders o ON u.user_id = o.user_id;
```

---

## 🔗 関連ドキュメント

### 他の標準文書
- [../README.md](../README.md) - コーディング標準全体
- [../../02-architecture-standards/](../../02-architecture-standards/) - アーキテクチャ標準
- [../../03-development-process/](../../03-development-process/) - 開発プロセス

### 外部リソース
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [SQL Style Guide](https://www.sqlstyle.guide/)

---

## 📝 ドキュメントのメンテナンス

### 更新履歴

| 日付 | バージョン | 変更内容 |
|------|-----------|---------|
| 2025-11-13 | 2.0.0 | 大規模リファクタリング: 1ファイル(165.6KB)から9ファイルに分割 |
| 2024-10-09 | 1.0.0 | 初版作成 |

---

## 📊 ファイルサイズ一覧

```
sql/
├── README.md (このファイル)
├── AI-QUICK-REFERENCE.md (作成予定)
├── 01-syntax-and-style.md           3.5 KB
├── 02-database-design.md           21.7 KB
├── 03-query-optimization.md        16.2 KB
├── 04-security-access-control.md   16.7 KB
├── 05-error-handling.md            22.8 KB
├── 06-testing-qa.md                18.9 KB
├── 07-operations-monitoring.md     25.2 KB
├── 08-documentation-generation.md  25.2 KB
└── 09-documentation-standards.md   16.6 KB

合計: ~166 KB (元: 165.6 KB)
ファイル数: 9個 (元: 1個)
平均サイズ: ~18 KB
最大サイズ: 25.2 KB
```

---

## ⚠️ 重要な注意事項

1. **元ファイルの扱い**
   - 元の `sql-standards.md` は `_archive/` に移動予定
   - 参照する場合は、この新しい構造を使用してください

2. **AI活用時の推奨**
   - 単一タスクには関連ファイルのみを読み込ませる
   - コードレビューには `AI-QUICK-REFERENCE.md` を優先使用
   - 詳細確認が必要な場合のみ個別ファイルを参照

3. **データベース固有の注意**
   - PostgreSQL/MySQL両対応を基本とする
   - DBMS固有機能使用時は明示的にコメント記載

---

**このREADMEについて**: このファイルは、SQLコーディング規約ドキュメント群のナビゲーションハブです。各ドキュメントへの適切なアクセスを支援し、効率的な情報検索を可能にします。
