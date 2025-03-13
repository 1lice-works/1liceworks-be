package com.elice.iliceworksbe.notification.repository;

import com.elice.iliceworksbe.notification.entity.EventReminder;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventReminderRepository extends JpaRepository<EventReminder, Long> {
    @EntityGraph(attributePaths = {"event"})
    @Query("SELECT e FROM EventReminder e WHERE e.notifyTime BETWEEN :start AND :end")
    Page<EventReminder> findByNotifyTimeBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    List<EventReminder> findAllByEventId(Long eventId);

    @Transactional
    @Modifying
    @Query("DELETE FROM EventReminder er WHERE er.event.id = :eventId")
    void deleteByEventId(Long eventId);
}
