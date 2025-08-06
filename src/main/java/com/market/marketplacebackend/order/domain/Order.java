package com.market.marketplacebackend.order.domain;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.cart.domain.CartItem;
import com.market.marketplacebackend.common.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
@Entity
@Getter
@Builder
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Setter
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Builder.Default
    private Integer totalPrice = 0;

    private LocalDateTime expiresAt;

    public static Order create(Account account, List<CartItem> cartItems, Map<Long, Integer> itemQuantities, LocalDateTime expiresAt) {
        Order order = Order.builder()
                .account(account)
                .orderStatus(OrderStatus.PENDING)
                .expiresAt(expiresAt)
                .build();

        cartItems.forEach(cartItem -> {
            OrderItem newOrderItem = OrderItem.builder()
                    .order(order)
                    .cartItem(cartItem)
                    .quantity(itemQuantities.get(cartItem.getId()))
                    .build();

            order.addOrderItem(newOrderItem);
        });
        return order;
    }

    private void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
        this.totalPrice += item.getCartItem().getProduct().getPrice() * item.getQuantity();
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(this.orderItems);
    }

    public void cancel() {
        this.orderStatus = OrderStatus.CANCELED;
    }
}
