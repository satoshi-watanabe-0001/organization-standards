---
title: "Phase 1 Project Initialization Guide"
version: "2.0.0"
created_date: "2025-11-05"
last_updated: "2025-11-05"
status: "Active"
phase: "Phase 1 - Project Initialization"
owner: "Engineering Leadership Team"
document_type: "Lightweight Navigation Guide"
---

# Phase 1: プロジェクト初期化ガイド

> プロジェクト構造の構築と開発環境のセットアップ - 軽量ナビゲーションガイド

**対象**: Devin、Cursor、その他の自律型AIエージェント  
**前提**: Phase 0で要件分析とタスク分解が完了している  
**目的**: プロジェクトの骨組みを構築し、開発を開始できる状態にする

---

## 📋 このガイドについて

### ドキュメントタイプ
- **軽量ナビゲーション型**: 既存ドキュメントへの参照を重視
- **所要時間**: 5-10分で読める
- **重複回避**: 詳細は既存ドキュメントを参照

### Phase 1 の位置づけ

```
Phase 0: 要件分析・企画 [完了]
    ↓
┌─────────────────────────────────────────┐
│  Phase 1: プロジェクト初期化 ★ここ      │
│  - 技術選定 → 構造構築 → 環境設定       │
└─────────────────────────────────────────┘
    ↓
Phase 2: 設計 へ
```

### スキップ可能性

#### ⚠️ スキップ可能なケース
- **既存プロジェクトへの機能追加**: プロジェクト構造が既に存在
- **Hotfix**: 緊急対応で新規セットアップ不要
- **POC/実験的機能**: 既存の実験用環境を使用

#### 🔴 スキップ不可のケース
- **新規プロジェクト**: 必ずPhase 1を実施
- **大規模リファクタリング**: プロジェクト構造の再構築が必要
- **技術スタック変更**: 新しい技術への移行

---

## ⚠️ 禁止事項チェック

### このフェーズ特有の禁止事項
- **組織標準違反**: 組織のディレクトリ構造、命名規則を無視してはいけない
- **不適切な技術選定**: 組織承認外のフレームワークやライブラリを独断で選定してはいけない
- **CI/CD設定の省略**: 組織標準のCI/CDパイプライン設定を省略してはいけない
- **ドキュメント不足**: README、DEVELOPMENT.md等の必須ドキュメントを省略してはいけない

### 全フェーズ共通禁止事項
- **CI/CD設定の無断変更**: 明示的な指示がない限り、GitHub Actions、CircleCI、Jenkins等のCI/CD設定ファイルを変更してはいけない
- **本番環境への直接変更**: 本番データベース、本番サーバー、本番設定ファイルを直接変更してはいけない
- **セキュリティ設定の独断変更**: 認証・認可・暗号化等のセキュリティ設定を独自判断で変更してはいけない
- **組織標準外技術の無断導入**: プロジェクトで使用していない新しいライブラリ・フレームワーク・言語を独断で導入してはいけない
- **プロジェクト構造の大幅変更**: ディレクトリ構造、命名規則、アーキテクチャパターンを独断で大きく変更してはいけない

### 📚 詳細確認
禁止事項の詳細、具体例、例外ケースについては以下のドキュメントを参照してください：
- [AI開発タスクの禁止事項](../01-organization-standards/ai-task-prohibitions.md)
- [禁止事項チェックリスト](../01-organization-standards/ai-task-prohibitions-checklist.md)

---

## 🎯 Phase 1 の成果物

### 必須成果物
1. **プロジェクト構造** (ディレクトリ、設定ファイル)
2. **開発環境設定ドキュメント** (`DEVELOPMENT.md`)
3. **READMEファイル** (`README.md`)
4. **依存関係管理ファイル** (`package.json`, `requirements.txt`, `pom.xml` 等)

### オプション成果物
5. **ADR（アーキテクチャ決定記録）** (`docs/adr/`)
6. **プロジェクトテンプレート選定記録**
7. **初期CI/CD設定**

---

## 📊 Phase 1 の所要時間

| プロジェクト規模 | 所要時間 | 例 |
|----------------|---------|-----|
| **小規模** | 30-60分 | シンプルなAPI、単一機能 |
| **中規模** | 1-2時間 | マイクロサービス1つ、フルスタックアプリ |
| **大規模** | 2-4時間 | 複数サービス、複雑なインフラ |
| **超大規模** | 4-8時間 | 新規プラットフォーム、複数技術スタック |

---

## 🔄 Phase 1 のワークフロー: 5ステップ

### **Step 1.1: 技術スタックの決定** (15-30分)

**実行内容**:
1. Phase 0の要件に基づいて技術を選定
2. 承認済み技術リストから選択
3. 新規技術の場合は承認プロセスを実施
4. ADRを作成（重要な決定の場合）

**チェックリスト**:
- [ ] バックエンド技術を決定（言語、フレームワーク）
- [ ] フロントエンド技術を決定（該当する場合）
- [ ] データベースを決定
- [ ] インフラ/クラウドプラットフォームを決定
- [ ] 監視/ログツールを決定
- [ ] 承認済み技術リストと照合

**参照ドキュメント**:
- 🔴 **必須**: `../../05-technology-stack/approved-technologies.md` - 承認済み技術リスト
- 🔴 **必須**: `../../05-technology-stack/backend-stack.md` - バックエンド技術選定基準
- 🔴 **必須**: `../../05-technology-stack/frontend-stack.md` - フロントエンド技術選定基準
- 🔴 **必須**: `../../05-technology-stack/database-stack.md` - データベース選定基準
- 🟡 **推奨**: `../../05-technology-stack/infrastructure-stack.md` - インフラ構成
- 🟡 **推奨**: `../../05-technology-stack/monitoring-logging.md` - 監視ツール
- 🟡 **推奨**: `../../10-governance/technology-radar.md` - 技術トレンドと評価
- 🟡 **推奨**: `../../08-templates/adr-template.md` - ADRテンプレート

---

### **Step 1.2: プロジェクトテンプレートの選択** (10-20分)

**実行内容**:
1. 選定した技術スタックに対応するテンプレートを確認
2. テンプレートを適用してプロジェクト構造を生成
3. 組織標準に準拠したディレクトリ構造を構築

**チェックリスト**:
- [ ] 適切なプロジェクトテンプレートを選択
- [ ] テンプレートからプロジェクト構造を生成
- [ ] ディレクトリ構造が組織標準に準拠
- [ ] `.gitignore`, `.editorconfig` 等の設定ファイルを配置

**参照ドキュメント**:
- 🔴 **必須**: `../../08-templates/project-templates/` - プロジェクトテンプレート一覧
- 🔴 **必須**: `../../02-architecture-standards/design-principles.md` - 設計原則
- 🟡 **推奨**: `../../08-templates/project-readme-template.md` - READMEテンプレート
- 🟡 **推奨**: `../../03-development-process/documentation-standards.md` - ドキュメント標準

---

### **Step 1.3: 依存関係の設定** (15-30分)

**実行内容**:
1. パッケージマネージャーの設定ファイルを作成
2. 必要な依存関係（ライブラリ、フレームワーク）をリストアップ
3. バージョン管理戦略を決定（固定 vs 範囲指定）
4. 依存関係のインストールとバリデーション

**チェックリスト**:
- [ ] `package.json` / `requirements.txt` / `pom.xml` を作成
- [ ] 必須ライブラリを追加（フレームワーク、ユーティリティ）
- [ ] 開発用ライブラリを追加（テスト、リント、フォーマッター）
- [ ] バージョンロックファイルを生成（`package-lock.json`, `poetry.lock` 等）
- [ ] 依存関係の脆弱性スキャンを実施

**参照ドキュメント**:
- 🔴 **必須**: `../../05-technology-stack/[言語]-stack.md` - 各技術スタックの依存関係
- 🟡 **推奨**: `../../07-security-compliance/vulnerability-management.md` - 脆弱性管理
- 🟡 **推奨**: `../../10-governance/deprecation-policy.md` - 廃止ポリシー
- ⚪ **参考**: `../../09-reference/best-practices.md` - ベストプラクティス

---

### **Step 1.4: 開発環境の構築** (20-40分)

**実行内容**:
1. IDE/エディタの設定ファイルを配置
2. Linter、Formatter の設定
3. Git hooks の設定（pre-commit, pre-push）
4. 開発用Docker環境の構築（該当する場合）
5. 環境変数テンプレートの作成（`.env.example`）

**チェックリスト**:
- [ ] `.vscode/` または `.idea/` 設定を配置
- [ ] Linter設定ファイルを配置（`.eslintrc`, `.pylintrc` 等）
- [ ] Formatter設定ファイルを配置（`.prettierrc`, `.editorconfig` 等）
- [ ] Git hooks を設定（Husky, pre-commit 等）
- [ ] `.env.example` を作成
- [ ] `DEVELOPMENT.md` を作成（開発環境構築手順）

**参照ドキュメント**:
- 🔴 **必須**: `../../06-tools-and-environment/ide-setup.md` - IDE設定ガイド
- 🔴 **必須**: `../../06-tools-and-environment/linters-formatters.md` - Linter/Formatter設定
- 🔴 **必須**: `../../06-tools-and-environment/recommended-extensions.md` - 推奨拡張機能
- 🟡 **推奨**: `../../03-development-process/git-workflow.md` - Git運用ルール
- 🟡 **推奨**: `../../05-technology-stack/container-standards.md` - Dockerコンテナ標準
- 🟡 **推奨**: `../../08-templates/development-setup-template.md` - 開発環境ドキュメントテンプレート

---

### **Step 1.5: 初期ドキュメントの作成** (15-30分)

**実行内容**:
1. `README.md` の作成（プロジェクト概要、セットアップ手順）
2. `CONTRIBUTING.md` の作成（コントリビューションガイド）
3. `DEVELOPMENT.md` の作成（開発環境構築の詳細）
4. `docs/` ディレクトリの構築（ADR、設計書等の格納場所）

**チェックリスト**:
- [ ] `README.md` を作成（テンプレートから）
- [ ] プロジェクト概要を記載
- [ ] セットアップ手順を記載
- [ ] 実行方法を記載
- [ ] `CONTRIBUTING.md` を作成（該当する場合）
- [ ] `docs/` ディレクトリを作成
- [ ] `docs/adr/` ディレクトリを作成（ADR用）

**参照ドキュメント**:
- 🔴 **必須**: `../../08-templates/project-readme-template.md` - READMEテンプレート
- 🔴 **必須**: `../../03-development-process/documentation-standards.md` - ドキュメント標準
- 🟡 **推奨**: `../../08-templates/contributing-template.md` - コントリビューションガイドテンプレート
- 🟡 **推奨**: `../../08-templates/adr-template.md` - ADRテンプレート
- ⚪ **参考**: `../../09-reference/best-practices.md` - ドキュメント作成のベストプラクティス

---

## 📋 参照ドキュメント優先度マトリクス

Phase 1で参照すべきドキュメントカテゴリ:

| ドキュメントカテゴリ | 優先度 | 主な用途 |
|---------------------|-------|---------|
| **../../05-technology-stack/** | 🔴 必須 | 技術選定基準、承認済み技術リスト |
| **../../10-governance/standards-hierarchy-and-precedence.md** | 🟡 推奨 | 標準の階層と優先度（プロジェクト標準設定時） |
| **../../08-templates/** | 🔴 必須 | プロジェクトテンプレート、ドキュメントテンプレート |
| **../../06-tools-and-environment/** | 🔴 必須 | IDE設定、Linter/Formatter設定 |
| **../../02-architecture-standards/** | 🟡 推奨 | 設計原則、アーキテクチャ方針 |
| **../../03-development-process/** | 🟡 推奨 | Git運用、ドキュメント標準 |
| **../../10-governance/** | 🟡 推奨 | 技術トレンド、ADR、廃止ポリシー |
| **../../07-security-compliance/** | ⚪ 参考 | 脆弱性管理、ライセンス管理 |
| **../../09-reference/** | ⚪ 参考 | ベストプラクティス、用語集 |

---

## ⚠️ よくある課題と対処法

### 課題1: 承認されていない技術を使用したい
**症状**: Phase 0で特定した技術が承認リストにない

**対処法**:
1. `../../10-governance/technology-radar.md` で評価ステータスを確認
2. `../../10-governance/exception-approval-process.md` に従って例外承認を申請
3. ADRを作成して選定理由を文書化
4. 承認が得られない場合は代替技術を検討

**参照**: `../../10-governance/exception-approval-process.md`

---

### 課題2: プロジェクトテンプレートが要件に合わない
**症状**: 既存のテンプレートが要件と合致しない

**対処法**:
1. 最も近いテンプレートを基にカスタマイズ
2. カスタマイズ内容をADRとして文書化
3. 設計原則に準拠しているか確認
4. 再利用可能な場合は新しいテンプレートとして提案

**参照**: `../../02-architecture-standards/design-principles.md`

---

### 課題3: 依存関係の競合
**症状**: ライブラリバージョンが競合する

**対処法**:
1. `package.json` / `requirements.txt` でバージョンを明示的に指定
2. ロックファイルを使用してバージョンを固定
3. 脆弱性スキャンを実施
4. 互換性のあるバージョンの組み合わせを選定

**参照**: `../../07-security-compliance/vulnerability-management.md`

---

### 課題4: 開発環境のセットアップが複雑
**症状**: 環境構築手順が複雑で時間がかかる

**対処法**:
1. Docker環境を構築してセットアップを簡略化
2. セットアップスクリプト（`setup.sh`, `setup.ps1`）を作成
3. `DEVELOPMENT.md` に詳細な手順を記載
4. トラブルシューティングセクションを追加

**参照**: `../../05-technology-stack/container-standards.md`

---

## ✅ Phase 1 完了チェックリスト

Phase 1が完了したと判断する基準:

### 🔴 必須項目
- [ ] プロジェクト構造が構築されている
- [ ] 技術スタックが決定し、文書化されている
- [ ] 依存関係が設定され、インストール可能
- [ ] 開発環境が構築され、動作確認完了
- [ ] `README.md` が作成されている
- [ ] `DEVELOPMENT.md` が作成されている
- [ ] Git リポジトリが初期化されている
- [ ] Linter/Formatter が設定されている

### 🟡 推奨項目
- [ ] ADRが作成されている（重要な決定がある場合）
- [ ] 初期CI/CD設定が完了している
- [ ] Docker環境が構築されている（該当する場合）
- [ ] Git hooks が設定されている
- [ ] `.env.example` が作成されている

### 検証方法
```bash
# プロジェクト構造の確認
ls -la

# 依存関係のインストール確認
npm install  # または pip install -r requirements.txt

# Linterの実行確認
npm run lint  # または flake8 .

# Formatterの実行確認
npm run format  # または black .

# 初期ビルド確認
npm run build  # または python setup.py build
```

---

## 🔄 次のフェーズへの移行

### Phase 2（設計）への移行条件
- ✅ Phase 1の必須項目がすべて完了
- ✅ 開発環境で基本的なビルドが成功
- ✅ チームメンバーが開発環境を構築できる

### 移行時の準備
1. Phase 1の成果物をコミット
2. 開発ブランチを作成（`develop`, `main` 等）
3. Phase 2で必要なドキュメントテンプレートを確認
4. 設計フェーズのタスクリストを準備

**次のステップ**: `phase-2-design-guide.md` を参照

---

## 📚 関連ドキュメント

### Phase 1 で重要なドキュメント
1. **技術選定**: `../../05-technology-stack/` 全ファイル
2. **プロジェクトテンプレート**: `../../08-templates/project-templates/`
3. **開発環境**: `../../06-tools-and-environment/` 全ファイル
4. **設計原則**: `../../02-architecture-standards/design-principles.md`
5. **Git運用**: `../../03-development-process/git-workflow.md`

### 関連するPhaseガイド
- `phase-0-requirements-planning-guide.md` - 前フェーズ
- `phase-2-design-guide.md` - 次フェーズ
- `AI-MASTER-WORKFLOW-GUIDE.md` - 全体ワークフロー
- `DOCUMENT-USAGE-MANUAL.md` - ドキュメント体系全体

---

## 🔄 ガイドの更新

本ガイドは、devin-organization-standards内のドキュメント構成の変更に応じて定期的に更新されます。

- **最終更新日**: 2025-11-05
- **バージョン**: 2.0.0（軽量ナビゲーション型）
- **対象**: devin-organization-standards 全カテゴリ

ドキュメント構成に変更があった場合や、本ガイドの改善提案がある場合は、適切なチャネルを通じてフィードバックをお願いします。

---

## まとめ

**Phase 1 の目的**: プロジェクトの骨組みを構築し、開発を開始できる状態にする

**重要な原則**:
- 承認済み技術リストを優先
- 組織標準のテンプレートを活用
- 開発環境の構築手順を文書化
- ADRで重要な決定を記録

**最も重要なこと**: Phase 1は将来の開発効率に直結します。時間をかけて適切にセットアップすることで、Phase 2以降の開発がスムーズになります。

疑問や矛盾がある場合は、関連するドキュメントを複数参照し、より具体的で新しいドキュメントの記載を優先してください。

---

## 📋 関連チェックリスト

Phase 1を開始・完了する際は、以下のチェックリストを使用してください：

### Phase 1開始前
- [Phase開始前チェックリスト](../../09-reference/checklists/phase-pre-work-checklist.md)

### Phase 1実施中
- [CI/CD設定チェックリスト](../../09-reference/checklists/ci-setup-checklist.md)

### Phase 1完了時
- [Phase 1 完了チェックリスト](../../09-reference/checklists/phase-1-completion-checklist.md)

---
