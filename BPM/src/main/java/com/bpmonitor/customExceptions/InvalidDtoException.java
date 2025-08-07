package com.bpmonitor.customExceptions;

public class InvalidDtoException extends RuntimeException {
   
	public InvalidDtoException(String message) {
        super(message);
    }
}
