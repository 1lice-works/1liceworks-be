package com.elice.iliceworksbe.auth.service;

import com.elice.iliceworksbe.auth.dto.request.*;
import com.elice.iliceworksbe.auth.dto.response.GetMySemiProfileResponseDto;
import com.elice.iliceworksbe.auth.dto.response.GetProfileResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AuthService extends UserDetailsService {

    UserDetails loadUserByUsername(String accountId);
    void verifyEmail(VerifyEmailRequestDto verifyEmailRequestDto);
    void confirmVerificationCode(ConfirmEmailRequestDto confirmEmailRequestDto);
    Boolean checkDuplicateAccountId(CheckDuplicateAccountIdRequestDto checkDuplicateAccountIdRequestDTO);
    void signUp(SignUpRequestDto signUpRequestDto);

    List<GetProfileResponseDto> getAllMemberProfiles(Long userId);

    GetProfileResponseDto getMyProfile(Long userId);

    void patchMyProfile(Long userId, PatchProfileRequestDto patchProfileRequestDto, MultipartFile profileImage);

    void patchMemberProfile(Long leaderUserId, Long memberUserId, PatchMemberProfileRequestDto patchProfileRequestDto);

    void logout(Long userId, HttpServletRequest request, HttpServletResponse response);

    String refreshAccessToken(String refreshToken);

    void changePasswordByEmail(ChangePasswordRequestDto changePasswordRequestDto);

    void verifyEmailPassword(VerifyEmailRequestDto verifyEmailRequestDto);

    GetMySemiProfileResponseDto getMyMinimalProfile(Long userId);
}
