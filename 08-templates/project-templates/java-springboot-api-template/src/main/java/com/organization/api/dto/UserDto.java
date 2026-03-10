package com.organization.api.dto;

import com.organization.api.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * ユーザーDTO.
 * 
 * <p>ユーザー情報のAPI入出力に使用するDTOクラスです。
 * Organization Standards準拠: EntityをAPIレスポンスとして直接返さない。
 * 
 * @author Organization Development Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    /**
     * ユーザーID.
     */
    private Long id;

    /**
     * メールアドレス（ログインID）.
     */
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレス形式で入力してください")
    private String email;

    /**
     * ユーザー名.
     */
    @NotBlank(message = "ユーザー名は必須です")
    @Size(min = 2, max = 100, message = "ユーザー名は2文字以上100文字以下で入力してください")
    private String name;

    /**
     * ロール.
     */
    private User.Role role;

    /**
     * アカウント有効状態.
     */
    private Boolean enabled;

    /**
     * 作成日時.
     */
    private Instant createdAt;

    /**
     * 更新日時.
     */
    private Instant updatedAt;
}
