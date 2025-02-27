package com.elice.iliceworksbe.calendar.repository;

import com.elice.iliceworksbe.calendar.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("""
    SELECT e FROM Event e
    JOIN e.calendar c
    JOIN EventParticipant ep ON ep.event = e
    WHERE ep.user.id IN :userIds
    AND c.id = :teamCalendarId
    AND (
        (e.dtStartTime BETWEEN :startDateTime AND :endDateTime)
        OR (e.dtEndTime BETWEEN :startDateTime AND :endDateTime)
        OR (e.dtStartTime <= :startDateTime AND e.dtEndTime >= :endDateTime)
    )
""")
    List<Event> findEventsByDateAndParticipants( // userIds 리스트에 있는 유저가 하나라도 속해있는 일정들을 가져오는 로직
            @Param("teamCalendarId") Long teamCalendarId,
            @Param("userIds") List<Long> userIds,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}
