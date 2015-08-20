package stockmarket.stock;

import java.math.BigDecimal;

/**
 * This interface extends the <tt>Stock</tt> interface with methods
 * and fields defining a stock that is owned as part of a stock portfolio.
 * This interface should be implemented either as a decorator class around
 * a <tt>Stock</tt> implementation, or as a class that extends said <tt>Stock</tt>
 * implementation.
 * <p>
 * The main fields of this interface are the additional values for an 
 * owned stock. There will always be a quantity of shares owned, a
 * principle (the initial amount spent on the shares), a total value of
 * all shares combined, and a net which is the difference between the
 * total value and the principle.
 * <p>
 * Like the <tt>Stock</tt> interface, <tt>OwnedStock</tt> eschews direct
 * setter methods. Instead, it inherits the <tt>setStockDetails()</tt>
 * method from <tt>Stock</tt>, and brings two more methods, <tt>increaseShares()</tt>
 * and <tt>decreaseShares()</tt>. The base properties of the stock are set
 * by the inherited method, while the values of all the other fields are
 * all dependent on the number of shares that are owned of the stock.
 * Therefore <tt>increaseShares()</tt> and <tt>decreaseShares()</tt>
 * should be implemented to change all values of this stock when they
 * are called.
 * 
 * @author craig
 * @version 2.0
 */
public interface OwnedStock extends Stock{

	/**
	 * Quantity of shares property name.
	 */
	String QUANTITY_OF_SHARES = "QuantityOfShares";
	
	/**
	 * Total Value property name.
	 */
	String TOTAL_VALUE = "TotalValue";
	
	/**
	 * Net property name.
	 */
	String NET = "Net";
	
	/**
	 * Principle property name.
	 */
	String PRINCIPLE = "Principle";
	
	/**
	 * Get the total quantity of shares of this stock that are owned.
	 * 
	 * @return the total quantity of shares of this stock that are owned.
	 */
	int getQuantityOfShares();
	
	/**
	 * Get the total combined value of all shares of this stock. 
	 * 
	 * @return the total combined value of all shares of this stock.
	 */
	BigDecimal getTotalValue();
	
	/**
	 * Get the net gains/losses on this stock since purchase.
	 * 
	 * @return the net gains/losses on this stock since purchase.
	 */
	BigDecimal getNet();
	
	/**
	 * Get the principle used to calculate the net gains/losses on this stock.
	 * 
	 * @return the principle used to calculate the net gains/losses on this stock.
	 */
	BigDecimal getPrinciple();
	
	/**
	 * Increase the number of shares owned of this stock. Adjusts the principle, totalValue,
	 * and net based on the change.
	 * 
	 * @param quantity the quantity of shares to increase by.
	 */
	void increaseShares(int quantity);
	
	/**
	 * Decrease the number of shares owned of this stok. Adjusts the principle, totalValue,
	 * and net based on the change. Returns a boolean value for whether or not there are
	 * any shares remaining of the stock.
	 * <p>
	 * Code handling an instance of <tt>OwnedStock</tt> can use the boolean value it returns
	 * as a means of executing a special response to if all shares have been sold.
	 * 
	 * @param quantity the quantity of shares to decrease by.
	 * @return true if there are shares remaining of the stock, false if there are
	 * not shares remaining.
	 */
	boolean decreaseShares(int quantity);
	
}
