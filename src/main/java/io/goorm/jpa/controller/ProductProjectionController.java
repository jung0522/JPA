package io.goorm.jpa.controller;

import io.goorm.jpa.dto.ProductResponse;
import io.goorm.jpa.dto.projection.ProductDto;
import io.goorm.jpa.dto.projection.ProductSummary;
import io.goorm.jpa.entity.Product;
import io.goorm.jpa.service.ProductProjectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 상품 Projection Controller
 * URL prefix: /api/products/projection
 *
 * 4가지 조회 방식 비교:
 * 1. Entity Projection (전체 컬럼)
 * 2. Interface Projection (필요한 컬럼만)
 * 3. Record/DTO Projection (필요한 컬럼만)
 * 4. Dynamic Projection (런타임 타입 결정)
 */
@RestController
@RequestMapping("/api/products/projection")
@RequiredArgsConstructor
public class ProductProjectionController {

    private final ProductProjectionService productProjectionService;

    /**
     * 1. Entity Projection
     * GET /api/products/projection/entity?status=ACTIVE
     *
     * 특징:
     * - 전체 컬럼 조회 (SELECT *)
     * - Service에서 DTO 변환
     * - 영속성 컨텍스트 관리
     */
    @GetMapping("/entity")
    public ResponseEntity<List<ProductResponse>> getByEntityProjection(@RequestParam String status) {
        List<ProductResponse> responses = productProjectionService.findByEntityProjection(status);
        return ResponseEntity.ok(responses);
    }

    /**
     * 2. Interface Projection
     * GET /api/products/projection/interface?status=ACTIVE
     *
     * 특징:
     * - 필요한 컬럼만 조회 (SELECT product_id, product_name, price, status)
     * - 프록시 객체 생성
     * - Getter 메서드로 접근
     */
    @GetMapping("/interface")
    public ResponseEntity<List<ProductSummary>> getByInterfaceProjection(@RequestParam String status) {
        List<ProductSummary> summaries = productProjectionService.findByInterfaceProjection(status);
        return ResponseEntity.ok(summaries);
    }

    /**
     * 3. Record/DTO Projection
     * GET /api/products/projection/dto?status=ACTIVE
     *
     * 특징:
     * - 필요한 컬럼만 조회 (SELECT product_id, product_name, price, status)
     * - 생성자 기반 매핑
     * - 불변 객체 (Record)
     */
    @GetMapping("/dto")
    public ResponseEntity<List<ProductDto>> getByDtoProjection(@RequestParam String status) {
        List<ProductDto> dtos = productProjectionService.findByDtoProjection(status);
        return ResponseEntity.ok(dtos);
    }

    /**
     * 4. Dynamic Projection (Entity)
     * GET /api/products/projection/dynamic/entity?status=ACTIVE
     *
     * 런타임에 Product.class 타입 지정
     */
    @GetMapping("/dynamic/entity")
    public ResponseEntity<List<Product>> getByDynamicProjectionEntity(@RequestParam String status) {
        List<Product> products = productProjectionService.findByDynamicProjection(status, Product.class);
        return ResponseEntity.ok(products);
    }

    /**
     * 4. Dynamic Projection (Interface)
     * GET /api/products/projection/dynamic/interface?status=ACTIVE
     *
     * 런타임에 ProductSummary.class 타입 지정
     */
    @GetMapping("/dynamic/interface")
    public ResponseEntity<List<ProductSummary>> getByDynamicProjectionInterface(@RequestParam String status) {
        List<ProductSummary> summaries = productProjectionService.findByDynamicProjection(status, ProductSummary.class);
        return ResponseEntity.ok(summaries);
    }

    /**
     * 4. Dynamic Projection (DTO)
     * GET /api/products/projection/dynamic/dto?status=ACTIVE
     *
     * 런타임에 ProductDto.class 타입 지정
     */
    @GetMapping("/dynamic/dto")
    public ResponseEntity<List<ProductDto>> getByDynamicProjectionDto(@RequestParam String status) {
        List<ProductDto> dtos = productProjectionService.findByDynamicProjection(status, ProductDto.class);
        return ResponseEntity.ok(dtos);
    }
}
