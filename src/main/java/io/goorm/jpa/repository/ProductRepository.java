package io.goorm.jpa.repository;

import io.goorm.jpa.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 상품 Repository
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}
