package com.elice.iliceworksbe.team.service.impl;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.auth.repository.ArchivingUserRepository;
import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.common.constant.Role;
import com.elice.iliceworksbe.common.constant.Status;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.team.*;
import com.elice.iliceworksbe.team.entity.*;
import com.elice.iliceworksbe.team.repository.*;
import com.elice.iliceworksbe.team.service.TeamService;
import com.elice.iliceworksbe.team.utils.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final TeamRepository teamRepository;
    private final ArchivingUserRepository archivingUserRepository;

    private final PositionRepository positionRepository;
    private final JobTitleRepository jobTitleRepository;
    private final UserTypeRepository userTypeRepository;

    private final PasswordEncoder passwordEncoder;


    @Transactional
    @Override
    public TeamMemberResponseDto postMember(Long userId, TeamMemberRequestDto teamMemberRequestDto) {
        User teamLeader = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER));

        if (userRepository.existsByAccountId(teamMemberRequestDto.accountId())) {
            throw new BaseException(ErrorCode.DUPLICATED_ACCOUNTID);
        }

        String generatedPassword = PasswordGenerator.generatePassword();

        User member = User.builder()
                .accountId(teamMemberRequestDto.accountId())
                .username(teamMemberRequestDto.userName())
                .password(passwordEncoder.encode(generatedPassword))
                .isTeamCreated(true)
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .team(teamLeader.getTeam())
                .build();

        userRepository.save(member);

        UserType userType = userTypeRepository.findByName(teamMemberRequestDto.userType())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER_TYPE));

        Position position = positionRepository.findByName(teamMemberRequestDto.position())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_POSITION));

        JobTitle jobTitle = jobTitleRepository.findByName(teamMemberRequestDto.jobTitle())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_JOB_TITLE));

        Employee employee = Employee.builder()
                .user(member)
                .userType(userType)
                .position(position)
                .jobTitle(jobTitle)
                .build();

        employeeRepository.save(employee);

        return TeamMemberResponseDto.of(member, generatedPassword);
    }
    @Transactional
    @Override
    public void deleteMember(Long leaderUserId, Long memberUserId) {
        Team team = userRepository.findById(leaderUserId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER)).getTeam();

        User memberUser = userRepository.findById(memberUserId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER));

        if (!memberUser.getTeam().equals(team)) {
            throw new BaseException(ErrorCode.WRONG_AUTHORIZATION);
        }
        archivingUserRepository.save(memberUser.toArchivingUser());

        userRepository.delete(memberUser);

    }
    @Transactional
    @Override
    public void pauseMember(Long leaderUserId, Long memberUserId) {
        Team team = userRepository.findById(leaderUserId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER)).getTeam();

        User memberUser = userRepository.findById(memberUserId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER));

        if (!memberUser.getTeam().equals(team)) {
            throw new BaseException(ErrorCode.WRONG_AUTHORIZATION);
        }

        memberUser.setUserStatus(Status.INACTIVE);
    }

    @Transactional
    @Override
    public TeamMemberDetailResponseDto patchMemberInfo(Long leaderUserId, Long memberUserId, TeamMemberInfoUpdateDto teamMemberInfoUpdateDto) {
        Team team = userRepository.findById(leaderUserId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER)).getTeam();

        User memberUser = userRepository.findById(memberUserId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER));

        if (!memberUser.getTeam().equals(team)) {
            throw new BaseException(ErrorCode.WRONG_AUTHORIZATION);
        }

        Employee employee = employeeRepository.findEmployeeByUser(memberUser).orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER));

        UserType userType = userTypeRepository.findByName(teamMemberInfoUpdateDto.userType())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER_TYPE));

        Position position = positionRepository.findByName(teamMemberInfoUpdateDto.position())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_POSITION));

        JobTitle jobTitle = jobTitleRepository.findByName(teamMemberInfoUpdateDto.jobTitle())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_JOB_TITLE));

        memberUser.patchUsername(teamMemberInfoUpdateDto.userName());
        employee.patchEmployeeInfo(
                teamMemberInfoUpdateDto,
                jobTitle,
                position,
                userType
        );
        return TeamMemberDetailResponseDto.of(memberUser, employee);
    }

    @Transactional
    @Override
    public TeamResponseDto patchTeamInfo(Long leaderUserId, Long teamId, TeamInfoUpdateDto teamInfoUpdateDto) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BaseException(ErrorCode.TEAM_NOT_FOUND));

        User teamLeader = userRepository.findById(leaderUserId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER));

        if (!teamLeader.getTeam().equals(team)) {
            throw new BaseException(ErrorCode.WRONG_AUTHORIZATION);
        }

        team.updateTeamInfo(teamInfoUpdateDto);

        return TeamResponseDto.from(team);
    }

}
