---
title: "標準の階層と優先度 / Standards Hierarchy and Precedence"
version: "1.0.0"
created_date: "2025-11-12"
last_updated: "2025-11-12"
status: "Active"
owner: "Engineering Leadership Team"
category: "governance"
---

# 標準の階層と優先度

> 組織標準・プロジェクト標準・チーム標準の優先度と適用ルール

**適用範囲**: 全開発プロジェクト・全チーム  
**優先度設定**: ハイブリッドアプローチ（領域別）

---

## 📋 目次

1. [概要](#概要)
2. [3層の標準階層](#3層の標準階層)
3. [ハイブリッドアプローチ](#ハイブリッドアプローチ)
4. [領域別優先度設定](#領域別優先度設定)
5. [矛盾解決の実践ガイド](#矛盾解決の実践ガイド)
6. [例外承認プロセス](#例外承認プロセス)
7. [実践例](#実践例)

---

## 概要

### 目的

本ドキュメントは、組織内に存在する3層の標準（組織・プロジェクト・チーム）の優先度を明確化し、矛盾がある場合の判断基準を提供します。

### 基本方針

**ハイブリッドアプローチ**: 領域によって異なる優先度を設定します。

```
統制必須領域: 組織 > プロジェクト > チーム
バランス領域: プロジェクト > 組織 ≈ チーム
柔軟性領域: チーム > プロジェクト > 組織
```

### 重要な原則

```yaml
principles:
  context_matters:
    description: "領域によって優先度が異なる"
    guideline: "一律の優先度は設定しない"
  
  security_first:
    description: "セキュリティ・コンプライアンスは組織標準が絶対優先"
    guideline: "例外は極めて困難"
  
  team_autonomy:
    description: "チームの自律性を尊重"
    guideline: "柔軟性領域ではチームの判断を優先"
  
  documented_exceptions:
    description: "例外は必ず文書化"
    guideline: "承認プロセスを経た例外のみ有効"
```

---

## 3層の標準階層

### 標準の定義

```yaml
標準の3層構造:
  layer1_organization:
    name: "組織標準（Organization Standards）"
    scope: "組織全体（全プロジェクト・全チーム）"
    location: "devin-organization-standards リポジトリ"
    examples:
      - セキュリティ標準
      - コーディング標準（基本）
      - アーキテクチャパターン（推奨）
      - 承認済み技術スタック
    approval: "Architecture Review Committee"
    update_frequency: "四半期レビュー"
  
  layer2_project:
    name: "プロジェクト標準（Project Standards）"
    scope: "特定プロジェクト内のみ"
    location: "プロジェクトリポジトリ内（docs/standards/）"
    examples:
      - プロジェクト固有のAPI設計
      - データモデル設計
      - 技術スタック選択（組織承認済みリスト内）
      - プロジェクト固有の品質基準
    approval: "Engineering Manager + Architect"
    update_frequency: "随時（プロジェクトニーズに応じて）"
  
  layer3_team:
    name: "チーム標準（Team Standards）"
    scope: "特定チーム内のみ"
    location: "チームリポジトリ内（TEAM_STANDARDS.md）"
    examples:
      - チーム固有のコーディング規約
      - 開発ツール・IDE設定
      - ワークフロー・プロセス
      - ブランチ命名規則
    approval: "Engineering Manager"
    update_frequency: "随時（チームの合意により）"
```

### 各層の役割

```
┌─────────────────────────────────────────────────────────┐
│  組織標準（Organization Standards）                       │
│  役割: 組織全体の統一性・安全性・品質の担保                 │
│  - 最低限守るべき基準                                      │
│  - セキュリティ・コンプライアンスの確保                     │
│  - ベストプラクティスの共有                                │
├─────────────────────────────────────────────────────────┤
│  プロジェクト標準（Project Standards）                     │
│  役割: プロジェクト特性への最適化                           │
│  - プロジェクト固有の要件対応                              │
│  - 組織標準の具体化・拡張                                  │
│  - プロジェクトチーム間の一貫性                            │
├─────────────────────────────────────────────────────────┤
│  チーム標準（Team Standards）                             │
│  役割: チームの生産性最大化・自律性の確保                   │
│  - チーム固有の作業スタイル                                │
│  - 個人の生産性向上                                        │
│  - チーム文化の反映                                        │
└─────────────────────────────────────────────────────────┘
```

---

## ハイブリッドアプローチ

### なぜハイブリッド型か

**単一優先度の問題点**:

```yaml
top_down_only:
  問題:
    - チームの自律性が制限される
    - イノベーションが抑制される
    - 現場の実情が反映されにくい
  
bottom_up_only:
  問題:
    - セキュリティリスクが増大
    - 組織全体の一貫性が失われる
    - コンプライアンス対応が困難
```

**ハイブリッド型の利点**:

```yaml
advantages:
  governance_and_innovation:
    description: "ガバナンスとイノベーションの両立"
    detail: "重要な領域は統制、柔軟な領域は自律性"
  
  risk_based_approach:
    description: "リスクベースの優先度設定"
    detail: "リスクの高い領域は組織標準を優先"
  
  team_productivity:
    description: "チーム生産性の最大化"
    detail: "ツールやスタイルはチームの選択を尊重"
  
  organizational_consistency:
    description: "組織の一貫性維持"
    detail: "重要な領域で統一性を確保"
```

---

## 領域別優先度設定

### 🔒 統制必須領域（組織標準が最優先）

**優先度**: `組織 > プロジェクト > チーム`

```yaml
category: 統制必須領域
priority_rule: "組織標準が絶対優先"
exception_difficulty: "極めて困難"

covered_areas:
  security:
    - セキュリティ標準全般
    - 認証・認可方式
    - データ保護・暗号化
    - 脆弱性管理
    - セキュリティテスト
    documents:
      - 07-security-compliance/*
    reason: "法的責任、データ漏洩リスク、組織全体の信頼性"
  
  compliance:
    - コンプライアンス要件
    - データプライバシー（GDPR、個人情報保護法）
    - ライセンス管理
    - 監査要件
    documents:
      - 07-security-compliance/compliance-requirements.md
      - 07-security-compliance/data-protection.md
    reason: "法的リスク、規制当局への対応"
  
  critical_infrastructure:
    - 認証基盤
    - データベース暗号化
    - ネットワークセキュリティ
    - バックアップ・DR戦略
    documents:
      - 02-architecture-standards/cloud-architecture.md
      - 05-technology-stack/infrastructure-stack.md
    reason: "組織全体のシステム安定性、災害対策"
  
  organizational_processes:
    - インシデント対応プロセス
    - 変更管理プロセス
    - リリース承認プロセス
    - 監査プロセス
    documents:
      - 03-development-process/incident-management.md
      - 03-development-process/change-management.md
    reason: "組織全体の運用効率、責任の明確化"

approval_for_exceptions:
  required_approvers:
    - CTO
    - Legal Department
    - Security Team
  process: "10-governance/exception-approval-process.md"
  risk_level: "Critical Risk"
  review_frequency: "月次 + 即時監査"
```

**判断基準**:
```
Q: この標準は組織全体のリスクに影響するか？
Yes → 統制必須領域

Q: 法的・規制要件に関連するか？
Yes → 統制必須領域

Q: セキュリティインシデントの可能性があるか？
Yes → 統制必須領域
```

---

### ⚖️ バランス領域（プロジェクト標準が最優先）

**優先度**: `プロジェクト > 組織 ≈ チーム`

```yaml
category: バランス領域
priority_rule: "プロジェクト標準を基本とし、組織標準をガイドラインとする"
exception_difficulty: "容易（合理的な理由があれば承認）"

covered_areas:
  api_design:
    - RESTful vs GraphQL
    - APIバージョニング戦略
    - エラーハンドリング形式
    - API認証方式（セキュリティ基準内）
    documents:
      - 02-architecture-standards/api-design-standards.md（ガイドライン）
    reason: "プロジェクト特性、クライアント要件、技術的制約"
  
  architecture_patterns:
    - マイクロサービス vs モノリス
    - イベント駆動 vs リクエスト駆動
    - キャッシュ戦略
    - データベース設計
    documents:
      - 02-architecture-standards/microservices-architecture.md（ガイドライン）
      - 02-architecture-standards/event-driven-architecture.md（ガイドライン）
      - 02-architecture-standards/caching-strategy.md（ガイドライン）
    reason: "スケール要件、パフォーマンス特性、ビジネス要件"
  
  technology_stack:
    - プログラミング言語（組織承認済みリスト内）
    - フレームワーク選択
    - データベース選択
    - メッセージング技術
    documents:
      - 05-technology-stack/approved-technologies.md（承認済みリスト）
      - 10-governance/technology-radar.md（評価・推奨）
    reason: "プロジェクト要件、チームスキル、技術的制約"
  
  quality_standards:
    - テストカバレッジ目標
    - パフォーマンス基準
    - コード複雑度制限
    - E2Eテスト戦略
    documents:
      - 04-quality-standards/testing-strategy.md（基準値を提供）
    reason: "プロジェクトのクリティカリティ、ビジネス要件"

approval_for_exceptions:
  required_approvers:
    - Engineering Manager
    - Architect
  process: "簡易承認（技術選定書に記載）"
  risk_level: "Medium Risk"
  review_frequency: "四半期"
  
decision_guideline:
  組織標準の役割: "ベストプラクティスの提供、推奨事項"
  プロジェクト標準の役割: "プロジェクト特性に応じた最適化"
  チーム標準の役割: "プロジェクト標準の補完（非矛盾の範囲）"
```

**判断基準**:
```
Q: プロジェクト固有の要件があるか？
Yes → プロジェクト標準を優先

Q: 組織標準は「推奨」「ガイドライン」か？
Yes → プロジェクト標準で上書き可能

Q: 選択肢が組織の承認済みリスト内か？
Yes → プロジェクトの判断で選択可能
```

---

### 🎨 柔軟性重視領域（チーム標準が最優先）

**優先度**: `チーム > プロジェクト > 組織`

```yaml
category: 柔軟性重視領域
priority_rule: "チーム標準を最優先、組織標準は参考程度"
exception_difficulty: "非常に容易（承認不要の場合も）"

covered_areas:
  coding_style:
    - インデント（スペース vs タブ、2 vs 4）
    - 命名規則の詳細（camelCase vs snake_case）
    - コメントスタイル
    - ファイル構成・命名
    documents:
      - 01-coding-standards/code-formatting.md（参考）
      - 01-coding-standards/naming-conventions.md（参考）
    reason: "チームの生産性、可読性の個人差、既存コードベースとの一貫性"
  
  development_tools:
    - IDE・エディタの選択（VSCode, IntelliJ, Vim等）
    - ローカル開発環境
    - デバッグツール
    - Git GUIツール
    documents:
      - 06-tools-and-environment/development-environment.md（推奨）
      - 06-tools-and-environment/ide-configuration.md（推奨）
    reason: "個人の生産性、習熟度の違い、個人の好み"
  
  testing_implementation:
    - テストフレームワークの選択（組織承認済みリスト内）
    - モックライブラリの選択
    - テストファイル命名規則
    - テストデータ管理方法
    documents:
      - 04-quality-standards/unit-testing.md（ガイドライン）
    reason: "チームの習熟度、プロジェクト特性"
  
  workflow_customization:
    - ブランチ命名規則の詳細
    - コミットメッセージ形式の詳細
    - PRレビュープロセスの詳細
    - タスク管理方法
    documents:
      - 03-development-process/git-workflow.md（基本方針のみ）
      - 03-development-process/branching-strategy.md（基本方針のみ）
    reason: "チームサイズ、作業スタイル、コミュニケーション方法"
  
  documentation_style:
    - ドキュメント形式（Markdown, Notion, Confluence等）
    - ドキュメント構造
    - 図表ツール
    documents:
      - 03-development-process/documentation-guidelines.md（参考）
    reason: "チームの好み、既存ツール、ワークフロー"

approval_for_exceptions:
  required_approvers:
    - Engineering Manager（形式的承認）
  process: "チーム合意で決定、マネージャーに報告"
  risk_level: "Low Risk"
  review_frequency: "年次（または不要）"
  
decision_guideline:
  組織標準の役割: "参考情報、推奨事項の提供"
  プロジェクト標準の役割: "プロジェクト内の統一性（任意）"
  チーム標準の役割: "チームの生産性最大化"
```

**判断基準**:
```
Q: 個人の生産性に直接影響するか？
Yes → チーム標準を優先

Q: チーム外に影響がないか？
Yes → チームの判断に委ねる

Q: 組織標準は「推奨」レベルか？
Yes → チーム標準で上書き可能
```

---

## 矛盾解決の実践ガイド

### 判断フローチャート

```
標準の矛盾を発見
    ↓
[ステップ1] 領域を特定
    ├─ 統制必須領域？ → 組織標準を採用
    ├─ バランス領域？ → ステップ2へ
    └─ 柔軟性領域？ → チーム標準を採用
    ↓
[ステップ2] バランス領域での判断
    ├─ プロジェクト標準がある？ → プロジェクト標準を採用
    ├─ プロジェクト標準がない？ → 組織標準を採用
    └─ 組織標準が「推奨」レベル？ → プロジェクトで決定
    ↓
[ステップ3] 不明な場合
    ├─ DOCUMENT-USAGE-MANUAL.md の矛盾解決ルールを適用
    ├─ それでも不明 → Engineering Manager に相談
    └─ 重要な判断 → Architecture Review Committee にエスカレーション
    ↓
判断を文書化
```

### DOCUMENT-USAGE-MANUAL.md の矛盾解決ルール（補完）

既存のルールに加えて、階層ルールを適用：

```
矛盾解決の優先順位：
1. 領域別優先度ルール（本ドキュメント）← NEW
2. ルール1: 具体的 > 抽象的
3. ルール2: 新しい > 古い
4. ルール3: ドメイン固有 > 一般
5. ルール4: Tier 1 > Tier 2 > Tier 3 > Tier 4
```

---

## 例外承認プロセス

### 例外申請が必要なケース

```yaml
exception_required:
  統制必須領域:
    condition: "組織標準から逸脱する場合"
    approval: "CTO + Legal + Security"
    difficulty: "極めて困難"
    
  バランス領域:
    condition: "組織の承認済みリスト外の技術を使用する場合"
    approval: "Engineering Manager + Architect"
    difficulty: "中程度（正当な理由があれば承認）"
    
  柔軟性領域:
    condition: "基本的に不要"
    approval: "Engineering Manager（報告のみ）"
    difficulty: "容易"
```

### 例外申請プロセス

詳細は `10-governance/exception-approval-process.md` を参照。

**簡易フロー**:
```
1. 例外の必要性を確認
2. 代替案の検討（3つ以上）
3. リスク評価
4. 例外申請書の作成
5. 承認者への提出
6. 承認後、例外台帳に登録
7. 定期レビュー
```

---

## 実践例

### 例1: セキュリティ要件の矛盾 🔒

**状況**:
```yaml
organization_standard:
  requirement: "全APIで認証必須"
  document: "07-security-compliance/authentication-authorization.md"
  
project_standard:
  requirement: "開発環境では認証不要で開発効率を上げたい"
  
team_standard:
  requirement: "記載なし"
```

**判断**:
```yaml
領域: 統制必須領域（セキュリティ）
優先度ルール: 組織 > プロジェクト > チーム
結果: 組織標準を採用（全APIで認証必須）
理由: "セキュリティ標準は例外なし"
```

**代替案**:
- 開発環境専用の簡易認証機構を実装
- テストユーザーの自動ログイン機能
- モック認証サービスの利用

---

### 例2: API設計の矛盾 ⚖️

**状況**:
```yaml
organization_standard:
  recommendation: "RESTful APIを推奨"
  document: "02-architecture-standards/api-design-standards.md"
  level: "ガイドライン"
  
project_standard:
  requirement: "GraphQLを採用（リアルタイム要件のため）"
  justification: "WebSocketベースのリアルタイム通信が必須要件"
  
team_standard:
  requirement: "記載なし"
```

**判断**:
```yaml
領域: バランス領域（アーキテクチャパターン）
優先度ルール: プロジェクト > 組織 ≈ チーム
結果: プロジェクト標準を採用（GraphQL）
理由: "組織標準は推奨レベル、プロジェクト要件に応じて選択可能"
```

**承認プロセス**:
- Engineering Manager + Architect の承認
- 技術選定書に判断根拠を記載
- Architecture Review Committee に報告（情報共有）

---

### 例3: コーディングスタイルの矛盾 🎨

**状況**:
```yaml
organization_standard:
  recommendation: "インデント2スペース"
  document: "01-coding-standards/code-formatting.md"
  level: "推奨"
  
project_standard:
  requirement: "記載なし"
  
team_standard:
  requirement: "インデント4スペース（チーム合意）"
  reason: "既存コードベースが4スペース、可読性の好み"
```

**判断**:
```yaml
領域: 柔軟性重視領域（コーディングスタイル）
優先度ルール: チーム > プロジェクト > 組織
結果: チーム標準を採用（4スペース）
理由: "チームの生産性を優先、組織標準は参考程度"
```

**承認プロセス**:
- チーム内合意で決定
- Engineering Manager に報告（形式的）
- プロジェクトリポジトリの `.editorconfig` に設定

---

### 例4: 技術スタック選択の矛盾 ⚖️

**状況**:
```yaml
organization_standard:
  approved_list:
    - Node.js
    - Java
    - Python
    - Go
  document: "05-technology-stack/approved-technologies.md"
  
project_standard:
  requirement: "Rustを使用したい（パフォーマンス要件）"
  justification: "超低レイテンシー要件（<1ms）のため"
  
team_standard:
  requirement: "記載なし"
```

**判断**:
```yaml
領域: バランス領域（技術スタック）
優先度ルール: プロジェクト > 組織 ≈ チーム
特殊ケース: 承認済みリスト外の技術 → 例外申請必要
結果: 例外申請プロセスを実施
```

**承認プロセス**:
1. 例外申請書の作成
   - 代替案の検討（Node.js, Go で実現可能性）
   - パフォーマンスベンチマーク
   - リスク評価（人材確保、保守性）
2. Engineering Manager + Architect の承認
3. Architecture Review Committee での審議
4. 承認後、例外台帳に登録
5. 四半期レビュー

---

### 例5: ツール選択の矛盾 🎨

**状況**:
```yaml
organization_standard:
  recommendation: "VSCode推奨"
  document: "06-tools-and-environment/development-environment.md"
  level: "推奨"
  
project_standard:
  requirement: "記載なし"
  
team_standard:
  requirement: "JetBrains IDE使用（チーム合意）"
  reason: "チームメンバーの習熟度、デバッグ機能の好み"
```

**判断**:
```yaml
領域: 柔軟性重視領域（開発ツール）
優先度ルール: チーム > プロジェクト > 組織
結果: チーム標準を採用（JetBrains IDE）
理由: "個人の生産性を最優先、承認不要"
```

**承認プロセス**:
- 承認不要
- チーム内で合意があればOK
- Engineering Manager への報告も不要（任意）

---

## まとめ

### 優先度マトリクス

| 領域カテゴリ | 優先度 | 組織標準の役割 | 承認レベル | 例外の難易度 |
|------------|--------|--------------|-----------|------------|
| **🔒 統制必須** | 組織 > プロジェクト > チーム | 必須基準 | CTO + Legal + Security | 極めて困難 |
| **⚖️ バランス** | プロジェクト > 組織 ≈ チーム | ガイドライン | Eng Manager + Architect | 容易 |
| **🎨 柔軟性** | チーム > プロジェクト > 組織 | 参考情報 | Eng Manager（報告） | 非常に容易 |

### 迷ったときの判断基準

```
Q1: セキュリティ・コンプライアンスに関連するか？
    Yes → 組織標準を採用

Q2: 組織全体のリスクに影響するか？
    Yes → 組織標準を採用

Q3: プロジェクト固有の要件があるか？
    Yes → プロジェクト標準を優先

Q4: 個人・チームの生産性に関することか？
    Yes → チーム標準を優先

Q5: 組織標準は「必須」「推奨」「参考」のどれか？
    必須 → 組織標準を採用
    推奨/参考 → プロジェクト/チームで判断可能
```

### 参照ドキュメント

- **矛盾解決ルール**: `00-guides/DOCUMENT-USAGE-MANUAL.md`
- **例外承認プロセス**: `10-governance/exception-approval-process.md`
- **技術選定ガイド**: `10-governance/technology-radar.md`
- **統合ワークフロー**: `00-guides/AI-MASTER-WORKFLOW-GUIDE.md`

---

**バージョン履歴**:
- v1.0.0 (2025-11-12): 初版作成
