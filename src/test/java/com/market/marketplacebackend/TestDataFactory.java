package com.market.marketplacebackend;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.cart.domain.Cart;
import com.market.marketplacebackend.cart.domain.CartItem;
import com.market.marketplacebackend.cart.dto.CartItemAddRequestDto;
import com.market.marketplacebackend.cart.dto.CartItemUpdateDto;
import com.market.marketplacebackend.common.enums.AccountRole;
import com.market.marketplacebackend.common.enums.Category;
import com.market.marketplacebackend.product.domain.Product;

public class TestDataFactory {

    public static Account createAccount() {
        return Account.builder()
                .name("test User")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.BUYER)
                .build();
    }
    public static Account createAccount(Long id) {
        return Account.builder()
                .id(id)
                .name("test User")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.BUYER)
                .build();
    }

    public static Product createProduct(Account account) {
        return Product.builder()
                .name("test product")
                .price(10000)
                .description("this is test product")
                .stock(100)
                .category(Category.FASHION)
                .account(account)
                .imageUrl("ImageUrl")
                .build();
    }

    public static Product createProduct(Long id, Account account) {
        return Product.builder()
                .id(id)
                .name("test product")
                .price(10000)
                .description("this is test product")
                .stock(100)
                .category(Category.FASHION)
                .account(account)
                .imageUrl("ImageUrl")
                .build();
    }

    public static Cart createCart(Account account) {
        return Cart.builder()
                .account(account)
                .build();
    }

    public static CartItem createCartItem(Product product, Cart cart, int quantity) {
        return CartItem.builder()
                .product(product)
                .cart(cart)
                .quantity(quantity)
                .build();
    }

    public static CartItemAddRequestDto createCartItemAddRequestDto(Long productId, int quantity) {
        return CartItemAddRequestDto.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
    }

    public static CartItemUpdateDto createCartItemUpdateDto(Long cartItemId, int quantity) {
        return CartItemUpdateDto.builder()
                .cartItemId(cartItemId)
                .quantity(quantity)
                .build();
    }
}

