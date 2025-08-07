package com.bpmonitor.services;
/**package com.edpr.remsweb.bpmonitor.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edpr.remsweb.bpmonitor.customExceptions.EntityNotFoundException;
import com.edpr.remsweb.bpmonitor.models.Activity;
import com.edpr.remsweb.bpmonitor.models.EDPRSystem;
import com.edpr.remsweb.bpmonitor.models.RelationshipActivitySystem;
import com.edpr.remsweb.bpmonitor.repositories.EDPRSystemRepository;
import com.edpr.remsweb.bpmonitor.repositories.RelationshipActivitySystemRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for the CRUD operations of relationshipActivitySystem table.
 * also contains operations for counting,deleting and updating a system's status.
 * @author joao7

@Service
@Slf4j
public class RelationshipActivitySystemService {
	
	   private final RelationshipActivitySystemRepository relationshipRepo;
	   private final EDPRSystemRepository sysRepo; // I alter stuff in System tables, but I don't alter anything in activities
	   //I only bridge activities and systems.
	   
	    public RelationshipActivitySystemService(
	    		RelationshipActivitySystemRepository relationshipRepo, 
	    		EDPRSystemRepository sysRepo) {
	        this.relationshipRepo = relationshipRepo;
	        this.sysRepo = sysRepo;
	        
	    }

	    
	    /**
	     * Fetches both activity and system, compares their status. If a system doesn't have the
	     * status equal to it's activity, alters the system's status so that it now matches it's activity's
	     * status.
	     * @param relationshipId - id of the relationship that bridges activity and system
	     * @return - true if all goes well, false if the relationship isn't found.
	    
	    @Transactional
	    public boolean updateSystemStatus(Long relationshipId) {
	    	
	    	Optional<RelationshipActivitySystem> systemOptional = relationshipRepo.findById(relationshipId);
	    	
	    	if(!systemOptional.isPresent()) {
	    		log.warn("No relationship found with ID: {}", relationshipId); 
	    		return false;
	    	}
	    	
	    	RelationshipActivitySystem sysRelationshipFromActivityToSystem = systemOptional.get();
	    	
	    	Activity act = sysRelationshipFromActivityToSystem.getActivity();
	    	EDPRSystem sys = sysRelationshipFromActivityToSystem.getSystem();
	    	
	    	if(sys.getCurrentStatus() != act.getActivityStatus()) {
	    		sys.setCurrentStatus(act.getActivityStatus());
	    		sysRepo.save(sys);
	    	}
	    	
	    	return true;
	    		
	    }
	    
	    
	    /**
	     * creates a relationshipBetween activity and EDPRSystem.
	     * @param relationship - RelationshipActivitySystem object.
	    
	    @Transactional
	    public void createRelationshipBetweenActivityAndSystem(RelationshipActivitySystem relationship) {
	    	saveRelationship(relationship);
	    }
	    
	    
	    /**
	     * Saves a relationship between an activity and a system, if it doesn't already exist.
	     
	    @Transactional
	    public RelationshipActivitySystem saveRelationship(RelationshipActivitySystem relationship) {
	        Long activityId = relationship.getActivity().getActivityID();
	        Long systemId = relationship.getSystem().getEdprSystemID();

	        if (relationshipRepo.existsByActivity_ActivityIDAndSystem_EdprSystemID(activityId, systemId)) {
	            throw new IllegalArgumentException("Relationship already exists between activity " + activityId + " and system " + systemId);
	        }

	        try {
	            return relationshipRepo.save(relationship);
	        } catch (RuntimeException e) {
	            log.error("Failed to save relationship: {}", e.getMessage());
	            throw e;
	        }
	    }

	    /**
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

	    /**
	     * Checks if a relationship between an activity and a system exists.
	     
	    @Transactional(readOnly = true)
	    public boolean existsRelationship(Long activityId, Long systemId) {
	        if (activityId == null || systemId == null || activityId <= 0 || systemId <= 0) return false;

	        try {
	            return relationshipRepo.existsByActivity_ActivityIDAndSystem_EdprSystemID(activityId, systemId);
	        } catch (RuntimeException e) {
	            log.error("Error checking existence of relationship between activity {} and system {}: {}", activityId, systemId, e.getMessage());
	            throw e;
	        }
	    }

	    /**
	     * Fetches all relationships.
	   
	    @Transactional(readOnly = true)
	    public List<RelationshipActivitySystem> findAllRelationships() {
	        try {
	            return relationshipRepo.findAll();
	        } catch (RuntimeException e) {
	            log.error("Error retrieving all relationships: {}", e.getMessage());
	            throw e;
	        }
	    }

	    /**
	     * Fetches a relationship by ID.
	     
	    @Transactional(readOnly = true)
	    public Optional<RelationshipActivitySystem> getRelationshipById(Long id) {
	        if (id <= 0) return Optional.empty();

	        try {
	            return relationshipRepo.findById(id);
	        } catch (EntityNotFoundException e) {
	            log.error("Relationship with ID {} not found", id);
	            throw e;
	        }
	    }

} */
