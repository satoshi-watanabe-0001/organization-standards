# Spring Boot API Template

**Organization Standards準拠** Java 17 + Spring Boot 3.2 REST APIテンプレート

## 📋 概要

このテンプレートは、organization-standardsに完全準拠したSpring Boot APIプロジェクトのスターターキットです。
エンタープライズグレードのセキュリティ、品質管理ツール、ベストプラクティスを実装しています。

### 主な特徴

- ✅ **Java 17** + **Spring Boot 3.2.1**
- ✅ **JWT認証** (アクセストークン15分、リフレッシュトークン7日)
- ✅ **BCryptパスワードハッシュ化**
- ✅ **Gradle** ビルドシステム
- ✅ **Checkstyle** (Google Java Style準拠)
- ✅ **SpotBugs** (静的解析)
- ✅ **JaCoCo** (テストカバレッジ80%強制)
- ✅ **レイヤーアーキテクチャ** (Controller/Service/Repository)
- ✅ **グローバル例外ハンドリング**
- ✅ **JPA監査** (作成日時/更新日時自動記録)
- ✅ **H2/PostgreSQL** 対応

---

## 🚀 クイックスタート

### 前提条件

- Java 17以上
- Gradle 8.x (またはGradle Wrapper使用)
- PostgreSQL 15+ (本番環境)

### 1. プロジェクトのクローン

```bash
git clone <repository-url>
cd api-template
```

### 2. Gradle Wrapperの生成

```bash
gradle wrapper --gradle-version 8.5
```

### 3. ビルド

```bash
./gradlew clean build
```

### 4. アプリケーションの起動

```bash
# 開発環境（H2インメモリDB使用）
./gradlew bootRun

# 本番環境
./gradlew bootRun --args='--spring.profiles.active=prod'
```

### 5. ヘルスチェック

```bash
curl http://localhost:8080/actuator/health
```

---

## 📁 プロジェクト構造

```
src/
├── main/
│   ├── java/com/organization/api/
│   │   ├── config/              # 設定クラス
│   │   │   └── SecurityConfig.java
│   │   ├── controller/          # REST API エンドポイント
│   │   │   └── UserController.java
│   │   ├── dto/                 # データ転送オブジェクト
│   │   │   └── UserDto.java
│   │   ├── entity/              # JPAエンティティ
│   │   │   ├── BaseEntity.java
│   │   │   └── User.java
│   │   ├── exception/           # 例外クラス
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ResourceNotFoundException.java
│   │   ├── repository/          # データアクセス層
│   │   │   └── UserRepository.java
│   │   ├── security/            # セキュリティ関連
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── JwtTokenProvider.java
│   │   ├── service/             # ビジネスロジック層
│   │   │   └── UserService.java
│   │   └── Application.java     # メインクラス
│   └── resources/
│       └── application.yml      # アプリケーション設定
└── test/                        # テストコード
```

---

## 🔐 セキュリティ設定

### JWT認証

#### アクセストークン取得（ログイン）

```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

# レスポンス
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "expiresIn": 900
}
```

#### 認証済みAPIリクエスト

```bash
GET /api/v1/users/1
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

### 環境変数設定（本番環境）

```bash
# JWT秘密鍵（最低256ビット、Base64エンコード）
export JWT_SECRET=your-base64-encoded-secret-key-minimum-256-bits

# データベース接続
export DATABASE_URL=jdbc:postgresql://localhost:5432/apidb
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=your-database-password

# SSL設定
export SSL_ENABLED=true
export SSL_KEYSTORE=file:/path/to/keystore.p12
export SSL_KEYSTORE_PASSWORD=your-keystore-password
```

---

## 🧪 テスト

### ユニットテスト実行

```bash
./gradlew test
```

### カバレッジレポート生成

```bash
./gradlew jacocoTestReport

# レポート確認
open build/reports/jacoco/test/html/index.html
```

### カバレッジ検証（80%以上必須）

```bash
./gradlew jacocoTestCoverageVerification
```

---

## 📊 品質チェック

### Checkstyle実行（Google Java Style準拠）

```bash
./gradlew checkstyleMain checkstyleTest
```

### SpotBugs実行（静的解析）

```bash
./gradlew spotbugsMain spotbugsTest

# レポート確認
open build/reports/spotbugs/spotbugs.html
```

### 全品質チェック実行

```bash
./gradlew check
```

---

## 🛠️ API エンドポイント

### ユーザー管理

| メソッド | エンドポイント | 説明 | 権限 |
|---------|---------------|------|------|
| GET | `/api/v1/users` | 全ユーザー取得 | ADMIN |
| GET | `/api/v1/users/{id}` | ユーザー詳細取得 | USER |
| POST | `/api/v1/users` | ユーザー作成 | ADMIN |
| PUT | `/api/v1/users/{id}` | ユーザー更新 | USER |
| DELETE | `/api/v1/users/{id}` | ユーザー削除 | ADMIN |

### ヘルスチェック

| メソッド | エンドポイント | 説明 |
|---------|---------------|------|
| GET | `/actuator/health` | ヘルスステータス |
| GET | `/actuator/info` | アプリケーション情報 |
| GET | `/actuator/metrics` | メトリクス |

---

## 🗄️ データベース

### 開発環境（H2）

- URL: `jdbc:h2:mem:testdb`
- Console: http://localhost:8080/h2-console
- Username: `sa`
- Password: （空白）

### 本番環境（PostgreSQL）

```sql
-- データベース作成
CREATE DATABASE apidb;

-- ユーザーテーブル作成（自動生成される）
```

---

## 📝 開発ガイドライン

### コーディング規約

- **Java 17+** 必須
- **Google Java Style** 準拠
- **Checkstyle** 違反ゼロ必須
- **テストカバレッジ 80%以上** 必須

### レイヤー設計

1. **Controller層**: HTTP要求/応答処理、入力検証、Serviceへの委譲
2. **Service層**: ビジネスロジック、トランザクション管理
3. **Repository層**: データアクセスのみ

### 命名規則

- **クラス名**: UpperCamelCase (例: `UserController`)
- **メソッド名**: lowerCamelCase (例: `findUserById`)
- **定数**: UPPER_SNAKE_CASE (例: `MAX_RETRY_COUNT`)

---

## 🔄 CI/CD

### GitHub Actions設定例

```yaml
name: Build and Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Build with Gradle
        run: ./gradlew build
      - name: Run tests
        run: ./gradlew test
      - name: Check coverage
        run: ./gradlew jacocoTestCoverageVerification
```

---

## 📚 参考資料

### Organization Standards

- `/organization-standards/01-coding-standards/java/AI-QUICK-REFERENCE.md`
- `/organization-standards/07-security-compliance/authentication-authorization.md`

### 公式ドキュメント

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Gradle Documentation](https://docs.gradle.org/)

---

## 📄 ライセンス

MIT License

---

## 👥 開発者

Organization Development Team

---

## 🆘 サポート

問題が発生した場合は、以下を確認してください:

1. Java 17以上がインストールされているか
2. 環境変数が正しく設定されているか
3. データベース接続情報が正しいか
4. ポート8080が使用可能か

