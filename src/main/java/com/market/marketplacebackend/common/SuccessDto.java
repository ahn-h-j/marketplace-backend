package com.market.marketplacebackend.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessDto {
    private String message;
    private Object data;
}
