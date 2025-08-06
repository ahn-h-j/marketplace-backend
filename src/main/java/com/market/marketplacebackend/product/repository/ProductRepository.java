package com.market.marketplacebackend.product.repository;

import com.market.marketplacebackend.common.enums.Category;
import com.market.marketplacebackend.product.domain.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "SELECT p FROM Product p JOIN FETCH p.account a",
            countQuery = "SELECT count(p) FROM Product p")
    Page<Product> findAllFetchJoin(Pageable pageable);

    @Query(value = "SELECT p FROM Product p JOIN FETCH p.account a WHERE p.category = :category",
            countQuery = "SELECT count(p) FROM Product p WHERE p.category = :category")
    Page<Product> findAllByCategory(Category category, Pageable pageable);

    @Query(value = "SELECT p FROM Product p JOIN FETCH p.account a WHERE p.id = :productId")
    Optional<Product> findByIdWithAccount(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Product> findByIdIn(List<Long> productIds);
}
