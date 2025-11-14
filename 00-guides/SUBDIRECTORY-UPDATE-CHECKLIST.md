---
title: "新規サブディレクトリ追加時の更新チェックリスト"
version: "1.0.0"
created_date: "2025-11-14"
last_updated: "2025-11-14"
status: "Active"
owner: "Documentation Team"
---

# 新規サブディレクトリ追加時の更新チェックリスト

## 📋 目的

新しいサブディレクトリを組織標準フォルダ（01-10）に追加する際、関連ドキュメントの整合性を保つためのチェックリストです。

---

## ✅ 必須作業（Phase 1: サブディレクトリ作成時）

### 1. サブディレクトリの作成

- [ ] 親フォルダ配下に新規サブディレクトリを作成
- [ ] サブディレクトリ内にREADME.mdを作成
- [ ] README.mdにYAMLフロントマター（version, created_date, last_updated等）を追加
- [ ] サブディレクトリの目的、対象、主要ドキュメントを記載

**確認事項**:
- サブディレクトリ名は小文字とハイフン（kebab-case）を使用
- README.mdは必須（どんなに小さいフォルダでも作成）

---

### 2. 親フォルダREADMEの更新

- [ ] 親フォルダのREADME.mdを開く
- [ ] 「サブディレクトリ」または「ディレクトリ構成」セクションを探す
- [ ] 新規サブディレクトリの情報を追加:
  ```markdown
  #### 📂 [サブディレクトリ名]/
  **目的**: 簡潔な説明（1-2行）
  
  **主要ドキュメント**:
  - `ファイル名.md` - 説明
  ```
- [ ] 親フォルダREADMEのメタデータ（last_updated, version）を更新

**確認事項**:
- 他のサブディレクトリと同じフォーマットで記載
- アルファベット順または論理的な順序で配置

---

## ✅ 推奨作業（Phase 2: 00-guides更新）

### 3. 00-guides/README.mdの確認

- [ ] `/devin-organization-standards/00-guides/README.md`を開く
- [ ] 「バージョン履歴」セクションで、親フォルダの変更が記録されているか確認
- [ ] 必要に応じて00-guides/README.mdのバージョンを更新

---

### 4. 主要ガイドドキュメントへの参照追加

新規サブディレクトリが**特定の開発フェーズで参照される重要なリソース**の場合、以下のドキュメントに参照を追加:

#### 4.1 AI-MASTER-WORKFLOW-GUIDE.md

- [ ] `/devin-organization-standards/00-guides/AI-MASTER-WORKFLOW-GUIDE.md`を開く
- [ ] 該当するPhaseセクション（Phase 0-6）を探す
- [ ] 「参照ドキュメント」テーブルまたは「組織標準の参照」リストに追加

**追加基準**:
- Phase 0-1で参照: プロジェクト初期化、技術選定に関連
- Phase 2で参照: 設計標準、アーキテクチャガイドライン
- Phase 3で参照: 実装標準、コード生成、テスト標準
- Phase 4で参照: 品質標準、テスト要件、不具合管理
- Phase 5-6で参照: デプロイメント、運用保守

**フォーマット例**:
```markdown
| 🟡推奨 | 02-architecture-standards | api/ | API設計標準（新規プロジェクト時） |
```
または
```markdown
- `03-development-process/code-generation-standards/` - コード生成標準に準拠
```

#### 4.2 DOCUMENT-USAGE-MANUAL.md

- [ ] `/devin-organization-standards/00-guides/DOCUMENT-USAGE-MANUAL.md`を開く
- [ ] 該当するPhaseセクションのドキュメントリストに追加

**フォーマット例**:
```markdown
**Phase 3: 実装**
- 03-development-process/code-generation-standards/ (コード生成標準)
```

#### 4.3 AI-DELIVERABLE-REFERENCE-GUIDE.md（該当する場合）

- [ ] 新規サブディレクトリが成果物テンプレートに関連する場合のみ
- [ ] `/devin-organization-standards/00-guides/AI-DELIVERABLE-REFERENCE-GUIDE.md`に参照を追加

#### 4.4 AI-PRE-WORK-CHECKLIST.md（該当する場合）

- [ ] 新規サブディレクトリが作業開始前の確認事項に関連する場合のみ
- [ ] `/devin-organization-standards/00-guides/AI-PRE-WORK-CHECKLIST.md`に参照を追加

---

### 5. メタデータの更新

更新した各ガイドドキュメントで:

- [ ] YAMLフロントマターの`last_updated`を今日の日付（YYYY-MM-DD）に更新
- [ ] 必要に応じて`version`を更新（メジャー変更の場合）

---

## 🔍 検証作業（Phase 3: 品質確認）

### 6. 参照整合性の確認

- [ ] 追加した参照パスが実際のフォルダ構造と一致しているか確認
- [ ] 各サブディレクトリにREADME.mdが存在するか確認
- [ ] リンク形式が他の参照と統一されているか確認

**検証コマンド例**:
```bash
# サブディレクトリの存在確認
ls -la /mnt/aidrive/devin-organization-standards/02-architecture-standards/api/

# README存在確認
test -f /mnt/aidrive/devin-organization-standards/02-architecture-standards/api/README.md && echo "✅ README存在" || echo "❌ README不在"
```

---

### 7. ドキュメント間の一貫性確認

- [ ] AI-MASTER-WORKFLOW-GUIDEとDOCUMENT-USAGE-MANUALで同じサブディレクトリへの参照が一致しているか
- [ ] 説明文が統一されているか（完全一致である必要はないが、意味が一貫）
- [ ] 優先度表示（🔴必須/🟡推奨/⚪参考）が適切か

---

## 📊 影響範囲マトリクス

新規サブディレクトリの追加場所に応じた、更新が必要なドキュメントの目安:

| 親フォルダ | 必須更新 | 推奨更新（00-guides） |
|-----------|---------|---------------------|
| 01-coding-standards/ | 親README | AI-MASTER-WORKFLOW-GUIDE (Phase 3), DOCUMENT-USAGE-MANUAL |
| 02-architecture-standards/ | 親README | AI-MASTER-WORKFLOW-GUIDE (Phase 0/2), DOCUMENT-USAGE-MANUAL |
| 03-development-process/ | 親README | AI-MASTER-WORKFLOW-GUIDE (Phase 3), DOCUMENT-USAGE-MANUAL |
| 04-quality-standards/ | 親README | AI-MASTER-WORKFLOW-GUIDE (Phase 4), DOCUMENT-USAGE-MANUAL |
| 05-technology-stack/ | 親README | AI-MASTER-WORKFLOW-GUIDE (Phase 0/1), DOCUMENT-USAGE-MANUAL |
| 06-tools-and-environment/ | 親README | DOCUMENT-USAGE-MANUAL |
| 07-security-compliance/ | 親README | AI-MASTER-WORKFLOW-GUIDE (Phase 2), DOCUMENT-USAGE-MANUAL |
| 08-templates/ | 親README | AI-MASTER-WORKFLOW-GUIDE (Phase 0/3), DOCUMENT-USAGE-MANUAL |
| 09-operations/ | 親README | AI-MASTER-WORKFLOW-GUIDE (Phase 6), DOCUMENT-USAGE-MANUAL |
| 10-governance/ | 親README | DOCUMENT-USAGE-MANUAL |

---

## 🚀 実施タイミング

### 即座（サブディレクトリ作成と同時）
- Phase 1: サブディレクトリ作成時の作業（1-2）

### 当日中
- Phase 2: 00-guides更新（3-5）

### 翌営業日まで
- Phase 3: 品質確認（6-7）

---

## 📝 チェックリスト使用例

### ケース1: 新規サブディレクトリ「03-development-process/code-generation-standards/」を追加

**Phase 1（即座）**:
1. ✅ `code-generation-standards/`フォルダを作成
2. ✅ `code-generation-standards/README.md`を作成（YAMLフロントマター含む）
3. ✅ `03-development-process/README.md`に情報追加
4. ✅ `03-development-process/README.md`のメタデータ更新

**Phase 2（当日中）**:
5. ✅ `00-guides/AI-MASTER-WORKFLOW-GUIDE.md`のPhase 3セクションに参照追加
6. ✅ `00-guides/DOCUMENT-USAGE-MANUAL.md`のPhase 3セクションに参照追加
7. ✅ 両ファイルのメタデータ（last_updated）を更新

**Phase 3（翌営業日まで）**:
8. ✅ パス一致確認、README存在確認
9. ✅ 両ガイド間の一貫性確認

---

## 🔧 自動化の可能性

### 今後の改善案

1. **スクリプト化**:
   - サブディレクトリ作成時に自動でREADMEテンプレートを生成
   - 親フォルダREADMEに自動でエントリ追加

2. **CI/CDチェック**:
   - 新規サブディレクトリ追加時、READMEの存在をチェック
   - 親フォルダREADMEへの記載漏れを検出

3. **定期検証**:
   - 週次で参照整合性を自動チェック
   - メタデータの更新遅延を自動検出

---

## 📚 関連ドキュメント

- `guides-outdated-content-report.md` - 古い記載の調査レポート
- `guides-update-complete-report.md` - 更新完了レポート（2025-11-14実施例）
- `00-guides/README.md` - 00-guidesフォルダの概要

---

## 📞 サポート

このチェックリストに関する質問や改善提案:
- ドキュメントチーム
- プロセス改善チーム

---

**最終更新**: 2025-11-14  
**バージョン**: 1.0.0
