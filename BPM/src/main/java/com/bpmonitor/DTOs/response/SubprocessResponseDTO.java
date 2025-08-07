package com.bpmonitor.DTOs.response;

import java.util.ArrayList;
import java.util.List;

import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.models.Activity;
import com.bpmonitor.models.Process;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubprocessResponseDTO {
	
	private Long subprocessID;		
	private String subprocessName;	
	private OperationalStatus subprocessStatus;	
    private List<ActivityResponseDTO> activities = new ArrayList<>();
	
}
