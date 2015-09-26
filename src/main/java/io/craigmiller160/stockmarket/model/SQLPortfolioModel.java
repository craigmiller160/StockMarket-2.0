package io.craigmiller160.stockmarket.model;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

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
 * @version 2.2
 */
@ThreadSafe
@Entity
@Table (name="portfolio")
public class SQLPortfolioModel extends PortfolioModel{

	/**
	 * SerialVersionUID for serialization support.
	 */
	@Transient
	private static final long serialVersionUID = -6524963260822254311L;
	
	/**
	 * The SQL userid, not a bound property, only necessary for
	 * saving and loading the portfolio from the database.
	 */
	@GuardedBy("this")
	@SequenceGenerator (name="portfolio_sequence", sequenceName="portfolio_sequence", allocationSize=1)
	@Id @GeneratedValue (strategy=GenerationType.TABLE, generator="portfolio_sequence")
	private int userID;
	
	/**
	 * The timestamp of the last save of this class.
	 */
	@Temporal(value=TemporalType.TIMESTAMP)
	private Calendar timestamp;
	
	/**
	 * Create a new instance of this model.
	 */
	public SQLPortfolioModel() {
		super();
	}
	
	/**
	 * Shared logger for the program.
	 */
	@Transient
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

	public synchronized Calendar getTimestamp() {
		return timestamp;
	}

	public synchronized void setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}

}
