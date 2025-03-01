package com.elice.iliceworksbe.team.service.impl;

import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleRequestDto;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleResponseDto;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleUpdateDto;
import com.elice.iliceworksbe.team.entity.JobTitle;
import com.elice.iliceworksbe.team.repository.JobTitleRepository;
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

    @Test
    void jobTitle_저장_성공() {
        // given
        JobTitleRequestDto requestDto = new JobTitleRequestDto("일반직");
        JobTitle savedJobTitle = JobTitle.from(requestDto);

        given(jobTitleRepository.existsByName(requestDto.name())).willReturn(false);
        given(jobTitleRepository.save(any(JobTitle.class))).willReturn(savedJobTitle);

        // when
        JobTitleResponseDto responseDto = jobTitleService.postJobTitle(requestDto);

        // then
        assertThat(responseDto.name()).isEqualTo("일반직");
        verify(jobTitleRepository).save(any(JobTitle.class)); // save() 호출 검증
    }

    @Test
    void jobTitle_저장_실패_중복된_이름() {
        // given
        JobTitleRequestDto requestDto = new JobTitleRequestDto("일반직");

        given(jobTitleRepository.existsByName(requestDto.name())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> jobTitleService.postJobTitle(requestDto))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.DUPLICATED_JOB_TITLE_NAME.getMessage());

        verify(jobTitleRepository, never()).save(any(JobTitle.class)); // save()가 호출되지 않아야 함
    }

    @Test
    void jobTitle_조회_성공() {
        // given
        Long jobTitleId = 1L;
        JobTitle jobTitle = new JobTitle(jobTitleId, "일반직");

        given(jobTitleRepository.findById(jobTitleId)).willReturn(Optional.of(jobTitle));

        // when
        JobTitleResponseDto foundJobTitle = jobTitleService.getJobTitle(jobTitleId);

        // then
        assertThat(foundJobTitle.name()).isEqualTo("일반직");
    }

    @Test
    void jobTitle_조회_실패_존재하지않음() {
        // given
        Long jobTitleId = 1L;
        given(jobTitleRepository.findById(jobTitleId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> jobTitleService.getJobTitle(jobTitleId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.NOT_FOUND_JOB_TITLE.getMessage());
    }

    @Test
    void jobTitle_전체조회() {
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
        assertThat(allJobTitles.get(0).name()).isEqualTo("일반직");
        assertThat(allJobTitles.get(1).name()).isEqualTo("사무직");
    }

    @Test
    void jobTitle_수정_성공() {
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
        assertThat(updatedJobTitle.name()).isEqualTo("사무직");
    }

    @Test
    void jobTitle_수정_실패_중복된_이름() {
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

    @Test
    void jobTitle_삭제_성공() {
        // given
        Long jobTitleId = 1L;
        JobTitle jobTitle = new JobTitle(jobTitleId, "일반직");

        given(jobTitleRepository.findById(jobTitleId)).willReturn(Optional.of(jobTitle));

        // when
        jobTitleService.deleteJobTitle(jobTitleId);

        // then
        verify(jobTitleRepository, times(1)).deleteById(jobTitleId); // deleteById() 호출 검증
    }

    @Test
    void jobTitle_삭제_실패_존재하지않음() {
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