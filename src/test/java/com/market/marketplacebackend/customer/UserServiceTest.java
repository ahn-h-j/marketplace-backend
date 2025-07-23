package com.market.marketplacebackend.customer;

// Removed unused import for UserController
import com.market.marketplacebackend.customer.domain.Customer;
import com.market.marketplacebackend.customer.dto.LoginDto;
import com.market.marketplacebackend.customer.dto.SignUpDto;
import com.market.marketplacebackend.customer.repository.CustomerRepository;
import com.market.marketplacebackend.customer.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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

    @Mock
    private HttpSession httpSession;

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
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customer);
        //when
        Customer result = userService.join(signUpDto);

        //then
        assertThat(result.getEmail()).isEqualTo(signUpDto.getEmail());
        assertThat(result.getName()).isEqualTo(signUpDto.getName());
        assertThat(result.getPassword()).isEqualTo(signUpDto.getPassword());
        assertThat(result.getPhoneNumber()).isEqualTo(signUpDto.getPhoneNumber());
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

    @Test
    @DisplayName("로그인 성공(서비스)")
    void login_Success_Service() throws Exception {
        //given
        LoginDto loginDto = LoginDto.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();

        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.ofNullable(customer));

        //when
        Customer loginCustomer = userService.login(loginDto);

        //then
        assertThat(loginCustomer.getEmail()).isEqualTo(loginDto.getEmail());
        assertThat(loginCustomer.getPassword()).isEqualTo(loginDto.getPassword());
        verify(customerRepository).findByEmail("test@example.com");
        verify(httpSession).setAttribute("UserId", 1L);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void signUp_Password_Inconsistency_Fail() throws Exception {
        //given
        LoginDto loginDto = LoginDto.builder()
                .email("test@example.com")
                .password("invalidPW123")
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();

        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.ofNullable(customer));

        //when
        assertThrows(IllegalArgumentException.class, () -> userService.login(loginDto));
        //then
        verify(customerRepository).findByEmail("test@example.com");
    }
}
