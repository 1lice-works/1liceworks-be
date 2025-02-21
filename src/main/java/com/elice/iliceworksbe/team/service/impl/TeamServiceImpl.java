package com.elice.iliceworksbe.team.service.impl;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.auth.repository.ArchivingUserRepository;
import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.calendar.entity.Calendar;
import com.elice.iliceworksbe.calendar.repository.CalendarRepository;
import com.elice.iliceworksbe.common.constant.CalendarType;
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
    private final CalendarRepository calendarRepository;

    private final PositionRepository positionRepository;
    private final JobTitleRepository jobTitleRepository;
    private final UserTypeRepository userTypeRepository;

    private final PasswordEncoder passwordEncoder;

    private static final String CALENDAR_NAME = "님의 캘린더";


    @Transactional
    @Override
    public TeamMemberResponseDto postMember(Long userId, TeamMemberRequestDto teamMemberRequestDto) {
        User teamLeader = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER));

        if (userRepository.existsByAccountId(teamMemberRequestDto.accountId())) {
            throw new BaseException(ErrorCode.DUPLICATED_ACCOUNTID);
        }

        String generatedPassword = PasswordGenerator.generatePassword();

        User member = addNewMember(teamMemberRequestDto, teamLeader, generatedPassword);

        userRepository.save(member);

        UserType userType = userTypeRepository.findByName(teamMemberRequestDto.userType())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER_TYPE));

        Position position = positionRepository.findByName(teamMemberRequestDto.position())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_POSITION));

        JobTitle jobTitle = jobTitleRepository.findByName(teamMemberRequestDto.jobTitle())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_JOB_TITLE));

        Employee employee = addNewEmployee(member, userType, position, jobTitle);

        employeeRepository.save(employee);

        Calendar memberCalendar = addNewCalendar(teamMemberRequestDto, member);

        calendarRepository.save(memberCalendar);

        return TeamMemberResponseDto.of(member, generatedPassword);
    }

    private Calendar addNewCalendar(TeamMemberRequestDto teamMemberRequestDto, User member) {
        return Calendar.builder()
                .name(makeCalendarName(teamMemberRequestDto.userName()))
                .type(CalendarType.MEMBER)
                .typeId(member.getId())
                .build();
    }

    private String makeCalendarName(String userName) {
        return userName + CALENDAR_NAME;
    }

    private Employee addNewEmployee(User member, UserType userType, Position position, JobTitle jobTitle) {
        return Employee.builder()
                .user(member)
                .userType(userType)
                .position(position)
                .jobTitle(jobTitle)
                .build();
    }

    private User addNewMember(TeamMemberRequestDto teamMemberRequestDto, User teamLeader, String generatedPassword) {
        return User.builder()
                .accountId(teamMemberRequestDto.accountId())
                .username(teamMemberRequestDto.userName())
                .password(passwordEncoder.encode(generatedPassword))
                .isTeamCreated(true)
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .team(teamLeader.getTeam())
                .build();
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

        patchEmployeeInfo(teamMemberInfoUpdateDto, employee, userType, position, jobTitle);

        return TeamMemberDetailResponseDto.of(memberUser, employee);
    }

    private void patchEmployeeInfo(TeamMemberInfoUpdateDto teamMemberInfoUpdateDto, Employee employee, UserType userType, Position position, JobTitle jobTitle) {
        employee.patchEmployeeInfo(
                teamMemberInfoUpdateDto,
                jobTitle,
                position,
                userType
        );
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
