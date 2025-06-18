package com.saminavi.attendancePortal.SaminaviAttendanceApplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Minimum number of threads
        executor.setMaxPoolSize(20); // Maximum number of threads
        executor.setQueueCapacity(100); // Queue capacity for tasks
        executor.setThreadNamePrefix("EmailAsync-");
        executor.initialize();
        return executor;
    }
} 