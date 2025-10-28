# アーキテクチャ設計標準 / Architecture Standards

**最終更新日**: 2025-10-24  
**バージョン**: 1.0.0  
**対象**: 全開発チーム・テクニカルリード・自律型AI Devin  
**適用範囲**: 全プロジェクト共通アーキテクチャ標準

---

## 📖 概要

このディレクトリは、組織全体で統一されたアーキテクチャ設計標準を定義し、スケーラブルで保守性の高いシステム構築を支援します。特に自律型AI Devinが一貫したアーキテクチャパターンでシステムを設計できるよう、明確で具体的なガイドラインを提供します。

### 🎯 目的

- **一貫性の確保**: 全プロジェクトで統一されたアーキテクチャパターンの採用
- **品質の向上**: 実証済みの設計原則とベストプラクティスの適用
- **生産性の向上**: AI開発者が効率的にシステム設計を実行できる環境の提供
- **技術負債の削減**: 初期段階から適切な設計判断を行うことで将来の負債を防止

---

## 📂 ディレクトリ構成

```
02-architecture-standards/
├── README.md                        # このファイル
├── design-principles.md             # アーキテクチャ設計原則
├── api-architecture.md              # API設計標準
├── microservices-guidelines.md     # マイクロサービス設計
├── database-design.md              # データベース設計標準
├── frontend-architecture.md        # フロントエンド設計
└── security-architecture.md        # セキュリティアーキテクチャ
```

---

## 📋 各ファイルの役割

### `design-principles.md` - 設計原則の基礎
**内容**:
- SOLID原則、DDD（ドメイン駆動設計）、Clean Architecture
- レイヤードアーキテクチャ、ヘキサゴナルアーキテクチャ
- スケーラビリティとパフォーマンスの設計原則
- 疎結合・高凝集の実現方法

**対象読者**: アーキテクト、テクニカルリード、Devin AI

**利用シーン**:
- プロジェクト初期のアーキテクチャ選定時
- 技術負債のリファクタリング計画時
- アーキテクチャレビュー時

---

### `api-architecture.md` - API設計標準
**内容**:
- RESTful API設計原則（リソース指向、HTTPメソッド、ステータスコード）
- GraphQL設計パターン
- APIバージョニング戦略
- エラーハンドリングとレスポンス形式
- APIドキュメンテーション（OpenAPI/Swagger）

**対象読者**: バックエンド開発者、Devin AI

**利用シーン**:
- Web API、マイクロサービスAPI設計時
- API仕様書作成時
- クライアント・サーバー間の通信設計時

---

### `microservices-guidelines.md` - マイクロサービス設計
**内容**:
- サービス分割戦略（ドメイン境界、コンテキストマッピング）
- サービス間通信パターン（同期/非同期、メッセージキュー）
- データ整合性の保証（分散トランザクション、Saga パターン）
- サービスディスカバリー、API Gateway
- 障害分離とサーキットブレーカー

**対象読者**: アーキテクト、マイクロサービス開発者、Devin AI

**利用シーン**:
- モノリスからマイクロサービスへの移行時
- 大規模システムのアーキテクチャ設計時
- サービス間通信の設計時

---

### `database-design.md` - データベース設計標準
**内容**:
- スキーマ設計原則（正規化、非正規化の判断基準）
- インデックス戦略とクエリ最適化
- データモデリング（ER図、論理設計、物理設計）
- トランザクション設計
- RDBMS vs NoSQL の選定基準

**対象読者**: データベースエンジニア、バックエンド開発者、Devin AI

**利用シーン**:
- データベーススキーマ設計時
- パフォーマンス最適化時
- データベース技術選定時

---

### `frontend-architecture.md` - フロントエンド設計
**内容**:
- コンポーネント設計原則（Atomic Design、再利用性）
- 状態管理戦略（Redux、Context API、Zustand）
- ルーティング設計
- パフォーマンス最適化（コード分割、遅延読み込み）
- アクセシビリティとSEO対策

**対象読者**: フロントエンド開発者、Devin AI

**利用シーン**:
- SPAアプリケーション設計時
- コンポーネント設計時
- フロントエンドのパフォーマンス改善時

---

### `security-architecture.md` - セキュリティアーキテクチャ
**内容**:
- セキュアアーキテクチャ設計原則
- ゼロトラストアーキテクチャ
- 認証・認可のアーキテクチャ設計
- データ暗号化戦略
- セキュリティゾーニングとネットワーク分離

**対象読者**: セキュリティエンジニア、アーキテクト、Devin AI

**利用シーン**:
- セキュリティ設計レビュー時
- システム全体のセキュリティ戦略策定時
- 機密情報を扱うシステムの設計時

---

## 🤖 Devin（自律型AI）の利用パターン

### パターン1: 新規プロジェクト設計時
```
1. design-principles.md を参照してアーキテクチャパターンを選択
2. 各専門ファイル（API、DB、フロントエンド等）を参照して詳細設計
3. security-architecture.md でセキュリティ要件を確認
4. 設計ドキュメントを生成
```

**プロンプト例**:
```
以下のアーキテクチャ標準に準拠したシステム設計を行ってください：
- /02-architecture-standards/design-principles.md
- /02-architecture-standards/api-architecture.md
- /02-architecture-standards/database-design.md

要件：[プロジェクト要件を記載]
```

---

### パターン2: 既存システムのリファクタリング時
```
1. design-principles.md の原則と現状を比較
2. 該当する専門ファイルでベストプラクティスを確認
3. リファクタリング計画を策定
4. 段階的な改善を実行
```

**プロンプト例**:
```
以下のアーキテクチャ原則に基づいて、既存システムの問題点を分析してください：
- /02-architecture-standards/design-principles.md
- /02-architecture-standards/microservices-guidelines.md

現状のアーキテクチャ：[現状を記載]
```

---

### パターン3: アーキテクチャレビュー時
```
1. 該当するアーキテクチャファイルを参照
2. チェックリストを使用して設計をレビュー
3. 改善提案を生成
```

**プロンプト例**:
```
以下の標準に基づいてアーキテクチャレビューを実施してください：
- /02-architecture-standards/design-principles.md
- /02-architecture-standards/security-architecture.md

レビュー対象：[設計ドキュメントへのパスまたは内容]
```

---

## 👥 開発チームの利用パターン

### 新規メンバーのオンボーディング
1. **基礎理解**: `design-principles.md`で組織のアーキテクチャ哲学を理解
2. **専門分野習得**: 担当領域（API、DB、フロントエンド等）の標準を学習
3. **実践**: 小規模タスクで標準を適用

### テクニカルリードの利用
1. **技術選定**: プロジェクト開始時に該当ファイルを参照して技術スタック決定
2. **レビュー基準**: コードレビューとアーキテクチャレビューの基準として活用
3. **チーム教育**: 標準を基にしたトレーニング実施

### アーキテクトの利用
1. **標準策定**: 新しいアーキテクチャパターンの評価と標準への反映
2. **技術負債管理**: 既存システムと標準の Gap 分析
3. **横断的ガイダンス**: 複数プロジェクト間でのアーキテクチャ整合性確保

---

## 📊 アーキテクチャ選定フローチャート

```
プロジェクト開始
    ↓
[要件分析]
    ↓
┌─────────────────────────┐
│ 規模・複雑度の評価      │
└───────┬─────────────────┘
        ↓
  ┌─────┴─────┐
  │           │
小規模      大規模・複雑
  │           │
  ↓           ↓
モノリス    マイクロサービス
(Layered)   (microservices-guidelines.md)
  ↓           ↓
[API設計] ←─────┘
(api-architecture.md)
  ↓
[データベース設計]
(database-design.md)
  ↓
[フロントエンド設計]
(frontend-architecture.md)
  ↓
[セキュリティ設計]
(security-architecture.md)
  ↓
設計完了
```

---

## ⚙️ 設計原則の優先順位（Tier分類）

### 🔴 Tier 1: 必須（すべてのプロジェクトで適用）
- `design-principles.md` - 基本設計原則
- `security-architecture.md` - セキュリティ設計
- `api-architecture.md` または `frontend-architecture.md` - プロジェクトタイプに応じて選択

### 🟡 Tier 2: 推奨（プロジェクト特性に応じて適用）
- `database-design.md` - データベースを使用する場合
- `microservices-guidelines.md` - マイクロサービスアーキテクチャ採用時

### 🟢 Tier 3: 任意（必要に応じて参照）
- 特定の技術スタックやパターンに特化したガイドライン

---

## 🔄 更新・ガバナンス

### 標準の更新プロセス
1. **提案**: アーキテクチャチームがIssue作成
2. **議論**: ステークホルダーレビュー（最低2名のアーキテクト承認）
3. **承認**: テクニカルリード最終承認
4. **マージ**: Pull Request経由で更新
5. **通知**: 全開発チームへのアナウンス

### バージョン管理
- **Major**: アーキテクチャパターンの根本的変更（例: モノリス→マイクロサービス）
- **Minor**: 新しい設計パターンの追加
- **Patch**: 誤字修正、軽微な改善

---

## 📚 関連リソース

### 内部ドキュメント
- **コーディング規約**: `/01-coding-standards/`
- **セキュリティ標準**: `/07-security-compliance/`
- **技術スタック**: `/05-technology-stack/`

### 外部参考資料
- [The Twelve-Factor App](https://12factor.net/)
- [Martin Fowler's Architecture Guide](https://martinfowler.com/architecture/)
- [Microsoft Azure Architecture Center](https://docs.microsoft.com/en-us/azure/architecture/)
- [AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/)
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

## ✅ クイックチェックリスト

### 設計開始前の確認事項
- [ ] プロジェクト要件と制約を明確化
- [ ] 適用すべきアーキテクチャパターンを選定
- [ ] 該当する標準ドキュメントをすべて確認
- [ ] セキュリティ要件を確認

### 設計完了時の確認事項
- [ ] `design-principles.md` の原則に準拠
- [ ] API/DB/フロントエンドの設計標準に準拠
- [ ] `security-architecture.md` のセキュリティ要件を満たす
- [ ] スケーラビリティとパフォーマンス要件を考慮
- [ ] ドキュメント化が完了（アーキテクチャ図、設計書等）

---

## 💡 ベストプラクティス

### 設計時の推奨事項
1. **シンプルさを優先**: 必要以上に複雑な設計は避ける（YAGNI原則）
2. **段階的な進化**: 小さく始めて必要に応じて拡張
3. **自動化を考慮**: CI/CD、テスト、デプロイの自動化を設計段階から考慮
4. **監視可能性**: ログ、メトリクス、トレーシングを設計に組み込む

### よくある落とし穴
- ❌ 過剰なマイクロサービス分割（Distributed Monolith）
- ❌ セキュリティの後回し（Security by Design の欠如）
- ❌ データベース設計の軽視（後のパフォーマンス問題の原因）
- ❌ ドキュメント化の欠如（属人化と技術負債の蓄積）

---

## 📞 お問い合わせ

- **アーキテクチャチーム**: architecture@yourorg.com
- **GitHub Issues**: https://github.com/[your-org]/devin-organization-standards/issues
- **Slack**: #architecture-standards

---

**次のステップ**: 各アーキテクチャファイルの詳細を確認し、プロジェクトに適用してください。
