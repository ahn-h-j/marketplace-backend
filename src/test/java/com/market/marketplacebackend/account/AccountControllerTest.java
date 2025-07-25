package com.market.marketplacebackend.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.account.controller.AccountController;
import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.dto.LoginDto;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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

        Account account = signUpDto.toEntity();

        ServiceResult<Account> result = ServiceResult.success("회원가입 성공", account);

        when(accountService.join(any(SignUpDto.class))).thenReturn(result);

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

    @Test
    @DisplayName("로그인 성공(컨트롤러)")
    void login_Success_Controller() throws Exception{
        //given
        LoginDto loginDto = LoginDto.builder()
                .email("test@example.com")
                .password("password123")
                .build();
        Account account = Account.builder()
                .id(1L)
                .name("test")
                .email("test@example.com")
                .build();

        ServiceResult<Account> result = ServiceResult.success("로그인 성공", account);

        when(accountService.login(any(LoginDto.class))).thenReturn(result);

        //when & then
        MvcResult mvcResult = mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인 성공"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.name").value("test"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andReturn();
        System.out.println("실제 JSON: " + mvcResult.getResponse().getContentAsString());


    }

    @Test
    @DisplayName("로그인 실패 - validation 오류")
    void login_Validation_Fail() throws Exception {
        // given - 이름이 빈 값
        LoginDto invalidDto = LoginDto.builder()
                .email("invalid-email")
                .password("123")
                .build();

        // when & then
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

    }
}
