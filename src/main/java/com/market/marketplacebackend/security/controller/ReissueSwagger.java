package com.market.marketplacebackend.security.controller;

import com.market.marketplacebackend.common.ServiceResult;
import com.market.marketplacebackend.security.dto.ReissueResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Authentication", description = "인증 관리 API")
public interface ReissueSwagger {

    @Operation(
            summary = "JWT 토큰 재발급",
            description = "리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급받습니다. " +
                         "리프레시 토큰은 쿠키로 전달되어야 하며, 새로운 토큰들은 응답 헤더와 쿠키로 반환됩니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                    {
                                        "success": true,
                                        "code": "OK",
                                        "message": "토큰 재발행 완료",
                                        "data": {
                                            "newAccessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                            "refreshCookie": "HttpOnly; Secure; SameSite=Strict"
                                        },
                                        "timeStamp": "2025-08-27T12:18:21.5722917"
                                    }
                                    """
                            )
                    ),
                    headers = {
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "access",
                                    description = "새로 발급된 액세스 토큰",
                                    schema = @Schema(type = "string"),
                                    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "리프레시 토큰 누락",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = {
                                    @ExampleObject(
                                            name = "쿠키 없음",
                                            summary = "리프레시 토큰 쿠키가 없는 경우",
                                            value = """
                                            {
                                                "success": false,
                                                "code": "NO_REFRESH_TOKEN_COOKIE",
                                                "message": "쿠키가 없습니다.",
                                                "data": null,
                                                "timeStamp": "2025-08-27T12:18:54.1693644"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "리프레시 토큰 누락",
                                            summary = "리프레시 토큰이 요청에 포함되지 않은 경우",
                                            value = """
                                            {
                                                "success": false,
                                                "code": "MISSING_REFRESH_TOKEN",
                                                "message": "리프레쉬 토큰이 누락되었습니다.",
                                                "data": null,
                                                "timeStamp": "2025-08-27T12:18:54.1693644"
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 리프레시 토큰",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = {
                                    @ExampleObject(
                                            name = "만료된 리프레시 토큰",
                                            summary = "리프레시 토큰이 만료된 경우",
                                            value = """
                                            {
                                                "success": false,
                                                "code": "EXPIRED_REFRESH_TOKEN",
                                                "message": "리프레쉬 토큰이 만료되었습니다.",
                                                "data": null,
                                                "timeStamp": "2025-08-27T12:18:54.1693644"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 리프레시 토큰",
                                            summary = "잘못된 형식이거나 변조된 토큰",
                                            value = """
                                            {
                                                "success": false,
                                                "code": "INVALID_REFRESH_TOKEN",
                                                "message": "유효하지 않은 리프레쉬 토큰입니다.",
                                                "data": null,
                                                "timeStamp": "2025-08-27T12:18:54.1693644"
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "저장된 리프레시 토큰을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "토큰 미존재",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "REFRESH_TOKEN_NOT_FOUND",
                                        "message": "저장된 리프레쉬 토큰을 찾을 수 없습니다.",
                                        "data": null,
                                        "timeStamp": "2025-08-27T12:18:54.1693644"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = {
                                    @ExampleObject(
                                            name = "데이터베이스 오류",
                                            value = """
                                            {
                                                "success": false,
                                                "code": "DB_ERROR",
                                                "message": "데이터베이스 오류가 발생했습니다.",
                                                "data": null,
                                                "timeStamp": "2025-08-27T12:18:54.1693644"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "서버 내부 오류",
                                            value = """
                                            {
                                                "success": false,
                                                "code": "INTERNAL_ERROR",
                                                "message": "서버 내부 오류가 발생했습니다.",
                                                "data": null,
                                                "timeStamp": "2025-08-27T12:18:54.1693644"
                                            }
                                            """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/reissue")
    ResponseEntity<ServiceResult<ReissueResponseDto>> reissue(HttpServletRequest request, HttpServletResponse response);
}