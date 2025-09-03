package com.market.marketplacebackend.product.dto;

import com.market.marketplacebackend.common.enums.Category;
import com.market.marketplacebackend.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponseDto {
    private Long id;
    private String name;
    private Integer price;
    private String description;
    private Integer stock;
    private Category category;
    private String sellerName;
    private String imageUrl;

    public static ProductDetailResponseDto fromEntity(Product product) {
        return ProductDetailResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .stock(product.getStock())
                .category(product.getCategory())
                .sellerName(product.getAccount().getName())
                .imageUrl(product.getImageUrl())
                .build();
    }
}