# PRセルフレビューリマインダー

## 概要

このGitHub Actionsワークフローは、PRが投稿された際に**シンプルなリマインダーコメント**を自動投稿します。
このコメントを受け取ったAIエージェントが、**自律的に組織標準ドキュメントを確認**し、**セルフレビューを実施**します。

---

## 特徴

### CI側（シンプル）
- ✅ **固定メッセージのみ投稿**（約150行のYAML）
- ✅ **チェックリスト確認状態を自動検出**
- ✅ **ラベル自動管理**（`needs-self-review`）
- ✅ **重複投稿を防止**
- ✅ **保守が容易**

### AI側（自律的）
- ✅ `/devin-organization-standards` を自動読み込み
- ✅ PR内容を分析（変更ファイル、言語、種類）
- ✅ 適切なチェックリストを自律的に選定
- ✅ セルフレビューを実施して結果をコメント

---

## 動作フロー

```
1. PRが作成される（opened イベント）
   ↓
2. CI: 既存コメントをチェック
   ↓
3. CI: リマインダーコメントを投稿
   「チェックリストを正しく選定し、確認していますか？」
   ↓
4. AI: コメントを検知
   ↓
5. AI: /devin-organization-standards を読み込み
   - PRテンプレート
   - コードレビュー基準
   - 言語別コーディング規約
   - 品質基準
   - セキュリティ基準 等
   ↓
6. AI: PR内容を分析
   - 変更ファイルのリスト
   - 使用言語の特定
   - 変更タイプの判定（API/UI/Migration等）
   ↓
7. AI: 適切なチェックリストを選定
   ↓
8. AI: 各項目を確認
   ↓
9. AI: レビュー結果をコメント
   - 使用したチェックリスト
   - 確認結果（各項目のチェック状況）
   - 修正内容（必要に応じて）
```

---

## セットアップ方法

### 1. ワークフローファイルを配置

プロジェクトの `.github/workflows/` ディレクトリにコピー:

```bash
# プロジェクトのルートディレクトリで実行
mkdir -p .github/workflows

# 組織標準からコピー
cp devin-organization-standards/08-templates/ci-templates/github-actions/pr-self-review-reminder.yml \
   .github/workflows/
```

### 2. コミット & プッシュ

```bash
git add .github/workflows/pr-self-review-reminder.yml
git commit -m "feat: PRセルフレビューリマインダーを追加"
git push
```

### 3. 完了

設定変更は不要です。次のPRから自動的に動作します。

---

## 投稿されるコメント内容

```markdown
## 📋 セルフレビューリマインダー

@<PR作成者>

PRを投稿いただきありがとうございます！

**マージ前に、組織標準のチェックリストを正しく選定し、確認していますか？**

---

### 🔍 確認手順

1. **適切なチェックリストを選定してください**
   - 変更内容に応じた適切なチェックリストを `devin-organization-standards` から選んでください
   - 複数のチェックリストが該当する場合は、すべて確認してください

2. **セルフレビューを実施してください**
   - 選定したチェックリストに沿って、すべての項目を確認してください
   - 不明な点があれば、ドキュメントを参照するか質問してください

3. **確認完了をコメントしてください**
   - チェックリスト確認完了後、このPRにコメントしてください
   - どのチェックリストを使用したか明記してください

---

### 📚 参考資料

**組織標準ドキュメント**: `/devin-organization-standards`

主なチェックリスト:
- [PRテンプレート](../devin-organization-standards/08-templates/pr-templates/PULL_REQUEST_TEMPLATE.md)
- [コードレビュー基準](../devin-organization-standards/03-development-process/code-review-standards.md)
- [コード品質基準](../devin-organization-standards/04-quality-standards/code-quality-standards.md)
- [コーディング規約](../devin-organization-standards/01-coding-standards/)

---

### ⚠️ 重要

- このリマインダーは**初回PR投稿時のみ**表示されます
- 適切なチェックリストの選定とセルフレビューは**必須**です
- CI品質ゲートがすべて通過するまで、このPRはマージできません

---

**AIエージェントの方へ**: 
このコメントを受け取った場合は、`devin-organization-standards` 内のドキュメントを確認し、
このPRの変更内容に適したチェックリストを選定してセルフレビューを実施してください。
```

---

## AIの期待動作

### 1. リマインダーを検知

AIがPRのコメントとしてリマインダーを検知します。

### 2. ドキュメントを読み込み

AI Driveまたはリポジトリ内の `/devin-organization-standards` から以下を読み込みます:

- `08-templates/pr-templates/PULL_REQUEST_TEMPLATE.md`
- `03-development-process/code-review-standards.md`
- `04-quality-standards/code-quality-standards.md`
- `01-coding-standards/` 配下の言語別規約
- その他、関連するドキュメント

### 3. PR内容を分析

```
例: Pythonファイルの変更 + API追加

→ 分析結果:
  - 言語: Python
  - 変更タイプ: API追加、ビジネスロジック
  - 影響範囲: セキュリティ、パフォーマンス
```

### 4. チェックリストを選定

```
必要なチェックリスト:
✓ PRテンプレート（基本チェック）
✓ Pythonコーディング規約
✓ コードレビュー基準（APIセクション）
✓ セキュリティ基準
```

### 5. セルフレビュー実施

各チェックリストの項目を順に確認:

```markdown
## ✅ セルフレビュー完了

### 使用したチェックリスト
- PRテンプレート (PULL_REQUEST_TEMPLATE.md)
- Pythonコーディング規約 (python-standards.md)
- コードレビュー基準 (code-review-standards.md)
- セキュリティ基準 (security-testing.md)

### 確認結果

#### ✅ 基本チェック
- [x] PRの説明文は日本語で記載
- [x] タイトルも日本語で記載
- [x] 関連Issueをリンク済み

#### ✅ Pythonコーディング規約
- [x] 関数名: スネークケース準拠
- [x] クラス名: パスカルケース準拠
- [x] Docstring: すべての公開関数に記載済み
- [x] Type Hints: 使用済み
- [x] Black/Ruff: フォーマット済み

#### ✅ API設計
- [x] RESTful設計に準拠
- [x] エラーレスポンス定義済み
- [x] API仕様書を更新

#### ✅ セキュリティ
- [x] 入力バリデーション実装
- [x] SQLインジェクション対策（パラメータ化クエリ）
- [x] 認証・認可の実装

#### ✅ テスト
- [x] ユニットテスト追加: 15件
- [x] カバレッジ: 88%
- [x] 正常系・異常系・境界値テスト実施

### 修正した内容
1. `process_user_input()` 関数の複雑度が12だったため、2つに分割
2. エラーハンドリングを追加（ValueError, TypeError, HTTPException）
3. ログレベルを適切に設定

### 補足
すべてのチェック項目をクリアしました。レビュー準備完了です。
```

---

## 従来方式との比較

### ❌ 従来方式: CI主導型

```yaml
# 複雑なCIワークフロー (200行以上)
jobs:
  analyze:
    steps:
      - PRを分析
      - 言語を判定
      - 変更タイプを判定
      - チェックリストを生成（Pythonスクリプト 500行）
      - コメント投稿
```

**問題点**:
- CIが複雑化（700行以上のコード）
- ドキュメント更新のたびにCIも修正が必要
- 新しい言語追加でコード修正が必要
- 保守コストが高い

---

### ✅ 新方式: AI自律型（採用）

```yaml
# シンプルなCIワークフロー (50行)
jobs:
  remind:
    steps:
      - 既存コメントチェック
      - リマインダーコメント投稿（固定メッセージ）
```

**メリット**:
- CIはシンプル（50行のYAMLのみ）
- ドキュメント追加だけで拡張可能
- AIが状況に応じて柔軟に対応
- 保守コストが劇的に低下

---

## メリット

### 1. **シンプルなCI**
- 約50行のYAMLファイルのみ
- 固定メッセージを投稿するだけ
- 複雑なロジック不要

### 2. **保守性が高い**
- ドキュメント更新だけでAIの動作が改善
- CIコードの修正不要
- 新しいルール追加が容易

### 3. **AIの自律性を活用**
- AIがドキュメントから学習
- 状況に応じて適切に判断
- 柔軟な対応が可能

### 4. **拡張性が高い**
- 新しい言語: ドキュメント追加のみ
- 新しいチェック項目: ドキュメント更新のみ
- コード修正不要

---

## カスタマイズ

### 特定ブランチのみで実行

```yaml
on:
  pull_request:
    types: [opened]
    branches:
      - main
      - develop
      - release/*
```

### メッセージの変更

ワークフローファイルの `body` セクションを編集:

```yaml
body: |
  ## 📋 カスタムメッセージ
  
  プロジェクト固有のメッセージをここに追加
```

### ラベルの自動追加

```yaml
- name: Add Label
  uses: actions/github-script@v7
  with:
    script: |
      await github.rest.issues.addLabels({
        owner: context.repo.owner,
        repo: context.repo.repo,
        issue_number: context.issue.number,
        labels: ['needs-self-review']
      });
```

---

## トラブルシューティング

### リマインダーが投稿されない

**確認1**: ワークフローファイルの配置
```bash
ls -la .github/workflows/pr-self-review-reminder.yml
```

**確認2**: GitHub Actions の実行ログ
- Repository → Actions → 該当のワークフローを確認

**確認3**: 権限設定
```yaml
permissions:
  contents: read
  pull-requests: write
  issues: write
```

### 2回目以降も投稿される

**原因**: コメント検出ロジックが動作していない

**解決**: 検索文字列がコメント内に含まれているか確認
```javascript
c.body.includes('チェックリストを正しく選定し、確認していますか')
```

### AIがリマインダーに反応しない

**確認1**: AIエージェントがPRコメントを監視しているか

**確認2**: `/devin-organization-standards` のパスが正しいか

**確認3**: AIに明示的に指示
```markdown
@devin このリマインダーに対応してください
```

---

## 関連ドキュメント

- [PRテンプレート](../pr-templates/PULL_REQUEST_TEMPLATE.md)
- [コードレビュー基準](../../../03-development-process/code-review-standards.md)
- [コード品質基準](../../../04-quality-standards/code-quality-standards.md)
- [CI/CD パイプライン](../../../03-development-process/ci-cd-pipeline.md)

---

## 更新履歴

- **2025-11-10**: 初版作成
  - AI自律型セルフレビューシステムとして実装
  - CI側はシンプルなリマインダーのみ
  - AIが自律的にチェックリストを選定

---

**ファイル名**: `pr-self-review-reminder.yml`  
**場所**: `devin-organization-standards/08-templates/ci-templates/github-actions/`  
**種類**: GitHub Actions ワークフロー  
**バージョン**: 1.0
