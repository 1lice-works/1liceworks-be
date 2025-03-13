package com.elice.iliceworksbe.team.dto.team;

import com.elice.iliceworksbe.team.entity.Team;
import lombok.Builder;

@Builder
public record TeamResponseDto(
        String companyName,
        String teamName,
        String domainName,
        String industry,
        String scale
) {
    public static TeamResponseDto from(Team team) {
        return TeamResponseDto.builder()
                .companyName(team.getCompanyName())
                .teamName(team.getTeamName())
                .domainName(team.getDomainName())
                .industry(team.getIndustry().getKoreanName())
                .scale(team.getScale().getKoreanName())
                .build();
    }
}
