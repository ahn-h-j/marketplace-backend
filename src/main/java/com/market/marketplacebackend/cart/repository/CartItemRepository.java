package com.market.marketplacebackend.cart.repository;

import com.market.marketplacebackend.cart.domain.Cart;
import com.market.marketplacebackend.cart.domain.CartItem;
import com.market.marketplacebackend.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
