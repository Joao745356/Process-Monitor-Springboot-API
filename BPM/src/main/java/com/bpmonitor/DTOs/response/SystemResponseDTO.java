package com.bpmonitor.DTOs.response;

import java.util.List;

import com.bpmonitor.enums.EDPRLocal;
import com.bpmonitor.enums.OperationalStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * this dto represents a full 
 * @author joao7
 *
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemResponseDTO {

	private Long SYstemId;
	private EDPRLocal local;
	private String systemName;
	private String systemComponent;
	private String systemDescription;
	private OperationalStatus status;
	private List<TaskResponseDTO> tasks;
}
