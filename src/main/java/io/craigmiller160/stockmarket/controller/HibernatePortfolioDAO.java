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
import java.util.regex.Pattern;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

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
 * @version 2.3
 */
public class HibernatePortfolioDAO implements PortfolioDAO {

	//TODO split this into a dao and a service layer
	//Additional create portfolio info goes there
	//getPortfolio by name goes there
	//Converting portfolio list to strings goes there
	//Remove property change listener stuff
	
	/**
	 * The <tt>SessionFactory</tt> for this DAO.
	 */
	private SessionFactory sessionFactory;
	
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
	 * Create the DAO and inject the <tt>SessionFactory</tt> parameter
	 * into it.
	 * 
	 * @param factory the <tt>SessionFactory</tt> for this DAO.
	 */
	public HibernatePortfolioDAO(SessionFactory factory) {
		this.sessionFactory = factory;
	}
	
	/**
	 * Closes the <tt>SessionFactory</tt>. This method should be
	 * invoked at the end of the program to ensure an orderly VM shutdown
	 * and avoid resource leaks.
	 */
	public void closeFactory(){
		sessionFactory.close();
	}

	/**
	 * {@inheritDoc}
	 * @throws HibernateException if Hibernate is unable to perform
	 * the required operation.
	 */
	@Transactional
	@Override
	public PortfolioModel createNewPortfolio(String portfolioName, 
			BigDecimal startingCashBalance) throws HibernateException {
		SQLPortfolioModel portfolio = new SQLPortfolioModel();
		portfolio.setPortfolioName(portfolioName);
		portfolio.setInitialValue(startingCashBalance);
		portfolio.setCashBalance(startingCashBalance);
		portfolio.setNetWorth(startingCashBalance);
		
		sessionFactory.getCurrentSession().save(portfolio);
		
		return portfolio;
	}

	/**
	 * {@inheritDoc}
	 * @throws HibernateException if Hibernate is unable to perform
	 * the required operation.
	 */
	@Transactional
	@Override
	@SuppressWarnings("unchecked") //hibernate list() method doesn't support generics
	public List<String> getSavedPortfolios() throws HibernateException {
		List<String> portfolioNames = new ArrayList<>();
		
		List<SQLPortfolioModel> portfolioList = sessionFactory.getCurrentSession()
									.createCriteria(PortfolioModel.class)
									.list();
		
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
		
		return portfolioNames;
	}

	/**
	 * {@inheritDoc}
	 * @throws HibernateException if Hibernate is unable to perform
	 * the required operation.
	 * @throws IllegalArgumentException if the fileName parameter is
	 * not a valid fileName for a portfolio.
	 */
	@Transactional
	@Override
	public PortfolioModel getPortfolio(String fileName) throws HibernateException {
		String regex = "\\d+-.+-.+-\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
		boolean matches = Pattern.matches(regex, fileName);
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
	 * @throws HibernateException if Hibernate is unable to perform
	 * the required operation.
	 */
	@Transactional
	public PortfolioModel getPortfolio(int userid) throws HibernateException{
		SQLPortfolioModel portfolio = (SQLPortfolioModel) sessionFactory.getCurrentSession()
											.createCriteria(SQLPortfolioModel.class)
											.add(Restrictions.naturalId().set("userID", userid))
											.setFetchMode("stockList", FetchMode.JOIN)
											.uniqueResult();
		
		return portfolio;
	}

	/**
	 * {@inheritDoc}
	 * @throws HibernateException if Hibernate is unable to perform
	 * the required operation.
	 */
	@Transactional
	@Override
	public void savePortfolio(PortfolioModel portfolio) throws HibernateException {
		sessionFactory.getCurrentSession().saveOrUpdate(portfolio);
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
