package com.organization.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * グローバル例外ハンドラー.
 * 
 * <p>アプリケーション全体の例外を統一的に処理し、
 * クライアントに適切なエラーレスポンスを返します。
 * 
 * <p>Organization Standards準拠:
 * <ul>
 *   <li>統一されたエラーレスポンス形式</li>
 *   <li>適切なHTTPステータスコード</li>
 *   <li>詳細なエラーログ記録</li>
 *   <li>機密情報の非公開</li>
 * </ul>
 * 
 * @author Organization Development Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * バリデーションエラーのハンドリング.
     * 
     * <p>Bean Validationで検証に失敗した場合に発生します。
     * フィールドごとのエラーメッセージを返却します。
     * 
     * @param ex 例外オブジェクト
     * @param request リクエスト情報
     * @return エラーレスポンス
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        // フィールドエラーを抽出
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("バリデーションエラー: {}", errors);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("入力値の検証に失敗しました")
                .path(request.getDescription(false).replace("uri=", ""))
                .validationErrors(errors)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 認証エラーのハンドリング.
     * 
     * <p>ログイン認証に失敗した場合に発生します。
     * 
     * @param ex 例外オブジェクト
     * @param request リクエスト情報
     * @return エラーレスポンス
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex,
            WebRequest request) {
        
        log.warn("認証失敗: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Authentication Failed")
                .message("ユーザー名またはパスワードが正しくありません")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * 認可エラーのハンドリング.
     * 
     * <p>権限不足でアクセスが拒否された場合に発生します。
     * 
     * @param ex 例外オブジェクト
     * @param request リクエスト情報
     * @return エラーレスポンス
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request) {
        
        log.warn("アクセス拒否: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Access Denied")
                .message("このリソースへのアクセス権限がありません")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * リソース未検出エラーのハンドリング.
     * 
     * <p>カスタム例外として定義し、このハンドラーで処理します。
     * 
     * @param ex 例外オブジェクト
     * @param request リクエスト情報
     * @return エラーレスポンス
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {
        
        log.warn("リソース未検出: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Resource Not Found")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * その他の予期しないエラーのハンドリング.
     * 
     * <p>上記以外のすべての例外をキャッチし、
     * 内部サーバーエラーとして処理します。
     * 詳細なエラー情報はログに記録し、クライアントには最小限の情報のみ返却します。
     * 
     * @param ex 例外オブジェクト
     * @param request リクエスト情報
     * @return エラーレスポンス
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {
        
        // 詳細なエラー情報をログに記録（スタックトレース含む）
        log.error("予期しないエラーが発生しました", ex);

        // クライアントには一般的なエラーメッセージのみ返却（セキュリティのため）
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("サーバー内部でエラーが発生しました")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * エラーレスポンスDTO.
     * 
     * <p>統一されたエラーレスポンス形式を提供します。
     */
    @lombok.Builder
    @lombok.Getter
    private static class ErrorResponse {
        private Instant timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private Map<String, String> validationErrors;
    }
}
