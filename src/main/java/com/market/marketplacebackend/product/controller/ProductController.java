package com.market.marketplacebackend.product.controller;

import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.product.domain.Product;
import com.market.marketplacebackend.product.dto.ProductCreateRequestDto;
import com.market.marketplacebackend.product.dto.ProductDetailResponseDto;
import com.market.marketplacebackend.product.dto.ProductResponseDto;
import com.market.marketplacebackend.product.dto.ProductUpdateRequestDto;
import com.market.marketplacebackend.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ServiceResult<ProductDetailResponseDto>> createProduct(@Valid @RequestBody ProductCreateRequestDto productCreateRequestDto,
                                                                                 @RequestParam Long accountId
    ){
        Product serviceResult = productService.createProduct(accountId, productCreateRequestDto);

        ProductDetailResponseDto responseDto = ProductDetailResponseDto.fromEntity(serviceResult);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.getId())
                .toUri();

        ServiceResult<ProductDetailResponseDto> finalResult = ServiceResult.success("상품 등록 완료", responseDto);
        return ResponseEntity.created(location).body(finalResult);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ServiceResult<ProductDetailResponseDto>> updateProduct(@Valid @RequestBody ProductUpdateRequestDto productUpdateRequestDto,
                                                                                 @RequestParam Long accountId,
                                                                                 @PathVariable Long productId
    ){
        Product serviceResult = productService.updateProduct(accountId, productId, productUpdateRequestDto);

        ProductDetailResponseDto responseDto = ProductDetailResponseDto.fromEntity(serviceResult);

        ServiceResult<ProductDetailResponseDto> finalResult = ServiceResult.success("상품 수정 완료", responseDto);
        return ResponseEntity.ok(finalResult);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@RequestParam Long accountId, @PathVariable Long productId){
        productService.deleteProduct(accountId, productId);

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
}
