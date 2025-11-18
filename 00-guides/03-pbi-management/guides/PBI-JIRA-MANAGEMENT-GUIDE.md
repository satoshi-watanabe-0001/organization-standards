# PBI JIRA管理ガイド (PBI JIRA Management Guide)

## 📋 ドキュメント情報

| 項目 | 内容 |
|------|------|
| **ドキュメント名** | PBI JIRA管理ガイド |
| **バージョン** | 1.0.0 |
| **最終更新日** | 2025-11-17 |
| **対象読者** | 自律的AIエージェント、開発チーム、プロジェクトマネージャー、JIRA管理者 |
| **目的** | JIRAでのPBI管理の実践的な方法を提供し、効率的な運用を支援 |

---

## 📖 目次

1. [はじめに](#はじめに)
2. [JIRA設定](#jira設定)
3. [PBIワークフロー](#pbiワークフロー)
4. [カスタムフィールド設定](#カスタムフィールド設定)
5. [Epicとストーリーの管理](#epicとストーリーの管理)
6. [スプリント運用](#スプリント運用)
7. [検索とフィルター](#検索とフィルター)
8. [ダッシュボードとレポート](#ダッシュボードとレポート)
9. [自動化とインテグレーション](#自動化とインテグレーション)
10. [ベストプラクティス](#ベストプラクティス)
11. [トラブルシューティング](#トラブルシューティング)
12. [AIエージェント向けガイダンス](#aiエージェント向けガイダンス)

---

## はじめに

### このガイドの目的

このガイドは、**JIRAでPBIを効率的に管理する**ための実践的な方法を提供します。

#### 対象システム
- **JIRA Software**: Scrum/Kanbanボード
- **JIRA Cloud**: クラウド版
- **JIRA Data Center**: オンプレミス版

### 前提知識

- JIRAの基本操作（課題作成、検索等）
- Scrumの基礎知識
- PBIの基本概念（[PBI-CREATION-BEST-PRACTICES.md](../core/PBI-CREATION-BEST-PRACTICES.md)参照）

---

## JIRA設定

### プロジェクト初期設定

#### 1. プロジェクト作成

```
JIRA → Projects → Create Project
↓
Template: Scrum
↓
Project Name: [プロジェクト名]
Project Key: [略称] (例: ECOM for ECサイト)
```

#### 2. Issue Type設定

**推奨Issue Type構成**:

| Issue Type | 用途 | 階層 |
|-----------|------|------|
| **Epic** | 大規模機能（複数スプリント） | 最上位 |
| **Story** | PBI（1スプリント内） | Epic配下 |
| **Task** | 技術タスク（Storyの子タスク） | Story配下 |
| **Bug** | バグ報告 | 独立またはStory配下 |
| **Spike** | 技術調査 | 独立 |

#### Issue Type Scheme設定

```
Settings → Issues → Issue Types → Issue Type Schemes
↓
プロジェクトに以下を関連付け:
- Epic
- Story
- Task
- Bug
- Spike (Sub-task)
```

---

### カスタムフィールド設定

#### 必須カスタムフィールド

##### 1. PBI Type（必須）

```
フィールド名: PBI Type
フィールドタイプ: Select List (single choice)
説明: PBIの8つのタイプを識別

選択肢:
☐ NPI - 新規プロジェクト立ち上げ
☐ NFD - 新規機能開発
☐ ENH - 既存機能追加・改修
☐ BUG - バグ修正
☐ REF - リファクタリング
☐ ARC - 設計変更・アーキテクチャ改善
☐ HOT - 緊急修正
☐ POC - 実験的機能
```

**設定手順**:
```
Settings → Issues → Custom Fields → Create Custom Field
↓
Field Type: Select List (single choice)
↓
Field Name: PBI Type
↓
Options: [上記8つを追加]
↓
Screens: Default Screen, Create Issue Screen
```

##### 2. Business Value（推奨）

```
フィールド名: Business Value
フィールドタイプ: Text Field (multi-line)
説明: ビジネス価値を定量的に記載

例: "売上10%向上、カスタマーサポート月50件削減"
```

##### 3. Technical Complexity（推奨）

```
フィールド名: Technical Complexity
フィールドタイプ: Select List (single choice)

選択肢:
☐ Low - 既存パターン踏襲
☐ Medium - 一部新技術
☐ High - 未経験技術・複雑なロジック
```

##### 4. Phase Path（推奨・自動計算）

```
フィールド名: Phase Path
フィールドタイプ: Text Field (read-only)
説明: PBIタイプに応じた推奨Phase経路を自動表示

例: "0→2→3→4→5"

自動化ルール:
IF PBI Type = "NFD" THEN Phase Path = "0→2→3→4→5"
IF PBI Type = "BUG" THEN Phase Path = "(0簡易)→3→4"
...
```

##### 5. Acceptance Criteria Checklist（推奨）

```
フィールド名: Acceptance Criteria
フィールドタイプ: Checklist (via Plugin or Text Field)
説明: 受け入れ基準のチェックリスト

手動チェックリスト例:
- [ ] 機能要件1
- [ ] 機能要件2
- [ ] 非機能要件（パフォーマンス）
- [ ] テスト要件
```

---

## PBIワークフロー

### 推奨ワークフロー

```
[To Do] → [In Progress] → [In Review] → [Done]
   ↓           ↓              ↓
Backlog    開発中         レビュー中
```

#### 詳細ステータス

| ステータス | 説明 | 次のアクション |
|----------|------|--------------|
| **Backlog** | バックログに追加済み、未着手 | スプリント計画で選択 |
| **Ready** | スプリント開始準備完了 | スプリント開始 |
| **In Progress** | 開発中 | 実装完了 |
| **In Review** | レビュー待ち・レビュー中 | レビュー承認 |
| **Testing** | QAテスト中 | テスト完了 |
| **Done** | 完了 | - |
| **Blocked** | ブロックされている | ブロッカー解消 |

#### ワークフロー設定

```
Settings → Issues → Workflows → Edit Workflow
↓
Add Transitions:
- Backlog → Ready (条件: 受け入れ基準が記載済み)
- Ready → In Progress (条件: スプリント開始)
- In Progress → In Review (条件: PR作成済み)
- In Review → Testing (条件: コードレビュー承認)
- Testing → Done (条件: テスト合格)
- Any → Blocked (条件: ブロッカー発生)
```

### ワークフロー自動化

#### 自動遷移ルール

**ルール1: PR作成時に "In Review" へ自動遷移**
```
Trigger: GitHub PRが作成された
Condition: Issue Status = "In Progress"
Action: Transition to "In Review"
```

**ルール2: PR承認時に "Testing" へ自動遷移**
```
Trigger: GitHub PRが承認された
Condition: Issue Status = "In Review"
Action: Transition to "Testing"
```

**ルール3: デプロイ成功時に "Done" へ自動遷移**
```
Trigger: CI/CDデプロイ成功
Condition: Issue Status = "Testing"
Action: Transition to "Done"
```

---

## Epicとストーリーの管理

### Epic構造

```
Epic: [大規模機能名]
├─ Epic Link: [親Epic]（あれば）
├─ Story 1: [PBI-1]（優先度: High）
├─ Story 2: [PBI-2]（優先度: Medium）
└─ Story 3: [PBI-3]（優先度: Low）
```

### Epic作成手順

```
Create Issue → Issue Type: Epic
↓
Summary: [Epic名]（例: "ユーザー認証システム"）
↓
Description:
## 概要
[Epicの目的]

## ビジネス価値
[定量的な目標]

## スコープ
### 含まれるもの
- [ ] 機能1
- [ ] 機能2

### 含まれないもの
- [ ] 将来機能A

## タイムライン
- Phase 1: [期間]
- Phase 2: [期間]
↓
Epic Name: [Epic名]
Epic Color: [色選択]
Start Date / Due Date: [期間設定]
```

### StoryとEpicのリンク

```
Story作成時:
Epic Link → [該当Epic選択]
```

---

## スプリント運用

### スプリント計画

#### 1. スプリント作成

```
Backlog → Create Sprint
↓
Sprint Name: Sprint [番号]（例: Sprint 23）
Duration: 2 weeks
Start Date: [開始日]
End Date: [終了日]
Goal: [スプリントゴール]
```

#### 2. PBI選択基準

**選択の優先順位**:
1. **ビジネス価値**: ROIが高いもの
2. **依存関係**: 他のPBIの前提となるもの
3. **リスク**: 技術的不確実性が高いもの（早期検証）
4. **ベロシティ**: チームの過去のベロシティを考慮

**選択プロセス**:
```
1. バックログからPBIをドラッグ&ドロップでスプリントに追加
2. ストーリーポイント合計がベロシティ内か確認
3. 依存関係をチェック
4. スプリントゴール達成に必要なPBIが揃っているか確認
```

#### 3. キャパシティプランニング

```
Team Capacity:
- Developer A: 40h (8h × 5日)
- Developer B: 32h (休暇1日)
- Developer C: 40h

Total: 112h
平均ベロシティ: 30ポイント
今回目標: 28-32ポイント
```

### デイリースクラム管理

**JIRAボードの活用**:
```
Active Sprint Board → Filter by Assignee
↓
各メンバーの進捗を確認:
- In Progress: 現在作業中のPBI
- Blocked: ブロックされているPBI（赤フラグ）
- Done: 完了したPBI
```

### スプリントレビュー

**完了基準**:
```
Definition of Done (DoD):
- [ ] コード実装完了
- [ ] 単体テスト・統合テスト完了（カバレッジ80%以上）
- [ ] コードレビュー承認
- [ ] QAテスト合格
- [ ] ドキュメント更新
- [ ] デプロイ完了（ステージング環境）
```

---

## 検索とフィルター

### よく使うJQLクエリ

#### 1. 自分のアクティブPBI
```jql
assignee = currentUser() 
AND status IN ("In Progress", "In Review") 
AND type = Story 
ORDER BY priority DESC
```

#### 2. 特定タイプのPBI
```jql
"PBI Type" = "NFD" 
AND status != Done 
ORDER BY "Business Value" DESC
```

#### 3. ブロックされているPBI
```jql
status = Blocked 
AND resolution = Unresolved 
ORDER BY created ASC
```

#### 4. 今スプリントの未完了PBI
```jql
sprint in openSprints() 
AND status != Done 
ORDER BY priority DESC, "Story Points" DESC
```

#### 5. Epic配下の全PBI
```jql
"Epic Link" = ECOM-100 
ORDER BY status ASC, priority DESC
```

#### 6. 高優先度かつ見積もり未設定
```jql
priority = High 
AND "Story Points" is EMPTY 
AND type = Story 
ORDER BY created DESC
```

### カスタムフィルター作成

```
Filters → Create Filter
↓
Name: [フィルター名]（例: "My Active Stories"）
JQL: [上記クエリ]
↓
Save → Add to Favorites
```

---

## ダッシュボードとレポート

### 推奨ダッシュボード

#### 1. チームダッシュボード

**含めるガジェット**:
- **Sprint Burndown**: スプリントバーンダウンチャート
- **Sprint Health**: スプリント健全性（残作業・ベロシティ）
- **Assigned to Me**: 自分のタスク一覧
- **Created vs Resolved**: 作成 vs 解決チャート

#### 2. プロダクトオーナーダッシュボード

**含めるガジェット**:
- **Epic Progress**: Epic進捗状況
- **Epic Burndown**: Epicバーンダウン
- **Backlog Health**: バックログ健全性
- **Release Burndown**: リリースバーンダウン

#### 3. 管理者ダッシュボード

**含めるガジェット**:
- **Velocity Chart**: ベロシティチャート（過去5スプリント）
- **Control Chart**: サイクルタイム分析
- **Cumulative Flow**: 累積フロー図
- **PBI Type Distribution**: PBIタイプ別分布

### レポート生成

#### バーンダウンチャート
```
Reports → Burndown Chart
↓
Sprint: [対象スプリント選択]
↓
Export: PNG / PDF
```

#### ベロシティレポート
```
Reports → Velocity Chart
↓
Number of Sprints: 5-10
↓
Analyze: 平均ベロシティ、トレンド
```

---

## 自動化とインテグレーション

### JIRA Automation Rules

#### ルール1: PBI Type設定忘れ防止

```
Trigger: Issue Created
Condition: Issue Type = Story AND "PBI Type" is EMPTY
Action: 
  - Add Comment: "@creator PBI Typeを設定してください"
  - Set Priority: Low
```

#### ルール2: 受け入れ基準未記載の警告

```
Trigger: Issue Transitioned to "Ready"
Condition: Description does NOT contain "受け入れ基準"
Action:
  - Transition to "Backlog"
  - Add Comment: "受け入れ基準を記載してください"
```

#### ルール3: 長期未完了PBIの通知

```
Trigger: Scheduled (Daily 9:00 AM)
Condition: 
  - Status = "In Progress"
  - Updated < -7d (7日間更新なし)
Action:
  - Send Email: Assignee
  - Add Comment: "7日間更新がありません。状況を確認してください"
```

### GitHub連携

#### PR作成時のJIRA更新

**GitHub Actions設定**:
```yaml
name: JIRA Integration

on:
  pull_request:
    types: [opened, closed]

jobs:
  update-jira:
    runs-on: ubuntu-latest
    steps:
      - name: Extract JIRA Issue Key
        id: jira_key
        run: |
          echo "::set-output name=key::$(echo '${{ github.event.pull_request.title }}' | grep -oE '[A-Z]+-[0-9]+')"
      
      - name: Transition to In Review
        if: github.event.action == 'opened'
        uses: atlassian/gajira-transition@master
        with:
          issue: ${{ steps.jira_key.outputs.key }}
          transition: "In Review"
      
      - name: Transition to Done
        if: github.event.action == 'closed' && github.event.pull_request.merged == true
        uses: atlassian/gajira-transition@master
        with:
          issue: ${{ steps.jira_key.outputs.key }}
          transition: "Done"
```

### Slack連携

**通知設定**:
```
JIRA → Settings → System → Webhooks → Create Webhook
↓
URL: [Slack Incoming Webhook URL]
Events:
  - Issue Created
  - Issue Updated (Status変更時)
  - Issue Commented
↓
Filter: project = ECOM AND type = Story
```

---

## ベストプラクティス

### PBI作成のベストプラクティス

#### 1. 命名規則

```
✅ Good:
- "ユーザー登録機能 - メール認証"
- "商品検索パフォーマンス改善"
- "[BUG] ログイン時にセッションが切れる"

❌ Bad:
- "実装" ← 何の実装か不明
- "バグ修正" ← どのバグか不明
- "ECOM-123" ← Issue keyのみ
```

#### 2. Description記載

**推奨フォーマット**:
```markdown
## ユーザーストーリー（NFD/ENHの場合）
として: [ユーザータイプ]
〜したい: [やりたいこと]
なぜなら: [理由]

## ビジネス価値
- 指標1: [数値目標]
- 指標2: [数値目標]

## 受け入れ基準
- [ ] 基準1
- [ ] 基準2

## 技術的考慮事項
- 使用技術: [技術スタック]
- API: [エンドポイント]

## 見積もり根拠
[見積もりの理由]
```

#### 3. ラベル活用

**推奨ラベル**:
- `frontend`: フロントエンド作業
- `backend`: バックエンド作業
- `database`: データベース変更あり
- `security`: セキュリティ関連
- `performance`: パフォーマンス改善
- `tech-debt`: 技術的負債
- `blocked`: ブロック中
- `urgent`: 緊急対応

---

### バックログ管理

#### バックログリファインメント（週1回推奨）

```
アジェンダ:
1. 新規PBIのレビュー
   - 受け入れ基準の明確化
   - 見積もり（プランニングポーカー）

2. 既存PBIの更新
   - 優先順位の見直し
   - 大きすぎるPBIの分割

3. Epicの進捗確認
   - 完了状況
   - 次スプリントの計画
```

#### バックログの健全性指標

| 指標 | 目標値 | 測定方法 |
|------|--------|---------|
| **Ready PBIの割合** | 30%以上 | Ready状態のPBI数 / 全PBI数 |
| **見積もり済みPBI** | 80%以上 | Story Points設定済みPBI / 全PBI |
| **古いPBI** | 10%以下 | 3ヶ月以上更新なしPBI / 全PBI |

---

## トラブルシューティング

### よくある問題と解決策

#### 問題1: PBIが大きすぎて進まない

**症状**: 1つのPBIが2週間以上進捗しない

**解決策**:
1. [PBI-SPLITTING-GUIDE.md](../guides/PBI-SPLITTING-GUIDE.md)を参照して分割
2. Epic化して複数のStoryに分解
3. 毎日の進捗確認を強化

#### 問題2: 依存関係でブロックが頻発

**症状**: 多くのPBIが "Blocked" 状態

**解決策**:
1. JIRA Issue Linksで依存関係を明示
2. バックログリファインメントで依存関係を事前に解消
3. 独立性の高いPBIを優先的に選択

#### 問題3: 見積もりと実績が大きく乖離

**症状**: 見積もり5ポイントが実際は10日かかる

**解決策**:
1. プランニングポーカーでチーム全体で見積もり
2. 過去の実績データを参照（Velocity Chart）
3. 不確実性が高い場合はSpike/POCを先に実施

---

## AIエージェント向けガイダンス

### JIRA API活用

AIエージェントは、JIRA REST APIを使用してPBIを自動管理できます。

#### PBI自動作成

```python
import requests

def create_pbi_in_jira(pbi_data):
    url = "https://your-domain.atlassian.net/rest/api/3/issue"
    headers = {
        "Authorization": f"Bearer {api_token}",
        "Content-Type": "application/json"
    }
    
    payload = {
        "fields": {
            "project": {"key": "ECOM"},
            "issuetype": {"name": "Story"},
            "summary": pbi_data["title"],
            "description": {
                "type": "doc",
                "version": 1,
                "content": [
                    {
                        "type": "paragraph",
                        "content": [
                            {"type": "text", "text": pbi_data["description"]}
                        ]
                    }
                ]
            },
            "customfield_10001": pbi_data["pbi_type"],  # PBI Type
            "customfield_10002": pbi_data["business_value"],  # Business Value
            "priority": {"name": pbi_data["priority"]},
            "labels": pbi_data["labels"]
        }
    }
    
    response = requests.post(url, json=payload, headers=headers)
    return response.json()
```

#### PBI更新

```python
def update_pbi_status(issue_key, new_status):
    url = f"https://your-domain.atlassian.net/rest/api/3/issue/{issue_key}/transitions"
    headers = {
        "Authorization": f"Bearer {api_token}",
        "Content-Type": "application/json"
    }
    
    # Get available transitions
    response = requests.get(url, headers=headers)
    transitions = response.json()["transitions"]
    
    # Find target transition
    transition_id = next(
        t["id"] for t in transitions if t["name"] == new_status
    )
    
    # Execute transition
    payload = {"transition": {"id": transition_id}}
    requests.post(url, json=payload, headers=headers)
```

---

## 関連ドキュメント

### 必読ドキュメント

| ドキュメント名 | 目的 | リンク |
|-------------|------|-------|
| **PBI-CREATION-BEST-PRACTICES.md** | PBI作成ガイド | [Link](../core/PBI-CREATION-BEST-PRACTICES.md) |
| **PBI-TEMPLATE-CATALOG.md** | テンプレート集 | [Link](../templates-prompts/PBI-TEMPLATE-CATALOG.md) |
| **PBI-SPLITTING-GUIDE.md** | PBI分割方法 | [Link](../guides/PBI-SPLITTING-GUIDE.md) |

---

## まとめ

### JIRA運用の成功要因

1. ✅ **標準化**: PBI Typeカスタムフィールドで分類
2. ✅ **自動化**: GitHub連携、自動通知
3. ✅ **可視化**: ダッシュボード・レポート活用
4. ✅ **継続改善**: レトロスペクティブで運用改善

### 次のステップ

1. **JIRA設定**: カスタムフィールド・ワークフロー設定
2. **チーム教育**: 本ガイドの内容を共有
3. **自動化**: GitHub/Slack連携の設定
4. **ダッシュボード**: チーム・PO用ダッシュボード作成

---

**Document Version**: 1.0.0  
**Last Updated**: 2025-11-17  
**Maintained By**: Engineering Leadership Team
