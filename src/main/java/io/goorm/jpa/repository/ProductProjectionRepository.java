package io.goorm.jpa.repository;

import io.goorm.jpa.dto.projection.ProductDto;
import io.goorm.jpa.dto.projection.ProductSummary;
import io.goorm.jpa.entity.Product;
import io.goorm.jpa.enums.ProductStatus;

import java.util.List;

/**
 * 상품 Projection Repository
 * 3가지 조회 방식 비교 (발전 과정)
 */
public interface ProductProjectionRepository {

    // ========== 1. Entity Projection (초기, ~2010년대) ==========

    /**
     * Entity 조회 - 전체 컬럼 SELECT
     * SELECT * FROM tb_product WHERE status = ?
     *
     * 특징:
     * - 전체 컬럼 조회 (불필요한 데이터 포함)
     * - 영속성 컨텍스트 관리
     * - CUD 작업 가능
     *
     * 사용 시기: CUD 작업이 필요한 경우만
     */
    List<Product> findByStatus(ProductStatus status);

    // ========== 2. Interface Projection (Spring Data JPA, ~2018년) ==========

    /**
     * Interface Projection - 필요한 컬럼만 SELECT
     * SELECT product_id, product_name, price, status FROM tb_product WHERE status = ?
     *
     * 특징:
     * - 필요한 컬럼만 조회
     * - 프록시 객체 생성 (성능 오버헤드)
     * - 코드 간결
     *
     * 현재 상황: Record가 나온 후 거의 사용 안 함 (레거시)
     */
    List<ProductSummary> findSummaryByStatus(ProductStatus status);

    // ========== 3. Record Projection (현재 추세!, Java 16+, 2021년~) ==========

    /**
     * Record Projection - 필요한 컬럼만 SELECT
     * SELECT product_id, product_name, price, status FROM tb_product WHERE status = ?
     *
     * 특징:
     * - 필요한 컬럼만 조회
     * - 불변 객체 (Record)
     * - 프록시 없음 (성능 우수)
     * - 코드 간결
     * - equals/hashCode 자동
     *
     * 현재 실무 표준!
     */
    List<ProductDto> findDtoByStatus(ProductStatus status);
}
