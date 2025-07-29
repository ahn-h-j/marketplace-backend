package com.market.marketplacebackend.product.domain;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.common.enums.Category;
import com.market.marketplacebackend.product.dto.ProductUpdateRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer price;
    private String description;
    private Integer stock;
    @Enumerated(EnumType.STRING)
    private Category category;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public void updateIfChanged(ProductUpdateRequestDto dto) {
        if (dto.getName() != null && !this.name.equals(dto.getName())) {
            this.name = dto.getName();
        }
        if (dto.getPrice() != null && !this.price.equals(dto.getPrice())) {
            this.price = dto.getPrice();
        }
        if (dto.getDescription() != null && !this.description.equals(dto.getDescription())) {
            this.description = dto.getDescription();
        }
        if (dto.getStock() != null && !this.stock.equals(dto.getStock())) {
            this.stock = dto.getStock();
        }
        if (dto.getCategory() != null && !this.category.equals(dto.getCategory())) {
            this.category = dto.getCategory();
        }
    }
}
