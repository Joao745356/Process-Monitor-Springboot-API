package com.bpmonitor.DTOs.response;

import com.bpmonitor.models.Task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor
public class TaskResponse {

	private boolean success;
    private String message;
    private Task task; 

    
}
