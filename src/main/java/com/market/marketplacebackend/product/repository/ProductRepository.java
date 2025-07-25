package com.market.marketplacebackend.product.repository;

import com.market.marketplacebackend.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
