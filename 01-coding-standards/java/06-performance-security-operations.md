# Performance, Security & Operations

**説明**: パフォーマンス最適化、セキュリティ、監視、運用

**主要トピック**:
- JPA/Hibernate最適化
- キャッシング戦略
- 非同期処理・並行処理
- JWT実装のベストプラクティス
- データ保護・暗号化
- 構造化ロギング
- Docker化・コンテナ運用
- CI/CD設定（GitHub Actions）

---

## 7. パフォーマンス最適化

### 7.1 データベース最適化

#### **JPA/Hibernateの最適化**
```java
// ✅ Good: パフォーマンスを考慮したエンティティ設計
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email", unique = true),
    @Index(name = "idx_user_status_created", columnList = "status, created_at"),
    @Index(name = "idx_user_last_login", columnList = "last_login_at")
})
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 50)
    private Long id;
    
    @Column(name = "email", nullable = false, length = 100)
    private String email;
    
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    @Column(name = "last_login_at")
    private Instant lastLoginAt;
    
    // 遅延ローディングでパフォーマンス向上
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @BatchSize(size = 20)  // N+1問題対策
    private List<Order> orders = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @BatchSize(size = 10)
    private Set<Role> roles = new HashSet<>();
    
    // バージョン管理による楽観的ロック
    @Version
    private Long version;
    
    // ヘルパーメソッド
    public boolean isActive() {
        return UserStatus.ACTIVE.equals(this.status);
    }
    
    public void updateLastLogin() {
        this.lastLoginAt = Instant.now();
    }
}

// パフォーマンス最適化されたRepository
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 必要なフィールドのみ取得するProjection
    @Query("SELECT new com.company.dto.UserSummary(u.id, u.email, u.firstName, u.lastName, u.status) " +
           "FROM User u WHERE u.status = :status")
    List<UserSummary> findUserSummariesByStatus(@Param("status") UserStatus status);
    
    // FETCH JOINでN+1問題解消
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.roles r " +
           "WHERE u.id IN :userIds")
    List<User> findUsersWithRoles(@Param("userIds") List<Long> userIds);
    
    // バッチ更新でパフォーマンス向上
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :loginTime WHERE u.id IN :userIds")
    int updateLastLoginBatch(@Param("userIds") List<Long> userIds, @Param("loginTime") Instant loginTime);
    
    // ページングでメモリ効率向上
    @Query(value = "SELECT * FROM users u WHERE u.created_at >= :since ORDER BY u.created_at DESC",
           countQuery = "SELECT COUNT(*) FROM users u WHERE u.created_at >= :since",
           nativeQuery = true)
    Page<User> findRecentUsers(@Param("since") Instant since, Pageable pageable);
    
    // ストリーミング処理で大量データ対応
    @QueryHints(@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE, value = "50"))
    @Query("SELECT u FROM User u WHERE u.status = :status")
    Stream<User> streamUsersByStatus(@Param("status") UserStatus status);
}

// DTOプロジェクション
public record UserSummary(
    Long id,
    String email,
    String firstName,
    String lastName,
    UserStatus status
) {}
```

#### **キャッシング戦略**
```java
// ✅ Good: 階層化されたキャッシング戦略
@Service
@CacheConfig(cacheNames = "users")
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 読み取り専用データのキャッシュ
    @Cacheable(key = "#id", unless = "#result == null")
    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        log.debug("Fetching user from database: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    // 条件付きキャッシュ
    @Cacheable(key = "'active-users'", condition = "#useCache == true")
    @Transactional(readOnly = true)
    public List<UserSummary> findActiveUsers(boolean useCache) {
        return userRepository.findUserSummariesByStatus(UserStatus.ACTIVE);
    }
    
    // キャッシュ更新
    @CachePut(key = "#result.id")
    @CacheEvict(cacheNames = "userStats", allEntries = true)  // 統計キャッシュをクリア
    @Transactional
    public User updateUser(Long id, UpdateUserRequest request) {
        User user = findUserById(id);
        
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        
        User updatedUser = userRepository.save(user);
        log.info("User updated and cache refreshed: {}", id);
        
        return updatedUser;
    }
    
    // キャッシュ削除
    @CacheEvict(key = "#id")
    @CacheEvict(cacheNames = "userStats", allEntries = true)
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("User deleted and cache evicted: {}", id);
    }
    
    // 複雑なキャッシュキー
    @Cacheable(
        cacheNames = "userSearch",
        key = "#email + '_' + #status.name()",
        condition = "#email != null and #email.length() > 0"
    )
    @Transactional(readOnly = true)
    public Optional<User> findUserByEmailAndStatus(String email, UserStatus status) {
        return userRepository.findByEmailAndStatus(email, status);
    }
    
    // 手動キャッシュ制御
    public UserStatistics getUserStatistics(Long userId) {
        String cacheKey = "user:stats:" + userId;
        
        // キャッシュから取得試行
        UserStatistics cached = (UserStatistics) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("User statistics retrieved from cache: {}", userId);
            return cached;
        }
        
        // データベースから計算
        UserStatistics stats = calculateUserStatistics(userId);
        
        // キャッシュに保存（TTL設定）
        redisTemplate.opsForValue().set(cacheKey, stats, Duration.ofHours(1));
        log.debug("User statistics calculated and cached: {}", userId);
        
        return stats;
    }
    
    // 大量データ処理でストリーミング使用
    @Transactional(readOnly = true)
    public void processAllActiveUsers(Consumer<User> processor) {
        try (Stream<User> userStream = userRepository.streamUsersByStatus(UserStatus.ACTIVE)) {
            userStream
                .filter(User::isActive)
                .forEach(user -> {
                    try {
                        processor.accept(user);
                    } catch (Exception e) {
                        log.error("Error processing user: {}", user.getId(), e);
                        // 続行
                    }
                });
        }
    }
    
    private UserStatistics calculateUserStatistics(Long userId) {
        // 統計計算ロジック
        return UserStatistics.builder()
                .userId(userId)
                .totalOrders(/* calculation */)
                .totalSpent(/* calculation */)
                .lastOrderDate(/* calculation */)
                .build();
    }
}

// キャッシュ設定
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        RedisCacheManager.Builder builder = RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory())
                .cacheDefaults(cacheConfiguration());
        
        // 個別キャッシュ設定
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // ユーザーキャッシュ - 長期保持
        cacheConfigurations.put("users", 
            cacheConfiguration().entryTtl(Duration.ofHours(6)));
        
        // 検索結果キャッシュ - 短期保持
        cacheConfigurations.put("userSearch", 
            cacheConfiguration().entryTtl(Duration.ofMinutes(30)));
        
        // 統計キャッシュ - 中期保持
        cacheConfigurations.put("userStats", 
            cacheConfiguration().entryTtl(Duration.ofHours(2)));
        
        return builder
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
    
    private RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();
    }
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(
                new RedisStandaloneConfiguration("localhost", 6379)
        );
        factory.setValidateConnection(true);
        return factory;
    }
}
```

### 7.2 アプリケーション最適化

#### **非同期処理・並行処理**
```java
// ✅ Good: 非同期処理の適切な実装
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProcessingService {
    
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;
    
    @Async("orderProcessingExecutor")
    @Transactional
    public CompletableFuture<Order> processOrderAsync(CreateOrderRequest request) {
        log.info("Starting async order processing for user: {}", request.getUserId());
        
        try {
            // 在庫確認（並行実行）
            CompletableFuture<Void> inventoryCheck = inventoryService
                    .checkAvailabilityAsync(request.getItems());
            
            // ユーザー確認（並行実行）
            CompletableFuture<User> userFetch = userService
                    .findUserByIdAsync(request.getUserId());
            
            // 両方の処理完了を待機
            CompletableFuture.allOf(inventoryCheck, userFetch).join();
            
            User user = userFetch.get();
            
            // 注文作成
            Order order = createOrder(request, user);
            
            // 支払い処理（非同期）
            CompletableFuture<Payment> paymentFuture = paymentService
                    .processPaymentAsync(order.getTotal(), request.getPaymentMethod());
            
            // 通知送信（非同期・fire-and-forget）
            notificationService.sendOrderConfirmationAsync(order);
            
            // 支払い完了待機
            Payment payment = paymentFuture.get(30, TimeUnit.SECONDS);
            order.setPayment(payment);
            order.setStatus(OrderStatus.CONFIRMED);
            
            Order savedOrder = orderRepository.save(order);
            
            // イベント発行
            eventPublisher.publishEvent(new OrderCreatedEvent(savedOrder));
            
            log.info("Order processing completed: {}", savedOrder.getId());
            return CompletableFuture.completedFuture(savedOrder);
            
        } catch (Exception e) {
            log.error("Error processing order for user: {}", request.getUserId(), e);
            throw new OrderProcessingException("Failed to process order", e);
        }
    }
    
    // バッチ処理での並行実行
    @Async("batchProcessingExecutor")
    public CompletableFuture<BatchProcessingResult> processOrdersBatch(List<Long> orderIds) {
        log.info("Starting batch processing for {} orders", orderIds.size());
        
        List<CompletableFuture<OrderProcessingResult>> futures = orderIds.stream()
                .map(this::processSingleOrderAsync)
                .collect(Collectors.toList());
        
        // 全ての処理完了を待機
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );
        
        return allOf.thenApply(v -> {
            List<OrderProcessingResult> results = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
            
            return BatchProcessingResult.builder()
                    .totalProcessed(results.size())
                    .successCount(results.stream().mapToInt(r -> r.isSuccess() ? 1 : 0).sum())
                    .failureCount(results.stream().mapToInt(r -> r.isSuccess() ? 0 : 1).sum())
                    .results(results)
                    .build();
        });
    }
    
    private CompletableFuture<OrderProcessingResult> processSingleOrderAsync(Long orderId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new OrderNotFoundException(orderId));
                
                // 処理ロジック
                processOrder(order);
                
                return OrderProcessingResult.success(orderId);
                
            } catch (Exception e) {
                log.error("Failed to process order: {}", orderId, e);
                return OrderProcessingResult.failure(orderId, e.getMessage());
            }
        }, batchProcessingExecutor);
    }
}

// 非同期設定
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    
    @Bean(name = "orderProcessingExecutor")
    public Executor orderProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("OrderProcessing-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "batchProcessingExecutor")
    public Executor batchProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("BatchProcessing-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }
    
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
```

---

## 8. セキュリティ・監査

### 8.1 認証・認可の詳細実装

#### **JWT実装のベストプラクティス**
```java
// ✅ Good: セキュアなJWT実装
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration:3600000}")  // 1時間
    private int jwtExpirationMs;
    
    @Value("${app.jwt.refresh-expiration:604800000}")  // 7日間
    private int refreshExpirationMs;
    
    private final UserService userService;
    private Key key;
    
    @PostConstruct
    public void init() {
        // HMACキーの生成（本番環境では環境変数から取得）
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generateAccessToken(UserPrincipal userPrincipal) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationMs);
        
        return Jwts.builder()
                .setSubject(userPrincipal.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .claim("email", userPrincipal.getEmail())
                .claim("roles", userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .claim("tokenType", "ACCESS")
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    
    public String generateRefreshToken(UserPrincipal userPrincipal) {
        Date expiryDate = new Date(System.currentTimeMillis() + refreshExpirationMs);
        
        return Jwts.builder()
                .setSubject(userPrincipal.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .claim("tokenType", "REFRESH")
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    
    public UserPrincipal getUserFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        Long userId = Long.parseLong(claims.getSubject());
        String email = claims.get("email", String.class);
        
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);
        
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        
        return UserPrincipal.builder()
                .id(userId)
                .email(email)
                .authorities(authorities)
                .build();
    }
    
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // トークンタイプチェック
            String tokenType = claims.get("tokenType", String.class);
            if (!"ACCESS".equals(tokenType)) {
                log.warn("Invalid token type: {}", tokenType);
                return false;
            }
            
            // ユーザー存在チェック
            Long userId = Long.parseLong(claims.getSubject());
            if (!userService.existsById(userId)) {
                log.warn("User not found for token: {}", userId);
                return false;
            }
            
            return true;
            
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.debug("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error validating JWT: {}", e.getMessage());
        }
        
        return false;
    }
}
```

### 8.2 データ保護・暗号化

#### **機密データの暗号化**
```java
// ✅ Good: AES暗号化を使用した機密データ保護
@Service
@RequiredArgsConstructor
@Slf4j
public class EncryptionService {
    
    @Value("${app.encryption.key}")
    private String encryptionKey;
    
    private static final String ALGORITHM = "AES/GCM/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    private static final int IV_LENGTH = 12; // GCM推奨IV長
    
    private SecretKeySpec secretKey;
    
    @PostConstruct
    public void init() {
        try {
            // 暗号化キーの準備（本番環境では安全な鍵管理システムを使用）
            byte[] keyBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            keyBytes = sha.digest(keyBytes);
            secretKey = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize encryption service", e);
        }
    }
    
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            
            // ランダムIV生成
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom.getInstanceStrong().nextBytes(iv);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            
            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // IV + 暗号化データを結合
            byte[] encryptedWithIv = new byte[IV_LENGTH + encryptedData.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, IV_LENGTH);
            System.arraycopy(encryptedData, 0, encryptedWithIv, IV_LENGTH, encryptedData.length);
            
            return Base64.getEncoder().encodeToString(encryptedWithIv);
            
        } catch (Exception e) {
            log.error("Encryption failed", e);
            throw new EncryptionException("Failed to encrypt data", e);
        }
    }
    
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        
        try {
            byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);
            
            // IVと暗号化データを分離
            byte[] iv = new byte[IV_LENGTH];
            byte[] encryptedData = new byte[encryptedWithIv.length - IV_LENGTH];
            
            System.arraycopy(encryptedWithIv, 0, iv, 0, IV_LENGTH);
            System.arraycopy(encryptedWithIv, IV_LENGTH, encryptedData, 0, encryptedData.length);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            
            byte[] decryptedData = cipher.doFinal(encryptedData);
            
            return new String(decryptedData, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            log.error("Decryption failed", e);
            throw new DecryptionException("Failed to decrypt data", e);
        }
    }
}
```

---


## 9. 監視・ロギング

### 9.1 構造化ロギング

#### **Logbackを使用した高度なロギング設定**
```xml
<!-- logback-spring.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- プロファイル別設定 -->
    <springProfile name="dev">
        <property name="LOG_LEVEL" value="DEBUG"/>
        <property name="LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%logger{36}] - %msg%n"/>
    </springProfile>
    
    <springProfile name="prod">
        <property name="LOG_LEVEL" value="INFO"/>
        <property name="LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%logger{36}] [%X{traceId:-}] [%X{userId:-}] - %msg%n"/>
    </springProfile>
    
    <!-- コンソール出力 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- ファイル出力（アプリケーションログ） -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>10GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <root level="${LOG_LEVEL}">
        <springProfile name="dev">
            <appender-ref ref="CONSOLE"/>
            <appender-ref ref="FILE"/>
        </springProfile>
        <springProfile name="prod">
            <appender-ref ref="FILE"/>
        </springProfile>
    </root>
    
</configuration>
```

#### **構造化ロギングの実装**
```java
// ✅ Good: 構造化されたロギング実装
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    
    // 構造化ログ用定数
    private static final String ORDER_CREATED = "ORDER_CREATED";
    private static final String ORDER_PAYMENT_PROCESSED = "ORDER_PAYMENT_PROCESSED";
    private static final String ORDER_FAILED = "ORDER_FAILED";
    
    // セキュリティログ用マーカー
    private static final Marker SECURITY_MARKER = MarkerFactory.getMarker("SECURITY");
    
    // パフォーマンスログ用マーカー
    private static final Marker PERFORMANCE_MARKER = MarkerFactory.getMarker("PERFORMANCE");
    
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        // コンテキスト情報をMDCに設定
        String traceId = UUID.randomUUID().toString();
        String userId = getCurrentUserId();
        
        MDC.put("traceId", traceId);
        MDC.put("userId", userId);
        MDC.put("operation", "createOrder");
        
        Stopwatch stopwatch = Stopwatch.createStarted();
        
        try {
            // 構造化ログ - リクエスト開始
            log.info("Order creation started - userId: {}, itemCount: {}, totalAmount: {}",
                    request.getUserId(),
                    request.getItems().size(),
                    request.getTotalAmount());
            
            // ビジネスロジック実行
            Order order = processOrder(request);
            
            // 構造化ログ - 正常完了
            stopwatch.stop();
            log.info(PERFORMANCE_MARKER,
                    "Order created successfully - orderId: {}, processingTime: {}ms, userId: {}",
                    order.getId(),
                    stopwatch.elapsed(TimeUnit.MILLISECONDS),
                    request.getUserId());
            
            return order;
            
        } catch (Exception e) {
            // 予期しないエラーログ
            log.error("Unexpected error during order creation - userId: {}, traceId: {}",
                    request.getUserId(),
                    traceId,
                    e);
            
            throw new OrderCreationException("Failed to create order", e);
            
        } finally {
            // MDCをクリア
            MDC.clear();
        }
    }
    
    private Order processOrder(CreateOrderRequest request) {
        // 実際の注文処理ロジック
        return null; // 実装省略
    }
    
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) auth.getPrincipal()).getId().toString();
        }
        return "anonymous";
    }
}
```

---


## 10. デプロイメント・運用

### 10.1 Docker化・コンテナ運用

#### **Dockerfileのベストプラクティス**
```dockerfile
# ✅ Good: マルチステージビルドによる最適化
# ビルドステージ
FROM openjdk:17-jdk-slim AS builder

# 作業ディレクトリ設定
WORKDIR /app

# Gradleファイルをコピー（依存関係キャッシュ最適化）
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./

# 依存関係をダウンロード（レイヤーキャッシュ活用）
RUN ./gradlew dependencies --no-daemon

# ソースコードをコピー
COPY src/ src/

# アプリケーションをビルド
RUN ./gradlew build --no-daemon -x test

# 実行ステージ
FROM openjdk:17-jre-slim

# セキュリティ：非rootユーザーで実行
RUN groupadd -r spring && useradd -r -g spring spring

# 作業ディレクトリ設定
WORKDIR /app

# ヘルスチェック用curlインストール
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# ビルドステージからJARファイルをコピー
COPY --from=builder /app/build/libs/*.jar app.jar

# アプリケーション設定
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=prod

# ポート公開
EXPOSE 8080

# ヘルスチェック設定
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 非rootユーザーに切り替え
USER spring:spring

# アプリケーション起動
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

#### **docker-compose.yml**
```yaml
# ✅ Good: 本番環境対応のDocker Compose設定
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    image: company/java-app:latest
    container_name: java-app
    restart: unless-stopped
    
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/appdb
      - SPRING_DATASOURCE_USERNAME=appuser
      - SPRING_DATASOURCE_PASSWORD_FILE=/run/secrets/db_password
      - SPRING_REDIS_HOST=redis
      - SPRING_REDIS_PORT=6379
      - JAVA_OPTS=-Xmx1g -Xms512m -XX:+UseG1GC
    
    ports:
      - "8080:8080"
    
    depends_on:
      db:
        condition: service_healthy
      redis:
        condition: service_healthy
    
    volumes:
      - app_logs:/app/logs
      - /etc/localtime:/etc/localtime:ro
    
    secrets:
      - db_password
      - jwt_secret
    
    networks:
      - app-network
    
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"

  db:
    image: postgres:15-alpine
    container_name: postgres-db
    restart: unless-stopped
    
    environment:
      - POSTGRES_DB=appdb
      - POSTGRES_USER=appuser
      - POSTGRES_PASSWORD_FILE=/run/secrets/db_password
    
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init-scripts:/docker-entrypoint-initdb.d
    
    secrets:
      - db_password
    
    networks:
      - app-network
    
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U appuser -d appdb"]
      interval: 30s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: redis-cache
    restart: unless-stopped
    
    command: redis-server --appendonly yes --requirepass ${REDIS_PASSWORD}
    
    volumes:
      - redis_data:/data
    
    networks:
      - app-network
    
    healthcheck:
      test: ["CMD", "redis-cli", "auth", "${REDIS_PASSWORD}", "ping"]
      interval: 30s
      timeout: 5s
      retries: 3

  nginx:
    image: nginx:alpine
    container_name: nginx-proxy
    restart: unless-stopped
    
    ports:
      - "80:80"
      - "443:443"
    
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
      - nginx_logs:/var/log/nginx
    
    depends_on:
      - app
    
    networks:
      - app-network

volumes:
  postgres_data:
  redis_data:
  app_logs:
  nginx_logs:

networks:
  app-network:
    driver: bridge

secrets:
  db_password:
    file: ./secrets/db_password.txt
  jwt_secret:
    file: ./secrets/jwt_secret.txt
```

### 10.2 CI/CD設定

#### **GitHub Actions設定**
```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

env:
  JAVA_VERSION: '17'
  NODE_VERSION: '18'

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
          POSTGRES_DB: testdb
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
      
      redis:
        image: redis:7
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 6379:6379

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Set up JDK
      uses: actions/setup-java@v3
      with:
        java-version: ${{ env.JAVA_VERSION }}
        distribution: 'temurin'

    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-

    - name: Grant execute permission for gradlew
      run: chmod +x gradlew

    - name: Run tests
      run: ./gradlew test jacocoTestReport
      env:
        SPRING_PROFILES_ACTIVE: test
        SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/testdb
        SPRING_DATASOURCE_USERNAME: postgres
        SPRING_DATASOURCE_PASSWORD: postgres
        SPRING_REDIS_HOST: localhost
        SPRING_REDIS_PORT: 6379

    - name: Upload test results
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-results
        path: build/reports/tests/test/

    - name: Upload coverage reports
      uses: codecov/codecov-action@v3
      with:
        file: build/reports/jacoco/test/jacocoTestReport.xml

    - name: Run security scan
      run: ./gradlew dependencyCheckAnalyze

    - name: Run code quality analysis
      uses: sonarqube-quality-gate-action@master
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}

  build:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Set up JDK
      uses: actions/setup-java@v3
      with:
        java-version: ${{ env.JAVA_VERSION }}
        distribution: 'temurin'

    - name: Build application
      run: ./gradlew build -x test

    - name: Build Docker image
      run: |
        docker build -t ${{ secrets.DOCKER_REGISTRY }}/java-app:${{ github.sha }} .
        docker tag ${{ secrets.DOCKER_REGISTRY }}/java-app:${{ github.sha }} ${{ secrets.DOCKER_REGISTRY }}/java-app:latest

    - name: Log in to Docker registry
      uses: docker/login-action@v2
      with:
        registry: ${{ secrets.DOCKER_REGISTRY }}
        username: ${{ secrets.DOCKER_USERNAME }}
        password: ${{ secrets.DOCKER_PASSWORD }}

    - name: Push Docker image
      run: |
        docker push ${{ secrets.DOCKER_REGISTRY }}/java-app:${{ github.sha }}
        docker push ${{ secrets.DOCKER_REGISTRY }}/java-app:latest

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    environment: production

    steps:
    - name: Deploy to production
      uses: appleboy/ssh-action@v0.1.7
      with:
        host: ${{ secrets.PRODUCTION_HOST }}
        username: ${{ secrets.PRODUCTION_USER }}
        key: ${{ secrets.PRODUCTION_SSH_KEY }}
        script: |
          cd /opt/app
          docker-compose pull
          docker-compose up -d --no-deps app
          docker system prune -f

    - name: Health check
      run: |
        sleep 30
        curl -f ${{ secrets.PRODUCTION_URL }}/actuator/health || exit 1

    - name: Notify deployment
      uses: 8398a7/action-slack@v3
      with:
        status: ${{ job.status }}
        channel: '#deployments'
        webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

---


