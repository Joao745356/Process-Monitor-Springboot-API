package com.bpmonitor.schedulers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bpmonitor.enums.TaskRecurrence;
import com.bpmonitor.executors.TaskExecutorService;
import com.bpmonitor.factories.TaskFactory;
import com.bpmonitor.models.Task;
import com.bpmonitor.repositories.TaskRepository;
import com.bpmonitor.services.TaskService;

import lombok.extern.slf4j.Slf4j;


@Component // this is a component of my app, that is gonna schedule tasks.
@Slf4j
public class TaskRefractorScheduler {
	
	private final TaskRepository taskRepo; // vou ao repository de tasks
	
	private final TaskExecutorService taskExecutor; // responsible for executing the tasks themselves. 
	
	private final TaskFactory taskFactory; // responsible for injecting TaskValidationResultService's into tasks
	
	public TaskRefractorScheduler(
			final TaskRepository taskRepo, 
			final TaskExecutorService taskExecutor,
			final TaskFactory taskFactory) {
		this.taskRepo = taskRepo;
		this.taskExecutor = taskExecutor;
		this.taskFactory = taskFactory;
	}
	
	
	@Scheduled(fixedRate = 5 * 60 * 1000) // everyMinute
	public void Run5MinutesTasks() {
			log.info("I'm gonna run all minute tasks");
	        List<Task> tasks = taskRepo.findTaskByRecurrence(TaskRecurrence.EVERY_5_MINUTES); // I get all 5 minute tasks

	        for (Task task : tasks) {
	            try {            
	            	taskExecutor.executeControlTask(task);
	            } catch (Exception e) {
	                e.printStackTrace();
	                // Optionally: Save TaskError
	            }
	        }
	    }
	}

/**
	


	    @Scheduled(fixedRate = 60000) // Runs every minute
	    public void runScheduledTasks() {
	        List<Task> tasks = taskService.getTasksByRecurrence(TaskRecurrence.EVERYMINUTE);
	        for (Task task : tasks) {
	            taskExecutor.executeTask(task);
	        }
	    }
	    
	    
	    @Scheduled(fixedRate = 30 * 60000) // 30 minutes * 60,000 ms
	    public void runHalfHourlyTasks() {
	        List<Task> tasks = taskService.getTasksByRecurrence(TaskRecurrence.THIRTYMINUTES);
	        for (Task task : tasks) {
	            taskExecutor.executeTask(task);
	        }
	    }
	    
	    @Scheduled(fixedRate = 60 * 60000) // 60 minutes * 60,000 ms
	    public void runHourlyTasks() {
	        List<Task> tasks = taskService.getTasksByRecurrence(TaskRecurrence.HOURLY);
	        for (Task task : tasks) {
	            taskExecutor.executeTask(task);
	        }
	    }
	    
	    
	    @Scheduled(fixedRate = 24 * 60 * 60000) // 24 hours * 60 minutes * 60,000 ms
	    public void runDailyTasks() {
	        List<Task> tasks = taskService.getTasksByRecurrence(TaskRecurrence.DAILY);
	        for (Task task : tasks) {
	            taskExecutor.executeTask(task);
	        }
	    }*/

