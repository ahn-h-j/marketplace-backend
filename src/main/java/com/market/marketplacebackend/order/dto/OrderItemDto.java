package com.market.marketplacebackend.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    @NotNull(message = "상품을 선택해 주세요")
    private Long cartItemId;
    @NotNull(message = "상품 수량을 선택해 주세요")
    @Min(value = 1, message = "상품 수량은 1개 이상이어야 합니다")
    private Integer quantity;
}
