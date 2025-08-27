package com.market.marketplacebackend.product.controller;

import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.product.dto.ProductCreateRequestDto;
import com.market.marketplacebackend.product.dto.ProductDetailResponseDto;
import com.market.marketplacebackend.product.dto.ProductResponseDto;
import com.market.marketplacebackend.product.dto.ProductUpdateRequestDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product", description = "상품 관리 API")
@RequestMapping("/product")
public interface ProductSwagger {

    @Operation(
            summary = "상품 등록",
            description = "새로운 상품을 등록합니다. 판매자 권한이 필요합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @RequestBody(
            description = "등록할 상품 정보",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = ProductCreateRequestDto.class),
                    examples = {
                            @ExampleObject(
                                    name = "전자기기 상품 등록",
                                    summary = "스마트폰 등록 예시",
                                    value = """
                                    {
                                        "name": "iPhone 15 Pro",
                                        "price": 1200000,
                                        "description": "최신 스마트폰입니다. A17 Pro 칩셋 탑재",
                                        "stock": 50,
                                        "category": "ELECTRONICS"
                                    }
                                    """
                            ),
                            @ExampleObject(
                                    name = "패션 상품 등록",
                                    summary = "의류 등록 예시",
                                    value = """
                                    {
                                        "name": "가을 니트 스웨터",
                                        "price": 89000,
                                        "description": "부드러운 울 소재의 가을 니트입니다.",
                                        "stock": 30,
                                        "category": "FASHION"
                                    }
                                    """
                            )
                    }
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "상품 등록 성공",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                    {
                                        "success": true,
                                        "code": "OK",
                                        "message": "상품 등록 완료",
                                        "data": {
                                            "id": 1,
                                            "name": "iPhone 15 Pro",
                                            "price": 1200000,
                                            "description": "최신 스마트폰입니다. A17 Pro 칩셋 탑재",
                                            "stock": 50,
                                            "category": "ELECTRONICS",
                                            "sellerName": "김판매자"
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
                            examples = @ExampleObject(
                                    name = "유효성 검증 실패",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "VALIDATION_ERROR",
                                        "message": "입력값이 유효하지 않습니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
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
                    responseCode = "403",
                    description = "판매자 권한 없음",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "권한 없음",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "FORBIDDEN_NOT_SELLER",
                                        "message": "셀러 권한이 없는 계정입니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            )
    })
    @PostMapping
    ResponseEntity<ServiceResult<ProductDetailResponseDto>> createProduct(
            @Valid @org.springframework.web.bind.annotation.RequestBody ProductCreateRequestDto productCreateRequestDto,
            @AuthenticationPrincipal PrincipalDetails userDetails
    );

    @Operation(
            summary = "상품 수정",
            description = "기존 상품 정보를 수정합니다. 해당 상품의 판매자만 수정할 수 있습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @Parameter(
            name = "productId",
            description = "수정할 상품의 ID",
            required = true,
            example = "1"
    )
    @RequestBody(
            description = "수정할 상품 정보 (변경하고 싶은 필드만 포함)",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = ProductUpdateRequestDto.class),
                    examples = {
                            @ExampleObject(
                                    name = "가격과 재고 수정",
                                    summary = "가격과 재고만 변경하는 경우",
                                    value = """
                                    {
                                        "price": 1100000,
                                        "stock": 45
                                    }
                                    """
                            ),
                            @ExampleObject(
                                    name = "전체 정보 수정",
                                    summary = "모든 필드를 변경하는 경우",
                                    value = """
                                    {
                                        "name": "iPhone 15 Pro Max",
                                        "price": 1400000,
                                        "description": "더 큰 화면의 최신 스마트폰입니다.",
                                        "stock": 35,
                                        "category": "ELECTRONICS"
                                    }
                                    """
                            )
                    }
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "상품 수정 성공",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                    {
                                        "success": true,
                                        "code": "OK",
                                        "message": "상품 수정 완료",
                                        "data": {
                                            "id": 1,
                                            "name": "iPhone 15 Pro Max",
                                            "price": 1400000,
                                            "description": "더 큰 화면의 최신 스마트폰입니다.",
                                            "stock": 35,
                                            "category": "ELECTRONICS",
                                            "sellerName": "김판매자"
                                        },
                                        "timeStamp": "2025-08-27T12:18:21.5722917"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "상품 수정 권한 없음",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "권한 없음",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "FORBIDDEN_PRODUCT",
                                        "message": "해당 상품에 대한 권한이 없습니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
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
            )
    })
    @PatchMapping("/{productId}")
    ResponseEntity<ServiceResult<ProductDetailResponseDto>> updateProduct(
            @Valid @org.springframework.web.bind.annotation.RequestBody ProductUpdateRequestDto productUpdateRequestDto,
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @PathVariable Long productId
    );

    @Operation(
            summary = "상품 삭제",
            description = "상품을 삭제합니다. 해당 상품의 판매자만 삭제할 수 있습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @Parameter(
            name = "productId",
            description = "삭제할 상품의 ID",
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
                    description = "상품 삭제 권한 없음",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "권한 없음",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "FORBIDDEN_PRODUCT",
                                        "message": "해당 상품에 대한 권한이 없습니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
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
            )
    })
    @DeleteMapping("/{productId}")
    ResponseEntity<?> deleteProduct(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @PathVariable Long productId
    );

    @Operation(
            summary = "상품 목록 조회",
            description = "전체 상품 목록을 페이징하여 조회합니다. 카테고리로 필터링할 수 있습니다."
    )
    @Parameter(
            name = "category",
            description = "필터링할 카테고리 (ELECTRONICS, BOOKS, FASHION, BEAUTY, HOME_GOODS, SPORTS, FOOD)",
            required = false,
            example = "ELECTRONICS"
    )
    @Parameter(
            name = "pageable",
            description = "페이징 정보 (page, size, sort)",
            example = "page=0&size=20&sort=id,asc"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "상품 목록 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                    {
                                        "success": true,
                                        "code": "OK",
                                        "message": "상품 전체 조회 완료",
                                        "data": {
                                            "content": [
                                                {
                                                    "name": "iPhone 15 Pro",
                                                    "price": 1200000,
                                                    "category": "ELECTRONICS",
                                                    "sellerName": "김판매자"
                                                },
                                                {
                                                    "name": "가을 니트 스웨터",
                                                    "price": 89000,
                                                    "category": "FASHION",
                                                    "sellerName": "이판매자"
                                                }
                                            ],
                                            "pageable": {
                                                "pageNumber": 0,
                                                "pageSize": 20
                                            },
                                            "totalElements": 2,
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
                    responseCode = "404",
                    description = "존재하지 않는 카테고리",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "카테고리 미존재",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "CATEGORY_NOT_FOUND",
                                        "message": "존재하지 않는 카테고리입니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping
    ResponseEntity<ServiceResult<Page<ProductResponseDto>>> findAllProducts(
            @RequestParam(required = false) String category,
            Pageable pageable
    );

    @Operation(
            summary = "상품 상세 조회",
            description = "특정 상품의 상세 정보를 조회합니다."
    )
    @Parameter(
            name = "productId",
            description = "조회할 상품의 ID",
            required = true,
            example = "1"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "상품 상세 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                    {
                                        "success": true,
                                        "code": "OK",
                                        "message": "상품 상세 조회 완료",
                                        "data": {
                                            "id": 1,
                                            "name": "iPhone 15 Pro",
                                            "price": 1200000,
                                            "description": "최신 스마트폰입니다. A17 Pro 칩셋 탑재",
                                            "stock": 50,
                                            "category": "ELECTRONICS",
                                            "sellerName": "김판매자"
                                        },
                                        "timeStamp": "2025-08-27T12:18:21.5722917"
                                    }
                                    """
                            )
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
            )
    })
    @GetMapping("/{productId}")
    ResponseEntity<ServiceResult<ProductDetailResponseDto>> findDetailProducts(
            @PathVariable Long productId
    );
}