package com.market.marketplacebackend.cart.service;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.cart.domain.Cart;
import com.market.marketplacebackend.cart.domain.CartItem;
import com.market.marketplacebackend.cart.dto.CartItemAddRequestDto;
import com.market.marketplacebackend.cart.dto.CartItemUpdateDto;
import com.market.marketplacebackend.cart.repository.CartItemRepository;
import com.market.marketplacebackend.cart.repository.CartRepository;
import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import com.market.marketplacebackend.product.domain.Product;
import com.market.marketplacebackend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public void createCartForAccount(Account savedAccount) {
        cartRepository.save(new Cart(savedAccount));
    }

    @Transactional(readOnly = true)
    public Cart getCart(Long accountId) {
        return cartRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));
    }

    @Transactional
    public CartItem addItemToCart(Long accountId, CartItemAddRequestDto cartItemAddRequestDto) {
        Cart cart = cartRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));

        Product product = productRepository.findById(cartItemAddRequestDto.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        return cart.addProduct(product, cartItemAddRequestDto.getQuantity());
    }

    @Transactional
    public CartItem updateItem(Long accountId, CartItemUpdateDto cartItemUpdateDto) {
        CartItem cartItem = cartItemRepository.findById(cartItemUpdateDto.getCartItemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_IN_CART));

        if (!cartItem.getCart().getAccount().getId().equals(accountId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_CART); // 또는 적절한 권한 없음 에러
        }

        return cartItem.getCart().updateCartItem(cartItem,cartItemUpdateDto.getQuantity());
    }

    @Transactional
    public void deleteItem(Long accountId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_IN_CART));

        if (!cartItem.getCart().getAccount().getId().equals(accountId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_CART); // 또는 적절한 권한 없음 에러
        }

        cartItem.getCart().deleteCartItem(cartItem);
    }

    @Transactional
    public void deleteAllCartItems(Long accountId) {
        Cart cart = cartRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));

        cart.deleteAllCartItems();
    }
}
