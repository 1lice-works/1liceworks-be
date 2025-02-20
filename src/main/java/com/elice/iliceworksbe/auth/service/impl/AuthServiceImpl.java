package com.elice.iliceworksbe.auth.service.impl;

import com.elice.iliceworksbe.auth.dto.request.*;
import com.elice.iliceworksbe.auth.dto.response.GetProfileResponseDto;
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
import com.elice.iliceworksbe.common.service.FirebaseStorageService;
import com.elice.iliceworksbe.team.entity.Employee;
import com.elice.iliceworksbe.team.entity.Team;
import com.elice.iliceworksbe.team.repository.EmployeeRepository;
import com.elice.iliceworksbe.team.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.elice.iliceworksbe.auth.utils.VerificationCodeGenerator.generateVerificationCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;

    private final FirebaseStorageService firebaseStorageService;
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
    }

    @Override
    public List<GetProfileResponseDto> getAllMemberProfiles(Long userId) {

        // 1. 현재 유저의 팀 정보 조회
        Team team = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER)).getTeam();

        // 2. 해당 팀에 속하는 모든 유저 조회
        List<User> userList = userRepository.findByTeam(team);

        // 3. 해당 팀에 속하는 유저의 직원 정보 조회
        List<GetProfileResponseDto> memberProfiles = new ArrayList<>();

        for(User user : userList) {
            Employee employee = employeeRepository.findEmployeeByUser(user).orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER));
            memberProfiles.add(GetProfileResponseDto.of(user, employee));
        }

        return memberProfiles;
    }

    @Override
    public GetProfileResponseDto getMyProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER));
        Employee employee = employeeRepository.findEmployeeByUser(user).orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER));

        return GetProfileResponseDto.of(user, employee);
    }

    @Override
    @Transactional
    public void patchMyProfile(Long userId, PatchProfileRequestDto patchProfileRequestDto, MultipartFile profileImage) {

        // 0. 내 정보 가져오기
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER));
        Employee employee = employeeRepository.findEmployeeByUser(user).orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER));

        // 1. 프로필 이미지 수정
        String originalProfileImageUrl = user.getProfileImage();
        String updatedProfileImageUrl;

        try {
            if (profileImage != null) { // 이미지를 등록하거나 수정해야하는 상황
                if(originalProfileImageUrl != null) {
                    firebaseStorageService.deleteImage(originalProfileImageUrl);
                }
                updatedProfileImageUrl = firebaseStorageService.uploadImage(profileImage);
            } else { // 이미지 등록을 안하거나 기존 프로필 이미지를 삭제할 시
                if(originalProfileImageUrl != null){
                    firebaseStorageService.deleteImage(originalProfileImageUrl);
                }
                updatedProfileImageUrl = null;
            }
        } catch (IOException e) {
            throw new BaseException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }

        // 2. 프로필 수정
        user.patchProfile(patchProfileRequestDto, updatedProfileImageUrl);
        employee.designateResponsibility(patchProfileRequestDto.responsibility());

        userRepository.save(user);
        employeeRepository.save(employee);
    }
}
