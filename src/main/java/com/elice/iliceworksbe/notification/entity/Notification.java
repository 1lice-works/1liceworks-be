package com.elice.iliceworksbe.notification.entity;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.common.entity.BaseEntity;
import com.elice.iliceworksbe.notification.dto.request.NotificationRequestDto;
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
@Table(name = "NOTIFICATION")
@AuditOverride(forClass = BaseEntity.class)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
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

    public static Notification from(NotificationRequestDto requestDto) {
        return Notification.builder()
                .message(requestDto.message())
                .notifyTime(LocalDateTime.now())
                .build();
    }

    public void assignUser(User user) {
        this.user = user;
    }
}
