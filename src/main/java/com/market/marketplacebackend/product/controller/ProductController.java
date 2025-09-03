package com.market.marketplacebackend.product.controller;

import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.product.domain.Product;
import com.market.marketplacebackend.product.dto.ProductCreateRequestDto;
import com.market.marketplacebackend.product.dto.ProductDetailResponseDto;
import com.market.marketplacebackend.product.dto.ProductResponseDto;
import com.market.marketplacebackend.product.dto.ProductUpdateRequestDto;
import com.market.marketplacebackend.product.service.ProductService;
import com.market.marketplacebackend.security.domain.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController implements ProductSwagger {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ServiceResult<ProductDetailResponseDto>> createProduct( @Valid @RequestPart("requestDto") ProductCreateRequestDto productCreateRequestDto,
                                                                                  @AuthenticationPrincipal PrincipalDetails userDetails,
                                                                                  @RequestPart("image") MultipartFile image
    ) throws IOException {
        Product serviceResult = productService.createProduct(userDetails.getAccount().getId(), productCreateRequestDto, image);

        ProductDetailResponseDto responseDto = ProductDetailResponseDto.fromEntity(serviceResult);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.getId())
                .toUri();
        ServiceResult<ProductDetailResponseDto> finalResult = ServiceResult.success("상품 등록 완료", responseDto);
        return ResponseEntity.created(location).body(finalResult);
    }

    @PatchMapping(value = "/update/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ServiceResult<ProductDetailResponseDto>> updateProduct(@PathVariable Long productId,
                                                                                 @RequestPart("requestDto") ProductUpdateRequestDto productUpdateRequestDto,
                                                                                 @RequestPart(value = "image", required = false) MultipartFile image,
                                                                                 @AuthenticationPrincipal PrincipalDetails userDetails
    ) throws IOException {
        Product serviceResult = productService.updateProduct(userDetails.getAccount().getId(), productId, productUpdateRequestDto, image);

        ProductDetailResponseDto responseDto = ProductDetailResponseDto.fromEntity(serviceResult);

        ServiceResult<ProductDetailResponseDto> finalResult = ServiceResult.success("상품 수정 완료", responseDto);
        return ResponseEntity.ok(finalResult);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@AuthenticationPrincipal PrincipalDetails userDetails, @PathVariable Long productId){
        productService.deleteProduct(userDetails.getAccount().getId(), productId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ServiceResult<Page<ProductResponseDto>>> findAllProducts(
            @RequestParam(required = false) String category,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ){
        Page<Product> serviceResult = productService.findAllProducts(category, pageable);

        Page<ProductResponseDto> responseDto = serviceResult
                .map(ProductResponseDto::fromEntity);

        ServiceResult<Page<ProductResponseDto>> finalResult = ServiceResult.success("상품 전체 조회 완료", responseDto);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ServiceResult<ProductDetailResponseDto>> findDetailProducts(
            @PathVariable Long productId
    ){
        Product serviceResult = productService.findDetailProducts(productId);

        ProductDetailResponseDto productDetailResponseDto = ProductDetailResponseDto.fromEntity(serviceResult);
        ServiceResult<ProductDetailResponseDto> finalResult = ServiceResult.success("상품 상세 조회 완료", productDetailResponseDto);
        return ResponseEntity.ok(finalResult);
    }
}
