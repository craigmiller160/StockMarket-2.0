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
	 * Copy constructor for <tt>DefaultOwnedStock</tt>. Creates a 
	 * deep copy of the stock parameter. Accepts all kinds of 
	 * stock objects, and parses the object along the way to check
	 * for which fields to set.
	 * <p>
	 * <b>NOTE:</b> This does NOT guarantee that all fields
	 * of this class will have a value. Whether a field will
	 * have a value or be null is determined entirely by
	 * whether is has a value or is null in the stock
	 * parameter being copied. In addition, this constructor
	 * accepts <tt>Stock</tt> objects that are NOT 
	 * <tt>OwnedStock</tt>s. If that is the case, all the 
	 * <tt>OwnedStock</tt> values will be set to 0. 
	 * 
	 * @param stock the stock to copy to this class.
	 */
	public DefaultOwnedStock(Stock stock) {
		super(stock);
		if(stock instanceof OwnedStock){
			copyStock((OwnedStock) stock);
		}
		else{
			principle = new BigDecimal(0);
			totalValue = new BigDecimal(0);
			net = new BigDecimal(0);
		}
	}
	
	/**
	 * Stock copying method. The values of the stock
	 * parameter, if there are any, will be set to 
	 * this class.
	 * 
	 * @param stock the stock to be copied.
	 */
	private void copyStock(OwnedStock stock){
		//If quantity == 0, then 
		if(stock.getQuantityOfShares() > 0){
			setQuantityOfShares(stock.getQuantityOfShares());
			setPrinciple(stock.getPrinciple());
			setTotalValue(stock.getTotalValue());
			setNet(stock.getNet());
		}
		else{
			principle = new BigDecimal(0);
			totalValue = new BigDecimal(0);
			net = new BigDecimal(0);
		}
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
	 * @param rawText the text for the quantity of shares.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 * @throws IllegalArgumentException if the value passed to this method is 
	 * less than 0.
	 */
	private void setQuantityOfShares(String rawText){
		int num = Integer.parseInt(rawText);
		if(num < 0){
			throw new IllegalArgumentException("Quantity can't be less than 0: " + num);
		}
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
	 * @throws IllegalArgumentException if the value passed to this method is 
	 * less than 0.
	 */
	protected void setQuantityOfShares(int quantity){
		if(quantity < 0){
			throw new IllegalArgumentException("Quantity can't be less than 0: " + quantity);
		}
		synchronized(this){
			this.quantityOfShares = quantity;
		}
	}
	
	/**
	 * Sets the principle field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the text for the principle, the initial amount spent on this stock.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 * @throws IllegalArgumentException if the value passed to this method is 
	 * less than 0.
	 */
	private void setPrinciple(String rawText){
		BigDecimal num = new BigDecimal(Double.parseDouble(rawText));
		if(num.compareTo(new BigDecimal(0)) < 0){
			throw new IllegalArgumentException("Principle can't be less than 0: " + num);
		}
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
	 * @throws IllegalArgumentException if the value passed to this method is 
	 * less than 0.
	 */
	protected void setPrinciple(BigDecimal principle){
		if(principle.compareTo(new BigDecimal(0)) < 0){
			throw new IllegalArgumentException("Principle can't be less than 0: " + principle);
		}
		synchronized(this){
			this.principle = principle;
		}
	}
	
	/**
	 * Sets the total value field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the text for the total value of all shares of this stock.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 * @throws IllegalArgumentException if the value passed to this method is 
	 * less than 0.
	 */
	private void setTotalValue(String rawText){
		BigDecimal num = new BigDecimal(Double.parseDouble(rawText));
		if(num.compareTo(new BigDecimal(0)) < 0){
			throw new IllegalArgumentException("Total Value can't be less than 0: " + num);
		}
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
	 * @throws IllegalArgumentException if the value passed to this method is 
	 * less than 0.
	 */
	protected void setTotalValue(BigDecimal totalValue){
		if(totalValue.compareTo(new BigDecimal(0)) < 0){
			throw new IllegalArgumentException("Total Value can't be less than 0: " + totalValue);
		}
		this.totalValue = totalValue;
	}
	
	/**
	 * Sets the net field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the text for the net gains/losses on this stock.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setNet(String rawText){
		BigDecimal num = new BigDecimal(Double.parseDouble(rawText));
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
	protected void setNet(BigDecimal net){
		synchronized(this){
			this.net = net;
		}
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
	public BigDecimal subtractShares(int quantity){
		if(getCurrentPrice() == null){
			throw new IllegalStateException(
					"Stock details must be set before "
					+ "adding/subtracting shares");
		}
		
		BigDecimal valueToSubtract = null;
		BigDecimal valueOfShares = null;
		synchronized(this){
			if(quantity > quantityOfShares){
				throw new InsufficientSharesException(
						"Only own " + quantityOfShares 
						+ ", not enough to subtract " + quantity);
			}
			
			double quantityD = quantity;
			double quantityOfSharesD = quantityOfShares;
			
			valueToSubtract = principle.multiply(
					new BigDecimal(quantityD / quantityOfSharesD));
			
			valueOfShares = getCurrentPrice().multiply(new BigDecimal(quantity));
		}
		
		LOGGER.logp(Level.FINEST, this.getClass().getName(), "decreaseShares()", 
				symbol + ": Subtracted Quantity: " + quantity + " Value to Subtract From Principle: " 
				+ moneyFormat.format(valueToSubtract));
		
		synchronized(this){
			quantityOfShares -= quantity;
			principle = principle.compareTo(valueToSubtract) >= 0 ? principle.subtract(valueToSubtract) : new BigDecimal(0);
		}
		
		setTotalValueAndNet();
		
		return valueOfShares;
	}
	
	/**
	 * Set the totalValue and net fields. This method should be invoked only after the conclusion
	 * of an increase/decrease operation.
	 */
	private void setTotalValueAndNet(){
		BigDecimal currentPrice = getCurrentPrice(); //getCurrentPrice() is synchronized already
		synchronized(this){
			//If quantity == 0, then there's no total value or net
			if(quantityOfShares > 0){
				totalValue = currentPrice.multiply(new BigDecimal(quantityOfShares));
				net = totalValue.subtract(principle);
			}
			else{
				totalValue = new BigDecimal(0);
				net = new BigDecimal(0);
			}
		}
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
	public BigDecimal subtractShares(OwnedStock stock) {
		BigDecimal valueOfShares = null;
		if(!this.equals(stock)){
			throw new IllegalArgumentException(this.getSymbol() 
					+ " != " + stock.getSymbol());
		}
		
		if(stock.getCurrentPrice() == null || 
				stock.getCurrentPrice().equals(new BigDecimal(0))){
			throw new IllegalStateException("Stock parameter details not set");
		}
		
		setCurrentPrice(stock.getCurrentPrice());
		valueOfShares = subtractShares(stock.getQuantityOfShares());
		
		return valueOfShares;
	}

}
