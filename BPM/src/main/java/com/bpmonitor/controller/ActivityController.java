package com.bpmonitor.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bpmonitor.DTOs.CreateActivityDTO;
import com.bpmonitor.customExceptions.EntityNotFoundException;
import com.bpmonitor.models.Activity;
import com.bpmonitor.services.ActivityService;

import lombok.extern.slf4j.Slf4j;


/**
 * This class is responsible for exposing the endpoints related
 * to the ActivityService functionalities.
 * @author joao7
 */
@Slf4j
@RestController
@RequestMapping("/Activities")
public class ActivityController {

	
	private final ActivityService actService;
	
	public ActivityController(ActivityService actService) {
		this.actService = actService;
	}
	
	
	@PostMapping("/createAct")
	public ResponseEntity<?> createActivity(@RequestBody CreateActivityDTO activityDto){
		try {
			Activity act = actService.createActivity(activityDto);
			return ResponseEntity.status(HttpStatus.OK).body(act);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the activity");
		}
		
	}
	

	
	@PostMapping("/update/{id}")
	public ResponseEntity<?> updateActivity(@PathVariable Long id,@RequestBody Activity activity){
		try {
			Activity act = actService.updateActivity(id, activity);
			return ResponseEntity.status(HttpStatus.OK).body(act);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while updating the activity" + activity);
		}
	}
	
	@DeleteMapping("/delete/id/{id}")
	public ResponseEntity<?> deleteActivity(@PathVariable Long id){
		try {
			boolean act = actService.deleteActivityByID(id);
			return ResponseEntity.status(HttpStatus.OK).body(act);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while deleting the activity at id" + id);
		}
	}
	
	@DeleteMapping("/delete/bySubprocess/id/{id}")
	public ResponseEntity<?> deleteActivitiesTiedToASubprocess(@PathVariable Long id){
		try {
			boolean act = actService.deleteActivityBySubprocess_SubprocessID(id);
			return ResponseEntity.status(HttpStatus.OK).body(act);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occured while deleting the actitivies tied to subprocess with id " + id); 
		}
	}
	
	
	/**
	 * Fetches all activities from the DB.
	 * @return - a response entity containing all
	 *  activities if the operation is succesful.
	 */
	 @GetMapping("/getAll")
	 public ResponseEntity<?> getAllActivities() {
	    	log.info("Received request to /getAllActvities");
	    	try {
	    	   List<Activity> activities =  actService.getAllActivities();
	    	   return ResponseEntity.status(HttpStatus.OK).body(activities);
	       }catch(Exception e) {
	         	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching all tasks");
	       }
	    	
	 }
	 
	 /**
	  * Fetch an activity by it's Id.
	  * @param id - id of the activity I wish to retrieve.
	  * @return - responseEntity containing the requested activity.
	  */
	 @GetMapping("/id/{id}")
	 public ResponseEntity<?> getActivityByID(@PathVariable Long id){
		 log.error("passei aqui");
		 try {
			 Optional<Activity> act = actService.getActivityById(id);
			 return ResponseEntity.status(HttpStatus.OK).body(act);
		 }catch(EntityNotFoundException e) {
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No activity found for id {"+id+"}");
		 }
	 }
	 
	 /**
	  * Fetches all activities that belong to a subprocess.
	  * @param id - id of the subprocess we wish to
	  * retrieve the activities of.
	  * @return - responseEntity with all activities that
	  * belong to the process in it's body.
	  */
	 @GetMapping("/subprocess/id/{id}")
	 public ResponseEntity<?> getActivitiesBySubprocess(@PathVariable Long id){
		 try {
			 List<Activity> listActivities = actService.findActivityBySubprocess_SubprocessID(id);
			 return ResponseEntity.status(HttpStatus.OK).body(listActivities);
		 }catch(EntityNotFoundException e) {
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No activity found for subprocess with id {"+id+"}");
		 }	 
	 }
	 
	 
	 	
	 	
}
