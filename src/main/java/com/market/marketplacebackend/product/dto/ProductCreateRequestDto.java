package com.market.marketplacebackend.product.dto;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.common.enums.Category;
import com.market.marketplacebackend.product.domain.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequestDto {
    @NotBlank(message = "이름을 입력해주세요")
    private String name;
    @NotNull(message = "가격을 입력해주세요")
    private Integer price;
    @NotBlank(message = "제품 설명을 입력해주세요")
    private String description;
    @NotNull(message = "재고수량을 입력해주세요")
    private Integer stock;
    @NotNull(message = "카테고리를 입력해주세요")
    private Category category;

    public Product toEntity(Account account){
        return Product.builder()
                .name(name)
                .price(price)
                .description(description)
                .stock(stock)
                .category(category)
                .account(account)
                .build();
    }
}
