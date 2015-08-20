package stockmarket.model;

import static stockmarket.controller.StockMarketController.CASH_BALANCE_PROPERTY;
import static stockmarket.controller.StockMarketController.CHANGE_IN_NET_WORTH_PROPERTY;
import static stockmarket.controller.StockMarketController.NET_WORTH_PROPERTY;
import static stockmarket.controller.StockMarketController.PORTFOLIO_NAME_PROPERTY;
import static stockmarket.controller.StockMarketController.TOTAL_STOCK_VALUE_PROPERTY;
import static stockmarket.controller.StockMarketController.STOCK_LIST_PROPERTY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import stockmarket.stock.OwnedStock;

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
	 * Create a new instance of this model. All fields are initially
	 * left as <tt>null</tt>.
	 */
	public PortfolioModel() {
		super();
		//TODO for the moment, this is no longer a synchronized list. If I stick with that,
		//documentation will need to change.
		//TODO the DAO will need to be changed if this sticks, no need to synchronize and copy 
		//the list now. Probably still the need to copy though.
		stockList = new ArrayList<>();
	}
	
	/**
	 * {@inheritDoc} This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed. Because a reference to the stock list is released with 
	 * the event, <tt>PropertyChangeListener</tt>s should handle it with
	 * care.
	 */
	@Override
	public synchronized void setStockList(List<OwnedStock> stockList){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setStockList", "Entering method", 
				new Object[] {"Stock List: " + stockList});
		
		List<OwnedStock> oldValue = this.stockList;
		this.stockList = stockList;
		firePropertyChange(STOCK_LIST_PROPERTY, oldValue, stockList);
	}
	
	/**
	 * {@inheritDoc} This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 */
	@Override
	public synchronized void setPortfolioName(String portfolioName){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setPortfolioName", "Entering method", 
				new Object[] {"Name: " + portfolioName});
		
		String oldValue = this.portfolioName;
		this.portfolioName = portfolioName;
		firePropertyChange(PORTFOLIO_NAME_PROPERTY, oldValue, portfolioName);
	}
	
	/**
	 * {@inheritDoc} This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 * 
	 */
	@Override
	public synchronized void setTotalStockValue(BigDecimal portfolioValue){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setTotalStockValue", "Entering method", 
				new Object[] {"Total Stock Value: " + portfolioValue});
		
		BigDecimal oldValue = this.totalStockValue;
		this.totalStockValue = portfolioValue;
		firePropertyChange(TOTAL_STOCK_VALUE_PROPERTY, oldValue, portfolioValue);
	}
	
	/**
	 * {@inheritDoc} This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 */
	@Override
	public synchronized void setNetWorth(BigDecimal netWorth){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setNetWorth", "Entering method", 
				new Object[] {"Net Worth: " + netWorth});
		
		BigDecimal oldValue = this.netWorth;
		this.netWorth = netWorth;
		firePropertyChange(NET_WORTH_PROPERTY, oldValue, netWorth);
	}
	
	/**
	 * {@inheritDoc} This is 
	 * calculated by comparing the current net worth to the starting cash balance. 
	 * This is a bound property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 */
	@Override
	public synchronized void setChangeInNetWorth(BigDecimal netWorthChange){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setChangeInNetWorth", "Entering method", 
				new Object[] {"Net Worth Change: " + netWorthChange});
		
		BigDecimal oldValue = this.changeInNetWorth;
		this.changeInNetWorth = netWorthChange;
		firePropertyChange(CHANGE_IN_NET_WORTH_PROPERTY, oldValue, netWorthChange);
	}
	
	/**
	 * {@inheritDoc} This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 */
	@Override
	public synchronized void setCashBalance(BigDecimal cashBalance){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setCashBalance", "Entering method", 
				new Object[] {"Cash Balance: " + cashBalance});
		
		BigDecimal oldValue = this.cashBalance;
		this.cashBalance = cashBalance;
		firePropertyChange(CASH_BALANCE_PROPERTY, oldValue, cashBalance);
	}
	
	/**
	 * {@inheritDoc} The stock list reference
	 * returned should be used in as few places as possible to minimize potential
	 * areas where multiple threads could make concurrent modifications to this list.
	 */
	@Override
	public synchronized List<OwnedStock> getStockList(){
		return stockList;
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

}