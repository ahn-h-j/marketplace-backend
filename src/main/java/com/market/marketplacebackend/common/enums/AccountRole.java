package com.market.marketplacebackend.common.enums;

import lombok.Getter;

@Getter
public enum AccountRole {

    BUYER("BUYER"),
    SELLER("SELLER");

    private final String role;

    AccountRole(String role) {
        this.role = role;
    }
}
