package stockmarket.stock;

/**
 * Exception thrown if a <tt>Stock</tt> instance is unable to download 
 * its attributes because the instance's symbol does not correspond to
 * an actual stock.
 * 
 * @author Craig
 * @version 2.0
 */
public class InvalidStockException extends Exception{

	/**
	 * SerialVersionUID for consistent serialization.
	 */
	private static final long serialVersionUID = 5179683968502880461L;

	/**
	 * An Exception with the default message.
	 */
	public InvalidStockException(){
		super();
	}
	
	/**
	 * An Exception with a custom message.
	 * 
	 * @param message a custom message for the exception.
	 */
	public InvalidStockException(String message){
		super(message);
	}
	
	/**
	 * An Exception with a detailed cause.
	 * 
	 * @param cause the cause of the Exception.
	 */
	public InvalidStockException(Throwable cause){
		super(cause);
	}
	
	/**
	 * An Exception with a custom message and a detailed cause.
	 * 
	 * @param message a custom message for the Exception.
	 * @param cause the cause of the Exception.
	 */
	public InvalidStockException(String message, Throwable cause){
		super(message, cause);
	}
	
}
