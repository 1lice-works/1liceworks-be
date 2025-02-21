package com.elice.iliceworksbe.auth.service.impl;

import com.elice.iliceworksbe.auth.dto.request.*;
import com.elice.iliceworksbe.auth.dto.response.GetProfileResponseDto;
import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.auth.repository.AuthTokenRepository;
import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.auth.service.AuthService;
import com.elice.iliceworksbe.auth.utils.JwtTokenProvider;
import com.elice.iliceworksbe.auth.utils.RefreshTokenProvider;
import com.elice.iliceworksbe.common.constant.Role;
import com.elice.iliceworksbe.common.constant.Status;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.common.model.RedisDAO;
import com.elice.iliceworksbe.common.service.EmailService;
import com.elice.iliceworksbe.common.service.FirebaseStorageService;
import com.elice.iliceworksbe.team.entity.*;
import com.elice.iliceworksbe.team.repository.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
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
    private final PositionRepository positionRepository;
    private final JobTitleRepository jobTitleRepository;
    private final UserTypeRepository userTypeRepository;
    private final AuthTokenRepository authTokenRepository;

    private final FirebaseStorageService firebaseStorageService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RedisDAO redisDAO;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;


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
        sendVerificationCodeByEmail(verifyEmailRequestDto);
    }

    @Override
    public void verifyEmailPassword(VerifyEmailRequestDto verifyEmailRequestDto) {
        sendVerificationCodeByEmail(verifyEmailRequestDto);
    }

    private void sendVerificationCodeByEmail(VerifyEmailRequestDto verifyEmailRequestDto) {
        String verificationCode = generateVerificationCode();
        emailService.sendEmail(verifyEmailRequestDto.email(), "1liceworks 인증코드 이메일", verificationCode);
        redisDAO.setValues(verifyEmailRequestDto.email(), verificationCode, 300000L); // 5분간 인증 설정
        log.info("{} 이메일 전송, 인증코드 {}", verifyEmailRequestDto.email(), verificationCode);
    }

    @Override
    public void confirmVerificationCode(ConfirmEmailRequestDto confirmEmailRequestDto) {
        String email = confirmEmailRequestDto.email();
        String savedVerificationCode = redisDAO.getValues(email);
        if(savedVerificationCode == null || !savedVerificationCode.equals(confirmEmailRequestDto.verificationCode())){
            throw new BaseException(ErrorCode.INVALID_AUTH_CODE);
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
        String checkEmail = redisDAO.getValues(signUpRequestDto.userInfo().privateEmail());
        if(checkEmail == null || !checkEmail.equals("VERIFIED")) {
            throw new BaseException(ErrorCode.UNVERIFIED_EMAIL);
        }

        // 2. 계정ID 중복 확인
        if (userRepository.existsByAccountId(signUpRequestDto.userInfo().accountId())){
            throw new BaseException(ErrorCode.DUPLICATED_ACCOUNT_ID);
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

        // 5. 해당 유저 Employee 정보 저장
        Position defaultPosition = positionRepository.findById(1L).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_POSITION));
        JobTitle defaultJobTitle = jobTitleRepository.findById(1L).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_JOB_TITLE));
        UserType defaultUserType = userTypeRepository.findById(1L).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER_TYPE));

        Employee signUpEmployee = Employee.builder()
                .user(signUpUser)
                .userType(defaultUserType)
                .position(defaultPosition)
                .jobTitle(defaultJobTitle)
                .hireDate(LocalDateTime.now())
                .build();

        userRepository.save(signUpUser);
        employeeRepository.save(signUpEmployee);
    }

    @Override
    public List<GetProfileResponseDto> getAllMemberProfiles(Long userId) {

        // 1. 현재 유저의 팀 정보 조회
        Team team = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER)).getTeam();

        // 2. 해당 팀에 속하는 모든 유저 조회
        List<User> userList = userRepository.findByTeam(team);

        // 3. 해당 팀에 속하는 유저의 직원 정보 조회
        List<GetProfileResponseDto> memberProfiles = new ArrayList<>();

        for(User user : userList) {
            Employee employee = employeeRepository.findEmployeeByUser(user).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));
            memberProfiles.add(GetProfileResponseDto.of(user, employee));
        }

        return memberProfiles;
    }

    @Override
    public GetProfileResponseDto getMyProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));
        Employee employee = employeeRepository.findEmployeeByUser(user).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));

        return GetProfileResponseDto.of(user, employee);
    }

    @Override
    @Transactional
    public void patchMyProfile(Long userId, PatchProfileRequestDto patchProfileRequestDto, MultipartFile profileImage) {

        // 0. 내 정보 가져오기
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));
        Employee employee = employeeRepository.findEmployeeByUser(user).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));

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
            throw new BaseException(ErrorCode.FAILED_TO_UPLOAD_IMAGE);
        }

        // 2. 프로필 수정
        user.patchMyProfile(patchProfileRequestDto, updatedProfileImageUrl);
        employee.designateResponsibility(patchProfileRequestDto.responsibility());

        userRepository.save(user);
        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void patchMemberProfile(Long leaderUserId, Long memberUserId, PatchMemberProfileRequestDto patchProfileRequestDto) {
        // 1. leaderUserId로 팀 조회
        Team team = userRepository.findById(leaderUserId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER)).getTeam();

        // 2. 해당 팀에 userId가 존재하는지 확인
        User memberUser = userRepository.findById(memberUserId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));

        if (!memberUser.getTeam().equals(team)) {
            throw new BaseException(ErrorCode.INVALID_AUTHORIZATION);
        }

        // 3. employee 정보 조회
        Employee employee = employeeRepository.findEmployeeByUser(memberUser).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));

        // 4. position, jobtitle, usertype 조회
        Position patchedPosition = positionRepository.findByName(patchProfileRequestDto.position()).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_POSITION));
        JobTitle patchedJobTitle = jobTitleRepository.findByName(patchProfileRequestDto.jobTitle()).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_JOB_TITLE));
        UserType patchedUserType = userTypeRepository.findByName(patchProfileRequestDto.userType()).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER_TYPE));

        // 5. 해당 user 정보 변경
        memberUser.patchUsername(patchProfileRequestDto.username());
        employee.patchProfile(patchProfileRequestDto, patchedPosition, patchedJobTitle, patchedUserType);
    }

    @Override
    @Transactional
    public void logout(Long userId, HttpServletRequest request, HttpServletResponse response) {
        // 1. AccessToken 블랙리스트 등록
        invalidateAccessToken(request);

        // 2. RefreshToken AuthToken 테이블에서 삭제
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            String refreshToken = null;
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
            if (refreshToken != null) {
                authTokenRepository.deleteByRefreshToken(refreshToken);
            }
        }

        // 3. RefreshToken 쿠키 제거
        invalidateRefreshToken(response);
    }

    @Override
    @Transactional
    public String refreshAccessToken(String refreshToken) {

        User user = refreshTokenProvider.validateRefreshToken(refreshToken);

        // Refresh Token 검증
        if (user == null) {
            throw new BaseException(ErrorCode.INVALID_JWT);
        }

        return jwtTokenProvider.generateAccessToken(user.getAccountId(), user.getId(), List.of(new SimpleGrantedAuthority(user.getRole().name())));
    }

    @Override
    public void changePasswordByEmail(ChangePasswordRequestDto changePasswordRequestDto) {

        // 0. 유저 찾기
        User user = userRepository.findByAccountId(changePasswordRequestDto.accountId()).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));

        // 1. 해당 유저의 개인 이메일이 맞는지 확인
        if (!user.getPrivateEmail().equals(changePasswordRequestDto.privateEmail())) {
            throw new BaseException(ErrorCode.NOT_FOUND_USER);
        }

        // 2. 해당 이메일이 인증됐는지 여부 확인
        String checkEmail = redisDAO.getValues(changePasswordRequestDto.privateEmail());
        if(checkEmail == null || !checkEmail.equals("VERIFIED")) {
            throw new BaseException(ErrorCode.UNVERIFIED_EMAIL);
        }

        // 3. 이전 비밀번호와 동일한지 확인
        if (passwordEncoder.matches(changePasswordRequestDto.newPassword(), user.getPassword())) {
            throw new BaseException(ErrorCode.FAILED_TO_MATCH_BEFORE_PASSWORD);
        }

        user.changePassword(passwordEncoder.encode(changePasswordRequestDto.newPassword()));
        userRepository.save(user);
    }

    private void invalidateAccessToken(HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        redisDAO.setValues(accessToken, "logout", jwtTokenProvider.getAccessTokenExpiration());
    }

    private static void invalidateRefreshToken(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // 즉시 만료
        response.addCookie(refreshTokenCookie); // 클라이언트에 삭제 요청
    }
}
