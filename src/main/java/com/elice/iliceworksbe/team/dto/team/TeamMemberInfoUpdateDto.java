package com.elice.iliceworksbe.team.dto.team;

public record TeamMemberInfoUpdateDto(
        String userName,
        String jobTitleName,
        String positionName,
        String userTypeName,
        String responsibility,
        String employeeNumber
) {
}
