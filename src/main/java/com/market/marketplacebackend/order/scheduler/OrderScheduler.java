package com.market.marketplacebackend.order.scheduler;

import com.market.marketplacebackend.common.enums.OrderStatus;
import com.market.marketplacebackend.order.domain.Order;
import com.market.marketplacebackend.order.repository.OrderRepository;
import com.market.marketplacebackend.order.service.OrderCancellationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderRepository orderRepository;
    private static final int BATCH_SIZE = 100;
    private final OrderCancellationService orderCancellationService;

    @Scheduled(fixedDelay = 60000)
    public void cleanupExpiredOrders() {
        Pageable pageable = PageRequest.of(0, BATCH_SIZE);
        Page<Long> orderPages;
        do {
            orderPages = orderRepository.findExpiredOrderIds(OrderStatus.PENDING, LocalDateTime.now(), pageable);

            List<Long> orderIds = orderPages.getContent();

            if (!orderPages.hasContent()) {
                break;
            }

            List<Order> expiredOrders = orderRepository.findWithDetailsByIds(orderIds);

            for (Order expiredOrder : expiredOrders) {
                orderCancellationService.cancelOrder(expiredOrder);
            }
            pageable = orderPages.nextPageable();

        } while (orderPages.hasNext());
    }
}
