package com.bpmonitor.DTOs.response;

import java.util.List;

import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.enums.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class SubprocessTreeNodeResponseDTO {

	
	private Long id;
    private String name;
    private OperationalStatus operationalStatus;
    private TaskStatus taskStatus;
    private List<SubprocessTreeNodeResponseDTO> children;  // These are my child nodes

    
    
    public SubprocessTreeNodeResponseDTO(Long id,
    		String name,
    		OperationalStatus status,
    		List<SubprocessTreeNodeResponseDTO> children) {
    	this.id = id;
    	this.name = name;
    	this.operationalStatus = status;
    	this.taskStatus = TaskStatus.UNRUN; // for nodes that don't care for taskStatus
    	this.children = children;
    	
    }
    
    public SubprocessTreeNodeResponseDTO(Long id,
    		String name,
    		TaskStatus status,
    		List<SubprocessTreeNodeResponseDTO> children) {
    	this.id = id;
    	this.name = name;
    	this.operationalStatus = OperationalStatus.UNRUN; // for nodes that don't care for operationalStatus
    	this.taskStatus = status; 
    	this.children = children;
    	
    }
}
