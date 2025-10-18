package io.goorm.jpa.exception;

import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

/**
 * MVC 전역 예외 처리 (View 반환)
 * 적용 대상: io.goorm.jpa.controller.web 패키지
 */
@Slf4j
@ControllerAdvice(basePackages = "io.goorm.jpa.controller.web")
@RequiredArgsConstructor
public class MvcExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException e, Model model) {
        log.warn("[BusinessException] code={}, detailMessage={}, args={}",
                e.getErrorCode().getCode(),
                e.getDetailMessage(),
                e.getArgs() != null ? Arrays.toString(e.getArgs()) : "none");

        String message = getMessage(e.getErrorCode(), e.getArgs());
        addErrorToModel(model, e.getErrorCode().getCode(), message, e.getErrorCode().getHttpStatus().value());

        return "error/error";
    }

    @ExceptionHandler(OptimisticLockException.class)
    public String handleOptimisticLockException(OptimisticLockException e, Model model) {
        log.warn("[OptimisticLockException] entity={}, message={}",
                e.getEntity() != null ? e.getEntity().getClass().getSimpleName() : "unknown",
                e.getMessage(), e);

        return buildErrorView(model, ErrorCode.OPTIMISTIC_LOCK_FAILED);
    }

    @ExceptionHandler(PessimisticLockException.class)
    public String handlePessimisticLockException(PessimisticLockException e, Model model) {
        log.warn("[PessimisticLockException] message={}", e.getMessage(), e);

        return buildErrorView(model, ErrorCode.PESSIMISTIC_LOCK_FAILED);
    }

    @ExceptionHandler(DataAccessException.class)
    public String handleDataAccessException(DataAccessException e, Model model) {
        log.error("[DataAccessException] type={}, message={}", e.getClass().getSimpleName(), e.getMessage(), e);

        return buildErrorView(model, ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        log.error("[UnexpectedException] type={}, message={}", e.getClass().getName(), e.getMessage(), e);

        return buildErrorView(model, ErrorCode.INTERNAL_SERVER_ERROR);
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

    // 공통 메서드: Model에 에러 정보 추가
    private void addErrorToModel(Model model, String errorCode, String message, int statusCode) {
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("errorMessage", message);
        model.addAttribute("statusCode", statusCode);
    }

    // 공통 메서드: 에러 뷰 생성
    private String buildErrorView(Model model, ErrorCode errorCode) {
        String message = getMessage(errorCode, null);
        addErrorToModel(model, errorCode.getCode(), message, errorCode.getHttpStatus().value());
        return "error/error";
    }
}
