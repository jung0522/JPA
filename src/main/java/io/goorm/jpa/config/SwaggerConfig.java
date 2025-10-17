package io.goorm.jpa.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
                        .title("JPA ManyToOne 관계 학습 API")
                        .description("""
                                ## JPA N+1 문제와 해결 방법 학습용 API

                                ### 학습 목표
                                1. N+1 문제 발생 원인 이해
                                2. JOIN vs FETCH JOIN 차이 이해
                                3. @EntityGraph, @BatchSize 활용
                                4. Entity vs DTO 반환 차이
                                5. Cascade & orphanRemoval 이해

                                ### API 카테고리
                                - **N+1 문제 비교**: 5가지 방법 비교 (N+1, JOIN, FETCH JOIN, EntityGraph, BatchSize)
                                - **반환 방식**: Entity vs DTO
                                - **페이징**: Spring Data JPA Pageable
                                - **Cascade 테스트**: PERSIST, REMOVE, orphanRemoval
                                """)
                        .version("1.0.0")
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 서버")
                ));
    }
}
