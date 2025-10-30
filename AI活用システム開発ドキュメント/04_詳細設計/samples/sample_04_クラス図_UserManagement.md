# クラス図 - ユーザー管理モジュール

**プロジェクト名:** ECサイト構築プロジェクト  
**ドキュメントID:** CLASS-USER-001  
**バージョン:** 1.0  
**作成日:** 2025-10-30  
**作成者:** 詳細設計者

---

## 1. 基本情報

### 1.1 目的

ユーザー管理モジュールのクラス構造を定義し、クラス間の関係を明確にする。

### 1.2 対象範囲

- Controller層（プレゼンテーション）
- Service層（ビジネスロジック）
- Repository層（データアクセス）
- Entity層（ドメインモデル）
- DTO層（データ転送オブジェクト）

---

## 2. 全体クラス図

```mermaid
classDiagram
    %% Controller層
    class UserController {
        -UserService userService
        +register(request: UserCreateRequest) ResponseEntity~UserResponse~
        +login(request: LoginRequest) ResponseEntity~AuthResponse~
        +getProfile() ResponseEntity~UserResponse~
        +updateProfile(request: ProfileUpdateRequest) ResponseEntity~UserResponse~
    }
    
    class AuthController {
        -UserService userService
        +login(request: LoginRequest) ResponseEntity~AuthResponse~
        +refresh(request: RefreshTokenRequest) ResponseEntity~AuthResponse~
        +logout() ResponseEntity~Void~
    }
    
    %% Service層
    class UserService {
        -UserRepository userRepository
        -PasswordEncoder passwordEncoder
        -EmailService emailService
        -JwtTokenProvider jwtTokenProvider
        +createUser(request: UserCreateRequest) UserDto
        +authenticate(email: String, password: String) AuthResponse
        +getUserById(userId: Long) UserDto
        +updateProfile(userId: Long, request: ProfileUpdateRequest) UserDto
        +changePassword(userId: Long, request: PasswordChangeRequest) void
    }
    
    class EmailService {
        -SendGrid sendGrid
        +sendWelcomeEmail(email: String, name: String) void
        +sendPasswordResetEmail(email: String, token: String) void
    }
    
    class JwtTokenProvider {
        -String jwtSecret
        +generateAccessToken(user: User) String
        +generateRefreshToken(user: User) String
        +validateToken(token: String) boolean
        +getUserIdFromToken(token: String) Long
    }
    
    %% Repository層
    class UserRepository {
        <<interface>>
        +save(user: User) User
        +findById(id: Long) Optional~User~
        +findByEmail(email: String) Optional~User~
        +existsByEmail(email: String) boolean
    }
    
    %% Entity層
    class User {
        -Long id
        -String email
        -String password
        -String name
        -String phone
        -UserRole role
        -UserStatus status
        -boolean emailVerified
        -LocalDateTime lastLoginAt
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
        +getId() Long
        +setLastLoginAt(time: LocalDateTime) void
    }
    
    class UserRole {
        <<enumeration>>
        CUSTOMER
        ADMIN
    }
    
    class UserStatus {
        <<enumeration>>
        ACTIVE
        SUSPENDED
        DELETED
    }
    
    %% DTO層
    class UserDto {
        -Long id
        -String email
        -String name
        -String phone
        -String role
        -boolean emailVerified
        -LocalDateTime createdAt
        +from(user: User)$ UserDto
    }
    
    class UserCreateRequest {
        -String email
        -String password
        -String name
        -String phone
    }
    
    class AuthResponse {
        -String accessToken
        -String refreshToken
        -String tokenType
        -int expiresIn
        -UserDto user
    }
    
    %% 関係
    UserController --> UserService
    AuthController --> UserService
    UserService --> UserRepository
    UserService --> EmailService
    UserService --> JwtTokenProvider
    UserRepository --> User
    User --> UserRole
    User --> UserStatus
    UserDto --> User : converts
    UserService --> UserDto : returns
```

---

## 3. 層別クラス詳細

### 3.1 Controller層

#### UserController

```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody UserCreateRequest request) {
        UserDto userDto = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.from(userDto));
    }
    
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        UserDto userDto = userService.getUserById(principal.getId());
        return ResponseEntity.ok(UserResponse.from(userDto));
    }
    
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ProfileUpdateRequest request) {
        UserDto userDto = userService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(UserResponse.from(userDto));
    }
}
```

---

### 3.2 Entity層

#### User（ユーザーエンティティ）

```mermaid
classDiagram
    class User {
        -Long id
        -String email
        -String password
        -String name
        -String phone
        -UserRole role
        -UserStatus status
        -boolean emailVerified
        -LocalDateTime lastLoginAt
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
        -Long version
        +User()
        +builder() UserBuilder
        +getId() Long
        +getEmail() String
        +setLastLoginAt(LocalDateTime) void
    }
    
    class UserRole {
        <<enumeration>>
        CUSTOMER
        ADMIN
    }
    
    class UserStatus {
        <<enumeration>>
        ACTIVE
        SUSPENDED
        DELETED
    }
    
    User --> UserRole
    User --> UserStatus
```

**実装:**

```java
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 20)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;
    
    @Column(nullable = false)
    private boolean emailVerified;
    
    @Column
    private LocalDateTime lastLoginAt;
    
    @Version
    private Long version;  // 楽観的ロック
    
    // Setterメソッド
    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}
```

---

### 3.3 DTO層

#### UserDto（データ転送オブジェクト）

```mermaid
classDiagram
    class UserDto {
        -Long id
        -String email
        -String name
        -String phone
        -String role
        -boolean emailVerified
        -LocalDateTime createdAt
        +from(user: User)$ UserDto
        +builder() UserDtoBuilder
    }
    
    class UserCreateRequest {
        -String email
        -String password
        -String name
        -String phone
        +validate() void
    }
    
    class ProfileUpdateRequest {
        -String name
        -String phone
    }
    
    class UserResponse {
        -Long id
        -String email
        -String name
        -String phone
        -String role
        -boolean emailVerified
        -LocalDateTime createdAt
        -Links _links
        +from(dto: UserDto)$ UserResponse
    }
```

**実装:**

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private String role;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    
    /**
     * EntityからDTOへの変換
     */
    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "メールアドレスの形式が不正です")
    private String email;
    
    @NotBlank(message = "パスワードは必須です")
    @Size(min = 8, message = "パスワードは8文字以上必要です")
    private String password;
    
    @NotBlank(message = "名前は必須です")
    @Size(max = 100, message = "名前は100文字以内です")
    private String name;
    
    @Pattern(regexp = "^0\\d{1,4}-\\d{1,4}-\\d{4}$", 
             message = "電話番号の形式が不正です")
    private String phone;
}
```

---

## 4. デザインパターン適用

### 4.1 Repository パターン

```mermaid
classDiagram
    class UserRepository {
        <<interface>>
        +save(user: User) User
        +findById(id: Long) Optional~User~
        +findByEmail(email: String) Optional~User~
        +existsByEmail(email: String) boolean
    }
    
    class JpaRepository {
        <<interface>>
    }
    
    UserRepository --|> JpaRepository
```

**利点:**
- データアクセス層の抽象化
- テスト容易性の向上
- ビジネスロジックとデータアクセスの分離

---

### 4.2 DTO パターン

```mermaid
graph LR
    Controller[Controller] -->|UserCreateRequest| Service[Service]
    Service -->|UserDto| Controller
    Service -->|User Entity| Repository[Repository]
    Repository -->|User Entity| Service
    
    style Service fill:#1168bd,color:#ffffff
```

**利点:**
- Entity の直接露出を防止
- レイヤー間のデータ転送最適化
- API バージョニングの容易化

---

## 5. クラス関係図

### 5.1 依存関係

```mermaid
graph TD
    UC[UserController] --> US[UserService]
    AC[AuthController] --> US
    
    US --> UR[UserRepository]
    US --> ES[EmailService]
    US --> JWT[JwtTokenProvider]
    US --> PE[PasswordEncoder]
    
    UR --> UE[User Entity]
    
    US --> UD[UserDto]
    UC --> UCR[UserCreateRequest]
    UC --> UR2[UserResponse]
    
    style US fill:#1168bd,color:#ffffff
    style UE fill:#51cf66,color:#ffffff
```

### 5.2 パッケージ構成

```
com.ecshop
├── controller
│   ├── UserController.java
│   └── AuthController.java
├── service
│   ├── UserService.java
│   ├── EmailService.java
│   └── JwtTokenProvider.java
├── repository
│   └── UserRepository.java
├── entity
│   ├── User.java
│   ├── UserRole.java (enum)
│   └── UserStatus.java (enum)
└── dto
    ├── UserDto.java
    ├── UserCreateRequest.java
    ├── ProfileUpdateRequest.java
    ├── UserResponse.java
    └── AuthResponse.java
```

---

## 6. シーケンス図（ユーザー登録）

```mermaid
sequenceDiagram
    participant C as Client
    participant UC as UserController
    participant US as UserService
    participant UR as UserRepository
    participant ES as EmailService
    
    C->>UC: POST /api/v1/users<br/>{email, password, name}
    UC->>UC: @Valid バリデーション
    UC->>US: createUser(request)
    
    US->>UR: existsByEmail(email)
    UR-->>US: false
    
    US->>US: passwordEncoder.encode(password)
    US->>US: User Entity 作成
    
    US->>UR: save(user)
    UR-->>US: User (id=123)
    
    US->>ES: sendWelcomeEmail(email, name)
    ES-->>US: async (非同期)
    
    US->>US: UserDto.from(user)
    US-->>UC: UserDto
    
    UC->>UC: UserResponse.from(userDto)
    UC-->>C: 201 Created<br/>UserResponse
```

---

## 7. 変更履歴

| バージョン | 日付 | 変更内容 | 変更者 |
|-----------|------|---------|--------|
| 1.0 | 2025-10-30 | 初版作成 | 詳細設計者 |

---

**ドキュメント終了**
