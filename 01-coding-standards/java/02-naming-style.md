# Naming Conventions & Code Style

**説明**: 命名規則、コードフォーマット、スタイルガイドライン

**主要トピック**:
- パッケージ、クラス、メソッド、変数の命名規則
- インデント、改行、空白のルール
- Google Java Style Guideの適用

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


