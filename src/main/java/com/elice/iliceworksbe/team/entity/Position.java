package com.elice.iliceworksbe.team.entity;

import com.elice.iliceworksbe.common.entity.BaseEntity;
import com.elice.iliceworksbe.team.dto.position.PositionRequestDto;
import com.elice.iliceworksbe.team.dto.position.PositionUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "POSITION")
@AuditOverride(forClass = BaseEntity.class)
public class Position extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public void update(PositionUpdateDto positionUpdateDto) {
        this.name = positionUpdateDto.name();
    }

    public static Position from(PositionRequestDto requestDto) {
        return Position.builder()
                .name(requestDto.name())
                .build();
    }
}
