package com.market.marketplacebackend.account.controller;

import com.market.marketplacebackend.account.dto.AccountResponseDto;
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
    public ResponseEntity<ServiceResult<AccountResponseDto>> signUp(@Valid @RequestBody SignUpDto signUpDto){
        Account serviceResult = accountService.join(signUpDto);

        AccountResponseDto accountResponseDto = AccountResponseDto.fromEntity(serviceResult);
        ServiceResult<AccountResponseDto> finalResult = ServiceResult.success("회원가입 성공", accountResponseDto);

        return ResponseEntity.ok(finalResult);
    }

    @PostMapping("/login")
    public ResponseEntity<ServiceResult<AccountResponseDto>> login(@Valid @RequestBody LoginDto loginDto){
        Account serviceResult = accountService.login(loginDto);

        AccountResponseDto accountResponseDto = AccountResponseDto.fromEntity(serviceResult);
        ServiceResult<AccountResponseDto> finalResult = ServiceResult.success("로그인 성공", accountResponseDto);

        return ResponseEntity.ok(finalResult);
    }
}
