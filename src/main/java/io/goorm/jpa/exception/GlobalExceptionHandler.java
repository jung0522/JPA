package io.goorm.jpa.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Entity 직접 반환 시 발생하는 LAZY 로딩 에러
     */
    @ExceptionHandler({
            org.hibernate.LazyInitializationException.class,
            com.fasterxml.jackson.databind.JsonMappingException.class,
            com.fasterxml.jackson.databind.exc.InvalidDefinitionException.class
    })
    public ResponseEntity<ErrorResponse> handleLazyInitializationException(Exception ex) {
        log.error("========================================");
        log.error("❌ Entity 직접 반환 에러 발생!");
        log.error("========================================");
        log.error("에러 타입: {}", ex.getClass().getSimpleName());
        log.error("에러 메시지: {}", ex.getMessage());
        log.error("========================================");
        log.error("원인: Entity를 직접 JSON으로 변환하려 했습니다.");
        log.error("LAZY 프록시 객체(ByteBuddyInterceptor)는 직렬화할 수 없습니다!");
        log.error("========================================");
        log.error("해결책: DTO로 변환해서 반환하세요!");
        log.error("예시: GET /api/posts/{{id}}/dto (O)");
        log.error("예시: GET /api/comments/{{id}}/entity (X)");
        log.error("========================================", ex);

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Entity 직접 반환 에러: " + ex.getClass().getSimpleName() + " - DTO로 변환하세요!",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex) {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "서버 오류가 발생했습니다.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * 에러 응답 DTO
     */
    public record ErrorResponse(
            int status,
            String message,
            LocalDateTime timestamp
    ) {
        public static ErrorResponse of(int status, String message, LocalDateTime timestamp) {
            return new ErrorResponse(status, message, timestamp);
        }
    }
}
