package io.craigmiller160.stockmarket.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import io.craigmiller160.stockmarket.model.PortfolioModel;
import io.craigmiller160.stockmarket.model.SQLPortfolioModel;
import net.jcip.annotations.ThreadSafe;

/**
 * An implementation of the <tt>PortfolioPersistService</tt> interface. This
 * class adds transactional management and additional operations to
 * the basic CRUD operations performed by the DAO.
 * <p>
 * <b>THREAD SAFETY:</b> This class is thread-safe. The DAO field is
 * final and cannot be changed, and it has no other state fields
 * that need to be guarded.
 * 
 * @author craig
 * @version 2.4
 */
@ThreadSafe
public class PortfolioPersistServiceImpl implements PortfolioPersistService {

	/**
	 * The DAO for this class.
	 */
	private final PortfolioDao portfolioDao;
	
	/**
	 * The format for amounts of money to display.
	 */
	private final NumberFormat moneyFormat = new DecimalFormat("$###,###,###,##0.00");
	
	/**
	 * The format for the timestamp to display.
	 */
	private final DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	/**
	 * Creates a new persist service, with the specified DAO.
	 * 
	 * @param portfolioDao the DAO for this class.
	 */
	public PortfolioPersistServiceImpl(PortfolioDao portfolioDao) {
		this.portfolioDao = portfolioDao;
	}

	@Transactional
	@Override
	public PortfolioModel createNewPortfolio(String portfolioName, BigDecimal startingCashBalance) {
		SQLPortfolioModel portfolioModel = new SQLPortfolioModel();
		portfolioModel.setPortfolioName(portfolioName);
		portfolioModel.setInitialValue(startingCashBalance);
		portfolioModel.setCashBalance(startingCashBalance);
		portfolioModel.setNetWorth(startingCashBalance);
		
		portfolioDao.insertPortfolio(portfolioModel);
		
		return portfolioModel;
	}

	@Transactional
	@Override
	public List<String> getSavedPortfolioNames() {
		List<PortfolioModel> portfolioList = portfolioDao.getPortfolioList();
		
		List<String> portfolioNames = new ArrayList<>();
		for(PortfolioModel portfolioModel : portfolioList){
			int id = ((SQLPortfolioModel) portfolioModel).getUserID();
			String name = portfolioModel.getPortfolioName();
			BigDecimal netWorth = portfolioModel.getNetWorth();
			Calendar timestamp = ((SQLPortfolioModel) portfolioModel).getTimestamp();
			
			String fileName = String.format("%1$d-%2$s-%3$s-"
					+"%4$s", id, name, moneyFormat.format(netWorth), 
					timestampFormat.format(timestamp.getTime()));
			portfolioNames.add(fileName);
		}
		
		return portfolioNames;
	}

	@Transactional
	@Override
	public PortfolioModel getPortfolio(String fileName) {
		String regex = "\\d+-.+-.+-\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
		boolean matches = Pattern.matches(regex, fileName);
		if(!matches){
			throw new IllegalArgumentException(fileName 
					+ " is not a value returned in the list by HibernatePortfolioDAO.loadPortfolioList()");
		}
		else{
			String[] split = fileName.split("-");
			return portfolioDao.getPortfolio(Integer.parseInt(split[0]));
		}
	}

	@Transactional
	@Override
	public PortfolioModel getPortfolio(int userid) {
		return portfolioDao.getPortfolio(userid);
	}

	@Transactional
	@Override
	public void savePortfolio(PortfolioModel portfolioModel) {
		portfolioDao.updatePortfolio(portfolioModel);
	}

}
