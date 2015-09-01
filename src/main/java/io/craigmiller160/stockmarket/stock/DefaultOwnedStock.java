package io.craigmiller160.stockmarket.stock;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * This implementation of the <tt>OwnedStock</tt> interface extends the 
 * <tt>DefaultStock</tt> class and builds on it with the properties
 * of the new interface. This class is intended to be used either as 
 * a decorator around an <tt>AbstractStock</tt> implementation, or
 * as a stand-alone class instantiated with a stock's symbol passed to it.
 * <p>
 * The main interaction with this class is through the <tt>increaseShares()</tt>
 * and <tt>decreaseShares()</tt> methods, which adjust all the values of this
 * class. Private setter methods do exist, however, to facilitate compatibility
 * with the <tt>StockFileDownloader</tt> class, which uses the stock downloading
 * process to load values into this class from a saved source.
 * <p>
 * The <tt>Map</tt> returned by the <tt>getValueMap()</tt> method has been altered to add
 * additional fields to it: QUANTITY_OF_SHARES, TOTAL_VALUE, and NET. Constants have been
 * added to this class that represent the keys for these new entries in the map.
 * <p>
 * <b>THREAD SAFETY:</b> This class is nearly entirely thread safe. All mutable state
 * is guarded by this class's intrinsic lock, as all of the state fields are interconnected.
 * <p>
 * The only potential hole in this class's thread safety is the <tt>StockDownloader</tt> object
 * it depends on. If the downloader used in conjunction with this class is not thread safe, then
 * the thread safety of this entire class could be compromised. If, however, it is thread safe, 
 * then this class can be properly relied upon in a concurrent environment.
 * 
 * @author craig
 * @version 2.0
 * @see io.craigmiller160.stockmarket.stock.StockDownloader StockDownloader
 * @see io.craigmiller160.stockmarket.stock.StockFileDownloader StockFileDownloader
 */
@ThreadSafe
public class DefaultOwnedStock extends DefaultStock implements OwnedStock{

	/**
	 * SerialVersionUID for serialization support.
	 */
	private static final long serialVersionUID = -1417312411285855441L;
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.stock.OwnedStock");
	
	/**
	 * Private <tt>NumberFormat</tt> used for formatting <tt>BigDecimal</tt> values
	 * for the logger.
	 */
	private static final NumberFormat moneyFormat = new DecimalFormat("$###,###,###,##0.00");
	
	/**
	 * Quantity of shares owned of this stock.
	 */
	@GuardedBy("this")
	private int quantityOfShares;
	
	/**
	 * The amount of money originally spent in total on all shares of this stock.
	 * When shares are bought, it is increased by the raw amount spent on the new
	 * shares. When shares are sold, it is decreased by a percentage equal to the
	 * percentage of total shares sold.
	 */
	@GuardedBy("this")
	private BigDecimal principle;
	
	/**
	 * The total combined value of all shares of the stock.
	 */
	@GuardedBy("this")
	private BigDecimal totalValue;
	
	/**
	 * The net value of the stock, how much it has gained/lost since
	 * purchase.
	 */
	@GuardedBy("this")
	private BigDecimal net;
	
	/**
	 * Constructs a <tt>DefaultOwnedStock</tt> defined by a stock symbol
	 * parameter.
	 * 
	 * @param symbol the symbol of the stock.
	 */
	public DefaultOwnedStock(String symbol){
		super(symbol);
		principle = new BigDecimal(0);
		totalValue = new BigDecimal(0);
		net = new BigDecimal(0);
	}
	
	/**
	 * Constructs a <tt>DefaultOwnedStock</tt> wrapping around an existing <tt>AbstractStock</tt>. 
	 * 
	 * @param stock the stock to wrap around.
	 */
	public DefaultOwnedStock(AbstractStock stock) {
		this(stock.getSymbol());
	}
	
	@Override
	public String getSymbol(){
		return super.getSymbol();
	}
	
	@Override
	public BigDecimal getCurrentPrice(){
		return super.getCurrentPrice();
	}
	
	/**
	 * Sets the quantity field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param quantity the quantity of shares.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setQuantityOfShares(String quantity){
		int num = Integer.parseInt(quantity);
		synchronized(this){
			this.quantityOfShares = num;
		}
	}
	
	/**
	 * Sets the quantity field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param quantity the quantity of shares owned of this stock.
	 */
	protected synchronized void setQuantityOfShares(int quantity){
		this.quantityOfShares = quantity;
	}
	
	/**
	 * Sets the principle field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param principle the principle, the initial amount spent on this stock.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setPrinciple(String principle){
		BigDecimal num = new BigDecimal(Double.parseDouble(principle));
		synchronized(this){
			this.principle = num;
		}
	}
	
	/**
	 * Sets the principle field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param principle the principle, the initial amount spent on the stock.
	 */
	protected synchronized void setPrinciple(BigDecimal principle){
		this.principle = principle;
	}
	
	/**
	 * Sets the total value field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param totalValue the total value of all shares of this stock.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setTotalValue(String totalValue){
		BigDecimal num = new BigDecimal(Double.parseDouble(totalValue));
		synchronized(this){
			this.totalValue = num;
		}
	}
	
	/**
	 * Sets the total value field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param totalValue the totalValue of all shares of the stock.
	 */
	protected synchronized void setTotalValue(BigDecimal totalValue){
		this.totalValue = totalValue;
	}
	
	/**
	 * Sets the net field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param net the net gains/losses on this stock.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setNet(String net){
		BigDecimal num = new BigDecimal(Double.parseDouble(net));
		synchronized(this){
			this.net = num;
		}
	}
	
	/**
	 * Sets the net field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param net the net gains/losses on this stock.
	 */
	protected synchronized void setNet(BigDecimal net){
		this.net = net;
	}
	
	@Override
	public void addShares(int quantity){
		//getCurrentPrice() is synchronized, no need for additional synchronization here
		BigDecimal valueToAdd = getCurrentPrice().multiply(new BigDecimal(quantity));
		
		if(getCurrentPrice() == null){
			throw new IllegalStateException(
					"Stock details must be set before "
					+ "adding/subtracting shares");
		}
		
		LOGGER.logp(Level.FINEST, this.getClass().getName(), "increaseShares()", 
				symbol + ": Added Quantity: " + quantity + " Value To Add: " 
				+ moneyFormat.format(valueToAdd));
		
		synchronized(this){
			quantityOfShares += quantity;
			principle = principle.add(valueToAdd);
		}
		
		setTotalValueAndNet();
	}
	
	@Override
	public boolean subtractShares(int quantity){
		boolean sharesRemaining = true;
		
		if(getCurrentPrice() == null){
			throw new IllegalStateException(
					"Stock details must be set before "
					+ "adding/subtracting shares");
		}
		
		BigDecimal valueToSubtractFromPrinciple = null;
		synchronized(this){
			if(quantity > quantityOfShares){
				throw new InsufficientSharesException(
						"Only own " + quantityOfShares 
						+ ", not enough to subtract " + quantity);
			}
			
			valueToSubtractFromPrinciple = principle.multiply(
					new BigDecimal(quantity / quantityOfShares));
		}
		
		//TODO might be able to get rid of this value here, I don't think I need it. Setting
		//the total in a completely separate way. If the method words with this commented out,
		//then remove it.
		//getCurrentPrice() method is synchronized, no need for additional synchronization here
		//BigDecimal valueToSubtractFromTotal = getCurrentPrice().multiply(new BigDecimal(quantity));
		
		LOGGER.logp(Level.FINEST, this.getClass().getName(), "decreaseShares()", 
				symbol + ": Subtracted Quantity: " + quantity + " Value to Subtract From Principle: " 
				+ moneyFormat.format(valueToSubtractFromPrinciple));
		
		synchronized(this){
			quantityOfShares -= quantity;
			principle = principle.subtract(valueToSubtractFromPrinciple);
		}
		
		setTotalValueAndNet();
		
		synchronized(this){
			if(quantityOfShares > 0){
				sharesRemaining = true;
			}
			else{
				sharesRemaining = false;
			}
		}
		
		return sharesRemaining;
	}
	
	/**
	 * Set the totalValue and net fields. This method should be invoked only after the conclusion
	 * of an increase/decrease operation.
	 */
	private void setTotalValueAndNet(){
		BigDecimal tempTotalValue = null;
		int tempQuantity = 0;
		BigDecimal tempNet = null;
		BigDecimal currentPrice = getCurrentPrice(); //getCurrentPrice() is synchronized already
		synchronized(this){
			totalValue = currentPrice.multiply(new BigDecimal(quantityOfShares));
			net = totalValue.subtract(principle);
			
			tempQuantity = quantityOfShares;
			tempTotalValue = totalValue;
			tempNet = net;
		}
		
		LOGGER.logp(Level.FINEST, this.getClass().getName(), "setTotalValueAndNet()", 
				symbol + ": Quantity of Shares: " + tempQuantity + " Total Value: " 
				+ moneyFormat.format(tempTotalValue) + " Net: " + moneyFormat.format(tempNet));
	}
	
	@Override
	public synchronized int getQuantityOfShares(){
		return quantityOfShares;
	}
	
	@Override
	public synchronized BigDecimal getTotalValue(){
		return totalValue;
	}
	
	@Override
	public synchronized BigDecimal getNet(){
		return net;
	}
	
	@Override
	public synchronized BigDecimal getPrinciple(){
		return principle;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>StockFileDownloader:</b> This downloader can be used with this method 
	 * as a way of updating <tt>OwnedStock</tt> properties after loading a saved 
	 * portfolio. This type of <tt>StockDownloader</tt> should only
	 * be used with <tt>fullDetails</tt> set to false, as it only contains a subset
	 * of the stock data.
	 */
	@Override
	public void setStockDetails(StockDownloader downloader, boolean fullDetails)
			throws InvalidStockException, UnknownHostException, IOException {
		super.setStockDetails(downloader, fullDetails);
		if(downloader instanceof StockFileDownloader){
			Map<String,String> valueMap = downloader.downloadStockDetails(symbol, null);
			
			synchronized(this){
				setQuantityOfShares(valueMap.get(QUANTITY_OF_SHARES));
				setPrinciple(valueMap.get(PRINCIPLE));
				setTotalValue(valueMap.get(TOTAL_VALUE));
				setNet(valueMap.get(NET));
			}
			
			LOGGER.logp(Level.INFO, this.getClass().getName(), 
					"setStockDetails", "StockFileDownloader has set owned values");
			
		}
		else{
			setTotalValueAndNet();
		}
	}
	
	@Override
	protected Map<String, Object> getModifiableValueMap(boolean fullDetails){
		Map<String,Object> valueMap = super.getModifiableValueMap(fullDetails);
		
		synchronized(this){
			valueMap.put(QUANTITY_OF_SHARES, getQuantityOfShares());
			valueMap.put(TOTAL_VALUE, getTotalValue());
			valueMap.put(NET, getNet());
			valueMap.put(PRINCIPLE, getPrinciple());
		}
		
		return valueMap;
	}

	@Override
	public void addShares(OwnedStock stock) {
		if(!this.equals(stock)){
			throw new IllegalArgumentException(this.getSymbol() 
					+ " != " + stock.getSymbol());
		}
		
		if(stock.getCurrentPrice() == null || 
				stock.getCurrentPrice().equals(new BigDecimal(0))){
			throw new IllegalStateException("Stock parameter details not set");
		}
		
		setCurrentPrice(stock.getCurrentPrice());
		addShares(stock.getQuantityOfShares());
	}

	@Override
	public boolean subtractShares(OwnedStock stock) {
		boolean sharesRemaining = true;
		
		if(!this.equals(stock)){
			throw new IllegalArgumentException(this.getSymbol() 
					+ " != " + stock.getSymbol());
		}
		
		if(stock.getCurrentPrice() == null || 
				stock.getCurrentPrice().equals(new BigDecimal(0))){
			throw new IllegalStateException("Stock parameter details not set");
		}
		
		setCurrentPrice(stock.getCurrentPrice());
		sharesRemaining = subtractShares(stock.getQuantityOfShares());
		
		return sharesRemaining;
	}

}
