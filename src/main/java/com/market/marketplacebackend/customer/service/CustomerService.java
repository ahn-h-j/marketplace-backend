package com.market.marketplacebackend.customer.service;

import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
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
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final HttpSession httpSession;

    public ServiceResult<Customer> join(SignUpDto signUpDto) {
        if(customerRepository.existsByEmail(signUpDto.getEmail())){
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATE);
        }

        Customer customer = signUpDto.toEntity();
        Customer savedCustomer = customerRepository.save(customer);

        return ServiceResult.success("회원 가입 성공", savedCustomer);
    }

    public ServiceResult<Customer> login(LoginDto loginDto) {
        Customer customer = customerRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_NOT_FOUND));

        if(!customer.getPassword().equals(loginDto.getPassword())){
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        httpSession.setAttribute("UserId",customer.getId());

        return ServiceResult.success("로그인 성공", customer);
    }

}
