package com.bpmonitor.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig implements AsyncConfigurer {

	
	/**
	 * for async methods
	 * @return
	 */
	 @Bean(name = "taskExecutor")  
	    public ThreadPoolTaskExecutor taskExecutor() {
	        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	        executor.setCorePoolSize(16);      // Minimum threads kept alive
	        executor.setMaxPoolSize(64);      // Scales up under load
	        executor.setQueueCapacity(500);    // Tasks queue if all threads are busy
	        executor.setThreadNamePrefix("Async-");  // Helps in debugging
	        executor.initialize();
	        return executor;
	    }
	 
	 @Override
	 public Executor getAsyncExecutor() {
	     return taskExecutor(); // delegate to your bean
	 }

}
