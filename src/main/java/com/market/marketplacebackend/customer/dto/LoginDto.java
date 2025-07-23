package com.market.marketplacebackend.customer.dto;

import com.market.marketplacebackend.customer.domain.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

    public Customer toEntity(){
        return Customer.builder()
                .email(email)
                .password(password)
                .build();
    }
}

