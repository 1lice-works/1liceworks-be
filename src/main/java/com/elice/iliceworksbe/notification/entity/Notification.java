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
    @Column(name = "notification_id", nullable = false)
    private Long id;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "notify_time", nullable = false)
    private LocalDateTime notifyTime;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "is_sent", nullable = false)
    private boolean isSent = false;

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
