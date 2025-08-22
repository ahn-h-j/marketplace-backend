package com.market.marketplacebackend.security.controller;

import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.common.enums.TokenType;
import com.market.marketplacebackend.common.exception.BusinessException;
import com.market.marketplacebackend.common.exception.ErrorCode;
import com.market.marketplacebackend.security.dto.ReissueResponseDto;
import com.market.marketplacebackend.security.service.ReissueService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final ReissueService reissueService;

    @PostMapping("/reissue")
    public ResponseEntity<ServiceResult<ReissueResponseDto>> reissue(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length == 0){
            throw new BusinessException(ErrorCode.NO_REFRESH_TOKEN_COOKIE);
        }

        String refresh = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(TokenType.REFRESH.getValue()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new BusinessException(ErrorCode.NO_REFRESH_TOKEN_COOKIE));

        ReissueResponseDto reissueResponseDto = reissueService.reIssueToken(refresh);

        response.setHeader(TokenType.ACCESS.getValue(), reissueResponseDto.getNewAccessToken());
        response.addCookie(reissueResponseDto.getRefreshCookie());

        ServiceResult<ReissueResponseDto> finalResult = ServiceResult.success("토큰 재발행 완료", reissueResponseDto);

        return ResponseEntity.ok(finalResult);

    }
}