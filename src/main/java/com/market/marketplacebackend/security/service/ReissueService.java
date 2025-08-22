package com.market.marketplacebackend.security.service;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.repository.AccountRepository;
import com.market.marketplacebackend.common.JwtUtil;
import com.market.marketplacebackend.common.enums.TokenType;
import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import com.market.marketplacebackend.security.dto.ReissueResponseDto;
import com.market.marketplacebackend.security.repository.RefreshRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ReissueService {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final AccountRepository accountRepository;
    private final AuthService authService;

    public ReissueResponseDto reIssueToken(String refresh){
        if (refresh == null) {
            throw new BusinessException(ErrorCode.MISSING_REFRESH_TOKEN);
        }
        Boolean isExist = refreshRepository.existsByRefreshToken(refresh);
        if(!isExist){
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        Claims claims = jwtUtil.validateAndParse(refresh);

        if(!jwtUtil.isRefreshToken(claims)){
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String role = claims.get("role", String.class);
        String email = claims.get("email", String.class);

        String newAccessToken = jwtUtil.createAccessToken(email, role);
        String newRefreshToken = jwtUtil.createRefreshToken(email, role);

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        authService.saveRefreshToken(account, newRefreshToken);

        Cookie refreshCookie = createCookie(TokenType.REFRESH.getValue(), newRefreshToken);

        return new ReissueResponseDto(newAccessToken,refreshCookie);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key,value);
        cookie.setMaxAge((int) (JwtUtil.REFRESH_TOKEN_EXPIRE_TIME / 1000));
        cookie.setHttpOnly(true);
        return cookie;
    }
}
