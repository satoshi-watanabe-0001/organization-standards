# Devin Guidelines & Documentation Standards

**説明**: Devin実行ガイドライン、ドキュメンテーション標準

**主要トピック**:
- Devin向け実装指示
- 自動コード生成指針
- 品質保証チェックリスト
- Javadoc標準形式
- パッケージ、クラス、メソッドのドキュメンテーション
- README、API仕様書の作成

---

## 11. Devin実行ガイドライン

### 11.1 自動コード生成指針

#### **Devin向け実装指示**
```java
/**
 * Devin実行ガイドライン：Javaコード生成時の必須チェックリスト
 * 
 * 【基本原則】
 * 1. 必ずSpring Boot 3.x + Java 17以上を使用すること
 * 2. 全てのクラスにLombokアノテーションを適用すること
 * 3. Spring Securityによる認証・認可を実装すること
 * 4. JPA/Hibernateを使用したデータアクセス層を構築すること
 * 5. 包括的な例外処理とログ出力を含めること
 * 
 * 【必須実装項目】
 * ✅ エンティティクラス（JPA）
 * ✅ Repositoryインターフェース（Spring Data JPA）
 * ✅ Serviceクラス（ビジネスロジック）
 * ✅ Controllerクラス（REST API）
 * ✅ DTOクラス（リクエスト・レスポンス）
 * ✅ 例外クラス（カスタム例外）
 * ✅ テストクラス（Unit + Integration）
 * ✅ 設定クラス（Security, Cache, Async等）
 * 
 * 【コード品質チェック】
 * - Checkstyleルールに準拠すること
 * - 80%以上のテストカバレッジを確保すること
 * - SonarQubeの品質ゲートをパスすること
 * - セキュリティ脆弱性がないこと
 */

// ✅ Devin実行例：ユーザー管理システムの完全実装
/**
 * INSTRUCTION FOR DEVIN:
 * Generate a complete user management system with the following requirements:
 * 
 * ENTITIES:
 * - User (id, email, firstName, lastName, passwordHash, status, createdAt, updatedAt)
 * - Role (id, name, description)
 * - UserRole (userId, roleId) - Many-to-Many relationship
 * 
 * BUSINESS REQUIREMENTS:
 * - User registration with email verification
 * - JWT-based authentication and authorization
 * - Role-based access control
 * - Password hashing with BCrypt
 * - Account activation/deactivation
 * - Audit trail for security events
 * 
 * API ENDPOINTS:
 * - POST /api/v1/auth/register - User registration
 * - POST /api/v1/auth/login - User login
 * - POST /api/v1/auth/refresh - Token refresh
 * - GET /api/v1/users/{id} - Get user by ID
 * - PUT /api/v1/users/{id} - Update user
 * - DELETE /api/v1/users/{id} - Delete user
 * - GET /api/v1/users - List users with pagination
 * 
 * VALIDATION RULES:
 * - Email must be valid and unique
 * - Password must be at least 8 characters with special chars
 * - First name and last name are required (1-50 characters)
 * - Roles must exist in the system
 * 
 * SECURITY REQUIREMENTS:
 * - All endpoints except registration/login require authentication
 * - Users can only access their own data unless they have ADMIN role
 * - Admin users can manage all users
 * - JWT tokens expire after 1 hour
 * - Refresh tokens expire after 7 days
 * 
 * TEST REQUIREMENTS:
 * - Unit tests for all service methods
 * - Integration tests for all API endpoints
 * - Security tests for authorization rules
 * - Test data builders for clean test setup
 * 
 * PERFORMANCE REQUIREMENTS:
 * - Use appropriate database indexes
 * - Implement caching for frequently accessed data
 * - Use pagination for list endpoints
 * - Optimize N+1 query problems
 * 
 * MONITORING REQUIREMENTS:
 * - Structured logging with correlation IDs
 * - Custom metrics for business events
 * - Health checks for dependencies
 * - Security audit logging
 */
```

### 11.2 品質保証チェックリスト

#### **コード生成後の必須確認項目**
```java
/**
 * Devin生成コード品質チェックリスト
 * 
 * 【アーキテクチャ確認】
 * □ レイヤー分離が適切に実装されているか
 * □ 依存性注入がコンストラクタベースで実装されているか
 * □ 単一責任原則が守られているか
 * □ インターフェースと実装が適切に分離されているか
 * 
 * 【セキュリティ確認】
 * □ SQLインジェクション対策が実装されているか
 * □ XSS対策が実装されているか
 * □ 認証・認可が適切に実装されているか
 * □ 機密データが暗号化されているか
 * □ セキュリティヘッダーが設定されているか
 * 
 * 【パフォーマンス確認】
 * □ データベースインデックスが適切に設定されているか
 * □ N+1問題が解決されているか
 * □ キャッシュが適切に実装されているか
 * □ ページネーションが実装されているか
 * □ 非同期処理が適切に使用されているか
 * 
 * 【テスト確認】
 * □ 単体テストが全てのメソッドをカバーしているか
 * □ 統合テストがAPIエンドポイントをカバーしているか
 * □ エラーケースのテストが含まれているか
 * □ セキュリティテストが含まれているか
 * □ テストデータの構築が適切か
 * 
 * 【ログ・監視確認】
 * □ 構造化ログが適切に出力されているか
 * □ エラーログが十分な情報を含んでいるか
 * □ パフォーマンスメトリクスが収集されているか
 * □ セキュリティイベントがログ出力されているか
 * 
 * 【設定・運用確認】
 * □ 環境別設定が適切に分離されているか
 * □ Dockerfileが最適化されているか
 * □ ヘルスチェックが実装されているか
 * □ CI/CDパイプラインが設定されているか
 */

// 自動検証スクリプト（Devin実行後の品質チェック）
public class CodeQualityValidator {
    
    public void validateGeneratedCode(String projectPath) {
        // 1. アーキテクチャ検証
        validateArchitecture(projectPath);
        
        // 2. セキュリティ検証
        validateSecurity(projectPath);
        
        // 3. パフォーマンス検証
        validatePerformance(projectPath);
        
        // 4. テスト検証
        validateTests(projectPath);
        
        // 5. 設定検証
        validateConfiguration(projectPath);
    }
    
    private void validateArchitecture(String projectPath) {
        // レイヤー分離チェック
        // 依存関係チェック
        // 命名規則チェック
    }
    
    private void validateSecurity(String projectPath) {
        // セキュリティアノテーションチェック
        // SQLインジェクション脆弱性チェック
        // 認証・認可実装チェック
    }
    
    private void validatePerformance(String projectPath) {
        // データベースクエリ効率チェック
        // キャッシュ実装チェック
        // 非同期処理チェック
    }
    
    private void validateTests(String projectPath) {
        // テストカバレッジチェック
        // テスト品質チェック
        // モックの適切な使用チェック
    }
    
    private void validateConfiguration(String projectPath) {
        // 設定ファイル妥当性チェック
        // 環境変数使用チェック
        // Docker設定チェック
    }
}
```

### 11.3 継続的改善プロセス

#### **Devin学習・改善指針**
```java
/**
 * Devin継続的改善プロセス
 * 
 * 【フィードバックループ】
 * 1. 生成コード品質メトリクス収集
 * 2. 実装パターンの分析と最適化
 * 3. よくある問題の特定と対策
 * 4. ベストプラクティスの更新
 * 
 * 【品質向上サイクル】
 * Week 1: コード生成とレビュー
 * Week 2: テスト実行と品質測定
 * Week 3: 問題点の特定と分析
 * Week 4: 改善策の実装と検証
 * 
 * 【メトリクス指標】
 * - コード品質スコア（SonarQube）
 * - テストカバレッジ率
 * - セキュリティ脆弱性数
 * - パフォーマンステスト結果
 * - デプロイ成功率
 */

@Component
@Slf4j
public class DevinQualityMetrics {
    
    @EventListener
    public void onCodeGenerated(CodeGeneratedEvent event) {
        // 生成コードの品質メトリクス収集
        collectQualityMetrics(event.getGeneratedCode());
        
        // 改善提案の生成
        generateImprovementSuggestions(event);
    }
    
    private void collectQualityMetrics(GeneratedCode code) {
        QualityMetrics metrics = QualityMetrics.builder()
                .codeComplexity(calculateComplexity(code))
                .testCoverage(calculateTestCoverage(code))
                .securityScore(calculateSecurityScore(code))
                .performanceScore(calculatePerformanceScore(code))
                .build();
        
        // メトリクスの永続化と分析
        metricsRepository.save(metrics);
        
        log.info("Quality metrics collected: {}", metrics);
    }
}
```

---

**Java規約ドキュメント完成**

このJavaコーディング規約は、以下の章構成で包括的に作成されました：

1. **基本設定・ツール設定** - 開発環境とツールチェーンの標準化
2. **命名規則・スタイル** - 一貫した命名とフォーマット規則
3. **クラス設計・構造化** - SOLID原則に基づく設計パターン
4. **エラーハンドリング・例外処理** - 堅牢な例外処理戦略
5. **バリデーション・セキュリティ** - 入力検証とセキュリティ対策
6. **テスト戦略・品質保証** - 包括的なテスト実装方針
7. **パフォーマンス最適化** - データベースとアプリケーション最適化
8. **セキュリティ・監査** - 認証・認可と監査ログシステム
9. **監視・ロギング** - 構造化ログとメトリクス収集
10. **デプロイメント・運用** - Docker化とCI/CD設定
11. **Devin実行ガイドライン** - AI向けの具体的実装指示

既存の`css-styling-standards.md`と同等の詳細度・品質で作成し、実践的なコード例（Good/Bad対比）、具体的な設定ファイル、包括的なベストプラクティスを含めています。

# Java ドキュメンテーション標準追加セクション

---


## X. ドキュメンテーション標準（Documentation Standards）

### X.1 Javadoc 必須要件

#### **適用範囲**

**Level 1: 必須（品質ゲート）**
- すべてのパッケージ（`package-info.java`）
- すべてのパブリッククラス・インターフェース
- すべてのパブリックメソッド
- すべてのパブリックフィールド（定数）

**Level 2: 強く推奨**
- protectedメソッド
- 複雑な内部ロジック（循環的複雑度10以上）
- ビジネスルール・制約を反映した実装
- 非自明な実装（パフォーマンス最適化、技術的回避策）

**Level 3: 任意**
- 単純なゲッター/セッター
- 自己説明的なprivateメソッド

---

### X.2 Javadoc 標準形式

#### **パッケージDocumentation（必須: package-info.java）**

```java
/**
 * ユーザー認証パッケージ
 * 
 * <p>ユーザーのログイン・ログアウト・トークン検証機能を提供する。
 * JWTベースの認証を実装し、リフレッシュトークンをサポート。</p>
 * 
 * <p>主なクラス:</p>
 * <ul>
 *   <li>{@link com.example.auth.AuthService} - 認証サービスのメインクラス</li>
 *   <li>{@link com.example.auth.TokenManager} - トークン管理クラス</li>
 * </ul>
 * 
 * <p>典型的な使用例:</p>
 * <pre>{@code
 * AuthService authService = new AuthService();
 * TokenPair tokens = authService.login("user@example.com", "password");
 * }</pre>
 * 
 * @since 1.0
 */
package com.example.auth;
```

**必須要素**:
- パッケージの目的・責任の説明
- `@since`: バージョン情報

**推奨要素**:
- 主なクラスへのリンク（`{@link}`）
- 典型的な使用例（`<pre>{@code}`）

---

#### **クラス・インターフェース Javadoc（必須）**

```java
/**
 * ユーザー認証サービスクラス
 * 
 * <p>JWTトークンの生成・検証、ユーザーセッション管理を担当する。
 * シングルトンパターンで実装され、アプリケーション全体で共有される。</p>
 * 
 * <p>このクラスはスレッドセーフであり、マルチスレッド環境で安全に使用できる。</p>
 * 
 * <p>使用例:</p>
 * <pre>{@code
 * AuthService authService = AuthService.getInstance();
 * TokenPair tokens = authService.login(credentials);
 * System.out.println(tokens.getAccessToken());
 * }</pre>
 * 
 * @author システム開発チーム
 * @version 1.0
 * @since 1.0
 * @see TokenManager
 * @see LoginCredentials
 */
public class AuthService {
    // ...
}
```

**必須要素**:
- クラス/インターフェースの目的と責任
- `@since`: 導入バージョン

**推奨要素**:
- `@author`: 作成者（オプション、Gitで管理も可）
- `@version`: バージョン情報
- `@see`: 関連クラスへのリンク
- 使用例（`<pre>{@code}`）
- スレッドセーフ性などの重要な特性

---

#### **メソッド Javadoc（パブリックAPIは必須）**

```java
/**
 * ユーザーをログインさせ、JWTトークンを発行する
 * 
 * <p>認証情報を検証し、成功した場合はアクセストークンと
 * リフレッシュトークンのペアを返す。</p>
 * 
 * <p>トランザクション内で以下を実行:</p>
 * <ol>
 *   <li>認証情報の検証</li>
 *   <li>ユーザーセッションの作成</li>
 *   <li>JWTトークンの生成</li>
 * </ol>
 * 
 * @param credentials ユーザーの認証情報。nullは許可されない。
 * @return アクセストークンとリフレッシュトークンを含む {@link TokenPair} オブジェクト
 * @throws AuthenticationException 認証情報が不正な場合
 * @throws NetworkException 認証サーバーへの接続に失敗した場合
 * @throws IllegalArgumentException credentialsがnullの場合
 * 
 * @see LoginCredentials
 * @see TokenPair
 * @since 1.0
 */
public TokenPair login(@NonNull LoginCredentials credentials) 
        throws AuthenticationException, NetworkException {
    // 実装...
}
```

**必須要素**:
- メソッドの目的・動作の説明
- `@param`: すべてのパラメータの説明（null許可の可否を明記）
- `@return`: 戻り値の説明（voidの場合は不要）
- `@throws`: 発生する可能性のあるすべての例外

**推奨要素**:
- `@see`: 関連クラス・メソッドへのリンク
- `@since`: 導入バージョン
- `@deprecated`: 非推奨の場合（代替手段を明記）

---

#### **コンストラクタ Javadoc（必須）**

```java
/**
 * 指定された設定でAuthServiceを初期化する
 * 
 * @param tokenExpiration トークン有効期限（秒）。正の整数でなければならない。
 * @param refreshEnabled リフレッシュトークン機能の有効化フラグ
 * @throws IllegalArgumentException tokenExpirationが0以下の場合
 * @since 1.0
 */
public AuthService(int tokenExpiration, boolean refreshEnabled) {
    // 実装...
}
```

---

#### **フィールド・定数 Javadoc（パブリックは必須）**

```java
/**
 * JWT トークンの有効期限設定
 * 
 * <p>セキュリティ要件に基づき、短期間のアクセストークンと
 * 長期間のリフレッシュトークンを使い分ける。</p>
 * 
 * @since 1.0
 */
public static final class TokenExpiration {
    /** アクセストークン有効期限（15分、単位: ミリ秒） */
    public static final long ACCESS_TOKEN = 15 * 60 * 1000L;
    
    /** リフレッシュトークン有効期限（7日、単位: ミリ秒） */
    public static final long REFRESH_TOKEN = 7 * 24 * 60 * 60 * 1000L;
}
```

---

#### **列挙型（Enum）Javadoc**

```java
/**
 * ユーザーの認証ステータスを表す列挙型
 * 
 * @since 1.0
 */
public enum AuthStatus {
    /** 未認証状態 */
    UNAUTHENTICATED,
    
    /** 認証済み、アクティブ */
    AUTHENTICATED,
    
    /** トークン有効期限切れ */
    EXPIRED,
    
    /** 強制ログアウト済み */
    REVOKED
}
```

---

### X.3 CheckStyle による自動チェック

#### **推奨 CheckStyle 設定（checkstyle.xml）**

```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
    "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
    "https://checkstyle.org/dtds/configuration_1_3.dtd">

<module name="Checker">
    <module name="TreeWalker">
        <!-- Javadoc 必須化 -->
        <module name="MissingJavadocMethod">
            <property name="scope" value="public"/>
            <property name="allowMissingPropertyJavadoc" value="true"/>
            <property name="tokens" value="METHOD_DEF, CTOR_DEF"/>
        </module>
        
        <module name="MissingJavadocType">
            <property name="scope" value="public"/>
        </module>
        
        <module name="MissingJavadocPackage"/>
        
        <!-- Javadoc 品質チェック -->
        <module name="JavadocMethod">
            <property name="validateThrows" value="true"/>
            <property name="validateReturnTag" value="true"/>
        </module>
        
        <module name="JavadocType"/>
        
        <module name="JavadocStyle">
            <property name="checkFirstSentence" value="true"/>
            <property name="checkEmptyJavadoc" value="true"/>
        </module>
        
        <!-- パラメータ・戻り値のドキュメント -->
        <module name="JavadocParagraph"/>
        <module name="JavadocTagContinuationIndentation"/>
        <module name="NonEmptyAtclauseDescription"/>
        <module name="SingleLineJavadoc"/>
    </module>
</module>
```

#### **Maven 統合（pom.xml）**

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.0</version>
    <configuration>
        <configLocation>checkstyle.xml</configLocation>
        <failOnViolation>true</failOnViolation>
        <violationSeverity>warning</violationSeverity>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### **Gradle 統合（build.gradle）**

```groovy
plugins {
    id 'checkstyle'
}

checkstyle {
    toolVersion = '10.12.0'
    configFile = file("${rootDir}/checkstyle.xml")
}

tasks.withType(Checkstyle) {
    reports {
        xml.required = true
        html.required = true
    }
}
```

---

### X.4 Javadoc 生成

#### **Maven Javadoc Plugin**

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <version>3.5.0</version>
    <configuration>
        <show>public</show>
        <nohelp>true</nohelp>
        <charset>UTF-8</charset>
        <encoding>UTF-8</encoding>
        <docencoding>UTF-8</docencoding>
        <links>
            <link>https://docs.oracle.com/en/java/javase/17/docs/api/</link>
        </links>
    </configuration>
    <executions>
        <execution>
            <id>attach-javadocs</id>
            <goals>
                <goal>jar</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**生成コマンド**:
```bash
mvn javadoc:javadoc
```

#### **Gradle Javadoc Task**

```groovy
javadoc {
    options.encoding = 'UTF-8'
    options.charSet = 'UTF-8'
    options.author = true
    options.version = true
    options.links("https://docs.oracle.com/en/java/javase/17/docs/api/")
}

task javadocJar(type: Jar) {
    classifier = 'javadoc'
    from javadoc
}
```

**生成コマンド**:
```bash
./gradlew javadoc
```

---

### X.5 コードレビューチェックリスト

**レビュアーは以下を確認**:

#### **必須項目**
- [ ] パッケージに `package-info.java` が存在するか
- [ ] パブリッククラス・インターフェースすべてに Javadoc があるか
- [ ] パブリックメソッドすべてに Javadoc があるか
- [ ] `@param`、`@return`、`@throws` が適切に記載されているか

#### **品質項目**
- [ ] 「何を」だけでなく「なぜ」が説明されているか
- [ ] ビジネスルール・制約が明記されているか
- [ ] null許可の可否が明記されているか（`@NonNull`、`@Nullable`）
- [ ] 複雑なロジック（複雑度10以上）にコメントがあるか
- [ ] 使用例が必要な複雑なAPIに `<pre>{@code}` があるか

#### **エラー検出**
- [ ] CheckStyle で Javadoc ルール違反がないか
- [ ] Javadoc生成でエラー・警告がないか

---

### X.6 ベストプラクティス

#### **✅ Good Examples**

```java
/**
 * 注文処理サービス
 * 
 * <p>注文の作成・更新・キャンセル処理を担当する。
 * 在庫管理システムとの連携により、在庫確認・引き当てを実行。</p>
 * 
 * <p>このクラスはスレッドセーフである。</p>
 * 
 * @since 1.0
 */
public class OrderService {
    
    /**
     * 注文を作成し、在庫を引き当てる
     * 
     * <p>トランザクション内で以下を実行:</p>
     * <ol>
     *   <li>在庫の可用性チェック</li>
     *   <li>注文レコードの作成</li>
     *   <li>在庫の引き当て</li>
     * </ol>
     * 
     * <p>いずれかの処理が失敗した場合、すべてロールバックされる。</p>
     * 
     * @param orderData 注文データ。nullは許可されない。
     * @return 作成された注文オブジェクト
     * @throws InsufficientStockException 在庫不足の場合
     * @throws DatabaseException データベース操作に失敗した場合
     * @throws IllegalArgumentException orderDataがnullまたは不正な場合
     * @since 1.0
     */
    public Order createOrder(@NonNull OrderData orderData) 
            throws InsufficientStockException, DatabaseException {
        // 実装...
    }
}
```

#### **❌ Bad Examples**

```java
// ❌ Javadoc なし（パブリックメソッドは必須）
public void processPayment(PaymentData data) {
    // ...
}

// ❌ 「何を」の繰り返しのみ（「なぜ」がない）
/**
 * ユーザーを取得する
 * @param id ID
 * @return ユーザー
 */
public User getUser(String id) {
    // ...
}

// ❌ パラメータ・例外の説明不足
/**
 * データを処理する
 */
public void processData(Object data) throws Exception {
    // ...
}

// ❌ null許可の可否が不明確
/**
 * ユーザー名を設定する
 * @param name 名前
 */
public void setName(String name) {
    // nameがnullの場合の動作が不明
}
```

---

### X.7 特殊なタグの使用

#### **@deprecated タグ（非推奨API）**

```java
/**
 * レガシー認証メソッド（非推奨）
 * 
 * <p>このメソッドは非推奨であり、将来のバージョンで削除される予定です。
 * 代わりに {@link #authenticateWithToken(String)} を使用してください。</p>
 * 
 * @param username ユーザー名
 * @param password パスワード
 * @return 認証結果
 * @deprecated バージョン 2.0 以降、{@link #authenticateWithToken(String)} を使用
 * @see #authenticateWithToken(String)
 */
@Deprecated
public boolean authenticate(String username, String password) {
    // レガシー実装...
}
```

#### **{@link} タグ（クロスリファレンス）**

```java
/**
 * ユーザー情報を更新する
 * 
 * <p>更新前に {@link #validateUserData(UserData)} で検証を実行すること。</p>
 * 
 * @param userData ユーザーデータ
 * @see #validateUserData(UserData)
 * @see User
 */
public void updateUser(UserData userData) {
    // 実装...
}
```

#### **{@code} と <pre> タグ（コード例）**

```java
/**
 * JSON文字列をパースする
 * 
 * <p>使用例:</p>
 * <pre>{@code
 * String json = "{\"name\":\"John\",\"age\":30}";
 * User user = parser.parse(json, User.class);
 * System.out.println(user.getName()); // "John"
 * }</pre>
 * 
 * @param json JSON文字列
 * @param clazz パース先のクラス
 * @return パースされたオブジェクト
 */
public <T> T parse(String json, Class<T> clazz) {
    // 実装...
}
```

---

### X.8 まとめ

**必須ルール**:
1. すべてのパッケージに `package-info.java`
2. すべてのパブリッククラス・インターフェースに Javadoc
3. すべてのパブリックメソッドに完全な Javadoc（`@param`、`@return`、`@throws`）
4. CheckStyle による自動チェック

**推奨プラクティス**:
1. protectedメソッドにも Javadoc
2. 複雑なロジック（複雑度10以上）にインラインコメント
3. ビジネスルール・制約の明記
4. 使用例（`<pre>{@code}`）の提供
5. null許可の可否を明記（`@NonNull`、`@Nullable`）
6. 定期的な Javadoc 生成と確認

**コードレビューで不合格となる条件**:
- パブリックAPIに Javadoc がない
- `@param`、`@return`、`@throws` の記載漏れ
- CheckStyle ルール違反
- Javadoc 生成エラー

---

**このセクションを `java-standards.md` の末尾に追加してください。**

