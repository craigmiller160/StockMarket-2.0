package io.craigmiller160.stockmarket.controller;

import java.util.List;

import io.craigmiller160.stockmarket.model.PortfolioModel;

/**
 * Basic interface for a DAO object to handle CRUD operations
 * for persisting a stock portfolio in this program. Implementations
 * of this interface should be combined with a datasource and used
 * as the lowest level point of interaction with the database.
 * 
 * @author craig
 * @version 2.4
 */
public interface PortfolioDao {

	/**
	 * Insert a new portfolio into the database.
	 * 
	 * @param portfolioModel the new portfolio to insert.
	 */
	void insertPortfolio(PortfolioModel portfolioModel);
	
	/**
	 * Get a list of all portfolios in the database.
	 * 
	 * @return a list of all portfolios in the database.
	 */
	List<PortfolioModel> getPortfolioList();
	
	/**
	 * Get a portfolio from the database, based on the supplied
	 * userid.
	 * 
	 * @param userid the userid of the portfolio to retrieve.
	 * @return the specified portfolio.
	 */
	PortfolioModel getPortfolio(int userid);
	
	/**
	 * Update a portfolio in the database.
	 * 
	 * @param portfolioModel the portfolio to update in the database.
	 */
	void updatePortfolio(PortfolioModel portfolioModel);
	
	/**
	 * Delete a portfolio from the database.
	 * 
	 * @param portfolioModel the portfolio to delete from the database.
	 */
	void deletePortfolio(PortfolioModel portfolioModel);
	
}
