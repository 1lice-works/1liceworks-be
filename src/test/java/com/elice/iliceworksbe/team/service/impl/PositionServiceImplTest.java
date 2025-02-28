package com.elice.iliceworksbe.team.service.impl;

import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.position.PositionRequestDto;
import com.elice.iliceworksbe.team.dto.position.PositionResponseDto;
import com.elice.iliceworksbe.team.dto.position.PositionUpdateDto;
import com.elice.iliceworksbe.team.entity.Position;
import com.elice.iliceworksbe.team.repository.PositionRepository;
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
class PositionServiceImplTest {

    @Mock
    private PositionRepository positionRepository;

    @InjectMocks
    private PositionServiceImpl positionService;

    @Test
    void Position_저장_성공() {
        // given
        PositionRequestDto requestDto = new PositionRequestDto("팀장");
        Position savedPosition = Position.from(requestDto);

        given(positionRepository.existsByName(requestDto.name())).willReturn(false);
        given(positionRepository.save(any(Position.class))).willReturn(savedPosition);

        // when
        PositionResponseDto responseDto = positionService.postPosition(requestDto);

        // then
        assertThat(responseDto.name()).isEqualTo("팀장");
        verify(positionRepository).save(any(Position.class));
    }

    @Test
    void Position_저장_실패_중복된_이름() {
        // given
        PositionRequestDto requestDto = new PositionRequestDto("팀장");

        given(positionRepository.existsByName(requestDto.name())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> positionService.postPosition(requestDto))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.DUPLICATED_POSITION_NAME.getMessage());

        verify(positionRepository, never()).save(any(Position.class));
    }

    @Test
    void Position_조회_성공() {
        // given
        Long positionId = 1L;
        Position position = new Position(positionId, "팀장");

        given(positionRepository.findById(positionId)).willReturn(Optional.of(position));

        // when
        PositionResponseDto foundPosition = positionService.getPosition(positionId);

        // then
        assertThat(foundPosition.name()).isEqualTo("팀장");
    }

    @Test
    void Position_조회_실패_존재하지않음() {
        // given
        Long positionId = 1L;
        given(positionRepository.findById(positionId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> positionService.getPosition(positionId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.NOT_FOUND_POSITION.getMessage());
    }

    @Test
    void Position_전체조회() {
        // given
        List<Position> positions = List.of(
                new Position(1L, "팀장"),
                new Position(2L, "사원")
        );

        given(positionRepository.findAll()).willReturn(positions);

        // when
        List<PositionResponseDto> allPositions = positionService.getAllPositions();

        // then
        assertThat(allPositions).hasSize(2);
        assertThat(allPositions.get(0).name()).isEqualTo("팀장");
        assertThat(allPositions.get(1).name()).isEqualTo("사원");
    }

    @Test
    void Position_수정_성공() {
        // given
        Long positionId = 1L;
        Position position = new Position(positionId, "팀장");
        PositionUpdateDto updateDto = new PositionUpdateDto("부장");

        given(positionRepository.findById(positionId)).willReturn(Optional.of(position));
        given(positionRepository.existsByName(updateDto.name())).willReturn(false);
        given(positionRepository.save(any(Position.class))).willReturn(position);

        // when
        PositionResponseDto updatedPosition = positionService.patchPosition(positionId, updateDto);

        // then
        assertThat(updatedPosition.name()).isEqualTo("부장");
    }

    @Test
    void Position_수정_실패_중복된_이름() {
        // given
        Long positionId = 1L;
        Position position = new Position(positionId, "팀장");
        PositionUpdateDto updateDto = new PositionUpdateDto("부장");

        given(positionRepository.findById(positionId)).willReturn(Optional.of(position));
        given(positionRepository.existsByName(updateDto.name())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> positionService.patchPosition(positionId, updateDto))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.DUPLICATED_POSITION_NAME.getMessage());
    }

    @Test
    void Position_삭제_성공() {
        // given
        Long positionId = 1L;
        Position position = new Position(positionId, "팀장");

        given(positionRepository.findById(positionId)).willReturn(Optional.of(position));

        // when
        positionService.deletePosition(positionId);

        // then
        verify(positionRepository).deleteById(positionId);
    }

    @Test
    void Position_삭제_실패_존재하지않음() {
        // given
        Long positionId = 1L;
        given(positionRepository.findById(positionId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> positionService.deletePosition(positionId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.NOT_FOUND_POSITION.getMessage());

        verify(positionRepository, never()).deleteById(anyLong());
    }
}
