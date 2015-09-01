package io.craigmiller160.stockmarket.stock;

/**
 * A checked exception to be thrown if a program attempts to 
 * subtract more shares from an <tt>OwnedStock</tt> than are
 * available to be removed.
 * 
 * @author craig
 * @version 2.0
 */
public class InsufficientSharesException extends IllegalStateException {

	/**
	 * serialVersionUID for serialization support.
	 */
	private static final long serialVersionUID = 186483214642399510L;

	/**
	 * Create a new exception with no specified message.
	 */
	public InsufficientSharesException() {
		super();
	}

	/**
	 * Create a new exception with the specified message.
	 * 
	 * @param message the message for the exception.
	 */
	public InsufficientSharesException(String message) {
		super(message);
	}

	/**
	 * Create a new exception with the specified cause.
	 * 
	 * @param cause the cause of the exception.
	 */
	public InsufficientSharesException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create a new exception with the specified message
	 * and cause.
	 * 
	 * @param message the message for the exception.
	 * @param cause the cause of the exception.
	 */
	public InsufficientSharesException(String message, Throwable cause) {
		super(message, cause);
	}

}
