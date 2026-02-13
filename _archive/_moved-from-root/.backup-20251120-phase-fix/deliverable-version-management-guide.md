---
title: "成果物バージョン管理ガイド"
version: "1.0.0"
date: "2025-11-19"
status: "提案"
author: "AI Autonomous Development Analysis"
target_location: "03-development-process/deliverable-version-management-guide.md"
---

# 成果物バージョン管理ガイド

## 📋 このドキュメントについて

### 目的
開発プロセス全体を通じて、**成果物がどのように進化するか**を明確にし、適切なバージョン管理を行う。

### 対象
- 自律型AIエージェント
- 開発者
- プロジェクトマネージャー

### 重要性
Phase間で同じ成果物が更新される場合（特にPhase 2.1と2.2）、バージョン管理を明確にすることで:
- ✅ 混乱を防止
- ✅ 進捗を可視化
- ✅ 適切なタイミングで更新

---

## 📊 成果物のライフサイクル

### 基本概念

```
成果物の状態:
  1. 未作成 (Not Created)
  2. ドラフト版 (Draft) - Phase 2.1等で作成
  3. 暫定版 (Interim) - Phase 3実装中に更新
  4. 完成版 (Final) - Phase 2.2等で完成
  5. 承認済み (Approved) - レビュー・承認完了
```

### バージョン番号ルール

```yaml
バージョン形式: "v[Major].[Minor].[Patch]"

Major (1.x.x):
  - フェーズ間の大きな変更
  - 例: Phase 2.1 → Phase 2.2

Minor (x.1.x):
  - 同一フェーズ内の機能追加
  - 例: APIエンドポイント追加

Patch (x.x.1):
  - 軽微な修正
  - 例: タイポ修正、フォーマット調整
```

---

## 📂 Phase別成果物バージョン管理

### Phase 2.1 → Phase 2.2での進化

#### 1. ADR (Architecture Decision Record)

| フェーズ | バージョン | 状態 | 内容 | ファイル名 |
|---------|----------|------|------|-----------|
| Phase 1 | v0.1.0 | Draft | 初期技術選定 | `ADR-001-typescript-v0.1.0.md` |
| Phase 2.1 | v0.2.0 | Draft | アーキテクチャ決定追加 | `ADR-004-layered-arch-v0.2.0.md` |
| Phase 3 | v0.3.0 | Interim | 実装中の追加決定 | `ADR-005-error-handling-v0.3.0.md` |
| Phase 2.2 | v1.0.0 | Final | 全決定事項の確定版 | `ADR-001-typescript-v1.0.0.md` |
| Phase 4 | v1.0.0 | Approved | レビュー承認済み | `ADR-001-typescript-v1.0.0.md` |

**命名規則**:
```
docs/adr/
  ├── ADR-001-typescript-selection.md         (最新版へのシンボリックリンク)
  ├── versions/
  │   ├── ADR-001-typescript-v0.1.0.md       (Phase 1版)
  │   └── ADR-001-typescript-v1.0.0.md       (Phase 2.2版)
```

**ベストプラクティス**:
- ✅ 最新版のみをメインディレクトリに配置
- ✅ 旧バージョンは`versions/`サブディレクトリに保管
- ✅ 各バージョンにメタデータを記載

**メタデータ例**:
```yaml
---
adr_number: "001"
title: "TypeScript Selection"
version: "1.0.0"
status: "Approved"
date: "2025-11-19"
phase: "Phase 2.2"
supersedes: "v0.1.0"
---
```

---

#### 2. API仕様書

| フェーズ | バージョン | 状態 | 内容 | 完成度 |
|---------|----------|------|------|-------|
| Phase 2.1 | v0.1.0 | Draft | API契約書（基本仕様） | 30% |
| Phase 3 | v0.5.0 | Interim | 実装ベースの詳細追加 | 70% |
| Phase 2.2 | v1.0.0 | Final | 完全版API仕様書 | 100% |
| Phase 4 | v1.0.0 | Approved | レビュー承認済み | 100% |

**v0.1.0 (Phase 2.1) の内容**:
```yaml
# API契約書 v0.1.0

含まれる内容:
  - エンドポイント一覧
  - 基本的なリクエスト/レスポンス形式
  - 認証方式の概要
  - ステータスコード（主要なもの）

含まれない内容:
  - 詳細なエラーケース
  - バリデーションルール詳細
  - レート制限の実装詳細
  - サンプルコード
```

**v1.0.0 (Phase 2.2) の内容**:
```yaml
# 完全版API仕様書 v1.0.0

追加された内容:
  - 全エラーケースの詳細
  - バリデーションルール完全版
  - レート制限の実装詳細
  - サンプルリクエスト/レスポンス
  - 認証フローの詳細図
  - パフォーマンス特性
  - 制限事項と既知の問題
```

**ファイル構成**:
```
docs/api/
  ├── openapi-spec.yaml                  (最新版)
  ├── versions/
  │   ├── openapi-spec-v0.1.0.yaml      (Phase 2.1版)
  │   ├── openapi-spec-v0.5.0.yaml      (Phase 3版)
  │   └── openapi-spec-v1.0.0.yaml      (Phase 2.2版)
  └── changelog.md                       (変更履歴)
```

**changelog.md 例**:
```markdown
# API仕様書 変更履歴

## v1.0.0 (2025-11-19) - Phase 2.2
### Added
- 全エンドポイントのエラーケース詳細
- バリデーションルール完全版
- サンプルコード

### Changed
- レスポンス形式の詳細化
- 認証フローの図解追加

## v0.5.0 (2025-11-17) - Phase 3
### Added
- 実装ベースの詳細追加
- 追加エンドポイント

## v0.1.0 (2025-11-15) - Phase 2.1
### Added
- 初版リリース
- エンドポイント一覧
- 基本仕様
```

---

#### 3. 設計書

| フェーズ | バージョン | 状態 | 内容 | ページ数 |
|---------|----------|------|------|---------|
| Phase 2.1 | - | - | 未作成（Phase 2.1では作らない） | 0 |
| Phase 2.2 | v1.0.0 | Final | 詳細設計書 | 30ページ |
| Phase 4 | v1.0.0 | Approved | レビュー承認済み | 30ページ |

**重要**: Phase 2.1では設計書は作成しない
- Phase 2.1: ADR + API契約 + 制約条件のみ
- Phase 2.2: 完全な設計書を新規作成

---

#### 4. アーキテクチャ図

| フェーズ | バージョン | 状態 | 内容 | 詳細度 |
|---------|----------|------|------|-------|
| Phase 2.1 | v0.1.0 | Draft | アーキテクチャ概要図 | 概要 |
| Phase 2.2 | v1.0.0 | Final | 完全版アーキテクチャ図 | 詳細 |

**v0.1.0 vs v1.0.0**:

```
v0.1.0 (Phase 2.1):
  システム全体構成
  ├── Frontend
  ├── Backend
  └── Database
  
  詳細度: 低（箱と矢印）

v1.0.0 (Phase 2.2):
  システム全体構成
  ├── Frontend (React)
  │   ├── Pages
  │   ├── Components
  │   └── Services
  ├── Backend (Express)
  │   ├── Controllers
  │   ├── Services
  │   ├── Repositories
  │   └── Middlewares
  └── Database (PostgreSQL)
      ├── users テーブル
      └── インデックス
  
  詳細度: 高（クラス・メソッドレベル）
```

---

## 📋 バージョン管理テンプレート

### メタデータブロック（すべての成果物に追加）

```yaml
---
# 成果物メタデータ
document_type: "[ADR/API Spec/Design Doc/etc]"
title: "[タイトル]"
version: "1.0.0"
status: "Draft/Interim/Final/Approved"
created_date: "YYYY-MM-DD"
last_updated: "YYYY-MM-DD"
phase: "Phase X.X"
author: "[作成者]"
reviewers: ["[レビュアー1]", "[レビュアー2]"]
supersedes: "v0.x.x"  # 前バージョン（ある場合）
related_documents:
  - "[関連ドキュメント1]"
  - "[関連ドキュメント2]"
---
```

### バージョン更新時のチェックリスト

```yaml
更新前:
  - [ ] 現在のバージョン番号を確認
  - [ ] 変更内容を明確化
  - [ ] 適切なバージョン番号を決定（Major/Minor/Patch）

更新中:
  - [ ] メタデータのversionを更新
  - [ ] last_updatedを更新
  - [ ] supersedes（前バージョン）を記載
  - [ ] changelog.mdに変更内容を記録

更新後:
  - [ ] 旧バージョンをversions/に移動
  - [ ] 最新版をメインディレクトリに配置
  - [ ] 関連ドキュメントのリンクを更新
```

---

## 🔄 Phase間での成果物の流れ

### 全体フロー

```
Phase 2.1 (事前設計)
  ↓ 作成
  ADR v0.2.0 (Draft)
  API契約 v0.1.0 (Draft)
  制約条件 v1.0.0 (Final)  ← Phase 2.1で完成
  ↓
Phase 3 (実装)
  ↓ 実装中に更新
  ADR v0.3.0 (Interim)  ← 追加決定事項
  API仕様 v0.5.0 (Interim)  ← 実装ベースの詳細
  ↓
Phase 4 (レビュー・QA)
  ↓ レビュー
  バグ修正、軽微な調整
  ↓
Phase 2.2 (詳細設計)
  ↓ 完成版作成
  ADR v1.0.0 (Final)
  API仕様 v1.0.0 (Final)
  設計書 v1.0.0 (Final)  ← Phase 2.2で新規作成
  アーキテクチャ図 v1.0.0 (Final)
  データモデル v1.0.0 (Final)
  ↓
Phase 5 (デプロイメント)
  ↓ 承認
  すべて Approved状態へ
```

---

## 📊 成果物進化マトリックス

### マトリックス表

| 成果物 | Phase 2.1 | Phase 3 | Phase 4 | Phase 2.2 | 備考 |
|--------|-----------|---------|---------|-----------|------|
| **ADR** | v0.2.0 (Draft) | v0.3.0 (Interim) | - | v1.0.0 (Final) | 追加決定あれば更新 |
| **API契約書** | v0.1.0 (Draft) | v0.5.0 (Interim) | - | - | Phase 2.1で作成、Phase 3で詳細化 |
| **API仕様書** | - | - | - | v1.0.0 (Final) | API契約の完全版 |
| **制約条件文書** | v1.0.0 (Final) | - | - | - | Phase 2.1で完成 |
| **設計書** | - | - | - | v1.0.0 (Final) | Phase 2.2で新規作成 |
| **アーキテクチャ図** | v0.1.0 (Draft) | - | - | v1.0.0 (Final) | 概要→詳細 |
| **データモデル文書** | v0.1.0 (Draft) | - | - | v1.0.0 (Final) | スキーマ→完全版 |
| **実装コード** | - | v0.x.x | v1.0.0 | - | Phase 3-4で完成 |
| **テストコード** | - | v0.x.x | v1.0.0 | - | Phase 3-4で完成 |

---

## 🎯 AIエージェント向けガイドライン

### 成果物作成時

```python
# 疑似コード
def create_deliverable(phase, deliverable_type):
    # 1. 適切なバージョン番号を決定
    if phase == "Phase 2.1":
        if deliverable_type in ["ADR", "API Contract", "Architecture Diagram"]:
            version = "v0.1.0"  # Draft
            status = "Draft"
    
    elif phase == "Phase 2.2":
        # Phase 2.1で作成したものの完成版
        if deliverable_type in ["ADR", "API Spec", "Architecture Diagram"]:
            version = "v1.0.0"  # Final
            status = "Final"
        # Phase 2.2で新規作成
        elif deliverable_type in ["Design Doc", "Data Model"]:
            version = "v1.0.0"  # Final
            status = "Final"
    
    # 2. メタデータを追加
    metadata = {
        "version": version,
        "status": status,
        "phase": phase,
        "date": today()
    }
    
    # 3. ファイル名にバージョンを含める（versions/内）
    if is_historical_version():
        filename = f"{base_name}-{version}.md"
        directory = "versions/"
    else:
        filename = f"{base_name}.md"
        directory = "."
    
    return create_file(directory + filename, content, metadata)
```

### 成果物更新時

```python
def update_deliverable(deliverable, changes):
    # 1. 現在のバージョンを取得
    current_version = deliverable.get_version()
    
    # 2. 変更の種類を判定
    if changes.is_major():
        new_version = increment_major(current_version)
    elif changes.is_minor():
        new_version = increment_minor(current_version)
    else:
        new_version = increment_patch(current_version)
    
    # 3. 旧バージョンをアーカイブ
    archive_old_version(deliverable, current_version)
    
    # 4. 新バージョンを作成
    updated_deliverable = create_new_version(
        deliverable,
        new_version,
        changes
    )
    
    # 5. changelog更新
    update_changelog(deliverable, current_version, new_version, changes)
    
    return updated_deliverable
```

---

## 📚 ベストプラクティス

### DO（推奨）
- ✅ すべての成果物にメタデータを追加
- ✅ バージョン番号を統一ルールで管理
- ✅ 旧バージョンをversions/に保管
- ✅ changelogを維持
- ✅ Phase 2.1では軽量版、Phase 2.2で完全版

### DON'T（非推奨）
- ❌ バージョン番号なしで上書き
- ❌ 旧バージョンを削除
- ❌ Phase 2.1で完全版を作成
- ❌ changelogを省略
- ❌ メタデータを省略

---

## 🔍 トラブルシューティング

### Q: Phase 2.1と2.2で同じ成果物を作るの?

A: **部分的にYES**

```
同じ成果物（進化するもの）:
  - ADR（v0.2.0 → v1.0.0）
  - API仕様（v0.1.0 契約 → v1.0.0 完全版）
  - アーキテクチャ図（v0.1.0 概要 → v1.0.0 詳細）

Phase 2.2でのみ作成（新規）:
  - 設計書（v1.0.0）
  - データモデル文書完全版（v1.0.0）
```

### Q: バージョン番号はいつインクリメントする?

A: **フェーズ遷移時**

```
Phase 2.1 → Phase 3: Minor更新（v0.1.0 → v0.2.0）
Phase 3 → Phase 4: Minor更新（v0.2.0 → v0.3.0）
Phase 4 → Phase 2.2: Major更新（v0.3.0 → v1.0.0）
```

### Q: 旧バージョンはいつまで保管?

A: **プロジェクト終了まで**

- プロジェクト実行中: すべてのバージョンを保管
- プロジェクト終了後: 最新版と主要マイルストーン版のみ保管

---

**作成日**: 2025-11-19  
**次回レビュー**: 3ヶ月後  
**オーナー**: Engineering Leadership Team
