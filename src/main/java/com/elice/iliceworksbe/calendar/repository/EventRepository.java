package com.elice.iliceworksbe.calendar.repository;

import com.elice.iliceworksbe.calendar.entity.Calendar;
import com.elice.iliceworksbe.calendar.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
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

    @Query("SELECT e FROM Event e " +
            "WHERE ((e.dtStartTime BETWEEN :startDate AND :endDate) " +
            "OR (e.dtEndTime BETWEEN :startDate AND :endDate))" +
            "AND (e.calendar = :calendar)")
    List<Event> findEventsWithinThreeMonthsByCalendar(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate,
                                                      @Param("calendar") Calendar calendar);


    @Query("SELECT e FROM Event e " +
            "WHERE e.title LIKE %:keyword% " +
            "AND e.calendar = :calendar ")
    List<Event> findByTitleContainingAndCalendar(
            @Param("keyword") String keyword,
            @Param("calendar") Calendar calendar);

    @Query("SELECT e FROM Event e " +
            "WHERE e.title LIKE %:keyword% " +
            "AND e.calendar = :calendar " +
            "AND e.privacy <> 'PRIVATE'")
    List<Event> findByTitleContainingAndCalendarAndNotPrivate(
            @Param("keyword") String keyword,
            @Param("calendar") Calendar calendar);
}
