package com.elice.iliceworksbe.team.service.impl;

import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleRequestDto;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleResponseDto;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleUpdateDto;
import com.elice.iliceworksbe.team.entity.JobTitle;
import com.elice.iliceworksbe.team.repository.JobTitleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

import java.util.Optional;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class JobTitleServiceImplTest {

    @Mock
    private JobTitleRepository jobTitleRepository;

    @InjectMocks
    private JobTitleServiceImpl jobTitleService;

    @DisplayName("직책 저장 성공")
    @Test
    void givenJobTitle_whenPostJobTitle_thenSave() {
        // given
        JobTitleRequestDto requestDto = new JobTitleRequestDto("일반직");
        JobTitle savedJobTitle = JobTitle.from(requestDto);

        given(jobTitleRepository.existsByName(requestDto.name())).willReturn(false);
        given(jobTitleRepository.save(any(JobTitle.class))).willReturn(savedJobTitle);

        // when
        JobTitleResponseDto responseDto = jobTitleService.postJobTitle(requestDto);

        // then
        assertThat(responseDto.name()).isEqualTo(responseDto.name());
        verify(jobTitleRepository).save(any(JobTitle.class)); // save() 호출 검증
    }
    @DisplayName("직책 저장 실패 - 중복된 직책명")
    @Test
    void givenDuplicatedJobTitle_whenPostJobTitle_thenThrow_DUPLICATED_JOB_TITLE_NAME() {
        // given
        JobTitleRequestDto requestDto = new JobTitleRequestDto("일반직");

        given(jobTitleRepository.existsByName(requestDto.name())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> jobTitleService.postJobTitle(requestDto))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.DUPLICATED_JOB_TITLE_NAME.getMessage());

        verify(jobTitleRepository, never()).save(any(JobTitle.class)); // save()가 호출되지 않아야 함
    }
    @DisplayName("직책 조회 성공")
    @Test
    void givenJobTitle_whenGetJobTitle_thenReturnJobTitle() {
        // given
        Long jobTitleId = 1L;
        JobTitle jobTitle = new JobTitle(jobTitleId, "일반직");

        given(jobTitleRepository.findById(jobTitleId)).willReturn(Optional.of(jobTitle));

        // when
        JobTitleResponseDto foundJobTitle = jobTitleService.getJobTitle(jobTitleId);

        // then
        assertThat(foundJobTitle.name()).isEqualTo(jobTitle.getName());
    }
    @DisplayName("직책 조회 실패 - 존재하지 않는 직책")
    @Test
    void givenNonExistJobTitle_whenGetJobTitle_thenThrow_NOT_FOUND_JOB_TITLE() {
        // given
        Long jobTitleId = 1L;
        given(jobTitleRepository.findById(jobTitleId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> jobTitleService.getJobTitle(jobTitleId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.NOT_FOUND_JOB_TITLE.getMessage());
    }
    @DisplayName("직책 전체 조회 성공 ")
    @Test
    void givenJobTitles_whenGetAllJobTitles_thenReturnJobTitles() {
        // given
        List<JobTitle> jobTitles = List.of(
                new JobTitle(1L, "일반직"),
                new JobTitle(2L, "사무직")
        );

        given(jobTitleRepository.findAll()).willReturn(jobTitles);

        // when
        List<JobTitleResponseDto> allJobTitles = jobTitleService.getAllJobTitles();

        // then
        assertThat(allJobTitles).hasSize(2);
        assertThat(allJobTitles.get(0).name()).isEqualTo(jobTitles.get(0).getName());
        assertThat(allJobTitles.get(1).name()).isEqualTo(jobTitles.get(1).getName());
    }
    @DisplayName("직책 수정 성공")
    @Test
    void givenJobTitle_whenPatchJobTitle_thenReturnUpdatedJobTitle() {
        // given
        Long jobTitleId = 1L;
        JobTitle jobTitle = new JobTitle(jobTitleId, "일반직");
        JobTitleUpdateDto updateDto = new JobTitleUpdateDto("사무직");

        given(jobTitleRepository.findById(jobTitleId)).willReturn(Optional.of(jobTitle));
        given(jobTitleRepository.existsByName(updateDto.name())).willReturn(false);
        given(jobTitleRepository.save(any(JobTitle.class))).willReturn(jobTitle);

        // when
        JobTitleResponseDto updatedJobTitle = jobTitleService.patchJobTitle(jobTitleId, updateDto);

        // then
        assertThat(updatedJobTitle.name()).isEqualTo(jobTitle.getName());
    }

    @DisplayName("직책 수정 실패 - 중복된 직책명")
    @Test
    void givenDuplicatedJobTitle_whenPatchJobTitle_thenThrow_DUPLICATED_JOB_TITLE_NAME() {
        // given
        Long jobTitleId = 1L;
        JobTitle jobTitle = new JobTitle(jobTitleId, "일반직");
        JobTitleUpdateDto updateDto = new JobTitleUpdateDto("사무직");

        given(jobTitleRepository.findById(jobTitleId)).willReturn(Optional.of(jobTitle));
        given(jobTitleRepository.existsByName(updateDto.name())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> jobTitleService.patchJobTitle(jobTitleId, updateDto))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.DUPLICATED_JOB_TITLE_NAME.getMessage());

    }

    @DisplayName("직책 삭제 성공")
    @Test
    void givenJobTitle_whenDeleteJobTitle_thenDeleteJobTitle() {
        // given
        Long jobTitleId = 1L;
        JobTitle jobTitle = new JobTitle(jobTitleId, "일반직");

        given(jobTitleRepository.findById(jobTitleId)).willReturn(Optional.of(jobTitle));

        // when
        jobTitleService.deleteJobTitle(jobTitleId);

        // then
        verify(jobTitleRepository, times(1)).deleteById(jobTitleId); // deleteById() 호출 검증
    }

    @DisplayName("직책 삭제 실패 - 존재하지 않는 직책")
    @Test
    void givenJobTitle_whenDeleteJobTitle_thenThrow_NOT_FOUND_JOB_TITLE() {
        // given
        Long jobTitleId = 1L;
        given(jobTitleRepository.findById(jobTitleId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> jobTitleService.deleteJobTitle(jobTitleId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.NOT_FOUND_JOB_TITLE.getMessage());

        verify(jobTitleRepository, never()).deleteById(anyLong()); // deleteById()가 호출되지 않아야 함
    }
}