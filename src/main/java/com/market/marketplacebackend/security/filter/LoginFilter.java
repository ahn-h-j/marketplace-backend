package com.market.marketplacebackend.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.dto.LoginDto;
import com.market.marketplacebackend.common.CookieUtil;
import com.market.marketplacebackend.common.JwtUtil;
import com.market.marketplacebackend.security.domain.PrincipalDetails;
import com.market.marketplacebackend.security.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final CookieUtil cookieUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, AuthService authService, CookieUtil cookieUtil) {
        super(authenticationManager);
        this.setRequiresAuthenticationRequestMatcher(new RegexRequestMatcher("/login", "POST"));

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.cookieUtil = cookieUtil;
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
        Long id = account.getId();

        String accessToken = jwtUtil.createAccessToken(id, email, role);
        String refreshToken = jwtUtil.createRefreshToken(id, email, role);

        authService.saveRefreshToken(account, refreshToken);

        response.addHeader("Set-Cookie", cookieUtil.createAccessCookie(accessToken).toString());
        response.addHeader("Set-Cookie", cookieUtil.createRefreshCookie(refreshToken).toString());
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed){
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
