package com.organization.api.exception;

/**
 * リソース未検出例外.
 * 
 * <p>指定されたIDやキーでリソースが見つからない場合にスローされます。
 * Organization Standards準拠のカスタム例外です。
 * 
 * <p>使用例:
 * <pre>{@code
 * User user = userRepository.findById(id)
 *     .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
 * }</pre>
 * 
 * @author Organization Development Team
 * @version 1.0.0
 * @since 2024-01-01
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * リソース名、フィールド名、フィールド値を指定した例外を作成.
     * 
     * @param resourceName リソース名（例: "User"）
     * @param fieldName フィールド名（例: "id"）
     * @param fieldValue フィールド値（例: 123）
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
    }

    /**
     * カスタムメッセージを指定した例外を作成.
     * 
     * @param message エラーメッセージ
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
