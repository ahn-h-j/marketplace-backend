package com.market.marketplacebackend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 성공
    OK(HttpStatus.OK, "OK", "성공"),

    // 회원가입 관련
    EMAIL_DUPLICATE(HttpStatus.CONFLICT, "EMAIL_DUPLICATE", "이미 사용중인 이메일입니다"),

    // 회원정보 관련
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", "존재하지 않는 계정입니다."),
    FORBIDDEN_NOT_SELLER(HttpStatus.FORBIDDEN, "FORBIDDEN_NOT_SELLER", "셀러 권한이 없는 계정입니다."),

    // 로그인 관련
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "EMAIL_NOT_FOUND", "존재하지 않는 이메일입니다"),
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다"),

    // 요청 파라미터 관련
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "입력값이 유효하지 않습니다."),

    // 서버 오류 관련
    DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB_ERROR", "데이터베이스 오류가 발생했습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 내부 오류가 발생했습니다."),

    // 상품 관련
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "존재하지 않는 상품입니다."),
    FORBIDDEN_PRODUCT(HttpStatus.FORBIDDEN, "FORBIDDEN_PRODUCT", "해당 상품에 대한 권한이 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", "존재하지 않는 카테고리입니다."),

    //장바구니 관련
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CART_NOT_FOUND", "카트가 존재하지 않습니다."),
    PRODUCT_NOT_IN_CART(HttpStatus.NOT_FOUND, "PRODUCT_NOT_IN_CART", "카트에 해당 상품이 존재하지 않습니다."),
    FORBIDDEN_CART(HttpStatus.NOT_FOUND, "FORBIDDEN_CART", "해당 카트에 대한 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}