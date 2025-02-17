package com.elice.iliceworksbe.team.service;

import com.elice.iliceworksbe.team.dto.userType.UserTypeRequestDto;
import com.elice.iliceworksbe.team.dto.userType.UserTypeResponseDto;
import com.elice.iliceworksbe.team.dto.userType.UserTypeUpdateDto;

import java.util.List;

public interface UserTypeService {
    UserTypeResponseDto postUserType(UserTypeRequestDto userTypeRequestDto);
    UserTypeResponseDto getUserType(Long userTypeId);
    List<UserTypeResponseDto> getAllUserTypes();
    UserTypeResponseDto patchUserType(Long userTypeId, UserTypeUpdateDto userTypeUpdateDto);
    void deleteUserType(Long userTypeId);
}
