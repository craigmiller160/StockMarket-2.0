package io.craigmiller160.stockmarket.stock;

import java.math.BigDecimal;

import javax.persistence.MappedSuperclass;

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
 * <p>
 * <b>THREAD SAFETY:</b> Interfaces can make no guarantee about the 
 * thread safety of inheriting classes. However, <tt>OwnedStock</tt>
 * implementations should ideally be thread safe, so they can be handled 
 * concurrently by this program. Whether they are or not is ultimately
 * determined by the implementing class, so the documentation for
 * the implementation used by this program should be consulted.
 * 
 * @author craig
 * @version 2.2
 */
@MappedSuperclass
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
	 * Add the specified number of shares of this stock. The number of shares added is multiplied by 
	 * the current share price to get the amount to increase the  total value. 
	 * The principle and net are also updated accordingly.
	 * <p>
	 * The stock details must be set before adding/subtracting any shares. 
	 * If they have not been set, an exception is thrown.
	 * 
	 * @param quantity the quantity of shares to add.
	 * @throws IllegalStateException if the stock details have not been set before
	 * invoking this method.
	 */
	void addShares(int quantity);
	
	/**
	 * Add the shares of the <tt>OwnedStock</tt> parameter to this stock, only if the
	 * parameter is the same stock as this one (ie, has the same symbol). If it is not 
	 * the same stock as this one, an exception is thrown. This method is
	 * a helper method for adding shares, and should be used with a more recently updated
	 * OwnedStock object as its parameter. 
	 * <p>
	 * The stock details must be set before adding/subtracting any shares. 
	 * If they have not been set, an exception is thrown.
	 * <p>
	 * The current share price is also updated during this operation. This method
	 * operates on the assumption that the stock parameter has had its details
	 * updated more recently than this object has been, and it sets the current
	 * price of the parameter as the current price of this whole object. This is
	 * done prior to the operations to update the principle, total value, and net.
	 * 
	 * @param stock the stock who's shares are to be added to this one.
	 * @throws IllegalStateException if the stock details have not been set before
	 * invoking this method, either in this stock class or the parameter.
	 * @throws IllegalStateException if the stock details have not been set before
	 * invoking this method.
	 * @throws IllegalArgumentException if the stock parameter is not the same
	 * stock as this one (symbols don't match).
	 */
	void addShares(OwnedStock stock);
	
	/**
	 * Subtract the specified number of shares from this stock. The number of shares subtracted is 
	 * multiplied by the current share price to get the amount to decrease the
	 * total value by. The principle and net are also updated accordingly.
	 * Finally, the method returns the value of the shares that were subtracted,
	 * the "profit" from a sale.
	 * <p>
	 * The stock details must be set before adding/subtracting any shares. 
	 * If they have not been set, an exception is thrown.
	 * <p>
	 * 
	 * @param quantity the quantity of shares to decrease by.
	 * @return the value of the shares that were subtracted.
	 * @throws InsufficientSharesException if the quantity of shares to be subtracted
	 * is greater than the total quantity that this stock has.
	 * @throws IllegalStateException if the stock details have not been set before
	 * invoking this method.
	 */
	BigDecimal subtractShares(int quantity);
	
	/**
	 * Subtract the shares of the <tt>OwnedStock</tt> parameter to this stock, only if the
	 * parameter is the same stock as this one (ie, has the same symbol). If it
	 * is not the same stock as this one, an exception is thrown. This method is
	 * a helper method for subtracting shares, and should be used with a more recently updated
	 * OwnedStock object as its parameter. Finally, the method returns the value of the shares that were subtracted,
	 * the "profit" from a sale.
	 * <p>
	 * The stock details must be set before adding/subtracting any shares. 
	 * If they have not been set, an exception is thrown.
	 * <p>
	 * The current share price is also updated during this operation. This method
	 * operates on the assumption that the stock parameter has had its details
	 * updated more recently than this object has been, and it sets the current
	 * price of the parameter as the current price of this whole object. This is
	 * done prior to the operations to update the principle, total value, and net.
	 * <p>
	 * 
	 * @param stock the stock who's shares are to be added to this one.
	 * @return the value of the shares that were subtracted.
	 * @throws InsufficientSharesException if the quantity of shares to be subtracted
	 * is greater than the total quantity that this stock has.
	 * @throws IllegalStateException if the stock details have not been set before
	 * invoking this method, either in this stock class or the parameter.
	 * @throws IllegalArgumentException if the stock parameter is not the same
	 * stock as this one (symbols don't match).
	 */
	BigDecimal subtractShares(OwnedStock stock);
	
}
