package com.elice.iliceworksbe.notification.repository;

import com.elice.iliceworksbe.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findTop50ByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(Long userId, LocalDateTime createdAt);
    @Modifying
    @Query("UPDATE Notification e SET e.isRead = true WHERE e.user.id = :userId AND e.isRead = false")
    void markAllAsReadByUserId(@Param("userId") Long userId);
}
