package com.elice.iliceworksbe.notification.entity;

import com.elice.iliceworksbe.calendar.dto.request.PostTeamEventRequestDto;
import com.elice.iliceworksbe.calendar.entity.Event;
import com.elice.iliceworksbe.common.entity.BaseEntity;
import com.elice.iliceworksbe.notification.dto.request.EventReminderRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EVENT_REMINDER")
@AuditOverride(forClass = BaseEntity.class)
public class EventReminder extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_reminder_id")
    private Long id;

    @Column(name = "notify_time")
    private LocalDateTime notifyTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    public static EventReminder from(EventReminderRequestDto requestDto) {
        return EventReminder.builder()
                .notifyTime(requestDto.notifyTime())
                .build();
    }

    public static EventReminder from(PostTeamEventRequestDto.EventReminderDto eventReminderDto) {
        return EventReminder.builder()
                .notifyTime(eventReminderDto.notifyTime())
                .build();
    }

    public void assignEvent(Event event) {
        this.event = event;
    }

}
