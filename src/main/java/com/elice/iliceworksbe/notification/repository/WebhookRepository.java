package com.elice.iliceworksbe.notification.repository;

import com.elice.iliceworksbe.notification.entity.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WebhookRepository extends JpaRepository<Webhook, Long> {
    Optional<Webhook> findByCalendarId(Long calendarId);
}
