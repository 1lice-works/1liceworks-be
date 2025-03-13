package com.elice.iliceworksbe.calendar.entity;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.common.entity.BaseEntity;
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
@Table(name = "EVENT_PARTICIPANT")
@AuditOverride(forClass = BaseEntity.class)
public class EventParticipant extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_participant_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static EventParticipant of(Event event, User user) {
        return EventParticipant.builder()
                .event(event)
                .user(user)
                .build();
    }
}
