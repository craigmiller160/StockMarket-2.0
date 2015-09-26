package io.craigmiller160.stockmarket.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import io.craigmiller160.stockmarket.model.PortfolioModel;
import io.craigmiller160.stockmarket.model.SQLPortfolioModel;

/**
 * Data Access Object that utilizes the Hibernate framework to access
 * the database. All database access occurs within Hibernate-managed 
 * transactions. Because no actual SQL is utilized, this DAO is immune
 * to SQL Injection.
 * <p>
 * This class can have <tt>PropertyChangeListener</tt>s added to it.
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
 * @version 2.2
 */
public class HibernatePortfolioDAO implements PortfolioDAO {

	/**
	 * The <tt>SessionFactory</tt> for this DAO.
	 */
	private SessionFactory factory;
	
	/**
	 * The format for amounts of money to display.
	 */
	private NumberFormat moneyFormat = new DecimalFormat("$###,###,###,##0.00");
	
	/**
	 * The format for the timestamp to display.
	 */
	private DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	/**
	 * List of stored external <tt>PropertyChangeListener</tt>s.
	 */
	private List<PropertyChangeListener> listenerList = Collections.synchronizedList(new ArrayList<>());
	
	/**
	 * The shared logger for the program.
	 */
	private static final Logger LOGGER = 
			Logger.getLogger("stockmarket.controller.HibernatePortfolioDAO");
	
	/**
	 * Create the DAO and inject the <tt>SessionFactory</tt> parameter
	 * into it.
	 * 
	 * @param factory the <tt>SessionFactory</tt> for this DAO.
	 */
	public HibernatePortfolioDAO(SessionFactory factory) {
		this.factory = factory;
	}
	
	/**
	 * Closes the <tt>SessionFactory</tt>. This method should be
	 * invoked at the end of the program to ensure an orderly VM shutdown
	 * and avoid resource leaks.
	 */
	public void closeFactory(){
		factory.close();
	}

	@Override
	public PortfolioModel createNewPortfolio(String portfolioName, BigDecimal startingCashBalance) throws Exception {
		SQLPortfolioModel portfolio = new SQLPortfolioModel();
		
		Session session = null;
		try{
			session = factory.openSession();
			session.beginTransaction();
			session.save(portfolio);
			session.getTransaction().commit();
		}
		finally{
			if(session != null){
				session.close();
			}
		}
		
		LOGGER.logp(Level.INFO, this.getClass().getName(), 
				"createNewPortfolio", "New Portfolio Created");
		
		return portfolio;
	}

	@Override
	@SuppressWarnings("unchecked") //hibernate list() method doesn't support generics
	public List<String> getSavedPortfolios() throws Exception {
		List<String> portfolioNames = new ArrayList<>();
		
		List<SQLPortfolioModel> portfolioList = null;
		Session session = null;
		try{
			session = factory.openSession();
			Criteria portfolioListCriteria = session.createCriteria(PortfolioModel.class);
			portfolioList = (List<SQLPortfolioModel>) portfolioListCriteria.list();
		}
		finally{
			if(session != null){
				session.close();
			}
		}
		
		for(SQLPortfolioModel portfolio : portfolioList){
			int id = portfolio.getUserID();
			String name = portfolio.getPortfolioName();
			BigDecimal netWorth = portfolio.getNetWorth();
			Calendar timestamp = portfolio.getTimestamp();
			
			String fileName = String.format("%1$d-%2$s-%3$s-"
					+"%4$s", id, name, moneyFormat.format(netWorth), 
					timestampFormat.format(timestamp.getTime()));
			portfolioNames.add(fileName);
		}
		
		LOGGER.logp(Level.INFO, this.getClass().getName(), 
				"getSavedPortfolios", "Saved Portfolios Retrieved");
		
		return portfolioNames;
	}

	@Override
	public PortfolioModel getPortfolio(String fileName) throws Exception {
		String regex = "\\d+-.+-.+-\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
		boolean matches = Pattern.matches(regex, fileName);
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"loadPortfolio()", "Portfolio File Name: " + fileName);
		
		if(!matches){
			throw new IllegalArgumentException(fileName 
					+ " is not a value returned in the list by HibernatePortfolioDAO.loadPortfolioList()");
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
	 * @throws Exception if an error occurs while trying to access the database.
	 */
	public PortfolioModel getPortfolio(int userid) throws Exception{
		SQLPortfolioModel portfolio = null;
		
		Session session = null;
		try{
			session = factory.openSession();
			session.beginTransaction();
			portfolio = session.get(SQLPortfolioModel.class, userid);
			
			//This section is here to conform to the established design of the program
			//Because a listener could not be added prior to session.get(), the model
			//is setting its fields to their existing values to fire PropertyChangeEvents
			portfolio.addPropertyChangeListener(this);
			portfolio.setCashBalance(portfolio.getCashBalance());
			portfolio.setPortfolioName(portfolio.getPortfolioName());
			portfolio.setNetWorth(portfolio.getNetWorth());
			portfolio.setStockList(portfolio.getStockList());
			portfolio.removePropertyChangeListener(this);
			
			session.getTransaction().commit();
		}
		finally{
			if(session != null){
				session.close();
			}
		}
		
		return portfolio;
	}

	@Override
	public void savePortfolio(PortfolioModel portfolio) throws Exception {
		Session session = null;
		try{
			session = factory.openSession();
			session.beginTransaction();
			session.saveOrUpdate(portfolio);
			session.getTransaction().commit();
		}
		finally{
			if(session != null){
				session.close();
			}
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listenerList.add(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listenerList.remove(listener);		
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		synchronized(listenerList){
			for(PropertyChangeListener listener : listenerList){
				listener.propertyChange(event);
			}
		}
	}

}
