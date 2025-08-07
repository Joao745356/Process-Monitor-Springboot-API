package com.bpmonitor.services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bpmonitor.DTOs.CreateSubprocessDTO;
import com.bpmonitor.customExceptions.EntityNotFoundException;
import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.models.Activity;
import com.bpmonitor.models.Process;
import com.bpmonitor.models.Subprocess;
import com.bpmonitor.repositories.ActivityRepository;
import com.bpmonitor.repositories.ProcessRepository;
import com.bpmonitor.repositories.SubprocessRepository;

import lombok.extern.slf4j.Slf4j;


/**
 * class responsible for CRUD operations of the subprocess table.
 * also includes count and exist methods, and methods to update a 
 * subprocess's status based on it's activities. 
 * @author joao7
 */
@Service
@Slf4j
public class SubprocessService {

   

private final SubprocessRepository subprocessRepo; // repo for  - interface table
private final ProcessRepository processRepo;
private final ActivityRepository actRepo;
    
    public SubprocessService(
    		SubprocessRepository SubprocessRepository,
    		 ProcessRepository processRepo,
    		 ActivityRepository actRepo) {
    
    	this.subprocessRepo = SubprocessRepository;
    	this.processRepo = processRepo;
    	this.actRepo = actRepo;
    	
    }
    
    /**
     * if one or more of a subprocess's activies are compromised or down, 
     * the parent subprocess will also be compromised or down.
     * if one of my activities is compromised, I am still up.
     * if one or more of my activities are compromised I am compromised.
     * if 1 or multiple of my activies are down, I am down.
     * @param id
     * @return
     */
    public boolean updateSubprocessStatusBasedOnActivityStatus(Long id) {
    
    	// I fetch all activities that belong to this subprocess 
    	List<Activity> existingActivities= actRepo.findActivityBySubprocess_SubprocessID(id);
   	 	int counterOfFails = 0; //counts failed activities
   	 	int counterOfCompromised = 0; //counts compromised act
   	 
   	   if (existingActivities == null || existingActivities.isEmpty()) {
   	        log.warn("No tasks found for activity ID: {}", id); // check if there are activities
   	        return false;
   	    }
   	 
   	for( Activity a : existingActivities) { // for each task of this activity
   		if(a.getActivityStatus().equals(OperationalStatus.DOWN)) {
   			counterOfFails++; // count fails
   		}
   		if(a.getActivityStatus().equals(OperationalStatus.COMPROMISED)) {
   			counterOfCompromised++; // count compromised
   		}
   	}
   	
   	  if (counterOfFails == 0 && counterOfCompromised == 0 ) { // if there's no activities failing or compromised
   		updateSubprocessStatus(id, OperationalStatus.UP); // I'm up.
   	    } else if ( counterOfFails > 0) { // if there's  1 or more down, I'm down.
   	    	updateSubprocessStatus(id, OperationalStatus.DOWN);
   	    } else {
   	    	updateSubprocessStatus(id, OperationalStatus.COMPROMISED); // if there's compromised but no fails, I'm compromised. 
   	    }
   		return true;	
    }
    
    /**
     * updates only an activitie's status.
     * @param id- id of the activity
     * @param newStatus - can be UP, DOWN, COMPROMISED.
     * @return
     */
    @Transactional
    public Subprocess updateSubprocessStatus(Long id, OperationalStatus newStatus) {
 		
 	   Optional<Subprocess> existingOpt = subprocessRepo.findById(id);
         if (!existingOpt.isPresent()) {
             throw new EntityNotFoundException("Activity not found with ID: " + id);
         }
         Subprocess existing = existingOpt.get();
 	   
         existing.setSubprocessStatus(newStatus);
 	   
         return subprocessRepo.save(existing);
    }
    
    /**
     * creates a subprocess and saves it to DB.
     * @param subprocessToSave - subprocess to save.
     */
    @Transactional
    public void createSubprocess(CreateSubprocessDTO subprocessToSave) {
    	saveSubprocess(subprocessToSave);
    }
    
    
    /**
     * Updates an entire subprocess based on it's id.
     * @param subprocessID - new data to update into the subprocess.
     * @return true if process has been updated, false otherwise.
     */
    @Transactional
    public boolean updateSubprocess(Long subprocessID, CreateSubprocessDTO newSubprocessData) {
    	
    	Optional<Subprocess> optionalResult = subprocessRepo.findById(subprocessID);
    	
    	
    		if (!optionalResult.isPresent()) return false;
    		
    		Process process = processRepo.findById(newSubprocessData.getProcessId())
    	            .orElseThrow(() -> new EntityNotFoundException("Process not found with id " + newSubprocessData.getProcessId()));
    	
    	Subprocess subprocess = optionalResult.get();
    
    	subprocess.setProcess(process);
    	subprocess.setSubprocessName(newSubprocessData.getSubprocessName());
    	subprocess.setSubprocessStatus(newSubprocessData.getSubprocessStatus());
    	
    	
    	subprocessRepo.save(subprocess);
    	
    	return true;
    	
    }
    
    
    /**
     * Saves a given Subprocess into the Database.
     * @param Subprocess - object to save.
     * @return Subprocess that's been saved.
     */
    public Subprocess saveSubprocess(CreateSubprocessDTO dto) {
    	
    	try {
    		
    		Process process = processRepo.findById(dto.getProcessId())
    	            .orElseThrow(() -> new EntityNotFoundException("Process not found with id " + dto.getProcessId()));
    	            		
    	            		
    		Subprocess sub = new Subprocess();
        	sub.setProcess(process);
        	sub.setSubprocessName(dto.getSubprocessName());
        	sub.setSubprocessStatus(dto.getSubprocessStatus());
    	 return this.subprocessRepo.save(sub);
    	}catch(RuntimeException e){
    		log.error("something went wrong with saveSubprocess method {}", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * Fetches all Subprocesses from the DB. 
     * @return List of Subprocesses with all records in the DB.
     */
    @Transactional(readOnly = true)
    public List<Subprocess> findAllSubprocesses(){
    	try {
    		return this.subprocessRepo.findAll();
    	}catch(RuntimeException e) {
    		log.error("something went wrong at findAllSubprocesses method : {}", e.getMessage());
    		throw e;
    	}
    }
    	
    /**
     * Fetches an Subprocess by it's id.
     * @param id - id of the Process we wish to fetch from the DB.
     * @return Subprocess fetched from the DB.
     */
    @Transactional(readOnly = true)
    public Optional<Subprocess> getSubprocessByID(Long id){
    		if( id <= 0) {return Optional.empty();}
    		try {
    			return this.subprocessRepo.findById(id);
    		}catch(EntityNotFoundException e) {
    			log.error("couldn't find Subprocess for ID : {}" , id);
    			throw e;
    		}
    	}
    
    
    
    /**
     * Fetches all subprocess tied to a process.
     * @param subprocessID - ID of the subprocess we want the activities of.
     * @return a list of activities tied to the subprocess.
     */
    @Transactional(readOnly = true)
    public List<Subprocess> findSubprocessByProcess_ProcessID(Long subprocessID){
    	
    	try {
    		return this.subprocessRepo.findSubprocessByProcess_ProcessID(subprocessID);
        	}catch(RuntimeException e){
        		log.error("something went wrong with findActivityBySubprocess_SubprocessID method {} ", e.getMessage() );
        		throw e;
        	}
    }
    	
    /**
     * Counts all Subprocesses in the DB.
     * @return long value representing the amount of Subprocesses registries in the DB.
     */
    @Transactional(readOnly = true)
    public long countAllSubprocesses() {
    	
    	try {
    		return this.subprocessRepo.count();
    	}catch(RuntimeException e) {
    		log.error("something went wrong with countAllSubprocesses method : {}", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * checks if a process exists in the DB by given id.
     * @param id - id you wish to check.
     * @return true if it exists, false otherwise.
     */
    @Transactional(readOnly = true)
    public boolean existsSubprocessByID(Long id) {
    	if(id <= 0 ) { return false;}
    	try {
    		return this.subprocessRepo.existsById(id);
    	}catch(RuntimeException e) {
    		log.error("something went wrong with the existsSubprocessByID method : {} ", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * deletes a Subprocess by it's ID.
     * @param id - id of the Subprocess we wish to delete.
     * @return true if all went well, false otherwise.
     */
    @Transactional
    public boolean deleteSubprocessById(Long id) {
    	if(id <= 0) {  throw new IllegalArgumentException("Invalid ID: ID must be positive.");}
    	try{
    	 this.subprocessRepo.deleteById(id);
    	 return true;
    	}catch(RuntimeException e) {
    		log.error("something went wrong with"
    				+ " deleteSubprocessById method {}", e.getMessage());
    		throw e;
    	}
    }
}
