package io.goorm.jpa.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common (COMMON)
    INTERNAL_SERVER_ERROR("COMMON001", "error.common.internal", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT("COMMON002", "error.common.invalid.input", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("COMMON003", "error.common.not.found", HttpStatus.NOT_FOUND),

    // User (USER)
    USER_NOT_FOUND("USER001", "error.user.not.found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER002", "error.user.already.exists", HttpStatus.CONFLICT),
    USER_FORBIDDEN("USER003", "error.user.forbidden", HttpStatus.FORBIDDEN),
    USER_INVALID_PASSWORD("USER004", "error.user.invalid.password", HttpStatus.BAD_REQUEST),

    // Board (BOARD)
    BOARD_NOT_FOUND("BOARD001", "error.board.not.found", HttpStatus.NOT_FOUND),
    BOARD_FORBIDDEN("BOARD002", "error.board.forbidden", HttpStatus.FORBIDDEN),

    // Course (COURSE)
    COURSE_NOT_FOUND("COURSE001", "error.course.not.found", HttpStatus.NOT_FOUND),
    COURSE_FORBIDDEN("COURSE002", "error.course.forbidden", HttpStatus.FORBIDDEN),
    COURSE_FULL("COURSE003", "error.course.full", HttpStatus.CONFLICT),

    // Enrollment (ENROLLMENT)
    ENROLLMENT_NOT_FOUND("ENROLLMENT001", "error.enrollment.not.found", HttpStatus.NOT_FOUND),
    ENROLLMENT_ALREADY_EXISTS("ENROLLMENT002", "error.enrollment.already.exists", HttpStatus.CONFLICT),
    ENROLLMENT_CANNOT_CANCEL("ENROLLMENT003", "error.enrollment.cannot.cancel", HttpStatus.BAD_REQUEST),
    ENROLLMENT_RETRY_EXCEEDED("ENROLLMENT004", "error.enrollment.retry.exceeded", HttpStatus.CONFLICT),
    ENROLLMENT_ALREADY_PROCESSED("ENROLLMENT005", "error.enrollment.already.processed", HttpStatus.BAD_REQUEST),
    ENROLLMENT_BATCH_NO_ITEMS("ENROLLMENT006", "error.enrollment.batch.no.items", HttpStatus.BAD_REQUEST),

    // Concurrency (CONCURRENCY)
    OPTIMISTIC_LOCK_FAILED("CONCURRENCY001", "error.concurrency.optimistic", HttpStatus.CONFLICT),
    PESSIMISTIC_LOCK_FAILED("CONCURRENCY002", "error.concurrency.pessimistic", HttpStatus.CONFLICT),

    // Authentication (AUTH)
    AUTH_INVALID_TOKEN("AUTH001", "error.auth.invalid.token", HttpStatus.UNAUTHORIZED),
    AUTH_EXPIRED_TOKEN("AUTH002", "error.auth.expired.token", HttpStatus.UNAUTHORIZED),
    AUTH_UNAUTHORIZED("AUTH003", "error.auth.unauthorized", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String messageKey;
    private final HttpStatus httpStatus;
}
