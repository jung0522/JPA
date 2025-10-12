package io.goorm.jpa.repository;

import io.goorm.jpa.entity.Product;
import io.goorm.jpa.enums.ProductStatus;

import java.util.List;
import java.util.Optional;

/**
 * 상품 Query Repository (Query Methods)
 * 다양한 조회 메서드를 정의
 */
public interface ProductQueryRepository {

    // ========== 기본 조회 ==========

    /**
     * 상품명으로 조회
     */
    Optional<Product> findByProductName(String productName);

    /**
     * 상태로 조회
     */
    List<Product> findByStatus(ProductStatus status);

    // ========== 비교 연산 ==========

    /**
     * 가격 범위로 검색
     */
    List<Product> findByPriceBetween(Integer minPrice, Integer maxPrice);

    /**
     * 가격 이상 상품 조회
     */
    List<Product> findByPriceGreaterThanEqual(Integer price);

    /**
     * 가격 미만 상품 조회
     */
    List<Product> findByPriceLessThan(Integer price);

    /**
     * 재고 초과 상품 조회
     */
    List<Product> findByStockQuantityGreaterThan(Integer quantity);

    // ========== 문자열 검색 ==========

    /**
     * 상품명 포함 검색 (LIKE %keyword%)
     */
    List<Product> findByProductNameContaining(String keyword);

    /**
     * 상품명 시작 검색 (LIKE keyword%)
     */
    List<Product> findByProductNameStartingWith(String prefix);

    /**
     * 상품명 끝 검색 (LIKE %keyword)
     */
    List<Product> findByProductNameEndingWith(String suffix);

    /**
     * 설명 포함 검색
     */
    List<Product> findByDescriptionContaining(String keyword);

    // ========== 조건 조합 (AND) ==========

    /**
     * 상태 + 가격 이상
     */
    List<Product> findByStatusAndPriceGreaterThanEqual(ProductStatus status, Integer price);

    /**
     * 상품명 포함 + 가격 범위
     */
    List<Product> findByProductNameContainingAndPriceBetween(String keyword, Integer minPrice, Integer maxPrice);

    /**
     * 상품명 포함 + 가격 범위 + 재고 초과
     */
    List<Product> findByProductNameContainingAndPriceBetweenAndStockQuantityGreaterThan(
            String keyword, Integer minPrice, Integer maxPrice, Integer minStock);

    /**
     * 상태 + 재고 초과
     */
    List<Product> findByStatusAndStockQuantityGreaterThan(ProductStatus status, Integer quantity);

    // ========== 조건 조합 (OR) ==========

    /**
     * 상태 OR 가격 이상
     */
    List<Product> findByStatusOrPriceGreaterThanEqual(ProductStatus status, Integer price);

    /**
     * 상품명 포함 OR 설명 포함
     */
    List<Product> findByProductNameContainingOrDescriptionContaining(String nameKeyword, String descKeyword);

    // ========== 정렬 (OrderBy) ==========

    /**
     * 가격 오름차순
     */
    List<Product> findByStatusOrderByPriceAsc(ProductStatus status);

    /**
     * 가격 내림차순
     */
    List<Product> findByStatusOrderByPriceDesc(ProductStatus status);

    /**
     * 등록일 내림차순
     */
    List<Product> findByStatusOrderByCreatedAtDesc(ProductStatus status);

    /**
     * 가격 내림차순 → 등록일 내림차순 (다중 정렬)
     */
    List<Product> findByStatusOrderByPriceDescCreatedAtDesc(ProductStatus status);

    /**
     * 전체 조회 - 가격 오름차순
     */
    List<Product> findAllByOrderByPriceAsc();

    /**
     * 전체 조회 - 등록일 내림차순
     */
    List<Product> findAllByOrderByCreatedAtDesc();

    // ========== 제한 (Top, First) ==========

    /**
     * 최신 상품 10개
     */
    List<Product> findTop10ByOrderByCreatedAtDesc();

    /**
     * 최고가 상품 1개
     */
    Optional<Product> findFirstByOrderByPriceDesc();

    /**
     * 최저가 상품 1개
     */
    Optional<Product> findFirstByOrderByPriceAsc();

    /**
     * 상태별 가격 높은 순 5개
     */
    List<Product> findTop5ByStatusOrderByPriceDesc(ProductStatus status);

    // ========== 존재 여부 (Exists) ==========

    /**
     * 상품명 존재 여부
     */
    boolean existsByProductName(String productName);

    /**
     * 가격 범위 내 상품 존재 여부
     */
    boolean existsByPriceBetween(Integer minPrice, Integer maxPrice);

    /**
     * 재고 있는 상품 존재 여부
     */
    boolean existsByStockQuantityGreaterThan(Integer quantity);

    // ========== 개수 (Count) ==========

    /**
     * 상태별 상품 개수
     */
    long countByStatus(ProductStatus status);

    /**
     * 가격 범위 내 상품 개수
     */
    long countByPriceBetween(Integer minPrice, Integer maxPrice);

    /**
     * 재고 있는 상품 개수
     */
    long countByStockQuantityGreaterThan(Integer quantity);

    /**
     * 상품명 포함 개수
     */
    long countByProductNameContaining(String keyword);

    // ========== 삭제 (Delete) - 주의: 실무에서는 논리 삭제 권장 ==========

    /**
     * 상태별 삭제
     */
    long deleteByStatus(ProductStatus status);

    /**
     * 재고 0 이하 삭제
     */
    long deleteByStockQuantityLessThanEqual(Integer quantity);
}
