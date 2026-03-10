# CI設定テンプレート集
## CI Configuration Templates

**バージョン**: 1.0.0  
**最終更新日**: 2025-11-07  
**対象**: AI Agents, 開発者全員

---

## 📋 概要

このディレクトリには、プロジェクトのCI/CD設定を迅速にセットアップするための言語別テンプレートが含まれています。

**目的**:
- プロジェクト初期化時のCI設定時間を削減
- 組織標準に準拠したCI設定の提供
- 必須品質ゲート設定の漏れを防止

---

## 📂 テンプレート構成

```
ci-templates/
├── README.md                    # このファイル
├── java-spring-boot/            # Java (Spring Boot) プロジェクト用
│   ├── build.gradle.template    # Gradle設定テンプレート
│   └── ci.yaml.template         # GitHub Actions CIワークフロー
├── typescript-node/             # TypeScript/Node.js プロジェクト用
│   ├── package.json.template    # package.json スクリプト設定
│   ├── jest.config.js.template  # Jest カバレッジ設定
│   └── ci.yaml.template         # GitHub Actions CIワークフロー
├── python/                      # Python プロジェクト用
│   ├── pyproject.toml.template  # Poetry設定テンプレート
│   ├── .coveragerc.template     # カバレッジ設定
│   └── ci.yaml.template         # GitHub Actions CIワークフロー
└── github-actions/              # ⭐NEW GitHub Actions CI品質ゲート（言語非依存）
    ├── pr-language-check.yaml   # PR言語チェックCI
    ├── pr-language-check.md     # 詳細ドキュメント
    └── README.md                # GitHub Actions CIテンプレート集
```

---

## 🚀 使用方法

### Phase 1（プロジェクト初期化）時

1. **プロジェクトの言語を特定**
   ```bash
   # Java
   test -f build.gradle && echo "Java (Gradle)"
   
   # TypeScript
   test -f package.json && echo "TypeScript/Node.js"
   
   # Python
   test -f pyproject.toml && echo "Python"
   ```

2. **該当するテンプレートディレクトリを開く**
   - Java: `java-spring-boot/`
   - TypeScript: `typescript-node/`
   - Python: `python/`

3. **テンプレートをプロジェクトにコピー**
   ```bash
   # Java の例
   cp ci-templates/java-spring-boot/ci.yaml.template .github/workflows/ci.yaml
   
   # build.gradle に追記する内容を確認
   cat ci-templates/java-spring-boot/build.gradle.template
   ```

4. **テンプレート内のプレースホルダーを置換**
   - `${PROJECT_NAME}`: プロジェクト名
   - `${JAVA_VERSION}`: Javaバージョン（例: 17）
   - `${NODE_VERSION}`: Node.jsバージョン（例: 18）
   - `${PYTHON_VERSION}`: Pythonバージョン（例: 3.11）

5. **ローカルで動作確認**
   - CI設定チェックリスト（`/00-guides/CI-SETUP-CHECKLIST.md`）を実行
   - すべての品質ゲートコマンドをローカルで実行

6. **コミット・プッシュしてCI実行**
   - PRを作成してCI実行を確認
   - すべての品質ゲートがパスすることを確認

---

## 📚 各テンプレートの詳細

### Java (Spring Boot)

**対象プロジェクト**:
- Gradle ビルドツール
- Spring Boot フレームワーク
- JUnit 5 テストフレームワーク

**含まれる品質ゲート**:
- ✅ Checkstyle（コーディング規約チェック）
- ✅ Spotless（コードフォーマットチェック）
- ✅ JUnit（ユニットテスト）
- ✅ JaCoCo（カバレッジ測定・閾値チェック 80%）

**参照**: `java-spring-boot/README.md`

---

### TypeScript/Node.js

**対象プロジェクト**:
- npm/yarn パッケージマネージャー
- TypeScript
- Jest テストフレームワーク

**含まれる品質ゲート**:
- ✅ ESLint（リンティング）
- ✅ Prettier（コードフォーマットチェック）
- ✅ TypeScript Compiler（型チェック）
- ✅ Jest（ユニットテスト・カバレッジ測定・閾値チェック 80%）

**参照**: `typescript-node/README.md`

---

### Python

**対象プロジェクト**:
- Poetry パッケージマネージャー
- pytest テストフレームワーク

**含まれる品質ゲート**:
- ✅ Pylint（リンティング）
- ✅ Black（コードフォーマットチェック）
- ✅ mypy（型チェック）
- ✅ pytest（ユニットテスト・カバレッジ測定・閾値チェック 80%）

**参照**: `python/README.md`

---

## ⚠️ 重要な注意事項

1. **テンプレートは出発点**
   - プロジェクトの特性に応じてカスタマイズが必要
   - 依存関係のバージョンは最新のものを使用

2. **カバレッジ閾値は80%固定**
   - 組織標準に従い、変更不可
   - 特別な理由がある場合は組織のDevOpsチームに相談

3. **テンプレートの更新**
   - 組織標準の変更に応じて定期的に更新
   - 最新版を使用することを推奨

4. **CI設定チェックリストを必ず実行**
   - テンプレート使用後も `/00-guides/CI-SETUP-CHECKLIST.md` を実行
   - 設定の妥当性を確認

---



---

### GitHub Actions (言語非依存) ⭐NEW

**対象プロジェクト**:
- すべてのGitHubリポジトリ
- すべてのプログラミング言語

**含まれる品質ゲート**:
- ✅ **PR言語チェック**（必須）
  - PRタイトル・説明文の日本語記載を自動検証
  - 英語PRはCI失敗でマージ防止
  - botコメントで修正方法を自動提示
  - 技術用語（API、JWT等）の英語表記は許可

**使用方法**:
```bash
# プロジェクトに配置
cp github-actions/pr-language-check.yaml <project>/.github/workflows/

# ブランチ保護ルールの設定（推奨）
# Settings → Branches → Add rule
# ✅ Require status checks to pass before merging
# ✅ Select: "PR Language Check"
```

**重要度**: ⭐⭐⭐⭐⭐ 必須（すべてのプロジェクトに導入）

**期待効果**:
- 英語PR率: 100% → 0%
- PR理解時間: 平均10分 → 平均3分
- レビューサイクル: 平均2日 → 平均1日
- チーム全体のコミュニケーション効率向上

**詳細ドキュメント**:
- `github-actions/README.md` - GitHub Actions CIテンプレート集の総合ガイド
- `github-actions/pr-language-check.md` - PR言語チェックの詳細ドキュメント
  - 配置方法
  - 動作確認手順
  - カスタマイズ方法
  - トラブルシューティング
  - FAQ

**関連ドキュメント**:
- `/00-guides/PR-LANGUAGE-ISSUE-SOLUTION.md` - PR言語問題の解決策
- `/08-templates/pr-templates/PULL_REQUEST_TEMPLATE.md` - PRテンプレート
- `/00-guides/phase-guides/phase-4-review-qa-guide.md` - Step 4.1.6

## 🔗 関連ドキュメント

- 🔴 **必須**: `/00-guides/CI-SETUP-CHECKLIST.md` - CI設定チェックリスト
- 🔴 **必須**: `/03-development-process/ci-cd-pipeline.md` - CI/CD標準
- 🔴 **必須**: `/04-quality-standards/code-quality-standards.md` - コード品質基準
- 🔴 **必須**: `/04-quality-standards/testing-standards.md` - テスト・カバレッジ基準
- 🟡 **推奨**: `/00-guides/phase-guides/phase-1-project-initialization-guide.md` - プロジェクト初期化ガイド

---

## 📝 テンプレートのカスタマイズガイド

各テンプレートには、以下のようなプレースホルダーが含まれています:

| プレースホルダー | 説明 | 例 |
|---------------|------|-----|
| `${PROJECT_NAME}` | プロジェクト名 | `ec-site-auth-service` |
| `${JAVA_VERSION}` | Javaバージョン | `17` |
| `${NODE_VERSION}` | Node.jsバージョン | `18` |
| `${PYTHON_VERSION}` | Pythonバージョン | `3.11` |
| `${PACKAGE_NAME}` | パッケージ名 | `@myorg/mypackage` |

**置換方法**:

```bash
# 一括置換（sed を使用）
sed -i 's/${PROJECT_NAME}/ec-site-auth-service/g' ci.yaml
sed -i 's/${JAVA_VERSION}/17/g' ci.yaml
```

または、エディタの検索置換機能を使用してください。

---

## 🆘 トラブルシューティング

**Q: テンプレートをコピーしたがCIが失敗する**

A: 以下を確認してください:
1. プレースホルダーがすべて置換されているか
2. 依存関係が正しくインストールされているか
3. `/00-guides/CI-SETUP-CHECKLIST.md` を実行して設定を検証

---

**Q: カバレッジ閾値を変更したい**

A: 組織標準の80%から変更することはできません。特別な理由がある場合はDevOpsチームに相談してください。

---

**Q: 自分の言語用のテンプレートが無い**

A: 以下の対応を検討してください:
1. 既存のテンプレートを参考にカスタマイズ
2. `/03-development-process/ci-cd-pipeline.md` を参照して手動で設定
3. DevOpsチームに新しいテンプレート作成を依頼

---

## 変更履歴

| バージョン | 日付 | 変更内容 |
|---------|------|---------|
| 1.0.0 | 2025-11-07 | 初版作成（Java, TypeScript, Python） |

---

**ドキュメント管理者**: DevOps Team  
**レビュー頻度**: 四半期ごと  
**次回レビュー**: 2026-02-07
