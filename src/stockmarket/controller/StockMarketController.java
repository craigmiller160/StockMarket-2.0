package stockmarket.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import mvp.concurrent.AbstractConcurrentListenerController;
import mvp.core.AbstractPropertyModel;
import mvp.listener.ListenerDialog;
import stockmarket.gui.PortfolioState;
import stockmarket.gui.dialog.Dialog;
import stockmarket.gui.dialog.DialogFactory;
import stockmarket.model.PortfolioModel;
import stockmarket.stock.DefaultStock;
import stockmarket.stock.HistoricalQuote;
import stockmarket.stock.InvalidStockException;
import stockmarket.stock.Stock;
import stockmarket.stock.StockDownloader;
import stockmarket.stock.YahooStockDownloader;
import stockmarket.util.Language;

//TODO document how all property names and action commands and shared values are in this class

//TODO document how action commands are the constant value to use with the getter
//in abstractlistenerview classes

public class StockMarketController extends AbstractConcurrentListenerController {

	//TODO the StockMarketController will need its catch blocks changed to reflect
		//the changes in the throws statement in this class.
	
	public static final String STOCK_LIST_PROPERTY = "StockList";
	
	public static final String PORTFOLIO_NAME_PROPERTY = "PortfolioName";
	
	public static final String TOTAL_STOCK_VALUE_PROPERTY = "TotalStockValue";
	
	public static final String NET_WORTH_PROPERTY = "NetWorth";
	
	public static final String CHANGE_IN_NET_WORTH_PROPERTY = "ChangeInNetWorth";
	
	public static final String CASH_BALANCE_PROPERTY = "CashBalance";
	
	public static final String USER_ID_PROPERTY = "UserID";
	
	public static final String SELECTED_STOCK_PROPERTY = "SelectedStock";
	
	public static final String SELECTED_STOCK_HISTORY_PROPERTY = "SelectedStockHistory";
	
	public static final String PORTFOLIO_STATE_PROPERTY = "PortfolioState";
	
	public static final String DIALOG_DISPLAYED_PROPERTY = "DialogDisplayed";
	
	public static final String NEW_PORTFOLIO_ACTION = "NewPortfolio";
	
	public static final String OPEN_PORTFOLIO_ACTION = "OpenPortfolio";
	
	public static final String SAVE_PORTFOLIO_ACTION = "SavePortfolio";
	
	public static final String CLOSE_PORTFOLIO_ACTION = "ClosePortfolio";
	
	public static final String EXIT_PROGRAM_ACTION = "ExitProgram";
	
	public static final String ABOUT_PROGRAM_ACTION = "AboutProgram";
	
	public static final String LANGUAGE_MENU_ACTION = "LanguageMenu";
	
	public static final String DEBUG_MENU_ACTION = "DebugMenu";
	
	public static final String EDIT_PORTFOLIO_NAME_ACTION = "EditPortfolioName";
	
	public static final String MARKET_DATA_ACTION = "MarketData";
	
	public static final String REFRESH_PORTFOLIO_ACTION = "RefreshPortfolio";
	
	public static final String STOCK_DETAILS_ACTION = "StockDetails";
	
	public static final String STOCK_SEARCH_ACTION = "StockSearch";
	
	public static final String STOCK_HISTORY_INTERVAL_ACTION = "StockHistoryInterval";
	
	public static final String BUY_STOCK_PREP_ACTION = "BuyStockPrep";
	
	public static final String SELL_STOCK_PREP_ACTION = "SellStockPrep";
	
	public static final String BUY_STOCK_ACTION = "BuyStock";
	
	public static final String SELL_STOCK_ACTION = "SellStock";
	
	public static final String CANCEL_ACTION = "Cancel";
	
	public static final String SAVE_PORTFOLIO_NAME_ACTION = "SavePortfolioName";
	
	public static final String OPEN_SELECTED_PORTFOLIO_ACTION = "OpenSelectedPortfolio";
	
	public static final double INITIAL_CASH_BALANCE_VALUE = 5_000.00;
	
	//TODO this needs to be used in the ChartPanel to set the combo box
	public static final int INITIAL_HISTORY_LENGTH_MONTHS = 6;
	
	/**
	 * Components Enabled value. Enable components for when the program is running, 
	 * but no portfolio is currently open.
	 */
	//public static final Integer ENABLE_NO_PORTFOLIO_OPEN = 1; //TODO remove
	
	/**
	 * Components Enabled value. Enable components for when portfolio is open, but 
	 * no stock has been loaded into the main display.
	 */
	//public static final Integer ENABLE_NO_STOCK_LOADED = 2; //TODO remove
	
	/**
	 * Components Enabled value. Enable components for when portfolio is open and a 
	 * "lookup" stock is loaded into the main display.
	 * A "lookup" stock is one whose values are being viewed, but no shares of
	 * it are currently owned in the portfolio.
	 */
	//public static final Integer ENABLE_LOOKUP_STOCK_LOADED = 3; //TODO remove
	
	/**
	 * Components Enabled value. Enable components for when portfolio is open and an 
	 * "owned" stock is loaded into the main display.
	 * An "owned" stock has at least one share owned in the portfolio.
	 */
	//public static final Integer ENABLE_OWNED_STOCK_LOADED = 4; //TODO remove
	
	/**
	 * Components Enabled value. Enable components for when portfolio is open and 
	 * currently being refreshed.
	 */
	//public static final int ENABLE_REFRESH_PORTFOLIO = 5; //TODO remove
	
	//public static final Integer PORTFOLIO_NAME_DIALOG = 6; //TODO remove
	
	//public static final Integer OPEN_PORTFOLIO_DIALOG = 7; //TODO remove
	
	private static final Logger LOGGER = Logger.getLogger(
			"stockmarket.controller.StockMarketController");
	
	private final PortfolioDAO portfolioDAO;
	
	private static final Language LANGUAGE = Language.getInstance();
	
	public StockMarketController() {
		super();
		this.portfolioDAO = new SQLPortfolioDAO();
		((SQLPortfolioDAO) portfolioDAO).addPropertyChangeListener(this);
		setThreadFactory(new EventThreadFactory());
		
	}

	public StockMarketController(int threadPoolSize){
		super(threadPoolSize);
		this.portfolioDAO = new SQLPortfolioDAO();
		((SQLPortfolioDAO) portfolioDAO).addPropertyChangeListener(this);
		setThreadFactory(new EventThreadFactory());
	}

	public StockMarketController(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit){
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit);
		this.portfolioDAO = new SQLPortfolioDAO();
		((SQLPortfolioDAO) portfolioDAO).addPropertyChangeListener(this);
		setThreadFactory(new EventThreadFactory());
	}

	//TODO check methods, if theyre invoked by multiple threads will there be issues???
	
	@Override
	protected void processEvent(final String actionCommand, final Object valueFromView) {
		if(actionCommand == EDIT_PORTFOLIO_NAME_ACTION){
			Thread.currentThread().setName("EditPortfolioName");
			showPortfolioNameDialog();
		}
		else if(actionCommand == MARKET_DATA_ACTION){
			Thread.currentThread().setName("MarketWebData");
			marketDataWebpage();
		}
		else if(actionCommand == NEW_PORTFOLIO_ACTION){
			//TODO clear any old data. For this and for the open action. and the close action
			//That SHOULD already happen, the new model should overwrite the
			//existing values displayed in the GUI. Test this when the program is more assembled.
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
		else if(actionCommand == SAVE_PORTFOLIO_ACTION){
			Thread.currentThread().setName("SavePortfolio");
			savePortfolio();
		}
		else if(actionCommand == SAVE_PORTFOLIO_NAME_ACTION){
			Thread.currentThread().setName("SavePortfolioName");
			savePortfolioName(valueFromView);
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
	
	public void changeStockHistoryInterval(Object valueFromView){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"changeStockHistoryInterval", "Entering method", 
				new Object[] {"Months: " + valueFromView});
		
		YahooStockDownloader downloader = new YahooStockDownloader();
		
		try {
			Integer months = (Integer) valueFromView;
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
			String[] messageArr = ex.getMessage().split(" ");
			String symbol = messageArr[messageArr.length - 1];
			
			displayExceptionDialog(
					LANGUAGE.getString("invalid_stock_title"), 
					"\"" + symbol + "\" " 
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
		catch(Exception ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"changeStockHistoryInterval", 
					"Exception", ex);
		}
		
	}
	
	public void searchForStock(Object valueFromView){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"searchForStock", "Entering method", 
				new Object[] {"Symbol: " + valueFromView});
		
		String symbol = ((String) valueFromView).toUpperCase();
		
		try {
			
			final StockDownloader downloader = new YahooStockDownloader();
			final Stock stock = new DefaultStock(symbol);
			
			
			Future<Stock> downloadStock = 
					eventExecutor.submit(new Callable<Stock>(){
						@Override
						public Stock call() throws Exception {
							Thread.currentThread().setName("StockSearch");
							
							stock.setStockDetails(downloader, true);
							
							Thread.currentThread().setName("EventActionThread");
							
							return stock;
						}
					});
			
			Future<List<HistoricalQuote>> downloadHistory = 
					eventExecutor.submit(new Callable<List<HistoricalQuote>>(){
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
						
					});
			
			Stock downloadedStock = downloadStock.get();
			List<HistoricalQuote> historyList = downloadHistory.get();
			
			setModelProperty(PORTFOLIO_STATE_PROPERTY, PortfolioState.OPEN_STOCK);
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
		catch(NoSuchMethodException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"searchForStock", 
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
	 * Launder the <tt>ExecutionException</tt> from the stock search 
	 * and respond to it appropriately.
	 * 
	 * @param ex the exception to be laundered.
	 * @param symbol the symbol of the stock.
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
			URI uri = new URI("http://finance.yahoo.com/stock-center/");
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"marketDataWebpage", "URI: " + uri);
			if(Desktop.isDesktopSupported()){
				Desktop.getDesktop().browse(uri);
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
	
	public void openPortfolio(Object valueFromView){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"openPortfolio", "Entering method", 
				new Object[]{"|" + valueFromView + "|"});
		
		try {
			//Enable GUI components
			setModelProperty(PORTFOLIO_STATE_PROPERTY, PortfolioState.OPEN_NO_STOCK);
			
			//Load portfolio model from the DAO
			PortfolioModel portfolioModel = portfolioDAO.getPortfolio(
					(String) valueFromView);
			
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
		catch(NoSuchMethodException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"openPortfolio", 
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
	
	/**
	 * Show the open portfolio dialog so the user can select the saved 
	 * portfolio they want to open.
	 */
	public void showOpenPortfolioDialog(){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"showOpenPortfolioDialog", 
				"Entering method");
		
		try {
			List<String> portfolioNameList = portfolioDAO.getSavedPortfolios();
			
			//TODO remove these
			//Object[] dialogConfig = new Object[2];
			//dialogConfig[0] = OPEN_PORTFOLIO_DIALOG;
			//dialogConfig[1] = portfolioNameList;
			
			//TODO redo this log entry
			//LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					//"showOpenPortfolioDialog()", "Dialog Config Values: 0: " 
							//+ dialogConfig[0] + " 1: " 
							//+ dialogConfig[1]);
			
			setModelProperty(DIALOG_DISPLAYED_PROPERTY, 
					Dialog.OPEN_PORTFOLIO_DIALOG, portfolioNameList);
			
			LOGGER.logp(Level.INFO, this.getClass().getName(), 
					"showOpenPortfolioDialog()", 
					"Open portfolio dialog displayed");
		}
		catch(NoSuchMethodException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"showPortfolioNameDialog()", 
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
			
			//TODO remove this code and redo the log entry
			//Object[] dialogConfig = new Object[2];
			//dialogConfig[0] = PORTFOLIO_NAME_DIALOG;
			//dialogConfig[1] = name != null ? name : "";
			
			//LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					//"showPortfolioNameDialog", "Dialog Config Values",
					//new Object[] {"Dialog Code: " + dialogConfig[0], 
								 // "Name: " + dialogConfig[1]});
			
			setModelProperty(DIALOG_DISPLAYED_PROPERTY, 
					Dialog.PORTFOLIO_NAME_DIALOG, name);
			
			LOGGER.logp(Level.INFO, this.getClass().getName(), 
					"showPortfolioNameDialog", 
					"Portfolio name dialog displayed");
		}
		catch(NoSuchMethodException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"showPortfolioNameDialog", 
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
			
			PortfolioModel portfolioModel = portfolioDAO.createNewPortfolio(
					LANGUAGE.getString("new_portfolio_name"), 
					new BigDecimal(INITIAL_CASH_BALANCE_VALUE));
			
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
		catch(NoSuchMethodException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"newPortfolio", "Exception", ex);
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
		catch(NoSuchMethodException ex){
			displayExceptionDialog(ex);
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"savePortfolioName", "Exception", ex);
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
		
		PortfolioModel portfolioModel = null;
		synchronized(modelList){
			for(AbstractPropertyModel model : modelList){
				if(model instanceof PortfolioModel){
					portfolioModel = (PortfolioModel) model;
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
	
	/**
	 * Display a dialog to show the details of an exception.
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
	
	private void displayExceptionDialog(final String title, final String message){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				ListenerDialog exceptionDialog = DialogFactory.createExceptionDialog(null, title, message);
				exceptionDialog.showDialog();
			}
		});
	}
	
	private class EventThreadFactory implements ThreadFactory{

		@Override
		public Thread newThread(Runnable r) {
			return new EventActionThread(r);
		}
		
	}
	
	private class EventActionThread extends Thread{
		
		public EventActionThread(Runnable r){
			super(r);
			setName("EventActionThread");
			setUncaughtExceptionHandler(new EventUncaughtExceptionHandler());
		}
		
	}
	
	private class EventUncaughtExceptionHandler implements UncaughtExceptionHandler{

		@Override
		public void uncaughtException(Thread thread, Throwable throwable) {
			//TODO figure out a unified gui response for exceptions
			displayExceptionDialog(throwable);
			
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"uncaughtException()", "Uncaught Exception: " + thread.getName() 
					+ "." + thread.getId(), throwable);
		}
		
	}

}
