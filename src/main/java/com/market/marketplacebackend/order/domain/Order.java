package com.market.marketplacebackend.order.domain;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.cart.domain.CartItem;
import com.market.marketplacebackend.common.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Slf4j
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

    private Integer totalPrice;

    private LocalDateTime expiresAt;

    public static Order create(Account account, List<CartItem> cartItems, Map<Long, Integer> itemQuantities, LocalDateTime expiresAt) {
        Order order = Order.builder()
                .account(account)
                .orderStatus(OrderStatus.PENDING)
                .expiresAt(expiresAt)
                .build();

        List<OrderItem> list = cartItems.stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .cartItem(cartItem)
                        .quantity(itemQuantities.get(cartItem.getId()))
                        .build())
                .toList();

        order.setOrderItems(list);
        order.calculateTotalPrice();
        log.info("order : {}", order);
        return order;
    }
    private void calculateTotalPrice() {
        this.totalPrice = this.orderItems.stream()
                .mapToInt(item -> item.getCartItem().getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    public void cancel() {
        this.orderStatus = OrderStatus.CANCELED;
    }
}
