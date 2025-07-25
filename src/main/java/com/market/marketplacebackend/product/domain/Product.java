package com.market.marketplacebackend.product.domain;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.common.enums.Category;
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
    @Enumerated(EnumType.STRING) // <- 이 어노테이션을 추가
    private Category category;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
