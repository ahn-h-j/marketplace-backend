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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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

    @GetMapping()
    public ResponseEntity<ServiceResult<List<ProductResponseDto>>> findAllProducts(){
        List<Product> serviceResult = productService.findAllProducts();

        List<ProductResponseDto> responseDto = serviceResult.stream()
                .map(ProductResponseDto::fromEntity)
                .toList();

        ServiceResult<List<ProductResponseDto>> finalResult = ServiceResult.success("상품 전체 조회 완료", responseDto);
        return ResponseEntity.ok(finalResult);
    }
}
