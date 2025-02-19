package com.elice.iliceworksbe.team.service.impl;

import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleRequestDto;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleResponseDto;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleUpdateDto;
import com.elice.iliceworksbe.team.entity.JobTitle;
import com.elice.iliceworksbe.team.repository.JobTitleRepository;
import com.elice.iliceworksbe.team.service.JobTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobTitleServiceImpl implements JobTitleService {

    private final JobTitleRepository jobTitleRepository;

    @Transactional
    @Override
    public JobTitleResponseDto postJobTitle(JobTitleRequestDto jobTitleRequestDto) {
        JobTitle savedJobTitle = jobTitleRepository.save(JobTitle.from(jobTitleRequestDto));
        return JobTitleResponseDto.from(savedJobTitle);
    }

    @Override
    public JobTitleResponseDto getJobTitle(Long jobTitleId) {
        JobTitle findedJobTitle = jobTitleRepository.findById(jobTitleId)
                .orElseThrow(() -> new BaseException(ErrorCode.JOB_TITLE_NOT_FOUND));
        return JobTitleResponseDto.from(findedJobTitle);
    }

    @Override
    public List<JobTitleResponseDto> getAllJobTitles() {
        return jobTitleRepository.findAll()
                .stream()
                .map(JobTitleResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public JobTitleResponseDto patchJobTitle(Long jobTitleId, JobTitleUpdateDto jobTitleUpdateDto) {
        JobTitle findedJobTitle = jobTitleRepository.findById(jobTitleId)
                .orElseThrow(() -> new BaseException(ErrorCode.JOB_TITLE_NOT_FOUND));

        findedJobTitle.update(jobTitleUpdateDto);

        JobTitle updatedJobTitle = jobTitleRepository.save(findedJobTitle);
        return JobTitleResponseDto.from(updatedJobTitle);
    }

    @Transactional
    @Override
    public void deleteJobTitle(Long jobTitleId) {
        jobTitleRepository.deleteById(jobTitleId);
    }

    @PostConstruct
    public void init() {
        JobTitle jobTitle = JobTitle.builder()
                .name("없음")
                .build();

        jobTitleRepository.save(jobTitle);
    }
}
