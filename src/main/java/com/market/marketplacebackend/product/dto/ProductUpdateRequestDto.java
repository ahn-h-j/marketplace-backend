package com.market.marketplacebackend.product.dto;

import com.market.marketplacebackend.common.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequestDto {

    private String name;
    private Integer price;
    private String description;
    private Integer stock;
    private Category category;
}