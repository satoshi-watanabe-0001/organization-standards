# AI System Development Audit Log Policy v2.0 (Normative Version)
## AIシステム開発監査ログポリシー v2.0（規範版）

---

## Table of Contents

- [1. Purpose and Scope (目的と適用範囲)](#section-1)
- [2. Definitions (用語定義)](#section-2)
- [3. Log Generation Requirements (ログ生成要件)](#section-3)
- [4. Three-Element Structure Requirements (3要素構造要件)](#section-4)
- [5. Evaluation Perspective Requirements (評価観点要件)](#section-5)
- [6. Confidentiality and Masking Requirements (機密保持・マスキング要件)](#section-6)
- [7. Storage Path Conventions (保存パス規約)](#section-7)
- [8. Failsafe Requirements (フェイルセーフ要件)](#section-8)
- [9. Feature Groups and Traceability (feature_groups/traceability要件)](#section-9)
- [Appendix A: Correspondence Table (付録A: 対応表)](#appendix-a)

---

## 1. Purpose and Scope (目的と適用範囲) {#section-1}

### 1.1 目的 (Purpose) {#1.1}

本ポリシーは、AIエージェント（AI Agent）がシステム開発において実行する全ての作業について、透明性（transparency）、説明責任（accountability）、および追跡可能性（traceability）を確保するための監査ログ（audit log）生成要件を定める。

本ポリシーの目的は以下の通りである：

- 開発プロセスの完全な記録と再現可能性の確保
- 品質（quality）、セキュリティ（security）、コスト（cost）観点での事後評価可能性
- 規制遵守（compliance）および内部統制の強化
- AIエージェントの意思決定過程の透明化

### 1.2 適用範囲 (Scope) {#1.2}

本ポリシーは以下に適用される：

- 自律型AIエージェントによるコード生成、変更、レビュー作業
- GitHubプルリクエスト（Pull Request）における開発成果物の記録
- ソフトウェア開発ライフサイクルにおけるAI支援作業全般

## 2. Definitions (用語定義) {#section-2}

### 2.1 基本用語 {#2.1}

- **AIエージェント (AI Agent)**: 自律的にソフトウェア開発作業を実行するAIシステム
- **監査ログ (Audit Log)**: AIエージェントの作業記録を構造化された形式で保存したデータ
- **要件 (Requirement)**: システム開発における単一の機能的または非機能的要求
- **成果物 (Artifact)**: 開発プロセスで生成、変更、削除されるファイル、設定、ドキュメント等
- **Feature Group**: 関連する複数の要件をグループ化した論理的単位

### 2.2 技術用語 {#2.2}

- **Chain-of-Thought (CoT)**: AIモデルの逐語的思考過程
- **Provenance**: データや成果物の来歴・由来情報
- **Traceability**: 要件から実装まで双方向追跡可能性

## 3. Log Generation Requirements (ログ生成要件) {#section-3}

### 3.1 必須要件 (MUST Requirements) {#3.1}

#### 3.1.1 生成タイミング (Generation Timing)

AIエージェントは以下の条件下で監査ログを生成しなければならない（**_MUST_**）：

- 単一要件の実装完了時
- 作業が異常終了した場合
- 外部システムとの相互作用完了時

#### 3.1.2 一要件一ログ原則 (One Log Per Requirement)

AIエージェントは要件単位でログを分離しなければならない（**_MUST_**）。複数要件を含む単一ログの生成は禁止される（**_MUST NOT_**）。

#### 3.1.3 形式仕様 (Format Specifications)

監査ログは以下の形式に準拠しなければならない（**_MUST_**）：

- JSON形式（RFC8259準拠）
- スキーマバージョン明記
- UTF-8エンコーディング
- 構造化データとして機械可読

### 3.2 推奨要件 (SHOULD Requirements) {#3.2}

#### 3.2.1 関連要件参照

ログには関連要件への参照を含むべきである（**_SHOULD_**）。

#### 3.2.2 外部標準準拠

可能な限りOpenTelemetry GenAI semantic conventions (gen_ai.*) に準拠すべきである（**_SHOULD_**）。

### 3.3 任意要件 (MAY Requirements) {#3.3}

#### 3.3.1 拡張フィールド

組織固有の拡張フィールドを追加してもよい（**_MAY_**）。ただし、スキーマ互換性を維持しなければならない（**_MUST_**）。

## 4. Three-Element Structure Requirements (3要素構造要件) {#section-4}

### 4.1 PLAN要素 {#4.1}

#### 4.1.1 必須内容 (Mandatory Content)

PLAN要素は以下を含まなければならない（**_MUST_**）：

- 受入条件（acceptance criteria）の明記
- 実行手順（steps）の詳細化
- 依存関係（dependencies）の特定
- 各ステップの実行状態（planned/done/skipped/failed）

#### 4.1.2 ツール呼び出し記録

外部ツール呼び出しがある場合、以下を記録しなければならない（**_MUST_**）：

- ツール名
- 実行目的
- 実行結果
- エビデンス（機密部分はマスク）

### 4.2 RATIONALE要素 {#4.2}

#### 4.2.1 Chain-of-Thought逐語禁止

AIエージェントは逐語的思考過程（verbatim Chain-of-Thought）を記録してはならない（**_MUST NOT_**）。内部思考は要約形式（summarized form）でのみ記録が許可される。

#### 4.2.2 必須内容

RATIONALE要素は以下を含まなければならない（**_MUST_**）：

- 判断根拠の要約（summary）
- 代替案（alternatives）の比較（最低2案）
- 採用/却下理由
- 特定されたリスクと軽減策
- 前提条件（assumptions）

#### 4.2.3 根拠の客観性

判断根拠は観測可能な事実、既存制約、技術仕様に基づかなければならない（**_MUST_**）。

### 4.3 ARTIFACTS要素 {#4.3}

#### 4.3.1 変更ファイル記録

全ての変更ファイルについて以下を記録しなければならない（**_MUST_**）：

- ファイルパス
- 変更タイプ（added/modified/deleted/renamed）
- 変更概要
- 差分ハイライト（可能な場合）

#### 4.3.2 テスト結果

実行されたテストについて以下を記録しなければならない（**_MUST_**）：

- テストタイプ（unit/integration/e2e/lint/security_scan/perf）
- テスト名
- 実行結果（pass/fail/skipped）
- エビデンス

## 5. Evaluation Perspective Requirements (評価観点要件) {#section-5}

### 5.1 品質評価 (Quality Assessment) {#5.1}

#### 5.1.1 必須評価項目

以下の品質観点を評価しなければならない（**_MUST_**）：

- スコア（1-5段階）
- テストカバレッジ状況
- 後方互換性への影響
- 可読性・保守性の考慮

### 5.2 セキュリティ評価 (Security Assessment) {#5.2}

#### 5.2.1 脅威考慮

以下のセキュリティ脅威を考慮しなければならない（**_MUST_**）：

- Injection攻撃（SQLインジェクション、Prompt Injectionを含む）
- 認証・認可の不備
- 機密情報の漏洩
- SSRF（Server-Side Request Forgery）
- 依存関係のリスク
- ログ・監視の不備

### 5.3 コスト評価 (Cost Assessment) {#5.3}

#### 5.3.1 必須考慮項目

以下のコスト要因を評価しなければならない（**_MUST_**）：

- 計算量・処理コスト
- 外部API呼び出しコスト
- スケール時のコスト影響
- 代替案との比較

## 6. Confidentiality and Masking Requirements (機密保持・マスキング要件) {#section-6}

### 6.1 マスク対象 (Items to be Masked) {#6.1}

#### 6.1.1 必須マスク項目

以下の情報は記録してはならない（**_MUST NOT_**）、またはマスクしなければならない（**_MUST_**）：

- APIキー、シークレット、パスワード
- 個人情報（PII）
- 内部ホスト名、IPアドレス
- 脆弱性悪用手順の詳細

### 6.2 マスキング記法 (Masking Notation) {#6.2}

#### 6.2.1 標準記法

機密情報のマスキングは以下の記法を使用しなければならない（**_MUST_**）：

- `[REDACTED:API_KEY]` - APIキー
- `[REDACTED:PII]` - 個人情報
- `[REDACTED:HOST]` - ホスト情報
- `[REDACTED:SECRET]` - その他機密情報

#### 6.2.2 マスキング記録

マスクした情報については、何をなぜマスクしたかを別途記録しなければならない（**_MUST_**）。

## 7. Storage Path Conventions (保存パス規約) {#section-7}

### 7.1 ファイル命名規則 (File Naming Conventions) {#7.1}

#### 7.1.1 標準形式

監査ログファイルは以下の命名規則に従わなければならない（**_MUST_**）：

```
{YYYY-MM-DD}_{HH-mm-ss}_{feature_group}_{requirement_id}_audit.json
```

### 7.2 ディレクトリ構造 (Directory Structure) {#7.2}

#### 7.2.1 階層構造

以下のディレクトリ構造を使用しなければならない（**_MUST_**）：

```
/audit_logs/
├── {YYYY}/
│   ├── {MM}/
│   │   ├── feature_groups/
│   │   │   └── {feature_group_name}/
│   │   │       └── [audit log files]
```

### 7.3 保管ポリシー (Retention Policy) {#7.3}

#### 7.3.1 保管期間

監査ログは最低3年間保管しなければならない（**_MUST_**）。

## 8. Failsafe Requirements (フェイルセーフ要件) {#section-8}

### 8.1 ログ生成失敗時の対応 (Log Generation Failure Handling) {#8.1}

#### 8.1.1 必須動作

ログ生成に失敗した場合、以下を実行しなければならない（**_MUST_**）：

- エラー詳細の記録
- 部分ログの保存（可能な場合）
- 作業継続可否の判断
- エスカレーション実行

### 8.2 部分ログ処理 (Partial Log Handling) {#8.2}

#### 8.2.1 最小限記録

部分ログでも以下は必ず含まなければならない（**_MUST_**）：

- タイムスタンプ
- 実行者情報
- 失敗理由
- 実行済み作業の概要

## 9. Feature Groups and Traceability (feature_groups/traceability要件) {#section-9}

### 9.1 Feature Group定義 (Feature Group Definition) {#9.1}

#### 9.1.1 グルーピング基準

関連要件は以下の基準でグループ化しなければならない（**_MUST_**）：

- 機能的関連性
- 技術的依存関係
- ビジネス価値の関連性

### 9.2 トレーサビリティマッピング (Traceability Mapping) {#9.2}

#### 9.2.1 必須リンク

各監査ログは以下へのトレーサビリティを確保しなければならない（**_MUST_**）：

- 元要件への参照
- 関連要件への参照
- 成果物への参照
- テスト結果への参照

### 9.3 相互参照要件 (Cross-Reference Requirements) {#9.3}

#### 9.3.1 双方向参照

要件と実装間の双方向参照を維持しなければならない（**_MUST_**）。

---

## Appendix A: Correspondence Table (付録A: 対応表) {#appendix-a}

| 旧表現（システムメッセージ型） | 新表現（ポリシー条文） |
|---|---|
| "あなたはソフトウェア開発AIです" | AIエージェントの適用範囲として定義（1.2項） |
| "必ず「PR監査ログ」を1つ出力する" | 一要件一ログ原則として必須化（3.1.2項） |
| "JSONは本メッセージで指定するスキーマに準拠" | 形式仕様として必須化（3.1.3項） |
| "生の思考（逐語的なChain-of-Thought）を出力・保存してはならない" | Chain-of-Thought逐語禁止として明文化（4.2.1項） |
| "代替案（最低2案）" | RATIONALE要素の必須内容として規定（4.2.2項） |
| "機密保持・安全" | 機密保持・マスキング要件として独立章化（6章） |
| "シークレット、APIキー...は[REDACTED]に置換" | 必須マスク項目とマスキング記法として詳細化（6.1-6.2項） |
| "PR監査ログJSON" | 保存パス規約として構造化（7章） |
| "すべての作業完了時" | 生成タイミングとして必須化（3.1.1項） |
| "品質・セキュリティ・コスト" | 評価観点要件として詳細化（5章） |
| "ツール呼び出し" | ツール呼び出し記録として必須化（4.1.2項） |
| "重要な設計判断がある場合はADR" | 推奨要件として位置づけ（3.2項） |

---

**Document Version:** v2.0  
**Effective Date:** 2026-02-06  
**Next Review Date:** 2027-02-06  
**Approved By:** AI System Development Committee

本ポリシーは、AI開発の透明性と説明責任を確保し、品質・セキュリティ・コストの観点から適切な監査証跡を維持することを目的として制定されました。全てのAIエージェントは本ポリシーに準拠した監査ログの生成を実行しなければなりません。