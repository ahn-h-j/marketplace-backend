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
public class ProductResponseDto {
    private String name;
    private Integer price;
    private Category category;
    private String sellerName;

    public static ProductResponseDto fromEntity(Product product) {
        return ProductResponseDto.builder()
                .name(product.getName())
                .price(product.getPrice())
                .category(product.getCategory())
                .sellerName(product.getAccount().getName()) // 필요한 정보만 가공
                .build();
    }
}
