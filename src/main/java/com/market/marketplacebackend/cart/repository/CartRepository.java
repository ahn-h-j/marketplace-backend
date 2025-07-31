package com.market.marketplacebackend.cart.repository;

import com.market.marketplacebackend.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
