package com.bpmonitor.DTOs.response;

import java.time.LocalDateTime;

import com.bpmonitor.enums.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResultResponseDTO {

	
	private Long id;
    private String name;
    private TaskStatus status;
    private LocalDateTime timestamp;
    private TaskErrorResponseDTO error; // might not exist, but if a result has status == FAIL it will exist
}
