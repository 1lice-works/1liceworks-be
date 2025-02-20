package com.elice.iliceworksbe.team.dto.team;


import com.elice.iliceworksbe.auth.entity.User;
import lombok.Builder;

@Builder
public record TeamMemberResponseDto(
        String userName,
        String accountId,
        String password
) {
    public static TeamMemberResponseDto from(User user) {
        return TeamMemberResponseDto.builder()
                .userName(user.getUsername())
                .accountId(user.getAccountId())
                .password(user.getPassword())
                .build();
    }
}
