package com.organization.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザー作成リクエストDTO.
 * 
 * <p>このクラスは、ユーザー作成APIのリクエストボディを表現します。
 * Bean Validationアノテーションにより、入力検証を行います。</p>
 * 
 * <p><strong>WHY原則:</strong> organization-standardsの
 * 04-error-handling-validation.md に準拠し、Bean Validationを活用します。
 * 入力検証をコントローラー層で宣言的に実施することで、
 * ビジネスロジックとの責務分離を明確にします。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    /**
     * ユーザー名.
     */
    @NotBlank(message = "ユーザー名は必須項目です")
    @Size(min = 2, max = 100, message = "ユーザー名は2〜100文字で入力してください")
    private String name;

    /**
     * メールアドレス.
     */
    @NotBlank(message = "メールアドレスは必須項目です")
    @Email(message = "メールアドレスの形式が不正です")
    @Size(max = 255, message = "メールアドレスは255文字以内で入力してください")
    private String email;

    /**
     * パスワード.
     */
    @NotBlank(message = "パスワードは必須項目です")
    @Size(min = 8, max = 100, message = "パスワードは8〜100文字で入力してください")
    private String password;

}
