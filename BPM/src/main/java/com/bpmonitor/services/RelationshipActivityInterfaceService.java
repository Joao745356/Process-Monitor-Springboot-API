package com.bpmonitor.services;
/**package com.edpr.remsweb.bpmonitor.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edpr.remsweb.bpmonitor.customExceptions.EntityNotFoundException;
import com.edpr.remsweb.bpmonitor.models.Activity;
import com.edpr.remsweb.bpmonitor.models.EDPRInterface;
import com.edpr.remsweb.bpmonitor.models.RelationshipActivityInterface;
import com.edpr.remsweb.bpmonitor.repositories.EDPRInterfaceRepository;
import com.edpr.remsweb.bpmonitor.repositories.RelationshipActivityInterfaceRepository;
import lombok.extern.slf4j.Slf4j;


/**
 * Service class responsible for CRUD of relationshipActivityInterface table.
 * 
 * @author joao7

@Service
@Slf4j
public class RelationshipActivityInterfaceService {


	   private final RelationshipActivityInterfaceRepository relationshipRepo;
	   private final EDPRInterfaceRepository intRepo;
	   
	  
	   
	    public RelationshipActivityInterfaceService(
	    		RelationshipActivityInterfaceRepository relationshipRepo,
	    		EDPRInterfaceRepository intRepo) {
	        
	    	this.relationshipRepo = relationshipRepo;
	    	this.intRepo = intRepo;
	    }

	   
	     * Fetches both activity and interface, compares their status. If a interface doesn't have the
	     * status equal to it's activity, alters the system's status so that it now matches it's activity's
	     * status.
	     * @param relationshipId - id of the relationship that bridges activity and system
	     * @return - true if all goes well, false if the relationship isn't found.
	   
	    @Transactional
	    public boolean updateInterfaceStatus(Long relationshipId) {
	    	
	    	Optional<RelationshipActivityInterface> systemOptional = relationshipRepo.findById(relationshipId);
	    	
	    	if(!systemOptional.isPresent()) {
	    		log.warn("No relationship found with ID: {}", relationshipId); 
	    		return false;
	    	}
	    	
	    	RelationshipActivityInterface sysRelationshipFromActivityToSystem = systemOptional.get();
	    	
	    	Activity act = sysRelationshipFromActivityToSystem.getActivity();
	    	EDPRInterface sys = sysRelationshipFromActivityToSystem.getEdprInterface();
	    	
	    	if(sys.getCurrentStatus() != act.getActivityStatus()) {
	    		sys.setCurrentStatus(act.getActivityStatus());
	    		intRepo.save(sys);
	    	}
	    	
	    	return true;
	    		
	    }
	    
	    

	     * creates a relationshipBetween activity and EDPRInterface.
	     * @param relationship - RelationshipActivityInterface object.
	 
	    @Transactional
	    public void createRelationshipBetweenActivityAndInterface(RelationshipActivityInterface relationship) {
	    	saveRelationship(relationship);
	    }




	  
	     * Saves a relationship between an activity and a system, if it doesn't already exist.
	 
	    @Transactional
	    public RelationshipActivityInterface saveRelationship(RelationshipActivityInterface relationship) {
	        Long activityId = relationship.getActivity().getActivityID();
	        Long interfaceId = relationship.getEdprInterface().getEdprInterfaceID();

	        if (relationshipRepo.existsByActivity_ActivityIDAndEdprInterface_EdprInterfaceID(activityId, interfaceId)) {
	            throw new IllegalArgumentException("Relationship already exists between activity " + activityId + " and interface " + interfaceId);
	        }

	        try {
	            return relationshipRepo.save(relationship);
	        } catch (RuntimeException e) {
	            log.error("Failed to save relationship: {}", e.getMessage());
	            throw e;
	        }
	    }

	    
	     * Deletes a relationship by its ID.
	    
	    @Transactional
	    public boolean deleteRelationshipById(Long id) {
	        if (id <= 0) throw new IllegalArgumentException("Invalid ID");

	        try {
	            relationshipRepo.deleteById(id);
	            return true;
	        } catch (RuntimeException e) {
	            log.error("Error deleting relationship with ID {}: {}", id, e.getMessage());
	            throw e;
	        }
	    }

	   
	     * Checks if a relationship between an activity and a system exists.
	   
	    @Transactional(readOnly = true)
	    public boolean existsRelationship(Long activityId, Long systemId) {
	        if (activityId == null || systemId == null || activityId <= 0 || systemId <= 0) return false;

	        try {
	            return relationshipRepo.existsByActivity_ActivityIDAndEdprInterface_EdprInterfaceID(activityId, systemId);
	        } catch (RuntimeException e) {
	            log.error("Error checking existence of relationship between activity {} and interface {}: {}", activityId, systemId, e.getMessage());
	            throw e;
	        }
	    }

	   
	     * Fetches all relationships.
	   
	    @Transactional(readOnly = true)
	    public List<RelationshipActivityInterface> findAllRelationships() {
	        try {
	            return relationshipRepo.findAll();
	        } catch (RuntimeException e) {
	            log.error("Error retrieving all relationships: {}", e.getMessage());
	            throw e;
	        }
	    }

	  
	     * Gets a relationship by ID.
	     
	    @Transactional(readOnly = true)
	    public Optional<RelationshipActivityInterface> getRelationshipById(Long id) {
	        if (id <= 0) return Optional.empty();

	        try {
	            return relationshipRepo.findById(id);
	        } catch (EntityNotFoundException e) {
	            log.error("Relationship with ID {} not found", id);
	            throw e;
	        }
	    }
} */
