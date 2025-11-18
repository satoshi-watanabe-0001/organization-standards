# コード品質標準
## Code Quality Standards

**最終更新日**: 2025-10-27  
**バージョン**: 1.0.0  
**対象**: 全開発者・QAエンジニア・自律型AI Devin  
**適用範囲**: 全プロジェクト共通コード品質基準

---

## 📖 概要

このドキュメントは、組織全体で統一されたコード品質基準を定義し、保守性が高く信頼性のあるソフトウェアの開発を支援します。自律型AI Devinが品質基準を満たすコードを生成できるよう、明確で測定可能なガイドラインを提供します。

### 🎯 目的

- **品質の定量化**: 測定可能な品質指標による客観的評価
- **保守性の向上**: 長期的に維持しやすいコードベースの実現
- **技術的負債の削減**: 早期の品質問題検出と是正
- **開発効率の向上**: 一貫した品質基準による開発速度の維持

---

## 📂 目次

1. [コード品質メトリクス](#1-コード品質メトリクス)
2. [静的コード解析](#2-静的コード解析)
3. [コード複雑度管理](#3-コード複雑度管理)
4. [コードスメルと対処方法](#4-コードスメルと対処方法)
5. [コードレビュー品質基準](#5-コードレビュー品質基準)
6. [品質ゲート](#6-品質ゲート)
7. [技術的負債の測定](#7-技術的負債の測定)
8. [自動化とCI/CD統合](#8-自動化とcicd統合)
9. [言語別品質ツール](#9-言語別品質ツール)
10. [Devin AIガイドライン](#10-devin-aiガイドライン)

---

## 1. コード品質メトリクス

### 1.1 基本メトリクス

#### **コード行数メトリクス**

**関数/メソッドサイズ**:
```yaml
理想:     20行以内
推奨上限:  50行
警告:     50-100行（分割を検討）
違反:     100行超（必ず分割）
```

**クラス/ファイルサイズ**:
```yaml
理想:     200行以内
推奨上限:  400行
警告:     400-1000行（リファクタリング検討）
違反:     1000行超（必ず分割）
```

**例外条件**:
- 設定ファイル・データ定義
- 自動生成コード
- ドメインモデル（多数のフィールドを持つエンティティ）
- レガシーコードの段階的リファクタリング中

#### **コメント密度**

**基準**:
```yaml
コメント率: 10-30%（コード行数に対するコメント行数の比率）
理想:      15-20%
過剰:      40%超（過剰コメントは自己文書化コードへの変更を検討）
不足:      5%未満（複雑なロジックへのコメント不足）
```

**推奨するコメント**:
- なぜそうするのか（Why）の説明
- 複雑なアルゴリズムの意図
- ビジネスルールの根拠
- 非自明な実装の理由
- TODO、FIXME、HACK等の技術的負債マーカー

**避けるべきコメント**:
- コードを読めば分かる内容（What）
- 古くなったコメント
- コメントアウトされたコード（バージョン管理に任せる）

---

### 1.2 コード重複率（Duplication）

**基準**:
```yaml
理想:     3%以下
推奨上限:  5%
警告:     5-10%（重複排除を検討）
違反:     10%超（即座にリファクタリング）
```

**測定方法**:
- 6行以上の連続した重複コード
- トークンベースまたは構文ベースの重複検出
- ツール: SonarQube、PMD Copy/Paste Detector、jscpd

**DRY原則の適用**:
```
重複発見 → 評価 → 抽象化 → 共通化
     ↓
3回ルール: 3回出現したら抽象化を検討
```

**注意事項**:
- 表面的な重複と本質的な重複を区別
- 無理な抽象化は避ける（単なる偶然の一致の可能性）

---

### 1.3 保守性指数（Maintainability Index）

**定義**:
保守性指数は、コードの保守しやすさを0-100のスコアで表す総合指標

**計算式**:
```
MI = max(0, (171 - 5.2 * ln(HV) - 0.23 * CC - 16.2 * ln(LOC)) * 100 / 171)

HV: Halstead Volume（コードの情報量）
CC: Cyclomatic Complexity（循環的複雑度）
LOC: Lines of Code（コード行数）
```

**評価基準**:
```yaml
優秀:    85-100（緑）  - 非常に保守しやすい
良好:    65-84（黄）   - 適度に保守しやすい
注意:    40-64（橙）   - 保守が困難、改善推奨
問題:    0-39（赤）    - 非常に保守困難、要リファクタリング
```

**ツール**:
- Visual Studio（C#、VB.NET）
- SonarQube（多言語対応）
- Radon（Python）
- ESLint + complexity plugin（JavaScript/TypeScript）

---

## 2. 静的コード解析

### 2.1 静的解析ツールの必須導入

#### **言語別リンター**

**Python**:
```yaml
必須ツール:
  - pylint:      総合的な静的解析
  - flake8:      PEP8準拠チェック
  - mypy:        型チェック
  - black:       自動フォーマッター
  - isort:       import文の整理

設定:
  pylint-score:  8.0以上
  flake8-max-line-length: 100
  mypy-strict:   True（段階的に適用）
```

**TypeScript/JavaScript**:
```yaml
必須ツール:
  - ESLint:      構文・スタイルチェック
  - TypeScript:  型チェック（厳密モード）
  - Prettier:    自動フォーマッター
  - tsc:         型エラー検出

設定:
  eslint-rules:  airbnb-base または standard
  typescript-strict: true
  no-any:        warn（段階的にerrorへ）
```

**Java**:
```yaml
必須ツール:
  - Checkstyle:  コーディング規約チェック
  - PMD:         コード品質チェック
  - SpotBugs:    バグパターン検出
  - SonarQube:   総合的な品質分析

設定:
  checkstyle:    Google Style または Sun Style
  pmd-ruleset:   category/java/bestpractices.xml
  spotbugs-effort: max
```

**SQL**:
```yaml
必須ツール:
  - SQLFluff:    SQL構文チェック
  - pg_format:   PostgreSQL フォーマッター
  - sqlcheck:    アンチパターン検出

設定:
  sqlfluff-dialect: postgres/mysql/etc
  max-line-length: 100
```

---

### 2.2 静的解析の品質基準

#### **違反の重要度分類**

**Blocker（即座に修正必須）**:
- セキュリティ脆弱性（SQLインジェクション、XSS等）
- メモリリーク、リソースリーク
- デッドロックの可能性
- Null参照エラー

**Critical（リリース前に修正必須）**:
- 例外処理の欠如
- リソースのクローズ漏れ
- 型安全性の欠如
- API契約違反

**Major（修正推奨）**:
- コードスメル（長大な関数、複雑な条件分岐）
- パフォーマンス問題の可能性
- 推奨パターンからの逸脱
- ドキュメント不足

**Minor（改善推奨）**:
- 命名規則違反
- フォーマット不整合
- 使用されていない変数
- 冗長なコード

**Info（情報提供）**:
- TODO/FIXMEコメント
- 複雑度の通知
- 改善提案

#### **品質ゲート基準**

```yaml
コミット前（Pre-commit）:
  - Blocker violations:    0件
  - Critical violations:   0件
  - Major violations:      許容範囲内（新規追加なし）
  
プルリクエスト時:
  - Blocker violations:    0件
  - Critical violations:   0件
  - Major violations:      前回比で増加なし
  - Technical Debt Ratio:  5.0%以下
  
リリース前:
  - Blocker violations:    0件
  - Critical violations:   0件
  - Major violations:      10件以下/1000行
  - Code Coverage:         80%以上
  - Maintainability Index: 65以上
```

---

## 3. コード複雑度管理

### 3.1 循環的複雑度（Cyclomatic Complexity）

#### **定義**
コード内の独立した実行パスの数を表す指標

**計算式**:
```
CC = E - N + 2P

E: エッジ数（制御フローの矢印）
N: ノード数（実行ステートメント）
P: 接続されたコンポーネント数（通常1）
```

**簡易計算**:
```
CC = 1 + 分岐点の数

分岐点:
- if/else
- for/while/do-while
- case文
- &&、||
- try/catch
- 三項演算子
```

#### **品質基準**

```yaml
優秀:    1-5   （緑）  - シンプルで理解しやすい
良好:    6-10  （黄）  - 適度な複雑さ
注意:    11-15 （橙）  - 複雑、分割を検討
問題:    16-20 （赤）  - 非常に複雑、要リファクタリング
危険:    21+   （濃赤）- テスト困難、即座にリファクタリング
```

**Devin指示**:
```
関数の循環的複雑度を10以下に保つこと。
15を超える場合は以下の手法で分割せよ：
1. 条件分岐の抽出（Extract Method）
2. ポリモーフィズムによる分岐の置き換え
3. 戦略パターンの適用
4. ガード節による早期リターン
```

#### **例: リファクタリング前後**

**リファクタリング前（CC = 8）**:
```python
def calculate_discount(customer, amount):
    if customer.is_premium:
        if amount > 1000:
            discount = 0.20
        elif amount > 500:
            discount = 0.15
        else:
            discount = 0.10
    else:
        if amount > 1000:
            discount = 0.10
        elif amount > 500:
            discount = 0.05
        else:
            discount = 0
    return amount * (1 - discount)
```

**リファクタリング後（CC = 2 + 2 + 2 = 6）**:
```python
def calculate_discount(customer, amount):
    discount_rate = get_discount_rate(customer, amount)
    return apply_discount(amount, discount_rate)

def get_discount_rate(customer, amount):
    if customer.is_premium:
        return get_premium_discount_rate(amount)
    else:
        return get_standard_discount_rate(amount)

def get_premium_discount_rate(amount):
    if amount > 1000:
        return 0.20
    elif amount > 500:
        return 0.15
    else:
        return 0.10

def get_standard_discount_rate(amount):
    if amount > 1000:
        return 0.10
    elif amount > 500:
        return 0.05
    else:
        return 0.0

def apply_discount(amount, rate):
    return amount * (1 - rate)
```

---

### 3.2 認知的複雑度（Cognitive Complexity）

#### **定義**
人間がコードを理解するのにかかる認知的負荷を表す指標

**循環的複雑度との違い**:
- ネストの深さを重視
- 線形的な構造は複雑度を増やさない
- 読みやすさに焦点

**計算ルール**:
```
+1: if, else if, else, switch, for, while, do-while, catch
+1: 論理演算子の連鎖（&&、||）
+1: ネストごとの追加ペナルティ
+0: else（直前のifと同レベルのため）
```

#### **品質基準**

```yaml
優秀:    0-5   （緑）  - 非常に読みやすい
良好:    6-10  （黄）  - 読みやすい
注意:    11-15 （橙）  - やや複雑、改善推奨
問題:    16-25 （赤）  - 読みにくい、要改善
危険:    26+   （濃赤）- 非常に読みにくい、即座にリファクタリング
```

**測定ツール**:
- SonarQube（多言語対応）
- ESLint plugin: complexity（JavaScript/TypeScript）
- radon（Python）

---

### 3.3 ネスト深度（Nesting Depth）

#### **品質基準**

```yaml
理想:     2レベル以内
推奨上限:  3レベル
警告:     4レベル（早期リターンで改善）
違反:     5レベル以上（即座にリファクタリング）
```

#### **改善手法**

**1. 早期リターン（Guard Clauses）**:

**改善前（ネスト4レベル）**:
```python
def process_order(order):
    if order is not None:
        if order.is_valid():
            if order.items:
                if order.total_amount > 0:
                    # 処理
                    return True
    return False
```

**改善後（ネスト1レベル）**:
```python
def process_order(order):
    if order is None:
        return False
    if not order.is_valid():
        return False
    if not order.items:
        return False
    if order.total_amount <= 0:
        return False
    
    # 処理
    return True
```

**2. 関数抽出**:

**改善前**:
```javascript
function processData(data) {
  if (data) {
    for (let item of data) {
      if (item.isActive) {
        if (item.value > 0) {
          // 複雑な処理
        }
      }
    }
  }
}
```

**改善後**:
```javascript
function processData(data) {
  if (!data) return;
  
  data.filter(isValidItem)
      .forEach(processItem);
}

function isValidItem(item) {
  return item.isActive && item.value > 0;
}

function processItem(item) {
  // 複雑な処理
}
```

---

## 4. コードスメルと対処方法

### 4.1 主要なコードスメル

#### **Long Method（長大なメソッド）**

**兆候**:
- 50行以上のメソッド
- 複数の責任を持つ
- スクロールしないと全体が見えない

**対処方法**:
```
1. Extract Method: サブメソッドへの抽出
2. 単一責任原則の適用
3. ヘルパーメソッドの作成
```

**例**:
```python
# Before
def generate_report(data):
    # データ検証（10行）
    # データ変換（20行）
    # レポート生成（30行）
    # ファイル保存（15行）
    pass  # 合計75行

# After
def generate_report(data):
    validated_data = validate_data(data)
    transformed_data = transform_data(validated_data)
    report = create_report(transformed_data)
    save_report(report)
```

---

#### **Large Class（巨大なクラス）**

**兆候**:
- 400行以上のクラス
- 多数のフィールドとメソッド
- 複数の責任を持つ

**対処方法**:
```
1. Extract Class: 責任ごとにクラスを分割
2. Extract Subclass: 特殊化した機能をサブクラスへ
3. Extract Interface: 共通インターフェースの抽出
```

---

#### **Duplicated Code（重複コード）**

**兆候**:
- 同じコードブロックが複数箇所に存在
- わずかな違いのみの類似コード

**対処方法**:
```
1. Extract Method: 共通メソッドへの抽出
2. Pull Up Method: 親クラスへの引き上げ
3. Template Method: テンプレートメソッドパターン
4. Strategy Pattern: 戦略パターンの適用
```

---

#### **Dead Code（使用されないコード）**

**兆候**:
- 呼び出されない関数
- 使用されない変数
- 実行されない条件分岐

**対処方法**:
```
1. 即座に削除（バージョン管理があるため復元可能）
2. 削除前に検索で使用箇所を確認
3. IDE/ツールの未使用コード検出機能を活用
```

**検出ツール**:
- ESLint: no-unused-vars
- Pylint: unused-variable, unused-argument
- IntelliJ IDEA: Code Inspection
- SonarQube: Dead Code Detection

---

#### **Magic Numbers（マジックナンバー）**

**兆候**:
- コード中に意味不明な数値リテラル
- 何を表すのか分からない定数

**対処方法**:
```
1. 名前付き定数への置き換え
2. Enumの使用
3. 設定ファイルへの外部化
```

**例**:
```typescript
// Before
if (user.status === 3) {
  // ...
}

// After
const UserStatus = {
  ACTIVE: 1,
  INACTIVE: 2,
  SUSPENDED: 3,
  DELETED: 4
} as const;

if (user.status === UserStatus.SUSPENDED) {
  // ...
}
```

---

#### **God Object（神オブジェクト）**

**兆候**:
- すべての責任を持つ巨大なクラス
- 多数の依存関係
- システムの中心的存在

**対処方法**:
```
1. 責任の分割（Single Responsibility Principle）
2. Facade Patternの適用
3. ドメイン駆動設計（DDD）の適用
```

---

### 4.2 コードスメル検出の自動化

#### **SonarQubeルール**

```yaml
Code Smells - Blocker:
  - 関数の複雑度 > 15
  - クラスの複雑度 > 80
  - 重複コード > 10%
  
Code Smells - Critical:
  - 関数の行数 > 100
  - クラスの行数 > 1000
  - 深いネスト > 4レベル
  
Code Smells - Major:
  - 関数の行数 > 50
  - クラスの行数 > 500
  - 長いパラメータリスト > 7個
  - マジックナンバー
```

---

## 5. コードレビュー品質基準

### 5.1 レビューチェックリスト

#### **機能性（Functionality）**
- [ ] 要件を満たしているか
- [ ] エッジケースが考慮されているか
- [ ] エラーハンドリングが適切か
- [ ] バリデーションが実装されているか

#### **コード品質（Code Quality）**
- [ ] 命名が明確で一貫しているか
- [ ] 関数/メソッドが50行以内か
- [ ] 循環的複雑度が10以下か
- [ ] DRY原則が守られているか
- [ ] SOLID原則が適用されているか

#### **テスト（Testing）**
- [ ] ユニットテストが書かれているか
- [ ] テストカバレッジが80%以上か
- [ ] エッジケースのテストがあるか
- [ ] テストが独立しているか

#### **セキュリティ（Security）**
- [ ] 入力値の検証があるか
- [ ] SQLインジェクション対策があるか
- [ ] XSS対策があるか
- [ ] 認証・認可が適切か
- [ ] 機密情報がハードコードされていないか

#### **パフォーマンス（Performance）**
- [ ] N+1問題がないか
- [ ] 不要なループがないか
- [ ] 適切なデータ構造が使用されているか
- [ ] メモリリークの可能性がないか

#### **保守性（Maintainability）**
- [ ] コメントが適切か（Why、特にWhat）
- [ ] ドキュメントが更新されているか
- [ ] 技術的負債が追加されていないか
- [ ] 後続の開発者が理解できるか

---

### 5.2 レビューの品質基準

#### **レビュー時間**

```yaml
小規模変更（< 100行）:
  推奨時間: 10-20分
  最大時間: 30分

中規模変更（100-500行）:
  推奨時間: 30-60分
  最大時間: 90分

大規模変更（> 500行）:
  対応: 分割を推奨、または複数レビュアー
```

**原則**: 1回のレビューで400行以下が理想

#### **レビューコメントの品質**

**良いコメント**:
```
具体的: 「この関数は複雑すぎます」→「この関数の循環的複雑度が15です。10以下に分割してください」
建設的: 「ダメです」→「〇〇の理由で△△に変更してはどうでしょうか」
理由明記: 「変更してください」→「パフォーマンスのため、この部分をキャッシュしてください」
```

**避けるべきコメント**:
- 曖昧な指摘
- 感情的な表現
- スタイルのみの指摘（自動フォーマッターに任せる）

---

## 6. 品質ゲート

### 6.1 コミット前品質ゲート（Pre-commit）

**必須チェック**:
```yaml
Static Analysis:
  - Linter violations:     0 errors
  - Type errors:           0 errors
  - Security issues:       0 critical/high
  
Formatting:
  - Code formatting:       100% compliant
  - Import sorting:        Organized
  
Testing:
  - Unit tests:            All passing
  - Changed files:         Tests included
```

**ツール**: pre-commit hooks, husky, lint-staged

---

### 6.2 プルリクエスト品質ゲート

**必須基準**:
```yaml
Code Quality:
  - Blocker violations:         0
  - Critical violations:        0
  - Major violations:           新規追加なし
  - Code duplication:           < 5%
  - Cyclomatic complexity:      < 10 per function
  - Cognitive complexity:       < 15 per function
  
Testing:
  - Code coverage:              80%以上
  - Coverage decrease:          許容なし
  - All tests passing:          必須
  
Security:
  - Security vulnerabilities:   0 high/critical
  - Dependency vulnerabilities: 0 high/critical
  
Documentation:
  - README updated:             必要に応じて
  - API docs updated:           API変更時必須
```

---

### 6.3 リリース前品質ゲート

**必須基準**:
```yaml
Overall Quality:
  - Maintainability Index:      > 65
  - Technical Debt Ratio:       < 5%
  - Code Smells:                < 10/1000 LOC
  
Testing:
  - Code coverage:              > 80%
  - Integration tests:          100% passing
  - E2E tests:                  Critical paths passing
  
Performance:
  - Performance degradation:    < 10%
  - Memory leaks:               None detected
  
Security:
  - Security scan:              No vulnerabilities
  - Dependency scan:            All up-to-date
```

---

## 7. 技術的負債の測定

### 7.1 技術的負債比率（Technical Debt Ratio）

**定義**:
```
TDR = 技術的負債の修正コスト / 開発コスト × 100%

技術的負債の修正コスト = 違反の重要度 × 標準修正時間

SonarQubeのデフォルト修正時間:
- Blocker:   1日
- Critical:  1時間
- Major:     30分
- Minor:     10分
- Info:      5分
```

**品質基準**:
```yaml
優秀:    < 5%   （A）
良好:    5-10%  （B）
注意:    10-20% （C）
問題:    20-50% （D）
危険:    > 50%  （E）
```

**管理方針**:
```
- 新規コード: TDR < 3% を維持
- 既存コード: 段階的に改善（月次で1%削減目標）
- レガシーコード: 変更時にリファクタリング
```

---

### 7.2 技術的負債の可視化

#### **ダッシュボード指標**

```yaml
全体指標:
  - Total Technical Debt:    〇〇日分
  - Technical Debt Ratio:    ✓✓%
  - Debt per Developer:      △△日/人
  
トレンド:
  - Debt Added (今月):       +XX日
  - Debt Removed (今月):     -YY日
  - Net Change:              ±ZZ日
  
品質分布:
  - Blocker issues:          0件（目標）
  - Critical issues:         〇件
  - Major issues:            ✓件
  - Maintainability Index:   XX点
```

---

## 8. 自動化とCI/CD統合

### 8.1 CI/CDパイプラインの品質チェック

#### **パイプライン構成**

```yaml
stages:
  - lint          # 静的解析
  - test          # ユニットテスト
  - quality       # 品質ゲート
  - security      # セキュリティスキャン
  - build         # ビルド
  - integration   # 統合テスト
  - deploy        # デプロイ

jobs:
  lint:
    - ESLint/Pylint/Checkstyle実行
    - フォーマットチェック
    - 型チェック（TypeScript/mypy）
    
  quality-gate:
    - SonarQube分析
    - カバレッジチェック（80%以上）
    - 複雑度チェック
    - 重複コードチェック
    - 技術的負債評価
    
  security-scan:
    - SAST（Static Application Security Testing）
    - 依存関係脆弱性スキャン（Snyk/Dependabot）
    - シークレットスキャン（gitleaks）
```

#### **品質ゲートの失敗条件**

```yaml
Pipeline Failure Conditions:
  - Blocker violations:      > 0
  - Critical violations:     > 0
  - Code coverage:           < 80%
  - Coverage decrease:       > 1%
  - Duplicated lines:        > 5%
  - Security vulnerabilities: > 0 (high/critical)
  - Technical Debt Ratio:    > 5%
```

---

### 8.2 自動修正ツール

#### **フォーマッター（自動整形）**

```yaml
Python:
  - black:       コード整形
  - isort:       import整理
  - autopep8:    PEP8準拠

TypeScript/JavaScript:
  - Prettier:    コード整形
  - ESLint --fix: 自動修正可能なルール

Java:
  - Google Java Format
  - Spotless

SQL:
  - SQLFluff --fix
  - pg_format
```

**使用方法**:
```bash
# Pre-commit hookで自動実行
$ pre-commit install

# または手動実行
$ npm run format
$ black .
$ mvn spotless:apply
```

---

## 9. 言語別品質ツール

### 9.1 Python

**必須ツールセット**:
```yaml
静的解析:
  - pylint:          総合的静的解析
  - flake8:          スタイルチェック
  - mypy:            型チェック
  - bandit:          セキュリティチェック
  
フォーマット:
  - black:           コード整形
  - isort:           import整理
  
品質測定:
  - radon:           複雑度測定
  - coverage:        カバレッジ測定
  - pytest:          テスト実行
```

**設定例（pyproject.toml）**:
```toml
[tool.pylint.messages_control]
max-line-length = 100
min-similarity-lines = 4
max-args = 7
max-locals = 15
max-returns = 6
max-branches = 12
max-statements = 50

[tool.mypy]
strict = true
warn_return_any = true
warn_unused_configs = true

[tool.black]
line-length = 100
target-version = ['py310']

[tool.coverage.run]
branch = true
omit = ["*/tests/*", "*/migrations/*"]

[tool.coverage.report]
fail_under = 80
```

---

### 9.2 TypeScript/JavaScript

**必須ツールセット**:
```yaml
静的解析:
  - ESLint:          構文チェック
  - TypeScript:      型チェック
  - SonarQube:       品質分析
  
フォーマット:
  - Prettier:        コード整形
  
品質測定:
  - Jest:            テスト実行
  - Istanbul/nyc:    カバレッジ測定
  - complexity-report: 複雑度測定
```

**設定例（.eslintrc.json）**:
```json
{
  "extends": [
    "eslint:recommended",
    "plugin:@typescript-eslint/recommended",
    "plugin:@typescript-eslint/recommended-requiring-type-checking",
    "prettier"
  ],
  "plugins": ["@typescript-eslint", "complexity"],
  "rules": {
    "@typescript-eslint/no-explicit-any": "error",
    "@typescript-eslint/explicit-function-return-type": "warn",
    "complexity": ["error", 10],
    "max-lines": ["warn", 400],
    "max-lines-per-function": ["warn", 50],
    "max-depth": ["error", 4],
    "max-params": ["warn", 5]
  }
}
```

**tsconfig.json（厳密モード）**:
```json
{
  "compilerOptions": {
    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true,
    "strictFunctionTypes": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noImplicitReturns": true,
    "noFallthroughCasesInSwitch": true
  }
}
```

---

### 9.3 Java

**必須ツールセット**:
```yaml
静的解析:
  - Checkstyle:      スタイルチェック
  - PMD:             コード品質
  - SpotBugs:        バグ検出
  - SonarQube:       総合品質
  
フォーマット:
  - Google Java Format
  - Spotless Maven Plugin
  
品質測定:
  - JUnit 5:         テスト実行
  - JaCoCo:          カバレッジ測定
```

**設定例（pom.xml）**:
```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-checkstyle-plugin</artifactId>
  <version>3.3.0</version>
  <configuration>
    <configLocation>google_checks.xml</configLocation>
    <violationSeverity>warning</violationSeverity>
    <failOnViolation>true</failOnViolation>
  </configuration>
</plugin>

<plugin>
  <groupId>com.github.spotbugs</groupId>
  <artifactId>spotbugs-maven-plugin</artifactId>
  <version>4.7.3.5</version>
  <configuration>
    <effort>Max</effort>
    <threshold>Low</threshold>
  </configuration>
</plugin>

<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.10</version>
  <configuration>
    <rules>
      <rule>
        <element>BUNDLE</element>
        <limits>
          <limit>
            <counter>LINE</counter>
            <value>COVEREDRATIO</value>
            <minimum>0.80</minimum>
          </limit>
        </limits>
      </rule>
    </rules>
  </configuration>
</plugin>
```

---

### 9.4 SQL

**必須ツールセット**:
```yaml
静的解析:
  - SQLFluff:        SQL構文チェック
  - sqlcheck:        アンチパターン検出
  
フォーマット:
  - SQLFluff:        自動整形
  - pg_format:       PostgreSQL用
```

**設定例（.sqlfluff）**:
```ini
[sqlfluff]
dialect = postgres
templater = jinja
max_line_length = 100
indent_unit = space

[sqlfluff:rules]
tab_space_size = 2
max_line_length = 100
indent_unit = space

[sqlfluff:rules:L010]
capitalisation_policy = upper

[sqlfluff:rules:L030]
capitalisation_policy = upper
```

---

## 10. Devin AIガイドライン

### 10.1 コード生成時の品質チェック

**Devinが実行すべき品質チェック**:

```yaml
コード生成前:
  1. 要件の明確化
  2. 既存コードの調査（重複防止）
  3. アーキテクチャパターンの確認
  
コード生成中:
  4. 関数サイズを50行以内に保つ
  5. 循環的複雑度を10以下に保つ
  6. ネスト深度を3レベル以内に保つ
  7. DRY原則の適用
  8. SOLID原則の適用
  
コード生成後:
  9. 静的解析ツールの実行（Linter）
  10. テストコードの生成（カバレッジ80%以上）
  11. ドキュメントの更新
  12. セルフレビューの実施
```

---

### 10.2 Devinへのプロンプト例

#### **高品質コード生成プロンプト**

```
【タスク】
〇〇機能を実装してください。

【品質要件】
以下の品質標準を満たすこと：
/04-quality-standards/code-quality-standards.md

【必須チェック項目】
- [ ] 関数サイズ: 50行以内
- [ ] 循環的複雑度: 10以下
- [ ] ネスト深度: 3レベル以内
- [ ] コメント率: 15-20%
- [ ] DRY原則: 重複コードなし
- [ ] SOLID原則: 適用
- [ ] テストカバレッジ: 80%以上
- [ ] 静的解析: エラーゼロ

【実装手順】
1. 既存コードの調査（重複防止）
2. 設計（クラス図・フロー図）
3. 実装（品質基準遵守）
4. テストコード作成
5. 静的解析実行
6. セルフレビュー

【出力】
- 実装コード
- テストコード
- 品質メトリクスレポート
- セルフレビューコメント
```

---

### 10.3 品質メトリクスレポートフォーマット

**Devinが生成すべきレポート**:

```markdown
## 品質メトリクスレポート

### コード統計
- 総行数: XXX行
- 関数数: XX個
- クラス数: XX個
- コメント率: XX%

### 複雑度メトリクス
- 平均循環的複雑度: X.X
- 最大循環的複雑度: XX (関数名: `function_name`)
- 平均ネスト深度: X.X
- 最大ネスト深度: X (関数名: `function_name`)

### 品質評価
- 保守性指数: XX点（評価: 優秀/良好/注意/問題）
- コード重複率: X.X%
- 技術的負債比率: X.X%

### 静的解析結果
- Blocker violations: 0件 ✅
- Critical violations: 0件 ✅
- Major violations: X件
- Minor violations: XX件

### テストカバレッジ
- Line Coverage: XX% ✅
- Branch Coverage: XX%
- Function Coverage: XX%

### 品質ゲート判定
✅ PASS / ❌ FAIL

【改善推奨事項】
- 関数`xxx`の複雑度が12です。10以下に分割してください。
- クラス`Yyy`が450行です。400行以下に分割を検討してください。
```

---

## 📊 クイックリファレンス

### 品質メトリクス一覧表

| メトリクス | 理想 | 推奨上限 | 警告 | 違反 |
|---|---|---|---|---|
| 関数サイズ | 20行 | 50行 | 100行 | 100行超 |
| クラスサイズ | 200行 | 400行 | 1000行 | 1000行超 |
| 循環的複雑度 | 5以下 | 10以下 | 15以下 | 15超 |
| 認知的複雑度 | 5以下 | 10以下 | 15以下 | 25超 |
| ネスト深度 | 2レベル | 3レベル | 4レベル | 5レベル超 |
| コメント率 | 15-20% | 10-30% | 5-40% | 5%未満 or 40%超 |
| コード重複率 | 3%以下 | 5%以下 | 10%以下 | 10%超 |
| 保守性指数 | 85-100 | 65-84 | 40-64 | 40未満 |
| カバレッジ | 85%以上 | 80%以上 | 70%以上 | 70%未満 |
| 技術的負債比率 | 3%以下 | 5%以下 | 10%以下 | 10%超 |

---

## 📚 関連ドキュメント

### 内部リソース
- **コーディング規約**: `/01-coding-standards/`
- **品質標準**: `/04-quality-standards/README.md`
- **テスト標準**: `/04-quality-standards/testing-standards.md`
- **CI/CD**: `/03-development-process/ci-cd-pipeline.md`
- **技術的負債管理**: `/03-development-process/technical-debt-management.md`

### 外部参考資料
- [SonarQube Documentation](https://docs.sonarqube.org/)
- [Code Metrics - Microsoft](https://learn.microsoft.com/en-us/visualstudio/code-quality/code-metrics-values)
- [Cognitive Complexity - SonarSource](https://www.sonarsource.com/resources/cognitive-complexity/)
- [Refactoring - Martin Fowler](https://refactoring.com/)
- [Clean Code - Robert C. Martin](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)

---

## ✅ チェックリスト

### コード作成時
- [ ] 関数サイズ50行以内
- [ ] 循環的複雑度10以下
- [ ] ネスト深度3レベル以内
- [ ] DRY原則遵守
- [ ] SOLID原則適用
- [ ] 適切なコメント（15-20%）
- [ ] **コメント規約準拠** ✨
  - [ ] 日本語で記述（技術用語を除く）
  - [ ] WHY原則（「なぜ」を説明）
  - [ ] 複雑度10以上の関数に詳細コメント
  - [ ] TODO/FIXME/HACKに担当者・期限・理由
- [ ] 静的解析エラーゼロ

### コードレビュー時
- [ ] 品質メトリクスが基準内
- [ ] テストカバレッジ80%以上
- [ ] **テストコメント4要素確認** ✨
  - [ ] 【テスト対象】【テストケース】【期待結果】【ビジネス要件】
  - [ ] Given-When-Then構造の詳細コメント
- [ ] コードスメルなし
- [ ] セキュリティチェック完了
- [ ] ドキュメント更新済み

### リリース前
- [ ] 保守性指数65以上
- [ ] 技術的負債比率5%以下
- [ ] 全品質ゲート通過
- [ ] パフォーマンステスト完了

---

**最終更新**: 2025-11-15（コメント規約チェック項目追加）  
**次回レビュー予定**: 2026-01-27
