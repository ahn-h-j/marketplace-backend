package com.market.marketplacebackend.order.dto;

import com.market.marketplacebackend.order.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private List<OrderItemDto> orderItems;
    private int totalPrice;

    public static OrderResponseDto fromEntity(Order order) {
        return OrderResponseDto.builder()
                .orderItems(order.getOrderItems().stream()
                        .map(orderItem -> {
                            return OrderItemDto.builder()
                                    .cartItemId(orderItem.getCartItem().getId())
                                    .quantity(orderItem.getQuantity())
                                    .build();
                        })
                        .toList())
                .totalPrice(order.getTotalPrice())
                .build();
    }
}
