package com.elice.iliceworksbe.team.dto.position;


import com.elice.iliceworksbe.team.entity.Position;
import lombok.Builder;

@Builder
public record PositionResponseDto(Long id, String name) {
    public static PositionResponseDto from(Position position) {
        return PositionResponseDto.builder()
                .id(position.getId())
                .name(position.getName())
                .build();
    }
}
