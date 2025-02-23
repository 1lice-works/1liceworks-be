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


    @Transactional
    @Override
    public TeamMemberResponseDto postMember(Long userId, TeamMemberRequestDto teamMemberRequestDto) {
        User teamLeader = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));

        if (userRepository.existsByAccountId(teamMemberRequestDto.accountId())) {
            throw new BaseException(ErrorCode.DUPLICATED_ACCOUNT_ID);
        }

        String generatedPassword = PasswordGenerator.generatePassword();
        User member = addNewMember(teamMemberRequestDto, teamLeader, generatedPassword);
        userRepository.save(member);

        UserType userType = findUserTypeByName(teamMemberRequestDto.userType());
        Position position = findPositionByName(teamMemberRequestDto.position());
        JobTitle jobTitle = findJobTitleByName(teamMemberRequestDto.jobTitle());

        Employee employee = addNewEmployee(member, userType, position, jobTitle);
        employeeRepository.save(employee);

        Calendar memberCalendar = addNewCalendar(teamMemberRequestDto, member);
        calendarRepository.save(memberCalendar);

        return TeamMemberResponseDto.of(member, generatedPassword);
    }

    private Calendar addNewCalendar(TeamMemberRequestDto teamMemberRequestDto, User member) {
        return Calendar.builder()
                .name(makeCalendarName(teamMemberRequestDto))
                .type(CalendarType.MEMBER)
                .typeId(member.getId())
                .build();
    }

    private String makeCalendarName(TeamMemberRequestDto dto) {
        return dto.userName() + " / " + dto.position();
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
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER)).getTeam();

        User memberUser = userRepository.findById(memberUserId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));

        validateMemberBelongsToTeam(team, memberUser);

        archivingUserRepository.save(memberUser.toArchivingUser());
        userRepository.delete(memberUser);
    }

    @Transactional
    @Override
    public void pauseMember(Long leaderUserId, Long memberUserId) {
        Team team = userRepository.findById(leaderUserId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER)).getTeam();

        User memberUser = userRepository.findById(memberUserId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));

        validateMemberBelongsToTeam(team, memberUser);

        memberUser.setUserStatus(Status.INACTIVE);
    }

    @Transactional
    @Override
    public TeamMemberDetailResponseDto patchMemberInfo(Long leaderUserId, Long memberUserId, TeamMemberInfoUpdateDto teamMemberInfoUpdateDto) {
        Team team = userRepository.findById(leaderUserId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER)).getTeam();

        User memberUser = userRepository.findById(memberUserId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));

        validateMemberBelongsToTeam(team, memberUser);

        Employee employee = employeeRepository.findEmployeeByUser(memberUser)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));

        UserType userType = findUserTypeByName(teamMemberInfoUpdateDto.userType());
        Position position = findPositionByName(teamMemberInfoUpdateDto.position());
        JobTitle jobTitle = findJobTitleByName(teamMemberInfoUpdateDto.jobTitle());

        memberUser.patchUsername(teamMemberInfoUpdateDto.userName());

        patchEmployeeInfo(teamMemberInfoUpdateDto, employee, userType, position, jobTitle);

        return TeamMemberDetailResponseDto.of(memberUser, employee);
    }

    private void patchEmployeeInfo(TeamMemberInfoUpdateDto teamMemberInfoUpdateDto,
                                   Employee employee, UserType userType, Position position, JobTitle jobTitle) {
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
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_TEAM));

        User teamLeader = userRepository.findById(leaderUserId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));

        validateMemberBelongsToTeam(team, teamLeader);

        team.updateTeamInfo(teamInfoUpdateDto);

        return TeamResponseDto.from(team);
    }

    private static void validateMemberBelongsToTeam(Team team, User memberUser) {
        if (!memberUser.getTeam().equals(team)) {
            throw new BaseException(ErrorCode.INVALID_AUTHORIZATION);
        }
    }

    private UserType findUserTypeByName(String name) {
        return userTypeRepository.findByName(name.trim())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER_TYPE));
    }

    private Position findPositionByName(String name) {
        return positionRepository.findByName(name.trim())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_POSITION));
    }

    private JobTitle findJobTitleByName(String name) {
        return jobTitleRepository.findByName(name.trim())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_JOB_TITLE));
    }
}
