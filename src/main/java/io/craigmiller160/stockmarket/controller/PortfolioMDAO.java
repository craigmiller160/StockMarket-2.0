package io.craigmiller160.stockmarket.controller;

import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.List;

import io.craigmiller160.stockmarket.model.PortfolioModel;

/**
 * Interface that defines a data access object for accessing the datasource
 * of a stock portfolio. Implementations of this interface should be
 * used to save/load a stock portfolio between program sessions. This
 * allows for an abstraction layer to exist between the program's 
 * implementation and the logic used to access the data storage location.
 * <p>
 * <b>DEPRECATED:</b> As of Version 2.4, this class has been deprecated.
 * Its attributes have been split between <tt>PortfolioDao</tt> and 
 * <tt>PortfolioPersistService</tt>.
 * 
 * @author craig
 * @version 2.0
 */
@Deprecated
public interface PortfolioMDAO extends PropertyChangeListener{

	/**
	 * Add an external <tt>PropertyChangeListener</tt> to pass events to as the
	 * model is being constructed.
	 * 
	 * @param listener the external listener.
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);
	
	/**
	 * Removes an external <tt>PropertyChangeListener</tt> so that events
	 * are no longer passed to it as the model is being constructed.
	 * 
	 * @param listener the external listener.
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);
	
	/**
	 * Create a new stock portfolio, with the specified name and starting 
	 * cash balance.
	 * 
	 * @param portfolioName the name of the portfolio.
	 * @param startingCashBalance the starting cash balance of the portfolio.
	 * @return a new stock portfolio.
	 * @throws Exception if a problem occurs while trying to access the data source.
	 */
	PortfolioModel createNewPortfolio(String portfolioName, BigDecimal startingCashBalance) 
			throws Exception;
	
	/**
	 * Returns a list of the file names all saved portfolios from the data source.
	 * 
	 * @return a list of the names of all saved portfolios.
	 * @throws Exception if a problem occurs while trying to access the data source.
	 */
	List<String> getSavedPortfolios() throws Exception;
	
	/**
	 * Returns a portfolio loaded from the data source that matches the
	 * specified file name. <b>NOTE:</b> if the file name string does not match
	 * the expected file name format, an exception will be thrown. To avoid this,
	 * this method should maintain an explicit contract with <tt>getSavedPortfolios()</tt>.
	 * The file name parameter here should be a perfect match to the format of the strings 
	 * in the list returned by that method.
	 * 
	 * @param fileName the file name of the portfolio.
	 * @return the portfolio loaded from the data source.
	 * @throws Exception if a problem occurs while trying to access the data source,
	 * or if the file name parameter isn't a valid file name for a portfolio.
	 * @see io.craigmiller160.stockmarket.controller.PortfolioMDAO#getSavedPortfolios() 
	 * PortfolioDAO.getSavedPortfolios()
	 */
	PortfolioModel getPortfolio(String fileName) throws Exception;
	
	/**
	 * Saves the portfolio to the data source for storing its values between
	 * program sessions.
	 * 
	 * @param portfolio the portfolio to be saved.
	 * @throws Exception if a problem occurs while trying to access the data source.
	 */
	void savePortfolio(PortfolioModel portfolio) throws Exception;
	
}
