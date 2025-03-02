package com.elice.iliceworksbe.team.service.impl;

import com.elice.iliceworksbe.auth.entity.ArchivingUser;
import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.auth.repository.ArchivingUserRepository;
import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.calendar.entity.Calendar;
import com.elice.iliceworksbe.calendar.repository.CalendarRepository;
import com.elice.iliceworksbe.common.constant.Role;
import com.elice.iliceworksbe.common.constant.Status;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.constant.Industry;
import com.elice.iliceworksbe.team.constant.Scale;
import com.elice.iliceworksbe.team.dto.team.*;
import com.elice.iliceworksbe.team.entity.*;
import com.elice.iliceworksbe.team.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ArchivingUserRepository archivingUserRepository;

    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private JobTitleRepository jobTitleRepository;

    @Mock
    private UserTypeRepository userTypeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TeamServiceImpl teamService;

    private User teamBeLeader;
    private User teamBeMember;
    private User teamFeLeader;
    private User teamFeMember;
    private Team teamBe;
    private Team teamFe;
    private Employee soogyeongEmployee;
    private Employee kyungjunEmployee;
    private Position position;
    private JobTitle jobTitle;
    private UserType userType;

    @BeforeEach
    void setUp() {
        teamBe = Team.builder()
                .id(1L)
                .companyName("일리스웍스 컴퍼니")
                .teamName("일리스 BE팀")
                .domainName("threadly.ilice-works.com")
                .hasPrivateDomain(true)
                .industry(Industry.CONSTRUCTION)
                .scale(Scale.TEN_TO_NINETEEN)
                .build();

        teamFe = Team.builder()
                .id(2L)
                .companyName("일리스웍스 컴퍼니")
                .teamName("일리스 FE팀")
                .domainName("threadly.ilice-works.com")
                .hasPrivateDomain(true)
                .industry(Industry.CONSTRUCTION)
                .scale(Scale.TEN_TO_NINETEEN)
                .build();


        jobTitle = JobTitle.builder().name("일반직").build();
        position = Position.builder().name("사원").build();
        userType = UserType.builder().name("정규직").build();

        teamBeLeader = User.builder()
                .id(1L)
                .accountId("hi563@threadly.ilice-works.com")
                .username("정태승")
                .password(passwordEncoder.encode("!a12345678"))
                .privateEmail("hi563@naver.com")
                .role(Role.LEADER)
                .status(Status.ACTIVE)
                .team(teamBe)
                .isTeamCreated(true)
                .build();

        teamFeLeader = User.builder()
                .id(2L)
                .accountId("hyerim@threadly.ilice-works.com")
                .username("양혜림")
                .password(passwordEncoder.encode("!a12345678"))
                .privateEmail("hyerim@naver.com")
                .role(Role.LEADER)
                .status(Status.ACTIVE)
                .team(teamFe)
                .isTeamCreated(true)
                .build();

        teamBeMember = User.builder()
                .id(3L)
                .accountId("soogyeong@threadly.ilice-works.com")
                .username("엄수경")
                .password(passwordEncoder.encode("!a12345678"))
                .privateEmail("soogyeong@naver.com")
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .team(teamBe)
                .isTeamCreated(true)
                .build();

        teamFeMember = User.builder()
                .id(4L)
                .accountId("kyungjun@threadly.ilice-works.com")
                .username("정경준")
                .password(passwordEncoder.encode("!a12345678"))
                .privateEmail("kyungjun@naver.com")
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .team(teamFe)
                .isTeamCreated(true)
                .build();

        soogyeongEmployee = Employee.builder()
                .employeeNumber("24-12347")
                .hireDate(LocalDateTime.now())
                .position(position)
                .jobTitle(jobTitle)
                .userType(userType)
                .responsibility("백엔드 개발")
                .user(teamBeMember)
                .build();

        kyungjunEmployee = Employee.builder()
                .employeeNumber("24-12347")
                .hireDate(LocalDateTime.now())
                .position(position)
                .jobTitle(jobTitle)
                .userType(userType)
                .responsibility("프론트 개발")
                .user(teamFeMember)
                .build();

    }

    @Test
    void 멤버_생성_성공() {
        // Given
        Long teamLeaderId = teamBeLeader.getId();

        TeamMemberRequestDto requestDto = TeamMemberRequestDto.builder()
                .userName("명광호")
                .accountId("kwangho@threadly.ilice-works.com")
                .jobTitle("일반직")
                .position("사원")
                .userType("정규직")
                .build();

        given(userRepository.findById(teamLeaderId)).willReturn(Optional.of(teamBeLeader));
        given(userRepository.existsByAccountId(requestDto.accountId())).willReturn(false);
        given(userTypeRepository.findByName(requestDto.userType())).willReturn(Optional.of(userType));
        given(positionRepository.findByName(requestDto.position())).willReturn(Optional.of(position));
        given(jobTitleRepository.findByName(requestDto.jobTitle())).willReturn(Optional.of(jobTitle));

        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(employeeRepository.save(any(Employee.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(calendarRepository.save(any(Calendar.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        TeamMemberResponseDto responseDto = teamService.postMember(teamLeaderId, requestDto);

        // Then
        assertNotNull(responseDto);
        assertEquals(requestDto.userName(), responseDto.userName());
        assertEquals(requestDto.accountId(), responseDto.accountId());

        verify(userRepository, times(1)).save(any(User.class));
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(calendarRepository, times(1)).save(any(Calendar.class));
    }

    @Test
    void 멤버_생성_실패_중복된_유저아이디() {
        // given
        Long teamLeaderId = teamBeLeader.getId();

        TeamMemberRequestDto requestDto = TeamMemberRequestDto.builder()
                .userName("명광호")
                .accountId("kwangho@threadly.ilice-works.com")
                .jobTitle("일반직")
                .position("사원")
                .userType("정규직")
                .build();

        given(userRepository.findById(teamLeaderId)).willReturn(Optional.of(teamBeLeader));
        given(userRepository.existsByAccountId(requestDto.accountId())).willReturn(true);

        // when & then

        assertThatThrownBy(() -> teamService.postMember(teamLeaderId, requestDto))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.DUPLICATED_ACCOUNT_ID.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test // jobTitle
    void 멤버_생성_실패_존재하지않는_직책() {
        // given
        Long teamLeaderId = teamBeLeader.getId();

        TeamMemberRequestDto requestDto = TeamMemberRequestDto.builder()
                .userName("명광호")
                .accountId("kwangho@threadly.ilice-works.com")
                .jobTitle("일반직")
                .position("사원")
                .userType("정규직")
                .build();

        given(userRepository.findById(teamLeaderId)).willReturn(Optional.of(teamBeLeader));
        given(userRepository.existsByAccountId(requestDto.accountId())).willReturn(false);

        lenient().when(positionRepository.findByName(requestDto.position())).thenReturn(Optional.of(position));
        lenient().when(userTypeRepository.findByName(requestDto.userType())).thenReturn(Optional.of(userType));
        lenient().when(jobTitleRepository.findByName(requestDto.jobTitle())).thenReturn(Optional.empty());

        // when & then

        assertThatThrownBy(() -> teamService.postMember(teamLeaderId, requestDto))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.NOT_FOUND_JOB_TITLE.getMessage());

        verify(userRepository, times(1)).save(any(User.class));
        verify(employeeRepository, never()).save(any(Employee.class));
        verify(calendarRepository, never()).save(any(Calendar.class));
    }

    @Test // position
    void 멤버_생성_실패_존재하지않는_직급() {
        // given
        Long teamLeaderId = teamBeLeader.getId();

        TeamMemberRequestDto requestDto = TeamMemberRequestDto.builder()
                .userName("명광호")
                .accountId("kwangho@threadly.ilice-works.com")
                .jobTitle("일반직")
                .position("사원")
                .userType("정규직")
                .build();

        given(userRepository.findById(teamLeaderId)).willReturn(Optional.of(teamBeLeader));
        given(userRepository.existsByAccountId(requestDto.accountId())).willReturn(false);

        lenient().when(positionRepository.findByName(requestDto.position())).thenReturn(Optional.empty());
        lenient().when(userTypeRepository.findByName(requestDto.userType())).thenReturn(Optional.of(userType));
        lenient().when(jobTitleRepository.findByName(requestDto.jobTitle())).thenReturn(Optional.of(jobTitle));


        // when & then

        assertThatThrownBy(() -> teamService.postMember(teamLeaderId, requestDto))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.NOT_FOUND_POSITION.getMessage());

        verify(userRepository, times(1)).save(any(User.class));
        verify(employeeRepository, never()).save(any(Employee.class));
        verify(calendarRepository, never()).save(any(Calendar.class));
    }

    @Test // userType
    void 멤버_생성_실패_존재하지않는_유형() {
        // given
        Long teamLeaderId = teamBeLeader.getId();

        TeamMemberRequestDto requestDto = TeamMemberRequestDto.builder()
                .userName("명광호")
                .accountId("kwangho@threadly.ilice-works.com")
                .jobTitle("일반직")
                .position("사원")
                .userType("정규직")
                .build();

        given(userRepository.findById(teamLeaderId)).willReturn(Optional.of(teamBeLeader));
        given(userRepository.existsByAccountId(requestDto.accountId())).willReturn(false);

        lenient().when(positionRepository.findByName(requestDto.position())).thenReturn(Optional.of(position));
        lenient().when(userTypeRepository.findByName(requestDto.userType())).thenReturn(Optional.empty());
        lenient().when(jobTitleRepository.findByName(requestDto.jobTitle())).thenReturn(Optional.of(jobTitle));

        // when & then

        assertThatThrownBy(() -> teamService.postMember(teamLeaderId, requestDto))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.NOT_FOUND_USER_TYPE.getMessage());

        verify(userRepository, times(1)).save(any(User.class));
        verify(employeeRepository, never()).save(any(Employee.class));
        verify(calendarRepository, never()).save(any(Calendar.class));
    }

    @Test
    void 멤버_삭제_성공() {
        // given
        Long teamLeaderId = teamBeLeader.getId();
        Long teamMemberId = teamBeMember.getId();

        given(userRepository.findById(teamLeaderId)).willReturn(Optional.of(teamBeLeader));
        given(userRepository.findById(teamMemberId)).willReturn(Optional.of(teamBeMember));

        // when
        teamService.deleteMember(teamLeaderId, teamMemberId);

        // then
        verify(archivingUserRepository, times(1)).save(any(ArchivingUser.class));
        verify(userRepository, times(1)).delete(any(User.class));

    }

    @Test
    void 멤버_삭제_실패_존재하지않는_유저() {
        // given
        Long teamLeaderId = teamBeLeader.getId();
        Long teamMemberId = 100L;

        given(userRepository.findById(teamLeaderId)).willReturn(Optional.of(teamBeLeader));
        given(userRepository.findById(teamMemberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> teamService.deleteMember(teamLeaderId, teamMemberId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.NOT_FOUND_USER.getMessage());

        verify(archivingUserRepository, never()).save(any(ArchivingUser.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void 멤버_삭제_실패_유저가_팀에_속하지_않음() {
        // given
        Long teamBeLeaderId = teamBeLeader.getId();
        Long teamFeMemberId = teamFeMember.getId();

        given(userRepository.findById(teamBeLeaderId)).willReturn(Optional.of(teamBeLeader));
        given(userRepository.findById(teamFeMemberId)).willReturn(Optional.of(teamFeMember));

        // when & then
        assertThatThrownBy(() -> teamService.deleteMember(teamBeLeaderId, teamFeMemberId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.INVALID_AUTHORIZATION.getMessage());

        verify(archivingUserRepository, never()).save(any(ArchivingUser.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void 멤버_상태_정지_성공() {
        // given
        Long teamBeLeaderId = teamBeLeader.getId();
        Long teamBeMemberId = teamBeMember.getId();

        given(userRepository.findById(teamBeLeaderId)).willReturn(Optional.of(teamBeLeader));
        given(userRepository.findById(teamBeMemberId)).willReturn(Optional.of(teamBeMember));

        // when
        teamService.pauseMember(teamBeLeaderId, teamBeMemberId);

        // then
        assertThat(teamBeMember.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @Test
    void 멤버_상태_정지_실패_유저가_팀에_속하지_않음() {
        // given
        Long teamBeLeaderId = teamBeLeader.getId();
        Long teamFeMemberId = teamFeMember.getId();

        given(userRepository.findById(teamBeLeaderId)).willReturn(Optional.of(teamBeLeader));
        given(userRepository.findById(teamFeMemberId)).willReturn(Optional.of(teamFeMember));

        // when & then
        assertThatThrownBy(() -> teamService.pauseMember(teamBeLeaderId, teamFeMemberId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.INVALID_AUTHORIZATION.getMessage());

        assertThat(teamBeMember.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    void 멤버_정보_수정_성공() {
        // given
        Long teamBeLeaderId = teamBeLeader.getId();
        Long teamBeMemberId = teamBeMember.getId();
        TeamMemberInfoUpdateDto updateDto = TeamMemberInfoUpdateDto.builder()
                .userName("엄수정")
                .jobTitle("일반직")
                .position("사원")
                .userType("정규직")
                .responsibility("백엔드 고양이발")
                .employeeNumber("24-52525")
                .build();

        given(userRepository.findById(teamBeLeaderId)).willReturn(Optional.of(teamBeLeader));
        given(userRepository.findById(teamBeMemberId)).willReturn(Optional.of(teamBeMember));
        given(employeeRepository.findEmployeeByUser(teamBeMember)).willReturn(Optional.of(soogyeongEmployee));
        given(userTypeRepository.findByName(updateDto.userType())).willReturn(Optional.of(userType));
        given(positionRepository.findByName(updateDto.position())).willReturn(Optional.of(position));
        given(jobTitleRepository.findByName(updateDto.jobTitle())).willReturn(Optional.of(jobTitle));

        // when
        TeamMemberDetailResponseDto responseDto = teamService.patchMemberInfo(teamBeLeaderId, teamBeMemberId, updateDto);

        // then
        assertThat(responseDto.userName()).isEqualTo(updateDto.userName());
        assertThat(responseDto.responsibility()).isEqualTo(updateDto.responsibility());
        assertThat(responseDto.employeeNumber()).isEqualTo(updateDto.employeeNumber());
    }

    @Test
    void 멤버_정보_수정_실패_유저가_팀에_속하지_않음() {
        // given
        Long teamBeLeaderId = teamBeLeader.getId();
        Long teamFeMemberId = teamFeMember.getId();
        TeamMemberInfoUpdateDto updateDto = TeamMemberInfoUpdateDto.builder()
                .userName("엄수정")
                .jobTitle("일반직")
                .position("사원")
                .userType("정규직")
                .responsibility("백엔드 고양이발")
                .employeeNumber("24-52525")
                .build();

        given(userRepository.findById(teamBeLeaderId)).willReturn(Optional.of(teamBeLeader));
        given(userRepository.findById(teamFeMemberId)).willReturn(Optional.of(teamFeMember));

        // when & then
        assertThatThrownBy(() -> teamService.patchMemberInfo(teamBeLeaderId, teamFeMemberId, updateDto))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.INVALID_AUTHORIZATION.getMessage());

    }

    @Test
    void 멤버_정보_수정_실패_유저로_직원을_찾을수없음() {
        // given
        Long teamBeLeaderId = teamBeLeader.getId();
        Long teamBeMemberId = teamBeMember.getId();
        TeamMemberInfoUpdateDto updateDto = TeamMemberInfoUpdateDto.builder()
                .userName("엄수정")
                .jobTitle("일반직")
                .position("사원")
                .userType("정규직")
                .responsibility("백엔드 고양이발")
                .employeeNumber("24-52525")
                .build();

        given(userRepository.findById(teamBeLeaderId)).willReturn(Optional.of(teamBeLeader));
        given(userRepository.findById(teamBeMemberId)).willReturn(Optional.of(teamBeMember));
        given(employeeRepository.findEmployeeByUser(teamBeMember)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> teamService.patchMemberInfo(teamBeLeaderId, teamBeMemberId, updateDto))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.NOT_FOUND_EMPLOYEE.getMessage());
    }

    @Test
    void 팀_정보_수정_성공() {
        // given
        Long teamBeId = teamBe.getId();
        Long teamBeLeaderId = teamBeLeader.getId();
        TeamInfoUpdateDto updateDto = new TeamInfoUpdateDto("이리스 BE팀");

        given(teamRepository.findById(teamBeId)).willReturn(Optional.of(teamBe));
        given(userRepository.findById(teamBeLeaderId)).willReturn(Optional.of(teamBeLeader));

        // when
        TeamResponseDto responseDto = teamService.patchTeamInfo(teamBeId, teamBeLeaderId, updateDto);

        // then
        assertThat(teamBe.getTeamName()).isEqualTo(responseDto.teamName());
    }

    @Test
    void 팀_정보_수정_실패_다른_팀_정보_수정() {
        // given
        Long teamFeId = teamFe.getId();
        Long teamBeLeaderId = teamBeLeader.getId();
        TeamInfoUpdateDto updateDto = new TeamInfoUpdateDto("이리스 BE팀");

        given(teamRepository.findById(teamFeId)).willReturn(Optional.of(teamFe));
        given(userRepository.findById(teamBeLeaderId)).willReturn(Optional.of(teamBeLeader));

        // when & then
        assertThatThrownBy(() -> teamService.patchTeamInfo(teamBeLeaderId, teamFeId, updateDto))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.INVALID_AUTHORIZATION.getMessage());

    }

}
