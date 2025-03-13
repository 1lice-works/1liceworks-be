package com.elice.iliceworksbe.calendar.entity;

import com.elice.iliceworksbe.common.constant.CalendarType;
import com.elice.iliceworksbe.common.entity.BaseEntity;
import com.elice.iliceworksbe.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditOverride;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CALENDAR")
@AuditOverride(forClass = BaseEntity.class)
public class Calendar extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CalendarType type;

    @Column(name = "type_id", nullable = false)
    private Long typeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Calendar)) return false;

        Calendar calendar = (Calendar) o;
        return getId().equals(calendar.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
