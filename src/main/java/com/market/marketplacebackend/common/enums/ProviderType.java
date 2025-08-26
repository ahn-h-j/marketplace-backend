package com.market.marketplacebackend.common.enums;

import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import com.market.marketplacebackend.security.provider.FacebookUserInfo;
import com.market.marketplacebackend.security.provider.GoogleUserInfo;
import com.market.marketplacebackend.security.provider.NaverUserInfo;
import com.market.marketplacebackend.security.provider.OAuth2UserInfo;

import java.util.Map;
import java.util.function.Function;

public enum ProviderType {
    GOOGLE("google", GoogleUserInfo::new),
    FACEBOOK("facebook", FacebookUserInfo::new),
    NAVER("naver", attributes -> new NaverUserInfo((Map<String, Object>) attributes.get("response")));

    private final String registrationId;
    private final Function<Map<String, Object>, OAuth2UserInfo> creator;

    ProviderType(String registrationId, Function<Map<String, Object>, OAuth2UserInfo> creator) {
        this.registrationId = registrationId;
        this.creator = creator;
    }

    public static ProviderType from(String registrationId){
        for (ProviderType type : ProviderType.values()) {
            if(type.registrationId.equalsIgnoreCase(registrationId)){
                return type;
            }
        }
        throw new BusinessException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
    }
    public OAuth2UserInfo createUserInfo(Map<String, Object> attributes) {
        return this.creator.apply(attributes);
    }
}
