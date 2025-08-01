package com.market.marketplacebackend.cart.domain;

import com.market.marketplacebackend.account.domain.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    private List<CartItem> cartItems = new ArrayList<>();

    public Cart(Account account) {
        this.account = account;
    }

    public void addCartItem(CartItem cartItem) {
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
}
