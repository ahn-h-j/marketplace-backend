package com.market.marketplacebackend.cart.dto;

import com.market.marketplacebackend.cart.domain.CartItem;
import com.market.marketplacebackend.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDto {
    private Long productId;
    private String productName;
    private int productPrice;
    private int quantity;
    private int totalPricePerItem;

    public static CartItemResponseDto fromEntity(CartItem cartItem) {
        Product product = cartItem.getProduct();
        int price = product.getPrice();
        int quantity = cartItem.getQuantity();

        return CartItemResponseDto.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productPrice(price)
                .quantity(quantity)
                .totalPricePerItem(price * quantity)
                .build();
    }
}
