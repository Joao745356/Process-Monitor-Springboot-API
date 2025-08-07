package com.bpmonitor.DTOs.response;

import java.util.List;

import com.bpmonitor.enums.OperationalStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * this DTO represents the nodes to send to the frontend when I get a request for 
 * Process -> Subprocess -> Activities and want to present the info that way. 
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessTreeNodeResponseDTO {

	  	private Long id;
	    private String name;
	    private OperationalStatus status;
	    private List<ProcessTreeNodeResponseDTO> children;  // These are my child nodes

	    
}
