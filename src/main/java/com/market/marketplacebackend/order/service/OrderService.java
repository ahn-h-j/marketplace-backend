package com.market.marketplacebackend.order.service;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.repository.AccountRepository;
import com.market.marketplacebackend.cart.domain.CartItem;
import com.market.marketplacebackend.cart.repository.CartItemRepository;
import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import com.market.marketplacebackend.order.domain.Order;
import com.market.marketplacebackend.order.dto.OrderCreateRequestDto;
import com.market.marketplacebackend.order.dto.OrderItemDto;
import com.market.marketplacebackend.order.dto.OrderStatusUpdateRequestDto;
import com.market.marketplacebackend.order.repository.OrderRepository;
import com.market.marketplacebackend.product.domain.Product;
import com.market.marketplacebackend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderCancellationService orderCancellationService;

    @Transactional
    public Order createOrder(Long accountId, OrderCreateRequestDto orderCreateRequestDto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        Map<Long, Integer> itemQuantities = orderCreateRequestDto.getOrderItems().stream()
                .collect(Collectors.toMap(OrderItemDto::getCartItemId, OrderItemDto::getQuantity));

        List<CartItem> cartItems = cartItemRepository.findAllByIdWithProduct(itemQuantities.keySet());

        if (cartItems.size() != itemQuantities.size()) {
            throw new BusinessException(ErrorCode.REQUESTED_CART_ITEMS_NOT_FOUND);
        }

        List<Product> products = productRepository.findByIdIn(
                cartItems.stream()
                .map(cartItem -> cartItem.getProduct().getId())
                .toList()
        );

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (CartItem cartItem : cartItems) {
            Product product = productMap.get(cartItem.getProduct().getId());
            int quantity = itemQuantities.get(cartItem.getId());
            product.decreaseStock(quantity);
        }

        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(15);


        Order order = Order.create(account, cartItems, itemQuantities, expirationTime);
        return orderRepository.save(order);
    }

    @Transactional
    public Order changeOrderStatus(Long accountId, Long orderId, OrderStatusUpdateRequestDto orderStatusUpdateRequestDto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if(!order.getAccount().getId().equals(account.getId())){
            throw new BusinessException(ErrorCode.FORBIDDEN_ORDER);
        }

        switch (orderStatusUpdateRequestDto.getNewStatus()){
            case CONFIRMED -> order.confirm();
            case PROCESSING -> order.process();
            case SHIPPED -> order.ship();
            case DELIVERED -> order.deliver();
            case CANCELED -> orderCancellationService.cancelOrder(order);
            default -> throw new BusinessException(ErrorCode.INVALID_STATE_TRANSITION);
        }
        return order;
    }

    @Transactional(readOnly = true)
    public Page<Order> findAllOrder(Long accountId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByAccountId(accountId, pageable);
        List<Order> pageList = orderPage.getContent();

        if (pageList.isEmpty()) {
            return orderPage;
        }
        List<Long> orderIds = pageList.stream()
                .map(Order::getId)
                .toList();

        List<Order> fetchedOrders = orderRepository.findWithDetailsByIds(orderIds);

        Map<Long, Order> tempSortMap = fetchedOrders.stream()
                .collect(Collectors.toMap(Order::getId, Function.identity()));

        List<Order> finalContent = pageList.stream()
                .map(order -> tempSortMap.get(order.getId()))
                .toList();
        return new PageImpl<>(finalContent , orderPage.getPageable(), orderPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Order findOrder(Long accountId, Long orderId) {
       Order order =  orderRepository.findWithDetailsById(orderId)
               .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
       if(!order.getAccount().getId().equals(accountId)){
           throw new BusinessException(ErrorCode.FORBIDDEN_ORDER);
       }
       return order;
    }
}
