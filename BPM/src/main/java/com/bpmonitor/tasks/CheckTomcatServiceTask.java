package com.bpmonitor.tasks;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.bpmonitor.enums.TaskRecurrence;
import com.bpmonitor.enums.TaskStatus;
import com.bpmonitor.enums.TaskType;
import com.bpmonitor.models.Activity;
import com.bpmonitor.models.Interface;
import com.bpmonitor.models.System;
import com.bpmonitor.models.Task;
import com.bpmonitor.models.TaskValidationResult;
import com.fasterxml.jackson.databind.JsonNode;


/**
 * This task checks if a Tomcat service is up and running by sending an HTTP request to the Tomcat server.
 */
public class CheckTomcatServiceTask extends Task {

    private String tomcatUrl; // URL of the Tomcat server (e.g., "http://localhost:8080")

    public CheckTomcatServiceTask( String taskName, String taskDescription,
    		TaskStatus status, Activity activity, TaskType type, TaskRecurrence recurrence,
    		String workload) {
        
    	super( taskName, taskDescription, type, status, activity, recurrence, workload );
        
        JsonNode workloadJson = super.getParsedWorkload(); // crio um jsonNode
		String tomcatUrl = workloadJson.get("TOMCATURL").asText();  // Tiro o valor de "URL" no meu json para uma variavel
		
        this.tomcatUrl = tomcatUrl;
    }
    


    @Override
    public void run() {
        if (tomcatUrl == null) {
            return;
        }
        execute();
    }

   
    @Override
	public TaskValidationResult execute() {
        
    	if (tomcatUrl == null) {
    	    return null;
    	}
    	try {
        
    		URL url = new URL(tomcatUrl);
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false); // We'll handle redirects manually
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            
    		// Consider both success (200) and redirect (3xx) as valid responses
            if (responseCode == HttpURLConnection.HTTP_OK || 
                (responseCode >= HttpURLConnection.HTTP_MOVED_PERM && 
                 responseCode <= HttpURLConnection.HTTP_SEE_OTHER)) {
               
                
                // If you want to follow redirects explicitly:
                if (responseCode >= 300 && responseCode <= 399) {
                    String newUrl = connection.getHeaderField("Location");
                }
            } else {
             
                    
            }

        } catch (IOException e) {
          
        }
    	return null;
    }

   

   
}