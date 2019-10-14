package de.ffm.rka.rkareddit.exception;

public class UserAuthenticationLostException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param message
	 */
	public UserAuthenticationLostException(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param rootException
	 */
	public UserAuthenticationLostException(String message, Throwable rootException) {
		super(message, rootException);
	}
}
