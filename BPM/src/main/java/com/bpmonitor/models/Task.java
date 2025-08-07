package com.bpmonitor.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.bpmonitor.enums.*;
import com.bpmonitor.models.System;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Tasks represents validations to either a system or interface. Each task
 * implements specific behavior for execution and validation. String taskName,
 * String taskDescription, EDPRSystem system, Activity activity, TaskRecurrence
 * recurrence, String workload
 */

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TASK_TYPE", discriminatorType = DiscriminatorType.STRING) // esta coluna distingue tasks
																						// umas das outras.
@Table(name = "CONTROL_TASKS")
@Getter
@Setter // lombok creates getters and setters for this class
@NoArgsConstructor // no args constructor for JPA, it instanciates objects then reads from DB into
					// them lazy loading.
@ToString
public class Task implements Runnable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq_gen")
	@SequenceGenerator(name = "global_seq_gen", sequenceName = "GLOBAL_ID_SEQ", allocationSize = 1)
	@Column(name = "TASK_ID")
	private Long taskID;

	@Column(name = "TASK_NAME", nullable = false, length = 100)
	private String taskName;

	@Column(name = "TASK_DESCRIPTION", nullable = true, length = 1000)
	private String taskDescription;

	@Column(name = "WORKLOAD", length = 4000) // Stores JSON as a string
	private String workload; // JSON as a string in the database

	@ManyToOne // Many tasks can belong to one activity
	@JoinColumn(name = "ACTIVITY_ID")
	@JsonBackReference
	private Activity activity; // I belong to this activity

	@Enumerated(EnumType.STRING)
	@Column(name = "RECURRENCE", nullable = false)
	private TaskRecurrence recurrence;

	@Enumerated(EnumType.STRING)
	@Column(name = "TASK_STATUS", nullable = false, length = 20)
	private TaskStatus taskStatus;

	@ManyToOne
	@JoinColumn(name = "SYSTEM_ID", nullable = true)
	@JsonBackReference
	private System system;

	@ManyToOne
	@JoinColumn(name = "INTERFACE_ID", nullable = true)
	@JsonBackReference
	private Interface Interface;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "task")
	@JsonManagedReference
	private List<TaskValidationResult> results = new ArrayList<>();

	public Collection<TaskValidationResult> results() {
		return results;
	}

	/**
	 * Constructor for the Task class, with a system, interface will be null.
	 * 
	 * @param taskID          - ID of the task
	 * @param taskName
	 * @param taskDescription
	 * @param system          - System the task affects.
	 * @param activity        - activity this task belongs to.
	 * @param recurrence      - how often this task should run.
	 * @param workload        - JSON with necessary info for a task to run.
	 */
	public Task(String taskName, String taskDescription, TaskType taskType, TaskStatus taskStatus, Activity activity,
			TaskRecurrence recurrence, String workload) {
		this.taskName = taskName;
		this.taskDescription = taskDescription;
		// this.taskType = taskType;
		this.taskStatus = taskStatus;
		this.activity = activity;
		this.recurrence = recurrence;
		this.workload = workload;
	}


	

	/**
	 * Each task implements run in their own way.
	 */
	@Override
	public void run() {
	};

	/**
	 * code to be run by a task when it's run method is called.
	 */
	public TaskValidationResult execute() {
		TaskValidationResult taskR = new TaskValidationResult();
		return taskR;
	};

	/**
	 * parses the json of workload in the DB.
	 * 
	 * @return
	 */
	public JsonNode getParsedWorkload() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readTree(this.workload);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON workload", e);
		}
	}

	@Override
	public String toString() {
		return "Task{" + "id=" + taskID + ", name='" + taskName + '\'' + ", status=" + taskStatus + '\''
				+ ",taskDescription " + taskDescription + '\'' + ",workload " + workload + "'\'' " +

				'}';
	}
}
