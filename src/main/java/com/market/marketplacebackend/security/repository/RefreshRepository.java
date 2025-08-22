package com.market.marketplacebackend.security.repository;

import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.security.domain.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {
    Boolean existsByRefreshToken(String refresh);

    Optional<Refresh> findByAccount(Account account);
}
