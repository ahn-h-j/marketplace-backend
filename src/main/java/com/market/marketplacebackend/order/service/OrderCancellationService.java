package com.market.marketplacebackend.order.service;

import com.market.marketplacebackend.order.domain.Order;
import com.market.marketplacebackend.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderCancellationService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cancelOrder(Order order){
        order.cancel();
        order.getOrderItems().forEach(orderItem -> {
            Product product = orderItem.getCartItem().getProduct();
            product.increaseStock(orderItem.getQuantity());
        });
    }
}
