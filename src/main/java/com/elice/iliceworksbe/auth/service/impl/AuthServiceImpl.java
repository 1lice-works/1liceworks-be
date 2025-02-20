package com.elice.iliceworksbe.auth.service.impl;

import com.elice.iliceworksbe.auth.dto.request.CheckDuplicateAccountIdRequestDto;
import com.elice.iliceworksbe.auth.dto.request.ConfirmEmailRequestDto;
import com.elice.iliceworksbe.auth.dto.request.SignUpRequestDto;
import com.elice.iliceworksbe.auth.dto.request.VerifyEmailRequestDto;
import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.auth.service.AuthService;
import com.elice.iliceworksbe.common.constant.Role;
import com.elice.iliceworksbe.common.constant.Status;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.common.model.RedisDAO;
import com.elice.iliceworksbe.common.service.EmailService;
import com.elice.iliceworksbe.team.entity.Employee;
import com.elice.iliceworksbe.team.entity.Team;
import com.elice.iliceworksbe.team.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.elice.iliceworksbe.auth.utils.VerificationCodeGenerator.generateVerificationCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final UserTypeRepository userTypeRepository;
    private final JobTitleRepository jobTitleRepository;

    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RedisDAO redisDAO;

    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        User user = userRepository.findByAccountId(accountId).orElseThrow(() -> new UsernameNotFoundException(accountId));
        return new UserDetailsImpl(user);
    }

    @Override
    public void verifyEmail(VerifyEmailRequestDto verifyEmailRequestDto) {

        // 1. email 중복 확인
        if (userRepository.existsByPrivateEmail(verifyEmailRequestDto.email())) {
            throw new BaseException(ErrorCode.DUPLICATED_EMAIL);
        }

        // 2. 인증코드 전송
        String verificationCode = generateVerificationCode();
        emailService.sendEmail(verifyEmailRequestDto.email(), "1liceworks 인증코드 이메일", verificationCode);
        redisDAO.setValues(verifyEmailRequestDto.email(), verificationCode, 300000L); // 5분간 인증 설정
        log.info("{} 이메일 전송, 인증코드 {}", verifyEmailRequestDto.email(), verificationCode);
    }

    @Override
    public void confirmVerificationCode(ConfirmEmailRequestDto confirmEmailRequestDto) {
        String email = confirmEmailRequestDto.email();
        String savedVerificationCode = redisDAO.getValues(email);
        if(!savedVerificationCode.equals(confirmEmailRequestDto.verificationCode())){
            throw new BaseException(ErrorCode.WRONG_AUTH_CODE);
        }
        redisDAO.setValues(email, "VERIFIED", 300000L); // 5분간 해당 이메일이 인증됐음을 설정
    }

    @Override
    public Boolean checkDuplicateAccountId(CheckDuplicateAccountIdRequestDto checkDuplicateAccountIdRequestDTO) {
        return userRepository.existsByAccountId(checkDuplicateAccountIdRequestDTO.accountId());
    }

    @Override
    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) {
        // 1. 이메일 인증 확인
        if(!redisDAO.getValues(signUpRequestDto.userInfo().privateEmail()).equals("VERIFIED")) {
            throw new BaseException(ErrorCode.UNVERIFIED_EMAIL);
        }

        // 2. 계정ID 중복 확인
        if (userRepository.existsByAccountId(signUpRequestDto.userInfo().accountId())){
            throw new BaseException(ErrorCode.DUPLICATED_ACCOUNTID);
        }

        // 3. 개인 이메일 중복 확인
        if (userRepository.existsByPrivateEmail(signUpRequestDto.userInfo().privateEmail())){
            throw new BaseException(ErrorCode.DUPLICATED_EMAIL);
        }

        // 3. 팀 생성 후 저장
        Team newTeam = Team.builder()
                .companyName(signUpRequestDto.teamInfo().companyName())
                .teamName(signUpRequestDto.teamInfo().teamName())
                .hasPrivateDomain(signUpRequestDto.teamInfo().hasPrivateDomain())
                .domainName(signUpRequestDto.teamInfo().domainName())
                .industry(signUpRequestDto.teamInfo().industry())
                .scale(signUpRequestDto.teamInfo().scale())
                .build();

        teamRepository.save(newTeam);

        // 4. 유저 생성 후 저장
        User signUpUser = User.builder()
                .accountId(signUpRequestDto.userInfo().accountId())
                .username(signUpRequestDto.userInfo().username())
                .privateEmail(signUpRequestDto.userInfo().privateEmail())
                .password(passwordEncoder.encode(signUpRequestDto.userInfo().password()))
                .isTeamCreated(true)
                .role(Role.LEADER)
                .status(Status.ACTIVE)
                .team(newTeam)
                .build();

        userRepository.save(signUpUser);

        Employee teamLeader = Employee.builder()
                .user(signUpUser)
                .userType(userTypeRepository.findById(1L).get())
                .position(positionRepository.findById(1L).get())
                .jobTitle(jobTitleRepository.findById(1L).get())
                .build();

        employeeRepository.save(teamLeader);
    }

    @PostConstruct
    public void init(){
        User user = User.builder()
                .accountId("hi563@threadly.ilice-works.com")
                .username("정태승")
                .password(passwordEncoder.encode("!a12345678"))
                .privateEmail("hi563@naver.com")
                .role(Role.LEADER)
                .status(Status.ACTIVE)
                .isTeamCreated(false)
                .build();

        userRepository.save(user);
    }
}
