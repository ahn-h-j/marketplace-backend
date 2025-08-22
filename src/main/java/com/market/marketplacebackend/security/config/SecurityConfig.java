package com.market.marketplacebackend.security.config;

import com.market.marketplacebackend.security.filter.JwtAuthenticationFilter;
import com.market.marketplacebackend.security.filter.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, LoginFilter loginFilter
            , JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login","/user/signup", "/reissue").permitAll()
                        .requestMatchers(HttpMethod.GET, "/product", "/product/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/product").hasRole("SELLER")
                        .requestMatchers(HttpMethod.PATCH, "/product/{productId}").hasRole("SELLER")
                        .requestMatchers(HttpMethod.DELETE, "/product/{productId}").hasRole("SELLER")
                        .requestMatchers("/user/**", "/cart/**", "/order/**").hasAnyRole("SELLER", "BUYER")

                        .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
