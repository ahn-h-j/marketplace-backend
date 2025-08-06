package com.market.marketplacebackend.order.repository;

import com.market.marketplacebackend.common.enums.OrderStatus;
import com.market.marketplacebackend.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderStatusAndExpiresAtBefore(OrderStatus orderStatus, LocalDateTime now);
}
