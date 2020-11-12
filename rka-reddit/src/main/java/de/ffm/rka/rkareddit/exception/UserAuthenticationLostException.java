package de.ffm.rka.rkareddit.exception;

public class UserAuthenticationLostException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param message for exception
	 */
	public UserAuthenticationLostException(String message) {
		super(message);
	}
	
	/**
	 * @param message message for exception
	 * @param rootException message for exception
	 */
	public UserAuthenticationLostException(String message, Throwable rootException) {
		super(message, rootException);
	}
}
