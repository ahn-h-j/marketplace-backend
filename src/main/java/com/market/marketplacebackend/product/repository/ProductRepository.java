package com.market.marketplacebackend.product.repository;

import com.market.marketplacebackend.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "SELECT p FROM Product p JOIN FETCH p.account a",
            countQuery = "SELECT count(p) FROM Product p")
    Page<Product> findAllFetchJoin(Pageable pageable);
}
