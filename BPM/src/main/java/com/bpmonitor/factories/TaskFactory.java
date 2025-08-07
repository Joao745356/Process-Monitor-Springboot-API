package com.bpmonitor.factories;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bpmonitor.DTOs.CreateTaskDTO;
import com.bpmonitor.enums.TaskRecurrence;
import com.bpmonitor.enums.TaskStatus;
import com.bpmonitor.enums.TaskType;
import com.bpmonitor.models.Activity;
import com.bpmonitor.models.Task;
import com.bpmonitor.services.ActivityService;
import com.bpmonitor.tasks.CheckTomcatServiceTask;
import com.bpmonitor.tasks.HttpCheckTask;
import com.bpmonitor.tasks.PingServerTask;
import com.bpmonitor.tasks.QueryDBTask;
import com.bpmonitor.tasks.SFTPConnectionTask;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TaskFactory {
	
	  private final ObjectMapper objectMapper = new ObjectMapper();
	    private final ActivityService actService;

	    private final Map<TaskType, Class<? extends Task>> taskClassMap = new HashMap<>();

	    @Autowired
	    public TaskFactory(ActivityService actService) {
	        this.actService = actService;
	    }

	    @PostConstruct
	    private void init() {
	        taskClassMap.put(TaskType.PINGSERVERTASK, PingServerTask.class);
	        //taskClassMap.put(TaskType.CHECKPANCAKESTASK, PingServerTask.class);
	        //taskClassMap.put(TaskType.CHECKTOMCATSERVICETASK, CheckTomcatServiceTask.class);
	        taskClassMap.put(TaskType.QUERYDBTASK, QueryDBTask.class);
	        //taskClassMap.put(TaskType.SFTPCONNECTIONTASK, SFTPConnectionTask.class);
	        //taskClassMap.put(TaskType.CHECKWEBCONNECTIONTASK, CheckWebConnectionTask.class);
	    }

	    /**
	     * instantiates all the tasks at launch time!
	     * @param dto - dto of the task to create.
	     * @return
	     */
	    public Task createExecutableTask(CreateTaskDTO dto) {
	    	log.debug("I arrived createExecutableTask");
	        Class<? extends Task> taskClass = taskClassMap.get(dto.getTaskType());

	        if (taskClass == null) {
	            throw new IllegalArgumentException("Unsupported task type: " + dto.getTaskType());
	        }

	        Activity activity = actService.getActivityById(dto.getActivityID())
	                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

	        log.debug("I found the activity!");
	        
	        String workloadStr;
	        log.debug("I'm gonna read the workload");
	        try {
	            workloadStr = objectMapper.writeValueAsString(dto.getWorkload());
	        } catch (IOException e) {
	            throw new RuntimeException("Failed to serialize workload", e);
	        }

	        
	        log.debug("gonna create the task!");
	        try {
	            return taskClass
	                .getConstructor(String.class,
	                		String.class,
	                		TaskStatus.class,
	                		Activity.class,
	                		TaskType.class,
	                		TaskRecurrence.class,
	                		String.class)
	                .newInstance(
	                        dto.getTaskName(),
	                        dto.getTaskDescription(),
	                        dto.getTaskStatus(),
	                        activity,
	                        dto.getTaskType(),
	                        dto.getRecurrence(),
	                        workloadStr	                      
	                );
	        } catch (ReflectiveOperationException e) {
	        	log.debug("I messed up here ");
	            throw new RuntimeException("Failed to instantiate task of type: " + taskClass.getSimpleName(), e);
	        }
	    }
	    
}
