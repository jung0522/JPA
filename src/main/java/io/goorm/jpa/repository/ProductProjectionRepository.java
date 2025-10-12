package io.goorm.jpa.repository;

import io.goorm.jpa.dto.projection.ProductDto;
import io.goorm.jpa.dto.projection.ProductSummary;
import io.goorm.jpa.entity.Product;
import io.goorm.jpa.enums.ProductStatus;

import java.util.List;

/**
 * 상품 Projection Repository
 * 4가지 조회 방식 비교
 */
public interface ProductProjectionRepository {

    // ========== 1. Entity Projection (전체 컬럼 조회) ==========

    /**
     * Entity 조회 - 전체 컬럼 SELECT
     * SELECT * FROM tb_product WHERE status = ?
     */
    List<Product> findByStatus(ProductStatus status);

    // ========== 2. Interface Projection (필요한 컬럼만 조회) ==========

    /**
     * Interface Projection - 필요한 컬럼만 SELECT
     * SELECT product_id, product_name, price, status FROM tb_product WHERE status = ?
     */
    List<ProductSummary> findSummaryByStatus(ProductStatus status);

    // ========== 3. Record/DTO Projection (필요한 컬럼만 조회) ==========

    /**
     * Record Projection - 필요한 컬럼만 SELECT
     * SELECT product_id, product_name, price, status FROM tb_product WHERE status = ?
     */
    List<ProductDto> findDtoByStatus(ProductStatus status);

    // ========== 4. Dynamic Projection (동적 타입 지정) ==========

    /**
     * Dynamic Projection - 런타임에 타입 결정
     * 호출 시 Class<T>를 넘겨서 Entity/Interface/DTO 중 선택
     */
    <T> List<T> findDynamicByStatus(ProductStatus status, Class<T> type);
}
