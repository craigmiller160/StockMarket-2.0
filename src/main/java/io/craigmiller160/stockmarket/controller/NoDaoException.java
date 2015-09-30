package io.craigmiller160.stockmarket.controller;

/**
 * An exception that is thrown in the controller if
 * the DAO class has not been properly set.
 * 
 * @author craig
 * @version 2.3
 */
public class NoDaoException extends Exception {

	/**
	 * The serialVersionUID for this class.
	 */
	private static final long serialVersionUID = 7492084728837863526L;

	/**
	 * Create a new exception.
	 */
	public NoDaoException() {
		
	}

	/**
	 * Create a new exception with the specified message.
	 * 
	 * @param message the message for the exception.
	 */
	public NoDaoException(String message) {
		super(message);
	}

	/**
	 * Create a new exception with the specified cause.
	 * 
	 * @param cause the cause for the exception.
	 */
	public NoDaoException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create a new exception with the specified cause
	 * and message.
	 * 
	 * @param message the message for the exception.
	 * @param cause the cause for the exception.
	 */
	public NoDaoException(String message, Throwable cause) {
		super(message, cause);
	}

}
