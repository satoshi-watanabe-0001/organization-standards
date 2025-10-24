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
