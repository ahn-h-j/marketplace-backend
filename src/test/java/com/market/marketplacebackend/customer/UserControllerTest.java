package com.market.marketplacebackend.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.marketplacebackend.common.SuccessDto;
import com.market.marketplacebackend.customer.controller.UserController;
import com.market.marketplacebackend.customer.dto.SignUpDto;
import com.market.marketplacebackend.customer.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
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

        SuccessDto successDto = SuccessDto.builder()
                .message("회원가입 성공")
                .data("회원가입이 완료되었습니다")
                .build();

        when(userService.join(any(SignUpDto.class))).thenReturn(successDto);

        // when & then
        mockMvc.perform(post("/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value("회원 가입 성공"))
                        .andExpect(jsonPath("$.code").value(200));

        verify(userService, times(1)).join(any(SignUpDto.class));
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
