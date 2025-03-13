package com.elice.iliceworksbe.ai.service.impl;

import com.elice.iliceworksbe.ai.config.property.AIProperty;
import com.elice.iliceworksbe.ai.dto.*;
import com.elice.iliceworksbe.ai.service.AIService;
import com.elice.iliceworksbe.calendar.dto.response.EventJsonResponseDto;
import com.elice.iliceworksbe.calendar.service.EventService;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final EventService eventService;

    private final AIProperty aiProperty;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .findAndRegisterModules();

    @Override
    public GenerateScheduleResponseDto generateSchedule(GenerateScheduleRequestDto requestDto) {
        try {
            String url = aiProperty.getFlaskUrl() + "/generate_schedule";

            String requestBodyJson = objectMapper.writeValueAsString(requestDto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("외부 API 요청 실패 - 상태코드: {}, 응답: {} ", response.statusCode(), response.body());
                throw new BaseException(ErrorCode.EXTERNAL_API_ERROR);
            }

            log.info("generateSchedule Response: {}", response.body());
            return objectMapper.readValue(response.body(), GenerateScheduleResponseDto.class);
        } catch (Exception e) {
            log.error("Error in generateSchedule: ", e);
            return new GenerateScheduleResponseDto();
        }
    }

    @Override
    public FindFreeTimeResponseDto findFreeTime(Long teamCalendarId, FindFreeTimeRequestDto requestDto) {
        try {
            String url = aiProperty.getFlaskUrl() + "/find_free_time";
            List<EventJsonResponseDto> events = eventService.getEventsByDateAndParticipants(teamCalendarId, requestDto.getDate(), requestDto.getUserIds());

            FindFreeTimeInputDto inputDto = FindFreeTimeInputDto.of(requestDto.getDuration(), requestDto.getDate(), events);

            String requestBodyJson = objectMapper.writeValueAsString(inputDto);
            log.info("duration: {}, date: {}, events: {}", requestDto.getDuration(), requestDto.getDate(), events);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("외부 API 요청 실패 - 상태코드: {}, 응답: {} ", response.statusCode(), response.body());
                throw new BaseException(ErrorCode.EXTERNAL_API_ERROR);
            }

            log.info("findFreeTime Response: {}", response.body());
            FindFreeTimeResponseDto responseDto = objectMapper.readValue(response.body(), FindFreeTimeResponseDto.class);

            List<FindFreeTimeResponseDto.FreeTimeSlotDto> validFreeTimes = filterValidFreeTimes(responseDto.getFreeTimeSlotDtos(), events);

            return new FindFreeTimeResponseDto(validFreeTimes);
        } catch (Exception e) {
            log.error("Error in findFreeTime: ", e);
            return new FindFreeTimeResponseDto();
        }
    }

    private List<FindFreeTimeResponseDto.FreeTimeSlotDto> filterValidFreeTimes(
            List<FindFreeTimeResponseDto.FreeTimeSlotDto> freeTimeDtos,
            List<EventJsonResponseDto> events
    ) {
        return freeTimeDtos.stream()
                .filter(freeTime -> events.stream().noneMatch(event ->
                        event.getDtStartTime().isBefore(freeTime.getEndTime()) && event.getDtEndTime().isAfter(freeTime.getStartTime())
                ))
                .collect(Collectors.toList());
    }
}
