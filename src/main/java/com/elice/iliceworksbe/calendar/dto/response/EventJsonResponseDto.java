package com.elice.iliceworksbe.calendar.dto.response;

import com.elice.iliceworksbe.calendar.entity.Event;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventJsonResponseDto {
    private Long id;
    private String title;
    private LocalDateTime dtStartTime;
    private LocalDateTime dtEndTime;

    public static EventJsonResponseDto from(Event event) {
        return EventJsonResponseDto
                .builder()
                .id(event.getId())
                .title(event.getTitle())
                .dtStartTime(event.getDtStartTime())
                .dtEndTime(event.getDtEndTime())
                .build();
    }
}
