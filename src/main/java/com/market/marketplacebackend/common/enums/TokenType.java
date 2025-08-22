package com.market.marketplacebackend.common.enums;

import lombok.Getter;

@Getter
public enum TokenType {
    ACCESS("ACCESS"),
    REFRESH("REFRESH");

    private final String value;
    TokenType(String value) { this.value = value; }
}
