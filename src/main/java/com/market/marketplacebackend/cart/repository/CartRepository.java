package com.market.marketplacebackend.cart.repository;

import com.market.marketplacebackend.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByAccountId(Long accountId);
}
