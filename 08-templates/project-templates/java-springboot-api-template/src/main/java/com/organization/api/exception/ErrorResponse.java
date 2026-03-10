package com.organization.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 統一エラーレスポンス形式.
 * 
 * <p>このクラスは、すべてのエラーレスポンスで共通のJSON形式を提供します。</p>
 * 
 * <p><strong>レスポンス例:</strong></p>
 * <pre>{@code
 * {
 *   "timestamp": "2025-11-20T08:00:00Z",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "入力値が不正です",
 *   "path": "/api/users",
 *   "details": {
 *     "email": "メールアドレスの形式が不正です",
 *     "name": "名前は必須項目です"
 *   }
 * }
 * }</pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * エラー発生日時.
     */
    private Instant timestamp;

    /**
     * HTTPステータスコード.
     */
    private int status;

    /**
     * HTTPエラー名.
     */
    private String error;

    /**
     * エラーメッセージ.
     */
    private String message;

    /**
     * リクエストパス.
     */
    private String path;

    /**
     * 詳細エラー情報 (フィールド別バリデーションエラー等).
     */
    private Map<String, String> details;

}
