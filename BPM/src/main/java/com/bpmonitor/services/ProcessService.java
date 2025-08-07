package com.bpmonitor.services;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bpmonitor.DTOs.CreateProcessDTO;
import com.bpmonitor.DTOs.response.ProcessResponseDTO;
import com.bpmonitor.DTOs.response.ProcessTreeNodeResponseDTO;
import com.bpmonitor.DTOs.response.SubprocessResponseDTO;
import com.bpmonitor.DTOs.response.SubprocessTreeNodeResponseDTO;
import com.bpmonitor.customExceptions.EntityNotFoundException;
import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.models.Process;
import com.bpmonitor.models.Subprocess;
import com.bpmonitor.repositories.ProcessRepository;
import com.bpmonitor.repositories.SubprocessRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProcessService {

	
	private final ProcessRepository processRepository; // repo for  - process table
	private final SubprocessRepository subprocessRepo; // repo for - subprocess table
    
    public ProcessService(
    		ProcessRepository ProcessRepo,
    		SubprocessRepository subprocessRepo) {
    	this.processRepository = ProcessRepo;
    	this.subprocessRepo = subprocessRepo;  	
    }
    
    
    /**
     * Creates a process.
     * @param process - process object you wish to create.
     */
    @Transactional
    public void createProcess(CreateProcessDTO process) {
    	saveProcess(process);
    }
    
    /**
     * updates a process based on newData.
     * @param id - id of the process to update
     * @param newProcessData - new Process object
     * @return true if all goes well, false otherwise.
     */
    @Transactional
    public boolean updateProcess(Long id, CreateProcessDTO newProcessData) {
    	
    	Optional <Process> optProcess = processRepository.findById(id);
    	
    	if(!optProcess.isPresent()) return false;
    	
    	Process process = optProcess.get();
    	
    	process.setProcessName(newProcessData.getProcessName());
    	
    	
    	processRepository.save(process);
    	return true;
    }
    
    /**
     * updates a process status based on the status of it's subprocesses.
     * @param id - id of the process to update.
     * @return True if all goes well, false otherwise.
     */
    @Transactional
    public boolean updateProcessStatusBasedOnSubprocesses(Long id) {
    	
    	Optional <Process> optProcess = processRepository.findById(id);
    	List<Subprocess> Subprocess = subprocessRepo.findSubprocessByProcess_ProcessID(id);
    	int counterOfDowns = 0;
    	int counterOfCompromised = 0;
    	
    	if(!optProcess.isPresent()) return false;
    	
    	Process process = optProcess.get();
    	
    	for(Subprocess s : Subprocess) {
    		if(s.getSubprocessStatus().equals(OperationalStatus.COMPROMISED)) {
    			counterOfCompromised++;
    		}else if(s.getSubprocessStatus().equals(OperationalStatus.DOWN)) {
    			counterOfDowns++;
    		}
    	}
    		if(counterOfDowns == 0 && counterOfCompromised == 0) {
    			process.setProcessStatus(OperationalStatus.UP);
    			processRepository.save(process);
    		}else if(counterOfDowns >= 1) {
    			process.setProcessStatus(OperationalStatus.DOWN);
    			processRepository.save(process);
    		}else {
    			process.setProcessStatus(OperationalStatus.COMPROMISED);
    			processRepository.save(process);
    			
    		}
    	
		return true;
    }
    	
    	
 
    
    /**
     * Saves a given process into the Database.
     * @param process - object to save.
     * @return ProcessResponseDTO containing ID, name and status of the process.
     */
    public ProcessResponseDTO saveProcess(CreateProcessDTO dto) {
    	try {
    		
    		Process process = new Process(
    				dto.getProcessName(),
    				dto.getStatus()
    				);
    		
    		this.processRepository.save(process);
    		
    		ProcessResponseDTO response = new ProcessResponseDTO(
    				process.getProcessID(),
    				process.getProcessName(),
    				process.getProcessStatus(),
    				null
    				);
    		
    		
    	 return response;
    	}catch(RuntimeException e){
    		log.error("something went wrong with saveProcess method {}", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * Fetches all Processes from the DB, with their subprocesses, and activities. 
     * Does not return tasks for the acitivies, and returns all the 3 mentioned objects,
     * in the form of a ProcessTreeNodeResponseDTO. 
     * @return List of ProcessTreeNodeResponseDTO for each process in the DB.
     */
    @Transactional(readOnly = true)
    public List<ProcessTreeNodeResponseDTO> GetProcessTreeNodes(){
    	
    	/**
    	 * Encontro todos os processos na DB, mapeio os mesmos e os seus subprocess e activities
    	 * para nodes dentro de cada process tree e envio.
		 */
    	try {
    		 List<Process> processes = this.processRepository.findAll();
    	        
    	     
    		 List<ProcessTreeNodeResponseDTO> processTreeNodes = processes.stream()
    		            .map(process -> new ProcessTreeNodeResponseDTO(
    		                process.getProcessID(),
    		                process.getProcessName(),
    		                process.getProcessStatus(),
    		                process.getSubprocesses().stream()
    		                    .map(subprocess -> new ProcessTreeNodeResponseDTO(
    		                        subprocess.getSubprocessID(),
    		                        subprocess.getSubprocessName(),
    		                        subprocess.getSubprocessStatus(),
    		                        subprocess.getActivities().stream()
    		                            .map(activity -> new ProcessTreeNodeResponseDTO(
    		                                activity.getActivityID(),
    		                                activity.getActivityName(),
    		                                activity.getActivityStatus(),
    		                                null  // I don't show tasks on the main table.
    		                            ))
    		                            .collect(Collectors.toList())
    		                    ))
    		                    .collect(Collectors.toList())
    		            ))
    		            .collect(Collectors.toList());
    	        
    	        return processTreeNodes;
    	}catch(RuntimeException e) {
    		log.error("something went wrong at findAllProcesses method : {}", e.getMessage());
    		throw e;
    	}
   
    }
  
    /**
     * Fetches all Processes from the DB, with their subprocesses, and activities. 
     * Now returns tasks for the acitivies, and returns all the 3 mentioned objects,
     * in the form of a SubprocessTreeNodeResponseDTO. 
     * @return List of ProcessTreeNodeResponseDTO for each process in the DB.
     */
    @Transactional(readOnly = true)
    public List<SubprocessTreeNodeResponseDTO> GetSubprocessTreeNodes(){
    	
    	/**
    	 * Encontro todos os processos na DB, mapeio os mesmos e os seus subprocess e activities
    	 * para nodes dentro de cada process tree e envio.
		 */
    	try {
    		 List<Process> processes = this.processRepository.findAll();
    	        
    	     
    		 List<SubprocessTreeNodeResponseDTO> processTreeNodes = processes.stream()
    		            .map(process -> new SubprocessTreeNodeResponseDTO(
    		                process.getProcessID(),
    		                process.getProcessName(),
    		                process.getProcessStatus(), // dosn't care for task Status
    		                process.getSubprocesses().stream()
    		                    .map(subprocess -> new SubprocessTreeNodeResponseDTO(
    		                        subprocess.getSubprocessID(),
    		                        subprocess.getSubprocessName(),
    		                        subprocess.getSubprocessStatus(),// dosn't care for task Status
    		                        subprocess.getActivities().stream()
    		                            .map(activity -> new SubprocessTreeNodeResponseDTO(
    		                                activity.getActivityID(),
    		                                activity.getActivityName(),
    		                                activity.getActivityStatus(),// dosn't care for task Status
    		                                activity.getTasks().stream()
    		                                .map(task -> new SubprocessTreeNodeResponseDTO(
    		                                    task.getTaskID(),
    		                                    task.getTaskName(),
    		                                    task.getTaskStatus(),// dosn't care for operational Status
    		                                    task.getResults().stream()
    		                                        .map(result -> new SubprocessTreeNodeResponseDTO(
    		                                            result.getTaskValidationResultID(),
    		                                            result.getTask().getTaskName(), // or date/time/whatever makes sense
    		                                            result.getStatus(), // dosn't care for task Status
    		                                            null
    		                                        ))
    		                                        .collect(Collectors.toList())
    		                                ))
    		                                .collect(Collectors.toList())
    		                            ))
    		                            .collect(Collectors.toList())
    		                    ))
    		                    .collect(Collectors.toList())
    		            ))
    		            .collect(Collectors.toList());
    	        
    	        return processTreeNodes;
    	}catch(RuntimeException e) {
    		log.error("something went wrong at findAllProcesses method : {}", e.getMessage());
    		throw e;
    	}
   
    }

    /**
     * Fetches all Processes from the DB. 
     * @return List of Processes with all records in the DB.
     */
    @Transactional(readOnly = true)
    public List<Process> findAllProcesses(){
    	try {
    		return this.processRepository.findAll();
    	}catch(RuntimeException e) {
    		log.error("something went wrong at findAllProcesses method : {}", e.getMessage());
    		throw e;
    	}
    }

    	
    /**
     * Fetches a Process by it's id.
     * @param id - id of the Process we wish to fetch from the DB.
     * @return ProcessResponseDTO, containing only the processes subprocesses.
     */
    @Transactional(readOnly = true)
    public Optional<ProcessResponseDTO> getProcessByID(Long id){
    		if( id <= 0) {return Optional.empty();}
    		try {
    			
    			Optional<Process> process1 = processRepository.findById(id);
    			 Process process = process1.get();
    	            
    			 
    	            // Convert the list of Subprocess to SubprocessResponseDTOs
    			 List<SubprocessResponseDTO> subprocessDTOs = process.getSubprocesses().stream()
    					    .map(subprocess -> new SubprocessResponseDTO(
    					        subprocess.getSubprocessID(),
    					        subprocess.getSubprocessName(),
    					        subprocess.getSubprocessStatus(),
    					        null // I don't send activities here.
    					    ))
    					    .collect(Collectors.toList());
    	            
    	            ProcessResponseDTO response = new ProcessResponseDTO(
    	                process.getProcessID(),
    	                process.getProcessName(),
    	                process.getProcessStatus(),
    	                subprocessDTOs
    	            );
    	            
    	            return Optional.of(response);
    	        
    		}catch(EntityNotFoundException e) {
    			log.error("couldn't find Process for ID : {}" , id);
    			throw e;
    		}
    	}
    	
    /**
     * Counts all Processes in the DB.
     * @return long value representing the amount of Process registries in the DB.
     */
    @Transactional(readOnly = true)
    public long countAllProcesses() {
    	
    	try {
    		return this.processRepository.count();
    	}catch(RuntimeException e) {
    		log.error("something went wrong with countAllProcesses method : {}", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * checks if a process exists in the DB by given id.
     * @param id - id you wish to check.
     * @return true if it exists, false otherwise.
     */
    @Transactional(readOnly = true)
    public boolean existsProcessByID(Long id) {
    	if(id <= 0 ) { return false;}
    	try {
    		return this.processRepository.existsById(id);
    	}catch(RuntimeException e) {
    		log.error("something went wrong with the existsProcessByID method : {} ", e.getMessage());
    		throw e;
    	}
    }
    
    /**
     * deletes a Process by it's ID.
     * @param id - id of the Process we wish to delete.
     * @return true if all went well, false otherwise.
     */
    @Transactional
    public boolean deleteProcessById(Long id) {
    	if(id <= 0) {  throw new IllegalArgumentException("Invalid ID: ID must be positive.");}
    	try{
    	 this.processRepository.deleteById(id);
    	 return true;
    	}catch(RuntimeException e) {
    		log.error("something went wrong with"
    				+ " deleteProcessById method {}", e.getMessage());
    		throw e;
    	}
    }



}
