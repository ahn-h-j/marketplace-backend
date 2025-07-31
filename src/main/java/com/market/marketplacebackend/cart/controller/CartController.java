package com.market.marketplacebackend.cart.controller;

import com.market.marketplacebackend.cart.domain.Cart;
import com.market.marketplacebackend.cart.service.CartService;
import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.product.dto.ProductDetailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<ServiceResult<CartResponseDto>> getCart(@PathVariable Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        CartResponseDto cartResponseDto = CartResponseDto.from(cart);
        ServiceResult<CartResponseDto> finalResult = ServiceResult.success("카트 조회 완료", cartResponseDto);
        return ResponseEntity.ok(finalResult);
    }
}
