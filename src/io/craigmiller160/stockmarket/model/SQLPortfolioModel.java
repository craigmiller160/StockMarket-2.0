package io.craigmiller160.stockmarket.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * An extension of the standard <tt>PortfolioModel</tt> to integrate
 * a field for a SQL userid primary key. This value is used when loading
 * and saving this model from the database.
 * <p>
 * <b>THREAD SAFETY:</b> The additions to this class are completely
 * thread safe. Consult the superclass documentation for details on its
 * synchronization policy.
 * 
 * @author craig
 * @version 2.0
 */
@ThreadSafe
public class SQLPortfolioModel extends PortfolioModel{

	/**
	 * SerialVersionUID for serialization support.
	 */
	private static final long serialVersionUID = -6524963260822254311L;
	
	/**
	 * The SQL userid, not a bound property, only necessary for
	 * saving and loading the portfolio from the database.
	 */
	@GuardedBy("this")
	private int userID;
	
	/**
	 * Create a new instance of this model.
	 */
	public SQLPortfolioModel() {
		super();
	}
	
	/**
	 * Shared logger for the program.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.model.SQLPortfolioModel");
	
	/**
	 * Sets the SQL userid for this portfolio. This is not a bound
	 * property, and is only necessary for saving and loading this
	 * portfolio from the database. Because this value is essential
	 * to saving the state of this portfolio, it should always match
	 * a value in the database.
	 * 
	 * @param userID the SQL userid for this portfolio.
	 */
	public void setUserID(int userID){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setUserID", "Entering method", 
				new Object[] {"UserID: " + userID});
		
		synchronized(this){
			this.userID = userID;
		}
	}
	
	/**
	 * Returns the SQL userid for saving and loading this portfolio's state
	 * 
	 * @return the SQL userid.
	 */
	public synchronized int getUserID(){
		return userID;
	}

}
