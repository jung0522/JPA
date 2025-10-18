package io.goorm.jpa.exception;

import io.goorm.jpa.dto.common.ErrorResponse;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

/**
 * REST API 전역 예외 처리 (JSON 응답)
 * 적용 대상: io.goorm.jpa.controller.api 패키지
 */
@Slf4j
@RestControllerAdvice(basePackages = "io.goorm.jpa.controller.api")
@RequiredArgsConstructor
public class RestApiExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn("[BusinessException] code={}, detailMessage={}, args={}",
                e.getErrorCode().getCode(),
                e.getDetailMessage(),
                e.getArgs() != null ? Arrays.toString(e.getArgs()) : "none");

        String message = getMessage(e.getErrorCode(), e.getArgs());
        ErrorResponse response = ErrorResponse.of(e.getErrorCode().getCode(), message);

        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockException(OptimisticLockException e) {
        log.warn("[OptimisticLockException] entity={}, message={}",
                e.getEntity() != null ? e.getEntity().getClass().getSimpleName() : "unknown",
                e.getMessage(), e);

        return buildResponse(ErrorCode.OPTIMISTIC_LOCK_FAILED);
    }

    @ExceptionHandler(PessimisticLockException.class)
    public ResponseEntity<ErrorResponse> handlePessimisticLockException(PessimisticLockException e) {
        log.warn("[PessimisticLockException] message={}", e.getMessage(), e);

        return buildResponse(ErrorCode.PESSIMISTIC_LOCK_FAILED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Validation failed");

        log.warn("[ValidationException] field={}, errors=[{}]",
                e.getBindingResult().getObjectName(), errors);

        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ErrorResponse response = ErrorResponse.of(errorCode.getCode(), message);

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException e) {
        log.error("[DataAccessException] type={}, message={}", e.getClass().getSimpleName(), e.getMessage(), e);

        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("[UnexpectedException] type={}, message={}", e.getClass().getName(), e.getMessage(), e);

        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    // 공통 메서드: 다국어 메시지 조회
    private String getMessage(ErrorCode errorCode, Object[] args) {
        return messageSource.getMessage(
                errorCode.getMessageKey(),
                args,
                errorCode.getMessageKey(),
                LocaleContextHolder.getLocale()
        );
    }

    // 공통 메서드: ErrorResponse 생성
    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode errorCode) {
        String message = getMessage(errorCode, null);
        ErrorResponse response = ErrorResponse.of(errorCode.getCode(), message);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }
}
