package com.market.marketplacebackend.account.controller;

import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.dto.LoginDto;
import com.market.marketplacebackend.account.dto.SignUpDto;
import com.market.marketplacebackend.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<ServiceResult<Account>> signUp(@Valid @RequestBody SignUpDto signUpDto){
        ServiceResult<Account> result = accountService.join(signUpDto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<ServiceResult<Account>> login(@Valid @RequestBody LoginDto loginDto){
        ServiceResult<Account> result = accountService.login(loginDto);
        return ResponseEntity.ok(result);
    }
}
