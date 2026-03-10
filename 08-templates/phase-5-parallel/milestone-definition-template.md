---
template_name: "Milestone Definition Template"
version: "1.0.0"
purpose: "Phase 5並行実施のトリガーとなるマイルストーンを定義"
target_phase: "Phase 3 (実装) → Phase 5 (詳細設計) 並行作業"
category: "development-process"
related_templates:
  - "phase-5-work-block-checklist.md"
  - "api-contract-template.md"
  - "as-built-notes-template.md"
---

# マイルストーン定義テンプレート / Milestone Definition Template

## 📋 テンプレートの使い方

このテンプレートは、**Phase 3実装中にPhase 5を並行実施する際のトリガー**となるマイルストーンを定義します。

### 使用タイミング
- Phase 1（プロジェクト初期化）完了時
- Phase 2A（事前設計）完了時
- Phase 3開始前

### 記入ルール
- 1マイルストーン = 1つの独立した機能単位
- 期間: 3-7日（1週間以内推奨）
- プロジェクト全体で3-8個のマイルストーンに分割

---

## 🎯 マイルストーン基本情報

### Milestone ID
**MS-[番号]**: MS-001

### マイルストーン名
**日本語名**: コア機能完成
**英語名**: Core Features Completion

### 担当者
- **実装担当**: [開発者名]
- **レビュー担当**: [テックリード名]

### 期間
- **開始日**: YYYY-MM-DD
- **終了日**: YYYY-MM-DD
- **期間**: [X]日

---

## 🔧 実装内容

### 機能概要

**このマイルストーンで実装する機能の概要を記述**

例:
```
ユーザー認証マイクロサービスのコア機能を実装。
- ユーザー登録API
- ログインAPI
- JWTトークン発行機能
- ユーザーデータモデル（PostgreSQL）
```

---

### 実装対象

#### APIエンドポイント

| エンドポイント | メソッド | 概要 | 優先度 |
|-------------|---------|------|--------|
| `/api/v1/auth/register` | POST | ユーザー登録 | 必須 |
| `/api/v1/auth/login` | POST | ログイン | 必須 |
| `/api/v1/auth/logout` | POST | ログアウト | 必須 |
| `/api/v1/users/me` | GET | ユーザー情報取得 | 推奨 |

#### データモデル

| テーブル/モデル | 概要 | 主要フィールド |
|---------------|------|--------------|
| `users` | ユーザー情報 | id, email, password_hash, created_at |
| `user_sessions` | セッション管理 | id, user_id, token, expires_at |

#### ビジネスロジック

- [ ] ユーザー登録バリデーション（メール形式、パスワード強度）
- [ ] パスワードハッシュ化（bcrypt）
- [ ] JWTトークン生成・検証
- [ ] セッション管理

#### テスト

- [ ] ユニットテスト（カバレッジ80%以上）
- [ ] 統合テスト（APIエンドポイント）
- [ ] セキュリティテスト（SQL injection, XSS）

---

## ✅ 完了基準（Completion Criteria）

### 実装完了の定義

```yaml
implementation:
  - [ ] 全APIエンドポイントが実装され、動作確認済み
  - [ ] すべてのユニットテストがパス（カバレッジ80%以上）
  - [ ] 統合テスト（主要シナリオ）が完了
  - [ ] エラーハンドリングが適切に実装されている
  - [ ] ログ出力が実装されている

code_quality:
  - [ ] コードレビューが完了している
  - [ ] Linter/Formatter（ESLint, Prettier等）がクリア
  - [ ] 静的解析ツール（SonarQube等）がクリア
  - [ ] セキュリティスキャン（Snyk, OWASP等）がクリア

documentation_readiness:
  - [ ] コードコメントが適切に記述されている
  - [ ] 主要な関数・クラスにJSDoc/docstring等がある
  - [ ] READMEに実装内容が簡潔に記載されている
```

---

## 🚀 Phase 5トリガー条件

### トリガータイミング

**条件**: 上記完了基準をすべて満たした時

**推奨タイミング**:
- ✅ **Option A (推奨)**: マイルストーン完了の金曜日午後
- **Option B**: 翌週月曜日午前
- **Option C**: Phase 4（レビュー・QA）期間中

**選択理由**: 
```
金曜午後を推奨する理由:
- 実装の記憶が最も鮮明
- 週末前に区切りがつく
- 週明けは次のマイルストーン実装に集中できる
```

---

### Phase 5作業ブロック情報

**ブロック番号**: Phase 5 Block #[X]

**予定時間**: [4-6]時間

**実施日時**: YYYY-MM-DD 13:00 - 17:00

**実施場所**: [会議室予約 or リモート]

---

## 📝 Phase 5成果物（このマイルストーンで作成）

### 必須成果物

| 成果物 | テンプレート | 予定時間 | 担当者 |
|--------|------------|---------|--------|
| API仕様書（該当エンドポイント） | `api-specification-template.md` | 2時間 | [名前] |
| シーケンス図（主要フロー） | Mermaid/PlantUML | 1時間 | [名前] |
| データモデル図（ER図） | dbdiagram.io | 1時間 | [名前] |

### 推奨成果物

| 成果物 | テンプレート | 予定時間 | 担当者 |
|--------|------------|---------|--------|
| ADR（重要な技術決定） | `adr-template.md` | 30分 | [名前] |
| セキュリティ設計メモ | - | 30分 | [名前] |

### 成果物の完成度目標

```yaml
api_specification:
  target_completion: "30%"
  content: "このマイルストーンで実装したエンドポイントのみ"
  
sequence_diagram:
  target_completion: "40%"
  content: "コア機能のフロー（登録・ログイン）"
  
data_model:
  target_completion: "50%"
  content: "users, user_sessionsテーブル"
  
adr:
  target_completion: "N/A（追加のみ）"
  content: "JWT選定理由、bcrypt選定理由"
```

---

## 📊 進捗トラッキング

### Phase 5全体進捗

```yaml
overall_progress:
  after_this_milestone: "25%"
  cumulative_hours: "4時間"
  remaining_milestones: "3"
  estimated_total_hours: "16時間"
```

### 成果物別進捗

| 成果物 | 現在の完成度 | このマイルストーン後 |
|--------|------------|---------------------|
| API仕様書 | 0% | 30% |
| アーキテクチャ図 | 0% | 20% |
| データモデル文書 | 0% | 50% |
| 設計書 | 0% | 15% |

---

## ⚠️ リスクと対応

### 予想されるリスク

| リスク | 影響度 | 対応策 |
|--------|--------|--------|
| 実装が予定より遅れる | 高 | Phase 5ブロックを1日遅らせる |
| Phase 5作業時間が不足 | 中 | 80%ルール適用、次ブロックで完成 |
| ドキュメント作成中に実装の誤りを発見 | 低 | 実装修正を優先、ドキュメントは後で更新 |

---

## 🔗 関連リンク

### 関連Issue/タスク

- **実装Issue**: #[番号] - [Issue URL]
- **Phase 5作業ブロックタスク**: #[番号] - [Task URL]

### 参照ドキュメント

- Phase 2A成果物: `[ファイル名].md`
- API契約書: `[ファイル名].md`
- 制約条件文書: `[ファイル名].md`

---

## 📅 スケジュール詳細

### 日次計画（例: 5日間マイルストーン）

```
Day 1 (月曜):
  - プロジェクト構造セットアップ
  - データモデル実装
  - ユニットテスト基盤構築

Day 2 (火曜):
  - ユーザー登録API実装
  - パスワードハッシュ化実装
  - ユニットテスト作成

Day 3 (水曜):
  - ログインAPI実装
  - JWTトークン生成実装
  - 統合テスト作成

Day 4 (木曜):
  - ログアウトAPI実装
  - エラーハンドリング強化
  - コードレビュー実施

Day 5 (金曜):
  午前: 最終調整、テスト完了
  午後: ✅ Phase 5 作業ブロック #1 実施
```

---

## ✍️ 記入例

### 実際のプロジェクト例

```yaml
milestone_id: "MS-001"
name_ja: "ユーザー認証コア機能完成"
name_en: "User Authentication Core Features"

duration:
  start: "2025-11-25"
  end: "2025-11-29"
  days: 5

implementation:
  apis:
    - POST /api/v1/auth/register
    - POST /api/v1/auth/login
    - POST /api/v1/auth/logout
  
  data_models:
    - users (id, email, password_hash, created_at)
    - user_sessions (id, user_id, token, expires_at)
  
  tests:
    - unit_tests: 85% coverage
    - integration_tests: 主要シナリオ3件

completion_criteria:
  all_checked: true
  review_completed: true
  tests_passing: true

phase_5_trigger:
  date: "2025-11-29"
  time: "13:00-17:00"
  duration: "4時間"
  block_number: 1

phase_5_deliverables:
  - API仕様書（登録・ログイン・ログアウト）: 2時間
  - シーケンス図（認証フロー）: 1時間
  - ER図（users, user_sessions）: 1時間

progress:
  overall: "25%"
  api_spec: "30%"
  architecture_diagram: "20%"
  data_model: "50%"
```

---

## 📝 記入チェックリスト

作成前に以下を確認:

```yaml
before_start:
  - [ ] プロジェクト全体を3-8個のマイルストーンに分割している
  - [ ] 各マイルストーンが独立した機能単位である
  - [ ] 期間が3-7日以内に収まっている
  - [ ] 完了基準が明確に定義されている
  - [ ] Phase 5成果物が具体的に列挙されている
  - [ ] Phase 5作業時間が現実的（2-6時間）

during_execution:
  - [ ] 実装進捗を毎日更新している
  - [ ] 完了基準の達成状況をチェックしている
  - [ ] リスクが顕在化した場合、対応策を記録している

after_completion:
  - [ ] Phase 5作業ブロックを実施した
  - [ ] 成果物を指定場所に保存した
  - [ ] 進捗トラッキングを更新した
  - [ ] 次のマイルストーンに引き継ぐ事項を記録した
```

---

## 🔄 次のマイルストーンへの引き継ぎ

### 完了事項

- ✅ [完了した項目を列挙]

### 未完了事項・技術的負債

- ⚠️ [次のマイルストーンに持ち越す項目]

### 学んだこと・改善点

- 💡 [実装中に発見した改善点、注意事項]

---

**このテンプレートを使用して、Phase 5並行実施を効率的に進めましょう。**
