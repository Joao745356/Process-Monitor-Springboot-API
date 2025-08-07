package com.bpmonitor.DTOs;



import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.models.Process;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubprocessDTO {

	
	private String subprocessName;
	
	private Long processId; //I belong to this process

	private OperationalStatus subprocessStatus;
	
	
}
