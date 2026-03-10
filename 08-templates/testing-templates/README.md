---
title: "テストテンプレート"
version: "2.0.0"
created_date: "2025-11-05"
updated_date: "2025-11-18"
status: "Active"
---

# testing-templates - テストテンプレート

> Phase 3統合テスト実装用テンプレート

**ディレクトリパス**: `/devin-organization-standards/08-templates/testing-templates/`  
**ステータス**: 🟢 アクティブ

---

## 📖 概要

このディレクトリには、Phase 3（実装フェーズ）における統合テスト実装のためのテンプレートファイルが格納されています。

これらのテンプレートは、以下のドキュメントと連携して使用されます：

- [統合テスト実装ガイド](../../03-development-process/testing-standards/04-integration-testing-implementation.md)
- [PBIタイプ別テスト要件マトリックス](../../03-development-process/testing-standards/01-strategy-pbi-matrix.md)

---

## 📁 ファイル一覧

### 1. integration-test-template.py

**言語**: Python  
**フレームワーク**: FastAPI/Flask + pytest + TestContainers  
**サイズ**: 9.2KB

**用途**:
- Python プロジェクトの統合テストテンプレート
- APIエンドポイントテスト
- データベースCRUD操作テスト
- トランザクション処理テスト

**特徴**:
- TestContainers を使用したデータベースコンテナのセットアップ
- AAA（Arrange-Act-Assert）パターン
- 日本語コメント付き
- 段階的にコメントアウト解除して実装可能

**使用方法**:
```bash
# テンプレートをコピー
cp integration-test-template.py tests/integration/test_your_feature.py

# 必要に応じて編集
# 1. テスト対象のAPIエンドポイントに合わせて修正
# 2. コメントアウトされたテストを段階的に有効化
# 3. プロジェクト固有のモデル名に修正
```

---

### 2. integration-test-template.test.ts

**言語**: TypeScript  
**フレームワーク**: Express/NestJS + Jest + Supertest + TestContainers  
**サイズ**: 8.0KB

**用途**:
- TypeScript/Node.js プロジェクトの統合テストテンプレート
- RESTful APIエンドポイントテスト
- データベース操作テスト
- 認証・認可テスト

**特徴**:
- TestContainers Node.js版の使用
- Supertest を使用したHTTPテスト
- TypeORM との統合例
- AAA（Arrange-Act-Assert）パターン
- 日本語コメント付き

**使用方法**:
```bash
# テンプレートをコピー
cp integration-test-template.test.ts tests/integration/your-feature.test.ts

# 必要に応じて編集
# 1. テスト対象のAPIエンドポイントに合わせて修正
# 2. コメントアウトされたテストを段階的に有効化
# 3. プロジェクト固有のエンティティ名に修正
```

---

### 3. IntegrationTestTemplate.java

**言語**: Java  
**フレームワーク**: Spring Boot + JUnit 5 + TestContainers + MockMvc  
**サイズ**: 18KB

**用途**:
- Java/Spring Boot プロジェクトの統合テストテンプレート
- REST APIエンドポイントテスト（MockMvc使用）
- データベース操作テスト（JPA/Hibernate）
- トランザクション処理テスト
- 認証・認可テスト

**特徴**:
- TestContainers による完全なコンテナ環境
- Spring Boot Test の自動設定活用
- MockMvc による高速なHTTPテスト
- @Transactional による自動ロールバック
- AssertJ による流暢なアサーション
- 日本語コメント付き

**使用方法**:
```bash
# テンプレートをコピー
cp IntegrationTestTemplate.java src/test/java/com/yourcompany/integration/YourFeatureIntegrationTest.java

# 必要に応じて編集
# 1. パッケージ名を変更
# 2. テスト対象のAPIエンドポイントに合わせて修正
# 3. コメントアウト（@Disabled）されたテストを段階的に有効化
# 4. プロジェクト固有のエンティティ・DTOに修正
```

**依存関係の追加**:

Maven (`pom.xml`):
```xml
<dependencies>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>testcontainers</artifactId>
        <version>1.19.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <version>1.19.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>1.19.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

Gradle (`build.gradle`):
```gradle
dependencies {
    testImplementation 'org.testcontainers:testcontainers:1.19.0'
    testImplementation 'org.testcontainers:postgresql:1.19.0'
    testImplementation 'org.testcontainers:junit-jupiter:1.19.0'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

test {
    useJUnitPlatform()
}
```

---

## 🎯 テンプレート選択ガイド

| プロジェクトタイプ | 推奨テンプレート | 備考 |
|-----------------|----------------|------|
| FastAPI | integration-test-template.py | Python 3.9以上推奨 |
| Flask | integration-test-template.py | pytest使用 |
| Express | integration-test-template.test.ts | Node.js 16以上推奨 |
| NestJS | integration-test-template.test.ts | TypeScript 4.5以上推奨 |
| Spring Boot | IntegrationTestTemplate.java | Java 17以上推奨 |
| Spring MVC | IntegrationTestTemplate.java | Spring Boot 3.x推奨 |

---

## 📝 使用上の注意事項

### 共通事項

1. **Docker必須**: すべてのテンプレートがTestContainersを使用するため、Docker環境が必要です
2. **環境変数**: テスト用の環境変数設定が必要な場合があります
3. **実行時間**: コンテナ起動時間を考慮し、タイムアウト設定を調整してください
4. **段階的実装**: コメントアウトされたテストは、機能実装に合わせて段階的に有効化してください

### 言語別の注意事項

#### Python
- pytest 7.0以上が必要
- `testcontainers-python` パッケージをインストール
- 非同期テスト（`@pytest.mark.asyncio`）を使用する場合は `pytest-asyncio` も必要

#### TypeScript
- Jest 29.0以上が必要
- `testcontainers` パッケージをインストール
- TypeORMの場合、テスト用データソース設定が必要

#### Java
- JDK 17以上推奨
- Spring Boot 3.x系推奨
- TestContainersのDockerイメージダウンロードに時間がかかる場合があります

---

## 🔗 関連ドキュメント

### 必読ドキュメント

1. [統合テスト実装ガイド（Phase 3用）](../../03-development-process/testing-standards/04-integration-testing-implementation.md)
   - TestContainersの詳細な導入手順
   - トラブルシューティングガイド
   - CI/CD統合手順

2. [PBIタイプ別テスト要件マトリックス](../../03-development-process/testing-standards/01-strategy-pbi-matrix.md)
   - Phase 3完了基準チェックリスト
   - 統合テスト実施手順
   - FAQ

### 参考ドキュメント

3. [AI-MASTER-WORKFLOW-GUIDE](../../00-guides/02-ai-guides/audit-log/AI-MASTER-WORKFLOW-GUIDE.md)
4. [Integration Testing Standards](../../04-quality-standards/integration-testing.md)
5. [Testing Strategy](../../04-quality-standards/testing-strategy.md)

---

## 🚀 クイックスタート

### Python プロジェクトの場合

```bash
# 1. テンプレートをコピー
cp /path/to/integration-test-template.py tests/integration/test_api.py

# 2. 依存関係をインストール
pip install pytest pytest-asyncio testcontainers httpx

# 3. テストを実行
pytest tests/integration/test_api.py -v
```

### TypeScript プロジェクトの場合

```bash
# 1. テンプレートをコピー
cp /path/to/integration-test-template.test.ts tests/integration/api.test.ts

# 2. 依存関係をインストール
npm install --save-dev jest @types/jest supertest @types/supertest testcontainers

# 3. テストを実行
npm test -- tests/integration/api.test.ts
```

### Java プロジェクトの場合

```bash
# 1. テンプレートをコピー
cp /path/to/IntegrationTestTemplate.java src/test/java/com/example/integration/ApiIntegrationTest.java

# 2. 依存関係を追加（pom.xmlまたはbuild.gradleに記載）

# 3. テストを実行
# Maven
mvn test -Dtest=ApiIntegrationTest

# Gradle
./gradlew test --tests ApiIntegrationTest
```

---

## 💡 ベストプラクティス

### 1. テスト分離

各テストケースは独立して実行できるようにしてください：
- データベースの状態をテスト前にクリーンアップ
- `@Transactional`（Java）や `db_session.rollback()`（Python）を活用

### 2. テストデータ管理

テストデータは明示的に作成し、予測可能にしてください：
- ハードコードされたテストデータを使用
- ファクトリーパターンの活用も検討

### 3. アサーションの明確化

何をテストしているか明確にしてください：
- 1つのテストで1つの概念をテスト
- エラーメッセージを含めたアサーション

### 4. パフォーマンス

統合テストの実行時間を意識してください：
- 必要最小限のテストデータ
- コンテナの再利用（セッションスコープ）
- 並列実行の活用

---

## 🐛 トラブルシューティング

### よくある問題

1. **Docker接続エラー**
   - Dockerデーモンが起動しているか確認
   - Docker Desktopの場合、設定を確認

2. **ポート競合**
   - TestContainersがランダムポートを割り当てることを確認
   - 固定ポート使用を避ける

3. **タイムアウトエラー**
   - コンテナ起動に時間がかかる場合、タイムアウト値を増やす
   - ネットワーク環境を確認

詳細は [統合テスト実装ガイドのトラブルシューティングセクション](../../03-development-process/testing-standards/04-integration-testing-implementation.md#トラブルシューティング) を参照してください。

---

## 📞 サポート

質問や問題がある場合は、以下に連絡してください：

- **技術的な質問**: テクニカルリードに相談
- **ドキュメントの改善提案**: GitHubのIssueまたはPRで提案
- **テンプレートの改善提案**: Engineering Leadership Teamに提案

---

## 📈 更新履歴

| バージョン | 日付 | 変更内容 |
|----------|------|---------|
| 2.0.0 | 2025-11-18 | Java統合テストテンプレート追加、ドキュメント全面改訂 |
| 1.1.0 | 2025-11-18 | Python/TypeScript統合テストテンプレート追加 |
| 1.0.0 | 2025-11-05 | 初版作成（将来の拡張用として予約） |

---

**Last Updated**: 2025-11-18  
**Version**: 2.0.0  
**Status**: 🟢 Active  
**Maintained by**: Engineering Leadership Team
