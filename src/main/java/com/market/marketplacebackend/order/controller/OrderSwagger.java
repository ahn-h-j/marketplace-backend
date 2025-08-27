package com.market.marketplacebackend.order.controller;

import com.market.marketplacebackend.common.CustomPageResponse;
import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.order.dto.OrderCreateRequestDto;
import com.market.marketplacebackend.order.dto.OrderResponseDto;
import com.market.marketplacebackend.order.dto.OrderSimpleResponseDto;
import com.market.marketplacebackend.order.dto.OrderStatusUpdateRequestDto;
import com.market.marketplacebackend.security.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order", description = "주문 관리 API")
@RequestMapping("/order")
public interface OrderSwagger {

    @Operation(
            summary = "주문 생성",
            description = "장바구니의 상품들을 바탕으로 새로운 주문을 생성합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @RequestBody(
            description = "주문할 상품들의 정보",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = OrderCreateRequestDto.class),
                    examples = {
                            @ExampleObject(
                                    name = "단일 상품 주문",
                                    summary = "장바구니의 한 상품을 주문하는 경우",
                                    value = """
                                    {
                                        "orderItems": [
                                            {
                                                "cartItemId": 1,
                                                "quantity": 2
                                            }
                                        ]
                                    }
                                    """
                            ),
                            @ExampleObject(
                                    name = "다중 상품 주문",
                                    summary = "장바구니의 여러 상품을 주문하는 경우",
                                    value = """
                                    {
                                        "orderItems": [
                                            {
                                                "cartItemId": 1,
                                                "quantity": 2
                                            },
                                            {
                                                "cartItemId": 3,
                                                "quantity": 1
                                            },
                                            {
                                                "cartItemId": 5,
                                                "quantity": 3
                                            }
                                        ]
                                    }
                                    """
                            )
                    }
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 생성 성공",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                    {
                                        "success": true,
                                        "code": "OK",
                                        "message": "주문 생성 완료",
                                        "data": {
                                            "orderItems": [
                                                {
                                                    "cartItemId": 1,
                                                    "quantity": 2
                                                },
                                                {
                                                    "cartItemId": 3,
                                                    "quantity": 1
                                                }
                                            ],
                                            "totalPrice": 2489000,
                                            "orderStatus": "PENDING"
                                        },
                                        "timeStamp": "2025-08-27T12:18:21.5722917"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "입력값 유효성 검증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = {
                                    @ExampleObject(
                                            name = "유효성 검증 실패",
                                            summary = "수량이 1개 미만인 경우",
                                            value = """
                                            {
                                                "success": false,
                                                "code": "VALIDATION_ERROR",
                                                "message": "상품 수량은 1개 이상이어야 합니다",
                                                "data": null,
                                                "timeStamp": "2025-08-27T12:18:54.1693644"
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "AUTHENTICATION_REQUIRED",
                                        "message": "인증이 필요한 서비스입니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "요청한 장바구니 상품을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "장바구니 상품 미존재",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "REQUESTED_CART_ITEMS_NOT_FOUND",
                                        "message": "요청한 장바구니 상품 중 일부를 찾을 수 없습니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "재고 부족",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "재고 부족",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "NOT_ENOUGH_STOCK",
                                        "message": "재고는 0개 미만이 될 수 없습니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            )
    })
    @PostMapping
    ResponseEntity<ServiceResult<OrderResponseDto>> createOrder(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @Valid @org.springframework.web.bind.annotation.RequestBody OrderCreateRequestDto orderCreateRequestDto
    );

    @Operation(
            summary = "주문 상태 변경",
            description = "주문의 상태를 변경합니다. 판매자만 자신의 상품에 대한 주문 상태를 변경할 수 있습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @Parameter(
            name = "orderId",
            description = "상태를 변경할 주문의 ID",
            required = true,
            example = "1"
    )
    @RequestBody(
            description = "변경할 주문 상태 정보",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = OrderStatusUpdateRequestDto.class),
                    examples = {
                            @ExampleObject(
                                    name = "주문 확인",
                                    summary = "주문을 확인 상태로 변경",
                                    value = """
                                    {
                                        "newStatus": "CONFIRMED"
                                    }
                                    """
                            ),
                            @ExampleObject(
                                    name = "배송 시작",
                                    summary = "상품을 배송중 상태로 변경",
                                    value = """
                                    {
                                        "newStatus": "SHIPPED"
                                    }
                                    """
                            ),
                            @ExampleObject(
                                    name = "배송 완료",
                                    summary = "배송을 완료 상태로 변경",
                                    value = """
                                    {
                                        "newStatus": "DELIVERED"
                                    }
                                    """
                            ),
                            @ExampleObject(
                                    name = "주문 취소",
                                    summary = "주문을 취소 상태로 변경",
                                    value = """
                                    {
                                        "newStatus": "CANCELED"
                                    }
                                    """
                            )
                    }
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 상태 변경 성공",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                    {
                                        "success": true,
                                        "code": "OK",
                                        "message": "상태 변경 완료",
                                        "data": {
                                            "orderItems": [
                                                {
                                                    "cartItemId": 1,
                                                    "quantity": 2
                                                }
                                            ],
                                            "totalPrice": 2400000,
                                            "orderStatus": "CONFIRMED"
                                        },
                                        "timeStamp": "2025-08-27T12:18:21.5722917"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "주문 접근 권한 없음",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "권한 없음",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "FORBIDDEN_ORDER",
                                        "message": "해당 주문에 대한 권한이 없습니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "주문을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "주문 미존재",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "ORDER_NOT_FOUND",
                                        "message": "주문이 존재하지 않습니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "허용되지 않는 상태 변경",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "잘못된 상태 변경",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "INVALID_STATE_TRANSITION",
                                        "message": "허용되지 않는 주문 상태 변경입니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            )
    })
    @PatchMapping("/{orderId}")
    ResponseEntity<ServiceResult<OrderResponseDto>> changeOrderStatus(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @PathVariable Long orderId,
            @Valid @org.springframework.web.bind.annotation.RequestBody OrderStatusUpdateRequestDto orderStatusUpdateRequestDto
    );

    @Operation(
            summary = "사용자 주문 목록 조회",
            description = "사용자의 주문 목록을 페이징하여 조회합니다. 주문 ID 내림차순으로 정렬됩니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @Parameter(
            name = "pageable",
            description = "페이징 정보 (page, size, sort)",
            example = "page=0&size=20&sort=id,desc"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 목록 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                    {
                                        "success": true,
                                        "code": "OK",
                                        "message": "주문 조회 완료",
                                        "data": {
                                            "content": [
                                                {
                                                    "orderId": 3,
                                                    "representativeProductName": "iPhone 15 Pro 외 1개",
                                                    "totalPrice": 2489000,
                                                    "orderStatus": "SHIPPED"
                                                },
                                                {
                                                    "orderId": 2,
                                                    "representativeProductName": "가을 니트 스웨터",
                                                    "totalPrice": 89000,
                                                    "orderStatus": "DELIVERED"
                                                },
                                                {
                                                    "orderId": 1,
                                                    "representativeProductName": "무선 이어폰 외 2개",
                                                    "totalPrice": 450000,
                                                    "orderStatus": "CANCELED"
                                                }
                                            ],
                                            "pageable": {
                                                "pageNumber": 0,
                                                "pageSize": 20
                                            },
                                            "totalElements": 3,
                                            "totalPages": 1,
                                            "first": true,
                                            "last": true
                                        },
                                        "timeStamp": "2025-08-27T12:18:21.5722917"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "AUTHENTICATION_REQUIRED",
                                        "message": "인증이 필요한 서비스입니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping
    ResponseEntity<ServiceResult<CustomPageResponse<OrderSimpleResponseDto>>> findAllOrder(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            Pageable pageable
    );

    @Operation(
            summary = "주문 상세 조회",
            description = "특정 주문의 상세 정보를 조회합니다. 본인의 주문만 조회할 수 있습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @Parameter(
            name = "orderId",
            description = "조회할 주문의 ID",
            required = true,
            example = "1"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 상세 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                    {
                                        "success": true,
                                        "code": "OK",
                                        "message": "주문 상세 조회 완료",
                                        "data": {
                                            "orderItems": [
                                                {
                                                    "cartItemId": 1,
                                                    "quantity": 2
                                                },
                                                {
                                                    "cartItemId": 3,
                                                    "quantity": 1
                                                }
                                            ],
                                            "totalPrice": 2489000,
                                            "orderStatus": "PROCESSING"
                                        },
                                        "timeStamp": "2025-08-27T12:18:21.5722917"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "주문 접근 권한 없음",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "권한 없음",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "FORBIDDEN_ORDER",
                                        "message": "해당 주문에 대한 권한이 없습니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "주문을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "주문 미존재",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "ORDER_NOT_FOUND",
                                        "message": "주문이 존재하지 않습니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/{orderId}")
    ResponseEntity<ServiceResult<OrderResponseDto>> findOrder(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @PathVariable Long orderId
    );
}