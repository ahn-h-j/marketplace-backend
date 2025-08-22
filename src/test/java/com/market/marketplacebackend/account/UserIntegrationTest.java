package com.market.marketplacebackend.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.marketplacebackend.common.enums.AccountRole;
import com.market.marketplacebackend.common.exception.ErrorCode;
import com.market.marketplacebackend.account.domain.Account;
import com.market.marketplacebackend.account.dto.LoginDto;
import com.market.marketplacebackend.account.dto.SignUpDto;
import com.market.marketplacebackend.account.repository.AccountRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.jwt.secret=ksf92jf12jf23jdfh4skdlf2398rjskfjweofjr9203sldf9230jsdf023r"
})
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DisplayName("회원가입 통합 성공 테스트")
    void signUp_Integration_Success() throws Exception{
        SignUpDto signUpDto = SignUpDto.builder()
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.BUYER)
                .build();

        mockMvc.perform(post("/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입 성공"));

        Optional<Account> customer = accountRepository.findByEmail("test@example.com");
        assertThat(customer).isPresent();
        assertThat(customer.get().getName()).isEqualTo("test");
    }

    @Test
    @DisplayName("회원가입 통합 실패 테스트(중복 이메일 존재)")
    void signUp_Integration_Fail() throws Exception {
        SignUpDto signUpDto = SignUpDto.builder()
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();
        String password = bCryptPasswordEncoder.encode(signUpDto.getPassword());
        Account account = signUpDto.toEntity(password);
        accountRepository.save(account);

        mockMvc.perform(post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ErrorCode.EMAIL_DUPLICATE.getCode()))
                .andExpect(jsonPath("$.message").value("이미 사용중인 이메일입니다"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timeStamp").exists());
    }



    @Test
    @DisplayName("로그인 통합 성공 테스트")
    void login_Integration_Success() throws Exception{
        LoginDto loginDto = LoginDto.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        Account account = Account.builder()
                .name("test")
                .email("test@example.com")
                .password(bCryptPasswordEncoder.encode("password123"))
                .phoneNumber("010-1234-5678")
                .accountRole(AccountRole.BUYER)
                .build();
        accountRepository.save(account);

        MvcResult result = mockMvc.perform(post("/login") // 경로 변경
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        // JWT 토큰 검증
        String authorizationHeader = result.getResponse().getHeader("Authorization");
        assertThat(authorizationHeader).isNotNull();
        assertThat(authorizationHeader).startsWith("Bearer ");

        // 쿠키 검증
        Cookie[] cookies = result.getResponse().getCookies();
        assertThat(cookies).isNotEmpty();
    }

    @Test
    @DisplayName("로그인 통합 실패 테스트(비밀번호 불일치)")
    void login_Integration_Password_Mismatch_Failure() throws Exception{
        Account account = Account.builder()
                .name("test")
                .email("test@example.com")
                .password(bCryptPasswordEncoder.encode("password123"))
                .phoneNumber("010-1234-5678")
                .build();
        accountRepository.save(account);

        LoginDto loginDto = LoginDto.builder()
                .email("test@example.com")
                .password("invalidPW123")
                .build();

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 통합 실패 테스트(이메일 존재X)")
    void login_Integration_Email_NotFound_Failure() throws Exception{
        LoginDto loginDto = LoginDto.builder()
                .email("invalid@example.com")
                .password(bCryptPasswordEncoder.encode("password123"))
                .build();

        Account account = Account.builder()
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();
        accountRepository.save(account);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());

    }
}
