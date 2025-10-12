package io.goorm.jpa.controller;

import io.goorm.jpa.dto.ProductResponse;
import io.goorm.jpa.service.ProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 상품 Query Controller (조회)
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductQueryController {

    private final ProductQueryService productQueryService;

    /**
     * 상품명으로 조회
     * GET /api/products/name/{productName}
     */
    @GetMapping("/name/{productName}")
    public ResponseEntity<ProductResponse> getProductByName(@PathVariable String productName) {
        ProductResponse response = productQueryService.findByProductName(productName);
        return ResponseEntity.ok(response);
    }

    /**
     * 가격 범위로 검색
     * GET /api/products/price-range?min=1000000&max=3000000
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<ProductResponse>> getProductsByPriceRange(
            @RequestParam Integer min,
            @RequestParam Integer max) {
        List<ProductResponse> responses = productQueryService.findByPriceRange(min, max);
        return ResponseEntity.ok(responses);
    }

    /**
     * 상품명 키워드 검색
     * GET /api/products/search?keyword=맥북
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        List<ProductResponse> responses = productQueryService.searchByKeyword(keyword);
        return ResponseEntity.ok(responses);
    }

    /**
     * 재고 있는 상품 조회
     * GET /api/products/in-stock
     */
    @GetMapping("/in-stock")
    public ResponseEntity<List<ProductResponse>> getProductsInStock() {
        List<ProductResponse> responses = productQueryService.findProductsInStock();
        return ResponseEntity.ok(responses);
    }

    /**
     * 최신 상품 10개 조회
     * GET /api/products/latest
     */
    @GetMapping("/latest")
    public ResponseEntity<List<ProductResponse>> getLatestProducts() {
        List<ProductResponse> responses = productQueryService.findLatestProducts();
        return ResponseEntity.ok(responses);
    }

    /**
     * 가격 이상 상품 조회
     * GET /api/products/price-greater-than?price=1000000
     */
    @GetMapping("/price-greater-than")
    public ResponseEntity<List<ProductResponse>> getProductsByPriceGreaterThan(@RequestParam Integer price) {
        List<ProductResponse> responses = productQueryService.findByPriceGreaterThan(price);
        return ResponseEntity.ok(responses);
    }

    /**
     * 상태별 상품 개수 조회
     * GET /api/products/count/ACTIVE
     */
    @GetMapping("/count/{status}")
    public ResponseEntity<Long> countProductsByStatus(@PathVariable String status) {
        Long count = productQueryService.countByStatus(status);
        return ResponseEntity.ok(count);
    }

    /**
     * 상품명 존재 여부 확인
     * GET /api/products/exists?name=맥북 프로 M3
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkProductExists(@RequestParam String name) {
        Boolean exists = productQueryService.existsByProductName(name);
        return ResponseEntity.ok(exists);
    }

    /**
     * 상태별 상품 조회 (가격 내림차순)
     * GET /api/products/by-status?status=ACTIVE
     */
    @GetMapping("/by-status")
    public ResponseEntity<List<ProductResponse>> getProductsByStatus(@RequestParam String status) {
        List<ProductResponse> responses = productQueryService.findByStatusOrderByPriceDesc(status);
        return ResponseEntity.ok(responses);
    }

    /**
     * 상품명 + 가격 범위 검색
     * GET /api/products/search/with-price?keyword=맥북&min=1000000&max=3000000
     */
    @GetMapping("/search/with-price")
    public ResponseEntity<List<ProductResponse>> searchProductsWithPrice(
            @RequestParam String keyword,
            @RequestParam Integer min,
            @RequestParam Integer max) {
        List<ProductResponse> responses = productQueryService.searchByKeywordAndPriceRange(keyword, min, max);
        return ResponseEntity.ok(responses);
    }

    /**
     * 상품명 + 가격 범위 + 재고 검색
     * GET /api/products/search/full?keyword=맥북&min=1000000&max=3000000&minStock=1
     */
    @GetMapping("/search/full")
    public ResponseEntity<List<ProductResponse>> searchProductsFull(
            @RequestParam String keyword,
            @RequestParam Integer min,
            @RequestParam Integer max,
            @RequestParam Integer minStock) {
        List<ProductResponse> responses = productQueryService.searchByKeywordAndPriceRangeAndStock(
                keyword, min, max, minStock);
        return ResponseEntity.ok(responses);
    }

    /**
     * 최고가 상품 조회
     * GET /api/products/most-expensive
     */
    @GetMapping("/most-expensive")
    public ResponseEntity<ProductResponse> getMostExpensiveProduct() {
        ProductResponse response = productQueryService.findMostExpensiveProduct();
        return ResponseEntity.ok(response);
    }
}
