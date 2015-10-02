package io.craigmiller160.stockmarket.controller;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import io.craigmiller160.stockmarket.model.PortfolioModel;
import io.craigmiller160.stockmarket.model.SQLPortfolioModel;
import net.jcip.annotations.ThreadSafe;

/**
 * Implemented <tt>PortfolioDao</tt> using the Hibernate
 * framework. This class simply runs CRUD operations to
 * persist stock portfolios to the database. It does not
 * perform any additional operations, and does NOT execute
 * its operations in the context of a transaction. All 
 * transaction and session control operations must be 
 * handled by a service layer class for this class to
 * be able to perform atomic operations on the database.
 * <p>
 * <b>THREAD SAFETY:</b> This DAO is thread-safe. The
 * <tt>SessionFactory</tt> field is final and cannot be
 * changed, and it has no other state besides that.
 * 
 * @author craig
 * @version 2.4
 */
@ThreadSafe
public class HibernatePortfolioDao implements PortfolioDao {

	/**
	 * The <tt>SessionFactory</tt> used by this DAO.
	 */
	private final SessionFactory sessionFactory;
	
	/**
	 * Creates a new DAO with the specified <tt>SessionFactory</tt>.
	 * 
	 * @param sessionFactory the <tt>SessionFactory</tt>.
	 */
	public HibernatePortfolioDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * Method to close the <tt>SessionFactory</tt>. Should only
	 * be invoked when this class is destroyed.
	 */
	public void closeFactory(){
		this.sessionFactory.close();
	}

	@Override
	public void insertPortfolio(PortfolioModel portfolioModel) {
		sessionFactory.getCurrentSession().save(portfolioModel);
	}

	@Override
	@SuppressWarnings("unchecked") //hibernate list() method doesn't support generics
	public List<PortfolioModel> getPortfolioList() {
		return (List<PortfolioModel>) sessionFactory.getCurrentSession()
					.createCriteria(SQLPortfolioModel.class)
					.list();
	}

	@Override
	public PortfolioModel getPortfolio(int userid) {
		return (SQLPortfolioModel) sessionFactory.getCurrentSession()
					.createCriteria(SQLPortfolioModel.class)
					.add(Restrictions.naturalId().set("userID", userid))
					.setFetchMode("stockList", FetchMode.JOIN)
					.uniqueResult();
	}

	@Override
	public void updatePortfolio(PortfolioModel portfolioModel) {
		sessionFactory.getCurrentSession().update(portfolioModel);
	}

	@Override
	public void deletePortfolio(PortfolioModel portfolioModel) {
		sessionFactory.getCurrentSession().delete(portfolioModel);
	}

}
