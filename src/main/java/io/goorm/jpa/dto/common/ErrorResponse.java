package io.goorm.jpa.dto.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final String code;
    private final String message;
    private final LocalDateTime timestamp;

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, LocalDateTime.now());
    }
}
