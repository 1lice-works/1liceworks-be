package com.elice.iliceworksbe.notification.repository;

import com.elice.iliceworksbe.notification.entity.EventNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<EventNotification, Long> {
    @Query("SELECT e FROM EventNotification e WHERE e.notifyTime BETWEEN :start AND :end")
    List<EventNotification> findByNotifyTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
