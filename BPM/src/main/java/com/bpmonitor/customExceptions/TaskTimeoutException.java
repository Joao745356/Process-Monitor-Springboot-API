package com.bpmonitor.customExceptions;

public class TaskTimeoutException extends RuntimeException {
	
	//if a task takes too long to complete this exception could be used. 
	public TaskTimeoutException(String message) {
        super(message);
    }
}
