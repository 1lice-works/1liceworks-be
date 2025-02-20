package com.elice.iliceworksbe.team.service;

import com.elice.iliceworksbe.team.dto.team.*;

public interface TeamService {
    TeamMemberResponseDto postMember(Long userId, TeamMemberRequestDto teamMemberRequestDto);
    TeamMemberDetailResponseDto patchMemberInfo(Long leaderUserId, Long memberId, TeamMemberInfoUpdateDto teamMemberInfoUpdateDto);
    TeamResponseDto patchTeamInfo(Long teamId, TeamInfoUpdateDto teamInfoUpdateDto);
}
