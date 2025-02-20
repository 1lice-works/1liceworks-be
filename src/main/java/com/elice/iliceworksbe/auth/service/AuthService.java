package com.elice.iliceworksbe.auth.service;

import com.elice.iliceworksbe.auth.dto.request.*;
import com.elice.iliceworksbe.auth.dto.response.GetProfileResponseDto;
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
}
