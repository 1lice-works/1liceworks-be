package com.elice.iliceworksbe.team.service.impl;

import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.userType.UserTypeRequestDto;
import com.elice.iliceworksbe.team.dto.userType.UserTypeResponseDto;
import com.elice.iliceworksbe.team.dto.userType.UserTypeUpdateDto;
import com.elice.iliceworksbe.team.entity.UserType;
import com.elice.iliceworksbe.team.repository.UserTypeRepository;
import org.junit.jupiter.api.DisplayName;
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

    @DisplayName("사용자 유형 저장 성공")
    @Test
    void givenUserType_whenPostUserType_thenSave() {
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

    @DisplayName("사용자 유형 저장 실패 - 중복된 사용자 유형명")
    @Test
    void givenDuplicatedUserType_whenPostUserType_thenThrow_DUPLICATED_USER_TYPE_NAME() {
        // given
        UserTypeRequestDto requestDto = new UserTypeRequestDto("계약직");

        given(userTypeRepository.existsByName(requestDto.name())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userTypeService.postUserType(requestDto))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.DUPLICATED_USER_TYPE_NAME.getMessage());

        verify(userTypeRepository, never()).save(any(UserType.class));
    }

    @DisplayName("사용자 유형 조회 성공")
    @Test
    void givenUserType_whenGetUserType_thenReturnUserType() {
        // given
        Long userTypeId = 1L;
        UserType userType = new UserType(userTypeId, "계약직");

        given(userTypeRepository.findById(userTypeId)).willReturn(Optional.of(userType));

        // when
        UserTypeResponseDto foundUserType = userTypeService.getUserType(userTypeId);

        // then
        assertThat(foundUserType.name()).isEqualTo("계약직");
    }

    @DisplayName("사용자 유형 조회 실패 - 존재하지 않는 사용자 유형")
    @Test
    void givenNonExistUserType_whenGetUserType_thenThrow_NOT_FOUND_USER_TYPE() {
        // given
        Long userTypeId = 1L;
        given(userTypeRepository.findById(userTypeId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userTypeService.getUserType(userTypeId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.NOT_FOUND_USER_TYPE.getMessage());
    }

    @DisplayName("사용자 유형 전체 조회 성공")
    @Test
    void givenUserTypes_whenGetAllUserTypes_thenReturnUserTypes() {
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

    @DisplayName("사용자 유형 수정 성공")
    @Test
    void givenUserType_whenPatchUserType_thenReturnUpdatedUserType() {
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

    @DisplayName("사용자 유형 수정 실패 - 중복된 사용자 유형명")
    @Test
    void givenDuplicatedUserType_whenPatchUserType_thenThrow_DUPLICATED_USER_TYPE_NAME() {
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

    @DisplayName("사용자 유형 삭제 성공")
    @Test
    void givenUserType_whenDeleteUserType_thenDeleteUserType() {
        // given
        Long userTypeId = 1L;
        UserType userType = new UserType(userTypeId, "계약직");

        given(userTypeRepository.findById(userTypeId)).willReturn(Optional.of(userType));

        // when
        userTypeService.deleteUserType(userTypeId);

        // then
        verify(userTypeRepository).deleteById(userTypeId);
    }

    @DisplayName("사용자 유형 삭제 실패 - 존재하지 않는 사용자 유형")
    @Test
    void givenUserType_whenDeleteUserType_thenThrow_NOT_FOUND_USER_TYPE() {
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