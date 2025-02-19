package com.elice.iliceworksbe.team.web;

import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.common.exception.BaseResponse;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.position.PositionRequestDto;
import com.elice.iliceworksbe.team.dto.position.PositionResponseDto;
import com.elice.iliceworksbe.team.dto.position.PositionUpdateDto;
import com.elice.iliceworksbe.team.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/position")
public class PositionController {

    private final PositionService positionService;

    @PostMapping
    public BaseResponse<PositionResponseDto> postPosition(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody PositionRequestDto positionRequestDto) {
        PositionResponseDto postResponseDto = positionService.postPosition(positionRequestDto);
        return new BaseResponse<>(postResponseDto);
    }

    @GetMapping
    public BaseResponse<List<PositionResponseDto>> getAllPositions(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<PositionResponseDto> getResponseDtos = positionService.getAllPositions();
        return new BaseResponse<>(getResponseDtos);
    }

    @GetMapping("/{positionId}")
    public BaseResponse<PositionResponseDto> getPosition(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long positionId) {
        PositionResponseDto getResponseDto = positionService.getPosition(positionId);
        return new BaseResponse<>(getResponseDto);
    }

    @PatchMapping("/{positionId}")
    public BaseResponse<PositionResponseDto> patchPosition(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long positionId,
            @Valid @RequestBody PositionUpdateDto positionUpdateDto) {
        PositionResponseDto patchResponseDto = positionService.patchPosition(positionId, positionUpdateDto);
        return new BaseResponse<>(patchResponseDto);
    }

    @DeleteMapping("/{positionId}")
    public BaseResponse<String> deletePosition(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long positionId) {
        positionService.deletePosition(positionId);
        return new BaseResponse<>(ErrorCode.NO_CONTENT);
    }
}
