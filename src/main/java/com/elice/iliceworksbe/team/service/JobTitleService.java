package com.elice.iliceworksbe.team.service;

import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleRequestDto;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleResponseDto;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleUpdateDto;

import java.util.List;

public interface JobTitleService {
    JobTitleResponseDto postJobTitle(JobTitleRequestDto jobTitleRequestDto);
    JobTitleResponseDto getJobTitle(Long jobTitleId);
    List<JobTitleResponseDto> getAllJobTitles();
    JobTitleResponseDto patchJobTitle(Long jobTitleId, JobTitleUpdateDto jobTitleUpdateDto);
    void deleteJobTitle(Long jobTitleId);
}
