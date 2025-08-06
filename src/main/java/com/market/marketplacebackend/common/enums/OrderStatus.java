package com.market.marketplacebackend.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    PENDING("주문 대기"),
    CONFIRMED("주문 확인"),
    PROCESSING("상품 준비중"),
    SHIPPED("배송중"),
    DELIVERED("배송 완료"),
    CANCELED("주문 취소");

    private final String description;
}