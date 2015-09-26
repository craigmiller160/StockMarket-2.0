package io.craigmiller160.stockmarket.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.craigmiller160.stockmarket.stock.DefaultOwnedStock;
import io.craigmiller160.stockmarket.stock.InvalidStockException;
import io.craigmiller160.stockmarket.stock.OwnedStock;
import io.craigmiller160.stockmarket.stock.StockFileDownloader;

/**
 * JUnit test case for the <tt>PortfolioModel</tt> class. It tests
 * the key methods for the model. The operations to calculate the 
 * various values of the portfolio are all tested here. In addition,
 * the operations to add/remove stocks from the stock list, as well
 * as the internal responses on if to add/combine a stock when its 
 * added, are all tested as well.
 * 
 * @author craig
 * @version 2.0
 */
public class PortfolioModelTest {

	/**
	 * The portfolio object being tested. Stored as a private field
	 * so it can be manipulated by multiple methods over the course 
	 * of the test. 
	 */
	private PortfolioModel portfolio = new PortfolioModel();
	
	/**
	 * Test the <tt>PortfolioModel</tt>'s methods.
	 */
	@Test
	public void testPortfolioMethods(){
		testInitialValue();
		testCashBalance();
		testAddNewStock();
		testAddExistingStock();
		testAddEmptyStock();
		testSetStockList();
	}
	
	/**
	 * Test the operation to set the initial value of the portfolio.
	 */
	private void testInitialValue(){
		portfolio.setInitialValue(new BigDecimal(5000));
		portfolio.setCashBalance(new BigDecimal(5000));
		portfolio.setNetWorth(new BigDecimal(5000));
		assertEquals("Initial Value Test: initialValue failed", 
				portfolio.getInitialValue(), new BigDecimal(5000));
		assertEquals("Initial Value Test: cashBalance failed", 
				portfolio.getCashBalance(), new BigDecimal(5000));
		assertEquals("Initial Value Test: totalStockValue failed", 
				portfolio.getTotalStockValue(), new BigDecimal(0));
		assertEquals("Initial Value Test: netWorth failed", 
				portfolio.getNetWorth(), new BigDecimal(5000));
		assertEquals("Initial Value Test: changeInNetWorth failed", 
				portfolio.getChangeInNetWorth(), new BigDecimal(0));
	}
	
	/**
	 * Test the operation changing the value of the cash balance.
	 */
	private void testCashBalance(){
		portfolio.setCashBalance(new BigDecimal(3000));
		assertEquals("Cash Balance Test: cashBalance failed", 
				portfolio.getCashBalance(), new BigDecimal(3000));
		assertEquals("Cash Balance Test: netWorth failed", 
				portfolio.getNetWorth(), new BigDecimal(3000));
		assertEquals("Cash Balance Test: changeInNetWorth failed", 
				portfolio.getChangeInNetWorth(), new BigDecimal(-2000));
	}
	
	/**
	 * Test adding a stock that doesn't exist
	 * in the list. The stock should be added
	 * to the list in a new index. Also test
	 * how the other fields are updated.
	 */
	private void testAddNewStock(){
		OwnedStock stock = getStock("AAPL", "Apple", new BigDecimal(45));
		stock.addShares(20);
		
		portfolio.setStockInList(stock);
		
		assertEquals("Add New Stock Test: stockList size failed", 
				portfolio.getStockListSize(), 1);
		assertEquals("Add New Stock Test: stockList contents failed", 
				portfolio.getStockInList(0), stock);
		assertEquals("Add New Stock Test: totalStockValue failed", 
				portfolio.getTotalStockValue(), new BigDecimal(900));
		assertEquals("Add New Stock Test, netWorth failed", 
				portfolio.getNetWorth(), new BigDecimal(3900));
		assertEquals("Add New Stock Test, changeInNetWorth failed", 
				portfolio.getChangeInNetWorth(), new BigDecimal(-1100));
	}
	
	/**
	 * Test adding a stock that already exists in the 
	 * list. The stock added should replace the stock
	 * already in the list. Also test how the other
	 * fields are updated.
	 */
	private void testAddExistingStock(){
		OwnedStock stock = getStock("AAPL", "Apple", new BigDecimal(45));
		stock.addShares(25);
		
		portfolio.setStockInList(stock);
		
		assertEquals("Add Existing Stock Test: stockList size failed", 
				portfolio.getStockListSize(), 1);
		assertEquals("Add Existing Stock Test: stockList contents failed", 
				portfolio.getStockInList(0), stock);
		assertEquals("Add Existing Stock Test: totalStockValue failed", 
				portfolio.getTotalStockValue(), new BigDecimal(1125));
		assertEquals("Add Existing Stock Test, netWorth failed", 
				portfolio.getNetWorth(), new BigDecimal(4125));
		assertEquals("Add Existing Stock Test, changeInNetWorth failed", 
				portfolio.getChangeInNetWorth(), new BigDecimal(-875));
	}
	
	/**
	 * Test adding a stock with 0 shares that already
	 * exists in the list. The result should be that
	 * the stock is removed from the list. Also test
	 * how the other fields are updated.
	 */
	private void testAddEmptyStock(){
		OwnedStock stock = getStock("AAPL", "Apple", new BigDecimal(45));
		stock.addShares(0);
		
		portfolio.setStockInList(stock);
		
		assertEquals("Add Empty Stock Test: stockList size failed", 
				portfolio.getStockListSize(), 0);
		assertEquals("Add Empty Stock Test: totalStockValue failed", 
				portfolio.getTotalStockValue(), new BigDecimal(0));
		assertEquals("Add Empty Stock Test, netWorth failed", 
				portfolio.getNetWorth(), new BigDecimal(3000));
		assertEquals("Add Empty Stock Test, changeInNetWorth failed", 
				portfolio.getChangeInNetWorth(), new BigDecimal(-2000));
	}
	
	/**
	 * Test setting a new stock list and its
	 * effect on other fields in the model.
	 */
	private void testSetStockList(){
		List<OwnedStock> stockList = new ArrayList<>();
		OwnedStock stock = getStock("GOOG", "Google", new BigDecimal(222));
		stock.addShares(5);
		stockList.add(stock);
		
		portfolio.setStockList(stockList);
		
		assertEquals("Set Stock List Test: stockList size failed", 
				portfolio.getStockListSize(), 1);
		assertEquals("Set Stock List Test: totalStockValue failed", 
				portfolio.getTotalStockValue(), new BigDecimal(1110));
		assertEquals("Set Stock List Test: netWorth failed", 
				portfolio.getNetWorth(), new BigDecimal(4110));
		assertEquals("Set Stock List Test: changeInNetWorth failed", 
				portfolio.getChangeInNetWorth(), new BigDecimal(-890));
	}
	
	private OwnedStock getStock(String symbol, String name, BigDecimal price){
		DefaultOwnedStock stock = new DefaultOwnedStock(symbol);
		StockFileDownloader downloader = new StockFileDownloader();
		downloader.setSymbol(symbol);
		downloader.setName(name);
		downloader.setCurrentPrice(price.toString());
		downloader.setQuantityOfShares("0");
		downloader.setPrinciple("0");
		downloader.setTotalValue("0");
		downloader.setNet("0");
		try {
			stock.setStockDetails(downloader, false);
		} catch (UnknownHostException e) {
			//Won't be thrown in this dummy test class
		} catch (InvalidStockException e) {
			//Won't be thrown in this dummy test class
		} catch (IOException e) {
			//Won't be thrown in this dummy test class
		}
		
		return stock;
	}
	
	
}
