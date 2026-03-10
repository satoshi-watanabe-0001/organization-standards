# AI-QUICK-REFERENCE: Java Coding Standards

**目的**: AIエージェント（Devin等）が3分で確認できる、Java開発の必須チェック項目TOP30  
**対象**: Java 17+、Spring Boot 3.0+  
**最終更新**: 2025-11-15

---

## ⚡ 必須チェック項目 TOP 30

### 📦 1. プロジェクトセットアップ（5項目）

#### ✅ 1. Java & Spring Bootバージョン
- **必須**: Java 17以上、Spring Boot 3.0以上
- **確認**: `build.gradle`または`pom.xml`
- **参照**: [01-introduction-setup.md](01-introduction-setup.md)

#### ✅ 2. Gradle設定
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.0.0'
    id 'io.spring.dependency-management' version '1.1.0'
    id 'checkstyle'
    id 'jacoco'
}
```
- **必須プラグイン**: checkstyle、jacoco、spotbugs
- **参照**: [01-introduction-setup.md](01-introduction-setup.md)

#### ✅ 3. Checkstyle設定
- **ルールセット**: Google Java Style Guide
- **設定ファイル**: `config/checkstyle/checkstyle.xml`
- **必須違反レベル**: `maxErrors=0`, `maxWarnings=10`
- **参照**: [01-introduction-setup.md](01-introduction-setup.md)

#### ✅ 4. JaCoCoカバレッジ基準
```gradle
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80  // 80%以上
            }
        }
    }
}
```
- **最小カバレッジ**: 80%
- **参照**: [05-testing-quality.md](05-testing-quality.md)

#### ✅ 5. 依存性注入（DI）の活用
- **必須**: コンストラクタインジェクション
- **禁止**: フィールドインジェクション（`@Autowired`フィールド）
- **参照**: [03-class-design-architecture.md](03-class-design-architecture.md)

---

### 🎯 2. 命名規則（5項目）

#### ✅ 6. パッケージ名
- **形式**: `com.company.project.module`（小文字のみ）
- **例**: `com.example.userservice.controller`
- **参照**: [02-naming-style.md](02-naming-style.md)

#### ✅ 7. クラス名
- **形式**: UpperCamelCase（パスカルケース）
- **例**: `UserController`, `UserService`, `UserRepository`
- **サフィックス**: Controller、Service、Repository等を明示
- **参照**: [02-naming-style.md](02-naming-style.md)

#### ✅ 8. メソッド名
- **形式**: lowerCamelCase（キャメルケース）
- **動詞で開始**: `getUserById`, `createUser`, `updateUser`, `deleteUser`
- **Boolean**: `isActive`, `hasPermission`, `canAccess`
- **参照**: [02-naming-style.md](02-naming-style.md)

#### ✅ 9. 変数名
- **形式**: lowerCamelCase
- **意味のある名前**: `userName`（○）、`name`（△）、`n`（×）
- **定数**: `UPPER_SNAKE_CASE`（例: `MAX_RETRY_COUNT`）
- **参照**: [02-naming-style.md](02-naming-style.md)

#### ✅ 10. Boolean変数名
- **推奨プレフィックス**: `is`, `has`, `can`, `should`
- **例**: `isActive`, `hasPermission`, `canEdit`, `shouldRetry`
- **参照**: [02-naming-style.md](02-naming-style.md)

---

### 🏗️ 3. クラス設計（5項目）

#### ✅ 11. 単一責任原則（SRP）
- **ルール**: 1クラス = 1責務
- **悪い例**: `UserController`にビジネスロジック、DB操作を混在
- **良い例**: Controller（入力検証）→ Service（ビジネスロジック）→ Repository（DB操作）
- **参照**: [03-class-design-architecture.md](03-class-design-architecture.md)

#### ✅ 12. Controller層の責務
```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        // 入力検証のみ、ビジネスロジックはServiceに委譲
    }
}
```
- **責務**: HTTP要求/応答処理、入力検証、Serviceへの委譲
- **禁止**: ビジネスロジック、DB操作
- **参照**: [03-class-design-architecture.md](03-class-design-architecture.md)

#### ✅ 13. Service層の責務
```java
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    
    public UserDto getUser(Long id) {
        // ビジネスロジック、トランザクション管理
    }
}
```
- **責務**: ビジネスロジック、トランザクション管理、他Serviceとの連携
- **禁止**: HTTP関連処理、直接的なDB操作
- **参照**: [03-class-design-architecture.md](03-class-design-architecture.md)

#### ✅ 14. Repository層の責務
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```
- **責務**: DB操作のみ
- **禁止**: ビジネスロジック
- **参照**: [03-class-design-architecture.md](03-class-design-architecture.md)

#### ✅ 15. DTO vs Entity
- **Entity**: DB永続化オブジェクト、内部のみ使用
- **DTO**: API入出力、レイヤー間データ転送
- **禁止**: EntityをAPIレスポンスとして直接返す
- **参照**: [03-class-design-architecture.md](03-class-design-architecture.md)

---

### 🚨 4. エラーハンドリング（5項目）

#### ✅ 16. カスタム例外の作成
```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s not found with id: %d", resource, id));
    }
}
```
- **必須**: `BusinessException`, `ResourceNotFoundException`, `ValidationException`
- **参照**: [04-error-handling-validation.md](04-error-handling-validation.md)

#### ✅ 17. グローバル例外ハンドラー
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
        ResourceNotFoundException ex) {
        // 統一されたエラーレスポンス
    }
}
```
- **必須**: `@RestControllerAdvice`による統一的な例外処理
- **参照**: [04-error-handling-validation.md](04-error-handling-validation.md)

#### ✅ 18. Bean Validationの活用
```java
public class CreateUserRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50)
    private String name;
    
    @Email(message = "Invalid email format")
    private String email;
}
```
- **必須**: `@Valid`, `@Validated`による入力検証
- **参照**: [04-error-handling-validation.md](04-error-handling-validation.md)

#### ✅ 19. セキュリティ対策（SQLインジェクション）
- **必須**: JPA、PreparedStatementの使用
- **禁止**: 文字列連結によるSQL構築
- **参照**: [04-error-handling-validation.md](04-error-handling-validation.md)

#### ✅ 20. セキュリティ対策（XSS）
- **必須**: 入力データのサニタイゼーション
- **ライブラリ**: OWASP Java HTML Sanitizer
- **参照**: [04-error-handling-validation.md](04-error-handling-validation.md)

---

### 🧪 5. テスト戦略（5項目）

#### ✅ 21. 単体テスト（Unit Test）
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void testGetUser() {
        // Arrange, Act, Assert
    }
}
```
- **必須**: JUnit 5、Mockito
- **カバレッジ**: 80%以上
- **参照**: [05-testing-quality.md](05-testing-quality.md)

#### ✅ 22. 統合テスト（Integration Test）
```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testGetUser() throws Exception {
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk());
    }
}
```
- **必須**: `@SpringBootTest`, `@AutoConfigureMockMvc`
- **参照**: [05-testing-quality.md](05-testing-quality.md)

#### ✅ 23. テストカバレッジ基準
- **最小**: 80%
- **測定**: JaCoCo
- **除外**: 設定クラス、DTOのgetterのみのクラス
- **参照**: [05-testing-quality.md](05-testing-quality.md)

#### ✅ 24. テストの構造化
- **パターン**: Arrange-Act-Assert（AAA）
- **命名**: `test<メソッド名>_<条件>_<期待結果>`
- **例**: `testGetUser_WhenUserExists_ReturnsUser`
- **参照**: [05-testing-quality.md](05-testing-quality.md)

#### ✅ 25. テストの独立性
- **ルール**: テスト間で状態を共有しない
- **必須**: `@BeforeEach`, `@AfterEach`で状態をリセット
- **参照**: [05-testing-quality.md](05-testing-quality.md)

---

### 🚀 6. パフォーマンス & 運用（5項目）

#### ✅ 26. JPA N+1問題対策
```java
@Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
Optional<User> findByIdWithOrders(@Param("id") Long id);
```
- **必須**: `JOIN FETCH`、`@EntityGraph`の活用
- **参照**: [06-performance-security-operations.md](06-performance-security-operations.md)

#### ✅ 27. キャッシング戦略
```java
@Cacheable(value = "users", key = "#id")
public UserDto getUser(Long id) {
    // キャッシュ対象メソッド
}
```
- **必須**: `@Cacheable`, `@CacheEvict`
- **推奨**: Redis
- **参照**: [06-performance-security-operations.md](06-performance-security-operations.md)

#### ✅ 28. 非同期処理
```java
@Async
public CompletableFuture<String> processAsync() {
    // 非同期処理
}
```
- **必須**: `@Async`, `CompletableFuture`
- **設定**: `@EnableAsync`
- **参照**: [06-performance-security-operations.md](06-performance-security-operations.md)

#### ✅ 29. 構造化ロギング
```java
log.info("User created: userId={}, userName={}, email={}", 
    user.getId(), user.getName(), user.getEmail());
```
- **必須**: SLF4J、Logback
- **形式**: 構造化（key=value）
- **禁止**: `log.info("User: " + user)` （文字列連結）
- **参照**: [06-performance-security-operations.md](06-performance-security-operations.md)

#### ✅ 30. Docker化
```dockerfile
FROM eclipse-temurin:17-jre-alpine
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```
- **必須**: multi-stage build、非rootユーザー
- **参照**: [06-performance-security-operations.md](06-performance-security-operations.md)

---

## 💬 7. コメント規約（2025-11-15追加）✨

### ✅ 31. 日本語コメントの記述
- **必須**: すべてのコメントを日本語で記述（技術用語を除く）
- **例外**: 公開APIのJavadoc（英語推奨）
- **参照**: [java-inline-comment-examples.md](java-inline-comment-examples.md)

### ✅ 32. WHY原則の遵守
- **必須**: 「WHAT」ではなく「WHY」を説明
- **悪い例**: `// ユーザーIDを取得する`
- **良い例**: `// キャッシュ無効化のため最新のユーザーIDを直接取得`
- **参照**: [java-inline-comment-examples.md](java-inline-comment-examples.md)

### ✅ 33. 複雑度基準の適用
- **必須**: 循環的複雑度10以上のメソッドに詳細コメント
- **推奨**: ビジネスロジックを含むメソッドすべて
- **参照**: [java-inline-comment-examples.md](java-inline-comment-examples.md)

### ✅ 34. テストコメント4要素
```java
/**
 * 【テスト対象】ユーザー作成メソッド
 * 【テストケース】有効なユーザーデータで作成
 * 【期待結果】ユーザーIDが生成され、DBに保存される
 * 【ビジネス要件】ユーザー登録時のバリデーション（REQ-001）
 */
@Test
void testCreateUser_WithValidData_ReturnsUserId() {
    // Given: 有効なユーザーデータ
    // When: ユーザー作成を実行
    // Then: ユーザーIDが返される
}
```
- **必須**: 【テスト対象】【テストケース】【期待結果】【ビジネス要件】を明記
- **推奨**: Given-When-Then構造の詳細コメント
- **参照**: [java-test-comment-examples.md](java-test-comment-examples.md)

### ✅ 35. TODO/FIXME/HACKの書式
```java
// TODO: [担当者名] [期限: YYYY-MM-DD] 理由: キャッシュ機能を追加する（パフォーマンス向上のため）
// FIXME: [担当者名] [期限: YYYY-MM-DD] 理由: nullチェックが不足している（NPE対策）
// HACK: [担当者名] 理由: ライブラリのバグ回避のための暫定対応（issue #123参照）
```
- **必須**: 担当者、期限、理由を記載
- **参照**: [java-inline-comment-examples.md](java-inline-comment-examples.md)

---

## 🔍 チェックリスト使用方法

### コード実装前（5分）
1. ✅ 1-5: プロジェクトセットアップ確認
2. ✅ 6-10: 命名規則確認
3. ✅ 11-15: クラス設計パターン確認

### コード実装中（随時）
1. ✅ 16-20: エラーハンドリング、セキュリティ
2. ✅ 21-25: テスト実装
3. ✅ 31-35: コメント規約遵守

### コードレビュー前（5分）
1. ✅ 1-35: 全項目再確認
2. ✅ 26-30: パフォーマンス、運用面の確認
3. ✅ 31-35: コメント品質チェック

---

## 🤖 Devinへの指示例

```
以下の必須チェック項目TOP35に厳密に従って実装してください:
- 規約: /devin-organization-standards/01-coding-standards/java/AI-QUICK-REFERENCE.md
- 重点項目: ✅11-15（クラス設計）、✅16-20（エラーハンドリング）、✅21-25（テスト）、✅31-35（コメント規約）
- テストカバレッジ: 80%以上（✅23）
- コメント: 日本語、WHY原則、テスト4要素を遵守（✅31-35）
- 実装完了後、✅1-35の全項目を確認し、違反がないことを報告してください
```

---

## 📊 違反時の対処

| チェック項目 | 違反例 | 修正方法 | 参照 |
|------------|--------|---------|------|
| ✅11 SRP違反 | Controllerにビジネスロジック | Serviceに移動 | 03 |
| ✅12 Controller責務 | DBアクセス | Repositoryに委譲 | 03 |
| ✅15 Entity公開 | Entityを直接返す | DTO変換 | 03 |
| ✅18 バリデーション | 手動検証 | Bean Validation | 04 |
| ✅23 カバレッジ | 70% | 80%まで追加 | 05 |
| ✅26 N+1問題 | 遅延ロード | JOIN FETCH | 06 |
| ✅32 WHY原則 | 「何を」のコメント | 「なぜ」に変更 | Comment |
| ✅34 テスト4要素 | 要素不足 | 4要素追加 | Comment |

---

## 🔗 詳細ドキュメント

各チェック項目の詳細は以下を参照：
- **[README.md](README.md)**: 全体ナビゲーション
- **[01-introduction-setup.md](01-introduction-setup.md)**: ✅1-5
- **[02-naming-style.md](02-naming-style.md)**: ✅6-10
- **[03-class-design-architecture.md](03-class-design-architecture.md)**: ✅11-15
- **[04-error-handling-validation.md](04-error-handling-validation.md)**: ✅16-20
- **[05-testing-quality.md](05-testing-quality.md)**: ✅21-25
- **[06-performance-security-operations.md](06-performance-security-operations.md)**: ✅26-30
- **[java-inline-comment-examples.md](java-inline-comment-examples.md)**: ✅31-33, 35
- **[java-test-comment-examples.md](java-test-comment-examples.md)**: ✅34

---

**最終確認**: 実装完了後、✅1-35の全項目をチェックし、違反ゼロを確認してください。
