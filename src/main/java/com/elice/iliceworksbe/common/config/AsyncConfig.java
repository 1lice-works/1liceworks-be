package com.elice.iliceworksbe.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);  // 기본 스레드 개수
        executor.setMaxPoolSize(64);   // 최대 스레드 개수
        executor.setQueueCapacity(500); // 대기열 크기
        executor.setThreadNamePrefix("Async-Thread-");
        executor.initialize();
        return executor;
    }
}
