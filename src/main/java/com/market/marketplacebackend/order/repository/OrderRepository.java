package com.market.marketplacebackend.order.repository;

import com.market.marketplacebackend.common.enums.OrderStatus;
import com.market.marketplacebackend.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderStatusAndExpiresAtBefore(OrderStatus orderStatus, LocalDateTime now);

    @Query(value = "SELECT o FROM Order o " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH oi.cartItem ci " +
            "JOIN FETCH ci.product p " +
            "WHERE o.orderStatus = :orderStatus AND o.expiresAt < :now",
            countQuery = "SELECT count(o) FROM Order o WHERE o.orderStatus = :orderStatus AND o.expiresAt < :now")
    Page<Order> findExpiredOrdersWithDetails(@Param("orderStatus") OrderStatus orderStatus, @Param("now") LocalDateTime now, Pageable pageable);
}
