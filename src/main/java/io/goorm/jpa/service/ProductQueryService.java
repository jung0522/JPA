package io.goorm.jpa.service;

import io.goorm.jpa.dto.ProductResponse;
import io.goorm.jpa.entity.Product;
import io.goorm.jpa.enums.ProductStatus;
import io.goorm.jpa.exception.ProductNotFoundException;
import io.goorm.jpa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 상품 Query Service (조회)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {

    private final ProductRepository productRepository;

    /**
     * 상품명으로 조회
     */
    public ProductResponse findByProductName(String productName) {
        Product product = productRepository.findByProductName(productName)
                .orElseThrow(() -> new ProductNotFoundException("상품명: " + productName));
        return ProductResponse.from(product);
    }

    /**
     * 가격 범위로 검색
     */
    public List<ProductResponse> findByPriceRange(Integer minPrice, Integer maxPrice) {
        List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice);
        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 상품명 키워드 검색
     */
    public List<ProductResponse> searchByKeyword(String keyword) {
        List<Product> products = productRepository.findByProductNameContaining(keyword);
        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 재고 있는 상품 조회
     */
    public List<ProductResponse> findProductsInStock() {
        List<Product> products = productRepository.findByStockQuantityGreaterThan(0);
        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 최신 상품 10개 조회
     */
    public List<ProductResponse> findLatestProducts() {
        List<Product> products = productRepository.findTop10ByOrderByCreatedAtDesc();
        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 가격 이상 상품 조회
     */
    public List<ProductResponse> findByPriceGreaterThan(Integer price) {
        List<Product> products = productRepository.findByPriceGreaterThanEqual(price);
        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 상태별 상품 개수 조회
     */
    public Long countByStatus(String status) {
        ProductStatus productStatus = ProductStatus.valueOf(status);
        return productRepository.countByStatus(productStatus);
    }

    /**
     * 상품명 존재 여부 확인
     */
    public Boolean existsByProductName(String productName) {
        return productRepository.existsByProductName(productName);
    }

    /**
     * 상태별 상품 조회 (가격 내림차순)
     */
    public List<ProductResponse> findByStatusOrderByPriceDesc(String status) {
        ProductStatus productStatus = ProductStatus.valueOf(status);
        List<Product> products = productRepository.findByStatusOrderByPriceDesc(productStatus);
        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 상품명 + 가격 범위 검색
     */
    public List<ProductResponse> searchByKeywordAndPriceRange(String keyword, Integer minPrice, Integer maxPrice) {
        List<Product> products = productRepository.findByProductNameContainingAndPriceBetween(
                keyword, minPrice, maxPrice);
        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 상품명 + 가격 범위 + 재고 검색
     */
    public List<ProductResponse> searchByKeywordAndPriceRangeAndStock(
            String keyword, Integer minPrice, Integer maxPrice, Integer minStock) {
        List<Product> products = productRepository
                .findByProductNameContainingAndPriceBetweenAndStockQuantityGreaterThan(
                        keyword, minPrice, maxPrice, minStock);
        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 최고가 상품 조회
     */
    public ProductResponse findMostExpensiveProduct() {
        Product product = productRepository.findFirstByOrderByPriceDesc()
                .orElseThrow(() -> new ProductNotFoundException("상품이 없습니다."));
        return ProductResponse.from(product);
    }
}
