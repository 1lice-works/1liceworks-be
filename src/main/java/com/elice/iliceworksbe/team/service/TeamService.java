package com.elice.iliceworksbe.team.service;

import com.elice.iliceworksbe.team.dto.team.TeamMemberDetailResponseDto;
import com.elice.iliceworksbe.team.dto.team.TeamMemberInfoUpdateDto;
import com.elice.iliceworksbe.team.dto.team.TeamMemberRequestDto;
import com.elice.iliceworksbe.team.dto.team.TeamMemberResponseDto;

public interface TeamService {
    TeamMemberResponseDto addMember(Long teamLeaderId, TeamMemberRequestDto teamMemberRequestDto);
    TeamMemberDetailResponseDto updateMemberInfo(Long memberId, TeamMemberInfoUpdateDto teamMemberInfoUpdateDto);
}
