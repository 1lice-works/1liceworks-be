package com.elice.iliceworksbe.common.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Slf4j
public class DiscordNotification {

    private static final String WEBHOOK_URL = System.getenv("DISCORD_WEBHOOK_1LICEWORKS_URL");

    public static void sendDiscordNotification(ApplicationEvent event, String applicationPid) {

        if (WEBHOOK_URL == null || WEBHOOK_URL.isEmpty()) {
            System.err.println("웹훅 URL이 설정되지 않았습니다.");
            return;
        }

        String message = "";

        if (event instanceof ApplicationFailedEvent) {
            message = "💥 Server Failed";
        } else if (event instanceof ApplicationReadyEvent) {
            message = "🚀 Server Started";
        } else if (event instanceof ContextClosedEvent) {
            message = "🔒 Server Closed";
        }

        sendDiscordNotification("PID= " + applicationPid + " " + message);
    }


    public static void sendDiscordNotification(String message) {
        try {
            if (WEBHOOK_URL == null || WEBHOOK_URL.isEmpty()) {
                log.warn("❌ 웹훅 URL이 설정되지 않았습니다.");
                return;
            }

            // JSON 페이로드 생성
            String payload = "{\"content\": \"" + message + "\"}";

            // HttpClient 객체 생성 (싱글톤으로 관리 가능)
            HttpClient client = HttpClient.newHttpClient();

            // HttpRequest 생성
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(WEBHOOK_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();


            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 204) {
                log.info("✅ Discord Notification sent successfully.");
            } else {
                log.warn("⚠️ Failed to send Discord Notification. Response: " + response.body());
            }
        } catch (Exception e) {
            log.warn("❌ Error sending notification: " + e.getMessage(), e);
        }
    }
}
