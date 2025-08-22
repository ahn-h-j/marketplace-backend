package com.market.marketplacebackend.security.service;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.common.JwtUtil;
import com.market.marketplacebackend.security.domain.Refresh;
import com.market.marketplacebackend.security.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RefreshRepository refreshRepository;

    @Transactional
    public void saveRefreshToken(Account account, String refreshToken){
        LocalDateTime expiration = LocalDateTime.now().plus(JwtUtil.REFRESH_TOKEN_EXPIRE_TIME, ChronoUnit.MILLIS);

        Optional<Refresh> existingToken = refreshRepository.findByAccount(account);

        if(existingToken.isPresent()){
            Refresh refresh = existingToken.get();
            refresh.updateToken(refreshToken, expiration);
        }else{
            Refresh newRefreshToken = Refresh.builder()
                    .account(account)
                    .refreshToken(refreshToken)
                    .expiration(expiration)
                    .build();
            refreshRepository.save(newRefreshToken);
        }

    }
}
