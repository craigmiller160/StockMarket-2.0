package stockmarket.stock;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * API for module for downloading stock data from a web service. Implementations of this
 * interface should be tailored for the service it connects to.
 * <p>
 * <b>IMPLEMENTATION NOTES:</b>
 * <p>
 * <b>1)</b> The <tt>downloadStockDetails()</tt> requires the symbol of the stock it's
 * getting information from, and an array of the names of the fields that it is downloading
 * information for. The fields should all be constant values from the <tt>AbstractStock</tt>
 * class to ensure consistency. It returns a <tt>Map</tt> of the constant field names (keys)
 * and raw downloaded information (values).
 * <p>
 * <b>2)</b> Because not all online services will offer the same information, <tt>downloadStockDetails()</tt>
 * should check the field names and throw an <tt>IllegalArgumentException</tt> if any of the
 * fields requested are not supported by the service. Implementations should not feel obligated
 * to service each and every field with a constant name in the <tt>AbstractStock</tt> class if
 * it would prevent a particular service from being used.
 * <p>
 * <b>3)</b> Because not all field names from <tt>AbstractStock</tt> may be used in <tt>StockDownloader</tt>
 * implementations, subclasses should clearly document which fields are accepted in their program.
 * <p>
 * <b>4)</b> Both the <tt>downloadStockDetails()</tt> and <tt>downloadStockHistory()</tt>
 * methods should perform some sort of check on whether or not the stock they are searching
 * for is a valid stock. If not, they should throw an <tt>InvalidStockException</tt>.
 * <p>
 * <b>5)</b> The <tt>downloadStockHistory()</tt> method should be used to retrieve historical
 * information about the stock over a set period of months (specified by the parameter value).
 * It returns a list of <tt>HistoricalQuote</tt> objects containing the date of the quote and 
 * the closing price of the stock on that day.
 * 
 * @author craig
 * @version 2.0
 * @see stockmarket.stock.AbstractStock AbstractStock
 */
public interface StockDownloader {

	/**
	 * Downloads detailed data on a stock and returns it in a <tt>Map</tt> collection.
	 * Accepts the stock's marketplace symbol and an array of names of the fields to
	 * be downloaded.
	 * <p>
	 * The field names should be the constant values found in the <tt>AbstractStock</tt>
	 * class to maintain consistency. Implementations should not be bound to utilize
	 * each and every constant that exists, however if it does not there should be a
	 * check on the field names provided to ensure that they are supported. If they are
	 * not supported, an <tt>IllegalArgumentException</tt> should be thrown.
	 * <p>
	 * In addition, the stock should be checked early on to ensure that it is a legitimate
	 * stock on the market. If it is not, an <tt>InvalidStockException</tt> should be thrown.
	 * <p>
	 * Additional exceptions may be thrown based on various errors that may occur during 
	 * execution. Implementing classes should document these exceptions so they can be
	 * anticipated.
	 * 
	 * @param symbol the stock's marketplace symbol.
	 * @param fields an array of constant values from the <tt>AbstractStock</tt> class defining
	 * the fields this class should download data for.
	 * @return a <tt>Map</tt> containing the data for the requested fields for the stock.
	 * @throws IllegalArgumentException if one or more of the fields data is requested for is
	 * not supported by the downloader.
	 * @throws InvalidStockException if the stock is not a legitimate marketplace stock.
	 * @throws UnknownHostException if a connection cannot be made to the web service.
	 * @throws IOException if a problem occurs while trying to download data from the
	 * web service.
	 * @see stockmarket.stock.AbstractStock AbstractStock 
	 */
	Map<String,String> downloadStockDetails(String symbol, String[] fields) 
			throws InvalidStockException, UnknownHostException, IOException;
	
	/**
	 * Downloads the history of this stock, and returns it as a list of <tt>HistoricalQuote</tt>
	 * objects. The time period to get the history for is defined by a number of months passed
	 * as a parameter.
	 * <p>
	 * The stock should be checked early on to ensure that it is a legitimate
	 * stock on the market. If it is not, an <tt>InvalidStockException</tt> should be thrown.
	 * <p>
	 * Additional exceptions may be thrown based on various errors that may occur during 
	 * execution. Implementing classes should document these exceptions so they can be
	 * anticipated.
	 * 
	 * @param symbol the stock's marketplace symbol.
	 * @param months the number of months to download history for.
	 * @return a list of <tt>HistoricalQuote</tt>s for the history of the stock.
	 * @throws InvalidStockException if the stock is not a legitimate marketplace stock.
	 * @throws UnknownHostException if a connection cannot be made to the web service.
	 * @throws IOException if a problem occurs while trying to download data from the
	 * web service.
	 */
	List<HistoricalQuote> downloadStockHistory(String symbol, int months) 
			throws InvalidStockException, UnknownHostException, IOException;
	
}
