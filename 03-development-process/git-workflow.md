# Git ワークフロー標準
## Git Workflow Standards

**最終更新日**: 2025-10-23  
**バージョン**: 1.0  
**対象**: 全開発者・Devin AI  

---

## 目次

1. [ブランチ戦略](#1-ブランチ戦略)
2. [ブランチ命名規則](#2-ブランチ命名規則)
3. [コミット規約](#3-コミット規約)
4. [プルリクエストプロセス](#4-プルリクエストプロセス)
5. [マージ戦略](#5-マージ戦略)
6. [リリースフロー](#6-リリースフロー)
7. [緊急対応（Hotfix）](#7-緊急対応hotfix)
8. [Devin AI向けガイドライン](#8-devin-ai向けガイドライン)

---

## 1. ブランチ戦略

### 1.1 基本戦略：GitHub Flow + Release Branches

**採用理由**:
- シンプルで理解しやすい
- 継続的デプロイメントに適合
- リリースバージョン管理が可能

### 1.2 主要ブランチ

#### **main ブランチ**
- **役割**: 本番環境の現在の状態を反映
- **保護設定**: 
  - 直接プッシュ禁止
  - 必須レビュー: 2名以上の承認
  - ステータスチェック必須（CI/CD成功）
  - 履歴の書き換え禁止
- **デプロイ**: mainへのマージで自動本番デプロイ

```
main (本番環境と同期)
  ↑
  └─ マージ: プルリクエスト経由のみ
```

#### **develop ブランチ**
- **役割**: 開発統合ブランチ（次リリースの準備）
- **保護設定**:
  - 直接プッシュ禁止
  - 必須レビュー: 1名以上の承認
  - ステータスチェック必須
- **デプロイ**: developへのマージでステージング環境に自動デプロイ

```
develop (ステージング環境と同期)
  ↑
  └─ マージ: フィーチャーブランチから
```

### 1.3 作業ブランチ

#### **feature/* ブランチ（機能開発）**
- **元ブランチ**: develop
- **マージ先**: develop
- **命名**: `feature/issue-番号-簡潔な説明`
- **例**: `feature/123-user-authentication`

#### **bugfix/* ブランチ（バグ修正）**
- **元ブランチ**: develop
- **マージ先**: develop
- **命名**: `bugfix/issue-番号-簡潔な説明`
- **例**: `bugfix/456-login-error`

#### **hotfix/* ブランチ（緊急修正）**
- **元ブランチ**: main
- **マージ先**: main + develop（両方）
- **命名**: `hotfix/バージョン-簡潔な説明`
- **例**: `hotfix/1.2.1-security-patch`

#### **release/* ブランチ（リリース準備）**
- **元ブランチ**: develop
- **マージ先**: main + develop（両方）
- **命名**: `release/バージョン番号`
- **例**: `release/1.3.0`

---

## 2. ブランチ命名規則

### 2.1 命名パターン

```
<タイプ>/<識別子>-<簡潔な説明>
```

### 2.2 タイプ一覧

| タイプ | 用途 | 例 |
|--------|------|-----|
| `feature/` | 新機能開発 | `feature/789-payment-integration` |
| `bugfix/` | バグ修正 | `bugfix/234-null-pointer-fix` |
| `hotfix/` | 緊急修正 | `hotfix/1.0.1-critical-security` |
| `release/` | リリース準備 | `release/2.0.0` |
| `refactor/` | リファクタリング | `refactor/567-optimize-queries` |
| `docs/` | ドキュメント更新 | `docs/890-api-documentation` |
| `test/` | テスト追加/修正 | `test/345-e2e-tests` |
| `chore/` | 雑務（依存更新等） | `chore/678-update-dependencies` |

### 2.3 命名ルール

**必須ルール**:
- 小文字のみ使用
- 単語の区切りは `-` （ハイフン）
- 30文字以内を推奨
- 日本語禁止（英語のみ）

**推奨ルール**:
- Issue番号を含める
- 動詞始まり（add-, fix-, update-等）
- 明確で検索可能な名前

**❌ Bad Examples**:
```
feature/NewFeature          # 大文字使用
bugfix/fix_bug              # アンダースコア使用
feature/ユーザー認証        # 日本語使用
feature/very-long-branch-name-that-explains-everything  # 長すぎる
```

**✅ Good Examples**:
```
feature/123-add-user-auth
bugfix/456-fix-login-error
refactor/789-optimize-db-queries
docs/012-update-api-docs
```

---

## 3. コミット規約

### 3.1 Conventional Commits 採用

**フォーマット**:
```
<type>(<scope>): <subject>

<body>

<footer>
```

### 3.2 Type（必須）

| Type | 説明 | 例 |
|------|------|-----|
| `feat` | 新機能 | `feat(auth): add JWT authentication` |
| `fix` | バグ修正 | `fix(login): resolve null pointer exception` |
| `docs` | ドキュメント | `docs(readme): update installation guide` |
| `style` | コードフォーマット | `style(css): fix indentation` |
| `refactor` | リファクタリング | `refactor(api): simplify error handling` |
| `perf` | パフォーマンス改善 | `perf(db): optimize query performance` |
| `test` | テスト追加/修正 | `test(auth): add unit tests for login` |
| `chore` | ビルド・雑務 | `chore(deps): update dependencies` |
| `ci` | CI/CD設定 | `ci(github): add automated testing` |
| `revert` | コミット取り消し | `revert: revert commit abc1234` |

### 3.3 Scope（オプション）

**推奨Scope**:
- モジュール名: `auth`, `payment`, `user`
- レイヤー名: `api`, `db`, `ui`
- ファイル名: `login`, `dashboard`

### 3.4 Subject（必須）

**ルール**:
- 50文字以内
- 命令形・現在形（"add" not "added"）
- 先頭小文字
- 末尾にピリオド不要
- 日本語禁止（英語のみ）

### 3.5 Body（推奨）

**記載内容**:
- **WHY**: なぜこの変更が必要か
- **WHAT**: 何を変更したか（詳細）
- **HOW**: どのように実装したか（複雑な場合）

**フォーマット**:
- 72文字で折り返し
- 箇条書き可（`-` または `*` 使用）

### 3.6 Footer（条件付き必須）

**Breaking Changes**:
```
BREAKING CHANGE: describe incompatible changes
```

**Issue参照**:
```
Closes #123
Fixes #456, #789
Refs #012
```

### 3.7 コミット例

#### **✅ Good Example 1: シンプルな修正**
```
fix(login): resolve session timeout issue

Users were experiencing unexpected logouts after 5 minutes.
Increased session timeout from 5 to 30 minutes.

Fixes #456
```

#### **✅ Good Example 2: 新機能追加**
```
feat(payment): add Stripe payment integration

Implemented Stripe payment gateway with the following features:
- Credit card payment processing
- Payment history tracking
- Automatic invoice generation

Technical details:
- Used Stripe SDK v3.2.0
- Added webhook endpoint for payment confirmations
- Implemented retry logic for failed transactions

Closes #123
```

#### **✅ Good Example 3: Breaking Change**
```
refactor(api): change authentication endpoint structure

BREAKING CHANGE: Auth endpoint moved from /auth to /api/v2/auth

Migration guide:
- Update all client applications to use new endpoint
- New response format includes refresh_token field
- Old endpoint will be deprecated on 2024-12-31

Refs #789
```

#### **❌ Bad Examples**
```
fix: bug                                    # 不明確
update code                                 # typeなし、scopeなし
feat(auth): ユーザー認証機能を追加         # 日本語使用
Fixed the login bug.                        # 過去形、大文字始まり
```

### 3.8 コミット粒度

**推奨粒度**:
- 1コミット = 1つの論理的変更
- 複雑度10以上の変更は複数コミットに分割
- レビュー可能なサイズ（200-400行が理想）

**コミットタイミング**:
- 機能の完成時
- バグ修正の完成時
- リファクタリング完了時
- テスト追加時

**避けるべきコミット**:
- WIP（Work In Progress）コミット（ドラフトPR除く）
- 複数の無関係な変更を含むコミット
- コンパイルエラー・テスト失敗を含むコミット

---

## 4. プルリクエストプロセス

### 4.1 プルリクエスト作成前チェックリスト

**必須項目**:
- [ ] ローカルで全テストが成功
- [ ] コーディング規約に準拠
- [ ] コンパイルエラーなし
- [ ] 不要なコメント・デバッグコード削除
- [ ] 最新のベースブランチをマージ済み

### 4.2 プルリクエストテンプレート

```markdown
## 概要
<!-- 変更内容を簡潔に説明 -->

## 変更理由
<!-- なぜこの変更が必要か -->

## 変更内容
<!-- 具体的な変更点 -->
- 
- 

## 関連Issue
Closes #

## テスト方法
<!-- レビュアーが検証できる手順 -->
1. 
2. 

## スクリーンショット（該当する場合）
<!-- UI変更がある場合 -->

## チェックリスト
- [ ] テストが追加/更新されている
- [ ] ドキュメントが更新されている
- [ ] Breaking Changeがある場合、マイグレーションガイドを記載
- [ ] レビュー準備完了
```

### 4.3 プルリクエストタイトル

**フォーマット**:
```
[Type] Brief description (#issue-number)
```

**例**:
```
[Feature] Add user authentication (#123)
[Bugfix] Fix login session timeout (#456)
[Hotfix] Patch critical security vulnerability (#789)
```

### 4.4 レビュー依頼

**レビュアー選定基準**:
- コード所有者（CODEOWNERS参照）
- 関連モジュールの専門家
- 最低2名以上（mainブランチへのマージ時）

**レビュー依頼タイミング**:
- ドラフトPR: 設計相談・早期フィードバック
- 通常PR: 実装完了・レビュー準備完了時

### 4.5 レビュープロセス

**レビュアーの責任**:
1. **機能性**: 要件を満たしているか
2. **コード品質**: 保守性メトリクス基準内か
3. **セキュリティ**: 脆弱性がないか
4. **テスト**: 適切なテストがあるか
5. **ドキュメント**: 必要なドキュメントがあるか

**レビューコメントの種類**:
- **MUST**: 必須の修正（マージブロッカー）
- **SHOULD**: 強く推奨される修正
- **NITS**: 軽微な指摘（修正任意）
- **QUESTION**: 質問・確認

**レビュー期限**:
- 通常PR: 24時間以内に初回レビュー
- 緊急PR: 4時間以内に初回レビュー
- Hotfix: 1時間以内に初回レビュー

### 4.6 マージ条件

**必須条件**:
- [ ] 必須レビュー承認数を満たす
- [ ] 全CI/CDチェック成功
- [ ] コンフリクト解消済み
- [ ] 全MUSTコメント対応済み

**推奨条件**:
- [ ] 全SHOULDコメント対応済み
- [ ] カバレッジ基準維持（80%以上）
- [ ] パフォーマンステスト成功（該当する場合）

---

## 5. マージ戦略

### 5.1 マージ方法

#### **Squash and Merge（推奨）**
- **使用場面**: feature/* → develop
- **メリット**: 履歴がクリーン、リバート容易
- **デメリット**: 詳細な履歴が失われる

```
develop: ... ← [F] Add user authentication (#123)
             ↑
feature:     [A]─[B]─[C]  (3コミットを1つに統合)
```

#### **Merge Commit**
- **使用場面**: develop → main, release/* → main/develop
- **メリット**: 完全な履歴保持
- **デメリット**: 履歴が複雑

```
main:    ... ← [M] Merge develop into main
             ↗
develop: ...
```

#### **Rebase and Merge（非推奨）**
- **使用場面**: 特殊なケースのみ
- **注意**: 共有ブランチでは使用禁止

### 5.2 マージ戦略マトリクス

| 元ブランチ | 先ブランチ | マージ方法 | 理由 |
|-----------|-----------|-----------|------|
| feature/* | develop | Squash and Merge | 履歴をクリーンに |
| bugfix/* | develop | Squash and Merge | 履歴をクリーンに |
| develop | main | Merge Commit | リリース履歴保持 |
| release/* | main | Merge Commit | バージョン履歴保持 |
| release/* | develop | Merge Commit | バックマージ |
| hotfix/* | main | Merge Commit | 緊急性・トレーサビリティ |
| hotfix/* | develop | Merge Commit | バックマージ |

---

## 6. リリースフロー

### 6.1 通常リリース手順

#### **Step 1: リリースブランチ作成**
```bash
# developブランチから作成
git checkout develop
git pull origin develop
git checkout -b release/1.3.0
git push origin release/1.3.0
```

#### **Step 2: リリース準備作業**
- バージョン番号更新
- CHANGELOG.md 更新
- リリースノート作成
- 最終テスト実行

```bash
# バージョン更新コミット
git commit -m "chore(release): bump version to 1.3.0"
```

#### **Step 3: mainへマージ（本番リリース）**
```bash
# プルリクエスト作成: release/1.3.0 → main
# タイトル: [Release] Version 1.3.0
# レビュー・承認後マージ
```

#### **Step 4: タグ作成**
```bash
git checkout main
git pull origin main
git tag -a v1.3.0 -m "Release version 1.3.0"
git push origin v1.3.0
```

#### **Step 5: developへバックマージ**
```bash
# プルリクエスト作成: release/1.3.0 → develop
# または main → develop
```

#### **Step 6: リリースブランチ削除**
```bash
git branch -d release/1.3.0
git push origin --delete release/1.3.0
```

### 6.2 セマンティックバージョニング

**フォーマット**: `MAJOR.MINOR.PATCH`

| バージョン | 変更内容 | 例 |
|-----------|----------|-----|
| **MAJOR** | 互換性のない変更 | 1.0.0 → 2.0.0 |
| **MINOR** | 後方互換性のある機能追加 | 1.2.0 → 1.3.0 |
| **PATCH** | 後方互換性のあるバグ修正 | 1.2.3 → 1.2.4 |

**プレリリース**:
- `1.3.0-alpha.1`: アルファ版
- `1.3.0-beta.2`: ベータ版
- `1.3.0-rc.1`: リリース候補

---

## 7. 緊急対応（Hotfix）

### 7.1 Hotfix フロー

#### **Step 1: Hotfix ブランチ作成**
```bash
# mainブランチから作成
git checkout main
git pull origin main
git checkout -b hotfix/1.2.1-security-patch
```

#### **Step 2: 修正実装**
- 最小限の変更に限定
- テスト追加必須
- 迅速なレビュー

#### **Step 3: mainへマージ**
```bash
# プルリクエスト作成: hotfix/* → main
# 緊急レビュー（1時間以内）
# 承認後即座にマージ
```

#### **Step 4: タグ作成**
```bash
git checkout main
git pull origin main
git tag -a v1.2.1 -m "Hotfix: Security patch"
git push origin v1.2.1
```

#### **Step 5: developへバックマージ**
```bash
# プルリクエスト作成: hotfix/* → develop
# または main → develop
```

### 7.2 Hotfix 判断基準

**Hotfix適用すべき状況**:
- セキュリティ脆弱性（CVSS 7.0以上）
- 本番環境でのクリティカルバグ
- データ損失の可能性
- サービス停止

**通常フロー適用すべき状況**:
- 軽微なバグ
- 機能改善
- パフォーマンス最適化（緊急性低）

---

## 8. Devin AI向けガイドライン

### 8.1 ブランチ操作

**ブランチ作成時**:
```bash
# Issueに基づいてブランチ作成
git checkout develop
git pull origin develop
git checkout -b feature/[issue-number]-[brief-description]
git push -u origin feature/[issue-number]-[brief-description]
```

**作業完了時**:
```bash
# 最新のdevelopをマージ
git checkout develop
git pull origin develop
git checkout feature/[your-branch]
git merge develop
# コンフリクト解消
git push origin feature/[your-branch]
```

### 8.2 コミットメッセージ生成

**Devin指示**:
1. 変更内容を分析
2. 適切なtypeを選択
3. scopeを特定
4. 50文字以内のsubject作成
5. 必要に応じてbodyを追加
6. Issue番号をfooterに記載

**例**:
```
feat(auth): implement JWT token refresh mechanism

Added automatic token refresh logic to prevent session expiration.
Tokens are refreshed 5 minutes before expiration.

Implementation details:
- Added refresh_token field to auth response
- Implemented background token refresh timer
- Updated API client to handle token refresh

Closes #123
```

### 8.3 プルリクエスト作成

**Devin指示**:
1. PRテンプレートに従って記述
2. 変更内容を明確に説明
3. テスト方法を具体的に記載
4. 関連Issueをリンク
5. レビュアーを指定

### 8.4 コードレビュー対応

**Devin指示**:
1. レビューコメントを理解
2. MUSTコメントは必ず対応
3. SHOULDコメントは可能な限り対応
4. 質問には明確に回答
5. 対応後にコメントで通知

### 8.5 自動化推奨事項

**Git Hooks**:
```bash
# pre-commit: コミット前チェック
- コーディング規約チェック
- テスト実行
- コミットメッセージ形式チェック

# pre-push: プッシュ前チェック
- 全テスト実行
- ビルド成功確認
```

---

## 9. トラブルシューティング

### 9.1 一般的な問題

#### **問題1: コンフリクト解決**
```bash
# 最新のベースブランチを取得
git checkout develop
git pull origin develop

# フィーチャーブランチでマージ
git checkout feature/your-branch
git merge develop

# コンフリクト解消
# ... 手動でファイルを編集 ...

git add .
git commit -m "chore: resolve merge conflicts"
git push origin feature/your-branch
```

#### **問題2: 誤ったブランチからブランチ作成**
```bash
# 正しいブランチから再作成
git checkout develop
git checkout -b feature/correct-branch
git cherry-pick <commit-hash>  # 必要なコミットのみ適用
```

#### **問題3: コミットメッセージ修正**
```bash
# 最新コミットのメッセージ修正
git commit --amend

# プッシュ済みの場合（注意: 共有ブランチでは禁止）
git push --force-with-lease origin feature/your-branch
```

### 9.2 緊急時の対応

#### **本番環境での問題発覚**
1. Hotfixブランチ作成
2. 最小限の修正
3. 緊急レビュー（1時間以内）
4. 即座にデプロイ
5. 事後検証・ドキュメント更新

---

## 10. ベストプラクティス

### 10.1 DO（推奨）

✅ 小さく頻繁にコミット  
✅ 明確なコミットメッセージ  
✅ 定期的にベースブランチをマージ  
✅ プルリクエスト前にセルフレビュー  
✅ テストを追加  
✅ ドキュメントを更新  

### 10.2 DON'T（禁止）

❌ mainブランチへの直接プッシュ  
❌ 大きすぎるプルリクエスト（1000行超）  
❌ WIPコミットをマージ  
❌ レビューなしでマージ  
❌ 履歴の書き換え（共有ブランチ）  
❌ 複数の無関係な変更を1つのPRに  

---

## 11. リファレンス

### 11.1 便利なGitコマンド

```bash
# ブランチ一覧（リモート含む）
git branch -a

# マージ済みブランチの確認
git branch --merged

# 作業中の変更を一時保存
git stash
git stash pop

# 特定のコミットを現在のブランチに適用
git cherry-pick <commit-hash>

# コミット履歴の確認（グラフ表示）
git log --oneline --graph --all

# ファイルの変更履歴を確認
git log -p <file-path>

# 特定の変更を検索
git log -S "search-term"
```

### 11.2 関連ドキュメント

- [Conventional Commits](https://www.conventionalcommits.org/)
- [Semantic Versioning](https://semver.org/)
- [GitHub Flow](https://docs.github.com/en/get-started/quickstart/github-flow)
- [コードレビュー標準](./code-review-standards.md)

---

## 改訂履歴

| バージョン | 日付 | 変更内容 | 変更者 |
|-----------|------|----------|--------|
| 1.0 | 2025-10-23 | 初版作成 | システム |

---

**このドキュメントは全開発者が遵守すべき標準です。Devin AIは自動的にこれらの規則に従います。**
