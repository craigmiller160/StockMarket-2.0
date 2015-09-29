package io.craigmiller160.stockmarket.aspect;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.craigmiller160.stockmarket.model.PortfolioModel;
import io.craigmiller160.stockmarket.stock.OwnedStock;

/**
 * This aspect handles all logging calls for the model classes
 * of this program.
 * 
 * @author craig
 * @version 2.3
 */
public aspect ModelLogging {

	/**
	 * The logger for this aspect.
	 */
	private static final Logger LOGGER = 
			LoggerFactory.getLogger("io.craigmiller160.stockmarket.model");
	
	/**
	 * A pointcut that points to all public setter methods
	 * for all classes in the model package, EXCEPT for 
	 * the <tt>setStockInList(OwnedStock)</tt> method of the 
	 * <tt>PortfolioModel</tt> class.
	 */
	public pointcut modelPropertySetter() :
		execution(public void set*(..)) && modelClasses() && !stockInListSetter();
	
	/**
	 * A pointcut that points to all classes in the model
	 * package.
	 */
	public pointcut modelClasses() :
		within(io.craigmiller160.stockmarket.model.*);
	
	/**
	 * A pointcut that points to the <tt>setStockInList(OwnedStock)</tt>
	 * method of the <tt>PortfolioModel</tt> class.
	 */
	public pointcut stockInListSetter() :
		execution(public void io.craigmiller160.stockmarket.model.PortfolioModel.setStockInList(*));
	
	/**
	 * Advice that runs before any method specified by the
	 * <tt>modelPropertySetter()</tt> pointcut. It logs the
	 * new value that a model property is being set to.
	 */
	before() : modelPropertySetter() || stockInListSetter(){
		Object[] params = thisJoinPoint.getArgs();
		String methodName = thisJoinPoint.getSignature().getName();
		String paramText = Arrays.deepToString(params);
		LOGGER.debug("\"{} : {}\"", methodName, paramText);
	}
	
	/**
	 * Advice that runs after the <tt>setStockInList(OwnedStock)</tt>
	 * method. It logs the change being made to the stock list.
	 */
	after() : stockInListSetter(){
		String methodName = thisJoinPoint.getSignature().getName();
		PortfolioModel model = (PortfolioModel) thisJoinPoint.getTarget();
		OwnedStock paramStock = (OwnedStock) thisJoinPoint.getArgs()[0];
		OwnedStock listStock = model.getStockInList(paramStock);
		if(paramStock.getQuantityOfShares() == 0 && 
				listStock == null){
			logStockUpdateSuccessful(methodName, paramStock);
		}
		else if(listStock != null && 
				paramStock.getQuantityOfShares() == listStock.getQuantityOfShares()){
			logStockUpdateSuccessful(methodName, paramStock);
		}
		else{
			logStockUpdateFailure(methodName, paramStock);
		}
	}
	
	/**
	 * Log a successful update of a stock in the portfolio.
	 * 
	 * @param methodName the name of the method executed.
	 * @param paramStock the stock that was successfully updated.
	 */
	private void logStockUpdateSuccessful(String methodName, OwnedStock paramStock){
		LOGGER.info("\"{} Successful: {}, {} shares\"", 
				methodName, paramStock.getSymbol(), paramStock.getQuantityOfShares());
	}
	
	/**
	 * Log a failed update of a stock in the portfolio.
	 * 
	 * @param methodName the name of the method executed.
	 * @param paramStock the stock that failed to be updated.
	 */
	private void logStockUpdateFailure(String methodName, OwnedStock paramStock){
		LOGGER.error("\"{} Failure: {}, {} shares\"", 
				methodName, paramStock.getSymbol(), paramStock.getQuantityOfShares());
	}
	
}
