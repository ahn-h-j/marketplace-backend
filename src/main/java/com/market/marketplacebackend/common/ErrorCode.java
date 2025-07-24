package com.market.marketplacebackend.common;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 성공
    OK("OK", "성공"),

    // 회원가입 관련
    EMAIL_DUPLICATE("EMAIL_DUPLICATE", "이미 사용중인 이메일입니다"),

    // 로그인 관련
    EMAIL_NOT_FOUND("EMAIL_NOT_FOUND", "존재하지 않는 이메일입니다"),
    PASSWORD_MISMATCH("PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

}