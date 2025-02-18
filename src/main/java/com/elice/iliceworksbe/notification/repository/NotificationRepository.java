package com.elice.iliceworksbe.notification.repository;

import com.elice.iliceworksbe.notification.entity.EventNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<EventNotification, Long> {
    List<EventNotification> findByNotifyTime(LocalDateTime notifyTime);
}
