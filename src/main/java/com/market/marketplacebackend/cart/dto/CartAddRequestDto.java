package com.market.marketplacebackend.cart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartAddRequestDto {
    @NotNull(message = "상품을 선택해 주세요")
    private Long productId;
    @NotNull(message = "상품 수량을 선택해 주세요")
    private Integer quantity;
}
