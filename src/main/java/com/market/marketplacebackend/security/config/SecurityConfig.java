package com.market.marketplacebackend.security.config;

import com.market.marketplacebackend.security.filter.JwtAuthenticationFilter;
import com.market.marketplacebackend.security.filter.LoginFilter;
import com.market.marketplacebackend.security.handler.OAuth2AuthenticationFailureHandler;
import com.market.marketplacebackend.security.handler.OAuth2AuthenticationSuccessHandler;
import com.market.marketplacebackend.security.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    @Order(0)
    public SecurityFilterChain oauth2FilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/oauth2/**", "/login/oauth2/**")
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                );
        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http, LoginFilter loginFilter, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/user/signup", "/reissue").permitAll()
                        .requestMatchers(HttpMethod.GET, "/product", "/product/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/product").hasRole("SELLER")
                        .requestMatchers(HttpMethod.PATCH, "/product/**").hasRole("SELLER")
                        .requestMatchers(HttpMethod.DELETE, "/product/**").hasRole("SELLER")
                        .requestMatchers("/user/**", "/cart/**", "/order/**").hasAnyRole("SELLER", "BUYER")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
                );
        return http.build();
    }
}