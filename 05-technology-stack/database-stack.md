# データベーススタック標準

## ドキュメント情報

- **バージョン**: 1.0.0
- **最終更新日**: 2025-10-24
- **ステータス**: アクティブ
- **管理者**: データベースチーム / Platform Engineering
- **カテゴリー**: 技術スタック

## 目次

1. [概要](#概要)
2. [承認済みデータベース技術](#承認済みデータベース技術)
3. [データベース選択ガイドライン](#データベース選択ガイドライン)
4. [データベース標準構成](#データベース標準構成)
5. [バックアップと復旧](#バックアップと復旧)
6. [パフォーマンス最適化](#パフォーマンス最適化)
7. [セキュリティ要件](#セキュリティ要件)
8. [Devin AIガイドライン](#devin-aiガイドライン)

---

## 概要

### 目的

本ドキュメントは、組織全体で使用される承認済みデータベース技術と、その選択・実装・運用に関する標準を定義します。

### 適用範囲

- **リレーショナルデータベース（RDBMS）**: トランザクション処理、構造化データ
- **NoSQLデータベース**: 柔軟なスキーマ、高スケーラビリティ
- **キャッシュデータベース**: 高速データアクセス
- **検索エンジン**: 全文検索、ログ分析
- **時系列データベース**: メトリクス、IoTデータ

### 基本原則

1. **適切なツールの選択**: ユースケースに最適なデータベースを選択
2. **スケーラビリティ**: 将来の成長を考慮した設計
3. **可用性**: 高可用性構成の実装
4. **セキュリティ**: データ保護とアクセス制御
5. **パフォーマンス**: 効率的なクエリとインデックス戦略
6. **運用性**: 監視、バックアップ、復旧の自動化

---

## 承認済みデータベース技術

### リレーショナルデータベース（RDBMS）

#### PostgreSQL（推奨）

**適用ケース**:
- トランザクション処理が必要なアプリケーション
- 複雑なクエリとJOIN操作
- JSONデータの混在（JSONB型）
- 地理空間データ（PostGIS）

**推奨バージョン**: PostgreSQL 15以上

**利点**:
- オープンソースで豊富な拡張機能
- ACID準拠の信頼性
- 高度なインデックス機能（B-tree, Hash, GiST, GIN）
- JSONサポート（JSONB）
- 強力なレプリケーション機能

**接続例**:
```javascript
// Node.js (pg)
const { Pool } = require('pg');

const pool = new Pool({
  host: process.env.DB_HOST,
  port: 5432,
  database: process.env.DB_NAME,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  max: 20,
  idleTimeoutMillis: 30000,
  connectionTimeoutMillis: 2000,
});
```

#### MySQL / MariaDB

**適用ケース**:
- シンプルなトランザクション処理
- 読み取り性能重視のアプリケーション
- レガシーシステムとの互換性

**推奨バージョン**: MySQL 8.0以上 / MariaDB 10.6以上

---

### NoSQLデータベース

#### MongoDB（ドキュメント指向）

**適用ケース**:
- 柔軟なスキーマが必要なアプリケーション
- 階層的データ構造（ネストされたオブジェクト）
- 高速な読み書き操作
- 水平スケーリングが必要な場合

**推奨バージョン**: MongoDB 6.0以上

**利点**:
- JSONライクなドキュメント構造
- 柔軟なスキーマ設計
- 強力なクエリ機能（Aggregation Framework）
- 組み込みシャーディング
- 変更ストリーム（Change Streams）

**接続例**:
```javascript
// Node.js (mongoose)
const mongoose = require('mongoose');

mongoose.connect(process.env.MONGODB_URI, {
  maxPoolSize: 10,
  serverSelectionTimeoutMS: 5000,
  socketTimeoutMS: 45000,
});
```

#### DynamoDB（AWSのみ）

**適用ケース**:
- AWS環境でのサーバーレスアプリケーション
- 予測可能な低レイテンシー
- 極めて高いスケーラビリティが必要な場合

---

### キャッシュデータベース

#### Redis（推奨）

**適用ケース**:
- セッション管理
- キャッシング層
- リアルタイムランキング
- Pub/Subメッセージング
- レート制限

**推奨バージョン**: Redis 7.0以上

**利点**:
- インメモリで超高速
- 豊富なデータ構造（String, Hash, List, Set, Sorted Set）
- Pub/Sub機能
- TTL（Time To Live）による自動削除
- Redisクラスタによる高可用性

**接続例**:
```javascript
// Node.js (ioredis)
const Redis = require('ioredis');

const redis = new Redis({
  host: process.env.REDIS_HOST,
  port: 6379,
  password: process.env.REDIS_PASSWORD,
  retryStrategy: (times) => Math.min(times * 50, 2000),
  maxRetriesPerRequest: 3,
});
```

---

### 検索エンジン

#### Elasticsearch

**適用ケース**:
- 全文検索
- ログ分析（ELKスタック）
- リアルタイム分析
- アプリケーション検索機能

**推奨バージョン**: Elasticsearch 8.0以上

**利点**:
- 強力な全文検索機能
- 分散アーキテクチャ
- リアルタイム分析
- Kibanaによる可視化

---

### 時系列データベース

#### InfluxDB

**適用ケース**:
- メトリクス収集
- IoTセンサーデータ
- アプリケーション監視
- ビジネス分析

**推奨バージョン**: InfluxDB 2.x

---

## データベース選択ガイドライン

### 選択基準マトリクス

| ユースケース | 推奨データベース | 理由 |
|------------|---------------|------|
| **トランザクション処理** | PostgreSQL | ACID準拠、信頼性 |
| **柔軟なスキーマ** | MongoDB | ドキュメント指向、スキーマレス |
| **キャッシング** | Redis | インメモリ、高速 |
| **全文検索** | Elasticsearch | 強力な検索機能 |
| **時系列データ** | InfluxDB | 時系列最適化 |
| **セッション管理** | Redis | 高速アクセス、TTL |
| **リアルタイム分析** | Elasticsearch | リアルタイム集計 |
| **地理空間データ** | PostgreSQL (PostGIS) | 地理空間関数 |

### 複数データベース併用（Polyglot Persistence）

プロジェクトの要件に応じて、複数のデータベースを組み合わせることを推奨します。

**例**: Eコマースアプリケーション
```
- PostgreSQL: トランザクション（注文、在庫）
- Redis: セッション、カート、キャッシュ
- MongoDB: 商品カタログ、レビュー
- Elasticsearch: 商品検索
```

---

## データベース標準構成

### PostgreSQL標準構成

#### 接続プール設定

```yaml
# 推奨接続プール設定
connection_pool:
  min_connections: 5
  max_connections: 20
  idle_timeout: 30s
  connection_timeout: 5s
  statement_timeout: 30s
```

#### インデックス戦略

```sql
-- 主キーインデックス（自動作成）
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 検索用インデックス
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at DESC);

-- 複合インデックス
CREATE INDEX idx_orders_user_status ON orders(user_id, status);

-- 部分インデックス（条件付き）
CREATE INDEX idx_active_users ON users(id) WHERE deleted_at IS NULL;
```

---

### MongoDB標準構成

#### インデックス設計

```javascript
// 単一フィールドインデックス
db.users.createIndex({ email: 1 }, { unique: true });

// 複合インデックス
db.orders.createIndex({ userId: 1, createdAt: -1 });

// テキストインデックス（全文検索）
db.products.createIndex({ 
  name: "text", 
  description: "text" 
});

// TTLインデックス（自動削除）
db.sessions.createIndex(
  { createdAt: 1 },
  { expireAfterSeconds: 3600 }
);
```

---

### Redis標準構成

#### キー命名規則

```
# 形式: <namespace>:<entity>:<id>:<field>
user:profile:12345:name
user:session:abc123
cache:product:98765
rate_limit:api:user:12345
```

#### TTL設定

```javascript
// セッション: 1時間
await redis.setex('session:abc123', 3600, sessionData);

// キャッシュ: 5分
await redis.setex('cache:product:123', 300, productData);

// レート制限: 1分
await redis.setex('rate_limit:api:user:123', 60, requestCount);
```

---

## バックアップと復旧

### PostgreSQL

#### 自動バックアップ

```bash
# pg_dumpを使用した論理バックアップ
pg_dump -h $DB_HOST -U $DB_USER -Fc $DB_NAME > backup_$(date +%Y%m%d_%H%M%S).dump

# 復元
pg_restore -h $DB_HOST -U $DB_USER -d $DB_NAME backup.dump
```

#### 継続的アーカイビング（WAL）

```ini
# postgresql.conf
wal_level = replica
archive_mode = on
archive_command = 'cp %p /backup/wal/%f'
```

### MongoDB

```bash
# mongodumpを使用したバックアップ
mongodump --uri="mongodb://user:pass@host:27017/dbname" --out=/backup/

# 復元
mongorestore --uri="mongodb://user:pass@host:27017/dbname" /backup/
```

---

## パフォーマンス最適化

### クエリ最適化

#### PostgreSQL

```sql
-- EXPLAINでクエリプラン分析
EXPLAIN ANALYZE
SELECT * FROM orders
WHERE user_id = 123
  AND status = 'completed'
ORDER BY created_at DESC
LIMIT 10;

-- 適切なインデックスを作成
CREATE INDEX idx_orders_user_status ON orders(user_id, status, created_at DESC);
```

#### MongoDB

```javascript
// クエリプラン分析
db.orders.find({ userId: 123, status: "completed" })
  .sort({ createdAt: -1 })
  .limit(10)
  .explain("executionStats");
```

### 接続プール管理

- **最小接続数**: アプリケーションの通常負荷に対応
- **最大接続数**: ピーク時の負荷に対応（データベースの最大接続数を考慮）
- **アイドルタイムアウト**: 未使用接続の自動クローズ

---

## セキュリティ要件

### アクセス制御

```sql
-- PostgreSQL: 最小権限の原則
CREATE ROLE app_user WITH LOGIN PASSWORD 'secure_password';
GRANT CONNECT ON DATABASE mydb TO app_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;

-- 読み取り専用ユーザー
CREATE ROLE readonly_user WITH LOGIN PASSWORD 'secure_password';
GRANT CONNECT ON DATABASE mydb TO readonly_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO readonly_user;
```

### 暗号化

- **転送中の暗号化**: SSL/TLS接続を必須化
- **保存時の暗号化**: 機密データの暗号化（Transparent Data Encryption）
- **パスワード管理**: 環境変数またはシークレット管理ツール（AWS Secrets Manager, HashiCorp Vault）

### 監査ログ

```sql
-- PostgreSQL: 監査ログ有効化
ALTER SYSTEM SET log_statement = 'all';
ALTER SYSTEM SET log_connections = 'on';
ALTER SYSTEM SET log_disconnections = 'on';
```

---

## Devin AIガイドライン

### データベース実装時の推奨事項

1. **接続管理**
   - 必ず接続プールを使用
   - エラーハンドリングとリトライロジックの実装
   - 接続リークの防止

2. **クエリ最適化**
   - N+1問題の回避
   - 適切なインデックスの作成
   - ページネーションの実装

3. **セキュリティ**
   - SQLインジェクション対策（プリペアドステートメント）
   - 最小権限の原則
   - 機密情報の暗号化

4. **トランザクション管理**
   - ACID特性の理解
   - 適切なトランザクション境界の設定
   - デッドロック処理

5. **監視とロギング**
   - スロークエリログの有効化
   - 接続数の監視
   - エラーログの記録

### コード例テンプレート

```javascript
// PostgreSQL接続とクエリの例
const { Pool } = require('pg');

class DatabaseService {
  constructor() {
    this.pool = new Pool({
      host: process.env.DB_HOST,
      port: 5432,
      database: process.env.DB_NAME,
      user: process.env.DB_USER,
      password: process.env.DB_PASSWORD,
      max: 20,
      idleTimeoutMillis: 30000,
      connectionTimeoutMillis: 2000,
    });
  }

  async query(text, params) {
    const start = Date.now();
    try {
      const res = await this.pool.query(text, params);
      const duration = Date.now() - start;
      console.log('Executed query', { text, duration, rows: res.rowCount });
      return res;
    } catch (error) {
      console.error('Database query error', { text, error });
      throw error;
    }
  }

  async transaction(callback) {
    const client = await this.pool.connect();
    try {
      await client.query('BEGIN');
      const result = await callback(client);
      await client.query('COMMIT');
      return result;
    } catch (error) {
      await client.query('ROLLBACK');
      throw error;
    } finally {
      client.release();
    }
  }
}

module.exports = new DatabaseService();
```

---

## 関連ドキュメント

- [アーキテクチャ標準](../02-architecture-standards/)
- [セキュリティ標準](../07-security-compliance/)
- [パフォーマンステスト標準](../03-development-process/testing-standards.md)

---

**最終更新**: 2025-10-24  
**次回レビュー予定**: 2026-01-24
