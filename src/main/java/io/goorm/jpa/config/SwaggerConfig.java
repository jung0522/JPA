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
                                ## 학사관리 시스템 API

                                ### 핵심 기능
                                - **인증/인가**: 세션 기반 인증, 권한별 접근 제어
                                - **게시판**: CRUD, 검색, 페이징 (Query Methods)
                                - **강의관리**: CRUD, 검색, 통계 (JPQL + QueryDSL)
                                - **수강신청**: 신청/승인/거절, 일괄처리 (QueryDSL + 락킹)
                                - **데이터 관리**: JPA Auditing, Soft Delete, 예외 처리

                                ### 신규 기능
                                - **양방향 관계**: Course ↔ Curriculum, Course ↔ Enrollment
                                - **편의 메서드**: 엔티티 내 비즈니스 로직 캡슐화
                                - **비관적 락**: 동시성 제어 및 배치 처리
                                - **통계 쿼리**: 강의별, 강사별, 월별 통계
                                - **고급 검색**: 복합 조건 검색 및 동적 쿼리

                                ### 테스트 계정
                                - **admin** / 1234 (관리자)
                                - **instructor01, instructor02** / 1234 (강사)
                                - **student01, student02** / 1234 (학생)

                                ### 사용 방법
                                1. `/api/auth/login` 으로 로그인
                                2. 브라우저에서 자동으로 쿠키 설정
                                3. API 테스트 및 기능 검증
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
