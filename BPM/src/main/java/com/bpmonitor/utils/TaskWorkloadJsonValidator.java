package com.bpmonitor.utils;

import com.bpmonitor.enums.TaskType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * this class only serves to validate Json task worloads.
 * @author joao7
 *
 */
@Slf4j
public class TaskWorkloadJsonValidator {

	
	

	/**
	 * Don't instantiate this class.
	 * 	 
	 */
	private TaskWorkloadJsonValidator() {
        throw new UnsupportedOperationException("Utility class, do not instantiate!");
    }
	
	
   
	
	/**
     * Validates if a string is valid JSON.
     */
    public static boolean isValidJson(String jsonStr) {
        try {
            new ObjectMapper().readTree(jsonStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * distinguishes a task by it's type and validates it's JSONworkload.
     * @param type - type of the task
     * @param node - workload 
     */
    public static boolean pickValidationByTaskType(TaskType type, JsonNode  node) {
    	
    	log.debug("Validating task workload for type {}: {}", type, node.toPrettyString());
    	
    	switch(type) {
    	case PINGSERVERTASK :
    		return validateFieldForPingServer(node);
    		
    	
    	
    	case QUERYDBTASK: 
    		return validateFieldForQueryDBTask(node);
    		
    		
    	case TOMCATSERVICETASK:
    		//TODO : return validateFieldForTomcatServiceTask(node);
    		break;
    		
    	case HTTPCHECKTASK:
    		//TODO : return validateFieldForWebconnectionTask(node);
    		break;
    	
    	case STFPCONNECTIONTASK:
    		//TODO: return validateFieldSTFPConnectionTask(node);
    		break;
    	
    	default: 
    		log.error("tried to validate a taskType that doesn't exist in PickValidationByTaskType()");
    		return false;
    	}
    	
		return false;
    }
    
   
    /**
     * Validates required fields for any task.
     * @param node JSON workload
     * @param requiredFields Fields to check (e.g., "HOST", "BDURL", "USERNAME")
     * @throws IllegalArgumentException if validation fails
     */
    public static boolean validateFields(JsonNode node, String... requiredFields) {
    	   log.info("Raw workload JSON string: {}", node.toString());

    	    for (String field : requiredFields) {
    	        // Check if field exists
    	        if (!node.has(field)) {
    	            log.error("Here is entire node: '{}'", node);
    	            log.error("Missing required field: '{}'", field);
    	            throw new IllegalArgumentException("Missing or empty field: " + field);        
    	        }

    	        // Optional: check if the field is empty
    	        if (node.get(field).asText().isEmpty()) {
    	            log.error("Field '{}' exists but is empty", field);
    	            throw new IllegalArgumentException("Field '" + field + "' cannot be empty");
    	        }
    	    }

    	    return true;
    	}
    
  
    
    
    /**
     * Validates that a JSON node has the required fields for a task.
     * @throws IllegalArgumentException if validation fails.
     */
    public static boolean validateFieldForPingServer(JsonNode  node
    		) {    	
        return validateFields(node, "HOST", "RESPONSIBLEPARTY");
    }
    
    /**
     * Validates that a JSON node has the required fields for a task.
     * @throws IllegalArgumentException if validation fails.
     */
   
    public static boolean validateFieldForQueryDBTask(JsonNode node) {    	
   
        return validateFields(node, "BDURL","USERNAME" , "PASSWORD", "QUERY" );
    }
    
    
   
}
