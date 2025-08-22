package com.market.marketplacebackend.account.service;

import com.market.marketplacebackend.cart.service.CartService;
import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.dto.SignUpDto;
import com.market.marketplacebackend.account.repository.AccountRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CartService cartService;
    private final BCryptPasswordEncoder passwordEncoder;

    public Account join(SignUpDto signUpDto) {
        if(accountRepository.existsByEmail(signUpDto.getEmail())){
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATE);
        }
        String password = passwordEncoder.encode(signUpDto.getPassword());
        Account account = signUpDto.toEntity(password);
        Account savedAccount = accountRepository.save(account);
        cartService.createCartForAccount(savedAccount);

        return savedAccount;
    }
}
