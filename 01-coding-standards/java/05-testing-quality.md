# Testing Strategy & Quality Assurance

**説明**: テスト戦略、単体テスト、統合テスト、品質保証

**主要トピック**:
- JUnit 5とMockitoの活用
- 単体テスト（Unit Test）のベストプラクティス
- 統合テスト（Integration Test）
- Spring Boot Testの活用
- テストカバレッジ基準（80%以上）

---

## 6. テスト戦略・品質保証

### 6.1 単体テスト（Unit Test）

#### **JUnit 5とMockitoの活用**
```java
// ✅ Good: 包括的な単体テスト
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Test")
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private UserService userService;
    
    @Nested
    @DisplayName("createUser Tests")
    class CreateUserTests {
        
        @Test
        @DisplayName("Should create user successfully with valid request")
        void shouldCreateUserSuccessfully() {
            // Given
            CreateUserRequest request = CreateUserRequest.builder()
                    .email("test@example.com")
                    .firstName("John")
                    .lastName("Doe")
                    .password("SecurePassword123!")
                    .build();
            
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
            
            User savedUser = User.builder()
                    .id(1L)
                    .email(request.getEmail())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .passwordHash("encoded-password")
                    .status(UserStatus.ACTIVE)
                    .build();
            
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            
            // When
            User result = userService.createUser(request);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo(request.getEmail());
            assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
            
            verify(userRepository).existsByEmail(request.getEmail());
            verify(passwordEncoder).encode(request.getPassword());
            verify(userRepository).save(argThat(user -> 
                user.getEmail().equals(request.getEmail()) &&
                user.getFirstName().equals(request.getFirstName()) &&
                user.getPasswordHash().equals("encoded-password")
            ));
            verify(emailService).sendWelcomeEmail(savedUser);
        }
        
        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            // Given
            CreateUserRequest request = CreateUserRequest.builder()
                    .email("existing@example.com")
                    .firstName("John")
                    .lastName("Doe")
                    .password("SecurePassword123!")
                    .build();
            
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
            
            // When & Then
            assertThatThrownBy(() -> userService.createUser(request))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("User already exists with email: existing@example.com");
            
            verify(userRepository).existsByEmail(request.getEmail());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
            verify(emailService, never()).sendWelcomeEmail(any(User.class));
        }
    }
    
    @Nested
    @DisplayName("findUserById Tests")
    class FindUserByIdTests {
        
        @Test
        @DisplayName("Should return user when found")
        void shouldReturnUserWhenFound() {
            // Given
            Long userId = 1L;
            User expectedUser = User.builder()
                    .id(userId)
                    .email("test@example.com")
                    .firstName("John")
                    .lastName("Doe")
                    .status(UserStatus.ACTIVE)
                    .build();
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
            
            // When
            User result = userService.findUserById(userId);
            
            // Then
            assertThat(result).isEqualTo(expectedUser);
            verify(userRepository).findById(userId);
        }
        
        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Given
            Long userId = 999L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            
            // When & Then
            assertThatThrownBy(() -> userService.findUserById(userId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found with ID: 999");
            
            verify(userRepository).findById(userId);
        }
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "invalid-email", "@example.com", "test@"})
    @DisplayName("Should reject invalid email formats")
    void shouldRejectInvalidEmailFormats(String invalidEmail) {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .email(invalidEmail)
                .firstName("John")
                .lastName("Doe")
                .password("SecurePassword123!")
                .build();
        
        // When & Then
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

// テストデータビルダーパターン
public class UserTestDataBuilder {
    private Long id = 1L;
    private String email = "test@example.com";
    private String firstName = "John";
    private String lastName = "Doe";
    private String passwordHash = "encoded-password";
    private UserStatus status = UserStatus.ACTIVE;
    private Instant createdAt = Instant.now();
    
    public static UserTestDataBuilder aUser() {
        return new UserTestDataBuilder();
    }
    
    public UserTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }
    
    public UserTestDataBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public UserTestDataBuilder withStatus(UserStatus status) {
        this.status = status;
        return this;
    }
    
    public User build() {
        return User.builder()
                .id(id)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .passwordHash(passwordHash)
                .status(status)
                .createdAt(createdAt)
                .build();
    }
}

// 使用例
@Test
void shouldProcessActiveUsersOnly() {
    // Given
    User activeUser = UserTestDataBuilder.aUser()
            .withId(1L)
            .withStatus(UserStatus.ACTIVE)
            .build();
    
    User inactiveUser = UserTestDataBuilder.aUser()
            .withId(2L)
            .withStatus(UserStatus.INACTIVE)
            .build();
    
    // Test logic...
}
```

### 6.2 統合テスト（Integration Test）

#### **Spring Boot Testの活用**
```java
// ✅ Good: 包括的な統合テスト
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@Testcontainers
@DisplayName("User Integration Test")
class UserIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TestEntityManager testEntityManager;
    
    @LocalServerPort
    private int port;
    
    private String baseUrl;
    
    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/users";
        userRepository.deleteAll();
    }
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Test
    @DisplayName("Should create user via REST API")
    void shouldCreateUserViaApi() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .email("integration@example.com")
                .firstName("Integration")
                .lastName("Test")
                .password("SecurePassword123!")
                .build();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateUserRequest> entity = new HttpEntity<>(request, headers);
        
        // When
        ResponseEntity<UserResponse> response = restTemplate.postForEntity(
                baseUrl, entity, UserResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getBody().getFirstName()).isEqualTo(request.getFirstName());
        
        // データベース確認
        Optional<User> savedUser = userRepository.findByEmail(request.getEmail());
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
    
    @Test
    @DisplayName("Should return validation errors for invalid request")
    void shouldReturnValidationErrors() {
        // Given
        CreateUserRequest invalidRequest = CreateUserRequest.builder()
                .email("invalid-email")
                .firstName("")
                .lastName("Doe")
                .password("weak")
                .build();
        
        HttpEntity<CreateUserRequest> entity = new HttpEntity<>(invalidRequest);
        
        // When
        ResponseEntity<ValidationErrorResponse> response = restTemplate.postForEntity(
                baseUrl, entity, ValidationErrorResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().getFieldErrors()).containsKeys("email", "firstName", "password");
    }
    
    @Test
    @DisplayName("Should handle duplicate email creation")
    void shouldHandleDuplicateEmail() {
        // Given - 既存ユーザーを作成
        User existingUser = User.builder()
                .email("duplicate@example.com")
                .firstName("Existing")
                .lastName("User")
                .passwordHash("encoded-password")
                .status(UserStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();
        userRepository.save(existingUser);
        
        CreateUserRequest duplicateRequest = CreateUserRequest.builder()
                .email("duplicate@example.com")
                .firstName("Duplicate")
                .lastName("User")
                .password("SecurePassword123!")
                .build();
        
        HttpEntity<CreateUserRequest> entity = new HttpEntity<>(duplicateRequest);
        
        // When
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                baseUrl, entity, ErrorResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("USER_ALREADY_EXISTS");
        
        // データベース確認 - 元のユーザーのみが存在することを確認
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getFirstName()).isEqualTo("Existing");
    }
}

// Repository層の統合テスト
@DataJpaTest
@DisplayName("UserRepository Integration Test")
class UserRepositoryIntegrationTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    @DisplayName("Should find users by email ignore case")
    void shouldFindUsersByEmailIgnoreCase() {
        // Given
        User user = User.builder()
                .email("Test@Example.COM")
                .firstName("John")
                .lastName("Doe")
                .passwordHash("encoded-password")
                .status(UserStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();
        entityManager.persistAndFlush(user);
        
        // When
        Optional<User> result = userRepository.findByEmailIgnoreCase("test@example.com");
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("Test@Example.COM");
    }
    
    @Test
    @DisplayName("Should find users with filters")
    void shouldFindUsersWithFilters() {
        // Given
        Instant baseTime = Instant.now().minus(Duration.ofDays(30));
        
        User user1 = createUser("john@example.com", "John", "Doe", UserStatus.ACTIVE, baseTime);
        User user2 = createUser("jane@example.com", "Jane", "Smith", UserStatus.ACTIVE, baseTime.plus(Duration.ofDays(1)));
        User user3 = createUser("bob@example.com", "Bob", "Johnson", UserStatus.INACTIVE, baseTime.plus(Duration.ofDays(2)));
        
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);
        
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> result = userRepository.findUsersWithFilters(
                null,  // email filter
                "J",   // firstName filter
                UserStatus.ACTIVE,  // status filter
                baseTime.minus(Duration.ofDays(1)),  // startDate
                Instant.now(),  // endDate
                pageable
        );
        
        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(User::getFirstName)
                .containsExactly("Jane", "John");  // 作成日時の降順
    }
    
    private User createUser(String email, String firstName, String lastName, UserStatus status, Instant createdAt) {
        return User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .passwordHash("encoded-password")
                .status(status)
                .createdAt(createdAt)
                .build();
    }
}
```

### 6.3 テスト設定・ユーティリティ

#### **テスト専用設定**
```properties
# application-test.properties
# データベース設定（Testcontainersで上書きされる）
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop

# ログレベル
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# メール送信を無効化
app.mail.enabled=false

# キャッシュを無効化
spring.cache.type=none

# セキュリティテスト用設定
app.jwt.secret=test-secret-key-for-testing-purposes-only
app.jwt.expiration=3600000

# 非同期処理を同期実行
spring.task.execution.pool.core-size=1
spring.task.execution.pool.max-size=1
spring.task.scheduling.pool.size=1
```

```java
// テスト用設定クラス
@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public Clock testClock() {
        return Clock.fixed(Instant.parse("2024-01-15T10:00:00Z"), ZoneOffset.UTC);
    }
    
    @Bean
    @Primary  
    public EmailService mockEmailService() {
        return Mockito.mock(EmailService.class);
    }
    
    @Bean
    @Primary
    public NotificationService mockNotificationService() {
        return Mockito.mock(NotificationService.class);
    }
    
    @EventListener
    public void handleTestEvent(ApplicationReadyEvent event) {
        // テスト用初期データの設定
        initializeTestData();
    }
    
    private void initializeTestData() {
        // 必要に応じてテスト用初期データを設定
    }
}

// テストスライス用カスタムアノテーション
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(TestConfig.class)
@Testcontainers
public @interface IntegrationTest {
}

// 使用例
@IntegrationTest
class OrderServiceIntegrationTest {
    // テストコード
}
```

---


