# ドキュメントヘッダー優先度テンプレート

## 目的

各組織標準ドキュメントに優先度情報を追加し、AI エージェントが矛盾解決時に適切な判断ができるようにする。

---

## ヘッダーテンプレート

### セキュリティ・コンプライアンス関連ドキュメント

```yaml
---
title: "ドキュメントタイトル"
version: "1.0.0"
last_updated: "2025-11-20"
status: "active"
category: "security-compliance"
priority_level: "required"
conflict_resolution: "organization > project > team"
applicable_domain: "security-compliance"
override_difficulty: "extremely-difficult"
approval_required: "CTO + Security Team"
description: "このドキュメントはセキュリティ統制必須領域に属します。例外承認は極めて困難です。"
---
```

**適用ドキュメント**:
- `/07-security-compliance/` 配下の全ドキュメント

---

### API設計・アーキテクチャ関連ドキュメント

```yaml
---
title: "ドキュメントタイトル"
version: "1.0.0"
last_updated: "2025-11-20"
status: "active"
category: "architecture-standards"
priority_level: "recommended"
conflict_resolution: "project > organization ≈ team"
applicable_domain: "architecture-design"
override_difficulty: "easy"
approval_required: "Engineering Manager + Architect"
description: "このドキュメントは推奨ガイドラインです。プロジェクト要件に応じて選択可能です。"
---
```

**適用ドキュメント**:
- `/02-architecture-standards/` 配下の全ドキュメント

---

### コーディング標準ドキュメント（言語固有）

```yaml
---
title: "ドキュメントタイトル"
version: "1.0.0"
last_updated: "2025-11-20"
status: "active"
category: "coding-standards"
priority_level: "recommended"
conflict_resolution: "team > project > organization"
applicable_domain: "coding-style"
override_difficulty: "very-easy"
approval_required: "Engineering Manager (report only)"
description: "このドキュメントは参考情報です。チームの生産性を優先してください。"
related_priority_documents:
  - path: "/07-security-compliance/"
    note: "セキュリティ関連項目は /07-security-compliance/ を最優先"
---
```

**適用ドキュメント**:
- `/01-coding-standards/typescript/` 配下の全ドキュメント
- `/01-coding-standards/python/` 配下の全ドキュメント
- 他の言語標準

---

### 品質標準ドキュメント

```yaml
---
title: "ドキュメントタイトル"
version: "1.0.0"
last_updated: "2025-11-20"
status: "active"
category: "quality-standards"
priority_level: "recommended"
conflict_resolution: "project > organization ≈ team"
applicable_domain: "quality-assurance"
override_difficulty: "easy"
approval_required: "Engineering Manager"
description: "このドキュメントは推奨値です。プロジェクト特性に応じて調整可能です。"
---
```

**適用ドキュメント**:
- `/04-quality-standards/` 配下の全ドキュメント

---

### 技術スタック関連ドキュメント

```yaml
---
title: "ドキュメントタイトル"
version: "1.0.0"
last_updated: "2025-11-20"
status: "active"
category: "technology-stack"
priority_level: "required"
conflict_resolution: "organization > project > team"
applicable_domain: "technology-selection"
override_difficulty: "moderate"
approval_required: "Architecture Review Committee"
description: "このドキュメントは承認済み技術リストです。リスト外の技術使用には承認が必要です。"
note: "承認済みリスト内での選択は柔軟（project > organization）"
---
```

**適用ドキュメント**:
- `/05-technology-stack/` 配下の全ドキュメント

---

## フィールド説明

### priority_level
- **required**: 必須基準（例外が極めて困難）
- **recommended**: 推奨ガイドライン（例外が容易）
- **reference**: 参考情報（例外承認不要）

### conflict_resolution
矛盾発生時の優先順位:
- `organization > project > team`: 組織標準が最優先
- `project > organization ≈ team`: プロジェクト標準が最優先
- `team > project > organization`: チーム標準が最優先

### applicable_domain
適用領域の明示:
- `security-compliance`: セキュリティ・コンプライアンス
- `architecture-design`: アーキテクチャ設計
- `coding-style`: コーディングスタイル
- `quality-assurance`: 品質保証
- `technology-selection`: 技術選定

### override_difficulty
例外承認の難易度:
- `extremely-difficult`: 極めて困難
- `difficult`: 困難
- `moderate`: 中程度
- `easy`: 容易
- `very-easy`: 非常に容易

### approval_required
例外承認に必要な承認者

---

## 実装例

### Before（優先度情報なし）

```yaml
---
title: "REST API Design Standards"
version: "2.0.0"
last_updated: "2024-10-29"
status: "active"
---
```

### After（優先度情報追加）

```yaml
---
title: "REST API Design Standards"
version: "2.0.0"
last_updated: "2025-11-20"
status: "active"
category: "architecture-standards"
priority_level: "recommended"
conflict_resolution: "project > organization ≈ team"
applicable_domain: "architecture-design"
override_difficulty: "easy"
approval_required: "Engineering Manager + Architect"
description: "このドキュメントは推奨ガイドラインです。プロジェクト要件に応じて選択可能です。"
---
```

---

## ロールアウト計画

### Phase 1: 高優先度ドキュメント（1週間）
1. `/07-security-compliance/` - すべてのセキュリティ関連
2. `/01-coding-standards/typescript/AI-QUICK-REFERENCE.md`
3. `/01-coding-standards/python/AI-QUICK-REFERENCE.md`

### Phase 2: 中優先度ドキュメント（2週間）
4. `/02-architecture-standards/` - すべてのアーキテクチャ標準
5. `/05-technology-stack/` - すべての技術スタック
6. `/04-quality-standards/` - すべての品質標準

### Phase 3: 低優先度ドキュメント（1週間）
7. `/01-coding-standards/` - 残りのコーディング標準
8. `/03-development-process/` - 開発プロセス
9. その他のドキュメント

---

**作成日**: 2025-11-20  
**作成者**: 自律型AIエージェント  
**承認**: Engineering Leadership Team
