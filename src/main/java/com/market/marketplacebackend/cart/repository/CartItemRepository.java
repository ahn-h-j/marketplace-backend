package com.market.marketplacebackend.cart.repository;

import com.market.marketplacebackend.cart.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
