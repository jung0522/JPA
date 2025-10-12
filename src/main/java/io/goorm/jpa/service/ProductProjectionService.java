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
 * 4가지 조회 방식 비교
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductProjectionService {

    private final ProductRepository productRepository;

    /**
     * 1. Entity Projection
     * 전체 컬럼 조회 → Service에서 DTO 변환
     */
    public List<ProductResponse> findByEntityProjection(String status) {
        log.info("=== Entity Projection 시작 ===");
        ProductStatus productStatus = ProductStatus.valueOf(status);

        List<Product> products = productRepository.findByStatus(productStatus);
        log.info("조회된 Entity 개수: {}", products.size());

        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 2. Interface Projection
     * 필요한 컬럼만 조회 (Interface 기반)
     */
    public List<ProductSummary> findByInterfaceProjection(String status) {
        log.info("=== Interface Projection 시작 ===");
        ProductStatus productStatus = ProductStatus.valueOf(status);

        List<ProductSummary> summaries = productRepository.findSummaryByStatus(productStatus);
        log.info("조회된 Interface Projection 개수: {}", summaries.size());

        return summaries;
    }

    /**
     * 3. Record/DTO Projection
     * 필요한 컬럼만 조회 (Record 기반)
     */
    public List<ProductDto> findByDtoProjection(String status) {
        log.info("=== Record/DTO Projection 시작 ===");
        ProductStatus productStatus = ProductStatus.valueOf(status);

        List<ProductDto> dtos = productRepository.findDtoByStatus(productStatus);
        log.info("조회된 DTO Projection 개수: {}", dtos.size());

        return dtos;
    }

    /**
     * 4. Dynamic Projection
     * 런타임에 타입 결정
     */
    public <T> List<T> findByDynamicProjection(String status, Class<T> type) {
        log.info("=== Dynamic Projection 시작 (타입: {}) ===", type.getSimpleName());
        ProductStatus productStatus = ProductStatus.valueOf(status);

        List<T> results = productRepository.findDynamicByStatus(productStatus, type);
        log.info("조회된 Dynamic Projection 개수: {}", results.size());

        return results;
    }
}
