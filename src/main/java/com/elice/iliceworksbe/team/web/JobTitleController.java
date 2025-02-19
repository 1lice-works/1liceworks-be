package com.elice.iliceworksbe.team.web;

import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.common.exception.BaseResponse;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleRequestDto;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleResponseDto;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleUpdateDto;
import com.elice.iliceworksbe.team.service.JobTitleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job-title")
public class JobTitleController {
    
    private final JobTitleService jobTitleService;

    @PostMapping
    public BaseResponse<JobTitleResponseDto> postJobTitle(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody JobTitleRequestDto jobTitleRequestDto) {
        JobTitleResponseDto postResponseDto = jobTitleService.postJobTitle(jobTitleRequestDto);
        return new BaseResponse<>(postResponseDto);
    }

    @GetMapping
    public BaseResponse<List<JobTitleResponseDto>> getAllJobTitles(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<JobTitleResponseDto> getResponseDtos = jobTitleService.getAllJobTitles();
        return new BaseResponse<>(getResponseDtos);
    }

    @GetMapping("/{jobTitleId}")
    public BaseResponse<JobTitleResponseDto> getJobTitle(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long jobTitleId) {
        JobTitleResponseDto getResponseDto = jobTitleService.getJobTitle(jobTitleId);
        return new BaseResponse<>(getResponseDto);
    }

    @PatchMapping("/{jobTitleId}")
    public BaseResponse<JobTitleResponseDto> patchJobTitle(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long jobTitleId,
            @Valid @RequestBody JobTitleUpdateDto jobTitleUpdateDto) {
        JobTitleResponseDto patchResponseDto = jobTitleService.patchJobTitle(jobTitleId, jobTitleUpdateDto);
        return new BaseResponse<>(patchResponseDto);
    }

    @DeleteMapping("/{jobTitleId}")
    public BaseResponse<String> deleteJobTitle(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long jobTitleId) {
        jobTitleService.deleteJobTitle(jobTitleId);
        return new BaseResponse<>(ErrorCode.NO_CONTENT);
    }
}
