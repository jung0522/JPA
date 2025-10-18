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
                        .title("JPA 학습 프로젝트 API")
                        .description("""
                                ## Milestone 0: Backbone (기본 골격)

                                ### 현재 구현된 기능
                                - ✅ JWT 인증/인가
                                - ✅ JPA Auditing (생성/수정 시간, 사용자 추적)
                                - ✅ Soft Delete
                                - ✅ 전역 예외 처리
                                - ✅ API 표준 응답 포맷
                                - ✅ P6Spy SQL 로깅

                                ### 테스트 계정
                                - **admin** / 1234 (ADMIN)
                                - **student01** / 1234 (USER)
                                - **student02** / 1234 (USER)

                                ### 사용 방법
                                1. `/api/auth/login` 으로 로그인
                                2. 받은 토큰을 우측 상단 **Authorize** 버튼에 입력: `Bearer {token}`
                                3. 인증이 필요한 API 테스트

                                ### 다음 단계
                                - **Milestone 1**: Board (Query Methods)
                                - **Milestone 2**: Course (JPQL + Pessimistic Lock)
                                - **Milestone 3**: Enrollment (QueryDSL + Optimistic Lock)
                                - **Milestone 4**: Course-CourseWeek-Lecture (양방향 + Cascade + 편의 메서드)
                                """)
                        .version("0.0.1-SNAPSHOT")
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 서버")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT 토큰을 입력하세요 (Bearer 제외)")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
