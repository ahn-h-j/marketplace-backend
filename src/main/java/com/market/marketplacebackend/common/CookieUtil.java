package com.market.marketplacebackend.common;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public static final String ACCESS_TOKEN_NAME = "ACCESS";
    public static final String REFRESH_TOKEN_NAME = "REFRESH";
    public static final String REFRESH_TOKEN_PATH = "/reissue"; // 토큰 재발급 경로

    public ResponseCookie createAccessCookie(String value){
        return ResponseCookie.from(ACCESS_TOKEN_NAME, value)
                .maxAge(JwtUtil.ACCESS_TOKEN_EXPIRE_TIME / 1000)
                .path("/")
                .secure(false)
                .httpOnly(true)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie createRefreshCookie(String value){
        return ResponseCookie.from(REFRESH_TOKEN_NAME, value)
                .maxAge(JwtUtil.REFRESH_TOKEN_EXPIRE_TIME / 1000)
                .path(REFRESH_TOKEN_PATH)
                .secure(false)
                .httpOnly(true)
                .sameSite("Strict")
                .build();
    }
}
