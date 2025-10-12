package io.goorm.jpa.controller;

import io.goorm.jpa.dto.ProductResponse;
import io.goorm.jpa.dto.projection.ProductDto;
import io.goorm.jpa.dto.projection.ProductSummary;
import io.goorm.jpa.service.ProductProjectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 상품 Projection Controller
 * URL prefix: /api/products/projection
 *
 * 3가지 조회 방식 비교 (발전 과정):
 * 1. Entity Projection (초기, ~2010년대)
 * 2. Interface Projection (Spring Data JPA, ~2018년)
 * 3. Record Projection (현재 추세!, Java 16+, 2021년~)
 */
@RestController
@RequestMapping("/api/products/projection")
@RequiredArgsConstructor
public class ProductProjectionController {

    private final ProductProjectionService productProjectionService;

    /**
     * 1. Entity Projection (초기, ~2010년대)
     * GET /api/products/projection/entity?status=ACTIVE
     *
     * 특징:
     * - 전체 컬럼 조회 (SELECT *)
     * - Service에서 DTO 변환
     * - 영속성 컨텍스트 관리
     *
     * 문제점:
     * - 불필요한 데이터 조회 (메모리/네트워크 낭비)
     * - 수동 변환 번거로움
     *
     * 사용 시기: CUD 작업이 필요한 경우만
     */
    @GetMapping("/entity")
    public ResponseEntity<List<ProductResponse>> getByEntityProjection(@RequestParam String status) {
        List<ProductResponse> responses = productProjectionService.findByEntityProjection(status);
        return ResponseEntity.ok(responses);
    }

    /**
     * 2. Interface Projection (Spring Data JPA, ~2018년)
     * GET /api/products/projection/interface?status=ACTIVE
     *
     * 특징:
     * - 필요한 컬럼만 조회 (SELECT product_id, product_name, price, status)
     * - 프록시 객체 생성
     * - Getter 메서드로 접근
     *
     * 장점:
     * - 필요한 컬럼만 조회 (성능 향상)
     * - 코드 간결
     *
     * 문제점:
     * - 프록시 오버헤드 (성능)
     * - 불변성 불명확
     * - equals/hashCode 애매함
     *
     * 현재 상황: Record가 나온 후 거의 사용 안 함 (레거시)
     */
    @GetMapping("/interface")
    public ResponseEntity<List<ProductSummary>> getByInterfaceProjection(@RequestParam String status) {
        List<ProductSummary> summaries = productProjectionService.findByInterfaceProjection(status);
        return ResponseEntity.ok(summaries);
    }

    /**
     * 3. Record Projection (현재 추세!, Java 16+, 2021년~)
     * GET /api/products/projection/record?status=ACTIVE
     *
     * 특징:
     * - 필요한 컬럼만 조회 (SELECT product_id, product_name, price, status)
     * - 생성자 기반 매핑
     * - 불변 객체 (Record)
     * - 프록시 없음
     *
     * 장점 (모든 문제 해결!):
     * - ✅ 필요한 컬럼만 조회 (성능 향상)
     * - ✅ 코드 간결 (한 줄)
     * - ✅ 불변성 보장 (final 자동)
     * - ✅ equals/hashCode 자동
     * - ✅ 프록시 없음 (성능 우수)
     * - ✅ 타입 안전
     *
     * 현재 실무 표준!
     */
    @GetMapping("/record")
    public ResponseEntity<List<ProductDto>> getByRecordProjection(@RequestParam String status) {
        List<ProductDto> dtos = productProjectionService.findByRecordProjection(status);
        return ResponseEntity.ok(dtos);
    }
}
