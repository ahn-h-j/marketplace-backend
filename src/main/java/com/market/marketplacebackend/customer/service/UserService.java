package com.market.marketplacebackend.customer.service;

import com.market.marketplacebackend.customer.domain.Customer;
import com.market.marketplacebackend.customer.dto.LoginDto;
import com.market.marketplacebackend.customer.dto.SignUpDto;
import com.market.marketplacebackend.customer.repository.CustomerRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CustomerRepository customerRepository;
    private final HttpSession httpSession;

    public Customer join(SignUpDto signUpDto) {
        if(customerRepository.existsByEmail(signUpDto.getEmail())){
            throw new IllegalArgumentException("이미 사용중인 이메일입니다 : " + signUpDto.getEmail());
        }

        Customer customer = signUpDto.toEntity();

        return customerRepository.save(customer);
    }

    public Customer login(LoginDto loginDto) {
        Customer customer = customerRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("email이 존재하지 않습니다"));

        if(!customer.getPassword().equals(loginDto.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        httpSession.setAttribute("UserId",customer.getId());

        return Customer.builder()
                .name(customer.getName())
                .email(customer.getEmail())
                .build();
    }

}
