package com.market.marketplacebackend.account;

// Removed unused import for UserController
import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.dto.LoginDto;
import com.market.marketplacebackend.account.dto.SignUpDto;
import com.market.marketplacebackend.account.repository.AccountRepository;
import com.market.marketplacebackend.account.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private HttpSession httpSession;

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
        //when
        ServiceResult<Account> result = accountService.join(signUpDto);

        //then
        assertThat(result.getData().getEmail()).isEqualTo(signUpDto.getEmail());
        assertThat(result.getData().getName()).isEqualTo(signUpDto.getName());
        assertThat(result.getData().getPassword()).isEqualTo(signUpDto.getPassword());
        assertThat(result.getData().getPhoneNumber()).isEqualTo(signUpDto.getPhoneNumber());
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

    @Test
    @DisplayName("로그인 성공(서비스)")
    void login_Success_Service() {
        //given
        LoginDto loginDto = LoginDto.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();

        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.ofNullable(account));

        //when
        ServiceResult<Account> loginCustomer = accountService.login(loginDto);

        //then
        assertThat(loginCustomer.getData().getEmail()).isEqualTo(loginDto.getEmail());
        assertThat(loginCustomer.getData().getPassword()).isEqualTo(loginDto.getPassword());
        verify(accountRepository).findByEmail("test@example.com");
        verify(httpSession).setAttribute("UserId", 1L);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void signUp_Password_Inconsistency_Fail() {
        //given
        LoginDto loginDto = LoginDto.builder()
                .email("test@example.com")
                .password("invalidPW123")
                .build();

        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();

        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.ofNullable(account));

        //when
        BusinessException exception = assertThrows(BusinessException.class, () -> accountService.login(loginDto));

        //then
        assertEquals(ErrorCode.PASSWORD_MISMATCH, exception.getErrorCode());
        verify(accountRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("로그인 실패 - 이메일 없음")
    void signUp_Email_NotFound_Fail() {
        //given
        LoginDto loginDto = LoginDto.builder()
                .email("test@example.com")
                .password("invalidPW123")
                .build();

        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        //when
        BusinessException exception = assertThrows(BusinessException.class, () -> accountService.login(loginDto));

        //then
        assertEquals(ErrorCode.EMAIL_NOT_FOUND, exception.getErrorCode());
        verify(accountRepository).findByEmail("test@example.com");
    }
}
