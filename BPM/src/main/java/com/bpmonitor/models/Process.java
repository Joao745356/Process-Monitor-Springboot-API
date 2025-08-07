package com.bpmonitor.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.bpmonitor.enums.OperationalStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * the process class represents a process.
 * 
 * @author joao7
 *
 */

@Entity
@Table(name = "PROCESS")
@Getter
@Setter
@NoArgsConstructor 
@ToString
public class Process {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq_gen")
	@SequenceGenerator(name = "global_seq_gen", sequenceName = "GLOBAL_ID_SEQ", allocationSize = 1)
	@Column(name = "PROCESS_ID")
	private Long processID;

	@Column(name = "PROCESS_NAME", nullable = false, length = 80)
	private String processName;

	@Enumerated(EnumType.STRING)
	@Column(name = "PROCESS_STATUS", nullable = false)
	private OperationalStatus processStatus;

	// One-to-many relationship with Subprocess
	@OneToMany(mappedBy = "process", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<Subprocess> subprocesses = new ArrayList<>();

	public Process(String procName, OperationalStatus currentStatus) {
		this.processName = procName;
		this.processStatus = currentStatus;
	}

}
