package com.elice.iliceworksbe.auth.dto.request;

public record PatchMemberProfileRequestDto(
        String username,
        String userType,
        String position,
        String jobTitle,
        String responsibility,
        String employeeNumber
) {
}
