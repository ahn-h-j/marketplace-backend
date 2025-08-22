package com.market.marketplacebackend.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.marketplacebackend.account.controller.AccountController;
import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.dto.SignUpDto;
import com.market.marketplacebackend.account.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("회원가입 성공(컨트롤러)")
    void signUp_Success_Controller() throws Exception {
        // given
        SignUpDto signUpDto = SignUpDto.builder()
                .name("홍길동")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();
        String password = bCryptPasswordEncoder.encode(signUpDto.getPassword());
        Account account = signUpDto.toEntity(password);

        when(accountService.join(any(SignUpDto.class))).thenReturn(account);

        // when & then
        mockMvc.perform(post("/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value("회원가입 성공"))
                        .andExpect(jsonPath("$.code").value("OK"));

        verify(accountService, times(1)).join(any(SignUpDto.class));
    }

    @Test
    @DisplayName("회원가입 실패 - validation 오류")
    void signUp_Validation_Fail() throws Exception {
        // given - 이름이 빈 값
        SignUpDto invalidDto = SignUpDto.builder()
                .name("")
                .email("invalid-email")
                .password("123")
                .phoneNumber("invalid")
                .build();

        // when & then
        mockMvc.perform(post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

    }
}
