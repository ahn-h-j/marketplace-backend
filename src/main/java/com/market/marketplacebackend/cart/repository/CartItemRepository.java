package com.market.marketplacebackend.cart.repository;

import com.market.marketplacebackend.cart.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product WHERE ci.id IN :cartItemIds")
    List<CartItem> findAllByIdWithProduct(Set<Long> cartItemIds);
}
