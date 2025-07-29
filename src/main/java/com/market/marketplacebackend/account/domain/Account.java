package com.market.marketplacebackend.account.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.market.marketplacebackend.common.enums.AccountRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    @JsonIgnore
    private String password;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private AccountRole accountRole;
}
