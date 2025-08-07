package com.bpmonitor.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.bpmonitor.enums.TaskStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents the result of whether a Task was successfully executed or not.
 * This class holds the status of the task's execution, which can be one of three states:
 * UNRUN, SUCCESS, or FAIL.
 * 
 * @author joao7
 *
 */

@Entity
@Table(name = "TASK_RESULT")
@Getter @Setter //lombok creates getters and setters for this class 
@NoArgsConstructor //no args constructor for JPA, it instanciates objects then reads from DB into them lazy loading.
@ToString
public class TaskValidationResult {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq_gen")
	@SequenceGenerator(name = "global_seq_gen", sequenceName = "GLOBAL_ID_SEQ", allocationSize = 1) 
	@Column(name = "TASK_RESULT_ID")
	private Long taskValidationResultID;
	
	@ManyToOne // Many taskResults can belong to one task
	@JoinColumn(name = "TASK_ID") // Foreign key in BPM_SUBPROCESS
	private Task task;

    public Task task() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    // The status of the task, representing whether it was run or not, and the result of that execution.
	@Enumerated(EnumType.STRING)
	@Column(name = "RESULT")
    private TaskStatus status; // represents UNRUN ( task hasn't been run yet ), SUCCESS, FAIL.
	
	@Column(name = "DATE_OF_EXECUTION")
    private LocalDateTime timestamp;
	
	@Column(name = "TASK_RESULT_DESCRIPTION", length = 500)
    private String resultDescription; // for error details.
	
	
	@OneToMany(mappedBy = "taskValidationResult", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TaskError> taskErrors = new ArrayList<>();
	
	public TaskValidationResult(
			Task task,
			TaskStatus status,
			LocalDateTime timestamp,
			String description) {
		this.task = task;
		this.status = status;
		this.timestamp = timestamp;
		this.resultDescription = description;
	}
    
   
}

