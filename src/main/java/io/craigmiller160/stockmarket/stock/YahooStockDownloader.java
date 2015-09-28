package io.craigmiller160.stockmarket.stock;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import net.jcip.annotations.ThreadSafe;

import org.joda.time.DateTime;

/**
 * Implemented <tt>StockDownloader</tt> utilizing Yahoo!Finance's online
 * service to acquire stock data. This class downloads both detailed field data
 * and an extended history of the stock.
 * <p>
 * <b>SUPPORTED STOCK FIELDS:</b> This downloader only supports the following fields.
 * All names for these fields are constant values in the <tt>AbstractStock</tt> class.
 * Using any fields that are not listed here in with the <tt>downloadStockDetails()</tt>
 * method will throw an <tt>IllegalArgumentException</tt>.
 * <p>
 * ASK<br>
 * ASK_SIZE<br>
 * AVERAGE_DAILY_VOLUME<br>
 * BID<br>
 * CHANGE<br>
 * CHANGE_IN_PERCENT<br>
 * CHANGE_50_DAY_AVG<br>
 * CHANGE_50_DAY_AVG_PERCENT<br>
 * CHANGE_200_DAY_AVG<br>
 * CHANGE_200_DAY_AVG_PERCENT<br>
 * CHANGE_YEAR_HIGH<br>
 * CHANGE_YEAR_HIGH_PERCENT<br>
 * CHANGE_YEAR_LOW<br>
 * CHANGE_YEAR_LOW_PERCENT<br>
 * CURRENT_PRICE<br>
 * CURRENCY<br>
 * DAYS_HIGH<br>
 * DAYS_LOW<br>
 * DAYS_RANGE<br>
 * FIFTY_DAY_AVG<br>
 * LAST_TRADE_DATE<br>
 * LAST_TRADE_SIZE<br>
 * LAST_TRADE_TIME<br>
 * MARKET_CAPITALIZATION<br>
 * NAME<br>
 * OPEN<br>
 * PREVIOUS_CLOSE<br>
 * REVENUE<br>
 * SYMBOL<br>
 * TWO_HUNDRED_DAY_AVG<br>
 * VOLUME<br>
 * YEAR_HIGH<br>
 * YEAR_LOW<br>
 * YEAR_RANGE
 * <p>
 * <b>THREAD SAFETY:</b> This class has no mutable state. All variables are either
 * static and final, or local and confined to the individual thread stack. Therefore
 * any instance of this class can be successfully used in a concurrent environment.
 * 
 * @author craig
 * @version 2.0
 */
@ThreadSafe
public final class YahooStockDownloader implements StockDownloader {

	/**
	 * A <tt>Map</tt> of property codes, to be checked against a list
	 * of field names provided to the <tt>downloadStockDetails()</tt> method.
	 */
	private static final Map<String,String> propertyCodeMap;

	/**
	 * Map value for the symbol field, used to identify it and excluse it while parsing the
	 * field array. Because of how Yahoo's csv downloading tool and the mechanism
	 * developed to check the validity of the stock, actually including this property
	 * in the URL query would give a false positive verification.
	 */
	private static final String SYMBOL_FIELD = "Symbol";
	
	/**
	 * Static initializer creates the <tt>propertyCodeMap</tt>.
	 */
	static{
		Map<String,String> tempMap = new HashMap<>();
		tempMap.put(AbstractStock.ASK, "a");
		tempMap.put(AbstractStock.ASK_SIZE, "a5");
		tempMap.put(AbstractStock.AVERAGE_DAILY_VOLUME, "a2");
		tempMap.put(AbstractStock.BID, "b");
		tempMap.put(AbstractStock.BID_SIZE, "b6");
		tempMap.put(AbstractStock.CHANGE_TODAY, "c1");
		tempMap.put(AbstractStock.CHANGE_TODAY_PERCENT, "p2");
		tempMap.put(AbstractStock.CHANGE_50_DAY_AVG, "m7");
		tempMap.put(AbstractStock.CHANGE_50_DAY_AVG_PERCENT, "m8");
		tempMap.put(AbstractStock.CHANGE_200_DAY_AVG, "m5");
		tempMap.put(AbstractStock.CHANGE_200_DAY_AVG_PERCENT, "m6");
		tempMap.put(AbstractStock.CHANGE_YEAR_HIGH, "k4");
		tempMap.put(AbstractStock.CHANGE_YEAR_HIGH_PERCENT, "k5");
		tempMap.put(AbstractStock.CHANGE_YEAR_LOW, "j5");
		tempMap.put(AbstractStock.CHANGE_YEAR_LOW_PERCENT, "j6");
		
		//On YahooFinance website, this property is called LastTradePrice 
		tempMap.put(AbstractStock.CURRENT_PRICE, "l1");
		tempMap.put(AbstractStock.CURRENCY, "c4");
		tempMap.put(AbstractStock.DAYS_HIGH, "h");
		tempMap.put(AbstractStock.DAYS_LOW, "g");
		tempMap.put(AbstractStock.DAYS_RANGE, "m");
		tempMap.put(AbstractStock.FIFTY_DAY_AVG, "m3");
		tempMap.put(AbstractStock.LAST_TRADE_DATE, "d1");
		tempMap.put(AbstractStock.LAST_TRADE_SIZE, "k3");
		tempMap.put(AbstractStock.LAST_TRADE_TIME, "t1");
		tempMap.put(AbstractStock.MARKET_CAPITALIZATION, "j1");
		tempMap.put(AbstractStock.NAME, "n");
		tempMap.put(AbstractStock.OPEN, "o");
		tempMap.put(AbstractStock.PREVIOUS_CLOSE, "p");
		tempMap.put(AbstractStock.REVENUE, "s6");
		tempMap.put(AbstractStock.SYMBOL, SYMBOL_FIELD);
		tempMap.put(AbstractStock.TWO_HUNDRED_DAY_AVG, "m4");
		tempMap.put(AbstractStock.VOLUME, "v");
		tempMap.put(AbstractStock.YEAR_HIGH, "k");
		tempMap.put(AbstractStock.YEAR_LOW, "j");
		tempMap.put(AbstractStock.YEAR_RANGE, "w");
		
		propertyCodeMap = Collections.unmodifiableMap(tempMap);
	}

	/**
	 * Constructs a downloader object using Yahoo!Finance's service.
	 */
	public YahooStockDownloader() {
		
	}

	@Override
	public Map<String,String> downloadStockDetails(String symbol, String[] fields)
			throws InvalidStockException, UnknownHostException, IOException{
		StringBuffer csvFile = new StringBuffer();
		
		//Construct URL and connect to it
		URL url = createUrlForDetails(symbol, fields);
		URLConnection connection = url.openConnection();
		
		//Download stock data
		try(BufferedReader reader = new BufferedReader(
				new InputStreamReader(connection.getInputStream()))) {
			String line = "";
			while((line = reader.readLine()) != null){
				csvFile.append(line);
			}
		}
		
		//Parse downloaded stock data
		Map<String,String> stockDataMap = null;
		if(!csvFile.toString().equals("")){
			try{
				stockDataMap = parseCsvForDetails(csvFile.toString(), fields);
			}
			catch(InvalidStockException ex){
				throw new InvalidStockException(symbol);
			}
		}
		
		stockDataMap.put(AbstractStock.SYMBOL, symbol);
		
		return stockDataMap;
	}
	
	/**
	 * Construct the URL from the values provided.
	 * 
	 * @param symbol the symbol of the stock.
	 * @param fields the fields to get information for.
	 * @return the constructed URL.
	 * @throws MalformedURLException if the URL created is not valid.
	 */
	private URL createUrlForDetails(String symbol, String[] fields) throws MalformedURLException{
		String urlStart = "http://download.finance.yahoo.com/d/quotes.csv?s=";
		String urlProperties = getURLProperties(fields);
		String urlEnd = "&e=.csv";
		
		return new URL(urlStart + symbol + urlProperties + urlEnd);
	}
	
	/**
	 * Parse the downloaded csv text and pair each entry with the appropriate
	 * field name in a <tt>Map</tt>.
	 * 
	 * @param csvText the raw csv text to be parsed.
	 * @param fields the fields to be paired with the raw data from the csv.
	 * @return a <tt>Map</tt> containing data parsed from the csv text.
	 * @throws InvalidStockException if the stock is not a valid marketplace stock.
	 */
	private Map<String, String> parseCsvForDetails(String csvText, String[] fields) 
			throws InvalidStockException{
		Map<String,String> stockDataMap = new HashMap<>();
		String[] tempData = csvText.split(",");
		
		if(!verifyStock(tempData)){
			throw new InvalidStockException();
		}
		
		for(int i = 0, n = 0; i < tempData.length; i++){
			if(fields[n] != AbstractStock.SYMBOL){
				//As long as the value of the field array at this index
				//isn't SYMBOL, parse the tempData for a value
				String dataEntry = null;
				if((tempData[i].charAt(0) == '"')
						&& (tempData[i].charAt(tempData[i].length() - 1) != '"')){
					//If the first char is ", but the last char is NOT "
					//Only occurs when Name has comma in the middle, combine fields to fix 
					dataEntry = tempData[i] + tempData[i + 1];
					dataEntry = dataEntry.substring(1, dataEntry.length() - 1);
					i++;
				}
				else if((tempData[i].charAt(0) == '"') 
						&& (tempData[i].charAt(tempData[i].length() - 1) == '"')){
					//If the first and last char are "
					//Remove the quotes, helps with number parsing later on.
					dataEntry = tempData[i].substring(1, tempData[i].length() - 1);
				}
				else{
					//Nothing special, add to final map.
					dataEntry = tempData[i];
				}
				
				if(dataEntry.charAt(dataEntry.length() - 1) == '%'){
					//If there's a % sign at the end of the string, remove it to help with number parsing
					dataEntry = dataEntry.substring(0, dataEntry.length() - 1);
				}
				
				stockDataMap.put(fields[n], dataEntry);
			}
			else{
				//If the field array value is SYMBOL, decrement i to avoid tempData moving ahead
				i--;
			}
			//At the end of every operation, increment the index for the field array
			n++;
		}
		
		return stockDataMap;
	}
	
	/**
	 * Verify that the stock being downloaded is a valid marketplace stock.
	 * If it is, this method returns true, if not it returns false.
	 * 
	 * @param stockData the data to parse to confirm the stock's validitiy.
	 * @return true if the stock is valid, false if it is not.
	 */
	private boolean verifyStock(String[] stockData){
		boolean verified = false;
		for(String s : stockData){
			if(!s.equals("N/A")){
				verified = true;
				break;
			}
		}
		return verified;
	}
	
	/**
	 * Prepare a <tt>String</tt> of property values to be added to the URL.
	 * These property codes are prepared based on the array of field names 
	 * passed as a parameter. If any of the fields are not supported by this
	 * downloader, an <tt>IllegalArgumentException</tt> is thrown.
	 * 
	 * @param fields the list of field names for the property codes to be
	 * added to the URL.
	 * @return a <tt>String</tt> of property codes to be added to the URL.
	 * @throws IllegalArgumentException if one or more of the property codes
	 * aren't supported by this downloader.
	 */
	private String getURLProperties(String[] fields){
		StringBuffer urlProperties = new StringBuffer("&f=");
		
		for(int i = 0; i < fields.length; i++){
			String code = propertyCodeMap.get(fields[i]);
			if(code == SYMBOL_FIELD){
				//do nothing
			}
			else if(code == null){
				throw new IllegalArgumentException(fields[i] 
						+ " is not supported by this downloader");
			}
			else{
				urlProperties.append(code);
			}
		}
		
		return urlProperties.toString();
	}

	@Override
	public List<HistoricalQuote> downloadStockHistory(String symbol, int months) 
			throws InvalidStockException, UnknownHostException, IOException{
		//Create URL & connect
		URL url = createUrlForHistory(symbol, months);
		URLConnection connection = url.openConnection();
		
		//Download the csv data 
		StringBuffer csvFile = new StringBuffer();
		try(BufferedReader reader = new BufferedReader(
				new InputStreamReader(connection.getInputStream()))) {
			
			String line = "";
			while((line = reader.readLine()) != null){
				csvFile.append(line + "\n");
			}
		}
		catch(FileNotFoundException ex){
			//FileNotFoundException is thrown if the stock is invalid and
			//yahoo can't produce the chart.
			throw new InvalidStockException(symbol);
		}
		
		//Parse the csv data for the stock history
		List<HistoricalQuote> historyList = null;
		if(!(csvFile.toString().equals(""))){
			historyList = parseCsvForHistory(csvFile.toString(), symbol);
		}

		return historyList;
	}
	
	private URL createUrlForHistory(String symbol, int months) throws MalformedURLException{
		String urlStart = "http://ichart.yahoo.com/table.csv?s=";
		String urlFromDate = getFromDateURLCode(months);
		String urlToDate = getToDateURLCode();
		String urlInterval = "&g=d"; //Daily quote interval
		String urlEnd = "&ignore=.csv";
		
		return new URL(urlStart + symbol + urlFromDate + urlToDate + urlInterval + urlEnd);
	}
	
	/**
	 * Parse the csv data for the stock history. Individual quotes are identified and
	 * stores in <tt>HistoricalQuote</tt> objects, a list of which is returned to the
	 * caller.
	 * 
	 * @param csvData the raw csv data being parsed for historical stock information.
	 * @return a list of <tt>HistoricalQuote</tt> objects composed from the raw data.
	 */
	private List<HistoricalQuote> parseCsvForHistory(String csvData, String symbol){
		List<HistoricalQuote> historyList = new StockHistoryList(symbol);
		
		try(Scanner scan = new Scanner(csvData)){
			
			int lineCount = 0; //First line is header, needs to be skipped
			String[] historyData = null;
			while(scan.hasNext()){
				if(lineCount > 0){
					historyData = scan.nextLine().split(",");
					int[] calNums = parseCalendarNumbers(historyData[0]);
					Calendar date = new GregorianCalendar(calNums[2], calNums[0], calNums[1]);
					BigDecimal closeValue = new BigDecimal(Double.parseDouble(historyData[4]));
					historyList.add(new HistoricalQuote(date, closeValue));
				}
				else{
					scan.nextLine();
				}
				lineCount++;
			}
		}
		
		return historyList;
	}
	
	/**
	 * Parse raw calendar number values from a <tt>String</tt> and return
	 * their <tt>int</tt> values.
	 * 
	 * @param nums a <tt>String</tt> containing calendar number values.
	 * @return the <tt>int</tt> calendar values. 
	 */
	private int[] parseCalendarNumbers(String nums){
		int[] calNums = new int[3];
		String[] calText = nums.split("-");
		calNums[0] = Integer.parseInt(calText[1]) - 1; //Month
		calNums[1] = Integer.parseInt(calText[2]); //Day
		calNums[2] = Integer.parseInt(calText[0]); //Year
		return calNums;
	}
	
	/**
	 * Composes the URL code for the "from date", the date the history starts at.
	 * 
	 * @param months the number of months ago the "from date" is.
	 * @return the URL code for the "from date".
	 */
	private String getFromDateURLCode(int months){
		Calendar from = new DateTime(DateTime.now().minusMonths(months)).toGregorianCalendar();
		int sMonth = from.get(Calendar.MONTH);
		int sDay = from.get(Calendar.DAY_OF_MONTH);
		int sYear = from.get(Calendar.YEAR);
		return "&a=" + sMonth + "&b=" + sDay + "&c=" + sYear;
	}
	
	/**
	 * Composes the URL code for the "to date", the date the history ends at.
	 * 
	 * @return the URL code for the "to date".
	 */
	private String getToDateURLCode(){
		Calendar today = Calendar.getInstance();
		int eMonth = today.get(Calendar.MONTH);
		int eDay = today.get(Calendar.DAY_OF_MONTH);
		int eYear = today.get(Calendar.YEAR);
		return "&d=" + eMonth + "&e=" + eDay + "&f=" + eYear;
	}

}
