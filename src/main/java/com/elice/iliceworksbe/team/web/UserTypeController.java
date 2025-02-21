package com.elice.iliceworksbe.team.web;

import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.common.exception.BaseResponse;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.userType.UserTypeRequestDto;
import com.elice.iliceworksbe.team.dto.userType.UserTypeResponseDto;
import com.elice.iliceworksbe.team.dto.userType.UserTypeUpdateDto;
import com.elice.iliceworksbe.team.service.UserTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-type")
@Tag(name = "UserType", description = "사용자 유형 관련 API 입니다.")

public class UserTypeController {

    private final UserTypeService userTypeService;

    @Operation(summary = "사용자 유형 생성", description = "사용자 유형을 생성합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @PostMapping
    public BaseResponse<UserTypeResponseDto> postUserType(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserTypeRequestDto userTypeRequestDto) {
        UserTypeResponseDto postResponseDto = userTypeService.postUserType(userTypeRequestDto);
        return new BaseResponse<>(postResponseDto);
    }

    @Operation(summary = "모든 사용자 유형 조회", description = "모든 사용자 유형을 조회합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @GetMapping
    public BaseResponse<List<UserTypeResponseDto>> getAllUserTypes(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<UserTypeResponseDto> getResponseDtos = userTypeService.getAllUserTypes();
        return new BaseResponse<>(getResponseDtos);
    }

    @Operation(summary = "단일 사용자 유형 조회", description = "사용자 유형을 조회합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @GetMapping("/{userTypeId}")
    public BaseResponse<UserTypeResponseDto> getUserType(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userTypeId) {
        UserTypeResponseDto getResponseDto = userTypeService.getUserType(userTypeId);
        return new BaseResponse<>(getResponseDto);
    }

    @Operation(summary = "사용자 유형 수정", description = "사용자 유형을 수정합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @PatchMapping("/{userTypeId}")
    public BaseResponse<UserTypeResponseDto> patchUserType(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userTypeId,
            @Valid @RequestBody UserTypeUpdateDto userTypeUpdateDto) {
        UserTypeResponseDto patchResponseDto = userTypeService.patchUserType(userTypeId, userTypeUpdateDto);
        return new BaseResponse<>(patchResponseDto);
    }

    @Operation(summary = "사용자 유형 삭제", description = "사용자 유형을 삭제합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @DeleteMapping("/{userTypeId}")
    public BaseResponse<String> deleteUserType(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userTypeId) {
        userTypeService.deleteUserType(userTypeId);
        return new BaseResponse<>(ErrorCode.NO_CONTENT);
    }
}
