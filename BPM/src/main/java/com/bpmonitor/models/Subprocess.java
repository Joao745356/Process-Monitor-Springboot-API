package com.bpmonitor.models;


import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.bpmonitor.enums.OperationalStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * the process class represents a subprocess.
 * @author joao7
 *
 */

@Entity
@Table(name = "SUBPROCESS")
@Getter @Setter
@NoArgsConstructor // for jpa
@ToString
public class Subprocess {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq_gen")
	@SequenceGenerator(name = "global_seq_gen", sequenceName = "GLOBAL_ID_SEQ", allocationSize = 1)
	@Column(name = "SUBPROCESS_ID")
	private Long subprocessID;
	
	@Column(name = "SUBPROCESS_NAME", nullable = false, length = 80)
	private String subprocessName;
	
	@ManyToOne // Many subproc can belong to one process
	@JoinColumn(name = "PROCESS_ID") // Foreign key in SUBPROCESS
	@JsonBackReference
	private Process process; //I belong to this process

	@Enumerated(EnumType.STRING)
	@Column(name ="SUBPROCESS_STATUS", nullable = false)
	private OperationalStatus subprocessStatus;
	
	// One-to-many relationship with Subprocess
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "subprocess")
    @JsonManagedReference
    private List<Activity> activities = new ArrayList<>();

    public Collection<Activity> activities() {
        return activities;
    }


    public Subprocess(
			String subprocessName,
			Process process,
			OperationalStatus currentStatus) 
	{
		this.subprocessName = subprocessName;
		this.process = process;
		this.subprocessStatus = currentStatus;
		
	}
}
