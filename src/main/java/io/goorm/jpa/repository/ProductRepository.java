package io.goorm.jpa.repository;

import io.goorm.jpa.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 상품 Repository
 * - JpaRepository: 기본 CRUD 메서드 제공
 * - ProductQueryRepository: Query Methods 제공
 * - ProductPagingRepository: Paging Query Methods 제공
 * - ProductProjectionRepository: Projection Query Methods 제공
 */
public interface ProductRepository extends JpaRepository<Product, Long>,
        ProductQueryRepository,
        ProductPagingRepository,
        ProductProjectionRepository {
}
