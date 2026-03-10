# Java テストコメント実装例

## 📖 概要

本ドキュメントは、Java（JUnit 5/Mockito）におけるテストコメントの具体例を提供します。

**共通原則**: [00-test-comment-standards.md](../00-test-comment-standards.md)を必ず参照してください。

---

## 🧪 JUnit 5スタイル

### 1. 基本的なテストケース

```java
@Test
@DisplayName("有効なデータでのユーザー作成")
void testCreateUserWithValidData() {
    /**
     * 【テスト対象】: 有効なデータでのユーザー作成
     * 
     * 【テストケース】:
     * 必須項目（メール、パスワード、名前）をすべて指定し、
     * 正常にユーザーが作成されることを検証する。
     * 
     * 【期待結果】:
     * - ユーザーオブジェクトが返される
     * - データベースにレコードが保存される
     * - パスワードがハッシュ化される
     * - IDと作成日時が自動設定される
     * 
     * 【ビジネス要件】:
     * [REQ-USER-001] 有効なデータでユーザー登録ができること
     */
    
    // === Given: テストデータの準備 ===
    // 有効なユーザーデータ（すべての必須項目を含む）
    UserCreateRequest request = UserCreateRequest.builder()
        .email("test@example.com")
        .password("SecurePass123!")
        .name("テストユーザー")
        .build();
    
    // === When: ユーザー作成を実行 ===
    User user = userService.createUser(request);
    
    // === Then: 結果を検証 ===
    // ユーザーオブジェクトが正常に生成されていることを確認
    assertNotNull(user);
    assertEquals("test@example.com", user.getEmail());
    assertEquals("テストユーザー", user.getName());
    
    // パスワードがハッシュ化されていることを確認
    // セキュリティ要件: 平文パスワードは保存禁止
    assertNotEquals("SecurePass123!", user.getPassword());
    assertTrue(user.getPassword().startsWith("$2a$"));  // BCryptハッシュの識別子
    
    // 自動設定項目の確認
    assertNotNull(user.getId());  // IDが発行されている
    assertNotNull(user.getCreatedAt());  // 作成日時が記録されている
    
    // データベース永続化の確認
    // データベースから取得して、正しく保存されているか検証
    User savedUser = userRepository.findById(user.getId()).orElseThrow();
    assertEquals("test@example.com", savedUser.getEmail());
}
```

---

### 2. ParameterizedTestを使ったテーブル駆動テスト

```java
@ParameterizedTest
@DisplayName("パスワードバリデーション")
@MethodSource("passwordTestCases")
void testPasswordValidation(String password, boolean expectedValid, String reason) {
    /**
     * 【テスト対象】: パスワードバリデーション
     * 
     * 【テストケース】:
     * 様々なパターンのパスワードが、セキュリティポリシーに従って
     * 正しく検証されることを確認する。
     * 
     * 【期待結果】:
     * - 正常系: 英数字+記号を含む8文字以上のパスワードが有効
     * - 異常系: 要件を満たさないパスワードが無効
     * - 境界値: 最小/最大長が正しく判定される
     * 
     * 【ビジネス要件】:
     * [REQ-SEC-002] パスワードは8文字以上、英大文字・小文字・数字・記号を含むこと
     */
    
    // パスワードバリデーションを実行
    boolean isValid = passwordValidator.validate(password);
    
    // 期待結果と一致することを確認
    // テスト失敗時にreasonが表示され、どのケースで失敗したか分かる
    assertEquals(expectedValid, isValid, 
        String.format("テスト失敗: %s (パスワード: %s)", reason, password));
}

/**
 * パスワードバリデーションのテストケース
 */
private static Stream<Arguments> passwordTestCases() {
    return Stream.of(
        // 【正常系】
        Arguments.of("ValidPass123!", true, "英数字+記号を含む8文字（最小要件）"),
        Arguments.of("SecurePassword2025!", true, "長いパスワード（推奨）"),
        Arguments.of("パスワード123!", true, "日本語を含むパスワード（許可）"),
        
        // 【異常系: 長さ不足】
        Arguments.of("Pass12!", false, "7文字（8文字未満）"),
        Arguments.of("Abc1!", false, "5文字（短すぎる）"),
        
        // 【異常系: 複雑度不足】
        Arguments.of("password123!", false, "大文字なし"),
        Arguments.of("PASSWORD123!", false, "小文字なし"),
        Arguments.of("PasswordABC!", false, "数字なし"),
        Arguments.of("Password123", false, "記号なし"),
        
        // 【境界値】
        Arguments.of("Pass123!", true, "最小要件をちょうど満たす（8文字、英大小数記号）"),
        Arguments.of("a".repeat(128) + "A1!", true, "最大長（128文字）"),
        Arguments.of("a".repeat(129) + "A1!", false, "最大長超過（129文字）")
    );
}
```

---

### 3. Mockitoを使ったモックテスト

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private EmailService emailService;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    @DisplayName("ユーザー登録時のウェルカムメール送信")
    void testSendWelcomeEmailOnRegistration() {
        /**
         * 【テスト対象】: ユーザー登録時のウェルカムメール送信
         * 
         * 【テストケース】:
         * 新規ユーザーが登録した際、ウェルカムメールが送信されることを検証。
         * 実際のメール送信サービスは呼び出さず、モックで代替する。
         * 
         * 【期待結果】:
         * - ユーザー登録が成功する
         * - sendEmailメソッドが1回呼ばれる
         * - 送信先がユーザーのメールアドレスである
         * - 件名が「登録ありがとうございます」である
         * 
         * 【ビジネス要件】:
         * [REQ-USER-002] 登録完了後、ウェルカムメールを送信すること
         */
        
        // === Given: メール送信サービスをモック ===
        // モック設定の理由:
        // 1. 実際のメール送信は時間がかかる（テストが遅くなる）
        // 2. 外部サービスの状態に依存しない（テストの安定性向上）
        // 3. 実際にメールが送信されない（テスト用アドレスの汚染を防ぐ）
        
        // Repositoryのモック設定
        User savedUser = User.builder()
            .id(1L)
            .email("newuser@example.com")
            .name("新規ユーザー")
            .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // === When: ユーザー登録を実行 ===
        UserCreateRequest request = UserCreateRequest.builder()
            .email("newuser@example.com")
            .password("ValidPass123!")
            .name("新規ユーザー")
            .build();
        
        User user = userService.createUser(request);
        
        // === Then: メール送信が呼ばれたことを検証 ===
        // sendEmailが1回だけ呼ばれたことを確認
        verify(emailService, times(1)).sendEmail(
            eq("newuser@example.com"),     // 送信先
            eq("登録ありがとうございます"),  // 件名
            contains("新規ユーザー")        // 本文にユーザー名が含まれる
        );
        
        // ユーザーが正常に作成されていることも確認
        assertNotNull(user);
        assertEquals("newuser@example.com", user.getEmail());
    }
}
```

---

### 4. 例外テスト

```java
@Test
@DisplayName("メールアドレス重複時のエラー")
void testCreateUserWithDuplicateEmailRaisesError() {
    /**
     * 【テスト対象】: メールアドレス重複時のエラー
     * 
     * 【テストケース】:
     * 既存ユーザーと同じメールアドレスでユーザー作成を試みた場合、
     * DuplicateEmailException例外が発生することを検証。
     * 
     * 【期待結果】:
     * - DuplicateEmailException例外がthrowされる
     * - エラーメッセージが「このメールアドレスは既に使用されています」
     * - データベースにユーザーが追加されない（1件のまま）
     * 
     * 【ビジネス要件】:
     * [REQ-USER-003] メールアドレスは一意であること
     */
    
    // === Given: 既存ユーザーを作成 ===
    // メールアドレス: existing@example.com のユーザーを作成
    UserCreateRequest existingRequest = UserCreateRequest.builder()
        .email("existing@example.com")
        .password("Pass123!")
        .name("既存ユーザー")
        .build();
    userService.createUser(existingRequest);
    
    // === When & Then: 同じメールアドレスで作成し、例外を期待 ===
    // assertThrowsで例外をキャッチ
    UserCreateRequest duplicateRequest = UserCreateRequest.builder()
        .email("existing@example.com")  // 既存と同じメールアドレス
        .password("NewPass456!")
        .name("新規ユーザー")
        .build();
    
    DuplicateEmailException exception = assertThrows(
        DuplicateEmailException.class,
        () -> userService.createUser(duplicateRequest)
    );
    
    // 例外メッセージが適切であることを確認
    // ユーザーに表示されるエラーメッセージなので、日本語で分かりやすい内容
    assertEquals("このメールアドレスは既に使用されています", exception.getMessage());
    
    // データベースにユーザーが追加されていないことを確認
    // つまり、重複登録が実際に防止された
    assertEquals(1, userRepository.count());  // 既存ユーザー1件のみ
}
```

---

## 🗄️ データベーステスト（Spring Boot Test）

### 1. トランザクション制御

```java
@SpringBootTest
@Transactional
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    @DisplayName("エラー時のトランザクションロールバック")
    void testRollbackOnError() {
        /**
         * 【テスト対象】: エラー時のトランザクションロールバック
         * 
         * 【テストケース】:
         * ユーザー作成中にエラーが発生した場合、
         * トランザクションがロールバックされ、データベースに影響がないことを検証。
         * 
         * 【期待結果】:
         * - 例外が発生する
         * - データベースにユーザーが追加されない
         * - トランザクションが正しくロールバックされる
         * 
         * 【ビジネス要件】:
         * [REQ-DATA-001] エラー時はトランザクションをロールバックし、データ整合性を保つこと
         */
        
        // === Given: 初期状態のユーザー数を記録 ===
        long initialCount = userRepository.count();
        
        // === When: エラーが発生するユーザー作成 ===
        // 無効なメールアドレスでユーザー作成を試みる
        // データベース制約違反によりエラーが発生するはず
        User invalidUser = User.builder()
            .email("invalid-email")  // 無効な形式
            .password("Pass123!")
            .name("テストユーザー")
            .build();
        
        // DataIntegrityViolationException が発生することを期待
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(invalidUser);
            userRepository.flush();  // ここでバリデーションエラー
        });
        
        // === Then: ロールバック確認 ===
        // ユーザー数が変わっていないことを確認
        // つまり、トランザクションが正しくロールバックされた
        assertEquals(initialCount, userRepository.count());
        
        // データベース接続が正常であることを確認
        // ロールバック後も、データベース操作が可能
        User validUser = User.builder()
            .email("valid@example.com")
            .password("Pass123!")
            .name("正常ユーザー")
            .build();
        User savedUser = userRepository.save(validUser);
        assertNotNull(savedUser.getId());
    }
}
```

---

## 🔐 セキュリティテスト

### 1. SQLインジェクション対策

```java
@Test
@DisplayName("ユーザー検索におけるSQLインジェクション対策")
void testPreventSqlInjectionInUserSearch() {
    /**
     * 【テスト対象】: ユーザー検索におけるSQLインジェクション対策
     * 
     * 【テストケース】:
     * SQLインジェクション攻撃を試み、適切にエスケープされることを検証。
     * 攻撃者が悪意あるSQL文を検索クエリに含めても、
     * データベースに影響がないことを確認。
     * 
     * 【期待結果】:
     * - SQLインジェクションが成功しない
     * - データベースが破壊されない
     * - 安全に検索結果が返される
     * 
     * 【ビジネス要件】:
     * [REQ-SEC-010] すべての入力値はサニタイズし、SQLインジェクションを防止すること
     */
    
    // === Given: テスト用ユーザーを作成 ===
    User user1 = userRepository.save(User.builder()
        .email("user1@example.com")
        .password("Pass1!")
        .name("ユーザー1")
        .build());
    
    User user2 = userRepository.save(User.builder()
        .email("user2@example.com")
        .password("Pass2!")
        .name("ユーザー2")
        .build());
    
    long initialCount = userRepository.count();
    
    // === When: SQLインジェクション攻撃を試行 ===
    // 攻撃シミュレーション: "'; DROP TABLE users; --" という文字列を検索
    // 不適切な実装では、以下のSQL文が実行されてしまう:
    //   SELECT * FROM users WHERE name LIKE '%'; DROP TABLE users; --%';
    // この場合、usersテーブルが削除される
    String maliciousQuery = "'; DROP TABLE users; --";
    
    // 検索を実行
    // 正しい実装では、プレースホルダーを使用し、文字列としてエスケープされる
    List<User> results = userRepository.searchByName(maliciousQuery);
    
    // === Then: セキュリティ検証 ===
    // 1. 検索結果が安全に返される（エラーにならない）
    assertNotNull(results);
    
    // 2. 悪意あるクエリに一致するユーザーはいない（空の結果）
    assertTrue(results.isEmpty());
    
    // 3. データベースが破壊されていないことを確認
    // usersテーブルが削除されていない
    assertEquals(initialCount, userRepository.count());
    
    // 4. 正常な検索が可能であることを確認
    // つまり、テーブル構造が保持されている
    List<User> normalResults = userRepository.searchByName("ユーザー1");
    assertEquals(1, normalResults.size());
    assertEquals("ユーザー1", normalResults.get(0).getName());
}
```

---

## 📊 パフォーマンステスト

### 1. レスポンスタイム測定

```java
@Test
@DisplayName("大量ユーザー作成のパフォーマンス")
void testBulkUserCreationPerformance() {
    /**
     * 【テスト対象】: 大量ユーザー作成のパフォーマンス
     * 
     * 【テストケース】:
     * 1,000件のユーザーを一括作成し、処理時間が5秒以内であることを検証。
     * バッチインサートを使用することで、1件ずつ作成するより高速であることを確認。
     * 
     * 【期待結果】:
     * - 1,000件のユーザーが作成される
     * - 処理時間が5秒以内
     * - メモリ使用量が適切（メモリリークなし）
     * 
     * 【ビジネス要件】:
     * [REQ-PERF-002] 1,000件のユーザー一括作成は5秒以内に完了すること
     */
    
    // === Given: 1,000件のユーザーデータを生成 ===
    // メモリ効率を考慮し、Streamを使用
    List<User> userList = IntStream.range(0, 1000)
        .mapToObj(i -> User.builder()
            .email(String.format("user%d@example.com", i))
            .password("Pass123!")
            .name(String.format("ユーザー%d", i))
            .build())
        .collect(Collectors.toList());
    
    // === When: バッチインサートを実行 ===
    // 処理時間を計測
    long startTime = System.currentTimeMillis();
    
    // バッチインサートの利点:
    // - 1件ずつSAVEするより100倍高速
    // - データベース接続の回数が減る
    // - トランザクションオーバーヘッドが減る
    userRepository.saveAll(userList);
    userRepository.flush();
    
    long elapsedTime = System.currentTimeMillis() - startTime;
    
    // === Then: パフォーマンス検証 ===
    // 処理時間が要件（5秒 = 5000ms）を満たすことを確認
    assertTrue(elapsedTime < 5000,
        String.format("パフォーマンス要件を満たしていません: %dms（要件: 5000ms以内）", elapsedTime));
    
    // すべてのユーザーが作成されたことを確認
    assertEquals(1000, userRepository.count());
    
    // パフォーマンスメトリクスをログに記録
    // CI/CDで経時変化をモニタリングし、パフォーマンス劣化を検出
    logger.info("Performance Test - bulk_user_creation: {} records in {}ms",
        1000, elapsedTime);
}
```

---

## 🧩 統合テスト（Spring Boot Test）

### 1. REST APIの統合テスト

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserApiIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @DisplayName("ユーザー登録から初回ログインまでの完全フロー")
    void testUserRegistrationToFirstLoginFlow() throws Exception {
        /**
         * 【テスト対象】: ユーザー登録から初回ログインまでの完全フロー
         * 
         * 【テストシナリオ】:
         * 1. ユーザーが登録フォームから情報を入力
         * 2. 登録APIが呼ばれ、データベースにユーザーが作成される
         * 3. 確認メールが送信される
         * 4. ユーザーがメール内のリンクをクリック（メール認証）
         * 5. アカウントが有効化される
         * 6. ユーザーがログインできる
         * 
         * 【期待結果】:
         * - 各ステップが正常に完了する
         * - メール認証前はログイン不可
         * - メール認証後はログイン可能
         * 
         * 【ビジネス要件】:
         * [REQ-USER-001] ユーザー登録機能
         * [REQ-SEC-004] メールアドレス認証必須
         * [REQ-AUTH-001] 認証済みユーザーのみログイン可能
         */
        
        // === Step 1: ユーザー登録 ===
        UserCreateRequest registerRequest = UserCreateRequest.builder()
            .email("newuser@example.com")
            .password("ValidPass123!")
            .name("新規ユーザー")
            .build();
        
        String requestJson = objectMapper.writeValueAsString(registerRequest);
        
        MvcResult registerResult = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isCreated())  // HTTPステータス201
            .andReturn();
        
        // レスポンスからユーザーIDを取得
        String responseBody = registerResult.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseBody);
        Long userId = responseJson.get("id").asLong();
        
        // === Step 2: メール認証前のログイン試行（失敗することを期待） ===
        // この時点ではまだアカウントが有効化されていないため、ログインは拒否されるはず
        LoginRequest loginRequest = LoginRequest.builder()
            .email("newuser@example.com")
            .password("ValidPass123!")
            .build();
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andExpect(status().isForbidden())  // HTTPステータス403
            .andExpect(jsonPath("$.error").value("メールアドレスの確認が必要です"));
        
        // === Step 3: メールアドレス認証 ===
        // 実際のメールから取得するトークンをシミュレート
        // テスト環境では、データベースから直接トークンを取得
        String verificationToken = "test-verification-token-" + userId;
        
        mockMvc.perform(get("/api/users/verify")
                .param("token", verificationToken))
            .andExpect(status().isOk())  // HTTPステータス200
            .andExpect(jsonPath("$.message").value("メールアドレスが確認されました"));
        
        // === Step 4: メール認証後のログイン試行（成功することを期待） ===
        // 認証済みのため、今度はログインが成功するはず
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andExpect(status().isOk())  // HTTPステータス200
            .andExpect(jsonPath("$.access_token").exists())  // JWTトークンが返される
            .andReturn();
        
        // JWTトークンを取得
        String loginResponse = loginResult.getResponse().getContentAsString();
        JsonNode loginJson2 = objectMapper.readTree(loginResponse);
        String accessToken = loginJson2.get("access_token").asText();
        
        // === Step 5: 取得したトークンでプロフィールにアクセス ===
        // 発行されたトークンが有効であることを確認
        mockMvc.perform(get("/api/profile")
                .header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("newuser@example.com"))
            .andExpect(jsonPath("$.name").value("新規ユーザー"));
    }
}
```

---

## ✅ レビューチェックリスト

Javaテストコードレビュー時に確認:

- [ ] すべてのテストメソッドにJavadocコメントがある
- [ ] 4要素（対象・ケース・期待結果・要件）が記載されている
- [ ] Given-When-Thenセクションにコメントがある
- [ ] モックを使う理由が明記されている
- [ ] ParameterizedTestの各ケースに説明がある
- [ ] すべてのコメントが日本語で記述されている

---

## 🔗 関連ドキュメント

- [00-test-comment-standards.md](../00-test-comment-standards.md) - 共通テストコメント原則
- [java/inline-comment-examples.md](inline-comment-examples.md) - Javaインラインコメント例
- [java-testing-guide.md](java-testing-guide.md) - Javaテスト全体のガイド
