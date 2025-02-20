package com.elice.iliceworksbe.team.dto.team;

import com.elice.iliceworksbe.team.entity.Team;

public record TeamResponseDto(
        String companyName,
        String teamName,
        String domainName,
        String industry,
        String scale
) {
    public static TeamResponseDto from(Team team) {
        return new TeamResponseDto(
                team.getCompanyName(),
                team.getTeamName(),
                team.getDomainName(),
                team.getIndustry().getKoreanName(),
                team.getScale().getKoreanName()
        );
    }
}
