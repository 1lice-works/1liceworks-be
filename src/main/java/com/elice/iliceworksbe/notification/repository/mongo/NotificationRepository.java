package com.elice.iliceworksbe.notification.repository.mongo;


import com.elice.iliceworksbe.notification.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    @Query("{ 'userId': ?0, 'isSent': false }")
    List<Notification> findUnsentNotifications(String userId);

    boolean existsByUserIdAndIsReadFalse(String userId);

}
