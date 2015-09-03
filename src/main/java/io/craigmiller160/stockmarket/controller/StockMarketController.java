package io.craigmiller160.stockmarket.controller;

import io.craigmiller160.mvp.concurrent.AbstractConcurrentListenerController;
import io.craigmiller160.mvp.core.AbstractPropertyModel;
import io.craigmiller160.mvp.listener.ListenerDialog;
import io.craigmiller160.stockmarket.gui.PortfolioState;
import io.craigmiller160.stockmarket.gui.dialog.Dialog;
import io.craigmiller160.stockmarket.gui.dialog.DialogFactory;
import io.craigmiller160.stockmarket.model.InsufficientFundsException;
import io.craigmiller160.stockmarket.model.PortfolioModel;
import io.craigmiller160.stockmarket.stock.DefaultOwnedStock;
import io.craigmiller160.stockmarket.stock.DefaultStock;
import io.craigmiller160.stockmarket.stock.HistoricalQuote;
import io.craigmiller160.stockmarket.stock.InvalidStockException;
import io.craigmiller160.stockmarket.stock.OwnedStock;
import io.craigmiller160.stockmarket.stock.Stock;
import io.craigmiller160.stockmarket.stock.StockDownloader;
import io.craigmiller160.stockmarket.stock.YahooStockDownloader;
import io.craigmiller160.stockmarket.util.Language;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import net.jcip.annotations.ThreadSafe;

/**
 * The controller for the <tt>StockMarket</tt> program. This class extends
 * <tt>AbstractConcurrentListenerController</tt> from the MVP Framework, 
 * and fully utilizes its concurrency. All events received from the GUI
 * are off-loaded to background threads, where the appropriate task is executed.
 * <p>
 * All property names and action commands used throughout this program are
 * constants in this class, allowing them to be managed in a centralized location.
 * All views and models should import these values to ensure consistency across the 
 * program.
 * <p>
 * <b>THREAD SAFETY:</b> This class is thread safe. All methods invoked by 
 * <tt>processEvent(String,Object)</tt> are properly constructed so that they 
 * can be accessed by multiple threads simultaneously.
 * 
 * @author craig
 * @version 2.0
 */
@ThreadSafe
public class StockMarketController extends AbstractConcurrentListenerController {
	
	//TODO for refreshing the portfolio, make sure it takes individual stock lookup/refreshes into account
	
	/**
	 * The property name for the stock list.
	 */
	public static final String STOCK_LIST_PROPERTY = "StockList";
	
	/**
	 * The property name for the portfolio name.
	 */
	public static final String PORTFOLIO_NAME_PROPERTY = "PortfolioName";
	
	/**
	 * The property name for the total value of all stocks.
	 */
	public static final String TOTAL_STOCK_VALUE_PROPERTY = "TotalStockValue";
	
	/**
	 * The property name for the net worth.
	 */
	public static final String NET_WORTH_PROPERTY = "NetWorth";
	
	/**
	 * The property name for the change in net worth.
	 */
	public static final String CHANGE_IN_NET_WORTH_PROPERTY = "ChangeInNetWorth";
	
	/**
	 * The property name for the cash balance.
	 */
	public static final String CASH_BALANCE_PROPERTY = "CashBalance";
	
	/**
	 * The property name for the userid.
	 */
	public static final String USER_ID_PROPERTY = "UserID";
	
	/**
	 * The property name for the currently selected stock.
	 */
	public static final String SELECTED_STOCK_PROPERTY = "SelectedStock";
	
	/**
	 * The property name for the currently selected stock's history.
	 */
	public static final String SELECTED_STOCK_HISTORY_PROPERTY = "SelectedStockHistory";
	
	/**
	 * The property name for the portfolio's current state.
	 */
	public static final String PORTFOLIO_STATE_PROPERTY = "PortfolioState";
	
	/**
	 * The property name for the dialog being displayed.
	 */
	public static final String DIALOG_DISPLAYED_PROPERTY = "DialogDisplayed";
	
	/**
	 * The property name for a stock in the stock list.
	 */
	public static final String STOCK_IN_LIST_PROPERTY = "StockInList";
	
	/**
	 * The action command for creating a new portfolio.
	 */
	public static final String NEW_PORTFOLIO_ACTION = "NewPortfolio";
	
	/**
	 * The action command for opening a saved portfolio.
	 */
	public static final String OPEN_PORTFOLIO_ACTION = "OpenPortfolio";
	
	/**
	 * The action command for saving a portfolio.
	 */
	public static final String SAVE_PORTFOLIO_ACTION = "SavePortfolio";
	
	/**
	 * The action command for closing a portfolio.
	 */
	public static final String CLOSE_PORTFOLIO_ACTION = "ClosePortfolio";
	
	/**
	 * The action command for exiting the program.
	 */
	public static final String EXIT_PROGRAM_ACTION = "ExitProgram";
	
	/**
	 * The action command for displaying the about program dialog.
	 */
	public static final String ABOUT_PROGRAM_ACTION = "AboutProgram";
	
	/**
	 * The action command for displaying the language dialog.
	 */
	public static final String LANGUAGE_MENU_ACTION = "LanguageMenu";
	
	/**
	 * The action command for toggling debugging mode.
	 */
	public static final String DEBUG_MENU_ACTION = "DebugMenu";
	
	/**
	 * The action command for editing the portfolio name.
	 */
	public static final String EDIT_PORTFOLIO_NAME_ACTION = "EditPortfolioName";
	
	/**
	 * The action command for opening a webpage to view current market data.
	 */
	public static final String MARKET_DATA_ACTION = "MarketData";
	
	/**
	 * The action command for refreshing the stock portfolio.
	 */
	public static final String REFRESH_PORTFOLIO_ACTION = "RefreshPortfolio";
	
	/**
	 * The action command for displaying details about an owned stock.
	 */
	public static final String STOCK_DETAILS_ACTION = "StockDetails";
	
	/**
	 * The action command for searching the web for a stock. 
	 */
	public static final String STOCK_SEARCH_ACTION = "StockSearch";
	
	/**
	 * The action command for looking up the details of a stock in the portfolio.
	 */
	public static final String LOOKUP_PORTFOLIO_STOCK_ACTION = "LookupPortfolioStock";
	
	/**
	 * The action command for changing the stock history interval.
	 */
	public static final String STOCK_HISTORY_INTERVAL_ACTION = "StockHistoryInterval";
	
	/**
	 * The action command for displaying the buy stock dialog.
	 */
	public static final String BUY_STOCK_DIALOG_ACTION = "BuyStockDialog";
	
	/**
	 * The action command for displaying the sell stock dialog.
	 */
	public static final String SELL_STOCK_DIALOG_ACTION = "SellStockdIALOG";
	
	/**
	 * The action command for buying shares of a stock.
	 */
	public static final String BUY_STOCK_ACTION = "BuyStock";
	
	/**
	 * The action command for selling shares of a stock.
	 */
	public static final String SELL_STOCK_ACTION = "SellStock";
	
	/**
	 * The action command for canceling an action.
	 */
	public static final String CANCEL_ACTION = "Cancel";
	
	/**
	 * The action command for saving a portfolio name change.
	 */
	public static final String SAVE_PORTFOLIO_NAME_ACTION = "SavePortfolioName";
	
	/**
	 * The action command for opening a saved portfolio.
	 */
	public static final String OPEN_SELECTED_PORTFOLIO_ACTION = "OpenSelectedPortfolio";
	
	/**
	 * The initial cash balance the user starts with in this program.
	 */
	public static final double INITIAL_CASH_BALANCE_VALUE = 5_000.00;
	
	/**
	 * The initial length of the stock history to retrieve, in months.
	 */
	public static final int INITIAL_HISTORY_LENGTH_MONTHS = 6;
	
	/**
	 * The logger for this program.
	 */
	private static final Logger LOGGER = Logger.getLogger(
			"stockmarket.controller.StockMarketController");
	
	/**
	 * The data access object for saving/loading the program's state.
	 */
	private PortfolioDAO portfolioDAO;
	
	/**
	 * The shared <tt>Language</tt> module for locale-specific text.
	 */
	private static final Language LANGUAGE = Language.getInstance();
	
	/**
	 * Constructs an instance of this controller with no set thread pool size.
	 * Threads are created as needed, as many threads as are necessary, 
	 * and when they're no longer in use they are disposed of.
	 */
	public StockMarketController() {
		super();
		try{
			this.portfolioDAO = new SQLPortfolioDAO();
			((SQLPortfolioDAO) portfolioDAO).addPropertyChangeListener(this);
		}
		catch(IOException ex){
			ex = new IOException(
					"Database connection failed: " + ex.getMessage(), ex);
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), "Constructor",
					"Exception", ex);
		}
		
		setThreadFactory(new EventThreadFactory());
		
	}

	/**
	 * Constructs an instance of this controller with a fixed thread pool size.
	 * The exact number of threads specified here are created at initialization.
	 * Unless the properties of the thread pool are altered, there will never be
	 * more threads than this available during the lifespan of this object,
	 * and any threads that die will be replaced.
	 * 
	 * @param threadPoolSize the fixed size of the thread pool.
	 */
	public StockMarketController(int threadPoolSize){
		super(threadPoolSize);
		try{
			this.portfolioDAO = new SQLPortfolioDAO();
			((SQLPortfolioDAO) portfolioDAO).addPropertyChangeListener(this);
		}
		catch(IOException ex){
			ex = new IOException(
					"Database connection failed: " + ex.getMessage(), ex);
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), "Constructor",
					"Exception", ex);
		}
		setThreadFactory(new EventThreadFactory());
	}

	/**
	 * Constructs an instance of this controller with a much more detailed
	 * configuration. The parameters set the minimum number of threads to
	 * be available at all times (corePoolSize), the maximum number of threads
	 * that could be created (maximumPoolSize), and the time to keep any additional
	 * threads beyond the core pool size alive (keepAliveTime, unit). 
	 * 
	 * @param corePoolSize the minimum number of threads to be available at all times.
	 * @param maximumPoolSize the maximum number of threads that could be created.
	 * @param keepAliveTime the raw number for the amount of time to keep threads beyond
	 * the core pool size alive.
	 * @param unit the unit of time for the keepAliveTime value.
	 */
	public StockMarketController(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit){
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit);
		try{
			this.portfolioDAO = new SQLPortfolioDAO();
			((SQLPortfolioDAO) portfolioDAO).addPropertyChangeListener(this);
		}
		catch(IOException ex){
			ex = new IOException(
					"Database connection failed: " + ex.getMessage(), ex);
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), "Constructor",
					"Exception", ex);
		}
		setThreadFactory(new EventThreadFactory());
	}

	@Override
	protected void processEvent(final String actionCommand, final Object valueFromView) {
		if(actionCommand == BUY_STOCK_DIALOG_ACTION){
			Thread.currentThread().setName("ShowBuyStockDialog");
			showBuyStockDialog();
		}
		else if(actionCommand == BUY_STOCK_ACTION){
			Thread.currentThread().setName("BuyStock");
			buyStock(valueFromView);
		}
		else if(actionCommand == EDIT_PORTFOLIO_NAME_ACTION){
			Thread.currentThread().setName("EditPortfolioName");
			showPortfolioNameDialog();
		}
		else if(actionCommand == LOOKUP_PORTFOLIO_STOCK_ACTION){
			Thread.currentThread().setName("LookupPortfolioStock");
			lookupPortfolioStock(valueFromView);
		}
		else if(actionCommand == MARKET_DATA_ACTION){
			Thread.currentThread().setName("MarketWebData");
			marketDataWebpage();
		}
		else if(actionCommand == NEW_PORTFOLIO_ACTION){
			Thread.currentThread().setName("NewPortfolio");
			createNewPortfolio();
			showPortfolioNameDialog();
		}
		else if(actionCommand == OPEN_PORTFOLIO_ACTION){
			Thread.currentThread().setName("ShowPortfolioList");
			showOpenPortfolioDialog();
		}
		else if(actionCommand == OPEN_SELECTED_PORTFOLIO_ACTION){
			Thread.currentThread().setName("OpenPortfolio");
			openPortfolio(valueFromView);
		}
		else if(actionCommand == REFRESH_PORTFOLIO_ACTION){
			Thread.currentThread().setName("RefreshPortfolio");
			refreshPortfolio();
		}
		else if(actionCommand == SAVE_PORTFOLIO_ACTION){
			Thread.currentThread().setName("SavePortfolio");
			savePortfolio();
		}
		else if(actionCommand == SAVE_PORTFOLIO_NAME_ACTION){
			Thread.currentThread().setName("SavePortfolioName");
			savePortfolioName(valueFromView);
		}
		else if(actionCommand == SELL_STOCK_ACTION){
			Thread.currentThread().setName("SellStock");
			sellStock(valueFromView);
		}
		else if(actionCommand == SELL_STOCK_DIALOG_ACTION){
			Thread.currentThread().setName("ShowSellStockDialog");
			showSellStockDialog();
		}
		else if(actionCommand == STOCK_HISTORY_INTERVAL_ACTION){
			Thread.currentThread().setName("StockHistoryInterval");
			changeStockHistoryInterval(valueFromView);
		}
		else if(actionCommand == STOCK_SEARCH_ACTION){
			Thread.currentThread().setName("StockSearch");
			searchForStock(valueFromView);
		}
		
		Thread.currentThread().setName("EventActionThread");
		
	}
	
	/**
	 * Refresh all stocks in the portfolio.
	 */
	public void refreshPortfolio(){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"refreshPortfolio", "Entering method");
		
		//TODO needs a progress bar
		
		try {
			@SuppressWarnings("unchecked") //The getter method being called returns the correct type
			List<OwnedStock> stockList = (List<OwnedStock>) getModelProperty(STOCK_LIST_PROPERTY);
			StockDownloader downloader = new YahooStockDownloader();
			
			List<FutureTask<Stock>> downloadTasks = new ArrayList<>();
			for(OwnedStock s : stockList){
				downloadTasks.add(getDownloadStockTask(s, downloader));
			}
			
			//Use a new executor here to be able to await termination of all threads
			//before proceeding
			ExecutorService refreshExecutor = Executors.newCachedThreadPool();
			
			for(FutureTask<Stock> task : downloadTasks){
				refreshExecutor.submit(task);
			}
			
			refreshExecutor.awaitTermination(1, TimeUnit.MINUTES);
			
			setModelProperty(STOCK_LIST_PROPERTY, stockList);
			setModelProperty(PORTFOLIO_STATE_PROPERTY, PortfolioState.OPEN_NO_STOCK);
			
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"refreshPortfolio", "Refresh successful");
			
		}
		catch(InterruptedException ex){
			//TODO visual feedback? full pro-con in stock search method
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"refreshPortfolio", 
					"Exception", ex);
		}
		catch (NoSuchMethodException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"refreshPortfolio", 
					"Exception", ex);
		} 
		catch (IllegalAccessException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"refreshPortfolio", 
					"Exception", ex);
		} 
		catch (ReflectiveOperationException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"refreshPortfolio", 
					"Exception", ex);
		} 
		catch (Exception ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"refreshPortfolio", 
					"Exception", ex);
		}
	}
	
	/**
	 * Show the sell stock dialog.
	 */
	public void showSellStockDialog(){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"showSellStockDialog", "Entering method");
		
		try {
			Stock selectedStock = (Stock) getModelProperty(SELECTED_STOCK_PROPERTY);
			
			setModelProperty(DIALOG_DISPLAYED_PROPERTY, 
					Dialog.SELL_STOCK_DIALOG, selectedStock);
			
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"showSellStockDialog", "Sell Stock Dialog Displayed");
		} 
		catch (NoSuchMethodException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"showSellStockDialog", 
					"Exception", ex);
		} 
		catch (IllegalAccessException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"showSellStockDialog", 
					"Exception", ex);
		} 
		catch (ReflectiveOperationException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"showSellStockDialog", 
					"Exception", ex);
		} 
		catch (Exception ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"showSellStockDialog", 
					"Exception", ex);
		}
	}
	
	/**
	 * Sell shares of the selected stock.
	 * 
	 * @param valueFromView the number of shares to sell.
	 */
	public void sellStock(Object valueFromView){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"sellStock", "Entering method");
		
		int quantityToSell;
		if(valueFromView instanceof Integer){
			quantityToSell = (Integer) valueFromView;
		}
		else{
			throw new IllegalArgumentException("Not a valid Integer: " + valueFromView);
		}
		
		try {
			OwnedStock selectedStock = (OwnedStock) getModelProperty(SELECTED_STOCK_PROPERTY);
			BigDecimal profit = selectedStock.subtractShares(quantityToSell);
			BigDecimal cashBalance = (BigDecimal) getModelProperty(CASH_BALANCE_PROPERTY);
			cashBalance = cashBalance.add(profit);
			
			if(selectedStock.getQuantityOfShares() == 0){
				setModelProperty(PORTFOLIO_STATE_PROPERTY, PortfolioState.OPEN_STOCK);
			}
			else{
				setModelProperty(PORTFOLIO_STATE_PROPERTY, PortfolioState.OPEN_OWNED_STOCK);
			}
			setModelProperty(SELECTED_STOCK_PROPERTY, selectedStock);
			setModelProperty(STOCK_IN_LIST_PROPERTY, selectedStock);
			setModelProperty(CASH_BALANCE_PROPERTY, cashBalance);
			
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"sellStock", selectedStock.getSymbol() 
					+ " Shares sold: " + quantityToSell);
		} 
		catch (NoSuchMethodException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"sellStock", 
					"Exception", ex);
		} 
		catch (IllegalAccessException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"sellStock", 
					"Exception", ex);
		} 
		catch (ReflectiveOperationException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"sellStock", 
					"Exception", ex);
		} 
		catch (Exception ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"sellStock", 
					"Exception", ex);
		}
	}
	
	/**
	 * Purchase shares of the selected stock, based on the value
	 * provided from the view.
	 * 
	 * @param valueFromView the quantity of shares purchased.
	 * @throws IllegalArgumentException if the value from the view
	 * is not a valid integer.
	 * @throws InsufficientFundsException if the number of shares
	 * selected costs more than the value of the cash balance.
	 */
	public void buyStock(Object valueFromView){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"buyStock", "Entering method");
		
		int quantityToBuy;
		if(valueFromView instanceof Integer){
			quantityToBuy = (Integer) valueFromView;
		}
		else{
			throw new IllegalArgumentException("Not a valid Integer: " + valueFromView);
		}
		
		try {
			Stock selectedStock = (Stock) getModelProperty(SELECTED_STOCK_PROPERTY);
			BigDecimal cashBalance = (BigDecimal) getModelProperty(CASH_BALANCE_PROPERTY);
			
			BigDecimal cost = selectedStock.getCurrentPrice().multiply(new BigDecimal(quantityToBuy));
			if(cost.compareTo(cashBalance) > 0){
				throw new InsufficientFundsException("Cash: " + cashBalance + " Cost: " + cost);
			}
			cashBalance = cashBalance.subtract(cost);
			setModelProperty(CASH_BALANCE_PROPERTY, cashBalance);
			
			OwnedStock ownedStock = null;
			if(selectedStock instanceof OwnedStock){
				ownedStock = (OwnedStock) selectedStock;
				ownedStock.addShares(quantityToBuy);
			}
			else{
				ownedStock = new DefaultOwnedStock(selectedStock);
				ownedStock.addShares(quantityToBuy);
			}
			
			setModelProperty(STOCK_IN_LIST_PROPERTY, ownedStock);
			setModelProperty(PORTFOLIO_STATE_PROPERTY, PortfolioState.OPEN_OWNED_STOCK);
			setModelProperty(SELECTED_STOCK_PROPERTY, ownedStock);
		} 
		catch (NoSuchMethodException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"buyStock", 
					"Exception", ex);
		} 
		catch (IllegalAccessException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"buyStock", 
					"Exception", ex);
		} 
		catch (ReflectiveOperationException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"buyStock", 
					"Exception", ex);
		} 
		catch (Exception ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"buyStock", 
					"Exception", ex);
		}
		
	}
	
	/**
	 * Get the selected stock and cash balance, and then show the buy stock dialog.
	 */
	public void showBuyStockDialog(){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"showBuyStockDialog", "Entering method");
		
		try {
			Stock selectedStock = (Stock) getModelProperty(SELECTED_STOCK_PROPERTY);
			BigDecimal cashBalance = (BigDecimal) getModelProperty(CASH_BALANCE_PROPERTY);
			
			setModelProperty(DIALOG_DISPLAYED_PROPERTY, 
					Dialog.BUY_STOCK_DIALOG, selectedStock, cashBalance);
			
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"showBuyStockDialog", "Buy Stock Dialog Displayed");
		} 
		catch (NoSuchMethodException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"showBuyStockDialog", 
					"Exception", ex);
		} 
		catch (IllegalAccessException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"showBuyStockDialog", 
					"Exception", ex);
		} 
		catch (ReflectiveOperationException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"showBuyStockDialog", 
					"Exception", ex);
		} 
		catch (Exception ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"showBuyStockDialog", 
					"Exception", ex);
		}
	}
	
	/**
	 * Respond to a change in the stock history interval by downloading
	 * a new history for the new interval.
	 * 
	 * @param valueFromView a value from the view that is the interval
	 * to set for the history.
	 * @throws IllegalArgumentException if the value from the view is 
	 * not a valid integer.
	 * @throws ClassCastException if the value returned when getting the
	 * SELECTED_STOCK_PROPERTY is not an instance of <tt>Stock</tt>.
	 */
	public void changeStockHistoryInterval(Object valueFromView){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"changeStockHistoryInterval", "Entering method", 
				new Object[] {"Months: " + valueFromView});
		
		YahooStockDownloader downloader = new YahooStockDownloader();
		
		try {
			Integer months = null;
			if(valueFromView instanceof Integer){
				months = (Integer) valueFromView;
			}
			else{
				throw new IllegalArgumentException("Not valid integer: " + valueFromView);
			}
			
			Stock selectedStock = (Stock) getModelProperty(SELECTED_STOCK_PROPERTY);
			
			List<HistoricalQuote> historyList = 
					selectedStock.getStockHistory(downloader, months);
			
			setModelProperty(SELECTED_STOCK_HISTORY_PROPERTY, historyList);
			
			LOGGER.logp(Level.INFO, this.getClass().getName(), 
					"changeStockHistoryInterval", 
					"Stock History Interval Changed: " + months);
		}
		catch(UnknownHostException ex){
			displayExceptionDialog(LANGUAGE.getString("connect_exception_title"), 
					LANGUAGE.getString("connect_exception_message"));
			LOGGER.logp(Level.SEVERE, this.getClass().getName(),
					"changeStockHistoryInterval", "Exception", ex);
		}
		catch(InvalidStockException ex){
			displayExceptionDialog(
					LANGUAGE.getString("invalid_stock_title"), 
					"\"" + ex.getMessage() + "\" " 
					+ LANGUAGE.getString("invalid_stock_message"));
			LOGGER.logp(Level.SEVERE, this.getClass().getName(),
					"changeStockHistoryInterval", "Exception", ex);
		}
		catch(IOException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"changeStockHistoryInterval", 
					"Exception", ex);
		}
		catch(IllegalAccessException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"changeStockHistoryInterval", 
					"Exception", ex);
		}
		catch(NoSuchMethodException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"changeStockHistoryInterval", 
					"Exception", ex);
		}
		catch(ReflectiveOperationException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"changeStockHistoryInterval", 
					"Exception", ex);
		}
		catch(Exception ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"changeStockHistoryInterval", 
					"Exception", ex);
		}
		
	}
	
	/**
	 * Lookup and download the details for a stock currently
	 * owned in the stock portfolio.
	 * 
	 * @param valueFromView the index of the stock in the portfolio.
	 */
	public void lookupPortfolioStock(Object valueFromView){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"lookupPortfolioStock", "Entering method", 
				new Object[] {"Symbol: " + valueFromView});
		
		Integer index = null;
		if(valueFromView instanceof Integer){
			index = (Integer) valueFromView;
		}
		else{
			throw new IllegalArgumentException("Not a valid Integer: " + valueFromView);
		}
		
		try {
			if(index != null && index.compareTo(new Integer(0)) >= 0){
				StockDownloader downloader = new YahooStockDownloader();
				OwnedStock stock = (OwnedStock) getModelProperty(STOCK_IN_LIST_PROPERTY, index);
				
				if(stock != null){
					FutureTask<Stock> downloadStock = getDownloadStockTask(stock, downloader);
					FutureTask<List<HistoricalQuote>> downloadHistory = getDownloadHistoryTask(stock, downloader);
					
					eventExecutor.submit(downloadStock);
					eventExecutor.submit(downloadHistory);
					
					Stock downloadedStock = downloadStock.get();
					List<HistoricalQuote> historyList = downloadHistory.get();
					
					setModelProperty(PORTFOLIO_STATE_PROPERTY, PortfolioState.OPEN_OWNED_STOCK);
					setModelProperty(SELECTED_STOCK_PROPERTY, downloadedStock);
					setModelProperty(SELECTED_STOCK_HISTORY_PROPERTY, historyList);
					setModelProperty(STOCK_IN_LIST_PROPERTY, downloadedStock);
					
					LOGGER.logp(Level.INFO, this.getClass().getName(), 
							"lookupPortfolioStock", "Stock Found: " + stock.getSymbol());
				}
			}
		}
		catch(InterruptedException ex){
			Thread.currentThread().interrupt();
			//TODO consider whether or not to have a dialog here
			//Pro: visual feedback on interrupt
			//Con: interrupts are probably deliberately being done, so no need for the visual
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"lookupPortfolioStock", 
					"Exception", ex);
		}
		catch(ExecutionException ex){
			launderStockExecutionException(ex);
		}
		catch (NoSuchMethodException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"lookupPortfolioStock", 
					"Exception", ex);
		} 
		catch (IllegalAccessException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"lookupPortfolioStock", 
					"Exception", ex);
		} 
		catch (ReflectiveOperationException ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"lookupPortfolioStock", 
					"Exception", ex);
		} 
		catch (Exception ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"lookupPortfolioStock", 
					"Exception", ex);
		}
	}
	
	/**
	 * Search the web for information about the selected stock.
	 * 
	 * @param valueFromView the symbol of the selected stock.
	 * @throws IllegalArgumentException if the value from the view
	 * is not a valid String.
	 * @throws Error if an error occurs while trying to search for the stock.
	 */
	public void searchForStock(Object valueFromView){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"searchForStock", "Entering method", 
				new Object[] {"Symbol: " + valueFromView});
		
		String symbol = null;
		if(valueFromView instanceof String){
			symbol = ((String) valueFromView).toUpperCase();
		}
		else{
			throw new IllegalArgumentException("Not a valid String: " + valueFromView);
		}
		
		try {
			
			StockDownloader downloader = new YahooStockDownloader();
			Stock stock = (Stock) getModelProperty(STOCK_IN_LIST_PROPERTY, symbol);
			if(stock == null){
				stock = new DefaultStock(symbol);
			}
			
			FutureTask<Stock> downloadStock = getDownloadStockTask(stock, downloader);
			FutureTask<List<HistoricalQuote>> downloadHistory = getDownloadHistoryTask(stock, downloader);
			
			eventExecutor.submit(downloadStock);
			eventExecutor.submit(downloadHistory);
			
			Stock downloadedStock = downloadStock.get();
			List<HistoricalQuote> historyList = downloadHistory.get();
			
			if(stock instanceof OwnedStock){
				setModelProperty(PORTFOLIO_STATE_PROPERTY, PortfolioState.OPEN_OWNED_STOCK);
				setModelProperty(STOCK_IN_LIST_PROPERTY, stock);
			}
			else{
				setModelProperty(PORTFOLIO_STATE_PROPERTY, PortfolioState.OPEN_STOCK);
			}
			
			setModelProperty(SELECTED_STOCK_PROPERTY, downloadedStock);
			setModelProperty(SELECTED_STOCK_HISTORY_PROPERTY, historyList);
			
			LOGGER.logp(Level.INFO, this.getClass().getName(), 
					"searchForStock", "Stock Found: " + symbol);
		}
		catch(InterruptedException ex){
			Thread.currentThread().interrupt();
			//TODO consider whether or not to have a dialog here
			//Pro: visual feedback on interrupt
			//Con: interrupts are probably deliberately being done, so no need for the visual
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"searchForStock", 
					"Exception", ex);
		}
		catch(ExecutionException ex){
			launderStockExecutionException(ex);
		}
		catch(IllegalAccessException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"changeStockHistoryInterval", 
					"Exception", ex);
		}
		catch(NoSuchMethodException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"searchForStock", 
					"Exception", ex);
		}
		catch(ReflectiveOperationException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"changeStockHistoryInterval", 
					"Exception", ex);
		}
		catch(Exception ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"searchForStock", 
					"Exception", ex);
		}
		
	}
	
	/**
	 * Returns a <tt>FutureTask</tt> to download the specified stock using
	 * the downloader.
	 * 
	 * @param stock the stock to download details for.
	 * @param downloader the downloader.
	 * @return the task to download the specified stock.
	 */
	private FutureTask<Stock> getDownloadStockTask(final Stock stock, final StockDownloader downloader){
		Callable<Stock> callable = new Callable<Stock>(){
			@Override
			public Stock call() throws Exception {
				Thread.currentThread().setName("StockSearch");
				stock.setStockDetails(downloader, true);
				Thread.currentThread().setName("EventActionThread");
				
				return stock;
			}
		};
		
		FutureTask<Stock> downloadStock = new FutureTask<>(callable);
		return downloadStock;
	}
	
	/**
	 * Returns the <tt>FutureTask</tt> for downloading the specified
	 * stock's history using the specified downloader.
	 * 
	 * @param stock the stock to download history for.
	 * @param downloader the downloader.
	 * @return the task to download the stock's history.
	 */
	private FutureTask<List<HistoricalQuote>> getDownloadHistoryTask(final Stock stock, final StockDownloader downloader){
		Callable<List<HistoricalQuote>> callable = new Callable<List<HistoricalQuote>>(){
			@Override
			public List<HistoricalQuote> call() throws Exception {
				Thread.currentThread().setName("StockSearch");
				List<HistoricalQuote> list = 
						stock.getStockHistory(
								downloader, 
								INITIAL_HISTORY_LENGTH_MONTHS);
				Thread.currentThread().setName("EventActionThread");
				
				return list;
			}
		};
		
		FutureTask<List<HistoricalQuote>> downloadHistory = new FutureTask<>(callable);
		return downloadHistory;
	}
	
	/**
	 * Launder the <tt>ExecutionException</tt> from the stock search 
	 * and respond to it appropriately.
	 * 
	 * @param ex the execution exception to be laundered.
	 * @throws Error if an error has occured, it is rethrown.
	 */
	private void launderStockExecutionException(ExecutionException ex){
		if(ex.getCause() instanceof InvalidStockException){
			String[] messageArr = ex.getMessage().split(" ");
			String symbol = messageArr[messageArr.length - 1];
			
			displayExceptionDialog(
					LANGUAGE.getString("invalid_stock_title"), 
					"\"" + symbol + "\" " 
					+ LANGUAGE.getString("invalid_stock_message"));
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"launderStockExecutionException", 
					"Exception", ex.getCause());
		}
		else if(ex.getCause() instanceof UnknownHostException){
			displayExceptionDialog(LANGUAGE.getString("connect_exception_title"), 
					LANGUAGE.getString("connect_exception_message"));
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"launderStockExecutionException", 
					"Exception", ex.getCause());
		}
		else if(ex.getCause() instanceof IOException){
			displayExceptionDialog(ex.getCause());
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"launderStockExecutionException", 
					"Exception", ex.getCause());
		}
		else if(ex.getCause() instanceof RuntimeException){
			displayExceptionDialog(ex.getCause());
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"launderStockExecutionException", 
					"Exception", ex.getCause());
		}
		else if(ex.getCause() instanceof Error){
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"launderStockExecutionException", 
					"Error", ex.getCause());
			throw (Error) ex.getCause();
		}
		else{
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"launderStockExecutionException", 
					"Exception", ex);
		}
	}
	
	/**
	 * Open a webpage displaying market data in the default browser.
	 */
	public void marketDataWebpage(){
		LOGGER.logp(Level.FINEST, this.getClass().getName(),
				"marketDataWebpage", "Entering method");
		
		try {
			//Create the URL for the webpage and call on the default browser to open it.
			URI uri = new URI("http://finance.yahoo.com/stock-center/");
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"marketDataWebpage", "URI: " + uri);
			if(Desktop.isDesktopSupported()){
				Desktop.getDesktop().browse(uri);
			}
			else{
				displayExceptionDialog(LANGUAGE.getString("error_title"), 
						LANGUAGE.getString("desktop_not_supported"));
				
				LOGGER.logp(Level.SEVERE, this.getClass().getName(),
						"marketDataWebpage", "Desktop access not supported");
			}
			
			LOGGER.logp(Level.INFO, this.getClass().getName(),
					"marketDataWebpage", "Market data webpage is open");
		}
		catch(URISyntaxException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"marketDataWebpage", 
					"Exception", ex);
		}
		catch(IOException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"marketDataWebpage", 
					"Exception", ex);
		}
	}
	
	/**
	 * Open a saved stock portfolio.
	 * 
	 * @param valueFromView the name of the saved stock portfolio to open.
	 * @throws IllegalArgumentException if the value from the view 
	 * is not a valid String.
	 */
	public void openPortfolio(Object valueFromView){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"openPortfolio", "Entering method", 
				new Object[]{"|" + valueFromView + "|"});
		
		if(portfolioDAO != null){
			try {
				//Enable GUI components
				setModelProperty(PORTFOLIO_STATE_PROPERTY, PortfolioState.OPEN_NO_STOCK);
				
				String portfolioName = null;
				if(valueFromView instanceof String){
					portfolioName = (String) valueFromView;
				}
				else{
					throw new IllegalArgumentException("Not a valid String: " + valueFromView);
				}
				
				//Load portfolio model from the DAO
				PortfolioModel portfolioModel = portfolioDAO.getPortfolio(
						portfolioName);
				
				//If there's already a PortfolioModel in this controller, remove it.
				AbstractPropertyModel oldModel = null;
				synchronized(modelList){
					for(AbstractPropertyModel model : modelList){
						if(model instanceof PortfolioModel){
							oldModel = model;
							break;
						}
					}
				}
				if(oldModel != null){
					removePropertyModel(oldModel);
				}
				
				//Add new portfolio model
				addPropertyModel(portfolioModel);
				
				LOGGER.logp(Level.INFO, this.getClass().getName(), 
						"openPortfolio", "Portfolio loaded and opened in the program");
				
			}
			catch(IllegalAccessException ex){
				displayExceptionDialog(ex);
				LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
						"changeStockHistoryInterval", 
						"Exception", ex);
			}
			catch(NoSuchMethodException ex){
				displayExceptionDialog(ex);
				LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
						"openPortfolio", 
						"Exception", ex);
			}
			catch(ReflectiveOperationException ex){
				displayExceptionDialog(ex);
				LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
						"changeStockHistoryInterval", 
						"Exception", ex);
			}
			catch(InterruptedException ex){
				Thread.currentThread().interrupt();
				//TODO do I need a dialog here? see stock search method for pros and cons
				LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
						"openPortfolio", 
						"Exception", ex);
			}
			catch(Exception ex){
				displayExceptionDialog(ex);
				LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
						"openPortfolio", 
						"Exception", ex);
			}
		}
		else{
			displayExceptionDialog(LANGUAGE.getString("database_failed_title"), 
					LANGUAGE.getString("database_failed_text"));
		}
		
		
	}
	
	/**
	 * Show the open portfolio dialog so the user can select the saved 
	 * portfolio they want to open.
	 */
	public void showOpenPortfolioDialog(){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"showOpenPortfolioDialog", 
				"Entering method");
		
		if(portfolioDAO != null){
			try {
				List<String> portfolioNameList = portfolioDAO.getSavedPortfolios();
				
				setModelProperty(DIALOG_DISPLAYED_PROPERTY, 
						Dialog.OPEN_PORTFOLIO_DIALOG, portfolioNameList);
				
				LOGGER.logp(Level.INFO, this.getClass().getName(), 
						"showOpenPortfolioDialog()", 
						"Open portfolio dialog displayed");
			}
			catch(IllegalAccessException ex){
				displayExceptionDialog(ex);
				LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
						"changeStockHistoryInterval", 
						"Exception", ex);
			}
			catch(NoSuchMethodException ex){
				displayExceptionDialog(ex);
				LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
						"showPortfolioNameDialog()", 
						"Exception", ex);
			}
			catch(ReflectiveOperationException ex){
				displayExceptionDialog(ex);
				LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
						"changeStockHistoryInterval", 
						"Exception", ex);
			}
			catch(InterruptedException ex){
				Thread.currentThread().interrupt();
				//TODO visual or not? see stock search method for pros and cons
				LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
						"showPortfolioNameDialog()", 
						"Exception", ex);
			}
			catch (Exception ex) {
				displayExceptionDialog(ex);
				LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
						"showPortfolioNameDialog()", 
						"Exception", ex);
			}
		}
		else{
			displayExceptionDialog(LANGUAGE.getString("database_failed_title"), 
					LANGUAGE.getString("database_failed_text"));
		}
		
	}
	
	/**
	 * Display the portfolio name dialog, so that the portfolio name 
	 * can be edited.
	 */
	public void showPortfolioNameDialog(){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"showPortfolioNameDialog", 
				"Entering method");
		
		String name = "";
		try {
			Object obj = getModelProperty(PORTFOLIO_NAME_PROPERTY);
			if(obj != null && obj instanceof String){
				name = (String) obj;
			}
			
			setModelProperty(DIALOG_DISPLAYED_PROPERTY, 
					Dialog.PORTFOLIO_NAME_DIALOG, name);
			
			LOGGER.logp(Level.INFO, this.getClass().getName(), 
					"showPortfolioNameDialog", 
					"Portfolio name dialog displayed");
		}
		catch(IllegalAccessException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"changeStockHistoryInterval", 
					"Exception", ex);
		}
		catch(NoSuchMethodException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"showPortfolioNameDialog", 
					"Exception", ex);
		}
		catch(ReflectiveOperationException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"changeStockHistoryInterval", 
					"Exception", ex);
		}
		catch (Exception ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"showPortfolioNameDialog", 
					"Exception", ex);
		}
	}
	
	/**
	 * Create a new stock portfolio.
	 */
	public void createNewPortfolio(){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"newPortfolio", "Entering method");
		try{
			//Enable GUI components
			setModelProperty(PORTFOLIO_STATE_PROPERTY, PortfolioState.OPEN_NO_STOCK);
			
			PortfolioModel portfolioModel = null;
			if(portfolioDAO != null){
				portfolioModel = portfolioDAO.createNewPortfolio(
						LANGUAGE.getString("new_portfolio_name"), 
						new BigDecimal(INITIAL_CASH_BALANCE_VALUE));
			}
			else{
				portfolioModel = new PortfolioModel();
				portfolioModel.addPropertyChangeListener(this);
				portfolioModel.setPortfolioName(LANGUAGE.getString("new_portfolio_name"));
				portfolioModel.setInitialValue(new BigDecimal(INITIAL_CASH_BALANCE_VALUE));
				portfolioModel.setStockList(null);
				portfolioModel.removePropertyChangeListener(this);
				displayExceptionDialog(LANGUAGE.getString("database_failed_title"),
						LANGUAGE.getString("database_failed_text"));
			}
			
			//If there's already a PortfolioModel in this controller, remove it.
			AbstractPropertyModel oldModel = null;
			synchronized(modelList){
				for(AbstractPropertyModel model : modelList){
					if(model instanceof PortfolioModel){
						oldModel = model;
						break;
					}
				}
			}
			if(oldModel != null){
				removePropertyModel(oldModel);
			}
			
			//Add new portfolio model
			addPropertyModel(portfolioModel);
			
			LOGGER.logp(Level.INFO, this.getClass().getName(), 
					"newPortfolio()", "New portfolio created");
		}
		catch(IllegalAccessException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"changeStockHistoryInterval", 
					"Exception", ex);
		}
		catch(NoSuchMethodException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"newPortfolio", "Exception", ex);
		}
		catch(ReflectiveOperationException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"changeStockHistoryInterval", 
					"Exception", ex);
		}
		catch(InterruptedException ex){
			Thread.currentThread().interrupt();
			//TODO show dialog? see stock search method for pros and cons
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"newPortfolio", "Exception", ex);
		}
		catch(Exception ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"newPortfolio", "Exception", ex);
		}
	}
	
	/**
	 * Save the new portfolio name.
	 * 
	 * @param valueFromView the new portfolio name.
	 * @throws IllegalArgumentException if valueFromView is not a valid <tt>String</tt>.
	 */
	public void savePortfolioName(Object valueFromView){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"savePortfolioName", "Entering method", 
				new Object[] {"Name: " + valueFromView});
		
		//Get the new name of the portfolio 
		String newName = "";
		if(valueFromView instanceof String){
			newName = (String) valueFromView;
		}
		else{
			throw new IllegalArgumentException(valueFromView 
					+ " is not a valid String");
		}
		
		//Set the name to the model, and save the change to the database.
		try {
			setModelProperty(PORTFOLIO_NAME_PROPERTY, newName);
			savePortfolio();
			LOGGER.logp(Level.INFO, this.getClass().getName(), 
					"savePortfolioName", "Portfolio Name Saved");
		}
		catch(IllegalAccessException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"changeStockHistoryInterval", 
					"Exception", ex);
		}
		catch(NoSuchMethodException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"savePortfolioName", "Exception", ex);
		}
		catch(ReflectiveOperationException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"changeStockHistoryInterval", 
					"Exception", ex);
		}
		catch(InterruptedException ex){
			Thread.currentThread().interrupt();
			//TODO gui dialog? see stock search method for pros and cons
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"savePortfolioName", "Exception", ex);
		}
		catch (Exception ex) {
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"savePortfolioName", "Exception", ex);
		}
	}
	
	/**
	 * Save the portfolio to the database.
	 */
	public void savePortfolio(){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"savePortfolio", "Entering method");
		
		if(portfolioDAO != null){
			PortfolioModel portfolioModel = null;
			synchronized(modelList){
				for(AbstractPropertyModel model : modelList){
					if(model instanceof PortfolioModel){
						portfolioModel = (PortfolioModel) model;
						break;
					}
				}
			}
			
			if(portfolioModel != null){
				LOGGER.logp(Level.FINEST, this.getClass().getName(), 
						"savePortfolio", "Portfolio Model found and "
								+ "about to try to save it");
				
				try {
					portfolioDAO.savePortfolio(portfolioModel);
					LOGGER.logp(Level.INFO, this.getClass().getName(), 
							"savePortfolio", "Portfolio saved successfully");
				}
				catch(InterruptedException ex){
					Thread.currentThread().interrupt();
					//TODO gui dialog? see stock search method for pros and cons
					LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
							"savePortfolio", "Exception", ex);
				}
				catch (Exception ex) {
					displayExceptionDialog(ex);
					LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
							"savePortfolio", "Exception", ex);
				}
			}
			else{
				LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
						"savePortfolio", "Portfolio Model not found and saving"
								+ " has failed");
			}
		}
		else{
			displayExceptionDialog(LANGUAGE.getString("database_failed_title"),
					LANGUAGE.getString("database_failed_text"));
		}
		
	}
	
	/**
	 * Display a dialog to show the details of an exception. This method is
	 * safe to be invoked from multiple threads because it doesn't access
	 * any mutable state of this class, and all actions it causes occur
	 * only on the <tt>EventDispatchThread</tt>.
	 * 
	 * @param t the throwable/exception to display the details of.
	 */
	private void displayExceptionDialog(final Throwable t){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				ListenerDialog exceptionDialog = DialogFactory.createExceptionDialog(null, t);
				exceptionDialog.showDialog();
			}
		});
	}
	
	/**
	 * Display a dialog to show the details of an exception. This method is
	 * safe to be invoked from multiple threads because it doesn't access
	 * any mutable state of this class, and all actions it causes occur
	 * only on the <tt>EventDispatchThread</tt>.
	 * 
	 * @param title the title for the dialog.
	 * @param message the message for the dialog.
	 */
	private void displayExceptionDialog(final String title, final String message){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				ListenerDialog exceptionDialog = DialogFactory.createExceptionDialog(null, title, message);
				exceptionDialog.showDialog();
			}
		});
	}
	
	/**
	 * The thread factory used by the executor in this controller.
	 * 
	 * @author craig
	 * @version 2.0
	 */
	private class EventThreadFactory implements ThreadFactory{

		@Override
		public Thread newThread(Runnable r) {
			return new EventActionThread(r);
		}
		
	}
	
	/**
	 * The thread used by the executor in this controller. Sets
	 * a custom thread name and an <tt>UncaughtExceptionHandler</tt>.
	 * 
	 * @author craig
	 * @version 2.0
	 */
	private class EventActionThread extends Thread{
		
		/**
		 * Creates a new thread of this type.
		 * 
		 * @param r the task for the thread to execute.
		 */
		public EventActionThread(Runnable r){
			super(r);
			setName("EventActionThread");
			setUncaughtExceptionHandler(new EventUncaughtExceptionHandler());
		}
		
	}
	
	/**
	 * The <tt>UncaughtExceptionHandler</tt> for the threads created
	 * by this class.
	 * 
	 * @author craig
	 * @version 2.0
	 *
	 */
	private class EventUncaughtExceptionHandler implements UncaughtExceptionHandler{

		@Override
		public void uncaughtException(Thread thread, Throwable throwable) {
			displayExceptionDialog(throwable);
			
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"uncaughtException()", "Uncaught Exception: " + thread.getName() 
					+ "." + thread.getId(), throwable);
		}
		
	}

}
