package com.market.marketplacebackend.cart.domain;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.product.domain.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    public Cart(Account account) {
        this.account = account;
    }

    public CartItem addProduct(Product product, int quantity) {
        Optional<CartItem> existingItem = this.cartItems.stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst();

        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.addQuantity(quantity);
        } else {
            cartItem = CartItem.createCartItem(product, quantity);
            this.addCartItem(cartItem);
        }
        return cartItem;
    }

    private void addCartItem(CartItem cartItem) {
        this.cartItems.add(cartItem);
        cartItem.setCart(this);
    }

    public CartItem updateCartItem(CartItem cartItem, int quantity){
        cartItem.updateQuantity(quantity);
        if(cartItem.getQuantity() < 1){
            cartItems.remove(cartItem);
            return null;
        }
        return cartItem;
    }

    public void deleteCartItem(CartItem cartItem){
        cartItems.remove(cartItem);
    }

    public void deleteAllCartItems() {
        cartItems.clear();
    }
}
