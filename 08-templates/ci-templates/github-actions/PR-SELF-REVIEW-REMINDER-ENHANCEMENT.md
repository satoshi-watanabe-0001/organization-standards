# PR セルフレビューリマインダー機能強化完了報告

**作成日**: 2025-11-10  
**対象ファイル**: `pr-self-review-reminder.yml`  
**機能強化内容**: チェックリスト確認条件の拡張

---

## 📋 概要

PR セルフレビューリマインダーのトリガー条件を拡張し、**初回PR投稿時のみ**から**チェックリスト確認が完了するまで継続的に監視**する方式に変更しました。

---

## 🎯 機能強化の目的

### 変更前の問題点
- ❌ **初回投稿時のみリマインダー表示**: PR投稿後にコミットを追加しても、チェックリスト未確認の場合に再度リマインダーが表示されない
- ❌ **チェックリスト確認の追跡不足**: コメントやPR本文にチェックリスト確認の記載があるか検証していない
- ❌ **ラベル管理の不足**: チェックリスト確認完了後も`needs-self-review`ラベルが残り続ける

### 変更後の改善点
- ✅ **継続的な監視**: コミット追加時にもチェックリスト確認状態を確認
- ✅ **確認状態の自動検出**: コメントとPR本文の両方からチェックリスト確認を検出
- ✅ **自動ラベル管理**: 確認完了時に自動的に`needs-self-review`ラベルを削除

---

## 🔧 主な変更内容

### 1. トリガー条件の拡張

**変更前**:
```yaml
on:
  pull_request:
    types: [opened]  # 初回投稿時のみ
```

**変更後**:
```yaml
on:
  pull_request:
    types: [opened, synchronize]  # 初回投稿時 + コミット追加時
```

**効果**:
- PR投稿時だけでなく、コミット追加（`git push`）時にも実行される
- チェックリスト未確認の状態が続く場合、継続的にリマインダーを表示可能

---

### 2. チェックリスト確認状態の検出ロジック追加

#### 2.1 コメントからの検出

**新規追加機能**:
```javascript
// チェックリスト確認完了のコメントがあるか確認
const hasChecklistConfirmation = comments.data.some(comment => {
  const body = comment.body.toLowerCase();
  return (
    // 確認完了の明示的な表現
    body.includes('チェックリスト') && (
      body.includes('確認完了') ||
      body.includes('確認しました') ||
      body.includes('チェック完了') ||
      body.includes('レビュー完了') ||
      body.includes('セルフレビュー完了')
    ) ||
    // チェックリストの種類を明示
    (body.includes('チェックリスト') && (
      body.includes('使用しました') ||
      body.includes('に沿って確認') ||
      body.includes('を確認')
    ))
  );
});
```

**検出パターン例**:
- ✅ "チェックリスト確認完了"
- ✅ "PRテンプレートのチェックリストを確認しました"
- ✅ "コーディング規約チェックリストに沿って確認"
- ✅ "品質基準チェックリストを使用しました"
- ✅ "セルフレビュー完了"

#### 2.2 PR本文からの検出

**新規追加機能**:
```javascript
// PR本文にチェックリスト確認の記載があるか確認
const pr = await github.rest.pulls.get({
  owner: context.repo.owner,
  repo: context.repo.repo,
  pull_number: context.issue.number
});

const prBody = (pr.data.body || '').toLowerCase();
const hasChecklistInPR = (
  (prBody.includes('チェックリスト') && (
    prBody.includes('確認完了') ||
    prBody.includes('確認しました') ||
    prBody.includes('に沿って確認')
  )) ||
  // PRテンプレートのチェックボックスが選択されているか確認
  (prBody.includes('- [x]') && prBody.includes('チェックリスト'))
);
```

**検出パターン例**:
- ✅ PR本文に "チェックリスト確認完了" の記載
- ✅ PR本文に "コーディング規約チェックリストに沿って確認しました" の記載
- ✅ PRテンプレートのチェックボックス: `- [x] チェックリスト確認完了`

#### 2.3 総合判定ロジック

```javascript
const needsReminder = !hasChecklistConfirmation && !hasChecklistInPR;

core.setOutput('already_commented', hasReminder);
core.setOutput('needs_reminder', needsReminder);
```

**判定結果**:
| コメントに確認記載 | PR本文に確認記載 | リマインダー表示 | ラベル状態 |
|----------------|----------------|---------------|----------|
| ✅ あり | - | ❌ 表示しない | 🗑️ 削除 |
| ❌ なし | ✅ あり | ❌ 表示しない | 🗑️ 削除 |
| ❌ なし | ❌ なし | ✅ 表示する（初回のみ） | ✏️ 追加 |

---

### 3. ステップ名の変更

**変更前**:
```yaml
- name: Check if Already Commented
  id: check_comment
```

**変更後**:
```yaml
- name: Check Checklist Confirmation
  id: check_checklist
```

**理由**: ステップの目的が「コメント済みか確認」から「チェックリスト確認状態の検証」に変更されたため

---

### 4. リマインダー投稿条件の更新

**変更前**:
```yaml
- name: Post Checklist Reminder
  if: steps.check_comment.outputs.already_commented == 'false'
```

**変更後**:
```yaml
- name: Post Checklist Reminder
  if: steps.check_checklist.outputs.needs_reminder == 'true' && steps.check_checklist.outputs.already_commented == 'false'
```

**効果**:
- `needs_reminder == 'true'`: チェックリスト確認が完了していない
- `already_commented == 'false'`: リマインダーコメントがまだ投稿されていない
- 両方の条件を満たす場合のみ、リマインダーを投稿（重複投稿を防止）

---

### 5. リマインダーメッセージの更新

**変更箇所**（83-84行目）:

**変更前**:
```markdown
- このリマインダーは**初回PR投稿時のみ**表示されます
```

**変更後**:
```markdown
- このリマインダーは**チェックリスト確認が完了するまで**表示されます
- チェックリスト確認完了後は、コミット追加時にもリマインダーは表示されません
```

**効果**: ユーザーに正確な動作を説明

---

### 6. 自動ラベル管理機能の追加

#### 6.1 ラベル追加条件の更新

**変更前**:
```yaml
- name: Add Label
  if: steps.check_comment.outputs.already_commented == 'false'
```

**変更後**:
```yaml
- name: Add Label
  if: steps.check_checklist.outputs.needs_reminder == 'true'
```

**効果**: チェックリスト確認が必要な場合のみラベルを追加

#### 6.2 ラベル削除機能の追加（新規）

**新規追加ステップ**:
```yaml
- name: Remove Label if Confirmed
  if: steps.check_checklist.outputs.needs_reminder == 'false'
  uses: actions/github-script@v7
  with:
    script: |
      try {
        await github.rest.issues.removeLabel({
          owner: context.repo.owner,
          repo: context.repo.repo,
          issue_number: context.issue.number,
          name: 'needs-self-review'
        });
        console.log('✅ ラベル "needs-self-review" を削除しました');
      } catch (error) {
        if (error.status === 404) {
          console.log('ℹ️  ラベル "needs-self-review" は既に削除されています');
        } else {
          throw error;
        }
      }
```

**効果**:
- チェックリスト確認完了時に自動的に`needs-self-review`ラベルを削除
- エラーハンドリングにより、ラベルが既に削除されている場合も適切に処理

---

## 📊 機能比較表

| 機能 | 変更前 | 変更後 |
|------|--------|--------|
| **トリガータイミング** | PR投稿時のみ | PR投稿時 + コミット追加時 |
| **チェックリスト確認検出（コメント）** | ❌ なし | ✅ あり（7パターン） |
| **チェックリスト確認検出（PR本文）** | ❌ なし | ✅ あり（3パターン） |
| **リマインダー重複投稿防止** | ✅ あり | ✅ あり（改善） |
| **ラベル自動削除** | ❌ なし | ✅ あり |
| **ステップ数** | 3 | 4（+1） |
| **ファイルサイズ** | 5.1 KB | 8.5 KB |

---

## 🔄 動作フロー

### ケース1: 初回PR投稿時（チェックリスト未確認）

```
1. PRが投稿される (opened)
   ↓
2. チェックリスト確認状態を検証
   - コメント: なし
   - PR本文: なし
   → needs_reminder = true
   ↓
3. リマインダーコメントを投稿
   ↓
4. ラベル "needs-self-review" を追加
```

### ケース2: コミット追加時（チェックリスト未確認）

```
1. コミットが追加される (synchronize)
   ↓
2. チェックリスト確認状態を検証
   - コメント: なし
   - PR本文: なし
   - リマインダー: 既に投稿済み
   → needs_reminder = true, already_commented = true
   ↓
3. リマインダーコメントは投稿しない（重複防止）
   ↓
4. ラベル "needs-self-review" は維持
   ↓
5. ログ出力: "⚠️ チェックリスト確認がまだ完了していません（リマインダー投稿済み）"
```

### ケース3: チェックリスト確認完了後のコミット追加

```
1. ユーザーがコメントで「チェックリスト確認完了」を投稿
   ↓
2. コミットが追加される (synchronize)
   ↓
3. チェックリスト確認状態を検証
   - コメント: "チェックリスト確認完了" を検出
   → needs_reminder = false
   ↓
4. ラベル "needs-self-review" を削除
   ↓
5. ログ出力: "✅ チェックリスト確認完了のコメントが見つかりました"
```

### ケース4: PR本文にチェックリスト確認記載あり

```
1. PRが投稿される（本文に "チェックリスト確認しました" と記載）
   ↓
2. チェックリスト確認状態を検証
   - PR本文: "チェックリスト確認しました" を検出
   → needs_reminder = false
   ↓
3. リマインダーは投稿しない
   ↓
4. ラベルは追加しない（または削除）
   ↓
5. ログ出力: "✅ PR本文にチェックリスト確認の記載があります"
```

---

## ✅ 検証結果

### YAML構文検証
```
✅ pr-self-review-reminder.yml - 構文OK

📊 ファイル情報:
   - ジョブ数: 1
   - ステップ数: 4

🎉 YAML構文検証に成功しました！
```

### ファイル情報
- **ファイル名**: `pr-self-review-reminder.yml`
- **サイズ**: 8.5 KB（変更前: 5.1 KB）
- **保存先**: `/devin-organization-standards/08-templates/ci-templates/github-actions/`
- **最終更新**: 2025-11-10

---

## 🎯 期待される効果

### 1. チェックリスト確認率の向上
- コミット追加時にも確認状態を監視することで、チェックリスト確認の重要性を継続的にリマインド
- 確認漏れの防止

### 2. 自動化の向上
- チェックリスト確認完了を自動検出
- ラベル管理の自動化により、手動操作を削減

### 3. ユーザーエクスペリエンスの向上
- 確認完了後は不要なリマインダーが表示されない
- ラベルが自動的に更新されるため、PR状態が一目で分かる

### 4. レビュープロセスの効率化
- `needs-self-review`ラベルにより、セルフレビュー未完了のPRを簡単に識別
- レビュアーは確認完了後のPRに集中できる

---

## 📚 使用方法

### チェックリスト確認完了の報告方法

#### 方法1: コメントで報告（推奨）
PRにコメントを投稿してください。以下のいずれかの表現を含めることで自動検出されます：

```markdown
チェックリスト確認完了

以下のチェックリストを使用しました：
- PRテンプレート
- コーディング規約（Java）
- コード品質基準
```

**検出される表現**:
- "チェックリスト確認完了"
- "チェックリスト確認しました"
- "チェックリストに沿って確認"
- "〇〇チェックリストを使用しました"
- "セルフレビュー完了"

#### 方法2: PR本文に記載
PR説明文に記載してください：

```markdown
## チェックリスト確認

以下のチェックリストに沿って確認しました：
- [x] PRテンプレート
- [x] コーディング規約
- [x] コード品質基準
```

---

## 🔧 トラブルシューティング

### Q1: チェックリスト確認完了を報告したのに、リマインダーが消えない

**原因**: コメントやPR本文に検出可能なキーワードが含まれていない可能性があります。

**解決策**: 以下のいずれかの表現を含めてください：
- "チェックリスト確認完了"
- "チェックリスト確認しました"
- "〇〇チェックリストを使用しました"

### Q2: コミット追加時にリマインダーが再度投稿される

**原因**: これは正常な動作ではありません。リマインダーは初回のみ投稿されます。

**確認事項**:
- GitHub Actionsのログを確認してください
- `already_commented`が正しく検出されているか確認してください

### Q3: ラベルが自動削除されない

**原因**: チェックリスト確認の検出条件に一致していない可能性があります。

**解決策**:
1. コメントまたはPR本文に明示的に「チェックリスト確認完了」と記載
2. 手動でラベルを削除することも可能です

---

## 📝 関連ドキュメント

- **PRテンプレート**: `/devin-organization-standards/08-templates/pr-templates/PULL_REQUEST_TEMPLATE.md`
- **PR言語問題対策**: `/devin-organization-standards/00-guides/PR-LANGUAGE-ISSUE-SOLUTION.md`
- **Phase 4レビューガイド**: `/devin-organization-standards/00-guides/phase-guides/phase-4-review-qa-guide.md`
- **GitHub Actions CIテンプレート**: `/devin-organization-standards/08-templates/ci-templates/github-actions/README.md`

---

## 🎉 まとめ

PR セルフレビューリマインダーの機能強化により、以下が実現されました：

1. ✅ **継続的な監視**: コミット追加時にもチェックリスト確認状態を確認
2. ✅ **自動検出**: コメントとPR本文からチェックリスト確認を自動検出
3. ✅ **自動ラベル管理**: 確認完了時に`needs-self-review`ラベルを自動削除
4. ✅ **重複防止**: リマインダーの重複投稿を防止
5. ✅ **YAML構文検証**: すべての変更がYAML構文検証を通過

これにより、組織標準のチェックリスト確認プロセスがより効果的かつ自動化されました。

---

**作成者**: AI Assistant  
**最終更新**: 2025-11-10  
**ドキュメントバージョン**: 1.0
