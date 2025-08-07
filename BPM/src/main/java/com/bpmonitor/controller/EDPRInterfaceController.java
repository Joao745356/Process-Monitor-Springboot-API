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

import com.bpmonitor.DTOs.CreateInterfaceDTO;
import com.bpmonitor.DTOs.response.InterfaceResponseDTO;
import com.bpmonitor.DTOs.response.InterfaceTreeNodeDTO;
import com.bpmonitor.customExceptions.EntityNotFoundException;
import com.bpmonitor.models.Interface;
import com.bpmonitor.services.InterfaceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/EDPRInterfaces")
public class EDPRInterfaceController {

	private final InterfaceService edprIntService;
	
	public EDPRInterfaceController(InterfaceService edprIntService) {
		this.edprIntService = edprIntService;
	}
	
	
	  @PostMapping("/createEDPRInterface")
	    public ResponseEntity<?> createEDPRInterface(@RequestBody CreateInterfaceDTO intDto) {
	        try {
	            Interface edprInterface = edprIntService.saveEDPRInterface(intDto);
	            return ResponseEntity.status(HttpStatus.CREATED).body(edprInterface);
	        } catch (Exception e) {
	            log.error("Error creating EDPRInterface: {}", e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("An error occurred while creating the interface.");
	        }
	    }

	    @PostMapping("/update/id/{id}")
	    public ResponseEntity<?> updateInterface(@PathVariable Long id, @RequestBody CreateInterfaceDTO intDto) {
	        try {
	            Interface edprInterface = edprIntService.updateEDPRInterface(id, intDto);
	            return ResponseEntity.status(HttpStatus.OK).body(edprInterface);
	        } catch (EntityNotFoundException e) {
	            log.error("Interface not found for update: {}", id);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("Interface not found for id " + id);
	        } catch (Exception e) {
	            log.error("Error updating EDPRInterface: {}", e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("An error occurred while updating the interface.");
	        }
	    }

	    @DeleteMapping("/delete/id/{id}")
	    public ResponseEntity<?> deleteInterface(@PathVariable Long id) {
	        try {
	            boolean deleted = edprIntService.deleteEDPRInterfaceById(id);
	            if (deleted) {
	                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	            } else {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                        .body("No interface found with id " + id);
	            }
	        } catch (Exception e) {
	            log.error("Error deleting EDPRInterface: {}", e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("An error occurred while deleting the interface at id " + id);
	        }
	    }

	    @GetMapping("/getAll")
	    public ResponseEntity<?> getAllInterfaces() {
	        try {
	            List<Interface> EDPRInterfaces = edprIntService.getAllInterfaces();
	            return ResponseEntity.status(HttpStatus.OK).body(EDPRInterfaces);
	        } catch (Exception e) {
	            log.error("Error fetching all EDPRInterfaces: {}", e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("An error occurred while fetching all interfaces.");
	        }
	    }
	    
	    
	    /**
	     * retrieves all the nodes necessary to build a table with the data for interfaces
	     * @return
	     */
	    @GetMapping("/getAllEDPRInterfaceTreeNodes")
	    public ResponseEntity<?> getAllEDPRInterfaceTreeNodes(){
	    	try {
	    		List<InterfaceTreeNodeDTO> interfaces = edprIntService.GetEDPRInterfaceTreeNodes();
	    		return ResponseEntity.status(HttpStatus.OK).body(interfaces );    	
	    	}catch (Exception e) {
	            log.error("Error fetching all getAllEDPRSystemTreeNodes: {}", e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("An error occurred while fetching all interfaces tree nodes.");
	        }
	    }

	    /**
	     * retrieves all the data of the interface. 
	     * @param 
	     * @return
	     */
	    @GetMapping("/id/{id}")
	    public ResponseEntity<?> getInterfaceById(@PathVariable Long id) {
	        try {
	            Optional<InterfaceResponseDTO> edprInterface = edprIntService.getEDPRInterfaceByID(id);
	            if (edprInterface.isPresent()) {
	                return ResponseEntity.status(HttpStatus.OK).body(edprInterface.get());
	            } else {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                        .body("No interface found with id " + id);
	            }
	        } catch (Exception e) {
	            log.error("Error fetching EDPRInterface with id {}: {}", id, e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("An error occurred while fetching interface with id " + id);
	        }
	    }

}
