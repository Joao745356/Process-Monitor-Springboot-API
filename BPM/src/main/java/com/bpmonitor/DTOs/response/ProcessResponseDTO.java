package com.bpmonitor.DTOs.response;

import java.util.ArrayList;
import java.util.List;

import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.models.Subprocess;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ProcessResponseDTO {
	
	private Long processID;	
	private String processName;
	private OperationalStatus processStatus;
    private List<SubprocessResponseDTO> subprocesses = new ArrayList<>();
    
}
