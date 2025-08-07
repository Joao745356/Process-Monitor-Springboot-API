package com.bpmonitor.DTOs;




import com.bpmonitor.enums.TaskRecurrence;
import com.bpmonitor.enums.TaskStatus;
import com.bpmonitor.enums.TaskType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor
public class CreateTaskDTO {

	private String taskName;
	private String taskDescription;
	private TaskType taskType;
	private TaskStatus taskStatus;
	private Long activityID; // I belong to this activity
	private TaskRecurrence recurrence;
	private Long SystemID;
	private Long InterfaceID;
	private String workload;
	
	
	
	public String getWorkloadAsString() {
	    try {
	        return new ObjectMapper().writeValueAsString(this.workload);
	    } catch (Exception e) {
	        throw new RuntimeException("Invalid workload format", e);
	    }
	}
	
	@Override
	public String toString() {
	    return "CreateTaskDTO{" +
	            "taskName='" + taskName + '\'' +
	            ", taskDescription='" + taskDescription + '\'' +
	            ", taskType=" + taskType +
	            ", taskStatus=" + taskStatus +
	            ", activityID=" + activityID +
	            ", recurrence=" + recurrence +
	            ", systemID=" + SystemID +
	            ", InterfaceID=" + InterfaceID +
	            ", workload=" + workload +
	            '}';
	}
	
}