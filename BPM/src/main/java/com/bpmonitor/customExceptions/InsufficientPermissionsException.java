package com.bpmonitor.customExceptions;

public class InsufficientPermissionsException extends RuntimeException {
	
	//for when a user tries to do something they don't have permissions to
	//for example, deleting a subprocess. 
    public InsufficientPermissionsException(String message) {
        super(message);
    }
}