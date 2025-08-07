package com.bpmonitor.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bpmonitor.DTOs.CreateInterfaceDTO;
import com.bpmonitor.DTOs.response.InterfaceResponseDTO;
import com.bpmonitor.DTOs.response.InterfaceTreeNodeDTO;
import com.bpmonitor.DTOs.response.SystemResponseDTO;
import com.bpmonitor.DTOs.response.SystemTreeNodeDTO;
import com.bpmonitor.DTOs.response.TaskResponseDTO;
import com.bpmonitor.customExceptions.EntityNotFoundException;
import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.enums.TaskStatus;
import com.bpmonitor.models.Interface;
import com.bpmonitor.models.System;
import com.bpmonitor.models.Task;
import com.bpmonitor.repositories.ActivityRepository;
import com.bpmonitor.repositories.InterfaceRepository;
import com.bpmonitor.repositories.SystemRepository;
import com.bpmonitor.repositories.TaskRepository;

import lombok.extern.slf4j.Slf4j;



/**
 * Class responsible for managing the EDPRInterface table's CRUD operations.
 * Also includes methods to count, verify existance, find by origin or destination.
 * @author joao7
 */
@Service
@Slf4j
public class InterfaceService {
	
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
	
	
    private final InterfaceRepository interfaceRepository; // repo for  - interface table
    private final SystemRepository sysRepo;
    private final ActivityRepository actRepo;
    private final TaskRepository taskRepo;
    
    public InterfaceService(
    		InterfaceRepository intRepo,
    		SystemRepository sysRepo,
    		ActivityRepository actRepo,
    		TaskRepository taskRepo) {
    
    	this.interfaceRepository = intRepo;
    	this.sysRepo = sysRepo;
    	this.actRepo = actRepo;
    	this.taskRepo = taskRepo;
    	
    }
    
    /**
     * creates an EDPRInterface. 
     * @param interfaceEDPR - EDPRInterface object.
     */
    @Transactional
    public void createEDPRInterface (CreateInterfaceDTO interfaceEDPR) {
    	saveEDPRInterface(interfaceEDPR);
    	}
    
    
    /**
     * updates an EDPRInterface. 
     * @param newInterfaceData - EDPRInterface object.
     */
    @Transactional
    public Interface updateEDPRInterface(Long id, CreateInterfaceDTO newInterfaceData) {
        
    	Optional<Interface> existingOpt = interfaceRepository.findById(id);
        if (!existingOpt.isPresent()) {
            throw new EntityNotFoundException("Interface not found with ID: " + id);
        }
        Interface existing = existingOpt.get();
        Optional<System> originOpt = sysRepo.findById(newInterfaceData.getOriginId());
        Optional<System> destinationOpt = sysRepo.findById(newInterfaceData.getDestinationId());
        
        	System origin = originOpt.get();
        
        
        	System destination = destinationOpt.get();
       
        
        
        existing.setEdprInterfaceName(newInterfaceData.getInterfaceName());
        existing.setCurrentStatus(newInterfaceData.getCurrentStatus());
        existing.setDestination(origin);
        existing.setOrigin(destination);
        existing.setEdprInterfaceGoal(newInterfaceData.getInterfaceGoal());
        existing.setEdprInterfaceName(newInterfaceData.getInterfaceName());
        
    
        
        return interfaceRepository.save(existing);
    }
    
    /**
     * finds all interfaces with origin in this system.
     * @param system
     * @return
     */
    public List<Interface> findByOriginSystem(System system) {
        return interfaceRepository.findByOrigin(system);
    }
    
    /**
     * finds all interfaces with destination in this system.
     * @param system
     * @return
     */
    public List<Interface> findByDestinationSystem(System system) {
        return interfaceRepository.findByDestination(system);
    }
    
    /**
     * deletes all interfaces associated with a certain system. 
     * @param edprSystem - system whose interfaces I wanna delete.
     */
    @Transactional
    public void deleteBySystem(System system) {
    	// Delete interfaces where the provided system is either the origin or destination
        List<Interface> interfacesToDelete = interfaceRepository.findByOriginOrDestination(system, system);
        
        // Check if there are any interfaces to delete
        if (!interfacesToDelete.isEmpty()) {
            interfaceRepository.deleteAll(interfacesToDelete); // Deletes the interfaces from the database
            log.info("Deleted {} interfaces related to system with ID: {}", interfacesToDelete.size(), system.getSystemID());
        } else {
            log.info("No interfaces found related to system with ID: {}", system.getSystemID());
        }
    }
    
    
    /**
     * Saves a given interface into the Database.
     * @param interfaceEDPR - object to save.
     * @return EDPPInterface that's been saved.
     */
    @Transactional
    public Interface saveEDPRInterface(CreateInterfaceDTO interface2) {
    	try {
    		
    		 // Map the DTO to the actual entity (EDPRInterface)
            Interface edprInterface = new Interface();
            
            // Manually set fields from the DTO to the entity
            edprInterface.setEdprInterfaceName(interface2.getInterfaceName());
            edprInterface.setEdprInterfaceGoal(interface2.getInterfaceGoal());
            edprInterface.setEdprInterfaceTechnicalData(interface2.getInterfaceTechnicalData());
            edprInterface.setCurrentStatus(interface2.getCurrentStatus());
            // Assuming that the origin and destination are other entities (EDPRSystem), 
            // you would need to fetch them from the repository using the IDs in the DTO
            System origin = sysRepo.findById(interface2.getOriginId())
                    .orElseThrow(() -> new EntityNotFoundException("Origin system not found"));
            System destination = sysRepo.findById(interface2.getDestinationId())
                    .orElseThrow(() -> new EntityNotFoundException("Destination system not found"));

            edprInterface.setOrigin(origin);
            edprInterface.setDestination(destination);
            

            return this.interfaceRepository.save(edprInterface);
    	
    	}catch(RuntimeException e){
    		log.error("something went wrong with saveInterface method {}", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * Fetches all EDPRInterfaces from the DB.
     * @return List of EDPRInterfaces with all records in the DB.
     */
     @Transactional(readOnly = true)
    public List<Interface> findAllEDPRInterfaces(){
    	try {
    		return this.interfaceRepository.findAll();
    	}catch(RuntimeException e) {
    		log.error("something went wrong at findAllEDPRInterfaces method : {}", e.getMessage());
    		throw e;
    	}
    }
    	
   
    	
    /**
     * Counts all interfaces in the DB.
     * @return long value representing the amount of EDPRinterface registries in the DB.
     */
     @Transactional(readOnly = true)
    public long countAllInterfaces() {
    	
    	try {
    		return this.interfaceRepository.count();
    	}catch(RuntimeException e) {
    		log.error("something went wrong with countAllinterfaces method : {}", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * checks if an edprInterface exists in the DB by given id.
     * @param id - id you wish to check.
     * @return true if it exists, false otherwise.
     */
     @Transactional(readOnly = true)
    public boolean existsEDPRInterfaceByID(Long id) {
    	if(id <= 0 ) { return false;}
    	try {
    		return this.interfaceRepository.existsById(id);
    	}catch(RuntimeException e) {
    		log.error("something went wrong with the existsEDPRInterfaceByID method : {} ", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * deletes an interface by it's ID.
     * @param id - id of the interface we wish to delete.
     * @return true if all went well, false otherwise.
     */
     @Transactional
    public boolean deleteEDPRInterfaceById(Long id) {
    	if(id <= 0) {  throw new IllegalArgumentException("Invalid ID: ID must be positive.");}
    	try{
    	 this.interfaceRepository.deleteById(id);
    	 return true;
    	}catch(RuntimeException e) {
    		log.error("something went wrong with"
    				+ " deleteEDPRInterfaceById method {}", e.getMessage());
    		throw e;
    	}
    }

     @Transactional(readOnly = true)
	public List<Interface> getAllInterfaces(){

		List<Interface> interfaces = this.interfaceRepository.findAll();
		return interfaces;
		}

     
     /**
	     * Fetches an EDPRInterface by it's id.
	     * @param id - id of the interface we wish to fetch from the DB.
	     * @return EDPRInterface fetched from the DB.
	     */
	    @Transactional(readOnly = true)
	    public Optional<InterfaceResponseDTO> getEDPRInterfaceByID(Long id){
	    		if( id <= 0) {return Optional.empty();}
	    		 try {
	    		        return this.interfaceRepository.findById(id)
	    		            .map(EDPRInterface -> {
	    		                List<TaskResponseDTO> taskDTOs = EDPRInterface.getTasks() != null
	    		                    ? EDPRInterface.getTasks().stream()
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

	    		                return new InterfaceResponseDTO(
	    		                		EDPRInterface.getInterfaceID(),
	    		                		EDPRInterface.getEdprInterfaceName(),
	    		                		EDPRInterface.getEdprInterfaceGoal(),
	    		                		EDPRInterface.getEdprInterfaceTechnicalData(),
	    		                		EDPRInterface.getOrigin().getSystemID(),
	    		                		EDPRInterface.getDestination().getSystemID(),
	    		                		EDPRInterface.getCurrentStatus(),
	    		                    taskDTOs
	    		                );
	    		            });

	    		    } catch (EntityNotFoundException e) {
	    		        log.error("Couldn't find EDPRSystem for ID: {}", id);
	    		        throw e;
	    		    }
	    	}
	    	
	    
	    
     	/**
	     * Fetches all EDPRInterfaces from the DB, in the form 
	     * of a list of EDPRInterfaceTreeNodeDTO's. 
	     * @return List of ProcessTreeNodeResponseDTO for each process in the DB.
	     */
	    @Transactional(readOnly = true)
	    public List<InterfaceTreeNodeDTO> GetEDPRInterfaceTreeNodes(){
	    	
	    
	    	try {
	    		 List<Interface> interfaces = this.interfaceRepository.findAll();
	    	        
	    	     
	    		 List<InterfaceTreeNodeDTO> EDPRInterfaceTreeNodes = interfaces.stream()
	    		            .map(EDPRinterface -> new InterfaceTreeNodeDTO(
	    		            		EDPRinterface.getInterfaceID(),
	    		            		EDPRinterface.getEdprInterfaceName(),
	    		            		EDPRinterface.getOrigin().getSystemID(),
	    		            		EDPRinterface.getDestination().getSystemID(),
	    		            		EDPRinterface.getCurrentStatus()
	    		            ))
	    		            .collect(Collectors.toList());
	    	        
	    	        return EDPRInterfaceTreeNodes;
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
    public void updateInterfaceStatusBasedOnTasks(Long id) {	    
    	
    	List<Task> existingTasks = taskRepo.findByEdprInterface_EdprInterfaceID(id);

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
    	 Optional<Interface> optSystem = interfaceRepository.findById(id);

    	    if (optSystem.isPresent()) {
    	        Interface sys = optSystem.get();
    	        sys.setCurrentStatus(currentStatus);
    	        interfaceRepository.save(sys);
    	    } else {
    	        log.warn("Didn't find the system by ID {}", id);
    	    }
    }
    
	
	
}


