package com.elice.iliceworksbe.team.service;

import com.elice.iliceworksbe.team.dto.position.PositionRequestDto;
import com.elice.iliceworksbe.team.dto.position.PositionResponseDto;
import com.elice.iliceworksbe.team.dto.position.PositionUpdateDto;


import java.util.List;

public interface PositionService {
    PositionResponseDto postPosition(PositionRequestDto positionRequestDto);
    PositionResponseDto getPosition(Long positionId);
    List<PositionResponseDto> getAllPositions();
    PositionResponseDto patchPosition(Long positionId, PositionUpdateDto positionUpdateDto);
    void deletePosition(Long positionId);
}
