package com.elice.iliceworksbe.auth.web;

import com.elice.iliceworksbe.auth.dto.request.*;
import com.elice.iliceworksbe.auth.dto.response.GetProfileResponseDto;
import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.auth.service.AuthService;
import com.elice.iliceworksbe.common.exception.BaseResponse;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "로그인, 회원가입 관련 API 입니다.")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "일반 이메일 로그인 요청 (AT X)", security = {}, description = "일반 이메일 로그인을 합니다.")
    @PostMapping("/login")
    public BaseResponse<String> login(@RequestBody @Valid LoginRequestDto loginRequestDTO) {
        // Swagger 용. 실제 구현은 Filter에 존재
        return new BaseResponse<>(ErrorCode.SUCCESS);
    }

    @Operation(summary = "accountId 중복 여부 확인", security = {}, description = "해당 계정ID을 입력하고 가입된 계정ID인지 확인합니다. true인 경우, 이미 가입된 이메일입니다.")
    @PostMapping("/validate-email")
    public BaseResponse<Boolean> checkDuplicateAccountId(@RequestBody @Valid CheckDuplicateAccountIdRequestDto checkDuplicateAccountIdRequestDTO) {
        return new BaseResponse<>(authService.checkDuplicateAccountId(checkDuplicateAccountIdRequestDTO));
    }

    @Operation(summary = "이메일 인증 코드 발급 요청", security = {},description = "이메일 인증을 위한 인증 코드를 발급 요청합니다.")
    @PostMapping("/verify-email")
    public BaseResponse<String> verifyEmail(@RequestBody @Valid VerifyEmailRequestDto verifyEmailRequestDto) {
        authService.verifyEmail(verifyEmailRequestDto);
        return new BaseResponse<>(ErrorCode.SUCCESS);
    }

    @Operation(summary = "이메일 인증 코드 확인 요청 (AT X)", security = {},description = "인증 코드가 올바른지 확인하는 요청입니다.")
    @PostMapping("/verify")
    public BaseResponse<String> confirmVerificationCode(@RequestBody @Valid ConfirmEmailRequestDto confirmEmailRequestDto) {
        authService.confirmVerificationCode(confirmEmailRequestDto);
        return new BaseResponse<>(ErrorCode.SUCCESS);
    }

    @Operation(summary = "이메일 일반 회원가입 (AT X)", security = {}, description = "이메일 인증을 통한 팀장의 회원가입입니다.")
    @PostMapping(value = "/signup")
    public BaseResponse<String> signup(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        authService.signUp(signUpRequestDto);
        return new BaseResponse<>(ErrorCode.SUCCESS);
    }

    @Operation(summary = "내 프로필 조회", description = "이메일, 사용자명, 프로필이미지 등을 조회합니다.")
    @GetMapping("/my-profile")
    public BaseResponse<GetProfileResponseDto> getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return new BaseResponse<>(authService.getMyProfile(userDetails.getUserId()));
    }

    @Operation(summary = "팀장의 모든 구성원 프로필 조회", description = "팀원들의 모든 이메일, 사용자명, 프로필이미지 등을 조회합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @GetMapping("/profile")
    public BaseResponse<List<GetProfileResponseDto>> getAllMemberProfiles(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return new BaseResponse<>(authService.getAllMemberProfiles(userDetails.getUserId()));
    }

    @Operation(summary = "내 프로필 변경", description = "프로필 변경입니다.")
    @PatchMapping(value = "/my-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> patchMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                  @RequestPart(value = "image", required = false) MultipartFile profileImage,
                                                                  @RequestPart(value = "text") @Valid PatchProfileRequestDto patchProfileRequestDto
    ) {
        authService.patchMyProfile(userDetails.getUserId(), patchProfileRequestDto, profileImage);
        return new BaseResponse<>(ErrorCode.NO_CONTENT);
    }
}
