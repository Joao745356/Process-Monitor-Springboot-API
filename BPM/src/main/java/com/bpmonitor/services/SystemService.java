package com.bpmonitor.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bpmonitor.DTOs.CreateSystemDTO;
import com.bpmonitor.DTOs.response.ProcessTreeNodeResponseDTO;
import com.bpmonitor.DTOs.response.SystemResponseDTO;
import com.bpmonitor.DTOs.response.SystemTreeNodeDTO;
import com.bpmonitor.DTOs.response.TaskResponseDTO;
import com.bpmonitor.customExceptions.EntityNotFoundException;
import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.enums.TaskStatus;
import com.bpmonitor.models.Process;
import com.bpmonitor.models.System;
import com.bpmonitor.models.Task;
import com.bpmonitor.repositories.InterfaceRepository;
import com.bpmonitor.repositories.SystemRepository;
import com.bpmonitor.repositories.TaskRepository;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class SystemService {

	 private final SystemRepository sysRepository; // repo for  - system 
	 private final InterfaceRepository interfaceRepository; //repo for - interface
	 private final TaskRepository taskRepo;
	    
	    public SystemService(
	    		SystemRepository sysRepo,
	    		InterfaceRepository interfaceRepository,
	    		TaskRepository taskRepo) {
	    
	    	this.sysRepository = sysRepo;
	    	this.interfaceRepository = interfaceRepository;
	    	this.taskRepo = taskRepo;
	    	
	    }
	    
	    
	    
	    
	    /**
	     * creates a edprSystem and saves to BD.
	     * @param systemEDPR - EDPRSystem object
	     */
	    @Transactional
	    public void createEDPRSystem(CreateSystemDTO systemEDPR) {
	    	saveEDPRSystem(systemEDPR);
	    }
	    
	    /**
	     * Saves a given system into the Database.
	     * @param systemEDPR - object to save.
	     * @return systemEDPR that's been saved.
	     */
	    public System saveEDPRSystem(CreateSystemDTO systemEDPR) {
	    	
	    	System system = new System();
	    	system.setSystemName(systemEDPR.getName());
	    	system.setSystemComponent(systemEDPR.getSystemComponent());
	    	system.setSystemDescription(systemEDPR.getDescription());
	    	system.setCurrentStatus(systemEDPR.getStatus());
	    	system.setLocal(systemEDPR.getLocal());

	    	try {
	    	 return this.sysRepository.save(system);
	    	}catch(RuntimeException e){
	    		log.error("something went wrong with saveEDPRSystem method {}", e.getMessage());
	    		throw e;
	    	}
	    }
	    
	    /**
	     * updates an EDPRSystem. 
	     * @param newInterfaceData - EDPRInterface object.
	     */
	    @Transactional
	    public System updateEDPRSystem(Long id, CreateSystemDTO newSystemData) {
	        
	    	Optional<System> existingOpt = sysRepository.findById(id);
	        if (!existingOpt.isPresent()) {
	            throw new EntityNotFoundException("System not found with ID: " + id);
	        }
	        System existing = existingOpt.get();
	        
      
	        existing.setSystemName(newSystemData.getName());
	        existing.setCurrentStatus(newSystemData.getStatus());
	        existing.setLocal(newSystemData.getLocal());
	        existing.setSystemComponent(newSystemData.getSystemComponent());
	        existing.setSystemDescription(newSystemData.getDescription());
	       
	        
	    
	        
	        return sysRepository.save(existing);
	    }
	    
	    
	    /**
	     * Fetches all EDPRSystem from the DB.
	     * @return List of EDPRSystem with all records in the DB.
	     */
	    @Transactional(readOnly = true)
	    public List<System> findAllEDPRSystems(){
	    	try {
	    		return this.sysRepository.findAll();
	    	}catch(RuntimeException e) {
	    		log.error("something went wrong at findAllEDPRSystems method : {}", e.getMessage());
	    		throw e;
	    	}
	    }
	    	
	    /**
	     * Fetches an EDPRSystem by it's id.
	     * @param id - id of the interface we wish to fetch from the DB.
	     * @return EDPRSystem fetched from the DB.
	     */
	    @Transactional(readOnly = true)
	    public Optional<SystemResponseDTO> getEDPRSystemByID(Long id){
	    		if( id <= 0) {return Optional.empty();}
	    		 try {
	    		        return this.sysRepository.findById(id)
	    		            .map(system -> {
	    		                List<TaskResponseDTO> taskDTOs = system.getTasks() != null
	    		                    ? system.getTasks().stream()
	    		                        .map(task -> new TaskResponseDTO(	    		                            
	    		                            task.getTaskID(),
	    		                            task.getTaskName(),
	    		                            task.getTaskDescription(),
	    		                            task.getWorkload(),
	    		                            task.getActivity().getActivityID(),
	    		                            task.getRecurrence(),
	    		                            task.getTaskStatus(),
	    		                            task.getSystem() != null ? task.getSystem().getSystemID() : null,
	    		                            task.getInterface() != null ? task.getInterface().getInterfaceID() : null
	    		                        ))
	    		                        .collect(Collectors.toList())
	    		                    : Collections.emptyList();

	    		                return new SystemResponseDTO(
	    		                    system.getSystemID(),
	    		                    system.getLocal(),
	    		                    system.getSystemName(),
	    		                    system.getSystemComponent(),
	    		                    system.getSystemDescription(),
	    		                    system.getCurrentStatus(),
	    		                    taskDTOs
	    		                );
	    		            });

	    		    } catch (EntityNotFoundException e) {
	    		        log.error("Couldn't find EDPRSystem for ID: {}", id);
	    		        throw e;
	    		    }
	    	}
	    	
	    /**
	     * Counts all Systems in the DB.
	     * @return long value representing the amount of System registries in the DB.
	     */
	    @Transactional(readOnly = true)
	    public long countAllSystems() {
	    	
	    	try {
	    		return this.sysRepository.count();
	    	}catch(RuntimeException e) {
	    		log.error("something went wrong with countAllSystems method : {}", e.getMessage());
	    		throw e;
	    	}
	    }
	    
	    /**
	     * checks if an System exists in the DB by given id.
	     * @param id - id you wish to check.
	     * @return true if it exists, false otherwise.
	     */
	    @Transactional(readOnly = true)
	    public boolean existsEDPRSystemByID(Long id) {
	    	if(id <= 0 ) { return false;}
	    	try {
	    		return this.sysRepository.existsById(id);
	    	}catch(RuntimeException e) {
	    		log.error("something went wrong with the existsSystemByID method : {} ", e.getMessage());
	    		throw e;
	    	}
	    }
	    
	    /**
	     * deletes an System by it's ID.
	     * @param id - id of the System we wish to delete.
	     * @return true if all went well, false otherwise.
	     */
	    @Transactional
	    public boolean deleteEDPRSystemById(Long id) {
	    	if(id <= 0) {  throw new IllegalArgumentException("Invalid ID: ID must be positive.");}
	    	try{
	    	 	try {
		    		 Optional<System> system = sysRepository.findById(id);
		    		 
		    		    if (system.isPresent()) {
		    		        // Deleting associated interfaces
		    		    	interfaceRepository.deleteByOriginOrDestination(system.get(), system.get());  // Delete interfaces associated with the system
		    		    	sysRepository.delete(system.get());  // Then delete the system
		    		    	return true;
		    		    } else {
		    		        throw new EntityNotFoundException("System not found");
		    		    }
		    	}catch(RuntimeException e) {
		    		log.error("something went wrong with the existsSystemByID method : {} ", e.getMessage());
		    		throw e;
	    }
	    	}catch(RuntimeException e) {
	    		log.error("something went wrong with the existsSystemByID method : {} ", e.getMessage());
	    		throw e;
	    	}
	    }


	    /**
	     * Fetches all System from the DB, in the form of SystemTreeNodeDTOs.  
	     * @return List of ProcessTreeNodeResponseDTO for each process in the DB.
	     */
	    @Transactional(readOnly = true)
	    public List<SystemTreeNodeDTO> GetSystemTreeNodes(){
	    	
	    
	    	try {
	    		 List<System> systems = this.sysRepository.findAll();
	    	        
	    	     
	    		 List<SystemTreeNodeDTO> EDPRSystemTreeNodes = systems.stream()
	    		            .map(system -> new SystemTreeNodeDTO(
	    		               system.getSystemID(),
	    		               system.getLocal(),
	    		               system.getSystemName(),
	    		               system.getSystemComponent(),
	    		               system.getCurrentStatus()
	    		            ))
	    		            .collect(Collectors.toList());
	    	        
	    	        return EDPRSystemTreeNodes;
	    	}catch(RuntimeException e) {
	    		log.error("something went wrong at GetSystemTreeNodes method : {}", e.getMessage());
	    		throw e;
	    	}
	   
	    }


		
	    /**
	     * updates a system's status based on all tasks that test it. 
	     * @param id - id of system whose status we plan on updating
	     */
	    @Transactional
	    public void updateSystemStatusBasedOnTasks(Long id) {	    
	    	
	    	List<Task> existingTasks = taskRepo.findBySystem_EdprSystemID(id);

	        if (existingTasks == null || existingTasks.isEmpty()) {
	            log.warn("No tasks found for system ID: {}", id);
	            return;
	        }

	        long failCount = existingTasks.stream()
	                .filter(task -> TaskStatus.FAIL.equals(task.getTaskStatus()))
	                .count();

	        OperationalStatus newStatus;
	        if (failCount == 0) {
	            newStatus = OperationalStatus.UP;
	        } else if (failCount == 1) {
	            newStatus = OperationalStatus.COMPROMISED;
	        } else {
	            newStatus = OperationalStatus.DOWN;
	        }

	        updateSystemStatus(id, newStatus);		    
	    		
	    }



	    @Transactional
	    private void updateSystemStatus(Long id, OperationalStatus currentStatus) {
	    	 Optional<System> optSystem = sysRepository.findById(id);

	    	    if (optSystem.isPresent()) {
	    	        System sys = optSystem.get();
	    	        sys.setCurrentStatus(currentStatus);
	    	        sysRepository.save(sys);
	    	    } else {
	    	        log.warn("Didn't find the system by ID {}", id);
	    	    }
	    }
	
		
	
		
		
}
