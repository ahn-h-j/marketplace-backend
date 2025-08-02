package com.market.marketplacebackend.cart.service;

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

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProduct(cart, product);

        CartItem finalCartItem = addOrUpdateCartItem(cartItemAddRequestDto, existingCartItem, product, cart);

        cartItemRepository.save(finalCartItem);

        return finalCartItem;
    }

    @Transactional
    public CartItem updateItem(Long accountId, CartItemUpdateDto cartItemUpdateDto) {
        Cart cart = cartRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));

        Product product = productRepository.findById(cartItemUpdateDto.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        CartItem existingCartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_IN_CART));

        return cart.updateCartItem(existingCartItem,cartItemUpdateDto.getQuantity());
    }

    @Transactional
    public void deleteItem(Long accountId, Long productId) {
        Cart cart = cartRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        CartItem existingCartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_IN_CART));

        cart.deleteCartItem(existingCartItem);
    }

    @Transactional
    public void deleteAllCartItems(Long accountId) {
        Cart cart = cartRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));

        cart.deleteAllCartItems();
    }

    private static CartItem addOrUpdateCartItem(CartItemAddRequestDto cartItemAddRequestDto, Optional<CartItem> existingCartItem, Product product, Cart cart) {
        CartItem finalCartItem;
        if (existingCartItem.isPresent()) {
            finalCartItem = existingCartItem.get();
            finalCartItem.addQuantity(cartItemAddRequestDto.getQuantity());
        } else {
            finalCartItem = CartItem.builder()
                    .product(product)
                    .quantity(cartItemAddRequestDto.getQuantity())
                    .build();

            cart.addCartItem(finalCartItem);
        }
        return finalCartItem;
    }



}
