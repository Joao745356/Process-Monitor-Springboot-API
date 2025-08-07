package com.bpmonitor.tasks;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.Transient;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import com.bpmonitor.enums.*;
import com.bpmonitor.models.*;
import com.bpmonitor.services.TaskValidationResultService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * this task is responsible for pinging ANY server. 
 */
@Slf4j
@NoArgsConstructor
@Entity
@DiscriminatorValue("PINGSERVERTASK")
public class PingServerTask extends Task {

	

	@Transient
	private String host; //name of the server to ping
	
	@Transient
	int timeout = 5000; //10 seconds
	

	public PingServerTask( 
			String taskName, 
			String taskDescription, 
			TaskStatus status, 
			Activity activity, 
			TaskType type, 
			TaskRecurrence recurrence, 
			String workload	
			) {
			
		super( taskName,
			taskDescription, 
			type, 
			status, 
			activity, 
			recurrence,
			workload
			);
	}
	

	
	@PostLoad
	public void extractWorkload() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(this.getWorkload());
            this.host = node.get("HOST").asText();
        } catch (Exception e) {
            // handle or log
        }
    }


	
	// setters and getters
	public void setHost(String host){this.host = host;}
	
	public String getHost() { return this.host; }

	
	@Override
	public void run() {
		execute();
	}

	@Override
	public TaskValidationResult execute() {
		 
		String description = "";
		
		// safety: if a workload  hasn't been extracted yet, I do it 
	    if (this.host == null) {
	    	log.debug("I entered extractworkload in execute for pingservertask.");
	        extractWorkload();
	    }
	    
	    String cleanHost = host.replaceFirst("^(https?://)", "").replaceAll("/$", "");
	    log.debug("Trying to ping cleaned host: " + cleanHost);
	    
		try {
			
			
			InetAddress inetAddress = InetAddress.getByName(cleanHost);
			boolean success = inetAddress.isReachable(timeout);
			
			if(success) {
				this.setTaskStatus(TaskStatus.SUCCESS);
				description = "pinged host successfully : " + host;
				log.debug("I'm returning a success");
				return new TaskValidationResult(this,this.getTaskStatus(),LocalDateTime.now(),description); 
			}else {
				this.setTaskStatus(TaskStatus.FAIL);
				description = "couldn't reach host : " + host;
				log.debug("I'm returning an error");
				return new TaskValidationResult(this,this.getTaskStatus(),LocalDateTime.now(),description); 
			}
			
		}catch (UnknownHostException e) {
			this.setTaskStatus(TaskStatus.FAIL);
            log.debug("Host not found: " + host);
            return new TaskValidationResult(this,this.getTaskStatus(),
            		LocalDateTime.now(),"I couldn't find the host " + cleanHost); 
        } catch (Exception e) {
        	log.debug("Host not found: " + host);
             super.setTaskStatus(TaskStatus.FAIL);
            e.printStackTrace();
            return new TaskValidationResult(this,this.getTaskStatus(),
            		LocalDateTime.now(),
            		"I couldn't reach this host + " + cleanHost); 
        }
	}

	
	@Override
	public String toString() {
	    return "PingServerTask{" +
	            "taskName='" + getTaskName() + '\'' +
	            ", host='" + host + '\'' +
	            ", timeout=" + timeout +	        
	            '}';
	}
	

}
