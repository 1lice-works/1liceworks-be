package com.elice.iliceworksbe.team.dto.team;

import lombok.Builder;

@Builder
public record TeamMemberRequestDto(
        String userName,
        String accountId,
        String jobTitle,
        String position,
        String userType
) {
}
