package com.bpmonitor.DTOs.response;

import java.util.List;

import com.bpmonitor.enums.OperationalStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/**
 * this DTO represents an EDPRInterface while loading
 * the tasks.
 * @author joao7
 *
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterfaceResponseDTO {

	
	private Long InterfaceID;
	private String InterfaceName;
	private String InterfaceGoal;
	private String InterfaceTechnicalData;
	private Long originId;
	private Long destinationId;
	private OperationalStatus status;
	private List<TaskResponseDTO> tasks;
}
