package com.bpmonitor.DTOs;

import java.util.List;

import com.bpmonitor.enums.EDPRLocal;
import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.models.Task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateActivityDTO {

	private String name;
    private String description;
    private Long  subprocessId; 
}
