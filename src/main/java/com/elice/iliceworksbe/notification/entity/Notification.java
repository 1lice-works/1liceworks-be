package com.elice.iliceworksbe.notification.entity;

import com.elice.iliceworksbe.common.entity.MongoBaseEntity;
import com.elice.iliceworksbe.notification.dto.request.NotificationRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification extends MongoBaseEntity {

    @Id
    private String id;

    @Field(name = "message")
    private String message;

    @Field(name = "notify_time")
    private LocalDateTime notifyTime;

    @Field(name = "is_read")
    private boolean isRead = false;

    @Field(name = "is_sent")
    private boolean isSent = false;

    @Field(name = "user_id") // @ManyToOne 대신 user의 ID만 저장
    private String userId;

    public static Notification from(NotificationRequestDto requestDto) {
        return Notification.builder()
                .message(requestDto.message())
                .notifyTime(LocalDateTime.now())
                .isRead(false)
                .isSent(false)
                .userId(String.valueOf(requestDto.userId()))
                .build();
    }

}