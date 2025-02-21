package com.elice.iliceworksbe.team.service;

import com.elice.iliceworksbe.team.dto.team.*;

public interface TeamService {
    TeamMemberResponseDto postMember(Long userId, TeamMemberRequestDto teamMemberRequestDto);

    void deleteMember(Long leaderUserId, Long memberUserId);
    void pauseMember(Long leaderUserId, Long memberUserId);
    TeamMemberDetailResponseDto patchMemberInfo(Long leaderUserId, Long memberUserId, TeamMemberInfoUpdateDto teamMemberInfoUpdateDto);
    TeamResponseDto patchTeamInfo(Long leaderUserId, Long teamId, TeamInfoUpdateDto teamInfoUpdateDto);
}
