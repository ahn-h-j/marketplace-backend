package com.market.marketplacebackend.product.controller;

import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.product.domain.Product;
import com.market.marketplacebackend.product.dto.ProductCreateRequestDto;
import com.market.marketplacebackend.product.dto.ProductDetailResponseDto;
import com.market.marketplacebackend.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}
