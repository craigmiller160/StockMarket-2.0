package io.craigmiller160.stockmarket.model;

import static io.craigmiller160.stockmarket.controller.StockMarketController.CASH_BALANCE_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.CHANGE_IN_NET_WORTH_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.NET_WORTH_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.PORTFOLIO_NAME_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.STOCK_LIST_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.TOTAL_STOCK_VALUE_PROPERTY;
import io.craigmiller160.mvp.core.AbstractPropertyModel;
import io.craigmiller160.stockmarket.stock.OwnedStock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * A <tt>JavaBean</tt> bound property model storing the values defining
 * a stock portfolio in the <tt>StockMarket</tt> program. These values 
 * are the portfolio's name, its cash balance (the amount of money available
 * to buy stocks), the value of the stocks in the portfolio, the list of those
 * stocks, the net worth, and the change in that net worth since the portfolio
 * was created.
 * <p>
 * <b>THREAD SAFETY:</b> While this model is completely thread safe, extra care must
 * be taken with the stock list property. It can be leaked from this class via the
 * getter method or the <tt>PropertyChangeEvent</tt> fired by its setter. The leaked
 * reference must be properly synchronized when either iterating or modifying it. 
 * Synchronization should be done on the list's intrinsic lock. Not-iteration reads
 * do not need locking, as this list is a synchronized collection.
 * 
 * @author craig
 * @version 2.0
 */
@ThreadSafe
public class PortfolioModel extends AbstractPropertyModel implements Portfolio {

	//TODO this model needs default values to avoid exceptions occuring when attempting
	//to save values to database.
	
	//TODO maybe stockList should be a set? It shouldn't accept duplicate values, but
	//it should throw an exception if a duplicate is submitted. Or some sort of warning,
	//rather than loose the values.
	
	/**
	 * SerialVersionUID for serialization support.
	 */
	private static final long serialVersionUID = -6524963260822254311L;

	/**
	 * The name of the portfolio.
	 */
	@GuardedBy("this")
	private String portfolioName;
	
	/**
	 * The cash balance available to purchase stocks.
	 */
	@GuardedBy("this")
	private BigDecimal cashBalance;
	
	/**
	 * The total value of the stocks in the portfolio.
	 */
	@GuardedBy("this")
	private BigDecimal totalStockValue;
	
	/**
	 * The net worth of the portfolio, combining the value of the stocks
	 * with the cash balance.
	 */
	@GuardedBy("this")
	private BigDecimal netWorth;
	
	/**
	 * The change in net worth since the creation of this portfolio.
	 */
	@GuardedBy("this")
	private BigDecimal changeInNetWorth;
	
	/**
	 * The list of stocks owned by this portfolio.
	 */
	@GuardedBy("this")
	private List<OwnedStock> stockList;
	
	/**
	 * Shared logger for the program.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.model.PortfolioModel");
	
	/**
	 * Create a new instance of this model.
	 */
	public PortfolioModel() {
		super();
		stockList = new ArrayList<>();
		portfolioName = "";
		cashBalance = new BigDecimal(0);
		totalStockValue = new BigDecimal(0);
		netWorth = new BigDecimal(0);
		changeInNetWorth = new BigDecimal(0);
	}
	
	/**
	 * {@inheritDoc} This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 */
	@Override
	public void setStockList(List<OwnedStock> stockList){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setStockList", "Entering method", 
				new Object[] {"Stock List: " + stockList});
		
		List<OwnedStock> oldValue = null;
		synchronized(this){
			oldValue = this.stockList;
			if(stockList == null){
				this.stockList = new ArrayList<>();
			}
			else{
				this.stockList = new ArrayList<>(stockList);
			}
		}
		
		firePropertyChange(STOCK_LIST_PROPERTY, oldValue, stockList);
	}
	
	/**
	 * {@inheritDoc} This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 */
	@Override
	public void setPortfolioName(String portfolioName){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setPortfolioName", "Entering method", 
				new Object[] {"Name: " + portfolioName});
		
		String oldValue = null;
		synchronized(this){
			oldValue = this.portfolioName;
			this.portfolioName = portfolioName;
		}
		
		firePropertyChange(PORTFOLIO_NAME_PROPERTY, oldValue, portfolioName);
	}
	
	/**
	 * {@inheritDoc} This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 * 
	 */
	@Override
	public void setTotalStockValue(BigDecimal portfolioValue){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setTotalStockValue", "Entering method", 
				new Object[] {"Total Stock Value: " + portfolioValue});
		
		BigDecimal oldValue = null;
		synchronized(this){
			oldValue = this.totalStockValue;
			this.totalStockValue = portfolioValue;
		}
		
		firePropertyChange(TOTAL_STOCK_VALUE_PROPERTY, oldValue, portfolioValue);
	}
	
	/**
	 * {@inheritDoc} This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 */
	@Override
	public void setNetWorth(BigDecimal netWorth){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setNetWorth", "Entering method", 
				new Object[] {"Net Worth: " + netWorth});
		
		BigDecimal oldValue = null;
		synchronized(this){
			oldValue = this.netWorth;
			this.netWorth = netWorth;
		}
		
		firePropertyChange(NET_WORTH_PROPERTY, oldValue, netWorth);
	}
	
	/**
	 * {@inheritDoc} This is 
	 * calculated by comparing the current net worth to the starting cash balance. 
	 * This is a bound property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 */
	@Override
	public void setChangeInNetWorth(BigDecimal netWorthChange){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setChangeInNetWorth", "Entering method", 
				new Object[] {"Net Worth Change: " + netWorthChange});
		
		BigDecimal oldValue = null;
		synchronized(this){
			oldValue = this.changeInNetWorth;
			this.changeInNetWorth = netWorthChange;
		}
		
		firePropertyChange(CHANGE_IN_NET_WORTH_PROPERTY, oldValue, netWorthChange);
	}
	
	/**
	 * {@inheritDoc} This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 */
	@Override
	public void setCashBalance(BigDecimal cashBalance){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setCashBalance", "Entering method", 
				new Object[] {"Cash Balance: " + cashBalance});
		
		BigDecimal oldValue = null;
		synchronized(this){
			oldValue = this.cashBalance;
			this.cashBalance = cashBalance;
		}
		
		firePropertyChange(CASH_BALANCE_PROPERTY, oldValue, cashBalance);
	}

	//TODO need a more descriptive name here
	public void setStockInList(OwnedStock stock){
		List<OwnedStock> copyList = null;
		synchronized(this){
			int index = stockList.indexOf(stock);
			if(index >= 0){
				if(stock.getQuantityOfShares() > 0){
					stockList.set(index, stock);
				}
				else{
					stockList.remove(stock);
				}
			}
			else{
				stockList.add(stock);
			}
			copyList = new ArrayList<>(stockList);
		}
		
		firePropertyChange(STOCK_LIST_PROPERTY, null, copyList);
	}
	
	public OwnedStock getStockInList(OwnedStock stock){
		OwnedStock result = null;
		synchronized(this){
			int index = stockList.indexOf(stock);
			result = index >= 0 ? stockList.get(index) : null;
		}
		
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * To maintain thread safety, the stock list returned is
	 * a shallow copy of the one guarded by this class's lock.
	 * The elements in the list are shared references, though.
	 * If the <tt>OwnedStock</tt> implementation is not thread
	 * safe, then additional synchronization measures will be
	 * needed.
	 */
	@Override
	public synchronized List<OwnedStock> getStockList(){
		List<OwnedStock> copyList = new ArrayList<>(stockList);
		return copyList;
	}
	
	@Override
	public synchronized String getPortfolioName(){
		return portfolioName;
	}
	
	@Override
	public synchronized BigDecimal getTotalStockValue(){
		return totalStockValue;
	}
	
	@Override
	public synchronized BigDecimal getNetWorth(){
		return netWorth;
	}
	
	@Override
	public synchronized BigDecimal getChangeInNetWorth(){
		return changeInNetWorth;
	}
	
	@Override
	public synchronized BigDecimal getCashBalance(){
		return cashBalance;
	}
	
	//TODO the handling of the stock list needs to be improved for thread-safety purposes.
		//The stock history list has the same issue. Need to figure out the most thread-safe
		//way to share handling these lists between threads. They don't need to be accessed
		//concurrently, but their data needs to be passed between classes safely.

}
