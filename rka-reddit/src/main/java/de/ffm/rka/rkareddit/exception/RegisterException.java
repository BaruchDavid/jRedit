package de.ffm.rka.rkareddit.exception;

public class RegisterException extends Exception{

	private static final long serialVersionUID = 1090221583695997602L;

	final String message;

	public RegisterException(String message) {
		super(message);
		this.message = message;
	}	
}
