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

import com.bpmonitor.DTOs.CreateSubprocessDTO;
import com.bpmonitor.customExceptions.EntityNotFoundException;
import com.bpmonitor.models.Subprocess;
import com.bpmonitor.services.SubprocessService;

@RestController
@RequestMapping("/Subprocesses")
public class SubprocessController {

	
	private final SubprocessService subprocessService; 
	
	public SubprocessController(SubprocessService subServ) {
		this.subprocessService = subServ;
	}
	

	/**
	 * Creates a subprocess.
	 * @param createSubprocessDTO - check DTO class, only has name, process and status.
	 * @return returns a response entity with the subprocess in it's body.
	 */
	@PostMapping("/createSubprocess")
	public ResponseEntity<?> CreateSubprocess(@RequestBody CreateSubprocessDTO createSubprocessDTO){
		try {
			Subprocess sub = subprocessService.saveSubprocess(createSubprocessDTO);
			return ResponseEntity.status(HttpStatus.OK).body(sub);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the subprocess.");
		}
		
	}
	

	
	/**
	 * updates a subprocess by it's id. 
	 * @param id - id of the subprocess to update.
	 * @param createSubprocessDTO - check DTO class, only has name, process and status.
	 * @return returns a response entity with true if all went well, false otherwise.
	 */
	@PostMapping("/update/id/{id}")
	public ResponseEntity<?> updateSubprocess(@PathVariable Long id,@RequestBody CreateSubprocessDTO createSubprocessDTO){
		try {
			boolean updated = subprocessService.updateSubprocess(id, createSubprocessDTO);
			return ResponseEntity.status(HttpStatus.OK).body(updated);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while updating the subprocess" + createSubprocessDTO);
		}
	}
	
	/**
	 * deletes a subprocess by it's id.
	 * @param id - id of the subprocess to delete.
	 * @return returns a response entity with true if all went well, false otherwise.
	 */
	@DeleteMapping("/delete/id/{id}")
	public ResponseEntity<?> deleteSubprocess(@PathVariable Long id){
		try {
			boolean deleted = subprocessService.deleteSubprocessById(id);
			return ResponseEntity.status(HttpStatus.OK).body(deleted);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while deleting the subprocess at id" + id);
		}
	}
	
	
	
	
	/**
	 * Fetches all Subprocesses from the DB.
	 * @return - a response entity containing all
	 *  activities if the operation is succesful.
	 */
	 @GetMapping("/getAll")
	 public ResponseEntity<?> getAllSubprocesses() {
	    	try {
	    	   List<Subprocess> subprocesses =  subprocessService.findAllSubprocesses();
	    	   return ResponseEntity.status(HttpStatus.OK).body(subprocesses);
	       }catch(Exception e) {
	         	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching all subprocesses");
	       }
	    	
	 }
	 
	 /**
	  * Fetch an Subprocess by it's Id.
	  * @param id - id of the activity I wish to retrieve.
	  * @return - responseEntity containing the requested activity.
	  */
	 @GetMapping("/id/{id}")
	 public ResponseEntity<?> getSubprocessByID(@PathVariable Long id){
		 try {
			 Optional<Subprocess> subprocess =  subprocessService.getSubprocessByID(id);
			 return ResponseEntity.status(HttpStatus.OK).body(subprocess);
		 }catch(EntityNotFoundException e) {
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No subprocess found for id {"+id+"}");
		 }
	 }
	 
	 /**
	  * Fetches all Subprocesses that belong to a process.
	  * @param id - id of the subprocess we wish to
	  * retrieve the activities of.
	  * @return - responseEntity with all activities that
	  * belong to the process in it's body.
	  */
	 @GetMapping("/process/id/{id}")
	 public ResponseEntity<?> getSubprocessesByProcess(@PathVariable Long id){
		 try {
			 List<Subprocess> subprocess =  subprocessService.findSubprocessByProcess_ProcessID(id);
			 return ResponseEntity.status(HttpStatus.OK).body(subprocess);
		 }catch(EntityNotFoundException e) {
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No subprocesses found for process with id {"+id+"}");
		 }	 
	 }
	 
	 
	
	
}
