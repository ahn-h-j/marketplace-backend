package com.market.marketplacebackend.customer.controller;

import com.market.marketplacebackend.customer.domain.Customer;
import com.market.marketplacebackend.customer.dto.ApiResponse;
import com.market.marketplacebackend.customer.dto.LoginDto;
import com.market.marketplacebackend.customer.dto.SignUpDto;
import com.market.marketplacebackend.customer.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponse<Customer> signUp(@Valid @RequestBody SignUpDto signUpDto){

        Customer newCustomer = userService.join(signUpDto);
        return ApiResponse.success(newCustomer, "회원 가입 성공");
    }

    @PostMapping("/login")
    public ApiResponse<Customer> login(@Valid @RequestBody LoginDto loginDto){

        Customer loginCustomer = userService.login(loginDto);
        return ApiResponse.success(loginCustomer, "로그인 성공");
    }
}
