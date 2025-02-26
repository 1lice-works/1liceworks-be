package com.elice.iliceworksbe.calendar.repository;

import com.elice.iliceworksbe.calendar.entity.EventParticipant;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    List<EventParticipant> findByEvent(Event event);

    @Transactional
    @Modifying
    @Query("DELETE FROM EventParticipant ep WHERE ep.event.id = :eventId")
    void deleteByEventId(Long eventId);
}
