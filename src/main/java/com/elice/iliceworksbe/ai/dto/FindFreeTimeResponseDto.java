package com.elice.iliceworksbe.ai.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FindFreeTimeResponseDto {

    @JsonProperty("freeTimeDtos")
    private List<FreeTimeSlotDto> freeTimeSlotDtos;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FreeTimeSlotDto {
        @JsonProperty("startTime")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime startTime;

        @JsonProperty("endTime")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime endTime;
    }
}
