package io.goorm.jpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * JPA Auditing 설정
 * - @CreatedDate, @LastModifiedDate: 자동으로 생성/수정 시간 입력
 * - @CreatedBy, @LastModifiedBy: 자동으로 생성자/수정자 입력
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {

    /**
     * 현재 작업자 정보를 제공하는 Bean
     * Spring Security의 SecurityContext에서 인증된 사용자 ID를 가져옴
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
                return Optional.empty();
            }

            // Spring Security의 UserDetails.getUsername()은 우리 User의 ID를 반환
            return Optional.of(authentication.getName());
        };
    }
}
