package com.elice.iliceworksbe.team.service.impl;

import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.userType.UserTypeRequestDto;
import com.elice.iliceworksbe.team.dto.userType.UserTypeResponseDto;
import com.elice.iliceworksbe.team.dto.userType.UserTypeUpdateDto;
import com.elice.iliceworksbe.team.entity.UserType;
import com.elice.iliceworksbe.team.repository.UserTypeRepository;
import com.elice.iliceworksbe.team.service.UserTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTypeServiceImpl implements UserTypeService{

    private final UserTypeRepository userTypeRepository;

    @Transactional
    @Override
    public UserTypeResponseDto postUserType(UserTypeRequestDto userTypeRequestDto) {
        UserType savedUserType = userTypeRepository.save(UserType.from(userTypeRequestDto));
        return UserTypeResponseDto.from(savedUserType);
    }

    @Override
    public UserTypeResponseDto getUserType(Long userTypeId) {
        UserType findedUserType = userTypeRepository.findById(userTypeId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_TYPE_NOT_FOUND));
        return UserTypeResponseDto.from(findedUserType);
    }

    @Override
    public List<UserTypeResponseDto> getAllUserTypes() {
        return userTypeRepository.findAll()
                .stream()
                .map(UserTypeResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserTypeResponseDto patchUserType(Long userTypeId, UserTypeUpdateDto userTypeUpdateDto) {
        UserType findedUserType = userTypeRepository.findById(userTypeId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_TYPE_NOT_FOUND));

        findedUserType.update(userTypeUpdateDto);

        UserType updatedUserType = userTypeRepository.save(findedUserType);
        return UserTypeResponseDto.from(updatedUserType);
    }

    @Transactional
    @Override
    public void deleteUserType(Long userTypeId) {
        userTypeRepository.deleteById(userTypeId);
    }

    @PostConstruct
    public void init() {
        UserType userType = UserType.builder()
                .name("없음")
                .build();

        userTypeRepository.save(userType);
    }
}
