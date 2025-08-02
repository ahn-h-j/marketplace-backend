package com.market.marketplacebackend.cart.controller;

import com.market.marketplacebackend.cart.domain.Cart;
import com.market.marketplacebackend.cart.domain.CartItem;
import com.market.marketplacebackend.cart.dto.*;
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
    public ResponseEntity<ServiceResult<CartItemResponseDto>> addItemToCart(@Valid @RequestBody CartItemAddRequestDto cartItemAddRequestDto,
                                                                            @PathVariable Long accountId
    ){
        CartItem serviceResult = cartService.addItemToCart(accountId, cartItemAddRequestDto);

        CartItemResponseDto cartItemResponseDto = CartItemResponseDto.fromEntity(serviceResult);
        ServiceResult<CartItemResponseDto> finalResult = ServiceResult.success("장바구니에 상품 추가 완료", cartItemResponseDto);

        return ResponseEntity.ok(finalResult);
    }

    @PatchMapping("/items/{accountId}")
    public ResponseEntity<ServiceResult<CartItemResponseDto>> updateItem(@Valid @RequestBody CartItemUpdateDto cartItemUpdateDto,
                                                                         @PathVariable Long accountId
    ){
        CartItem serviceResult = cartService.updateItem(accountId, cartItemUpdateDto);

        if (serviceResult == null) {
            return ResponseEntity.ok(ServiceResult.success("장바구니에서 상품이 삭제되었습니다.", null));
        }

        CartItemResponseDto cartItemResponseDto = CartItemResponseDto.fromEntity(serviceResult);
        ServiceResult<CartItemResponseDto> finalResult = ServiceResult.success("장바구니에 상품 수정 완료", cartItemResponseDto);

        return ResponseEntity.ok(finalResult);
    }

    @DeleteMapping("/items/{accountId}/{productId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long productId, @PathVariable Long accountId
    ){
        cartService.deleteItem(accountId, productId);

        return ResponseEntity.noContent().build();
    }
    //상품 전체 삭제
}
