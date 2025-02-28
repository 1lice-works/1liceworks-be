package com.elice.iliceworksbe.team.service.impl;

import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.userType.UserTypeRequestDto;
import com.elice.iliceworksbe.team.dto.userType.UserTypeResponseDto;
import com.elice.iliceworksbe.team.dto.userType.UserTypeUpdateDto;
import com.elice.iliceworksbe.team.entity.UserType;
import com.elice.iliceworksbe.team.repository.UserTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserTypeServiceImplTest {

    @Mock
    private UserTypeRepository userTypeRepository;

    @InjectMocks
    private UserTypeServiceImpl userTypeService;

    @Test
    void UserType_저장_성공() {
        // given
        UserTypeRequestDto requestDto = new UserTypeRequestDto("계약직");
        UserType savedUserType = UserType.from(requestDto);

        given(userTypeRepository.existsByName(requestDto.name())).willReturn(false);
        given(userTypeRepository.save(any(UserType.class))).willReturn(savedUserType);

        // when
        UserTypeResponseDto responseDto = userTypeService.postUserType(requestDto);

        // then
        assertThat(responseDto.name()).isEqualTo("계약직");
        verify(userTypeRepository).save(any(UserType.class));
    }

    @Test
    void UserType_저장_실패_중복된_이름() {
        // given
        UserTypeRequestDto requestDto = new UserTypeRequestDto("계약직");

        given(userTypeRepository.existsByName(requestDto.name())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userTypeService.postUserType(requestDto))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.DUPLICATED_USER_TYPE_NAME.getMessage());

        verify(userTypeRepository, never()).save(any(UserType.class));
    }

    @Test
    void UserType_조회_성공() {
        // given
        Long userTypeId = 1L;
        UserType userType = new UserType(userTypeId, "계약직");

        given(userTypeRepository.findById(userTypeId)).willReturn(Optional.of(userType));

        // when
        UserTypeResponseDto foundUserType = userTypeService.getUserType(userTypeId);

        // then
        assertThat(foundUserType.name()).isEqualTo("계약직");
    }

    @Test
    void UserType_조회_실패_존재하지않음() {
        // given
        Long userTypeId = 1L;
        given(userTypeRepository.findById(userTypeId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userTypeService.getUserType(userTypeId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.NOT_FOUND_USER_TYPE.getMessage());
    }

    @Test
    void UserType_전체조회() {
        // given
        List<UserType> userTypes = List.of(
                new UserType(1L, "계약직"),
                new UserType(2L, "정규직")
        );

        given(userTypeRepository.findAll()).willReturn(userTypes);

        // when
        List<UserTypeResponseDto> allUserTypes = userTypeService.getAllUserTypes();

        // then
        assertThat(allUserTypes).hasSize(2);
        assertThat(allUserTypes.get(0).name()).isEqualTo("계약직");
        assertThat(allUserTypes.get(1).name()).isEqualTo("정규직");
    }

    @Test
    void UserType_수정_성공() {
        // given
        Long userTypeId = 1L;
        UserType userType = new UserType(userTypeId, "계약직");
        UserTypeUpdateDto updateDto = new UserTypeUpdateDto("정규직");

        given(userTypeRepository.findById(userTypeId)).willReturn(Optional.of(userType));
        given(userTypeRepository.existsByName(updateDto.name())).willReturn(false);
        given(userTypeRepository.save(any(UserType.class))).willReturn(userType);

        // when
        UserTypeResponseDto updatedUserType = userTypeService.patchUserType(userTypeId, updateDto);

        // then
        assertThat(updatedUserType.name()).isEqualTo("정규직");
    }

    @Test
    void UserType_수정_실패_중복된_이름() {
        // given
        Long userTypeId = 1L;
        UserType userType = new UserType(userTypeId, "계약직");
        UserTypeUpdateDto updateDto = new UserTypeUpdateDto("정규직");

        given(userTypeRepository.findById(userTypeId)).willReturn(Optional.of(userType));
        given(userTypeRepository.existsByName(updateDto.name())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userTypeService.patchUserType(userTypeId, updateDto))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.DUPLICATED_USER_TYPE_NAME.getMessage());
    }

    @Test
    void UserType_삭제_성공() {
        // given
        Long userTypeId = 1L;
        UserType userType = new UserType(userTypeId, "계약직");

        given(userTypeRepository.findById(userTypeId)).willReturn(Optional.of(userType));

        // when
        userTypeService.deleteUserType(userTypeId);

        // then
        verify(userTypeRepository).deleteById(userTypeId);
    }

    @Test
    void UserType_삭제_실패_존재하지않음() {
        // given
        Long userTypeId = 1L;
        given(userTypeRepository.findById(userTypeId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userTypeService.deleteUserType(userTypeId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.NOT_FOUND_USER_TYPE.getMessage());

        verify(userTypeRepository, never()).deleteById(anyLong());

    }
}