package com.market.marketplacebackend.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private int code;
    private T data;
    private String error;
    private LocalDateTime localDateTime;

    public static <T> ApiResponse<T> success(T data, String message){
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .localDateTime(LocalDateTime.now())
                .code(200)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, int code){
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .localDateTime(LocalDateTime.now())
                .code(code)
                .build();
    }
}
