package com.market.marketplacebackend.order.controller;

import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.order.domain.Order;
import com.market.marketplacebackend.order.dto.OrderCreateRequestDto;
import com.market.marketplacebackend.order.dto.OrderResponseDto;
import com.market.marketplacebackend.order.dto.OrderStatusUpdateRequestDto;
import com.market.marketplacebackend.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{accountId}")
    public ResponseEntity<ServiceResult<OrderResponseDto>> createOrder(@PathVariable Long accountId,
                                                                       @Valid @RequestBody OrderCreateRequestDto orderCreateRequestDto
                                                                       ){
        Order serviceResult = orderService.createOrder(accountId, orderCreateRequestDto);

        OrderResponseDto orderResponseDto = OrderResponseDto.fromEntity(serviceResult);
        ServiceResult<OrderResponseDto> finalResult = ServiceResult.success("주문 생성 완료", orderResponseDto);

        return ResponseEntity.ok(finalResult);
    }

    @PatchMapping("/{orderId}/{accountId}")
    public ResponseEntity<ServiceResult<OrderResponseDto>> changeOrderStatus(@PathVariable Long accountId,
                                                                             @PathVariable Long orderId,
                                                                             @Valid @RequestBody OrderStatusUpdateRequestDto orderStatusUpdateRequestDto){
        Order serviceResult = orderService.changeOrderStatus(accountId, orderId, orderStatusUpdateRequestDto);

        OrderResponseDto orderResponseDto = OrderResponseDto.fromEntity(serviceResult);
        ServiceResult<OrderResponseDto> finalResult = ServiceResult.success("상태 변경 완료", orderResponseDto);

        return ResponseEntity.ok(finalResult);
    }


    //주문 상태 변경, 내 주문 목록 조회, 주문 상세 조회
}
