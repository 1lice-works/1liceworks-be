package com.elice.iliceworksbe.team.service;

import com.elice.iliceworksbe.team.dto.team.*;

public interface TeamService {
    TeamMemberResponseDto addMember(Long userId, TeamMemberRequestDto teamMemberRequestDto);
    TeamMemberDetailResponseDto updateMemberInfo(Long userId, Long memberId, TeamMemberInfoUpdateDto teamMemberInfoUpdateDto);
    TeamResponseDto updateTeamInfo(Long userId, Long teamId, TeamInfoUpdateDto teamInfoUpdateDto);
}
