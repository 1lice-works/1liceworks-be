package com.elice.iliceworksbe.team.service.impl;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.common.constant.Role;
import com.elice.iliceworksbe.common.constant.Status;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.team.TeamMemberDetailResponseDto;
import com.elice.iliceworksbe.team.dto.team.TeamMemberInfoUpdateDto;
import com.elice.iliceworksbe.team.dto.team.TeamMemberRequestDto;
import com.elice.iliceworksbe.team.dto.team.TeamMemberResponseDto;
import com.elice.iliceworksbe.team.entity.Employee;
import com.elice.iliceworksbe.team.entity.JobTitle;
import com.elice.iliceworksbe.team.entity.Position;
import com.elice.iliceworksbe.team.entity.UserType;
import com.elice.iliceworksbe.team.repository.EmployeeRepository;
import com.elice.iliceworksbe.team.service.JobTitleService;
import com.elice.iliceworksbe.team.service.PositionService;
import com.elice.iliceworksbe.team.service.TeamService;
import com.elice.iliceworksbe.team.service.UserTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    private final UserTypeService userTypeService;
    private final JobTitleService jobTitleService;
    private final PositionService positionService;


    /**
     * TODO 비밀번호 자동 생성 기능 붙이기
     */
    @Transactional
    @Override
    public TeamMemberResponseDto addMember(Long userId, TeamMemberRequestDto teamMemberRequestDto) {
        User teamLeader = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER));

        if (teamLeader.getRole() != Role.LEADER) {
            throw new BaseException(ErrorCode.ROLE_PERMISSION_DENIED);
        }

        if (userRepository.existsByAccountId(teamMemberRequestDto.accountId())){
            throw new BaseException(ErrorCode.DUPLICATED_ACCOUNTID);
        }

        User member = User.builder()
                .accountId(teamMemberRequestDto.accountId())
                .username(teamMemberRequestDto.userName())
                .password(teamMemberRequestDto.password())
                .isTeamCreated(true)
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .team(teamLeader.getTeam())
                .build();

        userRepository.save(member);

        UserType userType = userTypeService.getUserTypeByName(teamMemberRequestDto.userTypeName());
        Position position = positionService.getPositionByName(teamMemberRequestDto.positionName());
        JobTitle jobTitle = jobTitleService.getJobTileByName(teamMemberRequestDto.positionName());

        Employee employee = Employee.builder()
                .user(member)
                .userType(userType)
                .position(position)
                .jobTitle(jobTitle)
                .build();

        employeeRepository.save(employee);

        return TeamMemberResponseDto.from(member);
    }

    @Transactional
    @Override
    public TeamMemberDetailResponseDto updateMemberInfo(Long memberId, TeamMemberInfoUpdateDto teamMemberInfoUpdateDto) {
        User teamMember = userRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER));

        Employee employee = employeeRepository.findByUser(teamMember)
                .orElseThrow(() -> new BaseException(ErrorCode.EMPLOYEE_NOT_FOUND));

        if (teamMemberInfoUpdateDto.userName() != null && !teamMemberInfoUpdateDto.userName().isBlank()) {
            teamMember.updateUserName(teamMemberInfoUpdateDto.userName());
        }

        UserType userType = userTypeService.getUserTypeByName(teamMemberInfoUpdateDto.userTypeName());
        Position position = positionService.getPositionByName(teamMemberInfoUpdateDto.positionName());
        JobTitle jobTitle = jobTitleService.getJobTileByName(teamMemberInfoUpdateDto.jobTitleName());

        employee.updateEmployeeInfo(
                teamMemberInfoUpdateDto,
                jobTitle,
                position,
                userType
        );

        return TeamMemberDetailResponseDto.of(teamMember, employee);
    }
}
