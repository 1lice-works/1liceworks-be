package com.elice.iliceworksbe.notification.repository;

import com.elice.iliceworksbe.notification.entity.EventReminder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventReminderRepository extends JpaRepository<EventReminder, Long> {
}
