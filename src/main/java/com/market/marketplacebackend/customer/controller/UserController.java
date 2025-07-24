package com.market.marketplacebackend.customer.controller;

import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.customer.domain.Customer;
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
    public ServiceResult<Customer> signUp(@Valid @RequestBody SignUpDto signUpDto){

        return userService.join(signUpDto);
    }

    @PostMapping("/login")
    public ServiceResult<Customer> login(@Valid @RequestBody LoginDto loginDto){

        return userService.login(loginDto);
    }
}
