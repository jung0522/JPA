package io.goorm.jpa.controller;

import io.goorm.jpa.dto.ProductResponse;
import io.goorm.jpa.service.ProductPagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 상품 Paging Controller
 * URL prefix: /api/products/paging
 */
@RestController
@RequestMapping("/api/products/paging")
@RequiredArgsConstructor
public class ProductPagingController {

    private final ProductPagingService productPagingService;

    /**
     * 전체 조회 (페이징)
     * GET /api/products/paging?page=0&size=10&sort=price,desc
     *
     * @param pageable page: 페이지 번호 (0부터 시작)
     *                 size: 페이지 크기 (기본 10)
     *                 sort: 정렬 (예: price,desc 또는 createdAt,asc)
     */
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> responses = productPagingService.findAll(pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 상태별 조회 (페이징)
     * GET /api/products/paging/by-status?status=ACTIVE&page=0&size=10
     */
    @GetMapping("/by-status")
    public ResponseEntity<Page<ProductResponse>> getProductsByStatus(
            @RequestParam String status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> responses = productPagingService.findByStatus(status, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 상품명 포함 검색 (페이징)
     * GET /api/products/paging/search?keyword=맥북&page=0&size=10&sort=price,desc
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> responses = productPagingService.searchByKeyword(keyword, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 설명 포함 검색 (페이징)
     * GET /api/products/paging/search-description?keyword=Apple&page=0&size=10
     */
    @GetMapping("/search-description")
    public ResponseEntity<Page<ProductResponse>> searchByDescription(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ProductResponse> responses = productPagingService.searchByDescription(keyword, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 상품명 OR 설명 포함 검색 (페이징)
     * GET /api/products/paging/search-all?name=맥북&desc=Apple&page=0&size=10
     */
    @GetMapping("/search-all")
    public ResponseEntity<Page<ProductResponse>> searchByKeywordOrDescription(
            @RequestParam String name,
            @RequestParam String desc,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ProductResponse> responses = productPagingService.searchByKeywordOrDescription(name, desc, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 가격 범위 검색 (페이징)
     * GET /api/products/paging/price-range?min=1000000&max=3000000&page=0&size=10
     */
    @GetMapping("/price-range")
    public ResponseEntity<Page<ProductResponse>> getProductsByPriceRange(
            @RequestParam Integer min,
            @RequestParam Integer max,
            @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProductResponse> responses = productPagingService.findByPriceRange(min, max, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 가격 이상 (페이징)
     * GET /api/products/paging/price-min?price=2000000&page=0&size=10
     */
    @GetMapping("/price-min")
    public ResponseEntity<Page<ProductResponse>> getProductsByPriceGreaterThan(
            @RequestParam Integer price,
            @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProductResponse> responses = productPagingService.findByPriceGreaterThan(price, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 가격 미만 (페이징)
     * GET /api/products/paging/price-max?price=2000000&page=0&size=10
     */
    @GetMapping("/price-max")
    public ResponseEntity<Page<ProductResponse>> getProductsByPriceLessThan(
            @RequestParam Integer price,
            @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> responses = productPagingService.findByPriceLessThan(price, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 재고 초과 (페이징)
     * GET /api/products/paging/stock?quantity=5&page=0&size=10
     */
    @GetMapping("/stock")
    public ResponseEntity<Page<ProductResponse>> getProductsByStockGreaterThan(
            @RequestParam Integer quantity,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ProductResponse> responses = productPagingService.findByStockGreaterThan(quantity, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 재고 있는 상품 (페이징)
     * GET /api/products/paging/in-stock?page=0&size=10
     */
    @GetMapping("/in-stock")
    public ResponseEntity<Page<ProductResponse>> getProductsInStock(
            @PageableDefault(size = 10, sort = "stockQuantity", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> responses = productPagingService.findProductsInStock(pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 상태 + 가격 이상 (페이징)
     * GET /api/products/paging/status-price?status=ACTIVE&price=1000000&page=0&size=10
     */
    @GetMapping("/status-price")
    public ResponseEntity<Page<ProductResponse>> getProductsByStatusAndPrice(
            @RequestParam String status,
            @RequestParam Integer price,
            @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> responses = productPagingService.findByStatusAndPrice(status, price, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 상품명 포함 + 가격 범위 (페이징)
     * GET /api/products/paging/search-price?keyword=맥북&min=1000000&max=3000000&page=0&size=10
     */
    @GetMapping("/search-price")
    public ResponseEntity<Page<ProductResponse>> searchByKeywordAndPriceRange(
            @RequestParam String keyword,
            @RequestParam Integer min,
            @RequestParam Integer max,
            @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProductResponse> responses = productPagingService.searchByKeywordAndPriceRange(
                keyword, min, max, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 상품명 + 가격 범위 + 재고 (페이징)
     * GET /api/products/paging/search-full?keyword=맥북&min=1000000&max=3000000&minStock=1&page=0&size=10
     */
    @GetMapping("/search-full")
    public ResponseEntity<Page<ProductResponse>> searchFull(
            @RequestParam String keyword,
            @RequestParam Integer min,
            @RequestParam Integer max,
            @RequestParam Integer minStock,
            @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProductResponse> responses = productPagingService.searchFull(
                keyword, min, max, minStock, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 상태 + 재고 초과 (페이징)
     * GET /api/products/paging/status-stock?status=ACTIVE&quantity=5&page=0&size=10
     */
    @GetMapping("/status-stock")
    public ResponseEntity<Page<ProductResponse>> getProductsByStatusAndStock(
            @RequestParam String status,
            @RequestParam Integer quantity,
            @PageableDefault(size = 10, sort = "stockQuantity", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> responses = productPagingService.findByStatusAndStock(status, quantity, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 상태 OR 가격 이상 (페이징)
     * GET /api/products/paging/status-or-price?status=ACTIVE&price=2000000&page=0&size=10
     */
    @GetMapping("/status-or-price")
    public ResponseEntity<Page<ProductResponse>> getProductsByStatusOrPrice(
            @RequestParam String status,
            @RequestParam Integer price,
            @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> responses = productPagingService.findByStatusOrPrice(status, price, pageable);
        return ResponseEntity.ok(responses);
    }
}
