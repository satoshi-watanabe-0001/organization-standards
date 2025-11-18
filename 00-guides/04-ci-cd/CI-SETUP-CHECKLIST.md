---
title: "CI設定チェックリスト"
version: "1.0.0"
created_date: "2025-11-07"
last_updated: "2025-11-14"
status: "Active"
owner: "DevOps Team / Quality Assurance"
audience: "AI Agents, Developers"
---

# CI設定チェックリスト
## CI Setup Checklist

> プロジェクトのCI/CD設定が組織標準に準拠しているか確認するためのチェックリスト

**対象**: Devin、Cursor、開発者全員  
**目的**: 必須CI品質ゲート設定の漏れを防止し、品質基準を満たさないコードのマージを防ぐ

---

## 📌 このチェックリストの使い方

### 使用タイミング

| タイミング | 実施者 | 所要時間 | 目的 |
|-----------|--------|---------|------|
| **Phase 1 完了直後** | 実装担当AI/開発者 | 20-30分 | 初期CI設定の妥当性確認 |
| **Phase 3 実装開始前** | 実装担当AI/開発者 | 15分 | CI設定の再確認 |
| **Phase 4 レビュー時** | レビュー担当 | 10-15分 | CI実行結果の検証 |
| **CI設定変更後** | 変更者 | 15分 | 変更内容の妥当性確認 |

### 重要度の凡例

- 🔴 **必須** - 品質ゲート。未実施の場合はPRマージ不可
- 🟡 **推奨** - 実施推奨。スキップ時は理由を記録
- 🟢 **任意** - プロジェクトの特性に応じて実施

---

## 🎯 1. 必須CI品質ゲート設定 🔴

### 1.1 コード品質チェック

#### ✅ Linting (リンティング)

**目的**: コーディング規約違反の自動検出

- [ ] 🔴 言語別リンターが設定されている
  - [ ] **TypeScript/JavaScript**: ESLint実行
  - [ ] **Python**: pylint または flake8 実行
  - [ ] **Java**: Checkstyle実行

**検証方法**:
```bash
# TypeScript/JavaScript
cat .github/workflows/ci.yaml | grep -i "eslint"
# または
npm run | grep "lint"

# Python
cat .github/workflows/ci.yaml | grep -E "pylint|flake8"
# または
cat pyproject.toml | grep -E "pylint|flake8"

# Java
cat .github/workflows/ci.yaml | grep -i "checkstyle"
# または
./gradlew tasks | grep "checkstyle"
```

**期待される結果**:
- CIワークフローファイルにリンター実行ステップが存在する
- リンターエラー時にCIが失敗する設定になっている（`|| true` が無い）

**NG例**:
```yaml
# ❌ 失敗しても続行してしまう
- run: npm run lint || true
```

**OK例**:
```yaml
# ✅ エラー時にパイプライン停止
- run: npm run lint
```

---

#### ✅ コードフォーマットチェック

**目的**: コードフォーマット違反の検出（自動修正ではなくチェックのみ）

- [ ] 🔴 フォーマットチェックが設定されている
  - [ ] **TypeScript/JavaScript**: Prettier check
  - [ ] **Python**: black check または yapf check
  - [ ] **Java**: spotlessCheck

**検証方法**:
```bash
# TypeScript/JavaScript
cat .github/workflows/ci.yaml | grep -E "prettier.*check"
cat package.json | grep "format:check"

# Python
cat .github/workflows/ci.yaml | grep "black --check"

# Java
cat .github/workflows/ci.yaml | grep "spotlessCheck"
./gradlew tasks | grep "spotlessCheck"
```

**重要**: `spotlessApply` や `prettier --write` ではなく、**チェックのみ**を実行すること

**NG例**:
```yaml
# ❌ 自動修正してしまう（CI上では不適切）
- run: npm run format
- run: ./gradlew spotlessApply
```

**OK例**:
```yaml
# ✅ チェックのみ実行
- run: npm run format:check
- run: ./gradlew spotlessCheck
```

---

#### ✅ 型チェック

**目的**: 型安全性の検証（静的型付け言語の場合）

- [ ] 🔴 型チェックが設定されている
  - [ ] **TypeScript**: `tsc --noEmit` 実行
  - [ ] **Python**: mypy 実行（型ヒント使用時）

**検証方法**:
```bash
# TypeScript
cat .github/workflows/ci.yaml | grep -E "tsc|type-check"
cat package.json | grep "type-check"

# Python (型ヒント使用時)
cat .github/workflows/ci.yaml | grep "mypy"
```

---

### 1.2 ビルド検証

#### ✅ コンパイル/ビルド

**目的**: ソースコードのコンパイル可否、ビルド成功の検証

- [ ] 🔴 ビルドステージが存在する
  - [ ] `build` ジョブまたは `compile` ステップが存在
  - [ ] ビルド失敗時にCIが停止する

**検証方法**:
```bash
# 全言語共通
cat .github/workflows/ci.yaml | grep -E "build|compile"

# Java
./gradlew tasks | grep "build"

# TypeScript
npm run | grep "build"

# Python
cat setup.py  # または pyproject.toml でビルド設定確認
```

**期待される結果**:
- コンパイルエラーがある場合、CIが失敗する
- アーティファクト（jar/war/dist/wheelなど）が生成される

---

#### ✅ アーティファクト生成

**目的**: デプロイ可能な成果物の生成確認

- [ ] 🔴 成果物が正常に生成される
  - [ ] **Java**: jar/war ファイル
  - [ ] **TypeScript**: dist ディレクトリ
  - [ ] **Python**: wheel パッケージ（配布時）

**検証方法**:
```bash
# ローカルでビルド実行
./gradlew build  # Java
npm run build    # TypeScript
python -m build  # Python

# 成果物の確認
ls build/libs/*.jar        # Java (Gradle)
ls target/*.jar            # Java (Maven)
ls dist/                   # TypeScript
ls dist/*.whl              # Python
```

---

### 1.3 テスト実行と品質ゲート ⭐最重要

#### ✅ ユニットテスト実行

**目的**: 全テストの実行とパス確認

- [ ] 🔴 すべてのユニットテストが実行される
  - [ ] `test` ジョブが存在
  - [ ] テスト失敗時にCIが停止する

**検証方法**:
```bash
# 全言語共通
cat .github/workflows/ci.yaml | grep -i "test"

# Java
./gradlew tasks | grep "test"

# TypeScript
npm run | grep "test"

# Python
cat pyproject.toml | grep "pytest"
```

**期待される結果**:
- すべてのテストが実行される（特定のテストがスキップされていない）
- 1つでもテストが失敗するとCIが失敗する

---

#### ✅ カバレッジ測定

**目的**: テストカバレッジの計測

- [ ] 🔴 カバレッジ計測ツールが設定されている
  - [ ] **TypeScript**: Istanbul/Jest coverage
  - [ ] **Python**: coverage.py または pytest-cov
  - [ ] **Java**: JaCoCo

**検証方法**:
```bash
# TypeScript
cat package.json | grep "coverage"
cat jest.config.js | grep "coverage"

# Python
cat .coveragerc
cat pyproject.toml | grep "coverage"

# Java
cat build.gradle | grep "jacoco"
./gradlew tasks | grep "jacoco"
```

**期待される結果**:
- CIログでカバレッジレポートが生成される
- カバレッジ数値が確認できる

---

#### ✅ カバレッジ閾値強制 ⭐⭐最重要⭐⭐

**目的**: 組織標準の80%カバレッジ閾値を強制

- [ ] 🔴 **80%カバレッジ閾値チェックが実行されている**
  - [ ] **Java**: `jacocoTestCoverageVerification` タスクが実行される
  - [ ] **TypeScript**: `jest.config.js` に `coverageThreshold: 80` 設定
  - [ ] **Python**: `.coveragerc` に `fail_under = 80` 設定

**これが今回の問題の核心です！**

**検証方法（Java）**:

```bash
# 1. build.gradle で設定確認
cat build.gradle | grep -A 10 "jacocoTestCoverageVerification"

# 期待される出力:
# jacocoTestCoverageVerification {
#     violationRules {
#         rule {
#             limit {
#                 minimum = 0.80
#             }
#         }
#     }
# }

# 2. CI ワークフローで実行確認
cat .github/workflows/ci.yaml | grep "jacocoTestCoverageVerification"

# 期待される出力:
# - run: ./gradlew jacocoTestCoverageVerification

# 3. ローカルで実行確認
./gradlew jacocoTestCoverageVerification
```

**検証方法（TypeScript）**:

```bash
# 1. jest.config.js で設定確認
cat jest.config.js | grep -A 8 "coverageThreshold"

# 期待される出力:
# coverageThreshold: {
#   global: {
#     lines: 80,
#     functions: 80,
#     branches: 80,
#     statements: 80
#   }
# }

# 2. CI ワークフローで実行確認
cat .github/workflows/ci.yaml | grep "test:coverage"

# 3. ローカルで実行確認
npm run test:coverage
```

**検証方法（Python）**:

```bash
# 1. .coveragerc で設定確認
cat .coveragerc | grep "fail_under"

# 期待される出力:
# fail_under = 80

# 2. CI ワークフローで実行確認
cat .github/workflows/ci.yaml | grep -E "pytest.*cov|coverage"

# 3. ローカルで実行確認
pytest --cov --cov-fail-under=80
```

**期待される結果**:
- カバレッジが80%未満の場合、CIが失敗する
- CIログに "Coverage check failed" または類似のメッセージが表示される

**NG例（Java）**:
```yaml
# ❌ レポート生成のみで閾値チェックなし
- run: ./gradlew test jacocoTestReport
```

```gradle
// ❌ build.gradle に jacocoTestCoverageVerification が無い
plugins {
    id 'jacoco'
}

jacoco {
    toolVersion = "0.8.8"
}

// ここで終わり → 閾値チェックが実行されない！
```

**OK例（Java）**:
```yaml
# ✅ 閾値チェックを明示的に実行
- name: Verify Test Coverage
  run: ./gradlew jacocoTestCoverageVerification
```

```gradle
// ✅ build.gradle に閾値設定がある
plugins {
    id 'jacoco'
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80  // 80%閾値
            }
        }
    }
}

// check タスクに依存させる（推奨）
check.dependsOn jacocoTestCoverageVerification
```

---


## 📋 2. 言語別必須設定マトリクス 🔴

> **詳細**: [CI-SETUP-LANGUAGE-MATRIX.md](./CI-SETUP-LANGUAGE-MATRIX.md)

各言語（Java、TypeScript/JavaScript、Python等）の詳細な設定要件については、
上記の言語別マトリクスドキュメントを参照してください。

### クイックサマリー

各言語で以下の設定が必須です:

- **コード品質**: フォーマッター、リンター、静的解析
- **ビルド**: 依存関係解決、コンパイル、成果物生成
- **テスト**: ユニットテスト実行、カバレッジ測定（閾値80%以上）
- **CI統合**: GitHub Actions / Azure Pipelines設定

## 🔍 3. CI設定検証手順（段階的チェック）

### Phase 1: 設定ファイル存在確認（5分）

```bash
# 1. CIワークフローファイル確認
test -f .github/workflows/ci.yaml && echo "✅ CI workflow exists" || echo "❌ CI workflow NOT found"

# 2. 言語別設定ファイル確認
# Java
test -f build.gradle && echo "✅ build.gradle exists" || echo "⚠️ Not a Java project"

# TypeScript
test -f package.json && echo "✅ package.json exists" || echo "⚠️ Not a Node.js project"
test -f jest.config.js && echo "✅ jest.config.js exists" || echo "❌ Jest config NOT found"

# Python
test -f pyproject.toml && echo "✅ pyproject.toml exists" || echo "⚠️ Not a Poetry project"
test -f .coveragerc && echo "✅ .coveragerc exists" || echo "⚠️ Coverage config in pyproject.toml"
```

---

### Phase 2: 品質ゲートツール設定確認（10分）

#### Java プロジェクト

```bash
echo "=== Java CI設定検証 ==="

# 1. Gradle pluginsチェック
echo -e "\n1. Gradle Plugins:"
grep -E "id '(jacoco|spotless|checkstyle)'" build.gradle

# 2. JaCoCo 閾値設定チェック
echo -e "\n2. JaCoCo Coverage Threshold:"
grep -A 5 "jacocoTestCoverageVerification" build.gradle | grep "minimum"

# 3. CI ワークフロー必須ステップチェック
echo -e "\n3. CI Workflow Steps:"
grep -E "spotlessCheck|checkstyleMain|jacocoTestCoverageVerification" .github/workflows/ci.yaml

# 4. 利用可能タスク一覧
echo -e "\n4. Available Tasks:"
./gradlew tasks --group verification | grep -E "spotless|checkstyle|jacoco"
```

**期待される出力**:
```
=== Java CI設定検証 ===

1. Gradle Plugins:
    id 'jacoco'
    id 'com.diffplug.spotless' version '6.x.x'
    id 'checkstyle'

2. JaCoCo Coverage Threshold:
                minimum = 0.80

3. CI Workflow Steps:
      - run: ./gradlew spotlessCheck
      - run: ./gradlew checkstyleMain checkstyleTest
      - run: ./gradlew jacocoTestCoverageVerification

4. Available Tasks:
checkstyleMain
checkstyleTest
spotlessCheck
spotlessApply
jacocoTestReport
jacocoTestCoverageVerification
```

---


### Phase 3: ローカル実行確認（10分）

> **詳細**: [CI-SETUP-LANGUAGE-MATRIX.md](./CI-SETUP-LANGUAGE-MATRIX.md) の各言語セクションを参照

**チェック項目**:
- [ ] 品質ゲートコマンドがローカルで実行可能
- [ ] すべてのチェック（フォーマット、リント、テスト、カバレッジ）が合格
- [ ] ビルドが成功

### Phase 4: CI実行結果確認（Phase 4レビュー時）

**チェック項目**:
- [ ] PRでCIワークフローが自動実行
- [ ] 品質ゲートがすべて合格
- [ ] カバレッジレポートが生成・表示
- [ ] 失敗時は適切なエラーメッセージ表示

## ❌ 4. よくある設定ミスと対策

### ❌ ミス1: カバレッジ測定はあるが閾値強制がない

**症状**: カバレッジレポートは生成されるが、閾値未達でもCIがパスする

**Java の場合**:
```gradle
// ❌ 悪い例: レポート生成のみ
jacoco {
    toolVersion = "0.8.8"
}
// jacocoTestCoverageVerification が無い！
```

```yaml
# ❌ 悪い例: CI で jacocoTestReport のみ実行
- run: ./gradlew test jacocoTestReport
```

**✅ 修正方法**:
```gradle
// ✅ 良い例: 閾値検証を追加
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80
            }
        }
    }
}

check.dependsOn jacocoTestCoverageVerification
```

```yaml
# ✅ 良い例: CI で jacocoTestCoverageVerification を実行
- name: Verify Test Coverage
  run: ./gradlew jacocoTestCoverageVerification
```

---

### ❌ ミス2: ローカルでは動くがCIで実行されない

**症状**: `build.gradle` や `package.json` に設定はあるが、`.github/workflows/ci.yaml` で実行されていない

**Java の場合**:
```gradle
// build.gradle に設定はあるが...
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80
            }
        }
    }
}
```

```yaml
# ❌ CI ワークフローで実行されていない
jobs:
  build:
    steps:
      - run: ./gradlew build
      - run: ./gradlew test
      # jacocoTestCoverageVerification が無い！
```

**✅ 修正方法**:
```yaml
# ✅ CI で明示的に実行
jobs:
  build:
    steps:
      - run: ./gradlew build
      - run: ./gradlew test
      - run: ./gradlew jacocoTestCoverageVerification  # 追加
```

---

### ❌ ミス3: フォーマットチェックが警告のみで止まらない

**症状**: フォーマット違反があってもCIがパスしてしまう

```yaml
# ❌ 悪い例: 失敗しても続行
- run: ./gradlew spotlessCheck || true
- run: npm run format:check || echo "Format check failed but continuing"
```

**✅ 修正方法**:
```yaml
# ✅ 良い例: 失敗時にパイプライン停止
- name: Check Code Formatting
  run: ./gradlew spotlessCheck  # エラー時は exit 1

- name: Check Code Formatting
  run: npm run format:check  # エラー時は exit 1
```

---

### ❌ ミス4: checkタスクに依存関係が設定されていない

**症状**: `./gradlew build` 実行時に品質チェックがスキップされる

**Java の場合**:
```gradle
// ❌ 悪い例: 依存関係なし
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80
            }
        }
    }
}
// ここで終わり
```

この場合、`./gradlew build` を実行しても `jacocoTestCoverageVerification` は実行されない。

**✅ 修正方法**:
```gradle
// ✅ 良い例: check タスクに依存させる
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80
            }
        }
    }
}

// これを追加
check.dependsOn jacocoTestCoverageVerification
```

これにより、`./gradlew build` や `./gradlew check` 実行時に自動的にカバレッジ検証が実行される。

---

### ❌ ミス5: カバレッジ閾値が低すぎる

**症状**: 組織標準の80%ではなく、低い閾値が設定されている

```gradle
// ❌ 悪い例: 閾値が低すぎる
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.50  // 50% → 組織標準違反！
            }
        }
    }
}
```

```javascript
// ❌ 悪い例
coverageThreshold: {
  global: {
    lines: 60,  // 60% → 組織標準違反！
    functions: 60,
    branches: 60,
    statements: 60
  }
}
```

**✅ 修正方法**:
```gradle
// ✅ 良い例: 組織標準の80%
minimum = 0.80
```

```javascript
// ✅ 良い例
coverageThreshold: {
  global: {
    lines: 80,
    functions: 80,
    branches: 80,
    statements: 80
  }
}
```

**参照**: `/04-quality-standards/testing-standards.md` - カバレッジ基準

---

## 📊 5. チェックリスト完了基準

### Phase 1 (プロジェクト初期化) 完了時

**すべて ✅ になっていること**:

- [ ] 🔴 `.github/workflows/ci.yaml` が存在する
- [ ] 🔴 言語別設定ファイル（`build.gradle` / `package.json` / `pyproject.toml`）に品質ゲート設定がある
- [ ] 🔴 カバレッジ閾値（80%）が設定されている
- [ ] 🔴 ローカルですべての品質ゲートコマンドが実行できる
- [ ] 🔴 ローカル実行でエラーが無い（または予想通りのエラー）

---

### Phase 3 (実装) 完了時

**すべて ✅ になっていること**:

- [ ] 🔴 CI設定ファイルが最新の実装に対応している
- [ ] 🔴 新規追加した依存関係がCI環境で利用可能
- [ ] 🟡 実装中に品質ゲートコマンドを定期的に実行した

---

### Phase 4 (レビュー) 開始時

**すべて ✅ になっていること**:

- [ ] 🔴 **このチェックリストの全項目を検証済み**
- [ ] 🔴 PRのCIがすべてパスしている
- [ ] 🔴 CIログで以下が実行されたことを確認:
  - [ ] Lint / Checkstyle
  - [ ] Format check / Spotless check
  - [ ] Build / Compile
  - [ ] Tests
  - [ ] **Coverage verification (80%閾値チェック)**
- [ ] 🔴 カバレッジレポートで実際のカバレッジ率が80%以上

**これらがすべて ✅ でない場合、レビュー不合格**

---

## 🔧 6. トラブルシューティング

### Q1: カバレッジ閾値設定が見つからない

**対応手順**:
1. このチェックリストの「2. 言語別必須設定マトリクス」を参照
2. 該当言語のテンプレートをコピー
3. 組織標準ドキュメントを参照:
   - `/03-development-process/ci-cd-pipeline.md` - セクション3.3
   - `/04-quality-standards/testing-standards.md` - カバレッジ基準

---

### Q2: CIワークフローファイルが存在しない

**対応手順**:
1. `/08-templates/ci-templates/` (このドキュメント整備後に作成予定) を参照
2. または組織標準CI/CD設定を参照:
   - `/03-development-process/ci-cd-pipeline.md`
3. 言語別サンプルワークフローをコピーして作成

---

### Q3: ローカルで品質ゲートコマンドが失敗する

**対応手順**:

**フォーマット違反の場合**:
```bash
# Java
./gradlew spotlessApply

# TypeScript
npm run format

# Python
black src/
```

**Lint違反の場合**:
```bash
# 自動修正可能なものを修正
npm run lint:fix  # TypeScript
pylint src/ | grep "^C:"  # Python: Convention違反を確認
```

**カバレッジ不足の場合**:
1. カバレッジレポートを確認（`build/reports/jacoco/test/html/index.html` など）
2. テストが不足しているクラス/関数を特定
3. ユニットテストを追加
4. 参照: `/04-quality-standards/unit-testing.md`

---

### Q4: CIが実行されるがログに品質ゲートが見当たらない

**対応手順**:
1. `.github/workflows/ci.yaml` を開く
2. 必須ステップが存在するか確認（上記「言語別必須設定」参照）
3. 不足しているステップを追加
4. コミット・プッシュして再度CI実行

---

## 📚 7. 参照ドキュメント

### 組織標準ドキュメント
- [CI-SETUP-LANGUAGE-MATRIX.md](./CI-SETUP-LANGUAGE-MATRIX.md) - 言語別詳細設定マトリクス
- [CI-SETUP-QUICK-CHECKLIST.md](./CI-SETUP-QUICK-CHECKLIST.md) - クイックチェックリスト

- 🔴 **必須**: `/03-development-process/ci-cd-pipeline.md` - CI/CD標準全般
- 🔴 **必須**: `/04-quality-standards/code-quality-standards.md` - コード品質基準
- 🔴 **必須**: `/04-quality-standards/testing-standards.md` - テスト・カバレッジ基準
- 🟡 **推奨**: `/00-guides/AI-MASTER-WORKFLOW-GUIDE.md` - 全体ワークフロー
- 🟡 **推奨**: `/00-guides/phase-guides/phase-3-implementation-guide.md` - 実装フェーズ詳細
- 🟡 **推奨**: `/00-guides/phase-guides/phase-4-review-qa-guide.md` - レビューフェーズ詳細

### 言語別コーディング標準

- `/01-coding-standards/java-standards.md`
- `/01-coding-standards/typescript-javascript-standards.md`
- `/01-coding-standards/python-standards.md`

### テンプレート（このドキュメント整備後に作成予定）

- `/08-templates/ci-templates/java-spring-boot/`
- `/08-templates/ci-templates/typescript-node/`
- `/08-templates/ci-templates/python/`

---

## 🎓 9. AIエージェント向けガイドライン

### Devin / Cursor が自動実行すべきこと

#### Phase 1 完了時

```python
def auto_verify_ci_setup():
    """
    CI設定の自動検証（Phase 1完了時）
    """
    print("🔍 CI設定を自動検証中...")
    
    issues = []
    
    # 1. ワークフローファイル存在確認
    if not os.path.exists('.github/workflows/ci.yaml'):
        issues.append({
            'severity': 'ERROR',
            'message': 'CI workflow file not found',
            'action': 'Create .github/workflows/ci.yaml from template'
        })
    
    # 2. 言語検出と設定検証
    if os.path.exists('build.gradle'):  # Java
        issues.extend(verify_java_ci_config())
    elif os.path.exists('package.json'):  # TypeScript/Node
        issues.extend(verify_typescript_ci_config())
    elif os.path.exists('pyproject.toml'):  # Python
        issues.extend(verify_python_ci_config())
    
    # 3. レポート
    if issues:
        print("❌ CI設定に問題があります:\n")
        for issue in issues:
            severity_icon = "🔴" if issue['severity'] == 'ERROR' else "🟡"
            print(f"{severity_icon} [{issue['severity']}] {issue['message']}")
            print(f"   対応: {issue['action']}\n")
        
        print("📋 修正手順:")
        print("   1. /00-guides/CI-SETUP-CHECKLIST.md を開く")
        print("   2. 該当する言語セクションを参照")
        print("   3. 不足している設定を追加")
        print("   4. ローカルで品質ゲートコマンドを実行")
        print("   5. このスクリプトを再実行\n")
        
        return False
    else:
        print("✅ CI設定は組織標準に準拠しています")
        return True


def verify_java_ci_config():
    """Java プロジェクトのCI設定検証"""
    issues = []
    
    gradle_content = read_file('build.gradle')
    
    # JaCoCo plugin チェック
    if "id 'jacoco'" not in gradle_content:
        issues.append({
            'severity': 'ERROR',
            'message': 'JaCoCo plugin not configured in build.gradle',
            'action': 'Add: id \'jacoco\' to plugins block'
        })
    
    # Spotless plugin チェック
    if 'spotless' not in gradle_content:
        issues.append({
            'severity': 'ERROR',
            'message': 'Spotless plugin not configured in build.gradle',
            'action': 'Add: id \'com.diffplug.spotless\' to plugins block'
        })
    
    # カバレッジ閾値チェック
    if 'jacocoTestCoverageVerification' not in gradle_content:
        issues.append({
            'severity': 'ERROR',
            'message': 'Coverage threshold not configured in build.gradle',
            'action': 'Add jacocoTestCoverageVerification block with minimum = 0.80'
        })
    elif 'minimum = 0.80' not in gradle_content and 'minimum = 0.8' not in gradle_content:
        issues.append({
            'severity': 'WARNING',
            'message': 'Coverage threshold may not be 80%',
            'action': 'Verify minimum = 0.80 in jacocoTestCoverageVerification'
        })
    
    # CI ワークフローチェック
    if os.path.exists('.github/workflows/ci.yaml'):
        ci_yaml = read_file('.github/workflows/ci.yaml')
        
        if 'spotlessCheck' not in ci_yaml:
            issues.append({
                'severity': 'ERROR',
                'message': 'spotlessCheck not executed in CI workflow',
                'action': 'Add step: ./gradlew spotlessCheck'
            })
        
        if 'jacocoTestCoverageVerification' not in ci_yaml:
            issues.append({
                'severity': 'ERROR',
                'message': 'jacocoTestCoverageVerification not executed in CI workflow',
                'action': 'Add step: ./gradlew jacocoTestCoverageVerification'
            })
    
    return issues


def verify_typescript_ci_config():
    """TypeScript プロジェクトのCI設定検証"""
    issues = []
    
    # package.json チェック
    with open('package.json') as f:
        package_json = json.load(f)
    
    required_scripts = ['lint', 'format:check', 'type-check', 'test:coverage']
    for script in required_scripts:
        if script not in package_json.get('scripts', {}):
            issues.append({
                'severity': 'ERROR',
                'message': f'Missing npm script: {script}',
                'action': f'Add "{script}" script to package.json'
            })
    
    # jest.config.js チェック
    if os.path.exists('jest.config.js'):
        jest_config = read_file('jest.config.js')
        
        if 'coverageThreshold' not in jest_config:
            issues.append({
                'severity': 'ERROR',
                'message': 'Coverage threshold not configured in jest.config.js',
                'action': 'Add coverageThreshold with global: { lines: 80, ... }'
            })
    else:
        issues.append({
            'severity': 'WARNING',
            'message': 'jest.config.js not found',
            'action': 'Create jest.config.js with coverage threshold'
        })
    
    return issues


def verify_python_ci_config():
    """Python プロジェクトのCI設定検証"""
    issues = []
    
    # pyproject.toml チェック
    if os.path.exists('pyproject.toml'):
        pyproject_content = read_file('pyproject.toml')
        
        if 'pytest-cov' not in pyproject_content:
            issues.append({
                'severity': 'ERROR',
                'message': 'pytest-cov not in dependencies',
                'action': 'Add pytest-cov to dev dependencies'
            })
        
        if 'cov-fail-under' not in pyproject_content:
            # .coveragerc をチェック
            if not os.path.exists('.coveragerc'):
                issues.append({
                    'severity': 'ERROR',
                    'message': 'Coverage threshold not configured',
                    'action': 'Add cov-fail-under=80 to pyproject.toml or create .coveragerc'
                })
    
    # .coveragerc チェック
    if os.path.exists('.coveragerc'):
        coveragerc_content = read_file('.coveragerc')
        
        if 'fail_under = 80' not in coveragerc_content:
            issues.append({
                'severity': 'WARNING',
                'message': 'Coverage threshold may not be 80%',
                'action': 'Verify fail_under = 80 in .coveragerc'
            })
    
    return issues
```

#### Phase 4 レビュー時

```python
def auto_verify_ci_execution():
    """
    CI実行結果の自動検証（Phase 4レビュー時）
    """
    print("🔍 CI実行結果を検証中...")
    
    # 最新のCI実行ログを取得
    ci_log = get_latest_ci_log()  # GitHub API等から取得
    
    # 必須キーワードの存在確認
    required_checks = {
        'format': {
            'keywords': ['spotlessCheck', 'prettier', 'black --check'],
            'description': 'Code formatting check'
        },
        'lint': {
            'keywords': ['checkstyle', 'eslint', 'pylint'],
            'description': 'Linting'
        },
        'coverage': {
            'keywords': [
                'jacocoTestCoverageVerification',
                'coverageThreshold',
                'fail_under',
                'Coverage check'
            ],
            'description': 'Coverage threshold verification (80%)'
        },
        'build': {
            'keywords': ['BUILD SUCCESSFUL', 'compiled successfully', 'Build'],
            'description': 'Build/Compile'
        },
        'test': {
            'keywords': ['tests passed', 'All tests', 'OK'],
            'description': 'Tests execution'
        }
    }
    
    issues = []
    for check_name, check_info in required_checks.items():
        if not any(kw in ci_log for kw in check_info['keywords']):
            issues.append({
                'check': check_name,
                'description': check_info['description'],
                'message': f"{check_info['description']} not found in CI log"
            })
    
    if issues:
        print("❌ CI実行で品質ゲートが実行されていません:\n")
        for issue in issues:
            print(f"🔴 [{issue['check']}] {issue['message']}")
        
        print("\n📋 対応手順:")
        print("   1. .github/workflows/ci.yaml を確認")
        print("   2. 不足しているステップを追加")
        print("   3. /00-guides/CI-SETUP-CHECKLIST.md の該当セクションを参照")
        print("   4. 再度コミット・プッシュしてCI実行\n")
        
        return False
    else:
        print("✅ すべての品質ゲートがCI上で実行されました")
        
        # カバレッジ率の確認
        coverage_match = re.search(r'(\d+)%\s+coverage', ci_log)
        if coverage_match:
            coverage_pct = int(coverage_match.group(1))
            if coverage_pct >= 80:
                print(f"✅ カバレッジ {coverage_pct}% (>= 80%)")
            else:
                print(f"❌ カバレッジ {coverage_pct}% (< 80%)")
                return False
        
        return True
```

---

## 変更履歴

- **2025-11-13**: ファイルを3つに分割（共通、言語別、クイック）- 肥大化解消
| バージョン | 日付 | 変更内容 | 変更者 |
|---------|------|---------|-------|
| 1.0.0 | 2025-11-07 | 初版作成 | DevOps Team |

---

**ドキュメント管理者**: DevOps Team / Quality Assurance  
**レビュー頻度**: 四半期ごと  
**次回レビュー**: 2026-02-07
