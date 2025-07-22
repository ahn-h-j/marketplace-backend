package com.market.marketplacebackend.customer;

import com.market.marketplacebackend.common.SuccessDto;
import com.market.marketplacebackend.customer.controller.UserController;
import com.market.marketplacebackend.customer.domain.Customer;
import com.market.marketplacebackend.customer.dto.SignUpDto;
import com.market.marketplacebackend.customer.repository.CustomerRepository;
import com.market.marketplacebackend.customer.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("회원가입 성공(서비스)")
    void signUp_Success_Service() throws Exception{
        //given
        SignUpDto signUpDto = SignUpDto.builder()
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .build();

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customer);
        //when
        SuccessDto result = userService.join(signUpDto);

        //then
        assertThat(result.getMessage()).isEqualTo("회원가입 성공");
        verify(customerRepository).existsByEmail("test@example.com");
        verify(customerRepository).save(any(Customer.class));
    }
    @Test
    @DisplayName("회원가입 실패 - 동일한 이메일 존재")
    void signUp_Same_Email_Fail() throws Exception {
        //given
        SignUpDto signUpDto = SignUpDto.builder()
                .email("test@example.com")
                .build();

        when(customerRepository.existsByEmail("test@example.com"))
                .thenReturn(true);
        //when
        assertThrows(IllegalArgumentException.class, () -> userService.join(signUpDto));
        //then
        verify(customerRepository).existsByEmail("test@example.com");
    }
}
