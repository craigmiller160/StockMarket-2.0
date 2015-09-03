package io.craigmiller160.stockmarket.controller;

import io.craigmiller160.stockmarket.model.PortfolioModel;
import io.craigmiller160.stockmarket.model.SQLPortfolioModel;
import io.craigmiller160.stockmarket.stock.DefaultOwnedStock;
import io.craigmiller160.stockmarket.stock.DefaultStock;
import io.craigmiller160.stockmarket.stock.OwnedStock;
import io.craigmiller160.stockmarket.stock.Stock;
import io.craigmiller160.stockmarket.stock.StockFileDownloader;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import net.jcip.annotations.NotThreadSafe;

/**
 * Implementation of a data access object for accessing a SQL database
 * where stock portfolios are saved. Because it saves and 
 * loads the state of the implementation of this program, it is
 * dependent on the implementations of the <tt>AbstractStock</tt> 
 * class, and cannot use <tt>AbstractStock</tt> as an abstraction
 * layer. Specifically, it depends on the <tt>Stock</tt> and <tt>OwnedStock</tt>
 * classes. Any major changes made to these classes will need to be
 * reflected in this class as well.
 * <p>
 * This implementation works with a MySQL database, and currently only 
 * allows a single thread to access the database at a time. This poses
 * virtually no performance issues, as the database is only used infrequently
 * to save and load the program's total state. This can be changed in
 * future versions if database use needs to be expanded.
 * <p>
 * Lastly, this class can have <tt>PropertyChangeListener</tt>s added to it.
 * Any listeners that are added will be added to the <tt>PortfolioModel</tt>
 * instances that are created by the methods herein. This allows the models
 * to update the GUI as their properties are being set by this class. In order
 * to simplify the process of adding the listeners, this class doubles as
 * a <tt>PropertyChangeListener</tt> and is added to the models as a listener
 * when they are created. Any events received by this class are merely passed 
 * along to external listeners.
 * <p>
 * Listeners added to a model while inside of this class are removed from that
 * model before it is released from this class. 
 * <p>
 * <b>THREAD SAFETY:</b> This class has no mutable state, with the only 
 * changing values being thread local values passed to this class's methods.
 * However, due to the current restriction on only one thread accessing
 * the database at a time, this class should generally only be used by
 * one thread at a time. If this restriction is lifted in future versions,
 * this class is already properly constructed for concurrent access.
 * 
 * @author craig
 * @version 2.0
 * @see io.craigmiller160.stockmarket.stock.OwnedStock OwnedStock
 * @see io.craigmiller160.stockmarket.stock.DefaultStock Stock
 */
@NotThreadSafe
public class SQLPortfolioDAO implements
		PortfolioDAO, PropertyChangeListener {

	/**
	 * The database URL.
	 */
	private final String dburl;
	
	/**
	 * The database username.
	 */
	private final String dbusername;
	
	/**
	 * The database password;
	 */
	private final String dbpassword;
	
	/**
	 * The shared logger for the program.
	 */
	private static final Logger LOGGER = 
			Logger.getLogger("stockmarket.controller.SQLController");
	
	/**
	 * A semaphore to restrict access to the database connection.
	 */
	private final Semaphore semaphore;
	
	/**
	 * A list of property change listeners added to this class.
	 */
	private final List<PropertyChangeListener> listeners;
	
	/**
	 * Constructs this controller by loading the database properties from
	 * file and configuring access permits through a <tt>Semaphore</tt> 
	 * object. If it is unable to load the database properties, an exception
	 * is thrown and this object fails to be completely constructed.
	 * 
	 * @throws IOException if the database properties cannot be loaded.
	 */
	public SQLPortfolioDAO() throws IOException{
		listeners = Collections.synchronizedList(new ArrayList<>());
		semaphore = new Semaphore(1);
		Properties defaultProps = new Properties();
		
		defaultProps.load(
				this.getClass().getClassLoader().getResourceAsStream(
						"default.properties"));
		
		dburl = defaultProps.getProperty("dburl");
		dbusername = defaultProps.getProperty("dbusername");
		dbpassword = defaultProps.getProperty("dbpassword");
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"Constructor", "Successfully loaded database properties");
	}
	
	/**
	 * Add a property change listener to this class.
	 * 
	 * @param listener the property change listener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener){
		listeners.add(listener);
	}
	
	/**
	 * Remove a property change listener from this class.
	 * 
	 * @param listener the property change listener to remove.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener){
		listeners.remove(listener);
	}

	/**
	 * {@inheritDoc}
	 * @throws SQLException if there is an error attempting to access the database.
	 * @throws InterruptedException if a thread waiting on database access is interrupted.
	 */
	@Override
	public PortfolioModel createNewPortfolio(String portfolioName, 
			BigDecimal startingCashBalance) throws InterruptedException, SQLException{
		String query = "select * from portfolio;";
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"createNewPortfolio()", "SQL: Portfolio List Query: " + query);
		
		SQLPortfolioModel portfolio = new SQLPortfolioModel();
		portfolio.addPropertyChangeListener(this);
		
		portfolio.setPortfolioName(portfolioName);
		portfolio.setInitialValue(startingCashBalance);
		portfolio.setStockList(null);
		
		int userid = 0;
		semaphore.acquire();
		try(Statement statement = getConnection().createStatement(
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)){
			ResultSet resultSet = statement.executeQuery(query);
			
			resultSet.moveToInsertRow();
			resultSet.updateString(2, portfolioName); //portfolio_name
			resultSet.updateBigDecimal(3, portfolio.getCashBalance()); //cash_balance
			resultSet.updateBigDecimal(4, portfolio.getNetWorth()); //net_worth
			resultSet.updateBigDecimal(5, portfolio.getInitialValue());
			
			Timestamp now = new Timestamp(
					GregorianCalendar.getInstance().getTimeInMillis());
			resultSet.updateTimestamp(7, now); //timestamp
			
			resultSet.insertRow();
			
			resultSet.last();
			portfolio.setUserID(resultSet.getInt(1));
			
			userid = resultSet.getInt(1);
		}
		semaphore.release();
		
		LOGGER.logp(Level.INFO, this.getClass().getName(), 
				"createNewPortfolio()", "New Portfolio userid: " + userid);
		
		portfolio.removePropertyChangeListener(this);
		
		return portfolio;
	}

	/**
	 * {@inheritDoc}
	 * @throws SQLException if there is an error while trying to access the database. 
	 * @throws InterruptedException if a thread waiting on a database connection is interrupted.
	 */
	@Override
	public List<String> getSavedPortfolios() throws InterruptedException, SQLException{
		String query = "select * from portfolio;";
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"loadPortfolioList()", "SQL: Portfolio List Query: " + query);
		
		List<String> portfolioNames = new ArrayList<>();
		semaphore.acquire();
		try(Statement statement = getConnection().createStatement()){
			ResultSet resultSet = statement.executeQuery(query);
			
			NumberFormat moneyFormat = new DecimalFormat("$###,###,###,##0.00");
			while(resultSet.next()){
				int id = resultSet.getInt(1); //userid
				String name = resultSet.getString(2); //portfolio_name
				BigDecimal netWorth = resultSet.getBigDecimal(4); //net_worth
				Timestamp timestamp = resultSet.getTimestamp(7); //timestamp
				
				
				String fileName = String.format("%1$d-%2$s-%3$s-"
						+"%4$s", id, name, moneyFormat.format(netWorth), 
						timestamp.toString());
				portfolioNames.add(fileName);
			}
		}
		semaphore.release();
		
		LOGGER.logp(Level.INFO, this.getClass().getName(), 
				"loadPortfolioList()", "Successfully loaded list of saved portfolios");
		
		return portfolioNames;
	}

	
	
	/**
	 * {@inheritDoc}
	 * @throws SQLException if an error occurs while trying to access the database.
	 * @throws InterruptedException if a thread is interrupted while waiting on database access.
	 * @throws IllegalArgumentException if the parameter is not a valid value.
	 * @see io.craigmiller160.stockmarket.controller.SQLPortfolioDAO#getSavedPortfolios() 
	 * SQLPortfolioDAO.getSavedPortfolios()
	 */
	@Override
	public PortfolioModel getPortfolio(String fileName) throws InterruptedException, SQLException {
		String regex = "\\d+-.+-.+-\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d";
		boolean matches = Pattern.matches(regex, fileName);
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"loadPortfolio()", "Portfolio File Name: " + fileName);
		
		if(!matches){
			throw new IllegalArgumentException(fileName 
					+ " is not a value returned in the list by SQLController.loadPortfolioList()");
		}
		else{
			String[] split = fileName.split("-");
			return getPortfolio(Integer.parseInt(split[0]));
		}
	}
	
	/**
	 * Returns a portfolio loaded from the data source that matches the
	 * specified userid.
	 * 
	 * @param userid the userid of the portfolio in the database.
	 * @return the portfolio loaded from the data source.
	 * @throws SQLException if an error occurs while trying to access the database.
	 * @throws InterruptedException if a thread is interrupted while waiting for database
	 * access.
	 */
	public PortfolioModel getPortfolio(int userid) throws InterruptedException, SQLException{
		String portfolioQuery = "select * from portfolio where userid=" + userid + ";";
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"loadPortfolio()", "SQL: Load Portfolio Query: " + portfolioQuery);
		String stocksQuery = "select * from stocks where userid=" + userid + ";";
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"loadStocks()", "SQL: Load Stocks Query: " + stocksQuery);
		
		SQLPortfolioModel portfolio = new SQLPortfolioModel();
		portfolio.addPropertyChangeListener(this);
		portfolio.setUserID(userid);
		
		semaphore.acquire();
		try(Statement portfolioStatement = getConnection().createStatement(); 
				PreparedStatement stockStatement = getConnection().prepareStatement(stocksQuery)){
			ResultSet resultSet = portfolioStatement.executeQuery(portfolioQuery);
			
			while(resultSet.next()){
				portfolio.setPortfolioName(resultSet.getString(2)); //portfolio_name
				portfolio.setInitialValue(resultSet.getBigDecimal(5)); //initial_value
				portfolio.setCashBalance(resultSet.getBigDecimal(3)); //cash_balance
				portfolio.setNetWorth(resultSet.getBigDecimal(4)); //net_worth
				portfolio.setTotalStockValue(resultSet.getBigDecimal(6)); //total_stock_value
			}
			
			portfolio.setStockList(getStocks(stockStatement, userid));
		}
		semaphore.release();
		portfolio.removePropertyChangeListener(this);
		
		LOGGER.logp(Level.INFO, this.getClass().getName(), 
				"getPortfolio()", "Portfolio loaded successfully");
		
		return portfolio;
	}
	
	/**
	 * Loads the saved stocks from the database for the portfolio.
	 * 
	 * @param statement the <tt>PreparedStatement</tt> object to execute the query for the stock list.
	 * @param userid the userid to specify which stocks to retrieve.
	 * @return a list of saved stocks owned by the specified userid.
	 * @throws SQLException if an error occurs while trying to access the database.
	 */
	private List<OwnedStock> getStocks(PreparedStatement statement, int userid) throws SQLException{
		ResultSet resultSet = statement.executeQuery();
		
		List<OwnedStock> stockList = new ArrayList<>(10);
		StockFileDownloader downloader = new StockFileDownloader();
		while(resultSet.next()){
			String symbol = resultSet.getString(2).toUpperCase(); //symbol
			downloader.setName(resultSet.getString(3)); //name
			downloader.setCurrentPrice(resultSet.getString(4)); //current_price
			downloader.setQuantityOfShares(resultSet.getString(5)); //share_quantity
			downloader.setPrinciple(resultSet.getString(6)); //principle
			downloader.setTotalValue(resultSet.getString(7)); //total_value
			downloader.setNet(resultSet.getString(8)); //net
			
			//This dependency can't be helped... only so much I can do for abstraction
			OwnedStock oStock = new DefaultOwnedStock(symbol);
			try {
				oStock.setStockDetails(downloader, false);
			} catch (Exception ex) {
				//There should never ben an exception here, as StockFileDownloader's
				//dummy downloading logic doesn't throw its declared exceptions.
				LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
						"loadStocks()", "StockFileDownloader threw an exception", ex);
			}
			downloader.clearMap();
			stockList.add(oStock);
			
		}
		
		return stockList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>THREAD SAFETY:</b> A copy of the <tt>syncStockList</tt> parameter is
	 * made before working on it. However, the <tt>OwnedStock</tt> objects the copy
	 * contains are the same references in the original list. This method makes no
	 * changes to the state of any of the <tt>OwnedStock</tt> objects, and any changes 
	 * to this method should maintain this.
	 * 
	 * @throws SQLException if an error occurs while trying to access the database.
	 * @throws InterruptedException if a thread is interrupted while waiting for database access.
	 */
	@Override
	public void savePortfolio(PortfolioModel portfolio) throws InterruptedException, SQLException {
		if(! (portfolio instanceof SQLPortfolioModel)){
			throw new IllegalArgumentException(
					"SQL Database only accepts SQLPortfolioModels: " 
					+ portfolio.getClass().getName());
		}
		
		SQLPortfolioModel portfolioModel = (SQLPortfolioModel) portfolio;
		int userid = portfolioModel.getUserID();
		String portfolioName = portfolioModel.getPortfolioName() != null ? portfolioModel.getPortfolioName() : "";
		BigDecimal cashBalance = portfolioModel.getCashBalance() != null ? portfolioModel.getCashBalance() : new BigDecimal(0);
		BigDecimal netWorth = portfolioModel.getNetWorth() != null ? portfolioModel.getNetWorth() : new BigDecimal(0);
		BigDecimal initialValue = portfolioModel.getInitialValue() != null ? portfolioModel.getInitialValue() : new BigDecimal(0);
		BigDecimal totalStockValue = portfolioModel.getTotalStockValue() != null ? portfolioModel.getTotalStockValue() : new BigDecimal(0);
		
		//Shallow copy of list, avoids interfering with model list consistency
		//but still has shared references to contents.
		List<OwnedStock> stockList = new ArrayList<>(portfolioModel.getStockList());
		
		String portfolioQuery = "select * from portfolio where userid=" + userid + ";";
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"savePortfolio()", "SQL: Save Portfolio Query: " + portfolioQuery);
		String stocksQuery = "select * from stocks where userid=" + userid + ";";
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"saveStocks()", "SQL: Save Stocks Query: " + stocksQuery);
		
		semaphore.acquire();
		try(Statement portfolioStatement = getConnection().createStatement(
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); 
			PreparedStatement stockStatement = getConnection().prepareStatement(
				stocksQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, 
				ResultSet.CONCUR_UPDATABLE);){
			
			ResultSet resultSet = portfolioStatement.executeQuery(portfolioQuery);
			
			while(resultSet.next()){
				resultSet.updateString(2, portfolioName); //portfolio_name
				resultSet.updateBigDecimal(3, cashBalance); //cash_balance
				resultSet.updateBigDecimal(4, netWorth); //net_worth
				resultSet.updateBigDecimal(5, initialValue); //initial_value
				resultSet.updateBigDecimal(6, totalStockValue); //total_stock_value
				
				Timestamp now = new Timestamp(
						GregorianCalendar.getInstance().getTimeInMillis());
				resultSet.updateTimestamp(7,  now); //timestamp
				
				resultSet.updateRow();
			}
			
			//Currently re-using portfolioStatement here for the sake of simplicity
			saveStocks(stockStatement, portfolioStatement, stockList, userid);
		}
		semaphore.release();
		LOGGER.logp(Level.FINEST, this.getClass().getName(), "savePortfolio()",
				"Successfully saved portfolio");

	}
	
	/**
	 * Saves the list of stocks to the database.
	 * 
	 * @param stockStatement the statement to execute the query to save the stocks
	 * to the database.
	 * @param deleteStatement the statement used to execute the query to delete stocks
	 * from the database.
	 * @param stockList the list of stocks to save.
	 * @param userid the userid key for the table.
	 * @throws SQLException if the data is unable to be saved.
	 */
	private void saveStocks(PreparedStatement stockStatement, Statement deleteStatement, 
			List<OwnedStock> stockList, int userid) throws SQLException{
		
		ResultSet stocksResultSet = stockStatement.executeQuery();
		
		//Iterate through ResultSet. If a stock in the table doesn't exist
		//in the stockList, it is added to the stocksToRemove list.
		//If a stock is in the stockList, it's contents are updated and
		//it is removed from the list.
		List<String> stocksToRemove = new ArrayList<>();
		while(stocksResultSet.next()){
			String symbol = stocksResultSet.getString(2).toUpperCase(); //symbol
			int index = stockList.indexOf(new DefaultStock(symbol));
			if(index == -1){
				stocksToRemove.add(symbol);
			}
			else{
				OwnedStock oStock = stockList.get(index);
				Map<String,Object> valueMap = oStock.getValueMap(false);
				stocksResultSet.updateString(
						3, (String) valueMap.get(Stock.NAME)); //name
				stocksResultSet.updateBigDecimal(
						4, (BigDecimal) valueMap.get(Stock.CURRENT_PRICE)); //current_price
				stocksResultSet.updateInt(
						5, (Integer) valueMap.get(OwnedStock.QUANTITY_OF_SHARES)); //share_quantity
				stocksResultSet.updateBigDecimal(
						6, (BigDecimal) valueMap.get(OwnedStock.PRINCIPLE)); //principle
				stocksResultSet.updateBigDecimal(
						7, (BigDecimal) valueMap.get(OwnedStock.TOTAL_VALUE)); //total_value
				stocksResultSet.updateBigDecimal(
						8, (BigDecimal) valueMap.get(OwnedStock.NET)); //net
				stocksResultSet.updateRow();
				
				stockList.remove(index);
			}
		}
		
		//Any remaining stocks in the stockList are added to the table.
		for(OwnedStock oStock : stockList){
			stocksResultSet.moveToInsertRow();
			Map<String,Object> valueMap = oStock.getValueMap(false);
			
			stocksResultSet.updateInt(1, userid);
			stocksResultSet.updateString(
					2, (String) valueMap.get(Stock.SYMBOL));
			stocksResultSet.updateString(
					3, (String) valueMap.get(Stock.NAME));
			stocksResultSet.updateBigDecimal(
					4, (BigDecimal) valueMap.get(Stock.CURRENT_PRICE));
			stocksResultSet.updateInt(
					5, (Integer) valueMap.get(OwnedStock.QUANTITY_OF_SHARES));
			stocksResultSet.updateBigDecimal(
					6, (BigDecimal) valueMap.get(OwnedStock.PRINCIPLE));
			stocksResultSet.updateBigDecimal(
					7, (BigDecimal) valueMap.get(OwnedStock.TOTAL_VALUE));
			stocksResultSet.updateBigDecimal(
					8, (BigDecimal) valueMap.get(OwnedStock.NET));
			stocksResultSet.insertRow();
		}
		
		//Any stocks in the stocksToRemove list are removed from the table.
		if(stocksToRemove.size() > 0){
			StringBuilder remove = new StringBuilder(
					"delete from stocks where userid=" 
					+ userid + " and symbol in(");
			for(String s : stocksToRemove){
				remove.append("'" + s + "',");
			}
			remove.deleteCharAt(remove.length() - 1);
			remove.append(");");
			
			deleteStatement.executeUpdate(remove.toString());
		}
	}
	
	/**
	 * Retrieves a connection to the database.
	 * 
	 * @return a connection to the database.
	 * @throws SQLException if the connection to the database is unable
	 * to be established.
	 */
	private Connection getConnection() throws SQLException{
		return DriverManager.getConnection(dburl, dbusername, dbpassword);
	}

	/**
	 * This method is merely used to pass <tt>PropertyChangeEvent</tt>s to
	 * outside listeners. This mechanic is used to allow <tt>PortfolioModel</tt>s
	 * that are being constructed by this class to have their attributes passed
	 * to the view as they are set.
	 * 
	 * @param event the <tt>PropertyChangeEvent</tt> to pass to outside
	 * listeners.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		synchronized(listeners){
			for(PropertyChangeListener listener : listeners){
				listener.propertyChange(event);
			}
		}
	}

}
