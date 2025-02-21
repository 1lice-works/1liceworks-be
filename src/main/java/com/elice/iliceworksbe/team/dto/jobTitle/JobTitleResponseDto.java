package com.elice.iliceworksbe.team.dto.jobTitle;


import com.elice.iliceworksbe.team.entity.JobTitle;
import lombok.Builder;

@Builder
public record JobTitleResponseDto(Long id, String name) {
    public static JobTitleResponseDto from(JobTitle jobTitle) {
        return JobTitleResponseDto.builder()
                .id(jobTitle.getId())
                .name(jobTitle.getName())
                .build();
    }
}