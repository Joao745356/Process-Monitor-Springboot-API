package com.bpmonitor.eventListeners;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import com.bpmonitor.models.Task;
import com.bpmonitor.models.TaskValidationResult;
import com.bpmonitor.services.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor

/**
 * This event is a simple wrapper around a Task object,
 *  which will be passed whenever a task status changes.
 */
public class TaskStatusChangedEvent {

	
	    private final Task task;
	    private final TaskValidationResult result;

	  	    
}
