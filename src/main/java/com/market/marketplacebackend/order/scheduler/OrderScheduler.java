package com.market.marketplacebackend.order.scheduler;

import com.market.marketplacebackend.common.enums.OrderStatus;
import com.market.marketplacebackend.order.domain.Order;
import com.market.marketplacebackend.order.repository.OrderRepository;
import com.market.marketplacebackend.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderRepository orderRepository;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void cleanupExpiredOrders(){
        List<Order> expiredOrders = orderRepository.findByOrderStatusAndExpiresAtBefore(OrderStatus.PENDING, LocalDateTime.now());

        for (Order expiredOrder : expiredOrders) {
            expiredOrder.cancel();
            expiredOrder.getOrderItems().forEach(
                    orderItem -> {
                        Product product = orderItem.getCartItem().getProduct();
                        product.increaseStock(orderItem.getQuantity());
                    });
        }

    }
}
