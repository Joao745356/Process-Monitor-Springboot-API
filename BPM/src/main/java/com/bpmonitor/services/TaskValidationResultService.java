package com.bpmonitor.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bpmonitor.customExceptions.DeletionOfResultsException;
import com.bpmonitor.customExceptions.EntityNotFoundException;
import com.bpmonitor.enums.TaskStatus;
import com.bpmonitor.models.Task;
import com.bpmonitor.models.TaskValidationResult;
import com.bpmonitor.repositories.TaskRepository;
import com.bpmonitor.repositories.TaskValidationResultRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TaskValidationResultService {

	
private final TaskValidationResultRepository taskValResultRepo;
private final TaskRepository taskRepo;
	


	public TaskValidationResultService(
			TaskValidationResultRepository TaskValResultRepo,
			TaskRepository taskRepo
			) {
    
    	this.taskValResultRepo = TaskValResultRepo;
    	this.taskRepo = taskRepo;
    	
    }
	
	/**
	 * returns the latest result of a task.
	 * @param taskId
	 * @return
	 */
	public TaskValidationResult findLatestByTaskId(Long taskId) {
	    return taskValResultRepo
	        .findTopByTask_TaskIDOrderByTimestampDesc(taskId)
	        .orElseThrow(() -> new EntityNotFoundException("No results found for Task ID " + taskId));
	}

	
	
	/**
	 * void createValidationResult(TaskValidationResult result;
	 * void deleteOldTaskValidationResults();
	 * boolean UpdateTaskStatus(Long validationResultId);
	 * TaskValidationResult saveTaskValidationResult(TaskValidationResult TaskValidationResult);
	 * boolean deletetaskValidationResultById(Long id);
	 * boolean existsTaskValidationResultByID(Long id);
	 * long countAllTaskValidationResults();
	 * Optional<TaskValidationResult> getTaskErrorByID(Long id);
	 * List<TaskValidationResult> findAllTaskErrors();
	 * TaskValidationResult saveTaskValidationResult(TaskValidationResult TaskValidationResult);
	 */
	
    
    
	/**
	 * Creates a TaskValidationResult, and updates the status
	 * of it's parent task if and only if their statuses differ.
	 * @param result - TaskValidationResult object
	 */
	@Transactional
	public void createValidationResult(TaskValidationResult result) {
		saveTaskValidationResult(result); // I save a TaskValidationResult
	    updateTaskStatus(result.getTaskValidationResultID()); // I check and save the result of it's parent task if need be.
	}
	
	 /**
	  * This method deletes all succesful TaskValidationResults that are
	  * older than one day or longer, and it also deletes all failed TaskValidationResults 
	  * that are 3 days old or longer.
	  */
   @Transactional
   public void deleteOldTaskValidationResults() {
       try {
           LocalDateTime now = LocalDateTime.now(); // I check for the current time
     
           LocalDateTime oneDayAgo = now.minus(1, ChronoUnit.DAYS); // I get 1 day ago.
           List<TaskValidationResult> successResults = 
           		taskValResultRepo.findByStatusAndTimestampBefore(TaskStatus.SUCCESS, oneDayAgo); // I go get all succesful results.
           taskValResultRepo.deleteAll(successResults); // and delete them
           log.info("Deleted {} successful task validation results older than 1 day.", successResults.size());
      
           LocalDateTime oneWeekAgo = now.minus(1, ChronoUnit.WEEKS); // I get 1 week ago.
           List<TaskValidationResult> failedResults = 
           		taskValResultRepo.findByStatusAndTimestampBefore(TaskStatus.FAIL, oneWeekAgo);
           taskValResultRepo.deleteAll(failedResults); // and delete them
           log.info("Deleted {} failed task validation results older than 1 week.", failedResults.size());

       } catch (RuntimeException e) {
           log.error("Something went wrong with deleting old task validation results: {}", e.getMessage());
           throw new DeletionOfResultsException("Failed to delete task validation results");
       }
   }
	
	
	/**
	 * Updates the Status of my parent task.
	 * @param validationResultId - it's in the name.
	 * @return true if all went ok, false if not.
	 */
	@Transactional
	private boolean updateTaskStatus(Long validationResultId) {
	
		Optional<TaskValidationResult> optionalResult = taskValResultRepo.findById(validationResultId);
	    
	    if (!optionalResult.isPresent()) {return false;}
	    
	    TaskValidationResult result = optionalResult.get();
	    Task task = result.getTask();
	   
	    // if a task's status and it's result's status don't match
	    if (task.getTaskStatus() != result.getStatus()) {
	        task.setTaskStatus(result.getStatus()); // I set them so they match
	        taskRepo.save(task); // and save.
	    }
	    
	    return true;
	}
    
    /**
     * Saves a given TaskValidationResult into the Database.
     * @param TaskValidationResult - object to save.
     * @return TaskValidationResult that's been saved.
     */
    public TaskValidationResult saveTaskValidationResult(TaskValidationResult TaskValidationResult) {
    	try {
    	 return this.taskValResultRepo.save(TaskValidationResult);
    	}catch(RuntimeException e){
    		log.error("something went wrong with saveTaskValidationResult method {}", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * Fetches all TaskValidationResult from the DB. 
     * @return List of TaskValidationResult with all records in the DB.
     */
    public List<TaskValidationResult> findAllTaskValidationResults(){
    	try {
    		return this.taskValResultRepo.findAll();
    	}catch(RuntimeException e) {
    		log.error("something went wrong at findAllTaskErrors method : {}", e.getMessage());
    		throw e;
    	}
    }
    	
    /**
     * Fetches an TaskValidationResult by it's id.
     * @param id - id of the TaskValidationResult we wish to fetch from the DB.saveTaskValidationResult
     * @return TaskValidationResult fetched from the DB.
     */
    public Optional<TaskValidationResult> getTaskValResultByID(Long id){
    		if( id <= 0) {return Optional.empty();}
    		try {
    			return this.taskValResultRepo.findById(id);
    		}catch(EntityNotFoundException e) {
    			log.error("couldn't find TaskResult for ID : {}" , id);
    			throw e;
    		}
    	}
    	
    /**
     * Counts all TaskValidationResult in the DB.
     * @return long value representing the amount of TaskValidationResult registries in the DB.
     */
    public long countAllTaskValidationResults() {
    	
    	try {
    		return this.taskValResultRepo.count();
    	}catch(RuntimeException e) {
    		log.error("something went wrong with countAllTaskValidationResults method : {}", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * Checks if a TaskValidationResult exists in the DB by given id.
     * @param id - id you wish to check.
     * @return true if it exists, false otherwise.
     */
    public boolean existsTaskValidationResultByID(Long id) {
    	if(id <= 0 ) { return false;}
    	try {
    		return this.taskValResultRepo.existsById(id);
    	}catch(RuntimeException e) {
    		log.error("something went wrong with the existsTaskValidationResultByID method : {} ", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * Deletes a TaskValidationResult by it's ID.
     * @param id - id of the TaskValidationResult we wish to delete.
     * @return true if all went well, false otherwise.
     */
    public boolean deletetaskValidationResultById(Long id) {
    	if(id <= 0) {  throw new IllegalArgumentException("Invalid ID: ID must be positive.");}
    	try{
    	 this.taskValResultRepo.deleteById(id);
    	 return true;
    	}catch(RuntimeException e) {
    		log.error("something went wrong with"
    				+ " deletetaskValidationResultById method {}", e.getMessage());
    		throw e;
    	}
    }

    /**
     * Returns all results for a specific task
     * @param id - id of task you want the results of.
     * @return List of taskValidationResults that balong to the desired task.
   
	public List<TaskValidationResult> findTaskValidationResultsByTask_TaskID(Long id) {
		try {
    		return this.taskValResultRepo.findTaskValidationResultByTask_TaskID(id);
        	}catch(RuntimeException e){
        		log.error("something went wrong with findTaskValidationResultsByTask_TaskID method {} ", e.getMessage() );
        		throw e;
        	}
	}  */
	
}
