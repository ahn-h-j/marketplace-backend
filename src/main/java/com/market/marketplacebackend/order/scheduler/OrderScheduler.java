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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderRepository orderRepository;
    private static final int BATCH_SIZE = 100;
    private final OrderCancellationService orderCancellationService;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void cleanupExpiredOrders(){
        Pageable pageable = PageRequest.of(0, BATCH_SIZE);
        Page<Order> expiredOrdersPage;
        do{
            expiredOrdersPage = orderRepository.findExpiredOrdersWithDetails(OrderStatus.PENDING, LocalDateTime.now(), pageable);

            if(!expiredOrdersPage.hasContent()){
                break;
            }
            for (Order expiredOrder : expiredOrdersPage.getContent()) {
               orderCancellationService.cancelOrder(expiredOrder);
            }
            pageable = expiredOrdersPage.nextPageable();
        }while (expiredOrdersPage.hasNext());
    }
}
