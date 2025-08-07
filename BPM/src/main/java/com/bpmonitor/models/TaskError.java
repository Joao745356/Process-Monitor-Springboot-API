package com.bpmonitor.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TASK_ERROR")
@Getter @Setter
@NoArgsConstructor // for jpa
@ToString
public class TaskError {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq_gen")
	@SequenceGenerator(name = "global_seq_gen", sequenceName = "GLOBAL_ID_SEQ", allocationSize = 1)
	@Column(name = "TASK_ERROR_ID")
	private Long taskErrorID;
	
	@ManyToOne
	@JoinColumn(name = "TASK_RESULT_ID", nullable = false)
	private TaskValidationResult taskValidationResult;
	
	@Column(name = "DATE_OF_EXECUTION")
    private LocalDateTime timestamp;
	
	@Column(name = "DESCRIPTION", nullable = false, length = 1000)
	private String errorDescription;
	
	@Column(name = "WORKLOAD_JSON", nullable = false, length = 4000)
	private String workload;
	
	public TaskError(
			TaskValidationResult taskResult,
			LocalDateTime time, 
			String errorDescription,
			String workload) {
		this.taskValidationResult = taskResult;
		this.timestamp = time;
		this.errorDescription = errorDescription;
		this.workload = workload;
	}
}
