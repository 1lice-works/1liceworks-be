package com.elice.iliceworksbe.notification.entity;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.calendar.entity.Event;
import com.elice.iliceworksbe.common.entity.BaseEntity;
import com.elice.iliceworksbe.notification.dto.request.EventNotificationRequestDto;
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
@Table(name = "EVENT_NOTIFICATION")
@AuditOverride(forClass = BaseEntity.class)
public class EventNotification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_notification_id")
    private Long id;

    @Column(name = "message")
    private String message;

    @Column(name = "notify_time")
    private LocalDateTime notifyTime;

    @Column(name = "is_read")
    private Boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    public static EventNotification from(EventNotificationRequestDto requestDto, User user, Event event){
        return EventNotification.builder()
                .notifyTime(requestDto.notifyTime())
                .message(requestDto.message())
                .isRead(false)
                .user(user)
                .event(event)
                .build();
    }
}
