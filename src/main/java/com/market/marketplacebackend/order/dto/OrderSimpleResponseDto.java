package com.market.marketplacebackend.order.dto;

import com.market.marketplacebackend.common.enums.OrderStatus;
import com.market.marketplacebackend.order.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSimpleResponseDto {
    private Long orderId;
    private String representativeProductName;
    private int totalPrice;
    private OrderStatus orderStatus;

    public static OrderSimpleResponseDto fromEntity(Order order) {
        return OrderSimpleResponseDto.builder()
                .orderId(order.getId())
                .representativeProductName(order.getRepresentativeProductNameAndCount())
                .totalPrice(order.getTotalPrice())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}
