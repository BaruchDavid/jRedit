package de.ffm.rka.rkareddit.exception;

public class ServiceException extends Exception{

	String message;

	public ServiceException(String message) {
		super(message);
		this.message = message;
	}	
}
