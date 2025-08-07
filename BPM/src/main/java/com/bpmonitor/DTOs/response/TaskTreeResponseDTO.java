package com.bpmonitor.DTOs.response;

import java.time.LocalDateTime;
import java.util.List;

import com.bpmonitor.enums.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




/**
 * this dto represents the Dtos for the TaskTreePannel. 
 * vai ficar na responsabilidade do controlador de tasks!
 * @author joao7
 *
 */
@Getter @Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class TaskTreeResponseDTO {
	  	private Long id;
	    private String name; // vem da Task
	    private TaskStatus status; // vem do TaskValidationResult
	    private List<TaskResultResponseDTO> results;
}
