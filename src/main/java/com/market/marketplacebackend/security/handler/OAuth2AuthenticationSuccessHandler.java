package com.market.marketplacebackend.security.handler;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.common.CookieUtil;
import com.market.marketplacebackend.common.JwtUtil;
import com.market.marketplacebackend.security.domain.PrincipalDetails;
import com.market.marketplacebackend.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final RequestCache requestCache;
    @Value("${app.oauth2.redirect-uri:http://localhost:3000/}")
    private String defaultRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        Account account = principalDetails.getAccount();
        String email = account.getEmail();
        String role = account.getAccountRole().name();

        String accessToken = jwtUtil.createAccessToken(email, role);
        String refreshToken = jwtUtil.createRefreshToken(email, role);

        authService.saveRefreshToken(account, refreshToken);

        response.addHeader("Set-Cookie", cookieUtil.createAccessCookie(accessToken).toString());
        response.addHeader("Set-Cookie", cookieUtil.createRefreshCookie(refreshToken).toString());

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        String targetUrl;
        if (savedRequest != null) {
            targetUrl = savedRequest.getRedirectUrl();
            requestCache.removeRequest(request, response);
        } else {
            targetUrl = defaultRedirectUri;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}