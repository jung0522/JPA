package io.goorm.jpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

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
     *
     * 실무에서는:
     * - Spring Security의 SecurityContext에서 인증된 사용자 ID 가져오기
     * - JWT 토큰에서 사용자 ID 추출
     *
     * 학습 목적으로는:
     * - 고정값(1L) 하드코딩
     */
    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> Optional.of(1L);
    }
}
