package com.bpmonitor.controller;


import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bpmonitor.DTOs.CreateTaskDTO;
import com.bpmonitor.DTOs.response.ProcessTreeNodeResponseDTO;
import com.bpmonitor.DTOs.response.TaskTreeResponseDTO;
import com.bpmonitor.enums.TaskRecurrence;
import com.bpmonitor.enums.TaskType;
import com.bpmonitor.models.Task;
import com.bpmonitor.services.TaskService;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/Tasks")
public class TaskController {

		private final TaskService taskService;

    
		public TaskController(TaskService taskService) {
        this.taskService = taskService;
		}
	  
	    @GetMapping("/getAll")
	    public ResponseEntity<?> getAllTasks() {
	    	log.info("Received request to /getAll");
	       
	    	try {
	    	   List<Task> tasks =  taskService.getAllTasks();
	    	   return ResponseEntity.status(HttpStatus.OK).body(tasks);
	       }catch(Exception e) {
   	         	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching all tasks");
	       }
	    	
	    }
	    

	    @PostMapping("/create")
	    public ResponseEntity<?> createTask(@RequestBody CreateTaskDTO task) {
	    	log.info("{}",task.toString()); // recebo o workload correto
	    	ObjectMapper mapper = new ObjectMapper();
	    
	    	try {
	    		
	    		JsonNode workloadNode = mapper.readTree(task.getWorkload());
		    	
		    	log.debug("Workload JSON content (parsed): {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(workloadNode));
		    	 
	    		Task savedTask = taskService.createTask(task);
	    		//log.debug("this is my taskType for task {} HELLO ", savedTask.getTaskType());
	            return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
	    	}catch(IllegalArgumentException e) {
	    		return ResponseEntity.badRequest().body(e.getMessage()); // I return a badresponse with the message from the service
	    	} catch (Exception e) {
    	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the task " + task);
    	    }
	    }

	    @DeleteMapping("delete/{id}")
	    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
	    	  try {
	    	        taskService.deleteTask(id);
	    	        log.info("Task with ID {} deleted successfully", id);
	    	        return ResponseEntity.status(HttpStatus.OK).body("task deleted succesfully.");
	    	    } catch (IllegalArgumentException e) {
	    	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    	    } catch (Exception e) {
	    	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the task");
	    	    }
	    }
	    
	    
	    // Get tasks by recurrence // GET http://localhost:8080/tasks/recurrence/DAILY
	    @GetMapping("/recurrence/{recurrence}")
	    public ResponseEntity<?> getTasksByRecurrence(@PathVariable TaskRecurrence recurrence) {
	    	
	    	try {
	    	List<Task> tasksToSend = taskService.getTasksByRecurrence(recurrence);
	    	return ResponseEntity.status(HttpStatus.OK).body(tasksToSend);
	    	}catch(IllegalArgumentException e) {
	    		return ResponseEntity.badRequest().body(e.getMessage()); // I return a badresponse with the message from the service
	    	}
	    	catch(Exception e) {
	    		return ResponseEntity.badRequest().body("An error occured while fetching task with recurrence " + recurrence);
	    	}
	    	
	    }
	    
	    
	    /**
	     * Gets all task treeNodes to represent in a tree table.
	     */
	    @GetMapping("/getTaskTreeNodes")
	    public ResponseEntity<List<TaskTreeResponseDTO>> getAllTaskTreeNodes(){
	    	List<TaskTreeResponseDTO> taskTreeNodes = taskService.GetTaskTreeNodes();
	    	return ResponseEntity.ok(taskTreeNodes);
	    }
	    
	 /* Get tasks by recurrence // GET http://localhost:8080/tasks/type/CheckWeb or somethin
	    @GetMapping("/type/{type}")
	    public ResponseEntity<?> getTasksByRecurrence(@PathVariable TaskType type) {
	        try {
	        	List<Task> tasksToSend = taskService.getTasksByType(type);
		    	return ResponseEntity.status(HttpStatus.OK).body(tasksToSend);
	        }catch(IllegalArgumentException e) {
	    		return ResponseEntity.badRequest().body(e.getMessage());
	        }catch(Exception e) {
	    		return ResponseEntity.badRequest().body("An error occured while fetching task with type " + type);
	    	}
	    }*/
	    
	    @GetMapping("/activity/{activityId}")//GET /tasks/activity/1 It will return all tasks that belong to activity_id = 1.
	    public ResponseEntity<?> getTasksByActivity(@PathVariable Long activityId) {
	    	try {
	    		List<Task> tasksToSend = taskService.getTasksByActivity(activityId);
		    	return ResponseEntity.status(HttpStatus.OK).body(tasksToSend);
	    	}catch(IllegalArgumentException e) {
	    		return ResponseEntity.badRequest().body(e.getMessage());
	        }catch(Exception e) {
	    		return ResponseEntity.badRequest().body("An error occured while fetching task of activity " + activityId);
	    	}
	        
	    }
}
