package com.market.marketplacebackend.customer.service;

import com.market.marketplacebackend.common.SuccessDto;
import com.market.marketplacebackend.customer.domain.Customer;
import com.market.marketplacebackend.customer.dto.SignUpDto;
import com.market.marketplacebackend.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CustomerRepository customerRepository;
    public SuccessDto join(SignUpDto signUpDto) {
        if(customerRepository.existsByEmail(signUpDto.getEmail())){
            throw new IllegalArgumentException("이미 사용중인 이메일입니다 : " + signUpDto.getEmail());
        }

        Customer customer = signUpDto.toEntity();
        Customer savedCustomer = customerRepository.save(customer);

        return SuccessDto.builder()
                .message("회원가입 성공")
                .data(savedCustomer)
                .build();
    }
}
