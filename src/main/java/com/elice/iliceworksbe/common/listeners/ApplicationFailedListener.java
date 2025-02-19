package com.elice.iliceworksbe.common.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static com.elice.iliceworksbe.common.listeners.DiscordNotification.sendDiscordNotification;

/*
애플리케이션이 시작 중에 실패하거나 실행 중 예외 발생하여 종료 될 때
 */
@Slf4j
@Component
public class ApplicationFailedListener implements ApplicationListener<ApplicationFailedEvent> {

    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        ApplicationPid applicationPid = new ApplicationPid();
        log.info("Application Failed : pid={}", applicationPid);
        sendDiscordNotification(event, applicationPid.toString());
    }
}
