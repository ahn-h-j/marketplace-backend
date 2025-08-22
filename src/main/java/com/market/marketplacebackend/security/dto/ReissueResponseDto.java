package com.market.marketplacebackend.security.dto;

import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReissueResponseDto {
        String newAccessToken;
        Cookie refreshCookie;
}
