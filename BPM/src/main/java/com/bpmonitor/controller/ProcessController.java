package com.bpmonitor.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bpmonitor.DTOs.CreateProcessDTO;
import com.bpmonitor.DTOs.response.ProcessResponseDTO;
import com.bpmonitor.DTOs.response.ProcessTreeNodeResponseDTO;
import com.bpmonitor.DTOs.response.SubprocessTreeNodeResponseDTO;
import com.bpmonitor.models.Process;
import com.bpmonitor.services.ProcessService;

import lombok.extern.slf4j.Slf4j;


@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/Processes")
@Slf4j
public class ProcessController {

	private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    /**
     * Creates a new process.
     * @param process the process to create
     */
    @PostMapping("/create")
    public ResponseEntity<Void> createProcess(@RequestBody CreateProcessDTO process) {
        processService.saveProcess(process);
        
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Updates a process with new data.
     * @param id the id of the process to update
     * @param process the new process data
     * @return ResponseEntity indicating success or failure
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateProcess(@PathVariable Long id, @RequestBody CreateProcessDTO process) {
        boolean updated = processService.updateProcess(id, process);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    

    /**
     * Gets all processes.
     * @return List of processes
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Process>> getAllProcesses() {
        List<Process> processes = processService.findAllProcesses();
        return ResponseEntity.ok(processes);
    }
    
    /**
     * Gets all process treeNodes to represent in a tree table
     */
    @GetMapping("/getProcessTreeNodes")
    public ResponseEntity<List<ProcessTreeNodeResponseDTO>> getAllProcessTreeNodes(){
    	List<ProcessTreeNodeResponseDTO> processes = processService.GetProcessTreeNodes();
    	return ResponseEntity.ok(processes);
    }

    
    /**
     * Gets all process treeNodes to represent in a tree table
     */
    @GetMapping("/getSubprocessTreeNodes")
    public ResponseEntity<List<SubprocessTreeNodeResponseDTO>> getAllSubprocessTreeNodes(){
    	List<SubprocessTreeNodeResponseDTO> processes = processService.GetSubprocessTreeNodes();
    	return ResponseEntity.ok(processes);
    }
    
    
    /**
     * Gets a process by its id.
     * @param id the id of the process to fetch
     * @return the process with the specified id, along with it's subprocesses.
     */
    @GetMapping("id/{id}")
    public ResponseEntity<ProcessResponseDTO> getProcessById(@PathVariable Long id) {
        Optional<ProcessResponseDTO> process = processService.getProcessByID(id);
        return process.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Counts all processes.
     * @return count of all processes
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAllProcesses() {
        long count = processService.countAllProcesses();
        return ResponseEntity.ok(count);
    }

    /**
     * Deletes a process by its id.
     * @param id the id of the process to delete
     * @return ResponseEntity indicating success or failure
     */
    @DeleteMapping("delete/id/{id}")
    public ResponseEntity<Void> deleteProcessById(@PathVariable Long id) {
        boolean deleted = processService.deleteProcessById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
