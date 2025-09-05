package com.intuitech.cvprocessor.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for async processing
 * 
 * Sets up thread pool for asynchronous operations.
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    /**
     * Create task executor for async operations
     * 
     * @return configured thread pool executor
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        log.info("Configuring async task executor");

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("CV-Processing-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();

        log.info("Async task executor configured with core pool size: {}, max pool size: {}", 
            executor.getCorePoolSize(), executor.getMaxPoolSize());

        return executor;
    }
}
