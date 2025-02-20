package com.elice.iliceworksbe.team.dto.team;


import com.elice.iliceworksbe.auth.entity.User;

public record TeamMemberResponseDto(
        String userName,
        String accountId,
        String password
) {
    public static TeamMemberResponseDto from(User user) {
        return new TeamMemberResponseDto(user.getUsername(), user.getAccountId(), user.getPassword());
    }
}
