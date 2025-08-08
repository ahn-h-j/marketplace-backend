package com.market.marketplacebackend.order.repository;

import com.market.marketplacebackend.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
