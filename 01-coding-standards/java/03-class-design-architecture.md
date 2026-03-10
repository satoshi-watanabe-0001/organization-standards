# Class Design & Architecture

**説明**: クラス設計原則、レイヤー設計、依存性注入

**主要トピック**:
- 単一責任原則（SRP）の適用
- 依存性注入（DI）の活用
- Controller、Service、Repository層の設計
- レイヤー間の責務分離

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


