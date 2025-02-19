package com.elice.iliceworksbe.common.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    private static void sendDiscordNotification(String message) {
        try {
            URL url = new URL(WEBHOOK_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String payload = "{\"content\": \"" + message + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            log.info("Discord Notification sent successfully.");
        } catch (Exception e) {
            log.warn("Error sending notification: " + e.getMessage());
        }
    }
}
