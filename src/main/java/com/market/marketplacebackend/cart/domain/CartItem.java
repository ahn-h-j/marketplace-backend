package com.market.marketplacebackend.cart.domain;

import com.market.marketplacebackend.product.domain.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(nullable = false)
    private int quantity;

    public static CartItem createCartItem(Product product, int quantity) {
        return CartItem.builder()
                .product(product)
                .quantity(quantity)
                .build();
    }

    public void addQuantity(Integer quantity) {
        this.quantity += quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(getId(), cartItem.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    void setCart(Cart cart) {
        this.cart = cart;
    }

    public void updateQuantity(Integer quantity){
        this.quantity = quantity;
    }
}
