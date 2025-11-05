---
title: "テンプレート集 - 使用ガイド"
version: "1.0.0"
last_updated: "2025-11-05"
status: "Active"
owner: "Documentation Team"
---

# テンプレート集

> プロジェクトドキュメント作成を効率化する標準テンプレート集

## 📖 概要

このディレクトリには、日常的に使用する各種ドキュメントの標準テンプレートが含まれています。これらのテンプレートを使用することで、ドキュメント作成時間を大幅に削減し、一貫性のある品質の高いドキュメントを作成できます。

### 目的

- **効率化**: ドキュメント作成時間を50%削減
- **標準化**: 組織全体で統一されたフォーマット
- **品質保証**: 必要な情報の漏れを防止
- **AI連携**: Devin、Cursor等のAIツールとの連携最適化

---

## 📋 利用可能なテンプレート

### 1. プロジェクト関連

#### project-readme-template.md
- **用途**: プロジェクトのREADME作成
- **サイズ**: 約14KB
- **含まれる内容**:
  - プロジェクト概要
  - セットアップ手順
  - 使用方法
  - 開発ガイド
  - デプロイ手順
  - トラブルシューティング

**使用タイミング**: 新規プロジェクト開始時

---

#### technical-proposal-template.md
- **用途**: 技術提案書作成
- **サイズ**: 約15KB
- **含まれる内容**:
  - 提案背景
  - 現状分析
  - 提案内容
  - 技術選定
  - ROI分析
  - 実装計画
  - リスク評価

**使用タイミング**: 新技術導入、アーキテクチャ変更提案時

---

### 2. API・設計関連

#### api-specification-template.md
- **用途**: RESTful API仕様書作成
- **サイズ**: 約17KB
- **含まれる内容**:
  - API概要
  - 認証方法
  - エンドポイント定義
  - リクエスト/レスポンス形式
  - エラーコード
  - レート制限
  - バージョニング

**使用タイミング**: API設計時、外部連携時

---

#### design-document-template.md
- **用途**: システム・機能設計書作成
- **サイズ**: 約17KB
- **含まれる内容**:
  - 設計概要
  - アーキテクチャ設計
  - データ設計
  - インターフェース設計
  - セキュリティ設計
  - パフォーマンス設計

**使用タイミング**: 新機能開発、システム刷新時

---

### 3. テスト関連

#### test-plan-template.md
- **用途**: テスト計画書作成
- **サイズ**: 約14KB
- **含まれる内容**:
  - テスト戦略
  - テスト範囲
  - テストスケジュール
  - リソース計画
  - テストケース
  - 合格基準

**使用タイミング**: リリース前、大規模機能追加時

---

### 4. 開発プロセス関連

#### pull-request-template.md
- **用途**: Pull Request作成
- **サイズ**: 約6KB
- **含まれる内容**:
  - 変更内容
  - 変更理由
  - テスト内容
  - チェックリスト
  - スクリーンショット
  - レビュアー向け情報

**使用タイミング**: 全てのPR作成時

---

#### issue-bug-report-template.md
- **用途**: バグ報告Issue作成
- **サイズ**: 約4KB
- **含まれる内容**:
  - バグの説明
  - 再現手順
  - 期待される動作
  - 実際の動作
  - 環境情報
  - スクリーンショット

**使用タイミング**: バグ発見時

---

#### issue-feature-request-template.md
- **用途**: 機能要望Issue作成
- **サイズ**: 約6KB
- **含まれる内容**:
  - 機能の説明
  - ユースケース
  - 実現したいこと
  - 実装案
  - 代替案
  - 優先度

**使用タイミング**: 新機能提案時

---

### 5. 運用関連

#### incident-report-template.md
- **用途**: インシデントレポート作成
- **サイズ**: 約10KB
- **含まれる内容**:
  - インシデント概要
  - タイムライン
  - 影響範囲
  - 根本原因分析
  - 対応内容
  - 再発防止策

**使用タイミング**: インシデント発生後

---

#### meeting-minutes-template.md
- **用途**: 議事録作成
- **サイズ**: 約7KB
- **含まれる内容**:
  - 会議情報
  - 参加者
  - 議題
  - 議論内容
  - 決定事項
  - アクションアイテム

**使用タイミング**: 重要な会議後

---

## 🚀 使用方法

### 基本的な使い方

1. **テンプレート選択**
   - 目的に合ったテンプレートを選択

2. **コピー**
   - テンプレートファイルをコピー

3. **カスタマイズ**
   - プロジェクトに合わせて内容を編集
   - `[プレースホルダー]` を実際の値に置換

4. **保存**
   - 適切な場所に保存

---

### AIツールとの連携

#### Devin使用例

```
devin-organization-standards/08-templates/api-specification-template.md
を基に、ユーザー認証APIの仕様書を作成してください。

要件:
- OAuth 2.0認証
- JWT トークン
- レート制限: 100 req/min
```

#### Cursor使用例

```
@devin-organization-standards/08-templates/design-document-template.md
を参照して、決済機能の設計書を作成
```

---

## 📊 期待される効果

### Before(テンプレート使用前)

- ドキュメント作成: 4-8時間
- 一貫性: 低い
- 情報漏れ: 頻繁
- レビュー時間: 長い

### After(テンプレート使用後)

- ドキュメント作成: 2-4時間(**50%削減**)
- 一貫性: 高い
- 情報漏れ: 最小限
- レビュー時間: 短縮

---

## ✅ チェックリスト

### テンプレート使用時


---

## 📂 サブディレクトリ構成

このディレクトリには、カテゴリ別のテンプレートを格納するサブディレクトリが含まれています。

| ディレクトリ | 用途 | 状態 | 予定ファイル数 |
|------------|------|------|---------------|
| **code-templates/** ([README](./code-templates/README.md)) | コードテンプレート | 🔵 将来の拡張用（README配置済み） | 5-8ファイル |
| **deployment-templates/** ([README](./deployment-templates/README.md)) | デプロイテンプレート | 🔵 将来の拡張用（README配置済み） | 4-6ファイル |
| **documentation-templates/** | ドキュメントテンプレート | ✅ 1ファイル | - |
| **project-templates/** ([README](./project-templates/README.md)) | プロジェクトテンプレート | 🔵 将来の拡張用（README配置済み） | 3-5ファイル |
| **testing-templates/** ([README](./testing-templates/README.md)) | テストテンプレート | 🔵 将来の拡張用（README配置済み） | 4-6ファイル |

---

### code-templates/ 🔵
**目的**: コード作成を効率化する実装テンプレート集

**含まれる予定のテンプレート**:
- `controller-template.ts` - コントローラークラステンプレート
- `service-template.ts` - サービスクラステンプレート
- `repository-template.ts` - リポジトリパターンテンプレート
- `model-template.ts` - データモデルテンプレート
- `dto-template.ts` - Data Transfer Objectテンプレート
- `test-template.spec.ts` - テストファイルテンプレート
- `dockerfile-template` - Dockerfileテンプレート
- `docker-compose-template.yml` - Docker Composeテンプレート

**期待効果**:
- ボイラープレートコードの自動生成
- プロジェクト間のコード一貫性確保
- 新規ファイル作成時間の削減

**状態**: 🚧 **Phase 2で実装予定**  
**予定時期**: 2025年Q2

---

### deployment-templates/ 🔵
**目的**: デプロイメント設定の標準化テンプレート

**含まれる予定のテンプレート**:
- `kubernetes-deployment-template.yaml` - K8sデプロイメント設定
- `helm-chart-template.yaml` - Helmチャートテンプレート
- `github-actions-template.yml` - GitHub Actions CI/CD
- `terraform-template.tf` - Terraformインフラコード
- `cloudformation-template.yaml` - AWS CloudFormation
- `ansible-playbook-template.yml` - Ansible自動化

**期待効果**:
- インフラ設定の標準化
- デプロイメントエラーの削減
- 環境構築時間の短縮

**状態**: 🚧 **Phase 2で実装予定**  
**予定時期**: 2025年Q2

---

### documentation-templates/ ✅
**目的**: ドキュメント作成テンプレート（補助資料）

**現在のファイル**:
- ✅ `project-documentation-README.md` - プロジェクトドキュメント構造ガイド

**状態**: ✅ **一部完成**  
**追加予定**: 必要に応じて追加

---

### project-templates/ 🔵
**目的**: プロジェクト立ち上げ時の設定ファイルテンプレート

**含まれる予定のテンプレート**:
- `project-structure-template.md` - プロジェクト構造ガイド
- `gitignore-template` - .gitignoreテンプレート（言語別）
- `env-template` - .env.exampleテンプレート
- `makefile-template` - Makefileテンプレート
- `package-json-template` - package.jsonテンプレート

**期待効果**:
- プロジェクト立ち上げ時間の短縮
- 設定ファイルの標準化
- 環境設定ミスの防止

**状態**: 🚧 **Phase 2で実装予定**  
**予定時期**: 2025年Q2

---

### testing-templates/ 🔵
**目的**: テストコード作成テンプレート

**含まれる予定のテンプレート**:
- `unit-test-template.spec.ts` - ユニットテストテンプレート
- `integration-test-template.spec.ts` - 統合テストテンプレート
- `e2e-test-template.spec.ts` - E2Eテストテンプレート（Playwright）
- `load-test-template.js` - 負荷テストテンプレート（K6）
- `test-data-template.json` - テストデータテンプレート
- `mock-template.ts` - モック作成テンプレート

**期待効果**:
- テストカバレッジの向上
- テストコード品質の標準化
- テスト作成時間の削減

**状態**: 🚧 **Phase 2で実装予定**  
**予定時期**: 2025年Q2

---

### サブディレクトリ利用ガイド

#### AIツール（Devin、Cursor等）向け
```yaml
テンプレート選択フロー:
  新規ファイル作成時:
    1. code-templates/ から適切なテンプレートを選択
    2. プロジェクト固有の内容でカスタマイズ
    3. コーディング標準に準拠していることを確認
  
  インフラ構築時:
    1. deployment-templates/ から該当テンプレートを選択
    2. 環境に応じたパラメータを設定
    3. セキュリティチェックリストで検証
  
  テスト作成時:
    1. testing-templates/ からテストタイプに応じたテンプレートを選択
    2. テスト対象に合わせてカスタマイズ
    3. テスト標準に準拠していることを確認
```

#### 開発者向け使用ステップ
1. **テンプレート選択**: 用途に応じたサブディレクトリから選択
2. **コピー**: プロジェクトディレクトリにコピー
3. **カスタマイズ**: プロジェクト固有の内容に編集
4. **検証**: チェックリストで品質確認

---

## 🎯 Phase 2 実装計画

### タイムライン
- **2025年Q1**: 要件定義と設計
- **2025年Q2**: サブディレクトリテンプレート実装
- **2025年Q3**: 運用開始とフィードバック収集

### 期待される効果
- **開発効率**: 新規ファイル作成時間を50%削減
- **品質向上**: テンプレート使用によるミス削減
- **標準化**: プロジェクト間の一貫性向上

---

- [ ] 適切なテンプレートを選択
- [ ] 全てのセクションを確認
- [ ] プレースホルダーを全て置換
- [ ] プロジェクト固有の情報を追加
- [ ] 不要なセクションを削除
- [ ] レビューを実施
- [ ] メタデータ(バージョン、日付)を更新

---

## 🤝 貢献

### 新しいテンプレートの提案

1. 必要性の説明
2. サンプルテンプレート作成
3. Pull Request作成
4. レビュープロセス

### 既存テンプレートの改善

- フィードバックをIssueで報告
- 改善案をPRで提出

---

## 📚 関連ドキュメント

- [ドキュメントガイドライン](../03-development-process/documentation-guidelines.md)
- [コーディング標準](../01-coding-standards/README.md)
- [アーキテクチャ標準](../02-architecture-standards/README.md)

---

## 📞 サポート

質問やフィードバックは:
- Slack: #dev-standards
- Email: engineering-team@example.com

---

**最終更新**: 2025-10-28  
**バージョン**: 1.0.0  
**管理者**: Documentation Team