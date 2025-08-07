package com.bpmonitor.DTOs.response;

import java.util.ArrayList;
import java.util.List;

import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.models.Subprocess;
import com.bpmonitor.models.Task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponseDTO {


	private Long activityID;		
	private String activityName;	 
	private String activityDescription;		
	private OperationalStatus activityStatus;
	
}
