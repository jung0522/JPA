package io.goorm.jpa.controller;

import io.goorm.jpa.dto.ProductCreateRequest;
import io.goorm.jpa.dto.ProductResponse;
import io.goorm.jpa.dto.ProductUpdateRequest;
import io.goorm.jpa.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 상품 Controller
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 생성
     * POST /api/products
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductCreateRequest request) {
        ProductResponse response = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 상품 수정
     * PUT /api/products/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable("id") Long productId,
            @RequestBody ProductUpdateRequest request) {
        ProductResponse response = productService.update(productId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 삭제
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long productId) {
        productService.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
