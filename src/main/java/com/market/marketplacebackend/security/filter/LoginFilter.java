package com.market.marketplacebackend.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.dto.LoginDto;
import com.market.marketplacebackend.common.JwtUtil;
import com.market.marketplacebackend.common.enums.TokenType;
import com.market.marketplacebackend.security.domain.PrincipalDetails;
import com.market.marketplacebackend.security.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Autowired
    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            LoginDto loginRequest = mapper.readValue(request.getInputStream(), LoginDto.class);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword(),
                    null);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult){

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        Account account = principalDetails.getAccount();
        String email = account.getEmail();
        String role = account.getAccountRole().getRole();

        String accessToken = jwtUtil.createAccessToken(email, role);
        String refreshToken = jwtUtil.createRefreshToken(email, role);

        authService.saveRefreshToken(account, refreshToken);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(createCookie(TokenType.REFRESH.getValue(), refreshToken));
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed){
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    private Cookie createCookie(String key, String value){
        Cookie cookie = new Cookie(key,value);
        cookie.setMaxAge((int) (JwtUtil. REFRESH_TOKEN_EXPIRE_TIME / 1000));
        cookie.setHttpOnly(true);
        return cookie;
    }
}
