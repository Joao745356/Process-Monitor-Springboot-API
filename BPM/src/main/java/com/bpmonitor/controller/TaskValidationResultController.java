package com.bpmonitor.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bpmonitor.DTOs.response.TaskErrorResponseDTO;
import com.bpmonitor.DTOs.response.TaskValidationResponseDTO;
import com.bpmonitor.customExceptions.EntityNotFoundException;
import com.bpmonitor.enums.TaskStatus;
import com.bpmonitor.models.TaskError;
import com.bpmonitor.models.TaskValidationResult;
import com.bpmonitor.services.TaskValidationResultService;


@RestController
@RequestMapping("/TaskValidationResults")
public class TaskValidationResultController {
	
	private final TaskValidationResultService taskValResService;
	
	public TaskValidationResultController(TaskValidationResultService taskVal) {
		this.taskValResService = taskVal;
	}

	
	/**
	 * Fetches all TaskValidationResults from the DB.
	 * @return - a response entity containing all
	 *  activities if the operation is succesful.
	 */
	 @GetMapping("/getAll")
	 public ResponseEntity<?> getAllTaskValidationResults() {
	    	try {
	    	   List<TaskValidationResult> results =  taskValResService.findAllTaskValidationResults();
	    	   return ResponseEntity.status(HttpStatus.OK).body(results);
	       }catch(Exception e) {
	         	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching all taskValResults.");
	       }    	
	 }
	 
	    @DeleteMapping("delete/{id}")
	    public ResponseEntity<?> deleteTaskResult(@PathVariable Long id) {
	    	  try {
	    		  taskValResService.deletetaskValidationResultById(id);      
	    	      return ResponseEntity.status(HttpStatus.OK).body("taskResult deleted succesfully.");
	    	    } catch (IllegalArgumentException e) {
	    	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    	    } catch (Exception e) {
	    	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the task");
	    	    }
	    }
	 
	 /**
	  * Fetches a taskValResults by it's Id.
	  * @param id - id of the result I wish to retrieve.
	  * @return - responseEntity containing the requested activity.
	 
	 @GetMapping("/id/{id}")
	 public ResponseEntity<?> getTaskValidationResultByID(@PathVariable Long id){
		 try {
			 Optional<TaskValidationResult> result =  taskValResService.getTaskValResultByID(id);
			 return ResponseEntity.status(HttpStatus.OK).body(result);
		 }catch(EntityNotFoundException e) {
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No taskValResults found for id {"+id+"}");
		 }
	 } */
	 
	 /**
	  * Fetches a taskValResults by it's Id.
	  * @param id - id of the validation result we want.
	  * @return
	  */
	
	 @GetMapping("/id/{id}")
	 
	 public ResponseEntity<?> findTaskResultById(@PathVariable Long id) {
		 Optional<TaskValidationResult> result  = taskValResService.getTaskValResultByID(id);
		 
		 
		 
		 try {
			 TaskValidationResponseDTO resultdto = new TaskValidationResponseDTO(
					 result.get().getTaskValidationResultID(),
					 result.get().getStatus(), 
					 result.get().getTimestamp(),
					 result.get().getResultDescription(),
					 null);
			 
			 
					
			 
			 return ResponseEntity.status(HttpStatus.OK).body(resultdto);
		 }catch(EntityNotFoundException e) {
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No taskValResults found for id {"+id+"}");
		 }
		    
		    
		
	                    
		}
	 
	 
	 private TaskValidationResponseDTO toDTO(TaskValidationResult entity) {
		    TaskValidationResponseDTO dto = new TaskValidationResponseDTO();
		    dto.setTaskValidationResultID(entity.getTaskValidationResultID());
		    dto.setStatus(entity.getStatus());
		    dto.setTimestamp(entity.getTimestamp());
		    dto.setResultDescription(entity.getResultDescription());
		    // Map the list of taskErrors entities to their DTOs
		    if (entity.getTaskErrors() != null) {
		        List<TaskErrorResponseDTO> errorDTOs = entity.getTaskErrors().stream()
		            .map(this::taskErrorToDTO)
		            .collect(Collectors.toList());
		        dto.setTaskErrors(errorDTOs);
		    }

		    return dto;
		}

		private TaskErrorResponseDTO taskErrorToDTO(TaskError entity) {
		    TaskErrorResponseDTO dto = new TaskErrorResponseDTO();
		    dto.setId(entity.getTaskErrorID());
		    dto.setDescription(entity.getErrorDescription());
		    dto.setTimestamp(entity.getTimestamp());
		    return dto;
		}
	 /**
	  * Fetches all taskValResults belonging to a specific task.
	  * @param id - id of the activity I wish to retrieve.
	  * @return - responseEntity containing the requested activity.
	  
	 @GetMapping("/id/{id}")
	 public ResponseEntity<?> getTaskValidationResultsByTaskID(@PathVariable Long id){
		 try {
		 List<TaskValidationResult> results =  taskValResService.findTaskValidationResultsByTask_TaskID(id);
		 return ResponseEntity.status(HttpStatus.OK).body(results);
		 }catch(EntityNotFoundException e) {
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No TaskResults exist for Task with id {"+id+"}");
		 }
	 
	 }*/
}
