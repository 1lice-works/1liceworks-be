package com.elice.iliceworksbe.team.dto.team;

public record TeamMemberInfoUpdateDto(
        String userName,
        String jobTitle,
        String position,
        String userType,
        String responsibility,
        String employeeNumber
) {
}
