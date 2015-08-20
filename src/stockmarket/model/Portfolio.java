package stockmarket.model;

import java.math.BigDecimal;
import java.util.List;

import stockmarket.stock.OwnedStock;

/**
 * An interface defining the methods of a stock portfolio object.
 * 
 * @author craig
 * @version 2.0
 */
public interface Portfolio {

	/**
	 * Sets the name of this portfolio. 
	 * 
	 * @param portfolioName the name of this portfolio.
	 */
	void setPortfolioName(String portfolioName);
	
	/**
	 * Sets the cash balance, the amount of money available to purchase stocks. 
	 * 
	 * @param cashBalance the cash balance available to purchase stocks.
	 */
	void setCashBalance(BigDecimal cashBalance);
	
	/**
	 * Sets the net worth of this portfolio, the combined total of the total
	 * value of the stocks and the cash balance. 
	 * 
	 * @param netWorth the net worth of this portfolio.
	 */
	void setNetWorth(BigDecimal netWorth);
	
	/**
	 * Sets the change in net worth since this portfolio was created. 
	 * 
	 * @param netWorthChange the change in net worth since this portfolio was created.
	 */
	void setChangeInNetWorth(BigDecimal changeInNetWorth);
	
	/**
	 * Sets the total value of the stocks in this portfolio. 
	 * 
	 * @param portfolioValue the total value of the stocks in this portfolio.
	 */
	void setTotalStockValue(BigDecimal totalStockValue);
	
	/**
	 * Sets the list of stocks owned by this portfolio. 
	 * 
	 * @param stockList the list of stocks owned by this portfolio.
	 */
	void setStockList(List<OwnedStock> stockList);
	
	/**
	 * Returns the name of this portfolio.
	 * 
	 * @return the name of this portfolio.
	 */
	String getPortfolioName();
	
	/**
	 * Returns the cash balance available to purchase stocks.
	 * 
	 * @return the cash balance.
	 */
	BigDecimal getCashBalance();
	
	/**
	 * Returns the net worth of this portfolio.
	 * 
	 * @return the net worth of this portfolio.
	 */
	BigDecimal getNetWorth();
	
	/**
	 * Returns the change in net worth since the creation of this portfolio.
	 * 
	 * @return the change in net worth since the creation of this portfolio.
	 */
	BigDecimal getChangeInNetWorth();
	
	/**
	 * Returns the total value of all the stocks in this portfolio.
	 * 
	 * @return the total value of all the stocks in this portfolio.
	 */
	BigDecimal getTotalStockValue();
	
	/**
	 * Returns the list of stocks owned by this portfolio. 
	 * 
	 * @return the list of stocks owned by this portfolio.
	 */
	List<OwnedStock> getStockList();
	
}
