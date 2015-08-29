package stockmarket.stock;

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
 * @see stockmarket.stock.StockDownloader
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
				setChangeTodayInPercent(stockDataMap.get(CHANGE_TODAY_PERCENT));
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
	private synchronized void setCompanyName(String rawText){
		this.companyName = rawText;
	}
	
	/**
	 * Sets the change field.
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
	 * Sets the change in percent field.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setChangeTodayInPercent(String rawText){
		double value = Double.parseDouble(rawText);
		
		synchronized(this){
			this.changeTodayInPercent = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the 50 day average field.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setFiftyDayAvg(String rawText){
		double value = Double.parseDouble(rawText);
		
		synchronized(this){
			this.fiftyDayAvg = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the change from 50 day average field.
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
	 * Sets the sets change from 50 day average in percent field.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setChange50DayAvgPercent(String rawText){
		double value = 0;
		value = Double.parseDouble(rawText);
		this.change50DayAvgPercent = new BigDecimal(value);
	}
	
	/**
	 * Sets the 200 day average field.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setTwoHundredDayAvg(String rawText){
		double value = Double.parseDouble(rawText);
		
		synchronized(this){
			this.twoHundredDayAvg = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the change from 200 day average field.
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
	 * Sets the change from 200 day average in percent field.
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
	 * Sets the year high field.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setYearHigh(String rawText){
		double value = Double.parseDouble(rawText);
		
		synchronized(this){
			this.yearHigh = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the change from year high field.
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
	 * Sets the change from year high in percent field.
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
	 * Sets the year low field.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setYearLow(String rawText){
		double value = Double.parseDouble(rawText);
		
		synchronized(this){
			this.yearLow = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the change from year low field.
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
	 * Sets the change from year low in percent field.
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
	 * Sets the current price field.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setCurrentPrice(String rawText){
		double value = Double.parseDouble(rawText);
		
		synchronized(this){
			this.currentPrice = new BigDecimal(value);
		}
	}
	
	/**
	 * Sets the last trade date field.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setLastTradeDate(String rawText){
		String[] dateArr = rawText.split("/");
		int month = Integer.parseInt(dateArr[0]) - 1;
		int day = Integer.parseInt(dateArr[1]);
		int year = Integer.parseInt(dateArr[2]);
		
		synchronized(this){
			this.lastTradeDate = new GregorianCalendar(year, month, day);
		}
	}
	
	/**
	 * Sets the last trade time field.
	 * 
	 * @param rawText the raw text to be parsed and set to the field.
	 * @throws NumberFormatException if the raw text value wasn't properly
	 * parsed and is not a number value.
	 */
	private void setLastTradeTime(String rawText){
		int hours = 12, minutes = 0;
		String amPM = rawText.substring(rawText.length() - 2, rawText.length());
		String[] timeArr = rawText.substring(0, rawText.length() - 2).split(":");
		if(amPM.equalsIgnoreCase("pm")){
			hours = 12 + Integer.parseInt(timeArr[0]);
		}
		else{
			hours = Integer.parseInt(timeArr[0]);
		}
		minutes = Integer.parseInt(timeArr[1]);
		
		synchronized(this){
			this.lastTradeTime = new GregorianCalendar(1970, 0, 1, hours, minutes);
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
	public synchronized BigDecimal getChangeTodayInPercent(){
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
				stockValues.put(CHANGE_TODAY_PERCENT, getChangeTodayInPercent());
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
