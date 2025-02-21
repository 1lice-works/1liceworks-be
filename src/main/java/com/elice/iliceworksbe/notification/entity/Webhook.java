package com.elice.iliceworksbe.notification.entity;

import com.elice.iliceworksbe.calendar.entity.Calendar;
import com.elice.iliceworksbe.common.constant.ContentType;
import com.elice.iliceworksbe.common.entity.BaseEntity;
import com.elice.iliceworksbe.notification.dto.request.WebhookRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "WEBHOOK")
@AuditOverride(forClass = BaseEntity.class)
public class Webhook extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "webhook_id")
    private Long id;

    @Column(name = "payload_url")
    private String payloadUrl;

    @Column(name = "content_type")
    @Enumerated(EnumType.STRING)
    private ContentType contentType;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    public static Webhook from(WebhookRequestDto requestDto){
        return Webhook.builder()
                .payloadUrl(requestDto.payloadUrl())
                .contentType(requestDto.contentType())
                .build();
    }

    public void assignCalendar(Calendar calendar){
        this.calendar = calendar;}
}
