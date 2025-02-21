package com.elice.iliceworksbe.team.dto.team;


import com.elice.iliceworksbe.auth.entity.User;
import lombok.Builder;

@Builder
public record TeamMemberResponseDto(
        String userName,
        String accountId,
        String password
) {
    public static TeamMemberResponseDto of(User user, String password) {
        return TeamMemberResponseDto.builder()
                .userName(user.getUsername())
                .accountId(user.getAccountId())
                .password(password)
                .build();
    }
}
