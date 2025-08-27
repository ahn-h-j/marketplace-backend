package com.market.marketplacebackend.cart.controller;

import com.market.marketplacebackend.cart.domain.Cart;
import com.market.marketplacebackend.cart.domain.CartItem;
import com.market.marketplacebackend.cart.dto.*;
import com.market.marketplacebackend.cart.service.CartService;
import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.security.domain.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ServiceResult<CartResponseDto>> getCart(@AuthenticationPrincipal PrincipalDetails userDetails) {
        Cart cart = cartService.getCart(userDetails.getAccount().getId());
        CartResponseDto cartResponseDto = CartResponseDto.from(cart);
        ServiceResult<CartResponseDto> finalResult = ServiceResult.success("카트 조회 완료", cartResponseDto);
        return ResponseEntity.ok(finalResult);
    }

    @PostMapping("/items")
    public ResponseEntity<ServiceResult<CartItemResponseDto>> addItemToCart(@Valid @RequestBody CartItemAddRequestDto cartItemAddRequestDto,
                                                                            @AuthenticationPrincipal PrincipalDetails userDetails
    ){
        CartItem serviceResult = cartService.addItemToCart(userDetails.getAccount().getId(), cartItemAddRequestDto);

        CartItemResponseDto cartItemResponseDto = CartItemResponseDto.fromEntity(serviceResult);
        ServiceResult<CartItemResponseDto> finalResult = ServiceResult.success("장바구니에 상품 추가 완료", cartItemResponseDto);

        return ResponseEntity.ok(finalResult);
    }

    @PatchMapping("/items")
    public ResponseEntity<ServiceResult<CartItemResponseDto>> updateItem(@Valid @RequestBody CartItemUpdateDto cartItemUpdateDto,
                                                                         @AuthenticationPrincipal PrincipalDetails userDetails
    ){
        CartItem serviceResult = cartService.updateItem(userDetails.getAccount().getId(), cartItemUpdateDto);

        if (serviceResult == null) {
            return ResponseEntity.ok(ServiceResult.success("장바구니에서 상품이 삭제되었습니다.", null));
        }

        CartItemResponseDto cartItemResponseDto = CartItemResponseDto.fromEntity(serviceResult);
        ServiceResult<CartItemResponseDto> finalResult = ServiceResult.success("장바구니에 상품 수정 완료", cartItemResponseDto);

        return ResponseEntity.ok(finalResult);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long cartItemId,
                                           @AuthenticationPrincipal PrincipalDetails userDetails
    ){
        cartService.deleteItem(userDetails.getAccount().getId(), cartItemId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> deleteAllCartItems(@AuthenticationPrincipal PrincipalDetails userDetails){
        cartService.deleteAllCartItems(userDetails.getAccount().getId());

        return ResponseEntity.noContent().build();
    }
}
