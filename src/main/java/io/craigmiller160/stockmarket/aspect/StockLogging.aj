package io.craigmiller160.stockmarket.aspect;

import java.math.BigDecimal;

import java.util.List;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.craigmiller160.stockmarket.stock.OwnedStock;
import io.craigmiller160.stockmarket.stock.StockDownloader;
import io.craigmiller160.stockmarket.stock.StockFileDownloader;
import io.craigmiller160.stockmarket.stock.HistoricalQuote;

/**
 * An Aspect to handle all logging for classes in the stock
 * package.
 * 
 * @author craig
 * @version 2.3
 *
 */
public aspect StockLogging {

	/**
	 * The logger for this aspect.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger("io.craigmiller160.stockmarket.stock");
	
	/**
	 * A pointcut pointing to all classes in the stock
	 * package.
	 */
	pointcut stockPackageAllClasses() :
		within(io.craigmiller160.stockmarket.stock.*);
	
	/**
	 * A pointcut pointing to the <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * method.
	 */
	pointcut stockDetailsSetter() :
		execution(public void setStockDetails(StockDownloader,boolean));
	
	/**
	 * A pointcut pointing to the <tt>getStockHistory(StockDownloader,boolean)</tt>
	 * method.
	 */
	pointcut stockHistory() :
		execution(public List<HistoricalQuote> getStockHistory(StockDownloader,int));
	
	/**
	 * A pointcut pointing to the method in the
	 * <tt>OwnedStock</tt> interface and its subclasses that change
	 * the quantity of shares owned by the stock.
	 */
	pointcut shareQuantityChange() :
		execution(public * io.craigmiller160.stockmarket.stock.DefaultOwnedStock.*Shares(int));
	
	/**
	 * Advice that runs before the <tt>setStockDetails(StockDownloader,boolean)</tt>
	 * method. This advice logs the downloading operation that sets the stock's 
	 * details.
	 * 
	 * @param d the <tt>StockDownloader</tt> object.
	 * @param b whether or not to download the full details of the stock.
	 */
	before(StockDownloader d, boolean b) : stockDetailsSetter() && stockPackageAllClasses() && args(d,b){
		String methodName = thisJoinPoint.getSignature().getName();
		LOGGER.info("\"{}, Downloading. Full Details: {}\"", methodName, b);
		if(d instanceof StockFileDownloader){
			LOGGER.info("\"{}, FileDownloader setting values\"", methodName);
		}
	}
	
	/**
	 * Advice that runs before the methods
	 * of the <tt>OwnedStock</tt> interface and its subclasses that
	 * change the quantity of shares owned by the stock.
	 * This advice logs the increase in the number of shares of the
	 * stock.
	 * 
	 * @param i the amount of shares of the stock to change.
	 */
	before() : shareQuantityChange(){
		Integer quantity = (Integer) thisJoinPoint.getArgs()[0];
		String methodName = thisJoinPoint.getSignature().getName();
		OwnedStock oStock = (OwnedStock) thisJoinPoint.getTarget();
		BigDecimal currentPrice = oStock.getCurrentPrice();
		if(currentPrice != null){
			//If these conditions aren't true, the targeted method will be throwing
			//an exception.
			BigDecimal principleChange = currentPrice.multiply(new BigDecimal(quantity));
			LOGGER.info("{} Quantity: {} Principle Change: {}", 
					methodName, quantity, principleChange);
		}
	}
	
	/**
	 * Advice that runs before the <tt>getStockHistory(StockDownloader,int)</tt>
	 * method. This advice logs the length of the history that is being downloaded.
	 * 
	 * @param d the <tt>StockDownloader</tt>.
	 * @param i the amount of months to download a history for.
	 */
	before(StockDownloader d, int i) : stockHistory() && args(d,i){
		String methodName = thisJoinPoint.getSignature().getName();
		LOGGER.info("{} for {} months", methodName, i);
	}
	
	/**
	 * Advice that runs after one of the url creation methods returns.
	 * It logs the url that has been created for the web data call.
	 * 
	 * @param url the url returned by the method.
	 */
	after() returning(URL url) : 
		execution(private URL io.craigmiller160.stockmarket.stock.YahooStockDownloader.createUrl*(..)) {
		String methodName = thisJoinPoint.getSignature().getName();
		LOGGER.debug("\"Downloading Stock: {} : {}\"", methodName, url);
	}
	
	/**
	 * Advice that runs before the one of the CSV parsing methods executes.
	 * It logs the csv text that is being parsed.
	 */
	before() : 
		execution(private * io.craigmiller160.stockmarket.stock.YahooStockDownloader.parseCsv*(..)){
		String methodName = thisJoinPoint.getSignature().getName();
		String csvText = (String) thisJoinPoint.getArgs()[0];
		LOGGER.debug("\"Downloading Stock: {} : {}\"", methodName, csvText);
	}
	
	
	
}
