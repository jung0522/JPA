package io.goorm.jpa.service;

import io.goorm.jpa.dto.ProductResponse;
import io.goorm.jpa.entity.Product;
import io.goorm.jpa.enums.ProductStatus;
import io.goorm.jpa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 Paging Service
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductPagingService {

    private final ProductRepository productRepository;

    /**
     * 전체 조회 (페이징)
     */
    public Page<ProductResponse> findAll(Pageable pageable) {
        Page<Product> products = productRepository.findAllBy(pageable);
        return products.map(ProductResponse::from);
    }

    /**
     * 상태별 조회 (페이징)
     */
    public Page<ProductResponse> findByStatus(String status, Pageable pageable) {
        ProductStatus productStatus = ProductStatus.valueOf(status);
        Page<Product> products = productRepository.findByStatus(productStatus, pageable);
        return products.map(ProductResponse::from);
    }

    /**
     * 상품명 포함 검색 (페이징)
     */
    public Page<ProductResponse> searchByKeyword(String keyword, Pageable pageable) {
        Page<Product> products = productRepository.findByProductNameContaining(keyword, pageable);
        return products.map(ProductResponse::from);
    }

    /**
     * 설명 포함 검색 (페이징)
     */
    public Page<ProductResponse> searchByDescription(String keyword, Pageable pageable) {
        Page<Product> products = productRepository.findByDescriptionContaining(keyword, pageable);
        return products.map(ProductResponse::from);
    }

    /**
     * 상품명 OR 설명 포함 검색 (페이징)
     */
    public Page<ProductResponse> searchByKeywordOrDescription(
            String nameKeyword, String descKeyword, Pageable pageable) {
        Page<Product> products = productRepository.findByProductNameContainingOrDescriptionContaining(
                nameKeyword, descKeyword, pageable);
        return products.map(ProductResponse::from);
    }

    /**
     * 가격 범위 검색 (페이징)
     */
    public Page<ProductResponse> findByPriceRange(Integer minPrice, Integer maxPrice, Pageable pageable) {
        Page<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        return products.map(ProductResponse::from);
    }

    /**
     * 가격 이상 (페이징)
     */
    public Page<ProductResponse> findByPriceGreaterThan(Integer price, Pageable pageable) {
        Page<Product> products = productRepository.findByPriceGreaterThanEqual(price, pageable);
        return products.map(ProductResponse::from);
    }

    /**
     * 가격 미만 (페이징)
     */
    public Page<ProductResponse> findByPriceLessThan(Integer price, Pageable pageable) {
        Page<Product> products = productRepository.findByPriceLessThan(price, pageable);
        return products.map(ProductResponse::from);
    }

    /**
     * 재고 초과 (페이징)
     */
    public Page<ProductResponse> findByStockGreaterThan(Integer quantity, Pageable pageable) {
        Page<Product> products = productRepository.findByStockQuantityGreaterThan(quantity, pageable);
        return products.map(ProductResponse::from);
    }

    /**
     * 재고 있는 상품 (페이징)
     */
    public Page<ProductResponse> findProductsInStock(Pageable pageable) {
        Page<Product> products = productRepository.findByStockQuantityGreaterThanEqual(0, pageable);
        return products.map(ProductResponse::from);
    }

    /**
     * 상태 + 가격 이상 (페이징)
     */
    public Page<ProductResponse> findByStatusAndPrice(String status, Integer price, Pageable pageable) {
        ProductStatus productStatus = ProductStatus.valueOf(status);
        Page<Product> products = productRepository.findByStatusAndPriceGreaterThanEqual(
                productStatus, price, pageable);
        return products.map(ProductResponse::from);
    }

    /**
     * 상품명 포함 + 가격 범위 (페이징)
     */
    public Page<ProductResponse> searchByKeywordAndPriceRange(
            String keyword, Integer minPrice, Integer maxPrice, Pageable pageable) {
        Page<Product> products = productRepository.findByProductNameContainingAndPriceBetween(
                keyword, minPrice, maxPrice, pageable);
        return products.map(ProductResponse::from);
    }

    /**
     * 상품명 포함 + 가격 범위 + 재고 초과 (페이징)
     */
    public Page<ProductResponse> searchFull(
            String keyword, Integer minPrice, Integer maxPrice, Integer minStock, Pageable pageable) {
        Page<Product> products = productRepository
                .findByProductNameContainingAndPriceBetweenAndStockQuantityGreaterThan(
                        keyword, minPrice, maxPrice, minStock, pageable);
        return products.map(ProductResponse::from);
    }

    /**
     * 상태 + 재고 초과 (페이징)
     */
    public Page<ProductResponse> findByStatusAndStock(String status, Integer quantity, Pageable pageable) {
        ProductStatus productStatus = ProductStatus.valueOf(status);
        Page<Product> products = productRepository.findByStatusAndStockQuantityGreaterThan(
                productStatus, quantity, pageable);
        return products.map(ProductResponse::from);
    }

    /**
     * 상태 OR 가격 이상 (페이징)
     */
    public Page<ProductResponse> findByStatusOrPrice(String status, Integer price, Pageable pageable) {
        ProductStatus productStatus = ProductStatus.valueOf(status);
        Page<Product> products = productRepository.findByStatusOrPriceGreaterThanEqual(
                productStatus, price, pageable);
        return products.map(ProductResponse::from);
    }
}
