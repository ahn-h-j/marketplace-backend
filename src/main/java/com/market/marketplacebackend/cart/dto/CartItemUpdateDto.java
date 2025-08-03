package com.market.marketplacebackend.cart.dto;

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
public class CartItemUpdateDto {
    @NotNull(message = "상품을 선택해 주세요")
    private Long cartItemId;
    @NotNull(message = "상품 수량을 선택해 주세요")
    @Min(0)
    private Integer quantity;
}