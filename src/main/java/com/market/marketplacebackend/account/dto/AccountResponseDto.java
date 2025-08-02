package com.market.marketplacebackend.account.dto;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.common.enums.AccountRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {
    private String name;
    private String email;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private AccountRole accountRole;

    public static AccountResponseDto fromEntity(Account account) {
        return AccountResponseDto.builder()
                .name(account.getName())
                .email(account.getEmail())
                .phoneNumber(account.getPhoneNumber())
                .accountRole(account.getAccountRole())
                .build();
    }
}
