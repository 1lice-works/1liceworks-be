package com.elice.iliceworksbe.team.web;

import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.common.exception.BaseResponse;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.team.*;
import com.elice.iliceworksbe.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team")
@Tag(name = "Team", description = "팀 관련 API 입니다.")

public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "팀원 생성", description = "팀장이 팀원을 생성합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @PostMapping("/member")
    public BaseResponse<TeamMemberResponseDto> postMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody TeamMemberRequestDto teamMemberRequestDto) {
        TeamMemberResponseDto teamMemberResponseDto = teamService.postMember(userDetails.getUserId(), teamMemberRequestDto);
        return new BaseResponse<>(teamMemberResponseDto);
    }

    @Operation(summary = "팀원 삭제", description = "팀장이 팀원을 삭제하고 아카이빙 유저 테이블에 팀원을 저장합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @DeleteMapping("/member/{memberId}")
    public BaseResponse<String> deleteMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long memberId) {
        teamService.deleteMember(userDetails.getUserId(), memberId);
        return new BaseResponse<>(ErrorCode.NO_CONTENT);
    }

    @Operation(summary = "팀원 상태 일시정지", description = "팀장이 팀원의 상태를 일시정지로 변경합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @PatchMapping("/member/{memberId}/pause")
    public BaseResponse<String> pauseMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long memberId) {
        teamService.pauseMember(userDetails.getUserId(), memberId);
        return new BaseResponse<>(ErrorCode.NO_CONTENT);
    }

    @Operation(summary = "팀원 수정", description = "팀장이 팀원 정보를 수정합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @PatchMapping("/member/{memberId}")
    public BaseResponse<TeamMemberDetailResponseDto> patchMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long memberId,
            @RequestBody TeamMemberInfoUpdateDto teamMemberInfoUpdateDto) {
        TeamMemberDetailResponseDto teamMemberDetailResponseDto = teamService.patchMemberInfo(userDetails.getUserId(), memberId, teamMemberInfoUpdateDto);
        return new BaseResponse<>(teamMemberDetailResponseDto);
    }

    @Operation(summary = "팀 정보 수정", description = "팀장이 팀 정보를 수정합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @PatchMapping("/{teamId}")
    public BaseResponse<TeamResponseDto> patchTeam(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long teamId,
            @RequestBody TeamInfoUpdateDto teamInfoUpdateDto) {
        TeamResponseDto teamResponseDto = teamService.patchTeamInfo(userDetails.getUserId(), teamId, teamInfoUpdateDto);
        return new BaseResponse<>(teamResponseDto);
    }
}
