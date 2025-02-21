package com.elice.iliceworksbe.calendar.repository;

import com.elice.iliceworksbe.calendar.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

}
