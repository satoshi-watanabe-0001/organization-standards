/**
 * 統合テストテンプレート (Java)
 * Integration Test Template for Java/Spring Boot Projects
 * 
 * 使用方法:
 * 1. このファイルをコピーして src/test/java/com/example/integration/ 配下に配置
 * 2. ResourceApiIntegrationTest.java のようにリネーム
 * 3. テスト対象に応じて実装を追加
 */

package com.example.integration;

import com.example.dto.ResourceRequest;
import com.example.dto.ResourceResponse;
import com.example.entity.Resource;
import com.example.repository.ResourceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * リソースAPI統合テスト
 * 
 * TestContainersを使用してPostgreSQLコンテナを自動起動し、
 * APIエンドポイントからデータベースまでの統合動作を検証します。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ResourceApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceRepository resourceRepository;

    // PostgreSQLコンテナの起動
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    // Spring Bootアプリケーションのデータソース設定を動的に変更
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterEach
    void tearDown() {
        // 各テスト後にデータをクリア
        resourceRepository.deleteAll();
    }

    // ==========================================
    // API Endpoint Tests
    // ==========================================

    /**
     * リソース作成APIの正常系テスト
     * 
     * テストパターン: POST /api/resources
     * 期待結果: 201 Created
     */
    @Test
    @Order(1)
    @DisplayName("リソース作成の正常系テスト")
    void testCreateResource_Success() throws Exception {
        // Arrange
        ResourceRequest request = ResourceRequest.builder()
                .name("Test Resource")
                .description("Test Description")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Resource"))
                .andExpect(jsonPath("$.description").value("Test Description"));

        // データベース検証
        assertThat(resourceRepository.findAll()).hasSize(1);
        Resource savedResource = resourceRepository.findAll().get(0);
        assertThat(savedResource.getName()).isEqualTo("Test Resource");
    }

    /**
     * リソース作成のバリデーションエラーテスト
     * 
     * テストパターン: POST /api/resources (不正なデータ)
     * 期待結果: 400 Bad Request
     */
    @Test
    @Order(2)
    @DisplayName("リソース作成のバリデーションエラーテスト")
    void testCreateResource_ValidationError() throws Exception {
        // Arrange: 空の名前（バリデーションエラー）
        ResourceRequest request = ResourceRequest.builder()
                .name("")  // 空文字
                .description("Description")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());

        // データベース検証：データが作成されていないこと
        assertThat(resourceRepository.findAll()).isEmpty();
    }

    /**
     * リソース取得APIのテスト
     * 
     * テストパターン: GET /api/resources/{id}
     * 期待結果: 200 OK
     */
    @Test
    @Order(3)
    @DisplayName("リソース取得の正常系テスト")
    void testGetResource_Success() throws Exception {
        // Arrange: テストデータ作成
        Resource resource = Resource.builder()
                .name("Get Test")
                .description("Get Description")
                .build();
        Resource savedResource = resourceRepository.save(resource);

        // Act & Assert
        mockMvc.perform(get("/api/resources/{id}", savedResource.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedResource.getId()))
                .andExpect(jsonPath("$.name").value("Get Test"))
                .andExpect(jsonPath("$.description").value("Get Description"));
    }

    /**
     * 存在しないリソースの取得エラーテスト
     * 
     * テストパターン: GET /api/resources/{id} (存在しないID)
     * 期待結果: 404 Not Found
     */
    @Test
    @Order(4)
    @DisplayName("存在しないリソースの取得エラーテスト")
    void testGetResource_NotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/resources/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * リソース更新APIのテスト
     * 
     * テストパターン: PUT /api/resources/{id}
     * 期待結果: 200 OK
     */
    @Test
    @Order(5)
    @DisplayName("リソース更新の正常系テスト")
    void testUpdateResource_Success() throws Exception {
        // Arrange: テストデータ作成
        Resource resource = Resource.builder()
                .name("Original Name")
                .description("Original Description")
                .build();
        Resource savedResource = resourceRepository.save(resource);

        ResourceRequest updateRequest = ResourceRequest.builder()
                .name("Updated Name")
                .description("Updated Description")
                .build();

        // Act & Assert
        mockMvc.perform(put("/api/resources/{id}", savedResource.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedResource.getId()))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated Description"));

        // データベース検証
        Resource updatedResource = resourceRepository.findById(savedResource.getId()).orElseThrow();
        assertThat(updatedResource.getName()).isEqualTo("Updated Name");
        assertThat(updatedResource.getDescription()).isEqualTo("Updated Description");
    }

    /**
     * リソース削除APIのテスト
     * 
     * テストパターン: DELETE /api/resources/{id}
     * 期待結果: 204 No Content
     */
    @Test
    @Order(6)
    @DisplayName("リソース削除の正常系テスト")
    void testDeleteResource_Success() throws Exception {
        // Arrange: テストデータ作成
        Resource resource = Resource.builder()
                .name("To Delete")
                .description("Will be deleted")
                .build();
        Resource savedResource = resourceRepository.save(resource);

        // Act & Assert
        mockMvc.perform(delete("/api/resources/{id}", savedResource.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // データベース検証：削除されていることを確認
        assertThat(resourceRepository.findById(savedResource.getId())).isEmpty();
    }

    /**
     * リソース一覧取得APIのテスト
     * 
     * テストパターン: GET /api/resources
     * 期待結果: 200 OK
     */
    @Test
    @Order(7)
    @DisplayName("リソース一覧取得の正常系テスト")
    void testGetAllResources_Success() throws Exception {
        // Arrange: テストデータ作成
        Resource resource1 = Resource.builder().name("Resource 1").description("Description 1").build();
        Resource resource2 = Resource.builder().name("Resource 2").description("Description 2").build();
        Resource resource3 = Resource.builder().name("Resource 3").description("Description 3").build();
        resourceRepository.saveAll(java.util.List.of(resource1, resource2, resource3));

        // Act & Assert
        mockMvc.perform(get("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("Resource 1"))
                .andExpect(jsonPath("$[1].name").value("Resource 2"))
                .andExpect(jsonPath("$[2].name").value("Resource 3"));
    }

    // ==========================================
    // Database Operations Tests
    // ==========================================

    /**
     * CRUD操作の統合テスト
     */
    @Test
    @Order(8)
    @DisplayName("CRUD操作の統合テスト")
    void testCrudOperations() {
        // Create
        Resource resource = Resource.builder()
                .name("CRUD Test")
                .description("Test Description")
                .build();
        Resource savedResource = resourceRepository.save(resource);
        assertThat(savedResource.getId()).isNotNull();

        // Read
        Resource retrievedResource = resourceRepository.findById(savedResource.getId()).orElseThrow();
        assertThat(retrievedResource.getName()).isEqualTo("CRUD Test");

        // Update
        retrievedResource.setName("Updated Name");
        Resource updatedResource = resourceRepository.save(retrievedResource);
        assertThat(updatedResource.getName()).isEqualTo("Updated Name");

        // Delete
        resourceRepository.deleteById(updatedResource.getId());
        assertThat(resourceRepository.findById(updatedResource.getId())).isEmpty();
    }

    /**
     * 複雑なクエリのテスト（カスタムクエリ）
     */
    @Test
    @Order(9)
    @DisplayName("複雑なクエリのテスト")
    void testComplexQuery() {
        // Arrange: テストデータ作成
        Resource resource1 = Resource.builder().name("Alpha").description("First").build();
        Resource resource2 = Resource.builder().name("Beta").description("Second").build();
        Resource resource3 = Resource.builder().name("Gamma").description("Third").build();
        resourceRepository.saveAll(java.util.List.of(resource1, resource2, resource3));

        // Act: 名前でソートして取得（カスタムクエリの例）
        // List<Resource> sortedResources = resourceRepository.findAllByOrderByNameAsc();

        // Assert
        // assertThat(sortedResources).hasSize(3);
        // assertThat(sortedResources.get(0).getName()).isEqualTo("Alpha");
        // assertThat(sortedResources.get(1).getName()).isEqualTo("Beta");
        // assertThat(sortedResources.get(2).getName()).isEqualTo("Gamma");
    }

    // ==========================================
    // Transaction Tests
    // ==========================================

    /**
     * トランザクションコミットのテスト
     * 
     * 複数テーブルへの書き込みが全て成功することを確認
     */
    @Test
    @Order(10)
    @DisplayName("トランザクションコミットのテスト")
    void testTransactionCommit() throws Exception {
        // Arrange
        // 複数テーブルへの書き込みを含むリクエスト
        // String requestJson = """
        //     {
        //         "resource": {
        //             "name": "Main Resource",
        //             "description": "Description"
        //         },
        //         "relatedData": {
        //             "info": "Related Information"
        //         }
        //     }
        //     """;

        // Act & Assert
        // mockMvc.perform(post("/api/resources/with-related")
        //                 .contentType(MediaType.APPLICATION_JSON)
        //                 .content(requestJson))
        //         .andExpect(status().isCreated());

        // 両方のテーブルにデータが存在することを確認
        // Resource resource = resourceRepository.findByName("Main Resource").orElseThrow();
        // assertThat(resource.getRelatedData()).isNotNull();
        // assertThat(resource.getRelatedData().getInfo()).isEqualTo("Related Information");
    }

    /**
     * トランザクションロールバックのテスト
     * 
     * エラー発生時に全ての変更がロールバックされることを確認
     */
    @Test
    @Order(11)
    @DisplayName("トランザクションロールバックのテスト")
    void testTransactionRollback() throws Exception {
        // Arrange: エラーを発生させるリクエスト
        // String requestJson = """
        //     {
        //         "resource": {
        //             "name": "Test Resource",
        //             "description": "Description"
        //         },
        //         "relatedData": {
        //             "info": null
        //         }
        //     }
        //     """;

        // Act & Assert
        // mockMvc.perform(post("/api/resources/with-related")
        //                 .contentType(MediaType.APPLICATION_JSON)
        //                 .content(requestJson))
        //         .andExpect(status().isBadRequest());

        // リソースも作成されていないことを確認（ロールバック成功）
        // assertThat(resourceRepository.findByName("Test Resource")).isEmpty();
    }

    // ==========================================
    // Authentication & Authorization Tests
    // ==========================================

    /**
     * 認証なしでの保護されたエンドポイントへのアクセステスト
     */
    @Test
    @Order(12)
    @Disabled("認証機能が実装された後に有効化")
    @DisplayName("認証なしでのアクセステスト")
    void testProtectedEndpoint_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/protected/resources")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 認証ありでの保護されたエンドポイントへのアクセステスト
     */
    @Test
    @Order(13)
    @Disabled("認証機能が実装された後に有効化")
    @DisplayName("認証ありでのアクセステスト")
    void testProtectedEndpoint_WithAuth() throws Exception {
        // Arrange
        String token = "valid_jwt_token";

        // Act & Assert
        mockMvc.perform(get("/api/protected/resources")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ==========================================
    // External Service Integration Tests
    // ==========================================

    /**
     * 外部サービス連携テスト（モック使用）
     */
    @Test
    @Order(14)
    @Disabled("外部サービス連携が必要な場合に実装")
    @DisplayName("外部サービス連携テスト")
    void testExternalServiceIntegration() throws Exception {
        // モックの設定とテスト実装
        // @MockBean を使用して外部サービスをモック化
    }
}
