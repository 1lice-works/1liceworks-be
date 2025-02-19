package com.elice.iliceworksbe.team.web;

import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.common.exception.BaseResponse;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.userType.UserTypeRequestDto;
import com.elice.iliceworksbe.team.dto.userType.UserTypeResponseDto;
import com.elice.iliceworksbe.team.dto.userType.UserTypeUpdateDto;
import com.elice.iliceworksbe.team.service.UserTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-type")
public class UserTypeController {

    private final UserTypeService userTypeService;

    @PostMapping
    public BaseResponse<UserTypeResponseDto> postUserType(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserTypeRequestDto userTypeRequestDto) {
        UserTypeResponseDto postResponseDto = userTypeService.postUserType(userTypeRequestDto);
        return new BaseResponse<>(postResponseDto);
    }

    @GetMapping
    public BaseResponse<List<UserTypeResponseDto>> getAllUserTypes(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<UserTypeResponseDto> getResponseDtos = userTypeService.getAllUserTypes();
        return new BaseResponse<>(getResponseDtos);
    }

    @GetMapping("/{userTypeId}")
    public BaseResponse<UserTypeResponseDto> getUserType(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userTypeId) {
        UserTypeResponseDto getResponseDto = userTypeService.getUserType(userTypeId);
        return new BaseResponse<>(getResponseDto);
    }

    @PatchMapping("/{userTypeId}")
    public BaseResponse<UserTypeResponseDto> patchUserType(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userTypeId,
            @Valid @RequestBody UserTypeUpdateDto userTypeUpdateDto) {
        UserTypeResponseDto patchResponseDto = userTypeService.patchUserType(userTypeId, userTypeUpdateDto);
        return new BaseResponse<>(patchResponseDto);
    }

    @DeleteMapping("/{userTypeId}")
    public BaseResponse<String> deleteUserType(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userTypeId) {
        userTypeService.deleteUserType(userTypeId);
        return new BaseResponse<>(ErrorCode.NO_CONTENT);
    }
}
