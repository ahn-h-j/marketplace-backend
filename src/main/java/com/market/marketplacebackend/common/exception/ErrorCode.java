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
    NOT_ENOUGH_STOCK(HttpStatus.CONFLICT, "NOT_ENOUGH_STOCK", "재고는 0개 미만이 될 수 없습니다."),

    //장바구니 관련
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CART_NOT_FOUND", "카트가 존재하지 않습니다."),
    PRODUCT_NOT_IN_CART(HttpStatus.NOT_FOUND, "PRODUCT_NOT_IN_CART", "카트에 해당 상품이 존재하지 않습니다."),
    FORBIDDEN_CART(HttpStatus.FORBIDDEN, "FORBIDDEN_CART", "해당 카트에 대한 권한이 없습니다."),
    REQUESTED_CART_ITEMS_NOT_FOUND(HttpStatus.NOT_FOUND, "REQUESTED_CART_ITEMS_NOT_FOUND", "요청한 장바구니 상품 중 일부를 찾을 수 없습니다."),

    //주문 관련
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "주문이 존재하지 않습니다."),
    FORBIDDEN_ORDER(HttpStatus.FORBIDDEN, "FORBIDDEN_ORDER", "해당 주문에 대한 권한이 없습니다."),
    INVALID_STATE_TRANSITION(HttpStatus.CONFLICT, "INVALID_STATE_TRANSITION", "허용되지 않는 주문 상태 변경입니다."),

    //JWT
    NO_REFRESH_TOKEN_COOKIE(HttpStatus.NOT_FOUND, "NO_REFRESH_TOKEN_COOKIE", "쿠키가 없습니다."),
    MISSING_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "MISSING_REFRESH_TOKEN", "리프레쉬 토큰이 누락되었습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_REFRESH_TOKEN", "리프레쉬 토큰이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "유효하지 않은 리프레쉬 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "REFRESH_TOKEN_NOT_FOUND", "저장된 리프레쉬 토큰을 찾을 수 없습니다."),

    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_REQUIRED", "인증이 필요한 서비스입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_ACCESS_TOKEN", "유효하지 않은 액세스 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_ACCESS_TOKEN", "만료된 액세스 토큰입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근 권한이 없습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}