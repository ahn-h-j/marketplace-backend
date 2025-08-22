package com.market.marketplacebackend.account;

import com.market.marketplacebackend.cart.service.CartService;
import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.dto.SignUpDto;
import com.market.marketplacebackend.account.repository.AccountRepository;
import com.market.marketplacebackend.account.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CartService cartService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DisplayName("회원가입 성공(서비스)")
    void signUp_Success_Service(){
        //given
        SignUpDto signUpDto = SignUpDto.builder()
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();

        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();

        when(accountRepository.save(any(Account.class)))
                .thenReturn(account);
        doNothing().when(cartService).createCartForAccount(any(Account.class));

        //when
        Account result = accountService.join(signUpDto);

        //then
        assertThat(result.getEmail()).isEqualTo(signUpDto.getEmail());
        assertThat(result.getName()).isEqualTo(signUpDto.getName());
        assertThat(result.getPassword()).isEqualTo(signUpDto.getPassword());
        assertThat(result.getPhoneNumber()).isEqualTo(signUpDto.getPhoneNumber());
        verify(accountRepository).existsByEmail("test@example.com");
        verify(accountRepository).save(any(Account.class));
    }
    @Test
    @DisplayName("회원가입 실패 - 동일한 이메일 존재")
    void signUp_Same_Email_Fail() {
        //given
        SignUpDto signUpDto = SignUpDto.builder()
                .email("test@example.com")
                .build();

        when(accountRepository.existsByEmail("test@example.com"))
                .thenReturn(true);
        //when
        BusinessException exception = assertThrows(BusinessException.class, () -> accountService.join(signUpDto));

        //then
        assertEquals(ErrorCode.EMAIL_DUPLICATE, exception.getErrorCode());
        verify(accountRepository).existsByEmail("test@example.com");
    }
}
