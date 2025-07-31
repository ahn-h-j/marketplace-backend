package com.market.marketplacebackend.cart.dto;

import com.market.marketplacebackend.cart.domain.Cart;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CartResponseDto {
    private List<CartItemResponseDto> items;
    private int totalPrice;

    public static CartResponseDto from(Cart cart) {
        List<CartItemResponseDto> itemDtos = cart.getCartItems().stream()
                .map(CartItemResponseDto::fromEntity)
                .collect(Collectors.toList());

        int total = cart.getCartItems().stream()
                .mapToInt(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        return CartResponseDto.builder()
                .items(itemDtos)
                .totalPrice(total)
                .build();
    }
}
