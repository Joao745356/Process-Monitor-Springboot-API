package com.bpmonitor.executors;


import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bpmonitor.enums.TaskStatus;
import com.bpmonitor.eventListeners.TaskStatusChangedEvent;
import com.bpmonitor.factories.TaskFactory;
import com.bpmonitor.models.Task;
import com.bpmonitor.models.TaskError;
import com.bpmonitor.models.TaskValidationResult;
import com.bpmonitor.repositories.TaskErrorRepository;
import com.bpmonitor.repositories.TaskValidationResultRepository;
import com.bpmonitor.services.ActivityService;
import com.bpmonitor.services.EmailNotificationService;
import com.bpmonitor.services.TaskErrorService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskExecutorService {


	private final TaskValidationResultRepository taskValRepo;
	private final TaskErrorRepository taskErrorRepo;
	private final TaskErrorService taskErrorService;
	private final ActivityService actService;
	private final EmailNotificationService emailService;
	private final TaskFactory taskFactory;
	
	private ApplicationEventPublisher eventPublisher;
	
	public TaskExecutorService(
			TaskValidationResultRepository taskValRepo,
			TaskFactory taskFactory,
			 TaskErrorRepository taskErrorRepo,
			 TaskErrorService taskErrorService,
			 EmailNotificationService emailService,
			 ActivityService actService,
			 ApplicationEventPublisher eventPublisher) {
		this.taskValRepo = taskValRepo;
		this.taskFactory = taskFactory;
		this.taskErrorRepo = taskErrorRepo;
		this.taskErrorService = taskErrorService; 
		this.emailService = emailService;
		this.actService = actService;
		this.eventPublisher = eventPublisher;
	}
	
	
	@Async("taskExecutor")
	@Transactional
	public void executeControlTask(Task task) {
		
		  try {
		        TaskValidationResult result = task.execute(); // execute task

		        log.error(result.getTask().getWorkload()); // is my task null?
		        log.error("this is my description for this result {}", result.getResultDescription()); // why is this empty
		        // Save TaskValidationResult first
		        saveTaskValidationResultAndCommit(result);
		        log.error("result id is {}", result.getTaskValidationResultID());
		        
		       

		        // Ensure the error creation happens after commit of the result
		        if (result.getStatus() == TaskStatus.FAIL) {		        
		            createTaskError(result);			            
		        }
		        
		        // I publish my taskStatusChangedEvent!
		        //This will trigger @EventListener methods 
		        /**
		         * This way, the event handling is separated 
		         * from the business logic, and the status updates are done 
		         * asynchronously and in the right order: task -> activity -> subprocess -> system/interface.
		         */
		       
		        eventPublisher.publishEvent(new TaskStatusChangedEvent(task, result));
		        log.error("I published the event with {} taskID, and {} result", task.getTaskID(), result.getStatus());

		    } catch (Exception e) {
		        log.error("Something went wrong saving TaskValidationResult in TaskExecutorService");
		        e.printStackTrace();
		    }
		
	}
	
	


	@Transactional
	public void saveTaskValidationResultAndCommit(TaskValidationResult result) {
	    taskValRepo.save(result); 
	    taskValRepo.flush(); // Forces DB write immediately
	    // No explicit wait, the @Transactional annotation will manage commit
	}
	
	private String extractResponsiblePartyFromWorkload(String workload) {
	    try {
	        // Parse the JSON string
	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode workloadNode = objectMapper.readTree(workload);  // Parse JSON string into a JsonNode
	        
	        // Extract the RESPONSIBLEPARTY field from the JSON
	        JsonNode responsiblePartyNode = workloadNode.get("RESPONSIBLEPARTY");
	        
	        if (responsiblePartyNode != null && responsiblePartyNode.isTextual()) {
	            return responsiblePartyNode.asText();  // Return the value as text (e.g., email)
	        } else {
	            return "default@domain.com";  // Default value if the field is not found
	        }
	    } catch (Exception e) {
	        log.error("Error parsing workload JSON", e);
	        return "default@domain.com";  // Fallback value if there's an issue
	    }
	}
	
	@Transactional
	public void createTaskError(TaskValidationResult result) {
		try {
	        
	        TaskError error = new TaskError(
	            result,
	            LocalDateTime.now(),
	            result.getResultDescription(),
	            result.getTask().getWorkload()
	        );
	        
	        // Parse the workload JSON
	        String workload = result.getTask().getWorkload();
	        String responsibleParty = extractResponsiblePartyFromWorkload(workload);
	        
	       // emailService.notifyResponsibleParty(responsibleParty,
	       // 		result.getResultDescription());
	        
	        log.error("email has been sent ");
	        
	        result.getTaskErrors().add(error); // associate error with result
	        taskErrorRepo.save(error);
	       

	    } catch (Exception e) {
	        log.error("Failed to create TaskError for result ID: " + result.getTaskValidationResultID(), e);
	    }

	}

}
