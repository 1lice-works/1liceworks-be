package com.elice.iliceworksbe.ai.service.impl;

import com.elice.iliceworksbe.ai.config.property.AIProperty;
import com.elice.iliceworksbe.ai.service.AIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final AIProperty aiProperty;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String generateSchedule(String prompt) {
        try {
            String url = aiProperty.getFlaskUrl() + "/generate_schedule";
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("prompt", prompt);

            String requestBodyJson = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("generateSchedule Response: {}", response.body());
            return response.body();
        } catch (Exception e) {
            log.error("Error in generateSchedule: ", e);
            return "{}";
        }
    }

    @Override
    public String findFreeTime(Map<String, Object> calendarData) {
        try {
            String url = aiProperty.getFlaskUrl() + "/find_free_time";

            String requestBodyJson = objectMapper.writeValueAsString(calendarData);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("findFreeTime Response: {}", response.body());
            return response.body();
        } catch (Exception e) {
            log.error("Error in findFreeTime: ", e);
            return "{}";
        }
    }
}
