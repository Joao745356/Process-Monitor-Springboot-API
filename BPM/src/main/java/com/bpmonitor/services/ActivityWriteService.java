package com.bpmonitor.services;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.bpmonitor.customExceptions.EntityNotFoundException;
import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.models.Activity;
import com.bpmonitor.repositories.ActivityRepository;
import com.bpmonitor.repositories.SubprocessRepository;
import com.bpmonitor.repositories.TaskRepository;

@Service
public class ActivityWriteService {

	
	private final ActivityRepository activityRepository; // repo for  - activity table 
    private final SubprocessRepository subprocessRepo;
    private final TaskRepository taskRepo;
    
    public ActivityWriteService(
    		ActivityRepository activityRepository,
    		SubprocessRepository subprocessRepo,
    		TaskRepository taskRepo) {
    	this.activityRepository = activityRepository;
    	this.subprocessRepo = subprocessRepo;
    	this.taskRepo = taskRepo;
    }
    
    /**
     * updates only an activitie's status.
     * @param id- id of the activity
     * @param newStatus - can be UP, DOWN, COMPROMISED.
     * @return
     */
    @Transactional
    public Activity updateActivityStatus(Long id, OperationalStatus newStatus) {
    	  Optional<Activity> existingOpt = activityRepository.findById(id);
          if (!existingOpt.isPresent()) {
              throw new EntityNotFoundException("Activity not found with ID: " + id);
          }
          Activity existing = existingOpt.get();
  	   
          existing.setActivityStatus(newStatus);
  	   
          return activityRepository.save(existing);
    }
}
