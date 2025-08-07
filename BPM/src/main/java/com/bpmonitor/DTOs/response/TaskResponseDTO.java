package com.bpmonitor.DTOs.response;



import com.bpmonitor.enums.TaskRecurrence;
import com.bpmonitor.enums.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/**
 * represents a task, with it's status and all data without results.
 * @author joao7
 *
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {

	private Long taskID; 
		
	private String taskName;
		
	private String taskDescription;
	
	private String workload; 
	
	private Long activityId;
		
	private TaskRecurrence recurrence;

	private TaskStatus taskStatus;
	      
    private Long SystemId; 
	
    private Long InterfaceId;
	
}
