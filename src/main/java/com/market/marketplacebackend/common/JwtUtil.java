package com.market.marketplacebackend.common;

import com.market.marketplacebackend.common.enums.TokenType;
import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일

    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}")String secret) {
        //key 객체 생성
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createAccessToken(String email, String role) {
        return createJwt(TokenType.ACCESS.getValue(), email, role, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(String email, String role) {
        return createJwt(TokenType.REFRESH.getValue(), email, role, REFRESH_TOKEN_EXPIRE_TIME);
    }

    public String createJwt(String category, String email, String role, Long expiredMs){
        return Jwts.builder()
                .claim("category", category)
                .claim("email",email)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public Claims validateAndParse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey) // <-- 이 부분이 반드시 필요합니다!
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.EXPIRED_ACCESS_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    public boolean isAccessToken(Claims claims) {
        String category = claims.get("category", String.class);
        return "access".equalsIgnoreCase(category);
    }

    public boolean isRefreshToken(Claims claims) {
        String category = claims.get("category", String.class);
        return "refresh".equalsIgnoreCase(category);
    }
}
