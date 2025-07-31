package com.market.marketplacebackend.cart.controller;

import com.market.marketplacebackend.cart.domain.Cart;
import com.market.marketplacebackend.cart.domain.CartItem;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CartResponseDto {
    private List<CartItem> items;
    private int totalPrice;

    public static CartResponseDto from(Cart cart) {
        int total = cart.getCartItems().stream()
                .mapToInt(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        return CartResponseDto.builder()
                .items(cart.getCartItems())
                .totalPrice(total)
                .build();
    }
}
