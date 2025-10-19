package io.goorm.jpa.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("학사관리 시스템 API")
                        .description("""
                                ## 학사관리 시스템 - 1단계

                                ### 현재 구현된 기능
                                - ✅ JWT 인증/인가
                                - ✅ JPA Auditing (생성/수정 시간, 사용자 추적)
                                - ✅ Soft Delete
                                - ✅ 전역 예외 처리
                                - ✅ API 표준 응답 포맷
                                - ✅ SQL 로깅

                                ### 사용 가능한 모듈
                                - **로그인 대시보드** - 메인 페이지
                                - **게시판** - Query Methods
                                - **강의관리** - JPQL + DTO
                                - **수강신청관리** - QueryDSL + DTO
                                - **내 수강신청** - 학생 전용

                                ### 테스트 계정
                                - **admin** / 1234 (관리자 - 모든 기능 접근)
                                - **instructor01, instructor02** / 1234 (강사 - 강의관리, 수강신청관리)
                                - **student01, student02** / 1234 (학생 - 강의 조회, 수강신청)

                                ### 사용 방법
                                1. `/api/auth/login` 으로 로그인 (세션 기반)
                                2. 브라우저에서 자동으로 쿠키 설정됨
                                3. 각 모듈별 API 테스트

                                ### 다음 단계 (2단계)
                                - **통계 기능** - 월별 강의 개설, 강사별 강의 통계
                                - **양방향 관계** - Course ↔ Curriculum, User ↔ Enrollment
                                - **편의 메서드** - Cascade, Orphan Removal
                                """)
                        .version("0.0.1-SNAPSHOT")
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 서버")
                ))
                .components(new Components());
    }
}
