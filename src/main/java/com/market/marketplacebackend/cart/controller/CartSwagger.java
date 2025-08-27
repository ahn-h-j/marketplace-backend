package com.market.marketplacebackend.cart.controller;

import com.market.marketplacebackend.cart.dto.CartItemAddRequestDto;
import com.market.marketplacebackend.cart.dto.CartItemResponseDto;
import com.market.marketplacebackend.cart.dto.CartItemUpdateDto;
import com.market.marketplacebackend.cart.dto.CartResponseDto;
import com.market.marketplacebackend.common.ServiceResult;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart", description = "장바구니 관리 API")
@RequestMapping("/cart")
public interface CartSwagger {

    @Operation(
            summary = "장바구니 조회",
            description = "사용자의 장바구니 정보와 담긴 상품들을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "장바구니 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                    {
                                        "success": true,
                                        "code": "OK",
                                        "message": "카트 조회 완료",
                                        "data": {
                                            "items": [
                                                {
                                                    "productId": 1,
                                                    "productName": "iPhone 15 Pro",
                                                    "productPrice": 1200000,
                                                    "quantity": 2,
                                                    "totalPricePerItem": 2400000
                                                },
                                                {
                                                    "productId": 2,
                                                    "productName": "가을 니트 스웨터",
                                                    "productPrice": 89000,
                                                    "quantity": 1,
                                                    "totalPricePerItem": 89000
                                                }
                                            ],
                                            "totalPrice": 2489000
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
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "장바구니를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "장바구니 미존재",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "CART_NOT_FOUND",
                                        "message": "카트가 존재하지 않습니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping
    ResponseEntity<ServiceResult<CartResponseDto>> getCart(@AuthenticationPrincipal PrincipalDetails userDetails);

    @Operation(
            summary = "장바구니에 상품 추가",
            description = "선택한 상품을 지정된 수량만큼 장바구니에 추가합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @RequestBody(
            description = "장바구니에 추가할 상품 정보",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = CartItemAddRequestDto.class),
                    examples = {
                            @ExampleObject(
                                    name = "상품 추가 예시",
                                    summary = "스마트폰을 2개 추가하는 경우",
                                    value = """
                                    {
                                        "productId": 1,
                                        "quantity": 2
                                    }
                                    """
                            ),
                            @ExampleObject(
                                    name = "다른 상품 추가 예시",
                                    summary = "의류를 1개 추가하는 경우",
                                    value = """
                                    {
                                        "productId": 5,
                                        "quantity": 1
                                    }
                                    """
                            )
                    }
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "상품 추가 성공",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                    {
                                        "success": true,
                                        "code": "OK",
                                        "message": "장바구니에 상품 추가 완료",
                                        "data": {
                                            "productId": 1,
                                            "productName": "iPhone 15 Pro",
                                            "productPrice": 1200000,
                                            "quantity": 2,
                                            "totalPricePerItem": 2400000
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
                    responseCode = "404",
                    description = "상품을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "상품 미존재",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "PRODUCT_NOT_FOUND",
                                        "message": "존재하지 않는 상품입니다.",
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
    @PostMapping("/items")
    ResponseEntity<ServiceResult<CartItemResponseDto>> addItemToCart(
            @Valid @org.springframework.web.bind.annotation.RequestBody CartItemAddRequestDto cartItemAddRequestDto,
            @AuthenticationPrincipal PrincipalDetails userDetails
    );

    @Operation(
            summary = "장바구니 상품 수량 수정",
            description = "장바구니에 담긴 상품의 수량을 수정합니다. 수량이 0이면 해당 상품이 삭제됩니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @RequestBody(
            description = "수정할 장바구니 상품 정보",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = CartItemUpdateDto.class),
                    examples = {
                            @ExampleObject(
                                    name = "수량 변경",
                                    summary = "수량을 3개로 변경하는 경우",
                                    value = """
                                    {
                                        "cartItemId": 1,
                                        "quantity": 3
                                    }
                                    """
                            ),
                            @ExampleObject(
                                    name = "상품 삭제",
                                    summary = "수량을 0으로 설정하여 상품을 삭제하는 경우",
                                    value = """
                                    {
                                        "cartItemId": 1,
                                        "quantity": 0
                                    }
                                    """
                            )
                    }
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수량 수정 성공",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = {
                                    @ExampleObject(
                                            name = "수정 성공",
                                            summary = "수량이 변경된 경우",
                                            value = """
                                            {
                                                "success": true,
                                                "code": "OK",
                                                "message": "장바구니에 상품 수정 완료",
                                                "data": {
                                                    "productId": 1,
                                                    "productName": "iPhone 15 Pro",
                                                    "productPrice": 1200000,
                                                    "quantity": 3,
                                                    "totalPricePerItem": 3600000
                                                },
                                                "timeStamp": "2025-08-27T12:18:21.5722917"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "상품 삭제 성공",
                                            summary = "수량이 0으로 설정되어 삭제된 경우",
                                            value = """
                                            {
                                                "success": true,
                                                "code": "OK",
                                                "message": "장바구니에서 상품이 삭제되었습니다.",
                                                "data": null,
                                                "timeStamp": "2025-08-27T12:18:21.5722917"
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "장바구니 접근 권한 없음",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "권한 없음",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "FORBIDDEN_CART",
                                        "message": "해당 카트에 대한 권한이 없습니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "장바구니 상품을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "상품 미존재",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "PRODUCT_NOT_IN_CART",
                                        "message": "카트에 해당 상품이 존재하지 않습니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            )
    })
    @PatchMapping("/items")
    ResponseEntity<ServiceResult<CartItemResponseDto>> updateItem(
            @Valid @org.springframework.web.bind.annotation.RequestBody CartItemUpdateDto cartItemUpdateDto,
            @AuthenticationPrincipal PrincipalDetails userDetails
    );

    @Operation(
            summary = "장바구니 상품 개별 삭제",
            description = "장바구니에서 특정 상품을 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @Parameter(
            name = "cartItemId",
            description = "삭제할 장바구니 상품의 ID",
            required = true,
            example = "1"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "상품 삭제 성공 (응답 본문 없음)"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "장바구니 접근 권한 없음",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "권한 없음",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "FORBIDDEN_CART",
                                        "message": "해당 카트에 대한 권한이 없습니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "장바구니 상품을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "상품 미존재",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "PRODUCT_NOT_IN_CART",
                                        "message": "카트에 해당 상품이 존재하지 않습니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            )
    })
    @DeleteMapping("/items/{cartItemId}")
    ResponseEntity<Void> deleteItem(
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    );

    @Operation(
            summary = "장바구니 전체 비우기",
            description = "사용자의 장바구니에 담긴 모든 상품을 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "장바구니 전체 삭제 성공 (응답 본문 없음)"
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
                    description = "장바구니를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "장바구니 미존재",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "CART_NOT_FOUND",
                                        "message": "카트가 존재하지 않습니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            )
    })
    @DeleteMapping("/items")
    ResponseEntity<Void> deleteAllCartItems(@AuthenticationPrincipal PrincipalDetails userDetails);
}