package com.bpmonitor.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bpmonitor.customExceptions.DeletionOfResultsException;
import com.bpmonitor.customExceptions.EntityNotFoundException;
import com.bpmonitor.models.Activity;
import com.bpmonitor.models.TaskError;
import com.bpmonitor.repositories.TaskErrorRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;



/**
 * Service class responsible for CRUD of taskError table.
 * also contains methods for deletion based on time, counting and exists.
 * @author joao7
 */
@Service
@Slf4j
public class TaskErrorService {

	
	private final TaskErrorRepository taskErrorRepo;
	
	private final EmailNotificationService EmailNotificationService;
	

	public TaskErrorService(
			TaskErrorRepository taskErrorRepo,
			EmailNotificationService EmailNotificationService
			) {
    	this.taskErrorRepo = taskErrorRepo;    	    	
    	this.EmailNotificationService = EmailNotificationService;
    }
	
	 private String extractResponsibleParty(String workloadJson) {
		 try {
	            ObjectMapper mapper = new ObjectMapper();
	            JsonNode node = mapper.readTree(workloadJson);
	            String s =  node.get("RESPONSIBLEPARTY").asText();
	            return s;
	        } catch (Exception e) {
	        	log.debug("something went wrong extractingResponsibleParty" + workloadJson);
	        	return "unknown@email.com";
	        }
		 
	    }
	 
	public void execute(TaskError error) {
        // Extract the responsible party from the workload
        String responsible = extractResponsibleParty(error.getWorkload());

        // Notify them (email, Slack, etc)
        EmailNotificationService.notifyResponsibleParty(responsible, error.getErrorDescription());

        // Maybe do additional stuff like logging or retry logic
    }
	
	
       	
	/**
	 * creates a TaskError.
	 * @param TaskError - TaskError object
	 */
	@Transactional
	public void createTaskError(TaskError taskError) {
		saveTaskError(taskError); // I save a TaskValidationResult
	}
	
	 /**
	  * this method deletes all task errors that are
	  * older than one week.
	  */
  @Transactional
  public void deleteOldTaskErrors() {
      try {
          LocalDateTime now = LocalDateTime.now(); // I check for the current time
    
          
     
          LocalDateTime oneWeekAgo = now.minus(1, ChronoUnit.WEEKS); // I get 1 week ago.
          List<TaskError> Errors = 
        		  taskErrorRepo.findByTimestampBefore(oneWeekAgo);
          taskErrorRepo.deleteAll(Errors); // and delete them
          log.info("Deleted {} failed task errors older than 1 week.", Errors.size());

      } catch (RuntimeException e) {
          log.error("Something went wrong with deleting old task errors: {}", e.getMessage());
          throw new DeletionOfResultsException("Failed to delete task errors");
      }
  }
	
    /**
     * Saves a given TaskError into the Database.
     * @param taskError - object to save.
     * @return taskError that's been saved.
     */
    public TaskError saveTaskError(TaskError taskError) {
    	try {
    	 return this.taskErrorRepo.save(taskError);
    	}catch(RuntimeException e){
    		log.error("something went wrong with saveTaskError method {}", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * Fetches all TaskErrors from the DB. 
     * @return List of TaskErrors with all records in the DB.
     */
    @Transactional (readOnly = true)
    public List<TaskError> findAllTaskErrors(){
    	try {
    		return this.taskErrorRepo.findAll();
    	}catch(RuntimeException e) {
    		log.error("something went wrong at findAllTaskErrors method : {}", e.getMessage());
    		throw e;
    	}
    }
    	
    /**
     * Fetches an TaskError by it's id.
     * @param id - id of the TaskError we wish to fetch from the DB.
     * @return TaskError fetched from the DB.
     */
    @Transactional (readOnly = true)
    public Optional<TaskError> getTaskErrorByID(Long id){
    		if( id <= 0) {return Optional.empty();}
    		try {
    			return this.taskErrorRepo.findById(id);
    		}catch(EntityNotFoundException e) {
    			log.error("couldn't find TaskError for ID : {}" , id);
    			throw e;
    		}
    	}
    	
    /**
     * Counts all TaskErrors in the DB.
     * @return long value representing the amount of TaskError registries in the DB.
     */
    @Transactional (readOnly = true)
    public long countAllTaskErrors() {
    	
    	try {
    		return this.taskErrorRepo.count();
    	}catch(RuntimeException e) {
    		log.error("something went wrong with countAllTaskErrors method : {}", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * checks if a TaskError exists in the DB by given id.
     * @param id - id you wish to check.
     * @return true if it exists, false otherwise.
     */
    @Transactional (readOnly = true)
    public boolean existsTaskErrorByID(Long id) {
    	if(id <= 0 ) { return false;}
    	try {
    		return this.taskErrorRepo.existsById(id);
    	}catch(RuntimeException e) {
    		log.error("something went wrong with the existsTaskErrorByID method : {} ", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * deletes a taskError by it's ID.
     * @param id - id of the taskError we wish to delete.
     * @return true if all went well, false otherwise.
     */
    @Transactional 
    public boolean deleteTaskErrorById(Long id) {
    	if(id <= 0) {  throw new IllegalArgumentException("Invalid ID: ID must be positive.");}
    	try{
    	 this.taskErrorRepo.deleteById(id);
    	 return true;
    	}catch(RuntimeException e) {
    		log.error("something went wrong with"
    				+ " deleteTaskErrorById method {}", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * Fetches a TaskErrors tied to a TaskValidationResult.
     * @param subprocessID - ID of the subprocess we want the activities of.
     * @return a list of activities tied to the subprocess.
     */
    @Transactional(readOnly = true)
    public List<TaskError> findTaskErrorByTaskValidationResult_Task_TaskID(Long TaskValResultID){
    	
    	try {
    		return this.taskErrorRepo.findTaskErrorByTaskValidationResult_Task_TaskID(TaskValResultID);
        	}catch(RuntimeException e){
        		log.error("something went wrong with findActivityBySubprocess_SubprocessID method {} ", e.getMessage() );
        		throw e;
        	}
    }
    
    
}
