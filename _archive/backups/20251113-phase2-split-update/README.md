---
title: "Phase 2分割更新バックアップ"
date: "2025-11-13"
backup_id: "20251113-phase2-split-update"
---

# Phase 2分割更新バックアップ

## バックアップ情報

- **作成日時**: 2025-11-13 05:13
- **作成理由**: Phase 2分割対応によるドキュメント修正前のバックアップ
- **対象ファイル**: 3件

## バックアップファイル一覧

### 1. README.md
- **元のパス**: `/devin-organization-standards/README.md`
- **バックアップ名**: `README.md.backup-20251113-051321`
- **サイズ**: 64,565 bytes
- **修正内容**: Phase 0-6 → Phase 0, 1, 2.1, 3, 4, 2.2, 5, 6

### 2. DOCUMENT-USAGE-MANUAL.md
- **元のパス**: `/devin-organization-standards/DOCUMENT-USAGE-MANUAL.md`
- **バックアップ名**: `DOCUMENT-USAGE-MANUAL.md.backup-20251113-051349`
- **サイズ**: 38,720 bytes
- **修正内容**: Phase 2分割構造セクション追加

### 3. AI-MASTER-WORKFLOW-GUIDE.md
- **元のパス**: `/devin-organization-standards/00-guides/AI-MASTER-WORKFLOW-GUIDE.md`
- **バックアップ名**: `AI-MASTER-WORKFLOW-GUIDE.md.backup-20251113-051336`
- **サイズ**: 46,110 bytes
- **修正内容**: Phase 2分割通知追加、ライフサイクル図更新

## 復元方法

バックアップから復元する場合:

```bash
# README.md の復元
cp /mnt/aidrive/devin-organization-standards/_archive/backups/20251113-phase2-split-update/README.md.backup-20251113-051321 \
   /mnt/aidrive/devin-organization-standards/README.md

# DOCUMENT-USAGE-MANUAL.md の復元
cp /mnt/aidrive/devin-organization-standards/_archive/backups/20251113-phase2-split-update/DOCUMENT-USAGE-MANUAL.md.backup-20251113-051349 \
   /mnt/aidrive/devin-organization-standards/DOCUMENT-USAGE-MANUAL.md

# AI-MASTER-WORKFLOW-GUIDE.md の復元
cp /mnt/aidrive/devin-organization-standards/_archive/backups/20251113-phase2-split-update/AI-MASTER-WORKFLOW-GUIDE.md.backup-20251113-051336 \
   /mnt/aidrive/devin-organization-standards/00-guides/AI-MASTER-WORKFLOW-GUIDE.md
```

## 関連ドキュメント

- **修正レポート**: `_archive/reports/PHASE2-SPLIT-UPDATE-REPORT.md`
- **修正後のファイル**: 上記の元のパスを参照

## 保持期間

- **推奨保持期間**: 6ヶ月（2025-05-13まで）
- **削除前確認**: 修正後の運用に問題がないことを確認後に削除可能
