package io.craigmiller160.stockmarket.model;

import static io.craigmiller160.stockmarket.controller.StockMarketController.CASH_BALANCE_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.CHANGE_IN_NET_WORTH_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.NET_WORTH_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.PORTFOLIO_NAME_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.STOCK_LIST_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.TOTAL_STOCK_VALUE_PROPERTY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import io.craigmiller160.mvp.core.AbstractPropertyModel;
import io.craigmiller160.stockmarket.stock.DefaultOwnedStock;
import io.craigmiller160.stockmarket.stock.OwnedStock;
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
 * @version 2.3
 */
@ThreadSafe
@MappedSuperclass
@Inheritance (strategy=InheritanceType.TABLE_PER_CLASS)
public class PortfolioModel extends AbstractPropertyModel implements Portfolio {

	/**
	 * SerialVersionUID for serialization support.
	 */
	@Transient
	private static final long serialVersionUID = -6524963260822254311L;

	/**
	 * The name of the portfolio.
	 */
	@GuardedBy("this")
	@Column (name="portfolio_name")
	private String portfolioName;
	
	/**
	 * The cash balance available to purchase stocks.
	 */
	@GuardedBy("this")
	@Column (name="cash_balance")
	private BigDecimal cashBalance;
	
	/**
	 * The total value of the stocks in the portfolio.
	 */
	@GuardedBy("this")
	@Column (name="total_stock_value")
	private BigDecimal totalStockValue;
	
	/**
	 * The net worth of the portfolio, combining the value of the stocks
	 * with the cash balance.
	 */
	@GuardedBy("this")
	@Column (name="net_worth")
	private BigDecimal netWorth;
	
	/**
	 * The initial value of this portfolio. Used to calculate
	 * the change in net worth over time.
	 */
	@GuardedBy("this")
	@Column (name="initial_value")
	private BigDecimal initialValue;
	
	/**
	 * The change in net worth since the creation of this portfolio.
	 */
	@GuardedBy("this")
	@Transient
	private BigDecimal changeInNetWorth;
	
	/**
	 * The list of stocks owned by this portfolio.
	 */
	@GuardedBy("this")
	@OneToMany (mappedBy="portfolio", targetEntity=DefaultOwnedStock.class, cascade=CascadeType.ALL)
	private List<OwnedStock> stockList;
	
	/**
	 * Create a new instance of this model.
	 */
	public PortfolioModel() {
		super();
		stockList = new ArrayList<>();
		portfolioName = "";
		initialValue = new BigDecimal(0);
		cashBalance = new BigDecimal(0);
		netWorth = new BigDecimal(0);
		changeInNetWorth = new BigDecimal(0);
		totalStockValue = new BigDecimal(0);
	}
	
	/**
	 * Set the initial values of this portfolio. This method should be
	 * invoked immediately after creating a new portfolio model instance,
	 * as it overwrites all other values based on the starting amount
	 * of cash provided as a parameter.
	 * 
	 * @param startingCash the starting amount of cash for the portfolio.
	 */
	public void setInitialValue(BigDecimal startingCash){
		synchronized(this){
			this.initialValue = startingCash;
		}
		calculateChangeInNetWorth();
	}
	
	/**
	 * {@inheritDoc} This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 */
	@Override
	public void setStockList(List<OwnedStock> stockList){
		synchronized(this){
			if(stockList == null){
				this.stockList = new ArrayList<>();
			}
			else{
				this.stockList = new ArrayList<>(stockList);
			}
		}
		
		firePropertyChange(STOCK_LIST_PROPERTY, null, stockList);
		
		calculateTotalStockValue();
		calculateNetWorth();
	}
	
	/**
	 * {@inheritDoc} This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 */
	@Override
	public void setPortfolioName(String portfolioName){
		synchronized(this){
			this.portfolioName = portfolioName;
		}
		
		firePropertyChange(PORTFOLIO_NAME_PROPERTY, null, portfolioName);
	}
	
	/**
	 * {@inheritDoc} This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 * 
	 */
	@Override
	public void setTotalStockValue(BigDecimal portfolioValue){
		synchronized(this){
			this.totalStockValue = portfolioValue;
		}
		
		firePropertyChange(TOTAL_STOCK_VALUE_PROPERTY, null, portfolioValue);
	}
	
	/**
	 * {@inheritDoc} This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 */
	@Override
	public void setNetWorth(BigDecimal netWorth){
		synchronized(this){
			this.netWorth = netWorth;
		}
		
		firePropertyChange(NET_WORTH_PROPERTY, null, netWorth);
		
		calculateChangeInNetWorth();
	}
	
	/**
	 * {@inheritDoc} This is 
	 * calculated by comparing the current net worth to the starting cash balance. 
	 * This is a bound property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 */
	@Override
	public void setChangeInNetWorth(BigDecimal netWorthChange){
		synchronized(this){
			this.changeInNetWorth = netWorthChange;
		}
		
		firePropertyChange(CHANGE_IN_NET_WORTH_PROPERTY, null, netWorthChange);
	}
	
	/**
	 * {@inheritDoc} This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 */
	@Override
	public void setCashBalance(BigDecimal cashBalance){
		synchronized(this){
			this.cashBalance = cashBalance;
		}
		
		firePropertyChange(CASH_BALANCE_PROPERTY, null, cashBalance);
		
		calculateNetWorth();
	}

	/**
	 * Sets the specified stock in the list. If the stock
	 * parameter is not in the list, it's added to it.
	 * If the stock is in the list, it replaces the existing
	 * instance in the list. If, however, the stock parameter
	 * has 0 shares and matches a stock in the list, then its
	 * match is simply removed.
	 * 
	 * @param stock the stock to be set in the list.
	 */
	public void setStockInList(OwnedStock stock){
		((DefaultOwnedStock)stock).setPortfolio(this);
		
		List<OwnedStock> copyList = null;
		int index;
		synchronized(this){
			index = stockList.indexOf(stock);
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
		
		calculateTotalStockValue();
		calculateNetWorth();
	}
	
	/**
	 * Get a stock from the list, a match for the stock 
	 * passed to this method.
	 * 
	 * @param stock a stock with the same symbol as the stock to 
	 * retrieve.
	 * @return the stock that matches the stock parameter, or
	 * null if there is no match.
	 */
	public OwnedStock getStockInList(OwnedStock stock){
		OwnedStock result = null;
		synchronized(this){
			int index = stockList.indexOf(stock);
			result = index >= 0 ? stockList.get(index) : null;
		}
		
		return result;
	}
	
	/**
	 * Get a stock from the list, based on its symbol.
	 * 
	 * @param symbol the symbol of the stock.
	 * @return the stock from the list, based on its symbol, or
	 * null if the stock doesn't exist.
	 */
	public OwnedStock getStockInList(String symbol){
		OwnedStock oStock = new DefaultOwnedStock(symbol);
		return getStockInList(oStock);
	}
	
	/**
	 * Get a stock from the list, based on the index number.
	 * 
	 * @param index the index number of the stock in the list.
	 * @return the stock from the list.
	 * @throws IndexOutOfBoundsException if the index parameter is not a valid
	 * index for the stock list.
	 */
	public OwnedStock getStockInList(int index){
		OwnedStock result = null;
		synchronized(this){
			result = stockList.get(index);
		}
		
		return result;
	}
	
	/**
	 * Determines if the specified stock exists in the list.
	 * 
	 * @param stock the stock to check agains the list.
	 * @return true if the stock exists in the list.
	 */
	public boolean isStockInList(OwnedStock stock){
		boolean result = false;
		synchronized(this){
			result = stockList.contains(stock);
		}
		return result;
	}
	
	/**
	 * Get the current size of the stock list. This is the 
	 * number of stocks owned by this portfolio.
	 * 
	 * @return the size of the stock list.
	 */
	public synchronized int getStockListSize(){
		return stockList.size();
	}
	
	/**
	 * Calculate the total value of all stocks. This
	 * method is called every time the stock list is changed.
	 */
	private void calculateTotalStockValue(){
		BigDecimal total = new BigDecimal(0);
		synchronized(this){
			if(stockList != null){
				for(OwnedStock s : stockList){
					total = total.add(s.getTotalValue());
				}
			}
		}
		
		setTotalStockValue(total);
	}
	
	/**
	 * Calculate the net worth of the portfolio. This
	 * method is called any time a value that affects 
	 * the net worth is changed.
	 */
	private void calculateNetWorth(){
		BigDecimal net = null;
		synchronized(this){
			if(cashBalance != null && totalStockValue != null){
				net = cashBalance.add(totalStockValue);
			}
		}
		
		if(net != null){
			setNetWorth(net);
		}
	}
	
	/**
	 * Calculate the change in the net worth of this portfolio.
	 * This method is invoked every time the net worth changes.
	 */
	private void calculateChangeInNetWorth(){
		BigDecimal change = null;
		synchronized(this){
			if(netWorth!= null && initialValue != null){
				change = netWorth.subtract(initialValue);
			}
		}
		
		if(change != null){
			setChangeInNetWorth(change);
		}
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
	
	/**
	 * Get the initial value of this portfolio.
	 * 
	 * @return the initial value of this portfolio.
	 */
	public synchronized BigDecimal getInitialValue(){
		return initialValue;
	}
	
	@Override
	public synchronized String toString(){
		return portfolioName != null ? portfolioName : "Portfolio";
	}

}
