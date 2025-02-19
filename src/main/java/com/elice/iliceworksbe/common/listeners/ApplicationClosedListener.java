package com.elice.iliceworksbe.common.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import static com.elice.iliceworksbe.common.listeners.DiscordNotification.sendDiscordNotification;

/*
서버 정상 종료 시
 */
@Slf4j
@Component
public class ApplicationClosedListener implements ApplicationListener<ContextClosedEvent> {

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        ApplicationPid applicationPid = new ApplicationPid();
        log.info("Application Closed : pid={}", applicationPid);
        sendDiscordNotification(event, applicationPid.toString());
    }
}
