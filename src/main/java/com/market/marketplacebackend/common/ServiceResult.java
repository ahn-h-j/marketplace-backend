package com.market.marketplacebackend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResult<T> {
    private boolean success;
    private ErrorCode code;
    private String message;
    private T data;
    private LocalDateTime timeStamp;


    public static <T> ServiceResult<T> success(String message, T data){
        return new ServiceResult<>(true, ErrorCode.OK, message, data,LocalDateTime.now());
    }

    public static <T> ServiceResult<T> failure(ErrorCode errorCode, String message){
        return new ServiceResult<>(false, errorCode, message, null, LocalDateTime.now());
    }
}


