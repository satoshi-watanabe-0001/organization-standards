# AI使用チェックリスト

## 概要

このドキュメントは、Devin AI組織横断開発標準に基づいたプロジェクト開発時の自己検証チェックリストを提供します。各フェーズで確認すべき項目を網羅し、標準への準拠を保証します。

## 関連ドキュメント
- [AI使用ガイド（メイン）](./AI-USAGE-GUIDE.md) - プロジェクト概要とセクション別ガイド
- [ワークフロー詳細](./AI-WORKFLOWS.md) - 各フェーズの詳細手順
- [プロンプト例集](./AI-PROMPTS.md) - 実践的なプロンプト例

---

## 目次
1. [プロジェクト初期化チェックリスト](#プロジェクト初期化チェックリスト)
2. [開発フェーズチェックリスト](#開発フェーズチェックリスト)
3. [レビュー・品質保証チェックリスト](#レビュー品質保証チェックリスト)
4. [デプロイメントチェックリスト](#デプロイメントチェックリスト)
5. [運用・保守チェックリスト](#運用保守チェックリスト)

---

## プロジェクト初期化チェックリスト

### 環境構築（Tier 3）

#### 開発環境設定
- [ ] **IDE/エディタ設定完了**
  - リンター・フォーマッター設定済み
  - 参照: [06-tools-and-environment/README.md](./06-tools-and-environment/README.md)

- [ ] **バージョン管理設定**
  - Gitリポジトリ初期化
  - .gitignore設定
  - ブランチ戦略確認
  - 参照: [01-coding-standards/version-control.md](./01-coding-standards/version-control.md)

- [ ] **依存関係管理**
  - パッケージマネージャー設定
  - 依存関係ファイル作成（package.json, requirements.txt等）
  - 参照: [01-coding-standards/dependency-management.md](./01-coding-standards/dependency-management.md)

#### 標準確認（Tier 1）
- [ ] **コーディング標準確認**
  - コードスタイルガイド理解
  - 命名規則確認
  - 参照: [01-coding-standards/README.md](./01-coding-standards/README.md)

- [ ] **アーキテクチャ方針確認**
  - システム設計原則理解
  - API設計標準確認
  - 参照: [02-architecture-standards/README.md](./02-architecture-standards/README.md)

- [ ] **セキュリティ要件確認**
  - セキュリティポリシー理解
  - 認証・認可要件確認
  - 参照: [03-security-standards/README.md](./03-security-standards/README.md)

### ドキュメント準備（Tier 3）
- [ ] **プロジェクトREADME作成**
  - テンプレート使用
  - 必須セクション記載
  - 参照: [08-templates/documentation-templates/project-readme-template.md](./08-templates/documentation-templates/project-readme-template.md)

- [ ] **アーキテクチャ設計書作成**
  - システム構成図
  - データフロー図
  - 参照: [08-templates/README.md](./08-templates/README.md)

---

## 開発フェーズチェックリスト

### コーディング（Tier 1）

#### コード品質
- [ ] **コードスタイル準拠**
  - スタイルガイド遵守
  - フォーマッター実行済み
  - 参照: [01-coding-standards/code-style-guide.md](./01-coding-standards/code-style-guide.md)

- [ ] **命名規則準拠**
  - 変数名・関数名が規則に準拠
  - 定数・クラス名が規則に準拠
  - 参照: [01-coding-standards/naming-conventions.md](./01-coding-standards/naming-conventions.md)

- [ ] **コード構成**
  - ディレクトリ構造が標準に準拠
  - モジュール分割適切
  - 参照: [01-coding-standards/code-organization.md](./01-coding-standards/code-organization.md)

#### エラー処理とログ
- [ ] **エラー処理実装**
  - 適切な例外処理
  - エラーメッセージの明確性
  - 参照: [01-coding-standards/error-handling.md](./01-coding-standards/error-handling.md)

- [ ] **ログ実装**
  - ログレベル適切
  - センシティブ情報除外
  - 参照: [01-coding-standards/logging-standards.md](./01-coding-standards/logging-standards.md)

### API開発（Tier 1）
- [ ] **API設計標準準拠**
  - RESTful原則遵守
  - エンドポイント命名規則準拠
  - バージョニング実装
  - 参照: [02-architecture-standards/api-design-standards.md](./02-architecture-standards/api-design-standards.md)

- [ ] **API仕様書作成**
  - OpenAPI/Swagger文書作成
  - リクエスト/レスポンス例記載
  - 参照: [08-templates/README.md](./08-templates/README.md)

### セキュリティ実装（Tier 1）
- [ ] **認証・認可実装**
  - トークンベース認証実装
  - 権限チェック実装
  - 参照: [03-security-standards/README.md](./03-security-standards/README.md)

- [ ] **入力検証実装**
  - サニタイゼーション実装
  - バリデーション実装
  - 参照: [03-security-standards/README.md](./03-security-standards/README.md)

- [ ] **機密情報管理**
  - 環境変数使用
  - シークレット管理ツール使用
  - 参照: [03-security-standards/README.md](./03-security-standards/README.md)

### テスト作成（Tier 2）
- [ ] **単体テスト作成**
  - カバレッジ80%以上
  - エッジケーステスト実装
  - 参照: [04-testing-standards/README.md](./04-testing-standards/README.md)

- [ ] **統合テスト作成**
  - APIエンドポイントテスト
  - データベース統合テスト
  - 参照: [04-testing-standards/README.md](./04-testing-standards/README.md)

- [ ] **E2Eテスト作成**
  - 主要フロー網羅
  - ユーザーシナリオテスト
  - 参照: [04-testing-standards/README.md](./04-testing-standards/README.md)

---

## レビュー・品質保証チェックリスト

### コードレビュー準備（Tier 2）
- [ ] **プルリクエスト作成**
  - 明確なタイトル・説明
  - 変更内容の要約
  - 関連Issue参照
  - 参照: [07-code-review/README.md](./07-code-review/README.md)

- [ ] **セルフレビュー実施**
  - コミット内容確認
  - 不要なコード削除
  - コメント追加
  - 参照: [07-code-review/README.md](./07-code-review/README.md)

### コードレビュー対応（Tier 2）
- [ ] **レビューコメント対応**
  - 指摘事項修正
  - 質問回答
  - 変更理由説明
  - 参照: [07-code-review/README.md](./07-code-review/README.md)

- [ ] **チェックリスト確認**
  - 機能性チェック
  - パフォーマンスチェック
  - セキュリティチェック
  - 参照: [07-code-review/README.md](./07-code-review/README.md)

### セキュリティ監査（Tier 1）
- [ ] **脆弱性スキャン実行**
  - 依存関係スキャン
  - コードスキャン
  - 参照: [03-security-standards/README.md](./03-security-standards/README.md)

- [ ] **セキュリティチェックリスト確認**
  - OWASP Top 10対策確認
  - データ暗号化確認
  - 参照: [03-security-standards/README.md](./03-security-standards/README.md)

### 品質保証（Tier 2）
- [ ] **テスト実行**
  - 全テストパス
  - カバレッジ基準達成
  - 参照: [04-testing-standards/README.md](./04-testing-standards/README.md)

- [ ] **静的解析実行**
  - リンターエラー0
  - 型チェックエラー0
  - 参照: [06-tools-and-environment/README.md](./06-tools-and-environment/README.md)

- [ ] **パフォーマンステスト**
  - レスポンスタイム基準達成
  - リソース使用量確認
  - 参照: [04-testing-standards/README.md](./04-testing-standards/README.md)

---

## デプロイメントチェックリスト

### CI/CD設定（Tier 2）
- [ ] **CI設定完了**
  - ビルド自動化
  - テスト自動実行
  - 静的解析自動実行
  - 参照: [05-cicd-standards/README.md](./05-cicd-standards/README.md)

- [ ] **CD設定完了**
  - 自動デプロイ設定
  - 環境別デプロイ設定
  - ロールバック機能確認
  - 参照: [05-cicd-standards/README.md](./05-cicd-standards/README.md)

### デプロイ前確認
- [ ] **環境変数設定**
  - 本番環境変数設定
  - シークレット設定
  - 参照: [03-security-standards/README.md](./03-security-standards/README.md)

- [ ] **データベースマイグレーション準備**
  - マイグレーションスクリプト確認
  - ロールバックスクリプト準備
  - 参照: [02-architecture-standards/README.md](./02-architecture-standards/README.md)

- [ ] **監視設定**
  - ログ収集設定
  - メトリクス収集設定
  - アラート設定
  - 参照: [06-tools-and-environment/README.md](./06-tools-and-environment/README.md)

### デプロイ実施
- [ ] **ステージング環境デプロイ**
  - デプロイ成功確認
  - スモークテスト実施
  - 参照: [05-cicd-standards/README.md](./05-cicd-standards/README.md)

- [ ] **本番環境デプロイ**
  - デプロイ計画承認
  - メンテナンス通知（必要時）
  - デプロイ実施
  - 動作確認
  - 参照: [05-cicd-standards/README.md](./05-cicd-standards/README.md)

### デプロイ後確認
- [ ] **ヘルスチェック**
  - サービス起動確認
  - エンドポイント疎通確認
  - 参照: [05-cicd-standards/README.md](./05-cicd-standards/README.md)

- [ ] **監視確認**
  - ログ出力確認
  - メトリクス収集確認
  - エラー率確認
  - 参照: [06-tools-and-environment/README.md](./06-tools-and-environment/README.md)

---

## 運用・保守チェックリスト

### 監視・アラート（Tier 3）
- [ ] **日次監視**
  - エラーログ確認
  - パフォーマンスメトリクス確認
  - リソース使用率確認
  - 参照: [06-tools-and-environment/README.md](./06-tools-and-environment/README.md)

- [ ] **アラート対応**
  - アラート原因調査
  - 対応実施
  - インシデント記録
  - 参照: [10-governance/README.md](./10-governance/README.md)

### ドキュメント更新（Tier 3）
- [ ] **ドキュメント最新化**
  - README更新
  - API仕様書更新
  - 変更履歴記録
  - 参照: [08-templates/README.md](./08-templates/README.md)

- [ ] **ナレッジ共有**
  - トラブルシューティングガイド更新
  - ベストプラクティス文書化
  - 参照: [09-reference/README.md](./09-reference/README.md)

### セキュリティ保守（Tier 1）
- [ ] **依存関係更新**
  - 脆弱性確認
  - パッケージ更新
  - テスト実施
  - 参照: [01-coding-standards/dependency-management.md](./01-coding-standards/dependency-management.md)

- [ ] **セキュリティパッチ適用**
  - セキュリティ情報確認
  - パッチ適用
  - 動作確認
  - 参照: [03-security-standards/README.md](./03-security-standards/README.md)

### 継続的改善（Tier 4）
- [ ] **コードベース改善**
  - リファクタリング実施
  - 技術的負債解消
  - 参照: [01-coding-standards/README.md](./01-coding-standards/README.md)

- [ ] **標準準拠確認**
  - 定期的な標準レビュー
  - 新標準の適用
  - 参照: [10-governance/README.md](./10-governance/README.md)

- [ ] **パフォーマンス最適化**
  - ボトルネック特定
  - 最適化実施
  - 効果測定
  - 参照: [04-testing-standards/README.md](./04-testing-standards/README.md)

---

## チェックリスト活用のベストプラクティス

### 1. 定期的な確認
- **日次**: 開発中のコード品質チェック
- **週次**: テスト・レビュー状況確認
- **月次**: 標準準拠確認・ドキュメント更新

### 2. チーム内共有
- プルリクエスト時にチェックリスト使用
- レビュー時の確認項目として活用
- 新メンバーのオンボーディング資料として使用

### 3. カスタマイズ
- プロジェクト特性に応じた項目追加
- 優先度の調整
- チーム固有の要件反映

### 4. 継続的改善
- チェックリストの定期的見直し
- フィードバックの反映
- 新標準の追加

---

## Tier別重要度

### Tier 1（最重要）- 48時間以内習得
- コーディング標準準拠
- セキュリティ要件実装
- アーキテクチャ方針遵守

### Tier 2（品質保証）- 1週間以内習得
- テスト作成・実行
- コードレビュー対応
- CI/CD設定

### Tier 3（効率化）- 必要時参照
- 環境設定
- ドキュメント作成
- 監視設定

### Tier 4（運用管理）- 必要時参照
- 継続的改善
- ガバナンス遵守

---

## 関連リソース

### 標準文書
- [コーディング標準](./01-coding-standards/README.md)
- [アーキテクチャ標準](./02-architecture-standards/README.md)
- [セキュリティ標準](./03-security-standards/README.md)
- [テスト標準](./04-testing-standards/README.md)
- [CI/CD標準](./05-cicd-standards/README.md)
- [ツール・環境](./06-tools-and-environment/README.md)
- [コードレビュー](./07-code-review/README.md)
- [テンプレート](./08-templates/README.md)
- [リファレンス](./09-reference/README.md)
- [ガバナンス](./10-governance/README.md)

### AI使用ガイド
- [AI使用ガイド（メイン）](./AI-USAGE-GUIDE.md)
- [ワークフロー詳細](./AI-WORKFLOWS.md)
- [プロンプト例集](./AI-PROMPTS.md)

---

## まとめ

このチェックリストは、Devin AI組織横断開発標準に準拠した高品質なソフトウェア開発を支援するためのツールです。各フェーズで適切に活用し、標準への準拠を確保してください。

**重要**: チェックリストは最低限の確認項目です。プロジェクトの特性に応じて、追加の確認項目を設定してください。