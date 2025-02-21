package com.elice.iliceworksbe.notification.repository;

import com.elice.iliceworksbe.notification.entity.EventReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventReminderRepository extends JpaRepository<EventReminder, Long> {
    @Query("SELECT e FROM EventReminder e WHERE e.notifyTime BETWEEN :start AND :end")
    List<EventReminder> findByNotifyTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    List<EventReminder> findAllByEventId(Long eventId);
    void deleteByEventId(Long eventId);
}
