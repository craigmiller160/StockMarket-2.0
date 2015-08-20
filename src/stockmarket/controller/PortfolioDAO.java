package stockmarket.controller;

import java.math.BigDecimal;
import java.util.List;

import stockmarket.model.PortfolioModel;

/**
 * Interface that defines a data access object for accessing the datasource
 * of a stock portfolio. Implementations of this interface should be
 * used to save/load a stock portfolio between program sessions. This
 * allows for an abstraction layer to exist between the program's 
 * implementation and the logic used to access the data storage location.
 * 
 * @author craig
 * @version 2.0
 */
public interface PortfolioDAO {

	/**
	 * Create a new stock portfolio.
	 * 
	 * @param portfolioName the name of the portfolio.
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
	 * @see stockmarket.controller.PortfolioDAO#getSavedPortfolios() 
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
