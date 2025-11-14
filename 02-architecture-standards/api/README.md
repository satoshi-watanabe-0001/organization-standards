# API設計標準 (API Architecture Standards)

**バージョン**: 2.0.0  
**最終更新日**: 2025-11-13  
**対象技術**: RESTful API, GraphQL, gRPC, WebSocket, OpenAPI 3.0+

---

## 📋 このディレクトリについて

API設計に関する組織標準をまとめたドキュメント群です。元々一つの大きなファイル（293.8 KB）でしたが、AIと人間の両方にとって使いやすいように、トピック別に分割しました。

---

## 📚 ドキュメント一覧

### 基礎編: API設計の原則

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [01-design-principles.md](./01-design-principles.md) | 62.2 KB | **API設計基本原則**<br>• 設計哲学<br>• APIファースト設計<br>• 一貫性の原則<br>• RESTful原則<br>• バージョニング戦略<br>• セキュリティ設計原則 |
| [02-restful-design-part1.md](./02-restful-design-part1.md) | 37.5 KB | **RESTful API設計標準 (基礎)**<br>• リソース設計<br>• リソース命名規則<br>• HTTPメソッド適用原則 |

### 実践編: RESTful API詳細設計

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [03a-rest-resource-design.md](./03a-rest-resource-design.md) | 10.8 KB | **リソース設計**<br>• リソース命名規則<br>• ネストしたリソース設計 |
| [03b-rest-http-methods.md](./03b-rest-http-methods.md) | 10.7 KB | **HTTPメソッド標準利用**<br>• 標準CRUDパターン<br>• GET/POST/PUT/PATCH/DELETE |
| [03c-rest-status-codes.md](./03c-rest-status-codes.md) | 15.6 KB | **ステータスコード標準使用**<br>• 適切なステータスコード選択<br>• 2xx/3xx/4xx/5xx系の使い分け |
| [03d-rest-query-params.md](./03d-rest-query-params.md) | 23.8 KB | **クエリパラメータ設計**<br>• ページネーションパターン<br>• フィルタリングと検索機能 |
| [03e-rest-response-format.md](./03e-rest-response-format.md) | 14.6 KB | **レスポンスフォーマット**<br>• JSONレスポンス構造設計<br>• エラーレスポンス |

### 認証・セキュリティ編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [04-authentication-authorization.md](./04-authentication-authorization.md) | 62.6 KB | **API認証・認可**<br>• JWT認証システム<br>• OAuth 2.0実装<br>• ロールベースアクセス制御 (RBAC) |

### ドキュメンテーション編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [05-api-documentation.md](./05-api-documentation.md) | 55.0 KB | **APIドキュメンテーション**<br>• OpenAPI 3.0仕様書作成<br>• スキーマ定義<br>• ドキュメント自動生成 |

### AI活用向けサマリー

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [AI-QUICK-REFERENCE.md](./AI-QUICK-REFERENCE.md) | 15-20 KB | **AI向けクイックリファレンス**<br>• 必須チェック項目TOP30<br>• よくある設計ミス<br>• セキュリティチェックリスト |

---

## 🎯 状況別ガイド

### 新規API開発を開始する場合

1. **まず読むべき**: [01-design-principles.md](./01-design-principles.md)
   - API設計の基本哲学を理解
   - 組織の設計方針を確認

2. **次に読むべき**: [02-restful-design-part1.md](./02-restful-design-part1.md)
   - リソース設計の基礎
   - 命名規則の理解

3. **実装時に参照**: 
   - [03a-rest-resource-design.md](./03a-rest-resource-design.md) - リソース設計
   - [03b-rest-http-methods.md](./03b-rest-http-methods.md) - HTTPメソッド選択
   - [04-authentication-authorization.md](./04-authentication-authorization.md) - 認証実装

### コードレビューを実施する場合

1. **AIに読ませる**: [AI-QUICK-REFERENCE.md](./AI-QUICK-REFERENCE.md)
   - チェックポイントが簡潔にまとまっている
   
2. **詳細確認が必要な場合**:
   - [03c-rest-status-codes.md](./03c-rest-status-codes.md) - ステータスコードの正しさ
   - [04-authentication-authorization.md](./04-authentication-authorization.md) - セキュリティ実装

### パフォーマンス問題が発生した場合

1. [03d-rest-query-params.md](./03d-rest-query-params.md) - ページネーション実装
2. [03e-rest-response-format.md](./03e-rest-response-format.md) - レスポンス最適化

### APIドキュメントを作成する場合

1. [05-api-documentation.md](./05-api-documentation.md) - OpenAPI仕様書の書き方

---

## 🔍 トピック別索引

### 認証関連
- JWT認証: [04-authentication-authorization.md](./04-authentication-authorization.md) § 3.1
- OAuth 2.0: [04-authentication-authorization.md](./04-authentication-authorization.md) § 3.2
- RBAC: [04-authentication-authorization.md](./04-authentication-authorization.md) § 3.3

### エラーハンドリング
- ステータスコード: [03c-rest-status-codes.md](./03c-rest-status-codes.md)
- エラーレスポンス: [03e-rest-response-format.md](./03e-rest-response-format.md)

### パフォーマンス最適化
- ページネーション: [03d-rest-query-params.md](./03d-rest-query-params.md) § 2.4.1
- キャッシング: [01-design-principles.md](./01-design-principles.md)

### セキュリティ
- セキュリティ設計原則: [01-design-principles.md](./01-design-principles.md) § 1.2.3
- 認証・認可全般: [04-authentication-authorization.md](./04-authentication-authorization.md)

---

## 💡 ベストプラクティス

### APIを設計する際の黄金律

1. **APIファースト** - 実装前に設計を完成させる
2. **一貫性** - 全エンドポイントで統一されたパターンを使用
3. **セキュリティ** - 認証・認可を最初から組み込む
4. **ドキュメント** - OpenAPI仕様書を常に最新に保つ
5. **バージョニング** - 下位互換性を保ちながら進化させる

### よくある間違い

❌ **避けるべき設計**:
- 動詞を含むURL: `/getUsers`, `/createPost`
- 不適切なHTTPメソッド: GETで副作用のある操作
- 一貫性のないレスポンス構造
- 認証なしのエンドポイント
- バージョニングなしでの破壊的変更

✅ **推奨する設計**:
- 名詞ベースのURL: `/users`, `/posts`
- 適切なHTTPメソッド: POST/PUT/PATCH/DELETEを使い分け
- 統一されたレスポンスフォーマット
- すべてのエンドポイントに認証
- 適切なバージョニング戦略

---

## 🔗 関連ドキュメント

### 他の標準文書
- [../README.md](../README.md) - アーキテクチャ標準全体
- [../../01-coding-standards/](../../01-coding-standards/) - コーディング標準
- [../../03-development-process/](../../03-development-process/) - 開発プロセス

### 外部リソース
- [OpenAPI Specification 3.0](https://swagger.io/specification/)
- [REST API Design Best Practices](https://restfulapi.net/)
- [OAuth 2.0 RFC 6749](https://tools.ietf.org/html/rfc6749)

---

## 📝 ドキュメントのメンテナンス

### 更新履歴

| 日付 | バージョン | 変更内容 |
|------|-----------|---------|
| 2025-11-13 | 2.0.0 | 大規模リファクタリング: 1ファイル(293.8KB)から10ファイルに分割 |
| 2025-10-15 | 1.0.0 | 初版作成 |

### 貢献方法

このドキュメントの改善提案がある場合:
1. 該当ファイルの具体的な問題点を特定
2. 改善案を作成
3. レビュープロセスに従って提出

### フィードバック

- 不明点や質問: [Issue]を作成
- 改善提案: [Pull Request]を送信
- 緊急の問題: [担当チーム]に連絡

---

## 📊 ファイルサイズ一覧

```
api/
├── README.md                            (このファイル)
├── 01-design-principles.md             62.2 KB
├── 02-restful-design-part1.md          37.5 KB
├── 03a-rest-resource-design.md         10.8 KB
├── 03b-rest-http-methods.md            10.7 KB
├── 03c-rest-status-codes.md            15.6 KB
├── 03d-rest-query-params.md            23.8 KB
├── 03e-rest-response-format.md         14.6 KB
├── 04-authentication-authorization.md   62.6 KB
├── 05-api-documentation.md             55.0 KB
└── AI-QUICK-REFERENCE.md               (作成予定)

合計: ~292 KB (元: 293.8 KB)
ファイル数: 10個 (元: 1個)
平均サイズ: ~29 KB
最大サイズ: 62.6 KB
```

---

## ⚠️ 重要な注意事項

1. **元ファイルの扱い**
   - 元の `api-architecture.md` は `_archive/` に移動済み
   - 参照する場合は、この新しい構造を使用してください

2. **AI活用時の推奨**
   - 単一タスクには関連ファイルのみを読み込ませる
   - コードレビューには `AI-QUICK-REFERENCE.md` を優先使用
   - 詳細確認が必要な場合のみ個別ファイルを参照

3. **今後の拡張**
   - GraphQL設計標準（予定）
   - gRPC設計標準（予定）
   - WebSocket設計標準（予定）

---

**このREADMEについて**: このファイルは、API設計標準ドキュメント群のナビゲーションハブです。各ドキュメントへの適切なアクセスを支援し、効率的な情報検索を可能にします。
