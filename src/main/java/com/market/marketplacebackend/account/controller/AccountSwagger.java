package com.market.marketplacebackend.account.controller;

import com.market.marketplacebackend.account.dto.AccountResponseDto;
import com.market.marketplacebackend.account.dto.SignUpDto;
import com.market.marketplacebackend.common.ServiceResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Account", description = "계정 관리 API")
@RequestMapping("/user")
public interface AccountSwagger {

    @Operation(
            summary = "회원가입", 
            description = "새로운 사용자를 등록합니다. 이메일과 전화번호는 유니크해야 하며, 비밀번호는 암호화되어 저장됩니다."
    )
    @RequestBody(
            description = "회원가입 정보",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = SignUpDto.class),
                    examples = {
                            @ExampleObject(
                                    name = "구매자 회원가입",
                                    summary = "일반 구매자로 가입하는 경우",
                                    value = """
                                    {
                                        "name": "김구매자",
                                        "email": "buyer@example.com",
                                        "password": "securePassword123!",
                                        "phoneNumber": "010-1234-5678",
                                        "accountRole": "BUYER"
                                    }
                                    """
                            ),
                            @ExampleObject(
                                    name = "판매자 회원가입",
                                    summary = "판매자로 가입하는 경우",
                                    value = """
                                    {
                                        "name": "김판매자",
                                        "email": "seller@example.com",
                                        "password": "strongPassword456!",
                                        "phoneNumber": "010-9876-5432",
                                        "accountRole": "SELLER"
                                    }
                                    """
                            )
                    }
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", 
                    description = "회원가입 성공",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = {
                                    @ExampleObject(
                                            name = "성공 응답 - 구매자",
                                            value = """
                                            {
                                                "success": true,
                                                "code": "OK",
                                                "message": "회원가입 성공",
                                                "data": {
                                                    "name": "김구매자",
                                                    "email": "buyer@example.com",
                                                    "phoneNumber": "010-1234-5678",
                                                    "accountRole": "BUYER"
                                                },
                                                "timeStamp": "2025-08-27T12:18:21.5722917"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "성공 응답 - 판매자",
                                            value = """
                                            {
                                                "success": true,
                                                "code": "OK",
                                                "message": "회원가입 성공",
                                                "data": {
                                                    "name": "김판매자",
                                                    "email": "seller@example.com",
                                                    "phoneNumber": "010-9876-5432",
                                                    "accountRole": "SELLER"
                                                },
                                                "timeStamp": "2025-08-27T12:18:21.5722917"
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "입력값 유효성 검증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = {
                                    @ExampleObject(
                                            name = "유효성 검증 실패",
                                            summary = "필수값 누락이나 형식 오류",
                                            value = """
                                            {
                                                "success": false,
                                                "code": "VALIDATION_ERROR",
                                                "message": "입력값이 유효하지 않습니다.",
                                                "data": null,
                                                "timeStamp": "2025-08-27T12:18:54.1693644"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "이메일 형식 오류",
                                            summary = "잘못된 이메일 형식",
                                            value = """
                                            {
                                                "success": false,
                                                "code": "VALIDATION_ERROR",
                                                "message": "이메일 형식이 올바르지 않습니다",
                                                "data": null,
                                                "timeStamp": "2025-08-27T12:18:54.1693644"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "전화번호 형식 오류",
                                            summary = "잘못된 전화번호 형식",
                                            value = """
                                            {
                                                "success": false,
                                                "code": "VALIDATION_ERROR",
                                                "message": "전화번호는 000-0000-0000 형식이어야 합니다",
                                                "data": null,
                                                "timeStamp": "2025-08-27T12:18:54.1693644"
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409", 
                    description = "중복된 이메일로 인한 충돌",
                    content = @Content(
                            schema = @Schema(implementation = ServiceResult.class),
                            examples = @ExampleObject(
                                    name = "이메일 중복",
                                    value = """
                                    {
                                        "success": false,
                                        "code": "EMAIL_DUPLICATE",
                                        "message": "이미 사용중인 이메일입니다",
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
    @PostMapping("/signup")
    ResponseEntity<ServiceResult<AccountResponseDto>> signUp(@Valid @org.springframework.web.bind.annotation.RequestBody SignUpDto signUpDto);
}
