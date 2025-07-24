package com.market.marketplacebackend.customer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.marketplacebackend.common.ErrorCode;
import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.customer.domain.Customer;
import com.market.marketplacebackend.customer.dto.LoginDto;
import com.market.marketplacebackend.customer.dto.SignUpDto;
import com.market.marketplacebackend.customer.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("회원가입 통합 성공 테스트")
    void signUp_Integration_Success() throws Exception{
        SignUpDto signUpDto = SignUpDto.builder()
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();

        mockMvc.perform(post("/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원 가입 성공"));

        Optional<Customer> customer = customerRepository.findByEmail("test@example.com");
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

        Customer customer = signUpDto.toEntity();
        customerRepository.save(customer);

        mockMvc.perform(post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ErrorCode.EMAIL_DUPLICATE))
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

        Customer customer = Customer.builder()
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();
        customerRepository.save(customer);

        MvcResult result = mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인 성공"))
                .andExpect(jsonPath("$.data.name").value("test"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andReturn();

        String response =  result.getResponse().getContentAsString();
        TypeReference<ServiceResult<Customer>> typeRef = new TypeReference<>() {};
        ServiceResult<Customer> responseDto = objectMapper.readValue(response, typeRef);

        assertThat(responseDto.isSuccess()).isTrue();
        assertThat(responseDto.getMessage()).isEqualTo("로그인 성공");
        assertThat(responseDto.getData().getName()).isEqualTo("test");
        assertThat(responseDto.getData().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("로그인 통합 실패 테스트(비밀번호 불일치)")
    void login_Integration_Password_Mismatch_Failure() throws Exception{
        LoginDto loginDto = LoginDto.builder()
                .email("test@example.com")
                .password("invalidPW123")
                .build();

        Customer customer = Customer.builder()
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();
        customerRepository.save(customer);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다"));

    }

    @Test
    @DisplayName("로그인 통합 실패 테스트(이메일 존재X)")
    void login_Integration_Email_NotFound_Failure() throws Exception{
        LoginDto loginDto = LoginDto.builder()
                .email("invalid@example.com")
                .password("password123")
                .build();

        Customer customer = Customer.builder()
                .name("test")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();
        customerRepository.save(customer);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("존재하지 않는 이메일입니다"));

    }
}
