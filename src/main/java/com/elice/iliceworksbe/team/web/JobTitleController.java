package com.elice.iliceworksbe.team.web;

import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.common.exception.BaseResponse;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleRequestDto;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleResponseDto;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleUpdateDto;
import com.elice.iliceworksbe.team.service.JobTitleService;
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
@RequestMapping("/api/job-title")
@Tag(name = "JobTitle", description = "직책 관련 API 입니다.")
public class JobTitleController {
    
    private final JobTitleService jobTitleService;

    @Operation(summary = "직책 생성", description = "직책을 생성합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @PostMapping
    public BaseResponse<JobTitleResponseDto> postJobTitle(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody JobTitleRequestDto jobTitleRequestDto) {
        JobTitleResponseDto postResponseDto = jobTitleService.postJobTitle(jobTitleRequestDto);
        return new BaseResponse<>(postResponseDto);
    }

    @Operation(summary = "모든 직책 조회", description = "모든 직책을 조회합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @GetMapping
    public BaseResponse<List<JobTitleResponseDto>> getAllJobTitles(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<JobTitleResponseDto> getResponseDtos = jobTitleService.getAllJobTitles();
        return new BaseResponse<>(getResponseDtos);
    }

    @Operation(summary = "단일 직책 조회", description = "직책을 조회합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @GetMapping("/{jobTitleId}")
    public BaseResponse<JobTitleResponseDto> getJobTitle(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long jobTitleId) {
        JobTitleResponseDto getResponseDto = jobTitleService.getJobTitle(jobTitleId);
        return new BaseResponse<>(getResponseDto);
    }

    @Operation(summary = "직책 수정", description = "직책을 수정합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @PatchMapping("/{jobTitleId}")
    public BaseResponse<JobTitleResponseDto> patchJobTitle(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long jobTitleId,
            @Valid @RequestBody JobTitleUpdateDto jobTitleUpdateDto) {
        JobTitleResponseDto patchResponseDto = jobTitleService.patchJobTitle(jobTitleId, jobTitleUpdateDto);
        return new BaseResponse<>(patchResponseDto);
    }

    @Operation(summary = "직책 삭제", description = "직책을 삭제합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @DeleteMapping("/{jobTitleId}")
    public BaseResponse<String> deleteJobTitle(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long jobTitleId) {
        jobTitleService.deleteJobTitle(jobTitleId);
        return new BaseResponse<>(ErrorCode.NO_CONTENT);
    }
}
