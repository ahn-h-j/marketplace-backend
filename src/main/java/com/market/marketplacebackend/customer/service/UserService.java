package com.market.marketplacebackend.customer.service;

import com.market.marketplacebackend.common.ErrorCode;
import com.market.marketplacebackend.common.ServiceResult;
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

    public ServiceResult<Customer> join(SignUpDto signUpDto) {
        if(customerRepository.existsByEmail(signUpDto.getEmail())){
            return ServiceResult.failure(ErrorCode.EMAIL_DUPLICATE, "이미 사용중인 이메일입니다");
        }

        Customer customer = signUpDto.toEntity();
        Customer savedCustomer = customerRepository.save(customer);

        return ServiceResult.success("회원 가입 성공", savedCustomer);
    }

    public ServiceResult<Customer> login(LoginDto loginDto) {
        Customer customer = customerRepository.findByEmail(loginDto.getEmail())
                .orElse(null);

        if (customer == null) {
            return ServiceResult.failure(ErrorCode.EMAIL_NOT_FOUND, "존재하지 않는 이메일입니다");
        }

        if(!customer.getPassword().equals(loginDto.getPassword())){
            return ServiceResult.failure(ErrorCode.PASSWORD_MISMATCH, "비밀번호가 일치하지 않습니다");
        }

        httpSession.setAttribute("UserId",customer.getId());

        return ServiceResult.success("로그인 성공", customer);
    }

}
