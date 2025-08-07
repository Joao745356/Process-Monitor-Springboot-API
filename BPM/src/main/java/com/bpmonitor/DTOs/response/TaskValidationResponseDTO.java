package com.bpmonitor.DTOs.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bpmonitor.enums.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskValidationResponseDTO {
	private Long taskValidationResultID;

    private TaskStatus status;

    private LocalDateTime timestamp;

    private String resultDescription;

    private List<TaskErrorResponseDTO> taskErrors = new ArrayList<>();
}
