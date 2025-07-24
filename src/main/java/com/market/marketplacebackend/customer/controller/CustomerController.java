package com.market.marketplacebackend.customer.controller;

import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.customer.domain.Customer;
import com.market.marketplacebackend.customer.dto.LoginDto;
import com.market.marketplacebackend.customer.dto.SignUpDto;
import com.market.marketplacebackend.customer.service.CustomerService;
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
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/signup")
    public ResponseEntity<ServiceResult<Customer>> signUp(@Valid @RequestBody SignUpDto signUpDto){
        ServiceResult<Customer> result = customerService.join(signUpDto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<ServiceResult<Customer>> login(@Valid @RequestBody LoginDto loginDto){
        ServiceResult<Customer> result = customerService.login(loginDto);
        return ResponseEntity.ok(result);
    }
}
