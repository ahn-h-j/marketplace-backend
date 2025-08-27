package com.market.marketplacebackend.order.controller;

import com.market.marketplacebackend.common.CustomPageResponse;
import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.order.domain.Order;
import com.market.marketplacebackend.order.dto.OrderCreateRequestDto;
import com.market.marketplacebackend.order.dto.OrderResponseDto;
import com.market.marketplacebackend.order.dto.OrderSimpleResponseDto;
import com.market.marketplacebackend.order.dto.OrderStatusUpdateRequestDto;
import com.market.marketplacebackend.order.service.OrderService;
import com.market.marketplacebackend.security.domain.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ServiceResult<OrderResponseDto>> createOrder(@AuthenticationPrincipal PrincipalDetails userDetails,
                                                                       @Valid @RequestBody OrderCreateRequestDto orderCreateRequestDto
                                                                       ){
        Order serviceResult = orderService.createOrder(userDetails.getAccount().getId(), orderCreateRequestDto);

        OrderResponseDto orderResponseDto = OrderResponseDto.fromEntity(serviceResult);
        ServiceResult<OrderResponseDto> finalResult = ServiceResult.success("주문 생성 완료", orderResponseDto);

        return ResponseEntity.ok(finalResult);
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<ServiceResult<OrderResponseDto>> changeOrderStatus(@AuthenticationPrincipal PrincipalDetails userDetails,
                                                                             @PathVariable Long orderId,
                                                                             @Valid @RequestBody OrderStatusUpdateRequestDto orderStatusUpdateRequestDto){
        Order serviceResult = orderService.changeOrderStatus(userDetails.getAccount().getId(), orderId, orderStatusUpdateRequestDto);

        OrderResponseDto orderResponseDto = OrderResponseDto.fromEntity(serviceResult);
        ServiceResult<OrderResponseDto> finalResult = ServiceResult.success("상태 변경 완료", orderResponseDto);

        return ResponseEntity.ok(finalResult);
    }

    @GetMapping
    public ResponseEntity<ServiceResult<CustomPageResponse<OrderSimpleResponseDto>>> findAllOrder(@AuthenticationPrincipal PrincipalDetails userDetails,
                                                                                    @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
                                                                              ){
        Page<Order> serviceResult = orderService.findAllOrder(userDetails.getAccount().getId(), pageable);

        Page<OrderSimpleResponseDto> orderSimpleResponseDto = serviceResult
                .map(OrderSimpleResponseDto::fromEntity);

        CustomPageResponse<OrderSimpleResponseDto> orderPageResponse = new CustomPageResponse<>(orderSimpleResponseDto);

        ServiceResult<CustomPageResponse<OrderSimpleResponseDto>> finalResult = ServiceResult.success("주문 조회 완료", orderPageResponse);

        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ServiceResult<OrderResponseDto>> findOrder(@AuthenticationPrincipal PrincipalDetails userDetails,
                                                                        @PathVariable Long orderId
    ){
        Order serviceResult = orderService.findOrder(userDetails.getAccount().getId(),orderId);

        OrderResponseDto orderResponseDto = OrderResponseDto.fromEntity(serviceResult);

        ServiceResult<OrderResponseDto> finalResult = ServiceResult.success("주문 상세 조회 완료", orderResponseDto);

        return ResponseEntity.ok(finalResult);
    }
}
