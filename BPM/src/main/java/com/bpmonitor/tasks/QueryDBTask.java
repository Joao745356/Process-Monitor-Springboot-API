package com.bpmonitor.tasks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

import com.bpmonitor.enums.*;
import com.bpmonitor.models.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;



/**
 * This task checks if a database query can be executed successfully.
 * It connects to the database, executes the query, and verifies the result.
 */
@Slf4j
@Entity
@DiscriminatorValue("QUERYDBTASK")
public class QueryDBTask extends Task{
	

	@Transient
    private String jdbcUrl;      // JDBC connection URL (e.g., "jdbc:mysql://localhost:3306/mydb")
	@Transient
	private String username;     // Database username
	@Transient
	private String password;     // Database password
	@Transient
	private String query;        // SQL query to execute
    

	@PostLoad
	public void extractWorkload() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(this.getWorkload());
            this.jdbcUrl = node.get("BDURL").asText();
            this.username = node.get("USERNAME").asText();
            this.password = node.get("PASSWORD").asText();
            this.query = node.get("QUERY").asText();
        } catch (Exception e) {
            // handle or log
        }
    }
	
	public QueryDBTask(
	        String taskName,
	        String taskDescription,
	        TaskStatus status,
	        Activity activity,
	        TaskType type,
	        TaskRecurrence recurrence,
	        String workload
	) {
	    super(taskName, taskDescription, type, status, activity, recurrence, workload);
	    extractWorkload(); 
	}


	@Override
	public void run() {
	     execute();
		
	}

	@Override
	public TaskValidationResult execute() {
		
		String description = "";
		
		if (jdbcUrl == null || username == null || password == null || query == null) {
	        extractWorkload(); // Safety fallback
	    }
		
		Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            // Step 1: Connect to a database 
            connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Step 2: Prepare and execute the query
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            //if I get here all is well
            this.setTaskStatus(TaskStatus.SUCCESS);
            description = "Query executed successfully: " + query;
                      

            return new TaskValidationResult(this, this.getTaskStatus(), LocalDateTime.now(), description);

        } catch (SQLException e) {
        	this.setTaskStatus(TaskStatus.FAIL);
            String error = "Database query failed: " + e.getMessage();
            log.error(error);
            return new TaskValidationResult(this, this.getTaskStatus(), LocalDateTime.now(), error);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                log.warn("Error closing DB resources: " + e.getMessage());
            }
        }
    }


	
	 @Override
	    public String toString() {
	        return "CheckDatabaseQueryTask{" +
	                "taskName='" + getTaskName() + '\'' +
	                ", jdbcUrl='" + jdbcUrl + '\'' +
	                ", query='" + query + '\'' +
	                '}';
	    }

	   
	

}
