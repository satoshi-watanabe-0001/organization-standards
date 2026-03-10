# Error Handling & Validation

**説明**: 例外処理、バリデーション、セキュリティ対策

**主要トピック**:
- カスタム例外の作成
- グローバル例外ハンドラー
- Bean Validationの活用
- 認証・認可の実装
- SQLインジェクション、XSS対策

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


