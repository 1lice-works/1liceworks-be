package com.elice.iliceworksbe.team.dto.jobTitle;


import com.elice.iliceworksbe.team.entity.JobTitle;

public record JobTitleResponseDto(Long id, String name) {
    public static JobTitleResponseDto from(JobTitle jobTitle) {
        return new JobTitleResponseDto(jobTitle.getId(), jobTitle.getName());
    }
}
