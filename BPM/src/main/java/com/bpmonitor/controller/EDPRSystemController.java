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

import com.bpmonitor.DTOs.CreateSystemDTO;
import com.bpmonitor.DTOs.response.SystemResponseDTO;
import com.bpmonitor.DTOs.response.SystemTreeNodeDTO;
import com.bpmonitor.models.System;
import com.bpmonitor.services.SystemService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/EDPRSystems")
public class EDPRSystemController {

    private final SystemService edprSystemService;

    public EDPRSystemController(SystemService edprSystemService) {
        this.edprSystemService = edprSystemService;
    }

    // Endpoint to create an EDPRSystem
    @PostMapping("/create")
    public ResponseEntity<?> createEDPRSystem(@RequestBody CreateSystemDTO edprSystem) {
        try {
            edprSystemService.createEDPRSystem(edprSystem);
            return ResponseEntity.status(HttpStatus.CREATED).body(edprSystem);
        } catch (Exception e) {
            log.error("Error creating EDPRSystem: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the system.");
        }
    }

    // Endpoint to update an EDPRSystem by its ID
    @PostMapping("/update/id/{id}")
    public ResponseEntity<?> updateEDPRSystem(@PathVariable Long id, @RequestBody CreateSystemDTO edprSystem) {
        try {
            Optional<SystemResponseDTO> existingSystem = edprSystemService.getEDPRSystemByID(id);
            if (existingSystem.isPresent()) {
                System updatedSystem = edprSystemService.saveEDPRSystem(edprSystem);
                return ResponseEntity.status(HttpStatus.OK).body(updatedSystem);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("System with ID " + id + " not found.");
            }
        } catch (Exception e) {
            log.error("Error updating EDPRSystem: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the system.");
        }
    }

    // Endpoint to delete an EDPRSystem by its ID
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<?> deleteEDPRSystem(@PathVariable Long id) {
        try {
            boolean deleted = edprSystemService.deleteEDPRSystemById(id);
            if (deleted) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No system found with ID " + id);
            }
        } catch (Exception e) {
            log.error("Error deleting EDPRSystem: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the system.");
        }
    }

    // Endpoint to get all EDPRSystems
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllEDPRSystems() {
        try {
            List<System> systems = edprSystemService.findAllEDPRSystems();
            return ResponseEntity.status(HttpStatus.OK).body(systems);
        } catch (Exception e) {
            log.error("Error fetching all EDPRSystems: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching all systems.");
        }
    }
    
    
    /**
     * retrieves all the nodes necessary to build a table with the data for edprsystems
     * @return
     */
    @GetMapping("/getAllEDPRSystemTreeNodes")
    public ResponseEntity<?> getAllEDPRSystemTreeNodes(){
    	try {
    		List<SystemTreeNodeDTO> systems = edprSystemService.GetSystemTreeNodes();
    		return ResponseEntity.status(HttpStatus.OK).body(systems);    	
    	}catch (Exception e) {
            log.error("Error fetching all EDPRSystemsTreeNodes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching all systems tree nodes.");
        }
    }
    
    
    

    /**
     * retrieves all the data of one system in specific. 
     * @param id of the edprsystem you want.
     * @return
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getEDPRSystemById(@PathVariable Long id) {
        try {
            Optional<SystemResponseDTO> system = edprSystemService.getEDPRSystemByID(id);
            if (system.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(system.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No system found with ID " + id);
            }
        } catch (Exception e) {
            log.error("Error fetching EDPRSystem with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching system with ID " + id);
        }
    }
}