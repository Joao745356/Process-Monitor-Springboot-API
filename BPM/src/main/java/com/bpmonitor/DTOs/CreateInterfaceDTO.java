package com.bpmonitor.DTOs;

import java.util.List;

import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.models.Task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateInterfaceDTO {

	private String InterfaceName;  // Represents the name of the System
	

	private String InterfaceGoal;  // User defined and not important for functionality
	

	private String InterfaceTechnicalData;  // User defined and not important for functionality
	

	private Long originId;  // what system do I come from
	

    private Long destinationId; // what system do I go to

    private OperationalStatus currentStatus; // Represents whether a system is up, down or compromised 
	

}
