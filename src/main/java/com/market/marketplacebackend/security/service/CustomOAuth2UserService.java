package com.market.marketplacebackend.security.service;


import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.repository.AccountRepository;
import com.market.marketplacebackend.common.enums.AccountRole;
import com.market.marketplacebackend.common.enums.ProviderType;
import com.market.marketplacebackend.security.domain.PrincipalDetails;
import com.market.marketplacebackend.security.provider.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final AccountRepository accountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2UserInfo oAuth2UserInfo = ProviderType.from(registrationId).createUserInfo(attributes);

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String name = oAuth2UserInfo.getName();
        String email = oAuth2UserInfo.getEmail();

        Optional<Account> account = accountRepository.findByEmail(email);

        Account userAccount = account.orElseGet(() -> accountRepository.save(
                Account.builder()
                        .name(name)
                        .email(email)
                        .accountRole(AccountRole.BUYER)
                        .provider(provider)
                        .providerId(providerId)
                        .build()
        ));
        return new PrincipalDetails(userAccount, attributes);

    }
}

