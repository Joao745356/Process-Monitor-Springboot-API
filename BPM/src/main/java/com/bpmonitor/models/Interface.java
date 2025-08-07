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
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.bpmonitor.enums.OperationalStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents the bridge between two systems.
 * @author joao7
 *
 */
@Entity
@Table(name = "INTERFACES")
@Getter @Setter //lombok creates getters and setters for this class 
@NoArgsConstructor //no args constructor for JPA, it instanciates objects then reads from DB into them lazy loading.
@ToString
public class Interface {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq_gen")
	@SequenceGenerator(name = "global_seq_gen", sequenceName = "GLOBAL_ID_SEQ", allocationSize = 1)
	@Column(name = "INTERFACE_ID")
	private Long InterfaceID;
	
	@Column(name = "INTERFACE_NAME", nullable = false, length = 200)
	private String edprInterfaceName;  // Represents the name of the System
	
	@Column(name = "INTERFACE_GOAL", length = 2000)
	private String edprInterfaceGoal;  // User defined and not important for functionality
	
	@Column(name = "INTERFACE_TECHNICAL_DATA", length = 200)
	private String edprInterfaceTechnicalData;  // User defined and not important for functionality
	
	@OneToOne
	@JoinColumn(name = "SYSTEM_ID_ORIGEM", nullable = false)
	private System origin;  // what system do I come from
	
	@OneToOne
	@JoinColumn(name = "SYSTEM_ID_DESTINO", nullable = false)
    private System destination; // what system do I go to
	
	@Enumerated(EnumType.STRING)
	@Column(name = "INTERFACE_STATUS", nullable = false)
    private OperationalStatus currentStatus; // Represents whether a system is up, down or compromised
	
	/*
	@OneToMany(mappedBy = "Interface", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JsonManagedReference
	private List<RelationshipActivityInterface> activities;*/
	
	@OneToMany(mappedBy = "system", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<Task> tasks = new ArrayList<>();
	
	public Interface(String intName,
			String intGoal,
			String intTechData,
			System origin,
			System destination,
			OperationalStatus currentStatus,
			List<Task> tasks
			) {
		this.edprInterfaceName = intName;
		this.edprInterfaceGoal = intGoal;
		this.edprInterfaceTechnicalData = intTechData;
		this.origin = origin;
		this.destination = destination;
		this.currentStatus = currentStatus;
		this.tasks = tasks;
		
	}
	
}
