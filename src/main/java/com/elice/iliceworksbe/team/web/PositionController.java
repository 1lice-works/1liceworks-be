package com.elice.iliceworksbe.team.web;

import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.common.exception.BaseResponse;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.position.PositionRequestDto;
import com.elice.iliceworksbe.team.dto.position.PositionResponseDto;
import com.elice.iliceworksbe.team.dto.position.PositionUpdateDto;
import com.elice.iliceworksbe.team.service.PositionService;
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
@RequestMapping("/api/position")
@Tag(name = "Position", description = "직급 관련 API 입니다.")

public class PositionController {

    private final PositionService positionService;

    @Operation(summary = "직급 생성", description = "직급을 생성합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @PostMapping
    public BaseResponse<PositionResponseDto> postPosition(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody PositionRequestDto positionRequestDto) {
        PositionResponseDto postResponseDto = positionService.postPosition(positionRequestDto);
        return new BaseResponse<>(postResponseDto);
    }

    @Operation(summary = "모든 직급 조회", description = "모든 직급을 조회합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @GetMapping
    public BaseResponse<List<PositionResponseDto>> getAllPositions(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<PositionResponseDto> getResponseDtos = positionService.getAllPositions();
        return new BaseResponse<>(getResponseDtos);
    }

    @Operation(summary = "단일 직급 조회", description = "직급을 조회합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @GetMapping("/{positionId}")
    public BaseResponse<PositionResponseDto> getPosition(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long positionId) {
        PositionResponseDto getResponseDto = positionService.getPosition(positionId);
        return new BaseResponse<>(getResponseDto);
    }

    @Operation(summary = "직급 수정", description = "직급을 수정합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @PatchMapping("/{positionId}")
    public BaseResponse<PositionResponseDto> patchPosition(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long positionId,
            @Valid @RequestBody PositionUpdateDto positionUpdateDto) {
        PositionResponseDto patchResponseDto = positionService.patchPosition(positionId, positionUpdateDto);
        return new BaseResponse<>(patchResponseDto);
    }

    @Operation(summary = "직급 삭제", description = "직급을 삭제합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @DeleteMapping("/{positionId}")
    public BaseResponse<String> deletePosition(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long positionId) {
        positionService.deletePosition(positionId);
        return new BaseResponse<>(ErrorCode.NO_CONTENT);
    }
}
