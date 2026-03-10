# プロジェクトテンプレート

organization-standards完全準拠の、即座に使用可能なプロジェクトテンプレート集です。

---

## 📦 利用可能なテンプレート

### 1. Java Spring Boot API テンプレート ⭐ NEW

**ディレクトリ**: `java-springboot-api-template/`

**概要**: Java 17 + Spring Boot 3.2 REST APIの完全なプロジェクトテンプレート

**主な特徴**:
- ✅ **Java 17** + **Spring Boot 3.2.1**
- ✅ **JWT認証実装済み** (アクセストークン15分、リフレッシュトークン7日)
- ✅ **Gradle** ビルドシステム
- ✅ **品質ツール統合済み**:
  - Checkstyle (Google Java Style準拠)
  - Spotless (コードフォーマット自動チェック)
  - SpotBugs (静的解析)
  - JaCoCo (テストカバレッジ80%強制)
- ✅ **CI/CD設定完備**:
  - GitHub Actions (5ファイル)
    - ci.yml (メインCI)
    - cd.yml (継続的デプロイメント)
    - pr-language-check.yaml (必須)
    - pr-self-review-reminder.yml (推奨)
    - pr-description-quality-gate.yml (推奨)
  - GitLab CI (完全版)
- ✅ **Docker設定** (Dockerfile, docker-compose.yml)
- ✅ **PostgreSQL + H2対応**
- ✅ **レイヤーアーキテクチャ** (Controller/Service/Repository)

**使用方法**:
```bash
# 1. テンプレートをコピー
cp -r java-springboot-api-template/ /your/project/path/

# 2. READMEの指示に従ってセットアップ
cd /your/project/path/
cat README.md

# 3. プロジェクト固有の情報に更新
# - application.yml (データベース設定等)
# - build.gradle (group, version)
# - パッケージ名変更
```

**詳細**: [README.md](./java-springboot-api-template/README.md)

**参照ドキュメント**:
- `/organization-standards/01-coding-standards/java/`
- `/organization-standards/08-templates/ci-templates/java-spring-boot/`

---

### 2. TypeScript Express API テンプレート ⭐ NEW

**ディレクトリ**: `typescript-express-api-template/`

**概要**: TypeScript + Express.js REST APIの完全なプロジェクトテンプレート

**主な特徴**:
- ✅ **TypeScript 5.x** + **Express.js 4.x**
- ✅ **JWT認証実装済み**
- ✅ **TypeORM + PostgreSQL**
- ✅ **品質ツール統合済み**:
  - ESLint (Airbnbスタイルガイド準拠)
  - Prettier (コードフォーマット)
  - Jest (テストカバレッジ80%推奨)
- ✅ **CI/CD設定完備**:
  - GitHub Actions CI/CD
- ✅ **Docker設定**
- ✅ **レイヤーアーキテクチャ** (Controller/Service/Repository)

**使用方法**:
```bash
# 1. テンプレートをコピー
cp -r typescript-express-api-template/ /your/project/path/

# 2. READMEの指示に従ってセットアップ
cd /your/project/path/
cat README.md

# 3. 依存関係インストール
npm install

# 4. プロジェクト固有の情報に更新
# - package.json (name, version, description)
# - .env.example → .env
```

**詳細**: [README.md](./typescript-express-api-template/README.md)

**参照ドキュメント**:
- `/organization-standards/01-coding-standards/typescript/`
- `/organization-standards/08-templates/ci-templates/typescript-node/`

---

## 🚀 使用タイミング

### Phase 1: プロジェクト初期化 (Step 1.1)
新規プロジェクト作成時に該当するテンプレートを使用

**推奨される使用ケース**:
- 新規APIプロジェクトの立ち上げ
- マイクロサービスの追加
- 既存プロジェクトのリファクタリング
- 学習・検証用プロジェクト

---

## 📊 効果

### プロジェクト立ち上げ時間の短縮

| 作業項目 | 従来 | テンプレート使用時 | 削減率 |
|---------|------|------------------|-------|
| **プロジェクト構造作成** | 2-3時間 | 5分 | **-97%** |
| **認証実装** | 1日 | 0分（実装済み） | **-100%** |
| **品質ツール設定** | 4-6時間 | 0分（設定済み） | **-100%** |
| **CI/CD設定** | 1-2日 | 0分（設定済み） | **-100%** |
| **Docker設定** | 2-4時間 | 0分（設定済み） | **-100%** |
| **合計** | **2-3日** | **2-3時間** | **-90%** |

### 品質保証

- ✅ **organization-standards準拠率**: 100%保証
- ✅ **テストカバレッジ**: 80%以上強制
- ✅ **コーディング規約**: 自動チェック
- ✅ **CI/CD**: 即座に動作可能
- ✅ **セキュリティ**: ベストプラクティス実装済み

---

## 🔧 カスタマイズガイド

### 最小限の変更で開始

1. **プロジェクト名の変更**
   - `build.gradle` / `package.json`
   - `README.md`

2. **データベース設定**
   - `application.yml` / `.env`

3. **パッケージ名/モジュール名の変更**
   - Javaの場合: `com.organization` → `com.yourcompany`
   - TypeScriptの場合: モジュール名

4. **CI/CD環境変数の設定**
   - GitHub Secrets / GitLab CI Variables

### 段階的な拡張

テンプレートをベースに、プロジェクト固有の機能を追加:
- ビジネスロジック
- 追加のエンドポイント
- カスタム認証・認可ロジック
- サードパーティ統合

---

## 📚 関連ドキュメント

### テンプレート索引
- [TEMPLATE-INDEX.md](../TEMPLATE-INDEX.md) - テンプレート索引
- [08-templates/README.md](../README.md) - テンプレート総合ガイド

### プロセスガイド
- [/00-guides/phase-guides/phase-1-project-initialization-guide.md](/00-guides/phase-guides/phase-1-project-initialization-guide.md) - プロジェクト初期化ガイド

### コーディング標準
- [/01-coding-standards/java/](/01-coding-standards/java/) - Java標準
- [/01-coding-standards/typescript/](/01-coding-standards/typescript/) - TypeScript標準

### CI/CD標準
- [/08-templates/ci-templates/](/08-templates/ci-templates/) - CI設定テンプレート
- [/03-development-process/ci-cd-pipeline.md](/03-development-process/ci-cd-pipeline.md) - CI/CD標準

---

## 🆘 トラブルシューティング

### Q: テンプレートをコピーしたがビルドエラーが出る

A: 以下を確認してください:
1. Java/Node.jsのバージョンが要件を満たしているか
2. 依存関係が正しくインストールされているか
3. 環境変数が設定されているか

### Q: CI/CDが動作しない

A: 以下を確認してください:
1. GitHub Secrets / GitLab CI Variablesが設定されているか
2. `.github/workflows/` ディレクトリが正しくコピーされているか
3. ブランチ保護ルールが設定されているか

### Q: 既存プロジェクトに適用したい

A: 段階的な移行を推奨します:
1. 新しいブランチでテンプレートをコピー
2. 既存コードを新しい構造に移行
3. テストを実行して動作確認
4. CI/CD設定を移行

---

## 📝 フィードバック

テンプレートの改善提案やバグ報告は、organization-standards管理者までご連絡ください。

---

**最終更新日**: 2025-11-20  
**バージョン**: 1.0.0  
**ステータス**: ✅ 利用可能
