package com.market.marketplacebackend.order.controller;

import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.order.domain.Order;
import com.market.marketplacebackend.order.dto.OrderCreateRequestDto;
import com.market.marketplacebackend.order.dto.OrderResponseDto;
import com.market.marketplacebackend.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{accountId}")
    public ResponseEntity<ServiceResult<OrderResponseDto>> createOrder(@PathVariable Long accountId,
                                                                       @Valid @RequestBody OrderCreateRequestDto orderCreateRequestDto
                                                                       ){
        log.info("in controller");
        Order serviceResult = orderService.createOrder(accountId, orderCreateRequestDto);

        OrderResponseDto orderResponseDto = OrderResponseDto.fromEntity(serviceResult);
        ServiceResult<OrderResponseDto> finalResult = ServiceResult.success("주문 생성 완료", orderResponseDto);

        return ResponseEntity.ok(finalResult);
    }
}
