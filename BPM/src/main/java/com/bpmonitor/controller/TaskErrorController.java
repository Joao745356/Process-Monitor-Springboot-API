package com.bpmonitor.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bpmonitor.customExceptions.EntityNotFoundException;
import com.bpmonitor.models.TaskError;
import com.bpmonitor.models.TaskValidationResult;
import com.bpmonitor.services.TaskErrorService;

@RestController
@RequestMapping("/TaskErrors")
public class TaskErrorController {
	
	private final TaskErrorService taskErrorService;
	
	
	public TaskErrorController(TaskErrorService taskErrorService) {
		this.taskErrorService = taskErrorService;
	}
	
	
	/**
	 * Fetches all TaskErrors from the DB.
	 * @return - a response entity containing all
	 *  TaskErrors if the operation is succesful.
	 */
	 @GetMapping("/getAll")
	 public ResponseEntity<?> getAllTaskErrors() {
	    	try {
	    	   List<TaskError> results =  taskErrorService.findAllTaskErrors();
	    	   return ResponseEntity.status(HttpStatus.OK).body(results);
	       }catch(Exception e) {
	         	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching all taskErrors.");
	       }
	    	
	 }
	 
	 /**
	  * Fetches a TaskError by it's Id.
	  * @param id - id of taskError to retrieve.
	  * @return - responseEntity containing the requested TaskError.
	  */
	 @GetMapping("/id/{id}")
	 public ResponseEntity<?> getTaskErrorByID(@PathVariable Long id){
		 try {
			 Optional<TaskError> taskError =  taskErrorService.getTaskErrorByID(id);
			 return ResponseEntity.status(HttpStatus.OK).body(taskError);
		 }catch(EntityNotFoundException e) {
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No taskError found for id {"+id+"}");
		 }
	 }
	 
	 
	 /**
	  * Fetches all taskErrors belonging to a specific task.
	  * @param id - id of the task whose task errors I wish to retrieve.
	  * @return - responseEntity containing the requested taskErrors.
	  */
	 @GetMapping("task/id/{id}")
	 public ResponseEntity<?> getTaskErrorsByTaskValidationResul_Task_TaskID(@PathVariable Long id){
		 try {
		 List<TaskError> results =  taskErrorService.findTaskErrorByTaskValidationResult_Task_TaskID(id);
		 return ResponseEntity.status(HttpStatus.OK).body(results);
		 }catch(EntityNotFoundException e) {
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No TaskErrors exist for Task with id {"+id+"}");
		 }
	 
	 }

}
