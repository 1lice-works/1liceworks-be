package com.elice.iliceworksbe.common.listeners;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static com.elice.iliceworksbe.common.listeners.DiscordNotification.sendDiscordNotification;

/*
서버 정상적으로 구성 완료 시
 */
@Slf4j
@Component
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent>{

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ApplicationPid applicationPid = new ApplicationPid();
        log.info("Application Start : pid={}", applicationPid);
        sendDiscordNotification(event, applicationPid.toString());
    }
}
