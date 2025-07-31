package com.market.marketplacebackend.cart.controller;

import com.market.marketplacebackend.cart.domain.Cart;
import com.market.marketplacebackend.cart.domain.CartItem;
import com.market.marketplacebackend.cart.dto.CartAddRequestDto;
import com.market.marketplacebackend.cart.dto.CartItemResponseDto;
import com.market.marketplacebackend.cart.dto.CartResponseDto;
import com.market.marketplacebackend.cart.service.CartService;
import com.market.marketplacebackend.common.ServiceResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/{accountId}")
    public ResponseEntity<ServiceResult<CartResponseDto>> getCart(@PathVariable Long accountId) {
        Cart cart = cartService.getCart(accountId);
        CartResponseDto cartResponseDto = CartResponseDto.from(cart);
        ServiceResult<CartResponseDto> finalResult = ServiceResult.success("카트 조회 완료", cartResponseDto);
        return ResponseEntity.ok(finalResult);
    }

    @PostMapping("/items/{accountId}")
    public ResponseEntity<ServiceResult<CartItemResponseDto>> addItemToCart(@Valid @RequestBody CartAddRequestDto cartAddRequestDto,
                                                                            @PathVariable Long accountId
    ){
        CartItem serviceResult = cartService.addItemToCart(accountId, cartAddRequestDto);

        CartItemResponseDto cartItemResponseDto = CartItemResponseDto.fromEntity(serviceResult);
        ServiceResult<CartItemResponseDto> finalResult = ServiceResult.success("장바구니에 상품 추가 완료", cartItemResponseDto);

        return ResponseEntity.ok(finalResult);
    }
}
