package com.elice.iliceworksbe.team.service.impl;

import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.dto.position.PositionRequestDto;
import com.elice.iliceworksbe.team.dto.position.PositionResponseDto;
import com.elice.iliceworksbe.team.dto.position.PositionUpdateDto;
import com.elice.iliceworksbe.team.entity.Position;
import com.elice.iliceworksbe.team.repository.PositionRepository;
import com.elice.iliceworksbe.team.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;

    @Transactional
    @Override
    public PositionResponseDto postPosition(PositionRequestDto positionRequestDto) {
        Position savedPosition = positionRepository.save(Position.from(positionRequestDto));
        return PositionResponseDto.from(savedPosition);
    }

    @Override
    public PositionResponseDto getPosition(Long positionId) {
        Position findedPosition = positionRepository.findById(positionId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_POSITION));
        return PositionResponseDto.from(findedPosition);
    }

    @Override
    public List<PositionResponseDto> getAllPositions() {
        return positionRepository.findAll()
                .stream()
                .map(PositionResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public PositionResponseDto patchPosition(Long positionId, PositionUpdateDto positionUpdateDto) {
        Position findedPosition = positionRepository.findById(positionId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_POSITION));

        findedPosition.update(positionUpdateDto);

        Position updatedPosition = positionRepository.save(findedPosition);
        return PositionResponseDto.from(updatedPosition);
    }

    @Transactional
    @Override
    public void deletePosition(Long positionId) {
        positionRepository.deleteById(positionId);
    }

    @Override
    public Position getPositionByName(String name) {
        return positionRepository.findByName(name)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_POSITION));
    }
}
