package com.bpmonitor.DTOs.response;

import com.bpmonitor.enums.EDPRLocal;
import com.bpmonitor.enums.OperationalStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/**
 * this DTO represents an EDPRSystem without loading
 * the tasks in order to avoid the overload of data.
 * @author joao7
 *
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemTreeNodeDTO{	
	private Long SystemID;
	private EDPRLocal local;
	private String systemName;
	private String systemComponent;
	private OperationalStatus status;
}
