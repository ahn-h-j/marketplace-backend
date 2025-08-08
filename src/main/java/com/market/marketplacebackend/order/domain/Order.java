package com.market.marketplacebackend.order.domain;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.cart.domain.CartItem;
import com.market.marketplacebackend.common.enums.OrderStatus;
import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

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

    private static final Set<OrderStatus> CANCELABLE_STATUSES = Set.of(
            OrderStatus.PENDING,
            OrderStatus.CONFIRMED,
            OrderStatus.PROCESSING
    );

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

    public String getRepresentativeProductNameAndCount() {
        if (orderItems == null || orderItems.isEmpty()) {
            return "주문 상품 없음";
        }

        String firstProductName = orderItems.get(0).getCartItem().getProduct().getName();
        int remainingCount = orderItems.size() - 1;

        if (remainingCount > 0) {
            return firstProductName + " 외 " + remainingCount + "건";
        } else {
            return firstProductName;
        }
    }

    public void cancel() {
        if(!CANCELABLE_STATUSES.contains(this.orderStatus)){
            throw new BusinessException(ErrorCode.INVALID_STATE_TRANSITION);
        }
        this.orderStatus = OrderStatus.CANCELED;
    }

    public void confirm() {
        if(!this.orderStatus.equals(OrderStatus.PENDING)){
            throw new BusinessException(ErrorCode.INVALID_STATE_TRANSITION);
        }
        this.orderStatus = OrderStatus.CONFIRMED;
    }

    public void process() {
        if(!this.orderStatus.equals(OrderStatus.CONFIRMED)){
            throw new BusinessException(ErrorCode.INVALID_STATE_TRANSITION);
        }
        this.orderStatus = OrderStatus.PROCESSING;
    }

    public void ship() {
        if(!this.orderStatus.equals(OrderStatus.PROCESSING)){
            throw new BusinessException(ErrorCode.INVALID_STATE_TRANSITION);
        }
        this.orderStatus = OrderStatus.SHIPPED;
    }

    public void deliver() {
        if(!this.orderStatus.equals(OrderStatus.SHIPPED)){
            throw new BusinessException(ErrorCode.INVALID_STATE_TRANSITION);
        }
        this.orderStatus = OrderStatus.DELIVERED;
    }
}
