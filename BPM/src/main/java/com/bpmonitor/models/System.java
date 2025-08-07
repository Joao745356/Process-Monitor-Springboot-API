	package com.bpmonitor.models;

import com.bpmonitor.enums.EDPRLocal;
import com.bpmonitor.enums.OperationalStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;



/**
 * represents a system that interacts with EDP.
 * has a status that can be down or up.
 * 
 * A system is up if all it's tasks are successful, otherwise it's down.
 * @author joao7
 *
 */

@Entity
@Table(name = "BPM_SYSTEM")
@Getter @Setter
@NoArgsConstructor // for jpa
@ToString
public class System {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq_gen")
	@SequenceGenerator(name = "global_seq_gen", sequenceName = "GLOBAL_ID_SEQ", allocationSize = 1)
	@Column(name = "SYSTEM_ID")
	private Long SystemID; // ID of this system
	
	@Enumerated(EnumType.STRING)
	@Column(name="SYSTEM_LOCAL", nullable = false, length = 200)
	private EDPRLocal local;
	
	@Column(name = "SYSTEM_NAME", nullable = false, length = 200)
	protected String systemName;  // Represents the name of the System
	
	@Column(name = "SYSTEM_COMPONENT", nullable = false, length = 200)
	protected String systemComponent;  // Represents the name of the System component
	
	@Column(name = "SYSTEM_DESCRIPTION", nullable = false, length = 100)
    protected String systemDescription;  // Brief description of the System
	
	@Enumerated(EnumType.STRING)
	@Column(name ="SYSTEM_STATUS", nullable = false)
    protected OperationalStatus currentStatus; // Represents whether a system is up, down or compromised
    
	@OneToMany(mappedBy = "system", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<Task> tasks = new ArrayList<>();
	
	public System(
			String sysName,
			EDPRLocal local,
			String sysComponent,
			String sysDescription,
			OperationalStatus currentStatus,
			List<Task> tasks
			) {
		this.systemName = sysName;
		this.local = local;
		this.systemComponent = sysComponent;
		this.systemDescription = sysDescription;
		this.currentStatus = currentStatus;
		this.tasks = tasks;
	}
    

}
