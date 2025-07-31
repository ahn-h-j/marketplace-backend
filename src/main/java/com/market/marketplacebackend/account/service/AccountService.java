package com.market.marketplacebackend.account.service;

import com.market.marketplacebackend.cart.domain.Cart;
import com.market.marketplacebackend.cart.repository.CartRepository;
import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.dto.LoginDto;
import com.market.marketplacebackend.account.dto.SignUpDto;
import com.market.marketplacebackend.account.repository.AccountRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final HttpSession httpSession;
    private final CartRepository cartRepository;

    public ServiceResult<Account> join(SignUpDto signUpDto) {
        if(accountRepository.existsByEmail(signUpDto.getEmail())){
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATE);
        }

        Account account = signUpDto.toEntity();
        Account savedAccount = accountRepository.save(account);
        cartRepository.save(new Cart(account));

        return ServiceResult.success("회원 가입 성공", savedAccount);
    }

    public ServiceResult<Account> login(LoginDto loginDto) {
        Account account = accountRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_NOT_FOUND));

        if(!account.getPassword().equals(loginDto.getPassword())){
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        httpSession.setAttribute("UserId", account.getId());

        return ServiceResult.success("로그인 성공", account);
    }

}
