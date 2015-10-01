package io.craigmiller160.stockmarket.controller;

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

import javax.swing.SwingUtilities;
import javax.transaction.NotSupportedException;

import org.hibernate.HibernateException;

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
 * <p>As of version 2.2, the DAO for handling database access is not generated
 * inside this class. Rather, it is injected via a setter method. If this setter
 * is not used to set this value at program startup, this controller will be
 * unable to save/load to and from the database.
 * <p>
 * <b>THREAD SAFETY:</b> This class is thread safe. All methods invoked by 
 * <tt>processEvent(String,Object)</tt> are properly constructed so that they 
 * can be accessed by multiple threads simultaneously.
 * 
 * @author craig
 * @version 2.3
 */
@ThreadSafe
public class StockMarketController extends AbstractConcurrentListenerController {
	
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
		setThreadFactory(new EventThreadFactory());
	}
	
	/**
	 * Set the <tt>PortfolioDAO</tt> object for saving and loading
	 * portfolios to and from the database. If this property is not
	 * set at the start of the program, the program will be unable
	 * to access the database.
	 * 
	 * @param dao the DAO to handle database access.
	 */
	public void setPortfolioDAO(PortfolioDAO dao){
		this.portfolioDAO = dao;
		this.portfolioDAO.addPropertyChangeListener(this);
	}
	
	/**
	 * Get the <tt>PortfolioDAO</tt> object currently
	 * set in this class.
	 * 
	 * @return the DAO to handle database access.
	 */
	public PortfolioDAO getPortfolioDAO(){
		return portfolioDAO;
	}

	/**
	 * {@inheritDoc}
	 * @throws RuntimeException if a runtime exception occurs during the operation
	 * of this program.
	 * @throws Error if an error occurs while executing the download.
	 */
	@Override
	protected void processEvent(final String actionCommand, final Object valueFromView) {
		try{
			parseEvent(actionCommand, valueFromView);
		}
		catch(InterruptedException ex){
			Thread.currentThread().interrupt();
		}
		catch(ExecutionException ex){
			launderStockExecutionException(ex);
		}
		catch(InsufficientFundsException ex){
			displayExceptionDialog(LANGUAGE.getString("insufficient_funds_title"), 
					LANGUAGE.getString("insufficient_funds_cash_message"));
		}
		catch(UnknownHostException ex){
			displayExceptionDialog(LANGUAGE.getString("connect_exception_title"), 
					LANGUAGE.getString("connect_exception_message"));
		}
		catch(InvalidStockException ex){
			displayExceptionDialog(
					LANGUAGE.getString("invalid_stock_title"), 
					"\"" + ex.getMessage() + "\" " 
					+ LANGUAGE.getString("invalid_stock_message"));
		}
		catch(Exception ex){
			if(ex instanceof RuntimeException){
				throw (RuntimeException) ex;
			}
			else{
				displayExceptionDialog(ex);
			}
		}
		
		Thread.currentThread().setName("EventActionThread");
		
	}
	
	/**
	 * Parse the event to invoke the correct action method.
	 * 
	 * @param actionCommand the action to be performed.
	 * @param valueFromView  the value returned by <tt>getValueForAction(String)</tt> in the 
	 * <tt>ListenerView</tt> interface. If no value is set to be returned by the view that 
	 * sent this event, this parameter will be null.
	 * @throws InterruptedException if the thread is interrupted while
	 * waiting on a task to complete.
	 * @throws NoSuchMethodException if the reflective operation cannot
	 * find a matching method in a registered model.
	 * @throws IllegalAccessException if the method attempting to be 
	 * accessed by the reflective operation is not accessible by this class.
	 * @throws ReflectiveOperationException if any other problem occurs
	 * while attempting to execute the reflective method.
	 * @throws ExecutionException if an exception occurs while trying to
	 * execute the operation.
	 * @throws InvalidStockException if the stock to download data for
	 * is not a valid stock.
	 * @throws IOException if the web data is unable to be properly 
	 * downloaded/parsed.
	 * @throws UnknownHostException if a connection cannot be made to
	 * the server to download the data.
	 * @throws Exception if any other kind of exception occurs during this operation.
	 * @throws URISyntaxException if the URI for the webpage is invalid.
	 * @throws RuntimeException if a runtime exception occurs while
	 * executing the download.
	 * @throws Error if an error occurs while executing the download.
	 * @throws NotSupportedException if desktop access is not supported
	 * by the platform running this application.
	 * @throws NoDaoException if the DAO object has not been set in this class.
	 */
	private void parseEvent(String actionCommand, Object valueFromView) throws Exception{
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
	}
	
	/**
	 * Refresh all stocks in the portfolio.
	 * 
	 * @throws InterruptedException if the thread is interrupted while
	 * waiting on a task to complete.
	 * @throws NoSuchMethodException if the reflective operation cannot
	 * find a matching method in a registered model.
	 * @throws IllegalAccessException if the method attempting to be 
	 * accessed by the reflective operation is not accessible by this class.
	 * @throws ReflectiveOperationException if any other problem occurs
	 * while attempting to execute the reflective method.
	 * @throws ExecutionException if an exception occurs while trying to
	 * execute the operation.
	 * @throws Exception if any other kind of exception occurs while 
	 * reflectively executing the method.
	 * 
	 */
	public void refreshPortfolio() throws InterruptedException, ExecutionException, Exception{
		@SuppressWarnings("unchecked") //The getter method being called returns the correct type
		List<OwnedStock> oldStockList = (List<OwnedStock>) getModelProperty(STOCK_LIST_PROPERTY);
		StockDownloader downloader = new YahooStockDownloader();
		
		List<FutureTask<Stock>> downloadTasks = new ArrayList<>();
		for(OwnedStock s : oldStockList){
			downloadTasks.add(getDownloadStockTask(s, downloader));
		}
		
		//Use a new executor here to be able to await termination of all threads
		//before proceeding
		ExecutorService refreshExecutor = Executors.newCachedThreadPool();
		
		for(FutureTask<Stock> task : downloadTasks){
			refreshExecutor.submit(task);
		}
		
		refreshExecutor.shutdown();
		refreshExecutor.awaitTermination(1, TimeUnit.MINUTES);
		
		List<OwnedStock> newStockList = new ArrayList<>();
		for(FutureTask<Stock> task : downloadTasks){
			newStockList.add((OwnedStock) task.get());
		}
		
		setModelProperty(STOCK_LIST_PROPERTY, oldStockList);
		setModelProperty(PORTFOLIO_STATE_PROPERTY, PortfolioState.OPEN_NO_STOCK);
	}
	
	/**
	 * Show the sell stock dialog.
	 * 
	 * @throws NoSuchMethodException if the reflective operation cannot
	 * find a matching method in a registered model.
	 * @throws IllegalAccessException if the method attempting to be 
	 * accessed by the reflective operation is not accessible by this class.
	 * @throws ReflectiveOperationException if any other problem occurs
	 * while attempting to execute the reflective method.
	 * @throws Exception if any other kind of exception occurs while 
	 * reflectively executing the method.
	 */
	public void showSellStockDialog() throws Exception{
		Stock selectedStock = (Stock) getModelProperty(SELECTED_STOCK_PROPERTY);
		setModelProperty(DIALOG_DISPLAYED_PROPERTY, 
				Dialog.SELL_STOCK_DIALOG, selectedStock);
	}
	
	/**
	 * Sell shares of the selected stock.
	 * 
	 * @param valueFromView the number of shares to sell.
	 * @throws IllegalArgumentException if <tt>valueFromView</tt> is not a 
	 * valid <tt>Integer</tt>.
	 * @throws NoSuchMethodException if the reflective operation cannot
	 * find a matching method in a registered model.
	 * @throws IllegalAccessException if the method attempting to be 
	 * accessed by the reflective operation is not accessible by this class.
	 * @throws ReflectiveOperationException if any other problem occurs
	 * while attempting to execute the reflective method.
	 * @throws Exception if any other kind of exception occurs while 
	 * reflectively executing the method.
	 */
	public void sellStock(Object valueFromView) throws Exception{
		int quantityToSell;
		if(valueFromView instanceof Integer){
			quantityToSell = (Integer) valueFromView;
		}
		else{
			throw new IllegalArgumentException("Not a valid Integer: " + valueFromView);
		}
		
		
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
	}
	
	/**
	 * Purchase shares of the selected stock, based on the value
	 * provided from the view.
	 * 
	 * @param valueFromView the quantity of shares purchased.
	 * @throws IllegalArgumentException if the <tt>valueFromView</tt>
	 * is not a valid <tt>Integer</tt>.
	 * @throws NoSuchMethodException if the reflective operation cannot
	 * find a matching method in a registered model.
	 * @throws IllegalAccessException if the method attempting to be 
	 * accessed by the reflective operation is not accessible by this class.
	 * @throws ReflectiveOperationException if any other problem occurs
	 * while attempting to execute the reflective method.
	 * @throws Exception if any other kind of exception occurs while 
	 * reflectively executing the method.
	 * @throws InsufficientFundsException if there is not enough cash
	 * available to purchase the selected number of shares of the stock.
	 */
	public void buyStock(Object valueFromView) throws Exception{
		int quantityToBuy;
		if(valueFromView instanceof Integer){
			quantityToBuy = (Integer) valueFromView;
		}
		else{
			throw new IllegalArgumentException("Not a valid Integer: " + valueFromView);
		}
		
		
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
	
	/**
	 * Get the selected stock and cash balance, and then show the buy stock dialog.
	 * 
	 * @throws NoSuchMethodException if the reflective operation cannot
	 * find a matching method in a registered model.
	 * @throws IllegalAccessException if the method attempting to be 
	 * accessed by the reflective operation is not accessible by this class.
	 * @throws ReflectiveOperationException if any other problem occurs
	 * while attempting to execute the reflective method.
	 * @throws Exception if any other kind of exception occurs while 
	 * reflectively executing the method.
	 */
	public void showBuyStockDialog() throws Exception{
		Stock selectedStock = (Stock) getModelProperty(SELECTED_STOCK_PROPERTY);
		BigDecimal cashBalance = (BigDecimal) getModelProperty(CASH_BALANCE_PROPERTY);
		
		setModelProperty(DIALOG_DISPLAYED_PROPERTY, 
				Dialog.BUY_STOCK_DIALOG, selectedStock, cashBalance);
	}
	
	/**
	 * Respond to a change in the stock history interval by downloading
	 * a new history for the new interval.
	 * 
	 * @param valueFromView a value from the view that is the interval
	 * to set for the history.
	 * @throws IllegalArgumentException if the <tt>valueFromView</tt> is 
	 * not a valid <tt>Integer</tt>.
	 * @throws ClassCastException if the value returned when getting the
	 * SELECTED_STOCK_PROPERTY is not an instance of <tt>Stock</tt>.
	 * @throws NoSuchMethodException if the reflective operation cannot
	 * find a matching method in a registered model.
	 * @throws IllegalAccessException if the method attempting to be 
	 * accessed by the reflective operation is not accessible by this class.
	 * @throws ReflectiveOperationException if any other problem occurs
	 * while attempting to execute the reflective method.
	 * @throws Exception if any other kind of exception occurs while 
	 * reflectively executing the method.
	 * @throws InvalidStockException if the stock to download data for
	 * is not a valid stock.
	 * @throws IOException if the web data is unable to be properly 
	 * downloaded/parsed.
	 * @throws UnknownHostException if a connection cannot be made to
	 * the server to download the data.
	 */
	public void changeStockHistoryInterval(Object valueFromView) throws Exception{
		YahooStockDownloader downloader = new YahooStockDownloader();
		
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
	}
	
	/**
	 * Lookup and download the details for a stock currently
	 * owned in the stock portfolio.
	 * 
	 * @param valueFromView the index of the stock in the portfolio.
	 * @throws NoSuchMethodException if the reflective operation cannot
	 * find a matching method in a registered model.
	 * @throws IllegalAccessException if the method attempting to be 
	 * accessed by the reflective operation is not accessible by this class.
	 * @throws ReflectiveOperationException if any other problem occurs
	 * while attempting to execute the reflective method.
	 * @throws InterruptedException if the thread is interrupted while
	 * waiting on a task to complete.
	 * @throws ExecutionException if an exception occurs while trying to
	 * execute the operation.
	 * @throws Exception if any other kind of exception occurs while 
	 * reflectively executing the method.
	 * @throws Error if an error occurs while trying to search for the stock.
	 */
	public void lookupPortfolioStock(Object valueFromView) throws Exception{
		Integer index = null;
		if(valueFromView instanceof Integer){
			index = (Integer) valueFromView;
		}
		else{
			throw new IllegalArgumentException("Not a valid Integer: " + valueFromView);
		}
		
		
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
			}
		}
		
	}
	
	/**
	 * Search the web for information about the selected stock.
	 * 
	 * @param valueFromView the symbol of the selected stock.
	 * @throws IllegalArgumentException if the value from the view
	 * is not a valid String.
	 * @throws Error if an error occurs while trying to search for the stock.
	 * @throws RuntimeException if a runtime exception occurs while
	 * executing the download.
	 * @throws NoSuchMethodException if the reflective operation cannot
	 * find a matching method in a registered model.
	 * @throws IllegalAccessException if the method attempting to be 
	 * accessed by the reflective operation is not accessible by this class.
	 * @throws ReflectiveOperationException if any other problem occurs
	 * while attempting to execute the reflective method.
	 * @throws InterruptedException if the thread is interrupted while
	 * waiting on a task to complete.
	 * @throws ExecutionException if an exception occurs while trying to
	 * execute the operation.
	 * @throws Exception if any other kind of exception occurs while 
	 * reflectively executing the method.
	 */
	public void searchForStock(Object valueFromView) throws Exception{
		String symbol = null;
		if(valueFromView instanceof String){
			symbol = ((String) valueFromView).toUpperCase();
		}
		else{
			throw new IllegalArgumentException("Not a valid String: " + valueFromView);
		}
			
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
		}
		else if(ex.getCause() instanceof UnknownHostException){
			displayExceptionDialog(LANGUAGE.getString("connect_exception_title"), 
					LANGUAGE.getString("connect_exception_message"));
		}
		else if(ex.getCause() instanceof Error){
			throw (Error) ex.getCause();
		}
		else if(ex.getCause() instanceof RuntimeException){
			throw (RuntimeException) ex.getCause();
		}
		else{
			displayExceptionDialog(ex);
		}
	}
	
	/**
	 * Open a webpage displaying market data in the default browser.
	 * @throws URISyntaxException if the URI for the webpage is invalid.
	 * @throws IOException if unable to access the webpage.
	 * @throws NotSupportedException if desktop access is not supported
	 * by the platform running this application.
	 */
	public void marketDataWebpage() throws Exception{
		//Create the URL for the webpage and call on the default browser to open it.
		URI uri = new URI("http://finance.yahoo.com/stock-center/");
		if(Desktop.isDesktopSupported()){
			Desktop.getDesktop().browse(uri);
		}
		else{
			displayExceptionDialog(LANGUAGE.getString("error_title"), 
					LANGUAGE.getString("desktop_not_supported"));
			throw new NotSupportedException("Desktop Access is not supported");
		}
	}
	
	/**
	 * Open a saved stock portfolio.
	 * 
	 * @param valueFromView the name of the saved stock portfolio to open.
	 * @throws IllegalArgumentException if the value from the view 
	 * is not a valid String.
	 * @throws HibernateException if Hibernate is unable to perform the
	 * database operation.
	 * @throws NoSuchMethodException if the reflective operation cannot
	 * find a matching method in a registered model.
	 * @throws IllegalAccessException if the method attempting to be 
	 * accessed by the reflective operation is not accessible by this class.
	 * @throws ReflectiveOperationException if any other problem occurs
	 * while attempting to execute the reflective method.
	 * @throws Exception if another exception occurs while attempting to 
	 * execute this operation.
	 */
	public void openPortfolio(Object valueFromView) throws Exception{
		if(portfolioDAO != null){
			
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
			
			firePortfolioPropertyChanges(portfolioModel);
		}
		else{
			displayExceptionDialog(LANGUAGE.getString("database_failed_title"), 
					LANGUAGE.getString("database_failed_text"));
			throw new NoDaoException("DAO not set, cannot access database");
		}
	}
	
	/**
	 * Force fire <tt>PropertyChangeEvents</tt> for the properties
	 * of a portfolio.
	 * 
	 * @param portfolioModel the portfolio to fire change events on.
	 */
	private void firePortfolioPropertyChanges(PortfolioModel portfolioModel){
		portfolioModel.forceFirePropertyChangeEvent(STOCK_LIST_PROPERTY);
		portfolioModel.forceFirePropertyChangeEvent(PORTFOLIO_NAME_PROPERTY);
		portfolioModel.forceFirePropertyChangeEvent(TOTAL_STOCK_VALUE_PROPERTY);
		portfolioModel.forceFirePropertyChangeEvent(NET_WORTH_PROPERTY);
		portfolioModel.forceFirePropertyChangeEvent(CHANGE_IN_NET_WORTH_PROPERTY);
		portfolioModel.forceFirePropertyChangeEvent(CASH_BALANCE_PROPERTY);
	}
	
	/**
	 * Show the open portfolio dialog so the user can select the saved 
	 * portfolio they want to open.
	 * @throws NoSuchMethodException if the reflective operation cannot
	 * find a matching method in a registered model.
	 * @throws IllegalAccessException if the method attempting to be 
	 * accessed by the reflective operation is not accessible by this class.
	 * @throws ReflectiveOperationException if any other problem occurs
	 * while attempting to execute the reflective method.
	 * @throws Exception if another exception occurs while attempting to 
	 * execute this operation.
	 */
	public void showOpenPortfolioDialog() throws Exception{
		if(portfolioDAO != null){
			
			List<String> portfolioNameList = portfolioDAO.getSavedPortfolios();
			
			setModelProperty(DIALOG_DISPLAYED_PROPERTY, 
					Dialog.OPEN_PORTFOLIO_DIALOG, portfolioNameList);
		}
		else{
			displayExceptionDialog(LANGUAGE.getString("database_failed_title"), 
					LANGUAGE.getString("database_failed_text"));
			throw new NoDaoException("DAO not set, cannot access database");
		}
		
	}
	
	/**
	 * Display the portfolio name dialog, so that the portfolio name 
	 * can be edited.
	 * @throws NoSuchMethodException if the reflective operation cannot
	 * find a matching method in a registered model.
	 * @throws IllegalAccessException if the method attempting to be 
	 * accessed by the reflective operation is not accessible by this class.
	 * @throws ReflectiveOperationException if any other problem occurs
	 * while attempting to execute the reflective method.
	 * @throws Exception if another exception occurs while attempting to 
	 * execute this operation.
	 * @throws InterruptedException if the thread is interrupted while
	 * attempting to execute this method.
	 * @throws HibernateException if Hibernate is unable to perform the
	 * database operation.
	 */
	public void showPortfolioNameDialog() throws Exception{
		String name = "";
		
		Object obj = getModelProperty(PORTFOLIO_NAME_PROPERTY);
		if(obj != null && obj instanceof String){
			name = (String) obj;
		}
		
		setModelProperty(DIALOG_DISPLAYED_PROPERTY, 
				Dialog.PORTFOLIO_NAME_DIALOG, name);
	}
	
	/**
	 * Create a new stock portfolio.
	 * @throws NoSuchMethodException if the reflective operation cannot
	 * find a matching method in a registered model.
	 * @throws IllegalAccessException if the method attempting to be 
	 * accessed by the reflective operation is not accessible by this class.
	 * @throws ReflectiveOperationException if any other problem occurs
	 * while attempting to execute the reflective method.
	 * @throws Exception if another exception occurs while attempting to 
	 * execute this operation.
	 * @throws HibernateException if Hibernate is unable to perform
	 * the database operation.
	 * @throws InterruptedException if the thread is interrupted while
	 * attempting to execute this method.
	 */
	public void createNewPortfolio() throws Exception{
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
		
		firePortfolioPropertyChanges(portfolioModel);
	}
	
	/**
	 * Save the new portfolio name.
	 * 
	 * @param valueFromView the new portfolio name.
	 * @throws IllegalArgumentException if valueFromView is not a valid <tt>String</tt>.
	 * @throws NoSuchMethodException if the reflective operation cannot
	 * find a matching method in a registered model.
	 * @throws IllegalAccessException if the method attempting to be 
	 * accessed by the reflective operation is not accessible by this class.
	 * @throws ReflectiveOperationException if any other problem occurs
	 * while attempting to execute the reflective method.
	 * @throws Exception if another exception occurs while attempting to 
	 * execute this operation.
	 * @throws HibernateException if Hibernate is unable to perform the
	 * database operation.
	 * @throws InterruptedException if the thread is interrupted while
	 * attempting to execute this method.
	 */
	public void savePortfolioName(Object valueFromView) throws Exception{
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
		
		setModelProperty(PORTFOLIO_NAME_PROPERTY, newName);
		savePortfolio();
	}
	
	/**
	 * Save the portfolio to the database.
	 * @throws NoSuchMethodException if the reflective operation cannot
	 * find a matching method in a registered model.
	 * @throws IllegalAccessException if the method attempting to be 
	 * accessed by the reflective operation is not accessible by this class.
	 * @throws ReflectiveOperationException if any other problem occurs
	 * while attempting to execute the reflective method.
	 * @throws Exception if another exception occurs while attempting to 
	 * execute this operation.
	 * @throws HibernateException if Hibernate is unable to perform the
	 * database operation.
	 * @throws InterruptedException if the thread is interrupted while
	 * attempting to execute this method.
	 * @throws NullPointerException if the <tt>PortfolioModel</tt> isn't
	 * found in the list of registered models.
	 */
	public void savePortfolio() throws Exception{
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
				portfolioDAO.savePortfolio(portfolioModel);
			}
			else{
				throw new NullPointerException("PortfolioModel not found");
			}
		}
		else{
			displayExceptionDialog(LANGUAGE.getString("database_failed_title"),
					LANGUAGE.getString("database_failed_text"));
			throw new NoDaoException("DAO not set, cannot access database");
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
			//TODO add additional handling for specific RuntimeExceptions that
			//should result in system shutdown
			if(throwable instanceof Error){
				System.exit(1);
			}
		}
		
	}

}
