package com.market.marketplacebackend.security.domain;

import com.market.marketplacebackend.account.domain.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Refresh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiration;

    public void updateToken(String newToken, LocalDateTime newExpiration) {
        this.refreshToken = newToken;
        this.expiration = newExpiration;
    }
}
