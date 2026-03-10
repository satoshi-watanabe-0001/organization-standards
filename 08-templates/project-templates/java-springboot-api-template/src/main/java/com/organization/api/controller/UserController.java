package com.organization.api.controller;

import com.organization.api.dto.UserDto;
import com.organization.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * ユーザーコントローラー.
 * 
 * <p>ユーザー管理のREST APIエンドポイントを提供します。
 * Organization Standards準拠:
 * <ul>
 *   <li>薄いController（ビジネスロジックはServiceに委譲）</li>
 *   <li>Bean Validationによる入力検証</li>
 *   <li>適切なHTTPステータスコード</li>
 *   <li>RESTful URL設計</li>
 * </ul>
 * 
 * @author Organization Development Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 全ユーザー取得.
     * 
     * <p>GET /api/v1/users
     * 
     * @return ユーザーリスト
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")  // 管理者のみアクセス可
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("全ユーザー取得リクエスト");
        List<UserDto> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * ユーザー詳細取得.
     * 
     * <p>GET /api/v1/users/{id}
     * 
     * @param id ユーザーID
     * @return ユーザー情報
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        log.info("ユーザー取得リクエスト: id={}", id);
        UserDto user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * ユーザー作成.
     * 
     * <p>POST /api/v1/users
     * 
     * @param request ユーザー作成リクエスト
     * @return 作成されたユーザー情報
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")  // 管理者のみアクセス可
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        
        log.info("ユーザー作成リクエスト: email={}", request.getEmail());
        
        UserDto userDto = UserDto.builder()
                .email(request.getEmail())
                .name(request.getName())
                .role(request.getRole())
                .build();
        
        UserDto createdUser = userService.createUser(userDto, request.getPassword());
        
        // 作成されたリソースのURIを返却
        URI location = URI.create("/api/v1/users/" + createdUser.getId());
        
        return ResponseEntity.created(location).body(createdUser);
    }

    /**
     * ユーザー更新.
     * 
     * <p>PUT /api/v1/users/{id}
     * 
     * @param id ユーザーID
     * @param userDto 更新情報
     * @return 更新されたユーザー情報
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDto userDto) {
        
        log.info("ユーザー更新リクエスト: id={}", id);
        UserDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * ユーザー削除.
     * 
     * <p>DELETE /api/v1/users/{id}
     * 
     * @param id ユーザーID
     * @return 削除成功レスポンス
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")  // 管理者のみアクセス可
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("ユーザー削除リクエスト: id={}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ユーザー作成リクエストDTO.
     */
    @lombok.Data
    private static class CreateUserRequest {
        @jakarta.validation.constraints.NotBlank(message = "メールアドレスは必須です")
        @jakarta.validation.constraints.Email(message = "有効なメールアドレス形式で入力してください")
        private String email;

        @jakarta.validation.constraints.NotBlank(message = "ユーザー名は必須です")
        @jakarta.validation.constraints.Size(min = 2, max = 100, 
            message = "ユーザー名は2文字以上100文字以下で入力してください")
        private String name;

        @jakarta.validation.constraints.NotBlank(message = "パスワードは必須です")
        @jakarta.validation.constraints.Size(min = 8, max = 100, 
            message = "パスワードは8文字以上100文字以下で入力してください")
        private String password;

        private com.organization.api.entity.User.Role role;
    }
}
