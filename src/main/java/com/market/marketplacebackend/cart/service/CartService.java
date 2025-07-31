package com.market.marketplacebackend.cart.service;

import com.market.marketplacebackend.cart.domain.Cart;
import com.market.marketplacebackend.cart.repository.CartRepository;
import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));
    }
}
