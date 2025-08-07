package com.bpmonitor.eventListeners;

import javax.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.bpmonitor.models.Task;
import com.bpmonitor.models.TaskValidationResult;
import com.bpmonitor.services.ActivityService;
import com.bpmonitor.services.InterfaceService;
import com.bpmonitor.services.ProcessService;
import com.bpmonitor.services.SubprocessService;
import com.bpmonitor.services.SystemService;
import com.bpmonitor.services.TaskService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskStatusListenerService {

	
	private final TaskService taskService;
    private final ActivityService activityService;
    private final SubprocessService subprocessService;
    private final ProcessService processService;
    private final SystemService systemService;
    private final InterfaceService interfaceService;

    // Constructor injection of services
    public TaskStatusListenerService(TaskService taskService, ActivityService activityService,
                                     SubprocessService subprocessService, ProcessService processService,
                                     SystemService systemService, InterfaceService interfaceService) {
        this.taskService = taskService;
        this.activityService = activityService;
        this.subprocessService = subprocessService;
        this.processService = processService;
        this.systemService = systemService;
        this.interfaceService = interfaceService;
    }

    @EventListener
    @Async("taskExecutor") // Ensures the event is handled asynchronously
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskStatusChanged(TaskStatusChangedEvent event) {
    	
    	
        Task task = event.getTask();
        TaskValidationResult result = event.getResult(); 
        
        log.debug("I have been called and am holding the task ID: {} and have the result for validation : {}", task.getTaskID(), result.getStatus());

        // Perform asynchronous updates to the related entities
        taskService.updateTaskStatusBasedOnResult(task, result); // this one isn't being saved right! added save

        // Update activity status based on the task
        activityService.updateActivityStatusBasedOnTasks(
        		task.getActivity().getActivityID());

        // Update subprocess status based on the activity
        subprocessService.updateSubprocessStatusBasedOnActivityStatus(
        		task.getActivity().getSubprocess().getSubprocessID());
        
        processService.updateProcessStatusBasedOnSubprocesses(
        		task.getActivity().getSubprocess().getProcess().getProcessID());

        if(task.getSystem() != null) {
        // update system status based on it's tasks
        systemService.updateSystemStatusBasedOnTasks(task.getSystem().getSystemID());
        }
        
        if(task.getInterface() != null) {
        // same for interfaces
        interfaceService.updateInterfaceStatusBasedOnTasks(task.getInterface().getInterfaceID());
        }
    }
}
