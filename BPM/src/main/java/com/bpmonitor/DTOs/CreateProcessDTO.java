package com.bpmonitor.DTOs;


import com.bpmonitor.enums.OperationalStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProcessDTO {

	private String processName;

	private OperationalStatus status = OperationalStatus.UNRUN; // começa sempre como UNRUN
	
	
}
