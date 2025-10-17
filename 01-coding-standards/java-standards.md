# Java コーディング規約
## Java Coding Standards

**最終更新日**: 2024-10-15  
**バージョン**: 1.0.0  
**対象**: Java 17+, Spring Boot 3.0+, Maven/Gradle  
**適用範囲**: エンタープライズアプリケーション・マイクロサービス・バックエンドAPI

## 目的

このドキュメントは、Javaプロジェクトにおける具体的なコーディング規約を定義し、エンタープライズ環境での保守性・拡張性・セキュリティを重視した高品質なコードを実現します。共通原則については`00-general-principles.md`を参照してください。

---

## 1. 基本設定・ツール設定

### 1.1 推奨ツールチェーン

#### **必須ツール**
```xml
<!-- Maven依存関係（pom.xml） -->
<properties>
    <java.version>17</java.version>
    <spring-boot.version>3.1.0</spring-boot.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.target>
</properties>

<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### **Gradle設定（build.gradle）**
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.1.0'
    id 'io.spring.dependency-management' version '1.1.0'
    id 'checkstyle'
    id 'jacoco'
    id 'org.sonarqube' version '4.0.0.2929'
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.testcontainers:junit-jupiter'
    testImplementation 'org.mockito:mockito-junit-jupiter'
}
```

#### **Checkstyle設定（checkstyle.xml）**
```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
    "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
    "https://checkstyle.org/dtds/configuration_1_3.dtd">

<module name="Checker">
    <property name="severity" value="error"/>
    <property name="fileExtensions" value="java, properties, xml"/>
    
    <!-- ファイルレベルチェック -->
    <module name="FileTabCharacter">
        <property name="eachLine" value="true"/>
    </module>
    
    <module name="TreeWalker">
        <!-- 命名規則 -->
        <module name="ConstantName"/>
        <module name="LocalFinalVariableName"/>
        <module name="LocalVariableName"/>
        <module name="MemberName"/>
        <module name="MethodName"/>
        <module name="PackageName"/>
        <module name="ParameterName"/>
        <module name="StaticVariableName"/>
        <module name="TypeName"/>
        
        <!-- インポート -->
        <module name="AvoidStarImport"/>
        <module name="IllegalImport"/>
        <module name="RedundantImport"/>
        <module name="UnusedImports"/>
        
        <!-- サイズ制限 -->
        <module name="LineLength">
            <property name="max" value="120"/>
        </module>
        <module name="MethodLength">
            <property name="max" value="50"/>
        </module>
        <module name="ParameterNumber">
            <property name="max" value="5"/>
        </module>
        
        <!-- 空白・フォーマット -->
        <module name="EmptyForIteratorPad"/>
        <module name="GenericWhitespace"/>
        <module name="MethodParamPad"/>
        <module name="NoWhitespaceAfter"/>
        <module name="NoWhitespaceBefore"/>
        <module name="OperatorWrap"/>
        <module name="ParenPad"/>
        <module name="TypecastParenPad"/>
        <module name="WhitespaceAfter"/>
        <module name="WhitespaceAround"/>
        
        <!-- 修飾子 -->
        <module name="ModifierOrder"/>
        <module name="RedundantModifier"/>
        
        <!-- ブロック -->
        <module name="AvoidNestedBlocks"/>
        <module name="EmptyBlock"/>
        <module name="LeftCurly"/>
        <module name="NeedBraces"/>
        <module name="RightCurly"/>
        
        <!-- 一般的なコーディング問題 -->
        <module name="EmptyStatement"/>
        <module name="EqualsHashCode"/>
        <module name="HiddenField"/>
        <module name="IllegalInstantiation"/>
        <module name="InnerAssignment"/>
        <module name="MagicNumber"/>
        <module name="MissingSwitchDefault"/>
        <module name="SimplifyBooleanExpression"/>
        <module name="SimplifyBooleanReturn"/>
        
        <!-- クラス設計 -->
        <module name="DesignForExtension"/>
        <module name="FinalClass"/>
        <module name="HideUtilityClassConstructor"/>
        <module name="InterfaceIsType"/>
        <module name="VisibilityModifier"/>
    </module>
</module>
```

#### **JaCoCo設定（Gradleに追加）**
```gradle
jacoco {
    toolVersion = "0.8.8"
}

jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80  // 80%以上のカバレッジを要求
            }
        }
    }
}
```

---

## 2. 命名規則・スタイル

### 2.1 命名規則

#### **パッケージ名**
```java
// ✅ Good: 小文字、ドット区切り、意味的な階層
package com.company.project.user.service;
package com.company.project.order.repository;

// ❌ Bad: 大文字・アンダースコア・意味不明
package com.Company.Project.User_Service;
package com.company.project.utils;  // 曖昧すぎる
```

#### **クラス名**
```java
// ✅ Good: PascalCase、目的が明確
public class UserService {
public class OrderController {
public class PaymentGatewayImpl implements PaymentGateway {
public abstract class BaseEntity {
public interface UserRepository extends JpaRepository<User, Long> {

// ❌ Bad: 命名が不適切
public class userservice {          // 小文字
public class UserServiceImpl {      // 不要なImpl接尾辞（インターフェースがない場合）
public class Manager {              // 抽象的すぎる
public class UserData {             // DataやInfoは避ける
```

#### **メソッド名**
```java
// ✅ Good: camelCase、動詞で開始、目的が明確
public User findUserById(Long id) {
public void validateUserInput(UserRequest request) {
public boolean isValidEmail(String email) {
public List<Order> getActiveOrdersByUser(Long userId) {

// ❌ Bad: 不適切な命名
public User user(Long id) {             // 動詞がない
public void check(UserRequest request) { // 抽象的すぎる
public boolean validEmail(String email) { // isで始まるべき
public List<Order> getOrders() {        // 条件が不明確
```

#### **変数名**
```java
// ✅ Good: camelCase、意味が明確
private String firstName;
private Long userId;
private List<Order> activeOrders;
private final String API_BASE_URL = "https://api.example.com";

// 一時変数も意味のある名前を
for (Order order : orders) {
    if (order.isActive()) {
        processActiveOrder(order);
    }
}

// ❌ Bad: 不適切な命名
private String fName;           // 略語
private Long id;                // 汎用的すぎる
private List<Order> list;       // 意味不明
private final String URL = ""; // 定数なのに大文字でない文脈

// 意味のない一時変数
for (Order o : orders) {        // oは意味不明
    if (o.isActive()) {
        processActiveOrder(o);
    }
}
```

### 2.2 フォーマット・インデント

#### **基本フォーマット**
```java
// ✅ Good: 一貫したフォーマット
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable @Positive Long id,
            @RequestParam(defaultValue = "false") boolean includeOrders) {
        
        try {
            UserResponse user = userService.findUserById(id, includeOrders);
            return ResponseEntity.ok(user);
            
        } catch (UserNotFoundException e) {
            logger.warn("User not found: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}

// ❌ Bad: 不一貫なフォーマット
@RestController@RequestMapping("/api/v1/users")
public class UserController{
private final UserService userService;
public UserController(UserService userService){this.userService = userService;}
@GetMapping("/{id}")public ResponseEntity<UserResponse> getUser(@PathVariable Long id){
try{
UserResponse user=userService.findUserById(id);
return ResponseEntity.ok(user);}catch(UserNotFoundException e){
return ResponseEntity.notFound().build();}}
}
```

#### **改行・空白のルール**
```java
// ✅ Good: 適切な改行と空白
public class OrderService {
    
    // メソッド間は1行空ける
    public Order createOrder(CreateOrderRequest request) {
        validateRequest(request);
        
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setItems(convertToOrderItems(request.getItems()));
        
        return orderRepository.save(order);
    }
    
    public void cancelOrder(Long orderId) {
        Order order = findOrderById(orderId);
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot cancel order in status: " + order.getStatus());
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
    
    // プライベートメソッド
    private void validateRequest(CreateOrderRequest request) {
        if (request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
    }
}
```

---

## 3. クラス設計・構造化

### 3.1 クラス設計原則

#### **単一責任原則の適用**
```java
// ✅ Good: 単一責任で分離
@Service
public class UserService {
    public User findUserById(Long id) { ... }
    public User createUser(CreateUserRequest request) { ... }
    public void updateUser(Long id, UpdateUserRequest request) { ... }
}

@Service  
public class UserNotificationService {
    public void sendWelcomeEmail(User user) { ... }
    public void sendPasswordResetEmail(User user) { ... }
}

@Component
public class UserValidator {
    public void validateCreateRequest(CreateUserRequest request) { ... }
    public void validateUpdateRequest(UpdateUserRequest request) { ... }
}

// ❌ Bad: 複数責任が混在
@Service
public class UserService {
    // ユーザー管理
    public User findUserById(Long id) { ... }
    public User createUser(CreateUserRequest request) { ... }
    
    // メール送信（別責任）
    public void sendWelcomeEmail(User user) { ... }
    
    // バリデーション（別責任）
    public void validateUser(User user) { ... }
    
    // レポート生成（別責任）
    public UserReport generateUserReport(Long userId) { ... }
}
```

#### **依存性注入の活用**
```java
// ✅ Good: コンストラクタインジェクション、immutable
@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    
    // 全依存性をコンストラクタで注入
    public OrderService(
            OrderRepository orderRepository,
            UserService userService,  
            PaymentService paymentService,
            NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }
    
    public Order processOrder(CreateOrderRequest request) {
        // ビジネスロジック
        User user = userService.findUserById(request.getUserId());
        Order order = createOrder(request, user);
        
        Payment payment = paymentService.processPayment(order);
        order.setPayment(payment);
        
        Order savedOrder = orderRepository.save(order);
        notificationService.sendOrderConfirmation(savedOrder);
        
        return savedOrder;
    }
}

// ❌ Bad: フィールドインジェクション、テストが困難
@Service
public class OrderService {
    
    @Autowired  // フィールドインジェクションは避ける
    private OrderRepository orderRepository;
    
    @Autowired
    private UserService userService;
    
    // 依存性が隠蔽され、テストが困難
    public Order processOrder(CreateOrderRequest request) {
        // ...
    }
}
```

### 3.2 レイヤー設計

#### **Controller層**
```java
// ✅ Good: 薄いController、責任が明確
@RestController
@RequestMapping("/api/v1/orders")
@Validated
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    
    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody @Valid CreateOrderRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        
        log.info("Creating order for user: {}", user.getId());
        
        try {
            Order order = orderService.createOrder(request, user.getId());
            OrderResponse response = orderMapper.toResponse(order);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .location(URI.create("/api/v1/orders/" + order.getId()))
                    .body(response);
                    
        } catch (InsufficientInventoryException e) {
            log.warn("Insufficient inventory for order request: {}", request, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            
        } catch (PaymentProcessingException e) {
            log.error("Payment processing failed for user: {}", user.getId(), e);
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Payment processing failed");
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable @Positive Long id,
            @AuthenticationPrincipal UserPrincipal user) {
        
        return orderService.findOrderById(id, user.getId())
                .map(orderMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

// ❌ Bad: 厚いController、ビジネスロジックが混在
@RestController  
@RequestMapping("/api/v1/orders")
public class OrderController {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        // ❌ Controllerでビジネスロジック実行
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // ❌ バリデーションロジック
        if (request.getItems().isEmpty()) {
            throw new RuntimeException("Order must have items");
        }
        
        // ❌ 複雑な計算ロジック
        BigDecimal total = request.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Order order = new Order();
        order.setUser(user);
        order.setTotal(total);
        // ... 複雑なロジック続く
        
        return ResponseEntity.ok(orderRepository.save(order));
    }
}
```

#### **Service層**
```java
// ✅ Good: ビジネスロジックの中心、トランザクション管理
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final OrderValidator orderValidator;
    
    @Transactional
    public Order createOrder(CreateOrderRequest request, Long userId) {
        log.debug("Creating order for user: {} with items: {}", userId, request.getItems().size());
        
        // バリデーション
        orderValidator.validateCreateRequest(request);
        
        // ビジネスルール適用
        User user = userService.findActiveUserById(userId);
        validateUserCanCreateOrder(user);
        
        // 在庫確認
        inventoryService.reserveItems(request.getItems());
        
        try {
            // 注文作成
            Order order = buildOrder(request, user);
            
            // 支払い処理
            Payment payment = paymentService.processPayment(
                    order.getTotal(), 
                    request.getPaymentMethod()
            );
            order.setPayment(payment);
            order.setStatus(OrderStatus.CONFIRMED);
            
            // 永続化
            Order savedOrder = orderRepository.save(order);
            
            // 非同期通知
            notificationService.sendOrderConfirmationAsync(savedOrder);
            
            log.info("Order created successfully: {}", savedOrder.getId());
            return savedOrder;
            
        } catch (PaymentProcessingException e) {
            // 在庫ロールバック
            inventoryService.releaseReservation(request.getItems());
            throw e;
        }
    }
    
    public Optional<Order> findOrderById(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId);
    }
    
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        
        if (!order.isCancellable()) {
            throw new IllegalStateException("Order cannot be cancelled in current status: " + order.getStatus());
        }
        
        order.cancel();
        orderRepository.save(order);
        
        // 在庫復元
        inventoryService.releaseItems(order.getItems());
        
        // 返金処理
        if (order.getPayment() != null) {
            paymentService.refund(order.getPayment());
        }
        
        log.info("Order cancelled: {}", orderId);
    }
    
    // プライベートヘルパーメソッド
    private void validateUserCanCreateOrder(User user) {
        if (!user.isActive()) {
            throw new UserNotActiveException("User is not active: " + user.getId());
        }
        
        if (user.hasUnpaidOrders()) {
            throw new UnpaidOrdersExistException("User has unpaid orders");
        }
    }
    
    private Order buildOrder(CreateOrderRequest request, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(Instant.now());
        
        List<OrderItem> items = request.getItems().stream()
                .map(this::buildOrderItem)
                .collect(Collectors.toList());
        
        order.setItems(items);
        order.calculateTotal();
        
        return order;
    }
    
    private OrderItem buildOrderItem(CreateOrderItemRequest request) {
        OrderItem item = new OrderItem();
        item.setProductId(request.getProductId());
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(request.getUnitPrice());
        return item;
    }
}
```

#### **Repository層**
```java
// ✅ Good: データアクセスに特化、クエリが明確
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // メソッド名によるクエリ生成
    Optional<Order> findByIdAndUserId(Long id, Long userId);
    
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.createdAt >= :since ORDER BY o.createdAt DESC")
    List<Order> findRecentOrdersByUser(@Param("userId") Long userId, @Param("since") Instant since);
    
    // 複雑なクエリはJPQLで明示
    @Query("""
        SELECT o FROM Order o 
        JOIN FETCH o.items oi 
        JOIN FETCH oi.product p 
        WHERE o.user.id = :userId 
        AND o.status = :status 
        AND o.createdAt BETWEEN :startDate AND :endDate
        ORDER BY o.createdAt DESC
        """)
    List<Order> findOrdersWithItemsAndProducts(
            @Param("userId") Long userId,
            @Param("status") OrderStatus status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );
    
    // ネイティブクエリは最後の手段
    @Query(value = """
        SELECT DATE(o.created_at) as order_date, COUNT(*) as order_count, SUM(o.total) as total_amount
        FROM orders o 
        WHERE o.user_id = :userId 
        AND o.created_at >= :since
        GROUP BY DATE(o.created_at)
        ORDER BY order_date DESC
        """, nativeQuery = true)
    List<Object[]> findOrderStatisticsByUser(@Param("userId") Long userId, @Param("since") Instant since);
    
    // バルク操作
    @Modifying
    @Query("UPDATE Order o SET o.status = :newStatus WHERE o.status = :oldStatus AND o.createdAt < :cutoffDate")
    int updateExpiredOrders(
            @Param("oldStatus") OrderStatus oldStatus,
            @Param("newStatus") OrderStatus newStatus,
            @Param("cutoffDate") Instant cutoffDate
    );
}

// ❌ Bad: 複雑すぎるRepository、ビジネスロジック混在
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // ❌ 複雑すぎるメソッド名
    List<Order> findByUserIdAndStatusAndCreatedAtGreaterThanAndTotalGreaterThanOrderByCreatedAtDesc(
            Long userId, OrderStatus status, Instant createdAt, BigDecimal total);
    
    // ❌ ビジネスロジックがRepository内に
    @Query("""
        SELECT o FROM Order o WHERE o.user.id = :userId 
        AND (o.status = 'CONFIRMED' OR o.status = 'SHIPPED') 
        AND o.total > (SELECT AVG(o2.total) FROM Order o2 WHERE o2.user.id = :userId)
        AND EXISTS (SELECT 1 FROM Payment p WHERE p.order.id = o.id AND p.status = 'COMPLETED')
        """)
    List<Order> findHighValueActiveOrdersWithCompletedPayment(@Param("userId") Long userId);
}
```

---

## 4. エラーハンドリング・例外処理

### 4.1 例外設計

#### **カスタム例外の作成**
```java
// ✅ Good: 意味のある例外階層
// ベース例外
public abstract class BusinessException extends RuntimeException {
    private final String errorCode;
    
    protected BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    protected BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}

// ドメイン固有例外
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Long userId) {
        super("USER_NOT_FOUND", "User not found with ID: " + userId);
    }
}

public class InsufficientInventoryException extends BusinessException {
    private final Long productId;
    private final int requested;
    private final int available;
    
    public InsufficientInventoryException(Long productId, int requested, int available) {
        super("INSUFFICIENT_INVENTORY", 
              String.format("Insufficient inventory for product %d: requested %d, available %d", 
                          productId, requested, available));
        this.productId = productId;
        this.requested = requested;
        this.available = available;
    }
    
    // getters...
}

public class PaymentProcessingException extends BusinessException {
    public PaymentProcessingException(String message, Throwable cause) {
        super("PAYMENT_PROCESSING_ERROR", message, cause);
    }
}

// ❌ Bad: 汎用的すぎる例外
public class SystemException extends RuntimeException {
    // 何の例外かわからない
}

public class DataException extends RuntimeException {
    // 曖昧すぎる
}
```

#### **例外処理のベストプラクティス**
```java
// ✅ Good: 適切な例外処理
@Service
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public User createUser(CreateUserRequest request) {
        try {
            // バリデーション
            validateCreateUserRequest(request);
            
            // 重複チェック
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException(request.getEmail());
            }
            
            // ユーザー作成
            User user = new User();
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setCreatedAt(Instant.now());
            user.setStatus(UserStatus.ACTIVE);
            
            User savedUser = userRepository.save(user);
            log.info("User created successfully: {}", savedUser.getId());
            
            return savedUser;
            
        } catch (DataIntegrityViolationException e) {
            // データベース制約違反
            log.error("Database constraint violation while creating user: {}", request.getEmail(), e);
            throw new UserCreationException("Failed to create user due to data constraint", e);
            
        } catch (Exception e) {
            // 予期しない例外
            log.error("Unexpected error while creating user: {}", request.getEmail(), e);
            throw new UserCreationException("Unexpected error occurred while creating user", e);
        }
    }
    
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    public Optional<User> findUserByEmail(String email) {
        try {
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            log.error("Error finding user by email: {}", email, e);
            return Optional.empty();  // 検索エラーは空の結果として扱う
        }
    }
    
    private void validateCreateUserRequest(CreateUserRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        if (!isValidEmail(request.getEmail())) {
            throw new InvalidEmailFormatException(request.getEmail());
        }
        
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new WeakPasswordException("Password must be at least 8 characters");
        }
    }
}

// ❌ Bad: 不適切な例外処理
@Service
public class UserService {
    
    public User createUser(CreateUserRequest request) {
        try {
            // ❌ チェック例外を無視
            User user = new User();
            user.setEmail(request.getEmail());
            return userRepository.save(user);
            
        } catch (Exception e) {
            // ❌ 全ての例外を同じように処理
            e.printStackTrace();  // ❌ printStackTrace使用
            return null;  // ❌ null返却
        }
    }
    
    public User findUserById(Long id) {
        try {
            return userRepository.findById(id).get();  // ❌ get()で例外が発生する可能性
        } catch (Exception e) {
            throw new RuntimeException(e);  // ❌ 汎用RuntimeException
        }
    }
}
```

### 4.2 グローバル例外ハンドラー

#### **統一された例外処理**
```java
// ✅ Good: 包括的なグローバル例外ハンドラー
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn("Business exception occurred: {} - {}", e.getErrorCode(), e.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .message(e.getMessage())
                .timestamp(Instant.now())
                .build();
                
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        log.info("User not found: {}", e.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .message(e.getMessage())
                .timestamp(Instant.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException e) {
        
        log.info("Validation failed: {}", e.getMessage());
        
        Map<String, String> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));
        
        ValidationErrorResponse error = ValidationErrorResponse.builder()
                .errorCode("VALIDATION_ERROR")
                .message("Input validation failed")
                .fieldErrors(fieldErrors)
                .timestamp(Instant.now())
                .build();
                
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolationException(
            ConstraintViolationException e) {
            
        log.info("Constraint violation: {}", e.getMessage());
        
        Map<String, String> constraintErrors = e.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));
        
        ValidationErrorResponse error = ValidationErrorResponse.builder()
                .errorCode("CONSTRAINT_VIOLATION")
                .message("Constraint validation failed")
                .fieldErrors(constraintErrors)
                .timestamp(Instant.now())
                .build();
                
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("Data integrity violation", e);
        
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("DATA_INTEGRITY_ERROR")
                .message("Data integrity constraint violated")
                .timestamp(Instant.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("ACCESS_DENIED")
                .message("Access denied")
                .timestamp(Instant.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected error occurred", e);
        
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .timestamp(Instant.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

// エラーレスポンスDTO
@Data
@Builder
public class ErrorResponse {
    private String errorCode;
    private String message;
    private Instant timestamp;
}

@Data
@Builder
public class ValidationErrorResponse {
    private String errorCode;
    private String message;
    private Map<String, String> fieldErrors;
    private Instant timestamp;
}
```

---

## 5. バリデーション・セキュリティ

### 5.1 入力バリデーション

#### **Bean Validationの活用**
```java
// ✅ Good: 包括的なバリデーション
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name can only contain letters and spaces")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name can only contain letters and spaces")
    private String lastName;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character"
    )
    private String password;
    
    @Past(message = "Birth date must be in the past")
    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;
    
    @Valid
    @NotNull(message = "Address is required")
    private AddressRequest address;
    
    @NotEmpty(message = "At least one phone number is required")
    @Size(max = 3, message = "Maximum 3 phone numbers allowed")
    private List<@Valid PhoneNumberRequest> phoneNumbers;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    
    @NotBlank(message = "Street address is required")
    @Size(max = 200, message = "Street address must not exceed 200 characters")
    private String street;
    
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^\\d{3}-\\d{4}$", message = "Postal code must be in format XXX-XXXX")
    private String postalCode;
    
    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 2, message = "Country code must be 2 characters")
    private String countryCode;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneNumberRequest {
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String number;
    
    @NotNull(message = "Phone type is required")
    private PhoneType type;
}

// カスタムバリデーション
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgeValidator.class)
@Documented
public @interface ValidAge {
    String message() default "Age must be between 18 and 120";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    int min() default 18;
    int max() default 120;
}

public class AgeValidator implements ConstraintValidator<ValidAge, CreateUserRequest> {
    
    private int min;
    private int max;
    
    @Override
    public void initialize(ValidAge constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }
    
    @Override
    public boolean isValid(CreateUserRequest request, ConstraintValidatorContext context) {
        if (request.getBirthDate() == null) {
            return true; // @NotNullで別途チェック
        }
        
        int age = Period.between(request.getBirthDate(), LocalDate.now()).getYears();
        return age >= min && age <= max;
    }
}

// ❌ Bad: 不十分なバリデーション
@Data
public class CreateUserRequest {
    private String email;        // バリデーションなし
    private String firstName;    // バリデーションなし
    private String password;     // バリデーションなし
}
```

#### **プログラマティックバリデーション**
```java
// ✅ Good: 複雑なビジネスルールのバリデーション
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderValidator {
    
    private final UserService userService;
    private final ProductService productService;
    private final InventoryService inventoryService;
    
    public void validateCreateOrderRequest(CreateOrderRequest request) {
        validateBasicFields(request);
        validateOrderItems(request.getItems());
        validateUserEligibility(request.getUserId());
        validateInventoryAvailability(request.getItems());
    }
    
    private void validateBasicFields(CreateOrderRequest request) {
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new IllegalArgumentException("Valid user ID is required");
        }
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        
        if (request.getItems().size() > 50) {
            throw new IllegalArgumentException("Order cannot contain more than 50 items");
        }
    }
    
    private void validateOrderItems(List<CreateOrderItemRequest> items) {
        for (CreateOrderItemRequest item : items) {
            validateOrderItem(item);
        }
        
        // 重複商品チェック
        Set<Long> productIds = items.stream()
                .map(CreateOrderItemRequest::getProductId)
                .collect(Collectors.toSet());
                
        if (productIds.size() != items.size()) {
            throw new IllegalArgumentException("Duplicate products are not allowed in the same order");
        }
    }
    
    private void validateOrderItem(CreateOrderItemRequest item) {
        if (item.getProductId() == null || item.getProductId() <= 0) {
            throw new IllegalArgumentException("Valid product ID is required");
        }
        
        if (item.getQuantity() == null || item.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        if (item.getQuantity() > 100) {
            throw new IllegalArgumentException("Maximum quantity per item is 100");
        }
        
        // 商品存在チェック
        if (!productService.existsById(item.getProductId())) {
            throw new ProductNotFoundException(item.getProductId());
        }
        
        // 商品が注文可能状態かチェック
        Product product = productService.findById(item.getProductId());
        if (!product.isOrderable()) {
            throw new ProductNotOrderableException(item.getProductId());
        }
    }
    
    private void validateUserEligibility(Long userId) {
        User user = userService.findById(userId);
        
        if (!user.isActive()) {
            throw new UserNotActiveException("User account is not active");
        }
        
        if (user.isSuspended()) {
            throw new UserSuspendedException("User account is suspended");
        }
        
        // 未払い注文チェック
        if (userService.hasUnpaidOrders(userId)) {
            throw new UnpaidOrdersExistException("User has unpaid orders");
        }
        
        // 注文頻度制限チェック
        int recentOrderCount = userService.getRecentOrderCount(userId, Duration.ofHours(1));
        if (recentOrderCount >= 10) {
            throw new TooManyOrdersException("Too many orders in the last hour");
        }
    }
    
    private void validateInventoryAvailability(List<CreateOrderItemRequest> items) {
        for (CreateOrderItemRequest item : items) {
            int availableQuantity = inventoryService.getAvailableQuantity(item.getProductId());
            
            if (availableQuantity < item.getQuantity()) {
                throw new InsufficientInventoryException(
                        item.getProductId(), 
                        item.getQuantity(), 
                        availableQuantity
                );
            }
        }
    }
}
```

### 5.2 セキュリティ対策

#### **認証・認可**
```java
// ✅ Good: Spring Securityの適切な設定
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);  // 強力なハッシュ強度
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // RESTful APIの場合はCSRF無効化
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            )
            
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/public/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasAnyRole("ADMIN", "MODERATOR")
                
                .requestMatchers("/api/v1/users/{userId}/**")
                    .access("@userSecurityService.canAccessUser(authentication, #userId)")
                
                .anyRequest().authenticated()
            )
            
            .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
    
    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtTokenProvider);
    }
}

// カスタムセキュリティサービス
@Service
@RequiredArgsConstructor
public class UserSecurityService {
    
    private final UserService userService;
    
    public boolean canAccessUser(Authentication authentication, Long userId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        
        // 管理者は全ユーザーにアクセス可能
        if (principal.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }
        
        // 自分自身のデータにのみアクセス可能
        return principal.getId().equals(userId);
    }
    
    public boolean canModifyOrder(Authentication authentication, Long orderId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        
        // 管理者は全注文を変更可能
        if (principal.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }
        
        // 注文の所有者のみ変更可能
        return userService.isOrderOwner(principal.getId(), orderId);
    }
}

// メソッドレベルセキュリティ
@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('USER')")
@Validated
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/{userId}")
    @PreAuthorize("@userSecurityService.canAccessUser(authentication, #userId)")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        User user = userService.findById(userId);
        return ResponseEntity.ok(UserMapper.toResponse(user));
    }
    
    @PutMapping("/{userId}")
    @PreAuthorize("@userSecurityService.canAccessUser(authentication, #userId)")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @RequestBody @Valid UpdateUserRequest request) {
        
        User updatedUser = userService.updateUser(userId, request);
        return ResponseEntity.ok(UserMapper.toResponse(updatedUser));
    }
    
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.canAccessUser(authentication, #userId)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
```

#### **SQLインジェクション対策**
```java
// ✅ Good: パラメータ化クエリの使用
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // メソッド名によるクエリ - 安全
    Optional<User> findByEmailIgnoreCase(String email);
    
    List<User> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
            String firstName, String lastName);
    
    // パラメータ化JPQL - 安全
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.status = :status")
    Optional<User> findByEmailAndStatus(@Param("email") String email, @Param("status") UserStatus status);
    
    // 複雑な検索もパラメータ化
    @Query("""
        SELECT u FROM User u 
        WHERE (:email IS NULL OR u.email LIKE %:email%) 
        AND (:firstName IS NULL OR u.firstName LIKE %:firstName%) 
        AND (:status IS NULL OR u.status = :status)
        AND u.createdAt BETWEEN :startDate AND :endDate
        ORDER BY u.createdAt DESC
        """)
    Page<User> findUsersWithFilters(
            @Param("email") String email,
            @Param("firstName") String firstName,
            @Param("status") UserStatus status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable
    );
    
    // ネイティブクエリもパラメータ化必須
    @Query(value = """
        SELECT u.* FROM users u 
        LEFT JOIN user_roles ur ON u.id = ur.user_id 
        LEFT JOIN roles r ON ur.role_id = r.id 
        WHERE r.name = :roleName 
        AND u.created_at >= :since
        """, nativeQuery = true)
    List<User> findUsersByRole(@Param("roleName") String roleName, @Param("since") Instant since);
}

// ❌ Bad: SQLインジェクションの脆弱性
@Repository
public class UnsafeUserRepository {
    
    @Autowired
    private EntityManager entityManager;
    
    // ❌ 危険: 文字列結合によるクエリ構築
    public List<User> findUsersByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = '" + email + "'";  // SQLインジェクション脆弱性
        Query query = entityManager.createNativeQuery(sql, User.class);
        return query.getResultList();
    }
    
    // ❌ 危険: 動的クエリの不適切な構築
    public List<User> searchUsers(String searchTerm) {
        String jpql = "SELECT u FROM User u WHERE u.firstName LIKE '%" + searchTerm + "%'";  // 危険
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        return query.getResultList();
    }
}
```

#### **XSS・データサニタイゼーション**
```java
// ✅ Good: 適切な入力サニタイゼーション
@Component
public class InputSanitizer {
    
    private static final String[] XSS_PATTERNS = {
        "<script", "</script>", "javascript:", "onclick=", "onerror=", "onload=",
        "<iframe", "</iframe>", "<object", "</object>", "<embed", "</embed>"
    };
    
    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        String sanitized = input.trim();
        
        // HTMLエンコード
        sanitized = StringEscapeUtils.escapeHtml4(sanitized);
        
        // 危険なパターンを除去
        for (String pattern : XSS_PATTERNS) {
            sanitized = sanitized.replaceAll("(?i)" + Pattern.quote(pattern), "");
        }
        
        // 連続する空白を単一空白に変換
        sanitized = sanitized.replaceAll("\\s+", " ");
        
        return sanitized;
    }
    
    public String sanitizeForDatabase(String input) {
        if (input == null) {
            return null;
        }
        
        // SQLインジェクション対策（JPA使用時は通常不要だが念のため）
        return input.replaceAll("['\"\\\\]", "");
    }
}

// バリデーションでの使用例
@Component
@RequiredArgsConstructor
public class CommentValidator {
    
    private final InputSanitizer inputSanitizer;
    
    public void validateAndSanitizeComment(CreateCommentRequest request) {
        // サニタイゼーション
        String sanitizedContent = inputSanitizer.sanitizeInput(request.getContent());
        request.setContent(sanitizedContent);
        
        // バリデーション
        if (sanitizedContent == null || sanitizedContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content is required");
        }
        
        if (sanitizedContent.length() > 1000) {
            throw new IllegalArgumentException("Comment content must not exceed 1000 characters");
        }
        
        // 不適切な内容のチェック
        if (containsInappropriateContent(sanitizedContent)) {
            throw new InappropriateContentException("Comment contains inappropriate content");
        }
    }
    
    private boolean containsInappropriateContent(String content) {
        // 不適切な単語のチェックロジック
        // 実際のプロジェクトでは外部サービスや設定ファイルを使用
        return false;
    }
}
```

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

