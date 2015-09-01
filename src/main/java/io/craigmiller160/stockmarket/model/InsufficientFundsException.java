package io.craigmiller160.stockmarket.model;

/**
 * Exception that is thrown by when there is not enough funds available
 * to make a transaction.
 * 
 * @author craig
 * @version 2.0
 */
public class InsufficientFundsException extends Exception {

	/**
	 * SerialVersionUID for serialization support.
	 */
	private static final long serialVersionUID = -6225359861482291469L;

	/**
	 * A new exception with no detail message.
	 */
	public InsufficientFundsException() {
		
	}

	/**
	 * A new exception with a detail message.
	 * 
	 * @param message the detail message.
	 */
	public InsufficientFundsException(String message) {
		super(message);
	}

	/**
	 * A new exception with the specified cause and the detail message
	 * from that cause.
	 * 
	 * @param throwable the cause of the exception.
	 */
	public InsufficientFundsException(Throwable throwable) {
		super(throwable);
		
	}

	/**
	 * A new exception with a specified cause and a user-defined detail message.
	 * 
	 * @param message the detail message.
	 * @param throwable the cause of the exception.
	 */
	public InsufficientFundsException(String message, Throwable throwable) {
		super(message, throwable);
	}

	/**
	 * A new exception with the specified detail message, cause, and 
	 * suppression enabled or disabled and writeable stack trace enabled
	 * or disabled.
	 * 
	 * @param message the detail message.
	 * @param throwable the cause of the exception.
	 * @param enableSuppression suppression enabled or disabled.
	 * @param writeableStackTrace writeable stack trace enabled or disabled.
	 */
	public InsufficientFundsException(String message, Throwable throwable,
			boolean enableSuppression, boolean writeableStackTrace) {
		super(message, throwable, enableSuppression, writeableStackTrace);
	}

}
