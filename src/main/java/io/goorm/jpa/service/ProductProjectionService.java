package io.goorm.jpa.service;

import io.goorm.jpa.dto.ProductResponse;
import io.goorm.jpa.dto.projection.ProductDto;
import io.goorm.jpa.dto.projection.ProductSummary;
import io.goorm.jpa.entity.Product;
import io.goorm.jpa.enums.ProductStatus;
import io.goorm.jpa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 상품 Projection Service
 * 3가지 조회 방식 비교 (발전 과정)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductProjectionService {

    private final ProductRepository productRepository;

    /**
     * 1. Entity Projection (초기, ~2010년대)
     * 전체 컬럼 조회 → Service에서 DTO 변환
     *
     * 문제점:
     * - 전체 컬럼 조회 (불필요한 데이터)
     * - 수동 변환 번거로움
     *
     * 사용 시기: CUD 작업이 필요한 경우만
     */
    public List<ProductResponse> findByEntityProjection(String status) {
        log.info("=== Entity Projection 시작 (전체 컬럼 조회) ===");
        ProductStatus productStatus = ProductStatus.valueOf(status);

        List<Product> products = productRepository.findByStatus(productStatus);
        log.info("조회된 Entity 개수: {} (전체 10개 컬럼)", products.size());

        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 2. Interface Projection (Spring Data JPA, ~2018년)
     * 필요한 컬럼만 조회 (Interface 기반)
     *
     * 문제점:
     * - 프록시 객체 생성 (성능 오버헤드)
     * - 불변성 불명확
     *
     * 현재 상황: Record가 나온 후 거의 사용 안 함 (레거시)
     */
    public List<ProductSummary> findByInterfaceProjection(String status) {
        log.info("=== Interface Projection 시작 (필요한 컬럼만, 프록시) ===");
        ProductStatus productStatus = ProductStatus.valueOf(status);

        List<ProductSummary> summaries = productRepository.findSummaryByStatus(productStatus);
        log.info("조회된 Interface Projection 개수: {} (4개 컬럼, 프록시 객체)", summaries.size());

        return summaries;
    }

    /**
     * 3. Record Projection (현재 추세!, Java 16+, 2021년~)
     * 필요한 컬럼만 조회 (Record 기반)
     *
     * 장점:
     * - 필요한 컬럼만 조회
     * - 불변 객체
     * - 프록시 없음 (성능 우수)
     * - 코드 간결
     * - equals/hashCode 자동
     *
     * 현재 실무 표준!
     */
    public List<ProductDto> findByRecordProjection(String status) {
        log.info("=== Record Projection 시작 (필요한 컬럼만, 불변 객체) ===");
        ProductStatus productStatus = ProductStatus.valueOf(status);

        List<ProductDto> dtos = productRepository.findDtoByStatus(productStatus);
        log.info("조회된 Record Projection 개수: {} (4개 컬럼, 불변 객체)", dtos.size());

        return dtos;
    }
}
