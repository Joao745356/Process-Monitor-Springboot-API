package com.bpmonitor.services;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.bpmonitor.DTOs.CreateTaskDTO;
import com.bpmonitor.DTOs.response.SubprocessTreeNodeResponseDTO;
import com.bpmonitor.DTOs.response.TaskErrorResponseDTO;
import com.bpmonitor.DTOs.response.TaskResultResponseDTO;
import com.bpmonitor.DTOs.response.TaskTreeResponseDTO;
import com.bpmonitor.customExceptions.EntityNotFoundException;
import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.enums.TaskRecurrence;
import com.bpmonitor.enums.TaskStatus;
import com.bpmonitor.enums.TaskType;
import com.bpmonitor.factories.TaskFactory;
import com.bpmonitor.models.Activity;
import com.bpmonitor.models.Interface;
import com.bpmonitor.models.Process;
import com.bpmonitor.models.System;
import com.bpmonitor.models.Task;
import com.bpmonitor.models.TaskError;
import com.bpmonitor.models.TaskValidationResult;
import com.bpmonitor.repositories.ActivityRepository;
import com.bpmonitor.repositories.InterfaceRepository;
import com.bpmonitor.repositories.SystemRepository;
import com.bpmonitor.repositories.TaskErrorRepository;
import com.bpmonitor.repositories.TaskRepository;
import com.bpmonitor.repositories.TaskValidationResultRepository;
import com.bpmonitor.tasks.*;
import com.bpmonitor.utils.*;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class responsible for CRUD of Control tasks table.
 * @author joao7
 *
 */
@Service
@Slf4j
public class TaskService {
	
	 
	  	private final TaskRepository taskRepository;
	  	private final ActivityRepository actitivityRepository;
	  	private final TaskValidationResultRepository taskValidationResultRepo;
	  	private final TaskErrorRepository taskErrorRepo;
	  	private final SystemRepository sysRepo;
	    private final InterfaceRepository interfaceRepo;
	    private final TaskFactory taskFactory;

	 
	    public TaskService(TaskRepository taskRepository,
	    		ActivityRepository actitivityRepository ,
	    		 TaskValidationResultRepository taskValidationResultRepo,
	    		 TaskErrorRepository taskErrorRepo,
	    		 SystemRepository sysRepo,
	    		 InterfaceRepository interfaceRepo,
	    		 TaskFactory taskFactory) {
	    	
	        this.taskRepository = taskRepository;
	        this.actitivityRepository = actitivityRepository;
	        this.taskValidationResultRepo = taskValidationResultRepo;
	        this.taskErrorRepo = taskErrorRepo;
	        this.sysRepo = sysRepo;
	        this.interfaceRepo = interfaceRepo;
	        this.taskFactory = taskFactory;
	    }
	  
	   
	    
	    
	  
	 	/**
	 	 * fetches all tasks in the DB
	 	 * @return
	 	 */
	    @Transactional(readOnly = true) 
	    public List<Task> getAllTasks() {
	    	log.debug("I entered the getAllTasks method " );
	        return taskRepository.findAll();
	    }

	    /**
	     * fetches a task from DB by it's ID.
	     * @param id
	     * @return
	     */
	    @Transactional(readOnly = true) 
	    public Optional<Task> getTaskById(Long id) {
	    	
	    	if (id == null || id <= 0) { 
	    		log.debug("Id send to getTaskByID was {} ", id);
	    		throw new IllegalArgumentException("ID must be a positive number");
	        }
	    	
	        return taskRepository.findById(id);
	    }

	    
	    public Task createSimpleTask(CreateTaskDTO dto) {
	    	Activity activity = actitivityRepository.findById(dto.getActivityID())
	                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));


	        Task task;

	      
	        switch (dto.getTaskType()) {
	        case PINGSERVERTASK:
	            PingServerTask pingTask = new PingServerTask();	     
	            task = pingTask;
	            break;

	        // Add more case statements for other task types here

	        default:
	            throw new IllegalArgumentException("Unsupported task type: " + dto.getTaskType());
	    }

    		
	        task.setTaskName(dto.getTaskName());
	        task.setTaskDescription(dto.getTaskDescription());
	        task.setTaskStatus(TaskStatus.UNRUN);
	        task.setActivity(activity);
	        task.setRecurrence(dto.getRecurrence());
	        task.setWorkload(dto.getWorkload());
	        
	    

	        if (dto.getSystemID() != 0) {
	            System system = sysRepo.findById(dto.getSystemID())
	                    .orElseThrow(() -> new EntityNotFoundException("System not found"));
	            task.setSystem(system);
	        } else if (dto.getInterfaceID() != 0) {
	            Interface edprInterface = interfaceRepo.findById(dto.getInterfaceID())
	                    .orElseThrow(() -> new EntityNotFoundException("Interface not found"));
	            task.setInterface(edprInterface);
	        }	        
	        task.setTaskStatus(TaskStatus.UNRUN);

	        return task;
	    }
	    
	    /**
	     * Creates a task to DB
	     * @param task 
	     * @return
	     */
	    @Transactional
	    public Task createTask(CreateTaskDTO taskdto) {
	        log.info("Creating task from DTO: {}", taskdto);
	        Task task = createSimpleTask(taskdto);
	        Task saved = null;
	        try {
	            saved = taskRepository.save(task);
	            taskRepository.flush();
	            log.info("Saved task with ID {}", saved.getTaskID());
	        } catch (Exception e) {
	            log.error("Error saving task", e);
	            throw e; // rethrow or handle accordingly
	        }
	        return saved;
	    }

	    /**
	     * updates a task in the DB
	     * @param task 
	     * @return
	     */
	    @Transactional
	    public void updateTask(Long id, CreateTaskDTO updatedTaskData) {
	    	
	    	if (updatedTaskData.getTaskName() == null) {
	            log.error("Attempted to update a task to a task with null name");
	            throw new IllegalArgumentException("Task name cannot be null");
	        }
	    	
	    	Activity activity = actitivityRepository.findById(updatedTaskData.getActivityID())
	    	        .orElseThrow(() -> new EntityNotFoundException("Activity not found"));
	    	
	    	Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
	    	
	    	 task.setTaskName(updatedTaskData.getTaskName());
             task.setTaskDescription(updatedTaskData.getTaskDescription());
             task.setTaskStatus(updatedTaskData.getTaskStatus());
             //task.setTaskType(updatedTaskData.getTaskType());
             task.setRecurrence(updatedTaskData.getRecurrence());
	    	 task.setActivity(activity);
	    }

	    @Transactional
	    public void updateTaskWorkload(Long id, String newWorkload) {
	    	if(newWorkload == null) {
	    		log.error("updateWorkLoad was sent a null or empty string");
	    		throw new IllegalArgumentException("Workload must be a JSON");
	    	}
	    	
	    	Task task = taskRepository.findById(id)
	    	        .orElseThrow(() -> new RuntimeException("Task not found"));
	    	    task.setWorkload(newWorkload);
	    }
	    
	    @Transactional(propagation = Propagation.REQUIRES_NEW)
		public void updateTaskStatusBasedOnResult(Task task, TaskValidationResult result) {
	    		
			  Task taskInDB = taskRepository.findById(task.getTaskID()).get();
			  TaskStatus previousStatus = taskInDB.getTaskStatus();
			  TaskStatus newStatus = result.getStatus();
			  	
			  log.debug("I am in update TaskStatus and my task's status was {} and my result status is {}",previousStatus, newStatus );
			  //if my task and taskValidationResult statuses don't match I change them!
			   log.debug("previous {} and new {}", previousStatus, newStatus);
			  if(previousStatus != newStatus) {
			        task.setTaskStatus(newStatus);
			        taskRepository.save(task); 
	    	}
			    
		}
	    

	    /**
	     * deletes task from DB
	     * @param id
	     */
	    @Transactional
	    public void deleteTask(Long id) {
	    	if(!taskRepository.existsById(id)) {
	    		log.warn("Attempted to delete a non-existent task with ID {}", id);
	    		throw new IllegalArgumentException("Task not found for ID " + id);
	    	}
	        log.info("Deleting task with ID {}", id);
	        
	        taskValidationResultRepo.deleteTaskValidationResultByTask_TaskID(id);
	        taskErrorRepo.deleteTaskErrorByTaskValidationResult_Task_TaskID(id);
	        taskRepository.deleteById(id);
	    }
	    
	    /**
	     * returns Task list with the recurrence sent to the method.
	     * @param recurrence - TaskRecurrence enum value
	     * @return
	     */
	    @Transactional(readOnly = true) 
	    public List<Task> getTasksByRecurrence(TaskRecurrence recurrence) {
	        return taskRepository.findTaskByRecurrence(recurrence);
	    }
	    
	 
	    
	    /**
	     * returns Task list with the status sent to the method.
	     * @param Status - Status enum value
	     * @return
	     */
	    @Transactional(readOnly = true) 
	    public List<Task> getTasksByStatus(TaskStatus Status) {
	        return taskRepository.findTaskByTaskStatus(Status);
	    }

	    /**
	     * returns Task list with the tasks that have activityId 
	     * as their foreign key to activity table.
	     * @param activityId - Id of the activity.
	     * @return
	     */
	    @Transactional(readOnly = true) 
		public List<Task> getTasksByActivity(Long activityId){
			return taskRepository.findTaskByActivity_ActivityID(activityId); // automatically does SELECT * FROM task WHERE activity_id = ?;
		}
	    
	    
	    
	    /**
	     * Fetches all tasks from the DB, with their validationresults, and errors 
	     * in the form of TaskTreeResponseDTOs.
	     * @return List of ProcessTreeNodeResponseDTO for each process in the DB.
	     */
	    @Transactional(readOnly = true)
	    public List<TaskTreeResponseDTO> GetTaskTreeNodes(){
	    	try {
	    		
	    		// 1. Busca todas as tasks da base de dados
	    		 List<Task> tasks = this.taskRepository.findAll();
	    	        
	    		 
	    		// 2. Mapeia cada task para o seu DTO correspondente
	    	        return tasks.stream().map(task -> {
	    	        	
	    	        	// 3. Para cada task, processamos os seus TaskValidationResults:
	    	            // - Ordenamos por timestamp decrescente (mais recente primeiro)
	    	            // - Limitamos a 3 (últimos 3 resultados)
	    	        	
	    	            List<TaskResultResponseDTO> results = task.getResults().stream() 
	    	                .sorted(Comparator.comparing(TaskValidationResult::getTimestamp).reversed())  
	    	                .limit(3)
	    	                .map(result -> {
	    	                	
	    	                	 // 4. Se o resultado falhou (status == FAIL) e tiver erros associados,
	    	                    // criamos o DTO do erro.
	    	                    TaskErrorResponseDTO errorDTO = null;
	    	                    if (result.getStatus() == TaskStatus.FAIL && result.getTaskErrors() != null && !result.getTaskErrors().isEmpty()) {
	    	                        TaskError error = result.getTaskErrors().get(0); // pegar o primeiro erro
	    	                        errorDTO = new TaskErrorResponseDTO(
	    	                            error.getTaskErrorID(),
	    	                            error.getTimestamp(),
	    	                            error.getErrorDescription()
	    	                        );
	    	                    }

	    	                 // 5. Criamos o DTO do resultado da task, com os dados relevantes
	    	                    return new TaskResultResponseDTO(
	    	                        result.getTaskValidationResultID(),  // ID do resultado
	    	                        task.getTaskName(),                  // Nome da task (repetido em todos os resultados, para contexto)
	    	                        result.getStatus(),                  // Status do resultado
	    	                        result.getTimestamp(),               // Timestamp do resultado
	    	                        errorDTO                             // DTO do erro (pode ser null)
	    	                    );
	    	                })
	    	                .collect(Collectors.toList());

	    	            // 6. Finalmente, criamos o DTO principal da Task com os 3 resultados
	    	            return new TaskTreeResponseDTO(
	    	                task.getTaskID(),      // ID da task
	    	                task.getTaskName(),    // Nome da task
	    	                task.getTaskStatus(),  // Status da task (podes mudar isto para usar o status mais recente dos resultados, se quiseres)
	    	                results                // Lista dos 3 últimos resultados
	    	            );

	    	        }).collect(Collectors.toList());  // 7. Colecionamos todos os DTOs das tasks numa lista
	    	        
	    	        
	    	}catch(RuntimeException e) {
	    		log.error("something went wrong at getTaskTreeNodes method : {}", e.getMessage());
	    		throw e;
	    	}
	   
	    }

}
