package io.goorm.jpa.repository;

import io.goorm.jpa.entity.Product;
import io.goorm.jpa.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 상품 Paging Repository
 * Query Methods + Pageable 조합
 */
public interface ProductPagingRepository {

    // ========== 기본 페이징 ==========

    /**
     * 전체 조회 (페이징)
     */
    Page<Product> findAllBy(Pageable pageable);

    /**
     * 상태별 조회 (페이징)
     */
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    // ========== 문자열 검색 + 페이징 ==========

    /**
     * 상품명 포함 검색 (페이징)
     */
    Page<Product> findByProductNameContaining(String keyword, Pageable pageable);

    /**
     * 설명 포함 검색 (페이징)
     */
    Page<Product> findByDescriptionContaining(String keyword, Pageable pageable);

    /**
     * 상품명 OR 설명 포함 검색 (페이징)
     */
    Page<Product> findByProductNameContainingOrDescriptionContaining(
            String nameKeyword, String descKeyword, Pageable pageable);

    // ========== 가격 조건 + 페이징 ==========

    /**
     * 가격 범위 검색 (페이징)
     */
    Page<Product> findByPriceBetween(Integer minPrice, Integer maxPrice, Pageable pageable);

    /**
     * 가격 이상 (페이징)
     */
    Page<Product> findByPriceGreaterThanEqual(Integer price, Pageable pageable);

    /**
     * 가격 미만 (페이징)
     */
    Page<Product> findByPriceLessThan(Integer price, Pageable pageable);

    // ========== 재고 조건 + 페이징 ==========

    /**
     * 재고 초과 (페이징)
     */
    Page<Product> findByStockQuantityGreaterThan(Integer quantity, Pageable pageable);

    /**
     * 재고 0 이상 (재고 있는 상품) (페이징)
     */
    Page<Product> findByStockQuantityGreaterThanEqual(Integer quantity, Pageable pageable);

    // ========== 복합 조건 + 페이징 ==========

    /**
     * 상태 + 가격 이상 (페이징)
     */
    Page<Product> findByStatusAndPriceGreaterThanEqual(
            ProductStatus status, Integer price, Pageable pageable);

    /**
     * 상품명 포함 + 가격 범위 (페이징)
     */
    Page<Product> findByProductNameContainingAndPriceBetween(
            String keyword, Integer minPrice, Integer maxPrice, Pageable pageable);

    /**
     * 상품명 포함 + 가격 범위 + 재고 초과 (페이징)
     */
    Page<Product> findByProductNameContainingAndPriceBetweenAndStockQuantityGreaterThan(
            String keyword, Integer minPrice, Integer maxPrice, Integer minStock, Pageable pageable);

    /**
     * 상태 + 재고 초과 (페이징)
     */
    Page<Product> findByStatusAndStockQuantityGreaterThan(
            ProductStatus status, Integer quantity, Pageable pageable);

    /**
     * 상태 OR 가격 이상 (페이징)
     */
    Page<Product> findByStatusOrPriceGreaterThanEqual(
            ProductStatus status, Integer price, Pageable pageable);
}
