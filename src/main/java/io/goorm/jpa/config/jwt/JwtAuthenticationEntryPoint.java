package io.goorm.jpa.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.goorm.jpa.dto.common.ErrorResponse;
import io.goorm.jpa.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT 인증 실패 시 401 Unauthorized JSON 응답 반환
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {

        String message = messageSource.getMessage(
                ErrorCode.AUTH_UNAUTHORIZED.getMessageKey(),
                null,
                LocaleContextHolder.getLocale()
        );

        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.AUTH_UNAUTHORIZED.getCode(),
                message
        );

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
