package com.market.marketplacebackend.customer.controller;

import com.market.marketplacebackend.common.SuccessDto;
import com.market.marketplacebackend.customer.dto.ApiResponse;
import com.market.marketplacebackend.customer.dto.SignUpDto;
import com.market.marketplacebackend.customer.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
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
    public ApiResponse<SuccessDto> signUp(@Valid @RequestBody SignUpDto signUpDto){

        SuccessDto result = userService.join(signUpDto);
        return ApiResponse.success(result, "회원 가입 성공");
    }
}
