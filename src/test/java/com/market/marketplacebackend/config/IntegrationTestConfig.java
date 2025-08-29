package com.market.marketplacebackend.config;

import com.market.marketplacebackend.cart.service.CartService;
import com.market.marketplacebackend.common.CookieUtil;
import com.market.marketplacebackend.common.JwtUtil;
import com.market.marketplacebackend.security.service.AuthService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@TestConfiguration
@Profile("test")
public class IntegrationTestConfig {
    @Bean
    public CartService cartService() {
        return Mockito.mock(CartService.class);
    }
    @Bean
    public JwtUtil jwtUtil() {
        return Mockito.mock(JwtUtil.class);
    }

    @Bean
    public AuthService authService() {
        return Mockito.mock(AuthService.class);
    }

    @Bean
    public CookieUtil cookieUtil() {
        return Mockito.mock(CookieUtil.class);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}