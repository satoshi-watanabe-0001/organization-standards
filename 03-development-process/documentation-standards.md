# ドキュメンテーション標準 / Documentation Standards

**最終更新日**: 2025-10-24  
**バージョン**: 1.0.0  
**対象**: 全開発者・テクニカルライター・自律型AI Devin  

---

## 📖 目次

1. [ドキュメンテーションの原則](#1-ドキュメンテーションの原則)
2. [ドキュメントの種類](#2-ドキュメントの種類)
3. [README標準](#3-readme標準)
4. [API仕様書標準](#4-api仕様書標準)
5. [アーキテクチャドキュメント](#5-アーキテクチャドキュメント)
6. [運用手順書](#6-運用手順書)
7. [コードコメント標準](#7-コードコメント標準)
8. [Markdown記法ガイドライン](#8-markdown記法ガイドライン)
9. [ドキュメント管理](#9-ドキュメント管理)
10. [AI開発者向けガイドライン](#10-ai開発者向けガイドライン)

---

## 1. ドキュメンテーションの原則

### 1.1 基本原則

#### **明確性（Clarity）**
- 専門用語を適切に説明
- 簡潔で分かりやすい表現
- 曖昧さを排除

#### **完全性（Completeness）**
- 必要な情報をすべて含む
- 前提条件を明示
- エッジケースも記載

#### **最新性（Currency）**
- コードと同期して更新
- 古い情報を削除
- 更新日時を明記

#### **アクセシビリティ（Accessibility）**
- 見つけやすい場所に配置
- 適切な階層構造
- 検索可能な形式

---

### 1.2 ドキュメンテーションのレベル

**Level 1: 最小限（必須）**
- プロジェクトREADME
- API仕様書（APIを提供する場合）
- セットアップ手順

**Level 2: 標準（推奨）**
- アーキテクチャドキュメント
- 運用手順書
- トラブルシューティングガイド

**Level 3: 包括的（理想）**
- チュートリアル
- ベストプラクティスガイド
- FAQと既知の問題

---

## 2. ドキュメントの種類

### 2.1 ドキュメント分類

```
docs/
├── README.md                    # プロジェクト概要
├── CHANGELOG.md                 # 変更履歴
├── CONTRIBUTING.md              # コントリビューションガイド
├── architecture/
│   ├── overview.md              # アーキテクチャ概要
│   ├── database-schema.md       # データベース設計
│   └── system-design.md         # システム設計詳細
├── api/
│   ├── openapi.yaml             # OpenAPI仕様
│   └── api-guide.md             # API使用ガイド
├── guides/
│   ├── setup.md                 # セットアップガイド
│   ├── deployment.md            # デプロイガイド
│   └── troubleshooting.md       # トラブルシューティング
└── operations/
    ├── monitoring.md            # 監視手順
    ├── backup-restore.md        # バックアップ・リストア
    └── incident-response.md     # インシデント対応
```

---

## 3. README標準

### 3.1 プロジェクトREADME構成

**必須セクション**:

```markdown
# プロジェクト名

[![Build Status](badge-url)](link)
[![Coverage](badge-url)](link)
[![License](badge-url)](link)

## 概要
プロジェクトの簡潔な説明（2〜3文）

## 主要機能
- 機能1
- 機能2
- 機能3

## 前提条件
- Node.js 18.x以上
- PostgreSQL 14.x以上
- Docker（開発環境）

## インストール
​```bash
# 依存関係のインストール
npm install

# 環境変数の設定
cp .env.example .env
​```

## 使用方法
​```bash
# 開発サーバー起動
npm run dev

# テスト実行
npm test

# ビルド
npm run build
​```

## 設定
環境変数の説明

## テスト
テスト実行方法と説明

## デプロイ
デプロイ手順の概要（詳細は docs/guides/deployment.md）

## コントリビューション
CONTRIBUTING.mdを参照

## ライセンス
MIT License - 詳細は LICENSE ファイルを参照

## 連絡先
- メンテナー: name@example.com
- Issue: https://github.com/org/repo/issues
```

---

### 3.2 モジュール/パッケージREADME

**構成**:
```markdown
# モジュール名

## 目的
このモジュールが解決する問題

## 使用例
​```typescript
import { Module } from './module';

const instance = new Module();
instance.doSomething();
​```

## API
### クラス名
#### メソッド名
- **引数**: 説明
- **戻り値**: 説明
- **例外**: 説明

## 依存関係
- 依存パッケージ1
- 依存パッケージ2

## テスト
​```bash
npm test
​```

## 関連ドキュメント
- [アーキテクチャ](../docs/architecture/overview.md)
```

---

## 4. API仕様書標準

### 4.1 OpenAPI/Swagger仕様

**推奨フォーマット**: OpenAPI 3.0+

**基本構成**:
```yaml
openapi: 3.0.0
info:
  title: プロジェクト名 API
  version: 1.0.0
  description: APIの説明
  contact:
    name: API サポート
    email: api-support@example.com

servers:
  - url: https://api.example.com/v1
    description: 本番環境
  - url: https://staging-api.example.com/v1
    description: ステージング環境

paths:
  /users:
    get:
      summary: ユーザー一覧取得
      description: 詳細な説明
      parameters:
        - name: limit
          in: query
          description: 取得件数
          required: false
          schema:
            type: integer
            default: 20
            minimum: 1
            maximum: 100
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  users:
                    type: array
                    items:
                      $ref: '#/components/schemas/User'
        '400':
          description: 不正なリクエスト
        '401':
          description: 認証エラー
        '500':
          description: サーバーエラー

components:
  schemas:
    User:
      type: object
      required:
        - id
        - email
      properties:
        id:
          type: string
          format: uuid
          example: "123e4567-e89b-12d3-a456-426614174000"
        email:
          type: string
          format: email
          example: "user@example.com"
        name:
          type: string
          example: "山田太郎"

  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

security:
  - BearerAuth: []
```

---

### 4.2 API使用ガイド

**構成**:
```markdown
# API使用ガイド

## 認証
JWTトークンを使用した認証方法

## レート制限
- 認証済み: 1000リクエスト/時間
- 未認証: 100リクエスト/時間

## エラーハンドリング
​```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "メールアドレスの形式が不正です",
    "details": [
      {
        "field": "email",
        "message": "有効なメールアドレスを入力してください"
      }
    ]
  }
}
​```

## ページネーション
​```
GET /users?page=1&limit=20
​```

## バージョニング
URLパスでバージョン指定: `/v1/users`

## サンプルコード
### JavaScript
​```javascript
const response = await fetch('https://api.example.com/v1/users', {
  headers: {
    'Authorization': 'Bearer YOUR_TOKEN',
    'Content-Type': 'application/json'
  }
});
const data = await response.json();
​```

### Python
​```python
import requests

response = requests.get(
    'https://api.example.com/v1/users',
    headers={'Authorization': 'Bearer YOUR_TOKEN'}
)
data = response.json()
​```
```

---

## 5. アーキテクチャドキュメント

### 5.1 システムアーキテクチャ概要

**構成**:
```markdown
# システムアーキテクチャ

## 概要
システムの全体像を1〜2段落で説明

## アーキテクチャ図
​```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ HTTPS
┌──────▼──────┐
│  API Gateway│
└──────┬──────┘
       │
┌──────▼──────┐    ┌──────────┐
│  App Server │───>│ Database │
└─────────────┘    └──────────┘
​```

## コンポーネント

### フロントエンド
- **技術スタック**: React 18, TypeScript
- **責務**: ユーザーインターフェース
- **通信**: RESTful API

### バックエンド
- **技術スタック**: Node.js, Express, TypeScript
- **責務**: ビジネスロジック、API提供
- **アーキテクチャパターン**: レイヤードアーキテクチャ

### データベース
- **RDBMS**: PostgreSQL 14
- **キャッシュ**: Redis
- **スキーマ**: [database-schema.md](./database-schema.md)参照

## データフロー
1. クライアントがAPIリクエスト
2. API Gatewayで認証・認可
3. アプリケーションサーバーで処理
4. データベースアクセス
5. レスポンス返却

## セキュリティ
- HTTPS通信必須
- JWT認証
- RBAC認可
- 詳細: [security-architecture.md](../../02-architecture-standards/security-architecture.md)

## スケーラビリティ
- 水平スケーリング対応
- ステートレス設計
- キャッシング戦略

## 監視とログ
- メトリクス: Prometheus
- ログ: ELK Stack
- トレーシング: Jaeger
```

---

### 5.2 データベーススキーマ

**構成**:
```markdown
# データベーススキーマ

## ER図
​```
[User] 1---* [Post]
[User] 1---* [Comment]
[Post] 1---* [Comment]
​```

## テーブル定義

### users テーブル
| カラム名 | 型 | NULL | デフォルト | 説明 |
|---------|-------|------|-----------|------|
| id | UUID | NO | uuid_generate_v4() | 主キー |
| email | VARCHAR(255) | NO | - | メールアドレス（UNIQUE） |
| password_hash | VARCHAR(255) | NO | - | パスワードハッシュ |
| name | VARCHAR(100) | YES | NULL | 氏名 |
| created_at | TIMESTAMP | NO | CURRENT_TIMESTAMP | 作成日時 |
| updated_at | TIMESTAMP | NO | CURRENT_TIMESTAMP | 更新日時 |

**インデックス**:
- PRIMARY KEY (id)
- UNIQUE INDEX (email)
- INDEX (created_at)

**制約**:
- email形式チェック
- password_hash長さ最小60文字

### posts テーブル
...（同様の形式）

## リレーション
- users.id → posts.user_id (1:N)
- users.id → comments.user_id (1:N)
- posts.id → comments.post_id (1:N)

## マイグレーション
​```bash
npm run migrate:up    # マイグレーション実行
npm run migrate:down  # ロールバック
​```
```

---

## 6. 運用手順書

### 6.1 デプロイ手順書

**構成**:
```markdown
# デプロイ手順書

## 前提条件
- AWS CLIインストール済み
- 適切な権限を持つIAMユーザー
- デプロイ対象のブランチがmain

## デプロイ手順

### 1. 事前確認
​```bash
# ブランチ確認
git branch --show-current

# テスト実行
npm test

# ビルド確認
npm run build
​```

### 2. ステージング環境デプロイ
​```bash
# ステージングにデプロイ
npm run deploy:staging

# 動作確認
curl https://staging-api.example.com/health
​```

### 3. 本番環境デプロイ
​```bash
# 本番にデプロイ
npm run deploy:production

# 動作確認
curl https://api.example.com/health
​```

### 4. デプロイ後確認
- [ ] ヘルスチェックが成功
- [ ] 主要APIエンドポイントが正常動作
- [ ] エラーログがないことを確認
- [ ] メトリクスが正常範囲内

## ロールバック手順
​```bash
# 前バージョンにロールバック
npm run rollback:production

# 確認
curl https://api.example.com/health
​```

## トラブルシューティング
### デプロイ失敗時
1. ログ確認: `npm run logs:production`
2. 原因特定
3. 修正またはロールバック

### 問い合わせ先
- Slack: #deployments
- Email: devops@example.com
```

---

### 6.2 監視・運用手順書

**構成**:
```markdown
# 監視・運用手順書

## 監視項目

### システムメトリクス
- CPU使用率: 80%以下が正常
- メモリ使用率: 85%以下が正常
- ディスク使用率: 80%以下が正常

### アプリケーションメトリクス
- レスポンスタイム: p95 < 500ms
- エラー率: < 1%
- リクエスト数: 監視のみ

## アラート対応

### Critical: サービス停止
1. インシデント宣言
2. 関係者に通知
3. 原因調査開始
4. 応急処置実施
5. 本修正実施
6. ポストモーテム実施

### Warning: パフォーマンス低下
1. メトリクス詳細確認
2. ログ確認
3. 必要に応じてスケールアウト

## バックアップ・リストア

### バックアップ
​```bash
# データベースバックアップ
npm run backup:db

# 確認
npm run backup:list
​```

### リストア
​```bash
# リストア実行
npm run restore:db --backup-id=YYYY-MM-DD-HH-MM

# 整合性確認
npm run db:verify
​```

## 定期メンテナンス
- 毎日: バックアップ確認
- 毎週: ログローテーション
- 毎月: セキュリティアップデート確認
```

---

## 7. コードコメント標準

### 7.1 コメントの原則

**コメントすべき内容**:
- ✅ なぜそのように実装したか（Why）
- ✅ 複雑なロジックの説明
- ✅ 制約事項や注意点
- ✅ TODOやFIXME

**コメント不要な内容**:
- ❌ コードを読めば分かること（What）
- ❌ 冗長な説明
- ❌ コミット履歴で分かること

---

### 7.2 言語別コメント標準

#### TypeScript/JavaScript (JSDoc)
```typescript
/**
 * ユーザーを認証し、JWTトークンを生成する
 * 
 * @param email - ユーザーのメールアドレス
 * @param password - ユーザーのパスワード（平文）
 * @returns JWTトークンとユーザー情報
 * @throws {AuthenticationError} 認証失敗時
 * 
 * @example
 * ```typescript
 * const result = await authenticateUser('user@example.com', 'password123');
 * console.log(result.token);
 * ```
 */
export async function authenticateUser(
  email: string,
  password: string
): Promise<AuthResult> {
  // パスワードハッシュを検証
  // bcryptを使用（Argon2への移行を検討中 TODO: #123）
  const isValid = await bcrypt.compare(password, user.passwordHash);
  
  if (!isValid) {
    throw new AuthenticationError('Invalid credentials');
  }
  
  // JWTトークン生成（有効期限: 1時間）
  const token = jwt.sign({ userId: user.id }, SECRET_KEY, { expiresIn: '1h' });
  
  return { token, user };
}
```

---

#### Python (Docstring)
```python
def authenticate_user(email: str, password: str) -> AuthResult:
    """
    ユーザーを認証し、JWTトークンを生成する
    
    Args:
        email: ユーザーのメールアドレス
        password: ユーザーのパスワード（平文）
    
    Returns:
        AuthResult: JWTトークンとユーザー情報
    
    Raises:
        AuthenticationError: 認証失敗時
    
    Example:
        >>> result = authenticate_user('user@example.com', 'password123')
        >>> print(result.token)
    """
    # 実装
    pass
```

詳細: `/01-coding-standards/[言語]-standards.md`参照

---

## 8. Markdown記法ガイドライン

### 8.1 基本記法

#### 見出し
```markdown
# H1: ドキュメントタイトル（1つのみ）
## H2: 主要セクション
### H3: サブセクション
#### H4: 詳細項目
```

#### 強調
```markdown
**太字**: 重要な用語
*斜体*: 強調
`コード`: コマンド、変数名、ファイル名
```

#### リスト
```markdown
- 順不同リスト
- 項目2
  - ネスト項目

1. 順序付きリスト
2. 項目2
   1. ネスト項目
```

#### リンク
```markdown
[リンクテキスト](URL)
[相対パス](./other-doc.md)
[アンカーリンク](#セクション名)
```

#### コードブロック
````markdown
```language
コード
```
````

#### 表
```markdown
| ヘッダー1 | ヘッダー2 |
|----------|----------|
| セル1    | セル2    |
```

#### 注意・警告
```markdown
> **注意**: 重要な注意事項

> **警告**: 危険な操作
```

---

### 8.2 スタイルガイド

**ファイル名**:
- 小文字とハイフン使用: `api-guide.md`
- 英語を推奨: `deployment-guide.md`

**見出し**:
- 階層を飛ばさない（H2の次はH3）
- 簡潔で分かりやすく

**文章**:
- 一文を短く（60文字以内を目標）
- 箇条書きを活用
- 専門用語には説明を付ける

---

## 9. ドキュメント管理

### 9.1 更新タイミング

**必須更新**:
- API仕様変更時 → API仕様書更新
- 環境変数追加時 → README/セットアップガイド更新
- デプロイ手順変更時 → デプロイ手順書更新
- アーキテクチャ変更時 → アーキテクチャドキュメント更新

**推奨更新**:
- 新機能追加時 → CHANGELOG更新
- バグ修正時 → トラブルシューティングガイド更新
- パフォーマンス改善時 → ベストプラクティスガイド更新

---

### 9.2 レビュープロセス

**ドキュメントもコードと同様にレビュー**:
1. Pull Requestでドキュメント変更を提出
2. レビュアーが内容を確認
   - 技術的正確性
   - 明確性
   - 完全性
3. 承認後にマージ

---

### 9.3 バージョン管理

**セマンティックバージョニング適用**:
- Major: 破壊的変更（APIの大幅変更等）
- Minor: 新機能追加
- Patch: 誤字修正、軽微な改善

**更新履歴**:
```markdown
## [1.2.0] - 2025-10-24
### Added
- 新しいAPI仕様追加

### Changed
- デプロイ手順を更新

### Fixed
- 誤字修正
```

---

## 10. AI開発者向けガイドライン

### 10.1 Devin AIのドキュメント作成

**プロンプト例**:
```
以下のドキュメンテーション標準に従ってREADMEを作成してください：
/03-development-process/documentation-standards.md

プロジェクト情報：
- 名称: [プロジェクト名]
- 技術スタック: [技術スタック]
- 目的: [目的]

含めるセクション：
- 概要
- インストール
- 使用方法
- 設定
- テスト
```

---

### 10.2 ドキュメント更新チェックリスト

Devin AIがドキュメントを更新する際のチェック項目:

- [ ] 既存ドキュメントを読んで現状を把握
- [ ] 変更内容に応じた適切なドキュメントを特定
- [ ] 標準フォーマットに従って更新
- [ ] コード例が正しく動作することを確認
- [ ] リンク切れがないことを確認
- [ ] 更新日時を記録
- [ ] CHANGELOGに変更を記録（該当する場合）

---

## 📚 参考資料

- [Google Developer Documentation Style Guide](https://developers.google.com/style)
- [Write the Docs](https://www.writethedocs.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Markdown Guide](https://www.markdownguide.org/)

---

## 次のステップ

1. プロジェクトに必要なドキュメントを特定
2. ドキュメントテンプレートを作成
3. 継続的なドキュメント更新の文化を醸成

---

**関連ドキュメント**:
- `/03-development-process/README.md` - 開発プロセス概要
- `/01-coding-standards/00-general-principles.md` - コードコメント標準
- `/02-architecture-standards/design-principles.md` - アーキテクチャドキュメント
