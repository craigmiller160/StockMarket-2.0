package io.craigmiller160.stockmarket.stock;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Basic stock object with a range of fields that are filled by data from a <tt>StockDownloader</tt> class.
 * The values of the various fields start out as <tt>null</tt> until a command is issued to download data
 * and set their values for the first time.
 * <p>
 * <b>THREAD SAFETY:</b> This class is MOSTLY thread safe. To clarify, all state fields in this class are
 * guarded by this class's intrinsic lock. The <tt>setStockDetails()</tt> method is synchronized,
 * as are all the private setter methods it calls on (those setters are only available to be called on
 * by that method, so all the fields can only be altered by a call to that method). All but two of the
 * fields are immutable objects and can be safely published through the getter methods, but the getters
 * are synchronized as well to avoid memory consistency issues. The two non-immutable object fields
 * have copies of their value returned by their respective getters.
 * <p>
 * <tt>getStockHistory()</tt> and <tt>getModifiableValueMap()</tt> (and its final counterpart from 
 * the superclass, <tt>getValueMap()</tt>) are not directly synchronized. However, the value map
 * is created by calling on the various getters, all of which are synchronized, so access to the fields
 * is still guarded by the lock. Meanwhile, the stock history is created without calling upon any
 * mutable state variables, and therefore offers no risk to the object's state or memory consistency,
 * and can be invoked without needing synchronization.
 * <p>
 * <b>HOWEVER</b>, the one potential hole in this class's thread safety lies in the <tt>StockDownloader</tt>.
 * The thread safety of the downloader object could ultimately compromise the thread safety of this 
 * class. When using this class in a multi-threaded environment, always ensure that the downloader
 * class being used is thread safe, and if it isn't then take appropriate measures to compensate.
 * 
 * @author craig
 * @version 2.0
 * @see io.craigmiller160.stockmarket.stock.StockDownloader
 */
@ThreadSafe
public class DefaultStock extends AbstractStock {

	/**
	 * SerialVersionUID for serialization support.
	 */
	private static final long serialVersionUID = 7534553580704011399L;
	
	/**
	 * Logger object for this class.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.stock.Stock");
	
	/**
	 * Company Name field.
	 */
	@GuardedBy("this")
	private String companyName;
	
	/**
	 *  The change in price during the last day of trading.
	 */
	@GuardedBy("this")
	private BigDecimal changeToday;
	
	/**
	 * The change in price during the last day of trading in percent.
	 */
	@GuardedBy("this")
	private BigDecimal changeTodayInPercent;
	
	/**
	 * Fifty Day Average price field.
	 */
	@GuardedBy("this")
	private BigDecimal fiftyDayAvg;
	
	/**
	 * Change in 50 Day Average price
	 */
	@GuardedBy("this")
	private BigDecimal change50DayAvg;
	
	/**
	 * Change in 50 Day Average price percent.
	 */
	@GuardedBy("this")
	private BigDecimal change50DayAvgPercent;
	
	/**
	 * 200 Day Average price.
	 */
	@GuardedBy("this")
	private BigDecimal twoHundredDayAvg;
	
	/**
	 * Change in 200 Day Average price.
	 */
	@GuardedBy("this")
	private BigDecimal change200DayAvg;
	
	/**
	 * Change in 200 Day Average Price percent.
	 */
	@GuardedBy("this")
	private BigDecimal change200DayAvgPercent;
	
	/**
	 * Year High price.
	 */
	@GuardedBy("this")
	private BigDecimal yearHigh;
	
	/**
	 * Change in price from Year High.
	 */
	@GuardedBy("this")
	private BigDecimal changeYearHigh;
	
	/**
	 * Change in price from Year High in percent.
	 */
	@GuardedBy("this")
	private BigDecimal changeYearHighPercent;
	
	/**
	 * Year Low price.
	 */
	@GuardedBy("this")
	private BigDecimal yearLow;
	
	/**
	 * Change in price from Year Low.
	 */
	@GuardedBy("this")
	private BigDecimal changeYearLow;
	
	/**
	 * Change in price from Year Low in percent.
	 */
	@GuardedBy("this")
	private BigDecimal changeYearLowPercent;
	
	/**
	 * Current price.
	 */
	@GuardedBy("this")
	private BigDecimal currentPrice;
	
	/**
	 * Last date the stock was traded.
	 */
	@GuardedBy("this")
	private Calendar lastTradeDate;
	
	/**
	 * Last time the stock was traded on the last date it was traded.
	 */
	@GuardedBy("this")
	private Calendar lastTradeTime;
	
	/**
	 * Constructs a stock based on the provided symbol.
	 * 
	 * @param symbol the marketplace symbol of the stock.
	 */
	public DefaultStock(String symbol) {
		super(symbol);
	}
	
	/**
	 * Copy constructor for <tt>DefaultStock</tt>. Creates a 
	 * deep copy of the stock parameter.
	 * <p>
	 * <b>NOTE:</b> This does NOT guarantee that all fields
	 * of this class will have a value. Whether a field will
	 * have a value or be null is determined entirely by
	 * whether is has a value or is null in the stock
	 * parameter being copied.
	 * 
	 * @param stock the stock to copy to this class.
	 */
	public DefaultStock(Stock stock){
		super(stock.getSymbol());
		copyStock(stock);
	}
	
	/**
	 * Stock deep copying method. Does several checks to confirm that
	 * the fields of the stock parameter have values, and then copies
	 * those values to the appropriate fields of this class.
	 * 
	 * @param stock the stock to copy.
	 */
	private void copyStock(Stock stock){
		//If price == null, details not set. No values will exist, all should
		//be set to defaults.
		if(stock.getCurrentPrice() != null){
			setCurrentPrice(stock.getCurrentPrice());
			//If DefaultStock, set DefaultStock values
			if(stock instanceof DefaultStock){
				setCompanyName(((DefaultStock) stock).getCompanyName());
				//If changeToday == null, fullDetails not set, so these values should be left null
				if(((DefaultStock) stock).getChangeToday() != null){
					setChangeToday(((DefaultStock) stock).getChangeToday());
					setChangeTodayPercent(((DefaultStock) stock).getChangeTodayPercent());
					setFiftyDayAvg(((DefaultStock) stock).getFiftyDayAvg());
					setChange50DayAvg(((DefaultStock) stock).getChange50DayAvg());
					setChange50DayAvgPercent(((DefaultStock) stock).getChange50DayAvgPercent());
					setTwoHundredDayAvg(((DefaultStock) stock).getTwoHundredDayAvg());
					setChange200DayAvg(((DefaultStock) stock).getChange200DayAvg());
					setChange200DayAvgPercent(((DefaultStock) stock).getChange200DayAvgPercent());
					setYearHigh(((DefaultStock) stock).getYearHigh());
					setChangeYearHigh(((DefaultStock) stock).getChangeYearHigh());
					setChangeYearHighPercent(((DefaultStock) stock).getChangeYearHighPercent());
					setYearLow(((DefaultStock) stock).getYearLow());
					setChangeYearLow(((DefaultStock) stock).getChangeYearLow());
					setChangeYearLowPercent(((DefaultStock) stock).getChangeYearLowPercent());
					setLastTradeDate(((DefaultStock) stock).getLastTradeDate());
					setLastTradeTime(((DefaultStock) stock).getLastTradeTime());
				}
			}
		}
	}
	
	@Override
	public void setStockDetails(StockDownloader downloader, boolean fullDetails) 
			throws InvalidStockException, UnknownHostException, IOException {
		
		String[] fields = null;
		if(fullDetails){
			fields = new String[20];
			fields[0] = NAME;
			fields[1] = CURRENT_PRICE;
			fields[2] = CHANGE_TODAY;
			fields[3] = CHANGE_TODAY_PERCENT;
			fields[4] = FIFTY_DAY_AVG;
			fields[5] = CHANGE_50_DAY_AVG;
			fields[6] = CHANGE_50_DAY_AVG_PERCENT;
			fields[7] = TWO_HUNDRED_DAY_AVG;
			fields[8] = CHANGE_200_DAY_AVG;
			fields[9] = CHANGE_200_DAY_AVG_PERCENT;
			fields[10] = YEAR_HIGH;
			fields[11] = CHANGE_YEAR_HIGH;
			fields[12] = CHANGE_YEAR_HIGH_PERCENT;
			fields[13] = YEAR_LOW;
			fields[14] = CHANGE_YEAR_LOW;
			fields[15] = CHANGE_YEAR_LOW_PERCENT;
			fields[16] = LAST_TRADE_DATE;
			fields[17] = LAST_TRADE_TIME;
			fields[18] = DAYS_HIGH;
			fields[19] = DAYS_LOW;
		}
		else{
			fields = new String[2];
			fields[0] = NAME;
			fields[1] = CURRENT_PRICE;
		}
		
		LOGGER.logp(Level.INFO, this.getClass().getName(), "setStockDetails()", 
				symbol + ": Downloading stock details. Full download: " + fullDetails);
		
		Map<String,String> stockDataMap = downloader.downloadStockDetails(symbol, fields);
		
		synchronized(this){
			setCompanyName(stockDataMap.get(NAME));
			setCurrentPrice(stockDataMap.get(CURRENT_PRICE));
			if(fullDetails){
				setChangeToday(stockDataMap.get(CHANGE_TODAY));
				setChangeTodayPercent(stockDataMap.get(CHANGE_TODAY_PERCENT));
				setFiftyDayAvg(stockDataMap.get(FIFTY_DAY_AVG));
				setChange50DayAvg(stockDataMap.get(CHANGE_50_DAY_AVG));
				setChange50DayAvgPercent(stockDataMap.get(CHANGE_50_DAY_AVG_PERCENT));
				setTwoHundredDayAvg(stockDataMap.get(TWO_HUNDRED_DAY_AVG));
				setChange200DayAvg(stockDataMap.get(CHANGE_200_DAY_AVG));
				setChange200DayAvgPercent(stockDataMap.get(CHANGE_200_DAY_AVG_PERCENT));
				setYearHigh(stockDataMap.get(YEAR_HIGH));
				setChangeYearHigh(stockDataMap.get(CHANGE_YEAR_HIGH));
				setChangeYearHighPercent(stockDataMap.get(CHANGE_YEAR_HIGH_PERCENT));
				setYearLow(stockDataMap.get(YEAR_LOW));
				setChangeYearLow(stockDataMap.get(CHANGE_YEAR_LOW));
				setChangeYearLowPercent(stockDataMap.get(CHANGE_YEAR_LOW_PERCENT));
				setLastTradeDate(stockDataMap.get(LAST_TRADE_DATE));
				setLastTradeTime(stockDataMap.get(LAST_TRADE_TIME));
			}
		}
		
	}
	
	@Override
	public List<HistoricalQuote> getStockHistory(StockDownloader downloader, 
			int months) throws InvalidStockException, UnknownHostException, IOException{
		LOGGER.logp(Level.INFO, this.getClass().getName(), "getStockHistory()", 
				symbol + ": Downloading stock history for " + months + " months");
		return super.getStockHistory(downloader, months);
	}
	
	
	/**
	 * Sets the company name field.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 */
	private void setCompanyName(String rawText){
		synchronized(this){
			this.companyName = rawText;
		}
	}
	
	/**
	 * Sets the change today field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setChangeToday(String rawText){
		double value = Double.parseDouble(rawText);
		synchronized(this){
			this.changeToday = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the change today field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param changeToday the change in price today.
	 */
	protected void setChangeToday(BigDecimal changeToday){
		synchronized(this){
			this.changeToday = changeToday;
		}
	}
	
	/**
	 * Sets the change today in percent field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setChangeTodayPercent(String rawText){
		double value = Double.parseDouble(rawText);
		synchronized(this){
			this.changeTodayInPercent = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the change today in percent field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param changeTodayPercent the percent change in price today.
	 */
	protected void setChangeTodayPercent(BigDecimal changeTodayPercent){
		synchronized(this){
			this.changeTodayInPercent = changeTodayPercent;
		}
	}
	
	/**
	 * Sets the 50 day average field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 * @throws IllegalArgumentException if the value passed to this method is 
	 * less than 0.
	 */
	private void setFiftyDayAvg(String rawText){
		double value = Double.parseDouble(rawText);
		if(value < 0){
			throw new IllegalArgumentException("50 Day Avg cannot be less than 0: " + value);
		}
		synchronized(this){
			this.fiftyDayAvg = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the 50 day average field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param fiftyDayAvg the 50 day average price.
	 * @throws IllegalArgumentException if the value passed to this method is 
	 * less than 0.
	 */
	protected void setFiftyDayAvg(BigDecimal fiftyDayAvg){
		if(fiftyDayAvg.compareTo(new BigDecimal(0)) < 0){
			throw new IllegalArgumentException("50 Day Avg cannot be less than 0: " + fiftyDayAvg);
		}
		synchronized(this){
			this.fiftyDayAvg = fiftyDayAvg;
		}
	}
	
	/**
	 * Sets the change from 50 day average field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setChange50DayAvg(String rawText){
		double value = Double.parseDouble(rawText);
		synchronized(this){
			this.change50DayAvg = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the change from 50 day average field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param change50DayAvg the change from 50 day average price.
	 */
	protected void setChange50DayAvg(BigDecimal change50DayAvg){
		synchronized(this){
			this.change50DayAvg = change50DayAvg;
		}
	}
	
	/**
	 * Sets the sets change from 50 day average in percent field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setChange50DayAvgPercent(String rawText){
		double value = Double.parseDouble(rawText);
		synchronized(this){
			this.change50DayAvgPercent = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the percent change from 50 day average field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param change50DayAvgPercent the percent change from 50 day average price.
	 */
	protected void setChange50DayAvgPercent(BigDecimal change50DayAvgPercent){
		synchronized(this){
			this.change50DayAvgPercent = change50DayAvgPercent;
		}
	}
	
	/**
	 * Sets the 200 day average field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 * @throws IllegalArgumentException if the value passed to this method is 
	 * less than 0.
	 */
	private void setTwoHundredDayAvg(String rawText){
		double value = Double.parseDouble(rawText);
		if(value < 0){
			throw new IllegalArgumentException("200 Day Avg cannot be less than 0: " + value);
		}
		synchronized(this){
			this.twoHundredDayAvg = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the 200 day average field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param twoHundredDayAvg the 200 day average price.
	 * @throws IllegalArgumentException if the value passed to this method is 
	 * less than 0.
	 */
	protected void setTwoHundredDayAvg(BigDecimal twoHundredDayAvg){
		if(twoHundredDayAvg.compareTo(new BigDecimal(0)) < 0){
			throw new IllegalArgumentException("200 Day Avg cannot be less than 0: " + twoHundredDayAvg);
		}
		synchronized(this){
			this.twoHundredDayAvg = twoHundredDayAvg;
		}
	}
	
	/**
	 * Sets the change from 200 day average field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setChange200DayAvg(String rawText){
		double value = Double.parseDouble(rawText);
		synchronized(this){
			this.change200DayAvg = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the change from 200 day average field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param change200DayAvg the change from 200 day average price.
	 */
	protected void setChange200DayAvg(BigDecimal change200DayAvg){
		synchronized(this){
			this.change200DayAvg = change200DayAvg;
		}
	}
	
	/**
	 * Sets the change from 200 day average in percent field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setChange200DayAvgPercent(String rawText){
		double value = Double.parseDouble(rawText);
		synchronized(this){
			this.change200DayAvgPercent = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the percent change from 200 day average field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param change200DayAvgPercent the percent change from 200 day average price.
	 */
	protected void setChange200DayAvgPercent(BigDecimal change200DayAvgPercent){
		synchronized(this){
			this.change200DayAvgPercent = change200DayAvgPercent;
		}
	}
	
	/**
	 * Sets the year high field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 * @throws IllegalArgumentException if the value passed to this method is 
	 * less than 0.
	 */
	private void setYearHigh(String rawText){
		double value = Double.parseDouble(rawText);
		if(value < 0){
			throw new IllegalArgumentException("Year High cannot be less than 0: " + value);
		}
		synchronized(this){
			this.yearHigh = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the year high field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param yearHigh the year high price.
	 * @throws IllegalArgumentException if the value passed to this method is 
	 * less than 0.
	 */
	protected void setYearHigh(BigDecimal yearHigh){
		if(yearHigh.compareTo(new BigDecimal(0)) < 0){
			throw new IllegalArgumentException("Year High cannot be less than 0: " + yearHigh);
		}
		synchronized(this){
			this.yearHigh = yearHigh;
		}
	}
	
	/**
	 * Sets the change from year high field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setChangeYearHigh(String rawText){
		double value = Double.parseDouble(rawText);
		synchronized(this){
			this.changeYearHigh = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the change from the year high field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param changeYearHigh the change from the year high price.
	 */
	protected void setChangeYearHigh(BigDecimal changeYearHigh){
		synchronized(this){
			this.changeYearHigh = changeYearHigh;
		}
	}
	
	/**
	 * Sets the change from year high in percent field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setChangeYearHighPercent(String rawText){
		double value = Double.parseDouble(rawText);
		synchronized(this){
			this.changeYearHighPercent = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the percent change from the year high field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param changeYearHighPercent the percent change from the year high price.
	 */
	protected void setChangeYearHighPercent(BigDecimal changeYearHighPercent){
		synchronized(this){
			this.changeYearHighPercent = changeYearHighPercent;
		}
	}
	
	/**
	 * Sets the year low field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 * @throws IllegalArgumentException if the value passed to this method is 
	 * less than 0.
	 */
	private void setYearLow(String rawText){
		double value = Double.parseDouble(rawText);
		if(value < 0){
			throw new IllegalArgumentException("Year Low cannot be less than 0: " + value);
		}
		synchronized(this){
			this.yearLow = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the year low field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param yearLow the year low price.
	 * @throws IllegalArgumentException if the value passed to this method is 
	 * less than 0.
	 */
	protected void setYearLow(BigDecimal yearLow){
		if(yearLow.compareTo(new BigDecimal(0)) < 0){
			throw new IllegalArgumentException("Year Low cannot be less than 0: " + yearLow);
		}
		synchronized(this){
			this.yearLow = yearLow;
		}
	}
	
	/**
	 * Sets the change from year low field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setChangeYearLow(String rawText){
		double value = Double.parseDouble(rawText);
		synchronized(this){
			this.changeYearLow = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the change from the year low field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param changeYearLow the change from the year low price.
	 */
	protected void setChangeYearLow(BigDecimal changeYearLow){
		synchronized(this){
			this.changeYearLow = changeYearLow;
		}
	}
	
	/**
	 * Sets the change from year low in percent field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setChangeYearLowPercent(String rawText){
		double value = Double.parseDouble(rawText);
		synchronized(this){
			this.changeYearLowPercent = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the percent change from the year low field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param changeYearLowPercent the percent change from the year low price.
	 */
	protected void setChangeYearLowPercent(BigDecimal changeYearLowPercent){
		synchronized(this){
			this.changeYearLowPercent = changeYearLowPercent;
		}
	}
	
	/**
	 * Sets the current price field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 * @throws IllegalArgumentException if the value passed to this method is 
	 * less than 0.
	 */
	private void setCurrentPrice(String rawText){
		double value = Double.parseDouble(rawText);
		if(value < 0){
			throw new IllegalArgumentException("Current Price cannot be less than 0: " + value);
		}
		synchronized(this){
			this.currentPrice = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the current share price field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param currentPrice the current share price.
	 * @throws IllegalArgumentException if the value passed to this method is 
	 * less than 0.
	 */
	protected void setCurrentPrice(BigDecimal currentPrice){
		if(currentPrice.compareTo(new BigDecimal(0)) < 0){
			throw new IllegalArgumentException("Year Low cannot be less than 0: " + currentPrice);
		}
		synchronized(this){
			this.currentPrice = currentPrice;
		}
	}
	
	/**
	 * Sets the last trade date field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 * @throws IllegalArgumentException if the value passed to this method
	 * does not conform to a valid date.
	 * @throws ArrayIndexOutOfBoundsException if the value passed to this 
	 * method is not a date string with intervals split by "/", and the
	 * resulting array is malformed compared with what is expected.
	 */
	private void setLastTradeDate(String rawText){
		String[] dateArr = rawText.split("/");
		int month = Integer.parseInt(dateArr[0]) - 1;
		int day = Integer.parseInt(dateArr[1]);
		int year = Integer.parseInt(dateArr[2]);
		
		if(month < 0 || month > 11 || 
				day < 0 || day > 31 || 
				year < 1000 || year > 3000){
			throw new IllegalArgumentException(
					String.format("Not a valid date: %1$02d-%2$02d-%3$04d", 
							month, day, year));
		}
		
		synchronized(this){
			this.lastTradeDate = new GregorianCalendar(year, month, day);
		}
	}
	
	/**
	 * Sets the last trade date field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param lastTradeDate the last trade date.
	 */
	protected void setLastTradeDate(Calendar lastTradeDate){
		synchronized(this){
			this.lastTradeDate = lastTradeDate;
		}
	}
	
	/**
	 * Sets the last trade time field. This method is private to only serve as
	 * a helper method for <tt>setStockDetails(StockDownloader,boolean)</tt>.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 * @throws IllegalArgumentException if the value passed to this method
	 * is not a valid time.
	 * @throws ArrayIndexOutOfBoundsException if the value passed to this 
	 * method is not a time string with intervals split by ":", and the
	 * resulting array is malformed compared with what is expected.
	 */
	private void setLastTradeTime(String rawText){
		int hours = 0, minutes = 0;
		String amPM = rawText.substring(rawText.length() - 2, rawText.length());
		String[] timeArr = rawText.substring(0, rawText.length() - 2).split(":");
		if(amPM.equalsIgnoreCase("pm")){
			hours = 12 + Integer.parseInt(timeArr[0]);
			if(hours == 24){
				hours = 12;
			}
		}
		else{
			hours = Integer.parseInt(timeArr[0]);
		}
		minutes = Integer.parseInt(timeArr[1]);
		
		if(hours < 0 || hours >= 24 || minutes < 0 || minutes >= 60){
			throw new IllegalArgumentException(
					String.format("Not a valid time: %1$02d:%2$02d", 
							hours, minutes));
		}
		
		synchronized(this){
			this.lastTradeTime = new GregorianCalendar(1970, 0, 1, hours, minutes);
		}
	}
	
	/**
	 * Sets the last trade time field. This method is protected to allow
	 * this class and subclasses an internal tool to change this value.
	 * Other classes should rely on <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * to update this stock.
	 * 
	 * @param lastTradeTime the last trade time.
	 */
	protected void setLastTradeTime(Calendar lastTradeTime){
		synchronized(this){
			this.lastTradeTime = lastTradeTime;
		}
	}
	
	/**
	 * Returns the company name.
	 * 
	 * @return the company name.
	 */
	public synchronized String getCompanyName(){
		return companyName;
	}
	
	/**
	 * Returns the change.
	 * 
	 * @return the change.
	 */
	public synchronized BigDecimal getChangeToday(){
		return changeToday;
	}
	
	/**
	 * Returns the change in percent.
	 * 
	 * @return the change in percent.
	 */
	public synchronized BigDecimal getChangeTodayPercent(){
		return changeTodayInPercent;
	}
	
	/**
	 * Returns the 50 day average.
	 * 
	 * @return the 50 day average.
	 */
	public synchronized BigDecimal getFiftyDayAvg(){
		return fiftyDayAvg;
	}
	
	/**
	 * Returns the change from 50 day average.
	 * 
	 * @return the change from 50 day average.
	 */
	public synchronized BigDecimal getChange50DayAvg(){
		return change50DayAvg;
	}
	
	/**
	 * Returns the change from 50 day average in percent.
	 * 
	 * @return the change from 50 day average in percent.
	 */
	public synchronized BigDecimal getChange50DayAvgPercent(){
		return change50DayAvgPercent;
	}
	
	/**
	 * Returns the 200 day average.
	 * 
	 * @return the 200 day average.
	 */
	public synchronized BigDecimal getTwoHundredDayAvg(){
		return twoHundredDayAvg;
	}
	
	/**
	 * Returns the change from 200 day average.
	 * 
	 * @return the change from 200 day average.
	 */
	public synchronized BigDecimal getChange200DayAvg(){
		return change200DayAvg;
	}
	
	/**
	 * Returns the change from 200 day average in percent.
	 * 
	 * @return the change from 200 day average in percent.
	 */
	public synchronized BigDecimal getChange200DayAvgPercent(){
		return change200DayAvgPercent;
	}
	
	/**
	 * Returns the year high.
	 * 
	 * @return the year high.
	 */
	public synchronized BigDecimal getYearHigh(){
		return yearHigh;
	}
	
	/**
	 * Returns the change from year high.
	 * 
	 * @return the change from year high.
	 */
	public synchronized BigDecimal getChangeYearHigh(){
		return changeYearHigh;
	}
	
	/**
	 * Returns the change from year high in percent.
	 * 
	 * @return the change from year high in percent.
	 */
	public synchronized BigDecimal getChangeYearHighPercent(){
		return changeYearHighPercent;
	}
	
	/**
	 * Returns the year low.
	 * 
	 * @return the year low.
	 */
	public synchronized BigDecimal getYearLow(){
		return yearLow;
	}
	
	/**
	 * Returns the change from year low.
	 * 
	 * @return the change from year low.
	 */
	public synchronized BigDecimal getChangeYearLow(){
		return changeYearLow;
	}
	
	/**
	 * Returns the change from year low percent.
	 * 
	 * @return the change from year low percent.
	 */
	public synchronized BigDecimal getChangeYearLowPercent(){
		return changeYearLowPercent;
	}
	
	@Override
	public synchronized BigDecimal getCurrentPrice(){
		return currentPrice;
	}
	
	/**
	 * Returns the last trade date.
	 * 
	 * @return the last trade date.
	 */
	public synchronized Calendar getLastTradeDate(){
		Calendar copy = new GregorianCalendar(
				lastTradeDate.get(Calendar.YEAR),
				lastTradeDate.get(Calendar.MONTH),
				lastTradeDate.get(Calendar.DAY_OF_MONTH));
		return copy;
	}
	
	/**
	 * Returns the last trade time.
	 * 
	 * @return the last trade time.
	 */
	public synchronized Calendar getLastTradeTime(){
		Calendar copy = new GregorianCalendar(
				lastTradeTime.get(Calendar.YEAR),
				lastTradeTime.get(Calendar.MONTH),
				lastTradeTime.get(Calendar.DAY_OF_MONTH),
				lastTradeTime.get(Calendar.HOUR_OF_DAY),
				lastTradeTime.get(Calendar.MINUTE));
		return copy;
	}
	
	@Override
	protected Map<String,Object> getModifiableValueMap(boolean fullDetails){
		Map<String,Object> stockValues = new HashMap<>();
		
		synchronized(this){
			stockValues.put(NAME, getCompanyName());
			stockValues.put(CURRENT_PRICE, getCurrentPrice());
			stockValues.put(SYMBOL, getSymbol());
			if(fullDetails){
				stockValues.put(CHANGE_TODAY, getChangeToday());
				stockValues.put(CHANGE_TODAY_PERCENT, getChangeTodayPercent());
				stockValues.put(FIFTY_DAY_AVG, getFiftyDayAvg());
				stockValues.put(CHANGE_50_DAY_AVG, getChange50DayAvg());
				stockValues.put(CHANGE_50_DAY_AVG_PERCENT, getChange50DayAvgPercent());
				stockValues.put(TWO_HUNDRED_DAY_AVG, getTwoHundredDayAvg());
				stockValues.put(CHANGE_200_DAY_AVG, getChange200DayAvg());
				stockValues.put(CHANGE_200_DAY_AVG_PERCENT, getChange200DayAvgPercent());
				stockValues.put(YEAR_HIGH, getYearHigh());
				stockValues.put(CHANGE_YEAR_HIGH, getChangeYearHigh());
				stockValues.put(CHANGE_YEAR_HIGH_PERCENT, getChangeYearHighPercent());
				stockValues.put(YEAR_LOW, getYearLow());
				stockValues.put(CHANGE_YEAR_LOW, getChangeYearLow());
				stockValues.put(CHANGE_YEAR_LOW_PERCENT, getChangeYearLowPercent());
				stockValues.put(LAST_TRADE_DATE, getLastTradeDate());
				stockValues.put(LAST_TRADE_TIME, getLastTradeTime());
			}
		}
		
		return stockValues;
	}

}
