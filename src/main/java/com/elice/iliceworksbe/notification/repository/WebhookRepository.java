package com.elice.iliceworksbe.notification.repository;

import com.elice.iliceworksbe.notification.entity.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookRepository extends JpaRepository<Webhook, Long> {
}
