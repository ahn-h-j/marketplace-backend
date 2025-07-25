package com.market.marketplacebackend.common.enums;

import lombok.Getter;

@Getter
public enum AccountRole {

    CUSTOMER("customer"),
    SELLER("seller");

    private final String role;

    AccountRole(String role) {
        this.role = role;
    }
}
