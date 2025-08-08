package com.market.marketplacebackend.order.repository;

import com.market.marketplacebackend.common.enums.OrderStatus;
import com.market.marketplacebackend.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByAccountId(Long accountId, Pageable pageable);

    @Query("SELECT o.id FROM Order o WHERE o.orderStatus = :orderStatus AND o.expiresAt < :now")
    Page<Long> findExpiredOrderIds(OrderStatus orderStatus, LocalDateTime now, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH oi.cartItem ci " +
            "JOIN FETCH ci.product p " +
            "WHERE o.id IN :orderIds")
    List<Order> findWithDetailsByIds(List<Long> orderIds);

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH oi.cartItem ci " +
            "JOIN FETCH ci.product p " +
            "JOIN FETCH o.account a " +
            "WHERE o.id = :orderId")
    Optional<Order> findWithDetailsById(Long orderId);
}
