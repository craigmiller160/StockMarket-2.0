package io.craigmiller160.stockmarket.stock;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * A special variation on the <tt>StockDownloader</tt> interface, designed
 * to load the stock's data from file. This class provides a limited implementation
 * of the <tt>StockDownloader</tt>. It is tied directly into the saving policy of 
 * the program, and can only retrieve the values of fields that the program
 * saves to a file.'
 * <p>
 * <b>THREAD SAFETY:</b> This class is completely thread safe. While the <tt>Map</tt>
 * of values is released from this class, it is a synchronized collection. As long as 
 * it is handled according to the guidelines of the synchronized collections, it can
 * be safely managed by multiple threads.
 * 
 * @author craig
 * @version 2.0
 * @see io.craigmiller160.stockmarket.stock.OwnedStock OwnedStock
 */
@ThreadSafe
public class StockFileDownloader implements StockDownloader {

	/**
	 * Map of the values to be added to a stock object by exploiting
	 * the downloader methods.
	 */
	@GuardedBy("this")
	private final Map<String,String> valueMap;
	
	/**
	 * Creates a new instance of this file downloader.
	 */
	public StockFileDownloader() {
		valueMap = Collections.synchronizedMap(new HashMap<>());
	}
	
	/**
	 * Set the symbol of the stock.
	 * 
	 * @param symbol the symbol of the stock.
	 */
	public synchronized void setSymbol(String symbol){
		valueMap.put(AbstractStock.SYMBOL, symbol.toUpperCase());
	}
	
	/**
	 * Set the name of the company.
	 * 
	 * @param name the name of the company.
	 */
	public synchronized void setName(String name){
		valueMap.put(AbstractStock.NAME, name);
	}
	
	/**
	 * Set the current price of a share of the stock.
	 * 
	 * @param currentPrice the current price of a share of the stock.
	 */
	public synchronized void setCurrentPrice(String currentPrice){
		valueMap.put(AbstractStock.CURRENT_PRICE, currentPrice);
	}
	
	/**
	 * Set the quantity of shares owned of the stock.
	 * 
	 * @param quantity the quantity of shares owned.
	 */
	public synchronized void setQuantityOfShares(String quantity){
		valueMap.put(OwnedStock.QUANTITY_OF_SHARES, quantity);
	}
	
	/**
	 * Set the principle, the original amount paid for the stock.
	 * 
	 * @param principle the principle of the stock.
	 */
	public synchronized void setPrinciple(String principle){
		valueMap.put(OwnedStock.PRINCIPLE, principle);
	}
	
	/**
	 * Set the total value of all shares of the stock.
	 * 
	 * @param totalValue the total value of the stock.
	 */
	public synchronized void setTotalValue(String totalValue){
		valueMap.put(OwnedStock.TOTAL_VALUE, totalValue);
	}
	
	/**
	 * Set the net of the stock, the amount its value has changed.
	 * 
	 * @param net the net of the stock.
	 */
	public synchronized void setNet(String net){
		valueMap.put(OwnedStock.NET, net);
	}
	
	/**
	 * Clear all values from the map.
	 */
	public synchronized void clearMap(){
		valueMap.clear();
	}

	@Override
	public Map<String, String> downloadStockDetails(String symbol,
			String[] fields) throws InvalidStockException, UnknownHostException, IOException {
		return valueMap;
	}

	/**
	 * This method is not implemented in this <tt>StockDownloader</tt>, and will throw
	 * an <tt>UnsupportedOperationException</tt> if invoked.
	 * 
	 * @throws UnsupportedOperationException if this method is invoked.
	 */
	@Override
	public List<HistoricalQuote> downloadStockHistory(String symbol, int months)
			throws InvalidStockException, UnknownHostException, IOException {
		throw new UnsupportedOperationException("Downloading the Stock History is not supported"
				+ " by this StockDownloader");
	}

}
