package com.bpmonitor.models;

import java.util.List;
import java.util.ArrayList;

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
 * the process class represents an activity.
 * @author joao7
 *
 */

@Entity
@Table(name = "ACTIVITY")
@Getter @Setter //lombok creates getters and setters for this class 
@NoArgsConstructor //no args constructor for JPA, it instanciates objects then reads from DB into them lazy loading.
@ToString
public class Activity { // if ANY of my activies are down I am down.
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq_gen")
	@SequenceGenerator(name = "global_seq_gen", sequenceName = "GLOBAL_ID_SEQ", allocationSize = 1)
	@Column(name = "ACTIVITY_ID")
	private Long activityID; 
	
	@Column(name = "ACTIVITY_NAME", nullable = false, length = 100)
	private String activityName;
	 
	@Column(name = "ACTIVITY_DESCRIPTION", length = 1000)
	private String activityDescription;
	
	@ManyToOne // Many activities can belong to one subprocess
	@JoinColumn(name = "SUBPROCESS_ID") // Foreign key in BPM_Activity
	@JsonBackReference
	private Subprocess subprocess; //subprocess I belong to

	public Subprocess subprocess() {
		return subprocess;
	}

	public void setSubprocess(Subprocess subprocess) {
		this.subprocess = subprocess;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "ACTIVITY_STATUS", nullable = false)
	private OperationalStatus activityStatus;
	
	// One-to-many relationship with Subprocess
    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Task> tasks = new ArrayList<>();
	
	 /**
	  * cascadetype.all = all changes to activity (save, update, delete)will cascade to 
	  * relationshipActivitySystem.
	  * 
	  * orphanRemoval = true - if a relationshipActivitySystem is removed from this list, it'll be removed from the Database.
	  
	 @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	 @JsonManagedReference
	 private List<RelationshipActivitySystem> relationshipActivitySystems;
	 
	 @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	 @JsonManagedReference
	 private List<RelationshipActivityInterface> relationshipActivityInterfaces;*/
	 
	 
	 //array de errors das minhas tasks para manter o meu status
	
	 
	public Activity( String actName, 
			String actDescription, Subprocess subproc, 
			OperationalStatus actStatus ) {
		this.activityName = actName;
		this.activityDescription = actDescription;
		this.subprocess = subproc;
		this.activityStatus = actStatus;
		//this.relationshipActivitySystems = (systems != null) ? systems : new ArrayList<>(); // both begind empty if constructor passes null
	    //this.relationshipActivityInterfaces = (interfaces != null) ? interfaces : new ArrayList<>();
		
	}


}
