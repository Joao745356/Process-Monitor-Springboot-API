package com.bpmonitor.DTOs;
import com.bpmonitor.enums.EDPRLocal;
import com.bpmonitor.enums.OperationalStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSystemDTO {

	
	private String name;
	private String systemComponent;
	private EDPRLocal local;
	private OperationalStatus status;
    private String description;
    

}
