package com.bpmonitor.DTOs.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskErrorResponseDTO {

	
	private Long id;
	private LocalDateTime timestamp;
	private String description;
	
}
