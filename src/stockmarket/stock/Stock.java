package stockmarket.stock;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * An interface defining the basic structure of a stock object.
 * This object represents a real-world stock traded publicly
 * on a stock exchange.
 * <p>
 * Stock objects should not use any public setter methods. Instead,
 * all values should be set via the <tt>setStockDetails()</tt> method.
 * This method accepts a <tt>StockDownloader</tt> as a parameter. 
 * Downloaders are used to load stock information from a specific source,
 * such as the web, rather than allowing the program to set the stock's 
 * values arbitrarily.
 * <p>
 * Public getter methods, however, can and should be used. Abstract method 
 * <tt>getCurrentPrice()</tt> is in this interface because the current price 
 * of a stock is one of its most important attributes, and access to this value
 * will be needed in nearly every conceivable implementation of this interface.
 * <p>
 * The abstract method <tt>getValueMap()</tt> should be implemented in addition
 * to any getter methods. This is a tool to allow for abstract handling of stock
 * objects, as this method (if properly implemented) can be used to retrieve all
 * the values of the implemented stock, without needing a specific reference to
 * the implementation.
 * <p>
 * Lastly, the <tt>getStockHistory()</tt> method has been provided. This method
 * invokes a corresponding method in the <tt>StockDownloader</tt> to retrieve
 * a history of the price of this stock over a set period of time.
 * <p>
 * <b>NOTE ABOUT DOWNLOADERS:</b> <tt>StockDownloader</tt> implementations may not support every
 * field name listed as a constant here. When designing a subclass of <tt>AbstractStock</tt>, be
 * aware of which downloader is likely to be used and which fields are accepted by it. Fields that
 * are not supported by the downloader, but are passed to it as a parameter, will likely throw an 
 * exception.
 * 
 * @author craig
 * @version 2.0
 * @see stockmarket.stock.StockDownloader StockDownloader
 */
public interface Stock {

	/**
	 * After hours change in stock price property name.
	 */
	String AFTER_HOURS_CHANGE = "AfterHoursChange";
	
	/**
	 * Annualized gain in stock price property name.
	 */
	String ANNUALIZED_GAIN = "AnnualizedGain";
	
	/**
	 * Stock's ask value property name.
	 */
	String ASK = "Ask";
	
	/**
	 * The size of the stock's ask property name.
	 */
	String ASK_SIZE = "AskSize";
	
	/**
	 * The stock's average daily volume propery name.
	 */
	String AVERAGE_DAILY_VOLUME = "AverageDailyVolume";
	
	/**
	 * The stock's bid property name.
	 */
	String BID = "Bid";
	
	/**
	 * The size of the stock's bid property name.
	 */
	String BID_SIZE = "BidSize";
	
	/**
	 * The change in price during today's trading property.
	 */
	String CHANGE_TODAY = "ChangeToday";
	
	/**
	 * The change in price during today's trading in percent property name.
	 */
	String CHANGE_TODAY_PERCENT = "ChangeTodayInPercent";
	
	/**
	 * The change from 50 day average property name.
	 */
	String CHANGE_50_DAY_AVG = "Change50DayAvg";
	
	/**
	 * The change from 50 day average in percent property name.
	 */
	String CHANGE_50_DAY_AVG_PERCENT = "Change50DayAvgPerent";
	
	/**
	 * The change from 200 day average property name.
	 */
	String CHANGE_200_DAY_AVG = "Change200DayAvg";
	
	/**
	 * The change from 200 day average in percent property name.
	 */
	String CHANGE_200_DAY_AVG_PERCENT = "Change200DayAvgPercent";
	
	/**
	 * The change from year high property name.
	 */
	String CHANGE_YEAR_HIGH = "ChangeYearHigh";
	
	/**
	 * The change from year high in percent property name.
	 */
	String CHANGE_YEAR_HIGH_PERCENT = "ChangeYearHighPercent";
	
	/**
	 * The change from year low property name.
	 */
	String CHANGE_YEAR_LOW = "ChangeYearLow";
	
	/**
	 * The change from year low in percent property name.
	 */
	String CHANGE_YEAR_LOW_PERCENT = "ChangeYearLowPercent";
	
	/**
	 * The commission property name.
	 */
	String COMMISSION = "Commission";
	
	/**
	 * The current price property name.
	 */
	String CURRENT_PRICE = "CurrentPrice";
	
	/**
	 * The currency property name.
	 */
	String CURRENCY = "Currency";
	
	/**
	 * The days high property name.
	 */
	String DAYS_HIGH = "DaysHigh";
	
	/**
	 * The days low property name.
	 */
	String DAYS_LOW = "DaysLow";
	
	/**
	 * The days range property name.
	 */
	String DAYS_RANGE = "DaysRange";
	
	/**
	 * The dividend pay date property name.
	 */
	String DIVIDEND_PAY_DATE = "DividendPayDate";
	
	/**
	 * The fifty day price average property name.
	 */
	String FIFTY_DAY_AVG = "FiftyDayAvg";
	
	/**
	 * The last trade date property name.
	 */
	String LAST_TRADE_DATE = "LastTradeDate";
	
	/**
	 * The last trade size property name.
	 */
	String LAST_TRADE_SIZE = "LastTradeSize";
	
	/**
	 * The last trade time property name.
	 */
	String LAST_TRADE_TIME = "LastTradeTime";
	
	/**
	 * The market capitalization property name.
	 */
	String MARKET_CAPITALIZATION = "MarketCapitalization";
	
	/**
	 * The company name property name.
	 */
	String NAME = "Name";
	
	/**
	 * The open price property name.
	 */
	String OPEN = "Open";
	
	/**
	 * The PE Ratio property name.
	 */
	String PE_RATIO = "PERatio";
	
	/**
	 * The previous close property name.
	 */
	String PREVIOUS_CLOSE = "PreviousClose";
	
	/**
	 * The price book property name.
	 */
	String PRICE_BOOK = "PriceBook";
	
	/**
	 * The price sales property name.
	 */
	String PRICE_SALES = "PriceSales";
	
	/**
	 * The revenue property name.
	 */
	String REVENUE = "Revenue";
	
	/**
	 * The shares float property name.
	 */
	String SHARES_FLOAT = "SharesFloat";
	
	/**
	 * The shares outstanding property name.
	 */
	String SHARES_OUTSTANDING = "SharesOutstanding";
	
	/**
	 * The short ratio property name.
	 */
	String SHORT_RATIO = "ShortRatio";
	
	/**
	 * The symbol property name.
	 */
	String SYMBOL = "Symbol";
	
	/**
	 * The two hundred day avg property name.
	 */
	String TWO_HUNDRED_DAY_AVG = "TwoHundredDayAvg";
	
	/**
	 * The volume property name.
	 */
	String VOLUME = "Volume";
	
	/**
	 * The year high property name.
	 */
	String YEAR_HIGH = "YearHigh";
	
	/**
	 * The year low property name.
	 */
	String YEAR_LOW = "YearLow";
	
	/**
	 * The year range property name.
	 */
	String YEAR_RANGE = "YearRange";
	
	/**
	 * Get the symbol of this stock.
	 * 
	 * @return the symbol code for this stock.
	 */
	String getSymbol();
	
	/**
	 * Gets the current price of a share of the stock. This is they key
	 * measurement, more than any other, of a stock's current value.
	 * <p>
	 * This method is to be implemented based on how the downloaded
	 * value is stored in the class.
	 * 
	 * @return the current price of a share of the stock.
	 */
	BigDecimal getCurrentPrice();
	
	/**
	 * Download the details of this stock. The values retrieved
	 * should be set as fields in this class.
	 * <p>
	 * The boolean <tt>fullDetails</tt> parameter should be kept constant
	 * with the <tt>getValueMap()</tt> method of this class. That is to say that when
	 * this parameter is false, the same subset of fields is used in both of
	 * these methods.
	 * <p>
	 * The list of fields to download passed to the <tt>StockDownloader</tt>
	 * object should be based on constant properties the downloader will recognize.
	 * The public fields provided here are mean to fulfill this purpose.
	 * 
	 * @param downloader the <tt>StockDownloader</tt> utility to get this stock's
	 * values.
	 * @param fullDetails if true, downloads the values for all the stock's fields. If
	 * false, only downloads a limited set of values for a "quick-view".
	 * @throws InvalidStockException if this stock is not a valid stock on the New York
	 * Stock Exchange.
	 * @throws UnknownHostException if a connection cannot be made to the web server
	 * to download stock details, most likely due to lack of an internet connection.
	 * @throws IOException if a problem occurs while trying to download and parse
	 * the stock details.
	 * @see stockmarket.stock.StockDownloader StockDownloader
	 */
	void setStockDetails(StockDownloader downloader, 
			boolean fullDetails) throws InvalidStockException, UnknownHostException, IOException;
	
	/**
	 * Download and get a list of <tt>HistoricalQuote</tt>s defining the recent history
	 * of this stock. Useful for long term analysis of trends and the generation of
	 * charts.
	 * 
	 * @param downloader the <tt>StockDownloader</tt> utility to get this stock's
	 * values from the internet.
	 * @param months the number of months to get a history for.
	 * @return a list of <tt>HistoricalQuote</tt>s for the period requested.
	 * @throws InvalidStockException if this stock is not a valid stock on the New York
	 * Stock Exchange.
	 * @throws UnknownHostException if a connection cannot be made to the web server
	 * to download stock details, most likely due to lack of an internet connection.
	 * @throws IOException if a problem occurs while trying to download and parse
	 * the stock details.
	 * @see stockmarket.stock.StockDownloader StockDownloader
	 */
	List<HistoricalQuote> getStockHistory(StockDownloader downloader, 
			int months) throws InvalidStockException, UnknownHostException, IOException;
	
	/**
	 * Get a <tt>Map</tt> of all the values contained in this stock. A boolean
	 * parameter is provided so that the full <tt>Map</tt> doesn't need to be
	 * constructed if all that is needed is a smaller subset of data.
	 * <p>
	 * The <tt>fullDetails</tt> parameter should be kept constant with the 
	 * <tt>setStockDetails()</tt> method of this class. That is to say that when
	 * this parameter is false, the same subset of fields is used in both of
	 * these methods.
	 * 
	 * @param fullDetails whether or not the <tt>Map</tt> should contain all
	 * the values of this stock.
	 * @return a <tt>Map</tt>  of the values of this stock.
	 */
	Map<String,Object> getValueMap(boolean fullDetails);
	
}
