package io.craigmiller160.stockmarket.controller;

import java.math.BigDecimal;
import java.util.List;

import io.craigmiller160.stockmarket.model.PortfolioModel;

/**
 * An interface for a persistence service layer for stock
 * portfolios. Implementations of this interface should call
 * on the appropriate DAO implementation to handle the 
 * actual persisting of the object, and add code
 * for transaction handling and additional operations on
 * top of the CRUD operations that the DAO handles.
 * 
 * @author craig
 * @version 2.4
 */
public interface PortfolioPersistService {

	/**
	 * Create a new portfolio with the specified portfolio name
	 * and starting cash balance.
	 * 
	 * @param portfolioName the name of the new portfolio.
	 * @param startingCashBalance the starting cash balance of the
	 * new portfolio.
	 * @return the newly created portfolio.
	 */
	PortfolioModel createNewPortfolio(String portfolioName, 
			BigDecimal startingCashBalance);
	
	/**
	 * Get a list of the names of all saved portfolios.
	 * 
	 * @return a list of the names of all saved portfolios.
	 */
	List<String> getSavedPortfolioNames();
	
	/**
	 * Get a portfolio with the corresponding file name.
	 * The filename must be a perfect match for one of
	 * the names returned by the <tt>getSavedPortfolioNames()</tt>
	 * method.
	 * 
	 * @param fileName the filename of the portfolio.
	 * @return the portfolio that matches the filename.
	 */
	PortfolioModel getPortfolio(String fileName);
	
	/**
	 * Get a portfolio with the specified userid.
	 * 
	 * @param userid the id of the portfolio.
	 * @return the portfolio with the specified userid.
	 */
	PortfolioModel getPortfolio(int userid);
	
	/**
	 * Save a portfolio to the database.
	 * 
	 * @param portfolioModel the portfolio to save.
	 */
	void savePortfolio(PortfolioModel portfolioModel);
}
