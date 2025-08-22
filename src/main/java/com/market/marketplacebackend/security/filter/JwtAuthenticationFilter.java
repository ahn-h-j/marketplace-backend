package com.market.marketplacebackend.security.filter;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.repository.AccountRepository;
import com.market.marketplacebackend.common.JwtUtil;
import com.market.marketplacebackend.common.enums.AccountRole;
import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import com.market.marketplacebackend.security.domain.PrincipalDetails;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authHeader.substring(7);

        Claims claims = jwtUtil.validateAndParse(accessToken);
        if(!jwtUtil.isAccessToken(claims)){
            throw new BusinessException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        Account account = Account.builder()
                .email(claims.get("email", String.class))
                .accountRole(AccountRole.valueOf(claims.get("role",String.class)))
                .build();

        PrincipalDetails customUserDetails = new PrincipalDetails(account);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}