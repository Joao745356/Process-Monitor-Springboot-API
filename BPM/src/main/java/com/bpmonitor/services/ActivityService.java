package com.bpmonitor.services;


import com.bpmonitor.DTOs.CreateActivityDTO;
import com.bpmonitor.customExceptions.EntityNotFoundException;
import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.enums.TaskStatus;
import com.bpmonitor.models.*;
import com.bpmonitor.repositories.ActivityRepository;
import com.bpmonitor.repositories.InterfaceRepository;
import com.bpmonitor.repositories.SubprocessRepository;
import com.bpmonitor.repositories.SystemRepository;
import com.bpmonitor.repositories.TaskRepository;
import com.bpmonitor.services.ActivityWriteService;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Class responsible for CRUD operations of the activity table.
 * also manages it's dependencies to other tables like subprocess, 
 * relationshipActivityInterface and relationshipActivitySystem.
 * 
 * Also includes methods to count, verify existance and others.
 * @author joao7
 */
@Service
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository; // repo for  - activity table 
    private final SubprocessRepository subprocessRepo;
    private final TaskRepository taskRepo;
    private final ActivityWriteService actWriteService;
    
    /** built in repo methods from springboot
	 * 
	 * Method Signature	Description
	 *	T save(T entity)							Save or update an entity
	 *	List<T> findAll()							Find all entities
	 *	Optional<T> findById(ID id)					Find one by ID
	 *	boolean existsById(ID id)					Check existence by ID
	 *	long count()								Count all entities
	 *	void deleteById(ID id)						Delete by ID
	 *	void delete(T entity)						Delete a specific entity
	 *	void deleteAll()							Delete all entities
	 *	void deleteAll(Iterable<? extends T>)		Delete multiple entities
	 *	List<T> findAllById(Iterable<ID>)			Find multiple by IDs
	 *	List<T> saveAll(Iterable<T>)				Save a list of entities
	 * 
	 */
    
    
       public ActivityService(ActivityRepository activityRepository,                            
                           SubprocessRepository subprocessRepo,
                           TaskRepository taskRepo,
                           ActivityWriteService actWriteService) {
        this.activityRepository = activityRepository;   
        this.subprocessRepo = subprocessRepo;
        this.taskRepo = taskRepo;
        this.actWriteService = actWriteService;
    }

       
     
       
       
       /**
        * updates the status of an activity based on the status of it's internal tasks.
        * If an activity has 1 or more of it's tasks fails, it'll be compromised or down.
        * @param id - id of the activity.
        * @return - true if all went well, false if activity not found, or it has no tasks.
        */
	    @Transactional
	    public boolean updateActivityStatusBasedOnTasks(Long id) {	    
	    	
	    	List<Task> existingTasks = taskRepo.findTaskByActivity_ActivityID(id);
	    	 int counterOfFails = 0;
	    	 
	    	   if (existingTasks == null || existingTasks.isEmpty()) {
	    	        log.warn("No tasks found for activity ID: {}", id);
	    	        return false;
	    	    }
	    	 
	    	for( Task t : existingTasks) { // for each task of this activity
	    		if(t.getTaskStatus().equals(TaskStatus.FAIL)) {
	    			counterOfFails++;
	    		}
	    	}
	    	
	    	  if (counterOfFails == 0) {
	    		  actWriteService.updateActivityStatus(id, OperationalStatus.UP);
	    	    } else if (counterOfFails == 1) {
	    	    	actWriteService.updateActivityStatus(id, OperationalStatus.COMPROMISED);
	    	    } else {
	    	    	actWriteService.updateActivityStatus(id, OperationalStatus.DOWN);
	    	    }
	    				   
	    		
	    	  /**
	    	   * I will never get here with a non-existing activity so I avoid the 
	    	   * check for the optional of get.
	    	   */
	    		Activity act = getActivityById(id).get(); 

	    		//I call on my parent subprocess to check it's activities statuses and update itself.
	    	//	act.getSubprocess().updateStatusBasedOnActivies(act.getSubprocess().getSubprocessID());
	    		
	    		
	    		
	    		// I check how many systems I test
	    		//List<EDPRSystem> sysList = act.getRelationshipActivitySystems() // need a way to get systems from here 
	    		
	    		//for(EDPRSystem s : sysList) {
	    			
	    			//each system should now update their status based on the activities that test it 
	    			
	    		//}
	    		return true;
	    				
	    		
	    }
	    
      
      
       
       /**
        * updates  an activity.
        * @param id - id of the activity
        * @param Activity - newActivityData
        * @return
        */
       @Transactional
       public Activity updateActivity(Long id, Activity newActivityData) {
           
       	Optional<Activity> existingOpt = activityRepository.findById(id);
           if (!existingOpt.isPresent()) {
               throw new EntityNotFoundException("Activity not found with ID: " + id);
           }
           Activity existing = existingOpt.get();
           
           existing.setActivityDescription(newActivityData.getActivityDescription());
           existing.setActivityName(newActivityData.getActivityName());;
           existing.setActivityStatus(newActivityData.getActivityStatus());
          // existing.setRelationshipActivityInterfaces(newActivityData.getRelationshipActivityInterfaces());
          // existing.setRelationshipActivitySystems(newActivityData.getRelationshipActivitySystems());
           existing.setSubprocess(newActivityData.getSubprocess());
           
           return activityRepository.save(existing);
       }
       
       
    
    /**
     * fetches all activities from the activities table.
     * @return
     */
       @Transactional(readOnly = true) 
    public List<Activity> getAllActivities(){
    	try {
    	return activityRepository.findAll();
    	}catch(RuntimeException e) {
    		log.error("something went wrong with getAllActivities {}: {}", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * deletes all activities tied to a subprocess.
     * @param subprocess
     * @return true if method executed correctly, else false.
     */
    @Transactional
    public boolean deleteActivityBySubprocess(Subprocess subprocess) {
    	
    	 if (subprocess == null) {
    	        log.warn("Attempted to delete activities with a null subprocess.");
    	        return false;
    	    }
    	 
    	try {
    	this.activityRepository.deleteActivityBySubprocess(subprocess);
    	return true;
    	}catch(RuntimeException e) {
    		log.error("something went wrong with deleteBySubprocess {}: {}", subprocess, e.getMessage(), e);
    		throw e;
    	}
    }
    
    /**
     * Counts all activities with given status.
     * @param status - OperationalStatus to count
     * @return - int value with the amount of activities with given status.
     */
    @Transactional(readOnly = true) 
    public int countActivityByActivityStatus(OperationalStatus status) {
    	try{
    	return this.activityRepository.countActivityByActivityStatus(status);
    	}catch(RuntimeException e) {
    		log.error("something went wrong with countActivityByActivityStatus {}: {}", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * Fetches all activities between X and Y activities.
     * @param idStart - starting index.
     * @param idEnd - end of index.
     * @return list of activities between start and end.
     */
    @Transactional(readOnly = true)
    public List<Activity> findActivityByActivityIDBetween(Long idStart, Long idEnd){
    	if(idStart != null && idEnd != null &&
    	idStart >= 0 && idEnd >= 0 && 
    	 idStart <= idEnd ) {
    	return this.activityRepository.findActivityByActivityIDBetween(idStart, idEnd);
    	}else {
    		log.error("Tried to find activities with invalid IDs: {} -> {} ", idStart, idEnd);
    		return Collections.emptyList(); // Safer than returning null;
    	}
    }
    
    /**
     * Fetches all activities with given name.
     * Most of the time probably one activity.
     * @param name - name we wish to look for in the BD.
     * @return a list of all activities with the given name.
     */
    @Transactional(readOnly = true)
    public List<Activity> findActivityByActivityName(String name){
    	try {
    	return this.activityRepository.findActivityByActivityName(name);
    	}catch(RuntimeException e){
    		log.error("something went wrong with findActivityByActivityName method {} ", e.getMessage() );
    		throw e;
    	}
    		
    }
    	
    /**
     * Fetches all activities tied to a subprocess.
     * @param subprocessID - ID of the subprocess we want the activities of.
     * @return a list of activities tied to the subprocess.
     */
    @Transactional(readOnly = true)
    public List<Activity> findActivityBySubprocess_SubprocessID(Long subprocessID){
    	
    	try {
    		return this.activityRepository.findActivityBySubprocess_SubprocessID(subprocessID);
        	}catch(RuntimeException e){
        		log.error("something went wrong with findActivityBySubprocess_SubprocessID method {} ", e.getMessage() );
        		throw e;
        	}
    }
    
    /**
     * Fetches all activities with the given status.
     * @param activityStatus - status we wish to check for.
     * @return List of activities witht the given status.
     */
    @Transactional(readOnly = true)
    public List<Activity> findActivityByActivityStatus(OperationalStatus activityStatus){
    	try {
    		return this.activityRepository.findActivityByActivityStatus(activityStatus);
        	}catch(RuntimeException e){
        		log.error("something went wrong with findActivityByActivityStatus method {} ", e.getMessage() );
        		throw e;
        	}
    }
    
    
    /**
     * Deletes all actitivies tied to a subprocess.
     * @param subprocessID - Id of the subprocess whose activities we want to delete.
     * @return true if deletion went right, false otherwise.
     */
    @Transactional
    public boolean deleteActivityBySubprocess_SubprocessID(Long subprocessID) {
    	 Optional<Subprocess> fetchSubprocess = subprocessRepo.findById(subprocessID);
    	    if (fetchSubprocess.isPresent()) {
    	        activityRepository.deleteActivityBySubprocess(fetchSubprocess.get());
    	        return true;
    	    } else {
    	        log.warn("Subprocess not found: {}", subprocessID);
    	        return false;
    	    }
    }
    
    
    /**
     * fetches an Activity from DB by it's ID.
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<Activity> getActivityById(Long id) {
    	if(id <= 0) {return Optional.empty();} // return empty if the id is below 0.
    	try {
        return activityRepository.findById(id);
    	}catch(EntityNotFoundException e) {
    		log.error("something went wrong with get activityByID method {}", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * saves an Activity to the DB
     * @param Activity 
     * @return an activity object
     */
    @Transactional
    public Activity saveActivity(Activity act) {
        return activityRepository.save(act);
    }
    
    /**
     * deletes an activity from DB
     * @param id
     */
    @Transactional
    public boolean deleteActivityByID(Long id) {
    	if(id <= 0) { throw new IllegalArgumentException("Invalid ID: ID must be positive.");}
    	
    	activityRepository.deleteById(id); // it's ignored if it's not found so i just return true anyway.
    	return true;
    }
    
    
    /**
     * adding a relationship entry to the relationshipActivitySystem table.
     * both activity and system will be added to the relationship table in order to 
     * bridge a many to many relationship.
     * @param activityId - ID of the activity
     * @param systemId - ID of the system
     
    @Transactional
    public void addSystemToActivity(Long activityId, Long systemId) {
    	
    	boolean exists = relSysRepo.existsByActivity_ActivityIDAndSystem_EdprSystemID(activityId, systemId);
    	if (!exists) {
    		Activity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new RuntimeException("Activity not found"));
            EDPRSystem edprSystem = systemRepository.findById(systemId)
                    .orElseThrow(() -> new RuntimeException("System not found"));

            RelationshipActivitySystem rel = new RelationshipActivitySystem(activity, edprSystem);
            relSysRepo.save(rel);
    	}

        
    }  */
    
    /**
     * adding a relationship entry to the relationshipActivityInterface table.
     * both activity and interface will be added to the relationship table in order to 
     * bridge a many to many relationship.
     * @param activityId - ID of the activity
     * @param interfaceId - ID of the interface
   
    @Transactional
    public void addInterfaceToActivity(Long activityId, Long interfaceId) {
    	
    	boolean exists = relIntRepo.existsByActivity_ActivityIDAndEdprInterface_EdprInterfaceID(activityId, interfaceId);
    	if(!exists) {		
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        EDPRInterface edprInterface = interfaceRepository.findById(interfaceId)
                .orElseThrow(() -> new RuntimeException("Interface not found"));

        RelationshipActivityInterface rel = new RelationshipActivityInterface(activity, edprInterface);
        relIntRepo.save(rel);
    }
    }  */



    /**
     * Creates an activity.
     * @param dto - check CreateActivityDTO
     * @return the created activity.
     */
    @Transactional
	public Activity createActivity(CreateActivityDTO dto) {
			
    		Optional<Subprocess> sub = subprocessRepo.findById(dto.getSubprocessId()); // eu tiro o subprocess do DTO
    		Activity activity = new Activity();
    		
    		if(sub.isPresent()) { 
    			Subprocess subP = sub.get();
    			activity.setSubprocess(subP);}
    	
    	
		    activity.setActivityName(dto.getName());
		    activity.setActivityDescription(dto.getDescription());
		    activity.setActivityStatus(OperationalStatus.UNRUN);
		    Activity saved = activityRepository.save(activity); // save first to get ID
		
		    return saved;
	}



	
}

