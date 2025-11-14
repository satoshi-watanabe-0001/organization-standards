---
title: "Phase 4 Review & QA Guide"
version: "1.0.0"
created_date: "2025-11-05"
last_updated: "2025-11-05"
status: "Active"
phase: "Phase 4 - Review & Quality Assurance"
owner: "Engineering & QA Team"
document_type: "Lightweight Navigation Guide"
---

# Phase 4: レビュー・品質保証ガイド

> 実装から品質保証へ - 軽量ナビゲーションガイド

**対象**: Devin、Cursor、その他の自律型AIエージェント  
**前提**: Phase 3（実装）が完了している  
**目的**: コードレビュー、テスト、品質チェックを実施し、リリース可能な品質を達成する

---

## 📋 このガイドについて

### ドキュメントタイプ
- **軽量ナビゲーション型**: 既存ドキュメントへの参照を重視
- **所要時間**: 5-10分で読める
- **重複回避**: 詳細は既存ドキュメントを参照

### Phase 4 の位置づけ

```
Phase 2: 設計
    ↓
Phase 3: 実装
    ↓
┌─────────────────────────────────────────┐
│  Phase 4: レビュー・品質保証 ★ここ        │
│  - コードレビュー                         │
│  - テスト実施（ユニット・統合・E2E）      │
│  - 品質メトリクス測定                     │
│  - 不具合管理                            │
└─────────────────────────────────────────┘
    ↓
Phase 5: デプロイメント
```

### 開始条件

Phase 4は以下の条件を満たす場合に実施：
- ✅ **すべてのプロジェクト**: 必須
  - コードレビューとテストは必ず実施
- ⚠️ **緊急Hotfix**: **簡易版で実施** (30分-1時間)
  - 最小限のレビューとテスト
  - 影響範囲のテストのみ

---

## ⚠️ 禁止事項チェック

### このフェーズ特有の禁止事項
- **レビュー省略**: コードレビューをスキップしてデプロイしてはいけない
- **テスト不十分**: カバレッジが組織基準未満のまま進めてはいけない
- **自己レビューのみ**: 自分で書いたコードを自分だけでレビューして完了としてはいけない
- **不具合放置**: 発見した不具合を未修正のまま放置してはいけない

### 全フェーズ共通禁止事項
- **CI/CD設定の無断変更**: 明示的な指示がない限り、GitHub Actions、CircleCI、Jenkins等のCI/CD設定ファイルを変更してはいけない
- **本番環境への直接変更**: 本番データベース、本番サーバー、本番設定ファイルを直接変更してはいけない
- **セキュリティ設定の独断変更**: 認証・認可・暗号化等のセキュリティ設定を独自判断で変更してはいけない
- **組織標準外技術の無断導入**: プロジェクトで使用していない新しいライブラリ・フレームワーク・言語を独断で導入してはいけない
- **プロジェクト構造の大幅変更**: ディレクトリ構造、命名規則、アーキテクチャパターンを独断で大きく変更してはいけない

### 📚 詳細確認
禁止事項の詳細、具体例、例外ケースについては以下のドキュメントを参照してください：
- [AI開発タスクの禁止事項](../01-organization-standards/ai-task-prohibitions.md)
- [禁止事項チェックリスト](../01-organization-standards/ai-task-prohibitions-checklist.md)

---

## 🎯 Phase 4 の成果物

### 必須成果物
1. **コードレビューレポート**
   - レビュー結果
   - 指摘事項と対応状況

2. **テストコード**
   - ユニットテスト
   - 統合テスト
   - E2Eテスト（該当する場合）

3. **テスト実行結果レポート**
   - すべてのテスト実行結果
   - カバレッジレポート
   - 失敗したテストの分析

4. **品質メトリクスレポート**
   - コード品質スコア
   - 複雑度メトリクス
   - 技術的負債の評価

### 推奨成果物
5. **パフォーマンステストレポート**（該当する場合）
6. **セキュリティスキャンレポート**
7. **不具合管理票**（発見された場合）

---

## 📊 Phase 4 の所要時間

| プロジェクトタイプ | 所要時間 | 含まれるタスク |
|------------------|---------|---------------|
| **新規プロジェクト** | 4-6時間 | 全タスク、カバレッジ目標達成 |
| **既存への大規模機能追加** | 3-4時間 | 影響範囲のテスト、回帰テスト |
| **既存への小規模機能追加** | 2-3時間 | 新規機能のテスト、一部回帰テスト |
| **バグ修正** | 1-2時間 | 修正箇所のテスト追加、回帰テスト |
| **リファクタリング** | 1-2時間 | 既存テスト実行、必要に応じて追加 |
| **緊急Hotfix** | 30分-1時間 | 最小限のレビュー・テスト |

---

## 🔄 Phase 4 のワークフロー: 7ステップ

### **Step 4.1: コードレビュー** (30-60分)

**実行内容**:
1. レビュー観点の確認
2. コードレビュー実施
3. フィードバックの反映

**チェックリスト**:
- [ ] コーディング標準に準拠しているか
- [ ] 設計原則（SOLID等）に従っているか
- [ ] セキュリティ脆弱性がないか
- [ ] パフォーマンス上の問題がないか
- [ ] エラーハンドリングが適切か
- [ ] ログ出力が適切か
- [ ] コメント・ドキュメントが十分か
- [ ] テストが適切に実装されているか

**🔴 必須参照**:
- [`03-development-process/code-review-standards.md`](../../03-development-process/code-review-standards.md)
  - **このドキュメントの記載内容に従ってコードレビューを実施**
  - **レビュー観点とチェックリストを確認**

**🟡 推奨参照**:
- 該当する言語別コーディング標準:
  - [`01-coding-standards/typescript.md`](../../01-coding-standards/typescript.md) (TypeScript)
  - [`01-coding-standards/python.md`](../../01-coding-standards/python.md) (Python)
  - [`01-coding-standards/java.md`](../../01-coding-standards/java.md) (Java)
- [`04-quality-standards/code-quality-standards.md`](../../04-quality-standards/code-quality-standards.md)
  - **コード品質の基準を確認**

**レビューの進め方**:
1. 自動チェック（リンター、フォーマッター）の実行
2. 手動レビュー（ロジック、設計、セキュリティ）
3. フィードバックの記録と対応

---



### **Step 4.1.5: CI設定とCI実行結果の検証** ⭐NEW (10-15分)

**実行内容**:
1. PRのCI実行状態確認
2. CI品質ゲートの実行確認
3. カバレッジ閾値達成確認

**目的**: CI/CD設定が組織標準に準拠し、すべての品質ゲートが実行されたことを確認

---

#### **チェックリスト（品質ゲート）**

**🔴 Level 1: 必須（不合格でマージ不可）**

- [ ] **CI実行結果**: 最新コミットでCIがすべてパスしている
- [ ] **品質ゲート実行確認**: CIログで以下が実行されている
  - [ ] Linting (ESLint/pylint/Checkstyle)
  - [ ] Format check (Prettier/black/spotlessCheck)
  - [ ] Build (compile/bundle)
  - [ ] Test (all tests pass)
  - [ ] **Coverage verification (80%閾値チェック)** ⭐最重要

- [ ] **設定ファイル確認**:
  - [ ] `.github/workflows/ci.yaml` が存在し、必須ステップが含まれている
  - [ ] 言語別設定ファイル（`build.gradle` / `package.json` / `pyproject.toml`）にカバレッジ閾値設定がある

---

#### **検証手順**

##### **1. GitHub UI でCI実行状態確認** (3分)

1. PRページを開く
2. **Checks** タブをクリック
3. すべてのチェックが ✅ (パス) になっていることを確認

**期待される結果**:
```
✓ CI Pipeline / build (pull_request)  ← すべて緑色
```

---

##### **2. CIログで品質ゲート実行確認** (5分)

**各ステップのログを展開して確認**:

| 言語 | 確認すべきステップ | ログ内キーワード |
|-----|------------------|----------------|
| **Java** | Run Spotless Check | `spotlessCheck`, `BUILD SUCCESSFUL` |
|  | Run Checkstyle | `checkstyleMain`, `BUILD SUCCESSFUL` |
|  | Run Tests | `test`, `BUILD SUCCESSFUL` |
|  | **Verify Test Coverage** | `jacocoTestCoverageVerification`, `BUILD SUCCESSFUL` ⭐ |
| **TypeScript** | Run Lint | `eslint`, `✓` |
|  | Check Code Formatting | `prettier --check`, `✓` |
|  | Type Check | `tsc --noEmit`, `✓` |
|  | **Run Tests with Coverage** | `PASS`, `Coverage summary`, `80%` ⭐ |
| **Python** | Run Pylint | `pylint`, `✓` |
|  | Check Code Formatting | `black --check`, `✓` |
|  | Type Check | `mypy`, `✓` |
|  | **Run Tests with Coverage** | `passed`, `80%`, `PASSED` ⭐ |

**最重要**: カバレッジ閾値チェックが実行され、パスしていることを確認

---

##### **3. カバレッジレポート確認** (2分)

**CIアーティファクトからカバレッジレポートをダウンロード**:

1. PRページの「Checks」タブ
2. 該当するCIジョブを選択
3. 「Artifacts」セクションからカバレッジレポートをダウンロード
   - Java: `coverage-report` (HTML)
   - TypeScript: `coverage-report` (HTML)
   - Python: `coverage-report` (HTML)

**確認項目**:
- 全体のカバレッジ率が 80% 以上
- 主要なクラス/モジュールのカバレッジが適切

---

##### **4. 設定ファイル確認** (5分)

**言語別に設定ファイルをレビュー**:

**Java の場合**:
```bash
# build.gradle で確認
# 1. JaCoCo plugin が存在するか
grep "id 'jacoco'" build.gradle

# 2. カバレッジ閾値設定が存在するか
grep -A 5 "jacocoTestCoverageVerification" build.gradle | grep "minimum = 0.8"

# 3. CI ワークフローで実行されているか
grep "jacocoTestCoverageVerification" .github/workflows/ci.yaml
```

**TypeScript の場合**:
```bash
# jest.config.js で確認
grep -A 6 "coverageThreshold" jest.config.js

# 期待される出力:
# coverageThreshold: {
#   global: {
#     lines: 80,
#     ...
#   }
# }

# CI ワークフローで実行されているか
grep "test:coverage" .github/workflows/ci.yaml
```

**Python の場合**:
```bash
# .coveragerc または pyproject.toml で確認
grep "fail_under = 80" .coveragerc
# または
grep "cov-fail-under=80" pyproject.toml

# CI ワークフローで実行されているか
grep "cov-fail-under" .github/workflows/ci.yaml
```

---

#### **不合格時の対応**

**ケース1: CIがパスしていない**

→ CI実行結果のエラーログを確認  
→ エラーを修正してコミット・プッシュ  
→ CI再実行を待つ

---

**ケース2: カバレッジ閾値チェックが見つからない**

→ `/00-guides/CI-SETUP-CHECKLIST.md` を実行  
→ 不足している設定を追加  
→ コミット・プッシュしてCI再実行

---

**ケース3: カバレッジが80%未満**

→ カバレッジレポートで不足している部分を特定  
→ ユニットテストを追加  
→ コミット・プッシュしてCI再実行

参照: `/04-quality-standards/unit-testing.md`

---

**ケース4: CIログで品質ゲートが実行されていない**

→ `.github/workflows/ci.yaml` を確認  
→ 必須ステップが含まれているか確認  
→ 不足しているステップを追加  
→ コミット・プッシュしてCI再実行

参照: `/00-guides/CI-SETUP-CHECKLIST.md` セクション2（言語別必須設定）

---

#### **🔴 必須参照**

- [`/00-guides/CI-SETUP-CHECKLIST.md`](../CI-SETUP-CHECKLIST.md)
  - **CI設定の詳細チェック方法**
  - **言語別必須設定マトリクス**
  - **よくある設定ミスと対策**

- [`/03-development-process/ci-cd-pipeline.md`](../../03-development-process/ci-cd-pipeline.md)
  - **CI/CD標準全般**
  - **CI品質ゲートの定義**

- [`/04-quality-standards/code-quality-standards.md`](../../04-quality-standards/code-quality-standards.md)
  - **コード品質基準**
  - **静的コード解析基準**

- [`/04-quality-standards/testing-standards.md`](../../04-quality-standards/testing-standards.md)
  - **テストカバレッジ基準（80%）**
  - **カバレッジ測定方法**

---

#### **完了条件**

**すべて ✅ になっていること**:

- [ ] 🔴 PRのCIがすべてパスしている
- [ ] 🔴 CIログで品質ゲートが実行されたことを確認
- [ ] 🔴 カバレッジレポートで80%以上を確認
- [ ] 🔴 設定ファイルに必須設定が含まれている

**これらが ✅ でない場合、レビュー不合格。実装者に差し戻し**

---

#### **自動化（AIエージェント向け）**

Devin / Cursor が自動実行すべきスクリプト:

```python
def verify_ci_execution_in_review():
    """
    Phase 4レビュー時のCI実行結果検証
    """
    print("🔍 CI実行結果を検証中...")
    
    # 1. CI実行状態確認
    ci_status = get_ci_status()  # GitHub API から取得
    if ci_status != 'success':
        print(f"❌ CI failed with status: {ci_status}")
        print("   → CI実行結果のエラーログを確認してください")
        return False
    
    # 2. CIログ取得
    ci_log = get_latest_ci_log()
    
    # 3. 必須品質ゲートの実行確認
    required_checks = {
        'format': ['spotlessCheck', 'prettier', 'black --check'],
        'lint': ['checkstyle', 'eslint', 'pylint'],
        'coverage': [
            'jacocoTestCoverageVerification',
            'coverageThreshold',
            'fail_under',
            'Coverage check'
        ],
        'build': ['BUILD SUCCESSFUL', 'compiled successfully'],
        'test': ['tests passed', 'All tests', 'PASS']
    }
    
    issues = []
    for check_name, keywords in required_checks.items():
        if not any(kw in ci_log for kw in keywords):
            issues.append(f"❌ {check_name} check not found in CI log")
    
    if issues:
        print("❌ CI実行で品質ゲートが実行されていません:")
        for issue in issues:
            print(f"   {issue}")
        print("\n📋 対応:")
        print("   1. .github/workflows/ci.yaml を確認")
        print("   2. /00-guides/CI-SETUP-CHECKLIST.md を参照")
        return False
    
    # 4. カバレッジ率確認
    coverage_match = re.search(r'(\d+)%\s+coverage', ci_log)
    if coverage_match:
        coverage_pct = int(coverage_match.group(1))
        if coverage_pct >= 80:
            print(f"✅ カバレッジ {coverage_pct}% (>= 80%)")
        else:
            print(f"❌ カバレッジ {coverage_pct}% (< 80%)")
            print("   → ユニットテストを追加してください")
            return False
    
    print("✅ すべてのCI検証が完了しました")
    return True
```

---

---

### **Step 4.1.6: PR説明文・言語チェック** ⭐NEW (5分)

**実行内容**:
1. PRタイトルの言語確認
2. PR説明文の言語確認
3. CI品質ゲート（言語チェック）の通過確認

**重要**: PRの説明文とタイトルは**日本語で記載することが必須**です。

**チェックリスト**:
- [ ] PRタイトルが日本語で記載されている
- [ ] PR説明文が日本語で記載されている
- [ ] 変更内容の概要が日本語で明確に記載されている
- [ ] テスト結果が日本語で記載されている
- [ ] レビュアーへの補足が日本語で記載されている
- [ ] CI品質ゲート「PR Language Check」が✅合格している

**英語記載の場合の対応**:

1. **PRを修正する（推奨）**
   - 「Edit」ボタンをクリック
   - タイトル・説明文を日本語に修正
   - 保存
   - CI自動再実行 → ✅合格を確認

2. **PRを再作成する**
   - PRクローズ
   - 日本語で新規PR作成
   - CI自動検証 → ✅合格を確認

**例外**:
- **技術用語**: API、OAuth、JWT、HTTP、REST等は英語可
- **コードブロック内**: コメントは英語可
- **固有名詞**: GitHub、Docker、Kubernetes等は英語可

**良い例（日本語）**:
```markdown
タイトル: JWT認証機能の実装

## 変更内容
RESTful APIにJWT（JSON Web Token）認証を実装しました。
OAuth 2.0プロトコルに準拠し、セキュアなトークン管理を実現しています。

## テスト結果
- 単体テストカバレッジ: 95%
- 統合テスト: すべて合格
- セキュリティテスト: 脆弱性なし
```

**悪い例（英語）**:
```markdown
Title: Implement JWT authentication

## Changes
Implemented JWT authentication for RESTful API.

## Test Results
- Unit test coverage: 95%
- Integration test: All passed
```

**自動検証**:

CI品質ゲート「PR Language Check」が自動的に検証します：
- ✅ 日本語検出 → 合格（緑チェック）
- ❌ 英語のみ → 失敗（赤バツ）+ botコメントで修正方法を通知

**📖 参照**:
- [PRテンプレート](../../08-templates/pr-templates/PULL_REQUEST_TEMPLATE.md)
- [PR言語問題の解決策](../../00-guides/PR-LANGUAGE-ISSUE-SOLUTION.md)

---

### **Step 4.1.7: PR記載レベル品質チェック** ⭐NEW (5分)

**実行内容**:
1. PR説明文の記載レベル確認
2. 必須セクションの存在確認
3. チェックリスト確認率の検証
4. CI品質ゲート（記載レベルチェック）の通過確認

**重要**: PRの説明文は**組織標準の記載レベル**を満たすことが必須です。

**チェックリスト**:
- [ ] 必須セクション7つがすべて記載されている
  - 変更内容の概要
  - 関連チケット
  - 変更の詳細
  - テストの実施状況
  - セキュリティチェック
  - 組織標準チェックリスト
  - レビュアーへの補足
- [ ] 変更内容の概要が50文字以上（推奨100文字以上）
- [ ] チェックボックス確認率が70%以上（推奨85%以上）
- [ ] 日本語で記載されている
- [ ] 関連チケット番号が明確（#123, PBI-456等）
- [ ] CI品質ゲート「PR Description Quality Gate」が✅合格している

**記載レベル未達の場合の対応**:

1. **PRを修正する（推奨）**
   - 「Edit」ボタンをクリック
   - 不足しているセクションを追加
   - 変更内容の概要を50文字以上に拡充
   - チェックリストを70%以上確認
   - 保存
   - CI自動再実行 → ✅合格を確認

2. **具体的な改善方法**
   - CIコメントに記載された改善点を確認
   - 不足セクション: テンプレートを参照して追加
   - 文字数不足: 変更の背景・影響範囲を追記
   - チェックボックス: 実施した項目にチェック

**良い例（記載レベル合格）**:
```markdown
## 📋 変更内容の概要

JWT認証機能を実装しました。RESTful APIにOAuth 2.0プロトコルに準拠した
セキュアなトークン管理機能を追加し、認証の信頼性を向上させています。
また、トークンのリフレッシュ機能も実装しました。

## 🎯 関連チケット

#456

## 🔍 変更の詳細

### 機能追加
- [x] JWT認証エンドポイントの実装
- [x] トークンリフレッシュ機能
- [x] 認証ミドルウェアの実装

## ✅ テストの実施状況

- [x] 単体テストを追加（カバレッジ95%）
- [x] 統合テストを追加
- [x] セキュリティテストを実施
- [x] 手動テストを実施
- [ ] E2Eテスト（次PRで実施予定）
- [ ] 負荷テスト（本番前に実施予定）

## 🔒 セキュリティチェック

- [x] セキュリティスキャンを実施
- [x] 脆弱性なし
- [x] シークレット漏洩なし
- [x] 認証・認可の実装を確認
- [x] SQLインジェクション対策を確認

## ✅ 組織標準チェックリスト

- [x] 日本語での記載を完了している
- [x] コーディング規約に準拠している
- [x] 全てのパブリックAPIにドキュメントコメントを記載している
- [x] CI品質ゲートが全て通過している

## 📖 レビュアーへの補足

認証フローはOAuth 2.0の標準仕様に従っています。
トークンの有効期限は15分、リフレッシュトークンは7日間です。
```

**悪い例（記載レベル未達）**:
```markdown
## 変更内容

JWT認証を追加

## テスト

テスト済み
```

**自動検証**:

CI品質ゲート「PR Description Quality Gate」が自動的に検証します：
- ✅ 記載レベル合格 → ✅通過（緑チェック）
- ❌ 記載レベル未達 → ❌失敗（赤バツ）+ botコメントで改善点を通知

**検証項目**:
1. 必須セクション7つの存在
2. 変更内容の概要: 50文字以上
3. チェックボックス確認率: 70%以上
4. 日本語記載の確認
5. 関連チケット番号の形式チェック
6. 組織標準チェックリストの確認状況

**統計情報の表示**:
CIコメントには以下の情報が表示されます：
- 変更内容の概要の文字数
- チェックボックス確認率（X/Y個、Z%）
- 不足しているセクション
- 具体的な改善方法

**📖 参照**:
- [PRテンプレート](../../08-templates/pr-templates/PULL_REQUEST_TEMPLATE.md)
- [PR記載レベル自動チェック実装ガイド](../../08-templates/ci-templates/github-actions/pr-description-quality-gate.md)
- [AI-PRE-WORK-CHECKLIST v1.3.0](../AI-PRE-WORK-CHECKLIST.md) - Stage 3参照

---

### **Step 4.2: テスト戦略の確認** (10-15分)

**実行内容**:
1. テスト戦略の確認
2. 必要なテストレベルの決定
3. カバレッジ目標の設定

**チェックリスト**:
- [ ] どのテストレベルが必要か確認（ユニット・統合・E2E）
- [ ] カバレッジ目標を確認（通常80%以上）
- [ ] テストデータの準備方法を確認
- [ ] テスト環境の準備状況を確認

**🔴 必須参照**:
- [`04-quality-standards/testing-strategy.md`](../../04-quality-standards/testing-strategy.md)
  - **このドキュメントの記載内容に従ってテスト戦略を策定**
- [`03-development-process/testing-standards.md`](../../03-development-process/testing-standards.md)
  - **テスト標準を確認**

---

### **Step 4.3: ユニットテスト** (60-90分)

**実行内容**:
1. ユニットテストケースの作成
2. ユニットテストの実装
3. テスト実行とカバレッジ測定

**チェックリスト**:
- [ ] すべての公開メソッド/関数にテストがあるか
- [ ] 正常系と異常系の両方をテストしているか
- [ ] エッジケースをテストしているか
- [ ] モック/スタブを適切に使用しているか
- [ ] テストカバレッジが目標値に達しているか
- [ ] すべてのテストがパスしているか

**🔴 必須参照**:
- [`04-quality-standards/unit-testing.md`](../../04-quality-standards/unit-testing.md)
  - **このドキュメントの記載内容に従ってユニットテストを実装**
  - **AAA（Arrange-Act-Assert）パターン等の実装パターンを確認**

**ユニットテストの例（TypeScript + Jest）**:
```typescript
describe('UserService', () => {
  describe('createUser', () => {
    it('should create a user with valid data', async () => {
      // Arrange
      const userData = { name: 'John', email: 'john@example.com' };
      
      // Act
      const user = await userService.createUser(userData);
      
      // Assert
      expect(user).toBeDefined();
      expect(user.name).toBe('John');
    });

    it('should throw error for invalid email', async () => {
      // Arrange
      const userData = { name: 'John', email: 'invalid' };
      
      // Act & Assert
      await expect(userService.createUser(userData))
        .rejects.toThrow('Invalid email');
    });
  });
});
```

---

### **Step 4.4: 統合テスト** (45-60分)

**実行内容**:
1. 統合テストシナリオの作成
2. 統合テストの実装
3. テスト実行と結果確認

**チェックリスト**:
- [ ] コンポーネント間の連携をテストしているか
- [ ] データベース操作をテストしているか
- [ ] 外部APIとの連携をテストしているか
- [ ] トランザクション処理をテストしているか
- [ ] すべてのテストがパスしているか

**🔴 必須参照**:
- [`04-quality-standards/integration-testing.md`](../../04-quality-standards/integration-testing.md)
  - **このドキュメントの記載内容に従って統合テストを実装**

**🟡 推奨参照**:
- [`04-quality-standards/test-data-management.md`](../../04-quality-standards/test-data-management.md)
  - **テストデータの管理方法を確認**

---

### **Step 4.5: E2Eテスト** (60-90分、該当する場合)

**実行内容**:
1. E2Eテストシナリオの作成
2. E2Eテストの実装
3. テスト実行と結果確認

**チェックリスト**:
- [ ] 主要なユーザーフローをテストしているか
- [ ] UIの表示と操作をテストしているか
- [ ] エンドツーエンドのデータフローをテストしているか
- [ ] エラーシナリオをテストしているか
- [ ] すべてのテストがパスしているか

**🔴 必須参照**:
- [`04-quality-standards/e2e-testing.md`](../../04-quality-standards/e2e-testing.md)
  - **このドキュメントの記載内容に従ってE2Eテストを実装**

**E2Eテストの例（Playwright）**:
```typescript
test('user can login and view dashboard', async ({ page }) => {
  // Navigate to login page
  await page.goto('/login');
  
  // Fill login form
  await page.fill('#email', 'user@example.com');
  await page.fill('#password', 'password123');
  await page.click('button[type="submit"]');
  
  // Verify navigation to dashboard
  await expect(page).toHaveURL('/dashboard');
  await expect(page.locator('h1')).toHaveText('Dashboard');
});
```

---

### **Step 4.6: コード品質チェック** (15-20分)

**実行内容**:
1. 静的解析の実行
2. リンター/フォーマッターのチェック
3. 品質メトリクスの測定

**チェックリスト**:
- [ ] リンターエラーがゼロであるか
- [ ] フォーマッターチェックがパスしているか
- [ ] 静的解析ツールの警告を確認
- [ ] 循環的複雑度が許容範囲内か
- [ ] コードの重複がないか
- [ ] 技術的負債スコアを確認

**🔴 必須参照**:
- [`04-quality-standards/code-quality-standards.md`](../../04-quality-standards/code-quality-standards.md)
  - **コード品質の基準を確認**
- [`04-quality-standards/quality-metrics.md`](../../04-quality-standards/quality-metrics.md)
  - **品質メトリクスの測定方法を確認**

**実行例**:
```bash
# リンターチェック
npm run lint

# フォーマッターチェック
npm run format:check

# テストカバレッジ測定
npm run test:coverage

# 静的解析（SonarQube等）
sonar-scanner
```

---

### **Step 4.7: パフォーマンス・セキュリティテスト** (30-60分、オプション)

**実行内容**:
1. パフォーマンステストの実施
2. セキュリティスキャンの実施
3. 結果の分析と改善

**チェックリスト**:
- [ ] レスポンスタイムが目標値以内か
- [ ] スループットが目標値を達成しているか
- [ ] メモリリークがないか
- [ ] セキュリティ脆弱性スキャンを実施
- [ ] 依存関係の脆弱性をチェック

**🟡 推奨参照**:
- [`04-quality-standards/performance-testing.md`](../../04-quality-standards/performance-testing.md)
  - **パフォーマンステストの実施方法を確認**
- [`04-quality-standards/security-testing.md`](../../04-quality-standards/security-testing.md)
  - **セキュリティテストの実施方法を確認**
- [`04-quality-standards/load-testing.md`](../../04-quality-standards/load-testing.md)
  - **負荷テストの実施方法を確認**
- [`07-security-compliance/security-checklist.md`](../../07-security-compliance/security-checklist.md)
  - **セキュリティチェックリストを確認**
- [`07-security-compliance/vulnerability-management.md`](../../07-security-compliance/vulnerability-management.md)
  - **脆弱性管理の方法を確認**

**実施するテスト（該当する場合）**:
- **パフォーマンステスト**: API/重要機能のレスポンスタイム
- **負荷テスト**: 想定負荷時のシステム動作
- **セキュリティスキャン**: OWASP ZAP、Snyk等
- **依存関係チェック**: npm audit、OWASP Dependency Check等

---


## 4.8 ドキュメントコメントレビュー基準（全言語共通） ⭐NEW

### 4.8.1 自動チェック（必須・却下基準）

#### TypeScript/JavaScript
```bash
npm run lint:jsdoc          # JSDocチェック
npm run docs:generate       # TypeDoc生成
```

**却下基準**:
- ❌ JSDoc警告が1件でもある
- ❌ TypeDoc生成が失敗する

---

#### Python
```bash
pylint --enable=missing-docstring src/    # Docstringチェック
pydocstyle src/                           # スタイルチェック
```

**却下基準**:
- ❌ Docstring警告が1件でもある
- ❌ Google Styleに準拠していない

---

#### Java
```bash
mvn checkstyle:check        # Javadocチェック
mvn javadoc:javadoc         # Javadoc生成
```

**却下基準**:
- ❌ Javadoc警告が1件でもある
- ❌ Javadoc生成が失敗する

---

### 4.8.2 手動レビュー基準（言語非依存）

#### 🔴 Level 1: 必須項目（却下基準）

**ファイルレベル**:
- [ ] すべてのファイルにヘッダーコメントがある
- [ ] ファイルの目的が明確に記述されている

**クラス・インターフェースレベル**:
- [ ] すべてのパブリッククラスにコメントがある
- [ ] クラスの責任が明確に記述されている

**関数・メソッドレベル**:
- [ ] すべてのパブリック関数にコメントがある
- [ ] パラメータ、戻り値、例外が記述されている

---

#### 🟡 Level 2: 品質項目（改善要求）

**コメントの質**:
- [ ] "Why"（なぜ）が説明されている
- [ ] ビジネスルール・制約が明記されている
- [ ] 複雑なロジックにコメントがある
- [ ] セキュリティ考慮事項が記載されている

---

### 4.8.3 カバレッジチェック（言語別）

#### TypeScript/JavaScript
```bash
npm run docs:coverage
```

#### Python
```bash
interrogate -v src/  # Docstringカバレッジ
```

#### Java
```bash
mvn javadoc:javadoc -Dshow=private
```

**基準**:
- パブリックAPI: **100%** 🔴必須
- 複雑なロジック: **80%以上** 🟡推奨

---

### 4.8.4 レビューコメントテンプレート（言語非依存）

```markdown
## ドキュメントコメント不足（却下）

以下のファイルでドキュメントコメントが不足しています:

**TypeScript**:
- `src/auth/services/auth.service.ts`
  - ❌ Line 45: `login()`に`@throws`が不足
  - ❌ Line 78: `generateTokens()`にJSDocがない

**Python**:
- `src/auth/services/auth_service.py`
  - ❌ Line 23: `authenticate()`にDocstringがない
  - ❌ Line 45: `Raises:`セクションが不足

**Java**:
- `src/auth/services/AuthService.java`
  - ❌ Line 34: `login()`に`@throws`が不足
  - ❌ Line 67: `generateTokens()`にJavadocがない

**修正方法**:
1. 言語別テンプレートを参照: `/08-templates/code-templates/[言語]/`
2. ドキュメントコメントを追加
3. 自動チェックで確認
4. 再提出

**参照ドキュメント**:
- `/00-guides/ai-guides/AI-DOCUMENTATION-COMMENT-CHECKLIST.md`
```

---

### 4.8.5 コメント品質レビューガイドライン

#### ❌ 低品質コメントの例

**TypeScript**:
```typescript
/**
 * ユーザーを取得する
 * @param id - ID
 * @returns ユーザー
 */
```

**問題点**:
- "What"のみで"Why"がない
- パラメータの詳細不足
- 戻り値の詳細不足

---

#### ✅ 高品質コメントの例

**TypeScript**:
```typescript
/**
 * ユーザーをIDで取得する
 * 
 * キャッシュ機構により頻繁なアクセスでもパフォーマンスを維持。
 * 存在しない場合はnullを返し、エラーをスローしない設計（呼び出し側でハンドリング）。
 * 
 * @param id - ユーザーの一意識別子（UUID v4形式）
 * @returns ユーザーオブジェクト、または見つからない場合はnull
 * 
 * @example
 * ```typescript
 * const user = await getUserById('123e4567-e89b-12d3-a456-426614174000');
 * if (user) {
 *   console.log(user.name);
 * }
 * ```
 */
```

**優れている点**:
- ビジネスロジック（キャッシュ）を説明
- 設計判断（エラーをスローしない）の理由
- パラメータの形式を明記
- 戻り値の詳細説明
- 使用例を記載

---

### 4.8.6 レビュー手順

```
[Phase 4開始]
  ↓
1. 自動チェック実行（5分）
   - 言語別Linter実行
   - ドキュメント生成テスト
   - カバレッジ確認
  ↓
2. 自動チェック結果確認
   ├─ NG → 却下（Phase 3に差し戻し）
   └─ OK → 手動レビューへ
  ↓
3. 手動レビュー（30-60分）
   - ファイルヘッダー確認
   - クラスコメント確認
   - 関数コメント確認
   - コメント品質確認（Why重視）
  ↓
4. レビューコメント作成
   - テンプレートを使用
   - 具体的な改善案を提示
  ↓
5. 承認/却下/改善要求の判断
```

---

### 4.8.7 言語別チェックリスト

#### TypeScript/JavaScript チェック項目
- [ ] `@fileoverview`が全ファイルにある
- [ ] すべてのエクスポートクラスに`@class`がある
- [ ] すべてのエクスポート関数に`@param`, `@returns`, `@throws`がある
- [ ] `eslint-plugin-jsdoc`のルールに違反していない
- [ ] TypeDocが正常に生成できる

#### Python チェック項目
- [ ] モジュールDocstringが全ファイルにある
- [ ] すべてのパブリッククラスにDocstringがある
- [ ] すべてのパブリック関数に`Args:`, `Returns:`, `Raises:`がある
- [ ] Google Styleに準拠している
- [ ] pylint, pydocstyleに違反していない

#### Java チェック項目
- [ ] パッケージJavadocが存在する（package-info.java）
- [ ] すべてのパブリッククラスにJavadocがある
- [ ] すべてのパブリックメソッドに`@param`, `@return`, `@throws`がある
- [ ] Checkstyleに違反していない
- [ ] Javadocが正常に生成できる

---

### 4.8.8 却下基準のまとめ

以下のいずれかに該当する場合、Phase 4を却下し、Phase 3に差し戻します:

**自動チェック違反** 🔴:
- [ ] 言語別Linterでエラー・警告が1件でもある
- [ ] ドキュメント生成ツールが失敗する
- [ ] パブリックAPIのカバレッジが100%未満

**手動レビュー違反** 🔴:
- [ ] ファイルヘッダーコメントが不足
- [ ] パブリッククラスにコメントが不足
- [ ] パブリック関数にコメントが不足
- [ ] パラメータ・戻り値・例外の説明が不足

**品質基準未達** 🟡:
- [ ] "Why"の説明が不足（改善要求）
- [ ] ビジネスロジックの説明が不足（改善要求）
- [ ] 複雑なロジックへのコメントが不足（改善要求）

---

### 4.8.9 参照ドキュメント

- 🔴 必須: `/00-guides/ai-guides/AI-DOCUMENTATION-COMMENT-CHECKLIST.md`
- 🔴 必須: `/00-guides/phase-guides/phase-3-implementation-guide.md` - セクション3.7
- 🔴 必須: `/08-templates/code-templates/` - 言語別テンプレート
- 🟡 推奨: `/01-coding-standards/[言語]-standards.md` - セクションX
- 🟡 推奨: `/03-development-process/documentation-standards.md` - セクション7

---

## 📚 不具合管理（継続的）

### 不具合が見つかった場合

**🔴 必須参照**:
- [`04-quality-standards/defect-management.md`](../../04-quality-standards/defect-management.md)
  - **このドキュメントの記載内容に従って不具合を管理**
  - **優先度付け、記録、追跡の方法を確認**

**不具合管理のワークフロー**:
1. **不具合の記録**: JIRA等に記録
2. **優先度付け**: Critical / High / Medium / Low
3. **修正**: Phase 3に戻って修正実施
4. **再テスト**: 修正箇所のテスト実施
5. **回帰テスト**: 影響範囲の確認

---

## 📚 参照ドキュメント一覧

### コアドキュメント（Phase 4で頻繁に参照）

| ドキュメント | 優先度 | 用途 |
|------------|-------|------|
| `03-development-process/code-review-standards.md` | 🔴 必須 | コードレビュー |
| `03-development-process/testing-standards.md` | 🔴 必須 | テスト標準 |
| `04-quality-standards/testing-strategy.md` | 🔴 必須 | テスト戦略 |
| `04-quality-standards/unit-testing.md` | 🔴 必須 | ユニットテスト |
| `04-quality-standards/integration-testing.md` | 🔴 必須 | 統合テスト |
| `04-quality-standards/e2e-testing.md` | 🔴 必須 | E2Eテスト |
| `04-quality-standards/code-quality-standards.md` | 🔴 必須 | コード品質 |
| `04-quality-standards/defect-management.md` | 🔴 必須 | 不具合管理 |
| `04-quality-standards/performance-testing.md` | 🟡 推奨 | パフォーマンステスト |
| `04-quality-standards/security-testing.md` | 🟡 推奨 | セキュリティテスト |

---

## 🔍 実践的な活用方法

### 基本的なワークフロー

1. **コードレビューから開始**
   - 必須: コードレビュー標準、コーディング標準
   
2. **テスト戦略を確認してテスト実施**
   - 必須: テスト戦略、ユニット・統合・E2Eテスト標準
   - 推奨: テストデータ管理
   
3. **品質チェックを実施**
   - 必須: コード品質標準、品質メトリクス
   
4. **必要に応じて追加テスト**
   - 推奨: パフォーマンス・セキュリティテスト

### プロジェクトタイプ別のアプローチ

**新規プロジェクト**:
- **全ステップを実施** (4-6時間)
- カバレッジ目標達成を重視
- パフォーマンス・セキュリティテストを実施

**既存への機能追加**:
- **影響範囲のテストを重点実施** (2-4時間)
- 回帰テストを必ず実施
- 新規機能のカバレッジ目標達成

**バグ修正**:
- **修正箇所のテスト追加** (1-2時間)
- 回帰テストで副作用がないことを確認

**リファクタリング**:
- **既存テストの実行** (1-2時間)
- すべてのテストがパスすることを確認
- 必要に応じてテスト追加

### テストカバレッジ目標

**推奨カバレッジ**:
- **ユニットテスト**: 80%以上
- **統合テスト**: 主要パスを網羅
- **E2Eテスト**: クリティカルなユーザーフローを網羅

---

## ⚠️ 重要な原則

### ドキュメント参照の原則
- **参照ドキュメントの記載内容が最優先**
- 本ガイドは参照先への案内役
- 疑問点は該当ドキュメントを直接確認

### テストの原則
- **テストファースト**: テストを最初に書く（TDD）または実装と並行
- **自動化**: 可能な限り自動化
- **独立性**: テスト間の依存関係を避ける
- **明確性**: テストの意図が明確であること
- **保守性**: テストコードも保守しやすく

### 完了基準
Phase 4は以下を満たしたら完了：
- ✅ コードレビューが完了し、指摘事項が対応済み
- ✅ すべてのテストがパスしている
- ✅ カバレッジ目標を達成している
- ✅ コード品質チェックがパスしている
- ✅ 不具合が修正されている（または次のイテレーションで対応予定）
- ✅ パフォーマンス・セキュリティテストがパスしている（該当する場合）

**次のフェーズへ**: Phase 5（デプロイメント）に進む前に、すべてのテストがパスしていることを確認

---

## 🔄 ガイドの更新

- **最終更新日**: 2025-11-05
- **バージョン**: 1.0.0（軽量ナビゲーション型）
- **対象**: devin-organization-standards 全カテゴリ

---

## まとめ

Phase 4は**品質を保証する重要なフェーズ**です。

- **目的**: コードレビュー、テスト、品質チェックを実施し、リリース可能な品質を達成する
- **成果物**: レビューレポート、テストコード、テスト実行結果、品質メトリクスレポート
- **所要時間**: 新規プロジェクトで4-6時間、機能追加で2-4時間、バグ修正で1-2時間

**最も重要な原則**: 
- 各ドキュメントの記載内容を確認し、その内容に従ってレビュー・テストを実施してください
- テストカバレッジ目標を達成してください
- すべてのテストがパスするまでPhase 5に進まないでください
- 不具合は適切に記録・管理してください

疑問や矛盾がある場合は、より具体的で新しいドキュメントを優先してください。

---

## 📋 関連チェックリスト

Phase 4を開始・完了する際は、以下のチェックリストを使用してください：

### Phase 4開始前
- [Phase開始前チェックリスト](../../09-reference/checklists/phase-pre-work-checklist.md)

### Phase 4実施中
- [AIドキュメントコメントチェックリスト](../../09-reference/checklists/ai-documentation-comment-checklist.md)

### Phase 4完了時
- [Phase 4 完了チェックリスト](../../09-reference/checklists/phase-4-completion-checklist.md)

---
