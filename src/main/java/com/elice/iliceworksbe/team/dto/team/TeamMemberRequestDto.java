package com.elice.iliceworksbe.team.dto.team;

public record TeamMemberRequestDto(
        String userName,
        String accountId,
        String password,
        String jobTitleName,
        String positionName,
        String userTypeName
) {
}
